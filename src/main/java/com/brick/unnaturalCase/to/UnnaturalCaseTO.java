package com.brick.unnaturalCase.to;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnnaturalCaseTO {
	
	//案件进度异常 job
	private String id;//主键
	private String deptId;//部门号
	private String deptName;//部门名称
	private int amount;
	private int A_B=0;//进件~访厂
	private int B_C=0;//访厂~初次提交
	private int C_D=0;//初次提交风控~最终提交风控
	private int D_E=0;//最终提交风控~审查核准
	private int E_F=0;//审查核准~业管初审
	private int F_G=0;//业管初审~拨款
	private int A_G=0;//进件~拨款
	private String order;
	private Date createOn;
	private String flag;
	private int num;
	private String visitArea;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Date getCreateOn() {
		return createOn;
	}
	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getA_B() {
		return A_B;
	}
	public void setA_B(int a_B) {
		A_B = a_B;
	}
	public int getB_C() {
		return B_C;
	}
	public void setB_C(int b_C) {
		B_C = b_C;
	}
	public int getC_D() {
		return C_D;
	}
	public void setC_D(int c_D) {
		C_D = c_D;
	}
	public int getD_E() {
		return D_E;
	}
	public void setD_E(int d_E) {
		D_E = d_E;
	}
	public int getE_F() {
		return E_F;
	}
	public void setE_F(int e_F) {
		E_F = e_F;
	}
	public int getF_G() {
		return F_G;
	}
	public void setF_G(int f_G) {
		F_G = f_G;
	}
	public int getA_G() {
		return A_G;
	}
	public void setA_G(int a_G) {
		A_G = a_G;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getVisitArea() {
		return visitArea;
	}
	public void setVisitArea(String visitArea) {
		this.visitArea = visitArea;
	}

	//逾期25天,前3期逾期 未回访job
	private String creditId;
	private String custId;
	private String custName;
	private int userId;
	private String name;
	private String upName;
	private int dunDay;
	private String rectId;
	private String recpId;
	private Date shouldPayDate;
	private String suplTrue;
	private double amerce;
	private double dunTotalPrice;
	private String lockCode;
	private String suplName;
	private String isUnnaturalCase;
	private String recpCode;
	private List<Map<String,String>> suplList=new ArrayList<Map<String,String>>();
	private String creditRunCode;
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
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUpName() {
		return upName;
	}
	public void setUpName(String upName) {
		this.upName = upName;
	}
	public int getDunDay() {
		return dunDay;
	}
	public void setDunDay(int dunDay) {
		this.dunDay = dunDay;
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
	public Date getShouldPayDate() {
		return shouldPayDate;
	}
	public void setShouldPayDate(Date shouldPayDate) {
		this.shouldPayDate = shouldPayDate;
	}
	public String getSuplTrue() {
		return suplTrue;
	}
	public void setSuplTrue(String suplTrue) {
		this.suplTrue = suplTrue;
	}
	public double getAmerce() {
		return amerce;
	}
	public void setAmerce(double amerce) {
		this.amerce = amerce;
	}
	public double getDunTotalPrice() {
		return dunTotalPrice;
	}
	public void setDunTotalPrice(double dunTotalPrice) {
		this.dunTotalPrice = dunTotalPrice;
	}
	public String getLockCode() {
		return lockCode==null?"":lockCode;
	}
	public void setLockCode(String lockCode) {
		this.lockCode = lockCode;
	}
	public String getSuplName() {
		return suplName==null?"":suplName;
	}
	public void setSuplName(String suplName) {
		this.suplName = suplName;
	}
	public String getIsUnnaturalCase() {
		return isUnnaturalCase;
	}
	public void setIsUnnaturalCase(String isUnnaturalCase) {
		this.isUnnaturalCase = isUnnaturalCase;
	}
	public String getRecpCode() {
		return recpCode;
	}
	public void setRecpCode(String recpCode) {
		this.recpCode = recpCode;
	}
	public List<Map<String,String>> getSuplList() {
		return suplList;
	}
	public void setSuplList(List<Map<String,String>> suplList) {
		this.suplList = suplList;
	}
	public String getCreditRunCode() {
		return creditRunCode;
	}
	public void setCreditRunCode(String creditRunCode) {
		this.creditRunCode = creditRunCode;
	}
	
	//拨款后待补文件batch job
	private String leaseCode;
	private String fileName;
	private String fileRemark;
	private String issueReason;
	private Date financeDate;
	private String shouldFinishDate;
	private String type;
	private String delayDay;

	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRemark() {
		return fileRemark;
	}
	public void setFileRemark(String fileRemark) {
		this.fileRemark = fileRemark;
	}
	public String getIssueReason() {
		return issueReason;
	}
	public void setIssueReason(String issueReason) {
		this.issueReason = issueReason;
	}
	public Date getFinanceDate() {
		return financeDate;
	}
	public void setFinanceDate(Date financeDate) {
		this.financeDate = financeDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDelayDay() {
		return delayDay;
	}
	public void setDelayDay(String delayDay) {
		this.delayDay = delayDay;
	}
	public String getShouldFinishDate() {
		return shouldFinishDate;
	}
	public void setShouldFinishDate(String shouldFinishDate) {
		this.shouldFinishDate = shouldFinishDate;
	}
	
	//出险逾期60天未理赔结案batch job
	private String insuranceCode;
	private String eqmtDescr;
	private String insuranceName;
	private String recordRemark;
	private int dayDiff;
	private Date startDate;
	private Date dealDate;

	public String getInsuranceCode() {
		return insuranceCode;
	}
	public void setInsuranceCode(String insuranceCode) {
		this.insuranceCode = insuranceCode;
	}
	public String getEqmtDescr() {
		return eqmtDescr;
	}
	public void setEqmtDescr(String eqmtDescr) {
		this.eqmtDescr = eqmtDescr;
	}
	public String getInsuranceName() {
		return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}
	public String getRecordRemark() {
		return recordRemark;
	}
	public void setRecordRemark(String recordRemark) {
		this.recordRemark = recordRemark;
	}
	public int getDayDiff() {
		return dayDiff;
	}
	public void setDayDiff(int dayDiff) {
		this.dayDiff = dayDiff;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getDealDate() {
		return dealDate;
	}
	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}
	
	private Date minCommitDate;
	private double leaseRze;
	private String isBack;
	private Date visitDate;
	private String state;

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getMinCommitDate() {
		return minCommitDate;
	}
	public void setMinCommitDate(Date minCommitDate) {
		this.minCommitDate = minCommitDate;
	}
	public double getLeaseRze() {
		return leaseRze;
	}
	public void setLeaseRze(double leaseRze) {
		this.leaseRze = leaseRze;
	}
	public String getIsBack() {
		return isBack;
	}
	public void setIsBack(String isBack) {
		this.isBack = isBack;
	}
	public Date getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}
	
	private int a;
	private int b;
	private int c;
	private int d;
	private int e;
	private int f;
	private int g;
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	public int getD() {
		return d;
	}
	public void setD(int d) {
		this.d = d;
	}
	public int getE() {
		return e;
	}
	public void setE(int e) {
		this.e = e;
	}
	public int getF() {
		return f;
	}
	public void setF(int f) {
		this.f = f;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
}
