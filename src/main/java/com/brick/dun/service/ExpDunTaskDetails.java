package com.brick.dun.service;



import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;

public class ExpDunTaskDetails extends AService{
	Log logger = LogFactory.getLog(ExpDunTaskDetails.class);
	   /**
     * 导出逾期催收报表
     * @param context
     */
 
	
	@SuppressWarnings("unchecked")
	public  void expExcel(Context context){
		List errList = context.errList;
		Map map = new HashMap();

		List<Map> dunList = null;
		List expdunlist = new ArrayList();
		List<Map> dictionary_cuishou = null;
		List<Map> dictionary_com = null;
		List<Map> dictionary_ht = null;
		List<Map> dictionary_lock = null;
		SimpleDateFormat sfs=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		SimpleDateFormat sf2=new SimpleDateFormat("yyyy-MM-dd"); 
		 // NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
		  DecimalFormat nfFSNum = new DecimalFormat("#.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        
		if(errList.isEmpty()){		
			try {
				/*2011/12/28 Yang Yun 导出逾期催收报表 数据抓取 SQL重写。*/
				/*2011/12/28 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件) -------*/
				Map<String, Object> rsMap = null;
				context.contextMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if (rsMap == null || rsMap.get("NODE") == null) {
					throw new Exception("Session lost");
				}
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				if(context.contextMap.get("dun_date")==null||context.contextMap.get("dun_date").equals("")){
					DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					context.contextMap.put("dun_date", format.format(new Date()));
				}
				/*--------------------------------------------------------------------------- */
				String[] decpIds = context.getRequest().getParameterValues("COMPANY[]");
				List<Integer> decpIdList = null;
				if(decpIds != null && decpIds.length > 0){
					decpIdList = new ArrayList<Integer>();
					for(String id : decpIds){
						decpIdList.add(Integer.parseInt(id));
					}
				}
				context.contextMap.put("COMPANY", decpIdList);
				String[] orgDecpIds = context.getRequest().getParameterValues("ORG_COMPANY[]");
				List<Integer> orgDecpIdList = null;
				if(orgDecpIds != null && orgDecpIds.length > 0){
					orgDecpIdList = new ArrayList<Integer>();
					for(String id : orgDecpIds){
						orgDecpIdList.add(Integer.parseInt(id));
					}
				}
				context.contextMap.put("ORG_COMPANY", orgDecpIdList);
				
				dunList=((List<Map>) DataAccessor.query("dunTask.expDunTaskDetails", context.contextMap, DataAccessor.RS_TYPE.LIST));
				dictionary_cuishou = (List<Map>) DictionaryUtil.getDictionary("催收结果");
				dictionary_com = (List<Map>) DictionaryUtil.getDictionary("企业类型");
				dictionary_ht = (List<Map>) DictionaryUtil.getDictionaryForAll("融资租赁合同类型");
				Iterator iter = dunList.iterator();
				while(iter.hasNext()){
					Map contentmap=(HashMap)iter.next();
					Map contextMapSun=new HashMap();
					contextMapSun.put("COMPANY_CODE", contentmap.get("COMPANY_CODE")==null ? "1" :contentmap.get("COMPANY_CODE").toString());
					contextMapSun.put("DECP_NAME_CN", contentmap.get("DECP_NAME_CN")==null ? "" :contentmap.get("DECP_NAME_CN").toString());
					contextMapSun.put("NAME", contentmap.get("NAME")==null ? "" :contentmap.get("NAME").toString()) ;
					contextMapSun.put("LEASE_CODE",contentmap.get("LEASE_CODE")==null ? "" :contentmap.get("LEASE_CODE").toString());
					contextMapSun.put("CUST_NAME",contentmap.get("CUST_NAME")==null ? "" :contentmap.get("CUST_NAME").toString());
					//0822新增首次逾期日期及期数
					contextMapSun.put("FIRSTDUNDATE",contentmap.get("FIRSTDUNDATE")==null ? "" :contentmap.get("FIRSTDUNDATE").toString());
					contextMapSun.put("FIRSTDUNPERIOD",contentmap.get("FIRSTDUNPERIOD")==null ? "" :contentmap.get("FIRSTDUNPERIOD").toString());
					//公司类型
					contextMapSun.put("CORP_TYPE",contentmap.get("CORP_TYPE")==null ? "" :contentmap.get("CORP_TYPE").toString());
					contextMapSun.put("MIN_PERIOD_NUM",contentmap.get("MIN_PERIOD_NUM")==null ? "" :contentmap.get("MIN_PERIOD_NUM").toString());
					//Modify by michael 2012-09-07 修正期数抓出Bug
					contextMapSun.put("MAX_PERIOD_NUM",contentmap.get("LEASE_PERIOD")==null ? "" :contentmap.get("LEASE_PERIOD").toString());
					contextMapSun.put("IRR_MONTH_price",contentmap.get("IRR_MONTH_PRICE")==null ? "" :nfFSNum.format(Double.parseDouble(contentmap.get("IRR_MONTH_PRICE").toString())));
					contextMapSun.put("AL_PERIOD_NUM",contentmap.get("AL_PERIOD_NUM")==null ? "" :contentmap.get("AL_PERIOD_NUM").toString());
					contextMapSun.put("PAY_DATE",contentmap.get("PAY_DATE")==null ? "" :sf2.format((sf2.parseObject(contentmap.get("PAY_DATE").toString()))));//租金到期日
					contextMapSun.put("DUN_DAY",contentmap.get("DUN_DAY")==null ? "" :contentmap.get("DUN_DAY").toString());
					contextMapSun.put("DUN_MONTHPRICE",contentmap.get("DUN_MONTHPRICE")==null ? "" : nfFSNum.format(Double.parseDouble(contentmap.get("DUN_MONTHPRICE").toString())));//应缴租金
					contextMapSun.put("LAST_PRICE",contentmap.get("LAST_PRICE")==null ? "" : nfFSNum.format(Double.parseDouble(contentmap.get("LAST_PRICE").toString())));
					contextMapSun.put("ANSWERPHONE_NAME",contentmap.get("ANSWERPHONE_NAME")==null ? "" : contentmap.get("ANSWERPHONE_NAME").toString());
					for(int i=0;i<dictionary_ht.size();i++){
						String RECT_TYPE=contentmap.get("RECT_TYPE")==null ? "" :contentmap.get("RECT_TYPE").toString();
						if(RECT_TYPE.equals((dictionary_ht.get(i).get("CODE").toString()))){
							RECT_TYPE=dictionary_ht.get(i).get("FLAG").toString();
							contextMapSun.put("RECT_TYPE",RECT_TYPE);
						}
						
					}//租赁方式
					contextMapSun.put("BRAND",contentmap.get("BRAND")==null ? "" :contentmap.get("BRAND").toString());  
					//Modify by Michael 2012 09-07 修正锁码抓取方式
					contextMapSun.put("LOCK_CODE",contentmap.get("LOCK_CODE")==null ? "" :contentmap.get("LOCK_CODE").toString());
					//Modify by Michael 2012 09-07 修正锁码抓取方式
					contextMapSun.put("SUPL_TRUE",contentmap.get("SUPL_TRUE")==null ? "" :contentmap.get("SUPL_TRUE").toString());
					contextMapSun.put("LOCK_DATE",contentmap.get("LOCK_DATE")==null ? "":sf2.format(sf2.parseObject(contentmap.get("LOCK_DATE").toString())));//锁码
					contextMapSun.put("CALL_DATE",contentmap.get("CALL_DATE")==null ? "" :sfs.format(sfs.parseObject(contentmap.get("CALL_DATE").toString())));//cuishou日期
					for(int i=0;i<dictionary_cuishou.size();i++){
						String RESULT=contentmap.get("RESULT")==null ? "" :contentmap.get("RESULT").toString();
						if(RESULT.equals((dictionary_cuishou.get(i).get("CODE").toString()))){
							RESULT=dictionary_cuishou.get(i).get("FLAG").toString();
							contextMapSun.put("RESULT",RESULT);
						}
					}
					//Modify by Michael 2012 09-11 增加催收电话内容
					contextMapSun.put("CALL_CONTENT",contentmap.get("CALL_CONTENT")==null ? "" :contentmap.get("CALL_CONTENT").toString());
					contextMapSun.put("VISIT_DATE",contentmap.get("VISIT_DATE")==null ? "" :contentmap.get("VISIT_DATE").toString());
					contextMapSun.put("VISIT_NAME",contentmap.get("VISIT_NAME")==null ? "" :contentmap.get("VISIT_NAME").toString());
					contextMapSun.put("VISIT_TIMES",contentmap.get("VISIT_TIMES")==null ? "" :contentmap.get("VISIT_TIMES").toString());
					contextMapSun.put("TOTAL_RENT_PRICE",contentmap.get("TOTAL_RENT_PRICE")==null ? "" : nfFSNum.format(Double.parseDouble(contentmap.get("TOTAL_RENT_PRICE").toString())));
					contextMapSun.put("RECP_ID",contentmap.get("RECP_ID")==null ? "" :contentmap.get("RECP_ID").toString());
					contextMapSun.put("PAYMONEY",contentmap.get("PAYMONEY")==null ? "" :nfFSNum.format(contentmap.get("PAYMONEY")).toString());
					contextMapSun.put("FINANCECONTRACT_DATE",contentmap.get("FINANCECONTRACT_DATE")==null ? "" :contentmap.get("FINANCECONTRACT_DATE").toString());
					expdunlist.add(contextMapSun);
				}
				ReportDunTaskDetails exc=new ReportDunTaskDetails();
				exc.reportDunTaskDetail(expdunlist, context);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("办事处--查询逾期错误!请联系管理员");
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	public static Map expDunListExcel(String dun_date,String s_employeeId){
		Map resultMap = new HashMap();
		
		List<Map> dunList = null;
		List expdunlist = new ArrayList();
		List<Map> dictionary_cuishou = null;
		List<Map> dictionary_com = null;
		List<Map> dictionary_ht = null;
		List<Map> dictionary_lock = null;
		SimpleDateFormat sfs=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		SimpleDateFormat sf2=new SimpleDateFormat("yyyy-MM-dd");
		NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
		Map context =new HashMap();

		try {
			Map<String, Object> rsMap = null;
			context.put("id", s_employeeId);
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById", context, DataAccessor.RS_TYPE.MAP);
			if (rsMap == null || rsMap.get("NODE") == null) {
				throw new Exception("Session lost");
			}
			context.put("p_usernode", rsMap.get("NODE"));
			if(dun_date==null||dun_date.equals("")){
				DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				context.put("dun_date", format.format(new Date()));
			}
			/*--------------------------------------------------------------------------- */
			dunList=((List<Map>) DataAccessor.query("dunTask.expDunTaskDetails", context, DataAccessor.RS_TYPE.LIST));
			dictionary_cuishou = (List<Map>) DictionaryUtil.getDictionary("催收结果");
			dictionary_com = (List<Map>) DictionaryUtil.getDictionary("企业类型");
			dictionary_ht = (List<Map>) DictionaryUtil.getDictionary("融资租赁合同类型");
			
			Iterator iter = dunList.iterator();
			while(iter.hasNext()){
				Map contentmap=(HashMap)iter.next();
				Map contextMapSun=new HashMap();
				
				contextMapSun.put("DECP_NAME_CN", contentmap.get("DECP_NAME_CN")==null ? "" :contentmap.get("DECP_NAME_CN").toString());
				contextMapSun.put("NAME", contentmap.get("NAME")==null ? "" :contentmap.get("NAME").toString()) ;
				contextMapSun.put("LEASE_CODE",contentmap.get("LEASE_CODE")==null ? "" :contentmap.get("LEASE_CODE").toString());
				contextMapSun.put("CUST_NAME",contentmap.get("CUST_NAME")==null ? "" :contentmap.get("CUST_NAME").toString());
				contextMapSun.put("CORP_TYPE",contentmap.get("CORP_TYPE")==null ? "" :contentmap.get("CORP_TYPE").toString());
				contextMapSun.put("MIN_PERIOD_NUM",contentmap.get("MIN_PERIOD_NUM")==null ? "" :contentmap.get("MIN_PERIOD_NUM").toString());
				//Modify by michael 2012-09-07 修正期数抓出Bug
				//contextMapSun.put("MAX_PERIOD_NUM",contentmap.get("MAX_PERIOD_NUM")==null ? "" :contentmap.get("MAX_PERIOD_NUM").toString());
				contextMapSun.put("MAX_PERIOD_NUM",contentmap.get("LEASE_PERIOD")==null ? "" :contentmap.get("LEASE_PERIOD").toString());
				
				contextMapSun.put("IRR_MONTH_price",contentmap.get("IRR_MONTH_PRICE")==null ? "" :nfFSNum.format(Double.parseDouble(contentmap.get("IRR_MONTH_PRICE").toString())));
				contextMapSun.put("AL_PERIOD_NUM",contentmap.get("AL_PERIOD_NUM")==null ? "" :contentmap.get("AL_PERIOD_NUM").toString());
				
				contextMapSun.put("PAY_DATE",contentmap.get("PAY_DATE")==null ? "" :sf2.format((sf2.parseObject(contentmap.get("PAY_DATE").toString()))));//租金到期日
				contextMapSun.put("DUN_DAY",contentmap.get("DUN_DAY")==null ? "" :contentmap.get("DUN_DAY").toString());
				contextMapSun.put("DUN_MONTHPRICE",contentmap.get("DUN_MONTHPRICE")==null ? "" : nfFSNum.format(Double.parseDouble(contentmap.get("DUN_MONTHPRICE").toString())));//应缴租金
				contextMapSun.put("LAST_PRICE",contentmap.get("LAST_PRICE")==null ? "" : nfFSNum.format(Double.parseDouble(contentmap.get("LAST_PRICE").toString())));
				
				contextMapSun.put("ANSWERPHONE_NAME",contentmap.get("ANSWERPHONE_NAME")==null ? "" : contentmap.get("ANSWERPHONE_NAME").toString());
				for(int i=0;i<dictionary_ht.size();i++){
					String RECT_TYPE=contentmap.get("RECT_TYPE")==null ? "" :contentmap.get("RECT_TYPE").toString();
					if(RECT_TYPE.equals((dictionary_ht.get(i).get("CODE").toString()))){
						RECT_TYPE=dictionary_ht.get(i).get("FLAG").toString();
						contextMapSun.put("RECT_TYPE",RECT_TYPE);
					}
					
				}//租赁方式
				contextMapSun.put("BRAND",contentmap.get("BRAND")==null ? "" :contentmap.get("BRAND").toString());  
				
				//Modify by Michael 2012 09-07 修正锁码抓取方式
				contextMapSun.put("LOCK_CODE",contentmap.get("LOCK_CODE")==null ? "" :contentmap.get("LOCK_CODE").toString());
				
				//Modify by Michael 2012 09-07 修正锁码抓取方式
				contextMapSun.put("SUPL_TRUE",contentmap.get("SUPL_TRUE")==null ? "" :contentmap.get("SUPL_TRUE").toString());
				
				contextMapSun.put("LOCK_DATE",contentmap.get("LOCK_DATE")==null ? "":sf2.format(sf2.parseObject(contentmap.get("LOCK_DATE").toString())));//锁码
				contextMapSun.put("CALL_DATE",contentmap.get("CALL_DATE")==null ? "" :sfs.format(sfs.parseObject(contentmap.get("CALL_DATE").toString())));//cuishou日期
				for(int i=0;i<dictionary_cuishou.size();i++){
					String RESULT=contentmap.get("RESULT")==null ? "" :contentmap.get("RESULT").toString();
					if(RESULT.equals((dictionary_cuishou.get(i).get("CODE").toString()))){
						RESULT=dictionary_cuishou.get(i).get("FLAG").toString();
						contextMapSun.put("RESULT",RESULT);
					}
					
				}
				//Modify by Michael 2012 09-11 增加催收电话内容
				contextMapSun.put("CALL_CONTENT",contentmap.get("CALL_CONTENT")==null ? "" :contentmap.get("CALL_CONTENT").toString());
				
				contextMapSun.put("VISIT_DATE",contentmap.get("VISIT_DATE")==null ? "" :contentmap.get("VISIT_DATE").toString());
				contextMapSun.put("VISIT_NAME",contentmap.get("VISIT_NAME")==null ? "" :contentmap.get("VISIT_NAME").toString());
				contextMapSun.put("VISIT_TIMES",contentmap.get("VISIT_TIMES")==null ? "" :contentmap.get("VISIT_TIMES").toString());
				
				expdunlist.add(contextMapSun);
				resultMap.put("expdunlist", expdunlist);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
}
