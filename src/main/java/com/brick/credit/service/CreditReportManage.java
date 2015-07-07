package com.brick.credit.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.LeaseUtil.CREDIT_LINE_TYPE;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.service.StartPayService;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PaylistUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.credit.to.CreditTo;
import com.brick.customer.service.CustomerCredit;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.invoice.service.InvoiceManageService;
import com.brick.log.service.LogPrint;
import com.brick.project.service.TagService;
import com.brick.project.to.TagTo;
import com.brick.quotation.service.Quotation;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.risk_audit.service.RiskAuditService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.special.to.CreditSpecialTO;
import com.brick.supplier.to.SupplierGroupTO;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.brick.visitation.service.VisitationService;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibm.icu.util.Calendar;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * 资信管理
 * 
 * @author li shaojie
 * @date Apr 27, 2010
 */

public class CreditReportManage extends BaseCommand {
	Log logger = LogFactory.getLog(CreditReportManage.class);

	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	private RiskAuditService riskAuditService;
	private VisitationService visitationService;
	private CreditReportService creditReportService;
	
	public RiskAuditService getRiskAuditService() {
		return riskAuditService;
	}
	public void setRiskAuditService(RiskAuditService riskAuditService) {
		this.riskAuditService = riskAuditService;
	}

	public VisitationService getVisitationService() {
		return visitationService;
	}

	public void setVisitationService(VisitationService visitationService) {
		this.visitationService = visitationService;
	}

	public CreditReportService getCreditReportService() {
		return creditReportService;
	}

	public void setCreditReportService(CreditReportService creditReportService) {
		this.creditReportService = creditReportService;
	}

	private TagService tagService;
	
	

	public TagService getTagService() {
		return tagService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}
	
	private InvoiceManageService invoiceManageService;
	
	public InvoiceManageService getInvoiceManageService() {
		return invoiceManageService;
	}

	public void setInvoiceManageService(InvoiceManageService invoiceManageService) {
		this.invoiceManageService = invoiceManageService;
	}

	/**
	 * 资信评审
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
		//只显示未废除的报告
		context.contextMap.put("creditStauts",0);
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));

			pagingInfo = baseService.queryForListWithPaging(
					"creditReportManage.getCreditReports2", context.contextMap,
					"CREDIT_RUNCODE", ORDER_TYPE.DESC);
			
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告审批列表错误!请联系管理员") ;
		}
		List<TagTo> tagList = tagService.getAllTags(1);//案件类型
		outputMap.put("tags", tagList);
		
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("creditStauts", context.contextMap.get("creditStauts"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit/creditReportExamine.jsp");
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

			StringBuffer CREDIT_IDS=new StringBuffer();
			String orderBy="";
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {////判断是否是从业务人员桌面提交的 add by ShenQi
				orderBy="FLAG";
				List<String> creditIds=(List<String>) DataAccessor.query("creditReportManage.getExtremelyCredit",context.contextMap,DataAccessor.RS_TYPE.LIST);
				for(int i=0;creditIds!=null&&i<creditIds.size();i++) {
					CREDIT_IDS.append("'"+creditIds.get(i)+"',");
				}
				if(CREDIT_IDS.length()!=0) {
					context.contextMap.put("CREDIT_IDS",CREDIT_IDS.substring(0,CREDIT_IDS.length()-1));
				}
			} else {
				orderBy="CREDIT_RUNCODE";
			}
			
			pagingInfo = baseService.queryForListWithPaging("creditReportManage.getCreditReports", context.contextMap,orderBy, ORDER_TYPE.DESC);
			
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {
				//业务人员桌面:加入访厂状态判断
				List<Map<String,String>> visitList=(List<Map<String,String>>)DataAccessor.query("creditReportManage.getVisitStatus",context.contextMap,DataAccessor.RS_TYPE.LIST);
				for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
					for(int j=0;visitList!=null&&j<visitList.size();j++) {
						if(String.valueOf(((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID")).equals(String.valueOf(visitList.get(j).get("CREDIT_ID")))) {
							((Map<String,Object>)(pagingInfo.getResultList().get(i))).put("VISIT_STATUS",visitList.get(j).get("VISIT_STATUS"));
							((Map<String,Object>)(pagingInfo.getResultList().get(i))).put("VISIT_ID",visitList.get(j).get("VISIT_ID"));
							break;
						}
					}
				}
			}
			
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
			
			boolean tag_authority = false;
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
			
			BaseTo baseTo = new BaseTo();
			baseTo.setModify_by(context.contextMap.get("s_employeeId").toString());
			baseTo.setResource_code("report_manage_tag");
			tag_authority = tagService.checkAccessForResource(baseTo);
			
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
			outputMap.put("HAS_VISIT",context.contextMap.get("HAS_VISIT"));
			outputMap.put("vip_flag",context.contextMap.get("vip_flag"));
			
			outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));//判断是否是从业务人员综合界面跳入
			
			List<TagTo> tagList = tagService.getAllTags(1);//案件类型
			outputMap.put("tags", tagList);
			
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
		outputMap.put("specialAlert", context.contextMap.get("specialAlert"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit/creditReportManage.jsp");
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
					"/credit/creditReportPowerManage.jsp");
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
			DataAccessor.execute("creditReportManage.updateconType", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

			sqlMapper.delete("creditReportManage.deleteCreditSchemaIrr", context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditScheme", context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditInsure", context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditEquipment", context.contextMap);
			sqlMapper.delete("creditReportManage.deleteCreditOtherPrice", context.contextMap);
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
						map.put("UNIT_PRICE", UNIT_PRICE[i]);
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
			String isImportEqip = (String) context.contextMap.get("isImportEqip");
			if(!"1".equals(isImportEqip)){
				context.contextMap.put("isImportEqip",null);
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
			sqlMapper.delete("creditReportManage.deletePayListFeeList", context.contextMap);
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
					sqlMapper.insert("creditReportManage.createCreditInsure", map);
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
					map.put("OTHER_NAME", HTMLUtil.parseStrParam2(OTHER_NAME[i], "0"));
					map.put("OTHER_PRICE", HTMLUtil.parseStrParam2(OTHER_PRICE[i], ""));
					map.put("OTHER_DATE", HTMLUtil.parseStrParam2(OTHER_DATE[i], ""));
					map.put("MEMO", HTMLUtil.parseStrParam2(OTHER_MEMO[i], "0"));
					map.put("CREDIT_ID", context.contextMap.get("credit_id"));
					sqlMapper.insert("creditReportManage.createCreditOtherPrice", map);
				}
			}
			//拨款方式交机前后
			if (context.request.getParameterValues("APPROPRIATEFUNS") != null) {
				String[] APPROPRIATEFUNS = context.request
				.getParameterValues("APPROPRIATEFUNS");
				Map creditidMap=new HashMap();
				creditidMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
				sqlMapper.delete("creditReportManage.deleteAppropiateMon", creditidMap);
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
					
					sqlMapper.insert("creditReportManage.createAppropiateMon", map);
				}
			}
			//修改主档
			//先删除该报告对应的主档的credit_id字段
			sqlMapper.update("customer.deleteZhudangCreditId", context.contextMap) ;
			if(!"".equals((String)context.contextMap.get("ACTILOG_ID"))){
				context.contextMap.put("creditId", context.contextMap.get("credit_id")) ;
				//如果修改
				sqlMapper.update("creditCustomer.updatelog", context.contextMap);
			}
			
			//******************************************************************************************add by ShenQi 2012-8-16
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
		if(StringUtils.isEmpty(context.contextMap.get("functionFrom"))||"N".equals(context.contextMap.get("functionFrom")+"")) {//非业务支撑跳转原来的页面
		
			if(errList.isEmpty()){
			String cust_type = (String) context.contextMap.get("H_CUST_TYPE");
				if (cust_type.equals("1")) {
					Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorp.selectCreditCustomerCorpForUpdate&credit_id="
							+ context.contextMap.get("credit_id"));
				} else {
					Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorp.selectCreditCustomerCorpForUpdate&credit_id="
							+ context.contextMap.get("credit_id"));
				}
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		
		} else {//业务支撑跳转
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditPaylistService.createCreditPaylist&credit_id="+context.contextMap.get("credit_id")+"&showFlag=4&word=up&functionFrom=Y");
		}
	}

	/**
	 * 查询资信方案信息用户更新
	 * 
	 * @param context
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditForUpdate(Context context) throws SQLException {
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
			//供应商联系人姓名
			Map supplierContactMap =(Map)DataAccessor.query("supplier.querySupplLinkManById", creditMap, DataAccessor.RS_TYPE.MAP);
			
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
			outputMap.put("supplierContactMap", supplierContactMap);
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
			//判断供应商是否有全面回购类型,如果有全面回购,供应商保证栏位不能选择无,默认选择回购 add by ShenQi
			if(creditMap.get("CREDIT_SPECIAL_CODE")!=null&&!"".equals(creditMap.get("CREDIT_SPECIAL_CODE"))) {//加入专案判别
				//如果此报告有专案则不限制供应商保证下拉框
			} else {
				for(int i=0;equipmentsList!=null&&i<equipmentsList.size();i++) {
					if("Y".equals(((Map<String,Object>)(equipmentsList.get(i))).get("BUY_BACK"))) {
						for(int j=0;j<suplList.size();j++) {
							if("4".equals(((Map<String,Object>)suplList.get(j)).get("CODE"))) {//不能选择供应商保证:无
								suplList.remove(j);
								outputMap.put("IS_BUY_BACK","Y");
								break;
							}
						}
						break;
					}
				}
			}
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
			if(schemeMap!=null&&(Constants.TAX_PLAN_CODE_4.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_6.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_7.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_8.equals(schemeMap.get("TAX_PLAN_CODE")))) {
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
				}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_6.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_7.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_8.equals(schemeMap.get("TAX_PLAN_CODE"))){
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
			
			if(schemeMap!=null&&"5".equals(schemeMap.get("TAX_PLAN_CODE"))) {
					paylist.put("SALES_PAY", schemeMap.get("SALES_PAY"));
					paylist.put("INCOME_PAY", schemeMap.get("INCOME_PAY"));
					paylist.put("OUT_PAY", schemeMap.get("OUT_PAY"));
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
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
			//Add by Michael 2012 09-21 增加税费测算方案
			outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
			
			//Add by Michael 2012 12-20  增加费用来源
			outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));
			
			//获得专案列表
			//增加办事处限制2014-02-13 zhang
			String s_employeeDecpId = String.valueOf(context.contextMap.get("s_employeeDecpId"));
			Map<String, Object> decpMap=new HashMap<String, Object>();
			decpMap.put("propertyCode", "BELONGDEPT");
			decpMap.put("decpId", s_employeeDecpId);
			List<CreditSpecialTO> creditSpecialList=(List<CreditSpecialTO>)DataAccessor.query("creditSpecial.queryCreditSpecialGroupByDecpId",decpMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditSpecialList",creditSpecialList);
			//来自业务支撑的操作
			if(StringUtils.isEmpty(context.contextMap.get("functionFrom"))) {
				outputMap.put("SUPPORT","N");
			} else {
				outputMap.put("SUPPORT",context.contextMap.get("functionFrom"));//业务支撑跳转过来的
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告查询基本信息（修改页使用）错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
//				Output.jspOutput(outputMap, context, "/credit/creditFrame.jsp");
				//根据报告类型
				int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrame.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrame.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrame.jsp");
				}
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
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditForShow(Context context) throws SQLException {
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
			outputMap.put("taxPlanList", DataAccessor.query("dataDictionary.queryDataDictionaryByValueAdded", null, DataAccessor.RS_TYPE.LIST));
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
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
			
			//Add by Michael 2012 12-20  增加费用来源
			outputMap.put("feeSourceList", DictionaryUtil.getDictionary("费用来源"));

			//获得专案列表
			List<CreditSpecialTO> creditSpecialList=null;
			creditSpecialList=(List<CreditSpecialTO>)DataAccessor.query("creditSpecial.queryCreditSpecialGroup",null,DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditSpecialList",creditSpecialList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告基本信息查看错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
//				if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
//					outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
//					Output.jspOutput(outputMap, context, "/credit/creditFrameCommit.jsp");
//				}else{
//					Output.jspOutput(outputMap, context, "/credit/creditFrameShow.jsp");
//				}
				//根据报告类型
				int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
				if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
					outputMap.put("commit_flag", context.contextMap.get("commit_flag"));

					if(productionType==1){
						Output.jspOutput(outputMap, context, "/credit/equip/creditFrameCommit.jsp");
					}else if(productionType==2){
						Output.jspOutput(outputMap, context, "/credit/truck/creditFrameCommit.jsp");
					}else if(productionType==3){
						Output.jspOutput(outputMap, context, "/credit/car/creditFrameCommit.jsp");
					}
				}else{
					if(productionType==1){
						Output.jspOutput(outputMap, context, "/credit/equip/creditFrameShow.jsp");
					}else if(productionType==2){
						Output.jspOutput(outputMap, context, "/credit/truck/creditFrameShow.jsp");
					}else if(productionType==3){
						Output.jspOutput(outputMap, context, "/credit/car/creditFrameShow.jsp");
					}
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
		
		//发送邮件
		//获得此报告案件的信息
		try {
			Map<String,Object>resultCorpMap=(Map)DataAccessor.query("creditCustomerCorp.getCreditCustomerCorpByCreditId",context.contextMap,DataAccessor.RS_TYPE.MAP);
			
			String corpName=(String)resultCorpMap.get("CORP_NAME_CN");
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setCreateBy(context.contextMap.get("s_employeeId").toString());
			mailSettingTo.setEmailFrom("tacfinance_service@tacleasing.cn");
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
			mailSettingTo.setEmailSubject("报告提交到审批");
			StringBuffer content=new StringBuffer();
			content.append("客户:"+corpName+"需您审批!");
			mailSettingTo.setEmailContent(content.toString());
			
			mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			logger.debug("业务员提交报告插入发送邮件失败!");
			e.printStackTrace();
			this.creditManage(context);
			return;
		}
		
		if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
		} else {
			this.creditManage(context);
		}
	}

	/**
	 * 单位主管审批通过
	 * 
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void examineCredit(Context context) throws Exception {
		String returnFlag = null;
		try {
			returnFlag = creditReportService.doCommitCredit(context);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
		if ("next".equals(returnFlag)) {
			creditOperationManage(context);
		} else {
			creditExamine(context);
		}
		//TODO
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
		List<Map<String, Object>> resultByGroup = null;
		Map<String, Object> mergedProject = null;
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
	 * 单位主管审批驳回
	 * 
	 * @param context
	 */
	
