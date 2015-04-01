package com.orfid.youxikuaile;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.parser.GameSitterItemsParser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class OfflineSitterPublishActivity extends Activity implements OnClickListener {

	private EditText sitterDescEt;
	private ImageButton backBtn;
	private Button saveBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offline_sitter_publish);
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		sitterDescEt = (EditText) findViewById(R.id.sitter_desc_et);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		saveBtn = (Button) findViewById(R.id.save_btn);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
	}

	private void obtainData() {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.save_btn:
			if (sitterDescEt.getText().toString().length() <= 0) {
				Toast.makeText(this, "请先填写陪玩介绍", Toast.LENGTH_SHORT).show();
			} else {
				try {
					doPublishOfflineSitterAction();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	private void doPublishOfflineSitterAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("online", 0);
        params.put("desc", sitterDescEt.getText().toString().trim());
        HttpRestClient.post("peiwan/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success           	
                    	
                    } else if (status == 0) {
                        Toast.makeText(OfflineSitterPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
