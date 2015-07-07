package com.brick.financialReport.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.financialReport.dao.InterestIncomeReportDAO;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class InterestIncomeReportService extends BaseService {
	
		private InterestIncomeReportDAO interestIncomeReportDAO;
		
		public InterestIncomeReportDAO getInterestIncomeReportDAO() {
			return interestIncomeReportDAO;
		}

		public void setInterestIncomeReportDAO(
				InterestIncomeReportDAO interestIncomeReportDAO) {
			this.interestIncomeReportDAO = interestIncomeReportDAO;
		}

		//利息收入明细batch job
		@Transactional
		public void generateInterestIncomeReport() throws Exception {
			try {
				
				//获得昨天新生成合同的支付表利息明细
				this.generateInterestIncomePay();
			} catch(Exception e) {
				throw e;
			}
		}
		
		private void generateInterestIncomePay() throws Exception {
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("startDate",DateUtil.dateToStr(cal.getTime()));
			param.put("endDate",DateUtil.dateToStr(cal.getTime()));
			
			try {
				List<Map<String,Object>> resultList=this.interestIncomeReportDAO.getHistoryInterestIncomePay(param);
				
				for(int i=0;resultList!=null&&i<resultList.size();i++) {
					resultList.get(i).put("s_employeeId",184);
				    this.interestIncomeReportDAO.generateHistoryInterestIncomePay(resultList.get(i));
				}
			} catch(Exception e) {
				throw e;
			}
		}
		
		//手动生成利息费明细的支付表
		public void generateHistoryInterestIncomePay(Context context) throws Exception {
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("startDate",context.contextMap.get("startDate"));
			param.put("endDate",context.contextMap.get("endDate"));
			param.put("recpCode",context.contextMap.get("recpCode"));
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			
			try {
				List<Map<String,Object>> resultList=this.interestIncomeReportDAO.getHistoryInterestIncomePay(param);
				
				String lastRecpId="";
				String currentRecpId="";
				
				for(int i=0;resultList!=null&&i<resultList.size();i++) {
					
					currentRecpId=resultList.get(i).get("RECP_ID").toString();
					if(!lastRecpId.equals(currentRecpId)) {
						param.put("recpId",currentRecpId);
						int flag=this.interestIncomeReportDAO.checkHistoryInterestIncomePay(param);
						lastRecpId=currentRecpId;
						
						if(flag>0) {
							this.interestIncomeReportDAO.cancelHistoryInterestIncomePay(param);
						}
					}
					
					resultList.get(i).put("s_employeeId",context.contextMap.get("s_employeeId"));
				    this.interestIncomeReportDAO.generateHistoryInterestIncomePay(resultList.get(i));
				}
			} catch(Exception e) {
				throw e;
			}
		}
}
