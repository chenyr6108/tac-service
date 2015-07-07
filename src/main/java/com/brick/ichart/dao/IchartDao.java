package com.brick.ichart.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;

public class IchartDao extends BaseDAO {

	//获得所有办事处
		public List<Map<String,Object>> getDeptList(Map<String,Object> param) throws DaoException {

			List<Map<String,Object>> result=this.getSqlMapClientTemplate().queryForList("employee.getCompany",param);

			if(result==null) {
				result=new ArrayList<Map<String,Object>>();
			}
			return result;
		}
}
