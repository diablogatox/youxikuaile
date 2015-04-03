package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.UserItem;

public class OnlineSitterSearchResultActivity extends Activity implements OnClickListener {

	private TextView titleTv;
	private ImageButton backIb;
	private ListView mListView;
	private List<UserItem> sitterItems = new ArrayList<UserItem>();
	private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_sitter_search_result);
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		titleTv = (TextView) findViewById(R.id.title_tv);
		backIb = (ImageButton) findViewById(R.id.back_btn);
		mListView = (ListView) findViewById(R.id.lv_sitters);
	}

	private void setListener() {
		backIb.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("test======>", "test");
			}
			
		});
	}

	private void obtainData() {
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			titleTv.setText(intent.getStringExtra("game") + "陪玩");
			try {
				JSONArray jArr = new JSONArray(intent.getStringExtra("result"));
				Log.d("jArr length======>", jArr.length()+"");
				for (int i=0; i<jArr.length(); i++) {
					String uid, photo, username, utime, distance, type, sex, signature = null;
					UserItem userItem = new UserItem();
					boolean isFollowed;
					JSONObject jObj = jArr.getJSONObject(i);
					uid = jObj.getString("uid");
			        photo = jObj.getString("photo");
			        username = jObj.getString("username");
			        utime = jObj.getString("utime");
			        distance = jObj.getString("distance");
			        type = jObj.getString("type");
			        sex = jObj.getString("sex");
			        isFollowed = jObj.getBoolean("isFollow");
			        if (!jObj.isNull("info")) {
			        	signature = jObj.getJSONObject("info").getString("title");
			        }
			        
			        userItem.setUid(uid);
			        userItem.setPhoto(photo);
			        userItem.setUsername(username);
			        userItem.setDistance(distance);
			        userItem.setUtime(utime);
			        userItem.setType(type);
			        userItem.setSignature(signature);
			        userItem.setSex(sex);
			        userItem.setFollow(isFollowed);
			        
			        sitterItems.add(userItem);
				}
				
				adapter = new MyAdapter(sitterItems);
				mListView.setAdapter(adapter);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		}
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private List<UserItem> items;
		private UserItem objBean;
		
		public MyAdapter(List<UserItem> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public UserItem getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(items.get(position).getUid());
		}

		HashMap<Integer,View> lmap = new HashMap<Integer,View>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(OnlineSitterSearchResultActivity.this).inflate(
                        R.layout.sitter_user_item, parent, false);
                viewHolder.userPhotoIv = (ImageView) convertView.findViewById(R.id.user_photo_iv);
                viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
                viewHolder.userGeoInfoTv = (TextView) convertView.findViewById(R.id.user_geo_info);
                viewHolder.userSignatureTv = (TextView) convertView.findViewById(R.id.user_signature);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getUsername() != null && !objBean.getUsername().equals("null")) viewHolder.userNameTv.setText(objBean.getUsername());
            if (!objBean.getDistance().equals("未知距离")) {
            	String geoInfo = objBean.getDistance() + " | " + objBean.getUtime();
            	viewHolder.userGeoInfoTv.setText(geoInfo);
            }
            if (objBean.getSignature() != null && !objBean.getSignature().equals("null")) viewHolder.userSignatureTv.setText(objBean.getSignature());
            
            viewHolder.userPhotoIv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String type = objBean.getType();
					String uid = objBean.getUid();
					String username = objBean.getUsername();
					String photo = objBean.getPhoto();
					boolean isFollowed = objBean.isFollow();
					Intent intent;
	            	if (type.equals("0")) {
	            		intent = new Intent(OnlineSitterSearchResultActivity.this, FriendHomeActivity.class);
	            	} else {
	            		intent = new Intent(OnlineSitterSearchResultActivity.this, PublicHomeActivity.class);
	            	}
	                intent.putExtra("uid", uid);
	                intent.putExtra("username", username);
	                intent.putExtra("photo", photo);
	                intent.putExtra("isFollowed", isFollowed);
	                startActivity(intent);
				}
            });
            
            return convertView;
		}
		
	}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			TextView userGeoInfoTv;
			TextView userSignatureTv;
		}
}
