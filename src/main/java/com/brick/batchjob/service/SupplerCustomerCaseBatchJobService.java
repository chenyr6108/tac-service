package com.brick.batchjob.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.SupplerCustomerCaseBatchJobDAO;
import com.brick.batchjob.to.SuplCustCaseTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class SupplerCustomerCaseBatchJobService extends BaseService {

	Log logger=LogFactory.getLog(SupplerCustomerCaseBatchJobService.class);
			
	private SupplerCustomerCaseBatchJobDAO supplerCustomerCaseBatchJobDAO;
	private BaseDAO baseDAO;
	
	public SupplerCustomerCaseBatchJobDAO getSupplerCustomerCaseBatchJobDAO() {
		return supplerCustomerCaseBatchJobDAO;
	}

	public void setSupplerCustomerCaseBatchJobDAO(
			SupplerCustomerCaseBatchJobDAO supplerCustomerCaseBatchJobDAO) {
		this.supplerCustomerCaseBatchJobDAO = supplerCustomerCaseBatchJobDAO;
	}

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Transactional(rollbackFor=Exception.class)
	public void batchJob() {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 供应商客户案况进度 start  --------------------");
		}
		
		List<SuplCustCaseTo> suplCustCaseList=null;
		List<SuplCustCaseTo> dunMoneyByCustList=null;
		List<Map<String,Object>> brandList=null;//供应商
		
		try {
			
			suplCustCaseList=this.supplerCustomerCaseBatchJobDAO.getSuplCustCase();
			dunMoneyByCustList=this.supplerCustomerCaseBatchJobDAO.getDunMoneyByCust();
			
			brandList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getBrand",null,RS_TYPE.LIST);
			
			Map<String,Object> restPeriod=new HashMap<String,Object>();
			for(int i=0;i<suplCustCaseList.size();i++) {
				//加入逾期金额
				if(suplCustCaseList.get(i).getLeaseCode()!=null) {
					for(int j=0;j<dunMoneyByCustList.size();j++) {
						if(suplCustCaseList.get(i).getCustId().equals(dunMoneyByCustList.get(j).getCustId())
								&&suplCustCaseList.get(i).getLeaseCode().equals(dunMoneyByCustList.get(j).getLeaseCode())) {
							suplCustCaseList.get(i).setDunMoney(dunMoneyByCustList.get(j).getDunMoney());
						}
					}
				}
				
				if(suplCustCaseList.get(i).getRecpId()!=null&&suplCustCaseList.get(i).getRectId()!=null) {
					//加入剩余期数
					/*restPeriod.put("C","租金");
					restPeriod.put("RECP_ID",suplCustCaseList.get(i).getRecpId());
					restPeriod.put("RECT_ID",suplCustCaseList.get(i).getRectId());
					restPeriod=(Map<String,Object>)DataAccessor.query("applyCompanyManage.findContractNoPayByContractId",restPeriod,DataAccessor.RS_TYPE.MAP);
					if(restPeriod==null) {
						suplCustCaseList.get(i).setPayPeriod(
								String.valueOf(
										Integer.valueOf(suplCustCaseList.get(i).getTotalPeriod()==null?"0":suplCustCaseList.get(i).getTotalPeriod())));
					} else {
						suplCustCaseList.get(i).setPayPeriod(
								String.valueOf(
										Integer.valueOf(suplCustCaseList.get(i).getTotalPeriod()==null?"0":suplCustCaseList.get(i).getTotalPeriod())
										-Integer.valueOf(restPeriod.get("WEIJIAOQISHU")==null?"0":restPeriod.get("WEIJIAOQISHU")+"")));
					}*/
					
					Map<String,Object> param=new HashMap<String,Object>();
					param.put("RECP_ID",suplCustCaseList.get(i).getRecpId());
					int payPeriod=(Integer)DataAccessor.query("businessReport.getPayPeriod",param,DataAccessor.RS_TYPE.OBJECT);
					suplCustCaseList.get(i).setPayPeriod(String.valueOf(payPeriod));
				}
				
				//加入供应商
				if(suplCustCaseList.get(i).getCreditId()==null) {
					continue;
				}
				boolean flag=true;
    			for(int j=0;brandList!=null&&j<brandList.size();j++) {
    				if(suplCustCaseList.get(i).getCreditId().equals(String.valueOf(brandList.get(j).get("CREDIT_ID")))) {
    					if(flag) {
    						suplCustCaseList.get(i).setSuplName((String)brandList.get(j).get("BRAND"));
    						flag=false;
    					} else {
    						suplCustCaseList.get(i).setSuplName(suplCustCaseList.get(i).getSuplName()+","+brandList.get(j).get("BRAND"));
    					}
    				}
    			}
			}
			
			//插入数据
			for(int i=0;i<suplCustCaseList.size();i++) {
				Thread.sleep(1);//防止主键重复
				suplCustCaseList.get(i).setSuplCustCaseId(String.valueOf(System.currentTimeMillis()));
				this.supplerCustomerCaseBatchJobDAO.insertSupplerCustomerCase(suplCustCaseList.get(i));
			}
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 供应商客户案况进度 end  --------------------");
		}
	}
	
	public List<SuplCustCaseTo> query(Context context,Map<String,Object> outputMap) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<SuplCustCaseTo> resultList=null;
		
		try {
			resultList=(List<SuplCustCaseTo>)this.baseDAO.queryForPage("businessReport.getSupplerCustomerCase","businessReport.getSupplerCustomerCaseCount",context,outputMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			context.errList.add("供应商客户案况进度出错!请联系管理员");
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<SuplCustCaseTo>();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	}
	
	public List<Map<String,Object>> getCaseTypeList(Context context) {
		
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=this.supplerCustomerCaseBatchJobDAO.getCaseTypeList(context);
		} catch (Exception e) {
			LogPrint.getLogStackTrace(e,logger);
			context.errList.add("供应商客户案况进度出错!请联系管理员");
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
}
