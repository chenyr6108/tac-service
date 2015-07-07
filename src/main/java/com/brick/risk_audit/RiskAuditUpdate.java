package com.brick.risk_audit;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.coderule.service.CodeRule;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * 
 * @author 吴振东
 * @date 下午12:54:45
 */
public class RiskAuditUpdate extends AService{
	Log logger = LogFactory.getLog(RiskAuditUpdate.class);

	/**
	 * 一级评审修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForUpdate_zulin(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map windIdeaMap = null;
		Map riskMemoMap = new HashMap();
		try {
			SelectReportInfo.selectReportInfo_zulin(context, outputMap);
			windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("windIdeaMap", windIdeaMap);
			/*//权限别
			outputMap.put("riskLevel", windIdeaMap.get("RISK_LEVEL"));
			outputMap.put("risk_level_memo", windIdeaMap.get("RISK_LEVEL_MEMO"));*/
			//----------------------------------
			//评审和查看页面，变量定义不统一，为了避免修改后产生BUG，就再定义一个统一的变量，传到前台。
			outputMap.put("windMap", windIdeaMap);
			//----------------------------------
			
			//评审内容
			context.contextMap.put("PRCM_USER_LEVEL",0);
			riskMemoMap= (Map) DataAccessor.query("riskAudit.selectRiskMemoListForUpdate",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("riskMemoMap",riskMemoMap);
			
			//设置参数,因为有些地方map里的key是小写,统一写成大写add by ShenQi
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
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("初级评审修改失败");
		} finally {
			if(errList.isEmpty()){
//				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_UpdateFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
//				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
			Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_UpdateFrame.jsp");
		}
	}
	
	/**
	 * 一级评审里面的测评分修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForUpdate_fen(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List xuanList = null;
		Map creditMap = null;
		Map windIdeaMap = null;
		List fenTypes = null;
		List fenList = null;
		List psTypeList = null;
		try {
			if(!"3".equals(context.contextMap.get("typeOfContract"))) {
				//非重车评分表部分
				windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(windIdeaMap==null){
					windIdeaMap=new HashMap();
				}
				windIdeaMap.put("prc_id", context.contextMap.get("prc_id"));
				context.contextMap.put("fenTy", "评分项目类型");
				xuanList = (List) DataAccessor.query("riskAuditUpdate.selectXuanFen",context.contextMap, DataAccessor.RS_TYPE.LIST);
				fenTypes = (List) DataAccessor.query("riskAuditUpdate.selectFenType",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				fenList = (List) DataAccessor.query("riskAudit.selectFenForUpdate",context.contextMap, DataAccessor.RS_TYPE.LIST);
				//评审行业类型
				context.contextMap.put("dataType","评审行业类型");
				psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("psTypeList", psTypeList); 			

				outputMap.put("fenshu", xuanList.size());
				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("createPrc_id", context.contextMap.get("createPrc_id"));
				outputMap.put("createType", context.contextMap.get("createType"));
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("windIdeaMap",windIdeaMap);
				outputMap.put("xuanList",xuanList);
				outputMap.put("fenType",fenTypes.size());
				outputMap.put("fenTypes",fenTypes);
				outputMap.put("showFlag",1);	
				outputMap.put("creditMap", creditMap);
				outputMap.put("fenList", fenList);
				outputMap.put("typeOfContract", context.contextMap.get("typeOfContract"));
			} else {
				//重车评分表部分 add by Shen Qi
				windIdeaMap= (Map) DataAccessor.query("riskAuditUpdate.selectWindIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
				if(windIdeaMap==null){
					windIdeaMap=new HashMap();
				}
				windIdeaMap.put("prc_id", context.contextMap.get("prc_id"));
				context.contextMap.put("fenTy", "评分项目类型");
				xuanList = (List) DataAccessor.query("riskAuditUpdate.selectXuanFen",context.contextMap, DataAccessor.RS_TYPE.LIST);
				fenTypes = (List) DataAccessor.query("riskAuditUpdate.selectFenType",context.contextMap, DataAccessor.RS_TYPE.LIST);	
				creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				fenList = (List) DataAccessor.query("riskAudit.selectFenForUpdate",context.contextMap, DataAccessor.RS_TYPE.LIST);

				outputMap.put("fenshu", xuanList.size());
				outputMap.put("prc_id", context.contextMap.get("prc_id"));
				outputMap.put("createPrc_id", context.contextMap.get("createPrc_id"));
				outputMap.put("createType", context.contextMap.get("createType"));
				outputMap.put("credit_id", context.contextMap.get("credit_id"));
				outputMap.put("windIdeaMap",windIdeaMap);
				outputMap.put("xuanList",xuanList);
				outputMap.put("fenType",fenTypes.size());
				outputMap.put("fenTypes",fenTypes);
				outputMap.put("showFlag",1);	
				outputMap.put("creditMap", creditMap);
				outputMap.put("fenList", fenList);
				outputMap.put("typeOfContract", context.contextMap.get("typeOfContract"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("一级评审--风控会议纪要测评记分表修改页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_UpdateFrame.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}	
	/**
	 *一级评审修改
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatewind(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		String istype="";
		String istypeId="";
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {	
			sqlMapper.startTransaction() ;
			
			/*String visitor_id = (String) context.contextMap.get("visitor_id");
			String visit_date = (String) context.contextMap.get("visit_date");
			if (StringUtils.isEmpty(visitor_id)) {
				throw new Exception("访厂人员不能为空。");
			}
			if (StringUtils.isEmpty(visit_date)) {
				throw new Exception("访厂时间不能为空。");
			}*/
			
