package com.brick.rent.dao;

import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.to.ReportDateTo;
import com.brick.rent.to.RentFinanceTO;
import com.brick.rent.to.SettlementLogTO;
import com.brick.rent.to.SettlementTO;
import com.brick.service.entity.Context;
import com.ibatis.sqlmap.client.SqlMapClient;

public class RentFinanceDAO extends BaseDAO {
	
	public List<RentFinanceTO> getUnDecomposeMoney(Map<String,Object> param) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getUnDecomposeMoney",param);
	}
	
	public String checkCustNameHasPayList(Context context) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkCustNameHasPayList",context.contextMap);
	}
	
	public Map<String,Object> getIncomeInfoByIncomeId(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getIncomeInfoByIncomeId",context.contextMap);
	}
	
	public List<Map<String,Object>> getLeasePriceByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getLeasePriceByCustCode",context.contextMap);
	}
	
	public Map<String,Object> getDecomposePrice(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getDecomposePrice",context.contextMap);
	}
	
	public void insertRentDecompose(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertRentDecompose",context.contextMap);
	}
	public void insertRent(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertRent",context.contextMap);
	}
	
	public List<Map<String,Object>> getDecomposePriceDetail(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getDecomposePriceDetail",context.contextMap);
	}
	
	public Map<String,Object> getRentPayDetail(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getRentPayDetail",context.contextMap);
	}
	
	public Map<String,Object> getReduceOwnPrice(Map<String,Object> param) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getReduceOwnPrice",param);
	}
	
	public List<Map<String,Object>> getFeeByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getFeeByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getPledgeAByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getPledgeAByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getPledgeBByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getPledgeBByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getPledgeCByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getPledgeCByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getStayBuyByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getStayBuyByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getTaxByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getTaxByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getLawFeeByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getLawFeeByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getIncomePayByCustCode(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getIncomePayByCustCode",context.contextMap);
	}
	
	public List<Map<String,Object>> getRedListByBillId(Context context) throws Exception {
		return (List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("rentFinance.getRedListByBillId",context.contextMap);
	}
	
	public Map<String,Object> getCustInfoByRecpId(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getCustInfoByRecpId",context.contextMap);
	}
	
	public Double getSettlementOwnPrice(Context context) throws Exception {
		Double ownPrice=(Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementOwnPrice",context.contextMap);
		if(ownPrice==null) {
			return 0d;
		}
		return ownPrice;
	}
	
	public Double getSettlementValueAddedTax(Context context) throws Exception {
		Double valAdd=(Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementValueAddedTax",context.contextMap);
		if(valAdd==null) {
			return 0d;
		}
		return valAdd;
	}
	
	public Double getSettlementInterest(Context context) throws Exception {
		Double interest=(Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementInterest",context.contextMap);
		if(interest==null) {
			return 0d;
		}
		return interest;
	}
	
	public Double getSettlementStayFee(Context context) throws Exception {
		Double stayFee=(Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementStayFee",context.contextMap);
		if(stayFee==null) {
			return 0d;
		}
		return stayFee;
	}
	
	public Double getSettlementLawFee(Context context) throws Exception {
		Double lawFee=(Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementLawFee",context.contextMap);
		if(lawFee==null) {
			return 0d;
		}
		return lawFee;
	}
	
	public boolean checkDuplicateCommit(Context context) throws Exception {
		
		String result=(String)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkDuplicateCommit",context.contextMap);
		
		if("Y".equals(result)) {
			return true;
		}
		 return false;
	}
	
	public boolean checkPendingData(Context context) throws Exception {
		
		String result=(String)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkPendingData",context.contextMap);
		
		if("Y".equals(result)) {
			return true;
		}
		 return false;
	}
	
	public void insertSettlement(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertSettlement",context.contextMap);
	}
	
	public List<Map<String,Object>> getSettlementHistoryByRecpId(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementHistoryByRecpId",context.contextMap);
	}
	
	public void approveOrRejectSettlement(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.approveOrRejectSettlement",context.contextMap);
	}
	
	public void insertSettlementLog(SettlementLogTO setLog) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.insertSettlementLog",setLog);
	}
	
	public Map<String,Object> getSettlementDetailByIdForEmail(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementDetailByIdForEmail",context.contextMap);
	}
	
	public void updateSettlementState(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateSettlementState",context.contextMap);
	}
	
	public void updateSettleDateByRecpId(Context context) throws Exception{
		this.getSqlMapClientTemplate().update("rentFinance.updateSettleDateByRecpId",context.contextMap);
	}
	
	public Map<String,Object> getSettlementDetailById(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementDetailById",context.contextMap);
	}
	
	public SettlementTO getSettlementById(Context context) throws Exception {
		return (SettlementTO)this.getSqlMapClientTemplate().queryForObject("rentFinance.getSettlementById",context.contextMap);
	}
	
	public List<Map<String,Object>> getSettlementDetailByRecpId(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementDetailByRecpId",context.contextMap);
	}
	
	public void updatePayDetailTableByRecpId(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updatePayDetailTableByRecpId",context.contextMap);
	}
	
	public String getEmailByRecpId(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("rentFinance.getEmailByRecpId",param);
	}
	
	public List<Map<String,Object>> getCustInfo() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getCustInfo");
	}
	
	public List<Map<String,Object>> getSettlementCustInfo() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementCustInfo");
	}
	
	public Map<String,Object> getLastDepositB(Context context) throws Exception {
		return (Map)this.getSqlMapClientTemplate().queryForObject("rentFinance.getLastDepositB",context.contextMap);
	}
	
	public List<Map<String,Object>> getDepositBRedList(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getDepositBRedList",context.contextMap);
	}
	
	public String checkRedDecomposeIsLock(Context context) throws Exception {
		String res=(String)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkRedDecomposeIsLock",context.contextMap);
		
		return res==null?"N":res;
	}
	
	public String checkRedDepositBCDecomposeIsLock(Context context) throws Exception {
		String res=(String)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkRedDepositBCDecomposeIsLock",context.contextMap);
		
		return res==null?"N":res;
	}
	
	public int checkCanBeAutoDecompose() throws Exception {
		return (Integer)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkCanBeAutoDecompose");
	}
	
	public long insertRedDecompose(Context context) throws Exception {
		return (Long)this.getSqlMapClientTemplate().insert("rentFinance.insertRedDecompose",context.contextMap);
	}
	
	public void insertRed(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertRed",context.contextMap);
	}
	
	public void updateRedFlag(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateRedFlag",context.contextMap);
	}
	
	public void commitRecord(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.commitRecord",context.contextMap);
	}
	
	public List<Map<String,Object>> getNullPrincipalList(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getNullPrincipalList",context.contextMap);
	}
	
	public void updateNullPrincipalByBillId(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateNullPrincipalByBillId",param);
	}
	
	public void updateNullPrincipalByBillIdPeriodNumRecpId(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateNullPrincipalByBillIdPeriodNumRecpId",param);
	}
	
	public double checkClaimRefundAmount(Context context) throws Exception {
		return (Double)this.getSqlMapClientTemplate().queryForObject("rentFinance.checkClaimRefundAmount",context.contextMap);
	}
	
	public void insertClaim(Context context) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertClaim",context.contextMap);
	}
	
	public List<Map<String,Object>> showClaimRefundList(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.showClaimRefundList",context.contextMap);
	}
	
	public void addRemark(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.addRemark",context.contextMap);
	}
	
	public List<Map<String,Object>> getUploadFileList(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getUploadFileList",context.contextMap);
	}
	
	public List<Map<String,Object>> getCashReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getCashReport",context.contextMap);
	}
	
	public List<Map<String,Object>> getFundReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getFundReport",context.contextMap);
	}
	
	public List<Map<String,Object>> getBalanceReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getBalanceReport",context.contextMap);
	}
	
	public Map<String,Object> getOwnPriceAndRenPrice(Map<String,Object> param) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getOwnPriceAndRenPrice",param);
	}
	
	public String getClaimCause(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("rentFinance.getClaimCause",param);
	}
	public String getRefundCause(Map<String,Object> param) throws Exception {
		return (String)this.getSqlMapClientTemplate().queryForObject("rentFinance.getRefundCause",param);
	}
	
	public List<Map<String,Object>> getDeptList() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("common.getCompanyList");
	}
	
	public ReportDateTo getDateReportMap() throws Exception {
		return (ReportDateTo)this.getSqlMapClientTemplate().queryForObject("reportDateUtil.queryDecomposeReportBeginToEndPeriod");
	}
	
	public void insertDecomposeDailyReport(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertDecomposeDailyReport",param);
	}
	public void insertDecomposeDailyDynamicReport(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertDecomposeDailyDynamicReport",param);
	}
	
	public List<Map<String,Object>> getHistoryCashReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getHistoryCashReport",context.contextMap);
	}
	public List<Map<String,Object>> getHistoryFundReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getHistoryFundReport",context.contextMap);
	}
	public List<Map<String,Object>> getHistoryBalanceReport(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getHistoryBalanceReport",context.contextMap);
	}
	
	public Map<String,Object> getCashIncome(Context context) throws Exception {
		return (Map<String,Object>)this.getSqlMapClientTemplate().queryForObject("rentFinance.getCashIncome",context.contextMap);
	}
	
	public List<Map<String,Object>> getCashFlow(Context context) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getCashFlow",context.contextMap);
	}
	
	public List<Map<String,Object>> getSettlementMailContent(Map<String,Object> param) throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementMailContent",param);
	}
	
	public List<Map<String,Object>> getSalesList() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementSalesEmail");
	}
	
	public void updateIncomeInfo(Context context) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateIncomeInfo",context.contextMap);
	}
	
	
	public List<String> getSettlementRecpId() throws Exception {
		return this.getSqlMapClientTemplate().queryForList("rentFinance.getSettlementRecpId");
	}
	
	public void insertSettlementPayment(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("rentFinance.insertSettlementPayment",param);
	}
	
	public void updateSettlementPayment(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().update("rentFinance.updateSettlementPayment",param);
	}
}
