package com.brick.job.service;

import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.job.dao.JobDAO;
import com.brick.job.to.JobTo;

public class JobService extends BaseService {
	
	private JobDAO jobDAO;
	
	public JobDAO getJobDAO() {
		return jobDAO;
	}

	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}

	@Transactional(rollbackFor=Exception.class)
	public void initJobDetail(StdScheduler quartz) throws Exception{
		if (jobDAO == null) {
			throw new Exception("数据库连接失败。");
		}
		jobDAO.deleteJobs();
		String[] triggerGroupNames = quartz.getTriggerGroupNames();
		if (triggerGroupNames == null || triggerGroupNames.length <= 0) {
			throw new Exception("没有触发器。");
		}
		Trigger trigger = null;
		String[] triggerNames = null;
		JobTo jobTo = null;
		for (String triggerGroupName : triggerGroupNames) {
			triggerNames = quartz.getTriggerNames(triggerGroupName);
			for (String triggerName : triggerNames) {
				trigger = quartz.getTrigger(triggerName, triggerGroupName);
				jobTo = new JobTo();
				jobTo.setJobName(trigger.getJobName());
				jobTo.setJobGroup(trigger.getGroup());
				jobTo.setNextTime(trigger.getNextFireTime());
				jobTo.setStartTime(trigger.getStartTime());
				jobTo.setDescr(trigger.getDescription());
				jobTo.setRunFlag(0);
				jobDAO.insertJob(jobTo);
			}
		}
	}

	public Integer getJobRunFlag(String jobName) throws DaoException {
		JobTo param = new JobTo();
		param.setJobName(jobName);
		return jobDAO.getJobRunFlag(param);
	}

	@Transactional(rollbackFor=Exception.class)
	public void doJobRunLog(JobTo jobTo) throws Exception{
		jobDAO.insertJobRunLog(jobTo);
		jobDAO.updateJobRunStatus(jobTo);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updateJobRunTime(JobTo jobTo) throws Exception{
		jobDAO.updateJobRunStatus(jobTo);
	}

	@Transactional(rollbackFor=Exception.class)
	public void updateJobRunFlag(JobTo jobTo) throws Exception {
		jobDAO.updateJobRunFlag(jobTo);
	}
}
