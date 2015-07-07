package com.brick.invoiceManagement.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.invoiceManagement.dao.InvoiceManagementDAO;
import com.brick.invoiceManagement.util.InvoiceManagementUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class InvoiceManagementService extends BaseService {

	Log logger=LogFactory.getLog(this.getClass());
	
	private InvoiceManagementDAO invoiceManagementDAO;
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public InvoiceManagementDAO getInvoiceManagementDAO() {
		return invoiceManagementDAO;
	}

	public void setInvoiceManagementDAO(InvoiceManagementDAO invoiceManagementDAO) {
		this.invoiceManagementDAO = invoiceManagementDAO;
	}
	
	public List<Map<String,Object>> stopInvoiceQuery(Map<String,Object> param) {
		return this.invoiceManagementDAO.stopInvoiceQuery(param);
	}
	
	public List<String> getEffectDateList() {
		return this.invoiceManagementDAO.getEffectDateList();
	}
	
	public List<Map<String,Object>> getPaymentDetail(Context context) {
		return this.invoiceManagementDAO.getPaymentDetail(context);
	}
	
	@Transactional
	public void stopInvoiceProcess(Context context) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("RECP_ID",context.contextMap.get("RECP_ID"));
		this.invoiceManagementDAO.cancelStopInvoice(param);
		
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,1);
		param.put("EFFECT_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));//获得次月生效时间
		
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		param.put("REMARK",context.contextMap.get("remark"));
		param.put("STOP_TYPE",InvoiceManagementUtil.STOP_TYPE.MANUAL.toString());
		this.invoiceManagementDAO.addStopInvoice(param);
		
		this.invoiceManagementDAO.insertDunRecord(param);//插入法务催收
		
		context.contextMap.put("OPERATE_FROM",InvoiceManagementUtil.OPERATE_FROM.MANUAL_FUNCTION.toString());
		this.invoiceManagementDAO.updateSpecialCase(context);//把维护自动停复开表中的数据作废
	}
	
	@Transactional
	public void openInvoiceProcess(Context context) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("RECP_ID",context.contextMap.get("RECP_ID"));
		
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,1);
		param.put("EFFECT_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));//获得次月生效时间
		
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		param.put("REMARK",context.contextMap.get("remark"));
		this.invoiceManagementDAO.updateStopInvoice(param);
		
		context.contextMap.put("OPERATE_FROM",InvoiceManagementUtil.OPERATE_FROM.MANUAL_FUNCTION.toString());
		this.invoiceManagementDAO.updateSpecialCase(context);//把维护自动停复开表中的数据作废
	}
	
	public String checkStopInvoice(Map<String,Object> param) {
		return this.invoiceManagementDAO.checkStopInvoice(param);
	}
	public String checkOpenInvoice(Map<String,Object> param) {
		return this.invoiceManagementDAO.checkOpenInvoice(param);
	}
	public String checkNotOpenInvoice(Map<String,Object> param) {
		return this.invoiceManagementDAO.checkNotOpenInvoice(param);
	}
	public String checkNotStopInvoice(Map<String,Object> param) {
		return this.invoiceManagementDAO.checkNotStopInvoice(param);
	}
	
	public List<Map<String,Object>> showLog(Context context) {
		return this.invoiceManagementDAO.showLog(context);
	}
	public List<Map<String,Object>> showSpecialLog(Context context) {
		return this.invoiceManagementDAO.showSpecialLog(context);
	}
	
	//系统自动停开发票
	@Transactional
	public void batchjobForStopInvoice() throws Exception {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,1);
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("EFFECT_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));//获得次月生效时间
		param.put("s_employeeId",184);
		param.put("REMARK","系统:逾期45天之案件系统自动停开发票");
		param.put("STOP_TYPE",InvoiceManagementUtil.STOP_TYPE.DUN_45.toString());
		try {
			List<String> dun45daysCaseList=this.invoiceManagementDAO.getDun45DaysCase();
			for(int i=0;i<dun45daysCaseList.size();i++) {
				if(StringUtils.isEmpty(dun45daysCaseList.get(i))) {
					continue;
				}
				param.put("RECP_ID",dun45daysCaseList.get(i));
				this.invoiceManagementDAO.addStopInvoice(param);
				this.invoiceManagementDAO.insertDunRecord(param);
			}
		} catch(Exception e) {
			throw e;
		}
	}
	
	//系统自动复开发票
	public void batchjobForOpenInvoice() throws Exception {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DATE,1);
		cal.add(Calendar.MONTH,1);
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("EFFECT_DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));//获得次月生效时间
		param.put("s_employeeId",184);
		param.put("REMARK","系统:逾期45天之案件已无逾期,系统自动复开发票");
		try {
			this.invoiceManagementDAO.updateStopDun45DaysInvoice(param);
		} catch(Exception e) {
			throw e;
		}
	}
	
	//系统每天8点30发送邮件job
	public void batchjobForSendEmailReminder() throws Exception {
		
		if(super.isWorkingDay()) {//如果是工作日才发送邮件
			List<Map<String,Object>> resultList=this.invoiceManagementDAO.getEmailContentList();
			if(resultList!=null&&resultList.size()>0) {//发送给各办事处
				List<DeptCmpyTO> cmpyList=this.baseDAO.getCompanyList();
				for(int i=0;cmpyList!=null&&i<cmpyList.size();i++) {
					if(Constants.CMPY_17.equals(cmpyList.get(i).getCompanyId())) {//苏州设备
						this.sendEmail(Constants.CMPY_17,resultList,204);
					} else if(Constants.CMPY_2.equals(cmpyList.get(i).getCompanyId())) {//昆山设备
						this.sendEmail(Constants.CMPY_2,resultList,205);
					} else if(Constants.CMPY_7.equals(cmpyList.get(i).getCompanyId())) {//南京设备
						this.sendEmail(Constants.CMPY_7,resultList,207);
					} else if(Constants.CMPY_13.equals(cmpyList.get(i).getCompanyId())) {//上海设备
						this.sendEmail(Constants.CMPY_13,resultList,206);
					} else if(Constants.CMPY_23.equals(cmpyList.get(i).getCompanyId())) {//天津设备
						this.sendEmail(Constants.CMPY_23,resultList,20);
					} else if(Constants.CMPY_22.equals(cmpyList.get(i).getCompanyId())) {//济南设备
						this.sendEmail(Constants.CMPY_22,resultList,21);
					} else if(Constants.CMPY_25.equals(cmpyList.get(i).getCompanyId())) {//武汉设备
						this.sendEmail(Constants.CMPY_25,resultList,22);
					} else if(Constants.CMPY_27.equals(cmpyList.get(i).getCompanyId())) {//长沙设备
						this.sendEmail(Constants.CMPY_27,resultList,23);
					} else if(Constants.CMPY_24.equals(cmpyList.get(i).getCompanyId())) {//郑州设备
						this.sendEmail(Constants.CMPY_24,resultList,24);
					} else if(Constants.CMPY_26.equals(cmpyList.get(i).getCompanyId())) {//宁波设备
						this.sendEmail(Constants.CMPY_26,resultList,25);
					} else if(Constants.CMPY_3.equals(cmpyList.get(i).getCompanyId())) {//东莞设备
						this.sendEmail(Constants.CMPY_3,resultList,210);
					} else if(Constants.CMPY_8.equals(cmpyList.get(i).getCompanyId())) {//佛山设备
						this.sendEmail(Constants.CMPY_8,resultList,211);
					} else if(Constants.CMPY_11.equals(cmpyList.get(i).getCompanyId())) {//厦门设备
						this.sendEmail(Constants.CMPY_11,resultList,212);
					} else if(Constants.CMPY_9.equals(cmpyList.get(i).getCompanyId())) {//重庆设备
						this.sendEmail(Constants.CMPY_9,resultList,208);
					} else if(Constants.CMPY_14.equals(cmpyList.get(i).getCompanyId())) {//成都设备
						this.sendEmail(Constants.CMPY_14,resultList,209);
					} else if(Constants.CMPY_16.equals(cmpyList.get(i).getCompanyId())) {//苏州商用车
						this.sendEmail(Constants.CMPY_16,resultList,213);
					} else if(Constants.CMPY_15.equals(cmpyList.get(i).getCompanyId())) {//上海商用车
						this.sendEmail(Constants.CMPY_15,resultList,26);
					} else if(Constants.CMPY_20.equals(cmpyList.get(i).getCompanyId())) {//上海乘用车
						this.sendEmail(Constants.CMPY_20,resultList,27);
					} else if(Constants.CMPY_21.equals(cmpyList.get(i).getCompanyId())) {//杭州乘用车
						this.sendEmail(Constants.CMPY_21,resultList,28);
					} else if(Constants.CMPY_28.equals(cmpyList.get(i).getCompanyId())) {//深圳乘用车
						this.sendEmail(Constants.CMPY_28,resultList,29);
					}
				}
			}
			if(resultList!=null&&resultList.size()>0) {
				StringBuffer mailContent=this.setCss();//发送给中后台
				int i=1;
				for(int j=0;j<resultList.size();j++) {
					if(resultList.get(j).get("DECP_ID")==null) {
						continue;
					}
					mailContent.append("<tr><td style='text-align: center;'>"+i+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("LEASE_CODE")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("CUST_NAME")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("NAME")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("DECP_NAME_CN")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("STOP_REMARK")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("STOP_TIME")+"</td>" +
							"<td style='text-align: center;'>"+resultList.get(j).get("EFFECT_DATE")+"</td>" +
							"</tr>");
					i++;
				}
				mailContent.append("</table></body></html>");
				MailSettingTo mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailSubject("逾期45天系统自动停开发票");
				mailSettingTo.setEmailContent(mailContent.toString());
				this.mailUtilService.sendMail(30,mailSettingTo);
			}
		}
	}
	
	private StringBuffer setCss() {
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
		mailContent.append("<font class='ff'>大家好:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下是逾期45天系统自动停开发票列表</font><br>");
		mailContent.append("<table class='grid_table'><tr><th>序号</th><th>合同号</th><th>客户名称</th><th>业务员</th><th>办事处</th>" +
				"<th>停开原因</th><th>停开时间</th><th>生效时间</th></tr>");
		return mailContent;
	}
	private void sendEmail(String company,List<Map<String,Object>> resultList,int mailType) {
		StringBuffer mailContent=this.setCss();
		boolean hasContent=false;
		int i=1;
		for(int j=0;j<resultList.size();j++) {
			if(resultList.get(j).get("DECP_ID")==null) {
				continue;
			}
			if(company.equals(resultList.get(j).get("DECP_ID").toString())) {
				mailContent.append("<tr><td style='text-align: center;'>"+i+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("LEASE_CODE")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("CUST_NAME")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("NAME")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("DECP_NAME_CN")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("STOP_REMARK")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("STOP_TIME")+"</td>" +
						"<td style='text-align: center;'>"+resultList.get(j).get("EFFECT_DATE")+"</td>" +
						"</tr>");
				hasContent=true;
				i++;
			}
		}
		mailContent.append("</table></body></html>");
		MailSettingTo mailSettingTo=new MailSettingTo();
		mailSettingTo.setEmailSubject("逾期45天系统自动停开发票");
		mailSettingTo.setEmailContent(mailContent.toString());
		try {
			if(hasContent) {
				this.mailUtilService.sendMail(mailType,mailSettingTo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void maintenanceSpecialInvoice(Context context) throws Exception {
		context.contextMap.put("OPERATE_FROM",InvoiceManagementUtil.OPERATE_FROM.AUTO_FUNCTION.toString());
		this.invoiceManagementDAO.updateSpecialCase(context);//首先把历史操作更新为-1
		this.invoiceManagementDAO.insertSpecialCase(context);//插入此次的操作
	}
	
	//系统自动跑出新案导出的开票资料,每月15号,25号或者31号晚上18:00跑
	@Transactional
	public void batchjobForGenerateInvoiceForNewCase() {
		
		Map<String,Object> param=new HashMap<String,Object>();
		boolean isRun=false;//是否跑job,每月15号跑和该结账月的结束时间跑
		String today=DateUtil.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd");
		ReportDateTo rdt=ReportDateUtil.getDateByDate(today);
		if("15".equals(today.split("-")[2])) {//每月15号跑
			isRun=true;
			param.put("payStartDate",rdt.getBeginTime());
			param.put("payEndDate",today);
		} else {
			if(rdt.getMonth()==12) {//12月的结账周期结束时间比较特别
				if("31".equals(today.split("-")[2])) {//该结账月的结束时间跑
					isRun=true;
					param.put("payStartDate",today.split("-")[0]+"-"+today.split("-")[1]+"-16");
					param.put("payEndDate",today);
				}
			} else {
				if("25".equals(today.split("-")[2])) {//该结账月的结束时间跑
					isRun=true;
					param.put("payStartDate",today.split("-")[0]+"-"+today.split("-")[1]+"-16");
					param.put("payEndDate",today);
				}
			}
		}
		if(isRun) {
			param.put("startDate",rdt.getBeginTime());
			param.put("endDate",rdt.getEndTime());
			
			Calendar cal=Calendar.getInstance();
			cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			param.put("lastDateOfMonth",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			
			List<Map<String,Object>> resultList=this.invoiceManagementDAO.getInvoiceListForNewCase(param);
			
			//List<Map<String,Object>> dataListI=new ArrayList<Map<String,Object>>();//营业税税费方案
			List<Map<String,Object>> dataListII=new ArrayList<Map<String,Object>>();//增值税税费方案
			List<Map<String,Object>> dataListIII=new ArrayList<Map<String,Object>>();//直租税费方案
			List<Map<String,Object>> dataListIV=new ArrayList<Map<String,Object>>();//乘用车委贷
			List<Map<String,Object>> dataListV=new ArrayList<Map<String,Object>>();//售后回租
			
			/** 增值税税费方案,直租税费方案,售后回租都要生成xml文件,xml文件用于自动导入税控系统开票 **/
			//处理数据
			for(int i=0;i<resultList.size();i++) {
				if(Constants.TAX_PLAN_CODE_1.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//营业税税费方案
					//do nothing,营业税的案子不可能再有新案
				} else if(Constants.TAX_PLAN_CODE_2.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//增值税税费方案
					Map<String,Object> dataMap=new HashMap<String,Object>();
					//单据号=公司别+流水号
					dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
					dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
					dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
					dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
					dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
					dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE"));
					dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
					dataMap.put("TAX_RATE",0.17);
					dataMap.put("INVOICE_TYPE",2);//增值税发票
					dataMap.put("XML_FILE_NAME","新案增值税税费方案("+today+")");
					dataMap.put("CASE_TYPE","新案");
					dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
					dataListII.add(dataMap);
					this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
				} else if(Constants.TAX_PLAN_CODE_4.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//直租税费方案
					Map<String,Object> dataMap=new HashMap<String,Object>();
					//单据号=公司别+流水号
					if("N".equals(resultList.get(i).get("FLAG").toString())) {//支付日在自然月最后天以后的,只会处理首付款
						dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
						dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
						dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
						dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
						dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
						dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("DEPOSIT_WHITOUT_TAX"));
						dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.DEPOSIT);
						dataMap.put("TAX_RATE",0.17);
						dataMap.put("INVOICE_TYPE",2);//增值税专用发票
						dataMap.put("XML_FILE_NAME","新案直接租赁税费方案("+today+")");
						dataMap.put("CASE_TYPE","新案");
						dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
						dataListIII.add(dataMap);
						this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
					} else {
						for(int j=0;j<3;j++) {//支付日在自然月最后天以前的,有3个金额,3成保证金,本金和利息,所以插入3条数据.其中租金和利息需要开在一张发票中,所以组装xml要处理
							dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
							dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
							dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
							dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
							dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
							if(j==0) {//组装保证金
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("DEPOSIT_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.DEPOSIT);
							} else if(j==1) {//组装本金
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("OWN_PRICE_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.CAPITAL);
							} else if(j==2) {//组装利息
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
							}
							dataMap.put("TAX_RATE",0.17);
							dataMap.put("INVOICE_TYPE",2);//增值税专用发票
							dataMap.put("XML_FILE_NAME","新案直接租赁税费方案("+today+")");
							dataMap.put("CASE_TYPE","新案");
							dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
							dataListIII.add(dataMap);
							this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
						}
					}
				} else if(Constants.TAX_PLAN_CODE_5.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//乘用车委贷
					Map<String,Object> dataMap=new HashMap<String,Object>();
					dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
					dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
					dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
					dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
					dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
					dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE"));
					dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
					dataMap.put("TAX_RATE",0.17);
					dataMap.put("INVOICE_TYPE",2);//增值税专用发票
					dataMap.put("XML_FILE_NAME","新案乘用车委贷税费方案("+today+")");
					dataMap.put("CASE_TYPE","新案");
					dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
					dataListIV.add(dataMap);
					this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
				} else if(Constants.TAX_PLAN_CODE_6.equals(resultList.get(i).get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_7.equals(resultList.get(i).get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_8.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//售后回租
					Map<String,Object> dataMap=new HashMap<String,Object>();
					if("21".equals(resultList.get(i).get("PAY_WAY").toString())||"23".equals(resultList.get(i).get("PAY_WAY").toString())) {//期末类型
						dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
						dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
						dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
						dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
						dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
						dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("DEPOSIT_WHITOUT_TAX"));
						dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.DEPOSIT);
						dataMap.put("TAX_RATE",0.17);
						dataMap.put("INVOICE_TYPE",1);//增值税普通发票
						dataMap.put("XML_FILE_NAME","新案售后回租税费方案("+today+")");
						dataMap.put("CASE_TYPE","新案");
						dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
						dataListV.add(dataMap);
						this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
					} else if("11".equals(resultList.get(i).get("PAY_WAY").toString())||"13".equals(resultList.get(i).get("PAY_WAY").toString())) {//期初类型
						for(int j=0;j<3;j++) {//售后回租期末类型有3个金额,3成保证金,本金和利息,所以插入3条数据.其中租金和利息需要开在一张发票中,所以组装xml要处理
							dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
							dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
							dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
							dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
							dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
							if(j==0) {//组装保证金
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("DEPOSIT_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.DEPOSIT);
								dataMap.put("INVOICE_TYPE",1);//增值税普通发票
							} else if(j==1) {//组装本金
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("OWN_PRICE_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.CAPITAL);
								dataMap.put("INVOICE_TYPE",1);//增值税普通发票
							} else if(j==2) {//组装利息
								dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE_WHITOUT_TAX"));
								dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
								if(Constants.TAX_PLAN_CODE_7.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//乘用车售后回租都是普票
									dataMap.put("INVOICE_TYPE",1);//增值税普通发票
								} else {
									dataMap.put("INVOICE_TYPE",2);//增值税专用发票
								}
							}
							dataMap.put("TAX_RATE",0.17);
							dataMap.put("XML_FILE_NAME","新案售后回租税费方案("+today+")");
							dataMap.put("CASE_TYPE","新案");
							dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
							dataListV.add(dataMap);
							this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
						}
					}
				}
			}
		}
	}
	
	//系统自动跑出旧案导出的开票资料,每月1号凌晨3点跑出当月的旧案开票资料
	public void batchjobForGenerateInvoiceForOldCase() {
		Map<String,Object> param=new HashMap<String,Object>();
		boolean isRun=false;//是否跑job,每月15号跑和该结账月的结束时间跑
		Calendar cal=Calendar.getInstance();
		String today=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		if("01".equals(today.split("-")[2])||"1".equals(today.split("-")[2])) {//每月1号跑
			isRun=true;
		}
		if(isRun) {
			ReportDateTo rdt=ReportDateUtil.getDateByDate(today);
			param.put("startDate",rdt.getBeginTime());
			cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			param.put("lastDateOfMonth",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			
			List<Map<String,Object>> resultList=this.invoiceManagementDAO.getInvoiceListForOldCase(param);

			List<Map<String,Object>> dataListII=new ArrayList<Map<String,Object>>();//增值税税费方案
			List<Map<String,Object>> dataListIII=new ArrayList<Map<String,Object>>();//直租税费方案
			List<Map<String,Object>> dataListIV=new ArrayList<Map<String,Object>>();//乘用车委贷
			List<Map<String,Object>> dataListV=new ArrayList<Map<String,Object>>();//售后回租
			
			/** 增值税税费方案,直租税费方案,售后回租都要生成xml文件,xml文件用于自动导入税控系统开票 **/
			//处理数据
			for(int i=0;i<resultList.size();i++) {
				if(Constants.TAX_PLAN_CODE_1.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//营业税税费方案
					//do nothing
				} else if(Constants.TAX_PLAN_CODE_2.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//增值税税费方案
					Map<String,Object> dataMap=new HashMap<String,Object>();
					//单据号=公司别+流水号
					dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
					dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
					dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
					dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
					dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
					dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE"));
					dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
					dataMap.put("TAX_RATE",0.17);
					dataMap.put("INVOICE_TYPE",2);//增值税发票
					dataMap.put("XML_FILE_NAME","旧案增值税税费方案("+today+")");
					dataMap.put("CASE_TYPE","旧案");
					dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
					dataListII.add(dataMap);
					this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
				} else if(Constants.TAX_PLAN_CODE_4.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//直租税费方案
					Map<String,Object> dataMap=new HashMap<String,Object>();
					//单据号=公司别+流水号
					for(int j=0;j<2;j++) {//直租旧案每期有2个金额,本金和利息,所以插入2条数据.其中租金和利息需要开在一张发票中,所以组装xml要处理
						dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
						dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
						dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
						dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
						dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
						if(j==0) {//组装本金
							dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("OWN_PRICE_WHITOUT_TAX"));
							dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.CAPITAL);
						} else if(j==1) {//组装利息
							dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE_WHITOUT_TAX"));
							dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
						}
						dataMap.put("TAX_RATE",0.17);
						dataMap.put("INVOICE_TYPE",2);//增值税专用发票
						dataMap.put("XML_FILE_NAME","旧案直接租赁税费方案("+today+")");
						dataMap.put("CASE_TYPE","旧案");
						dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
						dataListIII.add(dataMap);
						this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
					}
				} else if(Constants.TAX_PLAN_CODE_5.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//乘用车委贷
					Map<String,Object> dataMap=new HashMap<String,Object>();
					dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
					dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
					dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
					dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
					dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
					dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE"));
					dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
					dataMap.put("TAX_RATE",0.17);
					dataMap.put("INVOICE_TYPE",2);//增值税专用发票
					dataMap.put("XML_FILE_NAME","旧案乘用车委贷税费方案("+today+")");
					dataMap.put("CASE_TYPE","旧案");
					dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
					dataListIV.add(dataMap);
					this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
				} else if(Constants.TAX_PLAN_CODE_6.equals(resultList.get(i).get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_7.equals(resultList.get(i).get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_8.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//售后回租
					Map<String,Object> dataMap=new HashMap<String,Object>();
					for(int j=0;j<2;j++) {//售后回租旧案有2个金额,本金和利息,所以插入2条数据.其中租金和利息需要开在一张发票中,所以组装xml要处理
						dataMap.put("DOC_NUM",resultList.get(i).get("COMPANY_CODE").toString()+CodeRule.getInvoiceCode());
						dataMap.put("RECP_ID",resultList.get(i).get("RECP_ID"));
						dataMap.put("LEASE_CODE",resultList.get(i).get("LEASE_CODE"));
						dataMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
						dataMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
						if(j==0) {//组装本金
							dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("OWN_PRICE_WHITOUT_TAX"));
							dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.CAPITAL);
							dataMap.put("INVOICE_TYPE",1);//增值税普通发票
						} else if(j==1) {//组装利息
							dataMap.put("PRICE_WHITOUT_TAX",resultList.get(i).get("REN_PRICE_WHITOUT_TAX"));
							dataMap.put("PRICE_TYPE",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
							if(Constants.TAX_PLAN_CODE_7.equals(resultList.get(i).get("TAX_PLAN_CODE"))) {//乘用车售后回租都是普票
								dataMap.put("INVOICE_TYPE",1);//增值税普通发票
							} else {
								dataMap.put("INVOICE_TYPE",2);//增值税专用发票
							}
						}
						dataMap.put("TAX_RATE",0.17);
						dataMap.put("XML_FILE_NAME","旧案售后回租税费方案("+today+")");
						dataMap.put("CASE_TYPE","旧案");
						dataMap.put("FINANCE_DATE",rdt.getYear()+"-"+rdt.getMonth());
						dataListV.add(dataMap);
						this.invoiceManagementDAO.insertInvoiceInfo(dataMap);
					}
				}
			}
		}
	}
	public List<Map<String,Object>> getFinanceDateList() {
		return this.invoiceManagementDAO.getFinanceDateList();
	}
	
	public Map<String,Object> getInvoiceInfo(Map<String,Object> param) {
		return this.invoiceManagementDAO.getInvoiceInfo(param);
	}
	
	//导出开票资料
	public static List<Map<String,Object>> openInvoice(String cardFlag,String financeDate,String companyCode,String caseType,String content,String taxPlanCode,String employeeId) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		List<Map<String,Object>> invoiceList=null;
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		String productName="";
		String taxType="";
		
		param.put("financeDate",financeDate);
		param.put("companyCode",companyCode);
		param.put("content",content);
		param.put("result","-2");//未开票和开票失败类型
		param.put("orderBy","orderBy");
		try {
			if(InvoiceManagementUtil.CASE_TYPE.NEW.toString().equals(caseType)) {
				
				param.put("caseType","新案");
				if("0".equals(cardFlag)) {//直租税费方案
					taxType="直租税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					
					int j=1;
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						if(i!=0&&InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
						}
						if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之本金】");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							productName="融资租赁利息";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之利息】");
						} else {
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之保证金】");
						}
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME",productName);
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						if((j+"").length()==1) {
							dataMap.put("REMARK2","000"+j);
						} else if((j+"").length()==2) {
							dataMap.put("REMARK2","00"+j);
						} else if((j+"").length()==3) {
							dataMap.put("REMARK2","0"+j);
						}
						if(InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","保证金");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","利息");
						} else if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","首租");
						}
						resultList.add(dataMap);
					}
				} else if("1".equals(cardFlag)) {//增值税税费方案
					taxType="增值税税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_2});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME","租赁费");
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1");
						resultList.add(dataMap);
					}
				} else if("2".equals(cardFlag)) {//乘用车委贷税费方案
					taxType="乘用车委贷税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_5});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					resultList=invoiceList;
				} else if("3".equals(cardFlag)) {//售后回租税费方案
					taxType="售后回租税费方案";
					if("0".equals(taxPlanCode)) {//选择全部
						param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_6,Constants.TAX_PLAN_CODE_7,Constants.TAX_PLAN_CODE_8});
					} else {
						param.put("taxPlanCode",new String [] {taxPlanCode});
					}
					
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					
					int j=1;
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						if(i!=0&&InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
						}
						if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之本金】");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							productName="融资租赁利息";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之利息】");
						} else {
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-1 【本融资租赁本金即属设备采购之保证金】");
						}
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME",productName);
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						if((j+"").length()==1) {
							dataMap.put("REMARK2","000"+j);
						} else if((j+"").length()==2) {
							dataMap.put("REMARK2","00"+j);
						} else if((j+"").length()==3) {
							dataMap.put("REMARK2","0"+j);
						}
						if(InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","保证金");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","利息");
						} else if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","首租");
						}
						resultList.add(dataMap);
					}
				}
			} else if(InvoiceManagementUtil.CASE_TYPE.OLD.toString().equals(caseType)) {
				param.put("caseType","旧案");
				if("0".equals(cardFlag)) {//直租税费方案
					taxType="直租税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					
					int j=1;
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						if(i!=0&&InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
						}
						if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之本金】");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							productName="融资租赁利息";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之利息】");
						} else {
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之保证金】");
						}
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME",productName);
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						if((j+"").length()==1) {
							dataMap.put("REMARK2","000"+j);
						} else if((j+"").length()==2) {
							dataMap.put("REMARK2","00"+j);
						} else if((j+"").length()==3) {
							dataMap.put("REMARK2","0"+j);
						}
						if(InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","保证金");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","利息");
						} else if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","本金");
						}
						resultList.add(dataMap);
					}
				} else if("1".equals(cardFlag)) {//增值税税费方案
					taxType="增值税税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_2});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME","租赁费");
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM"));
						resultList.add(dataMap);
					}
				} else if("2".equals(cardFlag)) {//乘用车委贷税费方案
					taxType="乘用车委贷税费方案";
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_5});
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					resultList=invoiceList;
				} else if("3".equals(cardFlag)) {//售后回租税费方案
					taxType="售后回租税费方案";
					if("0".equals(taxPlanCode)) {//选择全部
						param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_6,Constants.TAX_PLAN_CODE_7,Constants.TAX_PLAN_CODE_8});
					} else {
						param.put("taxPlanCode",new String [] {taxPlanCode});
					}
					
					invoiceList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getInvoiceList",param,RS_TYPE.LIST);
					
					int j=1;
					for(int i=0;invoiceList!=null&&i<invoiceList.size();i++) {
						Map<String,Object> result=(Map<String,Object>)DataAccessor.query("invoiceManagement.getInvoiceInfo",invoiceList.get(i),RS_TYPE.OBJECT);//获得开票地址等信息
						if(result==null) {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS","");
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO","");
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER","");
						} else {
							((Map<String,Object>)invoiceList.get(i)).put("ADDRESS",result.get("ADDRESS"));
							((Map<String,Object>)invoiceList.get(i)).put("BANK_INFO",result.get("BANK_INFO"));
							((Map<String,Object>)invoiceList.get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
						}
						Map<String,Object> dataMap=new HashMap<String,Object>();
						if(i!=0&&InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
						}
						if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							j++;
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之本金】");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							productName="融资租赁利息";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之利息】");
						} else {
							productName="融资租赁本金";
							dataMap.put("REMARK1",invoiceList.get(i).get("LEASE_CODE")+"-"+invoiceList.get(i).get("PERIOD_NUM")+" 【本融资租赁本金即属设备采购之保证金】");
						}
						dataMap.put("RECD_ID",invoiceList.get(i).get("PERIOD_NUM"));
						dataMap.put("RECP_ID",invoiceList.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",invoiceList.get(i).get("DOC_NUM"));//单据号
						dataMap.put("LINK_ADDRESS",invoiceList.get(i).get("ADDRESS"));
						dataMap.put("BANK_NAME",invoiceList.get(i).get("BANK_INFO"));
						dataMap.put("CORP_TAX_CODE",invoiceList.get(i).get("TAX_REGISTRATION_NUMBER"));
						dataMap.put("CUST_NAME",invoiceList.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_NAME",productName);
						dataMap.put("TAX_RATE",invoiceList.get(i).get("TAX_RATE"));
						dataMap.put("PRICE",invoiceList.get(i).get("PRICE_WHITOUT_TAX"));
						if((j+"").length()==1) {
							dataMap.put("REMARK2","000"+j);
						} else if((j+"").length()==2) {
							dataMap.put("REMARK2","00"+j);
						} else if((j+"").length()==3) {
							dataMap.put("REMARK2","0"+j);
						}
						if(InvoiceManagementUtil.PRICE_TYPE.DEPOSIT.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","保证金");
						} else if(InvoiceManagementUtil.PRICE_TYPE.INTEREST.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","利息");
						} else if(InvoiceManagementUtil.PRICE_TYPE.CAPITAL.toString().equals(invoiceList.get(i).get("PRICE_TYPE"))) {
							dataMap.put("REMARK3","本金");
						}
						resultList.add(dataMap);
					}
				}
			}
			
			//插入日志
			StringBuffer logContent=new StringBuffer();
			String cmpy=String.valueOf(Constants.COMPANY_CODE).equals(companyCode)?"裕融":"裕国";
			logContent.append("【结账周期："+financeDate+"】【公司别："+cmpy+"】【新-旧案："+param.get("caseType")+"】【开票情况：未开票和开票失败】【租金缴款:全部】【查询内容："+content+"】");
			
			param.put("logTitle","导出"+taxType+"开票资料");
			param.put("logContent",logContent.toString());
			param.put("fileSize",resultList.size());
			param.put("s_employeeId",employeeId);
			DataAccessor.execute("invoiceManagement.insertLog",param,OPERATION_TYPE.INSERT);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public static List<Map<String,Object>> postAddress(String cardFlag,String financeDate,String companyCode,String caseType,String content,String taxPlanCode) {
		
		List<Map<String,Object>> resultList=null;
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("financeDate",financeDate);
		param.put("companyCode",companyCode);
		param.put("content",content);
		try {
			if(InvoiceManagementUtil.CASE_TYPE.NEW.toString().equals(caseType)) {
				param.put("caseType","新案");
				if("0".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
				} else if("1".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_2});
				} else if("2".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_5});
				} else if("3".equals(cardFlag)) {
					if("0".equals(taxPlanCode)) {//选择全部
						param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_6,Constants.TAX_PLAN_CODE_7,Constants.TAX_PLAN_CODE_8});
					} else {
						param.put("taxPlanCode",new String [] {taxPlanCode});
					}
				}
			} else if(InvoiceManagementUtil.CASE_TYPE.OLD.toString().equals(caseType)) {
				param.put("caseType","旧案");
				if("0".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
				} else if("1".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_2});
				} else if("2".equals(cardFlag)) {
					param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_5});
				} else if("3".equals(cardFlag)) {
					if("0".equals(taxPlanCode)) {//选择全部
						param.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_6,Constants.TAX_PLAN_CODE_7,Constants.TAX_PLAN_CODE_8});
					} else {
						param.put("taxPlanCode",new String [] {taxPlanCode});
					}
				}
			}
			
			resultList=(List<Map<String,Object>>)DataAccessor.query("invoiceManagement.getPostAddress",param,RS_TYPE.LIST);
			
			//获得寄件信息
			//--------------------------------------------------------------------------------------------------------------------------
			param.put("dataType","邮寄资料快递信息");
			List<Map<String,Object>> postManInfo=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,RS_TYPE.LIST);//寄件信息
			String postName="";
			String postCompany="";
			String postPhone="";
			String postAddress="";
			for(int i=0;postManInfo!=null&&i<postManInfo.size();i++) {
				if("寄件人".equals(postManInfo.get(i).get("CODE"))) {
					postName=postManInfo.get(i).get("FLAG")+"";
				} else if("寄件公司".equals(postManInfo.get(i).get("CODE"))) {
					postCompany=postManInfo.get(i).get("FLAG")+"";
				} else if("寄件人联系电话".equals(postManInfo.get(i).get("CODE"))) {
					postPhone=postManInfo.get(i).get("FLAG")+"";
				} else if("寄件人地址".equals(postManInfo.get(i).get("CODE"))) {
					postAddress=postManInfo.get(i).get("FLAG")+"";
				}
			}
			//--------------------------------------------------------------------------------------------------------------------------
			
			//获得快递公司信息
			//--------------------------------------------------------------------------------------------------------------------------
			param.put("dataType","快递公司");
			List<Map<String,Object>> postCompanyInfo=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,RS_TYPE.LIST);//快递公司
			Map<String,Object> postComapanyMap=new HashMap<String,Object>();
			for(int i=0;postCompanyInfo!=null&&i<postCompanyInfo.size();i++) {
				postComapanyMap.put(postCompanyInfo.get(i).get("FLAG")+"",postCompanyInfo.get(i).get("FLAG"));
			}
			//--------------------------------------------------------------------------------------------------------------------------
			
			//获得办事处对应邮寄方式
			//--------------------------------------------------------------------------------------------------------------------------
			param.put("dataType","快递-办事处");
			List<Map<String,Object>> companyInfo=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,RS_TYPE.LIST);//办事处对应快递公司
			Map<String,Object> comapanyMap=new HashMap<String,Object>();
			for(int i=0;companyInfo!=null&&i<companyInfo.size();i++) {
				comapanyMap.put(companyInfo.get(i).get("CODE")+"",companyInfo.get(i).get("SHORTNAME"));
			}
			//--------------------------------------------------------------------------------------------------------------------------
			
			//获得快递付款方式
			//--------------------------------------------------------------------------------------------------------------------------
			param.put("dataType","快递付款方式");
			List<Map<String,Object>> postPayWayInfo=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,RS_TYPE.LIST);//快递付款方式
			Map<String,Object> postPayWayMap=new HashMap<String,Object>();
			for(int i=0;postPayWayInfo!=null&&i<postPayWayInfo.size();i++) {
				postPayWayMap.put(postPayWayInfo.get(i).get("CODE")+"",postPayWayInfo.get(i).get("FLAG"));
			}
			//--------------------------------------------------------------------------------------------------------------------------
			
			for(int i=0;resultList!=null&&i<resultList.size();i++) {
				resultList.get(i).put("POST_NAME",postName);
				resultList.get(i).put("POST_COMPANY",postCompany);
				resultList.get(i).put("POST_PHONE",postPhone);
				resultList.get(i).put("POST_ADDRESS",postAddress);
				resultList.get(i).put("POST_WAY",comapanyMap.get(resultList.get(i).get("DECP_ID")+""));
				resultList.get(i).put("EXPRESS_PAY_WAY_DESCR",resultList.get(i).get("EXPRESS_PAY_WAY")==null?postPayWayMap.get("2"):postPayWayMap.get(resultList.get(i).get("EXPRESS_PAY_WAY")+""));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public List<Map<String,Object>> buildExternalInvoiceListFromWorkbook(Workbook workbook) throws Exception {
		
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		
		if(workbook!=null) {
			//表格
			Sheet sheet=workbook.getSheetAt(0);//只拿sheet1
			Iterator<Row> rit=sheet.rowIterator();
			rit.next();
			rit.next();
			for(;rit.hasNext();) {
				Map<String,Object> resultMap=new HashMap<String,Object>();
				Row row=(Row)rit.next();//得到行
				Iterator<Cell> cit=row.cellIterator();//遍历单元格
				for(;cit.hasNext();) {
					Cell cell=(Cell)cit.next();
					switch(cell.getColumnIndex()) {
					case 0:
						if(cell.getCellType()!=1) {
							break;
						}
						resultMap.put("leaseCode",cell.getStringCellValue());
						break;
					case 2:
						if(cell.getCellType()!=1) {
							break;
						}
						resultMap.put("periodNum",cell.getStringCellValue());
						break;
					case 6:
						if(cell.getCellType()!=1) {
							break;
						}
						resultMap.put("invoiceNum",cell.getStringCellValue());
						break;
					}
				}
				resultMap.put("priceType",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
				if(resultMap.get("leaseCode")==null||resultMap.get("invoiceNum")==null||resultMap.get("periodNum")==null) {
					continue;
				}
				resultList.add(resultMap);
			}
		}
		return resultList;
	}

	public List<Map<String,Object>> buildInternalInvoiceListFromWorkbook(Workbook workbook) throws Exception {
		
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		
		if(workbook!=null) {
			//表格
			Sheet sheet=workbook.getSheetAt(0);//只拿sheet1
			Iterator<Row> rit=sheet.rowIterator();
			rit.next();
			for(;rit.hasNext();) {
				Map<String,Object> resultMap=new HashMap<String,Object>();
				Row row=(Row)rit.next();//得到行
				Iterator<Cell> cit=row.cellIterator();//遍历单元格
				for(;cit.hasNext();) {
					Cell cell=(Cell)cit.next();
					switch(cell.getColumnIndex()) {
					case 2:
						if(cell.getCellType()!=1) {
							break;
						}
						resultMap.put("invoiceNum",cell.getStringCellValue());
						break;
					case 5:
						if(cell.getCellType()!=1) {
							break;
						}
						resultMap.put("type",cell.getStringCellValue());
						break;
					case 13:
						if(cell.getCellType()!=1) {
							break;
						}
						String info[]=cell.getStringCellValue().substring(0,cell.getStringCellValue().length()).trim().split("【")[0].split("-");
						if(info.length<2) {
							break;
						}
						String descr[]=cell.getStringCellValue().substring(0,cell.getStringCellValue().length()).trim().split("【");
						String priceType="";
						if(descr.length==2) {//直租和售后回租
							priceType=descr[1].split("】")[0];
						} else {
							priceType="租赁费";//非直租和售后回租
						}
						resultMap.put("leaseCode",info[0]);
						resultMap.put("periodNum",info[1]);
						resultMap.put("descr",descr);
						if("本融资租赁本金即属设备采购之本金".equals(priceType)) {
							resultMap.put("priceType",InvoiceManagementUtil.PRICE_TYPE.CAPITAL);
						} else if("本融资租赁本金即属设备采购之利息".equals(priceType)) {
							resultMap.put("priceType",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
						} else if("本融资租赁本金即属设备采购之保证金".equals(priceType)) {
							resultMap.put("priceType",InvoiceManagementUtil.PRICE_TYPE.DEPOSIT);
						} else {
							resultMap.put("priceType",InvoiceManagementUtil.PRICE_TYPE.INTEREST);
						}
						break;
					}
				}
				if("融资租赁利息".equals(resultMap.get("type"))||"融资租赁本金".equals(resultMap.get("type"))||"租赁费".equals(resultMap.get("type"))) {
					resultList.add(resultMap);
				}
			}
		}
		return resultList;
	}
	
	public void insertUploadLog(Map<String,Object> param) {
		this.invoiceManagementDAO.insertUploadLog(param);
	}
	
	public void uploadInvoiceNum(Map<String,Object> param) {
		this.invoiceManagementDAO.uploadInvoiceNum(param);
	}
	
	public Map<String,Object> getResult(Context context) {
		return this.invoiceManagementDAO.getResult(context);
	}
}
