package com.orfid.youxikuaile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orfid.youxikuaile.model.AtrrestOrder;
import com.orfid.youxikuaile.util.BitmapUtil;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


public class ArrastCorderActivity extends Activity {

	private TextView userNameText, sexText, jobText, errorText, postNameText,
			addressText;
	private ProgressDialog progressDialog;
	private String iconStr, nameStr, sexStr, jobStr, errorStr, postNameStr,
			addressStr;
	private ImageView imgView;
	private LinearLayout mainLayout;
	private String path, imgPath, photoPath;
	private boolean weixinChecked = false, qqChecked = false;
	private Button shareBtn, backBtn, saveBtn;
	private int height;
	private AtrrestOrder ao;
	public static QQAuth mQQAuth;
	private Tencent mTencent;
	private String APP_ID = "1101639686";
	private String APP_KEY_SEC = "j5TYxVnijyFBM8mK";
	private IWXAPI api;
	private boolean isSave = false;
	private Dialog builder;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				getScreenShot();
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arrest_layout);
		ao = getIntent().getParcelableExtra("arrest");
		if (savedInstanceState != null) {

			photoPath = savedInstanceState.getString("filePath");
			if (photoPath != null) {
				if (!photoPath.equals("")) {
					imgPath = photoPath;
				}
			}
			isSave = savedInstanceState.getBoolean("isSave");

		}
		if (ao != null) {
			iconStr = ao.getIcon();
			nameStr = ao.getName();
			sexStr = ao.getSex();
			jobStr = ao.getJob();
			errorStr = ao.getError();
			postNameStr = ao.getPostname();
			addressStr = ao.getAddress();
		}
		api = WXAPIFactory.createWXAPI(getApplicationContext(),
				"wxea88f1583d4ec313", true);
		api.registerApp("wxea88f1583d4ec313");
