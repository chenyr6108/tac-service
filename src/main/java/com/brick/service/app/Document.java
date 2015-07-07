package com.brick.service.app;


import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


public class Document extends AService {	
	Log logger = LogFactory.getLog(Document.class);
	
	/**
	 * 
	 * @param context
	 */
	public void loadFolderContent(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		List rsContent = null;
		List rsPath = null;		
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			try{
				rsContent = (List)DataAccessor.query("document.loadFolderContent", context.contextMap, DataAccessor.RS_TYPE.LIST);				
				rsPath = (List)DataAccessor.query("document.loadFolderPath", context.contextMap, DataAccessor.RS_TYPE.LIST);	
			}catch(Exception e){
				errList.add("ϵͳ����");				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		
		
		/*-------- output --------*/
		StringBuffer sb = new StringBuffer();
		sb.append("<ul class='jqueryFileTree' style='display: none;' path='");
		
		for(Object o : rsPath){			
			Map m = (Map)o;
			sb.append(" / ");
			sb.append((String)m.get("title"));
		}
		sb.append("'>");
		
		for(Object o : rsContent){
			Map m = (Map)o;
			sb.append("<li");
			
			
			String isFolder = (String)m.get("isFolder");
			if("1".equals(isFolder)){
				sb.append(" class=\"directory collapsed\" ");
				sb.append(" isFolder='1' ");
			}else{
				sb.append(" class=\"file\" ");
			}						
			
			
			sb.append(" crateDate='" + m.get("crateDate") + "' ");
			sb.append(" modifyDate='" + m.get("modifyDate") + "' ");
			
			sb.append(">");
			sb.append("<a id=\"" + m.get("uid") + "\" href=\"#\" rel=\"" + m.get("uid") + "\">");
			sb.append(m.get("title"));
			sb.append("</a></li>");	
			
		}
		sb.append("</ul>");
		System.out.print(sb);
		Output.txtOutput(sb.toString(),context);
		
	}
	
	/**
	 * 
	 * @param context
	 */
	public void createFolder(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map rs = null;
		
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			try{
				rs = (Map)DataAccessor.query("document.createFolder", context.contextMap, DataAccessor.RS_TYPE.MAP);				
				if(!"0".equals(rs.get("rtnCode"))){
					errList.add((String)rs.get("rtnDes"));
				}
			}catch(Exception e){
				errList.add("ϵͳ����");				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		
		
		/*-------- output --------*/
		outputMap.put("rs", rs);
		outputMap.put("errList", errList);
		
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 
	 * @param context
	 */
	public void createFile(Context context){
		List errList = context.errList;
		Map outputMap = new HashMap();
		Integer rs = null;
		
		
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			try{			
				
				context.contextMap.put("fileInfo", context.fileInfo);
				
				DataAccessor.execute("document.createFile", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);				
				
			}catch(Exception e){
				errList.add("ϵͳ����");				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		
		
		/*-------- output --------*/
		outputMap.put("errList", errList);
		
		Output.jsonOutput(outputMap, context);
	}	
		
	

}
