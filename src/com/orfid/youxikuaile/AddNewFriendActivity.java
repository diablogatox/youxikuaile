package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AddNewFriendActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_new_friends);
	}

	public void stepBack(View view) {
		finish();
	}
}
