package com.brick.prePayStatic.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;
import com.brick.prePayStatic.to.prePayStaticForDeptTo;
import com.brick.prePayStatic.to.prePayTotalForDeptTo;
import com.brick.prePayStatic.util.ExcelUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.SimpleDateFormat;

public class PrePayStaticForDeptCommand extends BaseCommand {
	   
	    Log logger=LogFactory.getLog(PrePayStaticForDeptCommand.class);      
	
	   @SuppressWarnings("unchecked")
	public void query(Context context){
		   
		   Map<String,Object> outputMap=new HashMap<String,Object>();
		   Map<String,Object> params=new HashMap<String,Object>();
		   Map<String,Object> search=new HashMap<String,Object>();
		   List<HashMap<String,Object>> list2=new ArrayList<HashMap<String,Object>>();
		   
		   String year=(String)context.contextMap.get("YEAR");
		   String m=(String) context.contextMap.get("MONTH");
		   if(m==null){
			   outputMap.put("month",""); 
		   }else{
			   outputMap.put("month",context.contextMap.get("MONTH"));
		   }
		   if(year==null||"".equals(year)){
			   outputMap.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
		   }else{
			   outputMap.put("YEAR", context.contextMap.get("YEAR"));
		   }
		   
		   List<String> yearList=null;
		   List<prePayStaticForDeptTo> dataList=null;
		   List<prePayStaticForDeptTo> newDataList=null;
		   prePayTotalForDeptTo p=null; 
		   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		   String backDate=sdf.format(new Date());
		   Calendar cal0=Calendar.getInstance();
		   try {
			 list2=(List<HashMap<String,Object>>)DataAccessor
						.query("prePayStaticForSupplier.getFinancialDate", outputMap, DataAccessor.RS_TYPE.LIST);
			  for(int i=0;i<list2.size();i++){
				  if(new Date().before(sdf1.parse(list2.get(i).get("ENDTIME").toString()))
						  &&new Date().after(sdf1.parse(list2.get(i).get("BEGINTIME").toString()))){
					 params.put("s"+i, sdf1.format(new Date())); 
				  }else{
					  params.put("s"+i, list2.get(i).get("ENDTIME"));
				  }
				  params.put("begin"+(i+1), list2.get(i).get("BEGINTIME"));
				  params.put("end"+(i+1), list2.get(i).get("ENDTIME"));
				  Calendar cal=Calendar.getInstance();
				  cal.setTime(sdf1.parse(list2.get(i).get("ENDTIME").toString()));
				  cal.add(Calendar.MONTH, -1);
				  params.put("up"+(i+1), sdf1.format(cal.getTime()));
			  }
			 yearList=(List<String>)DataAccessor
					.query("prePayStaticForSupplier.getYearList", null, DataAccessor.RS_TYPE.LIST);
			 
			 params.put("YEAR", outputMap.get("YEAR"));
			// dataList=(List<prePayStaticForDeptTo>)DataAccessor
					// .query("prePayStaticForSupplier.getDataList", params, DataAccessor.RS_TYPE.LIST);
			 cal0.add(Calendar.DATE, -1);
			 Calendar cal2=Calendar.getInstance();
			 cal2.add(Calendar.DATE, -1);
			 search.put("search", sdf1.format(cal2.getTime()));
			 dataList=(List<prePayStaticForDeptTo>)this.baseService.queryForList("prePayStaticForSupplier.getAllData",search);
			 Map<String,Object> list=getScale(dataList);
			 newDataList=(List<prePayStaticForDeptTo>)list.get("dataList");
			 
				 for(int k=0;k<newDataList.size();k++){
					 newDataList.get(k).setDate1(params.get("s0").toString());
					 newDataList.get(k).setDate2(params.get("s1").toString());
					 newDataList.get(k).setDate3(params.get("s2").toString());
					 newDataList.get(k).setDate4(params.get("s3").toString());
					 newDataList.get(k).setDate5(params.get("s4").toString());
					 newDataList.get(k).setDate6(params.get("s5").toString());
					 newDataList.get(k).setDate7(params.get("s6").toString());
					 newDataList.get(k).setDate8(params.get("s7").toString());
					 newDataList.get(k).setDate9(params.get("s8").toString());
					 newDataList.get(k).setDate10(params.get("s9").toString());
					 newDataList.get(k).setDate11(params.get("s10").toString());
					 newDataList.get(k).setDate12(params.get("s11").toString());
				 }
			 
			 p=(prePayTotalForDeptTo)list.get("p");
			 
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			context.errList.add("数据查询失败!");
		}
		   
		   
		   if(context.errList.isEmpty()){
			   outputMap.put("YearList", yearList);
			   outputMap.put("dataList", newDataList);
			   outputMap.put("backDate",sdf1.format(cal0.getTime()));
			   outputMap.put("total", p);
			   Output.jspOutput(outputMap, context, "/prePayStatic/prePayStaticForDept.jsp");
		   }else{
			   outputMap.put("errList", context.errList);
			   Output.jspOutput(outputMap, context, "/error.jsp");
		   }
	   }
	   
	   
	   @SuppressWarnings("unchecked")
	public void exportExcel(Context context) throws Exception{
		   
		   Map<String,Object> params=new HashMap<String,Object>();
		   Map<String,Object> search=new HashMap<String,Object>();
		   ExcelUtil excu=new ExcelUtil();
		   SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		   List<HashMap<String,Object>> list2=new ArrayList<HashMap<String,Object>>();
		   String year=(String)context.contextMap.get("YEAR");
		   Calendar cal0=Calendar.getInstance();
		   List<prePayStaticForDeptTo> dataList=null;
		   if(year==null||"".equals(year)){
			   year=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			   params.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
		   }else{
			   params.put("YEAR", context.contextMap.get("YEAR"));
		   }
		   
		   
		   try {
				 list2=(List<HashMap<String,Object>>)DataAccessor
							.query("prePayStaticForSupplier.getFinancialDate", params, DataAccessor.RS_TYPE.LIST);
				  for(int i=0;i<list2.size();i++){
					  params.put("begin"+(i+1), list2.get(i).get("BEGINTIME"));
					  params.put("end"+(i+1), list2.get(i).get("ENDTIME"));
					  Calendar cal=Calendar.getInstance();
					  cal.setTime(sdf1.parse(list2.get(i).get("ENDTIME").toString()));
					  cal.add(Calendar.MONTH, -1);
					  params.put("up"+(i+1), sdf1.format(cal.getTime()));
				  }
			 //  List<prePayStaticForDeptTo> dataList=(List<prePayStaticForDeptTo>)DataAccessor
					 //  .query("prePayStaticForSupplier.getDataList", params, DataAccessor.RS_TYPE.LIST);
					 cal0.add(Calendar.DATE, -1);
					 search.put("search", new Date());
					 dataList=(List<prePayStaticForDeptTo>)this.baseService.queryForList("prePayStaticForSupplier.getAllData",search);
			   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
			   context.response.setHeader("Content-Disposition"
					   ,"attachment;filename="+new String((year+"年交机前拨款金额统计表.xls").getBytes("GBK"),"ISO-8859-1"));
			    
			   

			    
			    ServletOutputStream out=context.response.getOutputStream();
			    
			    context.contextMap.put("sheetName", "交机前拨款金额统计表");
			    
			    excu.generateReport(getScale(dataList), context).write(out);
			    
			    out.flush();
			    out.close();
			    
		} catch (IOException e) {
			e.printStackTrace();
		}
	   }
	   
