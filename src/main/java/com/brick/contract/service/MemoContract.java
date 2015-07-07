package com.brick.contract.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.brick.collection.support.PayRate;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 合同模块的操作
 * @author wuzhendong
 * @date 6 29, 2010
 * @version
 */
public class MemoContract extends AService {

	Log logger = LogFactory.getLog(MemoContract.class);
	/**
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void Pact(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		List<Map> memoList = null;
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){	
		
			try {

				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));

				//
				memoList = (List) DataAccessor.query("rentContractPact.selectMemo", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("memoList", memoList);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		}	
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/rentcontract/pactMore.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/sys/acl/login.jsp");
		}	
	}	
	
	
	
	/**
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void inserttoremark(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;

		try { 
				DataAccessor.execute("rentContractPact.insertMemo", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				outputMap.put("PRCD_ID", context.contextMap.get("PRCD_ID"));
				outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));

				
		} catch (Exception e) {
			errList.add("添加备注失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);
		Output.jsonOutput(outputMap, context);
	}	

}
