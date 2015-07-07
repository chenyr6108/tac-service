package com.brick.information.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.information.to.InforDataOfWeek;
import com.brick.information.to.InformationStatistic;

public class InformationDao extends BaseDAO {

	// 获取所有的年份
	public List<String> getYearList() {

		@SuppressWarnings("unchecked")
		List<String> result = this.getSqlMapClientTemplate().queryForList(
				"informationStatistics.getAllYear");

		if (result == null) {
			result = new ArrayList<String>();
		}

		return result;
	}
	
	public List<InformationStatistic> getInfoStaList(Map<String,Object> params){
		
		@SuppressWarnings("unchecked")
		List<InformationStatistic> result=this.getSqlMapClientTemplate().queryForList(
				"informationStatistics.getInfoStatictisList",params);
		
		if(result==null){
			 result=new ArrayList<InformationStatistic>();
		}
		
		return result;
	}
	
	//资讯周报表
	@SuppressWarnings("unchecked")
	public List<InforDataOfWeek> getDateOfWeek(){
		
	     return this.getSqlMapClientTemplate()
	    		 .queryForList("informationStatistics.getAllDataOfWeek");
	}
	
	//抓取job数据
	public InforDataOfWeek getJobData(){
		
		Map<String,Object> param=new HashMap<String,Object>();
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(new Date());
        aCalendar.add(Calendar.DAY_OF_MONTH, -6);
        param.put("begin", aCalendar.getTime());
		return (InforDataOfWeek)this.getSqlMapClientTemplate()
				.queryForObject("informationStatistics.getJobData",param);
	}
	
	//抓取上周完成总数
/*	public Integer getLastWeekTotal(){
		
		Map<String,Object> param=new HashMap<String,Object>();
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(new Date());
        aCalendar.add(Calendar.DAY_OF_MONTH, -7);
        param.put("lastweek", aCalendar.getTime());
        return (Integer)this.getSqlMapClientTemplate()
        		.queryForObject("informationStatistics.getLastWeekTotal");
	}*/
	
	public void addLog(InforDataOfWeek infor){
		
		this.getSqlMapClientTemplate().insert("informationStatistics.addLog",infor);
	}
}
