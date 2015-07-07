package com.brick.bussinessReport.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.bussinessReport.to.DunCaseChartTO;
import com.brick.bussinessReport.to.DunCaseDetailTO;
import com.brick.bussinessReport.to.DunCaseTO;

public class DunCaseDAO extends BaseDAO {

	public List<DunCaseTO> getDunCaseByUserId(Map<String,Integer> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDunCaseByUserId",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	public DunCaseTO getDunCaseByMoney(Map<String,Object> param) throws Exception {
		
		DunCaseTO result=null;
		
		result=(DunCaseTO)this.getSqlMapClientTemplate().queryForObject("businessReport.getDunCaseByMoney",param);
		
		if(result==null) {
			result=new DunCaseTO();
		}
		
		return result;
	}
	
	public List<DunCaseTO> getTotalDunCaseByUserId() throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getTotalDunCaseByUserId");
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	public DunCaseTO getTotalDunCaseByMoney(Map<String,Object> paramMoney) throws Exception {
		
		DunCaseTO result=null;
		
		result=(DunCaseTO)this.getSqlMapClientTemplate().queryForObject("businessReport.getTotalDunCaseByMoney",paramMoney);
		
		if(result==null) {
			result=new DunCaseTO();
		}
		
		return result;
	}
	
	public List<DunCaseTO> getDunCaseDetail(Map<String,Object> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDunCaseDetail",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	public void insertDunCaseByUserId(DunCaseTO to) {
		this.getSqlMapClientTemplate().insert("businessReport.insertDunCaseByUserId",to);
	}
	
	public void insertDunCaseByMoney(DunCaseTO to) {
		this.getSqlMapClientTemplate().insert("businessReport.insertDunCaseByMoney",to);
	}
	
	public void insertTotalMoneyRentMoney(DunCaseTO to) {
		this.getSqlMapClientTemplate().insert("businessReport.insertTotalMoneyRentMoney",to);
	}
	
	public DunCaseTO getTotalMoney(Map<String,Object> paramMoney) throws Exception {
		
		DunCaseTO result=null;
		
		result=(DunCaseTO)this.getSqlMapClientTemplate().queryForObject("businessReport.getTotalMoney",paramMoney);
		
		if(result==null) {
			result=new DunCaseTO();
		}
		
		return result;
	}
	
	public List<DunCaseTO> queryDunCaseByMoney(Map<String,Object> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryDunCaseByMoney",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(一级查询)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DunCaseDetailTO> queryDunCase(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryDunCase",param);
		if(resultList==null) {
			resultList=new ArrayList<DunCaseDetailTO>();
		}
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(二级查询)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DunCaseDetailTO> queryDunCaseDetail(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryDunCaseDetail",param);
		if(resultList==null) {
			resultList=new ArrayList<DunCaseDetailTO>();
		}
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(一级查询+二级查询金额)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DunCaseDetailTO> queryDunCaseDetailByPrimaryAndPrice(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryDunCaseDetailByPrimaryAndPrice",param);
		if(resultList==null) {
			resultList=new ArrayList<DunCaseDetailTO>();
		}
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(一级查询金额+二级查询)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DunCaseDetailTO> queryDunCaseDetailByPriceAndSecondary(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList=null;
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryDunCaseDetailByPriceAndSecondary",param);
		if(resultList==null) {
			resultList=new ArrayList<DunCaseDetailTO>();
		}
		return resultList;
	}
	
	public List<DunCaseTO> queryRentMoney(Map<String,Object> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.queryRentMoney",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	public List<DunCaseChartTO> getTotalMoneyCount(Map<String,Object> param) {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getTotalMoneyCount",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
	
	public List<DunCaseChartTO> getDun181_(Map<String,Object> param) {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDun181",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
	
	public List<DunCaseChartTO> getDun31_91_(Map<String,Object> param) {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDun3191",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
	
	public void insertDunChart(DunCaseChartTO to) {
		this.getSqlMapClientTemplate().insert("businessReport.insertDunChart",to);
	}
	
	public List<DunCaseChartTO> getDayChart(Map<String,Object> param) throws Exception {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getDayChart",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
	
	public List<DunCaseChartTO> getWeekChart(Map<String,Object> param) throws Exception {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getWeekChart",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
	
	public List<DunCaseChartTO> getMonthChart(Map<String,Object> param) throws Exception {
		
		List<DunCaseChartTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("businessReport.getMonthChart",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseChartTO>();
		}
		
		return resultList;
	}
}
