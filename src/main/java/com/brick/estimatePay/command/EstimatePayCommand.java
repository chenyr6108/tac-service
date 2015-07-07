package com.brick.estimatePay.command;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.estimatePay.service.EstimatePayService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

public class EstimatePayCommand extends BaseCommand {

	Log logger = LogFactory.getLog(EstimatePayCommand.class);

	private EstimatePayService estimatePayService;

	public EstimatePayService getEstimatePayService() {
		return estimatePayService;
	}

	public void setEstimatePayService(EstimatePayService estimatePayService) {
		this.estimatePayService = estimatePayService;
	}
	
	
	/*
	 * 
	 * 
	 * 
	 *       displayDate：前台显示的天数
	 *       outputMap：封装返回的数据
	 *       searchDate:查询的日期(默认值:当天)
	 *       params:设置参数
	 *       workList:工作日的列表
	 */

	public void query(Context context) {

		Map<String, Object> outputMap = new HashMap<String, Object>();

		Map<String, Object> params = new HashMap<String, Object>();

		List<HashMap<String, Object>> DateList = null;
		

        
		//format date
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		DateFormat da=DateFormat.getDateInstance();
		Calendar date = Calendar.getInstance();
		String searchDate = (String) context.contextMap.get("searchDate");
        if(searchDate==null||"".equals(searchDate)){
        	params.put("jobDay", new Date());
        	outputMap.put("searchTime", "");
        	date.setTime(new Date());
        	date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);                 //当前的日期的前一天
        	outputMap.put("dataTime", sdf.format(date.getTime()));
        }else{
        	params.put("jobDay", searchDate);
        	outputMap.put("searchTime",searchDate);
        	Date d=null;
			try {
				d = da.parse(searchDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	date.setTime(d);
        	date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        	outputMap.put("dataTime", sdf.format(date.getTime()));
        }
		
		List<HashMap<String, Object>> workDayList = this
				.getEstimatePayService().getWorkDay(context,params);
		
		List<String> list =new ArrayList<String>();                             //装载符合查询条件的日期 
          
		for(int i=0;i<workDayList.size();i++){
			String s = (String) workDayList.get(i).get("DATE");
			params.put("d" + i, s);
		}
		
		//在前台展示的天数
		String displayDate=(String)context.contextMap.get("DISPLAY");
		   if(displayDate==null||"".equals(displayDate)){
			   outputMap.put("display", 7);                     //默认显示 7天
			   for(int i=0;i<workDayList.size();i++){
				   if(i==7){
					   break;
				   }
					String s = (String) workDayList.get(i).get("DATE");
					list.add(s);
				                                 
			   }
		   }else{
			   outputMap.put("display", displayDate);
			   int p=Integer.parseInt(displayDate);
		         for(int i=0;i<workDayList.size();i++){
				   if(i==p){
					   break;
				   }
					String s = (String) workDayList.get(i).get("DATE");
					list.add(s);
			   }
		   }
			
			
			DateList = this.getEstimatePayService().getDataList(params);
			
            List<HashMap<String,Object>> total=this.getEstimatePayService()
            		                            .getDataListByDay(params);
			

		if (context.errList.isEmpty()) {
			outputMap.put("dataList", DateList);
			outputMap.put("workDayList", list);                          
			outputMap.put("total", total);                                             //total：总金额及总件数
			Output.jspOutput(outputMap, context, "/estimatePay/estimatePay.jsp");
		} else {
			outputMap.put("errList", context.errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

}
