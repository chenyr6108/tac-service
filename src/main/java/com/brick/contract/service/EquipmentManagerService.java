package com.brick.contract.service;

import java.util.HashMap;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 设备管理
 * 
 */
public class EquipmentManagerService extends AService {
	Log logger = LogFactory.getLog(EquipmentManagerService.class);

	/**
	 * 设备分页
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getEquipmentManager(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query(
					"equipmentManager.queryEquipmentManagerAll",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap, context,
				"/equipmentManager/equipmentManager.jsp");
	}
	
	

	/**
	 * 回访设备分页
	 * 
	 * @param context
	 */
	public void getEquipmentManagerByBackVisit(Context context) {
		Map outputMap = new HashMap();
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query(
					"equipmentManager.queryEquipmentManagerAll",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		Output.jspOutput(outputMap, context,
				"/backVisit/visitManagerNew.jsp");
	}
}