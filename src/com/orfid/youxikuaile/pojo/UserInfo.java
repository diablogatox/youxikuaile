package com.orfid.youxikuaile.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {

	private String uid;
	private String username;
	private String photo;
	private String identity;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(uid);
		dest.writeString(username);
		dest.writeString(photo);
		dest.writeString(identity);

	}

	public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
		public UserInfo createFromParcel(Parcel in) {
			return new UserInfo(in);
		}

		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
	};

	private UserInfo(Parcel in) {
		this.uid = in.readString();
		this.username = in.readString();
		this.photo = in.readString();
		this.identity = in.readString();

	}

}
