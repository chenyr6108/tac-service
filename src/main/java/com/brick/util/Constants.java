/*****************************************************************************
 * Name : Constants.java
 *
 * DESCRIPTION :  Store Constants
 * 
 *
 * REVISION HISTORY:
 *
 *       Date          Author          Description
 *    ----------    ------------   -------------------
 *    2012-03-14       ShenQi          new program
 *****************************************************************************/
package com.brick.util;

public interface Constants {

	//公司名称
	public static final String COMPANY_NAME="裕融租赁有限公司";
	public static final String COMPANY_NAME_YUGUO="裕国融资租赁有限公司";
	public static final String COMPANY_NAME_ENGLISH="TAC leasing Co.Ltd";
	public static final String COMPANY_NAME_ENGLISH_YUGUO="TAC Finance Leasing Co.Ltd";
	//公司code
	public static final int COMPANY_CODE = 1;
	public static final int COMPANY_CODE_YUGUO = 2;
	
	//公司通讯地址
	public static final String COMPANY_COMMON_ADDRESS="苏州工业园区圆融时代广场民生金融大厦23栋11F";
	public static final String COMPANY_COMMON_ADDRESS_YUGUO="杭州市萧山区文明路328号江宁大厦B座12楼";
	//公司法人代表
	public static final String LEGAL_PERSON="许国兴";
	
	public static final String WD="WD";//工作日
	public static final String HD="HD";//休息日
	
	//T_PRJT_CREDITMEMO表,字段 AUDIT_STATE  评审分类 0是区域主管评审的,1是业务副总评审的
	public static final int AUDIT_STATE_0=0;
	public static final int AUDIT_STATE_1=1;
	
	//公司邮件地址
	public static final String EMAIL_FROM="tacfinance_service@tacleasing.cn";
	
	public static final String HR_EMAIL_FROM="hr_service@tacleasing.cn";
	
	//逾期报表金额标记
	public static final String _50="50万元以内";//小于50万元
	public static final String _50_100="50~100万元";//50万元~100万元
	public static final String _100_200="100~200万元";//100万元~200万元
	public static final String _200_300="200~300万元";//200万元~300万元
	public static final String _300="300以上万元";//300以上万元
	
	public static final int $50=500000;
	public static final int $100=1000000;
	public static final int $200=2000000;
	public static final int $300=3000000;
	
	//逾期天数
	public static final int DUN_DAY_7=7;
	public static final int DUN_DAY_8=8;
	public static final int DUN_DAY_14=14;
	public static final int DUN_DAY_15=15;
	public static final int DUN_DAY_30=30;
	public static final int DUN_DAY_31=31;
	public static final int DUN_DAY_60=60;
	public static final int DUN_DAY_61=61;
	public static final int DUN_DAY_90=90;
	public static final int DUN_DAY_91=91;
	public static final int DUN_DAY_180=180;
	public static final int DUN_DAY_181=181;
	
	public static final String CONTRACT_TYPE_2="2";//回租
	public static final String CONTRACT_TYPE_4="4";//商用车回租
	public static final String CONTRACT_TYPE_5="5";//新品回租
	public static final String CONTRACT_TYPE_6="6";//乘用车新品回租
	public static final String CONTRACT_TYPE_7="7";//新合同方案   目前就只有设备类型能用
	public static final String CONTRACT_TYPE_8="8";//新车委贷
	public static final String CONTRACT_TYPE_9="9";//设备售后回租
	public static final String CONTRACT_TYPE_10="10";//新车回租方案
	public static final String CONTRACT_TYPE_11="11";//商用车售后回租
	public static final String CONTRACT_TYPE_12="12";//二手车回租方案
	public static final String CONTRACT_TYPE_13="13";//原车融资方案
	public static final String CONTRACT_TYPE_14="14";//二手车委贷
	
	public static final String PRODUCTION_TYPE_1="1";//产品类别 设备
	public static final String PRODUCTION_TYPE_2="2";//产品类别 商用车
	public static final String PRODUCTION_TYPE_3="3";//产品类别 乘用车
	
	//总经理邮箱
	public static final String GM_MAIL = "ivanl@tacleasing.cn";
	
	public static final String SYSTEM_ID = "184";
	
	final static String TYPE_OWN_INPUT = "1";
	final static String TYPE_EXAMINANT_INPUT = "0";
	final static String RISK_FLAG_COMMIT = "111";
	final static String RISK_FLAG_PASS = "1";
	final static String RISK_FLAG_RETURN = "3";
	final static String RISK_FLAG_REJECT = "4";
	final static String RISK_LEVEL_BP = "4";
	final static String RISK_LEVEL_GM = "3";
	final static String RISK_LEVEL_DGM = "2";
	final static String RISK_LEVEL_M = "1";
	
	//委寄审核类别
	public static final String AUDIT_TYPE_LAW = "01";
	public static final String AUDIT_TYPE_OUTVISIT = "02";
	
	//税费方案
	public static final String TAX_PLAN_CODE_1="1";//营业税税费方案
	public static final String TAX_PLAN_CODE_2="2";//增值税税费方案
	public static final String TAX_PLAN_CODE_3="3";//增值税內含方案
	public static final String TAX_PLAN_CODE_4="4";//直接租赁税费方案
	public static final String TAX_PLAN_CODE_5="5";//乘用车委贷方案
	public static final String TAX_PLAN_CODE_6="6";//设备售后回租
	public static final String TAX_PLAN_CODE_7="7";//乘用车售后回租
	public static final String TAX_PLAN_CODE_8="8";//商用车售后回租
	
	//各办事处ID
	public static final String CMPY_17="17";//苏州设备
	public static final String CMPY_2="2";//昆山设备
	public static final String CMPY_7="7";//南京设备
	public static final String CMPY_13="13";//上海设备
	public static final String CMPY_23="23";//天津设备
	public static final String CMPY_22="22";//济南设备
	public static final String CMPY_25="25";//武汉设备
	public static final String CMPY_27="27";//长沙设备
	public static final String CMPY_24="24";//郑州设备
	public static final String CMPY_26="26";//宁波设备
	public static final String CMPY_3="3";//东莞设备
	public static final String CMPY_8="8";//佛山设备
	public static final String CMPY_11="11";//厦门设备
	public static final String CMPY_9="9";//重庆设备
	public static final String CMPY_14="14";//成都设备
	public static final String CMPY_16="16";//苏州商用车
	public static final String CMPY_15="15";//上海商用车
	public static final String CMPY_20="20";//上海乘用车
	public static final String CMPY_21="21";//杭州乘用车
	public static final String CMPY_28="28";//深圳乘用车
}