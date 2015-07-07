package com.brick.completedFile.biz;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.service.BaseService;
import com.brick.completedFile.dao.CompletedFileDao;
public class CompletedFileBiz extends BaseService {

	private CompletedFileDao completedFileDao;
	
	public CompletedFileDao getCompletedFileDao() {
		return completedFileDao;
	}

	public void setCompletedFileDao(CompletedFileDao completedFileDao) {
		this.completedFileDao = completedFileDao;
	}

	Log logger=LogFactory.getLog(CompletedFileBiz.class);
	
	//查询时间列表
	public List<Map<String,Object>> getDateList(String fileType) throws Exception {
		List<Map<String,Object>> resultList=null;
		resultList=this.completedFileDao.getDateList(fileType);
		return resultList;
	}
	

}
