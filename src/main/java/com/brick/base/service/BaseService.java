package com.brick.base.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.dao.BaseDAO;
import com.brick.base.exception.DaoException;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.BaseTo;
import com.brick.base.to.CheckedResult;
import com.brick.base.to.CreditLineTO;
import com.brick.base.to.DataDictionaryTo;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.SelectionTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.LeaseUtil.CREDIT_LINE_TYPE;
import com.brick.insurance.to.InsuCompanyTo;
import com.brick.log.service.LogPrint;
import com.brick.log.to.ActionLogTo;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.ibm.icu.text.SimpleDateFormat;

public class BaseService {
	
	Log logger = LogFactory.getLog(BaseService.class);
	
	public static enum ORDER_TYPE {ASC, DESC};
	
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Resource(name="baseDAO")
	public BaseDAO baseDAO;

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	public List<? extends Object> queryForList(String sql_id, BaseTo baseTo) throws ServiceException{
		try {
			return baseDAO.queryForList(sql_id, baseTo);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		
	}
	
	public List<? extends Object> queryForList(String sql_id) throws ServiceException {
		try {
			return baseDAO.queryForList(sql_id);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<? extends Object> queryForList(String sql_id, Map<String, Object> paramMap) throws ServiceException{
		try {
			return baseDAO.queryForListUseMap(sql_id, paramMap);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		
	}
	
	public Object queryForObj(String sql_id) throws ServiceException{
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			return baseDAO.queryForObjUseMap(sql_id, paramMap);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		
	}
	
	public Object queryForObj(String sql_id, BaseTo baseTo) throws ServiceException{
		try {
			return baseDAO.queryForListReturnObj(sql_id, baseTo);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		
	}
	
	public Object queryForObj(String sql_id, Map<String, Object> paramMap) throws ServiceException{
		try {
			return baseDAO.queryForObjUseMap(sql_id, paramMap);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		
	}
	
	public void insertByTO(String sql_id, BaseTo baseTo) throws ServiceException{
		try {
			baseDAO.insertByTO(sql_id, baseTo);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 验证modifyDate
	 * @param baseTo
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkModifyDateIsEq(BaseTo baseTo) throws ServiceException{
		boolean flag = false;
		try {
			if (StringUtils.isEmpty(baseTo.getTable_name())) {
				throw new ServiceException("没有表名，不能验证！");
			}
			if (StringUtils.isEmpty(baseTo.getPrimary_key())) {
				throw new ServiceException("没有主键名，不能验证！");
			}
			if (StringUtils.isEmpty(baseTo.getKey_value())) {
				throw new ServiceException("没有主键值，不能验证！");
			}
			List<BaseTo> result = (List<BaseTo>) baseDAO.checkModifyDate(baseTo);
			if (baseTo.getModify_date() != null) {
				if (result != null && result.size() == 1) {
					if (result.get(0).getModify_date() != null &&
							result.get(0).getModify_date().getTime() == baseTo.getModify_date().getTime()) {
						flag = true;
					}
				}
			} else {
				if (result != null && result.size() == 1 && result.get(0).getModify_date() == null) {
					flag = true;
				}
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}
	
	/**
	 * 验证评审时间
	 * @param baseTo
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkAuthDateIsEq(BaseTo baseTo) throws ServiceException{
		boolean flag = false;
		try {
			if (StringUtils.isEmpty(baseTo.getTable_name())) {
				throw new ServiceException("没有表名，不能验证！");
			}
			if (StringUtils.isEmpty(baseTo.getPrimary_key())) {
				throw new ServiceException("没有主键名，不能验证！");
			}
			if (StringUtils.isEmpty(baseTo.getKey_value())) {
				throw new ServiceException("没有主键值，不能验证！");
			}
			List<BaseTo> result = (List<BaseTo>) baseDAO.checkAuthDate(baseTo);
			if (baseTo.getAuth_date() != null) {
				if (result != null && result.size() == 1) {
					if (result.get(0).getAuth_date() != null &&
							result.get(0).getAuth_date().getTime() == baseTo.getAuth_date().getTime()) {
						flag = true;
					}
				}
			} else {
				if (result != null && result.size() == 1 && result.get(0).getAuth_date() == null) {
					flag = true;
				}
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}

	/**
	 * 验证有无权限
	 * @param baseTo
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkAccessForResource(BaseTo baseTo) throws ServiceException{
		boolean flag = false;
		try {
			if (StringUtils.isEmpty(baseTo.getModify_by())) {
				throw new Exception("没有给我用户ID，我怎么验证啊？");
			}
			if (StringUtils.isEmpty(baseTo.getResource_code())) {
				throw new Exception("没有给我资源编号，我怎么验证啊？");
			}
			Integer result = (Integer) baseDAO.queryForObject("permission.checkAccessForResByCode", baseTo);
			if (result != null && result > 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}
	
	/**
	 * 验证有无权限
	 * @param resource_code 资源编号
	 * @param user_id 用户ID
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkAccessForResource(String resource_code, String user_id) throws ServiceException{
		boolean flag = false;
		try {
			if (StringUtils.isEmpty(user_id)) {
				throw new Exception("没有给我用户ID，我怎么验证啊？");
			}
			if (StringUtils.isEmpty(resource_code)) {
				throw new Exception("没有给我资源编号，我怎么验证啊？");
			}
			BaseTo baseTo = new BaseTo();
			baseTo.setResource_code(resource_code);
			baseTo.setModify_by(user_id);
			Integer result = (Integer) baseDAO.queryForObject("permission.checkAccessForResByCode", baseTo);
			if (result != null && result > 0) {
				flag = true;
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return flag;
	}
	
	public int update(String sqlId) throws ServiceException {
		try {
			return baseDAO.update(sqlId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	public int update(String sqlId, Object o) throws ServiceException {
		try {
			return baseDAO.update(sqlId, o);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	public void delete(String sqlId, Object o) throws ServiceException {
		try {
			baseDAO.delete(sqlId, o);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	public void delete(String sqlId) throws ServiceException {
		try {
			baseDAO.delete(sqlId);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	public List<SelectionTo> getAllOffice() throws ServiceException{
		List<SelectionTo> result = null;
		try {
			result = (List<SelectionTo>) baseDAO.queryForList("department.getAllOffice");
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		return result;
	}
	
	public List<SelectionTo> getAllCust() {
		List<SelectionTo> result = null;
		try {
			result = (List<SelectionTo>) baseDAO.queryForList("businessSupport.getAllCust");
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		return result;
	}
	
	public List<SelectionTo> getAllSupl() {
		List<SelectionTo> result = null;
		try {
			result = (List<SelectionTo>) baseDAO.queryForList("businessSupport.getAllSupl");
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		return result;
	}
	
	/**
	 * 查询用户所在的办事处，以“,”隔开
	 * @param user_id
	 * @return
	 * @throws ServiceException
	 */
	public String getCurrentOffice(String user_id) throws ServiceException{
		String result = null;
		List<String> resultList = null;
		StringBuffer sb = null;
		try {
			BaseTo baseTo = new BaseTo();
			baseTo.setModify_by(user_id);
			resultList = (List<String>) baseDAO.queryForList("department.getCurrentOffice", baseTo);
			if (resultList == null || resultList.size() == 0) {
				throw new Exception("未找到对应的办事处。");
			}
			sb = new StringBuffer();
			for (String string : resultList) {
				sb.append(string);
				sb.append(",");
			}
			result = sb.substring(0, sb.length() - 1);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return result;
	}
	
	/**
	 * 当天是否是工作日
	 * @return
	 * @throws Exception
	 */
	public boolean isWorkingDay() throws Exception{
		boolean flag = false;
		try {
			String dayType = baseDAO.getDayType(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			if ("WD".equals(dayType)) {
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 是否工作日
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public boolean isWorkingDay(Date date) throws Exception{
		boolean flag = false;
		try {
			String dayType = baseDAO.getDayType(new SimpleDateFormat("yyyy-MM-dd").format(date));
			if ("WD".equals(dayType)) {
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 插入一条非业务日志
	 * @param actionLogTo
	 * @throws ServiceException
	 */
	public void insertActionLog(ActionLogTo actionLogTo) throws ServiceException{
		try {
			baseDAO.insertByTO("actionLog.insertActionLog", actionLogTo);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 插入一条非业务日志
	 * @param context
	 * @param logAction
	 * @param logContent
	 * @throws ServiceException
	 */
	public void insertActionLog(Context context, String logAction, String logContent) throws ServiceException{
		try {
			ActionLogTo actionLogTo = new ActionLogTo((String) context.contextMap.get("s_employeeName"), 
					logAction, logContent, context.contextMap.get("IP").toString());
			baseDAO.insertByTO("actionLog.insertActionLog", actionLogTo);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 分页查询<br>
	 * 使用真分页方式查询
	 * <br>
	 * 注意：
	 * 1.由于SQLServer 用物理分页，必须要有一个字段排序，
	 * 所以必须提供一个排序的字段，作为参数。
	 * Ex. pagingInfo.setOrderBy("ID");
	 * <br>
	 * 2.由于用嵌套方式实现分页的sql语句，所以在sql语句结尾
	 * 不能写 order by.
	 * <br>
	 * 3.PageSize 默认20.
	 * <br>
	 * @param sqlId
	 * @param paramMap
	 * @param defOrderBy --默认的排序字段，paramMap中的排序信息优先。
	 * @param defOrderType --ASC, DESC 默认是ASC
	 * @return
	 * @throws ServiceException
	 */
	public PagingInfo<Object> queryForListWithPaging(String sqlId, Map<String, Object> paramMap, String defOrderBy, ORDER_TYPE defOrderType) throws ServiceException{
		try {
//			Date begin = new Date();
			PagingInfo<Object> pagingInfo = new PagingInfo<Object>();
			if (!StringUtils.isEmpty((String) paramMap.get("__currentPage"))) {
				pagingInfo.setPageNo(Integer.parseInt((String) paramMap.get("__currentPage")));
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__pageSize"))) {
				pagingInfo.setPageSize(Integer.parseInt((String) paramMap.get("__pageSize")));
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__orderBy"))) {
				pagingInfo.setOrderBy((String) paramMap.get("__orderBy"));
			} else {
				pagingInfo.setOrderBy(defOrderBy);
			}
			if (StringUtils.isEmpty(pagingInfo.getOrderBy())) {
				throw new Exception("Order by 不能为空。");
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__orderType"))) {
				pagingInfo.setOrderType((String) paramMap.get("__orderType"));
			} else if (defOrderType == ORDER_TYPE.DESC) {
				pagingInfo.setOrderType("DESC");
			}
			pagingInfo.setParams(paramMap);
			pagingInfo = baseDAO.queryForListWithPaging(sqlId, pagingInfo);
//			Date endTime = new Date();
//			System.out.println(endTime.getTime() - begin.getTime());
			return pagingInfo;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	public PagingInfo<Object> queryForListWithPagingMVC(String sqlId,HttpServletRequest request, Map<String, Object> paramMap, String defOrderBy, ORDER_TYPE defOrderType) throws ServiceException{
		try {

			PagingInfo<Object> pagingInfo = new PagingInfo<Object>();
			if (!StringUtils.isEmpty(request.getParameter("__currentPage"))) {
				pagingInfo.setPageNo(Integer.parseInt(request.getParameter("__currentPage")));
			}
			if (!StringUtils.isEmpty(request.getParameter("__pageSize"))) {
				pagingInfo.setPageSize(Integer.parseInt(request.getParameter("__pageSize")));
			}
			if (!StringUtils.isEmpty(request.getParameter("__orderBy"))) {
				pagingInfo.setOrderBy(request.getParameter("__orderBy"));
			} else {
				pagingInfo.setOrderBy(defOrderBy);
			}
			if (StringUtils.isEmpty(pagingInfo.getOrderBy())) {
				throw new Exception("Order by 不能为空。");
			}
			if (!StringUtils.isEmpty(request.getParameter("__orderType"))) {
				pagingInfo.setOrderType(request.getParameter("__orderType"));
			} else if (defOrderType == ORDER_TYPE.DESC) {
				pagingInfo.setOrderType("DESC");
			}
			pagingInfo.setParams(paramMap);
			pagingInfo = baseDAO.queryForListWithPaging(sqlId, pagingInfo);
//			Date endTime = new Date();
//			System.out.println(endTime.getTime() - begin.getTime());
			return pagingInfo;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 分页查询<br>
	 * 使用真分页方式查询(此方法主要用于复杂的sql,在查询总数的时候进行优化，需要多写一条查询count的语句，id的格式为****_count)
	 * <br>
	 * 注意：
	 * 1.由于SQLServer 用物理分页，必须要有一个字段排序，
	 * 所以必须提供一个排序的字段，作为参数。
	 * Ex. pagingInfo.setOrderBy("ID");
	 * <br>
	 * 2.由于用嵌套方式实现分页的sql语句，所以在sql语句结尾
	 * 不能写 order by.
	 * <br>
	 * 3.PageSize 默认20.
	 * <br>
	 * @param sqlId
	 * @param paramMap
	 * @param defOrderBy --默认的排序字段，paramMap中的排序信息优先。
	 * @param defOrderType --ASC, DESC 默认是ASC
	 * @return
	 * @throws ServiceException
	 */
	public PagingInfo<Object> queryForListWithPagingForComplexSql(String sqlId, Map<String, Object> paramMap, String defOrderBy, ORDER_TYPE defOrderType) throws ServiceException{
		try {
//			Date begin = new Date();
			PagingInfo<Object> pagingInfo = new PagingInfo<Object>();
			if (!StringUtils.isEmpty((String) paramMap.get("__currentPage"))) {
				pagingInfo.setPageNo(Integer.parseInt((String) paramMap.get("__currentPage")));
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__pageSize"))) {
				pagingInfo.setPageSize(Integer.parseInt((String) paramMap.get("__pageSize")));
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__orderBy"))) {
				pagingInfo.setOrderBy((String) paramMap.get("__orderBy"));
			} else {
				pagingInfo.setOrderBy(defOrderBy);
			}
			if (StringUtils.isEmpty(pagingInfo.getOrderBy())) {
				throw new Exception("Order by 不能为空。");
			}
			if (!StringUtils.isEmpty((String) paramMap.get("__orderType"))) {
				pagingInfo.setOrderType((String) paramMap.get("__orderType"));
			} else if (defOrderType == ORDER_TYPE.DESC) {
				pagingInfo.setOrderType("DESC");
			}
			pagingInfo.setParams(paramMap);
			pagingInfo = baseDAO.queryForListWithPagingForComplexSql(sqlId, pagingInfo);

			return pagingInfo;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	/**
	 * 分页查询<br>
	 * 使用真分页方式查询
	 * <br>
	 * 注意：
	 * 1.由于SQLServer 用物理分页，必须要有一个字段排序，
	 * 所以必须提供一个排序的字段，作为参数。
	 * Ex. pagingInfo.setOrderBy("ID");
	 * <br>
	 * 2.由于用嵌套方式实现分页的sql语句，所以在sql语句结尾
	 * 不能写 order by.
	 * <br>
	 * 3.PageSize 默认20.
	 * <br>
	 * @param sqlId
	 * @param paramMap
	 * @param orderBy --默认的排序字段，paramMap中的排序信息优先。
	 * @return
	 * @throws ServiceException
	 */
	public PagingInfo<Object> queryForListWithPaging(String sqlId, Map<String, Object> paramMap, String defOrderBy) throws ServiceException{
		try {
			return this.queryForListWithPaging(sqlId, paramMap, defOrderBy, ORDER_TYPE.ASC);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public Object insert(String sqlId, Object o) throws ServiceException {
		try {
			return baseDAO.insert(sqlId, o);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public Object insert(String sqlId) throws ServiceException {
		try {
			return baseDAO.insert(sqlId);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 判断案件是否过期
	 * true = 未过期；false = 过期
	 * @param credit_id
	 * @return
	 * @throws ServiceException
	 */
	public boolean getIsExpiredByCreditId(String credit_id) throws ServiceException{
		boolean flag = false;
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int validDay = 0;
		int dateDiff = 0;
		try {
			if (StringUtils.isEmpty(credit_id)) {
				throw new ServiceException("credit_id is null.");
			}
			paramMap.put("credit_id", credit_id);
			
			//Add by Michael 2012 09-11 增加判断是否是设备款的首拨款,如果是设备款的首拨款就要卡有效期，否则就不卡
			Long payMentNum=(Long) queryForObj("businessSupport.isFirstPayMent", paramMap);
			
			if (payMentNum==0){
				resultMap = (Map<String, Object>) queryForObj("businessSupport.getDateDiff", paramMap);
				if (resultMap != null && resultMap.get("VALID_DAY") != null && resultMap.get("DATEDIFF") != null) {
					validDay = (Integer) resultMap.get("VALID_DAY");
					dateDiff = (Integer) resultMap.get("DATEDIFF");
					if (validDay >= dateDiff) {
						flag = true;
					}
				}
			}else{
				flag = true;
			}
			
		} catch (ServiceException e) {
			throw e;
		}
		return flag;
	}
	
	/**
	 * 判断案件是否过期
	 * @param credit_runcode
	 * @return
	 * @throws ServiceException
	 */
	public boolean getIsExpiredByCreditRuncode(String credit_runcode) throws ServiceException{
		boolean flag = false;
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		int validDay = 0;
		int dateDiff = 0;
		try {
			if (StringUtils.isEmpty(credit_runcode)) {
				throw new ServiceException("credit_runcode is null.");
			}
			paramMap.put("credit_runcode", credit_runcode);
			resultMap = (Map<String, Object>) queryForObj("businessSupport.getDateDiff", paramMap);
			if (resultMap != null && resultMap.get("VALID_DAY") != null && resultMap.get("DATEDIFF") != null) {
				validDay = (Integer) resultMap.get("VALID_DAY");
				dateDiff = (Integer) resultMap.get("DATEDIFF");
				if (validDay >= dateDiff) {
					flag = true;
				}
			}
		} catch (ServiceException e) {
			throw e;
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllDecp() throws ServiceException {
		List<Map<String, Object>> result = null;
		try {
			result = (List<Map<String, Object>>) baseDAO.queryForList("department.getAllDecp");
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		return result;
	}
	
	public List<InsuCompanyTo> getAllIncp() throws ServiceException{
		List<InsuCompanyTo> incps = null;
		try {
			incps = (List<InsuCompanyTo>) baseDAO.queryForList("insurance.getAllIncp");
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
		return incps;
	}
	
	/**
	 * 获取某月的第一个工作日
	 * @param year
	 * @param month
	 * @return
	 * @throws ServiceException
	 */
	public Date getFirstWorkingDayFromMonth(int year, int month) throws ServiceException{
		java.sql.Date d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", year);
		paramMap.put("month", month);
		d = (java.sql.Date) queryForObj("job.getFirstWorkingDay", paramMap);
		return d;
	}
	
	public Date getFirstWorkingDayFromMonth(Date date) throws ServiceException{
		java.sql.Date d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", DateUtil.dateToString(date, "yyyy"));
		paramMap.put("month", DateUtil.dateToString(date, "MM"));
		d = (java.sql.Date) queryForObj("job.getFirstWorkingDay", paramMap);
		return d;
	}
	
	/**
	 * 第二个工作日
	 * @param date
	 * @return
	 * @throws ServiceException
	 */
	public Date getSecondWorkingDayFromMonth(Date date) throws ServiceException{
		java.sql.Date d = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", DateUtil.dateToString(date, "yyyy"));
		paramMap.put("month", DateUtil.dateToString(date, "MM"));
		d = (java.sql.Date) queryForObj("job.getSecondWorkingDay", paramMap);
		return d;
	}
	
	/**
	 * 是否第一个工作日
	 * @return
	 */
	public boolean isTheFirstWorkingDay(){
		boolean flag = false;
		Date today = new Date();
		System.out.println(today);
		try {
			System.out.println("今天：===>>[" + DateUtil.dateToString(today, "yyyy-MM-dd") + "],第一个工作日：[" + DateUtil.dateToString(getFirstWorkingDayFromMonth(today), "yyyy-MM-dd") + "]");
			if (DateUtil.dateToString(today, "yyyy-MM-dd").equals(DateUtil.dateToString(getFirstWorkingDayFromMonth(today), "yyyy-MM-dd"))) {
				flag = true;
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return flag;
	}
	
	/**
	 * 是否第二个工作日
	 * @return
	 */
	public boolean isTheSecondWorkingDay(){
		boolean flag = false;
		Date today = new Date();
		try {
			System.out.println("今天：===>>[" + DateUtil.dateToString(today, "yyyy-MM-dd") + "],第二个工作日：[" + DateUtil.dateToString(getSecondWorkingDayFromMonth(today), "yyyy-MM-dd") + "]");
			if (DateUtil.dateToString(today, "yyyy-MM-dd").equals(DateUtil.dateToString(getSecondWorkingDayFromMonth(today), "yyyy-MM-dd"))) {
				flag = true;
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return flag;
	}
	
	public Date getTheFirstWorkingDayOfWeek(){
		return (java.sql.Date) this.queryForObj("job.getTheFirstWorkingDayOfWeek");
	}
	
	public boolean isTheFirstWorkingDayOfWeek(){
		boolean flag = false;
		Date today = new Date();
		try {
			System.out.println("今天：===>>[" + DateUtil.dateToString(today, "yyyy-MM-dd") + "],本周第一个工作日：[" + DateUtil.dateToString(getTheFirstWorkingDayOfWeek(), "yyyy-MM-dd") + "]");
			if (DateUtil.dateToString(today, "yyyy-MM-dd").equals(DateUtil.dateToString(getTheFirstWorkingDayOfWeek(), "yyyy-MM-dd"))) {
				flag = true;
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return flag;
	}
	
	/**
	 * 数据字典表
	 * @param type
	 * @return
	 * @throws ServiceException
	 */
	public List<DataDictionaryTo> getDataDictionaryByType(String type) throws ServiceException{
		List<DataDictionaryTo> result = null;
		DataDictionaryTo paramTo = new DataDictionaryTo();
		paramTo.setType(type);
		result = (List<DataDictionaryTo>) queryForList("dataDictionary.getDataByType", paramTo);
		return result;
	}
	
	/**
	 * 数据字典表flag转code
	 * @param type
	 * @param flag
	 * @return
	 * @throws ServiceException
	 */
	public String getDataDictionaryCodeByFlag(String type, String flag) throws ServiceException{
		String result = null;
		DataDictionaryTo paramTo = new DataDictionaryTo();
		paramTo.setType(type);
		paramTo.setFlag(flag);
		result = (String) queryForObj("dataDictionary.getDataByFlag", paramTo);
		return result;
	}
	
	/**
	 * 数据字典表code转flag
	 * @param type
	 * @param code
	 * @return
	 * @throws ServiceException
	 */
	public String getDataDictionaryFlagByCode(String type, String code) throws ServiceException{
		String result = null;
		DataDictionaryTo paramTo = new DataDictionaryTo();
		paramTo.setType(type);
		paramTo.setCode(code);
		result = (String) queryForObj("dataDictionary.getDataByCode", paramTo);
		return result;
	}
	
	/**
	 * 数据字典表转下拉列表
	 * @param type
	 * @return
	 * @throws ServiceException
	 */
	public List<SelectionTo> getDataDictionaryForSelect(String type) throws ServiceException{
		List<SelectionTo> result = null;
		DataDictionaryTo paramTo = new DataDictionaryTo();
		paramTo.setType(type);
		result = (List<SelectionTo>) queryForList("dataDictionary.getDataByTypeForSelect", paramTo);
		return result;
	}
	
	/**
	 * 添加系统业务日志
	 * 
	 * @param creditId 资信id
	 * @param contractId 合同ID
	 * @param logType 日志类型（如：现场调查报告）
	 * @param logTitle日志标题（如：生成）
	 * @param logCode 编号（生成对象的编号）
	 * @param memo  备注
	 * @param state  状态 1 使用中 2未使用
	 * @param userId 创建人 
	 * @param otherId  其他ID（备用）
	 * @param ip IP
	 * @throws ServiceException 
	 */
	public void addBusinessLogWithAll (Long creditId, Long contractId,
			String logType, String logTitle, String logCode, String memo,
			Integer state, Long userId, Long otherId ,String ip) throws ServiceException{
		Map<String, Object> map = new HashMap<String, Object>();
		if (creditId == null) {
			map.put("creditId", "");
		} else {
			map.put("creditId", creditId);
		}
		if (contractId == null) {
			map.put("contractId", "");
		} else {
			map.put("contractId", contractId);
		}
		map.put("logType", logType);
		map.put("logTitle", logTitle);
		map.put("logCode", logCode);
		map.put("memo", memo);
		map.put("state", state);
		map.put("userId", userId);
		map.put("ip", ip);
		if (otherId == null) {
			map.put("otherId", "");
		} else {
			map.put("otherId", otherId);
		}
		try {
			insert("sysBusinessLog.add", map);
		} catch (ServiceException e) {
			logger.error(e);
			throw e;
		}
	}
	
	public void addBusinessLog(String creditId, String logType, String logTitle, String memo, String userId, String ip){
		try {
			addBusinessLogWithAll(
					Long.parseLong(creditId)
					,null
					,logType
					,logTitle
					,""
					,memo
					,1
					,Long.parseLong(userId)
					,null
					,ip
				);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceException("记录日志失败");
		}
		
	}
	
	public void addBusinessLog(String creditId, String logType, String logTitle, String memo, HttpSession session){
		try {
			addBusinessLogWithAll(
					Long.parseLong(creditId)
					,null
					,logType
					,logTitle
					,""
					,memo
					,1
					,Long.parseLong(String.valueOf(session.getAttribute("s_employeeId")))
					,null
					,String.valueOf(session.getAttribute("IP"))
				);
		} catch (Exception e) {
			logger.error(e);
			throw new ServiceException("记录日志失败");
		}
		
	}
	
	/**
	 * 发送短信
	 * @param context
	 * @param msgList
	 * @param sqlMapper
	 * @return
	 */
	public String SendSMSMsg(Context context,List msgList) {
		String end = "短信发送失败";
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				if(msgList.size()>0){
					for(int i=0;i<msgList.size();i++){
						Map entityMap=(Map)msgList.get(i);
						Integer id= (Integer)insert("lockManagement.createSendMsg", entityMap);
						entityMap.put("SENDSMS", id);
						insert("lockManagement.createSendMsgDetil", entityMap);
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			end = "短信发送成功";
		} else {
			end = "短信发送失败";
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
		return end;
	}

	/**
	 * 集团<br>
	 * 验证交机前拨款额度，没有授信不卡
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	/*public boolean checkPayBeforeCreditExcludeEmptyForGroup(String credit_id) throws Exception {
		System.out.println("====授信额度卡关-集团-交机前====");
		boolean flag = false;
		if (!LeaseUtil.hasPayBefore(credit_id)) {
			//没有交机前拨款不卡关
			return true;
		}
		try {
			String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
			String group_code = LeaseUtil.getSuplGroupCodeBySuplId(supl_id);
			if (StringUtils.isEmpty(group_code)) {
				logger.info("该报告的供应商没有集团授信。");
				return true;
			}
			CreditLineTO line = LeaseUtil.getSuplPayBeforeCreditByGroup(group_code);
			if (line == null || line.getLine() <= 0 || StringUtils.isEmpty(line.getHasLine()) || line.getHasLine().equals("N")) {
				logger.info("该报告的供应商没有集团授信。");
				return true;
			}
			Double lineValue = line.getLine();
			Double used = LeaseUtil.getUsedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE);
			Double reused = line.getRepeatFlag() == 1 ? LeaseUtil.getReusedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE) : 0;
			Double lastValue = lineValue - used + reused;
			Double payMoney = LeaseUtil.getPayMoneyByCreditIdForBefore(credit_id);
			if (payMoney > lastValue) {
				this.message = "该案件的供应商的集团授信交机前拨款额度为：￥" + lineValue + ",已用额度为：￥" + (used - reused) + ",剩余额度为：￥" + lastValue + ",授信额度不足。";
			} else {
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}*/
	
	/**
	 * 集团<br>
	 * 验证交机前拨款额度
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	/*public boolean checkPayBeforeCreditForGroup(String credit_id) throws Exception {
		System.out.println("====授信额度卡关-集团-交机前====");
		boolean flag = false;
		if (!LeaseUtil.hasPayBefore(credit_id)) {
			//没有交机前拨款不卡关
			return true;
		}
		try {
			String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
			if (LeaseUtil.hasSuplGroupCredit(supl_id, CREDIT_LINE_TYPE.PAY_BEFORE)) {
				String group_code = LeaseUtil.getSuplGroupCodeBySuplId(supl_id);
				CreditLineTO line = LeaseUtil.getSuplPayBeforeCreditByGroup(group_code);
				Double lineValue = line.getLine();
				Double used = LeaseUtil.getUsedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE);
				Double reused = line.getRepeatFlag() == 1 ? LeaseUtil.getReusedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE) : 0;
				Double lastValue = lineValue - used + reused;
				Double payMoney = LeaseUtil.getPayMoneyByCreditIdForBefore(credit_id);
				if (payMoney > lastValue) {
					this.message = "该案件的供应商的集团授信交机前拨款额度为：￥" + lineValue + ",已用额度为：￥" + (used - reused) + ",剩余额度为：￥" + lastValue + ",授信额度不足。";
				} else {
					flag = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}*/
	
	/**
	 * 集团<br>
	 * 根据金额验证交机前授信
	 * @param credit_id
	 * @param payMoney
	 * @return
	 * @throws Exception
	 */
	/*public boolean checkPayBeforeCreditForGroup(String credit_id, double payMoney) throws Exception {
		System.out.println("====授信额度卡关-集团-交机前====");
		boolean flag = false;
		if (!LeaseUtil.hasPayBefore(credit_id)) {
			//没有交机前拨款不卡关
			return true;
		}
		try {
			String supl_id = LeaseUtil.getSuplIdByCreditId(credit_id);
			if (LeaseUtil.hasSuplGroupCredit(supl_id, CREDIT_LINE_TYPE.PAY_BEFORE)) {
				String group_code = LeaseUtil.getSuplGroupCodeBySuplId(supl_id);
				CreditLineTO line = LeaseUtil.getSuplPayBeforeCreditByGroup(group_code);
				Double lineValue = line.getLine();
				Double used = LeaseUtil.getUsedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE);
				Double reused = line.getRepeatFlag() == 1 ? LeaseUtil.getReusedLineByGroup(group_code, CREDIT_LINE_TYPE.PAY_BEFORE) : 0;
				Double lastValue = lineValue - used + reused;
				if (payMoney > lastValue) {
					this.message = "该案件的供应商的集团授信交机前拨款额度为：￥" + lineValue + ",已用额度为：￥" + (used - reused) + ",剩余额度为：￥" + lastValue + ",授信额度不足。";
				} else {
					flag = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}*/
	
	/**
	 * 供应商授信情况
	 * @param supl_id
	 * @return
	 * @throws Exception
	 */
	public List<CreditLineTO> getSuplCreditLine(String supl_id) throws Exception{
		List<CreditLineTO> lineList = new ArrayList<CreditLineTO>();
		CreditLineTO line = null;
		line = LeaseUtil.getSuplJointCreditBySuplId(supl_id);	//连保
		lineList.add(line);
		line = LeaseUtil.getSuplBuyBackCreditBySuplId(supl_id);	//回购
		lineList.add(line);
		line = LeaseUtil.getSuplPayBeforeCreditBySuplId(supl_id);	//交机前
		lineList.add(line);
		line = LeaseUtil.getInvoiceLineForSupl(supl_id);	//发票
		lineList.add(line);
		return lineList;
	}
	
	/**
	 * 验证供应商授信额度(优先卡关集团)
	 * true：有额度，false：没有额度。
	 * @param creditId
	 * @param credit_line_type
	 * @return
	 * @throws Exception 
	 */
	public CheckedResult checkSuplCreditLine(String creditId, CREDIT_LINE_TYPE credit_line_type) throws Exception{
		CheckedResult result = new CheckedResult();
		String msg = null;
		boolean flag = false;
		CreditLineTO line = null;
		String supl_id = LeaseUtil.getSuplIdByCreditId(creditId);
		double thisMoney = 0;
		if (credit_line_type.equals(CREDIT_LINE_TYPE.UNION)) {
			line = LeaseUtil.getSuplJointCreditBySuplId(supl_id);
			thisMoney = LeaseUtil.getRemainingPrincipalByCreditId(creditId);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.BUY_BACK)) {
			line = LeaseUtil.getSuplBuyBackCreditBySuplId(supl_id);
			thisMoney = LeaseUtil.getRemainingPrincipalByCreditId(creditId);
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.PAY_BEFORE)) {
			if (LeaseUtil.hasPayBefore(creditId)) {
				double pay = LeaseUtil.getRealPayMoneyByCreditId(creditId) + LeaseUtil.getRealPayMoneyInAuthByCreditId(creditId);
				double payBefore = LeaseUtil.getPayMoneyByCreditIdForBefore(creditId);
				thisMoney = pay >= payBefore ? 0 : (payBefore - pay);
				if (thisMoney == 0) {
					flag = true;
				} else {
					if (LeaseUtil.hasSuplGroupCredit(supl_id)) {
						//集团授信
						line = LeaseUtil.getSuplPayBeforeCreditByGroup(LeaseUtil.getSuplGroupCodeBySuplId(supl_id));
						if (line != null) {
							msg = "供应商集团授信，剩余额度：" + line.getLastLine() + ", 此次申请额度：" + thisMoney + "。";
						} else {
							msg = "供应商集团未授信";
						}
					} else {
						//非集团授信
						line = LeaseUtil.getSuplPayBeforeCreditBySuplId(supl_id);
						if (line != null) {
							msg = "供应商授信，剩余额度：" + line.getLastLine() + ", 此次申请额度：" + thisMoney + "。";
						} else {
							msg = "供应商未授信";
						}
					}
				}
			} else {
				flag = true;
			}
		} else if (credit_line_type.equals(CREDIT_LINE_TYPE.INVOICE)) {
			line = LeaseUtil.getInvoiceLineForSupl(supl_id);
			thisMoney = 1;
		} else {
			throw new Exception("额度类型错误。");
		}
		if (!flag) {
			if (line != null && line.getLastLine() >= thisMoney) {
				flag = true;
			} else {
				msg += "-授信额度不足。";
			}
		}
		result.setResult(flag);
		result.setMsg(msg);
		return result;
	}
	
	public List<SelectionTo> getAllGuarantor() {
		List<SelectionTo> data = null;
		try {
			data = LeaseUtil.getAllGuarantor();
		} catch (SQLException e) {
			data = new ArrayList<SelectionTo>();
		}
		return data ;
	}
	
	public int updateCompanyCode(String creditId, int companyCode){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("creditId", creditId);
		paramMap.put("companyCode", companyCode);
		int result = update("leaseUtil.updateCompanyCode", paramMap);
		return result;
	}
	
	public int dayDiff(Date dateFrom, Date dateTo) throws ParseException{
		int dayDiff = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = sdf.parse(sdf.format(dateFrom));
		Date d2 = sdf.parse(sdf.format(dateTo));
		dayDiff = new Long((d2.getTime() - d1.getTime()) / 1000 / 60 / 60 / 24).intValue();
		return dayDiff;
	}
	
}
