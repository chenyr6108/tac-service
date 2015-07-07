package com.ueditor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import com.brick.base.util.LeaseUtil;






import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * UEditor文件上传辅助类
 *
 */
public class Uploader {
	
	//文件大小常量, 单位kb
	private static final int MAX_SIZE = 10000;
	// 输出文件地址
	private String url = "";
	// 上传文件名
	private String fileName = "";
	// 状态
	private String state = "";
	// 文件类型
	private String type = "";
	// 原始文件名
	private String originalName = "";
	// 文件大小
	private String size = "";

	private HttpServletRequest request = null;
	private String title = "";

	// 保存路径
	private String savePath = null;
	// 文件允许格式
	//private String[] allowFiles = { ".rar", ".doc", ".docx", ".zip", ".pdf",".txt", ".swf", ".wmv", ".gif", ".png", ".jpg", ".jpeg", ".bmp" };
	private String[] allowFiles = null;
	// 文件大小限制，单位Byte
	private long maxSize = 0;
	
	private HashMap<String, String> errorInfo = new HashMap<String, String>();
	private Map<String, String> params = null;
	//上传的文件数据
	private byte[] fileBytes = null;
	
	public static final String ENCODE = "utf-8";

	public Uploader(HttpServletRequest request) {
		this.request = request;
		this.params = new HashMap<String, String>();

		this.setMaxSize(Uploader.MAX_SIZE );
		
		this.parseParams();
		
		HashMap<String, String> tmp = this.errorInfo;
		tmp.put("SUCCESS", "SUCCESS"); //默认成功
		// 未包含文件上传域
		tmp.put("NOFILE", "\\u672a\\u5305\\u542b\\u6587\\u4ef6\\u4e0a\\u4f20\\u57df");
		// 不允许的文件格式
		tmp.put("TYPE", "\\u4e0d\\u5141\\u8bb8\\u7684\\u6587\\u4ef6\\u683c\\u5f0f");
		// 文件大小超出限制
		tmp.put("SIZE", "\\u6587\\u4ef6\\u5927\\u5c0f\\u8d85\\u51fa\\u9650\\u5236");
		// 请求类型错误
		tmp.put("ENTYPE", "\\u8bf7\\u6c42\\u7c7b\\u578b\\u9519\\u8bef");
		// 上传请求异常
		tmp.put("REQUEST", "\\u4e0a\\u4f20\\u8bf7\\u6c42\\u5f02\\u5e38");
		// 未找到上传文件
		tmp.put("FILE", "\\u672a\\u627e\\u5230\\u4e0a\\u4f20\\u6587\\u4ef6");
        // IO异常
		tmp.put("IO", "IO\\u5f02\\u5e38");
		// 目录创建失败
		tmp.put("DIR", "\\u76ee\\u5f55\\u521b\\u5efa\\u5931\\u8d25");
        // 未知错误
		tmp.put("UNKNOWN", "\\u672a\\u77e5\\u9519\\u8bef");
	
	}
	
	public void download(HttpServletResponse response) throws Exception{
		String propertiesPath = request.getRealPath("/help/js/jsp/config.properties");

		Properties properties = new Properties();

		try {
		    properties.load( new FileInputStream( propertiesPath ) );
		} catch ( Exception e ) {
		    //加载失败的处理
		    e.printStackTrace();
		}
		
		String folder =  this.getFolderNameForDownload();
		String fileName = this.getFileNameForDownload();
		

		String dirPath = properties.getProperty("dirPath");
		
		dirPath += File.separator + getSavePathForDownload();
		dirPath += File.separator + folder;
		dirPath += File.separator + fileName;
		
		response.setHeader("Content-Disposition",
				"attachment; filename="
						+ new String(fileName.getBytes("gb2312"), "iso8859-1"));
		
		FileInputStream fis = new FileInputStream(dirPath);
		OutputStream out = response.getOutputStream();
		int length  = fis.available();
		int len = 0;
		byte[] buf = new byte[length];
		while((len=fis.read(buf))>0){
			out.write(buf,0,len);
		}
		
		fis.close();
        out.close();
	}

