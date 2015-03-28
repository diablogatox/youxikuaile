package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.RecommendItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class RecommendItemsParser {

    public List<RecommendItem> parse(JSONObject jObject) {

        JSONArray jFollowItems = null;
        try {
            /** Retrieves all the elements */
            jFollowItems = jObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getFollowItems(jFollowItems);
    }


    private List<RecommendItem> getFollowItems(JSONArray jFollowItems) {
        int followItemsCount = jFollowItems.length();
        List<RecommendItem> followItemsList = new ArrayList<RecommendItem>();
        RecommendItem followItem = null;

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

    private RecommendItem getFollowItem(JSONObject jFollowItem) {

        RecommendItem followItem = new RecommendItem();
        
        String img = null, content = null, id = null, 
        		title = null, ctime = null, link = null;
        
        try {
	        img = jFollowItem.getString("img");
	        content = jFollowItem.getString("content");
	        id = jFollowItem.getString("id");
	        title = jFollowItem.getString("title");
	        ctime = jFollowItem.getString("ctime");
	        link = jFollowItem.getString("link");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        followItem.setImg(img);
        followItem.setContent(content);
        followItem.setId(id);
        followItem.setTitle(title);
        followItem.setCtime(ctime);
        followItem.setLink(link);

        return followItem;
    }
}
