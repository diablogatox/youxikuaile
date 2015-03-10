package com.orfid.youxikuaile.pojo;

import java.util.List;

/**
 * Created by Administrator on 2015/3/5.
 */
public class FeedItem {

    private long feedId;
    private UserItem user;
    private String contentText;
    private int commentCount;
    private int forwardCount;
    private int praiseCount;
    private String publishTime;
    private int type;
    private List<FeedAttachmentImgItem> imgItems;

    public List<FeedAttachmentImgItem> getImgItems() {
        return imgItems;
    }

    public void setImgItems(List<FeedAttachmentImgItem> imgItems) {
        this.imgItems = imgItems;
    }

    public FeedItem() {
    }

    public FeedItem(long feedId, UserItem user, String contentText, int commentCount, int forwardCount, int praiseCount, String publishTime, int type, List<FeedAttachmentImgItem> imgItems) {
        this.feedId = feedId;
        this.user = user;
        this.contentText = contentText;
        this.commentCount = commentCount;
        this.forwardCount = forwardCount;
        this.praiseCount = praiseCount;
        this.publishTime = publishTime;
        this.type = type;
        this.imgItems = imgItems;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
