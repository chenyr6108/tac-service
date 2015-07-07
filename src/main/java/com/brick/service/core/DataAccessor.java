package com.brick.service.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.brick.base.to.BaseTo;
import com.brick.service.entity.DataWrap;

public class DataAccessor {
	
	public static enum RS_TYPE {LIST, MAP, OBJECT, PAGED};
	public static enum OPERATION_TYPE {QUERY, INSERT, UPDATE, DELETE};
	
	private static Logger logger = Logger.getLogger(DataAccessor.class);	
	private static SqlMapClient sqlMapper;
	//Add BY Michael 2011 10/09 增加JDBC的URL 来判断是测试DB还是正式DB
	public static String JdbcUrl;
	/*static{
			
	    try {
		      Reader reader = Resources.getResourceAsReader("config/sql/sqlmap_cfg.xml");
		      sqlMapper = SqlMapClientBuilder.buildSqlMapClient(reader);
			    //-->--Add BY Michael 2011 10/09 增加JDBC的URL 来判断是测试DB还是正式DB
		      SimpleDataSource simpleDS=(SimpleDataSource) sqlMapper.getDataSource();
		      JdbcUrl=simpleDS.getJdbcUrl();
		      //
		      BasicDataSource simpleDS=(BasicDataSource) sqlMapper.getDataSource();
		      JdbcUrl=simpleDS.getUrl();
		    //--<-----------------------------------------
		      reader.close(); 
		    } catch (Exception e) {
		    	e.printStackTrace();
		      // Fail fast.
		      throw new RuntimeException("Something bad happened while building the SqlMapClient instance." + e, e);
		    }
	}	*/
	private DataAccessor(){}
	/**
	 * add by yangxuan
	 * @return
	 */
	public static SqlMapClient getSession() {
		return sqlMapper;
	}
	
	public static void setSqlMapper(SqlMapClient client){
		sqlMapper = client;
	}
	
	public static void setJdbcUrl(String url){
		JdbcUrl = url;
	}
	

	/**
	 * 
	 * @param sqlId
	 * @param paramMap
	 * @param rsType
	 * @return
	 * @throws Exception
	 */
	public static Object query(final String sqlId, Map paramMap, RS_TYPE rsType) throws Exception {
		// TODO Auto-generated method stub	
		Object resultObj = null;
		
		try{			
			switch(rsType){
				case LIST:
					resultObj = sqlMapper.queryForList(sqlId, paramMap);
					break;
					
				case MAP:
					resultObj = (Map)sqlMapper.queryForObject(sqlId, paramMap);	
					break;
				
				case OBJECT:
					resultObj = sqlMapper.queryForObject(sqlId, paramMap);
					break;
					
				case PAGED:	
					DataWrap dw = new DataWrap();				
					
					Integer count = (Integer)sqlMapper.queryForObject(sqlId + "_count", paramMap);
					dw.recordCount = count.intValue();
					
					
					int pageSize = -1;
					try{
						pageSize = Integer.parseInt((String)paramMap.get("__pageSize"));
					}catch(Exception e){
						//e.printStackTrace();
						pageSize = dw.pageSize;
					}
					
					int currentPage = -1;
					try{
						currentPage = Integer.parseInt((String)paramMap.get("__currentPage"));
						 
					}catch(Exception e){
						currentPage = 1;
					} 
					//old paged 
					PaginatedList pagedList = sqlMapper.queryForPaginatedList(sqlId, paramMap, pageSize);
					pagedList.gotoPage(currentPage-1);
					dw.pageCount = dw.recordCount / pageSize + (dw.recordCount % pageSize==0?0:1);		
					dw.pageSize=pageSize; 
					dw.currentPage=currentPage;
					List list = new LinkedList();			
					
					for(Map m: (List<Map>)pagedList){
						list.add(m);
					}
					
					dw.rs = list;
					
					resultObj = dw;
					
			}
				

			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getCause().getMessage().toString());
		}
		
		return resultObj;
	}
	
	public static List query(final String sqlId, BaseTo baseTo) throws Exception{
		return sqlMapper.queryForList(sqlId, baseTo);
	}
	public static Object queryForObj(final String sqlId, BaseTo baseTo) throws Exception{
		return sqlMapper.queryForObject(sqlId, baseTo);
	}
	
	
	/**
	 * 
	 * @param sqlId
	 * @param paramMap
	 * @param optionType
	 * @return
	 * @throws Exception
	 */
	public static Object execute(final String sqlId, Map paramMap, OPERATION_TYPE optionType) throws Exception {
		// TODO Auto-generated method stub	
		Object resultObj = null;
		
		try{
			//sqlMapper.startTransaction();
			switch(optionType){
				case INSERT:
					resultObj = sqlMapper.insert(sqlId, paramMap);
					break;
				case UPDATE:
					resultObj = sqlMapper.update(sqlId, paramMap);	
					break;
				
				case DELETE:
					resultObj = sqlMapper.delete(sqlId, paramMap);
					break;
			}
				
			//sqlMapper.commitTransaction();  
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getCause().getMessage().toString());
		} finally {
			//sqlMapper.endTransaction();
		}
		return resultObj;
	}

}
