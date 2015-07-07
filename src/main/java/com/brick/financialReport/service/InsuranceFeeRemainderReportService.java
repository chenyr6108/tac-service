package com.brick.financialReport.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.financialReport.dao.InsuranceFeeRemainderReportDAO;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class InsuranceFeeRemainderReportService extends BaseService {

	private InsuranceFeeRemainderReportDAO insuranceFeeRemainderReportDAO;
	
	public InsuranceFeeRemainderReportDAO getInsuranceFeeRemainderReportDAO() {
		return insuranceFeeRemainderReportDAO;
	}

	public void setInsuranceFeeRemainderReportDAO(
			InsuranceFeeRemainderReportDAO insuranceFeeRemainderReportDAO) {
		this.insuranceFeeRemainderReportDAO = insuranceFeeRemainderReportDAO;
	}

	//保险费余额变动表batch job
	@Transactional
	public void generateInsuranceFeeRemainderReport() throws Exception {
		
		try {
			//获得昨天新生成合同的支付表保险明细
			this.generateInsuranceFeeRemainderPay();
			
			//生成昨天的保险明细费率变动报表
			this.generateInsuranceFeeRemainderFinancial();
			
			//跑特殊数据,支付表创建日期晚于第一期支付日期的数据
			this.generateInsuranceFeeRemainderFinancial(this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPaySpecial());
			
		} catch(Exception e) {
			throw e;
		}
	}
	
	private void generateInsuranceFeeRemainderPay() throws Exception {
		
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("startDate",DateUtil.dateToStr(cal.getTime()));
		param.put("endDate",DateUtil.dateToStr(cal.getTime()));
		
		try {
			List<Map<String,Object>> resultList=this.insuranceFeeRemainderReportDAO.getHistoryInsuranceFeeRemainderPay(param);
			
			for(int i=0;resultList!=null&&i<resultList.size();i++) {
				resultList.get(i).put("s_employeeId",184);
				this.insuranceFeeRemainderReportDAO.generateHistoryInsuranceFeeRemainderPay(resultList.get(i));
			}
		} catch(Exception e) {
			throw e;
		} 
	}
	
	private void generateInsuranceFeeRemainderFinancial() throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		
		List<Map<String,Object>> resultList=this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPay(param);
		
		Map<String,Object> insertMap=new HashMap<String,Object>();
		
		for(int i=0;resultList!=null&&i<resultList.size();i++) {
			
			param.put("RECP_ID",resultList.get(i).get("RECP_ID"));
			
			insertMap.clear();//初始化清除待插入的数据
			
			insertMap=this.insuranceFeeRemainderReportDAO.getOtherInformation(param);
			
			if(insertMap==null) {
				insertMap=new HashMap<String,Object>();
			}
			//设定设备总价款
			insertMap.put("LEASE_TOPRIC",resultList.get(i).get("LEASE_TOPRIC"));
			//租赁期数
			insertMap.put("LEASE_PERIOD",resultList.get(i).get("LEASE_PERIOD"));
			//当前期数
			insertMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
			//支付表状态 0正常,1正常结清,3是提前结清
			insertMap.put("RECP_STATUS",resultList.get(i).get("RECP_STATUS"));
			//保费
			insertMap.put("INSURE",resultList.get(i).get("INSURE"));
			//支付日期
			insertMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
			
			//如果是第一期的数据则不需要查询历史数据进行计算,因为报表显示栏位有期初余额,期末余额
			if("1".equals(String.valueOf(resultList.get(i).get("PERIOD_NUM")))) {
				//期初余额
				insertMap.put("BEGIN_MONEY",0);
				//本期新增,第一期的话,本期新增等于保费总额
				insertMap.put("INCREASE_MONEY",resultList.get(i).get("INSURE"));
				//本期减少
				insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
				//期末余额 等于期初余额+本期新增-本期减少  因为第一期期初余额=0 本期新增=保费总额 所以下面直接用保险总额-本期减少的
				insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
				
				//插入数据
				this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
			} else {
				//如果不是第一期的数据需要查询历史数据进行计算
				param.put("NUM",resultList.get(i).get("NUM"));
				List<Map<String,Object>> oneContractInsuranceFeeRemainderList=this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPayByRecpId(param);
				
				double monthInsuranceTotal=0;
				if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))||"1".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {//正常的,正常结清的
					//按照会记部的逻辑组装报表的最终数据
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					
					//期初余额
					insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
					//本期新增
					insertMap.put("INCREASE_MONEY",0);
					
					if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {
						//本期减少
						insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
						//期末余额
						insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
					} else {
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString())+Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
						//期末余额
						insertMap.put("END_MONEY",0);
					}
				
					//插入数据
					this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
				} else {//提前结清的,或者结清的
					//按照会记部的逻辑组装报表的最终数据
					
					String lastPayDate="";
					double reduceMoney=0;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//提前结清的逻辑和非提前结清的逻辑不同,所以有以下逻辑分支
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {//组装已付的保险费
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					boolean flag=true;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//组装最后一期的本期减少,需要把提前结清开始后所有期数的保险费加起来
						if(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("DAY").toString())<=0) {//用于判断提前结清 最后一期应该在报表生成的数据
							if(flag) {
								lastPayDate=oneContractInsuranceFeeRemainderList.get(j).get("PAY_DATE").toString();
								flag=false;
							}
							reduceMoney=(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("LEASE_PERIOD").toString())
										-Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("PERIOD_NUM").toString())+1)
										*Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
							break;
						}
					}
					
					if(lastPayDate.equals(resultList.get(i).get("PAY_DATE_1").toString())) {//如果报表日期不等于提前结清最后一期应该该显示的数据的支付日,则不插入生成报表
						//期初余额
						insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//本期新增
						insertMap.put("INCREASE_MONEY",0);
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//期末余额
						insertMap.put("END_MONEY",0);
						//插入数据
						this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
					}
				}
			}
		}
	}
	
	private void generateInsuranceFeeRemainderFinancial(List<Map<String,Object>> resultList) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		
		Map<String,Object> insertMap=new HashMap<String,Object>();
		
		for(int i=0;resultList!=null&&i<resultList.size();i++) {
			
			param.put("RECP_ID",resultList.get(i).get("RECP_ID"));
			
			insertMap.clear();//初始化清除待插入的数据
			
			insertMap=this.insuranceFeeRemainderReportDAO.getOtherInformation(param);
			
			//设定设备总价款
			insertMap.put("LEASE_TOPRIC",resultList.get(i).get("LEASE_TOPRIC"));
			//租赁期数
			insertMap.put("LEASE_PERIOD",resultList.get(i).get("LEASE_PERIOD"));
			//当前期数
			insertMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
			//支付表状态 0正常,1正常结清,3是提前结清
			insertMap.put("RECP_STATUS",resultList.get(i).get("RECP_STATUS"));
			//保费
			insertMap.put("INSURE",resultList.get(i).get("INSURE"));
			//支付日期
			insertMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
			
			//如果是第一期的数据则不需要查询历史数据进行计算,因为报表显示栏位有期初余额,期末余额
			if("1".equals(String.valueOf(resultList.get(i).get("PERIOD_NUM")))) {
				//期初余额
				insertMap.put("BEGIN_MONEY",0);
				//本期新增,第一期的话,本期新增等于保费总额
				insertMap.put("INCREASE_MONEY",resultList.get(i).get("INSURE"));
				//本期减少
				insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
				//期末余额 等于期初余额+本期新增-本期减少  因为第一期期初余额=0 本期新增=保费总额 所以下面直接用保险总额-本期减少的
				insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
				
				//插入数据
				this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
			} else {
				//如果不是第一期的数据需要查询历史数据进行计算
				param.put("reportDate",resultList.get(i).get("PAY_DATE"));
				param.put("NUM",0);
				List<Map<String,Object>> oneContractInsuranceFeeRemainderList=this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPayByRecpId(param);
				
				double monthInsuranceTotal=0;
				if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))||"1".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {//正常的,正常结清的
					//按照会记部的逻辑组装报表的最终数据
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					
					//期初余额
					insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
					//本期新增
					insertMap.put("INCREASE_MONEY",0);
					
					if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {
						//本期减少
						insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
						//期末余额
						insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
					} else {
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString())+Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
						//期末余额
						insertMap.put("END_MONEY",0);
					}
				
					//插入数据
					this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
				} else {//提前结清的
					//按照会记部的逻辑组装报表的最终数据
					
					String lastPayDate="";
					double reduceMoney=0;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//提前结清的逻辑和非提前结清的逻辑不同,所以有以下逻辑分支
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {//组装已付的保险费
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					boolean flag=true;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//组装最后一期的本期减少,需要把提前结清开始后所有期数的保险费加起来
						if(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("DAY").toString())<=0) {//用于判断提前结清 最后一期应该在报表生成的数据
							if(flag) {
								lastPayDate=oneContractInsuranceFeeRemainderList.get(j).get("PAY_DATE").toString();
								flag=false;
							}
							reduceMoney=(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("LEASE_PERIOD").toString())
										-Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("PERIOD_NUM").toString())+1)
										*Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
							break;
						}
					}
					
					if(lastPayDate.equals(resultList.get(i).get("PAY_DATE_1").toString())) {//如果报表日期不等于提前结清最后一期应该该显示的数据的支付日,则不插入生成报表
						//期初余额
						insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//本期新增
						insertMap.put("INCREASE_MONEY",0);
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//期末余额
						insertMap.put("END_MONEY",0);
						//插入数据
						this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
					}
				}
			}
		}
	}
	
	//用于手动重新生成历史合同的支付表保险明细,或者开放业务支撑让用户自己重新跑
	@Transactional
	public void generateHistoryInsuranceFeeRemainderPay(Context context) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("startDate",context.contextMap.get("startDate"));
		param.put("endDate",context.contextMap.get("endDate"));
		param.put("recpCode",context.contextMap.get("recpCode"));
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		try {
			List<Map<String,Object>> resultList=this.insuranceFeeRemainderReportDAO.getHistoryInsuranceFeeRemainderPay(param);
		
			//因为resultList中的结构每行数据是1个支付表的1个支付日,所以一个RECP_ID会对应12行或者24行或者36行等
			//所以定义lastRecpId,currentRecpId用作于去除重复的SQL操作
			String lastRecpId="";
			String currentRecpId="";
			for(int i=0;resultList!=null&&i<resultList.size();i++) {
				currentRecpId=resultList.get(i).get("RECP_ID").toString();
				if(!lastRecpId.equals(currentRecpId)) {//控制重复操作,每个支付表的第一个RECP_ID才做检查
					//跑历史数据,首先需要把保险支付表明细里存在的RECP_ID的数据状态更新成-1
					param.put("RECP_ID",currentRecpId);
					int flag=this.insuranceFeeRemainderReportDAO.checkHistoryInsuranceFeeRemainderPay(param);
					lastRecpId=currentRecpId;
					
					if(flag>0) {//如果flag大于0,则历史数据中存在此支付表的保险支付表,把此行数据状态更新成-1
						this.insuranceFeeRemainderReportDAO.cancelHistoryInsuranceFeeRemainderPay(param);
					}
				}
				resultList.get(i).put("s_employeeId",context.contextMap.get("s_employeeId"));
				this.insuranceFeeRemainderReportDAO.generateHistoryInsuranceFeeRemainderPay(resultList.get(i));
			}
		} catch(Exception e) {
			throw e;
		}
	}
	
	//用于手动生成保险费余额变动表
	@Transactional
	public void generateHistoryInsuranceFeeRemainderFinancial(Context context) throws Exception {
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("reportDate",context.contextMap.get("reportDate"));
		param.put("recpCode",context.contextMap.get("recpCode"));
		param.put("s_employeeId",context.contextMap.get("s_employeeId"));
		this.insuranceFeeRemainderReportDAO.cancelHistoryInsuranceFeeRemainderFinancial(param);//手动生成历史错误的余额变动表,首先把已经生成的状态更新为-1
		
		List<Map<String,Object>> resultList=this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPay(param);
		
		Map<String,Object> insertMap=new HashMap<String,Object>();
		
		for(int i=0;resultList!=null&&i<resultList.size();i++) {
			
			param.put("RECP_ID",resultList.get(i).get("RECP_ID"));
			
			insertMap.clear();//初始化清除待插入的数据
			
			insertMap=this.insuranceFeeRemainderReportDAO.getOtherInformation(param);
			
			//设定设备总价款
			insertMap.put("LEASE_TOPRIC",resultList.get(i).get("LEASE_TOPRIC"));
			//租赁期数
			insertMap.put("LEASE_PERIOD",resultList.get(i).get("LEASE_PERIOD"));
			//当前期数
			insertMap.put("PERIOD_NUM",resultList.get(i).get("PERIOD_NUM"));
			//支付表状态 0正常,1正常结清,3是提前结清
			insertMap.put("RECP_STATUS",resultList.get(i).get("RECP_STATUS"));
			//保费
			insertMap.put("INSURE",resultList.get(i).get("INSURE"));
			//支付日期
			insertMap.put("PAY_DATE",resultList.get(i).get("PAY_DATE"));
			
			//如果是第一期的数据则不需要查询历史数据进行计算,因为报表显示栏位有期初余额,期末余额
			if("1".equals(String.valueOf(resultList.get(i).get("PERIOD_NUM")))) {
				//期初余额
				insertMap.put("BEGIN_MONEY",0);
				//本期新增,第一期的话,本期新增等于保费总额
				insertMap.put("INCREASE_MONEY",resultList.get(i).get("INSURE"));
				//本期减少
				insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
				//期末余额 等于期初余额+本期新增-本期减少  因为第一期期初余额=0 本期新增=保费总额 所以下面直接用保险总额-本期减少的
				insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
				
				//插入数据
				this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
			} else {
				//如果不是第一期的数据需要查询历史数据进行计算
				param.put("NUM",resultList.get(i).get("NUM"));
				List<Map<String,Object>> oneContractInsuranceFeeRemainderList=this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPayByRecpId(param);
				
				double monthInsuranceTotal=0;
				if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))||"1".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {//正常的与正常结清的
					//按照会记部的逻辑组装报表的最终数据
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					
					//期初余额
					insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
					//本期新增
					insertMap.put("INCREASE_MONEY",0);
					
					if("0".equals(String.valueOf(resultList.get(i).get("RECP_STATUS")))) {
						//本期减少
						insertMap.put("REDUCE_MONEY",resultList.get(i).get("MONTH_INSURE"));
						//期末余额
						insertMap.put("END_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
					} else {
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString())+Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal-Double.valueOf(resultList.get(i).get("MONTH_INSURE").toString()));
						//期末余额
						insertMap.put("END_MONEY",0);
					}
					//插入数据
					this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
				} else {//提前结清的
					//按照会记部的逻辑组装报表的最终数据
					
					String lastPayDate="";
					double reduceMoney=0;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//提前结清的逻辑和非提前结清的逻辑不同,所以有以下逻辑分支
						if("Y".equals(oneContractInsuranceFeeRemainderList.get(j).get("FLAG"))) {
							break;
						} else {//组装已付的保险费
							monthInsuranceTotal=monthInsuranceTotal+Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
						}
					}
					
					boolean flag=true;
					for(int j=0;oneContractInsuranceFeeRemainderList!=null&&j<oneContractInsuranceFeeRemainderList.size();j++) {
						//组装最后一期的本期减少,需要把提前结清开始后所有期数的保险费加起来
						if(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("DAY").toString())<=0) {//用于判断提前结清 最后一期应该在报表生成的数据
							if(flag) {//第一个-数才是最后支付日期,flag用于判断这个
								lastPayDate=oneContractInsuranceFeeRemainderList.get(j).get("PAY_DATE").toString();
								flag=false;
							}
							reduceMoney=(Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("LEASE_PERIOD").toString())
										-Integer.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("PERIOD_NUM").toString())+1)
										*Double.valueOf(oneContractInsuranceFeeRemainderList.get(j).get("MONTH_INSURE").toString());
							break;
						}
					}
					
					if(lastPayDate.equals(resultList.get(i).get("PAY_DATE_1").toString())) {//如果报表日期不等于提前结清最后一期应该该显示的数据的支付日,则不插入生成报表
						//期初余额
						insertMap.put("BEGIN_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//本期新增
						insertMap.put("INCREASE_MONEY",0);
						//本期减少
						insertMap.put("REDUCE_MONEY",Double.valueOf(resultList.get(i).get("INSURE").toString())-monthInsuranceTotal);
						//期末余额
						insertMap.put("END_MONEY",0);
						//插入数据
						this.insuranceFeeRemainderReportDAO.generateInsuranceFeeRemainderReport(insertMap);
					}
				}
			}
		}
	}
	
	public List<String> getDateList() throws Exception {
		return this.insuranceFeeRemainderReportDAO.getDateList();
	}
	
	public List<Map<String,Object>> getCurrentInsuranceFeeRemainderPayByRecpCode(Context context) throws Exception {
		return this.insuranceFeeRemainderReportDAO.getCurrentInsuranceFeeRemainderPayByRecpCode(context.contextMap);
	}
}
