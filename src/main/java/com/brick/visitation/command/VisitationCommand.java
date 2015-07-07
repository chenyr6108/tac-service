package com.brick.visitation.command;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ConcurrencyException;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.BaseTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.baseManage.service.BusinessLog;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.visitation.service.VisitationService;
import com.brick.visitation.to.VisitationReportTo;
import com.brick.visitation.to.VisitationTO;

public class VisitationCommand extends BaseCommand {
	
	Log logger = LogFactory.getLog(this.getClass());
	
	private VisitationService visitationService;
	
	public VisitationService getVisitationService() {
		return visitationService;
	}

	public void setVisitationService(VisitationService visitationService) {
		this.visitationService = visitationService;
	}

	/**
	 * 申请访厂页面
	 * @param context
	 */
	public void applyVisit(Context context){
		logger.info("===================申请访厂页面=====================");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		PagingInfo<Object> pagingInfo = null;
		try {
			String search_content = (String) context.contextMap.get("search_content");
			if (!StringUtils.isEmpty(search_content)) {
				search_content = search_content.trim();
			}
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("search_content", search_content);
			outputMap.put("search_content", search_content);
			pagingInfo = baseService.queryForListWithPaging("visitation.getAllForApply", context.contextMap, "CREATE_DATE");
			outputMap.put("pagingInfo", pagingInfo);
		} catch (Exception e) {
			e.printStackTrace();
			context.contextMap.put("errorMsg", e.getMessage());
		}
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		outputMap.put("visitationTo", context.contextMap.get("visitationTo"));
		Output.jspOutput(outputMap, context, "/visit/visitApply.jsp");
	}
	
