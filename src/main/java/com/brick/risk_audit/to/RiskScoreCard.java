package com.brick.risk_audit.to;

import java.sql.Date;

public class RiskScoreCard {
	
	private String riskId;
	private String selectedScoreCard;
	private String allScoreCard;
	private Integer score;
	private Date scoreDate;
	public String getRiskId() {
		return riskId;
	}
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}
	public String getSelectedScoreCard() {
		return selectedScoreCard;
	}
	public void setSelectedScoreCard(String selectedScoreCard) {
		this.selectedScoreCard = selectedScoreCard;
	}
	public String getAllScoreCard() {
		return allScoreCard;
	}
	public void setAllScoreCard(String allScoreCard) {
		this.allScoreCard = allScoreCard;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public Date getScoreDate() {
		return scoreDate;
	}
	public void setScoreDate(Date scoreDate) {
		this.scoreDate = scoreDate;
	}
}
