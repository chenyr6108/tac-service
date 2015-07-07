package com.brick.aprv.filter;

import java.io.Serializable;

public class ApprovalFilter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer aprvId;

	private String leaseCode;
	
	public Integer getAprvId() {
		return aprvId;
	}

	public void setAprvId(Integer aprvId) {
		this.aprvId = aprvId;
	}

	public String getLeaseCode() {
		return leaseCode;
	}

	public void setLeaseCode(String leaseCode) {
		this.leaseCode = leaseCode;
	}

}
