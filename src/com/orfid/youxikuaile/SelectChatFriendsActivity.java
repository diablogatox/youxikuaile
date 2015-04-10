package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.adapter.ContactsAdapterSF;
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.pojo.Contacts;
import com.orfid.youxikuaile.pojo.UserItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectChatFriendsActivity extends Activity {

	private TextView tv_ps_cancel,tv_ps_sure;
	private GridView gv_ps;
	private ListView lv_ps1;
	private ContactsAdapterSF adaptersf;
	private SharedPreferences sp;
	private String token;
	private List<Map<String, Object>> mapList, mapList2;
	private MyGVAdapter gvAdapter;
	private List<UserItem> userItems = new ArrayList<UserItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_friends);
		
		initWidget();
		tv_ps_cancel = (TextView) findViewById(R.id.tv_ps_cancel);
		tv_ps_sure = (TextView) findViewById(R.id.tv_ps_sure);
		gv_ps = (GridView) findViewById(R.id.gv_ps);
		lv_ps1 = (ListView) findViewById(R.id.lv_ps1);
		
		mapList2 = new ArrayList<Map<String, Object>>();
		gvAdapter = new MyGVAdapter(this, mapList2);
		gv_ps.setAdapter(gvAdapter);
		
		gv_ps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("gv_index=====>", position+"");
