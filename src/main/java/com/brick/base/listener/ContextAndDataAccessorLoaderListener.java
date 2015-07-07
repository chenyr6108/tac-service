package com.brick.base.listener;

import javax.servlet.ServletContextEvent;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ChartDirector.Chart;

import com.brick.service.core.DataAccessor;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ContextAndDataAccessorLoaderListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		SqlMapClient sqlMapClient = null;
		BasicDataSource dataSource_JNDI = null;
		org.apache.commons.dbcp.BasicDataSource dataSource_JDBC = null;
		WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		if (appContext != null) {
			Object sqlMapClientObj = appContext.getBean("tacSqlMapClient");
			if (sqlMapClientObj instanceof SqlMapClient) {
				sqlMapClient = (SqlMapClient) sqlMapClientObj;
				if (DataAccessor.getSession() == null) {
					//System.out.println("setSqlMapper(" + sqlMapClient +")");
					DataAccessor.setSqlMapper(sqlMapClient);
				}
			}
			Object dataSourceObj = appContext.getBean("dataSource");
			if (dataSourceObj instanceof BasicDataSource) {
				dataSource_JNDI = (BasicDataSource) dataSourceObj;
				if (DataAccessor.JdbcUrl == null) {
					//System.out.println("setJdbcUrl(" +dataSource_JNDI.getUrl() + ")");
					DataAccessor.setJdbcUrl(dataSource_JNDI.getUrl());
				}
			} else if(dataSourceObj instanceof org.apache.commons.dbcp.BasicDataSource) {
				dataSource_JDBC = (org.apache.commons.dbcp.BasicDataSource) dataSourceObj;
				if (DataAccessor.JdbcUrl == null) {
					//System.out.println("setJdbcUrl(" +dataSource_JDBC.getUrl() + ")");
					DataAccessor.setJdbcUrl(dataSource_JDBC.getUrl());
				}
			}
		}
		Chart.setLicenseCode("SXZVFNRN9MZ9L8LGA0E2B1BB");
	}
	
}
