package com.brick.product.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.credit.service.CreditVouchManage;
import com.brick.log.service.LogPrint;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.StringUtils;

/**
 * @author HF
 * 
 * 2011 9:43:15 AM
 */
public class SupplierContributeService extends AService {
	Log logger = LogFactory.getLog(SupplierContributeService.class);

	@SuppressWarnings("unchecked")
	public void findAllSupplierContribute(Context context) {
		long time1 = new Date().getTime();
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		try {//SUMLEASE_RZE
			if (StringUtils.isEmpty((String) context.contextMap.get("sortorder"))) {
				context.contextMap.put("sortorder", "SUMLEASE_RZE");
			}
			if (StringUtils.isEmpty((String) context.contextMap.get("ordertype"))) {
				context.contextMap.put("ordertype", "DESC");
			}
			dw = (DataWrap) DataAccessor.query("supplier.queryContribute",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			System.out.println(new Date().getTime() - time1);
			if(dw!=null){
				List rs=(List) dw.getRs();
				Object last_price = null;
				for(int i=0;i<rs.size();i++){
					Map temp=(Map) rs.get(i);
					if(temp.get("GRANT_PRICE")!=null){
						last_price = SelectReportInfo.selectApplyLastPrice(Integer.parseInt(temp.get("ID").toString()));
						temp.put("LAST_PRICE", last_price == null ? 0.0 : last_price) ;
				      }
				}
			}
			System.out.println(new Date().getTime() - time1);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		Output.jspOutput(outputMap, context,
				"/product/supplierContribute/supplierContributeList.jsp");
	}
	
	public void findAllGuarantorContribute(Context context){
		
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("supplier.queryGuarantorContribute",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
			if(dw!=null){
				List rs=(List) dw.getRs();
				for(int i=0;i<rs.size();i++){
					Map temp=(Map) rs.get(i);
					if(temp.get("GRANT_PRICE")!=null){
						temp.put("LAST_PRICE",CreditVouchManage.VOUCHPLANBYLASTPRICE(temp.get("NAME").toString(),temp.get("IDENTYCODE").toString(),Integer.parseInt(temp.get("DIFFREENT").toString()))) ;
				    }
					if(Integer.parseInt(temp.get("DIFFREENT").toString())==1){
						temp.put("NAME", temp.get("NAME"));
						temp.put("CODE", temp.get("IDENTYCODE"));
						//Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchNatuSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);								
						Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchCropSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
						if(LastPrice!=null){
						temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
						}
					}else if(Integer.parseInt(temp.get("DIFFREENT").toString())==2){
						temp.put("NAME", temp.get("NAME"));
						temp.put("CODE", temp.get("IDENTYCODE"));
						Map LastPrice=(Map)DataAccessor.query("beforeMakeContract.selectVouchNatuSumIrrMonthAndLastPrice", temp, DataAccessor.RS_TYPE.MAP);
						if(LastPrice!=null){
						temp.put("SUMLASTPRICE", LastPrice.get("SHENGYUBENJIN"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		outputMap.put("code", context.contextMap.get("code"));
		outputMap.put("ranklow", context.contextMap.get("ranklow"));
		outputMap.put("rankup", context.contextMap.get("rankup"));
		Output.jspOutput(outputMap, context,
				"/product/guarantorContributes/guarantorContributeList.jsp");
		
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
					//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
					HashMap paylinesum = (HashMap) DataAccessor.query("danbaorenManage.findContractNoPayByContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					money.put("paylinesum", paylinesum);
					//实际TR
					double sjTR=0.0;
					if(((HashMap)contractlist.get(i)).get("TR_IRR_RATE")!=null){

						sjTR=Double.parseDouble(((HashMap)contractlist.get(i)).get("TR_IRR_RATE").toString());
					
					}
					String sjTRs=this.updateMoney(sjTR, nfFSNum);
					money.put("sjTR", sjTRs);					
					bigcontractlistmoney.add(money);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		outputMap.put("contractlists", bigcontractlistmoney);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/product/guarantorContributes/contractpact.jsp");
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
	public void querySupplContract(Context context){
		//Map outputMap = new HashMap();
		List errList = context.errList;
		Map outputMap = context.contextMap;
		List<Map> conlist = new ArrayList();
		try {
			conlist = (List<Map>) DataAccessor.query("supplier.querySupplContract",context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("conlist", conlist);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierContribute/suppliercontract.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
}

