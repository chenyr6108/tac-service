package com.brick.risk_audit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;



/**
 * 
 * @author 吴振东
 * @date 下午12:54:45
 */
public class RiskAuditSee extends BaseCommand{
	Log logger = LogFactory.getLog(RiskAuditSee.class);
	public final static Integer pointPageCount = Integer.valueOf(10);//每页显示的条数
	/**
	 * 风控会议纪要里面的风控部意见查看
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSee_zulin(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map windIdeaMap = null;
		List riskMemoList = null;
		try {
			SelectReportInfo.selectReportInfo_zulin(context, outputMap);
			windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
			riskMemoList= (List) DataAccessor.query("riskAudit.selectRiskMemoList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("windIdeaMap", windIdeaMap);
			/*//权限别
			outputMap.put("riskLevel", windIdeaMap.get("RISK_LEVEL"));
			outputMap.put("risk_level_memo", windIdeaMap.get("RISK_LEVEL_MEMO"));*/
			//----------------------------------
			//评审和查看页面，变量定义不统一，为了避免修改后产生BUG，就再定义一个统一的变量，传到前台。
			outputMap.put("windMap", windIdeaMap);
			//----------------------------------
			outputMap.put("riskMemoList",riskMemoList);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			//设置参数,判断是从查看页面跳转 add by ShenQi, used in riskAuditCorp.jsp
			outputMap.put("view",true);
			
			//设置参数,因为有些地方map里的key是小写,统一写成大写 add by ShenQi
			context.contextMap.put("CREDIT_ID", context.contextMap.get("credit_id"));
			Integer typeOfContract=(Integer) DataAccessor.query("riskAuditUpdate.checkContractType",context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			//typeOfContract=3是重车合同,其他的是0,1,2
			outputMap.put("typeOfContract", String.valueOf(typeOfContract));
			//2012/03/19 Yang Yun 共案查询
			List<Map<String, Object>> mergedList = (List<Map<String, Object>>) DataAccessor.query("riskAudit.getMergedByProject", context.contextMap, RS_TYPE.LIST);
			outputMap.put("mergedList", mergedList);
			//Add by Michael 2012 11-26 增加支票还款明细
			List<Map<String, Object>> checkPaylines=(List<Map<String, Object>>) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, RS_TYPE.LIST);
			outputMap.put("checkPaylines", checkPaylines);
			
			//附件相关
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("demandId", context.contextMap.get("credit_id"));
			paramMap.put("fileType", "risk");
			List<Map<String, Object>> fileList = (List<Map<String, Object>>) baseService.queryForList("demand.getFilesByDemandId", paramMap);
			outputMap.put("fileList", fileList);
			outputMap.put("bootPath", "riskFile");
			outputMap.put("credit_id",context.contextMap.get("credit_id"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目评审--查看风控现场调查报告错误!请联系管理员");
		} finally {
			Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_SeeFrame.jsp");
		}
	}
	