//		ApplicationData.getApplicatinInstance().addActivity(this);
		initView();

		if (nameStr != null) {
			userNameText.setText(nameStr);
		}
		if (sexStr != null) {
			sexText.setText(sexStr);
		}
		if (jobStr != null) {
			jobText.setText(jobStr);
		}
		if (errorStr != null) {
			errorText.setText(errorStr);
		}
		if (postNameStr != null) {
			postNameText.setText(postNameStr);
		}
		if (addressStr != null) {
			addressText.setText(addressStr);
		}
		if (iconStr != null) {
			Bitmap bm = BitmapFactory.decodeFile(iconStr);
			if (bm != null) {
				imgView.setImageBitmap(bm);
				bm = null;

			}
		}

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrastCorderActivity.this.finish();
			}
		});
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getShareDialog();
			}
		});
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveBtn.setEnabled(false);
				progressDialog = new ProgressDialog(ArrastCorderActivity.this);
				progressDialog.setTitle("请稍等");
				progressDialog.setMessage("图片正在保存...");
				progressDialog.show();
				Message me = new Message();
				me.what = 1;
				handler.sendMessageDelayed(me, 1000);

			}
		});

	}

	private void initView() {
		// TODO Auto-generated method stub
		mainLayout = (LinearLayout) findViewById(R.id.arrast_layout_main);
		backBtn = (Button) findViewById(R.id.arrast_order_back_btn);
		saveBtn = (Button) findViewById(R.id.arrast_order_three_save_btn);

		shareBtn = (Button) findViewById(R.id.arrast_order_three_share_btn);
		imgView = (ImageView) findViewById(R.id.arrast_order_user_icon);
		userNameText = (TextView) findViewById(R.id.arrast_user_name_text);
		sexText = (TextView) findViewById(R.id.arrast_user_sex_text);
		jobText = (TextView) findViewById(R.id.arrast_user_job_text);
		errorText = (TextView) findViewById(R.id.arrast_info_text_text);
		postNameText = (TextView) findViewById(R.id.arrast_user_post_name_text);
		addressText = (TextView) findViewById(R.id.arrast_user_address_text);
	}

	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// TODO:
			path = Environment.getExternalStorageDirectory().getPath()
					+ "/yxkuailePic";
		} else {
			path = this.getCacheDir().getPath() + "/yxkuailePic";
		}
		imgPath = path + "/" + bitName;
		File f = new File(imgPath);
		try {
			if (!f.getParentFile().exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				f.getParentFile().mkdirs();

			}
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Log.i("info", "file:" + f.getAbsolutePath());
			FileOutputStream fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		} finally {

		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
		if (background == null) {
			return null;
		}
		height = 0;
		int w = background.getWidth();
		int h = background.getHeight();
		// 设置想要的大小
		int newWidth = foreground.getWidth();
		int newHeight = foreground.getHeight() + height;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / w;
		float scaleHeight = ((float) newHeight) / h;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		background = Bitmap.createBitmap(background, 0, 0, w, h, matrix, true);
		int bgWidth = foreground.getWidth();
		int bgHeight = foreground.getHeight() + height;
		// int fgWidth = foreground.getWidth();
		// int fgHeight = foreground.getHeight();
		// create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
		Bitmap newbmp = Bitmap
				.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas cv = new Canvas(newbmp);
		// draw bg into
		cv.drawBitmap(background, 0, 0, null);// 在 0，0坐标开始画入bg
		// draw fg into
		cv.drawBitmap(foreground, 0, height, null);// 在 0，0坐标开始画入fg ，可以从任意位置画入
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		background = null;
		foreground = null;
		return newbmp;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ArrastCorderActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ao = null;

	}

	private void getScreenShot() {

		mainLayout.setDrawingCacheEnabled(true);
		mainLayout.buildDrawingCache();
		Bitmap bitmap = mainLayout.getDrawingCache();
		if (bitmap != null) {
			try {
				Bitmap bm = BitmapFactory.decodeResource(this.getResources(),
						R.drawable.arrast_bg);
				bitmap = toConformBitmap(bm, bitmap);
				bm = null;
				saveMyBitmap("arrast.png", bitmap);

				ContentResolver cr = ArrastCorderActivity.this
						.getContentResolver();
				String url = MediaStore.Images.Media.insertImage(cr, bitmap,
						"arrast", "");
				Intent intent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri uri = Uri.fromFile(new File(imgPath));
				intent.setData(uri);
				ArrastCorderActivity.this.sendBroadcast(intent);
				Toast.makeText(ArrastCorderActivity.this, "保存成功!",
						Toast.LENGTH_SHORT).show();
				bitmap.recycle();
				isSave = true;
			} catch (Exception e) {

				e.printStackTrace();

			}

		} else {
			Log.i("info", "bitmap==null");
		}
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		saveBtn.setEnabled(true);
	}

	private void getShareDialog() {
		builder = new Dialog(this);
		builder.show();
		View contentview = LayoutInflater.from(this).inflate(
				R.layout.share_dialog, null);
		builder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		builder.getWindow().setContentView(R.layout.share_dialog);
		final ImageView weixinImg;
		final ImageView qqImg;
		RelativeLayout cancelLayout, positiveLayout, weixinLayout, qqLayout;
		weixinImg = (ImageView) builder.findViewById(R.id.share_weixin_img);
		qqImg = (ImageView) builder.findViewById(R.id.share_qq_img);

		cancelLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_cancel);
		positiveLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_positive);
		weixinLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_weixin_layout);
		qqLayout = (RelativeLayout) builder
				.findViewById(R.id.share_new_qq_layout);
		qqImg.setImageResource(R.drawable.share_toqq_check1);
		weixinLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weixinChecked) {
					weixinChecked = false;
					weixinImg.setImageResource(R.drawable.share_weixin_check1);
				} else {
					weixinChecked = true;
					qqChecked = false;
					qqImg.setImageResource(R.drawable.share_qq_check1);
					weixinImg
							.setImageResource(R.drawable.share_weixin_checked1);
				}
			}
		});
		qqLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (qqChecked) {
					qqChecked = false;
					qqImg.setImageResource(R.drawable.share_toqq_check1);
				} else {
					qqChecked = true;
					weixinChecked = false;
					weixinImg.setImageResource(R.drawable.share_weixin_check1);
					qqImg.setImageResource(R.drawable.share_toqq_checked1);
				}
			}
		});
		cancelLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.dismiss();
			}
		});
		positiveLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isSave) {
					Toast.makeText(ArrastCorderActivity.this,
							getResources().getString(R.string.msg_pic_no_exit),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (weixinChecked) {
					// SharedPreferences userInfo = ArrastCorderActivity.this
					// .getSharedPreferences("user_info", 0);
					// userInfo.edit().putString("wx", "1").commit();
					// WXWebpageObject webpage = new WXWebpageObject();
					// webpage.webpageUrl =
					// "http://www.yxkuaile.com/share/wx_zp.html";

					// WXMediaMessage msg = new WXMediaMessage(webpage);
					// msg.title = getResources().getString(R.string.app_name);
					// msg.description = getResources().getString(
					// R.string.share_title_txt);

					// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120,
					// 120,
					// true);
					// msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp,
					// true);
					if (imgPath == null) {
						return;
					}
					File file = new File(imgPath);
					if (!file.exists()) {
						Toast.makeText(
								ArrastCorderActivity.this,
								getResources().getString(
										R.string.msg_pic_no_exit),
								Toast.LENGTH_LONG).show();
						return;
					}
					WXImageObject imgObj = new WXImageObject();
					imgObj.setImagePath(imgPath);
					WXMediaMessage msg = new WXMediaMessage();
					msg.mediaObject = imgObj;
					msg.title = getResources().getString(R.string.app_name);
					msg.description = getResources().getString(
							R.string.share_title_txt);
					Bitmap bmp = BitmapFactory.decodeResource(getResources(),
							R.drawable.x152);
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120,
							true);
					bmp = null;
					msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = String.valueOf(System.currentTimeMillis());
					req.message = msg;
					req.scene = SendMessageToWX.Req.WXSceneTimeline;
					api.sendReq(req);
					builder.dismiss();
				} else if (qqChecked) {
					if (imgPath == null) {
						return;
					}
					mQQAuth = QQAuth.createInstance(APP_ID,
							getApplicationContext());
					mTencent = Tencent.createInstance(APP_ID,
							ArrastCorderActivity.this);
					final Bundle params = new Bundle();
					// params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
					// QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
					// params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME,
					// getResources().getString(R.string.app_name));// 必填
					// params.putString(QzoneShare.SHARE_TO_QQ_TITLE,
					// getResources().getString(R.string.app_name));// 必填
					// params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,
					// getResources().getString(R.string.share_title_txt));
					// params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
					// "http://www.yxkuaile.com/share/zp.html");// 必填
					// ArrayList<String> pics = new ArrayList<String>();
					// pics.add("http://static.yxkuaile.com/yxxiazi/64.gif");
					// params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
					// pics);
					// mTencent.shareToQzone(ArrastCorderActivity.this, params,
					// new BaseUiListener());

					// params.putString(QQShare.SHARE_TO_QQ_APP_NAME,
					// getResources().getString(R.string.app_name));// 必填
					// params.putString(QQShare.SHARE_TO_QQ_TITLE,
					// getResources()
					// .getString(R.string.app_name));// 必填
					// params.putString(QQShare.SHARE_TO_QQ_SUMMARY,
					// getResources().getString(R.string.share_title_txt));
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,
							imgPath);
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME,
							getResources().getString(R.string.app_name));
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
							QQShare.SHARE_TO_QQ_TYPE_IMAGE);
					params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
							QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
					mTencent.shareToQQ(ArrastCorderActivity.this, params,
							new BaseUiListener());
					builder.dismiss();
				}
			}
		});

	}

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// Util.showResultDialog(IndexActivity.this, response.toString(),
			// "登录成功");
			Log.i("info", "share is success");
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
			// sendShare();

		}

		@Override
		public void onError(UiError e) {
			// Util.toastMessage(IndexActivity.this, "onError: " +
			// e.errorDetail);
			// Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			// Util.toastMessage(IndexActivity.this, "onCancel: ");
			// Util.dismissDialog();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		if (ApplicationData.getInstance() != null) {
//			outState.putParcelable("userData", ApplicationData.getInstance());
//
//		}
		if (imgPath != null) {
			outState.putString("filePath", imgPath);
		} else {
			outState.putString("filePath", "");
		}

		outState.putBoolean("isSave", isSave);

	}

}
