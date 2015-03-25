package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.orfid.youxikuaile.pojo.ActionItem;
import com.orfid.youxikuaile.widget.TitlePopup;
import com.orfid.youxikuaile.widget.TitlePopup.OnItemOnClickListener;

public class SittersActivity extends Activity implements OnClickListener {

	private TitlePopup titlePopup;
	private ImageButton backBtn, addSitterBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sitters);
		initView();
		setListener();
		obtainData();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		addSitterBtn.setOnClickListener(this);
		
		titlePopup.setItemOnClickListener(new OnItemOnClickListener() {

			@Override
			public void onItemClick(ActionItem item, int position) {
				switch (position) {
				case 0:
					startActivity(new Intent(SittersActivity.this, OnlineSitterPublishActivity.class));
					break;
				case 1:
//					startActivity(new Intent(SittersActivity.this, AddNewFriendActivity.class));
					break;
				}
			}
			
		});
	}
	
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		addSitterBtn = (ImageButton) findViewById(R.id.add_sitter_btn);
		
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.addAction(new ActionItem("线上陪玩发布"));
		titlePopup.addAction(new ActionItem("线下陪玩发布"));
	}

	private void obtainData() {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.add_sitter_btn:
			titlePopup.show(v);
			break;
		}
	}

}
