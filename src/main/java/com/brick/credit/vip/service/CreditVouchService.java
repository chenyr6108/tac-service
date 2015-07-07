package com.brick.credit.vip.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.FileUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 担保人信息
 * 
 */
public class CreditVouchService extends AService {
	Log logger = LogFactory.getLog(CreditVouchService.class);

	/**
	 * 查询担保人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreditVouchers(Context context) {
		Map outputMap = new HashMap();
		List corpList = new ArrayList();;
		List corpList_t = null;
		List natuList = null;
		Map creditMap = null;

		List corpTypeList = null;
		List natuTypeList = null;
		List<Map> vouchReportLists=null;
		
		Map trueContact = null;
		List corpProperty = null;
		List natuProperty = null;
		
		List errList = context.errList ;
		// List creditFinaceStatement = null;
		// creditMap.put("ID", context.contextMap.get("credit_id"));
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);

			corpList_t = (List) DataAccessor.query(
					"creditVoucher.selectCorpByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			natuList = (List) DataAccessor.query(
					"creditVoucher.selectVouchNatu", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);

			context.contextMap.put("dataType", "证件类型");
			natuTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("natuTypeList", natuTypeList);
			
			trueContact = (Map) DataAccessor.query(
					"trueContact.getTrueContactByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("trueContact", trueContact);
			corpProperty = (List) DataAccessor.query(
					"creditVoucher.getCorpProperty", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			natuProperty = (List) DataAccessor.query(
					"creditVoucher.getNatuProperty", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpProperty", corpProperty);
			outputMap.put("natuProperty", natuProperty);
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			//财务报表文件
			if (corpList_t != null && corpList_t.size() > 0) {
				Map<String, Object> map = null;
				for (Object object : corpList_t) {
					map = (Map<String, Object>) object;
					map.put("fileList", (List)DataAccessor.query("creditVoucher.getVoucherFile", map, RS_TYPE.LIST));
					corpList.add(map);
				}
				
			}
			
			List list = new ArrayList();
			for (int i = 0; i < corpList.size(); i++) {
				Map map = (Map) corpList.get(i);

				// List lis = new ArrayList();
				Map maps = new HashMap();

				// 开户银行
				List bankAccountList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpBankAccountByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("bankAccountList", bankAccountList);
				// 公司股东及份额
				List shareholderList = (List) DataAccessor.query(
						"creditVoucher.getCreditShareholderByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("shareholderList", shareholderList);
				// 法人公司项目
				List projectList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpProjectByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("projectList", projectList);

				// 财务信息 baiman- 
//				List creditFinaceStatement = (List) DataAccessor.query(
//						"creditVoucher.getCreditFinaceStatementByPjcccId", map,
//						DataAccessor.RS_TYPE.LIST);
//				Map v1 = null;
//				Map v2 = null;
//				Map v3 = null;
//				Map v4 = null;
//				Map v5 = null;
//				if (creditFinaceStatement.size() >= 1) {
//					v1 = (Map) creditFinaceStatement.get(0);
//					v2 = (Map) creditFinaceStatement.get(1);
//					v3 = (Map) creditFinaceStatement.get(2);
//					v4 = (Map) creditFinaceStatement.get(3);
//					v5 = (Map) creditFinaceStatement.get(4);
//				}
//				maps.put("v1", v1);
//				maps.put("v2", v2);
//				maps.put("v3", v3);
//				maps.put("v4", v4);
//				maps.put("v5", v5);

				// 过往记录项目
				List creditPriorProject = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpPriorProjectsByPjcccId",
						map, DataAccessor.RS_TYPE.LIST);
				maps.put("creditPriorProject", creditPriorProject);

				list.add(maps);

			}
			outputMap.put("list", list);
			//财务报表
			if(corpList.size()>0){
				List objlist=new ArrayList();
				vouchReportLists=(List<Map>)DataAccessor.query("creditVoucher.getVoucherReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(vouchReportLists.size()>0){
					
					for(int i=1;i<=vouchReportLists.size();i++){
						vouchReportLists.get(i-1).put("objcount", i);
						objlist.add( vouchReportLists.get(i-1));
					}
					outputMap.put("objlist",objlist);
				}
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询担保人信息错误!请联系管理员") ;
		}
		outputMap.put("creditMap", creditMap);
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		outputMap.put("corpList", corpList);
		outputMap.put("natuList", natuList);
		outputMap.put("prcd_id", context.contextMap.get("credit_id"));
		outputMap.put("credit_id", context.contextMap.get("credit_id"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 添加法人为担保人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void addCreditCorp(Context context) {

		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			// 法人身份
			String LEGAL_ID_CARD = (String) context.contextMap
					.get("LEGAL_ID_CARD");
			String idCard = (String) context.contextMap.get("otherPermit");
			if (LEGAL_ID_CARD == null || LEGAL_ID_CARD.equals("")) {
				context.contextMap.put("LEGAL_ID_CARD", idCard);
			}
			// 联系人
			String LINK_ID_CARD = (String) context.contextMap
					.get("LINK_ID_CARD");
			String idCards = (String) context.contextMap.get("linkPermit");
			if (LINK_ID_CARD == null || LINK_ID_CARD.equals("")) {
				context.contextMap.put("LINK_ID_CARD", idCards);
			}

			// 担保人法人基本信息
			long pjccc_id = Long.valueOf(sqlMapper.insert(
					"creditVoucher.createCreditCustomerCorp",
					context.contextMap).toString());
			context.contextMap.put("pjccc_id", pjccc_id);

			

			// 公司股东信息
			sqlMapper.delete(
					"creditVoucher.deleteCreditCorpShareholderByPjcccId",
					context.contextMap);
			String[] str1 = context.getRequest().getParameterValues(
					"HOLDER_NAME");
			if (str1 != null) {
				// if (str1.equals("")) {
				String[] HOLDER_NAME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_NAME", "");
				String[] HOLDER_CAPITAL = HTMLUtil.getParameterValues(
						context.request, "HOLDER_CAPITAL", "");
				String[] HOLDER_WAY = HTMLUtil.getParameterValues(
						context.request, "HOLDER_WAY", "");
				String[] HOLDER_RATE = HTMLUtil.getParameterValues(
						context.request, "HOLDER_RATE", "");
				String[] HOLDER_MOME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_MOME", "");
				for (int i = 0; i < HOLDER_NAME.length; i++) {
					Map shareholder = new HashMap();
					shareholder.put("HOLDER_NAME", HOLDER_NAME[i]);

					shareholder.put("HOLDER_CAPITAL", HOLDER_CAPITAL[i]
							.equals("") ? 0 : HOLDER_CAPITAL[i]);
					shareholder.put("HOLDER_WAY", HOLDER_WAY[i]);
					shareholder.put("HOLDER_RATE",
							HOLDER_RATE[i].equals("") ? 0 : HOLDER_RATE[i]);
					shareholder.put("HOLDER_MOME", HOLDER_MOME[i]);
					shareholder.put("pjccc_id", pjccc_id);
					sqlMapper.insert(
							"creditVoucher.createCreditCorpShareholder",
							shareholder);
				}
			}

			// 公司项目信息
			sqlMapper.delete("creditVoucher.deleteCreditCorpProjectByPjcccId",
					context.contextMap);
			String[] str2 = context.request.getParameterValues("PROJECT_NAMES");
			if (str2 != null) {
				// if (str2.equals("")) {
				String[] PROJECT_NAME = HTMLUtil.getParameterValues(
						context.request, "PROJECT_NAMES", "");
				String[] PROJECT_DATE = HTMLUtil.getParameterValues(
						context.request, "PROJECT_DATE", "");
				String[] PROJECT_CONTENT = HTMLUtil.getParameterValues(
						context.request, "PROJECT_CONTENT", "");
				for (int i = 0; i < PROJECT_NAME.length; i++) {
					Map project = new HashMap();
					project.put("PROJECT_NAME", PROJECT_NAME[i]);
					project.put("PROJECT_DATE", PROJECT_DATE[i]);
					project.put("PROJECT_CONTENT", PROJECT_CONTENT[i]);
					project.put("pjccc_id", pjccc_id);
					sqlMapper.insert("creditVoucher.createCreditCorpProject",
							project);
				}
			}

//			 添加财务信息    baiman-
//			String[] str3 = context.request
//					.getParameterValues("PROJECT_NAMEss");
//			if (str3 != null) {
//
//				String[] PROJECT_NAME = HTMLUtil.getParameterValues(context
//						.getRequest(), "PROJECT_NAMEss", "");
//				String[] MONEY_FUNDS = HTMLUtil.getParameterValues(context
//						.getRequest(), "MONEY_FUNDSs", "");
//				String[] ACCOUNTS_RECEIVABLE = HTMLUtil.getParameterValues(
//						context.getRequest(), "ACCOUNTS_RECEIVABLEs", "");
//				String[] STOCK = HTMLUtil.getParameterValues(context
//						.getRequest(), "STOCKs", "");
//				String[] CAPITAL_ASSERTS = HTMLUtil.getParameterValues(context
//						.getRequest(), "CAPITAL_ASSERTSs", "");
//				String[] TOTAL_ASSERTS = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_ASSERTSs", "");
//				String[] SHORTTIME_LOAN = HTMLUtil.getParameterValues(context
//						.getRequest(), "SHORTTIME_LOANs", "");
//				String[] ACCOUNTS_PAYABLE = HTMLUtil.getParameterValues(context
//						.getRequest(), "ACCOUNTS_PAYABLEs", "");
//				String[] TOTAL_OWES = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_OWESs", "");
//				String[] CONTRIBUTED_CAPITAL = HTMLUtil.getParameterValues(
//						context.getRequest(), "CONTRIBUTED_CAPITALs", "");
//				String[] CAPITAL_RESERVE = HTMLUtil.getParameterValues(context
//						.getRequest(), "CAPITAL_RESERVEs", "");
//				String[] UNDISTRIBUTED_PROFIT = HTMLUtil.getParameterValues(
//						context.getRequest(), "UNDISTRIBUTED_PROFITs", "");
//				String[] SALES_REVENUE = HTMLUtil.getParameterValues(context
//						.getRequest(), "SALES_REVENUEs", "");
//				String[] COST_OF_MARKETING = HTMLUtil.getParameterValues(
//						context.getRequest(), "COST_OF_MARKETINGs", "");
//				String[] PERIOD_EXPENSE = HTMLUtil.getParameterValues(context
//						.getRequest(), "PERIOD_EXPENSEs", "");
//				String[] TOTAL_PROFIT = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_PROFITs", "");
//				String[] DEBTR = HTMLUtil.getParameterValues(context
//						.getRequest(), "DEBTRs", "");
//				String[] PROFIT_MARGIN = HTMLUtil.getParameterValues(context
//						.getRequest(), "PROFIT_MARGINs", "");
//				String[] TTM = HTMLUtil.getParameterValues(
//						context.getRequest(), "TTMs", "");
//				String[] SALES_GROWTH = HTMLUtil.getParameterValues(context
//						.getRequest(), "SALES_GROWTHs", "");
//				String[] NAGR = HTMLUtil.getParameterValues(context
//						.getRequest(), "NAGRs", "");
//				for (int i = 0; i < PROJECT_NAME.length; i++) {
//					Map tempMap = new HashMap();
//					tempMap.put("PROJECT_NAME", StringFilter(PROJECT_NAME[i]));
//					tempMap.put("MONEY_FUNDS", StringFilter(MONEY_FUNDS[i]));
//					tempMap.put("ACCOUNTS_RECEIVABLE",
//							StringFilter(ACCOUNTS_RECEIVABLE[i]));
//					tempMap.put("STOCK", StringFilter(STOCK[i]));
//					tempMap.put("CAPITAL_ASSERTS",
//							StringFilter(CAPITAL_ASSERTS[i]));
//					tempMap
//							.put("TOTAL_ASSERTS",
//									StringFilter(TOTAL_ASSERTS[i]));
//					tempMap.put("SHORTTIME_LOAN",
//							StringFilter(SHORTTIME_LOAN[i]));
//					tempMap.put("ACCOUNTS_PAYABLE",
//							StringFilter(ACCOUNTS_PAYABLE[i]));
//					tempMap.put("TOTAL_OWES", StringFilter(TOTAL_OWES[i]));
//					tempMap.put("CONTRIBUTED_CAPITAL",
//							StringFilter(CONTRIBUTED_CAPITAL[i]));
//					tempMap.put("CAPITAL_RESERVE",
//							StringFilter(CAPITAL_RESERVE[i]));
//					tempMap.put("UNDISTRIBUTED_PROFIT",
//							StringFilter(UNDISTRIBUTED_PROFIT[i]));
//					tempMap
//							.put("SALES_REVENUE",
//									StringFilter(SALES_REVENUE[i]));
//					tempMap.put("COST_OF_MARKETING",
//							StringFilter(COST_OF_MARKETING[i]));
//					tempMap.put("PERIOD_EXPENSE",
//							StringFilter(PERIOD_EXPENSE[i]));
//					tempMap.put("TOTAL_PROFIT", StringFilter(TOTAL_PROFIT[i]));
//					tempMap.put("DEBTR", StringFilter(DEBTR[i]));
//					tempMap
//							.put("PROFIT_MARGIN",
//									StringFilter(PROFIT_MARGIN[i]));
//					tempMap.put("TTM", StringFilter(TTM[i]));
//					tempMap.put("SALES_GROWTH", StringFilter(SALES_GROWTH[i]));
//					tempMap.put("NAGR", StringFilter(NAGR[i]));
//					tempMap.put("pjccc_id", pjccc_id);
//					tempMap.put("s_employeeId", context.contextMap
//							.get("s_employeeId"));
//					sqlMapper.insert(
//							"creditVoucher.createCreditFinaneStatement",
//							tempMap);
//				}
//			}
//			
			
			//添加财务信息 附件
			saveVoucherReportFile(context);
			
			/*//添加财务信息    baiman+
			sqlMapper.delete("creditVoucher.deleteVoucherReport", context.contextMap);
			String[] project_item= HTMLUtil.getParameterValues(context.getRequest(), "project_itemV", "0");
			String [] ca_cash_price= HTMLUtil.getParameterValues(context.getRequest(), "ca_cash_priceV", "0");
			String [] ca_short_Invest= HTMLUtil.getParameterValues(context.getRequest(), "ca_short_InvestV", "0");
			String [] ca_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_bills_shouldV", "0");
			String [] ca_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_Funds_shouldV", "0");
			String [] ca_Goods_stock= HTMLUtil.getParameterValues(context.getRequest(), "ca_Goods_stockV", "0");
			String [] ca_other= HTMLUtil.getParameterValues(context.getRequest(), "ca_otherV", "0");
			String [] fa_land= HTMLUtil.getParameterValues(context.getRequest(), "fa_landV", "0");
			String [] fa_buildings= HTMLUtil.getParameterValues(context.getRequest(), "fa_buildingsV", "0");
			String [] fa_equipments= HTMLUtil.getParameterValues(context.getRequest(), "fa_equipmentsV", "0");
			String [] fa_rent_Assets= HTMLUtil.getParameterValues(context.getRequest(), "fa_rent_AssetsV", "0");
			String [] fa_transports= HTMLUtil.getParameterValues(context.getRequest(), "fa_transportsV", "0");
			String [] fa_other= HTMLUtil.getParameterValues(context.getRequest(), "fa_otherV", "0");
			String [] fa_Depreciations= HTMLUtil.getParameterValues(context.getRequest(), "fa_DepreciationsV", "0");
			String [] fa_Incompleted_projects= HTMLUtil.getParameterValues(context.getRequest(), "fa_Incompleted_projectsV", "0");
			String [] lang_Invest= HTMLUtil.getParameterValues(context.getRequest(), "lang_InvestV", "0");
			String [] other_Assets= HTMLUtil.getParameterValues(context.getRequest(), "other_AssetsV", "0");
			String [] sd_short_debt= HTMLUtil.getParameterValues(context.getRequest(), "sd_short_debtV", "0");
			String [] sd_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_bills_shouldV", "0");
			String [] sd_funds_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_funds_shouldV", "0");
			String [] sd_other_pay= HTMLUtil.getParameterValues(context.getRequest(), "sd_other_payV", "0");
			String [] sd_shareholders= HTMLUtil.getParameterValues(context.getRequest(), "sd_shareholdersV", "0");
			String [] sd_one_year= HTMLUtil.getParameterValues(context.getRequest(), "sd_one_yearV", "0");
			String [] sd_other= HTMLUtil.getParameterValues(context.getRequest(), "sd_otherV", "0");
			String [] lang_debt= HTMLUtil.getParameterValues(context.getRequest(), "lang_debtV", "0");
			String [] other_long_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_long_debtV", "0");
			String [] other_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_debtV", "0");
			String [] share_capital= HTMLUtil.getParameterValues(context.getRequest(), "share_capitalV", "0");
			String [] surplus_Capital= HTMLUtil.getParameterValues(context.getRequest(), "surplus_CapitalV", "0");
			String [] surplus_income= HTMLUtil.getParameterValues(context.getRequest(), "surplus_incomeV", "0");
			String [] this_losts= HTMLUtil.getParameterValues(context.getRequest(), "this_lostsV", "0");
			String [] project_changed= HTMLUtil.getParameterValues(context.getRequest(), "project_changedV", "0");
			String [] s_start_date= HTMLUtil.getParameterValues(context.getRequest(), "s_start_dateV", "0");
			String [] s_sale_net_income= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_net_incomeV", "0");
			String [] s_sale_cost= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_costV", "0");
			String [] s_other_gross_profit= HTMLUtil.getParameterValues(context.getRequest(), "s_other_gross_profitV", "0");
			String [] s_operating_expenses= HTMLUtil.getParameterValues(context.getRequest(), "s_operating_expensesV", "0");
			String [] s_nonbusiness_income= HTMLUtil.getParameterValues(context.getRequest(), "s_nonbusiness_incomeV", "0");
			String [] s_interest_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_interest_expenseV", "0");
			String [] s_other_nonbusiness_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_other_nonbusiness_expenseV", "0");
			String [] s_income_tax_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_income_tax_expenseV", "0");
			String [] ca_other_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_other_Funds_shouldV", "0");		
			for(int i=0;i<project_item.length;i++){
				Map tempMap=new HashMap();
				tempMap.put("project_item", project_item[i]);
				tempMap.put("ca_cash_price", ca_cash_price[i]);
				tempMap.put("ca_short_Invest", ca_short_Invest[i]);
				tempMap.put("ca_bills_should", ca_bills_should[i]);
				tempMap.put("ca_Funds_should", ca_Funds_should[i]);
				tempMap.put("ca_Goods_stock", ca_Goods_stock[i]);
				tempMap.put("ca_other", ca_other[i]);
				tempMap.put("fa_land", fa_land[i]);
				tempMap.put("fa_buildings", fa_buildings[i]);
				tempMap.put("fa_equipments", fa_equipments[i]);
				tempMap.put("fa_rent_Assets", fa_rent_Assets[i]);
				tempMap.put("fa_transports", fa_transports[i]);
				tempMap.put("fa_other", fa_other[i]);
				tempMap.put("fa_Depreciations", fa_Depreciations[i]);
				tempMap.put("fa_Incompleted_projects", fa_Incompleted_projects[i]);
				tempMap.put("lang_Invest", lang_Invest[i]);
				tempMap.put("other_Assets", other_Assets[i]);
				tempMap.put("sd_short_debt", sd_short_debt[i]); 
				tempMap.put("sd_bills_should", sd_bills_should[i]); 
				tempMap.put("sd_funds_should", sd_funds_should[i]); 
				tempMap.put("sd_other_pay", sd_other_pay[i]); 
				tempMap.put("sd_shareholders", sd_shareholders[i]); 
				tempMap.put("sd_one_year", sd_one_year[i]); 
				tempMap.put("sd_other", sd_other[i]); 
				tempMap.put("lang_debt", lang_debt[i]); 
				tempMap.put("other_long_debt", other_long_debt[i]); 
				tempMap.put("other_debt", other_debt[i]); 
				tempMap.put("share_capital", share_capital[i]); 
				tempMap.put("surplus_Capital", surplus_Capital[i]); 
				tempMap.put("surplus_income", surplus_income[i]); 
				tempMap.put("this_losts", this_losts[i]); 
				tempMap.put("project_changed", project_changed[i]); 
				tempMap.put("s_start_date", s_start_date[i]); 
				tempMap.put("s_sale_net_income", s_sale_net_income[i]); 
				tempMap.put("s_sale_cost", s_sale_cost[i]); 
				tempMap.put("s_other_gross_profit", s_other_gross_profit[i]); 
				tempMap.put("s_operating_expenses", s_operating_expenses[i]); 
				tempMap.put("s_nonbusiness_income", s_nonbusiness_income[i]); 
				tempMap.put("s_interest_expense", s_interest_expense[i]); 
				tempMap.put("s_other_nonbusiness_expense", s_other_nonbusiness_expense[i]); 
				tempMap.put("s_income_tax_expense", s_income_tax_expense[i]);  
				tempMap.put("ca_other_Funds_should", ca_other_Funds_should[i]);  
				tempMap.put("credit_id", context.getContextMap().get("credit_id"));
				tempMap.put("pjccc_id", pjccc_id);
				sqlMapper.insert("creditVoucher.createVoucherReport",tempMap);
			}*/
			
			
			
			
			// 过往记录项目
			String[] PROJECT_NAME2 = context.getRequest().getParameterValues(
					"PROJECT_NAME2");
			if (PROJECT_NAME2 != null) {
				PROJECT_NAME2 = HTMLUtil.getParameterValues(context
						.getRequest(), "PROJECT_NAME2", "");
				String[] PROJECT_CONTENT2 = HTMLUtil.getParameterValues(context
						.getRequest(), "PROJECT_CONTENT2", "");
				for (int i = 0; i < PROJECT_NAME2.length; i++) {
					if (!PROJECT_NAME2[i].equals("")) {
						Map map2 = new HashMap();
						map2.put("PROJECT_NAME", PROJECT_NAME2[i]);
						map2.put("PROJECT_CONTENT", PROJECT_CONTENT2[i]);
						map2.put("STATE", 2);
						map2.put("pjccc_id", pjccc_id);
						sqlMapper
								.insert(
										"creditVoucher.createCreditPriorProjects",
										map2);
					}
				}
			}

