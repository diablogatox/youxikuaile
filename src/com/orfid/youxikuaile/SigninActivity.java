package com.orfid.youxikuaile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends Activity {

    private EditText username, password;
    private Button loginBtn;
	private Button qqLogin;
	
	private Tencent mTencent;
	private String APP_ID = "1101639686";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signin);
        init();
	}

	public void jumpSignup(View view) {
		startActivity(new Intent(this, SignupActivity.class));
	}
	
	public void jumpRetrievePassword(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("forgetPassword", true);
		startActivity(intent);
	}

    private void init() {
    	
    	mTencent = Tencent.createInstance(APP_ID,
				SigninActivity.this);
    	
        username = (EditText) findViewById(R.id.et_login_username);
        password = (EditText) findViewById(R.id.et_login_password);
        loginBtn = (Button) findViewById(R.id.btn_signin);
        
        qqLogin = (Button) findViewById(R.id.qq_login);

        qqLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				login();
			}
			
		});
        
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = validateInput();
                if (isValid == true) {
                    Log.d("isValid======>", "true");
                    try {
                        doSigninAction();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void login()
	{
		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		if (!mTencent.isSessionValid())
		{
			mTencent.login(this, "all", new BaseUiListener());
		}
	}
    
    private class BaseUiListener implements IUiListener {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void onComplete(Object response) {
				// TODO Auto-generated method stub
	//			doComplete(response);
				Log.d("lalala", "-------------"+response.toString());
			}
	
			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub
				
			}
		
		}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mTencent.onActivityResult(requestCode, resultCode, data);
	}
    
    private boolean validateInput() {
        boolean isValid = true;
        if (username.getText().toString().trim().equals("")) {
            isValid = false;
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().trim().equals("")) {
            isValid = false;
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    private void doSigninAction() throws JSONException {
        RequestParams params = new RequestParams();
        params.put("username", username.getText().toString().trim());
        params.put("password", password.getText().toString().trim());
        final ProgressDialog dialog = new ProgressDialog(this);
        HttpRestClient.post("user/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
                        JSONObject data = response.getJSONObject("data");
                        String name, uid, pwd, token, photo, phone;
                        name = data.getString("username");
                        pwd = password.getText().toString().trim();
                        uid = data.getString("uid");
                        token = response.getString("token");
                        photo = data.getString("photo");
                        phone = username.getText().toString().trim();
                        dbHandler.addUser(name, uid, pwd, token, photo, phone, null, null);
                        Log.d("db_rec_count======>", MainApplication.getInstance().getDbHandler().getRawCount() + "");

                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else if (status == 0) {
                        Toast.makeText(SigninActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                if (dialog.isShowing() == false) {
                    dialog.setTitle("请稍等...");
                    dialog.show();
                }
            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }
}
