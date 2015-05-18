package com.orfid.youxikuaile.model;

import java.util.List;

import com.orfid.youxikuaile.DynamicContent;
import com.orfid.youxikuaile.pojo.UserInfo;

/**
 * 此类,是的动态的model。

 * @author clh
 * 
 */
public class Dynamic {

	private String feedid;
	private UserInfo user;
	private DynamicContent content;
	private List<Reference> reference;
	private String publishtime;
	private String commentcount;
	private String forwardcount;
	private String praisecount;
	private boolean is_praised;
	private String parentnode;
	private String category;

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public DynamicContent getContent() {
		return content;
	}

	public void setContent(DynamicContent content) {
		this.content = content;
	}

	public List<Reference> getReference() {
		return reference;
	}

	public void setReference(List<Reference> reference) {
		this.reference = reference;
	}

	public String getFeedid() {
		return feedid;
	}

	public void setFeedid(String feedid) {
		this.feedid = feedid;
	}

	public String getPublishtime() {
		return publishtime;
	}

	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}

	public Dynamic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public String getForwardcount() {
		return forwardcount;
	}

	public void setForwardcount(String forwardcount) {
		this.forwardcount = forwardcount;
	}

	public String getPraisecount() {
		return praisecount;
	}

	public void setPraisecount(String praisecount) {
		this.praisecount = praisecount;
	}

	public boolean getIs_praised() {
		return is_praised;
	}

	public void setIs_praised(boolean is_praised) {
		this.is_praised = is_praised;
	}

	public String getParentnode() {
		return parentnode;
	}

	public void setParentnode(String parentnode) {
		this.parentnode = parentnode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
