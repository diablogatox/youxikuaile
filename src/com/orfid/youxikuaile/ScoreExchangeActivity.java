package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ScoreExchangeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_exchange);
	}
	
	public void stepBack(View view) {
		finish();
	}

}
