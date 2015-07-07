package com.brick.special.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.special.service.CreditSpecialService;
import com.brick.special.to.CreditSpecialTO;
import com.brick.util.DateUtil;

public class CreditSpecialCommand extends BaseCommand {

	private CreditSpecialService creditSpecialService;

	public CreditSpecialService getCreditSpecialService() {
		return creditSpecialService;
	}

	public void setCreditSpecialService(CreditSpecialService creditSpecialService) {
		this.creditSpecialService = creditSpecialService;
	}
	
	public void queryCreditSpecialGroup(Context context) {
		
		List<CreditSpecialTO> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			resultList=this.creditSpecialService.queryCreditSpecialGroup();
		} catch (Exception e) {
			context.errList.add("获得专案出错!");
		}
		
			outputMap.put("resultList",resultList);
			outputMap.put("__action","creditSpecialCommand.queryCreditSpecialGroup");
		if(context.errList.isEmpty()) {
			Output.jspOutput(outputMap,context,"/creditSpecial/creditSpecialGroup.jsp");
		} else {
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}

	public void queryCreditSpecialGroupMap(Context context) {
		
		List<CreditSpecialTO> speccialMapList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,String>> provinceList=null;
		List<Map<String,String>> officeList=null;
		try {
			speccialMapList=this.creditSpecialService.queryCreditSpecialGroupMap(context);
			provinceList=(List<Map<String,String>>)DataAccessor.query("area.getProvinces",context.contextMap,DataAccessor.RS_TYPE.LIST);
			officeList=(List<Map<String,String>>)DataAccessor.query("modifyOrder.getDecpBymorder",context.contextMap,DataAccessor.RS_TYPE.LIST);
			
		} catch (Exception e) {
			
		}
		for(int i=0;i<speccialMapList.size();i++) {
			if("SUPPLIER_NAME".equals(speccialMapList.get(i).getPropertyCode())) {
				outputMap.put("suplNames",speccialMapList.get(i).getValue1().split(","));
				outputMap.put("suplIds",speccialMapList.get(i).getValue2().split(","));
			} else if("LEASE_PRODUCTION_BRAND".equals(speccialMapList.get(i).getPropertyCode())) {
				outputMap.put("brandNames",speccialMapList.get(i).getValue1().split(","));
			} else if("CUSTOMER_AREA_LIMIT".equals(speccialMapList.get(i).getPropertyCode())) {
				outputMap.put("areaLimitIds",speccialMapList.get(i).getValue1().split(","));
				outputMap.put("areaLimitNames",speccialMapList.get(i).getValue2().split(","));
			}
		}
		outputMap.put("provinceList",provinceList);
		outputMap.put("speccialMapList",speccialMapList);
		outputMap.put("officeList",officeList);
		Output.jsonOutput(outputMap,context);
	}
	
	public void configCreditSpecialGroup(Context context) {
		
		try {
			this.creditSpecialService.updateCreditSpecialGroupMap(context);
		} catch (Exception e) {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			context.errList.add("更新专案内容项目出错!");
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		this.queryCreditSpecialGroup(context);
	}

	public void addCreditSpecialGroup(Context context) {
		
		List<CreditSpecialTO> propertyList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			propertyList=this.creditSpecialService.getCreditSpecialPropertyList();
			
			this.creditSpecialService.addCreditSpecialGroupAndMap(context,propertyList);
		} catch (Exception e) {
			
		}
		
		this.queryCreditSpecialGroup(context);
	}
	
	public void queryCreditSpecialProperty(Context context) {
		
		List<CreditSpecialTO> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			resultList=this.creditSpecialService.getCreditSpecialPropertyList();
		} catch (Exception e) {
			context.errList.add("获得专案内容项目出错!");
		}
		
