package com.orfid.youxikuaile.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orfid.youxikuaile.PinyinUtils;
import com.orfid.youxikuaile.R;


public class ContactsAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	ImageLoader imageLoader;
	private DisplayImageOptions options;

	public ContactsAdapter(Context context, List<Map<String, Object>> list) {
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

			convertView = LayoutInflater.from(context).inflate(R.layout.add_friends1, null);

			holder.rl_friends111 = (RelativeLayout) convertView.findViewById(R.id.rl_friends111);
			holder.icon = (ImageView) convertView.findViewById(R.id.iv_add_friends1);
			holder.title = (TextView) convertView.findViewById(R.id.tv_add_friends_name);
			holder.catalog = (TextView) convertView.findViewById(R.id.catalogTv);

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
		
		final String uid = (String) list.get(position).get("uid");
		holder.rl_friends111.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(context,HomeFriendsPicActivity.class);
//				intent.putExtra("uid", uid);
//				intent.putExtra("beingFriend", true);
//				context.startActivity(intent);
			}
		});
//		holder.icon.setImageResource((Integer) list.get(position).get("icon"));
		ImageLoader.getInstance().displayImage(list.get(position).get("icon").toString(), holder.icon);
		holder.title.setText((String) list.get(position).get("name"));
		return convertView;
	}

	public final class ViewHolder {
		public RelativeLayout rl_friends111;
		public TextView catalog;
		public ImageView icon;
		public TextView title;
	}

}