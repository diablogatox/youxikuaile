package com.orfid.youxikuaile.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orfid.youxikuaile.AdvertThreeDetailItemActivity;
import com.orfid.youxikuaile.ListItemClickHelp;
import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.model.contants;

public class AdvertThreeListView extends BaseAdapter {

	private Context context;
	private List<contants> fansList;
	private LayoutInflater lif_adapter;
	private DisplayImageOptions options;
	private ListItemClickHelp callback;
	private String uid;

	public AdvertThreeListView(Context context, List<contants> fansList,
			ListItemClickHelp callback, String uid) {
		this.context = context;
		this.fansList = fansList;
		this.callback = callback;
		this.uid = uid;
//		ApplicationData.initImageLoader(this.context);
		this.lif_adapter = LayoutInflater.from(this.context);
//		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.loading)
//				.showImageForEmptyUri(R.drawable.fail)
//				.showImageOnFail(R.drawable.fail).cacheInMemory(true)
//				.cacheOnDisc(true).considerExifParams(true)
//				.bitmapConfig(Bitmap.Config.ARGB_4444).build();

	}

	public void changeData(List<contants> fansList) {
		this.fansList = fansList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.fansList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.fansList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub

		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			// 下拉项布局
			convertView = lif_adapter.inflate(R.layout.advertthree_list_item,
					null);

			holder.add_gz = (ImageView) convertView
					.findViewById(R.id.center_fans_add_gz_img_advertthree);
			holder.userName = (TextView) convertView
					.findViewById(R.id.center_fans_user_name_advertthree);
			holder.userSignture = (TextView) convertView
					.findViewById(R.id.center_fans_user_signture_advertthree);

			holder.gz_layout = (RelativeLayout) convertView
					.findViewById(R.id.center_fans_admin_advertthree);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.userName.setText(this.fansList.get(position).getTitle());
		holder.userSignture.setText(this.fansList.get(position).getContent());

		final View img_pis = convertView;
		final int arg0 = position;
		final int id_img = holder.gz_layout.getId();
		if (!this.fansList.get(position).getIsFollow()) {
			holder.add_gz.setVisibility(View.VISIBLE);
			holder.gz_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (fansList.get(arg0).getTitle().equals("0")) {
						Intent ii = new Intent(AdvertThreeListView.this.context,
								AdvertThreeDetailItemActivity.class);
						ii.putExtra("id",
								AdvertThreeListView.this.fansList.get(arg0)
										.getId());

//						ii.putExtra("id",
//								AdvertThreeListView.this.fansList.get(arg0).getId());
//						
						ii.putExtra("userName", AdvertThreeListView.this.fansList
								.get(arg0).getTitle());

						ii.putExtra("userSignture",
								AdvertThreeListView.this.fansList.get(arg0)
										.getContent());
						
						AdvertThreeListView.this.context.startActivity(ii);
					} else {
						Intent ii = new Intent(AdvertThreeListView.this.context,
								AdvertThreeDetailItemActivity.class);
						ii.putExtra("uid",
								AdvertThreeListView.this.fansList.get(arg0)
										.getUid());
						ii.putExtra("userName", AdvertThreeListView.this.fansList
								.get(arg0).getTitle());

						ii.putExtra("userSignture",
								AdvertThreeListView.this.fansList.get(arg0)
										.getContent());

						AdvertThreeListView.this.context.startActivity(ii);
					}

				}
			});
		} else if (this.fansList.get(position).getIsFollow()) {
			holder.add_gz.setVisibility(View.VISIBLE);
			holder.gz_layout.setClickable(false);

			holder.gz_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fansList.get(arg0).getTitle().equals("0")) {
					Intent ii = new Intent(AdvertThreeListView.this.context,
							AdvertThreeDetailItemActivity.class);
					ii.putExtra("uid",
							AdvertThreeListView.this.fansList.get(arg0)
									.getUid());

					ii.putExtra("id",
							AdvertThreeListView.this.fansList.get(arg0).getId());
					
					ii.putExtra("userName", AdvertThreeListView.this.fansList
							.get(arg0).getTitle());

					ii.putExtra("userSignture",
							AdvertThreeListView.this.fansList.get(arg0)
									.getContent());
					AdvertThreeListView.this.context.startActivity(ii);
				} else {
					Intent ii = new Intent(AdvertThreeListView.this.context,
							AdvertThreeDetailItemActivity.class);
					ii.putExtra("uid",
							AdvertThreeListView.this.fansList.get(arg0)
									.getUid());
					ii.putExtra("userName", AdvertThreeListView.this.fansList
							.get(arg0).getTitle());

					ii.putExtra("userSignture",
							AdvertThreeListView.this.fansList.get(arg0)
									.getContent());

					AdvertThreeListView.this.context.startActivity(ii);
				}
			}
		});
		return convertView;

	}

	private class Holder {
		private ImageView add_gz;
		private TextView userName, userSignture;
		private RelativeLayout gz_layout;

	}

}
