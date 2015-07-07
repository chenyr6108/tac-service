package com.brick.special.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.derby.iapi.util.StringUtil;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.special.dao.CreditSpecialDAO;
import com.brick.special.to.CreditSpecialTO;
import com.brick.util.web.HTMLUtil;

public class CreditSpecialService extends BaseService {

	private CreditSpecialDAO creditSpecialDAO;

	public CreditSpecialDAO getCreditSpecialDAO() {
		return creditSpecialDAO;
	}

	public void setCreditSpecialDAO(CreditSpecialDAO creditSpecialDAO) {
		this.creditSpecialDAO = creditSpecialDAO;
	}
	
	public List<CreditSpecialTO> getCreditSpecialPropertyList() throws Exception {
		return this.creditSpecialDAO.getCreditSpecialPropertyList();
	}
	
	public List<CreditSpecialTO> queryCreditSpecialGroup() throws Exception {
		return this.creditSpecialDAO.queryCreditSpecialGroup();
	}
	
	public boolean checkIsDuplicate(Context context) throws Exception {
		return this.creditSpecialDAO.checkIsDuplicate(context);
	}
	
	public boolean checkIsDuplicate1(Context context) throws Exception {
		return this.creditSpecialDAO.checkIsDuplicate1(context);
	}
	
	public void addCreditSpecialProperty(Context context) throws Exception {
		this.creditSpecialDAO.addCreditSpecialProperty(context);
	}
	
	public void updateCreditSpecialProperty(Context context) throws Exception {
		this.creditSpecialDAO.updateCreditSpecialProperty(context);
	}
	
	public void configCreditSpecialProperty(Context context) throws Exception {
		this.creditSpecialDAO.configCreditSpecialProperty(context);
	}
	
	@Transactional
	public void addCreditSpecialGroupAndMap(Context context,List<CreditSpecialTO> propertyList) throws Exception {
		
		this.creditSpecialDAO.addCreditSpecialGroup(context);
		
		for(int i=0;propertyList!=null&&i<propertyList.size();i++) {
			context.contextMap.put("propertyCode",propertyList.get(i).getPropertyCode());
			context.contextMap.put("checkedValue","Y");
			this.creditSpecialDAO.addCreditSpecialGroupMap(context);
		}
	}
	
	public List<CreditSpecialTO> queryCreditSpecialGroupMap(Context context) throws Exception {
		return this.creditSpecialDAO.queryCreditSpecialGroupMap(context);
	}
	
	public List<CreditSpecialTO> getAreaList() throws Exception {
		return this.creditSpecialDAO.getAreaList();
	}
	
	@Transactional
	public void deleteCreditSpecial(Context context) throws Exception {
		
		this.creditSpecialDAO.deleteCreditSpecialGroup(context);
		this.creditSpecialDAO.deleteCreditSpecialGroupMap(context);
	}
	
	@Transactional
	public void updateCreditSpecialGroupMap(Context context) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("creditSpecialCode",context.contextMap.get("creditSpecialCode"));
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		
		//更新专案启始与结束时间
		param.put("startDate",context.contextMap.get("startDateDescr"));
		param.put("endDate",context.contextMap.get("endDateDescr"));
		this.creditSpecialDAO.updateCreditSpecialGroup(param);
		
