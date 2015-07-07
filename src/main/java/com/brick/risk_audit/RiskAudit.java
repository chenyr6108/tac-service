package com.brick.risk_audit;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.BaseTo;
import com.brick.base.to.CreditLineTO;
import com.brick.base.to.GuiHuInfo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.log.service.LogPrint;
import com.brick.project.service.TagService;
import com.brick.project.to.TagTo;
import com.brick.risk_audit.service.RiskAuditService;
import com.brick.risk_audit.to.RiskAuditTo;
import com.brick.risk_audit.to.ScoreCardTO;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;



/**
 * 
 * @author 吴振东
 * @date 下午12:54:45
 */
public class RiskAudit extends BaseCommand{
	Log logger = LogFactory.getLog(RiskAudit.class);
	
	private RiskAuditService riskAuditService;
	
	private TagService tagService;
	
	

	public TagService getTagService() {
		return tagService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}
	
	public RiskAuditService getRiskAuditService() {
		return riskAuditService;
	}
	public void setRiskAuditService(RiskAuditService riskAuditService) {
		this.riskAuditService = riskAuditService;
	}
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	/**
	 * 评审管理查看页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void riskAuditShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		Map levelMap = new HashMap();
		boolean export1=false;//导出核准函
		boolean export2=false;//导出评审统计Excel
//-- Add BY Michael 增加权限，业务员只能看到自己的案件------------------------
		Map paramMap = new HashMap();
		Map rsMap = null;
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		try {
			rsMap = (Map) baseService.queryForObj("employee.getEmpInforById", paramMap);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
//------------------------------------------------------------------------
			
			// 2012/06/21 Yang Yun 增加办事处查询
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
			
			//绑定服务课人员，默认绿色通道筛选条件
			Map<String, Object> selectMap = new HashMap<String, Object>();
			selectMap.put("dept_id", 30);
			selectMap.put("user_id", context.contextMap.get("s_employeeId"));
			Integer flag = (Integer) baseService.queryForObj("riskAudit.isThisDept", selectMap);
			if (flag != null && flag == 1 && context.contextMap.get("vip_flag") == null) {
				context.contextMap.put("vip_flag", 1);
			}
			
			pagingInfo = baseService.queryForListWithPaging("riskAudit.getRiskAudits", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			List<TagTo> tagList = tagService.getAllTags(1);//案件类型
			outputMap.put("tags", tagList);
			//等级配置
			context.contextMap.put("rank", 1);
			levelMap = (Map) baseService.queryForObj("riskAudit.selectLevelMap",context.contextMap);
			
			//通过emplId获得用户的ResourceId.(add by ShenQi,2012-03-09)
			List<String> resourceIdList = (List<String>) baseService.queryForList("supplier.getResourceIdListByEmplId", context.contextMap);
			/*ResourceId               Permission  
	         *177                                                           导出核准函
		     *178                                                           导出评审统计Excel
		     * */
			
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				//below is hard code for ResourceId,we will enhance it in the future
				if("177".equals(resourceIdList.get(i))) {
					export1=true;
				} else if("178".equals(resourceIdList.get(i))) {
					export2=true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("一级评审管理页错误!请联系管理员");
		}
		
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("levelMap", levelMap);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("wind_state", context.contextMap.get("wind_state"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
		outputMap.put("rate_of_progress", context.contextMap.get("rate_of_progress"));
		outputMap.put("export1",export1);
		outputMap.put("export2",export2);
		
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/risk_audit/risk_auditManagerShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 2012/03/22 Yang Yun
	 * 初级评审管理页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void riskAudit(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		Map levelMap = new HashMap();
		try {
			/*2012/02/08 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
			Map<String, Object> rsMap = null;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (rsMap == null || rsMap.get("NODE") == null) {
				throw new Exception("Session lost");
			}
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			
			// 2012/06/21 Yang Yun 增加办事处查询
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
			String wind_state = (String) context.contextMap.get("wind_state");
			wind_state = wind_state == null ? "-1" : wind_state;
			context.contextMap.put("wind_state", wind_state);
			pagingInfo = baseService.queryForListWithPaging("riskAudit.getRiskAudits", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			
			
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			List<TagTo> tagList = tagService.getAllTags(1);//案件类型
			outputMap.put("tags", tagList);
			//等级配置
			context.contextMap.put("rank", 1);
			levelMap = (Map) DataAccessor.query("riskAudit.selectLevelMap",context.contextMap, DataAccessor.RS_TYPE.MAP);		
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("一级评审管理页错误!请联系管理员");
		}
		
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("levelMap", levelMap);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("wind_state", context.contextMap.get("wind_state"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/risk_audit/risk_auditManager.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	
	/**
	 * 一、二、三、四级评审管理页面
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void riskAuditAfter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		Map levelMap = new HashMap();
		try {
			/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
			Map<String, Object> rsMap = null;
			context.contextMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (rsMap == null || rsMap.get("NODE") == null) {
				throw new Exception("Session lost");
			}
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			String wind_state = (String) context.contextMap.get("wind_state");
			wind_state = wind_state == null ? "0" : wind_state;
			context.contextMap.put("wind_state", wind_state);
			context.contextMap.put("search_decp", 0);
			pagingInfo = baseService.queryForListWithPaging("riskAudit.getRiskAudits", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			for(int i=0;pagingInfo!=null&&i<pagingInfo.getResultList().size();i++) {
				List<TagTo> tagList = tagService.getProjectTags((Integer)((Map<String,Object>)(pagingInfo.getResultList().get(i))).get("ID"),1);
				((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAGS", tagList);

			}
			List<TagTo> tagList = tagService.getAllTags(1);//案件类型
			outputMap.put("tags", tagList);
			
			
			//等级配置
			context.contextMap.put("rank", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			levelMap = (Map) DataAccessor.query("riskAudit.selectLevelMap",context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("二、三、四级评审管理页错误!请联系管理员");
		}
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("levelMap", levelMap);
		outputMap.put("wind_state", context.contextMap.get("wind_state"));
		outputMap.put("prc_node", Integer.valueOf(context.contextMap.get("prc_node").toString()));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("credit_type", context.contextMap.get("credit_type"));
		outputMap.put("level_node", context.contextMap.get("level_node"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/risk_audit/risk_auditManagerAfter.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}		
	
	public void getFiles(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		//附件相关
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("demandId", context.contextMap.get("credit_id"));
		paramMap.put("fileType", "risk");
		List<Map<String, Object>> fileList = (List<Map<String, Object>>) baseService.queryForList("demand.getFilesByDemandId", paramMap);
		outputMap.put("fileList", fileList);
		outputMap.put("bootPath", "riskFile");
		outputMap.put("prc_id", context.contextMap.get("prc_id"));
		outputMap.put("credit_id",context.contextMap.get("credit_id"));
		Output.jspOutput(outputMap, context, "/risk_audit/risk_fileUpload.jsp");
	}
	
	
	
	/**
	 * 初级评审，评审页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSucc_zulin(Context context) {
		Map outputMap = new HashMap();
		Map windMap = null;
		List errList = context.errList ;
		try {
			SelectReportInfo.selectReportInfo_zulin(context, outputMap);
			
			//设置参数,因为有些地方map里的key是小写,统一写成大写add by ShenQi
			context.contextMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
			Integer typeOfContract=(Integer) DataAccessor.query("riskAuditUpdate.checkContractType",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			//typeOfContract=3是重车合同,其他的是0,1,2
			outputMap.put("typeOfContract", String.valueOf(typeOfContract));
			windMap= (Map) DataAccessor.query("riskAudit.selectWindExplain",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//2012/03/19 Yang Yun 共案查询
			List<Map<String, Object>> mergedList = (List<Map<String, Object>>) DataAccessor.query("riskAudit.getMergedByProject", context.contextMap, RS_TYPE.LIST);
			outputMap.put("mergedList", mergedList);
			
			//Add by Michael 2012 11-26 增加支票还款明细
			List<Map<String, Object>> checkPaylines=(List<Map<String, Object>>) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, RS_TYPE.LIST);
			outputMap.put("checkPaylines", checkPaylines);
			
			outputMap.put("prc_id", context.contextMap.get("prc_id"));
			outputMap.put("flag", context.contextMap.get("flag"));
			outputMap.put("windMap", windMap);
			/*//权限别
			RiskAuditTo risk = riskAuditService.getRiskLevel((String) context.contextMap.get("credit_id"));
			outputMap.put("riskLevel", risk.getRiskLevel());
			outputMap.put("risk_level_memo", risk.getRiskLevelMsg());*/
			
			//附件相关
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("demandId", context.contextMap.get("credit_id"));
			paramMap.put("fileType", "risk");
			List<Map<String, Object>> fileList = (List<Map<String, Object>>) baseService.queryForList("demand.getFilesByDemandId", paramMap);
			outputMap.put("fileList", fileList);
			outputMap.put("bootPath", "riskFile");
			outputMap.put("credit_id",context.contextMap.get("credit_id"));
			
			//查询历史评审记录
			Map<String, Object> paramMapForHistory = new HashMap<String, Object>();
			paramMapForHistory.put("this_prc_id", context.contextMap.get("prc_id"));
			paramMapForHistory.put("credit_id",context.contextMap.get("credit_id"));
			List<Map<String, Object>> prc_history = (List<Map<String, Object>>) baseService.queryForList("riskAudit.getRiskHistory", paramMapForHistory);
			if (prc_history != null && prc_history.size() > 0) {
				paramMapForHistory.put("PRCM_USER_LEVEL", 0);
				paramMapForHistory.put("prc_id", prc_history.get(0).get("PRC_ID"));
				Map<String, Object> historyMemo = (Map<String, Object>) baseService.queryForObj("riskAudit.selectRiskMemoListForUpdate", paramMapForHistory);
				outputMap.put("context", historyMemo.get("PRCM_CONTEXT"));
			}
			
			//身份证验证权限
			outputMap.put("idCard_verify_auth", baseService.checkAccessForResource("idCard_verify_auth", String.valueOf(context.contextMap.get("s_employeeId"))));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("一级评审--生成页现场调查报告页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				//Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_succFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
				outputMap.put("context", context.contextMap.get("context"));
				//Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
			Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_succFrame.jsp");
		}
	}
	
	/**
	 * 1,2,3,4,5级评审，评审页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSucc_zulinAfter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map windMap = null;
		List riskMemoList = null;
		Map levelMap = new HashMap();
		try {
			//等级配置
			SelectReportInfo.selectReportInfo_zulin(context, outputMap);
			windMap= (Map) DataAccessor.query("riskAudit.selectWindExplain",context.contextMap, DataAccessor.RS_TYPE.MAP);			
			context.contextMap.put("rank", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			levelMap = (Map) DataAccessor.query("riskAudit.selectLevelMap",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			//评审内容
			context.contextMap.put("riskMemoLevel", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			riskMemoList= (List) DataAccessor.query("riskAudit.selectRiskMemoList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("windMap", windMap);
			outputMap.put("levelMap", levelMap);
			outputMap.put("riskMemoList",riskMemoList);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("prc_node", context.contextMap.get("prc_node"));
			outputMap.put("prc_id", context.contextMap.get("prc_id"));
			outputMap.put("VIP_FLAG", context.contextMap.get("VIP_FLAG"));
			/*//权限别
			outputMap.put("riskLevel", windMap.get("RISK_LEVEL"));
			outputMap.put("risk_level_memo", windMap.get("RISK_LEVEL_MEMO"));*/
			
			//这里是二三四级查看修改时独有的数据
			
			//设置参数,因为有些地方map里的key是小写,统一写成大写add by ShenQi
			context.contextMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
			Integer typeOfContract=(Integer) DataAccessor.query("riskAuditUpdate.checkContractType",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			//typeOfContract=3是重车合同,其他的是0,1,2
			outputMap.put("typeOfContract", String.valueOf(typeOfContract));
			//2012/03/19 Yang Yun 共案查询
			List<Map<String, Object>> mergedList = (List<Map<String, Object>>) DataAccessor.query("riskAudit.getMergedByProject", context.contextMap, RS_TYPE.LIST);
			outputMap.put("mergedList", mergedList);
			
			//Add by Michael 2012 11-26 增加支票还款明细
			List<Map<String, Object>> checkPaylines=(List<Map<String, Object>>) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, RS_TYPE.LIST);
			outputMap.put("checkPaylines", checkPaylines);
			
			outputMap.put("showFlag",0);
			
			//附件相关
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("demandId", context.contextMap.get("credit_id"));
			paramMap.put("fileType", "risk");
			List<Map<String, Object>> fileList = (List<Map<String, Object>>) baseService.queryForList("demand.getFilesByDemandId", paramMap);
			outputMap.put("fileList", fileList);
			outputMap.put("bootPath", "riskFile");
			outputMap.put("credit_id",context.contextMap.get("credit_id"));
			
			//查询历史评审记录
			Map<String, Object> paramMapForHistory = new HashMap<String, Object>();
			paramMapForHistory.put("this_prc_id", context.contextMap.get("prc_id"));
			paramMapForHistory.put("credit_id",context.contextMap.get("credit_id"));
			List<Map<String, Object>> prc_history = (List<Map<String, Object>>) baseService.queryForList("riskAudit.getRiskHistory", paramMapForHistory);
			if (prc_history != null && prc_history.size() > 0) {
				paramMapForHistory.put("PRCM_USER_LEVEL", (Integer.parseInt((String) context.contextMap.get("prc_node") + 1)));
				paramMapForHistory.put("prc_id", prc_history.get(0).get("PRC_ID"));
				Map<String, Object> historyMemo = (Map<String, Object>) baseService.queryForObj("riskAudit.selectRiskMemoListForUpdate", paramMapForHistory);
				if(historyMemo != null){
					outputMap.put("context", historyMemo.get("PRCM_CONTEXT"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("二、三、四级评审--评审页现场调查报告页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_succFrameAfter.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}	
	/**
	 * 一级评审---测评分管理    （一级评审生成页评测计分表）
	 *                     
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSucc_fen(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List fenList = null;
		List psTypeList = null;
		Map creditMap = null;
		try {
			if(!"3".equals(context.contextMap.get("typeOfContract"))) {
				//非重车评分表部分
				context.contextMap.put("fenTy", "评分项目类型");
				fenList = (List) DataAccessor.query("riskAudit.selectFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				//评审行业类型
				context.contextMap.put("dataType","评审行业类型");
				psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("psTypeList", psTypeList); 	

				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("fenshu", fenList.size());
				outputMap.put("showFlag",context.contextMap.get("showFlag"));	
				outputMap.put("creditMap", creditMap);
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("fenList", fenList);
				outputMap.put("typeOfContract", context.contextMap.get("typeOfContract"));
			} else {
				//重车评分表部分 add by Shen Qi
				context.contextMap.put("fenTy", "评分项目类型");
				fenList = (List) DataAccessor.query("riskAudit.selectFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
			
				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("fenshu", fenList.size());
				outputMap.put("showFlag",1);	
				outputMap.put("creditMap", creditMap);
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("fenList", fenList);
				outputMap.put("typeOfContract", context.contextMap.get("typeOfContract"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("一级评审--生成页测评计分表页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_succFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}
	
	
	/**
	 * 二、三、四级评审---测评分管理
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSucc_fenAfter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		
		List xuanList = null;
		Map creditMap = null;
		Map windIdeaMap = null;
		Map windMap = null;
		List fenTypes = null;
		List psTypeList = null;
		try {
			if(!"3".equals(context.contextMap.get("typeOfContract"))) {
				//非重车评分表部分
				windMap= (Map) DataAccessor.query("riskAudit.selectWindExplain",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("fenTy", "评分项目类型");
				xuanList = (List) DataAccessor.query("riskAuditUpdate.selectXuanFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
				fenTypes = (List) DataAccessor.query("riskAuditUpdate.selectFenType",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//评审行业类型
				context.contextMap.put("dataType","评审行业类型");
				psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("psTypeList", psTypeList); 			
				outputMap.put("fenshu", xuanList.size());
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("prc_node", context.contextMap.get("prc_node"));
				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("xuanList",xuanList);
				outputMap.put("showFlag",1);	
				outputMap.put("creditMap", creditMap);
				outputMap.put("windMap", windMap);
				outputMap.put("windIdeaMap", windIdeaMap);
				outputMap.put("fenType",fenTypes.size());
				outputMap.put("fenTypes",fenTypes);
				outputMap.put("typeOfContract",context.contextMap.get("typeOfContract"));
			} else {
				//重车评分表部分 add by Shen Qi
				windMap= (Map) DataAccessor.query("riskAudit.selectWindExplain",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("fenTy", "评分项目类型");
				xuanList = (List) DataAccessor.query("riskAuditUpdate.selectXuanFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
				fenTypes = (List) DataAccessor.query("riskAuditUpdate.selectFenType",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("fenshu", xuanList.size());
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("prc_node", context.contextMap.get("prc_node"));
				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("xuanList",xuanList);
				outputMap.put("showFlag",1);	
				outputMap.put("creditMap", creditMap);
				outputMap.put("windMap", windMap);
				outputMap.put("windIdeaMap", windIdeaMap);
				outputMap.put("fenType",fenTypes.size());
				outputMap.put("fenTypes",fenTypes);
				outputMap.put("typeOfContract",context.contextMap.get("typeOfContract"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("二、三、四级评审--评审页测评计分表页错误!请联系管理员");
		} finally {
			Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_succFrameAfter.jsp");
		}
	}	
	
	/**
	 * 各级评审操作
	 * @param context
	 * @throws Exception
	 */
	public void doRisk(Context context) throws Exception{
		try {
			riskAuditService.doRiskAuth(context);
			String prc_node = (String) context.contextMap.get("prc_node");
			if (StringUtils.isEmpty(prc_node)) {
				riskAudit(context);
			} else {
				riskAuditAfter(context);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 添加到测评分表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void inserttopoint(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		try {
			String[] fen_id = HTMLUtil.getParameterValues(context.getRequest(),"fen_id","");
			for (int i=0;i < fen_id.length;i++) {
				int z=0;
				z=i+1;

				Map map=new HashMap();
				map.put("fen_id", fen_id[i]);
				
				map.put("psTypeBuut", context.contextMap.get("psTypeBuut"));
				map.put("prc_id", context.contextMap.get("prc_id"));
							
				String neirong = "fencontext"+Integer.toString(z);
				map.put("fencontext",context.contextMap.get(neirong).toString());
								
				String q=context.contextMap.get("credit_id").toString();
				map.put("credit_id", Integer.parseInt(q));
								
				String s = "fen"+Integer.toString(z);
				map.put("fen",context.contextMap.get(s));
				DataAccessor.execute("riskAudit.inserttopoint",map,DataAccessor.OPERATION_TYPE.INSERT);
			}		
		} catch (Exception e) {
			errList.add("添加测评分失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=riskAudit.riskAudit");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 查询资料
	 * pact page
	 * @param context
	 */	
	@SuppressWarnings("unchecked")
	public void queryCreditFile(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();		
		Map infor = new HashMap();
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);					
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询担保人资料的信息
			} catch (Exception e) {
				e.printStackTrace();
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add("评审资料错误!请联系管理员");
			}	
	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("CONTRACT_TYPEss", context.contextMap.get("CONTRACT_TYPE"));
			Output.jspOutput(outputMap, context, "/risk_audit/rentFile.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	
	/**
	 * 获取所有以前备注信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showBeforeMemo(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List beforeMemoList = null;
		try {
			beforeMemoList = (List) DataAccessor.query("riskAudit.showBeforeMemo", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("获取以往审核记录错误!请联系管理员");
		}
		outputMap.put("beforeMemoList", beforeMemoList);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 判断是否存在编号存在则将编号覆盖
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void backRealPrcHao(Context context) throws Exception{
		//需要credit_id
		Map temp = (Map) DataAccessor.query("riskAudit.selectRiskControlByCreditID", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
		if(!(temp == null || temp.size() == 0 || temp.get("REAL_PRC_HAO") == null || "".equals(temp.get("REAL_PRC_HAO").toString().trim()))){
			context.contextMap.put("real_code", temp.get("REAL_PRC_HAO").toString().trim()) ;
		} 
	}
	
	//以不通过附条件退回  For IT 使用 ，如果勾选保留合同号则不更新合同号栏位，如果勾选不保留合同号栏位则更新合同号栏位
	@SuppressWarnings("unchecked")
	public void rejectForIT(Context context) {
		Map outputMap = new HashMap() ;
		String msg = null;
		SqlMapClient sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			String LEASE_CODE = (String) context.contextMap.get("LEASE_CODE");
			if (!StringUtils.isEmpty(LEASE_CODE)) {
				LEASE_CODE = LEASE_CODE.trim();
			} else {
				LEASE_CODE = "";
			}
			context.contextMap.put("LEASE_CODE", LEASE_CODE);
			outputMap.put("LEASE_CODE", LEASE_CODE.trim());
			Object obj =  sqlMapper.queryForObject("riskAudit.selectLeaseCodeByLeaseCode", context.contextMap) ;
			if(obj != null){
				Integer rect = (Integer) sqlMapper.queryForObject("riskAudit.getRectByLeaseCode", context.contextMap);
				if (rect > 0) {
					throw new Exception("该案件已经文审，如需退案，请手工清除合同和支付表数据。");
				}
				context.contextMap.put("CREDIT_ID", (Integer) obj);
				sqlMapper.update("riskAudit.updatePrjtStateByLeaseCode", context.contextMap);
				sqlMapper.update("riskAudit.updatePrjtRISKStateByCreditID", context.contextMap);
				context.contextMap.put("rejectFlag", String.valueOf(context.contextMap.get("is_lease")));
				sqlMapper.insert("riskAudit.insertLeaseFlag", context.contextMap);
			} else {
				throw new Exception("未找到合同号");
			}
			Long creditId = DataUtil.longUtil(context.contextMap.get("CREDIT_ID"));
			Long contractId = null;
			String logType = "业务支撑";
			String logTitle = "以不通过附条件退回";
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			int state = 1;
			String logCode = String.valueOf(context.contextMap.get("LEASE_CODE"));
			String memo = "";
					
			if ("0".equals(String.valueOf(context.contextMap.get("is_lease")))) {					
				memo = logTitle+"不保留合同号";					
			}else{
				memo = logTitle+"保留合同号";	
			}
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId,(String)context.contextMap.get("IP"));			
			sqlMapper.commitTransaction();
			msg = "成功退回";
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger) ;
			msg = "退回失败-" + e.getMessage();
		}
		finally{
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		outputMap.put("returnStr", msg) ;
		Output.jspOutput(outputMap, context, "/risk_audit/rejectForIT.jsp") ;
	}
	
	/**
	 * 2012/03/22 Yang Yun
	 * @param context
	 */
	public void getTheSame(Context context) {
		WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(context.request.getSession().getServletContext());
		appContext.getBean("baseService");
		logger.info("=====================getTheSame.Start========================");
		List<BaseTo> userList = null;
		BaseTo searchTo = (BaseTo) context.getFormBean().get("searchTo");
		try {
			userList = (List<BaseTo>) riskAuditService.queryForList("riskAudit.getTheSame", searchTo);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Output.jsonArrayOutputForObject(userList, context);
		logger.info("=====================getTheSame.End========================");
	}
	
	public void getGuiHuForAudit(Context context) throws Exception{
		PagingInfo<Object> pagingInfo = null;
		pagingInfo = baseService.queryForListWithPaging("riskAudit.getGuiHuForAudit", context.contextMap, "CUST_NAME");
		Map<String, Object> sum = (Map<String, Object>) baseService.queryForObj("riskAudit.getGuiHuSumForAudit", context.contextMap);
		context.contextMap.put("pagingInfo", pagingInfo);
		context.contextMap.put("sum", sum);
		Output.jspOutput(context.contextMap, context, "/risk_audit/guiHuForAudit.jsp");
	}
	
	public void getRiskLevel(Context context){
		String credit_id = (String) context.contextMap.get("credit_id");
		RiskAuditTo risk = null;
		try {
			risk = riskAuditService.getRiskLevelForTemp(credit_id);
		} catch (Exception e) {
			logger.error(e);
		}
		Output.jsonObjectOutputForTo(risk, context);
	}
	
	//审查评分系统======================================================
	
	/**
	 * 评分系统设置页面
	 * @param context
	 * @throws SQLException 
	 */
	public void riskScoreCardSettingPage(Context context) throws SQLException{
		System.out.println("==========================评分系统项目和选项配置页面=============================");
		Map<String, Object> outputMap = new HashMap<String, Object>() ;
		PagingInfo<Object> pagingInfo = null;
		List<ScoreCardTO> ratingOptionList = null;
		List<SelectionTo> subjectLevel1 = null;
		Object subject_id = context.contextMap.get("subject_id");
		String search_status = (String) context.contextMap.get("search_status");
		if (StringUtils.isEmpty(search_status)) {
			search_status = "0";
		}
		context.contextMap.put("search_status", search_status);
		pagingInfo = baseService.queryForListWithPaging("riskAudit.getRatingSubject", context.contextMap, "subject_order_by");
		
		subjectLevel1 = (List<SelectionTo>) baseService.queryForList("riskAudit.getSubjectLevel1", context.contextMap);
		if (!StringUtils.isEmpty(subject_id)) {
			ratingOptionList = (List<ScoreCardTO>) baseService.queryForList("riskAudit.getRatingOption", context.contextMap);
		}
		outputMap.put("search_status", search_status);
		outputMap.put("subject_id", subject_id);
		outputMap.put("subjectLevel1", subjectLevel1);
		outputMap.put("contractTypeSelection", LeaseUtil.getExistingContractType());
		outputMap.put("search_context", context.contextMap.get("search_context"));
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("ratingOptionList", ratingOptionList);
		Output.jspOutput(outputMap, context, "/risk_audit/score_card/ratingSubjectAndOptionSettingPage.jsp");
	}
	
	public void addRatingSubject(Context context) throws SQLException{
		ScoreCardTO ratingSubject = (ScoreCardTO) context.getFormBean("riskScoreCard");
		ratingSubject.setSubject_create_by(String.valueOf(context.contextMap.get("s_employeeId")));
		ratingSubject.setSubject_status(0);
		Integer subject_id = (Integer) baseService.insert("riskAudit.addRatingSubject", ratingSubject);
		context.contextMap.put("subject_id", subject_id);
		riskScoreCardSettingPage(context);
	}
	
	public void addRatingOption(Context context) throws SQLException{
		ScoreCardTO ratingSubject = (ScoreCardTO) context.getFormBean("riskScoreCard");
		ratingSubject.setOption_create_by(String.valueOf(context.contextMap.get("s_employeeId")));
		ratingSubject.setOption_status(0);
		baseService.insert("riskAudit.addRatingOption", ratingSubject);
		context.contextMap.put("subject_id", ratingSubject.getSubject_id());
		riskScoreCardSettingPage(context);
	}
	
	public void updateRatingSubjectStatus(Context context) throws SQLException{
		System.out.println("==========================更新状态=============================");
		String id = (String) context.contextMap.get("id");
		String status = (String) context.contextMap.get("status");
		ScoreCardTO cardTO = new ScoreCardTO();
		cardTO.setSubject_id(Integer.parseInt(id));
		cardTO.setSubject_status(Integer.parseInt(status));
		baseService.update("riskAudit.updateRatingSubjectStatus", cardTO);
		riskScoreCardSettingPage(context);
	}
	
	public void updateRatingOptionStatus(Context context) throws SQLException{
		System.out.println("==========================更新状态=============================");
		String id = (String) context.contextMap.get("id");
		String status = (String) context.contextMap.get("status");
		ScoreCardTO cardTO = new ScoreCardTO();
		cardTO.setOption_id(Integer.parseInt(id));
		cardTO.setOption_status(Integer.parseInt(status));
		baseService.update("riskAudit.updateRatingOptionStatus", cardTO);
		riskScoreCardSettingPage(context);
	}
	
	public void saveAndshowScoreCard(Context context) throws SQLException{
		String contractType = (String) context.contextMap.get("contractType");
		String scoreCard = (String) context.contextMap.get("scoreCard");
		riskAuditService.saveScoreCardSetting(contractType, scoreCard, String.valueOf(context.contextMap.get("s_employeeId")));
		showScoreCard(context);
	}
	
	public void showScoreCardSetting(Context context) throws SQLException{
		Map<String, Object> outputMap = new HashMap<String, Object>() ;
		String contractType = (String) context.contextMap.get("contractType");
		if (StringUtils.isEmpty(contractType)) {
			contractType = "7";
		}
		context.contextMap.put("contractType", contractType);
		String scoreCard = LeaseUtil.getScoreCardByContractType(contractType);
		outputMap.put("scoreCard", riskAuditService.getAllScoreCard());
		outputMap.put("mode", "set");
		outputMap.put("contractTypeSelection", LeaseUtil.getExistingContractType());
		outputMap.put("contractType", contractType);
		outputMap.put("scoreCardValue", scoreCard);
		Output.jspOutput(outputMap, context, "/risk_audit/score_card/scoreCardSettingPage.jsp");
	}
	
	public void showScoreCard(Context context) throws SQLException{
		Map<String, Object> outputMap = new HashMap<String, Object>() ;
		String contractType = (String) context.contextMap.get("contractType");
		String scoreCard = LeaseUtil.getScoreCardByContractType(contractType);
		outputMap.put("scoreCard", riskAuditService.getScoreCard(scoreCard));
		outputMap.put("mode", "use");
		Output.jspOutput(outputMap, context, "/risk_audit/score_card/scoreCardPreview.jsp");
	}
	
	public void showScoreCardForCurrent(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>() ;
		String scoreCard = (String) context.contextMap.get("scoreCard");
		String scoreCardCode = (String) context.contextMap.get("scoreCardCode");
		outputMap.put("scoreCard", riskAuditService.getScoreCard(scoreCardCode));
		outputMap.put("selectedScoreCard", scoreCard);
		outputMap.put("mode", "show");
		Output.jspOutput(outputMap, context, "/risk_audit/score_card/scoreCardPreview.jsp");
	}
	
	public void doScoreCardForRisk(Context context) throws SQLException{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String contract_type = (String) context.contextMap.get("contract_type");
		String prc_id = (String) context.contextMap.get("prc_id");
		String scoreCardForRisk = (String) baseService.queryForObj("riskAudit.getScoreCardByRisk", context.contextMap);
		String creditId = LeaseUtil.getCreditIdByRiskId(prc_id);
		String cust_name = LeaseUtil.getCustNameByCreditId(creditId);
		outputMap.put("cust_name", cust_name);
		if(StringUtils.isEmpty(scoreCardForRisk)){
			outputMap.put("prc_id", prc_id);
			outputMap.put("scoreCard", riskAuditService.getScoreCardByContractType(contract_type));
			//评分表历史
			Map<String, Object> paramMapForHistory = new HashMap<String, Object>();
			paramMapForHistory.put("this_prc_id", prc_id);
			paramMapForHistory.put("credit_id", LeaseUtil.getCreditIdByRiskId(prc_id));
			List<Map<String, Object>> prc_history = (List<Map<String, Object>>) baseService.queryForList("riskAudit.getRiskHistory", paramMapForHistory);
			if (prc_history != null && prc_history.size() > 0) {
				outputMap.put("history_scoreCard", prc_history.get(0).get("SCORE_CARD"));
			}
		} else {
			outputMap.put("scoreCard", riskAuditService.getScoreCard(scoreCardForRisk));
			outputMap.put("msg", "已被评分。");
		}
		Output.jspOutput(outputMap, context, "/risk_audit/riskScoreCardPreview.jsp");
	}
	
	public void showScoreCardForRisk(Context context) throws SQLException{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String prc_id = (String) context.contextMap.get("prc_id");
		String creditId = LeaseUtil.getCreditIdByRiskId(prc_id);
		outputMap.put("scoreCard", riskAuditService.getAllScoreCardByPrcId(prc_id));
		outputMap.put("riskScore", riskAuditService.getRiskScoreCardById(prc_id));
		outputMap.put("msg", context.contextMap.get("msg"));
		outputMap.put("cust_name", LeaseUtil.getCustNameByCreditId(creditId));
		Output.jspOutput(outputMap, context, "/risk_audit/riskScoreCardPreview.jsp");
	}
	
	public void saveScoreCard(Context context) throws SQLException{
		String scoreCard = (String) context.contextMap.get("scoreCard");
		String scoreCardCode = (String) context.contextMap.get("scoreCardCode");
		String score = (String) context.contextMap.get("score");
		String prc_id = (String) context.contextMap.get("prc_id");
		String scoreCardForRisk = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("scoreCard", scoreCard);
		paramMap.put("scoreCardCode", scoreCardCode);
		paramMap.put("score", score);
		paramMap.put("prc_id", prc_id);
		scoreCardForRisk = (String) baseService.queryForObj("riskAudit.getScoreCardByRisk", paramMap);
		if(StringUtils.isEmpty(scoreCardForRisk)){
			baseService.update("riskAudit.saveScoreCard", paramMap);
			context.contextMap.put("msg", "评分成功。");
		} else {
			scoreCard = scoreCardForRisk;
			context.contextMap.put("msg", "已被评分。");
		}
		context.contextMap.put("scoreCard", scoreCard);
		context.contextMap.put("creditId", LeaseUtil.getCreditIdByRiskId(prc_id));
		showScoreCardForRisk(context);
	}
	
	public void updateCreditReport(Context context) throws Exception{
		
		riskAuditService.doUpdateCreditReport(context);
		if (StringUtils.isEmpty(context.contextMap.get("flag"))) {
			selectRiskAuditForSucc_zulinAfter(context);
		} else {
			selectRiskAuditForSucc_zulin(context);
		}
		
		
	}
	
	public void saveFileList(Context context){
		this.uploadFiles(context);
		String files = context.contextMap.get("ids")==null?"":context.contextMap.get("ids").toString();
		if(!files.equals("")){
			context.contextMap.put("demandIdForFile", context.contextMap.get("credit_id"));
			context.contextMap.put("files", files);
			context.contextMap.put("fileType", "risk");
			baseService.update("demand.updateDemandFiles", context.contextMap);
		}
		getFiles(context);
		/*if (StringUtils.isEmpty(context.contextMap.get("prc_node"))) {
			selectRiskAuditForSucc_zulin(context);
		} else {
			selectRiskAuditForSucc_zulinAfter(context);
		}*/
	}
	
	/**
	 * 上传文件
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void uploadFiles(Context context) {
		List fileItems = (List) context.contextMap.get("uploadList");
		String file_path = "";
		String err = "";
		String ids = "";
		if (fileItems != null && fileItems.size() > 0) {
			FileItem fileItem = null;
			for (int i = 0; i < fileItems.size(); i++) {
				fileItem = (FileItem) fileItems.get(i);
				logger.info("文件大小==========>>" + fileItem.getSize());
				if (fileItem.getSize() > (2*1024*1024)) {
					err = "不好意思，您上传的文件大于2M了。";
				}
			}
			for (int i = 0 ;i < fileItems.size() ;i++ ) {
				fileItem = (FileItem) fileItems.get(i);
				if(!fileItem.getName().equals("")){
					String title = "项目评审附件";
					String filePath = fileItem.getName();
					String type = filePath.substring(filePath.lastIndexOf(".") + 1);
					List errList = context.errList;
					Map contextMap = context.contextMap;
					try {
						String xmlPath = "riskFile";
						String bootPath = LeaseUtil.getFilePath(xmlPath);
						if (bootPath != null) {
							File realPath = new File(bootPath + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
							if (!realPath.exists()){
								realPath.mkdirs();
							}
							String imageName = FileExcelUpload.getNewFileName();
							File uploadedFile = new File(realPath.getPath() + File.separator + imageName + "." + type);
							file_path = '/' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + '/' + type + '/'+ imageName + "."+ type;
						
							if (errList.isEmpty()) {
								fileItem.write(uploadedFile);
								//增加关联
								contextMap.put("path", file_path);
								contextMap.put("fileName", fileItem.getName());
								contextMap.put("title", title);
								contextMap.put("fileType", "risk");
								contextMap.put("userId", context.contextMap.get("s_employeeId"));
								contextMap.put("date", new Date());
								int fId = (Integer)baseService.insert("demand.insertDemandFile", context.contextMap);
								if(ids.equals("")){
									ids = Integer.toString(fId);
								} else {
									ids = ids + "," + Integer.toString(fId);
								}
							}
						}
					} catch (Exception e) {
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
			}
		}
		Map<String,String> outputMap = new HashMap<String,String>();
		context.contextMap.put("ids", ids);
		context.contextMap.put("err", err);
	}
	
	/*JSONArray jsonArray = new JSONArray();
	List<Map<String, Object>> logList = (List<Map<String, Object>>) baseService.queryForList("creditReportManage.getEstimatesPayDateLog", context.contextMap);
	for (Map<String, Object> map : logList) {
		map.put("CREATE_TIME", DateUtil.dateToString((Timestamp)map.get("CREATE_TIME"), "yyyy-MM-dd HH:mm"));
		JSONObject jsonObj = JSONObject.fromObject(map);
		jsonArray.add(jsonObj);
	}*/
	
	public void getGuiHuByCreditId(Context context){
		Map<String,Object> outputMap = new HashMap<String,Object>();
		String creditId = (String) context.contextMap.get("credit_id");
		try {
			//归户
			String custId = LeaseUtil.getCustIdByCreditId(creditId);
			List<Map<String, String>> guarList = LeaseUtil.getGuarantorByCreditId(creditId);
			JSONArray guihuForGuar = new JSONArray();
			for (Map<String, String> guar : guarList) {
				guihuForGuar.add(JSONObject.fromObject(LeaseUtil.getGuiHuByGuar(guar, creditId)));
			}
			outputMap.put("guihuForGuar", guihuForGuar);
			outputMap.put("guihuForCust", JSONObject.fromObject(LeaseUtil.getGuiHuByCustId(custId, creditId)));
			JSONArray creditLine = new JSONArray();
			for (CreditLineTO o : LeaseUtil.getSuplCreditLine(LeaseUtil.getSuplIdByCreditId(creditId))) {
				if (o != null) {
					o.getStartDateStr();
					o.getEndDateStr();
					o.setStartDate(null);
					o.setEndDate(null);
					creditLine.add(JSONObject.fromObject(o));
				}
			}
			outputMap.put("creditLine", creditLine);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonOutput(outputMap, context);
	}
	
	
}