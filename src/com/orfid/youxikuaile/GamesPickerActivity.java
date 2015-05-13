package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
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
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.widget.wheelview.ArrayWheelAdapter;
import com.orfid.youxikuaile.widget.wheelview.OnWheelScrollListener;
import com.orfid.youxikuaile.widget.wheelview.WheelView;

/**
 * Created by Administrator on 2015/3/2.
 */
public class GamesPickerActivity extends Activity implements View.OnClickListener {

    WheelView games;
    TextView cancelTv, okTv;
    private List<String> ids = new ArrayList<String>();
    private List<String> names = new ArrayList<String>();
    private List<GameItem> gameItems = new ArrayList<GameItem>();
    private int currentIndex = 0;
    JSONArray jArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_picker);

        cancelTv = (TextView) findViewById(R.id.tv_cancel);
        okTv = (TextView) findViewById(R.id.tv_ok);
        cancelTv.setOnClickListener(this);
        okTv.setOnClickListener(this);
        int textSize = 0;
        textSize = adjustFontSize(getWindow().getWindowManager());
        
        games = (WheelView) findViewById(R.id.games);
        games.setAdapter(new ArrayWheelAdapter(new String[] {"加载中..."}, 300));
        games.setCyclic(false);// 可循环滚动
        
        games.TEXT_SIZE = textSize;
        games.addScrollingListener(onWheelScrollListener);
        
        try {
			doFetchMyGamesListAction();
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
        	currentIndex = games.getCurrentItem();
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
            	String gameId = ids.get(currentIndex);
            	String gameName = names.get(currentIndex);
            	Log.d("gameId======>", gameId);
            	Intent intent = new Intent();
            	intent.putExtra("gameName", gameName);
            	intent.putExtra("gameId", gameId);
				setResult(RESULT_OK, intent);
				finish();
				overridePendingTransition(0, 0);
                break;
        }
    }
    
    private void doFetchMyGamesListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("game/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (!response.isNull("data")) {
                    		jArr = response.getJSONArray("data");
                    		for (int i=0; i<jArr.length(); i++) {
                    			JSONObject jObj = jArr.getJSONObject(i);
                    			ids.add(jObj.getString("id"));
                    			names.add(jObj.getString("name"));
                    		}
                    	}
//                    	GameItemsParser parser = new GameItemsParser();
//                        gameItems = parser.parse(response);
//                        for (int i=0; i<gameItems.size(); i++) {
//                        	ids.add(gameItems.get(i).getId());
//                        	names.add(gameItems.get(i).getName());
//                        }
                        
                        if (jArr.length() <= 0) {
                        	games.setAdapter(new ArrayWheelAdapter(new String[] {"没有游戏"}, 300));
                        } else {
                        	games.setAdapter(new ArrayWheelAdapter(names.toArray(new String[names.size()]), 300));
                        }
                    } else if (status == 0) {
                        Toast.makeText(GamesPickerActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
