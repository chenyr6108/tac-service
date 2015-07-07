package com.brick.credit.vip.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.util.DataUtil;
import com.brick.util.NumberUtils;

/**
 * 财务报表计算  导出时用
 * @author Administrator
 *
 */
public class CreditReport{
	static Log logger = LogFactory.getLog(CreditReport.class);

	public static void valueChangeTable1(Map obj) {
		sumZiChanZongE(obj);
		sumFuZaiZongE(obj);
		sumJingZhiZongE(obj);
	}

	private static void sumJingZhiZongE(Map obj) {
		double share_capital = DataUtil.doubleUtil(obj.get("SHARE_CAPITAL"));
		double surplus_Capital = DataUtil.doubleUtil(obj.get("SURPLUS_CAPITAL"));
		double surplus_income = DataUtil.doubleUtil(obj.get("SURPLUS_INCOME"));
		double this_losts = DataUtil.doubleUtil(obj.get("THIS_LOSTS"));
		double project_changed = DataUtil.doubleUtil(obj.get("PROJECT_CHANGED"));
		
		double real_sum = share_capital + surplus_Capital + surplus_income + 
		                  this_losts + project_changed;
		
		obj.put("REAL_SUM", real_sum);
	}

	private static void sumFuZaiZongE(Map obj) {
		double sd_short_debt = DataUtil.doubleUtil(obj.get("SD_SHORT_DEBT"));
		double sd_bills_should = DataUtil.doubleUtil(obj.get("SD_BILLS_SHOULD"));
		double sd_funds_should = DataUtil.doubleUtil(obj.get("SD_FUNDS_SHOULD"));
		double sd_other_pay = DataUtil.doubleUtil(obj.get("SD_OTHER_PAY"));
		double sd_shareholders = DataUtil.doubleUtil(obj.get("SD_SHAREHOLDERS"));
		double sd_one_year = DataUtil.doubleUtil(obj.get("SD_ONE_YEAR"));
		double sd_other = DataUtil.doubleUtil(obj.get("SD_OTHER"));
		
		double sd_sum = sd_short_debt + sd_bills_should + sd_funds_should + sd_other_pay
		                + sd_shareholders + sd_one_year + sd_other;
		
		obj.put("SD_SUM", sd_sum);
		
		double lang_debt = DataUtil.doubleUtil(obj.get("LANG_DEBT"));
		double other_long_debt = DataUtil.doubleUtil(obj.get("OTHER_LONG_DEBT"));
		
		double ld_sum = lang_debt + other_long_debt;
		obj.put("LD_SUM", ld_sum);
		
		double other_debt = DataUtil.doubleUtil(obj.get("OTHER_DEBT"));
		
		double debt_sum = sd_sum + ld_sum + other_debt;
		obj.put("DEBT_SUM", debt_sum);
	}

	private static void sumZiChanZongE(Map obj) {
		double ca_short_Invest = DataUtil.doubleUtil(obj.get("CA_SHORT_INVEST"));
		double ca_bills_should = DataUtil.doubleUtil(obj.get("CA_BILLS_SHOULD"));
		double ca_Funds_should = DataUtil.doubleUtil(obj.get("CA_FUNDS_SHOULD"));
		double ca_other_Funds_should = DataUtil.doubleUtil(obj.get("CA_OTHER_FUNDS_SHOULD"));
		double ca_Goods_stock = DataUtil.doubleUtil(obj.get("CA_GOODS_STOCK"));
		double ca_cash_price = DataUtil.doubleUtil(obj.get("CA_CASH_PRICE"));
		double ca_other = DataUtil.doubleUtil(obj.get("CA_OTHER"));
		
		double ca_sum = ca_short_Invest + ca_bills_should + ca_Funds_should + ca_other_Funds_should
		                + ca_Goods_stock + ca_cash_price + ca_other;
		obj.put("CA_SUM", ca_sum);
		
		double fa_land = DataUtil.doubleUtil(obj.get("FA_LAND"));
		double fa_buildings = DataUtil.doubleUtil(obj.get("FA_BUILDINGS"));
		double fa_equipments = DataUtil.doubleUtil(obj.get("FA_EQUIPMENTS"));
		double fa_rent_Assets = DataUtil.doubleUtil(obj.get("FA_RENT_ASSETS"));
		double fa_transports = DataUtil.doubleUtil(obj.get("FA_TRANSPORTS"));
		double fa_other = DataUtil.doubleUtil(obj.get("FA_OTHER"));
		double fa_Depreciations = DataUtil.doubleUtil(obj.get("FA_DEPRECIATIONS"));
		double fa_Incompleted_projects = DataUtil.doubleUtil(obj.get("FA_INCOMPLETED_PROJECTS"));
		
		double fa_sum = fa_land + fa_buildings + fa_equipments + fa_rent_Assets + fa_transports
		                + fa_other - fa_Depreciations + fa_Incompleted_projects;
		obj.put("FA_SUM", fa_sum);
				
		double lang_Invest = DataUtil.doubleUtil(obj.get("LANG_INVEST")); 
		double other_Assets = DataUtil.doubleUtil(obj.get("OTHER_ASSETS"));
		
		double capital_sum = ca_sum + fa_sum + lang_Invest + other_Assets;
		obj.put("CAPITAL_SUM", capital_sum);
	}

	
	public static void valueChangeTable2(Map obj) {
		sumYingYeLiYi(obj);
		sumShuiQianSunYi(obj);
		sumShuiHouSunYi(obj);		
	}

	private static void sumShuiHouSunYi(Map obj) {
		double s_before_sum = DataUtil.doubleUtil(obj.get("S_BEFORE_SUM"));
		double s_income_tax_expense = DataUtil.doubleUtil(obj.get("S_INCOME_TAX_EXPENSE"));
		
		double s_after_sum = s_before_sum - s_income_tax_expense;
		obj.put("S_AFTER_SUM", s_after_sum);
	}

	private static void sumShuiQianSunYi(Map obj) {
		double s_interest_expense = DataUtil.doubleUtil(obj.get("S_INTEREST_EXPENSE"));
		double s_other_nonbusiness_expense = DataUtil.doubleUtil(obj.get("S_OTHER_NONBUSINESS_EXPENSE"));
		
		double s_nbe_sum = s_interest_expense + s_other_nonbusiness_expense;
		obj.put("S_NBE_SUM", s_nbe_sum);
		
		double s_nonbusiness_income = DataUtil.doubleUtil(obj.get("S_NONBUSINESS_INCOME"));
		double s_bp_sum = DataUtil.doubleUtil(obj.get("S_BP_SUM"));
		
		double s_before_sum = s_nonbusiness_income + s_bp_sum - s_nbe_sum;
		obj.put("S_BEFORE_SUM", s_before_sum);
	}

