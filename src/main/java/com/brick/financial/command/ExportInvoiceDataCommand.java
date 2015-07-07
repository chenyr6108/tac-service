package com.brick.financial.command;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.financial.util.LeaseBackInvoiceDataExcel;
import com.brick.financial.util.VATInvoice;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.StringUtils;

public class ExportInvoiceDataCommand extends BaseCommand {
              
	Log logger=LogFactory.getLog(ExportInvoiceDataCommand.class);

	       /*
	        * 
	        * param 设定 查询 参数   
	        * 当设定 creditNum为 0表示 旧案,为1时表示新案
	        */
	       
	       
	        //导出 普票资料-旧案
	       @SuppressWarnings("unchecked")
		public void exportGenerelInvoiceWithOldCredit(Context context){
	    	  // System.out.println("导出普票旧案");
		       Map<String,Object> param=new HashMap<String,Object>();
		       List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
		       List<HashMap<String,Object>> output=new ArrayList<HashMap<String,Object>>();
		       LeaseBackInvoiceDataExcel lde=new LeaseBackInvoiceDataExcel();
	    	   param=getDifTime();
	    	   param.put("creditNum", 0);
	    	   param.put("companyCode", context.contextMap.get("companyCode"));
	    	   try{
		    	   int s=0;
		    	   String re=null;
	    		   result=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.getInvoiceData", param, DataAccessor.RS_TYPE.LIST);
	    		   int count=1;
	    		   for(int i=0;result!=null&&i<result.size();i++){
	    			   
	    			   HashMap<String,Object> dataMap=new HashMap<String,Object>();
	   				    dataMap.put("RECD_ID",result.get(i).get("RECD_ID"));
						dataMap.put("RECP_ID",result.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",result.get(i).get("RUNNUM"));
						dataMap.put("LINK_ADDRESS",result.get(i).get("LINK_ADDRESS"));
						dataMap.put("CORP_TAX_CODE",result.get(i).get("CORP_TAX_CODE"));
						dataMap.put("BANK_NAME",result.get(i).get("BANK_NAME"));
						dataMap.put("CUST_NAME",result.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_KIND",result.get(i).get("PRODUCT_KIND"));
						dataMap.put("UNIT",result.get(i).get("UNIT"));
						dataMap.put("NUMBER",result.get(i).get("NUMBER"));
						dataMap.put("UNIT_PRICE",result.get(i).get("UNIT_PRICE"));
						dataMap.put("TAX_RATE",result.get(i).get("TAX_RATE"));
						dataMap.put("TAX_PLAN_CODE",result.get(i).get("TAX_PLAN_CODE"));
						s=Integer.parseInt(result.get(i).get("TAX_PLAN_CODE").toString());
						if(s==6){
							re="设备";
						}if(s==7){
							re="乘用车";
						}if(s==8){
							re="商用车";
						}
						dataMap.put("REMARK1",result.get(i).get("REMARK1")+"【本融资租赁本金即属"+re+"采购之本金】");
						
	    			   String num="";
	    			   if((count+"").length()==1){
	    				   num="000";
	    			   }else if((count+"").length()==2){
	    				   num="00";
	    			   }else if((count+"").length()==3){
	    				   num="0";
	    			   }
	    			  
	    				   dataMap.put("PRODUCT_NAME", "融资租赁本金");
	    				   dataMap.put("PRICE",Double.valueOf(result.get(i).get("OWN_PRICE")+""));
	    				   dataMap.put("REMARK2", num+count);
	    				   dataMap.put("REMARK3", "当期租金");
	    				   output.add(dataMap);
	    			
	    			   count++;
	    		   }
	    		   context.getResponse().setContentType("application/vnd.ms-excel;charset=GB2312");
	    		   context.response.setHeader("Content-Disposition"
	    					   ,"attachment;filename="+new String(("回租普通发票-旧案.xls").getBytes("GBK"),"ISO-8859-1"));
	    			 ServletOutputStream out=context.getResponse().getOutputStream();
	    			 context.contextMap.put("sheetName", "回租普通发票-旧案");
	    			 lde.exportInvoice(output, context).write(out);
	    			 out.flush();
	    			 out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //导出专票资料-旧案
	       @SuppressWarnings("unchecked")
		public void exportSpecialInvoiceWithOldCredit(Context context){
	    	  // System.out.println("导出专票旧案");
		       Map<String,Object> param=new HashMap<String,Object>();
		       List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
		       List<HashMap<String,Object>> output=new ArrayList<HashMap<String,Object>>();
		       LeaseBackInvoiceDataExcel lde=new LeaseBackInvoiceDataExcel();
	    	   param=getDifTime();
	    	   param.put("creditNum", 0);
	    	   param.put("companyCode", context.contextMap.get("companyCode"));
	    	   try{
		    	   int s=0;
		    	   String re=null;
	    		   result=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.getInvoiceData", param, DataAccessor.RS_TYPE.LIST);
	    		   int count=0;
	    		   for(int i=0;result!=null&&i<result.size();i++){
	    			   count++;
	    			   HashMap<String,Object> dataMap=new HashMap<String,Object>();
	   				    dataMap.put("RECD_ID",result.get(i).get("RECD_ID"));
						dataMap.put("RECP_ID",result.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",result.get(i).get("RUNNUM"));
						dataMap.put("LINK_ADDRESS",result.get(i).get("LINK_ADDRESS"));
						dataMap.put("CORP_TAX_CODE",result.get(i).get("CORP_TAX_CODE"));
						dataMap.put("BANK_NAME",result.get(i).get("BANK_NAME"));
						dataMap.put("CUST_NAME",result.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_KIND",result.get(i).get("PRODUCT_KIND"));
						dataMap.put("UNIT",result.get(i).get("UNIT"));
						dataMap.put("NUMBER",result.get(i).get("NUMBER"));
						dataMap.put("UNIT_PRICE",result.get(i).get("UNIT_PRICE"));
						dataMap.put("TAX_RATE",result.get(i).get("TAX_RATE"));
						dataMap.put("TAX_PLAN_CODE",result.get(i).get("TAX_PLAN_CODE"));
						s=Integer.parseInt(result.get(i).get("TAX_PLAN_CODE").toString());
						if(s==6){
							re="设备";
						}if(s==7){
							re="乘用车";
						}if(s==8){
							re="商用车";
						}
						dataMap.put("REMARK1",result.get(i).get("REMARK1"));
		    			   String num="";
		    			   if((count+"").length()==1){
		    				   num="000";
		    			   }else if((count+"").length()==2){
		    				   num="00";
		    			   }else if((count+"").length()==3){
		    				   num="0";
		    			   }
	    				   dataMap.put("PRODUCT_NAME", "融资租赁利息");
	    				   dataMap.put("PRICE",result.get(i).get("REN_PRICE"));
	    				   dataMap.put("REMARK2", num+count);
	    				   dataMap.put("REMARK3", "当期利息");
	    				   output.add(dataMap);
	    		   }
	    		   context.getResponse().setContentType("application/vnd.ms-excel;charset=GB2312");
	    		   context.response.setHeader("Content-Disposition"
	    					   ,"attachment;filename="+new String(("回租专用发票-旧案.xls").getBytes("GBK"),"ISO-8859-1"));
	    			 ServletOutputStream out=context.getResponse().getOutputStream();
	    			 context.contextMap.put("sheetName", "回租专用发票-旧案");
	    			 lde.exportInvoice(output, context).write(out);
	    			 out.flush();
	    			 out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //导出专票资料-新案
	       @SuppressWarnings("unchecked")
		public void exportSpecialInvoiceWithNewCredit(Context context){
	    	   //System.out.println("导出专票新案");
		       Map<String,Object> param=new HashMap<String,Object>();
		       List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
		       List<HashMap<String,Object>> output=new ArrayList<HashMap<String,Object>>();
		       LeaseBackInvoiceDataExcel lde=new LeaseBackInvoiceDataExcel();
	    	   param=getDifTime();
	    	   param.put("creditNum", 1);
	    	   param.put("companyCode", context.contextMap.get("companyCode"));
	    	   try{
		    	   int s=0;
		    	   String re=null;
	    		   result=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.getInvoiceData", param, DataAccessor.RS_TYPE.LIST);
	    		   int count=0;
	    		   for(int i=0;result!=null&&i<result.size();i++){
	    			   count++;
	    			   HashMap<String,Object> dataMap=new HashMap<String,Object>();
	   				    dataMap.put("RECD_ID",result.get(i).get("RECD_ID"));
						dataMap.put("RECP_ID",result.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",result.get(i).get("RUNNUM"));
						dataMap.put("LINK_ADDRESS",result.get(i).get("LINK_ADDRESS"));
						dataMap.put("CORP_TAX_CODE",result.get(i).get("CORP_TAX_CODE"));
						dataMap.put("BANK_NAME",result.get(i).get("BANK_NAME"));
						dataMap.put("CUST_NAME",result.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_KIND",result.get(i).get("PRODUCT_KIND"));
						dataMap.put("UNIT",result.get(i).get("UNIT"));
						dataMap.put("NUMBER",result.get(i).get("NUMBER"));
						dataMap.put("UNIT_PRICE",result.get(i).get("UNIT_PRICE"));
						dataMap.put("TAX_RATE",result.get(i).get("TAX_RATE"));
						dataMap.put("TAX_PLAN_CODE",result.get(i).get("TAX_PLAN_CODE"));
						s=Integer.parseInt(result.get(i).get("TAX_PLAN_CODE").toString());
						if(s==6){
							re="设备";
						}if(s==7){
							re="乘用车";
						}if(s==8){
							re="商用车";
						}
						dataMap.put("REMARK1",result.get(i).get("REMARK1"));
		    			   String num="";
		    			   if((count+"").length()==1){
		    				   num="000";
		    			   }else if((count+"").length()==2){
		    				   num="00";
		    			   }else if((count+"").length()==3){
		    				   num="0";
		    			   }
	    				   dataMap.put("PRODUCT_NAME", "融资租赁利息");
	    				   dataMap.put("PRICE",result.get(i).get("REN_PRICE"));
	    				   dataMap.put("REMARK2", num+count);
	    				   dataMap.put("REMARK3", "利息");
	    				   output.add(dataMap);
	    		   }
	    		   context.getResponse().setContentType("application/vnd.ms-excel;charset=GB2312");
	    		   context.response.setHeader("Content-Disposition"
	    					   ,"attachment;filename="+new String(("回租专用发票-新案.xls").getBytes("GBK"),"ISO-8859-1"));
	    			 ServletOutputStream out=context.getResponse().getOutputStream();
	    			 context.contextMap.put("sheetName", "回租专用发票-新案");
	    			 lde.exportInvoice(output, context).write(out);
	    			 out.flush();
	    			 out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //导出普票资料-新案
	       @SuppressWarnings("unchecked")
		public void exportGenerelInvoiceWithNewCredit(Context context){
	    	 //  System.out.println("导出普票新案");
		       Map<String,Object> param=new HashMap<String,Object>();
		       List<HashMap<String,Object>> result=new ArrayList<HashMap<String,Object>>();
		       List<HashMap<String,Object>> output=new ArrayList<HashMap<String,Object>>();
		       LeaseBackInvoiceDataExcel lde=new LeaseBackInvoiceDataExcel();
	    	   param=getDifTime();
	    	   param.put("creditNum", 1);
	    	   param.put("companyCode", context.contextMap.get("companyCode"));
	    	   try{
		    	   int s=0;
		    	   String re=null;
	    		   result=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.getInvoiceData", param, DataAccessor.RS_TYPE.LIST);
	    		   int count=1;
	    		   for(int i=0;result!=null&&i<result.size();i++){
	    			   for(int j=0;j<2;j++){
	    			   HashMap<String,Object> dataMap=new HashMap<String,Object>();
	   				    dataMap.put("RECD_ID",result.get(i).get("RECD_ID"));
						dataMap.put("RECP_ID",result.get(i).get("RECP_ID"));
						dataMap.put("RUNNUM",result.get(i).get("RUNNUM"));
						dataMap.put("LINK_ADDRESS",result.get(i).get("LINK_ADDRESS"));
						dataMap.put("CORP_TAX_CODE",result.get(i).get("CORP_TAX_CODE"));
						dataMap.put("BANK_NAME",result.get(i).get("BANK_NAME"));
						dataMap.put("CUST_NAME",result.get(i).get("CUST_NAME"));
						dataMap.put("PRODUCT_KIND",result.get(i).get("PRODUCT_KIND"));
						dataMap.put("UNIT",result.get(i).get("UNIT"));
						dataMap.put("NUMBER",result.get(i).get("NUMBER"));
						dataMap.put("UNIT_PRICE",result.get(i).get("UNIT_PRICE"));
						dataMap.put("TAX_RATE",result.get(i).get("TAX_RATE"));
						dataMap.put("TAX_PLAN_CODE",result.get(i).get("TAX_PLAN_CODE"));
						s=Integer.parseInt(result.get(i).get("TAX_PLAN_CODE").toString());
						if(s==6){
							re="设备";
						}if(s==7){
							re="乘用车";
						}if(s==8){
							re="商用车";
						}
						dataMap.put("REMARK1",result.get(i).get("REMARK1")+"【本融资租赁本金即属"+re+"采购之本金】");
						
	    			   String num="";
	    			   if((count+"").length()==1){
	    				   num="000";
	    			   }else if((count+"").length()==2){
	    				   num="00";
	    			   }else if((count+"").length()==3){
	    				   num="0";
	    			   }
	    			   
	    			   if(j==0){
	    				   dataMap.put("PRODUCT_NAME", "融资租赁本金");
	    				   dataMap.put("PRICE",Double.valueOf(result.get(i).get("OWN_PRICE")+"")-Double.valueOf(result.get(i).get("PLEDGE_PRICE")+""));
	    				   dataMap.put("REMARK2", num+count);
	    				   dataMap.put("REMARK3", "首租");
	    				   output.add(dataMap);
	    			   }if(j==1){
	    				   dataMap.put("PRODUCT_NAME", "融资租赁本金");
	    				   dataMap.put("PRICE",result.get(i).get("PLEDGE_PRICE"));
	    				   dataMap.put("REMARK2", num+count);
	    				   dataMap.put("REMARK3", "保证金");
	    				   if(j==1&&Double.valueOf(result.get(i).get("PLEDGE_PRICE")+"")<=0){
	    					   output.add(dataMap);
	    				   }else{
	    					   output.add(dataMap);
	    				   }
	    			   }
	    			 }
	    			   count++;
	    		   }
	    		   context.getResponse().setContentType("application/vnd.ms-excel;charset=GB2312");
	    		   context.response.setHeader("Content-Disposition"
	    					   ,"attachment;filename="+new String(("回租普通发票-新案.xls").getBytes("GBK"),"ISO-8859-1"));
	    			 ServletOutputStream out=context.getResponse().getOutputStream();
	    			 context.contextMap.put("sheetName", "回租普通发票-新案");
	    			 lde.exportInvoice(output, context).write(out);
	    			 out.flush();
	    			 out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
/*	       public static void main(String[] args){
	    	   Map<String,Object> m=new ExportInvoiceDataCommand().getDifTime();
	    	   System.out.println("qq");
	    	   System.out.println("qq");
	       }*/
	       
	       //计算 结账 周期 
	       public  Map<String,Object> getDifTime(){
	    	   Map<String,Object> param=new HashMap<String,Object>();
	    	   Date begin;
	    	   Date end;
	    	   Date now=new Date();
	    	   Calendar cal=Calendar.getInstance();
	    	   cal.setTime(new Date());
	    	   cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
	    	   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    	   int year=Integer.parseInt(new String(sdf.format(now)).split("-")[0]);
	    	   int month=Integer.parseInt(new String(sdf.format(now)).split("-")[1]);
	    	   int day=Integer.parseInt(new String(sdf.format(now)).split("-")[2]);
	    	   try{ 
	    		   begin=sdf.parse(year+"-"+month+"-"+"1");
	    		   param.put("BEGIN1", sdf.format(begin));
	    		   param.put("END1", sdf.format(cal.getTime()));
	    	   if(month==1){
	    		   if(day>25){
	    			   begin=sdf.parse(year+"-"+month+"-"+"26");
	    			   end=sdf.parse(year+"-"+(month+1)+"-"+"25");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }else{
	    			   begin=sdf.parse(year+"-"+month+"-"+"1");
	    			   end=sdf.parse(year+"-"+month+"-"+"25");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }
	    	   }
	    	   if(month>=2&&month<=10){
	    		   if(day>25){
	    			   begin=sdf.parse(year+"-"+month+"-"+"26");
	    			   end=sdf.parse(year+"-"+(month+1)+"-"+"25");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }else{
	    			   begin=sdf.parse(year+"-"+(month-1)+"-"+"26");
	    			   end=sdf.parse(year+"-"+month+"-"+"25");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }
	    	   }
	    	   if(month==11){
	    		   if(day>=1&&day<=25){
	    			   begin=sdf.parse(year+"-"+(month-1)+"-"+"26");
	    			   end=sdf.parse(year+"-"+month+"-"+"25");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }else{
	    			   begin=sdf.parse(year+"-"+month+"-"+"26");
	    			   end=sdf.parse(year+"-"+(month+1)+"-"+"31");
	    			   param.put("BEGIN", sdf.format(begin));
	    			   param.put("END", sdf.format(end));
	    		   }
	    	   }if(month==12){
    			   begin=sdf.parse(year+"-"+(month-1)+"-"+"26");
    			   end=sdf.parse(year+"-"+month+"-"+"31");
    			   param.put("BEGIN", sdf.format(begin));
    			   param.put("END", sdf.format(end));
	    	   }
	         }catch(Exception e){
	        	 e.printStackTrace();
	         }
	    	   return param;
	       }
	       
	       //增值税本金发票开具明细表
	       @SuppressWarnings("unchecked")
		public void queryWithVATDetail(Context context){
	    	   DecimalFormat nf = new DecimalFormat( "#,##0.00 ");
	    	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	   Map<String,Object> outputMap=new HashMap<String,Object>();
	    	   List<Map<String,Object>> dateList=null;
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   DataWrap dw=null;
	    	   try{
	    		   dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",context.contextMap,DataAccessor.RS_TYPE.LIST);
	    		   
	   			//获得财务结账周期
	   			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
	   				context.contextMap.put("startDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("endDate",dateList.get(0).get("ENDDATE"));
	   				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
	   			} else {
	   				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			}
	   			dw=(DataWrap)DataAccessor.query("priceReport.queryWithVATDetail", context.contextMap, DataAccessor.RS_TYPE.PAGED);
	   			  if(dw.rs!=null){
	   				  lst=(List<HashMap<String,Object>>)dw.rs;
	   				  for(int i=0;i<lst.size();i++){
	   					  double s=Double.parseDouble(lst.get(i).get("OWN_PRICE").toString());
	   					  lst.get(i).put("OWN_PRICE", nf.format(lst.get(i).get("OWN_PRICE")));
	   					  lst.get(i).put("princpleOutstandingTax", nf.format(s/1.17));
	   					  lst.get(i).put("tax", nf.format(s-s/1.17));
	   				  }
	   			  }
	    	   }catch(Exception e){
	        		LogPrint.getLogStackTrace(e, logger);
	     			e.printStackTrace();
	    			context.errList.add("数据查询失败!");
	    	   }
		      if(context.errList.isEmpty()){
		    	  outputMap.put("dw",dw);
		    	  outputMap.put("selectDate",context.contextMap.get("selectDate"));
			      outputMap.put("backDate", df.format(new Date()));
			      outputMap.put("lst",lst);
			      outputMap.put("dateList",dateList);
			      Output.jspOutput(outputMap, context, "/financial/VATInvoiceDetail.jsp");
	    	   }else{
	   			  outputMap.put("errList",context.errList) ;
				  Output.jspOutput(outputMap,context,"/error.jsp") ;
	    	   }
	       }
	       
	       //导出增值税本金发票开具明细
	       @SuppressWarnings("unchecked")
		public void exportVATInvoice(Context context) throws Exception{
	    	   DecimalFormat nf = new DecimalFormat( "#,##0.00 ");
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   VATInvoice vi=new VATInvoice();
	    	   String t=null;
	    	   try{

	   				t=context.contextMap.get("exportDate").toString();
	   				int year=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			
	    		   lst=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.queryWithVATDetail", context.contextMap,DataAccessor.RS_TYPE.LIST);
		   			  if(lst.size()>0){
		   				  for(int i=0;i<lst.size();i++){
		   					  double s=Double.parseDouble(lst.get(i).get("OWN_PRICE").toString());
		   					  lst.get(i).put("OWN_PRICE", nf.format(lst.get(i).get("OWN_PRICE")));
		   					  lst.get(i).put("princpleOutstandingTax", nf.format(s/1.17));
		   					  lst.get(i).put("tax", nf.format(s-s/1.17));
		   				  }
		   			  }
				   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				   context.response.setHeader("Content-Disposition"
						   ,"attachment;filename="+new String(("增值税本金发票开具明细("+t+").xls").getBytes("GBK"),"ISO-8859-1"));

				    
				    ServletOutputStream out=context.response.getOutputStream();
				    context.contextMap.put("sheetName", "增值税本金发票开具明细");
				    vi.exportInvoice(context,lst).write(out);
				    out.flush();
				    out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //增值税抵减明细表 
	       @SuppressWarnings("unchecked")
		public void queryWithVATOffsetAmount(Context context) throws Exception{
	    	   DecimalFormat nf = new DecimalFormat( "#,##0.00 ");
	    	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	   Map<String,Object> outputMap=new HashMap<String,Object>();
	    	   List<Map<String,Object>> dateList=null;
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   DataWrap dw=null;
	    	   try{
	    		   dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",context.contextMap,DataAccessor.RS_TYPE.LIST);
	    		   
	   			//获得财务结账周期
	   			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
	   				context.contextMap.put("startDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("endDate",dateList.get(0).get("ENDDATE"));
	   				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
	   			} else {
	   				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			}
	   			dw=(DataWrap)DataAccessor.query("priceReport.queryWithVATOffsetAmount", context.contextMap, DataAccessor.RS_TYPE.PAGED);
	   			  if(dw.rs!=null){
	   				  lst=(List<HashMap<String,Object>>)dw.rs;
	   				  for(int i=0;i<lst.size();i++){
	   					  lst.get(i).put("LEASE_TOPRIC", nf.format(lst.get(i).get("LEASE_TOPRIC")));
	   				  }
	   			  }
	    	   }catch(Exception e){
	        		LogPrint.getLogStackTrace(e, logger);
	     			e.printStackTrace();
	    			context.errList.add("数据查询失败!");
	    	   }
		      if(context.errList.isEmpty()){
		    	  outputMap.put("dw",dw);
		    	  outputMap.put("selectDate",context.contextMap.get("selectDate"));
			      outputMap.put("backDate", df.format(new Date()));
			      outputMap.put("lst",lst);
			      outputMap.put("dateList",dateList);
			      Output.jspOutput(outputMap, context, "/financial/VATOffsetAmountDetail.jsp");
	    	   }else{
	   			  outputMap.put("errList",context.errList) ;
				  Output.jspOutput(outputMap,context,"/error.jsp") ;
	    	   }
	       }
	       
	       //导出增值税抵减明细表
	       @SuppressWarnings("unchecked")
		public void exportVATOffsetAmount(Context context) throws Exception{
	    	   DecimalFormat nf = new DecimalFormat( "#,##0.00 ");
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   VATInvoice vi=new VATInvoice();
	    	   String t=null;
	    	   try{

	   				t=context.contextMap.get("exportDate").toString();
	   				int year=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			
	    		   lst=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.queryWithVATOffsetAmount", context.contextMap,DataAccessor.RS_TYPE.LIST);
		   			  if(lst!=null&&lst.size()>0){
		   				  for(int i=0;i<lst.size();i++){
		   					lst.get(i).put("LEASE_TOPRIC", nf.format(lst.get(i).get("LEASE_TOPRIC")));
		   					lst.get(i).put("taxType", "增值税普通发票");
		   					lst.get(i).put("projectName", "本金");
		   				  }
		   			  }
				   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				   context.response.setHeader("Content-Disposition"
						   ,"attachment;filename="+new String(("增值税抵减明细("+t+").xls").getBytes("GBK"),"ISO-8859-1"));

				    
				    ServletOutputStream out=context.response.getOutputStream();
				    context.contextMap.put("sheetName", "增值税抵减明细");
				    vi.exportVATOffsetAmountInvoice(context,lst).write(out);
				    out.flush();
				    out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //导出增值税抵减余额变动表
	       @SuppressWarnings("unchecked")
		public void exportVATBalance(Context context) throws Exception{
	    	   DecimalFormat nf = new DecimalFormat( "#,##0.00 ");
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   VATInvoice vi=new VATInvoice();
	    	   String t=null;
	    	   try{

	   				t=context.contextMap.get("exportDate").toString();
	   				int year=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("exportDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			
	    		   lst=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.queryWithVATOffsetAmount", context.contextMap,DataAccessor.RS_TYPE.LIST);
		   			  if(lst.size()>0){
		   				  for(int i=0;i<lst.size();i++){
		   					  double s=Double.parseDouble(lst.get(i).get("LEASE_TOPRIC").toString());
		   					  lst.get(i).put("LEASE_TOPRIC", nf.format(lst.get(i).get("LEASE_TOPRIC")));
		   					  double w=Double.parseDouble(lst.get(i).get("LAST_PRICE").toString());                       //未抵减的金额
		   					  lst.get(i).put("LAST_PRICE", nf.format(w));
		   					  double y=s-w;
		   					  lst.get(i).put("BALANCE", nf.format(y));
			   				  lst.get(i).put("taxType", "增值税普通发票");
			   				  lst.get(i).put("projectName", "本金");
		   				  }
		   			  }
				   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				   context.response.setHeader("Content-Disposition"
						   ,"attachment;filename="+new String(("增值税抵减余额("+t+").xls").getBytes("GBK"),"ISO-8859-1"));

				    
				    ServletOutputStream out=context.response.getOutputStream();
				    context.contextMap.put("sheetName", "增值税抵减余额");
				    vi.exportVATBalanceInvoice(context,lst).write(out);
				    out.flush();
				    out.close();
	    	   }catch(Exception e){
	    		   e.printStackTrace();
	    	   }
	       }
	       
	       //当月 拨款案件 
	       public void getRentDetail(Context context) throws Exception{
	    	   Map<String,Object> outputMap=new HashMap<String,Object>();
	    	   List<Map<String,Object>> dateList=null;
	    	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	   SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   DataWrap dw=null;
	    	   try{
	   			//获得财务结账周期
	    	   dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",context.contextMap,DataAccessor.RS_TYPE.LIST);
	   			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
	   				context.contextMap.put("startDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("endDate",dateList.get(0).get("ENDDATE"));
	   				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
	   			} else {
	   				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			}
	   			dw=(DataWrap)DataAccessor.query("priceReport.getRentDetail", context.contextMap, DataAccessor.RS_TYPE.PAGED);
	   			if(dw!=null){
	   				lst=(List<HashMap<String,Object>>)dw.rs;
	   				for(int i=0;i<lst.size();i++){
	   					lst.get(i).put("START_DATE", lst.get(i).get("START_DATE").toString().substring(0, 11));
	   					lst.get(i).put("PAY_DATE", lst.get(i).get("PAY_DATE").toString().substring(0, 11));
	   					   //设备和乘用车的租赁案件利息取未税利息 
	   					   if(Integer.parseInt(lst.get(i).get("TAX_PLAN_CODE").toString())==6||Integer.parseInt(lst.get(i).get("TAX_PLAN_CODE").toString())==7){
	   						  double d=Double.parseDouble(lst.get(i).get("REN_PRICE").toString());
	   						  lst.get(i).put("REN_PRICE", d/1.17);
	   					   }
	   				}
	   			}
	    	   }catch(Exception e){
	        		LogPrint.getLogStackTrace(e, logger);
	     			e.printStackTrace();
	    			context.errList.add("数据查询失败!");
	    	   }
	    	   if(context.errList.isEmpty()){
	    		    outputMap.put("dw", dw);
	    		    outputMap.put("resultList", lst);
		   			outputMap.put("companys1", LeaseUtil.getCompanys());
		   			outputMap.put("companyCode", context.contextMap.get("companyCode"));
		   			outputMap.put("selectDate",context.contextMap.get("selectDate"));
		   			outputMap.put("dateList",dateList);
		   			outputMap.put("backDate", df.format(new Date()));
		    	   Output.jspOutput(outputMap, context, "/financial/rentDetail.jsp");
	    	   }else{
		   			  outputMap.put("errList",context.errList) ;
					  Output.jspOutput(outputMap,context,"/error.jsp") ;
	    	   }

	       }
	       
	       //导出利息明细
	       public void exportRentDetail(Context context){
	    	   Map<String,Object> outputMap=new HashMap<String,Object>();
	    	   List<Map<String,Object>> dateList=null;
	    	   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	   SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
	    	   List<HashMap<String,Object>> lst=new ArrayList<HashMap<String,Object>>();
	    	   VATInvoice vi=new VATInvoice();
	    	   String sheetDate=null;
	    	   DataWrap dw=null;
	    	   try{
	   			//获得财务结账周期
	    	   dateList=(List<Map<String,Object>>)DataAccessor.query("priceReport.getDateList",context.contextMap,DataAccessor.RS_TYPE.LIST);
	   			if(StringUtils.isEmpty(context.contextMap.get("selectDate"))) {
	   				sheetDate=dateList.get(0).get("CODE").toString();
	   				context.contextMap.put("startDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("endDate",dateList.get(0).get("ENDDATE"));
	   				context.contextMap.put("financeStartDate",dateList.get(0).get("STARTDATE"));
	   				context.contextMap.put("financeEndDate",dateList.get(0).get("ENDDATE"));
	   			} else {
	   				int year=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[0]);
	   				int month=Integer.valueOf(context.contextMap.get("selectDate").toString().split("-")[1]);
	   				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
	   				sheetDate=year+"-"+month;
	   				context.contextMap.put("startDate",to.getBeginTime());	
	   				context.contextMap.put("endDate",to.getEndTime());
	   				context.contextMap.put("financeStartDate",to.getBeginTime());
	   				context.contextMap.put("financeEndDate",to.getEndTime());
	   			}
	   			List<HashMap<String,Object>> dataList=(List<HashMap<String,Object>>)DataAccessor.query("priceReport.exportDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
	   			   if(dataList.size()>0){
	   				   for(int k=0;k<dataList.size();k++){
	   					   dataList.get(k).put("START_DATE", dataList.get(k).get("START_DATE").toString().substring(0, 11));
	   					   dataList.get(k).put("PAY_DATE", dataList.get(k).get("PAY_DATE").toString().substring(0, 11));
	   					   //设备和乘用车的租赁案件利息取未税利息 
	   					   if(Integer.parseInt(dataList.get(k).get("PRODUCTION_TYPE").toString())==1||Integer.parseInt(dataList.get(k).get("PRODUCTION_TYPE").toString())==3){
	   						  double d=Double.parseDouble(dataList.get(k).get("REN_PRICE").toString());
	   						  dataList.get(k).put("REN_PRICE", d/1.17);
	   					   }
	   				   }
	   			   }
				   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				   context.response.setHeader("Content-Disposition"
						   ,"attachment;filename="+new String((sheetDate+"拨款案件利息明细.xls").getBytes("GBK"),"ISO-8859-1"));

				    
				    ServletOutputStream out=context.response.getOutputStream();
				    context.contextMap.put("sheetName", "当月拨款案件利息明细");
				    vi.exportRentDetail(context,dataList).write(out);
				    out.flush();
				    out.close();

	    	   }catch(Exception e){
	    		  e.printStackTrace();
	    	   }
	       }
	       
	       //获得利息详情
	       @SuppressWarnings("unchecked")
		public void showRentDetail(Context context){
	    	   Map<String,Object> outputMap=new HashMap<String,Object>();
	    	   List<HashMap<String,Object>> resultList=new ArrayList<HashMap<String,Object>>();
	    	   resultList=(List<HashMap<String,Object>>)this.baseService.queryForList("priceReport.showDetail",context.contextMap);
	    	   if(context.errList.isEmpty()){
	    		   //System.out.println(resultList);
	    		   outputMap.put("resultDetail", resultList);
	    		   Output.jspOutput(outputMap, context, "/financial/showDetail.jsp");
	    	   }else{
		   		   outputMap.put("errList",context.errList) ;
				   Output.jspOutput(outputMap,context,"/error.jsp") ;
	    	   }
	       }
}
