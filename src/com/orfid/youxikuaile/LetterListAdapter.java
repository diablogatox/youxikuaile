package com.orfid.youxikuaile;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 右边字母列表适配器
*@author liuyinjun

* @date 2015-3-16
 */
public class LetterListAdapter extends BaseAdapter implements SectionIndexer{
	private List<Letter> mList = null;
	private Context mContext;
	
	public LetterListAdapter(Context mContext, List<Letter> list) {
		this.mContext = mContext;
		this.mList = list;
	}

	public int getCount() {
		if(mList == null)
			return 0;
		return this.mList.size();
	}

	public Letter getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final Letter letter = mList.get(position);
		if (view == null) { 
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.letter_list_item, null);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.letter_tv);
			viewHolder.tvName = (TextView) view.findViewById(R.id.name_tv);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(letter.getFirstLetter());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
	
		viewHolder.tvName.setText(getItem(position).getName());
		
		return view;

	}
	

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvName;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return mList.get(position).getFirstLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).getFirstLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	

	@Override
	public Object[] getSections() {
		return null;
	}
}