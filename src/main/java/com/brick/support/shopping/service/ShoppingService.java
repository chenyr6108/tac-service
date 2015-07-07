package com.brick.support.shopping.service;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.support.shopping.to.PayOrderListTO;
import com.brick.support.shopping.to.PayOrderTO;
import com.brick.support.shopping.to.ShoppingCartTO;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class ShoppingService extends BaseService {
	
	public ShoppingCartTO getShoppingCartByUserId(String user_id, int item_type) {
		ShoppingCartTO cart = new ShoppingCartTO();
		cart.setUser_id(user_id);
		cart.setItem_type(item_type);
		cart = (ShoppingCartTO) queryForObj("shopping.getShoppingCartByUserId", cart);
		if (cart == null) {
			cart = new ShoppingCartTO();
		}
		return cart;
	}
	
	public void updateItemsForCart(ShoppingCartTO cart){
		if (StringUtils.isEmpty(cart.getUser_id())) {
			throw new ServiceException("获取登录信息失败。");
		}
		ShoppingCartTO oldCart = getShoppingCartByUserId(String.valueOf(cart.getUser_id()), cart.getItem_type());
		if (StringUtils.isEmpty(oldCart.getUser_id())) {
			insert("shopping.insertCart", cart);
		} else {
			update("shopping.updateCart", cart);
		}
		
	}

	@Transactional
	public String createPayOrder(ShoppingCartTO cart) {
		//新建订单
		String poId = getPayOrder(cart);
		cart.setId(poId);
		//添加订单明细
		insertPayOrderList(cart);
		return poId;
	}
	
	@Transactional
	public String getPayOrder(ShoppingCartTO cart){
		try {
			String order_code = DateUtil.getCurrentYear() + DateUtil.getCurrentMonth() + DateUtil.getCurrentDay();
			PayOrderTO po = new PayOrderTO();
			po.setOrder_code(order_code);
			order_code = (String) queryForObj("shopping.getPayOrderCodeByCode", po);
			if (StringUtils.isEmpty(order_code)) {
				order_code = po.getOrder_code() + "01";
			} else {
				order_code = order_code.substring(order_code.length() - 2, order_code.length());
				int code = Integer.valueOf(order_code);
				code ++;
				order_code = String.valueOf(code);
				order_code = order_code.length() == 1 ? ("0" + order_code) : order_code;
				order_code = po.getOrder_code() + order_code;
			}
			po.setOrder_code(order_code);
			po.setCreate_by(cart.getUser_id());
			po.setOrder_money(cart.getItems_money());
			po.setStatus(0);
			po.setOrder_type(cart.getOrder_type());
			String poId = (String) insert("shopping.insertPayOrder", po);
			return poId;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Transactional
	public void insertPayOrderList(ShoppingCartTO cart){
		PayOrderListTO pol = null;
		for (String item : cart.getItems().split(",")) {
			pol = new PayOrderListTO();
			pol.setOrder_id(cart.getId());
			pol.setItem_id(item);
			pol.setItem_type(cart.getItem_type());
			pol.setCreate_by(cart.getUser_id());
			pol.setStatus(0);
			insert("shopping.insertPayOrderList", pol);
		}
	}

	public void updatePayOrderLink(PayOrderTO po) {
		update("shopping.updatePayOrderLink", po);
	}

	public void cleanShoppingCart(ShoppingCartTO cart) {
		cart.setItems("");
		updateItemsForCart(cart);
	}
	
}
