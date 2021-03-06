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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.NewsFeedItemsParser;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.pojo.FeedItem;
import com.orfid.youxikuaile.widget.MyGridView;

/**
 * Created by Administrator on 2015/3/3.
 */
public class NewsFeedActivity extends Activity implements View.OnClickListener {

    private static final int COMPOSE_FEED = 0;
    private static final int FORWARD_ACTION = 1;
    private SwipeRefreshLayout swipeContainer;
    private ImageButton backBtn, composeAddBtn;
    private ListView newsFeedLv;
    private View emptyViewLl, editboxLlView, msgHintView;
    private ProgressBar pBar;
    private List<FeedItem> feedItems = new ArrayList<FeedItem>();
    private MyAdapter adapter;
    private GridViewAdapter gvAdapter;
    private TextView loadingTv;
    private Button publishBtn;
    private EditText commentEt;
    private ImageView mImageView;
    private ImageView msgIcon;
    private TextView msgCount;
    private boolean isButtonSent = false;
    private long feedId;
    private int pos;
    private String uid = null;
    private int messageFeedCount = 0;
    private TextView titleText;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        init();
        try {
        	if (messageFeedCount > 0) 
        		doFetchMessageFeedAction();
        	
            doFetchFeedListAction(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
    	
    	messageFeedCount = getIntent().getIntExtra("feedCount", 0);
    	
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        composeAddBtn = (ImageButton) findViewById(R.id.btn_compose_add);
        newsFeedLv = (ListView) findViewById(R.id.lv_news_feed);
        pBar = (ProgressBar) findViewById(R.id.progress_bar);
        loadingTv = (TextView) findViewById(R.id.tv_loading);
        emptyViewLl = findViewById(R.id.ll_empty_view);
        publishBtn = (Button) findViewById(R.id.btn_publish);
        commentEt = (EditText) findViewById(R.id.newsfeedpublish_et);
        editboxLlView = findViewById(R.id.editbox_ll_view);
        titleText = (TextView) findViewById(R.id.tv_title);
        mImageView = (ImageView) findViewById(R.id.newsfeedpublish_img);
        
        msgHintView = findViewById(R.id.ll_header_view);
        msgIcon = (ImageView) findViewById(R.id.last_msg_icon);
        msgCount = (TextView)  findViewById(R.id.msg_count_num);
        
        backBtn.setOnClickListener(this);
        composeAddBtn.setOnClickListener(this);
        publishBtn.setOnClickListener(this);
        mImageView.setOnClickListener(this);

//        newsFeedLv.addHeaderView(headerView);
//        newsFeedLv.removeHeaderView(headerView);
        newsFeedLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

            	FeedItem item = adapter.getItem(position);
            	Intent intent = new Intent(NewsFeedActivity.this, NewsFeedDetailActivity.class);
            	intent.putExtra("feedId", id);
            	intent.putExtra("uid", item.getUser().getUid());
            	intent.putExtra("isFollowed", item.getUser().isFollow());
            	intent.putExtra("photo", item.getUser().getPhoto());
            	intent.putExtra("name", item.getUser().getUsername());
            	intent.putExtra("time", item.getPublishTime());
            	intent.putExtra("content", item.getContentText());
            	intent.putExtra("forwardNum", item.getForwardCount());
            	intent.putExtra("commentNum", item.getCommentCount());
            	intent.putExtra("praiseNum", item.getPraiseCount());
            	if (item.getImgItems() != null && item.getImgItems().size() > 0) {
            		List<FeedAttachmentImgItem> imgItems = item.getImgItems();
            		String imgs = "[";
            		for (int i=0; i<imgItems.size(); i++) {
            			FeedAttachmentImgItem imgItem = imgItems.get(i);
            			imgs += "{\"id\":\"" + imgItem.getId() + "\",\"url\":\"" + imgItem.getUrl() + "\"}";
            			if (i != imgItems.size() -1) imgs += ",";
            		}
            		imgs += "]";
            		Log.d("imgs========>", imgs);
            		intent.putExtra("imgs", imgs);
            	}
            	if (item.getForward() != null) {
            		FeedItem forward = item.getForward();
            		if (forward.getImgItems() != null && forward.getImgItems().size() > 0) {
            			intent.putExtra("forwardHasImg", true);
            			intent.putExtra("forwardIcon", forward.getImgItems().get(forward.getImgItems().size() - 1).getUrl());
            		}
            		intent.putExtra("hasForward", true);
            		intent.putExtra("forwardContent", forward.getContentText());
            	}
            	startActivity(intent);
            }
        });
        
        newsFeedLv.setOnItemLongClickListener(new OnItemLongClickListener() {

        	final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
            HashMap user = dbHandler.getUserDetails();
            
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, final long id) {
              new AlertDialog.Builder(NewsFeedActivity.this)
              .setTitle("提示")
              .setMessage("确定删除吗?")
              .setPositiveButton("是", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                	  if (adapter.getItem(position).getUser().getUid().equals(user.get("uid").toString())) {
//	                      Toast.makeText(NewsFeedActivity.this, "position====>"+position, Toast.LENGTH_SHORT).show();
//	                      Toast.makeText(NewsFeedActivity.this, "feedid====>"+id, Toast.LENGTH_SHORT).show();
	                      feedItems.remove(position);
	                      adapter.notifyDataSetChanged();
	                      if (feedItems.size() <=0 ) emptyViewLl.setVisibility(View.VISIBLE);
	                      try {
	                          doRemoveFeedAction(id);
	                      } catch (JSONException e) {
	                          e.printStackTrace();
	                      }
                	  } else {
                		  Toast.makeText(NewsFeedActivity.this, "无法删除其他人的微博", Toast.LENGTH_SHORT).show();
                	  }
                  }
              })
              .setNegativeButton("否", null)
              .show();
              
              return true;
			}
        	
        });
