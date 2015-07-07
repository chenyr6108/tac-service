package com.brick.upload.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.FileExcelUpload;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

public class UploadService extends AService {
	/**
	 * 转到上传附件页面
	 * @param context
	 */
	public void getUploadJsp(Context context) {
		Map outputMap = new HashMap();
		outputMap.put("RECT_ID", context.contextMap.get("RECT_ID"));
		Output.jspOutput(outputMap, context,"/rentcontract/upload.jsp");		
	}
	/**
	 * 保存附件
	 * @param context
	 */
	public void uploadPic(Context context) {
		InputStream in = (InputStream) context.contextMap.get("picInputStream");
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			saveFileToDisk(context, fileItem,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Output.jspSendRedirect(context,context.request.getContextPath()+"/rentcontract/upload.jsp");
		
	}
	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * @param context
	 * @param fileItem
	 * @return
	 */
	public String saveFileToDisk(Context context, FileItem fileItem,SqlMapClient sqlMapClient) {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath();
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
						contextMap.put("title", "上传的pic附件");
						syupId = (Long) sqlMapClient.insert("uploadPicture.create", contextMap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
				  try{
					 // fileItem.getOutputStream().flush();
					 // fileItem.getOutputStream().close();
					  fileItem.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
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
	public String getUploadPath() {
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
		    	if("pic".equals(s)){
		    		Element pathElement=element.element("path");
		    		path = pathElement.getText();
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
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
		String bootPath = this.getUploadPath();;
		String path=bootPath +  savaPath;
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");	
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			context.response.setHeader("Content-Disposition", "attachment; filename=" + new String(name.getBytes("gb2312"), "iso8859-1"));

			output = context.response.getOutputStream();
			fis = new FileInputStream(file);

			byte[] b = new byte[1024];
			int i = 0;

			while ((i = fis.read(b)) != -1) {

				output.write(b, 0, i);
			}
			output.write(b, 0, b.length);

			output.flush();
			context.response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}
		}


	}
}
