package com.orfid.youxikuaile.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.orfid.youxikuaile.R;

public class SlideShowView extends FrameLayout {

    private final static boolean isAutoPlay = true; 
    

    private int[] imageUrls = new int[]{
			R.drawable.ad1,
			R.drawable.ad2
	};

    private List<ImageView> imageViewsList;

    private List<View> dotViewsList;
    
    private ViewPager viewPager;

    private int currentItem  = 0;

    private ScheduledExecutorService scheduledExecutorService;
    
    private Context context;
    
    //Handler
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
        
    };
    
    public SlideShowView(Context context) {
        this(context,null);

    }
    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }
    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
		
        initData();
        if(isAutoPlay){
            startPlay();
        }
        
    }

    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
    }

    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }

    private void initData(){
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

        initUI(context);
//        new GetListTask().execute("");
    }

    private void initUI(Context context){
    	if(imageUrls == null || imageUrls.length == 0)
    		return;
    	
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
        
        LinearLayout dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        
        for (int i = 0; i < imageUrls.length; i++) {
        	ImageView view =  new ImageView(context);
        	view.setTag(imageUrls[i]);
        	view.setScaleType(ScaleType.FIT_XY);
        	imageViewsList.add(view);
        	
        	ImageView dotView =  new ImageView(context);
        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        	params.leftMargin = 4;
			params.rightMargin = 4;
			dotLayout.addView(dotView, params);
        	dotViewsList.add(dotView);
		}
        
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }
    
    private class MyPagerAdapter  extends PagerAdapter{

        @Override
        public void destroyItem(View container, int position, Object object) {
            // TODO Auto-generated method stub
            //((ViewPag.er)container).removeView((View)object);
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
        	ImageView imageView = imageViewsList.get(position);

        	imageView.setBackgroundResource((Integer) imageView.getTag());
        	
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }
        
    }

    private class MyPageChangeListener implements OnPageChangeListener{

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
            case 1:
                isAutoPlay = false;
                break;
            case 2:
                isAutoPlay = true;
                break;
            case 0:
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                    viewPager.setCurrentItem(0);
                }

                else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                }
                break;
        }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub
            
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    ((View)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_focus);
                }else {
                    ((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_blur);
                }
            }
        }
        
    }
    
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }
        
    }
}