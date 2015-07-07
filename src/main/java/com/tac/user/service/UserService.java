package com.tac.user.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.tac.dept.dao.DeptDAO;
import com.tac.dept.to.DeptTo;
import com.tac.user.dao.UserDAO;
import com.tac.user.to.UserTo;


public class UserService extends BaseService {
	private UserDAO userDAO;
	private DeptDAO deptDAO;
	
	public PagingInfo queryForListWithPaging(String content,PagingInfo pagingInfo,String defaultOrderField,String defaultOrderDirection) throws Exception{		
		Map params = new HashMap();
		params.put("content",content);
		pagingInfo.setParams(params);
		return userDAO.queryForListWithPaging(pagingInfo);
	}

	public boolean isDeptLeader(int userId){
		boolean isLeader = false;
		UserTo user = userDAO.getUserById(userId);
		if(user.getDepartment()!=null){
			DeptTo dept = deptDAO.getDeptById(user.getDepartment());
			if(dept.getDeptLeader() == userId){
				isLeader = true;
			}
		}
		return isLeader;
	}
	
	public void getUserLeader(){
		
	}
	
	public UserTo getDeptLeaderByUserId(int userId){
		UserTo user = userDAO.getUserById(userId);
		if(user.getDepartment()!=null){
			DeptTo dept = deptDAO.getDeptById(user.getDepartment());
			return userDAO.getUserById(dept.getDeptLeader());			
		}
		return null;
	}
	
	public List<SelectionTo> getAllUsers(){
		return userDAO.getAllUsers();
	}
	public void saveUser(UserTo user) throws Exception{
		userDAO.insertUser(user);
	}
	public void updateUser(UserTo user) throws Exception{
		userDAO.updateUser(user);
	}
	
	public UserTo getUserById(int id){
		return userDAO.getUserById(id);
	}
	
	public void deleteUser(int id){
		userDAO.deleteUser(id);
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public DeptDAO getDeptDAO() {
		return deptDAO;
	}

	public void setDeptDAO(DeptDAO deptDAO) {
		this.deptDAO = deptDAO;
	}
	
	
}

