package com.brick.salesLeads.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.permission.util.CreateXmlUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class SalesLeadsManager extends AService {
	Log logger = LogFactory.getLog(SalesLeadsManager.class);

	/**
	 * 销售机会查询
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void querySalesLeads(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List chanceStageList = null;
		List sourcesList = null;
		try {
			dw = (DataWrap) DataAccessor.query("salesLeads.getSalesLeads",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			// chanceStageList = (List) DataAccessor.query(
			// "dataDictionary.getTypeDetail", context.contextMap,
			// DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "机会阶段");
			chanceStageList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "来源");
			sourcesList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("sourcesList", sourcesList);
		outputMap.put("dw", dw);
		outputMap.put("chanceStageList", chanceStageList);
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		outputMap.put("chance_stage_id", context.contextMap
				.get("chance_stage_id"));
		outputMap.put("content", context.contextMap.get("content"));

		Output.jspOutput(outputMap, context,
				"/salesLeads/salesLeadsManager.jsp");
	}

	/**
	 * 跳转到新增页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void jumpCreat(Context context) {
		Map outputMap = new HashMap();
		List chanceStageList = null;
		List sourcesList = null;
		List currencyList = null;
		// List custLevelList = null;
		// context.contextMap.put("custType", "承租人级别");
		context.contextMap.put("corp", "法人");
		context.contextMap.put("natu", "自然人");
		try {
			// context.contextMap.put("dataType", "承租人级别");
			// custLevelList = (List) DataAccessor.query(
			// "dataDictionary.queryDataDictionary", context.contextMap,
			// DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "机会阶段");
			chanceStageList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "来源");
			sourcesList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "货币类型");
			currencyList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		// outputMap.put("custLevelList", custLevelList);
		outputMap.put("chanceStageList", chanceStageList);
		outputMap.put("sourcesList", sourcesList);
		outputMap.put("currencyList", currencyList);

		Output.jspOutput(outputMap, context, "/salesLeads/salesLeadsCreat.jsp");
	}

	/**
	 * 跳转到修改页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void jumpModify(Context context) {
		Map outputMap = new HashMap();
		List chanceStageList = null;
		List sourcesList = null;
		List currencyList = null;
		Map salesMap = null;
		try {
			salesMap = (Map) DataAccessor.query("salesLeads.getSalesLeadsById",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("dataType", "机会阶段");
			chanceStageList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "来源");
			sourcesList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "货币类型");
			currencyList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("salesMap", salesMap);
		outputMap.put("chanceStageList", chanceStageList);
		outputMap.put("sourcesList", sourcesList);
		outputMap.put("currencyList", currencyList);

		Output
				.jspOutput(outputMap, context,
						"/salesLeads/salesLeadsModify.jsp");
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void jumpQuerySales(Context context) {
		Map salesMap = null;
		List chanceStageList = null;
		List sourcesList = null;
		List currencyList = null;
		try {
			salesMap = (Map) DataAccessor.query("salesLeads.getSalesLeadsById",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("dataType", "机会阶段");
			chanceStageList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "来源");
			sourcesList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dataType", "货币类型");
			currencyList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		salesMap.put("chanceStageList", chanceStageList);
		salesMap.put("sourcesList", sourcesList);
		salesMap.put("currencyList", currencyList);
		
		Output.jspOutput(salesMap, context, "/salesLeads/salesLeadsShow.jsp");
	}

	/**
	 * 新增
	 * 
	 * @param context
	 */
	public void addSalesLeads(Context context) {
		try {
			DataAccessor.execute("salesLeads.create", context.contextMap,
					DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		querySalesLeads(context);
	}

	/**
	 * 修改
	 * 
	 * @param context
	 */
	public void upSalesLeads(Context context) {
		try {
			DataAccessor.execute("salesLeads.updateSalesLeadsById",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		querySalesLeads(context);
	}

	/**
	 * 删除
	 * 
	 * @param context
	 */
	public void delSalesLeads(Context context) {
		try {
			DataAccessor.execute("salesLeads.delSalesLeadsById",
					context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		querySalesLeads(context);
	}

	/**
	 * 查询承租人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectCustomer(Context context) {
		Map outputMap = new HashMap();
		List custList = null;
		context.contextMap.put("corp", "法人");
		context.contextMap.put("natu", "自然人");
		context.contextMap.put("custType", "承租人级别");
		try {
			custList = (List) DataAccessor.query("salesLeads.getCustAll",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("custList", custList);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 查询联系人
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectLinkMan(Context context) {
		Map outputMap = new HashMap();
		List linkList = null;
		try {
			linkList = (List) DataAccessor.query("salesLeads.getLinkAll",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("linkList", linkList);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 销售漏斗查询
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void funnelSalesLeads(Context context) {
		Map outputMap = new HashMap();
		List salesList = null;
		context.contextMap.put("type", "机会阶段");
		try {
			salesList = (List) DataAccessor.query("salesLeads.getSalesFunnel",
					context.contextMap, DataAccessor.RS_TYPE.LIST);

			// 总金额
			double proportionCount = 0;
			// 总个数
			double countAll = 0;
			for (int i = 0; i < salesList.size(); i++) {
				Map where = (Map) salesList.get(i);
				double expect_money_sum = Double.valueOf(where.get(
						"EXPECT_MONEY_SUM").toString());
				double count = Double.valueOf(where.get("COUNT").toString());
				countAll += count;
				proportionCount += expect_money_sum;
			}
			List list = new ArrayList();

			for (int i = 0; i < salesList.size(); i++) {
				Map where = (Map) salesList.get(i);
				Map map = new HashMap();
				map.put("flag", where.get("FLAG"));
				if (!where.get("COUNT").equals(0)) {
					map.put("count", where.get("COUNT"));
					String countProp = getPassedYield(countAll, Double
							.valueOf(where.get("COUNT").toString()));
					map.put("countProp", countProp);
				} else {
					map.put("count", where.get("COUNT"));
					map.put("countProp", "0%");
				}

				if (!where.get("EXPECT_MONEY_SUM").equals(0)) {
					map.put("expect_money_sum", where.get("EXPECT_MONEY_SUM"));
					String moneyProp = getPassedYield(proportionCount, Double
							.valueOf(where.get("EXPECT_MONEY_SUM").toString()));
					map.put("moneyProp", moneyProp);
				} else {
					map.put("expect_money_sum", 0);
					map.put("moneyProp", "0%");
				}

				list.add(map);
			}

			String path = context.request.getRealPath("/")
					+ "/salesLeads/data/salesLeadsFunnel.xml";
			CreateXmlUtil.writeXml(CreateXmlUtil.creatSalesLeads(list), path);
			outputMap.put("list", list);
			outputMap.put("countAll", countAll);
			outputMap.put("proportionCount", proportionCount);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output
				.jspOutput(outputMap, context,
						"/salesLeads/salesLeadsFunnel.jsp");
	}

	/**
	 * 求百分比
	 * 
	 * @param tested
	 *            总数
	 * @param passed
	 *            求证的数
	 * @return 百分比
	 */
	public String getPassedYield(double tested, double passed) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		if (tested == 0) {
			return ("0% ");
		} else {
			return nf.format((double) passed / (double) tested);
		}
	}
}
