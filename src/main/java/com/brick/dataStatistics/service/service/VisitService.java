package com.brick.dataStatistics.service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.dataStatistics.service.dao.VisitDao;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class VisitService extends BaseService {
	
	    Log logger=LogFactory.getLog(VisitService.class);
          
	     private VisitDao visitDao;

		public VisitDao getVisitDao() {
			return visitDao;
		}

		public void setVisitDao(VisitDao visitDao) {
			this.visitDao = visitDao;
		}
		
		public List<String> getAllYear(Context context){
			
			List<String> errList=context.errList;
			List<String> result=null;
			try{
				result=this.getVisitDao().getAllYear();
			}catch(Exception e){
				LogPrint.getLogStackTrace(e, logger);
				e.printStackTrace();
				errList.add("数据分析--统计报表出错!请联系管理员");
			}
			return result;
			  
		}
		
/*		public List<HashMap<String,Object>> getStatisticsByYear(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getStatisticByYear(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}*/
		
		
		//get name list
		public List<HashMap<String,Object>> getNameList(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getNameList(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		public List<HashMap<String,Object>> getStatis(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getStatis(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		public List<HashMap<String,Object>> getCountPerVisitorByYear(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getCountPerVisitorByYear(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		public List<HashMap<String,Object>> getCountForMonth(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getCountForMonth(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		//now
		public List<HashMap<String,Object>> getDataList(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getDataList(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		
		
		public List<HashMap<String,Object>> getDeptList(Map<String,Object> params){
			
			List<HashMap<String,Object>> result=null;
			try{
				result=this.getVisitDao().getDeptList(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		public int getTotal(Map<String,Object> params){
			
			int total=0;   
			
			try{
			      total=this.getVisitDao().getTotal(params);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			 return total;
		}
}
