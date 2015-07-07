package com.brick.common.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.deptCmpy.to.DeptCmpyTO;

public class DeptMapDAO extends BaseDAO {
	
	public List<DeptCmpyTO> getDeptId_1() {
		
		List<DeptCmpyTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDeptId_1");
		
		if(resultList==null) {
			resultList=new ArrayList<DeptCmpyTO>();
		}
		
		return resultList;
	}
	
	public List<DeptCmpyTO> getDeptId_1(Map<String,String> param) {
		
		List<DeptCmpyTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDeptId_1",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DeptCmpyTO>();
		}
		
		return resultList;
	}
	
	public List<DeptCmpyTO> getDeptId_2() {
		
		List<DeptCmpyTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDeptId_2");
		
		if(resultList==null) {
			resultList=new ArrayList<DeptCmpyTO>();
		}
		
		return resultList;
	}
}
