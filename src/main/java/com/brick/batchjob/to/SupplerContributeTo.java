package com.brick.batchjob.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class SupplerContributeTo extends BaseTo {

	private static final long serialVersionUID = 1L;

	private String supplerContributeId;
	private String suplId;
	private String suplName;
	private String leaseCode;
	private double unitPrice;
	private double payMoney;
	private int leaseCount;
	private int equipmentCount;
	private String accrual;
	private String tr;
	private double prorate=1;
	private double grantPrice;
	private double restGrantPrice;
	private String creditId;
	private String custId;
	private String custName;
	private String rectId;
	private String recpId;
	private double restMoney;
	private String restPeriod;
	private int dunCountBySupl;//逾期15天以上(含15天)合同个数
	private int dunCountByLease;
	private String recpCode;
	private String status;
	private double percent;
	//20121228 zhangbo add
	private double trrAte;
	private Date firstPaydate;
	
	private int line;
	private int total_qty;
	private int left_qty;
	
	
	public String getSupplerContributeId() {
		return supplerContributeId;
	}
	public void setSupplerContributeId(String supplerContributeId) {
		this.supplerContributeId = supplerContributeId;
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
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public int getLeaseCount() {
		return leaseCount;
	}
	public void setLeaseCount(int leaseCount) {
		this.leaseCount = leaseCount;
	}
	public int getEquipmentCount() {
		return equipmentCount;
	}
	public void setEquipmentCount(int equipmentCount) {
		this.equipmentCount = equipmentCount;
	}
	public String getAccrual() {
		return accrual;
	}
	public void setAccrual(String accrual) {
		this.accrual = accrual;
	}
	public String getTr() {
		return tr;
	}
	public void setTr(String tr) {
		this.tr = tr;
	}
	public double getProrate() {
		return prorate;
	}
	public void setProrate(double prorate) {
		this.prorate = prorate;
	}
	public double getGrantPrice() {
		return grantPrice;
	}
	public void setGrantPrice(double grantPrice) {
		this.grantPrice = grantPrice;
	}
	public double getRestGrantPrice() {
		return restGrantPrice;
	}
	public void setRestGrantPrice(double restGrantPrice) {
		this.restGrantPrice = restGrantPrice;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getRectId() {
		return rectId;
	}
	public void setRectId(String rectId) {
		this.rectId = rectId;
	}
	public String getRecpId() {
		return recpId;
	}
	public void setRecpId(String recpId) {
		this.recpId = recpId;
	}
	public double getRestMoney() {
		return restMoney;
	}
	public void setRestMoney(double restMoney) {
		this.restMoney = restMoney;
	}
	public String getRestPeriod() {
		return restPeriod;
	}
	public void setRestPeriod(String restPeriod) {
		this.restPeriod = restPeriod;
	}
	public int getDunCountBySupl() {
		return dunCountBySupl;
	}
	public void setDunCountBySupl(int dunCountBySupl) {
		this.dunCountBySupl = dunCountBySupl;
	}
	public int getDunCountByLease() {
		return dunCountByLease;
	}
	public void setDunCountByLease(int dunCountByLease) {
		this.dunCountByLease = dunCountByLease;
	}
	public String getRecpCode() {
		return recpCode;
	}
	public void setRecpCode(String recpCode) {
		this.recpCode = recpCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public double getTrrAte() {
		return trrAte;
	}
	public void setTrrAte(double trrAte) {
		this.trrAte = trrAte;
	}

	public Date getFirstPaydate() {
		return firstPaydate;
	}
	public void setFirstPaydate(Date firstPaydate) {
		this.firstPaydate = firstPaydate;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getTotal_qty() {
		return total_qty;
	}
	public void setTotal_qty(int total_qty) {
		this.total_qty = total_qty;
	}
	public int getLeft_qty() {
		return left_qty;
	}
	public void setLeft_qty(int left_qty) {
		this.left_qty = left_qty;
	}
	
	
}
