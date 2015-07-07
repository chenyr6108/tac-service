package com.brick.common.command;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.common.service.CommonService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class CommonCommand extends BaseCommand {

	private CommonService commonService;
	
	public CommonService getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService=commonService;
	}

	public void getProductType(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> pagingInfo=null;
		//List<Map<String,Object>> resultList=this.commonService.getMaintaninceType();
		
		try {
			pagingInfo=baseService.queryForListWithPaging("prdcKind.getMaintaninceType",context.contextMap,"TYPE_1",ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		
		//outputMap.put("resultList",resultList);
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("productName",context.contextMap.get("PRODUCT_NAME"));
		Output.jspOutput(outputMap,context,"/productType/listAll.jsp");
	}
	
	public void delProductType(Context context) {
		
		boolean flag=false;
		this.commonService.delProductType(context);
		flag=true;
		
		Output.jsonFlageOutput(flag,context);
		
	}
	
	public void saveProductType(Context context) {
		
		boolean flag=false;
		List<Map<String,Object>> insertList=new ArrayList<Map<String,Object>>();
		insertList.add(context.contextMap);
		this.commonService.insertProductType(insertList);
		flag=true;
		
		Output.jsonFlageOutput(flag,context);
	}
	public void uploadExcel(Context context) throws InvalidFormatException, IOException {
		
		Map<String,Object> addMap=null;
		List<Map<String,Object>> addList=new ArrayList<Map<String,Object>>();
		
		Workbook workbook=null;
		
		InputStream in=(InputStream)context.getContextMap().get("excelInputStream");
		
		workbook=WorkbookFactory.create(in);//创建excel workbook
		
		Sheet sheet=workbook.getSheetAt(0);//获得第一个sheet
		
		Iterator<Row> rowIterator=sheet.iterator();//遍历行
		
		while(rowIterator.hasNext()) {
			
			Row row=(Row)rowIterator.next();//获得行
			
			Iterator<Cell> cellIterator=row.cellIterator();//遍历单元格
			
			addMap=new HashMap<String,Object>();
			
			while(cellIterator.hasNext()) {
				
				Cell cell=(Cell)cellIterator.next();//获得单元格
				if(cell.getColumnIndex()==0) {
					addMap.put("TYPE_1", cell.getStringCellValue());
				} else if(cell.getColumnIndex()==1) {
					addMap.put("TYPE_2", cell.getStringCellValue());
				} else if(cell.getColumnIndex()==2) {
					addMap.put("PRODUCT_NAME", cell.getStringCellValue());
				}
			}
			
			addList.add(addMap);
		}
		
		this.commonService.insertProductType(addList);
	}
	
	public void chartCondition(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		Calendar cal=Calendar.getInstance();
		String fromDate=null;
		String toDate=null;
		
		List<String> dayList=new ArrayList<String>();
		List<String> weekList=new ArrayList<String>();
		List<String> monthList=new ArrayList<String>();
		
		if("".equals(context.contextMap.get("fromDate"))&&"".equals(context.contextMap.get("toDate"))) {
			if("week".equals(context.contextMap.get("dateFormat"))) {
				//如果是选择天的话,取系统时间所在周的周日为From Date
				cal.add(Calendar.MONTH,-6);
				cal.set(Calendar.DAY_OF_WEEK,1);
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				//如果是选择天的话,取系统时间所在周的周六为To Date
				cal.add(Calendar.MONTH,6);
				cal.set(Calendar.DAY_OF_WEEK,7);
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				
				cal.set(Calendar.DAY_OF_WEEK,1);
				dayList.add(fromDate);
				for(int i=0;i<7;i++) {
					cal.add(Calendar.DATE,1);
					dayList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
				}
			}
		} else {
			if("day".equals(context.contextMap.get("dateFormat"))) {
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				Date fd=cal.getTime();
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("toDate").toString(),"yyyy-MM-dd"));
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				Date td=cal.getTime();
				long loop=(td.getTime()-fd.getTime())/(1000*60*60*24);
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				
				dayList.add(fromDate);
				for(int i=0;i<loop;i++) {
					cal.add(Calendar.DATE,1);
					dayList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
				}
				
			} else if("week".equals(context.contextMap.get("dateFormat"))) {
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				cal.set(Calendar.DAY_OF_WEEK,6);
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				Date fd=cal.getTime();
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("toDate").toString(),"yyyy-MM-dd"));
				cal.set(Calendar.DAY_OF_WEEK,6);
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				Date td=cal.getTime();
				
				long loop=((td.getTime()-fd.getTime())/(1000*60*60*24)+1)/7;
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				for(int i=0;i<loop;i++) {
					cal.set(Calendar.DAY_OF_WEEK,6);
					weekList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
					cal.add(Calendar.DATE,7);
				}
			} else if("month".equals(context.contextMap.get("dateFormat"))) {
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				fromDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("toDate").toString(),"yyyy-MM-dd"));
				cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				toDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				
				long loop=this.getMonthDiff(fromDate,toDate);
				
				cal.setTime(DateUtil.strToDate(context.contextMap.get("fromDate").toString(),"yyyy-MM-dd"));
				for(int i=0;i<loop;i++) {
					cal.add(Calendar.MONTH,i==0?0:1);
					cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
					monthList.add(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
				}
			}
		}
		
		outputMap.put("dayList",dayList);
		outputMap.put("weekList",weekList);
		outputMap.put("monthList",monthList);
		outputMap.put("fromDate",fromDate);
		outputMap.put("toDate",toDate);
		Output.jsonOutput(outputMap,context);
	}
	
	private long getMonthDiff(String startDate,String endDate) {
		
		long monthday=0;
		try {
			SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
			Date startDate1=fmt.parse(startDate);   
			Calendar starCal=Calendar.getInstance();     
			starCal.setTime(startDate1);     
			int sYear =starCal.get(Calendar.YEAR);    
			int sMonth=starCal.get(Calendar.MONTH);    
			int sDay=starCal.get(Calendar.DAY_OF_MONTH); 
			Date endDate1=fmt.parse(endDate);    
			Calendar endCal=Calendar.getInstance();       
			endCal.setTime(endDate1);      
			int eYear =endCal.get(Calendar.YEAR);      
			int eMonth=endCal.get(Calendar.MONTH);  
			int eDay=endCal.get(Calendar.DAY_OF_MONTH);    
			monthday=((eYear - sYear) * 12 + (eMonth - sMonth));    
			if(sDay<eDay) {
				monthday=monthday+1;  
			} 
		} catch(Exception e) {
			
		}
		
		return monthday;
	}
	
	public void updateBigItem(Context context) throws Exception{
		String old_type = (String) context.contextMap.get("old_type1");
		String new_type = (String) context.contextMap.get("new_type1");
		commonService.updateType1(old_type, new_type,(String)context.contextMap.get("s_employeeName"),(String)context.contextMap.get("IP"));
		Output.jsonFlageOutput(true,context);
	}
	
	public void updateSmallItem(Context context) throws Exception{
		String old_type = (String) context.contextMap.get("old_type2");
		String new_type = (String) context.contextMap.get("new_type2");
		String type1 = (String) context.contextMap.get("type1");
		String productLevel = (String) context.contextMap.get("productLevel");
		commonService.updateType2(old_type, new_type,type1,productLevel,(String)context.contextMap.get("s_employeeName"),(String)context.contextMap.get("IP"));
		Output.jsonFlageOutput(true,context);
	}
	
	public void getProductTypeItems(Context context){
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List list = commonService.getMaintaninceType1();
		outputMap.put("list",list);
		Output.jspOutput(outputMap, context, "/productType/item.jsp");
	}
}
