package com.brick.base.service;

import java.util.HashMap;
import java.util.Map;

import com.brick.base.to.DataSession;
import com.brick.util.StringUtils;

public class DataSessionService extends BaseService {

	public void doSaveDataSession(DataSession data) {
		if (StringUtils.isEmpty(data) || StringUtils.isEmpty(data.getItem_html())) {
			return;
		}
		String[] dataArray = data.getItem_html().split("█");
		String[] dataMap = null;
		for (String dataString : dataArray) {
			if (dataString.contains(":")) {
				dataMap = dataString.split(":");
				if (dataMap.length == 3 && (!StringUtils.isEmpty(dataMap[0]) || !StringUtils.isEmpty(dataMap[1])) && !StringUtils.isEmpty(dataMap[2])) {
					data.setItem_id(dataMap[0]);
					data.setItem_key(dataMap[1]);
					data.setItem_value(dataMap[2]);
					delete("businessSupport.deleteDataSessionByUserId", data);
					insert("businessSupport.insertDataSessionByUserId", data);
				}
			}
		}
	}
	
	/*public void doSaveDataSession(DataSession data) {
		if (StringUtils.isEmpty(data) || StringUtils.isEmpty(data.getItem_html())) {
			return;
		}
		delete("businessSupport.deleteDataSessionByUserId", data);
		insert("businessSupport.insertDataSessionByUserId", data);
	}*/
	
}
