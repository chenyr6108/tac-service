package com.brick.dataStatistics.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.dataStatistics.service.service.VisitService;
import com.brick.dataStatistics.service.to.DataStatistics;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.ibm.icu.text.SimpleDateFormat;

public class VisitCommand extends BaseCommand {
       Log logger=LogFactory.getLog(VisitCommand.class);
       
       
     private VisitService visitService;
       
     public VisitService getVisitService() {
		return visitService;
	}

	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}

	
    /*
     * 获取访厂的人数列表
     * 
    */
	
	public void getStatis(Context context) {
		  Map<String,Object> outputMap=new HashMap<String,Object>();
		  
		  Map<String,Object> params=new HashMap<String,Object>();
		  
		  SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//保存页面的查询条件
			if("".equals(context.contextMap.get("YEAR"))||context.contextMap.get("YEAR")==null) {
				//获得系统年份,初始化进入时候拿系统年份
				outputMap.put("YEAR",Calendar.getInstance().get(Calendar.YEAR));
			} else {
				outputMap.put("YEAR",context.contextMap.get("YEAR"));
			}
			
			if("".equals(context.contextMap.get("DEPT"))||context.contextMap.get("DEPT")==null){
				outputMap.put("DEPT",null);
				context.contextMap.put("DEPT","");
			}else{
				outputMap.put("DEPT",context.contextMap.get("DEPT"));
			}
			
			  //获取表中已存在的年份
			  List<String> yearList=this.getVisitService().getAllYear(context);
			  params.put("DEPT", context.contextMap.get("DEPT"));
	          params.put("YEAR",outputMap.get("YEAR"));
	          
	          List<HashMap<String,Object>> deptList=this.getVisitService().getDeptList(params);
	          List<HashMap<String,Object>> dataList=this.getVisitService().getDataList(params);
	          List<HashMap<String,Object>> k=this.getVisitService().getCountPerVisitorByYear(params);
	          List<HashMap<String,Object>> t=new ArrayList<HashMap<String,Object>>();
	        		  t=this.getVisitService().getCountForMonth(params);
	          List<DataStatistics> sumOfVisit=new ArrayList<DataStatistics>();
	          int total=this.getVisitService().getTotal(params);
	          
	          for(int i=0;i<k.size();i++){
	            DataStatistics f=new DataStatistics();
	            f.setId(Integer.parseInt(k.get(i).get("V").toString()));
	            f.setCount(Integer.parseInt(k.get(i).get("Z").toString()));
	            sumOfVisit.add(f);
	          }
	          
	          /*
/*	          for(int i=0;i<names.size();i++){
	        	  String n=(String)names.get(i).get("NAME");
	        	  if(n==null||n==""){
	        		  continue;
	        	  }
	        	  User u=new User();
	        	  u.setId(Integer.parseInt(names.get(i).get("ID").toString()));
	        	  u.setUsername((String)names.get(i).get("NAME"));
	        	  nameList.add(u);
	          }*/
	          
/*	          for(int i=0;i<datas.size();i++){
	        	  DateStatis d=new DateStatis();
	        	  d.setId(Integer.parseInt(datas.get(i).get("REAL_VISITOR").toString()));
	        	  d.setMonth(Integer.parseInt(datas.get(i).get("MO").toString()));
	        	  d.setCount(Integer.parseInt(datas.get(i).get("CO").toString()));
	        	  dataList.add(d);
	          }*/
              
	          //----------------对数据集重新组装----------------------------------
/*             for(int i=0;i<nameList.size();i++){
            	 List<DateStatis> dl=new ArrayList<DateStatis>();
            	 List<Integer> months=new ArrayList<Integer>();
            	 int no=nameList.get(i).getId();
            	 for(int j=0;j<dataList.size();j++){
            		 if(no!=dataList.get(j).getId()){
            			 continue;
            		 }else{
            			 dl.add(dataList.get(j));
            		 }
            	 }
            	 
            	 for(int m=0;m<dl.size();m++){
            		 months.add(dl.get(m).getMonth());
            	 }
            	 
            	 for(int n=1;n<=12;n++){
            		 if(months.contains(n)){
            			 continue;
            		 }
            		 dataList.add(new DateStatis(no, n, 0));
            	 }
             }*/
             
             //******************对统计每个月的总数列表进行重新组装****************************
              List<Integer> mo=new ArrayList<Integer>();
	          for(int i=0;i<t.size();i++){
	        	mo.add(Integer.parseInt(t.get(i).get("MON").toString()));
	          }
	          
	          for(int i=1;i<=12;i++){
	        	  if(mo.contains(i)){
	        		  continue;
	        	  }
	        	 HashMap<String,Object> m=new HashMap<String,Object>();
	        	 m.put("MON", i);
	        	 m.put("CI", 0);
	        	  t.add(m);
	          }
			  
		  if(context.errList.isEmpty()){
			  outputMap.put("backDate", df.format(new Date()));
			  outputMap.put("total", total);
			  outputMap.put("deptList", deptList);
			  outputMap.put("yearList", yearList);
			  outputMap.put("dataResult", dataList);
			  outputMap.put("sumOfVisit", sumOfVisit);
			  outputMap.put("countForMonth", t);
			  Output.jspOutput(outputMap, context, "/dataStatistics/visitStatistics.jsp");
		  }else{
				outputMap.put("errList",context.errList);
				Output.jspOutput(outputMap,context,"/error.jsp");
		  }
      } 
	   
}
