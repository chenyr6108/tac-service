package com.brick.backinfo.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.baseManage.service.BusinessLog;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 
 * 回信管理
 * 
 * @author 胡昭卿
 * @date 04-06, 2011
 */

public class BackInfoManage extends AService {
	Log logger = LogFactory.getLog(BackInfoManage.class);

	/**
	 * 报告显示
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creditManage(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		List errList = context.errList ;
		
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));

			dw = (DataWrap) DataAccessor.query(
					"creditReportManage.getCreditReportsHuiJain", context.contextMap,
					DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("拨款管理--回件管理错误!请联系管理员") ;
		}

		
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		//outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/backinfomanage/creditReportManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	
	/**
	 * 查看资料 pact page
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentFile(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List insorupd = new ArrayList();
		Map infor = new HashMap();
		try {
			insorupd = (List) DataAccessor.query("rentFile.selectRentFileForBack", context.contextMap,DataAccessor.RS_TYPE.LIST);
			// 查询承租人资料和合同资料的信息
			infor = (Map) DataAccessor.query("rentFile.selectInfor",context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 查询担保人资料的信息
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("拨款管理--查看回件错误!请联系管理员");
		}

		if (errList.isEmpty()) {
			outputMap.put("insorupd", insorupd);
			outputMap.put("infor", infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("rentFileFlag", context.contextMap.get("rentFileFlag"));
			Output.jspOutput(outputMap, context, "/backinfomanage/rentFile.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/sys/acl/login.jsp");
		}
	}
	
	
	//查看所有已上传的资料及合同必上传但是还没有上传的资料
	public void queryRentFileAll(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List fileAll = new ArrayList();
		List contractDetil = new ArrayList();
		try {
			// 查询所有已经上传的资料
			fileAll = (List) DataAccessor.query("rentFile.getUPAll", context.contextMap,DataAccessor.RS_TYPE.LIST);
			// 查询合同资料必须上传的但是没有上传的资料
			contractDetil = (List) DataAccessor.query("rentFile.getUPContractNo",context.contextMap, DataAccessor.RS_TYPE.LIST);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("拨款管理--查看回件资料上传错误!请联系管理员");
		}

		if (errList.isEmpty()) {
			outputMap.put("fileAll", fileAll);
			outputMap.put("contractDetil", contractDetil);
			Output.jspOutput(outputMap, context, "/backinfomanage/rentFileDetel.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/sys/acl/login.jsp");
		}
	}
}