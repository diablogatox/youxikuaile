package com.orfid.youxikuaile.pojo;

public class EventItem {
	String content;
	String id;
	String title;
	String ctmie;
	
	public EventItem(String content, String id, String title, String ctmie) {
		super();
		this.content = content;
		this.id = id;
		this.title = title;
		this.ctmie = ctmie;
	}
	
	
	public EventItem() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCtmie() {
		return ctmie;
	}
	public void setCtmie(String ctmie) {
		this.ctmie = ctmie;
	}
	
}
