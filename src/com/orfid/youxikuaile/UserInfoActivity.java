package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Administrator on 2015/3/2.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;
    private Button saveBtn;
    private View editAvatarRlView, editNicknameRlView, editGenderRlView, editAgeRlView,
        editAreaRlView, editCollegeRlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        saveBtn = (Button) findViewById(R.id.btn_save);
        editAvatarRlView = findViewById(R.id.rl_edit_avatar);
        editNicknameRlView = findViewById(R.id.rl_edit_nickname);
        editGenderRlView = findViewById(R.id.rl_edit_gender);
        editAgeRlView = findViewById(R.id.rl_edit_age);
        editAreaRlView = findViewById(R.id.rl_edit_area);
        editCollegeRlView = findViewById(R.id.rl_edit_college);

        backBtn.setOnClickListener(this);
        editAvatarRlView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_edit_avatar:
                Intent intent = new Intent(this, PhotoPickerActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);
                break;
        }
    }
}
