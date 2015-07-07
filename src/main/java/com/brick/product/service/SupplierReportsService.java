package com.brick.product.service;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;

import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;

public class SupplierReportsService extends BaseCommand {
	Log logger = LogFactory.getLog(SupplierReportsService.class);
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getSupplierReports(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		List supplierReportsLists = null;
		Map remark1Map = null;
		Map remark2Map = null;
		List suplBankAccount=null;
		String supl_id=null;
		Map suplGrantMoneyMap=null;
		Map totalPayMoneyMap=null;
		List supplLinkman=null;
		List supplLinkRecord=null;
		List creditList=null;
		if(errList.isEmpty()){
			try {
				supplierReportsLists = (List)DataAccessor.query("supplierReports.getSupplierReports", context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(supplierReportsLists.size()>0){
					outputMap.put("obj1", supplierReportsLists.get(0));
					outputMap.put("obj2", supplierReportsLists.get(1));
					outputMap.put("obj3", supplierReportsLists.get(2));
				}
				
				context.contextMap.put("type", 1);
				context.contextMap.put("suppl_id", context.contextMap.get("id"));
				remark1Map = (Map) DataAccessor.query(
						"supplierReports.selectSupplierReportsRemark", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				outputMap.put("remark1Map", remark1Map);
				
				context.contextMap.put("type", 0);
				remark2Map = (Map) DataAccessor.query(
						"supplierReports.selectSupplierReportsRemark", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				outputMap.put("remark2Map", remark2Map);
				
				//获得货币类型 add by ShenQi
				context.contextMap.put("dataType","货币类型");
				outputMap.put("moneyType",(List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
				
				outputMap.put("rs",(Map) DataAccessor.query("supplier.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP));
				suplBankAccount=(List) DataAccessor.query(
						"supplier.getSupplierBankAccountByCreditId", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("suplBankAccount", suplBankAccount);
				
				//查询 供应商图片信息
				outputMap.put("supplierImage",DataAccessor.query("SupplierImage.querySupplierImageBySupplierId", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
				
				outputMap.put("creditLine", baseService.getSuplCreditLine((String) context.contextMap.get("supplier_id")));
				
				/*//抓取供应商交机前授信额度
				suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
				outputMap.put("LIEN_GRANT_PRICE", suplGrantMoneyMap.get("LIEN_GRANT_PRICE"));
				outputMap.put("REPURCH_GRANT_PRICE", suplGrantMoneyMap.get("REPURCH_GRANT_PRICE"));
				outputMap.put("LIEN_LAST_PRICE",SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(context.contextMap.get("id").toString()))==null?0:SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(context.contextMap.get("id").toString())));
				outputMap.put("REPURCH_LAST_PRICE",SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(context.contextMap.get("id").toString()))==null?0:SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(context.contextMap.get("id").toString())));
				
				if (suplGrantMoneyMap!=null){
					outputMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));
					totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if (totalPayMoneyMap!=null){
						//判断授信的交机前拨款额度是否大于已用额度
						if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
							outputMap.put("advance_machine", 0);
						}else{
							outputMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
						}
					}
				}else{
					outputMap.put("advance_machine", 0);
					outputMap.put("advance_grant", 0);
				}*/
				
				List provinces=null;
				// 取省份
				provinces = (List) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("provinces", provinces);
				
				// 取所有市
				List citys=null;
				citys = (List) DataAccessor.query("area.getAllCitys", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				// 取所有地区
				List areas=null;
				areas = (List) DataAccessor.query("area.getAllAreas", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("areas", areas);
				
				supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
				supplLinkRecord=(List)DataAccessor.query("supplier.querySupplLinkRecord", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("supplLinkman", supplLinkman);
				outputMap.put("supplLinkRecord", supplLinkRecord);
				
				//该供应商所有报告
				creditList = (List) DataAccessor.query( "supplier.querySupplCredit", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("creditList", creditList);
				List<Map<String,Object>> intentList=null;
				List<Map<String,Object>> importantRecordList=null;
				//增加拜访记录的参数zhangbo
				Map<String,String> param1=new HashMap<String,String>();
				param1.put("dataType","拜访目的");
				intentList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary", param1, DataAccessor.RS_TYPE.LIST);
				param1.put("dataType","重点记录");
				param1.put("shortName","1");
				importantRecordList=(List<Map<String, Object>>)DataAccessor.query("dataDictionary.queryDataDictionary", param1, DataAccessor.RS_TYPE.LIST);
				outputMap.put("date", DateUtil.dateToString(Calendar.getInstance().getTime(), "yyyy-MM-dd"));
				outputMap.put("intentList", intentList);
				outputMap.put("importRecordList", importantRecordList);
				
				//该供应商所有操作日志
				context.contextMap.put("OPERATOR_TABLE_NAME", "T_SUPL_SUPPLIER");
				List logs = (List) DataAccessor.query( "supplier.queryOperationLogs", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("logs", logs);
				
			} catch (Exception e) {
				errList.add(e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()){
			outputMap.put("showFlag", 0);
			outputMap.put("supplier_id", context.contextMap.get("supplier_id"));
			Output.jspOutput(outputMap, context, "/product/supplier/allSupplierReports.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	private static SqlMapClient sqlMapper;
	
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void saveSupplierReports(Context context){
		sqlMapper=DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			sqlMapper.delete("supplierReports.deleteSupplierReport", context.contextMap);
			String[] project_item= HTMLUtil.getParameterValues(context.getRequest(), "project_item", "0");
			String [] ca_cash_price= HTMLUtil.getParameterValues(context.getRequest(), "ca_cash_price", "0");
			String [] ca_short_Invest= HTMLUtil.getParameterValues(context.getRequest(), "ca_short_Invest", "0");
			String [] ca_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_bills_should", "0");
			String [] ca_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_Funds_should", "0");
			String [] ca_Goods_stock= HTMLUtil.getParameterValues(context.getRequest(), "ca_Goods_stock", "0");
			String [] ca_other= HTMLUtil.getParameterValues(context.getRequest(), "ca_other", "0");
			String [] fa_land= HTMLUtil.getParameterValues(context.getRequest(), "fa_land", "0");
			String [] fa_buildings= HTMLUtil.getParameterValues(context.getRequest(), "fa_buildings", "0");
			String [] fa_equipments= HTMLUtil.getParameterValues(context.getRequest(), "fa_equipments", "0");
			String [] fa_rent_Assets= HTMLUtil.getParameterValues(context.getRequest(), "fa_rent_Assets", "0");
			String [] fa_transports= HTMLUtil.getParameterValues(context.getRequest(), "fa_transports", "0");
			String [] fa_other= HTMLUtil.getParameterValues(context.getRequest(), "fa_other", "0");
			String [] fa_Depreciations= HTMLUtil.getParameterValues(context.getRequest(), "fa_Depreciations", "0");
			String [] fa_Incompleted_projects= HTMLUtil.getParameterValues(context.getRequest(), "fa_Incompleted_projects", "0");
			String [] lang_Invest= HTMLUtil.getParameterValues(context.getRequest(), "lang_Invest", "0");
			String [] other_Assets= HTMLUtil.getParameterValues(context.getRequest(), "other_Assets", "0");
			String [] sd_short_debt= HTMLUtil.getParameterValues(context.getRequest(), "sd_short_debt", "0");
			String [] sd_bills_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_bills_should", "0");
			String [] sd_funds_should= HTMLUtil.getParameterValues(context.getRequest(), "sd_funds_should", "0");
			String [] sd_other_pay= HTMLUtil.getParameterValues(context.getRequest(), "sd_other_pay", "0");
			String [] sd_shareholders= HTMLUtil.getParameterValues(context.getRequest(), "sd_shareholders", "0");
			String [] sd_one_year= HTMLUtil.getParameterValues(context.getRequest(), "sd_one_year", "0");
			String [] sd_other= HTMLUtil.getParameterValues(context.getRequest(), "sd_other", "0");
			String [] lang_debt= HTMLUtil.getParameterValues(context.getRequest(), "lang_debt", "0");
			String [] other_long_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_long_debt", "0");
			String [] other_debt= HTMLUtil.getParameterValues(context.getRequest(), "other_debt", "0");
			String [] share_capital= HTMLUtil.getParameterValues(context.getRequest(), "share_capital", "0");
			String [] surplus_Capital= HTMLUtil.getParameterValues(context.getRequest(), "surplus_Capital", "0");
			String [] surplus_income= HTMLUtil.getParameterValues(context.getRequest(), "surplus_income", "0");
			String [] this_losts= HTMLUtil.getParameterValues(context.getRequest(), "this_losts", "0");
			String [] project_changed= HTMLUtil.getParameterValues(context.getRequest(), "project_changed", "0");
			String [] s_start_date= HTMLUtil.getParameterValues(context.getRequest(), "s_start_date", "0");
			String [] s_sale_net_income= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_net_income", "0");
			String [] s_sale_cost= HTMLUtil.getParameterValues(context.getRequest(), "s_sale_cost", "0");
			String [] s_other_gross_profit= HTMLUtil.getParameterValues(context.getRequest(), "s_other_gross_profit", "0");
			String [] s_operating_expenses= HTMLUtil.getParameterValues(context.getRequest(), "s_operating_expenses", "0");
			String [] s_nonbusiness_income= HTMLUtil.getParameterValues(context.getRequest(), "s_nonbusiness_income", "0");
			String [] s_interest_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_interest_expense", "0");
			String [] s_other_nonbusiness_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_other_nonbusiness_expense", "0");
			String [] s_income_tax_expense= HTMLUtil.getParameterValues(context.getRequest(), "s_income_tax_expense", "0");
			String [] ca_other_Funds_should= HTMLUtil.getParameterValues(context.getRequest(), "ca_other_Funds_should", "0");
			
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
				tempMap.put("supplier_id", context.getContextMap().get("supplier_id"));
				sqlMapper.insert("supplierReports.createSupplierReport",tempMap);
			}
			
			//添加备注  type=1为 负债表   type=0为 损益表
			context.contextMap.put("supplier_id", context.contextMap.get("supplier_id"));
			if(context.contextMap.get("fuzhai")!=null){
				context.contextMap.put("remark", context.contextMap.get("fuzhai"));
				context.contextMap.put("type",1);
				sqlMapper.insert("supplierReports.insertSupplierReportRemark", context.contextMap);
			}
			if(context.contextMap.get("sunyi")!=null){
				context.contextMap.put("remark", context.contextMap.get("sunyi"));
				context.contextMap.put("type",0);
				sqlMapper.insert("supplierReports.insertSupplierReportRemark", context.contextMap);			
			}
			sqlMapper.commitTransaction();		
		} catch (SQLException e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally {
			try {
				// 关闭事务
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			} 
		} 
		Output.jspSendRedirect(context,
			"defaultDispatcher?__action=supplier.findAllSupplier");
	}
}
