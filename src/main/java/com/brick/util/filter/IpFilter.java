package com.brick.util.filter;
/**
 * ip filter
 * @author yangxuan
 * @since 1.0
 */
//import java.io.IOException;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;

//public class IpFilter implements Filter {
	
//	private FilterConfig filterConfig;
//	
//	public void destroy() {
//		// TODO Auto-generated method stub
//
//	}

	 /**(non-Javadoc)
     *  
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) 
     */ 
//	public void doFilter(ServletRequest req, ServletResponse res,
//			FilterChain chain) throws IOException, ServletException {
//		String ip = req.getRemoteAddr();
//		System.out.println("ip==" + ip);
//		String ips = filterConfig.getInitParameter("ip");
//		System.out.println(ips);
//		if (ips.indexOf(ip) != -1) {
//			res.getWriter().println("");
//			return;
//		}
//		chain.doFilter(req, res);
//	}
//
//	/** (non-Javadoc) 
//     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig) 
//     */ 
////	public void init(FilterConfig filterConfig) throws ServletException {
////		this.filterConfig = filterConfig;
////	}
//
//}
