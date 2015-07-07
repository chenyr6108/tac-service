package com.brick.batchjob.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.ReportCancelBatchJobDAO;
import com.brick.util.DataUtil;

public class ReportCancelBatchJobService extends BaseService {
		
	Log logger = LogFactory.getLog(ReportCancelBatchJobService.class);
	
	private ReportCancelBatchJobDAO reportCancelBatchJobDAO;

	public ReportCancelBatchJobDAO getReportCancelBatchJobDAO() {
		return reportCancelBatchJobDAO;
	}

	public void setReportCancelBatchJobDAO(
			ReportCancelBatchJobDAO reportCancelBatchJobDAO) {
		this.reportCancelBatchJobDAO = reportCancelBatchJobDAO;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for report cancel start  --------------------");
		}
		
		try {
			List<Integer> result=this.reportCancelBatchJobDAO.getInvalidReportId();
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("CODE",-1);
			param.put("CASESTATE","单位主管自行退件");
			
			for(int i=0;i<result.size();i++) {
				param.put("CREDIT_ID",result.get(i));
				this.reportCancelBatchJobDAO.cancelCreditByCreditId(param);
				this.reportCancelBatchJobDAO.cancelCaseByCreditId(param);
				
				param.put("creditId",DataUtil.longUtil(result.get(i)));
				param.put("contractId",0);
				param.put("logType","报告管理");
				param.put("logTitle","报告撤销");
				param.put("state",1);
				param.put("memo","此报告1个月内没有任何操作,维护,搜寻,系统自动撤销");
				param.put("userId",DataUtil.longUtil("184"));//184是系统自动跑的Batch Job
				this.reportCancelBatchJobDAO.insertLog(param);
			}
		} catch(Exception e) {
			throw e;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for report cancel end  --------------------");
		}
	}
}
