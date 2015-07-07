package com.brick.litigation.command;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.litigation.service.LitigationService;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class LitigationCommand extends BaseCommand{
	Log logger = LogFactory.getLog(LitigationCommand.class);

	private BirtReportEngine birt;
	private MailUtilService mailUtilService;

	private LitigationService litigationService;
	
	public BirtReportEngine getBirt() {
		return birt;
	}

	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public LitigationService getLitigationService() {
		return litigationService;
	}
	public void setLitigationService(LitigationService litigationService) {
		this.litigationService = litigationService;
	}
	/**
	 * 增加诉讼进程
	 */
	public void addLitigation(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("LEASE_CODE", context.contextMap.get("lease_code"));
			map.put("PROCESS", context.contextMap.get("lProcess"));
			map.put("CREATE_USER", context.contextMap.get("s_employeeId"));
			map.put("LITIGATION_DATE", context.contextMap.get("litigationDate"));
			map.put("REMARK", context.contextMap.get("memo"));
			litigationService.addLitigation(map);
			
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}if (errList.isEmpty()) {
			outputMap.put("databack",true);
			Output.jsonOutput(outputMap, context);
		} else {
			context.contextMap.put("errList", errList);
			Output.jspOutput(context.contextMap, context, "/error.jsp");
		}
	}
	/**
	 * 查询诉讼案件
	 */
	@SuppressWarnings("unchecked")
	public void queryLitigationList(Context context) {
		List classList=null;
		List officeList=null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		context.contextMap.put("p_usernode", rsMap.get("NODE"));
		Map<String, Object> outputMap = new HashMap<String, Object>();
			try {
				PagingInfo<Object> dw = litigationService.queryLitigationList(context);
				outputMap.put("dw", dw);
				//诉讼状态
				context.contextMap.put("dataType", "诉讼进程");
				classList=this.baseService.queryForList("litigation.getPrClassList",context.contextMap);
				officeList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);
				outputMap.put("classList", classList);
				outputMap.put("officeList", officeList);
			} catch (ServiceException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			
			outputMap.put("DEPT_ID", context.contextMap.get("DEPT_ID"));
			outputMap.put("selectStatus", context.contextMap.get("selectStatus"));
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			Output.jspOutput(outputMap, context, "/litigation/litigationList.jsp");
	}
	/**
	 * 查看合同诉讼流程
	 */
	@SuppressWarnings("unchecked")
	public void queryLProcessList(Context context) {
		Map<String, Object> outputMap = new HashMap<String, Object>();
			try {
				List<Map> processList  = litigationService.queryLProcessList(context);
				outputMap.put("processList", processList);
			} catch (ServiceException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspOutput(outputMap, context, "/litigation/processList.jsp");
	}
	
	/**
	 * 起诉申请单审核页面
	 * @param context
	 * sue.STATE   
	 * 		1:已驳回
	 * 		2:单位主管审核中
	 * 		3:部门主管审核中
	 * 		4:总经理审核中
	 * 		5:已委寄
	 * 2014-1-7 yangliu
	 */
	public void querySueApprovalLetter(Context context){
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		context.contextMap.put("id", context.contextMap.get("s_employeeId"));
		context.contextMap.put("state", context.contextMap.get("state")==null?0:context.contextMap.get("state"));
		//权限
		String sueApplyUpUser = "N";	//单位主管
		String sueApplyUpUpUser = "N";	//部门主管
		String sueApplyManager = "N";	//总经理
		try {
			pagingInfo = baseService.queryForListWithPaging("sue.getAllSueList", context.contextMap, "SUEID", ORDER_TYPE.DESC);
			//权限
			List<String> resourceIdList = (List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(resourceIdList != null){
				for (int i = 0; i < resourceIdList.size(); i++) {
					if ("sueApplyUpUser".equals(resourceIdList.get(i)))  			sueApplyUpUser = "Y";
					else if ("sueApplyUpUpUser".equals(resourceIdList.get(i))) 		sueApplyUpUpUser = "Y";
					else if ("sueApplyManager".equals(resourceIdList.get(i)))  		sueApplyManager = "Y";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("sueApplyUpUser", sueApplyUpUser);
		outputMap.put("sueApplyUpUpUser", sueApplyUpUpUser);
		outputMap.put("sueApplyManager", sueApplyManager);
		outputMap.put("state", context.contextMap.get("state"));
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("startrange", context.contextMap.get("startrange"));
		outputMap.put("endrange", context.contextMap.get("endrange"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("NAME", context.contextMap.get("NAME"));
		Output.jspOutput(outputMap, context, "/dun/sueApprovalLetter.jsp");
	}
	
	/**
	 * 起诉申请单申请页面
	 * @param context
	 * sue.STATE   
	 * 		1:已驳回
	 * 		2:单位主管审核中
	 * 		3:部门主管审核中
	 * 		4:总经理审核中
	 * 		5:已委寄
	 * 2014-1-7 yangliu
	 */
	public void querySueApplyLetter(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		context.contextMap.put("id", context.contextMap.get("s_employeeId"));
		try {
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.DATE, -1);
//	        calendar.add(Calendar.YEAR, -1);
			context.contextMap.put("dun_date", format.format(calendar.getTime()));
//			context.contextMap.put("dun_date", context.contextMap.get("dun_date"));
			
			pagingInfo = baseService.queryForListWithPaging("sue.getSueList", context.contextMap, "dunDay", ORDER_TYPE.ASC);
			//生成按钮权限(非领导)
			String createAccess = "N";
			Map accMap = (Map<String,String>)DataAccessor.query("sue.getUserIsManager",context.contextMap,RS_TYPE.MAP);
			if(accMap != null){
				createAccess = "Y";
			}
			outputMap.put("createAccess", createAccess);
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
		Output.jspOutput(outputMap, context, "/dun/sueApplyLetter.jsp");
	}
	
	/**
	 * 查看起诉申请单信息
	 * @param context
	 * @throws Exception
	 * 2014-1-7 yangliu
	 */
	public void showSueApplyLetterInfo(Context context) throws Exception{
		Map outputMap = new HashMap();
		Map databack = new HashMap();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("rectId", context.contextMap.get("rectId"));
		outputMap.put("sue_rect_id", context.contextMap.get("rectId"));
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
//        calendar.add(Calendar.YEAR, -1);
		context.contextMap.put("dun_date", format.format(calendar.getTime()));
		map.put("dun_date", context.contextMap.get("dun_date"));
		map.put("sueId", context.contextMap.get("sueId"));
		//起诉申请单信息
		Map<String, Object> sueMap =new HashMap<String,Object>();
		//设备列表
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		//委外回访内容
		Map<String, Object> mapContent = new HashMap<String, Object>();
		//未缴总租金
		Map<String, Object> unPayPrice  = new HashMap<String, Object>();
		//违约金
	    Map<String, Object> mapDunFine  = new HashMap<String, Object>();
	    //法务费用
		Map<String, Object> settleMap = new HashMap<String, Object>();
		try {
			//起诉申请单信息
			sueMap = (Map<String,Object>)DataAccessor.query("sue.getSueById", map, RS_TYPE.MAP);
			//设备列表
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getEqupmentListByRectId",map,RS_TYPE.LIST);
			//委外回访内容
			if(sueMap == null){
				map.put("sueCreateDate", new Date());
			}else{
				map.put("sueCreateDate", sueMap.get("CREATE_DATE"));
			}
			mapContent = (Map<String,Object>)DataAccessor.query("sue.getLetterContentByRectId", map, RS_TYPE.MAP);
		    String decpId = mapContent.get("DECP_ID")==null?"":mapContent.get("DECP_ID").toString();
		    if(decpId.equals("3") || decpId.equals("8")){
		    	mapContent.put("court", "东莞市第二人民法院");
		    } else {
		    	mapContent.put("court", "苏州工业园区人民法院");
		    }
			//违约金
			DecimalFormat df = new DecimalFormat(".00");
			Map<String, Object>  dunAllFine=(Map<String, Object>)DataAccessor.query("dunTask.getAllFineByRectId", map, DataAccessor.RS_TYPE.MAP);
			Map<String, Object>  dunInCome =(Map<String, Object>)DataAccessor.query("dunTask.getinComeFineByRectId", map, DataAccessor.RS_TYPE.MAP);
			double allFine = Double.valueOf(String.valueOf(df.format(dunAllFine.get("FINE"))));
			double inComeFine = Double.valueOf(String.valueOf(df.format(dunInCome.get("DIFF_DUN"))));
			mapDunFine.put("fine", allFine-inComeFine);
			//回访日期&厂内营运状况
			Map<String, Object> backVisit = (Map<String, Object>)DataAccessor.query("backVisit.getVisitReviewRecordsByRectId", map, DataAccessor.RS_TYPE.MAP);
			outputMap.put("backVisit", backVisit);
		    //法务费用
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			context.contextMap.put("lawyfee", "法务费用") ;
			context.contextMap.put("RECP_ID", context.contextMap.get("recpId"));
			settleMap = (Map<String, Object>) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if(settleMap.get("TOTAL_LAWYFEE")==null){
				settleMap.put("TOTAL_LAWYFEE", 0);
			}
			double totalLawyfee = 0;
			double fine = 0;
			double unpayPrice = 0;
			if(settleMap != null && settleMap.get("TOTAL_LAWYFEE") != null){
				totalLawyfee = Double.parseDouble(settleMap.get("TOTAL_LAWYFEE").toString());
			}
			if(mapDunFine != null && mapDunFine.get("fine") != null){
				fine = Double.parseDouble(mapDunFine.get("fine").toString());
			}
			//未缴总租金
		    unPayPrice=(Map<String, Object>)DataAccessor.query("dunTask.getUnPayPriceByRectId", map, DataAccessor.RS_TYPE.MAP);
			if(unPayPrice != null && unPayPrice.get("UNPAY_PRICE") != null){
				unpayPrice = Double.parseDouble(unPayPrice.get("UNPAY_PRICE").toString());
			}
			//请求总额
			double total = unpayPrice + fine + totalLawyfee;
			//诉讼费
			double suePrice = 0;
			int p1 = 10000;
			int p2 = 100000;
			int p3 = 200000;
			int p4 = 500000;
			int p5 = 1000000;
			int p6 = 2000000;
			int p7 = 5000000;
			int p8 = 10000000;
			int p9 = 20000000;
			if(total <= 10000){
				suePrice = 50;
			} else if(total > p1 && total <= p2){
				suePrice = total * 0.025 - 200;
			} else if(total > p2 && total <= p3){
				suePrice = total * 0.02 + 300;
			} else if(total > p3 && total <= p4){
				suePrice = total * 0.015 + 1300;
			} else if(total > p4 && total <= p5){
				suePrice = total * 0.01 + 3800;
			} else if(total > p5 && total <= p6){
				suePrice = total * 0.009 + 4800;
			} else if(total > p6 && total <= p7){
				suePrice = total * 0.008 + 6800;
			} else if(total > p7 && total <= p8){
				suePrice = total * 0.007 + 11800;
			} else if(total > p8 && total <= p9){
				suePrice = total * 0.006 + 21800;
			} else if(total > p9){
				suePrice = total * 0.005 + 41800;
			}
			settleMap.put("total", total);
			settleMap.put("suePrice", suePrice);
			if("apply".equals(context.contextMap.get("opType"))){
				//起诉理由字典
				List<Map> resourceIdList=DictionaryUtil.getDictionary("起诉申请理由");
				String sueReasonList = "";
				for (Map m : resourceIdList) {
					//中文；作为分隔符
					sueReasonList = sueReasonList + String.valueOf(m.get("SHORTNAME")) + "；";
				}
				if(sueReasonList.length() > 0){
					sueReasonList = sueReasonList.substring(0, sueReasonList.length()-1);
				}
				outputMap.put("sueReasonList", sueReasonList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("nowDate", new Date());
		outputMap.put("opType", context.contextMap.get("opType"));
		outputMap.put("sueMap", sueMap);
		outputMap.put("suplTrue", context.contextMap.get("suplTrue"));
		outputMap.put("mapSueq", mapSueq);
		outputMap.put("mapContent", mapContent);
		outputMap.put("unPayPrice", unPayPrice);
		outputMap.put("mapDunFine", mapDunFine);
		outputMap.put("settleMap", settleMap);
		outputMap.put("recpId",context.contextMap.get("recpId"));
		Output.jspOutput(outputMap, context, "/dun/sueApplyLetterInfo.jsp");
	}
	

	/**
	 * 新增/修改起诉申请单
	 * @param context
	 * @throws Exception
	 * 2014-1-7 yangliu
	 */
	public void applySue(Context context) throws Exception{
		Map outputMap = new HashMap();
		Map<String,Object> sueMap = (Map<String,Object>)DataAccessor.query("sue.getSueByRectId", context.contextMap, RS_TYPE.MAP);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rectId", context.contextMap.get("sueRectId"));
		String cust = (String)DataAccessor.query("rentContract.getCustCodeByRectId", map, RS_TYPE.OBJECT);
		try {
			if(sueMap == null && "".equals(context.contextMap.get("sueId").toString())){
				//新增起诉申请单
			    DataAccessor.execute("sue.insertSue", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				context.contextMap.put("state", 2);
				//增加法务催收记录
				addDunRecord(context.contextMap.get("s_employeeId").toString(), context.contextMap.get("s_employeeName").toString(), cust, 2);
				//发邮件
				Map<String, Object> sue = (Map<String, Object>)DataAccessor.query("sue.getSueByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("sueId", sue.get("ID"));
				sue = (Map<String,Object>)DataAccessor.query("sue.getSueById", context.contextMap, RS_TYPE.MAP);
				context.contextMap.put("sueCreateDate", sue.get("CREATE_DATE"));
				context.contextMap.put("rectId", sue.get("RECT_ID"));
				Map<String, Object> sueDetail = (Map<String, Object>)DataAccessor.query("sue.getLetterContentByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("sue", sue);
				context.contextMap.put("sueDetail", sueDetail);
				this.makeSueWord(context);
			}else if (!"".equals(context.contextMap.get("sueId").toString()) && sueMap.get("FIRST_UP_USER_ID") == null){
				//修改起诉申请单
			    DataAccessor.execute("sue.updateSue", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				//增加法务催收记录
				addDunRecord(context.contextMap.get("s_employeeId").toString(), context.contextMap.get("s_employeeName").toString(), cust, 3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.querySueApplyLetter(context);
	}

	/**
	 * 审核（通过/驳回）起诉申请单
	 * @param context
	 * @throws Exception
	 * 2014-1-7 yangliu
	 */
	public void approvalSue(Context context) throws Exception{
		Map<String, Object> sue = new HashMap<String, Object>();
		Map<String, Object> sueDetail = new HashMap<String, Object>();
		try {
			//审核结果（1通过，0驳回）
			int status = Integer.parseInt(context.contextMap.get("status").toString());
			//当前申请单状态
			int state = Integer.parseInt(context.contextMap.get("state").toString());
			context.contextMap.put("orgState", state);
			if(state == 2){
				//单位主管审核
				context.contextMap.put("FIRST_UP_USER_ID", context.contextMap.get("s_employeeId"));
				context.contextMap.put("FIRST_UP_USER_MSG", context.contextMap.get("suggest"));
				context.contextMap.put("FIRST_UP_USER_DATE", new Date());
				context.contextMap.put("FIRST_UP_USER_STATUS", status);
			} else if(state == 3){
				//部门主管审核
				context.contextMap.put("SECOND_UP_USER_ID", context.contextMap.get("s_employeeId"));
				context.contextMap.put("SECOND_UP_USER_MSG", context.contextMap.get("suggest"));
				context.contextMap.put("SECOND_UP_USER_DATE", new Date());
				context.contextMap.put("SECOND_UP_USER_STATUS", status);
			} else if(state == 4){
				//总经理审核
				context.contextMap.put("GENERAL_MANAGER_ID", context.contextMap.get("s_employeeId"));
				context.contextMap.put("GENERAL_MANAGER_MSG", context.contextMap.get("suggest"));
				context.contextMap.put("GENERAL_MANAGER_DATE", new Date());
				context.contextMap.put("GENERAL_MANAGER_STATUS", status);
			}
			if(status == 0){
				state = 1;
			} else {
				if(state >= 4){
					state = 5;
				} else {
					state++;
				}
			}
			context.contextMap.put("state", state);
			DataAccessor.execute("sue.approvalSue", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			//获取该诉讼单相关数据
			sue = (Map<String, Object>)DataAccessor.query("sue.getSueById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("sueCreateDate", sue.get("CREATE_DATE"));
			context.contextMap.put("rectId", sue.get("RECT_ID"));
			sueDetail = (Map<String, Object>)DataAccessor.query("sue.getLetterContentByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("sue", sue);
			context.contextMap.put("sueDetail", sueDetail);
			//增加法务催收记录
			addDunRecord(context.contextMap.get("s_employeeId").toString(), context.contextMap.get("s_employeeName").toString(), sueDetail.get("CUST_CODE").toString(), status);
			//生成word发送邮件
			context.contextMap.put("status", status);
			this.makeSueWord(context);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.querySueApprovalLetter(context);
	}
	
	/**
	 * 增加法务催收记录
	 * @param employeeId 申请人ID
	 * @param employeeName 申请人名字
	 * @param custCode 
	 * @param status 1：通过	0：驳回		2：生成		3：修改
	 * @throws Exception
	 */
	private void addDunRecord(String employeeId, String employeeName, String custCode, int status) throws Exception{
	    Map<String, Object> dunRecord = new HashMap<String, Object>();
	    dunRecord.put("ANSWERPHONE_NAME", "000");
	    dunRecord.put("s_employeeId",employeeId);
	    dunRecord.put("PHONE_NUMBER", "000");
	    dunRecord.put("CUST_CODE", custCode);
	    String passStatus = "通过,审核人:";
	    if(status == 0){
	    	passStatus = "被驳回,审核人:";
	    } else if(status == 2){
	    	passStatus = "生成,申请人:";
	    } else if(status == 3){
	    	passStatus = "修改,操作人:";
	    }
	    dunRecord.put("LAWYFEERECORD", "起诉申请单已经" + passStatus + employeeName);
		DataAccessor.execute("collectionManage.createDunRecord", dunRecord, DataAccessor.OPERATION_TYPE.INSERT);
	}
	
	/**
	 * 生成起诉单word
	 * @param context
	 * @throws Exception
	 */
	private void makeSueWord(Context context) throws Exception{
		Map<String, Object> sue = (Map<String, Object>)context.contextMap.get("sue");
		Map<String, Object> sueDetail = (Map<String, Object>)context.contextMap.get("sueDetail");
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		String fileName = DateUtil.dateToString(new Date(), "yyyy_MM_dd") + File.separator+ "" + dateStr + ".pdf";
		String decpId = sueDetail.get("DECP_ID")==null?"":sueDetail.get("DECP_ID").toString();
	    if(decpId.equals("3") || decpId.equals("8")){
	    	sueDetail.put("court", "东莞市第二人民法院");
	    } else {
	    	sueDetail.put("court", "苏州工业园区人民法院");
	    }
	    sueDetail.put("APPLY_USER_NAME", sue.get("APPLY_USER_NAME"));
	    //单位主管名字
	    String fName = "";
	    if(sue.get("FIRST_UP_USER_STATUS") != null && !"0".equals(sue.get("FIRST_UP_USER_STATUS").toString())){
	    	fName = sue.get("FIRST_UP_USER_NAME").toString();
	    }
	    //部门主管名字
	    String sName = "";
	    if(sue.get("SECOND_UP_USER_STATUS") != null && !"0".equals(sue.get("SECOND_UP_USER_STATUS").toString())){
	    	sName = sue.get("SECOND_UP_USER_NAME").toString();
	    }
	    //总经理名字
	    String mName = "";
	    if(sue.get("GENERAL_MANAGER_STATUS") != null && !"0".equals(sue.get("GENERAL_MANAGER_STATUS").toString())){
	    	mName = sue.get("GENERAL_MANAGER_NAME").toString();
	    }
	    sueDetail.put("FIRST_UP_USER_NAME", fName);
	    sueDetail.put("SECOND_UP_USER_NAME", sName);
	    sueDetail.put("GENERAL_MANAGER_NAME", mName);
	    sueDetail.put("SUE_CREATE_DATE", DateUtil.dateToString((Date)sue.get("CREATE_DATE"), "yyyy-MM-dd"));
		String reasons = sue.get("REASONS")==null?"":sue.get("REASONS").toString();
		String[] reasonStrings = reasons.split("；");
		if(reasons.length() > 0 && reasonStrings.length > 0){
			reasons = "";
			for(int i = 0; i < reasonStrings.length; i++){
				reasons = reasons + "<br/>" + (i+1) + "、" + reasonStrings[i] + "；";
			}
		}
		sueDetail.put("REASONS", reasons);
	    //违约金
		DecimalFormat df = new DecimalFormat(".00");
		Map<String, Object> dunAllFine=(Map<String, Object>)DataAccessor.query("dunTask.getAllFineByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		Map<String, Object> dunInCome =(Map<String, Object>)DataAccessor.query("dunTask.getinComeFineByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		double allFine = Double.valueOf(String.valueOf(df.format(dunAllFine.get("FINE"))));
		double inComeFine = Double.valueOf(String.valueOf(df.format(dunInCome.get("DIFF_DUN"))));
		sueDetail.put("fine", allFine-inComeFine);
		//回访日期&厂内营运状况
		Map<String, Object> backVisit = (Map<String, Object>)DataAccessor.query("backVisit.getVisitReviewRecordsByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		if(backVisit == null || backVisit.get("PROD_DEGREE_DETAILED")==null){
			sueDetail.put("PROD_DEGREE_DETAILED", "&nbsp;");
		} else if("0".equals(backVisit.get("PROD_DEGREE_DETAILED").toString())){
			sueDetail.put("PROD_DEGREE_DETAILED", "佳");
		} else if("1".equals(backVisit.get("PROD_DEGREE_DETAILED").toString())){
			sueDetail.put("PROD_DEGREE_DETAILED", "可");
		} else if("2".equals(backVisit.get("PROD_DEGREE_DETAILED").toString())){
			sueDetail.put("PROD_DEGREE_DETAILED", "差");
		}
		if(backVisit == null || backVisit.get("VISIT_DATE")==null){
			sueDetail.put("VISIT_DATE", "&nbsp;");
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sueDetail.put("VISIT_DATE", DateUtil.dateToString(sdf.parse(backVisit.get("VISIT_DATE").toString()), "yyyy-MM-dd"));
		}
	    //法务费用
		context.contextMap.put("zujin", "租金") ;
		context.contextMap.put("zujinfaxi", "租金罚息") ;
		context.contextMap.put("sblgj", "设备留购价") ;
		context.contextMap.put("lawyfee", "法务费用") ;
		context.contextMap.put("RECP_ID", sueDetail.get("RECP_ID"));
		//法务费用
		Map<String, Object> settleMap = (Map<String, Object>) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
		if(settleMap.get("TOTAL_LAWYFEE")==null){
			settleMap.put("TOTAL_LAWYFEE", 0);
		}
		double totalLawyfee = 0;
		double fine = 0;
		double unpayPrice = 0;
		if(settleMap != null && settleMap.get("TOTAL_LAWYFEE") != null){
			totalLawyfee = Double.parseDouble(settleMap.get("TOTAL_LAWYFEE").toString());
		}
		if(sueDetail.get("fine") != null){
			fine = Double.parseDouble(sueDetail.get("fine").toString());
		}
		//未缴总租金
		Map<String, Object> unPayPrice=(Map<String, Object>)DataAccessor.query("dunTask.getUnPayPriceByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		if(unPayPrice != null && unPayPrice.get("UNPAY_PRICE") != null){
			unpayPrice = Double.parseDouble(unPayPrice.get("UNPAY_PRICE").toString());
		}
		//请求总额
		double total = unpayPrice + fine + totalLawyfee;
		//诉讼费
		double suePrice = 0;
		int p1 = 10000;
		int p2 = 100000;
		int p3 = 200000;
		int p4 = 500000;
		int p5 = 1000000;
		int p6 = 2000000;
		int p7 = 5000000;
		int p8 = 10000000;
		int p9 = 20000000;
		if(total <= 10000){
			suePrice = 50;
		} else if(total > p1 && total <= p2){
			suePrice = total * 0.025 - 200;
		} else if(total > p2 && total <= p3){
			suePrice = total * 0.02 + 300;
		} else if(total > p3 && total <= p4){
			suePrice = total * 0.015 + 1300;
		} else if(total > p4 && total <= p5){
			suePrice = total * 0.01 + 3800;
		} else if(total > p5 && total <= p6){
			suePrice = total * 0.009 + 4800;
		} else if(total > p6 && total <= p7){
			suePrice = total * 0.008 + 6800;
		} else if(total > p7 && total <= p8){
			suePrice = total * 0.007 + 11800;
		} else if(total > p8 && total <= p9){
			suePrice = total * 0.006 + 21800;
		} else if(total > p9){
			suePrice = total * 0.005 + 41800;
		}
		if("连保".equals(sueDetail.get("SUPLTRUE").toString())){
			sueDetail.put("SUPLTRUE", "是");
		} else {
			sueDetail.put("SUPLTRUE", "否");
		}
		sueDetail.put("SUM_OWN_PRICE", Double.valueOf(df.format(settleMap.get("SUM_OWN_PRICE"))));
		sueDetail.put("LEASE_PERIOD", Integer.parseInt(unPayPrice.get("LEASE_PERIOD").toString()));
		sueDetail.put("fine", fine);
		sueDetail.put("totalLawyfee", totalLawyfee);
		sueDetail.put("unpayPrice", unpayPrice);
		sueDetail.put("total", total);
		sueDetail.put("suePrice", suePrice);
	    
		//起诉申请单
		birt.executeReport("sue/sue.rptdesign", fileName, sueDetail);

		//发送EMAIL
		int state = Integer.parseInt(context.contextMap.get("state").toString());
		sueDetail.put("state", state);
		MailSettingTo mailSettingTo = new MailSettingTo();
		if(state == 1 || state == 2 || state == 5){
			//申请人
			mailSettingTo.setEmailTo(sue.get("APPLY_USER_EMAIL").toString());
			//抄送
			mailSettingTo.setEmailCc(sueDetail.get("EMAIL").toString()//经办
					+ ";" + sueDetail.get("UP_USER_EMAIL").toString()//经办领导
					+ ";" + sue.get("FIRST_UP_USER_EMAIL").toString());//申请人领导
			if(state == 5){
				//总经理通过
				mailSettingTo.setEmailSubject("[法务课]起訴申請申請單已经通过，请查看。");
			} else if(state == 1){
				//驳回
				String orgState = context.contextMap.get("orgState")==null?"":context.contextMap.get("orgState").toString();
				String rejectName = "";
				if(orgState.equals("2")){
					rejectName = "单位主管";
				} else if(orgState.equals("3")){
					rejectName = "部门主管";
				} else if(orgState.equals("4")){
					rejectName = "总经理";
				}
				mailSettingTo.setEmailSubject("[法务课]起訴申請申請單已被" + rejectName + "驳回，请查看。");
			} else if(state == 2){
				//申请
				mailSettingTo.setEmailSubject("[法务课]起訴申請申請單已经申请，请查看。");
			}
		} else if(state == 3){
			//单位主管通过通知部门主管
			//发送
			mailSettingTo.setEmailTo(sue.get("SECOND_UP_USER_EMAIL").toString());//部门主管
			//抄送
			mailSettingTo.setEmailCc(sue.get("APPLY_USER_EMAIL").toString()//申请人
					+ ";" + sue.get("FIRST_UP_USER_EMAIL").toString());//申请人领导
			mailSettingTo.setEmailSubject("[法务课]起訴申請單已由单位主管通过，请您及时审核。");
		}else if(state == 4){
			//部门主管通过通知总经理
			//发送			
			mailSettingTo.setEmailTo(sue.get("GENERAL_MANAGER_EMAIL").toString());//总经理
			
			//抄送
			mailSettingTo.setEmailCc(sue.get("APPLY_USER_EMAIL").toString()//申请人
					+ ";" + sue.get("FIRST_UP_USER_EMAIL").toString());//申请人领导
			mailSettingTo.setEmailSubject("[法务课]起訴申請單已由部门主管通过，请您及时审核。");

			//添加乘用车判断，乘用车CC 给林总经理
			int productionType = LeaseUtil.getProductionTypeByRectId(Long.parseLong(context.contextMap.get("rectId").toString()));
			if(productionType==3){
				String userid = DictionaryUtil.getFlag("裕国总经理", "1");
				mailSettingTo.setEmailCc(mailSettingTo.getEmailCc()+";"+LeaseUtil.getEmailByUserId(userid));//申请人
			}
			
		}
		mailSettingTo.setEmailContent(getOutVisitMailContent(sueDetail,sue.get("CREATE_DATE").toString()));
		mailSettingTo.setEmailAttachPath(birt.getOutputPath() + File.separator + fileName);
		mailUtilService.sendMail(mailSettingTo);
	}
	
	/**
	 * 发送邮件内容
	 * @param mapContent 申请单
	 * @param createDate 申请日期
	 * @return
	 */
	private String getOutVisitMailContent(Map<String, Object> mapContent,String createDate){
		if (mapContent == null) {
			return null;
		}
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			try {
				sb.append("<font size='3'>申请日期：</font>" + format.format(format.parse(createDate))
				+"<br><font size='3'>承租人名称：</font>"+mapContent.get("CUST_NAME")+
				"<br><font size='3'>合同號：</font>" +mapContent.get("LEASE_CODE")+
				"<br><font size='3'>逾期天数：</font>"+mapContent.get("DUN_DAY")+
				"<br><font size='3'>供应商连保：</font>"+mapContent.get("SUPLTRUE"));
				if("2".equals(mapContent.get("state").toString()) && mapContent.get("SUPLTRUE")!=null && "是".equals(mapContent.get("SUPLTRUE").toString())){
					sb.append("<br><font color='red' size='5'>供应商连保是否连同起诉，请表示意见</font>");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sb.append("</html>");
		return sb.toString();
	}
	
	/**
	 * 根据rectid获取设备列表
	 * @param rectId
	 * @return
	 */
	public static List<Map<String,String>> getSueEqupment(String rectId){
		Map<String, String> map = new HashMap<String, String>();
		map.put("rectId", rectId);
		List<Map<String,String>> sueEqupment = new ArrayList<Map<String,String>>();
		try {
			//设备列表
			sueEqupment = (List<Map<String,String>>)DataAccessor.query("dunTask.getEqupmentListByRectId",map,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sueEqupment;
	}
}
