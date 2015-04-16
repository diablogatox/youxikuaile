package com.orfid.youxikuaile.pojo;

public class FeedCommentItem {
	
	private String feedCommentId;
	private UserItem commentUser;
	private String commentMsg;
	private String commentTime;
	
	public UserItem getCommentUser() {
		return commentUser;
	}
	public void setCommentUser(UserItem commentUser) {
		this.commentUser = commentUser;
	}
	public String getCommentMsg() {
		return commentMsg;
	}
	public void setCommentMsg(String commentMsg) {
		this.commentMsg = commentMsg;
	}
	public String getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}
	public String getFeedCommentId() {
		return feedCommentId;
	}
	public void setFeedCommentId(String feedCommentId) {
		this.feedCommentId = feedCommentId;
	}
	
}
