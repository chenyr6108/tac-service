package com.brick.base.to;

import java.sql.Date;

import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class CreditLineTO extends BaseTo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer lineId;
	private Double line = 0D;
	private Integer repeatFlag = 0;	//0不循环，1循环
	private Date startDate;
	private Date endDate;
	private Double lastLine = 0D;
	private Integer status;
	private String hasLine;
	private String lineName;
	private Double usedLine = 0D;
	private Double reusedLine = 0D;
	private Double realUsedLine = 0D;
	
	private String startDateStr;
	private String endDateStr;
	
	public Integer getLineId() {
		return lineId;
	}
	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}
	public Double getLine() {
		return line;
	}
	public void setLine(Double line) {
		this.line = line;
	}
	public Integer getRepeatFlag() {
		return repeatFlag == null ? 0 : repeatFlag;
	}
	public void setRepeatFlag(Integer repeatFlag) {
		this.repeatFlag = repeatFlag;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Double getLastLine() {
		if (this.lastLine == null || this.lastLine == 0) {
			if (this.repeatFlag == 1) {
				this.lastLine = this.line - this.usedLine + this.reusedLine;
			} else {
				this.lastLine = this.line - this.usedLine;
			}
		}
		return lastLine;
	}
	public void setLastLine(Double lastLine) {
		this.lastLine = lastLine;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getHasLine() {
		return hasLine;
	}
	public void setHasLine(String hasLine) {
		this.hasLine = hasLine;
	}
	public String getLineName() {
		return lineName;
	}
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	public Double getUsedLine() {
		return usedLine;
	}
	public void setUsedLine(Double usedLine) {
		this.usedLine = usedLine;
	}
	public Double getReusedLine() {
		return reusedLine;
	}
	public void setReusedLine(Double reusedLine) {
		this.reusedLine = reusedLine;
	}
	public Double getRealUsedLine() {
		if (this.realUsedLine == null || this.realUsedLine == 0) {
			if (this.repeatFlag == 1) {
				this.realUsedLine = this.usedLine - this.reusedLine;
			} else {
				this.realUsedLine = this.usedLine;
			}
		}
		return realUsedLine;
	}
	public void setRealUsedLine(Double realUsedLine) {
		this.realUsedLine = realUsedLine;
	}
	public String getStartDateStr() {
		if (!StringUtils.isEmpty(this.startDateStr)) {
			return startDateStr;
		}
		if (startDate != null) {
			startDateStr = DateUtil.dateToStr(startDate);
		} else {
			startDateStr = "";
		}
		return startDateStr;
	}
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	public String getEndDateStr() {
		if (!StringUtils.isEmpty(this.endDateStr)) {
			return endDateStr;
		}
		if (endDate != null) {
			endDateStr = DateUtil.dateToStr(endDate);
		} else {
			endDateStr = "";
		}
		return endDateStr;
	}
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	
}
