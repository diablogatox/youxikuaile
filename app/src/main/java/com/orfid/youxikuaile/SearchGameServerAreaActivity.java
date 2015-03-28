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
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.pojo.GameAreaItem;

public class SearchGameServerAreaActivity extends Activity {

	private String gameId;
	private EditText searchEt;
	private ListView searchResultLv;
	private GridView areaGv;
	private TextView hintTv;
	private Button confirmBtn;
	private Handler mHandler = new Handler();
	private List<String> gameAreas = new ArrayList<String>();
	private List<GameAreaItem> tagGameAreas = new ArrayList<GameAreaItem>();
	private MyGridAdapter gridAdapter;
	private ArrayAdapter<String> listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_sittergame_serverarea);
		Intent intent = getIntent();
		gameId = intent.getStringExtra("gameId");
		initView();
		setListener();
		obtainData();
	}

	private void initView() {
		searchEt = (EditText) findViewById(R.id.search_et);
		searchResultLv = (ListView) findViewById(R.id.search_result_lv);
		hintTv = (TextView) findViewById(R.id.hint_tv);
		areaGv = (GridView) findViewById(R.id.area_gv);
		confirmBtn = (Button) findViewById(R.id.confirm_btn);
		
		listAdapter = new ArrayAdapter<String>(this, R.layout.game_area_item, gameAreas);
		gridAdapter = new MyGridAdapter(this, R.layout.game_area_item_tag_style, tagGameAreas);
		
		searchResultLv.setAdapter(listAdapter);
		areaGv.setAdapter(gridAdapter);
	}

	private void setListener() {
		searchEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.length() > 0) {
					Log.d("text is ====>", s.toString());
					listAdapter.clear();
					mHandler.removeCallbacksAndMessages(null);
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							try {
								doSearchGameAreaAction(s.toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
					}, 1000);
				} else {
					listAdapter.clear();
				}
			}
			
		});
		
		searchResultLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!areaGv.isShown()) areaGv.setVisibility(View.VISIBLE);
				if (!confirmBtn.isShown()) confirmBtn.setVisibility(View.VISIBLE);
				if (gridAdapter.getCount() < 10) {
//					Log.d("index of======>", tagGameAreas.indexOf(listAdapter.getItem(position))+"");
					for (int i=0; i<tagGameAreas.size(); i++) {
						if (tagGameAreas.get(i).getName().equals(listAdapter.getItem(position))) {
							Toast.makeText(SearchGameServerAreaActivity.this, "不能重复添加", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				
					tagGameAreas.add(new GameAreaItem(listAdapter.getItem(position)));
					gridAdapter.notifyDataSetChanged();

				} else {
					Toast.makeText(SearchGameServerAreaActivity.this, "最多添加10个区", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		areaGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final View iv = view.findViewById(R.id.del_indicator_iv);
				iv.setVisibility(View.VISIBLE);
				new AlertDialog.Builder(SearchGameServerAreaActivity.this)
					.setTitle("提示")
					.setMessage("是否删除?")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							gridAdapter.remove(gridAdapter.getItem(position));
							gridAdapter.notifyDataSetChanged();
							iv.setVisibility(View.GONE);
							if (gridAdapter.getCount() <= 0) {
								confirmBtn.setVisibility(View.GONE);
							}
						}
						
					})
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							iv.setVisibility(View.GONE);
						}
						
					})
					.show();
			}
			
		});
		
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			String jsonString = "[";
			
			@Override
			public void onClick(View v) {
				for (int i=0; i<tagGameAreas.size(); i++) {
					GameAreaItem item = tagGameAreas.get(i);
					jsonString += "{\"id\":\"0\"," + "\"name\":\"" + item.getName() + "\"}";
					if (i != tagGameAreas.size() - 1) jsonString += ",";
				}
				
				jsonString += "]";
				
				Log.d("jsonString==========>", jsonString);
				
				Intent intent = new Intent();
				intent.putExtra("gameId", gameId);
				intent.putExtra("areas", jsonString);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void obtainData() {
		
	}
	
	private void doSearchGameAreaAction(String keyword) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        if (user != null && user.size() > 0) {
        	params.put("token", user.get("token").toString());
        	params.put("id", gameId);
        	params.put("keyword", keyword);
        }
        HttpRestClient.post("game/area", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (response.getJSONArray("data").length() > 0) {
                    		if (hintTv.isShown()) hintTv.setVisibility(View.GONE);
                    		JSONArray jsonArray = response.getJSONArray("data");
                    		int len = jsonArray.length();
							for (int i=0;i<len;i++){ 
								gameAreas.add(jsonArray.get(i).toString());
							}
							listAdapter.notifyDataSetChanged();
							
                    	} else {
                    		hintTv.setText("搜索无此服");
                    		hintTv.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(SearchGameServerAreaActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
			public void onFinish() {
            	hintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				hintTv.setVisibility(View.VISIBLE);
			}
        });
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
