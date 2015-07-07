package com.brick.modifyOrder.to;
import java.io.Serializable;
import java.util.Date;

/**
 * 资讯需求单操作日志TO
 * @author yangliu
 *
 */
public class DemandLogTo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键ID
	private Integer id;
	//资讯需求单ID
	private Integer demandOrderId;
	//资讯需求单状态
	private Integer orderStatus;
	//操作开始时间
	private Date operateTimeBegin;
	//操作结束时间
	private Date operateTimeEnd;
	//操作人ID
	private Integer operatorId;
	//操作人姓名
	private String operatorName;
	//操作状态
	private Integer operateState;
	//处理意见
	private String operateSuggest;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDemandOrderId() {
		return demandOrderId;
	}
	public void setDemandOrderId(Integer demandOrderId) {
		this.demandOrderId = demandOrderId;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Date getOperateTimeBegin() {
		return operateTimeBegin;
	}
	public void setOperateTimeBegin(Date operateTimeBegin) {
		this.operateTimeBegin = operateTimeBegin;
	}
	public Date getOperateTimeEnd() {
		return operateTimeEnd;
	}
	public void setOperateTimeEnd(Date operateTimeEnd) {
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
	
	public DemandLogTo(){}
	public DemandLogTo(Integer id, Integer demandOrderId, Integer orderStatus,
			Date operateTimeBegin, Date operateTimeEnd, Integer operatorId,
			String operatorName, Integer operateState, String operateSuggest) {
		super();
		this.id = id;
		this.demandOrderId = demandOrderId;
		this.orderStatus = orderStatus;
		this.operateTimeBegin = operateTimeBegin;
		this.operateTimeEnd = operateTimeEnd;
		this.operatorId = operatorId;
		this.operatorName = operatorName;
		this.operateState = operateState;
		this.operateSuggest = operateSuggest;
	}
	
}
