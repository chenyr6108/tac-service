package com.brick.credit.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.product.ProductConstants;
import com.brick.product.service.ProductCredit;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class CreditVouchManage extends AService{
	Log logger = LogFactory.getLog(CreditVouchService.class);
	@SuppressWarnings("unchecked")
	public void getAllCieditVouch(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		List errList = context.errList ;
		try {
			dw = (DataWrap) DataAccessor.query("creditVoucher.allVouch",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			if(dw != null){
				List rs = (List) dw.getRs() ;
				for(int i=0;i < rs.size();i++){
					Map temp = (Map) rs.get(i) ;
					if(temp.get("PDVP_ID") != null){
						temp.put("LAST_PRICE",CreditVouchManage.VOUCHPLANBYLASTPRICE(temp.get("NAME").toString(),temp.get("CODE").toString(),Integer.parseInt(temp.get("TYPE").toString()))) ;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人授信浏览页错误!请联系管理员") ;
		}
		outputMap.put("dw", dw);
		outputMap.put("QSTARTMONEY", context.contextMap.get("QSTARTMONEY"));
		outputMap.put("QENDMONEY", context.contextMap.get("QENDMONEY"));
		outputMap.put("QCUGPDTATUS", context.contextMap.get("QCUGPDTATUS"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("type", context.contextMap.get("type"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit/creditVouchShow.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getAllCieditVouchManage(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("creditVoucher.allVouch",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			if(dw != null){
				List rs = (List) dw.getRs() ;
				for(int i=0;i < rs.size();i++){
					Map temp = (Map) rs.get(i) ;
					if(temp.get("PDVP_ID") != null){
						temp.put("LAST_PRICE",CreditVouchManage.VOUCHPLANBYLASTPRICE(temp.get("NAME").toString(),temp.get("CODE").toString(),Integer.parseInt(temp.get("TYPE").toString()))) ;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("担保人授信管理页错误!请联系管理员") ;
		}
		outputMap.put("dw", dw);
		outputMap.put("QSTARTMONEY", context.contextMap.get("QSTARTMONEY"));
		outputMap.put("QENDMONEY", context.contextMap.get("QENDMONEY"));
		outputMap.put("QCUGPDTATUS", context.contextMap.get("QCUGPDTATUS"));
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("type", context.contextMap.get("type"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit/creditVouchShowManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 进入授信管理页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getProductCreditJsp(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map product = null;		
		Map grantplan = null;	
		double last_price=0.0;
		DataWrap grantdetail = null;
		//List supplier = null;
	//	List<Map> unionGrantPlan = null;
		if (errList.isEmpty()) {		
			try {		
			//	supplier = (List) DataAccessor.query("creditVouch.querySuppliersWithNoThis", context.contextMap, DataAccessor.RS_TYPE.LIST);
				product = (Map) DataAccessor.query("creditVouch.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grantplan =  (Map)DataAccessor.query("creditVouch.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);
			//	unionGrantPlan = (List<Map>) DataAccessor.query("creditVouch.queryUnionGrantPlan", context.contextMap, DataAccessor.RS_TYPE.LIST);
				last_price=CreditVouchManage.VOUCHPLANBYLASTPRICE(context.contextMap.get("VOUCH_NAME").toString(),context.contextMap.get("VOUCH_CODE").toString(),Integer.parseInt(context.contextMap.get("TYPE").toString()));
				if(grantplan!=null)
				{   
					grantplan.put("LAST_PRICE", last_price);
					context.contextMap.put("PDVP_ID", grantplan.get("PDVP_ID"));
					//grantdetail =  (DataWrap)DataAccessor.query("creditVouch.queryAllGrantdetailById", context.contextMap,DataAccessor.RS_TYPE.PAGED);	
				}
				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("com.brick.product.service.getProductCreditJsp"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add("授信添加页错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			//outputMap.put("unionGrantPlan", unionGrantPlan);
		//	outputMap.put("supplier", supplier);
			outputMap.put("product", product);
			outputMap.put("grantplan", grantplan);
			outputMap.put("grantdetail", grantdetail);
			Output.jspOutput(outputMap, context,"/credit/createProductVouch.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 添加日志
	 * @param paramMap
	 * @param ProductConstants
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void productcreditlog(SqlMapClient sqlMapper ,Map paramMap,Integer ProductConstants) throws Exception {
		String memo = CreditVouchManage.createDemo(paramMap, ProductConstants);
		paramMap.put("MEMO", memo);
		paramMap.put("GRANT_PRICE", DataUtil.floatUtil(paramMap.get("GRANT_PRICE")));
		paramMap.put("LAST_PRICE", DataUtil.floatUtil(paramMap.get("LAST_PRICE")));
		// 添加授信日志
		paramMap.put("CUGL_STATUS", ProductConstants);//ProductConstants.CUGL_STATUS_MODIFY);
		sqlMapper.insert("creditVouch.productcreditlog", paramMap);
		
	}
	public static String createDemo(Map paramMap,Integer ProductConstants) throws Exception {
		String memo = "";
		if(ProductConstants == 5)
		{
			Map result = (Map)DataAccessor.query("creditVouch.queryAllGrantplanById", paramMap, DataAccessor.RS_TYPE.MAP);
			String newCUGP_CODE = (String)paramMap.get("CUGP_CODE");
			String oldCUGP_CODE= (String)result.get("CUVP_CODE");
			if(newCUGP_CODE.equals(oldCUGP_CODE))
			{
				memo += "协议编号未修改！" ;
			}
			else
			{
				memo += "协议编号从"+oldCUGP_CODE+"修改为"+newCUGP_CODE+"！" ;
			}
			String newGRANT_PRICE = paramMap.get("GRANT_PRICE").toString();
			String oldGRANT_PRICE= result.get("GRANT_PRICE").toString();
			if(newGRANT_PRICE.equals(oldGRANT_PRICE))
			{
				memo += "授信金额未修改！" ;
			}
			else
			{
				memo += "授信金额从"+oldGRANT_PRICE+"修改为"+newGRANT_PRICE+"！" ;
			}
			
			String newLAST_PRICE = (String)paramMap.get("LAST_PRICE");
			String oldLAST_PRICE= (String)result.get("LAST_PRICE").toString();
			if(newLAST_PRICE.equals(oldLAST_PRICE))
			{
				memo += "授信余额未修改！" ;
			}
			else
			{
				memo += "授信余额从"+oldLAST_PRICE+"修改为"+newLAST_PRICE+"！" ;
			}
			
			String newSTART_DATE = (String)paramMap.get("START_DATE");
			String oldSTART_DATE= (String)result.get("START_DATE").toString();
			if(newSTART_DATE.equals(oldSTART_DATE))
			{
				memo += "起始日期未修改！" ;
			}
			else
			{
				memo += "起始日期从"+oldSTART_DATE+"修改为"+newSTART_DATE+"！" ;
			}
			
			String newEND_DATE = (String)paramMap.get("END_DATE");
			String oldEND_DATE= (String)result.get("END_DATE").toString();
			if(newCUGP_CODE.equals(oldCUGP_CODE))
			{
				memo += "结束日期未修改！" ;
			}
			else
			{
				memo += "结束日期从"+oldEND_DATE+"修改为"+newEND_DATE+"！" ;
			}
			
		}
		else if(ProductConstants == 4)
		{
			memo += "删除记录";
		}
		else if(ProductConstants == 3)
		{
			memo += "取消授信";
		}
		else if(ProductConstants == 0)
		{
			memo += "添加授信";
		}
		return memo ;
	}
	
	/**
	 * 添加客户授信、授信方案
	 * @param context
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void creatProductCreditPlan(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map product = null;		
		Map grant = null;		
		Map grantplan = null;		
//		Map result = null ;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {		
			try {
				sqlMapper.startTransaction() ;
				product = (Map) DataAccessor.query("creditVouch.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grant =  (Map)DataAccessor.query("creditVouch.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);		
//				outputMap.put("payInfor", DictionaryUtil.getDictionary("支付方式"));
				context.contextMap.put("CUGP_STATUS", 0);
				if(grant == null)
				{
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MOFIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					sqlMapper.insert("creditVouch.creatProductCreditPlan", context.contextMap);
//					result = (Map)DataAccessor.query("creditVouch.queryAllGrantplanById", context.contextMap, DataAccessor.RS_TYPE.MAP);		
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					this.productcreditlog(sqlMapper,context.contextMap, ProductConstants.CUGL_STATUS_CREATE);
				}
				else
				{
					context.contextMap.put("MODITY_ID", context.request.getSession().getAttribute("s_employeeId"));
					context.contextMap.put("PRODUCT_ID", context.contextMap.get("PRODUCT_ID"));
					context.contextMap.put("REPEAT_CREDIT", context.request.getParameter("REPEAT_CREDIT"));
					
					sqlMapper.update("creditVouch.updateProductCreditPlan", context.contextMap);
					//添加日志
					context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					context.contextMap.put("MODITY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
					this.productcreditlog(sqlMapper,context.contextMap, ProductConstants.CUGL_STATUS_MODIFY);
				}
				
//				//联合授信
//				if(DataUtil.intUtil(context.contextMap.get("UNION_CREDIT"))==1){
//					sqlMapper.insert("creditVouch.insertUnion01", context.contextMap);
//					//DataAccessor.execute("productCredit.insertUnion02", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
//				}
								
				sqlMapper.commitTransaction() ;
			} catch (Exception e) {
				e.printStackTrace();
				errList.add("creatProductCreditPlan 出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add("授信添加错误!请联系管理员");
			} finally{
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("product", product);
			outputMap.put("grantplan", grantplan);
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditVouchManage.getAllCieditVouchManage");
//			Output.jspOutput(outputMap, context,"/customercredit/createCustCredit.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 查询日志
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryLog(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;	
		Map product = null ;
		Map grant = null ;
		double last_price=0.0;
		List<Map> unionGrantPlan = null;
		if (errList.isEmpty()) {		
			try {
			//	unionGrantPlan = (List<Map>) DataAccessor.query("creditVouch.queryUnionGrantPlan", context.contextMap, DataAccessor.RS_TYPE.LIST);
				product = (Map) DataAccessor.query("creditVouch.queryAllproductCredit", context.contextMap, DataAccessor.RS_TYPE.MAP);
				grant =  (Map)DataAccessor.query("creditVouch.queryAllGrantplanById", context.contextMap,DataAccessor.RS_TYPE.MAP);
				dw = (DataWrap) DataAccessor.query("creditVouch.queryLog", context.contextMap,DataAccessor.RS_TYPE.PAGED);		
//				outputMap.put("custInfor", DictionaryUtil.getDictionary("承租人级别")); 
				last_price=CreditVouchManage.VOUCHPLANBYLASTPRICE(context.contextMap.get("VOUCH_NAME").toString(),context.contextMap.get("VOUCH_CODE").toString(),Integer.parseInt(context.contextMap.get("TYPE").toString()));
				if(grant!=null){
					grant.put("LAST_PRICE", last_price);
				}
			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
		//	outputMap.put("unionGrantPlan", unionGrantPlan);
			outputMap.put("product", product);
			outputMap.put("grantplan", grant);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
//			Output.jspSendRedirect(context,"defaultDispatcher?__action=custCredit.queryAllCustCredit");
			Output.jspOutput(outputMap, context,"/credit/showlog.jsp"); 
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 取消授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delProductCreditPlan(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;	
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		if (errList.isEmpty()) {		
			try {
				sqlMapper.startTransaction() ;
				sqlMapper.update("creditVouch.delCreditPlan", context.contextMap);
				
				//sqlMapper.update("creditVouch.delUnionCreditPlan01", context.contextMap);
//				DataAccessor.execute("creditVouch.delCreditPlan", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//				
//				DataAccessor.execute("creditVouch.delUnionCreditPlan01", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				//DataAccessor.execute("creditVouch.delUnionCreditPlan02", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				
				//添加日志
				context.contextMap.put("CREATE_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				context.contextMap.put("MODIFY_ID", DataUtil.intUtil(context.request.getSession().getAttribute("s_employeeId")));
				this.productcreditlog(sqlMapper,context.contextMap, ProductConstants.CUGL_STATUS_DEL);
				sqlMapper.commitTransaction() ;
			} catch (Exception e) {
				errList.add("取消客户授信出错"+ e.toString());
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally{
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=creditVouchManage.getAllCieditVouchManage");
		} else {
			// 跳转到错误页面
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 进入联合授信修改页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getUnionByID(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap = null;
		
		if (errList.isEmpty()) {
			try {
				unionMap = (Map) DataAccessor.query("creditVouch.getUnionByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("进入联合授信修改页面错误!请联系管理员");
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("unionMap", unionMap);
			Output.jsonOutput(outputMap, context);
		} else {
			
		}
	}
	
	/**
	 * 修改联合授信
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateUnion(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap =null;
		SqlMapClient sqlMapper=DataAccessor.getSession();
		if (errList.isEmpty()) {
			try {
//				DataAccessor.getSession().startTransaction();
//				DataAccessor.getSession().update("creditVouch.updateUnionPriceByID",context.contextMap);
				sqlMapper.startTransaction();
				sqlMapper.update("creditVouch.updateUnionPriceByID", context.contextMap);
//				context.contextMap.put("PUVP_ID", context.contextMap.get("UNION_ID"));
//				unionMap = (Map) DataAccessor.getSession().queryForObject("productCredit.getUnionByID",context.contextMap);
//				context.contextMap.put("UNION_SUPPLIER_ID", unionMap.get("UNION_SUPPLIER_ID"));
//				context.contextMap.put("PRODUCT_ID", unionMap.get("PRODUCT_ID"));
//				DataAccessor.getSession().update("productCredit.updateUnionPriceByID02",context.contextMap);
				//DataAccessor.getSession().commitTransaction();
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally{
				try {
					//DataAccessor.getSession().endTransaction();
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add("修改联合授信错误!请联系管理员");
				}
			}
		}
		if (errList.isEmpty()){
			int id = DataUtil.intUtil(context.contextMap.get("PRODUCT_ID"));
			int type = DataUtil.intUtil(context.contextMap.get("TYPE"));
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=creditVouchManage.getProductCreditJsp&PRODUCT_ID="+id+"&TYPE="+type);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 取消联合授信
	 * @param context
	 */
	public void delUnionPrice(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map unionMap =null;
		
		SqlMapClient sqlMapper=DataAccessor.getSession();
		
		if (errList.isEmpty()) {
			try {
				//DataAccessor.getSession().startTransaction();
				sqlMapper.startTransaction();
				context.contextMap.put("PUVP_ID", context.contextMap.get("DEL_UNION_ID"));
//				unionMap = (Map) DataAccessor.getSession().queryForObject("productCredit.getUnionByID",context.contextMap);
//				context.contextMap.put("UNION_SUPPLIER_ID", unionMap.get("UNION_SUPPLIER_ID"));
//				context.contextMap.put("PRODUCT_ID", unionMap.get("PRODUCT_ID"));
				
				//DataAccessor.getSession().update("creditVouch.delUnionPriceByID",context.contextMap);
				sqlMapper.update("creditVouch.delUnionPriceByID", context.contextMap);
				
//				DataAccessor.getSession().update("productCredit.delUnionPriceByID02",context.contextMap);
				//DataAccessor.getSession().commitTransaction();
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add("取消联合授信错误!请联系管理员");
			} finally{
				try {
					//DataAccessor.getSession().endTransaction();
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if (errList.isEmpty()){
			int id = DataUtil.intUtil(context.contextMap.get("PRODUCT_ID"));
			int type = DataUtil.intUtil(context.contextMap.get("VOUCH_TYPE"));
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=creditVouchManage.getProductCreditJsp&PRODUCT_ID="+id+"&TYPE="+type);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 根据担保人的id查询对应的所有的合同
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllCieditByVouch (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List creditlist = null;
        
		if(errList.isEmpty()){		
			try {
				//找到所有的合同的信息，包括合同的id
				if(context.contextMap.get("type").toString().equals("1")){
					creditlist = (List) DataAccessor.query("creditVoucher.findCreditInfoByDanbaorenId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				}else if(context.contextMap.get("type").toString().equals("0")){
					creditlist = (List) DataAccessor.query("creditVoucher.findCreditInfoByDanbaorenNatuId", context.contextMap, DataAccessor.RS_TYPE.LIST);	
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("担保人授信管理--查询担保人对应报告错误!请联系管理员");
			}
		}
		outputMap.put("creditlist", creditlist);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/credit/creditpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	
	//查询出当前担保人授信余额（需要三个参数1：名称 2：证件号码 3：担保人类型）
	public static double VOUCHPLANBYLASTPRICE(String name,String vouchcode,int vouch_type)
	{
		Double lastPrice=0.0;
		Map vouchPlanManager=new HashMap();
		vouchPlanManager.put("name", name);
		vouchPlanManager.put("vouchcode", vouchcode);
		vouchPlanManager.put("ficbItem", "租金");
		try
		{
			
			if(vouch_type==0)//自然人
			{
				lastPrice = (Double) DataAccessor.query("creditVoucher.getLastPriceByName_Code_Natu",vouchPlanManager , DataAccessor.RS_TYPE.OBJECT);
			}
			else//法人
			{
				lastPrice = (Double) DataAccessor.query("creditVoucher.getLastPriceByName_Code_Corp",vouchPlanManager , DataAccessor.RS_TYPE.OBJECT);
			}
			
			if(lastPrice == null ){
				lastPrice = 0.0 ;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return lastPrice;
	}
	
}