	/**
	 * 风控会议纪要里面的测评分查看
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForSee_fen(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List xuanList = null;
		Map creditMap = null;
		Map windIdeaMap = null;
		List fenTypes = null;
		List psTypeList = null;
		try {
			context.contextMap.put("fenTy", "评分项目类型");
			xuanList = (List) DataAccessor.query("riskAuditUpdate.selectXuanFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
			windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
			fenTypes = (List) DataAccessor.query("riskAuditUpdate.selectFenType",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//评审行业类型
			context.contextMap.put("dataType","评审行业类型");
			psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("psTypeList", psTypeList); 			
			outputMap.put("fenshu", xuanList.size());
			outputMap.put("prc_id", context.contextMap.get("prc_id"));
			outputMap.put("xuanList",xuanList);
			outputMap.put("showFlag",1);	
			outputMap.put("creditMap", creditMap);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("windIdeaMap", windIdeaMap);
			outputMap.put("fenType",fenTypes.size());
			outputMap.put("fenTypes",fenTypes);
			outputMap.put("typeOfContract",context.contextMap.get("typeOfContract"));
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目评审--查看风控测评记分表错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_SeeFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}	
	/**
	 * 测评分等级管理页面one
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void fenOrder(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map fenMap = null;
		Map sumMap = null;
		List fenOrderList = new ArrayList();
		int barNum = 0; // 记录条数
		int total = 0; // 总页数
		Map count = new HashMap();
		Map risk_state = new HashMap();
		String id="";
		String id2="";
		String pageStr = HTMLUtil.getStrParam(context.getRequest(), "pageNum","0");// 请求的页 pageNum 返回的当前页数
		int page = Integer.parseInt(pageStr);

		 id = HTMLUtil.getStrParam(context.getRequest(), "credit_id", "1");
		 id2 = HTMLUtil.getStrParam(context.getRequest(), "prc_id", "1");
		
		context.contextMap.put("credit_id", Integer.parseInt(id));
		context.contextMap.put("prc_id", Integer.parseInt(id2));

		
		if (errList.isEmpty()) {
			try {	 
					fenMap = (Map) DataAccessor.query("riskAuditUpdate.fen_page",context.contextMap, DataAccessor.RS_TYPE.MAP);
					sumMap = (Map) DataAccessor.query("riskAuditUpdate.sumFenOrder",context.contextMap, DataAccessor.RS_TYPE.MAP);
					risk_state = (Map) DataAccessor.query("riskAuditUpdate.risk_state",context.contextMap, DataAccessor.RS_TYPE.MAP);
					if (fenMap!=null) {
						int fen_page=DataUtil.intUtil(fenMap.get("LEV"))/pointPageCount;
						if(DataUtil.intUtil(fenMap.get("LEV"))%pointPageCount==0) {
							page=fen_page;
						} else {
							page=fen_page+1;
						}
					}	

				barNum = ((Integer) DataAccessor.query( "riskAuditUpdate.getFenOrder_count", context.contextMap, RS_TYPE.OBJECT)).intValue();

				total = (barNum + pointPageCount-1) / pointPageCount;
				count.clear();				
				if (page <= 1) {
					context.contextMap.put("str",1);
					context.contextMap.put("end",pointPageCount);
					fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
					count.put("pageNum", 1);
					count.put("total", total);
					count.put("barNum", barNum);
				}		
				if (page > 1) {
					if (page <= total) {
						context.contextMap.put("str", (page - 1) * pointPageCount + 1);
						context.contextMap.put("end", page * pointPageCount);
						fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
						count.put("pageNum", page);
						count.put("total", total);
						count.put("barNum", barNum);
					}
					if (page > total) {
						context.contextMap.put("str", (total - 1) * pointPageCount + 1);
						context.contextMap.put("end", total * pointPageCount);
						fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
						count.put("pageNum", total);
						count.put("total", total);
						count.put("barNum", barNum);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目评审--风控会议纪要 排名错误!请联系管理员");
			}
		}

		if (errList.isEmpty()) {
			outputMap.put("count", count);	
			outputMap.put("risk_state", risk_state);
			outputMap.put("fenOrderList", fenOrderList);
			outputMap.put("fenMap", fenMap);
			outputMap.put("sumMap", sumMap);
			outputMap.put("prc_id",id);
			outputMap.put("credit_id",id2);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList) ;
		}
	}
	
	/**
	 * 测评分等级管理页面other
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void fenOrderOther(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map fenMap = null;
		Map sumMap = null;
		List fenOrderList = new ArrayList();
		int barNum = 0; // 记录条数
		int total = 0; // 总页数
		Map count = new HashMap();
		Map risk_state = new HashMap();
		String id="";
		String id2="";
		String pageStr = HTMLUtil.getStrParam(context.getRequest(), "pageNum","0");// 请求的页 pageNum 返回的当前页数
		int page = Integer.parseInt(pageStr);

		 id = HTMLUtil.getStrParam(context.getRequest(), "credit_id", "1");
		 id2 = HTMLUtil.getStrParam(context.getRequest(), "prc_id", "1");
		
		context.contextMap.put("credit_id", Integer.parseInt(id));
		context.contextMap.put("prc_id", Integer.parseInt(id2));

		
		if (errList.isEmpty()) {
			try {	    	
					fenMap = (Map) DataAccessor.query("riskAuditUpdate.fen_page",context.contextMap, DataAccessor.RS_TYPE.MAP);
					sumMap = (Map) DataAccessor.query("riskAuditUpdate.sumFenOrder",context.contextMap, DataAccessor.RS_TYPE.MAP);	
					risk_state = (Map) DataAccessor.query("riskAuditUpdate.risk_state",context.contextMap, DataAccessor.RS_TYPE.MAP);
				barNum = ((Integer) DataAccessor.query( "riskAuditUpdate.getFenOrder_count", context.contextMap, RS_TYPE.OBJECT)).intValue();

				total = (barNum + pointPageCount-1) / pointPageCount;
				count.clear();	
				
				if (page <= 1) {

					context.contextMap.put("str",1);
					context.contextMap.put("end",pointPageCount);
					fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
					count.put("pageNum", 1);
					count.put("total", total);
					count.put("barNum", barNum);
				}
			
				if (page > 1) {

					if (page <= total) {

						context.contextMap.put("str", (page - 1) * pointPageCount + 1);
						context.contextMap.put("end", page * pointPageCount);
						fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
						count.put("pageNum", page);
						count.put("total", total);
						count.put("barNum", barNum);
					}

					if (page > total) {

						context.contextMap.put("str", (total - 1) * pointPageCount + 1);
						context.contextMap.put("end", total * pointPageCount);
						fenOrderList = (List) DataAccessor.query("riskAuditUpdate.getFenOrder", context.contextMap,DataAccessor.RS_TYPE.LIST);
						count.put("pageNum", total);
						count.put("total", total);
						count.put("barNum", barNum);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("项目评审--风控会议纪要排名(其他)错误!请联系管理员");
			}
		}

		if (errList.isEmpty()) {
			outputMap.put("count", count);	
			outputMap.put("risk_state", risk_state);	
			outputMap.put("fenOrderList", fenOrderList);
			outputMap.put("fenMap", fenMap);
			outputMap.put("sumMap", sumMap);
			outputMap.put("prc_id",id);
			outputMap.put("credit_id",id2);
			Output.jsonOutput(outputMap, context);
		}	else {
			outputMap.put("errList", errList) ;
		}
	}	
}
