package com.brick.bpm.util;

public interface BpmConst {
	
	/**
	 * 结点类型:开始结点
	 */
	public static String FLOWCLASS_STARTEVENT = "R";
	
	/**
	 * 结点类型:结束结点
	 */
	public static String FLOWCLASS_ENDEVENT = "E";
	
	/**
	 * 结点类型:活动任务
	 */
	public static String FLOWCLASS_ACTIVITY = "A";
	
	/**
	 * 结点类型:关卡
	 */
	public static String FLOWCLASS_GATEWAY = "G";
	
	/**
	 * 作用域:流程级
	 */
	public static Integer SCOPE_PROCESS = 1;
	
	/**
	 * 作用域:结点级
	 */
	public static Integer SCOPE_FLOW = 2;
	
	/**
	 * 数据类型:整形
	 */
	public static String DATATYPE_INTEGER = "java.lang.Integer";
	
	/**
	 * 数据类型:字符串
	 */
	public static String DATATYPE_STRING = "java.lang.String";
	
	/**
	 * 准备标识
	 */
	public static final Integer STATE_READY = -1;
	
	/**
	 * 失败标识
	 */
	public static final Integer STATE_ABORTED = 0;
	
	/**
	 * 激活标识
	 */
	public static final Integer STATE_ACTIVE = 1;
	
	/**
	 * 成功标识
	 */
	public static final Integer STATE_COMPLETED = 2;
	
	/**
	 * 待处理标识
	 */
	public static final Integer STATE_PENDING = 3;
	
	/**
	 * 暂停标识
	 */
	public static final Integer STATE_SUSPENDING = 4;
	
	/**
	 * 非多任务
	 */
	public static final String MULTI_NONE = "N";
	
	/**
	 * 多任务并行
	 */
	public static final String MULTI_PARALLEL = "P";
	
	/**
	 * 多任务顺序
	 */
	public static final String MULTI_SEQUENCE = "S";
	
	/**
	 * 处理结果:完成
	 */
	public static final String RESULT_COMPLETE = "C";
	
	/**
	 * 审批结果:同意
	 */
	public static final String RESULT_ACCEPT = "A";
	
	/**
	 * 审批结果:跳过
	 */
	public static final String RESULT_PASS = "P";
	
	/**
	 * 审批结果:拒绝
	 */
	public static final String RESULT_REJECT = "R";
	
	/**
	 * 关卡方向:网关必须拥有一个进入顺序流， 和多个外出顺序流
	 */
	public static final String DIRECTION_DIVERGING = "D";
	
	/**
	 * 关卡方向:网关必须拥有多个进入顺序流， 但是只能有一个外出顺序流。
	 */
	public static final String DIRECTION_CONVERGING = "C";
	
}
