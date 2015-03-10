package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
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
import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/10.
 */
public class SelectSpecificActivity extends Activity implements View.OnClickListener {

    private String uid;
    private ImageButton backBtn;
    private View rl_select_specific2;
    private ProgressBar mPbar;
    private TextView mLoadingTv, nameTv;
    private ImageView photoIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_specific);
        init();
        try {
            doSearchUserByUidAction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        backBtn = (ImageButton) findViewById(R.id.btn_back);
        rl_select_specific2 = findViewById(R.id.rl_select_specific2);
        mPbar = (ProgressBar) findViewById(R.id.progress_bar);
        mLoadingTv = (TextView) findViewById(R.id.tv_loading);
        photoIv = (ImageView) findViewById(R.id.iv_ss_pic);
        nameTv = (TextView) findViewById(R.id.tv_ss_name);

        backBtn.setOnClickListener(this);
        rl_select_specific2.setOnClickListener(this);
    }

    private void doSearchUserByUidAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", uid);
        HttpRestClient.post("user/find", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        if (!response.getString("data").equals("[]") && response.getJSONArray("data").length() > 0) {
                            rl_select_specific2.setVisibility(View.VISIBLE);
                            JSONObject jUser = (JSONObject) response.getJSONArray("data").get(0);
                            nameTv.setText(jUser.getString("username"));
                            ImageLoader.getInstance().displayImage(jUser.getString("photo"), photoIv);

                        } else {
                            mLoadingTv.setText("没有相关的用户");
                            mLoadingTv.setVisibility(View.VISIBLE);
                        }
                    } else if (status == 0) {
                        Toast.makeText(SelectSpecificActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                mPbar.setVisibility(View.VISIBLE);
                mLoadingTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                mPbar.setVisibility(View.GONE);
                mLoadingTv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_select_specific2:
//                Intent intent = new Intent(this, UserHomeActivity.class);
//                intent.putExtra("uid", uid);
//                startActivity(intent);
                break;
        }
    }
}