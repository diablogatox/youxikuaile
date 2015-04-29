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
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.FollowItemsParser;
import com.orfid.youxikuaile.parser.MessageSessionItemsParser;
import com.orfid.youxikuaile.parser.RecommendItemsParser;
import com.orfid.youxikuaile.pojo.ActionItem;
import com.orfid.youxikuaile.pojo.MessageItem;
import com.orfid.youxikuaile.pojo.MessageSession;
import com.orfid.youxikuaile.pojo.RecommendItem;
import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.TitlePopup;
import com.orfid.youxikuaile.widget.TitlePopup.OnItemOnClickListener;

public class MainActivity extends Activity implements OnClickListener, AMapLocationListener {

	public static MainActivity instance = null;
	private ViewPager mTabPager;
	private ImageView mTab1, mTab2, mTab3, mTab4, mTab5;
	private ImageView userPicture, mPhotoIv;
	private int currIndex = 0;
	private EditText searchInput;
	private ImageButton searchBtn, addBtn, backBtn, nearbyPlayersBtn, nearbyOrganizationsBtn, nearbySittersBtn;
	private View view, titleBar, edittextBottomLine, searchOverlay, settingBtnView, userInfoTv, 
		feedRlView, newFansRlView, myFollowListRlView, fansListRl, latestFeedFl, mineGamesRlView, sysnotifyRlView,
		mineSittersRlView, mineRechargeRlView, mineGiftRlView, mineFeedRlView, gameLauncher;
	private ArrayList<View> views = new ArrayList<View>();
	private InputMethodManager imm;
	private TitlePopup titlePopup;
	private ListView hotRecommendLv, followedPublicLv, userSearchLv, msgSessionLv;
	private TextView nameTv, uidTv, emptyTv, hotEmptyTv, userSearchEmptyTv, titleTv;
	private Button newFansCountBtn, totalMsgCountBtn, newFeedMsgCountBtn, sysnotifyCountBtn;
	private ProgressBar mPbar;
	private List<String> msgCountdata = new ArrayList<String>();
    private Handler handler = new Handler();
    private MyAdapter1 myAdapter1;
    private MyAdapter myAdapter;
    private List<UserItem> userItems = new ArrayList<UserItem>();
    private List<RecommendItem> recommendItems = new ArrayList<RecommendItem>();
    private LocationManagerProxy mLocationManagerProxy;
    private int currentIndex = 0;
    
    private List<UserItem> friendUserItems = new ArrayList<UserItem>();
    private List<MessageSession> sessionMessageItems = new ArrayList<MessageSession>();
	private MyAdapter2 listAdapter;
	private MyAdapter3 myAdapter3;
	private Handler mHandler = new Handler();

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
		
//		mTabPager.setOffscreenPageLimit(5);
		
		init();
		
		
//		if (MainApplication.getInstance().getDbHandler().getRawCount() == 0) {
//            Intent intent = new Intent(this, SigninActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//        	try {
//    			doFetchMessageCountAction();
//    		} catch (JSONException e) {
//    			e.printStackTrace();
//    		}
//        }
        
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
                        
                        // 获取消息数目
                		doFetchMessageCountAction();
                		// 获取用户位置
                		doFetchUserLocationAction();

