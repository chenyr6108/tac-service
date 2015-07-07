package com.brick.support.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


/**
 * @date 2010 7 27
 * @author cheng
 * @version 1.0
 */
public class SupportPaylist extends AService {
	Log logger = LogFactory.getLog(SupportPaylist.class);

    
    /**
     * 根据支付表号 查询其关联信息，版本，分解状态，等
     * 以供查看
     */
    @SuppressWarnings("unchecked")
    public void queryPaylistInfo(Context context){
	List errList = context.getErrList();
	Map outputMap =new HashMap();
	
	String  payCode = HTMLUtil.getStrParam(context.getRequest(), "num", "1");
	Map payMap = new HashMap();
	List payList =new ArrayList();
	Map paramMap = new HashMap();
	
	paramMap.put("PAYCODE", payCode);
	
	if(errList.isEmpty()){
               try{
                	
                     if(!(payCode.equals("1"))){
                        	    
                            payList=  DataAccessor.getSession().queryForList("supportPaylist.queryInfoBycode", paramMap);
                            
                            if(payList.size()>0){
                        	       
                                	     //  for(int i=0;i<payList.size();i++){
                                		   
                                		   payMap = (Map) payList.get(0);
                                        		   if(!"".equals(payMap.get("STATUS"))&payMap.get("STATUS") != null){
                                        		       
                                                		   if(payMap.get("STATUS").equals("0") ){
                                                		       payMap.put("STATUS","正常" );
                                                		   }
                                                		   if(payMap.get("STATUS").equals("-1") ){
                                                		       payMap.put("STATUS","作废" );
                                                		   }
                                                		   if(payMap.get("STATUS").equals("-2") ){
                                                		       payMap.put("STATUS","删除" );
                                                		   }
                                        		   }
                                        		   if(!"".equals(payMap.get("ASSET_STATUS"))&payMap.get("ASSET_STATUS") != null){
                                        		       
                                                		   if(payMap.get("ASSET_STATUS").equals("0") ){
                                                		       payMap.put("ASSET_STATUS","正常" );
                                                		   }
                                                		   if(payMap.get("ASSET_STATUS").equals("1") ){
                                                		       payMap.put("ASSET_STATUS","融资" );
                                                		   }
                                                		   if(payMap.get("ASSET_STATUS").equals("2") ){
                                                		       payMap.put("ASSET_STATUS","保理" );
                                                		   }
                                        		   }   
                                        		   if(!"".equals(payMap.get("FUND_STATUS"))&payMap.get("FUND_STATUS") != null){
                                        		       
                                                		   if(payMap.get("FUND_STATUS").equals("0") ){
                                                		       payMap.put("FUND_STATUS","正常" );
                                                		   }
                                                		   if(payMap.get("FUND_STATUS").equals("1") ){
                                                		       payMap.put("FUND_STATUS","正常结清" );
                                                		   }
                                                		   if(payMap.get("FUND_STATUS").equals("2") ){
                                                		       payMap.put("FUND_STATUS","提前结清" );
                                                		   }
                                                		   if(payMap.get("FUND_STATUS").equals("3") ){
                                                		       payMap.put("FUND_STATUS","回购" );
                                                		   }
                                        		   } 
                                        		   if(!"".equals(payMap.get("WARN_STATUS"))&payMap.get("WARN_STATUS") != null){
                                        		       
                                                		   if(payMap.get("WARN_STATUS").equals("0") ){
                                                		       payMap.put("WARN_STATUS","正常" );
                                                		   }
                                                		   
                                                		   if(payMap.get("WARN_STATUS").equals("2") ){
                                                		       payMap.put("WARN_STATUS","转法务" );
                                                		   }
                                                		   
                                        		   } 
                                        		   if(!"".equals(payMap.get("REDUCE_OWN_PRICE"))&payMap.get("REDUCE_OWN_PRICE") != null){
                                                		 
                                                		       payMap.put("REDUCE_OWN_PRICE","已有分解资金" );
                                                		  
                                                		  
                                                	
                                        		   }else{
                                        		       payMap.put("REDUCE_OWN_PRICE","没有分解资金" );
                                        		   }
                                        		       
                                        		   if(!"".equals(payMap.get("LOCKED"))&payMap.get("LOCKED") != null){
                                        		       
                                                    		   if(payMap.get("LOCKED").equals("0") ){
                                                    		       payMap.put("LOCKED","等额" );
                                                    		   }
                                                    		   if(payMap.get("LOCKED").equals("1") ){
                                                    		       payMap.put("LOCKED","不等额" );
                                                    		   }
                                                    	
                                        		   }
                                        		   if(!"".equals(payMap.get("RECPSYMBOL"))&payMap.get("RECPSYMBOL") != null){
                                        		       
                                                		   
                                                		       payMap.put("RECPSYMBOL","已有分解资金" );
                                                		   
                                                	
                                        		   }else{
                                        		       payMap.put("REDUCE_OWN_PRICE","没有分解资金" );
                                        		   }
                                	      // RECPSYMBOL }
                        	       
                                	   }else{
                                	       payMap.put("msg", "根据支付表号查询无结果，请查看支付表号是否正确");
                                	   }
                            
                        	   
                        	}else{
                        	    
                        	    payMap.put("msg", "后台没有得到支付表号数据，请刷新页面 或退出系统重试");
                        	}
        	    }
        	    catch (Exception e) {
        		 errList.add("根据支付表号查询"+ e.getMessage());
        		 e.printStackTrace();
        		 LogPrint.getLogStackTrace(e, logger);
 				errList.add(e);
        	    }
        	   
	}
	if(errList.isEmpty()){
	    outputMap.put("payList", payList);
	    outputMap.put("payMap", payMap);
	    Output.jsonOutput(outputMap, context);
	    
	}else{
	    	outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
	
    }
    
 

	/**
	 * 根据支付表号删除支付表起租日期 支付表号 RECP_CODE 
	 */
	@SuppressWarnings("unchecked")
	public void deleteDate(Context context) {
		List errList = context.getErrList();
		Map outputMap = new HashMap();

		String payCode = HTMLUtil.getStrParam(context.getRequest(), "num", "1");
		Map payMap = new HashMap();
		List payList = new ArrayList();
		Map paramMap = new HashMap();

		paramMap.put("PAYCODE", payCode);
		paramMap.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));

