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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.parser.EventItemsParser;
import com.orfid.youxikuaile.pojo.EventItem;

public class EventListActivity extends Activity {

	private ListView eventListView;
	private String uid;
	private List<EventItem> eventItems = new ArrayList<EventItem>();
    private MyAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);
		initView();
		setListener();
		obtainData();
	}

	public void stepBack(View v) {
		finish();
	}
	
	private void initView() {
		uid = getIntent().getStringExtra("uid");
		
		eventListView = (ListView) findViewById(R.id.event_list_lv);
	}

	private void setListener() {

	}

	private void obtainData() {
		try {
			doFetchEventListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void doFetchEventListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", uid);
        HttpRestClient.post("huodong", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        EventItemsParser parser = new EventItemsParser();
                        eventItems = parser.parse(response.getJSONObject("data"));
                        adapter = new MyAdapter(EventListActivity.this, R.layout.event_item, eventItems);
                        eventListView.setAdapter(adapter);
                    	 

                    } else if (status == 0) {
                        Toast.makeText(EventListActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	private class MyAdapter extends ArrayAdapter<EventItem> {

		private Context context;
		private int resource;
		private List<EventItem> items;
		private EventItem objBean;
		
		public MyAdapter(Context context, int resource, List<EventItem> items) {
			super(context, resource, items);
			this.context = context;
			this.resource = resource;
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size() > 0 ? items.size() : 0;
		}

		@Override
		public EventItem getItem(int position) {
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
                viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
                viewHolder.eventCtime = (TextView) convertView.findViewById(R.id.event_ctime);
                viewHolder.eventContent = (TextView) convertView.findViewById(R.id.event_content);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            
            viewHolder.eventTitle.setText(objBean.getTitle());
            viewHolder.eventCtime.setText(Utils.covertTimestampToDate(Long.parseLong(objBean.getCtmie()) * 1000, false));
            viewHolder.eventContent.setText(objBean.getContent());
            
            return convertView;

		}
		
		public class ViewHolder {
			TextView eventTitle;
			TextView eventCtime;
			TextView eventContent;
		}
		
		
		
	}

}
