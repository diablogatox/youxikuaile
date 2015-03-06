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
    private TextView ageTv, nicknameTv, genderTv;

    private int gender = 0;
    private long timestamp;
    private InputStream photoInputStream;

    private static final int PHOTO_PICKER = 0;
    private static final int DATE_PICKER = 1;
    private static final int USER_NICKNAME = 2;
    private static final int USER_GENDER = 3;

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

        backBtn.setOnClickListener(this);
        editAvatarRlView.setOnClickListener(this);
        editNicknameRlView.setOnClickListener(this);
        editGenderRlView.setOnClickListener(this);
        editAgeRlView.setOnClickListener(this);
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
        if (photoInputStream != null) params.put("file", photoInputStream, "crop.png");
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
        final ProgressDialog dialog = new ProgressDialog(this);
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
                            String url = Constants.BASE_URL + photoUrl;
                            ImageLoader.getInstance().displayImage(url, userPickture);
                        }
                        nicknameTv.setText(data.getString("username"));
                        timestamp = Long.parseLong(data.getString("birthday"));
                        int age = Util.getAgeByTimestamp(Long.parseLong(data.getString("birthday")) * 1000L);
                        ageTv.setText(age==0?"":age+"");
                        gender = Integer.parseInt(data.getString("sex"));
                        genderTv.setText(Util.showGender(data.getString("sex")));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PHOTO_PICKER:
                Bitmap photo = data.getExtras().getParcelable("data");
                photoInputStream = Util.bitmap2InputStream(photo, 100);
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
        }
    }
}
