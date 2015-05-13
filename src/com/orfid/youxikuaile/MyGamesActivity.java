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
import com.orfid.youxikuaile.parser.GameItemsParser;
import com.orfid.youxikuaile.pojo.GameItem;

public class MyGamesActivity extends Activity implements OnClickListener {

	private ListView myGamesLv;
	private ImageButton backBtn;
	private Button addBtn, emptyHintBtn;
	private TextView loadingHintTv;
	private View emptyHintLlView;
	private MyAdapter myAdapter;
	private String games;
	private List<GameItem> gameItems = new ArrayList<GameItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_games);
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		myGamesLv = (ListView) findViewById(R.id.my_games_lv);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		addBtn = (Button) findViewById(R.id.add_btn);
		loadingHintTv = (TextView) findViewById(R.id.loading_hint_tv);
		emptyHintLlView = findViewById(R.id.empty_hint_ll_view);
		emptyHintBtn = (Button) findViewById(R.id.empty_hint_btn);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		emptyHintBtn.setOnClickListener(this);
	}

	private void obtainData() {
		try {
			doFetchMyGamesListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.add_btn:
			Intent intent = new Intent(this, AddGameActivity.class);
			intent.putExtra("games", games);
			startActivity(intent);
			finish();
			break;
		}
	}
	
	private void doFetchMyGamesListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("uid", user.get("uid").toString());
        HttpRestClient.post("game/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	games = response.toString();
                    	GameItemsParser parser = new GameItemsParser();
                        gameItems = parser.parse(response);
                        Log.d("gameItems count=====>", gameItems.size()+"");
                        myAdapter = new MyAdapter(MyGamesActivity.this, R.layout.game_item, gameItems);
                        myGamesLv.setAdapter(myAdapter);
                        if (gameItems.size() <= 0) {
        					emptyHintLlView.setVisibility(View.VISIBLE);
                        }
                    } else if (status == 0) {
                        Toast.makeText(MyGamesActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
			public void onFinish() {
            	loadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				if (myGamesLv.getChildCount() <= 0) {
					loadingHintTv.setVisibility(View.VISIBLE);
				}
			}
        });
    }
	
	private class MyAdapter extends ArrayAdapter<GameItem> {
		
		private List<GameItem> items;
		private GameItem objBean;
		private Context context;
		private int resource;

		public MyAdapter(Context context, int resource, List<GameItem> items) {
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
		public GameItem getItem(int position) {
			return items.get(position);
		}
		
		
		@Override
		public long getItemId(int position) {
			return Long.parseLong(items.get(position).getId());
		}



		HashMap<Integer,View> lmap = new HashMap<Integer,View>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.gameIconIv = (ImageView) convertView.findViewById(R.id.game_icon_iv);
                viewHolder.gameNameTv = (TextView) convertView.findViewById(R.id.game_name_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.gameIconIv);
            if (objBean.getName() != null) viewHolder.gameNameTv.setText(objBean.getName());
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView gameIconIv;
			TextView gameNameTv;
		}
		
	}

}
