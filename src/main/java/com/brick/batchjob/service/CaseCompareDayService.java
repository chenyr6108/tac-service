package com.brick.batchjob.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.CaseCompareDayDAO;
import com.brick.batchjob.to.CaseCompareDayTo;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class CaseCompareDayService extends BaseService {

	Log logger=LogFactory.getLog(CaseCompareDayService.class);

	private CaseCompareDayDAO caseCompareDayDAO;

	public CaseCompareDayDAO getCaseCompareDayDAO() {
		return caseCompareDayDAO;
	}

	public void setCaseCompareDayDAO(CaseCompareDayDAO caseCompareDayDAO) {
		this.caseCompareDayDAO = caseCompareDayDAO;
	}

	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case compare day start  --------------------");
		}

		List<CaseCompareDayTo> caseCompareDayList=null;
		Map<String,String> param=new HashMap<String,String>();
		param.put("TITLE","初审");

		try {
			caseCompareDayList=this.caseCompareDayDAO.getCaseCompareDay(param);

			for(int i=0;i<caseCompareDayList.size();i++) {
				Thread.sleep(1);//防止主键重复
				caseCompareDayList.get(i).setCaseCompareDayId(String.valueOf(System.currentTimeMillis()));
				this.caseCompareDayDAO.insertCaseCompareDay(caseCompareDayList.get(i));
			}
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		} catch (InterruptedException e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}

		if(logger.isDebugEnabled()) {
			logger.debug("batch job for case compare day end  --------------------");
		}
	}

	public List<CaseCompareDayTo> queryCaseCompareDay(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......queryCaseCompareDay";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<CaseCompareDayTo> resultList=null;
		
		try {
			resultList=this.caseCompareDayDAO.queryCaseCompareDay(context.contextMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
			context.errList.add("数据分析--案件进度时间比较表!请联系管理员(queryCaseCompareDay)");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	}
	
	public Map<String,List<CaseCompareDayTo>> getCaseCompareDayFilter() {
		
		Map<String,List<CaseCompareDayTo>> resultMap=null;
		
		try {
			resultMap=this.caseCompareDayDAO.getCaseCompareDayFilter();
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		return resultMap;
	}
}
