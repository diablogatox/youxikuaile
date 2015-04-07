package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class RecommendDetailActivity extends Activity {

	private String title, content;
	private TextView titleTv, contentTv;
	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend_detail);
		init();
		views();
		set();
	}

	private void set() {
		titleTv.setText(title);
		content = Utils.changeImgWidth(content); 
//		mWebView.loadUrl("http://www.oschina.net");
		mWebView.setBackgroundColor(getResources().getColor(R.color.global_bg_color));
		mWebView.loadDataWithBaseURL("", content, "text/html", "UTF-8", null);
	}

	private void views() {
		titleTv = (TextView) findViewById(R.id.detail_title_tv);
		contentTv = (TextView) findViewById(R.id.detail_content_tv);
		mWebView = (WebView) findViewById(R.id.detail_content_wv);
	}

	private void init() {
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		
	}
	
	public void goBack(View v) {
		finish();
	}

}
