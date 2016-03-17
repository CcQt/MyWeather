package com.coolweather.app.activity;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbckListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity implements OnClickListener {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	public ListView listView;
	public TextView titleText;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	private ProgressDialog progressDialog;
	
	private boolean isFromWeatherActivity;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		InitView();		
	}

	private void InitView() {
		// TODO 自动生成的方法存根
		listView = (ListView)findViewById(R.id.list_view);
		titleText = (TextView)findViewById(R.id.title_text);
		
		adapter = new ArrayAdapter<String>(getApplication(), android.R.layout.simple_expandable_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(getApplication());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO 自动生成的方法存根
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}			
		});
		queryProvinces();
	}
	
	private void queryProvinces(){
		provinceList = coolWeatherDB.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	

	private void queryCities() {
		// TODO 自动生成的方法存根
		cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	private void queryCounties() {
		// TODO 自动生成的方法存根
		countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	private void queryFromServer(String code, final String type) {
		// TODO 自动生成的方法存根
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://weather.com.cn/data/list3/city"+code+".xml";
		} else {
			address = "http://weather.com.cn/data/list3/city.xml";
//			address = "http://weather.com.cn/data/cityinfo.html";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbckListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO 自动生成的方法存根
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(coolWeatherDB, response);
				} else if("city".equals(type)){
					result = Utility.handlecitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handlecountiesResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO 自动生成的方法存根
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO 自动生成的方法存根
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO 自动生成的方法存根
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO 自动生成的方法存根
		if(progressDialog==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog() {
		// TODO 自动生成的方法存根
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 捕捉back
	 */
	public void onBackPressed(){
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		
	}
}
