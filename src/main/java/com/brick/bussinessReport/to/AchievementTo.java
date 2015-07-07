package com.brick.bussinessReport.to;

import java.text.DecimalFormat;

import com.brick.base.to.BaseTo;

public class AchievementTo extends BaseTo {

	private static final long serialVersionUID = 1L;

	private String deptId;//办事处ID
	private String deptName;//办事处名字
	private double currentAchievement=0;//本期业绩
	private double lastAchievement=0;//上期业绩
	private double compare1=0;//同期比
	private double compare2=0;//本期达成
	private double compare3=0;//累计达成
	private double compare4=0;//年度达成率
	private double currentTarget=0;//本期目标
	private double totalAchievement=0;//累计业绩
	private double totalTarget=0;//累计目标
	private double yearTarget=0;//年度目标
	
	private String achievementId;//主键	
	private double payMoney;//当日拨款金额
	private int payCount;//当日拨款案件
	private int approveCount;//当日审查通过件数
	private int pendingApproveCount;//当日送审案件
	private int cautionCount;//当日入保证金件数
	private int auditCount;//当日查询,在审查案子件数
	private int hasApproveCount;//已核准未拨款件数
	private double hasApproveAmount;//已核准未拨款金额
	private int achievementCount;//本月总拨款案
	private double achievementMoney;//本月总拨款金额
	private int lastAchievementCount;//本月尾款件数
	private double lastAchievementMoney;//本月尾款金额
	private double targetMoney;//本月任务
	private double achievement;//达成率
	private double nextDayPayMoney;//隔日拨款金额
	private String dayType;
	private String date;
	
	private double infoAmount;
	private int infoCount;
	private double hasAccessAmount;
	private int hasAccessCount;
	private double auditAmount1;
	private int auditCount1;
	private double approveAmount1;
	private int approveCount1;
	private String creditSpecialCode;//专案号
	
	private String creditId;
	
