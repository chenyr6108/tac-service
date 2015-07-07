package com.brick.log.service;

import org.apache.commons.logging.Log;


/**
 * @author qijianglong
 * @version Created：2011-4-15 下午01:12
 *
 */
public class LogPrint 
{
	/**
	 * 输出错误日志中堆栈信息
	 * @param dataType
	 * @return 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void getLogStackTrace(Exception e,Log logger)
	{
		logger.error(e.getMessage());
		for(int i=0; i <(e.getStackTrace()).length; i++)   { 
			logger.error("\tat "+e.getStackTrace()[i]); 
		} 
		
	
	}
}
