package com.brick.util.web;


import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;

import taobe.tec.jcc.JChineseConvertor;

/**
 * @author zxb
 * @version 1
 * 
 */
public class HTMLUtil {
	
	/**
	 * 
	 * @param request
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public static String getStrParam(final HttpServletRequest request, final String paramName, final String defaultValue){
		return parseStrParam(request.getParameter(paramName), defaultValue);
	}
	
	public static int getIntParam(final HttpServletRequest request, final String paramName, final int defaultValue){
		return parseIntParam(request.getParameter(paramName), defaultValue);
	}
	
	public static boolean getBooleanParam(final HttpServletRequest request, final String paramName, boolean defaultValue){
		return request.getParameter(paramName)!=null?true:defaultValue;
	}
	
	public static float getFloatParam(final HttpServletRequest request, final String paramName, float defaultValue){
		return parseFloatParam(request.getParameter(paramName), defaultValue);
		
	}	
	public static Date getDateParam(final HttpServletRequest request, final String paramName, Date defaultValue){
		return parseDateParam(request.getParameter(paramName), defaultValue);
		
	}	
	public static Long getLongParam(final HttpServletRequest request, final String paramName, Long defaultValue){
		return parseLongParam(request.getParameter(paramName), defaultValue);
		
	}	
	public static Double getDoubleParam(final HttpServletRequest request, final String paramName, Double defaultValue){
		return  parseDoubleParam(request.getParameter(paramName), defaultValue);
		
	}	
	public static String[] getParameterValues(final HttpServletRequest request , final String paramName , String defaultValue) {
		
		String[] toStr = request.getParameterValues(paramName);
		
		if(toStr == null) {
			return new String[0];
		}
		if (request.getCharacterEncoding() == null) {
			for (int i=0 ; i<toStr.length; i++) {
				toStr[i] = parseStrParam2(toStr[i], defaultValue);
			}
		} else {
			for (int i=0 ; i<toStr.length; i++) {
				toStr[i] = parseStrParam(toStr[i], defaultValue);
			}
		}
		
		return toStr;
	
	}
	
	public static void fillMapByCookie(Map toMap, final HttpServletRequest request){
		Cookie[] cookie = request.getCookies();
		
		if(cookie==null){return;}
		
		for(int i=0; i<cookie.length; i++){
			toMap.put(cookie[i].getName(), cookie[i].getValue());
			
		}
	}		
	
	public static void fillMapByRequest(Map toMap, final HttpServletRequest request) throws IOException{
		JChineseConvertor jcc = JChineseConvertor.getInstance();
        if(request.getCharacterEncoding()==null){

    		Enumeration em = request.getParameterNames();	
    		while(em.hasMoreElements()){			
    			String key = (String)em.nextElement();		
    			//取消繁转简
    			//toMap.put(key, jcc.t2s(parseStrParam2(request.getParameter(key), "")));
    			toMap.put(key, parseStrParam2(request.getParameter(key), ""));
    		}
		}else{

			Enumeration em = request.getParameterNames();	
			while(em.hasMoreElements()){			
				String key = (String)em.nextElement();		
				//取消繁转简
				//toMap.put(key, jcc.t2s(parseStrParam(request.getParameter(key), "")));
				toMap.put(key, parseStrParam(request.getParameter(key), ""));
			}
		}
			
	}
	
	public static void fillMapBySession(Map toMap, final HttpServletRequest request){
		HttpSession session = request.getSession();
		Enumeration em = session.getAttributeNames();				
		while(em.hasMoreElements()){			
			String key = (String)em.nextElement();
			toMap.put(key, session.getAttribute(key));
		}
	}	
	
	/**
	 * 
	 * @param s
	 * @param defaultValue
	 * @return
	 */
	public static String parseStrParam(final String s, String defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{  
				return s.trim();
				//return (new String(s.getBytes("ISO-8859-1"),"utf-8")).trim();
			}catch(Exception e){
				return defaultValue;
			}
		}		
	}
	
public static String parseStrParam2(final String s, String defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{  
				//return s.trim();
				return (new String(s.getBytes("ISO-8859-1"),"utf-8")).trim();
			}catch(Exception e){
				return defaultValue;
			}
		}		
	}
	
	/**
	 * 
	 * @param s
	 * @param defalut
	 * @return
	 */
	public static int parseIntParam(final String s, final int defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{
				return Integer.parseInt(s);
			}catch(Exception e){
				
			}
		}	
		
		return defaultValue;
	}	
	
	public static Float parseFloatParam(final String s, final float defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{
				return Float.parseFloat(s);
			}catch(Exception e){
				
			}
		}	
		
		return defaultValue;
	}
	
	public static Date parseDateParam(final String s, final Date defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{
				return Date.valueOf(s);
			}catch(Exception e){
				
			}
		}	
		
		return defaultValue;
	}
	public static Long parseLongParam(final String s, final Long defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{
				return Long.parseLong(s.trim());
			}catch(Exception e){
				
			}
		}	
		
		return defaultValue;
	}
	public static Double parseDoubleParam(final String s, final Double defaultValue){
		
		if (s==null || s.trim().equals("")){
			return defaultValue;
		}else{
			try{
				return Double.parseDouble(s.trim());
			}catch(Exception e){
				
			}
		}	
		
		return defaultValue;
	}
	
	public static void convertBeanEncode(Object obj, final String defaultValue){

		PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(obj);
		
		try{
			for(int i = 1; i<pd.length; i++){
				if( String.class == pd[i].getPropertyType()){
					PropertyUtils.setProperty(obj, 
											pd[i].getName(), 
											parseStrParam((String)PropertyUtils.getProperty(obj, pd[i].getName()), defaultValue)
											);
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void fillByRequest(Object obj, final HttpServletRequest request){
		Enumeration em = request.getParameterNames();				
		while(em.hasMoreElements()){
			try{
				String paramName = (String)em.nextElement();
				PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(obj, paramName);
				Class cls = pd.getPropertyType();				
				
				if(String.class.equals(cls)){
					PropertyUtils.setProperty(obj, paramName, getStrParam(request, paramName, ""));
				}else if("int".equals(cls.getName())){
					PropertyUtils.setProperty(obj, paramName, new Integer(getIntParam(request, paramName, 0)));
				}else if("boolean".equals(cls.getName())){
					PropertyUtils.setProperty(obj, paramName, new Boolean(getBooleanParam(request, paramName, false)));
				}else if("float".equals(cls.getName())){
					PropertyUtils.setProperty(obj, paramName, new Float(getFloatParam(request, paramName, 0f)));
				}
			}catch(Exception e){
				
			}
		}
	}
	
	public static List pushMsg(List msgList, String msg ){
		if(msgList == null){
			msgList = new ArrayList();
		}
		return msgList;
	}
	

}