	private static void sumYingYeLiYi(Map obj) {
		double s_sale_net_income = DataUtil.doubleUtil(obj.get("S_SALE_NET_INCOME"));
		double s_sale_cost = DataUtil.doubleUtil(obj.get("S_SALE_COST"));
		double s_other_gross_profit = DataUtil.doubleUtil(obj.get("S_OTHER_GROSS_PROFIT"));
		
		double s_sgp_sum = s_sale_net_income - s_sale_cost + s_other_gross_profit;
		obj.put("S_SGP_SUM", s_sgp_sum);
		
		double s_operating_expenses = DataUtil.doubleUtil(obj.get("S_OPERATING_EXPENSES"));
		
		double s_bp_sum = s_sgp_sum - s_operating_expenses;
		obj.put("S_BP_SUM", s_bp_sum);
	}

	public static void initAllData(Map<String, Map> exportMap) {
		Map obj1 = new HashMap();
		Map obj2 = new HashMap();
		Map obj3 = new HashMap();
		
		obj1 = exportMap.get("obj1");
		obj2 = exportMap.get("obj2");
		obj3 = exportMap.get("obj3");
				
		Map bili1 = bili(obj1);
		Map bili2 = bili(obj2);
		Map bili3 = bili(obj3);
		
		Map chayi12 = chayi(obj1,obj2);
		Map chayi23 = chayi(obj2,obj3);
						
		exportMap.put("bili1", bili1);
		exportMap.put("bili2", bili2);
		exportMap.put("bili3", bili3);	
		
		exportMap.put("chayi12", chayi12);
		exportMap.put("chayi23", chayi23);	
	}

