package com.brick.signOrder.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.signOrder.PdfPageNumerEventHelper;
import com.brick.signOrder.service.SignOrderService;
import com.brick.signOrder.to.SignOrderLogTo;
import com.brick.signOrder.to.SignOrderTo;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.JsonUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SignOrderCommand extends BaseCommand {

	public static final String[] COMPANY_CODE = {"裕融", "裕国"};
	public static final String FILE_TYPE = "signOrder";
	
	Log logger = LogFactory.getLog(SignOrderCommand.class);

	private SignOrderService signOrderService;
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void setSignOrderService(SignOrderService signOrderService) {
		this.signOrderService = signOrderService;
	}
	
	/**
	 * 查询所有签办单页面
	 * @param context
	 */
	public void getAllSignOrders(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		List<Map<String, Object>> flows = null;
		Map<Integer, Map<String, Object>> flowMap = null;
		
		boolean signOrder_selectAll = false;
		String isMy = null;
		
		try {
			
			if(context.contextMap.get("isMy")!=null) {
				isMy = context.contextMap.get("isMy").toString();
			}
			
			signOrder_selectAll = baseService.checkAccessForResource("signOrder_selectAll", String.valueOf(context.contextMap.get("s_employeeId")));
			
			if (signOrder_selectAll == false && !"1".equals(isMy)) {
				isMy = "2";
			}
			if (signOrder_selectAll == true && !"1".equals(isMy)) {
				isMy = "0";
			}
			context.contextMap.put("isMy", isMy);
			
			context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
			Map<String, Object> currentUser = (Map)DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			List<Map<String, Object>> status = (List)DataAccessor.query("signOrder.getStatusByUserId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			List<String> countesignStatus = new ArrayList<String>();
			for (Map<String, Object> map : status) {
				countesignStatus.add(map.get("CODE").toString());
			}
			context.contextMap.put("countesignStatus", countesignStatus);
			context.contextMap.put("CURR_NODE", currentUser.get("NODE"));
			dw = baseService.queryForListWithPaging("signOrder.getAllSignOrders", context.contextMap, "id", ORDER_TYPE.DESC);
			flows = this.signOrderService.getAllFlow();
			flowMap = this.signOrderService.getFlowMapFromList(flows);
			//查询字符串拼接
			StringBuilder urlParam = new StringBuilder("QSTART_DATE=");
			urlParam.append(context.contextMap.get("QSTART_DATE")==null?"":context.contextMap.get("QSTART_DATE").toString());
			urlParam.append("!QEND_DATE=");
			urlParam.append(context.contextMap.get("QEND_DATE")==null?"":context.contextMap.get("QEND_DATE").toString());
			urlParam.append("!QSEARCH_VALUE=");
			urlParam.append(context.contextMap.get("QSEARCH_VALUE")==null?"":context.contextMap.get("QSEARCH_VALUE").toString());
			urlParam.append("!isMy=");
			urlParam.append(context.contextMap.get("isMy")==null?"":context.contextMap.get("isMy").toString());
			urlParam.append("!isApply=");
			urlParam.append(context.contextMap.get("isApply")==null?"":context.contextMap.get("isApply").toString());
			urlParam.append("!current_state=");
			urlParam.append(context.contextMap.get("current_state")==null?"":context.contextMap.get("current_state").toString());
			urlParam.append("!companyCode=");
			urlParam.append(context.contextMap.get("companyCode")==null?"":context.contextMap.get("companyCode").toString());
			urlParam.append("!__currentPage=");
			urlParam.append(context.contextMap.get("__currentPage")==null?"1":context.contextMap.get("__currentPage").toString());
			urlParam.append("!__pageSize=");
			urlParam.append(context.contextMap.get("__pageSize")==null?"10":context.contextMap.get("__pageSize").toString());
			urlParam.append("!__orderBy=");
			urlParam.append(context.contextMap.get("__orderBy")==null?"":context.contextMap.get("__orderBy").toString());
			urlParam.append("!__orderType=");
			urlParam.append(context.contextMap.get("__orderType")==null?"":context.contextMap.get("__orderType").toString());
			outputMap.put("urlParam", urlParam.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("current_state", context.contextMap.get("current_state"));
		outputMap.put("code", context.contextMap.get("code"));
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("isMy", context.contextMap.get("isMy"));
		outputMap.put("isApply", context.contextMap.get("isApply"));
		outputMap.put("dw", dw);
		outputMap.put("flowMap", flowMap);
		outputMap.put("signOrder_selectAll", signOrder_selectAll);
		Output.jspOutput(outputMap, context, "/signOrder/querySignOrder.jsp");
	}
	
	/**
	 * 新增签办单页面
	 * @param context
	 */
	public void addSignOrder(Context context) {
		Map applyUser = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		//所有可会签部门
		List<Map> depts = new ArrayList<Map>();
		//所有已选择会签部门
		List<Map> comCounList = new ArrayList<Map>();
		try {
			// 申请人信息
			context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
			applyUser = (Map) DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//签办单状态
			List<Map<String, Object>> flows = this.signOrderService.getAllFlow();
			//会签名单
			for(Map s : flows){
				int ss = Integer.parseInt(s.get("CODE").toString());
				if(ss > -200 && ss <= -100 && "0".equals(s.get("STATUS").toString())){
					depts.add(s);
				}
			}
			String departmentCode = this.signOrderService.getDepartmentCodeByUserId(Integer.parseInt(applyUser.get("DEPARTMENT").toString()));
			outputMap.put("departmentCode", departmentCode);
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("comCounList", comCounList);
			outputMap.put("depts", depts);
			outputMap.put("applyUser", applyUser);
			outputMap.put("addType", context.contextMap.get("addType"));
			//添加签办单方法传来的添加成功与否状态
			outputMap.put("status", context.contextMap.get("status"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/signOrder/newSignOrder.jsp");
	}

	/**
	 * 添加签办单
	 * @param context
	 */
	public void insertSignOrders(Context context){
		//执行结果是否正常
		String status = "ok";
		Map outputMap = new HashMap();
		try {
			this.signOrderService.insertSignOrder(context);
		} catch (Exception e) {
			e.printStackTrace();
			status = "err";
		}
		if(status.equals("ok")){
			outputMap.put("status", status);
			Output.jspOutput(outputMap, context, "/signOrder/newSignOrder.jsp");
		} else {
			context.contextMap.put("status", status);
			context.contextMap.put("content", context.contextMap.get("content"));
			context.contextMap.put("companyName", context.contextMap.get("companyName"));
			context.contextMap.put("SUMMARY", context.contextMap.get("SUMMARY"));
			context.contextMap.put("departmentCode", context.contextMap.get("departmentCode"));
			this.addSignOrder(context);
		}
	}
	
	/**
	 * 查看签办单详细
	 * @param context
	 */
	public void getSignOrderById(Context context){
		//所有用户
		List<Map<String,String>> users = new ArrayList<Map<String,String>>();
		List<Map<String, Object>> fileList=null;
		List<Map<String, Object>> flows = null;
		Map<Integer, Map<String, Object>> flowMap = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		SignOrderTo signOrderTo = null;
		//所有可会签部门
		List<Integer> countersignList = new ArrayList<Integer>();
		//所有已选择会签部门
		List<Integer> selectedCountersignList = new ArrayList<Integer>();
		//所有后会名单
		List<Integer> lastCountersignList = new ArrayList<Integer>();
		//所有后会完成名单
		List<Integer> completeLastCountersignList = new ArrayList<Integer>();
		//后会用户
		String lastUserIds = "";
		String lastUsers = "";
		//log
		List<SignOrderLogTo> logs = null;
		//log操作类型
		Map<Integer, String> logOpType = null;
		//会签名单
		//List<Integer> countersigns = new ArrayList<Integer>();
		boolean signOrder_passReject = false;		//同意驳回权限
		boolean signOrder_counterSign = false;		//会签权限
		boolean signOrder_manager = false;			//总经理权限
		boolean signOrder_admin = false;			//管理员权限
		boolean signOrder_last = false;
		//主管审核信息
		Map<String, Object> headInfo = null;
		//会签信息
		List<Map<String, Object>> countersignInfo = null;
		//后会信息
		List<Map<String, Object>> lastCountersignInfo = null;
		try {
			//权限
			signOrder_passReject = baseService.checkAccessForResource("signOrder_passReject", String.valueOf(context.contextMap.get("s_employeeId")));
			signOrder_counterSign = baseService.checkAccessForResource("signOrder_counterSign", String.valueOf(context.contextMap.get("s_employeeId")));
			signOrder_manager = baseService.checkAccessForResource("signOrder_manager", String.valueOf(context.contextMap.get("s_employeeId")));
			signOrder_admin = baseService.checkAccessForResource("signOrder_admin", String.valueOf(context.contextMap.get("s_employeeId")));
			signOrder_last = baseService.checkAccessForResource("signOrder_last", String.valueOf(context.contextMap.get("s_employeeId")));
			//查询
			int signOrderId = Integer.parseInt(context.contextMap.get("signOrderId").toString());
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			flows = this.signOrderService.getAllFlow();
			flowMap = this.signOrderService.getFlowMapFromList(flows);
			//查看附件
			String bootPath = "signOrderImage";
			outputMap.put("bootPath", bootPath);
			fileList = this.signOrderService.getUploadFilesBySignOrderId(SignOrderCommand.FILE_TYPE, signOrderId);
			//会签名单
			String countersignString = signOrderTo.getCountersignCodeOrder();
			if(!StringUtils.isEmpty(countersignString)){
				String[] countersignStrings = countersignString.split(",");
				for(String c : countersignStrings){
					selectedCountersignList.add(Integer.parseInt(c));
				}
			}
			for(Map s : flows){
				int ss = Integer.parseInt(s.get("CODE").toString());
				if(ss > -200 && ss <= -100 && "0".equals(s.get("STATUS").toString())){
					boolean isFind = false;
					for(int c : selectedCountersignList){
						if(c == Integer.parseInt(s.get("CODE").toString())){
							isFind = true;
							break;
						}
					}
					if(!isFind){
						countersignList.add(ss);
					}
				}
			}
			//所有用户
			List<Map> allUsers = (List<Map>)DataAccessor.query("demand.getAllUsers",context.contextMap, DataAccessor.RS_TYPE.LIST);
			Map<String, String> tempUser = null;
			for(Map u : allUsers){
				tempUser = new HashMap<String, String>();
				tempUser.put("id", u.get("ID").toString());
				tempUser.put("name", u.get("NAME").toString());
				tempUser.put("email", u.get("EMAIL")==null?"":u.get("EMAIL").toString());
				users.add(tempUser);
			}
			//log信息
			logs = this.signOrderService.getSignOrderLogsById(signOrderId);
			//操作代码-中文map
			logOpType = new HashMap<Integer, String>();
			List<Map> dictionary = (List<Map>) DictionaryUtil.getDictionary("资讯需求单操作类型");
			for(Map d : dictionary){
				logOpType.put(Integer.parseInt(d.get("CODE").toString()), d.get("SHORTNAME").toString());
			}
			//后会名单
			String cc = StringUtils.toStringOrEmpty(signOrderTo.getLastCountersignCodeOrder());
			String[] ccs = cc.split(",");
			for(String c : ccs){
				if(!StringUtils.isEmpty(c)){
					lastCountersignList.add(Integer.parseInt(c));
				}
			}
			context.contextMap.put("ids", lastCountersignList);
			context.contextMap.put("ids1", cc);
			List<Map> tempUsers = new ArrayList<Map>();
			if(lastCountersignList.size() > 0) {
				tempUsers = (List<Map>)DataAccessor.query("signOrder.getUsersByIds",context.contextMap, DataAccessor.RS_TYPE.LIST);
			}
			for(Map<String, Object> u : tempUsers){
				lastUsers = lastUsers + "," + u.get("NAME").toString();
				lastUserIds = lastUserIds + "," + u.get("ID").toString();
			}
			if(!StringUtils.isEmpty(lastUsers)){
				lastUsers = lastUsers.substring(1, lastUsers.length());
				lastUserIds = lastUserIds.substring(1, lastUserIds.length());
			}
			//主管审核信息
			headInfo = (Map)DataAccessor.query("signOrder.getHeadInfoById",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//会签信息
			countersignInfo = (List<Map<String, Object>>)DataAccessor.query("signOrder.getCountersignInfoById",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//后会信息
			lastCountersignInfo = (List<Map<String, Object>>)DataAccessor.query("signOrder.getLastCountersignInfoById",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//判断新处理人是否是自己
			String currentOpId = signOrderTo.getCurrentOperatorId()==null?"":signOrderTo.getCurrentOperatorId().toString();
			if(currentOpId.equals(context.contextMap.get("s_employeeId").toString())){
				outputMap.put("isMySignOrder", "1");
			} else {
				outputMap.put("isMySignOrder", "0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("urlParam", context.contextMap.get("urlParam"));
		outputMap.put("users", JsonUtils.list2json(users));
		outputMap.put("headInfo", headInfo);
		outputMap.put("countersignInfo", countersignInfo);
		outputMap.put("lastCountersignList", lastCountersignList);
		outputMap.put("lastCountersignInfo", lastCountersignInfo);
		outputMap.put("lastUserIds", lastUserIds);
		outputMap.put("lastUsers", lastUsers);
		outputMap.put("logs", logs);
		outputMap.put("logOpType", logOpType);
		outputMap.put("signOrder_passReject", signOrder_passReject);
		outputMap.put("signOrder_counterSign", signOrder_counterSign);
		outputMap.put("signOrder_manager", signOrder_manager);
		outputMap.put("signOrder_admin", signOrder_admin);
		outputMap.put("signOrder_last", signOrder_last);
		outputMap.put("countersignList", countersignList);
		outputMap.put("selectedCountersignList", selectedCountersignList);
		outputMap.put("fileList", fileList);
		outputMap.put("flowMap", flowMap);
		outputMap.put("signOrderTo", signOrderTo);
		outputMap.put("opResultMessage", context.contextMap.get("opResultMessage"));
		Output.jspOutput(outputMap, context, "/signOrder/showSignOrder.jsp");
	}
	
	/**
	 * 走流程
	 * @param context
	 */
	public void next(Context context){
		SignOrderTo signOrderTo = null;
		String opResultMessage = "";
		try {
			//查询
			int signOrderId = Integer.parseInt(context.contextMap.get("signOrderId").toString());
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			Timestamp updateTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("updateTime").toString(), "yyyy-MM-dd HH:mm:ss.SSS").getTime());
			//验证状态
			if(updateTime.equals(signOrderTo.getUpdateTime())){
				int opState = Integer.parseInt(context.contextMap.get("opState").toString());
				signOrderTo = this.signOrderService.updateSignOrder(context, signOrderTo, opState, Integer.parseInt(context.contextMap.get("s_employeeId").toString()), context.contextMap.get("content").toString());
				opResultMessage = "ok";
				//发送邮件
				this.signOrderService.sendSignOrderEmail(signOrderTo, opState);
			} else {
				opResultMessage = "outOfTime";
			}
		} catch (Exception e) {
			e.printStackTrace();
			opResultMessage = e.getMessage();
		}
		context.contextMap.put("opResultMessage", opResultMessage);
		this.getSignOrderById(context);
	}
	
	/**
	 * 更新会签名单
	 * @param context
	 */
	public void updateCountersign(Context context){
		String opResultMessage = "";
		SignOrderTo signOrderTo = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		int signOrderId = StringUtils.ob2int(context.contextMap.get("signOrderId"));
		String countersignCodeOrder = StringUtils.toStringOrEmpty(context.contextMap.get("COUNTERSIGN_CODE_ORDER"));
		int opType = StringUtils.ob2int(context.contextMap.get("opType"));
		Timestamp updateTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("updateTime").toString(), "yyyy-MM-dd HH:mm:ss.SSS").getTime());
		try {
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			//验证状态
			if(updateTime.equals(signOrderTo.getUpdateTime())){
				this.signOrderService.updateCountersign(signOrderId, countersignCodeOrder, opType, Integer.parseInt(context.contextMap.get("s_employeeId").toString()));
			}
			opResultMessage = "ok";
		} catch (Exception e) {
			e.printStackTrace();
			opResultMessage = "更新出错！";
		}
		outputMap.put("urlParam", context.contextMap.get("urlParam"));
		context.contextMap.put("opResultMessage", opResultMessage);
		this.getSignOrderById(context);
	}
	
	/**
	 * 更新后会名单
	 * @param context
	 */
	public void updateLastCountersign(Context context){
		String opResultMessage = "";
		SignOrderTo signOrderTo = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		int signOrderId = StringUtils.ob2int(context.contextMap.get("signOrderId"));
		String lastCountersignCodeOrder = StringUtils.toStringOrEmpty(context.contextMap.get("LAST_COUNTERSIGN_CODE_ORDER"));
		int opType = StringUtils.ob2int(context.contextMap.get("opType"));
		Timestamp updateTime = new Timestamp(DateUtil.strToDate(context.contextMap.get("updateTime").toString(), "yyyy-MM-dd HH:mm:ss.SSS").getTime());
		try {
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			//验证状态
			if(updateTime.equals(signOrderTo.getUpdateTime())){
				this.signOrderService.updateLastCountersign(signOrderId, lastCountersignCodeOrder, opType, Integer.parseInt(context.contextMap.get("s_employeeId").toString()));
			}
			opResultMessage = "ok";
			//发送邮件
			this.signOrderService.sendSignOrderEmail(signOrderTo, opType);
		} catch (Exception e) {
			e.printStackTrace();
			opResultMessage = "更新出错！";
		}
		outputMap.put("urlParam", context.contextMap.get("urlParam"));
		context.contextMap.put("opResultMessage", opResultMessage);
		this.getSignOrderById(context);
	}
	
	/**
	 * 更新签办单页面
	 * @param context
	 */
	public void updateSignOrder(Context context){
		SignOrderTo signOrderTo = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		//所有可会签部门
		List<Map> depts = new ArrayList<Map>();
		//所有已选择会签部门
		List<Map> comCounList = new ArrayList<Map>();
		//附件
		List<Map<String, Object>> fileList = null;
		try {
			//查询
			int signOrderId = Integer.parseInt(context.contextMap.get("signOrderId").toString());
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			//处理已会签部门
			List<Map<String, Object>> flows = this.signOrderService.getAllFlow();
			Map<Integer, Map<String, Object>> flowMap = this.signOrderService.getFlowMapFromList(flows);
			
			String selectCouListString = StringUtils.toStringOrEmpty(signOrderTo.getCountersignCodeOrder());
			for(String code : selectCouListString.split(",")){
				if(!StringUtils.isEmpty(code)){
					comCounList.add(flowMap.get(Integer.parseInt(code)));
				}
			}
			
			//可选会签名单
			String temp = "," + selectCouListString + ",";
			for(Map s : flows){
				String code = s.get("CODE").toString();
				int ss = Integer.parseInt(code);
				if(ss > -200 && ss <= -100 && "0".equals(s.get("STATUS").toString()) && temp.indexOf("," + code + ",") < 0){
					depts.add(s);
				}
			}
			String codeName = this.signOrderService.getCountersignChinese(signOrderTo.getCountersignCodeOrder());
			//查看附件
			String bootPath = "signOrderImage";
			outputMap.put("bootPath", bootPath);
			fileList = this.signOrderService.getUploadFilesBySignOrderId(SignOrderCommand.FILE_TYPE, signOrderId);
			
			outputMap.put("fileList", fileList);
			outputMap.put("codeName", codeName);
			outputMap.put("signOrderTo", signOrderTo);
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("comCounList", comCounList);
			outputMap.put("depts", depts);
			//添加签办单方法传来的添加成功与否状态
			outputMap.put("status", context.contextMap.get("status"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/signOrder/updateSignOrder.jsp");
	}
	
	/**
	 * 修改签办单
	 * @param context
	 */
	public void alterSignOrder(Context context){
		//查询
		SignOrderTo signOrderTo = null;
		String status = "err";
		try {
			//查询
			int signOrderId = Integer.parseInt(context.contextMap.get("signOrderId").toString());
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			signOrderTo.setSummary(String.valueOf(context.contextMap.get("SUMMARY")));
			signOrderTo.setContent(String.valueOf(context.contextMap.get("content")));
			signOrderTo.setCountersignCodeOrder(String.valueOf(context.contextMap.get("chooseCodes")));
			StringBuilder suggest = new StringBuilder("");
			suggest.append("事由：" + signOrderTo.getSummary());
			suggest.append("<br/>签办单内容：" + signOrderTo.getContent());
			suggest.append("<br/>会签名单：" + this.signOrderService.getCountersignChinese(signOrderTo.getCountersignCodeOrder()));
			this.signOrderService.updateSignOrder(context, signOrderTo, Integer.parseInt(context.contextMap.get("opState").toString()), Integer.parseInt(context.contextMap.get("s_employeeId").toString()), suggest.toString());
			status = "ok";
		} catch (NumberFormatException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		context.contextMap.put("status", status);
		this.updateSignOrder(context);
	}
	
	public void transfer(Context context) {
		try {
			this.signOrderService.transfer(Integer.valueOf(context.contextMap.get("signOrderId").toString()), Integer.valueOf(context.contextMap.get("newOpUser").toString()));
			context.contextMap.put("opResultMessage", "转移成功！");
			this.getSignOrderById(context);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	public void export(Context context) {
		
		ByteArrayOutputStream baos = null;
		//所有用户
		List<Map<String,String>> users = new ArrayList<Map<String,String>>();
		List<Map<String, Object>> fileList=null;
		List<Map<String, Object>> flows = null;
		Map<Integer, Map<String, Object>> flowMap = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		SignOrderTo signOrderTo = null;
		//所有可会签部门
		List<Integer> countersignList = new ArrayList<Integer>();
		//所有已选择会签部门
		List<Integer> selectedCountersignList = new ArrayList<Integer>();
		//所有后会名单
		List<Integer> lastCountersignList = new ArrayList<Integer>();
		//所有后会完成名单
		List<Integer> completeLastCountersignList = new ArrayList<Integer>();
		//后会用户
		String lastUserIds = "";
		String lastUsers = "";
		//log
		List<SignOrderLogTo> logs = null;
		//log操作类型
		Map<Integer, String> logOpType = null;
		//主管审核信息
		Map<String, Object> headInfo = null;
		//会签信息
		List<Map<String, Object>> countersignInfo = null;
		//后会信息
		List<Map<String, Object>> lastCountersignInfo = null;
		
		try {
			//查询
			int signOrderId = Integer.parseInt(context.contextMap.get("signOrderId").toString());
			signOrderTo = this.signOrderService.getSignOrderById(signOrderId);
			flows = this.signOrderService.getAllFlow();
			flowMap = this.signOrderService.getFlowMapFromList(flows);
			//查看附件
			String bootPath = "signOrderImage";
			outputMap.put("bootPath", bootPath);
			fileList = this.signOrderService.getUploadFilesBySignOrderId(SignOrderCommand.FILE_TYPE, signOrderId);
			//会签名单
			String countersignString = signOrderTo.getCountersignCodeOrder();
			if(!StringUtils.isEmpty(countersignString)){
				String[] countersignStrings = countersignString.split(",");
				for(String c : countersignStrings){
					selectedCountersignList.add(Integer.parseInt(c));
				}
			}
			for(Map s : flows){
				int ss = Integer.parseInt(s.get("CODE").toString());
				if(ss > -200 && ss <= -100 && "0".equals(s.get("STATUS").toString())){
					boolean isFind = false;
					for(int c : selectedCountersignList){
						if(c == Integer.parseInt(s.get("CODE").toString())){
							isFind = true;
							break;
						}
					}
					if(!isFind){
						countersignList.add(ss);
					}
				}
			}
			//所有用户
			List<Map> allUsers = (List<Map>)DataAccessor.query("demand.getAllUsers",context.contextMap, DataAccessor.RS_TYPE.LIST);
			Map<String, String> tempUser = null;
			for(Map u : allUsers){
				tempUser = new HashMap<String, String>();
				tempUser.put("id", u.get("ID").toString());
				tempUser.put("name", u.get("NAME").toString());
				tempUser.put("email", u.get("EMAIL")==null?"":u.get("EMAIL").toString());
				users.add(tempUser);
			}
			//log信息
			logs = this.signOrderService.getSignOrderLogsById(signOrderId);
			//操作代码-中文map
			logOpType = new HashMap<Integer, String>();
			List<Map> dictionary = (List<Map>) DictionaryUtil.getDictionary("资讯需求单操作类型");
			for(Map d : dictionary){
				logOpType.put(Integer.parseInt(d.get("CODE").toString()), d.get("SHORTNAME").toString());
			}
			//后会名单
			String cc = StringUtils.toStringOrEmpty(signOrderTo.getLastCountersignCodeOrder());
			String[] ccs = cc.split(",");
			for(String c : ccs){
				if(!StringUtils.isEmpty(c)){
					lastCountersignList.add(Integer.parseInt(c));
				}
			}
			context.contextMap.put("ids", lastCountersignList);
			context.contextMap.put("ids1", cc);
			List<Map> tempUsers = new ArrayList<Map>();
			if(lastCountersignList.size() > 0) {
				tempUsers = (List<Map>)DataAccessor.query("signOrder.getUsersByIds",context.contextMap, DataAccessor.RS_TYPE.LIST);
			}
			for(Map<String, Object> u : tempUsers){
				lastUsers = lastUsers + "," + u.get("NAME").toString();
				lastUserIds = lastUserIds + "," + u.get("ID").toString();
			}
			if(!StringUtils.isEmpty(lastUsers)){
				lastUsers = lastUsers.substring(1, lastUsers.length());
				lastUserIds = lastUserIds.substring(1, lastUserIds.length());
			}
			//主管审核信息
			headInfo = (Map)DataAccessor.query("signOrder.getHeadInfoById",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//会签信息
			countersignInfo = (List<Map<String, Object>>)DataAccessor.query("signOrder.getCountersignInfoById",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//后会信息
			lastCountersignInfo = (List<Map<String, Object>>)DataAccessor.query("signOrder.getLastCountersignInfoById",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//判断新处理人是否是自己
			String currentOpId = signOrderTo.getCurrentOperatorId()==null?"":signOrderTo.getCurrentOperatorId().toString();
			if(currentOpId.equals(context.contextMap.get("s_employeeId").toString())){
				outputMap.put("isMySignOrder", "1");
			} else {
				outputMap.put("isMySignOrder", "0");
			}
			
			 // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	       // Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        Font fontTitle = new Font(bfChinese, 22, Font.BOLD);
	        Font fontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        //Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
	        //Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 0, 0, 0, 30); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos).setPageEvent(new PdfPageNumerEventHelper("签办单 " +signOrderTo.getSignCode()));
	        
	        Paragraph headerParagraph = new Paragraph();
	        String image = this.getClass().getResource("/").getPath() + ((signOrderTo.getCompanyCode()==1)?"/pdf_title_1.jpg":"/pdf_title_2.jpg");
	        headerParagraph.add(Image.getInstance(image));
	        HeaderFooter header = new HeaderFooter(headerParagraph, false);
	        header.setBorder(0);
	        header.setAlignment(HeaderFooter.ALIGN_CENTER);
	        document.setHeader(header);
	        
	        // 打开文档
	        document.open();
	        
	        PdfPTable tTitle = new PdfPTable(1);
	        tTitle.setWidthPercentage(90);
			
	        String title = (signOrderTo.getCompanyCode()==1)?"裕融租赁有限公司        签办单":"裕国融资租赁有限公司        签办单";
	        
			PdfPCell objTitle = new PdfPCell(new Phrase(title, fontTitle));
			objTitle.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objTitle.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objTitle.setBorder(0);
			tTitle.addCell(objTitle);
			
			PdfPCell objCode = new PdfPCell(new Phrase("\n编号:" + signOrderTo.getSignCode() + "\n", fontDefault));
			objCode.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			objCode.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCode.setBorder(0);
			tTitle.addCell(objCode);
			
			
			PdfPTable tT = new PdfPTable(5);
			tT.setWidthPercentage(90);
			tT.setWidths(new float[] {0.12f,0.11f,0.40f,0.20f,0.17f});
			
			PdfPCell objCell0 = new PdfPCell(new Phrase("事由", fontDefault));
			objCell0.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell0.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tT.addCell(objCell0);
			
			PdfPCell objCell1 = new PdfPCell(new Phrase(signOrderTo.getSummary(), fontDefault));
			objCell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell1.setColspan(4);
			tT.addCell(objCell1);
			
			PdfPCell objCell21 = new PdfPCell(new Phrase("高阶签核", fontDefault));
			objCell21.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell21.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell21.setRowspan(2);
			tT.addCell(objCell21);
			
			PdfPCell objCell22 = new PdfPCell(new Phrase("签核状况", fontDefault));
			objCell22.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell22.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell22);
			
			PdfPCell objCell23 = new PdfPCell(new Phrase("签核意见", fontDefault));
			objCell23.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell23.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell23);
			
			PdfPCell objCell24 = new PdfPCell(new Phrase("签核人", fontDefault));
			objCell24.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell24.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell24);
			
			PdfPCell objCell25 = new PdfPCell(new Phrase("签核时间", fontDefault));
			objCell25.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell25.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell25);
			
			PdfPCell objCell42 = new PdfPCell(new Phrase(logOpType.get(headInfo.get("SENIOR_OP")), fontDefault));
			objCell42.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell42.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell42);
			
			PdfPCell objCell43 = new PdfPCell(new Phrase(headInfo.get("SENIOR_SUGGEST").toString(), fontDefault));
			objCell43.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell43.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell43);
			
			PdfPCell objCell44 = new PdfPCell(new Phrase(headInfo.get("SENIOR_ORG_NAME").toString() + ((!headInfo.get("SENIOR_ORG_NAME").equals(headInfo.get("SENIOR_NAME")))?("(" + headInfo.get("SENIOR_NAME").toString()+ " 代)"):"") , fontDefault));
			objCell44.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell44.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell44);
			
			PdfPCell objCell45 = new PdfPCell(new Phrase(headInfo.get("SENIOR_TIME").toString().substring(0,19), fontDefault));
			objCell45.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell45.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT.addCell(objCell45);
			
			PdfPCell objCell2 = new PdfPCell(new Phrase("说明:\n" + signOrderTo.getContent(), fontDefault));
			objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell2.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			if (signOrderTo.getContent().toString().length()<500) {
				objCell2.setFixedHeight(380);
			}
			objCell2.setColspan(5);
			tT.addCell(objCell2);
			
			
			PdfPTable tCounter = new PdfPTable(5);
			tCounter.setWidthPercentage(90);
			tCounter.setWidths(new float[] {0.12f,0.11f,0.40f,0.20f,0.17f});
			tCounter.setHeaderRows(1);
			
			PdfPCell objCell51 = new PdfPCell(new Phrase("", fontDefault));
			objCell51.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell51.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell51);
			
			PdfPCell objCell52 = new PdfPCell(new Phrase("会签状况", fontDefault));
			objCell52.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell52.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tCounter.addCell(objCell52);
			
			PdfPCell objCell53 = new PdfPCell(new Phrase("会签意见", fontDefault));
			objCell53.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell53.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tCounter.addCell(objCell53);
			
			PdfPCell objCell54 = new PdfPCell(new Phrase("会签人", fontDefault));
			objCell54.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell54.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tCounter.addCell(objCell54);
			
			PdfPCell objCell55 = new PdfPCell(new Phrase("会签时间", fontDefault));
			objCell55.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell55.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tCounter.addCell(objCell55);
			
			PdfPCell objCell71 = new PdfPCell(new Phrase("单位主管", fontDefault));
			objCell71.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell71.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell71);

			PdfPCell objCell72 = new PdfPCell(new Phrase(logOpType.get(headInfo.get("UP_OP")), fontDefault));
			objCell72.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell72.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell72);
			
			PdfPCell objCell73 = new PdfPCell(new Phrase(headInfo.get("UP_SUGGEST").toString(), fontDefault));
			objCell73.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell73.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell73);
			
			PdfPCell objCell74 = new PdfPCell(new Phrase(headInfo.get("UP_ORG_NAME").toString()
				+ ((!headInfo.get("UP_ORG_NAME").equals(headInfo.get("UP_NAME")))?("(" + headInfo.get("UP_NAME").toString()+ " 代)"):""), fontDefault));
			objCell74.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell74.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell74);
			
			PdfPCell objCell75 = new PdfPCell(new Phrase(headInfo.get("UP_TIME").toString().substring(0,19), fontDefault));
			objCell75.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell75.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell75);
			
			PdfPCell objCell81 = new PdfPCell(new Phrase("处/部级主管", fontDefault));
			objCell81.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell81.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell81);
			
			PdfPCell objCell82 = new PdfPCell(new Phrase(logOpType.get(headInfo.get("UP_UP_OP")), fontDefault));
			objCell82.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell82.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell82);
			
			PdfPCell objCell83 = new PdfPCell(new Phrase(headInfo.get("UP_UP_SUGGEST").toString(), fontDefault));
			objCell83.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell83.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell83);
			
			PdfPCell objCell84 = new PdfPCell(new Phrase(headInfo.get("UP_UP_ORG_NAME").toString()
				+ ((!headInfo.get("UP_UP_ORG_NAME").equals(headInfo.get("UP_UP_NAME")))?("(" + headInfo.get("UP_UP_NAME").toString()+ " 代)"):""), fontDefault));
			objCell84.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell84.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell84);
			
			PdfPCell objCell85 = new PdfPCell(new Phrase(headInfo.get("UP_UP_TIME").toString().substring(0,19), fontDefault));
			objCell85.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell85.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell85);
			
			if (countersignInfo.size() > 0) {
				PdfPCell objCell61 = new PdfPCell(new Phrase("会签情形", fontDefault));
				objCell61.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell61.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				objCell61.setRowspan(countersignInfo.size());
				tCounter.addCell(objCell61);
			}
			
			for(Map item : countersignInfo) {
			
				PdfPCell objCell62 = new PdfPCell(new Phrase(logOpType.get(item.get("OPERATE_STATE")), fontDefault));
				objCell62.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell62.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell62);
				
				PdfPCell objCell63 = new PdfPCell(new Phrase(item.get("OPERATE_SUGGEST").toString(), fontDefault));
				objCell63.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell63.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell63);
				
				PdfPCell objCell64 = new PdfPCell(new Phrase(flowMap.get(item.get("SIGN_STATUS")).get("FLAG").toString()+"\n"+
						item.get("ORG_NAME").toString()+ ((!item.get("ORG_NAME").equals(item.get("NAME")))?("(" + item.get("NAME").toString()+ " 代)"):"") , fontDefault));
				objCell64.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell64.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell64);
				
				PdfPCell objCell65 = new PdfPCell(new Phrase(item.get("OPERATE_TIME_END").toString().substring(0,19), fontDefault));
				objCell65.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell65.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell65);
			}
			
			if(lastCountersignInfo.size()>0) {
				PdfPCell objCell91 = new PdfPCell(new Phrase("后会信息", fontDefault));
				objCell91.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell91.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				objCell91.setRowspan(lastCountersignInfo.size());
				tCounter.addCell(objCell91);
			}
			
			for(Map item : lastCountersignInfo) {
				
				PdfPCell objCell62 = new PdfPCell(new Phrase(logOpType.get(item.get("OPERATE_STATE")), fontDefault));
				objCell62.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell62.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell62);
				
				PdfPCell objCell63 = new PdfPCell(new Phrase(item.get("OPERATE_SUGGEST").toString(), fontDefault));
				objCell63.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell63.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell63);
				
				PdfPCell objCell64 = new PdfPCell(new Phrase(item.get("ORG_NAME").toString()+ ((!item.get("ORG_NAME").equals(item.get("NAME")))?("(" + item.get("NAME").toString()+ " 代)"):"") , fontDefault));
				objCell64.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell64.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell64);
				
				PdfPCell objCell65 = new PdfPCell(new Phrase(item.get("OPERATE_TIME_END").toString().substring(0,19), fontDefault));
				objCell65.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				objCell65.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tCounter.addCell(objCell65);
			}
			
			PdfPCell objCell101 = new PdfPCell(new Phrase("附件", fontDefault));
			objCell101.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell101.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tCounter.addCell(objCell101);
			
			String filenames = "";
			for(Map file : fileList) {
				filenames += file.get("ORG_FILE_NAME").toString()+";";
			}
			PdfPCell objCell102 = new PdfPCell(new Phrase(filenames, fontDefault));
			objCell102.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell102.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell102.setColspan(4);
			tCounter.addCell(objCell102);
			
			document.add(tTitle);
			document.add(tT);
			document.add(tCounter);
			document.close();
			
			// 支付表PDF名字的定义
			String strFileName = "signorder.pdf";
			
		    context.response.setContentType("application/pdf");
		    context.response.setCharacterEncoding("UTF-8");
		    context.response.setHeader("Pragma", "public");
		    context.response.setHeader("Cache-Control",
			    "must-revalidate, post-check=0, pre-check=0");
		    context.response.setDateHeader("Expires", 0);
		    context.response.setHeader("Content-Disposition",
			    "attachment; filename=" + strFileName);
	
		    ServletOutputStream o = context.response.getOutputStream();
	
		    baos.writeTo(o);
		    o.flush();
			closeStream(o);
	
			//记录到系统日志中 add by ShenQi
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("signOrderId")),null,
		   		 "导出 签办单",
	   		 	 "导出 签办单",
	   		 	 null,
	   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在签办单管理的签办单浏览使用导出签办单功能",
	   		 	 1,
	   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	   		 	 DataUtil.longUtil(0),
	   		 	 context.getRequest().getRemoteAddr());
			
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
     * 流关闭操作
     * @param content
     * @param align
     * @param FontDefault
     * @return
     */
    private void closeStream(OutputStream  o){
    	try {
	    
    		o.close();
	    
    	} catch (IOException e) {


    		e.printStackTrace();
    		LogPrint.getLogStackTrace(e, logger);
	    
    	}finally{
	    
    		try {
		
    			o.close();
		
    		} catch (IOException e) {
		 
    			e.printStackTrace();
    			LogPrint.getLogStackTrace(e, logger);
    		}
    	}
	
    }
	
}
