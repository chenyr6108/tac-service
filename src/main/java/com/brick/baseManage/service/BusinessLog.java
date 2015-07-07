package com.brick.baseManage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 系统业务日志
 * 
 * @author li shaojie
 * @date 6- 8, 2010
 */

public class BusinessLog extends BaseCommand {
	static Log logger = LogFactory.getLog(BusinessLog.class);

	/**
	 * 添加系统业务日志
	 * 
	 * @param creditId 资信id
	 * @param contractId 合同ID
	 * @param logType 日志类型（如：现场调查报告）
	 * @param logTitle日志标题（如：生成）
	 * @param logCode 编号（生成对象的编号）
	 * @param memo  备注
	 * @param state  状态 1 使用中 2未使用
	 * @param userId 创建人 
	 * @param otherId  其他ID（备用）
	 */
	@SuppressWarnings("unchecked")
	public static void addBusinessLog (Long creditId, Long contractId,
			String logType, String logTitle, String logCode, String memo,
			Integer state, Long userId, Long otherId ,SqlMapClient sqlMapper,String ip) throws Exception  {
		sqlMapper.startBatch() ;
		Map map = new HashMap();
		if (creditId == null) {
			map.put("creditId", "");
		} else {
			map.put("creditId", creditId);
		}
		if (contractId == null) {
			map.put("contractId", "");
		} else {
			map.put("contractId", contractId);
		}
		map.put("logType", logType);
		map.put("logTitle", logTitle);
		map.put("logCode", logCode);
		map.put("memo", memo);
		map.put("state", state);
		map.put("userId", userId);
		map.put("ip", ip);
		if (otherId == null) {
			map.put("otherId", "");
		} else {
			map.put("otherId", otherId);
		}
		sqlMapper.insert("sysBusinessLog.add", map);
		sqlMapper.executeBatch() ;
//		try {
//			DataAccessor.execute("sysBusinessLog.add", map,
//					DataAccessor.OPERATION_TYPE.INSERT);
//		} catch (Exception e) {
//			e.printStackTrace();
//			LogPrint.getLogStackTrace(e, logger);
//		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addBusinessLog (Long creditId, Long contractId,
			String logType, String logTitle, String logCode, String memo,
			Integer state, Long userId, Long otherId,String ip) {
		Map map = new HashMap();
		if (creditId == null) {
			map.put("creditId", "");
		} else {
			map.put("creditId", creditId);
		}
		if (contractId == null) {
			map.put("contractId", "");
		} else {
			map.put("contractId", contractId);
		}
		map.put("logType", logType);
		map.put("logTitle", logTitle);
		map.put("logCode", logCode);
		map.put("memo", memo);
		map.put("state", state);
		map.put("userId", userId);
		map.put("ip", ip);
		if (otherId == null) {
			map.put("otherId", "");
		} else {
			map.put("otherId", otherId);
		}
		try {
			DataAccessor.execute("sysBusinessLog.add", map,
					DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}	

	/**
	 * 日志管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void manage(Context context) {
		Map outputMap = new HashMap();
		PagingInfo<Object> pagingInfo = null;
		List errList = context.errList ;
		if(context.contextMap.get("start_date")==null||"".equals(context.contextMap.get("start_date"))) {
			context.contextMap.put("start_date",DateUtil.getCurrentDate());
		}
		try {
			pagingInfo = baseService.queryForListWithPaging("sysBusinessLog.manage", context.contextMap, "create_date", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("系统设置--系统日志列表错误!请联系管理员");
		}
		if(errList.isEmpty()){
			outputMap.put("pagingInfo", pagingInfo);
			outputMap.put("content", context.contextMap.get("content"));
			outputMap.put("start_date", context.contextMap.get("start_date"));
			outputMap.put("end_date", context.contextMap.get("end_date"));
			Output.jspOutput(outputMap, context, "/baseManage/businessLog/businessLogManage.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 日志查看 cheng
	 * 
	 * @param context  BusinessLog 需要提供 方法 支持 T_PRJT_CREDIT 的CREDIT_ID /PRJT_ID 来查询
	 *            LOG 然后显示在页面弹出层中 ajax 分页
	 */

	@SuppressWarnings("unchecked")
	public void showBusinessLog(Context context) {

		List errList = context.errList;

		Map outputMap = new HashMap();
		List log = new ArrayList();
		int barNum = 0; // 记录条数
		int total = 0; // 总页数
		Map count = new HashMap();
		String pageStr = HTMLUtil.getStrParam(context.getRequest(), "pageNum","0");// 请求的页 pageNum 返回的当前页数
		int page = Integer.parseInt(pageStr);

		String id = HTMLUtil.getStrParam(context.getRequest(), "PRJT_ID", "1");
		String id2 = HTMLUtil.getStrParam(context.getRequest(), "PRCD_ID", "1");
		
		context.contextMap.put("PRJT_ID", Integer.parseInt(id));
		context.contextMap.put("PRCD_ID", Integer.parseInt(id2));

		
		if (errList.isEmpty()) {

			try {
			    	if(id.equals("1")){
				    count = (Map) DataAccessor.query("sysBusinessLog.selectCredit", context.contextMap, RS_TYPE.MAP);
				  
				    context.contextMap.put("PRJT_ID", count.get("credit_id"));
			    	}
			    	

				barNum = ((Integer) DataAccessor.query( "sysBusinessLog.showLog_count", context.contextMap, RS_TYPE.OBJECT)).intValue();

				total = (barNum + 9) / 10;
				count.clear();	
				
				if (page <= 1) {

					context.contextMap.put("str", 1);
					context.contextMap.put("end", 10);
					log = (List) DataAccessor.query("sysBusinessLog.showLog",context.contextMap, RS_TYPE.LIST);

					for(int i=0;i<log.size();i++) {
						if(((Map)(log.get(i))).get("MEMO")==null||"".equals(((Map)(log.get(i))).get("MEMO"))) {
							
						} else {
							((Map)(log.get(i))).put("MEMO", StringUtils.autoInsertWrap(((Map)(log.get(i))).get("MEMO").toString(), 30));
						}
					}
					count.put("pageNum", 1);
					count.put("total", total);
					count.put("barNum", barNum);
				}

				if (page > 1) {

					if (page <= total) {

						context.contextMap.put("str", (page - 1) * 10 + 1);
						context.contextMap.put("end", page * 10);
						log = (List) DataAccessor.query(
								"sysBusinessLog.showLog", context.contextMap,
								RS_TYPE.LIST);
						count.put("pageNum", page);
						count.put("total", total);
						count.put("barNum", barNum);

					}

					if (page > total) {

						context.contextMap.put("str", (total - 1) * 10 + 1);
						context.contextMap.put("end", total * 10);
						log = (List) DataAccessor.query(
								"sysBusinessLog.showLog", context.contextMap,
								RS_TYPE.LIST);
						count.put("pageNum", total);
						count.put("total", total);
						count.put("barNum", barNum);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("系统设置--系统日志查看错误!请联系管理员");
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("log", log);
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	//插入系统日志加入IP记录 add by ShenQi
	@SuppressWarnings("unchecked")
	public static void addBusinessLogWithIp(Long creditId,Long contractId,String logType,String logTitle,String logCode,String memo,
			Integer state,Long userId,Long otherId,String ip) {
		
		Map<String,Object> paramMap=new HashMap<String,Object>();
		if(creditId==null) {
			paramMap.put("creditId","");
		} else {
			paramMap.put("creditId",creditId);
		}
		if(contractId==null) {
			paramMap.put("contractId","");
		} else {
			paramMap.put("contractId",contractId);
		}
		if(otherId==null) {
			paramMap.put("otherId","");
		} else {
			paramMap.put("otherId",otherId);
		}
		paramMap.put("logType",logType);
		paramMap.put("logTitle",logTitle);
		paramMap.put("logCode",logCode);
		paramMap.put("memo",memo);
		paramMap.put("state",state);
		paramMap.put("userId",userId);
		paramMap.put("ip",ip);
		
		try {
			DataAccessor.execute("sysBusinessLog.add",paramMap,DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
}
