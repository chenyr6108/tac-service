package com.tac.dept.to;

import java.util.Date;

import com.brick.base.to.BaseTo;

public class DeptTo extends BaseTo{

	private static final long serialVersionUID = 1L;
		
	private Integer id;
	
	private Integer status;
	
	private String name;
	
	private Integer deptLeader;

	private Integer orderNo;

	private Integer parentId;

	private Integer companyId;

	private String deptLeaderName;
	
	private Integer deptLevel;
	
	private String deptLevelName;
	
	private Date modifyDate;
	
	private Date createDate;
	
	private Integer createBy;
	
	private Integer modifyBy;
	
	private String companyName;
	
	private String parentName;

	private boolean open = true;//for dept tree
	
	private int deptOrder;//for dept tree
	
	private String displayName;//for dept tree

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDeptLeader() {
		return deptLeader;
	}

	public void setDeptLeader(Integer deptLeader) {
		this.deptLeader = deptLeader;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getDeptLeaderName() {
		return deptLeaderName;
	}

	public void setDeptLeaderName(String deptLeaderName) {
		this.deptLeaderName = deptLeaderName;
	}

	

	public Integer getDeptLevel() {
		return deptLevel;
	}

	public void setDeptLevel(Integer deptLevel) {
		this.deptLevel = deptLevel;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Integer getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(Integer modifyBy) {
		this.modifyBy = modifyBy;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getDeptOrder() {
		return deptOrder;
	}

	public void setDeptOrder(int deptOrder) {
		this.deptOrder = deptOrder;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDeptLevelName() {
		return deptLevelName;
	}

	public void setDeptLevelName(String deptLevelName) {
		this.deptLevelName = deptLevelName;
	}
	
	
}