	public String getCreditId() {
		return creditId;
	}
	public void setCreditId(String creditId) {
		this.creditId = creditId;
	}
	public String getAchievementId() {
		return achievementId;
	}
	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public double getCurrentAchievement() {
		return currentAchievement;
	}
	public void setCurrentAchievement(double currentAchievement) {
		this.currentAchievement = currentAchievement;
	}
	public double getLastAchievement() {
		return lastAchievement;
	}
	public void setLastAchievement(double lastAchievement) {
		this.lastAchievement = lastAchievement;
	}
	public double getCompare1() {
		return (this.currentAchievement-this.lastAchievement)/(this.lastAchievement==0?1:this.lastAchievement)*100;
	}
	public void setCompare1(double compare1) {
		this.compare1 = compare1;
	}
	public double getCompare2() {
		return this.currentAchievement/(this.currentTarget==0?1:this.currentTarget)*100;
	}
	public void setCompare2(double compare2) {
		this.compare2 = compare2;
	}
	public double getCompare3() {
		return this.totalAchievement/(this.totalTarget==0?1:this.totalTarget)*100;
	}
	public void setCompare3(double compare3) {
		this.compare3 = compare3;
	}
	public double getCompare4() {
		return this.totalAchievement/(this.yearTarget==0?1:this.yearTarget)*100;
	}
	public void setCompare4(double compare4) {
		this.compare4 = compare4;
	}
	public double getCurrentTarget() {
		return currentTarget;
	}
	public void setCurrentTarget(double currentTarget) {
		this.currentTarget = currentTarget;
	}
	public double getTotalAchievement() {
		return totalAchievement;
	}
	public void setTotalAchievement(double totalAchievement) {
		this.totalAchievement = totalAchievement;
	}
	public double getTotalTarget() {
		return totalTarget;
	}
	public void setTotalTarget(double totalTarget) {
		this.totalTarget = totalTarget;
	}
	public double getYearTarget() {
		return yearTarget;
	}
	public void setYearTarget(double yearTarget) {
		this.yearTarget = yearTarget;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public int getPayCount() {
		return payCount;
	}
	public void setPayCount(int payCount) {
		this.payCount = payCount;
	}
	public int getApproveCount() {
		return approveCount;
	}
	public void setApproveCount(int approveCount) {
		this.approveCount = approveCount;
	}
	public int getCautionCount() {
		return cautionCount;
	}
	public void setCautionCount(int cautionCount) {
		this.cautionCount = cautionCount;
	}
	public int getAuditCount() {
		return auditCount;
	}
	public void setAuditCount(int auditCount) {
		this.auditCount = auditCount;
	}
	public int getHasApproveCount() {
		return hasApproveCount;
	}
	public void setHasApproveCount(int hasApproveCount) {
		this.hasApproveCount = hasApproveCount;
	}
	public double getHasApproveAmount() {
		return hasApproveAmount;
	}
	public void setHasApproveAmount(double hasApproveAmount) {
		this.hasApproveAmount = hasApproveAmount;
	}
	public int getAchievementCount() {
		return achievementCount;
	}
	public void setAchievementCount(int achievementCount) {
		this.achievementCount = achievementCount;
	}
	public double getAchievementMoney() {
		return achievementMoney;
	}
	public void setAchievementMoney(double achievementMoney) {
		this.achievementMoney = achievementMoney;
	}
	public double getTargetMoney() {
		return targetMoney;
	}
	public void setTargetMoney(double targetMoney) {
		this.targetMoney = targetMoney;
	}
	public int getPendingApproveCount() {
		return pendingApproveCount;
	}
	public void setPendingApproveCount(int pendingApproveCount) {
		this.pendingApproveCount = pendingApproveCount;
	}
	public String getAchievement() {
		DecimalFormat dfPer=new DecimalFormat("##0.00");
		return String.valueOf(dfPer.format(achievement))+"%";
	}
	public void setAchievement(double achievement) {
		this.achievement = achievement;
	}
	public int getLastAchievementCount() {
		return lastAchievementCount;
	}
	public void setLastAchievementCount(int lastAchievementCount) {
		this.lastAchievementCount = lastAchievementCount;
	}
	public double getLastAchievementMoney() {
		return lastAchievementMoney;
	}
	public void setLastAchievementMoney(double lastAchievementMoney) {
		this.lastAchievementMoney = lastAchievementMoney;
	}
	public double getNextDayPayMoney() {
		return nextDayPayMoney;
	}
	public void setNextDayPayMoney(double nextDayPayMoney) {
		this.nextDayPayMoney = nextDayPayMoney;
	}
	public String getDayType() {
		return dayType;
	}
	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getInfoAmount() {
		return infoAmount;
	}
	public void setInfoAmount(double infoAmount) {
		this.infoAmount = infoAmount;
	}
	public int getInfoCount() {
		return infoCount;
	}
	public void setInfoCount(int infoCount) {
		this.infoCount = infoCount;
	}
	public double getHasAccessAmount() {
		return hasAccessAmount;
	}
	public void setHasAccessAmount(double hasAccessAmount) {
		this.hasAccessAmount = hasAccessAmount;
	}
	public int getHasAccessCount() {
		return hasAccessCount;
	}
	public void setHasAccessCount(int hasAccessCount) {
		this.hasAccessCount = hasAccessCount;
	}
	public double getAuditAmount1() {
		return auditAmount1;
	}
	public void setAuditAmount1(double auditAmount1) {
		this.auditAmount1 = auditAmount1;
	}
	public int getAuditCount1() {
		return auditCount1;
	}
	public void setAuditCount1(int auditCount1) {
		this.auditCount1 = auditCount1;
	}
	public double getApproveAmount1() {
		return approveAmount1;
	}
	public void setApproveAmount1(double approveAmount1) {
		this.approveAmount1 = approveAmount1;
	}
	public int getApproveCount1() {
		return approveCount1;
	}
	public void setApproveCount1(int approveCount1) {
		this.approveCount1 = approveCount1;
	}
	public String getCreditSpecialCode() {
		return creditSpecialCode;
	}
	public void setCreditSpecialCode(String creditSpecialCode) {
		this.creditSpecialCode = creditSpecialCode;
	}
	
	
}
