package com.brick.insurance.to;

import java.util.Date;

import com.brick.base.to.BaseTo;
import com.brick.util.StringUtils;

public class EndorsementsTo extends BaseTo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer insuId;
	private String listType;
	private String listTypeDesc;
	private String listCode;
	private Date getTime;
	private Integer status;
	private String remark;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getInsuId() {
		return insuId;
	}
	public void setInsuId(Integer insuId) {
		this.insuId = insuId;
	}
	public String getListType() {
		return listType;
	}
	public void setListType(String listType) {
		this.listType = listType;
	}
	public String getListCode() {
		return listCode;
	}
	public void setListCode(String listCode) {
		this.listCode = listCode;
	}
	public Date getGetTime() {
		return getTime;
	}
	public void setGetTime(Date getTime) {
		this.getTime = getTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getListTypeDesc() {
		return listTypeDesc;
	}
	public void setListTypeDesc(String listTypeDesc) {
		this.listTypeDesc = listTypeDesc;
	}
	
}
