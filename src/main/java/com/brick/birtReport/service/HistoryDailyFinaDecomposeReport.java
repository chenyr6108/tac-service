package com.brick.birtReport.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.util.DateUtil;

public class HistoryDailyFinaDecomposeReport {
	
	//Add by Michael 2012 5-14  通过日期  查询报表
	public static Map<String,Object> finaTodayFinaIncome(String query_date) throws Exception {
		System.out.println("==================Start========================");
		List finaTodayFinaIncome = new ArrayList();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("query_date", query_date);
		
		finaTodayFinaIncome = (List) DataAccessor.query("priceReport.getHistoryDailyDecomposeCashReport", paramMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("==================end========================");
		resultMap.put("finaTodayFinaIncome", finaTodayFinaIncome);
		return resultMap;
	}
	
	public static Map<String,Object> finaLastDecompose(String query_date) throws Exception {
		System.out.println("==================Start========================");
		List finaLastDecompose = new ArrayList();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("query_date", query_date);
		finaLastDecompose = (List) DataAccessor.query("priceReport.getHistoryDailyDecomposeLastReport", paramMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==================end========================");
		resultMap.put("finaLastDecompose", finaLastDecompose);
		return resultMap;
	}
	
	public static Map<String,Object> finaLastDynamicDecompose(String query_date) throws Exception {
		System.out.println("==================Start========================");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("query_date", query_date);
		List finaLastDynamicDecompose = new ArrayList();
		finaLastDynamicDecompose = (List) DataAccessor.query("priceReport.getHistoryDailyDecomposeDynamicReport", paramMap, DataAccessor.RS_TYPE.LIST);
		resultMap.put("finaLastDynamicDecompose", finaLastDynamicDecompose);
		System.out.println("==================end========================");
		return resultMap;
	}
	
	public static Map<String,Object> queryCarPayAccountDetail(String query_date, String contract_type, String user_id, boolean isNew) throws Exception {
		try {
			System.out.println("==================Start========================");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("query_date", query_date);
			paramMap.put("contract_type", contract_type);
			List queryDayList =  new ArrayList();
			queryDayList = (List) DataAccessor.query(
					"financeDecomposeReport.getQueryDayList", paramMap,
					DataAccessor.RS_TYPE.LIST);
			Map queryDay;
			String strDate = "";
			for (int i = 0; i < queryDayList.size(); i++) {
				queryDay = (Map) queryDayList.get(i);
				if (null != queryDay) {
					if (queryDay.get("DAY_TYPE").equals("WD")) {
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
						break;
					}else{
						if (i==0){
							strDate += "convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}else{
							strDate += ",convert(date,'"
									+ String.valueOf(queryDay.get("DATE")) + "')";
						}
					}
				}
			}
			paramMap.put("DATES", strDate);
			List<Map> carPayApplyInfoList = new ArrayList();
			carPayApplyInfoList = (List<Map>) DataAccessor.query("financeDecomposeReport.getCarPayApplyInfo", paramMap, DataAccessor.RS_TYPE.LIST);
			
			/*********************在逾期催收中增加扣款记录*************************/
			for (Map map : carPayApplyInfoList) {
				map.put("user_id", Integer.parseInt(user_id));
				String context = "扣款日期：" + DateUtil.getCurrentDate() + "，期数：" + String.valueOf(map.get("PERIOD_NUM"));
				map.put("context", context);
				DataAccessor.execute("dunTask.addDunRecordForDun", map, OPERATION_TYPE.INSERT);
			}
			/*********************在逾期催收中增加扣款记录*************************/
			
			BigDecimal total = new BigDecimal(0);
			if(carPayApplyInfoList!=null){
				for(Map m:carPayApplyInfoList){
					total = total.add(new BigDecimal((Double)m.get("IRR_MONTH_PRICE")));
				}
				resultMap.put("total_price",total);
			}
			
			//新导出扣款资料 农行>10000需拆分，租金需+1
			if(isNew){
				List<Map> carPayApplyInfoListNew = new ArrayList<Map>();
				for(Map map : carPayApplyInfoList){
					String bankName = map.get("BANK_NAME")==null?"":map.get("BANK_NAME").toString();
					double irrMonthPrice = map.get("IRR_MONTH_PRICE")==null?0:Double.parseDouble(map.get("IRR_MONTH_PRICE").toString());
					if((bankName.indexOf("农业") >= 0 || bankName.indexOf("农行") >= 0) && irrMonthPrice > 10000){
						int count = (int)Math.floor(irrMonthPrice / 10000);
						double lastPrice = irrMonthPrice % 10000 + count + 1;
						map.put("IRR_MONTH_PRICE",10000);
						for(int i = 0; i < count; i++){
							carPayApplyInfoListNew.add(new HashMap(map));
						}
						map.put("IRR_MONTH_PRICE",lastPrice);
						carPayApplyInfoListNew.add(map);
					} else if(irrMonthPrice == 0){
						continue;
					} else {
						map.put("IRR_MONTH_PRICE",irrMonthPrice + 1);
						carPayApplyInfoListNew.add(map);
					}
				}
				resultMap.put("carPayApplyInfoList", carPayApplyInfoListNew);
			} else {
				resultMap.put("carPayApplyInfoList", carPayApplyInfoList);
			}
			
			
			System.out.println("==================end========================");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
