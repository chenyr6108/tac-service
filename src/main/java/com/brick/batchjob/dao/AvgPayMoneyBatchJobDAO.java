package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.batchjob.to.AvgPayMoneyBatchJobTo;

public class AvgPayMoneyBatchJobDAO extends BaseDAO {

	public List<AvgPayMoneyBatchJobTo> getAvgPayMoney() throws Exception {

		List<AvgPayMoneyBatchJobTo> resultList=null;

		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAvgPayMoney");

		if(resultList==null) {
			resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		}

		return resultList;
	}

	//获得所有办事处
	public List<Map<String,Object>> getDeptList(Map<String,Object> param) throws DaoException {

		List<Map<String,Object>> result=this.getSqlMapClientTemplate().queryForList("employee.getCompany",param);

		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		return result;
	}

	//获得委贷信息
	public List<AvgPayMoneyBatchJobTo> getLoanInfoByAvg() {
		
		List<AvgPayMoneyBatchJobTo> resultList=null;

		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getLoanInfoByAvg");

		if(resultList==null) {
			resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		}

		return resultList;
	}
	
	public void insertData(AvgPayMoneyBatchJobTo insertTo) {

		this.getSqlMapClientTemplate().insert("businessReport.insertAvgPayMoney",insertTo);
	}
	
	//获得各区平均值,不包括当前月
	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyGroupByDept(Map<String,String> param) throws DaoException {
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAvgPayMoneyGroupByDept",param);
				
		if(resultList==null) {
			resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		}

		return resultList;
	}
	
	//获得全区平均值
	public AvgPayMoneyBatchJobTo getAvgPayMoneyTotal(Map<String,String> param) throws DaoException {
		
		AvgPayMoneyBatchJobTo result=null;
		result=(AvgPayMoneyBatchJobTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getAvgPayMoneyTotal",param);
		
		if(result==null) {
			result=new AvgPayMoneyBatchJobTo();
		}

		return result;
	}
	
	//获得当前月的平均数
	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyOfCurrentMonth(Map<String,String> param) throws DaoException {
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getAvgPayMoneyOfCurrentMonth",param);
				
		if(resultList==null) {
			resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		}

		return resultList;
	}
	
	public AvgPayMoneyBatchJobTo getAvgPayMoneyOfCurrentMonthTotal(Map<String,String> param) throws DaoException {
		
		AvgPayMoneyBatchJobTo result=null;
		result=(AvgPayMoneyBatchJobTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getAvgPayMoneyOfCurrentMonthTotal",param);
		
		if(result==null) {
			result=new AvgPayMoneyBatchJobTo();
		}
		
		return result;
	}
	
	public List<AvgPayMoneyBatchJobTo> getPayMoneyPayCountCurrentMonth(Map<String,String> param) throws DaoException {
		
		List<AvgPayMoneyBatchJobTo> result=null;
		result=this.getSqlMapClientTemplate().queryForList("businessReport.getPayMoneyPayCountCurrentMonth",param);
		
		if(result==null) {
			result=new ArrayList<AvgPayMoneyBatchJobTo>();
		}
		
		return result;
	}
	
	public List<String> getDateList1() {
		List<String> result=null;
		result=this.getSqlMapClientTemplate().queryForList("businessReport.getDateList1");
		
		if(result==null) {
			result=new ArrayList<String>();
		}
		
		return result;
	}
	/**全年各办事处平均值
	 * @author zhangbo
	 * @param param
	 * @return
	 * @throws DaoException
	 */
	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyYearList(Map<String,String> param) throws DaoException {
		
		List<AvgPayMoneyBatchJobTo> result=null;
		result=this.getSqlMapClientTemplate().queryForList("businessReport.getDeptAvgPayMoneyByYear",param);
		
		if(result==null) {
			result=new ArrayList<AvgPayMoneyBatchJobTo>();
		}
		
		return result;
	}
	/**
	 * 全年
	 * @param param
	 * @return
	 * @throws DaoException
	 */
	public AvgPayMoneyBatchJobTo getAvgYear(Map<String,String> param) throws DaoException {
		
		AvgPayMoneyBatchJobTo result=null;
		result=(AvgPayMoneyBatchJobTo)this.getSqlMapClientTemplate().queryForObject("businessReport.getAvgYear",param);
		
		if(result==null) {
			result=new AvgPayMoneyBatchJobTo();
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getComputeMoneyCount() throws Exception {
		
		List<Map<String,Object>> result=null;
		
		result=this.getSqlMapClientTemplate().queryForList("businessReport.getComputeMoneyCount",null);
		
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	public void insertComputeMoneyCount(Map<String,Object> param) throws Exception {
		this.getSqlMapClientTemplate().insert("businessReport.insertComputeMoneyCount",param);
	}
}
