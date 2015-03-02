package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015/3/2.
 */
public class GenderActivity extends Activity {

    private ImageButton backBtn;
    private ImageView iv_sexy_choice1;
    private ImageView iv_sexy_choice2;
    private RelativeLayout rl_sexy_boy;
    private RelativeLayout rl_sexy_girl;
    boolean boo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_gender);
        init();
    }

    private void init() {

        backBtn = (ImageButton) findViewById(R.id.btn_back);
        iv_sexy_choice1 = (ImageView) findViewById(R.id.iv_sexy_choice1);
        iv_sexy_choice2 = (ImageView) findViewById(R.id.iv_sexy_choice2);
        rl_sexy_boy = (RelativeLayout) findViewById(R.id.rl_sexy_boy);
        rl_sexy_girl = (RelativeLayout) findViewById(R.id.rl_sexy_girl);

        int gender = 1;
        if (gender == 1) {
            iv_sexy_choice1.setVisibility(View.VISIBLE);
        } else if (gender == 2) {
            iv_sexy_choice2.setVisibility(View.VISIBLE);
        }

        //男
        rl_sexy_boy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                boo = true;
                if (!iv_sexy_choice1.isShown()) {
                    // save change
                    //new SaveUserInfoTask(SexyActivity.this, null, null, boo?"1":"2", null).execute();
                }
                iv_sexy_choice1.setVisibility(View.VISIBLE);
                iv_sexy_choice2.setVisibility(View.GONE);

            }
        });
        //女
        rl_sexy_girl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                boo = false;
                if (!iv_sexy_choice2.isShown()) {
                    // save change
                    //new SaveUserInfoTask(SexyActivity.this, null, null, boo?"1":"2", null).execute();
                }
                iv_sexy_choice1.setVisibility(View.GONE);
                iv_sexy_choice2.setVisibility(View.VISIBLE);
            }
        });
        //返回
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				writeLoginData();
                Intent intent = new Intent();
                int selectedGender = 0;
                if (iv_sexy_choice1.isShown()) {
                    selectedGender = 1;
                } else if (iv_sexy_choice2.isShown()) {
                    selectedGender = 2;
                }
                intent.putExtra("selectedGender", selectedGender);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void stepBack(View view) {
        finish();
    }
}
