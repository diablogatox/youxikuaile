package com.orfid.youxikuaile;

import java.util.List;

import com.orfid.youxikuaile.model.Attach;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 此类,是的动态neir的model。

 * @author clh
 * 
 */
public class DynamicContent implements Parcelable {
	private String text;
	private List<Attach> attach;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Attach> getAttach() {
		return attach;
	}

	public void setAttach(List<Attach> attach) {
		this.attach = attach;
	}

	public DynamicContent() {
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

		dest.writeString(text);

		dest.writeList(attach);

	}

	public static final Parcelable.Creator<DynamicContent> CREATOR = new Parcelable.Creator<DynamicContent>() {
		public DynamicContent createFromParcel(Parcel in) {
			return new DynamicContent(in);
		}

		public DynamicContent[] newArray(int size) {
			return new DynamicContent[size];
		}
	};

	private DynamicContent(Parcel in) {

		this.text = in.readString();
		this.attach = in.readArrayList(Attach.class.getClassLoader());

	}

}
