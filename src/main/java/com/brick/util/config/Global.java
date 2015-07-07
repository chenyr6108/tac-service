package com.brick.util.config;

import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

/**
 * @author zxb
 *
 */
public class Global {
	private static Map map = new HashMap();
	
	public static Object get(String key){
		return map.get(key);
	}
	
	public static void put(String key, Object obj){
		map.put(key, obj);
	}
	
	public static DataSource getDataSource(String key){
		return (DataSource)map.get(key);
	}
	
}
