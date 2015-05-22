package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserDescActivity extends Activity {

	private TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_desc);
		
		tv = (TextView) findViewById(R.id.tv_desc_detail);
		if (getIntent() != null) {
			String desc = getIntent().getStringExtra("desc");
			tv.setText(desc == null || desc.equals("") ? "暂无介绍": desc);
		}
	}
	
	public void stepBack(View v) {
		finish();
	}
	
}
