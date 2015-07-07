package com.brick.decompose.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.brick.coderule.service.CodeRule;
import com.brick.credit.service.CreditVouchManage;

public class CheckManager extends AService {
	Log logger = LogFactory.getLog(CheckManager.class);
	public static final Logger log = Logger.getLogger(CheckManager.class);
//	static SqlMapClient sqlMapper = null;
//	static {
//		sqlMapper = DataAccessor.getSession();
//	}

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
	 * 查询所有来款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showCheckDecomposeInfo(Context context) {
		
		Calendar cal=Calendar.getInstance();
		String currentDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		cal.set(Calendar.DATE,cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		String startDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		String endDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List bankList = null;

		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (cardFlag == 0) {
			context.contextMap.put("search_decomposestatus", 4);
		} else if (cardFlag == 1) {
			context.contextMap.put("search_decomposestatus", 5);
		} else if (cardFlag == 2) {
			context.contextMap.put("search_decomposestatus", 1);
		} else if (cardFlag == 3) {
			context.contextMap.put("search_decomposestatus", 0);
		}

		DataWrap checkDecomposeListPage = null;
		// 查询条件
		String search_opposingunit = null;
		String search_beginopposingdate = null;
		String search_endopposingdate = null;
		String search_fiin_id = null;
		String search_beginincomemoney = null;
		String search_endincomemoney = null;
		String search_decomposename = null;
		String search_begindecomposedate = null;
		String search_enddecomposedate = null;
		String search_bankname = null;
		String search_bankno = null;
		String search_decomposestatus = null;
		if (context.contextMap.get("search_opposingunit") == null) {
			search_opposingunit = "";
		} else {
			search_opposingunit = context.contextMap.get("search_opposingunit")
					.toString();
			search_opposingunit.trim();
		}
		if (context.contextMap.get("search_beginopposingdate") == null) {
			if(cardFlag==0) {
				search_beginopposingdate=startDate;
				context.contextMap.put("search_beginopposingdate",startDate);
			} else {
				search_beginopposingdate=currentDate;
				context.contextMap.put("search_beginopposingdate",currentDate);
			}
		} else {
			search_beginopposingdate = context.contextMap.get(
					"search_beginopposingdate").toString();
			search_beginopposingdate.trim();
		}
		if (context.contextMap.get("search_endopposingdate") == null) {
			if(cardFlag==0) {
				search_endopposingdate=endDate;
				context.contextMap.put("search_endopposingdate",endDate);
			} else {
				search_endopposingdate=currentDate;
				context.contextMap.put("search_endopposingdate",currentDate);
			}
		} else {
			search_endopposingdate = context.contextMap.get(
					"search_endopposingdate").toString();
			search_endopposingdate.trim();
		}
		if (context.contextMap.get("search_fiin_id") == null) {
			search_fiin_id = "";
		} else {
			search_fiin_id = context.contextMap.get("search_fiin_id")
					.toString();
			search_fiin_id.trim();
		}
		if (context.contextMap.get("search_beginincomemoney") == null) {
			search_beginincomemoney = "";
		} else {
			search_beginincomemoney = context.contextMap.get(
					"search_beginincomemoney").toString();
			search_beginincomemoney.trim();
		}
		if (context.contextMap.get("search_endincomemoney") == null) {
			search_endincomemoney = "";
		} else {
			search_endincomemoney = context.contextMap.get(
					"search_endincomemoney").toString();
			search_endincomemoney.trim();
		}
		if (context.contextMap.get("search_decomposename") == null) {
			search_decomposename = "";
		} else {
			search_decomposename = context.contextMap.get(
					"search_decomposename").toString();
			search_decomposename.trim();
		}
		if (context.contextMap.get("search_begindecomposedate") == null) {
			search_begindecomposedate = "";
		} else {
			search_begindecomposedate = context.contextMap.get(
					"search_begindecomposedate").toString();
			search_begindecomposedate.trim();
		}
		if (context.contextMap.get("search_enddecomposedate") == null) {
			search_enddecomposedate = "";
		} else {
			search_enddecomposedate = context.contextMap.get(
					"search_enddecomposedate").toString();
			search_enddecomposedate.trim();
		}
		if (context.contextMap.get("search_bankname") == null) {
			search_bankname = "";
		} else {
			search_bankname = context.contextMap.get("search_bankname")
					.toString();
			search_bankname.trim();
		}
		if (context.contextMap.get("search_bankno") == null) {
			search_bankno = "";
		} else {
			search_bankno = context.contextMap.get("search_bankno").toString();
			search_bankno.trim();
		}
		if (context.contextMap.get("search_decomposestatus") == null) {
			search_decomposestatus = "";
		} else {
			search_decomposestatus = context.contextMap.get(
					"search_decomposestatus").toString();
			search_decomposestatus.trim();
		}

		if (errorList.isEmpty()) {
			try {
				checkDecomposeListPage = (DataWrap) DataAccessor.query(
						"decompose.queryCheckDecomposeInfo",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
				bankList = (List) DataAccessor.query("decompose.queryBankName",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.showCheckDecomposeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.showCheckDecomposeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("bankList", bankList);
			outputMap.put("dw", checkDecomposeListPage);
			outputMap.put("search_opposingunit", search_opposingunit);
			outputMap.put("search_beginopposingdate", search_beginopposingdate);
			outputMap.put("search_endopposingdate", search_endopposingdate);
			outputMap.put("search_fiin_id", search_fiin_id);
			outputMap.put("search_beginincomemoney", search_beginincomemoney);
			outputMap.put("search_endincomemoney", search_endincomemoney);
			outputMap.put("search_decomposename", search_decomposename);
			outputMap.put("search_begindecomposedate",
					search_begindecomposedate);
			outputMap.put("search_enddecomposedate", search_enddecomposedate);
			outputMap.put("search_bankname", search_bankname);
			outputMap.put("search_bankno", search_bankno);
			outputMap.put("search_decomposestatus", search_decomposestatus);
			
			outputMap.put("cardFlag", cardFlag);
			outputMap.put("__action", context.contextMap.get("__action"));
			
			Output.jspOutput(outputMap, context,
					"/decompose/showCheckDecompose.jsp");
		} else {
			errorList.add("分解确认管理页错误!请联系管理员") ;
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	
	
	@SuppressWarnings("unchecked")
	public void queryDecomposeBillById(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map incomeMap = null;
		Map decomposeEmpMap = null;
		List decomposeBillList = null;
		if (errorList.isEmpty()) {
			try {
				incomeMap = (Map) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				decomposeEmpMap = (Map) DataAccessor.query(
						"decompose.queryDecomposeEmp", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryDecomposeBill", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.queryDecomposeBillById"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.queryDecomposeBillById"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		if(errorList.isEmpty()){
			outputMap.put("incomeMap", incomeMap);
			outputMap.put("decomposeEmpMap", decomposeEmpMap);
			outputMap.put("decomposeBillList", decomposeBillList);
			outputMap.put("operate_flag", context.contextMap.get("operate_flag"));
			Output
					.jspOutput(outputMap, context,
							"/decompose/showDecomposeBill.jsp");
		} else {
			errorList.add("分解单管理页错误!请联系管理员") ;
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**
	 * 处理分解单：通过/驳回
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dueDecomposeBill(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		Map incomeMap = null;
		if (errorList.isEmpty()) {
			try {
				incomeMap = (Map) DataAccessor.query(
						"decompose.queryCustIncomeInfo", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}
		}
		String check_flag = context.contextMap.get("check_flag").toString();
		Integer check_id = Integer.valueOf(context.contextMap.get(
				"s_employeeId").toString());
		context.contextMap.put("check_id", check_id);
		Integer ficb_flag=Integer.valueOf(incomeMap.get("FICB_FLAG").toString());
		context.contextMap.put("ficb_flag", ficb_flag);
		// 财务通过提交上来的分解单。
		String incomeId="";
		String fiinId="";
		boolean flag=true;
		Map<String,Object> param1=new HashMap<String,Object>();
		param1.put("select_income_id",context.contextMap.get("select_income_id"));
		try {
		while(flag) {
			String leftId=(String)DataAccessor.query("decompose.getLeftId",param1,RS_TYPE.OBJECT);
			if(leftId==null||"".equals(leftId)) {
				incomeId=(String)DataAccessor.query("decompose.getOriginalFiinId",param1,RS_TYPE.OBJECT);
				flag=false;
			} else {
				param1.put("select_income_id",leftId);
			}
		}
		} catch(Exception e) {
			
		}
		fiinId=context.contextMap.get("select_income_id")+"";
		
		if (check_flag.equals("Y")) {
			context.contextMap.put("decompose_status", 5);
			List decomposeBillList = null;
			
			SqlMapClient sqlMapper = DataAccessor.getSession() ;
			
			try {
				
				sqlMapper.startTransaction() ;
				
				sqlMapper.update("decompose.updateIncomeStatus",
						context.contextMap);
				sqlMapper.update("decompose.updateDecomposeStatus",
						context.contextMap);
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryItemMoney", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				Integer recp_id=Integer
					.valueOf(((Map) decomposeBillList.get(0))
							.get("RECP_ID").toString());
				context.contextMap.put("recp_id", recp_id);
				context.contextMap.put("recp_status", ficb_flag);
				//财务确认后再回更支付表，提交财务的时候没有回更支付表
				if (decomposeBillList != null) {
					String ficb_item = "";
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
						
						if (ficb_item.equals("租金")||("增值税").equals(ficb_item)) {
							sqlMapper.update("decompose.updateMonthPrice", context.contextMap);
						}
					}
				}
				
				//胡昭卿加，删除整单冲红申请时锁定的待分解钱，然后生成新的
				//通过传递过来的来款数据的id,查询到对应来款的数据，同时通过来款数据的RED_ID判断出是否是冲红的数据（正常来款的RED_ID是NULL,删除结果会是0条，冲红的RED_ID是冲红对应的来款的FIIN_ID）
				//当确认的分解单是冲红单是，此时要将原来的分解单对应的支付表的reduce_own_price回更为0
				Map oldWaitMoney = (Map) DataAccessor.query("decompose.getNewZeroIncomeFiinId", context.contextMap, DataAccessor.RS_TYPE.MAP);

				//System.out.println("-------------------------------"+CreditVouchManage.VOUCHPLANBYLASTPRICE("齐姜龙7-15测试勿动", "123456", 1));
				
				if(oldWaitMoney.get("RED_ID")!=null){
					context.contextMap.put("LEFT_ID", oldWaitMoney.get("RED_ID"));
					//因为整单冲红的时候，会将剩余的待分解来款锁定，而分项冲红没有锁定，所以当冲红是分项冲红时，不用判断冲红数据的RED_TYPE
					//Map oldIncomeMoneyRedType = (Map) DataAccessor.query("decompose.findOldIncomeRedTypeByFiinId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					//if(oldIncomeMoneyRedType!=null){
						//if(oldIncomeMoneyRedType.get("RED_TYPE").toString().equals("1")){
							context.contextMap.put("OPPOSING_TYPE", "待分解来款");

							sqlMapper.delete("decompose.deleteWaitMoneyRemarkWhenConfirm",
									context.contextMap);
							//删除同笔来款下二次分解，或者需要二次冲红而出现的上次冲红的待分解来款（否则客户钱数会增加）
							sqlMapper.delete("decompose.deleteAllWaitMoneyWhenConfirm",
									context.contextMap);
							sqlMapper.delete("decompose.deleteWaitMoneyWhenConfirm",
									context.contextMap);
							
						//}
					//}
							//在0来款设置状态为4（提交财务时）时，需要反更支付表详细表的已分解的本金数额为0，此操作代码是从提交财务的操作方法中拷贝过来，如果财务确认，说明对应的期次没有还钱，如果驳回，说明已经还过了
		//Marked by Michael 2013 03-19 冲红时 会重复更新 还款金额					
//							String ficb_item = "";
//							if (decomposeBillList != null) {
//								for (int i = 0; i < decomposeBillList.size(); i++) {
//									ficb_item = ((Map) decomposeBillList.get(i)).get(
//											"FICB_ITEM").toString();
//									context.contextMap.put("real_price", Double
//											.valueOf(((Map) decomposeBillList.get(i)).get(
//													"REAL_PRICE").toString()));
//									context.contextMap.put("recp_id", Integer
//											.valueOf(((Map) decomposeBillList.get(i)).get(
//													"RECP_ID").toString()));
//									context.contextMap.put("period_num", Integer
//											.valueOf(((Map) decomposeBillList.get(i)).get(
//													"RECD_PERIOD").toString()));
//									if (ficb_item.equals("租金")||ficb_item.equals("增值税")) {
//										sqlMapper.update("decompose.updateMonthPriceWhenConfirm",
//												context.contextMap);
//
//									}
//								}
//							}
					
				}
				//胡昭卿加
				// 检测如果有待分解选项的，则生成待分解来款。
				
				String ficb_item = "";
				int recpId = 0 ;
				context.contextMap.put("zujin", "租金") ;
				double money=0;
				long pk=0;
				if (decomposeBillList != null) {
					for (int i = 0; i < decomposeBillList.size(); i++) {
						if(decomposeBillList.size()==2) {//修正来款金额大于分解金额冲回的bug-----------------------------------------------add by ShenQi
							if(Double.valueOf(((Map) decomposeBillList.get(0)).get("REAL_PRICE").toString())>0&&Double.valueOf(((Map) decomposeBillList.get(1)).get("REAL_PRICE").toString())<0) {
								money=Double.valueOf(((Map) decomposeBillList.get(0)).get("REAL_PRICE").toString());
							} else {
								money=money+(Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString())<0?-Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString()):
									Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString()));
							}
						} else if(decomposeBillList.size()==3) {
							if(Double.valueOf(((Map) decomposeBillList.get(0)).get("REAL_PRICE").toString())<0&&Double.valueOf(((Map) decomposeBillList.get(1)).get("REAL_PRICE").toString())<0
									&&Double.valueOf(((Map) decomposeBillList.get(2)).get("REAL_PRICE").toString())<0) {
								money=money+(Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString())<0?-Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString()):
									Double.valueOf(((Map) decomposeBillList.get(i)).get("REAL_PRICE").toString()));
							} else {
								money=Double.valueOf(((Map) decomposeBillList.get(0)).get("REAL_PRICE").toString());
							}
						}//修正来款金额大于分解金额冲回的bug-----------------------------------------------add by ShenQi
						ficb_item = ((Map) decomposeBillList.get(i)).get(
								"FICB_ITEM").toString();
						if (ficb_item.equals("待分解来款")) {
							double left_money = Double
									.valueOf(((Map) decomposeBillList.get(i))
											.get("REAL_PRICE").toString());
							String cust_name = ((Map) decomposeBillList.get(i))
									.get("CUST_NAME").toString();
							String cust_code= ((Map) decomposeBillList.get(i))
							.get("CUST_CODE").toString();
							String opposing_bankName="";
							if(((Map) decomposeBillList.get(i))
									.get("OPPOSING_BANKNAME")!=null)
							{
								opposing_bankName=((Map) decomposeBillList.get(i))
								.get("OPPOSING_BANKNAME").toString();
							}
							// Modify by Michael 2012 3-7 有待分解来款时增加来款账号、对方等信息 
							//context.contextMap.put("receipt_bankno", "XT-DFJ");
							//context.contextMap.put("opposing_bankno", "0");
							//context.contextMap.put("opposing_bankno", "88"+cust_code);
							//context.contextMap.put("opposing_unit", cust_name);
							context.contextMap.put("opposing_date", incomeMap.get("OPPOSING_DATE").toString());
							context.contextMap.put("opposing_type", "待分解来款");
							//冲红时待分解来款为负数
							if(left_money<0){
								context.contextMap.put("income_money", -left_money);
							}else{
								context.contextMap.put("income_money", left_money);
							}
							
							context.contextMap.put("left_id", incomeMap.get(
									"FIIN_ID").toString());
							context.contextMap.put("type", 1);
							context.contextMap.put("opposing_bankName", opposing_bankName);
							
							//Modify by Michael 2012 3-9 增加来款上传流水号
							context.contextMap.put("income_finance_code", incomeMap.get("INCOME_FINANCE_CODE").toString());
							
							//找到原来的来款中的客户的虚拟账户
							Map oldIncomeVirtual = (Map)DataAccessor.query("decompose.selectoldIncomeVirtual", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
							if(oldIncomeVirtual!=null){
								context.contextMap.put("virtual_code", oldIncomeVirtual.get("VIRTUAL_CODE"));
								// Add by Michael 2012 3-7 有待分解来款时增加来款账号、对方等信息 
								context.contextMap.put("receipt_bankno", oldIncomeVirtual.get("RECEIPT_BANKNO"));
								context.contextMap.put("opposing_bankno", oldIncomeVirtual.get("OPPOSING_BANKNO"));
								context.contextMap.put("opposing_unit", oldIncomeVirtual.get("OPPOSING_UNIT"));
								if(oldIncomeVirtual.get("UPLOAD_TIME")!=null){
									context.contextMap.put("upload_time", String.valueOf(oldIncomeVirtual.get("UPLOAD_TIME")));										
									
								}else{
									context.contextMap.put("upload_time", "");
								}										
							}
//							DataAccessor.execute("decompose.addIncomeAsLeft",
//									context.contextMap,
//									DataAccessor.OPERATION_TYPE.INSERT);
							pk=(Long) sqlMapper.insert("decompose.addIncomeAsLeft",
									context.contextMap);
						}
						//如果是结清的款，则修改支付表状态
						//取得临时的recp_id比对是否与上次相同 如果相同则不执行更新支付表的操作
						int tempRecpId = Integer.valueOf(((Map) decomposeBillList.get(i)).get("RECP_ID").toString()) ;
						if(recpId != tempRecpId && !ficb_item.equals("待分解来款")){	
							recpId = tempRecpId ;
							context.contextMap.put("recp_id", recpId) ;
							Integer count = (Integer) DataAccessor.query("decompose.isSettle", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
							if(count != null && count == 1){
									count = (Integer) DataAccessor.query("decompose.selectSettleState", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
									if(count == 1){
										context.contextMap.put("recp_status", 1) ;
									} else {
										context.contextMap.put("recp_status", 3) ;
									}
									sqlMapper.update("decompose.updateRecpStatus",
											context.contextMap);
							} else {
								if(ficb_flag == 3){//如果是3则是通过结清单进来的
									context.contextMap.put("jieqingbenjin", "结清本金") ;
									context.contextMap.put("jieqinglixi", "结清利息") ;
									context.contextMap.put("jieqingweiyuejin", "结清违约金") ;
									context.contextMap.put("jieqingsunhaijin", "结清损害金") ;
									context.contextMap.put("jieqingliugoujia", "结清留购价") ;
									context.contextMap.put("jieqingqitafeiyong", "结清其他费用") ;
									context.contextMap.put("jieqinglawyfeiyong", "结清法务费用") ;
									context.contextMap.put("jieqing", "结清") ;
									Integer cou = (Integer) DataAccessor.query("decompose.selectSettleIsSettle", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
									if(cou == 1){
										cou = (Integer) DataAccessor.query("decompose.selectSettleState", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
										if(cou == 1){
											context.contextMap.put("recp_status", 1) ;
										} else {
											context.contextMap.put("recp_status", 3) ;
										}
										sqlMapper.update("decompose.updateRecpStatus",
												context.contextMap);
									}
								} else if(ficb_item.substring(0,2).equals("结清")){ //不是正常结清也不是提前结清 则该状态为 正常（用于冲红）
									context.contextMap.put("recp_status", 5) ;
									sqlMapper.update("decompose.updateRecpStatus",
											context.contextMap);
								} else if(ficb_flag == 0 ){//不是正常结清也不是提前结清 则该状态为 正常（用于冲红）
									context.contextMap.put("recp_status", 0) ;
									sqlMapper.update("decompose.updateRecpStatus",
											context.contextMap);
								}
							}
						}

					}
					//冲红,来款金额大于分解金额,更新金额 bug   add by ShenQi-----------------------------------
					if(oldWaitMoney.get("RED_ID")!=null) {
						Map param=new HashMap();
						param.put("PK", pk);
						param.put("INCOME_MONEY", money<0?-money:money);
						sqlMapper.update("decompose.updateFiinInfoByPK",param); 
					}
					//冲红,来款金额大于分解金额,更新金额 bug   add by ShenQi-----------------------------------
				}
				sqlMapper.commitTransaction() ;
				
				System.out.println(ficb_flag);//TODO 判断是否从结清过来
				//加入新的租金分解插表模组
				Map<String,Object> paramMap=new HashMap<String,Object>();
				paramMap.put("select_income_id",fiinId);
				try {
					paramMap.put("fiin_id",incomeId);
					List<Map<String,Object>> keyList=(List<Map<String,Object>>)DataAccessor.query("decompose.getFicbId",paramMap,RS_TYPE.LIST);
					sqlMapper.startTransaction();
					
					paramMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					for(int i=0;keyList!=null&&i<keyList.size();i++) {
						paramMap.put("ficbId",keyList.get(i).get("FICB_ID"));
						paramMap.put("decomposeStatus",2);//更新成财务通过状态
						
						sqlMapper.update("decompose.confirmOrRejectDecompose",paramMap);
						
						paramMap.put("billId",keyList.get(i).get("BILL_ID"));
						paramMap.put("decomposeStatus",1);//在金流表中status=1是已确认
						sqlMapper.update("rentFinance.commitRecord",paramMap);
					}
					sqlMapper.commitTransaction();
				} catch (Exception e) {
					try {
						sqlMapper.endTransaction();
					} catch (SQLException e1) {
						
					}
				}
				
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("分解单财务通过错误!请联系管理员");
			} finally {
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if (check_flag.equals("N")) {
			context.contextMap.put("decompose_status", 1);
			List decomposeBillList = null;
			SqlMapClient sqlMapper = DataAccessor.getSession() ;
			try {
				sqlMapper.startTransaction() ;
				
				sqlMapper.update("decompose.updateIncomeStatus",
						context.contextMap);
//				DataAccessor.execute("decompose.updateIncomeStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				sqlMapper.update("decompose.updateDecomposeStatus",
						context.contextMap);
//				DataAccessor.execute("decompose.updateDecomposeStatus",
//						context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				decomposeBillList = (List) DataAccessor.query(
						"decompose.queryItemMoney", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				//此处要删除冲红单的冲红数据，同时要回更原来的冲红数据
				//胡昭卿加，删除整单冲红申请时锁定的待分解钱，然后生成新的
				//通过传递过来的来款数据的id,查询到对应来款的数据，同时通过来款数据的RED_ID判断出是否是冲红的数据（正常来款的RED_ID是NULL,删除结果会是0条，冲红的RED_ID是冲红对应的来款的FIIN_ID）
				Map oldWaitMoney = (Map) DataAccessor.query("decompose.getNewZeroIncomeFiinId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(oldWaitMoney.get("RED_ID")!=null){
					context.contextMap.put("NEWLEFT_ID", oldWaitMoney.get("RED_ID"));
					context.contextMap.put("NEWFIIN_ID", oldWaitMoney.get("FIIN_ID"));
					List oldWriteBackMoneys=(List) DataAccessor.query("decompose.getZeroIncomeWriteBackByFiinId", context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(int k=0;k<oldWriteBackMoneys.size();k++){
						HashMap WriteBackItem=(HashMap)oldWriteBackMoneys.get(k);
						context.contextMap.put("RECD_PERIOD", WriteBackItem.get("RECD_PERIOD"));
						context.contextMap.put("FICB_ITEM", WriteBackItem.get("FICB_ITEM"));
						sqlMapper.delete("decompose.updateoldWaitBackMoney",
									context.contextMap);
//						DataAccessor.execute("decompose.updateoldWaitBackMoney",
//								context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
						
					}
					sqlMapper.delete("decompose.deleteZeroWriteBackMoney",
							context.contextMap);
//					DataAccessor.execute("decompose.deleteZeroWriteBackMoney",
//							context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
					sqlMapper.delete("decompose.deleteThisIncome",
							context.contextMap);
//					DataAccessor.execute("decompose.deleteThisIncome",
//							context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
				}
				//胡昭卿加
				//当财务驳回分解单时，会将支付表详细表的reduce_own_price（已分解本金数）回更为null,因为此时的来款和分解单已经是财务驳回状态
				// 检测如果有待分解选项的，则生成待分解来款。
				if(oldWaitMoney.get("RED_ID")!=null){
					context.contextMap.put("NEWLEFT_ID", oldWaitMoney.get("RED_ID"));
					String ficb_item = "";
					decomposeBillList = (List) DataAccessor.query(
							"decompose.queryoldItemMoney", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
					if (decomposeBillList != null) {
						for (int i = 0; i < decomposeBillList.size(); i++) {
							ficb_item = ((Map) decomposeBillList.get(i)).get(
									"FICB_ITEM").toString();
							context.contextMap.put("real_price", -Double
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"REAL_PRICE").toString()));
							context.contextMap.put("recp_id", Integer
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"RECP_ID").toString()));
							context.contextMap.put("period_num", Integer
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"RECD_PERIOD").toString()));
							if (ficb_item.equals("租金")) {

							}
						}
					}
				}else{
					String ficb_item = "";
					if (decomposeBillList != null) {
						for (int i = 0; i < decomposeBillList.size(); i++) {
							ficb_item = ((Map) decomposeBillList.get(i)).get(
									"FICB_ITEM").toString();
							context.contextMap.put("real_price", -Double
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"REAL_PRICE").toString()));
							context.contextMap.put("recp_id", Integer
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"RECP_ID").toString()));
							context.contextMap.put("period_num", Integer
									.valueOf(((Map) decomposeBillList.get(i)).get(
											"RECD_PERIOD").toString()));
							if (ficb_item.equals("租金")) {

							}
						}
					}
				}
				
				sqlMapper.commitTransaction() ;
				
				//加入新的租金分解插表模组
				Map<String,Object> paramMap=new HashMap<String,Object>();
				paramMap.put("select_income_id",fiinId);
				try {
					paramMap.put("fiin_id",incomeId);
					List<Map<String,Object>> keyList=(List<Map<String,Object>>)DataAccessor.query("decompose.getFicbId",paramMap,RS_TYPE.LIST);
					sqlMapper.startTransaction();
					
					paramMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					for(int i=0;keyList!=null&&i<keyList.size();i++) {
						paramMap.put("ficbId",keyList.get(i).get("FICB_ID"));
						paramMap.put("decomposeStatus",-1);//更新成财务通过状态
						
						sqlMapper.update("decompose.confirmOrRejectDecompose",paramMap);
						
						paramMap.put("billId",keyList.get(i).get("BILL_ID"));
						paramMap.put("decomposeStatus",-1);//
						sqlMapper.update("rentFinance.commitRecord",paramMap);
					}
					sqlMapper.commitTransaction();
				} catch (Exception e) {
					try {
						sqlMapper.endTransaction();
					} catch (SQLException e1) {
						
					}
				}
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.dueDecomposeBill"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("分解单财务驳回错误!请联系管理员");
			} finally{
				try {
					sqlMapper.endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(errorList.isEmpty()){
			Output
					.jspOutput(outputMap, context,
							"/servlet/defaultDispatcher?__action=checkDecompose.showCheckDecomposeInfo");
		} else {
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 批量确认所有已提交来款
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void autoAllIncome(Context context){
		String resultStr="操作成功！";
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List unincomeList = null;
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			String ids[] = context.contextMap.get("ids").toString().trim().split("@") ;
			context.contextMap.put("ids", ids) ;
			unincomeList = (List) DataAccessor.query("decompose.getAllunincomes", context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("check_id", context.contextMap.get("s_employeeId"));
			
			if(!unincomeList.isEmpty()){
				for (int k = 0; k < unincomeList.size(); k++) {
					Map incomeMap = (Map) unincomeList.get(k);
					Integer ficb_flag=Integer.valueOf(incomeMap.get("FICB_FLAG").toString());
					context.contextMap.put("ficb_flag", ficb_flag);
					context.contextMap.put("decompose_status", 5);
					context.contextMap.put("select_income_id", incomeMap.get("FIIN_ID"));
					
					// 财务通过提交上来的分解单。
					List decomposeBillList = null;
					sqlMapper.update("decompose.updateIncomeStatus",
							context.contextMap);
//					DataAccessor.execute("decompose.updateIncomeStatus",
//							context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					sqlMapper.update("decompose.updateDecomposeStatus",
							context.contextMap);
					
//					DataAccessor.execute("decompose.updateDecomposeStatus",
//							context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					decomposeBillList = (List) DataAccessor.query(
							"decompose.queryItemMoney", context.contextMap,
							DataAccessor.RS_TYPE.LIST);
					Integer recp_id=Integer
						.valueOf(((Map) decomposeBillList.get(0))
								.get("RECP_ID").toString());
					context.contextMap.put("recp_id", recp_id);
					context.contextMap.put("recp_status", ficb_flag);
					//财务确认后再回更支付表，提交财务的时候没有回更支付表
					if (decomposeBillList != null) {
						String ficb_item = "";
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
							if (ficb_item.equals("租金") || ("增值税").equals(ficb_item)) {
//								DataAccessor.execute("decompose.updateMonthPrice",
//										context.contextMap,
//										DataAccessor.OPERATION_TYPE.UPDATE);
								sqlMapper.update("decompose.updateMonthPrice", context.contextMap);
							}
						}
					}
					//胡昭卿加，删除整单冲红申请时锁定的待分解钱，然后生成新的
					//通过传递过来的来款数据的id,查询到对应来款的数据，同时通过来款数据的RED_ID判断出是否是冲红的数据（正常来款的RED_ID是NULL,删除结果会是0条，冲红的RED_ID是冲红对应的来款的FIIN_ID）
					//当需要确认的冲红单的冲红类型是整单冲红是才可以删除
					Map oldWaitMoney = (Map) DataAccessor.query("decompose.getNewZeroIncomeFiinId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					
					
//					//授信变化
////制作分解单财务确认后，将是1、循环授信 2、供应商保证不为无  的授信余额加上此次各支付表对应的客户，供应商所对应的分解的租金加上
//					
//					//找出每一支付表被分的租金（这里给的参数有来款表ID）,这里能查询出支付表号和带分解来款，保证金，租金，但这里只取租金的钱数
//					context.contextMap.put("ficbItemType", "租金");
//					//System.out.println("-----------------------+select_income_id="+context.contextMap.get("select_income_id"));
//					//List getFinaAllMoneyDiffFicbItem=(List)DataAccessor.query("decompose.getFinaAllMoneyDiffFicbItem", context.contextMap,DataAccessor.RS_TYPE.LIST);
//					List getFinaAllMoneyDiffFicbItem=sqlMapper.queryForList("decompose.getFinaAllMoneyDiffFicbItem", context.contextMap);
//					for(int i=0;i<getFinaAllMoneyDiffFicbItem.size();i++)//遍历getFinaAllMoneyDiffFicbItem，取出具体的一条支付表及租金
//					{
//						Map getFinaDiffFicbItem=(HashMap)getFinaAllMoneyDiffFicbItem.get(i);
//						if(getFinaDiffFicbItem!=null)//只针对租金做方法
//						{
//							int recp_ids=Integer.parseInt(getFinaDiffFicbItem.get("RECP_ID").toString());//取出支付表ID
//							
//							Object MONEYCOUNTmin=getFinaDiffFicbItem.get("MINOWN_PRICE");//取该支付表中该租金的最小期数总额
//							Object MONEYCOUNTmax=getFinaDiffFicbItem.get("MAXOWN_PRICE");//取该支付表中该租金的最大期数总额
//							
//							//System.out.println("MONEYCOUNTmin="+MONEYCOUNTmin);
//							//System.out.println("MONEYCOUNTmax="+MONEYCOUNTmax);
//							
//							double moneyCountmin=0d;//将该支付表中该租金的金额转换成double类型的
//							double moneyCountmax=0d;//将该支付表中该租金的金额转换成double类型的
//							double moneyCount=0d;
//							if(MONEYCOUNTmin!=null && MONEYCOUNTmin!="")
//							{
//								moneyCountmin=Double.parseDouble(MONEYCOUNTmin.toString());
//							}
//							
//							if(MONEYCOUNTmax!=null && MONEYCOUNTmax!="")
//							{
//								moneyCountmax=Double.parseDouble(MONEYCOUNTmax.toString());
//							}
//							
//							moneyCount=moneyCountmin-moneyCountmax;
//							//System.out.println("moneyCount="+moneyCount);
//							
//							String suppType="供应商保证";
//							String suppState="无";
//							context.contextMap.put("recp_idbyfiin_id", recp_ids);
//							context.contextMap.put("suppType", suppType);
//							context.contextMap.put("suppState", suppState);
//							//通过支付表取出该支付表中的循环授信的及供应商保证不为无的客户
//							//List custList=(List)DataAccessor.query("decompose.getCustByCollectionPlan", context.contextMap,DataAccessor.RS_TYPE.LIST);
//							List custList=sqlMapper.queryForList("decompose.getCustByCollectionPlan", context.contextMap);
//							for(int h=0;h<custList.size();h++)
//							{
//								Map custMap=(HashMap)custList.get(h);
//								Map CUSTGRANTPLAN=new HashMap();//用来保存给客户在T_CUST_GRANTPLAN表中的主键和该次LAST_PRICE要加的金额
//								if(oldWaitMoney.get("RED_ID")!=null)
//								{
//									CUSTGRANTPLAN.put("lastprice",-moneyCount);
//								}
//								else
//								{
//									CUSTGRANTPLAN.put("lastprice",moneyCount);
//								}
//								
//								CUSTGRANTPLAN.put("cugpid", custMap.get("CUGP_ID"));
//								
//								//客户授信加上已经还的金额
//								sqlMapper.update("decompose.updatelastpriceByCugpid",CUSTGRANTPLAN);
//								
//							}
//							
//							//通过支付表取出该支付表中的循环授信的及供应商保证不为无的供应商
//							//List suppList=(List)DataAccessor.query("decompose.getSupplierByCollectionPlan", context.contextMap,DataAccessor.RS_TYPE.LIST);
//							List suppList=sqlMapper.queryForList("decompose.getSupplierByCollectionPlan", context.contextMap);
//							List floatList=new ArrayList();
//							for(int x=0;x<suppList.size();x++)
//							{
//								Map supplierMap=(HashMap)suppList.get(x);
//								Map CUSTGRANTPLAN=new HashMap();//用来保存给客户在T_CUST_GRANTPLAN表中的主键和该次LAST_PRICE要加的金额
//								
//								Object MONEYCOUNTZHI=supplierMap.get("LEASE_TOPRIC");//取该支付表中该租金的最小期数总额
//								Object MONEYUNTILPRICE=supplierMap.get("UNIT_PRICE");//取该支付表中该租金的最大期数总额
//								
//								Object REPEAT_CREDIT=supplierMap.get("REPEAT_CREDIT");
//								
//								float moneyCountZhi=0f;
//								float moneyUntilPrice=0f;
//								if(MONEYCOUNTZHI!=null && MONEYCOUNTZHI!="")
//								{
//									moneyCountZhi=Float.parseFloat(MONEYCOUNTZHI.toString());
//								}
//								
//								if(MONEYUNTILPRICE!=null && MONEYUNTILPRICE!="")
//								{
//									moneyUntilPrice=Float.parseFloat(MONEYUNTILPRICE.toString());
//								}
//								
//								//System.out.println("------------------MONEYCOUNTZHI="+MONEYCOUNTZHI);
//								//System.out.println("------------------MONEYUNTILPRICE="+MONEYUNTILPRICE);
//								
//								//System.out.println("------------------moneyCountZhi="+moneyCountZhi);
//								//System.out.println("------------------moneyUntilPrice="+moneyUntilPrice);
//								
//								
//								
//								if(x==suppList.size()-1)
//								{
//									float floatZhi=0f;
//									//System.out.println("---------------------="+floatList.size());
//									for(int h=0;h<floatList.size();h++)
//									{
//										//System.out.println("---------------------floatList.get(h)="+Float.parseFloat(floatList.get(h).toString()));
//										floatZhi=floatZhi+Float.parseFloat(floatList.get(h).toString());
//									}
//									floatList.add(1-floatZhi);
//									//System.out.println("运行了111="+x+"   "+(1-floatZhi));
//								}
//								else
//								{
//									floatList.add(moneyUntilPrice/moneyCountZhi);
//									//System.out.println("运行了="+x);
//								}
//								
//								//System.out.println("-------------------第"+x+1+"个供应商所占比例为："+floatList.get(x));
//								//CUSTGRANTPLAN.put("lastprice",moneyCount*Float.parseFloat(floatList.get(x).toString()));
//								if(oldWaitMoney.get("RED_ID")!=null)
//								{
//									CUSTGRANTPLAN.put("lastprice",-moneyCount*Float.parseFloat(floatList.get(x).toString()));
//								}
//								else
//								{
//									CUSTGRANTPLAN.put("lastprice",moneyCount*Float.parseFloat(floatList.get(x).toString()));
//								}
//								//CUSTGRANTPLAN.put("lastprice",moneyCount*moneyUntilPrice/moneyCountZhi);
//								CUSTGRANTPLAN.put("pdgpid", supplierMap.get("PDGP_ID"));
//								
//								//System.out.println("yunxinglemaaaa2222------------------"+moneyCount*moneyUntilPrice/moneyCountZhi);
//								//供应商授信加上已经还的金额
//								if(REPEAT_CREDIT.equals("1"))//是循环授信
//								{
//									sqlMapper.update("decompose.updatelastpriceByPdgpid",CUSTGRANTPLAN);
//								}
//								
//								//System.out.println("yunxinglemaaaa2222------------------"+moneyCount*moneyUntilPrice/moneyCountZhi);
//							}
							
//							
//							//通过支付表取出该支付表中的循环授信的及供应商保证不为无的担保人
//							List voucList=sqlMapper.queryForList("decompose.getdanbaorenByCollectionPlan", context.contextMap);
//							for(int z=0;z<voucList.size();z++)
//							{
//								Map voucMap=(HashMap)voucList.get(z);
//								Map CUSTGRANTPLAN=new HashMap();//用来保存给客户在T_CUST_GRANTPLAN表中的主键和该次LAST_PRICE要加的金额
//								//CUSTGRANTPLAN.put("lastprice",moneyCount);
//								if(oldWaitMoney.get("RED_ID")!=null)
//								{
//									CUSTGRANTPLAN.put("lastprice",-moneyCount);
//								}
//								else
//								{
//									CUSTGRANTPLAN.put("lastprice",moneyCount);
//								}
//								CUSTGRANTPLAN.put("pdvpid", voucMap.get("PDVP_ID"));
//								
//								//客户授信加上已经还的金额
//								sqlMapper.update("decompose.updatelastpriceByPdvpid",CUSTGRANTPLAN);
//							}
//							
//						}
//					}
					
					
					
					//System.out.println("-------------------------------"+CreditVouchManage.VOUCHPLANBYLASTPRICE("齐姜龙7-15测试勿动", "123456", 1));
					
					
					if(oldWaitMoney.get("RED_ID")!=null){
					context.contextMap.put("LEFT_ID", oldWaitMoney.get("RED_ID"));
					//因为整单冲红的时候，会将剩余的待分解来款锁定，而分项冲红没有锁定，所以当冲红是分项冲红时，不用判断冲红数据的RED_TYPE
					//Map oldIncomeMoneyRedType = (Map) DataAccessor.query("decompose.findOldIncomeRedTypeByFiinId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					//if(oldIncomeMoneyRedType!=null){
						//if(oldIncomeMoneyRedType.get("RED_TYPE").toString().equals("1")){
							context.contextMap.put("OPPOSING_TYPE", "待分解来款");
							sqlMapper.delete("decompose.deleteWaitMoneyRemarkWhenConfirm",
									context.contextMap);
//							DataAccessor.execute("decompose.deleteWaitMoneyRemarkWhenConfirm",
//									context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
							//删除同笔来款下二次分解，或者需要二次冲红而出现的上次冲红的待分解来款（否则客户钱数会增加）
							sqlMapper.delete("decompose.deleteAllWaitMoneyWhenConfirm",
									context.contextMap);
							sqlMapper.delete("decompose.deleteWaitMoneyWhenConfirm",
									context.contextMap);
						
//							DataAccessor.execute("decompose.deleteWaitMoneyWhenConfirm",
//									context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
						//}
					//}
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
//									if (ficb_item.equals("租金")||ficb_item.equals("增值税")) {
//										sqlMapper.update("decompose.updateMonthPriceWhenConfirm",
//												context.contextMap);
////										DataAccessor.execute("decompose.updateMonthPriceWhenConfirm",
////												context.contextMap,
////												DataAccessor.OPERATION_TYPE.UPDATE);
//									}
								}
							}
					}
					//胡昭卿加
					
					// 检测如果有待分解选项的，则生成待分解来款。
					int recpId = 0 ;
					String ficb_item = "";
					context.contextMap.put("zujin", "租金") ;
					if (decomposeBillList != null) {
						for (int i = 0; i < decomposeBillList.size(); i++) {
							ficb_item = ((Map) decomposeBillList.get(i)).get("FICB_ITEM").toString();
							if (ficb_item.equals("待分解来款")) {
								double left_money = Double
										.valueOf(((Map) decomposeBillList.get(i))
												.get("REAL_PRICE").toString());
								String cust_name = ((Map) decomposeBillList.get(i))
										.get("CUST_NAME").toString();
								//
								String cust_code= ((Map) decomposeBillList.get(i))
								.get("CUST_CODE").toString();
								String opposing_bankName="";
								if(((Map) decomposeBillList.get(i))
										.get("OPPOSING_BANKNAME")!=null){
									opposing_bankName=((Map) decomposeBillList.get(i))
									.get("OPPOSING_BANKNAME").toString();
								}
								// Modify by Michael 2012 3-7 有待分解来款时增加来款账号、对方等信息 
//								context.contextMap.put("receipt_bankno", "XT-DFJ");
//								//context.contextMap.put("opposing_bankno", "0");
//								context.contextMap.put("opposing_bankno", "88"+cust_code);
//								context.contextMap.put("opposing_unit", cust_name);
								
								context.contextMap.put("opposing_date", incomeMap.get("OPPOSING_DATE").toString());								
								context.contextMap.put("opposing_type", "待分解来款");
								context.contextMap.put("income_money", left_money);
								context.contextMap.put("left_id", incomeMap.get(
										"FIIN_ID").toString());
								context.contextMap.put("type", 1);
								context.contextMap.put("opposing_bankName", opposing_bankName);
								
								//Modify by Michael 2012 3-9 增加来款上传流水号
								context.contextMap.put("income_finance_code", incomeMap.get("INCOME_FINANCE_CODE").toString());
								//找到对应来款的客户的虚拟账号
								Map oldIncomeVirtual = (Map)DataAccessor.query("decompose.selectoldIncomeVirtual", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
								if(oldIncomeVirtual!=null){
									context.contextMap.put("virtual_code", oldIncomeVirtual.get("VIRTUAL_CODE"));
									// Add by Michael 2012 3-7 有待分解来款时增加来款账号、对方等信息 
									context.contextMap.put("receipt_bankno", oldIncomeVirtual.get("RECEIPT_BANKNO"));
									context.contextMap.put("opposing_bankno", oldIncomeVirtual.get("OPPOSING_BANKNO"));
									context.contextMap.put("opposing_unit", oldIncomeVirtual.get("OPPOSING_UNIT"));
									if(oldIncomeVirtual.get("UPLOAD_TIME")!=null){
										context.contextMap.put("upload_time", String.valueOf(oldIncomeVirtual.get("UPLOAD_TIME")));										
										
									}else{
										context.contextMap.put("upload_time", "");
									}											
								}
								sqlMapper.insert("decompose.addIncomeAsLeft",
										context.contextMap);
//								DataAccessor.execute("decompose.addIncomeAsLeft",
//										context.contextMap,
//										DataAccessor.OPERATION_TYPE.INSERT);
							}
							//如果是结清的款，则修改支付表状态
							//取得临时的recp_id比对是否与上次相同 如果相同则不执行更新支付表的操作
							int tempRecpId = Integer.valueOf(((Map) decomposeBillList.get(i)).get("RECP_ID").toString()) ;
							if(recpId != tempRecpId && !ficb_item.equals("待分解来款")){	
								recpId = tempRecpId ;
								context.contextMap.put("recp_id", recpId) ;
								Integer count = (Integer) DataAccessor.query("decompose.isSettle", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
								if(count != null && count == 1){
										count = (Integer) DataAccessor.query("decompose.selectSettleState", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
										if(count == 1){
											context.contextMap.put("recp_status", 1) ;
										} else {
											context.contextMap.put("recp_status", 3) ;
										}
										sqlMapper.update("decompose.updateRecpStatus",
												context.contextMap);
								} else {
									if(ficb_flag == 3){//如果是3则是通过结清单进来的
										context.contextMap.put("jieqing", "结清") ;
										Integer cou = (Integer) DataAccessor.query("decompose.selectSettleIsSettle", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
										if(cou == 1){
											 cou = (Integer) DataAccessor.query("decompose.selectSettleState", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
											if(cou == 1){
												context.contextMap.put("recp_status", 1) ;
											} else {
												context.contextMap.put("recp_status", 3) ;
											}
											sqlMapper.update("decompose.updateRecpStatus",
													context.contextMap);
										}
									} else if(ficb_item.substring(0,2).equals("结清")){ //不是正常结清也不是提前结清 则该状态为 正常（用于冲红）
										context.contextMap.put("recp_status", 5) ;
										sqlMapper.update("decompose.updateRecpStatus",
												context.contextMap);
									} else if(ficb_flag == 0 ){//不是正常结清也不是提前结清 则该状态为 正常（用于冲红）
										context.contextMap.put("recp_status", 0) ;
										sqlMapper.update("decompose.updateRecpStatus",
												context.contextMap);
									}
								}
							}
							//结束     如果是结清的款，则修改支付表状态 
							
							
//							if(ficb_item.equals("设备留购价")){
//								if(ficb_flag == 1){
//									sqlMapper.update("decompose.updateRecpStatus",
//											context.contextMap);
////									DataAccessor.execute("decompose.updateRecpStatus",
////											context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//								} else if(ficb_flag == 3){
//									sqlMapper.update("decompose.updateRecpStatus",
//											context.contextMap);
////									DataAccessor.execute("decompose.updateRecpStatus",
////											context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
//								}
//							}
						}
					}
				}
			}
			else
			{
				resultStr="没有来款记录！";
			}
			sqlMapper.commitTransaction() ;
			
			for(int i=0;ids!=null&&i<ids.length;i++) {
				// 财务通过提交上来的分解单。
				String incomeId="";
				String fiinId="";
				boolean flag=true;
				Map<String,Object> param1=new HashMap<String,Object>();
				param1.put("select_income_id",ids[i]);
				try {
					while(flag) {
						String leftId=(String)DataAccessor.query("decompose.getLeftId",param1,RS_TYPE.OBJECT);
						if(leftId==null||"".equals(leftId)) {
							incomeId=(String)DataAccessor.query("decompose.getOriginalFiinId",param1,RS_TYPE.OBJECT);
							flag=false;
						} else {
							param1.put("select_income_id",leftId);
						}
					}
				} catch(Exception e) {
				}
				fiinId=ids[i];
				
				//加入新的租金分解插表模组
				Map<String,Object> paramMap=new HashMap<String,Object>();
				paramMap.put("select_income_id",fiinId);
				try {
					paramMap.put("fiin_id",incomeId);
					List<Map<String,Object>> keyList=(List<Map<String,Object>>)DataAccessor.query("decompose.getFicbId",paramMap,RS_TYPE.LIST);
					sqlMapper.startTransaction();
					
					paramMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					for(int j=0;keyList!=null&&j<keyList.size();j++) {
						paramMap.put("ficbId",keyList.get(j).get("FICB_ID"));
						paramMap.put("decomposeStatus",2);//更新成财务通过状态
						
						sqlMapper.update("decompose.confirmOrRejectDecompose",paramMap);
						
						paramMap.put("billId",keyList.get(j).get("BILL_ID"));
						paramMap.put("decomposeStatus",1);//在金流表中status=1是已确认
						sqlMapper.update("rentFinance.commitRecord",paramMap);
					}
					sqlMapper.commitTransaction();
				} catch (Exception e) {
					try {
						sqlMapper.endTransaction();
					} catch (SQLException e1) {
						
					}
				}
			}
		} catch (Exception e) {
			resultStr="确认失败！";
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("批量确认失败!请联系管理员") ;
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		outputMap.put("returnStr", resultStr);
		Output.jsonOutput(outputMap, context);
//		Output.jspOutput(outputMap, context,
//				"/servlet/defaultDispatcher?__action=checkDecompose.showCheckDecomposeInfo");
	}
	
	
	/**
	 * 查询所有来款信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showCheckDecomposeInfoAjax(Context context) {
		Map outputMap = new HashMap();
		List errorList = context.errList;
		List bankList = null;

		String card = context.getRequest().getParameter("cardFlag");
		int cardFlag = card == null ? 0 : Integer.parseInt(card);
		if (cardFlag == 0) {
			context.contextMap.put("search_decomposestatus", 4);
		} else if (cardFlag == 1) {
			context.contextMap.put("search_decomposestatus", 5);
		} else if (cardFlag == 2) {
			context.contextMap.put("search_decomposestatus", 1);
		} else if (cardFlag == 3) {
			context.contextMap.put("search_decomposestatus", 0);
		}

		DataWrap checkDecomposeListPage = null;
		// 查询条件
		String search_opposingunit = null;
		String search_beginopposingdate = null;
		String search_endopposingdate = null;
		String search_fiin_id = null;
		String search_beginincomemoney = null;
		String search_endincomemoney = null;
		String search_decomposename = null;
		String search_begindecomposedate = null;
		String search_enddecomposedate = null;
		String search_bankname = null;
		String search_bankno = null;
		String search_decomposestatus = null;
		if (context.contextMap.get("search_opposingunit") == null) {
			search_opposingunit = "";
		} else {
			search_opposingunit = context.contextMap.get("search_opposingunit")
					.toString();
			search_opposingunit.trim();
		}
		if (context.contextMap.get("search_beginopposingdate") == null) {
			search_beginopposingdate = "";
		} else {
			search_beginopposingdate = context.contextMap.get(
					"search_beginopposingdate").toString();
			search_beginopposingdate.trim();
		}
		if (context.contextMap.get("search_endopposingdate") == null) {
			search_endopposingdate = "";
		} else {
			search_endopposingdate = context.contextMap.get(
					"search_endopposingdate").toString();
			search_endopposingdate.trim();
		}
		if (context.contextMap.get("search_fiin_id") == null) {
			search_fiin_id = "";
		} else {
			search_fiin_id = context.contextMap.get("search_fiin_id")
					.toString();
			search_fiin_id.trim();
		}
		if (context.contextMap.get("search_beginincomemoney") == null) {
			search_beginincomemoney = "";
		} else {
			search_beginincomemoney = context.contextMap.get(
					"search_beginincomemoney").toString();
			search_beginincomemoney.trim();
		}
		if (context.contextMap.get("search_endincomemoney") == null) {
			search_endincomemoney = "";
		} else {
			search_endincomemoney = context.contextMap.get(
					"search_endincomemoney").toString();
			search_endincomemoney.trim();
		}
		if (context.contextMap.get("search_decomposename") == null) {
			search_decomposename = "";
		} else {
			search_decomposename = context.contextMap.get(
					"search_decomposename").toString();
			search_decomposename.trim();
		}
		if (context.contextMap.get("search_begindecomposedate") == null) {
			search_begindecomposedate = "";
		} else {
			search_begindecomposedate = context.contextMap.get(
					"search_begindecomposedate").toString();
			search_begindecomposedate.trim();
		}
		if (context.contextMap.get("search_enddecomposedate") == null) {
			search_enddecomposedate = "";
		} else {
			search_enddecomposedate = context.contextMap.get(
					"search_enddecomposedate").toString();
			search_enddecomposedate.trim();
		}
		if (context.contextMap.get("search_bankname") == null) {
			search_bankname = "";
		} else {
			search_bankname = context.contextMap.get("search_bankname")
					.toString();
			search_bankname.trim();
		}
		if (context.contextMap.get("search_bankno") == null) {
			search_bankno = "";
		} else {
			search_bankno = context.contextMap.get("search_bankno").toString();
			search_bankno.trim();
		}
		if (context.contextMap.get("search_decomposestatus") == null) {
			search_decomposestatus = "";
		} else {
			search_decomposestatus = context.contextMap.get(
					"search_decomposestatus").toString();
			search_decomposestatus.trim();
		}

		if (errorList.isEmpty()) {
			try {
				checkDecomposeListPage = (DataWrap) DataAccessor.query(
						"decompose.queryCheckDecomposeInfo",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
				bankList = (List) DataAccessor.query("decompose.queryBankName",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				log
						.error("com.brick.decompose.service.CheckManager.showCheckDecomposeInfo"
								+ e.getMessage());
				e.printStackTrace();
				errorList
						.add("com.brick.decompose.service.CheckManager.showCheckDecomposeInfo"
								+ e.getMessage());
				LogPrint.getLogStackTrace(e, logger);
				errorList.add("查询所有来款信息错误!请联系管理员");
			}
		}
		if (errorList.isEmpty()) {
			outputMap.put("bankList", bankList);
			outputMap.put("dw", checkDecomposeListPage);
			outputMap.put("search_opposingunit", search_opposingunit);
			outputMap.put("search_beginopposingdate", search_beginopposingdate);
			outputMap.put("search_endopposingdate", search_endopposingdate);
			outputMap.put("search_fiin_id", search_fiin_id);
			outputMap.put("search_beginincomemoney", search_beginincomemoney);
			outputMap.put("search_endincomemoney", search_endincomemoney);
			outputMap.put("search_decomposename", search_decomposename);
			outputMap.put("search_begindecomposedate",
					search_begindecomposedate);
			outputMap.put("search_enddecomposedate", search_enddecomposedate);
			outputMap.put("search_bankname", search_bankname);
			outputMap.put("search_bankno", search_bankno);
			outputMap.put("search_decomposestatus", search_decomposestatus);
			
			outputMap.put("cardFlag", cardFlag);
			outputMap.put("__action", context.contextMap.get("__action"));
			
			Output.jsonOutput(outputMap, context);
//			Output.jspOutput(outputMap, context,
//					"/decompose/showCheckDecompose.jsp");
		} else {
			outputMap.put("errList", errorList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//进行整单冲红申请时判断剩余的待分解来款是否已经再次进行了分解
	@SuppressWarnings("unchecked")
	public void judgeBillCanWriteBack(Context context){
		
		Map outputMap = new HashMap();
		try{
			Map oldFiinIncome = (Map) DataAccessor.query("decompose.queryFiinInfoByFiinId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			List oldWriteBackList= (List) DataAccessor.query("decompose.queryIncomeWriteBackBills",context.contextMap, DataAccessor.RS_TYPE.LIST);
			double incomeMoney=Double.valueOf(oldFiinIncome.get("INCOME_MONEY").toString());
			/*double deposeMoney=0;
			for(int i=0;i<oldWriteBackList.size();i++) {
				if("租金".equals(((Map)oldWriteBackList.get(i)).get("FICB_ITEM"))||"增值税".equals(((Map)oldWriteBackList.get(i)).get("FICB_ITEM"))) {
					deposeMoney=deposeMoney+Double.valueOf(((Map)oldWriteBackList.get(i)).get("REAL_PRICE").toString());
				}
			}*/
			/*if(incomeMoney>deposeMoney) {
				//如果来款金额大于整单冲红,就不做冲红 
				outputMap.put("returnStrnew","incomeMoneyBigThenDesposeMoney");
				//Output.jsonOutput(outputMap, context);
				return;
			}*/	
			
			String returnStr="yes";
			//判断本条要申请冲红的数据是否被锁定
			List judgeLockedList=(List) DataAccessor.query("decompose.judgeLockedWriteBackAllBack",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查询要申请冲红的来款是否有过分解
			List WriteBillList=(List) DataAccessor.query("decompose.queryIncomeWriteBackBills",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			//查询要申请冲红的来款对应的分解期数下是否有更大期次的还款
			List FinaCollectionBills=(List) DataAccessor.query("decompose.queryFinaCollectionBills",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("jieqingitem", "待分解来款");
			context.contextMap.put("jieqingitem_jieqing", "结清%");
			//当申请冲红的来款是做了结清操作的，那么无论对应的支付表是否进行分解，都可以对做结清的来款进行申请
			List FinaCollectionBills_jieqing=(List) DataAccessor.query("decompose.queryFinaCollectionBills_JIEQING",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			//判断如果当前要申请的来款进行还款后进行了结清，那么就不能进行申请
			List FinaBillsLater_jieqing=(List) DataAccessor.query("decompose.FinaBillsLater_jieqing",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			//System.out.println(FinaBillsLater_jieqing.size()+"=========");
			//判断如果当前要申请的来款的分解项是结清的话，就可以进行冲红
			List FinaBills_jieqing=(List) DataAccessor.query("decompose.IfCollectionBills_JIEQING",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			//首先确定本笔要申请冲红的来款的唯一性和存在
			if(judgeLockedList.size()>0){
				//判断当前申请冲红的来款是否进行结清分解如果是结清，那么不管其他情况，都可以冲红
				if(FinaBills_jieqing.size()>0){
					returnStr="no";
					outputMap.put("returnStr", returnStr);
				}else{
					//查看要申请的冲红的来款剩余的待分解来款是否进行过处理（如果分解过，那么就不能申请，如果冲红过，则可以）
					List judgeList=(List) DataAccessor.query("decompose.judgeWriteBackAllBack",
							context.contextMap, DataAccessor.RS_TYPE.LIST);					
					if(judgeList.size()>0){
						if(((HashMap)judgeList.get(0))!=null&&((HashMap)judgeList.get(0)).get("SUMREAL_PRICE")!=null){
							if(Double.parseDouble(((HashMap)judgeList.get(0)).get("SUMREAL_PRICE").toString())>0.005){
								//System.out.println(Double.parseDouble(((HashMap)judgeList.get(0)).get("SUMREAL_PRICE").toString())+"=====");
								returnStr="noconfirmyes";
								outputMap.put("returnStr", returnStr);
							}
						}
						//查看要申请冲红的来款分解的支付表是否进行过结清操作，如果有，就不能申请
					}else if(FinaBillsLater_jieqing.size()>0){
						if(((HashMap)FinaBillsLater_jieqing.get(0))!=null&&((HashMap)FinaBillsLater_jieqing.get(0)).get("SUMREAL_PRICE")!=null){
							if(Double.parseDouble(((HashMap)FinaBillsLater_jieqing.get(0)).get("SUMREAL_PRICE").toString())>0.005){
								returnStr="jieqingyes";
								outputMap.put("returnStr", returnStr);
							}
						}
					//}
					//else if(FinaCollectionBills_jieqing.size()>0){
					//	outputMap.put("returnStr", returnStr);
					//}else if(FinaCollectionBills_jieqing.size()>0){
					//	returnStr="fenjiehoujieqing";
					//	outputMap.put("returnStr", returnStr);
					}else{
						returnStr="no";
						outputMap.put("returnStr", returnStr);
					}
					//当不是结清分解是，并且在分解后，其他的来款对相同的支付表又进行过分解，那么就不能申请
					if(FinaCollectionBills_jieqing.size()==0){
						if(FinaCollectionBills.size()>0){
							String returnStrnew="havemorecollbills";
							outputMap.put("returnStrnew", returnStrnew);
						}else{
							//如果同笔来款分解过两次，而且已经做过冲红单，但是财务还没有确认前，不能进行第二次冲红申请，如果returnStr不是yes，
							//说明已经进行过1468行的if else if
							if(returnStr.equals("yes")){
								returnStr="no";
								outputMap.put("returnStr", returnStr);
							}
						}
					}
				}		
				
			}else if(WriteBillList.size()==0){
				returnStr="nowritebill";
				outputMap.put("returnStr", returnStr);
			}else{
				returnStr="havelocked";
				outputMap.put("returnStr", returnStr);				
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
				Output.jsonOutput(outputMap, context);
		}
	}
	//冲红申请将来款记录的状态改为2(分项冲红申请)或者3(整单冲红申请)
	public void requrieBillCanWriteBack(Context context){
		Map callback = new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
			sqlMapper.startTransaction();
			String updateRedtype="";
			List judgeLockedList=(List) DataAccessor.query("decompose.judgeLockedWriteBackAllBack",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(judgeLockedList.size()>0){

				context.contextMap.put("requirereson", context.contextMap.get("requirereson"));
				
				sqlMapper.insert("decompose.addWriteBackRequireRemark",context.contextMap);
//				DataAccessor.execute("decompose.addWriteBackRequireRemark",context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				
				Object obj= sqlMapper.update("decompose.updateWriteBackState",context.contextMap);
//				Object obj=DataAccessor.execute("decompose.updateWriteBackState",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				if(context.contextMap.get("red_type").toString().equals("3")){
					Object objs= sqlMapper.update("decompose.lockWaitMoneyIncome",context.contextMap);
//					Object objs=DataAccessor.execute("decompose.lockWaitMoneyIncome",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
					if(obj!=null&&objs!=null){
						updateRedtype="success";
					}
				}else{
					if(obj!=null){
						updateRedtype="success";
					}
				}
				
				callback.put("updateRedtype", updateRedtype);
			}else{
				updateRedtype="havelocked";
				callback.put("updateRedtype", updateRedtype);
			}
			sqlMapper.commitTransaction() ;
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Output.jsonOutput(callback, context);
		}
	}
	//冲红审批通过会将来款数据状态改为0（分项冲红）或者1（整单冲红）
	public void responseBillCanWriteBack(Context context){
		Map callback = new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
			sqlMapper.startTransaction() ;
			String updateRedtype="";
			context.contextMap.put("responseanswer", context.contextMap.get("responseanswer"));
			sqlMapper.update("decompose.addWriteBackResponseRemark",context.contextMap);
//			DataAccessor.execute("decompose.addWriteBackResponseRemark",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			Object obj= sqlMapper.update("decompose.updateWriteBackPassState",context.contextMap);
//			Object obj=DataAccessor.execute("decompose.updateWriteBackPassState",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			
			// Add by Michael 2012 1/5 增加冲红确认人ID--------------------------------
			context.contextMap.put("red_check_id", context.getContextMap().get(
					"s_employeeId"));
			sqlMapper.update("decompose.updateDecomposeBillRedCheck",context.contextMap);
			//-----------------------------------------------------------------------
			
			if(obj!=null){
				updateRedtype="success";
			}
			callback.put("updateRedtype", updateRedtype);
			sqlMapper.commitTransaction() ;
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Output.jsonOutput(callback, context);
		}
	}
	//冲红审批通过会将来款数据状态改为0（分项冲红）或者1（整单冲红）
	public void responseBillCanWriteBackNoAgree(Context context){
		Map callback = new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
			sqlMapper.startTransaction() ;
			String updateRedtype="";
			context.contextMap.put("responseanswer", context.contextMap.get("responseanswer"));
			sqlMapper.update( "decompose.addWriteBackResponseRemark",context.contextMap);
//			DataAccessor.execute("decompose.addWriteBackResponseRemark",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			Object obj= sqlMapper.update("decompose.updateWriteBackNoPassState",context.contextMap);
//			Object obj=DataAccessor.execute("decompose.updateWriteBackNoPassState",context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			if(obj!=null){
				updateRedtype="success";
			}
			callback.put("updateRedtype", updateRedtype);
			sqlMapper.commitTransaction() ;
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Output.jsonOutput(callback, context);
		}
	}
	//制作分解单页面的数据
	@SuppressWarnings("unchecked")
	public void showAllFiinBillInfo(Context context){
		Map outputMap = new HashMap();
		Map custIncomeMap = null;
		List custDecomposeList = null;
		String cust_name = "";
		List errList = context.errList ; 
		try{
			custIncomeMap = (HashMap) DataAccessor.query(
					"decompose.queryCustIncomeInfoByIncome", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			//根据来款ID查询
			custDecomposeList = (List) DataAccessor.query("decompose.queryIncomeWriteBackBills", context.contextMap,DataAccessor.RS_TYPE.LIST);
			//根据客户编号
			cust_name = ((HashMap) DataAccessor.query("decompose.queryCustNameByCode", context.contextMap,DataAccessor.RS_TYPE.MAP)).get("CUST_NAME").toString(); 
			outputMap.put("select_income_id", context.contextMap.get("select_income_id"));
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			outputMap.put("cust_name", cust_name);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("制作分解单页面数据错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showCustWriteBackInfo.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
		
	}
	//找到财务确认的可以进行冲红申请的来款记录
	public void queryCanWriteBackBills(Context context){
		DataWrap dw = null;
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		try{
			if(context.contextMap.get("selecttype")!=null){
				if(context.contextMap.get("selecttype").toString().equals("4")){
					context.contextMap.put("ficb_typefind", 0);
				}
				if(context.contextMap.get("selecttype").toString().equals("3")){
					context.contextMap.put("selecttypenew", 3);
				}
				if(context.contextMap.get("selecttype").toString().equals("2")){
					context.contextMap.put("selecttypenew", 2);
				}
				if(context.contextMap.get("selecttype").toString().equals("1")){
					context.contextMap.put("selecttypenew", 1);
				}
				if(context.contextMap.get("selecttype").toString().equals("0")){
					context.contextMap.put("selecttypenew", 0);
				}
			}
			dw = (DataWrap) DataAccessor.query("decompose.queryAllCameBills", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("selecttype", context.contextMap.get("selecttype"));
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("分解单冲红--管理页面错误!请联系管理员");
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showWriteBackBills.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	//找到需要进行冲红审批的来款记录
	public void queryResQuireWriteBackBills(Context context){
		DataWrap dw = null;
		Map outputMap = new HashMap();
		List errList = context.errList;
		try{
			dw = (DataWrap) DataAccessor.query("decompose.queryAllRequireBills", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("selecttype", context.contextMap.get("selecttype"));
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("分解单冲红--管理页面错误!请联系管理员");
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showResponseWriteBackBills.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	//找到可以进行制作冲红单的合同的信息记录
	public void queryMakeCollWriteBackBills(Context context){
		DataWrap dw = null;
		Map outputMap = new HashMap();
		List errList = context.errList;
		try{
			if(context.contextMap.get("selecttype")!=null){
				if(context.contextMap.get("selecttype").toString().equals("1")){
					
					context.contextMap.put("ficb_typefind", 0);
				}
				if(context.contextMap.get("selecttype").toString().equals("3")){
					context.contextMap.put("ficb_typefind", 2);
				}
				if(context.contextMap.get("selecttype").toString().equals("2")){
					context.contextMap.put("JUDGEOVER", 2);
				}
				if(context.contextMap.get("selecttype").toString().equals("4")){
					context.contextMap.put("JUDGEOVERN", 1);
				}
			}
			context.contextMap.put("item_name", "待分解来款");
			dw = (DataWrap) DataAccessor.query("decompose.queryAllCanMakeRectBillsnew", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("selecttype", context.contextMap.get("selecttype"));
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("分解单冲红--管理页面错误!请联系管理员");
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showMakeWriteBackBills.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	@SuppressWarnings("unchecked")
	public void writeBackTranction(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
			//首先根据传递的FIIN_ID查找出这条来款以前是否做过冲红，如果有，那看以前的冲红单是否已确认，如果确认了，就正常进行，如果是驳回了，需要对以往分解单的冲红数据做删除
			/*
			Map oldWriteBackZeroIncome = (Map) DataAccessor.query("decompose.queryoldWriteBackZeroIncomeByFiinId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(oldWriteBackZeroIncome!=null){
				if(oldWriteBackZeroIncome.get("DECOMPOSE_STATUS")!=null){
					if(oldWriteBackZeroIncome.get("DECOMPOSE_STATUS").toString().equals("1")){
						oldWriteBackZeroIncome.put("select_income_id", oldWriteBackZeroIncome.get("FIIN_ID"));
						//sqlMapper.delete("decompose.deleteoldWriteBackItems",oldWriteBackZeroIncome);
						sqlMapper.delete("decompose.deleteWaitMoneyRemark",oldWriteBackZeroIncome);
						sqlMapper.delete("decompose.deleteThisIncome",oldWriteBackZeroIncome);
					}
				}
			}
			*/
			sqlMapper.startTransaction() ;
			Map oldFiinIncome = (Map) DataAccessor.query("decompose.queryFiinInfoByFiinId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			List oldWriteBackList= (List) DataAccessor.query("decompose.queryIncomeWriteBackBills",context.contextMap, DataAccessor.RS_TYPE.LIST);
			double incomeMoney=Double.valueOf(oldFiinIncome.get("INCOME_MONEY").toString());
			/*double deposeMoney=0;
			for(int i=0;i<oldWriteBackList.size();i++) {
				if("租金".equals(((Map)oldWriteBackList.get(i)).get("FICB_ITEM"))||"增值税".equals(((Map)oldWriteBackList.get(i)).get("FICB_ITEM"))) {
					deposeMoney=deposeMoney+Double.valueOf(((Map)oldWriteBackList.get(i)).get("REAL_PRICE").toString());
				}
			}*/	
			/*if(incomeMoney>deposeMoney) {
				//如果来款金额大于整单冲红,就不做冲红  //TODO
				return;
			}*/		
					
			Map summoneyMap = (Map) DataAccessor.query("decompose.findnewFinaIncomeMoney",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//向来款表中插入记录，来款金额是0，状态是提交财务的状态
			Object newFinaIncomeId=null;
			Map newFinaIncome=new HashMap();
			if(oldFiinIncome!=null){
				if(oldFiinIncome.get("LEFT_MONEY")==null){
					oldFiinIncome.put("LEFT_MONEY", 0.0);
				}
				if(oldFiinIncome.get("PAYMENT_MONEY")==null){
					oldFiinIncome.put("PAYMENT_MONEY", 0.0);
				}
				if(oldFiinIncome.get("COMMISSION_MONEY")==null){
					oldFiinIncome.put("COMMISSION_MONEY", 0.0);
				}
				if(oldFiinIncome.get("RECEIPT_UNIT")==null){
					oldFiinIncome.put("RECEIPT_UNIT","");
				}
				
				oldFiinIncome.put("INCOME_MONEY", 0.0);
				//oldFiinIncome.put("LEFT_ID", context.contextMap.get("select_income_id"));
				oldFiinIncome.put("RED_ID", context.contextMap.get("select_income_id"));
				//oldFiinIncome.put("OPPOSING_UNIT", context.contextMap.get("cust_name"));不能回写来款户名
				oldFiinIncome.put("DECOMPOSE_STATUS", 4);
				oldFiinIncome.put("FICB_FLAG", 0);
				newFinaIncomeId=sqlMapper.insert("decompose.createnewFiidIncome",oldFiinIncome);
				//用作反更支付表详细表用的参数
				//newFinaIncome.put("select_income_id", Integer.parseInt(newFinaIncomeId.toString()));
			}
			//向分解单表中插入对应的分解项目，钱数是原来钱数的相反数
			boolean flag=true;
			for(int i=0;i<oldWriteBackList.size();i++){
				Map newWriteBackBill=new HashMap();
				newWriteBackBill=(HashMap)oldWriteBackList.get(i);
				newWriteBackBill.put("recp_id",newWriteBackBill.get("RECP_ID"));
				newWriteBackBill.put("recp_code",newWriteBackBill.get("RECP_CODE"));
				newWriteBackBill.put("pay_date",newWriteBackBill.get("PAY_DATE"));
				newWriteBackBill.put("recd_period",newWriteBackBill.get("RECD_PERIOD"));
				newWriteBackBill.put("ficb_item",newWriteBackBill.get("FICB_ITEM"));
				newWriteBackBill.put("should_price",newWriteBackBill.get("SHOULD_PRICE"));
				newWriteBackBill.put("fiin_id",Integer.parseInt(newFinaIncomeId.toString()));
				newWriteBackBill.put("cust_code",newWriteBackBill.get("CUST_CODE"));
				newWriteBackBill.put("ficb_state",4);
				newWriteBackBill.put("ficb_type",1);
				//newWriteBackBill.put("recd_type",newWriteBackBill.get("RECD_TYPE"));
				newWriteBackBill.put("item_order",newWriteBackBill.get("ITEM_ORDER"));
				newWriteBackBill.put("decompose_id",newWriteBackBill.get("DECOMPOSE_ID"));
				newWriteBackBill.put("TAX_PLAN_CODE",newWriteBackBill.get("TAX_PLAN_CODE"));
				if(newWriteBackBill.get("FICB_ITEM")!=null){
					if(newWriteBackBill.get("FICB_ITEM").toString().equals("待分解来款")){
						newWriteBackBill.put("real_price",-Double.parseDouble(newWriteBackBill.get("REAL_PRICE").toString()));
						newWriteBackBill.put("real_own_price", 0);
						newWriteBackBill.put("principal_rundode",  "");
						newWriteBackBill.put("ori_principal_runcode",  "");
						flag=false;
						//sqlMapper.insert("decompose.updateDecomposeBillWaitMoney",newWriteBackBill);
					}else{
						
						/*Add by Michael 2012 10-25  增加实际销账本金金额
						 *   如果此次分解的是租金，要把此次对应分解的 租金所对应的利息 + 税金 抓出来 进行比较 
						 *   如果 real_price > 利息 + 税金  则此次有分解到 本金 ，则要把 实际分解到 的本金 保存起来  
						 */
						if("租金".equals(newWriteBackBill.get("FICB_ITEM").toString())||"结清本金".equals(newWriteBackBill.get("FICB_ITEM").toString())){
							
							newWriteBackBill.put("real_own_price", (newWriteBackBill.get("REAL_OWN_PRICE")==null || "".equals(newWriteBackBill.get("REAL_OWN_PRICE"))?0:-DataUtil.doubleUtil(newWriteBackBill.get("REAL_OWN_PRICE"))));
							
							if("2".equals(newWriteBackBill.get("TAX_PLAN_CODE")) && (newWriteBackBill.get("PRINCIPAL_RUNCODE")!=null ||(!"".equals(newWriteBackBill.get("PRINCIPAL_RUNCODE"))))){
								newWriteBackBill.put("principal_rundode",  CodeRule.genePrincipalRunCode());
								newWriteBackBill.put("ori_principal_runcode",  newWriteBackBill.get("PRINCIPAL_RUNCODE"));
							}else{           
								newWriteBackBill.put("principal_rundode",  newWriteBackBill.get("PRINCIPAL_RUNCODE"));
							}
						}else{
							newWriteBackBill.put("real_own_price",0);
						}
						
						newWriteBackBill.put("real_price", Double.parseDouble(newWriteBackBill.get("REAL_PRICE").toString())-2*Double.parseDouble(newWriteBackBill.get("REAL_PRICE").toString()));
						//sqlMapper.insert("decompose.addDecomposeBill",newWriteBackBill);
					}
				}
				sqlMapper.insert("decompose.addDecomposeBill",newWriteBackBill);
				newWriteBackBill.put("ficb_id",newWriteBackBill.get("FICB_ID"));
				sqlMapper.update("decompose.updateDecomposeBill",newWriteBackBill);
				
			}
			//flag为true表示原来的来款和支付表应付钱数吻合，没有生成待分解来款
			if(flag){
				Map newWriteBackBill=new HashMap();
				newWriteBackBill=(HashMap)oldWriteBackList.get(oldWriteBackList.size()-1);
				newWriteBackBill.put("recp_id",newWriteBackBill.get("RECP_ID"));
				newWriteBackBill.put("recp_code",newWriteBackBill.get("RECP_CODE"));
				newWriteBackBill.put("pay_date",newWriteBackBill.get("PAY_DATE"));
				newWriteBackBill.put("recd_period",0);
				newWriteBackBill.put("ficb_item","待分解来款");
				newWriteBackBill.put("should_price",summoneyMap.get("SUMMONEY"));
				newWriteBackBill.put("fiin_id",Integer.parseInt(newFinaIncomeId.toString()));
				newWriteBackBill.put("cust_code",newWriteBackBill.get("CUST_CODE"));
				newWriteBackBill.put("ficb_state",4);
				newWriteBackBill.put("ficb_type",1);
				//newWriteBackBill.put("recd_type",newWriteBackBill.get("RECD_TYPE"));
				newWriteBackBill.put("item_order",newWriteBackBill.get("ITEM_ORDER"));
				newWriteBackBill.put("decompose_id",newWriteBackBill.get("DECOMPOSE_ID"));							
				newWriteBackBill.put("real_price", summoneyMap.get("SUMMONEY"));
				
				newWriteBackBill.put("real_own_price", 0);
				newWriteBackBill.put("principal_rundode",  "");
				newWriteBackBill.put("ori_principal_runcode",  "");
				
				sqlMapper.insert("decompose.addDecomposeBill",newWriteBackBill);
				//newWriteBackBill.put("ficb_id",newWriteBackBill.get("FICB_ID"));
				//sqlMapper.update("decompose.updateDecomposeBill",newWriteBackBill);
			}
			//在0来款设置状态为4（提交财务时）时，需要反更支付表详细表的已分解的本金数额为0，此操作代码是从提交财务的操作方法中拷贝过来，如果财务确认，说明此合同没有还过钱，驳回说明已经还过
			
			List decomposeBillList = (List) DataAccessor.query(
					"decompose.queryItemMoney", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
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
					if (ficb_item.equals("租金")||ficb_item.equals("增值税")) {
						//sqlMapper.update("decompose.updateMonthPriceWhenConfirm",
						//		context.contextMap);
//						DataAccessor.execute("decompose.updateMonthPriceWhenConfirm",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
			}
			
			//向来款表中插入记录，待分解来款是已分解的钱数加上剩下的待分解钱数，同时将原来的待分解来款解锁
		/*
				Map newFiinIncomemon=new HashMap();
				newFiinIncomemon.put("RECEIPT_BANKNO", oldFiinIncome.get("RECEIPT_BANKNO"));
				newFiinIncomemon.put("RECEIPT_UNIT", oldFiinIncome.get("RECEIPT_UNIT"));
				newFiinIncomemon.put("OPPOSING_DATE", oldFiinIncome.get("OPPOSING_DATE"));
				newFiinIncomemon.put("PAYMENT_MONEY", oldFiinIncome.get("PAYMENT_MONEY"));
				newFiinIncomemon.put("LEFT_MONEY", oldFiinIncome.get("LEFT_MONEY"));
				newFiinIncomemon.put("OPPOSING_BANKNAME", oldFiinIncome.get("OPPOSING_BANKNAME"));
				newFiinIncomemon.put("COMMISSION_MONEY", oldFiinIncome.get("COMMISSION_MONEY"));
				newFiinIncomemon.put("OPPOSING_FLAG", oldFiinIncome.get("OPPOSING_FLAG"));
				newFiinIncomemon.put("OPPOSING_ADDRESS", oldFiinIncome.get("OPPOSING_ADDRESS"));
				newFiinIncomemon.put("OPPOSING_BANKNO", oldFiinIncome.get("OPPOSING_BANKNO"));
				newFiinIncomemon.put("OPPOSING_EXPLAIN", oldFiinIncome.get("OPPOSING_EXPLAIN"));
				newFiinIncomemon.put("OPPOSING_SUMMARY", oldFiinIncome.get("OPPOSING_SUMMARY"));
				newFiinIncomemon.put("OPPOSING_POSTSCRIPT", oldFiinIncome.get("OPPOSING_POSTSCRIPT"));
				newFiinIncomemon.put("OPPOSING_UNIT", context.contextMap.get("cust_name"));
				newFiinIncomemon.put("OPPOSING_TYPE", "待分解来款");
				newFiinIncomemon.put("INCOME_MONEY", summoneyMap.get("SUMMONEY"));
				newFiinIncomemon.put("LEFT_ID", Integer.parseInt(newFinaIncomeId.toString()));
				sqlMapper.insert("decompose.createnewFiidIncome",newFiinIncomemon);
				//删除原来剩余的待分解来款记录*/
				/*在财务还没有确认冲红数据前不能删除剩下的待分解来款
				context.contextMap.put("OPPOSING_TYPE", "待分解来款");	
				sqlMapper.delete("decompose.deleteWaitMoneyRemark",context.contextMap);
				sqlMapper.delete("decompose.deleteWaitMoney",context.contextMap);
				sqlMapper.update("decompose.realeseWaitMoneyIncome", context.contextMap);
				sqlMapper.update("decompose.updateWriteBackRealseLock", context.contextMap);
				*/
				//context.contextMap.put("newFinaIncomeId", newFinaIncomeId);
				//sqlMapper.update("decompose.updateOldIncomeWritBackRedId", context.contextMap);
				sqlMapper.commitTransaction() ;
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=checkDecompose.queryMakeCollWriteBackBills");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("分解单整单冲红--冲红过程发生错误!请联系管理员");
		}finally{
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	//制作分解单页面的数据
	@SuppressWarnings("unchecked")
	public void showAllFiinBillInfoCheck(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		String cust_name = "";
		
		try{
			custIncomeMap = (HashMap) DataAccessor.query(
					"decompose.queryCustIncomeInfoByIncome", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			//根据来款ID找到所有的分解项
			custDecomposeList = (List) DataAccessor.query("decompose.queryIncomeWriteBackBills", context.contextMap,DataAccessor.RS_TYPE.LIST);
			//根据客户编号
			cust_name = ((HashMap) DataAccessor.query("decompose.queryCustNameByCode", context.contextMap,DataAccessor.RS_TYPE.MAP)).get("CUST_NAME").toString(); 
			outputMap.put("select_income_id", context.contextMap.get("select_income_id"));
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			outputMap.put("cust_name", cust_name);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("制作分解单页面的数据错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showCustWriteBackInfoCheck.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void writeBackTranctionSome(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try{
//首先根据传递的FIIN_ID查找出这条来款以前是否做过冲红，如果有，那看以前的冲红单是否已确认，如果确认了，就正常进行，如果是驳回了，需要对以往分解单的冲红数据做删除
			/*
			List oldWriteBackZeroIncomes = (List) DataAccessor.query("decompose.queryoldWriteBackZeroIncomeByFiinId",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(oldWriteBackZeroIncomes.size()>0){
				for(int g=0;g<oldWriteBackZeroIncomes.size();g++){
				HashMap oldWriteBackZeroIncome=(HashMap)oldWriteBackZeroIncomes.get(g);
				if(oldWriteBackZeroIncome.get("DECOMPOSE_STATUS")!=null){
					if(oldWriteBackZeroIncome.get("DECOMPOSE_STATUS").toString().equals("1")){
						oldWriteBackZeroIncome.put("select_income_id", oldWriteBackZeroIncome.get("FIIN_ID"));
						sqlMapper.delete("decompose.deleteoldWriteBackItems",oldWriteBackZeroIncome);
						sqlMapper.delete("decompose.deleteWaitMoneyRemark",oldWriteBackZeroIncome);
						sqlMapper.delete("decompose.deleteThisIncome",oldWriteBackZeroIncome);
					}
				}
				}
			}*/
			sqlMapper.startTransaction() ;
			Map oldFiinIncome = (Map) DataAccessor.query("decompose.queryFiinInfoByFiinId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			String[] itemPeriods =HTMLUtil.getParameterValues(context.getRequest(), "item_num", "");
			Double summoneyMap = 0.0;
			//向来款表中插入记录，来款金额是0
			Object newFinaIncomeId=null;
			//首先查看本条来款以前是否进行过分项冲红
			//Map oldZeroIncome=(Map) DataAccessor.query("decompose.queryOldZeroIncomeInfoByFiinId",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map newFinaIncome=new HashMap();
			if(oldFiinIncome!=null){
				if(oldFiinIncome.get("LEFT_MONEY")==null){
					oldFiinIncome.put("LEFT_MONEY", 0.0);
				}
				if(oldFiinIncome.get("PAYMENT_MONEY")==null){
					oldFiinIncome.put("PAYMENT_MONEY", 0.0);
				}
				if(oldFiinIncome.get("COMMISSION_MONEY")==null){
					oldFiinIncome.put("COMMISSION_MONEY", 0.0);
				}
				if(oldFiinIncome.get("RECEIPT_UNIT")==null){
					oldFiinIncome.put("RECEIPT_UNIT","");
				}
				oldFiinIncome.put("INCOME_MONEY", 0.0);
				oldFiinIncome.put("OPPOSING_UNIT", context.contextMap.get("cust_name"));
				//oldFiinIncome.put("LEFT_ID", context.contextMap.get("select_income_id"));
				oldFiinIncome.put("RED_ID", context.contextMap.get("select_income_id"));
				oldFiinIncome.put("DECOMPOSE_STATUS", 4);
				oldFiinIncome.put("FICB_FLAG", 0);
				newFinaIncomeId=sqlMapper.insert("decompose.createnewFiidIncome",oldFiinIncome);	
				newFinaIncome.put("select_income_id", Integer.parseInt(newFinaIncomeId.toString()));
			}
			//向分解单表中插入对应的分解项目，钱数是原来钱数的相反数
			for(int i=0;i<=itemPeriods.length;i++){
				if(i<itemPeriods.length){
					Map newWriteBackBill=new HashMap();
					context.contextMap.put("ficb_id", itemPeriods[i]);
					Map oldBillCollection = (Map) DataAccessor.query("decompose.findOldBillShouldPriceByFicbId",context.contextMap, DataAccessor.RS_TYPE.MAP);				
					newWriteBackBill.put("recp_id",oldBillCollection.get("RECP_ID"));
					newWriteBackBill.put("pay_date",oldBillCollection.get("PAY_DATE"));
					newWriteBackBill.put("recp_code",oldBillCollection.get("RECP_CODE"));
					newWriteBackBill.put("recd_period",oldBillCollection.get("RECD_PERIOD"));
					newWriteBackBill.put("ficb_item",oldBillCollection.get("FICB_ITEM"));
					newWriteBackBill.put("should_price",oldBillCollection.get("SHOULD_PRICE"));
					newWriteBackBill.put("fiin_id",Integer.parseInt(newFinaIncomeId.toString()));
					newWriteBackBill.put("cust_code",oldBillCollection.get("CUST_CODE"));
					newWriteBackBill.put("ficb_state",4);
					newWriteBackBill.put("ficb_type",1);
					//newWriteBackBill.put("recd_type",oldBillCollection.get("RECD_TYPE"));
					newWriteBackBill.put("item_order",oldBillCollection.get("ITEM_ORDER"));
					newWriteBackBill.put("decompose_id",oldBillCollection.get("DECOMPOSE_ID"));				
					newWriteBackBill.put("real_price", Double.parseDouble(oldBillCollection.get("REAL_PRICE").toString())-2*Double.parseDouble(oldBillCollection.get("REAL_PRICE").toString()));
					summoneyMap+=Double.parseDouble(oldBillCollection.get("REAL_PRICE").toString());
					
					if("租金".equals(newWriteBackBill.get("FICB_ITEM").toString())||"结清本金".equals(newWriteBackBill.get("FICB_ITEM").toString())){
						newWriteBackBill.put("real_own_price", newWriteBackBill.get("REAL_OWN_PRICE"));
						
						if(newWriteBackBill.get("PRINCIPAL_RUNCODE")!=null){
							newWriteBackBill.put("principal_rundode",  CodeRule.genePrincipalRunCode());
							newWriteBackBill.put("ori_principal_runcode",  newWriteBackBill.get("PRINCIPAL_RUNCODE"));
						}else{           
							newWriteBackBill.put("principal_rundode",  newWriteBackBill.get("PRINCIPAL_RUNCODE"));
						}
					}
					
					sqlMapper.insert("decompose.addDecomposeBill",newWriteBackBill);
					newWriteBackBill.put("ficb_id",oldBillCollection.get("FICB_ID"));
					sqlMapper.update("decompose.updateDecomposeBill",newWriteBackBill);
				}else if(i==itemPeriods.length){
					Map newWriteBackBill=new HashMap();
					context.contextMap.put("ficb_id", itemPeriods[i-1]);
					Map oldBillCollection = (Map) DataAccessor.query("decompose.findOldBillShouldPriceByFicbId",context.contextMap, DataAccessor.RS_TYPE.MAP);									
					newWriteBackBill.put("recp_id",oldBillCollection.get("RECP_ID"));
					newWriteBackBill.put("recp_code",oldBillCollection.get("RECP_CODE"));
					newWriteBackBill.put("pay_date",oldBillCollection.get("PAY_DATE"));
					newWriteBackBill.put("recd_period",0);
					newWriteBackBill.put("ficb_item","待分解来款");
					newWriteBackBill.put("should_price",summoneyMap);
					newWriteBackBill.put("fiin_id",Integer.parseInt(newFinaIncomeId.toString()));
					newWriteBackBill.put("cust_code",oldBillCollection.get("CUST_CODE"));
					newWriteBackBill.put("ficb_state",4);
					newWriteBackBill.put("ficb_type",1);
					//newWriteBackBill.put("recd_type",4);
					newWriteBackBill.put("item_order",oldBillCollection.get("ITEM_ORDER"));
					newWriteBackBill.put("decompose_id",oldBillCollection.get("DECOMPOSE_ID"));				
					newWriteBackBill.put("real_price", summoneyMap);
					
					newWriteBackBill.put("real_own_price", 0);
					newWriteBackBill.put("principal_rundode",  "");
					newWriteBackBill.put("ori_principal_runcode",  "");
					
					sqlMapper.insert("decompose.addDecomposeBill",newWriteBackBill);
					newWriteBackBill.put("ficb_id",oldBillCollection.get("FICB_ID"));
					sqlMapper.update("decompose.updateDecomposeBill",newWriteBackBill);					
				}
			}
			//在0来款设置状态为4（提交财务时）时，需要反更支付表详细表的已分解的本金数额为0，此操作代码是从提交财务的操作方法中拷贝过来，如果财务确认，说明对应的期次没有还钱，如果驳回，说明已经还过了
			
			List decomposeBillList = (List) DataAccessor.query(
					"decompose.queryItemMoney", newFinaIncome,
					DataAccessor.RS_TYPE.LIST);
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
					if (ficb_item.equals("租金")||ficb_item.equals("增值税")) {
						sqlMapper.update("decompose.updateMonthPriceWhenConfirm",
								context.contextMap);
//						DataAccessor.execute("decompose.updateMonthPriceWhenConfirm",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.UPDATE);
					}
				}
			}
			
			//向来款表中插入记录，待分解来款是已分解的钱数加上剩下的待分解钱数，同时将原来的待分解来款解锁		
			/*	
				Map newFiinIncomemon=new HashMap();
				newFiinIncomemon.put("RECEIPT_BANKNO", oldFiinIncome.get("RECEIPT_BANKNO"));
				newFiinIncomemon.put("RECEIPT_UNIT", oldFiinIncome.get("RECEIPT_UNIT"));
				newFiinIncomemon.put("OPPOSING_DATE", oldFiinIncome.get("OPPOSING_DATE"));
				newFiinIncomemon.put("PAYMENT_MONEY", oldFiinIncome.get("PAYMENT_MONEY"));
				newFiinIncomemon.put("LEFT_MONEY", oldFiinIncome.get("LEFT_MONEY"));
				newFiinIncomemon.put("OPPOSING_BANKNAME", oldFiinIncome.get("OPPOSING_BANKNAME"));
				newFiinIncomemon.put("COMMISSION_MONEY", oldFiinIncome.get("COMMISSION_MONEY"));
				newFiinIncomemon.put("OPPOSING_FLAG", oldFiinIncome.get("OPPOSING_FLAG"));
				newFiinIncomemon.put("OPPOSING_ADDRESS", oldFiinIncome.get("OPPOSING_ADDRESS"));
				newFiinIncomemon.put("OPPOSING_BANKNO", oldFiinIncome.get("OPPOSING_BANKNO"));
				newFiinIncomemon.put("OPPOSING_EXPLAIN", oldFiinIncome.get("OPPOSING_EXPLAIN"));
				newFiinIncomemon.put("OPPOSING_SUMMARY", oldFiinIncome.get("OPPOSING_SUMMARY"));
				newFiinIncomemon.put("OPPOSING_POSTSCRIPT", oldFiinIncome.get("OPPOSING_POSTSCRIPT"));
				newFiinIncomemon.put("OPPOSING_TYPE", "待分解来款");
				newFiinIncomemon.put("INCOME_MONEY", summoneyMap);
				newFiinIncomemon.put("OPPOSING_UNIT", context.contextMap.get("cust_name"));
				newFiinIncomemon.put("LEFT_ID", Integer.parseInt(newFinaIncomeId.toString()));
				sqlMapper.insert("decompose.createnewFiidIncome",newFiinIncomemon);*/
				sqlMapper.update("decompose.updateWriteBackRealseLock", context.contextMap);
				sqlMapper.update("decompose.realeseWaitMoneyIncome", context.contextMap);
				context.contextMap.put("newFinaIncomeId", newFinaIncomeId);
				
				//sqlMapper.update("decompose.updateOldIncomeWritBackRedId", context.contextMap);
				
				sqlMapper.commitTransaction() ;
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("分解单分项冲红--冲红过程发生错误!请联系管理员");
		}finally{
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=checkDecompose.queryMakeCollWriteBackBills");
		} else {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/*
	 * 根据来款ID找到已经分解支付表对应的合同的ID
	 */
	@SuppressWarnings("unchecked")
	public void findWriteBills (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
        
		if(errList.isEmpty()){		
			try {
				
				contractlist = (List) DataAccessor.query("decompose.findAllRectCollWriteBack", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("冲红管理--查询支付表对应的合同错误!请联系管理员");
			}
		}
		outputMap.put("contractlist", contractlist);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/contractcollpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * 根据来款ID找到已经分解支付表对应的合同的ID
	 */
	@SuppressWarnings("unchecked")
	public void findRequireWriteBills (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
        
		if(errList.isEmpty()){		
			try {
				
				contractlist = (List) DataAccessor.query("decompose.findAllRectCollWriteBack", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("冲红管理--查询支付表对应的合同错误!请联系管理员");
			}
		}
		outputMap.put("contractlist", contractlist);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/contractcolrequirelpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	@SuppressWarnings("unchecked")
	public void findAllMemosByFiinId(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List writeBackMemos = null;
		if(errList.isEmpty()){		
			try {
				
				writeBackMemos = (List) DataAccessor.query("decompose.findAllMomesByFiinId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("冲红管理--查询支付表对应的合同错误!请联系管理员");
			}
		}
		outputMap.put("writeBackMemos", writeBackMemos);	
		
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
			//Output.jspOutput(outputMap, context, "/decompose/contractcolrequirelpact.jsp");
		}else{
			outputMap.put("errList", errList);
			//Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	//制作分解单页面的数据
	@SuppressWarnings("unchecked")
	public void selectWriteBillsForsee(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map custIncomeMap = null;
		List custDecomposeList = null;
		String cust_name = "";
		try{
			custIncomeMap = (Map) DataAccessor.query(
					"decompose.queryCustIncomeInfoByIncome", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			//根据来款ID找到所有的分解项
			custDecomposeList = (List) DataAccessor.query("decompose.queryIncomeWriteBackBills", context.contextMap,DataAccessor.RS_TYPE.LIST);
			//根据客户编号
			//cust_name = ((HashMap) DataAccessor.query("decompose.queryCustNameByCode", context.contextMap,DataAccessor.RS_TYPE.MAP)).get("CUST_NAME").toString(); 
			outputMap.put("select_income_id", context.contextMap.get("select_income_id"));
			outputMap.put("cust_code", context.contextMap.get("cust_code"));
			outputMap.put("recp_code", context.contextMap.get("recp_code"));
			outputMap.put("custIncomeMap", custIncomeMap);
			outputMap.put("custDecomposeList", custDecomposeList);
			outputMap.put("cust_name", cust_name);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("制作分解单页面的数据错误!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/decompose/showCustWriteBackInfoSee.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	public void getLockedByFiinid(Context context){
		Map outputMap = new HashMap();
		Map incomeLocked=null;
		List errList = context.errList ;
		try {
			 incomeLocked = (Map) DataAccessor.query("decompose.getNewZeroIncomeFiinId",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("租金分解--查询来款的锁定状态错误!请联系管理员");
		}
		outputMap.put("incomeLocked", incomeLocked);
		Output.jsonOutput(outputMap, context);
	}
}
