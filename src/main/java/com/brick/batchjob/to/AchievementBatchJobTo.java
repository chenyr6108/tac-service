package com.brick.batchjob.to;

import java.math.BigDecimal;
import java.util.Date;

import com.brick.base.to.BaseTo;

public class AchievementBatchJobTo extends BaseTo {

	private static final long serialVersionUID=1L;

	private String achievementId;//主键
	private String leaseCode;//合同号
	private String creditId;//报告号
	private int userId;//业务员
	private String custName;//客户名称
	private String custType;//客户类型
	private String custFrom;//客户来源
	private String contractType;//合同类型
	private String rectId;
	private double pledgePrice;//保证金
	private double leasePrice;//租金
	private double leasePeriod;//租期
	private BigDecimal payMoney;//已拨款
	private BigDecimal applyMoney;//申请拨款
	private String signContractDateDescr;//合同签订日期
	private Date payDate;//拨款日期
	private String startLeaseDateDescr;//起租日
	private String createOnDescr;//创建时间
	private String flag;//标志栏位  已列印未拨款或者已拨款
	
	public String getAchievementId() {
		return achievementId;
	}
	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	public String getCustFrom() {
		return custFrom;
	}
	public void setCustFrom(String custFrom) {
		this.custFrom = custFrom;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getRectId() {
		return rectId;
	}
	public void setRectId(String rectId) {
		this.rectId = rectId;
	}
	public double getPledgePrice() {
		return pledgePrice;
	}
	public void setPledgePrice(double pledgePrice) {
		this.pledgePrice = pledgePrice;
	}
	public double getLeasePrice() {
		return leasePrice;
	}
	public void setLeasePrice(double leasePrice) {
		this.leasePrice = leasePrice;
	}
	public double getLeasePeriod() {
		return leasePeriod;
	}
	public void setLeasePeriod(double leasePeriod) {
		this.leasePeriod = leasePeriod;
	}
	public BigDecimal getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}
	public BigDecimal getApplyMoney() {
		return applyMoney;
	}
	public void setApplyMoney(BigDecimal applyMoney) {
		this.applyMoney = applyMoney;
	}
	public String getSignContractDateDescr() {
		return signContractDateDescr;
	}
	public void setSignContractDateDescr(String signContractDateDescr) {
		this.signContractDateDescr = signContractDateDescr;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getStartLeaseDateDescr() {
		return startLeaseDateDescr;
	}
	public void setStartLeaseDateDescr(String startLeaseDateDescr) {
		this.startLeaseDateDescr = startLeaseDateDescr;
	}
	public String getCreateOnDescr() {
		return createOnDescr;
	}
	public void setCreateOnDescr(String createOnDescr) {
		this.createOnDescr = createOnDescr;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
