package com.orfid.youxikuaile.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;

import com.orfid.youxikuaile.R;

public class URLDrawable extends BitmapDrawable {
	 public Drawable drawable;
	 
	 @SuppressWarnings("deprecation")
	 public URLDrawable(Context context)
	 {
	  this.setBounds(getDefaultImageBounds(context));
	        
	        drawable = context.getResources().getDrawable(R.drawable.ic_launcher);
	        drawable.setBounds(getDefaultImageBounds(context));
	 }
	    
	    @Override
	    public void draw(Canvas canvas) {
	        if (drawable != null) {
	            drawable.draw(canvas);
	        }
	    }
	    
	    @SuppressWarnings("deprecation")
	 public Rect getDefaultImageBounds(Context context) {
	        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
	        int width = display.getWidth();
	        int height = (int) (width * 3 / 4);
	        
	        Rect bounds = new Rect(0, 0, width, height);
	        return bounds;
	    }
	}
