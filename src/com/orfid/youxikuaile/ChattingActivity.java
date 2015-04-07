package com.orfid.youxikuaile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChattingActivity extends Activity implements OnClickListener {

	ImageView icon_title_right;//群聊图标
	Button btn_chatting_voice, btn_voice_keyboard, btn_expression_keyboard,
			btn_expression_more, btn_chatting_more;
	EditText et_chatting_input;
	ListView lv_chatting_history;//聊天内容
	RelativeLayout rl_expression;//“发送表情”的相对布局
//	RelativeLayout rl_chatting_sent;//“发送”的相对布局
	RelativeLayout rl_chatting_voice_big;//“语音”的相对布局
	RelativeLayout rl_chatting_picturek;//“相册”的相对布局
	ViewPager vp_chatting_expression;//表情切换界面选择
	ImageView iv_dot0,iv_dot1;//表情切换的两个点
//	Button btn_chatting_sent;//发送按钮
	ImageButton iv_chatting_voice_big;//大语音图标
	ImageView iv_chatting_picturek;//相册图标
	private ImageView chatting_back;
	
	private Timer mTimer;
	private String messageContent;
	private Bitmap imgAttachment = null;
	private ChatAdapter chatAdapter;
	private List<ChatEntity> chatList;
	private SharedPreferences sp;
	private String token;
	private String uid = "";
	private String sid = "";
	private boolean isGroup = false;
	private boolean isButtonSent = false;
	
	private int mRecord_State = 0; // 录音的状态
	private static final int RECORD_NO = 0; // 不在录音
	private static final int RECORD_ING = 1; // 正在录音
	private static final int RECORD_ED = 2; // 完成录音
	private String mRecordPath;// 录音的存储名称
	private static final String PATH = "/sdcard/internetcommunity/Record/";// 录音存储路径
	private RecordUtil mRecordUtil;
	private float mRecord_Time = 0;// 录音的时间
	private static final int MAX_TIME = 60;// 最长录音时间
	private static final int MIN_TIME = 2;// 最短录音时间
	private double mRecord_Volume;// 麦克风获取的音量值
	private TextView mRecordTime;
	private ProgressBar mRecordProgressBar;
	private int mMAXVolume;// 最大音量高度
	private int mMINVolume;// 最小音量高度
	private TextView status_hint_text;
	private ViewPager mViewPager;
	private LinearLayout mDotsLayout;
	private List<String> staticFacesList;
	private int columns = 6;
	private int rows = 4;
	private List<View> views = new ArrayList<View>();
	private float tmpRecTime;
	private String photoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatting);
		
		Intent intent = getIntent();
		//if (intent != null) {
			Bundle bundle = intent.getExtras();
			uid = bundle.getString("toUid");
			sid = bundle.getString("sid");
			isGroup = bundle.getBoolean("isGroup");
		//}
		chatList = new ArrayList<ChatEntity>(); 
        chatAdapter = new ChatAdapter(this, chatList);
        
        initStaticFaces();
		findId();//寻找ID
		init();
		if (isGroup) {
			icon_title_right.setVisibility(View.VISIBLE);
		}

		et_chatting_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					// change button plus to send
					btn_chatting_more.setBackgroundResource(R.drawable.btn_sent);
					isButtonSent = true;
				} else {
					// change button plus back
					btn_chatting_more.setBackgroundResource(R.drawable.btn_more);
					isButtonSent = false;
				}
			}
		});

		lv_chatting_history.setAdapter(chatAdapter);
		lv_chatting_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				ChatEntity entity = (ChatEntity) chatAdapter.getItem(position);
				if (entity.getRecordUrl() != null) {
					Log.d("is record", "true");
					TextView tv = (TextView) view.findViewById(R.id.voice_duration);
					Log.d("record duration====>", Integer.parseInt(tv.getText().toString().replaceAll("[\\D]",""))+"");
					Intent intent = new Intent(ChattingActivity.this, VoiceStartActivity.class);
					intent.putExtra("audioUrl", entity.getRecordUrl());
					intent.putExtra("duration", Integer.parseInt(tv.getText().toString().replaceAll("[\\D]",""))+"");
					startActivity(intent);
				} else if (entity.getRecordTime() !=null
	 					&& !entity.getRecordTime().equals("")
	 					&& !entity.getRecordTime().equals("0.0")) {
					Log.d("is record", "true");
					Intent intent = new Intent(ChattingActivity.this, VoiceStartActivity.class);
					intent.putExtra("audioUrl", mRecordPath);
					Log.d("record time======>", tmpRecTime+"");
					intent.putExtra("duration", tmpRecTime+"");
					startActivity(intent);
				} else if (entity.getImgAttachmentUrl() != null) {
					Log.d("is img", "true");
					Intent intent = new Intent(ChattingActivity.this, ImageDetailsActivity.class);
					intent.putExtra("image_url", entity.getImgAttachmentUrl());
					startActivity(intent);
				} else if (entity.getImageAttachmentBitmap() != null) {
					Log.d("is img", "true");
					Intent intent = new Intent(ChattingActivity.this, ImageDetailsActivity.class);
					intent.putExtra("image_path", photoPath);
					startActivity(intent);
				} else {
					Log.d("is text", "true");
				}
			}
			
		});
		
		btn_chatting_voice.setOnClickListener(this);
		btn_voice_keyboard.setOnClickListener(this);
		btn_expression_keyboard.setOnClickListener(this);
		btn_expression_more.setOnClickListener(this);
		btn_chatting_more.setOnClickListener(this);
		chatting_back.setOnClickListener(this);

		iv_chatting_picturek.setOnClickListener(this);
		icon_title_right.setOnClickListener(this);
		et_chatting_input.setOnClickListener(this);
		
		iv_chatting_voice_big.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 开始录音
				case MotionEvent.ACTION_DOWN:
					if (mRecord_State != RECORD_ING) {
						status_hint_text.setText("松开 结束");
						// 开始动画效果
//						startRecordLightAnimation();
						// 修改录音状态
						mRecord_State = RECORD_ING;
						// 设置录音保存路径
						mRecordPath = PATH + UUID.randomUUID().toString()
								+ ".amr";
						// 实例化录音工具类
						mRecordUtil = new RecordUtil(mRecordPath);
						try {
							// 开始录音
							mRecordUtil.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
						new Thread(new Runnable() {

							public void run() {
								// 初始化录音时间
								mRecord_Time = 0;
								while (mRecord_State == RECORD_ING) {
									// 大于最大录音时间则停止录音
									if (mRecord_Time >= MAX_TIME) {
										mRecordHandler.sendEmptyMessage(0);
									} else {
										try {
											// 每隔200毫秒就获取声音音量并更新界面显示
											Thread.sleep(200);
											mRecord_Time += 0.2;
											if (mRecord_State == RECORD_ING) {
												mRecord_Volume = mRecordUtil
														.getAmplitude();
												mRecordHandler
														.sendEmptyMessage(1);
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}).start();
					}
					break;
				// 停止录音
				case MotionEvent.ACTION_UP:

					if (mRecord_State == RECORD_ING) {
						// 停止动画效果
//						stopRecordLightAnimation();
						
						// 修改录音状态
						mRecord_State = RECORD_ED;
						try {
							// 停止录音
							mRecordUtil.stop();
							// 初始录音音量
							mRecord_Volume = 0;
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 如果录音时间小于最短时间
						if (mRecord_Time <= MIN_TIME) {
							// 显示提醒
							Toast.makeText(ChattingActivity.this, "录音时间过短",
									Toast.LENGTH_SHORT).show();
							// 修改录音状态
							mRecord_State = RECORD_NO;
							// 修改录音时间
							mRecord_Time = 0;
							// 修改显示界面
							mRecordTime.setText("0″");
							mRecordProgressBar.setProgress(0);
							status_hint_text.setText("按住 说话");
							// 修改录音声音界面
//							ViewGroup.LayoutParams params = mRecordVolume
//									.getLayoutParams();
//							params.height = 0;
//							mRecordVolume.setLayoutParams(params);
						} else {
							// 录音成功,则显示录音成功后的界面
//							mRecordLayout.setVisibility(View.GONE);
//							mRecord.setVisibility(View.GONE);
//							mDisplayVoiceLayout.setVisibility(View.VISIBLE);
//							mDisplayVoicePlay
//									.setImageResource(R.drawable.globle_player_btn_play);
//							mDisplayVoiceProgressBar.setMax((int) mRecord_Time);
//							mDisplayVoiceProgressBar.setProgress(0);
//							mDisplayVoiceTime.setText((int) mRecord_Time + "″");
//							send.setVisibility(View.VISIBLE);
							Log.d("record path======>", mRecordPath);
							Log.d("record time======>", mRecord_Time+"");
//							Intent intent = new Intent();
//							intent.putExtra("recordPath", mRecordPath);
//							intent.putExtra("recordTime", mRecord_Time);
//							setResult(RESULT_OK, intent);
//							finish();
							send();
							
							mRecord_State = RECORD_ED;
							tmpRecTime = mRecord_Time;
							mRecord_Time = 0;
							mRecordTime.setText("0″");
							mRecordProgressBar.setProgress(0);
							status_hint_text.setText("按住 说话");
							
//							Log.d("uid=======>", uid);
							new UploadRecordTask(ChattingActivity.this, mRecordPath).execute(
									Constants.BASE_URL + "message/send");
						}
					}
					break;
				}
				return false;
			}
		});
		
		
		
		mViewPager.setOnPageChangeListener(new PageChange());
		
		mTimer = new Timer();  
        // start timer task  
		new LoadMessageListTask(1).execute();
	}

	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        // cancel timer  
        mTimer.cancel();  
    } 
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chatting_back:
			finish();
			break;
		case R.id.btn_chatting_voice://语音
			/*隐藏输入法*/
			InputMethodManager inputMethodManager =(InputMethodManager)ChattingActivity.this.getApplicationContext().
					getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputMethodManager.hideSoftInputFromWindow(et_chatting_input.getWindowToken(), 0);
			btn_chatting_voice.setVisibility(View.GONE);//语音按钮(隐藏)
			btn_expression_keyboard.setVisibility(View.GONE);//表情键盘按钮(隐藏)
			btn_voice_keyboard.setVisibility(View.VISIBLE);//语音键盘按钮(显示)
			btn_expression_more.setVisibility(View.VISIBLE);//表情按钮(显示)
			btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
			rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//			rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
			rl_chatting_voice_big.setVisibility(View.VISIBLE);//“语音”布局(显示)
			rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
			break;
		case R.id.btn_expression_keyboard://点击表情之后的“键盘”按钮
			et_chatting_input.requestFocus();//让输入框被选中
//			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)) .showInputMethodPicker();//选择输入法
			/*private void showInputMethodPicker() { 
			 * ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)) 
			 * .showInputMethodPicker(); }
			 * */
			InputMethodManager inputMethodManager4 =(InputMethodManager)ChattingActivity.this.getApplicationContext().
					getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputMethodManager4.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			btn_chatting_voice.setVisibility(View.VISIBLE);//语音按钮(显示)
			btn_expression_keyboard.setVisibility(View.GONE);//表情键盘按钮(隐藏)
			btn_voice_keyboard.setVisibility(View.GONE);//语音键盘按钮(隐藏)
			btn_expression_more.setVisibility(View.VISIBLE);//表情按钮(显示)
			btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
			rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//			rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
			rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
			rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
			break;
		case R.id.btn_chatting_more://添加
			
			if (isButtonSent) {
				if (!et_chatting_input.getText().toString().trim().equals("")) {
					//发送消息
					messageContent = et_chatting_input.getText().toString().trim();
					Log.d("message=======>", messageContent);
					send();
					new SendMessageTask().execute();

				} else {
					Toast.makeText(ChattingActivity.this, "请先输入内容", Toast.LENGTH_SHORT).show();
				}
			} else {
				// button plus action
				Log.d("click to expland plus actions====>", "foo");
			
				if (!rl_chatting_picturek.isShown()) {
					/*隐藏输入法*/
					InputMethodManager inputMethodManager1 =(InputMethodManager)ChattingActivity.this.getApplicationContext().
							getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager1.hideSoftInputFromWindow(et_chatting_input.getWindowToken(), 0);
					btn_chatting_voice.setVisibility(View.GONE);//语音按钮(隐藏)
					btn_expression_keyboard.setVisibility(View.GONE);//表情键盘按钮(隐藏)
					btn_voice_keyboard.setVisibility(View.VISIBLE);//语音键盘按钮(显示)
					btn_expression_more.setVisibility(View.VISIBLE);//表情按钮(显示)
					btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
					rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//					rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
					rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
					rl_chatting_picturek.setVisibility(View.VISIBLE);//“相册”布局(显示)
				} else {
					et_chatting_input.requestFocus();//让输入框被选中
					
					InputMethodManager inputMethodManager5 =(InputMethodManager)ChattingActivity.this.getApplicationContext().
							getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager5.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
					btn_chatting_voice.setVisibility(View.VISIBLE);//语音按钮(显示)
					btn_expression_keyboard.setVisibility(View.GONE);//表情键盘按钮(隐藏)
					btn_voice_keyboard.setVisibility(View.GONE);//语音键盘按钮(隐藏)
					btn_expression_more.setVisibility(View.VISIBLE);//表情按钮(显示)
					btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
					rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//					rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
					rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
					rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
				}
			}
			break;
		case R.id.btn_expression_more://表情
			/*隐藏输入法*/
			InputMethodManager inputMethodManager2 =(InputMethodManager)ChattingActivity.this.getApplicationContext().
					getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputMethodManager2.hideSoftInputFromWindow(et_chatting_input.getWindowToken(), 0);
			btn_chatting_voice.setVisibility(View.VISIBLE);//语音按钮(显示)
			btn_expression_keyboard.setVisibility(View.VISIBLE);//表情键盘按钮(显示)
			btn_voice_keyboard.setVisibility(View.GONE);//语音键盘按钮(隐藏)
			btn_expression_more.setVisibility(View.GONE);//表情按钮(隐藏)
			btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
			rl_expression.setVisibility(View.VISIBLE);//“表情”布局(显示)
//			rl_chatting_sent.setVisibility(View.VISIBLE);//“发送”布局(显示)
			rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
			rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
			break;
		case R.id.btn_voice_keyboard://点击语音之后的“键盘”按钮
			et_chatting_input.requestFocus();//让输入框被选中
			
			
			InputMethodManager inputMethodManager3 =(InputMethodManager)ChattingActivity.this.getApplicationContext().
					getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputMethodManager3.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			btn_chatting_voice.setVisibility(View.VISIBLE);//语音按钮(显示)
			btn_expression_keyboard.setVisibility(View.GONE);//表情键盘按钮(隐藏)
			btn_voice_keyboard.setVisibility(View.GONE);//语音键盘按钮(隐藏)
			btn_expression_more.setVisibility(View.VISIBLE);//表情按钮(显示)
			btn_chatting_more.setVisibility(View.VISIBLE);//添加按钮(显示)
			rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//			rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
			rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
			rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
			
			
//			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)) .showInputMethodPicker();//选择输入法 
			break;
		case R.id.et_chatting_input://输入框
			rl_expression.setVisibility(View.GONE);//“表情”布局(隐藏)
//			rl_chatting_sent.setVisibility(View.GONE);//“发送”布局(隐藏)
			rl_chatting_voice_big.setVisibility(View.GONE);//“语音”布局(隐藏)
			rl_chatting_picturek.setVisibility(View.GONE);//“相册”布局(隐藏)
			break;
		case R.id.icon_title_right://群聊图标
//			startActivity(new Intent(ChattingActivity.this,ChattingMessageActivity.class));
			break;
		case R.id.iv_chatting_picturek://
			startActivityForResult(new Intent(ChattingActivity.this,SelectPicActivity.class), 0);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				photoPath = data.getExtras().getString("photoPath");
				Log.d("photoPath========>>", photoPath+"");
				
				imgAttachment = BitmapFactory.decodeFile(photoPath);
				send();
				imgAttachment = null;
				
//				new UploadRecordTask(ChattingActivity.this, photoPath).execute(
//						AppConstants.SEND_MESSAGE);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private  class ChatAdapter extends BaseAdapter{
		MediaPlayer mp;
		ChatHolder chatHolder = null;
     	private Context context = null;
     	private List<ChatEntity> chatList = null;
     	private LayoutInflater inflater = null;
     	private int COME_MSG = 0;
     	private int TO_MSG = 1;
//     	private int NOTIFY_MSG = 2;
     	
// 		private DisplayImageOptions options;
// 		private ImageLoader imageLoader;
     	
     	public ChatAdapter(Context context,List<ChatEntity> chatList){
     		this.context = context;
     		this.chatList = chatList;
     		inflater = LayoutInflater.from(this.context);
//     		options = new DisplayImageOptions.Builder()
// 				.showStubImage(R.drawable.no_portrait_male)
// 				.showImageForEmptyUri(R.drawable.no_portrait_male).cacheInMemory()
// 				.cacheOnDisc().build();
//     		imageLoader = ImageLoader.getInstance();
     	}

 		@Override
 		public int getCount() {
 			return chatList.size();
 		}

 		@Override
 		public Object getItem(int position) {
 			return chatList.get(position);
 		}

 		@Override
 		public long getItemId(int position) {
 			return position;
 		}
 		
 		@Override
 		public int getItemViewType(int position) {
 			// 区别两种view的类型，标注两个不同的变量来分别表示各自的类型
 		 	ChatEntity entity = chatList.get(position);
 		 	if (entity.isComeMsg())
 		 	{
 		 		return COME_MSG;
// 		 	}else if (entity.isNofityMsg()){
// 		 		return NOTIFY_MSG;
 		 	} else {
 		 		return TO_MSG;
 		 	}
 		}

 		@Override
 		public int getViewTypeCount() {
 			// 这个方法默认返回1，如果希望listview的item都是一样的就返回1，我们这里有两种风格，返回2
 			return 2;
 		}

 		@Override
 		public View getView(int position, View convertView, ViewGroup parent) {
 			
 			if (convertView == null) {
 				chatHolder = new ChatHolder();
 				if (chatList.get(position).isComeMsg()) {
 					convertView = inflater.inflate(R.layout.chat_ta_item, null);
// 				}else if (chatList.get(position).isNofityMsg()) {
// 					convertView = inflater.inflate(R.layout.chat_notify_item, null);
 				} else {
 					convertView = inflater.inflate(R.layout.chat_me_item, null);
 				}
 				
// 				if (!chatList.get(position).isNofityMsg()) {
//	 				chatHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
	 				chatHolder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);
	 				chatHolder.voice_ll = convertView.findViewById(R.id.voice_ll);
	 				chatHolder.voice_duration = (TextView) convertView.findViewById(R.id.voice_duration);
	 				chatHolder.userImageView = (ImageView) convertView.findViewById(R.id.iv_user_image);
	 				chatHolder.imgAttachment_ll = convertView.findViewById(R.id.imgAttachment_ll);
	 				chatHolder.imgAttachment = (ImageView) convertView.findViewById(R.id.imgAttachment);
// 				} else {
// 					chatHolder.notifyTextView = (TextView) convertView.findViewById(R.id.tv_notify);
// 				}
 				convertView.setTag(chatHolder);
 			}else {
 				chatHolder = (ChatHolder)convertView.getTag();
 			}
 			
 			ImageLoader.getInstance().displayImage(Constants.BASE_URL + chatList.get(position).getUserImage(), chatHolder.userImageView);
// 			if (!chatList.get(position).isNofityMsg()) {
//	 			chatHolder.timeTextView.setText(chatList.get(position).getChatTime());
	 			Log.d("recordTime============>", chatList.get(position).getRecordTime());
//	 			Log.d("imgAttachm============>", chatList.get(position).getImgAttachment().toString());
	 			if (chatList.get(position).getRecordTime() ==null
	 					|| chatList.get(position).getRecordTime().equals("")
	 					|| chatList.get(position).getRecordTime().equals("0.0")) {
	 				if (chatList.get(position).getImageAttachmentBitmap() != null) {
		 				chatHolder.contentTextView.setVisibility(View.GONE);
		 				chatHolder.voice_ll.setVisibility(View.GONE);
		 				chatHolder.imgAttachment_ll.setVisibility(View.VISIBLE);
		 				chatHolder.imgAttachment.setImageBitmap(chatList.get(position).getImageAttachmentBitmap());
	 				} else if (chatList.get(position).getImgAttachmentUrl() != null &&
	 						!chatList.get(position).getImgAttachmentUrl().equals("")) {
	 					Log.d("good==========>", "very good");
	 					Log.d("imgUrl==========>", chatList.get(position).getImgAttachmentUrl());
	 					chatHolder.contentTextView.setVisibility(View.GONE);
		 				chatHolder.voice_ll.setVisibility(View.GONE);
		 				chatHolder.imgAttachment_ll.setVisibility(View.VISIBLE);
	 					ImageLoader.getInstance().displayImage(chatList.get(position).getImgAttachmentUrl(), chatHolder.imgAttachment);
	 				} else if (chatList.get(position).getRecordUrl() != null) {
	 					chatHolder.contentTextView.setVisibility(View.GONE);
		 				chatHolder.voice_ll.setVisibility(View.VISIBLE);
		 				chatHolder.imgAttachment_ll.setVisibility(View.GONE);
		 				
		 				mp = Utils.createNetAudio(chatList.get(position).getRecordUrl());
						mp.prepareAsync();
						mp.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								chatHolder.voice_duration.setText((mp.getDuration()/1000)+"``");
								
							}
						});
						
	 				} else {
	 					Log.d("here i am ======>", "entered");
		 				chatHolder.contentTextView.setVisibility(View.VISIBLE);
		 				chatHolder.voice_ll.setVisibility(View.GONE);
		 				chatHolder.imgAttachment_ll.setVisibility(View.GONE);
		 				SpannableStringBuilder sb = handler(chatHolder.contentTextView,
		 						chatList.get(position).getContent());
		 				chatHolder.contentTextView.setText(sb);
	 				}
	 			} else {
	 				chatHolder.contentTextView.setVisibility(View.GONE);
	 				chatHolder.imgAttachment_ll.setVisibility(View.GONE);
	 				chatHolder.voice_ll.setVisibility(View.VISIBLE);
	 				DecimalFormat decimalFormat=new DecimalFormat(".0");
	 				String time = decimalFormat.format(Float.parseFloat(chatList.get(position).getRecordTime()));
	 				chatHolder.voice_duration.setText(time);
	 			}
