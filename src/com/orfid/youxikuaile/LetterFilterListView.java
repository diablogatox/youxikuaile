package com.orfid.youxikuaile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;

/**
 * 字母控件
*@author liuyinjun

* @date 2015-3-16
 */
public class LetterFilterListView extends RelativeLayout {

	/** The context. */
	private Context mContext;

	/** The section indexter. */
	private SectionIndexer mSectionIndexter = null;

	/** The list view. */
	private ListView mListView;

	/** The letter view. */
	private LetterView mLetterView;
	
	/** The selectedLetter view */
	private LetterSelectedView mLetterSelectedView;

	public LetterFilterListView(Context context) {
		this(context, null);
	}

	public LetterFilterListView(Context context, AttributeSet attrs) {
		this(context, null, 0);
	}

	public LetterFilterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int count = getChildCount();
		/** 必须包括一个子 View */
		if (count < 1) {
			throw new IllegalArgumentException("this layout must contain 1 child views,and AdapterView  must in the first position!");
		}
		View view = this.getChildAt(0);
		AdapterView<?> adapterView = null;
		if (view instanceof AdapterView<?>) {
			adapterView = (AdapterView<?>) view;
			mListView = (ListView) adapterView;
			mSectionIndexter = (SectionIndexer) mListView.getAdapter();
			
			/** 右边的字母显示 */
			mLetterView = new LetterView(mContext);
			mLetterView.setListView(mListView);
			mLetterView.setId(5000);//为了下面的控件布局
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.topMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.bottomMargin = 10;
			this.addView(mLetterView, layoutParams);

			/** 点击右边的字母后显示选中的字母 */
			mLetterSelectedView = new LetterSelectedView(mContext);
			RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams1.topMargin = 10;
			layoutParams1.rightMargin = 10;
			layoutParams1.bottomMargin = 10;
			layoutParams1.addRule(RelativeLayout.LEFT_OF, mLetterView.getId());
			this.addView(mLetterSelectedView, layoutParams1);
		}
		/** 必须包括一个子 AdapterView，一般就是 ListView */
		if (adapterView == null) {
			throw new IllegalArgumentException("must contain a AdapterView in this layout!");
		}
	}

	/**
	 * 右边字母显示 View
	 * 
	 *@author liuyinjun
	 *
	 * @date 2015-3-16
	 */
	private class LetterView extends View {

		/** The list. */
		private ListView mListView;

		/** The letter. */
		private char[] mLetter;

		/** The paint. */
		private Paint mPaint;

		/** The width center. */
		private float mWidthCenter;

		/** 字母之间的间距. */
		private float mSingleHeight;

		public LetterView(Context context) {
			this(context, null);
		}
		
		
		public LetterView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}
		
		
		public LetterView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}


		/**
		 * 初始化.
		 */
		private void init() {
			mLetter = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#' };
			mPaint = new Paint();
			mPaint.setColor(Color.parseColor("#949494"));
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			mPaint.setTextSize(22);
			mPaint.setAntiAlias(true);
			mPaint.setTextAlign(Paint.Align.CENTER);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			float height = getHeight();
			mSingleHeight = height / mLetter.length;
			mWidthCenter = getMeasuredWidth() / (float) 2;
			for (int i = 0; i < mLetter.length; i++) {
				canvas.drawText(String.valueOf(mLetter[i]), mWidthCenter, mSingleHeight + (i * mSingleHeight), mPaint);
			}

		}

		/**
		 * Gets the list view.
		 * 
		 * @return the list view
		 */
		public ListView getListView() {
			return mListView;
		}

		/**
		 * Sets the list view.
		 * 
		 * @param listView
		 *            the new list view
		 */
		public void setListView(ListView listView) {
			this.mListView = listView;
		}

		public boolean onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			int index = 0;//点击的位置在 mLetters 中的索引
			int i = (int) event.getY();
			int div = (int) mSingleHeight;
			/** 重新计算出索引 */
			if (div != 0) {
				index = i / div;
			}
			if (index >= mLetter.length) {
				index = mLetter.length - 1;
			} else if (index < 0) {
				index = 0;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mLetterSelectedView.setViewY(mSingleHeight + (index * mSingleHeight));//设置选中字母显示的Y轴坐标位置
				mLetterSelectedView.setSelectedLetter(mLetter[index]);//设置选中的字母
				if (mLetterSelectedView.getVisibility() == View.GONE)//若控件为隐藏状态，则显示
					mLetterSelectedView.setVisibility(View.VISIBLE);
				/** 显示1s后消失*/
				mLetterSelectedView.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLetterSelectedView.setVisibility(View.GONE);
					}
				}, 1000);
				
				if (mListView.getAdapter() != null) {
					ListAdapter listAdapter = (ListAdapter) mListView.getAdapter();
					if (mSectionIndexter == null) {
						mSectionIndexter = (SectionIndexer) listAdapter;
					}
					int position = mSectionIndexter.getPositionForSection(mLetter[index]);

					if (position == -1) {//列表中没有首字母为选中字母的的项
						return true;
					}
					mListView.setSelection(position);

				}
			}
			return true;
		}
	}

	/**
	 * 点击右边字母后显示的出的加大字母
	*@author liuyinjun
	
	* @date 2015-3-16
	 */
	private class LetterSelectedView extends View {

		private Paint mPaint;
		/** 选中的字母 */
		private char mLetter = 'A';

		/** 绘制字母的Y轴坐标*/
		private float mY;

		public LetterSelectedView(Context context) {
			this(context, null);

		}

		public LetterSelectedView(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public LetterSelectedView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}

		private void init() {
			mPaint = new Paint();
			mPaint.setColor(Color.parseColor("#ff55bb22"));
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			mPaint.setTextSize(35);
			mPaint.setAntiAlias(true);
			mPaint.setTextAlign(Paint.Align.CENTER);

		}


		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawText(String.valueOf(mLetter), getMeasuredWidth() / (float) 2, mY, mPaint);
		}

		public void setSelectedLetter(char letter) {
			this.mLetter = letter;
			this.invalidate();//刷新控件
		}


		public void setViewY(float y) {
			this.mY = y;
		}

	}
	
}