			Map map3 = new HashMap();
			map3.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME3"));
			map3.put("PROJECT_CONTENT", context.contextMap
					.get("PROJECT_CONTENT3"));
			map3.put("STATE", 3);
			map3.put("pjccc_id", pjccc_id);
			sqlMapper.insert("creditVoucher.createCreditPriorProjects", map3);

			
			
			//添加法人担保资产
			
			String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
			if(HOUSE_NAME != null){
				//HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				
				for(int i = 0;i<HOUSE_NAME.length;i++){
					Map map4 = new HashMap();
					map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
					map4.put("VOUCH_ID", pjccc_id);
					map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
					map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
					map4.put("HOUSE_NAME", HOUSE_NAME[i]);
					map4.put("HOUSE_AREA", HOUSE_AREA[i]);
					map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
					map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
					map4.put("NOTES", NOTES[i]);
					map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
					map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
					sqlMapper.insert("creditVoucher.createProperty", map4);
				}
			}
			
			sqlMapper.commitTransaction();

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("添加法人为担保人错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

		//添加银行
		//查找最后一条Id即刚创建的Id
		Map outputMap = new HashMap();
//		List errList = context.errList;
		Map Piccc = null;
		String pj_id = null;
		if (errList.isEmpty()) {		
			try {			
				Piccc = (Map) DataAccessor.query("creditVoucher.selectEndPjccc_id", context.contextMap,DataAccessor.RS_TYPE.MAP);
				pj_id=Piccc.get("PJCCC_ID").toString();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) 
		{
			// 公司基本账户
			context.contextMap.put("BANK_NAME", context.contextMap
					.get("B_BANK_NAME"));
			context.contextMap.put("BANK_ACCOUNT", context.contextMap
					.get("B_BANK_ACCOUNT"));
			context.contextMap.put("STATE", "0");
			context.contextMap.put("pjccc_id", pj_id);
			 sqlMapper=DataAccessor.getSession();
			try {	
				
//			DataAccessor.execute("creditVoucher.deleteCreditCorpBankAccountByPjcccId", context.contextMap,DataAccessor.OPERATION_TYPE.DELETE);
//			DataAccessor.execute("creditVoucher.createCreditCorpBankAccount", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				
				sqlMapper.startTransaction();
				sqlMapper.delete("creditVoucher.deleteCreditCorpBankAccountByPjcccId", context.contextMap);
				sqlMapper.insert("creditVoucher.createCreditCorpBankAccount", context.contextMap);
			// 公司其他账户
			
			if (context.request.getParameterValues("BANK_NAME") != null) {
				String[] BANK_NAME = HTMLUtil.getParameterValues(
						context.request, "BANK_NAME", "");
				String[] BANK_ACCOUNT = HTMLUtil.getParameterValues(
						context.request, "BANK_ACCOUNT", "");
				for (int i = 0; i < BANK_NAME.length; i++) {
					context.contextMap.put("BANK_NAME", BANK_NAME[i]);
					context.contextMap.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
					context.contextMap.put("STATE", "1");
					context.contextMap.put("pjccc_id", pj_id);
					//DataAccessor.execute("creditVoucher.createCreditCorpBankAccount", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
					sqlMapper.insert("creditVoucher.createCreditCorpBankAccount", context.contextMap);
				}
			}
			sqlMapper.commitTransaction();
			} catch (Exception e) {
				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
			finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.debug(e);
				}
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		getCreditVouchers(context);
	}
	
	private void saveVoucherReportFile(Context context) throws Exception{
		System.out.println("============success============");
		List fileItems = (List) context.contextMap.get("uploadList");
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		//SqlMapClient sqlMapper = DataAccessor.getSession() ;
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			//InputStream in = fileItem.getInputStream();
			if (!fileItem.getName().equals("")) {
				//SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					//sqlMapClient.startTransaction();
					saveFileToDisk(context, fileItem);
					//sqlMapClient.commitTransaction();
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveFileToDisk(Context context, FileItem fileItem) throws Exception {
		String filePath = fileItem.getName();
		String new_file_name = (String) context.contextMap.get(fileItem.getFieldName() + "_name");
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = FileUpload.getUploadPath("cropReportImg");
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
			file_path = File.separator 
					+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())
					+ File.separator + type + File.separator + excelNewName
					+ "." + type;
			file_name = excelNewName + "." + type;
			try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					contextMap.put("file_path", file_path);
					contextMap.put("file_name", StringUtils.isEmpty(new_file_name) ? fileItem.getName() : new_file_name);
					contextMap.put("file_type", type);
					//contextMap.put("title", "暂收款水单附件");

					DataAccessor.execute("creditVoucher.insertVoucherReportFile", contextMap, OPERATION_TYPE.INSERT);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					fileItem.getInputStream().close();
				} catch (IOException e) {
					throw e;
				}
				fileItem.delete();
			}
		}
	}

