package com.brick.common.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.service.entity.Context;

public class CommonDAO extends BaseDAO {

	public List<Map<String,Object>> getMaintaninceType() {
		
		List<Map<String,Object>> resultList=null;
		
		resultList=(List<Map<String,Object>>)this.getSqlMapClientTemplate().queryForList("prdcKind.getMaintaninceType");
		
		if(resultList==null) {
			resultList=new ArrayList<Map<String,Object>>();
		}
		
		return resultList;
	}
	
	public void delProductType(Context context) {
		this.getSqlMapClientTemplate().update("prdcKind.delProductType", context.contextMap);
	}
	
	public void insertProductType(Map<String,Object> addMap) {
		this.getSqlMapClientTemplate().insert("prdcKind.insertProductType", addMap);
	}
	
	public void updateProductType(Map<String,Object> addMap){
		this.getSqlMapClientTemplate().insert("prdcKind.updateProductType", addMap);
	}
	
	public void updateType1(String old_type,String new_type){
		Map<String,String> param=new HashMap<String,String>();
		param.put("old_type", old_type);
		param.put("new_type", new_type);
		this.getSqlMapClientTemplate().insert("prdcKind.updateType1", param);
	}
	
	public void updateType2(String old_type,String new_type,String type1,String level){
		Map<String,String> param=new HashMap<String,String>();
		param.put("type1", type1);
		param.put("old_type", old_type);
		param.put("new_type", new_type);
		param.put("level", level);
		this.getSqlMapClientTemplate().insert("prdcKind.updateType2", param);
	}
	
	public List getMaintaninceType1(){
		return this.getSqlMapClientTemplate().queryForList("prdcKind.getMaintaninceType1");
	}
}