//        newsFeedLv.setOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				editboxLlView.setVisibility(View.GONE);
//				InputMethodManager imm = (InputMethodManager) getSystemService(
//					      Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				
//			}
//        	
//        });
        
        commentEt.addTextChangedListener(new TextWatcher() {

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
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					// change button plus to send
					mImageView.setImageResource(R.drawable.btn_sent);
					isButtonSent = true;
				} else {
					// change button plus back
					mImageView.setImageResource(R.drawable.white3);
					isButtonSent = false;
				}
			}
        	
        });

        swipeContainer.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.orange);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeContainer.setRefreshing(false);
//                    }
//                }, 3000);
                try {
                    doFetchFeedListAction(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        Intent intent = getIntent();
//        Bundle data = intent.getExtras();
//        List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
//        new FeedItem(0, new UserItem(), "lalalla", );
        
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        
        boolean isPublicAccount = getIntent().getBooleanExtra("isPublicAccount", false);
        uid = getIntent().getStringExtra("uid");
        if (isPublicAccount) {
        	titleText.setText("公共号动态");
        }
        if (uid !=null && !uid.equals(user.get("uid").toString())) {
        	composeAddBtn.setVisibility(View.GONE);
        }
        
        
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_compose_add:
            case R.id.btn_publish:
                startActivityForResult(new Intent(this, NewsFeedPublishActivity.class), COMPOSE_FEED);
                break;
            case R.id.newsfeedpublish_img:
            	if (isButtonSent) {
    				if (!commentEt.getText().toString().trim().equals("")) {
    					//发送消息
    					try {
							doSendFeedReplyAction();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					
    					FeedItem item = adapter.getItem(pos);
    					item.setCommentCount(item.getCommentCount()+1);
    					adapter.notifyDataSetChanged();
    					
    					editboxLlView.setVisibility(View.GONE);
    					InputMethodManager imm = (InputMethodManager) getSystemService(
    						      Context.INPUT_METHOD_SERVICE);
    						imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);

    				} else {
    					Toast.makeText(NewsFeedActivity.this, "请先输入内容", Toast.LENGTH_SHORT).show();
    				}
    			} else {

    			}
            	break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case COMPOSE_FEED:
//                FeedItem feedItem = (FeedItem) EventBus.getDefault().removeStickyEvent(FeedItem.class);
//                feedItems.add(0, feedItem);
//                adapter.notifyDataSetChanged();
//                emptyViewLl.setVisibility(View.GONE);
//                if (feedItem != null) {
                	try {
						doFetchFeedListAction(true);
						emptyViewLl.setVisibility(View.GONE);
					} catch (JSONException e) {
						e.printStackTrace();
					}
//                }
                break;
            case FORWARD_ACTION:
            	
//            	int position = data.getIntExtra("position", 0);
//            	Log.d("returned position======>", position+"");
//            	FeedItem item = adapter.getItem(position);
//            	int count = item.getCommentCount();
//            	item.setCommentCount(count++);
//            	adapter.notifyDataSetChanged();
            	
            	try {
					doFetchFeedListAction(true);
					emptyViewLl.setVisibility(View.GONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyAdapter extends ArrayAdapter<FeedItem> {

        private List<FeedItem> items;
        private FeedItem objBean;
        private int resource;
        private Context context;
        private int wh;

        public MyAdapter(Context context, int resource, List<FeedItem> arrayList) {
            super(context, resource, arrayList);
            this.items = arrayList;
            this.resource = resource;
            this.context = context;
            this.wh=(Utils.getScreenWidth(context)-Utils.Dp2Px(context, 99))/3;
        }

        @Override
        public int getCount() {
            return items == null ? 0: items.size();
        }

        @Override
        public FeedItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).getFeedId();
        }

        HashMap<Integer,View> lmap = new HashMap<Integer,View>();
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.userAvatarIv = (ImageView) convertView
                        .findViewById(R.id.iv_user_avatar);
                viewHolder.usernameTv = (TextView) convertView
                        .findViewById(R.id.tv_username);
                viewHolder.publishTimeTv = (TextView) convertView
                        .findViewById(R.id.tv_publish_time);
                viewHolder.contentTextTv = (TextView) convertView
                        .findViewById(R.id.tv_content_text);
                viewHolder.imagesGv = (MyGridView) convertView.findViewById(R.id.gv_images);
                viewHolder.rlGvWrapper = convertView.findViewById(R.id.rl_gv_wrapper);
                viewHolder.forwardRlView = convertView.findViewById(R.id.forward_rl_vew);
                viewHolder.replyRlView = convertView.findViewById(R.id.reply_rl_view);
                viewHolder.forwardNumTv = (TextView) convertView.findViewById(R.id.forward_num_tv);
                viewHolder.replyNumTv = (TextView) convertView.findViewById(R.id.reply_num_tv);
                viewHolder.praiseRlView = convertView.findViewById(R.id.praise_rl_view);
                viewHolder.praiseNumTv = (TextView) convertView.findViewById(R.id.praise_tv);
                viewHolder.forwardArea = convertView.findViewById(R.id.forword_area);
                viewHolder.forwardIcon = (ImageView) convertView.findViewById(R.id.forward_icon);
                viewHolder.forwardContent = (TextView) convertView.findViewById(R.id.forward_content);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = getItem(position);
            
            viewHolder.forwardArea.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					long forwardFeedId = getItem(position).getForward().getFeedId();
					Log.d("forwardFeedId=========>", forwardFeedId+"");
					//Toast.makeText(context,"forwardFeedId=========>"+ forwardFeedId, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(NewsFeedActivity.this, NewsFeedDetailActivity.class);
					intent.putExtra("fetchNeededFeedId", forwardFeedId);
					startActivity(intent);
				}
            	
            });
            
            viewHolder.imagesGv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Log.d("position========>", position+"");
					Log.d("id=======>", id+"");
					Log.d("url=============>", gvAdapter.getItem(position).getUrl());
					Intent intent = new Intent(NewsFeedActivity.this, PhotoDetailActivity.class);
					intent.putExtra("url", gvAdapter.getItem(position).getUrl());
					startActivity(intent);
				}
            	
            });
            
            viewHolder.userAvatarIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, v.getId()+"", Toast.LENGTH_SHORT).show();
                	Log.d("avatar clicked =====> ", "true");
                	Intent intent = new Intent(NewsFeedActivity.this, FriendHomeActivity.class);
					String uid = getItem(position).getUser().getUid();
					String username = getItem(position).getUser().getUsername();
					String photo = getItem(position).getUser().getPhoto();
					boolean isFollowed = getItem(position).getUser().isFollow();
					intent.putExtra("uid", uid);
	                intent.putExtra("username", username);
	                intent.putExtra("photo", photo);
	                intent.putExtra("isFollowed", isFollowed);
					startActivity(intent);
                }
            });
            viewHolder.forwardRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					FeedItem item = items.get(position);
					long feedId = item.getFeedId();
					String contentd = item.getContentText();
					String photo = null;
					if (item.getForward() != null) {
						feedId = item.getForward().getFeedId();
						contentd = item.getForward().getContentText();
					}
					if (item.getImgItems() != null && item.getImgItems().size() > 0) {
						FeedAttachmentImgItem imgItem = item.getImgItems().get(item.getImgItems().size() - 1);
						photo = imgItem.getUrl();
					}

					Intent intent = new Intent(NewsFeedActivity.this, NewsFeedForwardActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("feedId", feedId);
					intent.putExtra("content", contentd);
					intent.putExtra("photo", photo);
					startActivityForResult(intent, FORWARD_ACTION);
				}
            	
            });
            viewHolder.replyRlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d("current feedId =========>", objBean.getFeedId()+"");
					feedId = objBean.getFeedId();
					pos = position;
					editboxLlView.setVisibility(View.VISIBLE);
					commentEt.setFocusable(true);
					commentEt.setFocusableInTouchMode(true);
					commentEt.requestFocus();
	            	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(commentEt, InputMethodManager.SHOW_IMPLICIT);
				}
            	
            });
            viewHolder.praiseRlView.setOnClickListener(new OnClickListener() {
            	
				@Override
				public void onClick(View v) {
					FeedItem item = items.get(position);
					if (item.isPraised() == false) {
						feedId = item.getFeedId();
	//					pos = position;
						int newval = item.getPraiseCount()+1;
						item.setPraiseCount(newval);
						item.setPraised(true);
						adapter.notifyDataSetChanged();
						
						try {
							doPraiseFeedAction();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(NewsFeedActivity.this, "已经赞过了", Toast.LENGTH_SHORT).show();
					}
				}
            	
            });

			
            if (objBean.getUser().getPhoto() != null) ImageLoader.getInstance().displayImage(objBean.getUser().getPhoto(), viewHolder.userAvatarIv);
            viewHolder.usernameTv.setText(objBean.getUser().getUsername());
            viewHolder.publishTimeTv.setText(Utils.covertTimestampToDate(Long.parseLong(objBean.getPublishTime()) * 1000, false));
            SpannableStringBuilder sb = Utils.handlerFaceInContent(context, viewHolder.contentTextTv,
                    objBean.getContentText());
            viewHolder.contentTextTv.setText(sb);
            if (objBean.getImgItems() != null && objBean.getImgItems().size() > 0) {
                viewHolder.rlGvWrapper.setVisibility(View.VISIBLE);
                initImgAttachment(viewHolder.imagesGv, objBean.getImgItems());
            }
//            Log.d("forward count============>", objBean.getForwardCount()+"");
            if (objBean.getForwardCount() > 0) {
            	viewHolder.forwardNumTv.setText(objBean.getForwardCount()+"");
            }
            if (objBean.getCommentCount() > 0) {
            	viewHolder.replyNumTv.setText(objBean.getCommentCount()+"");
            }
            if (objBean.isPraised() == true) {
            	//已赞
            	viewHolder.praiseNumTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.feed_praise_red, 0, 0, 0);
            	viewHolder.praiseNumTv.setText(objBean.getPraiseCount()+"");
            } else if (objBean.getPraiseCount() > 0) {
            	viewHolder.praiseNumTv.setText(objBean.getPraiseCount()+"");
            }
            
            if (objBean.getForward() != null) {
            	FeedItem forward = objBean.getForward();
            	if (forward.getImgItems() != null && forward.getImgItems().size() > 0) {
            		FeedAttachmentImgItem imgItem = forward.getImgItems().get(forward.getImgItems().size() - 1);
            		ImageLoader.getInstance().displayImage(imgItem.getUrl(), viewHolder.forwardIcon);
            	}
            	SpannableStringBuilder s = Utils.handlerFaceInContent(NewsFeedActivity.this, viewHolder.forwardContent,
            			forward.getContentText());
            	viewHolder.forwardContent.setText(s);
            	viewHolder.forwardArea.setVisibility(View.VISIBLE);
            }

            return convertView;
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
                    w=2*wh+Utils.Dp2Px(context, 2);
                    myGv.setNumColumns(2);
                    break;
                case 3:
                case 5:
                case 6:
                    w=wh*3+Utils.Dp2Px(context, 2)*2;
                    myGv.setNumColumns(3);
                    break;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
            myGv.setLayoutParams(lp);
            gvAdapter = new GridViewAdapter(context, items);
            myGv.setAdapter(gvAdapter);
        }

        public class ViewHolder {
            ImageView userAvatarIv, forwardIcon;
            TextView usernameTv;
            TextView publishTimeTv;
            TextView contentTextTv;
            MyGridView imagesGv;
            View rlGvWrapper, forwardRlView, replyRlView, praiseRlView, forwardArea;
            TextView forwardNumTv, replyNumTv, praiseNumTv, forwardContent;
        }

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

    private void doFetchFeedListAction(final boolean isRefreshing) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        if (uid != null)
        	params.put("uid", uid);
