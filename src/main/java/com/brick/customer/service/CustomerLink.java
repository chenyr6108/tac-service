package com.brick.customer.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.coderule.service.CodeRule;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


/**
 * 2010-08-4 customerLink
 * 
 * @author cheng
 * 
 */
public class CustomerLink extends AService {
	Log logger = LogFactory.getLog(this.getClass());

	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	
	@Override
	protected void afterExecute(String action, Context context) {
		super.afterExecute(action, context);
	}
	/**
	 * 保存联系记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustLinkRecord(Context context) {

	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
		Map custLinkRecord = new HashMap();
		Long culr_id = 0l;	
		
		
	if(errList.isEmpty()){	
		
		try{	
			culr_id =  (Long) DataAccessor.execute("customer.createCustLINKRecord", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			
			context.contextMap.put("CULR_ID", culr_id);
			custLinkRecord = (Map)DataAccessor.query("customer.readCustLinkRecord",context.contextMap , DataAccessor.RS_TYPE.MAP);
									  
		}catch(Exception e){
		    
		    
		    	errList.add("保存联系记录出错 ："+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
		
	
	if(errList.isEmpty()){	
	   
		outputMap.put("custLinkRecord", custLinkRecord);
		Output.jsonOutput(outputMap, context);
		
	}else {
	    	outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
        	    
        	}

	}
	
	/**
	 * 查看联系记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void readCustLinkRecord(Context context) {

	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
		Map custLinkRecord = new HashMap();
		String  culr_id = HTMLUtil.getStrParam(context.request, "CULR_ID", "");
		
		
	if(errList.isEmpty()){	
		
		try{	
			
			context.contextMap.put("CULR_ID", culr_id);
			custLinkRecord = (Map)DataAccessor.query("customer.readCustLinkRecord",context.contextMap , DataAccessor.RS_TYPE.MAP);
									  
		}catch(Exception e){
		    
		    
		    	errList.add("查询联系记录出错 ："+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
		
	
	if(errList.isEmpty()){	
	   
		outputMap.put("custLinkRecord", custLinkRecord);
		Output.jsonOutput(outputMap, context);
		
	}else {
	    	outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
        	    
        	}

	}
	
	
	
	/**
	 * 删除一条记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteLinkrecord(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    int result = 0;
	    if(errList.isEmpty()){
		
		try {
		    
		    result = (Integer) DataAccessor.execute("customer.deleteLinkrecord", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		    outputMap.put("result", result);
		    
		} catch (Exception e) {
		    errList.add("删除联系人记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	
		
		
	
	
	/**
	 * 保存 客户关怀 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustLinkcare(Context context) {

	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
		Map custLinkcare = new HashMap();
		Long culc_id = 0l;	
		
		
	if(errList.isEmpty()){	
		
		try{	
			culc_id =  (Long) DataAccessor.execute("customer.createCustLinkCare", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			
			context.contextMap.put("CULC_ID", culc_id);
			custLinkcare = (Map)DataAccessor.query("customer.readCustLinkcare",context.contextMap , DataAccessor.RS_TYPE.MAP);
									  
		}catch(Exception e){
		    
		    
		    	errList.add("保存联系记录出错 ："+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
		
	
	if(errList.isEmpty()){	
	   
		outputMap.put("custLinkcare", custLinkcare);
		Output.jsonOutput(outputMap, context);
		
	}else {
	    	outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
        	    
        	}

	}
	
	
	/**
	 * 查看 客户关怀 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void readCustLinkcare(Context context) {

	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
		Map custLinkcare = new HashMap();
		String culc_id = HTMLUtil.getStrParam(context.request, "CULC_ID", "");	
		
		
	if(errList.isEmpty()){	
		
		try{	
			
			context.contextMap.put("CULC_ID", culc_id);
			custLinkcare = (Map)DataAccessor.query("customer.readCustLinkcare",context.contextMap , DataAccessor.RS_TYPE.MAP);
									  
		}catch(Exception e){
		    
		    
		    	errList.add("查看联系记录出错 ："+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
		
	
	if(errList.isEmpty()){	
	   
		outputMap.put("custLinkcare", custLinkcare);
		Output.jsonOutput(outputMap, context);
		
	}else {
	    	outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
        	    
        	}

	}
	
	
	
	/**
	 * 删除一条 客户关怀
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteLinkcare(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    int result = 0;
	    if(errList.isEmpty()){
		
		try {
		    
		    result = (Integer) DataAccessor.execute("customer.deleteLinkcare", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		    outputMap.put("result", result);
		    
		} catch (Exception e) {
		    errList.add("删除客户关怀记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	/**
	 * 查看 费用记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void readCustLinkExpense(Context context) {
	    
	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
	    Map custLinkExpense = new HashMap();
	    String cule_id = HTMLUtil.getStrParam(context.request, "CULE_ID", "");	
	    
	    
	    if(errList.isEmpty()){	
		
		try{	
		    
		    context.contextMap.put("CULE_ID", cule_id);
		    custLinkExpense = (Map)DataAccessor.query("customer.readCustLinkExpense",context.contextMap , DataAccessor.RS_TYPE.MAP);
		    
		}catch(Exception e){
		    
		    
		    errList.add("查看费用记录出错 ："+e.getMessage());
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    
	    if(errList.isEmpty()){	
		
		outputMap.put("custLinkExpense", custLinkExpense);
		Output.jsonOutput(outputMap, context);
		
	    }else {
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
		
	    }
	    
	}
	
	
	/**
	 * 保存 费用记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustLinkExpense(Context context) {
	    
	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
	    Map custLinkExpense = new HashMap();
	    Long cule_id = 0l;	
	    
	    
	    if(errList.isEmpty()){	
		
		try{	
		    cule_id =  (Long) DataAccessor.execute("customer.createCustLinkExpense", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		    
		    context.contextMap.put("CULE_ID", cule_id);
		    custLinkExpense = (Map)DataAccessor.query("customer.readCustLinkExpense",context.contextMap , DataAccessor.RS_TYPE.MAP);
		    
		}catch(Exception e){
		    
		    
		    errList.add("保存费用记录出错 ："+e.getMessage());
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    
	    if(errList.isEmpty()){	
		
		outputMap.put("custLinkExpense", custLinkExpense);
		Output.jsonOutput(outputMap, context);
		
	    }else {
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
		
	    }
	    
	}
	
	
	
	
	/**
	 * 删除一条  费用记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteLinkExpense(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    int result = 0;
	    if(errList.isEmpty()){
		
		try {
		    
		    result = (Integer) DataAccessor.execute("customer.deleteLinkExpense", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		    outputMap.put("result", result);
		    
		} catch (Exception e) {
		    errList.add("删除费用记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	/**
	 * 保存 竞争对手记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustLinkCompetitor(Context context) {
	    
	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    Long result = 0l;
	    if(errList.isEmpty()){	
		
		try{	
		    
		  result=   (Long) DataAccessor.execute("customer.createCustLinkCompetitor", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		    
		}catch(Exception e){
		    
		    errList.add("保存费用记录出错 ："+e.getMessage());
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    
	    if(errList.isEmpty()){	
		
		outputMap.put("result", result);
		Output.jsonOutput(outputMap, context);
		
	    }else {
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
		
	    }
	    
	}
	/**
	 * 删除一条  竞争对手记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteLinkCompetitor(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    int result = 0;
	    if(errList.isEmpty()){
		
		try {
		    
		    result = (Integer) DataAccessor.execute("customer.deleteLinkCompetitor", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		    outputMap.put("result", result);
		    
		} catch (Exception e) {
		    errList.add("删除费用记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	
	/**
	 * 竞争对手 管 理
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void queryLinkCompetitor(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    DataWrap dw = null;
	    
	    if(errList.isEmpty()){
		
		try {
		    
		    dw =  (DataWrap) DataAccessor.query("customer.queryLinkCompetitorList", context.contextMap, DataAccessor.RS_TYPE.PAGED);

		    
		    
		} catch (Exception e) {
		    
		    errList.add("查询竞争对手记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		outputMap.put("dw", dw);
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		outputMap.put("DANGEROUS_LEVEL", context.contextMap.get("DANGEROUS_LEVEL"));

		Output.jspOutput(outputMap, context, "/customer/queryLinkCompetitor.jsp");
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	/**
	 * 
	 * 查看竞争对手详细  
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void showLinkCompetitor(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    Map paramMap =new HashMap();
	    
	    String CULT_ID = HTMLUtil.getStrParam(context.request, "CULT_ID", "");
	    String flage = HTMLUtil.getStrParam(context.request, "flage", "");
	    	  
	    context.contextMap.put("CULT_ID", CULT_ID);
	    
	    if(errList.isEmpty()){
		
		try {
		    
		    paramMap = (Map) DataAccessor.query("customer.readCustLinkCompetitor", context.contextMap, DataAccessor.RS_TYPE.MAP);
		    
		} catch (Exception e) {
		    
		    errList.add("查询竞争对手详细信息出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		outputMap.put("pam", paramMap);
		
		if("".equals(flage)){
		    
		    Output.jspOutput(outputMap, context, "/customer/updateLinkCompetitor.jsp");
		    
		}else{
		    
		    Output.jspOutput(outputMap, context, "/customer/showLinkCompetitor.jsp");
		}
		
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	/**
	 * 
	 * 修改竞争对手详细  
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void updateLinkCompetitor(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    if(errList.isEmpty()){
		
		try {
		    
		    DataAccessor.execute("customer.updateLinkCompetitor", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		  
		} catch (Exception e) {
		    
		    errList.add("修改竞争对手详细信息出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		outputMap.put("result", 1);
		Output.jsonOutput(outputMap, context);
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	
	/**
	 * 查询客户 与联系人
	 * cust_id
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void queryCustomerInfo(Context context){
	    
	    Map outputMap = new HashMap();
	    List errList = context.errList;
	    List cust = new  ArrayList();
	    
	    if(errList.isEmpty()){
		
		try {
		   
		    cust = DataAccessor.getSession().queryForList("customer.queryCustlinInfo", context.contextMap);
		
		} catch (SQLException e) {
		
		    errList.add("查询客户 与联系人 信息出错 : "+e.getMessage()); 
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} 
		
		
	    }
	    
	    if(errList.isEmpty()){
		
		outputMap.put("cust", cust);
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList",  errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	    
	    
	}
}
