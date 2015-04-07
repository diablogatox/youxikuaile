package com.orfid.youxikuaile;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VoiceStartActivity extends Activity {
	
	private RelativeLayout rl_voice_start,rl_voice_start1;
	private TextView tv_voice_start2,tv_voice_date;
	private LinearLayout mDisplayVoiceLayout;
	private ImageView mDisplayVoicePlay;
	private ProgressBar mDisplayVoiceProgressBar;
	private TextView mDisplayVoiceTime;
	private String mRecordPath;// 录音的存储名称
	private float mRecord_Time;// 录音的时间
	private boolean mPlayState; // 播放状态
	private MediaPlayer mMediaPlayer;
	private int mPlayCurrentPosition;
	private String time = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_start);
		rl_voice_start = (RelativeLayout) findViewById(R.id.rl_voice_start);
//		rl_voice_start1 = (RelativeLayout) findViewById(R.id.rl_voice_start1);
//		tv_voice_start2 = (TextView) findViewById(R.id.tv_voice_start2);
//		tv_voice_date = (TextView) findViewById(R.id.tv_voice_date);
		
//		tv_voice_start2.setText("12");//声音的时间计时
//		tv_voice_date.setText("2014-12-18");//日期设置
		rl_voice_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			}
		});
//		rl_voice_start1.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				//开始播放声音
//			}
//		});
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mRecord_Time =  bundle.getString("duration").equals("0")?0:Float.parseFloat(bundle.getString("duration"));
//		mRecord_Time = Float.parseFloat(bundle.getString("duration"));
		mRecordPath = bundle.getString("audioUrl");
		time = bundle.getString("time");
		
		mDisplayVoiceLayout = (LinearLayout) findViewById(R.id.voice_display_voice_layout);
		mDisplayVoicePlay = (ImageView) findViewById(R.id.voice_display_voice_play);
		mDisplayVoiceProgressBar = (ProgressBar) findViewById(R.id.voice_display_voice_progressbar);
		mDisplayVoiceTime = (TextView) findViewById(R.id.voice_display_voice_time);
		
		mDisplayVoiceLayout.setVisibility(View.VISIBLE);
		mDisplayVoicePlay
				.setImageResource(R.drawable.globle_player_btn_play);
		mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
		mDisplayVoiceProgressBar.setProgress(0);
		mDisplayVoiceTime.setText((int) mRecord_Time + "″");
		
		mDisplayVoicePlay.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 播放录音
				if (!mPlayState) {
					mMediaPlayer = new MediaPlayer();
					try {
						// 添加录音的路径
						mMediaPlayer.setDataSource(mRecordPath);
						// 准备
						mMediaPlayer.prepare();
						// 播放
						mMediaPlayer.start();
						// 根据时间修改界面
						new Thread(new Runnable() {

							public void run() {

								mDisplayVoiceProgressBar
										.setMax((int) mRecord_Time);
								mPlayCurrentPosition = 0;
								while (mMediaPlayer.isPlaying()) {
									mPlayCurrentPosition = mMediaPlayer
											.getCurrentPosition() / 1000;
									mDisplayVoiceProgressBar
											.setProgress(mPlayCurrentPosition);
								}
							}
						}).start();
						// 修改播放状态
						mPlayState = true;
						// 修改播放图标
						mDisplayVoicePlay
								.setImageResource(R.drawable.globle_player_btn_stop);

						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									// 播放结束后调用
									public void onCompletion(MediaPlayer mp) {
										// 停止播放
										mMediaPlayer.stop();
										// 修改播放状态
										mPlayState = false;
										// 修改播放图标
										mDisplayVoicePlay
												.setImageResource(R.drawable.globle_player_btn_play);
										// 初始化播放数据
										mPlayCurrentPosition = 0;
										mDisplayVoiceProgressBar
												.setProgress(mPlayCurrentPosition);
									}
								});

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (mMediaPlayer != null) {
						// 根据播放状态修改显示内容
						if (mMediaPlayer.isPlaying()) {
							mPlayState = false;
							mMediaPlayer.stop();
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mPlayCurrentPosition = 0;
							mDisplayVoiceProgressBar
									.setProgress(mPlayCurrentPosition);
						} else {
							mPlayState = false;
							mDisplayVoicePlay
									.setImageResource(R.drawable.globle_player_btn_play);
							mPlayCurrentPosition = 0;
							mDisplayVoiceProgressBar
									.setProgress(mPlayCurrentPosition);
						}
					}
				}
			}
		});
		
	}
	@Override  
	public boolean onTouchEvent(MotionEvent event){   
	    finish();   
	    return true;   
	} 
}
