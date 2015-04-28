package com.orfid.youxikuaile.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 自定义ImageView ,圆形imageview，实现imageview 圆形边框
 * 
 *  * @author clh
 * 
 */
public class CircularImageView extends ImageView {
	private int borderWidth = 5;
	private int viewWidth;
	private int viewHeight;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private BitmapShader shader;

	public CircularImageView(Context context) {
		super(context);
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setup() {
		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);
		paintBorder = new Paint();
		setBorderColor(0xb0CFD3DB);
		paintBorder.setAntiAlias(true);

		int APIVersion;
		APIVersion = Integer.valueOf(android.os.Build.VERSION.SDK);

		if (APIVersion >= 11) {
			this.setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
		}

		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// // 此处相当于布局文件中的Android:layout_gravity属性
		// lp.gravity = Gravity.CENTER;
		// this.setLayoutParams(lp);
		// paintBorder.setShadowLayer(2.0f, 0.0f, 2.0f, 0x80000000);
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.invalidate();
	}

	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);

		this.invalidate();
	}

	private void loadBitmap() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

		if (bitmapDrawable != null)
			image = bitmapDrawable.getBitmap();
		bitmapDrawable = null;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		// load the bitmap
		loadBitmap();
		// init shader
		if (image != null) {
			shader = new BitmapShader(Bitmap.createScaledBitmap(image,
					canvas.getWidth(), canvas.getHeight(), false),
					Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);
			int circleCenter = viewWidth / 2;

			// circleCenter is the x or y of the view's center
			// radius is the radius in pixels of the cirle to be drawn
			// paint contains the shader that will texture the shape
			canvas.drawCircle(circleCenter + borderWidth, circleCenter
					+ borderWidth, circleCenter + borderWidth - 4.0f,
					paintBorder);
			canvas.drawCircle(circleCenter + borderWidth, circleCenter
					+ borderWidth, circleCenter - 4.0f, paint);

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

		viewWidth = width - (borderWidth * 2);
		viewHeight = height - (borderWidth * 2);

		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = viewWidth;
		}

		return result;
	}

	private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = viewHeight;
		}

		return (result + 2);
	}
}
