package com.brick.job.listener;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

import com.brick.base.exception.DaoException;
import com.brick.job.service.JobService;
import com.brick.job.to.JobTo;
import com.brick.util.DateUtil;

public class TriggerListenerForLog extends TriggerListenerSupport {
	
	private JobService jobService;
	
	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}

	@Override
	public String getName() {
		return "logTriggerLis";
	}

	/*@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			int triggerInstructionCode) {
		try {
			JobTo jobTo = new JobTo();
			jobTo.setJobName(context.getJobDetail().getName());
			jobTo.setPreviousTime(context.getPreviousFireTime());
			jobTo.setNextTime(context.getNextFireTime());
			jobTo.setFireTime(context.getFireTime());
			jobService.updateJobRunTime(jobTo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		boolean flag = false;
		try {
			Integer runFlag = jobService.getJobRunFlag(trigger.getJobName());
			if (runFlag == 1) {
				flag = true;
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	

}
