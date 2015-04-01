package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.orfid.youxikuaile.pojo.GameAreaItem;
import com.orfid.youxikuaile.pojo.GameItem;
import com.orfid.youxikuaile.pojo.GameSitterItem;
import com.orfid.youxikuaile.pojo.UserItem;

public class GameSitterItemsParser {
	public List<GameSitterItem> parse(JSONObject jObject) {
		JSONArray jGameSitterItems = null;
		try {
			jGameSitterItems = jObject.getJSONArray("items");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return getGameSitterItems(jGameSitterItems);
	}

	private List<GameSitterItem> getGameSitterItems(JSONArray jGameSitterItems) {
		int gameSitterItemsCount = jGameSitterItems.length();
        List<GameSitterItem> gameSitterItemsList = new ArrayList<GameSitterItem>();
        GameSitterItem gameSitterItem = null;

        for(int i=0; i<gameSitterItemsCount;i++) {
            try {
            	gameSitterItem = getGameSitterItem((JSONObject) jGameSitterItems.get(i));
            	gameSitterItemsList.add(gameSitterItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return gameSitterItemsList;
	}

	private GameSitterItem getGameSitterItem(JSONObject jsonObject) {
		GameSitterItem gameSitterItem = new GameSitterItem();
        String id = null, desc = null, utime = null, area = null;
        GameItem game = null;
        UserItem user = null;
        List<GameAreaItem> areas = new ArrayList<GameAreaItem>();

        try {
			id = jsonObject.getString("id");
			desc = jsonObject.getString("desc");
			utime = jsonObject.getString("utime");
			area = jsonObject.getString("gamearea");
			
			if (!area.trim().equals("") && !area.trim().equals("null")) {
				String[] gameAreas = area.split(",");
				for (int i=0; i<gameAreas.length; i++) {
					areas.add(new GameAreaItem(gameAreas[i]));
//					Log.d("testdddd=====>", gameAreas[i]);
				}
			}
			JSONObject jGame = jsonObject.getJSONObject("game");
			game = new GameItem(jGame.getString("id"), jGame.getString("name"), jGame.getString("img"));
			JSONObject jUser = jsonObject.getJSONObject("user");
			user = new UserItem(jUser.getString("uid"), jUser.getString("username"), jUser.getString("photo"), null, jUser.getString("type") );
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        gameSitterItem.setId(id);
        gameSitterItem.setDesc(desc);
        gameSitterItem.setUtime(utime);
        gameSitterItem.setAreas(areas);
        gameSitterItem.setGame(game);
        gameSitterItem.setUser(user);
        
        return gameSitterItem;
	}
}
