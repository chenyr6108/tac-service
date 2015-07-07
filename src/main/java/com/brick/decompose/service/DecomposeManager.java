package com.brick.decompose.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import com.brick.rent.RentFinanceUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.NumberUtils;
import com.brick.util.StringUtils;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;

public class DecomposeManager extends BaseCommand {
	Log logger = LogFactory.getLog(DecomposeManager.class);

	public static final Logger log = Logger.getLogger(DecomposeManager.class);
	static SqlMapClient sqlMapper = null;
	static {
		sqlMapper = DataAccessor.getSession();
	}
	private int fiin_id=0;
	private String comeName="";
	private boolean fail=false;
	/**
	 * 查询所有来款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showAllIncomeInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List bankList = null;
		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (cardFlag == 0) {
			context.contextMap.put("search_status", 2);
		} else if (cardFlag == 1) {
			context.contextMap.put("search_status", 3);
		} else if (cardFlag == 2) {
			context.contextMap.put("search_status", 4);
		} else if (cardFlag == 3) {
			context.contextMap.put("search_status", 5);
		} else if (cardFlag == 4) {
			context.contextMap.put("search_status", 1);
		} else if (cardFlag == 5) {
			context.contextMap.put("search_status", 6);
		} else if (cardFlag == 6) {
			context.contextMap.put("search_status", 7);
		} else if (cardFlag == 7) {
			context.contextMap.put("search_status", 0);
		}

		DataWrap incomeListPage = null;
		// 查询条件
		String search_startdate = null;
		String search_enddate = null;
		String search_startmoney = null;
		String search_endmoney = null;
		String search_status = null;
		String search_bankname = null;
		String search_bankno = null;
		String search_content = null;
		if (context.contextMap.get("search_startdate") == null) {
			search_startdate = "";
		} else {
			search_startdate = context.contextMap.get("search_startdate")
					.toString();
			search_startdate.trim();
		}
		if (context.contextMap.get("search_enddate") == null) {
			search_enddate = "";
		} else {
			search_enddate = context.contextMap.get("search_enddate")
					.toString();
			search_enddate.trim();
		}
		if (context.contextMap.get("search_startmoney") == null) {
			search_startmoney = "";
		} else {
			search_startmoney = context.contextMap.get("search_startmoney")
					.toString();
			search_startmoney.trim();
		}
		if (context.contextMap.get("search_endmoney") == null) {
			search_endmoney = "";
		} else {
			search_endmoney = context.contextMap.get("search_endmoney")
					.toString();
			search_endmoney.trim();
		}
		if (context.contextMap.get("search_status") == null) {
			search_status = "";
		} else {
			search_status = context.contextMap.get("search_status").toString();
			search_status.trim();
		}
		if (context.contextMap.get("search_bankname") == null) {
			search_bankname = "";
		} else {
			search_bankname = context.contextMap.get("search_bankname")
					.toString();
			search_bankname.trim();
		}
		if (context.contextMap.get("search_bankno") == null) {
			search_bankno = "";
		} else {
			search_bankno = context.contextMap.get("search_bankno").toString();
			search_bankno.trim();
		}
		if (context.contextMap.get("search_content") == null) {
			search_content = "";
		} else {
			search_content = context.contextMap.get("search_content")
					.toString();
			search_content.trim();
		}

		if (errorList.isEmpty()) {
			try {
				incomeListPage = (DataWrap) DataAccessor.query(
						"decompose.queryAllIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.PAGED);
				bankList = (List) DataAccessor.query("decompose.queryBankName",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.showAllIncomeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.showAllIncomeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询所有来款信息错误!请联系管理员");
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("bankList", bankList);
			outputMap.put("dw", incomeListPage);
			outputMap.put("search_startdate", search_startdate);
			outputMap.put("search_enddate", search_enddate);
			outputMap.put("search_startmoney", search_startmoney);
			outputMap.put("search_endmoney", search_endmoney);
			outputMap.put("search_status", cardFlag);
			outputMap.put("search_bankname", search_bankname);
			outputMap.put("search_bankno", search_bankno);
			outputMap.put("search_content", search_content);
			outputMap.put("cardFlag", cardFlag);
			outputMap.put("__action", context.contextMap.get("__action"));
			Output.jspOutput(outputMap, context, "/decompose/showIncome.jsp");
		} else {
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 根据银行名称查询银行账号信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryBankNo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List bankNoList = null;
		if (errorList.isEmpty()) {
			try {
				bankNoList = (List) DataAccessor.query(
						"decompose.queryBankAccount", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryBankNo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryBankNo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询银行账号信息错误");
			}
			outputMap
					.put("bankNoList", Output.serializer.serialize(bankNoList));
		} 
		if(errorList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}
	}

	/**
	 * 提示：查询承租人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCustInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List custList = null;
		if (errorList.isEmpty()) {
			try {
				custList = (List) DataAccessor.query("decompose.queryCustInfo",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryCustInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryCustInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询承租人信息错误!请联系管理员");
			}
			outputMap.put("custList", custList);
		}
		if(errorList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		} 
	}

	/**
	 * 提示：查询支付表信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpCodeInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List recpList = null;
		if (errorList.isEmpty()) {
			try {
				recpList = (List) DataAccessor.query(
						"decompose.queryRecpCodeInfo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryRecpCodeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryRecpCodeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询支付表信息错误!请联系管理员");
			}
			outputMap.put("recpList", recpList);
		}
		if(errorList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		} 
	}

	/**
	 * 根据承租人信息查询该承租人名下的分解项
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryCustDecomposeInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		String cust_name = "";
		String ficb_flag = context.contextMap.get("ficb_flag").toString();
		List suppList=null;
		List<Map<String,Object>> bankPayMontyMap=null;
		if (errorList.isEmpty()) {
			try {
				custIncomeMap = (HashMap) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				custDecomposeList = (List) DataAccessor.query(
						"decompose.queryCustDecomposeInfo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				cust_name = ((HashMap) DataAccessor.query(
						"decompose.queryCustNameByCode", context.contextMap,
						DataAccessor.RS_TYPE.MAP)).get("CUST_NAME").toString();
				//Add by Michael 2013 04-13 增加此客户的供应商列表
				suppList= (List) DataAccessor.query(
						"decompose.querySuppListByCustCode", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				
				bankPayMontyMap=(List<Map<String,Object>>)DataAccessor.query("decompose.getBankPayMoney",context.contextMap,RS_TYPE.LIST);
				
				if(bankPayMontyMap!=null) {
					custDecomposeList.addAll(bankPayMontyMap);
				}
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryCustDecomposeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryCustDecomposeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询承租人分解项错误!请联系管理员");
			}
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("ficb_flag", ficb_flag);
			outputMap.put("cust_name", cust_name);
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			outputMap.put("suppList", suppList);
			
			if(errorList.isEmpty()){
				Output.jspOutput(outputMap, context,
						"/decompose/showCustDecomposeInfo.jsp");
			} else {
				outputMap.put("errList", errorList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	/**
	 * 根据支付表查询需要正常结清的应分解项
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpNormalSettleInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		String cust_name = "";
		String ficb_flag = context.contextMap.get("ficb_flag").toString();
		context.contextMap.put("select_income_id", context.getRequest()
				.getParameter("select_income_id"));
		context.contextMap.put("cust_code", context.getRequest().getParameter(
				"cust_code"));
		context.contextMap.put("recp_code", context.getRequest().getParameter(
				"recp_code"));
		if (errorList.isEmpty()) {
			try {
				custIncomeMap = (HashMap) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				custDecomposeList = (List) DataAccessor.query(
						"decompose.queryRecpNormalSettle", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				cust_name = ((HashMap) DataAccessor.query(
						"decompose.queryCustNameByCode", context.contextMap,
						DataAccessor.RS_TYPE.MAP)).get("CUST_NAME").toString();
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryRecpNormalSettleInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询支付表需要正常结清的应分解项错误!请联系管理员");
			}
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("settle_flag", "normal");
			outputMap.put("cust_name", cust_name);
			outputMap.put("ficb_flag", ficb_flag);
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			if(errorList.isEmpty()){
				Output.jspOutput(outputMap, context,
						"/decompose/showCustDecomposeInfo.jsp");
			} else {
				outputMap.put("errList", errorList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	/**
	 * 根据支付表查询需要提前结清的应分解项
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRecpAheadSettleInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map custIncomeMap = null;
		Map custDecomposeMap = null;
		String cust_name = "";
		String ficb_flag = context.contextMap.get("ficb_flag").toString();
		context.contextMap.put("select_income_id", context.getRequest()
				.getParameter("select_income_id"));
		context.contextMap.put("cust_code", context.getRequest().getParameter(
				"cust_code"));
		context.contextMap.put("recp_code", context.getRequest().getParameter(
				"recp_code"));
		if (errorList.isEmpty()) {
			try {
				custIncomeMap = (HashMap) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("jieqingbenjin", "结清本金") ;
				context.contextMap.put("jieqinglixi", "结清利息") ;
				//Add by Michael 2011 12/31 增加结清罚息
				context.contextMap.put("jieqingfaxi", "结清罚息") ;
				
				context.contextMap.put("jieqingweiyuejin", "结清违约金") ;
				context.contextMap.put("jieqingsunhaijin", "结清损害金") ;
				context.contextMap.put("jieqingliugoujia", "结清留购价") ;
				context.contextMap.put("jieqinglawyfeiyong", "结清法务费用") ;
				context.contextMap.put("jieqingqitafeiyong", "结清其他费用") ;
				custDecomposeMap = (Map) DataAccessor.query(
						"decompose.queryRecpAheadSettle", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				cust_name = ((HashMap) DataAccessor.query(
						"decompose.queryCustNameByCode", context.contextMap,
						DataAccessor.RS_TYPE.MAP)).get("CUST_NAME")+"";
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.queryRecpAheadSettleInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.queryRecpAheadSettleInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询支付表需要提前结清的应分解项错误!请联系管理员");
			}
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("settle_flag", "ahead");
			outputMap.put("cust_name", cust_name);
			outputMap.put("ficb_flag", ficb_flag);
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeMap", custDecomposeMap);
			if(errorList.isEmpty()){
				Output.jspOutput(outputMap, context,
						"/decompose/showCustDecomposeInfoSettle.jsp");
			} else {
				outputMap.put("errList", errorList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	/**
	 * 制作手工分解单
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void handDecompose(Context context) {

		Map outputMap = new HashMap();
		List errorList = context.errList;
		// 获取带个参数
		String cust_code = context.contextMap.get("cust_code").toString();
		Integer fiin_id = Integer.valueOf(context.contextMap.get(
				"select_income_id").toString());
		// 获取参数集合
		String select_recp_id = context.contextMap.get("select_recp_id")
				.toString();
		String select_recp_code = context.contextMap.get("select_recp_code")
				.toString();
		String select_pay_date = context.contextMap.get("select_pay_date")
				.toString();
		String select_ficb_item = context.contextMap.get("select_ficb_item")
				.toString();
		String select_should_price = context.contextMap.get(
				"select_should_price").toString();
		String select_recd_type = context.contextMap.get("select_recd_type")
				.toString();
		String select_item_order = context.contextMap.get("select_item_order")
				.toString();
		String select_item_count = context.contextMap.get("select_item_count")
				.toString();
		String select_recd_period = context.contextMap
				.get("select_recd_period").toString();
		String tax_plan_code = context.contextMap
				.get("tax_plan_code").toString();
		
		String select_supl_code = context.contextMap
				.get("select_supl_code").toString();
		// 将参数集合拆开
		String recp_ids[] = select_recp_id.split(",");
		String recp_codes[] = select_recp_code.split(",");
		String pay_dates[] = select_pay_date.split(",");
		String ficb_items[] = select_ficb_item.split(",");
		String should_prices[] = select_should_price.split(",");
		String recd_types[] = select_recd_type.split(",");
		String item_orders[] = select_item_order.split(",");
		String item_counts[] = select_item_count.split(",");
		String recd_periods[] = select_recd_period.split(",");
		
		String tax_plan_codes[]= tax_plan_code.split(",");
		
		String select_supl_codes[] = select_supl_code.split(",");
		SqlMapClient sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			// 修改来款状态为分解中
			context.contextMap.put("decompose_status", 3);
			if (errorList.isEmpty()) {
				try {
//					DataAccessor.execute("decompose.updateIncomeStatus",
//							context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					sqlMapper.update("decompose.updateIncomeStatus", context.contextMap);
				} catch (Exception e) {
					log
							.error("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					e.printStackTrace();
					errorList
							.add("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
			//增加租金分解标记，是手动分解还是自动分解 DEPOSIT_FLAG
			context.contextMap.put("DEPOSIT_FLAG", 1);
			context.contextMap.put("ori_principal_runcode",  "");
			// 获取来款信息
			Map incomeMap = new HashMap();
			if (errorList.isEmpty()) {
				try {
					incomeMap = (Map) DataAccessor.query(
							"decompose.queryCustIncomeInfo", context.contextMap,
							DataAccessor.RS_TYPE.MAP);
//					DataAccessor.execute("decompose.deleteDecomposeBill",
//							context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
					
					sqlMapper.delete("decompose.deleteDecomposeBill", context.contextMap);
				} catch (Exception e) {
					log
							.error("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					e.printStackTrace();
					errorList
							.add("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
			double incomeMoney = Double.valueOf(incomeMap.get("INCOME_MONEY")
					.toString());
			String pay_date = incomeMap.get("OPPOSING_DATE").toString();
			// 根据来款金额分解各个分解项，插入分解单
			Integer recp_id = 0;
			String recp_code = "";
			for (int i = 0; i < item_counts.length; i++) {
				double should_price = Double.valueOf(should_prices[i]);
				double real_price = 0;
				if (incomeMoney >= 0.005) {
					if (incomeMoney >= should_price) {
						real_price = new BigDecimal(should_price).doubleValue();
						incomeMoney = new BigDecimal(incomeMoney).subtract(new BigDecimal(should_price)).doubleValue() ;
					} else {
						real_price = new BigDecimal(incomeMoney).doubleValue();
						incomeMoney = 0;
					}
				}
				context.contextMap.put("recp_id", Integer.valueOf(recp_ids[i]));
				recp_id = Integer.valueOf(recp_ids[i]);
				context.contextMap.put("recp_code", recp_codes[i]);
				recp_code = recp_codes[i];
				context.contextMap.put("pay_date", pay_dates[i]);
				context.contextMap.put("recd_period", recd_periods[i]);
				context.contextMap.put("ficb_item", ficb_items[i]);
				context.contextMap.put("should_price", Double
						.valueOf(should_prices[i]));
				context.contextMap.put("real_price", real_price);
				context.contextMap.put("fiin_id", fiin_id);
				context.contextMap.put("cust_code", cust_code);
				context.contextMap.put("ficb_state", 3);
				context.contextMap.put("ficb_type", 0);
				context.contextMap.put("recd_type", recd_types[i]);
				context.contextMap.put("item_order", item_orders[i]);
				context.contextMap.put("SUPL_CODE", select_supl_codes[i]);
				context.contextMap.put("decompose_id", context.getContextMap().get(
						"s_employeeId"));
				/*Add by Michael 2012 10-25  增加实际销账本金金额
				 *   如果此次分解的是租金，要把此次对应分解的 租金所对应的利息 + 税金 抓出来 进行比较 
				 *   如果 real_price > 利息 + 税金  则此次有分解到 本金 ，则要把 实际分解到 的本金 保存起来  
				 */
				if("租金".equals(ficb_items[i]) && "2".equals(tax_plan_codes[i])){
					//取出此次分解所对应的 租金 + 税金 及利息
					Map cal_price = (Map) DataAccessor.query(
							"decompose.queryRenTaxPriceForDecompose", context.contextMap,
							DataAccessor.RS_TYPE.MAP);
					//如果销账金额大于 租金 + 税金 则要计算出实际销账的本金金额
//					//如果来款金额大于 cal_price 则说明此次有将 租金+税金 销账完
					if (real_price>=DataUtil.doubleUtil(cal_price.get("CAL_PRICE"))){
						context.contextMap.put("real_own_price", new BigDecimal(DataUtil.doubleUtil(cal_price.get("IRR_MONTH_PRICE"))).subtract(new BigDecimal(DataUtil.doubleUtil(cal_price.get("REN_PRICE")))).doubleValue());
						//增加本金收据流水号
						context.contextMap.put("principal_rundode",  CodeRule.genePrincipalRunCode());
					}else{
						context.contextMap.put("real_own_price", 0);
						context.contextMap.put("principal_rundode",  "");
					}  
					
					//判断此次来款金额是否要超过已销账的增值税，如果没有则此次的来款金额是销账增值税，否则即有销账增值税又有销账租金
					//cal_price.get("CAL_VALUE_ADDED_PRICE")  销账的增值税，
					//如果  cal_price.get("CAL_VALUE_ADDED_PRICE") 大于 0，表示之前增值税已销账完成，此次销账租金,等于 0 表示之前销账的金额正好等于 增值税，此次的销账只是租金
					//否则此次的销账一定涉及到增值税
					if (DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))<0){
						//如果 来款大于  -DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))，则说明此次 销账中既有增值税又有租金，否则增值税
						if(real_price>(-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE")))){
														
							//实际销账的租金  等于  real_price - 差额 即  (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))) 数据
							context.contextMap.put("real_price", new BigDecimal(real_price).subtract(new BigDecimal((-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))))).doubleValue());
							context.contextMap.put("ficb_item", "租金");
							context.contextMap.put("should_price", cal_price.get("IRR_MONTH_PRICE"));
							sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
							
							context.contextMap.put("real_own_price", 0);
							context.contextMap.put("principal_rundode",  "");
							context.contextMap.put("real_price", (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))));
							context.contextMap.put("ficb_item", "增值税");
							context.contextMap.put("should_price", (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))));
							sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
							
						}else{  
							//否则此次只有销账增值税
							context.contextMap.put("should_price", (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))));
							context.contextMap.put("ficb_item", "增值税");
							sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
						}
					}else{
						//否则此次只销账租金
						sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
					}
					
				}else if("结清本金".equals(ficb_items[i]) && "2".equals(tax_plan_codes[i])){
					context.contextMap.put("real_own_price", real_price);
					//增加本金收据流水号
					context.contextMap.put("principal_rundode",  CodeRule.genePrincipalRunCode());
					sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
				}else{
					context.contextMap.put("real_own_price", 0);
					context.contextMap.put("principal_rundode",  "");
					sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);	
				}
				
				//-------------------Add by Michael 2012 10-25  增加实际销账本金金额-------------------------------------
			}
			// 如果分解的金额还有剩余，则剩下钱将作为待分解来款
			if (incomeMoney >= 0.005) {
				if (recp_id == null || recp_id == 0) {
					Map recpmap;
					try {
						recpmap = (Map) DataAccessor.query(
								"decompose.queryRecpInfo", context.contextMap,
								DataAccessor.RS_TYPE.MAP);
						recp_id = Integer
								.valueOf(recpmap.get("recp_id").toString());
						recp_code = recpmap.get("recp_code").toString();
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						errorList.add(e);
					}
				}
				context.contextMap.put("recp_id", Integer.valueOf(recp_id));
				context.contextMap.put("recp_code", recp_code);
				context.contextMap.put("pay_date", pay_date);
				context.contextMap.put("recd_period", 0);
				context.contextMap.put("ficb_item", "待分解来款");
				context.contextMap.put("should_price", incomeMoney);
				context.contextMap.put("real_price", incomeMoney);
				context.contextMap.put("fiin_id", fiin_id);
				context.contextMap.put("cust_code", cust_code);
				context.contextMap.put("ficb_state", 3);
				context.contextMap.put("ficb_type", 0);
				context.contextMap.put("recd_type", 4);
				context.contextMap.put("item_order", "099z");
				context.contextMap.put("decompose_id", context.getContextMap().get(
						"s_employeeId"));
				
				context.contextMap.put("real_own_price", 0);
				context.contextMap.put("principal_rundode",  "");
				context.contextMap.put("ori_principal_runcode",  "");
				context.contextMap.put("SUPL_CODE", 0);
				if (errorList.isEmpty()) {
					try {
						sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
					} catch (Exception e) {
						log
								.error("com.brick.decompose.service.DecomposeManager.handDecompose"
										+ e.getMessage());
						e.printStackTrace();
						errorList
								.add("com.brick.decompose.service.DecomposeManager.handDecompose"
										+ e.getMessage());
						LogPrint.getLogStackTrace(e, logger);
						errorList.add(e);
					}
				}
			}
			// 获取分解单信息和分解单操作人信息
			List decomposeBillList = null;
			Map decomposeEmpMap = null;
			try {
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryDecomposeBill", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				decomposeEmpMap = (HashMap) DataAccessor.query(
						"decompose.queryDecomposeEmp", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.handDecompose"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.handDecompose"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			String ficb_flag = context.contextMap.get("ficb_flag").toString();
			outputMap.put("ficb_flag", ficb_flag);
			outputMap.put("incomeMap", incomeMap);
			outputMap.put("decomposeEmpMap", decomposeEmpMap);
			outputMap.put("decomposeBillList", decomposeBillList);
			outputMap.put("operate_flag", 1);
			
			sqlMapper.update("decompose.updateDeposeCustName",context.contextMap);
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.debug(e);
			}
		}
		
		
		if(errorList.isEmpty()){
			Output
					.jspOutput(outputMap, context,
							"/decompose/showDecomposeBill.jsp");
		} else {
			errorList.add("制作手工分解单错误!请联系管理员") ;
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 自动生成分解单
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void autoDecompose(Context context) {
		Map outputMap = new HashMap();
		List custDecomposeList = null;
		List errorList = context.errList;
		Map custIncomeMap = null;
		String cust_code = context.contextMap.get("cust_code").toString();
		Integer fiin_id = Integer.valueOf(context.contextMap.get(
				"select_income_id").toString());
		List decomposeBillList = new ArrayList();
		Map decomposeEmpMap = null;
		String recp_code = context.contextMap.get("recp_code").toString();
		// 修改来款状态为分解中
		
		 sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			context.contextMap.put("decompose_status", 3);
			if (errorList.isEmpty()) {
				try {
					
//					DataAccessor.execute("decompose.updateIncomeStatus",
//							context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					sqlMapper.update("decompose.updateIncomeStatus", context.contextMap);
				} catch (Exception e) {
					log
							.error("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					e.printStackTrace();
					errorList
							.add("com.brick.decompose.service.DecomposeManager.handDecompose"
									+ e.getMessage());
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
			if (recp_code != null && !recp_code.equals("")
					&& !recp_code.equals("null")) {
				String settle_flag = context.contextMap.get("settle_flag")
						.toString();
				if (settle_flag.equals("normal")) {
					try {
						// 来款人资料
						custIncomeMap = (HashMap) DataAccessor.query(
								"decompose.queryCustIncomeInfo",
								context.contextMap, DataAccessor.RS_TYPE.MAP);
						// 分解单表
						custDecomposeList = (List) DataAccessor.query(
								"decompose.queryRecpNormalSettle",
								context.contextMap, DataAccessor.RS_TYPE.LIST);
//						DataAccessor.execute("decompose.deleteDecomposeBill",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.DELETE);
						sqlMapper.delete("decompose.deleteDecomposeBill", context.contextMap);
					} catch (Exception e1) {
						e1.printStackTrace();
						LogPrint.getLogStackTrace(e1, logger);
						errorList.add(e1);
					}
				} else if (settle_flag.equals("ahead")) {
					try {
						// 来款人资料
						custIncomeMap = (HashMap) DataAccessor.query(
								"decompose.queryCustIncomeInfo",
								context.contextMap, DataAccessor.RS_TYPE.MAP);
						// 分解单表
						custDecomposeList = (List) DataAccessor.query(
								"decompose.queryRecpAheadSettle",
								context.contextMap, DataAccessor.RS_TYPE.LIST);
//						DataAccessor.execute("decompose.deleteDecomposeBill",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.DELETE);
						sqlMapper.delete("decompose.deleteDecomposeBill", context.contextMap);
					} catch (Exception e1) {
						e1.printStackTrace();
						LogPrint.getLogStackTrace(e1, logger);
						errorList.add(e1);
					}
				}
			} else if (recp_code == null || recp_code.equals("")
					|| recp_code.equals("null")) {
				try {
					// 来款人资料
					custIncomeMap = (HashMap) DataAccessor.query(
							"decompose.queryCustIncomeInfo", context.contextMap,
							DataAccessor.RS_TYPE.MAP);
					// 分解单表
					custDecomposeList = (List) DataAccessor.query(
							"decompose.queryCustDecomposeInfo", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
//					DataAccessor.execute("decompose.deleteDecomposeBill",
//							context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
					sqlMapper.delete("decompose.deleteDecomposeBill", context.contextMap);
				} catch (Exception e1) {
					e1.printStackTrace();
					LogPrint.getLogStackTrace(e1, logger);
					errorList.add(e1);
				}
			}
			// 来款金额
			double incomeMoney = Double.valueOf(custIncomeMap.get("INCOME_MONEY")
					.toString());

			for (int i = 0; i < custDecomposeList.size(); i++) {
				Map where = (Map) custDecomposeList.get(i);
				double sprice = Double.valueOf(where.get("SPRICE").toString());

				context.contextMap.put("recp_id", where.get("PAYLIST_ID"));
				context.contextMap.put("recp_code", where.get("PAYLIST_CODE"));
				context.contextMap.put("pay_date", where.get("SDATE"));
				context.contextMap.put("ficb_item", where.get("FICB_ITEM"));
				context.contextMap.put("recd_period", where.get("RECD_PERIOD"));
				context.contextMap.put("should_price", where.get("SPRICE"));

				context.contextMap.put("fiin_id", fiin_id);
				context.contextMap.put("cust_code", cust_code);
				context.contextMap.put("ficb_state", 3);
				context.contextMap.put("ficb_type", 0);
				context.contextMap.put("recd_type", where.get("RECD_TYPE"));
				context.contextMap.put("item_order", where.get("O"));
				context.contextMap.put("decompose_id", context.getContextMap().get(
						"s_employeeId"));

				//增加租金分解标记，是手动分解还是自动分解 DEPOSIT_FLAG
				context.contextMap.put("DEPOSIT_FLAG", 0);
				
				if (incomeMoney >= 0.05) {
					double tempMoney = incomeMoney - sprice;
					if (tempMoney > 0) {
						context.contextMap.put("real_price", sprice);
					} else {
						context.contextMap.put("real_price", incomeMoney);
					}
					//incomeMoney -= sprice;
					incomeMoney=new BigDecimal(incomeMoney).subtract(new BigDecimal(sprice)).doubleValue();
					
					try {
//						DataAccessor.execute("decompose.addDecomposeBill",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.INSERT);
						sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
						errorList.add(e);
					}
				}
			}
			// 如果分解的金额还有剩余，则剩下钱将作为待分解来款
			if (incomeMoney >= 0.005) {
				Map recpMap = new HashMap();
				if (custDecomposeList.isEmpty()) {
					recpMap.put("recp_id", 0);
				} else {
					recpMap.put("recp_id", ((Map) custDecomposeList.get(0))
							.get("PAYLIST_ID"));
				}
				recpMap.put("recp_code", ((Map) custDecomposeList.get(0))
						.get("PAYLIST_CODE"));
				recpMap.put("pay_date", custIncomeMap.get("OPPOSING_DATE"));
				recpMap.put("recd_period", 0);
				recpMap.put("ficb_item", "待分解来款");
				recpMap.put("should_price", incomeMoney);
				recpMap.put("real_price", incomeMoney);
				recpMap.put("fiin_id", fiin_id);
				recpMap.put("cust_code", cust_code);
				recpMap.put("ficb_state", 3);
				recpMap.put("ficb_type", 0);
				recpMap.put("recd_type", 4);
				recpMap.put("item_order", "099z");
				recpMap.put("decompose_id", context.getContextMap().get(
						"s_employeeId"));

				//增加租金分解标记，是手动分解还是自动分解 DEPOSIT_FLAG
				context.contextMap.put("DEPOSIT_FLAG", 0);
				
				try {
//					DataAccessor.execute("decompose.addDecomposeBill", recpMap,
//							DataAccessor.OPERATION_TYPE.INSERT);
					sqlMapper.insert("decompose.addDecomposeBill", recpMap);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
			// 获取分解单信息和分解单操作人信息

			try {
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryDecomposeBill", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				decomposeEmpMap = (HashMap) DataAccessor.query(
						"decompose.queryDecomposeEmp", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			String ficb_flag = context.contextMap.get("ficb_flag").toString();
			outputMap.put("ficb_flag", ficb_flag);
			outputMap.put("incomeMap", custIncomeMap);
			outputMap.put("decomposeEmpMap", decomposeEmpMap);
			outputMap.put("decomposeBillList", decomposeBillList);
			outputMap.put("operate_flag", 1);
			
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.debug(e);
			}
		}
		if(errorList.isEmpty()){
			Output.jspOutput(outputMap, context,"/decompose/showDecomposeBill.jsp");
		} else {
			errorList.add("制作自动分解单错误!请联系管理员") ;
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

	}

	/**
	 * 将制作的分解单提交给财务
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void commitDecomposeBill(Context context) {
		String incomeId="";//老租金分解插入新租金分解使用
		String fiinId="";//老租金分解插入新租金分解使用
		
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map incomeMap = null;
		List decomposeBillList = null;
		
		SqlMapClient sqlMapper=DataAccessor.getSession();
		if (errorList.isEmpty()) {
			context.contextMap.put("decompose_status", 4);
			try {
//				DataAccessor.execute("decompose.updateIncomeStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("decompose.updateDecomposeStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
				boolean flag=true;
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("select_income_id",context.contextMap.get("select_income_id"));
				while(flag) {
					String leftId=(String)sqlMapper.queryForObject("decompose.getLeftId",param);
					if(leftId==null||"".equals(leftId)) {
						incomeId=(String)sqlMapper.queryForObject("decompose.getOriginalFiinId",param);
						flag=false;
					} else {
						param.put("select_income_id",leftId);
					}
				}
				fiinId=context.contextMap.get("select_income_id")+"";
						
				sqlMapper.startTransaction();
				sqlMapper.update("decompose.updateIncomeStatus", context.contextMap);
				sqlMapper.update("decompose.updateDecomposeStatus", context.contextMap);
				
				
				incomeMap = (Map) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryItemMoney", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				String ficb_item = "";
				if (decomposeBillList != null) {
					for (int i = 0; i < decomposeBillList.size(); i++) {
						ficb_item = ((Map) decomposeBillList.get(i)).get(
								"FICB_ITEM").toString();
						context.contextMap.put("real_price", Double
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"REAL_PRICE").toString()));
						context.contextMap.put("recp_id", Integer
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"RECP_ID").toString()));
						context.contextMap.put("period_num", Integer
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"RECD_PERIOD").toString()));
						if (ficb_item.equals("租金")) {
//							DataAccessor.execute("decompose.updateMonthPrice",
//									context.contextMap,
//									DataAccessor.OPERATION_TYPE.UPDATE);
							//sqlMapper.update("decompose.updateMonthPrice", context.contextMap);
						}
					}
				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.handDecompose"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.handDecompose"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("分解单提交到财务错误!请联系管理员");
			}
			finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					errorList.add(e);
				}
			}
			
			//加入新的租金分解插表模组
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("select_income_id",fiinId);
			List<Map<String,Object>> decomposeList=null;
			try {
				decomposeList=(List<Map<String,Object>>)DataAccessor.query("decompose.getDecomposeListById",param,RS_TYPE.LIST);
				param.put("fiin_id",incomeId);
				String income_id=DataAccessor.query("decompose.getIncomeIdByFiinId",param,RS_TYPE.OBJECT)+"";
				sqlMapper.startTransaction();
				for(int i=decomposeList.size()-1;decomposeList!=null&&i>=0;i--) {
					Map<String,Object> insertMap=new HashMap<String,Object>();
					insertMap.put("income_id",income_id);
					insertMap.put("recp_id",decomposeList.get(i).get("RECP_ID"));
					insertMap.put("period_num",decomposeList.get(i).get("PERIOD_NUM"));
					insertMap.put("pay_date",decomposeList.get(i).get("PAY_DATE"));
					insertMap.put("bill_code",decomposeList.get(i).get("FICB_ITEM"));
					insertMap.put("should_price",decomposeList.get(i).get("SHOULD_PRICE"));
					insertMap.put("decompose_price",decomposeList.get(i).get("DECOMPOSE_PRICE"));
					insertMap.put("decompose_from",decomposeList.get(i).get("DECOMPOSE_FROM"));
					insertMap.put("ficb_id",decomposeList.get(i).get("FICB_ID"));
					insertMap.put("decompose_status",1);
					insertMap.put("decompose_type",0);
					insertMap.put("has_red_decompose",0);
					insertMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					long billId=(Long)sqlMapper.insert("rentFinance.insertRentDecompose",insertMap);
					
					insertMap.put("bill_id",billId);
					insertMap.put("decompose_status",0);
					insertMap.put("table","T_RENT_DECOMPOSE");
					sqlMapper.insert("rentFinance.insertRent",insertMap);
				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e1) {
					
				}
			}
		}
		if(errorList.isEmpty()){
			Output.jspOutput(outputMap, context,"/servlet/defaultDispatcher?__action=decompose.showAllIncomeInfo");
		} else {
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 修改来款的资金备注（资金管理员）
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateOperateRemark(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		String updateresult = "备注信息修改成功";
		if (errorList.isEmpty()) {
			try {
				DataAccessor.execute("decompose.updateOperateRemark",
						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.updateOperateRemark"
								+ e.getMessage());
				e.printStackTrace();
				updateresult = "备注信息修改失败";
				errorList
						.add("com.brick.decompose.service.DecomposeManager.updateOperateRemark"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
			outputMap.put("updateresult", updateresult);
			Output.jsonOutput(outputMap, context);
		}
	}

	/**
	 * 分解全部来款
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void incomeAllClean(Context context) {
		StringBuffer resultStr=new StringBuffer();
		Map outputMap = new HashMap();
		List undecomposedList = null;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		sqlMapper=DataAccessor.getSession();
		//-----Add By Michael 2011 12/9 增加变量声明
		double sprice=0.0;
		double irr_month_price=0.0;
		double valueAdd_tax_price=0.0;
		Map where=null;
		boolean inComeMoneyIsOK=true;
		Map<String,Object> insertMap=new HashMap<String,Object>();
		try {
			
			undecomposedList = (List) DataAccessor.query(
					"decompose.getAllunincome", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			resultStr.append("基础资料获取出错,分解未开始,请联系资讯课!");
			LogPrint.getLogStackTrace(e, logger);
			outputMap.put("returnStr", resultStr.toString());
			Output.jsonOutput(outputMap, context);
			return;
		}
			if (!undecomposedList.isEmpty()) {
				try {
					undecomposedList = (List) DataAccessor.query(
							"decompose.getAllunincomeRECP", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
				} catch (Exception e) {
					resultStr.append("基础资料获取出错,分解未开始,请联系资讯课!");
					LogPrint.getLogStackTrace(e, logger);
					outputMap.put("returnStr", resultStr.toString());
					Output.jsonOutput(outputMap, context);
					return;
				}
				if(!undecomposedList.isEmpty()){
			
					for (int i = 0; i < undecomposedList.size(); i++) {
						try {	
						Map undecomposedMap = (Map) undecomposedList.get(i);
						Map map = new HashMap();
						map.put("select_income_id", undecomposedMap.get("FIIN_ID"));
						this.fiin_id=Integer.valueOf(undecomposedMap.get("FIIN_ID").toString());
						this.comeName=(String)undecomposedMap.get("CUST_NAME");
						// 修改来款状态为分解中
						map.put("ficb_flag", 0);
						map.put("decompose_status", 3);
						
						// 来款人资料
						custIncomeMap = (HashMap) DataAccessor.query(
								"decompose.queryCustIncomeInfo", map,
								DataAccessor.RS_TYPE.MAP);
						// 分解单表
						map.put("opposing_date", undecomposedMap.get("OPPOSING_DATE"));
						map.put("cust_code", undecomposedMap.get("CUST_CODE"));
						custDecomposeList = (List) DataAccessor.query(
								"decompose.queryCustDecomposeInfofenjieAll", map,
								DataAccessor.RS_TYPE.LIST);
						
						sqlMapper.startTransaction();//事务开始
						sqlMapper.update("decompose.updateIncomeStatus", map);
						sqlMapper.delete("decompose.deleteDecomposeBill", map);
						// 来款金额
						double incomeMoney = Double.valueOf(custIncomeMap.get("INCOME_MONEY")
								.toString());
						int fiin_id = Integer.parseInt(map.get("select_income_id").toString());
						String cust_code = map.get("cust_code").toString();
						/*
						 * Marked by Michael 2011 12/12
						 * 自动分解时要比对来款金额与将要分解的金额是否一致，如果一致才分解，如果不一致就不分解
						 * 所以就不处理还有剩余金额的情况，因为不存在还有金额的情况
						 * 只有一笔来款，对应一笔将要分解的款项，所以就不需要进行来款的加减、进行循环计算
						 */
						if(!custDecomposeList.isEmpty()){
							
							context.contextMap.put("ori_principal_runcode",  "");
							
							where = (Map) custDecomposeList.get(0);	//直接取第一笔要分解的款项						
							sprice = Double.valueOf(where.get("SPRICE").toString());
				
							//加入租金分解新模块,迁移数据用
							String fiin_id_=undecomposedMap.get("FIIN_ID").toString();//老的来款表中的主键
							Map<String,Object> param=new HashMap<String,Object>();
							param.put("fiin_id",fiin_id_);
							String incomeId=(String)DataAccessor.query("decompose.getIncomeIdByFiinId",param,RS_TYPE.OBJECT); 
							insertMap.put("income_id",incomeId);
							insertMap.put("recp_id",where.get("PAYLIST_ID"));
							insertMap.put("period_num",where.get("RECD_PERIOD"));
							insertMap.put("pay_date",where.get("SDATE"));
							
							insertMap.put("decompose_type",0);
							insertMap.put("has_red_decompose",0);
							insertMap.put("s_employeeId",184);
							insertMap.put("select_income_id",fiin_id_);
							insertMap.put("table","T_RENT_DECOMPOSE");
							
							if (sprice >= 0.05 && incomeMoney == sprice){
								context.contextMap.put("recp_id", where.get("PAYLIST_ID"));
								context.contextMap.put("recp_code", where.get("PAYLIST_CODE"));
								context.contextMap.put("pay_date", where.get("SDATE"));
								context.contextMap.put("ficb_item", where.get("FICB_ITEM"));
								context.contextMap.put("recd_period", where.get("RECD_PERIOD"));
								context.contextMap.put("should_price", where.get("SPRICE"));
			
								context.contextMap.put("fiin_id", fiin_id);
								context.contextMap.put("cust_code", cust_code);
								context.contextMap.put("ficb_state", 3);
								context.contextMap.put("ficb_type", 0);
								context.contextMap.put("recd_type", where.get("RECD_TYPE"));
								context.contextMap.put("item_order", where.get("O"));
								context.contextMap.put("decompose_id", context.getContextMap().get(
										"s_employeeId"));
								
								//增加租金分解标记，是手动分解还是自动分解 DEPOSIT_FLAG
								context.contextMap.put("DEPOSIT_FLAG", 0);
								
								if (incomeMoney >= 0.05) {
									
									double tempMoney = new BigDecimal(incomeMoney).subtract(new BigDecimal(sprice)).doubleValue();
									if (tempMoney > 0) {
										context.contextMap.put("real_price", sprice);
									} else {
										context.contextMap.put("real_price", incomeMoney);
									}
									
									if ("2".equals(where.get("TAX_PLAN_CODE"))){
										Map cal_price = (Map) DataAccessor.query(
												"decompose.queryRenTaxPriceForDecompose", context.contextMap,
												DataAccessor.RS_TYPE.MAP);

										//如果来款金额大于 cal_price 则说明此次有将 租金+税金 销账完
										if (incomeMoney>=DataUtil.doubleUtil(cal_price.get("CAL_PRICE"))){
											context.contextMap.put("real_own_price", new BigDecimal(DataUtil.doubleUtil(cal_price.get("IRR_MONTH_PRICE"))).subtract(new BigDecimal(DataUtil.doubleUtil(cal_price.get("REN_PRICE")))).doubleValue());
											//增加本金收据流水号
											context.contextMap.put("principal_rundode",  CodeRule.genePrincipalRunCode());
										}else{
											context.contextMap.put("real_own_price", 0);
											context.contextMap.put("principal_rundode",  "");
										}  
										
										//如果 cal_price.get("CAL_VALUE_ADDED_PRICE") 小于 0说明之前 的销账增值税没有销账完
										//此次有销账到增值税,并且来款大于 未销账的增值税时，说明既有销账到增值税又有销账到租金
										if (DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))<0 && sprice>(-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE")))){
											context.contextMap.put("ficb_item", "租金");
											context.contextMap.put("should_price", new BigDecimal(sprice).subtract(new BigDecimal(DataUtil.doubleUtil(where.get("VALUE_ADDED_TAX")))).doubleValue());
											context.contextMap.put("real_price", new BigDecimal(sprice).subtract(new BigDecimal(DataUtil.doubleUtil(where.get("VALUE_ADDED_TAX")))).doubleValue());
											sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
											
											//Add by Michael 2013 01-16 将租金与增值税切开 
											context.contextMap.put("ficb_item", "增值税");
											context.contextMap.put("should_price", (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))));
											context.contextMap.put("real_price", (-DataUtil.doubleUtil(cal_price.get("CAL_VALUE_ADDED_PRICE"))));
											context.contextMap.put("real_own_price", 0);
											context.contextMap.put("principal_rundode",  "");	
										}else{
											//否则增值税之前已经销账完成，此次销账租金
											context.contextMap.put("ficb_item", "租金");
											context.contextMap.put("should_price", new BigDecimal(sprice).doubleValue());
											context.contextMap.put("real_price", new BigDecimal(sprice).doubleValue());
										}										

									}else{
										context.contextMap.put("real_own_price", 0);
										context.contextMap.put("principal_rundode",  "");
									}

									incomeMoney= new BigDecimal(incomeMoney).subtract(new BigDecimal(sprice)).doubleValue();
									
									sqlMapper.insert("decompose.addDecomposeBill", context.contextMap);
									
								}					

								
								map.put("decompose_status", 4);
								
								sqlMapper.update("decompose.updateIncomeStatus", map);
								sqlMapper.update("decompose.updateDecomposeStatus", map);
								
								map.put("CUST_NAME",undecomposedMap.get("CUST_NAME"));
								sqlMapper.update("decompose.updateDeposeCustName",map);
							//add By Michael 2011 12/12 如果来款与将要分解的来款	
							}else{
								map.put("Abnormal_Status", 1);
								map.put("decompose_status", 2);
								sqlMapper.update("decompose.updateIncomeStatus", map);
								sqlMapper.update("decompose.updateIncomeDecomposeStatus", map);
							}
						}else{
							map.put("Abnormal_Status", 1);
							map.put("decompose_status", 2);
							sqlMapper.update("decompose.updateIncomeStatus", map);
							sqlMapper.update("decompose.updateIncomeDecomposeStatus", map);
						}
			//----------------------------------------------------------------------------------	
						sqlMapper.commitTransaction();
						
						//数据迁移
						List<Map<String,Object>> keyList=(List<Map<String,Object>>)DataAccessor.query("decompose.getFicbId1",insertMap,RS_TYPE.LIST);
						sqlMapper.startTransaction();
						for(int j=0;keyList!=null&&j<keyList.size();j++) {
							if(insertMap.get("income_id")==null) {
								continue;
							}
							if("租金".equals(keyList.get(j).get("FICB_ITEM"))) {
								insertMap.put("bill_code","RENT");
							} else if("增值税".equals(keyList.get(j).get("FICB_ITEM"))) {
								insertMap.put("bill_code","VALUE_ADD_TAX");
							}
							insertMap.put("should_price",keyList.get(j).get("SHOULD_PRICE"));
							insertMap.put("decompose_from","客户");
							
							insertMap.put("ficb_id",keyList.get(j).get("FICB_ID"));
							insertMap.put("decompose_status",1);
							insertMap.put("decompose_price",keyList.get(j).get("DECOMPOSE_PRICE"));
							long billId=(Long)sqlMapper.insert("rentFinance.insertRentDecompose",insertMap);
							
							insertMap.put("bill_id",billId);
							insertMap.put("decompose_status",0);
							sqlMapper.insert("rentFinance.insertRent",insertMap);
						}
						sqlMapper.commitTransaction();
						
						
						List decomposeBillList = (List) DataAccessor.query(
								"decompose.queryItemMoney", map,
								DataAccessor.RS_TYPE.LIST);
						String ficb_item = "";
						if (decomposeBillList != null) {
							for (int k = 0; k < decomposeBillList.size(); k++) {
								ficb_item = ((Map) decomposeBillList.get(k)).get(
										"FICB_ITEM").toString();
								context.contextMap.put("real_price", Double
										.valueOf(((Map) decomposeBillList.get(k)).get(
												"REAL_PRICE").toString()));
								context.contextMap.put("recp_id", Integer
										.valueOf(((Map) decomposeBillList.get(k)).get(
												"RECP_ID").toString()));
								context.contextMap.put("period_num", Integer
										.valueOf(((Map) decomposeBillList.get(k)).get(
												"RECD_PERIOD").toString()));
							}														
							
						}
						} catch (Exception e) {
							try {
								sqlMapper.endTransaction();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							fail=true;
							resultStr.append(this.comeName+"("+this.fiin_id+"),");
							e.printStackTrace();
							LogPrint.getLogStackTrace(e, logger);
						}
					}
				}else{
					resultStr.append("没有可分解支付表!");
					outputMap.put("returnStr", resultStr.toString());
					Output.jsonOutput(outputMap, context);
					return;
				}
		}else{
			resultStr.append("没有来款记录!");
			outputMap.put("returnStr", resultStr.toString());
			Output.jsonOutput(outputMap, context);
			return;
		}
		
		if(fail) {
			resultStr.append("以上来款自动分解未成功,请手动分解!");
		} else {
			resultStr.append("自动分解成功!");
		}
		
		outputMap.put("returnStr", resultStr.toString());
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 查询所有来款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showAllIncomeInfoAjax(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List bankList = null;
		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (cardFlag == 0) {
			context.contextMap.put("search_status", 2);
		} else if (cardFlag == 1) {
			context.contextMap.put("search_status", 3);
		} else if (cardFlag == 2) {
			context.contextMap.put("search_status", 4);
		} else if (cardFlag == 3) {
			context.contextMap.put("search_status", 5);
		} else if (cardFlag == 4) {
			context.contextMap.put("search_status", 1);
		} else if (cardFlag == 5) {
			context.contextMap.put("search_status", 6);
		} else if (cardFlag == 6) {
			context.contextMap.put("search_status", 7);
		} else if (cardFlag == 7) {
			context.contextMap.put("search_status", 0);
		}

		DataWrap incomeListPage = null;
		// 查询条件
		String search_startdate = null;
		String search_enddate = null;
		String search_startmoney = null;
		String search_endmoney = null;
		String search_status = null;
		String search_bankname = null;
		String search_bankno = null;
		String search_content = null;
		if (context.contextMap.get("search_startdate") == null) {
			search_startdate = "";
		} else {
			search_startdate = context.contextMap.get("search_startdate")
					.toString();
			search_startdate.trim();
		}
		if (context.contextMap.get("search_enddate") == null) {
			search_enddate = "";
		} else {
			search_enddate = context.contextMap.get("search_enddate")
					.toString();
			search_enddate.trim();
		}
		if (context.contextMap.get("search_startmoney") == null) {
			search_startmoney = "";
		} else {
			search_startmoney = context.contextMap.get("search_startmoney")
					.toString();
			search_startmoney.trim();
		}
		if (context.contextMap.get("search_endmoney") == null) {
			search_endmoney = "";
		} else {
			search_endmoney = context.contextMap.get("search_endmoney")
					.toString();
			search_endmoney.trim();
		}
		if (context.contextMap.get("search_status") == null) {
			search_status = "";
		} else {
			search_status = context.contextMap.get("search_status").toString();
			search_status.trim();
		}
		if (context.contextMap.get("search_bankname") == null) {
			search_bankname = "";
		} else {
			search_bankname = context.contextMap.get("search_bankname")
					.toString();
			search_bankname.trim();
		}
		if (context.contextMap.get("search_bankno") == null) {
			search_bankno = "";
		} else {
			search_bankno = context.contextMap.get("search_bankno").toString();
			search_bankno.trim();
		}
		if (context.contextMap.get("search_content") == null) {
			search_content = "";
		} else {
			search_content = context.contextMap.get("search_content")
					.toString();
			search_content.trim();
		}

		if (errorList.isEmpty()) {
			try {
				incomeListPage = (DataWrap) DataAccessor.query(
						"decompose.queryAllIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.PAGED);
				bankList = (List) DataAccessor.query("decompose.queryBankName",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.DecomposeManager.showAllIncomeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.DecomposeManager.showAllIncomeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("bankList", bankList);
			outputMap.put("dw", incomeListPage);
			outputMap.put("search_startdate", search_startdate);
			outputMap.put("search_enddate", search_enddate);
			outputMap.put("search_startmoney", search_startmoney);
			outputMap.put("search_endmoney", search_endmoney);
			outputMap.put("search_status", cardFlag);
			outputMap.put("search_bankname", search_bankname);
			outputMap.put("search_bankno", search_bankno);
			outputMap.put("search_content", search_content);
			outputMap.put("cardFlag", cardFlag);
			outputMap.put("__action", context.contextMap.get("__action"));
			Output.jsonOutput(outputMap, context);
		}
	}
	
	
	//第一次加载查询中国银行当日的所有分解款
	@SuppressWarnings("unchecked")
	public void finaComeViewDef(Context context)
	{
		Map bankOne=new HashMap();
		try
		{
			bankOne=(Map) DataAccessor.query("decompose.findAllBankTopOne", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		//String bank="中国银行";
		String bank="";
		if(bankOne!=null)
		{
			if(bankOne.size()>0)
			{
				//Object BANKS=bankOne.get("OPPOSING_BANKNAME");
				Object BANKS=bankOne.get("BANK_NAME");
				if(BANKS!=null)
				{
					bank=BANKS.toString();
				}
			}
		}
		
		
		
		String ficb_type="待分解来款";
		
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String dateState=format.format(new Date());
		
		context.contextMap.put("ficb_type", ficb_type);
		if(context.contextMap.get("typeState")==null || "".equals(context.contextMap.get("typeState")))
		{
			context.contextMap.put("bank", bank);
			context.contextMap.put("dateState",dateState);
		}
		
		
		Map outputMap = new HashMap();
		Map finaComeByDay=new HashMap();//查询当日来款
		Map finaComeByOldDay=new HashMap();//查前日待分解额
		List finaComeAllBank=new ArrayList();//查询所有银行
		List finaComeAllRent=new ArrayList();//查前当日所有财务已确认的分解单
		List finaComeAllMoney=new ArrayList();//查前当日所有财务已确认的分解单各项金额
		
		Map findAllNotDecompose=new HashMap(); //查所有未分解Total总额
		
		//List recpAndIncomeMoney=new ArrayList();//为每一个支付表填充一个带分解来款
		
		List errList = context.errList ;
		try {
			context.contextMap.put("daifenjielaikuan", "待分解来款") ;
			finaComeByDay = (Map) DataAccessor.query("decompose.findAllDayFinaCome", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaComeByOldDay = (Map) DataAccessor.query("decompose.findAllOldDayNotFinaCome", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaComeAllBank=(List) DataAccessor.query("decompose.findAllBank", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaComeAllRent=(List) DataAccessor.query("decompose.findAllDayFinaComeOk", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaComeAllMoney=(List) DataAccessor.query("decompose.findAllDayFinaComeOkMoney", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//搜索所有未分解未使用过的来款
			outputMap.put("finaIncome", DataAccessor.query("decompose.queryNotDecomposeFinaIncome1", context.contextMap, DataAccessor.RS_TYPE.LIST) );
			
			//Add by Michael 2012 1/4 增加所有未分解额
			findAllNotDecompose=(Map)DataAccessor.query("decompose.findAllNotDecompose", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
//			//循环finaComeAllMoney，为一条来款分解了多个支付表计算不同的未分解来款
//			int fiinId=0;//判断同一条来款
//			double innercome=0d;//判断同一条来款在分解后所剩下的钱
//			int recpid=0;//判断同一个支付表
//			for(int i=0;i<finaComeAllMoney.size();i++)
//			{
//				Map finacome=(HashMap)finaComeAllMoney.get(i);
//				
//				if(i==0)//第一条
//				{
//					recpid=Integer.parseInt(finacome.get("RECP_ID").toString());
//					fiinId=Integer.parseInt(finacome.get("FIIN_ID").toString());
//					Object INNERCOME=finacome.get("INCOME_MONEY");
//					if(INNERCOME==null || INNERCOME=="")
//					{
//						innercome=0d;
//					}
//					else
//					{
//						Object INNERCOMECURR=finacome.get("REAL_PRICE");
//						double innercomecurr=0d;
//						if(INNERCOMECURR==null || INNERCOMECURR=="")
//						{
//							innercomecurr=0d;
//						}
//						else
//						{
//							innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//						}
//						
//						if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//						{
//							innercome=Double.parseDouble(INNERCOME.toString())-innercomecurr;
//						}
//						
//					}
//					
//					if(finaComeAllMoney.size()==0)
//					{
//						Map recpMap=new HashMap();
//						recpMap.put("RECP", recpid);
//						recpMap.put("RECPMONEY", innercome);
//						recpAndIncomeMoney.add(recpMap);
//					}
//				}
//				else if(i>0)
//				{
//					//当不是同一支付表时作出相应的变化
//					int recpid1=Integer.parseInt(finacome.get("RECP_ID").toString());
//					int fiinId1=Integer.parseInt(finacome.get("FIIN_ID").toString());
//					
//					if(recpid==recpid1)//同一支付表
//					{
//						;
//					}
//					else//不同支付表
//					{
//						Map recpMap=new HashMap();
//						recpMap.put("RECP", recpid);
//						recpMap.put("RECPMONEY", innercome);
//						recpAndIncomeMoney.add(recpMap);
//					}
//					
//					
//					
//					
//					if(fiinId1==fiinId)//如果是同一条来款
//					{
//						Object INNERCOMECURR=finacome.get("REAL_PRICE");
//						double innercomecurr=0d;
//						if(INNERCOMECURR==null || INNERCOMECURR=="")
//						{
//							innercomecurr=0d;
//						}
//						else
//						{
//							innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//						}
//						
//						if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//						{
//							innercome=innercome-innercomecurr;
//						}
//					}
//					else
//					{
//						
//						//档不是同一来款时数据重置
//						fiinId=Integer.parseInt(finacome.get("FIIN_ID").toString());
//						Object INNERCOME=finacome.get("INCOME_MONEY");
//						if(INNERCOME==null || INNERCOME=="")
//						{
//							innercome=0d;
//						}
//						else
//						{
//							Object INNERCOMECURR=finacome.get("REAL_PRICE");
//							double innercomecurr=0d;
//							if(INNERCOMECURR==null || INNERCOMECURR=="")
//							{
//								innercomecurr=0d;
//							}
//							else
//							{
//								innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//							}
//							
//							if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//							{
//								innercome=Double.parseDouble(INNERCOME.toString())-innercomecurr;
//							}
//							
//						}
//					}
//				}
//				
//				
//			}
//			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("数据分析--每日来款租金分解明细表初始化错误!请联系管理员") ;
		}
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			//System.out.println("-------------=================="+finaComeAllRent.size());
			outputMap.put("finaComeByDay", finaComeByDay);
			outputMap.put("finaComeByOldDay", finaComeByOldDay);
			outputMap.put("finaComeAllBank", finaComeAllBank);
			outputMap.put("finaComeAllRent", finaComeAllRent);
			outputMap.put("finaComeAllMoney", finaComeAllMoney);
			outputMap.put("bank", context.contextMap.get("bank"));
			outputMap.put("dateState", context.contextMap.get("dateState"));
			
			outputMap.put("findAllNotDecompose", findAllNotDecompose);
			
			//outputMap.put("recpAndIncomeMoney", recpAndIncomeMoney);
			Output.jspOutput(outputMap, context, "/decompose/showDayIncomeView.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	//导出Excel表格
	@SuppressWarnings("unchecked")
	public void finaComeViewExcel(Context context)
	{
		List fatherList=new ArrayList();
		
		
		String ficb_type="待分解来款";
		
		context.contextMap.put("ficb_type", ficb_type);
		
		Map outputMap = new HashMap();
		Map finaComeByDay=new HashMap();//查询当日来款
		Map finaComeByOldDay=new HashMap();//查前日待分解额
		List finaComeAllBank=new ArrayList();//查询所有银行
		List finaComeAllRent=new ArrayList();//查前当日所有财务已确认的分解单
		List finaComeAllMoney=new ArrayList();//查前当日所有财务已确认的分解单各项金额
		List finaIncome = new ArrayList() ;//查询所有当日未使用过的来款
		
		//List recpAndIncomeMoney=new ArrayList();//为每一个支付表填充一个带分解来款
		
		List errList = context.errList ;
		try {
			context.contextMap.put("daifenjielaikuan", "待分解来款") ;
			finaComeAllBank=(List) DataAccessor.query("decompose.findAllBank", context.contextMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<finaComeAllBank.size();i++)
			{
				Map bankMap=(Map)finaComeAllBank.get(i);
				if(bankMap.get("OPPOSING_BANKNAME")!=null && !(bankMap.get("OPPOSING_BANKNAME").equals(""))){
					context.contextMap.put("bank", bankMap.get("OPPOSING_BANKNAME").toString());
				} else {
					context.contextMap.put("bank", null) ;
				}
				finaComeByDay = (Map) DataAccessor.query("decompose.findAllDayFinaCome", context.contextMap, DataAccessor.RS_TYPE.MAP);
				finaComeByOldDay = (Map) DataAccessor.query("decompose.findAllOldDayNotFinaCome", context.contextMap, DataAccessor.RS_TYPE.MAP);
				finaComeAllRent=(List) DataAccessor.query("decompose.findAllDayFinaComeOk", context.contextMap, DataAccessor.RS_TYPE.LIST);
				finaComeAllMoney=(List) DataAccessor.query("decompose.findAllDayFinaComeOkMoney", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//搜索所有未分解未使用过的来款
				finaIncome = (List) DataAccessor.query("decompose.queryNotDecomposeFinaIncome1", context.contextMap, DataAccessor.RS_TYPE.LIST );
		
				
				
//				//循环finaComeAllMoney，为一条来款分解了多个支付表计算不同的未分解来款
//				int fiinId=0;//判断同一条来款
//				double innercome=0d;//判断同一条来款在分解后所剩下的钱
//				int recpid=0;//判断同一个支付表
//				for(int j=0;j<finaComeAllMoney.size();j++)
//				{
//					System.out.println("yunxinglea!!!");
//					Map finacome=(HashMap)finaComeAllMoney.get(j);
//					
//					if(j==0)//第一条
//					{
//						recpid=Integer.parseInt(finacome.get("RECP_ID").toString());
//						fiinId=Integer.parseInt(finacome.get("FIIN_ID").toString());
//						Object INNERCOME=finacome.get("INCOME_MONEY");
//						if(INNERCOME==null || INNERCOME=="")
//						{
//							innercome=0d;
//						}
//						else
//						{
//							Object INNERCOMECURR=finacome.get("REAL_PRICE");
//							double innercomecurr=0d;
//							if(INNERCOMECURR==null || INNERCOMECURR=="")
//							{
//								innercomecurr=0d;
//							}
//							else
//							{
//								innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//							}
//							
//							if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//							{
//								innercome=Double.parseDouble(INNERCOME.toString())-innercomecurr;
//							}
//							
//						}
//						
//						if(finaComeAllMoney.size()==0)
//						{
//							Map recpMap=new HashMap();
//							recpMap.put("RECP", recpid);
//							recpMap.put("RECPMONEY", innercome);
//							recpAndIncomeMoney.add(recpMap);
//						}
//					}
//					else if(j>0)
//					{
//						//当不是同一支付表时作出相应的变化
//						int recpid1=Integer.parseInt(finacome.get("RECP_ID").toString());
//						int fiinId1=Integer.parseInt(finacome.get("FIIN_ID").toString());
//						
//						if(recpid==recpid1)//同一支付表
//						{
//							System.out.println("sseeeeeeeeeeeeeeeeesssssssssssssss1111111111");;
//						}
//						else//不同支付表
//						{
//							Map recpMap=new HashMap();
//							recpMap.put("RECP", recpid);
//							recpMap.put("RECPMONEY", innercome);
//							recpAndIncomeMoney.add(recpMap);
//							System.out.println("sseeeeeeeeeeeeeeeeesssssssssssssss");
//						}
//						
//						
//						
//						
//						if(fiinId1==fiinId)//如果是同一条来款
//						{
//							Object INNERCOMECURR=finacome.get("REAL_PRICE");
//							double innercomecurr=0d;
//							if(INNERCOMECURR==null || INNERCOMECURR=="")
//							{
//								innercomecurr=0d;
//							}
//							else
//							{
//								innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//							}
//							
//							if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//							{
//								innercome=innercome-innercomecurr;
//							}
//						}
//						else
//						{
//							
//							//档不是同一来款时数据重置
//							fiinId=Integer.parseInt(finacome.get("FIIN_ID").toString());
//							Object INNERCOME=finacome.get("INCOME_MONEY");
//							if(INNERCOME==null || INNERCOME=="")
//							{
//								innercome=0d;
//							}
//							else
//							{
//								Object INNERCOMECURR=finacome.get("REAL_PRICE");
//								double innercomecurr=0d;
//								if(INNERCOMECURR==null || INNERCOMECURR=="")
//								{
//									innercomecurr=0d;
//								}
//								else
//								{
//									innercomecurr=Double.parseDouble(INNERCOMECURR.toString());
//								}
//								
//								if(!finacome.get("FICB_ITEM").equals("待分解来款"))//不是带分解来款
//								{
//									innercome=Double.parseDouble(INNERCOME.toString())-innercomecurr;
//								}
//								
//							}
//						}
//					}
//					
//					
//				}
//				
				
				
				Map listMap=new HashMap();
				
				listMap.put("bank", context.contextMap.get("bank"));
				listMap.put("finaComeByDay", finaComeByDay);
				listMap.put("finaComeByOldDay", finaComeByOldDay);
				listMap.put("finaComeAllRent", finaComeAllRent);
				listMap.put("finaComeAllMoney", finaComeAllMoney);
				listMap.put("finaIncome", finaIncome);
				//listMap.put("recpAndIncomeMoney", recpAndIncomeMoney);
				//System.out.println("+++++++++++++++recpAndIncomeMoney="+recpAndIncomeMoney.size());
				
				fatherList.add(listMap);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			//添加详细错误信息
			errList.add("数据分析--每日来款租金分解明细表导出Excel错误!请联系管理员") ;
		}
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			ShowDayIncomeExcel show=new ShowDayIncomeExcel();
			show.INcomeExcelJoin(fatherList,context);
			
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	@SuppressWarnings("unchecked")
	public void updateOpposingUnit(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			DataAccessor.execute("decompose.updateOpposinUnit", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("AJAX修改来款名称错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("content", "修改成功！") ;
			Output.jsonOutput(outputMap, context) ;
		}else {
			outputMap.put("content", "修改失败！") ;
			Output.jsonOutput(outputMap, context) ;
		}
	}
	//修改来款帐号
	@SuppressWarnings("unchecked")
	public void updateBankNoUnit(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			DataAccessor.execute("decompose.updateBankNoUnit", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("AJAX修改来款名称错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("content", "修改成功！") ;
			Output.jsonOutput(outputMap, context) ;
		}else {
			outputMap.put("content", "修改失败！") ;
			Output.jsonOutput(outputMap, context) ;
		}
	}
	//修改虚拟帐号
	@SuppressWarnings("unchecked")
	public void updateVIRTUAL_CODE(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			DataAccessor.execute("decompose.updateVIRTUAL_CODE", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("AJAX修改来款名称错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("content", "修改成功！") ;
			Output.jsonOutput(outputMap, context) ;
		}else {
			outputMap.put("content", "修改失败！") ;
			Output.jsonOutput(outputMap, context) ;
		}
	}	
	@SuppressWarnings("unchecked")
	public void queryNotDecomposeFinaIncome(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			context.contextMap.put("daifenjielaikuan", "待分解来款") ;
			outputMap.put("dw", DataAccessor.query("decompose.queryNotDecomposeFinaIncome", context.contextMap, DataAccessor.RS_TYPE.PAGED) );
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("业务支撑-查询来款错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			outputMap.put("OPPOSING_UNIT",context.contextMap.get("OPPOSING_UNIT")) ;
			outputMap.put("dateBegin",context.contextMap.get("dateBegin")) ;
			outputMap.put("dateEnd",context.contextMap.get("dateEnd")) ;
			outputMap.put("moneyBegin",context.contextMap.get("moneyBegin")) ;
			outputMap.put("moneyEnd",context.contextMap.get("moneyEnd")) ;
			Output.jspOutput(outputMap, context, "/decompose/updateOpposingNameManage.jsp") ;
		}else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void ajaxCheckCustNameIsDecompose(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			outputMap.put("CUST_CODE", DataAccessor.query("decompose.checkCustNameIsDecompose", context.contextMap, RS_TYPE.OBJECT)) ;
		} catch (Exception e) {
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("租金分解-检查客户姓名错误！请联系管理员") ;
		}
		Output.jsonOutput(outputMap, context) ;
	}
	
	/*
	 * Add by Michael 2011 12/23 For 保证金冲错账，将数据还原到原始状态
	 */
	//------------------------------------------------------------------------------------
	public void recoverDecomposePirce(Context context){
		List errList = context.errList ;
		Map outputMap = new HashMap() ;		
		SqlMapClient sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			sqlMapper.update("decompose.updateIncomeDecomposeStatusTemp", context.contextMap);
			sqlMapper.delete("decompose.deleteDecomposeIncomeTemp", context.contextMap);
			sqlMapper.delete("decompose.deleteDecomposeBillTemp", context.contextMap);
			sqlMapper.update("decompose.updateCollectionDetailTemp", context.contextMap);
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("租金分解-恢复资金错误！请联系管理员") ;
		}
		finally{
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				logger.debug(e);
			}
		}
		if(errList.isEmpty()){
			outputMap.put("returnStr", "修改成功！") ;
		}else {
			outputMap.put("returnStr", "修改失败！") ;
		}
		Output.jspOutput(outputMap, context, "/decompose/recoverDecomposePirce.jsp") ;
	}
	//------------------------------------------------------------------------------------
	
	/*
	 * Add By Michael 2011 12/28  增加查询租金罚息明细
	 * 根据支付表号查当前支付表的罚息状况
	 */
	public void showDunDetail(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List writeBackDetails = null;
		if(errList.isEmpty()){		
			try {	
				writeBackDetails = (List) DataAccessor.query("decompose.findDunDetailByRecpCode", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("资金管理--查询支付表租金罚息错误!请联系管理员");
			}
		}
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}	
	
	//Add By Michael 2011 12/30 查询所有的剩余本金、利息
	public void getAllOwnRenPrice(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List allOwnRenPrice = null;	
		List allCollectionPlanList=null;
		if(errList.isEmpty()){		
			try {
				
				allCollectionPlanList = (List) DataAccessor.query(
						"decompose.getAllCollectionPlan", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				for (int i = 0; i < allCollectionPlanList.size(); i++) {
					Map where = (Map) allCollectionPlanList.get(i);
					context.contextMap.put("RECP_ID", where.get("RECP_ID"));
					context.contextMap.put("RECP_CODE", where.get("RECP_CODE"));
					context.contextMap.put("zujin", "租金") ;
					allOwnRenPrice = (List) DataAccessor.query("decompose.getAllOwnRenPrice", context.contextMap, DataAccessor.RS_TYPE.LIST);
					Map temp=(Map)allOwnRenPrice.get(0);
					context.contextMap.put("SUM_OWN_PRICE", temp.get("SUM_OWN_PRICE"));
					context.contextMap.put("SUM_REN_PRICE", temp.get("SUM_REN_PRICE"));
					sqlMapper.insert("decompose.insertIntoOwnPrice", context.contextMap);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("资金管理--查询所有的剩余本金、利息!请联系管理员");
			}
		}
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	
	//Add by Michael 2012-3-2 每日销账报表
	@SuppressWarnings("unchecked")
	public void dailyFinaDecomposeReport(Context context)
	{
		String ficb_type="待分解来款";		
		context.contextMap.put("ficb_type", ficb_type);
		
		Map outputMap = new HashMap();
		//---设备报表
		List finaTodayFinaIncomeID=new ArrayList();;//设备现金销账来款ID
		List finaTodayDecompose=new ArrayList();;//设备现金销账明细
		List finaTemporaryFinaIncomeID=new ArrayList();;//设备暂收款销账来款ID
		List finaTemporaryDecomposeDetail=new ArrayList();;//设备暂收款销账明细
		
		List payFeeListAll =new ArrayList();//费用设定明细
		
		//---重车报表
		List finaCarTodayFinaIncomeID=new ArrayList();;//重车现金销账来款ID
		List finaCarTodayDecompose=new ArrayList();;//重车现金销账明细
		List finaCarTemporaryFinaIncomeID=new ArrayList();;//重车暂收款销账来款ID
		List finaCarTemporaryDecomposeDetail=new ArrayList();;//重车暂收款销账明细	
		
		Integer payFeeCount=0;
		
		List errList = context.errList ;
		try {
			finaTodayFinaIncomeID = (List) DataAccessor.query("decompose.getAllTodayFinaIncomeID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaTodayDecompose = (List) DataAccessor.query("decompose.getAllTodayDecomposeDetail", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaTemporaryFinaIncomeID=(List) DataAccessor.query("decompose.getAllTemporaryFinaIncomeID", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaTemporaryDecomposeDetail=(List) DataAccessor.query("decompose.getAllTemporaryDecomposeDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			payFeeCount=(Integer) DataAccessor.query("decompose.getAllPayFeeList_count", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			payFeeListAll=(List) DataAccessor.query("decompose.getAllPayFeeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			finaCarTodayFinaIncomeID = (List) DataAccessor.query("decompose.getAllCarTodayFinaIncomeID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaCarTodayDecompose = (List) DataAccessor.query("decompose.getAllCarTodayDecomposeDetail", context.contextMap, DataAccessor.RS_TYPE.MAP);
			finaCarTemporaryFinaIncomeID=(List) DataAccessor.query("decompose.getAllCarTemporaryFinaIncomeID", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaCarTemporaryDecomposeDetail=(List) DataAccessor.query("decompose.getAllCarTemporaryDecomposeDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("资金管理--每日资金分解报表出错!请联系管理员");			
		}
		
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			outputMap.put("finaTodayFinaIncomeID", finaTodayFinaIncomeID);
			outputMap.put("finaTodayDecompose", finaTodayDecompose);
			outputMap.put("finaTemporaryFinaIncomeID", finaTemporaryFinaIncomeID);
			outputMap.put("finaTemporaryDecomposeDetail", finaTemporaryDecomposeDetail);

			outputMap.put("finaCarTodayFinaIncomeID", finaCarTodayFinaIncomeID);
			outputMap.put("finaCarTodayDecompose", finaCarTodayDecompose);
			outputMap.put("finaCarTemporaryFinaIncomeID", finaCarTemporaryFinaIncomeID);
			outputMap.put("finaCarTemporaryDecomposeDetail", finaCarTemporaryDecomposeDetail);
			
			outputMap.put("payFeeCount", payFeeCount);
			outputMap.put("payFeeListAll", payFeeListAll);
			
			Output.jspOutput(outputMap, context, "/decompose/dailyFinaDecomposeReport.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 2012/03/01 Yang Yun
	 * 暂收款认领，退款。
	 * @param context
	 */
	public void showFundsForClaim(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> bankList = null;
		PagingInfo<Object> funds = null;
		String search_startdate = null;
		String search_enddate = null;
		String search_startmoney = null;
		String search_endmoney = null;
		String search_bankname = null;
		String search_bankno = null;
		String search_content = null;
		String[] search_status = null;
		StringBuffer sb_status = null;
		String final_status = null;
		try {
			
			if (context.contextMap.get("search_startdate") == null) {
				search_startdate = "";
			} else {
				search_startdate = context.contextMap.get("search_startdate")
						.toString();
				search_startdate.trim();
			}
			if (context.contextMap.get("search_enddate") == null) {
				search_enddate = "";
			} else {
				search_enddate = context.contextMap.get("search_enddate")
						.toString();
				search_enddate.trim();
			}
			if (context.contextMap.get("search_startmoney") == null) {
				search_startmoney = "";
			} else {
				search_startmoney = context.contextMap.get("search_startmoney")
						.toString();
				search_startmoney.trim();
			}
			if (context.contextMap.get("search_endmoney") == null) {
				search_endmoney = "";
			} else {
				search_endmoney = context.contextMap.get("search_endmoney")
						.toString();
				search_endmoney.trim();
			}
			
			search_status = context.request.getParameterValues("search_status");
			if (search_status != null && search_status.length > 0) {
				sb_status = new StringBuffer();
				for (String string : search_status) {
					sb_status.append(string);
					sb_status.append(",");
				}
			} else {
				sb_status = new StringBuffer("1,2,6,");
			}
			final_status = sb_status.substring(0, sb_status.length() -1);
//			if (context.contextMap.get("search_status") == null) {
//				search_status = "";
//			} else {
//				search_status = context.contextMap.get("search_status").toString();
//				search_status.trim();
//			}
			if (context.contextMap.get("search_bankname") == null) {
				search_bankname = "";
			} else {
				search_bankname = context.contextMap.get("search_bankname")
						.toString();
				search_bankname.trim();
			}
			if (context.contextMap.get("search_bankno") == null) {
				search_bankno = "";
			} else {
				search_bankno = context.contextMap.get("search_bankno").toString();
				search_bankno.trim();
			}
			if (context.contextMap.get("search_content") == null) {
				search_content = "";
			} else {
				search_content = context.contextMap.get("search_content")
						.toString();
				search_content.trim();
			}
			context.contextMap.put("status", "(" + final_status + ")");
			funds = baseService.queryForListWithPaging("decompose.getAllForClaimOrReturn", context.contextMap, "OPPOSING_DATE", ORDER_TYPE.DESC);
			bankList = (List) DataAccessor.query("decompose.queryBankName", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//权限验证，认领、退款 和 审批 权限
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Integer resultFlag = null;
			
			String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
			//认领
			outputMap.put("claimRole", baseService.checkAccessForResource("claimRole", user_id));
			//退款
			outputMap.put("returnRole", baseService.checkAccessForResource("returnRole", user_id));
			//审核
			outputMap.put("authRole", baseService.checkAccessForResource("authRole", user_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("dw", funds);
		outputMap.put("bankList", bankList);
		outputMap.put("search_startdate", search_startdate);
		outputMap.put("search_enddate", search_enddate);
		outputMap.put("search_startmoney", search_startmoney);
		outputMap.put("search_endmoney", search_endmoney);
		outputMap.put("search_bankname", search_bankname);
		outputMap.put("search_bankno", search_bankno);
		outputMap.put("search_content", search_content);
		outputMap.put("final_status", final_status);
		outputMap.put("__action", "decompose.showFundsForClaim");
		outputMap.put("fiin_id", context.contextMap.get("fiin_id"));
		outputMap.put("surplus_money", context.contextMap.get("surplus_money"));
		outputMap.put("claimReason", context.contextMap.get("claimReason"));
		outputMap.put("amount", context.contextMap.get("amount"));
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		outputMap.put("errorMsg_rej", context.contextMap.get("errorMsg_rej"));
		outputMap.put("errorMsg_return", context.contextMap.get("errorMsg_return"));
		outputMap.put("fundReturn", context.contextMap.get("fundReturn"));
		outputMap.put("search_memo", context.contextMap.get("search_memo"));
		outputMap.put("search_file", context.contextMap.get("search_file"));
		if("1".equals(context.contextMap.get("BIZ_TYPE"))){
			Output.jspOutput(outputMap, context, "/decompose/fundsManagerForSales.jsp");
		}else{
			Output.jspOutput(outputMap, context, "/decompose/fundsManager.jsp");
		}
		
	}
	
	/**
	 * 财务已确认的认退款记录显示页面
	 * @param context
	 */
	public void fundsAuthSow(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		DataWrap fundsAuthed = null;
		String amount_from = (String) context.contextMap.get("amount_from");
		String amount_to = (String) context.contextMap.get("amount_to");
		String income_money_from = (String) context.contextMap.get("income_money_from");
		String income_money_to = (String) context.contextMap.get("income_money_to");
		String opp_data_from = (String) context.contextMap.get("opp_data_from");
		String opp_data_to = (String) context.contextMap.get("opp_data_to");
		String search_content = (String) context.contextMap.get("search_content");
		String fund_type = (String) context.contextMap.get("fund_type");
		try {
			amount_from = StringUtils.isEmpty(amount_from) ? null : amount_from.trim();
			amount_to = StringUtils.isEmpty(amount_to) ? null : amount_to.trim();
			income_money_from = StringUtils.isEmpty(income_money_from) ? null : income_money_from.trim();
			income_money_to = StringUtils.isEmpty(income_money_to) ? null : income_money_to.trim();
			opp_data_from = StringUtils.isEmpty(opp_data_from) ? null : opp_data_from.trim();
			opp_data_to = StringUtils.isEmpty(opp_data_to) ? null : opp_data_to.trim();
			search_content = StringUtils.isEmpty(search_content) ? null : search_content.trim();
			outputMap.put("amount_from", amount_from);
			outputMap.put("amount_to", amount_to);
			outputMap.put("income_money_from", income_money_from);
			outputMap.put("income_money_to", income_money_to);
			outputMap.put("opp_data_from", opp_data_from);
			outputMap.put("opp_data_to", opp_data_to);
			outputMap.put("search_content", search_content);
			outputMap.put("fund_type", fund_type);
			context.contextMap.put("amount_from", amount_from);
			context.contextMap.put("amount_to", amount_to);
			context.contextMap.put("income_money_from", income_money_from);
			context.contextMap.put("income_money_to", income_money_to);
			context.contextMap.put("opp_data_from", opp_data_from);
			context.contextMap.put("opp_data_to", opp_data_to);
			context.contextMap.put("search_content", search_content);
			context.contextMap.put("fund_type", fund_type);
			fundsAuthed = (DataWrap) DataAccessor.query("decompose.getAllAuthedFunds", context.contextMap, RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("dw", fundsAuthed);
		Output.jspOutput(outputMap, context, "/decompose/fundsAuthSow.jsp");
	}
	
	/**
	 * 2012/03/07 Yang Yun
	 * 提交认领单
	 * @param context
	 */
	public void subFundsClaim(Context context){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String errorMsg = null;
		String surplus_money_str = (String) context.contextMap.get("surplus_money");
		Double surplus_money = null;
		String amount_str = (String) context.contextMap.get("amount");
		Double amount = null;
		String fiin_id = (String) context.contextMap.get("fiin_id");
		String claimReason = (String) context.contextMap.get("claimReason");
		//Add by Michael 2012 07-27 增加认领款是否需要开立发票栏位
		String is_openInvoice = (String) context.contextMap.get("is_openInvoice");
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			surplus_money = StringUtils.isEmpty(surplus_money_str) ? 0D : Double.parseDouble(surplus_money_str);
			amount = StringUtils.isEmpty(amount_str) ? 0D : Double.parseDouble(amount_str);
			fiin_id = StringUtils.isEmpty(fiin_id) ? null : fiin_id.trim();
			claimReason = StringUtils.isEmpty(claimReason) ? null : claimReason.trim();
			is_openInvoice=StringUtils.isEmpty(is_openInvoice) ? null : is_openInvoice.trim();
			paramMap.put("fiin_id", fiin_id);
			paramMap.put("status", "(1,2,6)");
			//验证数据有没有被操作过
			List<Map<String, Object>> result = (List<Map<String, Object>>) sqlMap.queryForList("decompose.queryForCheckForClaim", paramMap);
			if (result == null || result.size() != 1 || ((BigDecimal) result.get(0).get("SURPLUS_MONEY")).doubleValue() != surplus_money) {
				errorMsg = "该款项已被他人销过账，请刷新数据。";
				throw new Exception(errorMsg);
			}
			if (amount > surplus_money) {
				errorMsg = "您申请的金额不能超过剩余金额。";
				throw new Exception(errorMsg);
			}
			if (((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 2 ||
					((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 1) {
				paramMap.put("decompose_status", "6");
				//把暂收款锁定成【认领中6】状态。
				sqlMap.update("decompose.updateIncomeForStatus", paramMap);
			}
			paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
			paramMap.put("claimReason", claimReason);
			paramMap.put("amount", amount);
			
			//Add by Michael 2012 07-27 增加认领款是否需要开立发票栏位
			paramMap.put("is_openInvoice", is_openInvoice);
			//往认领表中新增一条认领记录，状态为【提交0】
			sqlMap.insert("decompose.createClaimTag", paramMap);
			sqlMap.commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("errorMsg", errorMsg);
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		this.showFundsForClaim(context);
	}
	
	/**
	 * 2012/03/07 Yang Yun
	 * 提交退款单
	 * @param context
	 */
	public void subFundsReturn(Context context){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String errorMsg_return = null;
		String surplus_money_str = (String) context.contextMap.get("surplus_money");
		Double surplus_money = null;
		String amount_str = (String) context.contextMap.get("amount");
		String amount_input_str = (String) context.contextMap.get("amount_input");
		Double amount = null;
		Double amount_input = null;
		String fiin_id = (String) context.contextMap.get("fiin_id");
		
		String reason = (String) context.contextMap.get("reason");
		String reason_other = (String) context.contextMap.get("reason_other");
		String remark = (String) context.contextMap.get("remark");
		String payee = (String) context.contextMap.get("payee");
		String return_date = (String) context.contextMap.get("return_date");
		String pay_type = (String) context.contextMap.get("pay_type");
		String bank_name = (String) context.contextMap.get("bank_name");
		String bank_account = (String) context.contextMap.get("bank_account");
		
		Double handling_charge = StringUtils.isEmpty(context.contextMap.get("handling_charge")) ? 
				0 : Double.parseDouble((String) context.contextMap.get("handling_charge"));
		
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			surplus_money = StringUtils.isEmpty(surplus_money_str) ? 0D : Double.parseDouble(surplus_money_str);
			amount = StringUtils.isEmpty(amount_str) ? 0D : Double.parseDouble(amount_str);
			amount_input = StringUtils.isEmpty(amount_input_str) ? 0D : Double.parseDouble(amount_input_str);
			fiin_id = StringUtils.isEmpty(fiin_id) ? null : fiin_id.trim();
			reason = StringUtils.isEmpty(reason) ? null : reason.trim();
			reason_other = StringUtils.isEmpty(reason_other) ? null : reason_other.trim();
			remark = StringUtils.isEmpty(remark) ? null : remark.trim();
			payee = StringUtils.isEmpty(payee) ? null : payee.trim();
			return_date = StringUtils.isEmpty(return_date) ? null : return_date.trim();
			pay_type = StringUtils.isEmpty(pay_type) ? null : pay_type.trim();
			bank_name = StringUtils.isEmpty(bank_name) ? null : bank_name.trim();
			bank_account = StringUtils.isEmpty(bank_account) ? null : bank_account.trim();
			if ("-1".equals(reason)) {
				reason = reason_other;
			}
			paramMap.put("fiin_id", fiin_id);
			paramMap.put("surplus_money", surplus_money);
			paramMap.put("status", "(1,2,6)");
			paramMap.put("reason", reason);
			paramMap.put("amount", amount);
			paramMap.put("remark", remark);
			paramMap.put("payee", payee);
			paramMap.put("pay_type", pay_type);
			paramMap.put("bank_name", bank_name);
			paramMap.put("bank_account", bank_account);
			paramMap.put("amount_input", amount_input);
			paramMap.put("handling_charge", handling_charge);
			if (amount_input > 0 && amount != getRealAmount(amount_input)) {
				errorMsg_return = "‘申请退款金额’和‘实际退款金额’不符合手续费收取规则。";
				throw new Exception(errorMsg_return);
			}
			//验证数据有没有被操作过
			List<Map<String, Object>> result = (List<Map<String, Object>>) sqlMap.queryForList("decompose.queryForCheckForClaim", paramMap);
			if (result == null || result.size() != 1 || ((BigDecimal) result.get(0).get("SURPLUS_MONEY")).doubleValue() != surplus_money) {
				errorMsg_return = "该款项已被他人销过账，请刷新数据。";
				throw new Exception(errorMsg_return);
			}
			if (amount > surplus_money) {
				errorMsg_return = "您申请的金额不能超过剩余金额。";
				throw new Exception(errorMsg_return);
			}
			if (amount_input > surplus_money) {
				errorMsg_return = "您申请的金额不能超过剩余金额。";
				throw new Exception(errorMsg_return);
			}
			if (((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 2 ||
					((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 1) {
				paramMap.put("decompose_status", "6");
				//把暂收款锁定成【认领中6】状态。
				sqlMap.update("decompose.updateIncomeForStatus", paramMap);
			}
			paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
			paramMap.put("return_date", new SimpleDateFormat("yyyy-MM-dd").parse(return_date));
			//Modify by Michael 2012 08-30 增加退款单流水号
			//退款单流水号编码规则年、月 加3码流水码
			paramMap.put("RUNCODE", CodeRule.geneFundsReturnCode());
			//往认领表中新增一条认领记录，状态为【提交0】
			Long result_id = (Long) sqlMap.insert("decompose.createReturnTag", paramMap);
			
			/*
			 * Add By Michael 2013 04-11
			 * 申请退款时往付款记录表中增加一条记录
			 */
			paramMap.put("FSS_ID", result_id);
			sqlMap.insert("decompose.insertReturnPayMoney", paramMap);
			
			//插入操作日志
			String memo="申请退款";
			paramMap.put("RETURN_PAY_ID", result_id);
			paramMap.put("memo", memo);
			sqlMap.insert("rentContract.addRenturnMoneyLog", paramMap);
			
			sqlMap.commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("fundReturn", paramMap);
			context.contextMap.put("errorMsg_return", e.getMessage());
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		this.showFundsForClaim(context);
	}
	
	private double getRealAmount(double input){
		double handlingCharge = 0;
		double realAmount = 0;
		double inputAmount = input;
		if(inputAmount <= 10000){
			handlingCharge = 5.5;
		} else if (inputAmount <= 100000) {
			handlingCharge = 10.5;
		} else if (inputAmount <= 500000) {
			handlingCharge = 15.5;
		} else if (inputAmount <= 1000000) {
			handlingCharge = 20.5;
		} else {
			handlingCharge = inputAmount * 0.2 / 1000 + 0.5;
		}
		if(handlingCharge > 200.5){
			handlingCharge = 200.5;
		}
		realAmount = inputAmount - handlingCharge;
		if (realAmount < 0) {
			realAmount = 0;
		}
		return realAmount;
	}
	
	/**
	 * 2012/03/07 Yang Yun 
	 * 显示认退记录
	 * @param context
	 */
	public void showFundsDetail(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = null;
		try {
			resultList = (List<Map<String, Object>>) DataAccessor.query("decompose.showFundsDetail", context.contextMap, RS_TYPE.LIST);
			//权限验证，认领、退款 和 审批 权限
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Integer resultFlag = null;
			//认领
			paramMap.put("res_id", 173);
			paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
			resultFlag = (Integer) DataAccessor.query("permission.checkAccessForRes", paramMap, RS_TYPE.OBJECT);
			if (resultFlag == 1) {
				outputMap.put("claimRole", true);
			}
			//退款
			paramMap.put("res_id", 174);
			resultFlag = (Integer) DataAccessor.query("permission.checkAccessForRes", paramMap, RS_TYPE.OBJECT);
			if (resultFlag == 1) {
				outputMap.put("returnRole", true);
			}
			//审核
			paramMap.put("res_id", 175);
			resultFlag = (Integer) DataAccessor.query("permission.checkAccessForRes", paramMap, RS_TYPE.OBJECT);
			if (resultFlag == 1) {
				outputMap.put("authRole", true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outputMap.put("resultList", resultList);
		outputMap.put("BIZ_TYPE", context.contextMap.get("BIZ_TYPE"));
		
		Output.jspOutput(outputMap, context, "/decompose/fundsDetail.jsp");
	}
	
	/**
	 * 2012/03/07 Yang Yun
	 * 认退记录 驳回
	 * @param context
	 */
	public void rejectFundDetail(Context context){
		String  fund_type = (String) context.contextMap.get("fund_type");
		Integer flag = null;
		String errorMsg = null;
		SqlMapClient sqlMap = null;
		Integer checkResult = null;
		try {
			sqlMap = DataAccessor.getSession();
			sqlMap.startTransaction();
			if ("1".equals(fund_type)) {
				context.contextMap.put("check_status", 0);
				flag = (Integer) sqlMap.queryForObject("decompose.checkForRejectClaim", context.contextMap);
				if (flag != 1) {
					errorMsg = "该款项已被他人审核，请刷新数据。";
					throw new Exception(errorMsg);
				}
				sqlMap.update("decompose.authFundDetailForClaim", context.contextMap);
				checkResult = (Integer) sqlMap.queryForObject("decompose.checkBackStatusForClaim", context.contextMap);
				if (checkResult == 0) {
					//款项没有认退记录了要把状态还原成为分解状态。
					sqlMap.update("decompose.backStatusForClaim", context.contextMap);
				}
			} else if ("2".equals(fund_type)) {
				context.contextMap.put("check_status", 0);
				flag = (Integer) sqlMap.queryForObject("decompose.checkForRejectReturn", context.contextMap);
				if (flag != 1) {
					errorMsg = "该款项已被他人审核，请刷新数据。";
					throw new Exception(errorMsg);
				}
				sqlMap.update("decompose.authFundDetailForReturn", context.contextMap);
				checkResult = (Integer) sqlMap.queryForObject("decompose.checkBackStatusForReturn", context.contextMap);
				if (checkResult == 0) {
					//款项没有认退记录了要把状态还原成为分解状态。
					sqlMap.update("decompose.backStatusForReturn", context.contextMap);
				}
			} else {
				throw new Exception("既不是认领又不是退款，那是什么呢？");
			}
			sqlMap.commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("errorMsg_rej", errorMsg);
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		this.showFundsForClaim(context);
	}
	
	/**
	 * 认退款 财务确认 操作
	 * @param context
	 */
	public void passFundsReturn(Context context){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String surplus_money_str = (String) context.contextMap.get("surplus_money");
		Double surplus_money = null;
		String fiin_id = (String) context.contextMap.get("fiin_id");
		List<Map<String, Object>> fundDetail = null;
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			surplus_money = StringUtils.isEmpty(surplus_money_str) ? 0D : Double.parseDouble(surplus_money_str);
			fiin_id = StringUtils.isEmpty(fiin_id) ? null : fiin_id.trim();
			paramMap.put("fiin_id", fiin_id);
			paramMap.put("surplus_money", surplus_money);
			paramMap.put("status", "(1,2,6)");
			//1.验证数据有没有被操作过,或有没有申请记录
			List<Map<String, Object>> result = (List<Map<String, Object>>) sqlMap.queryForList("decompose.queryForCheckForClaim", paramMap);
			if (result == null || result.size() != 1 || ((BigDecimal) result.get(0).get("SURPLUS_MONEY")).doubleValue() != surplus_money) {
				throw new Exception("该款项已被他人操作过，请刷新数据。");
			}
			if (((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 2 || 
					((BigDecimal)result.get(0).get("DECOMPOSE_STATUS")).doubleValue() == 1) {
				throw new Exception("该款项没有认退申请，无法确认。");
			}
			//2.查询所有需要确认的认退款
			fundDetail = sqlMap.queryForList("decompose.getAllNeedAuth", paramMap);
			if (fundDetail == null || fundDetail.size() == 0) {
				throw new Exception("没有需要审核的人退款");
			}
			//3.遍历待确认的认退款，修改申请认退款明细状态为确认状态(1),并写入分解单表。
			for (Map<String, Object> map : fundDetail) {
				map.put("s_employeeId", context.contextMap.get("s_employeeId"));
				map.put("fiin_id", fiin_id);
				//插入分解单表
				sqlMap.insert("decompose.insertIntoBill", map);
				map.put("detail_id", map.get("ID"));
				map.put("detail_status", 1);
				//修改人退款表状态和保存审核人和时间
				if ("认领款".equals(map.get("FUND_TYPE"))) {
					sqlMap.update("decompose.authFundDetailForClaim", map);
				} else if ("退款".equals(map.get("FUND_TYPE"))) {
					sqlMap.update("decompose.authFundDetailForReturn", map);
				} else {
					throw new Exception("不正确的申请款项。");
				}
			}
			//4.把当前操作的来款状态改成认退确认(7)
			paramMap.put("decompose_status", "7");
			sqlMap.update("decompose.updateIncomeForStatus", paramMap);
			//5.判断剩余金额，不为0，则回写一条来款记录到来款表，金额为剩余金额，状态为待分解(2)，为0则不用回写
			if (surplus_money != 0) {
				Map<String, Object> resultMap = (Map<String, Object>) sqlMap.queryForObject("decompose.getOldIncome", paramMap);
				resultMap.put("INCOME_MONEY", surplus_money);
				resultMap.put("DECOMPOSE_STATUS", 2);
				resultMap.put("LEFT_ID", fiin_id);
				sqlMap.insert("decompose.insertNewIncome", resultMap);
			}
			sqlMap.commitTransaction();
			
		} catch (Exception e) {
			context.contextMap.put("errorMsg_rej", e.getMessage());
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		//加入向新的租金分解表中插数据 add by ShenQi
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		String incomeId="";
		boolean flag=true;
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("select_income_id",fiin_id);
		try {
			while(flag) {
				String leftId=(String)DataAccessor.query("decompose.getLeftId",param,RS_TYPE.OBJECT);
				if(leftId==null||"".equals(leftId)) {
					incomeId=(String)DataAccessor.query("decompose.getOriginalFiinId",param,RS_TYPE.OBJECT);
					flag=false;
				} else {
					param.put("select_income_id",leftId);
				}
			}

			param.put("fiin_id",incomeId);
			param.put("income_id",DataAccessor.query("decompose.getIncomeIdByFiinId",param,RS_TYPE.OBJECT));
			param.put("claimIncomeId",param.get("income_id"));
			param.put("refundIncomeId",param.get("income_id"));
			param.put("recp_id",-1);
			param.put("period_num",0);
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			if(!StringUtils.isEmpty(param.get("income_id"))) {
				sqlMapClient.startTransaction();
				for(int i=0;i<fundDetail.size();i++) {

					String payDate="";
					String billCode="";
					if("认领款".equals(fundDetail.get(i).get("FUND_TYPE"))) {
						Map<String,Object> claimMap=(Map<String,Object>)sqlMapClient.queryForObject("decompose.getClaimDetail",fundDetail.get(i));
						payDate=DateUtil.getCurrentDate();
						billCode=RentFinanceUtil.RENT_TYPE.CLAIM.toString();
						param.put("pay_date",payDate);
						param.put("bill_code",billCode);
						param.put("should_price",claimMap.get("AMOUNT"));
						param.put("decompose_price","-"+claimMap.get("AMOUNT"));
						param.put("decompose_from","客户");
						param.put("decompose_status",2);
						param.put("decompose_type",0);
						param.put("has_red_decompose",0);
						param.put("is_settlement_decompose",0);
						long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",param);//插入租金分解表
						param.put("bill_id",billId);
						param.put("decompose_status",1);
						param.put("table","T_RENT_DECOMPOSE");
						sqlMapClient.insert("rentFinance.insertRent",param);//插入金流表

						param.put("claimMoney",claimMap.get("AMOUNT"));
						param.put("claimReason",claimMap.get("REASON"));
						param.put("claimIsOpenInvoice",claimMap.get("IS_OPENINVOICE"));
						param.put("claimState",1);
						param.put("createTime",claimMap.get("CREATE_DATE"));
						param.put("createBy",claimMap.get("CREATE_BY"));
						param.put("authTime",claimMap.get("AUTH_DATE"));
						param.put("authBy",claimMap.get("AUTH_BY"));
						sqlMapClient.insert("decompose.insertClaim",param);//插入认领款表
					} else if("退款".equals(fundDetail.get(i).get("FUND_TYPE"))) {
						Map<String,Object> refundMap=(Map<String,Object>)sqlMapClient.queryForObject("decompose.getRefundDetail",fundDetail.get(i));
						payDate=refundMap.get("RETURN_DATE")+"";
						billCode=RentFinanceUtil.RENT_TYPE.REFUND.toString();
						param.put("pay_date",payDate);
						param.put("bill_code",billCode);
						param.put("should_price",refundMap.get("AMOUNT"));
						param.put("decompose_price","-"+refundMap.get("AMOUNT"));
						param.put("decompose_from","客户"); 
						param.put("decompose_status",2);
						param.put("decompose_type",0);
						param.put("has_red_decompose",0);
						param.put("is_settlement_decompose",0);
						long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",param);
						param.put("bill_id",billId);
						param.put("decompose_status",1);
						param.put("table","T_RENT_DECOMPOSE");
						sqlMapClient.insert("rentFinance.insertRent",param);//插入金流表

						param.put("serialNumber",refundMap.get("RUNCODE"));
						param.put("amount",refundMap.get("AMOUNT"));
						param.put("payee",refundMap.get("PAYEE"));
						param.put("refundMoney",refundMap.get("AMOUNT_INPUT"));
						param.put("fee",refundMap.get("HANDLING_CHARGE"));
						param.put("refundDate",refundMap.get("RETURN_DATE"));
						param.put("refundType",refundMap.get("REASON"));
						param.put("payWay",refundMap.get("PAY_TYPE"));
						param.put("bankName",refundMap.get("BANK_NAME"));
						param.put("bankAccount",refundMap.get("BANK_ACCOUNT"));
						param.put("remark",refundMap.get("REMARK"));
						param.put("createTime",refundMap.get("CREATE_DATE"));
						param.put("createBy",refundMap.get("CREATE_BY"));
						param.put("authTime",refundMap.get("AUTH_DATE"));
						param.put("authBy",refundMap.get("AUTH_BY"));
						param.put("refundState",1);
						sqlMapClient.insert("decompose.insertRefund",param);//插入退款表
					}
				}
				sqlMapClient.commitTransaction();
			}
		} catch(Exception e1) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
			}
		}
	
		this.showFundsForClaim(context);
	}
	
	/**
	 * 显示退款单 用于打印
	 * @param context
	 */
	public void showFundReturnDetail(Context context){
		String detail_id = (String) context.contextMap.get("detail_id");
		detail_id = StringUtils.isEmpty(detail_id) ? null : detail_id.trim();
		context.contextMap.put("detail_id", detail_id);
		Map<String, Object> resultMap = null;
		try {
			resultMap = (Map<String, Object>) DataAccessor.query("decompose.getFundReturnForPrint", context.contextMap, RS_TYPE.OBJECT);
			resultMap.put("RETURN_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(resultMap.get("RETURN_DATE")));
			resultMap.put("OPPOSING_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(resultMap.get("OPPOSING_DATE")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("resultMap", resultMap);
		Output.jspOutput(outputMap, context, "/decompose/fundReturnDetail.jsp");
	}
	
	//Add by Michael 2012-3-15 每日销账报表
	@SuppressWarnings("unchecked")
	public void dailyFinaDecomposeReportDetail(Context context)
	{
		String ficb_type="待分解来款";		
		context.contextMap.put("ficb_type", ficb_type);
		
		Map outputMap = new HashMap();

		List finaTodayFinaIncome=new ArrayList();//现金销账
		List finaLastDecompose=new ArrayList();//暂收款销账
		List finaLastDynamicDecompose=new ArrayList();//暂收款余额变动表	

		List errList = context.errList ;
		try {
			finaTodayFinaIncome = (List) DataAccessor.query("decompose.getDailyCurrencyDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaLastDecompose = (List) DataAccessor.query("decompose.getLastDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
			finaLastDynamicDecompose=(List) DataAccessor.query("decompose.getLastDynamicDecomposeRpt", context.contextMap, DataAccessor.RS_TYPE.LIST);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("财务报表--销账日报表出错!请联系管理员");			
		}
		
		//判断是否出错后分别执行
		if(errList.isEmpty()){
			outputMap.put("finaTodayFinaIncome", finaTodayFinaIncome);
			outputMap.put("finaLastDecompose", finaLastDecompose);
			outputMap.put("finaLastDynamicDecompose", finaLastDynamicDecompose);
			Output.jspOutput(outputMap, context, "/decompose/showDailyDecomposeView.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}	
	
	
	/*
	 * Add by Michael 2012 4-28
	 * 增加查询与暂收款金额一致支付表信息
	 */
	public void getCustRecpFundReturn(Context context)
	{
		Map outputMap = new HashMap();

		List writeBackDetails = null;	

		List errList = context.errList ;
		try {
			writeBackDetails = (List) DataAccessor.query("decompose.getCustRecpFundReturn", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			DecimalFormat df=new DecimalFormat("#,##0.00");
			for(int i=0;i<writeBackDetails.size();i++) {
				((Map)writeBackDetails.get(i)).put("IRR_MONTH_PRICE",df.format(Double.valueOf(((Map)writeBackDetails.get(i)).get("IRR_MONTH_PRICE").toString())));
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("暂收款管理--查询所有与暂收款金额一致支付表信息!请联系管理员");			
		}
		
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	
	
	/**
	 * 保存附件
	 * 
	 * @param context
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void uploadAll(Context context) throws IOException {
		List fileItems = (List) context.contextMap.get("uploadList");
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			InputStream in = fileItem.getInputStream();
			if (!fileItem.getName().equals("")) {
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem, sqlMapClient);
					sqlMapClient.commitTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} finally {
					try {
						sqlMapClient.endTransaction();
					} catch (SQLException e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
			}
		}
		//上传水单后发送邮件提醒 add by ShenQi
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("select_income_id",context.contextMap.get("fiinID"));
		Map<String,Object> result=new HashMap<String,Object>();
		try {
			result=(Map)DataAccessor.query("decompose.queryFiinInfoByFiinId",param,DataAccessor.RS_TYPE.MAP);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("水单上传");
			mailSettingTo.setEmailTo("HW@tacleasing.cn");
			mailSettingTo.setEmailCc("xujing@tacleasing.cn;zhanglijun@tacleasing.cn");
			mailSettingTo.setEmailContent("系统提示:<br>&nbsp;&nbsp;&nbsp;"+context.contextMap.get("s_employeeName")+"上传了户名为:"+result.get("OPPOSING_UNIT")+
					"(金额"+result.get("INCOME_MONEY")+")的水单!");  
			mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
		}
		
		if(errList.isEmpty()){
			outputMap.put("returnStr", "上传成功");
			this.showFundsForClaim(context);	
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * 
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,
			SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("transferCertificate");
		String file_path = "";
		String file_name = "";
		Long syupId = null;
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
			String excelNewName = FileExcelUpload.getNewFileName();
			File uploadedFile = new File(realPath.getPath() + File.separator
					+ excelNewName + "." + type);
			file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName
					+ "." + type;
			file_name = excelNewName + "." + type;
			try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					contextMap.put("file_path", file_path);
					contextMap.put("file_name", fileItem.getName());
					contextMap.put("title", "暂收款水单附件");

					sqlMapClient.insert(
							"rentFile.insertTransferCertificate", contextMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		return null;
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
	 * 查询下载文件
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryFileUpMore(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List logFileUpList =null;
		Boolean deleteRentFile=false;
			try {
				logFileUpList=(List)DataAccessor.query("rentFile.selectTransferCertificate", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("logFileUpList",logFileUpList);
			Output.jspOutput(outputMap, context, "/decompose/fileUpMore.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	/**
	 * 
	 * @return 下载
	 */
	public void download(Context context) {
		String savaPath = (String) context.contextMap.get("path");
		String name = (String) context.contextMap.get("name");
		String bootPath = this.getUploadPath("transferCertificate");
		String path = bootPath + savaPath;
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			context.response.setHeader("Content-Disposition",
					"attachment; filename="
							+ new String(name.getBytes("gb2312"), "iso8859-1"));

			output = context.response.getOutputStream();
			fis = new FileInputStream(file);

			byte[] b = new byte[1024];
			int i = 0;

			while ((i = fis.read(b)) != -1) {

				output.write(b, 0, i);
			}
			output.write(b, 0, b.length);

			output.flush();
			context.response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				output = null;
			}
		}

	}
	
	/*
	 * Add by Michael 2012 08-29
	 * 重新改版退款单
	 */
	public static Map<String,Object> exportFundReturnDetail(String  detail_id){
		Map paramMap= new HashMap();
		detail_id = StringUtils.isEmpty(detail_id) ? null : detail_id.trim();
		paramMap.put("detail_id", detail_id);
		Map<String, Object> resultMap = null;
		List financeBillList=null;
		try {
			resultMap = (Map<String, Object>) DataAccessor.query("decompose.getFundReturnForPrint", paramMap, RS_TYPE.OBJECT);
			Map financeIncomeCode = (Map) DataAccessor.query("decompose.queryFinanceIncomeCode", resultMap, RS_TYPE.MAP);
			financeBillList = (List) DataAccessor.query("decompose.queryFinanceBillDetailByFiinCode", financeIncomeCode, RS_TYPE.LIST);
			resultMap.put("RETURN_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(resultMap.get("RETURN_DATE")));
			resultMap.put("OPPOSING_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(resultMap.get("OPPOSING_DATE")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("resultMap", resultMap);
		resultMap.put("financeBillList", financeBillList);
		return resultMap;
	}
	
	/**
	 * 保证金、保险费押金冲回管理操作
	 * @author michael
	 * @param context
	 */
	public void pledgeRedBack(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;		
		PagingInfo<Object> dw = null;
			try {
				dw = baseService.queryForListWithPaging("decompose.queryPledgeRedBack", context.contextMap, "RECP_ID", ORDER_TYPE.ASC);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
			//判断是否出错后分别执行
			if(errList.isEmpty()){
				outputMap.put("dw", dw);
				outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
				Output.jspOutput(outputMap, context, "/decompose/pledge_RedBack.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}	
	}

	/**
	 * 执行冲回操作
	 * @author michael
	 * @param context
	 */
	public void doPledgeRedBack(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;		
			try {
				baseService.update("decompose.updatePledgeReturnBackFlag", context.contextMap);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
				
			//判断是否出错后分别执行
			if(errList.isEmpty()){
				outputMap.put("returnStr", "操作成功！");
				Output.jsonOutput(outputMap, context);
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}	
	}
	
	/**
	 * 执行冲回确认操作  （会计操作）
	 * @author michael
	 * @param context
	 */
	public void confirmPledgeRedBack(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;	
		List pledgeBillList =null;
		Map incomeMap=null;
			try {
				pledgeBillList=baseService.queryForList("decompose.queryPledgeBillList", context.contextMap);
				Map tempMap;
				for(int i=0;i<pledgeBillList.size();i++){
					tempMap=(Map) pledgeBillList.get(i);
					tempMap.put("REAL_PRICE", -DataUtil.doubleUtil(tempMap.get("REAL_PRICE")));
					tempMap.put("FICB_TYPE", 1);
					tempMap.put("DEPOSIT_FLAG", 1);
					tempMap.put("IS_PLEDGE_B", 1);
					tempMap.put("IS_PLEDGE_B", 1);
					baseService.insert("decompose.insertPledgeBillList", tempMap);
					
					tempMap.put("REAL_PRICE", -DataUtil.doubleUtil(tempMap.get("REAL_PRICE")));
					tempMap.put("FICB_TYPE", 1);
					tempMap.put("DEPOSIT_FLAG", 1);
					tempMap.put("IS_PLEDGE_B", 1);
					tempMap.put("IS_PLEDGE_B", 1);
					tempMap.put("FICB_ITEM", "待分解来款");
					baseService.insert("decompose.insertPledgeBillList", tempMap);
					
					incomeMap=(Map) baseService.queryForObj("decompose.queryPledgeIncome", tempMap);
					incomeMap.put("INCOME_MONEY", DataUtil.doubleUtil(tempMap.get("REAL_PRICE")));
					incomeMap.put("LEFT_ID", tempMap.get("FIIN_ID"));
					incomeMap.put("DECOMPOSE_STATUS", 2);
					baseService.insert("decompose.insertPledgeIncome", incomeMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
				
			//判断是否出错后分别执行
			if(errList.isEmpty()){
				outputMap.put("returnStr", "操作成功！");
				Output.jsonOutput(outputMap, context);
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}	
	}
	
	public boolean isWorkingDay() throws Exception{
		boolean flag = false;
		Map paramMap=new HashMap();
		paramMap.put("dateStr", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		Map resultMap=null;
		try {
			resultMap=(Map) DataAccessor.query(		
					"businessSupport.getDayType", paramMap,
					DataAccessor.RS_TYPE.MAP);
			if ("WD".equals(String.valueOf(resultMap.get("DAY_TYPE")))) {
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void getFundsDetailByFiinID(Context context)
	{
		Map outputMap = new HashMap();
		Map fundsDetail = null;	

		List errList = context.errList ;
		try {
			fundsDetail = (Map) DataAccessor.query("decompose.getFundsDetailByFiinID", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("暂收款管理--查询所有与暂收款金额一致支付表信息!请联系管理员");			
		}
		
		outputMap.put("fundsDetail", fundsDetail);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	public void getFundsReturnDetailByID(Context context)
	{
		
		Map outputMap = new HashMap();
		Map fundsReturnDetail = null;	

		List errList = context.errList ;
		try {
			if(DateUtil.strToDate(context.contextMap.get("CREATE_DATE").toString(),"yyyy-MM-dd").compareTo(DateUtil.strToDate("2014-2-27","yyyy-MM-dd"))==-1) {
				fundsReturnDetail = (Map) DataAccessor.query("decompose.getFundsReturnDetailByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} else {
				fundsReturnDetail = (Map) DataAccessor.query("rentFinance.getRefundDetail", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("暂收款管理--查询所有与暂收款金额一致支付表信息!请联系管理员");			
		}
		
		outputMap.put("fundsReturnDetail", fundsReturnDetail);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	public void queryReturnMoneyLog(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List logList =null;
			try {
				logList=(List)DataAccessor.query("decompose.getFundsReturnLogList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("logList",logList);
			Output.jspOutput(outputMap, context, "/backMoney/returnMoneyLog.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	//更新不明款邮件备注
	public void updateMailMemo(Context context) {
		
		try {
			DataAccessor.execute("decompose.updateMailMemo",context.contextMap,OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			
		}
		
		this.showFundsForClaim(context);
	}
	
	//每个工作日14点发送不明款邮件提醒
	public void sendUnknownMoney() throws Exception {
		
		if(baseService.isWorkingDay()) {
			try {
				List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("decompose.getUnknownMoney",null,DataAccessor.RS_TYPE.LIST);
				
				StringBuffer mailContent=new StringBuffer();
				mailContent.append("<style>" +
						".grid_table {width : 100%;border-collapse:collapse;border:solid #A6C9E2;border-width:1px 0 0 1px;overflow: hidden;}" +
						".grid_table th {border:solid #A6C9E2;border-width:0 1px 1px 0;background-color: #E1EFFB;padding : 2;margin : 1;font-weight: bold;text-align: center;color: #2E6E9E;height: 28px;font-size: 14px;font-family: '微软雅黑';}" +
						".grid_table tr {cursor: default;overflow: hidden;}" +
						".grid_table td {border:solid #A6C9E2;border-width:0 1px 1px 0;padding : 2;margin : 1;text-align: center;height: 28px;font-size: 12px;font-family: '微软雅黑';}" +
						".grid_table a {color: #0000FF;}" +
						".grid_table a:hover {color: #0000FF;font-weight: bold;text-decoration: underline;}" +
						"</style>");
				mailContent.append("<b style='font-family:微软雅黑'>大家好:<br>&nbsp;&nbsp;&nbsp;以下是不明款明细<br></b>");
				
				mailContent.append("<table class='grid_table'>" +
										"<tr>" +
											"<th style='text-align:center'>序号</th>" +
											"<th style='text-align:center'>来款日期</th>" +
											"<th style='text-align:center'>来款户名</th>" +
											"<th style='text-align:center'>收款帐号</th>" +
											"<th style='text-align:center'>来款金额</th>" +
											"<th style='text-align:center'>剩余金额</th>" +
											"<th style='text-align:center'>备注</th>" +
										"</tr>");
				for(int i=0;resultList!=null&&i<resultList.size();i++) {
					mailContent.append("<tr>" +
							"<td style='text-align:center'>"+(i+1)+"</td>" +
							"<td style='text-align:center'>"+resultList.get(i).get("INCOME_DATE")+"</td>" +
							"<td style='text-align:center'>"+resultList.get(i).get("INCOME_NAME")+"</td>" +
							"<td style='text-align:right'>"+resultList.get(i).get("RECEIPT_ACCOUNT")+"</td>" +
							"<td style='text-align:right'>"+NumberUtils.formatdigital(resultList.get(i).get("INCOME_MONEY")==null?0:Double.valueOf(resultList.get(i).get("INCOME_MONEY").toString()))+"</td>" +
							"<td style='text-align:right'>"+NumberUtils.formatdigital(resultList.get(i).get("REST_MONEY")==null?0:Double.valueOf(resultList.get(i).get("REST_MONEY").toString()))+"</td>" +
							"<td style='text-align:left'>"+StringUtils.autoInsertWrap(resultList.get(i).get("MAIL_MEMO").toString(),8)+"</td>" +
									"</tr>");
						
				}						
				mailContent.append("</table><br><br><b style='font-family:微软雅黑'>Regards</b>");
				
				MailSettingTo mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailContent(mailContent.toString());
				mailUtilService.sendMail(9,mailSettingTo);
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
}