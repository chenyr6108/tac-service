package com.brick.insurance.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
/*
 * 保单管理
 */
public class InusranceListService extends BaseCommand {
	static Log logger = LogFactory.getLog(InusranceListService.class);
	
	private MailUtilService mailUtil;
	
	public MailUtilService getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtilService mailUtil) {
		this.mailUtil = mailUtil;
	}

	/**
	 * 保单管理页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public  void queryAll(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> pagingInfo = null;
		List company=new ArrayList();
		/*2011/12/12 Yang Yun Add "保单状态" search field. Start*/
		String incustatus = null;
		if (StringUtils.isEmpty((String)context.contextMap.get("incustatus"))) {
			incustatus = "";
		} else {
			incustatus = ((String) context.contextMap.get("incustatus")).trim();
		}
		context.contextMap.put("incustatus", incustatus);
		/*2011/12/12 Yang Yun Add "保单状态" search field. End*/
		if (errList.isEmpty()) {
			try {
				 company = (List) DataAccessor.query("insurance.getCompany", context.contextMap, DataAccessor.RS_TYPE.LIST);
				 pagingInfo = baseService.queryForListWithPaging("insurance.insuListManage", context.contextMap, "CREDIT_RUNCODE", ORDER_TYPE.DESC);
			} catch (Exception e) {
				errList.add("保单管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		
		if (errList.isEmpty()) {
			/* 2012/01/30 Yang Yun 新增特殊说明查询.*************** */
			outputMap.put("remark_type", context.contextMap.get("remark_type"));
			/* *************************************************** */
			outputMap.put("isRenewal", context.contextMap.get("isRenewal"));
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("company", company);
			/*2011/12/12 Yang Yun Add "保单状态" search field. Start*/
			outputMap.put("incustatus", incustatus);
			/*2011/12/12 Yang Yun Add "保单状态" search field. End*/
			outputMap.put("INCP",context.request.getParameter("INCP"));
			outputMap.put("content", context.contextMap.get("content"));
			Output
					.jspOutput(outputMap, context,
							"/insurance/insuList/insuListManage.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	//保单付款管理页
	public void getInsurePayMoney(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		
		Object payStates=context.contextMap.get("payState");
		Object complayId=context.contextMap.get("complayId");
		String payState=null;
		String incpId=null;
		
		String companyCode = (String) context.contextMap.get("companyCode");
		if (StringUtils.isEmpty(companyCode)) {
			companyCode = "1";
			context.contextMap.put("companyCode", companyCode);
		}
		
		if((payStates==null || "".equals(payStates)) && (complayId==null || "".equals(complayId)))
		{
			try {
			Map incpIdNum=(Map)DataAccessor.query("insurance.getInsureComplayByTopOne",context.contextMap, DataAccessor.RS_TYPE.MAP);
			incpId=incpIdNum.get("INCP_ID").toString();
			} catch (Exception e) {
				errList.add("保单付款管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
			payState="0";
			
		}
		else
		{
			if(complayId==null || "".equals(complayId) )
			{
				try {
					Map incpIdNum=(Map)DataAccessor.query("insurance.getInsureComplayByTopOne",context.contextMap, DataAccessor.RS_TYPE.MAP);
					incpId=incpIdNum.get("INCP_ID").toString();
					} catch (Exception e) {
						errList.add("保单付款管理 : " + e);
						LogPrint.getLogStackTrace(e, logger);
						errList.add(e);
						
					}
			}
			else
			{
				incpId=complayId.toString();
			}
			payState=payStates.toString();
			
		}
		
		List complayList=null;
		context.contextMap.put("incpId", incpId);
		if (errList.isEmpty()) {
			try {
				complayList=(List)DataAccessor.query("insurance.getInsureComplay",context.contextMap, DataAccessor.RS_TYPE.LIST);
				if(payState.equals("0"))//未拨款
				{
					dw = (DataWrap) DataAccessor.query("insurance.getInsurePayMoney",context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}
				else if(payState.equals("1"))//拨款中
				{
					dw = (DataWrap) DataAccessor.query("insurance.queryPayMoneyByFirstInsusranceing",context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}
				else if(payState.equals("2"))//拨款成功
				{
					dw = (DataWrap) DataAccessor.query("insurance.queryPayMoneyByFirstInsusrancesuccess",context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}
				else if(payState.equals("3"))//驳回
				{
					dw = (DataWrap) DataAccessor.query("insurance.queryPayMoneyByFirstInsusranceback",context.contextMap, DataAccessor.RS_TYPE.PAGED);
				}
				
			} catch (Exception e) {
				errList.add("保单付款管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("payState", context.contextMap.get("payState"));
			outputMap.put("group_code", context.contextMap.get("group_code"));
			outputMap.put("companyCode", companyCode);
			if(payState.equals("0"))
			{
				outputMap.put("complayList", complayList);
				outputMap.put("complayId", incpId);
				Output.jspOutput(outputMap, context,"/backMoney/insurancePayNew.jsp");
			}
			else
			{
				Output.jspOutput(outputMap, context,"/backMoney/backMoneyInsurance.jsp");
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	public void getInsurPayList(Context context)
	{
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "incu_id", "00");
		//String[]  conPrice = HTMLUtil.getParameterValues(context.getRequest(), "insu_price", "0");
		String insu_nameId=null;
		String insu_bankname=null;
		String conIncu_id="";
		float price=0f;
		if(con != null ){
		     if(!(con[0].equals("00")))
		     {
		    	 if(con.length >0)
		    	 {
		    		 //System.out.println("-----------------------"+con.length);
    				for(int i=0;i<con.length;i++)
    				{
    					if(i==0)
    					{
    						conIncu_id=con[i];
    					}
    					else
    					{
    						conIncu_id=conIncu_id+","+con[i];
    					}
    					
    					String conPrices=context.contextMap.get("insu_price"+con[i]).toString();
    					insu_nameId=context.contextMap.get("insu_name"+con[i]).toString();
    					insu_bankname=context.contextMap.get("insu_bankname"+con[i]).toString();
    					if(conPrices!=null && !"".equals(conPrices))
    					{
    						//System.out.println(conPrices);
    						price+=Float.parseFloat(conPrices);
    						
    					}
    				}
    				//System.out.println("++++++++++++++++++="+conIncu_id);
                         	             	    
    			 }
		    	 
		     }
			 
		 }
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date time=new Date();
		String times=sf.format(time).toString();
		List errList = context.errList;
		List psTypeList = null;
		List bankAcount=null;
		
		if (errList.isEmpty()) {
			try {
		//拨款方式
		context.contextMap.put("dataType","拨款方式");
		psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		
		bankAcount=(List<Map>) DataAccessor.query("insurance.queryComplayBankByIncp_id", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保单付款管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		
		Map outputMap = new HashMap();
		outputMap.put("psTypeList", psTypeList); 
		outputMap.put("priceCount", price);
		outputMap.put("insu_nameId", insu_nameId);
		outputMap.put("insu_bankname", insu_bankname);
		outputMap.put("times", times);
		outputMap.put("conIncu_id", conIncu_id);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		Output.jspOutput(outputMap, context,"/backMoney/insuranceDetilPayNew.jsp");
		
	}
	
	
	
	
	//查询出该保险公司的银行及账号
	public void bankByincp_ip(Context context)
	{
		Map outputMap = new HashMap();
		List bankAcount=null;
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
		
		bankAcount=(List) DataAccessor.query("insurance.queryComplayBankByIncp_id", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保单付款管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		outputMap.put("bankAcount", bankAcount); 
		Output.jsonOutput(outputMap, context); 
	}
	
	
	//查询出该员工的银行及账号
	public void bankByUser_Name(Context context)
	{
		Map outputMap = new HashMap();
		List bankAcount=null;
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
		
		bankAcount=(List) DataAccessor.query("insurance.queryUserBankByUser_name", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				errList.add("保单付款管理 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
				
			}
		}
		outputMap.put("bankAcount", bankAcount); 
		Output.jsonOutput(outputMap, context); 
	}
	
}