	   //导出交机前未补回拨款案件
	   @SuppressWarnings("unchecked")
	public void exportLoseCase (Context context) throws Exception{
		   
		   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		   Map<String,Object> params=new HashMap<String,Object>();
		   ExcelUtil excu=new ExcelUtil();
		   List<HashMap<String,Object>> list2=new ArrayList<HashMap<String,Object>>();
		   String year=(String)context.contextMap.get("YEAR");
		   if(year==null||"".equals(year)){
			   year=String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			   params.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
		   }else{
			   params.put("YEAR", context.contextMap.get("YEAR"));
		   }
		   
		   
		   try {
			   params.put("daily", sdf.format(new Date()));
			   list2=(List<HashMap<String,Object>>)DataAccessor
						.query("prePayStaticForSupplier.getLoseCaseDaily", params, DataAccessor.RS_TYPE.LIST);

			   context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
			   context.response.setHeader("Content-Disposition"
					   ,"attachment;filename="+new String((sdf.format(new Date())+"交机前未补回拨款案件明细.xls").getBytes("GBK"),"ISO-8859-1"));
			    
			   

			    
			    ServletOutputStream out=context.response.getOutputStream();
			    
			    context.contextMap.put("sheetName", "交机前未补回案件明细");
			    
			    excu.create(list2, context).write(out);
			    
			    out.flush();
			    out.close();
			    
		} catch (IOException e) {
			e.printStackTrace();
		}
	   }
	   
