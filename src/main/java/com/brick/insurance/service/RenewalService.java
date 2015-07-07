package com.brick.insurance.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.StringUtils;

public class RenewalService extends AService {
	static Log logger = LogFactory.getLog(InusranceListService.class);
	/**
	 * 续保提醒
	 * 
	 */
	@SuppressWarnings("unchecked")
	public  void queryAll(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List insuCompany =null;
		DataWrap dw = null;
		if (errList.isEmpty()) {
			try {
				//查出所有的保险公司
				insuCompany=(List) DataAccessor.query("insurance.getCompany",context.contextMap, DataAccessor.RS_TYPE.LIST);
				/* 2011/12/30 Yang Yun 默认查询区间 一个礼拜以内的。 */
				String insu_start_date = (String) context.contextMap.get("INSU_START_DATE");
				String insu_end_date = (String) context.contextMap.get("INSU_END_DATE");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date today = new Date();
				if (StringUtils.isEmpty(insu_start_date) && StringUtils.isEmpty(insu_end_date)) {
					insu_start_date = sdf.format(today);
					Calendar day = Calendar.getInstance();
					day.setTime(today); 
					day.add(day.DATE, 7); 
					insu_end_date = sdf.format(day.getTime());
					context.contextMap.put("INSU_START_DATE", insu_start_date);
					context.contextMap.put("INSU_END_DATE", insu_end_date);
				}
				context.contextMap.put("output", "已导出");
				/* 2011/12/30 Yang Yun 默认查询区间 一个礼拜以内的。 */
				dw = (DataWrap) DataAccessor.query("insurance.renewalManage",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				errList.add("保单管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("insuCompany", insuCompany);
			outputMap.put("INCP_ID", context.getContextMap().get("INCP_ID"));
			outputMap.put("INSU_START_DATE", context.getContextMap().get("INSU_START_DATE"));
			outputMap.put("INSU_END_DATE", context.getContextMap().get("INSU_END_DATE"));
			outputMap.put("content", context.contextMap.get("content"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/renewal/insuranceManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
}
