package com.brick.bussinessReport.command;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.batchjob.service.SupplerContributeService;
import com.brick.batchjob.to.SupplerContributeTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class SupplerContributeCommand extends BaseCommand {

	Log logger=LogFactory.getLog(SupplerContributeCommand.class);

	private SupplerContributeService supplerContributeService;
	private BaseDAO baseDAO;

	public SupplerContributeService getSupplerContributeService() {
		return supplerContributeService;
	}
	public void setSupplerContributeService(
			SupplerContributeService supplerContributeService) {
		this.supplerContributeService = supplerContributeService;
	}
	public BaseDAO getBaseDAO() {
		return baseDAO;
	}
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	public void query(Context context) throws Exception {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		try {
			/*//点击排序
			if(context.contextMap.get("ORDER_TYPE")==null||"".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","ROUND(SUM(PAY_MONEY),0)");
				context.contextMap.put("SORT","DESC");
				outputMap.put("payMoneySort","DESC");
			} else if("leaseCount".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","LEASE_COUNT");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("leaseCountSort",context.contextMap.get("SORT"));
			} else if("payMoney".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","ROUND(SUM(PAY_MONEY),0)");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("payMoneySort",context.contextMap.get("SORT"));
			} else if("equCount".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","EQUIPMENT_COUNT");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("equCountSort",context.contextMap.get("SORT"));
			} else if("accrual".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","ACCRUAL");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("accrualSort",context.contextMap.get("SORT"));
			} else if("tr".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","ROUND(AVG(TR),2)");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("trSort",context.contextMap.get("SORT"));
			} else if("grantPrice".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","GRANT_PRICE");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("grantPriceSort",context.contextMap.get("SORT"));
			} else if("restGrantPrice".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","REST_GRANT_PRICE");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("restGrantPriceSort",context.contextMap.get("SORT"));
			} else if("dunCountBySupl".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","SUM(DUN_COUNT_BY_SUPL)");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("dunCountBySuplSort",context.contextMap.get("SORT"));
			} else if("percent".equals(context.contextMap.get("ORDER_TYPE"))) {
				context.contextMap.put("ORDER_TYPE","SUM(DUN_COUNT_BY_SUPL)/(LEASE_COUNT+0.0)*100");
				context.contextMap.put("SORT",context.contextMap.get("SORT")==null?"DESC":context.contextMap.get("SORT"));
				outputMap.put("percentSort",context.contextMap.get("SORT"));
			}*/
			if(context.contextMap.get("DATE")==null) {
				Calendar c=Calendar.getInstance();
				c.add(Calendar.DATE,-1);
				java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
				outputMap.put("DATE",df.format(c.getTime()));
			} else {
				outputMap.put("DATE",context.contextMap.get("DATE"));
			}
			PagingInfo<Object> pagingInfo = baseService.queryForListWithPaging("businessReport.getSuplContributeTotal", context.contextMap, "leaseCount", ORDER_TYPE.DESC);
			outputMap.put("SUPL_NAME",context.contextMap.get("SUPL_NAME"));
			outputMap.put("pagingInfo",pagingInfo);
			Output.jspOutput(outputMap,context,"/supplerContribute/supplerContributeTotal.jsp");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw e;
		}
	}
	
	public void queryDetailBySuplId(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......queryDetailBySuplId";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<SupplerContributeTo> resultList=null;
		
		try {
			context.contextMap.put("TYPE","供应商保证");
			resultList = this.supplerContributeService.getDetailBySuplId(context);
			for (SupplerContributeTo sc : resultList) {
				sc.setRestMoney(LeaseUtil.getRemainingPrincipalByRecpId(sc.getRecpId()));
			}
			
			//通过recp_id拿到recp_code
			outputMap.put("resultList",resultList);
			
			if(context.errList.isEmpty()) {
				outputMap.put("suplId",context.contextMap.get("suplId"));
				Output.jspOutput(outputMap,context,"/supplerContribute/supplerContributeDetail.jsp");
			} else {
				outputMap.put("errList",context.errList);
				Output.jspOutput(outputMap,context,"/error.jsp");
			}
		} catch (DaoException e) {
			context.errList.add("供应商贡献度出错!请联系管理员(queryDetailBySuplId)");
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
