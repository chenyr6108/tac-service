package com.brick.batchjob.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.batchjob.to.AchievementBatchJobTo;

public class AchievementBatchJobDAO extends BaseDAO {

	public List<AchievementBatchJobTo> getAchievementBatchJobData(Map<String,String> paramMap) throws Exception {
		
		List<AchievementBatchJobTo> result=this.getSqlMapClientTemplate().queryForList("achievement.getAchievementBatchJobData",paramMap);
		
		if(result==null) {
			result=new ArrayList<AchievementBatchJobTo>();
		}
		
		return result;
	}
	
	public void insertAchievementBatchJobData(AchievementBatchJobTo achievementBatchJobTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("achievement.insertAchievementBatchJobData",achievementBatchJobTo);
	}
	
}
