package com.brick.supplier.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;

public class SupplierDAO extends BaseDAO{
	
	public List<Map<String, Object>> getSupplierContactInfo(String supplierName,String notInList,Date beginDate,Date endDate) throws SQLException, DaoException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("supplierName", supplierName);
		params.put("notInList", notInList);
		if(beginDate!=null){
			params.put("beginDate", new java.sql.Date(beginDate.getTime()));
			params.put("date", new java.sql.Date(beginDate.getTime()));
		}
		if(endDate!=null){
			params.put("endDate", new java.sql.Date(endDate.getTime()));
			params.put("date", new java.sql.Date(endDate.getTime()));
		}
		return this.queryForListUseMap("supplier.getSupplierContactInfo", params);
	}
}
