package com.orfid.youxikuaile.pojo;

public class MessageItem {
	private String id;
	private String sendtime;
	private String text;
	private String file;
	private UserItem user;
	public MessageItem(String id, String sendtime, String text, String file, UserItem user) {
		this.id = id;
		this.sendtime = sendtime;
		this.text = text;
		this.file = file;
		this.user = user;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public UserItem getUser() {
		return user;
	}
	public void setUser(UserItem user) {
		this.user = user;
	}
}
