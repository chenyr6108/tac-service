package com.brick.contract.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.collection.service.StartPayService;
import com.brick.util.Constants;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.pdf.PdfPCell;

public class ContractBuilder {
	static Log logger = LogFactory.getLog(ContractBuilder.class);
	private static SqlMapClient sqlMap;
	
	public SqlMapClient getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(SqlMapClient sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	public static Map<String, Object> directContractBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			
			
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType) && companyCode!=1){
				map.put("comp_name", Constants.COMPANY_NAME_YUGUO);
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
			}
			map.put("equpmentAddress", LeaseUtil.getEqupmentAddressByCreditId(creditId));
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditId);
			List<Map<String, Object>> eqmtList = sqlMap.queryForList("rentContract.getEqmtByCreditId", paramMap);
			map.put("eqmtList", eqmtList);
		} catch (SQLException e) {
			logger.error(e);
		}
		return map;
	}
	
	public static Map<String, Object> directPaymentInstructionBuilder(String creditId, String bank){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			map.put("price2Supl", LeaseUtil.getPledgePriceFor2SuplByCreditId(creditId));
			map.put("totalPrice", LeaseUtil.getTotalPriceByCreditId(creditId));
			String[] bankInfo = bank.split("=");
			if (bankInfo != null && bankInfo.length == 4) {
				//${item.NAME}=${item.BANK_ACCOUNT}=${item.OPEN_ACCOUNT_BANK}=${item.MONEY}
				map.put("NAME", bankInfo[0]);
				map.put("BANK_ACCOUNT", bankInfo[1]);
				map.put("OPEN_ACCOUNT_BANK", bankInfo[2]);
				map.put("MONEY", bankInfo[3]);
			}
			
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType) && companyCode!=1){
				map.put("comp_name", Constants.COMPANY_NAME_YUGUO);
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return map;
	}
	
	public static Map<String, Object> directConfirmLetterForSuplBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType)){
				map.put("comp_name", LeaseUtil.getCompanyNameByCompanyCode(companyCode));
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
			}
			map.put("pledgePriceAvg", LeaseUtil.getPledgePriceForAvgByCreditId(creditId));
			map.put("payMoney", LeaseUtil.getPayMoneyByCreditId(creditId));
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditId);
			List<Map<String, Object>> eqmtList = sqlMap.queryForList("rentContract.getEqmtByCreditId", paramMap);
			map.put("eqmtList", eqmtList);
		} catch (SQLException e) {
			logger.error(e);
		}
		return map;
	}
	
	public static Map<String, Object> directPaymentAgreementBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType)){
				map.put("comp_name", LeaseUtil.getCompanyNameByCompanyCode(companyCode));
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
			}
			map.put("payMoney", LeaseUtil.getPayMoneyByCreditId(creditId));
			map.put("pledgePriceAvg", LeaseUtil.getPledgePriceForAvgByCreditId(creditId));
			map.put("price2Supl", LeaseUtil.getPledgePriceFor2SuplByCreditId(creditId));
			map.put("totalPrice", LeaseUtil.getTotalPriceByCreditId(creditId));
		} catch (SQLException e) {
			logger.error(e);
		}
		return map;
	}
	
	public static Map<String, Object> directLeaseStartAdviceBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType)){
				map.put("comp_name", LeaseUtil.getCompanyNameByCompanyCode(companyCode));
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
			}
			map.put("periods", LeaseUtil.getPeriodsByCreditId(creditId));
			map.put("equpmentAddress", LeaseUtil.getEqupmentAddressByCreditId(creditId));
			map.put("companyAddress", LeaseUtil.getCompanyAddressByCreditId(creditId));
			map.put("manufacturer", LeaseUtil.getManufacturerByCreditId(creditId));
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditId);
			List<Map<String, Object>> eqmtList = sqlMap.queryForList("rentContract.getEqmtByCreditId", paramMap);
			map.put("eqmtList", eqmtList);
		} catch (SQLException e) {
			logger.error(e);
		}
		return map;
	}
	public static Map<String, Object> directItemSituationBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			map.put("comp_name", Constants.COMPANY_NAME);
			map.put("periods", LeaseUtil.getPeriodsByCreditId(creditId));
			map.put("pledgePriceAvg", LeaseUtil.getPledgePriceForAvgByCreditId(creditId));
			map.put("pledgePriceForLast", LeaseUtil.getPledgePriceForLastByCreditId(creditId));
			map.put("pledgePeriodForLast", LeaseUtil.getPledgePeriodForLastByCreditId(creditId));
			map.put("pledgePriceForBack", LeaseUtil.getPledgePriceForBackByCreditId(creditId));
			map.put("payMoney", LeaseUtil.getPayMoneyByCreditId(creditId));
			map.put("equpmentAddress", LeaseUtil.getEqupmentAddressByCreditId(creditId));
			map.put("companyAddress", LeaseUtil.getCompanyAddressByCreditId(creditId));
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditId);
			List<Map<String, Object>> eqmtList = sqlMap.queryForList("rentContract.getEqmtByCreditId", paramMap);
			map.put("eqmtList", eqmtList);
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(creditId, Integer.valueOf(1));
			map.put("irrMonthPaylines", irrMonthPaylines);
		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}
	
	public static Map<String,Object> directReceiptBuilder(String creditId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("lease_code", LeaseUtil.getLeaseCodeByCreditId(creditId));
			map.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
			map.put("supl_name", LeaseUtil.getSuplNameByCreditId(creditId));
			//直租 添加公司别判断
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			if("7".equals(contractType)){
				map.put("comp_name", LeaseUtil.getCompanyNameByCompanyCode(companyCode));
				map.put("tax_code", LeaseUtil.getCompanyTaxCodeByCompanyCode(companyCode));
				map.put("address", LeaseUtil.getCompanyAddressByCompanyCode(companyCode) + " " +LeaseUtil.getCompanyTelephoneByCompanyCode(companyCode));
				map.put("bank", LeaseUtil.getCompanyBankNameByCompanyCode(companyCode) + " " +LeaseUtil.getCompanyBankAccountByCompanyCode(companyCode));
			}else{
				map.put("comp_name", Constants.COMPANY_NAME);
				map.put("tax_code", LeaseUtil.getCompanyNameByCompanyCode(1));
				map.put("address", LeaseUtil.getCompanyAddressByCompanyCode(1) + " " +LeaseUtil.getCompanyTelephoneByCompanyCode(1));
				map.put("bank", LeaseUtil.getCompanyBankNameByCompanyCode(1) + " " +LeaseUtil.getCompanyBankAccountByCompanyCode(1));
			}
			

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditId);
			List<Map<String, Object>> eqmtList = sqlMap.queryForList("rentContract.getEqmtByCreditId", paramMap);
			int i = 1;
			BigDecimal total = new BigDecimal(0);
			for(Map<String, Object> eqmt:eqmtList){
				int qty = (Integer)eqmt.get("THING_COUNT");
				BigDecimal price = new BigDecimal((Double)eqmt.get("UNIT_PRICE"));
				total = total.add(price.multiply(new BigDecimal(qty)));
				eqmt.put("index", i);
				i++;
			}
			map.put("total", total);
			map.put("eqmtList", eqmtList);

		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}
	
}
