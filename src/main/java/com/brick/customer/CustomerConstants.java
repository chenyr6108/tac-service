package com.brick.customer;

public class CustomerConstants {
	/** 客户授信日志 状态 0添加 */
	public final static Integer CUGL_STATUS_CREATE = Integer.valueOf(0);
	/** 客户授信日志 状态 1占用授信余额 */
	public final static Integer CUGL_STATUS_OCCUPY = Integer.valueOf(1);
	/** 客户授信日志 状态 2释放授信余额 */
	public final static Integer CUGL_STATUS_FREE = Integer.valueOf(2);
	/** 客户授信日志 状态 3取消授信余额 */
	public final static Integer CUGL_STATUS_CANCEL = Integer.valueOf(3);
	/** 客户授信日志 状态 4删除授信 */
	public final static Integer CUGL_STATUS_DEL = Integer.valueOf(4);
	/** 客户授信日志 状态 5修改授信 */
	public final static Integer CUGL_STATUS_MODIFY = Integer.valueOf(5);
}
