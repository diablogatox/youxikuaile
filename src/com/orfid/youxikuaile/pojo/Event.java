package com.orfid.youxikuaile.pojo;

public class Event {
	private String ctime;
	private String title;
	
	public Event(String ctime, String title) {
		super();
		this.ctime = ctime;
		this.title = title;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
