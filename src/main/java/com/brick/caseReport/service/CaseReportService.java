/*****************************************************************************
 * Name : CaseReportService.java
 *
 * DESCRIPTION :
 * 
 *
 * REVISION HISTORY:
 *
 *       Date         Author       Description
 *   ------------   ----------   ---------------
 *    2012/03/16      ShenQi	   new program         
 *****************************************************************************/
package com.brick.caseReport.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.caseReport.to.CaseReportTO;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;

public class CaseReportService extends AService {
	
	Log logger = LogFactory.getLog(CaseReportService.class);
	
	@SuppressWarnings("unchecked")
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		List<String> errList=context.errList;
		List<Map> dataResult=null;//数据结果
		List<Map> dataTotalResult=null;
		Map<String,String> dateResult=null;//日期结果
		Map outputMap=new HashMap();
		
		//List<Map<String,String>> companys=null;
		
		try {
			//初始化日期是拿昨天-------------------------------
			if(context.contextMap.get("DATE")==null) {
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DATE,-1);
				String date=DateUtil.dateToString(cal.getTime(), "yyyy-MM-dd");
				context.contextMap.put("DATE",date);
			}
			//------------------------------------------------
			
			//read me原先各区案况功能是过滤条件选择几号,就遍历此日期的6月前到当月数据,现在改成除当前月以外,其他月份都拿最后天的数据
			//获得所选日期以及前6个月的数据----------------------------------------------------------------------------------------BEGIN
//			dataResult=(List<Map>)DataAccessor.query("caseReportService.query",context.contextMap,RS_TYPE.LIST);
			//获得所选日期以及前6个月的数据----------------------------------------------------------------------------------------END
			
			
			//获得所选日期以及前6个月的日期----------------------------------------------------------------------------------------BEGIN
//			dateResult=(Map<String,String>)DataAccessor.query("caseReportService.queryDate",context.contextMap,RS_TYPE.OBJECT);
			//获得所选日期以及前6个月的日期----------------------------------------------------------------------------------------END
			
			
			//获得所选日期以及前6个月的总计----------------------------------------------------------------------------------------BEGIN
//			dataTotalResult=(List<Map>)DataAccessor.query("caseReportService.queryTotal",context.contextMap,RS_TYPE.LIST);
			
			Map<String,String> param=new HashMap<String,String>();
			param.put("DATE",(String)context.contextMap.get("DATE"));
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dataResult=(List<Map>)DataAccessor.query("caseReportService.query1",param,RS_TYPE.LIST);
			dataTotalResult=(List<Map>)DataAccessor.query("caseReportService.queryTotal1",param,RS_TYPE.LIST);
			
//			outputMap.put("DATE_CURRENT_MONTH",dateResult.get("DATE_CURRENT_MONTH"));
//			outputMap.put("DATE_ONE_MONTH_AGO",dateResult.get("DATE_ONE_MONTH_AGO"));
//			outputMap.put("DATE_TWO_MONTH_AGO",dateResult.get("DATE_TWO_MONTH_AGO"));
//			outputMap.put("DATE_THREE_MONTH_AGO",dateResult.get("DATE_THREE_MONTH_AGO"));
//			outputMap.put("DATE_FOUR_MONTH_AGO",dateResult.get("DATE_FOUR_MONTH_AGO"));
//			outputMap.put("DATE_FIVE_MONTH_AGO",dateResult.get("DATE_FIVE_MONTH_AGO"));
//			outputMap.put("DATE_SIX_MONTH_AGO",dateResult.get("DATE_SIX_MONTH_AGO"));
			
			outputMap.put("DATE_CURRENT_MONTH",param.get("DATE"));
			outputMap.put("DATE_ONE_MONTH_AGO",param.get("LAST_DATE_1"));
			outputMap.put("DATE_TWO_MONTH_AGO",param.get("LAST_DATE_2"));
			outputMap.put("DATE_THREE_MONTH_AGO",param.get("LAST_DATE_3"));
			outputMap.put("DATE_FOUR_MONTH_AGO",param.get("LAST_DATE_4"));
			outputMap.put("DATE_FIVE_MONTH_AGO",param.get("LAST_DATE_5"));
			outputMap.put("DATE_SIX_MONTH_AGO",param.get("LAST_DATE_6"));
			//获得所选日期以及前6个月的总计----------------------------------------------------------------------------------------END
			
			
			//获得办事处的drop down List
			//context.contextMap.put("decp_id", "2");//2代表的是拿分公司
			//companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("数据分析--案况报表列表错误!请联系管理员");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		if(errList.isEmpty()) {
			outputMap.put("DATA_RESULT",dataResult);
			outputMap.put("DATA_TOTAL_RESULT",dataTotalResult);
			
