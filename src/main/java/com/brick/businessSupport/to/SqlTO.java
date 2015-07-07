package com.brick.businessSupport.to;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.brick.base.to.BaseTo;

public class SqlTO extends BaseTo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String sql;
	private String sql_type;
	private Timestamp create_time;
	private Timestamp executed_time;
	private Integer result;
	private String item_code;
	private List<List<String>> resultList;
	
	/*
	 * 0：初始
	 * 1：测试
	 * 2：执行
	 */
	private Integer status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql_type() {
		return sql_type;
	}

	public void setSql_type(String sql_type) {
		this.sql_type = sql_type;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getExecuted_time() {
		return executed_time;
	}

	public void setExecuted_time(Timestamp executed_time) {
		this.executed_time = executed_time;
	}

	public String getItem_code() {
		return item_code;
	}

	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}

	public List<List<String>> getResultList() {
		return resultList;
	}

	public void setResultList(List<List<String>> resultList) {
		this.resultList = resultList;
	}

}
