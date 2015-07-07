package com.brick.car.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.contract.util.SimpleMoneyFormat;
import com.brick.service.core.DataAccessor;


public class CarBackLeasService extends BaseService{
	
	public static Map buildLeaseContract(String creditId) throws Exception{
		Map<String,String> paramMap=new HashMap<String,String>();
		paramMap.put("credit_id",  creditId);

    	Map contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", paramMap, DataAccessor.RS_TYPE.MAP);
    
	    if(contract == null){	
	    	contract =new HashMap();   
			contract.put("LEASE_CODE", "  ____ ");
			contract.put("CORP_NAME_CN", " ____  ");
			contract.put("LEGAL_PERSON", " ____  ");
			contract.put("REGISTERED_OFFICE_ADDRESS", " ");
			contract.put("COMMON_OFFICE_ADDRESS", " ");
			contract.put("POSTCODE", " ");
			contract.put("TELEPHONE", " ");
			contract.put("FAX", " ");		
    	}else{
    		if(contract.get("REGISTERED_OFFICE_ADDRESS")==null || "".equals(contract.get("REGISTERED_OFFICE_ADDRESS"))){
    			contract.put("REGISTERED_OFFICE_ADDRESS", contract.get("NATU_IDCARD_ADDRESS"));
    		}
    		if(contract.get("COMMON_OFFICE_ADDRESS")==null || "".equals(contract.get("COMMON_OFFICE_ADDRESS"))){
    			contract.put("COMMON_OFFICE_ADDRESS", contract.get("NATU_HOME_ADDRESS"));
    		}
    		if(contract.get("TELEPHONE")==null || "".equals(contract.get("TELEPHONE"))){
    			contract.put("TELEPHONE", contract.get("NATU_MOBILE"));
    		}
    	}

    	String custName = LeaseUtil.getCustNameByCreditId(creditId);
    	contract.put("cust_name", custName);
	    List v1 =  (List) DataAccessor.query("creditVoucher.selectAND", paramMap, DataAccessor.RS_TYPE.LIST);
	    List v2=  (List) DataAccessor.query("creditVoucher.selectVND", paramMap, DataAccessor.RS_TYPE.LIST); 
	    List vouchers = new ArrayList();
	    if(v1!=null && v1.size()>0){
		    for(int i=0;i<v1.size();i++){
		    	Map m = (Map) v1.get(i);
		    	Map voucher = new HashMap();
		    	voucher.put("name", m.get("CUST_NAME"));
		    	voucher.put("idcard", m.get("NATU_IDCARD"));
		    	voucher.put("address", m.get("NATU_IDCARD_ADDRESS"));
		    	vouchers.add(voucher);
		    }
	    }
	    if(v2!=null && v2.size()>0){
		    for(int i=0;i<v2.size();i++){
		    	Map m = (Map) v2.get(i);
		    	Map voucher = new HashMap();
		    	voucher.put("name", m.get("LEGAL_PERSON"));
		    	voucher.put("idcard", m.get("LEGAL_ID_CARD"));
		    	voucher.put("address", m.get("LEGAL_HOME_ADDRESS"));
		    	vouchers.add(voucher);
		    }
	    }
	    if(vouchers.size()<5){//不满5个补足5个
		    for(int i=0,len=(5-vouchers.size());i<len;i++){
		    	Map voucher = new HashMap();
		    	voucher.put("name", "");
		    	voucher.put("idcard", "");
		    	voucher.put("address", "");
		    	vouchers.add(voucher);
		    }
	    }
	    contract.put("vouchers", vouchers);
    	return contract;
	}
	
	public static Map buildBuyAndSaleContract(String creditId) throws Exception{
		List leaseholds = new ArrayList();
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("credit_id",creditId);
		List contractinfo = (List) DataAccessor.query(
				"exportContractPdf.judgeExitContract",
				paramMap, DataAccessor.RS_TYPE.LIST);
		if (contractinfo.size() == 0) {
			leaseholds = (List) DataAccessor
					.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
							paramMap,
							DataAccessor.RS_TYPE.LIST);
		} else {
			leaseholds = (List) DataAccessor
					.query("exportContractPdf.queryEquipmentByRectIdForleaseholds",
							paramMap,
							DataAccessor.RS_TYPE.LIST);
		}
		paramMap.put("leaseholds", leaseholds);
		String custName = LeaseUtil.getCustNameByCreditId(creditId);
		String leaseCode = LeaseUtil.getLeaseCodeByCreditId(creditId);
		paramMap.put("cust_name", custName);
		paramMap.put("lease_code", leaseCode);
		
		HashMap baseinfo = (HashMap) leaseholds.get(0);
		String leasetopric="";
		if (baseinfo.get("LEASE_TOPRIC") == null) {
			leasetopric = "  ";
		} else {
			leasetopric = baseinfo.get("LEASE_TOPRIC").toString();
		}
		String eAddress = "";
		if (baseinfo.get("EQUPMENT_ADDRESS") == null) {
			eAddress = "  ";
		} else {
			eAddress = baseinfo.get("EQUPMENT_ADDRESS").toString();
		}
		
		paramMap.put("lowerMoney", updateMoney(new Double(leasetopric)));
		paramMap.put("upperMoney", SimpleMoneyFormat.getInstance().format(new Double(leasetopric)));
		paramMap.put("address", eAddress);
		double payMoney = LeaseUtil.getPayMoneyByCreditId(creditId);
		paramMap.put("lowerPayMoney", updateMoney(new Double(payMoney)));
		paramMap.put("upperPayMoney", SimpleMoneyFormat.getInstance().format(new Double(payMoney)));
		
		
		String cust_id  = LeaseUtil.getCustIdByCreditId(creditId);

		String idCard = LeaseUtil.getNatuIdCardByCustId(cust_id);
		paramMap.put("idCard", idCard);	
		
		
		int period = LeaseUtil.getPeriodsByCreditId(creditId);
		paramMap.put("period", period);	
		Map contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", paramMap, DataAccessor.RS_TYPE.MAP);
		if(contract!=null){
			if(contract.get("REGISTERED_OFFICE_ADDRESS")==null || "".equals(contract.get("REGISTERED_OFFICE_ADDRESS"))){
				paramMap.put("address2", contract.get("NATU_IDCARD_ADDRESS"));
			}else{
				paramMap.put("address2", contract.get("REGISTERED_OFFICE_ADDRESS"));
			}
		}

		
		return paramMap;
	}
	
	/** ￥0.00 */
	private static String updateMoney(Double dNum) {

		NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
		String str="";
		if (dNum == 0d) {
			str+="0.00";
			return str;
		} else {
			str+=nfFSNum.format(dNum);
			return str;
		}

	}
	
	public static Map getCustInfo(String creditId) throws SQLException{
		
		Map<String,Object> result=new HashMap<String,Object>();
		String cust_id  = LeaseUtil.getCustIdByCreditId(creditId);
		String idCard = LeaseUtil.getNatuIdCardByCustId(cust_id);
		result.put("idCard", idCard);	
		String custName = LeaseUtil.getCustNameByCreditId(creditId);
		result.put("custName", custName);
		Integer cardType  = LeaseUtil.getNatuIdCardTypeByCustName(custName);
		if(cardType!=null){
			if(cardType==1){
				result.put("cardType", "身份证");
			}else if(cardType==2){
				result.put("cardType", "港澳台通行证");
			}else if(cardType==3){
				result.put("cardType", "护照");
			}else if(cardType==4){
				result.put("cardType", "其他");
			}			
		}
		return result;
	}
}
