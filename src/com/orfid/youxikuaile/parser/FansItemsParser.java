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
public class FansItemsParser {
	
	private boolean isNewFansNotify = false;
	
	public FansItemsParser() {};
	
	public FansItemsParser(boolean isNewFansNotify) {
		this.isNewFansNotify = isNewFansNotify;
	}

    public List<UserItem> parse(JSONObject jObject) {

        JSONArray jFollowItems = null;
        try {
            /** Retrieves all the elements */
        	if (isNewFansNotify == true) {
        		jFollowItems = jObject.getJSONArray("data");
        	} else {
        		jFollowItems = jObject.getJSONArray("items");
        	}
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
        
        String uid = null, photo = null, username = null, type = null;
        boolean isFollow = false;
        
        if (isNewFansNotify == true) {
        	try {
				JSONObject jUserObj = jFollowItem.getJSONObject("user");
				uid = jUserObj.getString("uid");
		        photo = jUserObj.getString("photo");
		        username = jUserObj.getString("username");
		        isFollow = jUserObj.getBoolean("isFollow");
		        type = jUserObj.getString("type");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        } else {
	        try {
		        uid = jFollowItem.getString("uid");
		        photo = jFollowItem.getString("photo");
		        username = jFollowItem.getString("username");
		        isFollow = jFollowItem.getBoolean("isFollow");
		        type = jFollowItem.getString("type");
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
        }
        
        followItem.setUid(uid);
        followItem.setPhoto(photo);
        followItem.setUsername(username);
        followItem.setFollow(isFollow);
        followItem.setType(type);

        return followItem;
    }
}
