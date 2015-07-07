package com.brick.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import com.brick.service.core.DataAccessor;
import com.ibatis.sqlmap.client.SqlMapClient;

import junit.framework.TestCase;

public class BaseTestCase extends TestCase {
	public ApplicationContext appContext;
	
	@Override
	protected void setUp() throws Exception {
		SqlMapClient sqlMapClient = null;
		BasicDataSource dataSource_JNDI = null;
		org.apache.commons.dbcp.BasicDataSource dataSource_JDBC = null;
		appContext = new ClassPathXmlApplicationContext("/config/app-context-test.xml");
		if (appContext != null) {
			Object sqlMapClientObj = appContext.getBean("sqlMapClient");
			if (sqlMapClientObj instanceof SqlMapClient) {
				sqlMapClient = (SqlMapClient) sqlMapClientObj;
				if (DataAccessor.getSession() == null) {
					System.out.println("setSqlMapper(" + sqlMapClient +")");
					DataAccessor.setSqlMapper(sqlMapClient);
				}
			}
			Object dataSourceObj = appContext.getBean("dataSource");
			if (dataSourceObj instanceof BasicDataSource) {
				dataSource_JNDI = (BasicDataSource) dataSourceObj;
				if (DataAccessor.JdbcUrl == null) {
					System.out.println("setJdbcUrl(" +dataSource_JNDI.getUrl() + ")");
					DataAccessor.setJdbcUrl(dataSource_JNDI.getUrl());
				}
			} else if(dataSourceObj instanceof org.apache.commons.dbcp.BasicDataSource) {
				dataSource_JDBC = (org.apache.commons.dbcp.BasicDataSource) dataSourceObj;
				if (DataAccessor.JdbcUrl == null) {
					System.out.println("setJdbcUrl(" +dataSource_JDBC.getUrl() + ")");
					DataAccessor.setJdbcUrl(dataSource_JDBC.getUrl());
				}
			}
		}
		super.setUp();
	}
	
	@Test
	public void testIsNull(){
		assertNotNull(appContext);
		org.apache.commons.dbcp.BasicDataSource dataSource = (org.apache.commons.dbcp.BasicDataSource) appContext.getBean("dataSource");
		System.out.println(dataSource.getUrl());
	}
	
//	public void testDailyJob(Context context){
//		LockManagementService lock=new LockManagementService();
//		lock.sendSmSToDunRentTime();
//		List<Map<String, Object>> resultList = null;
//		List<Map<String, Object>> listForCsv = new ArrayList<Map<String,Object>>();
//		BufferedWriter writer = null;
//		try {
//			resultList = (List<Map<String, Object>>) DataAccessor.query("yangYunTest.getAllPassed", null, DataAccessor.RS_TYPE.LIST);
//			File file = new File("D:/test/YangYunTest.csv");
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//			for (Map<String, Object> map : resultList) {
//				map.put("inter", InterestMarginUtil.getInterestMargin(String.valueOf(map.get("ID"))));
//				listForCsv.add(map);
//			}
//			StringBuffer sb = new StringBuffer("");
//			sb.append("CREDIT_ID");
//			sb.append(",");
//			sb.append("合同号");
//			sb.append(",");
//			sb.append("客户");
//			sb.append(",");
//			sb.append("办事处");
//			sb.append(",");
//			sb.append("客户经理");
//			sb.append(",");
//			sb.append("主管");
//			sb.append(",");
//			sb.append("核准日期");
//			sb.append(",");
//			sb.append("旧利差");
//			sb.append("\n");
//			for (Map<String, Object> map : listForCsv) {
//				sb.append(map.get("ID"));
//				sb.append(",");
//				sb.append(map.get("LEASE_CODE"));
//				sb.append(",");
//				sb.append(map.get("CUST_NAME"));
//				sb.append(",");
//				sb.append(map.get("DECP_NAME_CN"));
//				sb.append(",");
//				sb.append(map.get("USERNAME"));
//				sb.append(",");
//				sb.append(map.get("UPPER_USER"));
//				sb.append(",");
//				sb.append(map.get("WIND_RESULT_DATE"));
//				sb.append(",");
//				sb.append(map.get("inter"));
//				sb.append("\n");
//			}
//			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//			writer.write(sb.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				writer.flush();
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
}
