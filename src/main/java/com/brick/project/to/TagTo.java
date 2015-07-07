package com.brick.project.to;

import com.brick.base.to.BaseTo;

public class TagTo extends BaseTo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int projectId;
	private int tag2project;
	private String tagName;
	private String tagCode;
	private String comments;
	private int tagType;
	private String tagColor;
	private int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagCode() {
		return tagCode;
	}
	public void setTagCode(String tagCode) {
		this.tagCode = tagCode;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getTag2project() {
		return tag2project;
	}
	public void setTag2project(int tag2project) {
		this.tag2project = tag2project;
	}
	public int getTagType() {
		return tagType;
	}
	public void setTagType(int tagType) {
		this.tagType = tagType;
	}
	public String getTagColor() {
		return tagColor;
	}
	public void setTagColor(String tagColor) {
		this.tagColor = tagColor;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		StringBuffer content = new StringBuffer("");
		content.append(" 标签名称：" + tagName);
		content.append(" 标签Code：" + tagCode);
		content.append(" 标签颜色：" + tagColor);
		content.append(" 标签类型：" + tagType);
		content.append(" 标签备注：" + comments);
		return content.toString();
	}
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		TagTo tag = (TagTo) obj;
		if(this.id==tag.getId() && this.tagName.equals(tag.getTagName())
				&& this.tagCode.equals(tag.getTagCode()) && this.tagColor.equals(tag.getTagColor())
				&& this.tagType == tag.getTagType() 
				&&((this.comments==null && tag.getComments()==null)||(this.comments!=null && this.comments.equals(tag.getComments())))){
			isEqual = true;
		}
		return isEqual;
	}
	
	
}
