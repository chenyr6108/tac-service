package com.brick.coderule.core;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.service.core.DataAccessor;

/**
 * @author wujw
 * @date Jul 15, 2010
 * @version 
 */
public class CodeRuleCore {
	
	static Log logger = LogFactory.getLog(CodeRuleCore.class);
	
	/** 线程锁. */
	private static Object lock = new Object();
	
	/**
	 * 取得对应此类型和标示的最大的流水号
	 * @param type 类型
	 * @param flag 标示
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public static Integer fetchCode(String type, String flag) {
		
		Integer code = 0;
		
		synchronized (lock) {

			if ((flag == null) || flag.equals("")) {
				flag = "DEFAULT";
			}
		
			HashMap paramMap = new HashMap();
			paramMap.put("i_type", type);
			paramMap.put("i_flag", flag);
			
			try {
				
				code = (Integer)DataAccessor.query("coderule.getLastCode",paramMap, DataAccessor.RS_TYPE.OBJECT);

				//code =  (Long) paramMap.get("o_code"); 
			} catch (Exception e) {
				logger.info("com.brick.coderule.service.CodeRule.fatchCode():" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return code;
	}
	
	/**
	 * 取出流水编号
	 * @param type 类型
	 * @param flag 标示
	 * @param length 流水编号的长度
	 * @return
	 */
	public static String fetchStringCode(String type, String flag, Integer length) {
		
		long code = fetchCode(type, flag);
		
		String str = String.valueOf(Math.round(Math.pow(10, length)) + code);
		
		return str.substring(str.length() - length);
	
	}
	
	/**
	 * 取当前年份
	 * @return
	 */
	public static String fetchCurrentYear() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		return sf.format(new java.util.Date());
	}

	/**
	 * Add by Michael 2012-3-9 增加资金上传流水号
	 * 取当前年月日
	 * @return
	 */
	public static String fetchCurrentYearMMDD() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		return sf.format(new java.util.Date());
	}	
	
	/**
	 * Add by Michael 2012-8-30 增加退款单流水号
	 * 取当前年月
	 * @return
	 */
	public static String fetchCurrentYearMM() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		return sf.format(new java.util.Date());
	}	
	
	public static void main(String[] args) {
		String s = fetchCurrentYear();
		System.out.println(s);
	}
}
