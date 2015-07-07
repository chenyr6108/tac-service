package com.brick.support.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * @date 2011 05 03
 * @author fanyilong
 * @version 1.0
 */
public class ChangeSenson extends BaseCommand {
	Log logger = LogFactory.getLog(ChangeSenson.class);
	
	
    /**
     * 查找出所有报告，用来客户经理转移
     */
    @SuppressWarnings("unchecked")
    public void queryReport(Context context){
    	Map<String, Object> outputMap = new HashMap<String, Object>();
		DataWrap dw = null;
		boolean manageRole = false;
		try {

			dw = (DataWrap) DataAccessor.query(
					"creditReportManage.getAllSenson", context.contextMap,
					DataAccessor.RS_TYPE.PAGED);
			manageRole = baseService.checkAccessForResource("batchChangeManager", String.valueOf(context.contextMap.get("s_employeeId")));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		outputMap.put("dw", dw);
		outputMap.put("manageRole", manageRole);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("creditStauts", context.contextMap.get("creditStauts"));
		Output.jspOutput(outputMap, context, "/credit/creditChangeSenson.jsp");
	
    }
    
    /**
     * 查找出所有客户经理，单个的除了他自己本身,不单个的查找所有
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void querySenson(Context context){
    	Map outputMap = new HashMap();
		List sensonList = null;

		try {
			context.contextMap.put("dic_type", "员工职位");
			context.contextMap.put("dic_flag1", "业务员");
			context.contextMap.put("dic_flag2", "业务助理");
			if(context.contextMap.get("sensorid")!=null){
				
				sensonList =(List) DataAccessor.query(
						"creditReportManage.getSensonWithOutself", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			}else{
				sensonList =(List) DataAccessor.query(
						"creditReportManage.getSenson", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		outputMap.put("sensonList", sensonList);
		Output.jsonOutput(outputMap, context);
	
    }
    
    /**
     * 更改客户经理
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void changeSensor(Context context){
    	
    	SqlMapClient sqlMapper = null;
    	context.contextMap.put("rent", "租金");
    	
    	//添加系统日志参数。
    	String logType = "客户经理转移";
		String logTitle = "转移";
		Integer state = new Integer(1);
		Long userId = new Long(context.contextMap.get("s_employeeId").toString());
		Long otherId = new Long("0");
		String logCode = null;
    	
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			if(context.contextMap.get("credit_idmore")==null||"".equals(context.contextMap.get("credit_idmore"))){
				
				
				if(new ChangeSenson().isOver(context.contextMap)){
					sqlMapper.update("creditReportManage.updateSensorContract", context.contextMap);
					sqlMapper.update("creditReportManage.updateSensorCredit", context.contextMap);
					
					String custCode = context.contextMap.get("cust_code").toString();
					Map sensoro = new HashMap();
					Map sensorn = new HashMap();
					sensoro.put("userid", context.contextMap.get("sensorIdForisOver"));
					sensorn .put("userid", context.contextMap.get("sensor"));
					String sensorOld = ((Map)DataAccessor.query("creditReportManage.getSensonNameById", sensoro,DataAccessor.RS_TYPE.MAP)).get("NAME").toString();
					String sensorNew = ((Map)DataAccessor.query("creditReportManage.getSensonNameById", sensorn,DataAccessor.RS_TYPE.MAP)).get("NAME").toString();
					Long creditId = new Long(context.contextMap.get("credit_id10").toString());
					Long contractId = new Long("0");
					String memo = "客户编号"+custCode+"的客户经理由"+sensorOld+"转移为"+sensorNew;
					
					
					BusinessLog.addBusinessLog(creditId,contractId,logType,logTitle,logCode,memo,state,userId,otherId,sqlMapper,(String)context.contextMap.get("IP"));
					sqlMapper.commitTransaction();
				}
				
				
			}else{
				String k = context.contextMap.get("credit_idmore").toString();
				String n[] = k.split(",");
				
				String custCode = "";
				String memo = "";
				String sensorAll = "";
				Long creditId = new Long("0");
				Long contractId = new Long("0");
				
				for(int i = 0; i<n.length;i++){
					Map map = new HashMap();
					map.put("sensor", context.contextMap.get("sensor"));
					map.put("rent", "租金");
					map.put("credit_id10", n[i]);
					map.put("sensorIdForisOver", n[i+1]);
					
					if(new ChangeSenson().isOver(map)){
						sqlMapper.update("creditReportManage.updateSensorContract", map);
						sqlMapper.update("creditReportManage.updateSensorCredit", map);
						custCode+=(n[i+2]+",");
					}
					Map sensoro = new HashMap();
					sensoro.put("userid", context.contextMap.get("sensor"));
					sensorAll = ((Map)DataAccessor.query("creditReportManage.getSensonNameById", sensoro,DataAccessor.RS_TYPE.MAP)).get("NAME").toString();
		
					i+=2;
				}
				memo = "客户编号"+custCode+"的客户经理都转移为"+sensorAll;
				BusinessLog.addBusinessLog(creditId,contractId,logType,logTitle,logCode,memo,state,userId,otherId,sqlMapper,(String)context.contextMap.get("IP"));
				sqlMapper.commitTransaction();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		
		context.contextMap.put("content", context.contextMap.get("search_content"));
		//Output.jspSendRedirect(context, "defaultDispatcher?__action=ChangeSenson.queryReport");
		this.queryReport(context);
	
    }
    
    /**
     * 查找出报告中所有已经存在的客户经理
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void querySensonOn(Context context){
    	Map outputMap = new HashMap();
		List sensonList = null;

		try {		
			context.contextMap.put("dic_type", "员工职位");
			context.contextMap.put("dic_flag1", "业务员");
			context.contextMap.put("dic_flag2", "业务助理");
				sensonList =(List) DataAccessor.query(
						"creditReportManage.getSensonOn", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		outputMap.put("sensonList", sensonList);
		Output.jsonOutput(outputMap, context);
	
    }

    /**
     * 批量更改客户经理
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void changeSensorByLot(Context context){
    	
    	SqlMapClient sqlMapper = null;
    	context.contextMap.put("rent", "租金");
    	
    	//添加日志参数
    	String logType = "客户经理转移";
		String logTitle = "转移";
		Integer state = new Integer(1);
		Long userId = new Long(context.contextMap.get("s_employeeId").toString());
		Long otherId = new Long("0");
		String logCode = null;
    	
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			context.contextMap.put("sensorIdForisOver", context.contextMap.get("sensor1"));

			if(new ChangeSenson().isOver(context.contextMap)){
				
				sqlMapper.update("creditReportManage.updateSensorContractByLot", context.contextMap);
				sqlMapper.update("creditReportManage.updateSensorCreditByLot", context.contextMap);
			}
			
			Map sensoro = new HashMap();
			Map sensorn = new HashMap();
			sensoro.put("userid", context.contextMap.get("sensor1"));
			sensorn.put("userid", context.contextMap.get("sensor2"));
			String sensorOld = ((Map)DataAccessor.query("creditReportManage.getSensonNameById", sensoro,DataAccessor.RS_TYPE.MAP)).get("NAME").toString();
			String sensorNew = ((Map)DataAccessor.query("creditReportManage.getSensonNameById", sensorn,DataAccessor.RS_TYPE.MAP)).get("NAME").toString();
			Long creditId = new Long("0");
			Long contractId = new Long("0");
			String memo = "将客户经理为"+sensorOld+"的转移为"+sensorNew;
			
			
			BusinessLog.addBusinessLog(creditId,contractId,logType,logTitle,logCode,memo,state,userId,otherId,sqlMapper,(String)context.contextMap.get("IP"));
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		

		Output.jspSendRedirect(context, "defaultDispatcher?__action=ChangeSenson.queryReport");
	
    }
    
    /**
     * 判断某客户经理下我还款是否还款完毕
     * @param context
     */
    @SuppressWarnings("unchecked")
    public boolean isOver(Map contextMap){
    	
    	boolean b = false;
    	
    	List<Map> resultList = null;
    	
    	try {
			resultList = (List<Map>) DataAccessor.query(
					"creditReportManage.getBackMoneyOverOrNot", contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(resultList.size()<1){
			b = true;
		}else{
			
			for(int i = 0; i<resultList.size();i++){
				
				
				if(!((resultList.get(i).get("MONTH_PRICE").equals(resultList.get(i).get("REAL_PRICE"))&&
						(resultList.get(i).get("PERIOD_NUM").equals(resultList.get(i).get("RECD_PERIOD")))))){
					
					b = true;
					break;
				}
			}
		}
    	
    	return b;
	
    }
}