		if (errList.isEmpty()) {
			try {

				if (!(payCode.equals("1"))) {

					payList = DataAccessor.getSession().queryForList(
							"supportPaylist.queryInfoBycode", paramMap);
					if (payList.size() > 0) {
						
						payMap = (Map) payList.get(0);
						paramMap.put("RECPID", payMap.get("ID"));
						DataAccessor.getSession().startTransaction();

						DataAccessor.getSession().update(
								"supportPaylist.delDate", paramMap);
						DataAccessor.getSession().update(
								"supportPaylist.delAttachDate", paramMap);
						DataAccessor.getSession().commitTransaction();

					}

				} else {

					payMap.put("msg", "支付表号为空请刷新页面重试");
				}
			} catch (Exception e) {
				errList.add("根据支付表号删除支付表起租日期出错" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {

					errList.add("根据支付表号删除支付表起租日期事务出错" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}

			}
		}
		if (errList.isEmpty()) {
			outputMap.put("payList", payList);
			outputMap.put("payMap", payMap);
			Output.jsonOutput(outputMap, context);

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	/**
	 * PAYDATE_FLAG 日期标识 修改 Sql changeDateIdentification
	 */
	@SuppressWarnings("unchecked")
	public void changeDateIdentification(Context context) {
		List errList = context.getErrList();
		Map outputMap = new HashMap();

		String payCode = HTMLUtil.getStrParam(context.getRequest(), "num", "1");
		Map payMap = new HashMap();
		List payList = new ArrayList();
		Map paramMap = new HashMap();

		paramMap.put("PAYCODE", payCode);
		paramMap.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));

		if (errList.isEmpty()) {
			try {

				if (!(payCode.equals("1"))) {

					payList = DataAccessor.getSession().queryForList(
							"supportPaylist.queryInfoBycode", paramMap);
					if (payList.size() > 0) {

						payMap = (Map) payList.get(0);
						paramMap.put("RECPID", payMap.get("ID"));
						DataAccessor.getSession().startTransaction();

						DataAccessor.getSession().update(
								"supportPaylist.changeDateIdentification",
								paramMap);

						DataAccessor.getSession().commitTransaction();

					}

				} else {

					payMap.put("msg", "支付表号为空请刷新页面重试");
				}
			} catch (Exception e) {
				errList.add("根据支付表号修改日期标识出错" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {

					errList.add("根据支付表号修改日期标识事务出错" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("payList", payList);
			outputMap.put("payMap", payMap);
			Output.jsonOutput(outputMap, context);

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	/**
	 * 根据支付表号删除支付表 支付表相关的保险费，其他费用，融资租赁还款计划，融资租赁方案，备注信息,设备列表
	 */
	@SuppressWarnings("unchecked")
	public void delpayList(Context context) {
		List errList = context.getErrList();
		Map outputMap = new HashMap();

		String payCode = HTMLUtil.getStrParam(context.getRequest(), "num", "1");
		Map payMap = new HashMap();
		List payList = new ArrayList();
		Map paramMap = new HashMap();
		Map paramMap2 = new HashMap();
		List paramList = new ArrayList();
		paramMap.put("PAYCODE", payCode);
		paramMap.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));

		if (errList.isEmpty()) {
			try {

				if (!(payCode.equals("1"))) {

					payList = DataAccessor.getSession().queryForList(
							"supportPaylist.queryInfoBycode", paramMap);

					if (payList.size() > 0) {

						payMap = (Map) payList.get(0);
						paramMap.put("RECPID", payMap.get("ID"));

						paramList = DataAccessor.getSession().queryForList(
								"supportPaylist.selectRentContractdetail",
								paramMap);
						DataAccessor.getSession().startTransaction();

						DataAccessor.getSession().update(
								"supportPaylist.delpaylist", paramMap);

						DataAccessor.getSession().update(
								"supportPaylist.delCollectiondetail", paramMap);
						if (paramList.size() > 0) {
							for (int i = 0; i < paramList.size(); i++) {

								paramMap2 = (Map) paramList.get(i);
								// REDN_ID
								paramMap2.put("EMPLOYEEID", context.contextMap
										.get("s_employeeId"));
								paramMap2.put("MEMO", paramMap2.get("MEMO")
										+ " ");
								paramMap2.put("REDN_ID", "");
								DataAccessor
										.getSession()
										.insert(
												"supportPaylist.createRentContractDetail",
												paramMap2);
							}

						}

						DataAccessor.getSession().update(
								"supportPaylist.delCollectioninsure", paramMap);
						DataAccessor.getSession().update(
								"supportPaylist.delContractdetail", paramMap);
						DataAccessor.getSession().update(
								"supportPaylist.delCollectionOtherfee",
								paramMap);
						DataAccessor.getSession().update(
								"supportPaylist.delCollectionRemark", paramMap);
						DataAccessor.getSession().commitTransaction();

					}

				} else {

					payMap.put("msg", "支付表号为空请刷新页面重试");
				}
			} catch (Exception e) {
				errList.add("根据支付表号删除支付表 出错" + e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (SQLException e) {

					errList.add("根据支付表号删除支付表 事务出错" + e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("payList", payList);
			outputMap.put("payMap", payMap);
			Output.jsonOutput(outputMap, context);

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}

	/**
	 * 删除分解单
	 */
	@SuppressWarnings("unchecked")
	public void delFinaCllaction(Context context) {
		Map outputMap = new HashMap();
		List decomposeBillList = null;
		List cllactionBillList = null;
		String msg = "";
		try {
			decomposeBillList = (List) DataAccessor.query(
					"decompose.queryItemMoney", context.contextMap,
					DataAccessor.RS_TYPE.LIST);

			cllactionBillList = (List) DataAccessor.query(
					"supportPaylist.queryFina", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			if (!decomposeBillList.isEmpty()) {
				msg="删除成功";
				// 更新来款
				DataAccessor.execute("supportPaylist.delFinaIncome",
						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);

				for (int i = 0; i < cllactionBillList.size(); i++) {
					Map map = (Map) cllactionBillList.get(i);
					context.contextMap.put("ficb_id", Integer.parseInt(map.get(
							"FICB_ID").toString()));
					// 更新分解单
					DataAccessor.execute("supportPaylist.delFina", map,
							DataAccessor.OPERATION_TYPE.UPDATE);
				}

				String ficb_item = "";
				if (decomposeBillList != null) {
					for (int i = 0; i < decomposeBillList.size(); i++) {
						ficb_item = ((Map) decomposeBillList.get(i)).get(
								"FICB_ITEM").toString();
						context.contextMap.put("real_price", Double
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"REAL_PRICE").toString()));
						context.contextMap.put("recp_id", Integer
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"RECP_ID").toString()));
						context.contextMap.put("period_num", Integer
								.valueOf(((Map) decomposeBillList.get(i)).get(
										"RECD_PERIOD").toString()));
						if (ficb_item.equals("本金")) {
							DataAccessor.execute("decompose.updateOwnPrice",
									context.contextMap,
									DataAccessor.OPERATION_TYPE.UPDATE);
						} else if (ficb_item.equals("利息")) {
							DataAccessor.execute("decompose.updateRenPrice",
									context.contextMap,
									DataAccessor.OPERATION_TYPE.UPDATE);
						} else if (ficb_item.equals("费用")) {
							DataAccessor.execute("decompose.updateOtherPrice",
									context.contextMap,
									DataAccessor.OPERATION_TYPE.UPDATE);
						}
					}
				}
			}else {
				msg="对不起没有该来款ID";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("msg", msg);
		Output.jsonOutput(outputMap, context);
	}
    /**
     * 删除合同备注信息
     * rentContractPact.selectMemo 
     *     先查询，后选择删除   从合同号查找到  T_PRJT_CREDIT资信ID，查找合同备注T_RENT_CONTRACTREMARK
     */
    @SuppressWarnings("unchecked")
    public void selectRentcontractMemo(Context context){
	Map outputMap =new HashMap();
	List errList = context.getErrList();
	
	List memoList = new ArrayList();
	String param = HTMLUtil.getStrParam(context.getRequest(),"num","0");
	Map paramMap=new HashMap();
	paramMap.put("CODE", param);
	
	
	if(errList.isEmpty()){
	    
	    try {
		
		memoList= DataAccessor.getSession().queryForList("supportPaylist.selectContractmemo", paramMap);
		
	    } catch (SQLException e) {
		
		errList.add("根据合同号 查找合同备注信息 出错"+e.getMessage());
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    }
	    
	}
	
	
	if(errList.isEmpty()){
	    
	    outputMap.put("memoList", memoList);
	    Output.jsonOutput(outputMap, context);
	    
	}else{
	    outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
    }
    
    
    @SuppressWarnings("unchecked")
    public void delRentContractMemo(Context context){
	Map outputMap =new HashMap();
	List errList = context.getErrList();
	
	List memoList = new ArrayList();
	String paramStr = HTMLUtil.getStrParam(context.getRequest(),"num","0");
	
	String[] param =paramStr.split(",");
	Map paramMap=new HashMap();
	paramMap.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));
	
	if(errList.isEmpty()){
	    
	    	try {	
		
			if(param.length>0){
			    
        			    DataAccessor.getSession().startTransaction();
        			    
                			    for(int i=0;i<param.length;i++){
                				paramMap.put("RECRID", param[i]);
                				
                				DataAccessor.getSession().update("supportPaylist.delContractmemo", paramMap);
                			    }
        			    
        			    DataAccessor.getSession().commitTransaction();
        			    memoList.add("操作成功！！");
			    
			}else{
			    memoList.add("后台程序没有得到前台参数，请刷新页面重试");
			}
        		
        	    } catch (Exception e) {
        		
        		errList.add("删除合同备注信息 出错"+e.getMessage());
        		e.printStackTrace();
        		LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
        	    }finally{
        		
        		try {
        		    DataAccessor.getSession().endTransaction();
        		    
        		} catch (SQLException e) {
        		    
        		    errList.add("删除  合同备注信息 事务出错"+e.getMessage());
        		    e.printStackTrace();
        		    LogPrint.getLogStackTrace(e, logger);
    				errList.add(e);
        		}
        	    }
        	    
		}
	
	
	if(errList.isEmpty()){
	 
	    outputMap.put("memoList", memoList);
	    Output.jsonOutput(outputMap, context);
	    
	}else{
	    outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
	
	
    }
    

    /**
     * 查找合同承租人信息
     * 然后修改
     *     
     */
    @SuppressWarnings("unchecked")
    public void selectRentcontractCustomer(Context context){
	Map outputMap =new HashMap();
	List errList = context.getErrList();
	
	List customerList = new ArrayList();
	String param = HTMLUtil.getStrParam(context.getRequest(),"num","0");
	Map paramMap=new HashMap();
	paramMap.put("CODE", param);
	
	
	if(errList.isEmpty()){
	    
	    try {
		
		customerList= DataAccessor.getSession().queryForList("supportPaylist.selectRentcontractCustomer", paramMap);
		
		if(customerList.size()>0){
		    
		    paramMap = (Map) customerList.get(0);
		}else{
		    paramMap.clear();
		    paramMap.put("NAME2", "没有找到结果，请确定输入无误。");
		}
		
	    } catch (SQLException e) {
		
		errList.add("根据合同号 查找合同承租人  出错"+e.getMessage());
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    }
	}
	if(errList.isEmpty()){
	    
	    outputMap.put("paramMap", paramMap);
	    Output.jsonOutput(outputMap, context);
	    
	}else{
	    outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
    }
    /**
     * 合同承租人信息
     * 修改
     *     
     */
    @SuppressWarnings("unchecked")
    public void changeRentcontractCustomer(Context context){
	Map outputMap =new HashMap();
	List errList = context.getErrList();
	
	String param = HTMLUtil.getStrParam(context.getRequest(),"num","0");
	String id = HTMLUtil.getStrParam(context.getRequest(),"id","0");
	
	Map paramMap=new HashMap();
	
	paramMap.put("EMPLOYEEID", context.contextMap.get("s_employeeId"));
	paramMap.put("CUST_ID", id );
	paramMap.put("PARAM", param );
	
	if(errList.isEmpty()){
	    
	try {
	    
		DataAccessor.getSession().startTransaction();
		DataAccessor.getSession().update("supportPaylist.updateCustomername", paramMap);
		
		DataAccessor.getSession().update("supportPaylist.updateCreditname", paramMap);
		
		DataAccessor.getSession().commitTransaction();
	    } catch (SQLException e) {
		
		errList.add("修改合同承租人 出错"+e.getMessage());
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
		errList.add(e);
	    }finally{
		try {
		    DataAccessor.getSession().endTransaction();
		} catch (SQLException e) {
		    errList.add("修改合同承租人 事务  出错"+e.getMessage());
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	}
	
	if(errList.isEmpty()){
	    outputMap.put("paramMap", paramMap);
	    Output.jsonOutput(outputMap, context);
	    
	}else{
	    outputMap.put("errList", errList);
	    Output.jspOutput(outputMap, context, "/error.jsp");
	}
    }
}
