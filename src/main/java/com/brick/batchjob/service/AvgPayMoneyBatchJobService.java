package com.brick.batchjob.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.AvgPayMoneyBatchJobDAO;
import com.brick.batchjob.to.AvgPayMoneyBatchJobTo;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class AvgPayMoneyBatchJobService extends BaseService {

	Log logger=LogFactory.getLog(AvgPayMoneyBatchJobService.class);
	
	private AvgPayMoneyBatchJobDAO avgPayMoneyBatchJobDAO;

	public AvgPayMoneyBatchJobDAO getAvgPayMoneyBatchJobDAO() {
		return avgPayMoneyBatchJobDAO;
	}

	public void setAvgPayMoneyBatchJobDAO(
			AvgPayMoneyBatchJobDAO avgPayMoneyBatchJobDAO) {
		this.avgPayMoneyBatchJobDAO = avgPayMoneyBatchJobDAO;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for avg pay money start  --------------------");
		}
		
		List<AvgPayMoneyBatchJobTo> resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		List<Map<String,Object>> deptList=new ArrayList<Map<String,Object>>();
		/*List<AvgPayMoneyBatchJobTo> loanList=new ArrayList<AvgPayMoneyBatchJobTo>();*/
		
		try {
			resultList=this.avgPayMoneyBatchJobDAO.getAvgPayMoney();
			
			/*loanList=this.avgPayMoneyBatchJobDAO.getLoanInfoByAvg();*/
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("decp_id","2");
			deptList=this.avgPayMoneyBatchJobDAO.getDeptList(param);
			
			//设定插入的数据
			List<AvgPayMoneyBatchJobTo> insertDataList=new ArrayList<AvgPayMoneyBatchJobTo>();
			for(int i=0;deptList!=null&&i<deptList.size();i++) {
				AvgPayMoneyBatchJobTo insertTo=new AvgPayMoneyBatchJobTo();
				for(int j=0;j<resultList.size();j++) {
					if(String.valueOf(deptList.get(i).get("DECP_ID")).equals(resultList.get(j).getDeptId())) {
						insertTo.setDeptId(resultList.get(j).getDeptId());
						insertTo.setDeptName(resultList.get(j).getDeptName());
						insertTo.setPayYear(resultList.get(j).getPayYear());
						if(resultList.get(j).getPayMonth()==1) {
							insertTo.setAvgMoney_1(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_1(resultList.get(j).getPayMoney());
							insertTo.setPayCount_1(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==2) {
							insertTo.setAvgMoney_2(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_2(resultList.get(j).getPayMoney());
							insertTo.setPayCount_2(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==3) {
							insertTo.setAvgMoney_3(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_3(resultList.get(j).getPayMoney());
							insertTo.setPayCount_3(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==4) {
							insertTo.setAvgMoney_4(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_4(resultList.get(j).getPayMoney());
							insertTo.setPayCount_4(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==5) {
							insertTo.setAvgMoney_5(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_5(resultList.get(j).getPayMoney());
							insertTo.setPayCount_5(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==6) {
							insertTo.setAvgMoney_6(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_6(resultList.get(j).getPayMoney());
							insertTo.setPayCount_6(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==7) {
							insertTo.setAvgMoney_7(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_7(resultList.get(j).getPayMoney());
							insertTo.setPayCount_7(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==8) {
							insertTo.setAvgMoney_8(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_8(resultList.get(j).getPayMoney());
							insertTo.setPayCount_8(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==9) {
							insertTo.setAvgMoney_9(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_9(resultList.get(j).getPayMoney());
							insertTo.setPayCount_9(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==10) {
							insertTo.setAvgMoney_10(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_10(resultList.get(j).getPayMoney());
							insertTo.setPayCount_10(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==11) {
							insertTo.setAvgMoney_11(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_11(resultList.get(j).getPayMoney());
							insertTo.setPayCount_11(resultList.get(j).getPayCount());
						} else if(resultList.get(j).getPayMonth()==12) {
							insertTo.setAvgMoney_12(resultList.get(j).getAvgMoney());
							insertTo.setPayMoney_12(resultList.get(j).getPayMoney());
							insertTo.setPayCount_12(resultList.get(j).getPayCount());
						}
					}
				}
				
				
				if(insertTo.getDeptId()!=null) {
					insertDataList.add(insertTo);
				}
			}
			
			//插入数据
			for(int i=0;i<insertDataList.size();i++) {
				Thread.sleep(1);//防止主键重复
				insertDataList.get(i).setAvgPayMoneyId(String.valueOf(System.currentTimeMillis()));
				this.avgPayMoneyBatchJobDAO.insertData(insertDataList.get(i));
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for avg pay money end  --------------------");
		}
	}
	
	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyGroupByDept(Context context) {
		
		String log="employeeId="+context.contextMap.get("employeeId")+"......getAvgPayMoneyGroupByDept";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		
		try {
			resultList=this.avgPayMoneyBatchJobDAO.getAvgPayMoneyGroupByDept(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgPayMoneyGroupByDept)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	} 
	
	public List<Map<String,Object>> getDeptList(Map<String,Object> param) {
		
		List<Map<String,Object>> result=null;
		
		try {
			result=this.avgPayMoneyBatchJobDAO.getDeptList(param);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public AvgPayMoneyBatchJobTo getAvgPayMoneyTotal(Context context) {
		
		String log="employeeId="+context.contextMap.get("employeeId")+"......getAvgPayMoneyTotal";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		AvgPayMoneyBatchJobTo result=null;
		
		try {
			result=this.avgPayMoneyBatchJobDAO.getAvgPayMoneyTotal(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgPayMoneyTotal)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return result;
	}

	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyOfCurrentMonth(Context context) {
		
		String log="employeeId="+context.contextMap.get("employeeId")+"......getAvgPayMoneyOfCurrentMonth";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		
		try {
			resultList=this.avgPayMoneyBatchJobDAO.getAvgPayMoneyOfCurrentMonth(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgPayMoneyOfCurrentMonth)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	}
	
	public AvgPayMoneyBatchJobTo getAvgPayMoneyOfCurrentMonthTotal(Context context) {
		
		AvgPayMoneyBatchJobTo result=null;
		try {
			result=this.avgPayMoneyBatchJobDAO.getAvgPayMoneyOfCurrentMonthTotal(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgPayMoneyOfCurrentMonthTotal)");
		}
		
		if(result==null) {
			result=new AvgPayMoneyBatchJobTo();
		}
		
		return result;
	}
	
	public List<AvgPayMoneyBatchJobTo> getPayMoneyPayCountCurrentMonth(Context context) {
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		try {
			resultList=this.avgPayMoneyBatchJobDAO.getPayMoneyPayCountCurrentMonth(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getPayMoneyPayCountCurrentMonth)");
		}
		
		if(resultList==null) {
			resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
		}
		
		return resultList;
	}
	
	public List<String> getDateList1() {
		return this.avgPayMoneyBatchJobDAO.getDateList1();
	}
	/**
	 * 办事处全年
	 * @param context
	 * @return
	 */
	public List<AvgPayMoneyBatchJobTo> getAvgPayMoneyYearList(Context context) {
		
		String log="employeeId="+context.contextMap.get("employeeId")+"......getAvgPayMoneyYearList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<AvgPayMoneyBatchJobTo> resultList=null;
		
		try {
			resultList=this.avgPayMoneyBatchJobDAO.getAvgPayMoneyYearList(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgPayMoneyYearList)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	}
	/**
	 * 全年
	 * @param context
	 * @return
	 */
	public AvgPayMoneyBatchJobTo getAvgYear(Context context) {
		
		AvgPayMoneyBatchJobTo result=null;
		try {
			result=this.avgPayMoneyBatchJobDAO.getAvgYear(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--各区平均单价表出错!请联系管理员(getAvgYear)");
		}
		
		if(result==null) {
			result=new AvgPayMoneyBatchJobTo();
		}
		
		return result;
	}
	
	public void computeMoneyCount() throws Exception {
		try {
			List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
			
			result=this.avgPayMoneyBatchJobDAO.getComputeMoneyCount();
			
			for(int i=0;i<result.size();i++) {
				this.avgPayMoneyBatchJobDAO.insertComputeMoneyCount(result.get(i));
			}
		} catch(Exception e) {
			throw e;
		}
	}
}
