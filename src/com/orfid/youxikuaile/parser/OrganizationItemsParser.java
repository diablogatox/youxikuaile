package com.orfid.youxikuaile.parser;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.Event;
import com.orfid.youxikuaile.pojo.OrganizationItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class OrganizationItemsParser {

    public List<OrganizationItem> parse(JSONObject jObject) {

        JSONArray jFeedItems = null;
        try {
            /** Retrieves all the elements */
            jFeedItems = jObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFeedItems(jFeedItems);
    }


    private List<OrganizationItem> getFeedItems(JSONArray jFeedItems) {
        int feedItemsCount = jFeedItems.length();
        List<OrganizationItem> feedItemsList = new ArrayList<OrganizationItem>();
        OrganizationItem feedItem = null;

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

    private OrganizationItem getFeedItem(JSONObject jFeedItem) {

        OrganizationItem feedItem = new OrganizationItem();
        String uid = null, utime = null, distance = null, name = null, type = null, photo = null;
        Event lastEvent = null;
        try {
			uid = jFeedItem.getString("uid");
			name = jFeedItem.getString("username");
			utime = jFeedItem.getString("utime");
			distance = jFeedItem.getString("distance");
			type = jFeedItem.getString("type");
			photo = jFeedItem.getString("photo");
			if (!jFeedItem.isNull("info")) {
				JSONObject event = jFeedItem.getJSONObject("info");
				lastEvent = new Event(event.getString("ctime"), event.getString("title"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

        feedItem.setUid(uid);
        feedItem.setPhoto(photo);
        feedItem.setName(name);
        feedItem.setDistance(distance);
        feedItem.setUtime(utime);
        feedItem.setType(type);
        feedItem.setLastEvent(lastEvent);
        
        return feedItem;
    }
}
