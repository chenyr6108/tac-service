package com.brick.auditReport.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;

public class AuditReportTo extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer auditReportId;
	private Date date;
	private String dateStr;
	private String dayType;
	private Integer decpId;
	private String decpName;
	private Integer visitDaily;
	private Integer visitTotal;
	private Integer commitDaily;
	private Integer commitTotal;
	private Integer commitProDaily;
	private Integer commitProTotal;
	private Integer approvalDaily;
	private Integer returnDaily;
	private Integer rejectDaily;
	private Integer unaudited;
	private Double finishPercent;
	private Double approvedPercent;
	private Double finishPercentPro;
	private Double approvedPercentPro;
	private Integer sumApproved;
	private Integer sumReturn;
	private Integer sumReject;
	private String auditMonth;
	private String auditYear;
	private String start_date;
	private String end_date;
	
	private String apply_count;
	private String none_count;
	private String rj_count;
	private String apply_count_total;
	private String none_count_total;
	private String rj_count_total;
	
	public Integer getAuditReportId() {
		return auditReportId;
	}
	public void setAuditReportId(Integer auditReportId) {
		this.auditReportId = auditReportId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public String getDayType() {
		return dayType;
	}
	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
	public Integer getDecpId() {
		return decpId;
	}
	public void setDecpId(Integer decpId) {
		this.decpId = decpId;
	}
	public String getDecpName() {
		return decpName;
	}
	public void setDecpName(String decpName) {
		this.decpName = decpName;
	}
	public Integer getVisitDaily() {
		return visitDaily;
	}
	public void setVisitDaily(Integer visitDaily) {
		this.visitDaily = visitDaily;
	}
	public Integer getVisitTotal() {
		return visitTotal;
	}
	public void setVisitTotal(Integer visitTotal) {
		this.visitTotal = visitTotal;
	}
	public Integer getCommitDaily() {
		return commitDaily;
	}
	public void setCommitDaily(Integer commitDaily) {
		this.commitDaily = commitDaily;
	}
	public Integer getCommitTotal() {
		return commitTotal;
	}
	public void setCommitTotal(Integer commitTotal) {
		this.commitTotal = commitTotal;
	}
	public Integer getApprovalDaily() {
		return approvalDaily;
	}
	public void setApprovalDaily(Integer approvalDaily) {
		this.approvalDaily = approvalDaily;
	}
	public Integer getReturnDaily() {
		return returnDaily;
	}
	public void setReturnDaily(Integer returnDaily) {
		this.returnDaily = returnDaily;
	}
	public Integer getRejectDaily() {
		return rejectDaily;
	}
	public void setRejectDaily(Integer rejectDaily) {
		this.rejectDaily = rejectDaily;
	}
	public Integer getUnaudited() {
		return unaudited;
	}
	public void setUnaudited(Integer unaudited) {
		this.unaudited = unaudited;
	}
	public Double getFinishPercent() {
		return finishPercent;
	}
	public void setFinishPercent(Double finishPercent) {
		this.finishPercent = finishPercent;
	}
	public Double getApprovedPercent() {
		return approvedPercent;
	}
	public void setApprovedPercent(Double approvedPercent) {
		this.approvedPercent = approvedPercent;
	}
	public Integer getSumApproved() {
		return sumApproved;
	}
	public void setSumApproved(Integer sumApproved) {
		this.sumApproved = sumApproved;
	}
	public Integer getSumReturn() {
		return sumReturn;
	}
	public void setSumReturn(Integer sumReturn) {
		this.sumReturn = sumReturn;
	}
	public Integer getSumReject() {
		return sumReject;
	}
	public void setSumReject(Integer sumReject) {
		this.sumReject = sumReject;
	}
	public String getAuditMonth() {
		return auditMonth;
	}
	public void setAuditMonth(String auditMonth) {
		this.auditMonth = auditMonth;
	}
	public String getAuditYear() {
		return auditYear;
	}
	public void setAuditYear(String auditYear) {
		this.auditYear = auditYear;
	}
	public Integer getCommitProDaily() {
		return commitProDaily;
	}
	public void setCommitProDaily(Integer commitProDaily) {
		this.commitProDaily = commitProDaily;
	}
	public Integer getCommitProTotal() {
		return commitProTotal;
	}
	public void setCommitProTotal(Integer commitProTotal) {
		this.commitProTotal = commitProTotal;
	}
	public Double getFinishPercentPro() {
		return finishPercentPro;
	}
	public void setFinishPercentPro(Double finishPercentPro) {
		this.finishPercentPro = finishPercentPro;
	}
	public Double getApprovedPercentPro() {
		return approvedPercentPro;
	}
	public void setApprovedPercentPro(Double approvedPercentPro) {
		this.approvedPercentPro = approvedPercentPro;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getApply_count() {
		return apply_count;
	}
	public void setApply_count(String apply_count) {
		this.apply_count = apply_count;
	}
	public String getNone_count() {
		return none_count;
	}
	public void setNone_count(String none_count) {
		this.none_count = none_count;
	}
	public String getRj_count() {
		return rj_count;
	}
	public void setRj_count(String rj_count) {
		this.rj_count = rj_count;
	}
	public String getApply_count_total() {
		return apply_count_total;
	}
	public void setApply_count_total(String apply_count_total) {
		this.apply_count_total = apply_count_total;
	}
	public String getNone_count_total() {
		return none_count_total;
	}
	public void setNone_count_total(String none_count_total) {
		this.none_count_total = none_count_total;
	}
	public String getRj_count_total() {
		return rj_count_total;
	}
	public void setRj_count_total(String rj_count_total) {
		this.rj_count_total = rj_count_total;
	}
}
