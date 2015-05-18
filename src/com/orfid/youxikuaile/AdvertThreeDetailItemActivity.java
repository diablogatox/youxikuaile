package com.orfid.youxikuaile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orfid.youxikuaile.adapter.AdvertThreeListView;
import com.orfid.youxikuaile.model.HttpRequstModel;
import com.orfid.youxikuaile.model.User;
import com.orfid.youxikuaile.model.contants;

public class AdvertThreeDetailItemActivity extends Activity implements
		ListItemClickHelp {

	// 服务器的链接的接口
	public final String RecruitmentDetialUrl = "http://api.yxkuaile.com/zhaopin";
	private Button backBtn;
	private TextView usernamedetailitem, usersignturedetailitem;
	private ScrollView center_fans_user_signture_advertthree_detailitem_sv;

	private List<contants> fansList, allFansList = new ArrayList<contants>();
	private int page = 1;
	private String uid, fansId, userName, userSignture, id;
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
							JSONArray jsArray = json.getJSONArray("info");
							Type listType = new TypeToken<List<contants>>() {
							}.getType();
							Gson gson = new Gson();
							fansList = gson.fromJson(jsArray.toString(),
									listType);
							// lastPage = json.getBoolean("lastPage");
							if (fansList != null && fansList != null) {

								allFansList.addAll(fansList);

								setData();

							}

						} else if (json.getInt("status") == 0) {
							if (json.has("error")) {
								Toast.makeText(
										AdvertThreeDetailItemActivity.this,
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advert_three_detail_item);
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
//
//		ApplicationData.getApplicatinInstance().addActivity(this);
		uid = getIntent().getStringExtra("uid");
		userName = getIntent().getStringExtra("userName");
		userSignture = getIntent().getStringExtra("userSignture");
		id = getIntent().getStringExtra("id");
		initView();
		getData();

		usernamedetailitem.setText(userName);

		usersignturedetailitem.setText(userSignture);

	}

	private void initView() {
		// TODO Auto-generated method stub
		// footerView = LayoutInflater.from(this).inflate(
		// R.layout.list_bottom_refresh, null);
		// advertThreeListView = (ListView)
		// findViewById(R.id.hayou_center_advertthree_list_user);

		backBtn = (Button) findViewById(R.id.hayou_center_advertthree_back_button_detailitem);

		usernamedetailitem = (TextView) findViewById(R.id.center_fans_user_name_advertthree_detailitem);

		usersignturedetailitem = (TextView) findViewById(R.id.center_fans_user_signture_advertthree_detailitem);

		// center_fans_user_signture_advertthree_detailitem_sv =
		// (ScrollView)findViewById(R.id.center_fans_user_signture_advertthree_detailitem_sv);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// FansActivity
				// FansActivity.this.finish();
//				if (ApplicationData.getInstance().getUid().equals(uid)) {
//					Intent it = new Intent(AdvertThreeDetailItemActivity.this,
//							IndexActivity.class);
//					it.putExtra("tag", 3);
//					AdvertThreeDetailItemActivity.this.startActivity(it);
//					AdvertThreeDetailItemActivity.this.finish();
//				} else {
//					AdvertThreeDetailItemActivity.this.finish();
//				}
			}
		});

		// usersignturedetailitem.setOnScrollListener(new OnScrollListener() {
		//
		// // 当ListView不在滚动，并且ListView的最后一项的索引等于adapter的项数减一时则自动加载（因为索引是从0开始的）
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		//
		// Log.i("info", "onScrollStateChanged");
		// if (view.getAdapter() == null) {
		// Log.i("info", "view.getAdapter() is null ");
		// return;
		// }
		//
		// int itemsLastIndex = view.getAdapter().getCount() - 1; // 数据集最后一项的索引
		// int lastIndex = itemsLastIndex; // 加上底部的loadMoreView项
		// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
		// && visibleLastIndex == lastIndex) {
		// // 如果是自动加载,可以在这里放置异步加载数据的代码
		// if (mAdvertThreeListView != null) {
		// if (!lastPage) {
		// // fansListView.addFooterView(footerView);
		// page++;
		// mAdvertThreeListView.notifyDataSetChanged();
		// getData();
		//
		// } else {
		// Toast.makeText(
		// AdvertThreeDetailItemActivity.this,
		// AdvertThreeDetailItemActivity.this
		// .getResources()
		// .getString(
		// R.string.game_box_slider_list_bottom_notify),
		// Toast.LENGTH_SHORT / 2).show();
		// }
		// }
		//
		// }
		// }
		//
		// // 这三个int类型的参数可以自行Log打印一下就知道是什么意思了
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// AdvertThreeDetailItemActivity.this.visibleItemCount =
		// visibleItemCount;
		// visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		// }
		// });
	}

	private void setData() {

		if (mAdvertThreeListView == null) {
			mAdvertThreeListView = new AdvertThreeListView(this, allFansList,
					this, uid);
			// advertThreeListView.setAdapter(mAdvertThreeListView);
		} else {
			mAdvertThreeListView.changeData(allFansList);
		}

	}

	private void getData() {
//		if (requstModel == null) {
//			requstModel = new HttpRequstModel();
//		}
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		
//		params.add(new BasicNameValuePair("token", ApplicationData.token));
//		params.add(new BasicNameValuePair("id", ApplicationData
//				.getInstance_contants().getUid()));
//		if (ApplicationData.getInstance().getUid().equals(id)) {
//			params.add(new BasicNameValuePair("otherid", "0"));
//		} else {
//			params.add(new BasicNameValuePair("otherid", id));
//		}
//		params.add(new BasicNameValuePair("page", String.valueOf(page)));
//		requstModel.setRqquestHandler(AdvertThreeDetailItemActivity.this,
//				fansHandler, params, 1, RecruitmentDetialUrl);
	}

	@Override
	public void onClick(View item, View widget, int position, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case R.id.center_fans_admin_advertthree:
//			fansId = allFansList.get(position).getUid();
//			fansIndex = position;
//			progressDialog = ProgressDialog.show(
//					AdvertThreeDetailItemActivity.this, "请稍等...", "数据提交中...",
//					true);
			Log.i("info", "R.id.center_fans_admin");
			Log.i("info", "fansId: " + fansId);

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 监控返回键
//			if (ApplicationData.getInstance().getUid().equals(uid)) {
//				Intent it = new Intent(AdvertThreeDetailItemActivity.this,
//						IndexActivity.class);
//				it.putExtra("tag", 3);
//				AdvertThreeDetailItemActivity.this.startActivity(it);
//				AdvertThreeDetailItemActivity.this.finish();
//			} else {
//				AdvertThreeDetailItemActivity.this.finish();
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
