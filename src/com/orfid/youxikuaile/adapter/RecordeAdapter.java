package com.orfid.youxikuaile.adapter;

import java.util.List;

import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.model.LogInfo;
import com.orfid.youxikuaile.util.TimeUtils;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 此adapter,日志信息listview的adapter

 * @author clh
 *
 */
//自定义适配器Adapter  
public class RecordeAdapter extends BaseAdapter {

	private Activity activity = null;
	private List<LogInfo> recordeList;

	public RecordeAdapter(Activity activity, List<LogInfo> recordeList) {
		this.activity = activity;
		this.recordeList = recordeList;
	}

	public void optionsNotify(List<LogInfo> recordeList) {
		this.recordeList = recordeList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.recordeList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.recordeList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return this.recordeList.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			// 下拉项布局
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.center_chongzhi_recode_linearlayout_item, null);
			holder.textView_time = (TextView) convertView
					.findViewById(R.id.hayou_chongzhi_recorde_text_1);
			holder.textView_no = (TextView) convertView
					.findViewById(R.id.hayou_chongzhi_recorde_text_2);
			holder.textView_num = (TextView) convertView
					.findViewById(R.id.hayou_chongzhi_recorde_text_3);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.textView_time.setText(TimeUtils.getStrTime(String
				.valueOf(this.recordeList.get(position).getTime())));
		holder.textView_no.setText(this.recordeList.get(position).getNumber());
		holder.textView_num.setText(String.valueOf(Double
				.parseDouble(this.recordeList.get(position).getAmount()) * 10));
		return convertView;
	}

	private class Holder {
		TextView textView_time, textView_no, textView_num;
	}
}
