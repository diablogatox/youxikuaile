package com.orfid.youxikuaile.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.pojo.MessageItem;
import com.orfid.youxikuaile.pojo.MessageSession;
import com.orfid.youxikuaile.pojo.UserItem;

/**
 * Created by Administrator on 2015/3/5.
 */
public class MessageSessionItemsParser {

    public List<MessageSession> parse(JSONObject jObject) {

        JSONArray jMessageSessionItems = null;
        try {
            /** Retrieves all the elements */
            jMessageSessionItems = jObject.getJSONArray("sessions");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getMessageSessionItems(jMessageSessionItems);
    }


    private List<MessageSession> getMessageSessionItems(JSONArray jMessageSessionItems) {
        int messageSessionItemsCount = jMessageSessionItems.length();
        List<MessageSession> messageSessionItemsList = new ArrayList<MessageSession>();
        MessageSession messageSessionItem = null;

        for(int i=0; i<messageSessionItemsCount;i++) {
            try {
                messageSessionItem = getMessageSessionItem((JSONObject) jMessageSessionItems.get(i));
                messageSessionItemsList.add(messageSessionItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messageSessionItemsList;
    }

    private MessageSession getMessageSessionItem(JSONObject jMessageSessionItem) {

        MessageSession messageSessionItem = new MessageSession();
        String newmsg = null, sid = null, type = null;
        MessageItem lastMessage = null;
        List<UserItem> users = new ArrayList<UserItem>();
        try {
			newmsg = jMessageSessionItem.getString("newmsg");
			if (!jMessageSessionItem.getString("lastMessage").equals("[]")) {
				JSONObject jMessage = jMessageSessionItem.getJSONObject("lastMessage");
				JSONObject jUser = jMessage.getJSONObject("user");
				lastMessage = new MessageItem(jMessage.getString("id"), jMessage.getString("sendtime"),
						jMessage.getString("text"), null, new UserItem(jUser.getString("uid"), jUser.getString("username"), 
								jUser.getString("photo"), jUser.getString("signature"), jUser.getString("type")));
			}
			sid = jMessageSessionItem.getString("sid");
			JSONArray jUsers = jMessageSessionItem.getJSONArray("users");
			if (jUsers.length() > 0) {
				for (int i=0; i<jUsers.length(); i++) {
					JSONObject jUser = jUsers.getJSONObject(i);
					users.add(new UserItem(jUser.getString("uid"), jUser.getString("username"), jUser.getString("photo"), 
							jUser.getString("signature"), jUser.getString("type")));
				}
			}
			type = jMessageSessionItem.getString("type");
			
			messageSessionItem.setNewmsg(newmsg);
			messageSessionItem.setMessage(lastMessage);
			messageSessionItem.setId(sid);
			messageSessionItem.setType(type);
			messageSessionItem.setUsers(users.toArray(new UserItem[users.size()]));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return messageSessionItem;
    }
}
