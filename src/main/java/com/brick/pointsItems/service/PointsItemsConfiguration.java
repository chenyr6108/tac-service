package com.brick.pointsItems.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 测评项目配置
 * 
 * @author li shaojie
 * @date May 27, 2010
 */
public class PointsItemsConfiguration extends AService {
	Log logger = LogFactory.getLog(PointsItemsConfiguration.class);
	/**
	 * 测评项目管理
	 * 
	 * @param context
	 */

	@SuppressWarnings("unchecked")
	public void pointsItemsManage(Context context) {
		Map outputMap = new HashMap();
		List pointsItemsTypes = null;
		DataWrap dw=null;
		try {
			context.contextMap.put("dataType", "评分项目类型");
			pointsItemsTypes = (List) DataAccessor.query( "dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			dw=(DataWrap) DataAccessor.query( "pointsItems.getPointsItems", context.contextMap,
						DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("pointsItemsTypes", pointsItemsTypes);
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap, context, "/pointsItems/pointsItemsManage.jsp");
	}
	
	
	public void addPointsItems(Context context){
		try {
			DataAccessor.execute("pointsItems.addPointsItems", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=pointsItems.pointsItemsManage");
	}
	
	@SuppressWarnings("unchecked")
	public void getPointsItemsById(Context context){
		Map pointsItemsMap = new HashMap();
		try {
			pointsItemsMap = (Map) DataAccessor.query( "pointsItems.getPointsItemsById", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Map outputMap = new HashMap();
		outputMap.put("pointsItemsMap", pointsItemsMap);
		Output.jsonOutput(outputMap, context);
	}
	
	public void updatePointsItems(Context context){
		try { 
			DataAccessor.execute("pointsItems.updatePointsItems", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=pointsItems.pointsItemsManage");
	} 
	
	@SuppressWarnings("unchecked")
	public void getSubItemsByPointsId(Context context){
		List list=new ArrayList();
		try {
			list = (List) DataAccessor.query( "pointsItems.getSubItemsByPointsId", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Map outputMap = new HashMap();
		outputMap.put("list", list);
		Output.jsonOutput(outputMap, context);
	}
	
	@SuppressWarnings("unchecked")
	public void configuration(Context context){
		String [] SUBITEM_ID=HTMLUtil.getParameterValues(context.request, "SUBITEM_ID", "");
		String [] SUBITEM_CONTENT=HTMLUtil.getParameterValues(context.request, "SUBITEM_CONTENT", "");
		String [] SUBITEM_POINT=HTMLUtil.getParameterValues(context.request, "SUBITEM_POINT", "");
		for (int i = 0; i < SUBITEM_ID.length; i++) {
			if(SUBITEM_ID[i].equals("")){
				Map map=new HashMap();
				map.put("SUBITEM_CONTENT", SUBITEM_CONTENT[i]);
				map.put("SUBITEM_POINT", SUBITEM_POINT[i]);
				map.put("POINTS_ID", context.contextMap.get("POINTS_ID"));
				try { 
					DataAccessor.execute("pointsItems.insertSubItems", map, DataAccessor.OPERATION_TYPE.INSERT);
				} catch (Exception e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}else{
				Map map=new HashMap();
				map.put("SUBITEM_ID", SUBITEM_ID[i]);
				map.put("SUBITEM_CONTENT", SUBITEM_CONTENT[i]);
				map.put("SUBITEM_POINT", SUBITEM_POINT[i]); 
				try { 
					DataAccessor.execute("pointsItems.updateSubItems", map, DataAccessor.OPERATION_TYPE.UPDATE);
				} catch (Exception e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=pointsItems.pointsItemsManage");
	}
	
	@SuppressWarnings("unchecked")
	public void deleteSubItems(Context context){
		try { 
			 context.contextMap.put("STATUS", 1);
			DataAccessor.execute("pointsItems.updateSubItems", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
}
