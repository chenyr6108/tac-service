package com.brick.report.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.util.DateUtil;

public class VipProjectInfoReportService extends BaseService {
	
	@Transactional(rollbackFor = Exception.class)
	public void doService() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("paramDate", DateUtil.dateToStr(new Date()));
		this.delete("job.deleteProjectInfoForVip", paramMap);
		this.insert("job.insertProjectInfoForVip", paramMap);
	}
}
