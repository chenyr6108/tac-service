package com.brick.base;

import java.sql.Timestamp;
import java.util.Date;

import com.ibatis.sqlmap.client.SqlMapClient;

public class WatchDogForQuartz implements Runnable {
	private SqlMapClient sqlMapClient;
	
	public SqlMapClient getSqlMapClient() {
		return sqlMapClient;
	}

	public void setSqlMapClient(SqlMapClient sqlMapClient) {
		this.sqlMapClient = sqlMapClient;
	}
	
	@Override
	public void run() {
		while(true){
			System.out.println("=====================================");
			Timestamp updateTime = null;
			String errorMsg = null;
			try {
				try {
					updateTime = (Timestamp) sqlMapClient.queryForObject("businessSupport.getUpdateTime");
				} catch (Exception e) {
					errorMsg = "连接数据库失败。";
					throw new Exception(errorMsg);
				}
				if (updateTime == null) {
					errorMsg = "没有查询到更新时间。";
					throw new Exception(errorMsg);
				}
				if ((new Date().getTime() - updateTime.getTime()) > (5 * 60 * 1000)) {
					errorMsg = "Quartz 运行异常，请检查。";
					throw new Exception(errorMsg);
				}
				System.out.println("成功");
			} catch (Exception e) {
				sendEmail(errorMsg);
				e.printStackTrace();
			}
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendEmail(String errorMsg){
		System.out.println(errorMsg);
	}
	
}
