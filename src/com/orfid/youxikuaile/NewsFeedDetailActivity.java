package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.pojo.FeedItem;
import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.MyGridView;
import com.orfid.youxikuaile.widget.PagerSlidingTabStrip;

public class NewsFeedDetailActivity extends FragmentActivity implements OnClickListener {

	private static final int FORWARD_ACTION = 0;
	private static final int REPLY_ACTION = 1;
	private static final int PRAISE_ACTION = 2;
	
	private ViewPager pager;
    private PagerSlidingTabStrip pagertab;
    private MyPageAdapter pageAdapter;
    private ImageButton backBtn;
    private ImageView photoIv;
    private TextView nameTv, timeTv, contentTv, praiseNumTv;
    private View forwardArea;
    private ImageView forwardIcon;
    private TextView forwardContent;
    private MyGridView imagesGv;
    View rlGvWrapper, forward_rl_view, reply_rl_view, praise_rl_view;
    
    private int forwardNum, commentNum, praiseCount;
    private long feedId;
    private String content, ct, icon;
    private boolean hasForward, forwardHasImg;
    private int wh;
    private String uid, name, photo;
    private boolean isFollowed;
    
    List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
    private GridViewAdapter gvAdapter;
	private long fetchNeededFeedId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed_detail);
		init();
		setListener();
		obtainData();
	}

	private void obtainData() {
		Intent intent = getIntent();
		fetchNeededFeedId  = intent.getLongExtra("fetchNeededFeedId", 0);
		if (fetchNeededFeedId <= 0) {
			feedId = intent.getLongExtra("feedId", 0);
			forwardNum = intent.getIntExtra("forwardNum", 0);
			commentNum = intent.getIntExtra("commentNum", 0);
			photo = intent.getStringExtra("photo");
			name = intent.getStringExtra("name");
			uid = intent.getStringExtra("uid");
			isFollowed = intent.getBooleanExtra("isFollowed", false);
			String time = intent.getStringExtra("time");
			content = intent.getStringExtra("content");
			String imgs = intent.getStringExtra("imgs");
			praiseCount = intent.getIntExtra("praiseNum", 0);
			hasForward = intent.getBooleanExtra("hasForward", false);
			forwardHasImg = intent.getBooleanExtra("forwardHasImg", false);
			ct = intent.getStringExtra("forwardContent");
			icon = intent.getStringExtra("forwardIcon");
	
			if (imgs != null) {
				rlGvWrapper.setVisibility(View.VISIBLE);
				JSONArray jImgItemsArr = null;
				try {
					jImgItemsArr = new JSONArray(imgs);
					for (int i=0; i<jImgItemsArr.length(); i++) {
		                JSONObject jFile = jImgItemsArr.getJSONObject(i);
		                Log.d("image url==========>", jFile.getString("url"));
		                imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
		            }
					initImgAttachment(imagesGv, imgItems);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			if (hasForward == true) {
				if (forwardHasImg == true) {
					ImageLoader.getInstance().displayImage(icon, forwardIcon);
				}
				SpannableStringBuilder sb = Utils.handlerFaceInContent(this, forwardContent,
	                    ct);
				forwardContent.setText(sb);
				forwardArea.setVisibility(View.VISIBLE);
			}
			
			if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, photoIv);
			nameTv.setText(name);
			timeTv.setText(Utils.covertTimestampToDate(Long.parseLong(time) * 1000, false));
			SpannableStringBuilder sb = Utils.handlerFaceInContent(this, contentTv,
	                content);
			contentTv.setText(sb);
			praiseNumTv.setText(praiseCount+" 赞");
			
			List<Fragment> fragments = getFragments();
	        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
	        pager.setAdapter(pageAdapter);
	        pagertab.setViewPager(pager);
	        pagertab.setScrollOffset(0);
	        pagertab.setTextColorResource(R.color.grey);
		} else {
			feedId = fetchNeededFeedId;
			try {
				doFetchFeedAction(fetchNeededFeedId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		forward_rl_view.setOnClickListener(this);
		reply_rl_view.setOnClickListener(this);
		praise_rl_view.setOnClickListener(this);
		imagesGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(NewsFeedDetailActivity.this, PhotoDetailActivity.class);
				intent.putExtra("url", gvAdapter.getItem(position).getUrl());
				startActivity(intent);
				
			}
			
		});
		
		photoIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NewsFeedDetailActivity.this, FriendHomeActivity.class);
				intent.putExtra("uid", uid);
                intent.putExtra("username", name);
                intent.putExtra("photo", photo);
                intent.putExtra("isFollowed", isFollowed);
				startActivity(intent);
			}
			
		});
	}

	private void init() {
		backBtn = (ImageButton) findViewById(R.id.back_btn);
		pager = (ViewPager) findViewById(R.id.activity_main_pager);
        pagertab = (PagerSlidingTabStrip) findViewById(R.id.activity_main_pagertabstrip);
        photoIv = (ImageView) findViewById(R.id.photo);
        nameTv = (TextView) findViewById(R.id.name);
        timeTv = (TextView) findViewById(R.id.time);
        contentTv = (TextView) findViewById(R.id.content);
        praiseNumTv = (TextView) findViewById(R.id.praise_num);
        forwardArea = findViewById(R.id.forward_area);
        forwardIcon = (ImageView) findViewById(R.id.forward_icon);
        forwardContent = (TextView) findViewById(R.id.forward_content);
        imagesGv = (MyGridView) findViewById(R.id.gv_images);
        rlGvWrapper = findViewById(R.id.rl_gv_wrapper);
        forward_rl_view = findViewById(R.id.forward_rl_view);
        reply_rl_view = findViewById(R.id.reply_rl_view);
        praise_rl_view = findViewById(R.id.praise_rl_view);
        
        wh = (Utils.getScreenWidth(this)-Utils.Dp2Px(this, 99))/3;
	}
	
	class MyPageAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;


        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;

        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
            	return forwardNum + " 转发";
            case 1:
            	return commentNum + " 评论";
            default:
            	return null;
            }
        }
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(CommonListFragment.newInstance(feedId, 0));
        fList.add(CommonListFragment.newInstance(feedId, 1));
        return fList;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.forward_rl_view:
			if (hasForward) {
				Intent intent = new Intent(NewsFeedDetailActivity.this, NewsFeedForwardActivity.class);
				intent.putExtra("feedId", feedId);
				intent.putExtra("content", ct);
				intent.putExtra("photo", icon);
				startActivityForResult(intent, FORWARD_ACTION);
			} else {
				String photo = null;
				if (imgItems.size() > 0) {
					photo = imgItems.get(imgItems.size() - 1).getUrl();
				}
				
				Intent intent = new Intent(NewsFeedDetailActivity.this, NewsFeedForwardActivity.class);
				intent.putExtra("feedId", feedId);
				intent.putExtra("content", content);
				intent.putExtra("photo", photo);
				startActivityForResult(intent, FORWARD_ACTION);
			}
			break;
		case R.id.reply_rl_view:
			Intent intent2 = new Intent(NewsFeedDetailActivity.this, NewsFeedReplyActivity.class);
			intent2.putExtra("feedId", feedId);
			startActivityForResult(intent2, REPLY_ACTION);
			break;
		case R.id.praise_rl_view:
			try {
				doPraiseFeedAction();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	private void initImgAttachment(MyGridView myGv, List<FeedAttachmentImgItem> items) {
        int w=0;
        switch (items.size()) {
            case 1:
                w=wh;
                myGv.setNumColumns(1);
                break;
            case 2:
            case 4:
                w=2*wh+Utils.Dp2Px(this, 2);
                myGv.setNumColumns(2);
                break;
            case 3:
            case 5:
            case 6:
                w=wh*3+Utils.Dp2Px(this, 2)*2;
                myGv.setNumColumns(3);
                break;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myGv.setLayoutParams(lp);
        gvAdapter = new GridViewAdapter(this, items);
        myGv.setAdapter(gvAdapter);
    }
	
	public class GridViewAdapter extends BaseAdapter {

        Context context;
        List<FeedAttachmentImgItem> list;
        private int wh;

        public GridViewAdapter(Context context, List<FeedAttachmentImgItem> data) {
            this.context=context;
            this.wh=(Utils.getScreenWidth(context)-Utils.Dp2Px(context, 99))/3;
            this.list=data;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public FeedAttachmentImgItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(list.get(position).getId());
        }


        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            Holder holder;
            if (view==null) {
                view=LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
                holder=new Holder();
                holder.imageView=(ImageView) view.findViewById(R.id.imageView);
                view.setTag(holder);
            }else {
                holder= (Holder) view.getTag();
            }
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(wh,wh);
            view.setLayoutParams(param);
            Log.d("before render image url======>", list.get(position).getUrl());
            ImageLoader.getInstance().displayImage(list.get(position).getUrl(), holder.imageView);

            return view;
        }

        class Holder{
            ImageView imageView;
        }


    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
		case FORWARD_ACTION:
			forwardNum += 1;
			List<Fragment> fragments = getFragments();
	        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
	        pager.setAdapter(pageAdapter);
	        pagertab.setViewPager(pager);
//	        pagertab.setScrollOffset(0);
	        pagertab.setTextColorResource(R.color.grey);
			break;
		case REPLY_ACTION:
			commentNum += 1;
			List<Fragment> fragments2 = getFragments();
	        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments2);
	        pager.setAdapter(pageAdapter);
	        pagertab.setViewPager(pager);
	        pager.setCurrentItem(1);
	        pagertab.setTextColorResource(R.color.grey);
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void doPraiseFeedAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", feedId);
        HttpRestClient.post("feed/praise", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	praiseCount += 1;
//                    	praiseNumTv.setText(praiseCount+" 赞");
                    	Toast.makeText(NewsFeedDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
	
	
	private void doFetchFeedAction(long id) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", id);
        HttpRestClient.post("feed/view", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
//                    	Toast.makeText(NewsFeedDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    	 long feedId = 0;
                         int commentCount = 0, forwardCount = 0, praiseCount = 0, type = 0;
                         boolean isPraised = false;
                         String contentText = null, publishTime = null;
                         UserItem user = null;
                         FeedItem reference = null;
                         List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
                         List<FeedAttachmentImgItem> imgItems2 = new ArrayList<FeedAttachmentImgItem>();
                         
                    	JSONObject jFeedItem = response.getJSONObject("data");
                    	 feedId = Long.parseLong(jFeedItem.getString("feedid"));
                    	 content = jFeedItem.getString("text");
                         commentNum = Integer.parseInt(jFeedItem.getString("commentcount"));
                         forwardNum = Integer.parseInt(jFeedItem.getString("forwardcount"));
             			
                         praiseCount = Integer.parseInt(jFeedItem.getString("praisecount"));
                         publishTime = jFeedItem.getString("publishtime");
                         type = Integer.parseInt(jFeedItem.getString("type"));
                         isPraised = jFeedItem.getBoolean("is_praised");
//                         if (!jFeedItem.isNull("files") && !jFeedItem.getString("files").equals("[]")) {
//                             JSONArray jImgItemsArr = jFeedItem.getJSONArray("files");
//                             for (int i=0; i<jImgItemsArr.length(); i++) {
//                                 JSONObject jFile = jImgItemsArr.getJSONObject(i);
//                                 Log.d("image url==========>", jFile.getString("url"));
//                                 imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
//                             }
//                             initImgAttachment(imagesGv, imgItems);
//                         }
                         JSONObject jUserObj = jFeedItem.getJSONObject("user");
                         String photo = null;
//                         if (!jUserObj.isNull("photo")) photo = jUserObj.getString("photo");
//                         user = new UserItem(jUserObj.getString("uid"), jUserObj.has("birthday")?jUserObj.getString("birthday"):""
//                                 , jUserObj.has("sex")?jUserObj.getString("sex"):"", jUserObj.getString("username"), photo
//                                 , jUserObj.has("signature")?jUserObj.getString("signature"):"", jUserObj.has("isFollow")?jUserObj.getBoolean("isFollow"):false, null, null);
                         
//                         if (!jFeedItem.isNull("reference") && !jFeedItem.getString("reference").equals("[]")) {
//                         	JSONObject ref = jFeedItem.getJSONArray("reference").getJSONObject(0);
//                         	JSONObject u = ref.getJSONObject("user");
//                         	if (!ref.isNull("files") && !ref.getString("files").equals("[]")) {
//                                 JSONArray jImgItemsArr = ref.getJSONArray("files");
//                                 for (int i=0; i<jImgItemsArr.length(); i++) {
//                                     JSONObject jFile = jImgItemsArr.getJSONObject(i);
//                                     Log.d("image url==========>", jFile.getString("url"));
//                                     imgItems2.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
//                                 }
//                             }
//                         	reference = new FeedItem(
//                         			Long.parseLong(ref.getString("feedid")),
//                         			new UserItem(u.getString("uid"), u.getString("username"), u.getString("photo"), u.getString("signature"), u.getString("type")),
//                         			ref.getString("text"),
//                         			0, 
//                         			0,
//                         			0,
//                         			ref.getString("publishtime"),
//                         			Integer.parseInt(ref.getString("type")),
//                         			imgItems2
//                         			);
//                         }
                         
                         if (!jFeedItem.isNull("files") && !jFeedItem.getString("files").equals("[]")) {
              				rlGvWrapper.setVisibility(View.VISIBLE);
                             JSONArray jImgItemsArr = jFeedItem.getJSONArray("files");
                             for (int i=0; i<jImgItemsArr.length(); i++) {
                                 JSONObject jFile = jImgItemsArr.getJSONObject(i);
                                 Log.d("image url==========>", jFile.getString("url"));
                                 imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
                             }
                             initImgAttachment(imagesGv, imgItems);
                         }
                         
//                         if (imgs != null) {
//             				rlGvWrapper.setVisibility(View.VISIBLE);
//             				JSONArray jImgItemsArr = null;
//             				try {
//             					jImgItemsArr = new JSONArray(imgs);
//             					for (int i=0; i<jImgItemsArr.length(); i++) {
//             		                JSONObject jFile = jImgItemsArr.getJSONObject(i);
//             		                Log.d("image url==========>", jFile.getString("url"));
//             		                imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
//             		            }
//             					initImgAttachment(imagesGv, imgItems);
//             				} catch (JSONException e) {
//             					e.printStackTrace();
//             				}
//             				
//             			}
//             			if (hasForward == true) {
//             				if (forwardHasImg == true) {
//             					ImageLoader.getInstance().displayImage(icon, forwardIcon);
//             				}
//             				SpannableStringBuilder sb = Utils.handlerFaceInContent(NewsFeedDetailActivity.this, forwardContent,
//             	                    ct);
//             				forwardContent.setText(sb);
//             				forwardArea.setVisibility(View.VISIBLE);
//             			}
             			
                        name = jUserObj.getString("username");
             			uid =jUserObj.getString("uid");
             			isFollowed = jUserObj.has("isFollow")?jUserObj.getBoolean("isFollow"):false;
             			
                         SpannableStringBuilder sb = Utils.handlerFaceInContent(NewsFeedDetailActivity.this, contentTv,
                        		 content);
             			contentTv.setText(sb);
             			
             			if (photo != null && !photo.equals("null")) ImageLoader.getInstance().displayImage(photo, photoIv);
             			nameTv.setText( jUserObj.getString("username"));
             			timeTv.setText(Utils.covertTimestampToDate(Long.parseLong(publishTime) * 1000, false));
//             			SpannableStringBuilder sb = Utils.handlerFaceInContent(NewsFeedDetailActivity.this, contentTv,
//             	                content);
//             			contentTv.setText(sb);
             			praiseNumTv.setText(praiseCount+" 赞");
             			
             			List<Fragment> fragments = getFragments();
             	        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
             	        pager.setAdapter(pageAdapter);
             	        pagertab.setViewPager(pager);
             	        pagertab.setScrollOffset(0);
             	        pagertab.setTextColorResource(R.color.grey);
             	        
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedDetailActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
	
}
