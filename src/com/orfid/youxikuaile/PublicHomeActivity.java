package com.orfid.youxikuaile;

import java.util.HashMap;

import org.apache.http.Header;
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

    private String uid;
    private ImageView userPhoto, followActionHintIv;
    private TextView uidTv, publicName, followActionHintTv;
    private ImageButton backBtn;
    private View followBtnView;
    private boolean isFollowed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_home);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        isFollowed = intent.getBooleanExtra("isFollowed", false);
        String username = intent.getStringExtra("username");
        String photo = intent.getStringExtra("photo");
        
        Log.d("uid>>>>>>+++++++", uid);

        userPhoto = (ImageView) findViewById(R.id.user_picture);
        uidTv = (TextView) findViewById(R.id.tv_uid);
        publicName = (TextView) findViewById(R.id.public_name);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        followBtnView = findViewById(R.id.btn_follow);
        followActionHintTv = (TextView) findViewById(R.id.follow_action_hint_tv);
        followActionHintIv = (ImageView) findViewById(R.id.follow_action_hint_iv);

        backBtn.setOnClickListener(this);
        followBtnView.setOnClickListener(this);

        uidTv.setText(uid);
        if (username != null && !username.equals("null")) publicName.setText(username);
        if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, userPhoto);
        if (isFollowed == true) {
        	followActionHintTv.setText("取消关注");
        	followActionHintIv.setBackgroundDrawable(getResources().getDrawable(R.drawable.iv_unfollow_selector));
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
}