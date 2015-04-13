package com.orfid.youxikuaile.pojo;

public class MessageSession {
	
	private String id;
	private String newmsg;
	private UserItem[] users;
	private String type;
	private MessageItem message;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getNewmsg() {
		return newmsg;
	}
	public void setNewmsg(String newmsg) {
		this.newmsg = newmsg;
	}
	public UserItem[] getUsers() {
		return users;
	}
	public void setUsers(UserItem[] users) {
		this.users = users;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public MessageItem getMessage() {
		return message;
	}
	public void setMessage(MessageItem message) {
		this.message = message;
	}

}
