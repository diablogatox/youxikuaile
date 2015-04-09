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
    private String distance;
    private String utime;
    private String type;
    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getUtime() {
		return utime;
	}

	public void setUtime(String utime) {
		this.utime = utime;
	}

	public UserItem() {
	}

    public UserItem(String uid, String username, String photo,
			String signature, String type) {
		super();
		this.uid = uid;
		this.username = username;
		this.photo = photo;
		this.signature = signature;
		this.type = type;
	}
    
    public UserItem(String uid, String username, String photo,
			String signature, String type, boolean isFollow) {
		super();
		this.uid = uid;
		this.username = username;
		this.photo = photo;
		this.signature = signature;
		this.type = type;
		this.isFollow = isFollow;
	}

	public UserItem(String uid, String birthday, String sex, String username,
			String photo, String signature, boolean isFollow, String distance, String utime) {
		super();
		this.uid = uid;
		this.birthday = birthday;
		this.sex = sex;
		this.username = username;
		this.photo = photo;
		this.signature = signature;
		this.isFollow = isFollow;
		this.distance = distance;
		this.utime = utime;
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
