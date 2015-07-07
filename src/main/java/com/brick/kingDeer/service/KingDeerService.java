package com.brick.kingDeer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.base.service.BaseService;
import com.brick.coderule.service.CodeRule;
import com.brick.kingDeer.dao.KingDeerDAO;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.StringUtils;

public class KingDeerService extends BaseService {
	
	private long baseNum=50000000;
	private KingDeerDAO kingDeerDAO;

	public KingDeerDAO getKingDeerDAO() {
		return kingDeerDAO;
	}

	public void setKingDeerDAO(KingDeerDAO kingDeerDAO) {
		this.kingDeerDAO = kingDeerDAO;
	}
	
	public boolean checkResult(Context context) {//检查此数据是否生成凭证了,防止重复开tab
		boolean flag=false;
		if("Y".equals(this.kingDeerDAO.checkResult(context))) {
			flag=true;
		}
		return flag;
	}
	
	//********************************************************************************拨款类
	public void generateVoucherForPaymentOfCar(Context context) throws Exception {
		Map<String,Object> resultMap=this.kingDeerDAO.paymentQueryForCar(context);
		context.contextMap.put("errorMsg","");
		if(Constants.COMPANY_CODE==Integer.valueOf(resultMap.get("COMPANY_CODE").toString())) {//裕融
			if(Constants.CONTRACT_TYPE_8.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车委贷,现在包含新车委贷,二手车委贷
			   Constants.CONTRACT_TYPE_14.equals(resultMap.get("CONTRACT_TYPE").toString())) {
				//测试存储过程
				Map<String,Object> param=new HashMap<String,Object>();
				
				param.put("ACCOUNT_TYPE",1);//账套别
				param.put("BATCH_NUM",CodeRule.getKingDeerCode());//批次号
				param.put("COMPANY_CODE",Integer.valueOf(resultMap.get("COMPANY_CODE")+""));//公司别,not used in pro
				param.put("LOGIN_NAME",context.contextMap.get("s_employeeName"));//租赁系统登录名
				param.put("PAY_DATE",resultMap.get("PAY_DATE"));//拨款日或者来款日
				param.put("SETTLE_DATE",context.contextMap.get("settleDate"));//获得结账日期
				param.put("REMARK","支付委托车贷款/"+resultMap.get("CUST_CODE")+"/"+resultMap.get("CUST_NAME")+"/"+resultMap.get("LEASE_CODE"));
				param.put("CODE_NAME","1");
				
				param.put("B","1481.001");
				param.put("C",0);//借
				param.put("D",resultMap.get("PAY_MONEY"));//借方金额
				
				param.put("tacCode",resultMap.get("BANK_ACCOUNT"));//银行帐号
				String subjectCode=this.kingDeerDAO.getKingDeerBaseSubject(param);
				if(StringUtils.isEmpty(subjectCode)) {
					context.contextMap.put("errorMsg","银行帐号在金蝶中不存在");
					return;
				}
				param.put("B1",subjectCode);//通过银行帐号取科目代码
				param.put("C1",1);//贷
				param.put("D1",resultMap.get("PAY_MONEY"));//贷方金额
				
				param.put("tacCode",resultMap.get("CUST_CODE"));//客户编号
				String custCode=this.kingDeerDAO.getKingDeerBaseCustomer(param);
				if(StringUtils.isEmpty(custCode)) {
					context.contextMap.put("errorMsg","客户编号在金蝶中不存在");
					return;
				}
				param.put("E",custCode);
				
				param.put("tacCode",resultMap.get("LEASE_CODE"));//合同号
				String leaseCode=this.kingDeerDAO.getKingDeerBaseLease(param);
				if(StringUtils.isEmpty(leaseCode)) {
					context.contextMap.put("errorMsg","客户编号在金蝶中不存在");
					return;
				}
				param.put("F",leaseCode);
				param.put("G","");
				param.put("H","");
				param.put("I","");
				param.put("J","");
				
				this.kingDeerDAO.paymentForCarI(param);
				this.kingDeerDAO.k3Transfer(param);
				param.put("id",context.contextMap.get("id"));//更新批次号,操作人,操作时间等
				param.put("s_employeeId",context.contextMap.get("s_employeeId"));
				this.kingDeerDAO.updateBatchNum(param);
			} else if(Constants.CONTRACT_TYPE_10.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车售后回租
					  Constants.CONTRACT_TYPE_12.equals(resultMap.get("CONTRACT_TYPE").toString())||
					  Constants.CONTRACT_TYPE_13.equals(resultMap.get("CONTRACT_TYPE").toString())) {
				
			}
		} else {//裕国
			if(Constants.CONTRACT_TYPE_8.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车委贷,现在包含新车委贷,二手车委贷
					   Constants.CONTRACT_TYPE_14.equals(resultMap.get("CONTRACT_TYPE").toString())) {
						//测试存储过程
						Map<String,Object> param=new HashMap<String,Object>();
						
						param.put("ACCOUNT_TYPE",2);//账套别
						param.put("BATCH_NUM",CodeRule.getKingDeerCode());//批次号
						param.put("COMPANY_CODE",Integer.valueOf(resultMap.get("COMPANY_CODE")+""));//公司别,not used in pro
						param.put("LOGIN_NAME",context.contextMap.get("s_employeeName"));//租赁系统登录名
						param.put("PAY_DATE",resultMap.get("PAY_DATE"));//拨款日或者来款日
						param.put("SETTLE_DATE",context.contextMap.get("settleDate"));//获得结账日期
						param.put("REMARK","支付委托车贷款/"+resultMap.get("CUST_CODE")+"/"+resultMap.get("CUST_NAME")+"/"+resultMap.get("LEASE_CODE"));
						param.put("CODE_NAME","1");
						
						param.put("B","1481.001");
						param.put("C",0);//借
						param.put("D",resultMap.get("PAY_MONEY"));//借方金额
						
						param.put("tacCode",resultMap.get("BANK_ACCOUNT"));//银行帐号
						String subjectCode=this.kingDeerDAO.getKingDeerBaseSubject(param);
						if(StringUtils.isEmpty(subjectCode)) {
							context.contextMap.put("errorMsg","银行帐号在金蝶中不存在");
							return;
						}
						param.put("B1",subjectCode);//通过银行帐号取科目代码
						param.put("C1",1);//贷
						param.put("D1",resultMap.get("PAY_MONEY"));//贷方金额
						
						param.put("tacCode",resultMap.get("CUST_CODE"));//客户编号
						String custCode=this.kingDeerDAO.getKingDeerBaseCustomer(param);
						if(StringUtils.isEmpty(custCode)) {
							context.contextMap.put("errorMsg","客户编号在金蝶中不存在");
							return;
						}
						param.put("E",custCode);
						
						param.put("tacCode",resultMap.get("LEASE_CODE"));//合同号
						String leaseCode=this.kingDeerDAO.getKingDeerBaseLease(param);
						if(StringUtils.isEmpty(leaseCode)) {
							context.contextMap.put("errorMsg","客户编号在金蝶中不存在");
							return;
						}
						param.put("F",leaseCode);
						param.put("G","");
						param.put("H","");
						param.put("I","");
						param.put("J","");
						
						this.kingDeerDAO.paymentForCarI(param);
						//this.kingDeerDAO.k3Transfer(param);
						param.put("id",context.contextMap.get("id"));//更新批次号,操作人,操作时间等
						param.put("s_employeeId",context.contextMap.get("s_employeeId"));
						this.kingDeerDAO.updateBatchNum(param);
					} else if(Constants.CONTRACT_TYPE_10.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车售后回租
							  Constants.CONTRACT_TYPE_12.equals(resultMap.get("CONTRACT_TYPE").toString())||
							  Constants.CONTRACT_TYPE_13.equals(resultMap.get("CONTRACT_TYPE").toString())) {
						
					}
		}
	}
	public void generateVoucherForPaymentOfEqu(Context context) throws Exception {
		
	}
	public void generateVoucherForPaymentOfMotor(Context context) throws Exception {
		
	}
	
