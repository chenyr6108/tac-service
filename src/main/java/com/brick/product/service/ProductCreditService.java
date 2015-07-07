package com.brick.product.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;

public class ProductCreditService extends BaseService {
	
	Log logger = LogFactory.getLog(ProductCreditService.class);
	
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void saveProductCreditPlan(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> product = null;		
		Map<String, Object> grant = null;		
		Map<String, Object> grantplan = null;		
		List<Map<String, Object>> grantdetail = null;		
		//Map<String, Object> result = null ;
		Map<String, Object> old = null ;
		try {
			product = (Map<String, Object>) queryForObj("productCredit.queryAllproductCredit", context.contextMap);
			grant =  (Map<String, Object>) queryForObj("productCredit.queryAllGrantplanById", context.contextMap);		
			outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
			context.contextMap.put("CUGP_STATUS", 0);
			
			if(grant == null){
				context.contextMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				context.contextMap.put("MOFIFY_ID", context.contextMap.get("s_employeeId"));
				old = new HashMap<String, Object>();
				insert("productCredit.creatProductCreditPlan", context.contextMap);
				//result = (Map<String, Object>) queryForObj("productCredit.queryAllGrantplanById", context.contextMap);		
			}else{
				context.contextMap.put("CREATE_ID", context.contextMap.get("s_employeeId"));
				context.contextMap.put("MODIFY_ID", context.contextMap.get("s_employeeId"));
				old = (Map<String, Object>) queryForObj("productCredit.queryAllGrantplanById", context.contextMap);
				update("productCredit.updateProductCreditPlan", context.contextMap);
			}
			//记录更新日志
			saveLog(old, context.contextMap);
			
			//联合授信
			if(DataUtil.intUtil(context.contextMap.get("UNION_CREDIT"))==1){
				insert("productCredit.insertUnion01", context.contextMap);
				insert("productCredit.insertUnion02", context.contextMap);
			}
			String PDGP_ID = HTMLUtil.getStrParam(context.request, "PDGP_ID", "");
			/*if(PDGP_ID == null || "".equals(PDGP_ID)){
				PDGP_ID = result.get("PDGP_ID").toString();
			}*/
			String[] LEASE_PERIOD1 = HTMLUtil.getParameterValues(context.request, "LEASE_PERIOD1", "0");
			String[] LEASE_TERM1 = HTMLUtil.getParameterValues(context.request, "LEASE_TERM1", "0");
			String[] PAY_WAY1 = HTMLUtil.getParameterValues(context.request, "PAY_WAY1", "0");
			String[] MANAGEMENT_FEE_RATE1 = HTMLUtil.getParameterValues(context.request, "MANAGEMENT_FEE_RATE1", "0");
			String[] HEAD_HIRE_PERCENT1 = HTMLUtil.getParameterValues(context.request, "HEAD_HIRE_PERCENT1", "0");
			String[] PLEDGE_PRICE_RATE1 = HTMLUtil.getParameterValues(context.request, "PLEDGE_PRICE_RATE1", "0");
			String[] FLOAT_RATE1 = HTMLUtil.getParameterValues(context.request, "FLOAT_RATE1", "0");
			
			String[] PDGD_ID = HTMLUtil.getParameterValues(context.request, "PDGD_ID", "0");
			String[] LEASE_PERIOD = HTMLUtil.getParameterValues(context.request, "LEASE_PERIOD", "0");
			String[] LEASE_TERM = HTMLUtil.getParameterValues(context.request, "LEASE_TERM", "0");
			String[] PAY_WAY = HTMLUtil.getParameterValues(context.request, "PAY_WAY", "0");
			String[] MANAGEMENT_FEE_RATE = HTMLUtil.getParameterValues(context.request, "MANAGEMENT_FEE_RATE", "0");
			String[] HEAD_HIRE_PERCENT = HTMLUtil.getParameterValues(context.request, "HEAD_HIRE_PERCENT", "0");
			String[] PLEDGE_PRICE_RATE = HTMLUtil.getParameterValues(context.request, "PLEDGE_PRICE_RATE", "0");
			String[] FLOAT_RATE = HTMLUtil.getParameterValues(context.request, "FLOAT_RATE", "0");
			
			Map<String, Object> mapCreate = null;
			Map<String, Object> mapUpdate = null;
			if (LEASE_PERIOD1.length != 0) {
				for (int i = 0; i < LEASE_PERIOD1.length; i++) {
					mapCreate = new HashMap<String, Object>();
					mapCreate.put("PDGP_ID", DataUtil.intUtil(PDGP_ID));
					mapCreate.put("LEASE_PERIOD", DataUtil.intUtil(LEASE_PERIOD1[i]));
					mapCreate.put("LEASE_TERM", DataUtil.intUtil(LEASE_TERM1[i]));
					mapCreate.put("PAY_WAY", DataUtil.intUtil(PAY_WAY1[i]));
					mapCreate.put("MANAGEMENT_FEE_RATE", DataUtil.doubleUtil(MANAGEMENT_FEE_RATE1[i]));
					mapCreate.put("HEAD_HIRE_PERCENT",  DataUtil.doubleUtil(HEAD_HIRE_PERCENT1[i]));
					mapCreate.put("PLEDGE_PRICE_RATE",  DataUtil.doubleUtil(PLEDGE_PRICE_RATE1[i]));
					mapCreate.put("FLOAT_RATE",  DataUtil.doubleUtil(FLOAT_RATE1[i]));
					
					mapCreate.put("CREATE_ID", context.contextMap.get("s_employeeId"));
					mapCreate.put("MODIFY_ID", context.contextMap.get("s_employeeId"));
					insert("productCredit.createProductCreditDetail",mapCreate);
					
				}
			}
			if(PDGD_ID.length != 0){
				for (int j = 0; j < PDGD_ID.length; j++) {
					mapUpdate = new HashMap<String, Object>();
					mapUpdate.put("PDGP_ID", DataUtil.intUtil(PDGP_ID));
					mapUpdate.put("PDGD_ID", DataUtil.intUtil(PDGD_ID[j]));
					mapUpdate.put("LEASE_PERIOD", DataUtil.intUtil(LEASE_PERIOD[j]));
					mapUpdate.put("LEASE_TERM", DataUtil.intUtil(LEASE_TERM[j]));
					mapUpdate.put("PAY_WAY", DataUtil.intUtil(PAY_WAY[j]));
					mapUpdate.put("MANAGEMENT_FEE_RATE", DataUtil.doubleUtil(MANAGEMENT_FEE_RATE[j]));
					mapUpdate.put("HEAD_HIRE_PERCENT", DataUtil.doubleUtil(HEAD_HIRE_PERCENT[j]));
					mapUpdate.put("PLEDGE_PRICE_RATE", DataUtil.doubleUtil(PLEDGE_PRICE_RATE[j]));
					mapUpdate.put("FLOAT_RATE", DataUtil.doubleUtil(FLOAT_RATE[j]));
					mapUpdate.put("MOFIFY_ID", context.contextMap.get("s_employeeId"));
					update("productCredit.updateProductCreditDetail",mapUpdate);
				}
			}
			grantplan =  (Map<String, Object>) queryForObj("productCredit.queryAllGrantplanById", context.contextMap);	
			context.contextMap.put("PDGP_ID", grantplan.get("PDGP_ID"));
			grantdetail =  (List<Map<String, Object>>) queryForList("productCredit.queryAllGrantdetailById", context.contextMap);
			outputMap.put("product", product);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}
	}
	
