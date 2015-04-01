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
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.GameSitterItemsParser;
import com.orfid.youxikuaile.pojo.ActionItem;
import com.orfid.youxikuaile.pojo.GameAreaItem;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.pojo.GameSitterItem;
import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.TitlePopup;
import com.orfid.youxikuaile.widget.TitlePopup.OnItemOnClickListener;

public class SittersActivity extends Activity implements OnClickListener {

	private TitlePopup titlePopup;
	private ImageButton backBtn, addSitterBtn;
	private ListView mListView;
	private List<GameSitterItem> gameSitterItems = new ArrayList<GameSitterItem>();
	private MyAdapter adapter;
	private static final int ONLINE_SITTER_PUBLISH = 0;
	private static final int OFFLINE_SITTER_PUBLISH = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sitters);
		initView();
		setListener();
		obtainData();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		addSitterBtn.setOnClickListener(this);
		
		titlePopup.setItemOnClickListener(new OnItemOnClickListener() {

			@Override
			public void onItemClick(ActionItem item, int position) {
				switch (position) {
				case 0:
					Intent i1 = new Intent(SittersActivity.this, OnlineSitterPublishActivity.class);
					startActivityForResult(i1, ONLINE_SITTER_PUBLISH);
					break;
				case 1:
					Intent i2 = new Intent(SittersActivity.this, OfflineSitterPublishActivity.class);
					startActivityForResult(i2, OFFLINE_SITTER_PUBLISH);
					break;
				}
			}
			
		});
		
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				View overlayView = view.findViewById(R.id.overlay_rl);
//				overlayView.setVisibility(View.VISIBLE);
//				
//			}
//			
//		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case ONLINE_SITTER_PUBLISH:
			try {
				doFetchSitterGameListAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case OFFLINE_SITTER_PUBLISH:
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
		
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		addSitterBtn = (ImageButton) findViewById(R.id.add_sitter_btn);
		mListView = (ListView) findViewById(R.id.sitters_lv);
		
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.addAction(new ActionItem("线上陪玩发布"));
		titlePopup.addAction(new ActionItem("线下陪玩发布"));

	}

	private void obtainData() {
		try {
			doFetchSitterGameListAction();
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
		case R.id.add_sitter_btn:
			titlePopup.show(v);
			break;
		}
	}

	private void doFetchSitterGameListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("peiwan", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success           	
                    	GameSitterItemsParser parser = new GameSitterItemsParser();
                    	gameSitterItems = parser.parse(response.getJSONObject("data"));
                    	adapter = new MyAdapter(SittersActivity.this, R.layout.game_sitter_item, gameSitterItems);
                    	mListView.setAdapter(adapter);
                    	Log.d("items count=====>", gameSitterItems.size()+"");
                    } else if (status == 0) {
                        Toast.makeText(SittersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	private class MyAdapter extends ArrayAdapter<GameSitterItem> {
		
		private List<GameSitterItem> items;
		private GameSitterItem objBean;
		private Context context;
		private int resource;

		public MyAdapter(Context context, int resource, List<GameSitterItem> items) {
			super(context, resource, items);
			this.items = items;
			this.context = context;
			this.resource = resource;
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			if(observer != null){
				super.unregisterDataSetObserver(observer);
			}
		}
		
		@Override
		public int getCount() {
			return items == null ? 0: items.size();
		}

		@Override
		public GameSitterItem getItem(int position) {
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
                viewHolder.actionRightArrowIv = (ImageView) convertView.findViewById(R.id.action_right_arrow_iv);
                viewHolder.gameSitterGameiconIv = (ImageView) convertView.findViewById(R.id.game_sitter_gameicon_iv);
                viewHolder.gameSitterGamenameTv = (TextView) convertView.findViewById(R.id.game_sitter_gamename_tv);
                viewHolder.gameSitterUtimeTv = (TextView) convertView.findViewById(R.id.game_sitter_utime_tv);
                viewHolder.gameSitterDescTv = (TextView) convertView.findViewById(R.id.game_sitter_desc_tv);
                viewHolder.gameSitterAreaGv = (GridView) convertView.findViewById(R.id.game_sitter_area_gv);
                viewHolder.overlayView = convertView.findViewById(R.id.overlay_rl);
                viewHolder.sitterDeleteIv = (ImageView) convertView.findViewById(R.id.sitter_delete_iv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            
            GameItem game = objBean.getGame();
            if (game.getId().equals("") || game.getId().equals("null") || game.getId() == null) {
            	UserItem user = objBean.getUser();
            	if (user.getPhoto() != null && !user.getPhoto().equals("null")) {
	            	ImageLoader.getInstance().displayImage(user.getPhoto(), viewHolder.gameSitterGameiconIv);
	            }
	            viewHolder.gameSitterGamenameTv.setText(user.getUsername());
	            if (objBean.getDesc() != null && !objBean.getDesc().equals("null")) {
	            	viewHolder.gameSitterDescTv.setText(objBean.getDesc());
	            } else {
	            	viewHolder.gameSitterDescTv.setVisibility(View.GONE);
	            }
	            viewHolder.gameSitterUtimeTv.setText(Util.covertTimestampToDate(Long.parseLong(objBean.getUtime()) * 1000));
            } else {
	            Log.d("game icon======>", game.getPhoto());
	            if (game.getPhoto() != null && !game.getPhoto().equals("null")) {
	            	ImageLoader.getInstance().displayImage(game.getPhoto(), viewHolder.gameSitterGameiconIv);
	            }
	            viewHolder.gameSitterGamenameTv.setText(game.getName());
	            if (objBean.getDesc() != null && !objBean.getDesc().equals("null")) {
	            	viewHolder.gameSitterDescTv.setText(objBean.getDesc());
	            } else {
	            	viewHolder.gameSitterDescTv.setVisibility(View.GONE);
	            }
	            viewHolder.gameSitterUtimeTv.setText(Util.covertTimestampToDate(Long.parseLong(objBean.getUtime()) * 1000));
	            if (objBean.getAreas().size() > 0) {
		            MyGridAdapter gridAdapter = new MyGridAdapter(context, R.layout.game_area_item_tag_style, objBean.getAreas());
		    		viewHolder.gameSitterAreaGv.setAdapter(gridAdapter);
		    		viewHolder.gameSitterAreaGv.setVisibility(View.VISIBLE);
	            }
            }

            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView actionRightArrowIv;
			ImageView gameSitterGameiconIv;
			TextView gameSitterGamenameTv;
			TextView gameSitterUtimeTv;
			TextView gameSitterDescTv;
			GridView gameSitterAreaGv;
			View overlayView;
			ImageView sitterModifyIv;
			ImageView sitterDeleteIv;
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
		public void unregisterDataSetObserver(DataSetObserver observer) {
			if(observer != null){
				super.unregisterDataSetObserver(observer);
			}
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
