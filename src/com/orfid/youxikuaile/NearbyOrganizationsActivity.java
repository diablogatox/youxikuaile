package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.OrganizationItemsParser;
import com.orfid.youxikuaile.pojo.OrganizationItem;

public class NearbyOrganizationsActivity extends Activity implements OnClickListener {

    private ListView nearbyOrganizationsLv;
    private ImageButton backBtn;
    private TextView emptyTv;
    private List<OrganizationItem> organizationsItems = new ArrayList<OrganizationItem>();
    private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_organizations);
        initView();
        setListener();
        obtainData();
	}

    private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		emptyTv = (TextView) findViewById(R.id.empty_hint_tv);
		nearbyOrganizationsLv = (ListView) findViewById(R.id.lv_nearby_organizations);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		nearbyOrganizationsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OrganizationItem item = adapter.getItem(position);
				Intent intent = new Intent(NearbyOrganizationsActivity.this, EventDetailActivity.class);
				intent.putExtra("event_title", item.getLastEvent().getTitle());
//				intent.putExtra("user_title", item.getName());
				startActivity(intent);
			}
			
		});
	}

	private void obtainData() {
		try {
			doFindUsersByDistanceAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
    private void doFindUsersByDistanceAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("mod", 1);
        HttpRestClient.post("user/findByDistance", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	OrganizationItemsParser parser = new OrganizationItemsParser();
                        organizationsItems = parser.parse(response);
                        Log.d("organizationsItems count=====>", organizationsItems.size()+"");
                        adapter = new MyAdapter(NearbyOrganizationsActivity.this, R.layout.organization_item, organizationsItems);
                        nearbyOrganizationsLv.setAdapter(adapter);
                        if (organizationsItems.size() <= 0) {
                        	emptyTv.setText("没有活动");
                        	emptyTv.setVisibility(View.VISIBLE);
                        }
                    } else if (status == 0) {
                        Toast.makeText(NearbyOrganizationsActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
			public void onFinish() {
            	emptyTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				emptyTv.setVisibility(View.VISIBLE);
			}
        });
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		}
	}
	
	private class MyAdapter extends ArrayAdapter<OrganizationItem> {
		
		private Context context;
		private List<OrganizationItem> items;
		private int resource;
		private OrganizationItem objBean;

		public MyAdapter(Context context, int resource, List<OrganizationItem> items) {
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
		public OrganizationItem getItem(int position) {
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
                viewHolder.userGeoInfoTv = (TextView) convertView.findViewById(R.id.user_geo_info);
                viewHolder.userSignatureTv = (TextView) convertView.findViewById(R.id.last_event_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getName() != null && !objBean.getName().equals("null")) viewHolder.userNameTv.setText(objBean.getName());
            if (objBean.getDistance() != null && !objBean.getDistance().equals("未知距离")) {
            	String geoInfo = objBean.getDistance() + " | " + objBean.getUtime();
            	viewHolder.userGeoInfoTv.setText(geoInfo);
            }
            if (objBean.getLastEvent() != null && !objBean.getLastEvent().equals("null")) viewHolder.userSignatureTv.setText(objBean.getLastEvent().getTitle());
            return convertView;

		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			TextView userGeoInfoTv;
			TextView userSignatureTv;
		}
		
	}
}
