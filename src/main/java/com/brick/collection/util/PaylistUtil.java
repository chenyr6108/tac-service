package com.brick.collection.util;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

import com.brick.collection.core.PVUtils;
import com.brick.service.core.DataAccessor;
import com.brick.util.DataUtil;
/**
 * @author wujw
 * @date Jan 13, 2011
 * @version 
 */
public class PaylistUtil {
	
	public static Integer YEAR_MONTH = Integer.valueOf(12);
	public static Integer HUNDRED = Integer.valueOf(100);
	/**
	 * set cost_price of payline ,just for calculate rate different;<br>
	 * insert COST_PRICE into payline
	 * 
	 * @param paylist
	 * @param paylines contain LAST_PRICE
	 * @param LOAN_RATE
	 * @param MANAGE_RATE
	 * @param LEASE_TERM
	 */
	@SuppressWarnings("unchecked")
	public static void calculateLoanPaylist(Map paylist) {
		//计算成本   
		List<Map> paylines = (List<Map>) paylist.get("paylines");

		double loanRate = DataUtil.doubleUtil(paylist.get("LOAN_RATE")) / HUNDRED;
		double manageRate = DataUtil.doubleUtil(paylist.get("MANAGE_RATE")) / HUNDRED;
		double rate = loanRate + manageRate ;
		double leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		
		//实际TR
		double trIrrRate=DataUtil.doubleUtil(paylist.get("TR_IRR_RATE"));		
		int periodNums=1;
		
		for (Map payline : paylines) {
			//剩余本金 
			//double lastPrice = DataUtil.doubleUtil(payline.get("LAST_PRICE"));
			//实际本金 
			double REAL_OWN_PRICE = DataUtil.doubleUtil(payline.get("REAL_OWN_PRICE"));
			double costPrice = REAL_OWN_PRICE * rate / YEAR_MONTH * leaseTerm;
			//利息
			double ren_price = DataUtil.doubleUtil(payline.get("REN_PRICE"));
			//营业税
			double sales_tax = DataUtil.doubleUtil(payline.get("SALES_TAX"));
			//现值   利差
			
			//Modify by mcihael 2012 01/15 修改利差计算公式
			//double pv_price = PVUtils.pv2(loanRate, Integer.parseInt(payline.get("PERIOD_NUM").toString()), ren_price - sales_tax - costPrice);
			//用新的公式计算利差
			//当期资金成本息-当期利息
			double currentFinanceCostRen=DataUtil.doubleUtil(payline.get("CURRENTFINANCECOSTREN"));   //当期成本息
			double currentRenPrice=DataUtil.doubleUtil(payline.get("CURRENTRENPRICE"));			//当期利息
			double pv_price=0d;			
			pv_price = PVUtils.pv2(loanRate, Integer.parseInt(payline.get("PERIOD_NUM").toString()), -(currentFinanceCostRen - currentRenPrice));
			
			//Add by Michael 2012 01/31  因后收会有增加一期的本金余额，所以最后一期时，再进行一次运算--------------------
			double temp_pv_price=0d;
			double netFinance = 0d;//净本金
			double netCurrentFinance = 0d;//当期净本金
			double tempIrrPrice=0d;  //净现金流					
			periodNums= DataUtil.intUtil(payline.get("PERIOD_NUM"));
			//Add by Michael 2012 01/31  因后收会有增加一期的本金余额，所以最后一期时，再进行一次运算--------------
			if (periodNums==paylines.size()) {
				netFinance=DataUtil.doubleUtil(payline.get("NETFINANCE"));
				tempIrrPrice = DataUtil.doubleUtil(payline.get("IRR_PRICE"));
				
				currentRenPrice=netFinance*((trIrrRate/ HUNDRED)/12); 
				netCurrentFinance=tempIrrPrice-currentRenPrice; 
				currentFinanceCostRen = (netFinance*rate)/12; 	
				netFinance=netFinance-netCurrentFinance;   
				temp_pv_price= PVUtils.pv2(loanRate, paylines.size()+1, -(currentFinanceCostRen - currentRenPrice));
				pv_price+=temp_pv_price;
			}
			//-------------------------------------------------------------------------------------------
			
			payline.put("PV_PRICE", pv_price);
			payline.put("COST_PRICE", costPrice);
		}

	}
	
	@SuppressWarnings("unchecked")
	public static void calculateLoanPaylistCostPrice(Map paylist) {
		//计算成本   
		List<Map> paylines = (List<Map>) paylist.get("paylines");

		double loanRate = DataUtil.doubleUtil(paylist.get("LOAN_RATE")) / HUNDRED;
		double manageRate = DataUtil.doubleUtil(paylist.get("MANAGE_RATE")) / HUNDRED;
		double rate = loanRate + manageRate ;
		double leaseTerm = DataUtil.intUtil(paylist.get("LEASE_TERM"));
		for (Map payline : paylines) {
			//剩余本金 
			//double lastPrice = DataUtil.doubleUtil(payline.get("LAST_PRICE"));
			//实际本金 
			double REAL_OWN_PRICE = DataUtil.doubleUtil(payline.get("REAL_OWN_PRICE"));
			double costPrice = REAL_OWN_PRICE * rate / YEAR_MONTH * leaseTerm;

			payline.put("COST_PRICE", costPrice);
		}

	}
	/**
	 * set base rate for paylist that from the configuration table
	 * @param paylist
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void setBaseRate(Map paylist){
		//设置基础利率
		try {
			
			List<Map> baseRateList = (List<Map>) DataAccessor.query("moneyRate.queryAll", paylist, DataAccessor.RS_TYPE.LIST);
			
			for (Map map : baseRateList) {
				paylist.put(DataUtil.StringUtil(map.get("FILED_NAME")),DataUtil.doubleUtil(map.get("RATE_VALUE")));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//根据不同的合同类型来判断保险费税率
	@SuppressWarnings("unchecked")
	public static void setBaseRate(Map paylist,String contractType){
		//设置基础利率
		try {
			
			List<Map> baseRateList = (List<Map>) DataAccessor.query("moneyRate.queryAll", paylist, DataAccessor.RS_TYPE.LIST);
			
			for (Map map : baseRateList) {
				paylist.put(DataUtil.StringUtil(map.get("FILED_NAME")),DataUtil.doubleUtil(map.get("RATE_VALUE")));
			}
			// Add by Michael 2012 07-12 重车及重车回租不需要保险费计算，乘用车类型
			if (contractType.equals("3")||("4").equals(contractType)||("6").equals(contractType)||("8").equals(contractType)||("14").equals(contractType)
					||("10").equals(contractType)
					||("11").equals(contractType)
					||("12").equals(contractType)
					||("13").equals(contractType)){
				paylist.put("INSURE_BASE_RATE",DataUtil.doubleUtil("0.0"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
