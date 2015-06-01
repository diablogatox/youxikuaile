package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class ChattingUsersActivity extends Activity {

	private MyGridView chat_users_gv;
	private Button btn_quit;
	private String sid;
	private List<UserItem> users = new ArrayList<UserItem>();
	private MyAdapter adapter;
	private static final int ADD_CHAT_USER = 0;
	private JSONArray jArr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting_users);
		initView();
		setListener();
		obtainData();
	}

	public void stepBack(View v) {
		finish();
	}
	
	private void initView() {
		btn_quit = (Button) findViewById(R.id.btn_quit);
		chat_users_gv = (MyGridView) findViewById(R.id.chat_users_gv);
		
		chat_users_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		adapter = new MyAdapter(this, R.layout.chat_user_item, users);
		chat_users_gv.setAdapter(adapter);
	}

	private void setListener() {
		
		chat_users_gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				UserItem item = adapter.getItem(position);
				if (!item.getUid().equals("0")) {
					String type = item.getType();
					String uid = item.getUid();
					String username = item.getUsername();
					String photo = item.getPhoto();
					boolean isFollowed = item.isFollow();
					Intent intent;
	            	if (type.equals("0")) {
	            		intent = new Intent(ChattingUsersActivity.this, FriendHomeActivity.class);
	            	} else {
	            		intent = new Intent(ChattingUsersActivity.this, PublicHomeActivity.class);
	            	}
	                intent.putExtra("uid", uid);
	                intent.putExtra("username", username);
	                intent.putExtra("photo", photo);
	                intent.putExtra("isFollowed", isFollowed);
	                
	                startActivity(intent);
				} else {
					// add new user
					Intent intent = new Intent(ChattingUsersActivity.this, SelectFriendsActivity.class);
					startActivityForResult(intent, ADD_CHAT_USER);
				}
			}
			
		});
		
		btn_quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ChattingUsersActivity.this)
				.setMessage("确定退出聊天?")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							doRemoveAndQuitAction(sid);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
				})
				.setNegativeButton("否", null)
				.show();
			}
			
		});
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case ADD_CHAT_USER:
			Log.d("selected users=========>", data.getStringExtra("selected_friends"));
			List<String> members = new ArrayList<String>();
			try {
				jArr = new JSONArray(data.getStringExtra("selected_friends"));
				Log.d("jArr length========>", jArr.length()+"");
				for (int i=0; i<jArr.length(); i++) {
					JSONObject jObj = (JSONObject) jArr.get(i);
					Log.d("obj=====>", jObj.getString("uid"));
					members.add(jObj.getString("uid"));
				}
				Log.d("members count========>", members.size()+"");
				doJoinChatGroupAction(members);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void obtainData() {
		sid = getIntent().getStringExtra("sid");
		String usersStr = getIntent().getStringExtra("users");
		JSONArray usersArr;
		try {
			usersArr = new JSONArray(usersStr);
			for (int i=0; i<usersArr.length(); i++) {
				JSONObject jObj = (JSONObject) usersArr.get(i);
				String uid = jObj.getString("uid");
				String username = jObj.getString("username");
				String photo = jObj.getString("photo");
				String type = jObj.getString("type");
				users.add(new UserItem(uid, username, photo, null, type));
			}
			
			users.add(new UserItem("0", null, null, "add_pic", null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		adapter.notifyDataSetChanged();
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
            
            if (objBean.getUid().equals("0")) {
        		viewHolder.imgIv.setImageDrawable(getResources().getDrawable(R.drawable.add_pic));
        	}
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView imgIv;
			TextView titleTv;
		}
		
	}

	private void doRemoveAndQuitAction(String sid) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("sid", sid);
        HttpRestClient.post("message/exitGroup", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	 Toast.makeText(ChattingUsersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(ChattingUsersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
	
	private void doJoinChatGroupAction(List<String> members) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("sid", sid);
        params.put("members", members);
        HttpRestClient.post("message/joinGroup", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	 Toast.makeText(ChattingUsersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
//                    	if (jArr.length() > 0) {
//                    		
//	                    	for (int i=0; i < jArr.length(); i++) {
//	                    	 	JSONObject jObj = (JSONObject) jArr.get(i);
//	                    		adapter.insert(new UserItem(
//	                    					jObj.getString("uid"),
//	                    					jObj.getString("name"),
//	                    					jObj.has("icon")?jObj.getString("icon"):null,
//	                    					null,
//	                    					jObj.has("icon")?jObj.getString("info"):null
//	                    				), adapter.getCount() - 2);
//	                    	}
//                    	}
//                    	adapter.notifyDataSetChanged();
                    } else if (status == 0) {
                        Toast.makeText(ChattingUsersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
	
}
