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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.ActionItem;
import com.orfid.youxikuaile.widget.TitlePopup;
import com.orfid.youxikuaile.widget.TitlePopup.OnItemOnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	public static MainActivity instance = null;
	private ViewPager mTabPager;
	private ImageView mTab1, mTab2, mTab3, mTab4, mTab5;
	private ImageView userPicture, mPhotoIv;
	private int currIndex = 0;
	private EditText searchInput;
	private ImageButton searchBtn, addBtn, backBtn, nearbyPlayersBtn, nearbyOrganizationsBtn, nearbySittersBtn;
	private View view, titleBar, edittextBottomLine, searchOverlay, settingBtnView, userInfoTv, 
		feedRlView, newFansRlView, myFollowListRlView, fansListRl, latestFeedFl;
	private ArrayList<View> views = new ArrayList<View>();
	private InputMethodManager imm;
	private TitlePopup titlePopup;
	private ListView hotRecommendLv, followedPublicLv;
	private TextView nameTv, uidTv;
	private Button newFansCountBtn, totalMsgCountBtn, newFeedMsgCountBtn;
	private ProgressBar mPbar;
	private List<String> msgCountdata = new ArrayList<String>();
    private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (MainApplication.getInstance().getDbHandler().getRawCount() == 0) {
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // 自主登录用户更新token
            try {
                doSigninAction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

		setContentView(R.layout.activity_main);
		
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		instance = this;
		
		mTabPager = (ViewPager) findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mTab1 = (ImageView) findViewById(R.id.img_hot);
        mTab2 = (ImageView) findViewById(R.id.img_found);
        mTab3 = (ImageView) findViewById(R.id.img_message);
        mTab4 = (ImageView) findViewById(R.id.img_more);
        mTab5 = (ImageView) findViewById(R.id.img_mine);
        
        totalMsgCountBtn = (Button) findViewById(R.id.totalmsg_count_btn);
        
        mTab1.setOnClickListener(new MyOnClickListener(0));
        mTab2.setOnClickListener(new MyOnClickListener(1));
        mTab3.setOnClickListener(new MyOnClickListener(2));
        mTab4.setOnClickListener(new MyOnClickListener(3));
        mTab5.setOnClickListener(new MyOnClickListener(4));
        
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.frame_hot, null);
        View view2 = mLi.inflate(R.layout.frame_found, null);
        View view3 = mLi.inflate(R.layout.frame_message, null);
        View view4 = mLi.inflate(R.layout.frame_more, null);
        View view5 = mLi.inflate(R.layout.frame_mine, null);
        
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);

        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mPagerAdapter);
		
		init();
		setup(0);
		
		try {
			doFetchMessageCountAction();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
	}

    private void doSigninAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("username", user.get("phone").toString());
        params.put("password", user.get("password").toString());
        HttpRestClient.post("user/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success

                        JSONObject data = response.getJSONObject("data");
                        String uid, token;
                        uid = data.getString("uid");
                        token = response.getString("token");
                        dbHandler.updateUser(uid, Constants.KEY_TOKEN, token);
                        Log.d("updated token=======>", token);

                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			
			setup(arg0);
			
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1));
				if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				} else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 3:
				mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 4) {
					mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5_1));
				}
				break;
			case 4:
				mTab5.setImageDrawable(getResources().getDrawable(R.drawable.menu5));
				if (currIndex == 0) {
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.menu1_1));
				} else if (currIndex == 1) {
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.menu2_1));
				}
				else if (currIndex == 2) {
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.menu3_1));
				}
				else if (currIndex == 3) {
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.menu4_1));
				}
				break;
			}
			currIndex = arg0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index, false);
		}
	};
	
	public void toggleDropdownMenu(View view) {
		titlePopup.show(view);
	}
	
	private void init() {
		
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.addAction(new ActionItem(this, "发起群聊", R.drawable.icon_chat_more));
		titlePopup.addAction(new ActionItem(this, "添加好友", R.drawable.icon_add_friend));
		
		imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
//		view = views.get(currIndex);
		
	}
	
	private void setup(int index) {
		
		Log.d("currentIndex==========>", index+"");
		
		view = views.get(index);
		
		if (index == 0) {
			
			hotRecommendLv = (ListView) view.findViewById(R.id.hot_recommend);
			titleBar = view.findViewById(R.id.title);
			edittextBottomLine = view.findViewById(R.id.et_bottom_line);
			searchOverlay = view.findViewById(R.id.search_overlay);
			searchBtn = (ImageButton) view.findViewById(R.id.ib_search);
			addBtn = (ImageButton) view.findViewById(R.id.ib_add);
			backBtn = (ImageButton) view.findViewById(R.id.btn_back);
			searchInput = (EditText) view.findViewById(R.id.et_search_input);
			
			searchBtn.setOnClickListener(this);
			backBtn.setOnClickListener(this);
			searchOverlay.setOnClickListener(this);
			titlePopup.setItemOnClickListener(new OnItemOnClickListener() {

				@Override
				public void onItemClick(ActionItem item, int position) {
					switch (position) {
					case 0:
						break;
					case 1:
						
						startActivity(new Intent(MainActivity.this, AddNewFriendActivity.class));
						
						break;
					}
				}
				
			});
			
			hotRecommendLv.setAdapter(new MyAdapter());

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
			
		} else if (index == 1) {
			
			nearbyPlayersBtn = (ImageButton) view.findViewById(R.id.nearby_players);
            nearbyOrganizationsBtn = (ImageButton) view.findViewById(R.id.nearby_organizations);
            nearbySittersBtn = (ImageButton) view.findViewById(R.id.nearby_sitters);
			followedPublicLv = (ListView) view.findViewById(R.id.followed_public);
			
			nearbyPlayersBtn.setOnClickListener(this);
            nearbyOrganizationsBtn.setOnClickListener(this);
            nearbySittersBtn.setOnClickListener(this);
			
			followedPublicLv.setAdapter(new MyAdapter1());

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
			
		} else if (index == 2) { // 消息

            feedRlView = findViewById(R.id.rl_feed);
            newFansRlView = findViewById(R.id.rl_new_fans);
            newFansCountBtn = (Button) findViewById(R.id.newfans_count_btn);
            newFeedMsgCountBtn = (Button) findViewById(R.id.newfeed_count_btn);
            mPbar = (ProgressBar) findViewById(R.id.progress_bar);
            latestFeedFl = findViewById(R.id.latest_feed_fl);
            mPhotoIv = (ImageView) findViewById(R.id.user_photo_iv);

            feedRlView.setOnClickListener(this);
            newFansRlView.setOnClickListener(this);

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
            
            totalMsgCountBtn.setText("");
            totalMsgCountBtn.setVisibility(View.GONE);
     
            String feedUnreadMsgCount = null, newFansUnreadMsgCount = null, unreadMsgCount;

            if (msgCountdata != null && msgCountdata.size() > 0) {
				feedUnreadMsgCount = msgCountdata.get(1).toString();
				newFansUnreadMsgCount = msgCountdata.get(2).toString();
	            unreadMsgCount = msgCountdata.get(3).toString();
	            
	            if (!newFansUnreadMsgCount.equals("0")) {
	            	newFansCountBtn.setText(newFansUnreadMsgCount);
	            	newFansCountBtn.setVisibility(View.VISIBLE);
	            }
	            if (!feedUnreadMsgCount.equals("0")) {
	            	newFeedMsgCountBtn.setText(feedUnreadMsgCount);
	            	newFeedMsgCountBtn.setVisibility(View.VISIBLE);
	            }
            }
            
            //if (flagBoolMessage != true) {
	            try {
					doFetchLatestFeedAction();
				} catch (JSONException e) {
					e.printStackTrace();
				}
            //}
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        doFetchMessageCountAction();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, 1000);

        } else if (index == 3) {

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作

        } else if (index == 4) { // 我的
            settingBtnView = findViewById(R.id.btn_settings);
            userInfoTv = findViewById(R.id.tv_user_info);
            myFollowListRlView = findViewById(R.id.my_follow_list_rl_view);
            fansListRl = findViewById(R.id.fans_list_rl);
            userPicture = (ImageView) findViewById(R.id.user_picture);
            nameTv = (TextView) findViewById(R.id.name_tv);
            uidTv = (TextView) findViewById(R.id.uid_tv);
            
            final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
            final HashMap user = dbHandler.getUserDetails();
            String photo = user.get("photo").toString();
            String username = user.get("username").toString();
            String uid = user.get("uid").toString();
            if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(user.get("photo").toString(), userPicture);
            if (username != null) nameTv.setText(username);
            uidTv.setText(uid);
            
            settingBtnView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });
            userInfoTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                }
            });
            myFollowListRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, FollowListActivity.class));
				}
            	
            });
            fansListRl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, FansListActivity.class));
				}
            	
            });

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
        }
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_search:
			
			v.setVisibility(View.GONE);
			addBtn.setVisibility(View.GONE);
			backBtn.setVisibility(View.VISIBLE);
			searchInput.setVisibility(View.VISIBLE);
			edittextBottomLine.setVisibility(View.VISIBLE);
			searchOverlay.setVisibility(View.VISIBLE);
			titleBar.setBackgroundResource(R.color.header_bar_bg_color);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			searchInput.requestFocus();
			imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
			
			break;
		case R.id.btn_back:
			
			v.setVisibility(View.GONE);
			searchInput.setVisibility(View.GONE);
			edittextBottomLine.setVisibility(View.GONE);
			searchOverlay.setVisibility(View.GONE);
			addBtn.setVisibility(View.VISIBLE);
			searchBtn.setVisibility(View.VISIBLE);
			titleBar.setBackgroundDrawable(null);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			searchInput.setText("");
			imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			
			break;
		case R.id.search_overlay:
			
			v.setVisibility(View.GONE);
			searchInput.setVisibility(View.GONE);
			edittextBottomLine.setVisibility(View.GONE);
			backBtn.setVisibility(View.GONE);
			addBtn.setVisibility(View.VISIBLE);
			searchBtn.setVisibility(View.VISIBLE);
			titleBar.setBackgroundDrawable(null);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			searchInput.setText("");
			imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			
			break;
		case R.id.nearby_players:
			
			startActivity(new Intent(this, NearbyPlayersActivity.class));
			
			break;
        case R.id.nearby_organizations:

            startActivity(new Intent(this, NearbyOrganizationsActivity.class));

            break;
        case R.id.nearby_sitters:

            startActivity(new Intent(this, OnlineSittersActivity.class));

            break;
        case R.id.rl_feed:

            startActivity(new Intent(this, NewsFeedActivity.class));
            newFeedMsgCountBtn.setText("");
        	newFeedMsgCountBtn.setVisibility(View.GONE);
        	latestFeedFl.setVisibility(View.GONE);
        	if (msgCountdata != null && msgCountdata.size() > 0) msgCountdata.set(1, "0");
        	
            break;
        case R.id.rl_new_fans:
        	
            startActivity(new Intent(this, NewFansActivity.class));
            newFansCountBtn.setText("");
        	newFansCountBtn.setVisibility(View.GONE);
        	if (msgCountdata != null && msgCountdata.size() > 0) msgCountdata.set(2, "0");

            break;
		}
		
	}
	
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PictureViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new PictureViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.hot_recommend, parent, false);
//				viewHolder.iv_friends_pic = (ImageView) convertView
//						.findViewById(R.id.iv_friends_pic);
//				viewHolder.tv_friends_name = (TextView) convertView
//						.findViewById(R.id.tv_friends_name);
//				viewHolder.tv_music_content = (TextView) convertView
//						.findViewById(R.id.tv_music_content);
//				viewHolder.tv_distance = (TextView) convertView
//						.findViewById(R.id.tv_distance);
//				viewHolder.btn_voice = (Button) convertView
//						.findViewById(R.id.btn_voice);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (PictureViewHolder) convertView.getTag();
			}

