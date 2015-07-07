package com.brick.risk_audit.to;

import java.util.Date;

import com.brick.base.to.BaseTo;

public class ScoreCardTO extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer subject_id;
	private Integer option_id;
	private Integer subject_status;
	private Integer option_status;
	private String subject;
	private Integer subject_order_by;
	private Integer option_order_by;
	private String subject_memo;
	private String option_memo;
	private Integer subject_level;
	private Integer up_id;
	private String option_name;
	private Integer option_score;
	private Integer option_type;
	private Date subject_create_date;
	private String subject_create_by;
	private Date option_create_date;
	private String option_create_by;
	private Integer option_count;
	private Integer option_score_sum;
	private Integer nextLevelCount;
	private String scoreCard;
	
	public Integer getSubject_id() {
		return subject_id;
	}
	public void setSubject_id(Integer subject_id) {
		this.subject_id = subject_id;
	}
	public Integer getOption_id() {
		return option_id;
	}
	public void setOption_id(Integer option_id) {
		this.option_id = option_id;
	}
	public Integer getSubject_status() {
		return subject_status;
	}
	public void setSubject_status(Integer subject_status) {
		this.subject_status = subject_status;
	}
	public Integer getOption_status() {
		return option_status;
	}
	public void setOption_status(Integer option_status) {
		this.option_status = option_status;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getSubject_order_by() {
		return subject_order_by;
	}
	public void setSubject_order_by(Integer subject_order_by) {
		this.subject_order_by = subject_order_by;
	}
	public Integer getOption_order_by() {
		return option_order_by;
	}
	public void setOption_order_by(Integer option_order_by) {
		this.option_order_by = option_order_by;
	}
	public String getSubject_memo() {
		return subject_memo;
	}
	public void setSubject_memo(String subject_memo) {
		this.subject_memo = subject_memo;
	}
	public String getOption_memo() {
		return option_memo;
	}
	public void setOption_memo(String option_memo) {
		this.option_memo = option_memo;
	}
	public Integer getSubject_level() {
		return subject_level;
	}
	public void setSubject_level(Integer subject_level) {
		this.subject_level = subject_level;
	}
	public Integer getUp_id() {
		return up_id;
	}
	public void setUp_id(Integer up_id) {
		this.up_id = up_id;
	}
	public String getOption_name() {
		return option_name;
	}
	public void setOption_name(String option_name) {
		this.option_name = option_name;
	}
	public Integer getOption_score() {
		return option_score;
	}
	public void setOption_score(Integer option_score) {
		this.option_score = option_score;
	}
	public Integer getOption_type() {
		return option_type;
	}
	public void setOption_type(Integer option_type) {
		this.option_type = option_type;
	}

	public Date getSubject_create_date() {
		return subject_create_date;
	}
	public void setSubject_create_date(Date subject_create_date) {
		this.subject_create_date = subject_create_date;
	}
	public String getSubject_create_by() {
		return subject_create_by;
	}
	public void setSubject_create_by(String subject_create_by) {
		this.subject_create_by = subject_create_by;
	}
	public Date getOption_create_date() {
		return option_create_date;
	}
	public void setOption_create_date(java.sql.Date option_create_date) {
		this.option_create_date = option_create_date;
	}
	public String getOption_create_by() {
		return option_create_by;
	}
	public void setOption_create_by(String option_create_by) {
		this.option_create_by = option_create_by;
	}
	public Integer getOption_count() {
		return option_count;
	}
	public void setOption_count(Integer option_count) {
		this.option_count = option_count;
	}
	public Integer getOption_score_sum() {
		return option_score_sum;
	}
	public void setOption_score_sum(Integer option_score_sum) {
		this.option_score_sum = option_score_sum;
	}
	public Integer getNextLevelCount() {
		return nextLevelCount;
	}
	public void setNextLevelCount(Integer nextLevelCount) {
		this.nextLevelCount = nextLevelCount;
	}
	public String getScoreCard() {
		return scoreCard;
	}
	public void setScoreCard(String scoreCard) {
		this.scoreCard = scoreCard;
	}
	
}
