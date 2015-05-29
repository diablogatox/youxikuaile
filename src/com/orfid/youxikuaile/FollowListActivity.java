package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.pojo.Contacts;
import com.orfid.youxikuaile.pojo.UserItem;

public class FollowListActivity extends Activity implements OnClickListener {
	
	private ImageButton backBtn;
	private TextView loadingHintTv;
	private ProgressBar pBar;
	private ListView mListView;
	private List<UserItem> userItems = new ArrayList<UserItem>();
    private ContactsAdapterSF adapter;
    List<Map<String, Object>> mapList;

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
        params.put("public", 0);
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
//                        adapter = new MyAdapter(FollowListActivity.this, R.layout.follow_item, userItems);
                        adapter= new ContactsAdapterSF(FollowListActivity.this, getMapList(userItems));
                        mListView.setAdapter(adapter);
                        mListView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								final UserItem item = (UserItem) adapter.getItem(position);
								Intent intent = new Intent(FollowListActivity.this, FriendHomeActivity.class);
								String uid = item.getUid();
								String username = item.getUsername();
								String photo = item.getPhoto();
								boolean isFollowed = item.isFollow();
								intent.putExtra("uid", uid);
				                intent.putExtra("username", username);
				                intent.putExtra("photo", photo);
				                intent.putExtra("isFollowed", isFollowed);
								startActivity(intent);
							}
                        	
                        });
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
	
	
	private class ContactsAdapterSF extends BaseAdapter {

		private Context context;
		private List<Map<String, Object>> list;
		ImageLoader imageLoader;
		private DisplayImageOptions options;

		public ContactsAdapterSF(Context context, List<Map<String, Object>> list) {
			this.context = context;
			this.list = list;
			
			imageLoader = ImageLoader.getInstance();
	        imageLoader.init(ImageLoaderConfiguration
					.createDefault(context));
		}

		public int getCount() {
			return list == null ? 0 : list.size();  
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.follow_item, null);
				holder.iv_sf_pic = (ImageView) convertView.findViewById(R.id.user_photo_iv);
				holder.tv_sf_name = (TextView) convertView.findViewById(R.id.user_name_tv);
				holder.catalog = (TextView) convertView.findViewById(R.id.catalogTv1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String catalog = PinyinUtils.converterToFirstSpell(list.get(position).get("name") + "").substring(0, 1);
			if (position == 0) {
				holder.catalog.setVisibility(View.VISIBLE);
				holder.catalog.setText(catalog);
			} else {
				String lastCatalog = PinyinUtils.converterToFirstSpell(list.get(position - 1).get("name") + "").substring(0, 1);
				if (catalog.equals(lastCatalog)) {
					holder.catalog.setVisibility(View.GONE);
				} else {
					holder.catalog.setVisibility(View.VISIBLE);
					holder.catalog.setText(catalog);
				}
			}
			
//			holder.iv_select_friends.setImageResource(R.drawable.select_friends);
//			holder.iv_sf_pic.setImageResource((Integer) list.get(position).get("icon"));
			if (list.get(position).get("icon") != null) {
				ImageLoader.getInstance().displayImage(list.get(position).get("icon").toString(), holder.iv_sf_pic);
			}
			holder.tv_sf_name.setText((String) list.get(position).get("name"));
			

			return convertView;
		}

		public final class ViewHolder {

			public TextView catalog;
			public ImageView iv_sf_pic;
			public TextView tv_sf_name;
		}

	}

}
