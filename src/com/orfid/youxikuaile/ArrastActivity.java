package com.orfid.youxikuaile;

import java.io.File;
import java.io.IOException;

import com.orfid.youxikuaile.model.AtrrestOrder;
import com.orfid.youxikuaile.model.User;
import com.orfid.youxikuaile.util.CompressImage;
import com.orfid.youxikuaile.util.ImageTools;

import android.app.Activity;
import android.app.Dialog;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ArrastActivity extends Activity {
	private Button suerBtn, backBtn;
	private EditText nameEdit, sexEdit, jobEdit, errorEdit, postNameEdit,
			addreEdit;

	private ImageView iconImg;
	private File imgFile;
	private Dialog builderPic;
	private String nameStr, sexStr, jobStr, errorStr, postNameStr, addressStr;

	private TextView cameraBtn, picBtn;
	private String photoPath = "";
	private User userData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arrest_layout_edit);
		if (savedInstanceState != null) {
//			userData = savedInstanceState.getParcelable("userData");
//			if (userData != null) {
//				ApplicationData.setInstance(userData);
//			}
			photoPath = savedInstanceState.getString("filePath");
			nameStr = savedInstanceState.getString("nameStr");
			sexStr = savedInstanceState.getString("sexStr");
			jobStr = savedInstanceState.getString("jobStr");
			errorStr = savedInstanceState.getString("errorStr");
			postNameStr = savedInstanceState.getString("postNameStr");
			addressStr = savedInstanceState.getString("addressStr");

		}
