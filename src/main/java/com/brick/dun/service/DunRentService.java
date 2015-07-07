package com.brick.dun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.SendMsg;

/**
 * 租金催收
 * 
 * @author  li shaojie
 * @date Jul 15, 2010
 */

public class DunRentService extends AService {
	Log logger = LogFactory.getLog(DunRentService.class);

	
	/**
	 * 租金催收管理
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void manage(Context context){
		Map outputMap=new HashMap();
		DataWrap dw=null;
		
		try {
			outputMap.put("companyList", DataAccessor.query("companyManage.queryCompanyAlias", null,DataAccessor.RS_TYPE.LIST));
			/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
			Map<String, Object> rsMap = null;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (rsMap == null || rsMap.get("NODE") == null) {
				throw new Exception("Session lost");
			}
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			dw=(DataWrap)DataAccessor.query("dunRent.getAllDunRent", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		outputMap.put("dw", dw);
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("pay_type", context.contextMap.get("pay_type"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("NAME", context.contextMap.get("NAME"));
		outputMap.put("COMPANY", context.contextMap.get("COMPANY"));
		Output.jspOutput(outputMap, context, "/dun/dunRentManage.jsp");
	}
	
	/**
	 * 根据承租人编号。获取该承租人的逾期数据
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDunRentInfoByCustCode(Context context) {
		Map outputMap = new HashMap();
		List dunList = null;
		List dunRecordList = null;
		List custLinkman = null;
		List dictionary = null;
		try {
			dunList = (List) DataAccessor.query("dunRent.getDunRentInfo",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			dictionary = (List<Map>) DictionaryUtil.getDictionary("催收结果");
			dunRecordList = (List) DataAccessor.query(
					"dunTask.getFiveDunRecords", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			
			// 联系人
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkmanList", custLinkman);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("cust_id", context.contextMap.get("cust_id"));
		outputMap.put("cust_code", context.contextMap.get("cust_code").toString());
		outputMap.put("dictionary", dictionary);
		outputMap.put("dunList", dunList);
		outputMap.put("dunRecordList", dunRecordList);
		Output.jspOutput(outputMap, context, "/dun/dunRentInfo.jsp");
	}
	
	/**
	 * 给联系人发送短信。
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void sendMessage(Context context) {
		Map outputMap = new HashMap();
		Map codeAndName =new HashMap();
		List send = new ArrayList();
		String returnStr=null;
		try {
			codeAndName = (Map)DataAccessor.query("dunRent.codeAndName", context.contextMap, DataAccessor.RS_TYPE.MAP);
			String linkManMobile = context.contextMap.get("linkManMobile").toString();
			String[] mobile = linkManMobile.split(",");
			for(int i = 0; i<mobile.length;i++){
				Map sendManager = new HashMap();
				
				sendManager.put("SENDTYPE", 1);
				sendManager.put("MTEL", mobile[i]);
				sendManager.put("MESSAGE", codeAndName.get("LEASE_CODE")+""+codeAndName.get("CUST_NAME")+" - 敬爱的客户,本期租金即将到期请尽速缴款,裕融租赁提醒您。0512-80983566业管部");
				sendManager.put("STATE", 8);
				sendManager.put("LEASE_CODE", codeAndName.get("LEASE_CODE"));
				send.add(sendManager);
				SendMsg sendMsg = new SendMsg();
				returnStr = sendMsg.SendSMSMsg(context, send);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("returnStr", returnStr);
		Output.jsonOutput(outputMap, context);
	}

}