	public void upload() throws Exception {
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
		if (!isMultipart) {
			this.state = this.errorInfo.get("NOFILE");
			return;
		}
		
		if ( this.fileBytes == null ) {
			this.state = this.errorInfo.get("FILE");
			return;
		}
		
		//存储title
		this.title = this.getParameter( "pictitle" );

		try {
			

			String propertiesPath = request.getRealPath("/help/js/jsp/config.properties");

			Properties properties = new Properties();

			try {
			    properties.load( new FileInputStream( propertiesPath ) );
			} catch ( Exception e ) {
			    //加载失败的处理
			    e.printStackTrace();
			}
			

			String dirPath = properties.getProperty("dirPath");
			
			if(this.savePath == null){
				this.savePath = this.getDefaultSavePath();	
			}
			dirPath += File.separator + this.savePath;
			
			String folder = this.getFolder();
			this.url = "&savePath=" + this.savePath; 		
			this.url += "&folder=" + folder;
			
			dirPath += File.separator + folder;
			
			if (!this.checkFileType(this.originalName)) {
				this.state = this.errorInfo.get("TYPE");
				return;
			} 
			
			if ( this.fileBytes.length > this.maxSize ) {
				this.state = this.errorInfo.get("SIZE");
				return;
			} 
			
			this.fileName = this.getName(this.originalName);
			this.type = this.getFileExt(this.fileName);
			
			
			File dir = new File(dirPath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			dirPath += File.separator + this.fileName;
			this.url += "&fileName=" + this.fileName;
			
			FileOutputStream fos = new FileOutputStream(dirPath);
			fos.write( this.fileBytes );
			fos.close();
			
			this.state=this.errorInfo.get("SUCCESS");
		} catch ( Exception e ) {
			this.state=this.errorInfo.get("IO");
		}
		
	}
	
	public  String uploadFile(FileItem fileItem) throws Exception{
		String propertiesPath = request.getRealPath("/");

		Properties properties = new Properties();
		propertiesPath += "/help/js/jsp/config.properties";
		try {
		    properties.load( new FileInputStream( propertiesPath ) );
		} catch ( Exception e ) {
		    //加载失败的处理
		    e.printStackTrace();
		}
		

		String dirPath = "\\\\"+LeaseUtil.getIPAddress()+properties.getProperty("dirPath");
		
		if(this.savePath == null){
			this.savePath = this.getDefaultSavePath();	
		}
		
		dirPath += File.separator + this.savePath;
		
		String folder = this.getFolder();
		
		
		dirPath += File.separator + folder;
		
		File dir = new File(dirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		this.originalName = fileItem.getName();
		this.fileName = this.getName(this.originalName );
		dirPath += File.separator + this.fileName;
		File uploadFile  = new File(dirPath);
		fileItem.write(uploadFile);
		
		this.url = "&savePath=" + this.savePath; 		
		this.url += "&folder=" + folder; 
		this.url += "&fileName=" + this.fileName;
		this.state=this.errorInfo.get("SUCCESS");
		return "{'original':'"+this.originalName+"','url':'"+this.url+"','title':'"+this.originalName+"','state':'"+this.state+"'}";
	}
	/**
	 * 接受并保存以base64格式上传的文件
	 * @param fieldName
	 */
	public void uploadBase64(String fieldName){
		
		

		String propertiesPath = request.getRealPath("/help/js/jsp/config.properties");

		Properties properties = new Properties();

		try {
		    properties.load( new FileInputStream( propertiesPath ) );
		} catch ( Exception e ) {
		    //加载失败的处理
		    e.printStackTrace();
		}
		

		String filePath = "\\\\"+LeaseUtil.getIPAddress()+properties.getProperty("dirPath");

		if(this.savePath == null){
			this.savePath = this.getDefaultSavePath();			
		}
		filePath += File.separator + this.savePath;
		
		String folder = this.getFolder();
		this.url = "&savePath=" + this.savePath; 		
		this.url += "&folder=" + folder;
		filePath += File.separator + folder;
		
		File dir = new File(filePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String base64Data = this.request.getParameter(fieldName);
		this.fileName = this.getName("test.png");

		filePath += File.separator + this.fileName;
		this.url += "&fileName=" + this.fileName;
		
		BASE64Decoder decoder = new BASE64Decoder();
		try {

			File outFile = new File(filePath);
			OutputStream ro = new FileOutputStream(outFile);
			byte[] b = decoder.decodeBuffer(base64Data);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			ro.write(b);
			ro.flush();
			ro.close();
			this.state=this.errorInfo.get("SUCCESS");
		} catch (Exception e) {
			this.state = this.errorInfo.get("IO");
		}
	}

	public String getParameter ( String name ) {
		
		return this.params.get( name );
		
	}

	/**
	 * 文件类型判断
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileType(String fileName) {
		if(this.allowFiles == null){
			return true;
		}
		Iterator<String> type = Arrays.asList(this.allowFiles).iterator();
		while (type.hasNext()) {
			String ext = type.next();
			if (fileName.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	private String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	private void parseParams () {
		
		DiskFileItemFactory dff = new DiskFileItemFactory();
		try {
			ServletFileUpload sfu = new ServletFileUpload(dff);
			sfu.setSizeMax(this.maxSize);
			sfu.setHeaderEncoding("utf-8");
			
			FileItemIterator fii = sfu.getItemIterator(this.request);
			
			while (fii.hasNext()) {
				FileItemStream item = fii.next();
				//普通参数存储
        		if ( item.isFormField() ) {
        			
        			this.params.put( item.getFieldName(), this.getParameterValue( item.openStream()) );
        			
        		} else {
        			
        			//只保留一个
        			if ( this.fileBytes == null ) {
    					this.fileBytes = this.getFileOutputStream( item.openStream() );
    					this.originalName = item.getName();
        			}
        			
        		}
				
			}
			
		} catch (SizeLimitExceededException e) {
			this.state = this.errorInfo.get("SIZE");
		} catch (InvalidContentTypeException e) {
			this.state = this.errorInfo.get("ENTYPE");
		} catch (FileUploadException e) {
			this.state = this.errorInfo.get("REQUEST");
		} catch (Exception e) {
			this.state = this.errorInfo.get("UNKNOWN");
		}
		
	}

	/**
	 * 依据原始文件名生成新文件名
	 * @return
	 */
	private synchronized String getName(String fileName) {
		Random random = new Random();
		return this.fileName = "" + random.nextInt(10000)
				+ System.currentTimeMillis() + this.getFileExt(fileName);
	}

	
	private String getFolder(){
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		return formater.format(new Date());
	}
	private String getFolderNameForDownload(){
		
		return this.request.getParameter("folder");
	}
	private String getFileNameForDownload(){		
		return this.request.getParameter("fileName");
	}
	private String getSavePathForDownload(){		
		return this.request.getParameter("savePath");
	}

	
	
	/**
	 * 从输入流中获取字符串数据
	 * @param in 给定的输入流， 结果字符串将从该流中读取
	 * @return 从流中读取到的字符串
	 */
	private String getParameterValue ( InputStream in ) {
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		
		String result = "";
		String tmpString = null;
		
		try {
			
			while ( ( tmpString = reader.readLine() ) != null ) {
				result += tmpString;
			}
			
		} catch ( Exception e ) {
			//do nothing
		}

		return result;
		
	}
	
	private byte[] getFileOutputStream ( InputStream in ) {

		try {
			return IOUtils.toByteArray(in);
		} catch (IOException e) {
			return null;
		}
		
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public void setAllowFiles(String[] allowFiles) {
		this.allowFiles = allowFiles;
	}

	public void setMaxSize( long size ) {
		this.maxSize = size * 1024;
	}

	public String getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getState() {
		return this.state;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	public String getOriginalName() {
		return this.originalName;
	}
	
	private String getDefaultSavePath(){
		String propertiesPath = request.getRealPath("/help/js/jsp/config.properties");

		Properties properties = new Properties();

		try {
		    properties.load( new FileInputStream( propertiesPath ) );
		} catch ( Exception e ) {
		    //加载失败的处理
		    e.printStackTrace();
		}	
		List<String> savepaths = Arrays.asList(properties.getProperty( "savePath" ).split( "," ));
		
		return savepaths.get(0);

	}
	
	
	public String generateImgUrl(String filePath){
		String imageUrl = "&savePath=";
		String propertiesPath = request.getRealPath("/help/js/jsp/config.properties");
		Properties properties = new Properties();

		try {
		    properties.load(new FileInputStream(propertiesPath));
		} catch ( Exception e ) {
		    //加载失败的处理
		    e.printStackTrace();
		}
		String dirPath = "\\\\"+LeaseUtil.getIPAddress()+properties.getProperty("dirPath");
		
		List<String> savepaths = Arrays.asList( properties.getProperty("savePath").split( "," ));
		
		String savePath = "";
		for(String path:savepaths){
			savePath = dirPath + File.separator + path;
			if(filePath.indexOf(savePath) == 0){
				savePath = path;
				break;
			}
		}
		imageUrl += savePath;
		
		String [] paths = filePath.split("\\\\");
		
		imageUrl +="&folder=" + paths[paths.length-2];
		imageUrl +="&fileName=" + paths[paths.length-1];
		return imageUrl;
	}
}
