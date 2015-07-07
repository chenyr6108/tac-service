package com.brick.credit.util;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.exception.ServiceException;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

public class VouchClassUtil {
	
	/**
	 * 获取某个案件的租金情况
	 * resultMap中：
	 * 1. loss_price是剩余总租金。
	 * 2. loss_own_price是剩余总本金。
	 * 3. loss_ren_price是剩余总利息。
	 * @param credit_id
	 * @return
	 * @throws ServiceException
	 */
	public static Map<String, Object> getRentInfoByCreditId(String credit_id) throws Exception{
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", credit_id);
		resultMap = (Map<String, Object>) DataAccessor.query("businessSupport.getRentInfo", paramMap, RS_TYPE.OBJECT);
		return resultMap;
	}
	
	/**
	 * 获取某个案件的租金情况
	 * resultMap中：
	 * 1. loss_price是剩余总租金。
	 * 2. loss_own_price是剩余总本金。
	 * 3. loss_ren_price是剩余总利息。
	 * @return
	 * @throws ServiceException 
	 */
	public static Map<String, Object> getRentInfoByCreditRuncode(String credit_runcode) throws Exception{
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_runcode", credit_runcode);
		resultMap = (Map<String, Object>) DataAccessor.query("businessSupport.getRentInfo", paramMap, RS_TYPE.OBJECT);
		return resultMap;
	}
	
}
