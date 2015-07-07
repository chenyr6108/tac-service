package com.brick.common.mail.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.common.mail.to.EmailPlanTO;
import com.brick.common.mail.to.MailSettingTo;

public class MailUtilDAO extends BaseDAO {

	public void insertEmailSendRecord(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("common.insertEmailSendRecord",mailSettingTo);
	}
	
	public void saveEmailPlan(EmailPlanTO emailPlan){
		this.getSqlMapClientTemplate().insert("common.saveEmailPlan", emailPlan);
	}
	
	public List<EmailPlanTO> queryEmailPlansByType(int type){
		Map params = new HashMap();
		params.put("type", type);
		return this.getSqlMapClientTemplate().queryForList("common.getEmailPlansByType", params);
	}
	
	public void finsihEmailPlan(int id){
		Map params = new HashMap();
		params.put("id", id);
		params.put("status", 1);
		this.getSqlMapClientTemplate().update("common.updateEmailPlan",params);
	}
}
