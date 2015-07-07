package com.brick.collection.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.base.util.LeaseUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author wuzd
 * @date 10 26, 2010
 * @version 
 */
public class OpenInvoice extends AService {
	static Log logger = LogFactory.getLog(OpenInvoice.class);
	/**
	 * 查询未开发票的支付表明细
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryOpenInvoice(Context context) {		
		List errList = context.errList;
		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				if(context.contextMap.get("QSTART_DATE") == null || "".equals(context.contextMap.get("QSTART_DATE")) ){
					context.contextMap.put("QSTART_DATE", new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())) ;
				}
				dw = (DataWrap) DataAccessor.query("collectionManage.queryOpenInvoice", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());
		outputMap.put("isopen", context.contextMap.get("isopen"));
		outputMap.put("content", context.contextMap.get("content"));
		Output.jspOutput(outputMap, context, "/collection/openInvoiceManage.jsp");
	}
	
	/**
	 * 导出全部未开发票的支付表明细
	 * @param context
	 */	
	@SuppressWarnings("unchecked")
	public void openInvoice(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List list = null;
		if(errList.isEmpty()){		
			try {
				list = (List) DataAccessor.query("collectionManage.queryOpenInvoice",context.contextMap, DataAccessor.RS_TYPE.LIST);
				ByteArrayOutputStream baos = null;
				String strFileName = "";
				ExportExcel exl = new ExportExcel();
				exl.createexl();
				strFileName = "开具发票.xls";
				baos = exl.export("开具发票",list);
				context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
				ServletOutputStream out1 = context.response.getOutputStream();
				exl.close();
				baos.writeTo(out1);
				out1.flush();	
				out1.close();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					Map object = (Map) iterator.next();
					context.contextMap.put("RECD_ID", object.get("RECD_ID"));
					DataAccessor.execute("collectionManage.createInvoiceDetail",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				}				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
	}

	/**
	 * 导出部分未开发票的支付表明细
	 * @param context
	 */	
	@SuppressWarnings("unchecked")
	public void openInvoiceForPart(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		List list = null;
		if(errList.isEmpty()){		
			try {
				
				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				context.contextMap.put("idss",ids);				
				list = (List) DataAccessor.query("collectionManage.queryOpenInvoiceForPart",context.contextMap, DataAccessor.RS_TYPE.LIST);
				ByteArrayOutputStream baos = null;
				String strFileName = "";
				ExportExcel exl = new ExportExcel();
				exl.createexl();
				strFileName = "开具发票.xls";
				baos = exl.export("开具发票",list);
				context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
				context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
				ServletOutputStream out1 = context.response.getOutputStream();
				exl.close();
				baos.writeTo(out1);
				out1.flush();	
				out1.close();
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("RECD_ID", ids[i]);
					DataAccessor.execute("collectionManage.createInvoiceDetail",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
	}
	/**
	 * 上传开具发票，解析excel
	 */
	public void invoiceUpload(Context context) {
		Map outputMap = new HashMap();			
		Map contextmap = context.getContextMap();
		List errList = context.errList;
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		// 1. 读取 Excel 文件: 得到 Workbook 对象
		Workbook workbook = null;
		// 得到输入流
		InputStream in = (InputStream) contextmap.get("excelInputStream");
		List<Map> invoice = null;
		if (in == null) {
			// TODO exception
		}
		if(errList.isEmpty()){	
		try {
			
			workbook = WorkbookFactory.create(in);
			// 2. 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)
			invoice = buildInvoiceListFromWorkbook(workbook);
			// 判断数据是否合法
			//isValidatorComposesList(context, invoice);
			//保存上传的excel文件到硬盘
			this.saveServiceExcel(context);
			DataAccessor.getSession().startTransaction();
			for (int i = 0; i < invoice.size(); i++) {
				Map map = invoice.get(i);
				if(map.get("open_time")==null){
					map.put("open_time","");
				}
				if(map.get("invoice_code")==null){
					map.put("invoice_code","");
				}
				if(map.get("open_user")==null){
					map.put("open_user","");
				}
				if (map.get("open_time")==null && map.get("invoice_code")==null && map.get("open_user")==null) {
					map.put("isopen",null);
				}
				else{
					map.put("isopen",1);
				}
				Set<Entry> set = map.entrySet();
				for (Entry entry : set) {
					Object key = entry.getKey();
					Object value = entry.getValue();
					context.getContextMap().put(key, value);		
				}

				// 存储到数据库中
				sqlMapClient.update("collectionManage.updatePayIsopen", context.getContextMap());
				DataAccessor.execute("collectionManage.createInvoiceDetail2",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			//session.setAttribute("composes", composes);
			DataAccessor.getSession().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				errList.add("上传开票关闭事物错误");
				logger.info("上传开票关闭事物错误");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		// 5. 响应信息
		outputMap.put("invoice", invoice);
		HttpSession session = context.getRequest().getSession();
		HttpServletRequest request = context.getRequest();
		session.setAttribute("remark", context.getContextMap().get("remark"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/collection/openInvoice.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		}
	}
	/**
	 * 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始) 为每一行都常见一个 Compose 对象, 把这些对象放到一个 List
	 * 中
	 * 
	 * @param workbook
	 * @return
	 */
	private List<Map> buildInvoiceListFromWorkbook(Workbook workbook) {
		
		int num = 0;
		// List<Compose> Composes = new ArrayList<Compose>();
		List<Map> Composes = new ArrayList<Map>();
		// 放置 Excel 一行中的 Compose 相关值, 其顺序为:
		// 当前的行数
		Map values = null;
		if (workbook != null) {
			
			int x = workbook.getNumberOfSheets();
			for(int y=0;y<x;y++){ 
			// 表格
			Sheet sheet = workbook.getSheetAt(y);
			if (sheet != null) {
				Iterator rit = sheet.rowIterator();
				rit.next();
				// 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)			
				int i = 1 + num;	
				for (; rit.hasNext();) {
					values = new HashMap();					
					values.put("recd_id", null);
					values.put("open_time", null);
					values.put("invoice_code", null);
					values.put("open_user", null);
					values.put("rent_price", null);
					values.put("recp_id", null);
					Row row = (Row) rit.next();// 得到行
					Iterator cit = row.cellIterator();// 遍历单元格
					// cit.next();
					for (; cit.hasNext();) {

						Cell cell = (Cell) cit.next();
						String clounmName = "";
						
						switch (cell.getColumnIndex()) {
						case 0:
							clounmName = "recd_id";
							break;
						case 1:
							clounmName = "recp_id";
							break;
						case 2:
							clounmName = "rent_price";
							break;	
						case 3:
							clounmName = "invoice_code";
							break;						
						case 12:
							clounmName = "open_time";
							break;
						case 13:
							clounmName = "invoice_code";
							break;
						case 14:
							clounmName = "open_user";
							break;
						}

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							values.put(clounmName, cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (clounmName.equals("open_time")) {
								values.put(clounmName, new Date(cell.getDateCellValue().getTime()));
								break;
							}				
							DecimalFormat form = new DecimalFormat("#");
							values.put(clounmName, form.format(cell.getNumericCellValue()));							
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
						if (taglib == 16) {
							break;
						}
						values.put("rowNumber", (i++));
						Composes.add(values);
					}
					// Composes.add(buileComposeFromStringList(values));
				}
				num = Composes.size();
			}
			}
		}
		return Composes;		
	}

	/**
	 * 保存上传的excel文件
	 * 
	 * @param context
	 */
	public void saveServiceExcel(Context context) {
		//String target = null;
		// 得到保存在session中的消息
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		// 保存文件到硬盘中
		try {
			sqlMapClient.startTransaction();
			Long syupId = this.saveExcelFileToDisk(context, fileItem,sqlMapClient);
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

	/**
	 * 删除缓存文件 
	 * @param context
	 */
	public void deleteTemp(Context context) {
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		fileItem.delete();
		Output.jspSendRedirect(context,context.request.getContextPath()+"/decompose/upload.jsp");
		
	}
	/**
	 * 取得文件保存路径 
	 * @param context
	 */
	public String realPath(Context context,Long syupId) {
		String bootPath = this.getUploadPath();
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
	 * 
	 * @return picturePath 读取upload-config.xml文件
	 */
	public static String getUploadPath() {

		String path = null;
/*		Properties prop = new Properties();
		InputStream in = FileExcelUpload.class.getClassLoader()
				.getResourceAsStream("upload.xml");
		try {
			prop.loadFromXML(in);
			picturePath = prop.getProperty("excelPath");
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
		    for(Iterator it=nodes.iterator();it.hasNext();){
		    	Element element = (Element) it.next();
		    	Element nameElement=element.element("name");
		    	String s = nameElement.getText();
		    	if("compose".equals(s)){
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
	 * 保存excel文件到硬盘中
	 * @param context
	 * @param fileItem
	 * @return
	 */
	public static Long saveExcelFileToDisk(Context context, FileItem fileItem,SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = getUploadPath();
		//创建时间
		//Date create_time = new Date(System.currentTimeMillis()); 
		//创建人
		int s_employeeId = Integer.parseInt(String.valueOf(contextMap.get("s_employeeId")));
		//类型1
		int TYPE = 1;
		//文件存放位置
		String file_path="";
		//文件名称
		String file_name="";
		//备注
		String remark=(String) context.getRequest().getSession().getAttribute("remark");
		Long syupId = null;
		if (bootPath != null) {
			File realPath = new File(bootPath + File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
			// 重命名图片名称
			String excelNewName = getNewFileName();
			File uploadedFile = new File(realPath.getPath() + File.separator
					+ excelNewName + "." + type);
			// 存储在数据库中的路径
			file_path = File.separator + type + File.separator
					+ excelNewName + "." + type;
			// 图片的名称
			file_name = excelNewName + "." + type;
			// Write the uploaded file to the system
			try {
				if (errList.isEmpty()) {
					fileItem.write(uploadedFile);
					contextMap.put("file_path", file_path);
					contextMap.put("file_name", file_name);
					contextMap.put("s_employeeId", s_employeeId);
					contextMap.put("type", TYPE);
					contextMap.put("remark", remark);
					contextMap.put("title", "kaipiao上传的excel");
					contextMap.put("RECT_ID", "");
					
//					DataAccessor.execute("uploadPicture.create", contextMap,
//							DataAccessor.OPERATION_TYPE.INSERT);
					syupId = (Long) sqlMapClient.insert("uploadPicture.create", contextMap);
					//System.out.println(syup_id);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}

		return syupId;
	}

	/**
	 * 对文件进行重命名
	 * 
	 * @return 
	 */
	public static String getNewFileName() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(calendar.YEAR);
		int month = calendar.get(calendar.MONTH) + 1;
		int day = calendar.get(calendar.DATE);
		int hour = calendar.get(calendar.HOUR_OF_DAY);
		int minute = calendar.get(calendar.MINUTE);
		int second = calendar.get(calendar.SECOND);

		StringBuffer path = new StringBuffer();
		path.append(year);
		path = (month < 10) ? path.append(0).append(month) : path.append(month);
		path = (day < 10) ? path.append(0).append(day) : path.append(day);
		path = (hour < 10) ? path.append(0).append(hour) : path.append(hour);
		path = (minute < 10) ? path.append(0).append(minute) : path
				.append(minute);
		path = (second < 10) ? path.append(0).append(second) : path
				.append(second);

		// 生成十位的随机数
		for (int i = 0; i < 10; i++) {
			int random = (int) (Math.random() * 10);
			path.append(random);
		}
		return path.toString();
	}
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateYuDate(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		if(errList.isEmpty()) {
			try {
				DataAccessor.execute("collectionManage.updateYuDate", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);	
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}	
	}	

	/**
	 * 上传发票号码，解析excel
	 */
	public void invoiceCodeUpload(Context context) {
		Map outputMap = new HashMap();			
		Map contextmap = context.getContextMap();
		List errList = context.errList;
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		// 1. 读取 Excel 文件: 得到 Workbook 对象
		Workbook workbook = null;
		// 得到输入流
		InputStream in = (InputStream) contextmap.get("excelInputStream");
		List<Map> invoice = null;
		if (in == null) {
			// TODO exception
		}
		if(errList.isEmpty()){	
		try {
			
			workbook = WorkbookFactory.create(in);
			// 2. 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)
			invoice = buildInvoiceListFromWorkbook(workbook);
			//判断公司别是否正确
			StringBuffer errorMsg = new StringBuffer("");
			String companyCode = (String) context.contextMap.get("companyCode");
			for (int i = 0; i < invoice.size(); i++) {
				Map paramMap = invoice.get(i);
				if(paramMap.get("recd_id")!=null && !"".equals(paramMap.get("recd_id"))){
					String code = (String) DataAccessor.query("collectionManage.getCompanyCodeByRecdId", paramMap, RS_TYPE.OBJECT);
					if(!companyCode.equals(code)){
						errorMsg.append(paramMap.get("invoice_code"));
						errorMsg.append(",");
					}
				}

			}
			//存在错误信息，则不上传
			if(!"".equals(errorMsg.toString())){
				errorMsg.append("以上公司别不正确。");
				outputMap.put("errorMsg", errorMsg.toString());
				outputMap.put("companyCode", companyCode);
				Output.jspOutput(outputMap, context,"/collection/openInvoiceCode.jsp");
				return;
			}
			
			// 判断数据是否合法
			//isValidatorComposesList(context, invoice);
			//保存上传的excel文件到硬盘
			this.saveServiceExcel(context);
			DataAccessor.getSession().startTransaction();
			for (int i = 0; i < invoice.size(); i++) {
				Map map = invoice.get(i);
				if(map.get("open_time")==null){
					map.put("open_time","");
				}
				if(map.get("invoice_code")==null){
					map.put("invoice_code","");
				}
				if(map.get("open_user")==null){
					map.put("open_user","");
				}
				if (map.get("open_time")==null && map.get("invoice_code")==null && map.get("open_user")==null) {
					map.put("isopen",null);
				}
				else{
					map.put("isopen",1);
				}
				Set<Entry> set = map.entrySet();
				for (Entry entry : set) {
					Object key = entry.getKey();
					Object value = entry.getValue();
					context.getContextMap().put(key, value);		
				}

				// 存储到数据库中
				sqlMapClient.update("collectionManage.updateInvoiceCode", context.getContextMap());
			}
			//保存Excel 附件
			DataAccessor.execute("collectionManage.createInvoiceCodeFile",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
			//session.setAttribute("composes", composes);
			DataAccessor.getSession().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				errList.add("上传开票关闭事物错误");
				logger.info("上传开票关闭事物错误");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		// 5. 响应信息
		outputMap.put("invoice", invoice);
		HttpSession session = context.getRequest().getSession();
		HttpServletRequest request = context.getRequest();
		session.setAttribute("remark", context.getContextMap().get("remark"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/collection/openInvoiceCode.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		}
	}
}