	   public Map<String,Object> getScale(List<prePayStaticForDeptTo> dataList){
		   Map<String,Object> map=new HashMap<String,Object>();
		   
		    double sumAct1=0;              //1月总金额
		    double payed1=0;               //1月交机前总金额
		    int s1=0;                      //1月拨款总件数
		    int p1=0;                      //1月交机前拨款总件数
		    int x1=0;                      //1月交机前拨款待补文件总数
		    double y1=0;                   //1月交机前拨款待补金额总数
		    double sumAct2=0;
		    double payed2=0;
		    int s2=0;
		    int p2=0;
		    int x2=0;
		    double y2=0;
		    double sumAct3=0;
		    double payed3=0;
		    int s3=0;
		    int p3=0;
		    int x3=0;
		    double y3=0;
		    double sumAct4=0;
		    double payed4=0;
		    int s4=0;
		    int p4=0;
		    int x4=0;
		    double y4=0;
		    double sumAct5=0;
		    double payed5=0;
		    int s5=0;
		    int p5=0;
		    int x5=0;
		    double y5=0;
		    double sumAct6=0;
		    double payed6=0;
		    int s6=0;
		    int p6=0;
		    int x6=0;
		    double y6=0;
		    double sumAct7=0;
		    double payed7=0;
		    int s7=0;
		    int p7=0;
		    int x7=0;
		    double y7=0;
		    double sumAct8=0;
		    double payed8=0;
		    int s8=0;
		    int p8=0;
		    int x8=0;
		    double y8=0;
		    double sumAct9=0;
		    double payed9=0;
		    int s9=0;
		    int p9=0;
		    int x9=0;
		    double y9=0;
		    double sumAct10=0;
		    double payed10=0;
		    int s10=0;
		    int p10=0;
		    int x10=0;
		    double y10=0;
		    double sumAct11=0;
		    double payed11=0;
		    int s11=0;
		    int p11=0;
		    int x11=0;
		    double y11=0;
		    double sumAct12=0;
		    double payed12=0;
		    int s12=0;
		    int p12=0;
		    int x12=0;
		    double y12=0;
		    prePayTotalForDeptTo p=new prePayTotalForDeptTo();
		    
		     //各个月的总和
		    BigDecimal a=null;            //交机前拨款金额
		    BigDecimal b=null;            //实际
		    for(int i=0;i<dataList.size();i++){
		    	if(dataList.get(i).getActureOfJan()==0){
		    		dataList.get(i).setScaleOfJan("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfJan());
		    		b=new BigDecimal(dataList.get(i).getActureOfJan());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfJan(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfFeb()==0){
		    		dataList.get(i).setScaleOfFeb("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfFeb());
		    		b=new BigDecimal(dataList.get(i).getActureOfFeb());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfFeb(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfMar()==0){
		    		dataList.get(i).setScaleOfMar("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfMar());
		    		b=new BigDecimal(dataList.get(i).getActureOfMar());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfMar(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfApr()==0){
		    		dataList.get(i).setScaleOfApr("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfApr());
		    		b=new BigDecimal(dataList.get(i).getActureOfApr());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfApr(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfMay()==0){
		    		dataList.get(i).setScaleOfMay("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfMay());
		    		b=new BigDecimal(dataList.get(i).getActureOfMay());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfMay(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfJun()==0){
		    		dataList.get(i).setScaleOfJun("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfJun());
		    		b=new BigDecimal(dataList.get(i).getActureOfJun());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfJun(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfJul()==0){
		    		dataList.get(i).setScaleOfJul("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfJul());
		    		b=new BigDecimal(dataList.get(i).getActureOfJul());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfJul(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfAus()==0){
		    		dataList.get(i).setScaleOfAus("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfAus());
		    		b=new BigDecimal(dataList.get(i).getActureOfAus());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfAus(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfSep()==0){
		    		dataList.get(i).setScaleOfSep("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfSep());
		    		b=new BigDecimal(dataList.get(i).getActureOfSep());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfSep(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfOct()==0){
		    		dataList.get(i).setScaleOfOct("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfOct());
		    		b=new BigDecimal(dataList.get(i).getActureOfOct());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfOct(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfNov()==0){
		    		dataList.get(i).setScaleOfNov("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfNov());
		    		b=new BigDecimal(dataList.get(i).getActureOfNov());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfNov(re+"%");
		    	}
		    	if(dataList.get(i).getActureOfDec()==0){
		    		dataList.get(i).setScaleOfDec("0%");
		    	}else{
		    		a=new BigDecimal(dataList.get(i).getPrePayOfDec());
		    		b=new BigDecimal(dataList.get(i).getActureOfDec());
		    		BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		    		dataList.get(i).setScaleOfDec(re+"%");
		    	}
		    	s1+=dataList.get(i).getActrueNumOfJan();
		    	p1+=dataList.get(i).getPrePayNumOfJan();
		    	x1+=dataList.get(i).getCount1();
		    	y1+=dataList.get(i).getRemanentMoney1();
		    	sumAct1+=dataList.get(i).getActureOfJan();
		    	payed1+=dataList.get(i).getPrePayOfJan();
		    	s2+=dataList.get(i).getActrueNumOfFeb();
		    	p2+=dataList.get(i).getPrePayNumOfFeb();
		    	x2+=dataList.get(i).getCount2();
		    	y2+=dataList.get(i).getRemanentMoney2();
		    	sumAct2+=dataList.get(i).getActureOfFeb();
		    	payed2+=dataList.get(i).getPrePayOfFeb();
		    	s3+=dataList.get(i).getActrueNumOfMar();
		    	p3+=dataList.get(i).getPrePayNumOfMar();
		    	x3+=dataList.get(i).getCount3();
		    	y3+=dataList.get(i).getRemanentMoney3();
		    	sumAct3+=dataList.get(i).getActureOfMar();
		    	payed3+=dataList.get(i).getPrePayOfMar();
		    	s4+=dataList.get(i).getActrueNumOfApr();
		    	p4+=dataList.get(i).getPrePayNumOfApr();
		    	x4+=dataList.get(i).getCount4();
		    	y4+=dataList.get(i).getRemanentMoney4();
		    	sumAct4+=dataList.get(i).getActureOfApr();
		    	payed4+=dataList.get(i).getPrePayOfApr();
		    	s5+=dataList.get(i).getActrueNumOfMay();
		    	p5+=dataList.get(i).getPrePayNumOfMay();
		    	x5+=dataList.get(i).getCount5();
		    	y5+=dataList.get(i).getRemanentMoney5();
		    	sumAct5+=dataList.get(i).getActureOfMay();
		    	payed5+=dataList.get(i).getPrePayOfMay();
		    	s6+=dataList.get(i).getActrueNumOfJun();
		    	p6+=dataList.get(i).getPrePayNumOfJun();
		    	x6+=dataList.get(i).getCount6();
		    	y6+=dataList.get(i).getRemanentMoney6();
		    	sumAct6+=dataList.get(i).getActureOfJun();
		    	payed6+=dataList.get(i).getPrePayOfJun();
		    	s7+=dataList.get(i).getActrueNumOfJul();
		    	p7+=dataList.get(i).getPrePayNumOfJul();
		    	x7+=dataList.get(i).getCount7();
		    	y7+=dataList.get(i).getRemanentMoney7();
		    	sumAct7+=dataList.get(i).getActureOfJul();
		    	payed7+=dataList.get(i).getPrePayOfJul();
		    	s8+=dataList.get(i).getActrueNumOfAus();
		    	p8+=dataList.get(i).getPrePayNumOfAus();
		    	x8+=dataList.get(i).getCount8();
		    	y8+=dataList.get(i).getRemanentMoney8();
		    	sumAct8+=dataList.get(i).getActureOfAus();
		    	payed8+=dataList.get(i).getPrePayOfAus();
		    	s9+=dataList.get(i).getActrueNumOfSep();
		    	p9+=dataList.get(i).getPrePayNumOfSep();
		    	x9+=dataList.get(i).getCount9();
		    	y9+=dataList.get(i).getRemanentMoney9();
		    	sumAct9+=dataList.get(i).getActureOfSep();
		    	payed9+=dataList.get(i).getPrePayOfSep();
		    	s10+=dataList.get(i).getActrueNumOfOct();
		    	p10+=dataList.get(i).getPrePayNumOfOct();
		    	x10+=dataList.get(i).getCount10();
		    	y10+=dataList.get(i).getRemanentMoney10();
		    	sumAct10+=dataList.get(i).getActureOfOct();
		    	payed10+=dataList.get(i).getPrePayOfOct();
		    	s11+=dataList.get(i).getActrueNumOfNov();
		    	p11+=dataList.get(i).getPrePayNumOfNov();
		    	x11+=dataList.get(i).getCount11();
		    	y11+=dataList.get(i).getRemanentMoney11();
		    	sumAct11+=dataList.get(i).getActureOfNov();
		    	payed11+=dataList.get(i).getPrePayOfNov();
		    	s12+=dataList.get(i).getActrueNumOfDec();
		    	p12+=dataList.get(i).getPrePayNumOfDec();
		    	x12+=dataList.get(i).getCount12();
		    	y12+=dataList.get(i).getRemanentMoney12();
		    	sumAct12+=dataList.get(i).getActureOfDec();
		    	payed12+=dataList.get(i).getPrePayOfDec();
		    }
		    p.setActureSumOfJan(sumAct1);
		    p.setPrePaySumOfJan(payed1);
		    p.setActOfJan(s1);
		    p.setPreOfJan(p1);
		    p.setPreOfAddFileCount1(x1);
		    p.setPreOfAddFileMoney1(y1);
		    if(sumAct1==0){
		    	p.setScaleSumOfJan("0%");
		    }else{
		    	p.setScaleSumOfJan(new BigDecimal(payed1).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct1),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfFeb(sumAct2);
		    p.setPrePaySumOfFeb(payed2);
		    p.setActOfFeb(s2);
		    p.setPreOfFeb(p2);
		    p.setPreOfAddFileCount2(x2);
		    p.setPreOfAddFileMoney2(y2);
		    if(sumAct2==0){
		    	p.setScaleSumOfFeb("0%");
		    }else{
		    	p.setScaleSumOfFeb(new BigDecimal(payed2).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct2),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfMar(sumAct3);
		    p.setPrePaySumOfMar(payed3);
		    p.setActOfMar(s3);
		    p.setPreOfMar(p3);
		    p.setPreOfAddFileCount3(x3);
		    p.setPreOfAddFileMoney3(y3);
		    if(sumAct3==0){
		    	p.setScaleSumOfMar("0%");
		    }else{
		    	p.setScaleSumOfMar(new BigDecimal(payed3).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct3),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfApr(sumAct4);
		    p.setPrePaySumOfApr(payed4);
		    p.setActOfApr(s4);
		    p.setPreOfApr(p4);
		    p.setPreOfAddFileCount4(x4);
		    p.setPreOfAddFileMoney4(y4);
		    if(sumAct4==0){
		    	p.setScaleSumOfApr("0%");
		    }else{
		    	p.setScaleSumOfApr(new BigDecimal(payed4).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct4),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfMay(sumAct5);
		    p.setPrePaySumOfMay(payed5);
		    p.setActOfMay(s5);
		    p.setPreOfMay(p5);
		    p.setPreOfAddFileCount5(x5);
		    p.setPreOfAddFileMoney5(y5);
		    if(sumAct5==0){
		    	p.setScaleSumOfMay("0%");
		    }else{
		    	p.setScaleSumOfMay(new BigDecimal(payed5).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct5),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfJun(sumAct6);
		    p.setPrePaySumOfJun(payed6);
		    p.setActOfJun(s6);
		    p.setPreOfJun(p6);
		    p.setPreOfAddFileCount6(x6);
		    p.setPreOfAddFileMoney6(y6);
		    if(sumAct6==0){
		    	p.setScaleSumOfJun("0%");
		    }else{
		    	p.setScaleSumOfJun(new BigDecimal(payed6).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct6),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfJul(sumAct7);
		    p.setPrePaySumOfJul(payed7);
		    p.setActOfJul(s7);
		    p.setPreOfJul(p7);
		    p.setPreOfAddFileCount7(x7);
		    p.setPreOfAddFileMoney7(y7);
		    if(sumAct7==0){
		    	p.setScaleSumOfJul("0%");
		    }else{
		    	p.setScaleSumOfJul(new BigDecimal(payed7).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct7),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfAus(sumAct8);
		    p.setPrePaySumOfAus(payed8);
		    p.setActOfAus(s8);
		    p.setPreOfAus(p8);
		    p.setPreOfAddFileCount8(x8);
		    p.setPreOfAddFileMoney8(y8);
		    if(sumAct8==0){
		    	p.setScaleSumOfAus("0%");
		    }else{
		    	p.setScaleSumOfAus(new BigDecimal(payed8).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct8),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfSep(sumAct9);
		    p.setPrePaySumOfSep(payed9);
		    p.setActOfSep(s9);
		    p.setPreOfSep(p9);
		    p.setPreOfAddFileCount9(x9);
		    p.setPreOfAddFileMoney9(y9);
		    if(sumAct9==0){
		    	p.setScaleSumOfSep("0%");
		    }else{
		    	p.setScaleSumOfSep(new BigDecimal(payed9).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct9),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfOct(sumAct10);
		    p.setPrePaySumOfOct(payed10);
		    p.setActOfOct(s10);
		    p.setPreOfOct(p10);
		    p.setPreOfAddFileCount10(x10);
		    p.setPreOfAddFileMoney10(y10);
		    if(sumAct10==0){
		    	p.setScaleSumOfOct("0%");
		    }else{
		    	p.setScaleSumOfOct(new BigDecimal(payed10).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct10),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfNov(sumAct11);
		    p.setPrePaySumOfNov(payed11);
		    p.setActOfNov(s11);
		    p.setPreOfNov(p11);
		    p.setPreOfAddFileCount11(x11);
		    p.setPreOfAddFileMoney11(y11);
		    if(sumAct11==0){
		    	p.setScaleSumOfNov("0%");
		    }else{
		    	p.setScaleSumOfNov(new BigDecimal(payed11).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct11),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    
		    p.setActureSumOfDec(sumAct12);
		    p.setPrePaySumOfDec(payed12);
		    p.setActOfDec(s12);
		    p.setPreOfDec(p12);
		    p.setPreOfAddFileCount12(x12);
		    p.setPreOfAddFileMoney12(y12);
		    if(sumAct12==0){
		    	p.setScaleSumOfDec("0%");
		    }else{
		    	p.setScaleSumOfDec(new BigDecimal(payed12).multiply(new BigDecimal(100)).divide(new BigDecimal(sumAct12),0,BigDecimal.ROUND_HALF_UP)+"%");
		    }
		    map.put("dataList", dataList);
		    map.put("p", p);
		    return map;
	   }
	   
	   /*
	    * 
	    * 每天23:00跑job
	    * 记录交机前拨款件数与比例最新统计
	    */
	   @Transactional(rollbackFor=Exception.class)
	   public void addLog() throws Exception{
		   
		   Map<String,Object> params=new HashMap<String,Object>();
		   Map<String,Object> p=new HashMap<String,Object>();
		   SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		  int year=Calendar.getInstance().get(Calendar.YEAR);
		   p.put("YEAR", year);
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> list2=(List<HashMap<String,Object>>)this.baseService
						.queryForList("prePayStaticForSupplier.getFinancialDate", p);
		      params.put("YEAR", year);
			  for(int i=0;i<list2.size();i++){
				  if(new Date().before(sdf1.parse(list2.get(i).get("ENDTIME").toString()))
						  &&new Date().after(sdf1.parse(list2.get(i).get("BEGINTIME").toString()))){
					 params.put("s"+(i+1), sdf1.format(new Date())); 
				  }else{
					  params.put("s"+(i+1), list2.get(i).get("ENDTIME"));
				  }
				  params.put("begin"+(i+1), list2.get(i).get("BEGINTIME"));
				  params.put("end"+(i+1), list2.get(i).get("ENDTIME"));
				 // Calendar cal=Calendar.getInstance();
				 // cal.setTime(sdf1.parse(list2.get(i).get("ENDTIME").toString()));
				 // cal.add(Calendar.MONTH, -1);
				//  params.put("up"+(i+1), sdf1.format(cal.getTime()));
			  }
			  @SuppressWarnings("unchecked")
			List<prePayStaticForDeptTo> dataList=(List<prePayStaticForDeptTo>)this.baseService
					  .queryForList("prePayStaticForSupplier.getDataList", params);
			  for(int k=0;k<dataList.size();k++){
				  String now=UUID.randomUUID().toString();
				  dataList.get(k).setId(now);
				  this.baseService.insert("prePayStaticForSupplier.addRecordOfPrepay",dataList.get(k));
			  }
	   }
	   //test  %格式化
/*	   public static void main(String[] args){
		   BigDecimal a=new BigDecimal(465.6);
		   BigDecimal b=new BigDecimal(3189);
		   BigDecimal re=a.multiply(new BigDecimal(100)).divide(b,0,BigDecimal.ROUND_HALF_UP);
		   System.out.print("re="+re);
	   }*/
}
