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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.PlayerItemsParser;
import com.orfid.youxikuaile.pojo.UserItem;

public class NearbyPlayersActivity extends Activity implements OnClickListener {

    private ListView nearbyPlayersLv;
    private TextView emptyTv;
    private ImageButton backBtn;
    private Button filterBtn;
	private List<UserItem> playersItems = new ArrayList<UserItem>();
    private MyAdapter adapter;
    private int gameId = 0;
    private static final int FILTER_GAMES = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby_players);
        initView();
        setListener();
        obtainData();
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		nearbyPlayersLv = (ListView) findViewById(R.id.lv_nearby_players);
		emptyTv = (TextView) findViewById(R.id.npa_empty_hint_tv);
		filterBtn = (Button) findViewById(R.id.filter_btn);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		filterBtn.setOnClickListener(this);
		nearbyPlayersLv.setOnItemClickListener(new OnItemClickListener() {

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
            		intent = new Intent(NearbyPlayersActivity.this, FriendHomeActivity.class);
            	} else {
            		intent = new Intent(NearbyPlayersActivity.this, PublicHomeActivity.class);
            	}
                intent.putExtra("uid", uid);
                intent.putExtra("username", username);
                intent.putExtra("photo", photo);
                intent.putExtra("isFollowed", isFollowed);
                
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
        params.put("mod", 0);
        params.put("gameid", gameId);
        HttpRestClient.post("user/findByDistance", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	PlayerItemsParser parser = new PlayerItemsParser();
                        playersItems = parser.parse(response);
                        Log.d("playersItems count=====>", playersItems.size()+"");
                        adapter = new MyAdapter(NearbyPlayersActivity.this, R.layout.player_item, playersItems);
                        nearbyPlayersLv.setAdapter(adapter);
                        if (playersItems.size() <= 0) {
                        	emptyTv.setText("没有玩家");
                        	emptyTv.setVisibility(View.VISIBLE);
                        }
                    } else if (status == 0) {
                        Toast.makeText(NearbyPlayersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
	
	private class MyAdapter extends ArrayAdapter<UserItem> {
		
		private Context context;
		private List<UserItem> items;
		private int resource;
		private UserItem objBean;

		public MyAdapter(Context context, int resource, List<UserItem> items) {
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
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
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
            return convertView;

		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			TextView userGeoInfoTv;
			TextView userSignatureTv;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.filter_btn:
			Intent intent = new Intent(this, GamesPickerActivity.class);
			startActivityForResult(intent, FILTER_GAMES);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case FILTER_GAMES:
			gameId = Integer.parseInt(data.getStringExtra("gameId"));
			Log.d("gameId returned=====>", gameId+"");
			try {
				doFindUsersByDistanceAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	
	
}
