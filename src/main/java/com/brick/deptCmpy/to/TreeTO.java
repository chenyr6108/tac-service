package com.brick.deptCmpy.to;

import java.util.ArrayList;
import java.util.List;

public class TreeTO {

	private String deptId;
	private String upperDeptId;
	private String deptName;
	private String companyName;
	private List<TreeTO> deptList=new ArrayList<TreeTO>();
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
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public List<TreeTO> getDeptList() {
		return deptList;
	}
	public void setDeptList(List<TreeTO> deptList) {
		this.deptList = deptList;
	}
	public String getUpperDeptId() {
		return upperDeptId;
	}
	public void setUpperDeptId(String upperDeptId) {
		this.upperDeptId = upperDeptId;
	}
	
}
