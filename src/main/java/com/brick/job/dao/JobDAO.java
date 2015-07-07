package com.brick.job.dao;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.job.to.JobTo;

public class JobDAO extends BaseDAO {

	public void insertJob(JobTo jobTo) throws DaoException {
		try {
			getSqlMapClientTemplate().insert("job.insertJob", jobTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void deleteJobs() throws DaoException {
		try {
			getSqlMapClientTemplate().delete("job.deleteJobs");
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public Integer getJobRunFlag(JobTo param) throws DaoException {
		try {
			return (Integer) getSqlMapClientTemplate().queryForObject("job.getJobRunFlag", param);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void insertJobRunLog(JobTo jobTo) throws DaoException {
		try {
			getSqlMapClientTemplate().insert("job.insertJobRunLog", jobTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void updateJobRunStatus(JobTo jobTo) throws DaoException {
		try {
			getSqlMapClientTemplate().update("job.updateJobRunStatus", jobTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void updateJobRunFlag(JobTo jobTo) throws DaoException {
		try {
			getSqlMapClientTemplate().update("job.updateJobRunFlag", jobTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
}
