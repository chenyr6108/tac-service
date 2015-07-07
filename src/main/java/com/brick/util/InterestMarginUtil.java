package com.brick.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.collection.service.StartPayService;
import com.brick.service.core.DataAccessor;

public class InterestMarginUtil {
	public static String getInterestMargin(String credit_id) throws Exception{
		String result = null;
		Map schema = null;
		Map paylist = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("credit_id", credit_id);
		try {
			//查询方案
			schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",paramMap, DataAccessor.RS_TYPE.MAP);
			// 查询应付租金列表
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(credit_id, Integer.valueOf(1));
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
			// 
			if (schema != null) {
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
				List<Map<String, Object>> paylines = null;
				if (paylist.get("paylines") != null && paylist.get("paylines") instanceof List) {
					paylines = (List<Map<String, Object>>) paylist.get("paylines");
				}
				double RATE_DIFF = 0;
				for (Map<String, Object> payline : paylines) {
					RATE_DIFF = RATE_DIFF + (Double) payline.get("PV_PRICE");
				}
				result = new DecimalFormat("#.00").format(RATE_DIFF);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}
}
