package com.brick.deptCmpy.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.deptCmpy.to.TreeTO;

public class DeptCmpyDAO extends BaseDAO {

	public List<DeptCmpyTO> queryDeptCmpy(Map<String,Object> param) throws Exception {
			
			List<DeptCmpyTO> deptCmpyList=null;
			
			deptCmpyList=this.getSqlMapClientTemplate().queryForList("department.queryDeptCmpy",param);
			
			if(deptCmpyList==null) {
				deptCmpyList=new ArrayList<DeptCmpyTO>();
			}
			return deptCmpyList;
		}
	
	public void batchUpdateDept(Map<String,Object> param) throws Exception {
		
		this.getSqlMapClientTemplate().update("department.batchUpdateDept",param);
	}
	
	public void addDept(Map param) throws Exception {
		this.getSqlMapClientTemplate().insert("department.addDept",param);
	}

	public List<TreeTO> getTreeList() throws Exception {
		List<TreeTO> treeList=null;
		
		treeList=this.getSqlMapClientTemplate().queryForList("common.getTreeList");
		
		if(treeList==null) {
			treeList=new ArrayList<TreeTO>();
		}
		return treeList;
	}
}
