package com.brick.modifyOrder.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.BaseTo;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.modifyOrder.dao.ModifyOrderDao;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.sms.email.TestMail;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ModifyOrderService extends BaseService {
	Log logger = LogFactory.getLog(ModifyOrderService.class);

	private ModifyOrderDao modifyOrderDao;
	
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public ModifyOrderDao getModifyOrderDao() {
		return modifyOrderDao;
	}

	public void setModifyOrderDao(ModifyOrderDao modifyOrderDao) {
		this.modifyOrderDao = modifyOrderDao;
	}

	// 图片信息
	@SuppressWarnings("unchecked")
	public void addOrderImage(Context context) throws Exception{
			List fileItems = (List) context.contextMap.get("uploadList");
			if (fileItems != null && fileItems.size() > 0) {
				FileItem fileItem = null;
				for (int i = 0; i < fileItems.size(); i++) {
					fileItem = (FileItem) fileItems.get(i);
					logger.info("文件大小==========>>" + fileItem.getSize());
					if (fileItem.getSize() > 2042255) {
						throw new Exception("不好意思，您上传的文件大于2M了。");
					}
				}
				for (int i = 0 ;i < fileItems.size() ;i++ ) {
					fileItem = (FileItem) fileItems.get(i);
					InputStream in =fileItem.getInputStream();		
					if(!fileItem.getName().equals("")){
						saveFileToDisk(context,fileItem,"更改单附件");
					}
				}
			}
			try {
			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
			}
		
	}

	//保存文件
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,String title) throws Exception {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("modifyOrderImage");
		String file_path = "";

		if (bootPath != null) {
			File realPath = new File(bootPath + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
				String imageName = FileExcelUpload.getNewFileName();
				File uploadedFile = new File(realPath.getPath() + File.separator + imageName + "." + type);
				file_path = '/' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + '/' + type + '/'+ imageName + "."+ type;
				try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					//增加关联
					contextMap.put("PATH", file_path);
					contextMap.put("IMAGE_NAME", fileItem.getName());
					contextMap.put("TITLE", title);
					insert("modifyOrder.insertOrderFile", contextMap);
				}

			} catch (Exception e) {
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				throw e;
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
		return file_path;
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
	 * 撤案
	 */
	public void deleteModifyOrder(Map<String, Object> map){
		//撤案
		try {
			map.put("STATUS", "2");
			map.put("ORDER_STATUS", "1");
			map.put("ORDER_TYPE","2");
			modifyOrderDao.deleteModifyOrder(map);
		} catch (DaoException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	/**
	 * 添加
	 */
	@Transactional(rollbackFor=Exception.class)
	public void add(Context context)throws Exception{
		Map userLogin = null;
		Map<String, Object> conMap = context.contextMap;
		//String classorder =(String)conMap.get("ORDER_CLASS");
		Integer modifyId = 0 ;
			// 申请人信息
			userLogin = (Map) queryForObj("modifyOrder.queryUserByUid",context.contextMap);
			conMap.put("APPLY_LEADER_ID", userLogin.get("UPPERUSER").toString());
			conMap.put("APPLY_NAME", userLogin.get("NAME").toString());
			conMap.put("DEPT_ID", userLogin.get("DEPT_ID").toString());
			//格式化修改内容
			 conMap.put("INTRODUCTION", StringUtils.autoInsertWrap((String)conMap.get("INTRODUCTION"),40));
			//取处更改单编码
			String modifyOrderCode=CodeRule.getModifyOrderReturnCode();
			conMap.put("MODIFY_ORDER_CODE", modifyOrderCode);
			
			// 添加新订单，ORDER_TYPE:0表示申请，1表示处理，2表示验证,ORDER_STATUS:0表示未处理，1表示通过，2表示驳回
			modifyId = (Integer)insert("modifyOrder.insertOrder", conMap);
			//记录日志
			context.contextMap.put("ORDER_STATUS", "0");
			context.contextMap.put("ORDER_TYPE", "0");
			context.contextMap.put("MODIFY_ID", modifyId);
			context.contextMap.put("LOG_IP",context.getRequest().getRemoteAddr());
			context.contextMap.put("REMARK_LOG", "提交申请，更改单内容："+(String)context.contextMap.get("INTRODUCTION"));
			insertOrderLogNew(context.contextMap);
			//上传附件
			addOrderImage(context);
			
			// 发送邮件
			MailSettingTo mailSettingTo =new MailSettingTo();
			//邮件内容
			mailSettingTo.setEmailContent(getMailContent(context.contextMap));
			//抄送
			mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL")+";mohonghua@tacleasing.cn");
			//审核人的邮箱
			mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
			//邮件主题
			mailSettingTo.setEmailSubject("[资讯通知]：您好，更改单编号为："+modifyOrderCode+"需您审批");
			
			mailUtilService.sendMail(mailSettingTo);
			
	}
	
	//邮件内容（新建）
		private String getMailContent(Map<String, Object> orderEmail){
			if (orderEmail == null) {
				return null;
			}
			String isLeaseCode=(String)orderEmail.get("IS_LEASE_CODE");
			String isPay=(String)orderEmail.get("IS_PAY");
			if(("0").equals(isLeaseCode)){
				isLeaseCode="不保留合同号";
			}else if(("1").equals(isLeaseCode)){
				isLeaseCode="保留合同号";
			}
			if(("0").equals(isPay)){
				isPay="未拨款";
			}else if(("1").equals(isPay)){
				isPay="已拨款";
			}
			StringBuffer sb = new StringBuffer();
				sb.append("<html><head></head>");
				sb.append("<font size='3'><b>您好:<b><br></font>" + "<font size='3'>有一張更改單需您審批，詳細資訊如下：</font><br><br><br>"
				+"<font size='2'>更改单编号：</font>"+orderEmail.get("MODIFY_ORDER_CODE")
				+"<br><font size='2'>承租人名称：</font>"+orderEmail.get("CUST_NAME")+
				"<br><font size='2'>合同號:</font>" +orderEmail.get("LEASE_CODE")+
				"<br><font size='2'>是否撥款：</font>"+isPay+"<br><font size='2'>是否保留合同：</font>"+isLeaseCode+
				"<br><font size='2'>修改描述：</font>"+orderEmail.get("INTRODUCTION"));
				sb.append("</html>");
			return sb.toString();
		}
		//邮件内容
		public void insertOrderLogNew(Map map) {
			try {
				//换行格式化
				map.put("REMARK_LOG", StringUtils.autoInsertWrap((String)map.get("REMARK_LOG"),20));
				insert("modifyOrder.insertOrderLog",map);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		//更改附件
		public void updateFile(Map<String,Object> map){
			try {
				map.put("STATUS", "-1");
				modifyOrderDao.updateFile(map);
			} catch (DaoException e) {
				e.printStackTrace();
			}
		}
		//查询更改单每月平均处理时间
		public  List<Map> getAvgtimeForOrder(Context context) throws Exception {
			Map dateMap = new HashMap();
			dateMap.put("startDate",context.contextMap.get("year")+"-01-01");
			dateMap.put("endDate", context.contextMap.get("year")+"-12-01");
			List<Map> monthList =(List<Map>) queryForList("modifyOrder.getMonthList", dateMap);
			if(monthList !=null && monthList.size()>0){
				for(Map mon:monthList){
					Map paramMap = new HashMap();
					paramMap.put("date", mon.get("MONTH1"));
					//每个月平均值
					List<Map> avgTimeOrderByMonth =(List<Map>) queryForList("modifyOrder.getAvgtimeForOrder", paramMap);
					mon.put("avgList", avgTimeOrderByMonth);
					//每个月总数
					int count=(Integer)queryForObj("modifyOrder.getOrderCountByMonths", paramMap);
					mon.put("oCount", count);
				}
			}
			return monthList;
		}
		//查询总计
		public  Map getTotal(Context context) throws Exception {
			Map<String,Object> dateMap = new HashMap<String,Object>();
			dateMap.put("getYear",context.contextMap.get("year")+"-01-01");
			Map totalMap =(Map)queryForObj("modifyOrder.getTotal", dateMap);
			int count=(Integer)queryForObj("modifyOrder.getAllCountYear", dateMap);
			totalMap.put("allCount", count);
			return totalMap;
		}
		//查看每个月详情
		
		public  List<Map> getDayListForOrder(Context context) throws Exception {
			Map<String,Object> dateMap = new HashMap<String,Object>();
			dateMap.put("getYear",context.contextMap.get("year")+"-01-01");
			dateMap.put("mon",context.contextMap.get("status"));
			List<Map> daylisy =(List<Map>) queryForList("modifyOrder.getDayList",dateMap );
			return daylisy;
		}
		//更改单统计表
		public  List<Map> showOrderForDept(Context context) throws Exception {
			Map<String,Object> dateMap = new HashMap<String,Object>();
			dateMap.put("START_DATE", context.contextMap.get("START_DATE"));
			dateMap.put("END_DATE", context.contextMap.get("END_DATE"));
			List<Map> countList =(List<Map>) queryForList("modifyOrder.getOrderForDept",dateMap );
			return countList;
		}
		//分类合计
		public  List<Map> showCountByClass(Context context) throws Exception {
			Map<String,Object> dateMap = new HashMap<String,Object>();
			dateMap.put("START_DATE", context.contextMap.get("START_DATE"));
			dateMap.put("END_DATE", context.contextMap.get("END_DATE"));
			List<Map> countList =(List<Map>) queryForList("modifyOrder.showCountByClass",dateMap );
			return countList;
		}
		//办事处合计
		public  List<Map> showCountByDept(Context context) throws Exception {
			Map<String,Object> dateMap = new HashMap<String,Object>();
			dateMap.put("START_DATE", context.contextMap.get("START_DATE"));
			dateMap.put("END_DATE", context.contextMap.get("END_DATE"));
			List<Map> countList =(List<Map>) queryForList("modifyOrder.showCountByDept",dateMap );
			return countList;
		}
		
		/**
		 *查询更改单总数
		 */
		public  Integer getCountAllOrderOfClass(Context context) throws Exception{
			BaseTo baseTo =new BaseTo();
			baseTo.setAuth_date_str((String)context.contextMap.get("START_DATE"));
			baseTo.setCreate_date_str((String)context.contextMap.get("END_DATE"));
			int countAll=(Integer)DataAccessor.queryForObj("modifyOrder.getCountAllOrderOfClass", baseTo);
			return countAll;
		}
		
		/**
		 * 获取资讯需求单上传路径
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
		 * 生成邮件内容
		 * @param demand 资讯单信息
		 * @return
		 */
		public String getDemandMailContent(Map demand){
			StringBuffer sb = new StringBuffer();
			sb.append("<html><head></head>");
			sb.append("您好：<br>有一张资讯需求单需您处理，详细讯息如下：<br><br><br>"
						+"资讯需求单编号：<b>" + demand.get("DEMAND_CODE") + "</b>"
						+"<br>摘要：<b>" + demand.get("SUMMARY") + "</b>"
						+"<br>申请人：" + demand.get("APPLY_USER_NAME")
						+"<br>申请时间：" + DateUtil.dateToString((Date)demand.get("CREATE_TIME"))
						+"<br>资讯需求单当前状态：<b>" + demand.get("CODE_NAME") + "</b>");
			if(demand.get("HOURS") != null 
					&& !"".equals(demand.get("HOURS").toString()) 
					&& demand.get("newStatus") != null 
					&& Integer.parseInt(demand.get("newStatus").toString()) < 900){
				sb.append("<br>预计工时：" + demand.get("HOURS").toString() + "<br>预计完成日期：" + demand.get("PREDICT_DATE"));
			}
			sb.append("</html>");
			return sb.toString();
		}
		
		/**
		 * 获取某个状态码的下一个状态码
		 * @param stateMap
		 * @return
		 */
		public int getAfterDemandState(List<Map> stateMap, int currentState){
			int newState = 0;
			for(int i = 0; i < stateMap.size(); i++){
				int temState = Integer.parseInt(stateMap.get(i).get("CODE").toString());
				if(temState > currentState){
					newState = temState;
					break;
				}
			}
			return newState;
		}
		
		/**
		 * 查询当前状态的后一状态
		 * @param stateMap
		 * @return
		 */
		public int getDemandStateAfterOldState(List<Map> stateMap, int oldStatus){
			int index = 0;
			for(int i = 0; i < stateMap.size(); i++){
				if(oldStatus == Integer.parseInt(stateMap.get(i).get("CODE").toString())){
					//取得当前资讯单状态索引
					index = i;
					break;
				}
			}
			index = index + 1;
			return Integer.parseInt(stateMap.get(index).get("CODE").toString());
		}

		/**
		 * 获取资讯需求单状态map
		 * @param context
		 * @return
		 * @throws Exception
		 */
		public List<Map> getDemandStatesList(Context context) throws Exception{
			//资讯需求单状态
			List<Map> states = (List<Map>) DataAccessor.query("demand.getDemandStatesList", context.contextMap,DataAccessor.RS_TYPE.LIST);
			return states;
		}
		
		/**
		 * 取得资讯单更新后处理信息（处理人ID，更新后的状态）
		 * @param content
		 *  -1:已撤案
		 * 	0:已驳回 
			100:单位主管审核中 
			200:处级主管审核中 
			
			300-349会签
			300:部门会签确认中 
			301:总经理室
			302:业管部
			303:机器设备业务处
			304:稽核室
			305:审查处
			306:商用车业务处
			307:财务部
			308:乘用车业务处
			309:资讯部
			
			360:高阶签核
			
			400:资讯单分配中 
			450:资讯开发确认中
			500:资讯开发中
			600:资讯主管确认中
			
			900-949验收
			900:申请人验收中
			910:主管验收中
			990:验收通过
		 * @return
		 */
		public Map getUserIdByDemandStatus(Context context) {
			Map demand = null;
			//旧资讯单状态码
			int oldStatus = 0;
			//新资讯单状态码
			int newStatus = 0;
			//新处理人ID
			int newUserId = 0;
			String completeContersignDeptIds = "";
			try {
				demand = (Map)DataAccessor.query("demand.getDemandById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				oldStatus = (Integer)demand.get("ORDER_STATUS");
				//资讯单状态列表
				List<Map> stateMap = (List<Map>)context.contextMap.get("states");
				//默认通过状态
				String demandPassType = context.contextMap.get("opType")==null?"3":context.contextMap.get("opType").toString();
				int opType = Integer.parseInt(demandPassType);
				if(opType == -11){
					//撤案
					newStatus = -1;
				} else if(opType == 13){
					//转移
					newStatus = oldStatus;
					newUserId = Integer.parseInt(context.contextMap.get("newOpUser").toString());
					//分配给开发人员
					if(oldStatus >= 450 && oldStatus <= 500){
						demand.put("RESPONSIBLE_USER_ID", newUserId);
					}
				} else if(opType == 12){
					//重新提交
					for(int i = 0; i < stateMap.size(); i++){
						int code = Integer.parseInt(stateMap.get(i).get("CODE").toString());
						if(code > 0){
							//CODE>0为正常流程状态，取最小值为工作流程的第一步
							newStatus = code;
							break;
						}
					}
				} else if(opType == 14){
					//修改开发预估信息
					demand.put("HOURS", Double.parseDouble(context.contextMap.get("hours").toString()));
					demand.put("PREDICT_DATE", context.contextMap.get("PREDICT_DATE"));
					newUserId = Integer.parseInt(demand.get("CURRENT_OPERATOR_ID").toString());
					newStatus = oldStatus;
				} else if(opType == 9){
					//修改开发预估信息
					demand.put("ALTER_TYPE", context.contextMap.get("ALTER_TYPE").toString());
					demand.put("SYS_LEVEL", context.contextMap.get("SYS_LEVEL"));
					demand.put("RELEASE_TIME", context.contextMap.get("RELEASE_TIME"));
					demand.put("content", context.contextMap.get("content"));
					newStatus = 600;
				} 
//				else if(opType == -15) {
//					//开发取消，退回给申请人
//					newStatus = 0;
//				} 
				else if(opType < 0){
				//驳回
					if(oldStatus > 500){
						//资讯单开发状态以后被驳回，回到开发人员确认开发
						newStatus = 450;
						demand.put("HOURS", null);
						demand.put("PREDICT_DATE", null);
						newUserId = Integer.parseInt(demand.get("RESPONSIBLE_USER_ID").toString());
					} else if(oldStatus == 500){
						//资讯单开发状态反馈，状态修改为资讯单分配中
						newStatus = 400;
						demand.put("HOURS", null);
						demand.put("PREDICT_DATE", null);
					} else if(oldStatus > 300 && oldStatus < 350){
						//会签驳回，清空已会签名单
						completeContersignDeptIds = "null";
					} else if(oldStatus == 360){
						//总经理不同意
						newStatus = -2;
					} else {
						//未进入开发状态驳回后，退回给申请人
						newStatus = 0;
					}
				} else {
				//通过
					String oldSenior = demand.get("SENIOR_SIGN")==null?"":demand.get("SENIOR_SIGN").toString();
					//更新至下一个状态
					if(oldStatus > 0 && oldStatus < 300 || oldStatus >= 350 && oldStatus < 990){
						//非会签状态
						if(opType == 4 || opType == 5){
							//修改会签名单，更新
							newStatus = oldStatus;
							int seniorSign = 0;
							//会签名单修改,则更新高阶签核
							if(!oldSenior.equals("2")){
								seniorSign = context.contextMap.get("seniorSign")==null?0:1;
								if(seniorSign == 0){
									String deptIds = context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString();
									int count = this.getCodeNamesCount(deptIds, (Map<String,String>)context.contextMap.get("stateMap"));
									if(count >= 2){
										seniorSign = 1;
									}
								}
							}
							SqlMapClient sqlMapper = (SqlMapClient)context.contextMap.get("sqlMapper");
							Map param = new HashMap();
							param.put("demandId", demand.get("ID"));
							param.put("seniorSign", seniorSign);
							context.contextMap.put("seniorSign", seniorSign);
							param.put("COUNTERSIGN_CODE_ORDER", context.contextMap.get("COUNTERSIGN_CODE_ORDER"));
							sqlMapper.update("demand.updateDemandById", param);
						} else {
							if(oldStatus == 400){
								newUserId = Integer.parseInt(context.contextMap.get("ALTER_USER_ID").toString());
								demand.put("RESPONSIBLE_USER_ID", newUserId);
							} else if(oldStatus == 450){
								demand.put("HOURS", Double.parseDouble(context.contextMap.get("hours").toString()));
								demand.put("PREDICT_DATE", context.contextMap.get("PREDICT_DATE"));
								newUserId = Integer.parseInt(demand.get("CURRENT_OPERATOR_ID").toString());
							}
							newStatus = this.getDemandStateAfterOldState(stateMap, oldStatus);
						}
					} else if(oldStatus >= 300 && oldStatus < 350){
						context.contextMap.put("seniorSign", oldSenior);
						if(!oldSenior.equals("2")){
							//非必须高阶签核
							if(opType == 4 || opType == 5){
								//会签名单修改,则更新高阶签核
								int seniorSign = context.contextMap.get("seniorSign")==null?0:1;
								if(seniorSign == 0){
									String deptIds = context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString();
									int count = this.getCodeNamesCount(deptIds, (Map<String,String>)context.contextMap.get("stateMap"));
									if(count >= 2){
										seniorSign = 1;
									}
								}
								SqlMapClient sqlMapper = (SqlMapClient)context.contextMap.get("sqlMapper");
								Map param = new HashMap();
								param.put("id", demand.get("ID"));
								context.contextMap.put("seniorSign", seniorSign);
								param.put("seniorSign", seniorSign);
								oldSenior = String.valueOf(seniorSign);
								sqlMapper.update("demand.updateDemandSeniorSignById", param);
							} else if(opType == 16 || opType == 17){
								//保留意见，其他意见则更新高阶签核状态为强制签核
								SqlMapClient sqlMapper = (SqlMapClient)context.contextMap.get("sqlMapper");
								Map param = new HashMap();
								param.put("id", demand.get("ID"));
								oldSenior = "2";
								context.contextMap.put("seniorSign", oldSenior);
								param.put("seniorSign", 2);
								sqlMapper.update("demand.updateDemandSeniorSignById", param);
							}
						}
						//会签状态
						//COUNTERSIGN_CODE_ORDER:会签部门ID序列（，分割）, COMPLETE_CODE_ORDER：已完成会签部门ID（，分割）
						String contersignDeptIdOrder = context.contextMap.get("COUNTERSIGN_CODE_ORDER")==null?"":context.contextMap.get("COUNTERSIGN_CODE_ORDER").toString();
						String oldContersignDeptIdOrder = demand.get("COUNTERSIGN_CODE_ORDER")==null?"":demand.get("COUNTERSIGN_CODE_ORDER").toString();
						//如果有提交新的部门会签序列，则取新序列做流程
						contersignDeptIdOrder = contersignDeptIdOrder.equals("")?oldContersignDeptIdOrder:contersignDeptIdOrder;
						completeContersignDeptIds = demand.get("COMPLETE_CODE_ORDER")==null?"":demand.get("COMPLETE_CODE_ORDER").toString();
						//当前会签部门
						String currentComplete = "";
						//非修改会签部门就添加当前会签部门至已会签中
						if(opType == 6 || opType == 16 || opType == 17){
							currentComplete = oldStatus + "";
						}
						if(!currentComplete.equals("")){
							if(completeContersignDeptIds.equals("")){
								completeContersignDeptIds = currentComplete;
							} else {
								completeContersignDeptIds = completeContersignDeptIds + "," + currentComplete;
							}
						}
						//无会签部门
						//默认为会签全部结束，跳至会签后第一个状态
						newStatus = this.getAfterDemandState(stateMap, 349);
						if(newStatus == 360 && !(oldSenior.equals("2") || oldSenior.equals("1"))){
							//高阶签核状态如果未启用则跳过高阶签核
							newStatus = this.getAfterDemandState(stateMap, 360);
						}
						if(!contersignDeptIdOrder.equals("")){
							//在‘会签部门ID序列’中找到第一个不存在于‘已完成会签部门ID’的id，为下一个会签部门ID
							String[] allIds = contersignDeptIdOrder.split(",");
							if(completeContersignDeptIds.equals("")){
								newStatus = Integer.parseInt(allIds[0]);
							} else {
								String[] completeIds = completeContersignDeptIds.split(",");
								for(int i = 0; i < allIds.length; i++){
									boolean has = false;
									for(int j = 0; j < completeIds.length; j++){
										if(allIds[i].equals(completeIds[j])){
											has = true;
											break;
										}
									}
									//不存在
									if(!has){
										newStatus = Integer.parseInt(allIds[i]);
										break;
									}
								}
							}
						}
					}
				}
				context.contextMap.put("stateMap", stateMap);
				demand.put("newStatus", newStatus);
				demand.put("newUserId", newUserId);
				demand.put("COMPLETE_CODE_ORDER", completeContersignDeptIds);
				demand = this.getNewUserIdByNewStatus(context, demand);
				demand.put("COMPLETE_CODE_ORDER", completeContersignDeptIds);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			return demand;
		}
		
		/**
		 * 根据新的状态码获取新的处理人ID
		 * @return
		 */
		private Map getNewUserIdByNewStatus(Context context, Map demand){
			List<Map> stateMap = (List<Map>) context.contextMap.get("stateMap");
			int newStatus = Integer.parseInt(demand.get("newStatus").toString());
			int newUserId = Integer.parseInt(demand.get("newUserId").toString());
			if(newStatus != -1 && newUserId == 0 && newStatus != -2){
				//非撤销，根据下一步需求单状态，获取处理人ID
				if(newStatus == 300){
					//2014-3-14取消会签名单确定步骤，直接添加（业管302、资讯309）顺序会签
					newStatus = 302;
					String countersignCodeOrder = "302,309";
					if(demand.get("COUNTERSIGN_CODE_ORDER") != null && !StringUtils.isEmpty(demand.get("COUNTERSIGN_CODE_ORDER").toString())){
						countersignCodeOrder = demand.get("COUNTERSIGN_CODE_ORDER").toString();
						if(countersignCodeOrder.indexOf("302") < 0){
							countersignCodeOrder = "302," + countersignCodeOrder;
						}
						if(countersignCodeOrder.indexOf("309") < 0){
							countersignCodeOrder = "309," + countersignCodeOrder;
						}
					}
					context.contextMap.put("COUNTERSIGN_CODE_ORDER",countersignCodeOrder);
				}
				if(newStatus >= 301 && newStatus < 350){
					//待会签，从数据字典中状态里取id
					newUserId = getDemandStateByDemandCode(newStatus, stateMap);
				} else	if(newStatus >= 350 && newStatus < 950 || newStatus <= 300){
					//非验收通过,非会签，继续走流程，查询查理人ID
					switch (newStatus) {
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
						newUserId = this.getDemandStateByDemandCode(309, stateMap);
						break;
					case 900:
						newUserId = Integer.parseInt(demand.get("APPLY_USER_ID").toString());
						break;
					case 360:
						//高阶会签,从数据字典中状态里取id
						newUserId = getDemandStateByDemandCode(newStatus, stateMap);
					default:
						break;
					}
				}
			}
			demand.put("newStatus", newStatus);
			demand.put("newUserId", newUserId);
			return demand;
		}
		
		/**
		 * 根据资讯需求单状态码获取该状态应签核的userId
		 * @param demandCode
		 * @param demandStatesMap
		 * @return
		 */
		public int getDemandStateByDemandCode(int demandCode, List<Map> demandStatesMap){
			int userId = 0;
			for(int i = 0; i < demandStatesMap.size(); i++){
				if(Integer.parseInt(demandStatesMap.get(i).get("CODE").toString()) == demandCode){
					//字典表REMARK字段记录该会签部门的领导ID
					userId = Integer.parseInt(demandStatesMap.get(i).get("REMARK").toString());
					break;
				}
			}
			return userId;
		}
		
		/**
		 * 判断处级单位数量
		 * @param chooseCodesName
		 * @return
		 */
		public int getCodeNamesCount(String deptIds, Map stateMap){
			//判断处级单位数量，暂时用会签字符串中最后一个字为“处”字的部门数，之后考虑修改成新表关联
			int count = 0;
			String deptName = "";
			if(!StringUtils.isEmpty(deptIds)){
				String[] codes = deptIds.split(",");
				for(int i = 0; i < codes.length; i++){
					deptName = stateMap.get(codes[i])==null?"":stateMap.get(codes[i]).toString();
					if(deptName.trim().indexOf("处") == deptName.trim().length() - 1){
						count++;
					}
				}
			}
			return count;
		}

		/**
		 * 获取某人某个状态下资讯单的数量
		 * @param stateCode
		 * @param userId
		 * @return
		 */
		public int getDemandCountByStateAndUserId(int stateCode, int userId){
			return this.modifyOrderDao.getDemandCountByStateAndUserId(stateCode, userId);
		}
}
