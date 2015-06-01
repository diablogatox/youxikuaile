package com.orfid.youxikuaile.pojo;

public class OrganizationItem {
	
	private String uid;
	private String name;
	private String photo;
	private String distance;
	private String utime;
	private String type;
	private EventItem lastEvent;

	public OrganizationItem() {}
	
	public OrganizationItem(String uid, String name, String photo,
			String distance, String utime, EventItem lastEvent) {
		super();
		this.uid = uid;
		this.name = name;
		this.photo = photo;
		this.distance = distance;
		this.utime = utime;
		this.lastEvent = lastEvent;
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

	public EventItem getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(EventItem lastEvent) {
		this.lastEvent = lastEvent;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
