package com.brick.service.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.brick.baseManage.service.BusinessLog;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

public abstract class AService {	
	
	private static Logger logger = Logger.getLogger(AService.class.getName());
//	public static Map serviceMap = new HashMap();
	
	Map methodMap = new HashMap();
	
	/*static{
		
		Document document = null;
		SAXReader reader = new SAXReader();
		
		try{
			document = reader.read(Resources.getResourceAsReader("config/service-config.xml"));
			
			//init result
			List<Element> serviceList = document.selectNodes("//root/service");
			
			for(Element el : serviceList){
				String eClass  = el.attributeValue("class");		
				AService service = (AService)Class.forName(eClass).newInstance();
				serviceMap.put(el.attributeValue("name"), service);
			}
			
		}catch(Exception e){
			logger.error("Can not read config file while start app. \n." + e.getMessage());
		}
		
	}*/
	


	/**
	 * 
	 * @param sqlId
	 * @param context
	 * @param rsType
	 * @param withJsonOut TODO
	 */
	public static void commonQuery(final String action, 
			Context context, 
			final DataAccessor.RS_TYPE rsType,
			final Output.OUTPUT_TYPE outputType){		
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		Object rs = null;		
		DataWrap dw = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			try{		
				
				if(DataAccessor.RS_TYPE.PAGED == rsType){
					dw = (DataWrap)DataAccessor.query(action, context.contextMap, rsType);
					outputMap.put("dw", dw);
				}else{
					rs = DataAccessor.query(action, context.contextMap, rsType);
					outputMap.put("rs", rs);
				}
				
			}catch(Exception e){
				errList.add("ϵͳִ�д���.");				
				logger.error("Query error by \n." + e.getMessage());
			}
		}				

		outputMap.put("errList", errList);
		
		if(Output.OUTPUT_TYPE.JSON == outputType)Output.jsonOutput(outputMap, context);

		
	}	

	/**
	 * 
	 * @param action
	 * @param context
	 * @param optType
	 * @param withJsonOut TODO
	 * @throws Exception 
	 */
	public static void commonExecute(final String action, 
			Context context, 
			final DataAccessor.OPERATION_TYPE optType,
			final Output.OUTPUT_TYPE outputType) throws Exception{
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		Object rs = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			try{				
				rs = DataAccessor.execute(action, context.contextMap, optType);	
				outputMap.put("rs", rs);
			}catch(Exception e){
				errList.add("ϵͳִ�д���.");				
				logger.error("Execute error by \n." + e.getMessage());
				throw e;
			}
		}		

		outputMap.put("errList", errList);		
		
		if(Output.OUTPUT_TYPE.JSON == outputType)Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 2011/12/31 Yang Yun
	 * 新增一条系统日志。
	 * 
	 */
	public void addSysLog(Long creditId, Long contractId,
			String logType, String logTitle, String logCode, String memo, Long user_id, String ip){
		BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, 
				logCode, memo, 1, user_id, null,ip);
	}
	
	/**
	 * 
	 * @param method
	 * @param context
	 * @throws Exception 
	 */
	public void doService(final String action, final String method, Context context) throws Exception {
		try{
			Method mth = null;
			
			//get it from map at first
			if((mth = (Method)methodMap.get(method))==null){
				Class cl = this.getClass();
				mth = cl.getMethod(method, new Class[] {Context.class});				
				methodMap.put(method, mth);
			}
			/* 2012-09-12 Yang Yun 暂停记录
			//记录开始时间
			Date startDate = new Date();
			*/
			//10 		
			if(!preExecute(action, context))return;
			
			
			//20 execute
			Object[] arg = new Object[1];
			arg[0] = context;
			try{
				mth.invoke(this, arg);
			} catch (InvocationTargetException e) {
				logger.error(e);
				Output.errorPageOutput(e.getTargetException(), context);
			} catch (Exception e) {
				logger.error(e);
				Output.errorPageOutput(e, context);
			}
			
			//40
			afterExecute(action, context);
			
			/* 2012-09-12 Yang Yun 暂停记录
			//记录结束时间
			Date endDate = new Date();
			*/
			/* 2012-09-12 Yang Yun 暂停记录
			try {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("startDate", startDate);
				paramMap.put("endDate", endDate);
				paramMap.put("timeConsuming", endDate.getTime() - startDate.getTime());
				paramMap.put("actionInfo", action);
				paramMap.put("loginName", context.contextMap.get("s_employeeName"));
				paramMap.put("loginIp", context.contextMap.get("IP"));
				DataAccessor.execute("businessSupport.addPerformanceLog", paramMap, OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				//记录效能日志报错，不影响正常运作。
				logger.warn(e.getMessage());
			}
			*/
		
		}catch(Exception e){
			throw e;
		}
	}
	
	
	/**
	 * 
	 * @param ap
	 * @param context
	 * @return
	 */
	protected boolean preExecute(final String action, Context context){
		return true;
	}
	protected void afterExecute(final String action, Context context){
	}
	
	public boolean validateToken(Context context){
		double s_token = (Double) context.request.getSession().getAttribute(
		"s_token");
		double token=Double.parseDouble((String) context.contextMap.get("token"));
		if (s_token ==token ) {
			return true;
		}else{
			return false;
		}
	}
	public void invalidToken(Context context){
		HttpSession session=context.request.getSession();
		session.setAttribute("s_token", 0.0);
	}

	
	
}
