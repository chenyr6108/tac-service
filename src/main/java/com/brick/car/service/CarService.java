package com.brick.car.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.collection.service.StartPayService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.unnaturalCase.to.UnnaturalCaseTO;
import com.brick.util.DataUtil;


public class CarService extends BaseService{
	static Log logger = LogFactory.getLog(CarService.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public void checkRentFileAfterPayMoney() throws Exception{
	
		List<Map> list = (List<Map>) this.queryForList("creditReportManage.getCarCreditProjectAfterPayMoney");
		
		if(list!=null && list.size()>0){
			String fileNames = "('个人委托贷款借款合同','设定抵押合同','保证合同','共同还款承诺函','贷款用途申明','委托扣款授权书','提款申请书','委托书','放款通知书')";
			for(Map map:list){	
				String creditId = String.valueOf(map.get("ID")) ;
				Map params  = new HashMap();
				params.put("cardFlag", 2);
				params.put("CONTRACT_TYPE", 9);
				params.put("prcd_id", creditId);
				params.put("credit_id", creditId);
				params.put("fileNames", fileNames);
				
				List<Map> files = (List<Map>) this.queryForList("rentFile.selectRentFile",params);
				boolean isNeedSendEmail = false;
				
				List<UnnaturalCaseTO> fileList = (List<UnnaturalCaseTO>) this.queryForList("unnaturalCase.getUnnaturalUncompletedFileCase",params);
				if(fileList!=null && fileList.size()>0){
					isNeedSendEmail = true;
				}
			
				String content = sendUnCompletedFile(fileList);



				if(isNeedSendEmail){
					StringBuffer emails = new StringBuffer("");
					//审查专员邮件
					List<Map> riskAduitUsers = (List<Map>) this.queryForList("creditReportManage.getRiskAduitUsers",params);
					if(riskAduitUsers!=null){
						for(Map user:riskAduitUsers){
							String userId = String.valueOf(user.get("CREATE_USER_ID"));
							String email = LeaseUtil.getEmailByUserId(userId);
							if(email!=null){
								emails.append(email);
								emails.append(";");
							}
						}
					}
					String sensorId = LeaseUtil.getSensorIdByCreditId(creditId);
					String decp_id = LeaseUtil.getDecpIdByCreditId(creditId);
					//业务助理
					params.put("decp_id", decp_id);
					List<Map> assistants = (List<Map>) this.queryForList("creditReportManage.getBussinessAssistant",params);
					if(riskAduitUsers!=null){
						for(Map assistant:assistants){
							String email = (String) assistant.get("EMAIL");
							if(email!=null){
								emails.append(email);
								emails.append(";");
							}
						}
					}

					//主管
					String upUserId  =  LeaseUtil.getUpUserByUserId(sensorId);
					String email = LeaseUtil.getEmailByUserId(upUserId);
					
					MailSettingTo mailSettingTo = new MailSettingTo();
					mailSettingTo.setEmailContent(content);
					if(emails.indexOf(";")>0){
						 mailSettingTo.setEmailTo(emails.substring(0,emails.length()-1));
					}				
					mailSettingTo.setEmailSubject("乘用车合同衍生待补文件追踪");
					if(email!=null){
						mailSettingTo.setEmailCc(email);
					}
					mailUtilService.sendMail(mailSettingTo);
				}
			}
		}

		
	}
	public static Map getCarCreditProjectInfo(String creditId) throws Exception{
		Map<String,Object> info = new HashMap<String,Object>();
		//承租人名称
		String cust_id  = LeaseUtil.getCustIdByCreditId(creditId);
		String custName = LeaseUtil.getCustNameByCustId(cust_id);
		info.put("custName", custName);	
		String idCard = LeaseUtil.getNatuIdCardByCustId(cust_id);
		info.put("idCard", idCard);	
		String address = LeaseUtil.getNatuAddressByCustId(cust_id);
		info.put("address", address);	
		String mobile = LeaseUtil.getNatuMobileByCustId(cust_id);
		info.put("mobile", mobile);	
		String zip = LeaseUtil.getNatuZipByCustId(cust_id);
		info.put("zip", zip);	
		
		String mateName = LeaseUtil.getNatuMateNameByCustId(cust_id);
		info.put("mateName", mateName);	
		String mateIdCard = LeaseUtil.getNatuMateIdCardByCustId(cust_id);
		info.put("mateIdCard", mateIdCard);	
		String mateMobile = LeaseUtil.getNatuMateMobiledByCustId(cust_id);
		info.put("mateMobile", mateMobile);	
		
		String suplId = LeaseUtil.getSuplIdByCreditId(creditId);
		String suplName = LeaseUtil.getSuplNameByCreditId(creditId);
		info.put("suplName", suplName);
		String suplBankName = LeaseUtil.getSuppOpenAccountBankBySupplierId(suplId);
		info.put("suplBankName", suplBankName);
		String suplAccount = LeaseUtil.getSuppBankAccountBySupplierId(suplId);
		info.put("suplAccount", suplAccount);
		//期数
		int period = LeaseUtil.getPeriodsByCreditId(creditId);
		info.put("period", period);	
		//拨款金额
		Double payMoney = LeaseUtil.getPayMoneyByCreditId(creditId);
		payMoney = payMoney!=null?payMoney:0d;
		
		String upperCaseMoney = "";
		
		String money = NumberFormat.getInstance().format(payMoney);
		info.put("lowerCaseMoney", money);	
		money = money.replace(",", "");
		
		String month_price = "0";
		List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(creditId, Integer.valueOf(1));
		List<Map> list = new ArrayList<Map>();
		NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
		nfFSNum.setGroupingUsed(true);
		nfFSNum.setMaximumFractionDigits(2);
		for(Map m:irrMonthPaylines){
			month_price = NumberFormat.getInstance().format(m.get("IRR_MONTH_PRICE"));	
			int start = (Integer) m.get("IRR_MONTH_PRICE_START");
			int end = (Integer) m.get("IRR_MONTH_PRICE_END");
			String value =nfFSNum.format(m.get("IRR_MONTH_PRICE"));
			for(;start<=end;start++){
				Map map = new HashMap();
				map.put("index", start);
				map.put("value", value);
				list.add(map);
			}
		}
		info.put("list", list);
		info.put("month_price", month_price);	
		month_price= month_price.replace(",", "");
		String fen = getFen(month_price);
		info.put("fen", fen);	
		String jiao = getJiao(month_price);
		info.put("jiao", jiao);	
		String ge = getGe(month_price);
		info.put("ge", ge);	
		String shi = getShi(month_price);
		info.put("shi", shi);	
		String bai = getBai(month_price);
		info.put("bai", bai);	
		String qian = getQian(month_price);
		info.put("qian", qian);	
		String wan = getWan(month_price);
		info.put("wan", wan);	
		
		upperCaseMoney += new com.brick.car.util.CnUpperCaser(money).getCnString();
		info.put("upperCaseMoney", upperCaseMoney);	
		Map param = new HashMap();
		param.put("credit_id", creditId);
//		List<Map> corpList = (List<Map>) DataAccessor.query(
//				"creditVoucher.selectCorpByCreditId", param,
//				DataAccessor.RS_TYPE.LIST);
		List<Map> natuList = (List<Map>) DataAccessor.query(
				"creditVoucher.selectVouchNatu", param,
				DataAccessor.RS_TYPE.LIST);
		String guarantee = "";
		String guarantee_idcard = "";
		String guarantee_mobile ="";
		String guarantee_address ="";
//        for(Map corp:corpList){
//        	guarantee = (String) corp.get("CORP_NAME_CN");
//        }
        for(Map natu:natuList){
        	guarantee =  (String) natu.get("CUST_NAME");
        	guarantee_idcard = (String) natu.get("NATU_IDCARD");
        	guarantee_mobile =  (String) natu.get("NATU_MOBILE");
        	guarantee_address = (String) natu.get("NATU_HOME_ADDRESS");
        	break;
        }
        info.put("guarantee", guarantee);	
        info.put("guarantee_idcard", guarantee_idcard);	
        info.put("guarantee_mobile", guarantee_mobile);	
        info.put("guarantee_address", guarantee_address);
        
        info.put("guarantees", natuList);
        
        String leaseCode = LeaseUtil.getLeaseCodeByCreditId(creditId);
        info.put("leaseCode", leaseCode);	
        String bankName = LeaseUtil.getBankNameByCreditId(creditId);
        info.put("bankName", bankName);	
        String bankAccount = LeaseUtil.getBankAccountByCreditId(creditId);
        info.put("bankAccount", bankAccount);	
        
		List<Map> equipmentsList = (List<Map>) DataAccessor.query(
				"creditReportManage.selectCreditEquipment",
				param, DataAccessor.RS_TYPE.LIST);
		int i = 1;
		String carType = null;
		Double unit_price = null;
		String model_spec = null;
		int amount = 0;
		for(Map equipment:equipmentsList){
			amount += Integer.parseInt(String.valueOf(equipment.get("AMOUNT")));
			if(carType == null){
				carType = String.valueOf(equipment.get("TYPE_NAME")) +"/" + equipment.get("BRAND");
				info.put("carType",carType);
			}
			if(model_spec==null){
				model_spec = (String) equipment.get("MODEL_SPEC");
			}
			if(unit_price==null){
				unit_price = (Double) equipment.get("UNIT_PRICE");
				info.put("unit_price",NumberFormat.getInstance().format(unit_price));
				
				BigDecimal rate = new BigDecimal(payMoney).divide(new BigDecimal(unit_price), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
				info.put("rate",rate);
			}
			info.put("type_"+i, "小型普通客车");
			info.put("car_producer_"+i,equipment.get("MANUFACTURER"));
			info.put("car_brand_"+i,equipment.get("TYPE_NAME"));
			i++;
		}
		info.put("model_spec",model_spec);
		info.put("amount",amount);
		
		
		Calendar date = Calendar.getInstance();
		int year = date.get(Calendar.YEAR);
		info.put("year",year);
		
		String sensorId = LeaseUtil.getSensorIdByCreditId(creditId);
		String sensorName = LeaseUtil.getUserNameByUserId(sensorId);
		info.put("sensorName",sensorName);
		
		Double pledgePrice= LeaseUtil.getPledgePriceForAvgByCreditId(creditId);
		info.put("pledgePrice",NumberFormat.getInstance().format(pledgePrice));
		
		Map m = calculate(creditId);
		info.put("income_pay",m.get("INCOME_PAY"));

		List<Map> feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",param, DataAccessor.RS_TYPE.LIST);
		for(Map fee:feeList){
			if("保险费押金代收款".equals(fee.get("CREATE_SHOW_NAME"))){
				info.put("fee",fee.get("FEE"));
				break;
			}
		}
		
		List<Map> riskMemoList = (List) DataAccessor.query("riskAudit.selectRiskMemoListByCreditid", param, DataAccessor.RS_TYPE.LIST);
		for(Map memo:riskMemoList){
			if("0".equals(String.valueOf(memo.get("PRCM_USER_LEVEL")))){
				info.put("memo",memo.get("PRCM_CONTEXT"));
			}
		}
		
		double total = LeaseUtil.getTotalPriceByCreditId(creditId);
		info.put("total", nfFSNum.format(total));
		return info;
	}
	
	//获取角
	private static String getJiao(String money){
		String jiao = "零";
		int point = money.indexOf(".");
		if(point>=0){
			point ++;
			if(point<money.length()){
				jiao = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
			}
		}
		return jiao;
	}
	//获取分
	private static String getFen(String money){
		String fen = "零";
		int point = money.indexOf(".");
		if(point>=0){
			point += 2;
			if(point<money.length()){
				fen = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
			}
		}
		return fen;
	}
	
	private static String getGe(String money){
		String ge = "零";
		int point = money.indexOf(".");
		if(point<0){
			point = money.length()-1;
		}else{
			point = point -1;
		}
		if(point>=0){
			ge = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
		}
		return ge;
	}
	private static String getShi(String money){
		String shi = "零";
		int point = money.indexOf(".");
		if(point<0){
			point = money.length()-2;
		}else{
			point = point-2;
		}
		if(point>=0){
			shi = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
		}
		return shi;
	}
	
	private static String getBai(String money){
		String bai = "零";
		int point = money.indexOf(".");
		if(point<0){
			point = money.length()-3;
		}else{
			point = point-3;
		}
		if(point>=0){
			bai = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
		}
		return bai;
	}
	
	private static String getQian(String money){
		String qian = "零";
		int point = money.indexOf(".");
		if(point<0){
			point = money.length()-4;
		}else{
			point = point-4;
		}
		if(point>=0){
			qian = getUpperChar(Integer.parseInt(money.substring(point,point+1)));
		}
		return qian;
	}
	
	private static String getWan(String money){
		String wan = "";
		int point = money.indexOf(".");
		if(point<0){
			point = money.length()-5;
		}else{
			point = point-5;
		}
		if(point>=0){
			String number = money.substring(0,point+1);
			String qian = getQian(number);
			if(!"零".equals(qian)){
				wan = qian + "仟";
			}
			String bai = getBai(number);
			if("".equals(wan)){
				if(!"零".equals(bai)){
					wan = bai + "佰";
				}
			}else{
				wan += bai + "佰";
			}			
			String shi = getShi(number);
			if("".equals(wan)){
				if(!"零".equals(shi)){
					wan = shi + "拾";
				}
			}else{
				wan += shi + "拾";
			}
			String ge= getGe(number);
			if("".equals(wan)){
				if(!"零".equals(ge)){
					wan = ge;
				}
			}else{				
				wan += ge ;
			}
		}else{
			wan = "零";
		}
		return wan;
	}
	
	private static String getUpperChar(int number){
		String value = "";
		switch (number) {
			case 1:	{	value = "壹";		break;}
			case 2:	{	value= "贰";		break;}
			case 3:	{	value= "叁";		break;}
			case 4:	{	value= "肆";		break;}
			case 5:	{	value= "伍";		break;}
			case 6:	{	value= "陆";		break;}
			case 7:	{	value= "柒";		break;}
			case 8:	{	value= "捌";		break;}
			case 9:	{	value= "玖";		break;}
			case 0:	{	value= "零";		break;}
			default: break;
		}
 		return value;
	}

	 
	 
	 public static Map calculate(String credit_id){
			Map creditMap = null;
			Map schema = null;
			Map paylist = null;
			Map memoMap = null;
			Map contextMap = new HashMap();
			contextMap.put("credit_id", credit_id);
			try {				
				// credit_id 
				contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfo", contextMap, DataAccessor.RS_TYPE.MAP);
	
				// 
				memoMap = (Map) DataAccessor.query("creditReportManage.selectNewMemo", contextMap, DataAccessor.RS_TYPE.MAP);
		
				//查询方案
				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(contextMap.get("credit_id"), Integer.valueOf(1));
				//Add by Michael 2012 1/5 For 方案的查询
	
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				// 
				if(schema!=null&&"4".equals(schema.get("TAX_PLAN_CODE"))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
				}
				List companyList = null;
				companyList = (List) DataAccessor.query(
						"companyManage.queryCompanyAlias", null,
						DataAccessor.RS_TYPE.LIST);

				if (schema != null) {
					//Add by Michael 2012 01/29 在方案里增加合同类型
					schema.put("CONTRACT_TYPE", String.valueOf(creditMap.get("CONTRACT_TYPE")));
					
					//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算----------------------
					double totalFeeSet=0.0d;
					
					if("2".equals(schema.get("TAX_PLAN_CODE"))){
						List<Map> listTotalFeeSet=(List) DataAccessor.query("creditReportManage.getTotalFeeByRectID",contextMap, DataAccessor.RS_TYPE.LIST);
						for(Map map:listTotalFeeSet){
							totalFeeSet+=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						}	
					}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))||"5".equals(schema.get("TAX_PLAN_CODE"))){
						totalFeeSet=(Double)DataAccessor.query("creditReportManage.sumTotalFeeByRectID",contextMap, DataAccessor.RS_TYPE.OBJECT);
					}
					
					schema.put("FEESET_TOTAL", totalFeeSet);
					//-----------------------------------------------------------------------------
					
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));	
					paylist.put("PLEDGE_ENTER_MCTOAGRATE", schema.get("PLEDGE_ENTER_MCTOAGRATE"));	
					
					
				}
				//
				if("5".equals(schema.get("TAX_PLAN_CODE"))) {
					paylist.put("SALES_PAY", schema.get("SALES_PAY"));
					paylist.put("INCOME_PAY", schema.get("INCOME_PAY"));
					paylist.put("OUT_PAY", schema.get("OUT_PAY"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			return  paylist;
	 }
	 
	 
	 public String sendUnCompletedFile(List listUnCompletedFile){
		 UnnaturalCaseTO tempMap = new UnnaturalCaseTO();
		//如果没有待补文件则直接返回，不需要再发送Mail
		
		StringBuffer mailContent = new StringBuffer();
		mailContent.append("<html><head></head>");
		mailContent
				.append("<style>.rhead { background-color: #006699}"
						+ ".Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color: #000000; font-size: 9pt; text-decoration: none}"
						+ ".Body2BoldWhite2 { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #FFFFFF; font-size: 11pt; text-decoration: none }"
						+ ".Body2Bold { font-family: Arial, Helvetica, sans-serif; font-weight: bold; color: #000000; font-size: 8pt; text-decoration: none }"
						+ ".r11 {  background-color: #C4E2EC}"
						+ ".r12 { background-color: #D2EFF0}</style><body>");
		mailContent.append("<font size='3'><b>各位好:<b><br></font>"
				+ "<font size='2'>麻烦查收待补，谢谢咯~</font><br><br>");
		mailContent
				.append("<table border='1' cellspacing='0' width='1050px;' cellpadding='0'>"
						+ "<tr class='rhead'>"
						+ "<td class='Body2BoldWhite2' style='width:30px;' align='center'>序号</td>"
						+ "<td class='Body2BoldWhite2' style='width:100px;' align='center'>合同号</td>"
						+ "<td class='Body2BoldWhite2' align='center'>客户名称</td>"
						+ "<td class='Body2BoldWhite2' align='center'>待补文件名称</td>"
						+ "<td class='Body2BoldWhite2' align='center'>办事处</td>"
						+ "<td class='Body2BoldWhite2' align='center'>经办人</td>"
						+ "<td class='Body2BoldWhite2' align='center'>供应商</td>"
						+ "<td class='Body2BoldWhite2' align='center'>拨款日</td>"
						+ "<td class='Body2BoldWhite2' align='center'>应补回日</td>"
						+ "<td class='Body2BoldWhite2' align='center'>延迟天数</td>"
						+ "<td class='Body2BoldWhite2' align='center'>待补原因</td>"
						+ "<td class='Body2BoldWhite2' align='center'>拨款方式</td>"
						+ "<td class='Body2BoldWhite2' align='center'>备注</td></tr>");
		int num = 0;
		if (listUnCompletedFile != null) {
			for (int i = 0; i < listUnCompletedFile.size(); i++) {
				num++;
				tempMap = (UnnaturalCaseTO) listUnCompletedFile.get(i);
				mailContent.append("<tr class='r12'>" + "<td class=body2 >"
						+ num
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getLeaseCode()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getCustName()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getFileName()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getDeptName()
						+ "</td>"
						+ "<td class=body2>"
						+ tempMap.getName()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getSuplName()
						+ "</td>"
						+ "<td class=body2>"
						+ tempMap.getFinanceDate()
						+ "</td>"
						+ "<td class=body2>"
						+ tempMap.getShouldFinishDate()
						+ "</td>"
						+ "<td class=body2>"
						+ tempMap.getDelayDay()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getIssueReason()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getType()
						+ "</td>"
						+ "<td class=body2 >"
						+ tempMap.getFileRemark() + "</td></tr>");
			}
		}
		mailContent.append("</table>");
		mailContent.append("</body></html>");
		return mailContent.toString();
	}
	 
	 /**
	  * 
	  * @param date
	 * @throws Exception 
	  */
	 public static Map getCheckApply(String date,String companyCode) throws Exception{
	 	Calendar c = Calendar.getInstance();
	 	int year = c.get(Calendar.YEAR);
	 	int month = c.get(Calendar.MONTH)+1;
		ReportDateTo reportDate = null;
		if(date!=null){
			String[] dateArray = date.split("-");
			year =  Integer.parseInt(dateArray[0]);
			month = Integer.parseInt(dateArray[1]);
			reportDate = ReportDateUtil.getDateByYearAndMonth(year, month);
		}

		reportDate = ReportDateUtil.getDateByYearAndMonth(year, month);
		Map params = new HashMap();
		params.put("beginTime", reportDate.getBeginTime());
		params.put("endTime", reportDate.getEndTime());
		params.put("companyCode", companyCode);
		List list = (List) DataAccessor.query("report.getCheckApply", params,RS_TYPE.LIST);
		List l = new ArrayList();

		Map result = new HashMap();
		
		double total = 0d;
	
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				java.math.BigDecimal price = (java.math.BigDecimal) ((Map)list.get(i)).get("DECOMPOSE_PRICE");
				total += price.doubleValue();
			}
			l.add(list.get(0));
		}
		result.put("list", l);
		result.put("total", "￥" + total + "含税");
		
		year = reportDate.getBeginTime().getYear()+1990;
		month = reportDate.getBeginTime().getMonth()+1;
		int day = reportDate.getBeginTime().getDate();
		String dateStr = year + "年" + month + "月" + day + "日 至";
		
		year = reportDate.getEndTime().getYear()+1990;
		month = reportDate.getEndTime().getMonth()+1;
		day = reportDate.getEndTime().getDate();
		dateStr += year + "年" + month + "月" + day + "日 ";
		result.put("date", dateStr);
		String today = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH)+1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日 ";
		result.put("today", today);
		
		return result;
	 }
}
