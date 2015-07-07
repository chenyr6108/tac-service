package com.brick.credit.vip.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.BaseTo;
import com.brick.base.to.LinkManTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.credit.to.CreditTo;
import com.brick.customer.service.CustomerCredit;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.project.service.TagService;
import com.brick.project.to.TagTo;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.risk_audit.service.RiskAuditService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.supplier.to.SupplierGroupTO;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 * 资信管理
 * 
 * @author li shaojie
 * @date Apr 27, 2010
 */

public class CreditReportManage extends BaseCommand {
	Log logger = LogFactory.getLog(CreditReportManage.class);

	private RiskAuditService riskAuditService;
	
	public RiskAuditService getRiskAuditService() {
		return riskAuditService;
	}
	public void setRiskAuditService(RiskAuditService riskAuditService) {
		this.riskAuditService = riskAuditService;
	}
	private TagService tagService;

	public TagService getTagService() {
		return tagService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}
	/**
	 * 报告审批-管理页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creditExamine(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		List errList = context.errList ;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));

		//报告类型设置为全部 add by ShenQi
		context.contextMap.put("creditStauts",-3);
		//类型只留提交主管的
		if(context.contextMap.get("credit_type")==null||"".equals(context.contextMap.get("credit_type"))) {
			context.contextMap.put("credit_type",3);
		}
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("vip_flag", 1);
			pagingInfo = baseService.queryForListWithPaging(
					"creditReportManage.getCreditReports2_vip", context.contextMap,
					"CREDIT_RUNCODE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告审批列表错误!请联系管理员") ;
		}
		
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("creditStauts", context.contextMap.get("creditStauts"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/creditReportExamine.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}

	/**
	 * 作废报告
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invCredit(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try {
			DataAccessor.execute("creditReportManage.invCredit",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告审批作废错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
//			Output.jspSendRedirect(context,
//					"defaultDispatcher?__action=creditReport.creditExamine");此功能移到业务支撑中
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditDisabledEnabledCommand.query");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 启用报告
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void enaCredit(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try {
			DataAccessor.execute("creditReportManage.enaCredit",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告审批启用错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
//			Output.jspSendRedirect(context,
//					"defaultDispatcher?__action=creditReport.creditExamine");
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditDisabledEnabledCommand.query");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 资信管理
	 * 
	 * @param context  
	 */
	@SuppressWarnings("unchecked")
	public void creditManage(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		List errList = context.errList ;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));

		//默认过滤条件是调查中 add by shen qi
		if(context.contextMap.get("credit_type")==null||"".equals(context.contextMap.get("credit_type"))) {
			context.contextMap.put("credit_type",0);
		}
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("vip_flag", 1);
			pagingInfo = baseService.queryForListWithPaging("creditReportManage.getCreditReports", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			//add是控制页面是否显示新建报告
			boolean add=false;
			//view是控制页面是否显示查看链接
			boolean view=false;
			//modify是控制页面是否显示修改链接
			boolean modify=false;
			//commit是控制页面是否显示提交链接
			boolean commit=false;
			//info是控制页面是否显示资料链接
			boolean info=false;
			//log是控制页面是否显示日志链接
			boolean log=false;
			//cancel是控制页面是否显示撤销链接
			boolean cancel=false;
			//transfer是客户经理转移链接
			boolean transfer=false;
			//display是控制页面是否显示操作的列
			boolean display=true;
			//标签
			boolean tag_authority = false;
			BaseTo baseTo = new BaseTo();
			baseTo.setModify_by(context.contextMap.get("s_employeeId").toString());
			baseTo.setResource_code("report_manage_tag");
			tag_authority = tagService.checkAccessForResource(baseTo);
			
			//通过emplId获得用户的ResourceId.(add by ShenQi)
			List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    /*ResourceId               Permission  
	         *188  						新建报告
			  189  						查看
			  190 						修改
			  191  						提交
			  192      					资料
			  193  						日志
		      194  						撤销
		      215						客户经理转移
		     * */
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				//below is hard code for ResourceId,we will enhance it in the future
				if("188".equals(resourceIdList.get(i))) {
					add=true;
				} else if("189".equals(resourceIdList.get(i))) {
					view=true;
				} else if("190".equals(resourceIdList.get(i))) {
					modify=true;
				} else if("191".equals(resourceIdList.get(i))) {
					commit=true;
				} else if("192".equals(resourceIdList.get(i))) {
					info=true;
				} else if("193".equals(resourceIdList.get(i))) {
					log=true;
				} else if("194".equals(resourceIdList.get(i))) {
					cancel=true;
				} else if("215".equals(resourceIdList.get(i))) {
					transfer=true;
				}
			}
			
			if(!view&&!modify&&!commit&&!info&&!log&&!cancel&&!transfer) {
				display=false;
			}
			
			List<TagTo> tagList = tagService.getAllTags(1);
			outputMap.put("tags", tagList);
			
			outputMap.put("add",add);
			outputMap.put("view",view);
			outputMap.put("modify",modify);
			outputMap.put("commit",commit);
			outputMap.put("info",info);
			outputMap.put("log",log);
			outputMap.put("cancel",cancel);
			outputMap.put("transfer",transfer);
			outputMap.put("display",display);
			outputMap.put("tag_authority",tag_authority);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告管理错误!请联系管理员") ;
		}
		
		//插入日志记录移动到提交方法中,而不是放在遍历数据的方法中,造成的bug就是遍历数据也会插入系统日志 add by ShenQi
