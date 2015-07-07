package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;

public class InterestService extends BaseCommand{
	private static Log logger=LogFactory.getLog(InterestService.class);
	/**
	 * 利息收入明细
	 */
	@SuppressWarnings("unchecked")
	public void interestDetail(Context context){
		List errList=context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw=null;
		//Modify by Michael 2012 01/13 变更利息计算方式
		try{
			if(context.contextMap.get("start_date") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("start_date", sf.format(new Date())) ;
			}else {
				context.contextMap.put("start_date", context.contextMap.get("start_date") + "-01") ;
			}
		 	outputMap.put("start_date", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("start_date").toString())) ;
			
		 	dw=(PagingInfo)baseService.queryForListWithPaging("interestDetail.queryInterestDetail", context.contextMap, "RECP_ID",ORDER_TYPE.DESC);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		
		if(errList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content")) ;
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("companys", LeaseUtil.getCompanys());	
			//outputMap.put("start_date", context.contextMap.get("start_date")) ;
			//outputMap.put("endDate", context.contextMap.get("endDate")) ;
			Output.jspOutput(outputMap, context, "/report/queryInterest.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void expInterestDetail(Context context){
		List errList=context.errList;
		Map exportMap = new HashMap();
		String strFileName = "利息收入明细表.xls";
		
		try{
			//Add by Michael 2012 01/13 修改财务利息抓取逻辑
			if(context.contextMap.get("start_date") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("start_date", sf.format(new Date())) ;
			}else {
				context.contextMap.put("start_date", context.contextMap.get("start_date") + "-01") ;
			}
			
			List<Map> content=(List<Map>) DataAccessor.query("interestDetail.queryInterestDetail", context.contextMap,DataAccessor.RS_TYPE.LIST);
			exportMap.put("content", content);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}

		
		ByteArrayOutputStream baos = null;
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportInterestExcel(exportMap);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			insuranceUtil.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
	
}
