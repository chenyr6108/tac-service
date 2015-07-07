package com.brick.department.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

/**
 * 部门管理
 * 
 * @author li shaojie
 * @date Apr 15, 2010
 * @author wuzhendong
 * @date 2010,7,7
 */

public class DepartmentManage extends AService {
	Log logger = LogFactory.getLog(DepartmentManage.class);

	/**
	 * 获取所有总公司
	 * 
	 * @param context
	 */
	
	public void getCompanys(Context context) {
		Map outputMap = new HashMap();
		List companyList = null;
		try {
			companyList = (List) DataAccessor.query("department.getAllCompanys", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("companyList", companyList);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 根据ID获取所有分公司
	 * 
	 * @param context
	 */
	
	public void getFenCompanys(Context context) {
		Map outputMap = new HashMap();
		List companyFenList = null;
		try {
			companyFenList = (List) DataAccessor.query("department.getFenCompanys", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("companyFenList", companyFenList);
		Output.jsonOutput(outputMap, context);
	}

	
	/**
	 * 根据ID获取公司下的部门
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDept(Context context) {
		Map outputMap = new HashMap();
		List deptList = null;
		try {
			deptList = (List) DataAccessor.query("department.getDept", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("deptList", deptList);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 根据ID获取部门下的部门
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getFenDept(Context context) {
		Map outputMap = new HashMap();
		List fenDeptList = null;
		try {
			fenDeptList = (List) DataAccessor.query("department.getFenDept", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("fenDeptList", fenDeptList);
		Output.jsonOutput(outputMap, context);
	}	
	/**
	 * 添加部门信息
	 * @param context
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void add(Context context) {
		Map deptatcompany = new HashMap();
		try {
			int tiao=Integer.parseInt((String)context.contextMap.get("node"));
			if (tiao==1) {		
				this.commonExecute("department.create", context,DataAccessor.OPERATION_TYPE.INSERT, Output.OUTPUT_TYPE.JSON);
			} else {
				deptatcompany = (Map) DataAccessor.query("department.deptatcompany", context.contextMap,DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("decp_id", deptatcompany.get("DECP_ID"));
				this.commonExecute("department.create2", context,DataAccessor.OPERATION_TYPE.INSERT, Output.OUTPUT_TYPE.JSON);
			}

		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 根据部门id查询部门信息
	 * @param context
	 */
	@SuppressWarnings({ "unchecked" })
	public void getDeptById(Context context){
		Map outputMap=new HashMap();
		Map userMap = new HashMap();
		try {
				userMap = (Map) DataAccessor.query("department.getDeptById",context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("userMap",userMap);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 更新部门
	 * @param context
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	public void update(Context context){
		Map outputMap=new HashMap();
		Map shang=new HashMap();
		Map shang2=new HashMap();
		try {
			//查询一下该部门的上属部门 用于修改和删除
			shang = (Map) DataAccessor.query("department.shang",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if (Integer.parseInt(shang.get("PARENT_ID").toString())==0) {
				outputMap.put("shangID", shang.get("DECP_ID"));
				outputMap.put("shangName",shang.get("DECP_NAME_CN"));
			} else {
				context.contextMap.put("fu", shang.get("PARENT_ID"));
				shang2 = (Map) DataAccessor.query("department.shang2",context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("shangID",shang2.get("ID"));
				outputMap.put("shangName",shang2.get("DEPT_NAME"));
			}
			DataAccessor.execute("department.update", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}	
		Output.jsonOutput(outputMap,context);
	}
	
	/**
	 * 删除部门   查询一下该部门是否有下属部门 或者是否有员工
	 * @param context
	 */
	@SuppressWarnings({ "unchecked" })
	public void delete(Context context){
		Map outputMap=new HashMap();
		@SuppressWarnings("unused")
		List errorList=context.errList;
		List deptList=null;
		List userList=null;
		try {
			//查询一下该部门是否有下属部门
			deptList = (List) DataAccessor.query("department.youDept",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			//查询一下该部门是否员工
			userList = (List) DataAccessor.query("department.youUser",context.contextMap, DataAccessor.RS_TYPE.LIST);				
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("deptListsize", deptList.size());
		outputMap.put("userListsize", userList.size());
		Output.jsonOutput(outputMap,context);
	}
	/**
	 * 删除部门   
	 * @param context
	 */
	@SuppressWarnings({ "unchecked" })
	public void delDept(Context context){
		Map outputMap=new HashMap();
		Map shang=new HashMap();
		Map shang2=new HashMap();
		try {
			//查询一下该部门的上属部门 用于修改和删除
			shang = (Map) DataAccessor.query("department.shang",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			if (Integer.parseInt(shang.get("PARENT_ID").toString())==0) {
				outputMap.put("shangID", shang.get("DECP_ID"));
				outputMap.put("shangName",shang.get("DECP_NAME_CN"));
			} else {
				context.contextMap.put("fu", shang.get("PARENT_ID"));
				shang2 = (Map) DataAccessor.query("department.shang2",context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("shangID",shang2.get("ID"));
				outputMap.put("shangName",shang2.get("DEPT_NAME"));
			}			
			//修改部门状态为删除
			DataAccessor.execute("department.delete", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);						
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap,context);
	}	
	/**
	 * 获取所有部门
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllDepartments(Context context){
		Map outputMap = new HashMap();
		List companyList = null;
		try {
			companyList = (List) DataAccessor.query("department.getAllDepartments", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("companyList", companyList);
		Output.jsonOutput(outputMap, context);
	} 
	/**
	 * 获取部门结构图
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getDepartmentChart(Context context) {
		Map outputMap = new HashMap();
		List departmentList = null;
		try {
			departmentList = (List) DataAccessor.query("department.getDepartmentChart", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("departmentList", departmentList);
		Output.jspOutput(outputMap, context, "/department/deptmentChart.jsp");
		
	}
	/**
	 * 获取所有部门结构图
	 * @param context
	 */	
	@SuppressWarnings("unchecked")
	public void getAllDeptmentChart(Context context) {
		Map outputMap = new HashMap();
		List<List> departmentList = new ArrayList();
		try {
			departmentList = (List) DataAccessor.query("department.getAllDeptmentChart", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("departmentList", departmentList);
		Output.jspOutput(outputMap, context, "/department/allDeptmentChart.jsp");
		
	}
}
