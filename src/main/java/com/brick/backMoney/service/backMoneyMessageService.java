package com.brick.backMoney.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.backMoney.to.PayMoneyTO;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.tag.LabelValueBean;
import com.brick.base.to.CheckedResult;
import com.brick.base.to.DataDictionaryTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.LeaseUtil.CREDIT_LINE_TYPE;
import com.brick.insurance.service.InsuranceService;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.contract.RentContractConstants;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.special.to.CreditSpecialTO;
import com.brick.support.shopping.service.ShoppingService;
import com.brick.support.shopping.to.PayOrderTO;
import com.brick.support.shopping.to.ShoppingCartTO;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class backMoneyMessageService extends BaseCommand {
	
	Log logger = LogFactory.getLog(backMoneyMessageService.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	private InsuranceService insuranceService;
	
	public InsuranceService getInsuranceService() {
		return insuranceService;
	}

	public void setInsuranceService(InsuranceService insuranceService) {
		this.insuranceService = insuranceService;
	}
	
	private ShoppingService shoppingService;

	public ShoppingService getShoppingService() {
		return shoppingService;
	}

	public void setShoppingService(ShoppingService shoppingService) {
		this.shoppingService = shoppingService;
	}
	
	private BackMoneyService backMoneyService;

	public BackMoneyService getBackMoneyService() {
		return backMoneyService;
	}

	public void setBackMoneyService(BackMoneyService backMoneyService) {
		this.backMoneyService = backMoneyService;
	}

	/**
	 * 管理页面查询(设备拨款,只查询已复核的信息)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractBackMoney(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map rsNew = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		String searchStatus = StringUtils.isEmpty(context.contextMap.get("searchStatus")) ? "2" : (String)context.contextMap.get("searchStatus");
		String pcStatus = StringUtils.isEmpty(context.contextMap.get("pcStatus")) ? "1" : (String)context.contextMap.get("pcStatus");
		context.contextMap.put("searchStatus", searchStatus);
		context.contextMap.put("pcStatus", pcStatus);
		/*-------- data access --------*/
		if (errList.isEmpty()) {
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				rsNew = (Map) DataAccessor.query("employee.getComType", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				context.contextMap.put("decp_id", rsNew.get("DECP_ID"));
				context.contextMap.put("job", rsMap.get("JOB"));
				dw = baseService.queryForListWithPaging("rentContract.queryRentContractBackMoney", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询设备拨款错误!请联系管理员");
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("searchStatus", searchStatus);
		outputMap.put("pcStatus", pcStatus);

		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context,
					"/backMoney/backMoneyContract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	/**
	 * 保证金请款页面
	 * 
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractBackMoneyByMargin(Context context) throws Exception {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;
		Map<String, Object> rsMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
			dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.getPledgeForPay", context.contextMap, "LEASE_CODE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("search_status", context.contextMap.get("search_status"));
		Output.jspOutput(outputMap, context, "/backMoney/marginPay.jsp");
	}
	
	/**
	 * 乘用车手续费请款页面
	 * 
	 * @param context
	 * @throws Exception 
	 */
	public void queryHandlingCharge(Context context) throws Exception {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;
		ShoppingCartTO cart = null;
		String search_status = (String) context.contextMap.get("search_status");
		try {
			search_status = StringUtils.isEmpty(search_status) ? "-1" : search_status;
			context.contextMap.put("search_status", search_status);
			if ("-1".equals(search_status)) {
				context.contextMap.put("__orderBy", "CREDIT_RUNCODE");
				dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryHandlingCharge", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
			} else {
				context.contextMap.put("__orderBy", "APPLICATION_DATE");
				dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryHandlingChargeByOrder", context.contextMap, "APPLICATION_DATE", ORDER_TYPE.DESC);
			}
			cart = shoppingService.getShoppingCartByUserId(String.valueOf(context.contextMap.get("s_employeeId")), 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("search_status", search_status);
		outputMap.put("cart", cart);
		outputMap.put("search_value", context.contextMap.get("search_value"));
		outputMap.put("search_status", context.contextMap.get("search_status"));
		/* Add by ZhangYizhou on 2014-06-16 Begin */
		/* IT201406047 :增加 银行拨款日起始、结束时间的查询条件 */
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		/* Add by ZhangYizhou on 2014-06-16 End */
		Output.jspOutput(outputMap, context, "/backMoney/handlingChargePay.jsp");
	}
	
	/**
	 * 乘用车佣金请款页面
	 * 
	 * @param context
	 * @throws Exception 
	 */
	public void queryBrokerage(Context context) throws Exception {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;
		ShoppingCartTO cart = null;
		String search_status = (String) context.contextMap.get("search_status");
		try {
			search_status = StringUtils.isEmpty(search_status) ? "-1" : search_status;
			context.contextMap.put("search_status", search_status);
			if ("-1".equals(search_status)) {
				context.contextMap.put("__orderBy", "CREDIT_RUNCODE");
				dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryBrokerage", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
			} else {
				context.contextMap.put("__orderBy", "APPLICATION_DATE");
				dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryBrokerageByOrder", context.contextMap, "APPLICATION_DATE", ORDER_TYPE.DESC);
			}
			cart = shoppingService.getShoppingCartByUserId(String.valueOf(context.contextMap.get("s_employeeId")), 2);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("search_status", search_status);
		outputMap.put("cart", cart);
		outputMap.put("search_value", context.contextMap.get("search_value"));
		outputMap.put("search_status", context.contextMap.get("search_status"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap, context, "/backMoney/brokeragePay.jsp");
	}
	
	public void queryHandlingChargeDetail(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.queryForList("rentContract.getHandlingChargeDetail", context.contextMap);
		outputMap.put("result", result);
		outputMap.put("po_id", context.contextMap.get("po_id"));
		Output.jspOutput(outputMap, context, "/backMoney/handlingChargePayDetail.jsp");
		
	}
	
	/**
	 * 佣金订单明细
	 * @param context
	 */
	public void getBrokerageDetail(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> result = (List<Map<String, Object>>) baseService.queryForList("rentContract.getBrokerageDetail", context.contextMap);
		outputMap.put("result", result);
		outputMap.put("po_id", context.contextMap.get("po_id"));
		Output.jspOutput(outputMap, context, "/backMoney/brokeragePayDetail.jsp");
		
	}
	
	/**
	 * 加入购物车
	 * @param context
	 */
	public void addItemToCart(Context context){
		String itemStr = (String) context.contextMap.get("items");
		String add = (String) context.contextMap.get("addStr");
		String[] items = itemStr.split(",");
		String[] strs = add.split(",");
		String addStr = "";
		boolean addFlag = true;
		for(int i = 0; i < strs.length; i ++){
			addFlag = true;
			for(int j = 0; j < items.length; j ++){
				if(StringUtils.isEmpty(strs[i]) || strs[i].equals(items[j])){
					addFlag = false;
				}
			}
			if(addFlag){
				addStr += strs[i] + ",";
			}
		}
		if(addStr.length() > 0){
			addStr = addStr.substring(0, addStr.length() - 1);
		}
		if(itemStr.length() > 0){
			if(addStr.length() > 0){
				addStr = itemStr + "," + addStr;
			} else {
				addStr = itemStr;
			}
		}
		System.out.println(addStr);
		ShoppingCartTO cart = new ShoppingCartTO();
		cart.setItems(addStr);
		cart.setUser_id((String) context.contextMap.get("user_id"));
		cart.setItem_type(Integer.parseInt(String.valueOf(context.contextMap.get("item_type"))));
		if(!addStr.equals(itemStr)){
			try {
				shoppingService.updateItemsForCart(cart);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Output.jsonObjectOutputForTo(cart, context);
	}
	
	/**
	 * 显示购物车明细
	 * @param context
	 */
	public void showCart(Context context){
		System.out.println("=============showCart============");
		List<Map<String, Object>> resultList = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 1);
		if (StringUtils.isEmpty(cart.getItems())) {
			resultList = new ArrayList<Map<String,Object>>();
		} else {
			paramMap.put("cartItems", cart.getItems());
			resultList = (List<Map<String, Object>>) baseService.queryForList("rentContract.queryHandlingCharge", paramMap);
		}
		outputMap.put("cart", cart);
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/backMoney/handlingChargeCart.jsp");
	}
	
	/**
	 * 删除购物车里的明细
	 */
	public void delItemFromCart(Context context){
		String delStr = (String) context.contextMap.get("delStr");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 1);
		String newItems = cart.getItems();
		String[] delStrArr = delStr.split(",");
		for (String s : delStrArr) {
			newItems = newItems.replaceAll(s.trim(), "");
		}
		String items = "";
		for(String str : newItems.split(",")){
			if (!StringUtils.isEmpty(str.trim())) {
				items += str + ",";
			}
		}
		if (items.length() > 0) {
			items = items.substring(0, items.length() - 1);
		}
		cart.setItems(items);
		shoppingService.updateItemsForCart(cart);
		showCart(context);
	}
	
	/**
	 * 结算
	 * @throws Exception 
	 */
	public void doPayHandlingCharge(Context context) throws Exception{
		String allItems = (String) context.contextMap.get("allItems");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 1);
		if (!cart.getItems().equals(allItems)) {
			throw new Exception("页面数据不同步，请刷新页面后再申请。");
		}
		Double totalMoney = (Double) baseService.queryForObj("rentContract.getSumOutPayMoney", cart);
		cart.setItems_money(totalMoney);
		
		try {
			cart.setOrder_type(1);
			backMoneyService.doPayHandlingCharge(shoppingService, cart);
			context.contextMap.put("search_status", "1");
			queryHandlingCharge(context);
		} catch (Exception e) {
			e.printStackTrace();
			showCart(context);
		}
		
		
	}
	
	/* 佣金 */
	/**
	 * 显示购物车明细
	 * @param context
	 */
	public void showCartForBrokerage(Context context){
		System.out.println("=============showCart============");
		List<Map<String, Object>> resultList = null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 2);
		if (StringUtils.isEmpty(cart.getItems())) {
			resultList = new ArrayList<Map<String,Object>>();
		} else {
			paramMap.put("cartItems", cart.getItems());
			resultList = (List<Map<String, Object>>) baseService.queryForList("rentContract.queryBrokerage", paramMap);
		}
		outputMap.put("cart", cart);
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/backMoney/brokerageCart.jsp");
	}
	
	/**
	 * 删除购物车里的明细
	 */
	public void delItemFromCartForBrokerage(Context context){
		String delStr = (String) context.contextMap.get("delStr");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 2);
		String newItems = cart.getItems();
		String[] delStrArr = delStr.split(",");
		for (String s : delStrArr) {
			newItems = newItems.replaceAll(s.trim(), "");
		}
		String items = "";
		for(String str : newItems.split(",")){
			if (!StringUtils.isEmpty(str.trim())) {
				items += str + ",";
			}
		}
		if (items.length() > 0) {
			items = items.substring(0, items.length() - 1);
		}
		cart.setItems(items);
		shoppingService.updateItemsForCart(cart);
		showCartForBrokerage(context);
	}
	
	/**
	 * 结算
	 * @throws Exception 
	 */
	public void doPayBrokerage(Context context) throws Exception{
		String allItems = (String) context.contextMap.get("allItems");
		String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
		ShoppingCartTO cart = shoppingService.getShoppingCartByUserId(user_id, 2);
		if (!cart.getItems().equals(allItems)) {
			throw new Exception("页面数据不同步，请刷新页面后再申请。");
		}
		Double totalMoney = (Double) baseService.queryForObj("rentContract.getSumBrokerageMoney", cart);
		cart.setItems_money(totalMoney);
		
		try {
			cart.setOrder_type(2);
			backMoneyService.doPayBrokerage(shoppingService, cart);
			context.contextMap.put("search_status", "1");
			queryBrokerage(context);
		} catch (Exception e) {
			e.printStackTrace();
			showCart(context);
		}
		
		
	}
	
	
	public void queryShoppingList(Context context) throws Exception {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> dw = null;
		try {
			dw = (PagingInfo<Object>) baseService.queryForListWithPaging("rentContract.queryHandlingCharge", context.contextMap, "MODIFY_DATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		}
		outputMap.put("dw", dw);
		outputMap.put("search_value", context.contextMap.get("search_value"));
		outputMap.put("search_status", context.contextMap.get("search_status"));
		Output.jspOutput(outputMap, context, "/backMoney/handlingChargePay.jsp");
	}
	
	
	/**
	 * 管理页面查询(保险费拨款,只查询已复核的信息)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractBackMoneyByInsurance(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		/*-------- data access --------*/
		if (errList.isEmpty()) {
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
						paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));

				dw = (DataWrap) DataAccessor.query(
						"rentContract.queryRentContractBackMoneyByInsurance",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询保险费错误!请联系管理员");
			}
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));

		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/backMoney/insurancePay.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOne(Context context) throws Exception {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;
		List payDw = null;
		Map payMoney = null;
		List payMoney1 = null;
		List psTypeList = null;
		List<Map<String, Object>> feeList = null;
		String expectedDate=null;
		if (errList.isEmpty()) {
			try {
				// 查询该供应商的基本信息
				payMoney = (Map) DataAccessor.query(
						"rentContract.payMoneyManagerByOne",
						context.contextMap, DataAccessor.RS_TYPE.MAP);

				// payMoney1 = (List)
				// DataAccessor.query("rentContract.payMoneyManagerByOne1",
				// context.contextMap,DataAccessor.RS_TYPE.LIST);
				// 查询该供应商的所有银行
				dw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByOne",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 查询该供应商已付款
				payDw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByRECTID",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 拨款方式
				context.contextMap.put("dataType", "拨款方式");
				psTypeList = (List<Map>) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				//如果是此案件是专案,按照专案限制条件来卡
				context.contextMap.put("propertyCode","PAY_WAY");
				context.contextMap.put("credit_id",context.contextMap.get("CREDIT_ID"));
				List<CreditSpecialTO> creditSpecialCaseProperty=(List<CreditSpecialTO>)DataAccessor.query("creditReportManage.getCreditSpecialCasePropertyByCreditId",context.contextMap,DataAccessor.RS_TYPE.LIST);
				if(creditSpecialCaseProperty!=null&&creditSpecialCaseProperty.size()!=0) {
					//是专案
					if("Y".equals(creditSpecialCaseProperty.get(0).getCheckValue())) {
						if("网银汇款".equals(creditSpecialCaseProperty.get(0).getValue1())) {
							//网银汇款不限制下拉框
						} else {
							for(int i=0;i<psTypeList.size();i++) {
								if("2".equals(((Map<String,Object>)psTypeList.get(i)).get("CODE"))) {//CODE=2是现金
									psTypeList.remove(i);
									break;
								}
							}
						}
					} else {
						//do nothing
					}
				} else {
					if("N".equals(payMoney.get("NET_PAY"))) {//如果此供应商网银汇款是N,则设备请款时候不能选择现金 add by ShenQi 2012-10-9
						for(int i=0;i<psTypeList.size();i++) {
							if("2".equals(((Map<String,Object>)psTypeList.get(i)).get("CODE"))) {//CODE=2是现金
								psTypeList.remove(i);
								break;
							}
						}
					}
				}
				
				expectedDate=(String)DataAccessor.query("rentContract.getNewestExpectedDate",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
				context.contextMap.put("CREDIT_ID", payMoney.get("CREDIT_ID"));
				outputMap.put("TOTAL", DataAccessor.query(
						"rentContract.playdetilTOTAL", context.contextMap,
						DataAccessor.RS_TYPE.OBJECT));
				/* 2012/02/27 Yang Yun 增加费用明细 */
				feeList = (List<Map<String, Object>>) DataAccessor.query(
						"rentContract.getFeeList", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查看付款凭证错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("psTypeList", psTypeList);
			outputMap.put("dw", dw);
			outputMap.put("payMoney", payMoney);
			// outputMap.put("payMoney1", payMoney1);
			outputMap.put("payDw", payDw);
			outputMap.put("expectedDate", expectedDate);
			outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
			boolean isCar = false;
			boolean isCarBack = false;
			String creditId = String.valueOf(context.contextMap.get("CREDIT_ID"));
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			if("8".equals(contractType) || "14".equals(contractType)){
				isCar = true;
			}else if("10".equals(contractType) || "12".equals(contractType)){
				isCarBack = true;
			}
			
			outputMap.put("isCar", isCar);
			outputMap.put("isCarBack", isCarBack);
			List express = DictionaryUtil.getDictionary("快递方式");
			outputMap.put("express", express);
			
			List express_pay_way = DictionaryUtil.getDictionary("快递付款方式");
			outputMap.put("express_pay_way", express_pay_way);
			
			
			String decpName = LeaseUtil.getDecpNameByCreditId(creditId);
			String defaultExpress = "顺丰";
			if("苏州总公司".equals(decpName)||"昆山设备".equals(decpName)
					||"南京设备".equals(decpName)
					||"上海设备".equals(decpName)
					||"上海商用车".equals(decpName)
					||"苏州商用车".equals(decpName)
					||"苏州设备".equals(decpName)
					||"苏州小车".equals(decpName)
					||"苏州乘用车".equals(decpName)
					||"上海乘用车".equals(decpName)
					||"杭州乘用车".equals(decpName)
					||"宁波设备".equals(decpName)){
				defaultExpress = "汇通";
			}
			
			outputMap.put("defaultExpress", defaultExpress);
			Map paramMap = new HashMap();
			paramMap.put("credit_id", creditId);
			Map scheme = (Map) this.getBaseService().queryForObj("rentContract.getPrjtCreditSeheme", paramMap);
			Integer appropriation_way = (Integer) (scheme.get("APPROPRIATION_WAY")!=null?scheme.get("APPROPRIATION_WAY"):2);
			outputMap.put("APPROPRIATION_WAY", appropriation_way);
			if(appropriation_way==1){
				outputMap.put("ENDORSER_1", scheme.get("ENDORSER_1"));
				outputMap.put("ENDORSER_2", scheme.get("ENDORSER_2"));
			}
			
			//新增新车委贷开户行和账号
			
			String bankName = DictionaryUtil.getFlag("新车委贷开户行和账号", "bank");
			String bankAccount = DictionaryUtil.getFlag("新车委贷开户行和账号", "account");
			outputMap.put("bankName", bankName);
			outputMap.put("bankAccount", bankAccount);
			
//			//乘用车委贷
//			String taxPlanCode = LeaseUtil.getTaxPlanCodeByCreditId((String)context.contextMap.get("credit_id"));
//			if("5".equals(taxPlanCode)){
//				Date date = (Date) baseService.queryForObj("rentContract.getStartDateByCreditId",context.contextMap);
//				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//				outputMap.put("START_DATE", df.format(date));
//			}
			
			outputMap.put("bankAccount", bankAccount);
			Output.jspOutput(outputMap, context, "/backMoney/payMoney.jsp");
			
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 保证金申请付款
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneByMargin(Context context) throws Exception {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;
		List payDw = null;
		Map payMoney = null;
		List psTypeList = null;
		if (errList.isEmpty()) {
			try {
				context.contextMap.put("zj", "租金");
				// 查询该供应商的基本信息
				payMoney = (Map) DataAccessor.query(
						"rentContract.payMoneyManagerByOne_2",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询该供应商的所有银行
				dw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByOne",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 查询该供应商已付款
				payDw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByRECTIDByMargin",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 拨款方式
				context.contextMap.put("dataType", "拨款方式");
				psTypeList = (List<Map>) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--保证金申请付款错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("psTypeList", psTypeList);
			outputMap.put("dw", dw);
			outputMap.put("payMoney", payMoney);
			outputMap.put("payDw", payDw);
			List express = DictionaryUtil.getDictionary("快递方式");
			outputMap.put("express", express);
			
			List express_pay_way = DictionaryUtil.getDictionary("快递付款方式");
			outputMap.put("express_pay_way", express_pay_way);
			String leaseCode = (String) context.contextMap.get("lease_code");
			String creditId = LeaseUtil.getCreditIdByLeaseCode(leaseCode);
			String decpName = LeaseUtil.getDecpNameByCreditId(creditId);
			String defaultExpress = "顺丰";
			if("苏州总公司".equals(decpName)||"昆山设备".equals(decpName)
					||"南京设备".equals(decpName)
					||"上海设备".equals(decpName)
					||"上海商用车".equals(decpName)
					||"苏州商用车".equals(decpName)
					||"苏州设备".equals(decpName)
					||"苏州小车".equals(decpName)
					||"苏州乘用车".equals(decpName)
					||"上海乘用车".equals(decpName)
					||"杭州乘用车".equals(decpName)
					||"宁波设备".equals(decpName)){
				defaultExpress = "汇通";
			}
			outputMap.put("defaultExpress", defaultExpress);
			Output.jspOutput(outputMap, context,
					"/backMoney/marginDetilPay.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 保证金申请付款
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneByInsurance(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;
		List payDw = null;
		Map payMoney = null;
		List psTypeList = null;
		if (errList.isEmpty()) {
			try {
				// 查询该供应商的基本信息
				payMoney = (Map) DataAccessor.query(
						"rentContract.payMoneyManagerByOne",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询该供应商的所有银行
				dw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByOne",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 查询该供应商已付款
				payDw = (List) DataAccessor.query(
						"rentContract.payMoneyBankManagerByRECTIDByInsurance",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 拨款方式
				context.contextMap.put("dataType", "拨款方式");
				psTypeList = (List<Map>) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--保险申请付款错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("psTypeList", psTypeList);
			outputMap.put("dw", dw);
			outputMap.put("payMoney", payMoney);
			outputMap.put("payDw", payDw);
			System.out.println("/backMoney/insuranceDetilPay.jsp");
			Output.jspOutput(outputMap, context,
					"/backMoney/insuranceDetilPay.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 根据合同编号新建一条付款记录
	 * 
	 * @param context
	 */
	@Transactional(rollbackFor=Exception.class)
	public void createPayMoneyManager(Context context) {
		
		/*SqlMapClient sqlMapper=DataAccessor.getSession();*/
		Map outputMap = new HashMap();
		List errList = context.errList;
		int backState = Integer.parseInt(context.request
				.getParameter("backState"));
		String numType = context.contextMap.get("numType").toString();
		// System.out.println("----------->"+context.contextMap.get("backtype"));
		String bank_name = null;
		String bank_account = null;
		Long FSS_ID=null;
		if (numType.equals("0")) {
			bank_name = context.contextMap.get("bank_name_text").toString();
			bank_account = context.contextMap.get("bank_account").toString();
		} else if (numType.equals("1")) {
			bank_name = context.contextMap.get("bank_name_text").toString();
			bank_account = context.contextMap.get("bank_account").toString();
		} else if (numType.equals("2")) {
			bank_name = context.contextMap.get("bank_name2").toString();
			bank_account = context.contextMap.get("bank_account1").toString();
		} else if (numType.equals("3")) {
			bank_name = context.contextMap.get("bank_name3").toString();
			bank_account = context.contextMap.get("bank_account1").toString();
		}else if (numType.equals("4")) {
			bank_name = context.contextMap.get("bank_name4").toString();
			bank_account = context.contextMap.get("bank_account").toString();
		}else if (numType.equals("5")) {
			bank_name = context.contextMap.get("bank_name5").toString();
			bank_account = context.contextMap.get("bank_account").toString();
		}

		context.contextMap.put("bank_name", bank_name);
		context.contextMap.put("bank_account", bank_account);
		if (errList.isEmpty()) {
			try {
				// Marked by Michael 2012 4 -27 取消Check机制

				// // 检查是否还有剩余款项需要申请,如果为0则不能继续申请,抛出提示 add by ShenQi
				// context.contextMap.put("dataType","拨款方式");
				// context.contextMap.put("NUM",0);
				// List<Map<String,Object>>
				// checkList=(List<Map<String,Object>>)DataAccessor.query("rentContract.payMoneyByRect_ID",
				// context.contextMap,DataAccessor.RS_TYPE.LIST);
				// for(int i=0;checkList!=null&&i<checkList.size();i++) {
				// String status=String.valueOf(checkList.get(i).get("STATUS"));
				// double
				// payCount=((java.math.BigDecimal)checkList.get(i).get("PAYCOUNT")).doubleValue();
				// double
				// payed=((java.math.BigDecimal)checkList.get(i).get("PAYED")).doubleValue();
				// double
				// payMoney=((java.math.BigDecimal)checkList.get(i).get("PAY_MONEY")).doubleValue();
				// if((payCount-payed-payMoney)==0&&!status.equals("1")) {
				// errList.add("全部款项已经拨出,请勿继续申请!");
				// break;
				// }
				// }
				// if(!errList.isEmpty()) {
				// outputMap.put("errList", errList);
				// Output.jspOutput(outputMap, context, "/error.jsp");
				// return;
				// }
				
				/*sqlMapper.startTransaction();
				
				long pay_id=(Long)sqlMapper.insert("rentContract.createPayMoneyManager",context.contextMap);*/


				/*
				 * Add by Michael 2012 11/14 
				 * 如果是拨设备款时，要检查是否是首拨款，如果是首拨款，要检查是否要增加送件，如果是拨尾款，则要增加一条送件记录
				 */
				if (backState==0){
					//判断此笔申请拨款是否是首拨款
//					int payOrder=(Integer)DataAccessor.query("rentContract.queryIsFirstPayMoney",
//							context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					//如果是首拨款 要检查文件送审状态
					//首拨款
					//int rentFileSenderState=(Integer)DataAccessor.query("rentContract.queryRentFileSenderState",
					//context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					//if (payOrder!=0){
						//否则是拨尾款  则插入一条文件送审状态记录
					context.contextMap.put("CREDIT_ID", context.contextMap.get("CREDIT_ID"));
					FSS_ID = (Long)DataAccessor.execute("rentFile.insertRentFileSenderState",context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
					//}
					if("".equals(context.contextMap.get("LESSOR_TIME_HIDDEN")) || null==context.contextMap.get("LESSOR_TIME_HIDDEN")){
						DataAccessor.execute("rentContract.updateLessorTime",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					}
					
				}
				
				
				context.contextMap.put("FSS_ID", FSS_ID);
				
				long pay_id=(Long)DataAccessor.execute("rentContract.createPayMoneyManager",
						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				//维护预计拨款日期 add by ShenQi
				/*context.contextMap.put("EXPECTED_DATE",context.contextMap.get("expecteddate"));
				context.contextMap.put("PAY_DETAIL_ID",pay_id);
				
				sqlMapper.insert("rentContract.insertExpectedDate",context.contextMap);
				sqlMapper.commitTransaction();*/
				
				//拨款申请插入Email 通知 主管
				//查询出主管的Email
				String emilString=(String) DataAccessor.query("rentContract.queryExecutiveEmailByUserID", context.contextMap,DataAccessor.RS_TYPE.OBJECT);	
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailTo(emilString);
				mailSettingTo.setEmailSubject("拨款申请审批");
				mailSettingTo.setEmailContent("客户："+context.contextMap.get("CUST_NAME")+"；合同号："+context.contextMap.get("LEASE_CODE")+"；申请拨款："+context.contextMap.get("pay_money")+"。申请人："+context.contextMap.get("s_employeeName")+"。还请到TAC租赁系统中进行审批。");
				mailSettingTo.setCreateBy(String.valueOf(context.contextMap.get("s_employeeId")));
				mailUtilService.sendMail(mailSettingTo);
				
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("CREDIT_ID")), Long
						.parseLong(StringUtils
								.isEmpty((String) context.contextMap
										.get("RECT_ID")) ? "0"
								: (String) context.contextMap
										.get("RECT_ID")), 
						"拨款管理", "申请拨款", "", "申请拨设备款金额："+context.contextMap.get("pay_money"), 1, DataUtil.longUtil(context.contextMap
								.get("s_employeeId")), null, context.contextMap.get("IP").toString());
			} catch (Exception e) {
				/*try {
					sqlMapper.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}*/
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--添加付款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			if (backState == 0) {
				if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
					Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForShow&isSalesDesk=Y");
				} else { Output.jspSendRedirect(context,
						"defaultDispatcher?__action=backMoney.queryRentContractBackMoney");
				}
			} else if (backState == 1) {
				Output.jspSendRedirect(context,
						"defaultDispatcher?__action=backMoney.queryRentContractBackMoneyByMargin");
			} else if (backState == 3) {
				Output.jspSendRedirect(context,
						"defaultDispatcher?__action=insuranceList.getInsurePayMoney");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 根据保单编号新建一条付款记录
	 * 
	 * @param context
	 */
	@SuppressWarnings({ "unchecked" })
	public void createPayMoneyManagerNew(Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		@SuppressWarnings("unused")
		int backState = Integer.parseInt(context.request
				.getParameter("backState"));
		String[] conIncu_id = context.request.getParameter("conIncu_id")
				.toString().split(",");
		SqlMapClient sqlMapper = DataAccessor.getSession();

		Long numId;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction();
				numId = (Long) sqlMapper.insert(
						"rentContract.createPayMoneyManagerByInsurance",
						context.contextMap);
				// DataAccessor.execute("rentContract.createPayMoneyManagerByInsurance",
				// context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				context.contextMap.put("numId", numId);

				for (int i = 0; i < conIncu_id.length; i++) {

					// 插入保单和付款表中间表
					context.contextMap.put("insu_id", conIncu_id[i]);
					sqlMapper.insert(
							"rentContract.createPayMoneyInsuranceJoin",
							context.contextMap);
					// DataAccessor.execute("rentContract. ",
					// context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);

				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--添加保险费付款记录错误!请联系管理员");
			} finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					logger.debug(e);
				}
			}
		}

		if (errList.isEmpty()) {

			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=insuranceList.getInsurePayMoney");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 查询该供应商的所有付款记录
	@SuppressWarnings("unchecked")
	public void payMoneyByRect_ID(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;

		if (errList.isEmpty()) {
			try {
				context.contextMap.put("dataType", "拨款方式");
				// 查询该供应商的所有付款记录
				dw = (List) DataAccessor.query(
						"rentContract.payMoneyByRect_ID", context.contextMap,
						DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询供应商付款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {

			outputMap.put("dw", dw);
			Output.jspOutput(outputMap, context,
					"/backMoney/backMoneyDetal.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 查询所有该请款单下的保单
	@SuppressWarnings("unchecked")
	public void payInsuranceJoin(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List list = null;

		if (errList.isEmpty()) {
			try {
				// 查询该供应商的所有付款记录
				list = (List) DataAccessor.query(
						"insurance.getInsurePayMoneyJoin", context.contextMap,
						DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询保险费付款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("list", list);
			Output.jspOutput(outputMap, context,
					"/backMoney/insurancePayJoin.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 查询所有该请款单下的奖金
	@SuppressWarnings("unchecked")
	public void payBonusJoin(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List list = null;
		List bonusList = new ArrayList();
		List bonus = null;

		if (errList.isEmpty()) {
			try {
				bonus = (List) DataAccessor.query("bonusManage.queryAllBonus",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 查询该供应商的所有付款记录
				list = (List) DataAccessor.query(
						"insurance.getBonusPayMoneyJoin", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				for (int i = 0; i < list.size(); i++) {
					Map bonusMap = (Map) list.get(i);

					List bonusTypeL = new ArrayList();

					String bonusTypeStr = bonusMap.get("MONEY_VALUE")
							.toString();
					String[] bonusStr = bonusTypeStr.split(",");
					for (int j = 0; j < bonusStr.length; j++) {
						String bonusSun = bonusStr[j];
						String[] bonusSunL = bonusSun.split("-");

						Map bonusMaps = new HashMap();
						bonusMaps.put("typeName", bonusSunL[1]);
						bonusMaps.put("typeUpMoney", bonusSunL[0]);
						bonusTypeL.add(bonusMaps);
					}
					bonusMap.put("typeList", bonusTypeL);
					bonusList.add(bonusMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询奖金具体信息记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("bonusList", bonusList);
			outputMap.put("bonus", bonus);
			Output.jspOutput(outputMap, context, "/backMoney/bonusPayJoin.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 根据合同查询承租人及银行
	@SuppressWarnings("unchecked")
	public void custByRectId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;

		if (errList.isEmpty()) {
			try {

				dw = (List) DataAccessor.query("rentContract.custByRectId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询承租人及银行错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {

			outputMap.put("dw", dw);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 根据合同查询供应商人及银行
	@SuppressWarnings("unchecked")
	public void suplByRectId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;

		if (errList.isEmpty()) {
			try {

				dw = (List) DataAccessor.query("rentContract.suplByRectId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询供应商及银行错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 根据合同查询供应商人及银行
	@SuppressWarnings("unchecked")
	public void suplByRectIds(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;

		if (errList.isEmpty()) {
			try {

				dw = (List) DataAccessor.query("rentContract.suplByRectIds",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询供应商及银行错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 根据供应商Id查询供应商人及银行
	@SuppressWarnings("unchecked")
	public void suplBySuplIds(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List dw = null;

		if (errList.isEmpty()) {
			try {

				dw = (List) DataAccessor.query("rentContract.suplBySuplIds",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询供应商及银行错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dws", dw);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 根据供应商Id查询供应商人及银行
	@SuppressWarnings("unchecked")
	public void suplBySuplIdss(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map dw = null;

		if (errList.isEmpty()) {
			try {

				dw = (Map) DataAccessor.query("rentContract.suplBySuplIdss",
						context.contextMap, DataAccessor.RS_TYPE.MAP);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询供应商及银行错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dwss", dw);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 拨款审批 -- 管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryPayMoneys(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;

		Map rsMap = null;
		Map paramMap = new HashMap();
		String BACKSTATE = "";
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		Map<String, Double> sumMap = new HashMap<String, Double>();
		List<HashMap<String, Object>> bankAccountList=null;

		if (context.contextMap.get("BACKSTATE") == null) {
			BACKSTATE = "0";

		} else {
			BACKSTATE = context.contextMap.get("BACKSTATE").toString();
		}

		if (BACKSTATE.equals("2") || BACKSTATE.equals("3")) {
			context.contextMap.put("__orderBy", "ID");
			context.contextMap.put("__orderType", "DESC");
		}

		context.contextMap.put("BACKSTATE", Integer.parseInt(BACKSTATE));

		if (errList.isEmpty()) {
			try {
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
						paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				// dw = (DataWrap)
				// DataAccessor.query("rentContract.queryRentContractBackMoney",
				// context.contextMap, DataAccessor.RS_TYPE.PAGED);
				if (BACKSTATE.equals("3")) {
					dw = baseService.queryForListWithPaging(
							"rentContract.queryPayMoneyByFirstInsusrance",
							context.contextMap, "ID", ORDER_TYPE.DESC);
				} else if (BACKSTATE.equals("2")) {
					dw = baseService.queryForListWithPaging(
							"rentContract.queryPayMoneyByFirstInsusrance",
							context.contextMap, "ID", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("5")){
					dw = baseService.queryForListWithPaging(
							"rentContract.queryPayMoneyByReturnMoney",
							context.contextMap, "ID", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("9")){
					if (StringUtils.isEmpty(context.contextMap.get("shen_pi_STATE"))) {
						context.contextMap.put("shen_pi_STATE", "0");
					}
					dw = baseService.queryForListWithPaging("rentContract.queryHandlingChargeByOrder",context.contextMap, "ID", ORDER_TYPE.DESC);
				}else if(BACKSTATE.equals("10")){
					if (StringUtils.isEmpty(context.contextMap.get("shen_pi_STATE"))) {
						context.contextMap.put("shen_pi_STATE", "0");
					}
					dw = baseService.queryForListWithPaging("rentContract.queryBrokerageByOrder",context.contextMap, "ID", ORDER_TYPE.DESC);
				} else {
					if(context.contextMap.get("PAY_ORDER")==null||"".equals(context.contextMap.get("PAY_ORDER"))) {
						context.contextMap.put("PAY_ORDER","0");
					}
					dw = baseService.queryForListWithPaging(
							"rentContract.queryPayMoneyByFirst",
							context.contextMap, "ID", ORDER_TYPE.DESC);
					// sumMap=(Map<String,Double>)DataAccessor.query("rentContract.equipmentSum",
					// context.contextMap, DataAccessor.RS_TYPE.MAP);
					// 加入总计
					double con = 0.0;
					if (dw != null && dw.getResultList() != null) {
						List list = (List) dw.getResultList();
						for (int i = 0; i < list.size(); i++) {
							Map temp = (Map) list.get(i);
							if (temp != null && temp.get("PAY_MONEY") != null)
								con += Double.parseDouble(temp.get("PAY_MONEY")
										+ "");
						}
					}
					outputMap.put("TOTAL", con);
					outputMap.put("PAY_ORDER", context.contextMap.get("PAY_ORDER"));
				}

				// 2012/07/20 Yang Yun 增加办事处查询
				List<SelectionTo> decpList = baseService.getAllOffice();
				outputMap.put("decpList", decpList);
				
				/* 2014/08/27 Xuyuefei 增加银行账号明细*/
				bankAccountList=(List<HashMap<String, Object>>)DataAccessor.query(
						"bankAccount.getBankAccountInfo", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询付款信息审核错误!请联系管理员");
			}
		}

		outputMap.put("dw", dw);
		outputMap.put("bankAccountList", bankAccountList);
		outputMap.put("shen_pi_STATE", context.contextMap.get("shen_pi_STATE"));
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("PSTART_DATE", context.contextMap.get("PSTART_DATE"));
		outputMap.put("PEND_DATE", context.contextMap.get("PEND_DATE"));
		outputMap.put("RENTSTAUTS", context.contextMap.get("RENTSTAUTS"));
		outputMap.put("search_decp", context.contextMap.get("search_decp"));
		outputMap.put("vip_flag", context.contextMap.get("vip_flag"));
		outputMap.put("pay_way", context.contextMap.get("pay_way"));
		outputMap.put("BACKSTATE", BACKSTATE);
		outputMap.put("production_type", context.contextMap.get("production_type"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		if (errList.isEmpty()) {
			if (BACKSTATE.equals("3")) {
				Output.jspOutput(outputMap, context,
						"/backMoney/backMoneyInsuranceFirstAudit.jsp");
			} else if (BACKSTATE.equals("2")) {
				Output.jspOutput(outputMap, context,
						"/backMoney/backMoneyBonusFirstAudit.jsp");
			}else if(BACKSTATE.equals("5")){
				Output.jspOutput(outputMap, context,
						"/backMoney/backMoneyFundsReturnFirstAudit.jsp");
			}else if(BACKSTATE.equals("9")){
				Output.jspOutput(outputMap, context, "/backMoney/backMoneyHandlingChargeFirstAudit.jsp");
			}else if(BACKSTATE.equals("10")){
				Output.jspOutput(outputMap, context, "/backMoney/backMoneyBrokerageFirstAudit.jsp");
			}else {
				Output.jspOutput(outputMap, context,
						"/backMoney/backMoneyFirstAudit.jsp");
			}

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	@SuppressWarnings("unchecked")
	public void prePayMoneyUpdate(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney = null;
		List payDw = null;
		List<Map> psTypeList = null;
		Map payDetail = null;
		Object BACKSTATE = null;
		List<Map<String, Object>> feeList = null;
		if (errList.isEmpty()) {
			try {

				// 拨款方式
				context.contextMap.put("dataType", "拨款方式");
				psTypeList = (List<Map>) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

				payDetail = (Map) DataAccessor.query(
						"rentContract.queryPayById", context.contextMap,
						DataAccessor.RS_TYPE.MAP);

				payMoney = (Map) DataAccessor.query(
						"rentContract.payMoneyManager", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				BACKSTATE = payMoney.get("BACKSTATE");
				String backState = null;
				if (BACKSTATE != null) {
					backState = payMoney.get("BACKSTATE").toString();
				}
				if (backState.equals("0")) {
					payDw = (List) DataAccessor.query(
							"rentContract.payMoneyBankManagerByRECTID",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
				} else {
					payDw = (List) DataAccessor.query(
							"rentContract.payMoneyBankManagerByRECTIDByMargin",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
				}

				/* 2012/02/27 Yang Yun 增加费用明细 */
				feeList = (List<Map<String, Object>>) DataAccessor.query(
						"rentContract.getFeeList", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("feeList", feeList);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询拨款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("psTypeList", psTypeList);
			outputMap.put("payDw", payDw);
			outputMap.put("payMoney", payMoney);
			outputMap.put("payDetail", payDetail);
			outputMap.put("backState", BACKSTATE);
			Output.jspOutput(outputMap, context,
					"/backMoney/payMoneyUpdate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	public void updatePayMoney(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney = null;
		List payDw = null;
		Object backState = context.contextMap.get("backState");
		if (errList.isEmpty()) {
			try {

				String bank_name = null;
				String bank_account = null;
				String numType = context.contextMap.get("numType").toString();
				if (numType.equals("0")) {
					bank_name = context.contextMap.get("bank_names").toString();
					bank_account = context.contextMap.get("bank_account")
							.toString();
				} else if (numType.equals("1")) {
					bank_name = context.contextMap.get("bank_name1").toString();
					bank_account = context.contextMap.get("bank_account")
							.toString();
				} else if (numType.equals("2")) {
					bank_name = context.contextMap.get("bank_name2").toString();
					bank_account = context.contextMap.get("bank_account1")
							.toString();
				} else if (numType.equals("3")) {
					bank_name = context.contextMap.get("bank_name3").toString();
					bank_account = context.contextMap.get("bank_account1")
							.toString();
				}

				context.contextMap.put("bank_name", bank_name);
				context.contextMap.put("bank_account", bank_account);

				DataAccessor.execute("rentContract.updatePayMoneyManager",
						context.getContextMap(),
						DataAccessor.OPERATION_TYPE.UPDATE);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询拨款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			if (backState.equals("0")) {
				Output.jspSendRedirect(context,
						"defaultDispatcher?__action=backMoney.queryRentContractBackMoney");
			} else {
				Output.jspSendRedirect(context,
						"defaultDispatcher?__action=backMoney.queryRentContractBackMoneyByMargin");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	// 查询一条拨款记录的所有信息
	@SuppressWarnings("unchecked")
	public void payMoneyManagerByOneShow(Context context) throws Exception {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map payMoney = null;
		List payDw = null;
		List auditRe = null;
		if (errList.isEmpty()) {
			try {
				context.contextMap.put("dataType", "拨款方式");
				payMoney = (Map) DataAccessor.query(
						"rentContract.payMoneyManager", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				Object BACKSTATE = payMoney.get("BACKSTATE");
				String backState = null;
				if (BACKSTATE != null) {
					backState = payMoney.get("BACKSTATE").toString();
				}
				if (backState.equals("0")) {
					payDw = (List) DataAccessor.query(
							"rentContract.payMoneyBankManagerByRECTID",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
				} else {
					payDw = (List) DataAccessor.query(
							"rentContract.payMoneyBankManagerByRECTIDByMargin",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
				}
				String a = context.contextMap.get("CREDIT_ID").toString();
				auditRe = (List) DataAccessor.query(
						"rentContract.getLogByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				// 拨款方式
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询拨款记录错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("auditRe", auditRe);
			outputMap.put("payDw", payDw);
			outputMap.put("payMoney", payMoney);
			
			List express = DictionaryUtil.getDictionary("快递方式");
			outputMap.put("express", express);
			
			List express_pay_way = DictionaryUtil.getDictionary("快递付款方式");
			outputMap.put("express_pay_way", express_pay_way);
			

			Map expressinfo = (Map) this.getBaseService().queryForObj("rentContract.getExpressInfo", context.contextMap);
			outputMap.put("expressinfo", expressinfo);
			
			Output.jspOutput(outputMap, context,
					"/backMoney/backMoneyAudit.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 业务部审核
	@SuppressWarnings("unchecked")
	public void updateDepartMentById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap=DataAccessor.getSession();
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateDepartMentById",
						context.contextMap);
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				String memo = "拨款审批-单位主管审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				String creditId = null;
				if (StringUtils.isEmpty((String) context.contextMap
						.get("RECT_ID"))) {
					creditId = (String) DataAccessor.query(
							"rentContract.getCreditId", context.contextMap,
							DataAccessor.RS_TYPE.OBJECT);
				}
				
				//Add by Michael 2012 11-12 增加合同文件审核状况
				if("1".equals(context.contextMap.get("Num"))){
					//如果是拨尾款则找到当初这个案子的分派人员，且不需再分派人员
					int payMoneyPassCount=DataUtil.intUtil(context.contextMap.get("payMoneyPassCount"));
//					if (payMoneyPassCount==0){
//						//如果主管通过则要给此案件分派一个文审人员，将此案件状态更新为主管通过
//						String user_id = (String) DataAccessor.query(
//								"rentContract.getRentFileDispatchUser", context.contextMap,
//								DataAccessor.RS_TYPE.OBJECT);
//						context.contextMap.put("USER_ID", user_id);
//						sqlMap.update("rentContract.updateRentFileStateByPass",
//								context.contextMap);
//						//更新分派user 的最新事件
//						sqlMap.update("rentContract.updateDispatchUserDatetime",
//								context.contextMap);
//					}else{
//						//否则则是拨尾款，则要查询到最后一次收件分派的人员
//						//如果主管通过则要给此案件分派一个文审人员，将此案件状态更新为主管通过
//						String user_id = (String) DataAccessor.query(
//								"rentContract.getRentFileDispatchUserByPayMoneyPass", context.contextMap,
//								DataAccessor.RS_TYPE.OBJECT);
//						context.contextMap.put("USER_ID", user_id);
//						sqlMap.update("rentContract.updateRentFileStateByPass",
//								context.contextMap);
//					}
					
					sqlMap.update("rentContract.updateRentFileStateByPass",
							context.contextMap);
				}else{
					sqlMap.update("rentContract.updateRentFileStateByReject",
							context.contextMap);
					//退回到未初审状态
					Map payOrderMap = (Map)DataAccessor.query("rentContract.getPayOrderById", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if("1".equals(payOrderMap.get("PAY_ORDER").toString())) {
						sqlMap.update("rentContract.rejectContract",
								context.contextMap);
					}
				}
				
				
				BusinessLog.addBusinessLogWithIp(creditId == null ? null : Long.valueOf(creditId), Long
						.parseLong(StringUtils
								.isEmpty((String) context.contextMap
										.get("RECT_ID")) ? "0"
								: (String) context.contextMap
										.get("RECT_ID")), 
						"拨款管理", "审核", "", memo, 1, DataUtil.longUtil(context.contextMap
								.get("s_employeeId")), null, context.contextMap.get("IP").toString());
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--单位主管审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 业管部审核
	@SuppressWarnings("unchecked")
	public void updateExamById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateExamById", context.contextMap);
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				String memo = "拨款审批-业管部审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				//业管驳回时要更新案件送件状况
				if("2".equals(context.contextMap.get("Num"))){
					sqlMap.update("rentContract.updateRentFileStateByExam",
							context.contextMap);
					//退回到未初审状态
					//退回到未初审状态
					Map payOrderMap = (Map)DataAccessor.query("rentContract.getPayOrderById", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if("1".equals(payOrderMap.get("PAY_ORDER").toString())) {
						sqlMap.update("rentContract.rejectContract",
								context.contextMap);
					}
				}
				
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap
						.get("CREDIT_ID")), Long
						.parseLong(StringUtils
								.isEmpty((String) context.contextMap
										.get("RECT_ID")) ? "0"
								: (String) context.contextMap
										.get("RECT_ID")), 
						"拨款管理", "审核", "", memo, 1, DataUtil.longUtil(context.contextMap
								.get("s_employeeId")), null, context.contextMap.get("IP").toString());
//				super.addSysLog(null,
//						Long.parseLong(StringUtils
//								.isEmpty((String) context.contextMap
//										.get("RECT_ID")) ? "0"
//								: (String) context.contextMap.get("RECT_ID")),
//						"拨款", "审核", "", memo, DataUtil
//								.longUtil(context.contextMap
//										.get("s_employeeId")));
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				
				if ("0".equals(String.valueOf(context.contextMap.get("BACKSTATE"))) && "1".equals(context.contextMap.get("Num"))) {
					// 进入保险管理
					insuranceService.startInsurance((String)context.contextMap.get("RECT_ID"), context.contextMap.get("s_employeeId").toString(), sqlMap);
				} else {
					logger.info("非设备款或者是驳回操作，无需投保");
				}
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--业管部审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="
							+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 财务部审核
	@SuppressWarnings("unchecked")
	public void updateFinancialById(Context context) {
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateFinancialById",context.contextMap);
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				String memo = "拨款审批-财务审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				//财务驳回时要更新案件送件状况
				if("2".equals(context.contextMap.get("Num"))){
					sqlMap.update("rentContract.updateRentFileStateByFinance",
							context.contextMap);
					//退回到未初审状态
					Map payOrderMap = (Map)DataAccessor.query("rentContract.getPayOrderById", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if("1".equals(payOrderMap.get("PAY_ORDER").toString())) {
						sqlMap.update("rentContract.rejectContract",
								context.contextMap);
					}
				}
				
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap
										.get("CREDIT_ID")), Long
						.parseLong(StringUtils
								.isEmpty((String) context.contextMap
										.get("RECT_ID")) ? "0"
								: (String) context.contextMap
										.get("RECT_ID")), 
						"拨款管理", "审核", "", memo, 1, DataUtil.longUtil(context.contextMap
								.get("s_employeeId")), null, context.contextMap.get("IP").toString());
				
//				super.addSysLog(null,
//						Long.parseLong(StringUtils
//								.isEmpty((String) context.contextMap
//										.get("RECT_ID")) ? "0"
//								: (String) context.contextMap.get("RECT_ID")),
//						"拨款", "审核", "", memo, DataUtil
//								.longUtil(context.contextMap
//										.get("s_employeeId")));
				/* 2012/1/5 Yang Yun 拨款审批时记录Log */
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--财务部审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="
							+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	//退款业务部主管审批
	public void updateDepartMentReturnMoneyById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap=DataAccessor.getSession();
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateDepartMentById",
						context.contextMap);
				String memo = "退款审批-单位主管审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				context.contextMap.put("memo",memo);
				sqlMap.insert("rentContract.addRenturnMoneyLog", context.contextMap);
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("退款管理--单位主管审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	//退款业管审核
	public void updateExamReturnMoneyById(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMap = DataAccessor.getSession();
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateExamById", context.contextMap);
				String memo = "退款审批-业管部审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				context.contextMap.put("memo",memo);
				sqlMap.insert("rentContract.addRenturnMoneyLog", context.contextMap);
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("退款管理--业管部审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	// 退款财务部审核
	@SuppressWarnings("unchecked")
	public void updateFinancialReturnMoneyById(Context context) {
		Map outputMap = new HashMap();
		SqlMapClient sqlMap = DataAccessor.getSession();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateFinancialById",context.contextMap);
				String memo = "退款审批-财务审核-"
						+ ("1".equals(context.contextMap.get("Num")) ? "通过"
								: "驳回");
				context.contextMap.put("memo",memo);
				sqlMap.insert("rentContract.addRenturnMoneyLog", context.contextMap);
				
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("退款管理--财务部审核错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="
							+ context.contextMap.get("BACKSTATE"));
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//手续费业务部主管审批
		public void updateDepartMentHandlingChargeById(Context context) {
			Map outputMap = new HashMap();
			SqlMapClient sqlMap=DataAccessor.getSession();
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateDepartMentById", context.contextMap);
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		}
		
		//手续费业管审核
		public void updateExamHandlingChargeById(Context context) {
			Map outputMap = new HashMap();
			SqlMapClient sqlMap = DataAccessor.getSession();
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateExamById", context.contextMap);
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspSendRedirect(context, "defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		}

		// 手续费财务部审核
		@SuppressWarnings("unchecked")
		public void updateFinancialHandlingChargeById(Context context) {
			Map outputMap = new HashMap();
			SqlMapClient sqlMap = DataAccessor.getSession();
			try {
				sqlMap.startTransaction();
				sqlMap.update("rentContract.updateFinancialById",context.contextMap);
				sqlMap.commitTransaction();
			} catch (Exception e) {
				try {
					sqlMap.endTransaction();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			Output.jspSendRedirect(context,	"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+ context.contextMap.get("BACKSTATE"));
		}
	// //总经理审核
	// @SuppressWarnings("unchecked")
	// public void updateManagerById(Context context)
	// {
	// Map outputMap = new HashMap();
	// List errList = context.errList;
	// if(errList.isEmpty()) {
	// try {
	// DataAccessor.execute("rentContract.updateManagerById",
	// context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
	// } catch (Exception e) {
	// e.printStackTrace();
	// LogPrint.getLogStackTrace(e, logger);
	// errList.add("拨款管理--总经理审核错误!请联系管理员");
	// }
	// }
	// if(errList.isEmpty()) {
	// Output.jspSendRedirect(context,"defaultDispatcher?__action=backMoney.queryPayMoneys&BACKSTATE="+context.contextMap.get("BACKSTATE"));
	// }else{
	// outputMap.put("errList", errList);
	// Output.jspOutput(outputMap, context, "/error.jsp");
	// }
	// }
	//

	/**
	 * 管理页面查询(奖金拨款,只查询已复核的信息)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentContractBackMoneyByBonus(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		Object payStates = context.contextMap.get("payState");
		String payState = null;
		// Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		List bonusList = new ArrayList();
		List bonus = null;
		SqlMapClient sqlMap = DataAccessor.getSession();
		/*-------- data access --------*/
		if (errList.isEmpty()) {
			try {
				bonus = (List) DataAccessor.query(
						"bonusManage.queryAllBonusList", context.contextMap,
						DataAccessor.RS_TYPE.LIST);

				if (payStates == null || "".equals(payStates)) {
					payState = "0";
				} else {
					payState = payStates.toString();
				}

				if (payState.equals("0"))// 未拨款
				{
					dw = (DataWrap) DataAccessor.query(
							"rentContract.bonusAllM", context.contextMap,
							DataAccessor.RS_TYPE.PAGED);
					List rs = (List) dw.rs;
					for (int i = 0; i < rs.size(); i++) {
						Map bonusMap = (Map) rs.get(i);

						List bonusTypeL = new ArrayList();

						String bonusTypeStr = bonusMap.get("MONEY_VALUE")
								.toString();
						String[] bonusStr = bonusTypeStr.split(",");
						for (int j = 0; j < bonusStr.length; j++) {
							String bonusSun = bonusStr[j];
							String[] bonusSunL = bonusSun.split("-");

							Map bonusMaps = new HashMap();
							bonusMaps.put("typeName", bonusSunL[1]);
							bonusMaps.put("typeUpMoney", bonusSunL[0]);
							bonusTypeL.add(bonusMaps);
						}
						bonusMap.put("typeList", bonusTypeL);
						bonusList.add(bonusMap);
					}

					outputMap.put("update", context.contextMap.get("update"));
				} else if (payState.equals("1"))// 拨款中
				{
					dw = (DataWrap) DataAccessor.query(
							"insurance.queryPayMoneyByFirstBonusing",
							context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if (payState.equals("2"))// 拨款成功
				{
					dw = (DataWrap) DataAccessor.query(
							"insurance.queryPayMoneyByFirstBonussuccess",
							context.contextMap, DataAccessor.RS_TYPE.PAGED);
				} else if (payState.equals("3"))// 驳回
				{
					dw = (DataWrap) DataAccessor.query(
							"insurance.queryPayMoneyByFirstBonusback",
							context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--查询奖金拨款错误!请联系管理员");
			}
		}

		/*-------- output --------*/
		outputMap.put("bonus", bonus);
		outputMap.put("dw", dw);
		outputMap.put("bonusList", bonusList);
		outputMap.put("payState", context.contextMap.get("payState"));
		outputMap.put("content", context.contextMap.get("content"));

		if (errList.isEmpty()) {
			if (payState.equals("0"))// 未拨款
			{
				Output.jspOutput(outputMap, context, "/backMoney/bonusPay.jsp");
			} else {
				Output.jspOutput(outputMap, context,
						"/backMoney/backMoneyBonus.jsp");
			}

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	// 奖金拨款
	@SuppressWarnings("unchecked")
	public void getBonusPayList(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"TYPE_NUMBER", "00");
		// String[] conPrice = HTMLUtil.getParameterValues(context.getRequest(),
		// "insu_price", "0");

		String bonus_name = null;
		String conIncu_id = "";
		float price = 0f;
		if (con != null) {
			if (!(con[0].equals("00"))) {
				if (con.length > 0) {
					// System.out.println("-----------------------"+con.length);
					for (int i = 0; i < con.length; i++) {
						if (i == 0) {
							conIncu_id = con[i];
						} else {
							conIncu_id = conIncu_id + "," + con[i];
						}
						bonus_name = context.contextMap.get(
								"bonus_name" + con[i]).toString();
						try {
							context.contextMap.put("type_number", con[i]);
							List dw = (List) DataAccessor.query(
									"rentContract.bonusMoneyByNum",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
							for (int j = 0; j < dw.size(); j++) {
								Map dwMap = (Map) dw.get(j);
								Object conPrices = dwMap.get("UPMONEY");
								if (conPrices != null && !"".equals(conPrices)) {
									// System.out.println(conPrices);
									price += Float.parseFloat(conPrices
											.toString());

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					// System.out.println("++++++++++++++++++="+conIncu_id);

				}

			}

		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date time = new Date();
		String times = sf.format(time).toString();
		List errList = context.errList;
		List psTypeList = null;

		if (errList.isEmpty()) {
			try {
				// 拨款方式
				context.contextMap.put("dataType", "拨款方式");
				psTypeList = (List<Map>) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);

				Map outputMap = new HashMap();
				outputMap.put("psTypeList", psTypeList);
				outputMap.put("priceCount", price);
				outputMap.put("times", times);
				outputMap.put("bonus_name", bonus_name);
				outputMap.put("conIncu_id", conIncu_id);
				Output.jspOutput(outputMap, context,
						"/backMoney/bonusDetilPayNew.jsp");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 根据保单编号新建一条付款记录
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createPayMoneyBonusManagerNew(Context context) {

		Map outputMap = new HashMap();
		List errList = context.errList;
		@SuppressWarnings("unused")
		int backState = Integer.parseInt(context.request
				.getParameter("backState"));
		String[] conIncu_id = context.request.getParameter("conIncu_id")
				.toString().split(",");
		SqlMapClient sqlMapper = DataAccessor.getSession();

		Long numId;
		if (errList.isEmpty()) {
			try {
				sqlMapper.startTransaction();
				numId = (Long) sqlMapper.insert(
						"rentContract.createPayMoneyManagerByBonus",
						context.contextMap);
				// DataAccessor.execute("rentContract.createPayMoneyManagerByInsurance",
				// context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				context.contextMap.put("numId", numId);

				for (int i = 0; i < conIncu_id.length; i++) {

					// 插入保单和付款表中间表
					context.contextMap.put("insu_id", conIncu_id[i]);
					sqlMapper.insert("rentContract.createPayMoneyBonusJoin",
							context.contextMap);

				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理--添加奖金付款记录错误!请联系管理员");
			} finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		if (errList.isEmpty()) {

			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=backMoney.queryRentContractBackMoneyByBonus");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	public void saveExpectedDate(Context context) {

		String log = "employeeId=" + context.contextMap.get("s_employeeId")
				+ "......saveExpectedDate";

		if (logger.isDebugEnabled()) {
			logger.debug(log + " start.....");
		}

		try {
			DataAccessor.execute("rentContract.updateExpectedDate",
					context.contextMap, OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(log + " end.....");
		}

		this.queryPayMoneys(context);
	}

	/*
	 * Add by Michael 2012 07-31 点击拨款审批时要检查案件是否是交机前拨款 如果是交机前拨款要检查额度是否满足
	 * 
	 * 授信额度卡关
	 * 交机前
	 * 
	 */

	public void checkAdvanceMachineGrantPrice(Context context) {
		// 首先看此案是否是交机前拨款，将金额、案件带出来
		Map callback = new HashMap();
		List errList = context.errList;
		Map paraMap = null;
		Map applyTotalMoney = null;
		Map suplGrantMoneyMap = null;
		Map totalPayMoneyMap = null;
		String strReturnStr = "";
		String advanceMachine = "";
		Map isInvoiceCome = null;
		Double advanceLastPrice=0.0;
		
		BigDecimal payMoneyBigDecimal = new BigDecimal(
				String.valueOf(context.contextMap.get("pay_money")));
		//供应商级别
		Map  supplierLevel=null;
		
		if (errList.isEmpty()) {
			try {
				//*****授信额度卡关*****
				String credit_id = (String) context.contextMap.get("CREDIT_ID");
				Double pay_money = payMoneyBigDecimal.doubleValue();
				String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
				CheckedResult result = baseService.checkSuplCreditLine(credit_id, CREDIT_LINE_TYPE.PAY_BEFORE);
				if (!result.getResult()) {
					strReturnStr = result.getMsg();
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("拨款管理交机前拨款额度检核错误！");
			}
			
		}
		if (errList.isEmpty()) {
			callback.put("strReturnStr", strReturnStr);
			Output.jsonOutput(callback, context);
		} else {
			callback.put("errList", errList);
			Output.jspOutput(callback, context, "/error.jsp");
		}

	}
	
	//拨款审批导出Excel add by ShenQi 2012-8-31
	public static List<PayMoneyTO> payMoneyAuditList(String startDate,String endDate) {
		
		List<PayMoneyTO> payMoneyAuditList=null;
		List<PayMoneyTO> resultList=new ArrayList<PayMoneyTO>();
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("START_DATE",startDate);
			param.put("END_DATE",endDate);
			payMoneyAuditList=(List<PayMoneyTO>)DataAccessor.query("rentContract.payMoneyAuditList",param,RS_TYPE.LIST);
			
			//由于一个案件会对应多个机器,所以通过Java代码把相同案件多个机器加入到一个TO中
			for(int i=0;payMoneyAuditList!=null&&i<payMoneyAuditList.size();i++) {
				if(i==0) {
					//第一条数据不存在比较
					resultList.add(payMoneyAuditList.get(i));
				} else {
					//如果不是同一个案子则往List里加数据
					if(!payMoneyAuditList.get(i-1).getCreditRunCode().equals(payMoneyAuditList.get(i).getCreditRunCode())) {
						resultList.add(payMoneyAuditList.get(i));
					} else {//如果是同一个案子,则往上一条记录里加入这台机器
						resultList.get(resultList.size()-1).setEquipment(resultList.get(resultList.size()-1).getEquipment()+","+payMoneyAuditList.get(i).getEquipment());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	public void queryPayMoneyRentFile(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		List visitImage = null ;
		Map rentFileSenderState=null;
		boolean sendFile=false;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInforForSales", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询担保人资料的信息
				visitImage=(List) DataAccessor.query("backVisitImage.queryVistitImageByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				
				//增加权限判断  是否有保存keyin资料的权限
				if("2".equals(context.contextMap.get("cardFlag"))){
					
					List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
						if("294".equals(resourceIdList.get(i))) {
							sendFile=true;
						}
					}
					rentFileSenderState=(Map)DataAccessor.query("rentFile.getRentFileSenderState", context.contextMap, DataAccessor.RS_TYPE.MAP);
				} 

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("CONTRACT_TYPE", context.contextMap.get("CONTRACT_TYPE"));
			outputMap.put("visitImage", visitImage) ;
			outputMap.put("rentFileSenderState",rentFileSenderState);
			outputMap.put("sendFile", sendFile);
			
			Output.jspOutput(outputMap, context, "/backMoney/rentFileForShow.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}	


	public void showExamineRentFile(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		Map payMoneyInfo=null;
		try {
			context.contextMap.put("cardFlag", "2");
			context.contextMap.put("prcd_id", context.contextMap.get("CREDIT_ID"));
			context.contextMap.put("CONTRACT_TYPE", DataUtil.intUtil(context.contextMap.get("CONTRACT_TYPE"))+1);
			
			insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
			infor=(Map)DataAccessor.query("rentFile.selectInforForSales", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
			outputMap.put("insorupd", insorupd);
			outputMap.put("infor", infor);
			outputMap.put("CREDIT_ID", context.contextMap.get("CREDIT_ID"));
			outputMap.put("ID", context.contextMap.get("backId"));
			outputMap.put("RECT_ID", context.contextMap.get("RECT_ID")) ;
			outputMap.put("BACKSTATE", context.contextMap.get("BACKSTATE"));
			outputMap.put("FSS_ID", context.contextMap.get("FSS_ID"));
			context.contextMap.put("ID", context.contextMap.get("backId"));
			payMoneyInfo=(Map)DataAccessor.query("rentContract.getPayMoneyDetailByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("payMoneyInfo", payMoneyInfo) ;
			outputMap.put("payMoneyPassCount", (Integer)DataAccessor.query("rentContract.queryPayMoneyIsPassed", context.contextMap, DataAccessor.RS_TYPE.OBJECT)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}	
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/backMoney/examineRentFile.jsp");
		}else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	//Add by Michael 2012 11-15
	//申请拨款前检查是否是首拨款，如果是首拨款要检查是否要增加keyin 检核表
	public void checkFirstPayMoney(Context context)
	{
		Map outputMap = new HashMap();
		int isFirstPayMoney = 0;
		boolean checkFirstPayMoney=true;
		try {
			isFirstPayMoney=(Integer)DataAccessor.query("rentContract.queryIsFirstPayMoney", context.contextMap,DataAccessor.RS_TYPE.OBJECT);
			if (isFirstPayMoney==0){
				int rentFileSender=(Integer)DataAccessor.query("rentContract.queryRentFileSenderState", context.contextMap,DataAccessor.RS_TYPE.OBJECT);
				if(rentFileSender>0){
					checkFirstPayMoney=true;
				}else{
					checkFirstPayMoney=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("checkFirstPayMoney", checkFirstPayMoney);
		Output.jsonOutput(outputMap, context);
	}

	//Add by Michael 2012-12-10 For 业管初审
	public void examRentFileForHW(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		Map rentFileSenderState=null;
		//问题类别列表
		List issureTypeList= null;
		List invoiceList = null;
		List invoiceTotal = null;
		try {
			context.contextMap.put("cardFlag", "2");
			context.contextMap.put("prcd_id", context.contextMap.get("CREDIT_ID"));
			context.contextMap.put("CONTRACT_TYPE", DataUtil.intUtil(context.contextMap.get("CONTRACT_TYPE"))+1);
			
			if("false".equals(context.contextMap.get("saveFlag"))){
				insorupd=(List)DataAccessor.query("rentFile.selectRentFileByHW", context.contextMap, DataAccessor.RS_TYPE.LIST);
			}else{
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
				// add by ZhangYizhou on 2014-06-27 Begin
				List tempInsorupd = new ArrayList();
				for(Object item :insorupd) {
					Map rentFile = (Map)item;
					Map param = new HashMap();
					param.put("refd_id", rentFile.get("REFD_ID"));
					rentFile.put("LOG",(List)DataAccessor.query("rentFile.selectRentFileLog", param, DataAccessor.RS_TYPE.LIST));
					tempInsorupd.add(rentFile);
				}
				insorupd = tempInsorupd;
				// add by ZhangYizhou on 2014-06-27 End
			}
			invoiceTotal=(List)DataAccessor.query("invoice.queryPage", context.contextMap, DataAccessor.RS_TYPE.LIST);
			invoiceList=(List)DataAccessor.query("rentFile.selectInvoice", context.contextMap, DataAccessor.RS_TYPE.LIST);
			infor=(Map)DataAccessor.query("rentFile.selectInforForSales", context.contextMap, DataAccessor.RS_TYPE.MAP);
			rentFileSenderState=(Map)DataAccessor.query("rentContract.queryRentFileSenderStateByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("insorupd", insorupd);
			outputMap.put("infor", infor);
			outputMap.put("CREDIT_ID", context.contextMap.get("CREDIT_ID"));
			outputMap.put("FSS_ID", context.contextMap.get("FSS_ID"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("CONTRACT_TYPE", context.contextMap.get("CONTRACT_TYPE"));
			outputMap.put("saveFlag", context.contextMap.get("saveFlag"));
			outputMap.put("rentFileSenderState", rentFileSenderState);
			outputMap.put("invoices", invoiceList);
			outputMap.put("ALLOW_CHANGE_FILETYPE", RentContractConstants.ALLOW_CHANGE_FILETYPE);
			if ( invoiceTotal!=null && invoiceTotal.size() == 1) {
				outputMap.put("invoiceTotal", invoiceTotal.get(0));
			}
			issureTypeList=getInfoTestList(context);
			outputMap.put("issureTypeList", issureTypeList) ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}	
		if (errList.isEmpty()) {
			if("false".equals(context.contextMap.get("saveFlag"))){
				Output.jspOutput(outputMap, context, "/rentcontract/examRentFileForShow.jsp");
			}else{
				Output.jspOutput(outputMap, context, "/rentcontract/examRentFile.jsp");
			}
		}else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	//查询资料细项的问题类别
	public List getInfoTestList(Context context)
	{
		List infoTestList = new ArrayList();
		List errList = context.errList;
		try {	
			List<Map> resourceIdList=(List<Map>) DataAccessor.query("rentFile.getRentFileLossReason", context.contextMap, DataAccessor.RS_TYPE.LIST);				
			int i=0;
			for (Map map : resourceIdList) {
				LabelValueBean labelValueBean=new LabelValueBean();
				labelValueBean.setLabel(String.valueOf(map.get("REASON")));
				labelValueBean.setValue(i);
				infoTestList.add(labelValueBean);
				i++;
			}
		} catch (Exception e) {
			errList.add("查询案件文件不齐原因！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		return infoTestList;
	}
	
	public void checkBlackBank(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		int count=0;
		try {	
			count=(Integer)DataAccessor.query("bankAccount.getBlackBankByBankName", context.contextMap,DataAccessor.RS_TYPE.OBJECT);
			
		}catch (Exception e) {
			errList.add("检查银行黑名单错误！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}	
		if (errList.isEmpty()) {
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
	public void checkExamCanDo(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		boolean flag = true;
		String msg = null;
		String credit_id = (String) context.contextMap.get("credit_id");
		if(!baseService.getIsExpiredByCreditId(credit_id)){
			flag = false;
			msg = "案件已过期,操作失败。";
		} /*else if (!checkPledge(credit_id)) {
			flag = false;
			msg = "保证金未销账或销账金额不足，不能完成保证金拨款。";
		}*/
		outputMap.put("flag", flag);
		outputMap.put("msg", msg);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 业管部审核通过前检查合同管理是否全部通过'文审'、'复核'、'还款'，并返回合同进行至哪一步 
	 * step	
	 * 		1:文审未过	
	 * 		2:复核未过	
	 * 		3:还款未过	
	 * 		4:全部通过
	 * @param context
	 */
	public void checkExamPassState(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		String step = "0";
		try {
			result = (Map<String, Object>)DataAccessor.query("rentContract.queryRentContract", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if(result.get("RECT_ID") == null){
				//文审未过
				step = "1";
			} else {
				if(result.get("STATUS") != null && "0".equals(result.get("STATUS").toString())){
					if("0".equals(result.get("RECT_STATUS").toString()) || "2".equals(result.get("RECT_STATUS").toString())){
						//复核未过
						step = "2";
					} else if("1".equals(result.get("RECT_STATUS").toString()) && Integer.parseInt(result.get("PAYLISTFLAG").toString()) > 0){
						//还款未过
						step = "3";
					} else {
						//全部通过
						step = "4";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("step", step);
		Output.jsonOutput(outputMap, context);
	}
	
	public boolean checkPledge(String credit_id){
		boolean flag = false;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", credit_id);
		Double pledge = (Double) baseService.queryForObj("rentContract.getPledgeRealPrice", paramMap);
		Double pay = (Double) baseService.queryForObj("rentContract.getPledgePayPrice", paramMap);
		if ((pledge == null ? 0 : pledge) >= (pay == null ? 0 : pay)) {
			flag = true;
		}
		return flag;
	}
	
	
}

























