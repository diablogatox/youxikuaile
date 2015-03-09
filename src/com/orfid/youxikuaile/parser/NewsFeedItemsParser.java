package com.orfid.youxikuaile.parser;

import android.util.Log;
import com.orfid.youxikuaile.Constants;
import com.orfid.youxikuaile.pojo.FeedAttachmentImgItem;
import com.orfid.youxikuaile.pojo.FeedItem;
import com.orfid.youxikuaile.pojo.UserItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/5.
 */
public class NewsFeedItemsParser {

    public List<FeedItem> parse(JSONObject jObject) {

        JSONArray jFeedItems = null;
        try {
            /** Retrieves all the elements */
            jFeedItems = jObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFeedItems(jFeedItems);
    }


    private List<FeedItem> getFeedItems(JSONArray jFeedItems) {
        int feedItemsCount = jFeedItems.length();
        List<FeedItem> feedItemsList = new ArrayList<FeedItem>();
        FeedItem feedItem = null;

        for(int i=0; i<feedItemsCount;i++) {
            try {
                feedItem = getFeedItem((JSONObject) jFeedItems.get(i));
                feedItemsList.add(feedItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return feedItemsList;
    }

    private FeedItem getFeedItem(JSONObject jFeedItem) {

        FeedItem feedItem = new FeedItem();
        long feedId = 0;
        int commentCount = 0, forwardCount = 0, praiseCount = 0, type = 0;
        String contentText = null, publishTime = null;
        UserItem user = null;
        List<FeedAttachmentImgItem> imgItems = new ArrayList<FeedAttachmentImgItem>();
        try {
            feedId = Long.parseLong(jFeedItem.getString("feedid"));
            contentText = jFeedItem.getString("text");
            commentCount = Integer.parseInt(jFeedItem.getString("commentcount"));
            forwardCount = Integer.parseInt(jFeedItem.getString("forwardcount"));
            praiseCount = Integer.parseInt(jFeedItem.getString("praisecount"));
            publishTime = jFeedItem.getString("publishtime");
            type = Integer.parseInt(jFeedItem.getString("type"));
            if (!jFeedItem.isNull("files") && !jFeedItem.getString("files").equals("[]")) {
                JSONArray jImgItemsArr = jFeedItem.getJSONArray("files");
                for (int i=0; i<jImgItemsArr.length(); i++) {
                    JSONObject jFile = jImgItemsArr.getJSONObject(i);
                    Log.d("image url==========>", jFile.getString("url"));
                    imgItems.add(new FeedAttachmentImgItem(jFile.getString("url"), jFile.getString("id")));
                }
            }
            JSONObject jUserObj = jFeedItem.getJSONObject("user");
            String photo = null;
            if (!jUserObj.isNull("photo")) photo = jUserObj.getString("photo");
            user = new UserItem(jUserObj.getString("uid"), jUserObj.has("birthday")?jUserObj.getString("birthday"):""
                    , jUserObj.has("sex")?jUserObj.getString("sex"):"", jUserObj.getString("username"), photo
                    , jUserObj.has("signature")?jUserObj.getString("signature"):"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        feedItem.setFeedId(feedId);
        feedItem.setContentText(contentText);
        feedItem.setCommentCount(commentCount);
        feedItem.setForwardCount(forwardCount);
        feedItem.setPraiseCount(praiseCount);
        feedItem.setPublishTime(publishTime);
        feedItem.setType(type);
        feedItem.setUser(user);
        feedItem.setCommentCount(commentCount);
        feedItem.setImgItems(imgItems);

        return feedItem;
    }
}
