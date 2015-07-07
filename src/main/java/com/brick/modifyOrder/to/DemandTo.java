package com.brick.modifyOrder.to;
import java.io.Serializable;
import java.sql.Date;

/**
 * 资讯需求单TO
 * @author yangliu
 *
 */
public class DemandTo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//主键ID
	private Integer id;
	//资讯单编号
	private String demandCode;
	//申请人ID
	private Integer applyUserId;
	//申请人姓名
	private String applyUserName;
	//申请人所在部门ID
	private Integer applyDepartmentId;
	//申请人所在部门名称
	private String applyDepartmentName;
	//申请时间
	private Date createTime;
	//资讯单内容
	private String content;
	//希望完成日期
	private Date hopeCompleteDate;
	//资讯单状态
	private Integer orderStatus;
	//会签名单（资讯单会签状态id以,分割，有序）
	private String countersignCodeOrder;
	//已会签名单（资讯单会签状态id以,分割，有序）
	private String completeCodeOrder;
	//当前处理人ID
	private Integer currentOperatorId;
	//当前处理人姓名
	private String currentOperatorName;
	//预计工时
	private Double hours;
	//负责人ID
	private Integer responsibleUserId;
	//负责人名称
	private String responsibleUserName;
	//最近一次操作时间
	private Date lastOpTime;
	//概要
	private String summary;
	//预计完成日期
	private Date predictDate;
	//申请人所在办事处ID
	private Integer companyId;
	//申请人所在办事处名称
	private String companyName;
	//预计完成日期超过今天的天数
	private Integer dunDays;
	//实际完成日期
	private Date completeDate;
	//高阶签核 2:强制签核（不可修改） 1：需要  0：不需要
	private Integer seniorSign;
	//类型，0内部申请 1普通
	private Integer demandType;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDemandCode() {
		return demandCode;
	}

	public void setDemandCode(String demandCode) {
		this.demandCode = demandCode;
	}

	public Integer getApplyUserId() {
		return applyUserId;
	}

	public void setApplyUserId(Integer applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyUserName() {
		return applyUserName;
	}

	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public Integer getApplyDepartmentId() {
		return applyDepartmentId;
	}

	public void setApplyDepartmentId(Integer applyDepartmentId) {
		this.applyDepartmentId = applyDepartmentId;
	}

	public String getApplyDepartmentName() {
		return applyDepartmentName;
	}

	public void setApplyDepartmentName(String applyDepartmentName) {
		this.applyDepartmentName = applyDepartmentName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getHopeCompleteDate() {
		return hopeCompleteDate;
	}

	public void setHopeCompleteDate(Date hopeCompleteDate) {
		this.hopeCompleteDate = hopeCompleteDate;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCountersignCodeOrder() {
		return countersignCodeOrder;
	}

	public void setCountersignCodeOrder(String countersignCodeOrder) {
		this.countersignCodeOrder = countersignCodeOrder;
	}

	public String getCompleteCodeOrder() {
		return completeCodeOrder;
	}

	public void setCompleteCodeOrder(String completeCodeOrder) {
		this.completeCodeOrder = completeCodeOrder;
	}

	public Integer getCurrentOperatorId() {
		return currentOperatorId;
	}

	public void setCurrentOperatorId(Integer currentOperatorId) {
		this.currentOperatorId = currentOperatorId;
	}

	public String getCurrentOperatorName() {
		return currentOperatorName;
	}

	public void setCurrentOperatorName(String currentOperatorName) {
		this.currentOperatorName = currentOperatorName;
	}

	public Double getHours() {
		return hours;
	}

	public void setHours(Double hours) {
		this.hours = hours;
	}

	public Integer getResponsibleUserId() {
		return responsibleUserId;
	}

	public void setResponsibleUserId(Integer responsibleUserId) {
		this.responsibleUserId = responsibleUserId;
	}

	public String getResponsibleUserName() {
		return responsibleUserName;
	}

	public void setResponsibleUserName(String responsibleUserName) {
		this.responsibleUserName = responsibleUserName;
	}

	public Date getLastOpTime() {
		return lastOpTime;
	}

	public void setLastOpTime(Date lastOpTime) {
		this.lastOpTime = lastOpTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getPredictDate() {
		return predictDate;
	}

	public void setPredictDate(Date predictDate) {
		this.predictDate = predictDate;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getDunDays() {
		return dunDays;
	}

	public void setDunDays(Integer dunDays) {
		this.dunDays = dunDays;
	}

	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Integer getSeniorSign() {
		return seniorSign;
	}

	public void setSeniorSign(Integer seniorSign) {
		this.seniorSign = seniorSign;
	}

	public Integer getDemandType() {
		return demandType;
	}

	public void setDemandType(Integer demandType) {
		this.demandType = demandType;
	}

	public DemandTo(){}

	public DemandTo(Integer id, String demandCode, Integer applyUserId,
			String applyUserName, Integer applyDepartmentId,
			String applyDepartmentName, Date createTime, String content,
			Date hopeCompleteDate, Integer orderStatus,
			String countersignCodeOrder, String completeCodeOrder,
			Integer currentOperatorId, String currentOperatorName,
			Double hours, Integer responsibleUserId,
			String responsibleUserName, Date lastOpTime, String summary,
			Date predictDate, Integer companyId, String companyName,
			Integer dunDays, Date completeDate, Integer seniorSign,
			Integer demandType) {
		super();
		this.id = id;
		this.demandCode = demandCode;
		this.applyUserId = applyUserId;
		this.applyUserName = applyUserName;
		this.applyDepartmentId = applyDepartmentId;
		this.applyDepartmentName = applyDepartmentName;
		this.createTime = createTime;
		this.content = content;
		this.hopeCompleteDate = hopeCompleteDate;
		this.orderStatus = orderStatus;
		this.countersignCodeOrder = countersignCodeOrder;
		this.completeCodeOrder = completeCodeOrder;
		this.currentOperatorId = currentOperatorId;
		this.currentOperatorName = currentOperatorName;
		this.hours = hours;
		this.responsibleUserId = responsibleUserId;
		this.responsibleUserName = responsibleUserName;
		this.lastOpTime = lastOpTime;
		this.summary = summary;
		this.predictDate = predictDate;
		this.companyId = companyId;
		this.companyName = companyName;
		this.dunDays = dunDays;
		this.completeDate = completeDate;
		this.seniorSign = seniorSign;
		this.demandType = demandType;
	}

}
