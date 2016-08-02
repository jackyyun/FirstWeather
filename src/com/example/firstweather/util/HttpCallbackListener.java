package com.example.firstweather.util;

public interface HttpCallbackListener {
	public void onFinish(String response);
	
	public void onError(Exception e);
}
