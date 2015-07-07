package com.brick.area.service;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.exceptions.AppException;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Validation;
import com.ibatis.sqlmap.client.SqlMapClient;



/**
 * @author yangxuan
 * @version Created：2010-4-13 上午11:21:24
 *
 */

public class AreaService extends AService{
	private static final String SUCCESS = "<script type=\"text/javascript\">alert(\"操作成功!\");window.parent.frames['clientTreeFrame'].location.reload(true);</script>";
	private static final String ERROR = "<script type=\"text/javascript\">alert(\"操作失败!\")</script>";
	private static final String DELETE_FK = "<script type=\"text/javascript\">alert(\"不充许删除!\")</script>";
	static Logger logger = Logger.getLogger(AreaService.class);
	static SqlMapClient sqlMapper = null;
	static {
		sqlMapper = DataAccessor.getSession();
	}
	/**
	 * area.createArea
	 * area.createTree
	 * create area
	 * @param context
	 */
	@SuppressWarnings({ "unchecked" })
	public void createArea(Context context) {
		List errorList = context.errList;
		areaValidation(context);
		/**date accessor**/
		try {
			if (errorList.isEmpty()) {
				sqlMapper.startTransaction();
				sqlMapper.insert("area.createArea", context.contextMap);
				context.contextMap.put("parnetLeaf", "N");
				context.contextMap.put("updateId", context.contextMap.get("pid"));
				sqlMapper.update("area.updateParentLeaf", context.contextMap);
				sqlMapper.commitTransaction();
				context.request.getSession().setAttribute("msg", SUCCESS);
				
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			context.request.getSession().setAttribute("msg", ERROR);
		} finally {
			context.request.getSession().setAttribute("msg", SUCCESS);
			context.request.getSession().setAttribute("reload", "success");
			Output.jspSendRedirect(context, context.request.getContextPath()+"/sys/area/area_node.jsp");
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				logger.debug(e);
			}
		}
	}
	
	/**
	 * area.updateArea
	 * area.updateTree
	 * update area by id
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateArea(Context context) {
		List errList = context.errList;
		try {
			if (errList.isEmpty()) {
				DataAccessor.execute("area.updateArea", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				context.request.getSession().setAttribute("msg", SUCCESS);
			}
		} catch (Exception e) {
			logger.debug(e);
			context.request.getSession().setAttribute("msg", ERROR);
		} finally {
			context.request.getSession().setAttribute("msg", SUCCESS);
			context.request.getSession().setAttribute("reload", "success");
			Output.jspSendRedirect(context, context.request.getContextPath()+"/sys/area/area_node.jsp");
//			Output.jspOutput(outputMap, context, "/sys/area/area_list.jsp");
		}
	}
	
	/**
	 * area.deleteById
	 * area.deleteTree
	 * delete area by id
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteArea(Context context) throws AppException {
		List errList = context.errList;
		Map retVal = null;
		try {
			sqlMapper.startTransaction();
			if (errList.isEmpty()) {
				retVal = (Map) DataAccessor.query("area.queryById", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				context.contextMap.put("pid", ((Number)retVal.get("PID")).intValue());
				int count = ((Integer) DataAccessor.query("area.selectSubCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT)).intValue();
				if (count == 1) {
					sqlMapper.update("area.updateToLeaf", context.contextMap);
					//DataAccessor.execute("area.updateToLeaf", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				}
				sqlMapper.delete("area.deleteById", context.contextMap);
				//DataAccessor.execute("area.deleteById", context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
				context.request.getSession().setAttribute("msg", SUCCESS);
			}
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			logger.debug(e.getMessage());
			context.request.getSession().setAttribute("error", DELETE_FK);
		} finally {
			
			context.request.getSession().setAttribute("msg", SUCCESS);
			context.request.getSession().setAttribute("reload", "success");
			Output.jspSendRedirect(context, context.request.getContextPath()+"/sys/area/area_node.jsp");
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.debug(e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void queryAreaById(Context context) {
			List errList = context.errList;
			Map outputMap = null;
			Map retVal = null;
			try {
				if (errList.isEmpty()) {
					outputMap = new HashMap();
					retVal = (Map) DataAccessor.query("area.queryById", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				}
				outputMap.put("result", retVal);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			} finally {
				retVal.put("errList", errList);
				/**
				 * when updae,it needs select data from the method of queryAreaById
				 * but this method that used may need different pages 
				 * so that web page use it,please set paramters "select_update",if you want query object by id,please put 
				 * "select_update" to "N",others put "select_update" to "Y"
				**/
				if ("Y".equals(context.contextMap.get("select_update"))) {
					if (retVal != null) Output.jspOutput(outputMap, context, "/sys/area/area_node_modify.jsp");
				} else {
					if (retVal != null) Output.jspOutput(outputMap, context, "/sys/area/area_node.jsp");
				}
			}
	}
	
	/**用于前台的区域的选择注意一次只操作一个层次*/
	@SuppressWarnings("unchecked")
	public void getSubArea(Context context) {
		List errList = context.errList;
		List outputList = null;
		if (errList.isEmpty()) {
			try {
				outputList = (List) DataAccessor.query("area.getOneSubArea", context.contextMap,DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Output.jsonArrayOutput(outputList, context);
		}
	}
	/**
	 * util
	 * @param context
	 */
	private void areaValidation(Context context) {
		Validation.validateString("区域名","name", context, false, -1, -1);
		Validation.validateString("区号", "lesseearea", context, false, 3, 4);
	}
	/**
	 * 跳转到添加
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void goAddRegion(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		outputMap.put("id", context.contextMap.get("id"));
		outputMap.put("areaName", context.contextMap.get("areaName"));
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap , context, "/sys/area/area_node_add.jsp");
		}
	}
}
