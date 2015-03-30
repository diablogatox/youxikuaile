package com.orfid.youxikuaile.pojo;

import java.util.List;

public class GameSitterItem {
	private String id;
	private GameItem game;
	private String desc;
	private UserItem user;
	private List<GameAreaItem> areas;
	private String utime;
	
	public GameSitterItem() {
	}

	public GameSitterItem(String id, GameItem game, String desc, UserItem user,
			List<GameAreaItem> areas, String utime) {
		super();
		this.id = id;
		this.game = game;
		this.desc = desc;
		this.user = user;
		this.areas = areas;
		this.utime = utime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GameItem getGame() {
		return game;
	}

	public void setGame(GameItem game) {
		this.game = game;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public UserItem getUser() {
		return user;
	}

	public void setUser(UserItem user) {
		this.user = user;
	}

	public List<GameAreaItem> getAreas() {
		return areas;
	}

	public void setAreas(List<GameAreaItem> areas) {
		this.areas = areas;
	}

	public String getUtime() {
		return utime;
	}

	public void setUtime(String utime) {
		this.utime = utime;
	}
	
}
