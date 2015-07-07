package com.brick.util;
/***
 * 数字，百分比，税率的转换
 * number or percent,currency fromat
 * if exists other format pealse add in enum of DATE_TYPE
 * and update getDateFormat method
 * @author yangxuan
 * @since 1.0
 */
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public strictfp final class NumberUtils {
	public static enum DATE_TYPE {
					NULL,
					NUMBER,	
					PERCENT,
					CURRENCY
	};
	/**default constructor**/
	public NumberUtils(){}
	/***
	 * 
	 * 根据环境得到数字，百分比，税率指定格式的数据
	 * @param 
	 * 			obj:want to get value
	 * @param 
	 * 			java.util.Locale such as :Locale.GERMANY,
	 * 			Locale.US and so on
	 * @return
	 * 			String:result after transfrom
	 */
	public static String getDateFormat(Object obj,Locale inLocale,DATE_TYPE retType) {
		@SuppressWarnings("unused")
		NumberFormat numberFormat = null;
		switch (retType) {
		case NULL:
			return "";
		case NUMBER:
			 return getNumberFormat(obj, inLocale);
		case PERCENT:
			 return getPercentFormat(obj, inLocale);
		case CURRENCY:
			return getCurrencyFormat(obj, inLocale);
		default:
			throw new InvalidParameterException();
		}
	}
	
	/**
	 * 返回数字，百分比，税率默认本地数据
	 * get default format number
	 * @param 
	 * 		obj
	 * @return
	 */
	public static String getDateDefaultFormat(Object obj) {
		return getDateFormat(obj, null, NumberUtils.DATE_TYPE.NULL);
	}
	
	/**
	 * get percent of date
	 * @param 
	 * 		obj
	 * @param 
	 * 		inLocale
	 * @return
	 */
	public static String getPercentFormat(Object obj,Locale inLocale) {
		NumberFormat format = null;
		if (obj == null) throw new InvalidParameterException();
		 if (inLocale == null) {
			format = NumberFormat.getPercentInstance();
			return format.format(obj);
		} else {
			format = NumberFormat.getPercentInstance(inLocale);
			return format.format(obj);
		}
	}
	/**
	 * get decimal of number date
	 * @param 
	 * 		obj
	 * @param 
	 * 		inLocale
	 * @return
	 */
	
	public static String getNumberFormat(Object obj,Locale inLocale) {
		NumberFormat format = null;
		if (obj == null) throw new InvalidParameterException();
		if (inLocale == null) {
			format = NumberFormat.getInstance();
			return format.format(obj);
		} else {
			format = NumberFormat.getInstance(inLocale);
			return format.format(obj);
		}
	}
	
	/**
	 * get currency of date
	 * @param 
	 * 		obj
	 * @param 
	 * 		inLocale
	 * @return
	 */
	
	public static String getCurrencyFormat(Object obj,Locale inLocale) {
		NumberFormat format = null;
		 if (obj == null) throw new InvalidParameterException();
		 if (inLocale == null) {
			format = NumberFormat.getCurrencyInstance();
			return format.format(obj);
		} else {
			format = NumberFormat.getCurrencyInstance(inLocale);
			return format.format(obj);
		}
	}
	
	/**
	 * To retain two of the rounded
	 * such as: 234.235
	 * output:234.24
	 * @param 
	 * 		value
	 * @return
	 */
	public static double retain2rounded(double value) {
	       DecimalFormat df = new DecimalFormat("0.00");
	       String aa = df.format(value);
	       return Double.valueOf(aa);
	}
	
	/***
	 * General digital format
	 * such as :111111111110.123 
	 * output:111,111,111,111,0.123
	 * @param value
	 * @return
	 */
	public static String formatdigital(double value) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df.format(value);
    }
	/**
	 * 保留3为小数
	 * @param value
	 * @return
	 */
	public static double retain3rounded(double value) {
		DecimalFormat df = new DecimalFormat("0.000");
		String aa = df.format(value);
		return Double.valueOf(aa);
	}
	

}
