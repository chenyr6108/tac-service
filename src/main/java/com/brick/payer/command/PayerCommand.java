package com.brick.payer.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.payer.service.PayerService;
import com.brick.payer.to.PayerTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;


public class PayerCommand extends BaseCommand{
	
	private PayerService payerService;

	public void getPayers(Context context) throws Exception{
				
		String creditId = (String) context.contextMap.get("creditId");
		List<PayerTo> payers = payerService.getPayersByCreditId(Integer.parseInt(creditId));
		Map outputMap = new HashMap();
		outputMap.put("payers", payers);
		outputMap.put("creditId", creditId);
		Output.jspOutput(outputMap, context, "/rentcontract/payers.jsp");
	}
	
	public void savePayer(Context context)throws Exception{
		String creditId = (String) context.contextMap.get("creditId");
		String name = (String) context.contextMap.get("name");
		String mobile = (String) context.contextMap.get("mobile");
		String payerId = (String) context.contextMap.get("payerId");
		int userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		if(payerId!=null && !"".equals(payerId)){//更新
			PayerTo payer = new PayerTo();
			payer.setModifyBy(userid);
			payer.setLinkmanName(name);
			payer.setLinkmanMobile(mobile);
			payer.setCreditId(Integer.parseInt(creditId));
			payer.setId(Integer.parseInt(payerId));
			payerService.updatePayer(payer);
		}else{//保存
			PayerTo payer = new PayerTo();
			payer.setCreateBy(userid);
			payer.setLinkmanName(name);
			payer.setLinkmanMobile(mobile);
			payer.setCreditId(Integer.parseInt(creditId));
			payerService.savePayer(payer);
		}
		Map outputMap = new HashMap();
		outputMap.put("creditId", creditId);
		Output.jsonOutput(outputMap, context);
	}
	
	public void deletePayer(Context context){
		String payerId = (String) context.contextMap.get("payerId");
		int userid = (Integer) context.request.getSession().getAttribute("s_employeeId");
		PayerTo payer = new PayerTo();
		payer.setModifyBy(userid);
		payer.setId(Integer.parseInt(payerId));
		payerService.deletePayer(payer);
		Output.jsonFlageOutput(true, context);
	}
	
	public PayerService getPayerService() {
		return payerService;
	}

	public void setPayerService(PayerService payerService) {
		this.payerService = payerService;
	}
}