//        params.put("page", 1);
        HttpRestClient.post("feed", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        NewsFeedItemsParser parser = new NewsFeedItemsParser();
                        feedItems = parser.parse(response.getJSONObject("data"));
                        Log.d("feedItems count=====>", feedItems.size()+"");
                        adapter = new MyAdapter(NewsFeedActivity.this, R.layout.feed_item, feedItems);
                        newsFeedLv.setAdapter(adapter);
                        if (feedItems.size() <= 0) {
                            emptyViewLl.setVisibility(View.VISIBLE);
                        }
//                        adapter.notifyDataSetChanged();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

			@Override
			public void onFinish() {
                if (!isRefreshing) {
                    pBar.setVisibility(View.GONE);
                    loadingTv.setVisibility(View.GONE);
                } else {
                    swipeContainer.setRefreshing(false);
                }
			}

			@Override
			public void onStart() {
                if (!isRefreshing) {
                    pBar.setVisibility(View.VISIBLE);
                    loadingTv.setVisibility(View.VISIBLE);
                }
			}
        });
    }

    private void doRemoveFeedAction(long feedid) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", feedid);
        HttpRestClient.post("feed/remove", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success

                    } else if (status == 0) {
                        Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void doSendFeedReplyAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("pid", feedId);
        params.put("type", 2);
        params.put("text", commentEt.getText().toString().trim());
//        params.put("files", fileIds);
        HttpRestClient.post("feed/publish", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                       
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    
    private void doFetchMessageFeedAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        HttpRestClient.post("message/feed", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                       final String msgFeedData = response.getString("data");
                       JSONArray jArr;
                       try {
                    	   jArr = response.getJSONArray("data");
                    	   int length = jArr.length();
                    	   String photo = null;
                    	   if (length > 0) {
                    		   JSONObject jObj = jArr.getJSONObject(length - 1);
                    		   JSONObject jUser = jObj.getJSONObject("user");
                    		   if (jUser.getString("photo") != null && 
                    				   !jUser.getString("photo").equals("null")) {
                    			   photo = jUser.getString("photo");
                    		   }
                    		   if (photo != null && !photo.equals("null"))
                    			   ImageLoader.getInstance().displayImage(photo, msgIcon);
                    		   msgCount.setText(length+"条新消息");
                    		   msgHintView.setVisibility(View.VISIBLE);
                    		   msgHintView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(NewsFeedActivity.this, MessageFeedActivity.class);
									intent.putExtra("msgFeedData", msgFeedData);
									startActivity(intent);
									msgHintView.setVisibility(View.GONE);
								}
                    			   
                    		   });
                    	   }
                       } catch (Exception e) {
                    	   e.printStackTrace();
                       }
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
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
                    	Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    } else if (status == 0) {
                        Toast.makeText(NewsFeedActivity.this, response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
