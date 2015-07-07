package com.brick.collection.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.collection.CollectionConstants;
import com.brick.collection.core.DefaultPayableStrategy;
import com.brick.collection.core.IRRUtils;
import com.brick.collection.core.PVUtils;
import com.brick.collection.core.Pay;
import com.brick.collection.core.PayItem;
import com.brick.collection.support.PayRate;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.DataAccessor;
import com.brick.util.Constants;
import com.brick.util.DataUtil;

/**
 * @author wujw
 * @date Jun 8, 2010
 * @version
 */
public class PayUtils {
	/** logger. */
	private static Log logger = LogFactory.getLog(PayUtils.class);
	
	public static final double HUNDRED = 100D;
	
	public static final int MONTHS_OF_YEAR = 12;

	/** protected constructor. */
	protected PayUtils() {
		logger.debug("start");
	}
	/**
	 * 
	 * @param contractSchema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map calculate(Map schema, List<Map> equipList,
			List<Map> insureList, List<Map> otherFeeList) {
		Pay pay = populatePay(schema);

		return populatePaylist(pay, schema, equipList, insureList, otherFeeList);
	}
	/**
	 * 
	 * @param contractSchema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map calculateScale(Map schema, List<Map> equipList,
			List<Map> insureList, List<Map> otherFeeList) {
		Pay pay = populatePayScale(schema);
		
		return populatePaylistScale(pay, schema, equipList, insureList, otherFeeList);
	}
	/**
	 * 
	 * @param schema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @param paylineList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map recalculate(Map schema, List<Map> equipList,
			List<Map> insureList, List<Map> otherFeeList,List<Map> paylineList) {
		Pay pay = populatePay(schema);
		//paylineList解压后的每一期的钱
		for (Map payline : paylineList) {
            int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
            double itemMonthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
            pay.updateMonthPrice(periodNum, itemMonthPrice);
        }

        DefaultPayableStrategy.recalculate(pay,
            DefaultPayableStrategy.BALANCE_TYPE_BASE);
        
		return populatePaylist(pay, schema, equipList, insureList, otherFeeList);
	}
	/**
	 * 
	 * @param schema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @param paylineList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	//合同生成支付表  带SCALE按比例拆分  一张合同有N张支付表
	public static Map recalculateScale(Map schema, List<Map> equipList,
			List<Map> insureList, List<Map> otherFeeList,List<Map> paylineList) {
		Pay pay = populatePayScale(schema);
		
		for (Map payline : paylineList) {
			int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
			double itemMonthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			pay.updateMonthPrice(periodNum, itemMonthPrice);
		}
		
		DefaultPayableStrategy.recalculate(pay,
				DefaultPayableStrategy.BALANCE_TYPE_BASE);
		
		return populatePaylistScale(pay, schema, equipList, insureList, otherFeeList);
	}
	/**
	 * 
	 * @param pay
	 * @param contractSchema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map populatePaylistScale(Pay pay, Map schema,
			Collection<Map> equipList, Collection<Map> insureList,
			List<Map> otherFeeList) {

		//
		Map paylist = new HashMap();
		paylist.put("payEquipments", equipList);
		//TOTAL_PRICE设备款    LEASE_TOPRIC合同总价
		double totalPrice = DataUtil.doubleUtil(schema.get("TOTAL_PRICE"));
		double leaseTopic = DataUtil.doubleUtil(schema.get("LEASE_TOPRIC"));
		double priceRate = totalPrice / leaseTopic;
		
		//CONTRACT_PRICE合同总价
		paylist.put("CONTRACT_PRICE", leaseTopic);
		paylist.put("LEASE_TOPRIC", totalPrice);
		//paylist.put("LEASE_RZE", pay.getRestPrice());
		paylist.put("LEASE_RZE", schema.get("LEASE_RZE"));
		//
		paylist.put("LEASE_TERM", schema.get("LEASE_TERM"));
		paylist.put("LEASE_PERIOD", schema.get("LEASE_PERIOD"));
		// paylist.put("PLEDGE_PRICE", DataUtil.doubleUtil(schema.get("PLEDGE_PRICE")) * priceRate);
		paylist.put("PLEDGE_PRICE", pay.getPledgePrice());
		paylist.put("PLEDGE_PRICE_RATE", schema.get("PLEDGE_PRICE_RATE"));
		paylist.put("HEAD_HIRE", DataUtil.doubleUtil(schema.get("HEAD_HIRE")) * priceRate);
		paylist.put("HEAD_HIRE_PERCENT", schema.get("HEAD_HIRE_PERCENT"));
		paylist.put("MANAGEMENT_FEE", DataUtil.doubleUtil(schema.get("MANAGEMENT_FEE")) * priceRate);
		paylist.put("MANAGEMENT_FEE_RATE", schema.get("MANAGEMENT_FEE_RATE"));
		// paylist.put("FLOAT_RATE", schema.get("FLOAT_RATE"));
		paylist.put("FLOAT_RATE", pay.getUpRate());
		// paylist.put("YEAR_INTEREST", schema.get("YEAR_INTEREST"));
		paylist.put("YEAR_INTEREST", pay.getYearRate());
		paylist.put("YEAR_INTEREST_TYPE", schema.get("YEAR_INTEREST_TYPE"));
		paylist.put("FINE_RATE", schema.get("FINE_RATE"));
		paylist.put("FINE_TYPE", schema.get("FINE_TYPE"));
		
		paylist.put("PAY_WAY", schema.get("PAY_WAY"));
		paylist.put("START_DATE", schema.get("START_DATE"));
		if (schema.get("START_DATE") != null ) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date)schema.get("START_DATE"));
			calendar.add(Calendar.MONTH, (DataUtil.intUtil(schema.get("LEASE_PERIOD")) - 1)*DataUtil.intUtil(schema.get("LEASE_TERM")));
			paylist.put("END_DATE", calendar.getTime());
		} else {
			paylist.put("END_DATE", "");
		}
		//
		paylist.put("INSURANCE_COMPANY_ID", schema.get("INSURANCE_COMPANY_ID"));
		paylist.put("BUY_INSURANCE_WAY", schema.get("BUY_INSURANCE_WAY"));
		paylist.put("BUY_INSURANCE_TIME", schema.get("BUY_INSURANCE_TIME"));
		paylist.put("INSURE_REBATE_RATE", schema.get("INSURE_REBATE_RATE"));
		//
		paylist.put("DEAL_WAY", schema.get("DEAL_WAY"));
		paylist.put("EQUPMENT_ADDRESS", schema.get("EQUPMENT_ADDRESS"));
		paylist.put("BUSINESS_TRIP_PRICE", DataUtil.doubleUtil(schema.get("BUSINESS_TRIP_PRICE")) * priceRate);
		//
		
		//2010-08
		paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
		// use the configuration
		//Add by Michael 2012 09-21 增加税费配置方案，根据不同的方案读取不同的税率
		paylist.put("TAX_PLAN_CODE", schema.get("TAX_PLAN_CODE"));
		//Add by Michael  For 重车 不需要计算保险费费率
		String contractType= String.valueOf(schema.get("CONTRACT_TYPE"));
		PaylistUtil.setBaseRate(paylist,contractType);
		
		//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算
		paylist.put("FEESET_TOTAL", schema.get("FEESET_TOTAL"));
		
		//PaylistUtil.setBaseRate(paylist);
		//
		paylist.put("PLEDGE_AVE_PRICE", schema.get("PLEDGE_AVE_PRICE"));
		paylist.put("PLEDGE_BACK_PRICE", schema.get("PLEDGE_BACK_PRICE"));
		paylist.put("PLEDGE_LAST_PRICE", schema.get("PLEDGE_LAST_PRICE"));
		paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
		paylist.put("PLEDGE_ENTER_WAY", schema.get("PLEDGE_ENTER_WAY"));
		paylist.put("PLEDGE_ENTER_CMPRICE", schema.get("PLEDGE_ENTER_CMPRICE"));
		paylist.put("PLEDGE_ENTER_CMRATE", schema.get("PLEDGE_ENTER_CMRATE"));
		paylist.put("PLEDGE_ENTER_AG", schema.get("PLEDGE_ENTER_AG"));
		paylist.put("LOAN_RATE", schema.get("LOAN_RATE"));
		paylist.put("MANAGE_RATE", schema.get("MANAGE_RATE"));
		//
		paylist.put("PLEDGE_REALPRIC", schema.get("PLEDGE_REALPRIC"));

		//
		List<Map> payInusres = new ArrayList<Map>();
		for (Map map : insureList) {
			Map payInsure = new HashMap();
			payInsure.put("INSURE_ITEM", map.get("INSURE_ITEM"));
			payInsure.put("START_DATE", map.get("START_DATE"));
			payInsure.put("END_DATE", map.get("END_DATE"));
			payInsure.put("INSURE_RATE", map.get("INSURE_RATE"));
			payInsure.put("MEMO", map.get("MEMO"));
			payInsure.put("INSURE_PRICE", DataUtil.doubleUtil(map.get("INSURE_PRICE")) * priceRate);
			payInusres.add(payInsure);
		}
		paylist.put("payInusres", payInusres);
		
		//
		List<Map> payOtherFees = new ArrayList<Map>();
		for (Map map : otherFeeList) {
			Map payOtherFee = new HashMap();
			payOtherFee.put("OTHER_NAME", map.get("OTHER_NAME"));
			payOtherFee.put("OTHER_DATE", map.get("OTHER_DATE"));
			payOtherFee.put("MEMO", map.get("MEMO"));
			payOtherFee.put("OTHER_PRICE", DataUtil.doubleUtil(map.get("OTHER_PRICE")) * priceRate);
			payOtherFees.add(payOtherFee);
		}
		paylist.put("payOtherFees", payOtherFees);
		
		//
		List<Map> paylines = new ArrayList<Map>();
		
		for (PayItem payItem : pay.getPayItems()) {
			Map item = new HashMap();
			item.put("PERIOD_NUM", payItem.getIndex());
			item.put("PAY_DATE", payItem.getPayDate());
			item.put("MONTH_PRICE", payItem.getMonthPrice());
			item.put("OWN_PRICE", payItem.getOwnPrice());
			item.put("REN_PRICE", payItem.getRenPrice());
			item.put("LAST_PRICE", payItem.getLastPrice());

			if (payItem.isLocked()) {
				item.put("LOCKED", Integer.valueOf(1));
			} else {
				item.put("LOCKED", Integer.valueOf(0));
			}
			
			paylines.add(item);

		}
		
		paylist.put("paylines", paylines);
		//可以根据不同的税费方案来进行计算  1是普通的方案  2是增值税方案
		
		if ("2".equals(schema.get("TAX_PLAN_CODE"))){
			//---Add by Michael 2012 09-24 可以在此次 进行  方案类别的判断来进行相应的税率计算
			
			//Add by Michael 2012-09-12  增加增值税的计算
			double stampTaxRateAlone = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE_ALONE"));  //去除复税税率
			//总利息 * 17% =总增值税  ；总增值税*总期数   得出每一期的的增值税
			double totalRenPrice=0.0;   //总利息
			
			for (Map payline : paylines) {
				totalRenPrice += DataUtil.doubleUtil(payline.get("REN_PRICE"));
			}
			
			//实际每期的增值税
			double valueAddedTaxTrue=0.0;
			
			//  得出总的利息四舍五入取整   而 总增值税进位取整
			double totalValueAddedTax=Math.ceil(new BigDecimal(totalRenPrice).setScale(0, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(stampTaxRateAlone)).doubleValue()/HUNDRED);
			//平均每期的增值税 进位取整
			double valueAddedTax=0.0;
			valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(schema.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
			//保留总增值税  保存到方案里
			paylist.put("TOTAL_VALUEADDED_TAX", totalValueAddedTax);
			double tempTotalValueAdded=0.0;
			//用于保存实际每期的增值税
			double tempTotalValueAddedTrue=0.0;
			int j=1;
			for (Map payline : paylines) {
				//实际每期增值税
				valueAddedTaxTrue=new BigDecimal(DataUtil.doubleUtil(payline.get("REN_PRICE"))).multiply(new BigDecimal(stampTaxRateAlone)).doubleValue()/HUNDRED;
				
				if (j==DataUtil.intUtil(schema.get("LEASE_PERIOD"))){
					valueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
					//实际每期增值税
					valueAddedTaxTrue=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAddedTrue)).doubleValue();
				}
				payline.put("VALUE_ADDED_TAX", valueAddedTax);
				payline.put("VALUE_ADDED_TAX_TRUE", valueAddedTaxTrue);
				
				tempTotalValueAdded+= valueAddedTax;
				tempTotalValueAddedTrue+=valueAddedTaxTrue;
				j++;
			}
			
			calcalatePaylineIRRByValueAdded(paylist);
		
		}else if("1".equals(schema.get("TAX_PLAN_CODE"))||"3".equals(schema.get("TAX_PLAN_CODE"))){
			calcalatePaylineIRR(paylist);
		}
		// calculatePaylineLossPrice(paylist);
		
		return paylist;
	}
	/**
	 * 
	 * @param pay
	 * @param schema
	 * @param equipList
	 * @param insureList
	 * @param otherFeeList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	//拼装支付表   带populate的 填充支付表
	public static Map populatePaylist(Pay pay, Map schema,
			Collection<Map> equipList, Collection<Map> insureList,
			List<Map> otherFeeList) {

		Map paylist = new HashMap();
		paylist.put("payEquipments", equipList);
		
		double totalPrice = DataUtil.doubleUtil(schema.get("TOTAL_PRICE"));
		double leaseTopic = DataUtil.doubleUtil(schema.get("LEASE_TOPRIC"));
		// double priceRate = totalPrice / leaseTopic;
		
		//
		paylist.put("CONTRACT_PRICE", leaseTopic);
		paylist.put("LEASE_TOPRIC", totalPrice);
		//paylist.put("LEASE_RZE", pay.getRestPrice());
		paylist.put("LEASE_RZE", schema.get("LEASE_RZE"));
		//
		paylist.put("LEASE_TERM", schema.get("LEASE_TERM"));
		paylist.put("LEASE_PERIOD", schema.get("LEASE_PERIOD"));
		// paylist.put("PLEDGE_PRICE", DataUtil.doubleUtil(schema.get("PLEDGE_PRICE")) * priceRate);
		paylist.put("PLEDGE_PRICE", pay.getPledgePrice());
		paylist.put("PLEDGE_PRICE_RATE", schema.get("PLEDGE_PRICE_RATE"));
		paylist.put("HEAD_HIRE", schema.get("HEAD_HIRE"));
		paylist.put("HEAD_HIRE_PERCENT", schema.get("HEAD_HIRE_PERCENT"));
		paylist.put("MANAGEMENT_FEE", schema.get("MANAGEMENT_FEE"));
		paylist.put("MANAGEMENT_FEE_RATE", schema.get("MANAGEMENT_FEE_RATE"));
		// paylist.put("FLOAT_RATE", schema.get("FLOAT_RATE"));
		paylist.put("FLOAT_RATE", pay.getUpRate());
		// paylist.put("YEAR_INTEREST", schema.get("YEAR_INTEREST"));
		paylist.put("YEAR_INTEREST", pay.getYearRate());
		paylist.put("YEAR_INTEREST_TYPE", schema.get("YEAR_INTEREST_TYPE"));
		paylist.put("FINE_RATE", schema.get("FINE_RATE"));
		paylist.put("FINE_TYPE", schema.get("FINE_TYPE"));
		
		paylist.put("PAY_WAY", schema.get("PAY_WAY"));
		paylist.put("START_DATE", schema.get("START_DATE"));
		//支付表最后一期时间 
		if (schema.get("START_DATE") != null ) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date)schema.get("START_DATE"));
			calendar.add(Calendar.MONTH, (DataUtil.intUtil(schema.get("LEASE_PERIOD")) - 1)*DataUtil.intUtil(schema.get("LEASE_TERM")));
			paylist.put("END_DATE", calendar.getTime());
		} else {
			paylist.put("END_DATE", "");
		}
		//
		paylist.put("INSURANCE_COMPANY_ID", schema.get("INSURANCE_COMPANY_ID"));
		paylist.put("BUY_INSURANCE_WAY", schema.get("BUY_INSURANCE_WAY"));
		paylist.put("BUY_INSURANCE_TIME", schema.get("BUY_INSURANCE_TIME"));
		paylist.put("INSURE_REBATE_RATE", schema.get("INSURE_REBATE_RATE"));
		//
		paylist.put("DEAL_WAY", schema.get("DEAL_WAY"));
		paylist.put("EQUPMENT_ADDRESS", schema.get("EQUPMENT_ADDRESS"));
		paylist.put("BUSINESS_TRIP_PRICE", schema.get("BUSINESS_TRIP_PRICE"));
		//
		//2010-08
		paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
		// use the configuration
		//Add by Michael 2012 09-21 增加税费测算方案
		paylist.put("TAX_PLAN_CODE", schema.get("TAX_PLAN_CODE"));
		//Add by Michael  重车保险费率不计算
		PaylistUtil.setBaseRate(paylist,String.valueOf(schema.get("CONTRACT_TYPE")));
		//PaylistUtil.setBaseRate(paylist);
		 
		//add by Michael 把管理费收入总和传过来，计算营业税收入，会影响TR计算
		paylist.put("FEESET_TOTAL", schema.get("FEESET_TOTAL"));
		
		paylist.put("PLEDGE_AVE_PRICE", schema.get("PLEDGE_AVE_PRICE"));
		paylist.put("PLEDGE_BACK_PRICE", schema.get("PLEDGE_BACK_PRICE"));
		paylist.put("PLEDGE_LAST_PRICE", schema.get("PLEDGE_LAST_PRICE"));
		paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
		paylist.put("PLEDGE_ENTER_WAY", schema.get("PLEDGE_ENTER_WAY"));
		paylist.put("PLEDGE_ENTER_CMPRICE", schema.get("PLEDGE_ENTER_CMPRICE"));
		paylist.put("PLEDGE_ENTER_CMRATE", schema.get("PLEDGE_ENTER_CMRATE"));
		paylist.put("PLEDGE_ENTER_AG", schema.get("PLEDGE_ENTER_AG"));
//		paylist.put("LOAN_RATE", schema.get("LOAN_RATE"));
//		paylist.put("MANAGE_RATE", schema.get("MANAGE_RATE"));
		//
		paylist.put("PLEDGE_REALPRIC", schema.get("PLEDGE_REALPRIC"));
		
		paylist.put("DEFER_PERIOD", schema.get("DEFER_PERIOD"));
		//
		List<Map> payInusres = new ArrayList<Map>();
		for (Map map : insureList) {
			Map payInsure = new HashMap();
			payInsure.put("RECI_ID", map.get("RECI_ID"));
			payInsure.put("INSURE_ITEM", map.get("INSURE_ITEM"));
			payInsure.put("START_DATE", map.get("START_DATE"));
			payInsure.put("END_DATE", map.get("END_DATE"));
			payInsure.put("INSURE_RATE", map.get("INSURE_RATE"));
			payInsure.put("MEMO", map.get("MEMO"));
			payInsure.put("INSURE_PRICE", map.get("INSURE_PRICE"));
			payInusres.add(payInsure);
		}
		paylist.put("payInusres", payInusres);
		
		//
		List<Map> payOtherFees = new ArrayList<Map>();
		for (Map map : otherFeeList) {
			Map payOtherFee = new HashMap();
			payOtherFee.put("RECO_ID", map.get("RECO_ID"));
			payOtherFee.put("OTHER_NAME", map.get("OTHER_NAME"));
			payOtherFee.put("OTHER_DATE", map.get("OTHER_DATE"));
			payOtherFee.put("MEMO", map.get("MEMO"));
			payOtherFee.put("OTHER_PRICE", map.get("OTHER_PRICE"));
			payOtherFees.add(payOtherFee);
		}
		paylist.put("payOtherFees", payOtherFees);
		
		//还款计划
		List<Map> paylines = new ArrayList<Map>();
		
		for (PayItem payItem : pay.getPayItems()) {
			Map item = new HashMap();
			item.put("PERIOD_NUM", payItem.getIndex());
			item.put("PAY_DATE", payItem.getPayDate());
			item.put("MONTH_PRICE", payItem.getMonthPrice());
			item.put("OWN_PRICE", payItem.getOwnPrice());
			item.put("REN_PRICE", payItem.getRenPrice());
			item.put("LAST_PRICE", payItem.getLastPrice());
			
			if (payItem.isLocked()) {
				item.put("LOCKED", Integer.valueOf(1));
			} else {
				item.put("LOCKED", Integer.valueOf(0));
			}
			
			paylines.add(item);

		}
		
		paylist.put("paylines", paylines);
		//可以根据不同的税费方案来进行计算  1是普通的方案  2是增值税方案
		if (Constants.TAX_PLAN_CODE_2.equals(schema.get("TAX_PLAN_CODE"))){
			//---Add by Michael 2012 09-24 可以在此次 进行  方案类别的判断来进行相应的税率计算
			//Add by Michael 2012-09-12  增加增值税的计算
			double stampTaxRateAlone = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE_ALONE"));  //去除复税税率
			double manageFeeRate=DataUtil.doubleUtil(paylist.get("MANAGE_FEE_RATE"));  //去除管理费费
			//总利息 * 17% =总增值税  ；总增值税*总期数   得出每一期的的增值税
			double totalRenPrice=0.0;   //总利息
			
			for (Map payline : paylines) {
				totalRenPrice += DataUtil.doubleUtil(payline.get("REN_PRICE"));
			}
			
			//实际每期的增值税
			double valueAddedTaxTrue=0.0;
			
			//  得出总的利息四舍五入取整   而 总增值税进位取整
			double totalValueAddedTax=Math.ceil(new BigDecimal(totalRenPrice).setScale(0, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(stampTaxRateAlone)).doubleValue()/HUNDRED);
			
			//保留总增值税  保存到方案里
			paylist.put("TOTAL_VALUEADDED_TAX", totalValueAddedTax);
			//平均每期的增值税 进位取整
			double valueAddedTax=0.0;
			valueAddedTax=Math.ceil(new BigDecimal(totalValueAddedTax).divide(new BigDecimal(String.valueOf(schema.get("LEASE_PERIOD"))),2,BigDecimal.ROUND_HALF_UP).doubleValue());
			double tempTotalValueAdded=0.0;
			//用于保存实际每期的增值税
			double tempTotalValueAddedTrue=0.0;
			double pledge_last_valueAddTax=0.0;
			int valueAdd_last_period=DataUtil.intUtil(paylist.get("LEASE_PERIOD"))-DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"))+1;
			int j=1;
			for (Map payline : paylines) {
				//实际每期增值税
				valueAddedTaxTrue=new BigDecimal(DataUtil.doubleUtil(payline.get("REN_PRICE"))).multiply(new BigDecimal(stampTaxRateAlone)).doubleValue()/HUNDRED;
				if (j==DataUtil.intUtil(schema.get("LEASE_PERIOD"))){
					valueAddedTax=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAdded)).doubleValue();
					//实际每期增值税
					valueAddedTaxTrue=new BigDecimal(totalValueAddedTax).subtract(new BigDecimal(tempTotalValueAddedTrue)).doubleValue();
				}
				payline.put("VALUE_ADDED_TAX", valueAddedTax);
				payline.put("VALUE_ADDED_TAX_TRUE", valueAddedTaxTrue);
				
				//增加 最后抵充期数的增值税
				if(j>=valueAdd_last_period){
					pledge_last_valueAddTax+=valueAddedTax;
				}
				
				tempTotalValueAdded+= valueAddedTax;
				tempTotalValueAddedTrue+=valueAddedTaxTrue;
				j++;
			}
			paylist.put("PLEDGE_LAST_PRICE_TAX", pledge_last_valueAddTax);
			calcalatePaylineIRRByValueAdded(paylist);
		
		}else if(Constants.TAX_PLAN_CODE_1.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_3.equals(schema.get("TAX_PLAN_CODE"))||Constants.TAX_PLAN_CODE_5.equals(schema.get("TAX_PLAN_CODE"))){
			calcalatePaylineIRR(paylist);
		} else if(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))) {

			int payWay=DataUtil.intUtil(paylist.get("PAY_WAY"));//支付方式
			
			Map<String,Object> param=new HashMap<String,Object>();
			List<Map<String,Object>> rateList=null;
			
			double SALES_TAX_RATE_ALONE=0;
			double FBUILDTAX=0;
			double INSURE_BASE_RATE=0;
			double STAMP_TAX_TOPRIC=0;
			double STAMP_TAX_MONTHPRIC=0;
			double STAMP_TAX_INSUREPRIC=0;
			double LOAN_RATE=0;
			double MANAGE_RATE=0;
			try {
				param.put("TAX_PLAN_CODE",schema.get("TAX_PLAN_CODE"));
				rateList=(List<Map<String,Object>>)DataAccessor.query("moneyRate.queryAllNew",param,DataAccessor.RS_TYPE.LIST);
				
				for(int i=0;i<rateList.size();i++) {
					if("INSURE_BASE_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//保险费率
						INSURE_BASE_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("FBUILDTAX".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//城建税比率
						FBUILDTAX=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("SALES_TAX_RATE_ALONE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//增值税比率
						SALES_TAX_RATE_ALONE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_TOPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//购销合同印花税比率
						STAMP_TAX_TOPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_MONTHPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//租赁合同印花税比率
						STAMP_TAX_MONTHPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_INSUREPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//保险合同印花税比率
						STAMP_TAX_INSUREPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("LOAN_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//资金成本,贷款利率
						LOAN_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("MANAGE_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//管理费成本
						MANAGE_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					}
				}
			} catch (Exception e) {
				
			}
			
			//直接租赁组装支付表
			double leaseTopric=Double.valueOf(schema.get("LEASE_TOPRIC").toString());//设备总价款
			double leaseRze=Double.valueOf(schema.get("LEASE_RZE").toString());//获得概算成本
			double costWithOutTax=Double.valueOf(schema.get("LEASE_TOPRIC").toString())/(1+SALES_TAX_RATE_ALONE);//未税成本
			double incomeTaxValue=Math.round(costWithOutTax*SALES_TAX_RATE_ALONE);//进项税额
			double yearInterest=Double.valueOf(schema.get("YEAR_INTEREST").toString())/100;//合同年利率
			double avePledge=Double.valueOf(schema.get("PLEDGE_AVE_PRICE").toString());//获得平均抵充金额
			double backPledge=Double.valueOf(schema.get("PLEDGE_BACK_PRICE").toString());//用于期末返回金额
			double [] irrMonthPrice=new double[((List)schema.get("payList")).size()];
			double [] trCashFlow=new double[((List)schema.get("payList")).size()+1];
			double [] ctrCashFlow=new double[((List)schema.get("payList")).size()+1];
			double pvTotal=0;//利差总和
			
			int lastPledgePeriod=Integer.valueOf(schema.get("PLEDGE_LAST_PERIOD").toString());
			double magrFee=0;
			if(schema.get("FEESET_TOTAL")!=null&&!"".equals(schema.get("FEESET_TOTAL"))) {
				magrFee=Double.valueOf(schema.get("FEESET_TOTAL").toString());
				magrFee=magrFee/1.06;
			} else {
				magrFee=Double.valueOf(schema.get("MAGR_FEE")!=null?schema.get("MAGR_FEE").toString():"0");//获得管理费
				magrFee=magrFee/1.06;
			}
			
			//保险费 只有设备类型的才有保险费,其余类型保险费为0
			double insurance=0;
			if(schema.get("CONTRACT_TYPE")==null||Constants.CONTRACT_TYPE_2.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_5.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_7.equals(schema.get("CONTRACT_TYPE").toString())
					||"1".equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_9.equals(schema.get("CONTRACT_TYPE").toString())) {//设备类型的, 1是用于报价单
				insurance=costWithOutTax*INSURE_BASE_RATE*(Integer.valueOf(schema.get("LEASE_PERIOD").toString())/12);
			}
			
			paylist.put("total_insure_price",insurance);
			paylist.put("first_insure_price",insurance);
			
			double irrMonthPriceTotal=0;
			for(int i=0;i<((List)schema.get("payList")).size();i++) {
				irrMonthPrice[i]=Double.valueOf(((Map)(((List)schema.get("payList"))).get(i)).get("IRR_MONTH_PRICE").toString());
				irrMonthPriceTotal=irrMonthPriceTotal+irrMonthPrice[i];
			}
			
			//印花税
			double stampValue=leaseTopric*STAMP_TAX_TOPRIC+(irrMonthPriceTotal+avePledge)*STAMP_TAX_MONTHPRIC+insurance*STAMP_TAX_INSUREPRIC;
				
			paylist.put("STAMP_TAX_PRICE",stampValue);
			if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())!=0) {//延时拨款类型组装租金测算表
				for(int i=0;i<paylines.size();i++) {
					paylines.get(i).put("MONTH_PRICE",i==0?irrMonthPrice[i]+avePledge:irrMonthPrice[i]);
					if(i==0) {
						paylines.get(i).put("REN_PRICE",leaseTopric*yearInterest/12);//合同当期利息
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]+avePledge-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金
						paylines.get(i).put("LAST_PRICE",leaseTopric-(irrMonthPrice[i]+avePledge-leaseTopric*yearInterest/12));//当期余额
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]+avePledge);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("CONTRACT_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						paylines.get(i).put("INCOME_TAX_VALUE",incomeTaxValue-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",0);//实际支付增值税
						paylines.get(i).put("BUILD_TAX",0);//城建税
					} else {
						paylines.get(i).put("REN_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())*yearInterest/12);//合同当期利息=上期余额*合同利率/12
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金=当期租金-当期利息
						paylines.get(i).put("LAST_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())-Double.valueOf(paylines.get(i).get("OWN_PRICE").toString()));//当期余额=上期余额-当期本金
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("CONTRACT_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						paylines.get(i).put("INCOME_TAX_VALUE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())<0
								?0:Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())>Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())
								?0:Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())-Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString()));//实际支付增值税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("PAY_TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					}
				}
				//准备实际tr现金流
				if(payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL||//期初类型
						payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE||
						payWay==CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=irrMonthPrice[j]+avePledge+backPledge+magrFee-insurance-stampValue;//金流0 保证金先进入,管理费,期末返回金额
							ctrCashFlow[j]=irrMonthPrice[j]+avePledge+backPledge+magrFee;
						} else if(j==1) {
							trCashFlow[j]=irrMonthPrice[j];
							ctrCashFlow[j]=irrMonthPrice[j];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款1期
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=-backPledge;
							ctrCashFlow[j]=-backPledge;
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
							
							if(lastPledgePeriod==1) {
								
							}
						} else {
							trCashFlow[j]=irrMonthPrice[j]
									-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j).get("BUILD_TAX").toString());
							ctrCashFlow[j]=irrMonthPrice[j];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if(j==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
					
				} else {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=avePledge+backPledge+magrFee-insurance-stampValue;//金流0 保证金先进入,管理费,期末返回金额
							ctrCashFlow[j]=avePledge+backPledge+magrFee;
						} else if(j==1) {
							trCashFlow[j]=irrMonthPrice[0];
							ctrCashFlow[j]=irrMonthPrice[0];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款1期
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else if(j==trCashFlow.length-1) {//最后期需要减去期末返还金额
							trCashFlow[j]=irrMonthPrice[j-1]
									-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j-1).get("BUILD_TAX").toString())-backPledge;
							ctrCashFlow[j]=irrMonthPrice[j-1]-backPledge;
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else {
							trCashFlow[j]=irrMonthPrice[j-1]
									-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j-1).get("BUILD_TAX").toString());
							ctrCashFlow[j]=irrMonthPrice[j-1];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if((j-1)==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				}
				
			} else {//非延时拨款类型组装租金测算表
				for(int i=0;i<paylines.size();i++) {
					paylines.get(i).put("MONTH_PRICE",i==0?irrMonthPrice[i]+avePledge:irrMonthPrice[i]);
					if(i==0) {
						paylines.get(i).put("REN_PRICE",leaseTopric*yearInterest/12);//合同当期利息
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]+avePledge-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金
						paylines.get(i).put("LAST_PRICE",leaseTopric-(irrMonthPrice[i]+avePledge-leaseTopric*yearInterest/12));//当期余额
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]+avePledge);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("CONTRACT_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						paylines.get(i).put("INCOME_TAX_VALUE",incomeTaxValue-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",0);//实际支付增值税
						paylines.get(i).put("BUILD_TAX",0);//城建税
					} else {
						paylines.get(i).put("REN_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())*yearInterest/12);//合同当期利息=上期余额*合同利率/12
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金=当期租金-当期利息
						paylines.get(i).put("LAST_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())-Double.valueOf(paylines.get(i).get("OWN_PRICE").toString()));//当期余额=上期余额-当期本金
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("CONTRACT_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						paylines.get(i).put("INCOME_TAX_VALUE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())<0
								?0:Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())>Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())
								?0:Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())-Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString()));//实际支付增值税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("PAY_TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					}
				}
				
				//准备实际tr现金流
				if(payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL||//期初类型
						payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE||
						payWay==CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=-leaseTopric+avePledge+backPledge+irrMonthPrice[j]+magrFee-insurance-stampValue;//金流0  流出的金额=设备总价款+用于平均抵充金额+期末返回金额+首期租金
							ctrCashFlow[j]=-leaseTopric+avePledge+backPledge+irrMonthPrice[j]+magrFee;
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=-backPledge;
							ctrCashFlow[j]=-backPledge;
						} else {
							trCashFlow[j]=irrMonthPrice[j]
									-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j).get("BUILD_TAX").toString());
							ctrCashFlow[j]=Double.valueOf(paylines.get(j).get("MONTH_PRICE").toString());
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if(j==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				} else {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=-leaseTopric+avePledge+backPledge+magrFee-insurance-stampValue;//金流0  流出的金额=设备总价款+用于平均抵充金额+期末返回金额
							ctrCashFlow[j]=-leaseTopric+avePledge+backPledge+magrFee;
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=irrMonthPrice[j-1]
									-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j-1).get("BUILD_TAX").toString())-backPledge;
							ctrCashFlow[j]=Double.valueOf(paylines.get(j-1).get("MONTH_PRICE").toString())-backPledge;
						} else {
							trCashFlow[j]=irrMonthPrice[j-1]
									-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())
									-Double.valueOf(paylines.get(j-1).get("BUILD_TAX").toString());
							ctrCashFlow[j]=irrMonthPrice[j-1];
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if((j-1)==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				}
			}
			
			double tr=0;
			double ctr=0;	
			if(paylist.get("TR_IRR_RATE")==null||"".equals(paylist.get("TR_IRR_RATE"))) {
				tr=Math.round(IRRUtils.getIRR(trCashFlow,Double.NaN)*12.0d*Math.pow(10,10))/Math.pow(10,8);//实际TR
				paylist.put("TR_IRR_RATE",tr);
			}
			if(paylist.get("TR_RATE")==null||"".equals(paylist.get("TR_RATE"))) {
				ctr=Math.round(IRRUtils.getIRR(ctrCashFlow,Double.NaN)*12.0d*Math.pow(10,10))/Math.pow(10,8);//客户TR
				paylist.put("TR_RATE",ctr);
			}
			
			//利差数据准备
			List<Map<String,Object>> pvList=new ArrayList<Map<String,Object>>();
			//分2种类型,计算延迟拨款类型
			if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())!=0) {//延时拨款类型组装租金测算表
				//临时只做延迟拨款1期的
				for(int i=0;i<irrMonthPrice.length;i++) {
					Map<String,Object> pvMap=new HashMap<String,Object>();
					pvMap.put("PERIOD_NUM",i+1);
					if(i==0) {//第一期
						pvMap.put("INTEREST",leaseRze*ctr/100/12);//当期利息
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",leaseRze-Double.valueOf(pvMap.get("OWN").toString()));//本金余额
						pvMap.put("OWN_INTEREST",0);//资金成本息
					} else {
						pvMap.put("INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*ctr/100/12);//当期利息=上期本金余额*客户TR
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",Double.valueOf(pvList.get(i-1).get("REST").toString())-Double.valueOf(pvMap.get("OWN").toString()));//本金余额=上期本金余额-当期本金
						pvMap.put("OWN_INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					}
					pvMap.put("PV_PRICE",PVUtils.pv2(LOAN_RATE+MANAGE_RATE,i+1,-(Double.valueOf(pvMap.get("OWN_INTEREST").toString())-Double.valueOf(pvMap.get("INTEREST").toString()))));
					pvList.add(pvMap);
					pvTotal=pvTotal+Double.valueOf(pvMap.get("PV_PRICE").toString());
					paylines.get(i).put("PV_PRICE",pvMap.get("PV_PRICE"));
				}
			} else {//计算非延迟拨款类型
				for(int i=0;i<irrMonthPrice.length;i++) {
					Map<String,Object> pvMap=new HashMap<String,Object>();
					pvMap.put("PERIOD_NUM",i+1);
					if(i==0) {//第一期
						pvMap.put("INTEREST",leaseRze*ctr/100/12);//当期利息
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",leaseRze-Double.valueOf(pvMap.get("OWN").toString()));//本金余额
						pvMap.put("OWN_INTEREST",leaseRze*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					} else {
						pvMap.put("INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*ctr/100/12);//当期利息=上期本金余额*客户TR
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",Double.valueOf(pvList.get(i-1).get("REST").toString())-Double.valueOf(pvMap.get("OWN").toString()));//本金余额=上期本金余额-当期本金
						pvMap.put("OWN_INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					}
					
					pvMap.put("PV_PRICE",PVUtils.pv2(LOAN_RATE+MANAGE_RATE,i+1,-(Double.valueOf(pvMap.get("OWN_INTEREST").toString())-Double.valueOf(pvMap.get("INTEREST").toString()))));
					//System.out.println(Double.valueOf(pvMap.get("PV_PRICE").toString()));
					//System.out.println("当期本金:"+pvMap.get("OWN")+"当期利息:"+pvMap.get("INTEREST")+"本金余额:"+pvMap.get("REST")+"资金成本息"+pvMap.get("OWN_INTEREST"));
					pvList.add(pvMap);
					pvTotal=pvTotal+Double.valueOf(pvMap.get("PV_PRICE").toString());
					paylines.get(i).put("PV_PRICE",pvMap.get("PV_PRICE"));
				}
			}
		} else if(Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))||
				Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))||
				Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {//售后回租

			int payWay=DataUtil.intUtil(paylist.get("PAY_WAY"));//支付方式
			
			Map<String,Object> param=new HashMap<String,Object>();
			List<Map<String,Object>> rateList=null;
			
			double SALES_TAX_RATE_ALONE=0;
			double FBUILDTAX=0;
			double INSURE_BASE_RATE=0;
			double STAMP_TAX_TOPRIC=0;
			double STAMP_TAX_MONTHPRIC=0;
			double STAMP_TAX_INSUREPRIC=0;
			double LOAN_RATE=0;
			double MANAGE_RATE=0;
			try {
				param.put("TAX_PLAN_CODE",schema.get("TAX_PLAN_CODE"));
				rateList=(List<Map<String,Object>>)DataAccessor.query("moneyRate.queryAllNew",param,DataAccessor.RS_TYPE.LIST);
				
				for(int i=0;i<rateList.size();i++) {
					if("INSURE_BASE_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//保险费率
						INSURE_BASE_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("FBUILDTAX".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//城建税比率
						FBUILDTAX=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("SALES_TAX_RATE_ALONE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//增值税比率
						SALES_TAX_RATE_ALONE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_TOPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//购销合同印花税比率
						STAMP_TAX_TOPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_MONTHPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//租赁合同印花税比率
						STAMP_TAX_MONTHPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("STAMP_TAX_INSUREPRIC".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//保险合同印花税比率
						STAMP_TAX_INSUREPRIC=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("LOAN_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//资金成本,贷款利率
						LOAN_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					} else if("MANAGE_RATE".equalsIgnoreCase(rateList.get(i).get("FILED_NAME").toString())) {//管理费成本
						MANAGE_RATE=Double.valueOf(rateList.get(i).get("RATE_VALUE").toString())/100;
					}
				}
			} catch (Exception e) {
				
			}
			
			//售后回租组装支付表
			double leaseTopric=Double.valueOf(schema.get("LEASE_TOPRIC").toString());//设备总价款
			double leaseRze=Double.valueOf(schema.get("LEASE_RZE").toString());//获得概算成本
			double costWithOutTax=Double.valueOf(schema.get("LEASE_TOPRIC").toString())/(1+SALES_TAX_RATE_ALONE);//未税成本
			//double incomeTaxValue=Math.round(costWithOutTax*SALES_TAX_RATE_ALONE);//进项税额
			double yearInterest=Double.valueOf(schema.get("YEAR_INTEREST").toString())/100;//合同年利率
			double avePledge=Double.valueOf(schema.get("PLEDGE_AVE_PRICE").toString());//获得平均抵充金额
			double backPledge=Double.valueOf(schema.get("PLEDGE_BACK_PRICE").toString());//用于期末返回金额
			double [] irrMonthPrice=new double[((List)schema.get("payList")).size()];
			double [] trCashFlow=new double[((List)schema.get("payList")).size()+1];
			double [] ctrCashFlow=new double[((List)schema.get("payList")).size()+1];
			double pvTotal=0;//利差总和
			
			int lastPledgePeriod=Integer.valueOf(schema.get("PLEDGE_LAST_PERIOD").toString());
			double magrFee=0;
			if(schema.get("FEESET_TOTAL")!=null&&!"".equals(schema.get("FEESET_TOTAL"))) {
				magrFee=Double.valueOf(schema.get("FEESET_TOTAL").toString());
				magrFee=magrFee/1.06;
			} else {
				magrFee=Double.valueOf(schema.get("MAGR_FEE")!=null?schema.get("MAGR_FEE").toString():"0");//获得管理费
				magrFee=magrFee/1.06;
			}
			
			//保险费 只有设备类型的才有保险费,其余类型保险费为0
			double insurance=0;
			if(schema.get("CONTRACT_TYPE")==null||Constants.CONTRACT_TYPE_2.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_5.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_7.equals(schema.get("CONTRACT_TYPE").toString())
					||"1".equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_9.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_10.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_12.equals(schema.get("CONTRACT_TYPE").toString())
					||Constants.CONTRACT_TYPE_13.equals(schema.get("CONTRACT_TYPE").toString())) {//设备类型的, 1是用于报价单
				insurance=costWithOutTax*INSURE_BASE_RATE*(Integer.valueOf(schema.get("LEASE_PERIOD").toString())/12);
			}
			if(Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE"))) {
				insurance=0;//用于商用车报价单
			}
			paylist.put("total_insure_price",insurance);
			paylist.put("first_insure_price",insurance);
			
			double irrMonthPriceTotal=0;
			for(int i=0;i<((List)schema.get("payList")).size();i++) {
				irrMonthPrice[i]=Double.valueOf(((Map)(((List)schema.get("payList"))).get(i)).get("IRR_MONTH_PRICE").toString());
				irrMonthPriceTotal=irrMonthPriceTotal+irrMonthPrice[i];
			}
			
			//印花税
			double stampValue=leaseTopric*STAMP_TAX_TOPRIC+(irrMonthPriceTotal+avePledge)*STAMP_TAX_MONTHPRIC+insurance*STAMP_TAX_INSUREPRIC;
				
			paylist.put("STAMP_TAX_PRICE",stampValue);
			if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())!=0) {//延时拨款类型组装租金测算表
				for(int i=0;i<paylines.size();i++) {
					paylines.get(i).put("MONTH_PRICE",i==0?irrMonthPrice[i]+avePledge:irrMonthPrice[i]);
					if(i==0) {
						paylines.get(i).put("REN_PRICE",leaseTopric*yearInterest/12);//合同当期利息
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]+avePledge-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金
						paylines.get(i).put("LAST_PRICE",leaseTopric-(irrMonthPrice[i]+avePledge-leaseTopric*yearInterest/12));//当期余额
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]+avePledge);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("REN_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						//paylines.get(i).put("INCOME_TAX_VALUE",incomeTaxValue-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())+Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//实际支付增值税=增值税+城建税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					} else {
						paylines.get(i).put("REN_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())*yearInterest/12);//合同当期利息=上期余额*合同利率/12
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金=当期租金-当期利息
						paylines.get(i).put("LAST_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())-Double.valueOf(paylines.get(i).get("OWN_PRICE").toString()));//当期余额=上期余额-当期本金
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("REN_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						/*paylines.get(i).put("INCOME_TAX_VALUE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())<0
								?0:Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));*///進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())+Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//实际支付增值税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					}
				}
				//准备实际tr现金流
				if(payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL||//期初类型
						payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE||
						payWay==CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=irrMonthPrice[j]+avePledge+backPledge+magrFee-insurance-stampValue-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString());//金流0 保证金先进入,管理费,期末返回金额
							ctrCashFlow[j]=irrMonthPrice[j]+avePledge+backPledge+magrFee;
							if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&schema.get("SALES_PAY")!=null) {
								trCashFlow[j]=trCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
								ctrCashFlow[j]=ctrCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
							}
						} else if(j==1) {
							trCashFlow[j]=irrMonthPrice[j]-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=irrMonthPrice[j];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款1期
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=-backPledge;
							ctrCashFlow[j]=-backPledge;
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
							
							if(lastPledgePeriod==1) {
								
							}
						} else {
							trCashFlow[j]=irrMonthPrice[j]-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=irrMonthPrice[j];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if(j==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
					
				} else {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=avePledge+backPledge+magrFee-insurance-stampValue;//金流0 保证金先进入,管理费,期末返回金额
							ctrCashFlow[j]=avePledge+backPledge+magrFee;
							if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&schema.get("SALES_PAY")!=null) {
								trCashFlow[j]=trCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
								ctrCashFlow[j]=ctrCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
							}
						} else if(j==1) {
							trCashFlow[j]=irrMonthPrice[0]-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=irrMonthPrice[0];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款1期
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else if(j==trCashFlow.length-1) {//最后期需要减去期末返还金额
							trCashFlow[j]=irrMonthPrice[j-1]-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())-backPledge;
							ctrCashFlow[j]=irrMonthPrice[j-1]-backPledge;
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						} else {
							trCashFlow[j]=irrMonthPrice[j-1]-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=irrMonthPrice[j-1];
							
							if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())==j) {//如果延迟拨款
								trCashFlow[j]=trCashFlow[j]-leaseTopric;
								ctrCashFlow[j]=ctrCashFlow[j]-leaseTopric;
							}
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if((j-1)==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				}
				
			} else {//非延时拨款类型组装租金测算表
				for(int i=0;i<paylines.size();i++) {
					paylines.get(i).put("MONTH_PRICE",i==0?irrMonthPrice[i]+avePledge:irrMonthPrice[i]);
					if(i==0) {
						paylines.get(i).put("REN_PRICE",leaseTopric*yearInterest/12);//合同当期利息
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]+avePledge-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金
						paylines.get(i).put("LAST_PRICE",leaseTopric-(irrMonthPrice[i]+avePledge-leaseTopric*yearInterest/12));//当期余额
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]+avePledge);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("REN_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						//paylines.get(i).put("INCOME_TAX_VALUE",incomeTaxValue-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));//進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())+Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//实际支付增值税=增值税+城建税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					} else {
						paylines.get(i).put("REN_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())*yearInterest/12);//合同当期利息=上期余额*合同利率/12
						paylines.get(i).put("OWN_PRICE",irrMonthPrice[i]-Double.valueOf(paylines.get(i).get("REN_PRICE").toString()));//合同当期本金=当期租金-当期利息
						paylines.get(i).put("LAST_PRICE",Double.valueOf(paylines.get(i-1).get("LAST_PRICE").toString())-Double.valueOf(paylines.get(i).get("OWN_PRICE").toString()));//当期余额=上期余额-当期本金
						paylines.get(i).put("CONTRACT_PRICE",irrMonthPrice[i]);//合同租金
						paylines.get(i).put("TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("REN_PRICE").toString())/(1+SALES_TAX_RATE_ALONE)*SALES_TAX_RATE_ALONE);//增值税
						/*paylines.get(i).put("INCOME_TAX_VALUE",
								Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())<0
								?0:Double.valueOf(paylines.get(i-1).get("INCOME_TAX_VALUE").toString())-Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString()));*///進項稅餘額
						paylines.get(i).put("PAY_TAX_ADD_PRICE",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())+Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//实际支付增值税
						paylines.get(i).put("BUILD_TAX",Double.valueOf(paylines.get(i).get("TAX_ADD_PRICE").toString())*FBUILDTAX);//城建税
					}
				}
				
				//准备实际tr现金流
				if(payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL||//期初类型
						payWay==CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE||
						payWay==CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=-leaseTopric+avePledge+backPledge+irrMonthPrice[j]+magrFee-insurance-stampValue-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString());//金流0  流出的金额=设备总价款+用于平均抵充金额+期末返回金额+首期租金
							ctrCashFlow[j]=-leaseTopric+avePledge+backPledge+irrMonthPrice[j]+magrFee;
							if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&schema.get("SALES_PAY")!=null) {
								trCashFlow[j]=trCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
								ctrCashFlow[j]=ctrCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
							}
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=-backPledge;
							ctrCashFlow[j]=-backPledge;
						} else {
							trCashFlow[j]=irrMonthPrice[j]-Double.valueOf(paylines.get(j).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=Double.valueOf(paylines.get(j).get("MONTH_PRICE").toString());
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if(j==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				} else {
					for(int j=0;j<trCashFlow.length;j++) {
						if(j==0) {
							trCashFlow[j]=-leaseTopric+avePledge+backPledge+magrFee-insurance-stampValue;//金流0  流出的金额=设备总价款+用于平均抵充金额+期末返回金额
							ctrCashFlow[j]=-leaseTopric+avePledge+backPledge+magrFee;
							if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))&&schema.get("SALES_PAY")!=null) {
								trCashFlow[j]=trCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
								ctrCashFlow[j]=ctrCashFlow[j]-Double.valueOf(schema.get("SALES_PAY").toString());
							}
						} else if(j==trCashFlow.length-1) {
							trCashFlow[j]=irrMonthPrice[j-1]-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString())-backPledge;
							ctrCashFlow[j]=Double.valueOf(paylines.get(j-1).get("MONTH_PRICE").toString())-backPledge;
						} else {
							trCashFlow[j]=irrMonthPrice[j-1]-Double.valueOf(paylines.get(j-1).get("PAY_TAX_ADD_PRICE").toString());
							ctrCashFlow[j]=irrMonthPrice[j-1];
						}
					}
					
					//TR计算处理用于抵充期数
					if(lastPledgePeriod!=0) {
						for(int j=0;j<trCashFlow.length;j++) {
							if(j==0) {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									trCashFlow[j]=trCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
									ctrCashFlow[j]=ctrCashFlow[j]+irrMonthPrice[irrMonthPrice.length-jj];
								}
							} else {
								for(int jj=1;jj<=lastPledgePeriod;jj++) {
									if((j-1)==irrMonthPrice.length-jj) {
										trCashFlow[j]=trCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
										ctrCashFlow[j]=ctrCashFlow[j]-irrMonthPrice[irrMonthPrice.length-jj];
									}
								}
							}
						}
					}
				}
			}
			
			double tr=0;
			double ctr=0;	
			if(paylist.get("TR_IRR_RATE")==null||"".equals(paylist.get("TR_IRR_RATE"))) {
				tr=Math.round(IRRUtils.getIRR(trCashFlow,Double.NaN)*12.0d*Math.pow(10,10))/Math.pow(10,8);//实际TR
				paylist.put("TR_IRR_RATE",tr);
			}
			if(paylist.get("TR_RATE")==null||"".equals(paylist.get("TR_RATE"))) {
				ctr=Math.round(IRRUtils.getIRR(ctrCashFlow,Double.NaN)*12.0d*Math.pow(10,10))/Math.pow(10,8);//客户TR
				paylist.put("TR_RATE",ctr);
			}
			
			//利差数据准备
			List<Map<String,Object>> pvList=new ArrayList<Map<String,Object>>();
			//分2种类型,计算延迟拨款类型
			if(Integer.valueOf(schema.get("DEFER_PERIOD").toString())!=0) {//延时拨款类型组装租金测算表
				//临时只做延迟拨款1期的
				for(int i=0;i<irrMonthPrice.length;i++) {
					Map<String,Object> pvMap=new HashMap<String,Object>();
					pvMap.put("PERIOD_NUM",i+1);
					if(i==0) {//第一期
						pvMap.put("INTEREST",leaseRze*ctr/100/12);//当期利息
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",leaseRze-Double.valueOf(pvMap.get("OWN").toString()));//本金余额
						pvMap.put("OWN_INTEREST",0);//资金成本息
					} else {
						pvMap.put("INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*ctr/100/12);//当期利息=上期本金余额*客户TR
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",Double.valueOf(pvList.get(i-1).get("REST").toString())-Double.valueOf(pvMap.get("OWN").toString()));//本金余额=上期本金余额-当期本金
						pvMap.put("OWN_INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					}
					pvMap.put("PV_PRICE",PVUtils.pv2(LOAN_RATE+MANAGE_RATE,i+1,-(Double.valueOf(pvMap.get("OWN_INTEREST").toString())-Double.valueOf(pvMap.get("INTEREST").toString()))));
					pvList.add(pvMap);
					pvTotal=pvTotal+Double.valueOf(pvMap.get("PV_PRICE").toString());
					paylines.get(i).put("PV_PRICE",pvMap.get("PV_PRICE"));
				}
			} else {//计算非延迟拨款类型
				for(int i=0;i<irrMonthPrice.length;i++) {
					Map<String,Object> pvMap=new HashMap<String,Object>();
					pvMap.put("PERIOD_NUM",i+1);
					if(i==0) {//第一期
						pvMap.put("INTEREST",leaseRze*ctr/100/12);//当期利息
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",leaseRze-Double.valueOf(pvMap.get("OWN").toString()));//本金余额
						pvMap.put("OWN_INTEREST",leaseRze*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					} else {
						pvMap.put("INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*ctr/100/12);//当期利息=上期本金余额*客户TR
						pvMap.put("OWN",irrMonthPrice[i]-Double.valueOf(pvMap.get("INTEREST").toString()));//当期本金=当期租金-当期利息
						pvMap.put("REST",Double.valueOf(pvList.get(i-1).get("REST").toString())-Double.valueOf(pvMap.get("OWN").toString()));//本金余额=上期本金余额-当期本金
						pvMap.put("OWN_INTEREST",Double.valueOf(pvList.get(i-1).get("REST").toString())*(LOAN_RATE+MANAGE_RATE)/12);//资金成本息
					}
					
					pvMap.put("PV_PRICE",PVUtils.pv2(LOAN_RATE+MANAGE_RATE,i+1,-(Double.valueOf(pvMap.get("OWN_INTEREST").toString())-Double.valueOf(pvMap.get("INTEREST").toString()))));
					//System.out.println(Double.valueOf(pvMap.get("PV_PRICE").toString()));
					//System.out.println("当期本金:"+pvMap.get("OWN")+"当期利息:"+pvMap.get("INTEREST")+"本金余额:"+pvMap.get("REST")+"资金成本息"+pvMap.get("OWN_INTEREST"));
					pvList.add(pvMap);
					pvTotal=pvTotal+Double.valueOf(pvMap.get("PV_PRICE").toString());
					paylines.get(i).put("PV_PRICE",pvMap.get("PV_PRICE"));
				}
			}
		}
		
			//调尾差
			double ownPrice1=0;//每行四舍五入后的总和
			double ownPrice2=0;//每行未四舍五入后总和
			
			double renPrice1=0;//每行四舍五入后的总和
			double renPrice2=0;//每行未四舍五入后总和
			DecimalFormat df=new DecimalFormat("#.00");
			DecimalFormat df1=new DecimalFormat("#");
			for(int i=0;paylines!=null&&i<paylines.size();i++) {
				paylines.get(i).put("OWN_PRICE",df.format(Double.valueOf(paylines.get(i).get("OWN_PRICE")+"")));
				ownPrice1=ownPrice1+Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("OWN_PRICE")+"")));
				ownPrice2=ownPrice2+Double.valueOf(paylines.get(i).get("OWN_PRICE")+"");
				
				paylines.get(i).put("REN_PRICE",df.format(Double.valueOf(paylines.get(i).get("REN_PRICE")+"")));
				renPrice1=renPrice1+Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("REN_PRICE")+"")));
				renPrice2=renPrice2+Double.valueOf(paylines.get(i).get("REN_PRICE")+"");
				
				paylines.get(i).put("LAST_PRICE",Double.valueOf(df.format(Double.valueOf(paylines.get(i).get("LAST_PRICE")+""))));
			}
			double diff1=Double.valueOf(df.format(Double.valueOf(df1.format(ownPrice2))-Double.valueOf(df.format(ownPrice1))));
			
			double diff2=Double.valueOf(df.format(Double.valueOf(df1.format(renPrice2))-Double.valueOf(df.format(renPrice1))));
			if(paylines!=null&&paylines.size()!=0) {
				paylines.get(paylines.size()-1).put("OWN_PRICE",Double.valueOf(paylines.get(paylines.size()-1).get("OWN_PRICE")+"")+diff1);
				paylines.get(paylines.size()-1).put("REN_PRICE",Double.valueOf(paylines.get(paylines.size()-1).get("REN_PRICE")+"")+diff2);
			}
			
			if(Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))) {
				double monthPrice=0;
				double ownPrice=0;
				double renPrice=0;
				for(int i=0;paylines!=null&&i<paylines.size();i++) {
					monthPrice=Double.valueOf(paylines.get(i).get("MONTH_PRICE").toString());
					ownPrice=Double.valueOf(paylines.get(i).get("OWN_PRICE").toString());
					renPrice=Double.valueOf(paylines.get(i).get("REN_PRICE").toString());
					if(monthPrice-Double.valueOf(df.format(ownPrice+renPrice))!=0) {
						renPrice=renPrice+(monthPrice-Double.valueOf(df.format(ownPrice+renPrice)));
						paylines.get(i).put("REN_PRICE",df.format(renPrice));
					}
				}
			}
		return paylist;
	}
	/**
	 * 
	 * @param schema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Pay populatePayScale(Map schema) {
		Pay pay = DefaultPayableStrategy.createPayable();
		//
		pay.setYearRate(DataUtil.doubleUtil(schema.get("YEAR_INTEREST")));
		
		pay.setUpRate(DataUtil.doubleUtil(schema.get("FLOAT_RATE")));

		pay.setFirstRate(DataUtil.doubleUtil(schema.get("HEAD_HIRE_PERCENT")));
		pay.setHandRate(DataUtil.doubleUtil(schema.get("MANAGEMENT_FEE_RATE")));
		//设备款
		double totalPrice = DataUtil.doubleUtil(schema.get("TOTAL_PRICE"));
		
		pay.setTotalPrice(totalPrice); 
		//合同总价
		double leaseTopic = DataUtil.doubleUtil(schema.get("LEASE_TOPRIC"));
		pay.setPledgePrice((totalPrice / leaseTopic) * DataUtil.doubleUtil(schema.get("PLEDGE_PRICE")));

		int num = DataUtil.intUtil(schema.get("LEASE_PERIOD"));

		int leaseTerm = DataUtil.intUtil(schema.get("LEASE_TERM"));
		int unit = CollectionConstants.MONTH_IN_YEAR / leaseTerm;  
		pay.setUnit(unit);

		pay.setBaseRate(PayRate.getBaseRate(num * leaseTerm));  

		int payWay = DataUtil.intUtil(schema.get("PAY_WAY"));
		int payWayTemp = payWay;
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL) {
			payWay = CollectionConstants.PAY_WAY_END_EQUAL_CAPITAL;
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE) {
			payWay = CollectionConstants.PAY_WAY_END_EQUAL_RATE;
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
			payWay = CollectionConstants.PAY_WAY_END_UNEQUAL;
		}
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL) {
			pay.setType(Pay.TYPE_BEGIN_CORPUS);
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE || payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
			pay.setType(Pay.TYPE_BEGIN_ACCRUAL);
		} else if (payWay == CollectionConstants.PAY_WAY_END_EQUAL_CAPITAL) {
			pay.setType(Pay.TYPE_END_CORPUS);
		} else if (payWay == CollectionConstants.PAY_WAY_END_EQUAL_RATE || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL) {
			pay.setType(Pay.TYPE_END_ACCRUAL);
		}

		pay.setNum(num); 
		pay.setNumMonths(num * leaseTerm); 
		Date startDate = schema.get("FIRST_PAYDATE") == null ? null : (Date) schema.get("FIRST_PAYDATE");
		if (startDate != null) {
			//if(payWayTemp == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE || payWayTemp == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL ){
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				//c.add(Calendar.MONTH, -1);
				startDate = c.getTime();
				pay.setStartDate(startDate);
			//}else{
			//	pay.setStartDate(startDate);
			//}
		}

		DefaultPayableStrategy.calculate(pay,
				DefaultPayableStrategy.BALANCE_TYPE_BASE);
		return pay;
	}
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Pay populatePay(Map schema) {
		Pay pay = DefaultPayableStrategy.createPayable();

		//
		pay.setYearRate(DataUtil.doubleUtil(schema.get("YEAR_INTEREST")));
		
		pay.setUpRate(DataUtil.doubleUtil(schema.get("FLOAT_RATE")));
		// pay.setSaveRate(DataUtil.doubleUtil(schema.get(""))));
		pay.setFirstRate(DataUtil.doubleUtil(schema.get("HEAD_HIRE_PERCENT")));
		pay.setHandRate(DataUtil.doubleUtil(schema.get("MANAGEMENT_FEE_RATE")));

		double totalPrice = DataUtil.doubleUtil(schema.get("TOTAL_PRICE"));
		
		pay.setTotalPrice(totalPrice); 

		double leaseTopic = DataUtil.doubleUtil(schema.get("LEASE_TOPRIC"));
		pay.setPledgePrice(DataUtil.doubleUtil(schema.get("PLEDGE_PRICE")));

		int num = DataUtil.intUtil(schema.get("LEASE_PERIOD"));

		int leaseTerm = DataUtil.intUtil(schema.get("LEASE_TERM"));
		int unit = CollectionConstants.MONTH_IN_YEAR / leaseTerm;  
		pay.setUnit(unit);

		pay.setBaseRate(PayRate.getBaseRate(num * leaseTerm));  

		int payWay = DataUtil.intUtil(schema.get("PAY_WAY"));
		int payWayTemp = payWay;
		//把期初的替换成期末的
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL) {
			payWay = CollectionConstants.PAY_WAY_END_EQUAL_CAPITAL;
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE) {
			payWay = CollectionConstants.PAY_WAY_END_EQUAL_RATE;
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
			payWay = CollectionConstants.PAY_WAY_END_UNEQUAL;
		}
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL) {
			pay.setType(Pay.TYPE_BEGIN_CORPUS);
		} else if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE || payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
			pay.setType(Pay.TYPE_BEGIN_ACCRUAL);
		} else if (payWay == CollectionConstants.PAY_WAY_END_EQUAL_CAPITAL) {
			pay.setType(Pay.TYPE_END_CORPUS);
		} else if (payWay == CollectionConstants.PAY_WAY_END_EQUAL_RATE || payWay == CollectionConstants.PAY_WAY_END_UNEQUAL) {
			pay.setType(Pay.TYPE_END_ACCRUAL);
		}

		pay.setNum(num); 
		pay.setNumMonths(num * leaseTerm);  
		Date startDate = schema.get("START_DATE") == null ? null : (Date) schema.get("START_DATE");
		if (startDate != null) {
			//if(payWayTemp == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE || payWayTemp == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL ){
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				//c.add(Calendar.MONTH, -1);
				startDate = c.getTime();
				pay.setStartDate(startDate);
			//}else{
			//	pay.setStartDate(startDate);

			//}
		}

		DefaultPayableStrategy.calculate(pay,
				DefaultPayableStrategy.BALANCE_TYPE_BASE);
		return pay;
	}
	/**
	 * calculate the irr of paylines
	 * @param paylist
	 * 计算现金流
	 *
	 * Add by Michael 2012 09-24 计算增值税现金流
	 */
	@SuppressWarnings("unchecked")
	public static void calcalatePaylineIRRByValueAdded(Map paylist) {
		//计算现金流和应付租金
		double leaseTopric = DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"));
		//保证金
		double pledgePeriod = DataUtil.doubleUtil(paylist.get("PLEDGE_PERIOD"));
		double pledgeAvePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
		double pledgeLastPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"));
		double pledgeLastPriod = DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"));
		double pledgeBackPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE"));
		//首期租金
		double headhier = DataUtil.doubleUtil(paylist.get("HEAD_HIRE"));
		
		int leasePeriod = DataUtil.intUtil(paylist.get("LEASE_PERIOD"));
		
		int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		
		int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
		
		double salesTaxRate = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE")) / HUNDRED;
		
		/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//add by ShenQi  加入增值税内含 方案
			salesTaxRate=1/1.17*0.187;
		}*/
		
		double manageTaxRate = DataUtil.doubleUtil(paylist.get("MANAGE_FEE_RATE")) / HUNDRED;
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		//
		int periodNum = 1;
		
		double renPrice = 0d;
		
		double monthPrice = 0d;
		
		double sumMonthPrice = 0d;
		
		// sumMonthPrice
		for (Map payline : paylines) {
		
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			sumMonthPrice += monthPrice;
		
		}
		// insure 2010-09-07 change the calculation of insure price
		double insureBaseRate = DataUtil.doubleUtil(paylist.get("INSURE_BASE_RATE")) / HUNDRED;
		
		double tempTotalInsurePrice = new BigDecimal(leaseTopric * insureBaseRate * leasePeriod * leaseTerm / MONTHS_OF_YEAR).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		logger.info("保险费为：" + tempTotalInsurePrice);
		
		// stamp tax
		double stampTaxTopric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_TOPRIC"));
		
		double stampTaxMonthpric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_MONTHPRIC"));
		
		double stampTaxInsurepric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_INSUREPRIC"));
		// stampTaxTopric = 万分之三     -- 調整購銷合同印花稅為萬分之三。
		// stampTaxMonthpric = 千分之一   -- 租賃合同總額印花稅為千分之一。
		// stampTaxInsurepric = 千分之一  -- 保險合同印花稅為千分之一。
		double stampTax = leaseTopric * stampTaxTopric / HUNDRED 
					+ sumMonthPrice * stampTaxMonthpric / HUNDRED
					+ tempTotalInsurePrice * stampTaxInsurepric / HUNDRED;
		
		logger.info("印花稅为：" + stampTax);
		
		paylist.put("STAMP_TAX_PRICE", stampTax);
		paylist.put("INSURE_PRICE", tempTotalInsurePrice);
		//
		double salesTax = 0d;
		//每一期保证金所用的钱,现在只算了平均冲抵
		//double depositPrice = Math.round( pledgeAvePrice / leasePeriod * HUNDRED) / HUNDRED;
		
        BigDecimal bd1 = new BigDecimal(Double.toString(pledgeAvePrice)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(Double.valueOf(leasePeriod))); 
        double depositPrice=bd1.divide(bd2,2,BigDecimal.ROUND_HALF_UP).doubleValue();

		double irrMonthPrice = 0d;
		
		double irrPrice = 0d;
		
		double[] cashFlowsTr = new double[leasePeriod+1];
		
		double[] cashFlowsIrr;
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			cashFlowsIrr = new double[leasePeriod+1];
			
		} else { // 期末
			cashFlowsIrr = new double[leasePeriod+2];			
		}
		
		double leaseRZE = DataUtil.doubleUtil(paylist.get("LEASE_RZE"));
		
		double pv_own_price=0d;		
		
		cashFlowsTr[0] = -leaseRZE;
