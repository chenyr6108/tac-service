package com.brick.applycompanymanage.service;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.credit.service.CreditVouchManage;
import com.brick.log.service.LogPrint;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;


/**
 * 归户管理的操作
 * @author 胡昭卿
 * @date March 24, 2011
 * @version
 */
public class ApplyCompanyManage extends BaseCommand {

	Log logger = LogFactory.getLog(ApplyCompanyManage.class);
	
	
	public void getAllSupplier(Context context) throws Exception{
		
		List list = baseService.baseDAO.queryForList("applyCompanyManage.getAllSupplier");
		Output.jsonArrayOutputForObject(list, context);
	}
	
	public void getAllCustomer(Context context) throws Exception{
		
		List list = baseService.baseDAO.queryForList("custComterManage.getAllCustomer");
		Output.jsonArrayOutputForObject(list, context);
	}
	/**
	 * 管理页面查询,查询所有有合同的供应商，并统计签订合同的数量以及合同的总钱数
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void findAllApplyCompany (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		// 数字格式
		
		//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计
		double lianbao=0d;
		double huigou=0d;
		Map tempSuplTrue=null;
		
		int guihutype=0;

		if(context.contextMap.get("guihutype")!=null){
			guihutype=Integer.parseInt(context.contextMap.get("guihutype").toString());
		}
		
		if(context.contextMap.get("orgcodes")!=null){
			String orgcodes=context.contextMap.get("orgcodes").toString();
			context.contextMap.put("orgcode", context.contextMap.get("orgcodes").toString());
			if(orgcodes.length()>0){
				guihutype=2;
			}
		}
		context.contextMap.put("C", "租金");
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        List<String> creditIds = null;
        Double remainingPrincipal = null;
		if(errList.isEmpty()){		
			try {
				//根据查询类型不同要调用不同的sql,默认是供应商
				if(guihutype==0 && (context.contextMap.get("id")!=null&&!"".equals(context.contextMap.get("id")))){

						Map temp = (Map) baseService.queryForObj("applyCompanyManage.getApplyCompany", context.contextMap);
					if(temp!=null){	
						
						//Modify by Michael 2012 08-06
						//分别根据留购和连保的已用额度进行加总统计
//								Double lienLastPrice =(Double) (SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(temp.get("ID").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(temp.get("ID").toString())));
//								Double repurchLastPrice=(Double) (SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(temp.get("ID").toString()))==null ? 0.0 :SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(temp.get("ID").toString())));
						//已用留购额度加上已用连保额度
						if(temp.get("GRANT_PRICE") != null){
							//temp.put("LAST_PRICE",(lienLastPrice>0?lienLastPrice:0.0)+(repurchLastPrice>0?repurchLastPrice:0.0));
							Object o = SelectReportInfo.selectApplyLastPrice(Integer.parseInt(temp.get("ID").toString()));
							temp.put("LAST_PRICE",o==null ? 0.0 :o);
						}

						Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
						if(LastPrice!=null){	
							temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
						}

						//实际剩余本金
						remainingPrincipal = new Double(0);
						creditIds = LeaseUtil.getCreditIdBySuplId(String.valueOf(temp.get("ID")));
						for (String s : creditIds) {
							remainingPrincipal += LeaseUtil.getRemainingPrincipalByCreditId(s);
						}
						temp.put("remainingPrincipal", remainingPrincipal);
						
//								Object object=null;
//								if (suplTrue==1) {
//									object =SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(temp.get("ID").toString()));
//								}else if(suplTrue==3) {
//									object =SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(temp.get("ID").toString()));
//								}
						//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计--------------
						Map allLastPriceMap=SelectReportInfo.selectApplyAllLastPrice(Integer.parseInt(temp.get("ID").toString()));
				
						
						if (allLastPriceMap!=null){
							lianbao=DataUtil.doubleUtil(allLastPriceMap.get("shouxinjianshaoe_lien"));
							temp.put("LIANBAO", lianbao);
							huigou=DataUtil.doubleUtil(allLastPriceMap.get("shouxinjianshaoe_repurch"));
							temp.put("HUIGOU", huigou);
						}
						//-------------------------------------------------------------------
				
						// 2012/12/20 Yang Yun 增加已核准未拨款 归户 --------------------------
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("suplName", temp.get("NAME"));
						paramMap.put("suplTrue", 1);
						Double lian_bao_outstanding = (Double) baseService.queryForObj("applyCompanyManage.getApprovedOutstanding", paramMap);
						paramMap.put("suplTrue", 3);
						Double hui_gou_outstanding = (Double) baseService.queryForObj("applyCompanyManage.getApprovedOutstanding", paramMap);
						temp.put("lian_bao_outstanding", lian_bao_outstanding);
						temp.put("hui_gou_outstanding", hui_gou_outstanding);
						outputMap.put("result", temp);
					}
					// -----------------------------------------------------------------
				}else if(guihutype==1 && context.contextMap.get("id")!=null){
					Map temp = (Map) baseService.queryForObj("custComterManage.getCustmer", context.contextMap);						
					
					if(temp != null){

							if(temp.get("GRANT_PRICE") != null){
								//temp.put("LAST_PRICE",CustomerCredit.getCustCredit(temp.get("ID"))) ;
								//temp.put("SUMLASTPRICE", Double.parseDouble(temp.get("GRANT_PRICE").toString())-CustomerCredit.getCustCredit(temp.get("ID")));
							}
							temp.put("NEWCUST_ID", temp.get("ID"));
							Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectCustSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
							if(LastPrice!=null){
							temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
							}
							
							//实际剩余本金
							remainingPrincipal = new Double(0);
							creditIds = LeaseUtil.getCreditIdByCustId(String.valueOf(temp.get("ID")));
							for (String s : creditIds) {
								remainingPrincipal += LeaseUtil.getRemainingPrincipalByCreditId(s);
							}
							temp.put("remainingPrincipal", remainingPrincipal);
							
							//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计--------------
							List suplTrueList = (List<Map>) DataAccessor.query("applyCompanyManage.findSuplTrueByCustId", temp, DataAccessor.RS_TYPE.LIST);
							for(int j=0;j <suplTrueList.size();j++){
								tempSuplTrue = (Map) suplTrueList.get(j) ;
								if ("1".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
									lianbao=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
									temp.put("LIANBAO", lianbao);
								}
								if("3".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
									huigou=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
									temp.put("HUIGOU", huigou);
								}
							}
							//-------------------------------------------------------------------							
					}
					outputMap.put("result", temp);
				}else if(guihutype==2){
					dw = baseService.queryForListWithPaging("danbaorenManage.findAllDanbaoren", context.contextMap, "NAME");
					if(dw != null){
						List rs = (List) dw.getResultList() ;
						for(int i=0;i < rs.size();i++){
							Map temp = (Map) rs.get(i) ;
							if(temp.get("GRANT_PRICE") != null){
								temp.put("LAST_PRICE",CreditVouchManage.VOUCHPLANBYLASTPRICE(temp.get("NAME").toString(),temp.get("IDENTYCODE").toString(),Integer.parseInt(temp.get("DIFFREENT").toString()))) ;
								//temp.put("SUMLASTPRICE", Double.parseDouble(temp.get("GRANT_PRICE").toString())-CreditVouchManage.VOUCHPLANBYLASTPRICE(temp.get("NAME").toString(),temp.get("IDENTYCODE").toString(),Integer.parseInt(temp.get("DIFFREENT").toString())));
							}
							if(Integer.parseInt(temp.get("DIFFREENT").toString())==1){
								temp.put("NAME", temp.get("NAME"));
								temp.put("CODE", temp.get("IDENTYCODE"));
								//Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchNatuSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);								
								Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchCropSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
								if(LastPrice!=null){
								temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
								}
								
								//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计--------------
								List suplTrueList = (List<Map>) DataAccessor.query("applyCompanyManage.findSuplTrueByVouchCropId", temp, DataAccessor.RS_TYPE.LIST);
								for(int j=0;j <suplTrueList.size();j++){
									tempSuplTrue = (Map) suplTrueList.get(j) ;
									if ("1".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
										lianbao=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
										temp.put("LIANBAO", lianbao);
									}
									if("3".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
										huigou=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
										temp.put("HUIGOU", huigou);
									}
								}
								//-------------------------------------------------------------------	
							}else if(Integer.parseInt(temp.get("DIFFREENT").toString())==2){
								temp.put("NAME", temp.get("NAME"));
								temp.put("CODE", temp.get("IDENTYCODE"));
								Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchNatuSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
								if(LastPrice!=null){
								temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
								}
								
								//Add by Michael 2012 02/07 供应商增加 连保、回购 金额统计--------------
								List suplTrueList = (List<Map>) DataAccessor.query("applyCompanyManage.findSuplTrueByVouchNatuId", temp, DataAccessor.RS_TYPE.LIST);
								for(int j=0;j <suplTrueList.size();j++){
									tempSuplTrue = (Map) suplTrueList.get(j) ;
									if ("1".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
										lianbao=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
										temp.put("LIANBAO", lianbao);
									}
									if("3".equals(String.valueOf(tempSuplTrue.get("SUPL_TRUE")))){
										huigou=DataUtil.doubleUtil(tempSuplTrue.get("NET_FINANCE"));
										temp.put("HUIGOU", huigou);
									}
								}
								//-------------------------------------------------------------------	
							}
							
							//getCreditIdByGuarantor
							//实际剩余本金
							remainingPrincipal = new Double(0);
							creditIds = LeaseUtil.getCreditIdByGuarantor(String.valueOf(temp.get("NAME")));
							for (String s : creditIds) {
								remainingPrincipal += LeaseUtil.getRemainingPrincipalByCreditId(s);
							}
							temp.put("remainingPrincipal", remainingPrincipal);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("归户管理--管理页面错误!请联系管理员");
			}
			//结果集放入容器
			outputMap.put("dw", dw);
			//查询项的保存
			outputMap.put("guihutype", guihutype);
			outputMap.put("orgcodes", context.contextMap.get("orgcodes"));
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
		}
		if(errList.isEmpty()){
			if(guihutype==0){
				Output.jspOutput(outputMap, context, "/applycompanymanage/findAllApplyCompany.jsp");
				//Output.jspOutput(outputMap, context, "/applycompanymanage/guihuFrame.jsp");
			}else if(guihutype==1){
				Output.jspOutput(outputMap, context, "/applycompanymanage/findAllCustomer.jsp");
				//Output.jspOutput(outputMap, context, "/applycompanymanage/guihuFrame.jsp");
			}else if(guihutype==2){
				Output.jspOutput(outputMap, context, "/applycompanymanage/findAllDanBaoRen.jsp");
				//Output.jspOutput(outputMap, context, "/applycompanymanage/guihuFrame.jsp");
			}
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	
	
	public void getCreditLineForSupplier(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(context.contextMap.get("id"))) {
			context.contextMap.put("id", "0");
		}
		Map<String, Object> supl = (Map<String, Object>) baseService.queryForObj("danbaorenManage.getSupl", context.contextMap);
		if (supl != null) {
			supl.put("projectCount", LeaseUtil.getValidProjectBySuplId(String.valueOf(supl.get("SUPLID"))));
			outputMap.put("creditLine", baseService.getSuplCreditLine(String.valueOf(supl.get("SUPLID"))));
		}
		outputMap.put("id", context.contextMap.get("id"));
		outputMap.put("supl", supl);
		Output.jspOutput(outputMap, context, "/applycompanymanage/creditLineForSupplier.jsp");
	}
	
	public void getDetailForSupplier(Context context) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> supl = new HashMap<String, Object>();
		supl.put("SUPLID", context.contextMap.get("supplierid"));
		List<Map<String, Object>> projectList = (List<Map<String, Object>>) baseService.queryForList("danbaorenManage.getAllProjectBySuplId", supl);
		if (projectList == null) {
			projectList = new ArrayList<Map<String,Object>>();
		}
		for (Map<String, Object> project : projectList) {
			project.put("remainingPrincipal", LeaseUtil.getRemainingPrincipalByCreditId(String.valueOf(project.get("CREDIT_ID"))));
			project.put("allPrincipal", LeaseUtil.getTotalPrincipalByCreditId(String.valueOf(project.get("CREDIT_ID"))));
			project.put("remainingRental", LeaseUtil.getRemainingRental(String.valueOf(project.get("CREDIT_ID"))));
			project.put("remainingPeriodNum", LeaseUtil.getRemainingPeriodNum(String.valueOf(project.get("CREDIT_ID"))));
		}
		outputMap.put("projectList", projectList);
		Output.jspOutput(outputMap, context, "/applycompanymanage/detailForSupplier.jsp");
	}
	
	
	/**
	 * 根据供应商的id查询对应的所有的合同
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void findContractInfoBySupplierId (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
		List bigcontractlistmoney = new ArrayList();
		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        
		if(errList.isEmpty()){		
			try {
				//找到所有的合同的信息，包括合同的id
				contractlist = (List) baseService.queryForList("applyCompanyManage.findContractInfoBySupplierId", context.contextMap);
				//对合同进行一个遍历，找到每个合同对应的所有的支付表
				for(int i=0;i<contractlist.size();i++){
					HashMap money = new HashMap();
					money.put("contractlist", (HashMap)contractlist.get(i));
					context.contextMap.put("C", "租金");
					context.contextMap.put("RECT_ID", ((HashMap)contractlist.get(i)).get("RECT_ID"));
					context.contextMap.put("RECP_ID", ((HashMap)contractlist.get(i)).get("RECP_ID"));
					Map paylinesum =null;

					if (null!=((HashMap)contractlist.get(i)).get("RECP_ID") && ! (0==DataUtil.intUtil((((HashMap)contractlist.get(i)).get("RECP_ID"))))){
						//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
						paylinesum = (Map) baseService.queryForObj("applyCompanyManage.findContractNoPayByContractId", context.contextMap);
						if(paylinesum==null){
							paylinesum=new HashMap();
						}
						HashMap contractShengyuBenjin = (HashMap) baseService.queryForObj("applyCompanyManage.findShengyuBenjinContractId", context.contextMap);
						paylinesum.put("LASTPRICE", contractShengyuBenjin.get("SHENGYUBENJIN"));
					}else{
						paylinesum=new HashMap();
						paylinesum.put("LASTPRICE", ((HashMap)contractlist.get(i)).get("LEASE_RZE"));
						paylinesum.put("SHENGYUZUJIN", ((HashMap)contractlist.get(i)).get("PLEDGE_AVE_PRICE"));
						paylinesum.put("SHIJISHENGYUZUJIN", ((HashMap)contractlist.get(i)).get("SUMMONTH_PRICE"));
						paylinesum.put("WEIJIAOQISHU", ((HashMap)contractlist.get(i)).get("LEASE_PERIOD"));
						paylinesum.put("ZONGQISHU", ((HashMap)contractlist.get(i)).get("LEASE_PERIOD"));
						
					}
					money.put("paylinesum", paylinesum);
					
					//查询出合同状态
					context.contextMap.put("DICTYPE", "供应商保证");
					context.contextMap.put("SUPL_TRUE", null==((HashMap)contractlist.get(i)).get("SUPL_TRUE")?"4":((HashMap)contractlist.get(i)).get("SUPL_TRUE"));
					Map paylineState=(Map) baseService.queryForObj("applyCompanyManage.findDicTypeBySuplTrue", context.contextMap);
					
					money.put("paylineState", paylineState);
					
					//实际TR
					double sjTR=0.0;
					if(((HashMap)contractlist.get(i)).get("TR_IRR_RATE")!=null){

						sjTR=Double.parseDouble(((HashMap)contractlist.get(i)).get("TR_IRR_RATE").toString());
					}
					String sjTRs=this.updateMoney(sjTR, nfFSNum);
					money.put("sjTR", sjTRs);		
					
					//实际剩余本金
					money.put("remainingPrincipal", LeaseUtil.getRemainingPrincipalByRecpId(String.valueOf(context.contextMap.get("RECP_ID"))));
					
					bigcontractlistmoney.add(money);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("归户管理--查询供应商合同错误!请联系管理员");
			}
		}
		outputMap.put("contractlists", bigcontractlistmoney);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/applycompanymanage/contractpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 根据客户的id查询对应的所有的合同
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void findContractInfoByCustomerId (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
		List bigcontractlistmoney = new ArrayList();
		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        
		if(errList.isEmpty()){		
			try {
				//找到所有的合同的信息，包括合同的id
				contractlist = (List) DataAccessor.query("custComterManage.findContractInfoByCustomerId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//对合同进行一个遍历，找到每个合同对应的所有的支付表
				for(int i=0;i<contractlist.size();i++){
					HashMap money = new HashMap();
					money.put("contractlist", (HashMap)contractlist.get(i));
					
					context.contextMap.put("RECT_ID", ((HashMap)contractlist.get(i)).get("RECT_ID"));
					context.contextMap.put("RECP_ID", ((HashMap)contractlist.get(i)).get("RECP_ID"));
					context.contextMap.put("C", "租金");
					//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
					HashMap paylinesum = (HashMap) DataAccessor.query("applyCompanyManage.findContractNoPayByContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if(paylinesum==null){
						paylinesum=new HashMap();
					}
					//根据合同找到对应的实际剩余本金
					Map contractShenyuBenjin=(Map) DataAccessor.query("applyCompanyManage.findContractNoPayByContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					HashMap contractShengyuBenjin = (HashMap) DataAccessor.query("applyCompanyManage.findShengyuBenjinContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					paylinesum.put("LASTPRICE", contractShengyuBenjin.get("SHENGYUBENJIN"));
					money.put("paylinesum", paylinesum);
					
					//查询出合同状态
					context.contextMap.put("DICTYPE", "供应商保证");
					HashMap paylineState=(HashMap)DataAccessor.query("applyCompanyManage.findDicTypeContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					money.put("paylineState", paylineState);
					
					//实际TR
					double sjTR=0.0;
					if(((HashMap)contractlist.get(i)).get("TR_IRR_RATE")!=null){

						sjTR=Double.parseDouble(((HashMap)contractlist.get(i)).get("TR_IRR_RATE").toString());
					
					}
					String sjTRs=this.updateMoney(sjTR, nfFSNum);
					money.put("sjTR", sjTRs);		
					
					//实际剩余本金
					money.put("remainingPrincipal", LeaseUtil.getRemainingPrincipalByRecpId(String.valueOf(context.contextMap.get("RECP_ID"))));
					
					bigcontractlistmoney.add(money);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("归户管理--查询客户合同错误!请联系管理员");
			}
		}
		outputMap.put("contractlists", bigcontractlistmoney);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/applycompanymanage/contractpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 根据担保人的id查询对应的所有的合同
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void findContractInfoByDanbaorenId (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
		List bigcontractlistmoney = new ArrayList();
		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        
		if(errList.isEmpty()){		
			try {
				//找到所有的合同的信息，包括合同的id
				if(context.contextMap.get("type").toString().equals("1")){
					contractlist = (List) DataAccessor.query("danbaorenManage.findContractInfoByDanbaorenId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				}else if(context.contextMap.get("type").toString().equals("2")){
					contractlist = (List) DataAccessor.query("danbaorenManage.findContractInfoByDanbaorenNatuId", context.contextMap, DataAccessor.RS_TYPE.LIST);	
				}
					//对合同进行一个遍历，找到每个合同对应的所有的支付表
				for(int i=0;i<contractlist.size();i++){
					HashMap money = new HashMap();
					money.put("contractlist", (HashMap)contractlist.get(i));
					context.contextMap.put("RECT_ID", ((HashMap)contractlist.get(i)).get("RECT_ID"));
					context.contextMap.put("RECP_ID", ((HashMap)contractlist.get(i)).get("RECP_ID"));
					context.contextMap.put("C", "租金");
					//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
					HashMap paylinesum = (HashMap) DataAccessor.query("danbaorenManage.findContractNoPayByContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if(paylinesum==null){
						paylinesum=new HashMap();
					}
					HashMap contractShengyuBenjin = (HashMap) DataAccessor.query("applyCompanyManage.findShengyuBenjinContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					paylinesum.put("LASTPRICE", contractShengyuBenjin.get("SHENGYUBENJIN"));
					money.put("paylinesum", paylinesum);
					
					//查询出合同状态
					context.contextMap.put("DICTYPE", "供应商保证");
					HashMap paylineState=(HashMap)DataAccessor.query("applyCompanyManage.findDicTypeContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					money.put("paylineState", paylineState);
					
					//实际TR
					double sjTR=0.0;
					if(((HashMap)contractlist.get(i)).get("TR_IRR_RATE")!=null){

						sjTR=Double.parseDouble(((HashMap)contractlist.get(i)).get("TR_IRR_RATE").toString());
					
					}
					String sjTRs=this.updateMoney(sjTR, nfFSNum);
					money.put("sjTR", sjTRs);			
					
					//实际剩余本金
					money.put("remainingPrincipal", LeaseUtil.getRemainingPrincipalByRecpId(String.valueOf(context.contextMap.get("RECP_ID"))));
					
					bigcontractlistmoney.add(money);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("归户管理--查询担保人合同错误!请联系管理员");
			}
		}
		outputMap.put("contractlists", bigcontractlistmoney);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/applycompanymanage/contractpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/** ￥0.00 */
    private String updateMoney(double money, NumberFormat nfFSNum) {
    	String str = "";
	    str +=  nfFSNum.format(money);
	    return str;
    }

}
