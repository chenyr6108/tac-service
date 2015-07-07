package com.brick.project.service;



import java.util.List;
import java.util.Map;

import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.base.to.PagingInfo;
import com.brick.log.to.ActionLogTo;
import com.brick.project.dao.TagDAO;
import com.brick.project.to.TagTo;


public class TagService extends BaseService {
	private TagDAO tagDAO;
	
	public PagingInfo queryForListWithPaging(String tagName,Map paramMap) throws  ServiceException{		
		paramMap.put("tagName", tagName);
		return this.queryForListWithPaging("tag.getTags", paramMap, "create_date",ORDER_TYPE.ASC);
	}


	/**
	 * 判断标签名称是否已存在
	 * @param tagName
	 * @return
	 */
	public boolean isExistTagName(String tagName,Integer tagType){
		boolean isExist = false;
		int count = tagDAO.getTagCountByTagName(tagName,tagType);
		if(count>0){
			isExist = true;
		}
		return isExist;
	}
	
	public void saveTag(TagTo tag,String userName,String ip) throws Exception{
		tagDAO.insertTag(tag);
		ActionLogTo actionLogTo = new ActionLogTo();
		actionLogTo.setLogBy(userName);
		actionLogTo.setLogAction("新增标签");
		actionLogTo.setLogContent(tag.toString());
		actionLogTo.setLogIp(ip);
		this.insertActionLog(actionLogTo);
	}
	
	public void updateTag(TagTo tag,String userName,String ip) throws Exception{
		TagTo tagtO = tagDAO.getTagTo(tag.getId());
		if(!tagtO.equals(tag)){
			tagDAO.updateTag(tag);
			ActionLogTo actionLogTo = new ActionLogTo();
			actionLogTo.setLogBy(userName);
			actionLogTo.setLogAction("标签修改");
			actionLogTo.setLogContent(tagtO.toString() + "---->" +tag.toString());
			actionLogTo.setLogIp(ip);
			this.insertActionLog(actionLogTo);
		}		
	}
	
	
	public TagTo getTag(int id){
		return tagDAO.getTagTo(id);
	}
	
	public void updateTagStatus(int id,int userid,String userName,int status,String ip) throws Exception{
		tagDAO.updateTagStatus(id, userid,status);
		
		TagTo tag = tagDAO.getTagTo(id);
		ActionLogTo actionLogTo = new ActionLogTo();
		actionLogTo.setLogBy(userName);
		if(status==0){
			actionLogTo.setLogAction("标签启用");
		}else{
			actionLogTo.setLogAction("标签作废");
		}		
		actionLogTo.setLogContent(tag.toString());
		actionLogTo.setLogIp(ip);
		this.insertActionLog(actionLogTo);
	}
	
	/**
	 * 获取某类型所有标签
	 * @param tagType
	 * @return
	 */
	public List<TagTo> getAllTags(Integer tagType){
		return tagDAO.getAllTags(tagType);
	}
	
	/**
	 * 根据案件ID,l类型 获取该案件的标签
	 * @param projectId
	 * @param tagType
	 * @return
	 */
	public List<TagTo> getProjectTags(int projectId,Integer tagType){
		return tagDAO.getProjectTags(projectId,tagType);
	}
	
	/**
	 * 保存 标签与案件的关联信息
	 * @param projectId
	 * @param tagType
	 * @param tags
	 * @param userid
	 * @param ip
	 * @throws Exception 
	 */
	public void saveTag2Prjt_Credit(int projectId,Integer tagType,int [] tags,String userId,String ip) throws Exception{
		
		StringBuffer memo = new StringBuffer("");
		List<TagTo> tagList = this.getProjectTags(projectId,tagType);
		if(tags!= null &&tags.length>0){			
			for(int i=0,len=tags.length;i<len;i++){
				int tagId = tags[i];
				boolean isExist = false;
				for(int j=0,size= tagList.size();j<size;j++ ){
					if(tagId == tagList.get(j).getId()){
						isExist = true;
						break;
					}
				}
				if(!isExist){//新增一条数据
					tagDAO.insertTag2Prjt_Credit(tagId, projectId);	
					TagTo tag = tagDAO.getTagTo(tagId);
					memo.append("添加标签：" + tag.getTagName() + " ");
				}
			}
			
			for(int i=0,size= tagList.size();i<size;i++){
				boolean isExist = false;
				for(int j=0,len=tags.length;j<len;j++){
					if(tagList.get(i).getId()==tags[j]){
						isExist = true;
					}
				}
				if(!isExist){//删除
					tagDAO.deleteTag2Prjt_Credit(tagList.get(i).getTag2project());
					memo.append("删除标签：" + tagList.get(i).getTagName() + " ");
				}
			}
		}else{
			for(int i=0,size= tagList.size();i<size;i++){
				tagDAO.deleteTag2Prjt_Credit(tagList.get(i).getTag2project());
				memo.append("删除标签：" + tagList.get(i).getTagName() + " ");
			}
		}
		//业务日志
		if(!"".equals(memo.toString())){
			this.addBusinessLog(String.valueOf(projectId), "报告管理", "标签", memo.toString(), userId, ip);
		}
	}
	public TagDAO getTagDAO() {
		return tagDAO;
	}

	public void setTagDAO(TagDAO tagDAO) {
		this.tagDAO = tagDAO;
	}
}

