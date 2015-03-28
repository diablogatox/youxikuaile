package com.orfid.youxikuaile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.orfid.youxikuaile.widget.HorizontalListView;

/**
 * Created by Administrator on 2015/2/27.
 */
public class OnlineSittersActivity extends Activity implements GestureDetector.OnGestureListener, View.OnClickListener {

    HorizontalListView sitterGamesLv;
    ImageButton backBtn;
    Button sitterOfflineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online_sitter);

        backBtn = (ImageButton) findViewById(R.id.btn_back);
        sitterOfflineBtn = (Button) findViewById(R.id.btn_sitter_offline);
        sitterGamesLv = (HorizontalListView) findViewById(R.id.lv_sitter_games);

        backBtn.setOnClickListener(this);
        sitterOfflineBtn.setOnClickListener(this);

        sitterGamesLv.setAdapter(new MyAdapter());
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_sitter_offline:
                startActivity(new Intent(this, OfflineSittersActivity.class));
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 14;
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
                convertView = LayoutInflater.from(OnlineSittersActivity.this).inflate(
                        R.layout.sitter_game, parent, false);
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
