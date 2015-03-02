package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Administrator on 2015/3/2.
 */
public class NicknameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_nickname);
    }

    public void stepBack(View view) {
        finish();
    }
}
