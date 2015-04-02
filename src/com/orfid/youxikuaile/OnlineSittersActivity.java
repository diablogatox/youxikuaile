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
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.GameItemsParser;
import com.orfid.youxikuaile.parser.PlayerItemsParser;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.widget.HorizontalListView;

/**
 * Created by Administrator on 2015/2/27.
 */
public class OnlineSittersActivity extends Activity implements GestureDetector.OnGestureListener, View.OnClickListener {

    HorizontalListView sitterGamesLv;
    ImageButton backBtn;
    Button sitterOfflineBtn;
    TextView selectedAreaNameTv;
    Button searchBtn;
    private static final int SELECT_GAME_AREA = 0;
    private String selectedGameArea;
    private View areaSelectBgLl;
    private MyAdapter myAdapter;
	private String selectedGameId;
	private List<GameItem> gameItems = new ArrayList<GameItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online_sitter);

        backBtn = (ImageButton) findViewById(R.id.btn_back);
        sitterOfflineBtn = (Button) findViewById(R.id.btn_sitter_offline);
        sitterGamesLv = (HorizontalListView) findViewById(R.id.lv_sitter_games);
        areaSelectBgLl = findViewById(R.id.area_select_bg_ll);
        selectedAreaNameTv = (TextView) findViewById(R.id.selected_area_name_tv);
        searchBtn = (Button) findViewById(R.id.search_btn);

        backBtn.setOnClickListener(this);
        sitterOfflineBtn.setOnClickListener(this);
        areaSelectBgLl.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        sitterGamesLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("current select game id=====>", myAdapter.getItem(position).getName()+":"+id);
				selectedGameId = id+"";
				myAdapter.setSelection(position);
			}
			
		});
        
        try {
			doFetchSitterGamesListAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_sitter_offline:
                startActivity(new Intent(this, OfflineSittersActivity.class));
                break;
            case R.id.area_select_bg_ll:
            	Log.d("selected gameid======>", selectedGameId);
    			Intent intent = new Intent(this, SearchGameServerAreaActivity.class);
    			intent.putExtra("gameId", selectedGameId);
    			intent.putExtra("isSearch", true);
    			startActivityForResult(intent, SELECT_GAME_AREA);
            	break;
            case R.id.search_btn:
            	if (selectedAreaNameTv.getText().toString().length() > 0) {
            		try {
						doFindGameSittersAction();
					} catch (JSONException e) {
						e.printStackTrace();
					}
            	} else {
            		Toast.makeText(this, "请先选择游戏区", Toast.LENGTH_SHORT).show();
            	}
            	break;
        }
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case SELECT_GAME_AREA:
			selectedGameArea = data.getStringExtra("selectedArea");
			selectedAreaNameTv.setText(selectedGameArea);
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
                        myAdapter = new MyAdapter(OnlineSittersActivity.this, R.layout.sitter_game, gameItems);
                        sitterGamesLv.setAdapter(myAdapter);
                        myAdapter.setSelection(0);
                        selectedGameId = myAdapter.getItem(0).getId();
//                        if (gameItems.size() <= 0) {
//        					emptyHintLlView.setVisibility(View.VISIBLE);
//                        }
                    } else if (status == 0) {
                        Toast.makeText(OnlineSittersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
    
    private void doFindGameSittersAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("mod", 2);
        params.put("gameid", selectedGameId);
        params.put("gamearea", selectedGameArea);
        Log.d("gameid====>", selectedGameId);
        Log.d("gamearea====>", selectedGameArea);
        HttpRestClient.post("user/findByDistance", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	
                    } else if (status == 0) {
                        Toast.makeText(OnlineSittersActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
				
			}
        });
    }
}
