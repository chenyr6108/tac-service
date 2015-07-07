package com.brick.signOrder.to;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 签办单操作日志TO
 * @author yangliu
 *
 */
public class SignOrderLogTo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键ID
	private Integer id;
	//签办单id
	private Integer signOrderId;
	//签办单操作前状态
	private Integer signStatus;
	//操作开始时间
	private Timestamp operateTimeBegin;
	//操作结束时间
	private Timestamp operateTimeEnd;
	//操作人id
	private Integer operatorId;
	//操作人name
	private String operatorName;
	//操作人email
	private String operatorEmail;
	//操作类型
	private Integer operateState;
	//操作内容
	private String operateSuggest;
	//原始处理人ID
	private Integer orgOperatorId;
	//原始处理人name
	private String orgOperatorName;
	//原始处理人email
	private String orgOperatorEmail;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSignOrderId() {
		return signOrderId;
	}
	public void setSignOrderId(Integer signOrderId) {
		this.signOrderId = signOrderId;
	}
	public Integer getSignStatus() {
		return signStatus;
	}
	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}
	public Timestamp getOperateTimeBegin() {
		return operateTimeBegin;
	}
	public void setOperateTimeBegin(Timestamp operateTimeBegin) {
		this.operateTimeBegin = operateTimeBegin;
	}
	public Timestamp getOperateTimeEnd() {
		return operateTimeEnd;
	}
	public void setOperateTimeEnd(Timestamp operateTimeEnd) {
		this.operateTimeEnd = operateTimeEnd;
	}
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Integer getOperateState() {
		return operateState;
	}
	public void setOperateState(Integer operateState) {
		this.operateState = operateState;
	}
	public String getOperateSuggest() {
		return operateSuggest;
	}
	public void setOperateSuggest(String operateSuggest) {
		this.operateSuggest = operateSuggest;
	}
	public Integer getOrgOperatorId() {
		return orgOperatorId;
	}
	public void setOrgOperatorId(Integer orgOperatorId) {
		this.orgOperatorId = orgOperatorId;
	}
	public String getOrgOperatorName() {
		return orgOperatorName;
	}
	public void setOrgOperatorName(String orgOperatorName) {
		this.orgOperatorName = orgOperatorName;
	}
	public String getOperatorEmail() {
		return operatorEmail;
	}
	public void setOperatorEmail(String operatorEmail) {
		this.operatorEmail = operatorEmail;
	}
	public String getOrgOperatorEmail() {
		return orgOperatorEmail;
	}
	public void setOrgOperatorEmail(String orgOperatorEmail) {
		this.orgOperatorEmail = orgOperatorEmail;
	}
	
	public SignOrderLogTo(){}
	public SignOrderLogTo(Integer id, Integer signOrderId, Integer signStatus,
			Timestamp operateTimeBegin, Timestamp operateTimeEnd,
			Integer operatorId, String operatorName, String operatorEmail,
			Integer operateState, String operateSuggest, Integer orgOperatorId,
			String orgOperatorName, String orgOperatorEmail) {
		super();
		this.id = id;
		this.signOrderId = signOrderId;
		this.signStatus = signStatus;
		this.operateTimeBegin = operateTimeBegin;
		this.operateTimeEnd = operateTimeEnd;
		this.operatorId = operatorId;
		this.operatorName = operatorName;
		this.operatorEmail = operatorEmail;
		this.operateState = operateState;
		this.operateSuggest = operateSuggest;
		this.orgOperatorId = orgOperatorId;
		this.orgOperatorName = orgOperatorName;
		this.orgOperatorEmail = orgOperatorEmail;
	}
	
}
