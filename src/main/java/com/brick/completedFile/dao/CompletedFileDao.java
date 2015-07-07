package com.brick.completedFile.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class CompletedFileDao extends BaseDAO{
	
	//时间列表
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getDateList(String fileType) throws Exception {
		
			List<Map<String,Object>> resultList=null;
			Map<String,String> param=new HashMap<String,String>();
			param.put("fileType",fileType);
			resultList=this.getSqlMapClientTemplate().queryForList("settleManage.getCFDateList",param);
			return resultList;
		}
	

}
