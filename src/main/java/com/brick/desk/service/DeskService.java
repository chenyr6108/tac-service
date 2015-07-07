package com.brick.desk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.desk.dao.DeskDAO;
import com.brick.desk.to.DeskTO;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;

public class DeskService extends BaseService {
	private DeskDAO deskDAO;

	public DeskDAO getDeskDAO() {
		return deskDAO;
	}

	public void setDeskDAO(DeskDAO deskDAO) {
		this.deskDAO = deskDAO;
	}
	
	public List<DeskTO> getPermissionGroup() {
		return this.deskDAO.getPermissionGroup();
	}
	
	public int checkIsExist(Context context) {
		return this.deskDAO.checkIsExist(context);
	}
	
	public void insertPermissionGroup(Context context) {
		this.deskDAO.insertPermissionGroup(context);
	}
	
	public void deletePermissionGroup(Context context) {
		this.deskDAO.deletePermissionGroup(context);
	}
	
	public List<Map<String,String>> getDeskAuthList(Context context) {
		return this.deskDAO.getDeskAuthList(context);
	}
	
	public List<DeskTO> getPermissionMap(Context context) {
		return this.deskDAO.getPermissionMap(context);
	}
	
	public void deletePermissionMap(Context context) {
		this.deskDAO.deletePermissionMap(context);
	}
	
	@Transactional
	public void savePermissionMap(Context context) {
		
		this.deletePermissionMap(context);
		String [] auth=HTMLUtil.getParameterValues(context.request,"permission","");
		for(int i=0;i<auth.length;i++) {
			context.contextMap.put("auth",auth[i]);
			this.deskDAO.savePermissionMap(context);
		}
	}
	
	public List<DeskTO> getPermissionList(Context context) {
		return this.deskDAO.getPermissionList(context);
	}
	
	public List<DeskTO> getUserList() {
		return this.deskDAO.getUserList();
	}
	
	public void deletePermissionUser(Context context) {
		this.deskDAO.deletePermissionUser(context);
	}
	@Transactional
	public void savePermissionUser(Context context) {
		
		this.deletePermissionUser(context);
		String [] userId=HTMLUtil.getParameterValues(context.request,"userId","");
		for(int i=0;i<userId.length;i++) {
			if("null".equals(userId[i])) {
				continue;
			}
			context.contextMap.put("userId",userId[i]);
			this.deskDAO.savePermissionUser(context);
		}
	}
	
	public List<Map> getPermissionUserList(Context context) {
		return this.deskDAO.getPermissionUserList(context);
	}
}
