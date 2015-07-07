package com.brick.report.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.batchjob.to.AvgPayMoneyBatchJobTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class ShowReportService extends BaseService {
	
	public Map<String, Object> getTargetLimitResult(Context context) throws Exception{
		return getTargetLimitResult(context, 0);
	}
	
	public Map<String, Object> getTargetLimitResult(Context context, int searchYear) throws Exception{
		Map<String, Object> outputMap = new HashMap<String, Object>();
		// 当天完成钱数
		double sumLeaseToday = 0;
		// 剩余月
		String loseMonth;
		String date = DateUtil.dateToString(new Date(), "yyyy-MM-dd");
		int year = Integer.parseInt(date.split("-")[0]);
		int month = Integer.parseInt(date.split("-")[1]);
		int day = Integer.parseInt(date.split("-")[2]);
		int dayAllByMonth = getDayByMonthAndYear(year, month);
		double i = dayAllByMonth - day;
		double j = dayAllByMonth;
		double d = i / j;
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String loseDay = nf.format(d);
		loseMonth = (12 - month) + "" + loseDay.substring(1, loseDay.length());
		outputMap.put("loseMonth", searchYear == Integer.parseInt(DateUtil.dateToString(new Date(), "yyyy")) || searchYear == 0 ? loseMonth : 0);
		List<Map<String, Object>> resultList = null;
		AvgPayMoneyBatchJobTo avgMoneyTo=null;
		
		AvgPayMoneyBatchJobTo avgMoneyToCurrentMonth=null;
		String searchDate = null;
		try {
			sumLeaseToday = Double.valueOf(DataAccessor.query(
					"show.selectTargetLimitToday", context.contextMap,
					DataAccessor.RS_TYPE.OBJECT).toString());
			Map<String, Object> param = new HashMap<String, Object>();
			
			if (searchYear == Integer.parseInt(DateUtil.dateToString(new Date(), "yyyy")) || searchYear == 0) {
				searchDate = DateUtil.dateToString(new Date(), "yyyy-MM-dd");
			} else {
				searchDate = searchYear + "-12-31";
			}
			param.put("searchDate", searchDate);
			resultList = (List<Map<String, Object>>) DataAccessor.query("show.getGoalMoneyByMonth", param, DataAccessor.RS_TYPE.LIST);
			
			//获得委贷的总金额与件数 BY YEAR
			LoanTo loanToYear=(LoanTo)DataAccessor.query("loan.getAvgLoanMoney",null,DataAccessor.RS_TYPE.OBJECT);
			if(loanToYear==null) {
				loanToYear=new LoanTo();
			}
			//获得当前月委贷的总金额与件数
			Map<String,String> paramLoan=new HashMap<String,String>();
			paramLoan.put("BY_MONTH","Y");
			LoanTo loanToMonth=(LoanTo)DataAccessor.query("loan.getAvgLoanMoney",paramLoan,DataAccessor.RS_TYPE.OBJECT);
			if(loanToMonth==null) {
				loanToMonth=new LoanTo();
			}
			
			//加入委贷金额 add by ShenQi
			//取当前年的委贷金额Group By月份
     		List<LoanTo> loanList=(List<LoanTo>)DataAccessor.query("loan.getLoanInfoGroupByMonth",param,DataAccessor.RS_TYPE.LIST);
			
			//获得当前月的平均拨款
			context.contextMap.put("DATE",searchDate);
			context.contextMap.put("loanMoney",loanToMonth.getPayMoney()==null?0:loanToMonth.getPayMoney());
			context.contextMap.put("loanCount",loanToMonth.getCount());
			avgMoneyToCurrentMonth=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyOfCurrentMonthTotal",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
			
			if(avgMoneyToCurrentMonth==null) {
				avgMoneyToCurrentMonth=new AvgPayMoneyBatchJobTo();
			}
			
			//加入新栏位,平均拨款金额
			param.put("payMoney",avgMoneyToCurrentMonth.getPayMoney());
			param.put("payCount",avgMoneyToCurrentMonth.getPayCount());
			param.put("searchDate", searchDate);
			param.put("loanMoney",loanToYear.getPayMoney()==null?0:loanToYear.getPayMoney());
			param.put("loanCount",loanToYear.getCount());
			avgMoneyTo=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyGroupByMonth",param,DataAccessor.RS_TYPE.OBJECT);
			
			if(avgMoneyTo==null) {
				avgMoneyTo=new AvgPayMoneyBatchJobTo();
			}
			
			Calendar cal=Calendar.getInstance();
			if(!searchDate.split("-")[0].equals(DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd").split("-")[0])) {
				
			} else {
				//加入当前月的平均单价
				if(avgMoneyToCurrentMonth.getPayMonth()==1) {
					avgMoneyTo.setAvgMoney_1(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==2) {
					avgMoneyTo.setAvgMoney_2(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==3) {
					avgMoneyTo.setAvgMoney_3(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==4) {
					avgMoneyTo.setAvgMoney_4(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==5) {
					avgMoneyTo.setAvgMoney_5(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==6) {
					avgMoneyTo.setAvgMoney_6(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==7) {
					avgMoneyTo.setAvgMoney_7(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==8) {
					avgMoneyTo.setAvgMoney_8(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==9) {
					avgMoneyTo.setAvgMoney_9(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==10) {
					avgMoneyTo.setAvgMoney_10(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==11) {
					avgMoneyTo.setAvgMoney_11(avgMoneyToCurrentMonth.getAvgMoney());
				} else if(avgMoneyToCurrentMonth.getPayMonth()==12) {
					avgMoneyTo.setAvgMoney_12(avgMoneyToCurrentMonth.getAvgMoney());
				}
			}
			for(int ii=0;ii<resultList.size();ii++) {
				for(int jj=0;jj<loanList.size();jj++) {
					//通过月份累加在一起
					if((Integer)resultList.get(ii).get("MONTH")==loanList.get(jj).getMonth()) {
						double money=0;
						if((java.math.BigDecimal)resultList.get(ii).get("PAY_MONEY")==null) {
							money=loanList.get(jj).getPayMoney().doubleValue();
						} else {
							money=((java.math.BigDecimal)resultList.get(ii).get("PAY_MONEY")).doubleValue()+loanList.get(jj).getPayMoney().doubleValue();
						}
						resultList.get(ii).put("PAY_MONEY",money);
						if(resultList.get(ii).get("GOAL_MONEY")==null) {

						} else {
							resultList.get(ii).put("PERCENT_MONEY",money/
									((java.math.BigDecimal)resultList.get(ii).get("GOAL_MONEY")).doubleValue()*100);
						}
						break;
					}
				}
				
				if((Integer)resultList.get(ii).get("MONTH")==1) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_1());
				} else if((Integer)resultList.get(ii).get("MONTH")==2) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_2());
				} else if((Integer)resultList.get(ii).get("MONTH")==3) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_3());
				} else if((Integer)resultList.get(ii).get("MONTH")==4) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_4());
				} else if((Integer)resultList.get(ii).get("MONTH")==5) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_5());
				} else if((Integer)resultList.get(ii).get("MONTH")==6) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_6());
				} else if((Integer)resultList.get(ii).get("MONTH")==7) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_7());
				} else if((Integer)resultList.get(ii).get("MONTH")==8) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_8());
				} else if((Integer)resultList.get(ii).get("MONTH")==9) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_9());
				} else if((Integer)resultList.get(ii).get("MONTH")==10) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_10());
				} else if((Integer)resultList.get(ii).get("MONTH")==11) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_11());
				} else if((Integer)resultList.get(ii).get("MONTH")==12) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_12());
				}
			}
			//计算合计平均TR
			Double tempForAvgTrTotal = 0D;
			Double tempPayTotal = 0D;
			for (Map<String, Object> m : resultList) {
				tempPayTotal += Double.valueOf(m.get("PAY_MONEY").toString());
				tempForAvgTrTotal += (Double.valueOf(m.get("PAY_MONEY").toString()) * Double.valueOf(m.get("AVGTR").toString()));
			}
			Double avgTrTotal = tempForAvgTrTotal / tempPayTotal;
			outputMap.put("avgTrTotal", avgTrTotal);
			
			//加入当日拨款金额
			/*List<LoanTo> loanTodayList=(List<LoanTo>)DataAccessor.query("loan.getLoanInfoByToday",null,DataAccessor.RS_TYPE.LIST);
			if(loanTodayList.size()!=0) {
				sumLeaseToday=loanTodayList.get(0).getPayMoney().doubleValue()+sumLeaseToday;
			}*/
		
		} catch (Exception e) {
			System.out.println(111);
			throw e;
		}
		
		if(avgMoneyToCurrentMonth.getPayMonth()==1) {
			outputMap.put("avgMoneyTotal", avgMoneyToCurrentMonth.getAvgMoney());
		} else {
			outputMap.put("avgMoneyTotal", avgMoneyTo.getAvgMoney());//加入总平均
		}
		outputMap.put("sumLeaseToday", sumLeaseToday);
		outputMap.put("resultList", resultList);
		return outputMap;
	}
	
	public Map<String, Object> getTargetLimitResultEqu(Context context,int searchYear) throws Exception {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		// 当天完成钱数
		double sumLeaseToday=0;
		// 剩余月
		String loseMonth;
		String date=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
		int year=Integer.parseInt(date.split("-")[0]);
		int month=Integer.parseInt(date.split("-")[1]);
		int day=Integer.parseInt(date.split("-")[2]);
		int dayAllByMonth=getDayByMonthAndYear(year,month);
		double i=dayAllByMonth-day;
		double j=dayAllByMonth;
		double d=i/j;
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String loseDay=nf.format(d);
		loseMonth=(12-month)+""+loseDay.substring(1,loseDay.length());
		outputMap.put("loseMonth",searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0?loseMonth:0);
		List<Map<String, Object>> resultList=null;
		AvgPayMoneyBatchJobTo avgMoneyTo=null;
		
		AvgPayMoneyBatchJobTo avgMoneyToCurrentMonth=null;
		String searchDate=null;
		try{
			sumLeaseToday=Double.valueOf(DataAccessor.query("show.selectTargetLimitTodayEqu",context.contextMap,DataAccessor.RS_TYPE.OBJECT).toString());
			Map<String, Object> param=new HashMap<String,Object>();

			if (searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0) {
				searchDate=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
			} else {
				searchDate=searchYear+"-12-31";
			}
			param.put("searchDate",searchDate);
			param.put("cmpyName","设备");
			resultList=(List<Map<String,Object>>) DataAccessor.query("show.getGoalMoneyByMonthEqu",param,DataAccessor.RS_TYPE.LIST);

			//获得当前月的平均拨款
			context.contextMap.put("DATE",searchDate);
			avgMoneyToCurrentMonth=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyOfCurrentMonthTotalEqu",context.contextMap,DataAccessor.RS_TYPE.OBJECT);

			if(avgMoneyToCurrentMonth==null) {
				avgMoneyToCurrentMonth=new AvgPayMoneyBatchJobTo();
			}

			//加入新栏位,平均拨款金额
			param.put("payMoney",avgMoneyToCurrentMonth.getPayMoney());
			param.put("payCount",avgMoneyToCurrentMonth.getPayCount());
			param.put("searchDate",searchDate);
			avgMoneyTo=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyGroupByMonthEqu",param,DataAccessor.RS_TYPE.OBJECT);

			if(avgMoneyTo==null) {
				avgMoneyTo=new AvgPayMoneyBatchJobTo();
			}

			//加入当前月的平均单价
			if(avgMoneyToCurrentMonth.getPayMonth()==1) {
				avgMoneyTo.setAvgMoney_1(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==2) {
				avgMoneyTo.setAvgMoney_2(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==3) {
				avgMoneyTo.setAvgMoney_3(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==4) {
				avgMoneyTo.setAvgMoney_4(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==5) {
				avgMoneyTo.setAvgMoney_5(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==6) {
				avgMoneyTo.setAvgMoney_6(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==7) {
				avgMoneyTo.setAvgMoney_7(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==8) {
				avgMoneyTo.setAvgMoney_8(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==9) {
				avgMoneyTo.setAvgMoney_9(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==10) {
				avgMoneyTo.setAvgMoney_10(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==11) {
				avgMoneyTo.setAvgMoney_11(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==12) {
				avgMoneyTo.setAvgMoney_12(avgMoneyToCurrentMonth.getAvgMoney());
			}

			for(int ii=0;ii<resultList.size();ii++) {
				if((Integer)resultList.get(ii).get("MONTH")==1) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_1());
				} else if((Integer)resultList.get(ii).get("MONTH")==2) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_2());
				} else if((Integer)resultList.get(ii).get("MONTH")==3) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_3());
				} else if((Integer)resultList.get(ii).get("MONTH")==4) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_4());
				} else if((Integer)resultList.get(ii).get("MONTH")==5) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_5());
				} else if((Integer)resultList.get(ii).get("MONTH")==6) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_6());
				} else if((Integer)resultList.get(ii).get("MONTH")==7) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_7());
				} else if((Integer)resultList.get(ii).get("MONTH")==8) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_8());
				} else if((Integer)resultList.get(ii).get("MONTH")==9) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_9());
				} else if((Integer)resultList.get(ii).get("MONTH")==10) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_10());
				} else if((Integer)resultList.get(ii).get("MONTH")==11) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_11());
				} else if((Integer)resultList.get(ii).get("MONTH")==12) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_12());
				}
			}
			//计算合计平均TR
			Double tempForAvgTrTotal=0D;
			Double tempPayTotal=0D;
			for (Map<String,Object> m:resultList) {
				tempPayTotal+=((BigDecimal)m.get("PAY_MONEY")).doubleValue();
				tempForAvgTrTotal+=(((BigDecimal)m.get("PAY_MONEY")).doubleValue()*(Double)m.get("AVGTR"));
			}
			Double avgTrTotal=tempForAvgTrTotal / tempPayTotal;
			outputMap.put("avgTrTotal", avgTrTotal);

		}catch(Exception e) {
			throw e;
		}
		if(avgMoneyToCurrentMonth.getPayMonth()==1) {
			outputMap.put("avgMoneyTotal",avgMoneyToCurrentMonth.getAvgMoney());
		} else {
			outputMap.put("avgMoneyTotal",avgMoneyTo.getAvgMoney());//加入总平均
		}
		outputMap.put("sumLeaseToday",sumLeaseToday);
		outputMap.put("resultList",resultList);
		return outputMap;
	}
	
	public Map<String, Object> getTargetLimitResultMotor(Context context,int searchYear) throws Exception {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		// 当天完成钱数
		double sumLeaseToday=0;
		// 剩余月
		String loseMonth;
		String date=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
		int year=Integer.parseInt(date.split("-")[0]);
		int month=Integer.parseInt(date.split("-")[1]);
		int day=Integer.parseInt(date.split("-")[2]);
		int dayAllByMonth=getDayByMonthAndYear(year,month);
		double i=dayAllByMonth-day;
		double j=dayAllByMonth;
		double d=i/j;
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String loseDay=nf.format(d);
		loseMonth=(12-month)+""+loseDay.substring(1,loseDay.length());
		outputMap.put("loseMonth",searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0?loseMonth:0);
		List<Map<String, Object>> resultList=null;
		AvgPayMoneyBatchJobTo avgMoneyTo=null;
		List<Map<String,Object>> periodList=null;
		
		AvgPayMoneyBatchJobTo avgMoneyToCurrentMonth=null;
		String searchDate=null;
		try{
			sumLeaseToday=Double.valueOf(DataAccessor.query("show.selectTargetLimitTodayMotor",context.contextMap,DataAccessor.RS_TYPE.OBJECT).toString());
			Map<String, Object> param=new HashMap<String,Object>();

			if (searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0) {
				searchDate=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
			} else {
				searchDate=searchYear+"-12-31";
			}
			param.put("searchDate",searchDate);
			param.put("cmpyName","商用车");
			resultList=(List<Map<String,Object>>) DataAccessor.query("show.getGoalMoneyByMonthMotor",param,DataAccessor.RS_TYPE.LIST);

			//获得当前月的平均拨款
			context.contextMap.put("DATE",searchDate);
			avgMoneyToCurrentMonth=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyOfCurrentMonthTotalMotor",context.contextMap,DataAccessor.RS_TYPE.OBJECT);

			//获得平均租期
			periodList=(List<Map<String,Object>>)DataAccessor.query("show.getAvgPeriod",param,DataAccessor.RS_TYPE.LIST);
			
			if(periodList==null) {
				periodList=new ArrayList<Map<String,Object>>();
			}
			
			if(periodList.size()==12) {
				
			} else {
				Map<String,Object> avgMap=new HashMap<String,Object>();
				avgMap.put("AVG_PERIOD",0);
				avgMap.put("TOTAL_PERIOD",0);
				avgMap.put("TOTAL_COUNT",0);
				int loop=periodList.size();
				for(int l=0;l<(12-loop);l++) {
					periodList.add(avgMap);
				}
			}
			if(avgMoneyToCurrentMonth==null) {
				avgMoneyToCurrentMonth=new AvgPayMoneyBatchJobTo();
			}

			//加入新栏位,平均拨款金额
			param.put("payMoney",avgMoneyToCurrentMonth.getPayMoney());
			param.put("payCount",avgMoneyToCurrentMonth.getPayCount());
			param.put("searchDate",searchDate);
			avgMoneyTo=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyGroupByMonthMotor",param,DataAccessor.RS_TYPE.OBJECT);

			if(avgMoneyTo==null) {
				avgMoneyTo=new AvgPayMoneyBatchJobTo();
			}

			//加入当前月的平均单价
			if(avgMoneyToCurrentMonth.getPayMonth()==1) {
				avgMoneyTo.setAvgMoney_1(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==2) {
				avgMoneyTo.setAvgMoney_2(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==3) {
				avgMoneyTo.setAvgMoney_3(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==4) {
				avgMoneyTo.setAvgMoney_4(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==5) {
				avgMoneyTo.setAvgMoney_5(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==6) {
				avgMoneyTo.setAvgMoney_6(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==7) {
				avgMoneyTo.setAvgMoney_7(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==8) {
				avgMoneyTo.setAvgMoney_8(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==9) {
				avgMoneyTo.setAvgMoney_9(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==10) {
				avgMoneyTo.setAvgMoney_10(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==11) {
				avgMoneyTo.setAvgMoney_11(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==12) {
				avgMoneyTo.setAvgMoney_12(avgMoneyToCurrentMonth.getAvgMoney());
			}

			for(int ii=0;ii<resultList.size();ii++) {
				resultList.get(ii).put("AVG_PERIOD",periodList.get(ii).get("AVG_PERIOD"));
				resultList.get(ii).put("TOTAL_PERIOD",periodList.get(ii).get("TOTAL_PERIOD"));
				resultList.get(ii).put("TOTAL_COUNT",periodList.get(ii).get("TOTAL_COUNT"));
				if((Integer)resultList.get(ii).get("MONTH")==1) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_1());
				} else if((Integer)resultList.get(ii).get("MONTH")==2) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_2());
				} else if((Integer)resultList.get(ii).get("MONTH")==3) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_3());
				} else if((Integer)resultList.get(ii).get("MONTH")==4) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_4());
				} else if((Integer)resultList.get(ii).get("MONTH")==5) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_5());
				} else if((Integer)resultList.get(ii).get("MONTH")==6) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_6());
				} else if((Integer)resultList.get(ii).get("MONTH")==7) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_7());
				} else if((Integer)resultList.get(ii).get("MONTH")==8) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_8());
				} else if((Integer)resultList.get(ii).get("MONTH")==9) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_9());
				} else if((Integer)resultList.get(ii).get("MONTH")==10) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_10());
				} else if((Integer)resultList.get(ii).get("MONTH")==11) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_11());
				} else if((Integer)resultList.get(ii).get("MONTH")==12) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_12());
				}
			}
			//计算合计平均TR
			Double tempForAvgTrTotal=0D;
			Double tempPayTotal=0D;
			for (Map<String,Object> m:resultList) {
				tempPayTotal+=((BigDecimal)m.get("PAY_MONEY")).doubleValue();
				tempForAvgTrTotal+=(((BigDecimal)m.get("PAY_MONEY")).doubleValue()*(Double)m.get("AVGTR"));
			}
			Double avgTrTotal=tempForAvgTrTotal / tempPayTotal;
			outputMap.put("avgTrTotal", avgTrTotal);

		}catch(Exception e) {
			throw e;
		}
		if(avgMoneyToCurrentMonth.getPayMonth()==1) {
			outputMap.put("avgMoneyTotal",avgMoneyToCurrentMonth.getAvgMoney());
		} else {
			outputMap.put("avgMoneyTotal",avgMoneyTo.getAvgMoney());//加入总平均
		}
		outputMap.put("sumLeaseToday",sumLeaseToday);
		outputMap.put("resultList",resultList);
		return outputMap;
	}
	
	public Map<String, Object> getTargetLimitResultCar(Context context,int searchYear) throws Exception {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		// 当天完成钱数
		double sumLeaseToday=0;
		// 剩余月
		String loseMonth;
		String date=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
		int year=Integer.parseInt(date.split("-")[0]);
		int month=Integer.parseInt(date.split("-")[1]);
		int day=Integer.parseInt(date.split("-")[2]);
		int dayAllByMonth=getDayByMonthAndYear(year,month);
		double i=dayAllByMonth-day;
		double j=dayAllByMonth;
		double d=i/j;
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String loseDay=nf.format(d);
		loseMonth=(12-month)+""+loseDay.substring(1,loseDay.length());
		outputMap.put("loseMonth",searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0?loseMonth:0);
		List<Map<String, Object>> resultList=null;
		AvgPayMoneyBatchJobTo avgMoneyTo=null;
		
		AvgPayMoneyBatchJobTo avgMoneyToCurrentMonth=null;
		String searchDate=null;
		try{
			sumLeaseToday=Double.valueOf(DataAccessor.query("show.selectTargetLimitTodayCar",context.contextMap,DataAccessor.RS_TYPE.OBJECT).toString());
			Map<String, Object> param=new HashMap<String,Object>();

			if (searchYear==Integer.parseInt(DateUtil.dateToString(new Date(),"yyyy"))||searchYear==0) {
				searchDate=DateUtil.dateToString(new Date(),"yyyy-MM-dd");
			} else {
				searchDate=searchYear+"-12-31";
			}
			param.put("searchDate",searchDate);
			param.put("cmpyName","乘用车");
			resultList=(List<Map<String,Object>>) DataAccessor.query("show.getGoalMoneyByMonthCar",param,DataAccessor.RS_TYPE.LIST);

			List<LoanTo> loanList=(List<LoanTo>)DataAccessor.query("loan.getLoanInfoGroupByMonth",param,DataAccessor.RS_TYPE.LIST);
			
			//获得当前月的平均拨款
			context.contextMap.put("DATE",searchDate);
			avgMoneyToCurrentMonth=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyOfCurrentMonthTotalCar",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
																							 
			if(avgMoneyToCurrentMonth==null) {
				avgMoneyToCurrentMonth=new AvgPayMoneyBatchJobTo();
			}

			//加入新栏位,平均拨款金额
			param.put("payMoney",avgMoneyToCurrentMonth.getPayMoney());
			param.put("payCount",avgMoneyToCurrentMonth.getPayCount());
			param.put("searchDate",searchDate);
			avgMoneyTo=(AvgPayMoneyBatchJobTo)DataAccessor.query("businessReport.getAvgPayMoneyGroupByMonthCar",param,DataAccessor.RS_TYPE.OBJECT);

			if(avgMoneyTo==null) {
				avgMoneyTo=new AvgPayMoneyBatchJobTo();
			}

			//加入当前月的平均单价
			if(avgMoneyToCurrentMonth.getPayMonth()==1) {
				avgMoneyTo.setAvgMoney_1(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==2) {
				avgMoneyTo.setAvgMoney_2(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==3) {
				avgMoneyTo.setAvgMoney_3(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==4) {
				avgMoneyTo.setAvgMoney_4(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==5) {
				avgMoneyTo.setAvgMoney_5(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==6) {
				avgMoneyTo.setAvgMoney_6(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==7) {
				avgMoneyTo.setAvgMoney_7(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==8) {
				avgMoneyTo.setAvgMoney_8(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==9) {
				avgMoneyTo.setAvgMoney_9(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==10) {
				avgMoneyTo.setAvgMoney_10(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==11) {
				avgMoneyTo.setAvgMoney_11(avgMoneyToCurrentMonth.getAvgMoney());
			} else if(avgMoneyToCurrentMonth.getPayMonth()==12) {
				avgMoneyTo.setAvgMoney_12(avgMoneyToCurrentMonth.getAvgMoney());
			}

			for(int ii=0;ii<resultList.size();ii++) {
				for(int jj=0;jj<loanList.size();jj++) {
					//通过月份累加在一起
					if((Integer)resultList.get(ii).get("MONTH")==loanList.get(jj).getMonth()) {
						double money=0;
						if((java.math.BigDecimal)resultList.get(ii).get("PAY_MONEY")==null) {
							money=loanList.get(jj).getPayMoney().doubleValue();
						} else {
							money=((java.math.BigDecimal)resultList.get(ii).get("PAY_MONEY")).doubleValue()+loanList.get(jj).getPayMoney().doubleValue();
						}
						resultList.get(ii).put("PAY_MONEY",money);
						if(resultList.get(ii).get("GOAL_MONEY")==null) {

						} else {
							resultList.get(ii).put("PERCENT_MONEY",money/
									((java.math.BigDecimal)resultList.get(ii).get("GOAL_MONEY")).doubleValue()*100);
						}
						break;
					}
				}
				
				if((Integer)resultList.get(ii).get("MONTH")==1) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_1());
				} else if((Integer)resultList.get(ii).get("MONTH")==2) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_2());
				} else if((Integer)resultList.get(ii).get("MONTH")==3) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_3());
				} else if((Integer)resultList.get(ii).get("MONTH")==4) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_4());
				} else if((Integer)resultList.get(ii).get("MONTH")==5) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_5());
				} else if((Integer)resultList.get(ii).get("MONTH")==6) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_6());
				} else if((Integer)resultList.get(ii).get("MONTH")==7) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_7());
				} else if((Integer)resultList.get(ii).get("MONTH")==8) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_8());
				} else if((Integer)resultList.get(ii).get("MONTH")==9) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_9());
				} else if((Integer)resultList.get(ii).get("MONTH")==10) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_10());
				} else if((Integer)resultList.get(ii).get("MONTH")==11) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_11());
				} else if((Integer)resultList.get(ii).get("MONTH")==12) {
					resultList.get(ii).put("avgMoney",avgMoneyTo.getAvgMoney_12());
				}
			}

		}catch(Exception e) {
			throw e;
		}
		if(avgMoneyToCurrentMonth.getPayMonth()==1) {
			outputMap.put("avgMoneyTotal",avgMoneyToCurrentMonth.getAvgMoney());
		} else {
			outputMap.put("avgMoneyTotal",avgMoneyTo.getAvgMoney());//加入总平均
		}
		outputMap.put("sumLeaseToday",sumLeaseToday);
		outputMap.put("resultList",resultList);
		return outputMap;
	}
	/**
	 * 根据年月算出当前月的总天数
	 * 
	 * @param year
	 * @param month
	 * @return int
	 */
	public int getDayByMonthAndYear(int year, int month) {
		int day = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			day = 30;
			break;
		case 2:
			if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
				day = 29;
			} else {
				day = 28;
			}
			break;
		}
		return day;
	}

	public Double getTargetAmount(Integer year, Integer month, String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("year", year);
		paramMap.put("month", month);
		paramMap.put("productionType", productionType);
		return (Double) queryForObj("show.getTargetAmount", paramMap);
	}

	public Double getPayMoney(Date startDate, Date endDate, String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("productionType", productionType);
		return (Double) queryForObj("show.getPayMoney", paramMap);
	}

	public Double getAvgUnitPrice(Date startDate, Date endDate, String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("productionType", productionType);
		return (Double) queryForObj("show.getAvgUnitPrice", paramMap);
	}

	public Double getAvgTr(Date startDate, Date endDate, String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start_date", startDate);
		paramMap.put("end_date", endDate);
		paramMap.put("busi_type", productionType);
		paramMap.put("code_type", "1");
		return (Double) queryForObj("report.getAvgTrReportByYearAndMonth", paramMap);
	}
	
	public Double getAvgPeriod(Date startDate, Date endDate, String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("start_date", startDate);
		paramMap.put("end_date", endDate);
		paramMap.put("busi_type", productionType);
		paramMap.put("code_type", "1");
		return (Double) queryForObj("report.getAvgPeriodReportByYearAndMonth", paramMap);
	}
	
	public Double getLoanMoneyForCar(Date startDate, Date endDate, String productionType){
		if (StringUtils.isEmpty(productionType) || "3".equals(productionType)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			return (Double) queryForObj("loan.getLoanMoneyForCar", paramMap);
		} else {
			return 0D;
		}
	}
	
	public Object getSumLeaseToday(String productionType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productionType", productionType);
		return (Double) queryForObj("show.getSumLeaseToday", paramMap);
	}
	
	private List<Map<String,Object>> getDayRemain() {
		return (List<Map<String,Object>>) queryForList("show.getDayRemain");
	}
	
	public List<Map<String, Object>> getTargetLimitReport(String year, String productionType) throws Exception{
		ReportDateTo reportDate = null;
		Date startDate = null;
		Date endDate = null;
		Double targetAmount = null;
		Double payMoney = null;
		Double avgUnitPrice = null;
		Double avgTr = null;
		Double avgPeriod = null;
		Double finishPercent = null;
		Double loanMoney = null;
		Date dateDiffStart = null;
		Integer dateDiff = null;
		Map<String, Object> dataMap = null;
		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
		try {
			for (int i = 1; i <= 12; i++) {
				reportDate = ReportDateUtil.getDateByYearAndMonth(Integer.parseInt(year), i);
				dataMap = new HashMap<String, Object>();
				startDate = reportDate.getBeginTime();
				endDate = reportDate.getEndTime();
				targetAmount = this.getTargetAmount(Integer.parseInt(year), i, productionType);
				payMoney = this.getPayMoney(startDate, endDate, productionType);
				avgUnitPrice = this.getAvgUnitPrice(startDate, endDate, productionType);
				avgTr = this.getAvgTr(startDate, endDate, productionType);
				avgPeriod = this.getAvgPeriod(startDate, endDate, productionType);
				loanMoney = this.getLoanMoneyForCar(startDate, endDate, productionType);
				payMoney = payMoney == null ? 0 : payMoney;
				loanMoney = loanMoney == null ? 0 : loanMoney;
				payMoney = payMoney + loanMoney;
				finishPercent = targetAmount == null || targetAmount == 0 || payMoney == null ? 0 : (payMoney / targetAmount / 10);
				if (startDate.before(new Date())) {
					dateDiffStart = DateUtil.strToDay(DateUtil.getTomorrow());
				} else {
					dateDiffStart = startDate;
				}
				dateDiff = (int) ((endDate.getTime() - dateDiffStart.getTime()) / 1000 / 60 / 60 / 24 + 1);
				dataMap.put("targetAmount", targetAmount == null ? 0 : targetAmount);
				dataMap.put("payMoney", payMoney == null ? 0 : payMoney);
				dataMap.put("avgUnitPrice", avgUnitPrice == null ? 0 : avgUnitPrice);
				dataMap.put("avgTr", avgTr);
				dataMap.put("avgPeriod", avgPeriod);
				dataMap.put("finishPercent", finishPercent);
				dataMap.put("dayDiff", dateDiff);
				dataMap.put("title", i);
				dataList.add(dataMap);
			}
			reportDate = ReportDateUtil.getDateByYear(Integer.parseInt(year));
			dataMap = new HashMap<String, Object>();
			startDate = reportDate.getBeginTime();
			endDate = reportDate.getEndTime();
			targetAmount = this.getTargetAmount(Integer.parseInt(year), null, productionType);
			payMoney = this.getPayMoney(startDate, endDate, productionType);
			avgUnitPrice = this.getAvgUnitPrice(startDate, endDate, productionType);
			avgTr = this.getAvgTr(startDate, endDate, productionType);
			avgPeriod = this.getAvgPeriod(startDate, endDate, productionType);
			loanMoney = this.getLoanMoneyForCar(startDate, endDate, productionType);
			payMoney = payMoney == null ? 0 : payMoney;
			loanMoney = loanMoney == null ? 0 : loanMoney;
			payMoney = payMoney + loanMoney;
			finishPercent = targetAmount == null || payMoney == null ? 0 : (payMoney / targetAmount / 10);
			dateDiff = (int) ((endDate.getTime() - DateUtil.strToDay(DateUtil.getCurrentDate()).getTime()) / 1000 / 60 / 60 / 24);
			dataMap.put("targetAmount", targetAmount == null ? 0 : targetAmount);
			dataMap.put("payMoney", payMoney == null ? 0 : payMoney);
			dataMap.put("avgUnitPrice", avgUnitPrice == null ? 0 : avgUnitPrice);
			dataMap.put("avgTr", avgTr);
			dataMap.put("avgPeriod", avgPeriod);
			dataMap.put("finishPercent", finishPercent);
			dataMap.put("dayDiff", dateDiff);
			dataMap.put("title", "合计");
			dataList.add(dataMap);
			return dataList;
		} catch(Exception e){
			throw e;
		}
	}

}
