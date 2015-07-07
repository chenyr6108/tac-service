package com.brick.util.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.service.core.Output;

import flexjson.JSONSerializer;

public class CheckLoginFilter implements Filter {
	public static final String URI_QUERY_STRING = "uriQueryString";
	public static int i = 0;
	public static int j = 0;
	private Log log = LogFactory.getLog(CheckLoginFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hrequest = (HttpServletRequest) request;
		
		HttpSession session = hrequest.getSession();
		String uri = hrequest.getServletPath();
		String uri1 = hrequest.getRequestURI();
		String queryString = hrequest.getParameter("__action");
		
		/*if(!"/sys/acl/login.jsp".equals(uri)&&hrequest.getHeader("referer")==null) {
			//add by ShenQi,防止URL输入,因为框架中没有用_js window.open方法所有使用这个判断URL输入 暂时不加
			request.getRequestDispatcher("/sys/acl/login.jsp").forward(request,response);
		}*/

		 
		if(queryString==null){
			queryString="";
		}
		log.debug("uri is " + uri);
		log.debug("uri1 is " + uri1); 
		String code = (String) session.getAttribute("s_code"); 
		if(code == null && (uri1.indexOf("help/js/jsp/fileUp.js")>=0 || uri1.indexOf("help/js/jsp/imageManager.jsp")>=0 || queryString.indexOf("downloadImage")>=0)){
			code = "user_upload_file_for_helpDocument";//解决flash无法获取session　问题
		}
		if (queryString.equals("acl.login")|| uri.equals("/sys/acl/login.jsp")
				||"employeeCommand.checkUserId".equals(queryString)//未登录 重置密码 add by ShenQi
				||"employeeCommand.getPassword".equals(queryString)
				||"employeeCommand.resetPassword".equals(queryString)) {
			chain.doFilter(request, response);
		} else if ((code == null)) { 
			 if (uri.substring(0,9).equals("/servlet/") || uri.endsWith(".jsp")||uri.endsWith(".do")) {
				 if(uri.endsWith(".do")){
					 if(hrequest.getHeader("x-requested-with")!=null){//判断请求是否为AJAX
						 Map outputMap = new HashMap();
						 outputMap.put("session_tiemout", true);
						 JSONSerializer serializer = new JSONSerializer();
						 String json = serializer.serialize(outputMap);
						 response.getWriter().print(json);
						 return;
					 }
				 }
//			String url = fetchUrl(uri, queryString);
			request.getRequestDispatcher("/sys/welcome.jsp").forward(request,
					response);
//			session.setAttribute(URI_QUERY_STRING, url);
//			if (url.equals("/") || url.equals("/login/loginOut!loginOut.do")) {
//				session.removeAttribute(URI_QUERY_STRING);
//			}
			 } else {
			 chain.doFilter(request, response);
			 }
		} else {
			/**
			 List<String> operationList = (List) session
			 .getAttribute("operationList");			
			 if (uri.toLowerCase().endsWith(".do")
			 || uri.toLowerCase().endsWith(".jsp")
			 || uri.toLowerCase().endsWith("servlet")
			 || uri.toLowerCase().endsWith(".htm")
			 || uri.toLowerCase().endsWith(".html")) {
			 Iterator<String> it = operationList.iterator();
			 while (it.hasNext()) {
			 String urlStr = it.next();
			 if (fetchUrl(uri, queryString).indexOf(urlStr) != -1) {
			 request.getRequestDispatcher(
			 "/commons/error_nooperation.jsp").forward(
			 request, response);
			 }
			 }
			 }
			 */
			chain.doFilter(request, response);

		}
	}

	public String fetchUrl(String uri, String queryString) {
		String url;
		if (queryString != null) {
			url = uri + "?" + queryString;
		} else {
			url = uri;
		}
		return url;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
