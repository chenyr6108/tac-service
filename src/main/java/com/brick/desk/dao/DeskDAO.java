package com.brick.desk.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.desk.to.DeskTO;
import com.brick.service.entity.Context;

public class DeskDAO extends BaseDAO {

	public List<DeskTO> getPermissionGroup() {
		
		List<DeskTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("permission.getPermissionGroup");
		if(resultList==null) {
			resultList=new ArrayList<DeskTO>();
		}
		return resultList;
	}
	
	public int checkIsExist(Context context) {
		return (Integer)this.getSqlMapClientTemplate().queryForObject("permission.checkIsExist",context.contextMap);
	}
	
	public void insertPermissionGroup(Context context) {
		this.getSqlMapClientTemplate().insert("permission.insertPermissionGroup",context.contextMap);
	}
	
	public void deletePermissionGroup(Context context) {
		this.getSqlMapClientTemplate().update("permission.deletePermissionGroup",context.contextMap);
	}
	
	public List<DeskTO> getPermissionMap(Context context) {
		
		List<DeskTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("permission.getPermissionMap",context.contextMap);
		if(resultList==null) {
			resultList=new ArrayList<DeskTO>();
		}
		return resultList;
	}

	public List<Map<String,String>> getDeskAuthList(Context context) {
		
		List<Map<String,String>> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("dataDictionary.queryDataDictionary",context.contextMap);
		if(resultList==null) {
			resultList=new ArrayList<Map<String,String>>();
		}
		return resultList;
	}
	
	public void deletePermissionMap(Context context) {
		this.getSqlMapClientTemplate().update("permission.deletePermissionMap",context.contextMap);
	}
	
	public void savePermissionMap(Context context) {
		
		this.getSqlMapClientTemplate().insert("permission.savePermissionMap",context.contextMap);
	}
	
	public List<DeskTO> getPermissionList(Context context) {
		
		List<DeskTO> resultList=null;
		
		context.contextMap.put("type","欢迎页面模块");
		resultList=this.getSqlMapClientTemplate().queryForList("permission.getPermissionList",context.contextMap);
		if(resultList==null) {
			resultList=new ArrayList<DeskTO>();
		}
		return resultList;
	}
	
	public void deletePermissionUser(Context context) {
		this.getSqlMapClientTemplate().update("permission.deletePermissionUser",context.contextMap);
	}
	
	public void savePermissionUser(Context context) {
		
		this.getSqlMapClientTemplate().insert("permission.savePermissionUser",context.contextMap);
	}
	
	public List<DeskTO> getUserList() {
		
		List<DeskTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("permission.getUserList");
		if(resultList==null) {
			resultList=new ArrayList<DeskTO>();
		}
		return resultList;
	}
	
	public List<Map> getPermissionUserList(Context context) {
		
		List<Map> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("permission.getPermissionUserList",context.contextMap);
		if(resultList==null) {
			resultList=new ArrayList<Map>();
		}
		return resultList;
		
	}
	
}
