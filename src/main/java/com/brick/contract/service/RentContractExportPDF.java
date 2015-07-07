package com.brick.contract.service;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.util.LeaseUtil;

public class RentContractExportPDF {

	//回租 和 新品回租(发票告知函)
	public static Map exportCheckNotice(String creditId) throws Exception{
		Map result = new HashMap();
		String leaseCode = LeaseUtil.getLeaseCodeByCreditId(creditId);
		Double totalMoney  = LeaseUtil.getTotalPriceByCreditId(creditId);
		result.put("leaseCode", leaseCode);
		result.put("totalMoney", totalMoney);
		return result;
	}
}
