package com.brick.contract.service;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.transaction.annotation.Transactional;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.brick.base.tag.LabelValueBean;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.contract.RentContractConstants;
import com.brick.invoice.service.InvoiceManageService;
import com.brick.invoice.to.InvoiceTO;
import com.brick.log.service.LogPrint;

/**
 * 合同模块中的资料操作
 * @author wuzhendong
 * @date 7 13, 2010
 * @version
 */
public class RentFile extends AService {

	Log logger = LogFactory.getLog(RentFile.class);
	
	private String path;
	private MailUtilService mailUtilService;
	private InvoiceManageService invoiceManageService;
	
	/**
	 * 查询资料
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentFile(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		List visitImage = null ;
		List infoTestList= null;
		List rentFileSenderState=null;
		List invoiceList = null;
		List invoiceTotal = null;
		boolean sendFile=true;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
				invoiceTotal=(List)DataAccessor.query("invoice.queryPage", context.contextMap, DataAccessor.RS_TYPE.LIST);
				invoiceList=(List)DataAccessor.query("rentFile.selectInvoice", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				// add by ZhangYizhou on 2014-06-27 Begin
				//查询合同资料历史数据
				List tempInsorupd = new ArrayList();
				for(Object item :insorupd) {
					Map rentFile = (Map)item;
					Map param = new HashMap();
					param.put("refd_id", rentFile.get("REFD_ID"));
					rentFile.put("LOG",(List)DataAccessor.query("rentFile.selectRentFileLog", param, DataAccessor.RS_TYPE.LIST));
					tempInsorupd.add(rentFile);
				}
				insorupd = tempInsorupd;
				// add by ZhangYizhou on 2014-06-27 End
				
				
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询担保人资料的信息
				visitImage=(List) DataAccessor.query("backVisitImage.queryVistitImageByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				infoTestList=getInfoTestList(context);
				
				//增加权限判断  是否有保存keyin资料的权限
				if("2".equals(context.contextMap.get("cardFlag"))){
					
					//List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
//						if("280".equals(resourceIdList.get(i))) {
//							sendFile=true;
//						}
//					}
					rentFileSenderState=(List)DataAccessor.query("rentFile.getRentFileSenderState", context.contextMap, DataAccessor.RS_TYPE.LIST);
				} 

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("CONTRACT_TYPEss", context.contextMap.get("CONTRACT_TYPE"));
			outputMap.put("visitImage", visitImage) ;
			outputMap.put("infoTestList", infoTestList) ;
			outputMap.put("rentFileSenderState",rentFileSenderState);
			outputMap.put("sendFile", sendFile);
			outputMap.put("invoices", invoiceList);
			if ( invoiceTotal!=null && invoiceTotal.size() == 1) {
				outputMap.put("invoiceTotal", invoiceTotal.get(0));
			}
			outputMap.put("TRFS_STATE",context.getContextMap().get("TRFS_STATE"));
			outputMap.put("RECT_STATUS",context.getContextMap().get("RECT_STATUS"));
			outputMap.put("ALLOW_CHANGE_FILETYPE", RentContractConstants.ALLOW_CHANGE_FILETYPE);
			//System.out.println("context.contextMap.get()====="+context.contextMap.get("CONTRACT_TYPE"));
			if("true".equals(context.contextMap.get("forShow"))){
				Output.jspOutput(outputMap, context, "/rentcontract/rentFileForShow.jsp");
			} else {
				Output.jspOutput(outputMap, context, "/rentcontract/rentFile.jsp");
			}
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}

	/**
	 * 查询资料(补件)
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryRentFileForLoss(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		List visitImage = null ;
		List infoTestList= null;
		List rentFileSenderState=null;
		List invoiceList = null;
		List invoiceTotal = null;
		boolean sendFile=true;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
				invoiceTotal=(List)DataAccessor.query("invoice.queryPage", context.contextMap, DataAccessor.RS_TYPE.LIST);
				invoiceList=(List)DataAccessor.query("rentFile.selectInvoice", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				// add by ZhangYizhou on 2014-06-27 Begin
				List tempInsorupd = new ArrayList();
				for(Object item :insorupd) {
					Map rentFile = (Map)item;
					Map param = new HashMap();
					param.put("refd_id", rentFile.get("REFD_ID"));
					rentFile.put("LOG",(List)DataAccessor.query("rentFile.selectRentFileLog", param, DataAccessor.RS_TYPE.LIST));
					tempInsorupd.add(rentFile);
				}
				insorupd = tempInsorupd;
				// add by ZhangYizhou on 2014-06-27 End
				
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询担保人资料的信息
				visitImage=(List) DataAccessor.query("backVisitImage.queryVistitImageByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				infoTestList=getInfoTestList(context);
				
				//增加权限判断  是否有保存keyin资料的权限
				if("2".equals(context.contextMap.get("cardFlag"))){
					
					//List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
//						if("280".equals(resourceIdList.get(i))) {
//							sendFile=true;
//						}
//					}
					rentFileSenderState=(List)DataAccessor.query("rentFile.getRentFileSenderState", context.contextMap, DataAccessor.RS_TYPE.LIST);
				} 

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("CONTRACT_TYPEss", context.contextMap.get("CONTRACT_TYPE"));
			outputMap.put("visitImage", visitImage) ;
			outputMap.put("infoTestList", infoTestList) ;
			outputMap.put("rentFileSenderState",rentFileSenderState);
			outputMap.put("sendFile", sendFile);
			outputMap.put("invoices", invoiceList);
			if ( invoiceTotal!=null && invoiceTotal.size() == 1) {
				outputMap.put("invoiceTotal", invoiceTotal.get(0));
			}
			outputMap.put("ALLOW_CHANGE_FILETYPE", RentContractConstants.ALLOW_CHANGE_FILETYPE);
			//System.out.println("context.contextMap.get()====="+context.contextMap.get("CONTRACT_TYPE"));
			if("true".equals(context.contextMap.get("forShow"))){
				Output.jspOutput(outputMap, context, "/rentcontract/rentFileForShow.jsp");
			} else {
				Output.jspOutput(outputMap, context, "/rentcontract/rentFileForLossFile.jsp");
			}
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	
	public void queryRentFileForUpload(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List insorupd =new ArrayList();
		Map infor = new HashMap();
		List visitImage = null ;
		List infoTestList= null;
		List rentFileSenderState=null;
		boolean sendFile=true;
			try {
				insorupd=(List)DataAccessor.query("rentFile.selectRentFile", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询承租人资料和合同资料的信息
				infor=(Map)DataAccessor.query("rentFile.selectInfor", context.contextMap, DataAccessor.RS_TYPE.MAP);
				//查询担保人资料的信息
				visitImage=(List) DataAccessor.query("backVisitImage.queryVistitImageByPrcdId", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				infoTestList=getInfoTestList(context);
				
				//增加权限判断  是否有保存keyin资料的权限
				if("2".equals(context.contextMap.get("cardFlag"))){
					
					//List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
//					for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
//						if("280".equals(resourceIdList.get(i))) {
//							sendFile=true;
//						}
//					}
					rentFileSenderState=(List)DataAccessor.query("rentFile.getRentFileSenderState", context.contextMap, DataAccessor.RS_TYPE.LIST);
				} 

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("insorupd",insorupd);
			outputMap.put("infor",infor);
			outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
			outputMap.put("cardFlag", context.contextMap.get("cardFlag"));
			outputMap.put("CONTRACT_TYPEss", context.contextMap.get("CONTRACT_TYPE"));
			outputMap.put("visitImage", visitImage) ;
			outputMap.put("infoTestList", infoTestList) ;
			outputMap.put("rentFileSenderState",rentFileSenderState);
			outputMap.put("sendFile", sendFile);
			Output.jspOutput(outputMap, context, "/rentcontract/rentFileForUpload.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}
	/**
	 * 保存附件和文件份数
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void uploadAll(Context context) throws Exception {
		Map outputMap = new HashMap();
		
		List fileItems =(List)context.contextMap.get("uploadList");	
		try {	
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==0){
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthA")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idA"+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idA"+String.valueOf(i)));
					context.contextMap.put("file_count2",context.contextMap.get("FILE_COUNTA"+String.valueOf(i)));
					context.contextMap.put("copyfile_count2",context.contextMap.get("COPYFILE_COUNTA"+String.valueOf(i)));
					context.contextMap.put("memo2",context.contextMap.get("MEMOA"+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));			
					if (Integer.parseInt(context.contextMap.get("refd_idA"+String.valueOf(i)).toString())==0) {
						DataAccessor.execute("rentFile.insertReft", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
					} else {
						DataAccessor.execute("rentFile.updateReft", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					}								
				}				
			}
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==1){
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthB")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("file_count2",context.contextMap.get("FILE_COUNTB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("copyfile_count2",context.contextMap.get("COPYFILE_COUNTB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("memo2",context.contextMap.get("MEMOB"+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));			
					if (Integer.parseInt(context.contextMap.get("refd_idB"+String.valueOf(i)+String.valueOf(i)).toString())==0) {
						DataAccessor.execute("rentFile.insertReft", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
					} else {
						DataAccessor.execute("rentFile.updateReft", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
					}								
				}				
			}
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==2){
				context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));	
				JSONArray fileDataList = JSONArray.fromObject(context.contextMap.get("fileData"));
				String strLogString="";
				for (int i = 0; i < fileDataList.size(); i++) {
					JSONObject file = fileDataList.getJSONObject(i);
					Map param = new HashMap(); 
					for (Object key : file.keySet()) {
						param.put(key, file.get(key));
					}
					param.put("REASON", param.get("ISSURE_REASON"));
					param.put("s_employeeId", context.getContextMap().get("s_employeeId"));
					
					int refd_id = Integer.parseInt(param.get("refd_id2").toString());
					
					if(refd_id==0) {
						List<Map> list = (List<Map>)DataAccessor.query("rentFile.existsFiledetail", param, RS_TYPE.LIST);
						if (list != null && list.size() == 1) {
							refd_id = Integer.parseInt(list.get(0).get("REFD_ID").toString());
							param.put("refd_id2", refd_id);
						}
					}
					
					if(refd_id==0) {
						if ("".equals(param.get("ISSURE_REASON"))){
							strLogString+="新增进件：文件名："+param.get("FILE_NAME")+"<br>";
						}else{
							strLogString+="新增缺件：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
						}
						DataAccessor.execute("rentFile.insertFiledetail", param, DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentFile.insertFiledetailFirst", param, DataAccessor.OPERATION_TYPE.INSERT);
						if (param.get("REASON")!=null && !param.get("REASON").toString().isEmpty()) {
							DataAccessor.execute("rentFile.insertRentFileLossReason", param, DataAccessor.OPERATION_TYPE.INSERT);
							DataAccessor.execute("rentFile.insertRentFirstFileLossReason", param, DataAccessor.OPERATION_TYPE.INSERT);
						}
					} else {
						Map rentFileDetailMap=null;
						rentFileDetailMap= (Map<String, Object>) DataAccessor.query("rentFile.getRentFileDetailByRefdID", param, DataAccessor.RS_TYPE.MAP);
						if (rentFileDetailMap!=null){
							if("".equals(param.get("ISSURE_REASON"))&&!"".equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="新增进件：文件名："+param.get("FILE_NAME")+"<br>";
							}else if(!"".equals(param.get("ISSURE_REASON"))&&"".equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="新增缺件：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
							}else if(!"".equals(param.get("ISSURE_REASON"))&&!"".equals(rentFileDetailMap.get("ISSURE_REASON"))&&!param.get("ISSURE_REASON").equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="缺件问题类别变更：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
							}
						}
						DataAccessor.execute("rentFile.updateFiledetail", param,DataAccessor.OPERATION_TYPE.UPDATE);
						if (param.get("REASON")!=null && !param.get("REASON").toString().isEmpty()) {
							DataAccessor.execute("rentFile.insertRentFileLossReason", param,DataAccessor.OPERATION_TYPE.INSERT);
						}
					}	
				}
				if(context.getContextMap().get("invoiceData") != null && !"".equals(context.getContextMap().get("invoiceData"))) {
					JSONArray invoiceDataList = JSONArray.fromObject(context.contextMap.get("invoiceData"));
					for (int i = 0; i < invoiceDataList.size(); i++) {
						InvoiceTO invoceTo = (InvoiceTO)JSONObject.toBean(invoiceDataList.getJSONObject(i), InvoiceTO.class);
						invoceTo.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
						//业管收件或借出后
						if (RentContractConstants.ALLOW_CHANGE_FILETYPE && (new Integer(2).equals(invoceTo.getStatus()) || new Integer(1).equals(invoceTo.getStatus())))  {
							invoceTo.setInvoice_type(1);
							invoiceManageService.update("invoice.updateInvoiceType", invoceTo);
						}
						invoiceManageService.update("invoice.updateInvoiceStatus", invoceTo);
						context.contextMap.put("invoice_id", invoceTo.getInvoice_id());
						invoiceManageService.updateRemanentMoney(invoceTo,false);
					}
				}
				
				if (!"".equals(strLogString)){
			    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("prcd_id2")),DataUtil.longUtil(0),
				   		 "合同文件操作",
			   		 	 "待补文件操作",
			   		 	 null,
			   		 	 strLogString,
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
				}
			}			
		}  catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			throw e;
		}		
		
		if (fileItems != null) {
			for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
				FileItem fileItem = (FileItem) iterator.next();
				InputStream in =fileItem.getInputStream();		
				if(!fileItem.getName().equals("")){
					SqlMapClient sqlMapClient = DataAccessor.getSession();
					try {
						sqlMapClient.startTransaction();
						saveFileToDisk(context,fileItem,sqlMapClient);
						sqlMapClient.commitTransaction();		
					} catch (SQLException e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					} finally {
						try {
							sqlMapClient.endTransaction();
						} catch (SQLException e) {
							e.printStackTrace();
							LogPrint.getLogStackTrace(e, logger);
						}
					}				
				}
			}
		}
		outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));	

		if("true".equals(context.getContextMap().get("FOR_LOSS"))) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForLossFile");
		} else {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
		}
		

	}	
	
	
	public void uploadRentContractFile(Context context) throws Exception{
		
		List fileItems =(List)context.contextMap.get("uploadList");	
		for (Iterator iterator = fileItems.iterator(); iterator.hasNext();) {
			FileItem fileItem = (FileItem) iterator.next();
			InputStream in =fileItem.getInputStream();		
			if(!fileItem.getName().equals("")){
				SqlMapClient sqlMapClient = DataAccessor.getSession();
				try {
					sqlMapClient.startTransaction();
					saveFileToDisk(context,fileItem,sqlMapClient);
					sqlMapClient.commitTransaction();		
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				} finally {
					try {
						sqlMapClient.endTransaction();
					} catch (SQLException e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}				
			}
		}
		Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForShow");
	}
	public void uploadAllForHW(Context context) throws IOException {
		Map outputMap = new HashMap();
		try {
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==2){
				DataAccessor.getSession().startTransaction();
				context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));
				
				if(context.getContextMap().get("invoiceData") != null && !"".equals(context.getContextMap().get("invoiceData"))) {
					JSONArray invoiceDataList = JSONArray.fromObject(context.contextMap.get("invoiceData"));
					for (int i = 0; i < invoiceDataList.size(); i++) {
						InvoiceTO invoceTo = (InvoiceTO)JSONObject.toBean(invoiceDataList.getJSONObject(i), InvoiceTO.class);
						invoceTo.setModify_by(String.valueOf(context.contextMap.get("s_employeeId")));
						//业管收件或借出后
						if (RentContractConstants.ALLOW_CHANGE_FILETYPE && (new Integer(2).equals(invoceTo.getStatus()) || new Integer(1).equals(invoceTo.getStatus())))  {
							invoceTo.setInvoice_type(1);
							invoiceManageService.update("invoice.updateInvoiceType", invoceTo);
						}
						invoiceManageService.update("invoice.updateInvoiceStatus", invoceTo);
						context.contextMap.put("invoice_id", invoceTo.getInvoice_id());
						invoiceManageService.updateRemanentMoney(invoceTo,false);
					}
				}
				
				JSONArray fileDataList = JSONArray.fromObject(context.contextMap.get("fileData"));
				String strLogString="";
				for (int i = 0; i < fileDataList.size(); i++) {
					JSONObject file = fileDataList.getJSONObject(i);
					Map param = new HashMap(); 
					for (Object key : file.keySet()) {
						param.put(key, file.get(key));
					}
					param.put("REASON", param.get("ISSURE_REASON"));
					param.put("s_employeeId", context.getContextMap().get("s_employeeId"));
					
					int refd_id = Integer.parseInt(param.get("refd_id2").toString());
					
					if(refd_id==0) {
						List<Map> list = (List<Map>)DataAccessor.query("rentFile.existsFiledetail", param, RS_TYPE.LIST);
						if (list != null && list.size() == 1) {
							refd_id = Integer.parseInt(list.get(0).get("REFD_ID").toString());
							param.put("refd_id2", refd_id);
						}
					}
					
					if(refd_id==0) {
						if ("".equals(param.get("ISSURE_REASON"))){
							strLogString+="新增进件：文件名："+param.get("FILE_NAME")+"<br>";
						}else{
							strLogString+="新增缺件：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
						}
						DataAccessor.execute("rentFile.insertFiledetail", param,DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentFile.insertFiledetailFirst", param,DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentFile.insertFiledetailHW", param,DataAccessor.OPERATION_TYPE.INSERT);
						if (param.get("REASON")!=null && !param.get("REASON").toString().isEmpty()) {
							DataAccessor.execute("rentFile.insertRentFileLossReason", param, DataAccessor.OPERATION_TYPE.INSERT);
							DataAccessor.execute("rentFile.insertRentFirstFileLossReason", param, DataAccessor.OPERATION_TYPE.INSERT);
						}
					} else {
						Map rentFileDetailMap=null;
						rentFileDetailMap= (Map<String, Object>) DataAccessor.query("rentFile.getRentFileDetailByRefdID", param, DataAccessor.RS_TYPE.MAP);
						if (rentFileDetailMap!=null){
							if("".equals(param.get("ISSURE_REASON"))&&!"".equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="新增进件：文件名："+param.get("FILE_NAME")+"<br>";
							}else if(!"".equals(param.get("ISSURE_REASON"))&&"".equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="新增缺件：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
							}else if(!"".equals(param.get("ISSURE_REASON"))&&!"".equals(rentFileDetailMap.get("ISSURE_REASON"))&&!param.get("ISSURE_REASON").equals(rentFileDetailMap.get("ISSURE_REASON"))){
								strLogString+="缺件问题类别变更：文件名："+param.get("FILE_NAME")+";原因："+param.get("ISSURE_REASON")+";备注："+param.get("memo2")+"<br>";
							}
						}
						DataAccessor.execute("rentFile.updateFiledetail", param,DataAccessor.OPERATION_TYPE.UPDATE);
						DataAccessor.execute("rentFile.insertFiledetailHW", param,DataAccessor.OPERATION_TYPE.INSERT);
						if (param.get("REASON")!=null && !param.get("REASON").toString().isEmpty()) {
							DataAccessor.execute("rentFile.insertRentFileLossReason", param,DataAccessor.OPERATION_TYPE.INSERT);
						}
					}
					
				}
				
				if (!"".equals(context.contextMap.get("HW_MEMO")) &&  null!=context.contextMap.get("HW_MEMO")){
					DataAccessor.execute("rentContract.updateRentFileStateHWMemo", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				}
				
				DataAccessor.getSession().commitTransaction();
				
				if (!"".equals(context.contextMap.get("HW_MEMO")) &&  null!=context.contextMap.get("HW_MEMO")){
			    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("prcd_id2")),DataUtil.longUtil(0),
					   		 "业管合同文件初审",
				   		 	 "业管合同文件初审",
				   		 	 null,
				   		 	"业管合同文件初审备注："+context.contextMap.get("HW_MEMO"),
				   		 	 1,
				   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
				   		 	 DataUtil.longUtil(0),
				   		 	 context.getRequest().getRemoteAddr());
				}
				if (!"".equals(strLogString)){
			    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("prcd_id2")),DataUtil.longUtil(0),
					   		 "合同文件操作",
				   		 	 "待补文件操作",
				   		 	 null,
				   		 	 strLogString,
				   		 	 1,
				   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
				   		 	 DataUtil.longUtil(0),
				   		 	 context.getRequest().getRemoteAddr());
				}
		} }
			catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}		

		outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));	
		Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentFileSenderFollow");

	}	
	
	/**
	 * 业务行政专员keyin 检核表
	 * @param context
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void uploadAllForSales(Context context) throws IOException {
		Map outputMap = new HashMap();
		
		try {	
			if(DataUtil.intUtil(context.contextMap.get("cardFlag"))==2){
				DataAccessor.getSession().startTransaction();
				boolean update_flag=true;
				String strLogString="";

				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthC")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("COPYSIGN_COUNT2",context.contextMap.get("COPYSIGN_COUNTC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("SALES_MEMO",StringUtils.autoInsertWrap(context.contextMap.get("SALES_MEMOC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i))==null?"":String.valueOf(context.contextMap.get("SALES_MEMOC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i))),15));
					context.contextMap.put("FILE_NAME", context.contextMap.get("file_nameC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));	
					context.contextMap.put("FILE_TYPE", DataUtil.intUtil(context.contextMap.get("cardFlag"))+1);
					if (Integer.parseInt(context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)).toString())==0) {
						
						DataAccessor.execute("rentFile.insertReftBySales", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
						strLogString="新增案件检核表数据";
					} else {
						
						DataAccessor.execute("rentFile.updateReftBySales", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						strLogString="更新案件检核表数据";
						update_flag=false;
					}								
				}
//				if(update_flag){
//					DataAccessor.execute("rentFile.insertRentFileSenderState", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
//				}else if(update_flag==false){
//					DataAccessor.execute("rentFile.updateRentFileSenderState", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
//				}
				
				DataAccessor.getSession().commitTransaction();
				
				if (!"".equals(strLogString)){
			    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("prcd_id2")),DataUtil.longUtil(0),
					   		 "合同文件操作",
				   		 	 "进件操作",
				   		 	 null,
				   		 	 strLogString,
				   		 	 1,
				   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
				   		 	 DataUtil.longUtil(0),
				   		 	 context.getRequest().getRemoteAddr());
				}
			}			
		}  catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}		
		
		outputMap.put("prcd_id", context.contextMap.get("prcd_id"));
		outputMap.put("cardFlag", context.contextMap.get("cardFlag"));	
		Output.jspSendRedirect(context,"defaultDispatcher?__action=backMoney.queryRentContractBackMoney");	
	}	
	
	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("rentFile");
		String file_path="";
		String file_name="";
		Long syupId = null;
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
				String excelNewName = FileExcelUpload.getNewFileName();
				File uploadedFile = new File(realPath.getPath() + File.separator + excelNewName + "." + type);
				file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName + "." + type;
				file_name = excelNewName + "." + type;
				try {
					if (errList.isEmpty()) {
						fileItem.write(uploadedFile); 
						contextMap.put("file_path", file_path);
						contextMap.put("file_name", fileItem.getName());
						contextMap.put("title", "上传的资料文件的附件");
						//判断是修改还是添加	
						String name = fileItem.getFieldName();
						String[] nameAll=name.split("@");
						context.contextMap.put("refd_id2", nameAll[0]);
						context.contextMap.put("refi_id2", nameAll[1]);
						context.contextMap.put("prcd_id2", context.contextMap.get("prcd_id"));	
						syupId = (Long) sqlMapClient.insert("rentFile.insertFileForUp", contextMap);							

					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}finally{
				  try{
					 // fileItem.getOutputStream().flush();
					 // fileItem.getOutputStream().close();
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
	
	/**
	 * 
	 * @return 下载
	 */
	public void download(Context context) {
		String savaPath  = (String) context.contextMap.get("path");
		String name  = (String) context.contextMap.get("name");
		String bootPath = null ;
		//取得地址
		if(context.contextMap.get("bootPath")!=null || !context.contextMap.get("bootPath").equals("")){
			bootPath = this.getUploadPath(context.contextMap.get("bootPath").toString());
		}else {
			bootPath = this.getUploadPath("rentFile");
		}
		String path=bootPath +  savaPath;
		File file = new File(path);
		context.response.reset();
		OutputStream output = null;
//		FileInputStream fis = null;
		InputStream fis = null;
		try {
//			context.response.setContentType("bin");
//			context.response.setHeader("Content-Disposition", "attachment; filename=" + new String(name.getBytes("utf-8"), "iso8859-1"));
//			
//			output = context.response.getOutputStream();
//			fis = new FileInputStream(file);
//
//			byte[] b = new byte[1024];
//			int i = 0;
//
//			while ((i = fis.read(b)) != -1) {
//
//				output.write(b, 0, i);
//			}
//			output.write(b, 0, b.length);
//
//			output.flush();
//			context.response.flushBuffer();
			
			
			fis = new FileInputStream(file);
	        context.response.reset();
	        context.response.setContentType("bin");
	        context.response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(name.getBytes("utf-8"), "iso8859-1") + "\"");
	        output = context.response.getOutputStream();
	        byte[] b = new byte[100];
	        int len;
            while ((len = fis.read(b)) > 0)
            	output.write(b, 0, len);
            fis.close();
		
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				output = null;
			}
		}
	}
	
	/**
	 * 添加提醒日志
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id=0;
		try {	
				String fileValue=(String) context.contextMap.get("ids1");
				String[] ids=fileValue.split("@");
				
				context.contextMap.put("logTime", context.contextMap.get("logTime1"));
				context.contextMap.put("logMemo", context.contextMap.get("logMemo1"));
				context.contextMap.put("prcd_id", context.contextMap.get("prcd_idLog1"));				
				trfl_id=(Long)DataAccessor.execute("rentFile.insertLog", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("id", ids[i]);
					context.contextMap.put("trfl_id", trfl_id);
					DataAccessor.execute("rentFile.insertFile2Log", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);				
				}		
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);
		if("readOnly".equals(context.contextMap.get("logFlag"))) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForShow");
		} else {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
		}
	}
	
	
	/**
	 * 添加提醒日志
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog2(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id=0;
		try {	
				String fileValue=(String) context.contextMap.get("ids2");
				String[] ids=fileValue.split("@");

				context.contextMap.put("logTime", context.contextMap.get("logTime2"));
				context.contextMap.put("logMemo", context.contextMap.get("logMemo2"));
				context.contextMap.put("prcd_id", context.contextMap.get("prcd_idLog2"));
				trfl_id=(Long)DataAccessor.execute("rentFile.insertLog", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);	
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("id", ids[i]);
					context.contextMap.put("trfl_id", trfl_id);
					DataAccessor.execute("rentFile.insertFile2Log", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);				
				}		
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);	
		if("readOnly".equals(context.contextMap.get("logFlag"))) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForShow");
		} else {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
		}
	}
	/**
	 * 添加提醒日志
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void insertLog3(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		long trfl_id=0;
		try {	
				String fileValue=(String) context.contextMap.get("ids3");
				String[] ids=fileValue.split("@");

				context.contextMap.put("logTime", context.contextMap.get("logTime3"));
				context.contextMap.put("logMemo", context.contextMap.get("logMemo3"));
				context.contextMap.put("prcd_id", context.contextMap.get("prcd_idLog3"));
				trfl_id=(Long)DataAccessor.execute("rentFile.insertLog", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);	
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("id", ids[i]);
					context.contextMap.put("trfl_id", trfl_id);
					DataAccessor.execute("rentFile.insertFile2Log", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);				
				}		
		} catch (Exception e) {
			errList.add("添加提醒日志失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);
		if("readOnly".equals(context.contextMap.get("logFlag"))) {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContractForShow");
		} else {
			Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentContract");
		}
	}
	
	/**
	 * 查询下载文件
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryFileUpMore(Context context) {	
		Map outputMap = new HashMap();
		List errList = context.errList;		
		List logFileUpList =null;
		Boolean deleteRentFile=false;
			try {
				logFileUpList=(List)DataAccessor.query("rentFile.selectFileUpMore", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
				//Add by Michael 2012 5-16  增加合同资料删除权限的管控
				
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("214".equals(resourceIdList.get(i))) {
						deleteRentFile=true;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}	
		if (errList.isEmpty()) {
			outputMap.put("logFileUpList",logFileUpList);
			outputMap.put("id",context.contextMap.get("id"));
			outputMap.put("REFI_ID",context.contextMap.get("REFI_ID"));
			outputMap.put("PRCD_ID",context.contextMap.get("PRCD_ID"));
			if("true".equals(context.getContextMap().get("DELETE_ENABLE"))) {
				outputMap.put("DELETE_ENABLE", true);
			} else {
				outputMap.put("DELETE_ENABLE", false);
			}
			//Add by Michael 2012 5-16  增加合同资料删除权限的管控
			outputMap.put("deleteRentFile",deleteRentFile);
			Output.jspOutput(outputMap, context, "/rentcontract/fileUpMore.jsp");
		} else {
			Output.jspOutput(outputMap, context, "/sys/acl/login.jsp");
		}	
	}
	
	/**
	 * 删除下载文件
	 * pact page
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void invalidRentFileUp(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		try {	
				DataAccessor.execute("rentFile.invalidRentFileUp", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);				
		} catch (Exception e) {
			errList.add("删除文件失败！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("errList", errList);		
		Output.jsonOutput(outputMap, context);
	}	
	
	
	//查询资料细项的问题类别
	public List getInfoTestList(Context context)
	{
		List infoTestList = new ArrayList();
		List errList = context.errList;
		try {	
			List<Map> resourceIdList=(List<Map>) DataAccessor.query("rentFile.getRentFileLossReason", context.contextMap, DataAccessor.RS_TYPE.LIST);				
			int i=0;
			for (Map map : resourceIdList) {
				LabelValueBean labelValueBean=new LabelValueBean();
				labelValueBean.setLabel(String.valueOf(map.get("REASON")));
				labelValueBean.setValue(i);
				infoTestList.add(labelValueBean);
				i++;
			}
		} catch (Exception e) {
			errList.add("查询案件文件不齐原因！");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		return infoTestList;
	}
	
	//Add by Michael 2012 08-14
	//在初审前查看合同资料是否有添加
	public void getRentContractFileList(Context context)
	{
		Map outputMap = new HashMap();
		List rentContractFileList = null;
		try {
			rentContractFileList=(List)DataAccessor.query("rentFile.getRentContractFileList", context.contextMap,DataAccessor.RS_TYPE.LIST);
	
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("rentContractFileList", rentContractFileList);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 业管行政专员申请结清结清文件
	 * @param context
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void uploadRentSettleFileApply(Context context) throws IOException {
		Map outputMap = new HashMap();
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		try {	
			sqlMapClient.startTransaction();
				for (int i = 1; i <=DataUtil.intUtil(context.contextMap.get("statusLengthC")); i++) {
					context.contextMap.put("refd_id2",context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("refi_id2",context.contextMap.get("refi_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("COPYSIGN_COUNT2",context.contextMap.get("COPYSIGN_COUNTC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					context.contextMap.put("MEMO",StringUtils.autoInsertWrap(context.contextMap.get("MEMOC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i))==null?"":String.valueOf(context.contextMap.get("MEMOC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i))),15));
					context.contextMap.put("FILE_NAME", context.contextMap.get("file_nameC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)));
					
					if ("".equals(context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)).toString()) || context.contextMap.get("refd_idC"+String.valueOf(i)+String.valueOf(i)+String.valueOf(i)).toString()==null) {
						sqlMapClient.insert("rentFile.insertRentSettleFileDetail", context.contextMap);
					} else {
						sqlMapClient.update("rentFile.updateRentSettleFileDetail", context.contextMap);
					}								
				}
				
				//保存结清文件申请明细
				if ("".equals(context.contextMap.get("RENT_SETTLEFILE_ID").toString()) || context.contextMap.get("RENT_SETTLEFILE_ID").toString()==null) {
					sqlMapClient.insert("rentFile.insertRentSettleDetail", context.contextMap);
				} else {
					sqlMapClient.update("rentFile.updateRentSettleDetail", context.contextMap);
				}	
				
				sqlMapClient.commitTransaction();
				
		}  catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}		
		
		outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
		Output.jspSendRedirect(context,"defaultDispatcher?__action=rentContract.queryRentSettleFileSend");	
	}	
	
	
	public void exportRentFileForCar(Context context) throws Exception{
		
		Map param = new HashMap();
		String date = (String) context.contextMap.get("date");
		ReportDateTo reportDate = null;
		if(date!=null){
			String[] dateArray = date.split("-");
			int year =  Integer.parseInt(dateArray[0]);
			int month = Integer.parseInt(dateArray[1]);
			reportDate = ReportDateUtil.getDateByYearAndMonth(year, month);
		}else{
			Calendar c = Calendar.getInstance();
			reportDate = ReportDateUtil.getDateByYearAndMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1);
			date = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1);
		}
		param.put("startDate", reportDate.getBeginTime());
		param.put("endDate", reportDate.getEndTime());
		param.put("companyCode", context.contextMap.get("companyCode"));
		List creditIDs = (List)DataAccessor.query("rentFile.getNewCreditId", param,DataAccessor.RS_TYPE.LIST);
		if(creditIDs!=null){
	   	    context.response.setContentType("application/zip");
	   	    context.response.setCharacterEncoding("GBK");	   	    
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String("税务备案资料.zip".getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();

	   	    ZipOutputStream  zop = new ZipOutputStream(context.response.getOutputStream());
			zop.setEncoding("gbk");
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			
			WritableWorkbook wb = Workbook.createWorkbook(os);
			WritableSheet sheet = wb.createSheet("小车委贷导出备案资料明细表", 0);
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			sheet.setColumnView(0, 5);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 20);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 40);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);
			sheet.setColumnView(9, 20);
			sheet.setColumnView(10, 20);
			
			sheet.mergeCells(0, 0, 10, 0);
			Label label = new Label(0,0,"备案资料回传情况表");
			label.setCellFormat(format);
			sheet.addCell(label);
			int col = 0;
			label = new Label(col,1,"序号");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"合同号");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"第一期支付日");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"最后一期支付日");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"客户名称");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			
			label = new Label(col,1,"身份证号");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"身份证");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			
			label = new Label(col,1,"合同");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"财务规划服务协议书");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"区域办事处");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1,"业务员");
			label.setCellFormat(format);
			sheet.addCell(label);
			int index = 1;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			for(int i=0,len=creditIDs.size();i<len;i++){
				Integer creditId  = (Integer) creditIDs.get(i);
				if(creditId != null){
					param.put("creditId", creditId);				
					List results = (List)DataAccessor.query("rentFile.getCarContractFile", param,DataAccessor.RS_TYPE.LIST);
					if(results==null || results.size()==0){//身份证和合同多不存在
						String leaseCode = LeaseUtil.getLeaseCodeByCreditId(String.valueOf(creditId));
						String custName = LeaseUtil.getCustNameByCreditId(String.valueOf(creditId));
						String userId = LeaseUtil.getSensorIdByCreditId(String.valueOf(creditId));
						String userName = LeaseUtil.getUserNameByUserId(userId);
						String deptName = LeaseUtil.getDecpNameByUserId(userId);
						String idcard =  LeaseUtil.getNatuIdCardByCustId(LeaseUtil.getCustIdByCreditId(String.valueOf(creditId)));
						col = 0;
						
						label = new Label(col,1 + index,String.valueOf(index));
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,leaseCode);
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						Date firstDate = LeaseUtil.getFirstPeriodPayDateByCreditId(String.valueOf(creditId));
						label = new Label(col,1 + index,firstDate!=null?df.format(firstDate):"");
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						Date lastDate = LeaseUtil.getLastPeriodPayDateByCreditId(String.valueOf(creditId));
						label = new Label(col,1 + index,lastDate!=null?df.format(lastDate):"");
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,custName);
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,idcard);
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						
						label = new Label(col,1 + index,String.valueOf(0));
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,String.valueOf(0));
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,String.valueOf(0));
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,deptName);
						label.setCellFormat(format2);
						sheet.addCell(label);
						col++;
						
						label = new Label(col,1 + index,userName);
						label.setCellFormat(format2);
						sheet.addCell(label);
						index ++;
					}
					//导出PDF
					index = exportCarContractFile(results,zop,sheet,format2,index,String.valueOf(creditId));
				}


			}
			
			wb.write();
			wb.close();

			byte []  b = os.toByteArray();
			os.close();
			ZipEntry ze = new ZipEntry("备案资料回传情况表.xls");
		    ze.setSize(b.length);
            ze.setTime(new Date().getTime());	               
            zop.putNextEntry(ze);
            zop.write(b,0,b.length);          
			zop.close();
		}
	}
	private  String PATH ="\\\\"+LeaseUtil.getIPAddress()+"\\home\\filsoft\\financelease\\upload\\rentFile";
	
	private int exportCarContractFile(List<Map> results,ZipOutputStream zop,WritableSheet sheet,WritableCellFormat format,int index,String creditId) throws IOException, RowsExceededException, WriteException, SQLException{
		
		WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		font3.setColour(Colour.RED);
		WritableCellFormat format3 = new WritableCellFormat(font3);
		format3.setAlignment(Alignment.CENTRE);
		format3.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format3.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format3.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format3.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format3.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format3.setWrap(true);
		Map<String,String> filepaths  = new HashMap<String,String>();
		if(results!=null && results.size()>0){
			int cardCount = 0;
			int contractCount = 0 ;
			int serviceBookCount = 0;
			String leaseCode = "";
			String custName ="";
			String deptName ="";
			String userName ="";
			String idcard =  LeaseUtil.getNatuIdCardByCustId(LeaseUtil.getCustIdByCreditId(creditId));
			
			for(int i=0,len=results.size();i<len;i++){
				Map file  = results.get(i);
				String fileName= (String) file.get("FILE_FILENAME");
				custName = (String) file.get("CUST_NAME");
				leaseCode = (String) file.get("LEASE_CODE");
				deptName  = (String) file.get("DECP_NAME_CN");
				userName = (String) file.get("NAME");
				String filePath = (String) file.get("PATH");
				String rentFileName = (String) file.get("FILE_NAME");
				String path = PATH + filePath ;
				
//				File f = new File(path);
//				FileInputStream in = new FileInputStream(f);
				
				String fname = "";
				
				if("车主身份证复印件".equals(rentFileName)||"客户照片".equals(rentFileName)||"营业执照、营业执照副本(含年审章)".equals(rentFileName)){
					if(cardCount>0){
						continue;
					}
					//fname =  "车主身份证复印件/" +leaseCode+"("+custName+")"+fileName.substring(fileName.lastIndexOf(".")) ;	
					filepaths.put("f1",path);
					cardCount++;
					
				}else if("个人委托贷款借款合同".equals(rentFileName)){
					if(contractCount>0){
						continue;
					}
					//fname =  "乘用车合同资料/" +leaseCode+"("+custName+")"+fileName.substring(fileName.lastIndexOf("."));
					filepaths.put("f2",path);
					contractCount++;
				}else if("财务规划服务协议书".equals(rentFileName)){
					if(serviceBookCount>0){
						continue;
					}
					//fname =  "财务规划服务协议书/" +leaseCode+"("+custName+")"+fileName.substring(fileName.lastIndexOf("."));
					filepaths.put("f3",path);
					serviceBookCount++;
				}
				
				
//				ZipEntry ze = new ZipEntry(fname);
//			    ze.setSize(f.length());
//                ze.setTime(f.lastModified());		               
//                zop.putNextEntry(ze);
//                int length = -1;
//				byte [] b = new byte [(int) f.length()];
//				while((length = in.read(b))!=-1){
//					zop.write(b,0,length);
//				}
//				in.close();
			}
			
			if((cardCount+contractCount+serviceBookCount)>0){
				List<String> filepath = new ArrayList<String>();
				//按照身份证、合同、协议书顺序
				if(cardCount>0){
					filepath.add(filepaths.get("f1"));
				}
				if(contractCount>0){
					filepath.add(filepaths.get("f2"));
				}
				if(serviceBookCount>0){
					filepath.add(filepaths.get("f3"));
				}
				ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			 	com.lowagie.text.Document pdf = null;  
		        try {  
		        	Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
		        	pdf = new com.lowagie.text.Document(rectPageSize); // 其余4个参数，设置了页面的4个边距
		           	PdfCopy copy = new PdfCopy(pdf,os);
		           	pdf.open(); 
		            
		            for (int i = 0; i < filepath.size(); i++) {  
		            	PdfReader reader  = null;
		            	if(filepath.get(i).toLowerCase().endsWith(".pdf")){//文件
			                reader = new PdfReader(filepath.get(i));  
		 
		            	}else if(filepath.get(i).toLowerCase().endsWith(".jpg")
		            			||filepath.get(i).toLowerCase().endsWith(".jpeg")
		            			||filepath.get(i).toLowerCase().endsWith(".bmp")
		            			||filepath.get(i).toLowerCase().endsWith(".gif")
		            			||filepath.get(i).toLowerCase().endsWith(".png")){
		            		ByteArrayOutputStream imageBuff = new ByteArrayOutputStream(); 
		            		com.lowagie.text.Document documentJpg = new com.lowagie.text.Document(rectPageSize); // 其余4个参数，设置了页面的4个边距
		            		PdfWriter.getInstance(documentJpg, imageBuff);
		            		documentJpg.open();
		            		Image image= Image.getInstance(filepath.get(i));
		            		//更具像素大小按一定比例缩小
		            		if(image.getWidth()>=1500){
		            			image.scalePercent(20);
		            		}else if(image.getWidth()<1500 && image.getWidth()>800){
		            			image.scalePercent(50);
		            		}	
		            		image.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
		            		documentJpg.add(image);
		            		documentJpg.close();
		            		reader = new PdfReader(imageBuff.toByteArray());
		            	}else{
		            		 throw new Exception("客户照片格式不正确，必须为 jpg jpeg bmp gif png pdf。");
		            	}
		        		int n = reader.getNumberOfPages();  
		                for (int j = 1; j <= n; j++) {  
		                	pdf.newPage();  
		                    PdfImportedPage page = copy.getImportedPage(reader, j);  
		                    copy.addPage(page);  
		                }
		            }  
		            
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        } finally {  
		        	pdf.close();  
		        }  
		        byte []  b = os.toByteArray();
		        os.close();
				ZipEntry ze = new ZipEntry(leaseCode+"("+custName+").pdf");
			    ze.setSize(b.length);
		        ze.setTime(new Date().getTime());	               
		        zop.putNextEntry(ze);
		        zop.write(b,0,b.length);          
			}
			if(cardCount==0 || contractCount==0 ||serviceBookCount==0){
				format = format3;
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			int col = 0;
			Label label = new Label(col,1 + index,String.valueOf(index));
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,leaseCode);
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			Date firstDate = LeaseUtil.getFirstPeriodPayDateByCreditId(creditId);
			label = new Label(col,1 + index,firstDate!=null?df.format(firstDate):"");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			Date lastDate = LeaseUtil.getLastPeriodPayDateByCreditId(creditId);
			label = new Label(col,1 + index,firstDate!=null?df.format(lastDate):"");
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,custName);
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,idcard);
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			
			label = new Label(col,1 + index,String.valueOf(cardCount));
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,String.valueOf(contractCount));
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,String.valueOf(serviceBookCount));
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,deptName);
			label.setCellFormat(format);
			sheet.addCell(label);
			col++;
			
			label = new Label(col,1 + index,userName);
			label.setCellFormat(format);
			sheet.addCell(label);
			index ++;
		}
		return index;
	}
	

	public String getPath() {
		return "\\\\"+LeaseUtil.getIPAddress()+path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}
	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	public void sendCarRentFileEmail() throws Exception{		
		Map param = new HashMap();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		if((month == 12 && day == 31)||(month!=12 && day==26)){
			
			
			ReportDateTo reportDate = ReportDateUtil.getDateByYearAndMonth(year,month);
			param.put("startDate", reportDate.getBeginTime());
			param.put("endDate", reportDate.getEndTime());
			List creditIDs = (List)DataAccessor.query("rentFile.getNewCreditId", param,DataAccessor.RS_TYPE.LIST);
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			
			String filePath =  getPath() + File.separator + year;
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			
			filePath +=  File.separator +"新车委贷备案资料明细表("+year+"-"+month+").xls";
			file = new File(filePath);
			
			WritableWorkbook wb = Workbook.createWorkbook(file,workbookSettings);
			WritableSheet sheet = wb.createSheet("新车委贷备案资料明细表", 0);
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			WritableFont font3 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			font3.setColour(Colour.RED);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.CENTRE);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format3.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format3.setWrap(true);
			
			sheet.setColumnView(0, 5);
			sheet.setColumnView(1, 20);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 40);
			sheet.setColumnView(4, 20);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 20);
			sheet.setColumnView(7, 20);
			sheet.setColumnView(8, 20);

			
			sheet.mergeCells(0, 0, 8, 0);
			Label label = new Label(0,0,"备案资料回传情况表");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(0,1,"序号");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(1,1,"合同号");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(2,1,"客户名称");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(3,1,"身份证号");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(4,1,"身份证");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(5,1,"合同");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(6,1,"财务规划服务协议书");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(7,1,"区域办事处");
			label.setCellFormat(format);
			sheet.addCell(label);
			
			label = new Label(8,1,"业务员");
			label.setCellFormat(format);
			sheet.addCell(label);
			int index = 1;
			
			for(int i=0,len=creditIDs.size();i<len;i++){
				Integer creditId  = (Integer) creditIDs.get(i);
				if(creditId != null){
					param.put("creditId", creditId);				
					List results = (List)DataAccessor.query("rentFile.getCarContractFile", param,DataAccessor.RS_TYPE.LIST);
					if(results==null || results.size()==0){//身份证和合同多不存在
						String leaseCode = LeaseUtil.getLeaseCodeByCreditId(String.valueOf(creditId));
						String custName = LeaseUtil.getCustNameByCreditId(String.valueOf(creditId));
						String idcard =  LeaseUtil.getNatuIdCardByCustId(LeaseUtil.getCustIdByCreditId(String.valueOf(creditId)));
						String userId = LeaseUtil.getSensorIdByCreditId(String.valueOf(creditId));
						String userName = LeaseUtil.getUserNameByUserId(userId);
						String deptName = LeaseUtil.getDecpNameByUserId(userId);
						label = new Label(0,1 + index,String.valueOf(index));
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(1,1 + index,leaseCode);
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(2,1 + index,custName);
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(3,1 + index,idcard);
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(4,1 + index,String.valueOf(0));
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(5,1 + index,String.valueOf(0));
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(6,1 + index,String.valueOf(0));
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(7,1 + index,deptName);
						label.setCellFormat(format3);
						sheet.addCell(label);
						
						label = new Label(8,1 + index,userName);
						label.setCellFormat(format3);
						sheet.addCell(label);
						index ++;
					}else{
						int cardCount = 0;
						int contractCount = 0 ;
						int serviceBookCount  = 0;
						String leaseCode = "";
						String custName ="";
						String deptName ="";
						String userName ="";
						for(int j=0,len2=results.size();j<len2;j++){
							Map fileMap  = (Map) results.get(j);
							String fileName= (String) fileMap.get("FILE_FILENAME");
							custName = (String) fileMap.get("CUST_NAME");
							leaseCode = (String) fileMap.get("LEASE_CODE");
							deptName  = (String) fileMap.get("DECP_NAME_CN");
							userName = (String) fileMap.get("NAME");					
							String rentFileName = (String) fileMap.get("FILE_NAME");
							
							if("车主身份证复印件".equals(rentFileName)||"客户照片".equals(rentFileName)||"营业执照、营业执照副本(含年审章)".equals(rentFileName)){
								if(cardCount==0){
									cardCount++;
								}
							}else if("个人委托贷款借款合同".equals(rentFileName)){
								if(contractCount==0){
									contractCount++;
								}								
							}else if("财务规划服务协议书".equals(rentFileName)){
								if(serviceBookCount==0){
									serviceBookCount++;
								}								
							}			
						}
						WritableCellFormat format4 =null;
						if(contractCount==0||contractCount==0 || serviceBookCount==0){
							format4 = format3;
						}else{
							format4 = format2;
						}
						label = new Label(0,1 + index,String.valueOf(index));
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(1,1 + index,leaseCode);
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(2,1 + index,custName);
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(3,1 + index,String.valueOf(cardCount));
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(4,1 + index,String.valueOf(contractCount));
						label.setCellFormat(format4);
						sheet.addCell(label);
												
						label = new Label(5,1 + index,String.valueOf(serviceBookCount));
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(6,1 + index,deptName);
						label.setCellFormat(format4);
						sheet.addCell(label);
						
						label = new Label(7,1 + index,userName);
						label.setCellFormat(format4);
						sheet.addCell(label);
						index ++;
						
					}
				}
			}		
			wb.write();
			wb.close();
			
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailContent("乘用车备案资料回传情况表");
			mailSettingTo.setEmailAttachPath(filePath);
			mailUtilService.sendMail(2009, mailSettingTo);
		}

	}

	public void setInvoiceManageService(InvoiceManageService invoiceManageService) {
		this.invoiceManageService = invoiceManageService;
	}
	
}
