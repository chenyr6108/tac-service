package com.brick.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.project.to.TagTo;

public class TagDAO extends BaseDAO{
	/**
	 * 根据TAGNAME，类型 获取TAG的总数
	 * @param tagName
	 * @param tagType
	 * @return
	 */
	public int getTagCountByTagName(String tagName,Integer tagType){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("tagName", tagName);
		param.put("tagType", tagType);
		return  (Integer) this.getSqlMapClientTemplate().queryForObject("tag.getTagCount",param);
	}
	
	/**
	 * 保存
	 * @param tag
	 * @throws DaoException
	 */
	public void insertTag(TagTo tag) throws DaoException{
		this.getSqlMapClientTemplate().insert("tag.insertTag", tag);		
	}
	
	/**
	 * 根据ID查询
	 * @param id
	 * @return
	 */
	public TagTo getTagTo(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		return (TagTo) this.getSqlMapClientTemplate().queryForObject("tag.getTags", param);
	}
	
	/**
	 * 更新
	 * @param tag
	 */
	public void updateTag(TagTo tag){
		this.getSqlMapClientTemplate().update("tag.updateTag", tag);
	}
	
	/**
	 * 更新标签状态
	 * @param id
	 * @param status
	 * @param userid
	 */
	public void updateTagStatus(int id,int userid,int status){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		param.put("modify_by", userid);
		param.put("status", status);
		this.getSqlMapClientTemplate().update("tag.updateTagStatus", param);
	}
	/**
	 * 获取所有启用TAG
	 * @return
	 */
	public List<TagTo> getAllTags(Integer tagType){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("tagType", tagType);
		param.put("status", 0);
		return this.getSqlMapClientTemplate().queryForList("tag.getTags",param);
	}
	
	/**
	 * 根据案件ID 查询关联的标签
	 * @param projectId
	 * @return
	 */
	public List<TagTo> getProjectTags(int projectId,Integer tagType){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("projectId", projectId);
		param.put("tagType", tagType);
		return this.getSqlMapClientTemplate().queryForList("tag.getProjectTags",param);
	}
	
	/**
	 * 绑定标签与案件
	 * @param tagId
	 * @param projectId
	 */
	public void insertTag2Prjt_Credit(int tagId,int projectId){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("tagId", tagId);
		param.put("projectId", projectId);
		this.getSqlMapClientTemplate().insert("tag.insertTag2Prjt_Credit",param);
	}
	/**
	 * 删除标签与案件的绑定
	 * @param id
	 */
	public void deleteTag2Prjt_Credit(int id){
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("id", id);
		this.getSqlMapClientTemplate().update("tag.deleteTag2Prjt_Credit",param);
	}
}
