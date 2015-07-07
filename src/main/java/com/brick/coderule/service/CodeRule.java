package com.brick.coderule.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.coderule.core.CodeRuleCore;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
/**
 * 编码规则
 * @author wujw
 * @date Apr 27, 2010
 * @version
 */
public class CodeRule extends AService {
	
	static Log logger = LogFactory.getLog(CodeRule.class);
	/** ONE  */
	public final static Integer ONE = Integer.valueOf(1);
	/** TWO  */
	public final static Integer TWO = Integer.valueOf(2);
	/** THREE  */
	public final static Integer THREE = Integer.valueOf(3);
	/** FOUR  */
	public final static Integer FOUR = Integer.valueOf(4);
	/** FIVE  */
	public final static Integer FIVE = Integer.valueOf(5);
	/** SIX  */
	public final static Integer SIX = Integer.valueOf(6);

	/**
	 * 
	 * 承租人编码格式 
	 * 邮编-年份 流水号
	 * 010-2010 0012
	 * @param province_id 省份id
	 * @param city_id 市id
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateCustCode(Context context){
//		Long province_id = Long.parseLong((String) context.contextMap.get("province_id"));
//		Long city_id = Long.parseLong((String) context.contextMap.get("city_id"));
//		
//		
//		try {
//			if (city_id != null && city_id != -1l) {
//				context.contextMap.put("id", city_id);
//			} else {
//				context.contextMap.put("id", province_id);
//			}
//			map = (Map)DataAccessor.query("area.queryById", context.contextMap, DataAccessor.RS_TYPE.MAP);
//				
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		String zip = (String)map.get("LESSEEAREA");
//		
//		String lastCode = CodeRuleCore.fetchStringCode("客户编码", null, FOUR);
//		
//		String code = "02" + zip + CodeRuleCore.fetchCurrentYear() + lastCode;
		
		// 2010-09-06   wjw v1.2
		Map rsMap = null;
		
		context.contextMap.put("DECP_ID", context.contextMap.get("decp_id"));
		
		try {
		
			rsMap = (Map)DataAccessor.query("companyManage.readCompanyAliasByDecpId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		
		} catch (Exception e) {
		
			e.printStackTrace();
		
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("客户编码", null, FOUR);
		
		String code = "02" + alias + year + lastCode;
		
		logger.info("客户编码:"+code);
		
		return code;
	}
	/**
	 * 风控编码格式
	 * “公司简称” + “-” + “FK” + “/” + “年份” + “六位流水号”
	 * PQRJ-FK/201000015
	 * @param PRCD_ID 资信id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateWindCode(Object prcdId) {
		Map paramMap = new HashMap();
		paramMap.put("PRCD_ID", prcdId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByPrcdId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		// String lastCode = CodeRuleCore.fetchStringCode("风控编号", alias, FIVE);
		String lastCode = CodeRuleCore.fetchStringCode("风控编码", null, FOUR);
		
		String code = "03" + alias + year + lastCode;
		
		logger.info("风控编码:"+code);
		
		return code;
	}
	/**
	 * 融资租赁合同号
	 * “公司简称” + “-” + “租赁类型” + “/” + “年份” + “五位流水号”
	 * PQRJ-RZ/201000015
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateRentContractCode(Object prcdId) {
		Map paramMap = new HashMap();
		paramMap.put("PRCD_ID", prcdId);
		paramMap.put("dataType", "融资租赁合同类型");
		
		Map rsMap = null;
		List<Map> contractTypes = null;
		Integer contractType = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByPrcdId", paramMap, DataAccessor.RS_TYPE.MAP);
			
			contractTypes = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", paramMap, DataAccessor.RS_TYPE.LIST);
			
			contractType = (Integer)DataAccessor.query("coderule.readPrcdContractTypeByPrcdId", paramMap, DataAccessor.RS_TYPE.OBJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias =null;
		
		String type = "null";
		for (Map m : contractTypes) {
			if (DataUtil.intUtil(m.get("CODE")) == contractType) {
				type = String.valueOf(m.get("SHORTNAME"));
			}
		}
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("合同编码", alias, FOUR);
		
		String code = "05" + type + year + lastCode;
		
		logger.info("合同编码:"+code);
		
		return code;
	}
	/**
	 * 开票协议书编码
	 * “公司简称” + “-” + “KP” + “/” + “年份” + “五位流水号”
	 * PQRJ-KP/201000015
	 * @param rectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateInvoiceCode(Object rectId) {
		Map paramMap = new HashMap();
		paramMap.put("RECT_ID", rectId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByRectId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("开票协议书编码", null, FOUR);
		
		String code = "05KP" + year + lastCode;
		
		logger.info("开票协议书编码:"+code);
		
		return code;
	}
	/**
	 * 发货通知单编号
	 * “公司简称” + “-” + “FH” + “/” + “年份” + “五位流水号”
	 * PQRJ-FH/201000015
	 * @param rectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateDeliveryNoticeCode(Object rectId) {
		Map paramMap = new HashMap();
		paramMap.put("RECT_ID", rectId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByRectId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("发货通知单编码", null, FOUR);
		
		String code = "05" + "FH" + year + lastCode;
		
		logger.info("发货通知单编码:"+code);
		
		return code;
	}
	/**
	 * 付款通知单编号
	 * “公司简称” + “-” + “FH” + “/” + “年份” + “五位流水号”
	 * PQRJ-FH/201000015
	 * @param rectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generatePayMentCode(Object rectId) {
		Map paramMap = new HashMap();
		paramMap.put("RECT_ID", rectId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByRectId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("付款通知单编码", null, FOUR);
		
		String code = "05" + "PM" + year + lastCode;
		
		logger.info("付款通知单编码:"+code);
		
		return code;
	}
	/**
	 * 项目申请编号
	 * “公司简称” + “-” + “SQ” + “/” + “年份” + “五位流水号”
	 * PQRJ-SQ/201000015
	 * @param rectId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateProjectCode(Object decpId) {
		Map paramMap = new HashMap();
		paramMap.put("DECP_ID", decpId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByDecpId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("项目申请编码", null, FOUR);
		
		String code = alias + "SQ" + year + lastCode;
		
		logger.info("项目申请编码:"+code);
		
		return code;
	}
	/**
	 * 资信编号
	 * “公司简称” + “-” + “ZX” + “/” + “年份” + “五位流水号”
	 * PQRJ-ZX/201000015
	 * @param decpId 公司id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String generateProjectCreditCode(Object decpId) {
		Map paramMap = new HashMap();
		paramMap.put("DECP_ID", decpId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByDecpId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("资信编号", alias, FIVE);
		
		String code = alias + "-ZX/" + year + lastCode;
		
		logger.info("资信编号:"+code);
		
		return code;
	}
	/**
	 * 供应商编码
	 * 10 + 年 + 月 + 四位流水号
	 * @param context
	 * @return
	 */
	public static String generateSupplierCode(Context context) {
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		String lastCode = CodeRuleCore.fetchStringCode("供应商编码", null, FOUR);
		
		String code = "10" + year + lastCode;
		
		logger.info("供应商编码:"+code);
		
		return code;
	}
	
