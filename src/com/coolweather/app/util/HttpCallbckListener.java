package com.coolweather.app.util;

public interface HttpCallbckListener {
	void onFinish(String request);
	void onError(Exception e);
}
