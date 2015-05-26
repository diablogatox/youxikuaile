package com.orfid.youxikuaile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/2.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;
    private ImageView userPickture;
    private Button saveBtn;
    private View editAvatarRlView, editNicknameRlView, editGenderRlView, editAgeRlView,
        editAreaRlView, editCollegeRlView;
    private TextView ageTv, nicknameTv, genderTv, areaTv, collegeTv;
    private ProgressBar pBar;

    private int gender = 0;
    private long timestamp;
    private String province, city, school;
    private InputStream photoInputStream;

    private static final int PHOTO_PICKER = 0;
    private static final int DATE_PICKER = 1;
    private static final int USER_NICKNAME = 2;
    private static final int USER_GENDER = 3;
    private static final int AREA_PICKER = 4;
    private static final int COLLEGE_PICKER = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        init();
        try {
            doFetchUserInfoAction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {

        backBtn = (ImageButton) findViewById(R.id.btn_back);
        saveBtn = (Button) findViewById(R.id.btn_save);
        userPickture = (ImageView) findViewById(R.id.user_picture);
        editAvatarRlView = findViewById(R.id.rl_edit_avatar);
        editNicknameRlView = findViewById(R.id.rl_edit_nickname);
        editGenderRlView = findViewById(R.id.rl_edit_gender);
        editAgeRlView = findViewById(R.id.rl_edit_age);
        editAreaRlView = findViewById(R.id.rl_edit_area);
        editCollegeRlView = findViewById(R.id.rl_edit_college);
        ageTv = (TextView) findViewById(R.id.tv_age);
        nicknameTv = (TextView) findViewById(R.id.tv_nickname);
        genderTv = (TextView) findViewById(R.id.tv_gender);
        pBar = (ProgressBar) findViewById(R.id.progress_bar);
        areaTv = (TextView) findViewById(R.id.area_tv);
        collegeTv = (TextView) findViewById(R.id.college_tv);

        backBtn.setOnClickListener(this);
        editAvatarRlView.setOnClickListener(this);
        editNicknameRlView.setOnClickListener(this);
        editGenderRlView.setOnClickListener(this);
        editAgeRlView.setOnClickListener(this);
        editAreaRlView.setOnClickListener(this);
        editCollegeRlView.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_edit_avatar:
                Intent i1 = new Intent(this, PhotoPickerActivity.class);
                startActivityForResult(i1, PHOTO_PICKER);
                overridePendingTransition(0, 0);
                break;
            case R.id.rl_edit_nickname:
                Intent i2 = new Intent(this, NicknameActivity.class);
                startActivityForResult(i2, USER_NICKNAME);
                break;
            case R.id.rl_edit_gender:
                Intent i3 = new Intent(this, GenderActivity.class);
                i3.putExtra("gender", gender);
                startActivityForResult(i3, USER_GENDER);
                break;
            case R.id.rl_edit_age:
                Intent i4 = new Intent(this, WheelDatePickerActivity.class);
                startActivityForResult(i4, DATE_PICKER);
                overridePendingTransition(0, 0);
                break;
            case R.id.rl_edit_area:
            	Intent i5 = new Intent(this, WheelAreaPickerActivity.class);
                startActivityForResult(i5, AREA_PICKER);
                overridePendingTransition(0, 0);
            	break;
            case R.id.rl_edit_college:
            	Intent i6 = new Intent(this, SearchCollegeActivity.class);
	            startActivityForResult(i6, COLLEGE_PICKER);
            	break;
            case R.id.btn_save:
                try {
                    doSaveUserInfoAction();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void doSaveUserInfoAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("username", nicknameTv.getText().toString().trim());
        params.put("birthday", timestamp);
        params.put("sex", gender);
        params.put("province", province);
        params.put("city", city);
        params.put("school", school);
        if (photoInputStream != null) params.put("file", photoInputStream, "image_"+System.currentTimeMillis()+".png");
        final ProgressDialog dialog = new ProgressDialog(this);
        HttpRestClient.post("user/SaveInfo", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        Toast.makeText(UserInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        saveBtn.setEnabled(false);
                        JSONObject data = response.getJSONObject("data");
                        String uid = data.getString("uid");
                        String photo = data.getString("photo");
                        dbHandler.updateUser(uid, Constants.KEY_PHOTO, photo);
                    } else if (status == 0) {
                        Toast.makeText(UserInfoActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                if (dialog.isShowing() == false) {
                    dialog.setTitle("请稍等...");
                    dialog.show();
                }
            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void doFetchUserInfoAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        final HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", user.get("uid").toString());
        HttpRestClient.post("user/GetInfo", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        JSONObject data = response.getJSONObject("data");
                        String photoUrl = data.getString("photo");
                        if (photoUrl != null && !photoUrl.equals("")) {
//                            String url = Constants.BASE_URL + photoUrl;
                            ImageLoader.getInstance().displayImage(photoUrl, userPickture);
                        }
                        nicknameTv.setText(data.getString("username"));
                        timestamp = Long.parseLong(data.getString("birthday"));
                        int age = Utils.getAgeByTimestamp(Long.parseLong(data.getString("birthday")) * 1000);
                        ageTv.setText(age==0?"":age+"");
                        gender = Integer.parseInt(data.getString("sex"));
                        genderTv.setText(Utils.showGender(data.getString("sex")));
                        String p = "", c = "", s = "";
                        if (!data.isNull("province_name")) 
                        	p = data.getString("province_name");
                        if (!data.isNull("city_name")) 
                        	c = data.getString("city_name");
                        areaTv.setText(p + " " + c);
                        if (!data.isNull("school_name"))
                        	s = data.getString("school_name");
                        collegeTv.setText(s);

                    } else if (status == 0) {
                        Toast.makeText(UserInfoActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void onFinish() {
				pBar.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				pBar.setVisibility(View.VISIBLE);
			}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PHOTO_PICKER:
                Bitmap photo = data.getExtras().getParcelable("data");
                photoInputStream = Utils.bitmap2InputStream(photo, 100);
                userPickture.setImageBitmap(photo);
                saveBtn.setEnabled(true);
                break;
            case DATE_PICKER:
                ageTv.setText(data.getIntExtra("age", 0)+"");
                timestamp = data.getLongExtra("timestamp", 0);
                saveBtn.setEnabled(true);
                break;
            case USER_NICKNAME:
                nicknameTv.setText(data.getStringExtra("nickname"));
                saveBtn.setEnabled(true);
                break;
            case USER_GENDER:
                if (gender != data.getIntExtra("gender", 0)) saveBtn.setEnabled(true);
                gender = data.getIntExtra("gender", 0);
                String genderText = null;
                if (gender == 1) genderText = "男"; else if (gender == 2) genderText = "女";
                genderTv.setText(genderText);
//                saveBtn.setEnabled(true);
                break;
            case AREA_PICKER:
            	province = data.getStringExtra("provinceId");
            	city = data.getStringExtra("cityId");
            	String area = data.getStringExtra("provinceName") + " " + data.getStringExtra("cityName");
            	areaTv.setText(area);
            	saveBtn.setEnabled(true);
            	break;
            case COLLEGE_PICKER:
            	school = data.getStringExtra("collegeId");
            	String collegeName = data.getStringExtra("collegeName");
            	collegeTv.setText(collegeName);
            	saveBtn.setEnabled(true);
            	break;
        }
    }
}
