package com.orfid.youxikuaile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/3/3.
 */
public class NewsFeedPublishActivity extends Activity implements View.OnClickListener {

    private ImageButton backBtn;
    private Button saveBtn;
    private EditText contentEt;
    private TextView mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_publish);
        init();
    }

    private void init() {
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        contentEt = (EditText) findViewById(R.id.et_content);
        mCount = (TextView) findViewById(R.id.newsfeedpublish_count);
        saveBtn = (Button) findViewById(R.id.btn_save);

        backBtn.setOnClickListener(this);
        contentEt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.length();
                mCount.setText(String.valueOf(number));
                selectionStart = contentEt.getSelectionStart();
                selectionEnd = mCount.getSelectionEnd();
                if (temp.length() > 140) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    contentEt.setText(s);
                    contentEt.setSelection(tempSelection);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentEt.getText().toString().trim().length() == 0) {
                    Toast.makeText(NewsFeedPublishActivity.this, "内容不能为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    try {
                        doFeedPublishAction();
                        setResult(RESULT_OK, null);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (contentEt.getText().toString().trim().length() > 0) {
                    backDialog();
                } else {
                    finish();
                }
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (contentEt.getText().toString().trim().length() > 0) {
                backDialog();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("您编辑的内容将被清空");
        builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder .create().show();
    }

    private void doFeedPublishAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", 0);
        params.put("type", 0);
        params.put("text", contentEt.getText().toString().trim());
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);
        HttpRestClient.post("feed/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                        DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
//                        JSONObject data = response.getJSONObject("data");
//                        String name, uid, pwd, token, photo, phone;
//                        name = data.getString("username");
//                        pwd = password.getText().toString().trim();
//                        uid = data.getString("uid");
//                        token = response.getString("token");
//                        photo = data.getString("photo");
//                        phone = username.getText().toString().trim();
//                        dbHandler.addUser(name, uid, pwd, token, photo, phone);
//                        Log.d("db_rec_count======>", MainApplication.getInstance().getDbHandler().getRawCount() + "");
//
//                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();

                    } else if (status == 0) {
                        Toast.makeText(NewsFeedPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
