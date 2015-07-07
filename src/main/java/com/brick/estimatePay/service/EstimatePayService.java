package com.brick.estimatePay.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.estimatePay.dao.EstimatePayDao;
import com.brick.estimatePay.to.EstimatePayJobTo;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class EstimatePayService extends BaseService {

	
	  Log logger=LogFactory.getLog(EstimatePayService.class);
	  
	  private EstimatePayDao estimatePayDao;

	public EstimatePayDao getEstimatePayDao() {
		return estimatePayDao;
	}

	public void setEstimatePayDao(EstimatePayDao estimatePayDao) {
		this.estimatePayDao = estimatePayDao;
	}
	
	public List<HashMap<String,Object>> getWorkDay(Context context,Map<String,Object> params){
		
		List<String> errList=context.errList;
		List<HashMap<String,Object>> result=null;
		   try{
			   result=this.getEstimatePayDao().getAllWorkDay(params);
		   }catch(Exception e){
				LogPrint.getLogStackTrace(e, logger);
				e.printStackTrace();
				errList.add("数据分析--统计报表出错!请联系管理员");
		   }
		return  result;
	}
	
	
	public List<HashMap<String,Object>> getDataList(Map<String,Object> params){
		
		return this.getEstimatePayDao().getAllData(params);
	}
	
	
	
	
	
	public List<HashMap<String,Object>> getDataListByDay(Map<String,Object> params){
		
		return this.getEstimatePayDao().getEveryDayTotal(params);
	}
	
	
	
	@Transactional(rollbackFor=Exception.class)
	public void getPreFundingFor_30day()throws Exception{
		
		
		
		List<EstimatePayJobTo> result=this.getEstimatePayDao()
				.getEstimatePayOfJobTo();
		
		for(int i=0;i<result.size();i++){
			if(result.get(i).getPay_count()>0){
			    result.get(i).setId(String.valueOf(System.currentTimeMillis()));
			    this.getEstimatePayDao().addlog(result.get(i));
			}
		}
		
	}
}
