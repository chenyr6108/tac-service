package com.brick.businessSupport.command;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.BaseTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.businessSupport.service.BusinessSupportService;
import com.brick.businessSupport.to.SqlTO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.visitation.service.VisitationService;
import com.brick.visitation.to.VisitationReportTo;
import com.ibatis.sqlmap.client.SqlMapClient;

public class BusinessSupportCommand extends BaseCommand{
	
	Log logger = LogFactory.getLog(this.getClass());
	
	private BusinessSupportService businessSupportService;
	private MailUtilService mailUtilService;
	
	private VisitationService visitationService;
	
	public VisitationService getVisitationService() {
		return visitationService;
	}

	public void setVisitationService(VisitationService visitationService) {
		this.visitationService = visitationService;
	}
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	public BusinessSupportService getBusinessSupportService() {
		return businessSupportService;
	}
	public void setBusinessSupportService(BusinessSupportService businessSupportService) {
		this.businessSupportService = businessSupportService;
	}
	public void extensionProject(Context context){
		Map<String, Object> output = new HashMap<String, Object>();
		Output.jspOutput(output, context, "/businessSupport/extensionProjectValidDate.jsp");
	}
	
	public void updateExtensionProjectValidDate(Context context) throws Exception{
		String lease_code = StringUtils.isEmpty((String) context.contextMap.get("lease_code")) 
				? null : ((String)context.contextMap.get("lease_code")).trim();
		String valid_date = StringUtils.isEmpty((String) context.contextMap.get("valid_date")) 
				? null : ((String)context.contextMap.get("valid_date")).trim();
		String valid_day = StringUtils.isEmpty((String) context.contextMap.get("valid_day")) 
				? null : ((String)context.contextMap.get("valid_day")).trim();
		Object s_employeeId = context.contextMap.get("s_employeeId");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> output = new HashMap<String, Object>();
		Integer resultNum = 0;
		String result = null;
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			/* Modify by ZhangYizhou on 2014-06-19 Begin */
	    	/* IT201406071 : 延长案件有效期画面完善 */
			if (lease_code != null && (valid_day != null || valid_date!=null)) {
				paramMap.put("lease_code", lease_code);
				paramMap.put("valid_date", valid_date);
				paramMap.put("valid_day", valid_day);
			} else {
				throw new Exception("没有合同号或者有效期天数/有效日期怎么做啊？");
			}
			/*
			if (lease_code != null && valid_day != null) {
				paramMap.put("lease_code", lease_code);
				paramMap.put("valid_day", valid_day);
			} else {
				throw new Exception("没有合同号或者有效期天数怎么做啊？");
			}
			*/
			/* Modify by ZhangYizhou on 2014-06-19 End */
			resultNum = this.businessSupportService.updateExtensionProjectValidDate(paramMap);
			if (resultNum != null && resultNum == 1) {
				result = "操作成功。";
				sqlMap.commitTransaction();
			} else {
				throw new Exception("修改有效期失败！");
			}
		} catch (Exception e) {
			result = "操作失败，请核对合同号。";
			sqlMap.endTransaction();
			//throw e;
		} finally {
			try {
				Integer credit_id = (Integer) DataAccessor.query("businessSupport.getCreditIdByLeaseCode", paramMap, RS_TYPE.OBJECT);
				/* Modify by ZhangYizhou on 2014-06-19 Begin */
		    	/* IT201406071 : 延长案件有效期画面完善 */
				Integer rs_valid_day = (Integer) DataAccessor.query("businessSupport.getValidDayByLeaseCode", paramMap, RS_TYPE.OBJECT);
				this.addSysLog(credit_id.longValue(), 0L, "业务支撑", "延长案件有效期", lease_code, "案件有效期延长至" + rs_valid_day + "天", DataUtil.longUtil(s_employeeId),(String)context.contextMap.get("IP"));
				output.put("lease_code", lease_code);
				output.put("valid_date", valid_date);
				output.put("valid_day", valid_day);
				output.put("result", result);
				/*
				this.addSysLog(credit_id.longValue(), 0L, "业务支撑", "延长案件有效期", lease_code, "案件有效期延长至" + valid_day + "天", DataUtil.longUtil(s_employeeId),(String)context.contextMap.get("IP"));
				output.put("lease_code", lease_code);
				output.put("valid_day", valid_day);
				output.put("result", result);
				*/
				/* Modify by ZhangYizhou on 2014-06-19 End */
				Output.jspOutput(output, context, "/businessSupport/extensionProjectValidDate.jsp");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getAllCredit(Context context){
		PagingInfo<Object> pagingInfo = null;
		try {
			pagingInfo = baseService.queryForListWithPaging("businessSupport.getAllCreditForChangeVip", context.contextMap, "CREDIT_RUNCODE");
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("pagingInfo", pagingInfo);
		output.put("vip_flag", context.contextMap.get("vip_flag"));
		output.put("search_content", context.contextMap.get("search_content"));
		Output.jspOutput(output, context, "/businessSupport/changeVip.jsp");
	}
	
	public void doChangeVip(Context context){
		String to_vip_flag = (String) context.contextMap.get("to_vip_flag");
		String prcd_id = (String) context.contextMap.get("prcd_id");
		boolean flag = false;
		try {
			baseService.update("businessSupport.doChangeVip", context.contextMap);
			String memo = "1".equals(to_vip_flag) ? "普通案件转为绿色通道" : "绿色通道转为普通案件";
			BusinessLog.addBusinessLogWithIp(Long.parseLong(prcd_id), 0L, 
					"报告", "绿色通道转换", "", memo, 1, 
					Long.parseLong(context.contextMap.get("s_employeeId").toString()), 
					null, (String)context.contextMap.get("IP"));
			flag = true;
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void getIsExpiredByCreditId(Context context){
		boolean flag = false;
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			String credit_id = (String) context.contextMap.get("credit_id");
			flag = baseService.getIsExpiredByCreditId(credit_id);
		} catch (ServiceException e) {
			logger.warn(e);
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	//---------------------------------------------------------------------------------------ShenQi加的业务支持
	//业务支撑1,保证金B手动冲回
	public void decomposePledgeBQuery(Context context) {
		
		//查询已经手动冲回出来的 案件
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo=null;
		
		try {
			pagingInfo=baseService.queryForListWithPaging("businessSupport.getHistoryPledgeBChargeBack",context.contextMap,"CREATE_TIME");
		} catch (ServiceException e) {
			logger.debug("查询已经手动冲回出来的案件出错");
		}
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("pagingInfo",pagingInfo);
		
		Output.jspOutput(outputMap,context,"/businessSupport/pledgeB/decomposePledgeB.jsp");
	}
	
	//保证金B手动冲回
	public void decomposePledgeB(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapper=DataAccessor.getSession();
		
		try {
			
			Map<String,Object> baseDataMap=(Map<String,Object>)DataAccessor.query("businessSupport.getPledgeBChargeBackBaseData",context.contextMap,RS_TYPE.MAP);
		
			if(baseDataMap==null||baseDataMap.get("RECP_ID")==null||"".equals(baseDataMap.get("RECP_ID"))) {
				outputMap.put("msg","未找到此支付表号可冲回的记录!");
				Output.jsonOutput(outputMap,context);
				return;
			} else {
				Map<String,Object> tempMap=(Map<String,Object>)DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBLastPeriod",baseDataMap,DataAccessor.RS_TYPE.MAP);
				tempMap.put("RECP_ID",baseDataMap.get("RECP_ID"));
				
				List<Map<String,Object>> rentList=(List<Map<String,Object>>)DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBDetail",tempMap,DataAccessor.RS_TYPE.LIST);
				
				if(rentList==null||rentList.size()==0) {
					outputMap.put("msg","未找到此支付表!");
					Output.jsonOutput(outputMap,context);
					return;
				}
				Map<String,Object> tempBillMap=null;
				Map<String,Object> tempFinanceIncome=null;
				Map<String,Object> logMap=new HashMap<String,Object>();
				sqlMapper.startTransaction();
				for(int i=0;i<rentList.size();i++) {
					
					//插入一条冲回分解单
					tempBillMap=(Map<String,Object>)DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBCollectoinBill",baseDataMap,DataAccessor.RS_TYPE.MAP);
					tempBillMap.put("REAL_PRICE",0-(DataUtil.doubleUtil(((Map<String,Object>)rentList.get(i)).get("IRR_MONTH_PRICE"))));
					tempBillMap.put("FICB_TYPE","1");
					tempBillMap.put("IS_PLEDGE_B","1");
					sqlMapper.insert("financeDecomposeReport.insertCollectionBillByPledgeB",tempBillMap);
					
					//产生待分解来款
					tempBillMap.put("REAL_PRICE",(DataUtil.doubleUtil(((Map)rentList.get(i)).get("IRR_MONTH_PRICE"))));
					tempBillMap.put("FICB_ITEM","待分解来款");
					sqlMapper.insert("financeDecomposeReport.insertCollectionBillByPledgeB",tempBillMap);
					
					//将产生的待分解来款插入到来款表中
					tempFinanceIncome=(Map<String,Object>)DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBFinanceIncome",baseDataMap,DataAccessor.RS_TYPE.MAP);
					tempFinanceIncome.put("INCOME_MONEY",(DataUtil.doubleUtil(((Map)rentList.get(i)).get("IRR_MONTH_PRICE"))));
					tempFinanceIncome.put("PAYMENT_MONEY",0);
					tempFinanceIncome.put("LEFT_MONEY",0);
					tempFinanceIncome.put("COMMISSION_MONEY",0);
					Long fiin_id=(Long)sqlMapper.insert("financeDecomposeReport.insertFinanceIncome",tempFinanceIncome);
					
					logMap.put("fiinId",fiin_id);
					sqlMapper.update("businessSupport.updatePledgeBToPaddingMoney",logMap);
					
				}
				
				logMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
				logMap.put("recpId",baseDataMap.get("RECP_ID"));
				logMap.put("recpCode",baseDataMap.get("RECP_CODE"));
				logMap.put("price",baseDataMap.get("REAL_PRICE"));
				
				sqlMapper.insert("businessSupport.insertHistoryPledgeBChargeBack",logMap);
				
				sqlMapper.commitTransaction();
				
				outputMap.put("msg","冲回成功!");
				Output.jsonOutput(outputMap,context);
			}
		} catch (Exception e) {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e1) {
				outputMap.put("msg","出现异常!");
				Output.jsonOutput(outputMap,context);
			}
		}
		
	}
	
	public void getDispatchCaseList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo=null;
		List<Map<String,Object>> resultList=null;
		
		try {
			pagingInfo=baseService.queryForListWithPaging("businessSupport.getDispatchCaseList",context.contextMap,"DISPATCH_NAME");
			
			resultList=(List<Map<String,Object>>)DataAccessor.query("businessSupport.getDispatchLogList",null,RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug("查询已经手动冲回出来的案件出错");
		}
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("resultList",resultList);
		
		Output.jspOutput(outputMap,context,"/businessSupport/dispatchCase/dispatchCaseList.jsp");
	}
	
	public void getDispatchIdList(Context context) {
		
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("businessSupport.getDispatchIdList",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug(e);
		}
		
		Output.jsonArrayOutputForList(resultList,context);
	}
	
	public void updateDispatchId(Context context) {
		
		//不需要事务,重点是人员转移与收件时间更新
		Map<String,Object> userInfo=new HashMap<String,Object>();
		Map<String,Object> log=new HashMap<String,Object>();
		try {
			log=(Map<String,Object>)DataAccessor.query("businessSupport.getDispatchCaseList",context.contextMap,RS_TYPE.MAP);
			
			DataAccessor.execute("businessSupport.updateDispatchId",context.contextMap,OPERATION_TYPE.UPDATE);
			
			userInfo=(Map<String,Object>)DataAccessor.query("businessSupport.getDispatchUserInfo",context.contextMap,RS_TYPE.MAP);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("业管收件转移");
			mailSettingTo.setEmailTo(userInfo.get("EMAIL")+";"+context.contextMap.get("email"));
			mailSettingTo.setEmailContent("<font style='font-family: 微软雅黑'>Hi "+userInfo.get("NAME")+"<br>"+"&nbsp;&nbsp;"+
							context.contextMap.get("dispatchName")+"的文审案件(案件号:"+context.contextMap.get("creditRuncode")+")已由资讯部操作转移给你,请注意!</font>");
			mailSettingTo.setEmailCc("IT@tacleasing.cn;robin_chantw@tacleasing.cn");
			mailSettingTo.setCreateBy(context.contextMap.get("s_employeeId").toString());
			this.mailUtilService.sendMail(mailSettingTo);
			
			log.put("MEMO","由"+context.contextMap.get("dispatchName")+"转移给"+userInfo.get("NAME")+",移转前的收件时间为"+log.get("HW_TIME"));
			log.put("s_employeeId",context.contextMap.get("s_employeeId"));
			
			DataAccessor.execute("businessSupport.insertDispatchLog",log,OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			logger.debug(e);
		}
		
		context.contextMap.remove("id");
		this.getDispatchCaseList(context);
	}
	
	public void showDatabaseUpdate(Context context){
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo = null;
		pagingInfo = baseService.queryForListWithPaging("businessSupport.getAllSql", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("msg", context.contextMap.get("msg"));
		outputMap.put("search_status", context.contextMap.get("search_status"));
		outputMap.put("create_by", context.contextMap.get("create_by"));
		outputMap.put("search_context", context.contextMap.get("search_context"));
		SqlTO sql = (SqlTO) context.contextMap.get("sql");
		outputMap.put("resultList", sql == null ? null : sql.getResultList());
		Output.jspOutput(outputMap, context, "/businessSupport/databaseUpdate/databaseUpdate.jsp");
	}
	
	public void createSql(Context context) throws IOException{
		String sqlStr = (String) context.contextMap.get("sqlStr");
		String item_code = (String) context.contextMap.get("item_code");
		String sqlId = (String) context.contextMap.get("sql_id");
		try {
			if (StringUtils.isEmpty(sqlStr) || sqlStr.length() < 6) {
				throw new Exception("无效的SQL语句。");
			}
			sqlStr = sqlStr.trim();
			String temp = sqlStr.substring(0, 6);
			SqlTO sql = new SqlTO();
			sql.setSql(sqlStr);
			sql.setItem_code(item_code);
			sql.setCreate_by(String.valueOf(context.contextMap.get("s_employeeId")));
			if (temp.toUpperCase().indexOf("UPDATE") == 0) {
				sql.setSql_type("UPDATE");
			} else if (temp.toUpperCase().indexOf("INSERT") == 0) {
				sql.setSql_type("INSERT");
			} else if (temp.toUpperCase().indexOf("SELECT") == 0) {
				sql.setSql_type("SELECT");
			}
			if (StringUtils.isEmpty(sqlId)) {
				businessSupportService.createSql(sql);
			} else {
				sql.setId(sqlId);
				sql.setStatus(0);
				businessSupportService.updateSql(sql);
			}
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			context.contextMap.put("msg", str);
		}
		showDatabaseUpdate(context);
	}
	
	public void createAndTestSql(Context context) throws IOException{
		String sqlStr = (String) context.contextMap.get("sqlStr");
		String item_code = (String) context.contextMap.get("item_code");
		try {
			sqlStr = sqlStr.trim();
			String temp = sqlStr.substring(0, 10);
			SqlTO sql = new SqlTO();
			sql.setSql(sqlStr);
			sql.setItem_code(item_code);
			sql.setCreate_by(String.valueOf(context.contextMap.get("s_employeeId")));
			if (temp.toUpperCase().indexOf("UPDATE") == 0) {
				sql.setSql_type("UPDATE");
			} else if (temp.toUpperCase().indexOf("INSERT") == 0) {
				sql.setSql_type("INSERT");
			} else if (temp.toUpperCase().indexOf("SELECT") == 0) {
				sql.setSql_type("SELECT");
			}
			String sqlId = businessSupportService.createSql(sql);
			businessSupportService.doTest(sqlId);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			context.contextMap.put("msg", str);
		}
		showDatabaseUpdate(context);
	}
	
	public void createAndExecuteSql(Context context) throws IOException{
		String sqlStr = (String) context.contextMap.get("sqlStr");
		String item_code = (String) context.contextMap.get("item_code");
		try {
			sqlStr = sqlStr.trim();
			String temp = sqlStr.substring(0, 10);
			SqlTO sql = new SqlTO();
			sql.setSql(sqlStr);
			sql.setItem_code(item_code);
			sql.setCreate_by(String.valueOf(context.contextMap.get("s_employeeId")));
			if (temp.toUpperCase().indexOf("UPDATE") == 0) {
				sql.setSql_type("UPDATE");
			} else if (temp.toUpperCase().indexOf("INSERT") == 0) {
				sql.setSql_type("INSERT");
			} else if (temp.toUpperCase().indexOf("SELECT") == 0) {
				sql.setSql_type("SELECT");
			}
			String sqlId = businessSupportService.createSql(sql);
			businessSupportService.doExecute(sqlId);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			context.contextMap.put("msg", str);
		}
		showDatabaseUpdate(context);
	}
	
	public void doTest(Context context) throws IOException{
		String id = (String) context.contextMap.get("id");
		try {
			businessSupportService.doTest(id);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			context.contextMap.put("msg", str);
		}
		showDatabaseUpdate(context);
	}
	
	public void doExecute(Context context) throws IOException{
		String id = (String) context.contextMap.get("id");
		SqlTO sql = null;
		try {
			sql = businessSupportService.doExecute(id);
			context.contextMap.put("sql", sql);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
            String str = sw.toString();
            str = str.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            		.replaceAll("\r\n", "<br/>");
            sw.flush();
            sw.close();
            sw = null;
			context.contextMap.put("msg", str);
		}
		showDatabaseUpdate(context);
	}
	
	
	//业务支撑 更新小车拨款日与首期支付日
	public void queryMotorCarRecp(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo=null;
		List<Map<String,Object>> deptList=null;
		List<String> roleIds=null;
		try {
			roleIds=(List<String>)DataAccessor.query("businessSupport.getRoleId",context.contextMap,RS_TYPE.LIST);
			deptList=(List<Map<String,Object>>)DataAccessor.query("common.getMotorCarDeptList",null,RS_TYPE.LIST);
			pagingInfo=baseService.queryForListWithPaging("businessSupport.queryMotorCarRecp",context.contextMap,"FINANCECONTRACT_DATE",ORDER_TYPE.DESC);
		} catch (Exception e) {
			logger.debug(e);
		}
		
		for(int i=0;i<roleIds.size();i++) {
			if("70".equals(roleIds.get(i))) {
				outputMap.put("flag",true);
				break;
			}
		}
		
		outputMap.put("deptId",context.contextMap.get("deptId"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("deptList",deptList);
		outputMap.put("pagingInfo",pagingInfo);
		
		Output.jspOutput(outputMap,context,"/businessSupport/motorCar/queryMotorCarRecp.jsp");
	}
	
	public void queryMotorCarLog(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("businessSupport.queryMotorCarLog",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug(e);
		}
		
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/businessSupport/motorCar/queryMotorCarLog.jsp");
	}
	
	public void updateMotorCarDate(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=null;
		
		try {
			int hasUpdated;
			if(DataAccessor.query("businessSupport.checkHasUpdate",context.contextMap,RS_TYPE.OBJECT)==null) {
				hasUpdated=0;
			} else {
				hasUpdated=Integer.valueOf(DataAccessor.query("businessSupport.checkHasUpdate",context.contextMap,RS_TYPE.OBJECT).toString());
			}
			
			if(hasUpdated>0) {
				//后台验证已经修改过,方式打开多页面或者并发
				outputMap.put("msg","此数据已经更新过,系统将为你刷新页面!");
				Output.jsonOutput(outputMap,context);
				return;
			}
			sqlMapClient=DataAccessor.getSession();
			List<Map<String,Object>> resultList=(List<Map<String,Object>>)sqlMapClient.queryForList("businessSupport.getPayDetail",context.contextMap);
			
			Map<String,Object> recpMap=(Map<String,Object>)DataAccessor.query("businessSupport.queryMotorCarRecp",context.contextMap,RS_TYPE.MAP);
			recpMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
			
			Calendar c=Calendar.getInstance();
			c.setTime(DateUtil.strToDate(context.contextMap.get("newDate").toString(),"yyyy-MM-dd"));
			c.add(Calendar.MONTH,1);
			String firstPayDate=DateUtil.dateToString(c.getTime(),"yyyy-MM-dd");
			recpMap.put("MEMO","支付表号"+recpMap.get("RECP_CODE")+"的案件," +
					"<br>首期支付日:"+recpMap.get("FIRST_PAY_DATE").toString().split(" ")[0]+
					"->"+firstPayDate
					+"<br>银行拨款日:"+context.contextMap.get("newDate")
					/*+"<br>拨款日:"+recpMap.get("FINANCECONTRACT_DATE").toString().split(" ")[0]+
					"->"+context.contextMap.get("payDate").toString()*/
					+"<br>修改人:"+context.contextMap.get("s_employeeName"));
			sqlMapClient.startTransaction();
			
			
			sqlMapClient.update("businessSupport.updatePayDate0",context.contextMap);//更新新加字段银行拨款日
			sqlMapClient.update("businessSupport.updatePayDate1",context.contextMap);//更新报告表
			sqlMapClient.update("businessSupport.updatePayDate2",context.contextMap);//更新金蝶传票表
			context.contextMap.put("firstPayDate",firstPayDate);
			sqlMapClient.update("businessSupport.updatePayDate3",context.contextMap);//更新支付表表头表
			recpMap.put("FINANCECONTRACT_DATE", context.contextMap.get("newDate"));
			sqlMapClient.insert("businessSupport.insertMotorCarLog",recpMap);//插入记录日志
			for(int i=0;resultList!=null&&i<resultList.size();i++) {
				Calendar cal=Calendar.getInstance();
				/*cal.setTime(DateUtil.strToDate(context.contextMap.get("firstPayDate").toString(),"yyyy-MM-dd"));*/
				cal.setTime(DateUtil.strToDate(firstPayDate,"yyyy-MM-dd"));
				cal.add(Calendar.MONTH,i);
				resultList.get(i).put("newDate",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
				sqlMapClient.update("businessSupport.updatePayDate4",resultList.get(i));//更新支付表明细表的每期支付日
			}
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			System.out.println(e);
			logger.debug(e);
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
				logger.debug(e1);
			}
		}
		
		outputMap.put("msg","更新成功!");
		Output.jsonOutput(outputMap,context);
	}
	
	//启用已经更新过的乘用车业管操作功能,因为业管只能更新1次后操作功能就会锁掉,此功能用于开启业管操作功能
	public void enableMotorCarDate(Context context) {
		
		try {
			DataAccessor.execute("businessSupport.enableMotorCarDate",context.contextMap,OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			logger.debug(e);
		}
		Output.jsonFlageOutput(true,context);
	}
	
	/**
	 * 修改供应商保证
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateSuplPledge(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getInfoForUpdateSuplPledge", context.contextMap);
			if (item != null) {
				List<SelectionTo> suplPledges = baseService.getDataDictionaryForSelect("供应商保证");
				outputMap.put("item", item);
				outputMap.put("suplPledges", suplPledges);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		Output.jspOutput(outputMap,context,"/businessSupport/updateSuplPledge.jsp");
	}
	
	public void doUpdateSuplPledge(Context context) throws Exception{
		businessSupportService.doUpdateSuplPledge(context);
		showUpdateSuplPledge(context);
	}
	
	/**
	 * 修改设备
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateEqmt(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.queryForList("businessSupport.showUpdateEqmt", context.contextMap);
			if (list != null) {
				outputMap.put("list", list);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdateEqmt.jsp");
	}
	
	public void doUpdateEqmt(Context context) throws Exception{
		businessSupportService.doUpdateEqmt(context);
		showUpdateEqmt(context);
	}
	
	/**
	 * 修改开票资料
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateInvoiceInfo(Context context) throws Exception{//TODO
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getInvoiceInfoForCustInfo", context.contextMap);
			List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.queryForList("businessSupport.getInvoiceInfoForBankInfo", context.contextMap);
			if (item != null) {
				outputMap.put("item", item);
				outputMap.put("list", list);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		outputMap.put("msg", context.contextMap.get("msg"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdateInvoiceInfo.jsp");
	}
	
	public void doUpdateInvoiceInfo(Context context) throws Exception{
		try {
			businessSupportService.doUpdateInvoiceInfo(context);
			context.contextMap.put("msg", "修改成功！");
			
		} catch (Exception e) {
			logger.error(e);
			context.contextMap.put("msg", "修改失败！");
		}
		showUpdateInvoiceInfo(context);
	}
	
	/**
	 * 修改各种意见
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateMemo(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getCreditBaseInfo", context.contextMap);
			if (item != null) {
				context.contextMap.put("credit_id", item.get("ID"));
				Map<String, Object> pSIdeaMap= (Map<String, Object>) baseService.queryForObj("riskAudit.selectPSIdea",context.contextMap);
				Map<String, Object> pSIdeaMapOther = (Map<String, Object>) baseService.queryForObj("riskAudit.selectPSIdeaOther",context.contextMap);
				List<Map<String, Object>> memoList = (List<Map<String, Object>>) baseService.queryForList("creditReportManage.selectNewMemoFor2",context.contextMap);
				Map<String, Object> manageMap = null;
				Map<String, Object> manageMapDGM = null;
				if (memoList != null && memoList.size() == 1) {
					manageMap = memoList.get(0);
				} else if (memoList != null && memoList.size() == 2) {
					if (memoList.get(0).get("AUDIT_STATE") != null && (Integer)memoList.get(0).get("AUDIT_STATE") == 1) {
						manageMapDGM = memoList.get(0);
						manageMap = memoList.get(1);
					} else {
						manageMap = memoList.get(0);
					}
				}
				List<Map<String, Object>> riskMemo = (List<Map<String, Object>>) baseService.queryForList("businessSupport.getRiskMemoForUpdate", context.contextMap);
				outputMap.put("item", item);
				outputMap.put("pSIdeaMap", pSIdeaMap);
				outputMap.put("pSIdeaMapOther", pSIdeaMapOther);
				outputMap.put("manageMap", manageMap);
				outputMap.put("manageMapDGM", manageMapDGM);
				outputMap.put("riskMemo", riskMemo);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdateMemo.jsp");
	}
	
	public void doUpdateMemo(Context context) throws Exception{
		businessSupportService.doUpdateMemo(context);
		showUpdateMemo(context);
	}
	
	public void getDataDictionaryForSelect(Context context){
		String type = (String) context.contextMap.get("type");
		List<SelectionTo> data = baseService.getDataDictionaryForSelect(type);
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllCust(Context context){
		List<SelectionTo> data = baseService.getAllCust();
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllSupl(Context context){
		List<SelectionTo> data = baseService.getAllSupl();
		Output.jsonArrayOutputForObject(data, context);
	}
	
	public void getAllGuarantor(Context context){
		List<SelectionTo> data = (List<SelectionTo>) baseService.getAllGuarantor();
		Output.jsonArrayOutputForObject(data, context);
	}
	
	/**
	 * 修改客户名称
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateCustName(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getInfoForUpdateSuplPledge", context.contextMap);
			if (item != null) {
				outputMap.put("item", item);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdateCustName.jsp");
	}
	
	public void doUpdateCustName(Context context) throws Exception{
		String cust_name_updated = (String) context.contextMap.get("cust_name_updated");
		String cust_name_related = (String) context.contextMap.get("cust_name_related");
		String credit_id = (String) context.contextMap.get("ID");
		if (!StringUtils.isEmpty(cust_name_updated)) {
			businessSupportService.updateCustName(cust_name_updated, credit_id, context.getRequest().getSession());
		} else if (!StringUtils.isEmpty(cust_name_related)) {
			businessSupportService.updateCustNameForRelated(cust_name_related, credit_id, context.getRequest().getSession());
		}
		showUpdateCustName(context);
	}
	
	/**
	 * 修改访厂报告
	 * @param context
	 */
	public void showUpdateVisitReport(Context context){
		logger.info("===================修改访厂报告=====================");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> pagingInfo = null;
		try {
			String apply_date_from = (String) context.contextMap.get("apply_date_from");
			String apply_date_to = (String) context.contextMap.get("apply_date_to");
			String plan_date_from = (String) context.contextMap.get("plan_date_from");
			String plan_date_to = (String) context.contextMap.get("plan_date_to");
			String plan_visitor = (String) context.contextMap.get("plan_visitor");
			String real_date_from = (String) context.contextMap.get("real_date_from");
			String real_date_to = (String) context.contextMap.get("real_date_to");
			String real_visitor = (String) context.contextMap.get("real_visitor");
			String project_user = (String) context.contextMap.get("project_user");
			String visit_area = (String) context.contextMap.get("visit_area");
			String search_content = (String) context.contextMap.get("search_content");
			String search_status = (String) context.contextMap.get("search_status");
			String visit_result = (String) context.contextMap.get("visit_result");
			if (!StringUtils.isEmpty(apply_date_from)) {
				apply_date_from = apply_date_from.trim();
			}
			if (!StringUtils.isEmpty(apply_date_to)) {
				apply_date_to = apply_date_to.trim();
			}
			if (!StringUtils.isEmpty(plan_date_from)) {
				plan_date_from = plan_date_from.trim();
			}
			if (!StringUtils.isEmpty(plan_date_to)) {
				plan_date_to = plan_date_to.trim();
			}
			if (!StringUtils.isEmpty(plan_visitor)) {
				plan_visitor = plan_visitor.trim();
			}
			if (!StringUtils.isEmpty(real_date_from)) {
				real_date_from = real_date_from.trim();
			}
			if (!StringUtils.isEmpty(real_date_to)) {
				real_date_to = real_date_to.trim();
			}
			if (!StringUtils.isEmpty(real_visitor)) {
				real_visitor = real_visitor.trim();
			}
			if (!StringUtils.isEmpty(project_user)) {
				project_user = project_user.trim();
			}
			if (!StringUtils.isEmpty(visit_area)) {
				visit_area = visit_area.trim();
			}
			if (!StringUtils.isEmpty(search_content)) {
				search_content = search_content.trim();
			}
			if (!StringUtils.isEmpty(search_status)) {
				search_status = search_status.trim();
			}
			context.contextMap.put("apply_date_from", apply_date_from);
			outputMap.put("apply_date_from", apply_date_from);
			context.contextMap.put("apply_date_to", apply_date_to);
			outputMap.put("apply_date_to", apply_date_to);
			context.contextMap.put("plan_date_from", plan_date_from);
			outputMap.put("plan_date_from", plan_date_from);
			context.contextMap.put("plan_date_to", plan_date_to);
			outputMap.put("plan_date_to", plan_date_to);
			context.contextMap.put("plan_visitor", plan_visitor);
			outputMap.put("plan_visitor", plan_visitor);
			
			context.contextMap.put("real_date_from", real_date_from);
			outputMap.put("real_date_from", real_date_from);
			context.contextMap.put("real_date_to", real_date_to);
			outputMap.put("real_date_to", real_date_to);
			context.contextMap.put("real_visitor", real_visitor);
			outputMap.put("real_visitor", real_visitor);
			
			context.contextMap.put("project_user", project_user);
			outputMap.put("project_user", project_user);
			context.contextMap.put("visit_area", visit_area);
			outputMap.put("visit_area", visit_area);
			context.contextMap.put("search_content", search_content);
			outputMap.put("search_content", search_content);
			outputMap.put("search_status", search_status);
			outputMap.put("visit_result", visit_result);
			
			List<SelectionTo> decpList = baseService.getAllOffice();
			outputMap.put("decpList", decpList);
			
			//查询当前的登录的员工的部门ID
			String search_decp = (String) context.contextMap.get("search_decp");
			if (search_decp == null) {
				search_decp = "-1";
			} else {
				search_decp = search_decp.trim();
			}
			context.contextMap.put("search_decp", search_decp);
			outputMap.put("search_decp", search_decp);
			
			pagingInfo = baseService.queryForListWithPaging("visitation.getAllApplied", context.contextMap, "AUTH_DATE");
			outputMap.put("pagingInfo", pagingInfo);
		} catch (Exception e) {
			e.printStackTrace();
			context.contextMap.put("errorMsg", e.getMessage());
		}
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		Output.jspOutput(outputMap, context, "/businessSupport/showUpdateVisitReport.jsp");
	}
	
	/**
	 * 加载和初始化访厂报告
	 * @param context
	 */
	public void inputReport(Context context){
		logger.info("=============================填报访厂报告================================");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String visit_id = (String) context.contextMap.get("visit_id");
		try {
			if (StringUtils.isEmpty(visit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			VisitationReportTo reportTo = new VisitationReportTo();
			reportTo.setVisit_id(visit_id);
			reportTo = (VisitationReportTo) baseService.queryForObj("visitation.getReport", reportTo);
			if (reportTo == null || reportTo.getVisit_id() == null) {
				throw new Exception("数据过期，请刷新页面。");
			}
			outputMap.put("reportTo", reportTo);
			outputMap.put("show_type", context.contextMap.get("show_type"));
			Output.jspOutput(outputMap, context, "/businessSupport/visitForm.jsp");
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
			showUpdateVisitReport(context);
		}
	}
	
	/**
	 * 保存访厂报告
	 * @param context
	 * @throws Exception 
	 */
	public void doInputReport(Context context) throws Exception{
		logger.info("=============================保存访厂报告================================");
		try {
			visitationService.doInputReport(context);
			showUpdateVisitReport(context);
		} catch (Exception e) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("reportTo", (VisitationReportTo) context.getFormBean("reportTo"));
			outputMap.put("errorMsg", e.getMessage());
			Output.jspOutput(outputMap, context, "/businessSupport/visitForm.jsp");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 修改供应商
	 * @param context
	 * @throws Exception
	 */
	public void showUpdateSuplName(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getInfoForUpdateSuplPledge", context.contextMap);
			if (item != null) {
				outputMap.put("item", item);
			}
		}
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdateSuplName.jsp");
	}
	
	public void doUpdateSuplName(Context context) throws Exception{
		String supl_name_updated = (String) context.contextMap.get("supl_name_updated");
		String supl_name_related = (String) context.contextMap.get("supl_name_related");
		String credit_id = (String) context.contextMap.get("ID");
		if (!StringUtils.isEmpty(supl_name_updated)) {
			businessSupportService.updateSuplName(supl_name_updated, credit_id, context.getRequest().getSession());
		} else if (!StringUtils.isEmpty(supl_name_related)) {
			businessSupportService.updateSuplNameForRelated(supl_name_related, credit_id, context.getRequest().getSession());
		}
		showUpdateSuplName(context);
	}
	
	/**
	 * 修改交机情形页面
	 * @param context
	 * @throws Exception
	 */
	public void showUpdatePayWay(Context context) throws Exception{
		Map<String,Object> outputMap = new HashMap<String,Object>();
		if (!StringUtils.isEmpty(context.contextMap.get("credit_runcode")) || !StringUtils.isEmpty(context.contextMap.get("lease_code"))) {
			Map<String, Object> item = (Map<String, Object>) baseService.queryForObj("businessSupport.getCreditBaseInfo", context.contextMap);
			if (item != null) {
				outputMap.put("item", item);
				String credit_id = String.valueOf(item.get("ID"));
				outputMap.put("totalPayMoney", LeaseUtil.getPayMoneyByCreditId(credit_id));
				Map<String, Object> scMap = (Map<String, Object>) baseService.queryForObj("businessSupport.getScInfoForUpdatePayWay", item);
				outputMap.put("scMap", scMap);
				List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.queryForList("businessSupport.showUpdatePayWay", item);
				String type = null;
				for (Map<String, Object> map : list) {
					type = String.valueOf(map.get("TYPE"));
					if ("0".equals(type)) {
						outputMap.put("PAYPERCENT_0", map.get("PAYPERCENT"));
						outputMap.put("APPRORIATEMON_0", map.get("APPRORIATEMON"));
						outputMap.put("APPRORIATENAME_0", map.get("APPRORIATENAME"));
					}
					if ("1".equals(type)) {
						outputMap.put("PAYPERCENT_1", map.get("PAYPERCENT"));
						outputMap.put("APPRORIATEMON_1", map.get("APPRORIATEMON"));
						outputMap.put("APPRORIATENAME_1", map.get("APPRORIATENAME"));
					}
				}
			}
		}
		
		outputMap.put("credit_runcode", context.contextMap.get("credit_runcode"));
		outputMap.put("lease_code", context.contextMap.get("lease_code"));
		outputMap.put("msg", context.contextMap.get("msg"));
		Output.jspOutput(outputMap,context,"/businessSupport/showUpdatePayWay.jsp");
	}
	
	/**
	 * 修改动作
	 * @param context
	 * @throws Exception 
	 */
	public void doUpdatePayWay(Context context) throws Exception{
		businessSupportService.doUpdatePayWay(context);
		showUpdatePayWay(context);
	}
	
}
