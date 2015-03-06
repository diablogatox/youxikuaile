package com.orfid.youxikuaile.pojo;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/3/6.
 */
public class FeedAttachmentImgItem {
    private Bitmap image;

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
