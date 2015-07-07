package com.brick.servlet;

import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.brick.base.FormBeanComposer;
import com.brick.service.core.AService;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.FileInfo;
import com.brick.util.FileExcelUpload;
import com.brick.util.web.HTMLUtil;

public class DefaultDispatcher extends HttpServlet {
	
	Log logger = LogFactory.getLog(this.getClass());

	private static final long serialVersionUID = 2740693677625051632L;
	@SuppressWarnings("unchecked")
	private static Map fileRuleMap = new HashMap();
	private static Calendar calendar = Calendar.getInstance();
	private final String[] pictureTypes = new String[] { "bmp", "jpeg", "gif",
			"psd", "png" };

	public static boolean isMultiPart(HttpServletRequest request) {
		String content_type = request.getContentType();
		return content_type != null
				&& content_type.indexOf("multipart/form-data") != -1;
	}

	/**
	 * Constructor of the object.
	 */
	public DefaultDispatcher() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("__action");

		if ("queryUploadProcess".equals(action)) {
			this.queryUploadProcess(request, response);
		} else {
			this.doPost(request, response);
		}

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		if (isMultiPart(request)) {
			try {
				this.doMultiPost(request, response);
			} catch (Exception e) {
				// TODO
				e.printStackTrace();
			}
		} else {
			try {
				this.doApplicationPost(request, response);
			} catch (Exception e) {
				// TODO execute exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void doApplicationPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//PrintWriter out = response.getWriter();
		WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		String action = request.getParameter("__action");
		String second_id = request.getParameter("__second_id");
		if(second_id == null){
			second_id = (String) request.getSession().getAttribute("__second_id");
		}
		request.getSession().setAttribute("__second_id", second_id);
		AService service = null;
		String method = null;

		boolean blnConfigError = false;
		if (action == null) {
			blnConfigError = true;
		} else {

			String[] ac = action.split("\\.");

			if (ac.length == 2) {
//				service = (AService) AService.serviceMap.get(ac[0]);
				// service = (AService)ServicesBean.getBean(ac[0]);
				service = (AService)appContext.getBean(ac[0]);
				method = ac[1];
			}

			if (service == null || method == null) {
				blnConfigError = true;
			}
		}

		if (blnConfigError) {
			response.sendError(404, "�����ַ����");
			//out.print("�����ַ����");
			//out.flush();
			//out.close();
			return;
		}

		Context context = new Context(request, response, this
				.getServletContext());
		//2012/03/23 Yang Yun 增加FormBean
		FormBeanComposer formBeanComposer = (FormBeanComposer) appContext.getBean("formBeanComposer");
		if (formBeanComposer == null) {
			throw new Exception("FormBeanComposer is null.");
		}
		context.setFormBean(formBeanComposer.compose(request, action));
		
		HTMLUtil.fillMapByRequest(context.contextMap, request);
		// prefix "c_" before cookies
		HTMLUtil.fillMapByCookie(context.contextMap, request);
		// prefix "s_" before session
		HTMLUtil.fillMapBySession(context.contextMap, request);

		service.doService(action, method, context);

		//out.flush();
		//out.close();

	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void doMultiPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Context context = new Context(request, response, this
				.getServletContext());
		HTMLUtil.fillMapByRequest(context.contextMap, request);
		HTMLUtil.fillMapByCookie(context.contextMap, request);
		HTMLUtil.fillMapBySession(context.contextMap, request);
		Map contextMap = context.contextMap;

		// create file upload factory and upload servlet
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("utf-8");
		// set file upload progress listener
		FileUploadListener listener = new FileUploadListener();
		// upload servlet allows to set upload listener
		upload.setProgressListener(listener);

		HttpSession session = request.getSession();
		session.setAttribute("LISTENER", listener);
		session.setAttribute("UPLOADSTATE", "READY");

		File uploadedFile = null;
		List uploadedItems = null;
		FileItem fileItem = null;
		String fileName = null;
		String extName = null;
		String webFolderPath = null;
		// File realPath = null;
		String realPath = null;
		Map outputMap = new HashMap();
		List errList = context.errList;

		try {

			logger.info(request.getHeader("Content-Length"));

			String boundary = "--";
			boundary += request.getContentType().substring(
					request.getContentType().indexOf("=") + 1);// ��ȡ�ָ��

			logger.info(boundary);

			// String endBoundary = boundary + "--";
			// iterate over all uploaded files
			uploadedItems = upload.parseRequest(request);
			List uploadList=new ArrayList();
			List uploadStreamList=new ArrayList();
			Iterator i = uploadedItems.iterator();

			while (i.hasNext()) {

				fileItem = (FileItem) i.next();
	
				if (fileItem.isFormField()) {
					contextMap.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
				}
			}

			FileRule fileRule = (FileRule) fileRuleMap.get(contextMap.get("__action"));

			i = uploadedItems.iterator();
			while (i.hasNext()) {
				fileItem = (FileItem) i.next();

				if (!fileItem.isFormField()) {
					// 只支持图片的上传
					if ("pic".equals((String) contextMap.get("type"))) {
/*						String result = FileUpload
								.fileUpload(context, fileItem);
						if (result.equals("TitleIsNullError")) {
							errList.add("标题不能为空！");
							break;
						} else if (result.equals("FileNotFoundError")) {
							errList.add("上传文件路径不能为空！");
							break;
						} else if (result.equals("FileTypeError")) {
							errList.add("文件类型不正确！");
							break;
						} else if (result.equals("FileToBigError")) {
							context.errList.add("文件太大！");
							break;
						}*/
						context.getRequest().getSession().setAttribute("fileItem",fileItem);
						contextMap.put("picInputStream",fileItem.getInputStream());
					} else if ("excel".equals((String) contextMap.get("type"))) {// 如果上传的类型是excel
						// 1.判断上传的图片是否符合要求
						String result = FileExcelUpload.fileUpload(context,
								fileItem);
						if (result.equals("FileTypeError")) {
							errList.add("文件类型不正确！");
							break;
						} else if (result.equals("FileToBigError")) {
							context.errList.add("文件太大！");
							break;
						} else if (result.equals("FileNotFoundError")) {
							errList.add("上传文件路径不能为空！");
							break;
						}
						contextMap.put("excelInputStream",fileItem.getInputStream());
					} else{
							uploadList.add(fileItem);
							uploadStreamList.add(fileItem.getInputStream());
							
						}
					context.contextMap.put("uploadList",uploadList);
					context.contextMap.put("uploadStreamList",uploadStreamList);
					
				}
			}

		} catch (Exception e) {
			session.setAttribute("UPLOADSTATE", "ERROR");
			e.printStackTrace();
		}

		if (!errList.isEmpty()) {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
			return;
		}

		// do service
		String action = (String) contextMap.get("__action");
		AService service = null;
		String method = null;

		boolean blnConfigError = false;
		if (action == null) {
			blnConfigError = true;
		} else {

			String[] ac = action.split("\\.");

			if (ac.length == 2) {
				WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
				service = (AService)appContext.getBean(ac[0]);
				method = ac[1];
			}

			if (service == null || method == null) {
				blnConfigError = true;
			}
		}

		PrintWriter out = response.getWriter();
		if (blnConfigError) {
			response.sendError(404, "�����ַ����");
			out.print("�����ַ����");
			out.flush();
			out.close();
			return;
		}

		FileInfo fileInfo = new FileInfo();
		fileInfo.name = fileName;
		fileInfo.extName = extName;
		fileInfo.webPath = webFolderPath;
		// fileInfo.realPath = realPath.getPath();
		fileInfo.realPath = realPath;

		context.fileInfo = fileInfo;
		service.doService(action, method, context);

		out.flush();
		out.close();
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void queryUploadProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		StringBuffer buffy = new StringBuffer();
		long bytesRead = 0, contentLength = 0, item = 0;

		// Make sure the session has started
		FileUploadListener listener = (FileUploadListener) session
				.getAttribute("LISTENER");
		String uploadState = (String) session.getAttribute("UPLOADSTATE");

		buffy.append("{");

		if (uploadState == null) {

			buffy.append("UPLOADSTATE:'READY',");

		} else if ("READY".equals(uploadState)
				|| "RECIVING".equals(uploadState)) {

			// Get the meta information
			bytesRead = listener.getBytesRead();
			contentLength = listener.getContentLength();
			item = listener.getItem();

			buffy.append("bytesRead:" + bytesRead + ",");
			buffy.append("contentLength:" + contentLength + ",");
			buffy.append("item:" + item + ",");

			// Check to see if we're done
			if (bytesRead == contentLength) {
				buffy.append("percentComplete:100,");

				// No reason to keep listener in session since we're done
				session.setAttribute("LISTENER", null);
				session.setAttribute("UPLOADSTATE", null);
				buffy.append("UPLOADSTATE:'OK',");
			} else {
				// Calculate the percent complete
				long percentComplete = ((100 * bytesRead) / contentLength);

				session.setAttribute("UPLOADSTATE", "RECIVING");
				buffy.append("UPLOADSTATE:'RECIVING',");
				buffy.append("percentComplete:" + percentComplete + ",");
			}

		} else {
			// some thing is wrong
			session.setAttribute("UPLOADSTATE", null);
			buffy.append("UPLOADSTATE:'" + uploadState + "',");

		}

		buffy.append("p:0}");

		logger.info(buffy);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println(buffy.toString());
		out.flush();
		out.close();

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public void init() throws ServletException {
		String cfgFile = this.getInitParameter("upload-config");
		logger.info("load config from file "
				+ this.getServletContext().getRealPath(cfgFile));

		Document document = null;
		SAXReader reader = new SAXReader();

		try {
			document = reader.read(this.getServletContext()
					.getRealPath(cfgFile));
		} catch (Exception e) {
			logger.info("can not read config file \""
					+ this.getServletContext().getRealPath(cfgFile) + "\"\n"
					+ e.toString());
		}

		List<Element> elList = document.selectNodes("//configuration/action");
		for (Element el : elList) {

			FileRule fileRule = new FileRule();

			fileRule.actionName = el.selectSingleNode("./name").getText();
			el.selectSingleNode("./allowType").getText();
			el.selectSingleNode("./denyType").getText();
			fileRule.path = el.selectSingleNode("./path").getText();
			fileRule.maxSize = Long.parseLong(el.selectSingleNode("./maxSize")
					.getText());

			fileRuleMap.put(fileRule.actionName, fileRule);
		}

		logger.info("load completed");
	}

	private class FileRule {

		public String actionName;
		public String path;
		public long maxSize;
	}

}
