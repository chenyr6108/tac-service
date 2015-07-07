package com.brick.litigation.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.supplier.to.SupplierGroupTO;

public class LitigationDao extends BaseDAO{
	public void addLitigation(Map<String, Object> map)throws DaoException {
		try {
			getSqlMapClientTemplate().insert("litigation.addLitigation",map);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	public void updateLitigation(Map<String, Object> map)throws DaoException {
		try {
			getSqlMapClientTemplate().update("litigation.updateLitigation",map);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	public void addDunRecord(Map<String, Object> map)throws DaoException {
		try {
			getSqlMapClientTemplate().insert("dunTask.addDunRecord",map);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
}