//				
//				int lvIndex = Integer.parseInt(mapList2.get(position).get("lvIndex").toString());
//				Log.d("lv_index======>", lvIndex+"");
//				Map<String, Object> contact = mapList.get(lvIndex);
//				mapList2.remove(position);
//				contact.put("check", false);
			}
			
		});

		tv_ps_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_ps_sure.setOnClickListener(new OnClickListener() {
			
			Map inviteMap = new LinkedHashMap();
			
			@Override
			public void onClick(View arg0) {
				if (gv_ps.getChildCount() !=0) {
					List  inviteeUnRegisteredUserList = new LinkedList();
					List<String> members = new ArrayList<String>();
					for (int i=0; i<mapList2.size(); i++) {
						inviteMap.put("uid", mapList2.get(i).get("uid"));
						members.add(mapList2.get(i).get("uid").toString());
						JSONObject obj1 = new JSONObject(inviteMap);
						inviteeUnRegisteredUserList.add(obj1);
					}
					
					Log.d("inviteeUnRegisteredUserList=========>", inviteeUnRegisteredUserList.toString());
					Log.d("members=======>", members.toString());
					
					try {
						doCreateGroupTalkAction(members);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				} else {
					Toast.makeText(SelectChatFriendsActivity.this, "还没有添加邀请人", Toast.LENGTH_SHORT).show();
				}

			}
		});
		
		try {
			doFetchFriendListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	class MyGVAdapter extends BaseAdapter{

		private Context context;
		private List<Map<String, Object>> list;

		public MyGVAdapter(Context context, List<Map<String, Object>> list) {
			this.context = context;
			this.list = list;
		}
		
		@Override
		public int getCount() {
			return list == null ? 0 : list.size(); 
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			PictureViewHolder1 viewHolder = null;
			if (convertView == null) {
				viewHolder = new PictureViewHolder1();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.gridview_sf, parent, false);
				viewHolder.iv_gridview_sf = (ImageView) convertView
						.findViewById(R.id.iv_gridview_sf);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (PictureViewHolder1) convertView.getTag();
			}

			if (list.get(position).get("icon") != null) {
				ImageLoader.getInstance().displayImage(list.get(position).get("icon").toString(), viewHolder.iv_gridview_sf);
			} else {
				ImageLoader.getInstance().displayImage("drawable://"+R.drawable.no_portrait, viewHolder.iv_gridview_sf);
			}
			return convertView;
		}
		public class PictureViewHolder1 {
			ImageView iv_gridview_sf;
		}
		
	}
	
	private void initWidget() {
		lv_ps1 = (ListView) findViewById(R.id.lv_ps1);
		lv_ps1.setAdapter(adaptersf);
	}
	
	private void doFetchFriendListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("follow", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	FollowItemsParser parser = new FollowItemsParser();
                    	List<UserItem> friends = null;
                    	friends = parser.parse(response.getJSONObject("data"));
                        Log.d("userItems count=====>", userItems.size()+"");
                        adaptersf = new ContactsAdapterSF(SelectChatFriendsActivity.this, getMapList(friends));
                        lv_ps1.setAdapter(adaptersf);
                        lv_ps1.setOnItemClickListener(new OnItemClickListener() {
		    	        	
		    				@Override
		    				public void onItemClick(AdapterView<?> parent, View view, int position,
		    						long id) {
		    					
		    					ImageView iv = (ImageView) view.findViewById(R.id.iv_select_friends);
		    					Map<String, Object> contact = mapList.get(position);
		    					Log.d("contact=======>", contact.toString());
		    					boolean isChecked = Boolean.parseBoolean(contact.get("check").toString());
		    					if (!isChecked) {
		    						iv.setImageResource(R.drawable.select_friends_checked);
		    						contact.put("check", true);
		    						// add this contact to grid
		    						Map<String,Object> item = new HashMap<String, Object>();
		    						Log.d("lvIndex==========>", position+"");
		    						Log.d("uid==========>", contact.get("uid").toString());
		    						item.put("lvIndex", position);
		    						item.put("uid", contact.get("uid").toString());
		    						item.put("icon", contact.get("icon") == null?null:contact.get("icon").toString());
		    						mapList2.add(item);
		    						gvAdapter.notifyDataSetChanged();
		    						
		    					} else {
		    						iv.setImageResource(R.drawable.select_friends);
		    						contact.put("check", false);
		    						// remove this contact grid
//		    						mapList2.remove(position);
		    						int pos = 0;
		    						for (int i=0; i<mapList2.size(); i++) {
		    							if (position == Integer.parseInt(mapList2.get(i).get("lvIndex").toString())) {
		    								pos = i;
		    							}
		    						}

		    						mapList2.remove(pos);
		    						gvAdapter.notifyDataSetChanged();
		    					}
		    					
		    				}
		    			});
                    } else if (status == 0) {
                        Toast.makeText(SelectChatFriendsActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
	            
			}

			@Override
			public void onStart() {
				
			}
        });
	}
	
	private void doCreateGroupTalkAction(List<String> members) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("members", members);
        final ProgressDialog pDialog = new ProgressDialog(this);
        HttpRestClient.post("message/createGroup", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (!response.isNull("data")) {
                    		JSONObject jObj = response.getJSONObject("data");
                    		int sid = jObj.getInt("sid");
    						Intent i = new Intent();
    						i.setClass(SelectChatFriendsActivity.this, ChattingActivity.class);
    						i.putExtra("sid", sid);
    						startActivity(i);
    						finish();
                    	}
    					
                    } else if (status == 0) {
                        Toast.makeText(SelectChatFriendsActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
				if (pDialog.isShowing())
					pDialog.dismiss();
			}

			@Override
			public void onStart() {
				pDialog.setMessage("请稍等...");
				pDialog.show();
			}
        });
	}
	
	private List<Map<String, Object>> getMapList(List<UserItem> friends) {
		
		mapList = new ArrayList<Map<String, Object>>();

		
		ArrayList<Contacts> mylist = new ArrayList<Contacts>();
		
		for (int i = 0; i < friends.size(); i++) {
			String username = friends.get(i).getUsername();
			String uid = friends.get(i).getUid();
			String icon = friends.get(i).getPhoto();
			mylist.add(new Contacts(uid, icon, username, null, PinyinUtils.getAlpha(username), false));
		}
		
		Contacts[] ContactsArray = mylist.toArray(new Contacts[mylist.size()]);
		
		Arrays.sort(ContactsArray, new PinyinComparator());
		
		for (Contacts contacts : ContactsArray) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uid", contacts.getUid());
			map.put("icon", contacts.getIcon());
			map.put("name", contacts.getName());
			map.put("info", contacts.getInfo());
			map.put("check", contacts.getCheck());
			mapList.add(map);
		}
		return mapList;
	}
	
}
