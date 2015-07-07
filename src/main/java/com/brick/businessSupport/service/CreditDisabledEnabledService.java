package com.brick.businessSupport.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.businessSupport.dao.CreditDisabledEnabledDAO;
import com.brick.businessSupport.to.CreditTo;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class CreditDisabledEnabledService extends BaseService {

	Log logger=LogFactory.getLog(CreditDisabledEnabledService.class);
	
	private BaseDAO baseDAO;
	private CreditDisabledEnabledDAO creditDisabledEnabledDAO;

	public CreditDisabledEnabledDAO getCreditDisabledEnabledDAO() {
		return creditDisabledEnabledDAO;
	}

	public void setCreditDisabledEnabledDAO(
			CreditDisabledEnabledDAO creditDisabledEnabledDAO) {
		this.creditDisabledEnabledDAO = creditDisabledEnabledDAO;
	}
	
	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	
	public List<CreditTo> queryForPage(Context context,Map<String,Object> outputMap) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCreditList";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<CreditTo> resultList=null;
		
		try {
			resultList=(List<CreditTo>)this.baseDAO.queryForPage("creditReportManage.getCreditList","creditReportManage.getCreditCount",context,outputMap);
		} catch (DaoException e) {
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		return resultList;
	}
}
