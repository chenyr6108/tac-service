package com.brick.contract;

/**
 * 主要用于记录合同模块的一些常量
 * @author wujw
 * @date May 4, 2010
 * @version 
 */
public class RentContractConstants {
	
	/** 0正常 */
	public final static Integer STATUS_NOMAL = Integer.valueOf(0);
	/** -1作废 */
	public final static Integer STATUS_CANCEL = Integer.valueOf(-1);
	/** -2删除(伪删除) */
	public final static Integer STATUS_DELETE = Integer.valueOf(-2);
	
	
	/** 租凭方式 合同表T_RENT_CONTRACT[RECT_TYPE] 0融资租赁*/
	public final static Integer RECT_TYPE_FINANCE = Integer.valueOf(0);
	/** 租凭方式 合同表T_RENT_CONTRACT[RECT_TYPE] 1售后回租 */
	public final static Integer RECT_TYPE_SALEBACK = Integer.valueOf(1);
	
	
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 11期初等额本息支付 */
	public final static Integer SCHEMA_PAY_WAY_BEGIN_EQUAL_RATE = Integer.valueOf(11);
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 12期初等额本金支付 */
	public final static Integer SCHEMA_PAY_WAY_BEGIN_EQUAL_CAPITAL = Integer.valueOf(12);
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 13期初不等额支付 */
	public final static Integer SCHEMA_PAY_WAY_BEGIN_UNEQUAL = Integer.valueOf(11);
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 21期末等额本息支付 */
	public final static Integer SCHEMA_PAY_WAY_END_EQUAL_RATE = Integer.valueOf(21);
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 22期末等额本金支付 */
	public final static Integer SCHEMA_PAY_WAY_END_EQUAL_CAPITAL = Integer.valueOf(22);
	/** 支付方式 方案表T_RENT_CONTRACTSCHEMA[PAY_WAY] 23期末不等额支付 */
	public final static Integer SCHEMA_PAY_WAY_END_UNEQUAL = Integer.valueOf(23);
	
	
	/** 租赁周期 方案表T_RENT_CONTRACTSCHEMA[LEASE_TERM] 1月 */
	public final static Integer SCHEMA_LEASE_TERM_ONE_MONTH = Integer.valueOf(1);
	/** 租赁周期 方案表T_RENT_CONTRACTSCHEMA[LEASE_TERM] 3季 */
	public final static Integer SCHEMA_LEASE_TERM_THREE_MONTH = Integer.valueOf(3);
	/** 租赁周期 方案表T_RENT_CONTRACTSCHEMA[LEASE_TERM] 6半年 */
	public final static Integer SCHEMA_LEASE_TERM_SIX_MONTH = Integer.valueOf(6);
	/** 租赁周期 方案表T_RENT_CONTRACTSCHEMA[LEASE_TERM] 1年 */
	public final static Integer SCHEMA_LEASE_TERM_TWELVE_MONTH = Integer.valueOf(12);
	
	/**
	 * 是否允许收件时发票由复印件->原件
	 */
	public static final boolean ALLOW_CHANGE_FILETYPE = true;
}