                		setup(0);


                    } else if (status == 0) {
//                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();

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

    protected void doFetchUserLocationAction() {
    	//此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60*1000, 100, this);
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
		if (currentIndex == 2) {
			TitlePopup popup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			popup.addAction(new ActionItem(this, "发起群聊", R.drawable.icon_chat_more));
			popup.addAction(new ActionItem(this, "发布动态", R.drawable.icon_pen));
			popup.addAction(new ActionItem(this, "添加好友", R.drawable.icon_add_friend));
			
			popup.setItemOnClickListener(new OnItemOnClickListener() {

				@Override
				public void onItemClick(ActionItem item, int position) {
					switch (position) {
					case 0:
						
						startActivity(new Intent(MainActivity.this, SelectChatFriendsActivity.class));
						
						break;
					case 1:
						
						startActivity(new Intent(MainActivity.this, NewsFeedPublishActivity.class));
						
						break;
					case 2:
						
						startActivity(new Intent(MainActivity.this, AddNewFriendActivity.class));
						
						break;
					}
				}
				
			});
			popup.show(view);
		} else {
			titlePopup.show(view);
		}
	}
	
	private void init() {
		
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.addAction(new ActionItem(this, "发起群聊", R.drawable.icon_chat_more));
		titlePopup.addAction(new ActionItem(this, "添加好友", R.drawable.icon_add_friend));
		
		imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
//		view = views.get(currIndex);
		
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(true);
		
	}
	
	private void setup(int index) {
		
		Log.d("currentIndex==========>", index+"");
		
		currentIndex = index;
		
		view = views.get(index);
		
		if (index == 0) {
			
			listAdapter = new MyAdapter2(this, R.layout.fans_item, friendUserItems);
			
			hotRecommendLv = (ListView) view.findViewById(R.id.hot_recommend);
			titleBar = view.findViewById(R.id.title);
			edittextBottomLine = view.findViewById(R.id.et_bottom_line);
			searchOverlay = view.findViewById(R.id.search_overlay);
			searchBtn = (ImageButton) view.findViewById(R.id.ib_search);
			addBtn = (ImageButton) view.findViewById(R.id.ib_add);
			backBtn = (ImageButton) view.findViewById(R.id.btn_back);
			searchInput = (EditText) view.findViewById(R.id.et_search_input);
			hotEmptyTv = (TextView) view.findViewById(R.id.hot_empty_view);
			userSearchEmptyTv = (TextView) view.findViewById(R.id.user_search_empty_tv);
			userSearchLv = (ListView) view.findViewById(R.id.user_search_result_lv);
			
			userSearchLv.setAdapter(listAdapter);
			
			searchBtn.setOnClickListener(this);
			backBtn.setOnClickListener(this);
			searchOverlay.setOnClickListener(this);
//			userSearchLv.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					Log.d("testddddd======>", "true");
//						UserItem item = listAdapter.getItem(position);
//						String type = item.getType();
//						String uid = item.getUid();
//						String username = item.getUsername();
//						String photo = item.getPhoto();
//						boolean isFollowed = item.isFollow();
//						Intent intent;
//		            	if (type.equals("0")) {
//		            		intent = new Intent(MainActivity.this, FriendHomeActivity.class);
//		            	} else {
//		            		intent = new Intent(MainActivity.this, PublicHomeActivity.class);
//		            	}
//		                intent.putExtra("uid", uid);
//		                intent.putExtra("username", username);
//		                intent.putExtra("photo", photo);
//		                intent.putExtra("isFollowed", isFollowed);
//		                
//		                startActivity(intent);
//				}
//				
//			});
			
			searchInput.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
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
									doSearchFriendUserAction(s.toString());
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							
						}, 1000);
					} else {
						listAdapter.clear();
						if (userSearchEmptyTv.isShown())
							userSearchEmptyTv.setVisibility(View.GONE);
					}
				}
			});
			
			titlePopup.setItemOnClickListener(new OnItemOnClickListener() {

				@Override
				public void onItemClick(ActionItem item, int position) {
					switch (position) {
					case 0:
						
						startActivity(new Intent(MainActivity.this, SelectChatFriendsActivity.class));
						
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
			
			edittextBottomLine = view.findViewById(R.id.et_bottom_line);
			searchOverlay = view.findViewById(R.id.search_overlay);
			searchBtn = (ImageButton) view.findViewById(R.id.ib_search);
			addBtn = (ImageButton) view.findViewById(R.id.ib_add);
			backBtn = (ImageButton) view.findViewById(R.id.btn_back);
			searchInput = (EditText) view.findViewById(R.id.et_search_input);
			
			userSearchEmptyTv = (TextView) view.findViewById(R.id.user_search_empty_tv);
			userSearchLv = (ListView) view.findViewById(R.id.user_search_result_lv);
			
			userSearchLv.setAdapter(listAdapter);
			
			searchBtn.setOnClickListener(this);
			backBtn.setOnClickListener(this);
			searchOverlay.setOnClickListener(this);
			userSearchLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
						UserItem item = listAdapter.getItem(position);
						String type = item.getType();
						String uid = item.getUid();
						String username = item.getUsername();
						String photo = item.getPhoto();
						boolean isFollowed = item.isFollow();
						Intent intent;
		            	if (type.equals("0")) {
		            		intent = new Intent(MainActivity.this, FriendHomeActivity.class);
		            	} else {
		            		intent = new Intent(MainActivity.this, PublicHomeActivity.class);
		            	}
		                intent.putExtra("uid", uid);
		                intent.putExtra("username", username);
		                intent.putExtra("photo", photo);
		                intent.putExtra("isFollowed", isFollowed);
		                
		                startActivity(intent);
				}
				
			});
			
			searchInput.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
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
									doSearchFriendUserAction(s.toString());
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							
						}, 1000);
					} else {
						listAdapter.clear();
						if (userSearchEmptyTv.isShown())
							userSearchEmptyTv.setVisibility(View.GONE);
					}
				}
			});
			
			
			nearbyPlayersBtn = (ImageButton) view.findViewById(R.id.nearby_players);
            nearbyOrganizationsBtn = (ImageButton) view.findViewById(R.id.nearby_organizations);
            nearbySittersBtn = (ImageButton) view.findViewById(R.id.nearby_sitters);
			followedPublicLv = (ListView) view.findViewById(R.id.followed_public);
			mPbar = (ProgressBar) findViewById(R.id.progress_bar);
			emptyTv = (TextView) view.findViewById(R.id.empty_tv);
			titleTv = (TextView) view.findViewById(R.id.tv_title);
			
			nearbyPlayersBtn.setOnClickListener(this);
            nearbyOrganizationsBtn.setOnClickListener(this);
            nearbySittersBtn.setOnClickListener(this);
            
            followedPublicLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Log.d("uid=====>", id+"");
					Intent intent = new Intent(MainActivity.this, PublicHomeActivity.class);
	                intent.putExtra("uid", id+"");
	                intent.putExtra("username", myAdapter1.getItem(position).getUsername());
	                intent.putExtra("photo", myAdapter1.getItem(position).getPhoto());
	                intent.putExtra("isFollowed", true);
					startActivity(intent);
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

			edittextBottomLine = view.findViewById(R.id.et_bottom_line);
			searchOverlay = view.findViewById(R.id.search_overlay);
			searchBtn = (ImageButton) view.findViewById(R.id.ib_search);
			addBtn = (ImageButton) view.findViewById(R.id.ib_add);
			backBtn = (ImageButton) view.findViewById(R.id.btn_back);
			searchInput = (EditText) view.findViewById(R.id.et_search_input);
			
			userSearchEmptyTv = (TextView) view.findViewById(R.id.user_search_empty_tv);
			userSearchLv = (ListView) view.findViewById(R.id.user_search_result_lv);
			
			userSearchLv.setAdapter(listAdapter);
			
			searchBtn.setOnClickListener(this);
			backBtn.setOnClickListener(this);
			searchOverlay.setOnClickListener(this);
			userSearchLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
						UserItem item = listAdapter.getItem(position);
						String type = item.getType();
						String uid = item.getUid();
						String username = item.getUsername();
						String photo = item.getPhoto();
						boolean isFollowed = item.isFollow();
						Intent intent;
		            	if (type.equals("0")) {
		            		intent = new Intent(MainActivity.this, FriendHomeActivity.class);
		            	} else {
		            		intent = new Intent(MainActivity.this, PublicHomeActivity.class);
		            	}
		                intent.putExtra("uid", uid);
		                intent.putExtra("username", username);
		                intent.putExtra("photo", photo);
		                intent.putExtra("isFollowed", isFollowed);
		                
		                startActivity(intent);
				}
				
			});
			
			searchInput.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
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
									doSearchFriendUserAction(s.toString());
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							
						}, 1000);
					} else {
						listAdapter.clear();
						if (userSearchEmptyTv.isShown())
							userSearchEmptyTv.setVisibility(View.GONE);
					}
				}
			});
			
			
