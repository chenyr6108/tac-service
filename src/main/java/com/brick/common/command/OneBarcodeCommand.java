package com.brick.common.command;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code39Encoder;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.coderule.service.CodeRule;
import com.brick.log.service.LogPrint;
import com.brick.modifyOrder.command.ModifyOrderCommand;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 2013-10-29
 * 
 * @author zhang
 */
public class OneBarcodeCommand extends BaseCommand{
	Log logger = LogFactory.getLog(ModifyOrderCommand.class);
	// 得到条形码（支付表号）
	public String getOneBarcodeUtil(String recpCode) throws Exception {
		JBarcode localJBarcode = new JBarcode(EAN13Encoder.getInstance(),
				WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
		localJBarcode.setEncoder(Code39Encoder.getInstance());
		localJBarcode.setPainter(WideRatioCodedPainter.getInstance());
		localJBarcode.setTextPainter(BaseLineTextPainter.getInstance());
		localJBarcode.setShowCheckDigit(false);
		BufferedImage localBufferedImage = localJBarcode
				.createBarcode(recpCode);
		String recpCodeName = DateUtil.dateToString(new Date(),
				"yyyyMMddHHmmSSS") + recpCode;
		String path = saveToPNG(localBufferedImage, recpCodeName + ".png");
		return path;
	}

	// 得到条形码类型
	public String saveToPNG(BufferedImage paramBufferedImage, String paramString) {
		String path = saveToFile(paramBufferedImage, paramString, "png");
		return path;
	}
	// 写入服务器
	public String saveToFile(BufferedImage paramBufferedImage,
			String paramString1, String paramString2) {
		String path = "";
		try {
			File realPath = new File("D:/home/filsoft/financelease/oneCode"
					+ File.separator
					+ DateUtil.dateToString(new Date(), "yyyy_MM_dd")
					+ File.separator);
			if (!realPath.exists())
				realPath.mkdirs();
			FileOutputStream localFileOutputStream = new FileOutputStream(
					realPath + File.separator + paramString1);
			ImageUtil.encodeAndWrite(paramBufferedImage, paramString2,
					localFileOutputStream, 96, 96);
			localFileOutputStream.close();
			path = realPath + File.separator + paramString1;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return path;
	}
	//录入条形码页面	
	public void writeOneBarcode(Context context) {
		List expressList=null;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		context.contextMap.put("dataType", "快递公司");
		expressList=this.baseService.queryForList("modifyOrder.getOrderClassList",context.contextMap);
		outputMap.put("expressList", expressList);
		Output.jspOutput(outputMap, context, "/oneBarcode/writeOneBarcode.jsp");
	}
	
	
	//根据条形码编号查询承租人名称
	public void getCustNameByOneCode(Context context){
		Map outputMap = new HashMap();
		Map custNameMap= new HashMap();
		Map leaseCodeMap= new HashMap();
		try {
			String  ticketId =context.contextMap .get("TICKET_ID")==null?"":String.valueOf(context.contextMap .get("TICKET_ID"));
			String leaseCode="";
			if(ticketId.length()>14){
				leaseCode =ticketId.substring(0,14);
			}
			leaseCodeMap.put("leaseCode", leaseCode);
		    custNameMap=(Map)DataAccessor.query("decompose.getCustNameByLeaseCode",leaseCodeMap , DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("custNameMap", custNameMap);
		Output.jsonOutput(outputMap, context);
	}
	//添加条形码与快递单
	public void addOneBarcode(Context context){
		Map outputMap = new HashMap();
		Map OneBarcodeMap= new HashMap();
		try {
			String  ticketId =context.contextMap.get("TICKET_ID")==null?"":String.valueOf(context.contextMap .get("TICKET_ID"));
			String  expressId =context.contextMap.get("EXPRESS_ID")==null?"":String.valueOf(context.contextMap .get("EXPRESS_ID"));
			String  expressNameId =context.contextMap.get("EXPRESS_NAME_ID")==null?"":String.valueOf(context.contextMap .get("EXPRESS_NAME_ID"));
			OneBarcodeMap.put("ONE_BARCODE",ticketId);
			OneBarcodeMap.put("EXPRESS_ID",expressId);
			OneBarcodeMap.put("EXPRESS_NAME_ID",expressNameId);
			OneBarcodeMap.put("CREATE_USER", context.contextMap.get("s_employeeId"));
			//支付表号
			String recpCode="";
			if(ticketId.length()>16){
				recpCode =ticketId.substring(0,16);
			}
			OneBarcodeMap.put("RECP_CODE", recpCode);
			//期数
			String period="";
			if(ticketId.length()==19){
				period =ticketId.substring(17,19);
				Boolean isPeriod=isNum(period);
				if(isPeriod){
					period=period;
				}else{
					period =ticketId.substring(17,18);
				}
			}
			//因扫描仪设备不同、末尾最后一位补充位不稳定
			if(ticketId.length()==18){
				period =ticketId.substring(17,18);
				Boolean isPeriod=isNum(period);
				if(isPeriod){
					period=period;
				}else{
					period =ticketId.substring(17,18);
				}
			}
			OneBarcodeMap.put("PERIOD", period);
			DataAccessor.execute("decompose.addOneBarcode",OneBarcodeMap,DataAccessor.OPERATION_TYPE.INSERT);
			outputMap.put("isSuccess", "TRUE");
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	//判断是否数字
	public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	@SuppressWarnings("unchecked")
	public void queryOneBarcodeList(Context context) throws Exception {
				Map outputMap = new HashMap();
				PagingInfo<Object> pagingInfo = null;
				Map rsMap = new HashMap() ;
				Map paramMap = new HashMap();
				int status = 0;
				//删除权限
				boolean canDelete = false;
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				//pass显示通过按钮
				List expressList=null;
				boolean oneCode=false;
				List<String> resourceIdList=(List<String>) DataAccessor.query("modifyOrder.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("add-oneCode".equals(resourceIdList.get(i))) {
						oneCode=true;
					}
				}
				try {
					canDelete = baseService.checkAccessForResource("invoices_send_delete", String.valueOf(context.contextMap.get("s_employeeId")));
					context.contextMap.put("dataType", "快递公司");
					expressList=this.baseService.queryForList("modifyOrder.getOrderClassList",context.contextMap);
					String expressNameId =context.contextMap.get("expressNameId")==null ? "" :context.contextMap.get("expressNameId").toString();
					if(expressNameId.equals("-1")){
						context.contextMap.put("expressNameId", "");
					}
					if(StringUtils.isEmpty(context.contextMap.get("status"))){
						context.contextMap.put("status", status);
					} else {
						status = Integer.parseInt(context.contextMap.get("status").toString());
					}
					pagingInfo = baseService.queryForListWithPaging("decompose.getOneBarcodeList", context.contextMap, "CREATE_TIME", ORDER_TYPE.DESC);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				outputMap.put("canDelete", canDelete);
				outputMap.put("status", status);
				outputMap.put("companyCode", context.contextMap.get("companyCode"));
				outputMap.put("companys", LeaseUtil.getCompanys());	
				outputMap.put("expressNameId", context.contextMap.get("expressNameId"));
				outputMap.put("expressList", expressList);
				outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
				outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
				outputMap.put("oneCode", oneCode);
				outputMap.put("pagingInfo", pagingInfo);
				outputMap.put("content", context.contextMap.get("content"));
				Output.jspOutput(outputMap, context, "/oneBarcode/oneBarcodeList.jsp");
		}
	//*****************************************************************************************************
	//导出条形码
		public  List<Map<String,Object>> exportInvoiceForOneBarcode(String date,String invoiceType) throws Exception {
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("invoiceType",invoiceType);
				int year=Integer.valueOf(date.split("-")[0]);
				int month=Integer.valueOf(date.split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				param.put("financeStartDate",to.getBeginTime());
				param.put("financeEndDate",to.getEndTime());
				String beginDate = date+"-1";
				String endDate = DateUtil.getLastDayOfMonth(beginDate);
				param.put("startDate",beginDate);
				param.put("endDate",endDate);
				
				param.put("needOrderBy","Y");
				List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("priceReport.queryInvoice",param,DataAccessor.RS_TYPE.LIST);
				if(resultList.size()>0){
					for(int i=0;i<resultList.size();i++){
						Map<String,Object> imap=resultList.get(i);
						String  oneBarcode =imap.get("ONEBARCODE")==null?"":String.valueOf(imap.get("ONEBARCODE"));
						String path = getOneBarcodeUtil(oneBarcode);
						imap.put("PATH", path);
					}
				}
				return resultList;
			}
		
		//导出条形码
		public  List<Map<String,Object>> exportInvoiceForOneBarcode(String date,String invoiceType,String companyCode) throws Exception {
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("invoiceType",invoiceType);
				int year=Integer.valueOf(date.split("-")[0]);
				int month=Integer.valueOf(date.split("-")[1]);
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				param.put("financeStartDate",to.getBeginTime());
				param.put("financeEndDate",to.getEndTime());
				String beginDate = date+"-1";
				String endDate = DateUtil.getLastDayOfMonth(beginDate);
				param.put("startDate",beginDate);
				param.put("endDate",endDate);
				
				param.put("needOrderBy","Y");
				param.put("companyCode",companyCode);
				List<Map<String,Object>> resultList=(List<Map<String,Object>>)DataAccessor.query("priceReport.queryInvoice",param,DataAccessor.RS_TYPE.LIST);
				if(resultList.size()>0){
					for(int i=0;i<resultList.size();i++){
						Map<String,Object> imap=resultList.get(i);
						String  oneBarcode =imap.get("ONEBARCODE")==null?"":String.valueOf(imap.get("ONEBARCODE"));
						String path = getOneBarcodeUtil(oneBarcode);
						imap.put("PATH", path);
					}
				}
				return resultList;
			}
	//直租新案件的条形码
	@SuppressWarnings("unchecked")
	public void getOneCodeImage(Context context) throws Exception {
		List<Map<String,Object>> listOneCode =exportInvoiceForOneBarcode((String)context.contextMap.get("selectDate"),(String)context.contextMap.get("invoiceType"),(String)context.contextMap.get("companyCode"));
		ByteArrayOutputStream baos = null;
		try {
			if (listOneCode.size() > 0) {
				// 页面设置
			    Rectangle rectPageSize = new Rectangle(PageSize.A4);
				Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				document.open();
					BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
					float[] widthsSt2 = { 30f, 20f,50f };
					if(listOneCode.size()>0){
						for(int i=0;i<listOneCode.size();i++){
							PdfPTable tb = new PdfPTable(widthsSt2);
							tb.addCell(makeCell((String)listOneCode.get(i).get("CUST_NAME"),PdfPCell.ALIGN_CENTER, FontColumn));
							tb.addCell(makeCell((String)listOneCode.get(i).get("ONEBARCODE"),PdfPCell.ALIGN_CENTER, FontColumn));
							 Image img=Image.getInstance((String)listOneCode.get(i).get("PATH"));
							 img.setAlignment(Image.RIGHT);//设置图片显示位置
							 img.scaleAbsoluteHeight(50);
							 img.scaleAbsoluteWidth(150);
							tb.addCell(img);
							document.add(tb);
						}
					}
				if (document != null) {
					document.close();
				}
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=条形码对照表");
				ServletOutputStream o = context.response.getOutputStream();
				baos.writeTo(o);
				o.flush();
				o.close();
			} else {
				ServletOutputStream o = context.response.getOutputStream();
				o.flush();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	// 创建多列单元格
	private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		return objCell;
	}
	
	//直租旧案件的条形码
		@SuppressWarnings("unchecked")
		public void getOneCodeImageForOld(Context context) throws Exception {
			if(context.contextMap.get("startDate") == null){
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				context.contextMap.put("startDate", sf.format(new Date())) ;
			}else {
				context.contextMap.put("startDate", context.contextMap.get("startDate") + "-01") ;
			}
			context.contextMap.put("orderColumn","RUNNUM");
			//List<Map<String,Object>> listOneCode =exportInvoiceForOneBarcode((String)context.contextMap.get("selectDate"),(String)context.contextMap.get("invoiceType"));
			List<Map<String,Object>> listOneCode=(List<Map<String,Object>>) DataAccessor.query("priceReport.exportValueAddTaxByMonth", context.contextMap, DataAccessor.RS_TYPE.LIST);
			if(listOneCode.size()>0){
				for(int i=0;i<listOneCode.size();i++){
					Map<String,Object> imap=listOneCode.get(i);
					String  oneBarcode =imap.get("ONEBARCODE")==null?"":String.valueOf(imap.get("ONEBARCODE"));
					String path = getOneBarcodeUtil(oneBarcode);
					imap.put("PATH", path);
				}
			}
			ByteArrayOutputStream baos = null;
			try {
				if (listOneCode.size() > 0) {
					// 页面设置
				    Rectangle rectPageSize = new Rectangle(PageSize.A4);
					Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					document.open();
						BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
						Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
						float[] widthsSt2 = { 30f, 20f,50f };
						if(listOneCode.size()>0){
							for(int i=0;i<listOneCode.size();i++){
								PdfPTable tb = new PdfPTable(widthsSt2);
								tb.addCell(makeCell((String)listOneCode.get(i).get("CUST_NAME"),PdfPCell.ALIGN_CENTER, FontColumn));
								tb.addCell(makeCell((String)listOneCode.get(i).get("ONEBARCODE"),PdfPCell.ALIGN_CENTER, FontColumn));
								 Image img=Image.getInstance((String)listOneCode.get(i).get("PATH"));
								 img.setAlignment(Image.RIGHT);//设置图片显示位置
								 img.scaleAbsoluteHeight(50);
								 img.scaleAbsoluteWidth(150);
								tb.addCell(img);
								document.add(tb);
							}
						}
					if (document != null) {
						document.close();
					}
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition","attachment; filename=条形码对照表");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
				} else {
					ServletOutputStream o = context.response.getOutputStream();
					o.flush();
					o.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		
		//上传
		@SuppressWarnings("unchecked")
		public void upOneCodeExcel(Context context) {
			Map outputMap = new HashMap();			
			List errList = context.errList ;
			Map contextmap = context.getContextMap();
			Workbook workbook = null;
			InputStream in = (InputStream) contextmap.get("excelInputStream");
			if (in == null) {
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
			try {
				workbook = WorkbookFactory.create(in);
				// (从第二行开始)
				List<Map> composes = buildWorkbook(workbook);
				outputMap.put("composes", composes);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("发票条形码上传错误，解析excel错误!请联系管理员") ;
			}		
			if(errList.isEmpty()){
				Output.jspOutput(outputMap, context,"/oneBarcode/uploadOneBarcode.jsp");
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
		//
		private List<Map> buildWorkbook(Workbook workbook) {
			int num = 0;
			List<Map> Composes = new ArrayList<Map>();
			Map values = null;//行
			
			if (workbook != null) {
				int x = workbook.getNumberOfSheets();
				for(int y=0;y<x;y++){ 
				// 表格
				Sheet sheet = workbook.getSheetAt(y);
				if (sheet != null) {
					Iterator rit = sheet.rowIterator();
					rit.next();
					// 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)			
					int i = 1 + num; //-------------------------------------	
					for (; rit.hasNext();) {
						values = new HashMap();					
						values.put("ONE_BARCODE", null);
						values.put("EXPRESS_ID", null);
						values.put("RECP_CODE", null);
						values.put("PERIOD", null);
						values.put("CREATE_USER", null);
						values.put("EXPRESS_NAME_ID", null);
						
						Row row = (Row) rit.next();// 得到行
						Iterator cit = row.cellIterator();// 遍历单元格
						for (; cit.hasNext();) {
							Cell cell = (Cell) cit.next();
							String clounmName = "";
							int j = cell.getColumnIndex();
							switch (cell.getColumnIndex()) {
							case 0:
								clounmName = "ONE_BARCODE";// 必须存在
								break;
							case 1:
								clounmName = "EXPRESS_ID";// 必须存在							
								break;
							case 2:
								clounmName = "EXPRESS_NAME_ID";// 必须存在
								break;
							}
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								if(clounmName.equals("ONE_BARCODE"))
								{
									values.put(clounmName, cell.getStringCellValue().trim());
									String oneBarCode =cell.getStringCellValue().trim();
									
									//支付表号
									String recpCode="";
									if(oneBarCode.length()>16){
										recpCode =oneBarCode.substring(0,16);
									}
									values.put("RECP_CODE",recpCode);
									//期数
									String period="";
									period =oneBarCode.substring(17,oneBarCode.length());
									values.put("PERIOD", period);
								}
								if(clounmName.equals("EXPRESS_ID"))
								    {
										values.put(clounmName, cell.getStringCellValue().trim());
									}
								if(clounmName.equals("EXPRESS_NAME_ID"))
									{
										values.put(clounmName, cell.getStringCellValue().trim());
									}
								break;
							}
						}
						if (!values.isEmpty() && values.size() > 0) {
							Set<Entry> set = values.entrySet();
							int taglib = 0;
							for (Entry entry : set) {
								if (entry.getValue() == null) {
									taglib++;
								}
							}
							values.put("rowNumber", (i++));
							Composes.add(values);
						}
					}
					num = Composes.size();
				}
				}
			}
			return Composes;
					
		}
		//保存数据saveOneCode
		public void saveOneCode(Context context) {
			Map outputMap = new HashMap() ;
			List errList = context.errList ;
			// 得到保存在session中的消息
			FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
			SqlMapClient sqlMapClient = DataAccessor.getSession();
			// 保存文件到硬盘中
			try {
				sqlMapClient.startTransaction();
				Long syupId = FileExcelUpload.saveExcelFileToDisk(context, fileItem,sqlMapClient);
				this.saveExcelFileToDataBase(context, sqlMapClient,syupId);
				sqlMapClient.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("寄送发票上传的excel文件") ;
			} finally {
				try {
					sqlMapClient.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
			if(errList.isEmpty()){
				try {
					queryOneBarcodeList(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				outputMap.put("errList", errList) ;
				Output.jspOutput(outputMap, context, "/error.jsp") ;
			}
		}
		private void saveExcelFileToDataBase(Context context,SqlMapClient sqlMapClient, Long syupId) throws Exception {
			String path = realPath(context,syupId);
			InputStream in =new FileInputStream(path); 
			Workbook workbook = WorkbookFactory.create(in);
			List<Map> composes = buildWorkbook(workbook);
			for (int i = 0; i < composes.size(); i++) {
				Map map = composes.get(i);
				map.put("CREATE_USER",context.contextMap.get("s_employeeId"));
				Set<Entry> set = map.entrySet();
				for (Entry entry : set) {
					Object key = entry.getKey();
					Object value = entry.getValue();
					context.getContextMap().put(key, value);		
				}
				// 存储到数据库中
				sqlMapClient.insert("decompose.addOneBarcode", context.getContextMap());
			}
		}
		/**
		 * 取得文件保存路径 
		 * @param context
		 */
		public String realPath(Context context,Long syupId) {
			String bootPath = FileExcelUpload.getUploadPath();
			Map pathMap = null;
			List errList = context.errList;
			if(errList.isEmpty()) {
				try {
					context.contextMap.put("syupId", syupId);
					pathMap = (Map)DataAccessor.query("uploadPicture.queryPath", context.contextMap, DataAccessor.RS_TYPE.MAP);
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
			String path = (String) pathMap.get("PATH");
			String realPath = bootPath + path;
			return realPath;
		}
		
		/**
		 * 删除
		 * @param context
		 */
		public void deleteOneBarcode(Context context){
			String msg = "ok";
			try {
				DataAccessor.execute("decompose.deleteOneBarcode", context.contextMap, OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				msg = "err:" + e.getMessage();
			}
			Output.txtOutput(msg, context);
		}
}
