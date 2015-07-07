package com.brick.risk_audit;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.coderule.service.CodeRule;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * 
 * @author 吴振东
 * @date 下午12:54:45
 */
public class RiskAuditUpdateAfter extends AService{
	Log logger = LogFactory.getLog(RiskAuditUpdateAfter.class);

	/**
	 * 二、三、四级评审修改页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForUpdate_zulinAfter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		Map windIdeaMap = null;
		List riskMemoList = null;
		Map riskMemoMap = new HashMap();
		Map levelMap = new HashMap();
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
			
			outputMap.put("prc_node", context.contextMap.get("prc_node"));
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			//评审内容
			context.contextMap.put("PRCM_USER_LEVEL",Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			riskMemoMap= (Map) DataAccessor.query("riskAudit.selectRiskMemoListForUpdate",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("riskMemoMap",riskMemoMap);
			//所有的
			riskMemoList= (List) DataAccessor.query("riskAudit.selectRiskMemoList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("riskMemoList",riskMemoList);
			//等级配置
			context.contextMap.put("rank", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			levelMap = (Map) DataAccessor.query("riskAudit.selectLevelMap",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("levelMap", levelMap);
			//2012/03/19 Yang Yun 共案查询
			List<Map<String, Object>> mergedList = (List<Map<String, Object>>) DataAccessor.query("riskAudit.getMergedByProject", context.contextMap, RS_TYPE.LIST);
			outputMap.put("mergedList", mergedList);
			//Add by Michael 2012 11-26 增加支票还款明细
			List<Map<String, Object>> checkPaylines=(List<Map<String, Object>>) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, RS_TYPE.LIST);
			outputMap.put("checkPaylines", checkPaylines);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("二、三、四级评审--风控会议纪要现场调查报告修改页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_UpdateFrameAfter.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}
	/**
	 * 二、三、四级评审---测评分修改页面
	 * 
	 * @param context
	 */
	
	@SuppressWarnings("unchecked")
	public void selectRiskAuditForUpda_fenAfter(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList ;
		
		List xuanList = null;
		Map creditMap = null;
		Map windIdeaMap = null;
		Map windMap = null;
		List fenTypes = null;
		List psTypeList = null;
		try {
			windMap= (Map) DataAccessor.query("riskAudit.selectWindExplain",context.contextMap, DataAccessor.RS_TYPE.MAP);
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
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("prc_node", context.contextMap.get("prc_node"));
			outputMap.put("prc_id", context.contextMap.get("prc_id"));
			outputMap.put("xuanList",xuanList);
			outputMap.put("showFlag",1);	
			outputMap.put("creditMap", creditMap);
			outputMap.put("windMap", windMap);
			outputMap.put("windIdeaMap", windIdeaMap);
			outputMap.put("fenType",fenTypes.size());
			outputMap.put("fenTypes",fenTypes);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("二、三、四级评审--风控会议纪要测评记分表修改页错误!请联系管理员");
		} finally {
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context, "/risk_audit/risk_audit_UpdateFrameAfter.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
	}		
	/**
	 *除一级外,所有级别评审修改
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updatetowind(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		String prc_hao="";
		String id=context.contextMap.get("prc_node").toString();
		String istype="";
		String istypeId="";
		
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {	
			sqlMapper.startTransaction() ;
			
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
							sqlMapper.update("beforeMakeContract.updateCorpByIsType", context.contextMap) ;
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
				sqlMapper.update("riskAudit.updatetowind", context.contextMap) ;	
//				DataAccessor.execute("riskAudit.updatetowind", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
				//修改风控备注表信息
				context.contextMap.put("memoLevelUrl", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
				sqlMapper.update("riskAuditUpdate.upRiskMemoForSummit", context.contextMap);
//				DataAccessor.execute("riskAuditUpdate.upRiskMemoForSummit", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			} else {
				//修改风控备注表信息
				context.contextMap.put("memoLevelUrl", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
				//sqlMapper.update("riskAuditUpdate.upRiskMemoForResult", context.contextMap);
//				DataAccessor.execute("riskAuditUpdate.upRiskMemoForResult", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				if(context.contextMap.get("memo").equals("1")){
					sqlMapper.update("riskAuditUpdate.upRiskMemoForResult_forpass", context.contextMap);
				}else{
					sqlMapper.update("riskAuditUpdate.upRiskMemoForResult", context.contextMap);
				}
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
					
					//Modify by Michael 2012 02/08 如果合同号存在， 则不再重新产生合同号-------------------------------
//					String le_code=CodeRule.generateRentContractCode(context.contextMap.get("credit_id"));
//					context.contextMap.put("le_code", le_code);
					//结案修改资信表中的合同号
					//取得合同号 如果存在则不创建新的
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
					//----------------------------------------------------------------------------------------
					
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
			
			//加载日志
			long cre_id=Integer.parseInt((String) context.contextMap.get("credit_id"));
			long em_id=Integer.parseInt( context.contextMap.get("s_employeeId")+"");
			prc_hao=context.contextMap.get("prc_hao").toString(); 
			BusinessLog.addBusinessLog(cre_id, null, id+"级评审", "评审", prc_hao, "", 1, em_id, null,(String)context.contextMap.get("IP"));			
		} catch (Exception e) {
			errList.add(id+"级评审修改失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally {
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,"defaultDispatcher?__action=riskAudit.riskAuditAfter&prc_node="+id);
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
}
