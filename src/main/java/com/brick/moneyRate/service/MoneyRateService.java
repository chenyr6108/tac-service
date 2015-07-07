package com.brick.moneyRate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
import com.brick.baseManage.service.BusinessLog;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class MoneyRateService extends AService {
	Log logger = LogFactory.getLog(MoneyRateService.class);

	/**
	 * 利率管理页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getShow(Context context) {
		Map outputMap = new HashMap();
		List moneyRateList = null;
		context.contextMap.put("log_type", "利率管理");
		
		//查询日志操作
		List moneyRateLog=null;
		try {
			moneyRateList = (List) DataAccessor.query("moneyRate.queryAllNew",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("showNamesList",(List) DataAccessor.query("moneyRate.queryShowName",
					context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
			moneyRateLog=(List) DataAccessor.query("moneyRate.queryAllLog",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("TAX_PLAN_CODE", context.contextMap.get("TAX_PLAN_CODE"));
		outputMap.put("moneyRateLog", moneyRateLog);
		outputMap.put("moneyRateList", moneyRateList);
		Output.jspOutput(outputMap, context, "/moneyRate/moneyRateManage.jsp");
	}

	/**
	 * 修改利率
	 * 
	 * @param context
	 */
	public void updateMoneyRateByRateValue(Context context) {
		 //Map outputMap = new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			sqlMapper.update("moneyRate.updateMoneyRateByRateValue",context.contextMap);
//			DataAccessor.execute("moneyRate.updateMoneyRateByRateValue",
//					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			String s_employeeId=context.request.getSession().getAttribute("s_employeeId").toString();
			String s_employeeName=context.request.getSession().getAttribute("s_employeeName").toString();
			
			String filed_name=context.request.getParameter("filed_name");
			String show_name=context.request.getParameter("show_name");
			String rate_value=context.request.getParameter("rate_value");
			String rate_valueOld=context.request.getParameter("rate_valueOld");
			
			Long creditId=null;
			Long contractId=null;
			String logType="利率管理";//日志类型
			String logTitle="利率值修改";//日志标题
			String logCode=String.valueOf(context.contextMap.get("TAX_PLAN_CODE"));
			Long otherId=null;
			
			 SimpleDateFormat currDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date curr=new Date();
			 String currTime=currDate.format(curr);
			String memo="利率管理中，"+s_employeeName+"在"+currTime+"将名称为"+filed_name+"的利率值从"+rate_valueOld+"改为"+rate_value+"。";//备注
			Integer state=1;//状态 1 使用中 2未使用
			Long userId=Long.valueOf(s_employeeId);//创建人
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId,sqlMapper,(String)context.contextMap.get("IP"));
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=moneyRate.getShow");
		Output.jsonOutput(null, context);
	}
	
	/**
	 * 修改备注
	 * 
	 * @param context
	 */
	public void updateMoneyRateByRemark(Context context) {
		// Map outputMap = new HashMap();
		try {
			DataAccessor.execute("moneyRate.updateMoneyRateByRemark",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(null, context);
	}
	
	/**
	 * 于秋辰
	 * 查询利率  （已经百分比）
	 * @param context
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void queryMoneyRate(Context context) throws Exception{
		List moneyRateList = null ;
		moneyRateList = (List) DataAccessor.query("moneyRate.queryAll",
				null, DataAccessor.RS_TYPE.LIST);
		if(moneyRateList != null && moneyRateList.size() > 0){
			for(int i=0; i<moneyRateList.size(); i++){
				Map temp = (Map) moneyRateList.get(i) ;
				context.contextMap.put(temp.get("FILED_NAME"),(Double)temp.get("RATE_VALUE") / 100) ;//百分比
			}
		}
	}
	/**
	 * 修改起始时间或结束时间
	 * yqc
	 * @param context
	 */
	public void updateMoneyRateStartOREndDate(Context context) {
		try {
			DataAccessor.execute("moneyRate.updateMoneyRateStartOREndDate",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(null, context);
	}
	/**
	 * 增加利率
	 * yqc
	 * @param context
	 */
	public void createRateConfig(Context context) {
		List errList = context.errList ;
		try {
			DataAccessor.execute("moneyRate.createRateConfig",
					context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(null, context);
	}
	/**
	 * 删除利率
	 * yqc
	 * @param context
	 */
	public void deleteRateConfig(Context context) {
		List errList = context.errList ;
		try {
			DataAccessor.execute("moneyRate.deleteRateConfig",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(null, context);
	}
	/**
	 * 查询重复利率名称个数
	 * yqc
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryFiledNameCount(Context context) {
		Map outputMap = new HashMap();
		try {
			outputMap.put("count", DataAccessor.query("moneyRate.queryFiledNameCount", context.contextMap, RS_TYPE.OBJECT)) ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context) ;
	}

	/*
	 * Add by Michael 2012 1/9
	 * 测试表费用设置
	 */

	@SuppressWarnings("unchecked")
	public void queryFeeAll(Context context) {
		Map outputMap = new HashMap();
		List feeList = null;
		
		//查询日志操作
		List feeSetLog=null;
		try {
			feeList = (List) DataAccessor.query("paylistFeeSet.queryAllFeeSet",
					null, DataAccessor.RS_TYPE.LIST);
			feeSetLog=(List) DataAccessor.query("paylistFeeSet.queryAllFeeSetLog",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("feeList", feeList);
		outputMap.put("feeSetLog", feeSetLog);
		Output.jspOutput(outputMap, context, "/payListFeeSet/payListFeeSet.jsp");
	}	
	
	/**
	 * 增加费用
	 * @param context
	 */
	public void createFeeConfig(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String s_employeeId=context.request.getSession().getAttribute("s_employeeId").toString();
			context.contextMap.put("s_employeeId", s_employeeId);
			
			sqlMapper.insert("paylistFeeSet.createFeeConfig",context.contextMap);
			context.contextMap.put("memo", "新增费用显示名："+context.contextMap.get("create_show_name")+";列名："+context.contextMap.get("create_filed_name"));
			sqlMapper.insert("paylistFeeSet.createFeeConfigLog",context.contextMap);
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			errList.add("系统设置--费用设置增加错误!请联系管理员");
			LogPrint.getLogStackTrace(e, logger);
		}finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=moneyRate.queryFeeAll");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 *将费用作废
	 */
	public void deleteFeeConfig(Context context) {
		List errList = context.errList ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			sqlMapper.update("paylistFeeSet.deleteFeeConfig",context.contextMap);
			String s_employeeId=context.request.getSession().getAttribute("s_employeeId").toString();
			context.contextMap.put("s_employeeId", s_employeeId);
			context.contextMap.put("memo", "作废费用，ID为："+context.contextMap.get("id"));
			sqlMapper.insert("paylistFeeSet.createFeeConfigLog",context.contextMap);
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Output.jsonOutput(null, context);
	}		
	
	
	
	/**
	 * 税率方案配置管理页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getTaxConfigManage(Context context) {
		Map outputMap = new HashMap();
		List moneyRatePlanList = null;
		try {
			moneyRatePlanList = (List) DataAccessor.query("moneyRate.queryAllTaxPlan",
					null, DataAccessor.RS_TYPE.LIST);
			outputMap.put("moneyRatePlanList", moneyRatePlanList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/moneyRate/taxPlanConfig.jsp");
	}
	
	
}