	private static Map chayi(Map obj, Map obj2) {
		
		Map biliMap = new HashMap();
				
		double ca_short_Invest_bili = difference(DataUtil.doubleUtil(obj.get("CA_SHORT_INVEST")),DataUtil.doubleUtil(obj2.get("CA_SHORT_INVEST")));
		double ca_bills_should_bili = difference(DataUtil.doubleUtil(obj.get("CA_BILLS_SHOULD")),DataUtil.doubleUtil(obj2.get("CA_BILLS_SHOULD")));
		double ca_Funds_should_bili = difference(DataUtil.doubleUtil(obj.get("CA_FUNDS_SHOULD")),DataUtil.doubleUtil(obj2.get("CA_FUNDS_SHOULD")));
		double ca_other_Funds_should_bili = difference(DataUtil.doubleUtil(obj.get("CA_OTHER_FUNDS_SHOULD")),DataUtil.doubleUtil(obj2.get("CA_OTHER_FUNDS_SHOULD")));
		double ca_Goods_stock_bili = difference(DataUtil.doubleUtil(obj.get("CA_GOODS_STOCK")),DataUtil.doubleUtil(obj2.get("CA_GOODS_STOCK")));
		double ca_cash_price_bili = difference(DataUtil.doubleUtil(obj.get("CA_CASH_PRICE")),DataUtil.doubleUtil(obj2.get("CA_CASH_PRICE")));
		double ca_other_bili = difference(DataUtil.doubleUtil(obj.get("CA_OTHER")),DataUtil.doubleUtil(obj2.get("CA_OTHER")));
		
		biliMap.put("ca_short_invest_bili", ca_short_Invest_bili);
		biliMap.put("ca_bills_should_bili", ca_bills_should_bili);
		biliMap.put("ca_funds_should_bili", ca_Funds_should_bili);
		biliMap.put("ca_other_funds_should_bili", ca_other_Funds_should_bili);
		biliMap.put("ca_goods_stock_bili", ca_Goods_stock_bili);
		biliMap.put("ca_cash_price_bili", ca_cash_price_bili);
		biliMap.put("ca_other_bili", ca_other_bili);
		
		double ca_sum_bili = difference(DataUtil.doubleUtil(obj.get("CA_SUM")),DataUtil.doubleUtil(obj2.get("CA_SUM")));
		
		biliMap.put("ca_sum_bili", ca_sum_bili);
		
		double fa_land_bili = difference(DataUtil.doubleUtil(obj.get("FA_LAND")),DataUtil.doubleUtil(obj2.get("FA_LAND")));
		double fa_buildings_bili = difference(DataUtil.doubleUtil(obj.get("FA_BUILDINGS")),DataUtil.doubleUtil(obj2.get("FA_BUILDINGS")));
		double fa_equipments_bili = difference(DataUtil.doubleUtil(obj.get("FA_EQUIPMENTS")),DataUtil.doubleUtil(obj2.get("FA_EQUIPMENTS")));
		double fa_rent_Assets_bili = difference(DataUtil.doubleUtil(obj.get("FA_RENT_ASSETS")),DataUtil.doubleUtil(obj2.get("FA_RENT_ASSETS")));
		double fa_transports_bili = difference(DataUtil.doubleUtil(obj.get("FA_TRANSPORTS")),DataUtil.doubleUtil(obj2.get("FA_TRANSPORTS")));
		double fa_other_bili = difference(DataUtil.doubleUtil(obj.get("FA_OTHER")),DataUtil.doubleUtil(obj2.get("FA_OTHER")));
		double fa_Depreciations_bili = difference(DataUtil.doubleUtil(obj.get("FA_DEPRECIATIONS")),DataUtil.doubleUtil(obj2.get("FA_DEPRECIATIONS")));
		double fa_Incompleted_projects_bili = difference(DataUtil.doubleUtil(obj.get("FA_INCOMPLETED_PROJECTS")),DataUtil.doubleUtil(obj2.get("FA_INCOMPLETED_PROJECTS")));
		
		biliMap.put("fa_land_bili", fa_land_bili);
		biliMap.put("fa_buildings_bili", fa_buildings_bili);
		biliMap.put("fa_equipments_bili", fa_equipments_bili);
		biliMap.put("fa_rent_assets_bili", fa_rent_Assets_bili);
		biliMap.put("fa_transports_bili", fa_transports_bili);
		biliMap.put("fa_other_bili", fa_other_bili);
		biliMap.put("fa_depreciations_bili", fa_Depreciations_bili);
		biliMap.put("fa_incompleted_projects_bili", fa_Incompleted_projects_bili);
		
		double fa_sum_bili = difference(DataUtil.doubleUtil(obj.get("FA_SUM")),DataUtil.doubleUtil(obj2.get("FA_SUM")));
		
		biliMap.put("fa_sum_bili", fa_sum_bili);
		
		double lang_Invest_bili = difference(DataUtil.doubleUtil(obj.get("LANG_INVEST")),DataUtil.doubleUtil(obj2.get("LANG_INVEST"))); 
		double other_Assets_bili = difference(DataUtil.doubleUtil(obj.get("OTHER_ASSETS")),DataUtil.doubleUtil(obj2.get("OTHER_ASSETS")));
		
		biliMap.put("lang_invest_bili", lang_Invest_bili);
		biliMap.put("other_assets_bili", other_Assets_bili);
				
		double capital_sum_bili = difference(DataUtil.doubleUtil(obj.get("CAPITAL_SUM")),DataUtil.doubleUtil(obj2.get("CAPITAL_SUM")));
		biliMap.put("capital_sum_bili", capital_sum_bili);
		
		double sd_short_debt_bili = difference(DataUtil.doubleUtil(obj.get("SD_SHORT_DEBT")),DataUtil.doubleUtil(obj2.get("SD_SHORT_DEBT")));
		double sd_bills_should_bili = difference(DataUtil.doubleUtil(obj.get("SD_BILLS_SHOULD")),DataUtil.doubleUtil(obj2.get("SD_BILLS_SHOULD")));
		double sd_funds_should_bili = difference(DataUtil.doubleUtil(obj.get("SD_FUNDS_SHOULD")),DataUtil.doubleUtil(obj2.get("SD_FUNDS_SHOULD")));
		double sd_other_pay_bili = difference(DataUtil.doubleUtil(obj.get("SD_OTHER_PAY")),DataUtil.doubleUtil(obj2.get("SD_OTHER_PAY")));
		double sd_shareholders_bili = difference(DataUtil.doubleUtil(obj.get("SD_SHAREHOLDERS")),DataUtil.doubleUtil(obj2.get("SD_SHAREHOLDERS")));
		double sd_one_year_bili = difference(DataUtil.doubleUtil(obj.get("SD_ONE_YEAR")),DataUtil.doubleUtil(obj2.get("SD_ONE_YEAR")));
		double sd_other_bili = difference(DataUtil.doubleUtil(obj.get("SD_OTHER")),DataUtil.doubleUtil(obj2.get("SD_OTHER")));
		
		biliMap.put("sd_short_debt_bili", sd_short_debt_bili);
		biliMap.put("sd_bills_should_bili", sd_bills_should_bili);
		biliMap.put("sd_funds_should_bili", sd_funds_should_bili);
		biliMap.put("sd_other_pay_bili", sd_other_pay_bili);
		biliMap.put("sd_shareholders_bili", sd_shareholders_bili);
		biliMap.put("sd_one_year_bili", sd_one_year_bili);
		biliMap.put("sd_other_bili", sd_other_bili);
		
		double sd_sum_bili = difference(DataUtil.doubleUtil(obj.get("SD_SUM")),DataUtil.doubleUtil(obj2.get("SD_SUM")));
		
		biliMap.put("sd_sum_bili", sd_sum_bili);

		double lang_debt_bili = difference(DataUtil.doubleUtil(obj.get("LANG_DEBT")),DataUtil.doubleUtil(obj2.get("LANG_DEBT")));
		double other_long_debt_bili = difference(DataUtil.doubleUtil(obj.get("OTHER_LONG_DEBT")),DataUtil.doubleUtil(obj2.get("OTHER_LONG_DEBT")));
		
		biliMap.put("lang_debt_bili", lang_debt_bili);
		biliMap.put("other_long_debt_bili", other_long_debt_bili);
		
		double ld_sum_bili = difference(DataUtil.doubleUtil(obj.get("LD_SUM")),DataUtil.doubleUtil(obj2.get("LD_SUM")));
		
		biliMap.put("ld_sum_bili", ld_sum_bili);
		
		double other_debt_bili = difference(DataUtil.doubleUtil(obj.get("OTHER_DEBT")),DataUtil.doubleUtil(obj2.get("OTHER_DEBT")));
		
		biliMap.put("other_debt_bili", other_debt_bili);
		
		double debt_sum_bili = difference(DataUtil.doubleUtil(obj.get("DEBT_SUM")),DataUtil.doubleUtil(obj2.get("DEBT_SUM")));
		
		biliMap.put("debt_sum_bili", debt_sum_bili);
		
		double share_capital_bili = difference(DataUtil.doubleUtil(obj.get("SHARE_CAPITAL")),DataUtil.doubleUtil(obj2.get("SHARE_CAPITAL")));
		double surplus_Capital_bili = difference(DataUtil.doubleUtil(obj.get("SURPLUS_CAPITAL")),DataUtil.doubleUtil(obj2.get("SURPLUS_CAPITAL")));
		double surplus_income_bili = difference(DataUtil.doubleUtil(obj.get("SURPLUS_INCOME")),DataUtil.doubleUtil(obj2.get("SURPLUS_INCOME")));
		double this_losts_bili = difference(DataUtil.doubleUtil(obj.get("THIS_LOSTS")),DataUtil.doubleUtil(obj2.get("THIS_LOSTS")));
		double project_changed_bili = difference(DataUtil.doubleUtil(obj.get("PROJECT_CHANGED")),DataUtil.doubleUtil(obj2.get("PROJECT_CHANGED")));
		
		biliMap.put("share_capital_bili", share_capital_bili);
		biliMap.put("surplus_capital_bili", surplus_Capital_bili);
		biliMap.put("surplus_income_bili", surplus_income_bili);
		biliMap.put("this_losts_bili", this_losts_bili);
		biliMap.put("project_changed_bili", project_changed_bili);
		
		double real_sum_bili = difference(DataUtil.doubleUtil(obj.get("REAL_SUM")),DataUtil.doubleUtil(obj2.get("REAL_SUM")));
		
		biliMap.put("real_sum_bili", real_sum_bili);
		
		return biliMap;
	}

	private static double difference(double doublel, double double2) {
		double chayi = doublel - double2;
		return chayi;
	}

