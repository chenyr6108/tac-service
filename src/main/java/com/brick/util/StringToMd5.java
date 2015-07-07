package com.brick.util;
/***
 * MD5 provides a template, personal feel more secure and efficient in this way some, 
 * it is just a reference. If you feel good 
 * please use the MD5 implementation of a teacher Zhu
 * @author yangxuan
 * @since 1.0
 */
import java.security.MessageDigest;
import com.google.common.collect.*;

public final class StringToMd5 {
	
	private static final ImmutableList<String> hexDigits = ImmutableList.of("0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f");
	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *           字节数组
	 * @return 16进制字串
	 */

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits.get(d1) + hexDigits.get(d2);
	}

	public static String MD5Encode(final String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.getCause().getMessage().toString();
		}
		return resultString;
	}
	
	public static void main(String args[]) {
		System.out.println(StringToMd5.MD5Encode("test"));
	}
}
