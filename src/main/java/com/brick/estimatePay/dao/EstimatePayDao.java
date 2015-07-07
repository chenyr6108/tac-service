package com.brick.estimatePay.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.estimatePay.to.EstimatePayJobTo;

public class EstimatePayDao extends BaseDAO {

	
	//获取工作日
	public List<HashMap<String,Object>> getAllWorkDay(Map<String,Object> params){
		
		@SuppressWarnings("unchecked")
		List<HashMap<String,Object>> workDay=this.getSqlMapClientTemplate()
				.queryForList("estimatePay.getAllWorkDay",params);
		
		return workDay;
	}
	
	
	public List<HashMap<String,Object>> getAllData(Map<String,Object> params){
		
		@SuppressWarnings("unchecked")
		List<HashMap<String,Object>> dataList=this.getSqlMapClientTemplate()
				.queryForList("estimatePay.getDataList",params);
		
		return dataList;
	}
	
	public List<HashMap<String,Object>> getEveryDayTotal(Map<String,Object> params){
		
		@SuppressWarnings("unchecked")
		List<HashMap<String,Object>> dataList=this.getSqlMapClientTemplate()
				.queryForList("estimatePay.getEveryDayTotal",params);
		
		return dataList;
	}
     
	public void addlog(EstimatePayJobTo params) throws Exception{
		
		EstimatePayJobTo jobTo=new EstimatePayJobTo();
		jobTo.setId(String.valueOf(System.currentTimeMillis()));
		this.getSqlMapClientTemplate().insert("estimatePay.addLog",params);
	}
	
	//dataList of run job
	public List<EstimatePayJobTo> getEstimatePayOfJobTo(){
		
		@SuppressWarnings("unchecked")
		List<EstimatePayJobTo> result=this.getSqlMapClientTemplate()
				.queryForList("estimatePay.getDataOfJob");
		return result;
	}
	
}
