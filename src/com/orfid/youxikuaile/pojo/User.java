package com.orfid.youxikuaile.pojo;

import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private String uid;
	private String userName;
	private String mobile;
	private int sex;
	private String age;
	private String message;
	private String province;
	private String city;
	private String signature;
	private String photo;
	private String school_id;
	private int identity;
	private String bg;
	private String vip;
	private String birthday;
	private String contact;
	
	public String getMsg() {
		return message;
	}

	public void setMsg(String message) {
		this.message = message;
	}
	
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getIdentity() {
		return identity;
	}

	public void setIdentity(int identity) {
		this.identity = identity;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// dest.writeList(this.at);

		dest.writeString(uid);
		dest.writeString(userName);
		dest.writeString(mobile);
		dest.writeInt(sex);
		dest.writeString(age);
		dest.writeString(province);
		dest.writeString(city);
		dest.writeString(signature);
		dest.writeString(photo);
		dest.writeString(school_id);
		dest.writeInt(identity);
		dest.writeString(vip);
		dest.writeString(birthday);
		dest.writeString(message);
		dest.writeString(contact);
		
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	private User(Parcel in) {
		this.uid = in.readString();
		this.userName = in.readString();
		this.mobile = in.readString();
		this.sex = in.readInt();
		this.age = in.readString();
		this.province = in.readString();
		this.city = in.readString();
		this.signature = in.readString();
		this.photo = in.readString();
		this.school_id = in.readString();
		this.identity = in.readInt();
		this.vip = in.readString();
		this.birthday = in.readString();
		this.message = in.readString();
		this.contact = in.readString();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.userName;
	}

}