//		ApplicationData.getApplicatinInstance().addActivity(this);
		initView();
		if (nameStr != null) {
			nameEdit.setText(nameStr);
		}
		if (sexStr != null) {
			sexEdit.setText(sexStr);
		}
		if (jobStr != null) {
			jobEdit.setText(jobStr);
		}
		if (errorStr != null) {
			errorEdit.setText(errorStr);
		}
		if (postNameStr != null) {
			postNameEdit.setText(postNameStr);
		}
		if (addressStr != null) {
			addreEdit.setText(addressStr);
		}
		addListener();

	}

	private void addListener() {
		// TODO Auto-generated method stub
		suerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String nameStr = "" + nameEdit.getText().toString();
				String sexStr = "" + sexEdit.getText().toString();
				String jobStr = "" + jobEdit.getText().toString();
				String errorStr = "" + errorEdit.getText().toString();
				String postNameStr = "" + postNameEdit.getText().toString();
				String addreStr = "" + addreEdit.getText().toString();
				if (nameStr == null || nameStr.equals("")) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(R.string.arrast_name_null),
							Toast.LENGTH_SHORT).show();

					return;
				}
				if (sexStr == null || sexStr.equals("")) {

					Toast.makeText(getBaseContext(),
							getResources().getString(R.string.arrast_sex_null),
							Toast.LENGTH_SHORT).show();

					return;
				}
				if (jobStr == null || jobStr.equals("")) {

					Toast.makeText(getBaseContext(),
							getResources().getString(R.string.arrast_job_null),
							Toast.LENGTH_SHORT).show();

					return;
				}
				if (errorStr == null || errorStr.equals("")) {

					Toast.makeText(
							getBaseContext(),
							getResources()
									.getString(R.string.arrast_error_null),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (postNameStr == null || postNameStr.equals("")) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(
									R.string.arrast_postname_null),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (addreStr == null || addreStr.equals("")) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(
									R.string.arrast_address_null),
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (nameStr.getBytes().length > 15) {

					Toast.makeText(
							getBaseContext(),
							getResources()
									.getString(R.string.arrast_name_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (sexStr.getBytes().length > 3) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(R.string.arrast_sex_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (jobStr.getBytes().length > 18) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(R.string.arrast_job_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (errorStr.getBytes().length > 210) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(
									R.string.arrast_error_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (postNameStr.getBytes().length > 15) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(
									R.string.arrast_postname_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (addreStr.getBytes().length > 75) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(
									R.string.arrast_address_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (photoPath == null || photoPath.equals("")) {

					Toast.makeText(
							getBaseContext(),
							getResources().getString(R.string.arrast_icon_null),
							Toast.LENGTH_SHORT).show();
					return;

				}

				AtrrestOrder ao = new AtrrestOrder();
				ao.setIcon(photoPath);
				ao.setName(nameStr);
				ao.setSex(sexStr);
				ao.setJob(jobStr);
				ao.setError(errorStr);
				ao.setPostname(postNameStr);
				ao.setAddress(addreStr);
				Intent ii = new Intent(ArrastActivity.this,
						ArrastCorderActivity.class);
				ii.putExtra("arrest", ao);
				ArrastActivity.this.startActivity(ii);

				ArrastActivity.this.finish();

			}
		});
		nameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					nameEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					nameEdit.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		sexEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					sexEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					sexEdit.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		jobEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					jobEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					jobEdit.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		errorEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					errorEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					errorEdit
							.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		postNameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					postNameEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					postNameEdit
							.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		addreEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					addreEdit.setBackgroundResource(R.drawable.red_bg);
				} else {
					addreEdit
							.setBackgroundResource(R.drawable.arrast_text_no_selected_bg);
				}
			}
		});
		iconImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getPicDialog();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrastActivity.this.finish();
				;
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		backBtn = (Button) findViewById(R.id.arrast_three_back_btn);
		iconImg = (ImageView) findViewById(R.id.arrast_user_icon_edit);
		suerBtn = (Button) findViewById(R.id.arrast_sure_btn);
		nameEdit = (EditText) findViewById(R.id.arrast_user_name_edit);
		sexEdit = (EditText) findViewById(R.id.arrast_user_sex_edit);
		jobEdit = (EditText) findViewById(R.id.arrast_user_job_edit);
		errorEdit = (EditText) findViewById(R.id.arrast_info_text_edit);
		postNameEdit = (EditText) findViewById(R.id.arrast_user_post_name_edit);
		addreEdit = (EditText) findViewById(R.id.arrast_user_address_edit);

		// nameText = (TextView) findViewById(R.id.arrast_user_name);
		// sexText = (TextView) findViewById(R.id.arrast_user_sex);
		// jobText = (TextView) findViewById(R.id.arrast_user_job);
		// errorText = (TextView) findViewById(R.id.arrast_info_text);
		// postNameText = (TextView) findViewById(R.id.arrast_user_post_name);
		// addressText = (TextView) findViewById(R.id.arrast_user_address);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			if (requestCode == 2) {
				CompressImage compress = new CompressImage(ArrastActivity.this,
						photoPath);
				Bitmap bitmap = null;
				try {
					bitmap = compress.getBitmap();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Bitmap bitmap = BitmapFactory.decodeFile(imgFile
				// .getAbsolutePath());
				if (bitmap != null) {
					iconImg.setImageBitmap(bitmap);
					bitmap = null;
				} else {

				}

			} else if (requestCode == 1) {

				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				// 使用ContentProvider通过URI获取原始图片
				// Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
				// originalUri);
				if (originalUri != null) {
					photoPath = uri2filePath(originalUri);
					CompressImage compress = new CompressImage(
							ArrastActivity.this, photoPath);
					Bitmap bitmap = null;
					try {
						bitmap = compress.getBitmap();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (bitmap != null) {

						iconImg.setImageBitmap(bitmap);
						bitmap = null;

					}
				}

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String uri2filePath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(index);
		if (cursor != null) {
			cursor.close();
		}
		return path;
	}

	private void getPicDialog() {
		builderPic = new Dialog(ArrastActivity.this);
		builderPic.show();
		View view = LayoutInflater.from(ArrastActivity.this).inflate(
				R.layout.dialog_pic, null);
		builderPic.setTitle("选择图片");
		builderPic.setContentView(R.layout.dialog_pic);
		cameraBtn = (TextView) builderPic.findViewById(R.id.dialog_pic_camera);
		picBtn = (TextView) builderPic.findViewById(R.id.dialog_pic_pic);
		Window window = builderPic.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.3f;
		window.setAttributes(params);
		cameraBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 先验证手机是否有sdcard
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						String path1 = Environment
								.getExternalStorageDirectory().getPath()
								+ "/yxkuailePic"
								+ "/"
								+ ImageTools.createFileName();
						imgFile = new File(path1);
						if (!imgFile.getParentFile().exists()) {
							// 若不存在，创建目录，可以在应用启动的时候创建
							imgFile.getParentFile().mkdirs();

						}
						if (!imgFile.exists()) {
							try {
								imgFile.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						photoPath = "" + imgFile.getAbsolutePath();
						// imgFile = new File(Environment
						// .getExternalStorageDirectory(), ImageTools
						// .createFileName());
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(imgFile));
						startActivityForResult(intent, 2);
					} catch (ActivityNotFoundException e) {
						// TODO Auto-generated catch block
						Toast.makeText(ArrastActivity.this, "没有找到储存目录",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ArrastActivity.this, "没有储存卡",
							Toast.LENGTH_LONG).show();
				}
				builderPic.dismiss();
			}

		});
		picBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builderPic.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		if (ApplicationData.getInstance() != null) {
//			outState.putParcelable("userData", ApplicationData.getInstance());
//
//		}

		outState.putString("filePath", photoPath);
		outState.putString("nameStr", "" + nameEdit.getText().toString());
		outState.putString("sexStr", "" + sexEdit.getText().toString());
		outState.putString("jobStr", "" + jobEdit.getText().toString());
		outState.putString("errorStr", "" + errorEdit.getText().toString());
		outState.putString("postNameStr", ""
				+ postNameEdit.getText().toString());
		outState.putString("addressStr", "" + addreEdit.getText().toString());

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ArrastActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
