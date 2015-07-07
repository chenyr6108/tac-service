package com.brick.bussinessReport.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.CustomerVisitReportDAO;
import com.brick.bussinessReport.to.CustomerVisitTO;

public class CustomerVisitReportService extends BaseService {

	Log logger=LogFactory.getLog(CustomerVisitReportService.class);
			
	private CustomerVisitReportDAO customerVisitReportDAO;

	public CustomerVisitReportDAO getCustomerVisitReportDAO() {
		return customerVisitReportDAO;
	}

	public void setCustomerVisitReportDAO(
			CustomerVisitReportDAO customerVisitReportDAO) {
		this.customerVisitReportDAO = customerVisitReportDAO;
	}
	
	@Transactional
	public void batchJobForCustomerVisitReport() throws Exception {
		
		if(logger.isDebugEnabled()) {
			if(logger.isDebugEnabled()) {
				logger.debug("-------------客户拜访计划外出时间batch job 开始-------------");
			}
		}
		
		List<CustomerVisitTO> insertList=null;
		
		try {
			insertList=this.customerVisitReportDAO.getCustomerVisitTime();
			
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.customerVisitReportDAO.insertCustomerVisitTime(insertList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
		} catch(Exception e) {
			logger.debug("-------------客户拜访计划外出时间batch job 失败-------------");
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			if(logger.isDebugEnabled()) {
				logger.debug("-------------客户拜访计划外出时间batch job 结束-------------");
			}
		}
	}
	
	public List<CustomerVisitTO> getDateList() throws Exception {
		
		return this.customerVisitReportDAO.getDateList();
	}
	
	public Integer getDayCount(Map contextMap) throws Exception {
		return this.customerVisitReportDAO.getDayCount(contextMap);
	}
}
