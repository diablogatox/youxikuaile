package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.widget.PagerSlidingTabStrip;

public class NewsFeedDetailActivity extends FragmentActivity implements OnClickListener {

	private ViewPager pager;
    private PagerSlidingTabStrip pagertab;
    private MyPageAdapter pageAdapter;
    private ImageButton backBtn;
    private ImageView photoIv;
    private TextView nameTv, timeTv, contentTv, praiseNumTv;
    
    private int forwardNum, commentNum;
    private long feedId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed_detail);
		init();
		setListener();
		obtainData();
	}

	private void obtainData() {
		Intent intent = getIntent();
		feedId = intent.getLongExtra("feedId", 0);
		forwardNum = intent.getIntExtra("forwardNum", 0);
		commentNum = intent.getIntExtra("commentNum", 0);
		Log.d("forward num======>", forwardNum+"");
		Log.d("comment num======>", commentNum+"");
		String photo = intent.getStringExtra("photo");
		String name = intent.getStringExtra("name");
		String time = intent.getStringExtra("time");
		String content = intent.getStringExtra("content");
		int praiseCount = intent.getIntExtra("praiseNum", 0);
		
		if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, photoIv);
		nameTv.setText(name);
		timeTv.setText(Util.covertTimestampToDate(Long.parseLong(time) * 1000));
		contentTv.setText(content);
		praiseNumTv.setText(praiseCount+" 赞");
		
		List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);
        pagertab.setViewPager(pager);
        pagertab.setScrollOffset(0);
        pagertab.setTextColorResource(R.color.grey);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		
	}

	private void init() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		pager = (ViewPager) findViewById(R.id.activity_main_pager);
        pagertab = (PagerSlidingTabStrip) findViewById(R.id.activity_main_pagertabstrip);
        photoIv = (ImageView) findViewById(R.id.photo);
        nameTv = (TextView) findViewById(R.id.name);
        timeTv = (TextView) findViewById(R.id.time);
        contentTv = (TextView) findViewById(R.id.content);
        praiseNumTv = (TextView) findViewById(R.id.praise_num);
	}
	
	class MyPageAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;


        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
            	return forwardNum + " 转发";
            case 1:
            	return commentNum + " 评论";
            default:
            	return null;
            }
        }
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(MyFragment.newInstance("0 转发"));
        fList.add(MyFragment.newInstance("0 评论"));
        return fList;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		}
	}

}
