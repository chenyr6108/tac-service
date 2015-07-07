package com.brick.ichart.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.ichart.dao.IchartDao;

public class IchartService  extends BaseService {

	Log logger=LogFactory.getLog(IchartService.class);
	private IchartDao ichartDao;
	public IchartDao getIchartDao() {
		return ichartDao;
	}
	public void setIchartDao(IchartDao ichartDao) {
		this.ichartDao = ichartDao;
	}
	
public List<Map<String,Object>> getDeptList(Map<String,Object> param) {
		
		List<Map<String,Object>> result=null;
		
		try {
			result=this.ichartDao.getDeptList(param);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