	/**
	 * 查看访厂信息
	 * @param context
	 */
	public void getVisitInfo(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String visit_id = context.contextMap.get("visit_id") == null ? null : context.contextMap.get("visit_id").toString();
		VisitationTO visitationTO = new VisitationTO();
		visitationTO.setVisit_id(visit_id);
		try {
			visitationTO = (VisitationTO) baseService.queryForObj("visitation.getVisitInfo", visitationTO);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (visitationTO != null && visitationTO.getVisit_id() != null) {
			outputMap.put("resultFlag", "true");
		} else {
			outputMap.put("resultFlag", "false");
		}
		outputMap.put("resultData", visitationTO);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 审批页面
	 * @param context
	 */
	public void authVisit(Context context){
		logger.info("===================审批页面=====================");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		PagingInfo<Object> pagingInfo = null;
		try {
			if (StringUtils.isEmpty((String) context.contextMap.get("search_status"))) {
				context.contextMap.put("search_status", "0");
			}
			outputMap.put("search_status", context.contextMap.get("search_status"));
			String search_content = (String) context.contextMap.get("search_content");
			if (!StringUtils.isEmpty(search_content)) {
				search_content = search_content.trim();
			}
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			context.contextMap.put("search_content", search_content);
			outputMap.put("search_content", search_content);
			pagingInfo = baseService.queryForListWithPaging("visitation.getAllForAuth", context.contextMap, "CREATE_DATE");
			outputMap.put("pagingInfo", pagingInfo);
		} catch (Exception e) {
			e.printStackTrace();
			context.contextMap.put("errorMsg", e.getMessage());
		}
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		Output.jspOutput(outputMap, context, "/visit/visitAuth.jsp");
	}
	
	/**
	 * 审查访厂报告维护页面
	 * 也可供业务查看访厂进度，和结果
	 * @param context
	 */
	public void visitReportManager(Context context){
		logger.info("===================访厂管理页面=====================");
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
			
			//过滤业务员只能看到自己的案件
			Map rsMap = null;
			Map paramMap = new HashMap();
			paramMap.put("id", context.contextMap.get("s_employeeId"));
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			
			pagingInfo = baseService.queryForListWithPaging("visitation.getAllApplied", context.contextMap, "AUTH_DATE");
			outputMap.put("pagingInfo", pagingInfo);
			//权限
			BaseTo baseTo = new BaseTo();
			baseTo.setModify_by(context.contextMap.get("s_employeeId").toString());
			baseTo.setResource_code("FC-Assign");
			outputMap.put("assignRole", visitationService.checkAccessForResource(baseTo));
			baseTo.setResource_code("FC-EditReport");
			outputMap.put("editReportRole", visitationService.checkAccessForResource(baseTo));
		} catch (Exception e) {
			e.printStackTrace();
			context.contextMap.put("errorMsg", e.getMessage());
		}
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		
		Output.jspOutput(outputMap, context, "/visit/visitReport.jsp");
	}
	
	/**
	 * 访厂申请操作
	 * @param context
	 */
	public void doApplyVisit(Context context){
		logger.info("===================申请动作=====================");
		VisitationTO visitationTo = (VisitationTO) context.getFormBean("visitationTo");
		String errorMsg = null;
		try {
			//验证页面数据
			if (StringUtils.isEmpty(visitationTo.getCredit_id())) {
				errorMsg = "数据过期，请刷新页面";
				throw new ServiceException(errorMsg);
			}
			if (StringUtils.isEmpty(visitationTo.getVisit_area())) {
				errorMsg = "访厂区域不能为空";
				throw new ServiceException(errorMsg);
			}
			if (StringUtils.isEmpty(visitationTo.getHope_visit_date_str())) {
				errorMsg = "希望访厂时间不能为空";
				throw new ServiceException(errorMsg);
			}
			
			//验证是否能申请
			try {
				if (!visitationService.checkCanApply(visitationTo)) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new ServiceException("该报告已经提交访厂申请，请耐心等待。");
			}
			Date hopeVisitDate = visitationTo.getHope_visit_date();
			//下午3点前申请的，可申请次日访厂，如3点后申请，则只能申请隔日访厂。
			//验证
			/*Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HH");
			int nowHour = Integer.parseInt(sdf.format(now));
			sdf.applyPattern("yyyy-MM-dd");
			Calendar manager = Calendar.getInstance();
			if (nowHour >= 15) {
				manager.setTime(now);
				manager.add(manager.DATE, 1);
				if (!manager.getTime().before(hopeVisitDate)) {
					errorMsg = "申请时间不正确，请看下面的说明！";
					throw new ServiceException(errorMsg);
				}
			} else {
				if (!now.before(hopeVisitDate)) {
					errorMsg = "申请时间不正确，请看下面的说明！";
					throw new ServiceException(errorMsg);
				}
			}*/
			String s_employeeId = context.contextMap.get("s_employeeId") == null ? 
					"0" : context.contextMap.get("s_employeeId").toString();
			visitationTo.setCreate_by(s_employeeId);
			visitationTo.setModify_by(s_employeeId);
			baseService.insertByTO("visitation.insertVisitation", visitationTo);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(visitationTo.getCredit_id()), null, 
					"访厂管理", "申请访厂", "", "申请访厂[" + DateUtil.dateToStr(hopeVisitDate) + "]", 
					1, Long.parseLong(visitationTo.getCreate_by()), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			e.printStackTrace();
			context.contextMap.put("errorMsg", e.getMessage());
			context.contextMap.put("visitationTo", visitationTo);
		}
		if("Y".equals(context.contextMap.get("isSalesDesk"))) {////判断是否是从业务人员桌面提交的 add by ShenQi
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
		} else {
			applyVisit(context);
		}
	}
	
	/**
	 * 业务主管审核操作
	 * @param context
	 */
	public void doAuthVisit(Context context){
		visitationService.doAuthVisit(context);
		authVisit(context);
	}
	
	/**
	 * 审查经理分配
	 * @param context
	 */
	public void doAssign(Context context){
		logger.info("=======================分配动作=======================");
		String visit_id = (String) context.contextMap.get("visit_id");
		String visit_by = (String) context.contextMap.get("visitor_id");
		String visit_date = (String) context.contextMap.get("visit_date");
		String visit_time = (String) context.contextMap.get("visit_time");
		String modify_date_str = (String) context.contextMap.get("modify_date_str");
		String credit_id = (String) context.contextMap.get("credit_id");
		String visitor = (String) context.contextMap.get("visitor");
		try {
			if (StringUtils.isEmpty(visit_id) || StringUtils.isEmpty(modify_date_str) || StringUtils.isEmpty(credit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			if (StringUtils.isEmpty(visit_by)) {
				throw new Exception("访厂人员不能为空。");
			}
			if (StringUtils.isEmpty(visit_date)) {
				throw new Exception("访厂时间不能为空。");
			}
			if (StringUtils.isEmpty(visit_time)) {
				throw new Exception("访厂时间不能为空。");
			}
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			VisitationTO visitationTO = new VisitationTO();
			visitationTO.setVisit_id(visit_id);
			visitationTO.setKey_value(visit_id);
			visitationTO.setModify_date_str(modify_date_str);
			if (!visitationService.checkModifyDateIsEq(visitationTO)) {
				throw new ConcurrencyException();
			}
			visitationTO.setModify_by(s_employeeId);
			visitationTO.setAssi_visitor(Integer.parseInt(visit_by));
			visitationTO.setAssi_visit_date_str(visit_date);
			visitationTO.setAssi_visit_date_time(visit_time);
			visitationTO.setVisit_status(2);
			visitationService.updateForAll(visitationTO);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(credit_id), null, "访厂管理", "审查分配", 
					"", "审查经理分配:访厂人员[" + visitor + "],访厂时间[" + visit_date + visitationTO.getVisitTimeStr(visit_time) + "]", 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
		visitReportManager(context);
	}
	
	/**
	 * 审查驳回操作
	 * @param context
	 */
	public void rejectVisitByExam(Context context){
		logger.info("==================审查驳回操作==================");
		String visit_id = (String) context.contextMap.get("visit_id");
		String modify_date_str = (String) context.contextMap.get("modify_date_str");
		String credit_id = (String) context.contextMap.get("credit_id");
		try {
			DataAccessor.getSession().startTransaction();
			if (StringUtils.isEmpty(visit_id) || StringUtils.isEmpty(modify_date_str) || StringUtils.isEmpty(credit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			VisitationTO visitationTO = new VisitationTO();
			visitationTO.setVisit_id(visit_id);
			visitationTO.setKey_value(visit_id);
			visitationTO.setModify_date_str(modify_date_str);
			if (!visitationService.checkModifyDateIsEq(visitationTO)) {
				throw new ConcurrencyException();
			}
			visitationTO.setVisit_status(0);
			visitationTO.setModify_by(s_employeeId);
			visitationService.updatePortion(visitationTO);
			String reject_reason = (String) context.contextMap.get("reject_reason");
			if (StringUtils.isEmpty(reject_reason)) {
				reject_reason = "没有填写驳回理由。";
			}
			visitationTO.setReject_memo(reject_reason);
			visitationService.insertByTO("visitation.insertMemo", visitationTO);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(credit_id), null, "访厂管理", "审查管理", 
					"", "审查经理-驳回", 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
			DataAccessor.getSession().commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		visitReportManager(context);
	}
	
	/**
	 * 查看拒绝原因
	 * @param context
	 */
	public void showRejectReason(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String visit_id = (String) context.contextMap.get("visit_id");
		try {
			if (StringUtils.isEmpty(visit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			VisitationTO visiTo = new VisitationTO();
			visiTo.setVisit_id(visit_id);
			List<VisitationTO> resultList = (List<VisitationTO>) visitationService.queryForList("visitation.getMemo", visiTo);
			outputMap.put("resultList", resultList);
		} catch (Exception e) {
			e.printStackTrace();
			outputMap.put("errorMsg", e.getMessage());
		}
		Output.jspOutput(outputMap, context, "/visit/showRejectReason.jsp");
	}
	
	/**
	 * 审查经理取消分配
	 * 并驳回访厂申请
	 * @param context
	 */
	public void cancelAssign(Context context){
		logger.info("==================审查经理取消分配==================");
		String visit_id = (String) context.contextMap.get("visit_id");
		String modify_date_str = (String) context.contextMap.get("modify_date_str");
		String credit_id = (String) context.contextMap.get("credit_id");
		try {
			if (StringUtils.isEmpty(visit_id) || StringUtils.isEmpty(modify_date_str) || StringUtils.isEmpty(credit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			VisitationTO visitationTO = new VisitationTO();
			visitationTO.setVisit_id(visit_id);
			visitationTO.setKey_value(visit_id);
			visitationTO.setModify_date_str(modify_date_str);
			if (!visitationService.checkModifyDateIsEq(visitationTO)) {
				throw new ConcurrencyException();
			}
			String reject_reason = (String) context.contextMap.get("reject_reason");
			if (StringUtils.isEmpty(reject_reason)) {
				reject_reason = "没有填写驳回理由。";
			}
			visitationTO.setReject_memo(reject_reason);
			visitationTO.setModify_by(s_employeeId);
			visitationService.insertByTO("visitation.insertMemo", visitationTO);
			visitationTO.setVisit_status(0);
			visitationService.updateForAll(visitationTO);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(credit_id), null, "访厂管理", "审查管理", 
					"", "审查经理-取消分配并驳回", 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
		visitReportManager(context);
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
			reportTo = (VisitationReportTo) visitationService.queryForObj("visitation.getReport", reportTo);
			if (reportTo == null || reportTo.getVisit_id() == null) {
				throw new Exception("数据过期，请刷新页面。");
			}
			outputMap.put("reportTo", reportTo);
			outputMap.put("show_type", context.contextMap.get("show_type"));
			Output.jspOutput(outputMap, context, "/visit/visitForm.jsp");
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
			visitReportManager(context);
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
			visitReportManager(context);
		} catch (Exception e) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("reportTo", (VisitationReportTo) context.getFormBean("reportTo"));
			outputMap.put("errorMsg", e.getMessage());
			Output.jspOutput(outputMap, context, "/visit/visitForm.jsp");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 导出访厂报告
	 * @param context
	 */
	public void outputReport(Context context){
		logger.info("=============================导出访厂报告================================");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String visit_id = (String) context.contextMap.get("visit_id");
		try {
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			if (StringUtils.isEmpty(visit_id)) {
				throw new Exception("数据过期，请刷新页面。");
			}
			VisitationReportTo reportTo = new VisitationReportTo();
			reportTo.setVisit_id(visit_id);
			reportTo = (VisitationReportTo) visitationService.queryForObj("visitation.getReport", reportTo);
			if (reportTo == null || reportTo.getVisit_id() == null) {
				throw new Exception("数据过期，请刷新页面。");
			}
			outputMap.put("reportTo", reportTo);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(reportTo.getCredit_id()), null, "访厂管理", "访厂报告", 
					"", "导出访厂报告", 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/visit/visitFormForShow.jsp");
	}
	
	/**
	 * 无需访厂操作
	 * @param context
	 */
	public void noneVisitSub(Context context){
//		updateNoneVisitMemo
		String none_visit_memo = (String) context.contextMap.get("none_visit_memo");
		String none_visit_reason = (String) context.contextMap.get("none_visit_reason");
		String visit_id = (String) context.contextMap.get("visit_id");
		String modify_date_str = (String) context.contextMap.get("modify_date_str");
		String credit_id = (String) context.contextMap.get("credit_id");
		VisitationTO visit = new VisitationTO();
		visit.setModify_date_str(modify_date_str);
		visit.setVisit_id(visit_id);
		try {
			if (!baseService.checkModifyDateIsEq(visit)) {
				throw new Exception("数据已过期，请刷新页面。");
			}
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			visit.setModify_by(s_employeeId);
			visit.setNone_visit_memo(none_visit_memo);
			visit.setNone_visit_reason(none_visit_reason);
			baseService.update("visitation.updateNoneVisitMemo", visit);
			BusinessLog.addBusinessLogWithIp(Long.parseLong(credit_id), null, "访厂管理", "访厂", 
					"", "无需访厂（" + none_visit_reason + "）", 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
		}
		visitReportManager(context);
	}
	
	public void getCreditState(Context context) throws ServiceException{
		Integer state = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			state = (Integer) baseService.queryForObj("visitation.getCreditState", context.contextMap);
			outputMap.put("state", state);
			Output.jsonOutput(outputMap, context);
		} catch (ServiceException e) {
			throw e;
		}
		
	}
	
	
	
	
}
