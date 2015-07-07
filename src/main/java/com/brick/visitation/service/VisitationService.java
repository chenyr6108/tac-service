package com.brick.visitation.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ConcurrencyException;
import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.baseManage.service.BusinessLog;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;
import com.brick.visitation.dao.VisitationDAO;
import com.brick.visitation.to.VisitationReportTo;
import com.brick.visitation.to.VisitationTO;

public class VisitationService extends BaseService {
	Log logger = LogFactory.getLog(this.getClass());
	private static VisitationDAO visitationDAO;

	public VisitationDAO getVisitationDAO() {
		return visitationDAO;
	}

	public void setVisitationDAO(VisitationDAO visitationDAO) {
		this.visitationDAO = visitationDAO;
	}

	/**
	 * 检查能否申请
	 * @param visitationTo
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkCanApply(VisitationTO visitationTo) throws ServiceException {
		boolean flag = false;
		try {
			Integer result = visitationDAO.getApplyInfoByCreditId(visitationTo);
			if (result == 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}
	
	public boolean checkCanApply(String credit_id) throws ServiceException {
		VisitationTO visitationTo = new VisitationTO();
		visitationTo.setCredit_id(credit_id);
		return this.checkCanApply(visitationTo);
	}
	

	public void updateForAll(VisitationTO visitationTO) throws ServiceException {
		try {
			visitationDAO.updateForAll(visitationTO);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public void updatePortion(VisitationTO visitationTO) throws ServiceException {
		try {
			visitationDAO.updatePortion(visitationTO);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public boolean checkCanInputReport(VisitationReportTo reportTo) throws ServiceException {
		boolean flag = false;
		try {
			Integer result = visitationDAO.getReportByVisitId(reportTo);
			if (result != null && result == 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}
	
	/**
	 * 增加一条无需访厂记录
	 * @param credit_id
	 * @param memo
	 */
	public void addDontNeedVisit(String credit_id, String memo){
		Map<String, Object> paramForMerge = new HashMap<String, Object>();
		paramForMerge.put("CREDIT_ID", credit_id);
		paramForMerge.put("NONE_VISIT_MEMO", memo);
		insert("visitation.setVisitationForMerger", paramForMerge);
	}

	@Transactional(rollbackFor = Exception.class)
	public void doInputReport(Context context) throws Exception {
		VisitationReportTo reportTo = (VisitationReportTo) context.getFormBean("reportTo");
		try {
			String s_employeeId = context.contextMap.get("s_employeeId").toString();
			String update_mode = (String) context.contextMap.get("update_mode");
			if (update_mode == null) {
				throw new Exception("操作失败，请重试。");
			}
			if (reportTo == null) {
				throw new Exception("页面过期，请刷新页面。");
			}
			if (StringUtils.isEmpty(reportTo.getVisit_id()) || StringUtils.isEmpty(reportTo.getCredit_id())) {
				throw new Exception("数据过期，请刷新页面。");
			}
			if (StringUtils.isEmpty(reportTo.getReal_visit_date_str())) {
				throw new Exception("实际访厂时间不能为空。");
			}
			if (reportTo.getReal_visitor() == null || reportTo.getReal_visitor() == 0) {
				throw new Exception("实际访厂人员不能为空。");
			}
			String memo = null;
			if ("N".equals(update_mode)) {
				reportTo.setStatus(0);
				memo = "访厂报告保存成功。";
			} else if("F".equals(update_mode)) {
				reportTo.setStatus(1);
				memo = "访厂报告生成成功。";
			} else {
				throw new Exception("操作失败，请重试。");
			}
			reportTo.setModify_by(s_employeeId);
			
			//更新实际访厂时间
			VisitationTO visitTo = new VisitationTO();
			visitTo.setVisit_id(reportTo.getVisit_id());
			visitTo.setReal_visit_date_str(reportTo.getReal_visit_date_str());
			visitTo.setReal_visitor(reportTo.getReal_visitor());
			visitTo.setModify_by(s_employeeId);
			visitTo.setVisit_status(3);
			this.updatePortion(visitTo);
			if (StringUtils.isEmpty(reportTo.getReport_id())) {
				//验证有没有被填写过
				if (!this.checkCanInputReport(reportTo)) {
					throw new ConcurrencyException();
				}
				//新增
				this.insertByTO("visitation.insertReport", reportTo);
			} else {
				//验证有没有被修改过
				reportTo.setKey_value(reportTo.getReport_id());
				if (!this.checkModifyDateIsEq(reportTo)) {
					throw new ConcurrencyException();
				}
				//修改
				this.update("visitation.updateReport", reportTo);
			}
			BusinessLog.addBusinessLogWithIp(Long.parseLong(reportTo.getCredit_id()), null, "访厂管理", "访厂报告", 
					"", memo, 1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
			context.contextMap.put("errorMsg", "保存成功。");
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static VisitationReportTo getVisitReport(String visit_id){
		VisitationReportTo reportTo = new VisitationReportTo();
		reportTo.setVisit_id(visit_id);
		try {
			reportTo = (VisitationReportTo) visitationDAO.queryForObject("visitation.getReport", reportTo);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return reportTo;
	}

	public void doAuthVisit(Context context) {
		logger.info("===================审批动作=====================");
		String auth_type = (String) context.contextMap.get("auth_type");
		String auth_memo = (String) context.contextMap.get("auth_memo");
		String non_visit = (String) context.contextMap.get("non_visit");
		String s_employeeId = context.contextMap.get("s_employeeId").toString();
		String auth_date_str = (String) context.contextMap.get("auth_date_str");
		String visit_id = (String) context.contextMap.get("visit_id");
		VisitationTO visiTo = new VisitationTO();
		String logMsg = null;
		try {
			visiTo.setModify_by(s_employeeId);
			visiTo.setAuth_by(s_employeeId);
			visiTo.setVisit_id(visit_id);
			visiTo.setKey_value(visit_id);
			visiTo.setAuth_date_str(StringUtils.isEmpty(auth_date_str) ? null : auth_date_str);
			if (!this.checkAuthDateIsEq(visiTo)) {
				throw new ServiceException("该记录已被操作，请刷新数据。");
			}
			if (!StringUtils.isEmpty(non_visit)) {
				//专案免访厂
				visiTo.setNone_visit_memo("免访厂专案");
				visiTo.setNone_visit_reason("免访厂专案");
				update("visitation.updateNoneVisitMemo", visiTo);
				logMsg = "专案免访厂";
			} else {
				if ("Pass".equals(auth_type)) {
					logMsg = "通过";
					visiTo.setVisit_status(1);
				} else if ("Reject".equals(auth_type)) {
					logMsg = "驳回";
					visiTo.setVisit_status(-1);
				} else {
					throw new ServiceException("审批动作不合法。");
				}
				
				if (StringUtils.isEmpty(visit_id)) {
					throw new ServiceException("数据过期，无法审批。");
				}
				visiTo.setAuth_memo(auth_memo);
				update("visitation.updateVisitation", visiTo);
				
			}
			BusinessLog.addBusinessLogWithIp(Long.parseLong((String) context.contextMap.get("credit_id")), null, 
					"访厂管理", "业务主管审批", "", "业务主管审批-" + logMsg, 
					1, Long.parseLong(s_employeeId), null, context.contextMap.get("IP").toString());
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
		
	}
	
}