			outputMap.put("DATE",context.contextMap.get("DATE"));
			outputMap.put("DECP_ID",context.contextMap.get("DECP_ID"));
			outputMap.put("DECP_NAME_CN",context.contextMap.get("DECP_NAME_CN"));
			
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			outputMap.put("date",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			Output.jspOutput(outputMap, context, "/caseReport/caseReport.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case report start  --------------------");
		}
		Map paramMap=new HashMap();
		
		List<Map<String,String>> companys=null;
		
		//int countOfInfo=0;
		List<HashMap> countOfInfoGroupByCmpy=null;
		List<HashMap> amountOfInfoGroupByCmpy=null;
		
		List<HashMap> countOfHasAccessGroupByCmpy=null;
		List<HashMap> amountOfHasAccessGroupByCmpy=null;
		
		List<HashMap> countOfAuditGroupByCmpy=null;
		List<HashMap> amountOfAuditGroupByCmpy=null;
		
		List<HashMap> countOfApproveGroupByCmpy=null;
		List<HashMap> amountOfApproveGroupByCmpy=null;
		try {
			//计算资料栏位的总数量
			//countOfInfo=(Integer)DataAccessor.query("caseReportService.getCountOfInfo",null,DataAccessor.RS_TYPE.OBJECT);
			//计算资料栏位的总数量GROUP BY 办事处
			countOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfInfoGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			//计算资料栏位的金额总数GROUP BY 办事处
			amountOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfInfoGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			
			//已訪廠暂时没有
			countOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfHasAccessGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			amountOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfHasAccessGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			
			//计算审核中栏位的总数量GROUP BY 办事处
			countOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfAuditGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			//计算审核中栏位的金额总数GROUP BY 办事处
			amountOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfAuditGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			
			//计算已核准栏位的总数量
			//countOfApprove=(Integer)DataAccessor.query("caseReportService.getCountOfApprove",null,DataAccessor.RS_TYPE.OBJECT);
			//计算已核准栏位的总数量GROUP BY 办事处
			countOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfApproveGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			//计算已核准栏位的金额总数GROUP BY 办事处
			amountOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfApproveGroupByCmpy",null,DataAccessor.RS_TYPE.LIST);
			
			
			
			//获得所有办事处
			paramMap.put("decp_id", "2");//2代表的是拿分公司
			companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", paramMap, DataAccessor.RS_TYPE.LIST);
			
			//为所有办事处新增TO
			for(int i=0;companys!=null&&i<companys.size();i++) {
				paramMap.put(String.valueOf(companys.get(i).get("DECP_ID")), new CaseReportTO(String.valueOf(companys.get(i).get("DECP_ID")),companys.get(i).get("DECP_NAME_CN")));
			}
			
			//加入资料栏位的信息--------------------------------------------------------------
			for(int i=0;countOfInfoGroupByCmpy!=null&&i<countOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoCount(String.valueOf(countOfInfoGroupByCmpy.get(i).get("INFO_COUNT")));
				} else {
					
				}
			}

