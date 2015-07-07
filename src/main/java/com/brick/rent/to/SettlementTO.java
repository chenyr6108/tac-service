package com.brick.rent.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 结清审批信息
 * @author yangliu
 *
 */
public class SettlementTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键ID
	private Integer id;
	private Integer recpId;
	//结清单状态（审批中，通过，驳回）
	private Integer state;
	//修改人ID
	private Integer modifyBy;
	//修改时间
	private Date modifyTime;
	//申请备注
	private String applyRemark;
	//申请人ID
	private Integer applyUserId;
	//申请时间
	private Date applyTime;
	//结清审批状态码（保存于数据字典中）
	private Integer stateCode;
	//实际付金额
	private Double totalPayPrice;
	//总计应付金额
	private Double totalPrice;
	//减免金额
	private Double reductionPrice;
	//当前处理人id
	private Integer currentUserId;
	//当前处理人name
	private String currentUserName;
	//处理人主管id
	private Integer upUserId;
	//处理人主管name
	private String upUserName;
	//部门id
	private Integer department;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRecpId() {
		return recpId;
	}
	public void setRecpId(Integer recpId) {
		this.recpId = recpId;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getModifyBy() {
		return modifyBy;
	}
	public void setModifyBy(Integer modifyBy) {
		this.modifyBy = modifyBy;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getApplyRemark() {
		return applyRemark;
	}
	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}
	public Integer getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}
	public Date getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	public Integer getStateCode() {
		return stateCode;
	}
	public void setStateCode(Integer stateCode) {
		this.stateCode = stateCode;
	}
	public Double getTotalPayPrice() {
		return totalPayPrice;
	}
	public void setTotalPayPrice(Double totalPayPrice) {
		this.totalPayPrice = totalPayPrice;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Double getReductionPrice() {
		return reductionPrice;
	}
	public void setReductionPrice(Double reductionPrice) {
		this.reductionPrice = reductionPrice;
	}
	public Integer getCurrentUserId() {
		return currentUserId;
	}
	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}
	public String getCurrentUserName() {
		return currentUserName;
	}
	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}
	public Integer getUpUserId() {
		return upUserId;
	}
	public void setUpUserId(Integer upUserId) {
		this.upUserId = upUserId;
	}
	public String getUpUserName() {
		return upUserName;
	}
	public void setUpUserName(String upUserName) {
		this.upUserName = upUserName;
	}
	public Integer getDepartment() {
		return department;
	}
	public void setDepartment(Integer department) {
		this.department = department;
	}
	public SettlementTO(){}
	public SettlementTO(Integer id, Integer recpId, Integer state,
			Integer modifyBy, Date modifyTime, String applyRemark,
			Integer applyUserId, Date applyTime, Integer stateCode,
			Double totalPayPrice, Double totalPrice, Double reductionPrice,
			Integer currentUserId, String currentUserName, Integer upUserId,
			String upUserName, Integer department) {
		super();
		this.id = id;
		this.recpId = recpId;
		this.state = state;
		this.modifyBy = modifyBy;
		this.modifyTime = modifyTime;
		this.applyRemark = applyRemark;
		this.applyUserId = applyUserId;
		this.applyTime = applyTime;
		this.stateCode = stateCode;
		this.totalPayPrice = totalPayPrice;
		this.totalPrice = totalPrice;
		this.reductionPrice = reductionPrice;
		this.currentUserId = currentUserId;
		this.currentUserName = currentUserName;
		this.upUserId = upUserId;
		this.upUserName = upUserName;
		this.department = department;
	}
}
