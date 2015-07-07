package com.brick.base.util;

public class ClassUtil {
	public static String getSetMethod(String property){
		if (property != null && property.length() > 0) {
			return "set" + property.substring(0, 1).toUpperCase() + property.substring(1,property.length());
		} else {
			return null;
		}
	}
}
