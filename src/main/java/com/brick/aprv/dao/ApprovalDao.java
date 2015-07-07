package com.brick.aprv.dao;

import java.util.List;

import com.brick.aprv.filter.ApprovalFilter;
import com.brick.aprv.to.ApprovalTo;
import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class ApprovalDao extends BaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<ApprovalTo> selectApproval(ApprovalFilter filter) {
		return (List<ApprovalTo>)super.getSqlMapClientTemplate().queryForList("approval.selectApproval", filter);
	}
	
	public Integer insertApproval(ApprovalTo approval) {
		return (Integer)super.getSqlMapClientTemplate().insert("approval.insertApproval",approval);
	}
	
	public Integer updateApproval(ApprovalTo approval) {
		return super.getSqlMapClientTemplate().update("approval.updateApproval",approval);
	}
	
	public ApprovalTo previewApproval(ApprovalFilter filter) {
		return (ApprovalTo)super.getSqlMapClientTemplate().queryForObject("approval.previewApproval", filter);
	}
	
}