	private void saveLog(Map<String, Object> old, Map<String, Object> contextMap) {
	 	String[] checkedField = new String[]{"LIEN_GRANT_PRICE"
	 	,"LIEN_REPEAT_CREDIT"
	 	,"LIEN_START_DATE"
		,"LIEN_END_DATE"
	 	,"REPURCH_GRANT_PRICE"
	 	,"REPURCH_REPEAT_CREDIT"
	 	,"REPURCH_START_DATE"
		,"REPURCH_END_DATE"
	 	,"ADVANCEMACHINE_HAS"
	 	,"ADVANCEMACHINE_GRANT_PRICE"
		,"ADVANCE_MACHINEREPEAT_CREDIT"
		,"ADVANCE_START_DATE"
		,"ADVANCE_END_DATE"
	 	,"VOICE_CREDIT"
		,"VOICE_START_DATE"
		,"VOICE_END_DATE"
		,"VOICE_CONTINUE"};
	 	boolean saveLogFlag = false;
		for (String field : checkedField) {
			if (!isEqual(old.get(field), contextMap.get(field))) {
				saveLogFlag = true;
				break;
			}
		}
		if (saveLogFlag) {
			insert("productCredit.addGrantPlanLog", contextMap);
		}
		
	}
	
	private boolean isEqual(Object o, Object n){
		boolean flag = false;
		if (obj2Str(o).equals(obj2Str(n))) {
			flag = true;
		}
		return flag;
	}
	
	private String obj2Str(Object o){
		String result = null;
		if (o instanceof Date) {
			result = DateUtil.dateToString((Date)o);
		} else if (o instanceof java.sql.Date) {
			result = DateUtil.dateToString((java.sql.Date)o);
		} else if (o instanceof Double) {
			result = new DecimalFormat("#0.00").format(o);
		} else {
			result = String.valueOf(o);
		}
		return result;
	}

}
