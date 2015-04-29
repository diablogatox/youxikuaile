package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.FansItemsParser;
import com.orfid.youxikuaile.pojo.UserItem;

public class SysNotifyActivity extends Activity implements OnClickListener {
	
	private ImageButton backBtn;
	private ProgressBar mPbar;
	private TextView mLoadingHintTv;
	private ListView mListView;
	private List<UserItem> fansItems = new ArrayList<UserItem>();
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
			// TODO Auto-generated catch block
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
		mListView = (ListView) findViewById(R.id.fans_list_lv);
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

                    } else if (status == 0) {
                        Toast.makeText(SysNotifyActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
	            mPbar.setVisibility(View.GONE);
	            mLoadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				mPbar.setVisibility(View.VISIBLE);
				mLoadingHintTv.setVisibility(View.VISIBLE);
			}
        });
	}
	
	private class MyAdapter extends ArrayAdapter<UserItem> {
		
		private Context context;
		private List<UserItem> items;
		private int resource;
		private UserItem objBean;

		public MyAdapter(Context context, int resource, List<UserItem> items) {
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
		public UserItem getItem(int position) {
			return items.get(position);
		}
		
		HashMap<Integer,View> lmap = new HashMap<Integer,View>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.userPhotoIv = (ImageView) convertView.findViewById(R.id.user_photo_iv);
                viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
                viewHolder.actionBtn = (Button) convertView.findViewById(R.id.action_btn);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getUsername() != null) viewHolder.userNameTv.setText(objBean.getUsername());
            if (objBean.isFollow() == false) {
            	viewHolder.actionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_fans2));
            	viewHolder.actionBtn.setText("加关注");
            	viewHolder.actionBtn.setClickable(true);
            }
            
            return convertView;

		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			Button actionBtn;
		}
		
	}

}
