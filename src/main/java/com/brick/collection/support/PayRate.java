package com.brick.collection.support;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.service.core.DataAccessor;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * @author wujw
 * @date Jun 8, 2010
 * @version
 */
public class PayRate {
	/** logger. */
	private static Log logger = LogFactory.getLog(PayRate.class);
	/** 半年. */
	public static final int HALF_YEAR = 6;
	/** 一年. */
	public static final int ONE_YEAR = 12;
	/** 三年. */
	public static final int THREE_YEAR = 36;
	/** 五年. */
	public static final int FIVE_YEAR = 60;

	/**
	 * 基准利率
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map getBaseRate() {
		Map rateMap = null;
		try {
			rateMap = (Map) DataAccessor.query("payRate.readBaseRate", null,
					DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return rateMap;
	}

	@SuppressWarnings("unchecked")
	public static Double getBaseRate(Integer totalMonth) {
		//租赁周期  12*3=36个月  并查找基准利率
		Map rateMap = null;
		try {
			rateMap = (Map) DataAccessor.query("payRate.readBaseRate", null,
					DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		if (totalMonth <= HALF_YEAR) {
			logger.info("HALF_YEAR");
			return DataUtil.doubleUtil(String
					.valueOf(rateMap.get("SIX_MONTHS")));
		} else if (totalMonth <= ONE_YEAR) {
			logger.info("ONE_YEAR");
			return DataUtil.doubleUtil(String.valueOf(rateMap.get("ONE_YEAR")));
		} else if (totalMonth <= THREE_YEAR) {
			logger.info("THREE_YEAR");
			return DataUtil.doubleUtil(String.valueOf(rateMap
					.get("ONE_THREE_YEARS")));
		} else if (totalMonth <= FIVE_YEAR) {
			logger.info("FIVE_YEAR");
			return DataUtil.doubleUtil(String.valueOf(rateMap
					.get("THREE_FIVE_YEARS")));
		} else {
			logger.info("MORE_THEN_FIVE_YEAR");
			return DataUtil.doubleUtil(String.valueOf(rateMap
					.get("OVER_FIVE_YEARS")));
		}
	}
}
