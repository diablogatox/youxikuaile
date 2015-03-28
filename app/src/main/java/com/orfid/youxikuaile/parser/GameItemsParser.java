package com.orfid.youxikuaile.parser;


import com.orfid.youxikuaile.pojo.GameItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/5.
 */
public class GameItemsParser {

    public List<GameItem> parse(JSONObject jObject) {

        JSONArray jFeedItems = null;
        try {
            /** Retrieves all the elements */
            jFeedItems = jObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFeedItems(jFeedItems);
    }


    private List<GameItem> getFeedItems(JSONArray jFeedItems) {
        int feedItemsCount = jFeedItems.length();
        List<GameItem> feedItemsList = new ArrayList<GameItem>();
        GameItem feedItem = null;

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

    private GameItem getFeedItem(JSONObject jFeedItem) {

        GameItem feedItem = new GameItem();
        String id = null, name = null, img = null;
        try {
			id = jFeedItem.getString("id");
			name = jFeedItem.getString("name");
			img = jFeedItem.getString("img");
		} catch (JSONException e) {
			e.printStackTrace();
		}

        feedItem.setId(id);
        feedItem.setName(name);
        feedItem.setImg(img);
        
        return feedItem;
    }
}
