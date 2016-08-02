package com.example.firstweather.activity;

import com.example.firstweather.R;
import com.example.firstweather.service.AutoUpdateService;
import com.example.firstweather.util.HttpCallbackListener;
import com.example.firstweather.util.HttpUtil;
import com.example.firstweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
             
public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private TextView currentTemp;
	private TextView currentWeekday;
	private Button switchCity;
	private Button refreshWeather;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		currentTemp = (TextView) findViewById(R.id.current_temp);
		currentWeekday = (TextView) findViewById(R.id.current_weekday);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			
			queryWeatherCode(countyCode);
			
			
		}else{
			showWeather();
		}
	}
	
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name",""));
		temp1Text.setText(prefs.getString("temp1",""));
		temp2Text.setText(prefs.getString("temp2",""));
		weatherDespText.setText(prefs.getString("weather_desp",""));
		publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
		currentDateText.setText(prefs.getString("current_date",""));
		currentTemp.setText(prefs.getString("curent_temp",""));
		currentWeekday.setText(prefs.getString("current_weekday",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
		
		System.out.println("yeren 11111");
		System.out.println(prefs.getString("current_weekday",""));
	}
	
	private void queryWeatherCode(String countyCode){
		
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
		
	}
	
	
	private void queryWeatherInfo (String weatherCode){
		//String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		
		String address = "http://api.k780.com:88/?app=weather.today&weaid="+weatherCode+"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json";
		
		queryFromServer(address, "weatherCode");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				System.out.println(response+"1");
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
					String[] array = response.split("\\|");
					if(array != null && array.length == 2){
						String weatherCode = array[1];
						queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("同步失败");
						
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(WeatherActivity.this,ChooseArea.class);
			intent.putExtra("from_weather_activity",true);
			startActivity(intent);
			finish();
			break;
			
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code","");
			System.out.println(weatherCode);
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
			
		default:
			break;
		}
	}
	

}
