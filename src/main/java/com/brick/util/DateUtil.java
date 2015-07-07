package com.brick.util;
/**
 * 注意：可以直接通过joda-time提供的API来操作日期类型和JDK完全兼容
 * @author yangxuan
 * @since 1.0
 */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
	private static Logger logger = Logger.getLogger(DateUtil.class.getName());

	/**default constructor**/
	private DateUtil() {};

	/**
	 * chinese:字符到指定格式日期的转换,
	 * English:assign fromt date
	 * 		   such as:yyyy-MM-dd HH4:mm:ss
	 * @param 
	 * 		日期字符串：dateStr
	 * @return 
	 * 		resultVal
	 */
	public static Date strToDate(String dateStr, final String type) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(type);
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return date;
	}

	/**
	 * chinese:从字符串到日期和小时的转换
	 * english:
	 * 		String to date and Hour
	 * 		format:hour add minutes
	 * @param 
	 * 		日期字符串：dateStr
	 * @return
	 */
	public static Date strToDateHour(final String dateStr) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error(e);
		}
		return date;
	}

	/**
	 * chinese:日期到字符串
	 * english:
	 * 		date to String
	 * @param 
	 * 		日期：date
	 * @param 
	 * 		字符：str
	 * @return
	 */
	public static String dateToString(Date date, final String format) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 转换某个java.util.Date到java.lang.String
	 * default format:yyyy-MM-dd
	 * date convertor to String
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		if (null == date)
			return null;
		String str = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(str);
		return sdf.format(date);
	}

	/**
	 * 从java.util.lang 到java.util.Date
	 * @param dateStr
	 * @return
	 */
	public static Date strToDay(final String dateStr) {
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error(e.getCause().getMessage());
		}
		return date;
	}
	
	/**
	 * 得到当前天的下一天
	 *  get next day!
	 * @param Date
	 * @return
	 */
	public static Date getNextDay(final String Date) {
		Calendar calObj = Calendar.getInstance();
		SimpleDateFormat sfObj = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calObj.setTime(sfObj.parse(Date));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		calObj.add(Calendar.DAY_OF_YEAR, 1);
		return strToDay(sfObj.format(calObj.getTime()));
	}

	/**
	 * 
	 * input date and return str
	 * 
	 * @return yyyy-mm-dd
	 */

	public static String dateToStr(Date date) {
		if (null == date) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuffer dateStr = new StringBuffer();
		dateStr.append(calendar.get(Calendar.YEAR));
		dateStr.append("-");
		if (calendar.get(Calendar.MONTH) < 9) {
			dateStr.append("0");
		}
		dateStr.append(calendar.get(Calendar.MONTH) + 1);
		dateStr.append("-");
		dateStr.append(calendar.get(Calendar.DAY_OF_MONTH));
		return dateStr.toString();
	}
	
	/**
	 * 得到日期的完整字符串
	 * return data of full string
	 * @param date
	 * @return
	 */
	public static String dateToFullStr(Date date) {
		if (null == date)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 判断一个时间段是否与事实相符，after时间应该在before时间之后
	 * 
	 * @param before
	 *           前面的日期
	 * @param after
	 *            后面的日期
	 * @return true代表时间段是合法的，false代表时间段是不合法的
	 */

	public static boolean dateValidate(Date before, Date after) {
		if (before.before(after))
			return true;
		return false;
	}

	/**
	 * 判断两个时间段是否有冲突
	 * 
	 * @param firstStart
	 *           第一个时间段的开始日期
	 * @param firstEnd
	 *            第一个时间段的结束日期
	 * @param secondStart
	 *           第二个时间段的开始日期
	 * @param secondEnd
	 *            第二个时间段的结束日期
	 * @return 是否冲突， true代表不冲突 false代表冲突
	 */
	public static boolean dateConflict(Date firstStart, Date firstEnd, Date secondStart, Date secondEnd) {
		if (dateValidate(firstEnd, secondStart) || dateValidate(secondEnd, firstStart))
			return true;
		return false;
	}
	/***
	 * 得到当前的时间 
	 * get current time
	 * @return
	 */
	public static String getNowTime() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String weekday = "星期";
		int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayofweek == 1) {
			weekday += "日";
		} else if (dayofweek == 2) {
			weekday += "一";
		} else if (dayofweek == 3) {
			weekday += "二";
		} else if (dayofweek == 4) {
			weekday += "三";
		} else if (dayofweek == 5) {
			weekday += "四";
		} else if (dayofweek == 6) {
			weekday += "五";
		} else if (dayofweek == 7) {
			weekday += "六";
		}

		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		String now = df.format(date);

		return "" + now + "  " + weekday;
	}

	/**
	 * 根据所给Date值,返回该时间所在星期的第一天
	 * @param	
	 * 		date
	 */
	public static Date getWeekStart(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
		calendar.roll(Calendar.DAY_OF_YEAR, -(dayOfWeek - 2));
		return calendar.getTime();
	}

	/**
	 * 根据所给Date值,返回该时间所在星期的最后一天
	 * @param	
	 * 		date
	 */
	public static Date getWeekEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.roll(Calendar.DAY_OF_YEAR, (8 - dayOfWeek));
		return calendar.getTime();
	}

	/**
	 * 根据所给DateStr值,返回该时间所在星期的第一天
	 * 
	 * @param dateStr
	 *            yyyy-mm-dd
	 */
	public static Date getWeekStart(String dateStr) {
		Date date = DateUtil.strToDay(dateStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
		calendar.roll(Calendar.DAY_OF_YEAR, -(dayOfWeek - 2));
		return calendar.getTime();
	}

	/**
	 * 根据所给DateStr值,返回该时间所在星期的最后一天
	 * 
	 * @param dateStr
	 *            yyyy-mm-dd
	 */
	public static Date getWeekEnd(String dateStr) {
		Date date = DateUtil.strToDay(dateStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.roll(Calendar.DAY_OF_YEAR, (8 - dayOfWeek));
		return calendar.getTime();
	}

	/**
	 * 用来判断某个日期是否双休(周六周日)
	 * @param
	 * 		 year
	 * @param 
	 * 		 month
	 * @param 
	 * 		 date
	 * @return
	 */
	public static boolean isHoliday(int year,int month,int date){
		Calendar now = Calendar.getInstance();
		now.set(year, month - 1, date);
		int current = now.get(Calendar.DAY_OF_WEEK);
		if(current == Calendar.SUNDAY || current == Calendar.SATURDAY) return true;
		return false;
	}
	/**
	 * 参数为calendar当前时间 返回YYYY-MM格式
	 * @param
	 * 		calendar
	 */
	public static String getYearMonth(final Calendar calendar) {
		String tjDate = calendar.get(Calendar.YEAR) + "-";
		if (calendar.get(Calendar.MONTH) < 9)
			tjDate = tjDate + "0" + (calendar.get(Calendar.MONTH) + 1);
		else
			tjDate = tjDate + (calendar.get(Calendar.MONTH) + 1);
		return tjDate;
	}
	
	public static  java.sql.Timestamp string2TimeStamp(final String str) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设定格式
		//dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
		dateFormat.setLenient(false);
		java.util.Date timeDate;
		try {
			timeDate = dateFormat.parse(str.trim().toString());
			java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());//Timestamp类型,timeDate.getTime()返回一个long型
			return dateTime;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Date parseDateWithMillisecond(String dateStr) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		return sdf.parse(dateStr);
	}
	
	public static String formatDateWithMillisecond(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		return sdf.format(date);
	}
	
	public static String getCurrentYear(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		return sdf.format(new Date());
	}
	
	public static String getCurrentMonth(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		return sdf.format(new Date());
	}
	
	public static String getCurrentDay(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return sdf.format(new Date());
	}
	
	public static String getLastMonth(){
		String currentMonth = getCurrentMonth();
		Integer currentMonthInt = Integer.parseInt(currentMonth);
		int lastMonth = currentMonthInt == 1 ? 12 : (currentMonthInt - 1);
		return String.valueOf(lastMonth);
	}
	
	public static String getLastYearByMonth(String currentMonth) {
		Integer currentMonthInt = Integer.parseInt(currentMonth);
		String currentYear = getCurrentYear();
		int currentYearInt = Integer.parseInt(currentYear);
		int lastYear = currentMonthInt == 1 ? (currentYearInt - 1) : currentYearInt;
		return String.valueOf(lastYear);
	}
	
	public static String getCurrentDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	public static String getYesterday(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		return sdf.format(c.getTime());
	}
	
	public static String getTomorrow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, 1);
		return sdf.format(c.getTime());
	}
	
	public static String getCurrentYearMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		return sdf.format(new Date());
	}
	
	/**
	 * 获取某天所在月份最后一天日期
	 * @param date yyy-MM-dd
	 * @return
	 */
	public static String getLastDayOfMonth(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MONTH, 1);//下个月
		calendar.set(Calendar.DATE, 1);//1号
		calendar.add(Calendar.DATE, -1);//前一天
		return sdf.format(calendar.getTime()) + " 23:59:59";
	}
	
	/**
	 * 获取某天所在月份第一天日期
	 * @param date yyy-MM-dd
	 * @return
	 */
	public static String getFirstDayOfMonth(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.MONTH, 0);//所传月份
		calendar.set(Calendar.DATE, 1);//1号
		return sdf.format(calendar.getTime()) + " 00:00:00";
	}

}
