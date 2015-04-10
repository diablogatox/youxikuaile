package com.orfid.youxikuaile.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orfid.youxikuaile.Constants;
import com.orfid.youxikuaile.PinyinUtils;
import com.orfid.youxikuaile.R;

public class ContactsAdapterSF extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	ImageLoader imageLoader;
	private DisplayImageOptions options;

	public ContactsAdapterSF(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		
		imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
				.createDefault(context));
	}

	public int getCount() {
		return list == null ? 0 : list.size();  
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.select_friends, null);
			holder.iv_select_friends = (ImageView) convertView.findViewById(R.id.iv_select_friends);
			holder.iv_sf_pic = (ImageView) convertView.findViewById(R.id.iv_sf_pic);
			holder.tv_sf_name = (TextView) convertView.findViewById(R.id.tv_sf_name);
			holder.catalog = (TextView) convertView.findViewById(R.id.catalogTv1);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String catalog = PinyinUtils.converterToFirstSpell(list.get(position).get("name") + "").substring(0, 1);
		if (position == 0) {
			holder.catalog.setVisibility(View.VISIBLE);
			holder.catalog.setText(catalog);
		} else {
			String lastCatalog = PinyinUtils.converterToFirstSpell(list.get(position - 1).get("name") + "").substring(0, 1);
			if (catalog.equals(lastCatalog)) {
				holder.catalog.setVisibility(View.GONE);
			} else {
				holder.catalog.setVisibility(View.VISIBLE);
				holder.catalog.setText(catalog);
			}
		}
		
		holder.iv_select_friends.setImageResource(R.drawable.select_friends);
//		holder.iv_sf_pic.setImageResource((Integer) list.get(position).get("icon"));
		if (list.get(position).get("icon") != null) {
			ImageLoader.getInstance().displayImage(list.get(position).get("icon").toString(), holder.iv_sf_pic);
		}
		holder.tv_sf_name.setText((String) list.get(position).get("name"));
		

		return convertView;
	}

	public final class ViewHolder {

		public TextView catalog;
		public ImageView iv_select_friends;
		public ImageView iv_sf_pic;
		public TextView tv_sf_name;
	}

}