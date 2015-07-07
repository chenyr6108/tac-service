package com.tac.dept.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.tac.dept.dao.DeptDAO;
import com.tac.dept.to.DeptTo;
import com.tac.user.to.UserTo;


public class DeptService extends BaseService {
	
	private DeptDAO deptDAO;
	
	public PagingInfo queryForListWithPaging(String content,PagingInfo pagingInfo,String defaultOrderField,String defaultOrderDirection) throws Exception{		
		Map params = new HashMap();
		params.put("content",content);
		pagingInfo.setParams(params);
		return deptDAO.queryForListWithPaging(pagingInfo);
	}

	public Integer getDeptLeaderById(int id){
		return deptDAO.getDeptLeaderById(id);
	}
	public Integer getParentDeptById(int id){
		return deptDAO.getParentDeptById(id);
	}
	
	public String getDeptNameById(int id){
		return deptDAO.getDeptNameById(id);
	}
	
	public Integer saveDept(DeptTo dept){
		return deptDAO.insertDept(dept);
	}
	public void updateDept(DeptTo dept){
		deptDAO.updateDept(dept);
	}
	
	public DeptTo getDeptById(int id){
		return deptDAO.getDeptById(id);
	}
	
	public List<UserTo> getUsersByDeptId(int id){
		return deptDAO.getUsersByDeptId(id);
	}
	public void deleteDept(int id){
		deptDAO.deleteDept(id);
	}
	
	public List<DeptTo> getDeptsByCompanyId(int companyId){
		return deptDAO.getDeptsByCompanyId(companyId);
	}
	
	public List<DeptTo> dealDeptForTree(int companyId){
		
		
		
		List<DeptTo> depts = getDeptsByCompanyId(companyId);
	
		if(depts!=null && depts.size()>0){
			int size = depts.size();
			int minLevel = depts.get(0).getDeptLevel();
			int sortNo = 1;
			for(int i=0;i<size;i++){
				if(depts.get(i).getDeptLevel()==minLevel){
					depts.get(i).setDeptOrder(sortNo);
					sortNo = sortDeptForTree(depts.get(i),depts,sortNo);
				}else{
					break;
				}
			}
			
		}
		
		Collections.sort(depts, new Comparator<DeptTo>(){
			@Override
			public int compare(DeptTo arg0, DeptTo arg1) {
				if(arg0.getDeptOrder()>=arg1.getDeptOrder())
					return 1;
				else
					return -1;
			}			
		});
		return depts;
	}
	
	private int sortDeptForTree(DeptTo dept,List<DeptTo> depts,int sortNo){
		sortNo++;
		for(int i=0,j=depts.size();i<j;i++){
			if(dept.getId().equals(depts.get(i).getParentId())){
				depts.get(i).setDeptOrder(sortNo);
				sortNo = sortDeptForTree(depts.get(i), depts, sortNo);
			}
		}
		return sortNo;
	}
	public DeptDAO getDeptDAO() {
		return deptDAO;
	}

	public void setDeptDAO(DeptDAO deptDAO) {
		this.deptDAO = deptDAO;
	}
	
	/**
	 * 获取父部门信息
	 * @param id 部门id
	 * @return
	 */
	public DeptTo getParentDeptDetailById(int id){
		return deptDAO.getParentDeptDetailById(id);
	}
	
	/**
	 * 根据部门id获取主管信息
	 * @param deptId 	部门id
	 * @param level  	主管等级
	 * @return 无效返回0
	 */
	public DeptTo getDeptLeaderByDeptId(int deptId, int level){
		//区域主管
		DeptTo deptTo = this.getDeptById(deptId);
		if(deptTo != null){
			//防止无限循环
			int index = 0;
			while(deptTo.getDeptLevel() > level && index++ < 5){
				deptTo = this.getParentDeptDetailById(deptTo.getId());
			}
			if(deptTo.getDeptLevel()<=level){
				//部级
				return deptTo;
			}
		}
		return null;
	}

}

