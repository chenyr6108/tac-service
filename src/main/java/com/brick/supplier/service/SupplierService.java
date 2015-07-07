package com.brick.supplier.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.supplier.dao.SupplierDAO;

public class SupplierService extends BaseService{
	
	private SupplierDAO supplierDAO;
	
	public List<Map<String, Object>> getSupplierContactInfo(String supplierName,String notInList,Date beginDate,Date endDate) throws SQLException, DaoException{
		return supplierDAO.getSupplierContactInfo(supplierName,notInList,beginDate,endDate);
	}

	public SupplierDAO getSupplierDAO() {
		return supplierDAO;
	}

	public void setSupplierDAO(SupplierDAO supplierDAO) {
		this.supplierDAO = supplierDAO;
	}

	
}