	/**
	 * 风控流水号
	 * "03"+“公司简称” + “-”  + “年份” + “四位流水号”
	 * 
	 * @param PRCD_ID 资信id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String geneRiskCode(Object prcdId) {
		Map paramMap = new HashMap();
		paramMap.put("PRCD_ID", prcdId);
		
		Map rsMap = null;
		
		try {
			rsMap = (Map) DataAccessor.query("companyManage.readCompanyAliasByPrcdId", paramMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String alias = (String) rsMap.get("ALIAS");
		
		String year = CodeRuleCore.fetchCurrentYear();
		
		
		String lastCode = CodeRuleCore.fetchStringCode("风控流水号", null, FOUR);
		
		String code = "03" + alias + year + lastCode;
		
		logger.info("风控流水号:"+code);
		
		return code;
	}	
	

	/**
	 * 资金上传流水号
	 * "50"+ “年份” + “五位流水号”
	 * 
	 * @param PRCD_ID 资信id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String geneFinanceCode(Object prcdId) {
		String year = CodeRuleCore.fetchCurrentYearMMDD();
		
		String lastCode = CodeRuleCore.fetchStringCode("资金上传码", null, FIVE);
		
		String code = "50" + year + lastCode;
		
		logger.info("资金上传码:"+code);
		
		return code;
	}		
	
	
	/**
	 *报告编号
	 * “ 年月日” + “三位流水号”
	 * 
	 * @param PRCD_ID 资信id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String geneCreditRunCode() {
		String year = CodeRuleCore.fetchCurrentYearMMDD();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		
		String lastCode = CodeRuleCore.fetchStringCode("报告流水码", sf.format(new Date()), THREE);
		
		String code = year + lastCode;
		
		logger.info("报告流水码:"+code);
		
		return code;
	}	
	
	/**
	 *退款单流水码
	 * “ 年月” + “三位流水号”
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String geneFundsReturnCode() {
		String year = CodeRuleCore.fetchCurrentYearMM();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		
		String lastCode = CodeRuleCore.fetchStringCode("退款单流水码", sf.format(new Date()), THREE);
		
		String code = year + lastCode;
		
		logger.info("退款单流水码:"+code);
		
		return code;
	}	
	
	/**
	 *本金收据流水码
	 * “ 年月” + “四位流水号”
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String genePrincipalRunCode() {
		String year = CodeRuleCore.fetchCurrentYearMM();
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		
		String lastCode = CodeRuleCore.fetchStringCode("本金收据流水码", sf.format(new Date()), FOUR);
		
		String code = year + lastCode;
		
		logger.info("本金收据流水码:"+code);
		
		return code;
	}	
	/**
	 * 更改单流水码
	 * M+年月+三位流水号
	 * M201212001
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getModifyOrderReturnCode() {
		//取年月
		String year = CodeRuleCore.fetchCurrentYearMM();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
		String lastCode = CodeRuleCore.fetchStringCode("更改单流水码", sf.format(new Date()), THREE);
		String code = "M"+year + lastCode;
		logger.info("更改单流水码:"+code);
		return code;
	}	
	
	//发票单据号
	public static String getInvoiceCode() {
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
		String lastCode=CodeRuleCore.fetchStringCode("发票单据号",null,FOUR);
		String code=sf.format(new Date())+lastCode;
		return code;
	}
	//金蝶传票号
	public static String getKingDeerCode() {
		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
		String lastCode=CodeRuleCore.fetchStringCode("金蝶传票号",null,FOUR);
		String code=sf.format(new Date())+lastCode;
		return code;
	}
}