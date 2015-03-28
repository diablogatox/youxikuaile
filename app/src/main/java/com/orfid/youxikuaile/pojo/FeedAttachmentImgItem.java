package com.orfid.youxikuaile.pojo;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/3/6.
 */
public class FeedAttachmentImgItem {
    private Bitmap image;
    private String url;
    private String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FeedAttachmentImgItem(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public FeedAttachmentImgItem(Bitmap image) {
        this.image = image;

    }
}
