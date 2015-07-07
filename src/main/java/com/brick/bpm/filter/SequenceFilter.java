package com.brick.bpm.filter;

public class SequenceFilter extends ProcessFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer sequenceId;
	
	private String sequenceDefId;
	
	private Integer sourceId;

	private Integer targetId;
	
	public Integer getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getSequenceDefId() {
		return sequenceDefId;
	}

	public void setSequenceDefId(String sequenceDefId) {
		this.sequenceDefId = sequenceDefId;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

}
