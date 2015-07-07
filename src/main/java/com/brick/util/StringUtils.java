package com.brick.util;
/**
 * 字符串常用操作的工具类
 * @author yangxuan
 * @since 1.0
 * @see http://www.google.com.hk
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


public final class StringUtils {
	private StringUtils() {};
	/**
	 * 匹配正规表达式的串
	 * english:regex
	 * chinese:regex pattern
	 * @param 
	 * 		regex
	 * @param 
	 * 		s
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static boolean regexLike(final String regex,final String s) {
		if (s == null)
			return false;
		Pattern pattern = Pattern.compile(regex);
		return pattern.matches(regex, s);
	}
	
	/**
	 * 得到GBK的字符编码
	 * string to gbk
	 * @param 
	 * 		str
	 * @return
	 */
	public static String toGBK(final String str){
		String gbk = "";
		if (str!=null){
			try {
				gbk = new String(str.getBytes("iso_8859_1"),"GBK");
			} catch (UnsupportedEncodingException e) {
				
			}
		}
		return gbk;
	}
	
	/**
	 * 从java.lang.String到java.lang.Integer
	 * string to int or Integer
	 * @param 
	 * 		str
	 * @return
	 */
	public static int str2int(final String str) {
		if (null == str || "".equals(str)) return 0;
		return Integer.parseInt(str);
	}
	
	/**
	 * 把一个本身为原子类型的double类型字符串转换成dobule类型
	 * string to double
	 * @param 
	 * 		str
	 * @return
	 */
	public static double str2double(final String str) {
		if (null == str || "".equals(str)) return 0.0d;
		return Double.parseDouble(str);
	}
	
	/**
	 * 把一个本身为原子类型的float类型字符串转换成float类型
	 * string to float
	 * @param 
	 * 		str
	 * @return
	 */
	public static float str2float(final String str) {
		if (null == str || "".equals(str)) return 0.0f;
		return Float.parseFloat(str);
	}
	
	/**
	 * 判断字符串是不是空串
	 * str == null || str.equals("") == true
	 * @param 
	 * 		str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null) || str.trim().equals("");
	} 
	
	public static boolean isEmpty(Object object) {
		boolean flag = false;
		if (object == null) {
			flag = true;
		} else if ("".equals(String.valueOf(object).trim())) {
			flag = true;
		}
		return flag;
	}
	
	/***
	 * 首先保证一个字符串不会出错，此方法应该在操作一个字符串之前调用
	 * 	to execute string,it must be frist call
	 * @param 
	 * 		str
	 * @return
	 */
	public static String getCorrectStr(String str) {
		return (str==null)?"":str;
	}
	
	/**Character hash of**/
	private static MessageDigest digest = null;
	/**
	 * 得到字符串的HashCode值
	 * Character hash of
	 * 
	 * @param data
	 *            String
	 * @return String
	 */
	public synchronized static final String hash(final String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("loading MD5 MessageDigest error!");
				nsae.printStackTrace();
			}
		}
		/**Hash value calculation**/
		digest.update(data.getBytes());
		return encodeHex(digest.digest());
	}

	/**
	 * 解析16进制成为一个字符串
	 * 16 hex numbers into characters
	 * 
	 * @param bytes
	 *            byte[]
	 * @return String
	 */
	public static final String encodeHex(final byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		int i;

		for (i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}
	
	/**
	 * 2012 12-13
	 * @author michael 
	 * @param str 需要进行插入换行符的字符串
	 * @param len 需要在间隔多少个汉字插入换行符
	 * @return  返回插入换行符的字符串
	 */
	public static String autoInsertWrap(String str,int len){
		str=str.replaceAll("<br>", "");
		StringBuffer strBuf=new StringBuffer(str);
		int i=((str.length()/len)*2+str.length())/len;
		for (int j=1;j<=i;j++){
			strBuf.insert((j*len)+((j-1)*2), "<br>");
		}
		return strBuf.toString();
	}
	
	/**
	 * 将阿拉伯数字转换成中文数字
	 * @param num 0-99的数字
	 * @return
	 */
	public static String numToChinese(int num){
		String[] nums = {"零","一","二","三","四","五","六","七","八","九","十"};
		String numChinese = "";
		if(String.valueOf(num).length() == 2){
			if(num / 10 > 1){
				numChinese = nums[num / 10];
			}
			numChinese += nums[10];
		}
		if(num % 10 > 0){
			numChinese += nums[num % 10];
		}
		return numChinese;
	}
	
	/**
	 * toString() 如果为null则返回""
	 * @param object
	 * @return
	 */
	public static String toStringOrEmpty(Object object){
		return object==null?"":object.toString();
	}
	
	/**
	 * Object2String
	 * @param object
	 * @return
	 */
	public static int ob2int(Object object){
		return object==null?0:StringUtils.str2int(object.toString());
	}
}
