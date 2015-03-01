package com.orfid.youxikuaile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SignupActivity extends Activity {

    private EditText phoneEt, captchaEt, passwordEt, passwordConfirmEt;
    private TextView titleTv;
    private Button captchaBtn, signupBtn;
    private View termll;
    private boolean forgetPassword = false;
    private CountDownTimer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signup);
        obtainExtra();
        init();
	}

    private void obtainExtra() {
       forgetPassword = getIntent().getBooleanExtra("forgetPassword", false);
    }

    public void stepBack(View view) {
		finish();
	}

    private void init() {
        titleTv = (TextView) findViewById(R.id.tv_title);
        termll = findViewById(R.id.ll_terms);
        phoneEt = (EditText) findViewById(R.id.et_signup_phone);
        captchaEt = (EditText) findViewById(R.id.et_signup_captcha);
        passwordEt = (EditText) findViewById(R.id.et_signup_password);
        passwordConfirmEt = (EditText) findViewById(R.id.et_signup_password_confirm);
        captchaBtn = (Button) findViewById(R.id.btn_get_captcha);
        signupBtn = (Button) findViewById(R.id.btn_signup);

        if (forgetPassword) {
            titleTv.setText("找回密码");
            termll.setVisibility(View.GONE);
            signupBtn.setText("完成");
            passwordEt.setHint("请输入新密码");
            passwordConfirmEt.setHint("请输入新密码");
        }

        captchaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                boolean isValid = validateInput(phoneEt);
                if (isValid == true) {

                    // 获取验证码
                    try {
                        doGetCaptchaAction();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    v.setClickable(false);
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_verify2));

                    timer = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            ((TextView) v).setText("重新发送" + '(' + millisUntilFinished / 1000 + ')');
                        }

                        @Override
                        public void onFinish() {
                            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_verify1));
                            ((TextView) v).setText("重新发送");
                            v.setClickable(true);
                        }
                    }.start();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = validateInput(phoneEt, captchaEt, passwordEt, passwordConfirmEt);
                if (isValid == true) {
                    if (validateInput(passwordEt, passwordConfirmEt) == true) {// 两次密码输入是否匹配
                        // 注册
                        try {
                            if (forgetPassword) {
//                                doRetrievePasswordAction();
                            } else {
                                doSignupAction();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private boolean validateInput(EditText... views) {
        boolean isValid = true;
        if (views.length > 0) {
            if (views.length == 1) {
                // 手机号码
                if (views[0].getText().toString().trim().equals("")) {
                    Toast.makeText(SignupActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else if (Util.isMobile(views[0].getText().toString().trim()) == false) {
                    Toast.makeText(SignupActivity.this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            } else {
                int length = views.length;
                if (length == 2) {
                    if (views[0].getText().toString().trim().equals(views[1].getText().toString().trim()) == false) {
                        Toast.makeText(SignupActivity.this, "密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
                        views[0].setText("");
                        views[1].setText("");
                        isValid = false;
                    }
                } else if (length == 4) {
                    // 手机号码
                    if (views[0].getText().toString().trim().equals("")) {
                        Toast.makeText(SignupActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    } else if (views[1].getText().toString().trim().equals("")) {
                        Toast.makeText(SignupActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    } else if (views[2].getText().toString().trim().equals("")) {
                        Toast.makeText(SignupActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    } else if (views[3].getText().toString().trim().equals("")) {
                        Toast.makeText(SignupActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }

                }
            }
        }

        return isValid;
    }

    private void doGetCaptchaAction() throws JSONException {
        RequestParams params = new RequestParams();
        params.put("mobile", phoneEt.getText().toString().trim());
        params.put("act", Constants.ACT_SIGNUP);
        HttpRestClient.post("sms/sendCode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                  int status = response.getInt("status");
                  if (status == 0) {
                        Toast.makeText(SignupActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                  }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doSignupAction() throws JSONException {
        RequestParams params = new RequestParams();
        params.put("mobile", phoneEt.getText().toString().trim());
        params.put("password", passwordEt.getText().toString().trim());
        params.put("password_confirm", passwordConfirmEt.getText().toString().trim());
        params.put("scode", captchaEt.getText().toString().trim());
        final ProgressDialog dialog = new ProgressDialog(this);
        HttpRestClient.post("user/register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success

                    } else if (status == 0) {
                        Toast.makeText(SignupActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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

    private void doRetrievePasswordAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("mobile", phoneEt.getText().toString().trim());
        params.put("newPassword", passwordEt.getText().toString().trim());
        params.put("smscode", captchaEt.getText().toString().trim());
//        params.put("token", user.get("token").toString());
        final ProgressDialog dialog = new ProgressDialog(this);
        HttpRestClient.post("user/findPassword", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success

                    } else if (status == 0) {
                        Toast.makeText(SignupActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) timer.cancel();
    }
}
