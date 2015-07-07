package com.brick.help.dao;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.dao.BaseDAO;

public class HelpDAO extends BaseDAO {
	
	public void saveHelpDocument(int second_id,String content,String userId){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("second_id", second_id);
		params.put("content", content);
		params.put("userId", userId);
		this.getSqlMapClientTemplate().insert("help.insertHelp", params);
	}
	public void updateHelpDocument(int second_id,String content,String userId){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("second_id", second_id);
		params.put("content", content);
		params.put("userId", userId);
		this.getSqlMapClientTemplate().update("help.updateHelp", params);
	}
	
	public void saveHelpLog(int second_id,int size,String userId){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("second_id", second_id);
		params.put("size", size);
		params.put("userId", userId);
		this.getSqlMapClientTemplate().insert("help.insertHelpLog", params);		
	}
	public String getHelpDocument(int second_id){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("second_id", second_id);
		return (String) this.getSqlMapClientTemplate().queryForObject("help.getHelpDocument", params);
	}
	
	public String getMenuNameById(int second_id){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", second_id);
		return (String) this.getSqlMapClientTemplate().queryForObject("help.getMenuNameById", params);
	}
	
	public int getCountBySecondId(int second_id){
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("second_id", second_id);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("help.getCountBySecondId", params);
	}
}
