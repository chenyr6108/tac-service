package com.brick.job.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.CreditLineTO;
import com.brick.service.core.DataAccessor;
import com.brick.util.StringUtils;

public class CreditLineCleanJob extends BaseService {
	
	Log logger = LogFactory.getLog(CreditLineCleanJob.class);
	
	@Transactional
	public void doService(){
		List<CreditLineTO> list = null;
		Map<String, Object> grantplan = null;
		try {
			//清理联保额度
			list = (List<CreditLineTO>) queryForList("job.getLien");
			for (CreditLineTO creditLine : list) {
				update("job.cleanLien", creditLine);
				grantplan =  (Map<String, Object>)queryForObj("job.getPlanById", creditLine);
				grantplan.put("s_employeeName", "系统");
				grantplan = initMap(grantplan);
				insert("productCredit.addGrantPlanLog", grantplan);
			}
			System.out.println("清理联保授信" + list.size() + "个");
			
			//清理回购额度
			list = (List<CreditLineTO>) queryForList("job.getRepurch");
			for (CreditLineTO creditLine : list) {
				update("job.cleanRepurch", creditLine);
				grantplan =  (Map<String, Object>)queryForObj("job.getPlanById", creditLine);
				grantplan.put("s_employeeName", "系统");
				grantplan = initMap(grantplan);
				insert("productCredit.addGrantPlanLog", grantplan);
			}
			System.out.println("清理回购授信" + list.size() + "个");
			
			//清理交机前拨款额度
			list = (List<CreditLineTO>) queryForList("job.getAdvance");
			for (CreditLineTO creditLine : list) {
				update("job.cleanLienAdvance", creditLine);
				grantplan =  (Map<String, Object>)queryForObj("job.getPlanById", creditLine);
				grantplan.put("s_employeeName", "系统");
				grantplan = initMap(grantplan);
				insert("productCredit.addGrantPlanLog", grantplan);
			}
			System.out.println("清理交机前授信" + list.size() + "个");
			
			//清理发票待补额度
			list = (List<CreditLineTO>) queryForList("job.getVoice");
			for (CreditLineTO creditLine : list) {
				update("job.cleanVoice", creditLine);
				grantplan =  (Map<String, Object>)queryForObj("job.getPlanById", creditLine);
				grantplan.put("s_employeeName", "系统");
				grantplan = initMap(grantplan);
				insert("productCredit.addGrantPlanLog", grantplan);
			}
			System.out.println("清理发票待补授信" + list.size() + "个");
		} catch (ServiceException e) {
			logger.error(e);
			throw e;
		}
	}
	
	@Transactional
	public void doCreditLog(){
		List<CreditLineTO> list = null;
		Map<String, Object> grantplan = null;
		list = (List<CreditLineTO>) queryForList("job.getAllCredit");
		for (CreditLineTO creditLine : list) {
			grantplan =  (Map<String, Object>)queryForObj("job.getPlanById", creditLine);
			grantplan.put("s_employeeName", "系统");
			grantplan = initMap(grantplan);
			insert("productCredit.addGrantPlanLog", grantplan);
		}
		System.out.println("记录日志" + list.size() + "个");
		
	}
	
	private Map<String, Object> initMap(Map<String, Object> map){
		List<String> used = new ArrayList<String>();
		used.add("LIEN_GRANT_PRICE");
		used.add("REPURCH_GRANT_PRICE");
		used.add("REPURCHLOSS_GRANT_PRICE");
		used.add("LIEN_LAST_PRICE");
		used.add("REPURCH_LAST_PRICE");
		used.add("REPURCHLOSS_LAST_PRICE");
		used.add("ADVANCEMACHINE_LAST_PRICE");
		used.add("ADVANCEMACHINE_GRANT_PRICE");
		used.add("VOICE_CREDIT");
		used.add("VOICE_LAST_CREDIT");
		for(String key : map.keySet()){
			if (map.get(key) == null) {
				if (used.contains(key)) {
					map.put(key, 0);
				}
			}
		}
		return map;
	}
	
}
