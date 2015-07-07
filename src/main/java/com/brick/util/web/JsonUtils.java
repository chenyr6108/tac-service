package com.brick.util.web;
/**
 * json of result
 * to use with ajax framework
 * @author yangxuan 
 * @since 1.0
 */
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonUtils {
	/**default constructor**/
	private JsonUtils(){}
	private static org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory  
    .getLog(JsonUtils.class);
	/**
	 * jdk of object to json
	 * @param 
	 * 		obj
	 * @return String
	 */
	 public static String object2json(Object obj) {  
		    StringBuilder json = new StringBuilder();  
		    if (obj == null) {  
		      json.append("\"\"");  
		    } else if (obj instanceof String ||
		         obj instanceof Integer ||
		         obj instanceof Float  ||
		         obj instanceof Boolean ||
		         obj instanceof Short ||
		         obj instanceof Double || 
		         obj instanceof Long ||
		         obj instanceof BigDecimal ||
		         obj instanceof BigInteger || 
		         obj instanceof Byte) {  
		      json.append("\"").append(string2json(obj.toString())).append("\"");  
		    } else if (obj instanceof Object[]) {  
		      json.append(array2json((Object[]) obj));  
		    } else if (obj instanceof java.util.List) {  
		      json.append(list2json((java.util.List<?>) obj));  
		    } else if (obj instanceof java.util.Map) {  
		      json.append(map2json((java.util.Map<?, ?>) obj));  
		    } else if (obj instanceof java.util.Set) {  
		      json.append(set2json((java.util.Set<?>) obj));  
		    } else {  
		      json.append(bean2json(obj));  
		    }  
		    return json.toString();  
		  }  
		 
		   /**
		    * by java  reflection and introspection, transform bean to json
		    * @param 
		    * 		bean
		    * @return String
		    */
		  public static String bean2json(Object bean) {  
		    StringBuilder json = new StringBuilder();  
		    json.append("{");  
		    PropertyDescriptor[] props = null;  
		    try {  
		      props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();  
		    } catch (IntrospectionException e) {}  
		    if (props != null) {  
		      for (int i = 0; i < props.length; i++) {  
		        try {  
		          String name = object2json(props[i].getName());  
		          String value = object2json(props[i].getReadMethod().invoke(bean));  
		          json.append(name);  
		          json.append(":");  
		          json.append(value);  
		          json.append(",");  
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	logger.debug(e);
		        }  
		      }  
		      json.setCharAt(json.length() - 1, '}');  
		    } else {  
		      json.append("}");  
		    }  
		    return json.toString();  
		  }  
		 
		  /***
		   * jdk of list to json
		   * @param 
		   * 		java.util.List
		   * @return String
		   */
		  public static String list2json(java.util.List<?> list) {  
		    StringBuilder json = new StringBuilder();  
		    json.append("[");  
		    if (list != null && list.size() > 0) {  
		      for (Object obj : list) {  
		        json.append(object2json(obj));  
		        json.append(",");  
		      }  
		      json.setCharAt(json.length() - 1, ']');  
		    } else {  
		      json.append("]");  
		    }  
		    return json.toString();  
		  }  
		 
		  /***
		   * jdk of arrays to json
		   * @param 
		   * 		array
		   * @return String
		   */
		  public static String array2json(Object[] array) {  
		    StringBuilder json = new StringBuilder();  
		    json.append("[");  
		    if (array != null && array.length > 0) {  
		      for (Object obj : array) {  
		        json.append(object2json(obj));  
		        json.append(",");  
		      }  
		      json.setCharAt(json.length() - 1, ']');  
		    } else {  
		      json.append("]");  
		    }  
		    return json.toString();  
		  }  
		 
		  /***
		   * jdk of map to json
		   * @param 
		   * 		java.util.Map
		   * @return String
		   */
		  public static String map2json(java.util.Map<?, ?> map) {  
		    StringBuilder json = new StringBuilder();  
		    json.append("{");  
		    if (map != null && map.size() > 0) {  
		      for (Object key : map.keySet()) {  
		        json.append(object2json(key));  
		        json.append(":");  
		        json.append(object2json(map.get(key)));  
		        json.append(",");  
		      }  
		      json.setCharAt(json.length() - 1, '}');  
		    } else {  
		      json.append("}");  
		    }  
		    return json.toString();  
		  }  
		 
		  /***
		   * jdk of set to json
		   * @param 
		   * 	java.util.Set
		   * @return String
		   */
		  public static String set2json(java.util.Set<?> set) {  
		    StringBuilder json = new StringBuilder();  
		    json.append("[");  
		    if (set != null && set.size() > 0) {  
		      for (Object obj : set) {  
		        json.append(object2json(obj));  
		        json.append(",");  
		      }  
		      json.setCharAt(json.length() - 1, ']');  
		    } else {  
		      json.append("]");  
		    }  
		    return json.toString();  
		  }  
		 
		  /**
		   * jdk of string to json
		   * @param 
		   * 	java.lang.String
		   * @return String
		   */
		  public static String string2json(String s) {  
		    if (s == null)  
		      return "";  
		    StringBuilder sb = new StringBuilder();  
		    for (int i = 0; i < s.length(); i++) {  
		      char ch = s.charAt(i);  
		      switch (ch) {  
		      case '"':  
		        sb.append("\\\"");  
		        break;  
		      case '\\':  
		        sb.append("\\\\");  
		        break;  
		      case '\b':  
		        sb.append("\\b");  
		        break;  
		      case '\f':  
		        sb.append("\\f");  
		        break;  
		      case '\n':  
		        sb.append("\\n");  
		        break;  
		      case '\r':  
		        sb.append("\\r");  
		        break;  
		      case '\t':  
		        sb.append("\\t");  
		        break;  
		      case '/':  
		        sb.append("\\/");  
		        break;  
		      default:  
		        if (ch >= '\u0000' && ch <= '\u001F') {  
		          String ss = Integer.toHexString(ch);  
		          sb.append("\\u");  
		          for (int k = 0; k < 4 - ss.length(); k++) {  
		            sb.append('0');  
		          }  
		          sb.append(ss.toUpperCase());  
		        } else {  
		          sb.append(ch);  
		        }  
		      }  
		    }  
		    return sb.toString();  
		  }  
}
