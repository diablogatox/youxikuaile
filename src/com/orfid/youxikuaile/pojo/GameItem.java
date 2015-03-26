package com.orfid.youxikuaile.pojo;

public class GameItem {
	
	private String id;
	private String name;
	private String photo;
	private String img;
	private boolean isSelected;
	
	public GameItem(String id, String name, String photo) {
		super();
		this.id = id;
		this.name = name;
		this.photo = photo;
	}
	
	public GameItem(String id, String name, String photo, String img) {
		super();
		this.id = id;
		this.name = name;
		this.photo = photo;
		this.img = img;
	}

	public GameItem() {
		super();
	}

	public GameItem(String name, String photo) {
		super();
		this.name = name;
		this.photo = photo;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
	
}
