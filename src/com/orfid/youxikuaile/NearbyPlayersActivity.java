package com.orfid.youxikuaile;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NearbyPlayersActivity extends Activity {

    private ListView nearbyPlayersLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_nearby_players);
        nearbyPlayersLv = (ListView) findViewById(R.id.lv_nearby_players);

        nearbyPlayersLv.setAdapter(new MyAdapter());
	}

    public void goBack(View view) {
        finish();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PictureViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new PictureViewHolder();
                convertView = LayoutInflater.from(NearbyPlayersActivity.this).inflate(
                        R.layout.nearby_player, parent, false);
//				viewHolder.iv_friends_pic = (ImageView) convertView
//						.findViewById(R.id.iv_friends_pic);
//				viewHolder.tv_friends_name = (TextView) convertView
//						.findViewById(R.id.tv_friends_name);
//				viewHolder.tv_music_content = (TextView) convertView
//						.findViewById(R.id.tv_music_content);
//				viewHolder.tv_distance = (TextView) convertView
//						.findViewById(R.id.tv_distance);
//				viewHolder.btn_voice = (Button) convertView
//						.findViewById(R.id.btn_voice);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PictureViewHolder) convertView.getTag();
            }

//			viewHolder.tv_friends_name.setText("林俊杰");//名字
//			viewHolder.tv_distance.setText(500 + "m"); //距离
//			// 在下面进行判断，并显示或隐藏歌词和语音，实现相应的功能
//			viewHolder.tv_music_content.setText("她静悄悄的来过，她慢慢带走沉默。只是最后的承诺，还是没有带走了"); // 歌词
//			viewHolder.btn_voice.setVisibility(View.GONE);

            return convertView;
        }

        public class PictureViewHolder {
            ImageView iv_friends_pic;
            TextView tv_friends_name;
            TextView tv_music_content;
        }

    }
}
