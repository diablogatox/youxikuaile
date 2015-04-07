package com.orfid.youxikuaile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * 查看大图的Activity界面。
 * 
 * @author guolin
 */
public class ImageDetailsActivity extends Activity {
	
	/**
	 * 自定义的ImageView控制，可对图片进行多点触控缩放和拖动
	 */
	private ImageView zoomImageView, btn_back;

	/**
	 * 待展示的图片
	 */
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail);
        
		zoomImageView = (ImageView) findViewById(R.id.zoom_image_view);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		// 取出图片路径，并解析成Bitmap对象，然后在ZoomImageView中显示
		String imagePath = getIntent().getStringExtra("image_path");
		String imageUrl = getIntent().getStringExtra("image_url");
		if (imagePath != null) {
			bitmap = BitmapFactory.decodeFile(imagePath);
			zoomImageView.setImageBitmap(bitmap);
		} else if (imageUrl != null) {
			Log.d("imageUrl=====>", imageUrl);
			ImageLoader.getInstance().displayImage(imageUrl, zoomImageView);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 记得将Bitmap对象回收掉
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

}