	private static Map bili(Map obj) {
		
		Map biliMap = new HashMap();
		
		double capital_sum = DataUtil.doubleUtil(obj.get("CAPITAL_SUM"));
		
		double capital_sum_bili = scale(DataUtil.doubleUtil(obj.get("CAPITAL_SUM")),capital_sum);
		biliMap.put("capital_sum_bili", capital_sum_bili);
		
		double ca_short_Invest_bili = scale(DataUtil.doubleUtil(obj.get("CA_SHORT_INVEST")),capital_sum);
		double ca_bills_should_bili = scale(DataUtil.doubleUtil(obj.get("CA_BILLS_SHOULD")),capital_sum);
		double ca_Funds_should_bili = scale(DataUtil.doubleUtil(obj.get("CA_FUNDS_SHOULD")),capital_sum);
		double ca_other_Funds_should_bili = scale(DataUtil.doubleUtil(obj.get("CA_OTHER_FUNDS_SHOULD")),capital_sum);
		double ca_Goods_stock_bili = scale(DataUtil.doubleUtil(obj.get("CA_GOODS_STOCK")),capital_sum);
		double ca_cash_price_bili = scale(DataUtil.doubleUtil(obj.get("CA_CASH_PRICE")),capital_sum);
		double ca_other_bili = scale(DataUtil.doubleUtil(obj.get("CA_OTHER")),capital_sum);
		
		biliMap.put("ca_short_invest_bili", ca_short_Invest_bili);
		biliMap.put("ca_bills_should_bili", ca_bills_should_bili);
		biliMap.put("ca_funds_should_bili", ca_Funds_should_bili);
		biliMap.put("ca_other_funds_should_bili", ca_other_Funds_should_bili);
		biliMap.put("ca_goods_stock_bili", ca_Goods_stock_bili);
		biliMap.put("ca_cash_price_bili", ca_cash_price_bili);
		biliMap.put("ca_other_bili", ca_other_bili);
		
		double ca_sum_bili = scale(DataUtil.doubleUtil(obj.get("CA_SUM")),capital_sum);
		
		biliMap.put("ca_sum_bili", ca_sum_bili);
		
		double fa_land_bili = scale(DataUtil.doubleUtil(obj.get("FA_LAND")),capital_sum);
		double fa_buildings_bili = scale(DataUtil.doubleUtil(obj.get("FA_BUILDINGS")),capital_sum);
		double fa_equipments_bili = scale(DataUtil.doubleUtil(obj.get("FA_EQUIPMENTS")),capital_sum);
		double fa_rent_Assets_bili = scale(DataUtil.doubleUtil(obj.get("FA_RENT_ASSETS")),capital_sum);
		double fa_transports_bili = scale(DataUtil.doubleUtil(obj.get("FA_TRANSPORTS")),capital_sum);
		double fa_other_bili = scale(DataUtil.doubleUtil(obj.get("FA_OTHER")),capital_sum);
		double fa_Depreciations_bili = scale(DataUtil.doubleUtil(obj.get("FA_DEPRECIATIONS")),capital_sum);
		double fa_Incompleted_projects_bili = scale(DataUtil.doubleUtil(obj.get("FA_INCOMPLETED_PROJECTS")),capital_sum);
		
		biliMap.put("fa_land_bili", fa_land_bili);
		biliMap.put("fa_buildings_bili", fa_buildings_bili);
		biliMap.put("fa_equipments_bili", fa_equipments_bili);
		biliMap.put("fa_rent_assets_bili", fa_rent_Assets_bili);
		biliMap.put("fa_transports_bili", fa_transports_bili);
		biliMap.put("fa_other_bili", fa_other_bili);
		biliMap.put("fa_depreciations_bili", fa_Depreciations_bili);
		biliMap.put("fa_incompleted_projects_bili", fa_Incompleted_projects_bili);
		
		double fa_sum_bili = scale(DataUtil.doubleUtil(obj.get("FA_SUM")),capital_sum);
		
		biliMap.put("fa_sum_bili", fa_sum_bili);
		
		double lang_Invest_bili = scale(DataUtil.doubleUtil(obj.get("LANG_INVEST")),capital_sum); 
		double other_Assets_bili = scale(DataUtil.doubleUtil(obj.get("OTHER_ASSETS")),capital_sum);
		
		biliMap.put("lang_invest_bili", lang_Invest_bili);
		biliMap.put("other_assets_bili", other_Assets_bili);
		
		//double capital_sum = DataUtil.doubleUtil(obj.get("CAPITAL_SUM"));
		double sd_short_debt_bili = scale(DataUtil.doubleUtil(obj.get("SD_SHORT_DEBT")),capital_sum);
		double sd_bills_should_bili = scale(DataUtil.doubleUtil(obj.get("SD_BILLS_SHOULD")),capital_sum);
		double sd_funds_should_bili = scale(DataUtil.doubleUtil(obj.get("SD_FUNDS_SHOULD")),capital_sum);
		double sd_other_pay_bili = scale(DataUtil.doubleUtil(obj.get("SD_OTHER_PAY")),capital_sum);
		double sd_shareholders_bili = scale(DataUtil.doubleUtil(obj.get("SD_SHAREHOLDERS")),capital_sum);
		double sd_one_year_bili = scale(DataUtil.doubleUtil(obj.get("SD_ONE_YEAR")),capital_sum);
		double sd_other_bili = scale(DataUtil.doubleUtil(obj.get("SD_OTHER")),capital_sum);
		
		biliMap.put("sd_short_debt_bili", sd_short_debt_bili);
		biliMap.put("sd_bills_should_bili", sd_bills_should_bili);
		biliMap.put("sd_funds_should_bili", sd_funds_should_bili);
		biliMap.put("sd_other_pay_bili", sd_other_pay_bili);
		biliMap.put("sd_shareholders_bili", sd_shareholders_bili);
		biliMap.put("sd_one_year_bili", sd_one_year_bili);
		biliMap.put("sd_other_bili", sd_other_bili);
		
		double sd_sum_bili = scale(DataUtil.doubleUtil(obj.get("SD_SUM")),capital_sum);
		
		biliMap.put("sd_sum_bili", sd_sum_bili);

		double lang_debt_bili = scale(DataUtil.doubleUtil(obj.get("LANG_DEBT")),capital_sum);
		double other_long_debt_bili = scale(DataUtil.doubleUtil(obj.get("OTHER_LONG_DEBT")),capital_sum);
		
		biliMap.put("lang_debt_bili", lang_debt_bili);
		biliMap.put("other_long_debt_bili", other_long_debt_bili);
		
		double ld_sum_bili = scale(DataUtil.doubleUtil(obj.get("LD_SUM")),capital_sum);
		
		biliMap.put("ld_sum_bili", ld_sum_bili);
		
		double other_debt_bili = scale(DataUtil.doubleUtil(obj.get("OTHER_DEBT")),capital_sum);
		
		biliMap.put("other_debt_bili", other_debt_bili);
		
		double debt_sum_bili = scale(DataUtil.doubleUtil(obj.get("DEBT_SUM")),capital_sum);
		
		biliMap.put("debt_sum_bili", debt_sum_bili);
		
		double share_capital_bili = scale(DataUtil.doubleUtil(obj.get("SHARE_CAPITAL")),capital_sum);
		double surplus_Capital_bili = scale(DataUtil.doubleUtil(obj.get("SURPLUS_CAPITAL")),capital_sum);
		double surplus_income_bili = scale(DataUtil.doubleUtil(obj.get("SURPLUS_INCOME")),capital_sum);
		double this_losts_bili = scale(DataUtil.doubleUtil(obj.get("THIS_LOSTS")),capital_sum);
		double project_changed_bili = scale(DataUtil.doubleUtil(obj.get("PROJECT_CHANGED")),capital_sum);
		
		biliMap.put("share_capital_bili", share_capital_bili);
		biliMap.put("surplus_capital_bili", surplus_Capital_bili);
		biliMap.put("surplus_income_bili", surplus_income_bili);
		biliMap.put("this_losts_bili", this_losts_bili);
		biliMap.put("project_changed_bili", project_changed_bili);
		
		double real_sum_bili = scale(DataUtil.doubleUtil(obj.get("REAL_SUM")),capital_sum);
		
		biliMap.put("real_sum_bili", real_sum_bili);
				
		return biliMap;
	}

