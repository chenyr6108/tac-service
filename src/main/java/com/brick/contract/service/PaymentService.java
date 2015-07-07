package com.brick.contract.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.baseManage.service.BusinessLog;
import com.brick.coderule.service.CodeRule;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 付款单
 * @author kangk
 * @date Jun 22, 2010
 * @version 
 */
public class PaymentService extends AService {
	Log logger = LogFactory.getLog(PaymentService.class);
	@Override
	protected void afterExecute(String action, Context context) {
		if ("paymentService.submitPayment".equals(action)) {
			Long creditId = null;
			Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
			
			String logType = "融资租赁合同付款单";
			String logTitle = "生成";
			String logCode = String.valueOf(context.contextMap.get("PUPL_CODE"));
			String memo = "融资租赁合同生成付款单";
			int state = 1;
			Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
			Long otherId = null;
			
			BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle, logCode, memo, state, userId, otherId, (String)context.contextMap.get("IP"));
		}
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	/**
	 * 查询租赁合同对应的设备
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectEquipment(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List equipList = null;
		Map rentContract = null;
		if(errList.isEmpty()) {
			try {
				equipList = (List)DataAccessor.query("payment.getEquipByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				rentContract = (Map)DataAccessor.query("payment.getRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("equipList", equipList);
			outputMap.put("rentContract", rentContract);
			Output.jspOutput(outputMap, context,"/payment/paymentEquipment.jsp");
		}		
	}
		
	/**
	 * 进入付款单页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getPaymentJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		String[] pucd_ids = HTMLUtil.getParameterValues(context.request,"pucd_id", "0");
		Map pucdMap = new HashMap();	
		List equipList = null;
		Map supplierMap = null;
		Map rateMap = null;
		String code = null;
		Long rectId = 0l;
		if(errList.isEmpty()) {
			try {				
				pucdMap.put("pucdIds", pucd_ids);					
				equipList = (List<Map>) DataAccessor.query("payment.getEquipByPucdId", pucdMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("pucd_id", pucd_ids[0]);
				supplierMap = (Map) DataAccessor.query("payment.getSupplierByPucdId", context.contextMap, DataAccessor.RS_TYPE.MAP);				
				rateMap = (Map) DataAccessor.query("payment.getRateByPucdId", context.contextMap, DataAccessor.RS_TYPE.MAP);				
				rectId = DataUtil.longUtil(rateMap.get("RECT_ID"));
				code = CodeRule.generatePayMentCode(rectId);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("code", code);
			outputMap.put("equipList", equipList);
			outputMap.put("supplierMap", supplierMap);
			outputMap.put("rateMap",rateMap);
			Output.jspOutput(outputMap, context,"/payment/payment.jsp");
		}
	}
	
	/**
	 * 提交付款单
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void submitPayment(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Long puplId = 0l;
		if(errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();				
				puplId = (Long) DataAccessor.getSession().insert("payment.insertPaymentLog", context.contextMap);			
				context.contextMap.put("PUPL_ID", puplId);			
				String[] pucdIds = HTMLUtil.getParameterValues(context.request,"pucd_id", "0");				
				int payCode = DataUtil.intUtil(context.contextMap.get("PAY_MODE"));
				float pirstPriceRate = DataUtil.floatUtil(context.contextMap.get("FIRST_PRICE_RATE"));
				float lastPriceRate = DataUtil.floatUtil(context.contextMap.get("LAST_PRICE_RATE"));
				context.contextMap.put("pucdIds",pucdIds);
				if (payCode == 1) {
					context.contextMap.put("PRICE_RATE",pirstPriceRate);
				} else if (payCode == 2) {
					context.contextMap.put("PRICE_RATE",lastPriceRate);
				}else if (payCode == 3) {
					context.contextMap.put("PRICE_RATE",100);
				}
				DataAccessor.getSession().insert("payment.insertDetail2payLog", context.contextMap);			
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {	
			//Output.jspOutput(outputMap, context,"/payment/paymentList.jsp");
			Output.jspSendRedirect(context,"../servlet/defaultDispatcher?__action=paymentService.showPaymentLog&FLAG=0&PUPL_ID="+puplId);
		}
	}
	
	
	/**
	 * 查看付款单
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showPaymentLog(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map paymentLogMap = null;
		List equipList = null;
		if(errList.isEmpty()) {
			try {	
				outputMap.put("FLAG", context.contextMap.get("FLAG"));
				Long pupl_id = DataUtil.longUtil(context.contextMap.get("PUPL_ID"));	 
				paymentLogMap = (Map)DataAccessor.query("payment.getPaymentLogByPuplID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				equipList = (List)DataAccessor.query("payment.getPaymentEqmtByPuplID", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("equipList", equipList);
			outputMap.put("paymentLogMap", paymentLogMap);
			Output.jspOutput(outputMap, context,"/payment/paymentList.jsp");
		}
	}
	/**
	 * 进去修改付款单
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatePaymentJsp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map paymentLogMap = null;
		List equipList = null;
		Map rateMap = null;
		if(errList.isEmpty()) {
			try {	
				Long pupl_id = DataUtil.longUtil(context.contextMap.get("PUPL_ID"));	 
				paymentLogMap = (Map)DataAccessor.query("payment.getPaymentLogByPuplID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				equipList = (List)DataAccessor.query("payment.getPaymentEqmtByPuplID", context.contextMap, DataAccessor.RS_TYPE.LIST);
				rateMap = (Map)DataAccessor.query("payment.getRateByPuplID", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("equipList", equipList);
			outputMap.put("paymentLogMap", paymentLogMap);
			outputMap.put("rateMap", rateMap);
			Output.jspOutput(outputMap, context,"/payment/paymentUpdate.jsp");
		}
	}
	/**
	 * 修改付款单
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatePayment(Context context) {
		List errList = context.errList;
		Long puplId = 0l;
		//Long price = 0l;
		if(errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();				
				DataAccessor.getSession().update("payment.updatePaymentLog", context.contextMap);	
				puplId = DataUtil.longUtil(context.contextMap.get("PUPL_ID"));
				context.contextMap.put("PUPL_ID", puplId);			
				String[] pucdIds = HTMLUtil.getParameterValues(context.request,"pucd_id", "1");	
				int payCode = DataUtil.intUtil(context.contextMap.get("PAY_MODE"));
				int pirstPriceRate = DataUtil.intUtil(context.contextMap.get("FIRST_PRICE_RATE"));
				int lastPriceRate = DataUtil.intUtil(context.contextMap.get("LAST_PRICE_RATE"));
				if (payCode == 1) {
					context.contextMap.put("PRICE_RATE",pirstPriceRate);
				} else if (payCode == 2) {
					context.contextMap.put("PRICE_RATE",lastPriceRate);
				}else if (payCode == 3) {
					context.contextMap.put("PRICE_RATE",100);
				}
				for(int i = 0 ; i < pucdIds.length ; i++){
					context.contextMap.put("PUCDID",pucdIds[i]);
					//price = DataUtil.longUtil(DataAccessor.getSession().queryForObject("payment.queryPrice", context.contextMap));					
					//Object price = (DataAccessor.getSession().queryForObject("payment.queryPrice", context.contextMap));
					//context.contextMap.put("PRICE",price);
					double price = (Double)DataAccessor.getSession().queryForObject("payment.queryPrice", context.contextMap);
					System.out.println("price="+price);
					context.contextMap.put("PRICE", price);
					DataAccessor.getSession().update("payment.updateDetail2payLog", context.contextMap);
				}
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {	
			//Output.jspOutput(outputMap, context,"/payment/paymentList.jsp");
			Output.jspSendRedirect(context,"../servlet/defaultDispatcher?__action=paymentService.showPaymentLog&FLAG=0&PUPL_ID="+puplId);
		}
	}
}
