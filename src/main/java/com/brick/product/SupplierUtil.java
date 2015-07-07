package com.brick.product;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.credit.to.CreditTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.StringUtils;

public class SupplierUtil extends BaseService {
	
	/**
	 * 根据供应商名称，获取供应商交机前拨款授信额度
	 * 
	 * @author Yang Yun 2012/08/24
	 * @param suplName
	 * @return
	 * @throws Exception
	 */
	public static double getLineOfCredit(String suplName) throws Exception{
		double result = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = null;
		try {
			paramMap.put("SUPLNAME", suplName);
			resultMap = (Map) DataAccessor.query("rentContract.getSuplGrantMoneyBySupl",paramMap,DataAccessor.RS_TYPE.MAP);
			if (resultMap != null && resultMap.get("ADVANCEMACHINE_GRANT_PRICE") != null) {
				result = Double.parseDouble(resultMap.get("ADVANCEMACHINE_GRANT_PRICE").toString());
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 已经用掉的额度
	 * 
	 * @param suplName
	 * @return
	 * @throws Exception
	 */
	public static double getUsedCreditLimit(String suplName) throws Exception{
		double result = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = null;
		try {
			paramMap.put("SUPLNAME", suplName);
			resultMap = (Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl",paramMap,DataAccessor.RS_TYPE.MAP);
			if (resultMap != null && resultMap.get("TOTAL_APPRORIATEMON") != null) {
				result = Double.parseDouble(resultMap.get("TOTAL_APPRORIATEMON").toString());
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 剩余额度
	 * @param suplName
	 * @return
	 * @throws Exception
	 */
	public static double getRemainderLimit(String suplName) throws Exception{
		return getLineOfCredit(suplName) - getUsedCreditLimit(suplName);
	}
	
	/**
	 * 根据报告ID，取交机前拨款额度，及拨款对象（供应商）
	 * 
	 * @author Yang Yun 2012/08/24
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static CreditTo getInfoByBeforeDelivery(String creditId) throws SQLException{
		CreditTo credit = new CreditTo();
		credit.setCreditId(creditId);
		try {
			credit = (CreditTo) DataAccessor.getSession().queryForObject("riskAudit.getInfoByBeforeDelivery", credit);
		} catch (SQLException e) {
			throw e;
		}
		return credit;
	}
	
	public static boolean checkCreditLimit(String creditId) throws Exception{
		boolean flag = false;
		CreditTo credit = null;
		try {
			credit = getInfoByBeforeDelivery(creditId);
			if (credit == null) {
				throw new Exception("无交机前拨款。");
			}
			if (credit.getApproriateMoney() == 0) {
				throw new Exception("无交机前拨款。");
			}
			if (StringUtils.isEmpty(credit.getSuplName())) {
				throw new Exception("有交机前拨款，但是没有找到供应商。");
			}
			double remainderLimit = getRemainderLimit(credit.getSuplName());
			if (credit.getApproriateMoney() <= remainderLimit) {
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	
}
