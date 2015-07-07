package com.brick.sys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.sys.DAO.DataDictionaryDAO;
import com.brick.sys.TO.DataDictionaryTO;

public class DataDictionaryService extends BaseService {
	
	Log logger=LogFactory.getLog(DataDictionaryService.class);
	
	private DataDictionaryDAO dataDictionaryDAO;

	public DataDictionaryDAO getDataDictionaryDAO() {
		return dataDictionaryDAO;
	}

	public void setDataDictionaryDAO(DataDictionaryDAO dataDictionaryDAO) {
		this.dataDictionaryDAO = dataDictionaryDAO;
	}
	
	public List<DataDictionaryTO> getDBTableDetail(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableInfo start.....");
		}
		
		List<DataDictionaryTO> resultList=this.dataDictionaryDAO.getDBTableDetail(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableInfo end.....");
		}
		
		return resultList;
	}
	
	public List<DataDictionaryTO> isMaintenanceTable() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......isMaintenanceTable start.....");
		}
		
		List<DataDictionaryTO> resultList=this.dataDictionaryDAO.isMaintenanceTable();
		
		if(logger.isDebugEnabled()) {
			logger.debug("......isMaintenanceTable end.....");
		}
		
		return resultList;
	}
	
	@Transactional
	public void deleteInsertTableInfo(DataDictionaryTO dataDictionaryTO) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......deleteInsertTableInfo start.....");
		}
		
		this.dataDictionaryDAO.deleteTableInfo(dataDictionaryTO);
		
		for(int i=0;i<dataDictionaryTO.getColumnNameInsert().length;i++) {
			dataDictionaryTO.setColumnName(dataDictionaryTO.getColumnNameInsert()[i]);
			dataDictionaryTO.setDataType(dataDictionaryTO.getDataTypeInsert()[i]);
			dataDictionaryTO.setDescription(dataDictionaryTO.getDescriptionInsert()[i]);
			this.dataDictionaryDAO.insertTableInfo(dataDictionaryTO);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("......deleteInsertTableInfo end.....");
		}
	}
	
	public List<DataDictionaryTO> getDBTableConstraint(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableConstraint start.....");
		}
		
		List<DataDictionaryTO> resultList=this.dataDictionaryDAO.getDBTableConstraint(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableConstraint end.....");
		}
		
		return resultList;
	}

	public List<DataDictionaryTO> getDBTableDetail1(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableDetail1 start.....");
		}
		
		List<DataDictionaryTO> resultList=this.dataDictionaryDAO.getDBTableDetail1(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableDetail1 end.....");
		}
		
		return resultList;
		
	}
	
	//获得此表是否维护过
	public List<DataDictionaryTO> getDBTableDetail2(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableDetail2 start.....");
		}
		
		List<DataDictionaryTO> resultList=this.dataDictionaryDAO.getDBTableDetail2(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getDBTableDetail2 end.....");
		}
		
		return resultList;
		
	}
	
	@Transactional
	public void insertTableInfo(DataDictionaryTO dataDictionaryTO) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......insertTableInfo start.....");
		}
		
		for(int i=0;i<dataDictionaryTO.getColumnNameInsert().length;i++) {
			dataDictionaryTO.setColumnName(dataDictionaryTO.getColumnNameInsert()[i]);
			dataDictionaryTO.setDataType(dataDictionaryTO.getDataTypeInsert()[i]);
			dataDictionaryTO.setDescription(dataDictionaryTO.getDescriptionInsert()[i]);
			dataDictionaryTO.setVersion(1);
			this.dataDictionaryDAO.insertTableInfo(dataDictionaryTO);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("......insertTableInfo end.....");
		}
	}
	
	public List<DataDictionaryTO> getItUserList() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("......getItUserList start.....");
		}
		
		List<DataDictionaryTO> resultList=null;
		
		resultList=this.dataDictionaryDAO.getItUserList();
		
		if(resultList==null) {
			resultList=new ArrayList<DataDictionaryTO>();
		}

		if(logger.isDebugEnabled()) {
			logger.debug("......getItUserList end.....");
		}
		
		return resultList;
		
	}
}
