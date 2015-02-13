package com.orfid.youxikuaile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	public static MainActivity instance = null;
	private ViewPager mTabPager;
	private ImageView mTab1, mTab2, mTab3, mTab4, mTab5;
	private int currIndex = 0;
	private EditText searchInput;
	private ImageButton searchBtn, addBtn, backBtn;
	private View view, titleBar, edittextBottomLine, searchOverlay;
	private ArrayList<View> views = new ArrayList<View>();
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		instance = this;
		
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mTab1 = (ImageView) findViewById(R.id.img_hot);
        mTab2 = (ImageView) findViewById(R.id.img_found);
        mTab3 = (ImageView) findViewById(R.id.img_message);
        mTab4 = (ImageView) findViewById(R.id.img_more);
        mTab5 = (ImageView) findViewById(R.id.img_mine);
        
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        mTab5.setOnClickListener(new MyOnClickListener(4));
        
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.frame_hot, null);
        View view2 = mLi.inflate(R.layout.frame_found, null);
        View view3 = mLi.inflate(R.layout.frame_message, null);
        View view4 = mLi.inflate(R.layout.frame_more, null);
        View view5 = mLi.inflate(R.layout.frame_mine, null);
        
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);

        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mPagerAdapter);
		
		init();
		setListener();
        
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1));
				if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				} else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 4:
				mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				break;
			}
			currIndex = arg0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index, false);
		}
	};
	
	public void toggleDropdownMenu(View view) {

	}
	
	private void init() {
		
		imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		view = views.get(currIndex);
		
	}
	
	private void setListener() {
		
		if (currIndex == 0) {
			
			titleBar = view.findViewById(R.id.title);
			edittextBottomLine = view.findViewById(R.id.et_bottom_line);
			searchOverlay = view.findViewById(R.id.search_overlay);
			searchBtn = (ImageButton) view.findViewById(R.id.ib_search);
			addBtn = (ImageButton) view.findViewById(R.id.ib_add);
			backBtn = (ImageButton) view.findViewById(R.id.btn_back);
			searchInput = (EditText) view.findViewById(R.id.et_search_input);
			
			searchBtn.setOnClickListener(this);
			backBtn.setOnClickListener(this);
			searchOverlay.setOnClickListener(this);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_search:
			
			v.setVisibility(View.GONE);
			addBtn.setVisibility(View.GONE);
			backBtn.setVisibility(View.VISIBLE);
			searchInput.setVisibility(View.VISIBLE);
			edittextBottomLine.setVisibility(View.VISIBLE);
			searchOverlay.setVisibility(View.VISIBLE);
			titleBar.setBackgroundResource(R.color.header_bar_bg_color);
			
			searchInput.requestFocus();
			imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
			
			break;
		case R.id.btn_back:
			
			v.setVisibility(View.GONE);
			searchInput.setVisibility(View.GONE);
			edittextBottomLine.setVisibility(View.GONE);
			searchOverlay.setVisibility(View.GONE);
			addBtn.setVisibility(View.VISIBLE);
			searchBtn.setVisibility(View.VISIBLE);
			titleBar.setBackgroundColor(Color.TRANSPARENT);
			
			searchInput.setText("");
			imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			
			break;
		case R.id.search_overlay:
			
			v.setVisibility(View.GONE);
			searchInput.setVisibility(View.GONE);
			edittextBottomLine.setVisibility(View.GONE);
			backBtn.setVisibility(View.GONE);
			addBtn.setVisibility(View.VISIBLE);
			searchBtn.setVisibility(View.VISIBLE);
			titleBar.setBackgroundColor(Color.TRANSPARENT);
			
			searchInput.setText("");
			imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			
			break;
		}
		
	}

}
