package com.orfid.youxikuaile.model;

public class contants {
	
	public String id;	
	public String title;	
	public String content;	
	public String ctime;	
	public String company_name;	
	public String uid; 
	public boolean isFollow;


	public contants(String id, String title, String content, String ctime,
			String company_name, String uid,boolean isFollow) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.ctime = ctime;
		this.company_name = company_name;
		this.uid = uid;
		this.isFollow = isFollow;
	}
	
	public contants(){
		
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public boolean getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}
	
	@Override
	public String toString() {
		return "contants [id=" + id + ", title=" + title + ", content="
				+ content + ", ctime=" + ctime + ", company_name="
				+ company_name + ", uid=" + uid + "]";
	}
	
}
