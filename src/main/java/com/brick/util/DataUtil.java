package com.brick.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class DataUtil {
	public static final double crossinsure = 2430;
	public static final double cartype_big = 110;
	public static final double cartype_small = 111;

	/**
	 * 把字符串类型转换成double
	 * 
	 * @param str
	 * @return
	 */
	public static double doubleUtil(String str) {
		double d = 0d;
		if (str == null || str.equals("")) {
			d = 0d;
		} else {
			d = Double.parseDouble(str.replaceAll(",", "").replaceAll("￥", ""));
		}

		return d;
	}

	/**
	 * 把字符串类型转换成double
	 * 
	 * @param str
	 * @return
	 */
	public static double doubleUtil(Object obj) {
		String str = String.valueOf(obj == null ? "" : obj);
		double d = 0d;
		if (str == null || str.equals("")) {
			d = 0d;
		} else {
			d = Double.parseDouble(str.replaceAll(",", "").replaceAll("￥", ""));
		}

		return d;
	}

	/**
	 * 把字符串类型转换成int
	 * 
	 * @param str
	 * @return
	 */

	public static int intUtil(String str) {
		int temInd = 0;
		if (str == null || str.equals("")) {
			temInd = 0;
		} else {
			temInd = Integer.parseInt(str.replaceAll(",", "").replaceAll("￥", ""));
		}
		return temInd;
	}

	/**
	 * 把字符串类型转换成int
	 * 
	 * @param str
	 * @return
	 */

	public static int intUtil(Object obj) {
		String str = String.valueOf(obj == null ? "" : obj);
		int temInd = 0;
		if (str == null || str.equals("")) {
			temInd = 0;
		} else {
			temInd = Integer.parseInt(str.replaceAll(",", "").replaceAll("￥", ""));
		}
		return temInd;
	}
	/**
	 * 把字符串类型转换成float   kk
	 * 
	 * @param str
	 * @return
	 */
	
	public static float floatUtil(Object obj) {
		String str = String.valueOf(obj == null ? "" : obj);
		float temInd = 0;
		if (str == null || str.equals("")) {
			temInd = 0;
		} else {
			temInd = Float.parseFloat(str.replaceAll(",", "").replaceAll("￥", ""));
		}
		return temInd;
	}

	/**
	 * 把字符串类型转换成long
	 * 
	 * @param str
	 * @return
	 */

	public static long longUtil(String str) {
		long temLong = 0l;
		if (str == null || str.equals("")) {
			temLong = 0l;
		} else {
			temLong = Long.parseLong(str.replaceAll(",", "").replaceAll("￥", ""));
		}
		return temLong;
	}

	/**
	 * 把字符串类型转换成long
	 * 
	 * @param str
	 * @return
	 */

	public static long longUtil(Object obj) {
		String str = String.valueOf(obj == null ? "" : obj);
		long temLong = 0l;
		if (str == null || str.equals("")) {
			temLong = 0l;
		} else {
			temLong = Long.parseLong(str.replaceAll(",", "").replaceAll("￥", ""));
		}
		return temLong;
	}

	public static String formatdigital(double value) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		String aa = df.format(value);
		return aa;
	}

	public static double found2Pot(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		String aa = df.format(value);
		double f = Double.valueOf(aa);

		return f;
	}
	
	/**
	 * 日期格式化
	 * @param obj
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date dateUtil(Object obj, String pattern) throws ParseException {
		String source = String.valueOf(obj == null ? "" : obj);
		Date destDate = null;
		if (source != null && !source.equals("")) {
			destDate = new SimpleDateFormat(pattern).parse(source);
		} 
		return destDate;
	}
	/**
	 * 日期格式化
	 * @param obj
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date dateUtil(String source, String pattern) throws ParseException {

		Date destDate = null;
		if (source != null && !source.equals("")) {
			destDate = new SimpleDateFormat(pattern).parse(source);
		} 
		return destDate;
	}
	
	/**
	 * 把对象型转换成字符串
	 * @param obj
	 * @return
	 */
	public static String StringUtil(Object obj) {
		
		String str = String.valueOf(obj == null ? "" : obj);
		
		return str;
	}
	
	/**
	 * 日期格式化
	 * @param obj
	 * @param pattern
	 * @return
	 * @throws ParseException 
	 */
	public static String dateToStringUtil(Object obj, String pattern) throws ParseException{
		String dest = "";
		if (obj != null) {
			dest = new SimpleDateFormat(pattern).format((Date)obj);
		} 
		return dest;
	}

	public static void main(String[] args) {
		// double f = DataUtil.found2Pot(12.44743);
		// System.out.println("value=" + f);

		// // System.out.println("value2="+doubleToString(f));
		// double ff=112212345000.123;
		// System.out.println(cutpos(doubleToString(ff)));
		// String str="3451.123";
		// System.out.println(cutpos(str));
		// String aa=doubleToString(ff);
		// System.out.println(aa);
		String s = String.valueOf(null == null ? "" : null);
		System.out.println(s + "<");
	}
}
