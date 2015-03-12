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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.NewsFeedActivity.MyAdapter.ViewHolder;
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.pojo.UserItem;

public class FollowListActivity extends Activity implements OnClickListener {
	
	private ImageButton backBtn;
	private TextView loadingHintTv;
	private ProgressBar pBar;
	private ListView mListView;
	private List<UserItem> userItems = new ArrayList<UserItem>();
    private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follow_list);
		initView();
		setListener();
		obtainData();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
	}

	private void obtainData() {
		try {
			doFetchFollowListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		loadingHintTv = (TextView) findViewById(R.id.loading_hint_tv);
		pBar = (ProgressBar) findViewById(R.id.progress_bar);
		mListView = (ListView) findViewById(R.id.follow_list_lv);
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
	
	private void doFetchFollowListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
//        params.put("page", page);
        HttpRestClient.post("follow", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        FollowItemsParser parser = new FollowItemsParser();
                        userItems = parser.parse(response.getJSONObject("data"));
                        Log.d("userItems count=====>", userItems.size()+"");
                        adapter = new MyAdapter(FollowListActivity.this, R.layout.follow_item, userItems);
                        mListView.setAdapter(adapter);
//                        if (feedItems.size() <= 0) {
//                            emptyViewLl.setVisibility(View.VISIBLE);
//                        }

                    } else if (status == 0) {
                        Toast.makeText(FollowListActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
	            pBar.setVisibility(View.GONE);
	            loadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
	            pBar.setVisibility(View.VISIBLE);
	            loadingHintTv.setVisibility(View.VISIBLE);
			}
        });
    }
	
	private class MyAdapter extends ArrayAdapter<UserItem> {
		
		private List<UserItem> items;
		private UserItem objBean;
		private Context context;
		private int resource;

		public MyAdapter(Context context, int resource, List<UserItem> items) {
			super(context, resource, items);
			this.items = items;
			this.context = context;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return items == null ? 0: items.size();
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
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getUsername() != null) viewHolder.userNameTv.setText(objBean.getUsername());
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
		}
		
	}

}
