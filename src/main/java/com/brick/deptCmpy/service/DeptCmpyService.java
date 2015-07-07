package com.brick.deptCmpy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.deptCmpy.dao.DeptCmpyDAO;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.deptCmpy.to.TreeTO;
import com.brick.util.DeptMapListener;

public class DeptCmpyService extends BaseService {

	private DeptCmpyDAO deptCmpyDAO;

	public DeptCmpyDAO getDeptCmpyDAO() {
		return deptCmpyDAO;
	}

	public void setDeptCmpyDAO(DeptCmpyDAO deptCmpyDAO) {
		this.deptCmpyDAO = deptCmpyDAO;
	}
	
	public List<DeptCmpyTO> getCompanyList() throws Exception {
		return this.baseDAO.getCompanyList1();
	}
	
	public List<DeptCmpyTO> queryDeptCmpy(Map<String,Object> param) throws Exception {
		return this.deptCmpyDAO.queryDeptCmpy(param);
	}
	
	public List<DeptCmpyTO> getDeptList(Map<String,Object> param) throws Exception {
		return this.baseDAO.getDeptsList(param);
	}
	
	public List<Map<String,Object>> queryDataDictionary(Map param) throws Exception {
		
		List<Map<String,Object>> result=null;
		
		result=this.baseDAO.queryDataDictionary(param);
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	public void batchUpdateDept(Map<String,Object> param) throws Exception {
		
		if(param.get("upperDeptId")!=null&&param.get("deptMgr")!=null&&param.get("classId")!=null) {
			DeptMapListener.isLock="N";
			param.put("flag","1");
		} else if(param.get("upperDeptId")!=null&&param.get("deptMgr")==null&&param.get("classId")==null) {
			DeptMapListener.isLock="N";
			param.put("flag","2");
		} else if(param.get("upperDeptId")==null&&param.get("deptMgr")!=null&&param.get("classId")==null) {
			param.put("flag","3");
		} else if(param.get("upperDeptId")==null&&param.get("deptMgr")==null&&param.get("classId")!=null) {
			param.put("flag","4");
		} else if(param.get("upperDeptId")!=null&&param.get("deptMgr")!=null&&param.get("classId")==null) {
			DeptMapListener.isLock="N";
			param.put("flag","5");
		} else if(param.get("upperDeptId")==null&&param.get("deptMgr")!=null&&param.get("classId")!=null) {
			param.put("flag","6");
		} else if(param.get("upperDeptId")!=null&&param.get("deptMgr")==null&&param.get("classId")!=null) {
			DeptMapListener.isLock="N";
			param.put("flag","7");
		}
		
		this.deptCmpyDAO.batchUpdateDept(param);
	}
	
	public void addDept(Map param) throws Exception {
		this.deptCmpyDAO.addDept(param);
	}
	
	public List<TreeTO> getTreeList() throws Exception {
		return this.deptCmpyDAO.getTreeList();
	}
}
