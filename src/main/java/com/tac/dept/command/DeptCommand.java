package com.tac.dept.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.util.HtmlUtils;

import com.brick.base.command.BaseCommand;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;
import com.tac.company.service.CompanyService;
import com.tac.company.to.CompanyTo;
import com.tac.dept.service.DeptService;
import com.tac.dept.to.DeptTo;



public class DeptCommand extends BaseCommand{
	
	private final static String DEPT_LEVEL = "部门等级";
	
	private DeptService deptService;
	
	private CompanyService companyService;
	
	public void getDeptByCompany(Context context) throws Exception{
		String companyId = (String) context.contextMap.get("companyId");
		List<DeptTo> list = deptService.dealDeptForTree(Integer.parseInt(companyId));
		Output.jsonArrayOutputForObject(list, context);
	}
	
	public void getDepts(Context context) throws Exception{		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<CompanyTo> companys = companyService.getAllCompany();
		List deptLevels = DictionaryUtil.getDictionary(DEPT_LEVEL);
		outputMap.put("companys", companys);
		outputMap.put("deptLevels", deptLevels);
		Output.jspOutput(outputMap, context, "/dept/depts.jsp");
	}
	
	public void saveDept(Context context){		
		String deptName =  (String) context.contextMap.get("deptName");
		int parentId = HTMLUtil.getIntParam(context.request, "parentId", 0);
		int companyId =   HTMLUtil.getIntParam(context.request, "companyId", 0);
		int deptLeader =  HTMLUtil.getIntParam(context.request, "deptLeader", 0);
		int deptLevel =  HTMLUtil.getIntParam(context.request, "deptLevel", 0);
		int order =   HTMLUtil.getIntParam(context.request, "order", 0);
		int userId = (Integer) context.request.getSession().getAttribute("s_employeeId");
		DeptTo dept = new DeptTo();
		dept.setDeptLeader(deptLeader);
		dept.setParentId(parentId);
		dept.setCompanyId(companyId);
		dept.setOrderNo(order);
		dept.setName(deptName);
		dept.setCreateBy(userId);
		dept.setDeptLevel(deptLevel);
		deptService.saveDept(dept);
		Output.jsonFlageOutput(true, context);
	}
	
	public void deleteDept(Context context){		
		int deptId =   HTMLUtil.getIntParam(context.request, "deptId", 0);
		deptService.deleteDept(deptId);
		Output.jsonFlageOutput(true, context);
	}
	public void modifyDept(Context context){
		int deptId =   HTMLUtil.getIntParam(context.request, "deptId", 0);
		String deptName =  (String) context.contextMap.get("deptName");
		int parentId = HTMLUtil.getIntParam(context.request, "parentId", 0);
		int companyId =   HTMLUtil.getIntParam(context.request, "companyId", 0);
		int deptLeader =  HTMLUtil.getIntParam(context.request, "deptLeader", 0);
		int order =   HTMLUtil.getIntParam(context.request, "order", 0);
		int deptLevel =  HTMLUtil.getIntParam(context.request, "deptLevel", 0);
		int userId = (Integer) context.request.getSession().getAttribute("s_employeeId");
		DeptTo dept = new DeptTo();
		dept.setId(deptId);
		dept.setDeptLeader(deptLeader);
		dept.setParentId(parentId);
		dept.setCompanyId(companyId);
		dept.setOrderNo(order);
		dept.setName(deptName);
		dept.setDeptLevel(deptLevel);
		dept.setModifyBy(userId);
		deptService.updateDept(dept);
		Output.jsonFlageOutput(true, context);
	}
	public DeptService getDeptService() {
		return deptService;
	}

	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
}
