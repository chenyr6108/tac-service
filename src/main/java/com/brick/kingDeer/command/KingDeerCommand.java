package com.brick.kingDeer.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.coderule.service.CodeRule;
import com.brick.kingDeer.service.KingDeerService;
import com.brick.log.service.LogPrint;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class KingDeerCommand extends BaseCommand {

	Log logger=LogFactory.getLog(this.getClass());
	private KingDeerService kingDeerService;
	
	public KingDeerService getKingDeerService() {
		return kingDeerService;
	}

	public void setKingDeerService(KingDeerService kingDeerService) {
		this.kingDeerService = kingDeerService;
	}

	//点击批量抛转查看待批量抛转的数据,进行删除或者抛转
	public void paymentBatchQuery(Context context) {
		List<Map<String,Object>> resultList=null;
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<String> ids=(List<String>)context.request.getSession().getAttribute("batchIds");//通过session中获得添加的批量数据
		if(ids!=null) {
			String [] idsForBatchQuery=new String [ids.size()];
			for(int i=0;i<ids.size();i++) {
				idsForBatchQuery[i]=ids.get(i);
			}
			param.put("idsForBatchQuery",idsForBatchQuery);
		}		
		try {
			if("CAR".equals(context.contextMap.get("flag").toString())) {//乘用车拨款
				resultList=this.kingDeerService.batchQueryForCar(param);
			} else if("EQU".equals(context.contextMap.get("flag").toString())) {//设备拨款
				
			} else if("MOTOR".equals(context.contextMap.get("flag").toString())) {//重车拨款
				
			}
		} catch(Exception e) {
			
		}
		outputMap.put("batchSettleDate",DateUtil.getCurrentDate());
		outputMap.put("flag",context.contextMap.get("flag").toString());
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/kingDeer/paymentBatchDetail.jsp");
	}
	
	public void delBatchId(Context context) {//删除批量数据中的某条数据
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<String> ids=(List<String>)context.request.getSession().getAttribute("batchIds");//通过session中获得添加的批量数据
		for(int i=0;ids!=null&&i<ids.size();i++) {
			if(ids.get(i).toString().equals(context.contextMap.get("id")+"")) {
				ids.remove(i);
				break;
			}
		}
		if(ids!=null&&ids.size()==0) {
			context.request.getSession().removeAttribute("batchIds");
			context.request.getSession().removeAttribute("batchPayDate");
			context.request.getSession().removeAttribute("batchCardFlag");
		} else {
			context.request.getSession().setAttribute("batchIds",ids);
		}
		outputMap.put("flag",context.contextMap.get("flag").toString());
		Output.jsonOutput(outputMap,context);
	}
	
	public void delBatchIds(Context context) {//删除批量数据中的所有数据
		context.request.getSession().removeAttribute("batchIds");
		context.request.getSession().removeAttribute("batchPayDate");
		context.request.getSession().removeAttribute("batchCardFlag");
		Output.jsonOutput(new HashMap<String,Object>(),context);
	}
	
	public void paymentQuery(Context context) {//抛转页面查询
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> pagingInfo=null;
		
		if(context.request.getSession().getAttribute("batchCardFlag")!=null
				 &&!(context.request.getSession().getAttribute("batchCardFlag")+"").equals(context.contextMap.get("cardFlag")+"")) {//当使用tab切换类型时候,清除批量添加的数据
			context.request.getSession().removeAttribute("batchIds");
			context.request.getSession().removeAttribute("batchPayDate");
			context.request.getSession().removeAttribute("batchCardFlag");
		}
		
		if(context.contextMap.get("cardFlag")==null) {
			context.contextMap.put("cardFlag","0");
			context.contextMap.put("payDate",DateUtil.getYesterday());
			context.contextMap.put("companyCode","1");
			context.contextMap.put("contractType",-1);
			pagingInfo=baseService.queryForListWithPaging("kingDeer.paymentQueryForCar",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		} else if("0".equals(context.contextMap.get("cardFlag"))) {//乘用车拨款
			
			pagingInfo=baseService.queryForListWithPaging("kingDeer.paymentQueryForCar",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		} else if("1".equals(context.contextMap.get("cardFlag"))) {//设备拨款
			//TODO
			pagingInfo=baseService.queryForListWithPaging("kingDeer.paymentQueryForEqu",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		} else if("2".equals(context.contextMap.get("cardFlag"))) {//商用车拨款
			//TODO
			pagingInfo=baseService.queryForListWithPaging("kingDeer.paymentQueryForMotor",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		}
		
		for(int i=0;i<pagingInfo.getResultList().size();i++) {
			List<String> idsList=(List<String>)context.request.getSession().getAttribute("batchIds");
			if(idsList==null) {
				break;
			}
			String batchPayDate=context.request.getSession().getAttribute("batchPayDate")+"";
			for(int j=0;idsList!=null&&j<idsList.size();j++) {
				if(idsList.get(j).equals(((Map<String,Object>)pagingInfo.getResultList().get(i)).get("ID")+"")||!batchPayDate.equals(context.contextMap.get("payDate")+"")) {
					//如果此数据已经添加进批量列表或者此数据的拨款日期与批量的拨款日期不同,则锁住不予添加
					((Map<String,Object>)pagingInfo.getResultList().get(i)).put("LOCK","Y");
					break;
				}
			}
		}
		outputMap.put("settleDate",DateUtil.getCurrentDate());
		outputMap.put("dw",pagingInfo);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("companyCode",context.contextMap.get("companyCode"));
		outputMap.put("payDate",context.contextMap.get("payDate"));
		outputMap.put("contractType",context.contextMap.get("contractType"));
		outputMap.put("cardFlag",context.contextMap.get("cardFlag"));//0乘用车,1设备,2商用车
		outputMap.put("__action",context.contextMap.get("__action"));
		
		Output.jspOutput(outputMap,context,"/kingDeer/paymentQuery.jsp");
	}
	
	public void checkResult(Context context) {//验证是否已经抛转
		Output.jsonFlageOutput(this.kingDeerService.checkResult(context),context);
	}
	
	public void generateVoucherForPayment(Context context) {//生成拨款类凭证
		try {
			if("0".equals(context.contextMap.get("cardFlag"))) {//乘用车拨款
				this.kingDeerService.generateVoucherForPaymentOfCar(context);
			} else if("1".equals(context.contextMap.get("cardFlag"))) {//设备拨款
				this.kingDeerService.generateVoucherForPaymentOfEqu(context);
			} else if("2".equals(context.contextMap.get("cardFlag"))) {//商用车拨款
				this.kingDeerService.generateVoucherForPaymentOfMotor(context);
			}
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e,logger);
		}
		Output.jsonOutput(context.contextMap,context);
	}
	
	public void batchGenerateVoucherForPayment(Context context) {//批量生成拨款类凭证
		
		try {
			if("CAR".equals(context.contextMap.get("flag"))) {//乘用车拨款
				this.kingDeerService.batchGenerateVoucherForPaymentOfCar(context);
			} else if("EQU".equals(context.contextMap.get("flag"))) {//设备拨款
				
			} else if("MOTOR".equals(context.contextMap.get("flag"))) {//商用车拨款
				
			}
		} catch(Exception e) {
			LogPrint.getLogStackTrace(e,logger);
		}
		context.request.getSession().removeAttribute("batchIds");
		context.request.getSession().removeAttribute("batchPayDate");
		context.request.getSession().removeAttribute("batchCardFlag");
		context.contextMap.put("msg",context.contextMap.get("msg"));
		Output.jsonOutput(context.contextMap,context);
	}
	
	//批量添加凭证
	public void batchAdd(Context context) {
		List<String> idsList=null;
		String ids=context.contextMap.get("ids")+"";
		if(context.request.getSession().getAttribute("batchIds")==null) {
			idsList=new ArrayList<String>();
		} else {
			idsList=(List<String>)context.request.getSession().getAttribute("batchIds");
		}
		
		for(int i=0;i<ids.split("-").length;i++) {
			idsList.add(String.valueOf(ids.split("-")[i]));
		}
		context.request.getSession().setAttribute("batchIds",idsList);//加入添加的批量数据
		//批量数据的拨款日期一定是相同的,加入拨款日期为了页面卡关不能添加其他拨款日的数据
		context.request.getSession().setAttribute("batchPayDate",context.contextMap.get("payDate"));
		context.request.getSession().setAttribute("batchCardFlag",context.contextMap.get("cardFlag"));
		context.contextMap.put("msg","添加成功");
		Output.jsonOutput(context.contextMap,context);
	}
}