//		cashFlowsTr[0] = -leaseTopric;		
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric+headhier;//headhier首期租金
				
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}
			
		} else { // 期末

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric;
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}			
		}
		
		//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算
		double totalFeeSetSalesTax = 0d;
		totalFeeSetSalesTax=DataUtil.doubleUtil(paylist.get("FEESET_TOTAL"));
		//
		
		//Add by Michael 2012 09-24 将增加税去出来
		double valueAddedTax=0.0;
		
		int i = 1;
		
		for (Map payline : paylines) {
			//
			periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));			
			renPrice = DataUtil.doubleUtil(payline.get("REN_PRICE"));			
			//Marked by Michael 2012 01/30
			salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;			
			//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
//			if (periodNum == 1) {
//				salesTax=Math.round((renPrice+totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED;
//			} else {
//				salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;
//			}			
			//Add by Michael 2012 09-24 增加增值税  
			valueAddedTax=DataUtil.doubleUtil(payline.get("VALUE_ADDED_TAX"));
			
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				if (periodNum >= (leasePeriod - pledgeLastPriod) || periodNum == paylines.size()) {
					irrMonthPrice = 0d;
				} else {
					//如果期初 则应付租金＝上一期预期租金－平均至每期的保证金
					//irrMonthPrice = monthPrice - depositPrice;
					irrMonthPrice = DataUtil.doubleUtil(paylines.get(periodNum).get("MONTH_PRICE")) - depositPrice;
				}
				
			} else { // 期末
				
				if (periodNum > (leasePeriod - pledgeLastPriod)) {
					irrMonthPrice = 0d;
				} else {
					irrMonthPrice = monthPrice - depositPrice;
				}
			}		
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					//Modify by Michael 2012-09-24 增加每期的增值税 进来计算
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					irrPrice = irrMonthPrice - salesTax - stampTax - tempTotalInsurePrice-(Math.round(totalFeeSetSalesTax * manageTaxRate * HUNDRED) / HUNDRED)+valueAddedTax;
				} else {					
					payline.put("INSURE_PRICE", 0d);
					irrPrice = irrMonthPrice - salesTax+valueAddedTax;
				}
				
			} else { // 期末				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					//					
					irrPrice = irrMonthPrice  - stampTax - tempTotalInsurePrice+valueAddedTax;

				} else {
					double renPrices = DataUtil.doubleUtil(paylines.get(periodNum-2).get("REN_PRICE"));
					double salesTaxs = Math.round(renPrices * salesTaxRate * HUNDRED) / HUNDRED;
					
					payline.put("INSURE_PRICE", 0d);
					//Marked by Michael 2012 01/30  把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
					if(periodNum == 2){
						irrPrice = irrMonthPrice - salesTaxs- (Math.round((totalFeeSetSalesTax) * manageTaxRate * HUNDRED) / HUNDRED)+valueAddedTax;
					}else{
						irrPrice = irrMonthPrice - salesTaxs+valueAddedTax;
					}
				}
			}	

        	//保证金收入
			if (pledgePeriod != 0 && pledgePeriod == periodNum) {
				irrPrice = irrPrice + pledgeAvePrice + pledgeLastPrice+pledgeBackPrice;
			}
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				//保证金退回
				if (periodNum == leasePeriod) {
					irrPrice -= pledgeBackPrice;
					irrMonthPrice -= pledgeBackPrice;
				}
				
			} else { // 期末
				if (periodNum == leasePeriod) {
					irrPrice -= 0;
					irrMonthPrice -= 0;
				}
				
			}			
			
			
        	irrPrice = Math.round(irrPrice * HUNDRED) / HUNDRED;
        	
        	irrMonthPrice = Math.round(irrMonthPrice * Math.pow(10, 3))/Math.pow(10, 3);
        	
        	payline.put("DEPOSIT_PRICE", depositPrice);
        	payline.put("IRR_PRICE", irrPrice);
        	
        	payline.put("IRR_MONTH_PRICE", irrMonthPrice);
        	
        	payline.put("SALES_TAX", salesTax);
        	

        	
        	
			if (periodNum == 1) {
				
				pv_own_price = leaseRZE - irrMonthPrice +renPrice;
			} else {
				pv_own_price =(new BigDecimal(pv_own_price).subtract(new BigDecimal(irrMonthPrice))).add(new BigDecimal(renPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			
			if (periodNum == leasePeriod) {
				
				pv_own_price = 0;
			}
			//LAST_PRICE 剩余本金
        	//payline.put("REAL_OWN_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")) - depositPrice);
			payline.put("REAL_OWN_PRICE", pv_own_price);
//计算TR	
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = 0-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				} 				
			} else { // 期末	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = irrMonthPrice-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				}  				
			}
        	cashFlowsIrr[i] = irrPrice;
        	i++;
		}
		
		double trRate = Math.round(IRRUtils.getIRR(cashFlowsTr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);

		//根据应付租金算 客户TR
		paylist.put("TR_RATE", trRate);
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			
			
		} else { // 期末
			cashFlowsIrr[leasePeriod+1] = -(Double.parseDouble(paylines.get(leasePeriod-1).get("SALES_TAX").toString())+pledgeBackPrice);
		}
		
		//double trIrrRate = Math.round(IRRUtils.getIRR(cashFlowsIrr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		double trIrrRate = Math.round(IRRUtils.getTrIRR(cashFlowsIrr, Double.NaN,DataUtil.intUtil(paylist.get("DEFER_PERIOD")),leaseTopric) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		//根据净现金流 实际TR
		paylist.put("TR_IRR_RATE", trIrrRate);
		
		//Add by Michael 2012 01/15--------------------------------------------	
		double loanRate = DataUtil.doubleUtil(paylist.get("LOAN_RATE")) / HUNDRED;      //资金成本
		double manageRate = DataUtil.doubleUtil(paylist.get("MANAGE_RATE")) / HUNDRED;  //管理成本
		double rate = loanRate + manageRate ;     //合计成本
		double netFinance = 0d;//净本金
		double netCurrentFinance = 0d;//当期净本金
		double currentRenPrice = 0d;  //当期利息
		double tempIrrPrice=0d;  //净现金流	
		double currentFinanceCostRen=0d;  //当期资金成本息
		BigDecimal bigNetCurrentFinance = null;
		BigDecimal bigCurrentRenPrice = null;   
		BigDecimal bigNetFinance = null;   
		BigDecimal bigCurrentFinanceCostRen = null; 
		if(DataUtil.intUtil(paylist.get("DEFER_PERIOD"))>0){
			netFinance=DataUtil.doubleUtil(paylist.get("LEASE_RZE"))-DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC")); //净本金=概算成本（前期净本金余额）
		}else{
			netFinance=DataUtil.doubleUtil(paylist.get("LEASE_RZE")); //净本金=概算成本（前期净本金余额）
		}
		
		int k=1; //用来取 计算IRR 的现金流下标 
		for (Map payline : paylines) {
			//tempIrrPrice = DataUtil.doubleUtil(payline.get("IRR_PRICE"));
			tempIrrPrice = cashFlowsIrr[k];
			//根据实际TR计算当期利息
			currentRenPrice=netFinance*((trIrrRate/ HUNDRED)/12); //当期利息等于前期净本金*（trIrrRate/12）
			netCurrentFinance=tempIrrPrice-currentRenPrice; //当期净本金=当期净现金-当期利息
			currentFinanceCostRen = (netFinance*rate)/12; 	//当期资金成本息  當期淨本金餘額 * (合計成本/12)  合計成本 = 資金成本 + 管理成本					//资金成本息			
			netFinance=netFinance-netCurrentFinance;   //当期净本金余额=前期净本金余额-当期净本金
			
			bigNetCurrentFinance = new BigDecimal(String.valueOf(netCurrentFinance));
			bigCurrentRenPrice = new BigDecimal(String.valueOf(currentRenPrice));
			bigNetFinance = new BigDecimal(String.valueOf(netFinance));
			bigCurrentFinanceCostRen = new BigDecimal(String.valueOf(currentFinanceCostRen));
			
			payline.put("NETCURRENTFINANCE", bigNetCurrentFinance);
			payline.put("CURRENTRENPRICE", bigCurrentRenPrice);
			payline.put("NETFINANCE", bigNetFinance);  //前期净本金余额	
			payline.put("CURRENTFINANCECOSTREN", bigCurrentFinanceCostRen);  //当期资金成本息
			
			k++;
		}

		//------------------------------------------------------------------------------------		
        
	}
	
	@SuppressWarnings("unchecked")
	public static void calcalatePaylineIRR(Map paylist) {
		//计算现金流和应付租金
		double leaseTopric = DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"));
		//保证金
		double pledgePeriod = DataUtil.doubleUtil(paylist.get("PLEDGE_PERIOD"));
		double pledgeAvePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
		double pledgeLastPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"));
		double pledgeLastPriod = DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"));
		double pledgeBackPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE"));
		//首期租金
		double headhier = DataUtil.doubleUtil(paylist.get("HEAD_HIRE"));
		
		int leasePeriod = DataUtil.intUtil(paylist.get("LEASE_PERIOD"));
		
		int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		
		int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
		
		double salesTaxRate = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE")) / HUNDRED;
		
		/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//add by ShenQi  加入增值税内含 方案
			salesTaxRate=1/1.17*0.187;
		}*/
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		//
		int periodNum = 1;
		
		double renPrice = 0d;
		
		double monthPrice = 0d;
		
		double sumMonthPrice = 0d;
		
		// sumMonthPrice
		for (Map payline : paylines) {
		
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			sumMonthPrice += monthPrice;
		
		}
		// insure 2010-09-07 change the calculation of insure price
		double insureBaseRate = DataUtil.doubleUtil(paylist.get("INSURE_BASE_RATE")) / HUNDRED;
		
		double tempTotalInsurePrice = new BigDecimal(leaseTopric * insureBaseRate * leasePeriod * leaseTerm / MONTHS_OF_YEAR).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		logger.info("保险费为：" + tempTotalInsurePrice);
		
		// stamp tax
		double stampTaxTopric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_TOPRIC"));
		
		double stampTaxMonthpric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_MONTHPRIC"));
		
		double stampTaxInsurepric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_INSUREPRIC"));
		
		// stampTaxTopric = 万分之三     -- 調整購銷合同印花稅為萬分之三。
		// stampTaxMonthpric = 千分之一   -- 租賃合同總額印花稅為千分之一。
		// stampTaxInsurepric = 千分之一  -- 保險合同印花稅為千分之一。
		double stampTax = leaseTopric * stampTaxTopric / HUNDRED 
					+ sumMonthPrice * stampTaxMonthpric / HUNDRED
					+ tempTotalInsurePrice * stampTaxInsurepric / HUNDRED;
		
		logger.info("印花稅为：" + stampTax);
		
		paylist.put("STAMP_TAX_PRICE", stampTax);
		
		//
		double salesTax = 0d;
		//每一期保证金所用的钱,现在只算了平均冲抵
		//double depositPrice = Math.round( pledgeAvePrice / leasePeriod * HUNDRED) / HUNDRED;
		
        BigDecimal bd1 = new BigDecimal(Double.toString(pledgeAvePrice)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(Double.valueOf(leasePeriod))); 
        double depositPrice=bd1.divide(bd2,2,BigDecimal.ROUND_HALF_UP).doubleValue();

		double irrMonthPrice = 0d;
		
		double irrPrice = 0d;
		
		double[] cashFlowsTr = new double[leasePeriod+1];
		
		double[] cashFlowsIrr;
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			cashFlowsIrr = new double[leasePeriod+1];
			
		} else { // 期末
			cashFlowsIrr = new double[leasePeriod+2];			
		}
		
		double leaseRZE = DataUtil.doubleUtil(paylist.get("LEASE_RZE"));
		
		double pv_own_price=0d;		
		
		cashFlowsTr[0] = -leaseRZE;
