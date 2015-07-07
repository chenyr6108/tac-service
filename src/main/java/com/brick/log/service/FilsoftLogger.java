package com.brick.log.service;

/**
 * @author yangxuan
 * @version Created：2010-4-20 下午03:07:52
 *
 */

public class FilsoftLogger implements LoggerFactory {
	
	public Log getLog() {
		return new LogImpl();
	}

	

	
	
	
	

}
