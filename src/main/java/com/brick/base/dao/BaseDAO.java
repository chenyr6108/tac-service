package com.brick.base.dao;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.brick.base.exception.DaoException;
import com.brick.base.to.BaseTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.ClassUtil;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.service.entity.Context;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.scope.SessionScope;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public class BaseDAO extends SqlMapClientDaoSupport{

	Log logger = LogFactory.getLog(BaseDAO.class);
	
	private Map<String, SqlMapClient> sqlMapList;
	
	public Map<String, SqlMapClient> getSqlMapList() {
		return sqlMapList;
	}

	public void setSqlMapList(Map<String, SqlMapClient> sqlMapList) {
		this.sqlMapList = sqlMapList;
	}

	/**
	 * 分页查询
	 * 使用真分页方式查询
	 * 
	 * 注意：
	 * 1.由于SQLServer 用物理分页，必须要有一个字段排序，
	 * 所以必须提供一个排序的字段，作为参数。
	 * Ex. pagingInfo.setOrderBy("ID");
	 * 
	 * 2.由于用嵌套方式实现分页的sql语句，所以在sql语句结尾
	 * 不能写 order by.
	 * 
	 * 3.PageSize 默认20.
	 * 
	 * @param sqlId
	 * @param pagingInfo
	 * @return
	 * @throws SQLException
	 */
	public PagingInfo<Object> queryForListWithPaging(final String sqlId, PagingInfo<Object> pagingInfo) throws SQLException{
		List<Object> resultList = null;
		int pageNum = pagingInfo.getPageNo();
		String orderBy = pagingInfo.getOrderBy();
		Integer totalCount = null;
		Connection connection = null;
		SessionScope sessionScope = null;
		try {
			//获取SQL语句，和参数
			SqlMapClientImpl sci = (SqlMapClientImpl) this.getSqlMapClient();
			MappedStatement mappedStatement = sci.getDelegate().getMappedStatement(sqlId);
			Sql sql = mappedStatement.getSql();
			sessionScope = new SessionScope();
			sessionScope.incrementRequestStackDepth();
			StatementScope statementScope = new StatementScope(sessionScope);
			mappedStatement.initRequest(statementScope);
			//拿sql语句
			String strSql = sql.getSql(statementScope, pagingInfo.getParams());
			//拿参数
			Object[] params = sql.getParameterMap(statementScope, pagingInfo.getParams()).getParameterObjectValues(statementScope, pagingInfo.getParams());
			//拿配置sqlmap的resultClass
			Class ResultClass = sql.getResultMap(statementScope, pagingInfo.getParams()).getResultClass();
			//TotalCount
			connection = sci.getDataSource().getConnection();
			totalCount = this.getTotalCountForPaging(strSql, params, connection);
			//logger.info("TotalCount==>>" + totalCount);
			if (totalCount == null || totalCount == 0) {
				return pagingInfo;
			}
			pagingInfo.setTotalCount(totalCount);
			//判断当前页的有效性
			if (pageNum > pagingInfo.getTotalPage()) {
				pagingInfo.setPageNo(pagingInfo.getTotalPage());
			}
			if (pageNum < 1) {
				pagingInfo.setPageNo(1);
			}
			//排序字段 不能空
			if (StringUtils.isEmpty(orderBy)) {
				throw new DaoException(new Exception("order by 不能为空。"));
			}
			//再开一个连接
			if (connection != null) {
				if (!connection.isClosed()) {
					connection.close();
				}
				connection = null;
			}
			connection = sci.getDataSource().getConnection();
			resultList = this.getResultListForPaging(strSql, params, pagingInfo, ResultClass, connection);
			pagingInfo.setResultList(resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (sessionScope != null) {
				sessionScope.cleanup();
				sessionScope = null;
			}
		}
		return pagingInfo;
	}

	/**
	 * 分页查询
	 * 使用真分页方式查询(此方法主要用于复杂的sql,在查询总数的时候进行优化，需要多写一条查询count的语句，id的格式为****_count)
	 * 
	 * 注意：
	 * 1.由于SQLServer 用物理分页，必须要有一个字段排序，
	 * 所以必须提供一个排序的字段，作为参数。
	 * Ex. pagingInfo.setOrderBy("ID");
	 * 
	 * 2.由于用嵌套方式实现分页的sql语句，所以在sql语句结尾
	 * 不能写 order by.
	 * 
	 * 3.PageSize 默认20.
	 * 
	 * @param sqlId
	 * @param pagingInfo
	 * @return
	 * @throws SQLException
	 */
	public PagingInfo<Object> queryForListWithPagingForComplexSql(final String sqlId, PagingInfo<Object> pagingInfo) throws SQLException{
		List<Object> resultList = null;
		int pageNum = pagingInfo.getPageNo();
		String orderBy = pagingInfo.getOrderBy();
		Integer totalCount = null;
		Connection connection = null;
		SessionScope sessionScope = null;
		try {
			//获取SQL语句，和参数
			SqlMapClientImpl sci = (SqlMapClientImpl) this.getSqlMapClient();
			MappedStatement mappedStatement = sci.getDelegate().getMappedStatement(sqlId);
			Sql sql = mappedStatement.getSql();
			sessionScope = new SessionScope();
			sessionScope.incrementRequestStackDepth();
			StatementScope statementScope = new StatementScope(sessionScope);
			mappedStatement.initRequest(statementScope);
			//拿sql语句
			String strSql = sql.getSql(statementScope, pagingInfo.getParams());
			//拿参数
			Object[] params = sql.getParameterMap(statementScope, pagingInfo.getParams()).getParameterObjectValues(statementScope, pagingInfo.getParams());
			//拿配置sqlmap的resultClass
			Class ResultClass = sql.getResultMap(statementScope, pagingInfo.getParams()).getResultClass();
			//TotalCount
			connection = sci.getDataSource().getConnection();
			long x1 = System.currentTimeMillis();
			totalCount = (Integer) this.getSqlMapClientTemplate().queryForObject(sqlId+"_count",pagingInfo.getParams());
			long x2= System.currentTimeMillis();
			System.out.println("总数花费时间:"+(x2-x1));
			//logger.info("TotalCount==>>" + totalCount);
			if (totalCount == null || totalCount == 0) {
				return pagingInfo;
			}
			pagingInfo.setTotalCount(totalCount);
			//判断当前页的有效性
			if (pageNum > pagingInfo.getTotalPage()) {
				pagingInfo.setPageNo(pagingInfo.getTotalPage());
			}
			if (pageNum < 1) {
				pagingInfo.setPageNo(1);
			}
			//排序字段 不能空
			if (StringUtils.isEmpty(orderBy)) {
				throw new DaoException(new Exception("order by 不能为空。"));
			}
			//再开一个连接
			if (connection != null) {
				if (!connection.isClosed()) {
					connection.close();
				}
				connection = null;
			}
			connection = sci.getDataSource().getConnection();
			resultList = this.getResultListForPaging(strSql, params, pagingInfo, ResultClass, connection);
			pagingInfo.setResultList(resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			if (sessionScope != null) {
				sessionScope.cleanup();
				sessionScope = null;
			}
		}
		return pagingInfo;
	}
	/**
	 * 查询TotalCount
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private Integer getTotalCountForPaging(String sql, Object[] params, Connection connection) throws SQLException{
		Integer result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		sql = "select count(0) as total_count from (" + sql + ") t_count";
		//logger.info(sql);
		try {
			ps = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			if (rs == null) {
				return null;
			}
			while (rs.next()) {
				result = rs.getInt(1);
			}
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
	}
	
	/**
	 * 查询分页内容
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	private List<Object> getResultListForPaging(String sql, Object[] params, PagingInfo<Object> pagingInfo, 
			Class resultClass, Connection connection) throws Exception{
		List<Object> resultList = new ArrayList<Object>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String pagingSql = null;
		Map<String, Object> resultMap = null;
		Object resultObj = null;
		try {
			//拼装分页sql语句，运用sqlserver2005 的rownumber存储过程
			pagingSql = "select * from (select ROW_NUMBER() OVER(order by t_pag." + pagingInfo.getOrderBy() + " " + pagingInfo.getOrderType() + 
					") as rownum, t_pag.* from (" +
					sql + ") t_pag )t_pag_c where (t_pag_c.rownum - (" + pagingInfo.getPageNo() + " - 1) * " + 
					pagingInfo.getPageSize() + ")>0 and (t_pag_c.rownum - " + pagingInfo.getPageNo() + " * " + pagingInfo.getPageSize() + ")<=0 order by t_pag_c.rownum";
			//logger.info(pagingSql);
			//执行SQL
			ps = connection.prepareStatement(pagingSql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			if (rs == null) {
				return null;
			}
			//获取ResultSet 字段数据
			ResultSetMetaData md = rs.getMetaData();  
			int columnCount = md.getColumnCount();
			
			//如果是Map 就封装成Map
			if (HashMap.class.equals(resultClass)) {
				while (rs.next()) {
					resultMap = new HashMap(columnCount);  
					for (int i = 1; i <= columnCount; i ++){  
						resultMap.put(md.getColumnName(i).toUpperCase(),rs.getObject(i));  
					}  
					resultList.add(resultMap);  
				}
			} else {
				//不是Map，就调用Setter方法去封装To
				String colName = null;
				Object colValue = null;
				Method setter = null;
				Class<?> setMethodType = null;
				Object paramInvoke = null;
				while (rs.next()) {
					colName = null;
					resultObj = resultClass.newInstance();
					for (int i = 1; i <= columnCount; i ++){
						colName = md.getColumnName(i);
						colValue = rs.getObject(i);
						if (colValue == null) {
							logger.warn(colName + " = null;");
							continue;
						}
						if ("rownum".equalsIgnoreCase(colName)) {
							continue;
						}
						try {
							//获取setter方法
							setter = null;
							for (Method m : resultClass.getMethods()) {
								if (m.getName().equals(ClassUtil.getSetMethod(colName))) {
									setter = m;
									break;
								}
							}
							if (setter == null) {
								logger.warn("No Setter method in class [" + resultClass.getName() + "." + colName +"]");
								continue;
							}
							if (setter.getParameterTypes().length != 1) {
								logger.warn("No Setter method in class [" + resultClass.getName() + "." + colName +"]");
								continue;
							}
							setMethodType = setter.getParameterTypes()[0];
							//转换参数
							if (setMethodType.equals(String.class)) {
								paramInvoke = colValue.toString();
							} else if (setMethodType.equals(Integer.class)) {
								paramInvoke = Integer.valueOf(colValue.toString());
							} else if (setMethodType.equals(int.class)) {
								paramInvoke = Integer.valueOf(colValue.toString());
							} else if (setMethodType.equals(Double.class)) {
								paramInvoke = Double.valueOf(colValue.toString());
							} else if (setMethodType.equals(double.class)) {
								paramInvoke = Double.valueOf(colValue.toString());
							} else if (setMethodType.equals(BigDecimal.class)) {
								paramInvoke = new BigDecimal(colValue.toString());
							} else if (setMethodType.equals(Float.class)) {
								paramInvoke = Float.valueOf(colValue.toString());
							} else if (setMethodType.equals(float.class)) {
								paramInvoke = Float.valueOf(colValue.toString());
							} else if (setMethodType.equals(Long.class)) {
								paramInvoke = Long.valueOf(colValue.toString());
							} else if (setMethodType.equals(long.class)) {
								paramInvoke = Long.valueOf(colValue.toString());
							}  else if (setMethodType.equals(Boolean.class)) {
								paramInvoke = Boolean.valueOf(colValue.toString());
							} else if (setMethodType.equals(boolean.class)) {
								paramInvoke = Boolean.valueOf(colValue.toString());
							}else if (setMethodType.equals(Date.class)) {
								if (colValue instanceof java.sql.Date) {
									paramInvoke = new Date(((Date)colValue).getTime());
								} else {
									paramInvoke = new SimpleDateFormat("yyyy-MM-dd").parse(colValue.toString());
								}
							} else if (setMethodType.equals(java.sql.Date.class)) {
								if (colValue instanceof java.sql.Date) {
									paramInvoke = colValue;
								} else {
									if(colValue.toString().length()>10) {
										paramInvoke = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(colValue.toString()).getTime());
									} else {
										paramInvoke = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(colValue.toString()).getTime());
									}
								}
							} else if (setMethodType.equals(java.sql.Timestamp.class)) {
								if (colValue instanceof java.sql.Timestamp) {
									paramInvoke = colValue;
								}
							} else {
								logger.warn("类型不匹配");
								continue;
							}
						} catch (Exception e) {
							logger.warn(e.getMessage());
							continue;
						}
						if (setter != null) {
							setter.invoke(resultObj, paramInvoke);
						}
					}  
					resultList.add(resultObj);  
				}
			}
			return resultList;
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
	}
	
	public ResultSet queryBySql(String sql, Object[] params) throws DaoException, SQLException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getSqlMapClient().getDataSource().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs == null) {
				return null;
			}
			return rs;
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
		}
	}
	
	public List<? extends Object> queryForList(String sql_id, BaseTo baseTo) throws DaoException{
		try {
			return this.getSqlMapClientTemplate().queryForList(sql_id, baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<? extends Object> queryForList(String sql_id) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().queryForList(sql_id);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public List<? extends BaseTo> checkModifyDate(BaseTo baseTo) throws DaoException {
		try {
			return getSqlMapClientTemplate().queryForList("sys.getModifyDateTime", baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public List<? extends BaseTo> checkAuthDate(BaseTo baseTo) throws DaoException {
		try {
			return getSqlMapClientTemplate().queryForList("sys.getAuthDateTime", baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> queryForListReturnMap(String sql_id, BaseTo baseTo) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().queryForList(sql_id, baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public Object queryForListReturnObj(String sql_id, BaseTo baseTo) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().queryForObject(sql_id, baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public List<Map<String, Object>> queryForListUseMap(String sql_id,
			Map<String, Object> paramMap) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().queryForList(sql_id, paramMap);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public Object queryForObjUseMap(String sql_id, Map<String, Object> paramMap) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().queryForObject(sql_id, paramMap);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public void insertByTO(String sql_id, BaseTo baseTo) throws DaoException {
		try {
			this.getSqlMapClientTemplate().insert(sql_id, baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	//使用queryForPage只需在command中设置下面4个属性 add by ShenQi
	//	outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
	//	outputMap.put("currentPage",context.contextMap.get("currentPage"));
	//	outputMap.put("pageCount",context.contextMap.get("pageCount"));
	//	outputMap.put("pageSize",context.contextMap.get("pageSize"));
	public List<? extends BaseTo> queryForPage(String sqlIdQuery,String sqlIdCount,Context context,Map<String,Object> outputMap) throws DaoException {
		
		try {
			//初始化进入功能的时候 初始化totalPageSize总共有几条数据
			int totalPageSize=this.getTotalPageSize(sqlIdCount,context);
			//totalPageSize 总共多少条数据
			context.contextMap.put("pageTotalSize",totalPageSize);
			
			if((String)context.contextMap.get("pageSize")==null) {
				//初始化页面pagesize=10
				context.contextMap.put("pageSize","10");
			}
			
			int pageNum=Integer.valueOf((String)context.contextMap.get("currentPage")==null?"1":(String)context.contextMap.get("currentPage"));
			int pageSize=Integer.valueOf((String)context.contextMap.get("pageSize")==null?"10":(String)context.contextMap.get("pageSize"));
			//currentPage 第几页
			context.contextMap.put("currentPage",pageNum);
			//pageSize 每页显示多少条
			context.contextMap.put("pageSize",pageSize);
			//按照pageSize计算总共有几页
			context.contextMap.put("pageCount",this.getPageCount(totalPageSize,pageSize));
			
			outputMap.put("pageTotalSize",context.contextMap.get("pageTotalSize"));
			outputMap.put("currentPage",context.contextMap.get("currentPage"));
			outputMap.put("pageCount",context.contextMap.get("pageCount"));
			outputMap.put("pageSize",context.contextMap.get("pageSize"));
			return this.getSqlMapClientTemplate().queryForList(sqlIdQuery,context.contextMap,(pageNum-1)*pageSize,pageSize);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int getTotalPageSize(String sqlIdCount,Context context) throws DaoException {
		try {
			return (Integer)this.getSqlMapClientTemplate().queryForObject(sqlIdCount,context.contextMap);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int getPageCount(int totalPageSize,int pageSize) {
		
		int pageCount=0;
		if(totalPageSize<pageSize) {
			pageCount=1;
		}
		if(totalPageSize%pageSize>0&&totalPageSize/pageSize>=1) {
			pageCount=totalPageSize/pageSize+1;
		} else if(totalPageSize%pageSize>0&&totalPageSize/pageSize==0) {
			pageCount=1;
		} else if(totalPageSize%pageSize==0) {
			pageCount=totalPageSize/pageSize;
		}
		return pageCount;
	}
	
	public int update(String sqlId) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().update(sqlId);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public int update(String sqlId, Object o) throws DaoException {
		try {
			return this.getSqlMapClientTemplate().update(sqlId, o);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public Object queryForObject(String sqlId, BaseTo baseTo) throws DaoException {
		try {
			return getSqlMapClientTemplate().queryForObject(sqlId, baseTo);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	public String getDayType(String dateStr) throws DaoException {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("dateStr", dateStr);
			return (String) getSqlMapClientTemplate().queryForObject("businessSupport.getDayType", paramMap);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	//获得数据字典公用方法
	public List<Map<String,Object>> queryDataDictionary(Map<String,String> param) throws Exception {
		
		List<Map<String,Object>> result=null;
		
		result=this.getSqlMapClientTemplate().queryForList("dataDictionary.queryDataDictionary",param);
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	//获得company办事处公用方法,不含苏州办事处
	public List<Map<String,Object>> queryDept(Map<String,String> param) throws Exception {
		
		List<Map<String,Object>> result=null;
		
		result=this.getSqlMapClientTemplate().queryForList("customerVisit.getDeptList",param);
		if(result==null) {
			result=new ArrayList<Map<String,Object>>();
		}
		
		return result;
	}
	
	//获得company办事处公用方法,含苏州办事处
	public List<DeptCmpyTO> getCompanyList() throws Exception {
		
		List<DeptCmpyTO> companyList=null;
		
		companyList=this.getSqlMapClientTemplate().queryForList("common.getCompanyList");
		
		if(companyList==null) {
			companyList=new ArrayList<DeptCmpyTO>();
		}
		
		return companyList;
	}
	
	public List<DeptCmpyTO> getCompanyList1() throws Exception {
		
		List<DeptCmpyTO> companyList=null;
		
		companyList=this.getSqlMapClientTemplate().queryForList("common.getCompanyList1");
		
		if(companyList==null) {
			companyList=new ArrayList<DeptCmpyTO>();
		}
		
		return companyList;
	}
	
	public List<DeptCmpyTO> getDeptsList(Map<String,Object> param) {
		List<DeptCmpyTO> resultList=null;
		
		resultList=this.getSqlMapClientTemplate().queryForList("common.getDeptList",param);
		
		if(resultList==null) {
			resultList=new ArrayList<DeptCmpyTO>();
		}
		
		return resultList;
	}
	
	public Object insert(String sqlId, Object o) throws DaoException {
		try {
			return getSqlMapClientTemplate().insert(sqlId, o);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public Object insert(String sqlId) throws DaoException {
		try {
			return getSqlMapClientTemplate().insert(sqlId);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public Object delete(String sqlId, Object o) throws DaoException {
		try {
			return getSqlMapClientTemplate().delete(sqlId, o);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
	public Object delete(String sqlId) throws DaoException {
		try {
			return getSqlMapClientTemplate().delete(sqlId);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}
	
}
