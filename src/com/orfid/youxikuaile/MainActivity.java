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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.parser.RecommendItemsParser;
import com.orfid.youxikuaile.pojo.ActionItem;
import com.orfid.youxikuaile.pojo.RecommendItem;
import com.orfid.youxikuaile.pojo.UserItem;
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
	private TextView nameTv, uidTv, emptyTv, hotEmptyTv;
	private Button newFansCountBtn, totalMsgCountBtn, newFeedMsgCountBtn;
	private ProgressBar mPbar;
	private List<String> msgCountdata = new ArrayList<String>();
    private Handler handler = new Handler();
    private MyAdapter1 myAdapter1;
    private MyAdapter myAdapter;
    private List<UserItem> userItems = new ArrayList<UserItem>();
    private List<RecommendItem> recommendItems = new ArrayList<RecommendItem>();

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
		
		
		if (MainApplication.getInstance().getDbHandler().getRawCount() == 0) {
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
        	try {
    			doFetchMessageCountAction();
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
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
			hotEmptyTv = (TextView) view.findViewById(R.id.hot_empty_view);
			
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
			
			hotRecommendLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					RecommendItem item = myAdapter.getItem(position);
					Intent intent = new Intent(MainActivity.this, RecommendDetailActivity.class);
					intent.putExtra("title", item.getTitle());
					intent.putExtra("content", item.getContent());
					startActivity(intent);
				}
				
			});
//			hotRecommendLv.setAdapter(new MyAdapter());

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
            
            handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						doFetchHotRecommendListAction();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
            	
            }, 500);
			
		} else if (index == 1) {
			
			nearbyPlayersBtn = (ImageButton) view.findViewById(R.id.nearby_players);
            nearbyOrganizationsBtn = (ImageButton) view.findViewById(R.id.nearby_organizations);
            nearbySittersBtn = (ImageButton) view.findViewById(R.id.nearby_sitters);
			followedPublicLv = (ListView) view.findViewById(R.id.followed_public);
			mPbar = (ProgressBar) findViewById(R.id.progress_bar);
			emptyTv = (TextView) view.findViewById(R.id.empty_tv);
			
			nearbyPlayersBtn.setOnClickListener(this);
            nearbyOrganizationsBtn.setOnClickListener(this);
            nearbySittersBtn.setOnClickListener(this);
            
            followedPublicLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Log.d("uid=====>", id+"");
//					Intent intent = new Intent(MainActivity.this, PublicHomeActivity.class);
//	                intent.putExtra("uid", id+"");
//	                intent.putExtra("username", myAdapter1.getItem(position).getUsername());
//	                intent.putExtra("photo", myAdapter1.getItem(position).getPhoto());
//	                intent.putExtra("isFollowed", true);
//	                intent.putExtra("isPublic", true);
//					startActivity(intent);
				}
            	
            });
			
//			followedPublicLv.setAdapter(new MyAdapter1());

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
            
            handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					try {
						doFetchPublicFollowListAction();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
            	
            }, 500);
			
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
            

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
		            try {
						doFetchLatestFeedAction();
					} catch (JSONException e) {
						e.printStackTrace();
					}
                }
            }, 500);

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
	
	
	private class MyAdapter extends ArrayAdapter<RecommendItem> {
		
		private List<RecommendItem> items;
		private RecommendItem objBean;
		private Context context;
		private int resource;

		public MyAdapter(Context context, int resource, List<RecommendItem> items) {
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
		public RecommendItem getItem(int position) {
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
                viewHolder.imgIv = (ImageView) convertView.findViewById(R.id.img_iv);
                viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
                viewHolder.contentTv = (TextView) convertView.findViewById(R.id.content_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getImg() != null) ImageLoader.getInstance().displayImage(objBean.getImg(), viewHolder.imgIv);
            if (objBean.getTitle() != null) viewHolder.titleTv.setText(objBean.getTitle());
            if (objBean.getContent() != null) viewHolder.contentTv.setText(objBean.getContent());
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView imgIv;
			TextView titleTv;
			TextView contentTv;
		}
		
	}
	
	
	private class MyAdapter1 extends ArrayAdapter<UserItem> {
		
		private List<UserItem> items;
		private UserItem objBean;
		private Context context;
		private int resource;

		public MyAdapter1(Context context, int resource, List<UserItem> items) {
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
		public UserItem getItem(int position) {
			return items.get(position);
		}
		
		
		@Override
		public long getItemId(int position) {
			return Long.parseLong(items.get(position).getUid());
		}



		HashMap<Integer,View> lmap = new HashMap<Integer,View>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.userPhotoIv = (ImageView) convertView.findViewById(R.id.public_photo_iv);
                viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.public_name_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getUsername() != null) viewHolder.userNameTv.setText(objBean.getUsername());
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
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
			}

			@Override
			public void onStart() {
				mPbar.setVisibility(View.VISIBLE);
			}
        });
    }
    
    private void doFetchPublicFollowListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("public", 1);
        HttpRestClient.post("follow", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	FollowItemsParser parser = new FollowItemsParser();
                        userItems = parser.parse(response.getJSONObject("data"));
                        Log.d("userItems count=====>", userItems.size()+"");
                        myAdapter1 = new MyAdapter1(MainActivity.this, R.layout.found_public, userItems);
                        followedPublicLv.setAdapter(myAdapter1);
                        if (emptyTv.isShown() && userItems.size() > 0) {
                        	emptyTv.setVisibility(View.GONE);
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
				if (mPbar.isShown() == true) mPbar.setVisibility(View.GONE);
				if (userItems.size() <= 0) {
                	emptyTv.setText("没有关注公共账号");
                	emptyTv.setVisibility(View.VISIBLE);
                } else {
                	emptyTv.setVisibility(View.GONE);
                }
			}

			@Override
			public void onStart() {
				if (mPbar.isShown() == false) mPbar.setVisibility(View.VISIBLE);
				if (followedPublicLv.getChildCount() <= 0) {
					emptyTv.setVisibility(View.VISIBLE);
				}
			}
        });
    }
    
    private void doFetchHotRecommendListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("info", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	RecommendItemsParser parser = new RecommendItemsParser();
                        recommendItems = parser.parse(response.getJSONObject("data"));
                        Log.d("recommendItems count=====>", recommendItems.size()+"");
                        myAdapter = new MyAdapter(MainActivity.this, R.layout.hot_recommend, recommendItems);
                        hotRecommendLv.setAdapter(myAdapter);
                        if (hotEmptyTv.isShown() && recommendItems.size() > 0) {
                        	hotEmptyTv.setVisibility(View.GONE);
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
				if (recommendItems.size() <= 0) {
                	hotEmptyTv.setText("没有热门推荐");
                	hotEmptyTv.setVisibility(View.VISIBLE);
                } else {
                	hotEmptyTv.setVisibility(View.GONE);
                }
			}

			@Override
			public void onStart() {
				if (hotRecommendLv.getChildCount() <= 0) {
					hotEmptyTv.setVisibility(View.VISIBLE);
				}
			}
        });
    }

}
