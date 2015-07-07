package com.brick.base.to;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagingInfo<E> {
	/** 当前页数 * */

	private int pageNo = 1;

	/** 每页显示的记录数 * */

	private int pageSize = 10;

	/** 总记录 * */

	private int totalCount;

	/** 总页数* */

	private int totalPage;

	private boolean hasNext = false;

	private int nextPage;

	private boolean hasPre = false;

	private int prePage;

	/** 页面数据List * */

	private List<E> resultList;

	/** 参数 Map * */

	private Map<String, Object> params = new HashMap<String, Object>();

	/** URL 参数* */

	private String urlParam;

	private String orderBy;
	
	private String orderType = "ASC";
	
	public PagingInfo() {

	}

	public PagingInfo(int pageNo, int pageSize, int totalCount) {
		this.pageNo = pageNo;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
	}

	public void addParam(String name, Object value) {
		if (name != null && !name.equals("") && value != null
				&& !value.equals("")) {
			if (params.size() == 0) {
				urlParam = name + "=" + value;
			} else {
				urlParam = "&" + name + "=" + value;
			}
			params.put(name, value);
		}
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		if (totalCount == 0)
			return 0;
		totalPage = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			totalPage++;
		}
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/** 是否有下一页 */

	public boolean isHasNext() {
		this.hasNext = (pageNo + 1 <= getTotalPage());
		return hasNext;
	}

	public int getNextPage() {
		if (isHasNext())
			nextPage = pageNo + 1;
		else
			nextPage = totalPage;
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	/** 是否有上一页 */

	public boolean isHasPre() {
		this.hasPre = (pageNo - 1 >= 1);
		return hasPre;
	}

	public int getPrePage() {
		if (isHasPre())
			prePage = pageNo - 1;
		else
			prePage = pageNo;
		return prePage;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public List<E> getResultList() {
		if(resultList==null) {
			resultList=new ArrayList<E>();
		}
		return resultList;
	}

	public void setResultList(List<E> resultList) {
		this.resultList = resultList;
	}

	public Map<String, Object> getParams() {

		return params;

	}

	public void setParams(Map<String, Object> params) {

		this.params = params;

	}

	public void setUrlParam(String urlParam) {

		this.urlParam = urlParam;

	}

	public String getUrlParam() {

		return this.urlParam;

	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
