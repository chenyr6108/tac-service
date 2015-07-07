package com.brick.backVisit.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.FileExcelUpload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.log.service.LogPrint;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 
 * @author 齐姜龙
 * @创建日期 2011-3-28
 * @版本 V 1.0
 */
public class BackVisitMessageService extends BaseCommand{
	Log logger = LogFactory.getLog(BackVisitMessageService.class);
	
	/**
	 * 查询所有的公司
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllBackVisit(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;		
		if (errList.isEmpty()) {		
			try {			
				dw = (DataWrap) DataAccessor.query("backVisit.queryAllBackVisit", context.contextMap,DataAccessor.RS_TYPE.PAGED);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--回访信息列表错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("cust_name", context.contextMap.get("cust_name"));
			outputMap.put("lease_code", context.contextMap.get("lease_code"));
			Output.jspOutput(outputMap, context,"/backVisit/visitManager.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 查询所有的合同设备
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllBackVisitNew(Context context) {		
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		Map rsMap = null;
		Map paramMap = new HashMap();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		if (errList.isEmpty()) {		
			try {			
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				dw = baseService.queryForListWithPaging("backVisit.queryAllBackVisitNew", context.contextMap,"LEASE_CODE");			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--回访设备管理页错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("visit_results", context.contextMap.get("visit_results"));
			outputMap.put("cust_name", context.contextMap.get("cust_name"));
			outputMap.put("lease_code", context.contextMap.get("lease_code"));
			outputMap.put("STATECON", context.contextMap.get("STATECON"));
			Output.jspOutput(outputMap, context,"/backVisit/visitManagerNew.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	
	/**
	 * 根据合同编号查询该合同下的产品
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAllContractByLeaseCode(Context context) {
		
			Map outputMap = new HashMap();
			List errList = context.errList;
			List dw=null;
			if (errList.isEmpty()) {		
				try {			
					dw = (List) DataAccessor.query("backVisit.queryAllContractByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.LIST);			
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add("回访管理--查询产品错误!请联系管理员");
				}
			}		
			if (errList.isEmpty()) {
				outputMap.put("dw", dw);
				Output.jspOutput(outputMap, context,"/backVisit/rentContractdetall.jsp"); 
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		
	}
	
	/**
	 * 根据合同编号查询该合同下的信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryVisitReviewRecordByLeaseCode(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (Map) DataAccessor.query("backVisit.queryVisitReviewRecordByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.MAP);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--查询信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/createVisit.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据合同编号查询该合同下的信息new
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryVisitReviewRecordByLeaseCodeNew(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map visitReview = null;
		if (errList.isEmpty()) {		
			try {
				visitReview = (Map) DataAccessor.query("backVisit.queryVisitReviewRecordByLeaseCodeNew", context.contextMap,DataAccessor.RS_TYPE.MAP);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--查询信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/createVisitNew.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	
	/**
	 * 根据合同编号新建一条访问记录
	 * @param context
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void createVisitManager(Context context) throws IOException
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List fileItems =(List)context.contextMap.get("uploadList");	
		List imageName = (List) context.contextMap.get("imagesName") ;
		Integer visitId = 0 ;
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		if(errList.isEmpty()) {
			try {			
				sqlMapClient.startTransaction();
				visitId = (Integer) sqlMapClient.insert("backVisit.createVisitManager", context.contextMap);
				context.contextMap.put("ANSWERPHONE_NAME", "000");
				context.contextMap.put("PHONE_NUMBER", "000");
				context.contextMap.put("VISITRECORD", "回访原因："+context.contextMap.get("STATECON_TEXT")+";回访结果："+context.contextMap.get("visit_results_text"));
				sqlMapClient.insert("backVisit.createDunRecord", context.contextMap);
				context.contextMap.put("visit_id", visitId) ;
				if(fileItems != null && fileItems.size() > 0){
					for (int i = 0 ;i < fileItems.size() ;i++ ) {
						FileItem fileItem = (FileItem) fileItems.get(i);
						InputStream in =fileItem.getInputStream();		
						if(!fileItem.getName().equals("")){
							saveFileToDisk(context,fileItem,sqlMapClient,imageName.get(i).toString());
						}
					}
				}
				sqlMapClient.commitTransaction();	
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--新建访问记录错误!请联系管理员");
			}finally {
				try {
					sqlMapClient.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}	
		}
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backVisit.queryAllBackVisit");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	
	/**
	 * 根据合同编号新建一条访问记录new
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createVisitManagerNew(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List fileItems =(List)context.contextMap.get("uploadList");	
		Integer visitId = 0 ;
		Map dunDetail=null;
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		if(errList.isEmpty()) {
			try {			
				sqlMapClient.startTransaction();
				dunDetail = (Map) DataAccessor.query("backVisit.queryDunDetailByRecpID", context.contextMap,DataAccessor.RS_TYPE.MAP);			
				if(dunDetail==null){
					context.contextMap.put("AL_PERIOD_NUM", "0");
					context.contextMap.put("DUN_DAY", "0");
					context.contextMap.put("SHOULD_PAYPRICE", "0");
					context.contextMap.put("PAY_DATE", "");
				}else{
					context.contextMap.put("AL_PERIOD_NUM", dunDetail.get("AL_PERIOD_NUM")==null?0:dunDetail.get("AL_PERIOD_NUM"));
					context.contextMap.put("DUN_DAY", dunDetail.get("DUN_DAY")==null?0:dunDetail.get("DUN_DAY"));
					context.contextMap.put("SHOULD_PAYPRICE", dunDetail.get("SHOULD_PAYPRICE")==null?0:dunDetail.get("SHOULD_PAYPRICE"));
					context.contextMap.put("PAY_DATE", dunDetail.get("PAY_DATE")==null?"":dunDetail.get("PAY_DATE"));
				}	
				visitId = (Integer) sqlMapClient.insert("backVisit.createVisitManagerNew", context.contextMap);

				//当回访新建或者正常回访时,需要插入到t_dun_dunningrecord表, mantis:0000345  add by ShenQi
				
				context.contextMap.put("ANSWERPHONE_NAME", "000");
				context.contextMap.put("PHONE_NUMBER", "000");
				context.contextMap.put("RESULT", 16);
				context.contextMap.put("CALL_CONTENT", "已回访");
				context.contextMap.put("CALL_DATE", context.contextMap.get("visitNewDate"));//((String)context.contextMap.get("backVisit")).split(",")[2].split("=")[1]
				context.contextMap.put("CUST_CODE", context.contextMap.get("CUST_CODE"));//((String)context.contextMap.get("backVisit")).split(",")[2].split("=")[1]
				
				sqlMapClient.insert("dunTask.addDunRecord", context.contextMap);
				context.contextMap.put("visit_id", visitId) ;
				if(fileItems != null && fileItems.size() > 0){
					FileItem fileItem = null;
					for (int i = 0 ;i < fileItems.size() ;i++ ) {
						fileItem = (FileItem) fileItems.get(i);
						logger.info("文件大小==========>>" + fileItem.getSize());
						if (fileItem.getSize() > 2097152) {
							throw new Exception("上传的文件太大。");
						}
					}
					for (int i = 0 ;i < fileItems.size() ;i++ ) {
						fileItem = (FileItem) fileItems.get(i);
						InputStream in =fileItem.getInputStream();		
						if(!fileItem.getName().equals("")){
							saveFileToDisk(context,fileItem,sqlMapClient,"回访图片"+i);
						}
					}
				}
				sqlMapClient.commitTransaction();	
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--新建访问记录错误[" + e.getMessage() + "]!请联系管理员");
			}finally {
				try {
					sqlMapClient.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}	
		}
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backVisit.queryAllBackVisitNew");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	
	/**
	 * 根据合同编号查询该合同下的回访信息(查看信息用)（不用了（单条访问记录））
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewVisitReviewRecordByLeaseCode(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (Map) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.MAP);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/viewVisit.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据合同编号查询该合同下的回访信息(查看信息用)（（多条访问记录））
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewListVisitReviewRecordByLeaseCode(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		List visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.LIST);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--查看回访信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/viewListVisitNew.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据合同编号查询该合同下的回访信息(查看信息用)（（多条访问记录））
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewListVisitReviewRecordByLeaseCodeNew(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		List visitReview = null;
		/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. Start*/
		List visitResultList = null;
		List imgList = null;
		Map<String, Object> imgParam = null;
		Map<String, Object> tempMap = null;
		/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. End*/
		
