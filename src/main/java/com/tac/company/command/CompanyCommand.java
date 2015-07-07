package com.tac.company.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.tac.company.service.CompanyService;
import com.tac.company.to.CompanyTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;


public class CompanyCommand extends BaseCommand{
	
	private CompanyService companyService;

	public void getCompanys(Context context) throws Exception{				
		String content = (String) context.contextMap.get("content");
		PagingInfo pagingInfo = companyService.queryForListWithPaging(context.contextMap);	
		List<CompanyTo> list = companyService.getAllCompany();
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("dw", pagingInfo);
		outputMap.put("content", content);
		outputMap.put("companys", list);
		Output.jspOutput(outputMap, context, "/company/companys.jsp");
	}
	
	public void getCompanyById(Context context){
		int id = HTMLUtil.getIntParam(context.request, "id", 0);
		CompanyTo company = companyService.getCompanyById(id);
		
		try{
			Output.jsonObjectOutputForTo(company, context);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void addCompany(Context context) throws Exception{				
		String companyName = (String) context.contextMap.get("companyName");
		String companyCode = (String) context.contextMap.get("companyCode");
		String legalPerson = (String) context.contextMap.get("legalPerson");
		String companyTelephone = (String) context.contextMap.get("companyTelephone");
		String companyFax = (String) context.contextMap.get("companyFax");
		String companyUrl = (String) context.contextMap.get("companyUrl");	
		String companyPostcode = (String) context.contextMap.get("companyPostcode");
		String companyAddress = (String) context.contextMap.get("companyAddress");
		int companyOrder = HTMLUtil.getIntParam(context.request, "companyOrder", 0);
		int parentId = HTMLUtil.getIntParam(context.request, "parentId", 0);
		int userId = (Integer) context.request.getSession().getAttribute("s_employeeId");
		CompanyTo company = new CompanyTo();
		company.setName(companyName);
		company.setCode(companyCode);
		company.setLegalPerson(legalPerson);
		company.setTelephone(companyTelephone);
		company.setFax(companyFax);
		company.setUrl(companyUrl);
		company.setPostcode(companyPostcode);
		company.setAddress(companyAddress);
		company.setOrderNo(companyOrder);
		company.setParentId(parentId);
		companyService.saveCompany(company);	
		company.setCreateBy(userId);
		Output.jsonFlageOutput(true, context);

	}
	public void modifyCompany(Context context) throws Exception{				
		String companyName = (String) context.contextMap.get("companyName");
		String companyCode = (String) context.contextMap.get("companyCode");
		String legalPerson = (String) context.contextMap.get("legalPerson");
		String companyTelephone = (String) context.contextMap.get("companyTelephone");
		String companyFax = (String) context.contextMap.get("companyFax");
		String companyUrl = (String) context.contextMap.get("companyUrl");	
		String companyPostcode = (String) context.contextMap.get("companyPostcode");
		String companyAddress = (String) context.contextMap.get("companyAddress");
		int companyOrder = HTMLUtil.getIntParam(context.request, "companyOrder", 0);
		int parentId = HTMLUtil.getIntParam(context.request, "parentId", 0);
		int id = HTMLUtil.getIntParam(context.request, "companyId", 0);
		int userId = (Integer) context.request.getSession().getAttribute("s_employeeId");
		CompanyTo company = new CompanyTo();
		company.setId(id);
		company.setName(companyName);
		company.setCode(companyCode);
		company.setLegalPerson(legalPerson);
		company.setTelephone(companyTelephone);
		company.setFax(companyFax);
		company.setUrl(companyUrl);
		company.setPostcode(companyPostcode);
		company.setAddress(companyAddress);
		company.setOrderNo(companyOrder);
		company.setParentId(parentId);
		company.setModifyBy(userId);
		companyService.updateCompany(company);
		Output.jsonFlageOutput(true, context);

	}
	
	public void getAllCompanys(Context context){
		List<CompanyTo> list = companyService.getAllCompany();
		Output.jsonArrayOutputForList(list, context);
	}
	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
}
