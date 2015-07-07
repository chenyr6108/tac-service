package com.brick.util;

public class PasswordUtil {

	public static String resetPassword() {
		String[] pswdStr={"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"};
		
		int pswdLen=12;
		StringBuffer pswd=new StringBuffer();
		for(int i=0;i<pswdLen;i++) {
			int index=(int)(Math.random()*100.0);
			if(index>61) {
				index=(int)(Math.random()*10.0);
			}
			pswd.append(pswdStr[0].charAt(index));
		}

		return pswd.toString();
	}
}
