package com.brick.tables.service;




import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
/**
 * 
 * 全公司各辦事處______月______日進件統計表				
 * @author baiman
 *
 */
public class OfficeTableService extends AService {
	public static final Logger log = Logger.getLogger(OfficeTableService.class);
	Log logger = LogFactory.getLog(OfficeTableService.class);
	
	
	/**
	 * 查询所有公司办事处统计表
	 * @param context
	 */
	
	public void queryCompanys(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();

		List companyList = null;
		Map paramMap = new HashMap();
		Date querydate=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		//查询条件判断
		String type=null;
		String today="";
		if(context.getContextMap().get("QSTART_DATE")==null||"".equals(context.getContextMap().get("QSTART_DATE"))){
			today=format.format(querydate);
		}else{
			type = (String) context.contextMap.get("type");
			today =context.getContextMap().get("QSTART_DATE").toString();
		}
		paramMap.put("TODAY",today );
		if(errList.isEmpty()){		
			try {
				companyList = (List<Map>) DataAccessor.query("officeTable.queryCompanys", paramMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("办事处--查询全公司统计表错误!请联系管理员");
			}
		}
		outputMap.put("QSTART_DATE", today);
		outputMap.put("companyList",companyList );
		outputMap.put("type", context.contextMap.get("type")) ;
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/officeTables/queryCompanys.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	/**
	 * 按月、年统计各总分公司的统计表
	 * @param context
	 * @throws ParseException
	 */
	public void queryCompanyTables(Context context) throws ParseException{
		List errList = context.errList;
		Map outputMap = new HashMap();

		List<Object> companyList = new ArrayList();
		
		Map paramMap = new HashMap();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat sfYear=new SimpleDateFormat("yyyy年"); 
		SimpleDateFormat sfMon=new SimpleDateFormat("yyyy年MM月"); 
		
		//查询条件判断
		String type=null;
		Calendar  QSTART_DATE=Calendar.getInstance();
		Calendar querydate = Calendar.getInstance();
		Calendar begindate = Calendar.getInstance();
		Calendar today =Calendar.getInstance();
		Calendar tomorrow =Calendar.getInstance();
		Calendar enddate = Calendar.getInstance();
		Calendar now=Calendar.getInstance();	
		paramMap.put("id",context.getContextMap().get("aid") );
		if(context.getContextMap().get("type")==null){
			type="0";      //按月进行查询---0 ，  按年进行查询----1
		}else{
			type = (String) context.contextMap.get("type");
		}
		
		if(context.getContextMap().get("QSTART_DATE")==null||"".equals(context.getContextMap().get("QSTART_DATE"))){
			 QSTART_DATE.setTime(now.getTime());
		}else{
			QSTART_DATE.setTime((Date) sf.parseObject((String) context.getContextMap().get("QSTART_DATE")));
		}
		querydate.setTime(QSTART_DATE.getTime());
		int queryDay=querydate.get(Calendar.DATE);
		int queryMon=querydate.get(Calendar.MONTH);
		int queryYear=querydate.get(Calendar.YEAR);
		
		 if(type.equals("0")){
			    paramMap.put("type",type);
			    if(queryDay <25){
					begindate.setTime(querydate.getTime());
					enddate.setTime(querydate.getTime());
					begindate.set(Calendar.DATE,26 );
					begindate.add(Calendar.MONTH, -2);
					enddate.set(Calendar.DATE,26 );
					enddate.add(Calendar.MONTH,-1 );
				}else{
					begindate.setTime(querydate.getTime());
					enddate.setTime(querydate.getTime());
					begindate.set(Calendar.DATE,26 );
					begindate.add(Calendar.MONTH, -1);
					enddate.set(Calendar.DATE,26 );
				}
				today.setTime(begindate.getTime());
				tomorrow.setTime(begindate.getTime());
				tomorrow.add(Calendar.DATE,1);
				while((enddate.compareTo(tomorrow))>=0){
					paramMap.put("BEGINDATE",sf.format(begindate.getTime()).toString() );
					paramMap.put("ENDDATE",sf.format(enddate.getTime()).toString() );
					paramMap.put("TODAY",sf.format(today.getTime()).toString() );
					paramMap.put("TOMORROW",sf.format(tomorrow.getTime()).toString() );
					
					if(errList.isEmpty()){		
						try {
							Object obj = DataAccessor.query("officeTable.queryMonthTable", paramMap, DataAccessor.RS_TYPE.OBJECT);
						if(obj==null){
							obj=new Object();
						}
						companyList.add(obj);	
						} catch (Exception e) {
							e.printStackTrace();
							LogPrint.getLogStackTrace(e, logger);
							errList.add("办事处--查询全公司统计表错误!请联系管理员");
						}
					}
					tomorrow.add(Calendar.DATE,1);
					today.add(Calendar.DATE, 1);
					
				}
			}else{
				paramMap.put("type",type);
				begindate.setTime(querydate.getTime())	;
				
				begindate.set(Calendar.DATE, 1);
				begindate.set(Calendar.MONTH, 0);
				    int nowYear=now.get(Calendar.YEAR);
					if(queryYear==nowYear){
						enddate.setTime(now.getTime());
						enddate.set(Calendar.DATE, 1);
						enddate.add(Calendar.MONTH, 1);
					}else if(queryYear>nowYear){
						errList.add("年份超过今年");
						outputMap.put("errList", errList);
						Output.jspOutput(outputMap, context, "/error.jsp");
					}else{
						enddate.setTime(querydate.getTime());
						enddate.set(Calendar.DATE, 1);
						enddate.set(Calendar.MONTH, 0);
						enddate.add(Calendar.YEAR, 1);
					}
					tomorrow.setTime(begindate.getTime());
					today.setTime(begindate.getTime());
					tomorrow.add(Calendar.MONTH,1);
					while(enddate.compareTo(tomorrow)>=0){
						paramMap.put("BEGINDATE",sf.format(begindate.getTime()).toString() );
						paramMap.put("ENDDATE",sf.format(enddate.getTime()).toString() );
						paramMap.put("TODAY",sf.format(today.getTime()).toString() );
						paramMap.put("TOMORROW",sf.format(tomorrow.getTime()).toString() );
							if(errList.isEmpty()){		
								try {
									Object obj = DataAccessor.query("officeTable.queryMonthTable", paramMap, DataAccessor.RS_TYPE.OBJECT);
										if(obj==null){
											obj=new Object();
										}
									companyList.add(obj);
									} catch (Exception e) {
										e.printStackTrace();
										LogPrint.getLogStackTrace(e, logger);
										errList.add("办事处--查询全公司统计表错误!请联系管理员");
									}
							}
							tomorrow.add(Calendar.MONTH,1);
							today.add(Calendar.MONTH,1);
					}
		 }
		Map map=new HashMap();
		outputMap.put("QSTART_DATE", sf.format(QSTART_DATE.getTime()).toString());
		outputMap.put("BEGINDATE", sf.format(begindate.getTime()).toString());
		outputMap.put("ENDDATE",sf.format(enddate.getTime()).toString() );
		if(type.equals("0")){
			outputMap.put("QUERYTIME",(sfMon.format(enddate.getTime()).toString()) );//标题
		}else{
			outputMap.put("QUERYTIME",(sfYear.format(begindate.getTime()).toString() ));
		}
		
		outputMap.put("companyList",companyList );
		outputMap.put("aid",context.getContextMap().get("aid"));
		outputMap.put("aname",context.getContextMap().get("aname"));
		outputMap.put("type", type);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/officeTables/officeTableInfo.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/**
	 * 
	 * 导出Excel报表
	 * @param context
	 * @throws ParseException 
	 */
	public void reportExcel(Context context) throws ParseException
	{
		SimpleDateFormat sfYear=new SimpleDateFormat("yyyy年"); 
		SimpleDateFormat sfMon=new SimpleDateFormat("yyyy年MM月"); 
		
		Map outputMap = new HashMap();
		Map paramMap = new HashMap();//向Idatis传参
		List errorList = null;
		errorList = context.errList;

		Map maps = new HashMap();
		List companyList = new ArrayList();
		
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd"); 
		
		//查询数据companyList
		String type=context.getContextMap().get("type").toString();
		Calendar today =Calendar.getInstance();
		Calendar tomorrow =Calendar.getInstance();
		
		Calendar begindate=Calendar.getInstance();
		Calendar enddate =Calendar.getInstance();
		begindate.setTime((Date) sf.parse(context.getContextMap().get("BEGINDATE").toString())) ;
		enddate.setTime((Date) sf.parse(context.getContextMap().get("ENDDATE").toString()));
		
		paramMap.put("id",context.getContextMap().get("aid") );
		
		
		
		 if(type.equals("0")){
				today.setTime(begindate.getTime());
				tomorrow.setTime(begindate.getTime());
				tomorrow.add(Calendar.DATE,1);
				paramMap.put("type",type);
				while((enddate.compareTo(tomorrow))>=0){
					paramMap.put("TOMORROW",sf.format(tomorrow.getTime()).toString() );
					paramMap.put("BEGINDATE",sf.format(begindate.getTime()).toString() );
					paramMap.put("ENDDATE",sf.format(enddate.getTime()).toString() );
					paramMap.put("TODAY",sf.format(today.getTime()).toString() );
					
					if(errorList.isEmpty()){		
						try {
							List li = (List<Map>)DataAccessor.query("officeTable.queryMonthTable", paramMap, DataAccessor.RS_TYPE.LIST);
							for (Iterator iterator =li.iterator(); iterator.hasNext();) {
								Map map = (Map) iterator.next();
								companyList.add(map);
							}	
						} catch (Exception e) {
							e.printStackTrace();
							LogPrint.getLogStackTrace(e, logger);
							errorList.add("办事处--查询全公司统计表错误!请联系管理员");
						}
					}
					tomorrow.add(Calendar.DATE,1);
					today.add(Calendar.DATE, 1);
					
				}
			}else{  
				    paramMap.put("type",type);
					today.setTime(begindate.getTime());
					tomorrow.setTime(begindate.getTime());
					tomorrow.add(Calendar.MONTH,1);
					while(enddate.compareTo(tomorrow)>=0){
						
						paramMap.put("TOMORROW",sf.format(tomorrow.getTime()).toString() );
						paramMap.put("BEGINDATE",sf.format(begindate.getTime()).toString() );
						paramMap.put("ENDDATE",sf.format(enddate.getTime()).toString() );
						paramMap.put("TODAY",sf.format(today.getTime()).toString() );
							if(errorList.isEmpty()){		
								try {
									Object obj = DataAccessor.query("officeTable.queryMonthTable", paramMap, DataAccessor.RS_TYPE.OBJECT);;
									      Map map = (Map) obj;
										  companyList.add(map);
//										}
									} catch (Exception e) {
										e.printStackTrace();
										LogPrint.getLogStackTrace(e, logger);
										errorList.add("办事处--查询全公司统计表错误!请联系管理员");
									}
							}
						tomorrow.add(Calendar.MONTH,1);
						today.add(Calendar.MONTH,1);
					}
		 }
		maps.put("aname", context.getContextMap().get("aname"));
		if(type.equals("0")){
			maps.put("QUERYTIME",(sfMon.format(enddate.getTime()).toString()) );//标题
		}else{
			maps.put("QUERYTIME",(sfYear.format(begindate.getTime()).toString() ));
		}
		maps.put("type", type);	
		
		ReportExcel exc=new ReportExcel();
		exc.reportExcelJoin(companyList,maps, context);
	}
	/**
	 * 导出所有公司办事处统计表
	 * @param context
	 * @throws ParseException 
	 */
	
	public void exportAllCompanysTable(Context context) throws ParseException{
		List errList = context.errList;
		Map map = new HashMap();

		List companyList = null;
		Map paramMap = new HashMap();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日"); 
		SimpleDateFormat sfs=new SimpleDateFormat("yyyy-MM-dd"); 
		
	    String today="";
		today =context.getContextMap().get("QSTART_DATE").toString();
		
		paramMap.put("TODAY",today );
		if(errList.isEmpty()){		
			try {
				companyList = (List<Map>) DataAccessor.query("officeTable.queryCompanys", paramMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("办事处--查询全公司统计表错误!请联系管理员");
			}
		}

		map.put("TODAY", sf.format(sfs.parse(today)));
		ReportAllCompTbExcel exc=new ReportAllCompTbExcel();
		exc.reportAllCompTb(companyList,map, context);
	}
}
