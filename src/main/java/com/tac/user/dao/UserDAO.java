package com.tac.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.dao.BaseDAO;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.tac.user.to.UserTo;

public class UserDAO extends BaseDAO{

	public void insertUser(UserTo user){
		this.getSqlMapClientTemplate().insert("user.insertUser", user);		
	}
	public void deleteUser(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		this.getSqlMapClientTemplate().delete("user.deleteUser", param);
	}
	
	public void updateUser(UserTo user){      
		this.getSqlMapClientTemplate().update("user.updateUser", user);
	}
	
	
	public List<SelectionTo> getAllUsers(){
		return this.getSqlMapClientTemplate().queryForList("user.getAllUsers");
	}

	
	public UserTo getUserById(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (UserTo)this.getSqlMapClientTemplate().queryForObject("user.getUserById", param);
	}
	
	public PagingInfo queryForListWithPaging(PagingInfo pagingInfo)throws Exception{
		return queryForListWithPaging("user.getUsers",pagingInfo);
	}
	
}
