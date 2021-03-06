package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.EventItem;
import com.orfid.youxikuaile.pojo.UserItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class EventItemsParser {

    public List<EventItem> parse(JSONObject jObject) {

        JSONArray jFollowItems = null;
        try {
        	jFollowItems = jObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFollowItems(jFollowItems);
    }


    private List<EventItem> getFollowItems(JSONArray jFollowItems) {
        int followItemsCount = jFollowItems.length();
        List<EventItem> followItemsList = new ArrayList<EventItem>();
        EventItem followItem = null;

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

    private EventItem getFollowItem(JSONObject jEventItem) {

        EventItem eventItem = new EventItem();
        
        String id = null, title = null, ctmie = null, content = null;
        List<UserItem> users = new ArrayList<UserItem>();
        try {
	        id = jEventItem.getString("id");
	        title = jEventItem.getString("title");
	        ctmie = jEventItem.getString("ctime");
	        content = jEventItem.getString("content");
	        JSONArray jUsers = jEventItem.getJSONArray("joins");
	        if (jUsers.length() > 0) {
				for (int i=0; i<jUsers.length(); i++) {
					JSONObject jUser = jUsers.getJSONObject(i);
					users.add(new UserItem(jUser.getString("uid"), jUser.getString("username"), jUser.getString("photo"), 
							jUser.getString("signature"), jUser.getString("type")));
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        eventItem.setId(id);
        eventItem.setTitle(title);
        eventItem.setCtmie(ctmie);
        eventItem.setContent(content);
        eventItem.setUsers(users.toArray(new UserItem[users.size()]));

        return eventItem;
    }
}
