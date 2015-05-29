package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.MyGridView;

public class EventDetailUsersActivity extends Activity {

	private MyGridView event_users_gv;
	private MyAdapter adapter;
	private List<UserItem> users = new ArrayList<UserItem>();
	private String usersStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail_users);
		initView();
		setListener();
		obtainData();
	}
	
	private void initView() {
		event_users_gv = (MyGridView) findViewById(R.id.event_users_gv);
		
		adapter = new MyAdapter(this, R.layout.event_user_item, users);
		event_users_gv.setAdapter(adapter);
	}

	private void setListener() {
		
	}

	private void obtainData() {
		
		usersStr = getIntent().getStringExtra("usersStr");
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
	}

	public void stepBack(View v) {
		finish();
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
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView imgIv;
			TextView titleTv;
		}
		
	}

}
