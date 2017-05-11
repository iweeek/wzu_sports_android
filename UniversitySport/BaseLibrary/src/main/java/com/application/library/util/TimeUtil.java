package com.application.library.util;

/**
 * 有关日期工具类(extends TimeUtil)
 * <p/>
 * TimeUtil主要功能有：
 * 1.各种日期类型（字符，util.Date，sql.Date，Calendar等）转换
 * 2.获取指定日期的年份，月份，日份，小时，分，秒，毫秒
 * 3.获取当前/系统日期(指定日期格式)
 * 4.获取字符日期一个月的天数
 * 5.获取指定月份的第一天,最后一天
 * <p/>
 * DateUtil主要功能有：
 * 1.日期比较
 * 2.获取2个字符日期的天数差，周数差，月数差，年数差
 * 3.日期添加
 * 4.判断给定日期是不是润年
 */


import android.content.Context;

import com.application.library.R;
import com.application.library.log.DLOG;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public abstract class TimeUtil {

    // ---当前日期的年，月，日，时，分，秒
    public static Calendar now = Calendar.getInstance();

    // int year = now.get(Calendar.YEAR);
    // int date = now.get(Calendar.DAY_OF_MONTH);
    // int month = now.get(Calendar.MONTH) + 1;
    // int hour = now.get(Calendar.HOUR);
    // int min = now.get(Calendar.MINUTE);
    // int sec = now.get(Calendar.SECOND);

    // -------------------------------日期类型转换---------------------------------------------------------------------------

    /**
     * 字符型日期转化util.Date型日期
     *
     * @param p_format 格式:"yyyy-MM-dd" / "yyyy-MM-dd HH:mm:ss"
     * @Param:p_strDate 字符型日期
     * @Return:java.util.Date util.Date型日期
     * @Throws: ParseException
     * @Date: 2006-10-31
     */
    public static Date toUtilDateFromStrDateByFormat(String p_strDate, String p_format) throws ParseException {
        Date l_date = null;
        java.text.DateFormat df = new SimpleDateFormat(p_format);
        if (p_strDate != null && p_strDate.length() > 0 && p_format != null && p_format.length() > 0) {
            l_date = df.parse(p_strDate);
        }
        return l_date;
    }

    /**
     * 字符型日期转化成sql.Date型日期
     *
     * @param p_strDate 字符型日期
     * @return java.sql.Date sql.Date型日期
     * @throws ParseException
     * @Date: 2006-10-31
     */
    public static java.sql.Date toSqlDateFromStrDate(String p_strDate) throws ParseException {
        java.sql.Date returnDate = null;
        java.text.DateFormat sdf = new SimpleDateFormat();
        if (p_strDate != null && p_strDate.length() > 0) {
            returnDate = new java.sql.Date(sdf.parse(p_strDate).getTime());
        }
        return returnDate;
    }

    /**
     * util.Date型日期转化指定格式的字符串型日期 toStrDateFromUtilDateByFormat(new Date(),"yyyy-MM-dd HH:mm:ss");
     *
     * @param p_utilDate Date
     * @param p_format   String 格式1:"yyyy-MM-dd" 格式2:"yyyy-MM-dd HH:mm:ss EE" 格式3:"yyyy年MM月dd日 hh:mm:ss EE" 说明: 年-月-日 时:分:秒 星期 注意MM/mm大小写
     * @return String
     * @Date: 2006-10-31
     */
    public static String toStrDateFromUtilDateByFormat(Date p_utilDate, String p_format) throws ParseException {
        String l_result = "";
        if (p_utilDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(p_format);
            l_result = sdf.format(p_utilDate);
        }
        return l_result;
    }

    /**
     * util.Date型日期转化转化成Calendar日期
     *
     * @param p_utilDate Date
     * @return Calendar
     * @Date: 2006-10-31
     */
    public static Calendar toCalendarFromUtilDate(Date p_utilDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_utilDate);
        return c;
    }

    /**
     * util.Date型日期转化sql.Date(年月日)型日期
     *
     * @Param: p_utilDate util.Date型日期
     * @Return: java.sql.Date sql.Date型日期
     * @Date: 2006-10-31
     */
    public static java.sql.Date toSqlDateFromUtilDate(Date p_utilDate) {
        java.sql.Date returnDate = null;
        if (p_utilDate != null) {
            returnDate = new java.sql.Date(p_utilDate.getTime());
        }
        return returnDate;
    }

    /**
     * util.Date型日期转化sql.Time(时分秒)型日期
     *
     * @Param: p_utilDate util.Date型日期
     * @Return: java.sql.Time sql.Time型日期
     * @Date: 2006-10-31
     */
    public static java.sql.Time toSqlTimeFromUtilDate(Date p_utilDate) {
        java.sql.Time returnDate = null;
        if (p_utilDate != null) {
            returnDate = new java.sql.Time(p_utilDate.getTime());
        }
        return returnDate;
    }

    /**
     * util.Date型日期转化sql.Date(时分秒)型日期
     *
     * @Param: p_utilDate util.Date型日期
     * @Return: java.sql.Timestamp sql.Timestamp型日期
     * @Date: 2006-10-31
     */
    public static java.sql.Timestamp toSqlTimestampFromUtilDate(Date p_utilDate) {
        java.sql.Timestamp returnDate = null;
        if (p_utilDate != null) {
            returnDate = new java.sql.Timestamp(p_utilDate.getTime());
        }
        return returnDate;
    }

    /**
     * sql.Date型日期转化util.Date型日期
     *
     * @Param: sqlDate sql.Date型日期
     * @Return: java.util.Date util.Date型日期
     * @Date: 2006-10-31
     */
    public static Date toUtilDateFromSqlDate(java.sql.Date p_sqlDate) {
        Date returnDate = null;
        if (p_sqlDate != null) {
            returnDate = new Date(p_sqlDate.getTime());
        }
        return returnDate;
    }

    // -----------------获取指定日期的年份，月份，日份，小时，分，秒，毫秒----------------------------

    /**
     * 获取指定日期的年份
     *
     * @param p_date util.Date日期
     * @return int 年份
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getYearOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取指定日期的月份
     *
     * @param p_date util.Date日期
     * @return int 月份
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getMonthOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定日期的日份
     *
     * @param p_date util.Date日期
     * @return int 日份
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getDayOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfYear(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 返回星期几
     *
     * @param p_date
     * @return
     */
    public static int getWeekOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        String s = "一";
        int ret = 1;
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                s = "日";
                ret = 7;
                break;
            case 2:
                s = "一";
                ret = 1;
                break;
            case 3:
                s = "二";
                ret = 2;
                break;
            case 4:
                s = "三";
                ret = 3;
                break;
            case 5:
                s = "四";
                ret = 4;
                break;
            case 6:
                s = "五";
                ret = 5;
                break;
            case 7:
                s = "六";
                ret = 6;
                break;
        }
        // return "星期" + s;
        return ret;
    }

    /**
     * 获取指定日期的小时
     *
     * @param p_date util.Date日期
     * @return int 日份
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getHourOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定日期的分钟
     *
     * @param p_date util.Date日期
     * @return int 分钟
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getMinuteOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取指定日期的秒钟
     *
     * @param p_date util.Date日期
     * @return int 秒钟
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static int getSecondOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param p_date util.Date日期
     * @return long 毫秒
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static long getMillisOfDate(Date p_date) {
        Calendar c = Calendar.getInstance();
        c.setTime(p_date);
        return c.getTimeInMillis();
    }

    // -----------------获取当前/系统日期(指定日期格式)-----------------------------------------------------------------------------------

    /**
     * 获取指定日期格式当前日期的字符型日期
     *
     * @param p_format 日期格式 格式1:"yyyy-MM-dd" 格式2:"yyyy-MM-dd HH:mm:ss EE" 格式3:"yyyy年MM月dd日 hh:mm:ss EE" 说明: 年-月-日 时:分:秒 星期 注意MM/mm大小写
     * @return String 当前时间字符串
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static String getNowOfDateByFormat(String p_format) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(p_format);
        String dateStr = sdf.format(d);
        return dateStr;
    }

    /**
     * 获取指定日期格式系统日期的字符型日期
     *
     * @param p_format 日期格式 格式1:"yyyy-MM-dd" 格式2:"yyyy-MM-dd HH:mm:ss EE" 格式3:"yyyy年MM月dd日 hh:mm:ss EE" 说明: 年-月-日 时:分:秒 星期 注意MM/mm大小写
     * @return String 系统时间字符串
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static String getSystemOfDateByFormat(String p_format) {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(p_format);
        String dateStr = sdf.format(d);
        return dateStr;
    }

    /**
     * 获取字符日期一个月的天数
     *
     * @param p_date
     * @return 天数
     * @author zhuqx
     */
    public static long getDayOfMonth(Date p_date) throws ParseException {
        int year = getYearOfDate(p_date);
        int month = getMonthOfDate(p_date) - 1;
        int day = getDayOfDate(p_date);
        int hour = getHourOfDate(p_date);
        int minute = getMinuteOfDate(p_date);
        int second = getSecondOfDate(p_date);
        Calendar l_calendar = new GregorianCalendar(year, month, day, hour, minute, second);
        return l_calendar.getActualMaximum(l_calendar.DAY_OF_MONTH);
    }

    // -----------------获取指定月份的第一天,最后一天
    // ---------------------------------------------------------------------------

    /**
     * 获取指定月份的第一天
     *
     * @param p_strDate 指定月份
     * @param p_format  日期格式
     * @return String 时间字符串
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static String getDateOfMonthBegin(String p_strDate, String p_format) throws ParseException {
        Date date = toUtilDateFromStrDateByFormat(p_strDate, p_format);
        return toStrDateFromUtilDateByFormat(date, "yyyy-MM") + "-01";
    }

    /**
     * 获取指定月份的最后一天
     *
     * @param p_strDate 指定月份
     * @param p_format  日期格式
     * @return String 时间字符串
     * @author zhuqx
     * @Date: 2006-10-31
     */
    public static String getDateOfMonthEnd(String p_strDate, String p_format) throws ParseException {
        Date date = toUtilDateFromStrDateByFormat(getDateOfMonthBegin(p_strDate, p_format), p_format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return toStrDateFromUtilDateByFormat(calendar.getTime(), p_format);
    }

    /**
     * 日期是否有效
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static boolean birIsGood(int year, int month, int day) {
        if (year < 1000 || month == 0 || month > 12 || day == 0 || day > 31) {
            return false;
        }
        return true;
    }

    /**
     * 格式化为，分：秒
     *
     * @param time
     * @param time 1 毫秒 2秒
     * @return
     */
    public static String formatTime(int time) {
        if (time != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(time / (60 * 1000)).append(":");
            int millinSeconds = time % (60 * 1000);
            int seconds = (millinSeconds / 1000);
            if (seconds < 10) {
                sb.append("0" + seconds);
            } else {
                sb.append(seconds);
            }
            return sb.toString();
        } else {
            // 未知
            return "0:00";
        }

    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sf.format(d);
    }

    /**
     * 格式化为,分秒钟
     *
     * @param time
     * @param time 1 毫秒 2秒
     * @return
     */
    public static String formatTime(long time) {
        if (time != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(time / (60 * 1000)).append(":");
            int millinSeconds = (int) (time % (60 * 1000));
            int seconds = (millinSeconds / 1000);
            if (seconds < 10) {
                sb.append("0" + seconds);
            } else {
                sb.append(seconds);
            }
            return sb.toString();
        } else {
            // 未知
            return "0:00";
        }

    }

    public static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

//        if (hours > 0) {
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                seconds).toString();
//        } else {
//            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
//                    .toString();
//        }
    }

    /**
     * 格式化为,时分秒钟
     *
     * @param time
     * @param time
     * @return
     */
    public static String formatHMSTime(int time) {
        if (time != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(time / (60 * 60));
            sb.append(":");
            int millinMinutes = (int) (time % (60 * 60));
            int minutes = millinMinutes / (60);
            if (minutes < 10) {
                sb.append("0" + minutes);
            } else {
                sb.append(minutes);
            }
            sb.append(":");
            int millinSeconds = (int) (time % (60));
            int seconds = (millinSeconds);
            if (seconds < 10) {
                sb.append("0" + seconds);
            } else {
                sb.append(seconds);
            }
            return sb.toString();
        } else {
            // 未知
            return "0:00";
        }

    }

    /**
     * Date类型转换为String类型
     *
     * @param date
     * @return
     */
    public static String formatDate(Context context, Date date) {
        StringBuilder d = new StringBuilder();
        d.append(date.getYear()).append(context.getString(R.string.timeutil_text_year)).append(date.getMonth())
                .append(context.getString(R.string.timeutil_text_month)).append(date.getDay())
                .append(context.getString(R.string.timeutil_text_day)).append(date.getHours())
                .append(context.getString(R.string.timeutil_text_hours)).append(date.getMinutes())
                .append(context.getString(R.string.timeutil_text_minute));
        return d.toString();
    }

    /**
     * int类型转换为String类型
     *
     * @param time
     * @return
     */
    public static String formatDate(Context context, long time) {
        long newTime = time;
        Date date = new Date();
        date.setTime(newTime);
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.pickimagedialog_text_year_moon_day));
        return sdf.format(date);
    }

    /**
     * 日期转换
     *
     * @param dataStr
     * @return
     */
    public static String transformDate(Context context, String dataStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            StringBuilder sb = new StringBuilder();
            Date now = new Date();
            Date date = sdf.parse(dataStr);
            long bewteenTime = now.getTime() - date.getTime();
            if (bewteenTime < 0) {
                return context.getString(R.string.timeutil_text_just);
            }

            if (bewteenTime < 1000 * 60 * 60 * 24 * 3) {
                if (bewteenTime < 1000 * 60)// 小于一分钟
                {
                    return sb.append(bewteenTime / 1000).append(context.getString(R.string.timeutil_text_before_second))
                            .toString();
                }
                if (bewteenTime < 1000 * 60 * 60)// 小于一小时
                {
                    return sb.append(bewteenTime / 1000 / 60).append(context.getString(R.string.timeutil_text_before_minute))
                            .toString();
                }
                if (bewteenTime < 1000 * 60 * 60 * 24 && now.getDay() == date.getDay()) {// 小于一天
                    int hour = (int) (bewteenTime / 1000 / 60 / 60);
                    int min = (int) (bewteenTime % (1000 * 60 * 60)) / 1000 / 60;
                    return sb.append(hour).append(context.getString(R.string.timeutil_text_xiaoshi)).append(min)
                            .append(context.getString(R.string.timeutil_text_before_minute)).toString();
                }
                if (now.getDay() - date.getDay() == 1) // 差一天
                {
                    sb.append(context.getString(R.string.timeutil_text_yesterday));
                    sb.append(date.getHours());
                    sb.append(context.getString(R.string.timeutil_text_hours));
                    return sb.toString();
                }
                if (now.getDay() - date.getDay() == 2) {
                    sb.append(context.getString(R.string.timeutil_text_before_yesterday));
                    sb.append(date.getHours());
                    sb.append(context.getString(R.string.timeutil_text_hours));
                    return sb.toString();
                }
                SimpleDateFormat sdf1 = new SimpleDateFormat(context.getString(R.string.timeutil_text_month_day_hours));
                return sdf1.format(date);
            } else {
                SimpleDateFormat sdf1 = new SimpleDateFormat(context.getString(R.string.pickimagedialog_text_year_moon_day));
                return sdf1.format(date);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return dataStr;
        }
    }

    /**
     * 日期转换
     *
     * @param timestamp
     * @return
     */
    public static String formatLoginTime(Context context, long timestamp) {
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        Date date = new Date(timestamp);
        long bewteenTime = now.getTime() - timestamp;
        if (bewteenTime < 0) {
            return context.getString(R.string.timeutil_text_just);
        }

        if (bewteenTime < 1000L * 60 * 60 * 24 * 30) {

            if (bewteenTime < 1000 * 60 * 10) // 小于10分钟
            {
                return sb.append(bewteenTime / 1000 / 60).append(context.getString(R.string.timeutil_text_before_minute))
                        .toString();
            }

            if (bewteenTime < 1000 * 60 * 30) // 小于30分钟
            {
                return sb.append(context.getString(R.string.timeutil_text_within_half_hour)).toString();
            }

            if (bewteenTime < 1000 * 60 * 60)// 小于一小时
            {
                return sb.append(context.getString(R.string.timeutil_text_half_hour_before)).toString();
            }

            if (bewteenTime < 1000L * 60 * 60 * 24 /* && getDayOfYear(now) == getDayOfYear(date) */) {// 小于一天
                int hour = (int) (bewteenTime / 1000 / 60 / 60);
                int min = (int) (bewteenTime % (1000 * 60 * 60)) / 1000 / 60;
                // return sb.append(hour).append("小时").append(min).append("分钟之前").toString();
                return sb.append(hour).append(context.getString(R.string.timeutil_text_hour_before)).toString();
            }

            // if (getDayOfYear(now) - getDayOfYear(date) < 7)
            if (bewteenTime < 1000L * 60 * 60 * 24 * 7) {
                // int day = getDayOfYear(now) - getDayOfYear(date);
                int day = (int) (bewteenTime / (1000L * 60 * 60 * 24));
                return sb.append(day).append(context.getString(R.string.timeutil_text_day_before)).toString();
            }

            if (now.getMonth() == date.getMonth()) {
                return sb.append(context.getString(R.string.timeutil_text_this_month)).toString();
            }

            return sb.append(context.getString(R.string.timeutil_text_within_one_month)).toString();
            // SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日HH时");
            // return sdf1.format(date);
        } else {
            // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
            // return sdf1.format(date);
            return context.getString(R.string.timeutil_text_going_mars);
        }

    }

    /**
     * 是否是同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isInSameDay(long time1, long time2) {
        Calendar dayOne = new GregorianCalendar();
        Calendar dayTwo = new GregorianCalendar();
        dayOne.setTime(new Date(time1));
        dayTwo.setTime(new Date(time2));
        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)
                && dayOne.get(Calendar.WEEK_OF_YEAR) == dayTwo.get(Calendar.WEEK_OF_YEAR)
                && dayOne.get(Calendar.DAY_OF_WEEK) == dayTwo.get(Calendar.DAY_OF_WEEK)) {
            return true;
        }
        return false;
    }

    public static String formatFeedTime(Context context, long timestamp) {
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        Date date = new Date(timestamp);
        long bewteenTime = now.getTime() - timestamp;
        if (now.getYear() == date.getYear()) {
            if (getDayOfYear(now) == getDayOfYear(date)) {
                if (bewteenTime < 1000 * 60)// 小于一分钟
                {
                    return sb.append(context.getString(R.string.timeutil_text_just)).toString();
                }

                if (bewteenTime < 1000 * 60 * 60)// 小于一小时
                {
                    return sb.append(bewteenTime / 1000 / 60).append(context.getString(R.string.timeutil_text_minutes_ago))
                            .toString();
                }

                int hour = (int) (bewteenTime / 1000 / 60 / 60);
                return sb.append(hour).append("小时前").toString();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.timeutil_text_month_day));
                return sdf.format(date);
            }
        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(context.getString(R.string.pickimagedialog_text_year_moon_day));
            return sdf1.format(date);
        }

    }

    /**
     * 显示赛事时间
     *
     * @param context
     * @param timestamp
     * @return
     */
    public static String formatMtachListTime(Context context, long timestamp) {
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        Date date = new Date(timestamp);

        if (now.getYear() == date.getYear()) {
            if (getDayOfYear(now) + 1 == getDayOfYear(date)) {
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.timeutil_text_tomorrow));
                return sdf.format(date);
            }
            if (getDayOfYear(now) == getDayOfYear(date)) {
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.timeutil_text_today));
                return sdf.format(date);
            }

            if (getDayOfYear(now) - getDayOfYear(date) == 1) // 差一天
            {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sb.append(context.getString(R.string.timeutil_text_yesterday)).toString();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            return sdf.format(date);
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
        return sdf1.format(date);

    }

    /**
     * 格式化赛事时间
     *
     * @param context
     * @param timestamp
     * @return
     */
    public static String formatMatchTime(Context context, long timestamp) {
        StringBuilder sb = new StringBuilder();
        Date now = new Date();
        Date date = new Date(timestamp);
        long bewteenTime = now.getTime() - timestamp;

        if (now.getYear() == date.getYear()) {
            if (getDayOfYear(now) == getDayOfYear(date)) {
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.timeutil_text_today_hh_mm));
                return sdf.format(date);
            }

            if (getDayOfYear(now) - getDayOfYear(date) == 1) // 差一天
            {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sb.append(context.getString(R.string.timeutil_text_yesterday)).append(" ").append(sdf.format(date)).toString();
            }
            if (getDayOfYear(now) - getDayOfYear(date) == -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                return sb.append(context.getString(R.string.timeutil_text_tomorrow)).append(" ").append(sdf.format(date))
                        .toString();
            }

            SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.timeutil_text_month_day_hour_mm));
            return sdf.format(date);
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat(context.getString(R.string.timeutil_text_month_day_hour_mm));
        return sdf1.format(date);

    }

    public static String formatChatTime(long ti) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long time = ti;
        Calendar now = new GregorianCalendar();
        Date date = new Date(time * 1000);
        if (sdf.format(now.getTime()).equals(sdf.format(date))) {
            sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        } else {
            sdf = new SimpleDateFormat("MM-dd");
            return sdf.format(date);
        }
    }

    public static String getLivePreviewDay(long ti) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long time = ti;
        Date date = new Date(time * 1000);
        return sdf.format(date);
    }

    public static String getLivePreviewTime(long ti) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        long time = ti;
        Date date = new Date(time * 1000);
        return sdf.format(date);
    }

    public static String formatChatCellTime(Context context, long ti) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar now = new GregorianCalendar();
        Date date = new Date(ti);

        SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
        if (ff.format(now.getTime()).equals(ff.format(date))) {
            sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        } else if (isSameWeek(ti)) {
            sdf = new SimpleDateFormat("HH:mm");
            Calendar ca = new GregorianCalendar();
            ca.setTime(date);
            String s = "一";
            switch (ca.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    s = context.getString(R.string.timeutil_text_day);
                    break;
                case 2:
                    s = context.getString(R.string.timeutil_text_monday);
                    break;
                case 3:
                    s = context.getString(R.string.timeutil_text_tuesday);
                    break;
                case 4:
                    s = context.getString(R.string.timeutil_text_wednesday);
                    break;
                case 5:
                    s = context.getString(R.string.timeutil_text_thursday);
                    break;
                case 6:
                    s = context.getString(R.string.timeutil_text_friday);
                    break;
                case 7:
                    s = context.getString(R.string.timeutil_text_saturday);
                    break;
            }
            return context.getString(R.string.timeutil_text_week) + s + " " + sdf.format(date);
        } else {
            return sdf.format(date);
        }
        // else
        // {
        // now.add(Calendar.DAY_OF_YEAR, -1);
        // if (sdf.format(now.getTime()).equals(sdf.format(date)))
        // {
        // sdf = new SimpleDateFormat("HH:mm");
        // return "昨天 " + sdf.format(date);
        // }
        // sdf = new SimpleDateFormat("MM-dd HH:mm");
        // return sdf.format(date);
        // }
    }

    /**
     * 判断是否和今天是同一周
     *
     * @param ti
     * @return
     */
    private static boolean isSameWeek(long ti) {
        Calendar now = new GregorianCalendar();
        Calendar other = new GregorianCalendar();
        other.setTime(new Date(ti));
        if (now.get(Calendar.YEAR) == other.get(Calendar.YEAR)
                && now.get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR)
                && other.get(Calendar.DAY_OF_WEEK) != 1) {
            return true;
        }
        return false;
    }

    /**
     * 格式化最近比赛的时间
     *
     * @param timestamp
     * @return
     */
    public static String formatRecentMatchTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String formatMatchTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 格式化签到时间
     *
     * @param timestamp
     * @return
     */
    public static String formatCheckTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 格式化圈子时间
     *
     * @param timestamp
     * @return
     */
    public static String formatCircleTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 格式化提现时间
     *
     * @param timestamp
     * @return
     */
    public static String formatCashInTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 首页赛事时间
     *
     * @param timestamp
     * @return
     */
    public static String formatHomepageMatchTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String getNowHHmmss() {
        String ret = "";
        try {
            ret = TimeUtil.toStrDateFromUtilDateByFormat(new Date(), "HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 格式化剩余时间
     *
     * @param seconds
     * @return
     */
    public static String formatLeftTime(Context context, int seconds) {
        if (seconds < 60) {
            return seconds + context.getString(R.string.timeutil_text_second);
        } else if (seconds < 60 * 60) {
            return seconds / 60 + context.getString(R.string.timeutil_text_fengzhong);
        } else if (seconds < 60 * 60 * 24) {
            return seconds / (60 * 60) + context.getString(R.string.timeutil_text_xiaoshi);
        } else {
            int hour = (seconds % (60 * 60 * 24)) / (60 * 60);
            if (hour > 0) {
                return seconds / (60 * 60 * 24) + context.getString(R.string.timeutil_text_tian) + hour
                        + context.getString(R.string.timeutil_text_xiaoshi);
            } else {
                return seconds / (60 * 60 * 24) + context.getString(R.string.timeutil_text_tian);
            }
        }
    }

    /**
     * 取星座
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String getXingzuo(Context context, String dateStr, String format) {
        Date date = null;
        try {
            date = toUtilDateFromStrDateByFormat(dateStr, format);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String star = "";
        if (month == 1 && day >= 20 || month == 2 && day <= 18) {
            star = context.getString(R.string.star_shuiping);
        }
        if (month == 2 && day >= 19 || month == 3 && day <= 20) {
            star = context.getString(R.string.star_shuangyu);
        }
        if (month == 3 && day >= 21 || month == 4 && day <= 19) {
            star = context.getString(R.string.star_baiyang);
        }
        if (month == 4 && day >= 20 || month == 5 && day <= 20) {
            star = context.getString(R.string.star_jinniu);
        }
        if (month == 5 && day >= 21 || month == 6 && day <= 21) {
            star = context.getString(R.string.star_shuangzi);
        }
        if (month == 6 && day >= 22 || month == 7 && day <= 22) {
            star = context.getString(R.string.star_juxie);
        }
        if (month == 7 && day >= 23 || month == 8 && day <= 22) {
            star = context.getString(R.string.star_shizi);
        }
        if (month == 8 && day >= 23 || month == 9 && day <= 22) {
            star = context.getString(R.string.star_chunv);
        }
        if (month == 9 && day >= 23 || month == 10 && day <= 22) {
            star = context.getString(R.string.star_tianping);
        }
        if (month == 10 && day >= 23 || month == 11 && day <= 21) {
            star = context.getString(R.string.star_tianxie);
        }
        if (month == 11 && day >= 22 || month == 12 && day <= 21) {
            star = context.getString(R.string.star_sheshou);
        }
        if (month == 12 && day >= 22 || month == 1 && day <= 19) {
            star = context.getString(R.string.star_mojie);
        }
        return star;
    }

    public static String generateTime(Context context, int totalSeconds) {

        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60;

        return String.format(Locale.US, "%02d’%02d”", minutes, seconds)
                .toString();
    }

    public static String generateMinuteTime(Context context, int totalSeconds) {

        int minutes = (totalSeconds / 60) % 60;

        return String.valueOf(minutes);
    }

    public static String formatTimeLine(long time, String p_format) {
        Date d = new Date();
        d.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat(p_format);
        String dateStr = sdf.format(d);
        return dateStr;
    }
}
