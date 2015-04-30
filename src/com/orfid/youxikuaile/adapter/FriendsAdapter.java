package com.orfid.youxikuaile.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.pojo.RankUser;
import com.orfid.youxikuaile.widget.CircularImageView;
/**
 * 此adapter,是积分工厂里，好友积分排名列表的adapter

 * @author clh
 *
 */
public class FriendsAdapter extends BaseAdapter {
	private List<RankUser> userInfo;
	private Context context;
	private LayoutInflater lift;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private int page;

	public FriendsAdapter(Context context, List<RankUser> userInfo, int page) {
		super();

		this.context = context;
		this.userInfo = userInfo;
		this.page = page;
		this.lift = LayoutInflater.from(this.context);
		// TODO Auto-generated constructor stub
		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.ARGB_4444).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

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
			convertView = this.lift.inflate(
					R.layout.score_emplyee_layout_item_rank, null);

			holder.tvvv = (CircularImageView) convertView
					.findViewById(R.id.hayou_game_1_rank_item_user_icon);
			holder.textViewName = (TextView) convertView
					.findViewById(R.id.hayou_game_1_rank_item_user_name);
			holder.scoreText = (TextView) convertView
					.findViewById(R.id.hayou_game_1_rank_item_user_score);
			holder.numText = (TextView) convertView
					.findViewById(R.id.hayou_game_1_rank_item_user_num);
			holder.mainLayout = (RelativeLayout) convertView
					.findViewById(R.id.hayou_game_1_rank_item_layout);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		// Populate the text
		Typeface typeFace = Typeface.createFromAsset(this.context.getAssets(),
				"fonts/FZHPJW.TTF");
		holder.textViewName.setText(this.userInfo.get(position).getUsername());
		holder.numText.setText(String.valueOf(7 * (page - 1) + position + 1));
		holder.scoreText.setText(String.valueOf(this.userInfo.get(position)
				.getIntegral()));
		holder.numText.setTypeface(typeFace);
		holder.scoreText.setTypeface(typeFace);
		holder.tvvv.setBorderWidth(this.context.getResources()
				.getDimensionPixelSize(R.dimen.hayou_image_border_width));
		holder.tvvv.setBorderColor(0x80CFD3DB);
		if (this.userInfo.get(position).getPhoto() != null) {
			if (!this.userInfo.get(position).getPhoto().equals("")) {
				ImageLoader.getInstance().displayImage(
						this.userInfo.get(position).getPhoto(), holder.tvvv,
						options);
			} else {
				holder.tvvv.setImageResource(R.drawable.gb_user_icon);
			}
		} else {
			holder.tvvv.setImageResource(R.drawable.gb_user_icon);
		}

		// final View view = holder.mainLayout;
		// final int arg0 = this.userInfo.get(position).getUid();
		// final int id = holder.mainLayout.getId();
		// holder.mainLayout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// callback.onClick(view, parent, arg0, id);
		// }
		// });
		return convertView;
	}

	private class Holder {
		private TextView textViewName, scoreText, numText;
		private CircularImageView tvvv;
		private RelativeLayout mainLayout;

	}
}