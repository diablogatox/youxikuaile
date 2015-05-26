package com.orfid.youxikuaile.widget;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orfid.youxikuaile.R;
import com.orfid.youxikuaile.pojo.UserIcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义view，积分工厂，绘制游戏区域 开始绘制8*8的格子，即64个格子。 然后将HashMap<String, Bitmap>，头像bitmap
 * ，安装获得position，绘制到相应位置。
 * 
 * @author clh
 * 
 */
public class ScoreView extends View {
	private int index = 0;
	private int canvasWidth = 0, canvasHeight = 0, zoneWidth, zoneHeight;
	private Bitmap mPorLine, mLandLine, mDynamic, danamic1, danamic2, danamic3;
	private Bitmap hammer1Bit, hammer2Bit, palletBit, astigmatismBit;
	private int rectX_1, rectY_1, rectX_2, rectY_2, rectX_3, rectY_3;
	private int x_1, y_1, x_2, y_2, x_3, y_3;
	private int m1, m2, m3;
	private int hammerIndex = 0;
	private HashMap<String, Bitmap> gameUserIconMap;
	private List<UserIcon> userInfo;
	private int last_x = 0, first_x = 0;
	private int last_y = 0, first_y = 0;
	private int intNum = -1, locaNum = -1;
	private int distanceTranslate;
	private UserIcon user;
	private Handler viewHandler;
	private String uid = "-1", othreId = "-1";

