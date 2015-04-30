package com.orfid.youxikuaile;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.adapter.FriendsAdapter;
import com.orfid.youxikuaile.pojo.RankUser;
import com.orfid.youxikuaile.pojo.User;

/**
 * 此activity,是员工排行榜和好友积分排行榜界面 通过intent 传递过来的tag来区分。tag==0好友积分排行榜， tag==1
 * 是员工积分排行榜界面 不管tag等于多少 都是调用rankUrl 接口。通过传递不同的参数区分开 如果tag==1
 * 可以点击踢出好友，调用refuseUrl。 * @author clh
 * 
 * 
 */
public class GameFriendRankActiviy extends Activity {
	private final String rankUrl = "http://api.yxkuaile.com/apps/jfgc/sortlist";
	private final String refuseUrl = "http://api.yxkuaile.com/apps/jfgc/removeStaff";
	private ListView friendsList;
	private int page = 1;
	private int tag;
	private String staffID;
//	private HttpRequstModel requstModel;
	private List<RankUser> rankUserList;
	private boolean lastPage = true;
	private Button nextBtn, exitBtn, preBtn;
	private FriendsAdapter fa;
	private RelativeLayout reLayout;
	private TextView titleTop;
	private User userData;
	private RelativeLayout pbLayout;
	private PopupWindow mWindow;
	private Handler rankHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
					pbLayout.setVisibility(View.GONE);
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
//							ApplicationData.token = json.getString("token");
							JSONArray array = json.getJSONArray("users");
							Type listType = new TypeToken<List<RankUser>>() {
							}.getType();
							Gson gson = new Gson();
							rankUserList = gson.fromJson(array.toString(),
									listType);
							lastPage = json.getBoolean("lastPage");
							if (rankUserList != null) {

								setData();

							}
							// if (lastPage) {
							// nextBtn.setVisibility(View.GONE);
							// } else {
							// nextBtn.setVisibility(View.VISIBLE);
							// }
							// if (page == 1) {
							// preBtn.setVisibility(View.GONE);
							// } else {
							// preBtn.setVisibility(View.VISIBLE);
							// }

						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										GameFriendRankActiviy.this,
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
					pbLayout.setVisibility(View.GONE);
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
//							ApplicationData.token = json.getString("token");
							getData();
						} else if (json.getInt("status") == 0) {
							pbLayout.setVisibility(View.GONE);
							if (json.has("error")) {
								Toast.makeText(
										GameFriendRankActiviy.this,
										json.getJSONObject("error").getString(
												"info"), Toast.LENGTH_LONG)
										.show();
							}

						}

					} else {
						pbLayout.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_ranking_layout);
