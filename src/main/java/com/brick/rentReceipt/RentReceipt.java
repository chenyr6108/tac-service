package com.brick.rentReceipt;

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
import com.brick.log.service.LogPrint;
import com.brick.report.service.InsuranceUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;

public class RentReceipt extends BaseCommand{
	Log logger = LogFactory.getLog(RentReceipt.class);
	/**
	 * 本金收据列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentReceiptList(Context context)  {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
			try { 
				
				context.contextMap.put("ficb_item", "租金");
				//调分页查询方法
				if(context.contextMap.get("startDate") != null && !"".equals(context.contextMap.get("startDate"))){
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				
				dw = baseService.queryForListWithPaging("rentReceipt.queryRentReceiptList", context.contextMap,"PRINCIPALRUNCODE", ORDER_TYPE.DESC);
				
				if(context.contextMap.get("startDate")!=null && (!"".equals(context.contextMap.get("startDate")))) {
					outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("ISINVOICE", context.contextMap.get("ISINVOICE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));
		outputMap.put("OLDNEW_STATUS", context.contextMap.get("OLDNEW_STATUS"));
		

		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryRentReceipt.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	/**
	 * 本金收据Excel 列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentReceiptListToExcel(Context context)  {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List receiptList = null;
			try { 
				//调分页查询方法
				if(context.contextMap.get("startDate") != null){
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				
				receiptList = baseService.queryForList("rentReceipt.queryRentReceiptList", context.contextMap);
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
			ByteArrayOutputStream baos = null;
			String strFileName = "本金收据Excel("+DataUtil.StringUtil(context.contextMap.get("startDate"))+").xls";
			
			InsuranceUtil insuranceUtil = new InsuranceUtil();
			insuranceUtil.createexl();
			baos = insuranceUtil.exportRentReceiptList(receiptList);
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
			}
	}
	
	/**
	 * 打印本金收据日志列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewLog(Context context) { 
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		try {
			//调分页查询方法
			dw = baseService.queryForListWithPaging("rentReceipt.queryReciptLog", context.contextMap,"LOGCREATEDATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryReceiptLog.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
}
