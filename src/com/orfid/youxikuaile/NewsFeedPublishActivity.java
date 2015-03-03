package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Administrator on 2015/3/3.
 */
public class NewsFeedPublishActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_publish);
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.btn_back);

        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }
}