	private static double scale(double num, double capital_sum) {		
		double bili = num * 10000 / capital_sum /100.0 ;
		if(!Double.isInfinite(bili)&&!Double.isNaN(bili)){
			bili = NumberUtils.retain3rounded(bili);
		}
		return bili;
	}

	public static void initTable2Data(Map<String, Map> exportMap) {
		Map obj1 = new HashMap();
		Map obj2 = new HashMap();
		Map obj3 = new HashMap();
		
		obj1 = exportMap.get("obj1");
		obj2 = exportMap.get("obj2");
		obj3 = exportMap.get("obj3");
		
		Map bili21 = bili2(obj1);
		Map bili22 = bili2(obj2);
		Map bili23 = bili2(obj3);
				
		Map czlv12 = new HashMap();
		Map czlv23 = new HashMap();
	
		String s_start_date1 = DataUtil.StringUtil(obj1.get("S_START_DATE"));
		String project_item1 = DataUtil.StringUtil(obj1.get("PROJECT_ITEM"));
		
		String s_start_date2 = DataUtil.StringUtil(obj2.get("S_START_DATE"));
		String project_item2 = DataUtil.StringUtil(obj2.get("PROJECT_ITEM"));
		
		long data1 = DateDiff(s_start_date1, project_item1);
		long data2 = DateDiff(s_start_date2, project_item2);
		
		//根据页面算法 都用的data1 
		czlv12 = CZ(obj1,obj2,data1);
		czlv23 = CZ(obj2,obj3,data1);
		
		exportMap.put("bili21", bili21);
		exportMap.put("bili22", bili22);
		exportMap.put("bili23", bili23);	
		
		exportMap.put("czlv12", czlv12);
		exportMap.put("czlv23", czlv23);	
		
		
	}

	private static Map CZ(Map obj1, Map obj2, long data1) {
		Map biliMap = new HashMap();
		
		double s_sale_net_income_bili = rate(DataUtil.doubleUtil(obj1.get("S_SALE_NET_INCOME")),DataUtil.doubleUtil(obj2.get("S_SALE_NET_INCOME")),data1);
		double s_sale_cost_bili = rate(DataUtil.doubleUtil(obj1.get("S_SALE_COST")),DataUtil.doubleUtil(obj2.get("S_SALE_COST")),data1);
		double s_other_gross_profit_bili = rate(DataUtil.doubleUtil(obj1.get("S_OTHER_GROSS_PROFIT")),DataUtil.doubleUtil(obj2.get("S_OTHER_GROSS_PROFIT")),data1);		
		double s_sgp_sum_bili = rate(DataUtil.doubleUtil(obj1.get("S_SGP_SUM")),DataUtil.doubleUtil(obj2.get("S_SGP_SUM")),data1);		
		double s_operating_expenses_bili = rate(DataUtil.doubleUtil(obj1.get("S_OPERATING_EXPENSES")),DataUtil.doubleUtil(obj2.get("S_OPERATING_EXPENSES")),data1);
		double s_bp_sum_bili = rate(DataUtil.doubleUtil(obj1.get("S_BP_SUM")),DataUtil.doubleUtil(obj2.get("S_BP_SUM")),data1);
		double s_interest_expense_bili = rate(DataUtil.doubleUtil(obj1.get("S_INTEREST_EXPENSE")),DataUtil.doubleUtil(obj2.get("S_INTEREST_EXPENSE")),data1);
		double s_other_nonbusiness_expense_bili = rate(DataUtil.doubleUtil(obj1.get("S_OTHER_NONBUSINESS_EXPENSE")),DataUtil.doubleUtil(obj2.get("S_OTHER_NONBUSINESS_EXPENSE")),data1);
		double s_nbe_sum_bili = rate(DataUtil.doubleUtil(obj1.get("S_NBE_SUM")),DataUtil.doubleUtil(obj2.get("S_NBE_SUM")),data1);
		double s_nonbusiness_income_bili = rate(DataUtil.doubleUtil(obj1.get("S_NONBUSINESS_INCOME")),DataUtil.doubleUtil(obj2.get("S_NONBUSINESS_INCOME")),data1);
		double s_before_sum_bili = rate(DataUtil.doubleUtil(obj1.get("S_BEFORE_SUM")),DataUtil.doubleUtil(obj2.get("S_BEFORE_SUM")),data1);
		double s_income_tax_expense_bili = rate(DataUtil.doubleUtil(obj1.get("S_INCOME_TAX_EXPENSE")),DataUtil.doubleUtil(obj2.get("S_INCOME_TAX_EXPENSE")),data1);
		double s_after_sum_bili = rate(DataUtil.doubleUtil(obj1.get("S_AFTER_SUM")),DataUtil.doubleUtil(obj2.get("S_AFTER_SUM")),data1);
		
		biliMap.put("s_sale_net_income_bili", s_sale_net_income_bili);
		biliMap.put("s_sale_cost_bili", s_sale_cost_bili);
		biliMap.put("s_other_gross_profit_bili", s_other_gross_profit_bili);
		biliMap.put("s_sgp_sum_bili", s_sgp_sum_bili);
		biliMap.put("s_operating_expenses_bili", s_operating_expenses_bili);
		biliMap.put("s_bp_sum_bili", s_bp_sum_bili);
		biliMap.put("s_interest_expense_bili", s_interest_expense_bili);
		biliMap.put("s_other_nonbusiness_expense_bili", s_other_nonbusiness_expense_bili);
		biliMap.put("s_nbe_sum_bili", s_nbe_sum_bili);
		biliMap.put("s_nonbusiness_income_bili", s_nonbusiness_income_bili);
		biliMap.put("s_before_sum_bili", s_before_sum_bili);
		biliMap.put("s_income_tax_expense_bili", s_income_tax_expense_bili);
		biliMap.put("s_after_sum_bili", s_after_sum_bili);
		
		return biliMap;
	}

	
	private static double rate(double num1, double num2, long data1) {
		
		double bili =0.0d;
		
		if(num2==0){
			bili = Double.NaN;
		}else{
			bili = (num1 * 365 / data1 - num1) / num2 * 10000 / 100.0;
			bili = NumberUtils.retain3rounded(bili);
		}		
		return bili;
	}

