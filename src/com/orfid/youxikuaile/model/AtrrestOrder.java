package com.orfid.youxikuaile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AtrrestOrder implements Parcelable {

	private String icon;
	private String name;
	private String sex;
	private String job;
	private String error;
	private String postname;
	private String address;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public AtrrestOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getPostname() {
		return postname;
	}

	public void setPostname(String postname) {
		this.postname = postname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(icon);
		dest.writeString(name);
		dest.writeString(sex);
		dest.writeString(job);
		dest.writeString(error);
		dest.writeString(postname);
		dest.writeString(address);

	}

	public static final Parcelable.Creator<AtrrestOrder> CREATOR = new Parcelable.Creator<AtrrestOrder>() {
		public AtrrestOrder createFromParcel(Parcel in) {
			return new AtrrestOrder(in);
		}

		public AtrrestOrder[] newArray(int size) {
			return new AtrrestOrder[size];
		}
	};

	private AtrrestOrder(Parcel in) {

		this.icon = in.readString();
		this.name = in.readString();
		this.sex = in.readString();
		this.job = in.readString();
		this.error = in.readString();
		this.postname = in.readString();
		this.address = in.readString();

	}
}
