package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.widget.MyGridView;
import com.orfid.youxikuaile.widget.PagerSlidingTabStrip;

public class NewsFeedDetailActivity extends FragmentActivity implements OnClickListener {

	private ViewPager pager;
    private PagerSlidingTabStrip pagertab;
    private MyPageAdapter pageAdapter;
    private ImageButton backBtn;
    private ImageView photoIv;
    private TextView nameTv, timeTv, contentTv, praiseNumTv;
    private View forwardArea;
    private ImageView forwardIcon;
    private TextView forwardContent;
    private MyGridView imagesGv;
    View rlGvWrapper;
    
    private int forwardNum, commentNum;
    private long feedId;
    private int wh;
    
    List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
    private GridViewAdapter gvAdapter;
    
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
		String imgs = intent.getStringExtra("imgs");
		int praiseCount = intent.getIntExtra("praiseNum", 0);
		boolean hasForward = intent.getBooleanExtra("hasForward", false);
		boolean forwardHasImg = intent.getBooleanExtra("forwardHasImg", false);
		String ct = intent.getStringExtra("forwardContent");
		String icon = intent.getStringExtra("forwardIcon");
//		Log.d("received imgs=======>", imgs);
		if (imgs != null) {
			rlGvWrapper.setVisibility(View.VISIBLE);
			JSONArray jImgItemsArr = null;
			try {
				jImgItemsArr = new JSONArray(imgs);
				for (int i=0; i<jImgItemsArr.length(); i++) {
	                JSONObject jFile = jImgItemsArr.getJSONObject(i);
	                Log.d("image url==========>", jFile.getString("url"));
	                imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
	            }
				initImgAttachment(imagesGv, imgItems);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		if (hasForward == true) {
			if (forwardHasImg == true) {
				ImageLoader.getInstance().displayImage(icon, forwardIcon);
			}
			SpannableStringBuilder sb = Utils.handlerFaceInContent(this, forwardContent,
                    ct);
			forwardContent.setText(sb);
			forwardArea.setVisibility(View.VISIBLE);
		}
		
		if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, photoIv);
		nameTv.setText(name);
		timeTv.setText(Utils.covertTimestampToDate(Long.parseLong(time) * 1000));
		SpannableStringBuilder sb = Utils.handlerFaceInContent(this, contentTv,
                content);
		contentTv.setText(sb);
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
        forwardArea = findViewById(R.id.forward_area);
        forwardIcon = (ImageView) findViewById(R.id.forward_icon);
        forwardContent = (TextView) findViewById(R.id.forward_content);
        imagesGv = (MyGridView) findViewById(R.id.gv_images);
        rlGvWrapper = findViewById(R.id.rl_gv_wrapper);
        
        wh = (Utils.getScreenWidth(this)-Utils.Dp2Px(this, 99))/3;
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
	
	private void initImgAttachment(MyGridView myGv, List<FeedAttachmentImgItem> items) {
        int w=0;
        switch (items.size()) {
            case 1:
                w=wh;
                myGv.setNumColumns(1);
                break;
            case 2:
            case 4:
                w=2*wh+Utils.Dp2Px(this, 2);
                myGv.setNumColumns(2);
                break;
            case 3:
            case 5:
            case 6:
                w=wh*3+Utils.Dp2Px(this, 2)*2;
                myGv.setNumColumns(3);
                break;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myGv.setLayoutParams(lp);
        gvAdapter = new GridViewAdapter(this, items);
        myGv.setAdapter(gvAdapter);
    }
	
	public class GridViewAdapter extends BaseAdapter {

        Context context;
        List<FeedAttachmentImgItem> list;
        private int wh;

        public GridViewAdapter(Context context, List<FeedAttachmentImgItem> data) {
            this.context=context;
            this.wh=(Utils.getScreenWidth(context)-Utils.Dp2Px(context, 99))/3;
            this.list=data;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public FeedAttachmentImgItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(list.get(position).getId());
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Holder holder;
            if (view==null) {
                view=LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
                holder=new Holder();
                holder.imageView=(ImageView) view.findViewById(R.id.imageView);
                view.setTag(holder);
            }else {
                holder= (Holder) view.getTag();
            }
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
            view.setLayoutParams(param);
            Log.d("before render image url======>", list.get(position).getUrl());
            ImageLoader.getInstance().displayImage(list.get(position).getUrl(), holder.imageView);

            return view;
        }

        class Holder{
            ImageView imageView;
        }


    }

}
