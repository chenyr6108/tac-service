package com.brick.kingDeerTransfer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.command.BaseCommand;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.StringUtils;

public class kingDeerTransferService extends BaseCommand {
	Log logger=LogFactory.getLog(kingDeerTransferService.class);
	
	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	//抛转基础数据
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferBasicData()
	{
		//供应商
		getKingDeerTransferSupplInfo();
		//合同号
		getKingDeerTransferLeaseCode();
		//设备客户
		getKingDeerTransferEquipmentCust();
		//重车客户
		getKingDeerTransferCarCust();
	}
	
	//-------------------------------------------------------------------------------------------------- add by ShenQi
	//抛转当日现金销帐
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferCashHook() {
		
		try {
			String logContent="";
			String financeType="0";//0代表销帐日报表中的当日来款销帐类型
			String type="";//金蝶类型
			
			Map<String,Object> insertMap=new HashMap<String,Object>();
			
			//销帐日报表 保证金税金类型*******************************************************************************************************************
			type="13";
			//List<Map<String,Object>> equTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> equTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositTaxNew",null,RS_TYPE.LIST);
			logContent="保证金税金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equTaxList!=null&&i<equTaxList.size();i++) {
				insertMap=equTaxList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyDepositTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金税金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 法务费类型***********************************************************************************************************************
			type="18";
			//List<Map<String,Object>> lawFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyLawFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> lawFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyLawFeeNew",null,RS_TYPE.LIST);
			logContent="法务费类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;lawFeeList!=null&&i<lawFeeList.size();i++) {
				insertMap=lawFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyLawFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="法务费类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 重车保险费押金类型****************************************************************************************************************
			type="9";
			//List<Map<String,Object>> motorDepositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorDeposit",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorDepositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorDepositNew",null,RS_TYPE.LIST);
			logContent="重车保险费押金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorDepositList!=null&&i<motorDepositList.size();i++) {
				insertMap=motorDepositList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyMotorDeposit",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="重车保险费押金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 设备管理费收入来源于客户类型*******************************************************************************************************
			type="5";
			//List<Map<String,Object>> equMgrFeeByCustList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyEquManageFeeByCustomer",null,RS_TYPE.LIST);
			List<Map<String,Object>> equMgrFeeByCustList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyEquManageFeeByCustomerNew",null,RS_TYPE.LIST);
			logContent="设备管理费收入来源于客户类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equMgrFeeByCustList!=null&&i<equMgrFeeByCustList.size();i++) {
				insertMap=equMgrFeeByCustList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="设备管理费收入来源于客户类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 设备管理费收入来源于供应商类型*******************************************************************************************************
			type="6";
			//List<Map<String,Object>> equMgrFeeBySuplList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyEquManageFeeBySupplier",null,RS_TYPE.LIST);
			List<Map<String,Object>> equMgrFeeBySuplList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyEquManageFeeBySupplierNew",null,RS_TYPE.LIST);
			logContent="设备管理费收入来源于供应商类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equMgrFeeBySuplList!=null&&i<equMgrFeeBySuplList.size();i++) {
				insertMap=equMgrFeeBySuplList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="设备管理费收入来源于供应商类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 重车管理费收入供应商类型*************************************************************************************************************
			type="7";
			//List<Map<String,Object>> motorMgrFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorManageFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorMgrFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorManageFeeNew",null,RS_TYPE.LIST);
			logContent="重车管理费收入类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorMgrFeeList!=null&&i<motorMgrFeeList.size();i++) {
				insertMap=motorMgrFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="重车管理费收入类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 增值税收入包含设备和重车类型**********************************************************************************************************
			type="3 4";
			//List<Map<String,Object>> addedTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyAddedTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> addedTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyAddedTaxNew",null,RS_TYPE.LIST);
			logContent="增值税收入包含设备和重车类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;addedTaxList!=null&&i<addedTaxList.size();i++) {
				insertMap=addedTaxList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyAddedTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="增值税收入包含设备和重车类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 罚息类型*****************************************************************************************************************************
			type="16";
			//List<Map<String,Object>> dunTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDunTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> dunTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDunTaxNew",null,RS_TYPE.LIST);
			logContent="罚息类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;dunTaxList!=null&&i<dunTaxList.size();i++) {
				insertMap=dunTaxList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyDunTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="罚息类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 留购款类型*****************************************************************************************************************************
			type="15";
			//List<Map<String,Object>> stayPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyStayPrice",null,RS_TYPE.LIST);
			List<Map<String,Object>> stayPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyStayPriceNew",null,RS_TYPE.LIST);
			logContent="留购款类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;stayPriceList!=null&&i<stayPriceList.size();i++) {
				insertMap=stayPriceList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyStayPrice",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="留购款类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 租金,结清本金,结清利息类型**************************************************************************************************************
			type="1 2 3 4";
			//List<Map<String,Object>> rentPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyRentPrice",null,RS_TYPE.LIST);
			List<Map<String,Object>> rentPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyRentPriceNew",null,RS_TYPE.LIST);
			logContent="租金,结清本金,结清利息类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;rentPriceList!=null&&i<rentPriceList.size();i++) {
				insertMap=rentPriceList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyRentPrice",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="租金,结清本金,结清利息类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 保证金B,C类型**************************************************************************************************************************
			type="10 11";
			//List<Map<String,Object>> depositBList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositB",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositBList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositBNew",null,RS_TYPE.LIST);
			logContent="保证金B类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositBList!=null&&i<depositBList.size();i++) {
				insertMap=depositBList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyDepositB",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金B类型结束";
			this.insertLog(logContent,financeType,type);
			
			//List<Map<String,Object>> depositCList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositC",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositCList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositCNew",null,RS_TYPE.LIST);
			logContent="保证金C类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositCList!=null&&i<depositCList.size();i++) {
				insertMap=depositCList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyDepositC",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金C类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 保证金类型******************************************************************************************************************************
			type="13";
			//List<Map<String,Object>> depositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDeposit",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyDepositNew",null,RS_TYPE.LIST);
			logContent="保证金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositList!=null&&i<depositList.size();i++) {
				insertMap=depositList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyDeposit",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 重车家访费类型******************************************************************************************************************************
			type="8";
			//List<Map<String,Object>> motorVisitFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorVisitFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorVisitFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getDailyMotorVisitFeeNew",null,RS_TYPE.LIST);
			logContent="重车家访费类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorVisitFeeList!=null&&i<motorVisitFeeList.size();i++) {
				insertMap=motorVisitFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertDailyMotorVisitFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="重车家访费类型结束";
			this.insertLog(logContent,financeType,type);
			
		} catch (Exception e) {
			logger.debug("抛砖销帐日报表现金销帐出错!");
		}
	}
	//--------------------------------------------------------------------------------------------------
	
