package com.orfid.youxikuaile;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orfid.youxikuaile.model.HttpRequstModel;
import com.orfid.youxikuaile.pojo.DiceData;
import com.orfid.youxikuaile.pojo.RankUser;
import com.orfid.youxikuaile.pojo.User;
import com.orfid.youxikuaile.pojo.UserIcon;
import com.orfid.youxikuaile.widget.CircularImageView;
import com.orfid.youxikuaile.widget.ScoreView;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此activity,是个积分工厂游戏activity。 这个界面主要由自定义ScoreView 来绘制游戏区。
 * 
 * 通过调用 getAllInfoUrl 获取雇员的信息，以及坐标位置 点击工作工厂，调用factoryUrl，可以得到该玩游戏用户，在那几个好友工作打工
 * 并且点击离开工作，调用leaveFactoryUrl离开工厂 用户位置被移动后，需调用setPositionUrl接口发送数据保存 通过
 * 在不同的状态下调用getScoreUrl 获取用户此时账户信息。 * @author clh
 * 
 */
public class ScoreGameActivity extends Activity {
	private final String getAllInfoUrl = "http://api.yxkuaile.com/apps/jfgc";
	private final String factoryUrl = "http://api.yxkuaile.com/apps/jfgc/workfactory";
	private final String leaveFactoryUrl = "http://api.yxkuaile.com/apps/jfgc/leave";
	private final String getScoreUrl = "http://api.yxkuaile.com/apps/jfgc/harvest";
	private final String setPositionUrl = "http://api.yxkuaile.com/apps/jfgc/position";
	private ScoreView mView;
	private Button workFactoryBtn, employeeFriendsBtn, employeeBtn,
			friendsFactBtn, reCommendBtn, oneClickBtn, helpBtn, exitBtn,
			shareBtn;
	private CircularImageView userIcon;
	private TextView userName, userScore;
	private Dialog builder;
	private HttpRequstModel requstModel;
	private List<UserIcon> userIconList;
	private List<RankUser> factoryList;
	private User userData;
	private boolean weixinChecked = false, qqChecked = false;
	/*******************************/
	/*public static QQAuth mQQAuth;
	/*private IWXAPI api;
	/*private Tencent mTencent;
	/**************************/
	private boolean isRun = true;
	private String APP_ID = "1101639686";
	private String APP_KEY_SEC = "j5TYxVnijyFBM8mK";
	// 要保存的文件名
	private HashMap<String, Bitmap> gameInfoIcon = new HashMap<String, Bitmap>();
	private DiceData ddData;
	// tag==1获取工作工厂，tag==2 离开工厂，tag==3一键收积分；tag==4单个收积分
	// tag==7dialog消失
	private int tag, factoryIndex, scoreNum;
	private JSONObject jsonObject;
	private DisplayImageOptions options;
	private LayoutInflater loadInflater;
	private LinearLayout layout;
	private Timer timer;
	private String staffID, fuid;
	private TimerTask timerTask;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	/*
	private Handler scoreGameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				try {

					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							JSONObject js = json.getJSONObject("data");
							JSONArray array = js.getJSONArray("staff");
							JSONObject jt = js.getJSONObject("userAccount");
							Type listType = new TypeToken<List<UserIcon>>() {
							}.getType();
							Gson gson = new Gson();
							userIconList = gson.fromJson(array.toString(),
									listType);
							ddData = gson.fromJson(jt.toString(),
									DiceData.class);
							if (ddData != null) {
								userScore.setText(String.valueOf(ddData
										.getIntegral()));
							}

							if (userIconList != null) {
								if (userIconList.size() > 0) {
									chacked();

								}
							}
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										ScoreGameActivity.this,
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
			case 1:
				// 获取工厂信息
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							JSONArray jsArray = json.getJSONArray("users");
							Type listType = new TypeToken<List<RankUser>>() {
							}.getType();
							Gson gson = new Gson();
							factoryList = gson.fromJson(jsArray.toString(),
									listType);
							if (factoryList != null) {
								if (factoryList.size() > 0) {
									getDialog();
								} else {
									Toast.makeText(
											getApplicationContext(),
											getResources().getString(
													R.string.ha_factory_no_sit),
											Toast.LENGTH_SHORT).show();
								}
							}

						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										ScoreGameActivity.this,
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
				// 获取工厂信息
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							// 离开工厂
							// tag = 2;
							// getData();
							// layout.removeViewAt(factoryIndex);
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										ScoreGameActivity.this,
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
			case 3:
				// 一键收积分
				// 获取工厂信息
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							scoreNum = json.getInt("integral");
							//ApplicationData.userName = json.getString("userName");
							JSONObject jt = json.getJSONObject("userAccount");

							Gson gson = new Gson();
							ddData = gson.fromJson(jt.toString(),
									DiceData.class);

							// 一键收积分弹层
							getScoreDialog();
							if (ddData != null) {
								userScore.setText(String.valueOf(ddData
										.getIntegral()));
							}
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										ScoreGameActivity.this,
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
			case 4:
				// 单个受积分
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
							ApplicationData.token = json.getString("token");
							// 收积分弹层
							scoreNum = json.getInt("integral");
							JSONObject jt = json.getJSONObject("userAccount");

							Gson gson = new Gson();
							ddData = gson.fromJson(jt.toString(),
									DiceData.class);

							getScoreDialog();
							if (ddData != null) {
								userScore.setText(String.valueOf(ddData
										.getIntegral()));
							}
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										ScoreGameActivity.this,
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
			case 5:
				// 单个收积分
				tag = 4;
				staffID = (String) msg.obj;
				getData();
				break;
			case 6:
				jsonObject = (JSONObject) msg.obj;
				tag = 8;
				getData();
				break;
			case 7:
				if (builder != null) {
					if (builder.isShowing()) {
						builder.dismiss();
					}
				}
				break;
			case 8:
				break;
			case 11:

				if (gameInfoIcon.size() == userIconList.size()) {
					Log.i("info", "gameInfoIcon.size() == userIconList.size()");
					mView.setData(gameInfoIcon, userIconList);
					timer = new Timer();
					timerTask = new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							getUserData();
						}
					};
					timer.schedule(timerTask, 12000);
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
		
//		api = WXAPIFactory.createWXAPI(getApplicationContext(),
//				"wxea88f1583d4ec313", true);
//		api.registerApp("wxea88f1583d4ec313");

		loadInflater = LayoutInflater.from(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setContentView(R.layout.score_game_1);
		initView();
//		getUserData();
//		addListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mView = (ScoreView) findViewById(R.id.hayou_game_1_zone);
//		getUserData();
//		mView.setHandler(scoreGameHandler);
		shareBtn = (Button) findViewById(R.id.center_score_game_1_invite_btn);
		workFactoryBtn = (Button) findViewById(R.id.center_score_game_1_work_factroy);
		employeeFriendsBtn = (Button) findViewById(R.id.center_score_game_1_employee_friends);
		employeeBtn = (Button) findViewById(R.id.center_score_game_1_employee_btn);
		friendsFactBtn = (Button) findViewById(R.id.center_score_game_1_friends_factroy);
		reCommendBtn = (Button) findViewById(R.id.center_score_game_1_recommend_btn);
		oneClickBtn = (Button) findViewById(R.id.center_score_game_1_one_click);
		helpBtn = (Button) findViewById(R.id.center_score_game_1_help_btn);
		exitBtn = (Button) findViewById(R.id.hayou_game_1_exit_btn);
		userIcon = (CircularImageView) findViewById(R.id.center_score_game_1_user_icon);
		userName = (TextView) findViewById(R.id.center_score_game_1_user_name);
		userScore = (TextView) findViewById(R.id.center_score_game_1_user_score_num);
		userName.setText("lalala");
//		if (ApplicationData.getInstance().getPhoto() != null) {
//			if (!ApplicationData.getInstance().getPhoto().equals("")) {
//				ImageLoader.getInstance().displayImage(
//						ApplicationData.getInstance().getPhoto(), userIcon,
//						options);
//			}
//		}

	}
/*
	private void addListener() {
		// TODO Auto-generated method stub
		employeeFriendsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ii = new Intent(ScoreGameActivity.this,
						EmplyeeActivity.class);
				ii.putExtra("tag", 1);
				ScoreGameActivity.this.startActivity(ii);

			}
		});
		workFactoryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tag = 1;
				getData();
			}
		});
		employeeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ii = new Intent(ScoreGameActivity.this,
						GameFriendRankActiviy.class);
				ii.putExtra("tag", 1);
				ScoreGameActivity.this.startActivity(ii);
			}
		});
		friendsFactBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ii = new Intent(ScoreGameActivity.this,
						GameFriendRankActiviy.class);
				ii.putExtra("tag", 0);
				ScoreGameActivity.this.startActivity(ii);
			}
		});
		reCommendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ii = new Intent(ScoreGameActivity.this,
						EmplyeeActivity.class);
				ii.putExtra("tag", 2);
				ScoreGameActivity.this.startActivity(ii);
			}
		});
		helpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent ii = new Intent(ScoreGameActivity.this,
						ScoreHelpActivity.class);
				ScoreGameActivity.this.startActivity(ii);
			}
		});
		oneClickBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tag = 3;
				getData();
			}
		});
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ScoreGameActivity.this.finish();
			}
		});
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getShareDialog();
			}
		});
	}

	private void getDialog() {
		if (builder != null) {
			if (builder.isShowing()) {
				builder.dismiss();
			}
		}
		builder = new Dialog(ScoreGameActivity.this, R.style.my_dialog);
		builder.show();
		View contentview = LayoutInflater.from(ScoreGameActivity.this).inflate(
				R.layout.score_game_1_work_factory_dialog, null);
		builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		builder.getWindow().setContentView(
				R.layout.score_game_1_work_factory_dialog);
		builder.getWindow().setGravity(Gravity.CENTER);
		layout = (LinearLayout) builder
				.findViewById(R.id.hayou_score_2_content_layout);
		if (factoryList.size() > 0) {
			ApplicationData.initImageLoader(this);
			layout.removeAllViews();
			int n = factoryList.size();
			if (n > 5) {
				n = 5;
			}

			for (int i = 0; i < n; i++) {
				View content = LayoutInflater.from(ScoreGameActivity.this)
						.inflate(R.layout.score_game_1_work_factory_item, null);
				CircularImageView icon = (CircularImageView) content
						.findViewById(R.id.hayou_game_1_work_factroy_user_icon_1);
				TextView userName = (TextView) content
						.findViewById(R.id.hayou_game_1_work_factroy_user_score_name_1);
				TextView scoreNum = (TextView) content
						.findViewById(R.id.hayou_game_1_work_factroy_user_score_num_1);
				Button exitButton = (Button) content
						.findViewById(R.id.hayou_game_1_work_factroy_score_exit_1);
				ImageLoader.getInstance().displayImage(
						factoryList.get(i).getPhoto(), icon, options);
				scoreNum.setText("" + factoryList.get(i).getIntegral());
				userName.setText(factoryList.get(i).getUsername());
				final int j = i;
				exitButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						tag = 2;
						fuid = factoryList.get(j).getUid();
						factoryIndex = j;
						getData();
						builder.dismiss();
					}
				});
				layout.addView(content);

			}

		} else {
			layout.removeAllViews();
			View content = loadInflater.inflate(
					R.layout.score_game_1_work_factory_text_item, null);
			layout.addView(content);
		}
	}

	private void getData() {
		if (requstModel == null) {
			requstModel = new HttpRequstModel();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", ApplicationData.token));

		if (tag == 1) {
			// 工作工厂
			params.add(new BasicNameValuePair("uid", ApplicationData
					.getInstance().getUid()));
			params.add(new BasicNameValuePair("userName", ApplicationData
					.getInstance().getUserName()));
			requstModel.setRqquestHandler(ScoreGameActivity.this,
					scoreGameHandler, params, tag, factoryUrl);
		} else if (tag == 2) {
			// 离开工厂
			params.add(new BasicNameValuePair("uid", ApplicationData
					.getInstance().getUid()));
			params.add(new BasicNameValuePair("fuid", fuid));
			requstModel.setRqquestHandler(ScoreGameActivity.this,
					scoreGameHandler, params, tag, leaveFactoryUrl);
		} else if (tag == 3) {
			// 一键收积分
			params.add(new BasicNameValuePair("uid", ApplicationData
					.getInstance().getUid()));
			params.add(new BasicNameValuePair("staffID", "0"));
			requstModel.setRqquestHandler(ScoreGameActivity.this,
					scoreGameHandler, params, tag, getScoreUrl);
		} else if (tag == 4) {
			// 单个手积分
			params.add(new BasicNameValuePair("uid", ApplicationData
					.getInstance().getUid()));
			params.add(new BasicNameValuePair("staffID", String
					.valueOf(staffID)));
			requstModel.setRqquestHandler(ScoreGameActivity.this,
					scoreGameHandler, params, tag, getScoreUrl);
		} else if (tag == 8) {

			try {
				params.add(new BasicNameValuePair("uid", ApplicationData
						.getInstance().getUid()));
				params.add(new BasicNameValuePair("staffID", jsonObject
						.getJSONArray("uid").toString()));
				params.add(new BasicNameValuePair("position", String
						.valueOf(jsonObject.getInt("position"))));
				requstModel.setRqquestHandler(ScoreGameActivity.this,
						scoreGameHandler, params, tag, setPositionUrl);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void getUserData() {
		if (requstModel == null) {
			requstModel = new HttpRequstModel();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", ApplicationData.token));
		params.add(new BasicNameValuePair("uid", ApplicationData.getInstance()
				.getUid()));
		requstModel.setRqquestHandler(ScoreGameActivity.this, scoreGameHandler,
				params, 0, getAllInfoUrl);
	}

	private void chacked() {
		if (isRun) {

			Iterator iter = gameInfoIcon.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();

				for (int i = 0; i < userIconList.size(); i++) {
					if (userIconList.get(i).getId().equals(key)) {
						break;
					} else if (i == userIconList.size() - 1
							&& !userIconList.get(i).getId().equals(key)) {
						gameInfoIcon.remove(key);
					}
				}

			}

			for (final UserIcon item : userIconList) {
				if (!gameInfoIcon.containsKey(item.getId())) {
					executorService.submit(new Runnable() {
						@Override
						public void run() {
							try {
								Bitmap bm;
								String path = item.getPhoto();
								if (path != null) {
									if (!path.equals("")) {
										path = path.replaceAll("files",
												"files/50");
										URL url = new URL(path);
										HttpURLConnection conn = (HttpURLConnection) url
												.openConnection();
										conn.setDoInput(true);
										conn.connect();
										InputStream inputStream = conn
												.getInputStream();
										bm = BitmapFactory
												.decodeStream(inputStream);
									} else {
										Drawable d = getResources()
												.getDrawable(
														R.drawable.forum_icon);

										BitmapDrawable bd = (BitmapDrawable) d;

										bm = bd.getBitmap();
									}
								} else {
									Drawable d = getResources().getDrawable(
											R.drawable.forum_icon);

									BitmapDrawable bd = (BitmapDrawable) d;

									bm = bd.getBitmap();
								}

								gameInfoIcon.put(item.getId(), bm);
								bm = null;
								scoreGameHandler.sendEmptyMessage(11);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}

	}

	private void getScoreDialog() {
		if (builder != null) {
			if (builder.isShowing()) {
				builder.dismiss();
			}
		}
		builder = new Dialog(ScoreGameActivity.this, R.style.my_dialog);
		builder.show();
		View contentview = LayoutInflater.from(ScoreGameActivity.this).inflate(
				R.layout.get_score, null);
		builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		builder.getWindow().setContentView(R.layout.get_score);
		builder.getWindow().setGravity(Gravity.CENTER);
		TextView topText = (TextView) builder.findViewById(R.id.get_score_top);
		TextView contentText = (TextView) builder
				.findViewById(R.id.get_score_text);
		StringBuilder sb = new StringBuilder();
		if (tag == 4) {
			topText.setVisibility(View.GONE);
			sb.append("收取").append(String.valueOf(scoreNum)).append("积分");
			contentText.setText(sb.toString());
		} else if (tag == 3) {
			topText.setVisibility(View.VISIBLE);
			sb.append("一共收取").append(String.valueOf(scoreNum)).append("积分");
			contentText.setText(sb.toString());
		}
		scoreGameHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scoreGameHandler.sendEmptyMessage(7);
			}
		}, 2000);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (ApplicationData.getInstance() != null) {
			outState.putParcelable("userData", ApplicationData.getInstance());

		}

	}

	private void getShareDialog() {
		builder = new Dialog(this, R.style.my_dialog);
		builder.show();
		View contentview = LayoutInflater.from(this).inflate(
				R.layout.share_dialog, null);
		builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		builder.getWindow().setContentView(R.layout.share_dialog);
		final ImageView weixinImg;
		final ImageView qqImg;
		RelativeLayout cancelLayout, positiveLayout, weixinLayout, qqLayout;
		weixinImg = (ImageView) builder.findViewById(R.id.share_weixin_img);
		qqImg = (ImageView) builder.findViewById(R.id.share_qq_img);
		cancelLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_cancel);
		positiveLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_positive);
		weixinLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_weixin_layout);
		qqLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_qq_layout);

		weixinLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weixinChecked) {
					weixinChecked = false;
					weixinImg.setImageResource(R.drawable.share_weixin_check1);
				} else {
					weixinChecked = true;
					qqChecked = false;
					qqImg.setImageResource(R.drawable.share_qq_check1);
					weixinImg
							.setImageResource(R.drawable.share_weixin_checked1);
				}
			}
		});
		qqLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (qqChecked) {
					qqChecked = false;
					qqImg.setImageResource(R.drawable.share_qq_check1);
				} else {
					qqChecked = true;
					weixinChecked = false;
					weixinImg.setImageResource(R.drawable.share_weixin_check1);
					qqImg.setImageResource(R.drawable.share_qq_checked1);
				}
			}
		});
		cancelLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.dismiss();
			}
		});
		positiveLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weixinChecked) {
					WXWebpageObject webpage = new WXWebpageObject();
					webpage.webpageUrl = "http://www.yxkuaile.com/share/wx_jfgc.html";
					WXMediaMessage msg = new WXMediaMessage(webpage);
					msg.title = getResources().getString(
							R.string.share_title_txt_game_score);
					// msg.description =
					// getResources().getString(R.string.share_title_txt_game_score);
					Bitmap bmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.x152);
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 90, 90,
							true);
					msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = String.valueOf(System.currentTimeMillis());
					req.message = msg;
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
					api.sendReq(req);
					builder.dismiss();
				} else if (qqChecked) {
					mQQAuth = QQAuth.createInstance(APP_ID,
							getApplicationContext());
					mTencent = Tencent.createInstance(APP_ID,
							ScoreGameActivity.this);
					final Bundle params = new Bundle();
					params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
							QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
					params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME,
							getResources().getString(R.string.app_name));// 必填
					params.putString(QzoneShare.SHARE_TO_QQ_TITLE,
							getResources().getString(R.string.app_name));// 必填
					params.putString(
							QzoneShare.SHARE_TO_QQ_SUMMARY,
							getResources().getString(
									R.string.share_title_txt_game_score));

					params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
							"http://www.yxkuaile.com/share/jfgc.html");// 必填
					ArrayList<String> pics = new ArrayList<String>();
					pics.add("http://static.yxkuaile.com/yxxiazi/64.gif");
					params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
							pics);
					mTencent.shareToQzone(ScoreGameActivity.this, params,
							new BaseUiListener());
					builder.dismiss();
				}
			}
		});

	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(IndexActivity.this, response.toString(),
			// "登录成功");
			Log.i("info", "share is success");
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			// Util.toastMessage(IndexActivity.this, "onError: " +
			// e.errorDetail);
			// Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			// Util.toastMessage(IndexActivity.this, "onCancel: ");
			// Util.dismissDialog();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mView != null) {
			mView.deteBitmap();
		}
		userIconList = null;
		factoryList = null;
		gameInfoIcon = null;
		isRun = false;

	}
	*/
}
