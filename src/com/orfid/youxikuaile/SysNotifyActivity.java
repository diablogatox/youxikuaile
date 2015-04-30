package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.parser.SysNotifyParser;
import com.orfid.youxikuaile.pojo.SysNotifyItem;

public class SysNotifyActivity extends Activity implements OnClickListener {
	
	private ImageButton backBtn;
	private ProgressBar mPbar;
	private TextView mLoadingHintTv;
	private ListView mListView;
	private List<SysNotifyItem> sysNotifyItems = new ArrayList<SysNotifyItem>();
    private MyAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sys_notify);
		initView();
		setListener();
		obtainData();
	}

	private void obtainData() {
		try {
			doFetchSysNotifyAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		mPbar = (ProgressBar) findViewById(R.id.progress_bar);
		mLoadingHintTv = (TextView) findViewById(R.id.loading_hint_tv);
		mListView = (ListView) findViewById(R.id.sysnotify_list_lv);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		default:
			break;
		}
	}
	
	private void doFetchSysNotifyAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("message/notice", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	SysNotifyParser parser = new SysNotifyParser();
                    	sysNotifyItems = parser.parse(response);
                    	if (sysNotifyItems.size() > 0) {
                    		adapter = new MyAdapter(SysNotifyActivity.this, R.layout.sysnotify_item, sysNotifyItems);
                    		mListView.setAdapter(adapter);
                    	} else {
                    		mLoadingHintTv.setText("没有消息");
                    		if (!mLoadingHintTv.isShown()) mLoadingHintTv.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(SysNotifyActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
//	            mPbar.setVisibility(View.GONE);
	            mLoadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
//				mPbar.setVisibility(View.VISIBLE);
				mLoadingHintTv.setVisibility(View.VISIBLE);
			}
        });
	}
	
	private class MyAdapter extends ArrayAdapter<SysNotifyItem> {
		
		private Context context;
		private List<SysNotifyItem> items;
		private int resource;
		private SysNotifyItem objBean;

		public MyAdapter(Context context, int resource, List<SysNotifyItem> items) {
			super(context, resource, items);
			this.context = context;
			this.items = items;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public SysNotifyItem getItem(int position) {
			return items.get(position);
		}
		
		
		@Override
		public long getItemId(int position) {
			return Long.parseLong(items.get(position).getId());
		}

		HashMap<Integer,View> lmap = new HashMap<Integer,View>();
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.sysnotify_msg = (TextView) convertView.findViewById(R.id.sysnotify_msg);
                viewHolder.action_panel = convertView.findViewById(R.id.action_panel);
                viewHolder.btn_processed = (Button) convertView.findViewById(R.id.btn_processed);
                viewHolder.btn_deny = (Button) convertView.findViewById(R.id.btn_deny);
                viewHolder.btn_accept = (Button) convertView.findViewById(R.id.btn_accept);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
            	convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);

            viewHolder.sysnotify_msg.setText(objBean.getText());
            
            viewHolder.btn_deny.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					objBean.setAction("0");
//					adapter.notifyDataSetChanged();
//					Log.d("deny======>", "deny");
					Log.d("id=========>", objBean.getId());
					try {
						doReplySysNotifyAction(objBean.getId(), false);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
            });
            
            viewHolder.btn_accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					objBean.setAction("1");
//					adapter.notifyDataSetChanged();
//					Log.d("accept======>", "accept");
					Log.d("id=========>", objBean.getId());
					try {
						doReplySysNotifyAction(objBean.getId(), true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
            });
            
            switch (objBean.getAction()) {
            case "-1": // 未处理
            	viewHolder.action_panel.setVisibility(View.VISIBLE);
            	viewHolder.btn_processed.setVisibility(View.GONE);
            	break;
            case "0": // deny
            	viewHolder.action_panel.setVisibility(View.GONE);
            	viewHolder.btn_processed.setBackgroundResource(R.drawable.btn_fans1);
            	viewHolder.btn_processed.setText("已拒绝");
            	viewHolder.btn_processed.setVisibility(View.VISIBLE);
            	break;
            case "1": // accept
            	viewHolder.action_panel.setVisibility(View.GONE);
            	viewHolder.btn_processed.setBackgroundResource(R.drawable.btn_fans2);
            	viewHolder.btn_processed.setText("已同意");
           	 	viewHolder.btn_processed.setVisibility(View.VISIBLE);
            	break;
            	
            }

            
            return convertView;

		}
		
		public class ViewHolder {
			TextView sysnotify_msg;
			View action_panel;
			Button btn_processed;
			Button btn_deny, btn_accept;
		}
		
	}
	
	
	private void doReplySysNotifyAction(String id, boolean accept) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", id);
        params.put("action", accept == true ? 1 : 0);
        HttpRestClient.post("message/UserAction", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	doFetchSysNotifyAction();
                    } else if (status == 0) {
                        Toast.makeText(SysNotifyActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
