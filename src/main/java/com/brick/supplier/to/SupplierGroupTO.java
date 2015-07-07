package com.brick.supplier.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class SupplierGroupTO extends BaseTo {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String suplGroupCode;
	private String suplGroupName;
	private int suplCount;
	private String remark;
	private String suplId;
	private String suplName;
	
	private double leaseRze;
	private double restAmount;
	private String creditId;
	private String creditRunCode;
	private String flag;
	
	//获得detail
	private String signNum;
	private double union;
	private String unionLoopFlag;
	private double buyBack;
	private String buyBackLoopFlag;
	private double payBefore;
	private double invoice;
	private String payBeforeLoopFlag;
	private Date fromDate;
	private Date toDate;
	private String fromDateDescr;
	private String toDateDescr;
	private String s_employeeId;
	private Date createOn;
	private String createOnDescr;
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSuplGroupCode() {
		return suplGroupCode;
	}
	public void setSuplGroupCode(String suplGroupCode) {
		this.suplGroupCode = suplGroupCode;
	}
	public String getSuplGroupName() {
		return suplGroupName;
	}
	public void setSuplGroupName(String suplGroupName) {
		this.suplGroupName = suplGroupName;
	}
	public int getSuplCount() {
		return suplCount;
	}
	public void setSuplCount(int suplCount) {
		this.suplCount = suplCount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSuplId() {
		return suplId;
	}
	public void setSuplId(String suplId) {
		this.suplId = suplId;
	}
	public String getSuplName() {
		return suplName;
	}
	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}
	public String getSignNum() {
		return signNum;
	}
	public void setSignNum(String signNum) {
		this.signNum = signNum;
	}
	public double getRestAmount() {
		return restAmount;
	}
	public void setRestAmount(double restAmount) {
		this.restAmount = restAmount;
	}
	public double getLeaseRze() {
		return leaseRze;
	}
	public void setLeaseRze(double leaseRze) {
		this.leaseRze = leaseRze;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getCreditRunCode() {
		return creditRunCode;
	}
	public void setCreditRunCode(String creditRunCode) {
		this.creditRunCode = creditRunCode;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public double getUnion() {
		return union;
	}
	public void setUnion(double union) {
		this.union = union;
	}
	public String getUnionLoopFlag() {
		return unionLoopFlag;
	}
	public void setUnionLoopFlag(String unionLoopFlag) {
		this.unionLoopFlag = unionLoopFlag;
	}
	public double getBuyBack() {
		return buyBack;
	}
	public void setBuyBack(double buyBack) {
		this.buyBack = buyBack;
	}
	public String getBuyBackLoopFlag() {
		return buyBackLoopFlag;
	}
	public void setBuyBackLoopFlag(String buyBackLoopFlag) {
		this.buyBackLoopFlag = buyBackLoopFlag;
	}
	public double getPayBefore() {
		return payBefore;
	}
	public void setPayBefore(double payBefore) {
		this.payBefore = payBefore;
	}
	public String getPayBeforeLoopFlag() {
		return payBeforeLoopFlag;
	}
	public void setPayBeforeLoopFlag(String payBeforeLoopFlag) {
		this.payBeforeLoopFlag = payBeforeLoopFlag;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getFromDateDescr() {
		return fromDateDescr;
	}
	public void setFromDateDescr(String fromDateDescr) {
		this.fromDateDescr = fromDateDescr;
	}
	public String getToDateDescr() {
		return toDateDescr;
	}
	public void setToDateDescr(String toDateDescr) {
		this.toDateDescr = toDateDescr;
	}
	public String getS_employeeId() {
		return s_employeeId;
	}
	public void setS_employeeId(String s_employeeId) {
		this.s_employeeId = s_employeeId;
	}
	public Date getCreateOn() {
		return createOn;
	}
	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}
	public String getCreateOnDescr() {
		return createOnDescr;
	}
	public void setCreateOnDescr(String createOnDescr) {
		this.createOnDescr = createOnDescr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getInvoice() {
		return invoice;
	}
	public void setInvoice(double invoice) {
		this.invoice = invoice;
	}
}
