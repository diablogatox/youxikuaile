package com.orfid.youxikuaile.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orfid.youxikuaile.ListItemClickHelp;
import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.pojo.GameUser;
import com.orfid.youxikuaile.widget.CircularImageView;
/**
 * 此adapter,是积分工厂里，雇工用户列表的adapter

 * @author clh
 *
 */
public class EmplyeeAdapter extends BaseAdapter {
	private List<GameUser> userInfo;
	private Context context;
	private LayoutInflater lift;
	private ListItemClickHelp callback;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<Integer> isChecked;

	public EmplyeeAdapter(Context context, List<GameUser> userInfo,
			List<Integer> isChecked, ListItemClickHelp callback) {
		super();
		this.context = context;
		this.userInfo = userInfo;
		this.callback = callback;
		this.isChecked = isChecked;
		this.lift = LayoutInflater.from(this.context);
	}

	public void changeDate(List<GameUser> userInfo, List<Integer> isChecked) {
		this.userInfo = userInfo;
		this.isChecked = isChecked;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.userInfo.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.userInfo.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return this.userInfo.get(arg0).hashCode();
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		Holder holder;

		if (convertView == null) {
			// Inflate the view since it does not exist
			holder = new Holder();
			convertView = this.lift.inflate(R.layout.score_emplyee_layout_item,
					null);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.hayou_game_1_emolyee_item_layout);
			holder.tvvv = (CircularImageView) convertView
					.findViewById(R.id.hayou_game_1_emolyee_item_user_icon);
			holder.textViewName = (TextView) convertView
					.findViewById(R.id.hayou_game_1_emolyee_item_user_name);
			holder.choiceText = (TextView) convertView
					.findViewById(R.id.hayou_game_1_emolyee_item_user_choice);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		if (isChecked.contains(this.userInfo.get(position).getUid())) {
			holder.choiceText.setVisibility(View.VISIBLE);
		} else {
			holder.choiceText.setVisibility(View.GONE);
		}
		holder.textViewName.setText(this.userInfo.get(position).getUsername());
		if (this.userInfo.get(position).getPhoto() != null) {
			if (!this.userInfo.get(position).getPhoto().equals("")) {
				ImageLoader.getInstance().displayImage(
						this.userInfo.get(position).getPhoto(), holder.tvvv);
			} else {
				holder.tvvv.setImageResource(R.drawable.gb_user_icon);
			}
		} else {
			holder.tvvv.setImageResource(R.drawable.gb_user_icon);
		}

		final View view = convertView;
		final View layoutItem = holder.choiceText;
		final int arg0 = this.userInfo.get(position).getUid();
		final int id = holder.layout.getId();
		holder.layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (layoutItem.getVisibility() == View.VISIBLE) {
					layoutItem.setVisibility(View.GONE);
				} else {
					layoutItem.setVisibility(View.VISIBLE);
				}
				callback.onClick(view, parent, arg0, id);
			}
		});

		return convertView;
	}

	private class Holder {
		private TextView textViewName, choiceText;
		private CircularImageView tvvv;
		private RelativeLayout layout;

	}
}