package com.brick.contract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.payer.service.PayerService;
import com.brick.payer.to.PayerTo;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.sms.email.TestMail;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.SendMsg;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 锁码管理
 * 
 */
public class LockManagementService extends BaseCommand {
	Log logger = LogFactory.getLog(LockManagementService.class);
	
	private LockCodeService lockCodeService;
	
	private PayerService payerService;
	public LockCodeService getLockCodeService() {
		return lockCodeService;
	}
	public void setLockCodeService(LockCodeService lockCodeService) {
		this.lockCodeService = lockCodeService;
	}
	
	
	public PayerService getPayerService() {
		return payerService;
	}
	public void setPayerService(PayerService payerService) {
		this.payerService = payerService;
	}
	/**
	 * 设备锁码查询
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getLockManagement(Context context) {
		Map outputMap = new HashMap();
		Map rentRecpMap = null;
		Map eqmtMap = null;
		List queryAllList = null;
		List lockList = null;
		try {
			rentRecpMap = (Map) DataAccessor.query(
					"lockManagement.getRentRecp", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			eqmtMap = (Map) DataAccessor.query("lockManagement.queryEqmtById",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("FICB_ITEM", "租金");
			queryAllList = (List) DataAccessor.query(
					"lockManagement.queryAllById", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("rentRecpMap", rentRecpMap);
		outputMap.put("eqmtMap", eqmtMap);
		outputMap.put("queryAllList", queryAllList);
		outputMap.put("lockList", lockList);
		outputMap.put("rect_id1", context.contextMap.get("rect_id"));
		outputMap.put("eqmt_id1", context.contextMap.get("eqmt_id"));
		outputMap.put("recp_id1", context.contextMap.get("recp_id"));

		Output.jspOutput(outputMap, context,
				"/lockManagement/lockManagement.jsp");
	}
	/**
	 * 验证机号是否重复
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkProductNumber(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		Integer count = null;
		if(errList.isEmpty()) {
			try {
				count = (Integer)DataAccessor.query("lockManagement.AJAX_common_THING_NUMBER", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else{
			
		}	
	}
	/**
	 * 更新或者修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void doSomething(Context context) {
		Map outputMap = new HashMap();
		// int today = context.contextMap.get("today") == null ? 1 : Integer
		// .parseInt(context.contextMap.get("today").toString());
		
		try {
			//Modify by Michael 2012 5-4 一个合同的的设备联系人为同一人
//				DataAccessor.execute("lockManagement.updateRenter", context.contextMap,
//						DataAccessor.OPERATION_TYPE.UPDATE);
			
			
				DataAccessor.execute("lockManagement.updateRenterByRectID", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
//				Modify by Michael 2012 04-25 机号另外维护，此处不需要维护
//				DataAccessor.execute("lockManagement.updateTHING_NUMBER", context.contextMap,
//						DataAccessor.OPERATION_TYPE.UPDATE);

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		String[] todayArr = context.getRequest().getParameterValues("today");
		String[] rent_idArr = context.getRequest()
				.getParameterValues("rent_id");
		String[] lock_idArr = context.getRequest()
				.getParameterValues("lock_id");
		String[] lock_dateArr = context.getRequest().getParameterValues(
				"lock_date");
		String[] passwordsArr = context.getRequest().getParameterValues(
				"passwords");
		String[] isAtificialArr = context.getRequest().getParameterValues(
				"IS_ARTIFICIAL");
		String[] unlock_dateArr = context.getRequest().getParameterValues(
				"unlock_date");
		String[] remarkArr = HTMLUtil.getParameterValues(context.request, "remark", "");
		String[] remin_dayArr = context.getRequest().getParameterValues("remin_day");
		String[] send_wayArr = context.getRequest().getParameterValues("send_way");
		
		//Add by Michael 2012 04-25 增加期数,支付表ID
		String[] period_numArr = context.getRequest()
				.getParameterValues("period_num");
		String[] recp_idArr = context.getRequest()
				.getParameterValues("recp_id");
		
		if (todayArr!=null) {
			
			for (int i = 0; i < todayArr.length; i++) {
				Map map = new HashMap();
				// String today = todayArr[i];
				// int day = today == null ? 1 : Integer.parseInt(today);
				String rent_id = rent_idArr[i];
				String lock_id = lock_idArr[i];
				String lock_date = lock_dateArr[i];
				String passwords = passwordsArr[i];
				String isAtificial = isAtificialArr[i];
				String unlock_date = unlock_dateArr[i];
				String remark = remarkArr[i];
				String remin_day = remin_dayArr[i];
				String send_way = send_wayArr[i];
				
				//Add by Michael 2012 04-25 增加期数,支付表ID
				String period_num = period_numArr[i];
				String recp_id = recp_idArr[i];
				
				// if (isunlockArr != null) {
				// String isunlock = isunlockArr[i];
				// map.put("isunlock", isunlock);
				// } else {
				// map.put("isunlock", "1");
				// }
				// if (context.contextMap.get("isunlock" + (i + 1)) == null) {
				//
				// }
				
				map.put("isAtificial", isAtificial);
				map.put("rent_id", rent_id);
				map.put("lock_id", lock_id);
				map.put("lock_date", lock_date);
				map.put("passwords", passwords);
				map.put("unlock_date", unlock_date);
				
				map.put("remark", remark);
				map.put("remin_day", remin_day);
				map.put("send_way", send_way);
				map.put("rent_detail_id", context.contextMap.get("rent_detail_id"));
				map.put("s_employeeId", context.contextMap.get("s_employeeId"));
				
				//Add by Michael 2012 04-25 增加期数
				map.put("period_num", period_num);
				map.put("recp_id", context.contextMap.get("recp_id1"));
				map.put("eqmt_id", context.contextMap.get("eqmt_id1"));

				try {
					if (context.contextMap.get("lock_id") == null
							|| context.contextMap.get("lock_id").equals("")) {
						DataAccessor.execute("lockManagement.create", map,
								DataAccessor.OPERATION_TYPE.INSERT);
					} else {
						DataAccessor.execute("lockManagement.modify", map,
								DataAccessor.OPERATION_TYPE.UPDATE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}

		Output.jspOutput(outputMap, context,
				"/servlet/defaultDispatcher?__action=rentContract.queryRentContractByPlay");
	}

	/**
	 * 查询锁码所有信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void show(Context context) {
		Map outputMap = new HashMap();
//		if (context.contextMap.get("flag") == null) {
//			context.contextMap.put("start_date", "");
//			context.contextMap.put("end_date", "");
//		}
		List errList = context.errList;
		DataWrap dw = null;
		if (errList.isEmpty()) {
		try {
			dw = (DataWrap) DataAccessor.query("lockManagement.queryAll",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		
//		String startTime=null;
//		if(context.contextMap.get("start_date")==null || context.contextMap.get("start_date").equals(""))
//		{
//			 Date now =  new Date();  
//			  SimpleDateFormat dateFormat   =new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式  
//			  startTime =dateFormat.format(now); 
//		}
//		else
//		{
//			startTime=context.contextMap.get("start_date").toString();
//		}
//		
//		outputMap.put("start_date", startTime);
		String endTime=null;
		if(context.contextMap.get("end_date")==null || context.contextMap.get("end_date").equals(""))
		{
			 Date now =  new Date();  
			  SimpleDateFormat dateFormat   =new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式  
			  endTime =dateFormat.format(now); 
		}
		else
		{
			endTime=context.contextMap.get("end_date").toString();
		}
//		去除锁码起租
//		String payFlag=null;
//		if(context.contextMap.get("paydate_flag")==null || context.contextMap.get("paydate_flag").equals(""))
//		{
//			outputMap.put("paydate_flag", null);
//		}
//		else
//		{
//			outputMap.put("paydate_flag", context.contextMap.get("paydate_flag"));
//		}
		outputMap.put("end_date", endTime);
		Output.jspOutput(outputMap, context, "/lockManagement/lockShow.jsp");
	}
	
	/**
	 * 查询锁码下的客户及设备信息
	 * 
	 * @param context
	 */
	public void custShow(Context context)
	{
		Map outputMap = new HashMap();
		Map queryCustManager = null;
		try {
			queryCustManager = (Map) DataAccessor.query("lockManagement.queryCustByLock_Id",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("queryCustManager", queryCustManager);
		Output.jsonOutput(outputMap, context);
		
	}
	
	
	/**
	 * 发送短信
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateLockSendManager(Context context)
	{
		Map outputMap = new HashMap();
		List send = new ArrayList();
		String returnStr=null;
		try {
				context.contextMap.put("SENDTYPE", 1);
				context.contextMap.put("STATE", 7);
				send.add(context.contextMap);
				SendMsg sendmsg = new SendMsg();
				returnStr = sendmsg.SendSMSMsg(context, send);
				
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("returnStr", returnStr);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改锁码发送方式
	 * @param context
	 */
	public void updateLockSendEmail(Context context)
	{
		Map outputMap = new HashMap();

		try {
			//System.out.println("yungxinglea?="+context.contextMap.get("state"));
				DataAccessor.execute("lockManagement.createSendEmail", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				context.contextMap.put("LOCK_ID", context.contextMap.get("lock_id"));
				DataAccessor.execute("lockManagement.updatLockManageRealeaseLock", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			
		} catch (Exception e) {
			//outputMap.put("rsCount", rsCount);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	
	public void updateLockSendEmailError(Context context)
	{
		Map outputMap = new HashMap();

		try {
			//System.out.println("yungxinglea?="+context.contextMap.get("state"));
				DataAccessor.execute("lockManagement.createSendEmail", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
			
		} catch (Exception e) {
			//outputMap.put("rsCount", rsCount);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	public void sendEmail(Context context)
	{
		Map outputMap = new HashMap();
		Map sendManager=new HashMap();
		String returnStr=null;
		try {
			String subject =context.request.getParameter("subject");
			String toAddress =context.request.getParameter("toAddress");
			String pay_Date=context.request.getParameter("pay_Date");
			String end_dateFomate="";
    		if(pay_Date!=null && !"".equals(pay_Date))
    		{
    			end_dateFomate=pay_Date.substring(0,pay_Date.length()-11);
    		}
			String thing_name=context.request.getParameter("thing_name");
			String model_spec=context.request.getParameter("model_spec");
			String amount=context.request.getParameter("amount");
			String unit=context.request.getParameter("unit");
			String cust_name=context.request.getParameter("cust_name");
			String pass=context.request.getParameter("pass");
			
			
			String content="您好，"+cust_name+"，设备："+thing_name+"，型号："+model_spec+"，支付时间："+end_dateFomate+"，密码："+pass;
			sendManager.put("subject", subject);
			sendManager.put("toAddress", toAddress);
			sendManager.put("content", content);
			
			returnStr=TestMail.seneMail(sendManager);
			 
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("returnStr", returnStr);
		Output.jsonOutput(outputMap, context);
	}
	
	//定时发送邮件
	public void sendEmailToTime()
	{
		List lockList=null;
		Map lockMap=null;
		Map sendManager=new HashMap();
		Map outmap=new HashMap();
		
		try
		{
			lockList = (List)DataAccessor.query("lockManagement.selectIsEmailToTime",outmap,DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;i<lockList.size();i++)
			{
				lockMap=(Map)lockList.get(i);
				Map createLockMap=new HashMap();
				
				//发送邮件
				String subject ="锁码开通";
				String toAddress =lockMap.get("CORP_COMPANY_EMAIL").toString();
				String thing_name=lockMap.get("THING_NAME").toString();
				String model_spec=lockMap.get("MODEL_SPEC").toString();
				String cust_name=lockMap.get("CUST_NAME").toString();
				String pass=lockMap.get("PASSWORDS").toString();
				
				String content="您好，"+cust_name+"，设备："+thing_name+"，型号："+model_spec+"，密码："+pass;
				sendManager.put("subject", subject);
				sendManager.put("toAddress", toAddress);
				sendManager.put("content", content);
				
				String returnStr=TestMail.seneMail(sendManager);
				
				
				createLockMap.put("rent_id", lockMap.get("rent_id"));
				createLockMap.put("rent_detail_id", lockMap.get("rent_detail_id"));
				createLockMap.put("sendemail", lockMap.get("CORP_COMPANY_EMAIL"));
				createLockMap.put("passwords", lockMap.get("passwords"));
				createLockMap.put("sendsms", lockMap.get(""));
				if(returnStr.equals("邮件发送成功"))
				{
					createLockMap.put("SENDSTATE", 1);
				}
				else
				{
					createLockMap.put("SENDSTATE", 0);
				}
				DataAccessor.execute("lockManagement.createSendEmailToTime",createLockMap,DataAccessor.OPERATION_TYPE.INSERT);
				
				
				
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	//定时发送短信
	public void sendSmSToTime()
	{
		List lockList=null;
		Map lockMap=null;
		Map sendManager=new HashMap();
		Map outmap=new HashMap();
		List msgList= null;
		try
		{
			lockList = (List)DataAccessor.query("lockManagement.selectIsEmailToTime",outmap,DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;i<lockList.size();i++)
			{
				lockMap=(Map)lockList.get(i);
				List RenterPhones =  (List)DataAccessor.query("lockManagement.judgeRenterPhone",lockMap,DataAccessor.RS_TYPE.LIST);
				if(RenterPhones.size()>0){
					Map createLockMap=new HashMap();
					msgList=new ArrayList();
					createLockMap.put("rent_id", lockMap.get("rent_id"));
					createLockMap.put("rent_detail_id", lockMap.get("rent_detail_id"));
					createLockMap.put("sendemail", lockMap.get("CORP_COMPANY_EMAIL"));
					createLockMap.put("passwords", lockMap.get("passwords"));
					createLockMap.put("sendsms", lockMap.get(""));
					
					
					//发送短信
					String subject ="锁码";
					String mtel=((HashMap)RenterPhones.get(0)).get("RENTER_PHONE")==null?"13249487890":((HashMap)RenterPhones.get(0)).get("RENTER_PHONE").toString();
					
					String thing_name=lockMap.get("THING_NAME").toString();
					String model_spec=lockMap.get("MODEL_SPEC").toString();
					String cust_name=lockMap.get("CUST_NAME").toString();
					String pass=lockMap.get("PASSWORDS").toString();
					/*
					String thing_name="ceshi";
					String model_spec="ceshi";
					String cust_name="ceshi";
					String pass="ceshi";
					*/
					String content="您好，"+cust_name+"，设备："+thing_name+"，型号："+model_spec+"，密码："+pass;
					sendManager.put("MTEL", mtel);
					sendManager.put("MESSAGE", content);
					sendManager.put("LOCK_ID", lockMap.get("LOCK_ID"));
					sendManager.put("PASSWORDS", lockMap.get("PASSWORDS"));
					sendManager.put("SENDTYPE", lockMap.get("SENDTYPE"));
					sendManager.put("SENDEMAIL", lockMap.get("SENDEMAIL"));
					sendManager.put("SENDSMS", lockMap.get("SENDSMS"));
					sendManager.put("RECD_ID", lockMap.get("RECD_ID"));
					sendManager.put("RENT_DETAIL_ID", lockMap.get("RENT_DETAIL_ID"));
					sendManager.put("STATE", lockMap.get("STATE"));
					sendManager.put("LEASE_CODE", lockMap.get("LEASE_CODE"));
					msgList.add(sendManager);
					SendMsg sendmsg = new SendMsg();
					sendmsg.SendSMSMsg(msgList);
				}
				
			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	//定时发送逾期短信
	public void sendSmSToDunTime()
	{
		List dunList=null;
		Map dunMap=null;
		Map sendManager=new HashMap();
		Map outmap=new HashMap();
		List msgList=new ArrayList();
		try
		{
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunTime",outmap,DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;i<dunList.size();i++)
			{
				dunMap=(Map)dunList.get(i);
				Object RenterPhones=dunMap.get("CORP_HS_LINK_MODE");
				if(RenterPhones!=null && !"".equals(RenterPhones)){
				
					//发送短信
					String subject ="逾期提醒";
					String mtel=RenterPhones.toString();
					
					String cust_name=dunMap.get("CUST_NAME").toString();
					String LEASE_CODE=dunMap.get("LEASE_CODE").toString();
					String SHOULD_PAYDATE=dunMap.get("SHOULD_PAYDATE").toString();
					String DUN_DAY=dunMap.get("DUN_DAY").toString();
					String DUN_MONTHPRICE=dunMap.get("DUN_MONTHPRICE").toString();
					
					String content="您好，"+cust_name+"，合同号："+LEASE_CODE+"，应该在"+SHOULD_PAYDATE+"缴纳租金，现已逾期"+DUN_DAY+"天，罚息为"+DUN_MONTHPRICE+"元！";
					
					sendManager.put("MTEL", mtel);
					sendManager.put("MESSAGE", content);
					sendManager.put("SENDTYPE", "0");
					sendManager.put("STATE", "8");
					sendManager.put("LEASE_CODE", LEASE_CODE);
					
					msgList.add(sendManager);
					SendMsg sendmsg = new SendMsg();
					sendmsg.SendSMSMsg(msgList);
				}
				
			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 2012/02/22 Yang Yun
	 * 定时发短信JOB
	 */
	public void sendSmsDailyForDun() throws Exception{
		System.out.println("=======================生成催收短信JOB.Start=====================");
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			//发送设备催收短信
			/*paramMap.put("production_type", "1");
			paramMap.put("dun_day", "(-3, 1, 5, 7)");
			this.sendForEquipment(paramMap);*/
			
			//发送重车催收短信
			/*paramMap.put("production_type", "2");
			paramMap.put("dun_day", "(-5, -1, 3, 5)");
			this.sendForAuto(paramMap);*/
			
			//发送乘用车催收短信
			paramMap.put("production_type", "3");
			paramMap.put("dun_day", "(-3)");
			paramMap.put("dun_days", 3);
			this.sendForCar(paramMap);
		} catch (Exception e) {
			throw e;
		}
		System.out.println("=======================生成催收短信JOB.End=====================");
	}
	
	/**
	 * 2012/02/22 Yang Yun
	 * 设备
	 * @param paramMap
	 * @throws Exception 
	 */
	private void sendForEquipment(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> dunList = null;
		List<Map<String, Object>> smsList = new ArrayList<Map<String,Object>>();
		try {
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime", paramMap, DataAccessor.RS_TYPE.LIST);
			for (Map<String, Object> dunMap : dunList) {
				try {
					smsList.addAll(getSmsForEquipment(dunMap));
				} catch (SendSmsProcessException e) {
					logger.warn(e.getMessage());
					break;
				} catch (Exception e) {
					throw e;
				}
			}
			SendMsg.SendSMSMsg(smsList);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 2012/02/22 Yang Yun
	 * 重车
	 * @param paramMap
	 * @throws Exception 
	 */
	private void sendForAuto(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> dunList = null;
		List<Map<String, Object>> smsList = new ArrayList<Map<String,Object>>();
		try {
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime", paramMap, DataAccessor.RS_TYPE.LIST);
			for (Map<String, Object> dunMap : dunList) {
				try {
					smsList.addAll(getSmsForAuto(dunMap));
				} catch (SendSmsProcessException e) {
					logger.warn(e.getMessage());
					break;
				} catch (Exception e) {
					throw e;
				}
			}
			SendMsg.SendSMSMsg(smsList);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 2014/02/21 徐伟
	 * 乘用车
	 * @param paramMap
	 * @throws Exception 
	 */
	private void sendForCar(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> dunList = null;
		List<Map<String, Object>> dunList2 = null;
		List<Map<String, Object>> smsList = new ArrayList<Map<String,Object>>();
		try {
			//即将到期提醒
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTimeForCarBefore", paramMap, DataAccessor.RS_TYPE.LIST);
			
			for (Map<String, Object> dunMap : dunList) {
				try {
					smsList.addAll(getSmsForCar(dunMap));
				} catch (SendSmsProcessException e) {
					logger.warn(e.getMessage());
					break;
				} catch (Exception e) {
					throw e;
				}
			}
			//已逾期提醒（工作日）
			dunList2 = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTimeForCar", paramMap, DataAccessor.RS_TYPE.LIST);
			for (Map<String, Object> dunMap : dunList2) {
				try {
					smsList.addAll(getSmsForCarDunDays(dunMap));
				} catch (SendSmsProcessException e) {
					logger.warn(e.getMessage());
					break;
				} catch (Exception e) {
					throw e;
				}
			}
			SendMsg.SendSMSMsg(smsList);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 2012/02/22 Yang Yun
	 * 拼装设备短信
	 * 2012/03/01 Shen Qi
	 * 增加插入Send_TEST表的数据
	 * @param dunMap 
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> getSmsForEquipment(Map<String, Object> dunMap) throws Exception {
		List<Map<String, Object>> resultList = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Map<String, Object> sms = null;
		String mtel = (String) dunMap.get("LINK_MOBILE");
		//联系人手机号没有找到 就跳过 不发
		if (StringUtils.isEmpty(mtel)) {
			throw new SendSmsProcessException("联系人手机号没有找到.");
		}
		sms = new HashMap<String, Object>();
		resultList = new ArrayList<Map<String,Object>>();
		String subject = "租金提醒";
		String cust_name = dunMap.get("CUST_NAME").toString();
		String LEASE_CODE = dunMap.get("LEASE_CODE").toString();
		Object PAY_DATE = dunMap.get("PAY_DATE");
		Integer dun_day = (Integer) dunMap.get("DUN_DAY");
		String RECP_CODE = dunMap.get("RECP_CODE").toString();
		String IRR_MONTH_PRICE = dunMap.get("IRR_MONTH_PRICE").toString();
		String content = null;
		if (dun_day == -3) {
			content = "("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +
					" 元整，将于三日后到期，请及时缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
		} else if (dun_day == 1) {
			content = "("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +
					" 元整，已逾期一天尚未缴纳，裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
		} else if (dun_day == 5) {
			content = "("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +
					" 元整，已逾期五天尚未缴纳，裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
		} else if (dun_day == 7) {
			content = "("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +
					" 元整，已逾期七天尚未缴纳，裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
		} else {
			throw new SendSmsProcessException("逾期天数不正确。");
		}
		sms.put("MESSAGE", content);
		sms.put("SENDTYPE", "0");
		sms.put("STATE", "2");
		sms.put("LEASE_CODE", LEASE_CODE);
		sms.put("MTEL", mtel);
		
		//add by ShenQi,为插入SEND_TEST表准备数据
		sms.put("CONTRACT_NUMBER",LEASE_CODE);
		sms.put("CUST_NAME",cust_name);
		sms.put("CREATE_BY","System");//send by batch job
		sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
		sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		sms.put("LOG","getSmsForEquipment");
		
		resultList.add(sms);
//		String user_mobile = (String) dunMap.get("USER_MOBILE");
//		if (!StringUtils.isEmpty(user_mobile)) {
//			sms = new HashMap<String, Object>();
//			sms.put("MESSAGE", content);
//			sms.put("SENDTYPE", "0");
//			sms.put("STATE", "2");
//			sms.put("LEASE_CODE", LEASE_CODE);
//			sms.put("MTEL", user_mobile);
//			resultList.add(sms);
//		} else {
//			logger.warn("客户经理手机号找不到。");
//		}
		return resultList;
	}
	
	/**
	 * 2012/02/22 Yang Yun
	 * 拼装重车短信
	 * 2012/03/01 Shen Qi
	 * 增加插入Send_TEST表的数据
	 * @param dunMap
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> getSmsForAuto(Map<String, Object> dunMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Map<String, Object> sms = null;
		String mtel = (String) dunMap.get("LINK_MOBILE");
		//联系人手机号没有找到 就跳过 不发
		if (StringUtils.isEmpty(mtel)) {
			throw new SendSmsProcessException("联系人手机号没有找到.");
		}
		sms = new HashMap<String, Object>();
		resultList = new ArrayList<Map<String,Object>>();
		String subject = "租金提醒";
		String cust_name = dunMap.get("CUST_NAME").toString();
		String LEASE_CODE = dunMap.get("LEASE_CODE").toString();
		Object PAY_DATE = dunMap.get("PAY_DATE");
		Integer dun_day = (Integer) dunMap.get("DUN_DAY");
		String RECP_CODE = dunMap.get("RECP_CODE").toString();
		String IRR_MONTH_PRICE = dunMap.get("IRR_MONTH_PRICE").toString();
		String content = null;
		if (dun_day == -5) {
			content = "敬爱的" + cust_name +"，您本期车辆租金为  "+ IRR_MONTH_PRICE +" 元整，将于五日后到期，请及时缴款。若您已缴纳，请不予理会本信息。" ;
		} else if (dun_day == -1) {
			content = "敬爱的" + cust_name +"，您本期车辆租金为  "+ IRR_MONTH_PRICE +" 元整，将于一日后到期，请及时缴款。若您已缴纳，请不予理会本信息。" ;
		} else if (dun_day == 1) {
			content = "致 " + cust_name +"，于" + sdf.format(PAY_DATE) + "应缴车辆租金" + IRR_MONTH_PRICE +
					" 元整，现已到期，为避免产生滞纳金，特此通知缴纳(若您已缴纳，请不予理会本信息)。 " ;
		} else if (dun_day == 3) {
			content = "致 " + cust_name +"，于" + sdf.format(PAY_DATE) + "应缴车辆租金" + IRR_MONTH_PRICE +
					" 元整，现已逾期三日并已产生滞纳金，特此通知缴纳(若您已缴纳，请不予理会本信息)。 " ;
		} else if (dun_day == 5) {
			content = "致 " + cust_name +"，于" + sdf.format(PAY_DATE) + "应缴车辆租金" + IRR_MONTH_PRICE +
					" 元整，现已逾期五日并已产生滞纳金，特此通知缴纳(若您已缴纳，请不予理会本信息)。 " ;
		} else {
			throw new SendSmsProcessException("逾期天数不正确。");
		}
		sms.put("MESSAGE", content);
		sms.put("SENDTYPE", "0");
		sms.put("STATE", "2");
		sms.put("LEASE_CODE", LEASE_CODE);
		sms.put("MTEL", mtel);
		
		//add by ShenQi,为插入SEND_TEST表准备数据
		sms.put("CONTRACT_NUMBER",LEASE_CODE);
		sms.put("CUST_NAME",cust_name);
		sms.put("CREATE_BY","System");//send by batch job
		sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
		sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		sms.put("LOG","getSmsForAuto");
		
		resultList.add(sms);
		/**
		 * 增加连带保证人短信通知
		 */
		String creditId = LeaseUtil.getCreditIdByLeaseCode(LEASE_CODE);
		List<PayerTo> payers = payerService.getPayersByCreditId(Integer.parseInt(creditId));
		if(payers!=null && payers.size()>0){
			for(int i=0;i<payers.size();i++){
				sms = new HashMap<String, Object>();
				sms.put("MESSAGE", content);
				sms.put("SENDTYPE", "0");
				sms.put("STATE", "2");
				sms.put("LEASE_CODE", LEASE_CODE);
				sms.put("MTEL", payers.get(i).getLinkmanMobile());
				
			
				sms.put("CONTRACT_NUMBER",LEASE_CODE);
				sms.put("CUST_NAME",cust_name);
				sms.put("CREATE_BY","System");//send by batch job
				sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
				sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
				sms.put("LOG","getSmsForAuto");
				resultList.add(sms);
			}
		}

		
		String user_mobile = (String) dunMap.get("USER_MOBILE");
		if (!StringUtils.isEmpty(user_mobile)) {
			sms = new HashMap<String, Object>();
			sms.put("MESSAGE", content);
			sms.put("SENDTYPE", "0");
			sms.put("STATE", "2");
			sms.put("LEASE_CODE", LEASE_CODE);
			sms.put("MTEL", user_mobile);
			
			//add by ShenQi,为插入SEND_TEST表准备数据
			sms.put("CONTRACT_NUMBER",LEASE_CODE);
			sms.put("CUST_NAME",cust_name);
			sms.put("CREATE_BY","System");//send by batch job
			sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
			sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
			sms.put("LOG","getSmsForAuto");
			resultList.add(sms);
		} else {
			logger.warn("客户经理手机号找不到。");
		}
		return resultList;
	}
	
	
	/**
	 * 徐伟 2014-2-21
	 * 拼装乘用车短信 即将到期提醒
	 * 
	 * 增加插入Send_TEST表的数据
	 * @param dunMap 
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> getSmsForCar(Map<String, Object> dunMap) throws Exception {
		List<Map<String, Object>> resultList = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Map<String, Object> sms = null;
		String mtel = (String) dunMap.get("LINK_MOBILE");
		//联系人手机号没有找到 就跳过 不发
		if (StringUtils.isEmpty(mtel)) {
			throw new SendSmsProcessException("联系人手机号没有找到.");
		}
		sms = new HashMap<String, Object>();
		resultList = new ArrayList<Map<String,Object>>();
		String subject = "租金提醒";
		String cust_name = dunMap.get("CUST_NAME").toString();
		String LEASE_CODE = dunMap.get("LEASE_CODE").toString();
		//Object PAY_DATE = dunMap.get("PAY_DATE");
		Integer dun_day = (Integer) dunMap.get("DUN_DAY");
		//String RECP_CODE = dunMap.get("RECP_CODE").toString();
		//String IRR_MONTH_PRICE = dunMap.get("IRR_MONTH_PRICE").toString();
		String content = null;
		if (dun_day == -3) {
			content = "尊敬的客户，您本期的车贷将于三日后到期，请及时缴纳，感谢您的配合。若您已缴纳，请不予理会本信息。裕融租赁 0512-80983566" ;
		} else {
			throw new SendSmsProcessException("逾期天数不正确。");
		}
		sms.put("MESSAGE", content);
		sms.put("SENDTYPE", "0");
		sms.put("STATE", "2");
		sms.put("LEASE_CODE", LEASE_CODE);
		sms.put("MTEL", mtel);
		
		//add by ShenQi,为插入SEND_TEST表准备数据
		sms.put("CONTRACT_NUMBER",LEASE_CODE);
		sms.put("CUST_NAME",cust_name);
		sms.put("CREATE_BY","System");//send by batch job
		sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
		sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		sms.put("LOG","getSmsForCar");
		
		resultList.add(sms);
//		String user_mobile = (String) dunMap.get("USER_MOBILE");
//		if (!StringUtils.isEmpty(user_mobile)) {
//			sms = new HashMap<String, Object>();
//			sms.put("MESSAGE", content);
//			sms.put("SENDTYPE", "0");
//			sms.put("STATE", "2");
//			sms.put("LEASE_CODE", LEASE_CODE);
//			sms.put("MTEL", user_mobile);
//			resultList.add(sms);
//		} else {
//			logger.warn("客户经理手机号找不到。");
//		}
		return resultList;
	}
	
	/**
	 * 徐伟 2014-2-21
	 * 拼装乘用车短信 逾期3个工作日提醒
	 * 
	 * 增加插入Send_TEST表的数据
	 * @param dunMap 
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> getSmsForCarDunDays(Map<String, Object> dunMap) throws Exception {
		List<Map<String, Object>> resultList = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Map<String, Object> sms = null;
		String mtel = (String) dunMap.get("LINK_MOBILE");
		//联系人手机号没有找到 就跳过 不发
		if (StringUtils.isEmpty(mtel)) {
			throw new SendSmsProcessException("联系人手机号没有找到.");
		}
		sms = new HashMap<String, Object>();
		resultList = new ArrayList<Map<String,Object>>();
		String subject = "租金提醒";
		String cust_name = dunMap.get("CUST_NAME").toString();
		String LEASE_CODE = dunMap.get("LEASE_CODE").toString();
		//Object PAY_DATE = dunMap.get("PAY_DATE");
		Integer dun_day = (Integer) dunMap.get("DUN_DAYS");
		//String RECP_CODE = dunMap.get("RECP_CODE").toString();
		//String IRR_MONTH_PRICE = dunMap.get("IRR_MONTH_PRICE").toString();
		String content = null;
		
		if (dun_day == 3) {
			content = "尊敬的客户，您本期的车贷现已逾期三个工作日未缴，并已产生滞纳金，特此通知，请及时缴款。若您已缴纳，请不予理会本信息。裕融租赁 0512-80983566" ;
		}else {
			throw new SendSmsProcessException("逾期天数不正确。");
		}
		sms.put("MESSAGE", content);
		sms.put("SENDTYPE", "0");
		sms.put("STATE", "2");
		sms.put("LEASE_CODE", LEASE_CODE);
		sms.put("MTEL", mtel);
		
		//add by ShenQi,为插入SEND_TEST表准备数据
		sms.put("CONTRACT_NUMBER",LEASE_CODE);
		sms.put("CUST_NAME",cust_name);
		sms.put("CREATE_BY","System");//send by batch job
		sms.put("SEND_MODE","1");//0 means 手动, 1 means 自动
		sms.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
		sms.put("LOG","getSmsForCar");
		
		resultList.add(sms);
//		String user_mobile = (String) dunMap.get("USER_MOBILE");
//		if (!StringUtils.isEmpty(user_mobile)) {
//			sms = new HashMap<String, Object>();
//			sms.put("MESSAGE", content);
//			sms.put("SENDTYPE", "0");
//			sms.put("STATE", "2");
//			sms.put("LEASE_CODE", LEASE_CODE);
//			sms.put("MTEL", user_mobile);
//			resultList.add(sms);
//		} else {
//			logger.warn("客户经理手机号找不到。");
//		}
		return resultList;
	}
	
	class SendSmsProcessException extends Exception{
		public SendSmsProcessException(String message){
			super(message);
		}
	}
	
	
	//定时发送租金催收短信（提前三天发送）
	/*public void sendSmSToDunRentTime()
	{
		List dunList=null;
		Map dunMap=null;
		Map sendManager=new HashMap();
		Map outmap=new HashMap();
		List msgList1=new ArrayList();
		List msgList2=new ArrayList();
		List msgList3=new ArrayList();
		List msgList4=new ArrayList();
		try
		{//
			outmap.put("day", -3) ;
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime",outmap,DataAccessor.RS_TYPE.LIST);
			Map<String, Object> mailMap = new HashMap<String, Object>();
			for(int i=0;i<dunList.size();i++)
			{	
					dunMap=(Map)dunList.get(i);
					Object RenterPhones=dunMap.get("LINK_MOBILE");
					if(RenterPhones!=null && RenterPhones!=""){
						sendManager = new HashMap();
						msgList1=new ArrayList();
						//发送短信
						String subject ="租金提醒";
						String mtel=RenterPhones.toString();
						
						String cust_name=dunMap.get("CUST_NAME").toString();
						String LEASE_CODE=dunMap.get("LEASE_CODE").toString();
						String PAY_DATE=dunMap.get("PAY_DATE").toString();
						String RECP_CODE=dunMap.get("RECP_CODE").toString();
						String IRR_MONTH_PRICE=dunMap.get("IRR_MONTH_PRICE").toString();
						String content="("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +"  元整，将于三日后到期，" +
								"请及时缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
						sendManager.put("MTEL", mtel);
						sendManager.put("MESSAGE", content);
						sendManager.put("SENDTYPE", "0");
						sendManager.put("STATE", "2");
						sendManager.put("LEASE_CODE", LEASE_CODE);
						msgList1.add(sendManager);
						SendMsg sendmsg = new SendMsg();
						sendmsg.SendSMSMsg(msgList1);
					}
				}
			//逾期隔天
			outmap.put("day", 1) ;
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime",outmap,DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<dunList.size();i++)
			{	
				dunMap=(Map)dunList.get(i);
				Object RenterPhones=dunMap.get("LINK_MOBILE");
				if(RenterPhones!=null && RenterPhones!=""){
					sendManager = new HashMap();
					msgList2=new ArrayList();
					//发送短信
					String subject ="租金提醒";
					String mtel=RenterPhones.toString();
					
					String cust_name=dunMap.get("CUST_NAME").toString();
					String LEASE_CODE=dunMap.get("LEASE_CODE").toString();
					String PAY_DATE=dunMap.get("PAY_DATE").toString();
					String RECP_CODE=dunMap.get("RECP_CODE").toString();
					String IRR_MONTH_PRICE=dunMap.get("IRR_MONTH_PRICE").toString();
					String content="("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +"  元整，已逾期1天尚未缴纳，" +
					"裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
					sendManager.put("MTEL", mtel);
					sendManager.put("MESSAGE", content);
					sendManager.put("SENDTYPE", "0");
					sendManager.put("STATE", "2");
					sendManager.put("LEASE_CODE", LEASE_CODE);
					msgList2.add(sendManager);
					SendMsg sendmsg = new SendMsg();
					sendmsg.SendSMSMsg(msgList2);
				}
			}
			//逾期5天
			outmap.put("day", 5) ;
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime",outmap,DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<dunList.size();i++)
			{	
				dunMap=(Map)dunList.get(i);
				Object RenterPhones=dunMap.get("LINK_MOBILE");
				if(RenterPhones!=null && RenterPhones!=""){
					sendManager = new HashMap();
					msgList3=new ArrayList();
					//发送短信
					String subject ="租金提醒";
					String mtel=RenterPhones.toString();
					
					String cust_name=dunMap.get("CUST_NAME").toString();
					String LEASE_CODE=dunMap.get("LEASE_CODE").toString();
					String PAY_DATE=dunMap.get("PAY_DATE").toString();
					String RECP_CODE=dunMap.get("RECP_CODE").toString();
					String IRR_MONTH_PRICE=dunMap.get("IRR_MONTH_PRICE").toString();
					String content="("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +"  元整，已逾期5天尚未缴纳，" +
					"裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
					sendManager.put("MTEL", mtel);
					sendManager.put("MESSAGE", content);
					sendManager.put("SENDTYPE", "0");
					sendManager.put("STATE", "2");
					sendManager.put("LEASE_CODE", LEASE_CODE);
					msgList3.add(sendManager);
					SendMsg sendmsg = new SendMsg();
					sendmsg.SendSMSMsg(msgList3);
				}
			}
			//逾期7天
			outmap.put("day", 7) ;
			dunList = (List)DataAccessor.query("dunDaily.selectIsSmsToDunRentTime",outmap,DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<dunList.size();i++)
			{	
				dunMap=(Map)dunList.get(i);
				Object RenterPhones=dunMap.get("LINK_MOBILE");
				if(RenterPhones!=null && RenterPhones!=""){
					sendManager = new HashMap();
					msgList4=new ArrayList();
					//发送短信
					String subject ="租金提醒";
					String mtel=RenterPhones.toString();
					
					String cust_name=dunMap.get("CUST_NAME").toString();
					String LEASE_CODE=dunMap.get("LEASE_CODE").toString();
					String PAY_DATE=dunMap.get("PAY_DATE").toString();
					String RECP_CODE=dunMap.get("RECP_CODE").toString();
					String IRR_MONTH_PRICE=dunMap.get("IRR_MONTH_PRICE").toString();
					String content="("+LEASE_CODE+ ") (" + cust_name +") 敬爱的客户，您本期租金  "+ IRR_MONTH_PRICE +"  元整，已逾期7天尚未缴纳，" +
					"裕融租赁友情提醒您尽速缴款，若您已缴纳，请不予理会本信息。 业管部  0512-80983566" ;
					sendManager.put("MTEL", mtel);
					sendManager.put("MESSAGE", content);
					sendManager.put("SENDTYPE", "0");
					sendManager.put("STATE", "2");
					sendManager.put("LEASE_CODE", LEASE_CODE);
					msgList4.add(sendManager);
					SendMsg sendmsg = new SendMsg();
					sendmsg.SendSMSMsg(msgList4);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}*/
	
	
	/*public static void main(String args[]){
		LockManagementService lock=new LockManagementService();
		
		lock.sendSmSToTime();
		System.out.println("执行完毕");
	}*/
	@SuppressWarnings("unchecked")
	public void sendMessage(Context context)
	{
		Map outputMap = new HashMap();
		Map sendManager=new HashMap();
		List send = new ArrayList();
		String returnStr=null;
		try {
			String subject =context.request.getParameter("subject");
			String toAddress =context.request.getParameter("toAddress");
			String pay_Date=context.request.getParameter("pay_Date");
			String end_dateFomate="";
    		if(pay_Date!=null && !"".equals(pay_Date))
    		{
    			end_dateFomate=pay_Date.substring(0,pay_Date.length()-11);
    		}
			String thing_name=context.request.getParameter("thing_name");
			String model_spec=context.request.getParameter("model_spec");
			String amount=context.request.getParameter("amount");
			String unit=context.request.getParameter("unit");
			String cust_name=context.request.getParameter("cust_name");
			String pass=context.request.getParameter("pass");
			
			
			String content="您好，"+cust_name+"，设备："+thing_name+"，型号："+model_spec+"，支付时间："+end_dateFomate+"，密码："+pass;
			sendManager.put("subject", subject);
			sendManager.put("toPhone", toAddress);
			sendManager.put("content", content);
			send.add(sendManager);
			
			returnStr="123";//SendSms.sendSms(sendManager);
			 
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("returnStr", returnStr);
		Output.jsonOutput(outputMap, context);
	}

	public void deleteLockCodePasswordFile(Context context) throws IOException {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		String paramString="";
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			sqlMapClient.insert(
					"lockManagement.deleteLockCodePasswordFile", context.contextMap);
				paramString="&rect_id="+context.contextMap.get("RECT_ID")+"&eqmt_id="+context.contextMap.get("EQMT_ID")+"&recp_id="+context.contextMap.get("RECP_ID");
			sqlMapClient.commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(errList.isEmpty()){
			outputMap.put("returnStr", "删除成功！");
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 保存附件
	 * 
	 * @param context
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void uploadAll(Context context) throws IOException {
		List fileItems = (List) context.contextMap.get("uploadList");
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		String paramString="";
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			InputStream in = fileItem.getInputStream();
			if (!fileItem.getName().equals("")) {
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem, sqlMapClient);
					paramString="&rect_id="+context.contextMap.get("RECT_ID")+"&eqmt_id="+context.contextMap.get("EQMT_ID")+"&recp_id="+context.contextMap.get("RECP_ID");
					sqlMapClient.commitTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} finally {
					try {
						sqlMapClient.endTransaction();
					} catch (SQLException e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
			}
		}
		if(errList.isEmpty()){
			outputMap.put("returnStr", "上传成功");
			Output.jspSendRedirect(context,"defaultDispatcher?__action=lockManagement.getLockManagement"+paramString);	
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * 
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,
			SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("lockcodepassword");
		String file_path = "";
		String file_name = "";
		Long syupId = null;
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
			String excelNewName = FileExcelUpload.getNewFileName();
			File uploadedFile = new File(realPath.getPath() + File.separator
					+ excelNewName + "." + type);
			file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName
					+ "." + type;
			file_name = excelNewName + "." + type;
			try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					contextMap.put("file_path", file_path);
					contextMap.put("file_name", fileItem.getName());
					contextMap.put("title", "锁码密码附件");

					sqlMapClient.insert(
							"lockManagement.insertLockCodePasswordFile", contextMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					fileItem.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
				fileItem.delete();
			}
		}
		return null;
	}

	/**
	 * 
	 * @return 读取upload-config.xml文件 获取保存根路径
	 */
	public String getUploadPath(String xmlPath) {
		String path = null;
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources
					.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element element = (Element) it.next();
				Element nameElement = element.element("name");
				String s = nameElement.getText();
				if (xmlPath.equals(s)) {
					Element pathElement = element.element("path");
					path = pathElement.getText();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return path;
	}
	
	/**
	 * 
	 * @return 下载
	 */
	public void download(Context context) {
		String savaPath = (String) context.contextMap.get("path");
		String name = (String) context.contextMap.get("name");
		String bootPath = this.getUploadPath("lockcodepassword");
		System.out.println(bootPath);
		System.out.println(savaPath);
		String path = bootPath + savaPath;
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			context.response.setHeader("Content-Disposition",
					"attachment; filename="
							+ new String(name.getBytes("gb2312"), "iso8859-1"));

			output = context.response.getOutputStream();
			fis = new FileInputStream(file);

			byte[] b = new byte[1024];
			int i = 0;

			while ((i = fis.read(b)) != -1) {

				output.write(b, 0, i);
			}
			output.write(b, 0, b.length);

			output.flush();
			context.response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				output = null;
			}
		}

	}
	
	public void sendLockCodeByManual(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> result = (Map<String, Object>) baseService.queryForObj("lockManagement.getLockInfoByOne", context.contextMap);
		try {
			if ("3".equals(String.valueOf(result.get("LOCK_CODE")))) {
				lockCodeService.doSendLockCodeByManualForDirect(result);
				outputMap.put("msg", "直接锁码-发送成功");
			} else if ("1".equals(String.valueOf(result.get("LOCK_CODE")))) {
				lockCodeService.doSendLockCodeByManualForIndirect(result);
				outputMap.put("msg", "间接锁码-发送成功");
			} else {
				outputMap.put("msg", "无锁码-未发送");
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			logger.error(e);
			outputMap.put("msg", e.getMessage());
		}
		Output.jsonOutput(outputMap, context);
	}

}
