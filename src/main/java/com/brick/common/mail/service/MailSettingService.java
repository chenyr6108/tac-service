package com.brick.common.mail.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.service.BaseService;
import com.brick.common.mail.dao.MailSettingDAO;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.entity.Context;

public class MailSettingService extends BaseService {

	private MailSettingDAO mailSettingDAO;
	private BaseDAO baseDAO;
	
	public MailSettingDAO getMailSettingDAO() {
		return mailSettingDAO;
	}
	public void setMailSettingDAO(MailSettingDAO mailSettingDAO) {
		this.mailSettingDAO = mailSettingDAO;
	}
	public BaseDAO getBaseDAO() {
		return baseDAO;
	}
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	public List<MailSettingTo> query(Context context,Map<String,Object> outputMap) throws Exception {
		
		List<MailSettingTo> resultList=null;
		resultList=(List<MailSettingTo>)this.baseDAO.queryForPage("common.queryMailSettingPage","common.queryMailSettingPageCount",context,outputMap);
		
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public List<MailSettingTo> getMailTypeList(Context context) throws Exception {
		
		List<MailSettingTo> resultList=this.mailSettingDAO.getMailTypeList(context);
		
		return resultList;
	}
	
	public void addEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		this.mailSettingDAO.addEmailSetting(mailSettingTo);
	}
	
	public void updateEmail(Context context) throws Exception {
		this.mailSettingDAO.updateEmail(context);
	}
	
	public MailSettingTo getEmailSetting(Context context) throws Exception {
		
		return this.mailSettingDAO.getEmailSetting(context);
	}
	
	public Map<String,List<MailSettingTo>> getFilterList(Context context) throws Exception {
		
		return this.mailSettingDAO.getFilterList(context);
	}
	
	public List<MailSettingTo> getMailSettingList(Context context,Map<String,Object> outputMap) throws Exception {
		
		List<MailSettingTo> resultList=null;
		resultList=(List<MailSettingTo>)this.baseDAO.queryForPage("common.getMailSettingListPage","common.getMailSettingListPageCount",context,outputMap);
		
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
	public void saveEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		
		this.mailSettingDAO.saveEmailSetting(mailSettingTo);
	}
	
	public MailSettingTo getSetMail(Context context) throws Exception {
		
		return this.mailSettingDAO.getSetMail(context);
	}
	
	public void updateEmailSetting(MailSettingTo mailSettingTo) throws Exception {
		
		this.mailSettingDAO.updateEmailSetting(mailSettingTo);
	}
	
	public List<Map> getDeptList(Context context) throws Exception {
		
		List<Map> resultList=null;
		resultList=(List<Map>)this.baseDAO.queryForList("common.getDeptCompanyList");
		if(resultList==null) {
			resultList=new ArrayList<Map>();
		}
		
		return resultList;
	}
	
	public String getDeptName(Integer deptId) throws Exception {
		Object deptName = this.baseDAO.getSqlMapClientTemplate().queryForObject("common.getDeptCompanyName", deptId);
		if(deptName==null) {
			return "";
		}
		return deptName.toString();
	}
	
	public List<MailSettingTo> getMailSettingListByGroup(String mailTypeGroup) throws Exception {
		
		MailSettingTo to = new MailSettingTo();
		to.setEmailTypeGroup(mailTypeGroup);
		
		List<MailSettingTo> resultList=null;
		resultList=(List<MailSettingTo>)this.baseDAO.queryForList("common.getMailSettingListByGroup", to);
		
		if(resultList==null) {
			resultList=new ArrayList<MailSettingTo>();
		}
		
		return resultList;
	}
	
}
