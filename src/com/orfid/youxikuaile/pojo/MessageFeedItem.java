package com.orfid.youxikuaile.pojo;

public class MessageFeedItem {
	
	private String id;
	private UserItem user;
	private String content;
	private String pubTime;
	
	
	public MessageFeedItem() {
		super();
	}

	public MessageFeedItem(UserItem user, String content, String pubTime) {
		this.user = user;
		this.content = content;
		this.pubTime = pubTime;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserItem getUser() {
		return user;
	}
	public void setUser(UserItem user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPubTime() {
		return pubTime;
	}
	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}
	
}
