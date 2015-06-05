package com.orfid.youxikuaile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


public class GuideActivity extends Activity implements OnClickListener,OnPageChangeListener {

	private ViewPager viewPager;
	

	private ViewPagerAdapter vpAdapter;
	
	private ArrayList<View> views;

    private static final int[] pics = {R.drawable.guide1,R.drawable.guide2,R.drawable.guide3};
    
    private ImageView[] points;
    
    private int currentIndex;


	private Button qqLogin;


	private Button accountLogin;


	private ImageView signupIv;
	
	private Tencent mTencent;
	private String APP_ID = "1101639686";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		
		initView();
		
		initData();	
	}


	private void initView(){

		views = new ArrayList<View>();
		
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
		vpAdapter = new ViewPagerAdapter(views);
		
		qqLogin = (Button) findViewById(R.id.qq_login);
		accountLogin = (Button) findViewById(R.id.account_login);
		signupIv = (ImageView) findViewById(R.id.signup_iv);
		
		qqLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(GuideActivity.this, SigninActivity.class);
//	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//	            startActivity(intent);
//	            finish();
				login();
			}
			
		});
		
		accountLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GuideActivity.this, SigninActivity.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	            startActivity(intent);
	            finish();
			}
			
		});
		
		signupIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GuideActivity.this, SignupActivity.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	            startActivity(intent);
	            finish();
			}
			
		});
		
	}
	

	public void login()
	{
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		if (!mTencent.isSessionValid())
		{
			mTencent.login(this, "all", new BaseUiListener());
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mTencent.onActivityResult(requestCode, resultCode, data);
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
//			doComplete(response);
			Log.d("lalala", "-------------"+response.toString());
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			
		}
		
		}
	
	private void initData(){

		mTencent = Tencent.createInstance(APP_ID,
				GuideActivity.this);
		
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                														  LinearLayout.LayoutParams.MATCH_PARENT);
       
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        } 
        

        viewPager.setAdapter(vpAdapter);

        viewPager.setOnPageChangeListener(this);
        
        initPoint();
	}
	

	private void initPoint(){
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);       
		
        points = new ImageView[pics.length];


        for (int i = 0; i < pics.length; i++) {

        	points[i] = (ImageView) linearLayout.getChildAt(i);

        	points[i].setEnabled(true);

        	points[i].setOnClickListener(this);

        	points[i].setTag(i);
        }
        

        currentIndex = 0;

        points[currentIndex].setEnabled(false);
	}
	

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {

        setCurDot(position);
	}


	@Override
	public void onClick(View v) {
		 int position = (Integer)v.getTag();
         setCurView(position);
         setCurDot(position);		
	}


	private void setCurView(int position){
         if (position < 0 || position >= pics.length) {
             return;
         }
         viewPager.setCurrentItem(position);
     }


    private void setCurDot(int positon){
         if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
             return;
         }
         points[positon].setEnabled(false);
         points[currentIndex].setEnabled(true);

         currentIndex = positon;
     }
	
}
