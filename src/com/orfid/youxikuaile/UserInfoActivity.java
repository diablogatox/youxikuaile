package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/3/2.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;
    private ImageView userPickture;
    private Button saveBtn;
    private View editAvatarRlView, editNicknameRlView, editGenderRlView, editAgeRlView,
        editAreaRlView, editCollegeRlView;
    private static final int PHOTO_PICKER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        init();
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

        backBtn.setOnClickListener(this);
        editAvatarRlView.setOnClickListener(this);
        editNicknameRlView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_edit_avatar:
                Intent intent = new Intent(this, PhotoPickerActivity.class);
                startActivityForResult(intent, PHOTO_PICKER);
                overridePendingTransition(0, 0);
                break;
            case R.id.rl_edit_nickname:
                startActivity(new Intent(this, NicknameActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PHOTO_PICKER:
                Bitmap photo = data.getExtras().getParcelable("data");
                userPickture.setImageBitmap(photo);
                break;
        }
    }
}
