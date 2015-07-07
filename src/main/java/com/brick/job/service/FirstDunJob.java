package com.brick.job.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;

public class FirstDunJob extends BaseService {
	
	
	@Transactional(rollbackFor = Exception.class)
	public void doService() throws Exception{
		doFirstDunListener(new Date());
	}
	
	public void doFirstDunListener(Date d) throws SQLException{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("search_date", d);
		List<Map<String, Object>> result = (List<Map<String, Object>>) queryForList("job.getDunInfo", paramMap);
		String rect_id = null;
		Integer dun_day = null;
		Map<String, Object> rect = null;
		for (Map<String, Object> map : result) {
			rect_id = String.valueOf(map.get("RECT_ID"));
			dun_day = Integer.parseInt(String.valueOf(map.get("DUN_DAY")));
			if (rect_id == null || dun_day == null) {
				continue;
			}
			paramMap.put("rect_id", rect_id);
			rect = (Map<String, Object>) queryForObj("job.getFieldForDun", paramMap);
			if (dun_day == 31 && rect.get("FIRST_DUN_31") == null) {
				//updateFirstDun
				rect.put("FIRST_DUN_31", d);
				update("job.updateFirstDun", rect);
			} else if (dun_day == 61 && rect.get("FIRST_DUN_61") == null) {
				rect.put("FIRST_DUN_61", d);
				update("job.updateFirstDun", rect);
			} else if (dun_day == 91 && rect.get("FIRST_DUN_91") == null) {
				rect.put("FIRST_DUN_91", d);
				update("job.updateFirstDun", rect);
			} else if (dun_day == 181 && rect.get("FIRST_DUN_181") == null) {
				rect.put("FIRST_DUN_181", d);
				update("job.updateFirstDun", rect);
			}
		}
	}
	
	
}
