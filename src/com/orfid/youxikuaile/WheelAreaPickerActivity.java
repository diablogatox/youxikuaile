package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.widget.wheelview.ArrayWheelAdapter;
import com.orfid.youxikuaile.widget.wheelview.OnWheelScrollListener;
import com.orfid.youxikuaile.widget.wheelview.WheelView;

/**
 * Created by Administrator on 2015/3/2.
 */
public class WheelAreaPickerActivity extends Activity implements View.OnClickListener {

    WheelView parentAreas, subAreas;
    TextView cancelTv, okTv;
    private List<String> ids = new ArrayList<String>();
    private List<String> names = new ArrayList<String>();
    private List<String> ids2 = new ArrayList<String>();
    private List<String> names2 = new ArrayList<String>();
    private int currentIndex = 0, currentIndex2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_picker);

        cancelTv = (TextView) findViewById(R.id.tv_cancel);
        okTv = (TextView) findViewById(R.id.tv_ok);
        cancelTv.setOnClickListener(this);
        okTv.setOnClickListener(this);
        int textSize = 0;
        textSize = adjustFontSize(getWindow().getWindowManager());
        
        parentAreas = (WheelView) findViewById(R.id.parent_areas);
        parentAreas.setAdapter(new ArrayWheelAdapter(new String[] {"加载中..."}, 300));
        parentAreas.setCyclic(false);// 可循环滚动
        
        parentAreas.TEXT_SIZE = textSize;
        parentAreas.addScrollingListener(onWheelScrollListener);
        
        subAreas = (WheelView) findViewById(R.id.sub_areas);
        subAreas.setAdapter(new ArrayWheelAdapter(new String[] {"加载中..."}, 300));
        subAreas.setCyclic(false);// 可循环滚动
        
        subAreas.TEXT_SIZE = textSize;
        subAreas.addScrollingListener(onWheelScrollListener2);
        
        try {
			doFetchAreaListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
    }

    OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
        	currentIndex = parentAreas.getCurrentItem();
        	ids2.clear();
        	names2.clear();
        	subAreas.setAdapter(new ArrayWheelAdapter(new String[] {"加载中..."}, 300));
        	try {
				doFetchSubAreaListAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
    };
    
    OnWheelScrollListener onWheelScrollListener2 = new OnWheelScrollListener() {

        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
        	currentIndex2 = subAreas.getCurrentItem();
        }
    };


    public static int adjustFontSize(WindowManager windowmanager) {

        int screenWidth = windowmanager.getDefaultDisplay().getWidth();
        int screenHeight = windowmanager.getDefaultDisplay().getHeight();
        if (screenWidth <= 240) { // 240X320 屏幕
            return 20;
        } else if (screenWidth <= 320) { // 320X480 屏幕
            return 24;
        } else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
            return 34;
        } else if (screenWidth <= 540) { // 540X960 屏幕
            return 36;
        } else if (screenWidth <= 800) { // 800X1280 屏幕
            return 40;
        } else { // 大于 800X1280
            return 40;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.tv_ok:
            	String province = ids.get(currentIndex);
            	String city = ids2.get(currentIndex2);
				setResult(RESULT_OK, new Intent().putExtra("provinceId", province)
						.putExtra("provinceName", names.get(currentIndex))
						.putExtra("cityId", city)
						.putExtra("cityName", names2.get(currentIndex2)));
				finish();
				overridePendingTransition(0, 0);
                break;
        }
    }
    
    private void doFetchAreaListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("region", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	String data = response.getString("data").replaceAll("[${}]", "");
                    	String[] areas = data.split(",");
                    	for (int i=0; i<areas.length; i++) {
                    		String[] area = areas[i].split(":");
                    		ids.add(area[0].replaceAll("\"", ""));
                    		names.add(area[1].replaceAll("\"", ""));
                    	}
                    	
                    	Collections.reverse(ids);
                    	Collections.reverse(names);
                    	
                    	parentAreas.setAdapter(new ArrayWheelAdapter(names.toArray(new String[names.size()]), 300));
                    	
                    	Log.d("pid", ids.get(currentIndex));
                    	
                    	doFetchSubAreaListAction();

                    } else if (status == 0) {
                        Toast.makeText(WheelAreaPickerActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    
    private void doFetchSubAreaListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", ids.get(currentIndex));
        HttpRestClient.post("region", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	String data = response.getString("data").replaceAll("[${}]", "");
                    	String[] areas = data.split(",");
                    	for (int i=0; i<areas.length; i++) {
                    		String[] area = areas[i].split(":");
                    		ids2.add(area[0].replaceAll("\"", ""));
                    		names2.add(area[1].replaceAll("\"", ""));
                    	}
                    	
                    	Collections.reverse(ids2);
                    	Collections.reverse(names2);
                    	
                    	subAreas.setAdapter(new ArrayWheelAdapter(names2.toArray(new String[names2.size()]), 300));
                    	subAreas.setCurrentItem(0);
                    	
                    } else if (status == 0) {
                        Toast.makeText(WheelAreaPickerActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    
}
