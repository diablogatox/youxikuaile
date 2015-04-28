package com.orfid.youxikuaile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 图片压缩工具类
 * 
 * @author Administrator
 * 
 */
public class CompressImage {
	private Bitmap bm;
	private String filePath;
	private Activity context;

	public CompressImage(Activity context, String filePath) {
		this.filePath = filePath;
		this.context = context;
	}

	// public Bitmap getBitmap() throws Exception {
	// BitmapFactory.Options opt = new BitmapFactory.Options();
	// // 这个isjustdecodebounds很重要
	// opt.inJustDecodeBounds = true;
	// bm = BitmapFactory.decodeFile(filePath, opt);
	// opt.inJustDecodeBounds = false;
	// // 获取到这个图片的原始宽度和高度
	// int picWidth = opt.outWidth;
	// int picHeight = opt.outHeight;
	// Log.i("info", "picWidth:" + picWidth);
	// Log.i("info", "picHeight:" + picHeight);
	// // 获取屏的宽度和高度
	// WindowManager windowManager = context.getWindowManager();
	// Display display = windowManager.getDefaultDisplay();
	// int screenWidth = display.getWidth();
	// int screenHeight = display.getHeight();
	// // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
	// opt.inSampleSize = 1;
	// // 根据屏的大小和图片大小计算出缩放比例
	// if (picWidth > picHeight) {
	// if (picWidth > 480)
	// opt.inSampleSize = picWidth / 540;
	//
	// } else {
	// if (picHeight > 800) {
	// opt.inSampleSize = picHeight / 960;
	// }
	//
	// }
	// // 这次再真正地生成一个有像素的，经过缩放了的bitmap
	// Log.i("info", "opt.inSampleSize:" + opt.inSampleSize);
	// bm = BitmapFactory.decodeFile(filePath, opt);
	// Log.i("info", "bm.width:" + bm.getWidth());
	// Log.i("info", "bm.height:" + bm.getHeight());
	// return compressImage(bm, 100);
	//
	// }
	
	
	
	public Bitmap getBitmap() throws Exception {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		// 这个isjustdecodebounds很重要
		opt.inJustDecodeBounds = true;
		bm = BitmapFactory.decodeFile(filePath, opt);
		opt.inJustDecodeBounds = false;
		// 获取到这个图片的原始宽度和高度
		int picWidth = opt.outWidth;
		int picHeight = opt.outHeight;
		Log.i("info", "picWidth:" + picWidth);
		Log.i("info", "picHeight:" + picHeight);
		// 获取屏的宽度和高度
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		if (screenWidth > 480) {
			screenWidth = 480;
		}
		if (screenHeight > 800) {
			screenHeight = 800;
		}
		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opt.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (picWidth > picHeight) {
			if (picWidth > screenWidth)
				opt.inSampleSize = picWidth / screenWidth;

		} else {
			if (picHeight > screenHeight) {
				opt.inSampleSize = picHeight / screenHeight;
			}

		}
		// 这次再真正地生成一个有像素的，经过缩放了的bitmap
		Log.i("info", "opt.inSampleSize:" + opt.inSampleSize);
		bm = BitmapFactory.decodeFile(filePath, opt);
		Log.i("info", "bm.width:" + bm.getWidth());
		Log.i("info", "bm.height:" + bm.getHeight());
		return compressImage(bm, 100);

	}

	private Bitmap compressImage(Bitmap image, int size) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > size && options > 0) {
				// 循环判断如果压缩后图片是否大于50kb,大于继续压缩
				Log.i("info", "baos.toByteArray():" + baos.toByteArray().length
						/ 1024);
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);
				Log.i("info", "baos.toByteArray():" + baos.toByteArray().length
						/ 1024);
				// 这里压缩比options=50，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			image = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			// String path = Environment.getExternalStorageDirectory()
			// .getAbsolutePath() + "/compress.jpg";
			// FileOutputStream fileOutputStream = new FileOutputStream(new
			// File(
			// path));
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 50,
			// fileOutputStream);
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	// public static String zoom(String sourcePath, String targetPath, int
	// width,
	// int height) throws IOException {
	// File file = new File(sourcePath);
	// if (!file.exists()) {
	// throw new IOException("not found the image ：" + sourcePath);
	// }
	// if (null == targetPath || targetPath.isEmpty())
	// targetPath = sourcePath;
	//
	// String formatName = getImageFormatName(file);
	// if (null == formatName)
	// return targetPath;
	// formatName = formatName.toLowerCase();
	//
	// // 防止图片后缀与图片本身类型不一致的情况
	// String pathPrefix = getPathWithoutSuffix(targetPath);
	// targetPath = pathPrefix + formatName;
	//
	// // GIF需要特殊处理
	// if (IMAGE_FORMAT.GIF.getValue() == formatName) {
	// GifDecoder decoder = new GifDecoder();
	// int status = decoder.read(sourcePath);
	// if (status != GifDecoder.STATUS_OK) {
	// throw new IOException("read image " + sourcePath + " error!");
	// }
	//
	// AnimatedGifEncoder encoder = new AnimatedGifEncoder();
	// encoder.start(targetPath);
	// encoder.setRepeat(decoder.getLoopCount());
	// for (int i = 0; i < decoder.getFrameCount(); i++) {
	// encoder.setDelay(decoder.getDelay(i));
	// BufferedImage image = zoom(decoder.getFrame(i), width, height);
	// encoder.addFrame(image);
	// }
	// encoder.finish();
	// } else {
	// BufferedImage image = ImageIO.read(file);
	// BufferedImage zoomImage = zoom(image, width, height);
	// ImageIO.write(zoomImage, formatName, new File(targetPath));
	// }
	// BufferedImage image = ImageIO.read(file);
	// BufferedImage zoomImage = zoom(image, width, height);
	// ImageIO.write(zoomImage, formatName, new File(targetPath));
	//
	// return targetPath;
	// }

}
