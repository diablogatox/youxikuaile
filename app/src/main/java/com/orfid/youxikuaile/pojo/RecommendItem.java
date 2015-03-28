package com.orfid.youxikuaile.pojo;

public class RecommendItem {
	
	public RecommendItem() {};
	
	public RecommendItem(String img, String content, String id, String title,
			String ctime, String link) {
		super();
		this.img = img;
		this.content = content;
		this.id = id;
		this.title = title;
		this.ctime = ctime;
		this.link = link;
	}
	
	private String img;
	private String content;
	private String id;
	private String title;
	private String ctime;
	private String link;
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
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
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
}
