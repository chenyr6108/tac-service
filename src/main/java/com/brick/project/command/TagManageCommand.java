package com.brick.project.command;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.project.service.TagService;
import com.brick.project.to.TagTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;


public class TagManageCommand extends BaseCommand{
	
	private TagService tagService;

	public void getTags(Context context) throws Exception{
		
		
		String tagName = (String) context.contextMap.get("tagName");
		String status = (String) context.contextMap.get("status");
		String tagType = (String) context.contextMap.get("tagType");
		PagingInfo pagingInfo = tagService.queryForListWithPaging(tagName,context.contextMap);
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("dw", pagingInfo);
		outputMap.put("tagName", tagName);
		outputMap.put("status", status);
		outputMap.put("tagType", tagType);
		Output.jspOutput(outputMap, context, "/project/tagManage.jsp");
	}
	
	
	public void initTagCreate(Context context) throws Exception{
		Output.jspOutput(new HashMap(), context, "/project/tagCreate.jsp");
	}

	public void initTagModify(Context context) throws Exception{
		String id = (String) context.contextMap.get("id");
		Map<String, Object> outputMap = new HashMap<String, Object>();
		if(id!= null){
			TagTo tag = tagService.getTag(Integer.parseInt(id));
			outputMap.put("tag", tag);
		}
		Output.jspOutput(outputMap, context, "/project/tagModify.jsp");
	}

	public void createTag(Context context) throws Exception{
		int userid = (Integer) context.contextMap.get("s_employeeId");
		String tagName = (String) context.contextMap.get("tag_name");
		String tagCode = (String) context.contextMap.get("tag_code");
		String tagType = (String) context.contextMap.get("tag_type");
		String tagColor = (String) context.contextMap.get("tag_color");
		String comments = (String) context.contextMap.get("comments");
		
		TagTo tag = new TagTo();
		tag.setTagName(tagName);
		tag.setTagCode(tagCode);
		tag.setTagType(Integer.parseInt(tagType));
		tag.setTagColor(tagColor);
		tag.setCreate_by(String.valueOf(userid));
		tag.setComments(comments);
		
		tagService.saveTag(tag,(String)context.contextMap.get("s_employeeName"),(String)context.contextMap.get("IP"));
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("success", true);
		Output.jsonOutput(outputMap, context);
	}
	
	public void checkTagName(Context context) throws Exception{
		String tagName = (String) context.contextMap.get("tagName");
		String tagType = (String) context.contextMap.get("tagType");
		boolean isExist = tagService.isExistTagName(tagName,tagType!=null&&!"".equals(tagType)?Integer.parseInt(tagType):null);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("isExist", isExist);
		Output.jsonOutput(outputMap, context);
	}
	
	public void modifyTag(Context context) throws Exception{
		int userid = (Integer) context.contextMap.get("s_employeeId");
		String id = (String) context.contextMap.get("id");
		String tagName = (String) context.contextMap.get("tag_name");
		String tagCode = (String) context.contextMap.get("tag_code");
		String tagType = (String) context.contextMap.get("tag_type");
		String tagColor = (String) context.contextMap.get("tag_color");
		String comments = (String) context.contextMap.get("comments");
		
		TagTo tag = new TagTo();
		tag.setId(Integer.parseInt(id));
		tag.setTagName(tagName);
		tag.setTagCode(tagCode);
		tag.setTagType(Integer.parseInt(tagType));
		tag.setTagColor(tagColor);
		tag.setModify_by(String.valueOf(userid));
		tag.setComments(comments);
		
		tagService.updateTag(tag,(String)context.contextMap.get("s_employeeName"),(String)context.contextMap.get("IP"));
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("success", true);
		Output.jsonOutput(outputMap, context);
		
	}
	
	
	public void updateTagStatus(Context context)throws Exception{
		int userid =  (Integer) context.contextMap.get("s_employeeId");
		String id = (String) context.contextMap.get("id");
		String status = (String) context.contextMap.get("status");
		tagService.updateTagStatus(Integer.parseInt(id), userid,(String)context.contextMap.get("s_employeeName"),status!=null&&!"".equals(status)?Integer.parseInt(status):0,(String)context.contextMap.get("IP"));
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("success", true);
		Output.jsonOutput(outputMap, context);
	}
	
	public void getProjectTags(Context context)throws Exception{
		String projectId = (String) context.contextMap.get("id");
		String tagType = (String) context.contextMap.get("tagType");
		List<TagTo> tagList = tagService.getProjectTags(Integer.parseInt(projectId),tagType!=null&&!"".equals(tagType)?Integer.parseInt(tagType):null);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("tags", tagList);
		Output.jsonOutput(outputMap, context);
	}
	
	public TagService getTagService() {
		return tagService;
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}
}
