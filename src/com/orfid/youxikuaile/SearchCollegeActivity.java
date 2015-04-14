package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orfid.youxikuaile.pojo.CollegeItem;

public class SearchCollegeActivity extends Activity {

	private EditText searchEt;
	private ListView searchResultLv;
	private TextView hintTv;
	private ImageButton backBtn;
	private Handler mHandler = new Handler();
	private List<CollegeItem> collegeItems = new ArrayList<CollegeItem>();
	private MyAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_college);
		initView();
		setListener();
	}

	private void initView() {
		searchEt = (EditText) findViewById(R.id.search_et);
		searchResultLv = (ListView) findViewById(R.id.search_result_lv);
		hintTv = (TextView) findViewById(R.id.hint_tv);
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		
		listAdapter = new MyAdapter(this, R.layout.college_item, collegeItems);
		
		searchResultLv.setAdapter(listAdapter);
	}

	private void setListener() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
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
								doSearchCollegeAction(s.toString());
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
					CollegeItem item = listAdapter.getItem(position);
					Intent intent = new Intent();
					intent.putExtra("collegeId", item.getId());
					intent.putExtra("collegeName", item.getName());
					setResult(RESULT_OK, intent);
					finish();
			}
			
		});

	}
	
	private void doSearchCollegeAction(String keyword) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        if (user != null && user.size() > 0) {
        	params.put("token", user.get("token").toString());
        	params.put("keywords", keyword);
        }
        HttpRestClient.post("school/find", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	if (response.getJSONArray("schools").length() > 0) {
                    		if (hintTv.isShown()) hintTv.setVisibility(View.GONE);
                    		JSONArray jsonArray = response.getJSONArray("schools");
                    		int len = jsonArray.length();
							for (int i=0;i<len;i++){ 
								JSONObject jObj = jsonArray.getJSONObject(i);
								collegeItems.add(new CollegeItem(jObj.getString("id"), jObj.getString("name")));
							}
							listAdapter.notifyDataSetChanged();
							
                    	} else {
                    		hintTv.setText("搜索无此大学");
                    		hintTv.setVisibility(View.VISIBLE);
                    	}
                    } else if (status == 0) {
                        Toast.makeText(SearchCollegeActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
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
	
	private class MyAdapter extends ArrayAdapter<CollegeItem> {

		private Context context;
		private int resource;
		private List<CollegeItem> items;
		private CollegeItem objBean;
		
		public MyAdapter(Context context, int resource, List<CollegeItem> items) {
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
		public CollegeItem getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.parseLong(items.get(position).getId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						resource, parent, false);
				viewHolder.textView = (TextView) convertView.findViewById(R.id.college_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			objBean = items.get(position);
			viewHolder.textView.setText(objBean.getName());
			
			return convertView;
		}
		
		private class ViewHolder {
			TextView textView;
		}
		
	}

}
