package com.brick.base.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import com.brick.base.to.BaseTo;
import com.brick.base.to.CreditLineTO;
import com.brick.base.to.GuiHuInfo;
import com.brick.base.to.LinkManTo;
import com.brick.base.to.SelectionTo;

import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.util.Constants;
import com.brick.util.StringUtils;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

public class LeaseUtil {
	
	public static enum CREDIT_LINE_TYPE {UNION, BUY_BACK, PAY_BEFORE, INVOICE};
	
	private static SqlMapClient sqlMap;
	
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	//报告 -- Yang Yun
	//-----------------------------------------------
    
	//报告生成日
	public static Date getProjectCreateDate(String creditId) throws SQLException{
		Date createDate = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		createDate = (Date) sqlMap.queryForObject("leaseUtil.getCreateDate", paramMap);
		return createDate;
	}
	//报告第一次提交日
	public static Date getFirstCommitTime(String creditId) throws SQLException{
		Date createDate = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		createDate = (Date) sqlMap.queryForObject("leaseUtil.getFirstCommitTime", paramMap);
		return createDate;
	}
	
	//报告号案件号相互查询
	public static String getCreditIdByRunCode(String runCode) throws SQLException{
		String creditId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("runCode", runCode);
		creditId = (String) sqlMap.queryForObject("leaseUtil.getCreditIdByRunCode", paramMap);
		return creditId;
	}
	public static String getRunCodeByCreditId(String creditId) throws SQLException{
		String runCode = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		runCode = (String) sqlMap.queryForObject("leaseUtil.getRunCodeByCreditId", paramMap);
		return runCode;
	}
	
	public static Integer getGroupInsideByCreditId(String creditId) throws SQLException{
		Integer runCode = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		runCode = (Integer) sqlMap.queryForObject("leaseUtil.getGroupInsideByCreditId", paramMap);
		return runCode;
	}
	
