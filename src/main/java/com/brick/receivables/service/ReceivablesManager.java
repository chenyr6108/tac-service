package com.brick.receivables.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class ReceivablesManager extends AService {

	Log logger = LogFactory.getLog(ReceivablesManager.class);
	public static final Logger log = Logger.getLogger(ReceivablesManager.class);

	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}

	/**
	 * 查询所有应收账款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryReceivablesByYear(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		Map yearMap = new HashMap();
		List quarterList = new ArrayList();
		List monthList = new ArrayList();
		int cardFlag;
		Object temp1 = context.getRequest().getParameter("cardFlag");
		cardFlag = (temp1 == null ? 0 : Integer.valueOf(temp1.toString()));
		String search_year;
		Object temp2 = context.getRequest().getParameter("search_year");
		search_year = (temp2 == null ? "2010" : temp2.toString());
		context.contextMap.put("search_year", search_year);
		if (errorList.isEmpty()) {
			try {
				yearMap = (Map) DataAccessor.query(
						"receivables.queryReceivablesByYear",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				quarterList = (List) DataAccessor.query(
						"receivables.queryReceivablesByQuarter",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				monthList = (List) DataAccessor.query(
						"receivables.queryReceivablesByMonth",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.ReceivablesManager.queryReceivablesByYear"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.ReceivablesManager.queryReceivablesByYear"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("cardFlag", cardFlag);
		outputMap.put("yearMap", yearMap);
		outputMap.put("quarterList", quarterList);
		outputMap.put("monthList", monthList);
		Output.jspOutput(outputMap, context,
				"/receivables/showReceivablesMain.jsp");
	}
}
