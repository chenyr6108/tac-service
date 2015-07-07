package com.brick.customer.service;

import java.text.DecimalFormat;
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

/**
 * 2010-05-17 合同客户HAB级别表
 * 
 * @author yqc
 * 
 */
public class ContractCustomerHAB extends AService {
	Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 管现页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void query(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		DataWrap dw = null;
		Map rsMap = new HashMap();
		context.contextMap.put("id", context.contextMap.get("s_employeeId"));
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("job", rsMap.get("JOB")) ;
			dw = (DataWrap) DataAccessor.query("contractCustomerHAB.queryNew", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			
			//如果该合同已经拨款
			List<Map<String,Object>> payDetailList=null;
			List<Map> result=(List<Map>)dw.getRs();
			DecimalFormat df=new DecimalFormat("0.00");
			for(int i=0;i<result.size();i++) {
				context.contextMap.put("CREDIT_ID",result.get(i).get("CREDIT_ID"));
				payDetailList=(List<Map<String,Object>>)DataAccessor.query("contractCustomerHAB.payDetail",context.contextMap,DataAccessor.RS_TYPE.LIST);
				for(int j=0;payDetailList!=null&&j<payDetailList.size();j++) {
					if(result.get(i).get("CREDIT_ID").equals(payDetailList.get(j).get("CREDIT_ID"))) {
						if(df.format(result.get(i).get("SQPRICE")).equals(df.format(payDetailList.get(j).get("PAY_MONEY")))) {
							result.get(i).put("STATE",1);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("合同用户HAB分级管理页错误!请联系管理员") ;
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("HABLevel", context.contextMap.get("HABLevel"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/customer/contractCustomerHABManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * AJAX修改HAB字段
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void update(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		try {
			DataAccessor.execute("contractCustomerHAB.updateNew", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE) ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改HAB级别错误!请联系管理员");
		}
		if(errList.isEmpty()){
			outputMap.put("HAB", context.contextMap.get("CONTRACT_HAB")) ;
			Output.jsonOutput(outputMap, context) ;
		}
	}
	
}
