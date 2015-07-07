package com.brick.job.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.job.service.JobService;
import com.brick.job.to.JobTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class JobCommand extends BaseCommand {
	private JobService jobService;

	public JobService getJobService() {
		return jobService;
	}

	public void setJobService(JobService jobService) {
		this.jobService = jobService;
	}
	
	public void getAllJobs(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		try {
			if(context.contextMap.get("min")==null||"".equals(context.contextMap.get("min"))) {
				context.contextMap.put("min",30);
			}
			PagingInfo<Object> pagingInfo = baseService.queryForListWithPaging("job.getAllJobs", context.contextMap, "NEXT_TIME");
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("min", context.contextMap.get("min"));
			outputMap.put("__action", "jobCommand.getAllJobs");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/quartzJob/quartzJobManager.jsp");
	}
	
	public void showJobDetail(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		try {
			paraMap.put("jobName", context.contextMap.get("jobName"));
			List<Map<String, Object>> detail = (List<Map<String,Object>>) DataAccessor.query("job.getJobDetail", paraMap, RS_TYPE.LIST);
			outputMap.put("detail", detail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/quartzJob/quartzJobDetail.jsp");
	}
	
	public void getAllDetail(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		try {
			paraMap.put("jobName", context.contextMap.get("jobName"));
			List<JobTo> detail = (List<JobTo>) DataAccessor.query("job.getAllJobDetail", paraMap, RS_TYPE.LIST);
			outputMap.put("detail", detail);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/quartzJob/quartzJobDetailAll.jsp");
	}
	
	public void updateJobRunFlag(Context context){
		String job_id = (String) context.contextMap.get("job_id");
		String run_flag = (String) context.contextMap.get("run_flag");
		JobTo jobTo = new JobTo();
		jobTo.setJobId(job_id);
		jobTo.setRunFlag(Integer.valueOf(run_flag));
		try {
			jobService.updateJobRunFlag(jobTo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.getAllJobs(context);
		}
	}
}
