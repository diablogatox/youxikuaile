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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.GameItemsParser;
import com.orfid.youxikuaile.pojo.GameAreaItem;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.widget.HorizontalListView;

public class OnlineSitterPublishActivity extends Activity implements OnClickListener {

	private final static int ADD_SITTER_GAME_SERVER_AREA = 0;
	private ImageButton backBtn;
	private Button saveBtn;
	private HorizontalListView sitterGamesLv;
	private GridView areaGv;
	private TextView gameAreaTv;
	private MyAdapter myAdapter;
	private String selectedGameId;
	private List<GameItem> gameItems = new ArrayList<GameItem>();
	private List<GameAreaItem> tagGameAreas = new ArrayList<GameAreaItem>();
	private List<String> gameAreaNames = new ArrayList<String>();
	private MyGridAdapter gridAdapter;
	
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
		areaGv = (GridView) findViewById(R.id.area_gv);
		
		gridAdapter = new MyGridAdapter(this, R.layout.game_area_item_tag_style, tagGameAreas);
		areaGv.setAdapter(gridAdapter);
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
		
		areaGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final View iv = view.findViewById(R.id.del_indicator_iv);
				iv.setVisibility(View.VISIBLE);
				new AlertDialog.Builder(OnlineSitterPublishActivity.this)
					.setTitle("提示")
					.setMessage("是否删除?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							gridAdapter.remove(gridAdapter.getItem(position));
							gridAdapter.notifyDataSetChanged();
							iv.setVisibility(View.GONE);
						}
						
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							iv.setVisibility(View.GONE);
						}
						
					})
					.show();
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
			if (tagGameAreas.size() <= 0) {
				Toast.makeText(this, "还没有选择游戏区", Toast.LENGTH_SHORT).show();
			} else {
				try {
					doPublishGameSitterAction();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
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
	
	private void doPublishGameSitterAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        final ProgressDialog pDialog = new ProgressDialog(this);
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("gameid", selectedGameId);
        params.put("online", 1);
//        params.put("desc", desc);
        params.put("gamearea", gameAreaNames);
        HttpRestClient.post("peiwan/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (pDialog.isShowing()) pDialog.dismiss();
                    	finish();
                    } else if (status == 0) {
                        Toast.makeText(OnlineSitterPublishActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
				pDialog.setMessage("请稍等...");
				pDialog.show();
			}
        });
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case ADD_SITTER_GAME_SERVER_AREA:
			selectedGameId = data.getStringExtra("gameId");
			Log.d("onActivityResult gameId=====>", selectedGameId);
			String areas = data.getStringExtra("areas");
			Log.d("onActivityResult areas======>", areas);
			try {
				JSONArray jArr = new JSONArray(areas);
				for (int i=0; i<jArr.length(); i++) {
					JSONObject jObj = jArr.getJSONObject(i);
					Log.d("id=====>", jObj.getString("id"));
					Log.d("name=====>", jObj.getString("name"));
					gameAreaNames.add(jObj.getString("name"));
					tagGameAreas.add(new GameAreaItem(jObj.getString("name")));
				}
				gridAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
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

	class MyGridAdapter extends ArrayAdapter<GameAreaItem>{
		
		private List<GameAreaItem> items;
		private GameAreaItem objBean;
		private int resource;
		private Context context;
		
		public MyGridAdapter(Context context, int resource, List<GameAreaItem> arrayList) {
			super(context, resource, arrayList);
			this.items = arrayList;
			this.resource = resource;
			this.context = context;
		}

		
		@Override
		public int getCount() {
			return items == null ? 0: items.size();
		}


		@Override
		public GameAreaItem getItem(int position) {
			return items.get(position);
		}
		

//		@Override
//		public long getItemId(int position) {
//			return Long.parseLong(items.get(position).getId());
//		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PictureViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new PictureViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						resource, parent, false);
				viewHolder.tv_game_bg = (TextView) convertView.findViewById(R.id.game_area_item_tag_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (PictureViewHolder) convertView.getTag();
			}
			
			objBean = items.get(position);
			viewHolder.tv_game_bg.setText(objBean.getName());
			return convertView;
		}
		public class PictureViewHolder {
			TextView tv_game_bg;
		}
		
	}
}
