package com.brick.invoiceManagement.util;

public class InvoiceManagementUtil {
	public static enum PROCESS {
		STOP,//手动停开
		OPEN, //手动复开
		NOT_STOP,//不自动停开,
		NOT_OPEN//不自动复开
	}
	
	public static enum STOP_TYPE {
		MANUAL,//手动停开
		DUN_45 //逾期45天自动停开发票
	}
	
	public static enum SPECIAL_TYPE {
		NOT_STOP,//不自动停开
		NOT_OPEN //不自动复开
	}
	
	public static enum OPERATE_FROM {
		MANUAL_FUNCTION,//手动停复开功能
		AUTO_FUNCTION//自动停复开功能
	}
	
	public static enum PRICE_TYPE {
		INTEREST,//利息
		CAPITAL,//本金
		DEPOSIT//保证金
	}
	
	public static enum CASE_TYPE {
		NEW,//新案
		OLD,//旧案
		NORMAL,//开票功能的开票案件
		SPECIFIC//红冲或者作废功能的开票案件
	}
}
