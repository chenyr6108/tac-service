package com.brick.report.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.moneyRate.service.MoneyRateService;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;

/**
 * wuzd
 * 11-1-14
 */
public class DifferentReportService extends AService {
	Log logger = LogFactory.getLog(DifferentReportService.class);
	/**
	 * 成本差异 汇总
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void differentReportAll(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		List list = new ArrayList();
		List nianList=new ArrayList();
		List yueList=new ArrayList();
		List ren_priceList=new ArrayList();
		List caiwuList=new ArrayList();
		List shuiwuList=new ArrayList();
		List chayiList=new ArrayList();
		List yingyeList=new ArrayList();
		List chengjianList=new ArrayList();
		List jiaoyuList=new ArrayList();
		List insure_priceList=new ArrayList();
		List yinhuaList=new ArrayList();
		List xiaojiList=new ArrayList();
		List cliList=new ArrayList();
		List sliList=new ArrayList();
		List licList=new ArrayList();	
		//添加小计
		Double ren_priceSubtotal = 0.0 ;
		Double caiwuSubtotal = 0.0 ;
		Double shuiwuSubtotal = 0.0 ;
		Double chayiSubtotal = 0.0 ;
		Double yingyeSubtotal = 0.0 ;
		Double chengjianSubtotal = 0.0 ;
		Double jiaoyuSubtotal = 0.0 ;
		Double insure_priceSubtotal=0.0;
		Double yinhuaSubtotal=0.0;
		Double xiaojiSubtotal=0.0;
		Double cliSubtotal=0.0;
		Double sliSubtotal=0.0;
		Double licSubtotal=0.0;	
		//设备总金额
		Double shebeiTotal = 0.0 ;
		int t=0;//每一年当中有几个月份
		Map nianCountMap=new HashMap();
		List nianCountList=new ArrayList();
		//成本差异增加时间范围
		//Modify by Michael 2012 01/13 变更利息计算方式
		//String startDate = (String) context.contextMap.get("startDate") ;

		if(context.contextMap.get("startDate") == null){
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
			context.contextMap.put("startDate", sf.format(new Date())) ;
		}else {
			context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
		}
	 	
		
		String endDate = (String) context.contextMap.get("endDate") ;
		if (errorList.isEmpty()) {
			try {
				//成本差异增加时间范围
//				if(startDate != null && startDate.length() > 0){
//					String[] start = startDate.split("-") ;
//					context.contextMap.put("startNian", start[0]) ;
//					context.contextMap.put("startYue", start[1]) ;
//				}
//				if(endDate != null && endDate.length() > 0){
//					String[] end = endDate.split("-") ;
//					context.contextMap.put("endNian", end[0]) ;
//					context.contextMap.put("endYue", end[1]) ;
//				}
				//成本差异增加时间范围 结束	
				//Modify by Michael 2012 01/13 变更利息计算方式
				outputMap.put("startDate", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("startDate").toString())) ;
				
				MoneyRateService.queryMoneyRate(context) ;
				list = (List) DataAccessor.query("differentReport.differentReportAll",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//设备总金额
				shebeiTotal = (Double) DataAccessor.query("differentReport.differentReportSheBei", context.contextMap ,DataAccessor.RS_TYPE.OBJECT) ;
				String n="";
				
				if (list!=null && list.size() > 0) {
					n=((Map)list.get(0)).get("NIAN").toString();
					for (int i = 0; i < list.size(); i++) {
						//赋值	
						Map map=(Map)list.get(i);

							Map nianMap=new HashMap();
							Map yueMap=new HashMap();
							Map ren_priceMap=new HashMap();
							Map caiwuMap=new HashMap();
							Map shuiwuMap=new HashMap();
							Map chayi=new HashMap();
							Map yingyeMap=new HashMap();
							Map chengjianMap=new HashMap();
							Map jiaoyuMap=new HashMap();
							Map insure_priceMap=new HashMap();
							Map yinhuaMap=new HashMap();
							Map xiaojiMap=new HashMap();
							Map cliMap=new HashMap();
							Map sliMap=new HashMap();
							Map licMap=new HashMap();
							
							if(n==map.get("NIAN").toString()){
								t++;
							}else{	
								nianCountMap.put(n,t);
								nianCountList.add(nianCountMap);
								n=map.get("NIAN").toString();
								t=0;
							}
							nianMap.put("NIAN", map.get("NIAN"));
							yueMap.put("YUE", map.get("YUE"));
							ren_priceMap.put("REN_PRICE", map.get("REN_PRICE"));
							caiwuMap.put("CAIWU", map.get("CAIWU"));
							shuiwuMap.put("SHUIWU", map.get("SHUIWU"));
							chayi.put("CHAYI", map.get("CHAYI"));
							yingyeMap.put("YINGYE", map.get("YINGYE"));
							chengjianMap.put("CHENGJIAN", map.get("CHENGJIAN"));
							jiaoyuMap.put("JIAOYU", map.get("JIAOYU"));
							insure_priceMap.put("INSURE_PRICE", map.get("INSURE_PRICE"));
							yinhuaMap.put("YINHUA", map.get("YINHUA"));
							xiaojiMap.put("XIAOJI", map.get("XIAOJI"));
							cliMap.put("CLI", map.get("CLI"));
							sliMap.put("SLI", map.get("SLI"));
							licMap.put("LIC", map.get("LIC"));	
							
							nianList.add(nianMap);
							yueList.add(yueMap);
							ren_priceList.add(ren_priceMap);
							caiwuList.add(caiwuMap);
							shuiwuList.add(shuiwuMap);
							chayiList.add(chayi);
							yingyeList.add(yingyeMap);
							chengjianList.add(chengjianMap);
							jiaoyuList.add(jiaoyuMap);
							insure_priceList.add(insure_priceMap);
							yinhuaList.add(yinhuaMap);
							xiaojiList.add(xiaojiMap);
							cliList.add(cliMap);
							sliList.add(sliMap);
							licList.add(licMap);
							//添加小计
							if(map.get("REN_PRICE") != null){
								ren_priceSubtotal += (Double)map.get("REN_PRICE") ;
							}
							if(map.get("CAIWU") != null){
								caiwuSubtotal += (Double)map.get("CAIWU") ;
							}
							if(map.get("SHUIWU") != null){
								shuiwuSubtotal += (Double)map.get("SHUIWU") ;
							}
							if(map.get("CHAYI") != null){
								chayiSubtotal += (Double)map.get("CHAYI") ;
							}
							if(map.get("YINGYE") != null){
								yingyeSubtotal += (Double)map.get("YINGYE") ;
							}
							if(map.get("CHENGJIAN") != null){
								chengjianSubtotal += (Double)map.get("CHENGJIAN") ;
							}
							if(map.get("JIAOYU") != null){
								jiaoyuSubtotal += (Double)map.get("JIAOYU") ;
							}
							if(map.get("INSURE_PRICE") != null){
								insure_priceSubtotal += (Double)map.get("INSURE_PRICE") ;
							}
							if(map.get("YINHUA") != null){
								yinhuaSubtotal += (Double)map.get("YINHUA") ;
							}
							if(map.get("XIAOJI") != null){
								xiaojiSubtotal += (Double)map.get("XIAOJI") ;
							}
							if(map.get("CLI") != null){
								cliSubtotal += (Double)map.get("CLI") ;
							}
							if(map.get("SLI") != null){
								sliSubtotal += (Double)map.get("SLI") ;
							}
							if(map.get("LIC") != null){
								licSubtotal += (Double)map.get("LIC") ;
							}
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("nianList", nianList);
		outputMap.put("yueList", yueList);
		outputMap.put("ren_priceList", ren_priceList);
		outputMap.put("caiwuList", caiwuList);
		outputMap.put("shuiwuList", shuiwuList);
		outputMap.put("chayiList", chayiList);
		outputMap.put("yingyeList", yingyeList);
		outputMap.put("chengjianList", chengjianList);
		outputMap.put("jiaoyuList", jiaoyuList);
		outputMap.put("insure_priceList", insure_priceList);
		outputMap.put("yinhuaList", yinhuaList);
		outputMap.put("xiaojiList", xiaojiList);
		outputMap.put("cliList", cliList);
		outputMap.put("sliList", sliList);
		outputMap.put("licList", licList);	
		//传回开始与结束时间
//		outputMap.put("startDate", startDate) ;
//		outputMap.put("endDate", endDate) ;
		//传给页面小计
		outputMap.put("ren_priceSubtotal", ren_priceSubtotal) ;
		outputMap.put("caiwuSubtotal", caiwuSubtotal) ;
		outputMap.put("shuiwuSubtotal",shuiwuSubtotal ) ;
		outputMap.put("chayiSubtotal", chayiSubtotal) ;
		outputMap.put("yingyeSubtotal", yingyeSubtotal) ;
		outputMap.put("chengjianSubtotal", chengjianSubtotal) ;
		outputMap.put("jiaoyuSubtotal", jiaoyuSubtotal) ;
		outputMap.put("insure_priceSubtotal", insure_priceSubtotal) ;
		outputMap.put("yinhuaSubtotal", yinhuaSubtotal) ;
		outputMap.put("xiaojiSubtotal", xiaojiSubtotal) ;	
		outputMap.put("cliSubtotal", cliSubtotal) ;
		outputMap.put("sliSubtotal", sliSubtotal) ;
		outputMap.put("licSubtotal", licSubtotal) ;	
		//传给页面设备总金额
		outputMap.put("shebeiTotal",shebeiTotal) ;
		Output.jspOutput(outputMap, context,"/report/differentReport.jsp");
	}
	
	/**
	 * 风控报表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void riskReport(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		List list = new ArrayList();
		List resultList = new ArrayList();
		if (errorList.isEmpty()) {
			try {
				resultList = (List) DataAccessor.query("differentReport.riskReport",context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Iterator iterator = resultList.iterator(); iterator.hasNext();) {
					Map map = (Map) iterator.next();
					
					map.put("MAX_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("MAX_H"))));
					
					map.put("MIN_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("MIN_H"))));
					
					map.put("AVG_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("AVG_H"))));
					
					list.add(map);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		outputMap.put("list", list);	
		Output.jspOutput(outputMap, context,"/report/riskReport.jsp");
	}
	
	//导出评审统计Excel
	public void reportExcel(Context context)
	{
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		List list = new ArrayList();
		List resultList = new ArrayList();
		if (errorList.isEmpty()) {
			try {
				resultList = (List) DataAccessor.query("differentReport.riskReport",context.contextMap, DataAccessor.RS_TYPE.LIST);
				for (Iterator iterator = resultList.iterator(); iterator.hasNext();) {
					Map map = (Map) iterator.next();
					
					map.put("MAX_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("MAX_H"))));
					
					map.put("MIN_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("MIN_H"))));
					
					map.put("AVG_HCONTEXT", minToChinease(DataUtil.intUtil(map.get("AVG_H"))));
					
					list.add(map);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		ReportExcel exc=new ReportExcel();
		exc.reportExcelJoin(list, context);
	}
	
	public String minToChinease(int shijian) {
		if(shijian!=0){
			String ctext="";
			int dcontext=shijian/1440;
			ctext=ctext+String.valueOf(dcontext)+"天";
			shijian=shijian%1440;
			if(shijian!=0){
				int hcontext=shijian/60;
				ctext=ctext+String.valueOf(hcontext)+"时";
				shijian=shijian%60;
				if(shijian!=0){
					ctext=ctext+String.valueOf(shijian)+"分";
				}
			}
			return ctext;				
		}else{
			return "0分";
		}
	}	
	
	
	public void expDifferentToExcel(Context context) {
		List errorList = context.errList;
		List list = new ArrayList();
		List nianList=new ArrayList();
		List yueList=new ArrayList();
		List ren_priceList=new ArrayList();
		List caiwuList=new ArrayList();
		List shuiwuList=new ArrayList();
		List chayiList=new ArrayList();
		List yingyeList=new ArrayList();
		List chengjianList=new ArrayList();
		List jiaoyuList=new ArrayList();
		List insure_priceList=new ArrayList();
		List yinhuaList=new ArrayList();
		List xiaojiList=new ArrayList();
		List cliList=new ArrayList();
		List sliList=new ArrayList();
		List licList=new ArrayList();	
		int t=0;//每一年当中有几个月份
		Map nianCountMap=new HashMap();
		List nianCountList=new ArrayList();
		
		//添加小计
		Double ren_priceSubtotal = 0.0 ;
		Double caiwuSubtotal = 0.0 ;
		Double shuiwuSubtotal = 0.0 ;
		Double chayiSubtotal = 0.0 ;
		Double yingyeSubtotal = 0.0 ;
		Double chengjianSubtotal = 0.0 ;
		Double jiaoyuSubtotal = 0.0 ;
		Double insure_priceSubtotal=0.0;
		Double yinhuaSubtotal=0.0;
		Double xiaojiSubtotal=0.0;
		Double cliSubtotal=0.0;
		Double sliSubtotal=0.0;
		Double licSubtotal=0.0;	
		//设备总金额
		Double shebeiTotal = 0.0 ;
		Map exportMap = new HashMap();
		
//		String startDate = (String) context.contextMap.get("startDate") ;
//		String endDate = (String) context.contextMap.get("endDate") ;
		if (errorList.isEmpty()) {
			try {
				//成本差异增加时间范围
//				if(startDate != null && startDate.length() > 0){
//					String[] start = startDate.split("-") ;
//					context.contextMap.put("startNian", start[0]) ;
//					context.contextMap.put("startYue", start[1]) ;
//				}
//				if(endDate != null && endDate.length() > 0){
//					String[] end = endDate.split("-") ;
//					context.contextMap.put("endNian", end[0]) ;
//					context.contextMap.put("endYue", end[1]) ;
//				}
				
				//Modify by Michael 2012 01/13 变更利息计算方式
				//String startDate = (String) context.contextMap.get("startDate") ;

				if(context.contextMap.get("startDate") == null){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
					context.contextMap.put("startDate", sf.format(new Date())) ;
				}else {
					context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
				}
				//成本差异增加时间范围 结束
				MoneyRateService.queryMoneyRate(context) ;
				list = (List) DataAccessor.query("differentReport.differentReportAll",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//设备总金额
				shebeiTotal = (Double) DataAccessor.query("differentReport.differentReportSheBei", context.contextMap ,DataAccessor.RS_TYPE.OBJECT) ;
				String n="";
				if (list!=null && list.size() > 0) {
					n=((Map)list.get(0)).get("NIAN").toString();
					for (int i = 0; i < list.size(); i++) {
						//赋值	
						Map map=(Map)list.get(i);

							Map nianMap=new HashMap();
							Map yueMap=new HashMap();
							Map ren_priceMap=new HashMap();
							Map caiwuMap=new HashMap();
							Map shuiwuMap=new HashMap();
							Map chayi=new HashMap();
							Map yingyeMap=new HashMap();
							Map chengjianMap=new HashMap();
							Map jiaoyuMap=new HashMap();
							Map insure_priceMap=new HashMap();
							Map yinhuaMap=new HashMap();
							Map xiaojiMap=new HashMap();
							Map cliMap=new HashMap();
							Map sliMap=new HashMap();
							Map licMap=new HashMap();
							
							if(n==map.get("NIAN").toString()){
								t++;
							}else{	
								nianCountMap.put(n,t);
								nianCountList.add(nianCountMap);
								n=map.get("NIAN").toString();
								t=0;
							}
							nianMap.put("NIAN", map.get("NIAN"));
							yueMap.put("YUE", map.get("YUE"));
							ren_priceMap.put("REN_PRICE", map.get("REN_PRICE"));
							caiwuMap.put("CAIWU", map.get("CAIWU"));
							shuiwuMap.put("SHUIWU", map.get("SHUIWU"));
							chayi.put("CHAYI", map.get("CHAYI"));
							yingyeMap.put("YINGYE", map.get("YINGYE"));
							chengjianMap.put("CHENGJIAN", map.get("CHENGJIAN"));
							jiaoyuMap.put("JIAOYU", map.get("JIAOYU"));
							insure_priceMap.put("INSURE_PRICE", map.get("INSURE_PRICE"));
							yinhuaMap.put("YINHUA", map.get("YINHUA"));
							xiaojiMap.put("XIAOJI", map.get("XIAOJI"));
							cliMap.put("CLI", map.get("CLI"));
							sliMap.put("SLI", map.get("SLI"));
							licMap.put("LIC", map.get("LIC"));	
							
							nianList.add(nianMap);
							yueList.add(yueMap);
							ren_priceList.add(ren_priceMap);
							caiwuList.add(caiwuMap);
							shuiwuList.add(shuiwuMap);
							chayiList.add(chayi);
							yingyeList.add(yingyeMap);
							chengjianList.add(chengjianMap);
							jiaoyuList.add(jiaoyuMap);
							insure_priceList.add(insure_priceMap);
							yinhuaList.add(yinhuaMap);
							xiaojiList.add(xiaojiMap);
							cliList.add(cliMap);
							sliList.add(sliMap);
							licList.add(licMap);
							
							//添加小计
							if(map.get("REN_PRICE") != null){
								ren_priceSubtotal += (Double)map.get("REN_PRICE") ;
							}
							if(map.get("CAIWU") != null){
								caiwuSubtotal += (Double)map.get("CAIWU") ;
							}
							if(map.get("SHUIWU") != null){
								shuiwuSubtotal += (Double)map.get("SHUIWU") ;
							}
							if(map.get("CHAYI") != null){
								chayiSubtotal += (Double)map.get("CHAYI") ;
							}
							if(map.get("YINGYE") != null){
								yingyeSubtotal += (Double)map.get("YINGYE") ;
							}
							if(map.get("CHENGJIAN") != null){
								chengjianSubtotal += (Double)map.get("CHENGJIAN") ;
							}
							if(map.get("JIAOYU") != null){
								jiaoyuSubtotal += (Double)map.get("JIAOYU") ;
							}
							if(map.get("INSURE_PRICE") != null){
								insure_priceSubtotal += (Double)map.get("INSURE_PRICE") ;
							}
							if(map.get("YINHUA") != null){
								yinhuaSubtotal += (Double)map.get("YINHUA") ;
							}
							if(map.get("XIAOJI") != null){
								xiaojiSubtotal += (Double)map.get("XIAOJI") ;
							}
							if(map.get("CLI") != null){
								cliSubtotal += (Double)map.get("CLI") ;
							}
							if(map.get("SLI") != null){
								sliSubtotal += (Double)map.get("SLI") ;
							}
							if(map.get("LIC") != null){
								licSubtotal += (Double)map.get("LIC") ;
							}
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		
		exportMap.put("nianList", nianList);
		exportMap.put("yueList", yueList);
		exportMap.put("ren_priceList", ren_priceList);
		exportMap.put("caiwuList", caiwuList);
		exportMap.put("shuiwuList", shuiwuList);
		exportMap.put("chayiList", chayiList);
		exportMap.put("yingyeList", yingyeList);
		exportMap.put("chengjianList", chengjianList);
		exportMap.put("jiaoyuList", jiaoyuList);
		exportMap.put("insure_priceList", insure_priceList);
		exportMap.put("yinhuaList", yinhuaList);
		exportMap.put("xiaojiList", xiaojiList);
		exportMap.put("cliList", cliList);
		exportMap.put("sliList", sliList);
		exportMap.put("licList", licList);
		
		//传给页面小计
		exportMap.put("ren_priceSubtotal", ren_priceSubtotal) ;
		exportMap.put("caiwuSubtotal", caiwuSubtotal) ;
		exportMap.put("shuiwuSubtotal",shuiwuSubtotal ) ;
		exportMap.put("chayiSubtotal", chayiSubtotal) ;
		exportMap.put("yingyeSubtotal", yingyeSubtotal) ;
		exportMap.put("chengjianSubtotal", chengjianSubtotal) ;
		exportMap.put("jiaoyuSubtotal", jiaoyuSubtotal) ;
		exportMap.put("insure_priceSubtotal", insure_priceSubtotal) ;
		exportMap.put("yinhuaSubtotal", yinhuaSubtotal) ;
		exportMap.put("xiaojiSubtotal", xiaojiSubtotal) ;
		exportMap.put("cliSubtotal", cliSubtotal) ;
		exportMap.put("sliSubtotal", sliSubtotal) ;
		exportMap.put("licSubtotal", licSubtotal) ;	
		//传给页面设备总金额
		exportMap.put("shebeiTotal",shebeiTotal) ;
		
		ByteArrayOutputStream baos = null;
		String strFileName = "成本差异("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
		
		InsuranceUtil insuranceUtil = new InsuranceUtil();
		insuranceUtil.createexl();
		baos = insuranceUtil.exportDifferentExcel(exportMap);
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
			errorList.add(e);
		}
	}
}