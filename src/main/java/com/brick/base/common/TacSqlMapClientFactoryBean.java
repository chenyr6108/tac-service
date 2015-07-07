package com.brick.base.common;

import org.springframework.orm.ibatis.SqlMapClientFactoryBean;

import com.brick.base.util.ReflectUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;

public class TacSqlMapClientFactoryBean extends SqlMapClientFactoryBean {
	private SqlExecutor sqlExecutor;

	public SqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}

	public void setSqlExecutor(SqlExecutor sqlExecutor) {
		this.sqlExecutor = sqlExecutor;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		SqlMapClient c = (SqlMapClient) getObject();
		if (sqlExecutor != null && c instanceof SqlMapClientImpl) {
			SqlMapClientImpl client = (SqlMapClientImpl) c;
			SqlMapExecutorDelegate delegate = client.getDelegate();
			try {
				ReflectUtil.setFieldValue(delegate, "sqlExecutor", SqlExecutor.class, sqlExecutor);
				System.out.println("[iBATIS] success set ibatis SqlMapClient.sqlExecutor = "
								+ sqlExecutor.getClass().getName());
			} catch (Exception e) {
				System.out.println("[iBATIS] error,cannot set ibatis SqlMapClient.sqlExecutor = "
								+ sqlExecutor.getClass().getName()
								+ " cause:"
								+ e);
			}
		}
	}
}
