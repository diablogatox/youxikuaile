package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.UserItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class FollowItemsParser {

    public List<UserItem> parse(JSONObject jObject) {

        JSONArray jFollowItems = null;
        try {
            /** Retrieves all the elements */
            jFollowItems = jObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFollowItems(jFollowItems);
    }


    private List<UserItem> getFollowItems(JSONArray jFollowItems) {
        int followItemsCount = jFollowItems.length();
        List<UserItem> followItemsList = new ArrayList<UserItem>();
        UserItem followItem = null;

        for(int i=0; i<followItemsCount;i++) {
            try {
                followItem = getFollowItem((JSONObject) jFollowItems.get(i));
                followItemsList.add(followItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return followItemsList;
    }

    private UserItem getFollowItem(JSONObject jFollowItem) {

        UserItem followItem = new UserItem();
        
        String uid = null, photo = null, username = null, latestEvent = "", type = null;
        
        try {
	        uid = jFollowItem.getString("uid");
	        if (!jFollowItem.isNull("photo")) {
	        	photo = jFollowItem.getString("photo");
	        }
	        username = jFollowItem.getString("username");
	        latestEvent = jFollowItem.getString("signature");
	        type = jFollowItem.getString("type");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        followItem.setUid(uid);
        followItem.setPhoto(photo);
        followItem.setUsername(username);
        followItem.setLatestEvent(latestEvent);
        followItem.setType(type);

        return followItem;
    }
}
