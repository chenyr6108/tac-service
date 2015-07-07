package com.brick.insurance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

/*
 * 保单的作废
 */
public class CancellationService extends AService{
	static Log logger = LogFactory.getLog(InusranceListService.class);
	/**
	 * 保单管理页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public  void queryAll(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		List company=new ArrayList();
		if (errList.isEmpty()) {
			try {
				company = (List) DataAccessor.query("insurance.getCompany",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				dw = (DataWrap) DataAccessor.query("insurance.insuListManage",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				errList.add("保单管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("company", company);
			outputMap.put("INCP",context.request.getParameter("INCP"));
			outputMap.put("content", context.contextMap.get("content"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/cancellation/insuListManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
}
