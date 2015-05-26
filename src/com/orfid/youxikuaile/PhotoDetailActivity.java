package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoDetailActivity extends Activity {

	ImageView contentImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_detail);
		contentImg = (ImageView) findViewById(R.id.content_img);
		ImageLoader.getInstance().displayImage(getIntent().getStringExtra("url"), contentImg);
	}
	
	public void stepBack(View view) {
		finish();
	}

}