//			myAdapter3 = new MyAdapter3(this, R.layout.session_message_item, sessionMessageItems);
			
            feedRlView = findViewById(R.id.rl_feed);
            newFansRlView = findViewById(R.id.rl_new_fans);
            sysnotifyRlView = findViewById(R.id.sysnotify_rl_view);
            newFansCountBtn = (Button) findViewById(R.id.newfans_count_btn);
            sysnotifyCountBtn = (Button) findViewById(R.id.sysnotify_count_btn);
            newFeedMsgCountBtn = (Button) findViewById(R.id.newfeed_count_btn);
            mPbar = (ProgressBar) findViewById(R.id.progress_bar);
            latestFeedFl = findViewById(R.id.latest_feed_fl);
            mPhotoIv = (ImageView) findViewById(R.id.user_photo_iv);
            titleTv = (TextView) view.findViewById(R.id.tv_title);
            msgSessionLv = (ListView) view.findViewById(R.id.msg_session_lv);
            
//            msgSessionLv.setAdapter(myAdapter3);

            feedRlView.setOnClickListener(this);
            newFansRlView.setOnClickListener(this);
            sysnotifyRlView.setOnClickListener(this);

            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作
            
            totalMsgCountBtn.setText("");
            totalMsgCountBtn.setVisibility(View.GONE);
     
            String feedUnreadMsgCount = null, newFansUnreadMsgCount = null, unreadMsgCount, sysnotifyMsgCount;

            if (msgCountdata != null && msgCountdata.size() > 0) {
				feedUnreadMsgCount = msgCountdata.get(1).toString();
				newFansUnreadMsgCount = msgCountdata.get(2).toString();
	            unreadMsgCount = msgCountdata.get(3).toString();
	            sysnotifyMsgCount = msgCountdata.get(4).toString();
	            
	            if (!newFansUnreadMsgCount.equals("0")) {
	            	newFansCountBtn.setText(newFansUnreadMsgCount);
	            	newFansCountBtn.setVisibility(View.VISIBLE);
	            }
	            if (!feedUnreadMsgCount.equals("0")) {
	            	newFeedMsgCountBtn.setText(feedUnreadMsgCount);
	            	newFeedMsgCountBtn.setVisibility(View.VISIBLE);
	            }
	            if (!sysnotifyMsgCount.equals("0")) {
	            	sysnotifyCountBtn.setText(sysnotifyMsgCount);
	            	sysnotifyCountBtn.setVisibility(View.VISIBLE);
	            }
            }
            

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
		            try {
						doFetchLatestFeedAction();
						doFetchMessageSessionsAction();
					} catch (JSONException e) {
						e.printStackTrace();
					}
                }
            }, 500);

        } else if (index == 3) {

        	gameLauncher = findViewById(R.id.game_laucher);
        	
        	gameLauncher.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, ScoreGameActivity.class));
				}
        		
        	});
        	
            handler.removeCallbacksAndMessages(null); // 防止出现无意义的频繁访问接口的动作

        } else if (index == 4) { // 我的
            settingBtnView = findViewById(R.id.btn_settings);
            userInfoTv = findViewById(R.id.tv_user_info);
            myFollowListRlView = findViewById(R.id.my_follow_list_rl_view);
            fansListRl = findViewById(R.id.fans_list_rl);
            userPicture = (ImageView) findViewById(R.id.user_picture);
            nameTv = (TextView) findViewById(R.id.name_tv);
            uidTv = (TextView) findViewById(R.id.uid_tv);
            mineGamesRlView = findViewById(R.id.mine_games_rl_view);
            mineSittersRlView = findViewById(R.id.mine_sitters_rl_view);
            mineRechargeRlView = findViewById(R.id.mine_recharge_rl_view);
            mineGiftRlView = findViewById(R.id.mine_gift_rl_view);
            mineFeedRlView = findViewById(R.id.mine_feed_rl_view);
            
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
            mineGamesRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, MyGamesActivity.class));
				}
            	
            });
            mineSittersRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, SittersActivity.class));
				}
            	
            });
            mineRechargeRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, RechargeActivity.class));
				}
            	
            });
            mineGiftRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, GiftActivity.class));
				}
            	
            });
            mineFeedRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
					startActivity(intent);
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
			if (currentIndex == 0)
				titleBar.setBackgroundResource(R.color.header_bar_bg_color);
			
			if (currentIndex == 1 || currentIndex == 2) titleTv.setVisibility(View.GONE);
			
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
			
			if (currentIndex == 1 || currentIndex == 2) titleTv.setVisibility(View.VISIBLE);
			
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
			
			if (currentIndex == 1 || currentIndex == 2) titleTv.setVisibility(View.VISIBLE);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			searchInput.setText("");
			imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
			
			break;
		case R.id.nearby_players:
            Intent intent = new Intent(this, NearbyPlayersActivity.class);
			startActivity(intent);
			
			break;
        case R.id.nearby_organizations:

            startActivity(new Intent(this, NearbyOrganizationsActivity.class));

            break;
        case R.id.nearby_sitters:

            startActivity(new Intent(this, OnlineSittersActivity.class));

            break;
        case R.id.rl_feed:
        	Intent i = new Intent(this, NewsFeedActivity.class);
        	i.putExtra("feedCount", !newFeedMsgCountBtn.getText().toString().equals("") ? 
        			Integer.parseInt(newFeedMsgCountBtn.getText().toString()) : 0);
            startActivity(i);
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
            
        case R.id.sysnotify_rl_view:
        	
        	startActivity(new Intent(this, SysNotifyActivity.class));
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
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
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
        if (user != null && user.size() > 0) {
        params.put("token", user.get("token").toString());
        }
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
    
    private void doFetchMessageSessionsAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        final HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("message/session", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	MessageSessionItemsParser parser = new MessageSessionItemsParser();
                    	sessionMessageItems = parser.parse(response.getJSONObject("data"));
                    	myAdapter3 = new MyAdapter3(MainActivity.this, R.layout.session_message_item, sessionMessageItems);
                    	msgSessionLv.setAdapter(myAdapter3);
                    	msgSessionLv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								Log.d("enter here======>", "true");
								MessageSession item = myAdapter3.getItem(position);
								String sid = null;
//								if (item.getUsers().length <= 2) {
//									for(int i=0; i<item.getUsers().length; i++) {
//										if (!item.getUsers()[i].equals(user.get("uid").toString())) {
//											uid = item.getUsers()[i].getUid();
//										}
//									}
//								} else {
									sid = item.getId();
//								}
//								Log.d("uid=====xxxx======>", uid);
								Log.d("sid=====xxxx=======>", sid);
								Intent i = new Intent();
	    						i.setClass(MainActivity.this, ChattingActivity.class);
	    						i.putExtra("sid", sid);
//	    						i.putExtra("uid", uid);
	    						startActivity(i);
							}
                    		
                    	});
                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            Double geoLat = amapLocation.getLatitude();
            Double geoLng = amapLocation.getLongitude();   
            Log.d("geoLat=====>", geoLat+"");
            Log.d("getLng=====>", geoLng+"");
            // save geo
            final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
            HashMap user = dbHandler.getUserDetails();
            if (user != null && user.size() > 0) {
            	// local save
            	dbHandler.updateUser(user.get("uid").toString(), new String[] {
            		Constants.KEY_GEOLAT, Constants.KEY_GEOLNG}, new String[] {geoLat+"", geoLng+""});
            	// server save
            	try {
					doSaveUserLocationAction(geoLat, geoLng);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
	}

	private void doSaveUserLocationAction(Double geoLat, Double geoLng) throws JSONException {
		final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        if (user != null && user.size() > 0) {
        	params.put("token", user.get("token").toString());
        	params.put("latitude", geoLat);
        	params.put("longitude", geoLng);
        }
        HttpRestClient.post("user/gps", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("doSaveUserLocation_response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	
                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	private void doSearchFriendUserAction(String id) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        if (user != null && user.size() > 0) {
        params.put("token", user.get("token").toString());
        params.put("id", id);
        }
        HttpRestClient.post("user/find", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                    	Log.d("length========>", response.getJSONArray("data").length() +"");
                    	if (response.getJSONArray("data").length() > 0) {
                    		if (userSearchEmptyTv.isShown()) userSearchEmptyTv.setVisibility(View.GONE);
                    		JSONArray jsonArray = response.getJSONArray("data");
                    		int len = jsonArray.length();
							for (int i=0;i<len;i++) {
								JSONObject user = jsonArray.getJSONObject(i);
								UserItem userItem = new UserItem(
										user.getString("uid"),
										user.getString("username"),
										user.isNull("photo")?null:user.getString("photo"),
										user.isNull("signature")?null:user.getString("signature"),
										user.getString("type"),
										user.getBoolean("isfollow")
								);
								friendUserItems.add(userItem);
							}
							listAdapter.notifyDataSetChanged();
							
                    	} else {
                    		userSearchEmptyTv.setText("无相关信息");
                    	}
                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onStart() {
				if (!userSearchEmptyTv.isShown()) 
					userSearchEmptyTv.setVisibility(View.VISIBLE);
				
				userSearchEmptyTv.setText("搜索中...");
			}
            
        });
    }
	
	private class MyAdapter2 extends ArrayAdapter<UserItem> {
		
		private Context context;
		private List<UserItem> items;
		private int resource;
		private UserItem objBean;

		public MyAdapter2(Context context, int resource, List<UserItem> items) {
			super(context, resource, items);
			this.context = context;
			this.items = items;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public UserItem getItem(int position) {
			return items.get(position);
		}

		HashMap<Integer,View> lmap = new HashMap<Integer,View>();
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.userPhotoIv = (ImageView) convertView.findViewById(R.id.user_photo_iv);
                viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.user_name_tv);
                viewHolder.actionBtn = (Button) convertView.findViewById(R.id.action_btn);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
            	convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            if (objBean.getPhoto() != null && !objBean.getPhoto().equals("null")) ImageLoader.getInstance().displayImage(objBean.getPhoto(), viewHolder.userPhotoIv);
            if (objBean.getUsername() != null) viewHolder.userNameTv.setText(objBean.getUsername());
            if (objBean.isFollow() == false) {
            	viewHolder.actionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_fans2));
            	viewHolder.actionBtn.setText("加关注");
            	viewHolder.actionBtn.setClickable(true);
            } else {
            	viewHolder.actionBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_fans1));
            	viewHolder.actionBtn.setText("已关注");
            	viewHolder.actionBtn.setClickable(false);
            }
            
            viewHolder.actionBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (objBean.isFollow()) return;
					getItem(position).setFollow(true);
					listAdapter.notifyDataSetChanged();
					try {
						doFollowUserAction(objBean.getUid());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
            	
            });
            
            viewHolder.userPhotoIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UserItem item = objBean;
					String type = item.getType();
					String uid = item.getUid();
					String username = item.getUsername();
					String photo = item.getPhoto();
					boolean isFollowed = item.isFollow();
					Intent intent;
	            	if (type.equals("0")) {
	            		intent = new Intent(MainActivity.this, FriendHomeActivity.class);
	            	} else {
	            		intent = new Intent(MainActivity.this, PublicHomeActivity.class);
	            	}
	                intent.putExtra("uid", uid);
	                intent.putExtra("username", username);
	                intent.putExtra("photo", photo);
	                intent.putExtra("isFollowed", isFollowed);
	                
	                startActivity(intent);
				}
            	
            });
            
            return convertView;

		}
		
		public class ViewHolder {
			ImageView userPhotoIv;
			TextView userNameTv;
			Button actionBtn;
		}
		
	}
	
	
	private class MyAdapter3 extends ArrayAdapter<MessageSession> {
		
		private Context context;
		private List<MessageSession> items;
		private int resource;
		private MessageSession objBean;

		public MyAdapter3(Context context, int resource, List<MessageSession> items) {
			super(context, resource, items);
			this.context = context;
			this.items = items;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}

		@Override
		public MessageSession getItem(int position) {
			return items.get(position);
		}

		HashMap<Integer,View> lmap = new HashMap<Integer,View>();
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder = null;
			if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.msgIcon = (ImageView) convertView.findViewById(R.id.msg_icon);
                viewHolder.newmsgHint = (Button) convertView.findViewById(R.id.newmsg_hint);
                viewHolder.sessionName = (TextView) convertView.findViewById(R.id.session_name);
                viewHolder.msgTime = (TextView) convertView.findViewById(R.id.msg_time);
                viewHolder.msgContent = (TextView) convertView.findViewById(R.id.msg_content);
                viewHolder.groupIcon = convertView.findViewById(R.id.group_icon);
                viewHolder.topIcon = (ImageView) convertView.findViewById(R.id.top_center);
                viewHolder.bottomLeftIcon = (ImageView) convertView.findViewById(R.id.bottom_left);
                viewHolder.bottomRightIcon = (ImageView) convertView.findViewById(R.id.bottom_right);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
            	convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            
            String sid = objBean.getId();
            String newmsg = objBean.getNewmsg();
            if (Integer.parseInt(newmsg) > 0) {
            	viewHolder.newmsgHint.setText(newmsg);
            	viewHolder.newmsgHint.setVisibility(View.VISIBLE);
            }
            
            MessageItem msg = objBean.getMessage();
            if (msg != null) {
            	UserItem u = msg.getUser();
            	if (objBean.getType().equals("1")) {
            		if (u.getPhoto() != null && !u.getPhoto().equals("null"))
            			ImageLoader.getInstance().displayImage(u.getPhoto(), viewHolder.msgIcon);
            		if (u.getUsername() != null && !u.getPhoto().equals("null"))
            			viewHolder.sessionName.setText(u.getUsername());
            		viewHolder.msgTime.setText(Utils.covertTimestampToDate( Long.parseLong(msg.getSendtime()) * 1000 ));
            	} else if (objBean.getType().equals("2")) {
            		
            		UserItem[] users = objBean.getUsers();
                	int size = users.length;
                	if (size <= 2) {
                		String photo = null, name = null;
                		final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
                        HashMap user = dbHandler.getUserDetails();
                		for (int i=0; i<users.length; i++) {
                    		if (!users[i].getUid().equals(user.get("uid").toString())) {
                    			photo = users[i].getPhoto();
                    			name = users[i].getUsername();
                    		}
                    	}
                		ImageLoader.getInstance().displayImage(photo, viewHolder.msgIcon);
                		viewHolder.sessionName.setText(name);
                		viewHolder.msgContent.setText(msg.getText());
                	} else {
                		String sessionName = "";
                    	UserItem userItem = msg.getUser();
                    	viewHolder.sessionName.setText(userItem.getUsername());
                    	ImageLoader loader = ImageLoader.getInstance();
                    	if (!users[size-1].getPhoto().equals("null")) loader.displayImage(users[size-1].getPhoto(), viewHolder.topIcon);
                    	if (!users[size-2].getPhoto().equals("null")) loader.displayImage(users[size-2].getPhoto(), viewHolder.bottomLeftIcon);
                    	if (!users[size-3].getPhoto().equals("null")) loader.displayImage(users[size-3].getPhoto(), viewHolder.bottomRightIcon);
                    	viewHolder.msgIcon.setVisibility(View.GONE);
                    	viewHolder.groupIcon.setVisibility(View.VISIBLE);
                	}
                	
                	viewHolder.msgTime.setText(Utils.covertTimestampToDate( Long.parseLong(msg.getSendtime()) * 1000 ));
            	}
            	
            	viewHolder.msgContent.setText(msg.getText());
            	
            } else {
            	UserItem[] users = objBean.getUsers();
            	int size = users.length;
            	if (size <= 2) {
            		String photo = null, name = null;
            		final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
                    HashMap user = dbHandler.getUserDetails();
            		for (int i=0; i<users.length; i++) {
                		if (!users[i].getUid().equals(user.get("uid").toString())) {
                			photo = users[i].getPhoto();
                			name = users[i].getUsername();
                		}
                	}
            		ImageLoader.getInstance().displayImage(photo, viewHolder.msgIcon);
            		viewHolder.sessionName.setText(name);
            		viewHolder.msgContent.setText("[正在聊天]");
            	} else {
            		String sessionName = "";
                	for (int i=0; i<users.length; i++) {
                		UserItem user = users[i];
                		sessionName += user.getUsername() + (i != users.length-1?",":"");
                	}
                	viewHolder.sessionName.setText(sessionName + " 加入聊天室");
                	ImageLoader loader = ImageLoader.getInstance();
                	if (!users[size-1].getPhoto().equals("null")) loader.displayImage(users[size-1].getPhoto(), viewHolder.topIcon);
                	if (!users[size-2].getPhoto().equals("null")) loader.displayImage(users[size-2].getPhoto(), viewHolder.bottomLeftIcon);
                	if (!users[size-3].getPhoto().equals("null")) loader.displayImage(users[size-3].getPhoto(), viewHolder.bottomRightIcon);
                	viewHolder.msgIcon.setVisibility(View.GONE);
                	viewHolder.groupIcon.setVisibility(View.VISIBLE);
            	}
            	
            	
            }
            
            return convertView;

		}
		
		public class ViewHolder {
			ImageView msgIcon;
			TextView sessionName;
			TextView msgTime;
			TextView msgContent;
			Button newmsgHint;
			View groupIcon;
			ImageView topIcon, bottomLeftIcon, bottomRightIcon;
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
                    } else if (status == 0) {
                        Toast.makeText(MainActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

}