	public ScoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mPorLine = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.score_portrait_line);
		this.mLandLine = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.score_landscape_line);
		this.mDynamic = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.score_dynamic);
		danamic1 = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.dynamic_1);
		danamic2 = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.dynamic_2);
		danamic3 = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.dynamic_3);
		this.hammer1Bit = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.hammer1);
		this.hammer2Bit = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.hammer2);
		this.palletBit = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.pallet);
		this.astigmatismBit = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.astigmatism);

		this.distanceTranslate = (int) getResources().getDimension(
				R.dimen.hayou_image_border_width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint = new Paint();
		if (index == 0) {
			canvasWidth = this.getWidth();
			canvasHeight = this.getHeight();
			zoneWidth = canvasWidth / 8;
			zoneHeight = canvasHeight / 8;
			m1 = zoneHeight * 6;
			m2 = m1;
			m3 = m1;
			x_1 = (zoneWidth - danamic1.getWidth()) / 2;
			y_1 = (zoneHeight * 4 / 3 - danamic1.getHeight()) / 2;
			rectX_1 = zoneWidth * 6 + x_1;
			rectY_1 = zoneHeight * 2 + y_1;
			// 控制显示的区域，矩形之内能看到，之外看不到
			x_2 = (zoneWidth - danamic2.getWidth()) / 2;
			y_2 = (zoneHeight * 4 / 3 - danamic2.getHeight()) / 2;
			rectX_2 = zoneWidth * 6 + x_2;
			rectY_2 = zoneHeight * 2 + y_2 + zoneHeight * 4 / 3;

			x_3 = (zoneWidth - danamic3.getWidth()) / 2;
			y_3 = (zoneHeight * 4 / 3 - danamic3.getHeight()) / 2;
			rectX_3 = zoneWidth * 6 + x_3;
			rectY_3 = zoneHeight * 2 + y_3 + zoneHeight * 4 / 3 * 2;
			index = 1;
		}
		Rect src = new Rect();
		Rect dst = new Rect();
		src.left = 0; // 0,0
		src.top = 0;
		src.right = this.mDynamic.getWidth();//
		src.bottom = this.mDynamic.getHeight();//
		dst.left = zoneWidth * 6;
		dst.top = zoneHeight * 2;
		dst.right = zoneWidth * 7;
		dst.bottom = zoneHeight * 6;
		canvas.drawBitmap(this.mDynamic, src, dst, paint);
		for (int row = 0; row < 7; row++) {
			Rect srcc = new Rect();
			Rect dstc = new Rect();
			srcc.left = 0; // 0,0
			srcc.top = 0;
			srcc.right = this.mPorLine.getWidth();
			srcc.bottom = this.mPorLine.getHeight();
			dstc.left = 0;
			dstc.top = zoneHeight * (row + 1);
			dstc.right = this.getWidth();
			dstc.bottom = zoneHeight * (row + 1) + this.mPorLine.getHeight(); // mBitQQ.getHeight()
																				// //
																				// //
																				// +
			canvas.drawBitmap(this.mPorLine, srcc, dstc, paint);
		}
		if (userInfo != null) {
			if (userInfo.size() > 0) {
				for (UserIcon item : userInfo) {
					Bitmap val = gameUserIconMap.get(item.getId());
					if (val != null) {

						Rect s = new Rect();
						Rect d = new Rect();
						s.left = 0;
						s.top = 0;
						s.right = val.getWidth();
						s.bottom = val.getHeight();
						// number 是位置 计数是从1到64的标号
						int number = Integer.parseInt(item.getPosition()) + 1;
						int nHeight = number / 8;
						int mWidth;
						if (number % 8 == 0) {
							mWidth = 7;
							nHeight = number / 8 - 1;
						} else {
							mWidth = number % 8 - 1;
						}
						if (intNum == number
						// && locaNum != 23 && locaNum != 31
						// && locaNum != 39 && locaNum != 47
						// && locaNum != intNum
						) {
							if (locaNum > 0) {
								int nH = locaNum / 8;
								int mW;
								if (locaNum % 8 == 0) {
									mW = 7;
									nH = locaNum / 8 - 1;
								} else {
									mW = locaNum % 8 - 1;
								}
								d.left = zoneWidth * mW;
								d.top = zoneHeight * nH;
								d.right = zoneWidth * (mW + 1);
								d.bottom = zoneHeight * (nH + 1);
								canvas.drawBitmap((Bitmap) val, s, d, paint);
								locaNum = -1;
								intNum = -1;
								if (item.getOnline().equals("1")) {
									if (hammerIndex % 67 != 0) {
										Log.d("test1====>", "test1");
										Rect srcs6 = new Rect();// 图片
										srcs6.left = 0; // 0,0
										srcs6.top = 0;
										srcs6.right = this.hammer1Bit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs6.bottom = this.hammer1Bit
												.getHeight();
										Rect dsts6 = new Rect();
										dsts6.left = zoneWidth
												// * mW
												// + getResources()
												// .getDimensionPixelSize(
												// R.dimen.hayou_tiezi_pop_left)
												* (mW + 1)
												- (int) (zoneWidth / 3);
										dsts6.top = zoneHeight * (nH + 1)
												- (int) (zoneWidth / 3)
										// (int) (this.hammer1Bit
										// .getHeight() / 1.5)
										;
										dsts6.right = dsts6.left
												+ (int) (zoneWidth / 3);
										dsts6.bottom = dsts6.top
												+ (int) (zoneWidth / 3);
										canvas.drawBitmap(this.hammer1Bit,
												srcs6, dsts6, paint);

										Rect srcs7 = new Rect();// 图片
										srcs7.left = 0; // 0,0
										srcs7.top = 0;
										srcs7.right = this.palletBit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs7.bottom = this.palletBit
												.getHeight();
										Rect dsts7 = new Rect();
										dsts7.left = zoneWidth
												// * mW
												// + getResources()
												// .getDimensionPixelSize(
												// R.dimen.hayou_tiezi_pop_left)
												* (mW + 1)
												- (int) (zoneWidth / 3);
										dsts7.top = zoneHeight * (nH + 1)
												- (int) (zoneWidth / 3)
										// (int) (this.palletBit
										// .getHeight() / 1.5)
										;
										dsts7.right = dsts7.left
												+ (int) (zoneWidth / 3);
										dsts7.bottom = dsts7.top
												+ (int) (zoneWidth / 3);
										canvas.drawBitmap(this.palletBit,
												srcs7, dsts7, paint);

									} else if (hammerIndex % 67 == 0) {
										Log.d("test2====>", "test2");
										Rect srcs8 = new Rect();// 图片
										srcs8.left = 0; // 0,0
										srcs8.top = 0;
										srcs8.right = this.hammer2Bit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs8.bottom = this.hammer2Bit
												.getHeight();
										Rect dsts8 = new Rect();
										dsts8.left = zoneWidth * (mW + 1)
												- (int) (zoneWidth / 3)
										// * mW
										// + getResources()
										// .getDimensionPixelSize(
										// R.dimen.hayou_tiezi_pop_left)

										;
										dsts8.top = zoneHeight * (nH + 1)
												- (int) (zoneWidth / 3);
										// - (int) (this.hammer1Bit
										// .getHeight() / 1.5);
										dsts8.right = dsts8.left
												+ (int) (zoneWidth / 3);
										dsts8.bottom = dsts8.top
												+ (int) (zoneWidth / 3);
										canvas.drawBitmap(this.hammer2Bit,
												srcs8, dsts8, paint);

										Rect srcs9 = new Rect();// 图片
										srcs9.left = 0; // 0,0
										srcs9.top = 0;
										srcs9.right = this.palletBit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs9.bottom = this.palletBit
												.getHeight();
										Rect dsts9 = new Rect();
										dsts9.left = zoneWidth
												// * mW
												// + getResources()
												// .getDimensionPixelSize(
												// R.dimen.hayou_tiezi_pop_left)
												* (mW + 1)
												- (int) (zoneWidth / 3)

										;
										dsts9.top = zoneHeight * (nH + 1)
												- (int) (zoneWidth / 3);
										// - (int) (this.palletBit
										// .getHeight() / 1.5);
										dsts9.right = dsts9.left
												+ (int) (zoneWidth / 3);
										dsts9.bottom = dsts9.top
												+ (int) (zoneWidth / 3);
										canvas.drawBitmap(this.palletBit,
												srcs9, dsts9, paint);

										Rect srcs10 = new Rect();// 图片
										srcs10.left = 0; // 0,0
										srcs10.top = 0;
										srcs10.right = this.astigmatismBit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs10.bottom = this.astigmatismBit
												.getHeight();
										Rect dsts10 = new Rect();
										dsts10.left = zoneWidth * (mW + 1)
												- (int) (zoneWidth / 3)
										// * mW
										// + getResources()
										// .getDimensionPixelSize(
										// R.dimen.hayou_tiezi_pop_left)

										;
										dsts10.top = zoneHeight * (nH + 1)
												- (int) (zoneWidth / 3);
										// - (int) (this.astigmatismBit
										// .getHeight() / 1.5);
										dsts10.right = dsts10.left
												+ (int) (zoneWidth / 3);
										dsts10.bottom = dsts10.top
												+ (int) (zoneWidth / 3);
										canvas.drawBitmap(this.astigmatismBit,
												srcs10, dsts10, paint);

									}
								}
							} else {
								d.left = zoneWidth * mWidth + last_x;
								d.top = zoneHeight * nHeight + last_y;
								d.right = zoneWidth * (mWidth + 1) + last_x;
								d.bottom = zoneHeight * (nHeight + 1) + last_y;
								canvas.drawBitmap((Bitmap) val, s, d, paint);
								if (item.getOnline().equals("1")) {
									// Rect srcs11 = new Rect();// 图片
									// srcs11.left = 0; // 0,0
									// srcs11.top = 0;
									// srcs11.right =
									// this.astigmatismBit.getWidth();//
									// mBitDestTop.getWidth();,这个是桌面图的宽度，
									// srcs11.bottom =
									// this.astigmatismBit.getHeight();
									Rect dsts11 = new Rect();
									dsts11.left = zoneWidth * (mWidth + 1)
											- (int) (zoneWidth / 3)
											// * mWidth
											// + getResources()
											// .getDimensionPixelSize(
											// R.dimen.hayou_tiezi_pop_left)
											+ last_x;
									dsts11.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3)
											// - (int) (this.astigmatismBit
											// .getHeight() / 1.5)
											+ last_y;
									dsts11.right = dsts11.left
											+ (int) (zoneWidth / 3);
									dsts11.bottom = dsts11.top
											+ (int) (zoneWidth / 3);
									// int sW = zoneWidth
									// * mWidth
									// + getResources().getDimensionPixelSize(
									// R.dimen.hayou_tiezi_pop_left)
									// + last_x;
									// int sH = zoneHeight * (nHeight + 1)
									// - this.hammer1Bit.getHeight() + last_y;

									if (hammerIndex % 67 != 0) {
										Rect srcs11 = new Rect();// 图片
										srcs11.left = 0; // 0,0
										srcs11.top = 0;
										srcs11.right = this.hammer1Bit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs11.bottom = this.hammer1Bit
												.getHeight();
										canvas.drawBitmap(this.hammer1Bit,
												srcs11, dsts11, paint);
										// canvas.drawBitmap(this.hammer1Bit,
										// sW,
										// sH,
										// paint);
										Rect srcs12 = new Rect();// 图片
										srcs12.left = 0; // 0,0
										srcs12.top = 0;
										srcs12.right = this.palletBit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs12.bottom = this.palletBit
												.getHeight();
										canvas.drawBitmap(this.palletBit,
												srcs12, dsts11, paint);
										// canvas.drawBitmap(this.palletBit, sW,
										// sH,
										// paint);
									} else if (hammerIndex % 67 == 0) {
										Rect srcs13 = new Rect();// 图片
										srcs13.left = 0; // 0,0
										srcs13.top = 0;
										srcs13.right = this.hammer2Bit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs13.bottom = this.hammer2Bit
												.getHeight();
										canvas.drawBitmap(this.hammer2Bit,
												srcs13, dsts11, paint);
										// canvas.drawBitmap(this.hammer2Bit,
										// sW,
										// sH,
										// paint);
										Rect srcs14 = new Rect();// 图片
										srcs14.left = 0; // 0,0
										srcs14.top = 0;
										srcs14.right = this.palletBit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs14.bottom = this.palletBit
												.getHeight();
										canvas.drawBitmap(this.palletBit,
												srcs14, dsts11, paint);
										// canvas.drawBitmap(this.palletBit, sW,
										// sH,
										// paint);
										Rect srcs15 = new Rect();// 图片
										srcs15.left = 0; // 0,0
										srcs15.top = 0;
										srcs15.right = this.astigmatismBit
												.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
										srcs15.bottom = this.astigmatismBit
												.getHeight();
										canvas.drawBitmap(this.astigmatismBit,
												srcs15, dsts11, paint);
										// canvas.drawBitmap(this.astigmatismBit,
										// sW,
										// sH, paint);
									}
								}
							}
						} else {
							d.left = zoneWidth * mWidth;
							d.top = zoneHeight * nHeight;
							d.right = zoneWidth * (mWidth + 1);
							d.bottom = zoneHeight * (nHeight + 1);
							canvas.drawBitmap(val, s, d, paint);
							if (item.getOnline().equals("1")) {
								if (hammerIndex % 67 != 0) {
									Rect srcs1 = new Rect();// 图片
									srcs1.left = 0; // 0,0
									srcs1.top = 0;
									srcs1.right = this.hammer1Bit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
									srcs1.bottom = this.hammer1Bit.getHeight();
									Rect dsts1 = new Rect();
									dsts1.left = zoneWidth
											// * mWidth
											// + getResources()
											// .getDimensionPixelSize(
											// R.dimen.hayou_tiezi_pop_left)
											* (mWidth + 1)
											- (int) (zoneWidth / 3);
									dsts1.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3);
									// - (int) (this.hammer1Bit
									// .getHeight() / 1.5);
									dsts1.right = dsts1.left
											+ (int) (zoneWidth / 3);
									dsts1.bottom = dsts1.top
											+ (int) (zoneWidth / 3);
									canvas.drawBitmap(this.hammer1Bit, srcs1,
											dsts1, paint);

									Rect srcs2 = new Rect();// 图片
									srcs2.left = 0; // 0,0
									srcs2.top = 0;
									srcs2.right = this.palletBit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
									srcs2.bottom = this.palletBit.getHeight();
									Rect dsts2 = new Rect();
									dsts2.left = zoneWidth * (mWidth + 1)
											- (int) (zoneWidth / 3)
									// * mWidth
									// + getResources()
									// .getDimensionPixelSize(
									// R.dimen.hayou_tiezi_pop_left)
									;
									dsts2.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3);
									// - (int) (this.palletBit.getHeight() /
									// 1.5);
									dsts2.right = dsts2.left
											+ (int) (zoneWidth / 3);
									dsts2.bottom = dsts2.top
											+ (int) (zoneWidth / 3);
									canvas.drawBitmap(this.palletBit, srcs2,
											dsts2, paint);

								} else if (hammerIndex % 67 == 0) {

									Rect srcs3 = new Rect();// 图片
									srcs3.left = 0; // 0,0
									srcs3.top = 0;
									srcs3.right = this.hammer2Bit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
									srcs3.bottom = this.hammer2Bit.getHeight();
									Rect dsts3 = new Rect();
									dsts3.left = zoneWidth * (mWidth + 1)
											- (int) (zoneWidth / 3)
									// * mWidth
									// + getResources()
									// .getDimensionPixelSize(
									// R.dimen.hayou_tiezi_pop_left)
									;
									dsts3.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3);
									// - (int) (this.hammer1Bit
									// .getHeight() / 1.5);
									dsts3.right = dsts3.left
											+ (int) (zoneWidth / 3);
									dsts3.bottom = dsts3.top
											+ (int) (zoneWidth / 3);
									canvas.drawBitmap(this.hammer2Bit, srcs3,
											dsts3, paint);

									Rect srcs4 = new Rect();// 图片
									srcs4.left = 0; // 0,0
									srcs4.top = 0;
									srcs4.right = this.palletBit.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
									srcs4.bottom = this.palletBit.getHeight();
									Rect dsts4 = new Rect();
									dsts4.left = zoneWidth * (mWidth + 1)
											- (int) (zoneWidth / 3)
									// * mWidth
									// + getResources()
									// .getDimensionPixelSize(
									// R.dimen.hayou_tiezi_pop_left)
									;
									dsts4.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3);
									// - (int) (this.palletBit.getHeight() /
									// 1.5);
									dsts4.right = dsts4.left
											+ (int) (zoneWidth / 3);
									dsts4.bottom = dsts4.top
											+ (int) (zoneWidth / 3);
									canvas.drawBitmap(this.palletBit, srcs4,
											dsts4, paint);

									Rect srcs5 = new Rect();// 图片
									srcs5.left = 0; // 0,0
									srcs5.top = 0;
									srcs5.right = this.astigmatismBit
											.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
									srcs5.bottom = this.astigmatismBit
											.getHeight();
									Rect dsts5 = new Rect();
									dsts5.left = zoneWidth * (mWidth + 1)
											- (int) (zoneWidth / 3)
									// getResources()
									// .getDimensionPixelSize(
									// R.dimen.hayou_tiezi_pop_left)
									;
									dsts5.top = zoneHeight * (nHeight + 1)
											- (int) (zoneWidth / 3);
									// - (int) (this.astigmatismBit
									// .getHeight() / 1.5);
									dsts5.right = dsts5.left
											+ (int) (zoneWidth / 3);
									dsts5.bottom = dsts5.top
											+ (int) (zoneWidth / 3);
									canvas.drawBitmap(this.astigmatismBit,
											srcs5, dsts5, paint);

								}
							}
						}
					}
				}
			}
		}
		for (int row = 0; row < 7; row++) {
			Rect srcs = new Rect();// 图片
			Rect dsts = new Rect();// 屏幕
			// src 这个是表示绘画图片的大小
			srcs.left = 0; // 0,0
			srcs.top = 0;
			srcs.right = this.mLandLine.getWidth();//
			// mBitDestTop.getWidth();//这个是桌面图的宽度，
			srcs.bottom = this.mLandLine.getHeight();//
			dsts.left = zoneWidth * (row + 1); //
			dsts.top = 0;
			dsts.right = zoneWidth * (row + 1) + this.mLandLine.getWidth();

			dsts.bottom = this.getHeight();

			canvas.drawBitmap(this.mLandLine, srcs, dsts, paint);
		}
		hammerIndex++;
		if (hammerIndex > 670) {
			hammerIndex = 0;
		}
		if (hammerIndex % 67 == 0) {
			Message ms = new Message();
			// ms.what = 5;
			// ms.obj = arrs;
			// this.viewHandler.sendMessage(ms);
		}
		// 画矩形
		canvas.save();
		Rect rect = new Rect(zoneWidth * 6, zoneHeight * 2, zoneWidth * 7,
				zoneHeight * 6);
		canvas.clipRect(rect);
		m1 -= this.distanceTranslate;
		m2 -= this.distanceTranslate;
		m3 -= this.distanceTranslate;
		rectY_1 -= this.distanceTranslate;
		rectY_2 -= this.distanceTranslate;

		rectY_3 -= this.distanceTranslate;
		if (rectY_1 > zoneHeight * 2) {
			canvas.drawBitmap(danamic1, rectX_1, rectY_1, paint);
			m1 = zoneHeight * 6;
		} else if (rectY_1 <= zoneHeight * 2
				&& rectY_1 >= zoneHeight * 2 - danamic1.getHeight()) {
			canvas.drawBitmap(danamic1, rectX_1, rectY_1
					- this.distanceTranslate, paint);
			canvas.drawBitmap(danamic1, rectX_1, m1, paint);
		} else if (rectY_1 < zoneHeight * 2 - danamic1.getHeight()
				&& m1 > zoneHeight * 2 + y_1) {
			canvas.drawBitmap(danamic1, rectX_1, m1, paint);

		} else if (m1 <= zoneHeight * 2 + y_1) {
			rectY_1 = m1;
			canvas.drawBitmap(danamic1, rectX_1, rectY_1, paint);
		}

		if (rectY_2 > zoneHeight * 2) {
			canvas.drawBitmap(danamic2, rectX_2, rectY_2, paint);
			m2 = zoneHeight * 6;
		} else if (rectY_2 >= zoneHeight * 2 - danamic2.getHeight()
				&& rectY_2 <= zoneHeight * 2) {
			canvas.drawBitmap(danamic2, rectX_2, rectY_2
					- this.distanceTranslate, paint);
			canvas.drawBitmap(danamic2, rectX_2, m2, paint);
		} else if (rectY_2 < zoneHeight * 2 - danamic2.getHeight()
				&& m2 > zoneHeight * 2 + zoneHeight * 4 / 3 + y_2) {
			canvas.drawBitmap(danamic2, rectX_2, m2, paint);

		} else if (m2 <= zoneHeight * 2 + zoneHeight * 4 / 3 + y_2) {
			rectY_2 = m2;
			canvas.drawBitmap(danamic2, rectX_2, rectY_2, paint);
		}

		if (rectY_3 > zoneHeight * 2) {
			canvas.drawBitmap(danamic3, rectX_3, rectY_3, paint);
			m3 = zoneHeight * 6;
		} else if (rectY_3 <= zoneHeight * 2
				&& rectY_3 >= zoneHeight * 2 - danamic3.getHeight()) {
			canvas.drawBitmap(danamic3, rectX_3, rectY_3
					- this.distanceTranslate, paint);
			canvas.drawBitmap(danamic3, rectX_3, m3, paint);
		} else if (rectY_3 < zoneHeight * 2 - danamic3.getHeight()
				&& m3 > zoneHeight * 2 + y_3 + zoneHeight * 4 / 3 * 2) {
			canvas.drawBitmap(danamic3, rectX_3, m3, paint);

		} else if (m3 <= zoneHeight * 2 + y_3 + zoneHeight * 4 / 3 * 2) {
			rectY_3 = m3;
			canvas.drawBitmap(danamic3, rectX_3, rectY_3, paint);
		}
		canvas.restore();
		this.invalidate();
	}

	/**
	 * 1,view第一次显示时 2,横坚屏切换
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		// this.mWidth = w;
		// this.mHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/** 处理单点、多点触摸 **/
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			first_x = (int) event.getX();
			first_y = (int) event.getY();
			intNum = first_x / zoneWidth + first_y / zoneHeight * 8 + 1;
			uid = "-1";
			if (userInfo != null) {
				if (userInfo.size() > 0) {
					for (UserIcon item : userInfo) {
						if ((intNum - 1) == Integer
								.parseInt(item.getPosition())) {
							locaNum = intNum;
							uid = item.getId();
							user = item;
						}
					}
				}
			}

			locaNum = -1;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			int lan_x = (int) event.getX();
			int lan_y = (int) event.getY();
			last_x = lan_x - first_x;
			last_y = lan_y - first_y;
			if (lan_x > zoneWidth * 8) {
				lan_x = 0;
			}
			locaNum = -1;
			othreId = "-1";
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			int location_x = (int) event.getX();
			int location_y = (int) event.getY();
			last_x = location_x - first_x;
			last_y = location_y - first_y;
			if (location_x > zoneWidth * 7) {
				location_x = zoneWidth * 7;
				last_x = 0;
			}
			// locaNum 为移动之后落下的位置
			locaNum = location_x / zoneWidth + location_y / zoneHeight * 8 + 1;
			while (locaNum > 64) {
				locaNum = locaNum - 8;
			}

			Log.i("info", "locaNum: " + locaNum);
			if (locaNum == 23 || locaNum == 31 || locaNum == 39
					|| locaNum == 47) {
				locaNum = intNum;
			}
			if (user != null) {

				if (locaNum == intNum && Integer.parseInt(uid) > 0) {
					// 单个收积分
					last_x = 0;
					last_y = 0;
					Message ms = new Message();
					ms.what = 5;
					ms.obj = uid;
					this.viewHandler.sendMessage(ms);
				}

				if (locaNum != intNum && locaNum >= 1) {
					// 检查落下的位置有没有人，如果有人则不移动，会到开始位置
					if (userInfo != null) {
						for (UserIcon item : userInfo) {
							if ((locaNum - 1) == Integer.parseInt(item
									.getPosition())) {
								othreId = item.getId();
								item.setPosition(String.valueOf(intNum - 1));

							}
						}

					}
					user.setPosition(String.valueOf(locaNum - 1));
					try {
						Message ms = new Message();
						JSONObject json = new JSONObject();
						JSONArray js = new JSONArray();
						js.put(uid);
						if (Integer.parseInt(othreId) > 0) {
							js.put(othreId);
							json.put("position", 0);
						} else {
							json.put("position", locaNum - 1);
						}
						json.put("uid", js);
						ms.what = 6;
						ms.obj = json;
						this.viewHandler.sendMessage(ms);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return true;
	}

	public void setHandler(Handler handler) {
		this.viewHandler = handler;
	}

	public void setData(HashMap<String, Bitmap> gameUserIconMap,
			List<UserIcon> userInfo) {
		this.gameUserIconMap = gameUserIconMap;
		this.userInfo = userInfo;
	}

	public void deteBitmap() {
		gameUserIconMap = null;
		userInfo = null;
	}
}
