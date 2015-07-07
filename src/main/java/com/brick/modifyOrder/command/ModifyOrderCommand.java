package com.brick.modifyOrder.command;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.modifyOrder.service.ModifyOrderService;
import com.brick.modifyOrder.to.DemandLogTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.brick.util.web.JsonUtils;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ModifyOrderCommand extends BaseCommand {
	Log logger = LogFactory.getLog(ModifyOrderCommand.class);

	private ModifyOrderService modifyOrderService;
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public ModifyOrderService getModifyOrderService() {
		return modifyOrderService;
	}

	public void setModifyOrderService(ModifyOrderService modifyOrderService) {
		this.modifyOrderService = modifyOrderService;
	}

	/**
	 * 新增更改单页面
	 * 
	 * @param context
	 */

	public void modifyOrder(Context context) {
		Map userLogin = null;
		List classList=null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			// 申请人信息
			context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
			userLogin = (Map) DataAccessor.query("modifyOrder.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 数据字典
			context.contextMap.put("dataType", "更改单分类");
			classList=this.baseService.queryForList("modifyOrder.getOrderClassList",context.contextMap);
			outputMap.put("userLogin", userLogin);
			outputMap.put("classList", classList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/modifyOrder/modifyOrder.jsp");

	}

	/**
	 * 添加更改单
	 * 
	 * @param context
	 */
	public void addOrder(Context context) {
		List errList = context.errList;
		try {
			modifyOrderService.add(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		} else {
			context.contextMap.put("errList", errList);
			Output.jspOutput(context.contextMap, context, "/error.jsp");
		}
	}

	/**
	 * 查询更改单列表
	 */
	@SuppressWarnings("unchecked")
	public void queryModifyOrderList(Context context) {
		List errList = context.errList;
		List officeList=null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		List<Map> alterList = null;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		try {
			// 调分页查询方法
			// 只能看到自己申请的更改单，主管只能看本部门的更改单
			//分类 查询
			//办事处列表
			officeList=this.baseService.queryForList("modifyOrder.getDecpBymorder",outputMap);
			String statusQuery =(String)context.contextMap.get("QSELECT_STATUS");
			if(statusQuery==null ||("").equals(statusQuery) || ("-1").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "");
				context.contextMap.put("ORDER_TYPE", "");
			}else if(("0").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "0");
				context.contextMap.put("ORDER_TYPE", "0");
			}else if(("1").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "1");
				context.contextMap.put("ORDER_TYPE", "0");
			}else if(("2").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "2");
				context.contextMap.put("ORDER_TYPE", "0");
			}else if(("3").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "0");
				context.contextMap.put("ORDER_TYPE", "1");
			}else if(("4").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "1");
				context.contextMap.put("ORDER_TYPE", "1");
			}else if(("5").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "2");
				context.contextMap.put("ORDER_TYPE", "1");
			}else if(("6").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "0");
				context.contextMap.put("ORDER_TYPE", "2");
			}else if(("7").equals(statusQuery)){
				//已完成
				context.contextMap.put("ORDER_STATUS", "1");
				context.contextMap.put("ORDER_TYPE", "2");
			}else if(("8").equals(statusQuery)){
				context.contextMap.put("ORDER_STATUS", "2");
				context.contextMap.put("ORDER_TYPE", "2");
			}else if(("9").equals(statusQuery)){
				//已撤案
				context.contextMap.put("ORDER_STATUS", "1");
				context.contextMap.put("ORDER_TYPE", "2");
				context.contextMap.put("STATUS", "2");
			}else if(("10").equals(statusQuery)){
				//未完成
				context.contextMap.put("unEnd", "1");
				context.contextMap.put("ORDER_STATUS", "1");
				context.contextMap.put("ORDER_TYPE", "2");
				//context.contextMap.put("ORDER_STATUS", "1");
				//context.contextMap.put("ORDER_TYPE", "2");
				//context.contextMap.put("STATUS", "2");
			}
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			dw = baseService.queryForListWithPaging("modifyOrder.queryModifyOrderList", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
			alterList=(List<Map>) DataAccessor.query( "modifyOrder.queryAlterUserList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//pass显示通过按钮
			boolean pass=false;
			//nopass显示驳回按钮
			boolean nopass=false;
			//move 转移
			boolean move=false;
			//clpass处理通过按钮
			boolean clpass=false;
			//clnopass显示驳回按钮
			boolean clnopass=false;
			//yspass验收通过按钮
			boolean yspass=false;
			//ysnopass验收驳回按钮
			boolean ysnopass=false;
			
			List<String> resourceIdList=(List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				//below is hard code for ResourceId,we will enhance it in the future
				if("MR-Pass".equals(resourceIdList.get(i))) {
					pass=true;
				}else if("MR-Nopass".equals(resourceIdList.get(i))) {
					nopass=true;
				}else if("MR-Move".equals(resourceIdList.get(i))) {
					move=true;
				}else if("MR-Clpass".equals(resourceIdList.get(i))) {
					clpass=true;
				}else if("MR-Clnopass".equals(resourceIdList.get(i))) {
					clnopass=true;
				}else if("MR-Yspass".equals(resourceIdList.get(i))) {
					yspass=true;
				}else if("MR-Ysnopass".equals(resourceIdList.get(i))) {
					ysnopass=true;
				}
				
			outputMap.put("pass", pass);
			outputMap.put("move",move);
			outputMap.put("nopass", nopass);
			outputMap.put("clpass", clpass);
			outputMap.put("clnopass", clnopass);
			outputMap.put("yspass", yspass);
			outputMap.put("ysnopass", ysnopass);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		
		outputMap.put("officeList", officeList);
		outputMap.put("dw", dw);
		outputMap.put("alterList", alterList);
		outputMap.put("DECP_ID", context.contextMap.get("DECP_ID"));
		outputMap.put("SELECT_ALTER", context.contextMap.get("SELECT_ALTER"));
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("EMPLOYEEID",context.contextMap.get("s_employeeId"));
		
		//page
		outputMap.put("page", dw.getPageNo());
		//pageSize
		outputMap.put("pageSize", dw.getPageSize());
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,
					"/modifyOrder/queryModifyOrder.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 检查当前登陆人未验收的更改单数
	 * @param context
	 */
	public void getUncheckedCount(Context context) {
		int count = 0;
		try {
			count = (Integer)DataAccessor.query("modifyOrder.getUncheckedCount", context.contextMap, RS_TYPE.OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.txtOutput(String.valueOf(count), context);
	}
	
	
	/**
	 * 修改
	 */
	@SuppressWarnings("unchecked")
	public void updateModifyOrder(Context context) {
		Map outputMap = new HashMap();
		List<Map> fileList=null;
		List classList=null;
		try {
		Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
		fileList=(List<Map>) DataAccessor.query( "modifyOrder.showOrderFileList", context.contextMap, DataAccessor.RS_TYPE.LIST);
		// 数据字典
					context.contextMap.put("dataType", "更改单分类");
					classList=this.baseService.queryForList("modifyOrder.getOrderClassList",context.contextMap);
		outputMap.put("orderOne", orderOne);
		outputMap.put("fileList", fileList);
		outputMap.put("classList", classList);
		} catch (Exception e) {
			//e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/modifyOrder/modifyOrder.jsp");
	}
	/**
	 * 更改订单状态
	 */
	@SuppressWarnings("unchecked")
	public void takeOrder(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List<Map> alterUser=null;
		try {
			//查询此更改但详情
			Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//修改人列表
			alterUser=(List<Map>) DataAccessor.query( "modifyOrder.changeAlterUserList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			// 判断是否是通过 1为“通过”
			
			String status =(String)context.contextMap.get("ORDER_STATUS");
			String type =(String)context.contextMap.get("ORDER_TYPE");
			//TODO
			Map orderAlter = context.contextMap;
			//STATUS:0表示未处理，1表示通过，2表示驳回
			if (("1").equals(status)) {
				// 如果是“通过”更改 ORDER_TYPE为0,需分配谁做订单 ORDER_TYPE:0表示申请，1表示处理，2表示验证,
				if(("0").equals(type)){
					// 发送邮件
					MailSettingTo mailSettingTo =new MailSettingTo();
					//0还处于未到处理阶段
					// 随机选取谁来做该订单
					if(alterUser!=null && alterUser.size()>0){
						int max=alterUser.size();
						//查询最大值，如果为0，即都为0 随机分配
						Integer countAlter= (Integer)alterUser.get(max-1).get("COUNTORDER");
						if(countAlter==0){
								int j=(int)(Math.random()*max);
								orderAlter.put("ALTER_USER_ID", alterUser.get(j).get("USER_ID"));
								//发送对象 处理人
								mailSettingTo.setEmailTo((String)alterUser.get(j).get("EMAIL"));
						}else{
							//平均分配 取最小的
							orderAlter.put("ALTER_USER_ID", alterUser.get(0).get("USER_ID"));
							mailSettingTo.setEmailTo((String)alterUser.get(0).get("EMAIL"));
						}
						//邮件内容
						mailSettingTo.setEmailContent(getMailContentPass(orderOne,(String)context.contextMap.get("REMARK")));
						//抄送
						mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
						//邮件主题
						mailSettingTo.setEmailSubject("[资讯通知]：审核通过，等待处理");
						mailUtilService.sendMail(mailSettingTo);
					}
				}else if(("1").equals(type)){
					// 发送邮件
					MailSettingTo mailSettingTo =new MailSettingTo();
					//邮件内容
					mailSettingTo.setEmailContent(getMailContentClPass(orderOne,(String)context.contextMap.get("REMARK")));
					//抄送
					mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
					//验收人的邮箱
					mailSettingTo.setEmailTo((String)orderOne.get("EMAIL"));
					//邮件主题
					mailSettingTo.setEmailSubject("[资讯通知]：处理通过，等待验收");
					
					mailUtilService.sendMail(mailSettingTo);
				}else if(("2").equals(type)){
					// 发送邮件
					MailSettingTo mailSettingTo =new MailSettingTo();
					//邮件内容
					mailSettingTo.setEmailContent(getMailContentOver(orderOne,(String)context.contextMap.get("REMARK")));
					//抄送
					mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL")+";zhangsiqi@tacleasing.cn");
					//验收人的邮箱
					mailSettingTo.setEmailTo((String)orderOne.get("EMAIL"));
					//邮件主题
					mailSettingTo.setEmailSubject("[资讯通知]：更改单验收通过，已完成");
					
					mailUtilService.sendMail(mailSettingTo);
				}
				// 更改订单状态
				DataAccessor.execute("modifyOrder.updateModifyOrder",orderAlter,
						DataAccessor.OPERATION_TYPE.UPDATE);
				context.contextMap.put("REMARK_LOG",  (String)context.contextMap.get("REMARK"));
			}else if(("2").equals(status)){
				if(("2").equals(type) ||("1").equals(type)){
					Map noPassMap = new HashMap();
					//处理驳回、验收驳回，要改回 未处理状态
					noPassMap.put("MODIFY_ID", Integer.valueOf(context.contextMap.get("MODIFY_ID")==null?"0":context.contextMap.get("MODIFY_ID").toString()));
					noPassMap.put("ORDER_STATUS", "1");
					noPassMap.put("ORDER_TYPE","0");
					// 更改订单状态
					
					DataAccessor.execute("modifyOrder.updateModifyOrder",noPassMap,
							DataAccessor.OPERATION_TYPE.UPDATE);
					// 发送邮件
					MailSettingTo mailSettingTo =new MailSettingTo();
					//邮件内容
					mailSettingTo.setEmailContent(getMailContentClNoPass(orderOne,(String)context.contextMap.get("REMARK")));
					//抄送
					mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
					//处理人的邮箱
					mailSettingTo.setEmailTo((String)orderOne.get("ALTER_EMAIL"));
					//邮件主题
					mailSettingTo.setEmailSubject("[资讯通知]：处理被驳回，请重新处理");
					
					mailUtilService.sendMail(mailSettingTo);
				}else if(("0").equals(type)){ 
					// 更改订单状态
					DataAccessor.execute("modifyOrder.updateModifyOrder",orderAlter,
							DataAccessor.OPERATION_TYPE.UPDATE);
					// 发送邮件
					MailSettingTo mailSettingTo =new MailSettingTo();
					//邮件内容
					mailSettingTo.setEmailContent(getMailContentNoPass(orderOne,(String)context.contextMap.get("REMARK")));
					//抄送
					mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
					//验收人的邮箱
					mailSettingTo.setEmailTo((String)orderOne.get("EMAIL"));
					//邮件主题
					mailSettingTo.setEmailSubject("[资讯通知]：申请被驳回");
					
					mailUtilService.sendMail(mailSettingTo);
					}
				context.contextMap.put("REMARK_LOG",  (String)context.contextMap.get("REMARK"));
			}else{
				//增加附件
				modifyOrderService.addOrderImage(context);
				//修改过的更改单 都置为 未审核
				orderAlter.put("ORDER_STATUS", "0");
				orderAlter.put("ORDER_TYPE", "0");
				//修改更改单，格式化
				String intoduction=(String)context.contextMap.get("INTRODUCTION");
				intoduction.replaceAll("<br>", "");
				orderAlter.put("INTRODUCTION", StringUtils.autoInsertWrap(intoduction,40));
				
				DataAccessor.execute("modifyOrder.updateModifyOrder",orderAlter,
						DataAccessor.OPERATION_TYPE.UPDATE);
				// 记录日志
				context.contextMap.put("REMARK_LOG",  "修改更改单内容");
				context.contextMap.put("LOG_IP",context.getRequest().getRemoteAddr());
				//log"4"type修改
				context.contextMap.put("ORDER_TYPE", "4");
			 if(!errList.isEmpty()) {
					context.contextMap.put("errList", errList);
					Output.jspOutput(context.contextMap, context, "/error.jsp");
				}
			}
			// 记录日志
			
			insertOrderLog(context.contextMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			errList.add(e);
			LogPrint.getLogStackTrace(e, logger);
		}
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		
		queryModifyOrderList(context);
	}
	/**
	 * 查看订单详情
	 */
	@SuppressWarnings("unchecked")
	public void getOrderByMid(Context context) {
		List<Map> fileList=null;
		List<Map> logList=null;
		Map outputMap = new HashMap();
		try {
		Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
		//查看附件
		fileList=(List<Map>) DataAccessor.query( "modifyOrder.showOrderFileList", context.contextMap, DataAccessor.RS_TYPE.LIST);
		//查看处理情况
		logList=(List<Map>) DataAccessor.query( "modifyOrder.queryShowLogForOrder",context.contextMap, DataAccessor.RS_TYPE.LIST);
		outputMap.put("logList", logList);
		outputMap.put("orderOne", orderOne);
		outputMap.put("fileList", fileList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspOutput(outputMap, context, "/modifyOrder/showmOrder.jsp");
	}
	/**
	 * 提交处理
	 */
	@SuppressWarnings("unchecked")
	public void updateAlterOrder(Context context) {
		Map outputMap = new HashMap();
		try {
			//查询此更改但详情
			Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map orderAlter = context.contextMap;
			//将ORDER_TYPE 改为 处理 类型：1
			orderAlter.put("ORDER_TYPE", "1");
			//ORDER_STATUS ：0 
			orderAlter.put("ORDER_STATUS", "0");
			// 更改订单状态
			DataAccessor.execute("modifyOrder.updateModifyOrder", orderAlter,
					DataAccessor.OPERATION_TYPE.UPDATE);
			//记录日志
			context.contextMap.put("REMARK_LOG", (String)context.contextMap.get("REMARK_CL"));
			
			insertOrderLog(context.contextMap);
			//发送邮件
			MailSettingTo mailSettingTo =new MailSettingTo();
			//邮件内容
			mailSettingTo.setEmailContent(getMailContentCl(orderOne,(String)context.contextMap.get("REMARK_CL")));
			//抄送
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
			//发送对象 操作人主管
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			//邮件主题
			mailSettingTo.setEmailSubject("[资讯通知]：更改单处理完成，等待处理审核");
			
			mailUtilService.sendMail(mailSettingTo);

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		queryModifyOrderList(context);
	}
	/**
	 * 提交验证
	 */
	@SuppressWarnings("unchecked")
	public void updateAlterOrderYZ(Context context) {
		Map outputMap = new HashMap();
		try {
			//查询此更改但详情
			Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map orderAlter = context.contextMap;
			//将ORDER_TYPE 改为 处理 类型：1
			orderAlter.put("ORDER_TYPE", "2");
			//ORDER_STATUS ：0 
			orderAlter.put("ORDER_STATUS", "0");
			// 更改订单状态
			DataAccessor.execute("modifyOrder.updateModifyOrder", orderAlter,
					DataAccessor.OPERATION_TYPE.UPDATE);
			//记录日志
			context.contextMap.put("REMARK_LOG", (String)context.contextMap.get("REMARK_YZ"));
			insertOrderLog(context.contextMap);
			
			//发送邮件
			MailSettingTo mailSettingTo =new MailSettingTo();
			//邮件内容
			mailSettingTo.setEmailContent(getMailContentYs(orderOne,(String)context.contextMap.get("REMARK_YZ")));
			
			//抄送
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
			//发送对象 操作人主管
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			//邮件主题
			mailSettingTo.setEmailSubject("[资讯通知]：更改单验收完成，等待验收审核");
			
			mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		queryModifyOrderList(context);
	}
	//查看处理人员
	public void alterUser(Context context) {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map> userList = null;
			// 处理人员LIST
			PagingInfo<Object> users = null;
			try {
				// 调分页查询方法
				users = baseService.queryForListWithPaging("modifyOrder.queryAlterUserList",context.contextMap, "USER_STATUS", ORDER_TYPE.DESC);
				//所有职工
				userList=(List<Map>) DataAccessor.query( "modifyOrder.queryUserList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			outputMap.put("users", users);
			outputMap.put("userList", userList);
		Output.jspOutput(outputMap, context, "/modifyOrder/addAlterUser.jsp");

	}
	
	//添加处理人员
	public void addAlterUser(Context context) {
		try {
			DataAccessor.execute("modifyOrder.insertAlterUser", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
			Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.alterUser");
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	//更新处理人员状态
	@SuppressWarnings("unchecked")
	public void updateAlterUser(Context context) {
		try {
			String[] userId = HTMLUtil.getParameterValues(context.getRequest(),
					"USER_CHECK", "");
			DataAccessor.execute("modifyOrder.updateAlterUserSTATUS", context.contextMap,
					DataAccessor.OPERATION_TYPE.UPDATE);
			for (int i = 0; i < userId.length; i++) {
				context.contextMap.put("USER_ID", userId[i]);
				context.contextMap.put("USER_STATUS", "1");
				DataAccessor.execute("modifyOrder.updateAlterUser", context.contextMap,
						DataAccessor.OPERATION_TYPE.UPDATE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.alterUser");
	}
	//转移处理人
	public void updateAlterUserId(Context context) {
		Map outputMap = new HashMap();
		try {
			//之前的处理人
			Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//查询新处理人的邮箱
			Map	moveToUser = (Map) DataAccessor.query("modifyOrder.getEmailById",context.contextMap, DataAccessor.RS_TYPE.MAP);
			DataAccessor.execute("modifyOrder.updateModifyOrder", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			//记录日志
			context.contextMap.put("REMARK_LOG", "更改单转移处理人：由"+(String)orderOne.get("OLDNAME")+"转移给"+(String)moveToUser.get("NAME"));
			//3为转移
			context.contextMap.put("ORDER_TYPE", "3");
			insertOrderLog(context.contextMap);
			//发送邮件
			MailSettingTo mailSettingTo =new MailSettingTo();
			//邮件内容
			mailSettingTo.setEmailContent("[资讯通知]：更改单编号为："+(String)orderOne.get("MODIFY_ORDER_CODE")+"已转移给您处理。");
			//抄送
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
			//发送对象 操作人
			mailSettingTo.setEmailTo((String)moveToUser.get("EMAIL"));
			//邮件主题
			mailSettingTo.setEmailSubject("[资讯通知]：更改单转移处理人");
			
			mailUtilService.sendMail(mailSettingTo);

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		//Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		queryModifyOrderList(context);
	}
	//添加日志//
	public void insertOrderLog(Map map) {
		try {
			//换行格式化
			map.put("REMARK_LOG", StringUtils.autoInsertWrap((String)map.get("REMARK_LOG"),40));
			DataAccessor.execute("modifyOrder.insertOrderLog",map,DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	//查看日志
	@SuppressWarnings("unchecked")
	public void showOrderLog(Context context) {
		List errList = context.errList;
		List<Map> alterList = null;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		try {
			dw = baseService.queryForListWithPaging("modifyOrder.queryShowOrderLog", context.contextMap, "CREATE_TIME", ORDER_TYPE.ASC);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,
					"/modifyOrder/showOrderLog.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//处理
	private String getMailContentCl(Map<String, Object> orderEmail,String remark){
		if (orderEmail == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已处理，需您審批，詳細資訊如下：</font><br><br><br>"
			+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
			+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
			"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
			"<br><font size='2'>修改描述：</font>"+remark);
			sb.append("</html>");
		return sb.toString();
	}
	//处理通过
	private String getMailContentClPass(Map<String, Object> orderEmail,String remark){
		if (orderEmail == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已处理已审核通过，需您验收，詳細資訊如下：</font><br><br><br>"
			+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
			+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
			"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
			"<br><font size='2'>操作描述：</font>"+remark);
			sb.append("</html>");
		return sb.toString();
	}
	
	//处理驳回、验收驳回
	private String getMailContentClNoPass(Map<String, Object> orderEmail,String remark){
		if (orderEmail == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單被驳回，需您重新处理，詳細資訊如下：</font><br><br><br>"
			+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
			+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
			"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
			"<br><font size='2'>操作描述：</font>"+remark);
			sb.append("</html>");
		return sb.toString();
	}
	//初审通过
		private String getMailContentPass(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單初审通过，需您处理，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}
	//提交验收
		private String getMailContentYs(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已验收等待验收审核，需您验收審批，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}	
	//初审被驳回
		private String getMailContentNoPass(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單被驳回，请您及时查看并撤案，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}
	  //更改单撤案
		public void deleteModifyOrder(Context context){
			
			try {
				//查询此更改但详情
				Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
				Integer mId =Integer.valueOf(context.contextMap.get("MODIFY_ID")==null?"0":context.contextMap.get("MODIFY_ID").toString());
				String remarkDelete =(String)context.contextMap.get("REMARK_DELETE");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("MODIFY_ID", mId);
				map.put("REMARK", remarkDelete);
				modifyOrderService.deleteModifyOrder(map);
				//记录日志
				context.contextMap.put("REMARK_LOG",  remarkDelete);
				context.contextMap.put("LOG_IP",context.getRequest().getRemoteAddr());
				//log"5"type作废
				context.contextMap.put("ORDER_TYPE", "5");
				insertOrderLog(context.contextMap);
				//发送邮件
				MailSettingTo mailSettingTo =new MailSettingTo();
				//邮件内容
				mailSettingTo.setEmailContent(getMailContentDelete(orderOne,remarkDelete));
				//抄送
				mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
				//发送对象 操作人
				mailSettingTo.setEmailTo((String)orderOne.get("EMAIL"));
				//邮件主题
				mailSettingTo.setEmailSubject("[资讯通知]：更改单已撤案");
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.queryModifyOrderList");
		}
	 //更改单撤案
		private String getMailContentDelete(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已撤案，请您及时查看，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}
		//更改单已完成
		private String getMailContentOver(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已完成，请您查看，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}
		//未处理直接驳回
		
		@SuppressWarnings("unchecked")
		public void updateNoCl(Context context) {
			Map outputMap = new HashMap();
			try {
				//查询此更改但详情
				Map	orderOne = (Map) DataAccessor.query("modifyOrder.getOrderByMid",context.contextMap, DataAccessor.RS_TYPE.MAP);
				Map orderAlter = context.contextMap;
				//将ORDER_TYPE 改为 处理 类型：0
				orderAlter.put("ORDER_TYPE", "0");
				//ORDER_STATUS ：2 
				orderAlter.put("ORDER_STATUS", "2");
				// 更改订单状态
				DataAccessor.execute("modifyOrder.updateModifyOrder", orderAlter,
						DataAccessor.OPERATION_TYPE.UPDATE);
				//记录日志
				context.contextMap.put("REMARK_LOG", (String)context.contextMap.get("REMARK_NoCL"));
				//log"6"type不予处理
				context.contextMap.put("ORDER_TYPE", "6");
				insertOrderLog(context.contextMap);
				//发送邮件
				MailSettingTo mailSettingTo =new MailSettingTo();
				//邮件内容
				mailSettingTo.setEmailContent(getMailContentNoCl(orderOne,(String)context.contextMap.get("REMARK_NoCL")));
				//抄送
				mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
				//申请人
				mailSettingTo.setEmailTo((String)orderOne.get("EMAIL"));
				//邮件主题
				mailSettingTo.setEmailSubject("[资讯通知]：更改单退回通知，请您查看");
				
				mailUtilService.sendMail(mailSettingTo);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			queryModifyOrderList(context);
		}
		//不予处理
		private String getMailContentNoCl(Map<String, Object> orderEmail,String remark){
			if (orderEmail == null) {
				return null;
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單已被退回，请您及时查看，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>操作描述：</font>"+remark);
				sb.append("</html>");
			return sb.toString();
		}
		//删除附件
		public void updateFile(Context context){
			List errList = context.errList;
			try {
				
				modifyOrderService.updateFile(context.contextMap);
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}if (errList.isEmpty()) {
				updateModifyOrder(context);
			} else {
				context.contextMap.put("errList", errList);
				Output.jspOutput(context.contextMap, context, "/error.jsp");
			}
			
		}
		//quartz将未完成更改单定时发邮件通知
		public void getUnendOrderList() {
					try {
						getLeaderCountForJob();
						getApplyCountListForJob();
						getAtlerCountListForJob();
						getAtlerLeaderCountListForJob();
						//催收资讯需求单当前处理人
						this.emailToDemandToDo();
					} catch (Exception e) {
						e.printStackTrace();
					}
		}
		
		
		//催收领导人
		public void getLeaderCountForJob() throws Exception {
			//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
			try {
				if (baseService.isWorkingDay()==false) {
					return; 
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Map<String,Object> map=new HashMap<String,Object>();
			try {
				//未完成 领导邮件
				List<Map> LeaderList=(List<Map>) DataAccessor.query( "modifyOrder.getLeaderCountForJob", map, DataAccessor.RS_TYPE.LIST);
				if(LeaderList.size()>0){
					for(Map mapEmail :LeaderList){
						map.put("LEADEREMAIL", (String)mapEmail.get("EMAIL"));
						//查询更改单List
						List<Map> mOrderList=(List<Map>) DataAccessor.query( "modifyOrder.getOrderListByLeaderEmail", map, DataAccessor.RS_TYPE.LIST);
						//添加邮件
						MailSettingTo mailSettingTo =new MailSettingTo();
						//邮件内容
						mailSettingTo.setEmailContent(getMailContentUnendOrder(mapEmail,mOrderList));
						//To
						mailSettingTo.setEmailTo((String)mapEmail.get("EMAIL"));
						//邮件主题
						mailSettingTo.setEmailSubject("[系统通知]：更改单催办事宜");
						mailUtilService.sendMail(mailSettingTo);
					}
				}
			} catch (Exception e) {
				throw e;
			}
			
		}
		//催办邮件内容
			private String getMailContentUnendOrder(Map<String, Object> orderEmail,List<Map> mOrderList){
					if (orderEmail == null) {
						return null;
					}
					String code ="";
					if (mOrderList != null) {
						
						for(int i=0;i<mOrderList.size();i++){
							code=code+"<br>"+mOrderList.get(i).get("MODIFY_ORDER_CODE");
						}
					}
					StringBuffer sb = new StringBuffer();
						sb.append("<html><head></head>");
						sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>截至今日08：45，您有"+orderEmail.get("COUNT")
								+"張更改單未处理结束，请您及时查看并处理。<br>更改单编号为："+code+"<br></font><br><br><br>"
						);
						sb.append("</html>");
					return sb.toString();
				}
			
		//催收 申请人
			public void getApplyCountListForJob() throws Exception {
				//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
				try {
					if (baseService.isWorkingDay()==false) {
						return; 
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Map<String,Object> map=new HashMap<String,Object>();
				try {
					//未完成 申请人邮件
					List<Map> LeaderList=(List<Map>) DataAccessor.query( "modifyOrder.getApplyCountForJob", map, DataAccessor.RS_TYPE.LIST);
					if(LeaderList.size()>0){
						for(Map mapEmail :LeaderList){
							map.put("LEADEREMAIL", (String)mapEmail.get("EMAIL"));
							//查询更改单List
							List<Map> mOrderList=(List<Map>) DataAccessor.query( "modifyOrder.getOrderListByApplyEmail", map, DataAccessor.RS_TYPE.LIST);
							//添加邮件
							MailSettingTo mailSettingTo =new MailSettingTo();
							//邮件内容
							mailSettingTo.setEmailContent(getMailContentUnendOrder(mapEmail,mOrderList));
							//To
							mailSettingTo.setEmailTo((String)mapEmail.get("EMAIL"));
							//邮件主题
							mailSettingTo.setEmailSubject("[系统通知]：更改单催办事宜");
							mailUtilService.sendMail(mailSettingTo);
						}
					}
				} catch (Exception e) {
					throw e;
				}
				
			}
			//催收 处理人领导
			public void getAtlerLeaderCountListForJob() throws Exception {
				//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
				try {
					if (baseService.isWorkingDay()==false) {
						return; 
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Map<String,Object> map=new HashMap<String,Object>();
				try {
					//未完成 处理人
					List<Map> LeaderList=(List<Map>) DataAccessor.query( "modifyOrder.getAtlerLeaderCountListForJob", map, DataAccessor.RS_TYPE.LIST);
					if(LeaderList.size()>0){
						for(Map mapEmail :LeaderList){
							map.put("LEADEREMAIL", (String)mapEmail.get("EMAIL"));
							//查询更改单List
							List<Map> mOrderList=(List<Map>) DataAccessor.query( "modifyOrder.getOrderListByAtlerLeaderEmail", map, DataAccessor.RS_TYPE.LIST);
							//添加邮件
							MailSettingTo mailSettingTo =new MailSettingTo();
							//邮件内容
							mailSettingTo.setEmailContent(getMailContentUnendOrder(mapEmail,mOrderList));
							//To
							mailSettingTo.setEmailTo((String)mapEmail.get("EMAIL"));
							//邮件主题
							mailSettingTo.setEmailSubject("[系统通知]：更改单催办事宜");
							mailUtilService.sendMail(mailSettingTo);
						}
					}
				} catch (Exception e) {
					throw e;
				}
				
			}

			/**
			 * 催收 资讯需求单
			 * @throws Exception
			 */
			public void emailToDemandToDo() throws Exception {
				//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
				try {
					if (baseService.isWorkingDay()==false) {
						return; 
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Map<String,Object> map=new HashMap<String,Object>();
				try {
					//当前处理人
					List<Map> LeaderList=(List<Map>) DataAccessor.query("demand.getDemandTodoCountForJob", map, DataAccessor.RS_TYPE.LIST);
					if(LeaderList.size()>0){
						for(Map mapEmail :LeaderList){
							map.put("currentUserId", mapEmail.get("CURRENT_OPERATOR_ID"));
							//查询资讯需求单List
							List<Map> demandList=(List<Map>) DataAccessor.query("demand.getDemandListByAlterEmail", map, DataAccessor.RS_TYPE.LIST);
							//添加邮件
							MailSettingTo mailSettingTo =new MailSettingTo();
							//邮件内容
							mailSettingTo.setEmailContent(this.createDemandMailContent(demandList));
							//To
							mailSettingTo.setEmailTo((String)mapEmail.get("EMAIL"));
							//邮件主题
							mailSettingTo.setEmailSubject("[系统通知]：资讯需求单催办事宜");
							mailUtilService.sendMail(mailSettingTo);
						}
					}
				} catch (Exception e) {
					throw e;
				}
			}
			
			/**
			 * 生成资讯需求单邮件内容
			 * @param resMap
			 * @return
			 */
			private String createDemandMailContent(List<Map> resMap) {
				if(resMap==null) {
					return "";
				}
				StringBuffer mailContent=new StringBuffer();
				mailContent.append("<html><head></head>");
				mailContent.append("<style>.grid_table th {"+
									"border:solid #A6C9E2;"+
									"border-width:0 1px 1px 0;"+
									"background-color: #E1EFFB;"+
									"padding : 2;"+
									"margin : 1;"+
									"font-weight: bold;"+
									"text-align: center;"+
									"color: #2E6E9E;"+
									"height: 28px;"+
									"font-size: 14px;"+
									"font-family: '微软雅黑';"+
									"}" +
									".grid_table td {"+
									"border:solid #A6C9E2;"+
								    "border-width:0 1px 1px 0;"+
								    "text-align: center;"+
									"white-space: nowrap;"+
									"overflow: hidden;"+
									"background-color: #FFFFFF;"+
									"padding : 5px 5px;"+
									"font-size: 12px;"+
									"font-weight: normal;"+
									"color: black;"+
									"font-family: '微软雅黑';"+
									"}" +
									".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
				mailContent.append("<font class='ff'>Greeting:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;截至今日08：45，您有"+resMap.size()+"张资讯需求单未处理结束，请您及时查看并处理：</font><br>");
				mailContent.append("<table class='grid_table'><tr><th>资讯需求单编号</th><th>当前状态</th><th>概要</th><th>收到时间</th></tr>");
				for(Map res : resMap){
					mailContent.append("<tr><td style='text-align: center;'>"+res.get("DEMAND_CODE")+"</td>");
					mailContent.append("<td style='text-align: center;'>"+res.get("FLAG")+"</td>");
					mailContent.append("<td style='text-align: left;'>"+res.get("SUMMARY")+"</td>");
					mailContent.append("<td style='text-align: center;'>"+DateUtil.dateToString((Date)res.get("LAST_OP_TIME"), "yyyy-MM-dd HH:mm")+"</td></tr>");
				}
				mailContent.append("</table></body></html>");
				return mailContent.toString();
			}
			
			//催收 处理人
			public void getAtlerCountListForJob() throws Exception {
				//判断今天是否是工作日，如果是工作日才发送邮件,否则不发邮件
				try {
					if (baseService.isWorkingDay()==false) {
						return; 
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Map<String,Object> map=new HashMap<String,Object>();
				try {
					//未完成 处理人
					List<Map> LeaderList=(List<Map>) DataAccessor.query( "modifyOrder.getAlterCountForJob", map, DataAccessor.RS_TYPE.LIST);
					if(LeaderList.size()>0){
						for(Map mapEmail :LeaderList){
							map.put("LEADEREMAIL", (String)mapEmail.get("EMAIL"));
							//查询更改单List
							List<Map> mOrderList=(List<Map>) DataAccessor.query( "modifyOrder.getOrderListByAlterEmail", map, DataAccessor.RS_TYPE.LIST);
							//添加邮件
							MailSettingTo mailSettingTo =new MailSettingTo();
							//邮件内容
							mailSettingTo.setEmailContent(getMailContentUnendOrder(mapEmail,mOrderList));
							//To
							mailSettingTo.setEmailTo((String)mapEmail.get("EMAIL"));
							//邮件主题
							mailSettingTo.setEmailSubject("[系统通知]：更改单催办事宜");
							mailUtilService.sendMail(mailSettingTo);
						}
					}
				} catch (Exception e) {
					throw e;
				}
				
			}
			
		public static List<Map<String,Object>> queryModifyOrderListForExcel(String content,String employeeId,String status,String endDate,String startDate ,String decpId ) throws Exception {
				List<Map<String,Object>> resultList=null;
				Map paramMap = new HashMap();
				Map rsMap = null;
				paramMap.put("id", employeeId);
				//权限：部分,区域
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				//选择状态
				Map<String,Object> param=new HashMap<String,Object>();
				if(status==null ||("").equals(status) ){
					param.put("ORDER_STATUS", "");
					param.put("ORDER_TYPE", "");
				}else if(("0").equals(status)){
					param.put("ORDER_STATUS", "0");
					param.put("ORDER_TYPE", "0");
				}else if(("1").equals(status)){
					param.put("ORDER_STATUS", "1");
					param.put("ORDER_TYPE", "0");
				}else if(("2").equals(status)){
					param.put("ORDER_STATUS", "2");
					param.put("ORDER_TYPE", "0");
				}else if(("3").equals(status)){
					param.put("ORDER_STATUS", "0");
					param.put("ORDER_TYPE", "1");
				}else if(("4").equals(status)){
					param.put("ORDER_STATUS", "1");
					param.put("ORDER_TYPE", "1");
				}else if(("5").equals(status)){
					param.put("ORDER_STATUS", "2");
					param.put("ORDER_TYPE", "1");
				}else if(("6").equals(status)){
					param.put("ORDER_STATUS", "0");
					param.put("ORDER_TYPE", "2");
				}else if(("7").equals(status)){
					//已完成
					param.put("ORDER_STATUS", "1");
					param.put("ORDER_TYPE", "2");
				}else if(("8").equals(status)){
					param.put("ORDER_STATUS", "2");
					param.put("ORDER_TYPE", "2");
				}else if(("9").equals(status)){
					//已撤案
					param.put("ORDER_STATUS", "1");
					param.put("ORDER_TYPE", "2");
					param.put("STATUS", "2");
				}else if(("10").equals(status)){
					//未完成
					param.put("unEnd", "1");
					param.put("ORDER_STATUS", "1");
					param.put("ORDER_TYPE", "2");
				}
				param.put("p_usernode", rsMap.get("NODE"));
				param.put("QSTART_DATE", startDate);
				param.put("QEND_DATE", endDate);
				param.put("QSEARCH_VALUE", content);
				param.put("DECP_ID", decpId);
				param.put("s_employeeId", employeeId);
				resultList=(List<Map<String,Object>>)DataAccessor.query("modifyOrder.queryModifyOrderListForExcel",param,RS_TYPE.LIST);
				for(Map objectMap:resultList){
					//String STATUS=(String)objectMap.get("STATUS");
					String STATUS= objectMap.get("STATUS")==null?" ":String.valueOf(objectMap.get("STATUS"));
					String ORDER_TYPE= objectMap.get("ORDER_TYPE")==null?" ":String.valueOf(objectMap.get("ORDER_TYPE"));
					String ORDER_STATUS= objectMap.get("ORDER_STATUS")==null?" ":String.valueOf(objectMap.get("ORDER_STATUS"));
						if(STATUS.equals("2")){
							objectMap.put("NOWSTAUS", "已撤案");
						}else{
						if(ORDER_TYPE.equals("0")){
							if(ORDER_STATUS.equals("0")){
								objectMap.put("NOWSTAUS", "未审核");
							}else if(ORDER_STATUS.equals("1")){
								objectMap.put("NOWSTAUS", "已审核未处理");
							}else if(ORDER_STATUS.equals("2")){
								objectMap.put("NOWSTAUS", "驳回");
							}
						}
						if(ORDER_TYPE.equals("1")){
							if(ORDER_STATUS.equals("0")){
								objectMap.put("NOWSTAUS", "已处理未审核");
							}else if(ORDER_STATUS.equals("1")){
								objectMap.put("NOWSTAUS", "已处理已审核");
							}
						}else if(ORDER_TYPE.equals("2")){
							if(ORDER_STATUS.equals("0")){
								objectMap.put("NOWSTAUS", "已验收未审核");
							}else if(ORDER_STATUS.equals("1")){
								objectMap.put("NOWSTAUS", "已完成");
							}
						}
					}
				}
				return resultList;
			}
		
		//查询平均值
		public void getAvgtimeForOrder(Context context) {
			List errList = context.errList;
			Map outputMap = new HashMap();
			try {
				Integer defaultYear = Integer.parseInt(DateUtil.dateToString(new java.util.Date(), "yyyy"));
				String year = (String)context.contextMap.get("year");
				context.contextMap.put("year", (StringUtils.isEmpty(year) ? defaultYear : Integer.parseInt(year)));
				List<Map> list=modifyOrderService.getAvgtimeForOrder(context);
				//总计
				Map  total =modifyOrderService.getTotal(context);
				outputMap.put("total", total);
				outputMap.put("avglist", list);
				outputMap.put("year",context.contextMap.get("year") );
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/modifyOrder/showMonthsList.jsp");
			} else {
				context.contextMap.put("errList", errList);
				Output.jspOutput(context.contextMap, context, "/error.jsp");
			}
		}
		//查看天
		public void showDayList(Context context) {
			List errList = context.errList;
			Map outputMap = new HashMap();
			Integer defaultYear = Integer.parseInt(DateUtil.dateToString(new java.util.Date(), "yyyy"));
			try {
				String year = (String)context.contextMap.get("year");
				String mon=(String)context.contextMap.get("status");
				context.contextMap.put("year", (StringUtils.isEmpty(year) ? defaultYear : Integer.parseInt(year)));
				context.contextMap.put("status", (StringUtils.isEmpty(mon) ? defaultYear : Integer.parseInt(mon)));
				List<Map> list=modifyOrderService.getDayListForOrder(context);
				outputMap.put("list",list );
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/modifyOrder/showDayList.jsp");
			} else {
				context.contextMap.put("errList", errList);
				Output.jspOutput(context.contextMap, context, "/error.jsp");
			}
		}
		//更改单数据统计
		public void showOrderForDept(Context context) {
			List errList = context.errList;
			List classList=null;
			List officeList=null;
			Map outputMap = new HashMap();
			try {
				//更改单分类
				context.contextMap.put("dataType", "更改单分类");
				//所有的分类
				classList=this.baseService.queryForList("modifyOrder.getOrderClassListForCount",context.contextMap);
				//查询办事处
				officeList=this.baseService.queryForList("modifyOrder.getDeptList",outputMap);
				//所有的数据
				List<Map> list=modifyOrderService.showOrderForDept(context);
				//按照分类查合计
				List<Map> listByClass=modifyOrderService.showCountByClass(context);
				//按照办事处查合计
				List<Map> listByDept=modifyOrderService.showCountByDept(context);
				//查询更改单总件数
				Integer couAll =modifyOrderService.getCountAllOrderOfClass(context);
				
				outputMap.put("couAll",couAll );
				outputMap.put("listByClass",listByClass );
				outputMap.put("listByDept",listByDept );
				outputMap.put("list",list );
				outputMap.put("classList",classList );
				outputMap.put("officeList",officeList );
				outputMap.put("START_DATE", context.contextMap.get("START_DATE"));
				outputMap.put("END_DATE", context.contextMap.get("END_DATE"));
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/modifyOrder/showOrderForDept.jsp");
			} else {
				context.contextMap.put("errList", errList);
				Output.jspOutput(context.contextMap, context, "/error.jsp");
			}
		}
		@SuppressWarnings("unchecked")
		public void updateCount(Context context) {
			try {
				Map<String, Object> outputMap = new HashMap<String, Object>();
				DataAccessor.execute("modifyOrder.updateCount", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				outputMap.put("resultFlag", "true");
				Output.jsonOutput(outputMap, context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			//Output.jspSendRedirect(context,"defaultDispatcher?__action=modifyOrderCommand.alterUser");
		}
		
		/**
		 * 查询所有资讯需求单
		 * @param context
		 */
		@SuppressWarnings("unchecked")
		public void getDemandOrder(Context context) {
			Map outputMap = new HashMap();
			PagingInfo<Object> dw = null;
			//所有IT开发人员
			List<Map> its = null;
			//资讯需求单状态字典
			List<Map> states = new ArrayList<Map>();
			Map<Integer, String> stateMap = new HashMap<Integer, String>();
			Map currentUser = null;
			try {
				context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
				currentUser = (Map)DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("CURR_NODE", currentUser.get("NODE"));
				if(context.contextMap.get("demandType") == null){
					context.contextMap.put("demandType",-1);
				}
				
				its = (List<Map>) DataAccessor.query("demand.getITList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//判断当前登陆人是不是IT
				for(Map m : its){
					if(m.get("ID").toString().equals(context.contextMap.get("s_employeeId").toString())){
						outputMap.put("IAmIT", "Y");
						break;
					}
				}
				//第一次查询默认查询自己+分配中，如果非IT则改为查询自己
				if(outputMap.get("IAmIT") == null && "2".equals(context.contextMap.get("isMy"))){
					context.contextMap.put("isMy", 1);
				}
				//IT查询自己+分配中，需删除查询资讯需求单状态条件，否则冲突
				if("2".equals(context.contextMap.get("isMy"))){
					context.contextMap.put("current_state","");
				}
				
				dw = baseService.queryForListWithPaging("demand.queryDemandOrders", context.contextMap, "createTime", ORDER_TYPE.DESC);
				
				states = this.modifyOrderService.getDemandStatesList(context);
				
				//按钮权限,0无权限，1有权限
				String demandPrivateApply = "N";//内部申请
				List<String> resourceIdList = (List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId",context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(resourceIdList != null){
					for (int i = 0; i < resourceIdList.size(); i++) {
						if ("demand-privateApply".equals(resourceIdList.get(i))) {
							demandPrivateApply = "Y";
							break;
						}
					}
				}
				outputMap.put("demandPrivateApply", demandPrivateApply);
				outputMap.put("demandType", context.contextMap.get("demandType"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			for(Map s : states){
				stateMap.put(Integer.parseInt(s.get("CODE").toString()), s.get("SHORTNAME").toString());
			}
			//查询字符串拼接
			StringBuilder urlParam = new StringBuilder("QSTART_DATE=");
			urlParam.append(context.contextMap.get("QSTART_DATE")==null?"":context.contextMap.get("QSTART_DATE").toString());
			urlParam.append("!QEND_DATE=");
			urlParam.append(context.contextMap.get("QEND_DATE")==null?"":context.contextMap.get("QEND_DATE").toString());
			urlParam.append("!QSEARCH_VALUE=");
			urlParam.append(context.contextMap.get("QSEARCH_VALUE")==null?"":context.contextMap.get("QSEARCH_VALUE").toString());
			urlParam.append("!isMy=");
			urlParam.append(context.contextMap.get("isMy")==null?"":context.contextMap.get("isMy").toString());
			urlParam.append("!current_state=");
			urlParam.append(context.contextMap.get("current_state")==null?"":context.contextMap.get("current_state").toString());
			urlParam.append("!current_it=");
			urlParam.append(context.contextMap.get("current_it")==null?"":context.contextMap.get("current_it").toString());
			urlParam.append("!__currentPage=");
			urlParam.append(context.contextMap.get("__currentPage")==null?"1":context.contextMap.get("__currentPage").toString());
			urlParam.append("!__pageSize=");
			urlParam.append(context.contextMap.get("__pageSize")==null?"10":context.contextMap.get("__pageSize").toString());
			urlParam.append("!__orderBy=");
			urlParam.append(context.contextMap.get("__orderBy")==null?"":context.contextMap.get("__orderBy").toString());
			urlParam.append("!__orderType=");
			urlParam.append(context.contextMap.get("__orderType")==null?"":context.contextMap.get("__orderType").toString());

			outputMap.put("urlParam", urlParam.toString());
			outputMap.put("isMy", context.contextMap.get("isMy"));
			outputMap.put("current_state", context.contextMap.get("current_state"));
			outputMap.put("current_it", context.contextMap.get("current_it"));
			outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
			outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			outputMap.put("states", states);
			outputMap.put("stateMap", stateMap);
			outputMap.put("dw", dw);
			outputMap.put("its", its);
			outputMap.put("page", dw.getPageNo());
			outputMap.put("pageSize", dw.getPageSize());
			Output.jspOutput(outputMap, context, "/modifyOrder/queryDemandOrder.jsp");
		}
		
		/**
		 * 查看资讯单详细
		 */
		@SuppressWarnings("unchecked")
		public void getDemandById(Context context) {
			List<Map> fileList=null;
			Map demand = null;
			Map demandLog = null;
			//所有IT开发人员
			List<Map> its = null;
			List<DemandLogTo> demandLogList = null;
			List<Map> countersignLog = new ArrayList<Map>();
			List<Map> countersignLogAll = null;
			//总流程
			List<Integer> process = new ArrayList<Integer>();
			//高阶签核信息
			Map seniorMsg = null;
			//所有会签部门
			List<Map> depts = new ArrayList<Map>();
			Map outputMap = new HashMap();
			Map<Integer, String> stateMap = new HashMap<Integer, String>();
			Map currentUser = null;
			int firstState = 0;
			List<Map<String,String>> users = new ArrayList<Map<String,String>>();
			try {
				its = (List<Map>) DataAccessor.query("demand.getITList",context.contextMap, DataAccessor.RS_TYPE.LIST);
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
				outputMap.put("users", JsonUtils.list2json(users));
				
				context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
				currentUser = (Map)DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("CURR_NODE", currentUser.get("NODE"));
				//按钮权限,0无权限，1有权限
				String add = "N";//添加
				String alter = "N";//修改
				String passReject = "N";//通过&驳回
				String delete = "N";//撤案
				String check = "N";//验收
				String countersign = "N";//会签
				String dev = "N";//开发
				String move = "N";//分配
				String countersignList = "N";//会签名单
				String demandTransfer = "N";//转移
				String demandPrivateApply = "N";//内部申请
				String demandSenior = "N";//高阶签核
				List<String> resourceIdList = (List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId",context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(resourceIdList != null){
					for (int i = 0; i < resourceIdList.size(); i++) {
						if ("demand-add".equals(resourceIdList.get(i))) 					add = "Y";
						else if ("demand-alter".equals(resourceIdList.get(i)))  			alter = "Y";
						else if ("demand-passReject".equals(resourceIdList.get(i))) 		passReject = "Y";
						else if ("demand-delete".equals(resourceIdList.get(i)))  			delete = "Y";
						else if ("demand-check".equals(resourceIdList.get(i)))  			check = "Y";
						else if ("demand-countersign".equals(resourceIdList.get(i))) 		countersign = "Y";
						else if ("demand-dev".equals(resourceIdList.get(i)))  				dev = "Y";
						else if ("demand-move".equals(resourceIdList.get(i)))  				move = "Y";
						else if ("demand-countersign-list".equals(resourceIdList.get(i)))  	countersignList = "Y";
						else if ("demand-transfer".equals(resourceIdList.get(i)))  			demandTransfer = "Y";
						else if ("demand-privateApply".equals(resourceIdList.get(i)))  		demandPrivateApply = "Y";
						else if ("demand-senior".equals(resourceIdList.get(i)))  			demandSenior = "Y";
					}
				}
				outputMap.put("add", add);
				outputMap.put("alter", alter);
				outputMap.put("passReject", passReject);
				outputMap.put("delete", delete);
				outputMap.put("check", check);
				outputMap.put("countersign", countersign);
				outputMap.put("dev", dev);
				outputMap.put("move", move);
				outputMap.put("countersignList", countersignList);
				outputMap.put("demandTransfer", demandTransfer);
				outputMap.put("demandPrivateApply", demandPrivateApply);
				outputMap.put("demandSenior", demandSenior);
				
				demand = (Map)DataAccessor.query("demand.getDemandById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//从日志信息中取出有效数据
				demandLog = (Map)DataAccessor.query("demand.getDemandLogForDemandByDemandOrderId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//日志列表
				demandLogList = (List<DemandLogTo>)DataAccessor.query("demand.getDemandLogsByDemandId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//获得所有会签信息
				countersignLogAll = (List<Map>)DataAccessor.query("demand.getCountersignLogByDemandOrderId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//获取高阶签核信息
				seniorMsg = (Map)DataAccessor.query("demand.getSeniorMsg", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//完成会签的字符串数据
				String countersigns = demand.get("COUNTERSIGN_CODE_ORDER")==null?"":demand.get("COUNTERSIGN_CODE_ORDER").toString();
				//按照会签名单取出有效的会签信息
				if(countersignLog != null && demand != null && !countersigns.equals("")){
					String counCode = demand.get("COMPLETE_CODE_ORDER").toString();
					String[] cCodes = counCode.split(",");
					for(int i = 0; i < cCodes.length; i++){
						for(int j = 0; j < countersignLogAll.size(); j++){
							if(cCodes[i].equals(countersignLogAll.get(j).get("ORDER_STATUS").toString())){
								countersignLog.add(countersignLogAll.get(j));
								continue;
							}
						}
					}
				}
				List<Map> states = this.modifyOrderService.getDemandStatesList(context);
				boolean findFirstState = false;
				for(Map s : states){
					int ss = Integer.parseInt(s.get("CODE").toString());
					//总流程顺序添加会签前流程
					if(demand.get("DEMAND_TYPE").toString().equals("1") && ss > 0 && ss <= 300){
						process.add(ss);
					}
					stateMap.put(ss, s.get("SHORTNAME").toString());
					if(!findFirstState && ss > 0){
						firstState = ss;
						findFirstState = true;
					}
					if(ss > 300 && ss < 350){
						depts.add(s);
					}
				}
				//完成会签
				String[] tempCountersign = countersigns.split(",");
				//已存在会签名单
				List<Map<String, String>> comCounList = new ArrayList<Map<String,String>>();
				//可以会签名单
				List<Map<String, String>> restCounList = new ArrayList<Map<String,String>>();
				for(String tem : tempCountersign){
					//总流程顺序添加会签中流程
					if(!StringUtils.isEmpty(tem) && demand.get("DEMAND_TYPE").toString().equals("1")){
						process.add(Integer.parseInt(tem));
					}
					for(Map dept : depts){
						if(dept.get("CODE").toString().equals(tem)){
							//已会签
							comCounList.add(dept);
							break;
						}
					}
				}
				//总流程顺序添加会签后流程
				for(Map s : states){
					int ss = Integer.parseInt(s.get("CODE").toString());
					//总流程顺序添加会签后流程
					if((ss >= 350 && demand.get("DEMAND_TYPE").toString().equals("1")) || (ss >= 400 && demand.get("DEMAND_TYPE").toString().equals("0"))){
						String seniorSign = demand.get("SENIOR_SIGN")==null?"":demand.get("SENIOR_SIGN").toString();
						if(ss != 360 || (ss == 360 && (seniorSign.equals("1") || seniorSign.equals("2")))){
							process.add(ss);
						}
					}
				}
				
				for(Map dept : depts){
					if("0".equals(dept.get("STATUS").toString()) && countersigns.indexOf(dept.get("CODE").toString()) < 0){
						restCounList.add(dept);
					}
				}
				outputMap.put("comCounList", comCounList);
				outputMap.put("restCounList", restCounList);
				
				List<String> deptNames = new ArrayList<String>();
				if(demand.get("COUNTERSIGN_CODE_ORDER") != null && !demand.get("COUNTERSIGN_CODE_ORDER").toString().equals("")){
					String[] needToCountersignDepts = demand.get("COUNTERSIGN_CODE_ORDER").toString().split(",");
					for(int i = 0; i < needToCountersignDepts.length; i++){
						deptNames.add(stateMap.get(Integer.parseInt(needToCountersignDepts[i])));
					}
				}
				//操作代码-中文map
				Map<Integer, String> logOpType = new HashMap<Integer, String>();
				List<Map> dictionary = (List<Map>) DictionaryUtil.getDictionary("资讯需求单操作类型");
				for(Map d : dictionary){
					logOpType.put(Integer.parseInt(d.get("CODE").toString()), d.get("SHORTNAME").toString());
				}
				
				List<Map> alterTypeList = (List<Map>) DictionaryUtil.getDictionary("资讯单修改类型");
				//查看附件
				context.contextMap.put("fileType", "demand");
				fileList=(List<Map>) DataAccessor.query("demand.getFilesByDemandId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				String bootPath = "demandImage";
				//判断新处理人是否是自己
				String currentOpId = demand.get("CURRENT_OPERATOR_ID")==null?"":demand.get("CURRENT_OPERATOR_ID").toString();
				if(currentOpId.equals(context.contextMap.get("s_employeeId").toString())){
					outputMap.put("isMyDemand", "1");
				} else {
					outputMap.put("isMyDemand", "0");
				}
				
				outputMap.put("alterTypeList", alterTypeList);
				outputMap.put("process", process);
				outputMap.put("urlParam", context.contextMap.get("urlParam"));
				outputMap.put("its", its);
				outputMap.put("logOpType", logOpType);
				outputMap.put("firstState", firstState);
				outputMap.put("bootPath", bootPath);
				outputMap.put("fileList", fileList);
				outputMap.put("deptNames", deptNames);
				outputMap.put("demand", demand);
				outputMap.put("demandLog", demandLog);
				outputMap.put("demandLogList", demandLogList);
				outputMap.put("countersignLog", countersignLog);
				outputMap.put("seniorMsg", seniorMsg);
				outputMap.put("stateMap", stateMap);
				outputMap.put("opResultMessage", context.contextMap.get("opResultMessage"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspOutput(outputMap, context, "/modifyOrder/showDemand.jsp");
		}

		/**
		 * 新增资讯需求单
		 * @param context
		 */
		public void addDemand(Context context) {
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
				//会签名单
				List<Map> states = this.modifyOrderService.getDemandStatesList(context);
				for(Map s : states){
					int ss = Integer.parseInt(s.get("CODE").toString());
					if(ss > 300 && ss < 350 && "0".equals(s.get("STATUS").toString())){
						//默认选中资讯和业管
						if(ss == 302 || ss == 309){
							comCounList.add(s);
						} else {
							depts.add(s);
						}
					}
				}
				outputMap.put("comCounList", comCounList);
				outputMap.put("depts", depts);
				outputMap.put("applyUser", applyUser);
				outputMap.put("addType", context.contextMap.get("addType"));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspOutput(outputMap, context, "/modifyOrder/newDemandOrder.jsp");
		}
		
		/**
		 * 更新资讯需求单
		 * @param context
		 */
		public void updateDemand(Context context) {
			List<Map> fileList=null;
			String bootPath = "demandImage";
			Map demand = null;
			Map<String, Object> outputMap = new HashMap<String, Object>();
			//所有可会签部门
			List<Map> depts = new ArrayList<Map>();
			//所有已选择会签部门
			List<Map> comCounList = new ArrayList<Map>();
			try {
				demand = (Map) DataAccessor.query("demand.getDemandById",context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查看附件
				context.contextMap.put("fileType", "demand");
				fileList=(List<Map>) DataAccessor.query("demand.getFilesByDemandId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//会签名单
				List<Map> states = this.modifyOrderService.getDemandStatesList(context);
				String countersigns = demand.get("COUNTERSIGN_CODE_ORDER")==null?"":demand.get("COUNTERSIGN_CODE_ORDER").toString();
				String[] tempCountersign = countersigns.split(",");
				
				for(String tem : tempCountersign){
					for(Map s : states){
						if(tem.equals(s.get("CODE").toString())){
							//已选择会签
							comCounList.add(s);
							break;
						}
					}
				}
				for(Map d : states){
					int ss = Integer.parseInt(d.get("CODE").toString());
					if(ss > 300 && ss < 350 && "0".equals(d.get("STATUS").toString()) && countersigns.indexOf(d.get("CODE").toString()) < 0){
						depts.add(d);
					}
				}
				
				outputMap.put("comCounList", comCounList);
				outputMap.put("depts", depts);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			outputMap.put("bootPath", bootPath);
			outputMap.put("fileList", fileList);
			outputMap.put("demand", demand);
			Output.jspOutput(outputMap, context, "/modifyOrder/updateDemandOrder.jsp");
		}
		
		/**
		 * 创建资讯需求单
		 * @param context
		 */
		public void newDemand(Context context){
			//附件上传
			this.uploadFiles(context);
			List errList = context.errList;
			Map outputMap = new HashMap();
			SqlMapClient sqlMapper = null;
			//执行结果是否正常
			String status = "ok";
			if (errList.isEmpty()) {
				try {
					sqlMapper = DataAccessor.getSession();
					sqlMapper.startTransaction();
					Map maxDemand = (Map)sqlMapper.queryForObject("demand.getMaxDemandCode", context.contextMap);
					//demandCode格式为IT201402001
					String maxDemandCode = "";
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
					int currentDemandCodeNumber = Integer.parseInt(sdf.format(new Date()));
					//首次添加第一条记录
					if(maxDemand == null || maxDemand.get("DEMAND_CODE") == null || "".equals(maxDemand.get("DEMAND_CODE").toString())){
						currentDemandCodeNumber = currentDemandCodeNumber * 1000 + 1;
					} else {
						maxDemandCode = maxDemand.get("DEMAND_CODE").toString();
						int maxDemandCodeNumber = Integer.parseInt(maxDemandCode.substring(2, maxDemandCode.length()));
						//当前月不等于数据库最近一条code月份，即到次月第一条记录
						if((maxDemandCodeNumber / 1000) != currentDemandCodeNumber){
							currentDemandCodeNumber = currentDemandCodeNumber * 1000 + 1;
						} else {
							//任然是当月
							currentDemandCodeNumber = maxDemandCodeNumber + 1;
						}
					}
					context.contextMap.put("demandCode", "IT" + currentDemandCodeNumber);
					context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
					Map currentUser = (Map)sqlMapper.queryForObject("demand.queryUserByUid", context.contextMap);
					context.contextMap.put("APPLY_DEPARTMENT_ID", currentUser.get("DEPT_ID"));
					context.contextMap.put("COMPANY_ID", currentUser.get("DECP_ID"));
					List<Map> states = this.modifyOrderService.getDemandStatesList(context);
					int ORDER_STATUS = 0;
					String codeName = "";
					String addType = context.contextMap.get("addType")==null?"":context.contextMap.get("addType").toString();
					int currentOperatorId = 0;
					//资讯需求单类型，1普通，0内部
					int demandType = 1;
					for(int i = 0; i < states.size(); i++){
						int code = Integer.parseInt(states.get(i).get("CODE").toString());
						//内部申请
						if(addType.equals("private")){
							if(code == 400){
								ORDER_STATUS = code;
								codeName = states.get(i).get("SHORTNAME").toString();
								//资讯主管
								currentOperatorId = this.modifyOrderService.getDemandStateByDemandCode(309, states);
								demandType = 0;
								break;
							}
						} else {
							if(code > 0){
								//CODE>0为正常流程状态，取最小值为工作流程的第一步
								ORDER_STATUS = code;
								codeName = states.get(i).get("SHORTNAME").toString();
								currentOperatorId = Integer.parseInt(currentUser.get("HANDLE_UP_USER_ID").toString());
								break;
							}
						}
					}
					Date date = new Date();
					context.contextMap.put("ORDER_STATUS", ORDER_STATUS);
					context.contextMap.put("CREATE_TIME", date);
					context.contextMap.put("LAST_OP_TIME", date);
					context.contextMap.put("CURRENT_OPERATOR_ID", currentOperatorId);
					context.contextMap.put("demandType", demandType);
					//判断处级单位数量，暂时用会签字符串中最后一个字为“处”字的部门数，之后考虑修改成新表关联
					Map<String, String> stateMap = new HashMap<String, String>();
					for(Map s : states){
						stateMap.put(s.get("CODE").toString(), s.get("SHORTNAME").toString());
					}
					int count = this.modifyOrderService.getCodeNamesCount(context.contextMap.get("chooseCodes")==null?"":context.contextMap.get("chooseCodes").toString(),stateMap);
					if(count >= 2){
						context.contextMap.put("seniorSign", 1);
					}
					int id = (Integer) sqlMapper.insert("demand.insertDemandOrders", context.contextMap);
					context.contextMap.remove("demandType");
					//附件关联
					String files = context.contextMap.get("ids")==null?"":context.contextMap.get("ids").toString();
					if(!files.equals("")){
						context.contextMap.put("demandIdForFile", id);
						context.contextMap.put("files", files);
						context.contextMap.put("fileType", "demand");
						sqlMapper.update("demand.updateDemandFiles", context.contextMap);
					}
					//插入日志
					Map param = new HashMap();
					param.put("demandId", id);
					param.put("ORDER_STATUS", null);
					param.put("OPERATE_TIME_BEGIN", date);
					param.put("OPERATE_TIME_END", date);
					param.put("s_employeeId", context.contextMap.get("s_employeeId"));
					param.put("OPERATE_STATE", 1);
					StringBuilder content = new StringBuilder("资讯编号：" + "IT" + currentDemandCodeNumber);
					content.append("<br/>申请人：" + context.contextMap.get("s_employeeName").toString());
					content.append("<br/>申请时间：" + sdf.format(date));
					content.append("<br/>希望完成日期：" + context.contextMap.get("hopeDate"));
					content.append("<br/>摘要：" + context.contextMap.get("SUMMARY"));
					if(context.contextMap.get("chooseCodesName") != null){
						content.append("<br/>会签名单：" + context.contextMap.get("chooseCodesName"));
						String isSenior = "否";
						if("1".equals(context.contextMap.get("seniorSign")==null?"0":context.contextMap.get("seniorSign").toString())){
							isSenior = "是";
						}
						content.append("<br/>是否高阶签核：" + isSenior);
					}
					content.append("<br/>需求内容：" + context.contextMap.get("content"));
					//对应附件
					param.put("fileType", "demand");
					List<Map> fileList = (List<Map>) DataAccessor.query("demand.getFilesByDemandId", param, DataAccessor.RS_TYPE.LIST);
					if(fileList != null && fileList.size() > 0){
						content.append("<br/>附件：");
						for(Map f : fileList){
							content.append("<br/>" + f.get("ORG_FILE_NAME"));
						}
					}
					param.put("content", content.toString());
					sqlMapper.insert("demand.insertDemandLog", param);
			    	sqlMapper.commitTransaction();
			    	
			    	//mail
			    	Map<String, String> demand = (Map)DataAccessor.query("demand.getDemandById", param, DataAccessor.RS_TYPE.MAP);
			    	demand.put("CODE_NAME", codeName);
			    	MailSettingTo mailSettingTo =new MailSettingTo();
					//发送处理人
					mailSettingTo.setEmailTo(currentUser.get("HANDLE_UP_USER_EMAIL").toString());
					//邮件主题
					mailSettingTo.setEmailSubject("[资讯通知]：有一张新的资讯需求单已提交，等待您处理");
					//邮件内容
					mailSettingTo.setEmailContent(this.modifyOrderService.getDemandMailContent(demand));
					mailUtilService.sendMail(mailSettingTo);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					status = "err";
					errList.add(e);
				} finally { 
					try {
						sqlMapper.endTransaction();
					} catch (SQLException e) { 
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						errList.add(e);
						status = "err";
					} 
				}
			}
			if(status.equals("ok")){
				this.getDemandOrder(context);
			} else {
				outputMap.put("status", status);
				outputMap.put("content", context.contextMap.get("content"));
				outputMap.put("hopeDate", context.contextMap.get("hopeDate"));
				Output.jspOutput(outputMap, context, "/modifyOrder/newDemandOrder.jsp");
			}
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
						String title = "资讯需求单附件";
						String filePath = fileItem.getName();
						String type = filePath.substring(filePath.lastIndexOf(".") + 1);
						List errList = context.errList;
						Map contextMap = context.contextMap;
						String xmlPath = "demandImage";
						String bootPath = this.modifyOrderService.getDemandPath(xmlPath);
						if (bootPath != null) {
							File realPath = new File(bootPath + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
							if (!realPath.exists()){
								realPath.mkdirs();
							}
							String imageName = FileExcelUpload.getNewFileName();
							File uploadedFile = new File(realPath.getPath() + File.separator + imageName + "." + type);
							file_path = '/' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + '/' + type + '/'+ imageName + "."+ type;
							try {
								if (errList.isEmpty()) {
									fileItem.write(uploadedFile);
									//增加关联
									contextMap.put("path", file_path);
									contextMap.put("fileName", fileItem.getName().replaceAll(" ", ""));
									contextMap.put("title", title);
									contextMap.put("fileType", "demand");
									contextMap.put("userId", context.contextMap.get("s_employeeId"));
									contextMap.put("date", new Date());
									int fId = (Integer)DataAccessor.execute("demand.insertDemandFile", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
									if(ids.equals("")){
										ids = Integer.toString(fId);
									} else {
										ids = ids + "," + Integer.toString(fId);
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
			}
			Map<String,String> outputMap = new HashMap<String,String>();
			context.contextMap.put("ids", ids);
			context.contextMap.put("err", err);
		}
		
		/**
		 * 修改资讯需求单
		 * @param context
		 */
		public void editDemand(Context context){
			//附件上传
			this.uploadFiles(context);
			Map outputMap = new HashMap();
			SqlMapClient sqlMapper = null;
			try {
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				//修改前资讯单
				Map demand = (Map)sqlMapper.queryForObject("demand.getDemandById", context.contextMap);
				String hours = context.contextMap.get("HOURS")==null?"":context.contextMap.get("HOURS").toString();
				if(hours.equals("")){
					context.contextMap.put("HOURS", null);
				}
				Date date = new Date();
				context.contextMap.put("LAST_OP_TIME", date);
				int seniorSign = 0;
				if(context.contextMap.get("seniorSign")!=null){
					seniorSign = 1;
				}
				if(seniorSign == 0){
					//资讯单状态列表
					List<Map> states = this.modifyOrderService.getDemandStatesList(context);
					Map<String, String> stateMap = new HashMap<String, String>();
					for(Map s : states){
						stateMap.put(s.get("CODE").toString(), s.get("SHORTNAME").toString());
					}
					int count = this.modifyOrderService.getCodeNamesCount(context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString(), stateMap);
					if(count >= 2){
						seniorSign = 1;
					}
				}
				context.contextMap.put("seniorSign", seniorSign);
				sqlMapper.update("demand.updateDemandById", context.contextMap);
				//附件关联
				String files = context.contextMap.get("ids")==null?"":context.contextMap.get("ids").toString();
				if(!files.equals("")){
					context.contextMap.put("demandIdForFile", context.contextMap.get("demandId"));
					context.contextMap.put("files", files);
					context.contextMap.put("fileType", "demand");
					sqlMapper.update("demand.updateDemandFiles", context.contextMap);
				}	
				//删除附件
				String delFiles = context.contextMap.get("fileDelIds")==null?"":context.contextMap.get("fileDelIds").toString();
				if(!delFiles.equals("")){
					context.contextMap.put("delFiles", delFiles);
					sqlMapper.update("demand.delDemandFiles", context.contextMap);
				}
				//对应附件
				context.contextMap.put("fileType", "demand");
				List<Map> fileList = (List<Map>) DataAccessor.query("demand.getFilesByDemandId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//添加日志
				Map param = new HashMap();
				param.put("demandId", context.contextMap.get("demandId"));
				param.put("s_employeeId", context.contextMap.get("s_employeeId"));
				param.put("ORDER_STATUS", demand.get("ORDER_STATUS"));
				param.put("OPERATE_TIME_BEGIN", demand.get("LAST_OP_TIME"));
				param.put("OPERATE_TIME_END", date);
				param.put("OPERATE_STATE", 2);
				StringBuilder content = new StringBuilder("希望完成日期：" + context.contextMap.get("HOPE_COMPLETE_DATE"));
				content.append("<br/>摘要：" + context.contextMap.get("SUMMARY"));
				if(context.contextMap.get("chooseCodesName") != null){
					content.append("<br/>会签名单：" + context.contextMap.get("chooseCodesName"));
					String isSenior = "否";
					if("1".equals(context.contextMap.get("seniorSign")==null?"":context.contextMap.get("seniorSign").toString())){
						isSenior = "是";
					}
					content.append("<br/>是否高阶签核：" + isSenior);
				}
				content.append("<br/>需求内容：" + context.contextMap.get("CONTENT"));
				if(fileList != null && fileList.size() > 0){
					content.append("<br/>附件：");
					for(Map f : fileList){
						content.append("<br/>" + f.get("ORG_FILE_NAME"));
					}
				}
				param.put("content", content.toString());
				sqlMapper.insert("demand.insertDemandLog", param);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			} finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} 
			}
			this.getDemandOrder(context);
		}
		
		/**
		 * 添加资讯单操作日志，走流程
		 * @param context
		 */
		@SuppressWarnings("unchecked")
		public void addDemandLog(Context context) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			Map<String, Object> result = new HashMap<String, Object>();
			SqlMapClient sqlMapper = null;
			//操作结果提示信息
			String opResultMessage = "";
			try {
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				//资讯单状态列表
				List<Map> states = this.modifyOrderService.getDemandStatesList(context);
				Map<String, String> stateMap = new HashMap<String, String>();
				for(Map s : states){
					stateMap.put(s.get("CODE").toString(), s.get("SHORTNAME").toString());
				}
				context.contextMap.put("states", states);
				context.contextMap.put("stateMap", stateMap);
				Date date = new Date();
				context.contextMap.put("currentTime", date);
				context.contextMap.put("sqlMapper", sqlMapper);
				//根据当前状态获取下一个状态和处理信息
				Map newDemandInfo = this.modifyOrderService.getUserIdByDemandStatus(context);
				Map param = new HashMap();
				int newStatus = Integer.parseInt(newDemandInfo.get("newStatus").toString());
				//更新后状态
				param.put("ORDER_STATUS", newStatus);
				param.put("CURRENT_OPERATOR_ID", newDemandInfo.get("newUserId")==null?"0":newDemandInfo.get("newUserId"));
				param.put("demandId", newDemandInfo.get("ID"));
				param.put("LAST_OP_TIME", date);
				param.put("HOURS", newDemandInfo.get("HOURS"));
				param.put("PREDICT_DATE", newDemandInfo.get("PREDICT_DATE"));
				param.put("ALTER_TYPE", newDemandInfo.get("ALTER_TYPE"));
				param.put("SYS_LEVEL", newDemandInfo.get("SYS_LEVEL"));
				param.put("RELEASE_TIME", newDemandInfo.get("RELEASE_TIME"));
				param.put("COUNTERSIGN_CODE_ORDER", context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString());
				param.put("COMPLETE_CODE_ORDER", newDemandInfo.get("COMPLETE_CODE_ORDER"));
				param.put("RESPONSIBLE_USER_ID", newDemandInfo.get("RESPONSIBLE_USER_ID")==null?"":newDemandInfo.get("RESPONSIBLE_USER_ID"));
				sqlMapper.update("demand.updateDemandById", param);
				
				//插日志
				param.put("s_employeeId", context.contextMap.get("s_employeeId"));
				param.put("OPERATE_TIME_BEGIN", newDemandInfo.get("LAST_OP_TIME"));
				param.put("OPERATE_TIME_END", date);
				if(context.contextMap.get("content") != null){
					param.put("content", context.contextMap.get("content").toString());
				}
				//当前操作的旧状态
				param.put("ORDER_STATUS", newDemandInfo.get("ORDER_STATUS"));
				int opType = Integer.parseInt(context.contextMap.get("opType").toString());
				switch (opType) {
					case 4:
					case 5:
						//4:	添加会签名单
						//5:	修改会签名单
						String deptIds = context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString();
						String deptNames = "";
						if(!deptIds.equals("")){
							String[] codes = deptIds.split(",");
							for(int i = 0; i < codes.length; i++){
								deptNames = deptNames + "," + stateMap.get(codes[i]);
							}
							deptNames = deptNames.substring(1);
						}
						if(opType == 4){
							param.put("content", "添加会签名单：" + deptNames);
						} else {
							param.put("content", "会签名单修改为：" + deptNames);
						}
						//高阶会签
						if(context.contextMap.get("seniorSign") != null && !context.contextMap.get("seniorSign").toString().equals("0")){
							param.put("content", param.get("content") + "<br/>" + "是否高阶签核：是");
						} else {
							param.put("content", param.get("content") + "<br/>" + "是否高阶签核：否");
						}
						opResultMessage = "会签名单修改成功！";
						break;
					case 7:
						//分配
						context.contextMap.put("U_ID", Integer.parseInt(newDemandInfo.get("RESPONSIBLE_USER_ID").toString()));
						Map user = (Map) DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
						param.put("content", "分配给开发人员：" + user.get("NAME"));
						opResultMessage = "分配成功！";
						break;
					case 8:
						//开发确认
						param.put("content", "预计工时：" + newDemandInfo.get("HOURS") + " 小时<br/>预计完成日期：" + newDemandInfo.get("PREDICT_DATE"));
						opResultMessage = "提交成功！";
						break;
					case 9:
						//开发确认
						param.put("content", newDemandInfo.get("content") + "<br/>修改类型：" + newDemandInfo.get("ALTER_TYPE") + "<br/>系统等级：" + newDemandInfo.get("SYS_LEVEL") + "<br/>上线时间：" + newDemandInfo.get("RELEASE_TIME"));
						opResultMessage = "提交成功！";
						break;
					case 13:
						//转移
						context.contextMap.put("U_ID", Integer.parseInt(param.get("CURRENT_OPERATOR_ID").toString()));
						Map nUser = (Map) DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
						param.put("content", "处理人更改为：" + nUser.get("NAME"));
						if(newStatus >= 450 && newStatus <= 500){
							param.put("content", "处理人及开发人员更改为：" + nUser.get("NAME"));
						}
						opResultMessage = "转移成功！";
						break;
					case 14:
						//修改开发预估
						param.put("content", "预计工时：" + newDemandInfo.get("HOURS") + " 小时<br/>预计完成日期：" + newDemandInfo.get("PREDICT_DATE")
									+ "<br/>修改原因：" + context.contextMap.get("devAlterReason").toString());
						opResultMessage = "修改成功！";
						break;
					default:
						opResultMessage = "操作成功！";
						break;
				}
				param.put("OPERATE_STATE", opType);
				sqlMapper.insert("demand.insertDemandLog", param);
		    	sqlMapper.commitTransaction();

		    	newDemandInfo.put("CODE_NAME", stateMap.get(String.valueOf(newStatus)));
		    	//发送邮件
				MailSettingTo mailSettingTo =new MailSettingTo();
				//下一个处理人
		    	if(newDemandInfo.get("newUserId") != null && !newDemandInfo.get("newUserId").toString().equals("0")){
			    	context.contextMap.put("U_ID", newDemandInfo.get("newUserId"));
					Map opUser = (Map) DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
					//发送处理人
					mailSettingTo.setEmailTo(opUser.get("EMAIL").toString());
		    	} else {
					//已没处理人，发给资讯主管或开发人员
		    		String resMail = "";
		    		if(newDemandInfo.get("UP_RESPONSIBLE_USER_EMAIL") != null){
		    			resMail = newDemandInfo.get("UP_RESPONSIBLE_USER_EMAIL").toString() + ";" + newDemandInfo.get("RESPONSIBLE_USER_EMAIL").toString();
		    		} else {
		    			context.contextMap.put("U_ID", this.modifyOrderService.getDemandStateByDemandCode(309, states));
						Map opUser = (Map) DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
						resMail = opUser.get("EMAIL").toString();
		    		}
		    		if("-2".equals(newDemandInfo.get("newStatus")==null?"":newDemandInfo.get("newStatus").toString())){
						mailSettingTo.setEmailTo(newDemandInfo.get("APPLY_USER_EMAIL").toString());
						mailSettingTo.setEmailCc(resMail);
		    		} else {
						mailSettingTo.setEmailTo(resMail);
		    		}
		    	}
				//邮件主题
				String mailSub = "";
				switch (opType) {
				case 2:
					mailSub = "[资讯通知]：有一张资讯需求单已修改，等待您处理";
					break;
				case -3:
				case -6:
				case -10:
				case -15:
					mailSub = "[资讯通知]：有一张资讯需求单已驳回，等待您处理";
					opResultMessage = "驳回成功！";
					break;
				case -9:
					mailSub = "[资讯通知]：有一张资讯需求单已反馈，等待您处理";
					opResultMessage = "反馈成功！";
					break;
				case 11:
					mailSub = "[资讯通知]：有一张资讯需求单已撤案";
					opResultMessage = "撤案成功！";
					break;
				case 12:
					mailSub = "[资讯通知]：有一张资讯需求单已提交，等待您处理";
					opResultMessage = "提交成功！";
					break;
				case 13:
					mailSub = "[资讯通知]：有一张资讯需求单已转移给您，等待您处理";
					opResultMessage = "转移成功！";
					break;
				case -18:
//					不同意
					mailSub = "[资讯通知]：有一张资讯需求单已被总经理驳回，请查看";
					break;
				default:
					mailSub = "[资讯通知]：有一张资讯需求单状态已变更，等待您处理";
					break;
				}
				//验收
				if(newStatus >=  900 && newStatus < 950){
					mailSub = "[资讯通知]：有一张资讯需求单状态已变更，等待您验收";
				} else if (newStatus == 990){
					//验收通过
					mailSub = "[资讯通知]：有一张资讯需求单已验收通过，请查看";
				}
				mailSettingTo.setEmailSubject(mailSub);
				mailSettingTo.setEmailContent(this.modifyOrderService.getDemandMailContent(newDemandInfo));
				mailUtilService.sendMail(mailSettingTo);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				opResultMessage = "操作出错，请联系资讯部！";
			} finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} 
			}
			context.contextMap.put("opResultMessage", opResultMessage);
			this.getDemandById(context);
		}
		
		/**
		 * 获取申请人验收中的资讯需求单数
		 * @param context
		 * @return
		 */
		public void getCheckDemandCount(Context context){
			int count = this.modifyOrderService.getDemandCountByStateAndUserId(900, Integer.parseInt(context.contextMap.get("s_employeeId").toString()));
			Map<String,Integer> outputMap = new HashMap<String,Integer>();
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}
		
		/**
		 * 业务支撑查询资讯需求单
		 * @param context
		 */
		public void getDemandOrderAdmin(Context context){
			Map outputMap = new HashMap();
			PagingInfo<Object> dw = null;
			//所有IT开发人员
			List<Map> its = null;
			//资讯需求单状态字典
			List<Map> states = new ArrayList<Map>();
			Map<Integer, String> stateMap = new HashMap<Integer, String>();
			Map currentUser = null;
			List<Map<String,String>> users = new ArrayList<Map<String,String>>();
			try {
				context.contextMap.put("U_ID", context.contextMap.get("s_employeeId"));
				currentUser = (Map)DataAccessor.query("demand.queryUserByUid",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("CURR_NODE", currentUser.get("NODE"));
				if(context.contextMap.get("demandType") == null){
					context.contextMap.put("demandType",-1);
				}
				
				its = (List<Map>) DataAccessor.query("demand.getITList",context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				dw = baseService.queryForListWithPaging("demand.queryDemandOrders", context.contextMap, "createTime", ORDER_TYPE.DESC);
				
				states = this.modifyOrderService.getDemandStatesList(context);
				
				outputMap.put("demandType", context.contextMap.get("demandType"));
				
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
				outputMap.put("users", JsonUtils.list2json(users));
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			for(Map s : states){
				stateMap.put(Integer.parseInt(s.get("CODE").toString()), s.get("SHORTNAME").toString());
			}

			outputMap.put("current_state", context.contextMap.get("current_state"));
			outputMap.put("current_it", context.contextMap.get("current_it"));
			outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
			outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
			outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
			outputMap.put("states", states);
			outputMap.put("stateMap", stateMap);
			outputMap.put("dw", dw);
			outputMap.put("its", its);
			outputMap.put("page", dw.getPageNo());
			outputMap.put("pageSize", dw.getPageSize());
			Output.jspOutput(outputMap, context, "/modifyOrder/queryDemandOrderAdmin.jsp");
		}
		
		/**
		 * 获取处理人id
		 * @param context
		 */
		public void getOpUserId(Context context){
			Map outputMap = new HashMap();
			int stateCode = Integer.parseInt(context.contextMap.get("stateCode").toString());
			int newUserId = 0;
			try {
				Map demand = (Map)DataAccessor.query("demand.getDemandById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//资讯单状态列表
				List<Map> stateMap = this.modifyOrderService.getDemandStatesList(context);
				if(stateCode >= 301 && stateCode < 350){
					//待会签，从数据字典中状态里取id
					newUserId = this.modifyOrderService.getDemandStateByDemandCode(stateCode, stateMap);
				} else	if(stateCode >= 350 && stateCode < 950 || stateCode <= 300){
					//非验收通过,非会签，继续走流程，查询查理人ID
					switch (stateCode) {
					case 0:
						newUserId = Integer.parseInt(demand.get("APPLY_USER_ID").toString());
						break;
					case 100:
					case 910:
						//单位主管
						newUserId = Integer.parseInt(demand.get("UP_USER_ID").toString());
						break;
					case 200:
						//处级主管
						newUserId = Integer.parseInt(demand.get("UPP_USER_ID").toString());
						break;
					case 300:
					case 400:
					case 600:
						//资讯主管
						newUserId = this.modifyOrderService.getDemandStateByDemandCode(309, stateMap);
						break;
					case 900:
						newUserId = Integer.parseInt(demand.get("APPLY_USER_ID").toString());
						break;
					case 360:
						//高阶会签,从数据字典中状态里取id
						newUserId = this.modifyOrderService.getDemandStateByDemandCode(stateCode, stateMap);
					default:
						break;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputMap.put("userId", newUserId);
			Output.jsonOutput(outputMap, context);
		}
		
		/**
		 * 获取资讯需求单流程
		 * @param context
		 */
		public void getProcessByDemandId(Context context){
			Map outputMap = new HashMap();
			Map demand = null;
			Map<Integer, String> stateMap = new HashMap<Integer, String>();
			//总流程
			List<Map<String, String>> process = new ArrayList<Map<String, String>>();
			try {
				demand = (Map)DataAccessor.query("demand.getDemandById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//资讯单状态列表
				List<Map> stateList = this.modifyOrderService.getDemandStatesList(context);
				for(Map s : stateList){
					int ss = Integer.parseInt(s.get("CODE").toString());
					//总流程顺序添加会签前流程
					if(demand.get("DEMAND_TYPE").toString().equals("1") && ss > 0 && ss <= 300){
						Map<String, String> map = new HashMap<String, String>();
						map.put("code", String.valueOf(ss));
						map.put("name", s.get("SHORTNAME").toString());
						process.add(map);
					}
					stateMap.put(ss, s.get("SHORTNAME").toString());
				}
				//完成会签的字符串数据
				String countersigns = demand.get("COUNTERSIGN_CODE_ORDER")==null?"":demand.get("COUNTERSIGN_CODE_ORDER").toString();
				//完成会签
				String[] tempCountersign = countersigns.split(",");
				for(String tem : tempCountersign){
					//总流程顺序添加会签中流程
					if(!StringUtils.isEmpty(tem) && demand.get("DEMAND_TYPE").toString().equals("1")){
						Map<String, String> map = new HashMap<String, String>();
						map.put("code", tem);
						map.put("name", stateMap.get(Integer.parseInt(tem)));
						process.add(map);
					}
				}
				//总流程顺序添加会签后流程
				for(Map s : stateList){
					int ss = Integer.parseInt(s.get("CODE").toString());
					//总流程顺序添加会签后流程
					if((ss >= 350 && demand.get("DEMAND_TYPE").toString().equals("1")) || (ss >= 400 && demand.get("DEMAND_TYPE").toString().equals("0"))){
						String seniorSign = demand.get("SENIOR_SIGN")==null?"":demand.get("SENIOR_SIGN").toString();
						if(ss != 360 || (ss == 360 && (seniorSign.equals("1") || seniorSign.equals("2")))){
							Map<String, String> map = new HashMap<String, String>();
							map.put("code", String.valueOf(ss));
							map.put("name", stateMap.get(ss));
							process.add(map);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputMap.put("process", process);
			Output.jsonOutput(outputMap, context);
		}
		
		/**
		 * 更新资讯需求单状态
		 * @param context
		 */
		public void updateDemandState(Context context){
			Map outputMap = new HashMap();
			int count = 0;
			SqlMapClient sqlMapper = null;
			Map demand = null;
			//所有会签信息
			List<Map> countersignLogAll = null;
			List<Map> countersignLog = new ArrayList<Map>();
			//资讯单状态列表
			List<Map> stateMap = null;
			try {
				stateMap = this.modifyOrderService.getDemandStatesList(context);
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				demand = (Map)sqlMapper.queryForObject("demand.getDemandById", context.contextMap);
				Map map = new HashMap();
				map.put("demandId", context.contextMap.get("demandId"));
				int newState = Integer.parseInt(context.contextMap.get("newState").toString());
				int oldState = Integer.parseInt(context.contextMap.get("oldState").toString());
				map.put("newState", newState);
				map.put("newOpUser", context.contextMap.get("newOpUser"));
				int newSenior = Integer.parseInt(demand.get("SENIOR_SIGN")==null?"0":demand.get("SENIOR_SIGN").toString());
				//完成会签的名单
				String com = "";
				//会签名单
				String counter = demand.get("COUNTERSIGN_CODE_ORDER")==null?"":demand.get("COUNTERSIGN_CODE_ORDER").toString();
				String[] counters = counter.split(",");
				if(oldState > 300){
					if(newState <= 300){
						//清空已会签名单
						map.put("COMPLETE_CODE_ORDER", "null");
					} else if(newState < 350){
						//清除newState后的已会签名单
						com = demand.get("COMPLETE_CODE_ORDER")==null?"":demand.get("COMPLETE_CODE_ORDER").toString();
						int index = com.indexOf(String.valueOf(newState));
						if(index > 1){
							com = com.substring(0, index - 1);
						}
						map.put("COMPLETE_CODE_ORDER", com);
					}
					//高阶状态为1（手动或多处级签核）则保留该状态
					if(newSenior != 1){
						String[] cCodes = com.split(",");
						countersignLogAll = (List<Map>)DataAccessor.query("demand.getCountersignLogByDemandOrderId", context.contextMap, DataAccessor.RS_TYPE.LIST);
						//获取有效的会签信息
						for(int i = 0; i < cCodes.length; i++){
							for(int j = 0; j < countersignLogAll.size(); j++){
								if(cCodes[i].equals(countersignLogAll.get(j).get("ORDER_STATUS").toString())){
									countersignLog.add(countersignLogAll.get(j));
									break;
								}
							}
						}
						int cCount = 0;
						//先取消强制签核，再判断签核情形
						if(newSenior == 2){
							newSenior = 0;
						}
						//从会签名单中抓取处级部门数量
						for(int i = 0; i < counters.length; i++){
							for(int j = 0; j < stateMap.size(); j++){
								if(counters[i].equals(stateMap.get(j).get("CODE").toString()) && stateMap.get(j).get("FLAG").toString().indexOf("处") >= 0){
									cCount++;
									break;
								}
							}
						}
						//从完成会签的名单中判断是否有“其他意见”或“保留意见”
						for(Map log : countersignLog){
							int opState = Integer.parseInt(log.get("OPERATE_STATE")==null?"":log.get("OPERATE_STATE").toString());
							if(opState == 16 && opState == 17){
								newSenior = 2;
								break;
							}
						}
						if(newSenior == 0 && cCount >= 2){
							newSenior = 1;
						}
					}
				}
				map.put("newSenior", newSenior);
				count = sqlMapper.update("demand.updateDemandStateById", map);
				Map param = new HashMap();
				param.put("demandId", context.contextMap.get("demandId"));
				param.put("ORDER_STATUS", oldState);
				param.put("OPERATE_TIME_BEGIN", demand.get("LAST_OP_TIME"));
				param.put("OPERATE_TIME_END", new Date());
				param.put("s_employeeId", context.contextMap.get("s_employeeId"));
				param.put("OPERATE_STATE", 19);
				param.put("content", "状态变更为：" + context.contextMap.get("newStateName") 
						+ "<br/>处理人变更为：" + context.contextMap.get("newOpUserName")
						+ "<br/>变更原因：" + context.contextMap.get("devAlterReason"));
				sqlMapper.insert("demand.insertDemandLog", param);
		    	sqlMapper.commitTransaction();
			} catch (Exception e) {
				count = 0;
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			} finally { 
				outputMap.put("count", count);
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} 
			}
			Output.txtOutput(String.valueOf(count), context);
		}
}
