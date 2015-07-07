package com.brick.dun.command;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.dun.serviceService.DunTaskLawService;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

/**
 * 
 * 
 * @author
 * @date
 */

public class DunTaskLawCommand extends BaseCommand {
	Log logger = LogFactory.getLog(DunTaskLawCommand.class);
	// zhangbo0529
	private DunTaskLawService dunTaskLawService;

	public DunTaskLawService getDunTaskLawService() {
		return dunTaskLawService;
	}

	public void setDunTaskLawService(DunTaskLawService dunTaskLawService) {
		this.dunTaskLawService = dunTaskLawService;
	}

	// 律师函生成，并邮件通知
	@SuppressWarnings("unchecked")
	public void getLawLetter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				dunTaskLawService.getLawLetter(context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("生产律师函错误了");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("result", "true");
			Output.jsonOutput(outputMap, context);
		}
	}

	//根据合同号得到逾期详细内容(小车)
	public static Map<String,Object> getLetterCarContentByRectId(String rectId,String dun_date){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dun_date", dun_date);
		map.put("rectId", rectId);
		Map<String, Object> mapContent = new HashMap<String, Object>();
		//承租人及配偶
		Map cust = null;
		//保证人
		List<Map> vouch = null;
		//设备详细信息，birt的table强制换行，so在java中先拼接成String
		StringBuilder detailString = new StringBuilder("");
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		try {
			mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", map, RS_TYPE.MAP);
			//承租人及配偶、联系人
			map.put("cust_id", mapContent.get("CUST_ID"));
			cust = (Map) DataAccessor.query("customer.readCustNatu", map, DataAccessor.RS_TYPE.MAP);
			
//			map.put("credit_id", mapContent.get("CREDIT_ID"));
//			creditCustomerCorpMap = (Map) DataAccessor.query("creditCustomerCorp.getCreditCustomerNatuByCreditId", map, DataAccessor.RS_TYPE.MAP);
//			if (creditCustomerCorpMap == null) {
//				creditCustomerCorpMap = (Map) DataAccessor.query("creditCustomer.getCustomerInfoBycredit_id", map, DataAccessor.RS_TYPE.MAP);
//			}
			String spouseName = cust.get("NATU_MATE_NAME")==null?"":cust.get("NATU_MATE_NAME").toString();
			String spouseAddress = cust.get("NATU_MATE_IDCARD_ADDRESS")==null?"":cust.get("NATU_MATE_IDCARD_ADDRESS").toString();
			String spousePhone = cust.get("NATU_MATE_MOBILE")==null?"":cust.get("NATU_MATE_MOBILE").toString();
			String custName = cust.get("CUST_NAME")==null?"":cust.get("CUST_NAME").toString();
			String custPhone = cust.get("NATU_MOBILE")==null?"":cust.get("NATU_MOBILE").toString();
			String custAddress = cust.get("NATU_IDCARD_ADDRESS")==null?"":cust.get("NATU_IDCARD_ADDRESS").toString();
//			if(StringUtils.isEmpty(spouseName)){
//				spouseName = cust.get("MATE_NAME")==null?"":cust.get("MATE_NAME").toString();
//			}
//			if(StringUtils.isEmpty(spousePhone)){
//				spousePhone = cust.get("MATE_MOBILE")==null?"":cust.get("MATE_MOBILE").toString();
//			}
			mapContent.put("spouseName", spouseName);
			mapContent.put("spouseAddress", spouseAddress);
			mapContent.put("spousePhone", spousePhone);
			mapContent.put("custName", custName);
			mapContent.put("custPhone", custPhone);
			mapContent.put("custAddress", custAddress);
			//无配偶信息抓取保证人列表
			if(StringUtils.isEmpty(spouseName)){
				map.put("credit_id", mapContent.get("CREDIT_ID"));
				vouch = (List<Map>) DataAccessor.query("creditVoucher.selectVouchNatu", map, DataAccessor.RS_TYPE.LIST);
				mapContent.put("vouch", vouch);
			}
			//首期支付日
			Map dunDate = (Map)DataAccessor.query("dunTask.getMaxDunDateByRectId", map, DataAccessor.RS_TYPE.MAP);
			mapContent.put("DUN_DAY", dunDate.get("DUN_DAY")==null?"":dunDate.get("DUN_DAY").toString());
			mapContent.put("DUN_COUNT", StringUtils.numToChinese(Integer.parseInt(dunDate.get("DUN_COUNT").toString())));
			mapContent.put("PERIOD_NUM", dunDate.get("PERIOD_NUM")==null?"":dunDate.get("PERIOD_NUM").toString());
			BigDecimal mData = new BigDecimal(dunDate.get("DUN_PRICE")==null?"0":dunDate.get("DUN_PRICE").toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
			mapContent.put("DUN_PRICE", mData.toString());
			Map dunData = (Map)DataAccessor.query("dunTask.getDunDataByRectId", map, DataAccessor.RS_TYPE.MAP);
			Date payDate = DateUtil.strToDate(dunData.get("PAY_DATE")==null?"":dunData.get("PAY_DATE").toString(), "yyyy-MM-dd");
			mapContent.put("PAY_DATE", DateUtil.dateToString(payDate, "yyyy年M月d日"));
			
			//设备详细信息
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getSueqListByRectId",map,RS_TYPE.LIST);
			if(mapSueq != null){
				int i = 1;
				for(Map m : mapSueq){
					detailString.append("品牌为" + (m.get("THING_KIND")==null?"":m.get("THING_KIND").toString()));
					detailString.append("，型号为" + (m.get("MODEL_SPEC")==null?"":m.get("MODEL_SPEC").toString()));
					detailString.append("，车牌号码为" + (m.get("CAR_RIGSTER_NUMBER")==null?" ":m.get("CAR_RIGSTER_NUMBER").toString()));
					detailString.append("的" + (m.get("THING_NAME")==null?"":m.get("THING_NAME").toString()));
					detailString.append("，数量为" + (m.get("AMOUNT")==null?"":m.get("AMOUNT").toString()) + (m.get("UNIT")==null?"":m.get("UNIT").toString()));
					if(mapSueq.size() == i){
						detailString.append("。");
					} else {
						detailString.append("，");
					}
					i++;
				}
			}
			mapContent.put("carDetail", detailString.toString());
			
			Date nowdate=new Date();
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			String dateString =format1.format(nowdate);
			mapContent.put("nowDate", dateToCnDate(dateString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapContent;
	}
	
	//根据合同号得到逾期详细内容
	public static Map<String,String> getLetterContentByRectId(String rectId,String dun_date){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dun_date", dun_date);
		map.put("rectId", rectId);
		Map<String, String> mapContent = new HashMap<String, String>();
		Map<String, String> creditCustomerCorpMap = new HashMap<String, String>();
		try {
			mapContent = (Map<String,String>)DataAccessor.query("dunTask.getLetterContentByRectId", map, RS_TYPE.MAP);
			
			Date nowdate=new Date();
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			String dateString =format1.format(nowdate);
			mapContent.put("nowDate", dateToCnDate(dateString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapContent;
	}
	//根据合同号得到设备列表
	public static List<Map<String,String>>  getSueqListByRectId(String rectId){
		
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		Map<String, String> mapContent = new HashMap<String, String>();
		mapContent.put("rectId", rectId);
		try {
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getSueqListByRectId",mapContent,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapSueq;
	}

	//自然人担保人
	public static List<Map<String,String>> getNatureListByRectId(String rectId){
		
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		Map<String, String> mapContent = new HashMap<String, String>();
		mapContent.put("rectId", rectId);
		try {
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getNatureListByRectId",mapContent,RS_TYPE.LIST);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapSueq;
	}
	//联保公司
	public static List<Map<String,String>>  getCompanyListByRectId(String rectId){
		
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		Map<String, String> mapContent = new HashMap<String, String>();
		mapContent.put("rectId", rectId);
		try {
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getCompanyListByRectId",mapContent,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapSueq;
	}
	//查询罚息（累计罚息-已交罚息）
	public static Map<String,Object>  getDunFineByRectId(String rectId){
			Map<String, Object> mapDunFine  = new HashMap<String, Object>();
		try {
			DecimalFormat df = new DecimalFormat(".00");
			Map<String, String> mapContent = new HashMap<String, String>();
		    mapContent.put("rectId", rectId);
			Map<String, Object>  dunAllFine=(Map<String, Object>)DataAccessor.query("dunTask.getAllFineByRectId", mapContent, DataAccessor.RS_TYPE.MAP);
			Map<String, Object>  dunInCome =(Map<String, Object>)DataAccessor.query("dunTask.getinComeFineByRectId", mapContent, DataAccessor.RS_TYPE.MAP);
			double allFine = Double.valueOf(String.valueOf(df.format(dunAllFine.get("FINE"))));
			double inComeFine = Double.valueOf(String.valueOf(df.format(dunInCome.get("DIFF_DUN"))));
			mapDunFine.put("fine", allFine-inComeFine);
			 //BigDecimal b2 = new BigDecimal(dunInCome.get("DIFF_DUN"));
		    //BigDecimal b1 = new BigDecimal(dunAllFine.get("FINE"));  
			//mapDunFine.put("fine", b1.subtract(b2).doubleValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapDunFine;
	}
	/**
	 * 字符串日期转换成中文格式日期
	 * @param date	字符串日期 yyyy-MM-dd
	 * @return	yyyy年MM月dd日
	 * @throws Exception
	 */
	public static String dateToCnDate(String date) {
		String result = "";
		String[]  cnDate = new String[]{"〇","一","二","三","四","五","六","七","八","九"};
		String ten = "十";
		String[] dateStr = date.split("-");
		for (int i=0; i<dateStr.length; i++) {
			for (int j=0; j<dateStr[i].length(); j++) {
				String charStr = dateStr[i];
				String str = String.valueOf(charStr.charAt(j));
				if (charStr.length() == 2) {
					if (charStr.equals("10")) {
						result += ten;
						break;
					} else {
						if (j == 0) {
							if (charStr.charAt(j) == '1') 
								result += ten;
							else if (charStr.charAt(j) == '0')
								result += "";
							else
								result += cnDate[Integer.parseInt(str)] + ten;
						}
						if (j == 1) {
							if (charStr.charAt(j) == '0')
								result += "";
							 else
								result += cnDate[Integer.parseInt(str)];
						}
					}
				} else {
					result += cnDate[Integer.parseInt(str)];
				}
			}
			if (i == 0) {
				result += "年";
				continue;
			}
			if (i == 1) {
				result += "月";
				continue;
			}
			if (i == 2) {
				result += "日";
				continue;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void getAuditLetterList(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		Map rsMap = new HashMap() ;
		try {
			boolean audit=false;
			//查询用户的权限（共用更改单查询）
			List<String> resourceIdList=(List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				//below is hard code for ResourceId,we will enhance it in the future
				if("Law-audit".equals(resourceIdList.get(i))) {
					audit=true;
				}
			outputMap.put("audit", audit);
			}
			if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				context.contextMap.put("dun_date", format.format(new Date()));
			}
			pagingInfo = baseService.queryForListWithPaging("dunTask.getAuditLetterList", context.contextMap, "AUDIT_ID", ORDER_TYPE.DESC);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dun_date", context.contextMap.get("dun_date"));
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("SELECT_STATUS", context.contextMap.get("SELECT_STATUS"));
		Output.jspOutput(outputMap, context, "/dun/dunAuditLetter.jsp");
	}
	//委寄
	@SuppressWarnings("unchecked")
	public void sendLaw(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				dunTaskLawService.sendLaw(context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("委寄错误了");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("result", "true");
			Output.jsonOutput(outputMap, context);
		}
	}
	//驳回
	@SuppressWarnings("unchecked")
	public void nopassLaw(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				dunTaskLawService.nopassLaw(context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("律师函驳回错误了");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("result", "true");
			Output.jsonOutput(outputMap, context);
		}
	}
	//委外回访生成word文档 
	@SuppressWarnings("unchecked")
	public void getOutVisit(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				dunTaskLawService.getOutVisit(context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("委外回访生成记录表错误");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("result", "true");
			Output.jsonOutput(outputMap, context);
		}
	}
	//标的物置放地
	public static Map<String,Object>  getEqupmentAddressByRectId(String rectId){
		Map<String, Object> equpmentAddress  = new HashMap<String, Object>();
			try {
				Map<String, String> mapContent = new HashMap<String, String>();
			    mapContent.put("rectId", rectId);
			 equpmentAddress=(Map<String, Object>)DataAccessor.query("dunTask.getEqupmentAddressByRectId", mapContent, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
			}
	return equpmentAddress;
	}
	//委外得到设备列表
	public static List<Map<String,String>>  getOutVisitSueqListByRectId(String rectId){
		
		List<Map<String, String>> mapSueq =new ArrayList<Map<String,String>>();
		Map<String, String> mapContent = new HashMap<String, String>();
		mapContent.put("rectId", rectId);
		try {
			mapSueq = (List<Map<String,String>>)DataAccessor.query("dunTask.getEqupmentListByRectId",mapContent,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapSueq;
	}
	//主要联系人
	public static Map<String,Object>  getCustLinkByRectId(String rectId){
		Map<String, Object> custLink  = new HashMap<String, Object>();
			try {
				Map<String, String> mapContent = new HashMap<String, String>();
			    mapContent.put("rectId", rectId);
			    custLink=(Map<String, Object>)DataAccessor.query("dunTask.getCustLinkByRectId", mapContent, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
			}
	return custLink;
	}
	//未缴总租金
	public static Map<String,Object>  getUnPayPriceByRectId(String rectId){
		Map<String, Object> unPayPrice  = new HashMap<String, Object>();
			try {
				Map<String, String> mapContent = new HashMap<String, String>();
			    mapContent.put("rectId", rectId);
			    unPayPrice=(Map<String, Object>)DataAccessor.query("dunTask.getUnPayPriceByRectId", mapContent, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
			}
	return unPayPrice;
	}
	//委外回访列表审批
	@SuppressWarnings("unchecked")
	public void getOutVisitAuditList(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		Map rsMap = new HashMap() ;
		try {
			boolean audit=false;
			//查询用户的权限（共用更改单查询）
			List<String> resourceIdList=(List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
				if("outVisit-audit".equals(resourceIdList.get(i))) {
					audit=true;
				}
			outputMap.put("audit", audit);
			}
			if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				context.contextMap.put("dun_date", format.format(new Date()));
			}
			pagingInfo = baseService.queryForListWithPaging("dunTask.getOutVisitAuditList", context.contextMap, "AUDIT_ID", ORDER_TYPE.DESC);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dun_date", context.contextMap.get("dun_date"));
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("SELECT_STATUS", context.contextMap.get("SELECT_STATUS"));
		Output.jspOutput(outputMap, context, "/dun/dunAuditOutVisit.jsp");
	}
	//生成通过日期的数据（委外回访生成word文档 ）
	@SuppressWarnings("unchecked")
	public void getPassOutVisit(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				dunTaskLawService.getPassOutVisit(context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("委外申请通过错误了");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("result", "true");
			Output.jsonOutput(outputMap, context);
		}
	}	
	//委外回访驳回
		@SuppressWarnings("unchecked")
		public void nopassOutVisit(Context context) {
			Map outputMap = new HashMap();
			List errList = context.errList;
			if (errList.isEmpty()) {
				try {
					dunTaskLawService.nopassOutVisit(context);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add("委外回访驳回错误了");
				}
				outputMap.put("result", "true");
				Output.jsonOutput(outputMap, context);
			}
		}
	  //委外回访管控导出excle
		public static List<Map<String,Object>> getAuditExcel(String outDate)  {
			List<Map<String,Object>> outList=new ArrayList<Map<String,Object>>();
			try {
				Map<String, String> mapContent = new HashMap<String, String>();
				mapContent.put("outDate", outDate);
				outList=(List<Map<String,Object>>) DataAccessor.query("dunTask.getAuditExcel",mapContent, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return outList;
		}
		//委外回访内容
		public static Map<String,Object> getOutVisitContentByRectId(String rectId,String dun_date,String out){
			Map<String, String> map = new HashMap<String, String>();
			map.put("dun_date", dun_date);
			map.put("rectId", rectId);
			Map<String, Object> mapContent = new HashMap<String, Object>();
			try {
			     mapContent = (Map<String,Object>)DataAccessor.query("dunTask.getLetterContentByRectId", map, RS_TYPE.MAP);
			     if(out.equals("true")){
						DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
						Calendar calendar = Calendar.getInstance();//
						mapContent.put("nowDate",format.format(calendar.getTime()));
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mapContent;
		}
}
