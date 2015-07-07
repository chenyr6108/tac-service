package com.brick.dataStatistics.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.SimpleDateFormat;

public class RentOfDayCommand extends BaseCommand {
           
	Log logger=LogFactory.getLog(RentOfDayCommand.class);
	
	         @SuppressWarnings("unchecked")
			public void query(Context context){
	        	 
	        	 Map<String, Object> outputMap = new HashMap<String, Object>();
	        	 DataWrap dw=null;
	        	 
	        	 //如果查询日期为空，则查询所有
	        	 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	 if(context.contextMap.get("SEARCHDATE")==null||"".equals(context.contextMap.get("SEARCHDATE"))){
	        		 context.contextMap.put("payDate", context.contextMap.get("SEARCHDATE"));
	        	 }else{
	        		 context.contextMap.put("payDate",context.contextMap.get("SEARCHDATE") );
	        	 }
	        	 
	        	 try{
	        		 dw=(DataWrap)DataAccessor.query("dataStaticReport.getStaticList", context.contextMap, DataAccessor.RS_TYPE.PAGED);
	        	 }catch(Exception e){
	        		LogPrint.getLogStackTrace(e, logger);
	     			e.printStackTrace();
	    			context.errList.add("数据查询失败!");
	        	 }
	        	 
	        	 //计算比例
	        	 List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	        	 if(dw.rs !=null){
	        		 lst=(List<HashMap<String,Object>>)dw.rs;
	        	 }
	        	 for(int i=0;i<lst.size();i++){
	        		 BigDecimal result=null;
	        		 BigDecimal rentOfPay=new BigDecimal((Double)lst.get(i).get("RENTOFDAY"));
	        		 BigDecimal actPay=new BigDecimal((Double)lst.get(i).get("ACTUREPAY"));
	        		 result=actPay.multiply(new BigDecimal(100)).divide(rentOfPay,4,BigDecimal.ROUND_HALF_UP);
	        		 lst.get(i).put("scale", result);
	        	 }
	        	 
	        	 if(context.errList.isEmpty()){
	        		 outputMap.put("backDate", sdf.format(new Date()));
	        		 outputMap.put("dw",dw);
	        		 outputMap.put("payDate", context.contextMap.get("payDate"));
	        		 outputMap.put("dataList", lst);
	        		 Output.jspOutput(outputMap, context, "/rentcontract/rentOfDay.jsp");
	        	 }else{
	 				outputMap.put("errList", context.errList);
					Output.jspOutput(outputMap, context, "/error.jsp");
	        	 }
	         }
}