//		cashFlowsTr[0] = -leaseTopric;		
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric+headhier;//headhier首期租金
				
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}
			
		} else { // 期末

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric;
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}			
		}
		
		//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算
		double totalFeeSetSalesTax = 0d;
		totalFeeSetSalesTax=DataUtil.doubleUtil(paylist.get("FEESET_TOTAL"));
		//
		
		int i = 1;
		
		for (Map payline : paylines) {
			//
			periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));			
			renPrice = DataUtil.doubleUtil(payline.get("REN_PRICE"));			
			//Marked by Michael 2012 01/30
			salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;			
			//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
//			if (periodNum == 1) {
//				salesTax=Math.round((renPrice+totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED;
//			} else {
//				salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;
//			}			
			//
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				if (periodNum >= (leasePeriod - pledgeLastPriod) || periodNum == paylines.size()) {
					irrMonthPrice = 0d;
				} else {
					//如果期初 则应付租金＝上一期预期租金－平均至每期的保证金
					//irrMonthPrice = monthPrice - depositPrice;
					irrMonthPrice = DataUtil.doubleUtil(paylines.get(periodNum).get("MONTH_PRICE")) - depositPrice;
				}
				
			} else { // 期末
				
				if (periodNum > (leasePeriod - pledgeLastPriod)) {
					irrMonthPrice = 0d;
				} else {
					irrMonthPrice = monthPrice - depositPrice;
				}
			}		
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					irrPrice = irrMonthPrice - salesTax - stampTax - tempTotalInsurePrice-(Math.round(totalFeeSetSalesTax * salesTaxRate * HUNDRED) / HUNDRED);
				} else {					
					payline.put("INSURE_PRICE", 0d);
					irrPrice = irrMonthPrice - salesTax;
				}
				
			} else { // 期末				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					//					
					irrPrice = irrMonthPrice  - stampTax - tempTotalInsurePrice;

				} else {
					double renPrices = DataUtil.doubleUtil(paylines.get(periodNum-2).get("REN_PRICE"));
					double salesTaxs = Math.round(renPrices * salesTaxRate * HUNDRED) / HUNDRED;
					
					payline.put("INSURE_PRICE", 0d);
					//Marked by Michael 2012 01/30  把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
					if(periodNum == 2){
						irrPrice = irrMonthPrice - salesTaxs- (Math.round((totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED);
					}else{
						irrPrice = irrMonthPrice - salesTaxs;
					}
				}
			}	

        	//保证金收入
			if (pledgePeriod != 0 && pledgePeriod == periodNum) {
				irrPrice = irrPrice + pledgeAvePrice + pledgeLastPrice+pledgeBackPrice;
			}
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				//保证金退回
				if (periodNum == leasePeriod) {
					irrPrice -= pledgeBackPrice;
					irrMonthPrice -= pledgeBackPrice;
				}
				
			} else { // 期末
				if (periodNum == leasePeriod) {
					irrPrice -= 0;
					irrMonthPrice -= 0;
				}
				
			}			
			
			
        	irrPrice = Math.round(irrPrice * HUNDRED) / HUNDRED;
        	
        	irrMonthPrice = Math.round(irrMonthPrice * Math.pow(10, 3))/Math.pow(10, 3);
        	
        	payline.put("DEPOSIT_PRICE", depositPrice);
        
        	payline.put("IRR_PRICE", irrPrice);
        	
        	payline.put("IRR_MONTH_PRICE", irrMonthPrice);
        	
        	/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//加入小车的 增值税内含  增值税栏位的值 add by ShenQi
        		salesTax=renPrice/1.17*0.187;
        	}*/
        	payline.put("SALES_TAX", salesTax);
        	

        	
        	
			if (periodNum == 1) {
				
				pv_own_price = leaseRZE - irrMonthPrice +renPrice;
			} else {
				pv_own_price =(new BigDecimal(pv_own_price).subtract(new BigDecimal(irrMonthPrice))).add(new BigDecimal(renPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			
			if (periodNum == leasePeriod) {
				
				pv_own_price = 0;
			}
			//LAST_PRICE 剩余本金
        	//payline.put("REAL_OWN_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")) - depositPrice);
			payline.put("REAL_OWN_PRICE", pv_own_price);
//计算TR	
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = 0-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				} 				
			} else { // 期末	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = irrMonthPrice-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				}  				
			}
        	cashFlowsIrr[i] = irrPrice;
        	i++;
		}
		
		double trRate = Math.round(IRRUtils.getIRR(cashFlowsTr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		//根据应付租金算 客户TR
		paylist.put("TR_RATE", trRate);
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			
			
		} else { // 期末
			cashFlowsIrr[leasePeriod+1] = -(Double.parseDouble(paylines.get(leasePeriod-1).get("SALES_TAX").toString())+pledgeBackPrice);
		}
		
		//double trIrrRate = Math.round(IRRUtils.getIRR(cashFlowsIrr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		double trIrrRate = Math.round(IRRUtils.getTrIRR(cashFlowsIrr, Double.NaN,DataUtil.intUtil(paylist.get("DEFER_PERIOD")),leaseTopric) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		//根据净现金流 实际TR
		paylist.put("TR_IRR_RATE", trIrrRate);
		
		//Add by Michael 2012 01/15--------------------------------------------	
		double loanRate = DataUtil.doubleUtil(paylist.get("LOAN_RATE")) / HUNDRED;      //资金成本
		double manageRate = DataUtil.doubleUtil(paylist.get("MANAGE_RATE")) / HUNDRED;  //管理成本
		double rate = loanRate + manageRate ;     //合计成本
		double netFinance = 0d;//净本金
		double netCurrentFinance = 0d;//当期净本金
		double currentRenPrice = 0d;  //当期利息
		double tempIrrPrice=0d;  //净现金流	
		double currentFinanceCostRen=0d;  //当期资金成本息
		BigDecimal bigNetCurrentFinance = null;
		BigDecimal bigCurrentRenPrice = null;   
		BigDecimal bigNetFinance = null;   
		BigDecimal bigCurrentFinanceCostRen = null; 
		netFinance=DataUtil.doubleUtil(paylist.get("LEASE_RZE")); //净本金=概算成本（前期净本金余额）

		for (Map payline : paylines) {
			tempIrrPrice = DataUtil.doubleUtil(payline.get("IRR_PRICE"));
			//根据实际TR计算当期利息
			currentRenPrice=netFinance*((trIrrRate/ HUNDRED)/12); //当期利息等于前期净本金*（trIrrRate/12）
			netCurrentFinance=tempIrrPrice-currentRenPrice; //当期净本金=当期净现金-当期利息
			currentFinanceCostRen = (netFinance*rate)/12; 	//当期资金成本息  當期淨本金餘額 * (合計成本/12)  合計成本 = 資金成本 + 管理成本					//资金成本息			
			netFinance=netFinance-netCurrentFinance;   //当期净本金余额=前期净本金余额-当期净本金
			
			bigNetCurrentFinance = new BigDecimal(String.valueOf(netCurrentFinance));
			bigCurrentRenPrice = new BigDecimal(String.valueOf(currentRenPrice));
			bigNetFinance = new BigDecimal(String.valueOf(netFinance));
			bigCurrentFinanceCostRen = new BigDecimal(String.valueOf(currentFinanceCostRen));
			
			payline.put("NETCURRENTFINANCE", bigNetCurrentFinance);
			payline.put("CURRENTRENPRICE", bigCurrentRenPrice);
			payline.put("NETFINANCE", bigNetFinance);  //前期净本金余额	
			payline.put("CURRENTFINANCECOSTREN", bigCurrentFinanceCostRen);  //当期资金成本息		
		}

		//------------------------------------------------------------------------------------		
        
	}
	
	
	@SuppressWarnings("unchecked")
	public static void calcalatePaylineIRRForMultiStage(Map paylist) {
		//计算现金流和应付租金
		double leaseTopric = DataUtil.doubleUtil(paylist.get("LEASE_TOPRIC"));
		//保证金
		double pledgePeriod = DataUtil.doubleUtil(paylist.get("PLEDGE_PERIOD"));
		double pledgeAvePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
		double pledgeLastPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"));
		double pledgeLastPriod = DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"));
		double pledgeBackPrice = DataUtil.doubleUtil(paylist.get("PLEDGE_BACK_PRICE"));
		//首期租金
		double headhier = DataUtil.doubleUtil(paylist.get("HEAD_HIRE"));
		
		int leasePeriod = DataUtil.intUtil(paylist.get("LEASE_PERIOD"));
		
		int leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		
		int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
		
		double salesTaxRate = DataUtil.doubleUtil(paylist.get("SALES_TAX_RATE")) / HUNDRED;
		
		/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//add by ShenQi  加入增值税内含 方案
			salesTaxRate=1/1.17*0.187;
		}*/
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		//
		int periodNum = 1;
		
		double renPrice = 0d;
		
		double monthPrice = 0d;
		
		double sumMonthPrice = 0d;
		
		// sumMonthPrice
		for (Map payline : paylines) {
		
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			sumMonthPrice += monthPrice;
		
		}
		// insure 2010-09-07 change the calculation of insure price
		double insureBaseRate = DataUtil.doubleUtil(paylist.get("INSURE_BASE_RATE")) / HUNDRED;
		
		double tempTotalInsurePrice = leaseTopric * insureBaseRate * leasePeriod * leaseTerm / MONTHS_OF_YEAR;

		logger.info("保险费为：" + tempTotalInsurePrice);
		
		// stamp tax
		double stampTaxTopric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_TOPRIC"));
		
		double stampTaxMonthpric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_MONTHPRIC"));
		
		double stampTaxInsurepric = DataUtil.doubleUtil(paylist.get("STAMP_TAX_INSUREPRIC"));
		
		// stampTaxTopric = 万分之三     -- 調整購銷合同印花稅為萬分之三。
		// stampTaxMonthpric = 千分之一   -- 租賃合同總額印花稅為千分之一。
		// stampTaxInsurepric = 千分之一  -- 保險合同印花稅為千分之一。
		double stampTax = leaseTopric * stampTaxTopric / HUNDRED 
					+ sumMonthPrice * stampTaxMonthpric / HUNDRED
					+ tempTotalInsurePrice * stampTaxInsurepric / HUNDRED;
		
		logger.info("印花稅为：" + stampTax);
		
		paylist.put("STAMP_TAX_PRICE", stampTax);
		
		//
		double salesTax = 0d;
		//每一期保证金所用的钱,现在只算了平均冲抵
		//double depositPrice = Math.round( pledgeAvePrice / leasePeriod * HUNDRED) / HUNDRED;
		
        BigDecimal bd1 = new BigDecimal(Double.toString(pledgeAvePrice)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(Double.valueOf(leasePeriod))); 
        double depositPrice=bd1.divide(bd2,2,BigDecimal.ROUND_HALF_UP).doubleValue();

		double irrMonthPrice = 0d;
		
		double irrPrice = 0d;
		
		//add by Michael 2013 02-16 For 跨月支付
		double eachTax =0d;  //每期税费
		
		double[] cashFlowsTr = new double[leasePeriod+1];
		double[] cashFlowsTrTemp = new double[leasePeriod*leaseTerm+1];
		double[] cashFlowsIrr;
		double[] cashFlowsIrrTemp;
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			cashFlowsIrr = new double[leasePeriod+1];
			cashFlowsIrrTemp = new double[leasePeriod*leaseTerm+1];
		} else { // 期末
			cashFlowsIrr = new double[leasePeriod+2];	
			cashFlowsIrrTemp = new double[leasePeriod*leaseTerm+2];
		}
		
		double leaseRZE = DataUtil.doubleUtil(paylist.get("LEASE_RZE"));
		
		double pv_own_price=0d;		
		
		cashFlowsTr[0] = -leaseRZE;
