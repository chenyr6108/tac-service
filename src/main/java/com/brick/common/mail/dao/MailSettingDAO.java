package com.brick.common.mail.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.entity.Context;

public class MailSettingDAO extends BaseDAO {

	public List<MailSettingTo> getMailTypeList(Context context) throws Exception {
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getMailTypeList",context.contextMap);
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public void addEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("common.addEmailSetting",mailSettingTo);
	}
	
	public void updateEmail(Context context) throws Exception {
		
		this.getSqlMapClientTemplate().update("common.updateEmail",context.contextMap);
	}
	
	public MailSettingTo getEmailSetting(Context context) throws Exception {
		
		MailSettingTo mailSettingTo=null;
		
		mailSettingTo=(MailSettingTo)this.getSqlMapClientTemplate().queryForObject("common.getEmailSetting",context.contextMap);
		
		if(mailSettingTo==null) {
			mailSettingTo=new MailSettingTo();
		}
		
		return mailSettingTo;
	}
	
	public Map<String,List<MailSettingTo>> getFilterList(Context context) throws Exception {
		
		Map<String,List<MailSettingTo>> resultMap=new HashMap<String,List<MailSettingTo>>();
		
		context.contextMap.put("TYPE","邮件发送结果");
		resultMap.put("sendResultList",this.getSqlMapClientTemplate().queryForList("common.getSendResultList",context.contextMap));
		context.contextMap.put("TYPE","邮件所属功能");
		resultMap.put("mailTypeList",this.getSqlMapClientTemplate().queryForList("common.getMailTypeList",context.contextMap));
		context.contextMap.put("TYPE","邮件所属功能分类");
		resultMap.put("mailTypeGroupList",this.getSqlMapClientTemplate().queryForList("common.getMailTypeList",context.contextMap));
		return resultMap;
	}
	
	public void saveEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("common.saveEmailSetting",mailSettingTo);
	}
	
	public MailSettingTo getSetMail(Context context) throws Exception {
		
		MailSettingTo mailSettingTo=null;
		
		mailSettingTo=(MailSettingTo)this.getSqlMapClientTemplate().queryForObject("common.getSetMail",context.contextMap);
		
		if(mailSettingTo==null) {
			mailSettingTo=new MailSettingTo();
		}
		
		return mailSettingTo;
	}
	
	public void updateEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().insert("common.updateEmailSetting",mailSettingTo);
	}
	
	public MailSettingTo getEmailInfoByEmailType(int emailType) throws Exception {
		
		MailSettingTo mailSettingTo=null;
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("emailType",emailType);
		param.put("TYPE","邮件所属功能");
		mailSettingTo=(MailSettingTo)this.getSqlMapClientTemplate().queryForObject("common.getEmailInfoByEmailType",param);
		
		if(mailSettingTo==null) {
			mailSettingTo=new MailSettingTo();
		}
		return mailSettingTo;
	}
	
	public List<MailSettingTo> getSendMailRecord() throws Exception {
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getSendMailRecord");
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public List<MailSettingTo> getSendMailRecordWithoutQQ() throws Exception {
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getSendMailRecordWithoutQQ");
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}

	public List<MailSettingTo> getSendMailRecordForQQ() throws Exception {
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getSendMailRecordForQQ");
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public List<MailSettingTo> getSendHRMailRecord() throws Exception {
		
		List<MailSettingTo> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getSendHRMailRecord");
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public void updateSendMailRecord(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().update("common.updateSendMailRecord",mailSettingTo);
	}
	
	public void updateSendHRMailRecord(MailSettingTo mailSettingTo) throws Exception {
		
		this.getSqlMapClientTemplate().update("common.updateSendHRMailRecord",mailSettingTo);
	}
}
