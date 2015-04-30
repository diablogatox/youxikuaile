package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.orfid.youxikuaile.pojo.User;
/**
 * 此activity,是积分工厂帮助信息

 *  * @author clh
 * 
 */
public class ScoreHelpActivity extends Activity {
	private User userData;
	private WebView web;
	private Button backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_help);

		web = (WebView) findViewById(R.id.hayou_score_help_dialog_web_view);
		backBtn = (Button) findViewById(R.id.score_help_back_btn);
		web.loadUrl("file:///android_asset/score_help.html");
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ScoreHelpActivity.this.finish();
			}
		});
	}

}
