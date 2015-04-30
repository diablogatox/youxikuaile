package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.SysNotifyItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class SysNotifyParser {

    public List<SysNotifyItem> parse(JSONObject jObject) {

        JSONArray jFollowItems = null;
        try {
            /** Retrieves all the elements */
            jFollowItems = jObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFollowItems(jFollowItems);
    }


    private List<SysNotifyItem> getFollowItems(JSONArray jFollowItems) {
        int followItemsCount = jFollowItems.length();
        List<SysNotifyItem> followItemsList = new ArrayList<SysNotifyItem>();
        SysNotifyItem followItem = null;

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

    private SysNotifyItem getFollowItem(JSONObject jItem) {

        SysNotifyItem item = new SysNotifyItem();
        
        String id = null, text = null, type = null, action = null;
        
        try {
	      id = jItem.getString("id");
	      text = jItem.getString("text");
	      type = jItem.getString("type");
	      action = jItem.getString("action");
        } catch (Exception e) {
        	e.printStackTrace();
        }
   
        item.setId(id);
        item.setText(text);
        item.setType(type);
        item.setAction(action);

        return item;
    }
    
}
