package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class RetrievePasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_retrieve_password);
	}

	public void stepBack(View view) {
		finish();
	}
}
