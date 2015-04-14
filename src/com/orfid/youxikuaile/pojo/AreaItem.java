package com.orfid.youxikuaile.pojo;

public class AreaItem {
	
	private String parentId;
	private String name;
	
	public AreaItem(String parentId, String name) {
		this.parentId = parentId;
		this.name = name;
	}
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
