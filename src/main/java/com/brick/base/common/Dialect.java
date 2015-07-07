package com.brick.base.common;

import com.brick.base.to.PagingInfo;

public interface Dialect {
	String getPagingSql (String sql, PagingInfo<Object> pagingInfo);
	
	String getTotalCountSql (String sql);
}
