package com.orfid.youxikuaile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orfid.youxikuaile.parser.FeedCommentItemsParser;
import com.orfid.youxikuaile.pojo.FeedCommentItem;
import com.orfid.youxikuaile.pojo.UserItem;


public class CommonListFragment extends Fragment {
	public static final String FEED_ID = "feedId";
	public static final String POSITION = "position";
	private ListView myLv;
	private TextView hinterTv;
	private MyAdapter adapter;
	private String requestUrl = "feed/forwardlist";
	private List<FeedCommentItem> items = new ArrayList<FeedCommentItem>();

    public static final CommonListFragment newInstance(long feedId, int position)
    {
        CommonListFragment f = new CommonListFragment();
        Bundle bdl = new Bundle(2);
        bdl.putLong(FEED_ID, feedId);
        bdl.putInt(POSITION, position);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	long feedId = getArguments().getLong(FEED_ID);
    	int position = getArguments().getInt(POSITION);
    	if (position == 1) requestUrl = "feed/commentlist";
    	
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        myLv = (ListView) v.findViewById(R.id.my_lv);
        hinterTv = (TextView) v.findViewById(R.id.hinter_tv);
        
        try {
			doFeedPublishAction(feedId, position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        return v;
    }
    
    private void doFeedPublishAction(long id, final int position) throws JSONException {
        final DatabaseHandler dbHandler = MainApplication.getInstance().getDbHandler();
        HashMap user = dbHandler.getUserDetails();
        RequestParams params = new RequestParams();
        params.put("token", user.get("token").toString());
        params.put("id", id);
        HttpRestClient.post(requestUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("response=======>", response.toString());
                try {
                    int status = response.getInt("status");
                    if (status == 1) { // success
                        FeedCommentItemsParser parser = new FeedCommentItemsParser();
                        items = parser.parse(response.getJSONObject("data"));
                        adapter = new MyAdapter(getActivity(), R.layout.feed_comment_item, items);
                        if (items.size() <= 0) {
                        	if (position == 0) {
                        		hinterTv.setText("0 转发");
                        	} else if (position == 1) {
                        		hinterTv.setText("0 评论");
                        	}
                        	
                        } else {
                        	hinterTv.setVisibility(View.GONE);
                        	myLv.setAdapter(adapter);
                        }
                    } else if (status == 0) {
                        Toast.makeText(getActivity(), response.getString("text"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStart() {
            	hinterTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {

            }

        });
    }
    
    private class MyAdapter extends ArrayAdapter<FeedCommentItem> {

    	private Context context;
    	private int resource;
    	private List<FeedCommentItem> items;
    	private FeedCommentItem objBean;
    	
		public MyAdapter(Context context, int resource,
				List<FeedCommentItem> items) {
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
		public FeedCommentItem getItem(int position) {
			return items.get(position);
		}

		HashMap<Integer,View> lmap = new HashMap<Integer,View>();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
            if (lmap.get(position)==null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        resource, parent, false);
                viewHolder.userAvatarIv = (ImageView) convertView.findViewById(R.id.comment_user_iv);
                viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.comment_name_tv);
                viewHolder.commentMsgTv = (TextView) convertView.findViewById(R.id.comment_msg_tv);
                viewHolder.commentTimeTv = (TextView) convertView.findViewById(R.id.comment_time_tv);
                lmap.put(position, convertView);
                convertView.setTag(viewHolder);
            } else {
                convertView = lmap.get(position);
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            objBean = items.get(position);
            
            UserItem userItem = objBean.getCommentUser();
            if (userItem.getPhoto() != null && !userItem.getPhoto().equals("null")) {
            	ImageLoader.getInstance().displayImage(userItem.getPhoto(), viewHolder.userAvatarIv);
            }
            if (userItem.getUsername() != null && !userItem.getPhoto().equals("null")) {
            	viewHolder.userNameTv.setText(userItem.getUsername());
            }
            viewHolder.commentMsgTv.setText(objBean.getCommentMsg());
            viewHolder.commentTimeTv.setText(
            		Utils.covertTimestampToDate(
            				Long.parseLong(objBean.getCommentTime()) * 1000
            		)
            );
            
            return convertView;
		}
		
		public class ViewHolder {
			ImageView userAvatarIv;
			TextView userNameTv;
			TextView commentMsgTv;
			TextView commentTimeTv;
		}
		
		
    	
    }
}
