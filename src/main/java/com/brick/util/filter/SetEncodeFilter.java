package com.brick.util.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * the filter executes Character Filter  
 * @author yangxuan
 *
 */
public class SetEncodeFilter implements Filter {
	
	private String encoding = "utf-8";  
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
			HttpServletRequest request = (HttpServletRequest)servletRequest;
			HttpServletResponse  response = (HttpServletResponse)servletResponse;
			request.setCharacterEncoding(encoding);
			filterChain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.encoding = filterConfig.getInitParameter("encoding");	
	}

}