	/**
	 * 添加自然人为担保人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void addCreditNatu(Context context) {
		int istogether = Integer.parseInt(context.contextMap.get("istogether")
				.toString());
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			
			String natu_idcard = (String) context.contextMap.get("natu_idcard");
			String idCard = (String) context.contextMap.get("otherPermit");
			if (natu_idcard == null || natu_idcard.equals("")) {
				context.contextMap.put("natu_idcard", idCard);
			}
			String natu_mate_idcard = (String) context.contextMap
					.get("natu_mate_idcard");
			String mate_other_permit = (String) context.contextMap
					.get("mate_other_permit");
			if (natu_mate_idcard == null || natu_mate_idcard.equals("")) {
				context.contextMap.put("natu_mate_idcard", mate_other_permit);
			}
			long pron_id = Long.parseLong(sqlMapper.insert("creditVoucher.createNatu", context.contextMap).toString());
//			long pron_id = Long.parseLong(DataAccessor.execute(
//					"creditVoucher.createNatu", context.contextMap,
//					DataAccessor.OPERATION_TYPE.INSERT).toString());
			// 是联合担保
			if (istogether == 1) {
				context.contextMap.put("mate_id", pron_id);
				sqlMapper.insert("creditVoucher.createNatuMate",context.contextMap);
//				DataAccessor.execute("creditVoucher.createNatuMate",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//添加法人担保资产
			
			String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
			if(HOUSE_NAME != null){
				//HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				String[] PROPERTY_OWNER = HTMLUtil.getParameterValues(context.request, "PROPERTY_OWNER", "");
				
				for(int i = 0;i<HOUSE_NAME.length;i++){
					Map map4 = new HashMap();
					map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
					map4.put("VOUCH_ID", pron_id);
					map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
					map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
					map4.put("HOUSE_NAME", HOUSE_NAME[i]);
					map4.put("HOUSE_AREA", HOUSE_AREA[i]);
					map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
					map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
					map4.put("NOTES", NOTES[i]);
					map4.put("PROPERTY_OWNER", PROPERTY_OWNER[i]);
					map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
					map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
					sqlMapper.insert("creditVoucher.createProperty", map4);
//					DataAccessor.execute("creditVoucher.createProperty", map4, DataAccessor.OPERATION_TYPE.INSERT);
				}
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("添加自然人为担保人错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			getCreditVouchers(context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 删除法人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteCorp(Context context) {
		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try {
			context.contextMap.put("VOUCH_TYPE", 1);
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

			sqlMapper.delete(
					"creditVoucher.deleteCreditCorpBankAccountByPjcccId",
					context.contextMap);
			sqlMapper.delete(
					"creditVoucher.deleteCreditCorpShareholderByPjcccId",
					context.contextMap);
			//删除担保人财务信息  baiman+
			sqlMapper.delete("creditVoucher.deleteVoucherReport", context.contextMap);
			sqlMapper.delete("creditVoucher.deleteCreditCorpProjectByPjcccId",
					context.contextMap);
			//删除担保人原来的财务信息  baiman-
			sqlMapper.delete(
					"creditVoucher.deleteCreditFinanestatementByPjcccId",
					context.contextMap);

			sqlMapper.delete(
					"creditVoucher.deleteCreditPriorProjectsByPjcccId",
					context.contextMap);
			sqlMapper.delete("creditVoucher.deleteCreditCustomerCorpByPjcccId",
					context.contextMap);
			sqlMapper.delete("creditVoucher.deleteProperty", context.contextMap);
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("删除法人错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(!errList.isEmpty()){
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/errList.jsp") ;
		}
	}
	
	/**
	 * 删除自然人资产
	 * 
	 * @param context
	 */
	public void deleteCropProperty(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try {
			DataAccessor.execute("creditVoucher.deletePropertyById",
					context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("删除自然人资产错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, 
					"defaultDispatcher?__action=creditVoucherVip.getCreditVouchers&credit_id="
					+context.contextMap.get("credit_id")+"&showFlag=4");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "error.jsp") ;
		}
	}

