package com.brick.signOrder.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;
import com.brick.signOrder.to.SignOrderLogTo;
import com.brick.signOrder.to.SignOrderTo;

public class SignOrderDao extends BaseDAO{
	
	/**
	 * 查询全部
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<SignOrderTo> getAllSignOrders(Map<String, Object> param) throws Exception{
		return (List<SignOrderTo>)this.getSqlMapClientTemplate().queryForList("signOrder.getAllSignOrders",param);
	}

	/**
	 * 获取最大的编号
	 * @param companyName
	 * @param departmentCode
	 * @return
	 * @throws Exception
	 */
	public String getMaxSignOrderCodeByCompanyNameAndDepartmentCode(int companyCode, String departmentCode) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("companyCode", companyCode);
		param.put("departmentCode", departmentCode);
		return (String)this.getSqlMapClientTemplate().queryForObject("signOrder.getMaxSignOrderCodeByCompanyNameAndDepartmentCode",param);
	}

	/**
	 * 上传文件与签办单同步更新
	 * @param context
	 * @throws Exception
	 */
	public void syncSignOrderFiles(int signOrderId, String fileIds, String fileType) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("demandIdForFile", signOrderId);
		params.put("files", fileIds);
		params.put("fileType", fileType);
		this.getSqlMapClientTemplate().update("demand.updateDemandFiles", params);		
	}

	/**
	 * 添加签办单
	 * @param context
	 * @return
	 */
	public int insertSignOrder(SignOrderTo signOrderTo) throws Exception {
		return (Integer)this.getSqlMapClientTemplate().insert("signOrder.insertSignOrder", signOrderTo);		
	}
	
	/**
	 * 根据id获取签办单
	 * @param id
	 * @return
	 */
	public SignOrderTo getSignOrderById(int id) throws Exception{
		return (SignOrderTo)this.getSqlMapClientTemplate().queryForObject("signOrder.getSignOrderById", id);
	}

	/**
	 * 更新签办单
	 * @param signOrderTo
	 * @throws Exception
	 */
	public void updateSignOrder(SignOrderTo signOrderTo) throws Exception {
		this.getSqlMapClientTemplate().update("signOrder.updateSignOrder", signOrderTo);
	}
	
	/**
	 * 更新会签名单
	 * @param signOrderId
	 * @param countersignCodeOrder
	 * @param opType
	 * @throws Exception
	 */
	public void updateCountersign(int signOrderId, String countersignCodeOrder) throws Exception{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("signOrderId", signOrderId);
		param.put("countersignCodeOrder", countersignCodeOrder);
		this.getSqlMapClientTemplate().update("signOrder.updateCountersign", param);
	}

	/**
	 * 添加log
	 * @param signOrderLogTo
	 */
	public void addSignOrderLog(SignOrderLogTo signOrderLogTo) {
		this.getSqlMapClientTemplate().update("signOrder.addSignOrderLog", signOrderLogTo);
	}

	/**
	 * 查询log
	 * @param signOrderId
	 * @return
	 */
	public List<SignOrderLogTo> getSignOrderLogsById(int signOrderId) {
		return (List<SignOrderLogTo>)this.getSqlMapClientTemplate().queryForList("signOrder.getSignOrderLogsById", signOrderId);
	}
	
	/**
	 * 获取签办单上传附件
	 * @param fileType
	 * @param signOrderId
	 * @return
	 */
	public List<Map<String, Object>> getUploadFilesBySignOrderId(String fileType, int signOrderId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileType", fileType);
		params.put("demandId", signOrderId);
		return (List<Map<String, Object>>)this.getSqlMapClientTemplate().queryForList("demand.getFilesByDemandId", params);
	}
	
	/**
	 * 更新后会名单
	 * @param signOrderId
	 * @param lastCountersignCodeOrder
	 * @param opType
	 */
	public void updateLastCountersign(int signOrderId,
			String lastCountersignCodeOrder) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("signOrderId", signOrderId);
		param.put("lastCountersignCodeOrder", lastCountersignCodeOrder);
		this.getSqlMapClientTemplate().update("signOrder.updateLastCountersign", param);
	}

	/**
	 * 删除附件
	 * @param delFiles
	 */
	public void deleteFiles(String delFiles) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("delFiles", delFiles);
		this.getSqlMapClientTemplate().update("demand.delDemandFiles", param);
	}

}
