package com.brick.payer.service;

import java.util.List;
import java.util.Map;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.brick.payer.dao.PayerDAO;
import com.brick.payer.to.PayerTo;


public class PayerService extends BaseService {
	private PayerDAO payerDAO;
	
	public PagingInfo queryForListWithPaging(Map paramMap) throws  ServiceException{		
		return this.queryForListWithPaging("payer.getPayers", paramMap, "ID",ORDER_TYPE.ASC);
	}
	
	public List<PayerTo> getPayersByCreditId(Integer creditId){
		try{
		return payerDAO.getPayersByCreditId(creditId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	
	public Integer savePayer(PayerTo payer) throws Exception{
		return payerDAO.insertPayer(payer);
	}
	public void updatePayer(PayerTo payer) throws Exception{
		payerDAO.updatePayer(payer);
	}
	
	public PayerTo getPayerById(int id){
		return payerDAO.getPayerById(id);
	}
	
	public void deletePayer(PayerTo payer){
		payerDAO.deletePayer(payer);
	}

	public PayerDAO getPayerDAO() {
		return payerDAO;
	}

	public void setPayerDAO(PayerDAO payerDAO) {
		this.payerDAO = payerDAO;
	}
}

