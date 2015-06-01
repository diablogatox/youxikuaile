package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.OnlineSitterSearchResultActivity.ViewHolder;
import com.orfid.youxikuaile.parser.GameItemsParser;
import com.orfid.youxikuaile.pojo.UserItem;

public class OfflineSittersActivity extends Activity implements View.OnClickListener {

    private ListView offlineSittersLv;
    private ImageButton backBtn;
    private Button sitterOnlineBtn;
    private List<UserItem> items = new ArrayList<UserItem>();
    private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_offline_sitter);

        offlineSittersLv = (ListView) findViewById(R.id.lv_offline_sitters);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        sitterOnlineBtn = (Button) findViewById(R.id.btn_sitter_online);

        backBtn.setOnClickListener(this);
        sitterOnlineBtn.setOnClickListener(this);
        offlineSittersLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserItem item = adapter.getItem(position);
				String type = item.getType();
				String uid = item.getUid();
				String username = item.getUsername();
				String photo = item.getPhoto();
				boolean isFollowed = item.isFollow();
				Intent intent;
            	if (type.equals("0")) {
            		intent = new Intent(OfflineSittersActivity.this, FriendHomeActivity.class);
            	} else {
            		intent = new Intent(OfflineSittersActivity.this, PublicHomeActivity.class);
            	}
                intent.putExtra("uid", uid);
                intent.putExtra("username", username);
                intent.putExtra("photo", photo);
                intent.putExtra("isFollowed", isFollowed);
                
                startActivity(intent);
			}
        	
        });

        adapter = new MyAdapter(items);
        offlineSittersLv.setAdapter(adapter);
        
        try {
			doFetchOfflineSittersAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
            case R.id.btn_sitter_online:
                finish();
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(OfflineSittersActivity.this).inflate(
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
	            		intent = new Intent(OfflineSittersActivity.this, FriendHomeActivity.class);
	            	} else {
	            		intent = new Intent(OfflineSittersActivity.this, PublicHomeActivity.class);
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

        public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			TextView userGeoInfoTv;
			TextView userSignatureTv;
		}

    }
    
    private void doFetchOfflineSittersAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("mod", 2);
        HttpRestClient.post("user/findByDistance", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONArray jArr = response.getJSONArray("data");
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
        			        
        			        items.add(userItem);
        				}
        				
                      if (items.size() <= 0) {
//      					emptyHintLlView.setVisibility(View.VISIBLE);
                      }
        				
        				adapter.notifyDataSetChanged();

                    } else if (status == 0) {
                        Toast.makeText(OfflineSittersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