			//Add by Michael 2012 11/26 增加支票还款 状况
			sqlMapper.update("riskAudit.updatecreditCheckPay", context.contextMap);
			//Add by Michael 2012 11/26 增加支票还款 明细
			sqlMapper.delete("riskAudit.deleteCreditSchemaCheck", context.contextMap);
			String[] CHECK_PAY_START = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_START", "0");
			String[] CHECK_PAY_END = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_END", "0");
			
			for (int i=0; i<CHECK_PAY_START.length; i++) {
				Map paramMap = new HashMap();
				paramMap.put("CHECK_PAY_START", DataUtil.intUtil(CHECK_PAY_START[i]));
				paramMap.put("CHECK_PAY_END", DataUtil.intUtil(CHECK_PAY_END[i]));
				paramMap.put("credit_id", context.contextMap.get("credit_id"));
				paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
				//TYPE 1:表示是自行输入 ；0：表示是审查录入
				paramMap.put("TYPE", "0");
				sqlMapper.insert("riskAudit.addCreditSchemaCheck", paramMap);				
			}
			
			//修改供应商担保 add by ShenQi
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			sqlMapper.update("riskAudit.updateSupl",context.contextMap);
			
			//担保人本票修改
			String creditCustTypes=context.request.getParameter("creditCustType");
			String[] creditCustType=creditCustTypes.split(",");
			
			String corpManBox=context.request.getParameter("corpManBox");
			String[] corpManBoxOne=corpManBox.split(";");
			
