package com.brick.base;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;

import taobe.tec.jcc.JChineseConvertor;

import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;

/**
 * 
 * @author yangyun
 *
 */
public class FormBeanComposer {
	
	Log logger = LogFactory.getLog(this.getClass());
	
	private Resource formBeanConfig;
	private static List<Element> formBeans;
	private static List<Element> actions;

	public Resource getFormBeanConfig() {
		return formBeanConfig;
	}

	public void setFormBeanConfig(Resource formBeanConfig) {
		this.formBeanConfig = formBeanConfig;
	}

	/**
	 * Load formBean.xml
	 * 读取formBean，action节点，
	 * 但不初始化。
	 * @throws Exception
	 */
	public void initFormBeans() throws Exception{
		if (formBeanConfig == null) {
			throw new Exception("formBeanConfig was not found.");
		}
		SAXReader reader = new SAXReader();
		Document document = reader.read(formBeanConfig.getInputStream());
		if (formBeans == null) {
			formBeans = document.selectNodes("//formBeans/formBean");
		}
		if (actions == null) {
			actions = document.selectNodes("//formBeans/action");
		}
	}
	
	/**
	 * 2012/03/23 Yang Yun
	 * 封装FormBean
	 * @param request
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> compose(HttpServletRequest request, String action) throws Exception {
		Map<String, Object> formBean = new HashMap<String, Object>();
		JChineseConvertor jcc = JChineseConvertor.getInstance();
		//查询action 有没有配置 formBean
		String formBeanName = null;
		for (Element a : actions) {
			if (a.attributeValue("path").equals(action)) {
				formBeanName = a.attributeValue("formBean");
			}
		}
		if (formBeanName == null) {
			return formBean;
		}
		//查询action 配置的 formBean 并初始化
		Element property = null;
		Object beanProperty = null;
		for (Element f : formBeans) {
			if (f.attributeValue("id").equals(formBeanName)) {
				for (Object o : f.elements("property")) {
					property = (Element) o;
					beanProperty = Class.forName(property.attributeValue("type")).newInstance();
					formBean.put(property.attributeValue("name"), beanProperty);
				}
			}
		}
		if (formBean.size() == 0) {
			return formBean;
		}
		//装载formBean
		String toName = null;
		String toProperty = null;
		String[] toInfo = null;
		Object entity = null;
		Method setMethod = null;
		Class<?> setMethodType = null;
		Object paramInvoke = null;
		String param = null;
		String[] paramValues = null;
		StringBuffer sb = null;
		Enumeration keySet = request.getParameterNames();
		String key = null;
		while(keySet.hasMoreElements()) {
			paramInvoke = null;
			toInfo = null;
			setMethod = null;
			param = null;
			paramValues = null;
			sb = null;
			//遍历所有带“.”的参数名的参数
			key = (String)keySet.nextElement();
			//System.out.println("-------------->> " + key);
			if (!key.equals(action) && key.contains(".")) {
				toInfo = key.split("\\.");
				if (toInfo.length == 2) {
					param = request.getParameter(key);
					if(request.getCharacterEncoding() == null){
						param = HTMLUtil.parseStrParam2(param, "");
					}
					//取消繁转简  
					//param = jcc.t2s(param);
					toName = toInfo[0];
					toProperty = toInfo[1];
					//System.out.println("=======================>>> " + toName + "." + toProperty + " = " + param);
					entity = formBean.get(toName);
					if (entity == null) {
						//logger.warn("找不到FormBean" + toName + "." + toProperty + " = " + param);
						continue;
					}
					//获取setter方法
					for (Method m : entity.getClass().getMethods()) {
						if (m.getName().equals(getSetMethod(toProperty))) {
							setMethod = m;
							continue;
						}
					}
					if (setMethod == null) {
						throw new Exception("No Setter method in class [ " + entity.getClass().getName() + "." + toProperty + " ]");
					}
					if (setMethod.getParameterTypes().length != 1) {
						throw new Exception("No Setter method in class [ " + entity.getClass().getName() + "." + toProperty + " ]");
					}
					setMethodType = setMethod.getParameterTypes()[0];
					try {
						//转换参数
						if (setMethodType.equals(String.class)) {
							paramValues = request.getParameterValues(key);
							if (paramValues != null && paramValues.length > 1) {
								sb = new StringBuffer();
								for (String string : paramValues) {
									sb.append(HTMLUtil.parseStrParam2(string, ""));
									sb.append(",");
								}
								paramInvoke = sb.substring(0, sb.length() - 1);
							} else {
								paramInvoke = param;
							}
						} else if (setMethodType.equals(Integer.class)) {
							paramInvoke = Integer.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(int.class)) {
							paramInvoke = Integer.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(Double.class)) {
							paramInvoke = Double.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(double.class)) {
							paramInvoke = Double.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(Float.class)) {
							paramInvoke = Float.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(float.class)) {
							paramInvoke = Float.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(Long.class)) {
							paramInvoke = Long.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						} else if (setMethodType.equals(long.class)) {
							paramInvoke = Long.valueOf(StringUtils.isEmpty(param) ? "0" : param);
						}  else if (setMethodType.equals(Boolean.class)) {
							paramInvoke = Boolean.valueOf(param);
						} else if (setMethodType.equals(boolean.class)) {
							paramInvoke = Boolean.valueOf(param);
						}else if (setMethodType.equals(Date.class)) {
							paramInvoke = new SimpleDateFormat("yyyy-MM-dd").parse(param);
						} else if (setMethodType.equals(java.sql.Date.class)) {
							paramInvoke = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(param).getTime());
						} else {
							throw new Exception("类型不匹配");
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					setMethod.invoke(entity, paramInvoke);
					formBean.put(toName, entity);
				}
			}
		}
		return formBean;
	}
	
	private String getSetMethod(String property){
		if (property != null && property.length() > 0) {
			return "set" + property.substring(0, 1).toUpperCase() + property.substring(1,property.length());
		} else {
			return null;
		}
	}
	
}
