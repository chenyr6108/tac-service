package com.brick.risk_audit;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.to.GuiHuInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.collection.service.StartPayService;
import com.brick.credit.service.CreditVouchManage;
import com.brick.credit.util.VouchClassUtil;
import com.brick.customer.service.CustomerCredit;
import com.brick.customer.service.CustomerCredit.GUIHU_TYPE;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;



/**
 * 
 * @author 胡昭卿
 * @date 0331
 */
public class SelectReportInfo extends AService{
	static Log logger = LogFactory.getLog(SelectReportInfo.class);

	
	/**
	 *生成合同的时候需要取到的数据
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static void selectReportInfo_zulin(Context context,Map outputMap) {
	
		Map creditMap = null;
		Map schemeMap = null;
		Map pSIdeaMap = null;
		Map pSIdeaMapOther = null;
		Map equipmentsMap = null;
		Map insuresMap = null;
		Map otherMap = null;
		List fenList = null;		
		List insuresList = null;
		List explainlist = null;
		List equipmentsList = null;
		List otherPriceList = null;
		Map levelMap = new HashMap();
		List payWayList = null;
		Map manageMap=new HashMap();
		Map manageMapDGM = new HashMap();
		Map before=new HashMap();
		Map manageMapCrop=new HashMap();
		List contractType = null;
		Map reportBoKuanMap=null;
		//
		List supperGrantMap=null;
		try {
			//等级配置
			if(context.contextMap.get("prc_node")!=null){
				context.contextMap.put("rank", Integer.parseInt(context.contextMap.get("prc_node").toString())+1);
			}else{
				context.contextMap.put("rank", 1);
			}
			levelMap = (Map) DataAccessor.query("riskAudit.selectLevelMap",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			//评审部意见
			pSIdeaMap= (Map) DataAccessor.query("riskAudit.selectPSIdea",context.contextMap, DataAccessor.RS_TYPE.MAP);
			pSIdeaMapOther = (Map) DataAccessor.query("riskAudit.selectPSIdeaOther",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//业务主管审核意见
			List<Map<String, Object>> memoList = (List<Map<String, Object>>) DataAccessor.query("creditReportManage.selectNewMemoFor2",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if (memoList != null && memoList.size() == 1) {
				manageMap = memoList.get(0);
			} else if (memoList != null && memoList.size() == 2) {
				if (memoList.get(0).get("AUDIT_STATE") != null && (Integer)memoList.get(0).get("AUDIT_STATE") == 1) {
					manageMapDGM = memoList.get(0);
					manageMap = memoList.get(1);
				} else {
					manageMap = memoList.get(0);
				}
			} 
			//manageMap= (Map) DataAccessor.query("creditReportManage.selectNewMemo",context.contextMap, DataAccessor.RS_TYPE.MAP);
			manageMapCrop= (Map) DataAccessor.query("creditReportManage.selectMemoByPrcId",context.contextMap, DataAccessor.RS_TYPE.MAP);			
			//基本信息
			creditMap = (Map) DataAccessor.query("riskAudit.selectCreditBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(DataUtil.intUtil(creditMap.get("CUST_TYPE"))==1	&&   creditMap.get("CORP_NAME_CN")==null){
				creditMap = (Map) DataAccessor.query("riskAudit.getCustomerBycredit_id",context.contextMap, DataAccessor.RS_TYPE.MAP); 
			}
			if (creditMap != null && creditMap.get("MODIFY_DATE") != null) {
				creditMap.put("MODIFY_DATE", DateUtil.formatDateWithMillisecond((Date) creditMap.get("MODIFY_DATE")));
			}
			schemeMap = (Map) DataAccessor.query("riskAudit.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//如果供应商担保是null,则设置供应商担保为无 add by ShenQi
			if(schemeMap!=null&&schemeMap.get("SUPL_TRUE")==null) {
				schemeMap.put("SUPL_TRUE","4");
			}
			equipmentsList = (List) DataAccessor.query("riskAudit.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			equipmentsMap= (Map) DataAccessor.query("riskAudit.selectCreditEquipmentSum",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			if(equipmentsMap!=null){
				if(equipmentsMap.get("SUMTOTAL")!=null){
					double sheBeiHeX = Double.parseDouble(equipmentsMap.get("SUMTOTAL").toString());
					equipmentsMap.put("SUMTOTAL",Math.round(sheBeiHeX));
				}
			}
			insuresList = (List) DataAccessor.query("riskAudit.selectCreditInsure",context.contextMap, DataAccessor.RS_TYPE.LIST);
			insuresMap= (Map) DataAccessor.query("riskAudit.selectCreditInsureSum",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			otherPriceList = (List) DataAccessor.query("riskAudit.selectCreditOtherPrice",context.contextMap, DataAccessor.RS_TYPE.LIST);
			otherMap= (Map) DataAccessor.query("riskAudit.selectCreditOtherPriceSum",context.contextMap, DataAccessor.RS_TYPE.MAP);	
			explainlist= (List) DataAccessor.query("riskAudit.selectCreditExplain",context.contextMap, DataAccessor.RS_TYPE.LIST);	
			//支付方式
			context.contextMap.put("dictionaryType", "支付方式");
			payWayList = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//公司成立是否达到6个月
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String datetime = tempDate.format(new Date());
			Date begin=null;
			if (creditMap.get("INCORPORATING_DATE")==null) {
				 begin=tempDate.parse(datetime);
			}
			else {
				 begin=tempDate.parse(creditMap.get("INCORPORATING_DATE").toString());
			}		
			Date end = tempDate.parse(datetime);
			long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒
			long day=between/(24*3600)/30;
			if (day<=6) {
				outputMap.put("defuMonth", 0);
			} else {
				outputMap.put("defuMonth", 1);
			}
			
			context.contextMap.put("dataType", "融资租赁合同类型");
			contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionaryAll", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("contractType", contractType); 
			context.contextMap.put("dataType", "拨款方式");
			List bokuanType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("bokuanType", bokuanType); 
			//之前的评审分
			before= (Map) DataAccessor.query("riskAudit.selectBefore",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//新加的信息
			reportBoKuanMap=(HashMap)DataAccessor.query("beforeMakeContract.selectReportBoKuan",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("reportBoKuanMap", reportBoKuanMap);
			Map newbeforemap=(HashMap)DataAccessor.query("beforeMakeContract.selectnewCreditInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(schemeMap!=null){
				if(schemeMap.get("LEASE_RZE")!=null){
					context.contextMap.put("lease_rze", Double.parseDouble(schemeMap.get("LEASE_RZE").toString()));
					context.contextMap.put("cust_id", Integer.parseInt(newbeforemap.get("CUST_ID").toString()));
				}
			}
			if(newbeforemap!=null){
				if(newbeforemap.get("CUST_ID")!=null){
					context.contextMap.put("cust_id", Integer.parseInt(newbeforemap.get("CUST_ID").toString()));
				}
			}
			//根据客户id进行归户查询
			//Map custguihu = (Map)DataAccessor.query("beforeMakeContract.selectCustGuihu",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map ranklevel = (Map)DataAccessor.query("beforeMakeContract.selectRankLevel",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map riskEdu = (Map)DataAccessor.query("beforeMakeContract.selectRankEdu_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map riskEduBenan = (Map)DataAccessor.query("beforeMakeContract.selectRankEduBenan",context.contextMap, DataAccessor.RS_TYPE.MAP);
			Map customeredu = (Map)DataAccessor.query("beforeMakeContract.selectCustomerEdu",context.contextMap, DataAccessor.RS_TYPE.MAP);
			List applydu = (List)DataAccessor.query("beforeMakeContract.selectApplyEdu",context.contextMap, DataAccessor.RS_TYPE.LIST);
			//增加制造商List 只为导出pdf使用
			outputMap.put("manufacturers", DataAccessor.query("beforeMakeContract.selectManufacturers",context.contextMap, DataAccessor.RS_TYPE.LIST));
			//查询担保人
			List danbaorens = (List<Map>)DataAccessor.query("beforeMakeContract.selectDanbaos",context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			//查询本报告是否已经有支付表
			Map creditcontractplan=null;
			List creditcontractplans=(List)DataAccessor.query("beforeMakeContract.selectCreditContract",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(creditcontractplans.size()>0){
				 creditcontractplan=(HashMap)creditcontractplans.get(0);
			}
			//查询本报告是否已经生成合同
			List creditcontracts=(List)DataAccessor.query("beforeMakeContract.selectCreditContracts",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(creditcontracts.size()>0){
				Map creditcontract=(HashMap)creditcontracts.get(0);
				outputMap.put("creditcontract", creditcontract);
			}
			Map creditshemadetail = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//查询本报告是否已经评审无条件通过
			List creditriskcontrolpass = (List)DataAccessor.query("beforeMakeContract.selectCreditRiskControlPass",context.contextMap, DataAccessor.RS_TYPE.LIST);
			double shengqingbokuan=0.0;
			if(creditriskcontrolpass.size()>0){
				Map creditriskcontrolpassMap=(HashMap)creditriskcontrolpass.get(0);
				outputMap.put("creditriskcontrolpassMap", creditriskcontrolpassMap);
				
			}else{
				if(creditshemadetail != null){
					if(creditshemadetail.get("LEASE_TOPRIC") == null){
						creditshemadetail.put("LEASE_TOPRIC",0.0) ;
					} 
					if(creditshemadetail.get("PLEDGE_ENTER_MCTOAG") == null){
						creditshemadetail.put("PLEDGE_ENTER_MCTOAG",0.0) ;
					} 
					if(creditshemadetail.get("PLEDGE_ENTER_AG") == null){
						creditshemadetail.put("PLEDGE_ENTER_AG",0.0) ;
					} 
					shengqingbokuan=Double.parseDouble(creditshemadetail.get("LEASE_TOPRIC").toString())-Double.parseDouble(creditshemadetail.get("PLEDGE_ENTER_MCTOAG").toString())-Double.parseDouble(creditshemadetail.get("PLEDGE_ENTER_AG").toString());
				}
			}
			
			// 管理费用
			List feeListRZE=null;
			feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeListRZE", feeListRZE);
			List feeList=null;
			feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeList", feeList);	
			//费用设定明细 影响概算成本为1 不影响为0
			List feeSetListRZE=null;
			feeSetListRZE = (List) DataAccessor.query("creditReportManage.getFeeSetListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetListRZE", feeSetListRZE);
			List feeSetList=null;
			feeSetList = (List) DataAccessor.query("creditReportManage.getFeeSetList",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("feeSetList", feeSetList);
			
			
			//查询对应客户的所有评审通过的案件的对应的未还款的应付租金的总和和实际剩余本金总和
			context.contextMap.put("NEWCUST_ID", creditMap.get("CUST_ID"));
			Map sumIrrMonthPriceAndLastPrice = (Map)DataAccessor.query("beforeMakeContract.selectCustSumIrrMonthAndLastPrice",context.contextMap, DataAccessor.RS_TYPE.MAP);
			//实际本金总和要根据已还款的支付表详细得出，而剩余租金要根据未还款支付表详细算出，所以分开来算
			Map sumIrrMonthPrice = (Map)DataAccessor.query("beforeMakeContract.selectCustSumIrrMonthAndLastPrice_IrrMonthPirce",context.contextMap, DataAccessor.RS_TYPE.MAP);
			if(sumIrrMonthPrice!=null){
				if(sumIrrMonthPriceAndLastPrice==null){
					sumIrrMonthPriceAndLastPrice=new HashMap();
				}	
					sumIrrMonthPriceAndLastPrice.put("SHENGYUZUJIN", sumIrrMonthPrice.get("SHENGYUZUJIN"));
				
			}
			outputMap.put("sumIrrMonthPriceAndLastPrice", sumIrrMonthPriceAndLastPrice);
			//查询客户的授信额度
			Map custGrantMap_Biaozhun = (Map)DataAccessor.query("beforeMakeContract.selectcustGrantPrice_Biaozhun",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("custGrantMap_Biaozhun", custGrantMap_Biaozhun);
			Map shenQingBoMap = (Map)DataAccessor.query("beforeMakeContract.selectSqboje_THISCASE",context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("shenQingBoMap", shenQingBoMap);
			if(custGrantMap_Biaozhun!=null){
				double custGrantMap= CustomerCredit.getCustCredit(creditMap.get("CUST_ID"));
				double cust_shijibenji=Double.parseDouble(custGrantMap_Biaozhun.get("GRANT_PRICE").toString())-custGrantMap+shengqingbokuan;
				if(cust_shijibenji>0){
					context.contextMap.put("cust_shijibenji", cust_shijibenji);
				}
				Map newranklevel=(Map)DataAccessor.query("beforeMakeContract.selectranklevel_byshijibenjin",context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				if(ranklevel != null && newranklevel != null && ranklevel.get("RANK") != null && newranklevel.get("RANK") != null && Integer.parseInt(ranklevel.get("RANK").toString())<Integer.parseInt(newranklevel.get("RANK").toString())){
					ranklevel.put("RANK", newranklevel.get("RANK"));
				}
				
				outputMap.put("custGrantMap", custGrantMap);
				outputMap.put("cust_shijibenji", cust_shijibenji);
			}
			//查询对应供应商的所有评审通过的案件的对应的未还款的应付租金的总和和实际剩余本金总和
			List applyIds = (List) DataAccessor.query("beforeMakeContract.selectApplyIds",context.contextMap, DataAccessor.RS_TYPE.LIST);
			ArrayList applyEDUandBenjinYue=new ArrayList();
			ArrayList applyWuTiaoJianPass=new ArrayList();
			for(int i=0;i<applyIds.size();i++){
				HashMap applyId = (HashMap)applyIds.get(i);
				applyId.put("credit_id", context.contextMap.get("credit_id"));
				Object applyGrantLastPrice = (Object)selectApplyLastPrice(Integer.parseInt(applyId.get("ID").toString()));
				if(applyGrantLastPrice!=null){
					Map applyBokuanBi = (Map) DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",applyId, DataAccessor.RS_TYPE.MAP);
					if (applyBokuanBi == null) {
						break;
					}
					if(Double.parseDouble(applyBokuanBi.get("SHENGYUBENJINYUEBENAN").toString())>Double.parseDouble(applyGrantLastPrice.toString())){
						//Add by Michael 2012 5-14 只有是供应商连保时才将金额放进去
						if (!schemeMap.get("SUPL_TRUE").toString().equals("4")){
							applyEDUandBenjinYue.add(applyId);
						}
						
						//applyEDUandBenjinYue.add(applyId);
					}
						Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyGrant_PriceById",applyId, DataAccessor.RS_TYPE.MAP);
						if(applyGrantPrice!=null){
							if(applyGrantPrice.get("GRANT_PRICE")!=null){
								double bijiaoshu=0.0;
								if(creditriskcontrolpass.size()>0){
									bijiaoshu=Double.parseDouble(applyGrantPrice.get("GRANT_PRICE").toString())-Double.parseDouble(applyGrantLastPrice.toString());
								}else{
									bijiaoshu=Double.parseDouble(applyBokuanBi.get("SHENGYUBENJINYUEBENAN").toString())+Double.parseDouble(applyGrantPrice.get("GRANT_PRICE").toString())-Double.parseDouble(applyGrantLastPrice.toString());
									
								}
								if(bijiaoshu>0){
									context.contextMap.put("cust_shijibenji", bijiaoshu);
								}
//								Map newranklevel_apply=(Map)DataAccessor.query("beforeMakeContract.selectranklevel_byshijibenjin",context.contextMap, DataAccessor.RS_TYPE.MAP);
//								if(newranklevel_apply!=null&&newranklevel_apply.get("RANK")!=null){
//									if(Integer.parseInt(ranklevel.get("RANK").toString())<Integer.parseInt(newranklevel_apply.get("RANK").toString())){
//										ranklevel.put("RANK", newranklevel_apply.get("RANK"));
//									}
//								}
//								if(levelMap!=null&&levelMap.get("GRANT_PRICE_UPPER")!=null){
//									if(bijiaoshu>Double.parseDouble(levelMap.get("GRANT_PRICE_UPPER").toString())){
//										applyWuTiaoJianPass.add(applyId);
//									}
//								}
							}
					}
				
					
					
				}
				
				
			}
			outputMap.put("applyWuTiaoJianPass", applyWuTiaoJianPass);
			//Michael 2012 5-14
			outputMap.put("applyEDUandBenjinYue", applyEDUandBenjinYue);
			//查询对应供应商的所有评审通过的案件的对应的未还款的应付租金的总和和实际剩余本金总和
			ArrayList vouchEDUandBenjinYue = new ArrayList();
			ArrayList vouchWuTiaoJianPass=new ArrayList();
			List VoucherList= (List) DataAccessor.query("creditReportManage.getAllVouchers", context.contextMap,DataAccessor.RS_TYPE.LIST);
			if(VoucherList.size()>0){
				for(int i=0;i<VoucherList.size();i++){
					HashMap voucherMap=(HashMap)VoucherList.get(i);
					if(voucherMap.get("GRANT_PRICE")!=null){
						double vouch_lastprice=CreditVouchManage.VOUCHPLANBYLASTPRICE(voucherMap.get("VOUCHNAME").toString(),voucherMap.get("VOUCHCODE").toString(),Integer.parseInt(voucherMap.get("TYPE").toString()));
						if(Double.parseDouble(shenQingBoMap.get("SHENQINGBOKUANJINE").toString())>vouch_lastprice){
							
							//Add by Michael 2012 5-14 只有是供应商连保时才将金额放进去
							if (!schemeMap.get("SUPL_TRUE").toString().equals("4")){
								vouchEDUandBenjinYue.add(voucherMap);
							}
							//vouchEDUandBenjinYue.add(voucherMap);
						}
						
						Map vouchGrantPrice=new HashMap();
						if(voucherMap.get("TYPE").toString().equals("1")){
							vouchGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectVouchCrop_GrantPrice",voucherMap, DataAccessor.RS_TYPE.MAP);
						}else if(voucherMap.get("TYPE").toString().equals("0")){
							vouchGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectNatuCrop_GrantPrice",voucherMap, DataAccessor.RS_TYPE.MAP);
						}
						if(vouchGrantPrice!=null){
							if(vouchGrantPrice.get("GRANT_PRICE")!=null){
								double bijiaoshu=0.0;
								if(creditriskcontrolpass.size()>0){
									bijiaoshu=Double.parseDouble(vouchGrantPrice.get("GRANT_PRICE").toString())-vouch_lastprice;
								}else{
									bijiaoshu=shengqingbokuan+Double.parseDouble(vouchGrantPrice.get("GRANT_PRICE").toString())-vouch_lastprice;
									
									//System.out.println(bijiaoshu+"=======");
								}
								if(bijiaoshu>0){
									context.contextMap.put("cust_shijibenji", bijiaoshu);
								}
//								Map newranklevel_vouch=(Map)DataAccessor.query("beforeMakeContract.selectranklevel_byshijibenjin",context.contextMap, DataAccessor.RS_TYPE.MAP);
//								if(newranklevel_vouch!=null&&newranklevel_vouch.get("RANK")!=null){
//									if(Integer.parseInt(ranklevel.get("RANK").toString())<Integer.parseInt(newranklevel_vouch.get("RANK").toString())){
//										ranklevel.put("RANK", newranklevel_vouch.get("RANK"));
//									}
//								}
//								if(levelMap!=null&&levelMap.get("GRANT_PRICE_UPPER")!=null){
//									if(bijiaoshu>Double.parseDouble(levelMap.get("GRANT_PRICE_UPPER").toString())){
//										vouchWuTiaoJianPass.add(voucherMap);
//									}
//								}
							}
						}
						
					}
				}
			}
			outputMap.put("vouchWuTiaoJianPass", vouchWuTiaoJianPass);
			//Michael 2012 5-14
			outputMap.put("vouchEDUandBenjinYue", vouchEDUandBenjinYue);
			//查询出拨款情况
			List appropiateList = (List) DataAccessor.query("creditReportManage.getAppropiateByCreditId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("appropiateList", appropiateList);
			List suplList=(List)DictionaryUtil.getDictionary("供应商保证");
			
			outputMap.put("suplList", suplList);
			// irr month
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			outputMap.put("irrMonthPaylines", irrMonthPaylines);
			
			//含税租金
			Map<String, Object> paylist = new HashMap<String, Object>();
			paylist.put("TOTAL_VALUEADDED_TAX", schemeMap.get("TOTAL_VALUEADDED_TAX"));
			paylist.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
			StartPayService.packagePaylinesForValueAdded(paylist);
			List newIrrMonthPaylines = (List) paylist.get("irrMonthPaylines");
			outputMap.put("newIrrMonthPaylines", newIrrMonthPaylines);
			
			int supper_id = 0;
			List applyGrantLastPriceList = new ArrayList() ;
			for(int i = 0;i< applydu.size() ;i++){
				Map temp = (HashMap) applydu.get(i) ;
				supper_id=Integer.parseInt(temp.get("SUPPLIER_ID").toString());
				Object applyGrantLastPrice = selectApplyLastPrice(supper_id) ;
				applyGrantLastPriceList.add(applyGrantLastPrice);
				temp.put("applyGrantLastPrice", applyGrantLastPrice) ;
			}
			outputMap.put("applyGrantLastPriceList", applyGrantLastPriceList);
			
			supperGrantMap = (List<Map>) DataAccessor.query("creditReportManage.selectSupperGrantInfo",context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(supperGrantMap.size()>0){
				int applyID=Integer.parseInt(((HashMap)supperGrantMap.get(0)).get("ID").toString());
				((HashMap)supperGrantMap.get(0)).put("LAST_PRICE", selectApplyLastPrice(applyID));
			}
			
			context.contextMap.put("dataType", "锁码方式");
			List lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("lockList", lockList);
			//加入客户 归户 租金和本金余额
			outputMap.put("GUIHUOWN",CustomerCredit.getCustGuiHu(context.contextMap.get("cust_id"),GUIHU_TYPE.OWN)) ;
			outputMap.put("GUIHUIRR",CustomerCredit.getCustGuiHu(context.contextMap.get("cust_id"),GUIHU_TYPE.IRR)) ;
			//加入客户 归户 租金和本金余额结束
			outputMap.put("supperGrantMap", supperGrantMap);
			outputMap.put("creditcontractplan", creditcontractplan);
			//outputMap.put("custguihu", custguihu);
			outputMap.put("creditshemadetail", creditshemadetail);
			outputMap.put("rank", ranklevel);
			outputMap.put("customeredu", customeredu);
			outputMap.put("applydu", applydu);
			outputMap.put("danbaorens", danbaorens);
			outputMap.put("newbeforemap", newbeforemap);
			outputMap.put("before", before);		
			outputMap.put("fenList", fenList);
			outputMap.put("pSIdeaMap", pSIdeaMap);
			outputMap.put("pSIdeaMapOther", pSIdeaMapOther);
			outputMap.put("manageMap", manageMap);
			outputMap.put("manageMapDGM", manageMapDGM);
			outputMap.put("manageMapCrop", manageMapCrop);
			outputMap.put("creditMap", creditMap);
			outputMap.put("credit_id", context.contextMap.get("credit_id"));
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("explainlist", explainlist);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("insuresList", insuresList);
			outputMap.put("otherPriceList", otherPriceList);
			outputMap.put("showFlag",0);
			outputMap.put("memoLevel",0);
			outputMap.put("otherHeX", otherMap.get("SUMOTHER_PRICE").toString());
			outputMap.put("insureHeX", insuresMap.get("SUMINSURE_PRICE").toString());
			outputMap.put("sheBeiHeX", equipmentsMap.get("SUMTOTAL").toString());
			outputMap.put("levelMap", levelMap);
			outputMap.put("payWayList", payWayList);
			outputMap.put("riskEdu", riskEdu);
			outputMap.put("riskEduBenan", riskEduBenan);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	public static Object selectApplyLastPrice(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			//Object REAL_LAST_PRICE = (Object)DataAccessor.query("beforeMakeContract.selectApplyRealLastPrice_HeZhun",outputMap, DataAccessor.RS_TYPE.OBJECT);
			//首先搜索对应供应商的授信额度，如果没有返回null;
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			Map contract_onejiashaoe=null;
			if(applyGrantPrice!=null){
			//根据供应商搜索出所有的已核准通过的报告
				double shouxinjianshaoe=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				//Modify by Michael 2012 10-22 连保额度与回购额度切开，且分是否循环授信切开，总额度为连保额度和回购额度之和
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//连保额度
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//Map contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
							//回购额度 
							if("0".equals(String.valueOf(applyGrantPrice.get("REPURCH_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//Map contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							Map credit_onejiashaoe=null;
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				return Double.parseDouble(applyGrantPrice.get("GRANT_PRICE").toString())-shouxinjianshaoe;
			}else{
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

	//Add by Michael 2012 08-03 连保、留购连保方式 是否循环授信进行切割，针对不同的保证方式进行金额的计算
	//判断连保额度
	public static Object selectApplyLienLastPrice(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			//查询连保授信金额及授信是否循环
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyLienGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			if(applyGrantPrice!=null){
				double shouxinjianshaoe=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllLienCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//判断是否是循环授信  1是循环授信0是非循环授信
							Map contract_onejiashaoe=null;
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							Map credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				return Double.parseDouble(applyGrantPrice.get("LIEN_GRANT_PRICE").toString())-shouxinjianshaoe;
			}else{
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

	//Add by Michael 2012 08-03 连保、留购连保方式 是否循环授信进行切割，针对不同的保证方式进行金额的计算
	//判断回购额度
	public static Object selectApplyRepurchLastPrice(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyRepurchGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			if(applyGrantPrice!=null){
			//根据供应商搜索出所有的已核准通过的报告
				double shouxinjianshaoe=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllRepurchCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//判断是否是循环授信  1是循环授信0是非循环授信
							Map contract_onejiashaoe=null;
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							Map credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				return Double.parseDouble(applyGrantPrice.get("REPURCH_GRANT_PRICE").toString())-shouxinjianshaoe;
			}else{
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

	//Add by Michael 2012 08-03 连保、留购连保方式 是否循环授信进行切割，针对不同的保证方式进行金额的计算
	//已用连保额度
	public static Object selectApplyLienJiaoshaoe(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			//查询连保授信金额及授信是否循环
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyLienGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			if(applyGrantPrice!=null){
				double shouxinjianshaoe=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllLienCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//判断是否是循环授信  1是循环授信0是非循环授信
							Map contract_onejiashaoe=null;
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							Map credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				return shouxinjianshaoe;
			}else{
				return null;
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

	//Add by Michael 2012 08-03 连保、留购连保方式 是否循环授信进行切割，针对不同的保证方式进行金额的计算
	//已用回购额度
	public static Object selectApplyRepurchJiaoshaoe(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyRepurchGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			if(applyGrantPrice!=null){
			//根据供应商搜索出所有的已核准通过的报告
				double shouxinjianshaoe=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllRepurchCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//判断是否是循环授信  1是循环授信0是非循环授信
							Map contract_onejiashaoe=null;
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							Map credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				return shouxinjianshaoe;
			}else{
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

	
	public static Map selectApplyAllLastPrice(int applyId) {
		try{
			HashMap outputMap=new HashMap();
			outputMap.put("applyId", applyId);
			//Object REAL_LAST_PRICE = (Object)DataAccessor.query("beforeMakeContract.selectApplyRealLastPrice_HeZhun",outputMap, DataAccessor.RS_TYPE.OBJECT);
			//首先搜索对应供应商的授信额度，如果没有返回null;
			Map applyGrantPrice = (Map) DataAccessor.query("beforeMakeContract.selectApplyGrant_PriceByAPPLYId",outputMap, DataAccessor.RS_TYPE.MAP);
			Map contract_onejiashaoe=null;
			if(applyGrantPrice!=null){
			//根据供应商搜索出所有的已核准通过的报告
				double shouxinjianshaoe_lien=0.0;
				double shouxinjianshaoe_repurch=0.0;
				List ApplyAllCredit_HeZhun = (List)DataAccessor.query("beforeMakeContract.selectApplyAllCredit_HeZhun",outputMap, DataAccessor.RS_TYPE.LIST);
				//通过遍历报告查看报告对应的合同是否已经生成
				//Modify by Michael 2012 10-22 连保额度与回购额度切开，且分是否循环授信切开，总额度为连保额度和回购额度之和
				if(ApplyAllCredit_HeZhun.size()>0){
					for(int i=0;i<ApplyAllCredit_HeZhun.size();i++){
						HashMap credit_single=(HashMap)ApplyAllCredit_HeZhun.get(i);
						if(credit_single.get("RECT_ID")!=null){
							credit_single.put("RECT_ID", credit_single.get("RECT_ID"));
							credit_single.put("ID", applyId);
							//连保额度
							if("0".equals(String.valueOf(applyGrantPrice.get("LIEN_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//Map contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe_lien+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
							//回购额度 
							if("0".equals(String.valueOf(applyGrantPrice.get("REPURCH_REPEAT_CREDIT")))){
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneNoRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}else{
								contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchContractOneRepeat_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);
							}
							//Map contract_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(contract_onejiashaoe!=null){
								if(contract_onejiashaoe.get("SHOUXINJIANSHAOE") == null){
									contract_onejiashaoe.put("SHOUXINJIANSHAOE",0.0) ;
 								}
								shouxinjianshaoe_repurch+=Double.parseDouble(contract_onejiashaoe.get("SHOUXINJIANSHAOE") + "");
							}
						}else{
							Map credit_onejiashaoe=null;
							credit_single.put("credit_id", credit_single.get("CREDIT_ID"));
							credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyLienLastPriceByCreditID",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe_lien+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
							credit_onejiashaoe=(Map)DataAccessor.query("beforeMakeContract.selectApplyRepurchLastPriceByCreditID",credit_single, DataAccessor.RS_TYPE.MAP);						
							if(credit_onejiashaoe!=null){
								shouxinjianshaoe_repurch+=Double.parseDouble(credit_onejiashaoe.get("SHENGYUBENJINYUEBENAN").toString());
							}
						}
					}
				}
				outputMap.put("shouxinjianshaoe_repurch", shouxinjianshaoe_repurch);
				outputMap.put("shouxinjianshaoe_lien", shouxinjianshaoe_lien);
				
				return outputMap;
			}else{
				return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			return null;
		}
	}

}