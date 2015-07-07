package com.brick.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.util.FileUpload;
import com.brick.util.StringUtils;

public class ShowImg extends HttpServlet {
	
	Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("==========================================ShowImg==========================================");
		String file_name = request.getParameter("file_name");
		String file_path = FileUpload.getUploadPath("cropReportImg");
		if (StringUtils.isEmpty(file_name)) {
			throw new ServletException("没有文件名，不能显示。");
		}
		if (StringUtils.isEmpty(file_path)) {
			throw new ServletException("路径找不到。");
		}
		String perfectName = file_path + file_name;
		perfectName = perfectName.replace("\\", "/");
	    FileInputStream is = new FileInputStream(perfectName);
	    int i = is.available(); // 得到文件大小
	    byte data[] = new byte[i];
	    is.read(data); // 读数据
	    is.close();
	    response.setContentType("image/*"); // 设置返回的文件类型
	    OutputStream toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
	    toClient.write(data); // 输出数据
	    toClient.close();
	}

	
}
