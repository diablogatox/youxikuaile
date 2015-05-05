package com.orfid.youxikuaile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.adapter.RecordeAdapter;
import com.orfid.youxikuaile.model.HttpRequstModel;
import com.orfid.youxikuaile.model.LogInfo;
import com.orfid.youxikuaile.model.User;

/**
 * 此activity,是用户充值activity,在个人账户的个人中心界面，点击headerview上的充值button后，跳转至此activity
 * 主要由一个四个button组成，以及一个四个线性布局组成，这个本身应可以用fragment来写，但是没有采用，后续你有时间可以将其改成fragment。
 * 第一根button 是在线充值。可以输入数字 在edittext 里，点击确定，调用，第三方支付接口。即是 支付宝无限快捷支付 ，进行支付。
 * 第二个button。是购买vip..一个月100金币。调用buyVipURl接口购买。
 * 第三个button，是查看在线付钱购买金币的记录，通过调用recordeUrl 接口获得。
 * 第四个button。是金币购买积分，通过输入数据，调用changeUrl接口，完成交易。 ，*
 * 
 * @author clh
 * 
 */
public class RechageActivity extends Activity implements OnClickListener {
	private final String accountUrl = "http://api.yxkuaile.com/useraccount";
	private final String recordeUrl = "http://api.yxkuaile.com/useraccount/logs";
	private final String changeUrl = "http://api.yxkuaile.com/exchange/goldToIntegral";
	// private final String alipayUrl =
	// "http://api.yxkuaile.com/useraccount/recharge";
	private final String buyVipURl = "http://api.yxkuaile.com/useraccount/vip";
	private Button czOnline, recorde, changeJf, vipBtn, backBtn;
	private LinearLayout czLayout;
	private TextView gdTextNum;
	private LayoutInflater czInflater;
	private EditText numberMoney, exchange_score_num;
	private Button okCommit, exchange_ok_btn;
	private View record_heading;
	private int page = 1;
	// tag ==1 付款，==2vip，==3 支付宝记录，==4兑换
	private int tag = 1;
	private boolean lastPage;
	private ProgressBar pb;
	private String num, out_trade_no, month, trade_no;
	private ListView recordeList;
	private TextView empty_hint_tv;
	private RecordeAdapter reAdapter;
	private Dialog builder;
	private User userData;
	private HttpRequstModel requstModel;
	private String score = "0", gold = "0";
	private View changeLayout;
	private List<LogInfo> recordeInfoList,
			allLogInfos = new ArrayList<LogInfo>();
	private Handler czHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.RQF_PAY:
				Result result = new Result((String) msg.obj);
				// Result.mResult = (String) msg.obj;

