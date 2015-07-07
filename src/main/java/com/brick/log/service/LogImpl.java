package com.brick.log.service;

import org.apache.log4j.Logger;

import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;

/**
 * @author yangxuan
 * @version Created：2010-4-20 下午03:13:40
 *
 */

public class LogImpl implements Log{
	static Logger logger = Logger.getLogger(Log.class);
	
	public void doLog(Context context) {
		try {
			DataAccessor.execute("log.createLog", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}	
}