	public void batchGenerateVoucherForPaymentOfCar(Context context) throws Exception {
		List<String> idsList=(List<String>)context.request.getSession().getAttribute("batchIds");
		context.contextMap.put("BATCH_NUM",CodeRule.getKingDeerCode());//设置批量的batch num
		if(idsList==null) {
			context.contextMap.put("msg","无数据抛转!");
			return;
		} else {
			if("CAR".equals(context.contextMap.get("flag"))) {//乘用车拨款
				for(int i=0;i<idsList.size();i++) {
					context.contextMap.put("contractType",-1);
					context.contextMap.put("id",idsList.get(i));
					boolean result=this.checkResult(context);
					if(result) {//已经抛转够了,跳过不抛转
						continue;
					}
					Map<String,Object> resultMap=this.kingDeerDAO.paymentQueryForCar(context);
					if(Constants.CONTRACT_TYPE_8.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车委贷,现在包含新车委贷,二手车委贷
							   Constants.CONTRACT_TYPE_14.equals(resultMap.get("CONTRACT_TYPE").toString())) {
								//测试存储过程
								Map<String,Object> param=new HashMap<String,Object>();
								
								param.put("ACCOUNT_TYPE",2);//账套别
								param.put("BATCH_NUM",context.contextMap.get("BATCH_NUM"));//批次号
								param.put("COMPANY_CODE",Integer.valueOf(resultMap.get("COMPANY_CODE")+""));//公司别,not used in pro
								param.put("LOGIN_NAME",context.contextMap.get("s_employeeName"));//租赁系统登录名
								param.put("PAY_DATE",resultMap.get("PAY_DATE"));//拨款日或者来款日
								param.put("SETTLE_DATE",context.contextMap.get("settleDate"));//获得结账日期
								param.put("REMARK","支付委托车贷款/"+resultMap.get("CUST_CODE")+"/"+resultMap.get("CUST_NAME")+"/"+resultMap.get("LEASE_CODE"));
								param.put("CODE_NAME","1");
								
								param.put("B","1481.001");
								param.put("C",0);//借
								param.put("D",resultMap.get("PAY_MONEY"));//借方金额
								
								param.put("tacCode",resultMap.get("BANK_ACCOUNT"));//银行帐号
								String subjectCode=this.kingDeerDAO.getKingDeerBaseSubject(param);
								if(StringUtils.isEmpty(subjectCode)) {
									context.contextMap.put("msg","银行帐号在金蝶中不存在");
									return;
								}
								param.put("B1",subjectCode);//通过银行帐号取科目代码
								param.put("C1",1);//贷
								param.put("D1",resultMap.get("PAY_MONEY"));//贷方金额
								
								param.put("tacCode",resultMap.get("CUST_CODE"));//客户编号
								String custCode=this.kingDeerDAO.getKingDeerBaseCustomer(param);
								if(StringUtils.isEmpty(custCode)) {
									context.contextMap.put("msg","客户编号在金蝶中不存在");
									return;
								}
								param.put("E",custCode);
								
								param.put("tacCode",resultMap.get("LEASE_CODE"));//合同号
								String leaseCode=this.kingDeerDAO.getKingDeerBaseLease(param);
								if(StringUtils.isEmpty(leaseCode)) {
									context.contextMap.put("msg","客户编号在金蝶中不存在");
									return;
								}
								param.put("F",leaseCode);
								param.put("G","");
								param.put("H","");
								param.put("I","");
								param.put("J","");
								
								this.kingDeerDAO.paymentForCarI(param);
								param.put("id",context.contextMap.get("id"));//更新批次号,操作人,操作时间等
								param.put("s_employeeId",context.contextMap.get("s_employeeId"));
								this.kingDeerDAO.updateBatchNum(param);
							} else if(Constants.CONTRACT_TYPE_10.equals(resultMap.get("CONTRACT_TYPE").toString())||//乘用车售后回租
									  Constants.CONTRACT_TYPE_12.equals(resultMap.get("CONTRACT_TYPE").toString())||
									  Constants.CONTRACT_TYPE_13.equals(resultMap.get("CONTRACT_TYPE").toString())) {
								
							}
				}
			} else if("EQU".equals(context.contextMap.get("flag"))) {//设备拨款
				
			} else if("MOTOR".equals(context.contextMap.get("flag"))) {//商用车拨款
				
			}
			
			this.kingDeerDAO.k3Transfer(context.contextMap);//生成凭证
			context.contextMap.put("msg","抛转完成!");
		}
	}
	//********************************************************************************
	
	
	
	
	//获得金蝶基本档数据,不加入事务,因为乘用车与设备租赁系统共用金蝶数据库,而2个系统都有此job
	public void batchJobForKingDeerBaseData() throws Exception {
		
		List<Map<String,Object>> custInfo=this.kingDeerDAO.getKingDeerBaseCustInfo();
		List<Map<String,Object>> suplInfo=this.kingDeerDAO.getKingDeerBaseSuplInfo();
		List<Map<String,Object>> userInfo=this.kingDeerDAO.getKingDeerBaseUserInfo();
		List<Map<String,Object>> leaseInfo=this.kingDeerDAO.getKingDeerBaseLeaseInfo();
		List<Map<String,Object>> cmpyInfo=this.kingDeerDAO.getKingDeerBaseCmpyInfo();
		
		//客户基本档
		for(int i=0;custInfo!=null&&i<custInfo.size();i++) {
			if(StringUtils.isEmpty(custInfo.get(i).get("TAC_CODE"))) {
				continue;
			}
			long pkId=this.kingDeerDAO.insertKingDeerBaseInfoForTac(custInfo.get(i));//插入租赁系统的金蝶基础表
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("pkId",pkId);
			param.put("fItemID",baseNum+pkId);
			custInfo.get(i).put("FITEMID",baseNum+pkId);
			this.kingDeerDAO.updateKingDeerBaseInfoForTac(param);//更新租赁系统的金蝶基础表的FItemID
			this.kingDeerDAO.insertKingDeerBaseInfoForKingDeer(custInfo.get(i));//插入金蝶T_Item表
			this.kingDeerDAO.insertKingDeerOrganization(custInfo.get(i));
		}
		
		//供应商基本档
		for(int i=0;suplInfo!=null&&i<suplInfo.size();i++) {
			if(StringUtils.isEmpty(suplInfo.get(i).get("TAC_CODE"))) {
				continue;
			}
			long pkId=this.kingDeerDAO.insertKingDeerBaseInfoForTac(suplInfo.get(i));//插入租赁系统的金蝶基础表
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("pkId",pkId);
			param.put("fItemID",baseNum+pkId);
			suplInfo.get(i).put("FITEMID",baseNum+pkId);
			this.kingDeerDAO.updateKingDeerBaseInfoForTac(param);//更新租赁系统的金蝶基础表的FItemID
			this.kingDeerDAO.insertKingDeerBaseInfoForKingDeer(suplInfo.get(i));//插入金蝶T_Item表
			this.kingDeerDAO.insertKingDeerSupplier(suplInfo.get(i));
		}
		//职员基本档
		for(int i=0;userInfo!=null&&i<userInfo.size();i++) {
			if(StringUtils.isEmpty(userInfo.get(i).get("TAC_CODE"))) {
				continue;
			}
			long pkId=this.kingDeerDAO.insertKingDeerBaseInfoForTac(userInfo.get(i));//插入租赁系统的金蝶基础表
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("pkId",pkId);
			param.put("fItemID",baseNum+pkId);
			userInfo.get(i).put("FITEMID",baseNum+pkId);
			this.kingDeerDAO.updateKingDeerBaseInfoForTac(param);//更新租赁系统的金蝶基础表的FItemID
			this.kingDeerDAO.insertKingDeerBaseInfoForKingDeer(userInfo.get(i));//插入金蝶T_Item表
			this.kingDeerDAO.insertKingDeerEmp(userInfo.get(i));
		}

		//合同基本档
		for(int i=0;leaseInfo!=null&&i<leaseInfo.size();i++) {
			if(StringUtils.isEmpty(leaseInfo.get(i).get("TAC_CODE"))) {
				continue;
			}
			long pkId=this.kingDeerDAO.insertKingDeerBaseInfoForTac(leaseInfo.get(i));//插入租赁系统的金蝶基础表
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("pkId",pkId);
			param.put("fItemID",baseNum+pkId);
			leaseInfo.get(i).put("FITEMID",baseNum+pkId);
			this.kingDeerDAO.updateKingDeerBaseInfoForTac(param);//更新租赁系统的金蝶基础表的FItemID
			this.kingDeerDAO.insertKingDeerBaseInfoForKingDeer(leaseInfo.get(i));//插入金蝶T_Item表
		}
		//国别地区基本档
		for(int i=0;cmpyInfo!=null&&i<cmpyInfo.size();i++) {
			if(StringUtils.isEmpty(cmpyInfo.get(i).get("TAC_CODE"))) {
				continue;
			}
			long pkId=this.kingDeerDAO.insertKingDeerBaseInfoForTac(cmpyInfo.get(i));//插入租赁系统的金蝶基础表
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("pkId",pkId);
			param.put("fItemID",baseNum+pkId);
			cmpyInfo.get(i).put("FITEMID",baseNum+pkId);
			this.kingDeerDAO.updateKingDeerBaseInfoForTac(param);//更新租赁系统的金蝶基础表的FItemID
			this.kingDeerDAO.insertKingDeerBaseInfoForKingDeer(cmpyInfo.get(i));//插入金蝶T_Item表
		}
	}
	
	public List<Map<String,Object>> batchQueryForCar(Map<String,Object> param) throws Exception {
		return this.kingDeerDAO.batchQueryForCar(param);
	}
}
