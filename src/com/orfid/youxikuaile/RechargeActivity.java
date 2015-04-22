package com.orfid.youxikuaile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.orfid.youxikuaile.widget.ViewPagerIndicatorView;

public class RechargeActivity extends Activity {

	private ViewPagerIndicatorView viewPagerIndicatorView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		
		this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view);
//		TreeMap<String, View> map = new TreeMap<String, View>(new MyCopr());
		Map<String, View> map = new HashMap<String, View>();
		map.put("在线充值", LayoutInflater.from(this).inflate(R.layout.tab_online_recharge, null));
		map.put("充值记录", LayoutInflater.from(this).inflate(R.layout.tab_recharge_records, null));
		map.put("兑换积分", LayoutInflater.from(this).inflate(R.layout.tab_exchange_points, null));
		this.viewPagerIndicatorView.setupLayout(map);
	}
	
	class MyCopr implements Comparator<String>{
		
		@Override
		public int compare(String str1, String str2)
		{
			return str1.compareTo(str2);
		}
	}
	
	public void goBack(View view) {
		finish();
	}

}
