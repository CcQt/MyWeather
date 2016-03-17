package com.coolweather.app.adapter;

import java.util.List;
import java.util.zip.Inflater;
import com.coolweather.app.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private List<String> dataList;
	public MyAdapter(Context context, List<String> dataList) {
		// TODO �Զ����ɵĹ��캯�����
		this.context = context;
		this.dataList = dataList;
	}
	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO �Զ����ɵķ������
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO �Զ����ɵķ������
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			view = LayoutInflater.from(context).inflate(R.layout.mylist, null);
			viewHolder = new ViewHolder();
			viewHolder.myitem = (TextView)view.findViewById(R.id.myitem);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.myitem.setText(dataList.get(position));
//		view = LayoutInflater.from(context).inflate(R.layout.mylist, null);
//		TextView myitem = (TextView)view.findViewById(R.id.myitem);
//		myitem.setText(dataList.get(position));
		return view;
	}
	class ViewHolder{
		TextView myitem;
	}

}
