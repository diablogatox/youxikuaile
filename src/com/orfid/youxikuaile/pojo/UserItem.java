package com.orfid.youxikuaile.pojo;

/**
 * Created by Administrator on 2015/3/5.
 */
public class UserItem {
    private String uid;
    private String birthday;
    private String sex;
    private String username;
    private String photo;
    private String signature;
    private boolean isFollow;

    public UserItem() {
	}

	public UserItem(String uid, String birthday, String sex, String username, String photo, String signature) {
        this.uid = uid;
        this.birthday = birthday;
        this.sex = sex;
        this.username = username;
        this.photo = photo;
        this.signature = signature;
    }

    public UserItem(String uid, String birthday, String sex, String username,
			String photo, String signature, boolean isFollow) {
		super();
		this.uid = uid;
		this.birthday = birthday;
		this.sex = sex;
		this.username = username;
		this.photo = photo;
		this.signature = signature;
		this.isFollow = isFollow;
	}

	public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

	public boolean isFollow() {
		return isFollow;
	}

	public void setFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}
}
