package com.brick.collection.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.collection.CollectionConstants;
import com.brick.collection.core.IRRUtils;
import com.brick.collection.support.PayRate;
import com.brick.collection.util.PayUtils;
import com.brick.collection.util.PaylistUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.InterestMarginUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.chart.model.attribute.Size;

import com.brick.log.service.LogPrint;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version v1.3
 */
public class StartPayService {
	/** serial. */
	static final long serialVersionUID = 0L;

	/** logger. */
	private static Log logger = LogFactory.getLog(StartPayService.class);
	
	public static final double HUNDRED = 100D;
	
	public static final int MONTHS_OF_YEAR = 12;
	
	/**生成支付表号
	 * paylistCode
	 * @param paylists
	 * @param rentContract
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generatePaylistCode(List<Map> paylists,Map rentContract) {
		String paylistCode = String.valueOf(rentContract.get("LEASE_CODE")==null?"":rentContract.get("LEASE_CODE"));
    	int index = 0;
    	String lastNum = "";
    	
		for(Map p:paylists){
			String paylist_code = String.valueOf(p.get("RECP_CODE"));
			int k = paylist_code.indexOf("JHS");
			int lastIndex = paylist_code.lastIndexOf("-");
			
			if(k == -1){
			
				lastNum = paylist_code.substring(lastIndex+1);
				int num = DataUtil.intUtil(lastNum);
				if( index < num ){
					index = num;
				}
			}
		}
		Integer Num =  index + 1;
		paylistCode += "-"+Num;
		
    	logger.info("支付表号 : " + paylistCode);
        return paylistCode;
	}
	/**
	 * 生成支付表
	 * @param rentContract
	 * @param contractSchema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map createPaylist(Map rentContract, Map contractSchema,
			List<Map> equipList, List<Map> insureList, List<Map> otherFeeList, 
			List<Map> oldPaylists, List<Map> paylineList,Collection<Map> irrMonthPaylines) {
		logger.info("start pay...");

		Double totalPrice = 0d;

		for (Map m : equipList) {
			totalPrice += DataUtil.doubleUtil(String.valueOf(m.get("UNIT_PRICE")));
		}
		//Modify By Michael 2011 11/18 修正四射五入BUg
		contractSchema.put("TOTAL_PRICE", Math.round(totalPrice));
		contractSchema.put("LEASE_TOPRIC", rentContract.get("LEASE_TOPRIC"));
		
		//Add by Michael  For 重车 不需要计算保险费费率
		contractSchema.put("CONTRACT_TYPE", rentContract.get("CONTRACT_TYPE"));
		
		Map paylist = null;
		
		int payWay = DataUtil.intUtil(contractSchema.get("PAY_WAY"));

		if (irrMonthPaylines.size() > 0) {
			inferYearInterestByIrr(contractSchema, paylineList);
		} 
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			paylist = PayUtils.recalculateScale(contractSchema, equipList, insureList, otherFeeList, paylineList);
		} else {
			paylist = PayUtils.calculateScale(contractSchema, equipList, insureList, otherFeeList);
		}
		List paylines=(List)paylist.get("paylines");
		paylist.put("hu_weicha", "false");
		// calculate loan price (cost price)
		PaylistUtil.calculateLoanPaylist(paylist);
		
		PayUtils.balanceMonthPrice(paylist, paylineList);
		
		setIrrMonthPayline(paylist, irrMonthPaylines);
		// 如果期初方式 租金前提
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			
				if(contractSchema.get("PLEDGE_LAST_PERIOD")!=null){
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", contractSchema.get("PLEDGE_LAST_PERIOD").toString());
				}else{
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", "0");
				}
				
				
		}	
		paylist.put("oldpaylineList", paylineList);
		rentPremise(paylist);
		paylist.put("LEASE_CODE", rentContract.get("LEASE_CODE"));
		paylist.put("CUST_NAME", rentContract.get("CUST_NAME"));
		paylist.put("RECP_CODE", generatePaylistCode(oldPaylists, rentContract));
		paylist.put("RECT_ID", rentContract.get("RECT_ID"));
		return paylist;
		
	}
	/**
	 * 
	 * @param context
	 * @param rentContract
	 * @param equipList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map createPaylistByRecalculate(Context context ,Map rentContract, List<Map> equipList, List<Map> oldPaylists) {
		logger.info("start pay...");
		
		List<Map> rePaylineList = getPaylineList(context);
		
		//
		int pledgeWay = DataUtil.intUtil(context.contextMap.get("PLEDGE_WAY"));
		
		int leasePeriod = DataUtil.intUtil(context.contextMap.get("LEASE_PERIOD"));
		
		double pledgeRealPric = DataUtil.doubleUtil(context.contextMap.get("PLEDGE_REALPRIC"));
		
		if (pledgeWay == 1) {
			
			double pledgePriceAvg = pledgeRealPric / Double.valueOf(leasePeriod);
			
			int i=1;
			
			for (Map map : rePaylineList) {
				
				double monthPriceTemp = DataUtil.doubleUtil(map.get("MONTH_PRICE"));
				// fire
				map.put("MONTH_PRICE", monthPriceTemp + pledgePriceAvg);
				
				i++;
			}
			
		} else if (pledgeWay == 0) {
			
		
		} else if (pledgeWay == 2) {
			
			
		}
		
		
		List<Map> insureList = getInsureList(context);
		
		List<Map> otherFeeList = getOtherFeeList(context);
		
		// START_DATE
		context.contextMap.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));
		//
		Map paylist = PayUtils.recalculate(context.contextMap, equipList, insureList, otherFeeList,rePaylineList);
		
		paylist.put("LEASE_CODE", rentContract.get("LEASE_CODE"));
		paylist.put("CUST_NAME", rentContract.get("CUST_NAME"));
		paylist.put("RECP_CODE", generatePaylistCode(oldPaylists, rentContract));
		paylist.put("RECT_ID", rentContract.get("RECT_ID"));
		return paylist;
		
	}
	/**
	 * 
	 * @param context
	 * @param rentContract
	 * @param equipList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map createPaylist(Context context ,Map rentContract, List<Map> equipList, List<Map> oldPaylists) {
		logger.info("start pay...");
		
		List<Map> insureList = getInsureList(context);
		
		List<Map> otherFeeList = getOtherFeeList(context);
		// 2010-09-27 wjw v1.6
		List<Map> rePaylineList = upPackagePaylines(context);
		
		if(Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("TAX_PLAN_CODE"))||
				Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("TAX_PLAN_CODE"))||
				Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("TAX_PLAN_CODE"))||
				Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("TAX_PLAN_CODE"))) {
			context.contextMap.put("payList", rePaylineList);
		}
		inferYearInterestByIrr(context.contextMap, rePaylineList);
		
		// START_DATE
		context.contextMap.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));
		//
		
		//Add by Michael  重车保险费率不计算
		context.contextMap.put("CONTRACT_TYPE", String.valueOf(rentContract.get("CONTRACT_TYPE")));
		context.contextMap.put("FEESET_TOTAL", rentContract.get("FEESET_TOTAL"));
		//Map paylist = PayUtils.calculate(context.contextMap, equipList, insureList, otherFeeList);
		Map paylist = null;
		
		int payWay = DataUtil.intUtil(context.contextMap.get("PAY_WAY"));

		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			paylist = PayUtils.recalculate(context.contextMap, equipList, insureList, otherFeeList, rePaylineList);
		} else {
			paylist = PayUtils.calculate(context.contextMap, equipList, insureList, otherFeeList);
		}
		
		paylist.put("hu_weicha", "false");
		// calculate loan price (cost price)
		if(!Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("TAX_PLAN_CODE"))&&
				!Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("TAX_PLAN_CODE"))&&
				!Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("TAX_PLAN_CODE"))&&
				!Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("TAX_PLAN_CODE"))) {
			PaylistUtil.calculateLoanPaylist(paylist);
			//2010-12-04 wujw 调整合同生成尾差
			PayUtils.balanceMonthPrice(paylist, rePaylineList);
		}
		setIrrMonthPayline(paylist, keepPackagePayline(context));
		// 如果期初方式 租金前提
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			//胡昭卿加,为计算期末抵充金额时用
			
				if(context.contextMap.get("PLEDGE_LAST_PERIOD")!=null){
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", context.contextMap.get("PLEDGE_LAST_PERIOD").toString());
				}else{
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", "0");
				}
				
					

		}	
		paylist.put("oldpaylineList", rePaylineList);
		rentPremise(paylist);
		paylist.put("LEASE_CODE", rentContract.get("LEASE_CODE"));
		paylist.put("CUST_NAME", rentContract.get("CUST_NAME"));
		paylist.put("RECP_CODE", generatePaylistCode(oldPaylists, rentContract));
		paylist.put("RECT_ID", rentContract.get("RECT_ID"));
		return paylist;
		
	}
	
	/**
	 * credit head
	 * @param context
	 * @param schema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map createCreditPaylist(Map schema,List<Map> paylineList) {
		logger.info("start credit pay...");

		//
		int leasePeriod = DataUtil.intUtil(schema.get("LEASE_PERIOD"));

		double pledgeAvePrice = DataUtil.doubleUtil(schema.get("PLEDGE_AVE_PRICE"));
		
		double pledgePriceAvg = pledgeAvePrice / Double.valueOf(leasePeriod);
		
		int i=1;
		
		for (Map map : paylineList) {
			// 此处取的是页面中的应付租金。不是预期租金
			double monthPriceTemp = DataUtil.doubleUtil(map.get("MONTH_PRICE"));
			// fire
			map.put("MONTH_PRICE", monthPriceTemp + pledgePriceAvg);
			
			i++;
		}
		//
		List<Map> tempList = new ArrayList<Map>();
		
		Map paylist = null;
		//
		int payWay = DataUtil.intUtil(schema.get("PAY_WAY"));
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			paylist = PayUtils.recalculate(schema, tempList, tempList, tempList, paylineList);
		} else {
			paylist = PayUtils.calculate(schema, tempList, tempList, tempList);
		}
		// calculate loan price (cost price)
		PaylistUtil.calculateLoanPaylist(paylist);
		
		StartPayService.packagePaylines(paylist);
		
		return paylist;

	}
	/**
	 * credit head
	 * @param context
	 * @param schema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map createCreditPaylistIRR(Map schema,List<Map> paylineList,Collection<Map> irrMonthPaylines) {
		
		logger.info("start createCreditPaylistIRR ...");
		//paylineList解压后的每一期的钱  
		//irrMonthPaylines为解压的钱
		int payWay = DataUtil.intUtil(schema.get("PAY_WAY"));
		
		// 2010-09-26 wjw v1.6   计算年利率
		inferYearInterestByIrr(schema, paylineList);
		//
		List<Map> tempList = new ArrayList<Map>();
		
		Map paylist = null;
		//
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			//不等额计算方式  re开头的
			paylist = PayUtils.recalculate(schema, tempList, tempList, tempList, paylineList);
		
		} else {
			//等额
			paylist = PayUtils.calculate(schema, tempList, tempList, tempList);
			
		}
		// calculate loan price (cost price)  成本
		if(!Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))&&
				!Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))&&
					!Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&
						!Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {
			PaylistUtil.calculateLoanPaylist(paylist);
		}
		//调尾差
		paylist.put("hu_weicha", "false");
		if(!Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))&&
				!Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))&&
					!Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&
						!Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {
			PayUtils.balanceMonthPrice(paylist, paylineList);
		}
		//应付租金
		setIrrMonthPayline(paylist, irrMonthPaylines);
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			// 租金前提
			
				//胡昭卿加,为计算期末抵充金额时用
				paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", schema.get("PLEDGE_LAST_PERIOD"));
		}
		paylist.put("oldpaylineList", paylineList);
		rentPremise(paylist);
		return paylist;
		
	}
	/**
	 * 
	 * @param schema   计算年利率
	 * @param paylineList
	 */
	@SuppressWarnings("unchecked")
	public static void inferYearInterestByIrr(Map schema,List<Map> paylineList) {
		
		
		int leasePeriod = DataUtil.intUtil(schema.get("LEASE_PERIOD"));
		
		int leaseTerm = DataUtil.intUtil(schema.get("LEASE_TERM"));
		
		double pledgeAvePrice = DataUtil.doubleUtil(schema.get("PLEDGE_AVE_PRICE"));
		
		double[] cashFlows = new double[leasePeriod+1];
		
		double leaseTopric = DataUtil.doubleUtil(schema.get("LEASE_TOPRIC"));
		
		cashFlows[0] = -leaseTopric;//设备总价
		
		//Modify by Michael 转成BigDecimal 运算 保留2位小数
		//double pledgePriceAvg = Math.round(pledgeAvePrice / Double.valueOf(leasePeriod)*HUNDRED)/HUNDRED;//平均冲抵的保证金分开到每一期
		
        BigDecimal bd1 = new BigDecimal(Double.toString(pledgeAvePrice)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(Double.valueOf(leasePeriod))); 
        double pledgePriceAvg=bd1.divide(bd2,2,BigDecimal.ROUND_HALF_UP).doubleValue();
        
		int i=1;

		for (Map map : paylineList) {
			
			double monthPriceTemp = DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"));

			if(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
					||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
						||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
							||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {//直接租赁方案 计算年浮动利率特殊处理, 售后回租
				if(i==1) {
					cashFlows[i]=monthPriceTemp+bd1.doubleValue();
				} else {
					cashFlows[i]=monthPriceTemp;
				}
				
				/*//最后处理延迟拨款期数 延迟拨款不影响合同利率  实际是影响的  不显示给客户看
				if(i==paylineList.size()&&Integer.valueOf(schema.get("DEFER_PERIOD").toString())!=0) {
					
					cashFlows[0]=0;
					cashFlows[Integer.valueOf(schema.get("DEFER_PERIOD").toString())]=cashFlows[Integer.valueOf(schema.get("DEFER_PERIOD").toString())]-leaseTopric;
					double[] tempCashFlows = new double[leasePeriod];
					for(int j=0;j<tempCashFlows.length;j++) {
						tempCashFlows[j]=cashFlows[j+1];
					}
					
					cashFlows=tempCashFlows;
				}*/
				map.put("MONTH_PRICE", monthPriceTemp + pledgePriceAvg);
			} else {
				cashFlows[i] = monthPriceTemp + pledgePriceAvg;
				
				map.put("MONTH_PRICE", cashFlows[i]);
			}
			
			// fire
			
			i++;
		}
		
		
		double yearInterest = Math.round(IRRUtils.getIRR(cashFlows, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);

		logger.info("年利率为:"+yearInterest);
		
		double baseRate = PayRate.getBaseRate(leasePeriod * leaseTerm);
		
		double floatRate = (yearInterest / baseRate - 1) * HUNDRED;
		
		schema.put("YEAR_INTEREST", yearInterest);
		
		schema.put("FLOAT_RATE", floatRate);
		
		schema.put("JIZHUNLILV", baseRate);
		
		schema.put("payList", paylineList);
	}
	/**
	 * 
	 * @param context
	 * @param equipList
	 * @param oldPaylists
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map changePaylist(Context context , List<Map> equipList) {
		logger.info("start change paylist pay...");
		//
		List<Map> insureList = getInsureList(context);
		//
		List<Map> otherFeeList = getOtherFeeList(context);
		// 2010-09-19 v1.6
		// List<Map> rePaylineList = getPaylineList(context);
		List<Map> rePaylineList = upPackagePaylines(context);
		
		inferYearInterestByIrr(context.contextMap, rePaylineList);
		
		// START_DATE
		context.contextMap.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));

		Map paylist = null;
		//
		int payWay = DataUtil.intUtil(context.contextMap.get("PAY_WAY"));

		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			paylist = PayUtils.recalculate(context.contextMap, equipList, insureList, otherFeeList, rePaylineList);
		} else {
			paylist = PayUtils.calculate(context.contextMap, equipList, insureList, otherFeeList);
		}
		paylist.put("hu_weicha", "false");
		//2010-12-04 wujw 调整变更的尾差
		PayUtils.balanceMonthPrice(paylist, rePaylineList);
		//packagePaylines(paylist);
		paylist.put("irrMonthPaylines", keepPackagePayline(context));	
		//2011-4-2 wuzd 期初方式把应付租金前提
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			//胡昭卿加,为计算期末抵充金额时用
			
				if(context.contextMap.get("PLEDGE_LAST_PERIOD")!=null){
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", context.contextMap.get("PLEDGE_LAST_PERIOD").toString());
				}else{
					paylist.put("PAY_PLEDGE_LAST_PERIOD_HUADD", "0");
				}
				
				

		}	
		paylist.put("oldpaylineList", rePaylineList);
		rentPremise(paylist);
		return paylist;
		
	}
	/**
	 * 
	 * @param context
	 * @param equipList
	 * @param lockedPaylineList
	 * @param holdPaylistList
	 * @param lastPayline
	 * @param lockedPaylineFlag
	 * @param passedIndex
	 * @param sumHoldPrice
	 * @param sumHoldMonthPrice
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map changePaylist(Context context, List errList, Map oldPaylist, List<Map> equipList,
			List<Map> lockedPaylineList, List<Map> holdPaylistList,
			Map lastPayline, int passedIndex,
			Double sumHoldPrice) {

		context.contextMap.put("START_DATE", HTMLUtil.parseDateParam(String.valueOf(context.contextMap.get("START_DATE")), null));
		
		Map schema = new HashMap();
		//
		copySchema(schema, context.contextMap);
		schema.put("START_DATE", lastPayline.get("PAY_DATE"));
		//
		int changeNum = DataUtil.intUtil(context.contextMap.get("CHANGE_NUM"));
		int nowLeasePeriod = DataUtil.intUtil(context.contextMap.get("LEASE_PERIOD"));
		int leasePeriod = DataUtil.intUtil(oldPaylist.get("LEASE_PERIOD"));
		//
		if (nowLeasePeriod <= passedIndex || nowLeasePeriod < changeNum) {
			
			errList.add("设置了无效的租赁期数！此支付表已分解："+passedIndex+" 期，您设置的还款期数为：" 
					+ nowLeasePeriod + "期，您选择的开始变更的期数为：" + changeNum +"期");
			
			return oldPaylist;
		
		} else {
			//
			leasePeriod = nowLeasePeriod + 1 - changeNum;
			schema.put("LEASE_PERIOD", leasePeriod);
		}
		// ==========fire=========================================================================
		double newRZE = DataUtil.doubleUtil(context.contextMap.get("LEASE_RZE"));
		double oldRZE = DataUtil.doubleUtil(oldPaylist.get("LEASE_RZE"));
		
		logger.info("newRZE:" + newRZE + ";oldRZE:" + oldRZE);

		if (newRZE != oldRZE) {
		
			schema.put("TOTAL_PRICE", DataUtil.doubleUtil(context.contextMap.get("TOTAL_PRICE")) - sumHoldPrice);
			schema.put("LEASE_TOPRIC", DataUtil.doubleUtil(context.contextMap.get("TOTAL_PRICE")) - sumHoldPrice);
		
		} else {
		
			schema.put("TOTAL_PRICE", DataUtil.doubleUtil(lastPayline.get("LAST_PRICE")));
			schema.put("LEASE_TOPRIC", DataUtil.doubleUtil(lastPayline.get("LAST_PRICE")) );
	
		}
		logger.info("新的设备总价：" + DataUtil.doubleUtil(schema.get("TOTAL_PRICE")));
		// 2010-09-19 v1.6
		inferYearInterestByIrr(schema, lockedPaylineList);
		//
		List<Map> insureList = getInsureList(context);
		List<Map> otherFeeList = getOtherFeeList(context);
		
		Map paylist = null;
		//
		int payWay = DataUtil.intUtil(schema.get("PAY_WAY"));
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL){
			paylist = PayUtils.recalculate(schema, equipList, insureList, otherFeeList, lockedPaylineList);
		} else {
			paylist = PayUtils.calculate(schema, equipList, insureList, otherFeeList);
		}
		// connect
		List<Map> newPaylines = new ArrayList<Map>();
		for (Map payline : holdPaylistList) {
			newPaylines.add(payline);
		}
		for (Map payline : (List<Map>) paylist.get("paylines")) {
			payline.put("PERIOD_NUM", DataUtil.intUtil(payline.get("PERIOD_NUM")) - 1 + changeNum);
			newPaylines.add(payline);
		}
		
		paylist.put("paylines", newPaylines);
		
		//
		copyPaylistInfo(paylist, context.contextMap);
		
		//
		//packagePaylines(paylist);
		paylist.put("irrMonthPaylines", keepPackagePayline(context));
		
		
		return paylist;
		
	}
	/**
	 * 
	 * @param dest
	 * @param src
	 */
	@SuppressWarnings("unchecked")
	public static void copyPaylistInfo(Map dest, Map src) {
		dest.put("RECP_ID", src.get("RECP_ID"));
		dest.put("RECT_ID", src.get("RECT_ID"));
		dest.put("RECP_CODE", src.get("RECP_CODE"));
		
		dest.put("LEASE_PERIOD", src.get("LEASE_PERIOD"));
		
		dest.put("DEAL_WAY", src.get("DEAL_WAY"));
		dest.put("EQUPMENT_ADDRESS", src.get("EQUPMENT_ADDRESS"));
		dest.put("BUSINESS_TRIP_PRICE", src.get("BUSINESS_TRIP_PRICE"));
		dest.put("BUY_INSURANCE_WAY", src.get("BUY_INSURANCE_WAY"));
		dest.put("BUY_INSURANCE_TIME", src.get("BUY_INSURANCE_TIME"));
		dest.put("INSURE_REBATE_RATE", src.get("INSURE_REBATE_RATE"));
		dest.put("INSURANCE_COMPANY_ID", src.get("INSURANCE_COMPANY_ID"));
		dest.put("LEASE_RZE", src.get("LEASE_RZE"));
		// dest.put("END_DATE", src.get("END_DATE"));
		// dest.put("VERSION_CODE", src.get("VERSION_CODE"));
	}
	/**
	 * 
	 * @param dest
	 * @param src
	 */
	@SuppressWarnings("unchecked")
	public static void copySchema(Map dest, Map src) {
		
		 
		dest.put("LEASE_PERIOD", src.get("LEASE_PERIOD"));
		dest.put("LEASE_TERM", src.get("LEASE_TERM"));
		dest.put("PLEDGE_PRICE", src.get("PLEDGE_PRICE"));
		dest.put("PLEDGE_PRICE_RATE", src.get("PLEDGE_PRICE_RATE"));
		dest.put("HEAD_HIRE", src.get("HEAD_HIRE"));
		dest.put("HEAD_HIRE_PERCENT", src.get("HEAD_HIRE_PERCENT"));
		dest.put("MANAGEMENT_FEE", src.get("MANAGEMENT_FEE"));
		dest.put("MANAGEMENT_FEE_RATE", src.get("MANAGEMENT_FEE_RATE"));
		dest.put("FLOAT_RATE", src.get("FLOAT_RATE"));
		dest.put("YEAR_INTEREST_TYPE", src.get("YEAR_INTEREST_TYPE"));
		dest.put("YEAR_INTEREST", src.get("YEAR_INTEREST"));
		dest.put("PAY_WAY", src.get("PAY_WAY"));
		dest.put("FINE_TYPE", src.get("FINE_TYPE"));
		dest.put("FINE_RATE", src.get("FINE_RATE"));
		dest.put("START_DATE", src.get("START_DATE"));
		
		dest.put("DEAL_WAY", src.get("DEAL_WAY"));
		dest.put("EQUPMENT_ADDRESS", src.get("EQUPMENT_ADDRESS"));
		dest.put("BUSINESS_TRIP_PRICE", src.get("BUSINESS_TRIP_PRICE"));
		dest.put("BUY_INSURANCE_WAY", src.get("BUY_INSURANCE_WAY"));
		dest.put("BUY_INSURANCE_TIME", src.get("BUY_INSURANCE_TIME"));
		dest.put("INSURE_REBATE_RATE", src.get("INSURE_REBATE_RATE"));
		dest.put("INSURANCE_COMPANY_ID", src.get("INSURANCE_COMPANY_ID"));
		dest.put("LEASE_RZE", src.get("LEASE_RZE"));
		
		dest.put("LEASE_TOPRIC", src.get("LEASE_TOPRIC")); // contract price
		dest.put("CONTRACT_PRICE", src.get("LEASE_TOPRIC")); // paylist price
		dest.put("TOTAL_PRICE", src.get("TOTAL_PRICE")); // paylist price
		
		//
		dest.put("SALES_TAX_RATE", src.get("SALES_TAX_RATE"));
		dest.put("PLEDGE_WAY", src.get("PLEDGE_WAY"));
		dest.put("PLEDGE_PERIOD", src.get("PLEDGE_PERIOD"));
		dest.put("INSURE_BASE_RATE", src.get("INSURE_BASE_RATE"));
		dest.put("STAMP_TAX_TOPRIC", src.get("STAMP_TAX_TOPRIC"));
		dest.put("STAMP_TAX_MONTHPRIC", src.get("STAMP_TAX_MONTHPRIC"));
		dest.put("STAMP_TAX_INSUREPRIC", src.get("STAMP_TAX_INSUREPRIC"));
		//
		dest.put("PLEDGE_REALPRIC", src.get("PLEDGE_REALPRIC"));
		
	}
	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map>	getInsureList(Context context) {
		
		List<Map> insureList = new ArrayList<Map>();
		
		String[] reciIds = HTMLUtil.getParameterValues(context.request, "RECI_ID", "");
		String[] insureItems = HTMLUtil.getParameterValues(context.request, "INSURE_ITEM", "");
		String[] startDates = HTMLUtil.getParameterValues(context.request, "INSURE_START_DATE", "");
		String[] endDates = HTMLUtil.getParameterValues(context.request, "INSURE_END_DATE", "");
		String[] insureRates = HTMLUtil.getParameterValues(context.request, "INSURE_RATE", "");
		String[] insurePrices = HTMLUtil.getParameterValues(context.request, "INSURE_PRICE", "");
		String[] insureMemos = HTMLUtil.getParameterValues(context.request, "INSURE_MEMO", "");
		
		for (int i=0; i<(insureItems==null?0:insureItems.length);i++) {
			Map insure = new HashMap();
			insure.put("RECI_ID", reciIds[i]);
			insure.put("INSURE_ITEM", insureItems[i]);
			insure.put("START_DATE", startDates[i]);
			insure.put("END_DATE",endDates[i]);
			insure.put("INSURE_RATE", insureRates[i]);
			insure.put("MEMO", insureMemos[i]);
			insure.put("INSURE_PRICE", insurePrices[i]);
			insureList.add(insure);
		}
		
		return insureList;
	}
	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> getOtherFeeList(Context context) {
		
		List<Map> otherFeeList = new ArrayList<Map>();
		
		String[] recoIds = HTMLUtil.getParameterValues(context.request, "RECO_ID", "");
		String[] otherNames = HTMLUtil.getParameterValues(context.request, "OTHER_NAME", "");
		String[] otherPrices = HTMLUtil.getParameterValues(context.request, "OTHER_PRICE", "");
		String[] otherDates = HTMLUtil.getParameterValues(context.request, "OTHER_DATE", "");
		String[] otherMemos = HTMLUtil.getParameterValues(context.request, "OTHER_MEMO", "");
		
		for (int i=0; i<(otherNames==null?0:otherNames.length);i++) {
			Map otherFee = new HashMap();
			otherFee.put("RECO_ID", recoIds[i]);
			otherFee.put("OTHER_NAME", otherNames[i]);
			otherFee.put("OTHER_DATE", otherDates[i]);
			otherFee.put("MEMO", otherMemos[i]);
			otherFee.put("OTHER_PRICE", otherPrices[i]);
			otherFeeList.add(otherFee);
		}
		return otherFeeList;
	}
	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> getPaylineList(Context context) {
		List<Map> rePaylineList = new ArrayList<Map>();
		
		String[] periodNums = HTMLUtil.getParameterValues(context.request, "PERIOD_NUM", "0");
		String[] monthPrices = HTMLUtil.getParameterValues(context.request, "MONTH_PRICE", "0");
		
		for (int i=0;i<(periodNums==null?0:periodNums.length);i++) {
			Map line = new HashMap();
			line.put("PERIOD_NUM", periodNums[i]);
			line.put("MONTH_PRICE", monthPrices[i]);
			rePaylineList.add(line);
		}
		
		return rePaylineList;
	}
	/**
	 * 
	 * @param sid 
	 * @param flag
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> queryPackagePayline(Object sid,Integer flag) throws Exception {
		List<Map> irrMonthPaylines = null;
		Map paramMap = new HashMap();
		if (flag == 1) {
			paramMap.put("CREDIT_ID", sid);
			irrMonthPaylines = (List<Map>)DataAccessor.query("creditReportManage.readCreditSchemaIrr", paramMap, DataAccessor.RS_TYPE.LIST);
		} else if (flag == 2) {
			
		} else if (flag ==3) {
			
		}
	
		logger.info("应付租金表原始组为:"+irrMonthPaylines.size());
		return irrMonthPaylines;

	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> keepPackagePayline(Context context) {
		
		logger.info("up package paylines start ......");
		
		List<Map> irrMonthPaylines = new ArrayList<Map>();
		
		Map irrMonthPayline = null;
		
		String[] irrMonthPirces = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE", "0");
		String[] startNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_START", "0");
		String[] endNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_END", "0");
		
		for (int i=0; i<(startNums==null?0:startNums.length); i++) {
			
			double irrMonthPrice = DataUtil.doubleUtil(irrMonthPirces[i]);
			int startNum = DataUtil.intUtil(startNums[i]);
			int endNum = DataUtil.intUtil(endNums[i]);
			
			irrMonthPayline = new HashMap();
			irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
			irrMonthPayline.put("IRR_MONTH_PRICE_START", startNum);
			irrMonthPayline.put("IRR_MONTH_PRICE_END", endNum);
			
			irrMonthPaylines.add(irrMonthPayline);
			
		}

		return irrMonthPaylines;
		
	}
	/**
	 * 
	 * @param irrMonthPaylines
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> upPackagePaylines(Collection<Map> irrMonthPaylines) {
		
		List<Map> rePaylineList = new ArrayList<Map>();
		
		for (Map map : irrMonthPaylines) {
			// IRR_MONTH_PRICE,IRR_MONTH_PRICE_START,IRR_MONTH_PRICE_END
			double irrMonthPrice = DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"));
			int startNum = DataUtil.intUtil(map.get("IRR_MONTH_PRICE_START"));
			int endNum = DataUtil.intUtil(map.get("IRR_MONTH_PRICE_END"));
			
			for (int i=startNum; i<=endNum; i++) {
				
				Map line = new HashMap();
				line.put("PERIOD_NUM", i);
				line.put("MONTH_PRICE", irrMonthPrice);
				line.put("IRR_MONTH_PRICE", irrMonthPrice);
				rePaylineList.add(line);
			
			}
		}
		
		return rePaylineList;
	}
	
	//Add by Michael 2012 11-27 For增值税
	public static List<Map> upPackagePaylinesForValueTax(Collection<Map> irrMonthPaylines) {
		
		List<Map> rePaylineList = new ArrayList<Map>();
		
		for (Map map : irrMonthPaylines) {
			// IRR_MONTH_PRICE,IRR_MONTH_PRICE_START,IRR_MONTH_PRICE_END
			double irrMonthPriceTax = DataUtil.doubleUtil(map.get("MONTH_PRICE_TAX"));
			double irrMonthPrice = DataUtil.doubleUtil(map.get("MONTH_PRICE"));
			int startNum = DataUtil.intUtil(map.get("MONTH_PRICE_START"));
			int endNum = DataUtil.intUtil(map.get("MONTH_PRICE_END"));
			
			for (int i=startNum; i<=endNum; i++) {
				
				Map line = new HashMap();
				line.put("PERIOD_NUM", i);
				line.put("MONTH_PRICE", irrMonthPrice);
				line.put("MONTH_PRICE_TAX", irrMonthPriceTax);
				rePaylineList.add(line);
			
			}
		}
		
		return rePaylineList;
	}
	
	//Add by Michael 2012 11-27 For展开支票支付明细
	public static List<Map> upRentCheckPayLines(Collection<Map> checkPaySchema) {
		
		List<Map> rePaylineList = new ArrayList<Map>();
		
		for (Map map : checkPaySchema) {
			int startNum = DataUtil.intUtil(map.get("CHECK_START"));
			int endNum = DataUtil.intUtil(map.get("CHECK_END"));
			
			for (int i=startNum; i<=endNum; i++) {
				
				Map line = new HashMap();
				line.put("PERIOD_NUM", i);
				rePaylineList.add(line);
			
			}
		}
		
		return rePaylineList;
	}
	/**
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> upPackagePaylines(Context context) {
		
		logger.info("up package paylines start ......");
		
		List<Map> rePaylineList = new ArrayList<Map>();
		
		String[] irrMonthPirces = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE", "0");
		String[] startNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_START", "0");
		String[] endNums = HTMLUtil.getParameterValues(context.request, "PAY_IRR_MONTH_PRICE_END", "0");
		
		for (int i=0; i<(startNums==null?0:startNums.length); i++) {
			
			double irrMonthPrice = DataUtil.doubleUtil(irrMonthPirces[i]);
			int startNum = DataUtil.intUtil(startNums[i]);
			int endNum = DataUtil.intUtil(endNums[i]);
			
			for (int j=startNum;j<=endNum;j++){
				
				Map line = new HashMap();
				line.put("PERIOD_NUM", j);
				line.put("MONTH_PRICE", irrMonthPrice);
				line.put("IRR_MONTH_PRICE", irrMonthPrice);
				rePaylineList.add(line);
				
			}
			
		}

		return rePaylineList;
		
	}
	/**
	 * 
	 * @param paylist
	 */
	@SuppressWarnings("unchecked")
	public static void packagePaylines(Map paylist) {
		
		int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
		
		List<Map> paylines = paylist == null ? null : (List<Map>)paylist.get("paylines");
		
		if (paylines != null) {
			
			List<Map> irrMonthPaylines = new ArrayList<Map>();
			
			Map irrMonthPayline = null;
			
			double tempIrrMonthPrice = 0d;
			double irrMonthPrice = 0d;
			
			int periodNum = 1;
			
			for (Map payline : paylines) {
				
				periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
				irrMonthPrice = Math.round(DataUtil.doubleUtil(payline.get("IRR_MONTH_PRICE"))*HUNDRED)/HUNDRED;
				
				if (periodNum == 1) {
					
					tempIrrMonthPrice = irrMonthPrice;
					
					irrMonthPayline = new HashMap();
					irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
					irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
					irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
					
				} else if (periodNum == paylines.size()) {
					
					if (irrMonthPrice == tempIrrMonthPrice) {
						
						irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
						
					} else {
						
						if (payWay == 11 || payWay == 12 || payWay == 13) {
							
							irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
							
						} else {
							
							irrMonthPaylines.add(irrMonthPayline);
							
							irrMonthPayline = new HashMap();
							irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
							irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
							irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
							
						}
						
						
					}
					
					irrMonthPaylines.add(irrMonthPayline);
					
				} else {
					
					if (irrMonthPrice == tempIrrMonthPrice) {
						
						irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
						
					} else {
						
						irrMonthPaylines.add(irrMonthPayline);
						
						tempIrrMonthPrice = irrMonthPrice;
						
						irrMonthPayline = new HashMap();
						irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
						irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
						irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
						
					}
					
				}
				
			}
			
			// paylist.put("irrMonthPaylines", irrMonthPaylines);
			setIrrMonthPayline(paylist, irrMonthPaylines);
			
		}
		
		
	}
	/**
	 * 
	 * @param paylist
	 */
	@SuppressWarnings("unchecked")
	public static void packagePaylinesForMon(Map paylist) {
	    
	    int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
	    
	    List<Map> paylines = paylist == null ? null : (List<Map>)paylist.get("paylines");
	    
	    if (paylines != null) {
		
		List<Map> irrMonthPaylines = new ArrayList<Map>();
		
		Map irrMonthPayline = null;
		
		double tempIrrMonthPrice = 0d;
		double irrMonthPrice = 0d;
		
		int periodNum = 1;
		
		for (Map payline : paylines) {
		    
		    periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
		    irrMonthPrice = Math.round(DataUtil.doubleUtil(payline.get("MONTH_PRICE"))*HUNDRED)/HUNDRED;
		    
		    if (periodNum == 1) {
			
				tempIrrMonthPrice = irrMonthPrice;
				
				irrMonthPayline = new HashMap();
				irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
				irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
				irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
			
		    } else if (periodNum == paylines.size()) {
			
				if (irrMonthPrice == tempIrrMonthPrice) {
				    
				    irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
				    
				} else {
					
					irrMonthPaylines.add(irrMonthPayline);
					
					irrMonthPayline = new HashMap();
					irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
					irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
					irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
				    
				}
			
				irrMonthPaylines.add(irrMonthPayline);
			
		    } else {
			
				if (irrMonthPrice == tempIrrMonthPrice) {
				    
				    irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
				    
				} else {
				    
				    irrMonthPaylines.add(irrMonthPayline);
				    
				    tempIrrMonthPrice = irrMonthPrice;
				    
				    irrMonthPayline = new HashMap();
				    irrMonthPayline.put("IRR_MONTH_PRICE", irrMonthPrice);
				    irrMonthPayline.put("IRR_MONTH_PRICE_START", periodNum);
				    irrMonthPayline.put("IRR_MONTH_PRICE_END", periodNum);
				    
				}
			
		    }
		    
		}
		
		paylist.put("irrMonthPaylines", irrMonthPaylines);
		
	    }
	    
	    
	}
	
	
	@SuppressWarnings("unchecked")
	public static void packagePaylinesForValueAdded(Map paylist) {
	    
	    int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
	    double totalValueAddedTax=DataUtil.doubleUtil(paylist.get("TOTAL_VALUEADDED_TAX"));
		//平均每期的增值税
	    double valueAddedTax=0.0;
	    double tempTotalValueAdded =0.0;
	    double lastValueAddedTax=0.0;
		valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(paylist.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
		
		for (int i=1;i<=DataUtil.intUtil(paylist.get("LEASE_PERIOD"));i++){
			if(i==DataUtil.intUtil(paylist.get("LEASE_PERIOD"))){
				lastValueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
			}
			tempTotalValueAdded+= valueAddedTax;
		}
		
		List<Map> irrMonthPaylinesTemp = paylist == null ? null : (List<Map>)paylist.get("oldirrMonthPaylines");
		if(irrMonthPaylinesTemp == null){
			irrMonthPaylinesTemp = new ArrayList() ;
		}
		List irrMonthPaylines =new ArrayList();
		
		if(irrMonthPaylinesTemp.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylinesTemp.get(irrMonthPaylinesTemp.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			for(int i=0;i<irrMonthPaylinesTemp.size();i++){
				Map temp = (Map) irrMonthPaylinesTemp.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylinesTemp.size() - 1  ){
					if(valueAddedTax!=lastValueAddedTax){
						if(start != end ){
							map = new HashMap() ;
							map.put("MONTH_PRICE_START",start ) ;
							map.put("MONTH_PRICE_END",end - 1 ) ;
							map.put("MONTH_PRICE", price ) ;
							map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
							irrMonthPaylines.add(map) ;
						} 
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",end ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE", price ) ;
						map.put("MONTH_PRICE_TAX", price + lastValueAddedTax) ;
						irrMonthPaylines.add(map) ;
					}else {
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE", price ) ;
						map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
						irrMonthPaylines.add(map) ;
					}
				}else {
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",start ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price ) ;
					map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
					irrMonthPaylines.add(map) ;
				}
			}
		}
		
		paylist.put("irrMonthPaylines", irrMonthPaylines);
		paylist.put("valueAddedTax", valueAddedTax);
		paylist.put("lastValueAddedTax", lastValueAddedTax);

	}
	
	//计算平均冲抵的增值税
	public static void calculateAveValueAdded(Map paylist) {
	    
	    double totalValueAddedTax=DataUtil.doubleUtil(paylist.get("TOTAL_VALUEADDED_TAX"));
		//平均每期的增值税
	    double valueAddedTax=0.0;
	    double tempTotalValueAdded =0.0;
	    double lastValueAddedTax=0.0;
		valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(paylist.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
		
		for (int i=1;i<=DataUtil.intUtil(paylist.get("LEASE_PERIOD"));i++){
			if(i==DataUtil.intUtil(paylist.get("LEASE_PERIOD"))){
				lastValueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
			}
			tempTotalValueAdded+= valueAddedTax;
		}
		paylist.put("valueAddedTax", valueAddedTax);
		paylist.put("lastValueAddedTax", lastValueAddedTax);

	}
	
	/**
	 * 
	 * @param dest
	 * @param src
	 */
	@SuppressWarnings("unchecked")
	public static void copyPayline(Map dest, Map src) {
		dest.put("RECD_ID", src.get("RECD_ID"));
		dest.put("RECP_ID", src.get("RECP_ID"));
		dest.put("LOCKED", src.get("LOCKED"));
		dest.put("PERIOD_NUM", src.get("PERIOD_NUM"));
		dest.put("PAY_DATE", src.get("PAY_DATE"));
		dest.put("MONTH_PRICE", src.get("MONTH_PRICE"));
		dest.put("OWN_PRICE", src.get("OWN_PRICE"));
		dest.put("REN_PRICE", src.get("REN_PRICE"));
		dest.put("LAST_PRICE", src.get("LAST_PRICE"));
		dest.put("LOSS_PRICE", src.get("LOSS_PRICE"));
		dest.put("OTHER_PRICE", src.get("OTHER_PRICE"));
		dest.put("REDUCE_OWN_PRICE", src.get("REDUCE_OWN_PRICE"));
		dest.put("REDUCE_REN_PRICE", src.get("REDUCE_REN_PRICE"));
		dest.put("REDUCE_OTHER_PRICE", src.get("REDUCE_OTHER_PRICE"));
		dest.put("REDUCE_LOSS_PRICE", src.get("REDUCE_LOSS_PRICE"));
		//
		dest.put("DEPOSIT_PRICE", src.get("DEPOSIT_PRICE"));
		dest.put("IRR_PRICE", src.get("IRR_PRICE"));
		dest.put("IRR_MONTH_PRICE", src.get("IRR_MONTH_PRICE"));
		dest.put("SALES_TAX", src.get("SALES_TAX"));
		dest.put("INSURE_PRICE", src.get("INSURE_PRICE"));
	}
	/**
	 * 
	 * @param paylist
	 * @param irrMonthPaylines
	 */
	@SuppressWarnings("unchecked")
	public static void setIrrMonthPayline(Map paylist,Collection<Map> irrMonthPaylines) {
		
		paylist.put("irrMonthPaylines", irrMonthPaylines);
		
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void rentPremise(Map paylist){
		List paylines = (List) paylist.get("paylines");
		List oldpaylines = (List) paylist.get("oldpaylineList");
		int PAY_PLEDGE_LAST_PERIOD=0;
		if(paylist.get("PAY_PLEDGE_LAST_PERIOD_HUADD")!=null){
			PAY_PLEDGE_LAST_PERIOD=Integer.parseInt(paylist.get("PAY_PLEDGE_LAST_PERIOD_HUADD").toString());
		}
		for (int i = 0; i < paylines.size(); i++) {
			Map payline = (Map) paylines.get(i);
			Map oldpayline =new HashMap();
			if(oldpaylines.size()>0){
				oldpayline = (Map) (oldpaylines.get(i)==null?paylines.get(i):oldpaylines.get(i));
			}else{
				oldpayline = (Map) paylines.get(i);
			}
			if(i<=paylines.size()-PAY_PLEDGE_LAST_PERIOD-1){
				payline.put("IRR_MONTH_PRICE", oldpayline.get("IRR_MONTH_PRICE"));
			}else{
				//Map payline2 = (Map) paylines.get(i+1);
				//payline.put("IRR_MONTH_PRICE", 0);
				payline.put("IRR_MONTH_PRICE", oldpayline.get("IRR_MONTH_PRICE"));
			}
		}
		/*for (int i = 0; i < paylines.size(); i++) {
			Map payline = (Map) paylines.get(i);
			if (i == paylines.size()-1) {
				payline.put("IRR_MONTH_PRICE", 0);
			}else if(i==paylines.size()-2||i==paylines.size()-PAY_PLEDGE_LAST_PERIOD-2){
				payline.put("IRR_MONTH_PRICE", payline.get("IRR_MONTH_PRICE"));
			}else{
				Map payline2 = (Map) paylines.get(i+1);
				payline.put("IRR_MONTH_PRICE", payline2.get("IRR_MONTH_PRICE"));
			}
			
		}
		*/
		
	}
}
