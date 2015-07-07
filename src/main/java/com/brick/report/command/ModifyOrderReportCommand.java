package com.brick.report.command;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class ModifyOrderReportCommand extends BaseCommand{
	


	/**
	 * 业管文审统计表
	 * @param context
	 * @throws Exception
	 */
	public void getReport(Context context) throws Exception{
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		
		int currentYear = Integer.parseInt(DateUtil.dateToString(new Date(), "yyyy"));
		int searchYear = currentYear;
		String year = (String) context.contextMap.get("year");
		if (!StringUtils.isEmpty(year)) {
			try {
				searchYear = Integer.parseInt(year);
			} catch (NumberFormatException e) {
				searchYear = currentYear;
			}
		}
		outputMap.put("year", searchYear);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", searchYear);
		List<Map<String,Object>> resultList = (List<Map<String, Object>>) baseService.queryForList("report.getModifyOrderReport", paramMap);
		//合计的数量  需要加上未处理的数量
		if(resultList!=null){	
			int amountPosition = -1;
			int undealPosition = -1;
			for(int i=0,j=resultList.size();i<j;i++){
				Integer userid = (Integer) resultList.get(i).get("USERID");
				if(userid == null){
					amountPosition  = i;
						
				} else if(userid!=null && userid==-1){				
					undealPosition = i;
				}
			}
			if(undealPosition>=0){
				for(int no =1;no<=13;no++){
					String key = "QTY_";
					if(no==13){
						key += "TOTAL";
					}else{
						key += no;
					}
					int qty = ((Integer)resultList.get(amountPosition).get(key)).intValue() + ((Integer)resultList.get(undealPosition).get(key)).intValue();
					resultList.get(amountPosition).put(key, qty);
				}			
			}
		}
		
		List<Integer> yearList = (List<Integer>)DataAccessor.query("report.getModifyOrderYearList",null,RS_TYPE.LIST);
													
		outputMap.put("yearList", yearList);
		outputMap.put("resultList", resultList);
		Output.jspOutput(outputMap, context, "/report/modifyOrderReport.jsp");
	}
	
	/**
	 * 业管文审详细列表
	 * @param context
	 * @throws Exception
	 */
	public void showDetail(Context context) throws Exception{
		PagingInfo<Object> pagingInfo=null; 
		int year = Integer.parseInt((String) context.contextMap.get("year"));	

		context.contextMap.put("year", year);
		
		String month = (String) context.contextMap.get("month");
		if(!StringUtils.isEmpty(month)){
			context.contextMap.put("month", Integer.parseInt(month));
		}
		
		String userid = (String) context.contextMap.get("userid");
		
		if(!StringUtils.isEmpty(userid)){
			context.contextMap.put("userid", Integer.parseInt(userid));
		}
						
		pagingInfo = baseService.queryForListWithPaging("report.queryModifyOrderDetail",context.contextMap,"APPLY_TIME",ORDER_TYPE.ASC);
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("year", year);
		outputMap.put("month", month);
		outputMap.put("userid", userid);
		outputMap.put("dw", pagingInfo);
		Output.jspOutput(outputMap, context, "/report/modifyOrderList.jsp");
		
	}

}
