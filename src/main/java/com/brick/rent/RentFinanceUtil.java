package com.brick.rent;

public class RentFinanceUtil {
	public static enum RENT_TYPE {
		RENT,//租金
		VALUE_ADD_TAX,//增值税
		RENT_FINE,//租金罚息
		DEPOSIT_A,//保证金A
		DEPOSIT_B,//保证金B
		DEPOSIT_C,//保证金C
		STAY_BUY_PRICE,//设备留购价
		MANAGE_FEE,//管理费收入
		MANAGE_FEE2,//管理费收入
		INSURANCE_DEPUTY_FEE,//保险费押金代收款收入
		HOME_FEE,//家访费收入
		SETUP_FEE,//设定费收入
		OTHER_FEE,//其他费用收入
		TAX,//税金
		
		LITIGATION_FEE,//起诉费
		SHIFTING_CHARGES_FEE,//调档费
		ANNOUNCEMENT_FEE,//公告费
		LAWYER_LETTER_FEE,//律师函费
		COLLECTION_LETTER_FEE,//催收函费
		LAWYER_FEE,//律师费
		FILE_EXECUTION_FEE,//立案执行费
		LITIGATION_COPY_FEE,//诉状复印费
		OTHER_LAWY_FEE,//其他法务费用
		OUT_VISIT_FEE,//委外回访费
		PAY_TOKEN_FEE,//支付令费
		REPAY_LITIGATION_FEE,//补缴起诉费
		
		//结清类型
		SETTLEMENT_OWN_PRICE,//结清本金
		SETTLEMENT_REN_PRICE,//结清利息
		SETTLEMENT_VALUE_ADD_TAX,//结清增值税
		SETTLEMENT_RENT_FINE,//结清罚息
		SETTLEMENT_LAW_FEE,//结清法务费
		SETTLEMENT_STAY_PRICE,//结清留购价
		SETTLEMENT_OTHER_FEE,//结清其他费用
		
		//银行手续费收入
		BANK_FEE_INCOME,
		//暂收款类型
		CLAIM,//认领款
		REFUND//退款
	};
	
	public static enum AUTHORITY {//结清审批权限
		SALES_MANAGER,//结清审批-单位主管权限
		SALES_DIRECT,//结清审批-业务副总权限
		SERVICE_DIRECT,//结清审批-业管部权限
		GENERAL_MANAGER,//结清审批-总经理权限
		FINANCIAL,//结清审批-财务部权限
		FINANCIAL_STAFF,//结清审批-财务专员权限
		ACCOUNTANCY_STAFF,//结清审批-会计专员权限
		UNIT_MANAGER,//结清审批-区域主管权限
		
		CLAIM,//暂收款认领权限
		REFUND,//暂收款退款权限
		UPLOAD,//上传水单
		MODIFY_REMARK,//修改备注
		APPROVE,//通过
		REJECT//驳回
	};
	
	public static enum TABLE {
		T_RENT_DECOMPOSE
	}
}