		if (errList.isEmpty()) {		
			try {			
				visitReview = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCodeNew", context.contextMap,DataAccessor.RS_TYPE.LIST);			
				/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. Start*/
				if (visitReview != null && visitReview.size() > 0) {
					visitResultList = new ArrayList();
					for (Object o : visitReview) {
						tempMap = (Map<String, Object>) o;
						if (tempMap.get("VISIT_ID") != null) {
							imgParam = new HashMap<String, Object>();
							imgParam.put("visit_id", tempMap.get("VISIT_ID"));
							imgList = (List) DataAccessor.query("backVisitImage.queryImgForViewVisit", imgParam, DataAccessor.RS_TYPE.LIST) ;
							tempMap.put("imgList", imgList);
						}
						visitResultList.add(tempMap);
					}
				}
				/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. End*/
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--查看回访信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. Start*/
			//outputMap.put("backVisit", visitReview);
			outputMap.put("backVisit", visitResultList);
			/*2011/12/9 Yang Yun Add show image for viewListVisitNew page. End*/
			Output.jspOutput(outputMap, context,"/backVisit/viewListVisitNew.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据合同编号查询该合同下的回访信息(编辑信息用(（多条访问记录）))
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void editListVisitReviewRecordByLeaseCode(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		List visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.LIST);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--编辑回访信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/editListVisit.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	
	/**
	 * 根据合同编号查询该合同下的回访信息(编辑信息用(（多条访问记录）))new
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void editListVisitReviewRecordByLeaseCodeNew(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		List visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCodeNew", context.contextMap,DataAccessor.RS_TYPE.LIST);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--编辑回访信息错误!请联系管理员");
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/editListVisitNew.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据合同编号查询该合同下的回访信息(编辑信息用(不用了（单条访问记录）))
	 * @param context
	 */
@SuppressWarnings("unchecked")
public void editVisitReviewRecordByLeaseCode(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map visitReview = null;
		if (errList.isEmpty()) {		
			try {			
				visitReview = (Map) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCode", context.contextMap,DataAccessor.RS_TYPE.MAP);			
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if (errList.isEmpty()) {
			outputMap.put("backVisit", visitReview);
			Output.jspOutput(outputMap, context,"/backVisit/editVisit.jsp"); 
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	
	}
	
	/**
	 * 根据访问记录ID修改一条访问记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateVisitManagerByVisit_Id(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {			
				DataAccessor.execute("backVisit.updateVisitByVisit_Id", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--修改访问记录错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backVisit.queryAllBackVisit");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	
	
	/**
	 * 根据访问记录ID修改一条访问记录new
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateVisitManagerByVisit_IdNew(Context context)
	{
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {
			try {			
				DataAccessor.execute("backVisit.updateVisitByVisit_IdNew", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("回访管理--修改访问记录错误!请联系管理员");
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=backVisit.queryAllBackVisitNew");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}	
	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,SqlMapClient sqlMapClient ,String title) throws Exception {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("backVisitImage");
		String file_path="";
		
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())  + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
				String excelNewName = FileExcelUpload.getNewFileName();
				File uploadedFile = new File(realPath.getPath() + File.separator + excelNewName + "." + type);
				file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName + "." + type;
				try {
					
					if (errList.isEmpty()) {
						fileItem.write(uploadedFile); 
						contextMap.put("path", file_path);
						contextMap.put("image_name", fileItem.getName());
						contextMap.put("title", title);
						sqlMapClient.insert("backVisitImage.createVistitImage", contextMap);							
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
					throw e ;
				}finally{
				  try{
					    fileItem.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
				
				fileItem.delete();
			}
		}
		return null;
	}
	/**
	 * 
	 * @return 读取upload-config.xml文件 获取保存根路径
	 */
	public String getUploadPath(String xmlPath) {
		String path = null;		
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
		    for(Iterator it=nodes.iterator();it.hasNext();){
		    	Element element = (Element) it.next();
		    	Element nameElement=element.element("name");
		    	String s = nameElement.getText();
		    	if(xmlPath.equals(s)){
		    		Element pathElement=element.element("path");
		    		path = pathElement.getText();
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} 	
		return path;
	}
	
	public static Map<String,Object> exportVisitReviewRecords (String QSTART_DATE,String QEND_DATE,String lease_code,String STATECON,String visit_results,String s_employeeId) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map context= new HashMap();
		List allVisitReviewrecords = null;
		Map paramMap=new HashMap();
		context.put("QSTART_DATE", QSTART_DATE);
		context.put("QEND_DATE", QEND_DATE);
		context.put("lease_code", lease_code);
		context.put("STATECON", STATECON);
		context.put("visit_results", visit_results);
		paramMap.put("id", s_employeeId);
		try {
			paramMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
			context.put("p_usernode", paramMap.get("NODE"));

			allVisitReviewrecords=(List) DataAccessor.query("backVisit.queryAllVisitReviewrecords",context, DataAccessor.RS_TYPE.LIST);

			resultMap.put("allVisitReviewrecords", allVisitReviewrecords);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return resultMap;		
	}
}
