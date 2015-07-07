package com.brick.contribution.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.contribution.dao.ContributionDao;
import com.brick.log.service.LogPrint;

public class ContributionService extends BaseService {
	Log logger = LogFactory.getLog(ContributionService.class);
	private ContributionDao contributionDao;

	public void setContributionDao(ContributionDao contributionDao) {
		this.contributionDao = contributionDao;
	}

	public void setLog(Log logger) {
		this.logger = logger;
	}
	
	public List<Map<String, Object>> getContrubutions(Map<String, Object> param) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			result = this.contributionDao.getContributions(param);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return result;
	}
	
}
