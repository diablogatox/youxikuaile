package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.orfid.youxikuaile.pojo.FeedItem;
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
    private List<FeedItem> feedItems = new ArrayList<FeedItem>();
    private MyAdapter adapter;

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

        backBtn.setOnClickListener(this);
        composeAddBtn.setOnClickListener(this);

        newsFeedLv.addHeaderView(headerView);
//        newsFeedLv.removeHeaderView(headerView);

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

        public MyAdapter(Context context, int resource, List<FeedItem> arrayList) {
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

            return convertView;
        }

        public class ViewHolder {
            ImageView userAvatarIv;
            TextView usernameTv;
            TextView publishTimeTv;
            TextView contentTextTv;
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
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("failure response====>", responseString);
            }
        });
    }
}
