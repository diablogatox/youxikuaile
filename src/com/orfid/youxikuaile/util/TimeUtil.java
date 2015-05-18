package com.orfid.youxikuaile.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeUtil {
	public static String getDate(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;
	}

	/**
	 * 计算两个日期型的时间相差多少时间
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static String twoDateDistance(String timestampString) {
		Long startDate = Long.parseLong(timestampString) * 1000;
		Long endDate = System.currentTimeMillis();
		if (startDate == null || endDate == null) {
			return null;
		}
		long timeLong = endDate - startDate;
		if (timeLong < 0) {
			return null;
		}
		if (timeLong < 60 * 1000)
			return timeLong / 1000 + "秒前";
		else if (timeLong < 60 * 60 * 1000) {
			timeLong = timeLong / 1000 / 60;
			return timeLong + "分钟前";
			// }
			// else if (timeLong < 60 * 60 * 24 * 1000) {
			// timeLong = timeLong / 60 / 60 / 1000;
			// return timeLong + "小时前";
			// } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
			// timeLong = timeLong / 1000 / 60 / 60 / 24;
			// return timeLong + "天前";
			// } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
			// timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
			// return timeLong + "周前";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			return sdf.format(new java.util.Date(startDate));
		}
	}

	public static String timeDistance(String newTime, String oldTime) {
		Long startDate = Long.parseLong(oldTime) * 1000;
		Long endDate = Long.parseLong(newTime) * 1000;
		if (startDate == null || endDate == null) {
			return null;
		}
		long timeLong = endDate - startDate;
		if (timeLong < 15 * 60 * 1000) {
			return "";
		} else {
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new java.util.Date(endDate));

			return date;

		}
	}
}