				int mm = msg.arg1;
				Toast.makeText(RechageActivity.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
				String[] flag = result.parseResult();
				String s = flag[0].trim();

				JSONObject jsn = string2JSON(s, "&");
				try {
					trade_no = jsn.getString("trade_no");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String f = flag[2].trim();
				String n = flag[3].trim();

				if (f.equals("true") && n.equals("9000")) {
					// 回调地址
					// getData();
					czHandler.sendEmptyMessageDelayed(6, 500);

				}
				break;

			case 1:
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
//							ApplicationData.token = json.getString("token");
						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										RechageActivity.this,
										json.getJSONObject("error").getString(
												"info"), Toast.LENGTH_LONG)
										.show();
							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
//			case 2:
//				JSONObject json = (JSONObject) msg.obj;
//				try {
//					if (json.has("status")) {
//						if (json.getInt("status") == 1) {
//							ApplicationData.token = json.getString("token");
//							Toast.makeText(
//									RechageActivity.this,
//									getResources()
//											.getString(
//													R.string.hayou_center_open_vip_account)
//											+ month
//											+ getResources()
//													.getString(
//															R.string.hayou_center_open_vip_account_1),
//									Toast.LENGTH_LONG).show();
//							numberMoney.setText("1");
//						} else if (json.getInt("status") == 0) {
//							if (json.has("error")) {
//								Toast.makeText(
//										RechageActivity.this,
//										json.getJSONObject("error").getString(
//												"info"), Toast.LENGTH_LONG)
//										.show();
//							}
//
//						}
//
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
			case 3:
				if (pb != null) {
					pb.setVisibility(View.GONE);
				}
				if (recordeList != null) {
					recordeList.setVisibility(View.VISIBLE);
				}

				try {
					JSONObject js = (JSONObject) msg.obj;
					if (js.has("status")) {
						if (js.getInt("status") == 1) {
//							ApplicationData.token = js.getString("token");
							JSONArray jsArray = js.getJSONArray("logs");
							Type listType = new TypeToken<List<LogInfo>>() {
							}.getType();
							Gson gson = new Gson();
							recordeInfoList = gson.fromJson(jsArray.toString(),
									listType);
							lastPage = js.getBoolean("lastPage");
							if (recordeInfoList != null && allLogInfos != null) {
								if (recordeInfoList.size() > 0) {
									for (LogInfo item : recordeInfoList) {
										allLogInfos.add(item);
									}
									setData();
								}
							}

						} else if (js.getInt("status") == 0) {
							if (js.has("error")) {
								Toast.makeText(
										RechageActivity.this,
										js.getJSONObject("error").getString(
												"info"), Toast.LENGTH_LONG)
										.show();
							}

						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			
			case 6:
				getAccountData();
				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		if (ApplicationData.getInstance().getUid() == null
//				|| (Integer) ApplicationData.getInstance().getIdentity() == null) {
//			SharedPreferences userInfo = getSharedPreferences("user_info", 0);
//			String uId = userInfo.getString("uid", "");
//			String identity = userInfo.getString("identity", "");
//			ApplicationData.identity = identity;
//			ApplicationData.uid = uId;
//
//			ApplicationData.getInstance().setUid(ApplicationData.uid);
//
//			ApplicationData.getInstance().setIdentity(
//					Integer.parseInt(ApplicationData.identity));
//		}
		
		setContentView(R.layout.center_chongzhi);
//		if (savedInstanceState != null) {
//			userData = savedInstanceState.getParcelable("userData");
//			if (userData != null) {
//				ApplicationData.setInstance(userData);
//			}
//		}
//
//		ApplicationData.getApplicatinInstance().addActivity(this);
		czInflater = LayoutInflater.from(this);
		initView();
		
		getAccountData();

	}

	private void initView() {
		// TODO Auto-generated method stub
		backBtn = (Button) findViewById(R.id.alipay_back_button);
		czOnline = (Button) findViewById(R.id.hayou_chongzhi_online_button);
		recorde = (Button) findViewById(R.id.hayou_chongzhi_recorde_button);
		changeJf = (Button) findViewById(R.id.hayou_chongzhi_change_button);
//		vipBtn = (Button) findViewById(R.id.hayou_chongzhi_vip_button);
		czLayout = (LinearLayout) findViewById(R.id.hayou_chongzhi_layout);
		czOnline.setOnClickListener(this);
		recorde.setOnClickListener(this);
		changeJf.setOnClickListener(this);
//		vipBtn.setOnClickListener(this);
		View onlineLayout = czInflater.inflate(
				R.layout.center_chongzhi_online_linearlayout, null);
		numberMoney = (EditText) onlineLayout
				.findViewById(R.id.hayou_chongzhi_money_num);
		TextView goldText = (TextView) onlineLayout
				.findViewById(R.id.user_recharge_text);
		gdTextNum = (TextView) onlineLayout
				.findViewById(R.id.user_recharge_text_num);
		okCommit = (Button) onlineLayout
				.findViewById(R.id.hayou_chongzhi_ok_button);
		TextView sailText = (TextView) onlineLayout
				.findViewById(R.id.hayou_center_chongzhi_jinbi_text);
		TextView sailerText = (TextView) onlineLayout
				.findViewById(R.id.hayou_center_chongzhi_what);
		sailText.setText(getResources().getString(
				R.string.hayou_center_chongzhi_online_text));
		sailerText.setText(getResources().getString(
				R.string.hayou_center_chongzhi_online_tip));
		LinearLayout tipLayout = (LinearLayout) onlineLayout
				.findViewById(R.id.hayou_chongzhi_tips_layout);
		tipLayout.setVisibility(View.VISIBLE);
		goldText.setText(getResources().getString(
				R.string.hayou_center_chongzhi_online_gold_left));
		gdTextNum.setText(gold);
		numberMoney.setText("");
		czLayout.addView(onlineLayout);
//		onlineLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
//				android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
//				android.widget.LinearLayout.LayoutParams.MATCH_PARENT));
		okCommit.setOnClickListener(this);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RechageActivity.this.finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hayou_chongzhi_online_button:
			czOnline.setBackgroundResource(R.drawable.background_tab_line2);
			recorde.setBackgroundResource(R.drawable.center_chongzhi_1);
			changeJf.setBackgroundResource(R.drawable.center_chongzhi_1);
//			vipBtn.setBackgroundResource(R.drawable.center_chongzhi_1);
			czOnline.setTextColor(getResources().getColor(R.color.tab_selected));
			recorde.setTextColor(0xFF333333);
			changeJf.setTextColor(0xFF333333);
//			vipBtn.setTextColor(0xFF333333);
			czLayout.removeAllViews();
			tag = 1;

			View onlineLayout = czInflater.inflate(
					R.layout.center_chongzhi_online_linearlayout, null);
			numberMoney = (EditText) onlineLayout
					.findViewById(R.id.hayou_chongzhi_money_num);
			okCommit = (Button) onlineLayout
					.findViewById(R.id.hayou_chongzhi_ok_button);
			TextView goldText = (TextView) onlineLayout
					.findViewById(R.id.user_recharge_text);
			gdTextNum = (TextView) onlineLayout
					.findViewById(R.id.user_recharge_text_num);
			TextView sailText = (TextView) onlineLayout
					.findViewById(R.id.hayou_center_chongzhi_jinbi_text);
			TextView sailerText = (TextView) onlineLayout
					.findViewById(R.id.hayou_center_chongzhi_what);
			sailText.setText(getResources().getString(
					R.string.hayou_center_chongzhi_online_text));
			sailerText.setText(getResources().getString(
					R.string.hayou_center_chongzhi_online_tip));
			LinearLayout tipLayout = (LinearLayout) onlineLayout
					.findViewById(R.id.hayou_chongzhi_tips_layout);
			tipLayout.setVisibility(View.VISIBLE);
			goldText.setText(getResources().getString(
					R.string.hayou_center_chongzhi_online_gold_left));
			// goldTextNum.setText(goldStr);
			gdTextNum.setText(gold);
			numberMoney.setText("");
			okCommit.setOnClickListener(this);
			czLayout.addView(onlineLayout);
			break;

		case R.id.hayou_chongzhi_recorde_button:
			czOnline.setBackgroundResource(R.drawable.center_chongzhi_1);
			recorde.setBackgroundResource(R.drawable.background_tab_line2);
			changeJf.setBackgroundResource(R.drawable.center_chongzhi_1);
//			vipBtn.setBackgroundResource(R.drawable.center_chongzhi_1);
			czOnline.setTextColor(0xFF333333);
			recorde.setTextColor(getResources().getColor(R.color.tab_selected));
			changeJf.setTextColor(0xFF333333);
//			vipBtn.setTextColor(0xFF333333);
			czLayout.removeAllViews();
			if (allLogInfos != null) {
				allLogInfos.clear();
			} else {
				allLogInfos = new ArrayList<LogInfo>();
			}
			View recordeLayout = czInflater.inflate(
					R.layout.center_chongzhi_recode_linearlayout, null);
			View recordeTopLayout = czInflater
					.inflate(
							R.layout.center_chongzhi_recode_linearlayout_item_top,
							null);
			pb = (ProgressBar) recordeLayout
					.findViewById(R.id.chongzhi_recorde_layout_progress1);
			recordeList = (ListView) recordeLayout
					.findViewById(R.id.hayou_chongzhi_recorde_list_view);
			empty_hint_tv = (TextView) recordeLayout
					.findViewById(R.id.empty_hint_tv);

			recordeList.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			recordeList.addHeaderView(recordeTopLayout);
			tag = 3;
			getData();
			czLayout.addView(recordeLayout);
			break;
		case R.id.hayou_chongzhi_change_button:
			Log.d("entered======>", ">>>>>>>");
			czOnline.setBackgroundResource(R.drawable.center_chongzhi_1);
			recorde.setBackgroundResource(R.drawable.center_chongzhi_1);
			changeJf.setBackgroundResource(R.drawable.background_tab_line2);
//			vipBtn.setBackgroundResource(R.drawable.center_chongzhi_1);
			czOnline.setTextColor(0xFF333333);
			recorde.setTextColor(0xFF333333);
			changeJf.setTextColor(getResources().getColor(R.color.tab_selected));
//			vipBtn.setTextColor(0xFF333333);
			czLayout.removeAllViews();
			tag = 4;
			changeLayout = czInflater.inflate(
					R.layout.center_exchange_linearlayout, null);
			TextView inteText = (TextView) changeLayout
					.findViewById(R.id.user_recharge_text);
			gdTextNum = (TextView) changeLayout
					.findViewById(R.id.user_recharge_text_num);
			exchange_score_num = (EditText) changeLayout
					.findViewById(R.id.exchange_score_num);
//			okCommit = (Button) changeLayout
//					.findViewById(R.id.hayou_chongzhi_ok_button);
			TextView sailText_change = (TextView) changeLayout
					.findViewById(R.id.hayou_center_chongzhi_jinbi_text);
			TextView sailerText_change = (TextView) changeLayout
					.findViewById(R.id.hayou_center_chongzhi_what);
			sailText_change.setText(getResources().getString(
					R.string.hayou_center_mygift_sail_price_jinbi));
			sailerText_change.setText(getResources().getString(
					R.string.hayou_center_chongzhi_change_tip));
			LinearLayout tipLayout_change = (LinearLayout) changeLayout
					.findViewById(R.id.hayou_chongzhi_tips_layout);
			tipLayout_change.setVisibility(View.GONE);
			inteText.setText(getResources().getString(
					R.string.hayou_center_chongzhi_online_integral_left));
			
			exchange_ok_btn = (Button) changeLayout.findViewById(R.id.exchange_ok_button);
			// inteTextNum.setText(scoreStr);
			gdTextNum.setText(score);
			exchange_score_num.setText("");
			exchange_ok_btn.setOnClickListener(this);
			czLayout.addView(changeLayout);
			break;
		case R.id.hayou_chongzhi_ok_button:
			num = "" + numberMoney.getText().toString();
			num = num.trim();
//			Pattern pattern_num = Pattern.compile("^\\+?[1-9][0-9]*$",
//					Pattern.CASE_INSENSITIVE);
//			Matcher matcher_num = pattern_num.matcher(num);
//			if (ApplicationData.isLogi) {
//				if (tag == 1) {
//					if (matcher_num.matches()) {
				if (numberMoney.getText().toString().trim().length() > 0) {
						OnlinePay pay = new OnlinePay();
						pay.payMoney(RechageActivity.this, num, czHandler,
								getOutTradeNo());
				} else {
					Toast.makeText(this, "请先填入充值金额", Toast.LENGTH_SHORT).show();
				}
//					} else {
//						Toast.makeText(
//								getApplicationContext(),
//								getResources().getString(R.string.number_error),
//								Toast.LENGTH_SHORT).show();
//
//					}

//				} else {
//					if (matcher_num.matches()) {
//						getData();
//					}
//				}
//			}

			break;
//		case R.id.hayou_chongzhi_vip_button:
//			czOnline.setBackgroundResource(R.drawable.center_chongzhi_1);
//			recorde.setBackgroundResource(R.drawable.center_chongzhi_1);
//			changeJf.setBackgroundResource(R.drawable.center_chongzhi_1);
//			vipBtn.setBackgroundResource(R.drawable.center_chongzhi_2);
//			czOnline.setTextColor(0xFF333333);
//			recorde.setTextColor(0xFF333333);
//			changeJf.setTextColor(0xFF333333);
//			vipBtn.setTextColor(0xFFFFFFFF);
//			czLayout.removeAllViews();
//			tag = 2;
//			View vipLayout = czInflater.inflate(
//					R.layout.center_chongzhi_vip_linearlayout, null);
//			final EditText moneyNum = (EditText) vipLayout
//					.findViewById(R.id.hayou_chongzhi_vip_num);
//			final TextView payNum = (TextView) vipLayout
//					.findViewById(R.id.hayou_chongzhi_vip_pay_sum_num);
//			final TextView flowerNum = (TextView) vipLayout
//					.findViewById(R.id.hayou_chongzhi_vip_flower_num);
//			final TextView sweetNum = (TextView) vipLayout
//					.findViewById(R.id.hayou_chongzhi_vip_sweet_num);
//			Button sureBtn = (Button) vipLayout
//					.findViewById(R.id.hayou_chongzhi_vip_button);
//			month = moneyNum.getText().toString();
//			moneyNum.addTextChangedListener(new TextWatcher() {
//
//				@Override
//				public void afterTextChanged(Editable s) {
//
//				}
//
//				@Override
//				public void beforeTextChanged(CharSequence s, int start,
//						int count, int after) {
//
//				}
//
//				@Override
//				public void onTextChanged(CharSequence s, int start,
//						int before, int count) {
//					String str = moneyNum.getText().toString();
//					month = moneyNum.getText().toString();
//					try {
//
//						int num = Integer.parseInt(str);
//						payNum.setText(String.valueOf(num * 100));
//						flowerNum.setText(String.valueOf(num * 5));
//						sweetNum.setText(String.valueOf(num * 5));
//					} catch (Exception e) {
//						// TODO: handle exception
//
//					}
//				}
//
//			});
//			sureBtn.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (month != null) {
//						if (!month.equals("")) {
//							getData();
//						} else {
//							Toast.makeText(RechageActivity.this, "请填入购买数字",
//									Toast.LENGTH_LONG).show();
//						}
//					}
//
//				}
//			});
//			czLayout.addView(vipLayout);
//			break;
		case R.id.exchange_ok_button:

			if (exchange_score_num.getText().toString().trim().length() > 0) {
				try {
					doExchangeGoldToScoreAction();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this, "请先填入金币数", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
    HashMap user = dbHandler.getUserDetails();

	private void getData() {
		// TODO Auto-generated method stub
		if (requstModel == null) {
			requstModel = new HttpRequstModel();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	        
		params.add(new BasicNameValuePair("token", user.get("token").toString()));
//		params.add(new BasicNameValuePair("uid", user.get("uid").toString()));
		if (tag == 1) {
			params.add(new BasicNameValuePair("trade_no", trade_no));
			params.add(new BasicNameValuePair("amount", num));
			// requstModel.setRqquestHandler(RechageActivity.this, czHandler,
			// params, tag, alipayUrl);
		} else if (tag == 2) {
			params.add(new BasicNameValuePair("amount", String.valueOf(Integer
					.parseInt(month) * 100)));
			params.add(new BasicNameValuePair("month", month));
			System.out.println("params--->" + params);
			requstModel.setRqquestHandler(RechageActivity.this, czHandler,
					params, tag, buyVipURl);
		} else if (tag == 3) {
//			params.add(new BasicNameValuePair("type", "0"));
//			params.add(new BasicNameValuePair("page", String.valueOf(page)));
//			requstModel.setRqquestHandler(RechageActivity.this, czHandler,
//					params, tag, recordeUrl);
			
			try {
				doFetchLogsAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		} else if (tag == 4) {
			if (num != null) {
				if (!num.equals("")) {
					params.add(new BasicNameValuePair("amount", num));
					requstModel.setRqquestHandler(RechageActivity.this,
							czHandler, params, tag, changeUrl);
				}
			}

		}

	}

	private String getOutTradeNo() {
		long longTime = System.currentTimeMillis();
		// 取从a到b的随机数就是（int）（（b-a)*Math.random()＋a）
		int ran = (int) (9900 * Math.random() + 100);

		return String.valueOf((long) (longTime / 1000)) + String.valueOf(ran);
	}

	private void setData() {
		// recordeList.setVisibility(View.GONE);

		if (pb != null) {
			pb.setVisibility(View.GONE);
		}
		if (recordeList != null) {
			recordeList.setVisibility(View.VISIBLE);
		}
		// if (reAdapter == null) {
		reAdapter = new RecordeAdapter(this, allLogInfos);
		recordeList.setAdapter(reAdapter);
		// } else {
		// reAdapter.optionsNotify(allLogInfos);
		// }

	}

	private void getDialog() {
		builder = new Dialog(RechageActivity.this);
		builder.show();
		View contentview = LayoutInflater.from(RechageActivity.this).inflate(
				R.layout.center_guanzhu_create_group_dialog, null);
		builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		builder.getWindow().setContentView(
				R.layout.center_guanzhu_create_group_dialog);

	}

	private JSONObject string2JSON(String src, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arr = src.split(split);
			for (int i = 0; i < arr.length; i++) {
				String[] arrKey = arr[i].split("=");
				json.put(arrKey[0], arr[i].substring(arrKey[0].length() + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	private void getAccountData() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("token", user.get("token").toString()));
//		params.add(new BasicNameValuePair("uid", user.get("uid").toString()));
//
//		requstModel.setRqquestHandler(RechageActivity.this, czHandler, params,
//				5, accountUrl);
		
		try {
			doFetchUserAccountAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void doFetchUserAccountAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("useraccount", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONObject data = response.getJSONObject("data");
                    	score = data.getString("score");
                    	gold = data.getString("gold");
                    	
                    	runOnUiThread(new Runnable() {
                    	    @Override
                    	    public void run() {
                    	    	czOnline.performClick();
                    	    }
                    	});
                    	
                    } else if (status == 0) {
                        Toast.makeText(RechageActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	
	private void doFetchLogsAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("type", "0");
        HttpRestClient.post("useraccount/logs", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (pb != null) {
        					pb.setVisibility(View.GONE);
        				}
        				if (recordeList != null) {
        					recordeList.setVisibility(View.VISIBLE);
        				}

        				JSONArray jsArray = response.getJSONArray("data");
//						JSONArray jsArray = response.getJSONArray("logs");
						Type listType = new TypeToken<List<LogInfo>>() {
						}.getType();
						Gson gson = new Gson();
						recordeInfoList = gson.fromJson(jsArray.toString(),
								listType);
//						lastPage = js.getBoolean("lastPage");
						if (recordeInfoList != null && allLogInfos != null) {
							if (recordeInfoList.size() > 0) {
								for (LogInfo item : recordeInfoList) {
									allLogInfos.add(item);
								}
								setData();
//								record_heading.setVisibility(View.VISIBLE);
							} else {
//								Log.d("goes here==========>", "yes");
//								recordeList.setVisibility(View.GONE);
//								empty_hint_tv.setVisibility(View.VISIBLE);
							}
						}

                    } else if (status == 0) {
                        Toast.makeText(RechageActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	private void doExchangeGoldToScoreAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("amount", exchange_score_num.getText().toString().trim());
        HttpRestClient.post("exchange/goldToScore", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success

                    } else if (status == 0) {
                        Toast.makeText(RechageActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
}
