package com.brick.dun.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.BaseTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.dun.to.DunCaseTo;
import com.brick.dun.to.DunTaskEveryTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.Constants;
import com.brick.util.SendMsg;
import com.brick.util.StringUtils;

/**
 * 催收任务
 * 
 * @author li shaojie
 * @date Jun 11, 2010
 */

public class DunTaskService extends BaseCommand {
	Log logger = LogFactory.getLog(DunTaskService.class);
	/**
	 * 催收任务管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void manage(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		Map rsMap = new HashMap() ;
		//显示每日逾期报表按钮
		boolean everyDue=false;
		context.contextMap.put("id", context.contextMap.get("s_employeeId"));
		Object decpId = context.contextMap.get("COMPANY");
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("job", rsMap.get("JOB")) ;
			if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				context.contextMap.put("dun_date", format.format(new Date()));
			}

			//核准人
			outputMap.put("approvalUserName", context.contextMap.get("approvalUserName"));
			//原始经办人
			outputMap.put("orgUserName", context.contextMap.get("orgUserName"));
			//原始主管
			outputMap.put("orgUpUserName", context.contextMap.get("orgUpUserName"));
			List<Map> companys = (List<Map>)DataAccessor.query("companyManage.queryCompanyAlias", null,DataAccessor.RS_TYPE.LIST);
			outputMap.put("companyList", companys);

			if(context.contextMap.get("deptName") != null){
				String decpName = context.contextMap.get("deptName").toString();
				for (int i = 0; i < companys.size(); i++) {
					if(decpName.equals(companys.get(i).get("DECP_NAME_CN"))){
						decpId = companys.get(i).get("DECP_ID");
						context.contextMap.put("ORG_COMPANY", decpId);
						break;
					}
				}
			}
			//金额
			outputMap.put("moneyBegin", context.contextMap.get("moneyBegin")) ;
			outputMap.put("moneyEnd",context.contextMap.get("moneyEnd"));
			//按金额查询的类型
			if(context.contextMap.get("moneyType") != null){
				String moneyType = context.contextMap.get("moneyType").toString();
				if (moneyType.equals(Constants._50)) {
					outputMap.put("moneyEnd",Constants.$50);
					context.contextMap.put("moneyEnd", Constants.$50);
				} else if (moneyType.equals(Constants._50_100)) {
					outputMap.put("moneyBegin",Constants.$50);
					outputMap.put("moneyEnd",Constants.$100);
					context.contextMap.put("moneyBegin", Constants.$50);
					context.contextMap.put("moneyEnd", Constants.$100);
				} else if (moneyType.equals(Constants._100_200)) {
					outputMap.put("moneyBegin",Constants.$100);
					outputMap.put("moneyEnd",Constants.$200);
					context.contextMap.put("moneyBegin", Constants.$100);
					context.contextMap.put("moneyEnd", Constants.$200);
				} else if (moneyType.equals(Constants._200_300)) {
					outputMap.put("moneyBegin",Constants.$200);
					outputMap.put("moneyEnd",Constants.$300);
					context.contextMap.put("moneyBegin", Constants.$200);
					context.contextMap.put("moneyEnd", Constants.$300);
				} else if (moneyType.equals(Constants._300)) {
					outputMap.put("moneyBegin",Constants.$300);
					context.contextMap.put("moneyBegin", Constants.$300);
				}
			}
			outputMap.put("creditSpecialList", DataAccessor.query("dunTask.queryCreditSpecial", null,DataAccessor.RS_TYPE.LIST));
			pagingInfo = baseService.queryForListWithPaging("dunTask.getAllDunData", context.contextMap, "DUN_DAY", ORDER_TYPE.DESC);
			List<String> resourceIdList=(List<String>) DataAccessor.query("dunTask.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				if("everyDue".equals(resourceIdList.get(i))) {
					everyDue=true;
				}
			outputMap.put("everyDue", everyDue);
			}
			//诉讼进程
			outputMap.put("litigationList", DictionaryUtil.getDictionary("诉讼进程"));
			outputMap.put("lawyFeeList", DictionaryUtil.getDictionary("法务费用"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		//专案
		outputMap.put("CREDIT_SPECIAL_CODE", context.contextMap.get("CREDIT_SPECIAL_CODE"));
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("startrange", context.contextMap.get("startrange"));
		outputMap.put("endrange", context.contextMap.get("endrange"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
		//outputMap.put("pay_type", context.contextMap.get("pay_type"));
		outputMap.put("dun_date", context.contextMap.get("dun_date"));
		outputMap.put("COMPANY", context.contextMap.get("COMPANY"));
		outputMap.put("ORG_COMPANY", context.contextMap.get("ORG_COMPANY"));
		outputMap.put("NAME", context.contextMap.get("NAME"));
		
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		Output.jspOutput(outputMap, context, "/dun/dunTaskManage.jsp");
	}
	
	/**
	 * 根据承租人编号。获取该承租人的逾期数据
	 *     @author zhangbo20130130修改回溯时间查询条件
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDunInfoByCustCode(Context context) {
		Map outputMap = new HashMap();
		List dunList = null;
		List dunRecordList = null;
		List custLinkman = null;
		List dictionary = null;
		Map contract = new HashMap();
		Map creditCustomerCorpMap = null;
		try {
			//law显示添加法务费用按钮
			boolean law=false;
			//litigation显示添加诉讼按钮
			boolean litigation=false;
			List<String> resourceIdList=(List<String>) DataAccessor.query("litigation.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				//below is hard code for ResourceId,we will enhance it in the future
				if("Law-Show".equals(resourceIdList.get(i))) {
					law=true;
				}else if("Lt-Show".equals(resourceIdList.get(i))) {
					litigation=true;
				}
				outputMap.put("law", law);
				outputMap.put("litigation",litigation);
			}
			dunList = (List) DataAccessor.query("dunTask.getDunDataByCustCodeDate",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			dunRecordList = (List) DataAccessor.query(
					"dunTask.getFiveDunRecords", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			dictionary = (List<Map>) DictionaryUtil.getDictionary("催收结果");
			
			// 联系人
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkmanList", custLinkman);
			
			//根据合同号查询详情20130201zhang
			contract= (Map) DataAccessor.query("dunTask.getContractById",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//增加担保人列表
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("custId", context.contextMap.get("cust_id"));
			List<Map<String,Object> > natureList=(List) DataAccessor.query("dunTask.getNatureListByCustId",map,DataAccessor.RS_TYPE.LIST);
			outputMap.put("natureList", natureList);
			
			//承租人联系方式增加配偶、联系人
			map.put("credit_id", contract.get("PRCD_ID"));
			creditCustomerCorpMap = (Map) DataAccessor.query("creditCustomerCorp.getCreditCustomerNatuByCreditId", map, DataAccessor.RS_TYPE.MAP);
			if (creditCustomerCorpMap == null) {
				creditCustomerCorpMap = (Map) DataAccessor.query("creditCustomer.getCustomerInfoBycredit_id", map, DataAccessor.RS_TYPE.MAP);
			}
			outputMap.put("creditCustomerCorpMap", creditCustomerCorpMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("contract",contract);
		outputMap.put("CUST_ID", context.contextMap.get("cust_id"));
		outputMap.put("CUST_CODE", context.contextMap.get("cust_code").toString());
		outputMap.put("dictionary", dictionary);
		outputMap.put("dunList", dunList);
		outputMap.put("dunRecordList", dunRecordList);
		Output.jspOutput(outputMap, context, "/dun/dunInfo.jsp");
	}

	/**
	 * 插入一条催收记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void addDunRecord(Context context) {
		Map outputMap = new HashMap();
		try {
			context.contextMap.put("clerk_id", context.contextMap.get("s_employeeId"));
			DataAccessor.execute("dunTask.addDunRecord", context.contextMap,
					DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			outputMap.put("result", "N");
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("result", "Y");
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 根据客户编号 查询所有的逾期催收记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllDunRecords(Context context) {
		Map outputMap = new HashMap();
		List dunRecordsList = null;
		try {
			dunRecordsList = (List) DataAccessor.query(
					"dunTask.getAllDunRecords", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;i<dunRecordsList.size();i++) {
				if(((Map)(dunRecordsList.get(i))).get("CALL_CONTENT")==null||"".equals(((Map)(dunRecordsList.get(i))).get("CALL_CONTENT"))) {
					
				} else {
					((Map)(dunRecordsList.get(i))).put("CALL_CONTENT", StringUtils.autoInsertWrap(((Map)(dunRecordsList.get(i))).get("CALL_CONTENT").toString(), 30));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dunRecordsList", dunRecordsList);
		Output.jspOutput(outputMap, context, "/dun/dunRecordsManage.jsp");
	}

	/**
	 * 删除一条催收记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteDunRecord(Context context) {
		Map outputMap = new HashMap();
		try {
			DataAccessor.execute("dunTask.deleteDunRecord", context.contextMap,
					DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("result", "Y");
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 根据支付表ID获取该支付表的逾期信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDunInfoForDerateFine(Context context) {
		Map outputMap = new HashMap();
		Map dunInfoMap=new HashMap();
		String reduce_date="";
		if(context.contextMap.get("reduce_date")!=null){
			reduce_date=(String)context.contextMap.get("reduce_date");			
		}else{
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			reduce_date=format.format(new Date());
		}
		context.contextMap.put("reduce_date", reduce_date);
		try {
			dunInfoMap=(Map)DataAccessor.query("dunTask.getDunInfoByRecp_id", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dunInfoMap", dunInfoMap);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 保存罚息减免信息
	 * @param context
	 */
	public void saveDerate(Context context){
		try {
			DataAccessor.execute("dunTask.saveDerate", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		}catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=dunTask.manage");
	}
	/**
	 * 保存罚息减免信息
	 * @param context
	 */
	public void saveDerate_Zujin(Context context){
		try {
			DataAccessor.execute("dunTask.saveDerate", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		}catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=dunRent.manage");
	}
	/**
	 * 修改罚息减免信息
	 * @param context
	 */
	public void updateDerate(Context context){
	    try {
		DataAccessor.execute("dunTask.updateDerate", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
	    }catch (Exception e) { 
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	    }
	    Output.jspSendRedirect(context, "defaultDispatcher?__action=dunTask.derateFineManage");
	}
	/**
	 * 撤销罚息减免信息
	 * @param context
	 */
	public void repealDerate(Context context){
	    try {
		DataAccessor.execute("dunTask.repealDerate", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
	    }catch (Exception e) { 
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	    }
	    Output.jspSendRedirect(context,"defaultDispatcher?__action=dunTask.derateFineManage");
	}
	
	/**
	 * 罚息减免管理
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void derateFineManage(Context context){
		Map outputMap=new HashMap();
		DataWrap dw=new DataWrap();
		try { 
			dw = (DataWrap) DataAccessor.query("dunTask.getAllFineDerate",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("state", context.contextMap.get("state"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		Output.jspOutput(outputMap, context, "/dun/derateFineManage.jsp");
	}
	
	/**
	 * 罚息减免审批
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void derateFineExamine(Context context){
		Map outputMap=new HashMap();
		DataWrap dw=new DataWrap();
		String bos = (String) context.getContextMap().get("bos");
		try { 
			dw = (DataWrap) DataAccessor.query("dunTask.getAllFineDerate",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("state", context.contextMap.get("state"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("roleFlag", 1);
		if("".equals(bos) | bos == null){
		    
		    Output.jspOutput(outputMap, context, "/dun/derateFineManage.jsp");
		}else{
		    
		    Output.jspOutput(outputMap, context, "/dun/derateFineBoss.jsp");
		}
	}
	/**
	 * 罚息减免审批 经理审批
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void derateFineBos(Context context){
	    Map outputMap=new HashMap();
	    DataWrap dw=new DataWrap();
	    
	    String state =  (String) context.contextMap.get("state");


	    	context.getContextMap().put("state", state);
	    try { 
		dw = (DataWrap) DataAccessor.query("dunTask.getAllFineDerate",
			context.contextMap, DataAccessor.RS_TYPE.PAGED);
	    } catch (Exception e) {
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	    }
	    
	    outputMap.put("state", state); 
	    outputMap.put("dw", dw); 
	    outputMap.put("content", context.contextMap.get("content"));
	    outputMap.put("start_date", context.contextMap.get("start_date"));
	    outputMap.put("end_date", context.contextMap.get("end_date"));
	 
	    
		
		Output.jspOutput(outputMap, context, "/dun/derateFineBoss.jsp");
	    
	}
	
	/**
	 * 查询罚息减免明细用于审批
	 */
	@SuppressWarnings("unchecked")
	public void getDerateFineExamine(Context context){
		Map outputMap=new HashMap();
		Map derateFineInfo=null;
		String  boss = (String) context.getContextMap().get("Boss");
		try {
			derateFineInfo=(Map)DataAccessor.query("dunTask.getDerateFineInfo",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		  outputMap.put("derateFineInfo", derateFineInfo);
		  
		if("".equals(boss) | boss == null){
		  
			Output.jspOutput(outputMap, context, "/dun/examineDerateFine.jsp");
		}else{
		    
			Output.jspOutput(outputMap, context, "/dun/bossExamineDerateFine.jsp");
		}
		
		
	}
	
	/**
	 * 查询罚息减免明细
	 */
	@SuppressWarnings("unchecked")
	public void getDerateFineInfo(Context context){
		Map outputMap=new HashMap();
		Map derateFineInfo=null;
		try {
			derateFineInfo=(Map)DataAccessor.query("dunTask.getDerateFineInfo",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("derateFineInfo", derateFineInfo);
		Output.jspOutput(outputMap, context, "/dun/showDerateFine.jsp");
	}

	
	/**
	 * 修改罚息减免明细
	 */
	@SuppressWarnings("unchecked")
	public void getDerateFineUpdate(Context context){
	    Map outputMap=new HashMap();
	    Map derateFineInfo=null;
	    String state = (String) context.getContextMap().get("state");
	    try {
		derateFineInfo=(Map)DataAccessor.query("dunTask.getDerateFineInfo",
			context.contextMap, DataAccessor.RS_TYPE.MAP);
	    } catch (Exception e) { 
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	    }
	    outputMap.put("derateFineInfo", derateFineInfo);
	    if("".equals(state) | state ==null){
		
	    Output.jspOutput(outputMap, context, "/dun/updateDerateFine.jsp");
	    }
	    else if(state.equals("1")){
		
		Output.jspOutput(outputMap, context, "/dun/updateDerate.jsp");
	    }
	}
	
	/**
	 * 审批
	 * @param context
	 */
	public void examine(Context context){
		try {
			DataAccessor.execute("dunTask.examine", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=dunTask.derateFineExamine");
	}
	
	/**
	 * 根据承租人编号和日期获取逾期明细
	 * @param context
	 */
	//Modify by Michael 2011 12/29 修正罚息抓取逻辑
	@SuppressWarnings("unchecked")
	public void getDunDetailByCustIdAndDate(Context context){
		Map outputMap=new HashMap();
		List<Map> dunDetailList=null;
		List<Map> dunUnPayList=null;
		List periodNumList=null;
		String income_date=null;
		List dunInComeMoneyList=null;
		try {
			//******************临时方案
			dunDetailList=(List<Map>)DataAccessor.query("decompose.findDunDetailByRecpCode", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			//每期所交款的次数
			periodNumList=(List)DataAccessor.query("decompose.getPeriodNumByRecpCode", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			if(dunDetailList!=null && dunDetailList.size()>0){
				dunDetailList.get(0).put("isFirst", "true");
					for(int i=1;i < dunDetailList.size();i++){
						int periodNumTop =Integer.valueOf(dunDetailList.get(i-1).get("PERIOD_NUM").toString());
						int periodNum =Integer.valueOf(dunDetailList.get(i).get("PERIOD_NUM").toString());
						if(periodNum==periodNumTop){
							dunDetailList.get(i).put("isFirst", "false");
						}else{
							dunDetailList.get(i).put("isFirst", "true");
						}
					}
			}
			dunUnPayList=(List<Map>)DataAccessor.query("decompose.findDunDetailByRecpCodeUnPay", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			//
			if(dunDetailList!=null && dunUnPayList!=null && dunDetailList.size()>0 && dunUnPayList.size()>0){
				int last =dunDetailList.size();
				int detailLast =Integer.valueOf(dunDetailList.get(last-1).get("PERIOD_NUM").toString());
				int unOne =Integer.valueOf(dunUnPayList.get(0).get("PERIOD_NUM").toString());
				if(detailLast==unOne){
					outputMap.put("hide", unOne);
					//dunDetailList.get(last-1).put("hide", "true");
					//dunUnPayList.get(0).put("hide", "true");
				}
			}
			//******************临时方案
			dunInComeMoneyList=(List)DataAccessor.query("decompose.findDunDetailInComeMoneyByRecpID", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("periodNumList", periodNumList);
		outputMap.put("dunUnPayList", dunUnPayList);
		outputMap.put("dunDetailList", dunDetailList);
		outputMap.put("dunInComeMoneyList", dunInComeMoneyList);
		
		outputMap.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
		outputMap.put("RECP_CODE", context.contextMap.get("RECP_CODE"));
		
		//Modify by Michael 2011 12/29 修正罚息抓取逻辑
		//outputMap.put("income_date", income_date);
		//outputMap.put("cust_code", context.getContextMap().get("cust_code"));
		Output.jspOutput(outputMap, context, "/dun/showDunDetail.jsp");
	}
	
	//取到联系人
	@SuppressWarnings("unchecked")
	public void getCustLinkman(Context context) {
		Map outputMap = new HashMap();

		List custLinkman = null;
		try {
			// 联系人
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);

			outputMap.put("custLinkman", custLinkman);
			outputMap.put("linkCode", context.contextMap.get("cust_code").toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
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
			codeAndName = (Map)DataAccessor.query("dunTask.codeAndName", context.contextMap, DataAccessor.RS_TYPE.MAP);
			String linkManMobile = context.contextMap.get("linkManMobile").toString();
			String[] mobile = linkManMobile.split(",");
			for(int i = 0; i<mobile.length;i++){
				Map sendManager = new HashMap();
				
				sendManager.put("SENDTYPE", 1);
				sendManager.put("MTEL", mobile[i]);
				sendManager.put("MESSAGE", codeAndName.get("LEASE_CODE")+""+codeAndName.get("CUST_NAME")+" - 敬爱的客户,本期租金以过期尚未缴纳请尽速缴款,裕融租赁提醒您。0512-80983566业管部");
				sendManager.put("STATE", 2);
				sendManager.put("LEASE_CODE", codeAndName.get("LEASE_CODE"));
				
				//add by ShenQi,为插入SEND_TEST表准备数据
				sendManager.put("CONTRACT_NUMBER",codeAndName.get("LEASE_CODE")==null?"":codeAndName.get("LEASE_CODE"));
				sendManager.put("CUST_NAME",codeAndName.get("CUST_NAME")==null?"":codeAndName.get("CUST_NAME"));
				sendManager.put("CREATE_BY",context.contextMap.get("s_employeeName"));
				sendManager.put("SEND_MODE","0");//0 means 手动, 1 means 自动
				sendManager.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
				sendManager.put("LOG","sendMessage");
				
				send.add(sendManager);
				//modify by Michael 2013 02-20 修正重复发送Bug
//				SendMsg sendMsg = new SendMsg();
//				returnStr = sendMsg.SendSMSMsg(context, send);
			}
			SendMsg sendMsg = new SendMsg();
			returnStr = sendMsg.SendSMSMsg(context, send);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("returnStr", returnStr);
		Output.jsonOutput(outputMap, context);
	}
	
	public void getPrcId(Context context){
		Map outputMap = new HashMap();
		Object prc_id = null;
		try {
			prc_id = baseService.queryForObj("dunTask.getPrcId", context.contextMap);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("prc_id", prc_id);
		Output.jsonOutput(outputMap, context);
	}
	
	
	/**
	 * @author zhangbo
	 * @serialData 20130115
	 * //查询每日逾期表
	 * @return
	 */
	public static List<DunTaskEveryTo> getEveryDayOverDue(String sDate) {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("date", sDate);
		List<DunTaskEveryTo> resultList=null;
		try {
			resultList=(List<DunTaskEveryTo>)DataAccessor.query("dunTask.getEveryDayOverDue",param,RS_TYPE.LIST);
			List<Map> dictionaryList = (List<Map>) DictionaryUtil.getDictionary("催收结果");
			
			for(int i=0;i<resultList.size();i++){
				String result=resultList.get(i).getCallResult();
				if(result!=null && !"".equals(result)){
					for(int m=0;m<dictionaryList.size();m++){
						String dictionary=dictionaryList.get(m).get("CODE").toString();
						if(dictionary.equals(result)){
							resultList.get(i).setCallResult(dictionaryList.get(m).get("FLAG").toString());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	/**
	 * 应催收件数
	 */
	public static Integer getEveryDayOverDueCount(String date) throws Exception {
		BaseTo baseTo =new BaseTo();
		baseTo.setKey_value(date);
		int countDue=(Integer)DataAccessor.queryForObj("dunTask.getEveryDayOverDueCount", baseTo);
		return countDue;
	}
	/**
	 * 已催收件数
	 */
	public static Integer getEveryDayOverDueCallCount(String date) throws Exception{
		BaseTo baseTo =new BaseTo();
		baseTo.setKey_value(date);
		int countDue=(Integer)DataAccessor.queryForObj("dunTask.getEveryDayOverDueCallCount", baseTo);
		return countDue;
	}
	/**
	 * 担保人列表
	 * @throws Exception 
	 */
	public void getNatureListByCustId(Context context) throws Exception{
		Map databack = new HashMap();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("custId", context.contextMap.get("custId"));
		List<Map<String,Object> > natureList=(List) DataAccessor.query("dunTask.getNatureListByCustId",map,DataAccessor.RS_TYPE.LIST);
		databack.put("natureList", natureList);
		Output.jsonOutput(databack, context);
	}
	
	public void selectLatestPayDetail (Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				
				dw = baseService.queryForListWithPaging("dunRent.selectLatestPayDetail", context.contextMap, "LEASE_CODE", ORDER_TYPE.DESC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("startrange", context.contextMap.get("startrange"));
		outputMap.put("endrange", context.contextMap.get("endrange"));
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/dun/queryLatestPayDetail.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
		//律师函列表页面
		@SuppressWarnings("unchecked")
		public void getLetterList(Context context) {
			Map outputMap = new HashMap();
			PagingInfo<Object> pagingInfo = null;
			Map rsMap = new HashMap() ;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				//context.contextMap.put("job", rsMap.get("JOB")) ;
				if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar = Calendar.getInstance();//此时打印它获取的是系统当前时间
			        calendar.add(Calendar.DATE, -1);    //得到前一天
					context.contextMap.put("dun_date", format.format(calendar.getTime()));
				}
				pagingInfo = baseService.queryForListWithPaging("dunTask.getLetterList", context.contextMap, "dunDay", ORDER_TYPE.ASC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			outputMap.put("dun_date", context.contextMap.get("dun_date"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("startrange", context.contextMap.get("startrange"));
			outputMap.put("endrange", context.contextMap.get("endrange"));
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("NAME", context.contextMap.get("NAME"));
			Output.jspOutput(outputMap, context, "/dun/dunLetter.jsp");
		}
		//律师函担保人
		@SuppressWarnings("unchecked")
		public void getNatureByCustId(Context context) throws Exception{
			Map outputMap = new HashMap();
			Map databack = new HashMap();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("rectId", context.contextMap.get("rectId"));
			List<Map<String,Object> > natureList=(List) DataAccessor.query("dunTask.getNatureListByRectId",map,DataAccessor.RS_TYPE.LIST);
			List<Map<String,Object>> companyList=(List) DataAccessor.query("dunTask.getCompanyListByRectId",map,DataAccessor.RS_TYPE.LIST);
			outputMap.put("natureList", natureList);
			outputMap.put("companyList", companyList);
			Output.jspOutput(outputMap, context, "/dun/dunGuarantee.jsp");
		}
		//委外回访逾期列表
		@SuppressWarnings("unchecked")
		public void getOutVisitList(Context context) {
			Map outputMap = new HashMap();
			PagingInfo<Object> pagingInfo = null;
			Map rsMap = new HashMap() ;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			//context.contextMap.put("dun_date","2013-07-09");
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				if(context.contextMap.get("dun_date")==null|| "".equals(context.contextMap.get("dun_date"))){
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar = Calendar.getInstance();//
			        calendar.add(Calendar.DATE, -1);    //得到前一天
					context.contextMap.put("dun_date", format.format(calendar.getTime()));
				}
				if(context.contextMap.get("startrange")==null|| "".equals(context.contextMap.get("startrange"))){
					context.contextMap.put("startrange", 8);
				}
				
				pagingInfo = baseService.queryForListWithPaging("dunTask.getoutVisitList", context.contextMap, "dunDay", ORDER_TYPE.ASC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			outputMap.put("dun_date", context.contextMap.get("dun_date"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("startrange", context.contextMap.get("startrange"));
			outputMap.put("endrange", context.contextMap.get("endrange"));
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("NAME", context.contextMap.get("NAME"));
			Output.jspOutput(outputMap, context, "/dun/outVisit.jsp");
		}
		//新逾期2014-01-20
		@SuppressWarnings("unchecked")
		public void manageForNew(Context context) {
			Map outputMap = new HashMap();
			PagingInfo<Object> pagingInfo = null;
			Map rsMap = new HashMap() ;
			//显示每日逾期报表按钮
			boolean everyDue=false;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
//			Object decpId = context.contextMap.get("COMPANY");
			String[] decpIds = context.getRequest().getParameterValues("COMPANY[]");
			List<Integer> decpIdList = null;
			if(decpIds != null && decpIds.length > 0){
				decpIdList = new ArrayList<Integer>();
				for(String id : decpIds){
					decpIdList.add(Integer.parseInt(id));
				}
			}
			context.contextMap.put("COMPANY", decpIdList);
			String[] orgDecpIds = context.getRequest().getParameterValues("ORG_COMPANY[]");
			List<Integer> orgDecpIdList = null;
			if(orgDecpIds != null && orgDecpIds.length > 0){
				orgDecpIdList = new ArrayList<Integer>();
				for(String id : orgDecpIds){
					orgDecpIdList.add(Integer.parseInt(id));
				}
			}
			context.contextMap.put("ORG_COMPANY", orgDecpIdList);
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				context.contextMap.put("job", rsMap.get("JOB")) ;
				if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					context.contextMap.put("dun_date", format.format(new Date()));
				}

				//核准人
				outputMap.put("approvalUserName", context.contextMap.get("approvalUserName"));
				//原始经办人
				outputMap.put("orgUserName", context.contextMap.get("orgUserName"));
				//原始主管
				outputMap.put("orgUpUserName", context.contextMap.get("orgUpUserName"));
				List<Map> companys = (List<Map>)DataAccessor.query("companyManage.queryCompanyAlias", null,DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyList", companys);

				if(context.contextMap.get("deptName") != null){
					String decpName = context.contextMap.get("deptName").toString();
					for (int i = 0; i < companys.size(); i++) {
						if(decpName.equals(companys.get(i).get("DECP_NAME_CN"))){
//							decpId = companys.get(i).get("DECP_ID");
//							context.contextMap.put("ORG_COMPANY", decpId);
							context.contextMap.put("ORG_COMPANY", new ArrayList().add(companys.get(i).get("DECP_ID")));
							break;
						}
					}
				}
				//金额
				outputMap.put("moneyBegin", context.contextMap.get("moneyBegin")) ;
				outputMap.put("moneyEnd",context.contextMap.get("moneyEnd"));
				//按金额查询的类型
				if(context.contextMap.get("moneyType") != null){
					String moneyType = context.contextMap.get("moneyType").toString();
					if (moneyType.equals(Constants._50)) {
						outputMap.put("moneyEnd",Constants.$50);
						context.contextMap.put("moneyEnd", Constants.$50);
					} else if (moneyType.equals(Constants._50_100)) {
						outputMap.put("moneyBegin",Constants.$50);
						outputMap.put("moneyEnd",Constants.$100);
						context.contextMap.put("moneyBegin", Constants.$50);
						context.contextMap.put("moneyEnd", Constants.$100);
					} else if (moneyType.equals(Constants._100_200)) {
						outputMap.put("moneyBegin",Constants.$100);
						outputMap.put("moneyEnd",Constants.$200);
						context.contextMap.put("moneyBegin", Constants.$100);
						context.contextMap.put("moneyEnd", Constants.$200);
					} else if (moneyType.equals(Constants._200_300)) {
						outputMap.put("moneyBegin",Constants.$200);
						outputMap.put("moneyEnd",Constants.$300);
						context.contextMap.put("moneyBegin", Constants.$200);
						context.contextMap.put("moneyEnd", Constants.$300);
					} else if (moneyType.equals(Constants._300)) {
						outputMap.put("moneyBegin",Constants.$300);
						context.contextMap.put("moneyBegin", Constants.$300);
					}
				}
				outputMap.put("creditSpecialList", DataAccessor.query("dunTask.queryCreditSpecial", null,DataAccessor.RS_TYPE.LIST));
				pagingInfo = baseService.queryForListWithPaging("dunTask.getAllDunData_new", context.contextMap, "DUN_DAY", ORDER_TYPE.DESC);
				List<String> resourceIdList=(List<String>) DataAccessor.query("dunTask.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("everyDue".equals(resourceIdList.get(i))) {
						everyDue=true;
					}
				outputMap.put("everyDue", everyDue);
				}
				//诉讼进程
				outputMap.put("litigationList", DictionaryUtil.getDictionary("诉讼进程"));
				outputMap.put("lawyFeeList", DictionaryUtil.getDictionary("法务费用"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			//专案
			outputMap.put("CREDIT_SPECIAL_CODE", context.contextMap.get("CREDIT_SPECIAL_CODE"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("startrange", context.contextMap.get("startrange"));
			outputMap.put("endrange", context.contextMap.get("endrange"));
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
			//outputMap.put("pay_type", context.contextMap.get("pay_type"));
			outputMap.put("dun_date", context.contextMap.get("dun_date"));
			outputMap.put("COMPANY", context.contextMap.get("COMPANY"));
			outputMap.put("ORG_COMPANY", context.contextMap.get("ORG_COMPANY"));
			outputMap.put("NAME", context.contextMap.get("NAME"));
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("companys", LeaseUtil.getCompanys());
			outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
			Output.jspOutput(outputMap, context, "/dun/dunTaskManage.jsp");
		}
		//新逾期查询
		/**
		 * 根据承租人编号和日期获取逾期明细----新
		 * @param context
		 */
		@SuppressWarnings("unchecked")
		public void showNewDunDetailByDecpId(Context context){
			Map outputMap=new HashMap();
			List<Map> dunDetailList=null;
			List<Map> dunUnPayList=null;
			List periodNumList=null;
			String income_date=null;
			List dunInComeMoneyList=null;
			Map<String,Object> settlementMap=null;
			try {
				dunDetailList=(List<Map>)DataAccessor.query("decompose.findDunDetailByRecpIdNew", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				periodNumList=(List)DataAccessor.query("decompose.getPeriodNumByRecpIdNew", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if(dunDetailList!=null && dunDetailList.size()>0){
					dunDetailList.get(0).put("isFirst", "true");
						for(int i=1;i < dunDetailList.size();i++){
							int periodNumTop =Integer.valueOf(dunDetailList.get(i-1).get("PERIOD_NUM").toString());
							int periodNum =Integer.valueOf(dunDetailList.get(i).get("PERIOD_NUM").toString());
							if(periodNum==periodNumTop){
								dunDetailList.get(i).put("isFirst", "false");
							}else{
								dunDetailList.get(i).put("isFirst", "true");
							}
						}
				}
				dunUnPayList=(List<Map>)DataAccessor.query("decompose.findDunDetailByRecpCodeUnPay", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if(dunDetailList!=null && dunUnPayList!=null && dunDetailList.size()>0 && dunUnPayList.size()>0){
					int last =dunDetailList.size();
					int detailLast =Integer.valueOf(dunDetailList.get(last-1).get("PERIOD_NUM").toString());
					int unOne =Integer.valueOf(dunUnPayList.get(0).get("PERIOD_NUM").toString());
					if(detailLast==unOne){
						outputMap.put("hide", unOne);
					}
				}
				dunInComeMoneyList=(List)DataAccessor.query("decompose.findDunDetailInComeMoneyByRecpIDForNew", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				
				if("3".equals(context.contextMap.get("RECP_STATUS")+"")) {
					settlementMap=(Map<String,Object>)DataAccessor.query("decompose.getSettlementPay",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				}
			} catch (Exception e) { 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			outputMap.put("periodNumList", periodNumList);
			outputMap.put("dunUnPayList", dunUnPayList);
			outputMap.put("dunDetailList", dunDetailList);
			outputMap.put("dunInComeMoneyList", dunInComeMoneyList);
			outputMap.put("settlementMap",settlementMap);
			
			outputMap.put("CUST_NAME", context.contextMap.get("CUST_NAME"));
			outputMap.put("RECP_CODE", context.contextMap.get("RECP_CODE"));
			Output.jspOutput(outputMap, context, "/dun/showDunDetail.jsp");
		}
		
		/*
		 * add by xuyuefei 2014/8/6
		 * 每天23：00跑job
		 * 记录逾期180以上案件
		 */
		@Transactional(rollbackFor=Exception.class)
		public void markDelayCase_180() throws Exception{
			Map param=new HashMap();
			SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
			List<DunCaseTo> result=null;
				result=(List<DunCaseTo>)this.baseService.queryForList("dunTask.getDunCase_180");
				if(result!=null&&result.size()>0){
					for(int i=0;i<result.size();i++){
						Map paramMap=new HashMap();
						paramMap.put("rectId", result.get(i).getRectId());
						paramMap.put("leaseCode", result.get(i).getLeaseCode());
						paramMap.put("balance", result.get(i).getBalance());
						paramMap.put("creditCode", result.get(i).getCreditCode());
						paramMap.put("creditId", result.get(i).getCreditId());
						this.baseService.insert("dunTask.addLog", paramMap);
					 }
				}

		}
		

}
