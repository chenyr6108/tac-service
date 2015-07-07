package com.brick.collection;

/**
 * @author wujw
 * @date May 27, 2010
 * @version 
 */
public class CollectionConstants {
	
	 /** 一百. */
    public static final int HUNDRED = 100;

    /** 每年12月. */
    public static final int MONTH_IN_YEAR = 12;

	
	/** 0正常 */
	public final static Integer STATUS_NOMAL = Integer.valueOf(0);
	/** -1作废 */
	public final static Integer STATUS_CANCEL = Integer.valueOf(-1);
	/** -2删除(伪删除) */
	public final static Integer STATUS_DELETE = Integer.valueOf(-2);
	
	
	/** 支付方式  11期初等额本息支付 */
	public final static Integer PAY_WAY_BEGIN_EQUAL_RATE = Integer.valueOf(11);
	/** 支付方式  12期初等额本金支付 */
	public final static Integer PAY_WAY_BEGIN_EQUAL_CAPITAL = Integer.valueOf(12);
	/** 支付方式  13期初不等额支付 */
	public final static Integer PAY_WAY_BEGIN_UNEQUAL = Integer.valueOf(13);
	/** 支付方式  21期末等额本息支付 */
	public final static Integer PAY_WAY_END_EQUAL_RATE = Integer.valueOf(21);
	/** 支付方式  22期末等额本金支付 */
	public final static Integer PAY_WAY_END_EQUAL_CAPITAL = Integer.valueOf(22);
	/** 支付方式  23期末不等额支付 */
	public final static Integer PAY_WAY_END_UNEQUAL = Integer.valueOf(23);
	
	
	/** 租赁周期  1月 */
	public final static Integer LEASE_TERM_ONE_MONTH = Integer.valueOf(1);
	/** 租赁周期  3季 */
	public final static Integer LEASE_TERM_THREE_MONTH = Integer.valueOf(3);
	/** 租赁周期  6半年 */
	public final static Integer LEASE_TERM_SIX_MONTH = Integer.valueOf(6);
	/** 租赁周期  1年 */
	public final static Integer LEASE_TERM_TWELVE_MONTH = Integer.valueOf(12);
	
}
