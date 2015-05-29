package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.MyGridView;


public class EventDetailActivity extends Activity {

	private TextView event_title, title_tv, event_msg;
	private Button join_btn;
	private String id;
	private MyGridView event_users_gv;
	private MyAdapter adapter;
	private List<UserItem> users = new ArrayList<UserItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		initView();
		setListener();
		obtainData();
	}

	public void stepBack(View v) {
		finish();
	}
	
	private void initView() {
		title_tv = (TextView) findViewById(R.id.tv_title);
		event_title = (TextView) findViewById(R.id.event_title);
		event_msg = (TextView) findViewById(R.id.event_msg);
		join_btn = (Button) findViewById(R.id.join_btn);
		event_users_gv = (MyGridView) findViewById(R.id.event_users_gv);
		
		adapter = new MyAdapter(this, R.layout.chat_user_item, users);
		event_users_gv.setAdapter(adapter);
	}

	private void setListener() {
		join_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((TextView) v).setText("已参加");
				v.setClickable(false);
				try {
					doJoinEventAction(id);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	private void obtainData() {
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString("event_id");
		
		String usersStr = getIntent().getStringExtra("users");
		JSONArray usersArr;
		try {
			usersArr = new JSONArray(usersStr);
			for (int i=0; i<usersArr.length(); i++) {
				JSONObject jObj = (JSONObject) usersArr.get(i);
				String uid = jObj.getString("uid");
				String username = jObj.getString("username");
				String photo = jObj.getString("photo");
				users.add(new UserItem(uid, username, photo, null, null));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		adapter.notifyDataSetChanged();
		
		title_tv.setText(bundle.getString("username"));	
		event_title.setText(bundle.getString("event_title"));
		event_msg.setText(Html.fromHtml(bundle.getString("event_msg")));
	}
	
	private void doJoinEventAction(String id) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", id);
        HttpRestClient.post("huodong/join", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	Toast.makeText(EventDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(EventDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                viewHolder.imgIv = (ImageView) convertView.findViewById(R.id.user_icon);
                viewHolder.titleTv = (TextView) convertView.findViewById(R.id.user_name);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null) 
            	ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.imgIv);
            viewHolder.titleTv.setText(objBean.getUsername());
            viewHolder.titleTv.setVisibility(View.GONE);
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView imgIv;
			TextView titleTv;
		}
		
	}
	
}
