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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchGameServerAreaActivity extends Activity {

	private String gameId;
	private EditText searchEt;
	private ListView searchResultLv;
	private TextView hintTv;
	private Handler mHandler = new Handler();
	private List<String> gameAreas = new ArrayList<String>();
	private ArrayAdapter<String> listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_sittergame_serverarea);
		Intent intent = getIntent();
		gameId = intent.getStringExtra("gameId");
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		searchEt = (EditText) findViewById(R.id.search_et);
		searchResultLv = (ListView) findViewById(R.id.search_result_lv);
		hintTv = (TextView) findViewById(R.id.hint_tv);
		
		listAdapter = new ArrayAdapter<String>(this, R.layout.game_area_item, gameAreas);
		searchResultLv.setAdapter(listAdapter);
	}

	private void setListener() {
		searchEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.length() > 0) {
					Log.d("text is ====>", s.toString());
					listAdapter.clear();
					mHandler.removeCallbacksAndMessages(null);
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							try {
								doSearchGameAreaAction(s.toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
					}, 1000);
				} else {
					listAdapter.clear();
				}
			}
			
		});
	}

	private void obtainData() {
		
	}
	
	private void doSearchGameAreaAction(String keyword) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        if (user != null && user.size() > 0) {
        	params.put("token", user.get("token").toString());
        	params.put("id", gameId);
        	params.put("keyword", keyword);
        }
        HttpRestClient.post("game/area", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (response.getJSONArray("data").length() > 0) {
                    		if (hintTv.isShown()) hintTv.setVisibility(View.GONE);
                    		JSONArray jsonArray = response.getJSONArray("data");
                    		int len = jsonArray.length();
							for (int i=0;i<len;i++){ 
								gameAreas.add(jsonArray.get(i).toString());
							}
							listAdapter.notifyDataSetChanged();
							
                    	} else {
                    		hintTv.setText("搜索无此服");
                    		hintTv.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(SearchGameServerAreaActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
			public void onFinish() {
            	hintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				hintTv.setVisibility(View.VISIBLE);
			}
        });
    }


}
