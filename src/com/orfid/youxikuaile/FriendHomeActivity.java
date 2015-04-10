package com.orfid.youxikuaile;

import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2015/3/11.
 */
public class FriendHomeActivity extends Activity implements View.OnClickListener {

    private String uid;
    private ProgressBar mPbar;
    private ImageView userPhoto, followActionHintIv, feedItemFileIv, gameItemIconIv;
    private TextView titleTv, uidTv, collegeTv, 
    	gendernageTv, followActionHintTv, fansCountTv, distanceInfoTv,
    	feedItemTextTv, sitterDescTv, gameItemNameTv;
    private ImageButton backBtn;
    private View followBtnView, chatBtnView, feedItemLl, gameItemLl;
    private boolean isFollowed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_home);
        init();
//        try {
//            doFetchUserInfoAction();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void init() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        isFollowed = intent.getBooleanExtra("isFollowed", false);
        String username = intent.getStringExtra("username");
        String photo = intent.getStringExtra("photo");
        
        Log.d("uid>>>>>>+++++++", uid);

        mPbar = (ProgressBar) findViewById(R.id.progress_bar);
        userPhoto = (ImageView) findViewById(R.id.user_picture);
        titleTv = (TextView) findViewById(R.id.tv_title);
        uidTv = (TextView) findViewById(R.id.tv_uid);
        collegeTv = (TextView) findViewById(R.id.tv_college);
        gendernageTv = (TextView) findViewById(R.id.tv_gender_n_age);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        followBtnView = findViewById(R.id.btn_follow);
        chatBtnView = findViewById(R.id.btn_chat);
        followActionHintTv = (TextView) findViewById(R.id.follow_action_hint_tv);
        followActionHintIv = (ImageView) findViewById(R.id.follow_action_hint_iv);
        fansCountTv = (TextView) findViewById(R.id.fans_count_tv);
        distanceInfoTv = (TextView) findViewById(R.id.distance_info_tv);
        feedItemLl = findViewById(R.id.feed_item_ll);
        feedItemTextTv = (TextView) findViewById(R.id.feed_item_text_tv);
        feedItemFileIv = (ImageView) findViewById(R.id.feed_item_file_iv);
        sitterDescTv = (TextView) findViewById(R.id.sitter_desc_tv);
        gameItemLl = findViewById(R.id.game_item_ll);
        gameItemIconIv = (ImageView) findViewById(R.id.game_item_icon_iv);
        gameItemNameTv = (TextView) findViewById(R.id.game_item_name_tv);
        

        backBtn.setOnClickListener(this);
        followBtnView.setOnClickListener(this);
        chatBtnView.setOnClickListener(this);

        uidTv.setText(uid);
        if (username != null && !username.equals("null")) titleTv.setText(username);
        if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, userPhoto);
        if (isFollowed == true) {
        	followActionHintTv.setText("取消关注");
        	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_unfollow_selector));
        }
        
        try {
			doFetchUserInfoAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            	Intent intent = new Intent();
            	intent.putExtra("isFollowed", isFollowed);
            	setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_follow:
            	if (isFollowed == false) {
	                try {
	                    doFollowUserAction();
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
            	} else {
            		new AlertDialog.Builder(this)
            			.setTitle("提示")
            			.setMessage("确定取消关注?")
            			.setPositiveButton(getResources().getString(android.R.string.ok), new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
				                    doUnFollowUserAction();
				                } catch (JSONException e) {
				                    e.printStackTrace();
				                }
							}
            				
            			})
            			.setNegativeButton(getResources().getString(android.R.string.cancel), null)
            			.show();
            		
            	}
            	
                break;
            case R.id.btn_chat:
            	Intent i = new Intent(FriendHomeActivity.this, ChattingActivity.class);
            	i.putExtra("uid", uid);
            	startActivity(i);
            	break;
        }
    }

