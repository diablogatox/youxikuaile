package com.orfid.youxikuaile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class SelectPicActivity extends Activity {
	private Button btn_take_photo, btn_pick_photo, btn_cancel;   
	private LinearLayout layout;
	SharedPreferences sp_pic_info;
	Editor ed_pic_info;
	
	private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_ZOOM = 2; // 缩放
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_pic);
		
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);   
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);   
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);   
		layout=(LinearLayout)findViewById(R.id.pop_layout); 
		sp_pic_info = getSharedPreferences("pic", MODE_PRIVATE);
		ed_pic_info = sp_pic_info.edit();
		//���ѡ�񴰿ڷ�Χ�����������Ȼ�ȡ���㣬������ִ��onTouchEvent()��������������ط�ʱִ��onTouchEvent()��������Activity   
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
 
			}
		 });   
		 //ȡ��   
		 btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});  
		 //���ֻ����ѡ��
		 btn_pick_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, PHOTO_ZOOM);
			}
		});   
		 //����
		 btn_take_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(),"temp.jpg")));
                startActivityForResult(intent, PHOTO_GRAPH);
			}
		});

	}
	//ʵ��onTouchEvent���������������Ļʱ���ٱ�Activity   
	@Override  
	public boolean onTouchEvent(MotionEvent event){   
	    finish();   
	    return true;   
	} 
	/**
	 * ���ݲ�ͬ��ʽѡ��ͼƬ����ImageView
	 * 
	 * @param type
	 *            0-�������ѡ�񣬷�0Ϊ����
	 */
//	private void doHandlerPhoto(int type) {
//		try {
//			// ����ü����ͼƬ�ļ�
//			File pictureFileDir = new File(
//					Environment.getExternalStorageDirectory(), "/upload");
//			if (!pictureFileDir.exists()) {
//				pictureFileDir.mkdirs();
//			}
//			File picFile = new File(pictureFileDir, "upload-" + System.currentTimeMillis() + ".jpeg");
//			if (!picFile.exists()) {
//				picFile.createNewFile();
//			}
//			photoUri = Uri.fromFile(picFile);
//			Log.i("TEST", "photoUri-create-----" + photoUri);
//
//			if (type == PIC_FROM_LOCALPHOTO) {// ���
//				Intent intent = getCropImageIntent();
//				startActivityForResult(intent, PIC_FROM_LOCALPHOTO);
//			} else {
//				Intent cameraIntent = new Intent(
//						MediaStore.ACTION_IMAGE_CAPTURE);
//				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//				startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
//			}
//
//		} catch (Exception e) {
//			Log.i("HandlerPicError", "����ͼƬ���ִ���");
//		}
//	}

	/**
	 * ����ͼƬ��������
	 */
//	public Intent getCropImageIntent() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		setIntentParams(intent);
//		return intent;
//	}

	/**
	 * ���ù��ò���
	 */
//	private void setIntentParams(Intent intent) {
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", 600);
//		intent.putExtra("outputY", 600);
//		intent.putExtra("noFaceDetection", true); // no face detection
//		intent.putExtra("scale", true);
//		intent.putExtra("return-data", false);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//	}
	/**
	 * �õ�����󷵻ظ���һ��ҳ��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case PIC_FROM_CAMERA: // ����
//			try {
//				cropImageUriByTakePhoto();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			break;
//		case PIC_FROM_LOCALPHOTO:// ���
//			try {
//				if (photoUri != null) {
//					Log.i("TEST", "start-----" + photoUri);
////					Bitmap bitmap = decodeUriAsBitmap(photoUri);
//					Intent intent = new Intent();
//					intent.putExtra("photoUri", photoUri.getPath());
//					setResult(RESULT_OK, intent);
////					 SelectPicActivity.this.finish();
////					Log.i("TEST", "end-----" + photoUri);
//					finish();
//				} else {
//					SelectPicActivity.this.finish();
//					Log.i("TEST", "��photoUri-----" + photoUri);
//				}
//			} catch (Exception e) {
//				Log.i("TEST", "�쳣-----" + photoUri);
//				e.printStackTrace();
//			}
//			break;
//		}
		
		
		if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File picture = new File(Environment.getExternalStorageDirectory()
                    + "/temp.jpg");
            startPhotoZoom(Uri.fromFile(picture));
        }

        if (data == null)
            return;

        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM) {
            startPhotoZoom(data.getData());
        }
        // 处理结果
        if (requestCode == PHOTO_RESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                File file = savebitmap(photo);
				Intent intent = new Intent();
				intent.putExtra("photoPath", file.getAbsolutePath());
				setResult(RESULT_OK, intent);
                finish();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private File savebitmap(Bitmap bmp) {
	    String extStorageDirectory = Environment.getExternalStorageDirectory()
	            .toString();
	    OutputStream outStream = null;
	    String fName = "cropImage-" + System.currentTimeMillis();

	    File file = new File(extStorageDirectory, fName + ".png");
	    if (file.exists()) {
	        file.delete();
	        file = new File(extStorageDirectory, fName + ".png");
	        Log.e("file exist", "" + file + ",Bitmap= " + fName);
	    }
	    try {
	        outStream = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
	        outStream.flush();
	        outStream.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    Log.e("file", "" + file);
	    return file;

	}
	/**
	 * ������ĳɵ�ͼƬ�ü�����
	 */
//	private void cropImageUriByTakePhoto() {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(photoUri, "image/*");
//		setIntentParams(intent);
//		startActivityForResult(intent, PIC_FROM_LOCALPHOTO);
//	}
//
//	/**
//	 *�����ص�ͼƬת����bitmap����
//	 * @param uri
//	 * @return
//	 */
//	private Bitmap decodeUriAsBitmap(Uri uri) {
//		Bitmap bitmap = null;
//		try {
//			bitmap = BitmapFactory.decodeStream(getContentResolver()
//					.openInputStream(uri));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return bitmap;
//	}
	
	/**
     * 收缩图片
     * 
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

}
