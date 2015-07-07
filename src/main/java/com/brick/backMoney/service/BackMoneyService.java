package com.brick.backMoney.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.to.DataDictionaryTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.support.shopping.service.ShoppingService;
import com.brick.support.shopping.to.PayOrderTO;
import com.brick.support.shopping.to.ShoppingCartTO;

public class BackMoneyService extends BaseService {

	@Transactional
	public void doPayHandlingCharge(ShoppingService shoppingService, ShoppingCartTO cart) {
		//生成订单
		String poId = shoppingService.createPayOrder(cart);
		
		List<DataDictionaryTo> dataList = getDataDictionaryByType("乘用车手续费");
		String pay_way = "";
		String bank_name = "";
		String bank_account = "";
		String back_comp = "";
		for (DataDictionaryTo data : dataList) {
			if ("支付方式".equals(data.getFlag())) {
				pay_way = data.getCode();
			}
			if ("开户行".equals(data.getFlag())) {
				bank_name = data.getCode();
			}
			if ("账号".equals(data.getFlag())) {
				bank_account = data.getCode();
			}
			if ("拨款对象".equals(data.getFlag())) {
				back_comp = data.getCode();
			}
		}
		Map<String, Object> payDetail = new HashMap<String, Object>();
		payDetail.put("pay_way", pay_way);
		payDetail.put("bank_name", bank_name);
		payDetail.put("bank_account", bank_account);
		payDetail.put("back_comp", back_comp);
		payDetail.put("pay_money", cart.getItems_money());
		payDetail.put("create_id", cart.getUser_id());
		payDetail.put("back_state", 9);
		Integer payId = (Integer) insert("rentContract.insertPayDetailForHandlingCharge", payDetail);
		PayOrderTO po = new PayOrderTO();
		po.setId(poId);
		po.setOrder_link(String.valueOf(payId));
		shoppingService.updatePayOrderLink(po);
		shoppingService.cleanShoppingCart(cart);
	}
	
	@Transactional
	public void doPayBrokerage(ShoppingService shoppingService, ShoppingCartTO cart) {
		//生成订单
		String poId = shoppingService.createPayOrder(cart);
		
		List<DataDictionaryTo> dataList = getDataDictionaryByType("乘用车佣金");
		String pay_way = "";
		String bank_name = "";
		String bank_account = "";
		String back_comp = "";
		for (DataDictionaryTo data : dataList) {
			if ("支付方式".equals(data.getFlag())) {
				pay_way = data.getCode();
			}
			if ("开户行".equals(data.getFlag())) {
				bank_name = data.getCode();
			}
			if ("账号".equals(data.getFlag())) {
				bank_account = data.getCode();
			}
			if ("拨款对象".equals(data.getFlag())) {
				back_comp = data.getCode();
			}
		}
		Map<String, Object> payDetail = new HashMap<String, Object>();
		payDetail.put("pay_way", pay_way);
		payDetail.put("bank_name", bank_name);
		payDetail.put("bank_account", bank_account);
		payDetail.put("back_comp", back_comp);
		payDetail.put("pay_money", cart.getItems_money());
		payDetail.put("create_id", cart.getUser_id());
		payDetail.put("back_state", 10);
		Integer payId = (Integer) insert("rentContract.insertPayDetailForHandlingCharge", payDetail);
		PayOrderTO po = new PayOrderTO();
		po.setId(poId);
		po.setOrder_link(String.valueOf(payId));
		shoppingService.updatePayOrderLink(po);
		shoppingService.cleanShoppingCart(cart);
	}
	
	public static List<Map<String, Object>> queryHandlingChargeDetail(String po_id){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("po_id", po_id);
		List<Map<String, Object>> result = null;
		try {
			result = (List<Map<String, Object>>) DataAccessor.getSession().queryForList("rentContract.getHandlingChargeDetail", paraMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Map<String, Object>> getBrokerageDetail(String po_id){
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("po_id", po_id);
		List<Map<String, Object>> result = null;
		try {
			result = (List<Map<String, Object>>) DataAccessor.getSession().queryForList("rentContract.getBrokerageDetailForExport", paraMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
