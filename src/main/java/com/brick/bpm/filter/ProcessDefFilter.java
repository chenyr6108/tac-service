package com.brick.bpm.filter;

import java.io.Serializable;

/**
 * 流程定义查询条件
 * @author zhangyizhou
 *
 */
public class ProcessDefFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 流程定义ID
	 */
	private String processDefId;

	public String getProcessDefId() {
		return processDefId;
	}

	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}

}