//			viewHolder.tv_friends_name.setText("林俊杰");//名字
//			viewHolder.tv_distance.setText(500 + "m"); //距离
//			// 在下面进行判断，并显示或隐藏歌词和语音，实现相应的功能
//			viewHolder.tv_music_content.setText("她静悄悄的来过，她慢慢带走沉默。只是最后的承诺，还是没有带走了"); // 歌词
//			viewHolder.btn_voice.setVisibility(View.GONE);

			return convertView;
		}

		public class PictureViewHolder {
			ImageView iv_friends_pic;
			TextView tv_friends_name;
			TextView tv_music_content;
		}

	}
	
	
	class MyAdapter1 extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PictureViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new PictureViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.found_public, parent, false);
//				viewHolder.iv_friends_pic = (ImageView) convertView
//						.findViewById(R.id.iv_friends_pic);
//				viewHolder.tv_friends_name = (TextView) convertView
//						.findViewById(R.id.tv_friends_name);
//				viewHolder.tv_music_content = (TextView) convertView
//						.findViewById(R.id.tv_music_content);
//				viewHolder.tv_distance = (TextView) convertView
//						.findViewById(R.id.tv_distance);
//				viewHolder.btn_voice = (Button) convertView
//						.findViewById(R.id.btn_voice);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (PictureViewHolder) convertView.getTag();
			}

