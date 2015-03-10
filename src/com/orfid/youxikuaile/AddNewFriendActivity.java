package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddNewFriendActivity extends Activity implements View.OnClickListener {

    private EditText searchUidEt;
    private ImageButton backBtn;
    private View searchBtnllView;
    private  String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_new_friends);
        init();
	}

    private void init() {
        searchUidEt = (EditText) findViewById(R.id.et_add_new_friends);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        searchBtnllView = findViewById(R.id.ll_find_user_id);

        backBtn.setOnClickListener(this);
        searchBtnllView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_find_user_id:
               uid = searchUidEt.getText().toString().trim();
                Log.d("searched user id======>", uid);
                if(uid.equals("")||uid==null){
                    Toast.makeText(this, "请输入用户id", Toast.LENGTH_SHORT).show();
                    searchUidEt.requestFocus();
                    return;
                }else{
//                    try {
//                        doSearchUserByUidAction();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    Intent intent = new Intent(this, SelectSpecificActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
                break;
        }
    }

}
