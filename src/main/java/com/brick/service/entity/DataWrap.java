package com.brick.service.entity;


public class DataWrap {
	
	public int pageSize = 10;
	public int pageCount = -1;
	public int currentPage = -1;
	public int recordCount =-1;
	public Object rs;	
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public Object getRs() {
		return rs;
	}
	public void setRs(Object rs) {
		this.rs = rs;
	}
	
	
	
}