//			Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
//			String logType = "报告提交";
//			String logTitle = "提交";
//
//			String logCode = "";
//
//			try {
//
//				logCode = DataAccessor.query("creditReportManage.selectCreditCode", context.getContextMap(), RS_TYPE.OBJECT) +"";
//
//			} catch (Exception e) {
//
//				e.printStackTrace();
//				LogPrint.getLogStackTrace(e, logger);
//			}
//
//			if(logCode == null){
//				logCode = "";
//			}	
//			String memo="报告提交";
//			int state = 1;
//			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
//			Long otherId = null;
//
//
//
//			BusinessLog.addBusinessLog(creditId, null, logType, logTitle, logCode, memo, state, userId, otherId);
		
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/creditReportManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
		}
	}

	/**
	 * 资信授权管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void creditPowerManage(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List errList = context.errList ;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));

			dw = (DataWrap) DataAccessor.query(
					"creditReportManage.getCreditReports", context.contextMap,
					DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--项目授权列表错误!请联系管理员") ;
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,
					"/credit_vip/creditReportPowerManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 现场调查授权查询
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectUserPower(Context context) {
		Map outputMap = new HashMap();
		context.contextMap.put("job", "1");
		List userList = null;
		List errList = context.errList ;
		try {
			userList = (List) DataAccessor.query(
					"creditReportManage.selectUser", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--项目授权授权页错误!请联系管理员") ;
		}
		outputMap.put("credit_id", context.contextMap.get("creditId"));
		outputMap.put("userList", userList);
		outputMap.put("errList", errList) ;
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 现场调查取消授权查询
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectPower(Context context) {
		Map outputMap = new HashMap();
		List powerList = null;
		List errList = context.errList ;
		try {
			powerList = (List) DataAccessor.query(
					"creditReportManage.selectFalg", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--项目授权 撤销授权页 错误!请联系管理员") ;
		}
		outputMap.put("credit_id", context.contextMap.get("creditId"));
		outputMap.put("powerList", powerList);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 现场调查授权添加
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void addPower(Context context) {
		String[] str = context.request.getParameterValues("username");
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			for (int i = 0; i < str.length; i++) {
				Map map = new HashMap();
				map.put("user_id", str[i]);
				map.put("s_employeeId", context.contextMap.get("s_employeeId"));
				map.put("creditId", context.contextMap.get("crId"));
				sqlMapper.insert("creditReportManage.insertPower", map);
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			context.errList.add("项目管理--项目授权 添加授权错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		creditPowerManage(context);
	}

	/**
	 * 现场调查授权修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void upPower(Context context) {
		String[] str = context.request.getParameterValues("username");
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			// Map creditMap = (Map) DataAccessor.query(
			// "creditReportManage.selectCreditById", context.contextMap,
			// DataAccessor.RS_TYPE.MAP);
			// creditMap.put("s_employeeId", context.contextMap
			// .get("s_employeeId"));
			// creditMap.put("create_date", creditMap.get("CREATE_DATE"));
			sqlMapper.startTransaction() ;
			for (int i = 0; i < str.length; i++) {
				Map map = new HashMap();
				map.put("user_id", str[i]);
				map.put("s_employeeId", context.contextMap.get("s_employeeId"));
				map.put("creditId", context.contextMap.get("crId"));
				sqlMapper.delete("creditReportManage.deletePower", map);
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			context.errList.add("项目管理--项目授权 删除授权错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		creditPowerManage(context);
	}

	/**
	 * 添加方案
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createScheme(Context context) {
		
		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		//检查区域主管,客户经理等 add by ShenQi
		/*if(context.contextMap.get("clerkList_name")==null||context.contextMap.get("sensorList_name")==null||context.contextMap.get("customerComeList")==null) {
			errList.add("请联系管理员清除浏览器缓存!");
			outputMap.put("errList",errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}*/
		
		try {
			//基本信息
			DataAccessor.execute("creditReportManage.updateconType_vip", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

			sqlMapper.delete("creditReportManage.deleteCreditSchemaIrr", context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditScheme",
					context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditInsure",
					context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditEquipment",
					context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditOtherPrice",
					context.contextMap);
			if (context.request.getParameterValues("SUPPIER") != null) {
				String[] TYPE = context.request.getParameterValues("TYPE");
				String[] KIND = context.request.getParameterValues("KIND");
				String[] PRODUCT = context.request
						.getParameterValues("PRODUCT");
				String[] SUPPIER = context.request
						.getParameterValues("SUPPIER");
				String[] TYPE_NAME = context.request
						.getParameterValues("TYPE_NAME");
				String[] KIND_NAME = context.request
						.getParameterValues("KIND_NAME");
				String[] PRODUCT_NAME = context.request
						.getParameterValues("PRODUCT_NAME");
				String[] SUPPIER_NAME = context.request
						.getParameterValues("SUPPIER_NAME");
				String[] STAYBUY_PRICE = context.request
						.getParameterValues("STAYBUY_PRICE");
				String[] UNIT_PRICE = context.request
						.getParameterValues("UNIT_PRICE");
				String[] AMOUNT = context.request.getParameterValues("AMOUNT");
				String[] UNIT = context.request.getParameterValues("UNIT");
				String[] SHUI_PRICE = context.request.getParameterValues("SHUI_PRICE");
				//String[] MEMO = context.request.getParameterValues("MEMO");
				String[] LOCK_CODE = context.request
						.getParameterValues("LOCK_CODE");
				//配件说明
				String[] MOUNTINGS = context.request
						.getParameterValues("MOUNTINGS");
				Map map = null;
				Map<String, Object> paramMap = null;
				for (int i = 0; i < SUPPIER.length; i++) {
					if (!SUPPIER[i].equals("-1") && !TYPE[i].equals("-1")
							&& !PRODUCT[i].equals("-1")
							&& !KIND[i].equals("-1")) {
						int amount = Integer.parseInt(AMOUNT[i]);
						paramMap = new HashMap<String, Object>();
						paramMap.put("PRODUCT_ID", HTMLUtil.parseStrParam2(PRODUCT[i], "0"));
						paramMap.put("SUPPLIER_ID", HTMLUtil.parseStrParam2(SUPPIER[i], "0"));
						map = new HashMap();
						map.put("SUEQ_ID", sqlMapper.queryForObject("creditReportManage.getSueqId", paramMap));
						map.put("THING_NAME", HTMLUtil.parseStrParam2(
								KIND_NAME[i], ""));
						map.put("BRAND", HTMLUtil.parseStrParam2(
								SUPPIER_NAME[i], ""));
						map.put("MODEL_SPEC", HTMLUtil.parseStrParam2(
								PRODUCT_NAME[i], ""));
						map.put("UNIT_PRICE",  HTMLUtil
								.parseStrParam2(UNIT_PRICE[i], ""));
//						map.put("MEMO", HTMLUtil
//								.parseStrParam2(MEMO[i], ""));
						map.put("LOCK_CODE", HTMLUtil.parseStrParam2(
								LOCK_CODE[i], ""));
						//设备留购款，一个合同100块，在第一台设备加上100块，其他设备都为0
						//map.put("STAYBUY_PRICE", STAYBUY_PRICE[i]);
						if (i==0) {
							map.put("STAYBUY_PRICE", "100.00"); 
						}else {
							map.put("STAYBUY_PRICE", "0.00"); 
						}
						
						//配件说明
						map.put("MOUNTINGS", HTMLUtil.parseStrParam2(
								MOUNTINGS[i], ""));
						
						map.put("UNIT", HTMLUtil
								.parseStrParam2(UNIT[i], ""));
						map.put("SHUI_PRICE", HTMLUtil
								.parseStrParam2(SHUI_PRICE[i], ""));
						map.put("THING_KIND", HTMLUtil.parseStrParam2(
								TYPE_NAME[i], ""));
						map.put("s_employeeId", context.request
								.getSession().getAttribute("s_employeeId"));
						map.put("CREDIT_ID", context.contextMap
								.get("credit_id"));
						for (int j = 0; j < amount; j++) {
							sqlMapper.insert("creditReportManage.createCreditEquipment", map);
						}
					}
				}
			}
			// set base rate
			//PaylistUtil.setBaseRate(context.contextMap);
			String contractType=(String) context.contextMap.get("contract_typew");

			PaylistUtil.setBaseRate(context.contextMap,contractType);
												
			if(context.contextMap.get("pay__money")==null){
				context.contextMap.put("pay__money", 0f);
			}
			 
			if(context.contextMap.get("incomePay")==null){
				context.contextMap.put("incomePay", 0f);
			}
			
			if(context.contextMap.get("outPay")==null){
				context.contextMap.put("outPay", 0f);
			}


			sqlMapper.insert("creditReportManage.createCreditScheme",context.contextMap);
			/*
			 * wujw
			 * insert credit schema irr month price
			 * 
			 */
			String[] PAY_IRR_MONTH_PRICE = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_TAB1", "0");
			String[] PAY_IRR_MONTH_PRICE_START = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_START_TAB1", "0");
			String[] PAY_IRR_MONTH_PRICE_END = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_END_TAB1", "0");
			
			for (int i=0; i<PAY_IRR_MONTH_PRICE.length; i++) {
				Map paramMap = new HashMap();
				paramMap.put("IRR_MONTH_PRICE", DataUtil.doubleUtil(PAY_IRR_MONTH_PRICE[i]));
				paramMap.put("IRR_MONTH_PRICE_START", DataUtil.intUtil(PAY_IRR_MONTH_PRICE_START[i]));
				paramMap.put("IRR_MONTH_PRICE_END", DataUtil.intUtil(PAY_IRR_MONTH_PRICE_END[i]));
				paramMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				paramMap.put("S_EMPLOYEEID", context.contextMap.get("s_employeeId"));
				sqlMapper.insert("creditReportManage.create-credit-schema-irr", paramMap);				
			}
			//Add by Michael 2012 01/14 增加费用保存
			sqlMapper.delete("creditReportManage.deletePayListFeeList",
					context.contextMap);
			List feeSetList =null;
			Map where=null;
			feeSetList = (List) DataAccessor.query(
					"creditReportManage.getFeeSetListAll", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < feeSetList.size(); i++) {
				Map tempMap = new HashMap();
				where = (Map) feeSetList.get(i);
				tempMap.put("FEE_SET_ID", where.get("ID"));
				tempMap.put("CREATE_SHOW_NAME", where.get("CREATE_SHOW_NAME"));
				tempMap.put("IS_LEASERZE_COST", where.get("IS_LEASERZE_COST"));
				tempMap.put("CREATE_FILED_NAME", where.get("CREATE_FILED_NAME")) ;
				tempMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				tempMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				tempMap.put("FEE", DataUtil.doubleUtil(context.contextMap.get(where.get("CREATE_FILED_NAME"))));
				//Add by Michael 2012 12-20 增加费用来源
				tempMap.put("SOURCE_CODE", context.contextMap.get(where.get("CREATE_FILED_NAME")+"_SOURCE"));

				sqlMapper.insert("creditReportManage.insertPayListFeeList", tempMap);
			}			
			//----------------------------------------------------------------
			
				
			if (context.request.getParameterValues("INSURE_ITEM") != null) {
				String[] INSURE_ITEM = context.request
						.getParameterValues("INSURE_ITEM");
				String[] START_DATE = context.request
						.getParameterValues("START_DATE_");
				String[] END_DATE = context.request
						.getParameterValues("END_DATE");
				String[] INSURE_RATE = context.request
						.getParameterValues("INSURE_RATE");
				String[] INSURE_PRICE = context.request
						.getParameterValues("INSURE_PRICE");
				String[] INSURE_MEMO = context.request
						.getParameterValues("INSURE_MEMO");
				for (int i = 0; i < INSURE_ITEM.length; i++) {
					Map map = new HashMap();
					map.put("INSURE_ITEM", HTMLUtil.parseStrParam2(
							INSURE_ITEM[i], "0"));
					map.put("START_DATE", HTMLUtil.parseStrParam2(
							START_DATE[i], ""));
					map.put("END_DATE", HTMLUtil
							.parseStrParam2(END_DATE[i], ""));
					map.put("INSURE_RATE", HTMLUtil.parseStrParam2(
							INSURE_RATE[i], "0"));
					map.put("INSURE_PRICE", HTMLUtil.parseStrParam2(
							INSURE_PRICE[i], "0"));
					map
							.put("MEMO", HTMLUtil.parseStrParam2(
									INSURE_MEMO[i], ""));
					map.put("CREDIT_ID", context.contextMap.get("credit_id"));
					sqlMapper.insert("creditReportManage.createCreditInsure",
							map);
				}
			}

			if (context.request.getParameterValues("OTHER_NAME") != null) {
				String[] OTHER_NAME = context.request
						.getParameterValues("OTHER_NAME");
				String[] OTHER_PRICE = context.request
						.getParameterValues("OTHER_PRICE");
				String[] OTHER_DATE = context.request
						.getParameterValues("OTHER_DATE");
				String[] OTHER_MEMO = context.request
						.getParameterValues("OTHER_MEMO");
				for (int i = 0; i < OTHER_NAME.length; i++) {
					Map map = new HashMap();
					map.put("OTHER_NAME", HTMLUtil.parseStrParam2(
							OTHER_NAME[i], "0"));
					map.put("OTHER_PRICE", HTMLUtil.parseStrParam2(
							OTHER_PRICE[i], ""));
					map.put("OTHER_DATE", HTMLUtil.parseStrParam2(
							OTHER_DATE[i], ""));
					map
							.put("MEMO", HTMLUtil.parseStrParam2(OTHER_MEMO[i],
									"0"));
					map.put("CREDIT_ID", context.contextMap.get("credit_id"));
					sqlMapper.insert(
							"creditReportManage.createCreditOtherPrice", map);
				}
			}
			//拨款方式交机前后
			if (context.request.getParameterValues("APPROPRIATEFUNS") != null) {
				String[] APPROPRIATEFUNS = context.request
				.getParameterValues("APPROPRIATEFUNS");
				Map creditidMap=new HashMap();
				creditidMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				sqlMapper.delete(
						"creditReportManage.deleteAppropiateMon", creditidMap);
				for (int k = 0; k < APPROPRIATEFUNS.length; k++) {
					Map map = new HashMap();
					map.put("TYPE", HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k], ""));
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("0")){
						//System.out.println(Double.parseDouble(context.contextMap.get("APPROPIATEMON0").toString()));
						map.put("APPROPIATEMON", Double.parseDouble(context.contextMap.get("APPROPIATEMON0").toString().equals("")?"0":context.contextMap.get("APPROPIATEMON0").toString()));

					}
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("1")){
					map.put("APPROPIATEMON", Double.parseDouble(context.contextMap.get("APPROPIATEMON1").toString().equals("")?"0":context.contextMap.get("APPROPIATEMON1").toString()));
					}
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("0")){
					map.put("PAYPERCENT", Double.parseDouble(context.contextMap.get("PAYPERCENT0").toString().equals("")?"0":context.contextMap.get("PAYPERCENT0").toString()));
					}
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("1")){
					map.put("PAYPERCENT", Double.parseDouble(context.contextMap.get("PAYPERCENT1").toString().equals("")?"0":context.contextMap.get("PAYPERCENT1").toString()));
					}
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("0")){
					map.put("APPRORIATENAME", context.contextMap.get("APPRORIATENAME0"));
					}
					if(HTMLUtil.parseStrParam2(
							APPROPRIATEFUNS[k],"").equals("1")){
					map.put("APPRORIATENAME", context.contextMap.get("APPRORIATENAME1"));
					}

					map.put("STATUS", 0);
					map.put("CREATE_USER_ID", context.contextMap.get("s_employeeId"));
					map.put("MODIFY_USER_ID", context.contextMap.get("s_employeeId"));
					map.put("CREDIT_ID", context.contextMap.get("credit_id"));
					
					sqlMapper.insert(
							"creditReportManage.createAppropiateMon", map);
				}
			}
			//修改主档
			//先删除该报告对应的主档的credit_id字段
			/*sqlMapper.update("customer.deleteZhudangCreditId", context.contextMap) ;
			if(((String)context.contextMap.get("ACTILOG_ID"))!=""){
				context.contextMap.put("creditId", context.contextMap.get("credit_id")) ;
				//如果修改
				sqlMapper.update("creditCustomer.updatelog", context.contextMap);
			}*/
			
			//******************************************************************************************
			// 2012/12/20 Yang Yun copy (add by ShenQi 2012-8-16)
			//加入建议承做理由,其他租赁条件说明保存
			sqlMapper.delete("creditPriorRecords.deleteCreditPriorProjects1",context.contextMap);
			
			Map<String,Object> param1=new HashMap<String,Object>();
			param1.put("credit_id",context.contextMap.get("credit_id"));
			param1.put("STATE",1);
			param1.put("PROJECT_CONTENT",context.contextMap.get("ADVISE_CONTENT"));
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",param1);
			
			Map<String,Object> param2=new HashMap<String,Object>();
			param2.put("credit_id",context.contextMap.get("credit_id"));
			param2.put("STATE",50);
			param2.put("PROJECT_CONTENT",context.contextMap.get("EXPLAIN_CONTENT"));
			sqlMapper.insert("creditPriorRecords.createCreditPriorProjects",param2);
			//******************************************************************************************
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告基本信息添加错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(errList.isEmpty()){
		String cust_type = (String) context.contextMap.get("H_CUST_TYPE");
			if (cust_type.equals("1")) {
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorpVip.selectCreditCustomerCorpForUpdate&credit_id="
						+ context.contextMap.get("credit_id"));
			} else {
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorpVip.selectCreditCustomerCorpForUpdate&credit_id="
						+ context.contextMap.get("credit_id"));
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 查询资信方案信息用户更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditForUpdate(Context context) {
		Map outputMap = new HashMap();
		Map creditMap = null;
		Map schemeMap = null;
		List equipmentsList = null;
		List insuresList = null;
		List otherPriceList = null;
		List payWayList = null;
		List dealWayList = null;
		List insureBuyWayList = null;
		List insureCompanyList = null;
		List insureTypeList = null;
		List lockList = null;		
		List clerkList = null;		
		List sensorList = null;	
		List provinces = null;
		List citys = null;
		List area = null;
		List suplList=null;
		
		List errList = context.errList ;
		//供应商的授信信息 胡昭卿加
		Map supperGrantMap=new HashMap();
		//客户的授信信息 胡昭卿加
		Map custGrantMap=null;
		
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			schemeMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditScheme",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			
			equipmentsList = (List) DataAccessor.query(
					"creditReportManage.selectCreditEquipment",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			insuresList = (List) DataAccessor.query(
					"creditReportManage.selectCreditInsure",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			otherPriceList = (List) DataAccessor.query(
					"creditReportManage.selectCreditOtherPrice",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			//客户来源
			outputMap.put("customerCome", DictionaryUtil.getDictionary("客户来源"));		
			//省
			provinces = (List) DataAccessor.query("area.getProvinces",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("provinces", provinces);
			//市
			context.contextMap.put("provinceId", creditMap.get("PROVINCE_ID"));
			citys = (List) DataAccessor.query("area.getCitysByProvinceId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("citys", citys);
			//区
			context.contextMap.put("cityId", creditMap.get("CITY_ID"));
			area = (List) DataAccessor.query("area.getAreaByCityId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("area", area);
			//报告类型
			outputMap.put("creditTypes", DictionaryUtil.getDictionary("尽职调查报告类型"));
			//
			outputMap.put("contractType", DictionaryUtil.getDictionary("融资租赁合同类型")); 
			//区域主管
			context.contextMap.put("jobType", "员工职位");
			context.contextMap.put("jobName","业务主管");
			context.contextMap.put("jobName2","");
			clerkList = (List) DataAccessor.query("employee.getEmpForJob",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("clerkList", clerkList);
			//客户经理
			context.contextMap.put("jobType", "员工职位");
			context.contextMap.put("jobName","业务员");
			context.contextMap.put("jobName2","业务助理");
			sensorList = (List) DataAccessor.query("employee.getEmpForJob",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("sensorList", sensorList);
			
			outputMap.put("lockList", lockList);
			outputMap.put("creditMap", creditMap);
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("insuresList", insuresList);
			outputMap.put("otherPriceList", otherPriceList);

			//
			insureCompanyList = (List<Map>) DataAccessor.query(
					"insuCompany.queryInsureCompanyListForSelect", null,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureCompanyList", insureCompanyList);
			//
			insureTypeList = (List<Map>) DataAccessor.query(
					"insureType.queryInsureTypeList", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureTypeList", insureTypeList);
			//
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));

			context.contextMap.put("dictionaryType", "支付方式");
			payWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("dictionaryType", "租赁期满处理方式");
			dealWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("dictionaryType", "保险购买方式");
			insureBuyWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap,DataAccessor.RS_TYPE.LIST);
			
			suplList=(List)DictionaryUtil.getDictionary("供应商保证");
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			outputMap.put("suplList", suplList);
			outputMap.put("payWayList", payWayList);
			outputMap.put("dealWayList", dealWayList);
			outputMap.put("insureBuyWayList", insureBuyWayList);
			outputMap.put("showFlag", 0);
			
			// irr month
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			outputMap.put("irrMonthPaylines", irrMonthPaylines);
			
			//Add by Michael 2012 1/9 报告修改进入时要重新计算TR-----------------------------------
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
			if(schemeMap!=null&&"4".equals(schemeMap.get("TAX_PLAN_CODE"))) {
				schemeMap.put("payList",rePaylineList);
				schemeMap.put("PLEDGE_AVE_PRICE",schemeMap.get("PLEDGE_AVE_PRICE")==null||"".equals(schemeMap.get("PLEDGE_AVE_PRICE"))?0:schemeMap.get("PLEDGE_AVE_PRICE"));
				schemeMap.put("PLEDGE_BACK_PRICE",schemeMap.get("PLEDGE_BACK_PRICE")==null||"".equals(schemeMap.get("PLEDGE_BACK_PRICE"))?"0":schemeMap.get("PLEDGE_BACK_PRICE"));
				schemeMap.put("MAGR_FEE",schemeMap.get("MANAGEMENT_FEE")==null||"".equals(schemeMap.get("MANAGEMENT_FEE"))?0:schemeMap.get("MANAGEMENT_FEE"));
				schemeMap.put("PLEDGE_LAST_PERIOD",schemeMap.get("PLEDGE_LAST_PERIOD")==null||"".equals(schemeMap.get("PLEDGE_LAST_PERIOD"))?0:schemeMap.get("PLEDGE_LAST_PERIOD"));
			}
			Map paylist = null;
			if (schemeMap != null) {
				//Add by Michael 2012 01/29 在方案里增加合同类型
				schemeMap.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				
				if("2".equals(schemeMap.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))||"4".equals(schemeMap.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				schemeMap.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------
				
				schemeMap.put("TOTAL_PRICE", schemeMap.get("LEASE_TOPRIC"));
				schemeMap.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
				schemeMap.put("LEASE_TERM", schemeMap.get("LEASE_COURSE"));
				// 
				if (irrMonthPaylines.size() > 0) {
					// 如果应付租金存在，则以应付租金的方式计算
					paylist = StartPayService.createCreditPaylistIRR(schemeMap,rePaylineList,irrMonthPaylines);
				} else {
					// 如果应付租金不存在，则以年利率(合同利率)的方式计算
					paylist = StartPayService.createCreditPaylist(schemeMap,new ArrayList<Map>());
				}
			}
			outputMap.put("paylist", paylist);
			//-----------------------------------------------------------------------------------------
	
			//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeList", feeList);	
			
			//费用设定明细 影响概算成本为1 不影响为0
			List feeSetListRZE=null;
			feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetListRZE", feeSetListRZE);
			List feeSetList=null;
			feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetList", feeSetList);			
			
			//查询报告对应的供应商的授信信息
			List supperGrantList = (List) DataAccessor.query(
					"creditReportManage.selectSupperGrantInfo",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(supperGrantList.size()>0){
				supperGrantMap=(HashMap)supperGrantList.get(0);
				if(supperGrantMap.get("GRANT_PRICE")!=null){
					supperGrantMap.put("LAST_PRICE", SelectReportInfo.selectApplyLastPrice(Integer.parseInt(supperGrantMap.get("ID").toString())));
				}else{
					supperGrantMap.put("LAST_PRICE","0.00");
				}
			
			//抓取供应商交机前授信额度
			context.contextMap.put("suppl_id", supperGrantMap.get("ID"));
			context.contextMap.put("SUPLNAME", supperGrantMap.get("SUPNAME"));
			
			Map suplGrantMoneyMap;
			Map totalPayMoneyMap;
			
			suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if (suplGrantMoneyMap!=null){
				supperGrantMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));

				totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
				if (totalPayMoneyMap!=null){
					//判断授信的交机前拨款额度是否大于已用额度
					if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
						supperGrantMap.put("advance_machine", 0);
					}else{
						supperGrantMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
					}
				}
			}else{
				supperGrantMap.put("advance_machine", 0);
				supperGrantMap.put("advance_grant", 0);
			}
			outputMap.put("supperGrantMap", supperGrantMap);
			//查询报告对应的客户的授信信息
			custGrantMap = (Map) DataAccessor.query(
					"creditReportManage.grantcustInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(custGrantMap!=null){
			
				if(custGrantMap.get("GRANT_PRICE")!=null){
					custGrantMap.put("GRANT_PRICE", custGrantMap.get("GRANT_PRICE").toString());
					custGrantMap.put("LAST_PRICE", CustomerCredit.getCustCredit(custGrantMap.get("CUST_ID")));
					
				}else{
					custGrantMap.put("GRANT_PRICE", "0");
					custGrantMap.put("LAST_PRICE", "0");
				}
			}	
			}
			outputMap.put("custGrantMap", custGrantMap);
			//查询出拨款情况
			List appropiateList = (List) DataAccessor.query("creditReportManage.getAppropiateByCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("appropiateList", appropiateList);
			
			//查出主档信息
			context.contextMap.put("custId", creditMap.get("CUST_ID")+"") ;
			outputMap.put("groupNumIdlist",DataAccessor.query("customer.groupNumIdCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST));
			
			//在租赁方案中加入权限控制 add by ShenQi see mantis 307
			//199 公司代号
		    //200 区域主管
		    //201 客户经理
		    //202 客户来源
			
			List<String> resourceIdList=(List<String>)DataAccessor.query("supplier.getResourceIdListByEmplId",context.contextMap,DataAccessor.RS_TYPE.LIST);
			boolean cmpy=false;
			boolean areaMar=false;
			boolean customerMar=false;
			boolean customer=false;
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				if("199".equals(resourceIdList.get(i))) {
					cmpy=true;
				} else if("200".equals(resourceIdList.get(i))) {
					areaMar=true;
				} else if("201".equals(resourceIdList.get(i))) {
					customerMar=true;
				} else if("202".equals(resourceIdList.get(i))) {
					customer=true;
				}
			}
			
			//2012/08/17 Yang Yun 增加租赁物放置地 分3级（省-市-区）
			if (schemeMap != null) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				List<Object> citys_eq = null;
				List<Object> areas_eq = null;
				// 取市
				paramMap.put("provinceId", schemeMap.get("EQUPMENT_PROVINCE"));
				citys_eq = (List) DataAccessor.query("area.getCitysByProvinceId", paramMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys_eq", citys_eq);
				
				//去地区getAreaByCityId
				paramMap.put("cityId", schemeMap.get("EQUPMENT_CITY"));
				areas_eq = (List) DataAccessor.query("area.getAreaByCityId", paramMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("areas_eq", areas_eq);
			}
			
			outputMap.put("cmpy",cmpy);
			outputMap.put("areaMar",areaMar);
			outputMap.put("customerMar",customerMar);
			outputMap.put("customer",customer);
			
			//监控报告是否是有效的 add by ShenQi
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			DataAccessor.execute("creditReportManage.monitorCredit",context.contextMap,OPERATION_TYPE.UPDATE);
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi copy by Yang Yun
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
			
			//Add by Michael 2012 09-21 增加税费测算方案
			outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
			
			//Add by Michael 2012 12-20  增加费用来源
			outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告查询基本信息（修改页使用）错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	/**
	 * 查询资信方案信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditForShow(Context context) {
		Map outputMap = new HashMap();
		Map creditMap = null;
		Map schemeMap = null;
		List equipmentsList = null;
		List insuresList = null;
		List otherPriceList = null;
		List payWayList = null;
		List dealWayList = null;
		List insureBuyWayList = null;
		List insureCompanyList = null;
		List insureTypeList = null;
		List contractType = null;
		Map memoMap = null;
		List lockList = null;
		List suplList=null;
		Map creditCustomerCorpMap = null;
		//供应商的授信信息 胡昭卿加
		Map supperGrantMap=null;
		//客户的授信信息 胡昭卿加
		Map custGrantMap=null;
		List errList = context.errList ;
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			schemeMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditScheme",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			equipmentsList = (List) DataAccessor.query(
					"creditReportManage.selectCreditEquipment",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			insuresList = (List) DataAccessor.query(
					"creditReportManage.selectCreditInsure",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			otherPriceList = (List) DataAccessor.query(
					"creditReportManage.selectCreditOtherPrice",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			outputMap.put("lockList", lockList);
			context.contextMap.put("dataType", "融资租赁合同类型");
			contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("contractType", contractType); 			

			
			creditCustomerCorpMap = (Map) DataAccessor.query(
					"creditCustomerCorp.getCreditCustomerCorpByCreditId",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(creditCustomerCorpMap==null){
				creditCustomerCorpMap = (Map) DataAccessor.query(
						"creditCustomer.getCustomerInfoBycredit_id",
						context.contextMap, DataAccessor.RS_TYPE.MAP); 
			}
			outputMap.put("creditCustomerCorpMap", creditCustomerCorpMap);				
			outputMap.put("creditMap", creditMap);
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("insuresList", insuresList);
			outputMap.put("otherPriceList", otherPriceList);
			//
			insureCompanyList = (List<Map>) DataAccessor.query(
					"insuCompany.queryInsureCompanyListForSelect", null,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureCompanyList", insureCompanyList);
			//
			insureTypeList = (List<Map>) DataAccessor.query(
					"insureType.queryInsureTypeList", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureTypeList", insureTypeList);

			context.contextMap.put("dictionaryType", "支付方式");
			payWayList = (List) DataAccessor.query("creditCustomer.getItems",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dictionaryType", "租赁期满处理方式");
			dealWayList = (List) DataAccessor.query("creditCustomer.getItems",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dictionaryType", "保险购买方式");
			insureBuyWayList = (List) DataAccessor.query(
					"creditCustomer.getItems", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("payWayList", payWayList);
			outputMap.put("dealWayList", dealWayList);
			outputMap.put("insureBuyWayList", insureBuyWayList);
			outputMap.put("showFlag", 0);
			outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			outputMap.put("taxPlanList", DataAccessor.query("dataDictionary.queryDataDictionaryByValueAdded", null, DataAccessor.RS_TYPE.LIST));
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			suplList=(List)DictionaryUtil.getDictionary("供应商保证");
			outputMap.put("suplList", suplList);
			// irr month
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			outputMap.put("irrMonthPaylines", irrMonthPaylines);

			//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeList", feeList);	
			
			//费用设定明细 影响概算成本为1 不影响为0
			List feeSetListRZE=null;
			feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetListRZE", feeSetListRZE);
			List feeSetList=null;
			feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetList", feeSetList);
			//-------------------------------------------------------------------			
			
			//查询报告对应的供应商的授信信息
			List supperGrantList = (List) DataAccessor.query(
					"creditReportManage.selectSupperGrantInfo",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(supperGrantList.size()>0){
				supperGrantMap=(HashMap)supperGrantList.get(0);
				if(supperGrantMap.get("GRANT_PRICE")!=null){
					supperGrantMap.put("LAST_PRICE", SelectReportInfo.selectApplyLastPrice(Integer.parseInt(supperGrantMap.get("ID").toString())));
				}else{
					supperGrantMap.put("LAST_PRICE","0.00");
				}
			
				//抓取供应商交机前授信额度
				Map suplGrantMoneyMap;
				Map totalPayMoneyMap;
				context.contextMap.put("suppl_id", supperGrantMap.get("ID"));
				context.contextMap.put("SUPLNAME", supperGrantMap.get("SUPNAME"));
				suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
				if (suplGrantMoneyMap!=null){
					supperGrantMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));
	
					totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if (totalPayMoneyMap!=null){
						//判断授信的交机前拨款额度是否大于已用额度
						if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
							supperGrantMap.put("advance_machine", 0);
						}else{
							supperGrantMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
						}
					}
				}else{
					supperGrantMap.put("advance_machine", 0);
					supperGrantMap.put("advance_grant", 0);
				}
			}
			outputMap.put("supperGrantMap", supperGrantMap);
			//查询报告对应的客户的授信信息
			custGrantMap = (Map) DataAccessor.query(
					"creditReportManage.grantcustInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(custGrantMap!=null){

				if(custGrantMap.get("GRANT_PRICE")!=null){
					custGrantMap.put("GRANT_PRICE", custGrantMap.get("GRANT_PRICE").toString());
					custGrantMap.put("LAST_PRICE", CustomerCredit.getCustCredit(custGrantMap.get("CUST_ID")));
				}else{
					custGrantMap.put("GRANT_PRICE", "0");
					custGrantMap.put("LAST_PRICE", "0");
				}
			}
			outputMap.put("custGrantMap", custGrantMap);
			
			List appropiateList = (List) DataAccessor.query("creditReportManage.getAppropiateByCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("appropiateList", appropiateList);
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi copy by Yang Yun
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
			
			//Add by Michael 2012 12-20  增加费用来源
			outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告基本信息查看错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
				if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
					outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
					Output.jspOutput(outputMap, context, "/credit_vip/creditFrameCommit.jsp");
				}else{
					Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
				}
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	/**
	 * 提交资信到审批
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void commitCrditToWindControl(Context context) {
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			sqlMapper.update("creditReportManage.commitCreditToWind",context.contextMap);
			
			sqlMapper.commitTransaction() ;
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			context.errList.add("项目管理--现场调查报告担保人提交错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//插入系统日志记录 add by ShenQi
		Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
		String logType = "报告提交";
		String logTitle = "提交";

		String logCode = "";

		try {

			logCode = DataAccessor.query("creditReportManage.selectCreditCode", context.getContextMap(), RS_TYPE.OBJECT) +"";

		} catch (Exception e) {

			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		if(logCode == null){
			logCode = "";
		}	
		String memo="报告提交";
		int state = 1;
		Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
		Long otherId = null;



		BusinessLog.addBusinessLog(creditId, null, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		
		this.creditManage(context);
	}

	/**
	 * 提交资信到风控
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void examineCredit(Context context) {
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			context.contextMap.put("statee",1);
			sqlMapper.update("creditReportManage.examineCredit",
					context.contextMap);
			context.contextMap.put("AUDIT_STATE",Constants.AUDIT_STATE_0);//区域主管审批的
			sqlMapper.insert("creditReportManage.insertMemo",
					context.contextMap);
			
			//查询出该合同的客户经理
			Map creditById=(Map)DataAccessor.query("creditReportManage.selectSensor_IdById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			//查询出在数据字典中的案件狀況細項下的提案的主键
			String prvLog="提案与结果";
			String prvLogSun="提案";
			context.contextMap.put("logFlag", prvLog);
			context.contextMap.put("logName", prvLogSun);
			Map logTypeMap=(Map)DataAccessor.query("activitiesLog.logActlog_idNotStatus", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			
			//有这个客户经理建立的主档
			context.contextMap.put("sensoridBycredit", creditById.get("SENSOR_ID"));
			context.contextMap.put("custidBycredit", creditById.get("CUST_ID"));
			
			//如果该报告已经与主档有联系后就不更新下一个主档得credit
			Map logMaps=(Map)DataAccessor.query("activitiesLog.logFirstByCreditId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			if(logMaps!=null)
			{
				
				if(logMaps.size()>0)
				{
					Map entityMap=new HashMap();
					entityMap.put("id", DataUtil.longUtil(context.contextMap.get("s_employeeId")));
					entityMap.put("logName", prvLogSun);
					entityMap.put("actilog", logMaps.get("ACTILOG_ID"));
					sqlMapper.update("activitiesLog.updateCaseState",entityMap) ;
					
					entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
					entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
					sqlMapper.insert("activitiesLog.createLogByOther", entityMap);
				}
			}
			else
			{
				
				Map logMap=(Map)DataAccessor.query("activitiesLog.logFirst", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(logMap!=null)
				{
					if(logMap.size()>0)
					{
						
						Map entityMap=new HashMap();
						entityMap.put("id", DataUtil.longUtil(context.contextMap.get("s_employeeId")));
						entityMap.put("logName", prvLogSun);
						entityMap.put("actilog", logMap.get("ACTILOG_ID"));
						sqlMapper.update("activitiesLog.updateCaseState",entityMap) ;
						
						entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
						entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
						sqlMapper.insert("activitiesLog.createLogByOther", entityMap);
					}
					
				}
			}
			/* 2012-03-15 Yang Yun
			案件提交时自动共案*/
			this.projectsMerged(context.contextMap.get("credit_id").toString(), sqlMapper);
			sqlMapper.commitTransaction() ;
			//提交到审查
			riskAuditService.commitRisk(context.contextMap.get("credit_id").toString());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			context.errList.add("项目管理--报告审核提交错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
		Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
		String logType = "报告审批";
		String logTitle = "审批";

		String logCode = "";
		 
        		try {
        		    
        		    logCode = DataAccessor.query("creditReportManage.selectCreditCode", context.getContextMap(), RS_TYPE.OBJECT) +"";
        		    
        		} catch (Exception e) {
        		    
        		    e.printStackTrace();
        		    LogPrint.getLogStackTrace(e, logger);
        		}
        		
        		if(logCode == null){
        		    logCode = "";
        		}
		String crdate="";
		String lerze="";
		if(context.contextMap.get("CR_DATE")==null){
			crdate="空";
		}
		else{
			crdate=context.contextMap.get("CR_DATE").toString();
		}
		if(context.contextMap.get("LE_RZE")==null){
			lerze="空";
		}
		else{
			lerze=context.contextMap.get("LE_RZE").toString();
		}		
		String memo="报告审批通过  "+"成立日期："+crdate+"融资额："+lerze;
		int state = 1;
		Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
		Long otherId = null;
		
		
		
		BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));

		
		this.creditExamine(context);
	}

	/**
	 * 2012/03/15 Yang Yun
	 * 共案。
	 * @param credit_id
	 * @param sqlMapper
	 * @throws Exception
	 */
	private void projectsMerged(String credit_id, SqlMapClient sqlMapper) throws Exception{
		boolean mergeFlag = false;
		List<Map<String, Object>> projectInfoList = null;
		Map<String, Object> projectInfo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CREDIT_ID", credit_id);
		List<Map<String, Object>> resultByGroup = null;
		Map<String, Object> mergedProject = null;
		try {
			//查询当前案件的一些用到的比较的字段
			projectInfoList = sqlMapper.queryForList("creditReportManage.getProInfoForMerge", paramMap);
			if (projectInfoList != null && projectInfoList.size() > 0) {
				projectInfo = projectInfoList.get(0);
			} else {
				throw new Exception("共案失败，未找到提交的报告。");
			}
			//检查共案表中是否有本案（是否提交过），如有，则在共案表中作废本案记录，避免判断共案是出错。
			int result_flag = sqlMapper.update("creditReportManage.deleteBeforeMerge", paramMap);
			if (result_flag > 0) {
				logger.debug("该案子跑过共案的业务流程,并已作废历史。");
			} else {
				logger.debug("该案子第一次跑共案的业务流程。");
			}
			//去共案管理表找最近1个月的同一承租人 有无案件
			List<Map<String, Object>> resultByCust = sqlMapper.queryForList("creditReportManage.getResultByCust", projectInfo);
			if (resultByCust != null && resultByCust.size() > 0) {
				resultByGroup = sqlMapper.queryForList("creditReportManage.getResultByGroup", resultByCust.get(0));
			}
			if (resultByGroup != null && resultByGroup.size() > 0) {
				if ((Integer)resultByGroup.get(0).get("DAY_DIFF") > 60) {
					//不共案
					mergeFlag = false;
				} else {
					//可能共案，还须判断详细项
					mergedProject = (Map<String, Object>) sqlMapper.queryForObject("creditReportManage.getProInfoForMerge", resultByGroup.get(0));
					if (!mergedProject.get("THING_NAME").toString().equals(projectInfo.get("THING_NAME").toString())
							&& !mergedProject.get("DECP_NAME_CN").toString().equals(projectInfo.get("DECP_NAME_CN").toString())
							&& !mergedProject.get("EQUPMENT_ADDRESS").toString().substring(0, 2).equals(projectInfo.get("EQUPMENT_ADDRESS").toString().substring(0, 2))) {
						//不共案
						mergeFlag = false;
					} else {
						//共案
						mergeFlag = true;
					}
				}
			} else {
				mergeFlag = false;
			}
		} catch (Exception e) {
			mergeFlag = false;
		}
		Map<String, Object> paramForMerge = new HashMap<String, Object>();
		paramForMerge.put("CREDIT_ID", credit_id);
		paramForMerge.put("CUST_ID", projectInfo.get("CUST_ID"));
		paramForMerge.put("COMMIT_DATE", projectInfo.get("COMMIT_WIND_DATE"));
		if (mergeFlag) {
			//共案就新增一条相同的组号的记录
			paramForMerge.put("GROUP_CODE", resultByGroup.get(0).get("GROUP_CODE"));
			sqlMapper.insert("creditReportManage.insertMergeProject", paramForMerge);
			//判断有无访厂记录
			Integer visitCount = (Integer) sqlMapper.queryForObject("visitation.getHasVisitByCreditId", paramForMerge);
			if (visitCount == null || visitCount == 0) {
				//设置成“共案无需访厂”
				paramForMerge.put("NONE_VISIT_MEMO", "共案无需访厂");
				sqlMapper.insert("visitation.setVisitationForMerger", paramForMerge);
			}
		} else {
			//不共案就新增一条不同组号的记录
			paramForMerge.put("GROUP_CODE", "G" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			sqlMapper.insert("creditReportManage.insertMergeProject", paramForMerge);
		}
	}
	
	/**
	 * 提交资信到风控
	 * 
	 * @param context
	 */
	
	
	public void examineCredit2(Context context) {
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			context.contextMap.put("statee",2);
			sqlMapper.update("creditReportManage.examineCredit2",
					context.contextMap);
			context.contextMap.put("AUDIT_STATE",Constants.AUDIT_STATE_0);//区域主管审批的
			sqlMapper.insert("creditReportManage.insertMemo",
					context.contextMap);
			
			//查询出该合同的客户经理
			Map creditById=(Map)DataAccessor.query("creditReportManage.selectSensor_IdById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			//查询出在数据字典中的案件狀況細項下的提案的主键
			String prvLog="提案与结果";
			String prvLogSun="提案";
			context.contextMap.put("logFlag", prvLog);
			context.contextMap.put("logName", prvLogSun);
			Map logTypeMap=(Map)DataAccessor.query("activitiesLog.logActlog_idNotStatus", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			
			//有这个客户经理建立的主档
			context.contextMap.put("sensoridBycredit", creditById.get("SENSOR_ID"));
			context.contextMap.put("custidBycredit", creditById.get("CUST_ID"));
			
			//如果该报告已经与主档有联系后就不更新下一个主档得credit
			Map logMaps=(Map)DataAccessor.query("activitiesLog.logFirstByCreditId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			if(logMaps!=null)
			{
				
				if(logMaps.size()>0)
				{
					
					
					Map entityMap=new HashMap();
					entityMap.put("id", DataUtil.longUtil(context.contextMap.get("s_employeeId")));
					entityMap.put("logName", prvLogSun);
					entityMap.put("actilog", logMaps.get("ACTILOG_ID"));
					sqlMapper.update("activitiesLog.updateCaseState",entityMap) ;
					
					entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
					entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
					sqlMapper.insert("activitiesLog.createLogByOther", entityMap);
				}
			}
			else
			{
				
				Map logMap=(Map)DataAccessor.query("activitiesLog.logFirst", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(logMap!=null)
				{
					if(logMap.size()>0)
					{
						
						Map entityMap=new HashMap();
						entityMap.put("id", DataUtil.longUtil(context.contextMap.get("s_employeeId")));
						entityMap.put("logName", prvLogSun);
						entityMap.put("actilog", logMap.get("ACTILOG_ID"));
						sqlMapper.update("activitiesLog.updateCaseState",entityMap) ;
						
						entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
						entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
						sqlMapper.insert("activitiesLog.createLogByOther", entityMap);
					}
					
				}
			}
			
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			context.errList.add("项目管理--报告审核提交错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
		Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
		String logType = "报告审批";
		String logTitle = "审批";
		String logCode = "";
		 
        		try {
        		    
        		    logCode = DataAccessor.query("creditReportManage.selectCreditCode", context.getContextMap(), RS_TYPE.OBJECT) +"";
        		    
        		} catch (Exception e) {
        		    
        		    e.printStackTrace();
        		    LogPrint.getLogStackTrace(e, logger);
        		}
        		
        		if(logCode == null){
        		    logCode = "";
        		}
		
		
		String memo = " 报告审批未通过";
		int state = 1;
		Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
		Long otherId = null;
		
		BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		
	
		this.creditExamine(context);
	}

	/**
	 * 查看资料 pact page
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentFile(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List insorupd = new ArrayList();
		Map infor = new HashMap();
		try {
			insorupd = (List) DataAccessor.query("rentFile.selectRentFile", context.contextMap,DataAccessor.RS_TYPE.LIST);
			// 查询承租人资料和合同资料的信息
			infor = (Map) DataAccessor.query("rentFile.selectInfor",context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 查询担保人资料的信息
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报告管理--资料查询错误!请联系管理员") ;
		}

		if (errList.isEmpty()) {
			outputMap.put("insorupd", insorupd);
			outputMap.put("infor", infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("rentFileFlag", context.contextMap.get("rentFileFlag"));
			Output.jspOutput(outputMap, context, "/credit_vip/rentFile.jsp");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp");
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
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		context.contextMap.put("RETURN_DATE","");
		try {	
			sqlMapper.startTransaction() ;
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==0){
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthA")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idA"+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idA"+String.valueOf(i)));
					context.contextMap.put("file_count2",context.contextMap.get("FILE_COUNTA"+String.valueOf(i)));
					context.contextMap.put("copyfile_count2",context.contextMap.get("COPYFILE_COUNTA"+String.valueOf(i)));
					context.contextMap.put("memo2",context.contextMap.get("MEMOA"+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));			
					if (Integer.parseInt(context.contextMap.get("refd_idA"+String.valueOf(i)).toString())==0) {
						sqlMapper.insert("rentFile.insertReft", context.contextMap);
					} else {
						sqlMapper.update("rentFile.updateReft", context.contextMap);
					}								
				}				
			}
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==1){
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthB")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("file_count2",context.contextMap.get("FILE_COUNTB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("copyfile_count2",context.contextMap.get("COPYFILE_COUNTB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("memo2",context.contextMap.get("MEMOB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));			
					if (Integer.parseInt(context.contextMap.get("refd_idB"+String.valueOf(i)+String.valueOf(i)).toString())==0) {
						sqlMapper.insert("rentFile.insertReft", context.contextMap);
					} else {
						sqlMapper.update("rentFile.updateReft", context.contextMap);
					}								
				}				
			}
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==2){
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthC")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("file_count2",context.contextMap.get("FILE_COUNTC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("copyfile_count2",context.contextMap.get("COPYFILE_COUNTC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("memo2",context.contextMap.get("MEMOC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));			
					if (Integer.parseInt(context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)).toString())==0) {
						sqlMapper.insert("rentFile.insertReft", context.contextMap);
					} else {
						sqlMapper.insert("rentFile.updateReft", context.contextMap);
					}								
				}				
			}			
			sqlMapper.commitTransaction() ;
		}  catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("附件上传错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			InputStream in = fileItem.getInputStream();
			if (!fileItem.getName().equals("")) {
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem, sqlMapClient);
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
			if(DataUtil.intUtil(context.contextMap.get("rentFileFlag"))==1){
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReportVip.creditManage");	
			}else{
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReportVip.creditManage");
			}
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
		bootPath = this.getUploadPath("rentFile");
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
					contextMap.put("title", "上传的资料文件的附件");
					// 判断是修改还是添加
					String name = fileItem.getFieldName();
					String[] nameAll = name.split("@");
					context.contextMap.put("refd_id2", nameAll[0]);
					context.contextMap.put("refi_id2", nameAll[1]);
					context.contextMap.put("prcd_id2", context.contextMap
							.get("prcd_id"));
					syupId = (Long) sqlMapClient.insert(
							"rentFile.insertFileForUp", contextMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					// fileItem.getOutputStream().flush();
					// fileItem.getOutputStream().close();
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
		String bootPath = this.getUploadPath("rentFile");
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

	/**
	 * 添加提醒日志 pact page
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id = 0;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String fileValue = (String) context.contextMap.get("ids1");
			String[] ids = fileValue.split("@");

			context.contextMap.put("logTime", context.contextMap
					.get("logTime1"));
			context.contextMap.put("logMemo", context.contextMap
					.get("logMemo1"));
			context.contextMap.put("prcd_id", context.contextMap
					.get("prcd_idLog1"));
			trfl_id = (Long) sqlMapper.insert("rentFile.insertLog",
					context.contextMap);
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("id", ids[i]);
				context.contextMap.put("trfl_id", trfl_id);
				sqlMapper.insert("rentFile.insertFile2Log",
						context.contextMap);
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditReportVip.creditManage");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 添加提醒日志 pact page
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog2(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id = 0;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String fileValue = (String) context.contextMap.get("ids2");
			String[] ids = fileValue.split("@");

			context.contextMap.put("logTime", context.contextMap
					.get("logTime2"));
			context.contextMap.put("logMemo", context.contextMap
					.get("logMemo2"));
			context.contextMap.put("prcd_id", context.contextMap
					.get("prcd_idLog2"));
			trfl_id = (Long)sqlMapper.insert("rentFile.insertLog",
					context.contextMap);
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("id", ids[i]);
				context.contextMap.put("trfl_id", trfl_id);
				sqlMapper.insert("rentFile.insertFile2Log",
						context.contextMap);
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
				"defaultDispatcher?__action=creditReportVip.creditManage");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 添加提醒日志 pact page
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog3(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id = 0;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String fileValue = (String) context.contextMap.get("ids3");
			String[] ids = fileValue.split("@");

			context.contextMap.put("logTime", context.contextMap
					.get("logTime3"));
			context.contextMap.put("logMemo", context.contextMap
					.get("logMemo3"));
			context.contextMap.put("prcd_id", context.contextMap
					.get("prcd_idLog3"));
			trfl_id = (Long)sqlMapper.insert("rentFile.insertLog",
					context.contextMap);
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("id", ids[i]);
				context.contextMap.put("trfl_id", trfl_id);
				sqlMapper.insert("rentFile.insertFile2Log",
						context.contextMap);
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
			"defaultDispatcher?__action=creditReportVip.creditManage");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	// public void createEquipment(Context context) {
	// String[] types = context.request.getParameterValues("type");
	// System.out.println("****************" + types.length);
	// for (int i = 0; i < types.length; i++) {
	// System.out.println(types[i]);
	// }
	// }
	/**
	 * 查看风控会议纪要
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void watchRiskControlMemo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List<Map> riskList = null;
		List<Map> memoList = null;
		List riskMemo = null;
		try {
			riskList = (List<Map>) DataAccessor.query(
					"creditReportManage.queryRisks", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			for (int x = 0; x < riskList.size(); x++) {
				int prcId = DataUtil.intUtil(riskList.get(x).get("PRC_ID"));
				context.contextMap.put("prcId", prcId);
				riskMemo = (List) DataAccessor.query(
						"creditReportManage.queryRiskMemos",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				riskList.get(x).put("riskMemo", riskMemo);
			}
			memoList = (List) DataAccessor.query(
					"creditReportManage.selectMemo", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查看风控会议纪要错误!请联系管理员");
		}
		outputMap.put("riskList", riskList);
		outputMap.put("memoList", memoList);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/risk_audit/riskAuditView.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	
	
	
	@Override
	protected boolean preExecute(String action, Context context) {
	     
	    return super.preExecute(action, context);
	}
	
	
	

	/**
	 * 查看全部评审意见
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void memos(Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;

		List<Map> memoList = null;

		if (errList.isEmpty()) {

			try {

				outputMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				memoList = (List) DataAccessor.query(
						"creditReportManage.selectMemo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("memoList", memoList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("查看全部审核意见错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/credit_vip/memos.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "error.jsp") ;
		}
	}

	/**
	 * 
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void lockManager(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List lockList = null;
		try {
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询锁码方式错误!请联系管理员");
		}
		outputMap.put("lockList", lockList);
			Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改区域主管
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateClerkName(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		try {
			DataAccessor.execute("creditReportManage.updateClerkName",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改区域主管错误!请联系管理员");
		}
		Output.jsonOutput(outputMap, context);
	}	
	
	/**
	 * 修改客户来源
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateCustComeName(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		try {
			DataAccessor.execute("creditReportManage.updateCustComeName",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改客户来源错误!请联系管理员");
		}
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改客户经理
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateSensorName(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		try {
			DataAccessor.execute("creditReportManage.updateSensorName",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改客户经理错误!请联系管理员");
		}
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改报告类型
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateTypeName(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		try {
			DataAccessor.execute("creditReportManage.updateTypeName",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改报告类型错误!请联系管理员");
		}
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 获取没有上传的必传项
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void getunUp(Context context) {
		Map outputMap = new HashMap();
		List unUpList = null;
		try {
			context.contextMap.put("A", "承租人资料");
			context.contextMap.put("B", "担保人资料");
			//context.contextMap.put("C", "合同资料");
			
			unUpList = (List) DataAccessor.query("rentFile.getunUp", context.contextMap,DataAccessor.RS_TYPE.LIST);
			/* 2011/12/31 Yang Yun 增加活动日志 验证。  -------------------- */
			/*context.contextMap.put("type1", "1");
			context.contextMap.put("type2", "2");
			Map<String, Object> activitiesLog = (Map<String, Object>) DataAccessor.query("rentFile.getunUpForActive", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if ("false".equals(activitiesLog.get("RESULT"))) {
				Map<String, Object> activitiesLogMap = new HashMap<String, Object>();
				activitiesLogMap.put("FILE_TYPE", "活动日志");
				activitiesLogMap.put("FILE_NAME", "案件状况");
				unUpList.add(activitiesLogMap);
			}*/
			/* ---------------------------------------------------------- */
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("unUpList", unUpList);
		Output.jsonOutput(outputMap, context);
	}
	
	
	public void getunUpContract(Context context) {
		Map outputMap = new HashMap();
		List unUpList = null;
		try {
			context.contextMap.put("C", "合同资料");
			
			//查找该报告的合同类型
//			Map creditMap=new HashMap();
//			creditMap=(HashMap)DataAccessor.query("rentFile.getRentType", context.contextMap,DataAccessor.RS_TYPE.MAP);
//			
//			context.contextMap.put("CONTRACT_TYPE", creditMap.get("CONTRACT_TYPE"));
//			unUpList = (List) DataAccessor.query("rentFile.getunUpsss", context.contextMap,DataAccessor.RS_TYPE.LIST);
			Map creditMap=new HashMap();
			List creditMaps=null;
			creditMaps=(List)DataAccessor.query("rentFile.getisnotnull", context.contextMap,DataAccessor.RS_TYPE.LIST);
			creditMap=(HashMap)DataAccessor.query("rentFile.getRentType", context.contextMap,DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("CONTRACT_TYPE", creditMap.get("CONTRACT_TYPE"));
			creditMap.put("C", "合同资料");
			if(creditMaps.size()!=0){
				unUpList = (List) DataAccessor.query("rentFile.getunUpsss_no", creditMap,DataAccessor.RS_TYPE.LIST);
			}else{
				unUpList = (List) DataAccessor.query("rentFile.getunUpsss_yes", context.contextMap,DataAccessor.RS_TYPE.LIST);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("unUpList", unUpList);
		Output.jsonOutput(outputMap, context);
	}
	public void getLastGrantPrice(Context context) {
		Map outputMap = new HashMap();		
		String alertInfo="success";
		try {
			List grantPriceList = (List) DataAccessor.query("creditReportManage.getLastGrantPrice", context.contextMap,DataAccessor.RS_TYPE.LIST);
			if(grantPriceList.size()>0){
				double sumlastprice=0;
				for(int i=0;i<grantPriceList.size();i++){					
					Map grantPrice =(HashMap)grantPriceList.get(i);
					//if(grantPrice.get("SUPL_TRUE")!=null&&Integer.parseInt(grantPrice.get("SUPL_TRUE").toString())!=4){
						if(grantPrice.get("GRANT_PRICE")!=null){
							Double cust_lastprice=CustomerCredit.getCustCredit(grantPrice.get("CUST_ID"));
							if(cust_lastprice-Double.parseDouble(grantPrice.get("LEASE_TOPRIC")==null?"0":grantPrice.get("LEASE_TOPRIC").toString())<0){
								alertInfo="客户的授信余额小于融资租赁总价值！";
							}
						}
					//}				
					//if(grantPrice.get("SUPL_TRUE")!=null&&Integer.parseInt(grantPrice.get("SUPL_TRUE").toString())!=4&&grantPrice.get("REAL_LAST_PRICE")!=null){
						//sumlastprice+=Double.parseDouble(grantPrice.get("REAL_LAST_PRICE").toString());
						if(grantPrice.get("SUPPER_ID")!=null&&grantPrice.get("SUPPER_REPEAT_CREDIT")!=null){
							Object supplier_lastprice=SelectReportInfo.selectApplyLastPrice(Integer.parseInt(grantPrice.get("SUPPER_ID").toString()));
							if(Double.parseDouble(supplier_lastprice==null?"0":supplier_lastprice.toString())-Double.parseDouble(grantPrice.get("APPLYLEASE_TOPRIC")==null?"0":grantPrice.get("APPLYLEASE_TOPRIC").toString())<0){
								alertInfo="供应商的授信余额小于融资租赁总价值对应的比例！";
							}
						}
					//}				
				}
				/*if(((HashMap)grantPriceList.get(0)).get("SUPL_TRUE")!=null&&Integer.parseInt(((HashMap)grantPriceList.get(0)).get("SUPL_TRUE").toString())!=4&&((HashMap)grantPriceList.get(0)).get("LEASE_TOPRIC")!=null){
					if(sumlastprice<Double.parseDouble(((HashMap)grantPriceList.get(0)).get("LEASE_TOPRIC").toString())){
						alertInfo="供应商的授信余额小于融资租赁总价值！";
					}
				}*/
			}
			//根据报告查询到所有的担保人
			List VoucherList= (List) DataAccessor.query("creditReportManage.getAllVouchers", context.contextMap,DataAccessor.RS_TYPE.LIST);
			if(VoucherList.size()>0){
				for(int i=0;i<VoucherList.size();i++){
					HashMap voucherMap=(HashMap)VoucherList.get(i);
					if(voucherMap.get("GRANT_PRICE")!=null){
						double vouch_lastprice=CreditVouchManage.VOUCHPLANBYLASTPRICE(voucherMap.get("VOUCHNAME").toString(),voucherMap.get("VOUCHCODE").toString(),Integer.parseInt(voucherMap.get("TYPE").toString()));
						if(vouch_lastprice-Double.parseDouble(voucherMap.get("LEASE_TOPRIC")==null?"0":voucherMap.get("LEASE_TOPRIC").toString())<0){
							alertInfo="担保人的授信余额小于融资租赁总价值！";
						}
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("alertInfo", alertInfo);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改省、市、区
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updatePrivCityArea(Context context) {
		try {
			DataAccessor.execute("creditReportManage.updatePrivCityArea",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}	
	
	/**
	 * 弹出财务报表层 0411 胡昭卿
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void managePactCorp(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		List<Map> creditcorpreport = null;
		Map payMoneyMap=null;
		if(errList.isEmpty()){	
		
			try {
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				creditcorpreport = (List<Map>) DataAccessor.query("creditPriorRecords.getCorpReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(creditcorpreport!=null&&creditcorpreport.size()>0){
					outputMap.put("creditcorpreport", creditcorpreport);
				}
				payMoneyMap=(HashMap)DataAccessor.query("exportContractPdf.queryTwoContractByReportId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(payMoneyMap!=null){
					outputMap.put("payMoneyMap", payMoneyMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("弹出财务报表错误!请联系管理员");
			}
		
		}
		
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/credit_vip/pactCorp.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		
	}
	
	public void getApplyGrantBySupperId(Context context){
		Map supperGrantMap=new HashMap();
		Map outputMap = new HashMap();
		try {		
			//查询报告对应的供应商的授信信息
			//System.out.println(context.contextMap.get("supperid").toString()+"================");
			supperGrantMap = (Map) DataAccessor.query("creditReportManage.selectSupperGrantInfoBySupperId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//System.out.println(supperGrantMap.get("SUPNAME").toString()+"================");
				if(supperGrantMap.get("GRANT_PRICE")!=null){
					supperGrantMap.put("GRANT_PRICE", "￥"+updateMon(supperGrantMap.get("GRANT_PRICE")));
				}else{
					supperGrantMap.put("GRANT_PRICE", "￥0.00");
				}
				if(supperGrantMap.get("GRANT_PRICE")!=null){
					Object last_price=(Object)SelectReportInfo.selectApplyLastPrice(Integer.parseInt(supperGrantMap.get("ID").toString()));
					if(last_price!=null){
						supperGrantMap.put("LAST_PRICE", "￥"+updateMon(last_price));
					}else{
						supperGrantMap.put("LAST_PRICE", "￥0.00");
					}
					
				}else{
					supperGrantMap.put("LAST_PRICE", "￥0.00");
				}
				if(supperGrantMap.get("UNION_GRANT_PRICE")!=null){
					supperGrantMap.put("UNION_GRANT_PRICE", "￥"+updateMon(supperGrantMap.get("UNION_GRANT_PRICE")));
				}else{
					supperGrantMap.put("UNION_GRANT_PRICE", "￥0.00");
				}
				
				//抓取供应商交机前授信额度
				context.contextMap.put("suppl_id", supperGrantMap.get("ID"));
				context.contextMap.put("SUPLNAME", supperGrantMap.get("SUPNAME"));
				Map suplGrantMoneyMap;
				Map totalPayMoneyMap;
				
				suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
				if (suplGrantMoneyMap!=null){
					supperGrantMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));

					totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if (totalPayMoneyMap!=null){
						//判断授信的交机前拨款额度是否大于已用额度
						if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
							supperGrantMap.put("advance_machine", 0);
						}else{
							supperGrantMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
						}
					}
				}else{
					supperGrantMap.put("advance_machine", 0);
					supperGrantMap.put("advance_grant", 0);
				}
				
				outputMap.put("supperGrantMap", supperGrantMap);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
				Output.jsonOutput(outputMap, context);
		}

	}
	/**  财务格式  0.00 */
	private String updateMon(Object content) {
	    String str="";
	    
	    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
		
		str+="0.00";
		return str;
		
	    }
	    else{
		
		DecimalFormat df1 = new DecimalFormat("#,###.00"); 
		
		str+=df1.format(Double.parseDouble(content.toString()));
		return str;
	    }	
	}
	
	//查询所有介绍人
	@SuppressWarnings("unchecked")
	public void getAllSponsor(Context context){
		
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List errList = context.errList ;

		try {
			dw = (DataWrap) DataAccessor.query(
					"creditReportManage.getAllSponsor", context.contextMap,
					DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询所有介绍人错误!请联系管理员");
		}
		
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/sponsorManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//查询介绍人所介绍的公司
	@SuppressWarnings("unchecked")
	public void getSponsorByName(Context context){
		
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List errList = context.errList ;

		try {
			dw = (DataWrap) DataAccessor.query(
					"creditReportManage.getSponsorByName", context.contextMap,
					DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询介绍人公司错误!请联系管理员");
		}
		
		outputMap.put("dw", dw);
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/showSponsorCrop.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	

	//客户案况表
	public void queryCreditByCust(Context context)
	{
		List errorList = context.errList;
		HashMap  outputMap  = new HashMap();
		ArrayList   empInfo = new ArrayList();	 
		ArrayList   empOther = new ArrayList();	 
		ArrayList   company_count = new ArrayList();	//公司 数
		ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
		ArrayList   company_count1 = new ArrayList();	//公司 数
		
		ArrayList   actLog = new ArrayList();	 //客户案况
		
		List<Map<String,Object>> brandList=null;//供应商
		if(errorList.isEmpty()){
		    
		    try {
				context.contextMap.put("type1", "融资租赁合同类型");
				
				//查询条件
				
				/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
				Map<String, Object> rsMap = null;
				context.contextMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if (rsMap == null || rsMap.get("NODE") == null) {
					throw new Exception("Session lost");
				}
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				/*--------------------------------------------------------------------------- */
				
				outputMap.put("DATE_TYPE",context.contextMap.get("DATE_TYPE"));
				outputMap.put("BEGIN_DATE",context.contextMap.get("BEGIN_DATE"));
				outputMap.put("END_DATE",context.contextMap.get("END_DATE"));
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("creditReportManage.queryCreditByCust",context.contextMap);
				
//	        		company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("creditReportManage.getCountByComp", context.contextMap);
	        		
	        		company_count1 =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("creditReportManage.getCountByComp1", context.contextMap);
	        		
	        		//客户案况
	        		actLog= (ArrayList) DataAccessor.getSession().queryForList("creditReportManage.queryActLog",context.contextMap);
	        		
	        		brandList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getBrand",null,RS_TYPE.LIST);
	        		
	        		for(int i=0;empInfo!=null&&i<empInfo.size();i++) {
	        			boolean flag=true;
	        			for(int j=0;brandList!=null&&j<brandList.size();j++) {
	        				if(String.valueOf(((Map<String,Object>)empInfo.get(i)).get("CREDIT_ID")).equals(String.valueOf(brandList.get(j).get("CREDIT_ID")))) {
	        					if(flag) {
	        						((Map<String,Object>)empInfo.get(i)).put("BRAND",brandList.get(j).get("BRAND"));
	        						flag=false;
	        					} else {
	        						((Map<String,Object>)empInfo.get(i)).put("BRAND",(String)((Map<String,Object>)empInfo.get(i)).get("BRAND")+"<b>,</b><br>"+brandList.get(j).get("BRAND"));
	        					}
	        				}
	        			}
	        		}
		    } catch (Exception e) {
			
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		    }
		}
		
		if(errorList.isEmpty()){
			
			if(context.contextMap.get("creditcontext")!=null && !"".equals(context.contextMap.get("creditcontext")))
			{
				outputMap.put("creditcontext", context.contextMap.get("creditcontext"));
			}
			if(context.contextMap.get("sensorId")!=null && !"".equals(context.contextMap.get("sensorId")))
			{
				outputMap.put("sensorId", context.contextMap.get("sensorId"));
			}
			if(context.contextMap.get("decp_name")!=null && !"".equals(context.contextMap.get("decp_name")))
			{
				outputMap.put("decp_name", context.contextMap.get("decp_name"));
			}
			if(context.contextMap.get("state")!=null && !"".equals(context.contextMap.get("state")))
			{
				outputMap.put("state", context.contextMap.get("state"));
			}
			
		    outputMap.put("comp", empInfo);
		    outputMap.put("company_count", company_count);
		    outputMap.put("company_count1", company_count1);
		    outputMap.put("actLog", actLog);
		    Output.jspOutput(outputMap, context, "/credit_vip/creditCustReport.jsp");
		}
	}
	
	
	public void queryCreditByCustExcel(Context context)
	{
		List errorList = context.errList;
		HashMap  outputMap  = new HashMap();
		ArrayList   empInfo = new ArrayList();	 
		ArrayList   empOther = new ArrayList();	 
		ArrayList   company_count = new ArrayList();	//公司 数
		ArrayList   user_count = new ArrayList();	//公司 的旗下员工数
		if(errorList.isEmpty()){
		    
		    try {
		    	context.contextMap.put("type1", "融资租赁合同类型");
				
				String Query_DATE=context.request.getParameter("Query_DATE");
				String decp_id=context.request.getParameter("decp_Id");
				String Id=context.request.getParameter("Id");
				
				
					empInfo =   (ArrayList) DataAccessor.getSession().queryForList("creditReportManage.queryCreditByCust",context.contextMap);
				
	        		company_count =   (ArrayList)(ArrayList) DataAccessor.getSession().queryForList("creditReportManage.getCountByComp", context.contextMap);
	        		
		    } catch (SQLException e) {
			
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		    }
		}
		
		if(errorList.isEmpty()){
			if(context.contextMap.get("creditcontext")!=null && !"".equals(context.contextMap.get("creditcontext")))
			{
				outputMap.put("creditcontext", context.contextMap.get("creditcontext"));
			}
			if(context.contextMap.get("sensorId")!=null && !"".equals(context.contextMap.get("sensorId")))
			{
				outputMap.put("sensorId", context.contextMap.get("sensorId"));
			}
			if(context.contextMap.get("decp_name")!=null && !"".equals(context.contextMap.get("decp_name")))
			{
				outputMap.put("decp_name", context.contextMap.get("decp_name"));
			}
			if(context.contextMap.get("state")!=null && !"".equals(context.contextMap.get("state")))
			{
				outputMap.put("state", context.contextMap.get("state"));
			}
			CreditReportExcel excel=new CreditReportExcel();
		   excel.creditReportExcelJoin(empInfo, company_count, context);
		}
	}
	
	public void updateCreditDecpId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		boolean flag=false;
		try {			
			if(!flag){
				DataAccessor.execute("creditReportManage.updateCreditDecpId", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				flag=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--修改公司代号信息错误!请联系管理员");
		}
		if(flag){
			outputMap.put("alertInfo", "公司代号修改成功！");
		}else{
			outputMap.put("alertInfo", "公司代号修改失败！");
		}
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 于秋辰
	 * 报告提交时计算授信余额是否足够  并提示
	 * @param context
	 */
	public void getIsCanSubmit(Context context){
		Map outputMap = new HashMap();
		Integer creditCustInfoCount = (Integer) baseService.queryForObj("creditReportManage.getCreditCustInfoCount", context.contextMap);
		outputMap.put("custCount", creditCustInfoCount);
		Output.jsonOutput(outputMap, context);
	}
	
	//报告撤销功能 add by ShenQi see mantis 391
	@Transactional(rollbackFor=Exception.class)
	public void cancelCredit(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......cancelCredit";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List errList=context.errList;	
		Map outputMap = new HashMap();
		
		SqlMapClient sqlMapper=null;
		try {
			sqlMapper=DataAccessor.getSession();
			sqlMapper.startTransaction();
			
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id_cancel"));
			
			context.contextMap.put("CODE",-1);//报告撤销的CODE是-1
			sqlMapper.update("creditReportManage.cancelCreditByCreditId",context.contextMap);
			
			context.contextMap.put("CASESTATE","单位主管自行退件");
			
			sqlMapper.update("creditReportManage.cancelCaseByCreditId",context.contextMap);
			
			context.contextMap.put("creditId",DataUtil.longUtil(context.contextMap.get("CREDIT_ID")));
			context.contextMap.put("contractId",0);
			context.contextMap.put("logType","报告管理");
			context.contextMap.put("logTitle","报告撤销");
			context.contextMap.put("state",1);
			context.contextMap.put("memo",context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")"+context.contextMap.get("cancelRemark"));
			context.contextMap.put("userId",DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()));
			
			sqlMapper.insert("sysBusinessLog.add",context.contextMap);
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("撤销报告错误!请联系管理员") ;
			}
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(errList.isEmpty()) {
			this.creditManage(context);//跳转页面
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	//报告重新启用功能add by ShenQi 
	@Transactional(rollbackFor=Exception.class)
	public void enableCredit(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......enableCredit";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List errList=context.errList;	
		Map outputMap = new HashMap();
		
		SqlMapClient sqlMapper=null;
		try {
			sqlMapper=DataAccessor.getSession();
			sqlMapper.startTransaction();
			
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			
			context.contextMap.put("CODE",0);//报告启用的CODE是0
			sqlMapper.update("creditReportManage.cancelCreditByCreditId",context.contextMap);
			
			context.contextMap.put("CASESTATE","资料中");
			sqlMapper.update("creditReportManage.cancelCaseByCreditId",context.contextMap);
			
			context.contextMap.put("creditId",DataUtil.longUtil(context.contextMap.get("CREDIT_ID")));
			context.contextMap.put("contractId",0);
			context.contextMap.put("logType","报告管理");
			context.contextMap.put("logTitle","报告启用");
			context.contextMap.put("state",1);
			context.contextMap.put("memo",context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")");
			context.contextMap.put("userId",DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()));
			
			sqlMapper.insert("sysBusinessLog.add",context.contextMap);
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("启用报告错误!请联系管理员") ;
			}
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		if(errList.isEmpty()) {
			this.creditManage(context);//跳转页面
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	/**
	 * 2012/05/03 Yang Yun
	 * 维护访厂日期
	 */
	public void updateVisitDate(Context context){
		String visit_date = (String) context.contextMap.get("visit_date");
		String visit_credit_id = (String) context.contextMap.get("visit_credit_id");
		boolean flag = false;
		if (StringUtils.isEmpty(visit_date) || StringUtils.isEmpty(visit_credit_id)) {
			Output.jsonFlageOutput(flag, context);
			return;
		}
		System.out.println(visit_date + "<<===>>" + visit_credit_id);
		try {
			DataAccessor.execute("creditReportManage.updateVisitDate", context.contextMap, OPERATION_TYPE.UPDATE);
			flag = true;
			BusinessLog.addBusinessLogWithIp(Long.parseLong(visit_credit_id), null, "现场调查报告", 
					"维护访厂", null, "修改业务端访厂时间[" + visit_date + "]", 
					1, Long.parseLong(context.contextMap.get("s_employeeId").toString()), 
					null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	//加入转移客户经理 add by ShenQi
	public void getSensorList(Context context) {
		
		List<Map> sensorList=null;
		try {
			context.contextMap.put("dic_type", "员工职位");
			context.contextMap.put("dic_flag1", "业务员");
			context.contextMap.put("dic_flag2", "业务助理");
			sensorList=(List<Map>)DataAccessor.query("creditReportManage.getSensonWithOutself",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonArrayOutput(sensorList,context);
	}
	
	public void updateSensorId(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......updateSensorId";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		boolean flag=false;
		String creditId=(String)context.contextMap.get("CREDIT_ID");
		String sensorId=(String)context.contextMap.get("sensor_id");
		String sensorName=(String)context.contextMap.get("sensor");
		String processer=(String)context.contextMap.get("processer_id");
		String processerName=(String)context.contextMap.get("processer");
		String previousSensorId=(String)context.contextMap.get("PREVIOUS_SENSOR_ID");
		String previousSensorName=(String)context.contextMap.get("PREVIOUS_SENSOR_NAME");
		String from_processer = (String) context.contextMap.get("FROM_PROCESSER");
		if(StringUtils.isEmpty(creditId)) {
			Output.jsonFlageOutput(flag,context);
			return;
		}
		
		try {
			context.contextMap.put("SENSOR_ID",sensorId);
			context.contextMap.put("PROCESSER_ID",processer);
			if (StringUtils.isEmpty(sensorId) && StringUtils.isEmpty(processer)) {
				throw new Exception("没有更新");
			}
			DataAccessor.execute("creditReportManage.updateSensorId",context.contextMap,OPERATION_TYPE.UPDATE);
			//插入日志
			String creditCode=(String)DataAccessor.query("creditReportManage.getCreditCode",context.contextMap,RS_TYPE.OBJECT);
			
			BusinessLog.addBusinessLogWithIp(Long.parseLong(creditId),null,"报告管理", "转移客户经理",creditCode,
					(StringUtils.isEmpty(sensorId) ? "" : "协办业务从"+previousSensorName+"("+previousSensorId+")"+"转移到"+sensorName+"("+sensorId+");") +
					(StringUtils.isEmpty(processer) ? "" : "服务课人员从"+from_processer+"转移到"+processerName+"("+processer+");"),
					1,Long.parseLong(context.contextMap.get("s_employeeId").toString()), 
					null,(String)context.contextMap.get("IP"));
			
			flag=true;
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		Output.jsonFlageOutput(flag,context);
	}
	
	/**
	 * 添加方案
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createReScheme(Context context) {
		
		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		
		try {

			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

		
			sqlMapper.update("creditReportManage.upateReCreditScheme",context.contextMap);
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告提交到主管错误错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		
		if(errList.isEmpty()){
			
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorpVip.selectCreditCustomerCorpForShow&credit_id="
					+ context.contextMap.get("credit_id")+"&examineFlag="+context.contextMap.get("examineFlag")+"&commit_flag="+context.contextMap.get("commit_flag"));
			
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void selectCreditForCommit(Context context) {
		Map outputMap = new HashMap();
		Map creditMap = null;
		Map schemeMap = null;
		List equipmentsList = null;
		List insuresList = null;
		List otherPriceList = null;
		List payWayList = null;
		List dealWayList = null;
		List insureBuyWayList = null;
		List insureCompanyList = null;
		List insureTypeList = null;
		List lockList = null;		
		List clerkList = null;		
		List sensorList = null;	
		List provinces = null;
		List citys = null;
		List area = null;
		List suplList=null;
		
		List errList = context.errList ;
		//供应商的授信信息 胡昭卿加
		Map supperGrantMap=new HashMap();
		//客户的授信信息 胡昭卿加
		Map custGrantMap=null;
		
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			schemeMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditScheme",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			
			equipmentsList = (List) DataAccessor.query(
					"creditReportManage.selectCreditEquipment",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			insuresList = (List) DataAccessor.query(
					"creditReportManage.selectCreditInsure",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			otherPriceList = (List) DataAccessor.query(
					"creditReportManage.selectCreditOtherPrice",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			//客户来源
			outputMap.put("customerCome", DictionaryUtil.getDictionary("客户来源"));		
			//省
			provinces = (List) DataAccessor.query("area.getProvinces",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("provinces", provinces);
			//市
			context.contextMap.put("provinceId", creditMap.get("PROVINCE_ID"));
			citys = (List) DataAccessor.query("area.getCitysByProvinceId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("citys", citys);
			//区
			context.contextMap.put("cityId", creditMap.get("CITY_ID"));
			area = (List) DataAccessor.query("area.getAreaByCityId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("area", area);
			//报告类型
			outputMap.put("creditTypes", DictionaryUtil.getDictionary("尽职调查报告类型"));
			//
			outputMap.put("contractType", DictionaryUtil.getDictionary("融资租赁合同类型")); 
			//区域主管
			context.contextMap.put("jobType", "员工职位");
			context.contextMap.put("jobName","业务主管");
			context.contextMap.put("jobName2","");
			clerkList = (List) DataAccessor.query("employee.getEmpForJob",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("clerkList", clerkList);
			//客户经理
			context.contextMap.put("jobType", "员工职位");
			context.contextMap.put("jobName","业务员");
			context.contextMap.put("jobName2","业务助理");
			sensorList = (List) DataAccessor.query("employee.getEmpForJob",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			outputMap.put("sensorList", sensorList);
			
			outputMap.put("lockList", lockList);
			outputMap.put("creditMap", creditMap);
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("insuresList", insuresList);
			outputMap.put("otherPriceList", otherPriceList);

			//
			insureCompanyList = (List<Map>) DataAccessor.query(
					"insuCompany.queryInsureCompanyListForSelect", null,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureCompanyList", insureCompanyList);
			//
			insureTypeList = (List<Map>) DataAccessor.query(
					"insureType.queryInsureTypeList", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureTypeList", insureTypeList);
			//
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));

			context.contextMap.put("dictionaryType", "支付方式");
			payWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("dictionaryType", "租赁期满处理方式");
			dealWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("dictionaryType", "保险购买方式");
			insureBuyWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap,DataAccessor.RS_TYPE.LIST);
			
			suplList=(List)DictionaryUtil.getDictionary("供应商保证");
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			outputMap.put("suplList", suplList);
			outputMap.put("payWayList", payWayList);
			outputMap.put("dealWayList", dealWayList);
			outputMap.put("insureBuyWayList", insureBuyWayList);
			outputMap.put("showFlag", 0);
			//Add by Michael 2012 09-21 增加税费测算方案
			outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
			// irr month
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			outputMap.put("irrMonthPaylines", irrMonthPaylines);
			
			//Add by Michael 2012 1/9 报告修改进入时要重新计算TR-----------------------------------
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
			if(schemeMap!=null&&"4".equals(schemeMap.get("TAX_PLAN_CODE"))) {
				schemeMap.put("payList",rePaylineList);
				schemeMap.put("PLEDGE_AVE_PRICE",schemeMap.get("PLEDGE_AVE_PRICE")==null||"".equals(schemeMap.get("PLEDGE_AVE_PRICE"))?0:schemeMap.get("PLEDGE_AVE_PRICE"));
				schemeMap.put("PLEDGE_BACK_PRICE",schemeMap.get("PLEDGE_BACK_PRICE")==null||"".equals(schemeMap.get("PLEDGE_BACK_PRICE"))?"0":schemeMap.get("PLEDGE_BACK_PRICE"));
				schemeMap.put("MAGR_FEE",schemeMap.get("MANAGEMENT_FEE")==null||"".equals(schemeMap.get("MANAGEMENT_FEE"))?0:schemeMap.get("MANAGEMENT_FEE"));
				schemeMap.put("PLEDGE_LAST_PERIOD",schemeMap.get("PLEDGE_LAST_PERIOD")==null||"".equals(schemeMap.get("PLEDGE_LAST_PERIOD"))?0:schemeMap.get("PLEDGE_LAST_PERIOD"));
			}
			Map paylist = null;
			if (schemeMap != null) {
				//Add by Michael 2012 01/29 在方案里增加合同类型
				schemeMap.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
				//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
				double totalFeeSet=0.0d;
				
				if("2".equals(schemeMap.get("TAX_PLAN_CODE"))){
					List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(Map map:listTotalFeeSet){
						totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					}	
				}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))||"4".equals(schemeMap.get("TAX_PLAN_CODE"))){
					totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				
				schemeMap.put("FEESET_TOTAL", totalFeeSet);
				//-----------------------------------------------------------------------------
				
				schemeMap.put("TOTAL_PRICE", schemeMap.get("LEASE_TOPRIC"));
				schemeMap.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
				schemeMap.put("LEASE_TERM", schemeMap.get("LEASE_COURSE"));
				// 
				if (irrMonthPaylines.size() > 0) {
					// 如果应付租金存在，则以应付租金的方式计算
					paylist = StartPayService.createCreditPaylistIRR(schemeMap,rePaylineList,irrMonthPaylines);
				} else {
					// 如果应付租金不存在，则以年利率(合同利率)的方式计算
					paylist = StartPayService.createCreditPaylist(schemeMap,new ArrayList<Map>());
				}
			}
			outputMap.put("paylist", paylist);
			//-----------------------------------------------------------------------------------------
	
			//Add by Michael 2012 01/14 For 方案费用查询 影响概算成本为1 不影响为0
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeList", feeList);	
			
			//费用设定明细 影响概算成本为1 不影响为0
			List feeSetListRZE=null;
			feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetListRZE", feeSetListRZE);
			List feeSetList=null;
			feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetList", feeSetList);			
			
			//查询报告对应的供应商的授信信息
			List supperGrantList = (List) DataAccessor.query(
					"creditReportManage.selectSupperGrantInfo",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(supperGrantList.size()>0){
				supperGrantMap=(HashMap)supperGrantList.get(0);
				if(supperGrantMap.get("GRANT_PRICE")!=null){
					supperGrantMap.put("LAST_PRICE", SelectReportInfo.selectApplyLastPrice(Integer.parseInt(supperGrantMap.get("ID").toString())));
				}else{
					supperGrantMap.put("LAST_PRICE","0.00");
				}
			
				//抓取供应商交机前授信额度
				context.contextMap.put("suppl_id", supperGrantMap.get("ID"));
				context.contextMap.put("SUPLNAME", supperGrantMap.get("SUPNAME"));
				Map suplGrantMoneyMap;
				Map totalPayMoneyMap;
				
				suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
				if (suplGrantMoneyMap!=null){
					supperGrantMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));
	
					totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if (totalPayMoneyMap!=null){
						//判断授信的交机前拨款额度是否大于已用额度
						if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
							supperGrantMap.put("advance_machine", 0);
						}else{
							supperGrantMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
						}
					}
				}else{
					supperGrantMap.put("advance_machine", 0);
					supperGrantMap.put("advance_grant", 0);
				}
			}
			outputMap.put("supperGrantMap", supperGrantMap);
			//查询报告对应的客户的授信信息
			custGrantMap = (Map) DataAccessor.query(
					"creditReportManage.grantcustInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(custGrantMap!=null){
			
				if(custGrantMap.get("GRANT_PRICE")!=null){
					custGrantMap.put("GRANT_PRICE", custGrantMap.get("GRANT_PRICE").toString());
					custGrantMap.put("LAST_PRICE", CustomerCredit.getCustCredit(custGrantMap.get("CUST_ID")));
					
				}else{
					custGrantMap.put("GRANT_PRICE", "0");
					custGrantMap.put("LAST_PRICE", "0");
				}
				
			}
			outputMap.put("custGrantMap", custGrantMap);
			//查询出拨款情况
			List appropiateList = (List) DataAccessor.query("creditReportManage.getAppropiateByCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("appropiateList", appropiateList);
			
			//查出主档信息
			context.contextMap.put("custId", creditMap.get("CUST_ID")+"") ;
			outputMap.put("groupNumIdlist",DataAccessor.query("customer.groupNumIdCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST));
			
			//在租赁方案中加入权限控制 add by ShenQi see mantis 307
			//199 公司代号
		    //200 区域主管
		    //201 客户经理
		    //202 客户来源
			
			List<String> resourceIdList=(List<String>)DataAccessor.query("supplier.getResourceIdListByEmplId",context.contextMap,DataAccessor.RS_TYPE.LIST);
			boolean cmpy=false;
			boolean areaMar=false;
			boolean customerMar=false;
			boolean customer=false;
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				if("199".equals(resourceIdList.get(i))) {
					cmpy=true;
				} else if("200".equals(resourceIdList.get(i))) {
					areaMar=true;
				} else if("201".equals(resourceIdList.get(i))) {
					customerMar=true;
				} else if("202".equals(resourceIdList.get(i))) {
					customer=true;
				}
			}
			outputMap.put("cmpy",cmpy);
			outputMap.put("areaMar",areaMar);
			outputMap.put("customerMar",customerMar);
			outputMap.put("customer",customer);
			
			//监控报告是否是有效的 add by ShenQi
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			DataAccessor.execute("creditReportManage.monitorCredit",context.contextMap,OPERATION_TYPE.UPDATE);
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi copy by Yang Yun
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告查询基本信息（修改页使用）错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
				if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
					outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
					Output.jspOutput(outputMap, context, "/credit_vip/creditFrameCommit.jsp");
				}else{
					Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
				}
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}
	
	/**
	 * 绿色通道，案件状况表
	 * @param context
	 */
	public void showCreditStatusInfo(Context context){
		PagingInfo<Object> pagingInfo = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		double totalMoney = 0;
		try {
			pagingInfo = baseService.queryForListWithPaging("creditReportManage.getCreditStatusInfo", context.contextMap, "CREATE_DATE", ORDER_TYPE.DESC);
			if (pagingInfo != null && pagingInfo.getResultList() != null && pagingInfo.getResultList().size() > 0) {
				CreditTo credit = null;
				for(Object o : pagingInfo.getResultList()){
					if (o == null) {
						continue;
					}
					credit = (CreditTo) o;
					totalMoney += (credit.getLeaseRze() == null ? 0 : credit.getLeaseRze());
				}
			}
		} catch (ServiceException e) {
			logger.error(e);
		}
		outputMap.put("totalMoney", totalMoney);
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_status", context.contextMap.get("credit_status"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("pagingInfo", pagingInfo);
		Output.jspOutput(outputMap, context, "/credit_vip/creditReportStatusInfo.jsp");
	}
	
	public void addCreditMemo(Context context){
		boolean flag = false;
		try {
			baseService.insert("creditReportManage.insertCreditMemo", context.contextMap);
			flag = true;
		} catch (ServiceException e) {
			logger.error(e);
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	public void getCreditMemo(Context context){
		List<BaseTo> resultList = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			resultList = (List<BaseTo>) baseService.queryForList("creditReportManage.getCreditMemo", context.contextMap);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Output.jsonArrayOutputForObject(resultList, context);
	}
	
	public void getCreditMemoAll(Context context){
		List<BaseTo> resultList = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			resultList = (List<BaseTo>) baseService.queryForList("creditReportManage.getCreditMemoAll", context.contextMap);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/credit_vip/creditMemoDetail.jsp");
	}
	
	
	public  static Map<String,Object> getPrjtTransferCheckInfo(String ID) throws Exception{
		
		Map<String,Object> result = new HashMap<String,Object>();		
		Map<String,Object> credit_project = new HashMap<String,Object>();
		
		Map<String,Object> param =new HashMap<String,Object>();
		param.put("credit_id", ID);
		Map schemeMap = (Map) DataAccessor.query(
			"creditReportManage.selectCreditScheme",
			param, DataAccessor.RS_TYPE.MAP);
		
		//查询出拨款情况
		List<Map> appropiateList = (List<Map>) DataAccessor.query("creditReportManage.getAppropiateByCreditId", param,DataAccessor.RS_TYPE.LIST);
		result.put("paylist", appropiateList);
		
		StringBuffer pay_type = new StringBuffer("");
		for(Map appropiate:appropiateList){
			 String type = String.valueOf(appropiate.get("TYPE")) ;
			 if("0".equals(type)){
				 pay_type.append("交机/设定前  ");			 
			 }else if("1".equals(type)){
				 pay_type.append("交机/设定后  ");			 
			 }
		}
		
		credit_project.put("PAY_TYPE", pay_type.toString());
		credit_project.put("CUST_NAME", LeaseUtil.getCustNameByCreditId(ID));
		credit_project.put("SUPP_NAME", LeaseUtil.getSuplNameByCreditId(ID));
		credit_project.put("INVOICE_PERSON", schemeMap.get("INVOICE_PERSON")!=null?schemeMap.get("INVOICE_PERSON").toString():"");
	
		String code  = String.valueOf(schemeMap.get("SUPL_TRUE"));
		code = DictionaryUtil.getFlag("供应商保证", code);
		code = code!=null?code:"无";
		credit_project.put("CODE", code);
		
		String lockCode = LeaseUtil.getLockCodeByCreditId(ID);
		lockCode = DictionaryUtil.getFlag("锁码方式", lockCode);
		lockCode = lockCode!=null?lockCode:"无";
		credit_project.put("LOCK_CODE", lockCode);
		
		BigDecimal pay_money = new BigDecimal(LeaseUtil.getPayMoneyByCreditId(ID));
		pay_money = pay_money.subtract(new BigDecimal((Double)schemeMap.get("PLEDGE_ENTER_MCTOAG")));
		pay_money = pay_money.subtract(new BigDecimal((Double)schemeMap.get("PLEDGE_ENTER_AG")));
		
		NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
		nfFSNum.setGroupingUsed(true);
		nfFSNum.setMaximumFractionDigits(2);
		credit_project.put("PAY_MONEY",nfFSNum.format(pay_money));

		String servicerId = LeaseUtil.getServiceUserIdByCreditId(ID);
		if(servicerId != null){
			String servicerName = LeaseUtil.getUserNameByUserId(servicerId);
			String servicerMobile = LeaseUtil.getMobileByUserId(servicerId);
			credit_project.put("SERVICER_NAME", servicerName!=null?servicerName:"");
			credit_project.put("SERVICER_MOBILE", servicerMobile!=null?servicerMobile:"");
		}else{
			credit_project.put("SERVICER_NAME", "");
			credit_project.put("SERVICER_MOBILE", "");
		}
		
		List<BaseTo> memos = LeaseUtil.getCreditMemoByCreditId(ID);
		StringBuffer memo = new StringBuffer("");
		int i= 1;
		for(BaseTo m:memos){
			memo.append(i );
			memo.append(".");
			memo.append(m.getContent()!=null?m.getContent():"");
			memo.append("\n");
			i++;
		}

		credit_project.put("MEMO", memo.toString());

		List<Map<String,Object>> credit_projects = new ArrayList<Map<String,Object>>();
		credit_projects.add(credit_project);
		result.put("credit_project",credit_projects);
		
		String suplId = LeaseUtil.getSuplIdByCreditId(ID);
		List<Map<String,String>> s_linkmans = new ArrayList<Map<String,String>>();	
		boolean isNotEmpty = false;
		if(suplId!=null){
			List<LinkManTo> linkmans =  LeaseUtil.getLinkManInfoBySupplId(suplId);
		
			for(LinkManTo linkman:linkmans){
				isNotEmpty = true;
				Map<String,String> s_linkman = new HashMap<String,String>();
				s_linkman.put("NAME", linkman.getLink_name());
				s_linkman.put("PHONE", linkman.getLink_mobile());
				s_linkman.put("ADDRESS", linkman.getLink_work_address());
				s_linkmans.add(s_linkman);
			}
		}
		if(!isNotEmpty){//PDF 格式 空一行
			Map<String,String> s_linkman = new HashMap<String,String>();
			s_linkman.put("NAME", "");
			s_linkman.put("PHONE", "");
			s_linkman.put("ADDRESS", "");
			s_linkmans.add(s_linkman);
		}
		result.put("supplier_linkman",s_linkmans);
		
		String custId = LeaseUtil.getCustIdByCreditId(ID);
		List<Map<String,String>> c_linkmans = new ArrayList<Map<String,String>>();	
		isNotEmpty = false;
		if(custId!=null){
			List<LinkManTo> linkmans =  LeaseUtil.getLinkManInfoByCustId(custId);
			for(LinkManTo linkman:linkmans){
				isNotEmpty = true;
				Map<String,String> c_linkman = new HashMap<String,String>();
				c_linkman.put("NAME", linkman.getLink_name());
				c_linkman.put("PHONE", linkman.getLink_mobile());
				c_linkman.put("TYPE", linkman.getLink_relation());
				c_linkman.put("ADDRESS", linkman.getLink_work_address());
				c_linkmans.add(c_linkman);
			}
		}
		if(!isNotEmpty){//PDF 格式 空一行
			Map<String,String> c_linkman = new HashMap<String,String>();
			c_linkman.put("NAME", "");
			c_linkman.put("PHONE", "");
			c_linkman.put("TYPE", "");
			c_linkman.put("ADDRESS", "");
			c_linkmans.add(c_linkman);
		}
		result.put("customer_linkman",c_linkmans);
	
		
		return result;
	}
}