package com.brick.information.command;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.information.service.InformationService;
import com.brick.information.to.InforDataOfWeek;
import com.brick.information.to.InformationStatistic;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.ibm.icu.math.BigDecimal;

public class InformationCommand extends BaseCommand {
         
      	Log logger=LogFactory.getLog(InformationCommand.class);
			
	     private InformationService informationService;
	
	
	     public InformationService getInformationService() {
			return informationService;
		}


		public void setInformationService(InformationService informationService) {
			this.informationService = informationService;
		}


		public void query(Context context){
	    	 
	    	 Map<String,Object> outputMap=new HashMap<String,Object>();
             
	    	 Map<String,Object> params=new HashMap<String,Object>();
	    	 
	    	 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//保存页面的查询条件
				if("".equals(context.contextMap.get("YEAR"))||context.contextMap.get("YEAR")==null) {
					//获得系统年份,初始化进入时候拿系统年份
					outputMap.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
				} else {
					outputMap.put("YEAR",context.contextMap.get("YEAR"));
				}
				
				if("".equals(context.contextMap.get("TYPE"))||context.contextMap.get("TYPE")==null){
					outputMap.put("TYPE", "");
				}else{
					outputMap.put("TYPE",context.contextMap.get("TYPE") );
				}
				
				
                params.put("TYPE", outputMap.get("TYPE"));
				params.put("YEAR", outputMap.get("YEAR"));
				
				List<InformationStatistic> DataList=this.getInformationService()
						.getInfoStaList(context, params);
				
				//格式化
		    //	NumberFormat numberFormat = NumberFormat.getInstance();
		    //	numberFormat.setMinimumFractionDigits(2);
		    
				for(InformationStatistic f:DataList){
					BigDecimal result=null;
					int sum1=f.getPreForJan()+f.getPreForFeb()+f.getPreForMar()                //12个月分配到的总和
							+f.getPreForApi()+f.getPreForMay()+f.getPreForJun()            
							+f.getPreForJul()+f.getPreForAus()+f.getPreForSep()
							+f.getPreForOct()+f.getPreForNov()+f.getPreForDec();
					
					int sum2=f.getFisForJan()+f.getFisForFeb()+f.getFisForMar()               //12个月完成数的总和
							+f.getFisForApi()+f.getFisForMay()+f.getFisForJun()
							+f.getFisForJul()+f.getFisForAus()+f.getFisForSep()
							+f.getFisForOct()+f.getFisForNov()+f.getFisForDec();
					f.setPreTotal(sum1);
					f.setFisTotal(sum2);
					BigDecimal preTotal=new BigDecimal(sum1);
					BigDecimal finTotal=new BigDecimal(sum2);
					result=finTotal.multiply(new BigDecimal(100)).divide(preTotal,4,BigDecimal.ROUND_HALF_UP);
			//		result=numberFormat.format((float)sum2/(float)sum1*100.00)+"%";
					f.setFinishForScale(result);
				}
				
				//获取表中存在的年份
				List<String> yearList=this.getInformationService().getAllYear(context);
				
				if(context.errList.isEmpty()){
					outputMap.put("backDate", df.format(new Date()));
					outputMap.put("yearList", yearList);
					outputMap.put("DataList", DataList);
					if(context.contextMap.get("TYPE")==null||"".equals(context.contextMap.get("TYPE"))){
						outputMap.put("TYPE", 2);
					}else{
						outputMap.put("TYPE", context.contextMap.get("TYPE"));
					}
					Output.jspOutput(outputMap, context, "/information/informationStatistics.jsp");
				}else{
					outputMap.put("errList",context.errList);
					Output.jspOutput(outputMap,context,"/error.jsp");
				}
				
	    	 
	     }
	     
	     /*
	      * 统计每周资讯单
	      */
	     public void getStaticticsOfWeek(Context context){
	    	 
	    	 Map<String,Object> outputMap=new HashMap<String,Object>();
	    	 List<InforDataOfWeek> list=this.getInformationService().getDataOfWeek(context);
	    	 SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	    	 for(int i=0;i<list.size();i++){
	    		 list.get(i).setTotalOfUnFinish(list.get(i).getTotal()-list.get(i).getTotalOfFinish());
	    		 list.get(i).setBackDate(df.format(list.get(i).getStaticDate()));
	    		 BigDecimal t1=new BigDecimal(list.get(i).getTotal());
	    		 BigDecimal t2=new BigDecimal(list.get(i).getTotalOfFinish());
	    		 //设置总完成比例
	    		 if(t1.intValue()==0){
	    			 list.get(i).setTotalOfScale(new BigDecimal(0.0)); 
	    		 }else{
	    		 list.get(i).setTotalOfScale(t2.multiply(new BigDecimal(100)).divide(t1,4,BigDecimal.ROUND_HALF_UP));
	    		 }
	    		 
	    		 BigDecimal t3=new BigDecimal(list.get(i).getAddNumOfWeek_fis());
	    		 BigDecimal t4=new BigDecimal(list.get(i).getAddNumOfWeek());
	    		 //设置本期完成比
	    		 if(t4.intValue()==0){
	    			 list.get(i).setWeekOfScale(new BigDecimal(0.0));
	    		 }else{
	    		 list.get(i).setWeekOfScale(t3.multiply(new BigDecimal(100)).divide(t4,4,BigDecimal.ROUND_HALF_UP));
	    		 }
	    	 }
				if(context.errList.isEmpty()){
					outputMap.put("backDate", getLastFriday());
					outputMap.put("DataList", list);
					Output.jspOutput(outputMap, context, "/information/informationStatisticsOfWeek.jsp");
				}else{
					outputMap.put("errList",context.errList);
					Output.jspOutput(outputMap,context,"/error.jsp");
				}
	    	 
	     }
	     
	     /*
	      * 得到上一周5的日期
	      */
	     public String getLastFriday(){
	         // 设置当前日期
	         Calendar aCalendar = Calendar.getInstance();
	         aCalendar.setTime(new Date());
	         // 取当前日期是星期几(week:星期几)
	         int week = aCalendar.get(Calendar.DAY_OF_WEEK);
	         if(week==7){
	            week=-1;	
	         }else if(week==1){
	         	week=-2;
	         }else if(week==2){
	         	week=-3;
	         }else if(week==3){
	         	week=-4;
	         }else if(week==4){
	         	week=-5;
	         }else if(week==5){
	         	week=-6;
	         }else{
	         	week=-7;
	         }

	         DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	         Calendar c = Calendar.getInstance();
	         c.add(Calendar.DAY_OF_MONTH, week);
	         return df.format(c.getTime());
	     }
	     
	     
	     //test
/*	     public static void main(String[] args){
	    	 int num1 = 1;
	           int num2 = 3;
	    	NumberFormat numberFormat = NumberFormat.getInstance();
	    	numberFormat.setMaximumFractionDigits(2);
	    	String result = numberFormat.format((float)num1/(float)num2*100);
	    	System.out.println("num1和num2的百分比为:" + result + "%");
	     }*/
}
