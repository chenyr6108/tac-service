package com.brick.bussinessReport.dao;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class DateInitializeDAO extends BaseDAO {

	public void dateInitializeConfig(int day) {
		
		Map<String,Integer> paramMap=new HashMap<String,Integer>();
		
		paramMap.put("DAY",day);
		
		this.getSqlMapClientTemplate().insert("job.dateInitializeConfig",paramMap);
		
	}
	
	public void insertLeapYear(String year,String date) {
		
		Map<String,String> paramMap=new HashMap<String,String>();
		
		paramMap.put("YEAR",year);
		paramMap.put("DATE",date);
		
		this.getSqlMapClientTemplate().insert("job.insertLeapYear",paramMap);
	}
}
