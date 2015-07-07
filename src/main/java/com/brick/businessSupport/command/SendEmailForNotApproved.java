package com.brick.businessSupport.command;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.credit.to.CreditTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.DataAccessor;
import com.brick.util.NumberUtils;
import com.brick.util.StringUtils;

public class SendEmailForNotApproved extends BaseService {
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void doService() throws Exception{
		System.out.println("====================================异常案件发送Email,Job.start=======================================");
		try {
			if (!super.isWorkingDay()) {
				System.out.println("====================================异常案件发送Email,Job.end.非工作日不发送.=======================================");
				return;
			}
			//全部
			doSendEmail(null, 100);
			
			//苏州设备和重车
			doSendEmail("16,17", 110);
			
			//昆山
			doSendEmail("2", 111);
			
			//上海设备和重车
			doSendEmail("13,15", 112);
			
			//南京
			doSendEmail("7", 113);
			
			//重庆
			doSendEmail("9", 114);
			
			//成都
			doSendEmail("14", 115);
			
			//东莞
			doSendEmail("3", 116);
			
			//佛山
			doSendEmail("8", 117);
			
			//厦门
			doSendEmail("11", 118);
			
			List list = DictionaryUtil.getDictionary("异常案件");//后续的只需在字典表中配置
			if(list!=null){
				for(int i=0;i<list.size();i++){
					Map deptInfo = (Map) list.get(i);
					String decpId = (String) deptInfo.get("FLAG");
					String emailType = (String) deptInfo.get("CODE");			
					doSendEmail(decpId, Integer.parseInt(emailType));
				}
			}
			
			
			System.out.println("====================================异常案件发送Email,Job.end.success=======================================");
		} catch (Exception e) {
			System.out.println("====================================异常案件发送Email,Job.end.failed=======================================");
			throw e;
		}
	}
	
	private void doSendEmail(String decp_id, int m_id) throws Exception{
		List<CreditTo> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int index = 0;
		try {
			paramMap.put("decp_id", decp_id);
			StringBuffer sb = new StringBuffer("<html>" +
					"<head><style type=\"text/css\">" +
					"#dataBody td {border: 1px solid #A6C9E2;} " +
					"#dataBody th {border: 1px solid white;background-color: #A6C9E2;} " +
					"#dataBody table {background-color: white;border: 1px solid #A6C9E2;}" +
					"</style></head>" +
					"<body><div id='dataBody'>");
			paramMap.put("order", " order by tt.min_commit_date ");
			resultList = (List<CreditTo>) super.queryForList("businessSupport.getAllNotApproved", paramMap);
			sb.append("审核中超过3天案件列表：共计：" + (resultList == null ? 0 : resultList.size()) + "件。");
			if (resultList.size() > 0) {
				sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
				sb.append("<tr>");
				sb.append("<th>序号</th>");
				sb.append("<th>案件号</th>");
				sb.append("<th>客户名称</th>");
				sb.append("<th>办事处</th>");
				sb.append("<th>区域主管</th>");
				sb.append("<th>客户经理</th>");
				sb.append("<th>最早提交风控时间</th>");
				sb.append("<th>距今天数</th>");
				sb.append("<th>融资额</th>");
				sb.append("<th>有无退回记录</th>");
				sb.append("<th>案件状态</th>");
				sb.append("<th>备注</th>");
				sb.append("</tr>");
				for (CreditTo creditTo : resultList) {
					index ++;
					sb.append("<tr>");
					sb.append("<td>" + index + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getCreditRuncode() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getCustName() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getDecpName() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getUpperUser() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getSensorUser() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getMinCommitDate() + "&nbsp;</td>");
					sb.append("<td>" + String.valueOf(creditTo.getDayDiff()) + "&nbsp;</td>");
					sb.append("<td>" + NumberUtils.getCurrencyFormat((creditTo.getLeaseRze() == null ? "0" : creditTo.getLeaseRze()), Locale.CHINA) + "&nbsp;</td>");
					sb.append("<td>" + (creditTo.getArg1() > 0 ? "有" : "无") + "&nbsp;</td>");
					sb.append("<td>" + (creditTo.getState() == 1 ? "评审中" : "业务处理中") + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getMemo() + "&nbsp;</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
			sb.append("<br/>");
			sb.append("<br/>");
			
			//
			paramMap.put("order", "order by vi.MODIFY_DATE");
			resultList = (List<CreditTo>) super.queryForList("businessSupport.getAllNotCommit", paramMap);
			sb.append("访厂后超过5天未提交案件列表：共计：" + (resultList == null ? 0 : resultList.size()) + "件。");
			if (resultList.size() > 0) {
				sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" style=\"width: 1200px;\">");
				sb.append("<tr>");
				sb.append("<th style=\"width:1%;\">序号</th>");
				sb.append("<th style=\"width:8%;\">案件号</th>");
				sb.append("<th style=\"width:22%\">客户名称</th>");
				sb.append("<th style=\"width:9%\">办事处</th>");
				sb.append("<th style=\"width:9%\">区域主管</th>");
				sb.append("<th style=\"width:9%\">客户经理</th>");
				sb.append("<th style=\"width:10%\">实际访厂日</th>");
				sb.append("<th style=\"width:10%\">访厂报告提交日</th>");
				sb.append("<th>距今天数</th>");
				sb.append("<th style=\"width:10%\">融资额</th>");
				sb.append("<th style=\"width:32%\">增提资料</th>");
				sb.append("<th>备注</th>");
				sb.append("</tr>");
				index = 0;
				for (CreditTo creditTo : resultList) {
					index ++;
					sb.append("<tr>");
					sb.append("<td>" + index + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getCreditRuncode() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getCustName() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getDecpName() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getUpperUser() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getSensorUser() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getRealVisitDate() + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getModify_date_str() + "&nbsp;</td>");
					sb.append("<td>" + String.valueOf(creditTo.getDayDiff()) + "&nbsp;</td>");
					sb.append("<td>" + NumberUtils.getCurrencyFormat((creditTo.getLeaseRze() == null ? "0" : creditTo.getLeaseRze()), Locale.CHINA) + "&nbsp;</td>");
					sb.append("<td>" + (StringUtils.isEmpty(creditTo.getAddedInfo()) ? 
							"无" : creditTo.getAddedInfo().replace("\n", "<br/>")) + "&nbsp;</td>");
					sb.append("<td>" + creditTo.getMemo() + "&nbsp;</td>");
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
			sb.append("</div></body></html>");
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent(sb.toString().replace("null", ""));
			mailUtilService.sendMail(m_id, mailSettingTo);
		} catch (Exception e) {
			throw e;
		}
	}
}
