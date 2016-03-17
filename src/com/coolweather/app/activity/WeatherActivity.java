package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbckListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayot;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		InitView();
		InitDate();
	}


	private void InitView() {
		// TODO �Զ����ɵķ������
		weatherInfoLayot = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText = (TextView)findViewById(R.id.city_name);
		publishText = (TextView)findViewById(R.id.publish_text);
		weatherDespText = (TextView)findViewById(R.id.weather_desp);
		temp1Text = (TextView)findViewById(R.id.temp1);
		temp2Text = (TextView)findViewById(R.id.temp2);
		currentDateText = (TextView)findViewById(R.id.current_date);
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	private void InitDate() {
		// TODO �Զ����ɵķ������
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("ͬ���С�����");
			weatherInfoLayot.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			showWeather();
		}
	}
	/*
	 * ��ѯ�ؼ����Ŷ�Ӧ����������
	 */

	private void queryWeatherCode(String countyCode) {
		// TODO �Զ����ɵķ������
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromSever(address,"countyCode");
	}
	/*
	 * ��ѯ�������Ŷ�Ӧ������
	 */
	private void quertWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromSever(address,"weatherCode");
	}
	
	/*
	 * ���ݴ���ĵ�ַ���������������ѯ�������Ż�������Ϣ
	 */
	private void queryFromSever(String address, final String type) {
		// TODO �Զ����ɵķ������
		HttpUtil.sendHttpRequest(address, new HttpCallbckListener() {
			
			@Override
			public void onFinish(String request) {
				// TODO �Զ����ɵķ������
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(request)){
						String[] array = request.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode = array[1];
							quertWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, request);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO �Զ����ɵķ������
							showWeather();
						}
					});
					
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO �Զ����ɵķ������
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO �Զ����ɵķ������
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/*
	 * ��sr��ȡ��������Ϣ
	 */
	private void showWeather() {
		// TODO �Զ����ɵķ������
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("punlish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayot.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}


	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		if(v.getId()==R.id.switch_city){
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
		}else if(v.getId()==R.id.refresh_weather){
			publishText.setText("ͬ���С�����");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherCode(weatherCode);
			}
		}
	}
}