			outputMap.put("resultList",resultList);
		if(context.errList.isEmpty()) {
			Output.jspOutput(outputMap,context,"/creditSpecial/creditSpecialProperty.jsp");
		} else {
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	public void checkIsDuplicate(Context context) {
		
		boolean result=false;
		try {
			result=this.creditSpecialService.checkIsDuplicate(context);
		} catch (Exception e) {
			context.errList.add("检查项目代码是否重复出错!");
			Output.jspOutput(new HashMap<String,Object>(),context,"/error.jsp");
			return;
		}
		
		Output.jsonFlageOutput(result,context);
	}
	
	public void getBrandList(Context context) {
		
		List<Map<String,String>> resultList=null;
		
		try {
			resultList=this.creditSpecialService.getBrandList();
		} catch (Exception e) {
			
		}
		Output.jsonArrayOutputForList(resultList,context);
	}
	
	public void addCreditSpecialProperty(Context context) {
		
		try {
			this.creditSpecialService.addCreditSpecialProperty(context);
		} catch (Exception e) {
			context.errList.add("插入专案项目出错!");
			Output.jspOutput(new HashMap<String,Object>(),context,"/error.jsp");
			return;
		}
		
		this.queryCreditSpecialProperty(context);
	}

	public void updateCreditSpecialProperty(Context context) {
		
		try {
			this.creditSpecialService.updateCreditSpecialProperty(context);
		} catch (Exception e) {
			Output.jsonFlageOutput(false,context);
			return;
		}
		
		Output.jsonFlageOutput(true,context);
	}
	
	public void configCreditSpecialProperty(Context context) {
		
		try {
			this.creditSpecialService.configCreditSpecialProperty(context);
		} catch (Exception e) {
			context.errList.add("更新专案项目出错!");
			Output.jspOutput(new HashMap<String,Object>(),context,"/error.jsp");
			return;
		}
		this.queryCreditSpecialProperty(context);
	}
	
	public void checkCreditSpecialGroup(Context context) {
		
		boolean result=true;
		try {
			if(context.contextMap.get("startDateDescr")==null||context.contextMap.get("endDateDescr")==null) {
				result=false;
			} else {
				if(DateUtil.strToDate(context.contextMap.get("startDateDescr").toString(),"yyyy-MM-dd").
						compareTo(DateUtil.strToDate(context.contextMap.get("endDateDescr").toString(),"yyyy-MM-dd"))==1) {
					result=false;
				}
			}
			
			if(result) {
				result=this.creditSpecialService.checkIsDuplicate1(context);
			}
		} catch (Exception e) {
			context.errList.add("检查专案出错!");
			Output.jspOutput(new HashMap<String,Object>(),context,"/error.jsp");
			return;
		}
		
		Output.jsonFlageOutput(result,context);
	}
	
	public void getAreaList(Context context) {
		
		try {
			Output.jsonArrayOutputForList(this.creditSpecialService.getAreaList(),context);
		} catch (Exception e) {
			
		}
	}
	
	public void deleteCreditSpecialGroup(Context context) {
		
		boolean flag=false;
		try {
			this.creditSpecialService.deleteCreditSpecial(context);
		} catch (Exception e) {
			Output.jsonFlageOutput(flag,context);
		}
		
		flag=true;
		Output.jsonFlageOutput(flag,context);
	}
	
	public void getMaintaninceType(Context context) {
		
		List<Map<String,String>> resultList1=null;
		try {
			resultList1=this.creditSpecialService.getMaintaninceType();
		} catch (Exception e) {
		}
		
		Output.jsonArrayListOutput(resultList1,context);
	}
	
	public void logQuery(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> pagingInfo=null;
		try {
			pagingInfo=baseService.queryForListWithPaging("creditSpecial.getCreditSpecialLog",context.contextMap,"creditDate",ORDER_TYPE.DESC);
		} catch (ServiceException e) {
			
		}
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("pagingInfo",pagingInfo);
		Output.jspOutput(outputMap,context,"/creditSpecial/creditSpecialLog.jsp");
	}
}
