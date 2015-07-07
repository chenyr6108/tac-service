package com.brick.support.service;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;



/**
 * 
 * @author wuzd
 * @date 2010,7,29
 */
public class SupportRiskDelete extends AService{	
	Log logger = LogFactory.getLog(SupportRiskDelete.class);
	/**
	 * 查看风控
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void riskAuditAfter(Context context) {
		Map outputMap = new HashMap();
		@SuppressWarnings("unused")
		List errList = context.errList;
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("riskAudit.deleteRisk", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("wind_state", context.contextMap.get("wind_state"));
		outputMap.put("prc_node", context.contextMap.get("PRC_NODE"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("CREDIT_TYPE"));
		Output.jspOutput(outputMap, context, "/support/supportRiskDeleteManager.jsp");
	}
	/**
	 * 修改风控结果不通过到不通过附条件管理页
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateCreditForResult(Context context) {
		Map outputMap = new HashMap();
		@SuppressWarnings("unused")
		List errList = context.errList;
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("riskAudit.updateCreditForResult", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap, context, "/support/supportUpdateCreditForResult.jsp");
	}
	
	/**
	 * 修改风控结果
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void supportUpdateCredit(Context context) {
		Map outputMap = new HashMap();
		try {
			DataAccessor.execute("riskAudit.updateRiskResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			DataAccessor.execute("riskAudit.updateCreditResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=supportRiskDelete.updateCreditForResult");
		}
	}
	/**
	 * 修改风控结果ajax
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void supportUpdateCreditAjax(Context context) {
		Map outputMap = new HashMap();
		try {
			DataAccessor.execute("riskAudit.updateRiskResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			DataAccessor.execute("riskAudit.updateCreditResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			outputMap.put("updateresult", "修改风控成功！");
		} catch (Exception e) {
			outputMap.put("updateresult", "修改风控失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * ajax查测该风控是否生成合同
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void supportRiskDelete(Context context) {
		Map outputMap = new HashMap();
		Map rentMap = new HashMap();
		try {
			//是否生成合同
			rentMap = (Map) DataAccessor.query("supportRiskDelete.checkRent",context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			outputMap.put("rentMap", rentMap.get("CNT"));
			Output.jsonOutput(outputMap, context);
		}
	}	
	/**
	 * 删除风控
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delete(Context context) { 
		Map outputMap = new HashMap();
		Map riskMap= new HashMap();
		Map creditMap= new HashMap();
		Map countMap= new HashMap();
		List allRiskList = null;
		try {
			//修改审核意见表
			DataAccessor.execute("supportRiskDelete.updateCreditMemo", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			//该风控是否生成结果
			riskMap = (Map) DataAccessor.query("supportRiskDelete.checkResult",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (Integer.parseInt(riskMap.get("STATE").toString())==0) {
				//没有结果 删除风控
				DataAccessor.execute("supportRiskDelete.deleteRisk", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				//没有结果 打回资信
				creditMap = (Map) DataAccessor.query("supportRiskDelete.checkCredit",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("CREDIT_ID", creditMap.get("CREDIT_ID"));
				DataAccessor.execute("supportRiskDelete.updateCredit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			} else {
				//有结果
				creditMap = (Map) DataAccessor.query("supportRiskDelete.checkCredit",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("CREDIT_ID", creditMap.get("CREDIT_ID"));
				countMap = (Map) DataAccessor.query("supportRiskDelete.countRisk",context.contextMap, DataAccessor.RS_TYPE.MAP);
				if (Integer.parseInt(countMap.get("CNTTT").toString())==1) {
					//只有一条风控 删除风控 打回资信
					DataAccessor.execute("supportRiskDelete.deleteRisk", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					DataAccessor.execute("supportRiskDelete.updateCredit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				} else {
					//有多条风控 删除风控 打回资信
					allRiskList = (List) DataAccessor.query("supportRiskDelete.allRisk",context.contextMap, DataAccessor.RS_TYPE.LIST);
					int i=0;
					for (Iterator iterator = allRiskList.iterator(); iterator.hasNext();) {
						Map object = (Map) iterator.next();
						if (Integer.parseInt(object.get("STATE").toString())!=0) {
							i++;
						}		
					}
					if (i!=0) {
						DataAccessor.execute("supportRiskDelete.deleteRisk", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						DataAccessor.execute("supportRiskDelete.updateCredit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					} else {
						DataAccessor.execute("supportRiskDelete.deleteRisk", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						DataAccessor.execute("supportRiskDelete.updateCredit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
			}
			//清空合同号
			DataAccessor.execute("supportRiskDelete.deleteCreditLeaseCode", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("riskMap",riskMap);
		Output.jspSendRedirect(context,"defaultDispatcher?__action=supportRiskDelete.riskAuditAfter");
	}	
}
