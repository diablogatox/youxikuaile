package com.orfid.youxikuaile;

/**
 * 字母实体
*@author liuyinjun

* @date 2015-3-16
 */
public class Letter implements Comparable<Letter> {

	private int id;
	private String name;//名字
	private String firstLetter;//名字首字母
	

	public Letter() {
		super();
	}

	public Letter(int id, String name, String firstLetter) {
		super();
		this.id = id;
		this.name = name;
		this.firstLetter = firstLetter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}
	

	@Override
	public int compareTo(Letter another) {
		if (this.getFirstLetter().equals("@")
				|| another.getFirstLetter().equals("#")) {
			return -1;
		} else if (this.getFirstLetter().equals("#")
				|| another.getFirstLetter().equals("@")) {
			return 1;
		} else {
			return this.getFirstLetter().compareTo(another.getFirstLetter());
		}
	}

}
