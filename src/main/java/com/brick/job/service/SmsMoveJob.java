package com.brick.job.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.ibatis.sqlmap.client.SqlMapClient;

@Service("smsMoveJob")
public class SmsMoveJob {
	
	@Resource(name = "baseService")
	private BaseService baseService;
	
	@Transactional(rollbackFor = Exception.class)
	public void doService() throws Exception{
		SqlMapClient sqlMap_236 = baseService.baseDAO.getSqlMapList().get("sqlMapClient_236");
		System.out.println(sqlMap_236);
		List<Map<String, Object>> list = (List<Map<String, Object>>) baseService.queryForList("sendMessage.selectAllSendMessageForMove");
		if (list != null && list.size() > 0) {
			for (Map<String, Object> map : list) {
				sqlMap_236.insert("sendMessage.saveSendMessageForMove", map);
				baseService.update("sendMessage.updateMessageSendFlag", map);
			}
		}
		
	}
	
}
