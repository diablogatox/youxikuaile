package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.MessageFeedItem;
import com.orfid.youxikuaile.pojo.UserItem;


public class MessageFeedActivity extends Activity {

	private ListView messageFeedLv;
	private ImageButton backBtn;
	private String data;
	private List<MessageFeedItem> messageFeedItems = new ArrayList<MessageFeedItem>();
	private MyAdapter myAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_feed);
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		messageFeedLv = (ListView) findViewById(R.id.message_feed_lv);
		
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		myAdapter = new MyAdapter(this, R.layout.message_feed_item, messageFeedItems);
		messageFeedLv.setAdapter(myAdapter);
	}

	private void setListener() {
		messageFeedLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					
			}
			
		});
	}

	private void obtainData() {
		if (getIntent() != null) {
			data = getIntent().getStringExtra("msgFeedData");
			
			try {
				JSONArray jArr = new JSONArray(data);
				for (int i=0; i<jArr.length(); i++) {
					MessageFeedItem item = new MessageFeedItem();
					JSONObject jObj = jArr.getJSONObject(i);
					JSONObject jUser = jObj.getJSONObject("user");
					item.setId(jObj.getString("id"));
					item.setContent(jObj.getString("text"));
					item.setPubTime(jObj.getString("sendtime"));
					item.setUser(new UserItem(
							jUser.getString("uid"),
							jUser.getString("username"),
							jUser.getString("photo"),
							jUser.getString("signature"),
							jUser.getString("type")
						)
					);
					
					messageFeedItems.add(item);
				}
				
				myAdapter.notifyDataSetChanged();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private class MyAdapter extends ArrayAdapter<MessageFeedItem> {
		
		private Context context;
		private List<MessageFeedItem> items;
		private int resource;
		private MessageFeedItem objBean;

		public MyAdapter(Context context, int resource, List<MessageFeedItem> items) {
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
		public MessageFeedItem getItem(int position) {
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
                viewHolder.msg_icon_iv  = (ImageView) convertView.findViewById(R.id.msg_icon_iv);
                viewHolder.msg_uname_tv = (TextView) convertView.findViewById(R.id.msg_uname_tv);
                viewHolder.msg_content_tv = (TextView) convertView.findViewById(R.id.msg_content_tv);
                viewHolder.msg_time_tv = (TextView) convertView.findViewById(R.id.msg_time_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            
            UserItem user = objBean.getUser();
            if (user.getPhoto() != null && !user.getPhoto().equals("null")) {
            	ImageLoader.getInstance().displayImage(user.getPhoto(), viewHolder.msg_icon_iv);
            }
           viewHolder.msg_uname_tv.setText(user.getUsername());
           viewHolder.msg_content_tv.setText(objBean.getContent());
           viewHolder.msg_time_tv.setText( Utils.covertTimestampToDate( Long.parseLong(objBean.getPubTime()) * 1000 , false) );
            
            return convertView;

		}
		
		public class ViewHolder {
			ImageView msg_icon_iv;
			TextView msg_uname_tv, msg_content_tv, msg_time_tv;
		}
		
	}
}