	//此方法是报告驳回  添加注释   add by ShenQi 2012-7-26
	public void examineCredit2(Context context) {
		
		//如果此报告满足条件提交到业务副总 state=5
		Map<String, Object> creditMap=new HashMap<String, Object>();
		try {
			creditMap=(Map<String,Object>)DataAccessor.query("creditReportManage.getStateByCreditId",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int creditState=Integer.valueOf(creditMap.get("STATE").toString());
		
		if(creditState==5) {//如果等于5是业务副总提交过来
			context.contextMap.put("AUDIT_STATE",Constants.AUDIT_STATE_1);//业务副总审批的
		} else {//不等于5直接从业务主管提交
			context.contextMap.put("AUDIT_STATE",Constants.AUDIT_STATE_0);//区域主管审批的
		}
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			context.contextMap.put("statee",2);
			sqlMapper.update("creditReportManage.examineCredit2",
					context.contextMap);
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
		
		if(creditState==5) {//加入跳转条件,如果在业务副总页面审批,则跳转业务副总页面
			context.contextMap.put("content","");
			context.contextMap.put("start_date","");
			context.contextMap.put("end_date","");
			this.creditOperationManage(context);
		} else {
			this.creditExamine(context);
		}
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
			outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
			Output.jspOutput(outputMap, context, "/credit/rentFile.jsp");
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
			logger.info("文件大小==========>>" + fileItem.getSize());
			if (fileItem.getSize() > 2097152) {
				errList.add("附件太大了，不能大于2M了。") ;
			}
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
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
			} else {
				if(DataUtil.intUtil(context.contextMap.get("rentFileFlag"))==1){
					Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage");	
				}else{
					Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage");
				}
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
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
			} else {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditReport.creditManage");
			}
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
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
			} else {
				Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditReport.creditManage");
			}
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
			"defaultDispatcher?__action=creditReport.creditManage");
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
			Output.jspOutput(outputMap, context, "/credit/memos.jsp");
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
			context.contextMap.put("type1", "1");
			context.contextMap.put("type2", "2");
			Map<String, Object> activitiesLog = (Map<String, Object>) DataAccessor.query("rentFile.getunUpForActive", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if ("false".equals(activitiesLog.get("RESULT"))) {
				Map<String, Object> activitiesLogMap = new HashMap<String, Object>();
				activitiesLogMap.put("FILE_TYPE", "活动日志");
				activitiesLogMap.put("FILE_NAME", "案件状况");
				unUpList.add(activitiesLogMap);
			}
			/* ---------------------------------------------------------- */
			
			/* 2013/7/12 zhangbo 增加供应商联系人卡关。  -------------------- */
				//获取报告
				Map creditMap =(Map)DataAccessor.query("supplier.queryCreditByCreditId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				String SUPPLIER =creditMap.get("SUPPLIER_CONTACT")==null?"":String.valueOf(creditMap.get("SUPPLIER_CONTACT"));
				if("".equals(SUPPLIER) || "-1".equals(SUPPLIER)){
					Map<String, Object> activitiesMap = new HashMap<String, Object>();
					activitiesMap.put("FILE_TYPE", "供应商联系人");
					activitiesMap.put("FILE_NAME", "报告中未选择供应商联系人");
					unUpList.add(activitiesMap);
				} 
				//增加利差小0的不给提交的卡关
				String creditId =context.contextMap.get("credit_id")==null?"":String.valueOf(context.contextMap.get("credit_id"));
				double RATE_DIFF=LeaseUtil.getDiffByCreditId(creditId);
				//System.out.println("___________________________利差："+RATE_DIFF+"___________________________");
				if( RATE_DIFF < 1){
					Map<String, Object> diffMap = new HashMap<String, Object>();
					diffMap.put("FILE_TYPE", "利差");
					diffMap.put("FILE_NAME", "利差小于0元");
					unUpList.add(diffMap);
				}
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
			Output.jspOutput(outputMap, context, "/credit/pactCorp.jsp");
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
			Output.jspOutput(outputMap, context, "/credit/sponsorManage.jsp");
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
			Output.jspOutput(outputMap, context, "/credit/showSponsorCrop.jsp");
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
		    Output.jspOutput(outputMap, context, "/credit/creditCustReport.jsp");
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
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		String content = "" ;
		boolean flag1 = false ;
		boolean flag2 = false ;
		boolean flag3 = false ;
		try{
			if("credit".equals(context.contextMap.get("fromWhere"))) {//因为后面的评审浏览也会调用isCanSubmit方法 所以加入flag判断 只有报告提交时候进入下面 add by ShenQi
			//--------------------------------------------------------------------------------------------------------------------------------------------------------
			//如果是专案,检核专案条件 add by ShenQi 2013-4-25
			List<CreditSpecialTO> creditSpecialCaseProperty=(List<CreditSpecialTO>)DataAccessor.query("creditReportManage.getCreditSpecialCasePropertyByCreditId",context.contextMap,DataAccessor.RS_TYPE.LIST);
			if(creditSpecialCaseProperty==null||creditSpecialCaseProperty.size()==0) {
				//非专案,无需卡关
				logger.debug("非专案,无需卡关");
			} else {
				logger.debug("专案卡关开始");
				
				java.text.DecimalFormat dfm=new java.text.DecimalFormat("#,##0.00");  
				StringBuffer logContent=new StringBuffer();//初始化邮件内容
				//专案有效期
				Calendar dt=Calendar.getInstance();
				String startDateDescr=creditSpecialCaseProperty.get(0).getStartDateDescr();
				String endDateDescr=creditSpecialCaseProperty.get(0).getEndDateDescr();
				String creditName=creditSpecialCaseProperty.get(0).getCreditName();
				String creditCode=creditSpecialCaseProperty.get(0).getCreditCode();
				if(DateUtil.strToDate(DateUtil.dateToString(dt.getTime(),"yyyy-MM-dd"),"yyyy-MM-dd").compareTo(DateUtil.strToDate(startDateDescr,"yyyy-MM-dd"))==-1||
						DateUtil.strToDate(endDateDescr,"yyyy-MM-dd").compareTo(DateUtil.strToDate(DateUtil.dateToString(dt.getTime(),"yyyy-MM-dd"),"yyyy-MM-dd"))==-1) {
					logContent.append("专案限制:"+DateUtil.dateToString(dt.getTime(),"yyyy-MM-dd")+"不在专案有效期"+startDateDescr+"~"+endDateDescr+"内<br>");
					content+="专案限制:当前日期不在专案有效期内!";
					logger.debug("当前日期不在专案有效期内");
				}
				
				Map<String,Object> param=new HashMap<String,Object>();
				
				List<Map<String,Object>> equipmentsList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.selectCreditEquipment",context.contextMap,DataAccessor.RS_TYPE.LIST);
				Map<String,Object> schemeMap=(Map<String,Object>)DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap,DataAccessor.RS_TYPE.MAP);
				for(int i=0;i<creditSpecialCaseProperty.size();i++) {
					if("LEASE_PRODUCTION_NAME".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
						&&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//租赁物名称
						String productionName=creditSpecialCaseProperty.get(i).getValue1();
						param.put("type",productionName);
						List<String> productionList=(List<String>)DataAccessor.query("creditReportManage.getProductionList",param,DataAccessor.RS_TYPE.LIST);
						
						List<String> compareList=new ArrayList<String>();
						for(int l=0;l<equipmentsList.size();l++) {
							boolean flag=true;
							for(int j=0;productionList!=null&&j<productionList.size();j++) {
								if(equipmentsList.get(l).get("THING_NAME").equals(productionList.get(j))) {
									compareList.add((String)equipmentsList.get(l).get("THING_NAME"));
									flag=false;
									break;
								}
							}
							if(flag) {
								//加入不属于专案的租赁物名称
								logContent.append("专案限制:"+equipmentsList.get(l).get("THING_NAME")+"不在专案租赁物名称内<br>");
							}
						}
						if(compareList.size()<equipmentsList.size()) {
							content+="专案限制:该案件有租赁物不在专案租赁物名称设定内!";
							logger.debug("该案件有租赁物不在专案租赁物名称设定内");
						}
					} else if("LEASE_PRODUCTION_BRAND".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//租赁物厂牌
						List<String> compareList=new ArrayList<String>();
						if(creditSpecialCaseProperty.get(i).getValue1()!=null&&!"".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							if(creditSpecialCaseProperty.get(i).getValue1()==null) {
								content+=creditName+"租赁物厂牌维护有误,请联系管理员";
								outputMap.put("content",content);
								Output.jsonOutput(outputMap,context);
								return;
							}
							String [] brands=creditSpecialCaseProperty.get(i).getValue1().split(",");
							for(int l=0;l<equipmentsList.size();l++) {
								boolean flag=true;
								for(int j=0;j<brands.length;j++) {
									if(equipmentsList.get(l).get("THING_KIND").equals(brands[j])) {
										compareList.add((String)equipmentsList.get(l).get("THING_KIND"));
										flag=false;
										break;
									}
								}
								if(flag) {
									//加入不属于专案的租赁物名称
									logContent.append("专案限制:"+equipmentsList.get(l).get("THING_KIND")+"不在专案租赁物厂牌设定内<br>");
									break;
								}
							}
						}
						
						if(compareList.size()<equipmentsList.size()) {
							content+="专案限制:该案件有租赁物厂牌不在专案租赁物厂牌设定内!";
							logger.debug("该案件有租赁物厂牌不在专案租赁物厂牌设定内");
						}
					} else if("LEASE_PRODUCTION_IS_LOCK".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//租赁物锁码
						StringBuffer companreValue=new StringBuffer();
						if("不限制".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							//不做卡关
						} else if("锁码".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							for(int l=0;l<equipmentsList.size();l++) {
								if(equipmentsList.get(l).get("LOCK_CODE")==null) {
									content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
									companreValue.append("锁码栏位为空");
									break;
								} else {
									if("4".equals(equipmentsList.get(l).get("LOCK_CODE").toString())) {//4为无锁码;
										content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
										companreValue.append(equipmentsList.get(l).get("THING_NAME")+"设定无锁码");
										break;
									}
								}
							}
						} else if("不锁码".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							for(int l=0;l<equipmentsList.size();l++) {
								if(equipmentsList.get(l).get("LOCK_CODE")==null) {
									content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
									companreValue.append("锁码栏位为空");
									break;
								} else {
									if("1".equals(equipmentsList.get(l).get("LOCK_CODE").toString())) {//1,2,3为有锁码;
										content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
										companreValue.append(equipmentsList.get(l).get("THING_NAME")+"设定间接锁码");
										break;
									} else if("2".equals(equipmentsList.get(l).get("LOCK_CODE").toString())) {
										content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
										companreValue.append(equipmentsList.get(l).get("THING_NAME")+"设定异常锁码");
										break;
									} else if("3".equals(equipmentsList.get(l).get("LOCK_CODE").toString())) {
										content+="专案限制:该案件租赁物锁码方式与专案设定锁码方式不符!";
										companreValue.append(equipmentsList.get(l).get("THING_NAME")+"设定直接锁码");
										break;
									}
								}
							}
						}
						
						if(companreValue.length()!=0) {
							logContent.append("专案限制:"+companreValue+",与专案设定锁码方式不符<br>");
						}
					} else if("SUPPLIER_NAME".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//供应商名称
						List<String> compareList=new ArrayList<String>();
						if(creditSpecialCaseProperty.get(i).getValue1()==null||creditSpecialCaseProperty.get(i).getValue2()==null) {
							content+=creditName+"供应商名称维护有误,请联系管理员";
							outputMap.put("content",content);
							Output.jsonOutput(outputMap,context);
							return;
						}
						String [] supplierNames=creditSpecialCaseProperty.get(i).getValue1().split(",");
						String [] supplierIds=creditSpecialCaseProperty.get(i).getValue2().split(",");
						for(int l=0;l<equipmentsList.size();l++) {
							boolean flag=true;
							for(int j=0;j<supplierIds.length;j++) {
								if(supplierIds[j].equals(String.valueOf(equipmentsList.get(l).get("SUPPLIER_ID")==null?-100:equipmentsList.get(l).get("SUPPLIER_ID")))) {
									compareList.add(supplierNames[j]);
									flag=false;
									break;
								}
							}
							if(flag) {
								logContent.append("专案限制:"+equipmentsList.get(l).get("BRAND")+"不在专案供应商名称设定内<br>");
								break;
							}
						}
						
						if(compareList.size()<equipmentsList.size()) {
							content+="专案限制:该案件有供应商不在专案供应商名称设定内!";
							logger.debug("该案件有供应商不在专案供应商名称设定内");
						}
					} else if("SUPPLIER_LEVEL_LIMIT".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//供应商级别
						for(int l=0;l<equipmentsList.size();l++) {
							String level=(String)equipmentsList.get(l).get("SUPP_LEVEL");
							if(level==null||"".equals(level)) {
								logContent.append("专案限制:"
										+equipmentsList.get(l).get("BRAND")+"("+equipmentsList.get(l).get("SUPP_LEVEL")+")级别应大于等于专案设定的"
										+creditSpecialCaseProperty.get(i).getValue1()+"A级别<br>");
								content+="专案限制:该案件供应商级别应大于等于专案设定的"+creditSpecialCaseProperty.get(i).getValue1()+"A级别!";
								logger.debug("该案件供应商级别应大于等于专案设定的"+creditSpecialCaseProperty.get(i).getValue1()+"A级别!");
								break;
							} else {
								if(level.length()==1) {
									level="1A";
								}
								if(Integer.valueOf(level.substring(0,1))<Integer.valueOf(creditSpecialCaseProperty.get(i).getValue1())) {
									logContent.append("专案限制:"
											+equipmentsList.get(l).get("BRAND")+"("+equipmentsList.get(l).get("SUPP_LEVEL")+")级别应大于等于专案设定的"
											+creditSpecialCaseProperty.get(i).getValue1()+"A级别<br>");
									content+="专案限制:该案件供应商级别应大于等于专案设定的"+creditSpecialCaseProperty.get(i).getValue1()+"A级别!";
									logger.debug("该案件供应商级别应大于等于专案设定的"+creditSpecialCaseProperty.get(i).getValue1()+"A级别!");
									break;
								}
							}
						}
					} else if("IS_PAY_BEFORE".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())
							   &&!"不限制".equals(creditSpecialCaseProperty.get(i).getValue1())) {
						//是否交机前拨款
						int compareValue;
						if("交机前拨款".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							compareValue=0;
							List<Map<String,Object>> appropiateList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getAppropiateByCreditId",context.contextMap,DataAccessor.RS_TYPE.LIST);
							for(int l=0;appropiateList!=null&&l<appropiateList.size();l++) {
								if(appropiateList.get(l).get("TYPE")==null||"".equals(appropiateList.get(l).get("TYPE"))) {
									logContent.append("专案限制:该案件未设定是否交机前拨款与专案设定的交机前拨款不符<br>");
									content+="专案限制:该案件未设定是否交机前拨款与专案设定的交机前拨款不符!";
									logger.debug("该案件未设定是否交机前拨款与专案设定的交机前拨款不符");
									break;
								}
								if(Integer.valueOf(appropiateList.get(l).get("TYPE").toString())!=compareValue) {
									logContent.append("专案限制:该案件设定了交机后拨款与专案设定的交机前拨款不符<br>");
									content+="专案限制:该案件设定了交机后拨款与专案设定的交机前拨款不符!";
									logger.debug("该案件设定了交机后拨款与专案设定的交机前拨款不符");
									break;
								}
							}
						} else {
							compareValue=1;
							List<Map<String,Object>> appropiateList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getAppropiateByCreditId",context.contextMap,DataAccessor.RS_TYPE.LIST);
							for(int l=0;appropiateList!=null&&l<appropiateList.size();l++) {	
								if(appropiateList.get(l).get("TYPE")==null||"".equals(appropiateList.get(l).get("TYPE"))) {
									logContent.append("专案限制:该案件未设定是否交机前拨款与专案设定的交机前拨款不符<br>");
									content+="专案限制:该案件未设定是否交机前拨款与专案设定的交机前拨款不符!";
									logger.debug("该案件未设定是否交机前拨款与专案设定的交机前拨款不符");
									break;
								}
								if(Integer.valueOf(appropiateList.get(l).get("TYPE").toString())!=compareValue) {
									logContent.append("专案限制:该案件设定了交机前拨款与专案设定的交机后拨款不符<br>");
									content+="专案限制:该案件设定了交机前拨款与专案设定的交机后拨款不符!";
									logger.debug("该案件设定了交机前拨款与专案设定的交机后拨款不符");
									break;
								}
							}
						}
					} else if("SUPPLIER_UNION_PLEDGE".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//供应商保证
						if("不限制".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							
						} else if("全面连保".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							if(schemeMap!=null&&schemeMap.get("SUPL_TRUE")!=null) {
								if(Integer.valueOf(schemeMap.get("SUPL_TRUE").toString())!=1) {
									logContent.append("专案限制:该案件供应商保证与专案设定的全面连保条件不符<br>");
									content+="专案限制:该案件供应商保证与专案设定的全面连保条件不符!";
									logger.debug("该案件供应商保证与专案设定的全面连保条件不符");
								}
							} else {
								logContent.append("专案限制:该案件供应商保证与专案设定的全面连保条件不符<br>");
								content+="专案限制:该案件供应商保证与专案设定的全面连保条件不符!";
								logger.debug("该案件供应商保证与专案设定的全面连保条件不符");
							}
						} else if("全面回购".equals(creditSpecialCaseProperty.get(i).getValue1())) {
							if(schemeMap!=null&&schemeMap.get("SUPL_TRUE")!=null) {
								if(Integer.valueOf(schemeMap.get("SUPL_TRUE").toString())==4) {
									logContent.append("专案限制:该案件供应商保证与专案设定的全面回购条件不符<br>");
									content+="专案限制:该案件供应商保证与专案设定的全面回购条件不符!";
									logger.debug("该案件供应商保证与专案设定的全面回购条件不符");
								}
							} else {
								logContent.append("专案限制:该案件供应商保证与专案设定的全面回购条件不符<br>");
								content+="专案限制:该案件供应商保证与专案设定的全面回购条件不符!";
								logger.debug("该案件供应商保证与专案设定的全面回购条件不符");
							}
						}
					} else if("TOTAL_MONEY".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//专案总承做金额
						param.put("creditCode",creditCode);
						Integer totalLeaseRZE=(Integer)DataAccessor.query("creditReportManage.getCreditSpecialLeaseRZETotal",param,DataAccessor.RS_TYPE.OBJECT);
						
						int totalLeaseRZELimit=Integer.valueOf(creditSpecialCaseProperty.get(i).getValue1()==null||"".equals(creditSpecialCaseProperty.get(i).getValue1())?"0":creditSpecialCaseProperty.get(i).getValue1());
						if(totalLeaseRZE>=totalLeaseRZELimit) {
							//所有此专案的案件概算成本总和大于专案限定总承做金额,等于也需要卡住
							logContent.append("专案限制:该专案所有案件概算成本总额:"+dfm.format(totalLeaseRZE)+"大于等于专案"+dfm.format(totalLeaseRZELimit)+"的总承做金额<br>");
							content+="专案限制:该专案所有案件概算成本总额:"+totalLeaseRZE+"大于等于专案"+totalLeaseRZELimit+"的总承做金额!";
							logger.debug("该专案所有案件概算成本总额:"+totalLeaseRZE+"大于等于专案"+totalLeaseRZELimit+"的总承做金额");
						} else {
							//do nothing
						}
					} else if("LEASE_PERIOD".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//专案租赁期数,必卡
						if(schemeMap==null) {
							logContent.append("专案限制:该案件租赁期数与专案设定的租赁期限条件不符<br>");
							content+="专案限制:该案件租赁期数与专案设定的租赁期限条件不符!";
							logger.debug("该案件租赁期数与专案设定的租赁期限条件不符");
						} else {
							int period=schemeMap==null||schemeMap.get("LEASE_TERM")==null||"".equals(schemeMap.get("LEASE_TERM"))?0:Integer.valueOf(schemeMap.get("LEASE_TERM").toString());
							int minPeriod=creditSpecialCaseProperty.get(i).getValue1()==null||"".equals(creditSpecialCaseProperty.get(i).getValue1())?0:Integer.valueOf(creditSpecialCaseProperty.get(i).getValue1());
							int maxPeriod=creditSpecialCaseProperty.get(i).getValue2()==null||"".equals(creditSpecialCaseProperty.get(i).getValue2())?0:Integer.valueOf(creditSpecialCaseProperty.get(i).getValue2());
							if(period>maxPeriod||period<minPeriod) {
								logContent.append("专案限制:该案件租赁期数:"+period+"期不在专案设定的租赁期限"+minPeriod+"~"+maxPeriod+"条件内<br>");
								content+="专案限制:该案件租赁期数:"+period+"期不在专案设定的租赁期限"+minPeriod+"~"+maxPeriod+"条件内!";
								logger.debug("该案件租赁期数:"+period+"期不在专案设定的租赁期限"+minPeriod+"~"+maxPeriod+"条件内");
							}
						}
					}/* else if("LEASE_TR".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//此卡关加在 TR重新计算后
					}*/
					else if("LEASE_PERCENT".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//租赁成数
						double totalMoneyIncludeTax=0;
						for(int l=0;l<equipmentsList.size();l++) {
							totalMoneyIncludeTax+=equipmentsList.get(l).get("DENOMINATOR")==null||"".equals(equipmentsList.get(l).get("DENOMINATOR"))?0.0:Double.valueOf(equipmentsList.get(l).get("DENOMINATOR").toString());
						}
						double leaseRZE=schemeMap==null||schemeMap.get("LEASE_RZE")==null||"".equals(schemeMap.get("LEASE_RZE"))?0.00:Double.valueOf(schemeMap.get("LEASE_RZE").toString());
						DecimalFormat df=new DecimalFormat("#.00");
						double companreValue=Double.valueOf(df.format(leaseRZE/totalMoneyIncludeTax*100));
						double limitValue=creditSpecialCaseProperty.get(i).getValue1()==null||
								"".equals(creditSpecialCaseProperty.get(i).getValue1())
								?0.00:Double.valueOf(creditSpecialCaseProperty.get(i).getValue1());
						if(companreValue>=limitValue) {
							logContent.append("专案限制:该案件租赁成数"+companreValue+"%不在专案设定的租赁成数"+limitValue+"条件内<br>");
							content+="专案限制:该案件租赁成数"+companreValue+"%不在专案设定的租赁成数"+limitValue+"条件内!";
							logger.debug("该案件租赁成数"+companreValue+"%不在专案设定的租赁成数"+limitValue+"条件内");
						}
					} else if("SINGLE_MONEY".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//单案金额
						double leaseRZE=schemeMap==null||schemeMap.get("LEASE_RZE")==null||"".equals(schemeMap.get("LEASE_RZE"))?0.00:Double.valueOf(schemeMap.get("LEASE_RZE").toString());
						double limitValue=creditSpecialCaseProperty.get(i).getValue1()==null||
								"".equals(creditSpecialCaseProperty.get(i).getValue1())
								?0.00:Double.valueOf(creditSpecialCaseProperty.get(i).getValue1())*10000;
						if(leaseRZE>=limitValue) {
							logContent.append("专案限制:该案件概算成本"+dfm.format(leaseRZE)+"不在专案设定的单案金额"+dfm.format(limitValue)+"条件内<br>");
							content+="专案限制:该案件概算成本"+leaseRZE+"不在专案设定的单案金额"+limitValue+"条件内!";
							logger.debug("该案件概算成本"+leaseRZE+"不在专案设定的单案金额"+limitValue+"条件内");
						}
					} else if("CUSTOMER_MONEY_TOPLIMIT".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//承租人归户金额上限
						context.contextMap.put("data_type","客户来源");
						Map<String,Object> creditMap=(Map<String,Object>)DataAccessor.query("creditReportManage.selectCreditBaseInfo",context.contextMap,DataAccessor.RS_TYPE.MAP);
						if(creditMap==null||creditMap.get("CUST_ID")==null) {
							logContent.append("专案限制:该案件数据有误<br>");
							content+="专案限制:该案件数据有误!";
							logger.debug("该案件数据有误");
						} else {
							context.contextMap.put("NEWCUST_ID", creditMap.get("CUST_ID"));
							/*Map<String,Object> map=(Map<String,Object>)DataAccessor.query("beforeMakeContract.selectCustSumIrrMonthAndLastPrice",context.contextMap,DataAccessor.RS_TYPE.MAP);
							double compareValue=map.get("SHENGYUBENJIN")==null||"".equals(map.get("SHENGYUBENJIN"))?0.00:Double.valueOf(map.get("SHENGYUBENJIN").toString());*/
							double compareValue = LeaseUtil.getRemainingPrincipalByCustId(String.valueOf(creditMap.get("CUST_ID")));
							double limitValue=creditSpecialCaseProperty.get(i).getValue1()==null||"".equals(creditSpecialCaseProperty.get(i).getValue1())?0.00:Double.valueOf(creditSpecialCaseProperty.get(i).getValue1())*10000;
							if(compareValue>=limitValue) {
								logContent.append("专案限制:该案件客户归户金额:"+compareValue+"大于等于专案归户金额上限:"+limitValue+"<br>");
								content+="专案限制:该案件客户归户金额:"+compareValue+"大于等于专案归户金额上限:"+limitValue+"!";
								logger.debug("该案件客户归户金额:"+compareValue+"大于等于专案归户金额上限:"+limitValue);
							}
						}
					} else if("CUSTOMER_REGISTER_PERIOD".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						Map<String,Object> map=(Map<String,Object>)DataAccessor.query("creditCustomerCorp.getCreditCustomerCorpByCreditId",context.contextMap,DataAccessor.RS_TYPE.MAP);
						if(map==null||map.get("INCORPORATING_DATE")==null) {
							logContent.append("专案限制:该案件承租人成立日未维护与专案承租人成立年限限制不符<br>");
							content+="专案限制:该案件承租人成立日未维护与专案承租人成立年限限制不符!";
							logger.debug("该案件承租人成立日未维护与专案承租人成立年限限制不符");
						} else {
							Date d1=Calendar.getInstance().getTime();
							Date d2=DateUtil.strToDate(map.get("INCORPORATING_DATE").toString(),"yyyy-MM-dd HH:mm:ss");
							long diffMonth=d1.getTime()-d2.getTime();
							DecimalFormat df=new DecimalFormat("#.00");
							double compareValue=Double.valueOf(df.format(diffMonth/(1000*60*60*24)/30));
							double limitValue=creditSpecialCaseProperty.get(i).getValue1()==null||"".equals(creditSpecialCaseProperty.get(i).getValue1())?0.0:Double.valueOf(creditSpecialCaseProperty.get(i).getValue1());
							if(compareValue<limitValue) {
								logContent.append("专案限制:该案件承租人成立时间:"+compareValue+"月小于专案承租人成立年限:"+limitValue+"限制<br>");
								content+="专案限制:该案件承租人成立时间:"+compareValue+"月小于专案承租人成立年限:"+limitValue+"限制!";
								logger.debug("该案件承租人成立时间:"+compareValue+"月小于专案承租人成立年限:"+limitValue+"限制");
							}
						}
					} else if("CUSTOMER_AREA_LIMIT".equals(creditSpecialCaseProperty.get(i).getPropertyCode())
							   &&"Y".equals(creditSpecialCaseProperty.get(i).getCheckValue())) {
						//承租人地区限制
						context.contextMap.put("data_type","客户来源");
						Map<String,Object> creditMap=(Map<String,Object>)DataAccessor.query("creditReportManage.selectCreditBaseInfo",context.contextMap,DataAccessor.RS_TYPE.MAP);
						String provinceId=creditMap.get("PROVINCE_ID").toString();
						String cityId=creditMap.get("CITY_ID").toString();
						String areaId=creditMap.get("AREA_ID").toString();
						if(creditSpecialCaseProperty.get(i).getValue1()==null||creditSpecialCaseProperty.get(i).getValue2()==null) {
							content+=creditName+"承租人地区限制维护有误,请联系管理员";
							outputMap.put("content",content);
							Output.jsonOutput(outputMap,context);
							return;
						}
						String [] limitIds=creditSpecialCaseProperty.get(i).getValue1().split(",");
						String [] limitNames=creditSpecialCaseProperty.get(i).getValue2().split(",");
						if("-1".equals(limitIds[2])) {//区域
							if("-1".equals(limitIds[1])) {//城市
								if(provinceId.equals(limitIds[0])) {
									logContent.append("专案限制:该案件地区限制:"+limitNames[0]+"<br>");
									content+="专案限制:该案件地区限制:"+limitNames[0]+"!";
									logger.debug("该案件地区限制"+limitNames[0]);
								}
							} else {
								if(cityId.equals(limitIds[1])) {
									logContent.append("专案限制:该案件地区限制:"+limitNames[1]+"<br>");
									content+="专案限制:该案件地区限制:"+limitNames[1]+"!";
									logger.debug("该案件地区限制:"+limitNames[1]);
								}
							}
						} else {
							if(areaId.equals(limitIds[2])) {
								logContent.append("专案限制:该案件地区限制:"+limitNames[2]+"<br>");
								content+="专案限制:该案件地区限制:"+limitNames[2]+"!";
								logger.debug("该案件地区限制:"+limitNames[2]);
							}
						}
					}
				}
				
				param.clear();
				//加入专案卡关日志记录,如果日志内容长度不为空说明此案件有不满足专案条件,需要记录日志
				if(logContent.length()!=0) {
					
					param.put("s_employeeId",context.contextMap.get("s_employeeId"));
					param.put("creditId",context.contextMap.get("credit_id"));
					param.put("creditName",creditName);
					param.put("content",logContent);
					DataAccessor.execute("creditReportManage.insertCreditSpecialLog",param,OPERATION_TYPE.INSERT);
				}
				logger.debug("专案卡关结束");
			}
			}
		}catch(Exception e){
			LogPrint.getLogStackTrace(e,logger) ;
		}
		if(errList.isEmpty()){
			outputMap.put("content", content) ;
			Output.jsonOutput(outputMap, context) ;
		}else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
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
			context.contextMap.put("ip",context.contextMap.get("IP"));
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
			context.contextMap.put("ip",context.contextMap.get("IP"));
			
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
			sensorList=(List<Map>)DataAccessor.query("creditReportManage.getSensorList",context.contextMap,RS_TYPE.LIST);
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
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditCustomerCorp.selectCreditCustomerCorpForShow&credit_id="
					+ context.contextMap.get("credit_id")+"&examineFlag="+context.contextMap.get("examineFlag")+"&commit_flag="+context.contextMap.get("commit_flag")
					+ "&isSalesDesk="+context.contextMap.get("isSalesDesk"));
			
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void selectCreditForCommit(Context context) throws SQLException {
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
			if(schemeMap!=null&&(Constants.TAX_PLAN_CODE_4.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_6.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_7.equals(schemeMap.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_8.equals(schemeMap.get("TAX_PLAN_CODE")))) {
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
				}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_4.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_6.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_7.equals(schemeMap.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_8.equals(schemeMap.get("TAX_PLAN_CODE"))){
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
			
			//Add by Michael 2012 09-21 增加税费测算方案
			outputMap.put("taxPlanList", DictionaryUtil.getDictionary("税费方案"));
			
			//加入查询建议承做理由,其他租赁条件说明 add by ShenQi
			List<Map<String,Object>> contentList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",context.contextMap,RS_TYPE.LIST);
			outputMap.put("contentList",contentList);
			
			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1) {
			//加入专案卡关 TR,因为提交时候 实际TR会重新计算,所以卡关放在这里 add by ShenQi
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("credit_id",context.contextMap.get("credit_id"));
			param.put("propertyCode","LEASE_TR");
				List<CreditSpecialTO> creditSpecialCaseProperty=(List<CreditSpecialTO>)DataAccessor.query("creditReportManage.getCreditSpecialCasePropertyByCreditId",param,DataAccessor.RS_TYPE.LIST);
				if(creditSpecialCaseProperty==null||creditSpecialCaseProperty.size()==0) {
					//获得专案列表
					List<CreditSpecialTO> creditSpecialList=null;
					creditSpecialList=(List<CreditSpecialTO>)DataAccessor.query("creditSpecial.queryCreditSpecialGroup1",null,DataAccessor.RS_TYPE.LIST);
					outputMap.put("creditSpecialList",creditSpecialList);
					//do nothing
				} else {
					float minTr=creditSpecialCaseProperty.get(0).getValue1()==null||"".equals(creditSpecialCaseProperty.get(0).getValue1())?0:Float.valueOf(creditSpecialCaseProperty.get(0).getValue1().toString());
					float maxTr=creditSpecialCaseProperty.get(0).getValue2()==null||"".equals(creditSpecialCaseProperty.get(0).getValue2())?0:Float.valueOf(creditSpecialCaseProperty.get(0).getValue2().toString());
					float tr=Float.valueOf(paylist.get("TR_IRR_RATE").toString());
					if(tr<minTr||tr>maxTr) {
						//不满足专案条件  卡住
						context.contextMap.put("specialAlert","专案限制:该案件TR:"+tr+"不在专案TR:"+minTr+"~"+maxTr+"设定范围内");
						context.contextMap.remove("commit_flag");
						
						param.put("s_employeeId",context.contextMap.get("s_employeeId"));
						param.put("creditId",context.contextMap.get("credit_id"));
						param.put("creditName",creditSpecialCaseProperty.get(0).getCreditName());
						param.put("content","专案限制:该案件TR:"+tr+"不在专案TR:"+minTr+"~"+maxTr+"设定范围内<br>");
						DataAccessor.execute("creditReportManage.insertCreditSpecialLog",param,OPERATION_TYPE.INSERT);
						
						this.creditManage(context);
						return;
					}
					//获得专案列表
					List<CreditSpecialTO> creditSpecialList=null;
					creditSpecialList=(List<CreditSpecialTO>)DataAccessor.query("creditSpecial.queryCreditSpecialGroup1",null,DataAccessor.RS_TYPE.LIST);
					outputMap.put("creditSpecialList",creditSpecialList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--现场调查报告查询基本信息（修改页使用）错误!请联系管理员") ;
		} 
		if(errList.isEmpty()){
			outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
//			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
//				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
//				Output.jspOutput(outputMap, context, "/credit/creditFrameCommit.jsp");
//			}else{
//				Output.jspOutput(outputMap, context, "/credit/creditFrameShow.jsp");
//			}
			//根据报告类型
			int productionType = LeaseUtil.getProductionTypeByCreditId((String)context.contextMap.get("credit_id"));
			if(DataUtil.intUtil(context.contextMap.get("commit_flag"))==1){
				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));

				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameCommit.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameCommit.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameCommit.jsp");
				}
			}else{
				if(productionType==1){
					Output.jspOutput(outputMap, context, "/credit/equip/creditFrameShow.jsp");
				}else if(productionType==2){
					Output.jspOutput(outputMap, context, "/credit/truck/creditFrameShow.jsp");
				}else if(productionType==3){
					Output.jspOutput(outputMap, context, "/credit/car/creditFrameShow.jsp");
				}
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}
	
	public void creditOperationManage(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......creditOperationManage";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> pagingInfo=null;
		Map<String,Object> rsMap=null;
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("id",context.contextMap.get("s_employeeId"));
		
		try {
			
			rsMap=(Map<String,Object>)DataAccessor.query("employee.getEmpInforById",paramMap,DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode",rsMap.get("NODE"));
			context.contextMap.put("credit_type",5);//业务副总待审批的报告状态=5
			pagingInfo=baseService.queryForListWithPaging("creditReportManage.getCreditReportsTemp",context.contextMap,"CREDIT_RUNCODE",ORDER_TYPE.DESC);
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		List<TagTo> tagList = tagService.getAllTags(1);//案件类型
		outputMap.put("tags", tagList);
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("start_date",context.contextMap.get("start_date"));
		outputMap.put("end_date",context.contextMap.get("end_date"));
		Output.jspOutput(outputMap,context,"/credit/creditOpertionManager.jsp");
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	
//Add by Michael 2012 09-27  批量更新营业税改增值税
	public void selectCreditForUpdateForValueAddedTax(Context context) {
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
	
			Map baseRate = PayRate.getBaseRate();
			outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
			
			// irr month
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			outputMap.put("irrMonthPaylines", irrMonthPaylines);
			
			//Add by Michael 2012 1/9 报告修改进入时要重新计算TR-----------------------------------
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
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
				}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))){
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
			System.out.println("=================================================");
			System.out.println(paylist.get("TOTAL_VALUEADDED_TAX"));
			System.out.println("=================================================");
			outputMap.put("paylist", paylist);
			//-----------------------------------------------------------------------------------------
			paylist.put("credit_id", context.contextMap.get("credit_id"));
			DataAccessor.execute("creditPriorRecords.updateValueAddTax",paylist,OPERATION_TYPE.UPDATE);
			

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
	
		} finally {
			
		}
	}

	public void selectAllValueAddedTaxForModify(Context context) throws Exception {
		List listAllCreditIdList= (List)DataAccessor.query("creditPriorRecords.getAllForMDFCreditIDList",null,RS_TYPE.LIST);
		for (int i=0;i<listAllCreditIdList.size();i++){
			context.contextMap.put("credit_id",((Map) listAllCreditIdList.get(i)).get("CREDIT_ID"));
			selectCreditForUpdateForValueAddedTax(context);
		}
		
	}
	
	public void caseQuery(Context context) {
		PagingInfo<Object> pagingInfo=null;
		Map<String, Object> outputMap=new HashMap<String, Object>();
		double totalMoney=0;
		try {
			pagingInfo=baseService.queryForListWithPaging("creditReportManage.getCreditStatusInfoForSalesDesk",context.contextMap,"CREATE_DATE",ORDER_TYPE.DESC);
			if(pagingInfo!=null&&pagingInfo.getResultList()!=null
					&&pagingInfo.getResultList().size()>0) {
				CreditTo credit = null;
				for(Object o:pagingInfo.getResultList()){
					if(o==null) {
						continue;
					}
					credit=(CreditTo)o;
					totalMoney += (credit.getLeaseRze() == null ? 0 : credit.getLeaseRze());
				}
			}
		} catch(ServiceException e) {
			logger.error(e);
		}
		outputMap.put("totalMoney",totalMoney);
		outputMap.put("start_date",context.contextMap.get("start_date"));
		outputMap.put("end_date",context.contextMap.get("end_date"));
		outputMap.put("credit_status",context.contextMap.get("credit_status"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
		
		Output.jspOutput(outputMap, context,"/credit_vip/creditReportStatusInfo.jsp");
	}
	
	public void getAllUnCompletedFileCompany(Context context) {
		
		PagingInfo<Object> pagingInfo=null;
		Map<String, Object> outputMap=new HashMap<String, Object>();
		
		try {
			pagingInfo=baseService.queryForListWithPaging("rentFile.getAllUnCompletedFileCompany",context.contextMap,
					"FINANCECONTRACT_DATE",ORDER_TYPE.DESC);
		} catch(ServiceException e) {
			e.printStackTrace();
		}
		
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
		
		Output.jspOutput(outputMap,context,"/credit/unCompletedFileCompany.jsp");
	}
	
	//通过合同类型更新产品类别
	public void updateProductionType(Context context) {
		
		Map<String,Object> resultMap=null;
		context.contextMap.put("data_type","客户来源");
		try {
			resultMap=(Map)DataAccessor.query("creditReportManage.selectCreditBaseInfo",context.contextMap,DataAccessor.RS_TYPE.MAP);
			
			String contractType=null;
			String productionType=null;
			if(resultMap!=null) {
				contractType=resultMap.get("CONTRACT_TYPE").toString();
				
				if(Constants.CONTRACT_TYPE_2.equals(contractType)||Constants.CONTRACT_TYPE_5.equals(contractType)||Constants.CONTRACT_TYPE_7.equals(contractType)
						||Constants.CONTRACT_TYPE_9.equals(contractType)) {
					productionType=Constants.PRODUCTION_TYPE_1;
				} else if(Constants.CONTRACT_TYPE_4.equals(contractType)||Constants.CONTRACT_TYPE_11.equals(contractType)) {
					productionType=Constants.PRODUCTION_TYPE_2;
				} else if(Constants.CONTRACT_TYPE_6.equals(contractType)||Constants.CONTRACT_TYPE_8.equals(contractType)||Constants.CONTRACT_TYPE_10.equals(contractType)
						||Constants.CONTRACT_TYPE_12.equals(contractType)
						||Constants.CONTRACT_TYPE_13.equals(contractType)
						||Constants.CONTRACT_TYPE_14.equals(contractType)) {
					productionType=Constants.PRODUCTION_TYPE_3;//乘用车类型
				}
				
				Map<String,String> param=new HashMap<String,String>();
				param.put("creditId",context.contextMap.get("credit_id").toString());
				param.put("productionType",productionType);
				
				DataAccessor.execute("creditReportManage.updateProductionType",param,OPERATION_TYPE.UPDATE);
				Output.jsonFlageOutput(true,context);
				//不管成功或者是被或者异常都不影响报告提交
			} else {
				
				Output.jsonFlageOutput(false,context);
			}
			
		} catch (Exception e) {
			Output.jsonFlageOutput(false,context);
		}
	}
	
	public void checkDatePeriod(Context context) {
		//如果是专案,检核专案条件 add by ShenQi 2013-4-25
		boolean flag=false;
		String msg = null;
		String startDateDescr = null;
		String endDateDescr = null;
		String creditName = null;
		String creditCode = null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<CreditSpecialTO> creditSpecialCaseProperty=null;
		String credit_id = (String) context.contextMap.get("credit_id");
		try {
			creditSpecialCaseProperty=(List<CreditSpecialTO>)DataAccessor.query("creditReportManage.getCreditSpecialCasePropertyByCreditId",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			logger.error(e);
		}
		if(creditSpecialCaseProperty==null||creditSpecialCaseProperty.size()==0) {
			/*outputMap.put("flag",flag);
			outputMap.put("creditCode","无");
			outputMap.put("creditName","无");
			Output.jsonOutput(outputMap,context);
			return;*/
		} else {
			//专案有效期
			Calendar dt=Calendar.getInstance();
			startDateDescr=creditSpecialCaseProperty.get(0).getStartDateDescr();
			endDateDescr=creditSpecialCaseProperty.get(0).getEndDateDescr();
			creditName=creditSpecialCaseProperty.get(0).getCreditName();
			creditCode=creditSpecialCaseProperty.get(0).getCreditCode();
			if(DateUtil.strToDate(DateUtil.dateToString(dt.getTime(),"yyyy-MM-dd"),"yyyy-MM-dd").compareTo(DateUtil.strToDate(startDateDescr,"yyyy-MM-dd"))==-1||
					DateUtil.strToDate(endDateDescr,"yyyy-MM-dd").compareTo(DateUtil.strToDate(DateUtil.dateToString(dt.getTime(),"yyyy-MM-dd"),"yyyy-MM-dd"))==-1) {
				flag=true;
				msg = "当前日期不在专案有效期内";
				logger.debug(msg);
			}
		}
		/*
		//判断供应商发票待补情况
		//1.加入发票管理系统
		try {
			invoiceManageService.doAddProject(credit_id, Constants.SYSTEM_ID);
		} catch (ProcessException e) {
			e.printStackTrace();
			logger.warn(e);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
		
		try {
			String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
			CreditLineTO invoiceLine = LeaseUtil.getInvoiceLineForSupl(supl_id);
			int invoiceCount = 0;
			long now = new Date().getTime();
			if (invoiceLine == null || invoiceLine.getStatus() != 0 
					|| StringUtils.isEmpty(invoiceLine.getHasLine()) 
					|| "N".equals(invoiceLine.getHasLine())) {
				throw new ProcessException("未授信");
			}
			if (now < invoiceLine.getStartDate().getTime() || now > invoiceLine.getEndDate().getTime()) {
				throw new ProcessException("授信已过期");
			}
			if (invoiceLine.getRepeatFlag() == 1) {
				//循环
				invoiceCount = LeaseUtil.getBeingInvoiceBySuplId(supl_id);
			} else {
				//不循环
				invoiceCount = LeaseUtil.getAllInvoiceBySuplId(supl_id);
			}
			if (invoiceCount > invoiceLine.getLine()) {
				throw new ProcessException("授信额度不足");
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = true;
			msg = "发票授信：" + e.getMessage();
		}
		*/
		outputMap.put("flag",flag);
		outputMap.put("msg",msg);
		outputMap.put("creditCode",creditCode);
		outputMap.put("creditName",creditName);
		Output.jsonOutput(outputMap,context);
	}
	
	public void checkLockCode(Context context) throws Exception{
		Map<String, String> outputMap = new HashMap<String, String>();
		String msg = null;
		List<Integer> resultList = null;
		resultList = (List<Integer>) baseService.queryForList("creditReportManage.getLockCodeBySupp", context.contextMap);
		boolean has1 = false;
		boolean has2 = false;
		boolean has3 = false;
		boolean has4 = false;
		for (Integer integer : resultList) {
			if (integer == 1) {
				has1 = true;
			}
			if (integer == 2) {
				has2 = true;
			}
			if (integer == 3) {
				has3 = true;
			}
			if (integer == 4) {
				has4 = true;
			}
		}
		if (has3 && has1) {
			msg = "该供应设备部分可直接锁码";
		} else if (has3) {
			msg = "该供应商设备可直接锁码";
		} else if (has1) {
			msg = "该供应商设备可间接锁码";
		} else if (has4) {
			msg = "该供应商设备不可锁码";
		}
		outputMap.put("msg", msg);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * @param  业务员
	 * @param supplierId 选取第一个购买意向
	 */
	public void getSupplierContact(Context context){
		Map outputMap = new HashMap();
		try {	
			context.contextMap.put("createUserId", context.getContextMap().get("s_employeeId"));
			//context.contextMap.put("suppl_id",suplMap.get(0).get("ID"));
			List supplLinkman =(List)DataAccessor.query("supplier.querySupplLinkManByUserId", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    outputMap.put("supplLinkman", supplLinkman);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
				Output.jsonOutput(outputMap, context);
		}

	}
	
	public void bindTags(Context context) throws Exception{
		String project_id = (String) context.contextMap.get("project_id");
		String [] tags = (String[]) context.request.getParameterValues("tag");
		int [] tagids = null;
		if(tags!=null){
			tagids = new int[tags.length];
			for(int i =0,len=tags.length;i<len;i++){
				tagids[i] = Integer.parseInt(tags[i]);
			}
		}
		try{
			tagService.saveTag2Prjt_Credit(Integer.parseInt(project_id),1,tagids,String.valueOf(context.contextMap.get("s_employeeId")),(String)context.contextMap.get("IP"));
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("success", true);
		Output.jsonOutput(outputMap, context);
	}
	
	public void queryPdfFile(Context context){
		String credit_id = (String) context.contextMap.get("credit_id");
		String vip = (String) context.contextMap.get("vip");
		Map<String, Object> outputMap=new HashMap<String, Object>();
		outputMap.put("credit_id", credit_id);
		outputMap.put("vip", vip);
		Output.jspOutput(outputMap, context, "/credit/creditManageFile.jsp");
	}
	
	public void updateEstimatesPayDate(Context context){
		creditReportService.doUpdateEstimatesPayDate(context);
		creditManage(context);
    }
	
	public void getEstimatesPayDate(Context context) throws SQLException{
		Map<String, Object> result = (Map<String, Object>) baseService.queryForObj("creditReportManage.getEstimatesPayDate", context.contextMap);
		if (result != null) {
			String taxPlanCode = LeaseUtil.getTaxPlanCodeByCreditId((String)context.contextMap.get("credit_id"));
			if("5".equals(taxPlanCode)){//乘用车委贷
				Date date  = null;//起租日期
				//判断支付表是否生成
				date = (Date) baseService.queryForObj("rentContract.getStartDateByCreditId",context.contextMap);
				//合同方案表中拿
				if(date==null){
					date = (Date) baseService.queryForObj("rentContract.getContractStartDateByCreditId",context.contextMap);
				}
				if(date==null){
					date = (Date) result.get("START_DATE");					
				}
				if(date!=null){
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					result.put("START_DATE", df.format(date));
				}
			}else{
				result.put("START_DATE", "");
			}
			
			JSONArray jsonArray = new JSONArray();
			List<Map<String, Object>> logList = (List<Map<String, Object>>) baseService.queryForList("creditReportManage.getEstimatesPayDateLog", context.contextMap);
			for (Map<String, Object> map : logList) {
				map.put("CREATE_TIME", DateUtil.dateToString((Timestamp)map.get("CREATE_TIME"), "yyyy-MM-dd HH:mm"));
				JSONObject jsonObj = JSONObject.fromObject(map);
				jsonArray.add(jsonObj);
			}
			result.put("log", jsonArray);
		}
		Output.jsonOutput(result, context);
	}

	
	public void exportCustomerVerifiedBook(Context context) throws Exception{
		
		String id = (String) context.contextMap.get("credit_id");
		int credit_id =  Integer.parseInt(id);
		Map param = new HashMap();
		param.put("credit_id", credit_id);
		param.put("data_type", "客户来源");
		param.put("dictionaryType", "支付方式");
		param.put("payway", "支付方式");
		
		List<Map> equipmentsList = (List<Map>) DataAccessor.query(
				"creditReportManage.selectCreditEquipment",
				param, DataAccessor.RS_TYPE.LIST);
		
		Map schemeMap = (Map) DataAccessor.query(
				"creditReportManage.selectCreditScheme",
				param, DataAccessor.RS_TYPE.MAP);
		
		List<Map> payWayList = (List<Map>) DataAccessor.query("creditCustomer.getItems", param, DataAccessor.RS_TYPE.LIST);
		
		Map creditMap = (Map) DataAccessor.query(
				"creditReportManage.selectCreditBaseInfo",
				param, DataAccessor.RS_TYPE.MAP);
		
		List<Map> corpList = (List<Map>) DataAccessor.query(
				"creditVoucher.selectCorpByCreditId", param,
				DataAccessor.RS_TYPE.LIST);
		List<Map> natuList = (List<Map>) DataAccessor.query(
				"creditVoucher.selectVouchNatu", param,
				DataAccessor.RS_TYPE.LIST);
		
		List<Map> feeListRZE = (List<Map>) DataAccessor.query("creditReportManage.getCreditFeeListRZE",param, DataAccessor.RS_TYPE.LIST);
		List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(credit_id, Integer.valueOf(1));

	
		
		//Add by Michael 2012 1/9 报告修改进入时要重新计算TR-----------------------------------
		// 解压irrMonthPaylines到每一期的钱
		List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
		Map paylist = null;
		if (schemeMap != null) {
			//Add by Michael 2012 01/29 在方案里增加合同类型
			schemeMap.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
			//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
			double totalFeeSet=0.0d;
			
			if("2".equals(schemeMap.get("TAX_PLAN_CODE"))){
				List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",param, DataAccessor.RS_TYPE.LIST);
				for(Map map:listTotalFeeSet){
					totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
				}	
			}else if("1".equals(schemeMap.get("TAX_PLAN_CODE"))||"3".equals(schemeMap.get("TAX_PLAN_CODE"))){
				totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",param, DataAccessor.RS_TYPE.OBJECT);
			}
			
			schemeMap.put("FEESET_TOTAL", totalFeeSet);
			//-----------------------------------------------------------------------------
			
			schemeMap.put("TOTAL_PRICE", schemeMap.get("LEASE_TOPRIC"));
			schemeMap.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
			schemeMap.put("LEASE_TERM", schemeMap.get("LEASE_COURSE"));
			 if(schemeMap.get("payList")==null){
				 schemeMap.put("payList", rePaylineList);
			 }
			// 
			if (irrMonthPaylines.size() > 0) {
				// 如果应付租金存在，则以应付租金的方式计算
				paylist = StartPayService.createCreditPaylistIRR(schemeMap,rePaylineList,irrMonthPaylines);
			} else {
				// 如果应付租金不存在，则以年利率(合同利率)的方式计算
				paylist = StartPayService.createCreditPaylist(schemeMap,new ArrayList<Map>());
			}
		}


		List<Map> list = null;
		if (paylist!=null){
			paylist.put("irrMonthPaylines", irrMonthPaylines);
			Quotation.packagePaylinesForValueAdded(paylist);
			list = (List<Map>) paylist.get("irrMonthPaylines");
		}


			
		Map<String, Object> linkMan = new HashMap<String, Object>();


		Map<String, Object> paraMapForLinMan = new HashMap<String, Object>();
		paraMapForLinMan.put("cust_id", creditMap.get("CUST_ID"));
		List<Map<String, Object>> linkManTempList = (List) DataAccessor.query("customer.getDefaultLinkMan", paraMapForLinMan, RS_TYPE.LIST);
		if (linkManTempList != null && linkManTempList.size() == 1) {
			linkMan = linkManTempList.get(0);
		} else {
			logger.info("没有默认联系人，或者错误地设置了2个或以上默认的联系人！");
		}
		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
		float[] widthsStl = {0.2f,0.4f,0.1f,0.4f,0.4f,0.1f,0.4f,0.2f};
		PdfPTable table = new PdfPTable(widthsStl);
		table.setWidthPercentage(100f);
		
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		Font textFont = new Font(bfChinese, 10, Font.NORMAL);
		Font headFont = new Font(bfChinese, 10, Font.BOLD);
		
		NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
		nfFSNum.setGroupingUsed(true);
		nfFSNum.setMaximumFractionDigits(2);
		// 页面设置
		Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
		com.lowagie.text.Document document = new com.lowagie.text.Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);
		
		document.open();
		
		String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
		imageUrl = imageUrl.substring(6,imageUrl.length()-16)+"images/logo.jpg";
		Image image = Image.getInstance(imageUrl);

		image.scaleAbsoluteHeight(80);
		image.scaleAbsoluteWidth(150);		
		PdfPCell imageCell = new PdfPCell();
		imageCell.setColspan(8);
		imageCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		imageCell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
		imageCell.setBorder(0);
		imageCell.addElement(image);
		table.addCell(imageCell);
		
		table.addCell(makeCellSetColspanWithNoBorder("客  户  确  认   函",PdfPCell.ALIGN_CENTER,titleFont,8));

		

		table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,8));

		
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		table.addCell(makeCellSetColspanWithNoBorder("致："+creditMap.get("CUST_NAME").toString(),PdfPCell.ALIGN_LEFT,titleFont,6));
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		table.addCell(makeCellSetColspan("租赁物明细",PdfPCell.ALIGN_CENTER,headFont,6));
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		table.addCell(makeCellSetColspan("厂牌",PdfPCell.ALIGN_CENTER,headFont,2));
		table.addCell(makeCellSetColspan("产品名称",PdfPCell.ALIGN_CENTER,headFont,0));
		table.addCell(makeCellSetColspan("型号",PdfPCell.ALIGN_CENTER,headFont,0));
		table.addCell(makeCellSetColspan("数量",PdfPCell.ALIGN_CENTER,headFont,0));
		table.addCell(makeCellSetColspan("供货商",PdfPCell.ALIGN_CENTER,headFont,0));
		table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
		Double  staybuy_price= 0d;
        for(Map equipment:equipmentsList){
        	 table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        	 table.addCell(makeCellSetColspan(equipment.get("THING_KIND").toString(),PdfPCell.ALIGN_CENTER,textFont,2));
        	 table.addCell(makeCellSetColspan(equipment.get("THING_NAME").toString(),PdfPCell.ALIGN_CENTER,textFont,0));
        	 table.addCell(makeCellSetColspan(equipment.get("MODEL_SPEC").toString(),PdfPCell.ALIGN_CENTER,textFont,0));
        	 table.addCell(makeCellSetColspan(equipment.get("AMOUNT").toString(),PdfPCell.ALIGN_CENTER,textFont,0));
        	 table.addCell(makeCellSetColspan(equipment.get("BRAND").toString(),PdfPCell.ALIGN_CENTER,textFont,0));
        	 table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        	 if(staybuy_price!=null && staybuy_price==0d){
        		 staybuy_price = (Double) equipment.get("STAYBUY_PRICE");
        	 }
         }
    	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
	   	table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,2));
	   	table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,0));
	   	table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,0));
	   	table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,0));
	   	table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,0));
	    table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
	    Double money = schemeMap!=null&&schemeMap.get("LEASE_TOPRIC")!=null?(Double) schemeMap.get("LEASE_TOPRIC"):0d;
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("目标物总价" ,PdfPCell.ALIGN_LEFT,headFont,2));
        table.addCell(makeCellSetColspan("人民币"+nfFSNum.format(money)+"元（含税）" ,PdfPCell.ALIGN_LEFT,textFont,4));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        Double first_paymoney = schemeMap!=null&&schemeMap.get("PLEDGE_AVE_PRICE")!=null?(Double) schemeMap.get("PLEDGE_AVE_PRICE"):0d;
        BigDecimal precent = new BigDecimal(0);
        if(first_paymoney>0d){
        	precent = new BigDecimal(first_paymoney).multiply(new BigDecimal(100d)).divide(new BigDecimal(money),2,BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("首付款" ,PdfPCell.ALIGN_LEFT,headFont,2));
        table.addCell(makeCellSetColspan("自备款"+ String.valueOf(precent) +"%，"+ nfFSNum.format(first_paymoney) +"元" ,PdfPCell.ALIGN_LEFT,textFont,4));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("融资金额" ,PdfPCell.ALIGN_LEFT,headFont,2));
        table.addCell(makeCellSetColspan(nfFSNum.format(LeaseUtil.getPayMoneyByCreditId(id)) +"元" ,PdfPCell.ALIGN_LEFT,textFont,4));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        Double collateral = schemeMap!=null&&schemeMap.get("PLEDGE_PRICE")!=null?(Double) schemeMap.get("PLEDGE_PRICE"):0d;
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("保证金(期末返还，抵充)" ,PdfPCell.ALIGN_LEFT,headFont,2));
        table.addCell(makeCellSetColspan(nfFSNum.format(collateral) +"元",PdfPCell.ALIGN_LEFT,textFont,4));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        BigDecimal LEASE_PERIOD = schemeMap!=null&&schemeMap.get("LEASE_PERIOD")!=null?(BigDecimal) schemeMap.get("LEASE_PERIOD"):new BigDecimal(0);
        
        String payway = schemeMap!=null&&schemeMap.get("PAY_WAY")!=null?schemeMap.get("PAY_WAY").toString():"";
        for(Map map:payWayList){
        	if(map.get("CODE").equals(payway)){
        		payway = (String) map.get("FLAG");
        		break;
        	}
        }
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("租赁期限" ,PdfPCell.ALIGN_LEFT,headFont,2));
        table.addCell(makeCellSetColspan(LEASE_PERIOD!=null?LEASE_PERIOD.toString()+"期；每期租金（" + payway +"）":"-",PdfPCell.ALIGN_LEFT,textFont,4));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));

        

        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("期次" ,PdfPCell.ALIGN_CENTER,headFont,3));
        table.addCell(makeCellSetColspan("应付租金" ,PdfPCell.ALIGN_CENTER,headFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        if(list!=null){
	        for(Map map:list){
	        	Double IRR_MONTH_PRICE =  map.get("IRR_MONTH_PRICE")!=null?(Double) map.get("IRR_MONTH_PRICE"):0d;
	        	Double VALUE_ADDED_TAX =  map.get("VALUE_ADDED_TAX")!=null?(Double) map.get("VALUE_ADDED_TAX"):0d;
	        	Double MONTH_PRICE_TAX =  map.get("MONTH_PRICE_TAX")!=null?(Double) map.get("MONTH_PRICE_TAX"):0d;
	            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
	            table.addCell(makeCellSetColspan("第"+ String.valueOf(map.get("IRR_MONTH_PRICE_START")) +"-"+String.valueOf(map.get("IRR_MONTH_PRICE_END"))+"期" ,PdfPCell.ALIGN_CENTER,textFont,3));
	            table.addCell(makeCellSetColspan("每期"+nfFSNum.format(MONTH_PRICE_TAX)+"元"  ,PdfPCell.ALIGN_CENTER,textFont,3));
	            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
	        }
        }

        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan(" " ,PdfPCell.ALIGN_CENTER,textFont,3));
        table.addCell(makeCellSetColspan(" " ,PdfPCell.ALIGN_CENTER,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("每期租赁款支付" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan("首次租赁款是租赁合同生效日起，每月支付，共"+(LEASE_PERIOD!=null?LEASE_PERIOD.toString():"-")+"期" ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("应收费用" ,PdfPCell.ALIGN_CENTER,headFont,6));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        BigDecimal manageMoney = new BigDecimal(0);
        if(feeListRZE!=null&&feeListRZE.size()>0){
        	for(Map m:feeListRZE){
        		BigDecimal fee  = (BigDecimal)(m.get("FEE")!=null?m.get("FEE"):new BigDecimal(0));
        		manageMoney = manageMoney.add(fee);
        	}
        }
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("管理费" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan("人民币"+nfFSNum.format(manageMoney)+"元" ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        

       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("期满购买金" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan("人民币"+nfFSNum.format(staybuy_price)+"元" ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        BigDecimal totalRenPriceDouble = new BigDecimal(0); 
        if(list!=null){
    		for (int i=0; i<list.size(); i++) {
    			Double MONTH_PRICE_TAX =  list.get(i).get("MONTH_PRICE_TAX")!=null?(Double) list.get(i).get("MONTH_PRICE_TAX"):0d;
    			Integer start = (Integer) list.get(i).get("IRR_MONTH_PRICE_START");
    			Integer end = (Integer) list.get(i).get("IRR_MONTH_PRICE_END");
    			totalRenPriceDouble = new BigDecimal(MONTH_PRICE_TAX).multiply(new BigDecimal(end - start + 1)).add(totalRenPriceDouble);			
    		}

        }
		BigDecimal REN_PRICES = new BigDecimal(0); 
		if(paylist!=null){
			List<Map> paylines = (List<Map>) paylist.get("paylines");
	        for(Map payline:paylines){
	        	Double ren_price = payline.get("REN_PRICE")!=null?(Double)payline.get("REN_PRICE"):0d;
	        	REN_PRICES = REN_PRICES.add(new BigDecimal(ren_price));
	        }
		}

        REN_PRICES.setScale(2, BigDecimal.ROUND_HALF_UP);
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("租赁费支付总额" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan("人民币"+nfFSNum.format(totalRenPriceDouble)+"元(含税利息"+nfFSNum.format(REN_PRICES)+"元)（未含税利息"+nfFSNum.format(REN_PRICES.divide(new BigDecimal(1.17), 2,BigDecimal.ROUND_HALF_UP))+"元）" ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        String address = schemeMap!=null?(String)schemeMap.get("EQUPMENT_ADDRESS"):"";
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("租赁物放置地" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan(address ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));

        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("连带保证人（签约时需同时在场）",PdfPCell.ALIGN_LEFT,headFont,6));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("姓名/公司名称" ,PdfPCell.ALIGN_CENTER,headFont,0));
        table.addCell(makeCellSetColspan("身份证号码/营业执照编号" ,PdfPCell.ALIGN_CENTER,headFont,2));
        table.addCell(makeCellSetColspan("身份证地址/公司注册地址" ,PdfPCell.ALIGN_CENTER,headFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        for(Map natu:natuList){
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
            table.addCell(makeCellSetColspan(natu.get("CUST_NAME").toString() ,PdfPCell.ALIGN_CENTER,textFont,0));
            table.addCell(makeCellSetColspan(natu.get("NATU_IDCARD").toString() ,PdfPCell.ALIGN_CENTER,textFont,2));
            table.addCell(makeCellSetColspan(natu.get("NATU_IDCARD_ADDRESS").toString() ,PdfPCell.ALIGN_CENTER,textFont,3));
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        }
        
        for(Map corp:corpList){
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
            table.addCell(makeCellSetColspan(corp.get("CORP_NAME_CN").toString() ,PdfPCell.ALIGN_CENTER,textFont,0));
            table.addCell(makeCellSetColspan(corp.get("BUSINESS_LICENCE_CODE").toString() ,PdfPCell.ALIGN_CENTER,textFont,2));
            table.addCell(makeCellSetColspan(corp.get("REGISTERED_OFFICE_ADDRESS").toString() ,PdfPCell.ALIGN_CENTER,textFont,3));
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        }
        
        if(natuList.size()==0 && corpList.size()==0){
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
            table.addCell(makeCellSetColspan(" " ,PdfPCell.ALIGN_CENTER,textFont,0));
            table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,2));
            table.addCell(makeCellSetColspan(" ",PdfPCell.ALIGN_CENTER,textFont,3));
            table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        }
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("联系方式",PdfPCell.ALIGN_LEFT,headFont,6));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("联系人" ,PdfPCell.ALIGN_CENTER,headFont,0));
        table.addCell(makeCellSetColspan("电话" ,PdfPCell.ALIGN_CENTER,headFont,2));
        table.addCell(makeCellSetColspan("合同、发票寄送地址" ,PdfPCell.ALIGN_CENTER,headFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
      	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan(String.valueOf(linkMan.get("LINK_NAME")!=null?linkMan.get("LINK_NAME"):"") ,PdfPCell.ALIGN_CENTER,textFont,0));
        table.addCell(makeCellSetColspan(linkMan.get("LINK_MOBILE")!=null?String.valueOf(linkMan.get("LINK_MOBILE")):String.valueOf(linkMan.get("LINK_PHONE")!=null?linkMan.get("LINK_PHONE"):""),PdfPCell.ALIGN_CENTER,textFont,2));
        table.addCell(makeCellSetColspan(String.valueOf(linkMan.get("LINK_WORK_ADDRESS")!=null?linkMan.get("LINK_WORK_ADDRESS"):"") ,PdfPCell.ALIGN_CENTER,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
       
        
       	table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspan("备注：" ,PdfPCell.ALIGN_LEFT,headFont,0));
        table.addCell(makeCellSetColspan("请核对以上数据无误及租赁合同确认后，加盖公章回传，                                                            我司将派业务员前往签订合约" ,PdfPCell.ALIGN_LEFT,textFont,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
      	table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,4));
        table.addCell(makeCellSetColspanWithNoBorder("裕融租赁有限公司" ,PdfPCell.ALIGN_LEFT,headFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
      	table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,4));
        table.addCell(makeCellSetColspanWithNoBorder("苏州工业园区圆融时代广场民生金融大厦23栋" ,PdfPCell.ALIGN_LEFT,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        String sensor_telephone = creditMap.get("SENSOR_TELEPHONE")!=null&&!"".equals(creditMap.get("SENSOR_TELEPHONE"))?"#"+creditMap.get("SENSOR_TELEPHONE").toString():"";
      	table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,4));
        table.addCell(makeCellSetColspanWithNoBorder("电话：0512-80983566" + sensor_telephone,PdfPCell.ALIGN_LEFT,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        
      	table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,4));
        table.addCell(makeCellSetColspanWithNoBorder("传真：0512-80983567" ,PdfPCell.ALIGN_LEFT,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));

//        String name = creditMap.get("SENSOR_NAME")!=null?creditMap.get("SENSOR_NAME").toString():"";
//        String mobile = creditMap.get("SENSOR_MOBILE")!=null?creditMap.get("SENSOR_MOBILE").toString():"";
        String servicerId = LeaseUtil.getServiceUserIdByCreditId(id);
        String name =LeaseUtil.getUserNameByUserId(servicerId);
        String mobile =LeaseUtil.getMobileByUserId(servicerId);
      	table.addCell(makeCellSetColspanWithNoBorder(" ",PdfPCell.ALIGN_CENTER,titleFont,4));
        table.addCell(makeCellSetColspanWithNoBorder("经办："+(name!=null?name:"")+" " +(mobile!=null?mobile:""),PdfPCell.ALIGN_LEFT,textFont,3));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder("------------------------------------------------------------------------------------------------------------------------------------------------------",PdfPCell.ALIGN_CENTER,new Font(bfChinese, 8, Font.NORMAL),6));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder("致：裕融租赁有限公司" ,PdfPCell.ALIGN_LEFT,titleFont,3));
        table.addCell(makeBoldBorderCellSetSpan("\n \n \n 盖章处" ,PdfPCell.ALIGN_CENTER,new Font(bfChinese, 8, Font.NORMAL),0,4));
        table.addCell(makeBoldBorderCellSetSpan(creditMap.get("CUST_NAME").toString()+"      \n \n \n \n \n \n  \n                                            年        月       日 ",PdfPCell.ALIGN_CENTER,new Font(bfChinese, 8, Font.NORMAL),2,5));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder("用印人兹确认以上数据及租赁合同" ,PdfPCell.ALIGN_LEFT,new Font(bfChinese, 14, Font.NORMAL),3));    
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder("皆正确无误。" ,PdfPCell.ALIGN_LEFT,new Font(bfChinese, 14, Font.NORMAL),3));    
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder(" " ,PdfPCell.ALIGN_LEFT,new Font(bfChinese, 14, Font.NORMAL),3));    
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        table.addCell(makeCellSetColspanWithNoBorder(" " ,PdfPCell.ALIGN_LEFT,new Font(bfChinese, 14, Font.NORMAL),3));    
        table.addCell(makeCellWithNoBorder(" ",PdfPCell.ALIGN_CENTER,textFont));
        
        document.add(table);
        
		document.add(Chunk.NEXTPAGE);
		
		document.close();
		context.response.setContentType("application/pdf");
		context.response.setCharacterEncoding("UTF-8");
		context.response.setHeader("Pragma", "public");
		context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		context.response.setDateHeader("Expires", 0);
		context.response.setHeader("Content-Disposition","attachment; filename=客户确认函");
		
		ServletOutputStream o = context.response.getOutputStream();

		baos.writeTo(o); 
		o.flush();				
		o.close();
	}
	
	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorder(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}
	/** 创建 有边框 合并 单元格 */
	private PdfPCell makeCellSetColspan(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		return objCell;
	}
	/** 创建 无边框 合并 单元格 */
	private PdfPCell makeCellSetColspanWithNoBorder(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
		return objCell;
	}
	
    /** 创建 有边框 合并 单元格|_
     *  无上边 
     *  
     *  */
    private PdfPCell makeCellSetColspanNoTop(String content, int align,
	    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthTop(0.0f);
		return objCell;
    }
    /** 创建 有边框 合并 单元格
     *  无下边
     *  
     *  */
    private PdfPCell makeCellSetColspanNoBottom(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0.0f);
	return objCell;
    }
    
    /** 创建 有边框 合并 单元格|_
     *  
     *  只有下跟右边
     *  */
    private PdfPCell makeCellSetColspanNoTopAndLeft(String content, int align,
	    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthTop(0.0f);
		objCell.setBorderWidthLeft(0.0f);
		return objCell;
    }
    /** 创建 有边框 合并 单元格 |
     *  只有右边
     *  
     *  */
    private PdfPCell makeCellSetColspanNoBottomAndLeft(String content, int align,
	    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0.0f);
		objCell.setBorderWidthTop(0.0f);
		objCell.setBorderWidthLeft(0.0f);
		return objCell;
    }
    
    private PdfPCell makeBoldBorderCellSetSpan(String content, int align,
    	    Font FontDefault, int colspan,int rowSpan){
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	//objCell.setRowspan(rowSpan);
    	objCell.setBorderWidth(1.5f);
    	return objCell;
    }
    
    public void checkBankCharge(Context context) throws SQLException{
    	String creditId = (String) context.contextMap.get("creditId");
    	String taxPlanCode = LeaseUtil.getTaxPlanCodeByCreditId(creditId);
    	boolean flag = false;
    	if("5".equals(taxPlanCode)){//乘用车委贷增加银行手续费判断
    		double money = LeaseUtil.getBankCharge(creditId);
    		if(money>0d){
    			flag = true;
    		}
    	}else{
    		flag = true;
    	}
    	Output.jsonFlageOutput(flag, context);
    } 
    
}