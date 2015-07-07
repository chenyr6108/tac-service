package com.brick.help.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.help.service.HelpService;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;
import com.ueditor.Uploader;

public class HelpCommand extends BaseCommand {
	
	private Log logger = LogFactory.getLog(HelpCommand.class);
	
	private HelpService helpService;
	
	public HelpService getHelpService() {
		return helpService;
	}

	public void setHelpService(HelpService helpService) {
		this.helpService = helpService;
	}

	public void getMenu(Context context) {
		Map outputMap=new HashMap();
		List allMenuList=null;
		try {
			allMenuList=(List)DataAccessor.query("help.getMenuList",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("allMenuList", allMenuList);
		Output.jspOutput(outputMap, context, "/help/document.jsp");
	}
	
	public void saveHelpDocument(Context context){
		String second_id = (String) context.contextMap.get("second_id");
		String content = (String) context.contextMap.get("content");
		int count = helpService.getCountBySecondId(Integer.parseInt(second_id));
		if(count==0){
			helpService.saveHelpDocument(Integer.parseInt(second_id),content, String.valueOf(context.request.getSession().getAttribute("s_employeeId")));	
		}else{
			helpService.updateHelpDocument(Integer.parseInt(second_id),content, String.valueOf(context.request.getSession().getAttribute("s_employeeId")));	
		}
		Output.jsonFlageOutput(true, context);
	}
	
	public void getHelpDocument(Context context){
		String second_id = (String) context.getRequest().getSession().getAttribute("__second_id");
		String content = helpService.getHelpDocument(Integer.parseInt(second_id));
		Map outputMap = new HashMap();
		if(StringUtils.isEmpty(content)){
			outputMap.put("content", "此菜单暂无文档，请到【帮助功能】中进行编辑或者点击<a href=\"javascript:;\" onclick=\"modifyHelpDocument("+second_id+")\"><font color=\"red\">此处</font></a>进行编辑。");
		}else{
			outputMap.put("content", content);
		}
		
		Output.jsonOutput(outputMap, context);
	}
	
	public void editDocument(Context context){
		String second_id = (String) context.contextMap.get("second_id");
		String content = helpService.getHelpDocument(Integer.parseInt(second_id));
		if(StringUtils.isEmpty(content)){
			String name = helpService.getMenuNameById(Integer.parseInt(second_id));
			content = helpService.getHelpDocument(438);//帮助文档模板
			content = content.replaceAll("帮助文档模板", name);
		}
		Map outputMap = new HashMap();
		outputMap.put("second_id", second_id);
		outputMap.put("content", content);
		Output.jspOutput(outputMap, context, "/help/editDocument.jsp");
	}
	
	public void getHelpDocumentByParams(Context context){
		String second_id = (String) context.contextMap.get("second_id");
		String content = helpService.getHelpDocument(Integer.parseInt(second_id));
		if(StringUtils.isEmpty(content)){
			String name = helpService.getMenuNameById(Integer.parseInt(second_id));
			content = helpService.getHelpDocument(438);//帮助文档模板
			content = content.replaceAll("帮助文档模板", name);
		}
		Map outputMap = new HashMap();
		outputMap.put("content", content);
		Output.jsonOutput(outputMap, context);
	}
	
	public void setSecondInSession(Context context){
		String second_id = (String) context.contextMap.get("__second_id");
		context.getRequest().getSession().setAttribute("__second_id", second_id);
	}
	
	public void downloadImage(Context context) throws Exception{
		Uploader uploader = new Uploader(context.request);		
		uploader.download(context.response);
	}
	public void downloadFile(Context context) throws Exception{
		Uploader uploader = new Uploader(context.request);		
		uploader.download(context.response);
	}
	
	public void uploadImage(Context context) throws Exception{
		List<FileItem> fileItems = (List<FileItem>) context.contextMap.get("uploadList");
		Uploader uploader = new Uploader(context.request);
		
		if(fileItems!=null && fileItems.size()>0){
			String result  = uploader.uploadFile(fileItems.get(0));
			context.response.getWriter().print(result);
		}
		
	}
	
	public void queryHelpCommand(Context context){
		Map outputMap = new HashMap();
		Output.jspOutput(outputMap, context, "/help/documents.jsp");
	}
	
	public void newHelpCommand(Context context){
		Map outputMap=new HashMap();
		List allMenuList=null;
		try {
			allMenuList=(List)DataAccessor.query("help.getMenuList",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("allMenuList", allMenuList);
		Output.jspOutput(outputMap, context, "/help/newdocument.jsp");
	}
	
	public void getDocumentTemplate(Context context){
		String second_id = (String) context.contextMap.get("second_id");
		String name = helpService.getMenuNameById(Integer.parseInt(second_id));
		String content = helpService.getHelpDocument(438);//帮助文档模板
		content = content.replaceAll("帮助文档模板", name);

		Map outputMap = new HashMap();
		outputMap.put("content", content);
		Output.jsonOutput(outputMap, context);
	}
}
