package com.brick.insurance.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.DataDictionaryTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.insurance.service.InsuranceService;
import com.brick.insurance.to.EndorsementsTo;
import com.brick.insurance.to.InsuranceTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;

public class InsuranceCommand extends BaseCommand {
	
	Logger logger = Logger.getLogger(InsuranceCommand.class);
	
	private InsuranceService insuranceService;
	
	private MailUtilService mailUtilService;
	
	public InsuranceService getInsuranceService() {
		return insuranceService;
	}

	public void setInsuranceService(InsuranceService insuranceService) {
		this.insuranceService = insuranceService;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}


	/**
	 * 保单管理页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void getAllInsu(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		List<DataDictionaryTo> listType = null;
		List company = new ArrayList();
		Double totalInsuPrice = 0D;
		String incustatus = null;
		if (StringUtils.isEmpty((String) context.contextMap.get("incustatus"))) {
			incustatus = "";
		} else {
			incustatus = ((String) context.contextMap.get("incustatus")).trim();
		}
		if ((String) context.contextMap.get("status") == null) {
			context.contextMap.put("status", 0);
		}
		context.contextMap.put("incustatus", incustatus);
		if (errList.isEmpty()) {
			try {
				pagingInfo = baseService.queryForListWithPaging("insurance.getAllInsu", context.contextMap, "CREDITRUNCODE", ORDER_TYPE.DESC);
				company = (List<Map<String, Object>>) baseService.queryForList("insuCompany.queryInsureCompanyListForSelect",context.contextMap);
				//批单类型
				listType = baseService.getDataDictionaryByType("批单类型");
				
				//update 修改按钮zhangbo0510增加操作权限
				boolean update=false;
				//repeal 作废按钮
				boolean repeal=false;
				//pay     理赔
				boolean pay=false;
				//quit   退保按钮
				boolean quit=false;
				//更改单的权限语句（共用）
				List<String> resourceIdList=(List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					//below is hard code for ResourceId,we will enhance it in the future
					if("IC-Update".equals(resourceIdList.get(i))) {
						update=true;
					}else if("IC-Repeal".equals(resourceIdList.get(i))) {
						repeal=true;
					}else if("IC-Pay".equals(resourceIdList.get(i))) {
						pay=true;
					}else if("IC-Quit".equals(resourceIdList.get(i))) {
						quit=true;
					}
					outputMap.put("update", update);
					outputMap.put("repeal",repeal);
					outputMap.put("pay", pay);
					outputMap.put("quit", quit);
				}
				InsuranceTo insu = null;
				for (Object o : pagingInfo.getResultList()) {
					insu = (InsuranceTo) o;
					totalInsuPrice += insu.getInsuPrice() == null ? 0 : insu.getInsuPrice();
				}
			} catch (Exception e) {
				errList.add("保单管理 : " + e);
				logger.error(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("totalInsuPrice", totalInsuPrice);
			outputMap.put("isRenewal", context.contextMap.get("isRenewal"));
			outputMap.put("insu_status", context.contextMap.get("insu_status"));
			outputMap.put("incp_id", context.contextMap.get("incp_id"));
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("status", context.contextMap.get("status"));
			outputMap.put("exception_status", context.contextMap.get("exception_status"));
			outputMap.put("start_date_from", context.contextMap.get("start_date_from"));
			outputMap.put("start_date_to", context.contextMap.get("start_date_to"));
			outputMap.put("end_date_from", context.contextMap.get("end_date_from"));
			outputMap.put("end_date_to", context.contextMap.get("end_date_to"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("company", company);
			outputMap.put("listType", listType);
			Output.jspOutput(outputMap, context, "/insurance/insuList/insuListManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 退保管理页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void getSurrender(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		List<DataDictionaryTo> listType = null;
		List company = new ArrayList();
		String incustatus = null;
		if (StringUtils.isEmpty((String) context.contextMap.get("incustatus"))) {
			incustatus = "";
		} else {
			incustatus = ((String) context.contextMap.get("incustatus")).trim();
		}
		if ((String) context.contextMap.get("status") == null) {
			context.contextMap.put("status", 0);
		}
		context.contextMap.put("incustatus", incustatus);
		if (errList.isEmpty()) {
			try {
				pagingInfo = baseService.queryForListWithPaging("insurance.getAllSurrender", context.contextMap, "CREDITRUNCODE", ORDER_TYPE.DESC);
				company = (List<Map<String, Object>>) baseService.queryForList("insuCompany.queryInsureCompanyListForSelect",context.contextMap);
			} catch (Exception e) {
				errList.add("保单管理 : " + e);
				logger.error(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("incp_id", context.contextMap.get("incp_id"));
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("surrenderStatus", context.contextMap.get("surrenderStatus"));
			outputMap.put("start_date_from", context.contextMap.get("start_date_from"));
			outputMap.put("start_date_to", context.contextMap.get("start_date_to"));
			outputMap.put("end_date_from", context.contextMap.get("end_date_from"));
			outputMap.put("end_date_to", context.contextMap.get("end_date_to"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("company", company);
			Output.jspOutput(outputMap, context, "/insurance/surrender/surrenderManageNew.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 显示保单详细信息<br>
	 * 可用于查看或者更新。
	 * @param context
	 */
	public void showInsurance(Context context){
		List errList = context.errList;	
		Map outputMap = new HashMap();
		InsuranceTo insu = null;
		List<Map<String, Object>> eqmtList = null;
		List<DataDictionaryTo> listType = null;
		List<EndorsementsTo> endorsements = null;
		try {
			//保单信息
			insu = (InsuranceTo) baseService.queryForObj("insurance.getInsuInfoForShowOrUpdate", context.contextMap);
			//设备信息
			eqmtList = (List<Map<String, Object>>) baseService.queryForList("insurance.getEqmtInfo", context.contextMap);
			//批单类型
			listType = baseService.getDataDictionaryByType("批单类型");
			//批单
			endorsements = (List<EndorsementsTo>) baseService.queryForList("insurance.getEndorsements", context.contextMap);
			/*List<String> typeDescList = null;
			StringBuffer sb = null;
			for (int j = 0; j < endorsements.size(); j++) {
				typeDescList = (List<String>) baseService.queryForList("insurance.getTypeDesc", endorsements.get(j));
				if (typeDescList != null && typeDescList.size() > 0) {
					sb = new StringBuffer();
					for (int i = 0; i < typeDescList.size(); i++) {
						if (i > 0) {
							sb.append("<br/>");
						}
						sb.append(typeDescList.get(i));
					}
				}
				endorsements.get(j).setListTypeDesc(sb.toString());
			}*/
		} catch (Exception e) { 
			errList.add("读取保单信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("insu", insu);
			outputMap.put("eqmtList",eqmtList);
			outputMap.put("listType", listType);
			outputMap.put("endorsements", endorsements);
			outputMap.put("action_model", context.contextMap.get("action_model"));
			Output.jspOutput(outputMap, context, "/insurance/insuList/insureShow.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 退保单页面
	 * @param context
	 */
	public void showSurrender(Context context){
		List errList = context.errList;	
		Map outputMap = new HashMap();
		InsuranceTo insu = null;
		InsuranceTo surrender = null;
		List<Map<String, Object>> eqmtList = null;
		try {
			//保单信息
			insu = (InsuranceTo) baseService.queryForObj("insurance.getInsuInfoForShowOrUpdate", context.contextMap);
			//设备信息
			eqmtList = (List<Map<String, Object>>) baseService.queryForList("insurance.getEqmtInfo", context.contextMap);
			//退保单信息
			surrender = (InsuranceTo) baseService.queryForObj("insurance.getSurrender", context.contextMap);
		} catch (Exception e) { 
			errList.add("读取保单信息错误!" + e.toString());
			e.printStackTrace();
		}
		
		if (errList.isEmpty()) {
			outputMap.put("insu", insu);
			outputMap.put("surrender", surrender);
			outputMap.put("eqmtList",eqmtList);
			outputMap.put("action_model", context.contextMap.get("action_model"));
			Output.jspOutput(outputMap, context, "/insurance/surrender/surrenderShowNew.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 退保单修改
	 * @param context
	 */
	public void updateSurrender(Context context){
		int surrenderStatus = 0;
		try {
			String surrenderCode = (String) context.contextMap.get("surrenderCode");
			if (!StringUtils.isEmpty(surrenderCode)) {
				surrenderStatus = 10;
			}
			context.contextMap.put("surrenderStatus", surrenderStatus);
			baseService.update("insurance.updateSurrender", context.contextMap);
			context.contextMap.put("surrenderStatus", "");
			getSurrender(context);
		} catch (ServiceException e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	//发起批单
	public void addEndorsements(Context context) throws ServiceException{
		EndorsementsTo e = new EndorsementsTo();
		e.setCreate_by(String.valueOf(context.contextMap.get("s_employeeId")));
		e.setInsuId(Integer.parseInt(String.valueOf(context.contextMap.get("insuId"))));
		StringBuffer sb = new StringBuffer();
		for(String t : HTMLUtil.getParameterValues(context.request, "type", "0")){
			e.setListType(t);
			baseService.insert("insurance.saveEndorsements", e);
		}
		//e.setListType(sb.substring(0, sb.length() - 1));
		//baseService.insert("insurance.saveEndorsements", e);
		showInsurance(context);
	}
	
	
	//保存批单号
	public void saveListCode(Context context) throws ServiceException{
		EndorsementsTo e = new EndorsementsTo();
		e.setId(Integer.parseInt(String.valueOf(context.contextMap.get("id"))));
		if(!StringUtils.isEmpty(String.valueOf(context.contextMap.get("listCode")))){
			e.setListCode(String.valueOf(context.contextMap.get("listCode")));
		}
		if(!StringUtils.isEmpty(String.valueOf(context.contextMap.get("getTime")))){
			e.setGetTime(DateUtil.strToDay(String.valueOf(context.contextMap.get("getTime"))));
		}
		if(!StringUtils.isEmpty(String.valueOf(context.contextMap.get("remark")))){
			e.setRemark(String.valueOf(context.contextMap.get("remark")));
		}
		if(StringUtils.isEmpty(String.valueOf(context.contextMap.get("listCode")))){
			e.setStatus(0);
		} else {
			e.setStatus(1);
		}
		baseService.update("insurance.updateEndorsements", e);
		showInsurance(context);
	}
	
	/**
	 * 更新保单信息操作
	 * @param context
	 */
	public void updateInsuInfo(Context context){
		int insuStatus = 10;
		try {
			String insuCode = (String) context.contextMap.get("insuCode");
			if (!StringUtils.isEmpty(insuCode)) {
				insuStatus = 20;
			}
			context.contextMap.put("insuStatus", insuStatus);
			baseService.update("insurance.updateInsuInfoFromPage", context.contextMap);
			getAllInsu(context);
		} catch (ServiceException e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	/**
	 * 显示投保设置页面
	 * @param context
	 */
	public void showInsuSetPage(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> allOffice = baseService.getAllDecp();
			List<Map<String, Object>> insuCompany = (List<Map<String, Object>>) baseService.queryForList("insurance.getCompany", new HashMap());
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyCode", 1);
			List<Map<String, Object>> insu2Decp_tac = (List<Map<String, Object>>) baseService.queryForList("insurance.getInsu2DecpByCompanyCode", paramMap);
			paramMap.put("companyCode", 2);
			List<Map<String, Object>> insu2Decp_yuguo = (List<Map<String, Object>>) baseService.queryForList("insurance.getInsu2DecpByCompanyCode", paramMap);
			outputMap.put("allOffice", allOffice);
			outputMap.put("insuCompany", insuCompany);
			outputMap.put("insu2Decp_tac", insu2Decp_tac);
			outputMap.put("insu2Decp_yuguo", insu2Decp_yuguo);
			outputMap.put("msg", context.contextMap.get("msg"));
			Output.jspOutput(outputMap, context, "/insurance/insuCompany/insuCompanySet.jsp");
		} catch (ServiceException e) {
			Output.errorPageOutput(e, context);
		}
		
	}
	
	/**
	 * 保存投保设置
	 * @param context
	 */
	public void saveInsuSet(Context context){
		try {
			insuranceService.doSaveInsuSetting(context);
			context.contextMap.put("msg", "保存成功。");
			showInsuSetPage(context);
		} catch (Exception e) {
			e.printStackTrace();
			Output.errorPageOutput(e, context);
		}
	}

	/**
	 * 查询投保单
	 * @param context
	 */
	public void getInsuranceFile(Context context){
		try {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			PagingInfo<Object> pagingInfo = baseService.queryForListWithPaging("insurance.getInseranceFile", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			outputMap.put("pagingInfo", pagingInfo);
			Output.jspOutput(outputMap, context, "/insurance/insuranceFile.jsp");
		} catch (ServiceException e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	/**
	 * 下载投保单
	 * @param context
	 */
	public void downLoadFile(Context context){
		String filePath = (String) context.contextMap.get("filePath");
		try {
			Output.downLoadFile(filePath, context);
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	/**
	 * 发起理赔流程
	 * 并通知法务
	 * @param context
	 * @throws ServiceException 
	 */
	public void addClaimInfo(Context context) throws ServiceException{
		try {
			insuranceService.doAddClaimInfo(context);
			getAllInsu(context);
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
		
	}
	
	public void testInsurance(Context context){
		try {
			insuranceService.doInsurance();
			getAllInsu(context);
		} catch (Exception e) {
			
		}
		
	}
	
	public void testRenewalInsurance(Context context){
		try {
			insuranceService.doRenewalInsuranceService();
			getAllInsu(context);
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	public void testWaitingForSurrender(Context context){
		try {
			insuranceService.doWaitingForSurrender();
			getAllInsu(context);
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	public void testStartInsurance(Context context){
		try {
			String rectId = (String) baseService.queryForObj("insurance.getRectIdByLeaseCode", context.contextMap);
			if (StringUtils.isEmpty(rectId)) {
				throw new Exception("找不到合同");
			}
			insuranceService.startInsurance(rectId, "0");
			getAllInsu(context);
		} catch (Exception e) {
			Output.errorPageOutput(e, context);
		}
	}
	
	public void changeData(Context context){
		boolean flag = false;
		try {
			baseService.update("insurance.changeData", context.contextMap);
			flag = true;
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Output.jsonFlageOutput(flag, context);
	}
	
	
	
	
	
}
