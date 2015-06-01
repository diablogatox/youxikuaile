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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.FansItemsParser;
import com.orfid.youxikuaile.pojo.UserItem;

public class NewFansActivity extends Activity implements OnClickListener {

    private ListView mListView;
    private ImageButton backBtn;
    private TextView loadingHintTv;
    private ProgressBar mPbar;
    private List<UserItem> fansItems = new ArrayList<UserItem>();
    private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_new_fans);
		initView();
		setListener();
		obtainData();
	}

	private void obtainData() {
		try {
			doFetchNewFansListAction();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.btn_back);
		mListView = (ListView) findViewById(R.id.newfans_list_lv);
		loadingHintTv = (TextView) findViewById(R.id.loading_hint_tv);
		mPbar = (ProgressBar) findViewById(R.id.progress_bar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back:
				finish();
				break;
		}
	}
	
	private void doFetchNewFansListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("message/newfans", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        FansItemsParser parser = new FansItemsParser(true);
                        fansItems = parser.parse(response);
                        Log.d("fansItems count=====>", fansItems.size()+"");
                        adapter = new MyAdapter();
                        mListView.setAdapter(adapter);
//                        if (feedItems.size() <= 0) {
//                            emptyViewLl.setVisibility(View.VISIBLE);
//                        }

                    } else if (status == 0) {
                        Toast.makeText(NewFansActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
	            mPbar.setVisibility(View.GONE);
	            loadingHintTv.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				mPbar.setVisibility(View.VISIBLE);
	            loadingHintTv.setVisibility(View.VISIBLE);
			}
        });
    }
	
	private class MyAdapter extends BaseAdapter {
		
		
		@Override
        public int getCount() {
//            return fansItems.size();
            return fansItems == null ? 0 : fansItems.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

        	ViewHolder holder = null;

//            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.fan_item, parent, false);

                holder = new ViewHolder();
                
                holder.userPhotoIv = (ImageView) convertView.findViewById(R.id.user_photo_iv);
                holder.userNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
                holder.actionBtn = (Button) convertView.findViewById(R.id.action_btn);

//                holder.actionBtn.setOnClickListener(mBuyButtonClickListener);

                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
            
            final UserItem item = fansItems.get(position);
            
            holder.userPhotoIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(NewFansActivity.this, FriendHomeActivity.class);
					String type = item.getType();
					String uid = item.getUid();
					String username = item.getUsername();
					String photo = item.getPhoto();
					boolean isFollowed = item.isFollow();
					Intent intent;
	            	if (type.equals("0")) {
	            		intent = new Intent(NewFansActivity.this, FriendHomeActivity.class);
	            	} else {
	            		intent = new Intent(NewFansActivity.this, PublicHomeActivity.class);
	            	}
	                intent.putExtra("uid", uid);
	                intent.putExtra("username", username);
	                intent.putExtra("photo", photo);
	                intent.putExtra("isFollowed", isFollowed);
	                
	                startActivity(intent);
				}
            	
            });
            if (item.getPhoto() != null && !item.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(item.getPhoto(), holder.userPhotoIv);
            if (item.getUsername() != null) holder.userNameTv.setText(item.getUsername());
            if (item.isFollow() == false) {
            	holder.actionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_fans2));
            	holder.actionBtn.setText("加关注");
            	holder.actionBtn.setClickable(true);
//            	holder.actionBtn.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.d("ddddd======>", item.getUid());
//					}
//            		
//            	});
            }
            
            holder.actionBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d("ddddd======>", item.getUid());
					if (!item.isFollow()) {
						item.setFollow(true);
						notifyDataSetChanged();
						try {
							doFollowUserAction(item.getUid());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Log.d("already followed======>", "followed");
					}
				}
        		
        	});

            return convertView;

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		private  class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			Button actionBtn;
		}
		
	}
		
	private void doFollowUserAction(String uid) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("followUid", uid);
        HttpRestClient.post("follow/addFollow", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	Toast.makeText(NewFansActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(NewFansActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    
}
