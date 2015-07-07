package com.brick.job;

import org.quartz.impl.StdScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brick.job.service.JobService;

public class QuartzExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("======================Quartz Start=======================");
		try {
			ApplicationContext appContext = new ClassPathXmlApplicationContext("/config/quartz/app-context-quartz.xml");
			StdScheduler quartz = (StdScheduler) appContext.getBean("startQuertz");
			JobService jobService = (JobService) appContext.getBean("jobService");
			if (jobService == null) {
				throw new Exception("Job Service was not found.");
			}
			jobService.initJobDetail(quartz);
			System.out.println("===================Quartz Start success==================");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("===================Quartz Start failed==================");
		}
	}

}