	//抛转暂收款销帐
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferLastMonthUnknown() {
		
		try {
			String logContent="";
			String financeType="1";//1代表销帐日报表中的暂收款销帐类型
			String type="";//金蝶类型

			Map<String,Object> insertMap=new HashMap<String,Object>();
			
			//销帐日报表 保证金税金类型*******************************************************************************************************************
			type="13";
			//List<Map<String,Object>> equTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> equTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositTaxNew",null,RS_TYPE.LIST);
			logContent="保证金税金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equTaxList!=null&&i<equTaxList.size();i++) {
				insertMap=equTaxList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownDepositTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金税金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 租金罚息,结清罚息类型**************************************************************************************************************
			type="15";
			//List<Map<String,Object>> dunList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDunTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> dunList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDunTaxNew",null,RS_TYPE.LIST);
			logContent="租金罚息,结清罚息类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;dunList!=null&&i<dunList.size();i++) {
				insertMap=dunList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownDunTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="租金罚息,结清罚息类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 法务费用类型***********************************************************************************************************************
			type="16";
			//List<Map<String,Object>> lawFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownLawFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> lawFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownLawFeeNew",null,RS_TYPE.LIST);
			logContent="法务费用类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;lawFeeList!=null&&i<lawFeeList.size();i++) {
				insertMap=lawFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownLawFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="法务费用类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 保险费押金类型*********************************************************************************************************************
			type="9";
			//List<Map<String,Object>> motorDepositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorDeposit",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorDepositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorDepositNew",null,RS_TYPE.LIST);
			logContent="保险费押金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorDepositList!=null&&i<motorDepositList.size();i++) {
				insertMap=motorDepositList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownMotorDeposit",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保险费押金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 设备管理费收入来源于客户类型*********************************************************************************************************
			type="5";
			//List<Map<String,Object>> equMgrFeeByCustList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownEquManageFeeByCustomer",null,RS_TYPE.LIST);
			List<Map<String,Object>> equMgrFeeByCustList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownEquManageFeeByCustomerNew",null,RS_TYPE.LIST);
			logContent="设备管理费收入来源于客户类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equMgrFeeByCustList!=null&&i<equMgrFeeByCustList.size();i++) {
				insertMap=equMgrFeeByCustList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="设备管理费收入来源于客户类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 设备管理费收入来源于供应商类型*******************************************************************************************************
			type="6";
			//List<Map<String,Object>> equMgrFeeBySuplList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownEquManageFeeBySupplier",null,RS_TYPE.LIST);
			List<Map<String,Object>> equMgrFeeBySuplList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownEquManageFeeBySupplierNew",null,RS_TYPE.LIST);
			logContent="设备管理费收入来源于供应商类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;equMgrFeeBySuplList!=null&&i<equMgrFeeBySuplList.size();i++) {
				insertMap=equMgrFeeBySuplList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="设备管理费收入来源于供应商类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 重车管理费收入供应商类型*************************************************************************************************************
			type="7";
			//List<Map<String,Object>> motorMgrFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorManageFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorMgrFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorManageFeeNew",null,RS_TYPE.LIST);
			logContent="重车管理费收入类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorMgrFeeList!=null&&i<motorMgrFeeList.size();i++) {
				insertMap=motorMgrFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownManageFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="重车管理费收入类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 增值税收入包含设备和重车类型**********************************************************************************************************
			type="3 4";
			//List<Map<String,Object>> addedTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownAddedTax",null,RS_TYPE.LIST);
			List<Map<String,Object>> addedTaxList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownAddedTaxNew",null,RS_TYPE.LIST);
			logContent="增值税收入包含设备和重车类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;addedTaxList!=null&&i<addedTaxList.size();i++) {
				insertMap=addedTaxList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownAddedTax",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="增值税收入包含设备和重车类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 留购款收入类型***********************************************************************************************************************
			type="14";
			//List<Map<String,Object>> stayPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownStayPrice",null,RS_TYPE.LIST);
			List<Map<String,Object>> stayPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownStayPriceNew",null,RS_TYPE.LIST);
			logContent="收留购款类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;stayPriceList!=null&&i<stayPriceList.size();i++) {
				insertMap=stayPriceList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownStayPrice",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="收留购款类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 租金,结清本金,结清利息类型**************************************************************************************************************
			type="1 2 3 4";
			//List<Map<String,Object>> rentPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownRentPrice",null,RS_TYPE.LIST);
			List<Map<String,Object>> rentPriceList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownRentPriceNew",null,RS_TYPE.LIST);
			logContent="租金,结清本金,结清利息类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;rentPriceList!=null&&i<rentPriceList.size();i++) {
				insertMap=rentPriceList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownRentPrice",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="租金,结清本金,结清利息类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 保证金B,C类型**************************************************************************************************************************
			type="10 11";
			//List<Map<String,Object>> depositBList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositB",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositBList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositBNew",null,RS_TYPE.LIST);
			logContent="保证金B类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositBList!=null&&i<depositBList.size();i++) {
				insertMap=depositBList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownDepositB",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金B类型结束";
			this.insertLog(logContent,financeType,type);
			
			//List<Map<String,Object>> depositCList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositC",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositCList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositCNew",null,RS_TYPE.LIST);
			logContent="保证金C类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositCList!=null&&i<depositCList.size();i++) {
				insertMap=depositCList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownDepositC",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金C类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 保证金类型******************************************************************************************************************************
			type="13";
			//List<Map<String,Object>> depositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDeposit",null,RS_TYPE.LIST);
			List<Map<String,Object>> depositList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownDepositNew",null,RS_TYPE.LIST);
			logContent="保证金类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;depositList!=null&&i<depositList.size();i++) {
				insertMap=depositList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownDeposit",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="保证金类型结束";
			this.insertLog(logContent,financeType,type);
			
			//销帐日报表 重车家访费类型******************************************************************************************************************************
			type="8";
			//List<Map<String,Object>> motorVisitFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorVisitFee",null,RS_TYPE.LIST);
			List<Map<String,Object>> motorVisitFeeList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getUnknownMotorVisitFeeNew",null,RS_TYPE.LIST);
			logContent="重车家访费类型开始";
			this.insertLog(logContent,financeType,type);
			for(int i=0;motorVisitFeeList!=null&&i<motorVisitFeeList.size();i++) {
				insertMap=motorVisitFeeList.get(i);
				DataAccessor.execute("kingDeerTransfer.insertUnknownMotorVisitFee",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			logContent="重车家访费类型结束";
			this.insertLog(logContent,financeType,type);
			
			
			//开始转移暂收款 到 当日现金销帐中   为了当月不明款
			ReportDateTo reportDateTo=(ReportDateTo)DataAccessor.query("reportDateUtil.queryDecomposeReportBeginToEndPeriod",null,RS_TYPE.OBJECT);
			
			List<Map<String,Object>> transferDataList=null;
			
			Map<String,Object> period=new HashMap<String,Object>();
			
			
			period.put("BEGINTIME",reportDateTo.getBeginTime());
			period.put("ENDTIME",reportDateTo.getEndTime());
			
			ReportDateTo reportDateTo1=ReportDateUtil.getDateByYearAndMonth(reportDateTo.getYear(),reportDateTo.getMonth());
			period.put("BEGINTIME1",reportDateTo1.getBeginTime());
			period.put("ENDTIME1",reportDateTo1.getEndTime());
			transferDataList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getCurrentUnknownData1",period,RS_TYPE.LIST);
			
			/*Map<String,Object> runTime=(Map<String,Object>)DataAccessor.query("financeDecomposeReport.getRunTime1",null,DataAccessor.RS_TYPE.MAP);
			List<Map<String,Object>> list=null;
			if(runTime!=null) {
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("BEGINTIME",runTime.get("BEGINTIME"));
				param.put("ENDTIME",runTime.get("ENDTIME"));
				param.put("RUNTIME",(String)DataAccessor.query("financeDecomposeReport.getEndDate",runTime,DataAccessor.RS_TYPE.OBJECT));
				list=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getCurrentUnknownData2",param,RS_TYPE.LIST);
				transferDataList.addAll(list);
			}*/
			for(int i=0;transferDataList!=null&&i<transferDataList.size();i++) {
				insertMap=transferDataList.get(i);//暂收款抛转表和现金抛转 以下3个类型的对应金蝶的抛转类型不同,所以做了以下转换
				if("14".equals(insertMap.get("FHANDLETYPE").toString())) {
					insertMap.put("FHANDLETYPE",15);
				} else if("15".equals(insertMap.get("FHANDLETYPE").toString())) {
					insertMap.put("FHANDLETYPE",16);
				} else if("16".equals(insertMap.get("FHANDLETYPE").toString())) {
					insertMap.put("FHANDLETYPE",18);
				}
				DataAccessor.execute("kingDeerTransfer.insertCurrentUnknownData",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
			}
			for(int i=0;transferDataList!=null&&i<transferDataList.size();i++) {
				DataAccessor.execute("kingDeerTransfer.deleteCurrentUnknownData",transferDataList.get(i),DataAccessor.OPERATION_TYPE.DELETE);
			}
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailTo("shenqi@tacleasing.cn");
			mailSettingTo.setEmailSubject("当月不明款移转至当月现金销帐");
			
			StringBuffer mailContent=new StringBuffer();
			mailContent.append("<style>" +
					".grid_table {width : 100%;border-collapse:collapse;border:solid #A6C9E2;border-width:1px 0 0 1px;overflow: hidden;}" +
					".grid_table th {border:solid #A6C9E2;border-width:0 1px 1px 0;background-color: #E1EFFB;padding : 2;margin : 1;font-weight: bold;text-align: center;color: #2E6E9E;height: 28px;font-size: 14px;font-family: '微软雅黑';}" +
					".grid_table tr {cursor: default;overflow: hidden;}" +
					".grid_table td {border:solid #A6C9E2;border-width:0 1px 1px 0;padding : 2;margin : 1;text-align: center;height: 28px;font-size: 12px;font-family: '微软雅黑';}" +
					".grid_table a {color: #0000FF;}" +
					".grid_table a:hover {color: #0000FF;font-weight: bold;text-decoration: underline;}" +
					"</style>");
			mailContent.append("<table class='grid_table'>" +
					"<tr>" +
						"<th style='text-align:center'>序号</th>" +
						"<th style='text-align:center'>来款日期</th>" +
						"<th style='text-align:center'>客户编号</th>" +
						"<th style='text-align:center'>客户名称</th>" +
						"<th style='text-align:center'>合同号</th>" +
					"</tr>");
			
			for(int i=0;transferDataList!=null&&i<transferDataList.size();i++) {
				mailContent.append("<tr>" +
						"<td style='text-align:center'>"+(i+1)+"</td>" +
						"<td style='text-align:center'>"+transferDataList.get(i).get("FDATE")+"</td>" +
						"<td style='text-align:right'>"+transferDataList.get(i).get("FCUSTNO")+"</td>" +
						"<td style='text-align:right'>"+transferDataList.get(i).get("FCUSTNAME")+"</td>" +
						"<td style='text-align:center'>"+transferDataList.get(i).get("FCONTRACTNO")+"</td>" +
								"</tr>");
					
			}						
			mailContent.append("</table>");
			mailSettingTo.setEmailContent(mailContent.toString());
			
			mailUtilService.sendMail(mailSettingTo);
		} catch(Exception e) {
			logger.debug("抛砖销帐日报表暂收款销帐出错!");
		}
	}
	
	private void insertLog(String logContent,String financeType,String type) throws Exception {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("logContent",logContent);
		param.put("financeType",financeType);
		param.put("type",type);
		DataAccessor.execute("kingDeerTransfer.insertLog",param,DataAccessor.OPERATION_TYPE.INSERT);
	}
		
	//抛转当月现金销账
/*	@SuppressWarnings("unchecked")
	public void getKingDeerTransferCashHook()
	{
		getKingDeerTransferEquiRentPrice();
		getKingDeerTransferManageFee();
		getKingDeerTransferDeposit();
		getKingDeerTransferDunPrice();
		getCurrentLawFee();
	}*/
	
	//抛转当月不明款  每月最后一天跑当月的不明款
/*	@SuppressWarnings("unchecked")
	public void getKingDeerTransferCurrentUnknownSection()
	{
		getCurrentUnknownSection();
	}*/
	
	//抛转不明款,每月第一个工作日跑出上月所有不明款,条件是 上月,平且当月期末余额不为0
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferCurrentUnknownSection() {
		
		try {
			//int result=(Integer)DataAccessor.query("kingDeerTransfer.isFirstWorkingDay",null,RS_TYPE.OBJECT);
			
			Map<String,Object> runTime=(Map<String,Object>)DataAccessor.query("financeDecomposeReport.getRunTime1",null,DataAccessor.RS_TYPE.MAP);
			if(runTime!=null) {
				String logContent="";
				String financeType="3";//3代表销帐日报表中的暂收款余额变动
				String type="17";//金蝶类型
				Map<String,Object> param=new HashMap<String,Object>();
				/*param.put("year",runTime.get("YEAR"));
				param.put("month",runTime.get("MONTH"));
				param.put("startTime",runTime.get("BEGINTIME"));
				param.put("endTime",runTime.get("ENDTIME"));
				param.put("endTime1",(String)DataAccessor.query("financeDecomposeReport.getEndDate",runTime,DataAccessor.RS_TYPE.OBJECT));
				List<Map<String,Object>> lastMonthUnknownMoneyList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getLastMonthUnknownMoney",param,RS_TYPE.LIST);*/
				param.put("runTime",runTime.get("YEAR").toString()+"-"+runTime.get("MONTH").toString());
				param.put("startTime",runTime.get("BEGINTIME"));
				param.put("endTime",runTime.get("ENDTIME"));
				List<Map<String,Object>> lastMonthUnknownMoneyList=(List<Map<String,Object>>)DataAccessor.query("kingDeerTransfer.getLastMonthUnknownMoneyNew",param,RS_TYPE.LIST);
				logContent="上月所有不明款开始";
				this.insertLog(logContent,financeType,type);
				Map<String,Object> insertMap=new HashMap<String,Object>();
				for(int i=0;lastMonthUnknownMoneyList!=null&&i<lastMonthUnknownMoneyList.size();i++) {
					insertMap=lastMonthUnknownMoneyList.get(i);
					DataAccessor.execute("kingDeerTransfer.insertLastMonthUnknownMoney",insertMap,DataAccessor.OPERATION_TYPE.INSERT);
				}
				logContent="上月所有不明款结束";
				this.insertLog(logContent,financeType,type);
			}
		} catch (Exception e) {
			
		}
	}
	
	
/*	//不明款
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferLastMonthUnknown()
	{
		getKingDeerTransferLastMonthEquiRentPrice();
		getKingDeerTransferLastMonthManageFee();
		getKingDeerTransferLastMonthDeposit();
		getKingDeerTransferLastMonthDunPrice();
		getLastMonthLawFee();
	}*/	

	//获取当天创建的供应商资料
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferSupplInfo()
	{
		
		List kingDeerTransferSuppl=new ArrayList();//供应商List

		Map supplDetail;
		try {
			kingDeerTransferSuppl = (List) DataAccessor.query("kingDeerTransfer.getSupplierInfoByDay", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferSuppl.size(); i++) {
				supplDetail = (Map) kingDeerTransferSuppl.get(i);
				supplDetail.put("TYPE", "2");
				DataAccessor.execute("kingDeerTransfer.createTransferBasicInfo", supplDetail, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}	

	//获取当天创建的员工资料
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferEmployee()
	{
		 
		List kingDeerTransferEmployee=new ArrayList();//员工List

		Map employeeDetail;
		try {
			kingDeerTransferEmployee = (List) DataAccessor.query("kingDeerTransfer.getEmployeeInfoByDay", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEmployee.size(); i++) {
				employeeDetail = (Map) kingDeerTransferEmployee.get(i);
				employeeDetail.put("TYPE", "3");
				DataAccessor.execute("kingDeerTransfer.createTransferBasicInfo", employeeDetail, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//获取当天初审的合同
	@SuppressWarnings("unchecked")
	public void getKingDeerTransferLeaseCode()
	{
		 
		List kingDeerTransferLeaseCode=new ArrayList();//合同List

		Map leaseCodeDetail;
		try {
			kingDeerTransferLeaseCode = (List) DataAccessor.query("kingDeerTransfer.getLeaseCodeInfoByDay", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferLeaseCode.size(); i++) {
				leaseCodeDetail = (Map) kingDeerTransferLeaseCode.get(i);
				leaseCodeDetail.put("TYPE", "5");
				DataAccessor.execute("kingDeerTransfer.createTransferBasicInfo", leaseCodeDetail, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//获取设备客户
	public void getKingDeerTransferEquipmentCust()
	{
		 
		List kingDeerTransferCustDetail=new ArrayList();//设备客户List
		Map custDetail;
		try {
			kingDeerTransferCustDetail = (List) DataAccessor.query("kingDeerTransfer.getEquipmentCustByDay", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferCustDetail.size(); i++) {
				custDetail = (Map) kingDeerTransferCustDetail.get(i);
				custDetail.put("TYPE", "1");
				DataAccessor.execute("kingDeerTransfer.createTransferBasicInfo", custDetail, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//获取重车客户
	public void getKingDeerTransferCarCust()
	{
		 
		List kingDeerTransferCustDetail=new ArrayList();//重车List
		Map custDetail;
		try {
			kingDeerTransferCustDetail = (List) DataAccessor.query("kingDeerTransfer.getCarCustByDay", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferCustDetail.size(); i++) {
				custDetail = (Map) kingDeerTransferCustDetail.get(i);
				custDetail.put("TYPE", "6");
				DataAccessor.execute("kingDeerTransfer.createTransferBasicInfo", custDetail, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 1:设备租金（2012年9月之前案件）；
	 * 2:重车租金（2012年9月之前案件；
	 * 3:设备租金（2012年10月之后案件；
	 * 4:重车租金（2012年10月之后案件）；
	 * 5:设备管理费收入（来源于客户）；
	 * 6:设备管理费收入（来源于供应商）；
	 * 7:重车管理费收入；
	 * 8:重车家访费收入；
	 * 9:重车保险费押金；
	 * 10:设备期末保证金B/C；
	 * 11:重车期末保证金B/C；
	 * 12:设备首租；
	 * 13:代收供应商保证金及税金；
	 * 14:付供应商保证金及税金；
	 * 15:收留购款；
	 * 16:收罚息；
	 * 17：收当月不明款；
	 * 18：收律师函费、法务费、执行费、起诉费等；
	 */
	
	//设备租金
	public void getKingDeerTransferEquiRentPrice()
	{
		List kingDeerTransferEquiRentPrice=new ArrayList();//设备租金List
		Map equiRentPrice;
		try {
			//2012-9 之前设备案件 
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getEquiRentPriceBefore9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "1");
				DataAccessor.execute("kingDeerTransfer.createEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//2012-9 之前重车案件 
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getCarRentPriceBefore9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "2");
				DataAccessor.execute("kingDeerTransfer.createEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//2012-9 之后设备案件 
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getEquiRentPriceAfter9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "3");
				DataAccessor.execute("kingDeerTransfer.createEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//2012-9 之后重车案件 
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getCarRentPriceAfter9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "4");
				DataAccessor.execute("kingDeerTransfer.createEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保存设备增值税
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getEquiValueAddedTax", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "3");
				DataAccessor.execute("kingDeerTransfer.createValueAddedTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保存重车增值税
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getCarValueAddedTax", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "4");
				DataAccessor.execute("kingDeerTransfer.createValueAddedTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//管理费收入
	public void getKingDeerTransferManageFee()
	{
		List kingDeerTransferManageFee=new ArrayList();//管理费List
		Map equiRentPrice;
		try {
			//设备管理费 来源承租人
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getEquiManageFeeCust", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "5");
				DataAccessor.execute("kingDeerTransfer.createManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//设备管理费 来源供应商
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getEquiManageFeeSuppl", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "6");
				DataAccessor.execute("kingDeerTransfer.createManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车管理费
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getCarManageFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "7");
				DataAccessor.execute("kingDeerTransfer.createManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车家访费
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getCarVisitFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "8");
				DataAccessor.execute("kingDeerTransfer.createVisitFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保险费押金
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getCarInsureFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "9");
				DataAccessor.execute("kingDeerTransfer.createInsureFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//保证金B/C 
	public void getKingDeerTransferDeposit()
	{
		List kingDeerTransferDeposit=new ArrayList();//保证金 B/C List
		Map equiRentPrice;
		try {
			//设备保证金B
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getEquiDepositB", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "10");
				DataAccessor.execute("kingDeerTransfer.createDepositB", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//设备保证金C
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getEquiDepositC", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "10");
				DataAccessor.execute("kingDeerTransfer.createDepositC", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保证金B
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getCarDepositB", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "11");
				DataAccessor.execute("kingDeerTransfer.createDepositB", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保证金C
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getCarDepositC", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "11");
				DataAccessor.execute("kingDeerTransfer.createDepositC", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保证金
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getDeposit", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "13");
				DataAccessor.execute("kingDeerTransfer.createDeposit", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}			
			//保证金税金
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getDepositTax", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "13");
				DataAccessor.execute("kingDeerTransfer.createDepositTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//留购款、罚息
	public void getKingDeerTransferDunPrice()
	{
		List kingDeerTransferDeposit=new ArrayList();//留购款、罚息 List
		Map equiRentPrice;
		try {
			//留购款
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getStayPrice", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "15");
				DataAccessor.execute("kingDeerTransfer.createStayPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//罚息
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getDunPrice", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "16");
				DataAccessor.execute("kingDeerTransfer.createDunPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
		
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/*
	 * 当月不明款
	 * 每月最后一天跑当月的不明款
	 */
	public void getCurrentUnknownSection()
	{
		List kingDeerTransferUnSection=new ArrayList();//不明来款 List
		Map equiRentPrice;
		try {
			//留购款
			kingDeerTransferUnSection = (List) DataAccessor.query("kingDeerTransfer.getCurrentUnknownSection", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferUnSection.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferUnSection.get(i);
				equiRentPrice.put("TYPE", "17");
				DataAccessor.execute("kingDeerTransfer.createCurrentUnknownSection", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 收律师函费、起诉费
	 */
	public void getCurrentLawFee()
	{
		List kingDeerTransferCurrentLawFee=new ArrayList();//法务费用 List
		Map equiRentPrice;
		try {
			//收律师函费、起诉费
			kingDeerTransferCurrentLawFee = (List) DataAccessor.query("kingDeerTransfer.getCurrentLawFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferCurrentLawFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferCurrentLawFee.get(i);
				equiRentPrice.put("TYPE", "18");
				DataAccessor.execute("kingDeerTransfer.createCurrentLawFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	
	
	/**
	 * 非当月来款销账
	 */
	public void getKingDeerTransferLastMonthEquiRentPrice()
	{
		List kingDeerTransferEquiRentPrice=new ArrayList();//设备租金List
		Map equiRentPrice;
		try {
			//2012-9 之前设备案件 
//			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiRentPriceBefore9", null, DataAccessor.RS_TYPE.LIST);
//			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
//				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
//				equiRentPrice.put("TYPE", "1");
//				DataAccessor.execute("kingDeerTransfer.createLastMonthEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
//			}
			//2012-9 之前重车案件 
//			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarRentPriceBefore9", null, DataAccessor.RS_TYPE.LIST);
//			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
//				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
//				equiRentPrice.put("TYPE", "2");
//				DataAccessor.execute("kingDeerTransfer.createLastMonthEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
//			}
			
			//2012-9 之后设备案件 
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiRentPriceAfter9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "3");
				DataAccessor.execute("kingDeerTransfer.createLastMonthEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//2012-9 之后重车案件 
			//释放此段代码 ShenQi 因为会有客户少汇几块钱租金,业务员为了不逾期,垫上金额,所以在当月来款中体现不出
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarRentPriceAfter9", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "4");
				DataAccessor.execute("kingDeerTransfer.createLastMonthEquiRentPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保存设备增值税
			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiValueAddedTax", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
				equiRentPrice.put("TYPE", "3");
				DataAccessor.execute("kingDeerTransfer.createLastMonthValueAddedTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保存重车增值税
//			kingDeerTransferEquiRentPrice = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarValueAddedTax", null, DataAccessor.RS_TYPE.LIST);
//			for (int i = 0; i < kingDeerTransferEquiRentPrice.size(); i++) {
//				equiRentPrice = (Map) kingDeerTransferEquiRentPrice.get(i);
//				equiRentPrice.put("TYPE", "4");
//				DataAccessor.execute("kingDeerTransfer.createLastMonthValueAddedTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
//			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//管理费收入
	public void getKingDeerTransferLastMonthManageFee()
	{
		List kingDeerTransferManageFee=new ArrayList();//管理费List
		Map equiRentPrice;
		try {
			//设备管理费 来源承租人
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiManageFeeCust", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "5");
				DataAccessor.execute("kingDeerTransfer.createLastMonthManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//设备管理费 来源供应商
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiManageFeeSuppl", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "6");
				DataAccessor.execute("kingDeerTransfer.createLastMonthManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车管理费
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarManageFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "7");
				DataAccessor.execute("kingDeerTransfer.createLastMonthManageFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车家访费
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarVisitFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "8");
				DataAccessor.execute("kingDeerTransfer.createLastMonthVisitFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保险费押金
			kingDeerTransferManageFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarInsureFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferManageFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferManageFee.get(i);
				equiRentPrice.put("TYPE", "9");
				DataAccessor.execute("kingDeerTransfer.createLastMonthInsureFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//保证金B/C 
	public void getKingDeerTransferLastMonthDeposit()
	{
		List kingDeerTransferDeposit=new ArrayList();//保证金 B/C List
		Map equiRentPrice;
		try {
			//设备保证金B
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiDepositB", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "10");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDepositB", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//设备保证金C
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthEquiDepositC", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "10");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDepositC", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保证金B
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarDepositB", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "11");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDepositB", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
			//重车保证金C
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthCarDepositC", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "11");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDepositC", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//保证金
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthDeposit", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "13");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDeposit", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}			
			//保证金税金
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthDepositTax", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "13");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDepositTax", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//留购款、罚息
	public void getKingDeerTransferLastMonthDunPrice()
	{
		List kingDeerTransferDeposit=new ArrayList();//留购款、罚息 List
		Map equiRentPrice;
		try {
			//留购款
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthStayPrice", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "14");
				DataAccessor.execute("kingDeerTransfer.createLastMonthStayPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
			//罚息
			kingDeerTransferDeposit = (List) DataAccessor.query("kingDeerTransfer.getLastMonthDunPrice", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferDeposit.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferDeposit.get(i);
				equiRentPrice.put("TYPE", "15");
				DataAccessor.execute("kingDeerTransfer.createLastMonthDunPrice", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
		
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 收律师函费、起诉费
	 */
	public void getLastMonthLawFee()
	{
		List kingDeerTransferCurrentLawFee=new ArrayList();//法务费用 List
		Map equiRentPrice;
		try {
			//收律师函费、起诉费
			kingDeerTransferCurrentLawFee = (List) DataAccessor.query("kingDeerTransfer.getLastMonthLawFee", null, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < kingDeerTransferCurrentLawFee.size(); i++) {
				equiRentPrice = (Map) kingDeerTransferCurrentLawFee.get(i);
				equiRentPrice.put("TYPE", "16");
				DataAccessor.execute("kingDeerTransfer.createLastMonthLawFee", equiRentPrice, DataAccessor.OPERATION_TYPE.INSERT);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
}