		//更新租赁物名称
		param.put("propertyCode","LEASE_PRODUCTION_NAME");
		param.put("value1",context.contextMap.get("LEASE_PRODUCTION_NAME"));
		param.put("value3",context.contextMap.get("row1"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//更新租赁物品牌
		param.put("propertyCode","LEASE_PRODUCTION_BRAND");
		String [] brandNames=HTMLUtil.getParameterValues(context.request,"LEASE_PRODUCTION_BRAND","");
		StringBuffer names=new StringBuffer();
		for(int i=0;i<brandNames.length;i++) {
			if(i!=brandNames.length-1) {
				names.append(brandNames[i]).append(",");
			} else {
				names.append(brandNames[i]);
			}
		}
		param.put("value1",names);
		param.put("value3",context.contextMap.get("row2"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap1(param);
		
		//租赁物是否锁码
		param.put("propertyCode","LEASE_PRODUCTION_IS_LOCK");
		param.put("value1",context.contextMap.get("LEASE_PRODUCTION_IS_LOCK"));
		param.put("value3",context.contextMap.get("row3"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//租赁物是否设定抵押权
		/*param.put("propertyCode","LEASE_PRODUCTION_HAS_PLEDGE");
		param.put("value1",context.contextMap.get("LEASE_PRODUCTION_HAS_PLEDGE"));
		param.put("value3",context.contextMap.get("row4"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);*/
		
		//供应商名称
		param.put("propertyCode","SUPPLIER_NAME");
		String [] supplierNames=HTMLUtil.getParameterValues(context.request,"SUPPLIER_NAME","");
		String [] supplierIds=HTMLUtil.getParameterValues(context.request,"SUPPLIER_ID","");
		names=new StringBuffer();
		StringBuffer ids=new StringBuffer();
		for(int i=0;i<supplierNames.length;i++) {
			if(i!=supplierNames.length-1) {
				names.append(supplierNames[i]).append(",");
				ids.append(supplierIds[i]).append(",");
			} else {
				names.append(supplierNames[i]);
				ids.append(supplierIds[i]);
			}
		}
		param.put("value1",names);
		param.put("value2",ids);
		param.put("value3",context.contextMap.get("row5"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap1(param);
		param.remove("value2");
		
		//供应商级别限制
		param.put("propertyCode","SUPPLIER_LEVEL_LIMIT");
		param.put("value1",context.contextMap.get("SUPPLIER_LEVEL_LIMIT"));
		param.put("value3",context.contextMap.get("row6"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//付款方式
		param.put("propertyCode","PAY_WAY");
		param.put("value1",context.contextMap.get("PAY_WAY"));
		param.put("value2",context.contextMap.get("PAY_DAY"));
		param.put("value3",context.contextMap.get("row7"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		param.remove("value2");
		
		//是否交机前拨款
		param.put("propertyCode","IS_PAY_BEFORE");
		param.put("value1",context.contextMap.get("IS_PAY_BEFORE"));
		param.put("value3",context.contextMap.get("row8"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//供应商连保
		param.put("propertyCode","SUPPLIER_UNION_PLEDGE");
		param.put("value1",context.contextMap.get("SUPPLIER_UNION_PLEDGE"));
		param.put("value3",context.contextMap.get("row9"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//供应商回购
		/*param.put("propertyCode","SUPPLIER_BUY_BACK");
		param.put("value1",context.contextMap.get("SUPPLIER_BUY_BACK"));
		param.put("value3",context.contextMap.get("row10"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);*/
		
		//专案总承做金额
		param.put("propertyCode","TOTAL_MONEY");
		param.put("value1",context.contextMap.get("TOTAL_MONEY"));
		param.put("value3",context.contextMap.get("row11"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//租赁期数
		param.put("propertyCode","LEASE_PERIOD");
		if("Y".equals(context.contextMap.get("row12"))) {
			param.put("value1",HTMLUtil.getParameterValues(context.request,"LEASE_PERIOD","")[0]);
			param.put("value2",HTMLUtil.getParameterValues(context.request,"LEASE_PERIOD","")[1]);
		} else {
			param.put("value1","");
			param.put("value2","");
		}
		param.put("value3",context.contextMap.get("row12"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		param.remove("value2");
		
		//租赁TR
		param.put("propertyCode","LEASE_TR");
		if("Y".equals(context.contextMap.get("row13"))) {
			param.put("value1",HTMLUtil.getParameterValues(context.request,"LEASE_TR","")[0]);
			param.put("value2",HTMLUtil.getParameterValues(context.request,"LEASE_TR","")[1]);
		} else {
			param.put("value1","");
			param.put("value2","");
		}
		param.put("value3",context.contextMap.get("row13"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		param.remove("value2");
		
		//租赁成数
		param.put("propertyCode","LEASE_PERCENT");
		param.put("value1",context.contextMap.get("LEASE_PERCENT"));
		param.put("value3",context.contextMap.get("row14"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//单案金额
		param.put("propertyCode","SINGLE_MONEY");
		param.put("value1",context.contextMap.get("SINGLE_MONEY"));
		param.put("value2",context.contextMap.get("MIN_SINGLE_MONEY"));
		param.put("value3",context.contextMap.get("row15"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//承租人归户金额上限
		param.put("propertyCode","CUSTOMER_MONEY_TOPLIMIT");
		param.put("value1",context.contextMap.get("CUSTOMER_MONEY_TOPLIMIT"));
		param.put("value3",context.contextMap.get("row16"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//承租人成立年限
		param.put("propertyCode","CUSTOMER_REGISTER_PERIOD");
		param.put("value1",context.contextMap.get("CUSTOMER_REGISTER_PERIOD"));
		param.put("value3",context.contextMap.get("row17"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		//承租人地区限制
		param.put("propertyCode","CUSTOMER_AREA_LIMIT");
		param.put("value3",context.contextMap.get("row18"));
		if("Y".equals(context.contextMap.get("row18"))) {
			param.put("value1",HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[0].split(",")[0]+","+HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[1].split(",")[0]+","+HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[2].split(",")[0]);
			param.put("value2",HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[0].split(",")[1]+","+HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[1].split(",")[1]+","+HTMLUtil.getParameterValues(context.request,"CUSTOMER_AREA_LIMIT","")[2].split(",")[1]);
			this.creditSpecialDAO.updateCreditSpecialGroupMap1(param);
		} else {
			param.put("value1","");
			param.put("value2","");
			this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		}
		
		//访厂要求
		param.put("propertyCode","VISITATION");
		param.put("value1", context.contextMap.get("VISITATION"));
		param.remove("value2");
		param.put("value3",context.contextMap.get("row19"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		
		
		//访厂要求
		param.put("propertyCode","PRODUCT_RATE");
		
		String [] levels = HTMLUtil.getParameterValues(context.request,"PRODUCT_LEVEL","");
		String [] rates = HTMLUtil.getParameterValues(context.request,"PRODUCT_RATE","");
		StringBuffer level = new StringBuffer("");
		StringBuffer rate = new StringBuffer("");
		for(int i=0;i<levels.length;i++){
			level.append(levels[i]);
			level.append(",");
			rate.append(rates[i]);
			rate.append(",");
		}
		if(level.length()>0){
			param.put("value1",level.substring(0, level.length()-1));
		}else{
			param.put("value1",level.toString());
		}
		if(rate.length()>0){
			param.put("value2", rate.substring(0, rate.length()-1));
		}else{
			param.put("value1",rate.toString());
		}
		param.put("value3",context.contextMap.get("row20"));
		this.creditSpecialDAO.updateCreditSpecialGroupMap(param);
		//承做地区
		Map<String,Object> paramDecp=new HashMap<String,Object>();
		paramDecp.put("creditSpecialCode",context.contextMap.get("creditSpecialCode"));
		paramDecp.put("s_employeeId",context.contextMap.get("s_employeeId"));
		paramDecp.put("propertyCode","BELONGDEPT");
		paramDecp.put("value3",context.contextMap.get("row21"));//是否生效
		String checkedValue =context.contextMap.get("row21")==null?"":String.valueOf(context.contextMap.get("row21"));
		if(checkedValue.equals("Y")){
			String isAll =context.contextMap.get("IS_ALL")==null?"":String.valueOf(context.contextMap.get("IS_ALL"));
			if(isAll.equals("Y")){
				paramDecp.put("value1","Y");
				paramDecp.put("value2","");
			}else{
				String[] decptList = HTMLUtil.getParameterValues(context.getRequest(),"officeInput", "");
				StringBuffer decpValue = new StringBuffer("");
				for(int i=0;i<decptList.length;i++){
					decpValue.append(decptList[i]);
					decpValue.append(",");
				}
				paramDecp.put("value1","N");
				paramDecp.put("value2",decpValue);
			}
			this.creditSpecialDAO.updateCreditSpecialGroupMap1(paramDecp);
		}else{
			paramDecp.put("value1","");
			paramDecp.put("value2","");
		  this.creditSpecialDAO.updateCreditSpecialGroupMap(paramDecp);
		}
		
	}
	
	public List<Map<String,String>> getMaintaninceType() throws Exception {
		return this.creditSpecialDAO.getMaintaninceType();
	}
	
	public List<Map<String,String>> getBrandList() throws Exception {
		return this.creditSpecialDAO.getBrandList();
	}
}
