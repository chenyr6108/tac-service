package com.brick.rent.to;

import java.io.Serializable;
import java.util.Date;

/**
 * 结清审批日志
 * @author yangliu
 *
 */
public class SettlementLogTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键ID
	private Integer id;
	//结清审批信息主键ID
	private Integer settlementId;
	//结清审批状态码
	private Integer stateCode;
	//操作人ID
	private Integer opUserId;
	//操作时间
	private Date opTime;
	//操作内容
	private String opMsg;
	//操作状态(1：通过，0：驳回）
	private Integer opState;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSettlementId() {
		return settlementId;
	}
	public void setSettlementId(Integer settlementId) {
		this.settlementId = settlementId;
	}
	public Integer getStateCode() {
		return stateCode;
	}
	public void setStateCode(Integer stateCode) {
		this.stateCode = stateCode;
	}
	public Integer getOpUserId() {
		return opUserId;
	}
	public void setOpUserId(Integer opUserId) {
		this.opUserId = opUserId;
	}
	public Date getOpTime() {
		return opTime;
	}
	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}
	public String getOpMsg() {
		return opMsg;
	}
	public void setOpMsg(String opMsg) {
		this.opMsg = opMsg;
	}
	public Integer getOpState() {
		return opState;
	}
	public void setOpState(Integer opState) {
		this.opState = opState;
	}
	public SettlementLogTO(){}
	public SettlementLogTO(Integer id, Integer settlementId, Integer stateCode,
			Integer opUserId, Date opTime, String opMsg, Integer opState) {
		super();
		this.id = id;
		this.settlementId = settlementId;
		this.stateCode = stateCode;
		this.opUserId = opUserId;
		this.opTime = opTime;
		this.opMsg = opMsg;
		this.opState = opState;
	}
	
}