			for(int i=0;i<corpManBoxOne.length;i++)
			{
				String corpManBoxOneSun=corpManBoxOne[i];
				String[] corpManIsType=corpManBoxOneSun.split(",");
				for(int j=0;j<corpManIsType.length;j++)
				{
					if(corpManIsType.length>1)
					{
						istypeId=corpManIsType[0];
						istype=corpManIsType[1];
						context.contextMap.put("istypeId", istypeId);
						context.contextMap.put("istype", istype);
					
						if(Integer.parseInt(creditCustType[i])==0)
						{
							sqlMapper.update("beforeMakeContract.updateCorpByIsType", context.contextMap);
//							DataAccessor.execute("beforeMakeContract.updateCorpByIsType", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						}
						else if(Integer.parseInt(creditCustType[i])==1)
						{
							sqlMapper.update("beforeMakeContract.updateNatuByIsType", context.contextMap);
//							DataAccessor.execute("beforeMakeContract.updateNatuByIsType", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						}
					}
				}
			}
			
			
			
			
			
			
			if (context.contextMap.get("memo").equals("111")) {
				//复位	 资信状态
				sqlMapper.update("riskAuditUpdate.upCreditstate", context.contextMap);	
//				DataAccessor.execute("riskAuditUpdate.upCreditstate", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
				//复位	 风控状态
				sqlMapper.update("riskAuditUpdate.upCstate", context.contextMap);	
//				DataAccessor.execute("riskAuditUpdate.upCstate", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
				//提交			
				sqlMapper.update("riskAudit.updatetowind", context.contextMap);	
//				DataAccessor.execute("riskAudit.updatetowind", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
				//修改风控备注表
				context.contextMap.put("memoLevelUrl",0);
				sqlMapper.update("riskAuditUpdate.upRiskMemoForSummit", context.contextMap);
//				DataAccessor.execute("riskAuditUpdate.upRiskMemoForSummit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			} else {	
				//修改风控备注表
				context.contextMap.put("memoLevelUrl",0);
				//sqlMapper.update("riskAuditUpdate.upRiskMemoForResult", context.contextMap);
				if(context.contextMap.get("memo").equals("1")){
					sqlMapper.update("riskAuditUpdate.upRiskMemoForResult_forpass", context.contextMap);
				}else{
					sqlMapper.update("riskAuditUpdate.upRiskMemoForResult", context.contextMap);
				}
//				DataAccessor.execute("riskAuditUpdate.upRiskMemoForResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
				//结案修改风控内容 结果
				sqlMapper.update("riskAudit.updatetowindfeng", context.contextMap);
//				DataAccessor.execute("riskAudit.updatetowindfeng", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				//结案修改资信表风控状态
				sqlMapper.update("riskAudit.updatecredit", context.contextMap);
//				DataAccessor.execute("riskAudit.updatecredit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				//当为补充调查时
				if (context.contextMap.get("memo").equals("3")) {
					//结案修改资信表state=0
					sqlMapper.update("riskAudit.updatecreditstate", context.contextMap);					
//					DataAccessor.execute("riskAudit.updatecreditstate", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);					
				}
				//当为无条件通过 有条件通过时 
				if (context.contextMap.get("memo").equals("1") || context.contextMap.get("memo").equals("2")) {
					//结案修改资信表中的合同号
					
					//Marked by Michael 2012 02/08 如果合同号存在， 则不再重新产生合同号
//					String le_code=CodeRule.generateRentContractCode(context.contextMap.get("credit_id"));
//					context.contextMap.put("le_code", le_code);

//					//取得合同号 如果存在则不创建新的
//					String leaseCode = (String) DataAccessor.query("riskAudit.selectCreditLeaseCode", context.contextMap,RS_TYPE.OBJECT);
//					if(leaseCode != null && !"".equals(leaseCode)){
//						context.contextMap.put("le_code", leaseCode);
//					} else {
//						String le_code=CodeRule.generateRentContractCode(context.contextMap.get("credit_id"));
//						context.contextMap.put("le_code", le_code);
//					}
					
					//Modify by Michael 2012 02/08 如果合同号存在， 则不再重新产生合同号-------------------------------
					String leaseCode = (String) DataAccessor.query("riskAudit.selectCreditLeaseCode", context.contextMap,RS_TYPE.OBJECT);
					if(leaseCode != null && !"".equals(leaseCode)){
						context.contextMap.put("le_code", leaseCode);
					} else {
						String le_code=CodeRule.generateRentContractCode(context.contextMap.get("credit_id"));
						context.contextMap.put("le_code", le_code);
					}
					//----------------------------------------------------------------------------------------------------------
					
					sqlMapper.update("riskAudit.updatecreditLeaseCode", context.contextMap);
//					DataAccessor.execute("riskAudit.updatecreditLeaseCode", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					//结案修改风控表的真正流水号
					String real_code=CodeRule.geneRiskCode(context.contextMap.get("credit_id"));
					context.contextMap.put("real_code", real_code);	
					//修改时判断是否有之前的编号如果有则使用之前的
					RiskAudit.backRealPrcHao(context) ;
					sqlMapper.update("riskAudit.updateWindRealCode", context.contextMap);
					
//					DataAccessor.execute("riskAudit.updateWindRealCode", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);										
				}				
			}			
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			errList.add("初级评审--修改失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e.getMessage());
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,"defaultDispatcher?__action=riskAuditUpdate.selectRiskAuditForUpdate_fen&credit_id="+context.contextMap.get("credit_id")+"&prc_id="+context.contextMap.get("prc_id")+"&createPrc_id="+context.contextMap.get("prc_id")+"&createType=1");
		} else {
//			outputMap.put("errList", errList);
//			Output.jspOutput(outputMap, context, "/error.jsp") ;
			context.setErrList(errList);
			context.contextMap.put("showFlag", 1);
			context.contextMap.put("flag", 1);
			selectRiskAuditForUpdate_zulin(context);
		}
	}	

	/**
	 * ajax查测评分详细
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectXiangFen(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		List xiangfenList = null;
		try {
			context.contextMap.put("fenTy", "评分项目类型");
			xiangfenList = (List) DataAccessor.query("riskAuditUpdate.selectXiangFen",context.contextMap, DataAccessor.RS_TYPE.LIST);	
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目评审--查测评分详细错误!请联系管理员");
		} finally {
			outputMap.put("xiangfenList", xiangfenList);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	/**
	 * 修改测评分表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void inserttopoint(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			sqlMapper.delete("riskAuditUpdate.deletepoint", context.contextMap);
			//System.out.println("=========");
//			DataAccessor.execute("riskAuditUpdate.deletepoint",context.contextMap,DataAccessor.OPERATION_TYPE.DELETE);
			String[] fen_id = HTMLUtil.getParameterValues(context.getRequest(),"fen_id","");
			for (int i=0;i < fen_id.length;i++) {
				int z=0;
				z=i+1;

				Map map=new HashMap();
				map.put("fen_id", fen_id[i]);
				
				map.put("psTypeBuut", context.contextMap.get("psTypeBuut"));
				map.put("prc_id", context.contextMap.get("prc_id"));
							
				String neirong = "fencontext"+Integer.toString(z);
				map.put("fencontext",context.contextMap.get(neirong).toString());
								
				String q=context.contextMap.get("credit_id").toString();
				map.put("credit_id", Integer.parseInt(q));
								
				String s = "fen"+Integer.toString(z);
				map.put("fen",context.contextMap.get(s));
				sqlMapper.insert("riskAudit.inserttopoint", map);
//				DataAccessor.execute("riskAudit.inserttopoint",map,DataAccessor.OPERATION_TYPE.INSERT);
			}	
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			errList.add("修改测评分失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,"defaultDispatcher?__action=riskAudit.riskAudit");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/errList.jsp") ;
		}
	}	
}
