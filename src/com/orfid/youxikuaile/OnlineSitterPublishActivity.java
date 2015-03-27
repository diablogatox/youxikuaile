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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.GameItemsParser;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.widget.HorizontalListView;

public class OnlineSitterPublishActivity extends Activity implements OnClickListener {

	private final static int ADD_SITTER_GAME_SERVER_AREA = 0;
	private ImageButton backBtn;
	private Button saveBtn;
	private HorizontalListView sitterGamesLv;
	private TextView gameAreaTv;
	private MyAdapter myAdapter;
	private String selectedGameId;
	private List<GameItem> gameItems = new ArrayList<GameItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_sitter_publish);
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		saveBtn = (Button) findViewById(R.id.btn_publish);
		sitterGamesLv = (HorizontalListView) findViewById(R.id.lv_sitter_games);
		gameAreaTv = (TextView) findViewById(R.id.game_area_tv);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		gameAreaTv.setOnClickListener(this);
		
		sitterGamesLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("current select game id=====>", myAdapter.getItem(position).getName()+":"+id);
				selectedGameId = id+"";
				myAdapter.setSelection(position);
			}
			
		});
	}

	private void obtainData() {
		try {
			doFetchSitterGamesListAction();
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
		case R.id.btn_publish:
			break;
		case R.id.game_area_tv:
			Log.d("selected gameid======>", selectedGameId);
			Intent intent = new Intent(this, SearchGameServerAreaActivity.class);
			intent.putExtra("gameId", selectedGameId);
			startActivityForResult(intent, ADD_SITTER_GAME_SERVER_AREA);
			break;
		}
	}
	
	private void doFetchSitterGamesListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("game/list", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	games = response.toString();
                    	GameItemsParser parser = new GameItemsParser();
                        gameItems = parser.parse(response);
                        Log.d("gameItems count=====>", gameItems.size()+"");
                        myAdapter = new MyAdapter(OnlineSitterPublishActivity.this, R.layout.sitter_game, gameItems);
                        sitterGamesLv.setAdapter(myAdapter);
                        myAdapter.setSelection(0);
                        selectedGameId = myAdapter.getItem(0).getId();
//                        if (gameItems.size() <= 0) {
//        					emptyHintLlView.setVisibility(View.VISIBLE);
//                        }
                    } else if (status == 0) {
                        Toast.makeText(OnlineSitterPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
//            @Override
//			public void onFinish() {
//            	loadingHintTv.setVisibility(View.GONE);
//			}
//
//			@Override
//			public void onStart() {
//				if (myGamesLv.getChildCount() <= 0) {
//					loadingHintTv.setVisibility(View.VISIBLE);
//				}
//			}
        });
    }
	
	private class MyAdapter extends ArrayAdapter<GameItem> {
		
		private List<GameItem> items;
		private GameItem objBean;
		private Context context;
		private int resource;
		private int index;

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

		public void setSelection(int index) {
        	this.index = index;
        	notifyDataSetChanged();
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
            if (objBean.getImg() != null && !objBean.getImg().equals("null")) {
            		ImageLoader.getInstance().displayImage(objBean.getImg(), viewHolder.gameIconIv);
//            		if (index == position) {
//            			viewHolder.gameIconIv.setAlpha(1);
//            		} else {
//            			viewHolder.gameIconIv.setAlpha(0.5f);
//            		}
            }
            if (objBean.getName() != null) {
            	viewHolder.gameNameTv.setText(objBean.getName());
            	if (index==position) {
            		viewHolder.gameNameTv.setTextColor(getResources().getColor(R.color.header_bar_bg_color));
            	} else {
            		viewHolder.gameNameTv.setTextColor(getResources().getColor(R.color.grey));
            	}
            }
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView gameIconIv;
			TextView gameNameTv;
		}
		
	}

}
