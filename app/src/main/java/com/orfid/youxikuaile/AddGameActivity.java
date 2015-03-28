package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.pojo.GameItem;

public class AddGameActivity extends Activity implements OnClickListener {

	private String games;
	private MyAdapter adapter;
	private GridView gamesGv;
	private EditText gameInput;
	private Button gameAddConfirmBtn;
	private ImageButton backBtn;
	private Button saveGameBtn;
	private ProgressDialog pDialog;
	List<GameItem> gameItems = new ArrayList<GameItem>();
	List<GameItem> newAddedGames = new ArrayList<GameItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_game);
		initView();
		setListener();
		obtainData();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		gameAddConfirmBtn.setOnClickListener(this);
		saveGameBtn.setOnClickListener(this);
	}

	private void initView() {
		gamesGv = (GridView) findViewById(R.id.games_gv);
		gameInput = (EditText) findViewById(R.id.et_input_games);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		gameAddConfirmBtn = (Button) findViewById(R.id.btn_add_games_sure);
		saveGameBtn = (Button) findViewById(R.id.save_game_btn);
	}

	private void obtainData() {
		Intent intent = getIntent();
		games = intent.getStringExtra("games");
		
		if (games != null) {
			GameJSONParser parser = new GameJSONParser();
			JSONObject object = null;
			try {
				object = new JSONObject(games);
				gameItems = parser.parse(object);
				adapter = new MyAdapter(this, R.layout.game_item_tag_style, gameItems);
				gamesGv.setAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	class MyAdapter extends ArrayAdapter<GameItem>{
		
		private List<GameItem> items;
		private GameItem objBean;
		
		public MyAdapter(Context context, int resource, List<GameItem> arrayList) {
			super(context, resource, arrayList);
			this.items = arrayList;
		}

		
		@Override
		public int getCount() {
			return items == null ? 0: items.size();
		}


		@Override
		public GameItem getItem(int position) {
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
				convertView = LayoutInflater.from(AddGameActivity.this).inflate(
						R.layout.game_item_tag_style, parent, false);
				viewHolder.tv_game_bg = (TextView) convertView.findViewById(R.id.game_item_tag_btn);
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

	private class GameJSONParser {

		List<GameItem> listArray;
		
		public List<GameItem> parse(JSONObject jObject) {
			
			JSONArray jGames = null;
			try {			
				jGames = jObject.getJSONArray("data");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return getGames(jGames);
		}
		
		
		private List<GameItem> getGames(JSONArray jGames) {
			int gameCount = jGames.length();
			List<GameItem> gameList = new ArrayList<GameItem>();
			GameItem game = null;	

			for(int i=0; i<gameCount;i++) {
				try {
					game = getFriend((JSONObject)jGames.get(i));
					gameList.add(game);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			return gameList;
		}
		
		private GameItem getFriend(JSONObject jGame){

			GameItem game = new GameItem();
			String id = "";
			String name = "";
			
			try {
				id = jGame.getString("id");
				name = jGame.getString("name");

				game.setId(id);
				game.setName(name);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return game;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.btn_add_games_sure:
			if (gameInput.getText().toString().trim().length() <= 0) {
				Toast.makeText(this, "游戏名称不能为空", Toast.LENGTH_SHORT).show();
			} else {
				gameItems.add(new GameItem(gameInput.getText().toString().trim(), null));
				newAddedGames.add(new GameItem(gameInput.getText().toString().trim(), null));
				adapter.notifyDataSetChanged();
				gamesGv.smoothScrollToPosition(gameItems.size() - 1);
				gameInput.setText("");
				saveGameBtn.setEnabled(true);
			}
			break;
		case R.id.save_game_btn:
			Log.d("save btn clicked=====>", "true");
			if (newAddedGames.size() > 0) {
				pDialog = new ProgressDialog(this);
				pDialog.setTitle("保存中...");
				pDialog.show();
				for (int i=0; i<newAddedGames.size(); i++) {
					GameItem item = newAddedGames.get(i);
					try {
						doAddGameAction(i, item);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
			break;
		}
	}
	
	private void doAddGameAction(final int index, GameItem item) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("name", item.getName());
        HttpRestClient.post("game/add", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                       if (index == newAddedGames.size() - 1) {
                    	   newAddedGames.clear();
	           				pDialog.dismiss();
	           				Toast.makeText(AddGameActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                       }
                    } else if (status == 0) {
                        Toast.makeText(AddGameActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            

        });
    }

}
