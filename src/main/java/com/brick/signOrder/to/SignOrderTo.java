package com.brick.signOrder.to;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 签办单TO
 * @author yangliu
 *
 */
public class SignOrderTo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//主键ID
	private Integer id;
	//编号
	private String signCode;
	//申请人id
	private Integer applyUserId;
	//申请人名字
	private String applyUserName;
	//申请人email
	private String applyUserEmail;
	//申请公司id
	private Integer companyCode;
	//创建时间
	private Timestamp createTime;
	//内容
	private String content;
	//状态码
	private Integer orderStatus;
	//状态名
	private String orderStatusName;
	//会签名单（，分割）
	private String countersignCodeOrder;
	//完成会签名单（，分割）
	private String completeCodeOrder;
	//当前会签状态
	private Integer currentCountersignCodeOrder;
	//当前加签人
	private String currentCountersignCodeOrderName;
	//当前处理人id
	private Integer currentOperatorId;
	//当前处理人名
	private String currentOperatorName;
	//当前处理人email
	private String currentOperatorEmail;
	//概述
	private String summary;
	//完成时间
	private Timestamp completeTime;
	//后会名单（，分割）
	private String lastCountersignCodeOrder;
	//后会完成名单（，分割）
	private String lastCompleteCodeOrder;
	//字
	private String code;
	//更新时间
	private Timestamp updateTime;
	//代理人id
	private Integer agentUserId;
	//代理人name
	private String agentUserName;
	//申请人部门名
	private String deptName;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSignCode() {
		return signCode;
	}
	public void setSignCode(String signCode) {
		this.signCode = signCode;
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
	public String getApplyUserEmail() {
		return applyUserEmail;
	}
	public void setApplyUserEmail(String applyUserEmail) {
		this.applyUserEmail = applyUserEmail;
	}
	public Integer getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(Integer companyCode) {
		this.companyCode = companyCode;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getOrderStatusName() {
		return orderStatusName;
	}
	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
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
	public String getCurrentOperatorEmail() {
		return currentOperatorEmail;
	}
	public void setCurrentOperatorEmail(String currentOperatorEmail) {
		this.currentOperatorEmail = currentOperatorEmail;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Timestamp getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(Timestamp completeTime) {
		this.completeTime = completeTime;
	}
	public String getLastCountersignCodeOrder() {
		return lastCountersignCodeOrder;
	}
	public void setLastCountersignCodeOrder(String lastCountersignCodeOrder) {
		this.lastCountersignCodeOrder = lastCountersignCodeOrder;
	}
	public String getLastCompleteCodeOrder() {
		return lastCompleteCodeOrder;
	}
	public void setLastCompleteCodeOrder(String lastCompleteCodeOrder) {
		this.lastCompleteCodeOrder = lastCompleteCodeOrder;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getCurrentCountersignCodeOrder() {
		return currentCountersignCodeOrder;
	}
	public void setCurrentCountersignCodeOrder(Integer currentCountersignCodeOrder) {
		this.currentCountersignCodeOrder = currentCountersignCodeOrder;
	}
	public String getCurrentCountersignCodeOrderName() {
		return currentCountersignCodeOrderName;
	}
	public void setCurrentCountersignCodeOrderName(
			String currentCountersignCodeOrderName) {
		this.currentCountersignCodeOrderName = currentCountersignCodeOrderName;
	}
	public Integer getAgentUserId() {
		return agentUserId;
	}
	public void setAgentUserId(Integer agentUserId) {
		this.agentUserId = agentUserId;
	}
	public String getAgentUserName() {
		return agentUserName;
	}
	public void setAgentUserName(String agentUserName) {
		this.agentUserName = agentUserName;
	}
	
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public SignOrderTo(){}
	public SignOrderTo(Integer id, String signCode, Integer applyUserId,
			String applyUserName, String applyUserEmail, Integer companyCode,
			Timestamp createTime, String content, Integer orderStatus,
			String orderStatusName, String countersignCodeOrder,
			String completeCodeOrder, Integer currentCountersignCodeOrder,
			String currentCountersignCodeOrderName, Integer currentOperatorId,
			String currentOperatorName, String currentOperatorEmail,
			String summary, Timestamp completeTime,
			String lastCountersignCodeOrder, String lastCompleteCodeOrder,
			String code, Timestamp updateTime, Integer agentUserId,
			String agentUserName) {
		super();
		this.id = id;
		this.signCode = signCode;
		this.applyUserId = applyUserId;
		this.applyUserName = applyUserName;
		this.applyUserEmail = applyUserEmail;
		this.companyCode = companyCode;
		this.createTime = createTime;
		this.content = content;
		this.orderStatus = orderStatus;
		this.orderStatusName = orderStatusName;
		this.countersignCodeOrder = countersignCodeOrder;
		this.completeCodeOrder = completeCodeOrder;
		this.currentCountersignCodeOrder = currentCountersignCodeOrder;
		this.currentCountersignCodeOrderName = currentCountersignCodeOrderName;
		this.currentOperatorId = currentOperatorId;
		this.currentOperatorName = currentOperatorName;
		this.currentOperatorEmail = currentOperatorEmail;
		this.summary = summary;
		this.completeTime = completeTime;
		this.lastCountersignCodeOrder = lastCountersignCodeOrder;
		this.lastCompleteCodeOrder = lastCompleteCodeOrder;
		this.code = code;
		this.updateTime = updateTime;
		this.agentUserId = agentUserId;
		this.agentUserName = agentUserName;
	}
	
}
