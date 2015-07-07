package com.brick.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * filter access checek
 * @author yangxuan
 *
 */
public class AccessFilter implements Filter {

	public void destroy() {
		System.out.println("destroy......");
	}
	 /**(non-Javadoc)
     *  
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) 
     */ 
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest)servletRequest;
//		HttpServletResponse response = (HttpServletResponse)servletResponse;
//		HttpSession session = request.getSession(false);
//		
//		if (session == null || session.getAttribute("user") == null) {
//			System.out.println("ContextPath==" + request.getContextPath());
//		
//			response.sendRedirect(request.getContextPath() + "/login.jsp");
//			//response.sendRedirect("/login.jsp");
//			return;
//		}
//		filterChain.doFilter(request, response);
	}
	/** (non-Javadoc) 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig) 
     */ 
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("init()");
	}

}
