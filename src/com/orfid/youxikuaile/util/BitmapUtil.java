package com.orfid.youxikuaile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;

import com.orfid.youxikuaile.R;

/**
 * bitmap 处理工具类，
 * 
 * @param context
 * @return
 */
public class BitmapUtil {
	/**
	 * bitmap 压缩，返回byte数组，
	 * 
	 * @param context
	 * @return
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 本地网络图片，转化为bitmap resId 为本地图片id
	 * 
	 * @param context
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// ��ȡ��ԴͼƬ
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 本地网络图片，转化为指定宽高的bitmap resId 为本地图片id screenWidth 指定宽 screenHight 指定高
	 * 
	 * @param context
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int drawableId,
			int screenWidth, int screenHight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		InputStream stream = context.getResources().openRawResource(drawableId);
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
		return getBitmap(bitmap, screenWidth, screenHight);
	}

	/**
	 * bitmap转化为指定宽高的bitmap
	 * 
	 * screenWidth 指定宽 screenHight 指定高
	 * 
	 * @param context
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Log.e("jj", "ͼƬ���" + w + ",screenWidth=" + screenWidth);
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		// ��֤ͼƬ������.
		matrix.postScale(scale, scale);
		// w,h��ԭͼ������.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	/***
	 * ����ͼƬ��SD��
	 * 
	 * @param bm
	 * @param url
	 * @param quantity
	 */
	private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
	private static int MB = 1024 * 1024;
	public final static String DIR = "/sdcard/hypers";

	/**
	 * 保存图片至sd卡
	 * 
	 * @param context
	 * @return
	 */
	public static void saveBmpToSd(Bitmap bm, String url, int quantity) {
		// �ж�sdcard�ϵĿռ�
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			return;
		}
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
			return;
		String filename = url;
		// Ŀ¼�����ھʹ���
		File dirPath = new File(DIR);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}

		File file = new File(DIR + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, quantity, outStream);
			outStream.flush();
			outStream.close();

		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/***
	 * �ж�ͼƬ�Ǵ���
	 * 
	 * @param url
	 * @return
	 */
	// public static boolean Exist(String url) {
	// File file = new File(DIR + url);
	// return file.exists();
	// }

	/**
	 * 判断sd卡空间
	 * 
	 * @param context
	 * @return
	 */
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;

		return (int) sdFreeMB;
	}

	/**
	 * 获取圆形bitmap
	 * 
	 * @param context
	 * @return
	 */

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(outBitmap);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPX = bitmap.getWidth() / 2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return outBitmap;
	}

	/**
	 * 截取全屏为bitmap
	 * 
	 * @param context
	 * @return
	 */

	public static Bitmap takeScreenShot(Activity activity) {

		Log.i("TAG", "tackScreenShot");
		// View是你须要截图的View
		View targetView = activity.getWindow().getDecorView();
		// targetView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);//
		// 截全屏
		targetView.setDrawingCacheEnabled(true);
		targetView.buildDrawingCache();
		Bitmap b1 = targetView.getDrawingCache();
		if (b1 != null) {
			Log.i("info", "b1 != null");
			// 获取状况栏高度
			Rect frame = new Rect();
			activity.getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(frame);
			int statusBarHeight = frame.top;
			Log.i("TAG", "statusBarHeight = " + statusBarHeight);

			// 获取屏幕长和高
			int width = activity.getWindowManager().getDefaultDisplay()
					.getWidth();
			int height = activity.getWindowManager().getDefaultDisplay()
					.getHeight();

			// 去掉题目栏
			// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
			Bitmap b = Bitmap.createBitmap(
					b1,
					0,
					statusBarHeight
							+ activity.getResources().getDimensionPixelSize(
									R.dimen.hayou_user_list_top),
					width,
					height
							- statusBarHeight
							- activity.getResources().getDimensionPixelSize(
									R.dimen.hayou_user_list_top));
			targetView.destroyDrawingCache();
			return b;
		} else {
			Log.i("info", "b1 == null");
			Bitmap b = null;
			return b;
		}

	}

	/**
	 * 通过URL获得网上图片。如:http://www.xxxxxx.com/xx.jpg
	 * */
	public static Bitmap getBitmap(String url, int screenWidth, int screenHeight)
			throws MalformedURLException, IOException {
		Bitmap bmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		InputStream stream = new URL(url).openStream();
		byte[] bytes = getBytes(stream);
		// 这3句是处理图片溢出的begin( 如果不需要处理溢出直接 opts.inSampleSize=1;)
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		// opts.inSampleSize = computeSampleSize(opts, -1, displaypixels);
		// // end
		// Log.i("info", "opts.inSampleSize:" + opts.inSampleSize);
		int picWidth = opts.outWidth;
		int picHeight = opts.outHeight;

		// 获取屏的宽度和高度

		if (screenWidth > 320) {
			screenWidth = 320;
		}
		if (screenHeight > 480) {
			screenHeight = 480;
		}
		// isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
		opts.inSampleSize = 1;
		// 根据屏的大小和图片大小计算出缩放比例
		if (picWidth > picHeight) {
			if (picWidth > screenWidth)
				opts.inSampleSize = picWidth / screenWidth;
		} else {
			if (picHeight > screenHeight) {
				opts.inSampleSize = picHeight / screenHeight;
			}
		}
		opts.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return compressImage(bmp, 50);
	}

	/**
	 * 数据流转成btyle[]数组
	 * */
	public static byte[] getBytes(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[2048];
		int len = 0;
		try {
			while ((len = is.read(b, 0, 2048)) != -1) {
				baos.write(b, 0, len);
				baos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	public static Bitmap compressImage(Bitmap image, int size) {
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
}
