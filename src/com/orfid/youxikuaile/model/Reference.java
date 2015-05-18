package com.orfid.youxikuaile.model;

import com.orfid.youxikuaile.DynamicContent;
import com.tencent.connect.UserInfo;



public class Reference {

	private String feedid;
	private DynamicContent content;

	private UserInfo user;

	public String getFeedid() {
		return feedid;
	}

	public void setFeedid(String feedid) {
		this.feedid = feedid;
	}

	public DynamicContent getContent() {
		return content;
	}

	public void setContent(DynamicContent content) {
		this.content = content;
	}

	public Reference() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

}
