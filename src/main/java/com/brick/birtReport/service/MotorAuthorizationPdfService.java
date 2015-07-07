package com.brick.birtReport.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

public class MotorAuthorizationPdfService {

	//通过报告号获得客户名称
	public static Map<String,Object> getCustNameByCreditId(String creditId) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("credit_id",creditId);
		Map<String,Object> custInfo=null;
		try {
			custInfo=(Map<String,Object>)DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit",param,RS_TYPE.OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return custInfo;
	}
	
	public static List<Map<String,Object>> getMotorInfoByCreditId(String creditId) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("PRCD_ID",creditId);
		List<Map<String,Object>> motorInfo=null;
		
		try {
			motorInfo=(List<Map<String,Object>>)DataAccessor.query("rentContract.queryeqmtsPrjt",param,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return motorInfo;
	}
}
