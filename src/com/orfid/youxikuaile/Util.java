package com.orfid.youxikuaile;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}


	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}


	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}


	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static String showGender(String sex) {
        String gender = null;
        switch (Integer.parseInt(sex)) {
            case 0:
                break;
            case 1:
                gender = "男";
                break;
            case 2:
                gender = "女";
                break;
            default:
                break;
        }
        return gender;
    }

    public static int getAgeByTimestamp (long timestamp) {
        if (timestamp == 0) return 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int _year = cal.get(Calendar.YEAR);
        int _month = cal.get(Calendar.MONTH);
        int _day = cal.get(Calendar.DAY_OF_MONTH);

        GregorianCalendar cal2 = new GregorianCalendar();
        int y, m, d, a;

        y = cal2.get(Calendar.YEAR);
        m = cal2.get(Calendar.MONTH);
        d = cal2.get(Calendar.DAY_OF_MONTH);
        cal2.set(_year, _month, _day);
        a = y - cal2.get(Calendar.YEAR);
        if ((m < cal2.get(Calendar.MONTH))
                || ((m == cal2.get(Calendar.MONTH)) && (d < cal2
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            a = 0;
//                throw new IllegalArgumentException("Age < 0");
        return a;
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
