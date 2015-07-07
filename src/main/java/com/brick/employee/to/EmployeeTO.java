package com.brick.employee.to;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTO {
	
	private int id;
	private int status;
	private String name;
	private String jobDescr;
	private String upName;
	private String deptName;
	private String sex;
	private String code;
	private String loginTimeDescr;
	private String ip;
	private String email;
	private Date createDate;
	private String mobile;
	private String password;
	private String upperId;
	private String node;
	private String jobCode;
	private String deptId;
	private String companyId;
	private String companyName;
	private String telephone;
	private String telephone1;
	private String telephone2;
	private String newDeptName;
	private Integer deptLeader;	
	private String deptLeaderName;
	private Integer newCompanyId;
	private Integer department;
	private List<EmployeeTO> deptCmpyList=new ArrayList<EmployeeTO>();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUpName() {
		return upName;
	}
	public void setUpName(String upName) {
		this.upName = upName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJobDescr() {
		return jobDescr;
	}
	public void setJobDescr(String jobDescr) {
		this.jobDescr = jobDescr;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLoginTimeDescr() {
		return loginTimeDescr;
	}
	public void setLoginTimeDescr(String loginTimeDescr) {
		this.loginTimeDescr = loginTimeDescr;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUpperId() {
		return upperId;
	}
	public void setUpperId(String upperId) {
		this.upperId = upperId;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getJobCode() {
		return jobCode;
	}
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public List<EmployeeTO> getDeptCmpyList() {
		return deptCmpyList;
	}
	public void setDeptCmpyList(List<EmployeeTO> deptCmpyList) {
		this.deptCmpyList = deptCmpyList;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getTelephone1() {
		/*if(this.telephone!=null&&!"".equals(this.telephone)) {
			this.telephone1 = this.telephone.split("-")[0];
		} else {
			this.telephone1 = "";
		}*/
		return telephone;
	}
	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}
	public String getTelephone2() {
		/*if(this.telephone!=null&&!"".equals(this.telephone)) {
			this.telephone2 = this.telephone.split("-")[1];
		} else {
			this.telephone2 = "";
		}*/
		return telephone;
	}
	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}
	public String getNewDeptName() {
		return newDeptName;
	}
	public void setNewDeptName(String newDeptName) {
		this.newDeptName = newDeptName;
	}
	public String getDeptLeaderName() {
		return deptLeaderName;
	}
	public void setDeptLeaderName(String deptLeaderName) {
		this.deptLeaderName = deptLeaderName;
	}
	public Integer getDeptLeader() {
		return deptLeader;
	}
	public void setDeptLeader(Integer deptLeader) {
		this.deptLeader = deptLeader;
	}
	public Integer getNewCompanyId() {
		return newCompanyId;
	}
	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}
	public Integer getDepartment() {
		return department;
	}
	public void setDepartment(Integer department) {
		this.department = department;
	}
	
	
}