//		ApplicationData.getApplicatinInstance().addActivity(this);
		// tag:0好友 1员工
		tag = getIntent().getIntExtra("tag", -1);
		initView();
		getData();
		addLister();
	}

	private void initView() {
		// TODO Auto-generated method stub
		friendsList = (ListView) findViewById(R.id.hayou_score_game_1_rank_list);
		nextBtn = (Button) findViewById(R.id.hayou_game_1_rank_next_pagn);
		exitBtn = (Button) findViewById(R.id.hayou_game_1_rank_exit);
		preBtn = (Button) findViewById(R.id.hayou_score_game_1_previous_button);
		reLayout = (RelativeLayout) findViewById(R.id.hayou_score_game_1_previous_button_relative);
		pbLayout = (RelativeLayout) findViewById(R.id.hayou_score_choice_progress_bar);
		titleTop = (TextView) findViewById(R.id.hayou_score_rank_dialog_top);
		if (tag == 0) {
			titleTop.setText(getResources().getString(
					R.string.hayou_score_game_1_rank_friends_top));
		} else if (tag == 1) {
			titleTop.setText(getResources().getString(
					R.string.hayou_score_game_1_rank_emplyee_top));
		}
		reLayout.setVisibility(View.GONE);
	}

	private void addLister() {
		// TODO Auto-generated method stub
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				GameFriendRankActiviy.this.finish();
			}
		});
		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!lastPage) {
					page++;
					reLayout.setVisibility(View.VISIBLE);
					// pbLayout.setVisibility(View.VISIBLE);

					getData();
					if (page > 1) {
						reLayout.setVisibility(View.VISIBLE);
					} else {
						reLayout.setVisibility(View.GONE);
					}
				} else {
					Toast.makeText(
							GameFriendRankActiviy.this,
							getResources().getString(
									R.string.ha_score_game_2_lastpage_true),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		preBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (page > 1) {
					page--;
					pbLayout.setVisibility(View.VISIBLE);
					if (page == 1) {
						reLayout.setVisibility(View.GONE);
					} else {
						reLayout.setVisibility(View.VISIBLE);
					}
					getData();

				}
			}
		});
		friendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// tag==1员工，
				if (tag == 1) {
					if (mWindow != null) {
						mWindow.dismiss();
					}
					TextView tv = (TextView) arg1
							.findViewById(R.id.hayou_game_1_rank_item_user_score);
					View contentView = LayoutInflater.from(
							GameFriendRankActiviy.this).inflate(
							R.layout.score_game_1_rank_popupwindow, null);

					Button passBtn = (Button) contentView
							.findViewById(R.id.score_game_1_score_pass_button);
					final RelativeLayout rLayout = (RelativeLayout) contentView
							.findViewById(R.id.score_game_1_score_pass_pb);
					rLayout.setVisibility(View.GONE);
					mWindow = new PopupWindow(tv, LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					mWindow.setFocusable(true);
					mWindow.setContentView(contentView);
					mWindow.setAnimationStyle(R.style.anim_menu_rightbar);
					mWindow.setBackgroundDrawable(new BitmapDrawable());
					// mWindow.setOutsideTouchable(true);
					mWindow.showAsDropDown(
							tv,
							GameFriendRankActiviy.this
									.getResources()
									.getDimensionPixelSize(
											R.dimen.hayou_user_score_popupweindow_item_x),
							-1
									* GameFriendRankActiviy.this
											.getResources()
											.getDimensionPixelSize(
													R.dimen.hayou_score_game_1popupwindow));
					final int num = arg2;
					passBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mWindow.dismiss();
							staffID = rankUserList.get(num).getId();
							pbLayout.setVisibility(View.VISIBLE);
							rLayout.setVisibility(View.VISIBLE);
							refuseData();

						}
					});

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
//		params.add(new BasicNameValuePair("type", String.valueOf(tag)));
//		Log.i("info", "uid:" + ApplicationData.getInstance().getUid());
//		Log.i("info", "page:" + String.valueOf(page));
//		Log.i("info", "type:" + String.valueOf(tag));
//
//		requstModel.setRqquestHandler(GameFriendRankActiviy.this, rankHandler,
//				params, 1, rankUrl);
		
		try {
			doFetchRankListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void refuseData() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("token", ApplicationData.token));
//		params.add(new BasicNameValuePair("uid", ApplicationData.getInstance()
//				.getUid()));
//		params.add(new BasicNameValuePair("staffID", staffID));
//
//		requstModel.setRqquestHandler(GameFriendRankActiviy.this, rankHandler,
//				params, 2, refuseUrl);
	}

	private void setData() {
		fa = new FriendsAdapter(this, rankUserList, page);
		friendsList.setAdapter(fa);
	}
	
	
	private void doFetchRankListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("type", tag);
        HttpRestClient.post("apps/jfgc/sortlist", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response====(jfgc/sortlist)==>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	pbLayout.setVisibility(View.GONE);
//                    	JSONArray array = json.getJSONArray("users");
//						Type listType = new TypeToken<List<RankUser>>() {
//						}.getType();
//						Gson gson = new Gson();
//						rankUserList = gson.fromJson(array.toString(),
//								listType);
//						lastPage = json.getBoolean("lastPage");
//						if (rankUserList != null) {
//
//							setData();
//
//						}
                    } else if (status == 0) {
                        Toast.makeText(GameFriendRankActiviy.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