	/**
	 * 删除自然人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteNatu(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		SqlMapClient sqlMapper = null;
		try {
			context.contextMap.put("VOUCH_TYPE", 0);
			context.contextMap.put("pjccc_id", context.contextMap.get("pron_id"));
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			
			sqlMapper.delete("creditVoucher.deleteNatu",context.contextMap);
			sqlMapper.delete("creditVoucher.deleteProperty", context.contextMap);
			
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("删除自然人错误!请联系管理员") ;
		}  finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(!errList.isEmpty()){
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 删除自然人资产
	 * 
	 * @param context
	 */
	public void deleteNatuProperty(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		try {
			DataAccessor.execute("creditVoucher.deletePropertyById",
					context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("删除自然人资产错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context, 
					"defaultDispatcher?__action=creditVoucherVip.getCreditVouchers&credit_id="
					+context.contextMap.get("credit_id")+"&showFlag=4");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 显示担保人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCreditVouchersShow(Context context) {
		Map outputMap = new HashMap();
		List corpList = new ArrayList();
		List corpList_t = null;
		List natuList = null;
		Map creditMap = null;
		List errList = context.errList ;
		List<Map> vouchReportLists=null;
		List corpTypeList = null;
		List creditFinaceStatement = null;
		
		List corpProperty = null;
		List natuProperty = null;
		Map memoMap = null;
		try {
			context.contextMap.put("dataType", "证件类型");
			outputMap.put("natuTypeList", (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST));
			
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			corpList_t = (List) DataAccessor.query(
					"creditVoucher.selectCorpByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			natuList = (List) DataAccessor.query(
					"creditVoucher.selectVouchNatu", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);
			
			corpProperty = (List) DataAccessor.query(
					"creditVoucher.getCorpProperty", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			natuProperty = (List) DataAccessor.query(
					"creditVoucher.getNatuProperty", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpProperty", corpProperty);
			outputMap.put("natuProperty", natuProperty);
			
			if (corpList_t != null && corpList_t.size() > 0) {
				Map<String, Object> map = null;
				for (Object object : corpList_t) {
					map = (Map<String, Object>) object;
					map.put("fileList", (List)DataAccessor.query("creditVoucher.getVoucherFile", map, RS_TYPE.LIST));
					corpList.add(map);
				}
				
			}
			
			
			List list = new ArrayList();
			for (int i = 0; i < corpList.size(); i++) {
				Map map = (Map) corpList.get(i);
				Map maps = new HashMap();

				// 开户银行
				List bankAccountList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpBankAccountByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("bankAccountList", bankAccountList);
				// 公司股东及份额
				List shareholderList = (List) DataAccessor.query(
						"creditVoucher.getCreditShareholderByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("shareholderList", shareholderList);
				// 法人公司项目
				List projectList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpProjectByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("projectList", projectList);
				// 财务信息 baiman-
//				creditFinaceStatement = (List) DataAccessor.query(
//						"creditVoucher.getCreditFinaceStatementByPjcccId", map,
//						DataAccessor.RS_TYPE.LIST);
//				Map v1 = null;
//				Map v2 = null;
//				Map v3 = null;
//				Map v4 = null;
//				Map v5 = null;
//				if (creditFinaceStatement.size() >= 1) {
//					v1 = (Map) creditFinaceStatement.get(0);
//					v2 = (Map) creditFinaceStatement.get(1);
//					v3 = (Map) creditFinaceStatement.get(2);
//					v4 = (Map) creditFinaceStatement.get(3);
//					v5 = (Map) creditFinaceStatement.get(4);
//				}
//				maps.put("v1", v1);
//				maps.put("v2", v2);
//				maps.put("v3", v3);
//				maps.put("v4", v4);
//				maps.put("v5", v5);

				
				// 过往记录项目
				List creditPriorProject = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpPriorProjectsByPjcccId",
						map, DataAccessor.RS_TYPE.LIST);
				maps.put("creditPriorProject", creditPriorProject);

				list.add(maps);
			}
			outputMap.put("list", list);
			
			//财务信息 baiman+
			if(corpList.size()>0){
				List objlist=new ArrayList();
				vouchReportLists=(List<Map>)DataAccessor.query("creditVoucher.getVoucherReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(vouchReportLists.size()>0){
					
					for(int i=1;i<=vouchReportLists.size();i++){
						vouchReportLists.get(i-1).put("objcount", i);
						objlist.add( vouchReportLists.get(i-1));
					}
					outputMap.put("objlist",objlist);
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("显示担保人信息错误!请联系管理员") ;
		}
		outputMap.put("creditMap", creditMap);
		outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		outputMap.put("corpList", corpList);
		outputMap.put("flag", "show");
		outputMap.put("natuList", natuList);
		outputMap.put("prcd_id", context.contextMap.get("credit_id"));
		if(errList.isEmpty()){
			if (DataUtil.intUtil(context.contextMap.get("commit_flag")) == 1) {
				outputMap.put("commit_flag", context.contextMap.get("commit_flag"));
				Output.jspOutput(outputMap, context,
						"/credit_vip/creditFrameCommit.jsp");
			} else {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrameShow.jsp");
			}
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 根据ID查询法人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCorpById(Context context) {
		Map corpMap = null;
		List errList = context.errList ;
		try {
			corpMap = (Map) DataAccessor.query("creditVoucher.selectCorp",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("取得法人信息错误!请联系管理员") ;
		} 

		Output.jsonOutput(corpMap, context);
	}

	/**
	 * 修改法人担保
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void upCreditCorp(Context context) {
		// try {
		// DataAccessor.execute("creditVoucher.updateCorp",
		// context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		SqlMapClient sqlMapper = null;
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			// 法人身份
			String LEGAL_ID_CARD = (String) context.contextMap
					.get("LEGAL_ID_CARD");
			String idCard = (String) context.contextMap.get("otherPermit");
			if (LEGAL_ID_CARD == null || LEGAL_ID_CARD.equals("")) {
				context.contextMap.put("LEGAL_ID_CARD", idCard);
			}
			// 联系人
			String LINK_ID_CARD = (String) context.contextMap
					.get("LINK_ID_CARD");
			String idCards = (String) context.contextMap.get("linkPermit");
			if (LINK_ID_CARD == null || LINK_ID_CARD.equals("")) {
				context.contextMap.put("LINK_ID_CARD", idCards);
			}
			// 担保人法人基本信息修改
			sqlMapper.update("creditVoucher.updateCreditCustomerCorpById",
					context.contextMap);

			// 公司基本账户修改
			Map baseBankAccount = new HashMap();
			sqlMapper.delete(
					"creditVoucher.deleteCreditCorpBankAccountByPjcccId",
					context.contextMap);
			baseBankAccount.put("BANK_NAME", context.contextMap
					.get("B_BANK_NAME"));
			baseBankAccount.put("BANK_ACCOUNT", context.contextMap
					.get("B_BANK_ACCOUNT"));
			baseBankAccount.put("STATE", "0");
			baseBankAccount.put("pjccc_id", context.contextMap.get("pjccc_id"));
			sqlMapper.insert("creditVoucher.createCreditCorpBankAccount",
					baseBankAccount);

			// 公司其他账户修改
			if (context.request.getParameterValues("BANK_NAME") != null) {
				String[] BANK_NAME = HTMLUtil.getParameterValues(
						context.request, "BANK_NAME", "");
				String[] BANK_ACCOUNT = HTMLUtil.getParameterValues(
						context.request, "BANK_ACCOUNT", "");
				for (int i = 0; i < BANK_NAME.length; i++) {
					Map bankAccount = new HashMap();
					bankAccount.put("BANK_NAME", BANK_NAME[i]);
					bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
					bankAccount.put("STATE", "1");
					bankAccount.put("pjccc_id", context.contextMap
							.get("pjccc_id"));
					sqlMapper.insert(
							"creditVoucher.createCreditCorpBankAccount",
							bankAccount);
				}
			}

			// 公司股东信息
			sqlMapper.delete(
					"creditVoucher.deleteCreditCorpShareholderByPjcccId",
					context.contextMap);
			String[] str1 = context.getRequest().getParameterValues(
					"HOLDER_NAME");
			if (str1 != null) {
				// if (str1.equals("")) {
				String[] HOLDER_NAME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_NAME", "");
				String[] HOLDER_CAPITAL = HTMLUtil.getParameterValues(
						context.request, "HOLDER_CAPITAL", "");
				String[] HOLDER_WAY = HTMLUtil.getParameterValues(
						context.request, "HOLDER_WAY", "");
				String[] HOLDER_RATE = HTMLUtil.getParameterValues(
						context.request, "HOLDER_RATE", "");
				String[] HOLDER_MOME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_MOME", "");
				for (int i = 0; i < HOLDER_NAME.length; i++) {
					Map shareholder = new HashMap();
					shareholder.put("HOLDER_NAME", HOLDER_NAME[i]);

					shareholder.put("HOLDER_CAPITAL", HOLDER_CAPITAL[i]
							.equals("") ? 0 : HOLDER_CAPITAL[i]);
					shareholder.put("HOLDER_WAY", HOLDER_WAY[i]);
					shareholder.put("HOLDER_RATE",
							HOLDER_RATE[i].equals("") ? 0 : HOLDER_RATE[i]);
					shareholder.put("HOLDER_MOME", HOLDER_MOME[i]);
					shareholder.put("pjccc_id", context.contextMap
							.get("pjccc_id"));
					sqlMapper.insert(
							"creditVoucher.createCreditCorpShareholder",
							shareholder);
				}
			}

			// 公司项目信息
			sqlMapper.delete("creditVoucher.deleteCreditCorpProjectByPjcccId",
					context.contextMap);
			String[] str2 = context.request.getParameterValues("PROJECT_NAMES");
			if (str2 != null) {
				// if (str2.equals("")) {
				String[] PROJECT_NAME = HTMLUtil.getParameterValues(
						context.request, "PROJECT_NAMES", "");
				String[] PROJECT_DATE = HTMLUtil.getParameterValues(
						context.request, "PROJECT_DATE", "");
				String[] PROJECT_CONTENT = HTMLUtil.getParameterValues(
						context.request, "PROJECT_CONTENT", "");
				for (int i = 0; i < PROJECT_NAME.length; i++) {
					Map project = new HashMap();
					project.put("PROJECT_NAME", PROJECT_NAME[i]);
					project.put("PROJECT_DATE", PROJECT_DATE[i]);
					project.put("PROJECT_CONTENT", PROJECT_CONTENT[i]);
					project.put("pjccc_id", context.contextMap.get("pjccc_id"));
					sqlMapper.insert("creditVoucher.createCreditCorpProject",
							project);
				}
			}

			// 添加财务信息  baiman-
			saveVoucherReportFile(context);
//			sqlMapper.delete(
//					"creditVoucher.deleteCreditFinanestatementByPjcccId",
//					context.contextMap);
//			String[] str3 = context.request.getParameterValues("PROJECT_NAME");
//			if (str3 != null) {
//
//				String[] PROJECT_NAME = HTMLUtil.getParameterValues(context
//						.getRequest(), "PROJECT_NAME", "");
//				String[] MONEY_FUNDS = HTMLUtil.getParameterValues(context
//						.getRequest(), "MONEY_FUNDS", "");
//				String[] ACCOUNTS_RECEIVABLE = HTMLUtil.getParameterValues(
//						context.getRequest(), "ACCOUNTS_RECEIVABLE", "");
//				String[] STOCK = HTMLUtil.getParameterValues(context
//						.getRequest(), "STOCK", "");
//				String[] CAPITAL_ASSERTS = HTMLUtil.getParameterValues(context
//						.getRequest(), "CAPITAL_ASSERTS", "");
//				String[] TOTAL_ASSERTS = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_ASSERTS", "");
//				String[] SHORTTIME_LOAN = HTMLUtil.getParameterValues(context
//						.getRequest(), "SHORTTIME_LOAN", "");
//				String[] ACCOUNTS_PAYABLE = HTMLUtil.getParameterValues(context
//						.getRequest(), "ACCOUNTS_PAYABLE", "");
//				String[] TOTAL_OWES = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_OWES", "");
//				String[] CONTRIBUTED_CAPITAL = HTMLUtil.getParameterValues(
//						context.getRequest(), "CONTRIBUTED_CAPITAL", "");
//				String[] CAPITAL_RESERVE = HTMLUtil.getParameterValues(context
//						.getRequest(), "CAPITAL_RESERVE", "");
//				String[] UNDISTRIBUTED_PROFIT = HTMLUtil.getParameterValues(
//						context.getRequest(), "UNDISTRIBUTED_PROFIT", "");
//				String[] SALES_REVENUE = HTMLUtil.getParameterValues(context
//						.getRequest(), "SALES_REVENUE", "");
//				String[] COST_OF_MARKETING = HTMLUtil.getParameterValues(
//						context.getRequest(), "COST_OF_MARKETING", "");
//				String[] PERIOD_EXPENSE = HTMLUtil.getParameterValues(context
//						.getRequest(), "PERIOD_EXPENSE", "");
//				String[] TOTAL_PROFIT = HTMLUtil.getParameterValues(context
//						.getRequest(), "TOTAL_PROFIT", "");
//				String[] DEBTR = HTMLUtil.getParameterValues(context
//						.getRequest(), "DEBTR", "");
//				String[] PROFIT_MARGIN = HTMLUtil.getParameterValues(context
//						.getRequest(), "PROFIT_MARGIN", "");
//				String[] TTM = HTMLUtil.getParameterValues(
//						context.getRequest(), "TTM", "");
//				String[] SALES_GROWTH = HTMLUtil.getParameterValues(context
//						.getRequest(), "SALES_GROWTH", "");
//				String[] NAGR = HTMLUtil.getParameterValues(context
//						.getRequest(), "NAGR", "");
//				for (int i = 0; i < PROJECT_NAME.length; i++) {
//					Map tempMap = new HashMap();
//					tempMap.put("PROJECT_NAME", StringFilter(PROJECT_NAME[i]));
//					tempMap.put("MONEY_FUNDS", StringFilter(MONEY_FUNDS[i]));
//					tempMap.put("ACCOUNTS_RECEIVABLE",
//							StringFilter(ACCOUNTS_RECEIVABLE[i]));
//					tempMap.put("STOCK", StringFilter(STOCK[i]));
//					tempMap.put("CAPITAL_ASSERTS",
//							StringFilter(CAPITAL_ASSERTS[i]));
//					tempMap
//							.put("TOTAL_ASSERTS",
//									StringFilter(TOTAL_ASSERTS[i]));
//					tempMap.put("SHORTTIME_LOAN",
//							StringFilter(SHORTTIME_LOAN[i]));
//					tempMap.put("ACCOUNTS_PAYABLE",
//							StringFilter(ACCOUNTS_PAYABLE[i]));
//					tempMap.put("TOTAL_OWES", StringFilter(TOTAL_OWES[i]));
//					tempMap.put("CONTRIBUTED_CAPITAL",
//							StringFilter(CONTRIBUTED_CAPITAL[i]));
//					tempMap.put("CAPITAL_RESERVE",
//							StringFilter(CAPITAL_RESERVE[i]));
//					tempMap.put("UNDISTRIBUTED_PROFIT",
//							StringFilter(UNDISTRIBUTED_PROFIT[i]));
//
//					tempMap
//							.put("SALES_REVENUE",
//									StringFilter(SALES_REVENUE[i]));
//
//					tempMap.put("COST_OF_MARKETING",
//							StringFilter(COST_OF_MARKETING[i]));
//
//					tempMap.put("PERIOD_EXPENSE",
//							StringFilter(PERIOD_EXPENSE[i]));
//
//					tempMap.put("TOTAL_PROFIT", StringFilter(TOTAL_PROFIT[i]));
//					tempMap.put("DEBTR", StringFilter(DEBTR[i]));
//
//					tempMap
//							.put("PROFIT_MARGIN",
//									StringFilter(PROFIT_MARGIN[i]));
//
//					tempMap.put("TTM", StringFilter(TTM[i]));
//					tempMap.put("SALES_GROWTH", StringFilter(SALES_GROWTH[i]));
//					tempMap.put("NAGR", StringFilter(NAGR[i]));
//					tempMap.put("pjccc_id", context.contextMap.get("pjccc_id"));
//
//					tempMap.put("s_employeeId", context.contextMap
//							.get("s_employeeId"));
//
//					sqlMapper.insert(
//							"creditVoucher.createCreditFinaneStatement",
//							tempMap);
//				}
//			}
			// 添加财务信息  baiman+
			/*sqlMapper.delete("creditVoucher.deleteVoucherReport", context.contextMap);
			String[] project_item= HTMLUtil.getParameterValues(context.getRequest(), "project_itemV", "0");
			String [] ca_cash_price= HTMLUtil.getParameterValues(context.getRequest(), "ca_cash_priceV", "0");
			String [] ca_short_Invest= HTMLUtil.getParameterValues(context.getRequest(), "ca_short_InvestV", "0");
			String [] ca_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_bills_shouldV", "0");
			String [] ca_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_Funds_shouldV", "0");
			String [] ca_Goods_stock= HTMLUtil.getParameterValues(context.getRequest(), "ca_Goods_stockV", "0");
			String [] ca_other= HTMLUtil.getParameterValues(context.getRequest(), "ca_otherV", "0");
			String [] fa_land= HTMLUtil.getParameterValues(context.getRequest(), "fa_landV", "0");
			String [] fa_buildings= HTMLUtil.getParameterValues(context.getRequest(), "fa_buildingsV", "0");
			String [] fa_equipments= HTMLUtil.getParameterValues(context.getRequest(), "fa_equipmentsV", "0");
			String [] fa_rent_Assets= HTMLUtil.getParameterValues(context.getRequest(), "fa_rent_AssetsV", "0");
			String [] fa_transports= HTMLUtil.getParameterValues(context.getRequest(), "fa_transportsV", "0");
			String [] fa_other= HTMLUtil.getParameterValues(context.getRequest(), "fa_otherV", "0");
			String [] fa_Depreciations= HTMLUtil.getParameterValues(context.getRequest(), "fa_DepreciationsV", "0");
			String [] fa_Incompleted_projects= HTMLUtil.getParameterValues(context.getRequest(), "fa_Incompleted_projectsV", "0");
			String [] lang_Invest= HTMLUtil.getParameterValues(context.getRequest(), "lang_InvestV", "0");
			String [] other_Assets= HTMLUtil.getParameterValues(context.getRequest(), "other_AssetsV", "0");
			String [] sd_short_debt= HTMLUtil.getParameterValues(context.getRequest(), "sd_short_debtV", "0");
			String [] sd_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_bills_shouldV", "0");
			String [] sd_funds_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_funds_shouldV", "0");
			String [] sd_other_pay= HTMLUtil.getParameterValues(context.getRequest(), "sd_other_payV", "0");
			String [] sd_shareholders= HTMLUtil.getParameterValues(context.getRequest(), "sd_shareholdersV", "0");
			String [] sd_one_year= HTMLUtil.getParameterValues(context.getRequest(), "sd_one_yearV", "0");
			String [] sd_other= HTMLUtil.getParameterValues(context.getRequest(), "sd_otherV", "0");
			String [] lang_debt= HTMLUtil.getParameterValues(context.getRequest(), "lang_debtV", "0");
			String [] other_long_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_long_debtV", "0");
			String [] other_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_debtV", "0");
			String [] share_capital= HTMLUtil.getParameterValues(context.getRequest(), "share_capitalV", "0");
			String [] surplus_Capital= HTMLUtil.getParameterValues(context.getRequest(), "surplus_CapitalV", "0");
			String [] surplus_income= HTMLUtil.getParameterValues(context.getRequest(), "surplus_incomeV", "0");
			String [] this_losts= HTMLUtil.getParameterValues(context.getRequest(), "this_lostsV", "0");
			String [] project_changed= HTMLUtil.getParameterValues(context.getRequest(), "project_changedV", "0");
			String [] s_start_date= HTMLUtil.getParameterValues(context.getRequest(), "s_start_dateV", "0");
			String [] s_sale_net_income= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_net_incomeV", "0");
			String [] s_sale_cost= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_costV", "0");
			String [] s_other_gross_profit= HTMLUtil.getParameterValues(context.getRequest(), "s_other_gross_profitV", "0");
			String [] s_operating_expenses= HTMLUtil.getParameterValues(context.getRequest(), "s_operating_expensesV", "0");
			String [] s_nonbusiness_income= HTMLUtil.getParameterValues(context.getRequest(), "s_nonbusiness_incomeV", "0");
			String [] s_interest_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_interest_expenseV", "0");
			String [] s_other_nonbusiness_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_other_nonbusiness_expenseV", "0");
			String [] s_income_tax_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_income_tax_expenseV", "0");
			String [] ca_other_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_other_Funds_shouldV", "0");
			for(int i=0;i<project_item.length;i++){
				Map tempMap=new HashMap();
				tempMap.put("project_item", project_item[i]);
				tempMap.put("ca_cash_price", ca_cash_price[i]);
				tempMap.put("ca_short_Invest", ca_short_Invest[i]);
				tempMap.put("ca_bills_should", ca_bills_should[i]);
				tempMap.put("ca_Funds_should", ca_Funds_should[i]);
				tempMap.put("ca_Goods_stock", ca_Goods_stock[i]);
				tempMap.put("ca_other", ca_other[i]);
				tempMap.put("fa_land", fa_land[i]);
				tempMap.put("fa_buildings", fa_buildings[i]);
				tempMap.put("fa_equipments", fa_equipments[i]);
				tempMap.put("fa_rent_Assets", fa_rent_Assets[i]);
				tempMap.put("fa_transports", fa_transports[i]);
				tempMap.put("fa_other", fa_other[i]);
				tempMap.put("fa_Depreciations", fa_Depreciations[i]);
				tempMap.put("fa_Incompleted_projects", fa_Incompleted_projects[i]);
				tempMap.put("lang_Invest", lang_Invest[i]);
				tempMap.put("other_Assets", other_Assets[i]);
				tempMap.put("sd_short_debt", sd_short_debt[i]); 
				tempMap.put("sd_bills_should", sd_bills_should[i]); 
				tempMap.put("sd_funds_should", sd_funds_should[i]); 
				tempMap.put("sd_other_pay", sd_other_pay[i]); 
				tempMap.put("sd_shareholders", sd_shareholders[i]); 
				tempMap.put("sd_one_year", sd_one_year[i]); 
				tempMap.put("sd_other", sd_other[i]); 
				tempMap.put("lang_debt", lang_debt[i]); 
				tempMap.put("other_long_debt", other_long_debt[i]); 
				tempMap.put("other_debt", other_debt[i]); 
				tempMap.put("share_capital", share_capital[i]); 
				tempMap.put("surplus_Capital", surplus_Capital[i]); 
				tempMap.put("surplus_income", surplus_income[i]); 
				tempMap.put("this_losts", this_losts[i]); 
				tempMap.put("project_changed", project_changed[i]); 
				tempMap.put("s_start_date", s_start_date[i]); 
				tempMap.put("s_sale_net_income", s_sale_net_income[i]); 
				tempMap.put("s_sale_cost", s_sale_cost[i]); 
				tempMap.put("s_other_gross_profit", s_other_gross_profit[i]); 
				tempMap.put("s_operating_expenses", s_operating_expenses[i]); 
				tempMap.put("s_nonbusiness_income", s_nonbusiness_income[i]); 
				tempMap.put("s_interest_expense", s_interest_expense[i]); 
				tempMap.put("s_other_nonbusiness_expense", s_other_nonbusiness_expense[i]); 
				tempMap.put("s_income_tax_expense", s_income_tax_expense[i]);  
				tempMap.put("ca_other_Funds_should", ca_other_Funds_should[i]);  
				tempMap.put("credit_id", context.getContextMap().get("credit_id"));
				tempMap.put("pjccc_id", context.contextMap.get("pjccc_id"));
				sqlMapper.insert("creditVoucher.createVoucherReport",tempMap);
			}		*/
			
			
			// 过往记录项目
			sqlMapper.delete(
					"creditVoucher.deleteCreditPriorProjectsByPjcccId",
					context.contextMap);

			String[] PROJECT_NAME2 = context.getRequest().getParameterValues(
					"PROJECT_NAME2");
			if (PROJECT_NAME2 != null) {
				PROJECT_NAME2 = HTMLUtil.getParameterValues(context
						.getRequest(), "PROJECT_NAME2", "");
				String[] PROJECT_CONTENT2 = HTMLUtil.getParameterValues(context
						.getRequest(), "PROJECT_CONTENT2", "");
				for (int i = 0; i < PROJECT_NAME2.length; i++) {
					if (!PROJECT_NAME2[i].equals("")) {
						Map map2 = new HashMap();
						map2.put("PROJECT_NAME", PROJECT_NAME2[i]);
						map2.put("PROJECT_CONTENT", PROJECT_CONTENT2[i]);
						map2.put("STATE", 2);
						map2
								.put("pjccc_id", context.contextMap
										.get("pjccc_id"));
						sqlMapper
								.insert(
										"creditVoucher.createCreditPriorProjects",
										map2);
					}
				}
			}

			Map map3 = new HashMap();
			map3.put("PROJECT_NAME", context.contextMap.get("PROJECT_NAME3"));
			map3.put("PROJECT_CONTENT", context.contextMap
					.get("PROJECT_CONTENT3"));
			map3.put("STATE", 3);
			map3.put("pjccc_id", context.contextMap.get("pjccc_id"));
			sqlMapper.insert("creditVoucher.createCreditPriorProjects", map3);
			
			//添加或更新法人担保资产
			String[] ID = HTMLUtil.getParameterValues(context.request, "ID", "");
			if(ID != null){
				ID = HTMLUtil.getParameterValues(context.request, "ID", "");
				String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				
				for(int i = 0;i<HOUSE_NAME.length;i++){
					Map map4 = new HashMap();
					if(i < ID.length){
						map4.put("ID", ID[i]);
						map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
						map4.put("HOUSE_NAME", HOUSE_NAME[i]);
						map4.put("HOUSE_AREA", HOUSE_AREA[i]);
						map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
						map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
						map4.put("NOTES", NOTES[i]);
						sqlMapper.update("creditVoucher.updateCropProperty",map4);
					}else{
						map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map4.put("VOUCH_ID", context.contextMap.get("pjccc_id"));
						map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
						map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
						map4.put("HOUSE_NAME", HOUSE_NAME[i]);
						map4.put("HOUSE_AREA", HOUSE_AREA[i]);
						map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
						map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
						map4.put("NOTES", NOTES[i]);
						map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
						map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
						sqlMapper.insert("creditVoucher.createProperty", map4);
					}
				}
			}else{
				String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				
				if(HOUSE_NAME!=null){
					
					for(int i = 0;i<HOUSE_NAME.length;i++){
						Map map4 = new HashMap();
						map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map4.put("VOUCH_ID", context.contextMap.get("pjccc_id"));
						map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
						map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
						map4.put("HOUSE_NAME", HOUSE_NAME[i]);
						map4.put("HOUSE_AREA", HOUSE_AREA[i]);
						map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
						map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
						map4.put("NOTES", NOTES[i]);
						map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
						map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
						sqlMapper.insert("creditVoucher.createProperty", map4);
					}
				}
			}

			sqlMapper.commitTransaction();

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改法人担保错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(errList.isEmpty()){
			getCreditVouchers(context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 修改自然人担保
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void UpCreditNatu(Context context) {
		int istogether = Integer.parseInt(context.contextMap.get("istogether")
				.toString());
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String natu_idcard = (String) context.contextMap.get("natu_idcard");
			String idCard = (String) context.contextMap.get("otherPermit");
			if (natu_idcard == null || natu_idcard.equals("")) {
				context.contextMap.put("natu_idcard", idCard);
			}
			context.contextMap.put("mate_id", context.contextMap.get("pron_id"));
			sqlMapper.delete("creditVoucher.delNatuMateByMateId",context.contextMap);
//			DataAccessor.execute("creditVoucher.delNatuMateByMateId",
//					context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
			String natu_mate_idcard = (String) context.contextMap.get("natu_mate_idcard");
			String mate_other_permit = (String) context.contextMap.get("mate_other_permit");
			if (natu_mate_idcard == null || natu_mate_idcard.equals("")) {
				context.contextMap.put("natu_mate_idcard", mate_other_permit);
			}
			sqlMapper.update("creditVoucher.updateNatu",context.contextMap);
//			DataAccessor.execute("creditVoucher.updateNatu",
//					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);

			// 是联合担保
			if (istogether == 1) {

				sqlMapper.insert("creditVoucher.createNatuMate",context.contextMap);
//				DataAccessor.execute("creditVoucher.createNatuMate",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			
			//修改自然人担保资产
			String[] ID = HTMLUtil.getParameterValues(context.request, "ID", "");
			if(ID != null){
				ID = HTMLUtil.getParameterValues(context.request, "ID", "");
				
				String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				String[] PROPERTY_OWNER = HTMLUtil.getParameterValues(context.request, "PROPERTY_OWNER", "");
				
				for(int i = 0;i<HOUSE_NAME.length;i++){
					Map map4 = new HashMap();
					if(ID!=null){
						
						if(i < ID.length){
							map4.put("ID", ID[i]);
							map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
							map4.put("HOUSE_NAME", HOUSE_NAME[i]);
							map4.put("HOUSE_AREA", HOUSE_AREA[i]);
							map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
							map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
							map4.put("NOTES", NOTES[i]);
							map4.put("PROPERTY_OWNER", PROPERTY_OWNER[i]);
							sqlMapper.update("creditVoucher.updateNatuProperty",map4);
//							DataAccessor.execute("creditVoucher.updateNatuProperty",
//									map4, DataAccessor.OPERATION_TYPE.UPDATE);
						}else{
							map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
							map4.put("VOUCH_ID", context.contextMap.get("pron_id"));
							map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
							map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
							map4.put("HOUSE_NAME", HOUSE_NAME[i]);
							map4.put("HOUSE_AREA", HOUSE_AREA[i]);
							map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
							map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
							map4.put("NOTES", NOTES[i]);
							map4.put("PROPERTY_OWNER", PROPERTY_OWNER[i]);
							map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
							map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
							sqlMapper.insert("creditVoucher.createProperty", map4);
//							DataAccessor.execute("creditVoucher.createProperty", map4, DataAccessor.OPERATION_TYPE.INSERT);
						}
					}
				}
			}else{
				
				String[] HOUSE_NAME = HTMLUtil.getParameterValues(context.request, "HOUSE_NAME", "");
				String[] HOUSE_ADDRESS = HTMLUtil.getParameterValues(context.request, "HOUSE_ADDRESS", "");
				String[] HOUSE_AREA = HTMLUtil.getParameterValues(context.request, "HOUSE_AREA", "");
				String[] HOUSE_PROVE = HTMLUtil.getParameterValues(context.request, "HOUSE_PROVE", "");
				String[] HOUSE_OTHERRIGHT = HTMLUtil.getParameterValues(context.request, "HOUSE_OTHERRIGHT", "");
				String[] NOTES = HTMLUtil.getParameterValues(context.request, "NOTES", "");
				
				if(HOUSE_NAME!=null){
					for(int i = 0;i<HOUSE_NAME.length;i++){
						Map map4 = new HashMap();
	
						map4.put("CREDIT_ID", context.contextMap.get("credit_id"));
						map4.put("VOUCH_ID", context.contextMap.get("pron_id"));
						map4.put("VOUCH_TYPE", context.contextMap.get("VOUCH_TYPE"));
						map4.put("HOUSE_ADDRESS", HOUSE_ADDRESS[i]);
						map4.put("HOUSE_NAME", HOUSE_NAME[i]);
						map4.put("HOUSE_AREA", HOUSE_AREA[i]);
						map4.put("HOUSE_PROVE", HOUSE_PROVE[i]);
						map4.put("HOUSE_OTHERRIGHT", HOUSE_OTHERRIGHT[i]);
						map4.put("NOTES", NOTES[i]);
						map4.put("CREATE_ID", context.contextMap.get("CREATE_ID"));
						map4.put("MODIFY_ID", context.contextMap.get("MODIFY_ID"));
						sqlMapper.insert("creditVoucher.createProperty", map4);
//						DataAccessor.execute("creditVoucher.createProperty", map4, DataAccessor.OPERATION_TYPE.INSERT);
	
					}
				}
				
			}
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("修改自然人担保错误!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.debug(e);
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditPaylistServiceVip.showCreditPaylist&credit_id="
							+ context.contextMap.get("credit_id")
							+ "&showFlag=4&word=up");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "error.jsp") ;
		}
	}

	/**
	 * 打开担保人（法人）添加页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void gotoAddVoucherCorp(Context context) {
		Map outputMap = new HashMap();
		List corpTypeList = null;
		List natuTypeList = null;
		List errList = context.errList ;
		try {
			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);
			context.contextMap.put("dataType", "证件类型");
			natuTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("natuTypeList", natuTypeList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人添加页初始化错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context,
						"/credit_vip/creditVoucherCorps.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}

	// 过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 打开担保人（自然人）添加页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void gotoAddVoucherNatu(Context context) {
		Map outputMap = new HashMap();
		List natuTypeList = null;
		List errList = context.errList ;
		try {
			context.contextMap.put("dataType", "证件类型");
			natuTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("natuTypeList", natuTypeList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人添加页初始化错误!请联系管理员") ;
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context,
						"/credit_vip/creditVoucherNatu.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "error.jsp") ;
			}
		}
	}

	/**
	 * 反写数据自然人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void backWriteNatu(Context context) {
		Map outputMap = new HashMap();
		Map natuMap = null;
		List errList = context.errList ;
		try {
			natuMap = (Map) DataAccessor.query("creditVoucher.getNatuValues",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("反写自然人错误!请联系管理员") ;
		}
		outputMap.put("natuMap", natuMap);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 反写数据自然人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void backWriteCorp(Context context) {
		Map outputMap = new HashMap();
		Map corpMap = null;
		List errList = context.errList ;
		try {
			corpMap = (Map) DataAccessor.query("creditVoucher.getCorpValues",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(corpMap!=null&&corpMap.get("INCORPORATING_DATE")!=null){
				corpMap.put("INCORPORATING_DATE", corpMap.get("INCORPORATING_DATE").toString().substring(0,10));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("反写自然人错误!请联系管理员") ;
		}
		outputMap.put("corpMap", corpMap);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 担保人管理页面查询语句
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllVouch(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List errList = context.errList ;
		try {
			dw = (DataWrap) DataAccessor.query("creditVoucher.getAllVouch",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人管理页查询错误!请联系管理员") ;
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("type", context.contextMap.get("type"));
		Output.jspOutput(outputMap, context, "/credit_vip/creditAllVouch.jsp");
	}

	/**
	 * 担保人管理页面显示法人担保人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllVouchCorpShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List corpList = null;
		Map creditMap = null;

		List corpTypeList = null;
		List creditFinaceStatement = null;
		Map memoMap = null;
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			corpList = (List) DataAccessor.query(
					"creditVoucher.getAllVouchCorpShow", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);

			List list = new ArrayList();
			for (int i = 0; i < corpList.size(); i++) {
				Map map = (Map) corpList.get(i);
				Map maps = new HashMap();

				// 开户银行
				List bankAccountList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpBankAccountByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("bankAccountList", bankAccountList);
				// 公司股东及份额
				List shareholderList = (List) DataAccessor.query(
						"creditVoucher.getCreditShareholderByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("shareholderList", shareholderList);
				// 法人公司项目
				List projectList = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpProjectByPjcccId", map,
						DataAccessor.RS_TYPE.LIST);
				maps.put("projectList", projectList);
//				// 过往记录
//				creditFinaceStatement = (List) DataAccessor.query(
//						"creditVoucher.getCreditFinaceStatementByPjcccId", map,
//						DataAccessor.RS_TYPE.LIST);
//				Map v1 = null;
//				Map v2 = null;
//				Map v3 = null;
//				Map v4 = null;
//				Map v5 = null;
//				if (creditFinaceStatement.size() >= 1) {
//					v1 = (Map) creditFinaceStatement.get(0);
//					v2 = (Map) creditFinaceStatement.get(1);
//					v3 = (Map) creditFinaceStatement.get(2);
//					v4 = (Map) creditFinaceStatement.get(3);
//					v5 = (Map) creditFinaceStatement.get(4);
//				}
//				maps.put("v1", v1);
//				maps.put("v2", v2);
//				maps.put("v3", v3);
//				maps.put("v4", v4);
//				maps.put("v5", v5);

				// 过往记录项目
				List creditPriorProject = (List) DataAccessor.query(
						"creditVoucher.getCreditCorpPriorProjectsByPjcccId",
						map, DataAccessor.RS_TYPE.LIST);
				maps.put("creditPriorProject", creditPriorProject);

				list.add(maps);
			}
			outputMap.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人管理业显示法人担保人错误!请联系管理员") ;
		}
		outputMap.put("creditMap", creditMap);
		outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		outputMap.put("corpList", corpList);
		outputMap.put("flag", "show");
		outputMap.put("prcd_id", context.contextMap.get("credit_id"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,
					"/credit_vip/creditAllVouchCorpShow.jsp");
		}else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 担保人管理页面显示自然人担保人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllVouchNatuShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List natuList = null;
		Map creditMap = null;

		List corpTypeList = null;
		Map memoMap = null;
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			natuList = (List) DataAccessor.query(
					"creditVoucher.getAllVouchNatuShow", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人管理页显示法人担保人错误!请联系管理员") ;
		}
		outputMap.put("creditMap", creditMap);
		outputMap.put("examineFlag", context.contextMap.get("examineFlag"));
		outputMap.put("showFlag", context.contextMap.get("showFlag"));
		outputMap.put("flag", "show");
		outputMap.put("natuList", natuList);
		outputMap.put("prcd_id", context.contextMap.get("credit_id"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,
					"/credit_vip/creditAllVouchNatuShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	public void deleteVoucherReportFile(Context context){
		boolean flage = false;
		try {
			DataAccessor.execute("creditVoucher.deleteVoucherFile", context.contextMap, OPERATION_TYPE.UPDATE);
			flage = true;
			System.out.println("=========删除成功========");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	//公司沿革下的法人代表信息保存到担保自然人信息 add by ShenQi 2012-9-18
	public void saveNatural(Context context) {
		
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			param.put("credit_id",context.contextMap.get("credit_id"));//报告号
			param.put("cust_name",context.contextMap.get("LEGAL_PERSON"));//法人代表
			param.put("flagPermit",context.contextMap.get("LEGAL_IDCARD_FLAG"));//证件类型  0身份证号码, 1港澳台身份号, 2护照, 3其他
			param.put("natu_idcard",context.contextMap.get("LEGAL_ID_CARD"));//证件号
			param.put("natu_zip",context.contextMap.get("LEGAL_POSTCODE"));//邮编
			param.put("natu_mobile",context.contextMap.get("LEGAL_MOBILE_NUMBER1"));//手机号码
			param.put("natu_home_address",context.contextMap.get("LEGAL_HOME_ADDRESS"));//家庭地址
			param.put("natu_phone",context.contextMap.get("LEGAL_TELEPHONE"));//电话号码
			param.put("istogether",0);//是否联保 0是否  1是是
			
			param.put("natu_gender",context.contextMap.get("SEX"));
			param.put("natu_age",context.contextMap.get("AGE"));
			param.put("natu_fax",context.contextMap.get("FAX"));
			param.put("natu_work_units",context.contextMap.get("WORK_UNIT"));
			param.put("natu_mate_name",context.contextMap.get("MATE_NAME"));
			param.put("natu_mate_age",context.contextMap.get("MATE_AGES"));
			param.put("natu_mate_idcard",context.contextMap.get("MATE_ID_CARD"));
			param.put("natu_mate_phone",context.contextMap.get("MATE_PHONE"));
			param.put("natu_mate_work_units",context.contextMap.get("MATE_WORK_UNIT"));
			param.put("open_bank",context.contextMap.get("OPEN_BANK"));
			param.put("bank_accounts",context.contextMap.get("BANK_ACCOUNT"));
			
			DataAccessor.execute("creditVoucher.createNatu",param,OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			Output.jsonFlageOutput(false,context);
		}
		
		Output.jsonFlageOutput(true,context);
	}
}