package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GiftActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift);
	}
	
	public void goBack(View view) {
		finish();
	}

}
