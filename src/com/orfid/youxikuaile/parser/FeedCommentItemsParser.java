package com.orfid.youxikuaile.parser;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.FeedCommentItem;
import com.orfid.youxikuaile.pojo.UserItem;


public class FeedCommentItemsParser {

    public List<FeedCommentItem> parse(JSONObject jObject) {

        JSONArray jFeedCommentItems = null;
        try {
        	jFeedCommentItems = jObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFeedCommentItems(jFeedCommentItems);
    }


    private List<FeedCommentItem> getFeedCommentItems(JSONArray jFeedCommentItems) {
        int feedCommentItemsCount = jFeedCommentItems.length();
        List<FeedCommentItem> feedCommentItemsList = new ArrayList<FeedCommentItem>();
        FeedCommentItem feedCommentItem = null;

        for(int i=0; i<feedCommentItemsCount;i++) {
            try {
                feedCommentItem = getFollowItem((JSONObject) jFeedCommentItems.get(i));
                feedCommentItemsList.add(feedCommentItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return feedCommentItemsList;
    }

    private FeedCommentItem getFollowItem(JSONObject jFeedCommentItem) {

        FeedCommentItem feedCommentItem = new FeedCommentItem();
        String feedCommentId = null, commentMsg = null, commentTime = null;
        UserItem commentUser = null;
        try {
        	JSONObject user = jFeedCommentItem.getJSONObject("user");
        	
        	feedCommentId = jFeedCommentItem.getString("feedid");
        	commentMsg = jFeedCommentItem.getString("text");
        	commentTime = jFeedCommentItem.getString("publishtime");
        	commentUser = new UserItem(
        			user.getString("uid"),
        			user.getString("username"),
        			user.getString("photo"),
        			user.getString("signature"),
        			user.getString("type")
        	);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        feedCommentItem.setFeedCommentId(feedCommentId);
        feedCommentItem.setCommentMsg(commentMsg);
        feedCommentItem.setCommentTime(commentTime);
        feedCommentItem.setCommentUser(commentUser);

        return feedCommentItem;
    }
}