			for(int i=0;amountOfInfoGroupByCmpy!=null&&i<amountOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoAmount(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("INFO_AMOUNT")));
				} else {
					
				}
			}
			
			//加入已訪廠栏位信息--------------------------------------------------------------
			for(int i=0;countOfHasAccessGroupByCmpy!=null&&i<countOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessCount(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfHasAccessGroupByCmpy!=null&&i<amountOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessAmount(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_AMOUNT")));
				} else {
					
				}
			}
			//加入审核中栏位信息--------------------------------------------------------------
			for(int i=0;countOfAuditGroupByCmpy!=null&&i<countOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditCount(String.valueOf(countOfAuditGroupByCmpy.get(i).get("AUDIT_COUNT")));
				} else {
					
				}
			}
			DecimalFormat df=new DecimalFormat("0.00");
			for(int i=0;amountOfAuditGroupByCmpy!=null&&i<amountOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditAmount(String.valueOf(df.format(amountOfAuditGroupByCmpy.get(i).get("AUDIT_AMOUNT"))));
				} else {
					
				}
			}
			
			//加入已核准栏位信息--------------------------------------------------------------
			for(int i=0;countOfApproveGroupByCmpy!=null&&i<countOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveCount(String.valueOf(countOfApproveGroupByCmpy.get(i).get("APPROVE_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfApproveGroupByCmpy!=null&&i<amountOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveAmount(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("APPROVE_AMOUNT")));
				} else {
					
				}
			}
			
			//插入数据到表T_CASE_REPORT
			for(int i=0;companys!=null&&i<companys.size();i++) {
				Map<String,String> contextMap=new HashMap<String,String>();
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(companys.get(i).get("DECP_ID")));
				
				//设定插入的数据
				contextMap.put("DECP_ID",to.getDecpId());
				contextMap.put("DECP_NAME_CN",to.getDecpNameCn());
				contextMap.put("INFO_COUNT",to.getInfoCount());
				contextMap.put("INFO_AMOUNT",to.getInfoAmount());
				contextMap.put("HAS_ACCESS_COUNT",to.getHasAccessCount());
				contextMap.put("HAS_ACCESS_AMOUNT",to.getHasAccessAmount());
				contextMap.put("AUDIT_COUNT",to.getAuditCount());
				contextMap.put("AUDIT_AMOUNT",to.getAuditAmount());
				contextMap.put("APPROVE_COUNT",to.getApproveCount());
				contextMap.put("APPROVE_AMOUNT",to.getApproveAmount());
				
				DataAccessor.execute("caseReportService.batchUpdate",contextMap,OPERATION_TYPE.INSERT);
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("batch job for case report end  --------------------");
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			throw e;
		}
	}
	
	private String getLastDayOfMonth(int i) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM"); 
        Calendar calendar=Calendar.getInstance(); 
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-i); 
        String month= sdf.format(calendar.getTime());
        int lastDay=calendar.getActualMaximum(calendar.DAY_OF_MONTH);
        String date=month+"-"+String.valueOf(lastDay);
        return date;
	}
	
	@Transactional(rollbackFor=Exception.class)//各区案况batch job for设备
	public void batchJobForEqu() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case report设备 start  --------------------");
		}
		Map paramMap=new HashMap();
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("productionType",Constants.PRODUCTION_TYPE_1);
		
		List<Map<String,String>> companys=null;
		
		//int countOfInfo=0;
		List<HashMap> countOfInfoGroupByCmpy=null;
		List<HashMap> amountOfInfoGroupByCmpy=null;
		
		List<HashMap> countOfHasAccessGroupByCmpy=null;
		List<HashMap> amountOfHasAccessGroupByCmpy=null;
		
		List<HashMap> countOfAuditGroupByCmpy=null;
		List<HashMap> amountOfAuditGroupByCmpy=null;
		
		List<HashMap> countOfApproveGroupByCmpy=null;
		List<HashMap> amountOfApproveGroupByCmpy=null;
		try {
			//计算资料栏位的总数量GROUP BY 办事处
			countOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算资料栏位的金额总数GROUP BY 办事处
			amountOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//已訪廠暂时没有
			countOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			amountOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算审核中栏位的总数量GROUP BY 办事处
			countOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算审核中栏位的金额总数GROUP BY 办事处
			amountOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算已核准栏位的总数量GROUP BY 办事处
			countOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算已核准栏位的金额总数GROUP BY 办事处
			amountOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//获得所有办事处
			paramMap.put("decp_id", "2");//2代表的是拿分公司
			companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", paramMap, DataAccessor.RS_TYPE.LIST);
			
			//为所有办事处新增TO
			for(int i=0;companys!=null&&i<companys.size();i++) {
				paramMap.put(String.valueOf(companys.get(i).get("DECP_ID")), new CaseReportTO(String.valueOf(companys.get(i).get("DECP_ID")),companys.get(i).get("DECP_NAME_CN")));
			}
			
			//加入资料栏位的信息--------------------------------------------------------------
			for(int i=0;countOfInfoGroupByCmpy!=null&&i<countOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoCount(String.valueOf(countOfInfoGroupByCmpy.get(i).get("INFO_COUNT")));
				} else {
					
				}
			}

			for(int i=0;amountOfInfoGroupByCmpy!=null&&i<amountOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoAmount(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("INFO_AMOUNT")));
				} else {
					
				}
			}
			
			//加入已訪廠栏位信息--------------------------------------------------------------
			for(int i=0;countOfHasAccessGroupByCmpy!=null&&i<countOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessCount(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfHasAccessGroupByCmpy!=null&&i<amountOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessAmount(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_AMOUNT")));
				} else {
					
				}
			}
			//加入审核中栏位信息--------------------------------------------------------------
			for(int i=0;countOfAuditGroupByCmpy!=null&&i<countOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditCount(String.valueOf(countOfAuditGroupByCmpy.get(i).get("AUDIT_COUNT")));
				} else {
					
				}
			}
			DecimalFormat df=new DecimalFormat("0.00");
			for(int i=0;amountOfAuditGroupByCmpy!=null&&i<amountOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditAmount(String.valueOf(df.format(amountOfAuditGroupByCmpy.get(i).get("AUDIT_AMOUNT"))));
				} else {
					
				}
			}
			
			//加入已核准栏位信息--------------------------------------------------------------
			for(int i=0;countOfApproveGroupByCmpy!=null&&i<countOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveCount(String.valueOf(countOfApproveGroupByCmpy.get(i).get("APPROVE_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfApproveGroupByCmpy!=null&&i<amountOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveAmount(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("APPROVE_AMOUNT")));
				} else {
					
				}
			}
			
			//插入数据到表T_CASE_REPORT
			for(int i=0;companys!=null&&i<companys.size();i++) {
				Map<String,String> contextMap=new HashMap<String,String>();
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(companys.get(i).get("DECP_ID")));
				
				//设定插入的数据
				contextMap.put("DECP_ID",to.getDecpId());
				contextMap.put("DECP_NAME_CN",to.getDecpNameCn());
				contextMap.put("INFO_COUNT",to.getInfoCount());
				contextMap.put("INFO_AMOUNT",to.getInfoAmount());
				contextMap.put("HAS_ACCESS_COUNT",to.getHasAccessCount());
				contextMap.put("HAS_ACCESS_AMOUNT",to.getHasAccessAmount());
				contextMap.put("AUDIT_COUNT",to.getAuditCount());
				contextMap.put("AUDIT_AMOUNT",to.getAuditAmount());
				contextMap.put("APPROVE_COUNT",to.getApproveCount());
				contextMap.put("APPROVE_AMOUNT",to.getApproveAmount());
				
				DataAccessor.execute("caseReportService.batchUpdateEqu",contextMap,OPERATION_TYPE.INSERT);
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("batch job for case report设备 end  --------------------");
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			throw e;
		}
	}
	
	@Transactional(rollbackFor=Exception.class)//各区案况batch job for重车
	public void batchJobForMotor() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case report重车 start  --------------------");
		}
		Map paramMap=new HashMap();
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("productionType",Constants.PRODUCTION_TYPE_2);
		
		List<Map<String,String>> companys=null;
		
		//int countOfInfo=0;
		List<HashMap> countOfInfoGroupByCmpy=null;
		List<HashMap> amountOfInfoGroupByCmpy=null;
		
		List<HashMap> countOfHasAccessGroupByCmpy=null;
		List<HashMap> amountOfHasAccessGroupByCmpy=null;
		
		List<HashMap> countOfAuditGroupByCmpy=null;
		List<HashMap> amountOfAuditGroupByCmpy=null;
		
		List<HashMap> countOfApproveGroupByCmpy=null;
		List<HashMap> amountOfApproveGroupByCmpy=null;
		try {
			//计算资料栏位的总数量GROUP BY 办事处
			countOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算资料栏位的金额总数GROUP BY 办事处
			amountOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//已訪廠暂时没有
			countOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			amountOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算审核中栏位的总数量GROUP BY 办事处
			countOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算审核中栏位的金额总数GROUP BY 办事处
			amountOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算已核准栏位的总数量GROUP BY 办事处
			countOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算已核准栏位的金额总数GROUP BY 办事处
			amountOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//获得所有办事处
			paramMap.put("decp_id", "2");//2代表的是拿分公司
			companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", paramMap, DataAccessor.RS_TYPE.LIST);
			
			//为所有办事处新增TO
			for(int i=0;companys!=null&&i<companys.size();i++) {
				paramMap.put(String.valueOf(companys.get(i).get("DECP_ID")), new CaseReportTO(String.valueOf(companys.get(i).get("DECP_ID")),companys.get(i).get("DECP_NAME_CN")));
			}
			
			//加入资料栏位的信息--------------------------------------------------------------
			for(int i=0;countOfInfoGroupByCmpy!=null&&i<countOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoCount(String.valueOf(countOfInfoGroupByCmpy.get(i).get("INFO_COUNT")));
				} else {
					
				}
			}

			for(int i=0;amountOfInfoGroupByCmpy!=null&&i<amountOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoAmount(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("INFO_AMOUNT")));
				} else {
					
				}
			}
			
			//加入已訪廠栏位信息--------------------------------------------------------------
			for(int i=0;countOfHasAccessGroupByCmpy!=null&&i<countOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessCount(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfHasAccessGroupByCmpy!=null&&i<amountOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessAmount(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_AMOUNT")));
				} else {
					
				}
			}
			//加入审核中栏位信息--------------------------------------------------------------
			for(int i=0;countOfAuditGroupByCmpy!=null&&i<countOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditCount(String.valueOf(countOfAuditGroupByCmpy.get(i).get("AUDIT_COUNT")));
				} else {
					
				}
			}
			DecimalFormat df=new DecimalFormat("0.00");
			for(int i=0;amountOfAuditGroupByCmpy!=null&&i<amountOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditAmount(String.valueOf(df.format(amountOfAuditGroupByCmpy.get(i).get("AUDIT_AMOUNT"))));
				} else {
					
				}
			}
			
			//加入已核准栏位信息--------------------------------------------------------------
			for(int i=0;countOfApproveGroupByCmpy!=null&&i<countOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveCount(String.valueOf(countOfApproveGroupByCmpy.get(i).get("APPROVE_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfApproveGroupByCmpy!=null&&i<amountOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveAmount(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("APPROVE_AMOUNT")));
				} else {
					
				}
			}
			
			//插入数据到表T_CASE_REPORT
			for(int i=0;companys!=null&&i<companys.size();i++) {
				Map<String,String> contextMap=new HashMap<String,String>();
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(companys.get(i).get("DECP_ID")));
				
				//设定插入的数据
				contextMap.put("DECP_ID",to.getDecpId());
				contextMap.put("DECP_NAME_CN",to.getDecpNameCn());
				contextMap.put("INFO_COUNT",to.getInfoCount());
				contextMap.put("INFO_AMOUNT",to.getInfoAmount());
				contextMap.put("HAS_ACCESS_COUNT",to.getHasAccessCount());
				contextMap.put("HAS_ACCESS_AMOUNT",to.getHasAccessAmount());
				contextMap.put("AUDIT_COUNT",to.getAuditCount());
				contextMap.put("AUDIT_AMOUNT",to.getAuditAmount());
				contextMap.put("APPROVE_COUNT",to.getApproveCount());
				contextMap.put("APPROVE_AMOUNT",to.getApproveAmount());
				
				DataAccessor.execute("caseReportService.batchUpdateMotor",contextMap,OPERATION_TYPE.INSERT);
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("batch job for case report重车 end  --------------------");
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			throw e;
		}
	}
	
	@Transactional(rollbackFor=Exception.class)//各区案况batch job for乘用车
	public void batchJobForCar() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case report乘用车 start  --------------------");
		}
		Map paramMap=new HashMap();
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("productionType",Constants.PRODUCTION_TYPE_3);
		
		List<Map<String,String>> companys=null;
		
		//int countOfInfo=0;
		List<HashMap> countOfInfoGroupByCmpy=null;
		List<HashMap> amountOfInfoGroupByCmpy=null;
		
		List<HashMap> countOfHasAccessGroupByCmpy=null;
		List<HashMap> amountOfHasAccessGroupByCmpy=null;
		
		List<HashMap> countOfAuditGroupByCmpy=null;
		List<HashMap> amountOfAuditGroupByCmpy=null;
		
		List<HashMap> countOfApproveGroupByCmpy=null;
		List<HashMap> amountOfApproveGroupByCmpy=null;
		try {
			//计算资料栏位的总数量GROUP BY 办事处
			countOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算资料栏位的金额总数GROUP BY 办事处
			amountOfInfoGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfInfoGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//已訪廠暂时没有
			countOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			amountOfHasAccessGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfHasAccessGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算审核中栏位的总数量GROUP BY 办事处
			countOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算审核中栏位的金额总数GROUP BY 办事处
			amountOfAuditGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfAuditGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//计算已核准栏位的总数量GROUP BY 办事处
			countOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getCountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			//计算已核准栏位的金额总数GROUP BY 办事处
			amountOfApproveGroupByCmpy=(List<HashMap>)DataAccessor.query("caseReportService.getAmountOfApproveGroupByCmpyProductionType",param,DataAccessor.RS_TYPE.LIST);
			
			//获得所有办事处
			paramMap.put("decp_id", "2");//2代表的是拿分公司
			companys=(List<Map<String,String>>)DataAccessor.query("employee.getCompany", paramMap, DataAccessor.RS_TYPE.LIST);
			
			//为所有办事处新增TO
			for(int i=0;companys!=null&&i<companys.size();i++) {
				paramMap.put(String.valueOf(companys.get(i).get("DECP_ID")), new CaseReportTO(String.valueOf(companys.get(i).get("DECP_ID")),companys.get(i).get("DECP_NAME_CN")));
			}
			
			//加入资料栏位的信息--------------------------------------------------------------
			for(int i=0;countOfInfoGroupByCmpy!=null&&i<countOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoCount(String.valueOf(countOfInfoGroupByCmpy.get(i).get("INFO_COUNT")));
				} else {
					
				}
			}

			for(int i=0;amountOfInfoGroupByCmpy!=null&&i<amountOfInfoGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setInfoAmount(String.valueOf(amountOfInfoGroupByCmpy.get(i).get("INFO_AMOUNT")));
				} else {
					
				}
			}
			
			//加入已訪廠栏位信息--------------------------------------------------------------
			for(int i=0;countOfHasAccessGroupByCmpy!=null&&i<countOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessCount(String.valueOf(countOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfHasAccessGroupByCmpy!=null&&i<amountOfHasAccessGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setHasAccessAmount(String.valueOf(amountOfHasAccessGroupByCmpy.get(i).get("HAS_ACCESS_AMOUNT")));
				} else {
					
				}
			}
			//加入审核中栏位信息--------------------------------------------------------------
			for(int i=0;countOfAuditGroupByCmpy!=null&&i<countOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditCount(String.valueOf(countOfAuditGroupByCmpy.get(i).get("AUDIT_COUNT")));
				} else {
					
				}
			}
			DecimalFormat df=new DecimalFormat("0.00");
			for(int i=0;amountOfAuditGroupByCmpy!=null&&i<amountOfAuditGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfAuditGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setAuditAmount(String.valueOf(df.format(amountOfAuditGroupByCmpy.get(i).get("AUDIT_AMOUNT"))));
				} else {
					
				}
			}
			
			//加入已核准栏位信息--------------------------------------------------------------
			for(int i=0;countOfApproveGroupByCmpy!=null&&i<countOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(countOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveCount(String.valueOf(countOfApproveGroupByCmpy.get(i).get("APPROVE_COUNT")));
				} else {
					
				}
			}
			
			for(int i=0;amountOfApproveGroupByCmpy!=null&&i<amountOfApproveGroupByCmpy.size();i++) {
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("DECP_ID")));
				if(to!=null) {
					to.setApproveAmount(String.valueOf(amountOfApproveGroupByCmpy.get(i).get("APPROVE_AMOUNT")));
				} else {
					
				}
			}
			
			//插入数据到表T_CASE_REPORT
			for(int i=0;companys!=null&&i<companys.size();i++) {
				Map<String,String> contextMap=new HashMap<String,String>();
				CaseReportTO to=(CaseReportTO)paramMap.get(String.valueOf(companys.get(i).get("DECP_ID")));
				
				//设定插入的数据
				contextMap.put("DECP_ID",to.getDecpId());
				contextMap.put("DECP_NAME_CN",to.getDecpNameCn());
				contextMap.put("INFO_COUNT",to.getInfoCount());
				contextMap.put("INFO_AMOUNT",to.getInfoAmount());
				contextMap.put("HAS_ACCESS_COUNT",to.getHasAccessCount());
				contextMap.put("HAS_ACCESS_AMOUNT",to.getHasAccessAmount());
				contextMap.put("AUDIT_COUNT",to.getAuditCount());
				contextMap.put("AUDIT_AMOUNT",to.getAuditAmount());
				contextMap.put("APPROVE_COUNT",to.getApproveCount());
				contextMap.put("APPROVE_AMOUNT",to.getApproveAmount());
				
				DataAccessor.execute("caseReportService.batchUpdateCar",contextMap,OPERATION_TYPE.INSERT);
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("batch job for case report乘用车 end  --------------------");
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			throw e;
		}
	}
}
