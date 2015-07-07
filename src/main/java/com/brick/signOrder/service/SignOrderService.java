package com.brick.signOrder.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.signOrder.command.SignOrderCommand;
import com.brick.signOrder.dao.SignOrderDao;
import com.brick.signOrder.to.SignOrderLogTo;
import com.brick.signOrder.to.SignOrderTo;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.ibatis.common.resources.Resources;
import com.tac.agent.service.AgentService;
import com.tac.agent.to.Agent;
import com.tac.dept.service.DeptService;
import com.tac.dept.to.DeptTo;
import com.tac.user.dao.UserDAO;
import com.tac.user.to.UserTo;

public class SignOrderService extends BaseService {
	
	Log logger = LogFactory.getLog(SignOrderService.class);

	private SignOrderDao signOrderDao;
	private MailUtilService mailUtilService;
	private DeptService deptService;
	private UserDAO userDAO;
	private AgentService agentService;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void setSignOrderDao(SignOrderDao signOrderDao) {
		this.signOrderDao = signOrderDao;
	}
	
	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	/**
	 * 上传附件
	 * @param context
	 */
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
					String title = "签办单附件";
					String filePath = fileItem.getName();
					String type = filePath.substring(filePath.lastIndexOf(".") + 1);
					List errList = context.errList;
					Map contextMap = context.contextMap;
					String xmlPath = "signOrderImage";
					String bootPath = this.getDemandPath(xmlPath);
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
								contextMap.put("fileType", "signOrder");
								contextMap.put("userId", context.contextMap.get("s_employeeId"));
								contextMap.put("date", new Date());
								int fId = (Integer)DataAccessor.execute("signOrder.insertSignOrderFile", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
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
	 * 获取签办单上传路径
	 * @param context
	 * @return
	 */
	public String getDemandPath(String xmlPath){
		String path = null;
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
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
	 * 获取公司部门最大编号
	 * @param companyName		公司名
	 * @param departmentCode	部门简称
	 * @return
	 */
	public String getMaxSignOrderCodeByCompanyNameAndDepartmentCode(int companyCode, String departmentCode) throws Exception {
		return this.signOrderDao.getMaxSignOrderCodeByCompanyNameAndDepartmentCode(companyCode, departmentCode);
	}

	/**
	 * 获取新的签办单编号
	 * @param maxSignOrderCode
	 * @param companyName
	 * @param departmentCode
	 * @return
	 */
	public String getNewSignOrderCode(String maxSignOrderCode, int companyCode, String departmentCode) {
		//签办单编号格式为： 裕融(2014)业字第(05)008号
		String signOrderCode = "";
		if(StringUtils.isEmpty(maxSignOrderCode)){
			signOrderCode = SignOrderCommand.COMPANY_CODE[companyCode-1] + "(" + DateUtil.getCurrentYear() + ")" + departmentCode + "字第(" + DateUtil.getCurrentMonth() + ")001号";
		} else {
			if(Integer.parseInt(maxSignOrderCode.substring(3, 7)) != Integer.parseInt(DateUtil.getCurrentYear())){
				//年
				signOrderCode = SignOrderCommand.COMPANY_CODE[companyCode-1] + "(" + DateUtil.getCurrentYear() + ")" + departmentCode + "字第(" + DateUtil.getCurrentMonth() + ")001号";
			} else if (Integer.parseInt(maxSignOrderCode.substring(12, 14)) != Integer.parseInt(DateUtil.getCurrentMonth())){
				//月份
				signOrderCode = maxSignOrderCode.substring(0, 12) + DateUtil.getCurrentMonth() + ")001号";
			} else {
				int num = Integer.parseInt(maxSignOrderCode.substring(15, 18));
				num++;
				String sNum = Integer.toString(num);
				if(sNum.length() == 1){
					sNum = "00" + sNum;
				} else if(sNum.length() == 2){
					sNum = "0" + sNum;
				}
				signOrderCode = maxSignOrderCode.substring(0, 15) + sNum + "号";
			}
		}
		return signOrderCode;
	}

	/**
	 * 上传文件与签办单同步更新
	 * @param signOrderId 	签办单id
	 * @param fileIds		文件ids
	 * @param fileType		文件类型
	 * @throws Exception	
	 */
	public void syncSignOrderFiles(int signOrderId, String fileIds, String fileType) throws Exception {
		if(!fileIds.equals("")){
			this.signOrderDao.syncSignOrderFiles(signOrderId, fileIds, fileType);
		}
	}


	/**
	 * 添加签办单
	 * @param context
	 */
	@Transactional(rollbackFor = Exception.class)  
	public void insertSignOrder(Context context) throws Exception {
		SignOrderTo signOrderTo = new SignOrderTo();
		//附件上传
		this.uploadFiles(context);
		List errList = context.errList;
		if (errList.isEmpty()) {
			//签办单编号格式为： 裕融(2014)业字第(05)008号
			String signOrderCode = "";
			int companyCode = StringUtils.ob2int(context.contextMap.get("companyCode"));
			String departmentCode = String.valueOf(context.contextMap.get("departmentCode"));
			signOrderTo.setCode(departmentCode);
			String maxSignOrderCode = this.getMaxSignOrderCodeByCompanyNameAndDepartmentCode(companyCode, departmentCode);
			signOrderCode = this.getNewSignOrderCode(maxSignOrderCode, companyCode, departmentCode);
			signOrderTo.setSignCode(signOrderCode);
			signOrderTo.setSummary(String.valueOf(context.contextMap.get("SUMMARY")));
			signOrderTo.setContent(String.valueOf(context.contextMap.get("content")));
			signOrderTo.setCountersignCodeOrder(String.valueOf(context.contextMap.get("chooseCodes")));
			signOrderTo.setApplyUserId(StringUtils.ob2int(context.contextMap.get("s_employeeId")));
			//modify by ZhangYizhou by 2014-07-11 新建签办单时创建人不默认后会
			//signOrderTo.setLastCountersignCodeOrder(context.contextMap.get("s_employeeId").toString());
			signOrderTo.setUpdateTime(new Timestamp(new Date().getTime()));
			signOrderTo.setCompanyCode(companyCode);
			//插入签办单记录
			int opState = 1;
			signOrderTo = this.doWorkFlow(signOrderTo, opState);
			int signOrderId = this.signOrderDao.insertSignOrder(signOrderTo);
			context.contextMap.put("signOrderId", signOrderId);
			//附件关联
			this.syncSignOrderFiles(signOrderId, context.contextMap.get("ids")==null?"":context.contextMap.get("ids").toString(), "signOrder");
			//插入日志
			SignOrderLogTo signOrderLogTo = new SignOrderLogTo();
			signOrderTo = this.getSignOrderById(signOrderId);
			signOrderLogTo.setOperateTimeBegin(signOrderTo.getUpdateTime());
			signOrderLogTo.setOperateTimeEnd(signOrderTo.getUpdateTime());
			signOrderLogTo.setOperateState(1);
			signOrderLogTo.setSignOrderId(signOrderId);
			signOrderLogTo.setOperatorId(signOrderTo.getApplyUserId());
			signOrderLogTo.setOrgOperatorId(0);
			StringBuilder suggest = new StringBuilder("");
			suggest.append("签办单编号：" + signOrderTo.getSignCode());
			suggest.append("<br/>申请人：" + signOrderTo.getApplyUserName());
			suggest.append("<br/>字：" + signOrderTo.getCode());
			suggest.append("<br/>会签名单：" + this.getCountersignChinese(signOrderTo.getCountersignCodeOrder()));
			suggest.append("<br/>事由：" + signOrderTo.getSummary());
			suggest.append("<br/>签办单内容：" + signOrderTo.getContent());
			//获取上传文件
			List<Map<String, Object>> files = this.getUploadFilesBySignOrderId(SignOrderCommand.FILE_TYPE, signOrderId);
			StringBuilder fileNames = new StringBuilder("<br/>文件列表：<br/>");
			for(Map<String, Object> f : files){
				fileNames.append(f.get("ORG_FILE_NAME").toString() + "<br/>");
			}
			suggest.append(fileNames);
			signOrderLogTo.setOperateSuggest(suggest.toString());
			this.addSignOrderLog(signOrderLogTo);
			//发送邮件
			try {
				this.sendSignOrderEmail(signOrderTo, opState);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
	}

	/**
	 * 处理流程
	 * @param signOrderTo
	 * @param opState 		操作状态
	 * @return
	 */
	public SignOrderTo doWorkFlow(SignOrderTo signOrderTo, int opState) throws Exception {
		List<Map<String, Object>> flows = this.getAllFlow();
		Map<Integer, Map<String, Object>> flowsMap = this.getFlowMapFromList(flows);
		int newState = 0;
		int newUserId = 0;
		if(opState == 1){
			//新增
			newUserId = userDAO.getUserById(signOrderTo.getApplyUserId()).getUpperUser();
			newState = StringUtils.ob2int(flows.get(0).get("CODE"));
			signOrderTo.setOrderStatus(newState);
			signOrderTo.setCurrentOperatorId(newUserId);
		} else {
			signOrderTo = this.getSignOrderById(signOrderTo.getId());
			//当前流程状态
			Map<String, Object> currentFlow = flowsMap.get(signOrderTo.getOrderStatus());
			String stateType = currentFlow.get("SHORTNAME")==null?"":currentFlow.get("SHORTNAME").toString();
			if(stateType.equals("over")){
				throw new Exception("当前签办单流程状态已经结束，无法再继续");
			}
			//根据操作状态来判断处理，更新状态，处理人
			switch(opState){
				case 3:
					//通过
					if(stateType.equals("upUser")){
						newState = 2;
						int deptId = userDAO.getUserById(signOrderTo.getApplyUserId()).getDepartment();
						newUserId = this.deptService.getDeptLeaderByDeptId(deptId, 5).getDeptLeader();
					} else if(stateType.equals("upUpUser")){
						//会签
						signOrderTo = this.doCountersign(signOrderTo, flowsMap);
						newState = signOrderTo.getOrderStatus();
						newUserId = signOrderTo.getCurrentOperatorId();
					}
//					this.sendSignOrderEmail(signOrderTo, "approve");
					break;
				case -3:
					//驳回
					newState = -1;
					newUserId = signOrderTo.getApplyUserId();
//					this.sendSignOrderEmail(signOrderTo, "reject");
					break;
				case 6:
				case 16:
				case 17:
					if(stateType.equals("manager")){
						//高阶
						signOrderTo = this.doLastFlows(signOrderTo, true);
//						newState = -200;
//						newUserId = signOrderTo.getApplyUserId();
//						signOrderTo.setCurrentCountersignCodeOrder(signOrderTo.getApplyUserId());
					} else {
						//会签
						signOrderTo = this.doCountersign(signOrderTo, flowsMap);
//						newState = signOrderTo.getOrderStatus();
//						newUserId = signOrderTo.getCurrentOperatorId();
					}
					newState = signOrderTo.getOrderStatus();
					newUserId = signOrderTo.getCurrentOperatorId();
//					this.sendSignOrderEmail(signOrderTo, "approve");
					break;
				case -11:
					//撤案
					newState = -3;
					signOrderTo.setCompleteTime(new Timestamp(new Date().getTime()));
//					this.sendSignOrderEmail(signOrderTo, "delete");
					break;
				case 12:
					//提交
					newState = 1;
					newUserId = userDAO.getUserById(signOrderTo.getApplyUserId()).getUpperUser();
//					this.sendSignOrderEmail(signOrderTo, "commit");
				case 13:
					//转移
					//newState = signOrderTo.getOrderStatus();
					break;
				case -18:
					//不同意
					newState = -2;
					signOrderTo.setCompleteTime(new Timestamp(new Date().getTime()));
//					this.sendSignOrderEmail(signOrderTo, "disapprove");
					break;
				case 20:
					//后会知悉
					signOrderTo = this.doLastFlows(signOrderTo, true);
					newState = signOrderTo.getOrderStatus();
					newUserId = signOrderTo.getCurrentOperatorId();
//					this.sendSignOrderEmail(signOrderTo, "know");
					break;
				case 22:
					//结案
					newState = 5;
					signOrderTo.setCompleteTime(new Timestamp(new Date().getTime()));
					break;
			}
		}
		signOrderTo.setOrderStatus(newState);
		signOrderTo.setCurrentOperatorId(newUserId);
		return signOrderTo;
	}
	
	/**
	 * 获取所有流程
	 * @return
	 */
	public List<Map<String, Object>> getAllFlow() throws Exception{
		return DictionaryUtil.getDictionaryForAll("签办单流程状态");
	}
	
	/**
	 * 将流程状态变为map形式
	 * @param flows	流程状态
	 * @return
	 */
	public Map<Integer, Map<String, Object>> getFlowMapFromList(List<Map<String, Object>> flows){
		Map<Integer, Map<String, Object>> flowsMap = new HashMap<Integer, Map<String,Object>>();
		for(Map<String, Object> f : flows){
			flowsMap.put(StringUtils.ob2int(f.get("CODE")), f);
		}
		return flowsMap;
	}

	/**
	 * 根据部门id获取“字”号
	 * @param deptId
	 * @return
	 */
	public String getDepartmentCodeByUserId(int deptId) {
		String z = "";
		if(deptId == 34){
			z = "稽";
		} else {
			switch (this.deptService.getDeptLeaderByDeptId(deptId, 5).getId()) {
				case 2:
					z = "经";
					break;
				case 10:
					z = "业";
					break;
				case 14:
					z = "审";
					break;
				case 15:
					z = "机";
					break;
				case 26:
					z = "乘";
					break;
			}
		}
		return z;
	}
	
	/**
	 * 根据id查询签办单
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SignOrderTo getSignOrderById(int id) throws Exception {
		return this.signOrderDao.getSignOrderById(id);
	}
	
	/**
	 * 正常完成主流程
	 * @param signOrderTo
	 * @param flows
	 * @return
	 * @throws Exception
	 */
//	private SignOrderTo updateStateAndOpUserIdByMainFlow(SignOrderTo signOrderTo, List<Map<String, Object>> flows) throws Exception{
//		//获取下一流程，状态>0
//		int index = 0;
//		Map<String, Object> nextFlow = null;
//		int newState = 0;
//		int newUserId = 0;
//		boolean isFind = false;
//		for(Map<String, Object> f : flows){
//			int s = StringUtils.ob2int(f.get("CODE").toString());
//			if(s > 0 && s == signOrderTo.getOrderStatus()){
//				isFind = true;
//				break;
//			}
//			index++;
//		}
//		if(isFind){
//			boolean isGetNextFlow = false;
//			while(!isGetNextFlow){
//				if(StringUtils.ob2int(flows.get(index).get("CODE")) > 0){
//					//CODE大于0为主流程
//					nextFlow = flows.get(index + 1);
//					isGetNextFlow = true;
//				} else {
//					//找下一个
//					index++;
//				}
//			}
//		} else {
//			throw new Exception("当前签办单流程状态无效或流程已结束，检查签办单流程字典中有无该状态或流程是否状态异常！");
//		}
//		//更新后状态
//		newState = StringUtils.ob2int(nextFlow.get("CODE"));
//		String nextStateType = nextFlow.get("SHORTNAME").toString();
//		if(nextStateType.equals("upUser")){
//			//处理人为单位主管
//			int deptId = userDAO.getUserById(signOrderTo.getApplyUserId()).getDepartment();
//			newUserId = this.deptService.getDeptLeaderByDeptId(deptId, 10).getDeptLeader();
//		} else if(nextStateType.equals("upUpUser")){
//			//处理人为处级主管
//			int deptId = userDAO.getUserById(signOrderTo.getApplyUserId()).getDepartment();
//			newUserId = this.deptService.getDeptLeaderByDeptId(deptId, 5).getDeptLeader();
//		} else if(nextStateType.equals("countersign") || nextStateType.equals("manager") ){
//			//会签状态或总经理
//			newUserId = StringUtils.ob2int(nextFlow.get("REMARK"));
//		} else if(nextStateType.equals("lastCountersign")){
//			//后会
//			
//		} else if(nextStateType.equals("reject")){
//			//驳回
//			newState = 1;
//			newUserId = 0;
//		} else if(nextStateType.equals("over")){
//			//流程结束
//			newUserId = 0;
//		}
//		signOrderTo.setOrderStatus(newState);
//		signOrderTo.setCurrentOperatorId(newUserId);
//		return signOrderTo;
//	}

	/**
	 * 更新签办单
	 * @param signOrderTo
	 * @param opState		操作类型
	 * @param opUserId		操作人id
	 * @param suggest		操作日志
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)  
	public SignOrderTo updateSignOrder(Context context, SignOrderTo signOrderTo, int opState, int opUserId, String suggest) throws Exception {
		//上传附件
		this.uploadFiles(context);
		//删除附件
		String delFiles = context.contextMap.get("fileDelIds")==null?"":context.contextMap.get("fileDelIds").toString();
		if(!StringUtils.isEmpty(delFiles)){
			context.contextMap.put("delFiles", delFiles);
			this.signOrderDao.deleteFiles(delFiles);
		}
		//附件关联
		this.syncSignOrderFiles(signOrderTo.getId(), context.contextMap.get("ids")==null?"":context.contextMap.get("ids").toString(), "signOrder");
		Timestamp time = new Timestamp(new Date().getTime());
		//日志
		SignOrderLogTo signOrderLogTo = new SignOrderLogTo();
		signOrderLogTo.setOperateTimeBegin(signOrderTo.getUpdateTime());
		signOrderLogTo.setOperateTimeEnd(time);
		signOrderLogTo.setSignStatus(signOrderTo.getOrderStatus());
		signOrderLogTo.setOperateState(opState);
		signOrderLogTo.setSignOrderId(signOrderTo.getId());
		signOrderLogTo.setOperatorId(opUserId);
		signOrderLogTo.setOrgOperatorId(signOrderTo.getCurrentOperatorId());
		StringBuilder tempSg = new StringBuilder(suggest);
		if(opState == 1 || opState == 2){
			//附件列表
			List<Map<String, Object>> fileList = this.getUploadFilesBySignOrderId(SignOrderCommand.FILE_TYPE, signOrderTo.getId());
			if(fileList != null && fileList.size() > 0){
				tempSg.append("<br/>附件：");
				for(Map f : fileList){
					tempSg.append("<br/>" + f.get("ORG_FILE_NAME"));
				}
			}
		}
		signOrderLogTo.setOperateSuggest(tempSg.toString());
		//更新
		if(opState != 2){
			signOrderTo = this.doWorkFlow(signOrderTo, opState);
		}
		signOrderTo.setUpdateTime(time);
		this.signOrderDao.updateSignOrder(signOrderTo);
		//插入日志
		this.addSignOrderLog(signOrderLogTo);
		return signOrderTo;
	}
	
	/**
	 * 会签
	 * @param signOrderTo
	 * @return
	 */
	private SignOrderTo doCountersign(SignOrderTo signOrderTo, Map<Integer, Map<String, Object>> flowsMap){
		int newState = 0;
		int newUserId = 0;
		String countersign = StringUtils.toStringOrEmpty(signOrderTo.getCountersignCodeOrder());
		String completeCountersign = StringUtils.toStringOrEmpty(signOrderTo.getCompleteCodeOrder());
		//添加当前会签到已会签名单
		completeCountersign = completeCountersign == "" 
								? (signOrderTo.getCurrentCountersignCodeOrder()==null?"":signOrderTo.getCurrentCountersignCodeOrder().toString())
								: (completeCountersign + "," + signOrderTo.getCurrentCountersignCodeOrder().toString());
		signOrderTo.setCompleteCodeOrder(completeCountersign);
		String[] countersigns = countersign.split(",");
		String[] completeCountersigns = completeCountersign.split(",");
		if(countersign.equals("")){
			//无会签名单，进入下一主流程
			newState = 3;
			signOrderTo.setCurrentCountersignCodeOrder(0);
			String[] userList = flowsMap.get(newState).get("REMARK").toString().split(",");
			newUserId = StringUtils.ob2int(userList[signOrderTo.getCompanyCode()-1]);
		} else {
			boolean isFind = false;
			for(String s : countersigns){
				isFind = false;
				for(String ss : completeCountersigns){
					if(!ss.equals("") && ss.equals(s)) {
						isFind = true;
						break;
					}
				}
				if(!isFind){
					int tempState = StringUtils.str2int(s);
					if(tempState < 0){
						//会签
						newState = tempState;
						signOrderTo.setCurrentCountersignCodeOrder(newState);
						newUserId = StringUtils.ob2int(flowsMap.get(tempState).get("REMARK"));
					} else if(tempState > 0) {
						//加签
						newState = -200;
						signOrderTo.setCurrentCountersignCodeOrder(tempState);
						newUserId = tempState;
					}
					break;
				}
			}
			if(isFind){
				//会签结束，进入下一主流程
				newState = 3;
				signOrderTo.setCurrentCountersignCodeOrder(0);
				String[] userList = flowsMap.get(newState).get("REMARK").toString().split(",");
				newUserId = StringUtils.ob2int(userList[signOrderTo.getCompanyCode()-1]);
			}
		}
		signOrderTo.setCurrentOperatorId(newUserId);
		signOrderTo.setOrderStatus(newState);
		return signOrderTo;
	}
	
	/**
	 * 后会
	 * @param signOrderTo
	 * @param goNext 		是否需要获取下一流程
	 * @return
	 */
	private SignOrderTo doLastFlows(SignOrderTo signOrderTo, boolean goNext) throws Exception{
		//后会名单
		String lastCountersignCodeOrder = StringUtils.toStringOrEmpty(signOrderTo.getLastCountersignCodeOrder());
		if(StringUtils.isEmpty(lastCountersignCodeOrder)){
			signOrderTo.setCurrentOperatorId(signOrderTo.getApplyUserId());
			signOrderTo.setOrderStatus(4);
			return signOrderTo;
			//modify by ZhangYizhou 后会名单可以为空
			//throw new Exception("后会名单不能为空！");
		}
		signOrderTo.setOrderStatus(4);
		//无当前后会人员，则设置后会名单第一位为当前后会人员
		if(signOrderTo.getCurrentCountersignCodeOrder() == null){
			String[] lastCountersigns = lastCountersignCodeOrder.split(",");
			int firstLastUserId = Integer.parseInt(lastCountersigns[0]);
			signOrderTo.setCurrentCountersignCodeOrder(firstLastUserId);
			if(lastCountersigns.length == 1) {
				signOrderTo.setCurrentOperatorId(signOrderTo.getApplyUserId());
			} else {
				signOrderTo.setCurrentOperatorId(firstLastUserId);
			}
			return signOrderTo;
		}
		//后会完成名单
		String lastCompleteCodeOrder = StringUtils.toStringOrEmpty(signOrderTo.getLastCompleteCodeOrder());
		if(goNext && signOrderTo.getCurrentCountersignCodeOrder() != 0){
			lastCompleteCodeOrder = lastCompleteCodeOrder == "" 
									? signOrderTo.getCurrentCountersignCodeOrder().toString()
									: lastCompleteCodeOrder + "," + signOrderTo.getCurrentCountersignCodeOrder().toString();
			signOrderTo.setLastCompleteCodeOrder(lastCompleteCodeOrder);
		}
		String[] lastCountersignCodeOrders = lastCountersignCodeOrder.split(",");
		String[] lastCompleteCodeOrders = lastCompleteCodeOrder.split(",");
		boolean isFind = false;
		for(String s : lastCountersignCodeOrders){
			isFind = false;
			for(String ss : lastCompleteCodeOrders){
				if(!StringUtils.isEmpty(s) && s.equals(ss)){
					isFind = true;
					break;
				}
			}
			if(!isFind){
				//找到未后会的
				signOrderTo.setCurrentCountersignCodeOrder(Integer.parseInt(s));
				signOrderTo.setCurrentOperatorId(Integer.parseInt(s));
				break;
			}
		}
		if(isFind || lastCountersignCodeOrders.length==lastCompleteCodeOrders.length){
			//全部后会完	
			//modify by Zhangyizhou 后会结束不自动结案
			
			signOrderTo.setCurrentOperatorId(signOrderTo.getApplyUserId());
			
			/*signOrderTo.setOrderStatus(5);
			signOrderTo.setCompleteTime(new Timestamp(new Date().getTime()));
			*/
		}
		return signOrderTo;
	}

	/**
	 * 更新会签名单
	 * @param signOrderId
	 * @param countersignCodeOrder
	 * @param opType
	 */
	public void updateCountersign(int signOrderId, String countersignCodeOrder,
			int opType, int opUserId) throws Exception{
		//日志
		SignOrderTo signOrderTo = this.getSignOrderById(signOrderId);
		SignOrderLogTo signOrderLogTo = new SignOrderLogTo();
		signOrderLogTo.setOperateTimeBegin(signOrderTo.getUpdateTime());
		signOrderLogTo.setOperateTimeEnd(signOrderTo.getUpdateTime());
		signOrderLogTo.setOperateState(opType);
		signOrderLogTo.setSignStatus(signOrderTo.getOrderStatus());
		signOrderLogTo.setSignOrderId(signOrderTo.getId());
		signOrderLogTo.setOperatorId(opUserId);
		signOrderLogTo.setOrgOperatorId(signOrderTo.getCurrentOperatorId());
		StringBuilder suggest = new StringBuilder("");
		suggest.append("修改前会签名单：" + this.getCountersignChinese(signOrderTo.getCountersignCodeOrder()));
		//更新
		this.signOrderDao.updateCountersign(signOrderId, countersignCodeOrder);

		signOrderTo = this.getSignOrderById(signOrderId);
		suggest.append("<br/>修改后会签名单：" + this.getCountersignChinese(signOrderTo.getCountersignCodeOrder()));
		signOrderLogTo.setOperateSuggest(suggest.toString());
		//插入日志
		this.addSignOrderLog(signOrderLogTo);
	}
	
	/**
	 * 添加签办单log
	 * @param signOrderLogTo
	 */
	private void addSignOrderLog(SignOrderLogTo signOrderLogTo) throws Exception{
		this.signOrderDao.addSignOrderLog(signOrderLogTo);
	}

	/**
	 * 查询签办单log信息
	 * @param signOrderId
	 * @return
	 */
	public List<SignOrderLogTo> getSignOrderLogsById(int signOrderId) throws Exception {
		return this.signOrderDao.getSignOrderLogsById(signOrderId);
	}
	
	/**
	 * 获取会签名单流程
	 * @param countersignOrder	会签名单（，分割id）
	 * @return
	 */
	public String getCountersignChinese(String countersignOrder) throws Exception{
		if(StringUtils.isEmpty(countersignOrder)){
			return "";
		}
		List<Map<String, Object>> flows = this.getAllFlow();
		Map<Integer, Map<String, Object>> flowsMap = this.getFlowMapFromList(flows);
		StringBuilder countersignChinese = new StringBuilder("");
		String[] cs = countersignOrder.split(",");
		for(String c : cs){
			int id = Integer.parseInt(c);
			if(id < 0){
				//会签
				countersignChinese.append("→");
				countersignChinese.append(flowsMap.get(id).get("FLAG").toString());
			} else if(id > 0){
				//加签
				UserTo user = userDAO.getUserById(id);
				countersignChinese.append("→");
				countersignChinese.append(user.getName());
			}
		}
		return countersignChinese.toString().substring(1);
	}
	
	/**
	 * 获取签办单上传附件
	 * @param fileType
	 * @param signOrderId
	 * @return
	 */
	public List<Map<String, Object>> getUploadFilesBySignOrderId(String fileType, int signOrderId){
		return this.signOrderDao.getUploadFilesBySignOrderId(fileType, signOrderId);
	}

	/**
	 * 更新后会名单
	 * @param signOrderId				签办单id
	 * @param lastCountersignCodeOrder	新增的后会名单
	 * @param opType					操作类型
	 * @param opUserId					操作人ID
	 */
	@Transactional(rollbackFor = Exception.class)  
	public void updateLastCountersign(int signOrderId,
			String lastCountersignCodeOrder, int opType, int opUserId) throws Exception {
		//日志
		SignOrderTo signOrderTo = this.getSignOrderById(signOrderId);
		SignOrderLogTo signOrderLogTo = new SignOrderLogTo();
		signOrderLogTo.setOperateTimeBegin(signOrderTo.getUpdateTime());
		signOrderLogTo.setOperateTimeEnd(signOrderTo.getUpdateTime());
		signOrderLogTo.setOperateState(opType);
		signOrderLogTo.setSignStatus(signOrderTo.getOrderStatus());
		signOrderLogTo.setSignOrderId(signOrderTo.getId());
		signOrderLogTo.setOperatorId(opUserId);
		signOrderLogTo.setOrgOperatorId(signOrderTo.getCurrentOperatorId());
		//处理后会名单，将申请人挪至最后一个
		String oldC = signOrderTo.getLastCountersignCodeOrder();
		String newC = "";
		if(lastCountersignCodeOrder == null || "".equals(lastCountersignCodeOrder)) {
			return;
		}
		if (lastCountersignCodeOrder.indexOf(",") == 0) {
			throw new Exception("后会名单格式异常");
		}
		if(StringUtils.isEmpty(oldC)){
			newC = lastCountersignCodeOrder;
			if(signOrderTo.getOrderStatus()==4) {
				int operateorId = 0;
				if (newC.indexOf(",") == -1) {
					operateorId = Integer.valueOf(newC);
				} else {
					operateorId = Integer.valueOf(newC.substring(0,newC.indexOf(",")));
				}
				signOrderTo.setCurrentOperatorId(operateorId);
				signOrderTo.setCurrentCountersignCodeOrder(operateorId);
			}
			this.signOrderDao.updateSignOrder(signOrderTo);
			//modify by ZhangYizhou 初始创建人不列入后会行列
			//newC = lastCountersignCodeOrder + "," + signOrderTo.getApplyUserId().toString();
		} /*else if(oldC.lastIndexOf(",") < 0){
			//后会名单只有申请人一个
			newC = lastCountersignCodeOrder + "," + oldC;
			signOrderTo.setLastCountersignCodeOrder(newC);
			if(signOrderTo.getOrderStatus() == 4){
				signOrderTo = this.doLastFlows(signOrderTo, false);
			}
			this.signOrderDao.updateSignOrder(signOrderTo);
		} */else {
			newC = oldC + "," + lastCountersignCodeOrder;
			if(oldC.equals(signOrderTo.getLastCompleteCodeOrder()) && signOrderTo.getOrderStatus() == 4){
				int operateorId = 0;
				String unComplete = newC.substring(oldC.length()+1);
				if (unComplete.indexOf(",") == -1) {
					operateorId = Integer.valueOf(unComplete);
				} else {
					operateorId = Integer.valueOf(unComplete.substring(0,unComplete.indexOf(",")));
				}
				signOrderTo.setCurrentOperatorId(operateorId);
				signOrderTo.setCurrentCountersignCodeOrder(operateorId);
				//signOrderTo = this.doLastFlows(signOrderTo, false);
			}
			signOrderTo.setLastCountersignCodeOrder(newC);
			this.signOrderDao.updateSignOrder(signOrderTo);
		}
		StringBuilder suggest = new StringBuilder("");
		suggest.append("修改前后会名单：" + this.getCountersignChinese(oldC));
		//更新
		this.signOrderDao.updateLastCountersign(signOrderId, newC);
		//记录更新后信息
		//signOrderTo = this.getSignOrderById(signOrderId);
		suggest.append("<br/>修改后后会名单：" + this.getCountersignChinese(newC));
		signOrderLogTo.setOperateSuggest(suggest.toString());
		//插入日志
		this.addSignOrderLog(signOrderLogTo);
	}
	
	//后会时调整当前处理人
	public void updateLastCountersign(SignOrderTo signOrderTo,String lastCountersignCodeOrder) throws Exception {
		String[] lastCountersignCode = (signOrderTo.getLastCountersignCodeOrder()==null) ? new String[]{} : signOrderTo.getLastCountersignCodeOrder().split(",");
		String[] newLastCountersignCode = (lastCountersignCodeOrder==null) ? new String[]{} : lastCountersignCodeOrder.split(",");
		lastCountersignCode = (String[])ArrayUtils.addAll(lastCountersignCode, newLastCountersignCode);
		System.out.println(ArrayUtils.toString(lastCountersignCode));
	}
	
	//后会时调整当前处理人
		public void changeCurrentOperatorDuringLastCountersign(SignOrderTo signOrderTo) throws Exception {
			if (signOrderTo.getOrderStatus() != 4) {
				return;
			}
			String[] lastCountersignCode = signOrderTo.getLastCountersignCodeOrder().split(",");
			String[] completeCode = signOrderTo.getLastCompleteCodeOrder().split(",");
			if(lastCountersignCode.length > completeCode.length && completeCode.length > 0 ) {
				signOrderTo.setCurrentOperatorId(Integer.valueOf(lastCountersignCode[completeCode.length]));
			} else {
				signOrderTo.setCurrentOperatorId(signOrderTo.getApplyUserId());
			}
			this.signOrderDao.updateSignOrder(signOrderTo);
		}
	
	/**
	 * 发送签办单邮件
	 * @param signOrderTo
	 * @param type
	 */
	public void sendSignOrderEmail(SignOrderTo signOrderTo, int opType) throws Exception{
		signOrderTo = this.getSignOrderById(signOrderTo.getId());
		MailSettingTo mailSettingTo =new MailSettingTo();
		//查询代理信息，如果有代理，则to代理，cc原始处理人
		int orgUserId = signOrderTo.getCurrentOperatorId();
		Agent agent = this.agentService.getAgent(orgUserId, "签办单");
		if(agent == null){
			mailSettingTo.setEmailTo(signOrderTo.getCurrentOperatorEmail());
		} else {
			mailSettingTo.setEmailTo(agent.getAgentUserEmail());
			mailSettingTo.setEmailCc(signOrderTo.getCurrentOperatorEmail());
		}
		//subject
		String mailSub = "";
		int newState = signOrderTo.getOrderStatus();
		switch (opType) {
		case 1:
			//新增
			mailSub = "[签办单通知]：有一张签办单已提交，等待您审核";
			break;
		case 3:
			//通过
			if(newState == 2){
				mailSub = "[签办单通知]：有一张签办单已通过单位主管审核，等待您处理";
			} else if(newState == 4){
				mailSub = "[签办单通知]：有一张签办单审核通过高阶签核，等待您处理";
			} else {
				mailSub = "[签办单通知]：有一张签办单已通过处级主管审核，等待您处理";
			}
			break;
		case 5:
			//修改会签名单，不发邮件
			break;
		case -3:
			//驳回
			mailSub = "[签办单通知]：有一张签办单被驳回，等待您处理";
			break;
		case 12:
			//提交
			mailSub = "[签办单通知]：有一张签办单已重新提交，等待您审核";
			break;
		case -11:
			//撤案
			mailSub = "[签办单通知]：你有一张签办单已撤案，请查看";
			mailSettingTo.setEmailTo(signOrderTo.getApplyUserEmail());
			mailSettingTo.setEmailCc("");
			break;
		case -18:
			//不同意
			mailSub = "[签办单通知]：你有一张签办单被总经理驳回，请查看";
			mailSettingTo.setEmailTo(signOrderTo.getApplyUserEmail());
			mailSettingTo.setEmailCc("");
			break;
		case 20:
			if(newState == 5){
				mailSub = "[签办单通知]：签办单 " + signOrderTo.getSignCode() + " " + signOrderTo.getSummary() + " 已审核通过，请查看";
				//邮件发送给所有审核的人及其代理人
				List<SignOrderLogTo> logs = this.getSignOrderLogsById(signOrderTo.getId());
				Set<String> emails = new HashSet<String>();
				for(SignOrderLogTo o : logs){
					if(!StringUtils.isEmpty(o.getOperatorEmail())){
						emails.add(o.getOperatorEmail());
					}
					if(!StringUtils.isEmpty(o.getOrgOperatorEmail())){
						emails.add(o.getOrgOperatorEmail());
					}
				}
				StringBuilder es = new StringBuilder("");
				for(String e : emails){
					es.append(e + ";");
				}
				mailSettingTo.setEmailTo(es.toString());
			} else {
				//知悉
				mailSub = "[签办单通知]：有一张签办单已通过，等待您处理";
			}
			break;
		case 21:
			//添加后会名单
			mailSub = "[签办单通知]：有一张签办单已通过，等待您处理";
			break;
		case 22:
			//添加后会名单
			mailSub = "[签办单通知]：有一张签办单已结案，请查看";
			break;
		default:
			if(newState == 3){
				mailSub = "[签办单通知]：有一张签办单已完成会签，等待您处理";
			} else {
				mailSub = "[签办单通知]：有一张签办单状态已变更，等待您处理";
			}
			break;
		}
		mailSettingTo.setEmailSubject(mailSub);
		//content
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
							"font-family: '微软雅黑';}"+
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
							"font-family: '微软雅黑';}"+
							".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
		if(newState == 5 || newState == -2 || newState == -3){
			if(newState == 5){
				mailContent.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一张签办单已通过审批，详细信息如下：</font><br><br><br>");
			} else {
				mailContent.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一张签办单已结案，详细信息如下：</font><br><br><br>");
			}
			mailContent.append("<table class='grid_table'><tr><th>当前状态</th><th>签办单编号</th><th>事由</th><th>申请人</th><th>公司</th><th>申请时间</th><th>结案时间</th></tr>");
		} else {
			mailContent.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一张签办单需要您处理，详细信息如下：</font><br><br><br>");
			mailContent.append("<table class='grid_table'><tr><th>当前状态</th><th>签办单编号</th><th>事由</th><th>申请人</th><th>公司</th><th>申请时间</th><th>收到时间</th></tr>");
		}
		mailContent.append("<tr><td style='text-align: center;'>" + signOrderTo.getOrderStatusName() + "</td>");
		mailContent.append("<td style='text-align: center;'>" + signOrderTo.getSignCode() + "</td>");
		mailContent.append("<td style='text-align: left;'>" + signOrderTo.getSummary() + "</td>");
		mailContent.append("<td style='text-align: center;'>" + signOrderTo.getApplyUserName() + "</td>");
		mailContent.append("<td style='text-align: center;'>" + (signOrderTo.getCompanyCode()==1?"裕融":(signOrderTo.getCompanyCode()==2?"裕国":"")) + "</td>");
		mailContent.append("<td style='text-align: center;'>" + DateUtil.dateToString((Date)signOrderTo.getCreateTime(), "yyyy-MM-dd HH:mm") + "</td>");
		mailContent.append("<td style='text-align: center;'>" + DateUtil.dateToString((Date)signOrderTo.getUpdateTime(), "yyyy-MM-dd HH:mm") + "</td></tr>");
		mailContent.append("</table></body></html>");
		mailSettingTo.setEmailContent(mailContent.toString());
		//send
		mailUtilService.sendMail(mailSettingTo);
	}
	
	/**
	 * 更新会签名单
	 * @param signOrderId
	 * @param countersignCodeOrder
	 * @param opType
	 */
	@Transactional
	public void transfer(int signOrderId, int opUserId) throws Exception{
		//日志
		SignOrderTo signOrderTo = this.getSignOrderById(signOrderId);
		UserTo user = this.userDAO.getUserById(opUserId);
		
		if (signOrderTo!=null && user != null) {
			
			signOrderTo.setCurrentOperatorId(opUserId);
			this.signOrderDao.updateSignOrder(signOrderTo);
			
			SignOrderLogTo signOrderLogTo = new SignOrderLogTo();
			signOrderLogTo.setOperateTimeBegin(signOrderTo.getUpdateTime());
			signOrderLogTo.setOperateTimeEnd(signOrderTo.getUpdateTime());
			signOrderLogTo.setOperateState(13);
			signOrderLogTo.setSignStatus(signOrderTo.getOrderStatus());
			signOrderLogTo.setSignOrderId(signOrderTo.getId());
			signOrderLogTo.setOperatorId(opUserId);
			signOrderLogTo.setOrgOperatorId(signOrderTo.getCurrentOperatorId());
			signOrderLogTo.setOperateSuggest("处理人更改为：" + user.getName());
			//插入日志
			this.addSignOrderLog(signOrderLogTo);
		}
		
	}
}