//    private void doFetchUserInfoAction() throws JSONException {
//        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
//        HashMap user = dbHandler.getUserDetails();
//        RequestParams params = new RequestParams();
//        params.put("token", user.get("token").toString());
//        params.put("uid", uid);
//        HttpRestClient.post("user/GetInfo", params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("response=======>", response.toString());
//                try {
//                    int status = response.getInt("status");
//                    if (status == 1) { // success
//                        JSONObject data = response.getJSONObject("data");
//                        int genderIndex = Integer.parseInt(data.getString("sex"));
//                        String username = data.getString("username");
//                        String birthdayTimestamp = data.getString("birthday");
//                        String school = data.getString("school");
//                        String photo = data.getString("photo");
//
//                        if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, userPhoto);
//                        if (school != null && !school.equals("null")) collegeTv.setText(school);
////                        if (birthdayTimestamp != null ) gendernageTv.setText(Util.getAgeByTimestamp(Long.parseLong(birthdayTimestamp)));
//                    } else if (status == 0) {
//                        Toast.makeText(FriendHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onStart() {
//                mPbar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFinish() {
//                mPbar.setVisibility(View.GONE);
//            }
//        });
//    }
    
    @Override
	public void onBackPressed() {
    	Intent intent = new Intent();
    	intent.putExtra("isFollowed", isFollowed);
    	setResult(RESULT_OK, intent);
        finish();
	}

	private void doFollowUserAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("followUid", uid);
        HttpRestClient.post("follow/addFollow", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	isFollowed = true;
                    	Toast.makeText(FriendHomeActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(FriendHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


			@Override
			public void onFinish() {
            	followActionHintTv.setText("取消关注");
            	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_unfollow_selector));
				followBtnView.setClickable(true);
				mPbar.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				followBtnView.setClickable(false);
				mPbar.setVisibility(View.VISIBLE);
			}

        });
    }
    
    private void doUnFollowUserAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", uid);
        HttpRestClient.post("follow/cancel", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	isFollowed = false;
                    	Toast.makeText(FriendHomeActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(FriendHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
				followActionHintTv.setText("加关注");
	        	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_follow_selector));
				followBtnView.setClickable(true);
				mPbar.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				followBtnView.setClickable(false);
				mPbar.setVisibility(View.VISIBLE);
			}
            

        });
    }
    
    private void doFetchUserInfoAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", uid);
        HttpRestClient.post("home", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONObject data = response.getJSONObject("data");
                    	JSONObject user = data.getJSONObject("user");
                    	isFollowed = data.getBoolean("isFollow");
                    	String school = user.isNull("school")?"":user.getString("school");
                    	collegeTv.setText(school);
                    	String birthday = user.has("birthday") && user.isNull("birthday")?null:user.getString("birthday");
                    	Log.d("birthday=====>", birthday);
                    	if (birthday != null) {
                    		String age = Utils.getAge(Long.parseLong(birthday) * 1000)+"";
                    		gendernageTv.setText(age);
                    	}
                    	int sex;
                    	sex = user.isNull("sex")?2:user.getInt("sex");
                    	if (sex == 1) {
                    		gendernageTv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.icon_man, 0, 0, 0);
                    	}
                    	fansCountTv.setText(user.getString("fans"));
                    	if (!data.getString("distance").equals("未知距离") && !data.isNull("distance") && !data.getString("distance").equals("-1")) {
                    		distanceInfoTv.setText(data.getString("distance") + " | 1小时前");
                    		distanceInfoTv.setVisibility(View.VISIBLE);
                    	}
                    	if (!data.isNull("feed")) {
                    		JSONObject jFeed = data.getJSONObject("feed");
                    		if (jFeed.has("files")) {
                    			if (!jFeed.isNull("files") && !jFeed.getString("files").equals("[]")) {
                    				JSONArray files = jFeed.getJSONArray("files");
                    				JSONObject jObj = files.getJSONObject(0);
                    				String url = jObj.getString("url");
                    				ImageLoader.getInstance().displayImage(url, feedItemFileIv);
                    			}
                    		}
                    		String text = jFeed.getString("text");
                    		feedItemTextTv.setText(text);
                    		feedItemLl.setVisibility(View.VISIBLE);
                    	}
                    	if (!data.isNull("peiwan")) {
                    		JSONObject jSitter = data.getJSONObject("peiwan");
                    		sitterDescTv.setText(jSitter.getString("desc"));
                    	}
                    	if (!data.isNull("games")) {
                    		JSONArray games = data.getJSONArray("games");
                    		JSONObject jObj = games.getJSONObject(0);
                    		String img = jObj.isNull("img")?null:jObj.getString("img");
                    		String name = jObj.getString("name");
                    		if (img != null) ImageLoader.getInstance().displayImage(img, gameItemIconIv);
                    		gameItemNameTv.setText(name);
                    		gameItemLl.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(FriendHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {

			}

			@Override
			public void onStart() {

			}
            

        });
    }
}