package com.orfid.youxikuaile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.adapter.EmplyeeAdapter;
import com.orfid.youxikuaile.model.HttpRequstModel;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.pojo.GameUser;
import com.orfid.youxikuaile.pojo.User;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此activity,是雇佣好友界面和推荐好友页面 通过intent 传递过来的tag来区分。tag==1雇佣好友， tag==2 是推荐好友页面 如果
 * tag==1 调用emplyeeUrl接口。 如果 tag==2 调用recommendUrl 接口，获取数据 * @author clh
 * 
 * 
 */
public class EmplyeeActivity extends Activity implements ListItemClickHelp {
	private final String emplyeeUrl = "http://api.yxkuaile.com/apps/jfgc/friends";
	private final String recommendUrl = "http://api.yxkuaile.com/apps/jfgc/recommendUsers";
	private final String addEmplyeeUrl = "http://api.yxkuaile.com/apps/jfgc/addStaff";
	private GridView friendsGridView;
	private HttpRequstModel requstModel;
	private List<GameUser> gameUserList;
	private List<Integer> selectedList = new ArrayList<Integer>();
	private int page = 1;
	private int tag;
	private User userData;
	private EmplyeeAdapter ea;
	private Button nextPageBtn, sureBtn, exitBtn, preBtn;
	private RelativeLayout choiceLayout;
	private boolean lastPage;
	private TextView titName;
	private RelativeLayout relaLayout;
	/*
	private Handler emplyeeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
					relaLayout.setVisibility(View.GONE);
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							JSONArray array = json.getJSONArray("users");
							Type listType = new TypeToken<List<GameUser>>() {
							}.getType();
							Gson gson = new Gson();
							gameUserList = gson.fromJson(array.toString(),
									listType);
							lastPage = json.getBoolean("lastPage");

							if (gameUserList != null) {
								if (gameUserList.size() > 0) {

									setData();
								}
							}

						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										EmplyeeActivity.this,
										json.getJSONObject("error").getString(
												"info"), Toast.LENGTH_LONG)
										.show();
							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							// 添加成功
							Toast.makeText(
									getApplicationContext(),
									getResources()
											.getString(
													R.string.game_box_add_emplyee_success),
									Toast.LENGTH_SHORT).show();
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										EmplyeeActivity.this,
										json.getJSONObject("error").getString(
												"info"), Toast.LENGTH_LONG)
										.show();
							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

	};
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		tag = getIntent().getIntExtra("tag", 0);

		setContentView(R.layout.score_emplyee_layout);
		initView();
		getData();
		addListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		friendsGridView = (GridView) findViewById(R.id.game_box_score_game_1_emplyee_grid_view);
		nextPageBtn = (Button) findViewById(R.id.hayou_game_1_emplyee_rank_next_pagn);
		sureBtn = (Button) findViewById(R.id.hayou_score_game_1_emplyee_friends_sure);
		exitBtn = (Button) findViewById(R.id.hayou_score_game_1_emplyee_friends_exit);
		relaLayout = (RelativeLayout) findViewById(R.id.hayou_score_emplyee_progress_bar);
		preBtn = (Button) findViewById(R.id.hayou_score_game_1_emplyee_previous_button);
		choiceLayout = (RelativeLayout) findViewById(R.id.hayou_score_game_1_emplyee_previous_button_relative);
		titName = (TextView) findViewById(R.id.hayou_score_emplyee_dialog_top);
		if (tag == 1) {
			titName.setText(getResources().getString(
					R.string.hayou_score_game_1_emplyee_top));
		} else if (tag == 2) {
			titName.setText(getResources().getString(
					R.string.hayou_score_game_1_recommend_top));
		}
		choiceLayout.setVisibility(View.GONE);
	}

	private void addListener() {
		// TODO Auto-generated method stub
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EmplyeeActivity.this.finish();
			}
		});
		nextPageBtn.setVisibility(View.GONE);
		nextPageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (relaLayout.getVisibility() == View.GONE) {
				// if (emplyeeUserMap.size() % 9 == 0
				// && page < emplyeeUserMap.size() / 9 - 1) {
				// page++;
				// ItememplyeeUserMap.clear();
				//
				// for (int i = page * 9; i < page * 9 + 9; i++) {
				// ItememplyeeUserMap.put(i, emplyeeUserMap.get(i));
				// }
				// choiceLayout.setVisibility(View.VISIBLE);
				// setData();
				//
				// } else if (emplyeeUserMap.size() % 9 != 0
				// && page < emplyeeUserMap.size() / 9) {
				// page++;
				// ItememplyeeUserMap.clear();
				// if (page < emplyeeUserMap.size() / 9) {
				//
				// for (int i = page * 9; i < page * 9 + 9; i++) {
				// ItememplyeeUserMap.put(i, emplyeeUserMap.get(i));
				// }
				// } else if (page == emplyeeUserMap.size() / 9) {
				//
				// for (int i = page * 9; i < page * 9
				// + emplyeeUserMap.size() % 9; i++) {
				// ItememplyeeUserMap.put(i, emplyeeUserMap.get(i));
				// }
				//
				// }
				// choiceLayout.setVisibility(View.VISIBLE);
				// setData();
				// }
				//
				// }
				if (!lastPage) {
					page++;
					getData();
					if (page == 1) {
						choiceLayout.setVisibility(View.GONE);
					} else {
						choiceLayout.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		preBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (page > 0) {
				// ItememplyeeUserMap.clear();
				// for (int i = (page - 1) * 9; i < page * 9; i++) {
				// ItememplyeeUserMap.put(i, emplyeeUserMap.get(i));
				// }
				// page--;
				// if (page == 0) {
				// choiceLayout.setVisibility(View.GONE);
				// } else {
				// choiceLayout.setVisibility(View.VISIBLE);
				// }
				//
				// setData();
				// }
				if (page > 1) {
					page--;
					if (page == 1) {
						choiceLayout.setVisibility(View.GONE);
					} else {
						choiceLayout.setVisibility(View.VISIBLE);
					}
					getData();
				}
			}
		});
		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectedList.size() > 0) {
					// page--;
					// getData();
					addEmplyee();
				} else {
					Toast.makeText(EmplyeeActivity.this, "还没选择好友", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private void getData() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("token", ApplicationData.token));
//		params.add(new BasicNameValuePair("uid", ApplicationData.getInstance()
//				.getUid()));
//		params.add(new BasicNameValuePair("page", String.valueOf(page)));
//		if (tag == 1) {
//			requstModel.setRqquestHandler(EmplyeeActivity.this, emplyeeHandler,
//					params, 1, emplyeeUrl);
//		} else if (tag == 2) {
//
//			requstModel.setRqquestHandler(EmplyeeActivity.this, emplyeeHandler,
//					params, 1, recommendUrl);
//		}
		
		if (tag == 1) {
			try {
				doFetchFriendEmplyeeAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (tag == 2) {
			try {
				doFetchFriendEmplyeeAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void setData() {
		relaLayout.setVisibility(View.GONE);
		friendsGridView.setVisibility(View.VISIBLE);
		ea = new EmplyeeAdapter(this, gameUserList, selectedList, this);
		friendsGridView.setAdapter(ea);
	}

	private void addEmplyee() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("token", ApplicationData.token));
//		params.add(new BasicNameValuePair("uid", ApplicationData.getInstance()
//				.getUid()));
//
//		JSONArray ja = new JSONArray();
//		if (selectedList.size() > 0) {
//			for (Integer item : selectedList) {
//				ja.put(item);
//			}
//		}
//		params.add(new BasicNameValuePair("uid1", ja.toString()));
//		requstModel.setRqquestHandler(EmplyeeActivity.this, emplyeeHandler,
//				params, 2, addEmplyeeUrl);
		
		try {
			doAddEmplyeeAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View item, View widget, int position, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case R.id.hayou_game_1_emolyee_item_layout:
			if (selectedList.contains(position)) {
				selectedList.remove((Integer) position);
			} else {
				selectedList.add((Integer) position);
			}
			break;

		default:
			break;
		}
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (ApplicationData.getInstance() != null) {
//			outState.putParcelable("userData", ApplicationData.getInstance());
//
//		}
//
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		gameUserList = null;
		selectedList = null;
		ea = null;
	
	}
	
	private void doFetchFriendEmplyeeAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", user.get("uid").toString());
        HttpRestClient.post("apps/jfgc/recommendUsers", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONObject data= response.getJSONObject("data");
                    	JSONArray array = data.getJSONArray("items");
                    	Type listType = new TypeToken<List<GameUser>>() {
						}.getType();
						Gson gson = new Gson();
						gameUserList = gson.fromJson(array.toString(),
								listType);
//						lastPage = response.getBoolean("lastPage");

						if (gameUserList != null) {
							if (gameUserList.size() > 0) {

								setData();
							}
						}
                    } else if (status == 0) {
                        Toast.makeText(EmplyeeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            

        });
    }
	
	private void doFetchRecommendEmplyeeAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", user.get("uid").toString());
        HttpRestClient.post("apps/jfgc/friends", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONObject data= response.getJSONObject("data");
                    	JSONArray array = data.getJSONArray("items");
                    	Type listType = new TypeToken<List<GameUser>>() {
						}.getType();
						Gson gson = new Gson();
						gameUserList = gson.fromJson(array.toString(),
								listType);
//						lastPage = response.getBoolean("lastPage");

						if (gameUserList != null) {
							if (gameUserList.size() > 0) {

								setData();
							}
						}
                    } else if (status == 0) {
                        Toast.makeText(EmplyeeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            

        });
    }
	
	private void doAddEmplyeeAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", selectedList);
        HttpRestClient.post("apps/jfgc/addStaff", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	
                    } else if (status == 0) {
                        Toast.makeText(EmplyeeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	
}