	private static Map bili2(Map obj) {

		Map biliMap = new HashMap();
		double s_sale_net_income = DataUtil.doubleUtil(obj.get("S_SALE_NET_INCOME"));
		
		double s_sale_cost_bili = scale(DataUtil.doubleUtil(obj.get("S_SALE_COST")),s_sale_net_income);
		double s_other_gross_profit_bili = scale(DataUtil.doubleUtil(obj.get("S_OTHER_GROSS_PROFIT")),s_sale_net_income);		
		double s_sgp_sum_bili = scale(DataUtil.doubleUtil(obj.get("S_SGP_SUM")),s_sale_net_income);		
		double s_operating_expenses_bili = scale(DataUtil.doubleUtil(obj.get("S_OPERATING_EXPENSES")),s_sale_net_income);
		double s_bp_sum_bili = scale(DataUtil.doubleUtil(obj.get("S_BP_SUM")),s_sale_net_income);
		double s_interest_expense_bili = scale(DataUtil.doubleUtil(obj.get("S_INTEREST_EXPENSE")),s_sale_net_income);
		double s_other_nonbusiness_expense_bili = scale(DataUtil.doubleUtil(obj.get("S_OTHER_NONBUSINESS_EXPENSE")),s_sale_net_income);
		double s_nbe_sum_bili = scale(DataUtil.doubleUtil(obj.get("S_NBE_SUM")),s_sale_net_income);
		double s_nonbusiness_income_bili = scale(DataUtil.doubleUtil(obj.get("S_NONBUSINESS_INCOME")),s_sale_net_income);
		double s_before_sum_bili = scale(DataUtil.doubleUtil(obj.get("S_BEFORE_SUM")),s_sale_net_income);
		double s_income_tax_expense_bili = scale(DataUtil.doubleUtil(obj.get("S_INCOME_TAX_EXPENSE")),s_sale_net_income);
		double s_after_sum_bili = scale(DataUtil.doubleUtil(obj.get("S_AFTER_SUM")),s_sale_net_income);
		
		biliMap.put("s_sale_cost_bili", s_sale_cost_bili);
		biliMap.put("s_other_gross_profit_bili", s_other_gross_profit_bili);
		biliMap.put("s_sgp_sum_bili", s_sgp_sum_bili);
		biliMap.put("s_operating_expenses_bili", s_operating_expenses_bili);
		biliMap.put("s_bp_sum_bili", s_bp_sum_bili);
		biliMap.put("s_interest_expense_bili", s_interest_expense_bili);
		biliMap.put("s_other_nonbusiness_expense_bili", s_other_nonbusiness_expense_bili);
		biliMap.put("s_nbe_sum_bili", s_nbe_sum_bili);
		biliMap.put("s_nonbusiness_income_bili", s_nonbusiness_income_bili);
		biliMap.put("s_before_sum_bili", s_before_sum_bili);
		biliMap.put("s_income_tax_expense_bili", s_income_tax_expense_bili);
		biliMap.put("s_after_sum_bili", s_after_sum_bili);
			
		return biliMap;
	}


