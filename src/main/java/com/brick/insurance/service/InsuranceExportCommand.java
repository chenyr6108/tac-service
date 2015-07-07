package com.brick.insurance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.web.HTMLUtil;

public class InsuranceExportCommand {
	
	public static Map<String, Object> getInsuListByExcel(String paramContent, int flag) throws Exception{
		System.out.println("==========getInsuListByExcel==========");
		System.out.println(flag);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> insuInfoList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramap2 = null;
		if (flag == 1) {
			System.out.println(1 + paramContent);
			paramMap.put("recd_id", paramContent);
			insuInfoList = (List<Map<String, Object>>) DataAccessor.query("insurance.getInsuInfoForExport", paramMap, RS_TYPE.LIST);
			String[] recdIds = paramContent.split(",");
			for (String recdId : recdIds) {
				paramap2 = new HashMap<String, Object>();
				paramap2.put("flag", "已导出");
				paramap2.put("recd_id", recdId);
				DataAccessor.execute("insuranceExp.expFlag", paramap2, OPERATION_TYPE.UPDATE);
			}
		} else if (flag == 2) {
			paramMap.put("insu_id", paramContent);
			insuInfoList = (List<Map<String, Object>>) DataAccessor.query("insurance.getInsuInfoForExportByInsu", paramMap, RS_TYPE.LIST);
			
			String[] insu_ids = paramContent.split(",");
			StringBuffer sb = new StringBuffer();
			for (String string : insu_ids) {
				sb.append("'" + string + "',");
			}
			String insu_idsForSql = "(" + sb.substring(0, sb.length() - 1).toString() + ")";
			Map<String, Object> paramMapForInsuId = new HashMap<String, Object>();
			paramMapForInsuId.put("insu_idsForSql", insu_idsForSql);
			List<Map<String, Object>> recdIds = (List<Map<String, Object>>) DataAccessor.query("insurance.getEqmtByInsuIdForExp",paramMapForInsuId,DataAccessor.RS_TYPE.LIST);
			for (Map<String, Object> map : recdIds) {
				paramap2 = new HashMap<String, Object>();
				paramap2.put("flag", "已导出");
				paramap2.put("recd_id", map.get("RECD_ID"));
				DataAccessor.execute("insuranceExp.expFlag", paramap2, OPERATION_TYPE.UPDATE);
			}
		}
		resultMap.put("insuInfoList", insuInfoList);
		if (insuInfoList != null && insuInfoList.size() > 0) {
			System.out.println("insuInfoList size : " + insuInfoList.size());
			resultMap.put("incp_name", insuInfoList.get(0).get("INCP_NAME"));
			resultMap.put("incp_id", insuInfoList.get(0).get("INCP_ID"));
		}
		System.out.println("==========getInsuListByExcel.End==========");
		return resultMap;
	}
	
	public static List<Map<String, Object>> getInsuExcelForEmail(String paramContent) throws Exception{
		List<Map<String, Object>> insuInfoList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("insu_id", paramContent);
		insuInfoList = (List<Map<String, Object>>) DataAccessor.query("insurance.getInsuExcelForEmail", paramMap, RS_TYPE.LIST);
		return insuInfoList;
	}
	
	public static List<Map<String, Object>> waitingForSurrender(String paramContent) throws Exception{
		List<Map<String, Object>> insuInfoList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("insu_ids", paramContent);
		insuInfoList = (List<Map<String, Object>>) DataAccessor.query("insurance.getWaitingForSurrenderFile", paramMap, RS_TYPE.LIST);
		System.out.println(insuInfoList.size());
		return insuInfoList;
	}
	
}
