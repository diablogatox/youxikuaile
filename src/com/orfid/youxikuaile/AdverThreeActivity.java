package com.orfid.youxikuaile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.adapter.AdvertThreeListView;
import com.orfid.youxikuaile.model.HttpRequstModel;
import com.orfid.youxikuaile.model.User;
import com.orfid.youxikuaile.model.contants;
import com.orfid.youxikuaile.parser.FansItemsParser;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class AdverThreeActivity extends Activity implements ListItemClickHelp {

	// 服务器的链接的接口
	public final String RecruitmentUrl = "http://api.yxkuaile.com/zhaopin";
	private Button backBtn;
//	private DynamicAdapter mAdapter;
	private ImageView hot_advert_3_img;
	
	
	//private final String fansUrl = "http://api.yxkuaile.com/user/fans";
	//private final String addGzUrl = "http://api.yxkuaile.com/user/addFollow";
	private ListView advertThreeListView;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private int visibleItemCount;
	private View footerView;
	private List<contants> fansList, allFansList = new ArrayList<contants>();
	private int page = 1;
	private String id, fansId;
	private AdvertThreeListView mAdvertThreeListView;
	private Dialog psDialog;
	private HttpRequstModel requstModel;
	private boolean lastPage = false;
	private int fansIndex;
	private User userData;
	private ProgressDialog progressDialog;
	private Handler fansHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case 1:
				try {
					JSONObject json = (JSONObject) msg.obj;
					if (json.has("status")) {
						if (json.getInt("status") == 1) {
//							ApplicationData.token = json.getString("token");
							JSONArray jsArray = json.getJSONArray("infolist");
							
							Type listType = new TypeToken<List<contants>>() {
							}.getType();
							Gson gson = new Gson();
							fansList = gson.fromJson(jsArray.toString(),
									listType);
							//lastPage = json.getBoolean("lastPage");
							if (fansList != null && fansList != null) {

								allFansList.addAll(fansList);

								setData();

							}

						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										AdverThreeActivity.this,
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


			}

		}
	};

	public void stepBack(View view) {
	    finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hayou_activity_three);
//		if (ApplicationData.getInstance().getUid() == null
//				|| (Integer) ApplicationData.getInstance().getIdentity() == null) {
//
//		}
//		if (savedInstanceState != null) {
//			userData = savedInstanceState.getParcelable("userData");
//			if (userData != null) {
//				ApplicationData.setInstance(userData);
//			}
//		}
//		ApplicationData.getApplicatinInstance().addActivity(this);
		id = getIntent().getStringExtra("id");
		initView();
		getData();

	}

	private void initView() {
		// TODO Auto-generated method stub
		footerView = LayoutInflater.from(this).inflate(
				R.layout.list_bottom_refresh, null);
		advertThreeListView = (ListView) findViewById(R.id.hayou_center_advertthree_list_user);
		
		hot_advert_3_img = (ImageView)findViewById(R.id.hot_advert_3_img);
		
		
		advertThreeListView.setOnScrollListener(new OnScrollListener() {

			// 当ListView不在滚动，并且ListView的最后一项的索引等于adapter的项数减一时则自动加载（因为索引是从0开始的）
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				Log.i("info", "onScrollStateChanged");
				if (view.getAdapter() == null) {
					Log.i("info", "view.getAdapter() is null ");
					return;
				}

				int itemsLastIndex = view.getAdapter().getCount() - 1; // 数据集最后一项的索引
				int lastIndex = itemsLastIndex; // 加上底部的loadMoreView项
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& visibleLastIndex == lastIndex) {
					// 如果是自动加载,可以在这里放置异步加载数据的代码
					
					//这里有一个落差点啊，误差
					if (mAdvertThreeListView != null) {
						if (lastPage) {
							// fansListView.addFooterView(footerView);
							page++;
							mAdvertThreeListView.notifyDataSetChanged();
							getData();

						} else {
							Toast.makeText(
									AdverThreeActivity.this,
									AdverThreeActivity.this
											.getResources()
											.getString(
													R.string.game_box_slider_list_bottom_notify),
									Toast.LENGTH_SHORT / 2).show();
						}
					}

				}
			}

			// 这三个int类型的参数可以自行Log打印一下就知道是什么意思了
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				AdverThreeActivity.this.visibleItemCount = visibleItemCount;
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
			}
		});
	}

	private void setData() {

		if (mAdvertThreeListView == null) {
			mAdvertThreeListView = new AdvertThreeListView(this, allFansList, this, id);
			advertThreeListView.setAdapter(mAdvertThreeListView);
		} else {
			mAdvertThreeListView.changeData(allFansList);
		}

	}

	private void getData() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("token", ApplicationData.token));
//		params.add(new BasicNameValuePair("id", ApplicationData.getInstance_contants().getId()));
//	
//		requstModel.setRqquestHandler(AdverThreeActivity.this, fansHandler, params,
//				1, RecruitmentUrl);
	    
	    try {
            doFetchAdvertiseInfoAction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
	    
	}

	
	private void doFetchAdvertiseInfoAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("zhaopin", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        JSONObject data = response.getJSONObject("data");
                        JSONArray jsArray = data.getJSONArray("items");
                        
                        Type listType = new TypeToken<List<contants>>() {
                        }.getType();
                        Gson gson = new Gson();
                        fansList = gson.fromJson(jsArray.toString(),
                                listType);
                        //lastPage = json.getBoolean("lastPage");
                        if (fansList != null && fansList != null) {

                            allFansList.addAll(fansList);

                            setData();

                        }
                    } else if (status == 0) {
                        Toast.makeText(AdverThreeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	@Override
	public void onClick(View item, View widget, int position, int which) {
		// TODO Auto-generated method stub
//		switch (which) {
//		case R.id.center_fans_admin_advertthree:
//			fansId = allFansList.get(position).getUid();
//			fansIndex = position;
//			progressDialog = ProgressDialog.show(AdverThreeActivity.this, "请稍等...",
//					"数据提交中...", true);
//			Log.i("info", "R.id.center_fans_admin");
//			Log.i("info", "fansId: " + fansId);
//
//			break;
//
//		default:
//			break;
//		}
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (ApplicationData.getInstance() != null) {
//			outState.putParcelable("userData", ApplicationData.getInstance());
//
//		}
//
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 监控返回键
//			if (ApplicationData.getInstance().getUid().equals(id)) {
//				Intent it = new Intent(AdverThreeActivity.this, IndexActivity.class);
//				it.putExtra("tag", 3);
//				AdverThreeActivity.this.startActivity(it);
//				AdverThreeActivity.this.finish();
//			} else {
//				AdverThreeActivity.this.finish();
//			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		fansList = null;
		allFansList = null;

	}

	
	
}