	private static long DateDiff(String asStartDate, String asEndDate)  {				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = null ;
		Date endDate = null ;
		try {
			startDate = sdf.parse(asStartDate);
			endDate = sdf.parse(asEndDate);
		} catch (ParseException e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		long start = startDate.getTime();
		long end = endDate.getTime();
				
		long date = (end - start)/(1000*24*3600);		
		return date;
	}

	public static void ratio(Map<String, Map> exportMap) {
		
		Map obj1 = new HashMap();
		Map obj2 = new HashMap();
		Map obj3 = new HashMap();
		
		obj1 = exportMap.get("obj1");
		obj2 = exportMap.get("obj2");
		obj3 = exportMap.get("obj3");
		
		String s_start_date1 = DataUtil.StringUtil(obj1.get("S_START_DATE"));
		String project_item1 = DataUtil.StringUtil(obj1.get("PROJECT_ITEM"));
		
		String s_start_date2 = DataUtil.StringUtil(obj2.get("S_START_DATE"));
		String project_item2 = DataUtil.StringUtil(obj2.get("PROJECT_ITEM"));
		
		String s_start_date3 = DataUtil.StringUtil(obj3.get("S_START_DATE"));
		String project_item3 = DataUtil.StringUtil(obj3.get("PROJECT_ITEM"));
		
		long data1 = DateDiff(s_start_date1, project_item1);
		long data2 = DateDiff(s_start_date2, project_item2);
		long data3 = DateDiff(s_start_date3, project_item3);
		
		
		Map ratio1 = new HashMap();
		Map ratio2 = new HashMap();
		Map ratio3 = new HashMap();
		
		ratio1 = caiwubilv(obj1,obj2,data1);
		ratio2 = caiwubilv(obj2,obj3,data2);
		ratio3 = caiwubilv(obj3,null,data3);
		
		exportMap.put("ratio1", ratio1);
		exportMap.put("ratio2", ratio2);
		exportMap.put("ratio3", ratio3);
				
	}


	private static Map caiwubilv(Map obj1, Map obj2, long data) {		
		Map ratio = new HashMap();
		
		double fa_sum = DataUtil.doubleUtil(obj1.get("FA_SUM"));
		double lang_invest = DataUtil.doubleUtil(obj1.get("LANG_INVEST"));
		double real_sum = DataUtil.doubleUtil(obj1.get("REAL_SUM"));
		double ld_sum = DataUtil.doubleUtil(obj1.get("LD_SUM"));
		double gudingbilv = (fa_sum + lang_invest)/(real_sum + ld_sum)*10000/100.0 ;
		if(!Double.isInfinite(gudingbilv)&&!Double.isNaN(gudingbilv)){
			gudingbilv = NumberUtils.retain3rounded(gudingbilv);		
		}
		ratio.put("gudingbilv", gudingbilv);
		
		double debt_sum = DataUtil.doubleUtil(obj1.get("DEBT_SUM"));
		
		double fuzhaibilv = debt_sum / real_sum *10000/100.0;
		if(!Double.isInfinite(fuzhaibilv)&&!Double.isNaN(fuzhaibilv)){
			fuzhaibilv = NumberUtils.retain3rounded(fuzhaibilv);
		}
		ratio.put("fuzhaibilv", fuzhaibilv);
		
		double s_sale_net_income = DataUtil.doubleUtil(obj1.get("S_SALE_NET_INCOME"));
		double ca_funds_should1 = DataUtil.doubleUtil(obj1.get("CA_FUNDS_SHOULD"));
		double ca_bills_should1 = DataUtil.doubleUtil(obj1.get("CA_BILLS_SHOULD"));
		
		double ca_funds_should2 = 0d;
		double ca_bills_should2 = 0d;
		if(obj2!=null){
			ca_funds_should2 = DataUtil.doubleUtil(obj2.get("CA_FUNDS_SHOULD"));
			ca_bills_should2 = DataUtil.doubleUtil(obj2.get("CA_BILLS_SHOULD"));	
		}	
		//取得ca_other_Funds_should
		double ca_other_funds_should = DataUtil.doubleUtil(obj1.get("CA_OTHER_FUNDS_SHOULD")) ;
		
		
		double yingshouzhangkuanzhouzhuanlv = s_sale_net_income/(ca_funds_should1+ca_bills_should1+ca_funds_should2+ca_bills_should2)*2*365/data*1000/100.0;
		if(!Double.isInfinite(yingshouzhangkuanzhouzhuanlv)&&!Double.isNaN(yingshouzhangkuanzhouzhuanlv)){
			yingshouzhangkuanzhouzhuanlv = NumberUtils.retain3rounded(yingshouzhangkuanzhouzhuanlv);
		}		
		ratio.put("yingshouzhangkuanzhouzhuanlv", yingshouzhangkuanzhouzhuanlv);
		
		double yingshouzhangkuanzhouzhuanlv2 = 365/yingshouzhangkuanzhouzhuanlv;
		if(!Double.isInfinite(yingshouzhangkuanzhouzhuanlv2)&&!Double.isNaN(yingshouzhangkuanzhouzhuanlv2)){
			yingshouzhangkuanzhouzhuanlv2 = NumberUtils.retain3rounded(yingshouzhangkuanzhouzhuanlv2);
		}
		ratio.put("yingshouzhangkuanzhouzhuanlv2", yingshouzhangkuanzhouzhuanlv2);
		
		double s_sale_cost = DataUtil.doubleUtil(obj1.get("S_SALE_COST"));
		double ca_Goods_stock1 = DataUtil.doubleUtil(obj1.get("CA_GOODS_STOCK"));
		double ca_Goods_stock2 = 0d;
		if(obj2!=null){
			ca_Goods_stock2 = DataUtil.doubleUtil(obj2.get("CA_GOODS_STOCK"));
		}
		double cunhuozhouzhuanlv = s_sale_cost/(ca_Goods_stock1+ca_Goods_stock2)*2*365/data*1000/100.0;
		if(!Double.isInfinite(cunhuozhouzhuanlv)&&!Double.isNaN(cunhuozhouzhuanlv)){
			cunhuozhouzhuanlv = NumberUtils.retain3rounded(cunhuozhouzhuanlv);
		}
		ratio.put("cunhuozhouzhuanlv", cunhuozhouzhuanlv);
		
		double cunhuozhouzhuanlv2 = 365/cunhuozhouzhuanlv;
		if(!Double.isInfinite(cunhuozhouzhuanlv2)&&!Double.isNaN(cunhuozhouzhuanlv2)){
			cunhuozhouzhuanlv2 = NumberUtils.retain3rounded(cunhuozhouzhuanlv2);
		}
		ratio.put("cunhuozhouzhuanlv2", cunhuozhouzhuanlv2);
		
		double sd_bills_should1 = DataUtil.doubleUtil(obj1.get("SD_BILLS_SHOULD"));
		double sd_funds_should1 = DataUtil.doubleUtil(obj1.get("SD_FUNDS_SHOULD"));
		double sd_bills_should2 = 0d;
		double sd_funds_should2 = 0d;
		if(obj2!=null){
			sd_bills_should2 = DataUtil.doubleUtil(obj2.get("SD_BILLS_SHOULD"));
			sd_funds_should2 = DataUtil.doubleUtil(obj2.get("SD_FUNDS_SHOULD"));		
		}
		
		
		double yingfuzhangkuanzhouzhuanlv = s_sale_cost/(sd_bills_should1+sd_funds_should1+sd_bills_should2+sd_funds_should2)*2*365/data*1000/100.0;
		if(!Double.isInfinite(yingfuzhangkuanzhouzhuanlv)&&!Double.isNaN(yingfuzhangkuanzhouzhuanlv)){
			yingfuzhangkuanzhouzhuanlv = NumberUtils.retain3rounded(yingfuzhangkuanzhouzhuanlv);
		}	
		ratio.put("yingfuzhangkuanzhouzhuanlv", yingfuzhangkuanzhouzhuanlv);
		
		double yingfuzhangkuanzhouzhuanlv2 = 365/yingfuzhangkuanzhouzhuanlv;
		if(!Double.isInfinite(yingfuzhangkuanzhouzhuanlv2)&&!Double.isNaN(yingfuzhangkuanzhouzhuanlv2)){
			yingfuzhangkuanzhouzhuanlv2 = NumberUtils.retain3rounded(yingfuzhangkuanzhouzhuanlv2);
		}
		ratio.put("yingfuzhangkuanzhouzhuanlv2", yingfuzhangkuanzhouzhuanlv2);
		
		double capital_sum1 = DataUtil.doubleUtil(obj1.get("CAPITAL_SUM"));
		double capital_sum2 = 0d;
		if(obj2!=null){
			capital_sum2 = DataUtil.doubleUtil(obj2.get("CAPITAL_SUM"));
		}
		
		double zongzichanzhouzhuanlv = s_sale_net_income/(capital_sum1+capital_sum2)*2*365/data*1000/100.0;
		if(!Double.isInfinite(zongzichanzhouzhuanlv)&&!Double.isNaN(zongzichanzhouzhuanlv)){
			zongzichanzhouzhuanlv = NumberUtils.retain3rounded(zongzichanzhouzhuanlv);
		}
		ratio.put("zongzichanzhouzhuanlv", zongzichanzhouzhuanlv);
		
		double zongzichanzhouzhuanlv2 = 365/zongzichanzhouzhuanlv;
		
		if(!Double.isInfinite(zongzichanzhouzhuanlv2)&&!Double.isNaN(zongzichanzhouzhuanlv2)){			
			zongzichanzhouzhuanlv2 = NumberUtils.retain3rounded(zongzichanzhouzhuanlv2);
		}
		ratio.put("zongzichanzhouzhuanlv2", zongzichanzhouzhuanlv2);
		
		double ca_sum = DataUtil.doubleUtil(obj1.get("CA_SUM"));
		double sd_sum = DataUtil.doubleUtil(obj1.get("SD_SUM"));
		
		double liudongbilv = ca_sum/sd_sum*10000/100.0;
		if(!Double.isInfinite(liudongbilv)&&!Double.isNaN(liudongbilv)){
			liudongbilv = NumberUtils.retain3rounded(liudongbilv);
		}
		ratio.put("liudongbilv", liudongbilv);
		
		double ca_cash_price = DataUtil.doubleUtil(obj1.get("CA_CASH_PRICE"));
		double ca_short_Invest = DataUtil.doubleUtil(obj1.get("CA_SHORT_INVEST"));
		
		//修改速动比率计算方式 
		double sudongbilv = (ca_cash_price + ca_short_Invest + ca_bills_should1 + ca_funds_should1 + ca_other_funds_should)/debt_sum*10000/100.0;
		if(!Double.isInfinite(sudongbilv)&&!Double.isNaN(sudongbilv)){
			sudongbilv = NumberUtils.retain3rounded(sudongbilv);		
		}
		ratio.put("sudongbilv", sudongbilv);
//		double sudongbilv = (ca_cash_price + ca_short_Invest + ca_bills_should1 + ca_funds_should1)/sd_sum*10000/100.0;
//		if(!Double.isInfinite(sudongbilv)&&!Double.isNaN(sudongbilv)){
//			sudongbilv = NumberUtils.retain3rounded(sudongbilv);		
//		}
//		ratio.put("sudongbilv", sudongbilv);
		
		double s_interest_expense = DataUtil.doubleUtil(obj1.get("S_INTEREST_EXPENSE"));
		double s_before_sum = DataUtil.doubleUtil(obj1.get("S_BEFORE_SUM"));
		
		double d = s_interest_expense + s_before_sum;
		Double lixibaozhangbeishu ;
		
		if(d<0){
			//  N/A 情况
			lixibaozhangbeishu = null;
		}else{
			if(s_interest_expense!=0){
				lixibaozhangbeishu = 0d;
				lixibaozhangbeishu = d/s_interest_expense*10000/100.0;
				if(!Double.isInfinite(lixibaozhangbeishu)&&!Double.isNaN(lixibaozhangbeishu)){
					lixibaozhangbeishu = NumberUtils.retain3rounded(lixibaozhangbeishu);				
				}
			}else{
				lixibaozhangbeishu = Double.NaN;
			}
		}
		ratio.put("lixibaozhangbeishu", lixibaozhangbeishu);
		
		
		double changhuannengli = s_interest_expense / s_sale_net_income*10000/100.0;
		if(!Double.isInfinite(changhuannengli)&&!Double.isNaN(changhuannengli)){
			changhuannengli = NumberUtils.retain3rounded(changhuannengli);
		}
		ratio.put("changhuannengli", changhuannengli);
		
		double s_sgp_sum = DataUtil.doubleUtil(obj1.get("S_SGP_SUM"));
		double hl_maolilv =s_sgp_sum/s_sale_net_income*10000/100.0;
		if(!Double.isInfinite(hl_maolilv)&&!Double.isNaN(hl_maolilv)){
			hl_maolilv = NumberUtils.retain3rounded(hl_maolilv);
		}
		ratio.put("hl_maolilv", hl_maolilv);
		
		double s_bp_sum = DataUtil.doubleUtil(obj1.get("S_BP_SUM"));
		double hl_yingyeliyilv =s_bp_sum/s_sale_net_income*10000/100.0;
		if(!Double.isInfinite(hl_yingyeliyilv)&&!Double.isNaN(hl_yingyeliyilv)){
			hl_yingyeliyilv = NumberUtils.retain3rounded(hl_yingyeliyilv);
		}

		ratio.put("hl_yingyeliyilv", hl_yingyeliyilv);
		
		double s_after_sum = DataUtil.doubleUtil(obj1.get("S_AFTER_SUM"));
		double hl_chunyilv =s_after_sum/s_sale_net_income*10000/100.0;
		if(!Double.isInfinite(hl_chunyilv)&&!Double.isNaN(hl_chunyilv)){
			hl_chunyilv = NumberUtils.retain3rounded(hl_chunyilv);
		}
		
		ratio.put("hl_chunyilv", hl_chunyilv);
		
		double real_sum2 = 0d;
		if(obj2!=null){
			real_sum2 = DataUtil.doubleUtil(obj2.get("REAL_SUM"));
		}	
		double hl_jingzhihuolilv =s_after_sum/((real_sum + real_sum2)/2)*365/data*10000/100.0;
		
		if(!Double.isInfinite(hl_jingzhihuolilv)&&!Double.isNaN(hl_jingzhihuolilv)){
			hl_jingzhihuolilv = NumberUtils.retain3rounded(hl_jingzhihuolilv);
		}
		ratio.put("hl_jingzhihuolilv", hl_jingzhihuolilv);
		
		double zongzichanhuolilv = (s_after_sum + s_interest_expense)/((capital_sum1+capital_sum2)/2)*365/data*10000/100.0;
		if(!Double.isInfinite(zongzichanhuolilv)&&!Double.isNaN(zongzichanhuolilv)){
			zongzichanhuolilv = NumberUtils.retain3rounded(zongzichanhuolilv);
		}
		
		ratio.put("zongzichanhuolilv", zongzichanhuolilv);
		
		return ratio;
	}
	
	
}
