package com.orfid.youxikuaile;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NewsFeedForwardActivity extends Activity implements OnClickListener {

	private TextView originalContentTv;
	private ImageButton backBtn;
	private ProgressDialog pDialog;
	private EditText contentEt;
	private Button saveBtn;
	private long feedId;
	private int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed_forward);
		initView();
		setListener();
		obtainData();
	}

	private void obtainData() {
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		position = intent.getIntExtra("position", 0);
		feedId = intent.getLongExtra("feedId", 0);
		
		originalContentTv.setText(content);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	private void initView() {
		originalContentTv = (TextView) findViewById(R.id.original_content_tv);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		contentEt = (EditText) findViewById(R.id.content_et);
		saveBtn = (Button) findViewById(R.id.save_btn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.save_btn:
				if (contentEt.getText().toString().trim().equals("")) {
					Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
				} else {
					try {
						doFeedPublishAction();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
		}
	}
	
	private void doFeedPublishAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", feedId);
        params.put("type", 1);
        params.put("text", contentEt.getText().toString().trim());
        pDialog = new ProgressDialog(this);
        HttpRestClient.post("feed/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	Intent intent = new Intent();
                    	intent.putExtra("position", position);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedForwardActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                if (pDialog.isShowing() == false) {
                    pDialog.setTitle("请稍等...");
                    pDialog.show();
                }
            }

            @Override
            public void onFinish() {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
            }

        });
    }

}
