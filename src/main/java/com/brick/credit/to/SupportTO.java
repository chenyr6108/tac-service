package com.brick.credit.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 创建人：沈祺
 * 创建时间 ：2014-8-5
 * */
public class SupportTO {
	public static enum KeyOfMap {  
		IRR_MONTH_PRICE,//应付租金
		START_PERIOD,//开始期数
		END_PERIOD,//结束期数
		PERIOD_NUM,//期数
		MONTH_PRICE,
		OWN_PRICE,//本金
		REN_PRICE,//利息
		LAST_PRICE,//剩余本金
		PV_PRICE,//利差
		VALUE_ADDED_TAX,//平均增值税
		VALUE_ADDED_TAX_TRUE//实际增值税
	} 
	
	//注释为对应页面的栏位,只列出业务支撑需要更新的栏位
	private String creditId;//报告号
	private String leaseTotalVal;//融资租赁总价值
	private String leaseRZE;//概算成本
	private String leasePeriod;//租赁期数
	private String payWay;//支付方式
	private String taxPlanCode;//税费测算方案
	private String payMoney;//佣金
	private String incomePayType;//手续费类型
	private String incomePay;//手续费收入
	private String outPay;//银行手续费
	private List<Map<Enum<KeyOfMap>,Object>> payList=new ArrayList<Map<Enum<KeyOfMap>,Object>>();//租金列表
	private List<Map<Enum<KeyOfMap>,Object>> collectionList=new ArrayList<Map<Enum<KeyOfMap>,Object>>();//支付表
	private String firstIrrMonthPrice;//首期租金
	private String deposit;//保证金总额
	private String depositA;//用于平均抵充或者一次抵充
	private int depositBNum;//用于抵充最后期数
	private String depositB;//用于抵充最后期数保证金
	private String depositC;//用于期末返还
	private int delayPay;//延迟拨款期数
	private String toCompany;//入我司
	private String companyToSupplier;//我司入供应商
	private String toSupplier;//入供应商
	private String manageFee1;//管理费1
	private String manageFee2;//管理费2
	private String homeFee;//家访费
	private String setupFee;//设定费收入
	private String otherFee;//其他费用收入
	private String insuranceDeputyFee;//保险费押金代收款收入
	private String cTR;//客户TR
	private String aTR;//实际TR
	private String rateDiff;//利差
	private String yearInterest;//合同利率
	
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getLeaseTotalVal() {
		return leaseTotalVal;
	}
	public void setLeaseTotalVal(String leaseTotalVal) {
		this.leaseTotalVal = leaseTotalVal;
	}
	public String getLeaseRZE() {
		return leaseRZE;
	}
	public void setLeaseRZE(String leaseRZE) {
		this.leaseRZE = leaseRZE;
	}
	public String getLeasePeriod() {
		return leasePeriod;
	}
	public void setLeasePeriod(String leasePeriod) {
		this.leasePeriod = leasePeriod;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getTaxPlanCode() {
		return taxPlanCode;
	}
	public void setTaxPlanCode(String taxPlanCode) {
		this.taxPlanCode = taxPlanCode;
	}
	public String getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	public String getIncomePayType() {
		return incomePayType;
	}
	public void setIncomePayType(String incomePayType) {
		this.incomePayType = incomePayType;
	}
	public String getIncomePay() {
		return incomePay;
	}
	public void setIncomePay(String incomePay) {
		this.incomePay = incomePay;
	}
	public String getOutPay() {
		return outPay;
	}
	public void setOutPay(String outPay) {
		this.outPay = outPay;
	}
	public List<Map<Enum<KeyOfMap>, Object>> getPayList() {
		return payList;
	}
	public void setPayList(List<Map<Enum<KeyOfMap>, Object>> payList) {
		this.payList = payList;
	}
	public List<Map<Enum<KeyOfMap>, Object>> getCollectionList() {
		return collectionList;
	}
	public void setCollectionList(List<Map<Enum<KeyOfMap>, Object>> collectionList) {
		this.collectionList = collectionList;
	}
	public String getFirstIrrMonthPrice() {
		return firstIrrMonthPrice;
	}
	public void setFirstIrrMonthPrice(String firstIrrMonthPrice) {
		this.firstIrrMonthPrice = firstIrrMonthPrice;
	}
	public String getDeposit() {
		return deposit;
	}
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public String getDepositA() {
		return depositA;
	}
	public void setDepositA(String depositA) {
		this.depositA = depositA;
	}
	public int getDepositBNum() {
		return depositBNum;
	}
	public void setDepositBNum(int depositBNum) {
		this.depositBNum = depositBNum;
	}
	public String getDepositB() {
		return depositB;
	}
	public void setDepositB(String depositB) {
		this.depositB = depositB;
	}
	public String getDepositC() {
		return depositC;
	}
	public void setDepositC(String depositC) {
		this.depositC = depositC;
	}
	public int getDelayPay() {
		return delayPay;
	}
	public void setDelayPay(int delayPay) {
		this.delayPay = delayPay;
	}
	public String getToCompany() {
		return toCompany;
	}
	public void setToCompany(String toCompany) {
		this.toCompany = toCompany;
	}
	public String getCompanyToSupplier() {
		return companyToSupplier;
	}
	public void setCompanyToSupplier(String companyToSupplier) {
		this.companyToSupplier = companyToSupplier;
	}
	public String getToSupplier() {
		return toSupplier;
	}
	public void setToSupplier(String toSupplier) {
		this.toSupplier = toSupplier;
	}
	public String getManageFee1() {
		return manageFee1;
	}
	public void setManageFee1(String manageFee1) {
		this.manageFee1 = manageFee1;
	}
	public String getManageFee2() {
		return manageFee2;
	}
	public void setManageFee2(String manageFee2) {
		this.manageFee2 = manageFee2;
	}
	public String getHomeFee() {
		return homeFee;
	}
	public void setHomeFee(String homeFee) {
		this.homeFee = homeFee;
	}
	public String getSetupFee() {
		return setupFee;
	}
	public void setSetupFee(String setupFee) {
		this.setupFee = setupFee;
	}
	public String getOtherFee() {
		return otherFee;
	}
	public void setOtherFee(String otherFee) {
		this.otherFee = otherFee;
	}
	public String getInsuranceDeputyFee() {
		return insuranceDeputyFee;
	}
	public void setInsuranceDeputyFee(String insuranceDeputyFee) {
		this.insuranceDeputyFee = insuranceDeputyFee;
	}
	public String getcTR() {
		return cTR;
	}
	public void setcTR(String cTR) {
		this.cTR = cTR;
	}
	public String getaTR() {
		return aTR;
	}
	public void setaTR(String aTR) {
		this.aTR = aTR;
	}
	public String getRateDiff() {
		return rateDiff;
	}
	public void setRateDiff(String rateDiff) {
		this.rateDiff = rateDiff;
	}
	public String getYearInterest() {
		return yearInterest;
	}
	public void setYearInterest(String yearInterest) {
		this.yearInterest = yearInterest;
	}
	
}
