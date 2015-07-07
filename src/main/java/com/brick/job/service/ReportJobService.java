package com.brick.job.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.birtReport.service.BirtReportService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.util.DateUtil;

public class ReportJobService extends BaseService {
	
	private BirtReportEngine birt;
	private MailUtilService mailUtilService;
	
	public BirtReportEngine getBirt() {
		return birt;
	}

	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	/**
	 * 客户归户Job
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doReportForCustPerformance() throws Exception{
		List<SelectionTo> allCust = getAllCust();
		Map<String, Object> data = null;
		List<String> creditList = null;
		String custId = null;
		String custName = null;
		double totalRentMoney = 0;
		double remainingRentMoney = 0;
		double totalPrincipal = 0;
		double remainingPrincipal = 0;
		for (SelectionTo cust : allCust) {
			custId = cust.getOption_value();
			custName = cust.getDisplay_name();
			data = new HashMap<String, Object>();
			data.put("custId", custId);
			data.put("custName", custName);
			creditList = LeaseUtil.getCreditIdByCustId(custId);
			data.put("pro_count", creditList.size());
			totalRentMoney = 0;
			remainingRentMoney = 0;
			totalPrincipal = 0;
			remainingPrincipal = 0;
			for (String creditId : creditList) {
				totalRentMoney += LeaseUtil.getTotalRental(creditId);
				remainingRentMoney += LeaseUtil.getRemainingRental(creditId);
				totalPrincipal += LeaseUtil.getTotalPriceByCreditId(creditId);
				remainingPrincipal += LeaseUtil.getRemainingPrincipalByCreditId(creditId);
			}
			data.put("remainingPrincipal", remainingPrincipal);
			data.put("remainingRentMoney", remainingRentMoney);
			data.put("totalPrincipal", totalPrincipal);
			data.put("totalRentMoney", totalRentMoney);
			insert("report.insertReportForCustPerformance", data);
		}
	}
	
	/**
	 * 担保人归户
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doReportForGuarantorPerformance() throws Exception{
		//自然人
		List<SelectionTo> allGuarantor = (List<SelectionTo>) queryForList("report.getAllGuarantor");
		Map<String, Object> data = null;
		List<String> creditList = null;
		double totalRentMoney = 0;
		double remainingRentMoney = 0;
		double totalPrincipal = 0;
		double remainingPrincipal = 0;
		for (SelectionTo guarantor : allGuarantor) {
			data = new HashMap<String, Object>();
			data.put("guarantor", guarantor.getDisplay_name());
			creditList = LeaseUtil.getCreditIdByGuarantor(guarantor.getDisplay_name());
			data.put("pro_count", creditList.size());
			totalRentMoney = 0;
			remainingRentMoney = 0;
			totalPrincipal = 0;
			remainingPrincipal = 0;
			for (String creditId : creditList) {
				totalRentMoney += LeaseUtil.getTotalRental(creditId);
				remainingRentMoney += LeaseUtil.getRemainingRental(creditId);
				totalPrincipal += LeaseUtil.getTotalPriceByCreditId(creditId);
				remainingPrincipal += LeaseUtil.getRemainingPrincipalByCreditId(creditId);
			}
			data.put("remainingPrincipal", remainingPrincipal);
			data.put("remainingRentMoney", remainingRentMoney);
			data.put("totalPrincipal", totalPrincipal);
			data.put("totalRentMoney", totalRentMoney);
			insert("report.insertReportForGuarantorPerformance", data);
		}
	}
	
	
	/**
	 * 身份证上传不正确统计
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doCustIDCardStatistics() throws Exception{
		Date date = new Date();
		String dateStr = DateUtil.dateToStr(date);
		ReportDateTo reportDate = ReportDateUtil.getDateByDate(dateStr);
		if (dayDiff(reportDate.getBeginTime(), date) != 1) {
			//财务月的第二天运行
			System.out.println("财务月的第二天运行");
			return;
		}
		int year = 0; int month = 0;
		if (reportDate.getMonth() == 1) {
			month = 12;
			year = reportDate.getYear() - 1;
		} else {
			month = reportDate.getMonth();
			year = reportDate.getYear();
		}
		reportDate = ReportDateUtil.getDateByYearAndMonth(year, month);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start_date", reportDate.getBeginTime());
		paramMap.put("end_date", reportDate.getEndTime());
		List<Map<String, Object>> allData = (List<Map<String, Object>>) queryForList("report.doCustIDCardStatistics", paramMap);
		paramMap.put("type", "1");
		List<Map<String, Object>> targetData = (List<Map<String, Object>>) queryForList("report.doCustIDCardStatistics", paramMap);
		
		Map<String, Integer> targetMap = new HashMap<String, Integer>();
		for (Map<String, Object> map : targetData) {
			targetMap.put(String.valueOf(map.get("UID")), (Integer)map.get("C"));
		}
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> decpData = null;
		List<Map<String, Object>> dataList = null;
		Map<String, Object> uData = null;
		Object o = null;
		for (Map<String, Object> map : allData) {
			o = dataMap.get(String.valueOf(map.get("DCID")));
			if (o == null) {
				decpData = new HashMap<String, Object>();
			} else {
				decpData = (Map<String, Object>) o;
			}
			decpData.put("decpName", map.get("DCNAME"));
			decpData.put("date", year + "-" + month);
			dataList = (List<Map<String, Object>>) decpData.get("dataList");
			if (dataList == null) {
				dataList = new ArrayList<Map<String,Object>>();
			}
			uData = new HashMap<String, Object>();
			uData.put("uName", map.get("UNAME"));
			uData.put("uId", map.get("UID"));
			uData.put("allCount", map.get("C"));
			uData.put("targetCount", targetMap.get(String.valueOf(map.get("UID"))) == null ? 0 : targetMap.get(String.valueOf(map.get("UID"))).intValue());
			dataList.add(uData);
			decpData.put("dataList", dataList);
			dataMap.put(String.valueOf(map.get("DCID")), decpData);
		}
		
		List<SelectionTo> offices = getAllOffice();
		String decp_id = null;
		String mailContent = null;
		MailSettingTo mailSettingTo = null;
		StringBuffer mailTo = new StringBuffer();
		for (SelectionTo selectionTo : offices) {
			decp_id = selectionTo.getOption_value();
			decpData = (Map<String, Object>)dataMap.get(decp_id);
			if (decpData == null || decpData.get("dataList") == null || ((List<Map<String, Object>>) decpData.get("dataList")).size() == 0) {
				continue;
			}
			mailContent = writeDataTableForCustIDCardStatistics(decpData);
			mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailSubject("身份证上传不正确统计-" + ((Map<String, Object>)dataMap.get(decp_id)).get("decpName"));
			mailSettingTo.setEmailContent(mailContent);
			for (Map<String, Object> map : (List<Map<String, Object>>) decpData.get("dataList")) {
				mailTo.append(LeaseUtil.getEmailByUserId(String.valueOf(map.get("uId"))));
				mailTo.append(";");
			}
			mailSettingTo.setEmailTo(mailTo.substring(0, mailTo.length() - 1));
			mailSettingTo.setEmailCc(
				LeaseUtil.getEmailByUserId(
					LeaseUtil.getUpUserByUserId(
						String.valueOf(((List<Map<String, Object>>) decpData.get("dataList")).get(0).get("uId"))
					)
				) + ";yangyun@tacleasing.cn"
			);
			mailUtilService.sendMail(mailSettingTo);
		}
		
		mailSettingTo = new MailSettingTo();
		mailContent = writeDataTableForCustIDCardStatisticsAll(dataMap);
		mailSettingTo.setEmailContent(mailContent);
		mailUtilService.sendMail(160, mailSettingTo);
		
	}
	
	private String writeDataTableForCustIDCardStatisticsAll(Map<String, Object> dataMap){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> data = null;
		for (Iterator iterator = dataMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			data = (Map<String, Object>) dataMap.get(key);
			list.add(data);
		}
		StringBuffer sb = new StringBuffer("<html>");
		sb.append("<style type=\"text/css\">");
		sb.append(".panel_table {");
		sb.append("	width : 100%;");
		sb.append("	border-collapse:collapse;");
		sb.append("	border:solid #A6C9E2; ");
		sb.append("	border-width:1px 0 0 1px;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table th {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append("	background-color: #E1EFFB;");
		sb.append("	padding : 2;");
		sb.append("	margin : 1;");
		sb.append("	font-weight: bold;");
		sb.append("	text-align: center;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	color: #2E6E9E;");
		sb.append("	height: 28px;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table th *{");
		sb.append("	text-align: center;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table tr{");
		sb.append("	cursor: default;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table td {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append(" text-align: left;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	overflow: hidden;");
		sb.append("	background-color: #FFFFFF;");
		sb.append("	padding : 5px 5px;");
		sb.append("	font-size: 12px;");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append(".panel_table td *{");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	text-align: left;");
		sb.append("	font-size: 12px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append("</style>");
		
		sb.append("<table class=\"panel_table\">");
		sb.append("<tr>");
		sb.append("<th>" + list.get(0).get("date") + "</th>");
		sb.append("<th>业务员</th>");
		sb.append("<th>文审过案件数</th>");
		sb.append("<th>身份证上传不正确件数</th>");
		sb.append("<th>比例</th>");
		sb.append("</tr>");
		
		for (Map<String, Object> decpData : list) {
			sb.append(writeDataList((List<Map<String, Object>>)decpData.get("dataList"), String.valueOf(decpData.get("decpName"))));
		}
		sb.append("</table>");
		
		sb.append("</html>");
		return sb.toString();
	}
	
	private String writeDataTableForCustIDCardStatistics(Map<String, Object> decpData){
		List<Map<String, Object>> dataList = null;
		dataList = (List<Map<String, Object>>) decpData.get("dataList");
		StringBuffer sb = new StringBuffer("<html>");
		sb.append("<style type=\"text/css\">");
		sb.append(".panel_table {");
		sb.append("	width : 100%;");
		sb.append("	border-collapse:collapse;");
		sb.append("	border:solid #A6C9E2; ");
		sb.append("	border-width:1px 0 0 1px;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table th {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append("	background-color: #E1EFFB;");
		sb.append("	padding : 2;");
		sb.append("	margin : 1;");
		sb.append("	font-weight: bold;");
		sb.append("	text-align: center;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	color: #2E6E9E;");
		sb.append("	height: 28px;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table th *{");
		sb.append("	text-align: center;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table tr{");
		sb.append("	cursor: default;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table td {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append(" text-align: left;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	overflow: hidden;");
		sb.append("	background-color: #FFFFFF;");
		sb.append("	padding : 5px 5px;");
		sb.append("	font-size: 12px;");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append(".panel_table td *{");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	text-align: left;");
		sb.append("	font-size: 12px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append("</style>");
		
		sb.append("<table class=\"panel_table\">");
		sb.append("<tr>");
		sb.append("<th>" + decpData.get("date") + "</th>");
		sb.append("<th>业务员</th>");
		sb.append("<th>文审过案件数</th>");
		sb.append("<th>身份证上传不正确件数</th>");
		sb.append("<th>比例</th>");
		sb.append("</tr>");
		
		sb.append(writeDataList(dataList, String.valueOf(decpData.get("decpName"))));
		
		sb.append("</table>");
		
		sb.append("</html>");
		return sb.toString();
	}
	
	private String writeDataList(List<Map<String, Object>> dataList, String decpName){
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (Map<String, Object> map : dataList) {
			sb.append("<tr>");
			if (i == 0) {
				sb.append("<td rowspan=\"" + dataList.size() + "\">" + decpName + "</td>");
			}
			sb.append("<td>" + map.get("uName") + "</td>");
			sb.append("<td>" + map.get("allCount") + "</td>");
			sb.append("<td>" + map.get("targetCount") + "</td>");
			sb.append("<td>" + ((Integer)map.get("targetCount")/(Integer)map.get("allCount") * 100) + "%</td>");
			sb.append("</tr>");
			i ++;
		}
		return sb.toString();
	}
	
	
}
