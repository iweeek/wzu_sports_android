package com.tim.app.util;

import java.util.Date;

import org.joda.time.DateTime;

/**
* 日期工具类
*/
public class MyDateUtil {
	
	/**
	* 获取本周的开始日期
	*/
	public static Date getCurrentWeekStartDate() {
		
		DateTime now = new DateTime();
		if (now.getDayOfWeek() == 1) {
			return now.withMillisOfDay(0).toDate();
		} else {
			return now.withDayOfWeek(1)
					.withMillisOfDay(0)
					.toDate();
		}
	}
	
	/**
	* 获取本月的开始日期
	*/
	public static Date getCurrentMonthStartDate() {
		
		DateTime now = new DateTime();
		if (now.getDayOfMonth() == 1) {
			return now.withMillisOfDay(0).toDate();
		} else {
			return now.withDayOfMonth(1)
					.withMillisOfDay(0)
					.toDate();
		}
	}
	
	/**
	* 获取本年的开始日期
	*/
	public static Date getCurrentYearStartDate() {
		
		DateTime now = new DateTime();
		return now.withDayOfYear(1)
				.withMillisOfDay(0)
				.toDate();
	}
}
