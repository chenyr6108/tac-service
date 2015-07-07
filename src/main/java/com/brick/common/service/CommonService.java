package com.brick.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.common.dao.CommonDAO;
import com.brick.log.to.ActionLogTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;

public class CommonService extends BaseService {
	
	private CommonDAO commonDAO;

	public CommonDAO getCommonDAO() {
		return commonDAO;
	}

	public void setCommonDAO(CommonDAO commonDAO) {
		this.commonDAO = commonDAO;
	}
	
	public List<Map<String,Object>> getMaintaninceType() {
	    return this.commonDAO.getMaintaninceType();	
	}
	
	public void delProductType(Context context) {
		this.commonDAO.delProductType(context);
	}
	
	@Transactional
	public void insertProductType(List<Map<String,Object>> addList) {
		String productLevel = null;
		Map<String,Object> map = null;
		for(int i=0;i<addList.size();i++) {
			if(checkProdutTypeExist(addList.get(i))){
				this.commonDAO.updateProductType(addList.get(i));
			}else{
				map = addList.get(i);
				productLevel = (String) queryForObj("prdcKind.getProductLevelByType", map);
				this.commonDAO.insertProductType(map);
				if (!StringUtils.isEmpty(productLevel)) {
					map.put("productLevel", productLevel);
					update("prdcKind.updateProductLevelByType", map);
				}
			}
			
		}
	}

	
	public boolean checkProdutTypeExist(Map map){
		int count = 0;
		try {
			count = (Integer) DataAccessor.query("prdcKind.getProductTypeCount", map, RS_TYPE.OBJECT);
		} catch (Exception e) {

			e.printStackTrace();
		}
		if(count>0){
			return true;
		}
		return false;
	}
	
	public static List<Map<String,Object>> getProductList(String content,String type1,String type2,String level) {
		
		List<Map<String,Object>> resultList=null;
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("searchValue",content);
		param.put("TYPE1",type1);
		param.put("TYPE2",type2);
		param.put("PRODUCT_LEVEL",level);
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("prdcKind.query",param,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public void updateType1(String old_type,String new_type,String userName,String ip) throws ServiceException{
		commonDAO.updateType1(old_type, new_type);
		ActionLogTo actionLogTo = new ActionLogTo();
		actionLogTo.setLogBy(userName);
		actionLogTo.setLogAction("标的物大项修改");
		actionLogTo.setLogContent("大项："+old_type +"---->" + new_type);
		actionLogTo.setLogIp(ip);
		this.insertActionLog(actionLogTo);
	}
	
	public void updateType2(String old_type,String new_type,String type1,String level,String userName,String ip) throws ServiceException{
		commonDAO.updateType2(old_type, new_type,type1,level);
		ActionLogTo actionLogTo = new ActionLogTo();
		actionLogTo.setLogBy(userName);
		actionLogTo.setLogAction("标的物小项修改");
		actionLogTo.setLogContent("大项："+type1+",小项："+old_type +"---->" + new_type+";level-->"+level);
		actionLogTo.setLogIp(ip);
		this.insertActionLog(actionLogTo);
	}
	
	public List getMaintaninceType1(){
		return commonDAO.getMaintaninceType1();
	}
}
