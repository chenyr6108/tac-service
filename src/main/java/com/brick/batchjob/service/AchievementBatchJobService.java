package com.brick.batchjob.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.AchievementBatchJobDAO;
import com.brick.batchjob.to.AchievementBatchJobTo;
import com.brick.log.service.LogPrint;

public class AchievementBatchJobService extends BaseService {

	Log logger = LogFactory.getLog(AchievementBatchJobService.class);
	
	private AchievementBatchJobDAO achievementBatchJobDAO;

	public AchievementBatchJobDAO getAchievementBatchJobDAO() {
		return achievementBatchJobDAO;
	}

	public void setAchievementBatchJobDAO(
			AchievementBatchJobDAO achievementBatchJobDAO) {
		this.achievementBatchJobDAO = achievementBatchJobDAO;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for achievement start  --------------------");
		}
		
		Map<String,String> paramMap=new HashMap<String,String>();
		paramMap.put("CONTRACT_TYPE","融资租赁合同类型");
		paramMap.put("PRINT","导出 融资租赁合同");
		
		try {
			List<AchievementBatchJobTo> result=this.achievementBatchJobDAO.getAchievementBatchJobData(paramMap);
			
			for(int i=0;i<result.size();i++) {
				Thread.sleep(1);//防止主键重复
				result.get(i).setAchievementId(String.valueOf(System.currentTimeMillis()));
				this.achievementBatchJobDAO.insertAchievementBatchJobData(result.get(i));
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for achievement end  --------------------");
		}
	}
}
