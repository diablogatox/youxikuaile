package com.orfid.youxikuaile;

import android.graphics.Bitmap;

public class ChatEntity {

	private String id;
	private String userId;
	private String userName;
	private String userImage;
	private String content;
	private String chatTime;
	private boolean isComeMsg;
	private boolean isFollowed;
	private String recordTime;
	private String recordUrl;
	private String imgAttachmentUrl;
	private Bitmap imageAttachmentBitmap;
//	private boolean isNofityMsg;

	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
//	public String getChatTime() {
//		return chatTime;
//	}
//	public void setChatTime(String chatTime) {
//		this.chatTime = chatTime;
//	}
	public boolean isComeMsg() {
		return isComeMsg;
	}
	public void setComeMsg(boolean isComeMsg) {
		this.isComeMsg = isComeMsg;
	}
//	public boolean isNofityMsg() {
//		return isNofityMsg;
//	}
//	public void setNofityMsg(boolean isNofityMsg) {
//		this.isNofityMsg = isNofityMsg;
//	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public String getChatTime() {
		return chatTime;
	}
	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}
	public String getImgAttachmentUrl() {
		return imgAttachmentUrl;
	}
	public void setImgAttachmentUrl(String imgAttachmentUrl) {
		this.imgAttachmentUrl = imgAttachmentUrl;
	}
	public Bitmap getImageAttachmentBitmap() {
		return imageAttachmentBitmap;
	}
	public void setImageAttachmentBitmap(Bitmap imageAttachmentBitmap) {
		this.imageAttachmentBitmap = imageAttachmentBitmap;
	}
	public String getRecordUrl() {
		return recordUrl;
	}
	public void setRecordUrl(String recordUrl) {
		this.recordUrl = recordUrl;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public boolean isFollowed() {
		return isFollowed;
	}
	public void setFollowed(boolean isFollowed) {
		this.isFollowed = isFollowed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
