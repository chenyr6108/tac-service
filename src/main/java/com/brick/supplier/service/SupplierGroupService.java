package com.brick.supplier.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.log.to.ActionLogTo;
import com.brick.service.entity.Context;
import com.brick.supplier.dao.SupplierGroupDAO;
import com.brick.supplier.to.SupplierGroupTO;

public class SupplierGroupService extends BaseService {
	
	private SupplierGroupDAO supplierGroupDAO;

	public SupplierGroupDAO getSupplierGroupDAO() {
		return supplierGroupDAO;
	}

	public void setSupplierGroupDAO(SupplierGroupDAO supplierGroupDAO) {
		this.supplierGroupDAO = supplierGroupDAO;
	}
	
	public List<SupplierGroupTO> getSuplGroupInfo(Context context) throws Exception {
		return this.supplierGroupDAO.getSuplGroupInfo(context);
	}
	
	@Transactional
	public void addSuplGroup(Context context) throws Exception {
		this.supplierGroupDAO.addSuplGroup(context);
		ActionLogTo actionLogTo=new ActionLogTo();
		actionLogTo.setLogBy((String)context.contextMap.get("s_employeeName"));
		actionLogTo.setLogAction("添加了供应商集团");
		actionLogTo.setLogContent("添加了供应商集团：{"+context.contextMap.get("suplGroupName")+"}");
		actionLogTo.setLogIp((String)context.contextMap.get("IP"));
		this.baseDAO.insertByTO("actionLog.insertActionLog",actionLogTo);
	}
	
	public List<SupplierGroupTO> getSupl(Context context) throws Exception {
		return this.supplierGroupDAO.getSupl(context);
	}
	
	public List<SupplierGroupTO> getSuplList(Context context) throws Exception {
		return this.supplierGroupDAO.getSuplList(context);
	}
	
	private void removeSuplGroupMap(Context context) throws Exception {
		this.supplierGroupDAO.removeSuplGroupMap(context);
	}
	
	@Transactional
	public void insertSuplGroupMap(Context context) throws Exception {
		this.removeSuplGroupMap(context);
		String [] suplIds=((String)context.contextMap.get("suplId")).split("-");
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("suplGroupCode", (String)context.contextMap.get("suplGroupCode"));
		String result="N";
		ActionLogTo actionLogTo=new ActionLogTo();
		for(int i=0;i<suplIds.length;i++) {
			if(!"".equals(suplIds[i])) {
				param.put("suplId", suplIds[i]);
				result=this.supplierGroupDAO.checkHasMaping(param);
				if("Y".equals(result)) {//防止一个集团添加相同的供应商或者一个供应商分配给多个集团
					continue;
				}
				this.supplierGroupDAO.insertSuplGroupMap(param);
				actionLogTo.setLogContent("关联{"+context.contextMap.get("suplGroupName")+"}的供应商ID：{"+context.contextMap.get("suplId")+"}");
			} else {
				SupplierGroupTO to=new SupplierGroupTO();
				to.setSuplGroupCode((String)context.contextMap.get("suplGroupCode"));
				this.supplierGroupDAO.removeGroupCredit(to);
				actionLogTo.setLogContent("删除{"+context.contextMap.get("suplGroupName")+"}的供应商关联");
			}
		}
		
		actionLogTo.setLogBy((String)context.contextMap.get("s_employeeName"));
		actionLogTo.setLogAction("关联供应商到供应商集团");
		actionLogTo.setLogIp((String)context.contextMap.get("IP"));
		this.baseDAO.insertByTO("actionLog.insertActionLog",actionLogTo);
	}
	
	public String checkHasMaping(Map<String,String> param) throws Exception {
		return this.supplierGroupDAO.checkHasMaping(param);
	}
	
	public List<SupplierGroupTO> getNotLoopCreditAmount(Context context) throws Exception {
		
		return this.supplierGroupDAO.getNotLoopCreditAmount(context);
	}
	
	public List<SupplierGroupTO> getLoopCreditAmountSum(Context context) throws Exception {
		
		return this.supplierGroupDAO.getLoopCreditAmountSum(context);
	}
	
	public List<SupplierGroupTO> getNotLoopCreditAmountSum(Context context) throws Exception {

		return this.supplierGroupDAO.getNotLoopCreditAmountSum(context);
	}
	
	public SupplierGroupTO getSuplGroupCredit(Context context) throws Exception {
		
		return this.supplierGroupDAO.getSuplGroupCredit(context);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addGroupCredit(Map<String, Object> dataMap) throws Exception {
		update("supplier.removeGroupCredit", dataMap);
		insert("supplier.addGroupCredit", dataMap);
		/*this.supplierGroupDAO.removeGroupCredit(to);
		this.supplierGroupDAO.addGroupCredit(to);*/
	}
	
	public List<SupplierGroupTO> getCreditLog(Context context) throws Exception {
		
		return this.supplierGroupDAO.getCreditLog(context);
	}
	
	public SupplierGroupTO getPayBeforeLoop(Context context) throws Exception {
		return this.supplierGroupDAO.getPayBeforeLoop(context);
	}
	
	public SupplierGroupTO getPayBeforeNotLoop(Context context) throws Exception {
		return this.supplierGroupDAO.getPayBeforeNotLoop(context);
	}
}