//		cashFlowsTr[0] = -leaseTopric;		
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric+headhier;//headhier首期租金
				
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}
			
		} else { // 期末

			if (pledgePeriod != 0) {
				cashFlowsIrr[0] =  -leaseTopric;
			} else {
				cashFlowsIrr[0] =  -leaseRZE;
			}			
		}
		
		//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算
		double totalFeeSetSalesTax = 0d;
		totalFeeSetSalesTax=DataUtil.doubleUtil(paylist.get("FEESET_TOTAL"));
		//
		
		int i = 1;
		
		for (Map payline : paylines) {
			//
			periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));			
			renPrice = DataUtil.doubleUtil(payline.get("REN_PRICE"));			
			//Marked by Michael 2012 01/30
			salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;			
			//add by Michael 2012 01/30 把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
//			if (periodNum == 1) {
//				salesTax=Math.round((renPrice+totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED;
//			} else {
//				salesTax = Math.round(renPrice * salesTaxRate * HUNDRED) / HUNDRED;
//			}			
			//
			monthPrice = DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				if (periodNum >= (leasePeriod - pledgeLastPriod) || periodNum == paylines.size()) {
					irrMonthPrice = 0d;
				} else {
					//如果期初 则应付租金＝上一期预期租金－平均至每期的保证金
					//irrMonthPrice = monthPrice - depositPrice;
					irrMonthPrice = DataUtil.doubleUtil(paylines.get(periodNum).get("MONTH_PRICE")) - depositPrice;
				}
				
			} else { // 期末
				
				if (periodNum > (leasePeriod - pledgeLastPriod)) {
					irrMonthPrice = 0d;
				} else {
					irrMonthPrice = monthPrice - depositPrice;
				}
			}		
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					irrPrice = irrMonthPrice - salesTax - stampTax - tempTotalInsurePrice-(Math.round(totalFeeSetSalesTax * salesTaxRate * HUNDRED) / HUNDRED);
					eachTax= 0 - salesTax - stampTax - tempTotalInsurePrice-(Math.round(totalFeeSetSalesTax * salesTaxRate * HUNDRED) / HUNDRED);
				} else {					
					payline.put("INSURE_PRICE", 0d);
					irrPrice = irrMonthPrice - salesTax;
					eachTax = - salesTax;
				}
				
			} else { // 期末				
				// 2010-09-07 wjw v1.2
				// change the calculation of insure price
				if (periodNum == 1) {
					//irrMonthPrice应付租金   salesTax营业税   stampTax印花税   tempTotalInsurePrice保险费
					payline.put("INSURE_PRICE", tempTotalInsurePrice);
					//					
					irrPrice = irrMonthPrice  - stampTax - tempTotalInsurePrice;
					eachTax = - (stampTax + tempTotalInsurePrice);

				} else {
					double renPrices = DataUtil.doubleUtil(paylines.get(periodNum-2).get("REN_PRICE"));
					double salesTaxs = Math.round(renPrices * salesTaxRate * HUNDRED) / HUNDRED;
					
					payline.put("INSURE_PRICE", 0d);
					//Marked by Michael 2012 01/30  把管理费收入总和传过来，计算营业税收入，会影响TR计算 把管理费收入的营业税放入到第一期营业税里 
					if(periodNum == 2){
						irrPrice = irrMonthPrice - (Math.round((renPrices+totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED);
						eachTax = 0 - (Math.round((renPrices+totalFeeSetSalesTax) * salesTaxRate * HUNDRED) / HUNDRED);
					}else{
						irrPrice = irrMonthPrice - salesTaxs;
						eachTax = - salesTaxs;
					}
				}
			}	

        	//保证金收入
			if (pledgePeriod != 0 && pledgePeriod == periodNum) {
				irrPrice = irrPrice + pledgeAvePrice + pledgeLastPrice+pledgeBackPrice;
			}
			
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
				
				//保证金退回
				if (periodNum == leasePeriod) {
					irrPrice -= pledgeBackPrice;
					irrMonthPrice -= pledgeBackPrice;
				}
				
			} else { // 期末
				if (periodNum == leasePeriod) {
					irrPrice -= 0;
					irrMonthPrice -= 0;
					eachTax-=0;
				}
				
			}			
			
        	irrPrice = Math.round(irrPrice * HUNDRED) / HUNDRED;
        	
        	irrMonthPrice = Math.round(irrMonthPrice * Math.pow(10, 3))/Math.pow(10, 3);
        	
        	payline.put("DEPOSIT_PRICE", depositPrice);
        
        	payline.put("IRR_PRICE", irrPrice);
        	
        	payline.put("IRR_MONTH_PRICE", irrMonthPrice);
        	
        	/*if("3".equals(String.valueOf(paylist.get("TAX_PLAN_CODE")))) {//加入小车的 增值税内含  增值税栏位的值 add by ShenQi
        		salesTax=renPrice/1.17*0.187;
        	}*/
        	
        	payline.put("SALES_TAX", salesTax);
        	
			if (periodNum == 1) {
				
				pv_own_price = leaseRZE - irrMonthPrice +renPrice;
			} else {
				pv_own_price =(new BigDecimal(pv_own_price).subtract(new BigDecimal(irrMonthPrice))).add(new BigDecimal(renPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			
			if (periodNum == leasePeriod) {
				
				pv_own_price = 0;
			}
			//LAST_PRICE 剩余本金
        	//payline.put("REAL_OWN_PRICE", DataUtil.doubleUtil(payline.get("LAST_PRICE")) - depositPrice);
			payline.put("REAL_OWN_PRICE", pv_own_price);
//计算TR	
			if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
					|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = 0-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				} 				
			} else { // 期末	        	
				if (periodNum == leasePeriod) {
					cashFlowsTr[i] = irrMonthPrice-pledgeBackPrice;					
				}else{
					cashFlowsTr[i] = irrMonthPrice;
				}  				
			}
        	cashFlowsIrr[i] = irrPrice;
        	i++;
        	
        	eachTax = Math.round(eachTax * HUNDRED) / HUNDRED;
        	payline.put("EACH_TAX", eachTax);
        	payline.put("EACH_TAX_CUSTTR", cashFlowsTr[i]);
		}
		
		generateCustTRIrrDataFlows(paylines,leaseTerm,leasePeriod,cashFlowsTr[0],cashFlowsTrTemp);
		
		double trRate = Math.round(IRRUtils.getIRR(cashFlowsTr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		//根据应付租金算 客户TR
		paylist.put("TR_RATE", trRate);
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			
			
		} else { // 期末
			cashFlowsIrr[leasePeriod+1] = -(Double.parseDouble(paylines.get(leasePeriod-1).get("SALES_TAX").toString())+pledgeBackPrice);
			//用于计算实际TR 一期多月时  IRR 数据流
			cashFlowsIrrTemp[leasePeriod*leaseTerm+1] = -(pledgeBackPrice);
		}
		
		generateTrueTRIrrDataFlows(paylines,leaseTerm,leasePeriod,cashFlowsIrr[0],cashFlowsIrrTemp,payWay,-(Double.parseDouble(paylines.get(leasePeriod-1).get("SALES_TAX").toString())));
		
		//double trIrrRate = Math.round(IRRUtils.getIRR(cashFlowsIrr, Double.NaN) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		double trIrrRate = Math.round(IRRUtils.getTrIRR(cashFlowsIrr, Double.NaN,DataUtil.intUtil(paylist.get("DEFER_PERIOD")),leaseTopric) * 12.0d * Math.pow(10, 10)) / Math.pow(10, 8);
		
		//根据净现金流 实际TR
		paylist.put("TR_IRR_RATE", trIrrRate);
		
		//Add by Michael 2012 01/15--------------------------------------------	
		double loanRate = DataUtil.doubleUtil(paylist.get("LOAN_RATE")) / HUNDRED;      //资金成本
		double manageRate = DataUtil.doubleUtil(paylist.get("MANAGE_RATE")) / HUNDRED;  //管理成本
		double rate = loanRate + manageRate ;     //合计成本
		double netFinance = 0d;//净本金
		double netCurrentFinance = 0d;//当期净本金
		double currentRenPrice = 0d;  //当期利息
		double tempIrrPrice=0d;  //净现金流	
		double currentFinanceCostRen=0d;  //当期资金成本息
		BigDecimal bigNetCurrentFinance = null;
		BigDecimal bigCurrentRenPrice = null;   
		BigDecimal bigNetFinance = null;   
		BigDecimal bigCurrentFinanceCostRen = null; 
		netFinance=DataUtil.doubleUtil(paylist.get("LEASE_RZE")); //净本金=概算成本（前期净本金余额）

		for (Map payline : paylines) {
			tempIrrPrice = DataUtil.doubleUtil(payline.get("IRR_PRICE"));
			//根据实际TR计算当期利息
			currentRenPrice=netFinance*((trIrrRate/ HUNDRED)/12); //当期利息等于前期净本金*（trIrrRate/12）
			netCurrentFinance=tempIrrPrice-currentRenPrice; //当期净本金=当期净现金-当期利息
			currentFinanceCostRen = (netFinance*rate)/12; 	//当期资金成本息  當期淨本金餘額 * (合計成本/12)  合計成本 = 資金成本 + 管理成本					//资金成本息			
			netFinance=netFinance-netCurrentFinance;   //当期净本金余额=前期净本金余额-当期净本金
			
			bigNetCurrentFinance = new BigDecimal(String.valueOf(netCurrentFinance));
			bigCurrentRenPrice = new BigDecimal(String.valueOf(currentRenPrice));
			bigNetFinance = new BigDecimal(String.valueOf(netFinance));
			bigCurrentFinanceCostRen = new BigDecimal(String.valueOf(currentFinanceCostRen));
			
			payline.put("NETCURRENTFINANCE", bigNetCurrentFinance);
			payline.put("CURRENTRENPRICE", bigCurrentRenPrice);
			payline.put("NETFINANCE", bigNetFinance);  //前期净本金余额	
			payline.put("CURRENTFINANCECOSTREN", bigCurrentFinanceCostRen);  //当期资金成本息		
		}

		//------------------------------------------------------------------------------------		
        
	}
	
	/**
	 * 2013 02-17 For 一期多月时 计算客户TR IRR 数据流量
	 * @author michael
	 * @param paylines
	 * @param leaseTerm
	 * @param leasePeriod
	 * @param firstValue
	 * @param cashFlowsTemp
	 * @return
	 */
	public static double[] generateCustTRIrrDataFlows(List paylines,int leaseTerm,int leasePeriod,double firstValue,double[] cashFlowsTemp ){
		cashFlowsTemp[0]=firstValue;
		int k=1;
		for(int i=1;i<=leasePeriod;i++){
			for (int j = 1; j <= leaseTerm; j++) {
				if(j<leaseTerm){
					cashFlowsTemp[k]=0;
				}else{
					cashFlowsTemp[k]=DataUtil.doubleUtil(((Map) paylines.get(i)).get("EACH_TAX_CUSTTR"));
				}
				k++;
			}
		}

		return cashFlowsTemp;
	}

	/**
	 * 2013 02-17 For 一期多月时 计算实际TR IRR 数据流量
	 * @author michael
	 * @param paylines
	 * @param leaseTerm
	 * @param leasePeriod
	 * @param firstValue
	 * @param cashFlowsTemp
	 * @return
	 */
	public static double[] generateTrueTRIrrDataFlows(List paylines,int leaseTerm,int leasePeriod,double firstValue,double[] cashFlowsTemp,int payWay ,double lastTaxPrice){
		cashFlowsTemp[0]=firstValue;
		int k=1;
		
		if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
				|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
			for(int i=1;i<=leasePeriod;i++){
				cashFlowsTemp[k]=DataUtil.doubleUtil(((Map) paylines.get(i)).get("EACH_TAX"));
				k++;
				for (int j = 1; j <= leaseTerm; j++) {
					if(j<leaseTerm){
						cashFlowsTemp[k]=0;
					}else{
						cashFlowsTemp[k]=DataUtil.doubleUtil(((Map) paylines.get(i)).get("IRR_MONTH_PRICE"));
					}
				}
			}
			
		}else{
			
		}

		return cashFlowsTemp;
	}
	
	/**
	 * 2010-11-17 wujw v1.7
	 * balance the monthprice of paylist 
	 * keep the month price total is rounded
	 * balance it by change last payline
	 * 1.monthprice 2.ren_price 
	 * @param paylist
	 */
	@SuppressWarnings("unchecked")
	public static void balanceMonthPrice(Map paylist,List<Map> rePaylineList) {
		logger.info("balanceMonthPrice ...." + rePaylineList.size());
		if (rePaylineList.size() == 0) {
			return;
		}
		// should value
		int leasePeriod = DataUtil.intUtil(paylist.get("LEASE_PERIOD"));
		
		double pledgeAvePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_AVE_PRICE"));
		double pledgeLastPriod = DataUtil.intUtil(paylist.get("PLEDGE_LAST_PERIOD"));
		
		double realMonthPrice = 0d;
		
		for (Map payline : rePaylineList) {
			
			realMonthPrice += DataUtil.doubleUtil(payline.get("IRR_MONTH_PRICE"));
		}
		
		double realTotalMonthPrice = new BigDecimal(pledgeAvePrice).add(new BigDecimal(realMonthPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		// calculate value
		Map lastPayline = null;
		
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		
		double calTotalMontprice = 0d;

		for (Map payline : paylines) {

			calTotalMontprice = new BigDecimal(calTotalMontprice).add(new BigDecimal(DataUtil.doubleUtil(payline.get("MONTH_PRICE")))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() ;
			
			int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
			
			if (periodNum == paylines.size()) {
				
				lastPayline = payline;
			
			}
		}
		// balance last payline monthprice and renPrice
		double remainder = new BigDecimal(realTotalMonthPrice).subtract(new BigDecimal(calTotalMontprice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() ;
		
		double balanceMonthPrice = 0d;
		double balanceRenPrice = 0d;
		
		if (remainder != 0) {
			paylist.put("hu_weicha", "true");
			double tempMonthPrice = DataUtil.doubleUtil(lastPayline.get("MONTH_PRICE"));
			double ownPrice = DataUtil.doubleUtil(lastPayline.get("OWN_PRICE"));
			
			balanceMonthPrice = new BigDecimal(tempMonthPrice).add(new BigDecimal(remainder)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() ;
			balanceRenPrice = new BigDecimal(balanceMonthPrice).subtract(new BigDecimal(ownPrice)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			//
			int payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
			
			for (Map payline : paylines) {
				
				int periodNum = DataUtil.intUtil(payline.get("PERIOD_NUM"));
				
				if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {  // 期初
					
					if (periodNum >= (leasePeriod - pledgeLastPriod)) {
						payline.put("IRR_MONTH_PRICE", 0);
					} else {
						payline.put("IRR_MONTH_PRICE", rePaylineList.get(periodNum-1).get("IRR_MONTH_PRICE"));
					}
					
				} else { // 期末
					
					if (periodNum > (leasePeriod - pledgeLastPriod)) {
						payline.put("IRR_MONTH_PRICE", 0);
					} else {
						payline.put("IRR_MONTH_PRICE", rePaylineList.get(periodNum-1).get("IRR_MONTH_PRICE"));
					}
				}
				
				if (periodNum == leasePeriod) {
					payline.put("MONTH_PRICE", balanceMonthPrice);
					payline.put("REN_PRICE", balanceRenPrice);
				}
				
			}
			
		}
		
		
	}
	/**
	 * calculate the loss price of paylines
	 * @param paylist
	 */
	@SuppressWarnings("unchecked")
	public static void calculatePaylineLossPrice(Map paylist) {
		
		double pledgePrice = DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE"));
		List<Map> paylines = (List<Map>) paylist.get("paylines");
		
		int i = 0;
		double sumMonthPrice = 0d;
		
		for (Map payline : paylines) {
			if (i != 0) {
				sumMonthPrice += DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			}
			i++;
		}
		sumMonthPrice += pledgePrice;
		//
		i = 0;
		for (Map payline : paylines) {
			
			if (i != 0) {
				sumMonthPrice = sumMonthPrice - DataUtil.doubleUtil(payline.get("MONTH_PRICE"));
			}
			
			payline.put("LOSS_PRICE", sumMonthPrice);
			
			i++;
		}
		
	}
	
}
