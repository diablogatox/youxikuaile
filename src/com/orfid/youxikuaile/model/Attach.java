package com.orfid.youxikuaile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Attach implements Parcelable {
	private String id;
	// "image|audio",
	private String type;
	private String url;

	private String width;
	private String height;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Attach() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(id);
		dest.writeString(type);
		dest.writeString(url);
		dest.writeString(width);
		dest.writeString(height);
	}

	public static final Parcelable.Creator<Attach> CREATOR = new Parcelable.Creator<Attach>() {
		public Attach createFromParcel(Parcel in) {
			return new Attach(in);
		}

		public Attach[] newArray(int size) {
			return new Attach[size];
		}
	};

	private Attach(Parcel in) {

		this.id = in.readString();

		this.type = in.readString();
		this.url = in.readString();
		this.width = in.readString();
		this.height = in.readString();
	}

}
