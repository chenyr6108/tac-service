package com.brick.base.common;

import com.brick.base.to.PagingInfo;

public class SqlServerDialect implements Dialect {

	@Override
	public String getPagingSql(String sql, PagingInfo<Object> pagingInfo) {
		String pagingSql = "select * from (select ROW_NUMBER() OVER(order by t_pag." + pagingInfo.getOrderBy() + " " + pagingInfo.getOrderType() + 
				") as rownum, t_pag.* from (" + sql + ") t_pag )t_pag_c where t_pag_c.rownum >= (" + 
				pagingInfo.getPageNo() + " - 1) * " + pagingInfo.getPageSize() + 
				" and t_pag_c.rownum <= " + pagingInfo.getPageNo() + " * " + pagingInfo.getPageSize();
		return pagingSql;
	}

	@Override
	public String getTotalCountSql(String sql) {
		return "select count(0) as total_count from (" + sql + ") t_count";
	}

}
