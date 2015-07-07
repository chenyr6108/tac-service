package com.brick.dataStatistics.service.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class VisitDao extends BaseDAO {
         
	   public List<String> getAllYear(){
		   
		   @SuppressWarnings("unchecked")
		List<String> result=this.getSqlMapClientTemplate()
				   .queryForList("visitStatistics.getAllYear");
		   
		   if(result==null){
			   result=new ArrayList<String>();
		   }
		   
		   return result;
	   }
	   
	   
/*	   public List<HashMap<String,Object>> getStatisticByYear(Map<String,Object> params){
		      
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				   .queryForList("visitStatistics.getStatistics",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }*/
	   
	   
	   //get visitor list
	   public List<HashMap<String,Object>> getNameList(Map<String,Object> params){
		   
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				    .queryForList("visitStatistics.getAllVisitor",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }
	   
	   
	   public List<HashMap<String,Object>> getDataList(Map<String,Object> params){
		   
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				    .queryForList("visitStatistics.getDataList",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }
	   
	   
	   public List<HashMap<String,Object>> getStatis(Map<String,Object> params){
		   
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				    .queryForList("visitStatistics.getStatisticsByYear",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }
	   
	   
	   public List<HashMap<String,Object>> getCountPerVisitorByYear(Map<String,Object> params){
		   
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				    .queryForList("visitStatistics.getCountPerVisitorByYear",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }
	   
	   //获取到所有访客的办事处
	        public List<HashMap<String,Object>> getDeptList(Map<String,Object> params){
	        	
	 		   @SuppressWarnings("unchecked")
	 			List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
	 					    .queryForList("visitStatistics.getDeptList",params);
	 			   
	 			   if(result==null){
	 				   result=new ArrayList<HashMap<String,Object>>();
	 			   }
	 			   
	 			   return result;
	        }
	   
	   
	   public List<HashMap<String,Object>> getCountForMonth(Map<String,Object> params){
		   
		   @SuppressWarnings("unchecked")
		List<HashMap<String,Object>> result=this.getSqlMapClientTemplate()
				    .queryForList("visitStatistics.getCountForMonth",params);
		   
		   if(result==null){
			   result=new ArrayList<HashMap<String,Object>>();
		   }
		   
		   return result;
	   }
	   
	   public int getTotal(Map<String,Object> params){
		   
		    int total=Integer.parseInt(this.getSqlMapClientTemplate()
		    		.queryForObject("visitStatistics.getTotal",params).toString());
		    
		    return total;
	   }
	 
}
