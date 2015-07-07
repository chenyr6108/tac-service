package com.brick.decompose.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

public class DepositService extends AService {
	Log logger = LogFactory.getLog(DepositService.class);

	public static final Logger log = Logger.getLogger(DepositService.class);

	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		super.afterExecute(action, context);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}

	/**
	 * 查询已经分解了的承租人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryDepositInfo(Context context) {
		Map outputMap = new HashMap();
		List errorList = new ArrayList();
		errorList = context.errList;
		DataWrap depositListpage = null;
		String cust_name = null;
		if (context.contextMap.get("cust_name") == null) {
			cust_name = "";
		} else {
			cust_name = context.contextMap.get("cust_name").toString().trim();
		}
		context.contextMap.put("cust_name", cust_name);
		String recp_code = null;
		if (context.contextMap.get("recp_code") == null) {
			recp_code = "";
		} else {
			recp_code = context.contextMap.get("recp_code").toString().trim();
		}
		context.contextMap.put("recp_code", recp_code);
		String ss = context.request.getParameter("cardFlag");
//		System.out.println(ss);
		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (cardFlag == 0) {
			context.contextMap.put("deposit_status", 0);
		} else if (cardFlag == 1) {
			context.contextMap.put("deposit_status", 1);
		} else if (cardFlag == 2) {
			context.contextMap.put("deposit_status", 2);
		} else if (cardFlag == 3) {
			context.contextMap.put("deposit_status", 3);
		} else if (cardFlag == 4) {
			context.contextMap.put("deposit_status", -1);
		}

		if (errorList.isEmpty()) {
			try {
				depositListpage = (DataWrap) DataAccessor.query(
						"deposit.queryDepositInfo", context.contextMap,
						DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				log
						.error("com.brick.deposit.service.DepositService.queryDepositInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.deposit.service.DepositService.queryDepositInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询已经分解的承租人信息错误!请联系管理员");
			}
		}
		outputMap.put("cust_name", cust_name);
		outputMap.put("recp_code", recp_code);
		outputMap.put("deposit_status", context.contextMap
				.get("deposit_status"));
		outputMap.put("dw", depositListpage);
		outputMap.put("cardFlag", cardFlag);
		outputMap.put("__action", context.contextMap.get("__action"));
		if(errorList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showCustDeposit.jsp");
		} else {
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 将保证金冲抵租金--生成新的来款(冲抵)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void makeDepositToIncome(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		String operate_flag = "操作成功！";
		Integer deposit_status = 1;
		context.contextMap.put("deposit_status", deposit_status);
		context.contextMap.put("status", 1);
		SqlMapClient sqlMapper = null;
		if (errorList.isEmpty()) {
			try {
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				sqlMapper.update("deposit.updateDepositStatus",
						context.contextMap);
				sqlMapper.insert("deposit.insertDepositAsIncome",
						context.contextMap);
				sqlMapper.insert("deposit.insertFinaLogin", context.contextMap);
				// DataAccessor.execute("deposit.updateDepositStatus",
				// context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				// DataAccessor.execute("deposit.insertDepositAsIncome",
				// context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				// DataAccessor.execute("deposit.insertFinaLogin",
				// context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);

				sqlMapper.commitTransaction();
			} catch (Exception e) {
				log
						.error("com.brick.deposit.service.DepositService.makeDepositToIncome"
								+ e.getMessage());
				e.printStackTrace();
				operate_flag = "操作失败！";
				errorList
						.add("com.brick.deposit.service.DepositService.makeDepositToIncome"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("保证金冲抵错误!请联系管理员");
			} finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
		}
		outputMap.put("operateflag", operate_flag);
		if(errorList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}
	}

	/**
	 * 将保证金冲抵租金--生成新的来款(期末退回)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void makeDepositToEndback(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		String operate_flag = "操作成功！";
		Integer deposit_status = 3;
		context.contextMap.put("deposit_status", deposit_status);
		context.contextMap.put("status", 2);
		SqlMapClient sqlMapper = null;
		if (errorList.isEmpty()) {
			try {
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				sqlMapper.update("deposit.updateDepositStatus",
						context.contextMap);
				sqlMapper.insert("deposit.insertDepositAsEndback",
						context.contextMap);
				sqlMapper.insert("deposit.insertFinaLogin", context.contextMap);
				
//				DataAccessor.execute("deposit.updateDepositStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("deposit.insertDepositAsEndback",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
//				DataAccessor.execute("deposit.insertFinaLogin",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				log
						.error("com.brick.deposit.service.DepositService.makeDepositToEndback"
								+ e.getMessage());
				e.printStackTrace();
				operate_flag = "操作失败！";
				errorList
						.add("com.brick.deposit.service.DepositService.makeDepositToEndback"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errorList.add("保证金冲抵租金(期末退回)错误!请联系管理员");
				}
			}
		}
		outputMap.put("operateflag", operate_flag);
		if(errorList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}
	}

	/**
	 * 将保证金冲抵租金--生成新的来款(平均冲抵)
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void makeDepositToAverage(Context context) {
		Map outputMap = new HashMap();
		List errorList = null;
		errorList = context.errList;
		String operate_flag = "操作成功！";
		Integer deposit_status = 2;
		context.contextMap.put("deposit_status", deposit_status);
		context.contextMap.put("status", 3);
		SqlMapClient sqlMapper = null;
		if (errorList.isEmpty()) {
			try {
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				sqlMapper.update("deposit.updateDepositStatus",
						context.contextMap);
				Integer fiin_id = (Integer) sqlMapper.insert("deposit.insertDepositAsAverage",
						context.contextMap);
				sqlMapper.insert("deposit.insertFinaLogin", context.contextMap);
				// 查询支付表详细
				List recpList = (List) sqlMapper.queryForList("deposit.queryRecp", context.contextMap);
				double real_price = Double.parseDouble(context.contextMap.get("real_price").toString());
				int period_num = recpList.size();
				// 平均每期多少钱
				double price = real_price/period_num;
				
				for (int i = 0; i < recpList.size(); i++) {
					Map map = (Map) recpList.get(i);
					
					String recp_code = map.get("RECP_CODE").toString();
					String pay_date = map.get("PAY_DATE").toString();
					String cust_code = map.get("CUST_CODE").toString();
					String decompose_id = context.contextMap.get("s_employeeId").toString();
					String check_id = context.contextMap.get("s_employeeId").toString();
					
					map.put("recp_id", context.contextMap.get("recp_id"));
					map.put("recp_code", recp_code);
					map.put("pay_date", pay_date);
					map.put("recd_period", i+1);
					map.put("ficb_item", "平均冲抵");
					map.put("should_price", price);
					map.put("real_price", price);
					map.put("fiin_id", fiin_id);
					map.put("cust_code", cust_code);
					map.put("ficb_state", 5);
					map.put("ficb_type", 2);
					map.put("recd_type", 4);
					map.put("item_order", "098");
					map.put("decompose_id", decompose_id);
					map.put("check_id", check_id);
					map.put("check_date", 2);
					
					sqlMapper.insert("deposit.addDecomposeBill", map);
				}
				
//				DataAccessor.execute("deposit.updateDepositStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("deposit.insertDepositAsAverage",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				log
						.error("com.brick.deposit.service.DepositService.makeDepositToEndback"
								+ e.getMessage());
				e.printStackTrace();
				operate_flag = "操作失败！";
				errorList
						.add("com.brick.deposit.service.DepositService.makeDepositToEndback"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("保证金冲抵(平均冲抵)错误!请联系管理员");
			}finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				}
			}
		}
		outputMap.put("operateflag", operate_flag);
		if(errorList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}
	}
}
