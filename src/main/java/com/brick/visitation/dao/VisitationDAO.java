package com.brick.visitation.dao;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.visitation.to.VisitationReportTo;
import com.brick.visitation.to.VisitationTO;

public class VisitationDAO extends BaseDAO {

	public Integer getApplyInfoByCreditId(VisitationTO visitationTo) throws DaoException {
		Integer result = null;
		try {
			result = (Integer) getSqlMapClientTemplate().queryForObject("visitation.getApplyInfoByCreditId",visitationTo);
			return result;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void updateForAll(VisitationTO visitationTO) throws DaoException {
		try {
			getSqlMapClientTemplate().update("visitation.updateForAll",visitationTO);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public void updatePortion(VisitationTO visitationTO) throws DaoException {
		try {
			getSqlMapClientTemplate().update("visitation.updatePortion",visitationTO);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public Integer getReportByVisitId(VisitationReportTo reportTo) throws DaoException {
		try {
			return (Integer) getSqlMapClientTemplate().queryForObject("visitation.getReportByVisitId", reportTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

}
