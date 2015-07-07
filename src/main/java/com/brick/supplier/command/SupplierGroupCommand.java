package com.brick.supplier.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.supplier.service.SupplierGroupService;
import com.brick.supplier.to.SupplierGroupTO;

public class SupplierGroupCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(SupplierGroupCommand.class);
	
	private SupplierGroupService supplierGroupService;

	public SupplierGroupService getSupplierGroupService() {
		return supplierGroupService;
	}

	public void setSupplierGroupService(SupplierGroupService supplierGroupService) {
		this.supplierGroupService = supplierGroupService;
	}
	
	public void query(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		PagingInfo<Object> pagingInfo=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			pagingInfo=baseService.queryForListWithPaging("supplier.getSuplGroupInfo",context.contextMap,"suplCount",ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			context.errList.add("供应商集团查询出错,请联系管理员!");
			logger.debug("供应商集团查询出错,请联系管理员!");
			throw e;
		}
		outputMap.put("pagingInfo",pagingInfo);
		outputMap.put("supl_name", context.contextMap.get("supl_name"));
		outputMap.put("group_name", context.contextMap.get("group_name"));
		Output.jspOutput(outputMap,context,"/supplierGroup/supplierGroup.jsp");
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	//添加供应商集团
	public void addSuplGroup(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......addSuplGroup";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		context.contextMap.put("suplGroupCode", String.valueOf(System.currentTimeMillis()));
		boolean flag=false;
		
		try {
			this.supplierGroupService.addSuplGroup(context);
			flag=true;
		} catch (Exception e) {
			context.errList.add("供应商集团添加出错,请联系管理员!");
			logger.debug("供应商集团添加出错,请联系管理员!");
			throw e;
		}
		Output.jsonFlageOutput(flag,context);
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getSupl(Context context) throws Exception {
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getSupl";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<SupplierGroupTO> resultList=null;
		try {
			resultList=this.supplierGroupService.getSupl(context);
		} catch (Exception e) {
			context.errList.add("获得供应商出错,请联系管理员!");
			logger.debug("获得供应商出错,请联系管理员!");
			throw e;
		}
		
		Output.jsonArrayOutputForObject(resultList,context);
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	//获得供应商List
	public void getSuplList(Context context) throws Exception {
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getSuplList";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<SupplierGroupTO> resultList=null;
		try {
			resultList=this.supplierGroupService.getSuplList(context);
		} catch (Exception e) {
			context.errList.add("获得供应商出错,请联系管理员!");
			logger.debug("获得供应商出错,请联系管理员!");
			throw e;
		}
		
		Output.jsonArrayOutputForObject(resultList,context);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void mapSuplToGroup(Context context) throws Exception {
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......mapSuplToGroup";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		/*Map<String,String> param=new HashMap<String,String>();
		String [] suplIdArray=((String)context.contextMap.get("suplId")).split("-");
		
		StringBuffer suplIds=new StringBuffer();
		for(int i=0;i<suplIdArray.length;i++) {
			if(i==suplIdArray.length-1) {
				suplIds.append("'"+suplIdArray[i]+"'");
			} else {
				suplIds.append("'"+suplIdArray[i]+"',");
			}
		}
		param.put("suplIds", suplIds.toString());
		String result=this.supplierGroupService.checkHasMaping(param);
		
		if("Y".equals(result)) {//如果此供应商已经分配到集团,不能重复分配
			Output.jsonFlageOutput(false,context);
			return;
		}*/
		boolean flag=false;
		try {
			this.supplierGroupService.insertSuplGroupMap(context);
			flag=true;
		} catch (Exception e) {
			context.errList.add("供应商添加出错,请联系管理员!");
			logger.debug("供应商添加出错,请联系管理员!");
			Output.jsonFlageOutput(flag,context);
			throw e;
		}
		Output.jsonFlageOutput(flag,context);
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getCreditDetail(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCreditDetail";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		String suplGroupName=(String)context.contextMap.get("suplGroupName");
		String suplGroupCode=(String)context.contextMap.get("suplGroupCode");
		
		//回购联保,已用额度
		List<SupplierGroupTO> notLoopTotalList=this.supplierGroupService.getNotLoopCreditAmountSum(context);
		List<SupplierGroupTO> loopTotalList=this.supplierGroupService.getLoopCreditAmountSum(context);
		
		//交机前已用额度
		SupplierGroupTO loopPayBefore=this.supplierGroupService.getPayBeforeLoop(context);
		SupplierGroupTO notLoopPayBefore=this.supplierGroupService.getPayBeforeNotLoop(context);
		
		List<SupplierGroupTO> suplList=this.supplierGroupService.getSupl(context);
		
		//SupplierGroupTO result=this.supplierGroupService.getSuplGroupCredit(context);
		
		Map<String, Object> result = (Map<String, Object>) baseService.queryForObj("supplier.getSuplGroupCreditForShow", context.contextMap);
		
		PagingInfo<Object> logInfo = (PagingInfo<Object>) baseService.queryForListWithPaging("supplier.getSuplGroupCreditForLog", context.contextMap, "CREATE_ON", ORDER_TYPE.DESC);
		
		/*outputMap.put("unionAmount",0);
		outputMap.put("buyBackAmount",0);
		outputMap.put("payBeforeAmount",0);*/
		/*String unionLoopFlag=result.getUnionLoopFlag();
		String buyBackLoopFlag=result.getBuyBackLoopFlag();
		String payBeforeLoopFlag=result.getPayBeforeLoopFlag();*/
		/*for(int i=0;i<notLoopTotalList.size();i++) {
			
			if("N".equalsIgnoreCase(unionLoopFlag)) {
				if("UNION".equalsIgnoreCase(notLoopTotalList.get(i).getFlag())) {
					outputMap.put("unionAmount",notLoopTotalList.get(i).getLeaseRze());//如果连保,并且连保授信是非循环
				}
			} else if("Y".equalsIgnoreCase(unionLoopFlag)) {
				if("UNION".equalsIgnoreCase(loopTotalList.get(i).getFlag())) {
					outputMap.put("unionAmount",loopTotalList.get(i).getRestAmount());//如果连保,并且连保授信是循环
				}
			}
			
			if("N".equalsIgnoreCase(buyBackLoopFlag)) {
				if("BUY_BACK".equalsIgnoreCase(notLoopTotalList.get(i).getFlag())) {
					outputMap.put("buyBackAmount",notLoopTotalList.get(i).getLeaseRze());//如果回购,并且回购授信是非循环
				}
				
			} else if("Y".equalsIgnoreCase(buyBackLoopFlag)) {
				if("BUY_BACK".equalsIgnoreCase(loopTotalList.get(i).getFlag())) {//如果回购,并且回购授信是循环
					outputMap.put("buyBackAmount",loopTotalList.get(i).getRestAmount());
				}
			}
		}
		
		if("N".equalsIgnoreCase(payBeforeLoopFlag)) {//非循环交机前已用金额
			outputMap.put("payBeforeAmount",notLoopPayBefore.getRestAmount());
		} else if("Y".equalsIgnoreCase(payBeforeLoopFlag)) {//循环交机前已用金额
			outputMap.put("payBeforeAmount",loopPayBefore.getRestAmount());
		}*/
		
		outputMap.put("suplList",suplList);
		outputMap.put("suplGroupCode",suplGroupCode);
		outputMap.put("suplGroupName",suplGroupName);
		outputMap.put("result",result);
		outputMap.put("logInfo",logInfo);
		Output.jspOutput(outputMap,context,"/supplierGroup/supplierGroupCredit.jsp");
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getCreditHistory(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCreditHistory";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<SupplierGroupTO> resultList=new ArrayList<SupplierGroupTO>();
		
		List<SupplierGroupTO> notLoopList=this.supplierGroupService.getNotLoopCreditAmount(context);
		
		resultList.addAll(notLoopList);
		
		outputMap.put("resultList",resultList);
		
		Output.jspOutput(outputMap,context,"/supplierGroup/supplierGroupCreditHistory.jsp");
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void addGroupCredit(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......addGroupCredit";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		this.supplierGroupService.addGroupCredit(context.contextMap);
		this.getCreditDetail(context);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getCreditLog(Context context) throws Exception {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getCreditLog";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		List<SupplierGroupTO> resultList=null;
		
		try {
			resultList=this.supplierGroupService.getCreditLog(context);
		} catch (Exception e) {
			context.errList.add("获得日志出错,请联系管理员!");
			logger.debug("获得日志出错,请联系管理员!");
			throw e;
		}
		
		Output.jsonArrayOutputForObject(resultList,context);
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
