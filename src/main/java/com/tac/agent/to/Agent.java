package com.tac.agent.to;

import java.io.Serializable;
import java.sql.Timestamp;

public class Agent implements Serializable {

	/**
	 * 代理模组
	 */
	private static final long serialVersionUID = 1L;
	
	//主键ID
	private Integer id;
	//用户id
	private Integer userId;
	//用户名
	private String userName;
	//代理人id
	private Integer agentUserId;
	//代理人姓名
	private String agentUserName;
	//代理人email
	private String agentUserEmail;
	//状态
	private Integer status;
	//代理模组id
	private String agentModuleId;
	//代理模组名
	private String agentModuleName;
	//代理开始时间
	private Timestamp startTime;
	//代理结束时间
	private Timestamp endTime;
	//代理信息备注
	private String remark;
	//创建时间
	private Timestamp createTime;
	//创建人id
	private Integer createUserId;
	//创建人name
	private String createUserName;
	//更新时间
	private Timestamp updateTime;
	//更新人id
	private Integer updateUserId;
	//更新人name
	private String updateUserName;
	//逾期分钟数
	private int overdueMinute;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAgentModuleId() {
		return agentModuleId;
	}

	public void setAgentModuleId(String agentModuleId) {
		this.agentModuleId = agentModuleId;
	}

	public String getAgentModuleName() {
		return agentModuleName;
	}

	public void setAgentModuleName(String agentModuleName) {
		this.agentModuleName = agentModuleName;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public int getOverdueMinute() {
		return overdueMinute;
	}
	
	public void setOverdueMinute(int overdueMinute) {
		this.overdueMinute = overdueMinute;
	}
	
	public String getAgentUserEmail() {
		return agentUserEmail;
	}
	
	public void setAgentUserEmail(String agentUserEmail) {
		this.agentUserEmail = agentUserEmail;
	}

	public Agent(){}

	public Agent(Integer id, Integer userId, String userName,
			Integer agentUserId, String agentUserName, String agentUserEmail,
			Integer status, String agentModuleId, String agentModuleName,
			Timestamp startTime, Timestamp endTime, String remark,
			Timestamp createTime, Integer createUserId, String createUserName,
			Timestamp updateTime, Integer updateUserId, String updateUserName,
			int overdueMinute) {
		super();
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.agentUserId = agentUserId;
		this.agentUserName = agentUserName;
		this.agentUserEmail = agentUserEmail;
		this.status = status;
		this.agentModuleId = agentModuleId;
		this.agentModuleName = agentModuleName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.remark = remark;
		this.createTime = createTime;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateTime = updateTime;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
		this.overdueMinute = overdueMinute;
	}

}
