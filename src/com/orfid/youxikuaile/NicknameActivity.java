package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by Administrator on 2015/3/2.
 */
public class NicknameActivity extends Activity {

    private ImageButton backBtn;
    private Button nicknameResetBtn;
    private EditText nicknameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_nickname);
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        nicknameEt = (EditText) findViewById(R.id.et_nickname);
        nicknameResetBtn = (Button) findViewById(R.id.btn_nickname_reset);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nicknameEt.getText().toString().trim().length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("nickname", nicknameEt.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
        nicknameResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nicknameEt.setText("");
            }
        });
    }
}