//	 			imageLoader.init(ImageLoaderConfiguration
//	 					.createDefault(context));
//	 			imageLoader.displayImage(chatList.get(position).getUserImage(), chatHolder.userImageView,
// 					options);
// 			} else {
// 				chatHolder.notifyTextView.setText(chatList.get(position).getContent());
// 			}
 			
 			return convertView;
 		}
 		
 		
 		
 		private class ChatHolder{
// 			private TextView timeTextView;
 			private ImageView userImageView;
 			private TextView contentTextView;
 			private View voice_ll;
 			private TextView voice_duration;
 			private View imgAttachment_ll;
 			private ImageView imgAttachment;
// 			private TextView notifyTextView;
 		}
     	
     }
	
	private SpannableStringBuilder handler(final TextView gifTextView,String content) {
		SpannableStringBuilder sb = new SpannableStringBuilder(content);
		String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			String tempText = m.group();
			try {
				String num = tempText.substring("#[face/png/f_static_".length(), tempText.length()- ".png]#".length());
				String gif = "face/gif/f" + num + ".gif";
				/**
				 * 如果open这里不抛异常说明存在gif，则显示对应的gif
				 * 否则说明gif找不到，则显示png
				 * */
				InputStream is = getAssets().open(gif);
				sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is,new AnimatedGifDrawable.UpdateListener() {
							@Override
							public void update() {
								gifTextView.postInvalidate();
							}
						})), m.start(), m.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				is.close();
			} catch (Exception e) {
				String png = tempText.substring("#[".length(),tempText.length() - "]#".length());
				try {
					sb.setSpan(new ImageSpan(this, BitmapFactory.decodeStream(getAssets().open(png))), m.start(), m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return sb;
	}
	
	/**
	 * 初始化表情列表staticFacesList
	 */
	private void initStaticFaces() {
		try {
			staticFacesList = new ArrayList<String>();
			String[] faces = getAssets().list("face/png");
			//将Assets中的表情名称转为字符串一一添加进staticFacesList
			for (int i = 0; i < faces.length; i++) {
				staticFacesList.add(faces[i]);
			}
			//去掉删除图片
			staticFacesList.remove("emotion_del_normal.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void findId() {
		icon_title_right = (ImageView) findViewById(R.id.icon_title_right);
		et_chatting_input = (EditText) findViewById(R.id.et_chatting_input);
		iv_chatting_voice_big = (ImageButton) findViewById(R.id.iv_chatting_voice_big);
		iv_chatting_picturek = (ImageView) findViewById(R.id.iv_chatting_picturek);
		btn_chatting_voice = (Button) findViewById(R.id.btn_chatting_voice);
		btn_voice_keyboard = (Button) findViewById(R.id.btn_voice_keyboard);
//		btn_chatting_sent = (Button) findViewById(R.id.btn_chatting_sent);
		btn_expression_keyboard = (Button) findViewById(R.id.btn_expression_keyboard);
		btn_expression_more = (Button) findViewById(R.id.btn_expression_more);
		btn_chatting_more = (Button) findViewById(R.id.btn_chatting_more);
		lv_chatting_history = (ListView) findViewById(R.id.lv_chatting_history);
		rl_expression = (RelativeLayout) findViewById(R.id.rl_expression);
//		rl_chatting_sent = (RelativeLayout) findViewById(R.id.rl_chatting_sent);
		rl_chatting_voice_big = (RelativeLayout) findViewById(R.id.rl_chatting_voice_big);
		rl_chatting_picturek = (RelativeLayout) findViewById(R.id.rl_chatting_picturek);
		status_hint_text = (TextView) findViewById(R.id.status_hint_text);
		mRecordTime = (TextView) findViewById(R.id.voice_record_time);
		mRecordProgressBar = (ProgressBar) findViewById(R.id.voice_record_progressbar);
		mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
		chatting_back = (ImageView) findViewById(R.id.chatting_back);
	}
	
	private void init() {
		
		InitViewPager();
		
		mMINVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4.5f, getResources()
						.getDisplayMetrics());
		mMAXVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 65f, getResources()
						.getDisplayMetrics());
	}
	
	/*
	 * 初始表情 *
	 */
	private void InitViewPager() {
		// 获取页数
		for (int i = 0; i < getPagerCount(); i++) {
			views.add(viewPagerItem(i));
			LayoutParams params = new LayoutParams(16, 16);
			mDotsLayout.addView(dotsItem(i), params);
		}
		FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
		mViewPager.setAdapter(mVpAdapter);
		mDotsLayout.getChildAt(0).setSelected(true);
	}
	
	/**
	 * 根据表情数量以及GridView设置的行数和列数计算Pager数量
	 * @return
	 */
	private int getPagerCount() {
		int count = staticFacesList.size();
		return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
				: count / (columns * rows - 1) + 1;
	}
	
	private View viewPagerItem(int position) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
		GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
		/**
		 * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
		 * */
		List<String> subList = new ArrayList<String>();
		subList.addAll(staticFacesList
				.subList(position * (columns * rows - 1),
						(columns * rows - 1) * (position + 1) > staticFacesList
								.size() ? staticFacesList.size() : (columns
								* rows - 1)
								* (position + 1)));
		/**
		 * 末尾添加删除图标
		 * */
		subList.add("emotion_del_normal.png");
		FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
		gridview.setAdapter(mGvAdapter);
		gridview.setNumColumns(columns);
		// 单击表情执行的操作
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try {
					String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
					if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
						insert(getFace(png));
					} else {
						delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		return gridview;
	}
	
	private SpannableStringBuilder getFace(String png) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		try {
			/**
			 * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
			 * 所以这里对这个tempText值做特殊处理
			 * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
			 * */
			String tempText = "#[" + png + "]#";
			sb.append(tempText);
			sb.setSpan(
					new ImageSpan(ChattingActivity.this, BitmapFactory
							.decodeStream(getAssets().open(png))), sb.length()
							- tempText.length(), sb.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb;
	}
	
	/**
	 * 向输入框里添加表情
	 * */
	private void insert(CharSequence text) {
		int iCursorStart = Selection.getSelectionStart((et_chatting_input.getText()));
		int iCursorEnd = Selection.getSelectionEnd((et_chatting_input.getText()));
		if (iCursorStart != iCursorEnd) {
			((Editable) et_chatting_input.getText()).replace(iCursorStart, iCursorEnd, "");
		}
		int iCursor = Selection.getSelectionEnd((et_chatting_input.getText()));
		((Editable) et_chatting_input.getText()).insert(iCursor, text);
	}

	/**
	 * 删除图标执行事件
	 * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
	 * */
	private void delete() {
		if (et_chatting_input.getText().length() != 0) {
			int iCursorEnd = Selection.getSelectionEnd(et_chatting_input.getText());
			int iCursorStart = Selection.getSelectionStart(et_chatting_input.getText());
			if (iCursorEnd > 0) {
				if (iCursorEnd == iCursorStart) {
					if (isDeletePng(iCursorEnd)) {
						String st = "#[face/png/f_static_000.png]#";
						((Editable) et_chatting_input.getText()).delete(
								iCursorEnd - st.length(), iCursorEnd);
					} else {
						((Editable) et_chatting_input.getText()).delete(iCursorEnd - 1,
								iCursorEnd);
					}
				} else {
					((Editable) et_chatting_input.getText()).delete(iCursorStart,
							iCursorEnd);
				}
			}
		}
	}
	
	/**
	 * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
	 * **/
	private boolean isDeletePng(int cursor) {
		String st = "#[face/png/f_static_000.png]#";
		String content = et_chatting_input.getText().toString().substring(0, cursor);
		if (content.length() >= st.length()) {
			String checkStr = content.substring(content.length() - st.length(),
					content.length());
			String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(checkStr);
			return m.matches();
		}
		return false;
	}
	
	/**
	 * 用来控制录音
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					// 停止动画效果
//					stopRecordLightAnimation();
					// 修改录音状态
					mRecord_State = RECORD_ED;
					try {
						// 停止录音
						mRecordUtil.stop();
						// 初始化录音音量
						mRecord_Volume = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;

			case 1:
				// 根据录音时间显示进度条
				mRecordProgressBar.setProgress((int) mRecord_Time);
				// 显示录音时间
				mRecordTime.setText((int) mRecord_Time + "″");
				break;
			}
		}

	};
	
	public class UploadRecordTask extends AsyncTask<String, Void, String> {

		String recordPath;
		
		public UploadRecordTask(Context context, String recordPath) {
			this.recordPath = recordPath;
		}

		@Override
		protected String doInBackground(String... params) {
			File file=new File(recordPath);
			return UploadUtils.sendMessageVoice(ChattingActivity.this, uid, sid, file, params[0]);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if(result.length() > 0){
//				Log.d("result========>", result);
	        	Toast.makeText(ChattingActivity.this, "上传成功",Toast.LENGTH_LONG ).show();

	        	Log.d("upload record success====>", result);

	        }else{
	        	Toast.makeText(ChattingActivity.this, "上传失败，请检查网络", Toast.LENGTH_LONG ).show();
	        }
		}

	}
	
	// 发布消息
 	public void send() {
 		Log.d("mRecord_Time========>", mRecord_Time+"");
     	ChatEntity chatEntity = new ChatEntity();
//	     	chatEntity.setChatTime(Utils.getLocalTime());
     	chatEntity.setContent(messageContent);
     	chatEntity.setUserImage(sp.getString("photo", ""));
     	chatEntity.setComeMsg(false);
     	chatEntity.setRecordTime(mRecord_Time+"");
     	chatEntity.setImageAttachmentBitmap(imgAttachment);
     	chatList.add(chatEntity);
     	chatAdapter.notifyDataSetChanged();
     	lv_chatting_history.setSelection(chatList.size() - 1);
     	et_chatting_input.setText("");
     }
 	
 	/**
	 * 表情页改变时，dots效果也要跟着改变
	 * */
	class PageChange implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
				mDotsLayout.getChildAt(i).setSelected(false);
			}
			mDotsLayout.getChildAt(arg0).setSelected(true);
		}

	}
	
	private ImageView dotsItem(int position) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dot_image, null);
		ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
		iv.setId(position);
		return iv;
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			URL url=null;
			String result = "";
			try {
				url = new URL(Constants.BASE_URL + "message/send");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setRequestMethod("POST");
				conn.setDoOutput(true);

				Writer writer = new OutputStreamWriter(conn.getOutputStream());

				String str = "token=" + token + "&toUid=" + uid + "&sid=" + sid + "&text=" + messageContent
						+ "&file=" + null;
				Log.d("str-=========>", str);
				writer.write(str);
				writer.flush();

				Reader is = new InputStreamReader(conn.getInputStream());

				StringBuilder sb = new StringBuilder();
				char c[] = new char[1024];
				int len=0;

				while ((len = is.read(c)) != -1) {
					sb.append(c, 0, len);
				}
				result = sb.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("TEST", "发送消息JSON---" + result);
			JSONObject obj;
			try {
				obj = new JSONObject(result);
				if (1==obj.getInt("status")) {
//					Toast.makeText(ChattingActivity.this,obj.getString("text"),Toast.LENGTH_SHORT).show();
				}else if(0==obj.getInt("status")){
//					Toast.makeText(ChattingActivity.this,obj.getString("text"),Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/** 
     * do some action 
     */  
    private Handler doActionHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            int msgId = msg.what;  
            switch (msgId) {  
                case 1:  
                    // do some action  
//                	Log.d("test========>", "ddd");
                	new LoadMessageListTask(0).execute();
                    break;  
                default:  
                    break;  
            }  
        }  
    }; 
    
    private class LoadMessageListTask extends AsyncTask<String, Void, String> {

		private int init = 0;
		
		public LoadMessageListTask(int init) {
			this.init = init;
		}
		
		@Override
		protected String doInBackground(String... params) {
			URL url=null;
			String result = "";
			try {
				url = new URL(Constants.BASE_URL + "message/list");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setRequestMethod("POST");
				conn.setDoOutput(true);

				Writer writer = new OutputStreamWriter(conn.getOutputStream());

				String str = "token=" + token + "&toUid=" + uid + "&sid=" + sid + "&init=" + init;
				Log.d("str-=========>", str);
				writer.write(str);
				writer.flush();

				Reader is = new InputStreamReader(conn.getInputStream());

				StringBuilder sb = new StringBuilder();
				char c[] = new char[1024];
				int len=0;

				while ((len = is.read(c)) != -1) {
					sb.append(c, 0, len);
				}
				result = sb.toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("TEST", "消息列表JSONddd---" + result);
			JSONObject obj;
			try {
				obj = new JSONObject(result);
				if (1==obj.getInt("status")) {
//					Toast.makeText(ChattingActivity.this,obj.getString("text"),Toast.LENGTH_SHORT).show();
//					Log.d("init==============>", "");
					if (init == 1) {
//						setTimerTask();
						JSONObject jObj = new JSONObject(obj.getString("data"));
						if (jObj != null && jObj.length() > 0) {
							JSONArray jArr = new JSONArray(jObj.getString("messages"));
							if (jArr.length() > 0) {
								for (int i=0; i<jArr.length(); i++) {
									String attachImgUrl = null, attachRcdUrl = null;
									JSONObject jsonObj = (JSONObject) jArr.get(i); 
									JSONObject jUser = jsonObj.getJSONObject("user");
									if (!jsonObj.getJSONArray("files").equals("[]")) {
										JSONArray jFiles = jsonObj.getJSONArray("files");
										if (jFiles.length() > 0) {
											JSONObject jFile = (JSONObject) jFiles.get(0);
											if (jFile.getString("type").equals("image")) {
												attachImgUrl = Constants.BASE_URL + jFile.getString("url");
											} else {
												attachRcdUrl = Constants.BASE_URL + jFile.getString("url");
											}
										}
									}
									
									String currentUserId = sp.getString("uid", "");
									boolean isComeMsg = false;
									if (!jUser.getString("uid").equals(currentUserId)) {
										isComeMsg = true;
									}
									ChatEntity chatEntity = new ChatEntity();
							      	chatEntity.setChatTime(jsonObj.getString("sendtime"));
							      	chatEntity.setContent(jsonObj.getString("text"));
							      	chatEntity.setUserImage(jUser.getString("photo"));
							      	chatEntity.setComeMsg(isComeMsg);
							      	chatEntity.setRecordTime("");
							      	chatEntity.setImageAttachmentBitmap(null);
							      	chatEntity.setImgAttachmentUrl(attachImgUrl);
							      	chatEntity.setRecordUrl(attachRcdUrl);
							      	chatList.add(chatEntity);
								}
								
								Collections.reverse(chatList);
								lv_chatting_history.setSelection(chatList.size() - 1);
								chatAdapter.notifyDataSetChanged();
							}
							
							setTimerTask();
						}
					} else {
						JSONObject jObj = new JSONObject(obj.getString("data"));
						if (jObj != null && jObj.length() > 0) {
							JSONArray jArr = new JSONArray(jObj.getString("messages"));
							
							for (int i=0; i<jArr.length(); i++) {
								String attachImg = null, attachRcdUrl = null;
								JSONObject jsonObj = (JSONObject) jArr.get(i); 
								JSONArray jFiles = jsonObj.getJSONArray("files");
								if (jFiles.length() > 0) {
									JSONObject jFile = (JSONObject) jFiles.get(i);
									if (jFile.getString("type").equals("image")) {
										attachImg = Constants.BASE_URL + jFile.getString("url");
									} else {
										attachRcdUrl = jFile.getString("url");
									}
								}
								JSONObject jUser = new JSONObject(jsonObj.getString("user"));
								if (!jUser.getString("uid").equals(sp.getString("uid", ""))) {
									receive(jsonObj.getString("text"), "", attachRcdUrl, attachImg);
								}
							}
							
						}
					}
				}else if(0==obj.getInt("status")){
					Toast.makeText(ChattingActivity.this,obj.getString("text"),Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void setTimerTask() {  
		try {
	        mTimer.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                Message message = new Message();  
	                message.what = 1;  
	                doActionHandler.sendMessage(message);  
	            }  
	        }, 1000, 10000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);  
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
	
	// 接收消息
	  	public void receive(String message, String portrait, String recordTime) {
	  		ChatEntity chatEntity = new ChatEntity();
//	      	chatEntity.setChatTime(Utils.getLocalTime());
	      	chatEntity.setContent(message);
//	      	chatEntity.setUserImage(portrait);
	      	chatEntity.setComeMsg(true);
	      	chatEntity.setRecordTime(recordTime);
	      	chatList.add(chatEntity);
	      	chatAdapter.notifyDataSetChanged();
	      	lv_chatting_history.setSelection(chatList.size() - 1);
	  	}
	  	
	  	public void receive(String message, String portrait, String recordUrl, String attachImage) {
	  		ChatEntity chatEntity = new ChatEntity();
//	      	chatEntity.setChatTime(Utils.getLocalTime());
	      	chatEntity.setContent(message);
//	      	chatEntity.setUserImage(portrait);
	      	chatEntity.setComeMsg(true);
	      	chatEntity.setRecordTime("");
	      	chatEntity.setRecordUrl(recordUrl);
	      	chatEntity.setImgAttachmentUrl(attachImage);
	      	chatList.add(chatEntity);
	      	chatAdapter.notifyDataSetChanged();
	      	lv_chatting_history.setSelection(chatList.size() - 1);
	  	}

}
