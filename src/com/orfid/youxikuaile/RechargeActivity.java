package com.orfid.youxikuaile;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.widget.ViewPagerIndicatorView;

public class RechargeActivity extends Activity {

	private ViewPagerIndicatorView viewPagerIndicatorView;
	private ListView mListView;
	private String score, gold;
	private TextView gold_num, score_num;
	private ProgressBar chongzhi_recorde_layout_progress1;
	private TextView no_data_hint;
	private EditText numberMoney;
	private Button okCommit;
	private String num, trade_no;
	private int position = 0;
	
	private Handler czHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.RQF_PAY:
				Log.d("result=============>", (String) msg.obj);
				Result result = new Result((String) msg.obj);
				// Result.mResult = (String) msg.obj;

				int mm = msg.arg1;
				Toast.makeText(RechargeActivity.this, result.getResult(),
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
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		
		this.viewPagerIndicatorView = (ViewPagerIndicatorView) findViewById(R.id.viewpager_indicator_view);
//		TreeMap<String, View> map = new TreeMap<String, View>(new MyCopr());
		Map<String, View> map = new HashMap<String, View>();
		map.put("在线充值", LayoutInflater.from(this).inflate(R.layout.tab_online_recharge, null));
		map.put("充值记录", LayoutInflater.from(this).inflate(R.layout.tab_recharge_records, null));
		map.put("兑换积分", LayoutInflater.from(this).inflate(R.layout.tab_exchange_points, null));
		this.viewPagerIndicatorView.setupLayout(map);
		this.viewPagerIndicatorView.viewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    	viewPagerIndicatorView.viewPager.requestDisallowInterceptTouchEvent(true);
                            break;
                    case MotionEvent.ACTION_CANCEL:
                    	viewPagerIndicatorView.viewPager.requestDisallowInterceptTouchEvent(false);

                    default:
                            break;
                    }
                    return true;
            }
		});
		this.viewPagerIndicatorView.viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				position = arg0;
				setup(position);
			}
			
		});

		try {
			doFetchUserAccountAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setup(int position) {
		Log.d("position===========>", ""+position);
		switch (position) {
		case 0:
			score_num = (TextView) findViewById(R.id.score_num);
			score_num.setText(score+"个积分");
			break;
		case 1:
			mListView = (ListView) findViewById(R.id.hayou_chongzhi_recorde_list_view);
			chongzhi_recorde_layout_progress1 = (ProgressBar) findViewById(R.id.chongzhi_recorde_layout_progress1);
			no_data_hint = (TextView) findViewById(R.id.no_data_hint);
			try {
				doFetchLogsAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
//			ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {"1", "2", "3", "4"});
//			mListView.setAdapter(adapter);
			break;
		case 2:
			gold_num = (TextView) findViewById(R.id.gold_num);
			numberMoney = (EditText)
					findViewById(R.id.hayou_chongzhi_money_num);
			okCommit = (Button) findViewById(R.id.ok_btn);
			okCommit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d("ddddd=========>", "dddddddd");
					num = numberMoney.getText().toString().trim();
					
					OnlinePay pay = new OnlinePay();
					pay.payMoney(RechargeActivity.this, num, czHandler,
							getOutTradeNo());
				}
				
			});
			gold_num.setText(gold + "个金币");
			break;
		}
	}
	
	class MyCopr implements Comparator<String>{
		
		@Override
		public int compare(String str1, String str2)
		{
			return str1.compareTo(str2);
		}
	}
	
	public void goBack(View view) {
		finish();
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
                    	score_num = (TextView) findViewById(R.id.score_num);
            			score_num.setText(score+"个积分");
                    } else if (status == 0) {
                        Toast.makeText(RechargeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
                    	chongzhi_recorde_layout_progress1.setVisibility(View.GONE);
               
                    	JSONArray data = response.getJSONArray("data");
                    	if (data.length() > 0) {
                    		 
                    	} else {
                    		no_data_hint.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(RechargeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	private String getOutTradeNo() {
		long longTime = System.currentTimeMillis();
		// 取从a到b的随机数就是（int）（（b-a)*Math.random()＋a）
		int ran = (int) (9900 * Math.random() + 100);

		return String.valueOf((long) (longTime / 1000)) + String.valueOf(ran);
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

}