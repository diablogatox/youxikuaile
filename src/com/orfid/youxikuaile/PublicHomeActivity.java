package com.orfid.youxikuaile;

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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;


public class PublicHomeActivity extends Activity implements View.OnClickListener {

    private String uid, username, photo;
    private ImageView userPhoto, followActionHintIv, feedItemFileIv;
    private TextView uidTv, publicName, followActionHintTv, distanceInfoTv, 
    		fansCountTv, descTv, feedItemTextTv, activityContentTv;
    private ImageButton backBtn;
    private View followBtnView, feedItemLl, chatBtnView, rl_edit_age, rl_edit_area, event_rl, rl_desc;
    private boolean isFollowed;
    private HashMap user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_home);
        init();
    }

    private void init() {
    	
    	final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        user = dbHandler.getUserDetails();
        
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        isFollowed = intent.getBooleanExtra("isFollowed", false);
        username = intent.getStringExtra("username");
        photo = intent.getStringExtra("photo");
        
        Log.d("uid>>>>>>+++++++", uid);

        userPhoto = (ImageView) findViewById(R.id.user_picture);
        uidTv = (TextView) findViewById(R.id.tv_uid);
        publicName = (TextView) findViewById(R.id.public_name);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        followBtnView = findViewById(R.id.btn_follow);
        followActionHintTv = (TextView) findViewById(R.id.follow_action_hint_tv);
        followActionHintIv = (ImageView) findViewById(R.id.follow_action_hint_iv);
        distanceInfoTv = (TextView) findViewById(R.id.distance_info);
        fansCountTv = (TextView) findViewById(R.id.fans_count_tv);
        descTv = (TextView) findViewById(R.id.tv_desc);
        activityContentTv = (TextView) findViewById(R.id.activity_content_tv);
        
        rl_desc = findViewById(R.id.rl_desc);
        feedItemLl = findViewById(R.id.feed_item_ll);
        feedItemFileIv = (ImageView) findViewById(R.id.feed_item_file_iv);
        feedItemTextTv = (TextView) findViewById(R.id.feed_item_text_tv);
        chatBtnView = findViewById(R.id.btn_chat);
        rl_edit_age = findViewById(R.id.rl_edit_age);
        event_rl = findViewById(R.id.event_rl);

        backBtn.setOnClickListener(this);
        followBtnView.setOnClickListener(this);
        chatBtnView.setOnClickListener(this);
        rl_edit_age.setOnClickListener(this);
        event_rl.setOnClickListener(this);
        rl_desc.setOnClickListener(this);

        uidTv.setText(uid);
        if (username != null && !username.equals("null")) publicName.setText(username);
        if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, userPhoto);
        if (isFollowed == true) {
        	followActionHintTv.setText("取消关注");
        	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_unfollow_selector));
        } else {
        	followActionHintTv.setText("关注");
        	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_follow_selector));
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
            	String users = "[{\"uid\":\""+uid+"\",\"username\":\""+username+"\",\"photo\":\""+photo+"\"},"
            			+ "{\"uid\":\""+user.get("uid").toString()+"\",\"username\":\""+user.get("username").toString()+"\",\"photo\":\""+user.get("photo").toString()+"\"}]";
            	Log.d("users===========>", users);
            	
            	Intent i = new Intent(PublicHomeActivity.this, ChattingActivity.class);
            	i.putExtra("uid", uid);
            	i.putExtra("users", users);
            	startActivity(i);
            	break;
            	
            case R.id.rl_edit_age:
            	Intent intent = new Intent(this, NewsFeedActivity.class);
            	intent.putExtra("isPublicAccount", true);
            	intent.putExtra("uid", uid);
            	startActivity(intent);
//            	finish();
            	break;
            	
            case R.id.event_rl:
            	
            	Intent intent2 = new Intent(this, EventListActivity.class);
            	intent2.putExtra("uid", uid);
            	intent2.putExtra("username", username);
            	startActivity(intent2);
            	
            	break;
            case R.id.rl_desc:
            	
            	Intent intent3 = new Intent(this, UserDescActivity.class);
            	intent3.putExtra("desc", descTv.getText().toString());
            	startActivity(intent3);
            	
            	break;
        }
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
                    	Toast.makeText(PublicHomeActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(PublicHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
			}

			@Override
			public void onStart() {
				followBtnView.setClickable(false);
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
                    	Toast.makeText(PublicHomeActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(PublicHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
			}

			@Override
			public void onStart() {
				followBtnView.setClickable(false);
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
                    	
                    	if (!data.isNull("huodong") && data.getString("huodong") != null) {
                    		JSONObject jActivity = data.getJSONObject("huodong");
                    		activityContentTv.setText(jActivity.getString("title"));
                    	}
//                    	isFollowed = data.getBoolean("isFollow");
//                    	String school = user.isNull("school")?"":user.getString("school");
//                    	collegeTv.setText(school);
//                    	String birthday = user.has("birthday") && user.isNull("birthday")?null:user.getString("birthday");
//                    	Log.d("birthday=====>", birthday);
//                    	if (birthday != null) {
//                    		String age = Utils.getAge(Long.parseLong(birthday) * 1000)+"";
//                    		gendernageTv.setText(age);
//                    	}
//                    	int sex;
//                    	sex = user.isNull("sex")?2:user.getInt("sex");
//                    	if (sex == 1) {
//                    		gendernageTv.setCompoundDrawablesWithIntrinsicBounds( R.drawable.icon_man, 0, 0, 0);
//                    	}
                    	fansCountTv.setText(user.getString("fans"));
                    	descTv.setText(user.getString("desc"));
                    	
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
                    	
//                    	if (!data.isNull("peiwan")) {
//                    		JSONObject jSitter = data.getJSONObject("peiwan");
//                    		sitterDescTv.setText(jSitter.getString("desc"));
//                    	}
//                    	if (!data.isNull("games")) {
//                    		JSONArray games = data.getJSONArray("games");
//                    		JSONObject jObj = games.getJSONObject(0);
//                    		String img = jObj.isNull("img")?null:jObj.getString("img");
//                    		String name = jObj.getString("name");
//                    		if (img != null) ImageLoader.getInstance().displayImage(img, gameItemIconIv);
//                    		gameItemNameTv.setText(name);
//                    		gameItemLl.setVisibility(View.VISIBLE);
//                    	}
                    } else if (status == 0) {
                        Toast.makeText(PublicHomeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}