package com.orfid.youxikuaile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.NewsFeedItemsParser;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.pojo.FeedItem;

import com.orfid.youxikuaile.pojo.UserItem;
import com.orfid.youxikuaile.widget.MyGridView;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/3/3.
 */
public class NewsFeedActivity extends Activity implements View.OnClickListener {

    private SwipeRefreshLayout swipeContainer;
    private ImageButton backBtn, composeAddBtn;
    private ListView newsFeedLv;
    private View headerView;
    private ProgressBar pBar;
    private List<FeedItem> feedItems = new ArrayList<FeedItem>();
    private MyAdapter adapter;
    private GridViewAdapter gvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        init();
        try {
            doFetchFeedListAction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        backBtn = (ImageButton) findViewById(R.id.btn_back);
        composeAddBtn = (ImageButton) findViewById(R.id.btn_compose_add);
        headerView = getLayoutInflater().inflate(R.layout.unread_message, null);
        newsFeedLv = (ListView) findViewById(R.id.lv_news_feed);
        pBar = (ProgressBar) findViewById(R.id.progress_bar);

        backBtn.setOnClickListener(this);
        composeAddBtn.setOnClickListener(this);

//        newsFeedLv.addHeaderView(headerView);
//        newsFeedLv.removeHeaderView(headerView);
        newsFeedLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(NewsFeedActivity.this)
                        .setTitle("提示")
                        .setMessage("确定删除吗?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(NewsFeedActivity.this, "position====>"+position, Toast.LENGTH_SHORT).show();
                                Toast.makeText(NewsFeedActivity.this, "feedid====>"+id, Toast.LENGTH_SHORT).show();
                                feedItems.remove(position);
                                adapter.notifyDataSetChanged();
                                try {
                                    doRemoveFeedAction(id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.orange);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 3000);
            }
        });

//        Intent intent = getIntent();
//        Bundle data = intent.getExtras();
//        List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
//        new FeedItem(0, new UserItem(), "lalalla", );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_compose_add:
                startActivity(new Intent(this, NewsFeedPublishActivity.class));
                break;
        }
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
            this.wh=(Util.getScreenWidth(context)-Util.Dp2Px(context, 99))/3;
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.userAvatarIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, v.getId()+"", Toast.LENGTH_SHORT).show();
                }
            });

			objBean = items.get(position);
            if (objBean.getUser().getPhoto() != null) ImageLoader.getInstance().displayImage(objBean.getUser().getPhoto(), viewHolder.userAvatarIv);
            viewHolder.usernameTv.setText(objBean.getUser().getUsername());
            viewHolder.publishTimeTv.setText(objBean.getPublishTime());
            viewHolder.contentTextTv.setText(objBean.getContentText());
            if (objBean.getImgItems().size() > 0) {
                viewHolder.rlGvWrapper.setVisibility(View.VISIBLE);
                initImgAttachment(viewHolder.imagesGv, objBean.getImgItems());
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
                    w=2*wh+Util.Dp2Px(context, 2);
                    myGv.setNumColumns(2);
                    break;
                case 3:
                case 5:
                case 6:
                    w=wh*3+Util.Dp2Px(context, 2)*2;
                    myGv.setNumColumns(3);
                    break;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.WRAP_CONTENT);
            myGv.setLayoutParams(lp);
            gvAdapter = new GridViewAdapter(context, items);
            myGv.setAdapter(gvAdapter);
        }

        public class ViewHolder {
            ImageView userAvatarIv;
            TextView usernameTv;
            TextView publishTimeTv;
            TextView contentTextTv;
            MyGridView imagesGv;
            View rlGvWrapper;
        }

    }

    public class GridViewAdapter extends BaseAdapter {

        Context context;
        List<FeedAttachmentImgItem> list;
        private int wh;

        public GridViewAdapter(Context context, List<FeedAttachmentImgItem> data) {
            this.context=context;
            this.wh=(Util.getScreenWidth(context)-Util.Dp2Px(context, 99))/3;
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

    private void doFetchFeedListAction() throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
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
                        adapter = new MyAdapter(NewsFeedActivity.this, R.layout.feed_item, feedItems);
                        newsFeedLv.setAdapter(adapter);
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
				pBar.setVisibility(View.GONE);
			}

			@Override
			public void onStart() {
				pBar.setVisibility(View.VISIBLE);
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
}
