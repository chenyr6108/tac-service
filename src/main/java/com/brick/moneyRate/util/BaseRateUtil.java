package com.brick.moneyRate.util;

import java.util.HashMap;
import java.util.Map;

import com.brick.service.core.DataAccessor;

/**
 * @author wujw
 * @date Jan 13, 2011
 * @version 
 */
public class BaseRateUtil {

	/**
	 * read rate value what you want
	 * @param column
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Double getBaseRate(String column) throws Exception {
		
		if (column == null || column.equals("")) {
			return null;
		}
		
		Map paramMap = new HashMap();
		paramMap.put("COLUMN", column);
		Double rs = (Double)DataAccessor.query("moneyRate.read-baserate-value", paramMap, DataAccessor.RS_TYPE.OBJECT);
		
		return rs;
	}
	
}