//			viewHolder.tv_friends_name.setText("林俊杰");//名字
//			viewHolder.tv_distance.setText(500 + "m"); //距离
//			// 在下面进行判断，并显示或隐藏歌词和语音，实现相应的功能
//			viewHolder.tv_music_content.setText("她静悄悄的来过，她慢慢带走沉默。只是最后的承诺，还是没有带走了"); // 歌词
//			viewHolder.btn_voice.setVisibility(View.GONE);

			return convertView;
		}

		public class PictureViewHolder {
			ImageView iv_friends_pic;
			TextView tv_friends_name;
			TextView tv_music_content;
		}

	}

    private void doFetchMessageCountAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("message/count", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	
                    	JSONArray jArray = response.getJSONArray("data");
                    	if (jArray != null) {
                    		for (int i=0; i<jArray.length(); i++) {
                    			msgCountdata.add(jArray.get(i).toString());
                    		}
                    	}
                    	
                        String totalUnreadMsgCount = msgCountdata.get(0).toString();
//                        String feedUnreadMsgCount = msgCountdata.get(1).toString();
//                        String newFansUnreadMsgCount = msgCountdata.get(2).toString();
//                        String unreadMsgCount = msgCountdata.get(3).toString();
                        
                        if (!totalUnreadMsgCount.equals("0")) {
                        	totalMsgCountBtn.setText(totalUnreadMsgCount);
                        	totalMsgCountBtn.setVisibility(View.VISIBLE);
                        }
                        
//                        if (!newFansUnreadMsgCount.equals("0")) {
//                        	newFansCountBtn.setText(newFansUnreadMsgCount);
//                        	newFansCountBtn.setVisibility(View.VISIBLE);
//                        }
//                        String uid, token;
//                        uid = data.getString("uid");
//                        token = response.getString("token");
//                        dbHandler.updateUser(uid, Constants.KEY_TOKEN, token);
//                        Log.d("updated token=======>", token);
                    	

                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    boolean flagBoolMessage = false;
    
    private void doFetchLatestFeedAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("feed/newest", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	JSONObject data = response.getJSONObject("data");
                    	if (!data.isNull("feedid")) {
                    		if (!data.isNull("photo")) {
                    			String photo = data.getString("photo");
                    			ImageLoader.getInstance().displayImage(photo, mPhotoIv);
                    			latestFeedFl.setVisibility(View.VISIBLE);
                    		}
                    	}
                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
			public void onFinish() {
				mPbar.setVisibility(View.GONE);
				flagBoolMessage = true;
			}

			@Override
			public void onStart() {
				mPbar.setVisibility(View.VISIBLE);
			}
        });
    }

}
