package com.brick.financial.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.financial.dao.ContractControlSheetDAO;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class ContractControlSheetService extends BaseService {
	
	private ContractControlSheetDAO contractControlSheetDAO;

	public ContractControlSheetDAO getContractControlSheetDAO() {
		return contractControlSheetDAO;
	}

	public void setContractControlSheetDAO(
			ContractControlSheetDAO contractControlSheetDAO) {
		this.contractControlSheetDAO = contractControlSheetDAO;
	}
	
	public List<Map<String,Object>> getContractControlSheetDetail(Context context) throws Exception {
		return this.contractControlSheetDAO.getContractControlSheetDetail(context);
	}
	
	public static List<Map<String,Object>> getContractControlSheet(String fromDate,String toDate,String payOrder,String content,
			String contractType,String companyCode) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("fromDate",fromDate);
		param.put("toDate",toDate);
		param.put("payOrder",payOrder);
		param.put("content",content);
		param.put("contractType",contractType);
		param.put("companyCode",companyCode);
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentContract.getContractControlSheet",param,RS_TYPE.LIST);
		} catch (Exception e) {
			
		}
		return resultList;
	}
	
	public double getContractControlSheetPayMoneyTotal(Context context) throws Exception {
		return this.contractControlSheetDAO.getContractControlSheetPayMoneyTotal(context);
	}
	
	/**
	 * 导出小车委贷支付表明细
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static List<Map<String,Object>> exportRentDetailXls(String fromDate,String toDate,String companyCode) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("date1",fromDate);
		param.put("date2",toDate);
		param.put("companyCode",companyCode);
		List<Map<String,Object>> resultList=null;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentContract.exportRentDetailXls",param,RS_TYPE.LIST);
		} catch (Exception e) {
			
		}
		return resultList;
	}
}
