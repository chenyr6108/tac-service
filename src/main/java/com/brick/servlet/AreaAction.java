package com.brick.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.brick.service.core.AService;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;


/**
 * @author yangxuan
 * @version Created：2010-4-13 下午12:00:56
 *
 */

public class AreaAction extends HttpServlet {

	/**
	 * seriazabled
	 */
	private static final long serialVersionUID = 1L;

	private static final String CONTENT_TYPE = "text/html; charset=GBK";

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

		this.doPost(request, response);
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

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding("GBK");
		WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		/**area.createArea**/
		String action = HTMLUtil.getStrParam(request, "_action", "");
		String servicesName = null;
		String methodName = null;
		AService serviceInstance = null;
		if (null!=action || !"".equals(action)) {
			servicesName = action.split("\\.")[0];
			methodName = action.split("\\.")[1];
//			serviceInstance = (AService) AService.serviceMap.get(servicesName);
			serviceInstance = (AService) appContext.getBean(servicesName);
			Context context = new Context(request, response, this.getServletContext());
			HTMLUtil.fillMapByRequest(context.contextMap, request);
			HTMLUtil.fillMapByCookie(context.contextMap, request);
			HTMLUtil.fillMapBySession(context.contextMap, request);
			
			try {
				serviceInstance.doService(action, methodName, context);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} else {
			return;
		}
	}
}
