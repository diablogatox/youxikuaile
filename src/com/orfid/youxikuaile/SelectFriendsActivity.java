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

import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.adapter.ContactsAdapterSF;
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.pojo.Contacts;
import com.orfid.youxikuaile.pojo.UserItem;

public class SelectFriendsActivity extends Activity implements OnClickListener {
	
	private TextView backTv;
	private Button saveBtn;
	private ListView mListView;
	private GridView gv_ps;
	private MyGVAdapter gvAdapter;
	private ContactsAdapterSF adaptersf;
	private List<Map<String, Object>> mapList, mapList2;
	private List<UserItem> userItems = new ArrayList<UserItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_speak);
		initView();
		setListener();
		obtainData();
	}

	private void obtainData() {
		try {
			doFetchFollowListAction();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setListener() {
		
		mapList2 = new ArrayList<Map<String, Object>>();
		gvAdapter = new MyGVAdapter(this, mapList2);
		gv_ps.setAdapter(gvAdapter);
		
		backTv.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
        	
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
					item.put("name", contact.get("name").toString());
					mapList2.add(item);
					gvAdapter.notifyDataSetChanged();
					
				} else {
					iv.setImageResource(R.drawable.select_friends);
					contact.put("check", false);
					// remove this contact grid
//					mapList2.remove(position);
					int pos = 0;
					for (int i=0; i<mapList2.size(); i++) {
						if (position == Integer.parseInt(mapList2.get(i).get("lvIndex").toString())) {
							pos = i;
						}
					}
					mapList2.remove(pos);
					gvAdapter.notifyDataSetChanged();
				}
				
				Log.d("mapList2 size====>", mapList2.size()+"");
				
			}
		});
	}

	private void initView() {
		backTv = (TextView) findViewById(R.id.back_tv);
		saveBtn = (Button) findViewById(R.id.save_btn);
		mListView = (ListView) findViewById(R.id.lv_ps1);
		gv_ps = (GridView) findViewById(R.id.gv_ps);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_tv:
				finish();
				break;
			case R.id.save_btn:
				
				Map inviteMap = new LinkedHashMap();
				
				if (mapList2.size() > 0) {
					List  inviteeUnRegisteredUserList = new LinkedList();
					for (int i=0; i<mapList2.size(); i++) {
						inviteMap.put("uid", mapList2.get(i).get("uid"));
						inviteMap.put("name", mapList2.get(i).get("name"));
						
						JSONObject obj1 = new JSONObject(inviteMap);
						inviteeUnRegisteredUserList.add(obj1);
					}
					
					Log.d("inviteeUnRegisteredUserList=========>", inviteeUnRegisteredUserList.toString());
					
					Intent intent = new Intent();
					intent.putExtra("selected_friends", inviteeUnRegisteredUserList.toString());
					setResult(RESULT_OK, intent);
					finish();
					
				} else {
					Toast.makeText(SelectFriendsActivity.this, "还没有添加邀请人", Toast.LENGTH_SHORT).show();
				}
				
				
				
				
				break;
		}
		
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
		
		//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
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
                        adaptersf = new ContactsAdapterSF(SelectFriendsActivity.this, getMapList(userItems));
                        mListView.setAdapter(adaptersf);
//                        if (feedItems.size() <= 0) {
//                            emptyViewLl.setVisibility(View.VISIBLE);
//                        }

                    } else if (status == 0) {
                        Toast.makeText(SelectFriendsActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
//	            pBar.setVisibility(View.GONE);
//	            loadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
//	            pBar.setVisibility(View.VISIBLE);
//	            loadingHintTv.setVisibility(View.VISIBLE);
			}
        });
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
				convertView = LayoutInflater.from(SelectFriendsActivity.this).inflate(
						R.layout.gridview_sf, parent, false);
				viewHolder.iv_gridview_sf = (ImageView) convertView
						.findViewById(R.id.iv_gridview_sf);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (PictureViewHolder1) convertView.getTag();
			}
			
//			imageLoader.displayImage(AppConstants.MAIN_DOMAIN + "/" + list.get(position).get("icon"), viewHolder.iv_gridview_sf,
//					options, null);
			return convertView;
		}
		public class PictureViewHolder1 {
			ImageView iv_gridview_sf;
		}
		
	}
	
}
