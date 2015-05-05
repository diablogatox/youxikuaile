package com.orfid.youxikuaile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class TimeUtils {

	// 将字符串转为时间戳
	public static String getTimeString(String time) {

		String re_time = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Log.i("info", "time:" + time);
			time = time.trim();
			Date date = sdf.parse(time.trim());
			long timeL = date.getTime();
			timeL = timeL / 1000;
			String str = String.valueOf(timeL);
			re_time = str;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re_time;
	}

	public static String getStrTime(String cc_time) {
		// String re_StrTime = null;
		//
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// // 例如：cc_time=1291778220
		// long lcc_time = Long.valueOf(cc_time.trim());
		// re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		// // long startDate = Long.parseLong(cc_time);
		// // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// // sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		// // return sdf.format(new java.util.Date(startDate));
		// re_StrTime = re_StrTime.substring(0, 10);
		Log.i("info", "cc_time:" + cc_time);
		Long timestamp = Long.parseLong(cc_time) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
				.format(new java.util.Date(timestamp));
		return date;

	}
}