	public static boolean isImportEqipByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		Integer flag = (Integer) sqlMap.queryForObject("leaseUtil.getImportEqipByCreditId", paramMap);
		if(flag!=null && flag==1){
			return true;
		}
		return false;
	}
	//合同号查ID
	public static String getCreditIdByLeaseCode(String leaseCode) throws SQLException{
		String creditId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("leaseCode", leaseCode);
		creditId = (String) sqlMap.queryForObject("leaseUtil.getCreditIdByLeaseCode", paramMap);
		return creditId;
	}
	
	public static String getCreditIdByRiskId(String riskId) throws SQLException{
		String creditId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("riskId", riskId);
		creditId = (String) sqlMap.queryForObject("leaseUtil.getCreditIdByRiskId", paramMap);
		return creditId;
	}
	
	public static int getPayWayByCreditId(String creditId) throws SQLException{

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getPayWayByCreditId", paramMap);

	}
	//报告担保人
	public static int getCreditNatuByName(String credit_id, String natu_name) throws SQLException{
		int i = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", credit_id);
		paramMap.put("natu_name", natu_name);
		i = (Integer) sqlMap.queryForObject("leaseUtil.getCreditNatuByName", paramMap);
		return i;
	}
	
	public static List<Map<String, String>> getGuarantorByCreditId(String creditId) throws SQLException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", creditId);
		List<Map<String, String>> data = (List<Map<String, String>>) sqlMap.queryForList("leaseUtil.getGuarByCreditId", paramMap);
		return data;
	}
	
	public static List<SelectionTo> getAllGuarantor() throws SQLException {
		List<SelectionTo> data = (List<SelectionTo>) sqlMap.queryForList("report.getAllGuarantor");
		return data;
	}
	
	
	//获取报告服务课人员ID
	public static String getServiceUserIdByCreditId(String creditId) throws SQLException{
		String userid = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		userid = (String) sqlMap.queryForObject("leaseUtil.getServiceUserIdByCreditId", paramMap);
		return userid;
	}
	//获取锁码CODE
	public static String getLockCodeByCreditId(String creditId) throws SQLException{
		String lockCode = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		lockCode = (String) sqlMap.queryForObject("leaseUtil.getLockCodeByCreditId", paramMap);
		return lockCode;
	}
	
	public static List<BaseTo> getCreditMemoByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return sqlMap.queryForList("leaseUtil.getCreditMemoByCreditId", paramMap);
	}
	//报告供应商
	public static String getSuplNameByCreditId(String creditId) throws SQLException{
		String suplName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		suplName = (String) sqlMap.queryForObject("leaseUtil.getSuplNameByCreditId", paramMap);
		return suplName;
	}
	//报告供应商ID
	public static String getSuplIdByCreditId(String creditId) throws SQLException{
		String suplId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		suplId = (String) sqlMap.queryForObject("leaseUtil.getSuplIdByCreditId", paramMap);
		return suplId;
	}
	//报告制造商
	public static String getManufacturerByCreditId(String creditId) throws SQLException{
		String suplName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		suplName = (String) sqlMap.queryForObject("leaseUtil.getManufacturerByCreditId", paramMap);
		return suplName;
	}
	
	
	public static Date getFirstPeriodPayDateByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Date) sqlMap.queryForObject("leaseUtil.getFirstPeriodPayDateByCreditId", paramMap);
	}
	
	public static Date getLastPeriodPayDateByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Date) sqlMap.queryForObject("leaseUtil.getLastPeriodPayDateByCreditId", paramMap);
	}
	/**
	 * 供应商的所有已核准报告ID
	 */
	public static List<String> getCreditIdBySuplId(String suplId) throws SQLException{
		List<String> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suplId", suplId);
		resultList = sqlMap.queryForList("leaseUtil.getCreditIdBySuplId", paramMap);
		return resultList;
	}
	
	//客户的所有已核准报告ID
	public static List<String> getCreditIdByCustId(String custId) throws SQLException{
		List<String> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", custId);
		resultList = sqlMap.queryForList("leaseUtil.getCreditIdByCustId", paramMap);
		return resultList == null ? new ArrayList<String>() : resultList;
	}
	
	//担保人的所有已核准报告ID
	public static List<String> getCreditIdByGuarantor(String guarantor) throws SQLException{
		List<String> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("guarantor", guarantor);
		resultList = sqlMap.queryForList("leaseUtil.getCreditIdByGuarantor", paramMap);
		return resultList == null ? new ArrayList<String>() : resultList;
	}
	
	//报告承租人
	public static String getCustNameByCreditId(String creditId) throws SQLException{
		String custName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		custName = (String) sqlMap.queryForObject("leaseUtil.getCustNameByCreditId", paramMap);
		return custName;
	}
	//报告承租人Id
	
	public static String getCustIdByCreditId(String creditId) throws SQLException{
		String custId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		custId = (String) sqlMap.queryForObject("leaseUtil.getCustIdByCreditId", paramMap);
		return custId;
	}
	//首拨款日
	public static Date getFinancecontractDate(String creditId) throws SQLException{
		Date financecontractDate = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		financecontractDate = (Date) sqlMap.queryForObject("leaseUtil.getFinancecontractDate", paramMap);
		return financecontractDate;
	}
	
	//拨款记录，次数
	public static Integer getPayCountByCreditId(String creditId) throws SQLException{
		int i = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		i = (Integer) sqlMap.queryForObject("leaseUtil.getPayCountByCreditId", paramMap);
		return i;
	}
	
	//报告合同号
	public static String getLeaseCodeByCreditId(String creditId) throws SQLException{
		String leaseCode = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		leaseCode = (String) sqlMap.queryForObject("leaseUtil.getLeaseCodeByCreditId", paramMap);
		return leaseCode;
	}
	public static int getCompanyCodeByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getCompanyCodeByCreditId", paramMap);
	}
	
	//报告的业务经办人
	public static String getSensorIdByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getSensorIdByCreditId", paramMap);
		return result;
	}
	//原始经办人
	public static String getOrgSensorIdByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getOrgSensorIdByCreditId", paramMap);
		return result;
	}
	//报告的类别（新品回租、重车回租、乘用车回租、一般回租）
	public static String getContractTypeByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getContractTypeByCreditId", paramMap);
		return result;
	}
	public static String getContractTypeDescByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getContractTypeDescByCreditId", paramMap);
		return result;
	}
	public static List<SelectionTo> getAllContractType() throws SQLException{
		List<SelectionTo> result = null;
		result = (List<SelectionTo>) sqlMap.queryForList("leaseUtil.getAllContractType");
		return result;
	}
	public static List<SelectionTo> getExistingContractType() throws SQLException{
		List<SelectionTo> result = null;
		result = (List<SelectionTo>) sqlMap.queryForList("leaseUtil.getExistingContractType");
		return result;
	}
	
	//业种别
	public static List<SelectionTo> getProductionType() throws SQLException{
		List<SelectionTo> result = null;
		result = (List<SelectionTo>) sqlMap.queryForList("leaseUtil.getProductionType");
		return result;
	}
	
	//报告查询办事处
	public static String getDecpIdByCreditId(String creditId) throws SQLException{
		String decpId = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		decpId = (String) sqlMap.queryForObject("leaseUtil.getDecpIdByCreditId", paramMap);
		return decpId;
	}
	public static String getDecpNameByCreditId(String creditId) throws SQLException{
		String decpName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		decpName = (String) sqlMap.queryForObject("leaseUtil.getDecpNameByCreditId", paramMap);
		return decpName;
	}
	/**
	 * 根据报告ID 获取开户行名称
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static String getBankNameByCreditId(String creditId) throws SQLException{
		String bankName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		bankName = (String) sqlMap.queryForObject("leaseUtil.getBankNameByCreditId", paramMap);
		return bankName;
	}
	public static String getBankAccountByCreditId(String creditId) throws SQLException{
		String bankAccount = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		bankAccount = (String) sqlMap.queryForObject("leaseUtil.getBankAccountByCreditId", paramMap);
		return bankAccount;
	}
	
	
	/**
	 * @author zhangbo
	 * @param 根据案件号得到利差
	 * @param creditId
	 * @return double
	 * @throws Exception 
	 */
	public static double getDiffByCreditId(String creditId) throws Exception{
		Map<String,Object> schemaMap=new HashMap<String,Object>();
		Map<String,Object> contextMap=new HashMap<String,Object>();
		contextMap.put("credit_id", creditId);
		double RATE_DIFF=0.0d;
			schemaMap = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",contextMap, DataAccessor.RS_TYPE.MAP);
			if(schemaMap!=null){
				String diff =schemaMap.get("RATE_DIFF")==null?"0":String.valueOf(schemaMap.get("RATE_DIFF"));
			    RATE_DIFF=Double.parseDouble(diff);
		    }
		return RATE_DIFF;
	}
	//报告拨款额度
	public static Double getPayMoneyByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		//LEASE_TOPRIC - PLEDGE_ENTER_MCTOAG - PLEDGE_ENTER_AG
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPayMoneyByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//实际已拨款金额
	public static double getRealPayMoneyByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		d = (Double) sqlMap.queryForObject("leaseUtil.getRealPayMoneyByCreditId", paramMap);
		return d == null ? 0 : d;
	}
	
	//实际拨款中的金额
	public static double getRealPayMoneyInAuthByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		d = (Double) sqlMap.queryForObject("leaseUtil.getRealPayMoneyInAuthByCreditId", paramMap);
		return d == null ? 0 : d;
	}
	
	public static boolean hasPayBefore(String creditId) throws SQLException{
		boolean flag = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		Integer result = (Integer) sqlMap.queryForObject("leaseUtil.hasPayBefore", paramMap);
		if (result != null && result > 0) {
			flag = true;
		}
		return flag;
	}
	
	//报告设备总价款
	public static Double getTotalPriceByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getTotalPriceByCreditId", paramMap);
		d = decimal == null ? 0 : decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//合同设备总价款
	public static Double getTotalContractPriceByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getTotalContractPriceByCreditId", paramMap);
		d = decimal == null ? 0 : decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//报告交机前拨款的（金额）
	public static Double getPayMoneyByCreditIdForBefore(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPayBeforeMoney", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//报告的保证金A（平均抵冲）、B（用于抵冲最后几期）、C（期末返还）
	//总保证金
	public static Double getPledgePriceByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	//平均冲抵
	public static Double getPledgePriceForAvgByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceForAvgByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//最后抵充
	public static Double getPledgePriceForLastByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceForLastByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
		
	//最后抵充期数
	public static Integer getPledgePeriodForLastByCreditId(String creditId) throws SQLException{
		Integer period = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		period = (Integer) sqlMap.queryForObject("leaseUtil.getPledgePeriodForLastByCreditId", paramMap);
		return period;
	}
	
	//期末返还保证金
	public static Double getPledgePriceForBackByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceForBackByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//保证金入供应商(PLEDGE_ENTER_AG)
	public static Double getPledgePriceFor2SuplByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceFor2SuplByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//保证金我司入供应商
	public static Double getPledgePriceForOur2SuplByCreditId(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPledgePriceForOur2SuplByCreditId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//实际剩余本金
	public static Double getRemainingPrincipalByCreditId(String creditId) throws SQLException{
		Double result = 0D;
		String recpId = getRecpIdByCreditId(creditId);
		//判断是否过期
		if (isValidCredit(creditId)) {
			//判断是否有支付表
			if (StringUtils.isEmpty(recpId)) {
				//没有支付表
				result = getTotalPriceByCreditId(creditId) - getPledgePriceForAvgByCreditId(creditId);
			} else {
				result = getRemainingPrincipalByRecpId(recpId);
			}
		}
		return result;
	}
	
	/**
	 * 客户归户（实际剩余本金）
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static Double getRemainingPrincipalByCustId(String custId) throws Exception{
		Double result = 0D;
		List<String> credits = LeaseUtil.getCreditIdByCustId(custId);
		for (String credit : credits) {
			result += LeaseUtil.getRemainingPrincipalByCreditId(credit);
		}
		return result;
	}
	
	/**
	 * 客户归户（总租金）
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static Double getTotalRentMoneyByCustId(String custId) throws Exception{
		Double result = 0D;
		List<String> credits = LeaseUtil.getCreditIdByCustId(custId);
		for (String credit : credits) {
			result += LeaseUtil.getTotalRental(credit);
		}
		return result;
	}
	
	/**
	 * 客户归户（剩余租金）
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static Double getRemainingRentMoneyByCustId(String custId) throws Exception{
		Double result = 0D;
		List<String> credits = LeaseUtil.getCreditIdByCustId(custId);
		for (String credit : credits) {
			result += LeaseUtil.getRemainingRental(credit);
		}
		return result;
	}
	
	/**
	 * 总本金 = 设备总价款 - 保证金A
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static Double getTotalPrincipalByCreditId(String creditId) throws SQLException{
		Double result = 0D;
		//有效合同，和已拨款合同
		if (isValidCredit(creditId) || !StringUtils.isEmpty(getFinancecontractDate(creditId))) {
			result = getTotalPriceByCreditId(creditId) - getPledgePriceForAvgByCreditId(creditId);
		}
		return result;
	}
	
	
	/**
	 * 案件是否有效
	 * true：有效；false：过期。
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static boolean isValidCredit(String creditId) throws SQLException{
		boolean b = false;
		Integer i = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		i = (Integer) sqlMap.queryForObject("leaseUtil.getInvalidCredit", paramMap);
		if (i <= 0) {
			b = true;
		}
		return b;
	}
	
	//实际剩余本金
	public static Double getRemainingPrincipalByRectId(String rectId) throws SQLException{
		String recpId = getRecpIdByRectId(rectId);
		return getRemainingPrincipalByRecpId(recpId);
	}
	
	//实际剩余本金
	public static Double getRemainingPrincipalByRecpId(String recpId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("recpId", recpId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getRemainingPrincipal", paramMap);
		d = decimal == null ? 0 : (decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		return d;
	}
	
	public static String getRectIdByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getRectIdByCreditId", paramMap);
		return result;
	}
	
	public static String getRecpIdByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getRecpIdByCreditId", paramMap);
		return result;
	}
	
	public static String getRecpIdByRectId(String rectId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rectId", rectId);
		result = (String) sqlMap.queryForObject("leaseUtil.getRecpIdByRectId", paramMap);
		return result;
	}
	
	
	//报告的TR
	public static Double getTRByCreditId(String creditId) throws SQLException{
		Double result = 0D;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (Double) sqlMap.queryForObject("leaseUtil.getTRByCreditId", paramMap);
		return result;
	}
	
	//报告的概算成本
	public static Double getLeaseRzeByCreditId(String creditId) throws SQLException{
		Double result = 0D;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (Double) sqlMap.queryForObject("leaseUtil.getLeaseRzeByCreditId", paramMap);
		return result;
	}
	
	//专案
	public static String getSpecialCodeByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getSpecialCodeByCreditId", paramMap);
		return result;
	}
	//管理费
	//租赁物放置地
	public static String getEqupmentAddressByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getEqupmentAddressByCreditId", paramMap);
		return result;
	}
	//承租人注册地址
	public static String getCompanyAddressByCreditId(String creditId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (String) sqlMap.queryForObject("leaseUtil.getCompanyAddressByCreditId", paramMap);
		return result;
	}
	//期数
	public static int getPeriodsByCreditId(String creditId) throws SQLException{
		int i = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getPeriodsByCreditId", paramMap);
		i = decimal.intValue();
		return i;
	}
	
	
	public static String getTaxPlanCodeByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (String) sqlMap.queryForObject("leaseUtil.getTaxPlanCodeByCreditId",paramMap);
	}
	
	public static Integer getCreditIdByPayId(String payId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("payId", payId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getCreditIdByPayId",paramMap);
	}
	
	public static double getBankCharge(String creditId) throws SQLException{
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Double) sqlMap.queryForObject("leaseUtil.getBankChargeByCreditId",paramMap);
		
	}
	//访厂 -- Yang Yun
	//-----------------------------------------------
	//实际访厂时间
	//访厂报告生成时间
	//访厂人员
	//是否需要访厂（true false）
	//访厂报告的结果（可申请（免补）可申请（补） 婉拒 ）
	//访厂进度（、、）

	
	//评审 -- Yang Yun
	//-----------------------------------------------
	//评审进度
	//提交风控时间第一次
	//提交风控时间最后一次
	//评审人员（初、1、2、3、4级）
	//权限别（ 1、2、3、4）
	//结案时间
	//案件最终评审结果（、、、）
	//共案String[]
	//共案风控金额加总
	//评分表
	public static String getScoreCardByContractType(String contractType) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("contractType", contractType);
		result = (String) sqlMap.queryForObject("leaseUtil.getScoreCardByContractType", paramMap);
		return result;
	}
	
	//评等
	public static String getScoreLevelByScore(int score) {
		String result = "";
		if(score <= 16){
			result = "R6";
		} else if (score <= 22) {
			result = "R5";
		} else if (score <= 28) {
			result = "R4";
		} else if (score <= 34) {
			result = "R3";
		} else if (score <= 40) {
			result = "R2";
		} else if (score <= 46) {
			result = "R1";
		}
		return result;
	}
	
	//案件评分
	public static int getScoreByCreditId(String creditId) throws SQLException{
		Integer result;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		result = (Integer) sqlMap.queryForObject("leaseUtil.getScoreByCreditId", paramMap);
		return (result == null ? 0 : result);
	}
	
	
	//合同 --ShenQi
	//-----------------------------------------------
	//合同号
	public static String getLeaseCodeByRectId(Long rectId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rectId", rectId);
		result = (String) sqlMap.queryForObject("leaseUtil.getLeaseCodeByRectId", paramMap);
		return result;
	}
	
	public static int getProductionTypeByRectId(long rectId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rectId", rectId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getProductionTypeByRectId", paramMap);
	}
	public static int getProductionTypeByCreditId(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getProductionTypeByCreditId", paramMap);
	}
	public static int getCreditIdByRectId(String rectId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rectId", rectId);
		return (Integer) sqlMap.queryForObject("leaseUtil.getCreditIdByRectId", paramMap);
	}
	//起租日期
	//首期租金
	//租赁期数
	//TR
	//文审状态
	//结清状态
	//合同rectId得到报告creditId
	//合同类型
	//增值税开票资料确认书
	
	
	//支付表--ShenQi
	//-----------------------------------------------
	//支付表号
	//剩余本金
	//剩余租金
	//已缴期数
	//未缴期数
	//全部期数
	//详细信息（每期）（税费方案）
	//税费方案
	//支付日（、、、）
	//利差
	//未到期利息
	//预期租金
	//某期已缴租金
	
		
	//拨款相关 -- Yang Yun
	//-----------------------------------------------
	//实际当款拨款金额（首拨款，非首拨）
	//拨款日期
	//拨款类型
	//
	
	
	
	//来款分解--ShenQi
	//-----------------------------------------------
	//来款日期
	//来款金额
	//来款名称
	//来款虚拟帐号
	//分解帐号
	
	//来款剩余金额(包含待确认)
	public static Double getRemainingMoneyByIncomeId(String incomeId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("incomeId", incomeId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getRemainingMoneyByIncomeId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	//来款剩余金额（自己算确认的）
	public static Double getRealRemainingMoneyByIncomeId(String incomeId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("incomeId", incomeId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getRealRemainingMoneyByIncomeId", paramMap);
		d = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	
	//逾期--Zhangbo
	//-----------------------------------------------
	//逾期天数
	//逾期期数
	//逾期金额
	//首次逾期期数
	//逾期罚息总额custId
	public static List<Map<String,Object>> getTotalFineByCustId(String custId) throws SQLException{
			List<Map<String,Object>> result =  new ArrayList<Map<String,Object>>();;
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("CUST_ID", custId);
			result = (List<Map<String,Object>>) sqlMap.queryForObject("decompose.getTotalFineByMap", paramMap);
			return result;
		}
	//逾期罚息总额custCode
	public static List<Map<String,Object>> getTotalFineByCustCode(String custCode) throws SQLException{
		List<Map<String,Object>> result =  new ArrayList<Map<String,Object>>();;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CUST_CODE", custCode);
		result = (List<Map<String,Object>>) sqlMap.queryForList("decompose.getTotalFineByMap", paramMap);
		return result;
	}
	//租金罚息罚息总额custCode
	public static List<Map<String,Object>> getTotalFineByMapForDecompose(String custCode) throws SQLException{
		List<Map<String,Object>> result =  new ArrayList<Map<String,Object>>();;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CUST_CODE", custCode);
		result = (List<Map<String,Object>>) sqlMap.queryForList("decompose.getTotalFineByMapForDecompose", paramMap);
		return result;
	}
	
	//逾期罚息总额recpId
	public static Map<String,Object> getTotalFineByRecpId(String recpId) throws SQLException{
		Map<String,Object> result =  new HashMap<String,Object>();;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("RECP_ID", recpId);
		result = (Map<String,Object>) sqlMap.queryForObject("decompose.getTotalFineByMap", paramMap);
		return result;
	}
	public static Map<String,Object> getTotalFineByRecpIdForDecompose(String recpId) throws SQLException{
		Map<String,Object> result =  new HashMap<String,Object>();;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("RECP_ID", recpId);
		result = (Map<String,Object>) sqlMap.queryForObject("decompose.getTotalFineByMapForDecompose", paramMap);
		return result==null?new HashMap<String,Object>():result;
	}
    // 已缴罚息
	//结清--Zhangbo
	//-----------------------------------------------
	//本金
	//利息
	//罚息
	//法务费用
	//其他费用
	// 提前结清
	//正常结清
	//结清日期
	//
	
	/**
	 * 实际剩余租金
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static Double getRemainingRental(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		String recpId = getRecpIdByCreditId(creditId);
		//判断是否有支付表
		if (StringUtils.isEmpty(recpId)) {
			d = getTotalRental(creditId);
		} else {
			BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getRemainingRental", paramMap);
			d = decimal == null ? 0D : decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return d;
	}
	
	/**
	 * 总租金
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static Double getTotalRental(String creditId) throws SQLException{
		Double d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		BigDecimal decimal = (BigDecimal) sqlMap.queryForObject("leaseUtil.getTotalRental", paramMap);
		d = decimal == null ? 0D : decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d;
	}
	
	/**
	 * 剩余期数
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static Integer getRemainingPeriodNum(String creditId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		Integer i = (Integer) sqlMap.queryForObject("leaseUtil.getRemainingPeriodNum", paramMap);
		return i;
	}
	
	/**
	 * 供应商有效合同数
	 * @param suplId
	 * @return
	 * @throws SQLException
	 */
	public static Integer getValidProjectBySuplId(String suplId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suplId", suplId);
		Integer i = (Integer) sqlMap.queryForObject("leaseUtil.getValidProjectBySuplId", paramMap);
		return i;
	}
	
	//保险 -- Yang Yun
	//-----------------------------------------------
	//投保日期
	//合同当前保险公司
	//投保费
	//
	//
	
	//锁码 -- Yang Yun
	//-----------------------------------------------
	//锁码方式
	//锁码时间（根据条件）
	
	
	//用户
	//-----------------------------------------------
	//用户名
	public static String getUserNameByUserId(String userId) throws SQLException {
		String userName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		userName = (String) sqlMap.queryForObject("leaseUtil.getUserNameByUserId", paramMap);
		return userName;
	}
	//upUser
	public static String getUpUserByUserId(String userId) throws SQLException{
		String result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		result = (String) sqlMap.queryForObject("leaseUtil.getUpUserByUserId", paramMap);
		return result;
	}
	//办事处
	public static String getDecpNameByUserId(String userId) throws SQLException{
		String decpName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		decpName = (String) sqlMap.queryForObject("leaseUtil.getDecpNameByUserId", paramMap);
		return decpName;
	}
	public static String getDecpIdByUserId(String userId) throws SQLException{
		String decpName = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		decpName = (String) sqlMap.queryForObject("leaseUtil.getDecpIdByUserId", paramMap);
		return decpName;
	}

	//email
	public static String getEmailByUserId(String userId) throws SQLException{
		String mail = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		mail = (String) sqlMap.queryForObject("leaseUtil.getEmailByUserId", paramMap);
		return mail;
	}
	//手机
	public static String getMobileByUserId(String userId) throws SQLException{
		String mobile = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		mobile = (String) sqlMap.queryForObject("leaseUtil.getMobileByUserId", paramMap);
		return mobile;
	}
	//是否离职
	public static boolean isLeaveOffice(String userId) throws SQLException{
		boolean isLeaveOffice = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		Integer status = (Integer) sqlMap.queryForObject("leaseUtil.getUserStatusByUserId", paramMap); 
		if(status!=null && status.intValue() ==-2){
			isLeaveOffice = true;
		}
		return isLeaveOffice;
	}
	//课主管
	public static String getClassLeaderByDecp(String decpId){
		String classLeader = null;
		if ("17".equals(decpId)) {
			//classLeader = "13";
		}
		return classLeader;
	}
	
	//业务助理
	public static List<String> getAssistantByUserId(String user_id) throws SQLException{
		String decpId = LeaseUtil.getDecpIdByUserId(user_id);
		return getAssistantByDecp(decpId);
	}
	
	public static List<String> getAssistantByDecp(String decpId) throws SQLException{
		List<String> assistant = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("decpId", decpId);
		assistant = (List<String>) sqlMap.queryForList("leaseUtil.getAssistantByDecp", paramMap);
		return assistant;
	}
	
	//业务助理邮箱
	public static String getAssistantEmailByUserId(String user_id) throws SQLException{
		StringBuffer assistantEmail = new StringBuffer();
		String decpId = LeaseUtil.getDecpIdByUserId(user_id);
		List<String> assistants = getAssistantByDecp(decpId);
		if (assistants != null && assistants.size() > 0) {
			for (String assistant : assistants) {
				assistantEmail.append(LeaseUtil.getEmailByUserId(assistant));
				assistantEmail.append(";");
			}
		}
		return assistantEmail.toString();
	}
	
	//课主管
	public static String getClassLeaderByCreditId(String creditId) throws SQLException{
		String classLeader = null;
		String sensor = getSensorIdByCreditId(creditId);
		//苏州课主管
		//张雅：张林、凡洁、魏巍
		if("246".equals(sensor) || "285".equals(sensor)||"443".equals(sensor)){
			classLeader = "49";
		}
		//高荣：吴圣瑞、王梅、黄士财 
		if("179".equals(sensor) || "244".equals(sensor) || "287".equals(sensor)){
			classLeader = "39";
		}
		//金旻育：赵贵菊、宋扬 、王欣文
		if("158".equals(sensor) || "335".equals(sensor) || "247".equals(sensor)){
			classLeader = "61";
		}
		
		//福州办事处联络人员 组长：衡青，业务：周榕伟、吴可平、闵睿
		/*	ID	NAME
			364	周榕伟
			394	吴可平
			406	衡青
			461	闵睿
		*/
		if("364".equals(sensor) || "394".equals(sensor) || "461".equals(sensor)){
			classLeader = "406";
		}
		
		/*小组分配如下： 组长：李惠敏 组员：杨穆贤、陈小爽、柳丽婉、冯勇*/
		/*
			ID	NAME
			168	李惠敏
			199	陈小爽
			293	杨穆贤
			405	柳丽婉
			440	冯勇
		*/
		if("199".equals(sensor) || "293".equals(sensor) || "405".equals(sensor) || "440".equals(sensor)){
			classLeader = "168";
		}
		
		/*
			ID	NAME
			172	陈小兵 (组长)
			336	陈诚
			344	陈剑波
			439	钟冬梅
		 */
		if("336".equals(sensor) || "344".equals(sensor) || "439".equals(sensor)){
			classLeader = "172";
		}
		return classLeader;
	}

	//承租人 -- Yang Yun
	//-----------------------------------------------

	//承租人归户
	//承租人名称
	
	//虚拟账号
	public static String getCustVirtualCodeById(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getCustVirtualCodeById",paramMap);
	}
	
	//客户编号
	public static String getCustCodeById(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getCustCodeById",paramMap);
	}
	
	//承租人是否存在
	//法人（、、、、）
	//自然人
	public static String getNatuIdCardByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuIdCardByCustId",paramMap);
	}
	
	public static Integer getNatuIdCardTypeByCustName(String custName) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custName", custName);
		return (Integer) sqlMap.queryForObject("leaseUtil.getNatuIdCardTypeByCustName",paramMap);
	}
	
	public static String getNatuAddressByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuAddressByCustId",paramMap);
	}
	
	public static String getNatuMobileByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuMobileByCustId",paramMap);
	}
	
	public static String getNatuZipByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuZipByCustId",paramMap);
	}
	
	public static String getNatuMateNameByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuMateNameByCustId",paramMap);
	}
	public static String getNatuMateIdCardByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuMateIdCardByCustId",paramMap);
	}
	
	public static String getNatuMateMobiledByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getNatuMateMobiledByCustId",paramMap);
	}
	
	//邮件地址
	//税务登记号
	//组织机构代码
	//虚拟帐号
	//开户银行
	//承租人联系人
	public static  List<LinkManTo> getLinkManInfoByCustId(String cust_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (List<LinkManTo>) sqlMap.queryForList("leaseUtil.getLinkManInfoByCustId", paramMap);		
	}
	
	
	
	//供应商
	//-----------------------------------------------
	//供应商归户
	//供应商级别
	public static String getSuppLevelBySupplId(String suppl_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suppl_id", suppl_id);
		return (String) sqlMap.queryForObject("leaseUtil.getSuppLevelBySupplierId", paramMap);

	}
	
	public static String getSuppOpenAccountBankBySupplierId(String suppl_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suppl_id", suppl_id);
		return (String) sqlMap.queryForObject("leaseUtil.getSuppOpenAccountBankBySupplierId", paramMap);
	}
	
	public static String getSuppBankAccountBySupplierId(String suppl_id) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suppl_id", suppl_id);
		return (String) sqlMap.queryForObject("leaseUtil.getSuppBankAccountBySupplierId", paramMap);
	}
	
	/**
	 * 供应商联保额度
	 * @param supl_id
	 * @return
	 * @throws Exception 
	 */
	public static CreditLineTO getSuplJointCreditBySuplId(String supl_id) throws Exception{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplJointCreditBySuplId", paraMap);
		if (creditLine != null) {
			creditLine.setUsedLine(getUsedLineBySupl(supl_id, CREDIT_LINE_TYPE.UNION));
			creditLine.setReusedLine(getReusedLineBySupl(supl_id, CREDIT_LINE_TYPE.UNION));
		}
		return creditLine;
	}
	
	/**
	 * 供应商回购额度
	 * @param supl_id
	 * @return
	 * @throws Exception 
	 */
	public static CreditLineTO getSuplBuyBackCreditBySuplId(String supl_id) throws Exception{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplBuyBackCreditBySuplId", paraMap);
		if (creditLine != null) {
			creditLine.setUsedLine(getUsedLineBySupl(supl_id, CREDIT_LINE_TYPE.BUY_BACK));
			creditLine.setReusedLine(getReusedLineBySupl(supl_id, CREDIT_LINE_TYPE.BUY_BACK));
		}
		return creditLine;
	}
	
	/**
	 * 供应商交机前拨款额度
	 * @param supl_id
	 * @return
	 * @throws Exception 
	 */
	public static CreditLineTO getSuplPayBeforeCreditBySuplId(String supl_id) throws Exception{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplPayBeforeCreditBySuplId", paraMap);
		if (creditLine != null) {
			if (creditLine.getRepeatFlag() == 1) {
				creditLine.setUsedLine(getUsedLineBySupl(supl_id, CREDIT_LINE_TYPE.PAY_BEFORE));
				creditLine.setReusedLine(getReusedLineBySupl(supl_id, CREDIT_LINE_TYPE.PAY_BEFORE));
			} else {
				creditLine.setUsedLine(getUsedLineBySupl(supl_id, creditLine.getStartDate(), CREDIT_LINE_TYPE.PAY_BEFORE));
				creditLine.setReusedLine(0D);
			}
		}
		return creditLine;
	}
	
	/* ============================集团授信=====================================================*/
	/**
	 * 集团号
	 * @param supl_id
	 * @return
	 * @throws SQLException
	 */
	public static String getSuplGroupCodeBySuplId(String supl_id) throws SQLException{
		String group_code = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		group_code = (String) sqlMap.queryForObject("leaseUtil.getSuplGroupCodeBySuplId", paraMap);
		return group_code;
	}
	
	/**
	 * 供应商是否有集团
	 * @param supl_id
	 * @return
	 * @throws SQLException
	 */
	public static boolean hasSuplGroupCredit(String supl_id) throws SQLException{
		boolean flag = false;
		String group_code = getSuplGroupCodeBySuplId(supl_id);
		if (StringUtils.isEmpty(group_code)) {
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 供应商是否有集团授信
	 * @param supl_id
	 * @return
	 * @throws SQLException
	 */
	/*public static boolean hasSuplGroupCredit(String supl_id, CREDIT_LINE_TYPE credit_line_type) throws SQLException{
		boolean flag = false;
		String group_code = getSuplGroupCodeBySuplId(supl_id);
		if (StringUtils.isEmpty(group_code)) {
			flag = false;
		} else {
			CreditLineTO line = null;
			if (credit_line_type.equals(CREDIT_LINE_TYPE.UNION)) {
				line = LeaseUtil.getSuplJointCreditByGroup(group_code);
			} else if (credit_line_type.equals(CREDIT_LINE_TYPE.BUY_BACK)) {
				line = LeaseUtil.getSuplBuyBackCreditByGroup(group_code);
			} else if (credit_line_type.equals(CREDIT_LINE_TYPE.PAY_BEFORE)) {
				line = LeaseUtil.getSuplPayBeforeCreditByGroup(group_code);
			} else if (credit_line_type.equals(CREDIT_LINE_TYPE.INVOICE)) {
				line = LeaseUtil.getInvoiceLineForGroup(group_code);
			}
			if (line != null && line.getLine() > 0 && "Y".equals(line.getHasLine())) {
				flag = true;
			}
		}
		return flag;
	}*/
	
	/**
	 * 集团联保额度
	 * @param group_code
	 * @return
	 * @throws SQLException 
	 */
	public static CreditLineTO getSuplJointCreditByGroup(String group_code) throws SQLException{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplJointCreditByGroup", paraMap);
		return creditLine;
	}
	
	/**
	 * 集团回购额度
	 * @param group_code
	 * @return
	 * @throws SQLException 
	 */
	public static CreditLineTO getSuplBuyBackCreditByGroup(String group_code) throws SQLException{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplBuyBackCreditByGroup", paraMap);
		return creditLine;
	}
	
	/**
	 * 集团交机前拨款额度
	 * @param group_code
	 * @return
	 * @throws Exception 
	 */
	public static CreditLineTO getSuplPayBeforeCreditByGroup(String group_code) throws Exception{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getSuplPayBeforeCreditByGroup", paraMap);
		if (creditLine != null) {
			if (creditLine.getRepeatFlag() == 1) {
				creditLine.setUsedLine(getUsedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE));
				creditLine.setReusedLine(getReusedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE));
			} else {
				creditLine.setUsedLine(getUsedLineByGroup(group_code, creditLine.getStartDate(), CREDIT_LINE_TYPE.PAY_BEFORE));
				creditLine.setReusedLine(0D);
			}
		}
		return creditLine;
	}
	
	/**
	 * 集团发票待补额度
	 * @param group_code
	 * @return
	 * @throws SQLException 
	 */
	public static CreditLineTO getInvoiceLineForGroup(String group_code) throws SQLException{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getInvoiceLineForGroup", paraMap);
		return creditLine;
	}
	
	/**
	 * 已用额度（集团）
	 * @param group_code
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getUsedLineByGroup(String group_code, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		List<String> supls = sqlMap.queryForList("leaseUtil.getSuplByGroup", paraMap);
		Double all = 0D;
		for (String supl_id : supls) {
			all += getUsedLineBySupl(supl_id, credit_line_type);
		}
		return all;
	}
	
	/**
	 * 已用额度（集团）一次性
	 * @param group_code
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getUsedLineByGroup(String group_code, Date checkedDate, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		List<String> supls = sqlMap.queryForList("leaseUtil.getSuplByGroup", paraMap);
		Double all = 0D;
		for (String supl_id : supls) {
			all += getUsedLineBySupl(supl_id, checkedDate, credit_line_type);
		}
		return all;
	}
	
	/**
	 * 可回收额度（集团）
	 * @param group_code
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getReusedLineByGroup(String group_code, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("group_code", group_code);
		List<String> supls = sqlMap.queryForList("leaseUtil.getSuplByGroup", paraMap);
		Double all = 0D;
		for (String supl_id : supls) {
			all += getReusedLineBySupl(supl_id, credit_line_type);
		}
		return all;
	}
	
	
	/**
	 * 交机前拨款已用额度(单个案件)
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static double getUsedLineForPayBeforeByCreditId(String creditId) throws SQLException{
		double usedLine = 0;
		if (isValidCredit(creditId)) {
			double payBeforeMoney = getPayMoneyByCreditIdForBefore(creditId);
			double realPayMoney = getRealPayMoneyByCreditId(creditId);
			double realPayMoneyInAuth = getRealPayMoneyInAuthByCreditId(creditId);
			usedLine = (realPayMoney + realPayMoneyInAuth) >= payBeforeMoney ? payBeforeMoney : (realPayMoney + realPayMoneyInAuth);
		}
		return usedLine;
	}
	
	/**
	 * 供应商找案件，已核准，交机前的案件
	 */
	public static List<String> getCreditIdBySuplIdForPayBefore(String suplId) throws SQLException{
		List<String> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suplId", suplId);
		resultList = sqlMap.queryForList("leaseUtil.getCreditIdBySuplIdForPayBefore", paramMap);
		return resultList;
	}
	
	/**
	 * 供应商找案件，交机前、发票和照片都补回的案件
	 */
	public static List<String> getReusedCreditBySuplForPayBefore(String suplId) throws SQLException{
		List<String> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suplId", suplId);
		resultList = sqlMap.queryForList("leaseUtil.getReusedCreditBySuplForPayBefore", paramMap);
		return resultList;
	}
	
	/**
	 * 供应商交机前拨款已用额度
	 * @param suplId
	 * @return
	 * @throws SQLException
	 */
	public static double getUsedLineForPayBeforeBySuplId(String suplId) throws SQLException{
		double usedLine = 0;
		//已核准且交机前拨款的案件
		List<String> creditIdList = LeaseUtil.getCreditIdBySuplIdForPayBefore(suplId);
		for (String creditId : creditIdList) {
			usedLine += getUsedLineForPayBeforeByCreditId(creditId);
		}
		return usedLine;
	}
	
	/**
	 * 供应商交机前拨款已用额度,一次性
	 * @param suplId
	 * @return
	 * @throws SQLException
	 */
	public static double getUsedLineForPayBeforeBySuplId(String suplId, Date checkedDate) throws SQLException{
		double usedLine = 0;
		Date payDate = null;
		//已核准且交机前拨款的案件
		List<String> creditIdList = LeaseUtil.getCreditIdBySuplIdForPayBefore(suplId);
		for (String creditId : creditIdList) {
			payDate = LeaseUtil.getFinancecontractDate(creditId);
			if (payDate == null || checkedDate == null || (!payDate.before(checkedDate))) {
				usedLine += getUsedLineForPayBeforeByCreditId(creditId);
			}
		}
		return usedLine;
	}
	
	/**
	 * 供应商交机前拨款可回收额度额度
	 * @param suplId
	 * @return
	 * @throws SQLException
	 */
	public static double getReusedLineForPayBeforeBySuplId(String suplId) throws SQLException{
		double usedLine = 0;
		//发票照片回来的案件
		List<String> creditIdList = LeaseUtil.getReusedCreditBySuplForPayBefore(suplId);
		for (String creditId : creditIdList) {
			usedLine += getUsedLineForPayBeforeByCreditId(creditId);
		}
		return usedLine;
	}
	
	
	
	/**
	 * 已用额度（供应商）<br>
	 * 联保回购额度提案时卡关，核准时占用。<br>
	 * 交机前拨款申请拨款时卡关，申请拨款后占用。
	 * @param supl_id
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getUsedLineBySupl(String supl_id, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Double result = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		List<String> usedCredit = null;
		if (credit_line_type.equals(CREDIT_LINE_TYPE.UNION)) {
			paraMap.put("credit_line_type", 1);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.BUY_BACK)) {
			paraMap.put("credit_line_type", 2);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.PAY_BEFORE)) {
			result = getUsedLineForPayBeforeBySuplId(supl_id);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.INVOICE)) {
			result = (Double)sqlMap.queryForObject("leaseUtil.getInvoiceUsedLine", paraMap);
		} else {
			throw new Exception("额度类型错误。");
		}
		
		return result;
	}
	
	/**
	 * 已用额度（供应商）- 一次性<br>
	 * 联保回购额度提案时卡关，核准时占用。<br>
	 * 交机前拨款申请拨款时卡关，申请拨款后占用。
	 * @param supl_id
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getUsedLineBySupl(String supl_id, Date checkeDate, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Double result = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		List<String> usedCredit = null;
		if (credit_line_type.equals(CREDIT_LINE_TYPE.UNION)) {
			paraMap.put("credit_line_type", 1);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.BUY_BACK)) {
			paraMap.put("credit_line_type", 2);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.PAY_BEFORE)) {
			result = getUsedLineForPayBeforeBySuplId(supl_id, checkeDate);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.INVOICE)) {
			result = (Double)sqlMap.queryForObject("leaseUtil.getInvoiceUsedLine", paraMap);
		} else {
			throw new Exception("额度类型错误。");
		}
		
		return result;
	}
	
	//全部已用额度
	private static Double getUsedLineForAll(List<String> usedCredit) throws SQLException{
		Double d = 0D;
		for (String creditId : usedCredit) {
			d += getTotalPrincipalByCreditId(creditId);	//总本金
		}
		return d;
	}
	
	/**
	 * 可回收额度（供应商）
	 * @param supl_id
	 * @param credit_line_type
	 * @return
	 * @throws Exception
	 */
	public static Double getReusedLineBySupl(String supl_id, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		Double result = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		List<String> usedCredit = null;
		if (credit_line_type.equals(CREDIT_LINE_TYPE.UNION)) {
			paraMap.put("credit_line_type", 1);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit) - getUsedLineForReal(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.BUY_BACK)) {
			paraMap.put("credit_line_type", 2);
			usedCredit = sqlMap.queryForList("leaseUtil.getUsedCreditBySupl", paraMap);
			result = getUsedLineForAll(usedCredit) - getUsedLineForReal(usedCredit);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.PAY_BEFORE)) {
			result = getReusedLineForPayBeforeBySuplId(supl_id);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.INVOICE)) {
			result = (Double)sqlMap.queryForObject("leaseUtil.getInvoiceReusedLine", paraMap);
		} else {
			throw new Exception("额度类型错误。");
		}
		return result;
	}
	
	//实际已用额度
	private static Double getUsedLineForReal(List<String> usedCredit) throws SQLException{
		Double d = 0D;
		for (String creditId : usedCredit) {
			d += getRemainingPrincipalByCreditId(creditId);	//实际剩余本金
		}
		return d;
	}
	
	
	//供应商授信额度（交机前）
	//供应商授信额度交机后（连保、回购）（是否循环）
	//供应商授信剩余额度（、）
	//主要联系人
	public static LinkManTo getMainLinkManBySupplId(String suppl_id) throws SQLException{ 
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("link_type", 0);
		paramMap.put("suppl_id", suppl_id);
		return (LinkManTo) sqlMap.queryForObject("leaseUtil.getLinkManInfoBySupplierId", paramMap);
	}
	//供应商所有联系人
	public static List<LinkManTo> getLinkManInfoBySupplId(String suppl_id) throws SQLException{ 
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("suppl_id", suppl_id);
		return (List<LinkManTo>) sqlMap.queryForList("leaseUtil.getLinkManInfoBySupplierId", paramMap);
	}
	
	
	/**
	 * 根据承租人标识得到承租人名称
	 * 
	 * @param custId
	 * @return
	 * @throws SQLException 
	 */
	public static String getCustNameByCustId(String cust_id) throws SQLException{
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		return (String) sqlMap.queryForObject("leaseUtil.getCustNameByCustId",paramMap);
	}
	/**
	 * 根据支付表表头表标识得到该合同已缴期数
	 * @param recpId
	 * @return
	 */
	public String getPaidPeriodByRecpId(String recpId){
			
		//TODO
		
		
		return null;
	}
	public static String getRectIdByRecpId(String recpId) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("recpId", recpId);
		return (String) sqlMap.queryForObject("leaseUtil.getRectIdByRecpId",paramMap);
	}
	//根据支付表表头表标识得到该合同所有罚息
	//根据支付表表头表标识得到该合同已缴罚息
	//根据支付表表头表标识得到该合同未缴罚息
	
	//根据支付表表头表标识、期数得到该期罚息
	
	//根据支付表表头表标识、期数
	
	
	//发票
	//供应商一共待补发票数量
	public static int getAllInvoiceBySuplId(String supl_id) throws SQLException{
		int i = 0;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		i = (Integer) sqlMap.queryForObject("invoice.getInvoiceBySuplId", paraMap);
		return i;
	}
	
	//供应商目前正在待补发票的数量
	public static int getBeingInvoiceBySuplId(String supl_id) throws SQLException{
		int i = 0;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		paraMap.put("flag", "being");
		i = (Integer) sqlMap.queryForObject("invoice.getInvoiceBySuplId", paraMap);
		return i;
	}
	
	//供应商发票授信额度
	public static CreditLineTO getInvoiceLineForSupl(String supl_id) throws Exception{
		CreditLineTO creditLine = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("supl_id", supl_id);
		creditLine = (CreditLineTO) sqlMap.queryForObject("leaseUtil.getInvoiceLineForSupl", paraMap);
		if (creditLine != null) {
			creditLine.setUsedLine(getUsedLineBySupl(supl_id, CREDIT_LINE_TYPE.INVOICE));
			creditLine.setReusedLine(getReusedLineBySupl(supl_id, CREDIT_LINE_TYPE.INVOICE));
		}
		return creditLine;
	}
	
	
	/**
	 * 案件的归户金额
	 * @param creditId
	 * @return
	 * @throws SQLException
	 */
	public static GuiHuInfo getGuiHuByCreditId(String creditId) throws SQLException{
		GuiHuInfo guihu = new GuiHuInfo();
		guihu.setTotalPrincipal(getTotalPrincipalByCreditId(creditId));
		guihu.setRemainingPrincipal(getRemainingPrincipalByCreditId(creditId));
		guihu.setTotalRentMoney(getTotalRental(creditId));
		guihu.setRemainingRentMoney(getRemainingRental(creditId));
		return guihu;
	}
	
	/**
	 * 客户归户（不含本案）
	 * @param custId
	 * @param thisCreditId
	 * @return
	 * @throws SQLException
	 */
	public static GuiHuInfo getGuiHuByCustId(String custId, String thisCreditId) throws SQLException{
		List<String> creditIds = getCreditIdByCustId(custId);
		GuiHuInfo guihu = new GuiHuInfo();
		double totalPrincipal = 0;
		double remainingPrincipal = 0;
		double totalRentMoney = 0;
		double remainingRentMoney = 0;
		int count = 0;
		for (String creditId : creditIds) {
			if (!creditId.equals(thisCreditId) && isValidCredit(creditId)) {
				totalPrincipal += getTotalPrincipalByCreditId(creditId);
				remainingPrincipal += getRemainingPrincipalByCreditId(creditId);
				totalRentMoney += getTotalRental(creditId);
				remainingRentMoney += getRemainingRental(creditId);
				count ++;
			}
		}
		guihu.setTotalPrincipal(totalPrincipal);
		guihu.setRemainingPrincipal(remainingPrincipal);
		guihu.setTotalRentMoney(totalRentMoney);
		guihu.setRemainingRentMoney(remainingRentMoney);
		guihu.setProjectCount(count);
		return guihu;
	}
	
	/**
	 * 客户归户（含本案）
	 * @param custId
	 * @param thisCreditId
	 * @return
	 * @throws SQLException
	 */
	public static GuiHuInfo getGuiHuByCustId(String custId) throws SQLException{
		List<String> creditIds = getCreditIdByCustId(custId);
		GuiHuInfo guihu = new GuiHuInfo();
		double totalPrincipal = 0;
		double remainingPrincipal = 0;
		double totalRentMoney = 0;
		double remainingRentMoney = 0;
		int count = 0;
		for (String creditId : creditIds) {
			totalPrincipal += getTotalPrincipalByCreditId(creditId);
			remainingPrincipal += getRemainingPrincipalByCreditId(creditId);
			totalRentMoney += getTotalRental(creditId);
			remainingRentMoney += getRemainingRental(creditId);
			count ++;
		}
		guihu.setTotalPrincipal(totalPrincipal);
		guihu.setRemainingPrincipal(remainingPrincipal);
		guihu.setTotalRentMoney(totalRentMoney);
		guihu.setRemainingRentMoney(remainingRentMoney);
		guihu.setProjectCount(count);
		return guihu;
	}
	
	/**
	 * 担保人归户
	 * @param guar
	 * @param thisCreditId
	 * @return
	 * @throws SQLException
	 */
	public static GuiHuInfo getGuiHuByGuar(Map<String, String> guar, String thisCreditId) throws SQLException{
		String guarId = guar.get("ID");
		String guarName = guar.get("NAME");
		List<String> creditIds = getCreditIdByGuarantor(guarId);
		GuiHuInfo guihu = new GuiHuInfo();
		double totalPrincipal = 0;
		double remainingPrincipal = 0;
		double totalRentMoney = 0;
		double remainingRentMoney = 0;
		int count = 0;
		for (String creditId : creditIds) {
			if (!creditId.equals(thisCreditId) && isValidCredit(creditId)) {
				totalPrincipal += getTotalPrincipalByCreditId(creditId);
				remainingPrincipal += getRemainingPrincipalByCreditId(creditId);
				totalRentMoney += getTotalRental(creditId);
				remainingRentMoney += getRemainingRental(creditId);
				count ++;
			}
		}
		guihu.setTotalPrincipal(totalPrincipal);
		guihu.setRemainingPrincipal(remainingPrincipal);
		guihu.setTotalRentMoney(totalRentMoney);
		guihu.setRemainingRentMoney(remainingRentMoney);
		guihu.setProjectCount(count);
		guihu.setName(guarName);
		return guihu;
	}
	
	
	/**
	 * 获取文件路径
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static String getFilePath(String xmlPath) throws Exception{
		String path = null;
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element element = (Element) it.next();
				Element nameElement = element.element("name");
				String s = nameElement.getText();
				if (xmlPath.equals(s)) {
					Element pathElement = element.element("path");
					path = pathElement.getText();
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return path;
	}
	

	public static List<CreditLineTO> getSuplCreditLine(String supl_id) throws Exception{
		List<CreditLineTO> lineList = new ArrayList<CreditLineTO>();
		CreditLineTO line = null;
		line = getSuplJointCreditBySuplId(supl_id);	//连保
		lineList.add(line);
		line = getSuplBuyBackCreditBySuplId(supl_id);	//回购
		lineList.add(line);
		line = getSuplPayBeforeCreditBySuplId(supl_id);	//交机前
		lineList.add(line);
		line = getInvoiceLineForSupl(supl_id);	//发票
		lineList.add(line);
		return lineList;
	}
	

	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyNameByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = Constants.COMPANY_NAME;
				break;
			case 2:
				str = Constants.COMPANY_NAME_YUGUO;
				break;
		}
		return str;
	}
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyEnglisgNameByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = Constants.COMPANY_NAME_ENGLISH;
				break;
			case 2:
				str = Constants.COMPANY_NAME_ENGLISH_YUGUO;
				break;
		}
		return str;
	}
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyAddressByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = Constants.COMPANY_COMMON_ADDRESS;
				break;
			case 2:
				str = Constants.COMPANY_COMMON_ADDRESS_YUGUO;
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyRegisteredAddressByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "苏州工业园区东富路8号";
				break;
			case 2:
				str = "杭州市萧山区临江高新技术产业园区科技文化中心124房";
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyFaxByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "0512-80983567";
				break;
			case 2:
				str = "0571-57576377";
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyTelephoneByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "0512-80983566";
				break;
			case 2:
				str = "0571-57576388";
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyPostcodeByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "215022";
				break;
			case 2:
				str = "      ";
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyTaxCodeByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "321700555882350";
				break;
			case 2:
				str = "330181088852506";
				break;
		}
		return str;
	}
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyBankNameByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "中国银行苏州工业园区娄葑支行";
				break;
			case 2:
				str = "上海浦东发展银行萧山支行";
				break;
		}
		return str;
	}
	
	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyBankAccountByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "497558194856";
				break;
			case 2:
				str = "95070155300002639";
				break;
		}
		return str;
	}

	/**
	 * 
	 * @param companyCode
	 * @return
	 */
	public static String getCompanyBankName2ByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "交通银行苏州分行园区支行";
				break;
			case 2:
				str = "交通银行苏州分行园区支行";
				break;
		}
		return str;
	}
	public static int getCompanyCodeByPayId(String payId) throws SQLException {
		Integer result = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("payId", payId);
		result = (Integer) sqlMap.queryForObject("leaseUtil.getCompanyCodeByPayId", paramMap);
		return result;
	}
	
	public static  List<Map<String,String>> getCompanys(){
		List<Map<String,String>> companys = new ArrayList<Map<String,String>>();
		Map<String,String> company = new HashMap<String,String>();
		company.put("name", "裕融");
		company.put("code", "1");
		companys.add(company);
		company = new HashMap<String,String>();
		company.put("name", "裕国");
		company.put("code", "2");
		companys.add(company);
		return companys;
	} 
	
	public static String getCompanyShortNameByCompanyCode(int companyCode){
		String str = "";
		switch(companyCode){
			case 1:
				str = "裕融";
				break;
			case 2:
				str = "裕国";
				break;
		}
		return str;
	}
	public static String getIPAddress(){
		
		//Properties properties = new Properties();
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		if("10.2.1.219".equals(ip)){//quartz 指向 236
			ip = "10.2.1.236";
		}
//		if(!"".equals(ip) && !"10.2.1.236".equals(ip) && !"10.2.1.193".equals(ip)){
//			try {
//				InputStream stream = LeaseUtil.class.getResourceAsStream("config.properties");  
//			    properties.load(stream);
//			    ip = properties.getProperty("uploadip");
//			} catch (Exception e ) {
//			    //加载失败的处理
//			    e.printStackTrace();			    
//			}
//		}
		return ip;
	}
	
	public static void main(String[] args) throws UnknownHostException {
		System.out.println(getIPAddress());
	}
}