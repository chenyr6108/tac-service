package com.brick.help.service;



import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.help.dao.HelpDAO;

public class HelpService extends BaseService {
	private HelpDAO helpDAO;
	
	@Transactional(rollbackFor=Exception.class)
	public void saveHelpDocument(int second_id,String content,String userId){

		helpDAO.saveHelpDocument(second_id, content,userId);
		String currContent = helpDAO.getHelpDocument(438);//帮助文档模板
		String name = this.getMenuNameById(second_id);
		currContent = currContent.replaceAll("帮助文档模板", name);
		int size = content.length() - currContent.length();
		helpDAO.saveHelpLog(second_id, size, userId);

	}
	@Transactional(rollbackFor=Exception.class)
	public void updateHelpDocument(int second_id,String content,String userId){
		if(second_id!=438){//模板不能做修改
			
			String currContent = helpDAO.getHelpDocument(second_id);
			int size = content.length() - currContent.length();
			helpDAO.saveHelpLog(second_id, size, userId);
			helpDAO.updateHelpDocument(second_id, content,userId);
		}
	}
	
	public String getMenuNameById(int second_id){
		return helpDAO.getMenuNameById(second_id);
	}
	public String getHelpDocument(int second_id){
		return helpDAO.getHelpDocument(second_id);
	}
	
	public int getCountBySecondId(int second_id){
		return helpDAO.getCountBySecondId(second_id);
	}

	public HelpDAO getHelpDAO() {
		return helpDAO;
	}

	public void setHelpDAO(HelpDAO helpDAO) {
		this.helpDAO = helpDAO;
	}
	
}
