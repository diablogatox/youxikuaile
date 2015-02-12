package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SigninActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signin);
	}

	public void jumpSignup(View view) {
		startActivity(new Intent(this, SignupActivity.class));
	}
	
	public void jumpRetrievePassword(View view) {
		startActivity(new Intent(this, RetrievePasswordActivity.class));
	}
}
