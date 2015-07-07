package com.brick.batchjob.to;

import java.util.Date;

import com.brick.base.to.BaseTo;

public class CaseCompareDayTo extends BaseTo {

	private static final long serialVersionUID=1L;
	
	private String caseCompareDayId;//主键
	private String creditId;//报告号
	private String leaseCode;//合同号
	private String custName;//客户名称
	private String userId;
	private String deptId;
	private String userName;//客户经理
	private String deptName;//办事处
	private double a_f;
	private Date payDate;//拨款时间(A)
	private double a_b;
	private Date auditDate;//业管初审时间(B)
	private double b_c;
	private Date confirmDate;//审查核准时间(C)
	private Date lastRiskDate;//最终提交风控时间(D)
	private Date firstRiskDate;//初次提交风控时间(E)
	private double c_d;
	private double d_e;
	private double e_f;
	private Date creditCreateDate;//报告生成时间(F)
	private Date createOn;//batch job跑的时间
	private String createOnCode;
	private String createOnDescr;
	private String countCase;
	private double c_e;
	private String c_eType;
	private double c_e_1;//桌面的一次过案是指
	private double c_e_2;//桌面的非一次过案是指
	private double e_f_sum;
	private double b_c_sum;
	private double a_b_sum;
	private double f_g_sum;
	private Date visitDate;//访厂时间
	private double f_g;
	private double a_g;
	private double a_g_1;
	private double a_g_2;
	public double getE_f_sum() {
		return e_f_sum;
	}
	public void setE_f_sum(double e_f_sum) {
		this.e_f_sum = e_f_sum;
	}
	public double getB_c_sum() {
		return b_c_sum;
	}
	public void setB_c_sum(double b_c_sum) {
		this.b_c_sum = b_c_sum;
	}
	public double getA_b_sum() {
		return a_b_sum;
	}
	public void setA_b_sum(double a_b_sum) {
		this.a_b_sum = a_b_sum;
	}
	public String getCaseCompareDayId() {
		return caseCompareDayId;
	}
	public void setCaseCompareDayId(String caseCompareDayId) {
		this.caseCompareDayId = caseCompareDayId;
	}
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getLeaseCode() {
		return leaseCode;
	}
	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public double getA_f() {
		return a_f;
	}
	public void setA_f(double a_f) {
		this.a_f = a_f;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public double getA_b() {
		return a_b;
	}
	public void setA_b(double a_b) {
		this.a_b = a_b;
	}
	public Date getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}
	public double getB_c() {
		return b_c;
	}
	public void setB_c(double b_c) {
		this.b_c = b_c;
	}
	public Date getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Date getLastRiskDate() {
		return lastRiskDate;
	}
	public void setLastRiskDate(Date lastRiskDate) {
		this.lastRiskDate = lastRiskDate;
	}
	public Date getFirstRiskDate() {
		return firstRiskDate;
	}
	public void setFirstRiskDate(Date firstRiskDate) {
		this.firstRiskDate = firstRiskDate;
	}
	public double getD_e() {
		return d_e;
	}
	public void setD_e(double d_e) {
		this.d_e = d_e;
	}
	public double getE_f() {
		return e_f;
	}
	public void setE_f(double e_f) {
		this.e_f = e_f;
	}
	public Date getCreditCreateDate() {
		return creditCreateDate;
	}
	public void setCreditCreateDate(Date creditCreateDate) {
		this.creditCreateDate = creditCreateDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
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
	public double getC_d() {
		return c_d;
	}
	public void setC_d(double c_d) {
		this.c_d = c_d;
	}
	public String getCountCase() {
		return countCase;
	}
	public void setCountCase(String countCase) {
		this.countCase = countCase;
	}
	public double getC_e() {
		return c_e;
	}
	public void setC_e(double c_e) {
		this.c_e = c_e;
	}
	public String getC_eType() {
		return c_eType;
	}
	public void setC_eType(String c_eType) {
		this.c_eType = c_eType;
	}
	public double getC_e_1() {
		return c_e_1;
	}
	public void setC_e_1(double c_e_1) {
		this.c_e_1 = c_e_1;
	}
	public double getC_e_2() {
		return c_e_2;
	}
	public void setC_e_2(double c_e_2) {
		this.c_e_2 = c_e_2;
	}
	public Date getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}
	public double getF_g() {
		return f_g;
	}
	public void setF_g(double f_g) {
		this.f_g = f_g;
	}
	public double getA_g() {
		return a_g;
	}
	public void setA_g(double a_g) {
		this.a_g = a_g;
	}
	public double getA_g_1() {
		return a_g_1;
	}
	public void setA_g_1(double a_g_1) {
		this.a_g_1 = a_g_1;
	}
	public double getA_g_2() {
		return a_g_2;
	}
	public void setA_g_2(double a_g_2) {
		this.a_g_2 = a_g_2;
	}
	public double getF_g_sum() {
		return f_g_sum;
	}
	public void setF_g_sum(double f_g_sum) {
		this.f_g_sum = f_g_sum;
	}
	public String getCreateOnCode() {
		return createOnCode;
	}
	public void setCreateOnCode(String createOnCode) {
		this.createOnCode = createOnCode;
	}
	
}
