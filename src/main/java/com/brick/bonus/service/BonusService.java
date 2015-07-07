package com.brick.bonus.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.activityLog.to.LoanTo;
import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.poi.ExcelPOI;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 奖金管理
 * @author 范义龙
 * @version Created：2011-04-18 
 *
 */

public class BonusService extends BaseCommand {
	Log logger = LogFactory.getLog(BonusService.class) ;
	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		super.afterExecute(action, context);
	}
	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return super.preExecute(action, context);
	}
	
	/**查找所以奖金信息**/
	@SuppressWarnings("unchecked")
	public void findAllBonus(Context context) {
		List errList = context.errList;
		Map outputMap = context.contextMap;
		DataWrap dw = null;
		if (errList.isEmpty()){
			try {
				dw = (DataWrap) DataAccessor.query("bonusManage.queryAllBonus", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--奖金列表错误!请联系管理员") ;
			}			
		}
		if (errList.isEmpty()){
			outputMap.put("dw", dw);
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context, "/bonus/bonusList.jsp");
		} else {
			outputMap.put("errList",errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**添加一个奖金信息**/
	@SuppressWarnings("unchecked")
	public void createBonus(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("bonusManage.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--添加奖金错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=bonusManage.findAllBonus");
		}else{
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	/**根据ID查看一个奖金信息**/
	@SuppressWarnings("unchecked")
	public void getBonusById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map bonus = null;
		if (errList.isEmpty()) {
			try {
				bonus = (Map) DataAccessor.query("bonusManage.getBonusInfoById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--查看奖金信息错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()) {
			outputMap.put("bonus", bonus);
			Output.jsonOutput(outputMap, context);
		} 
	}

	/**修改一个奖金信息**/
	@SuppressWarnings("unchecked")
	public void updateBonus(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("bonusManage.update", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--修改奖金错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=bonusManage.findAllBonus");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**根据ID去删除一个奖金信息**/
	@SuppressWarnings("unchecked")
	public void deleteBonusById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("bonusManage.invalid", context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--删除奖金信息错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=bonusManage.findAllBonus");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**检查是否已经存在奖金名称**/
	@SuppressWarnings("unchecked")
	public void checkBonusName(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		int count = 0 ;
		if (errList.isEmpty()) {
			try {
				context.contextMap.put("name", context.request.getParameter("name"));
				count = (Integer)DataAccessor.query("bonusManage.checkBonusCount", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--查询奖金错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()){
			outputMap.put("count", count);
			Output.jsonOutput(outputMap, context);
		}
	}
	
	
	public void bonusAllExcel(Context context)
	{
		List errList = context.errList;
		Map outputMap = new HashMap();
		List bonus = null;
		if (errList.isEmpty()) {
			try {
				bonus = (List) DataAccessor.query("bonusManage.queryAllBonus", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//查询所有当月的合同业务员名称
				context.contextMap.put("content",(List)DataAccessor.query("bonusManage.selectAllContractUser", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger) ;
				errList.add("奖金管理--查看奖金信息错误!请联系管理员") ;
			} 
		}
		if (errList.isEmpty()) {
			BonusExcel bounsExc=new BonusExcel();
			bounsExc.bonusExcelJoin(bonus, context);
			
		} 
	}
	
	/**
	 * 2012/01/30 Yang Yun 新增奖金报表
	 */
	public void bonusShow(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> bonusList = new ArrayList<Map<String,Object>>();
		//取当前的 年月
		String today = DateUtil.dateToString(new Date());
		ReportDateTo monthReportDateTo = ReportDateUtil.getDateByDate(today);
		String currentYear = String.valueOf(monthReportDateTo.getYear());	//当前年份
		String currentMonth = String.valueOf(monthReportDateTo.getMonth());	//当前月份
		String year_month = context.contextMap.get("year_month") == null ? null : (String)context.contextMap.get("year_month");
		String[] year_month_array = year_month == null ? null : year_month.split("-");
		String month = year_month_array == null ? currentMonth : year_month_array[1];
		String year = year_month_array == null ? currentYear : year_month_array[0];
		paramMap.put("month", month);
		paramMap.put("year", year);
		List<LoanTo> loanList=null;
		List<LoanTo> loanCountList=null;
		try {
			bonusList = (List<Map<String, Object>>) DataAccessor.query("bonusManage.bonusShow", paramMap, DataAccessor.RS_TYPE.LIST);
			
			//加入委贷金额 add by ShenQi
			paramMap.put("YEAR",year);
			loanList=(List<LoanTo>)DataAccessor.query("loan.getLoanInfoGroupByUser",paramMap, DataAccessor.RS_TYPE.LIST);
			loanCountList=(List<LoanTo>)DataAccessor.query("loan.getLoanCountGroupByUser",paramMap, DataAccessor.RS_TYPE.LIST);
			String dept="";
			String Countdept="";
			for(int i=0;i<bonusList.size();i++) {
				//加入业绩
				for(int j=0;loanList!=null&&j<loanList.size();j++) {
					if(Integer.valueOf(month)==loanList.get(j).getMonth()
							&&String.valueOf(bonusList.get(i).get("EMP_ID")).equals(loanList.get(j).getUserId())) {
						bonusList.get(i).put("MONTH_PAY_MONEY",(new java.math.BigDecimal(bonusList.get(i).get("MONTH_PAY_MONEY").toString())).doubleValue()
								+loanList.get(j).getPayMoney().doubleValue()*1000);
						bonusList.get(i).put("YEAR_PAY_MONEY",(new java.math.BigDecimal(bonusList.get(i).get("YEAR_PAY_MONEY").toString())).doubleValue()
								+loanList.get(j).getPayMoney().doubleValue()*1000);	
						dept=String.valueOf(bonusList.get(i).get("DECP_ID"));
					}
					if("-1".equals(String.valueOf(bonusList.get(i).get("EMP_ID")))&&
							dept.equals(String.valueOf(bonusList.get(i).get("DECP_ID")))&&
							Integer.valueOf(month)==loanList.get(j).getMonth()) {
						bonusList.get(i).put("MONTH_PAY_MONEY",(new java.math.BigDecimal(bonusList.get(i).get("MONTH_PAY_MONEY").toString())).doubleValue()
								+loanList.get(j).getPayMoney().doubleValue()*1000);
						bonusList.get(i).put("YEAR_PAY_MONEY",(new java.math.BigDecimal(bonusList.get(i).get("YEAR_PAY_MONEY").toString())).doubleValue()
								+loanList.get(j).getPayMoney().doubleValue()*1000);
					} 
				}
				//加入案件数
				for(int j=0;loanCountList!=null&&j<loanCountList.size();j++) {
					if(Integer.valueOf(month)==loanCountList.get(j).getMonth()
							&&String.valueOf(bonusList.get(i).get("EMP_ID")).equals(loanCountList.get(j).getUserId())) {
						bonusList.get(i).put("MONTH_COUNT",(Integer)bonusList.get(i).get("MONTH_COUNT")
								+loanCountList.get(j).getCount());
						bonusList.get(i).put("YEAR_COUNT",(Integer)bonusList.get(i).get("YEAR_COUNT")
								+loanCountList.get(j).getCount());	
						Countdept=String.valueOf(bonusList.get(i).get("DECP_ID"));
					}
					if("-1".equals(String.valueOf(bonusList.get(i).get("EMP_ID")))&&
							Countdept.equals(String.valueOf(bonusList.get(i).get("DECP_ID")))&&
							Integer.valueOf(month)==loanCountList.get(j).getMonth()) {
						bonusList.get(i).put("MONTH_COUNT",(Integer)bonusList.get(i).get("MONTH_COUNT")
								+loanCountList.get(j).getCount());
						bonusList.get(i).put("YEAR_COUNT",(Integer)bonusList.get(i).get("YEAR_COUNT")
								+loanCountList.get(j).getCount());
					} 
				}
				
				// 加入业助和区域主管
				List<Map<String, Object>> assistantList = null;
				List<Map<String, Object>> managerList = null;
				Double payPercent = null;
				Double ratePercent = null;
				if ((Integer)bonusList.get(i).get("EMP_ID") == -1) {
					assistantList = (List<Map<String, Object>>) baseService.queryForList("bonusManage.getAssistantList", bonusList.get(i));
					managerList = (List<Map<String, Object>>) baseService.queryForList("bonusManage.getManagerList", bonusList.get(i));
					payPercent = ((BigDecimal)bonusList.get(i).get("MONTH_PAY_MONEY_PERCENT")).doubleValue();
					ratePercent = ((BigDecimal)bonusList.get(i).get("MONTH_RATE_DIFF_PERCENT")).doubleValue();
					assistantList = addAssistantMoney(assistantList, payPercent > ratePercent ? payPercent : ratePercent);
					managerList = addManagerMoney(managerList, payPercent > ratePercent ? payPercent : ratePercent);
					bonusList.get(i).put("assistantList", assistantList);
					bonusList.get(i).put("managerList", managerList);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> reportStartYearAndMonth = (Map<String, Object>) baseService.queryForObj("bonusManage.getReportStartYearAndMonth");
		outputMap.put("startYear", reportStartYearAndMonth.get("BONUS_YEAR"));
		outputMap.put("startMonth", reportStartYearAndMonth.get("BONUS_MONTH"));
		outputMap.put("currentYear", currentYear);
		outputMap.put("currentMonth", currentMonth);
		outputMap.put("year", year);
		outputMap.put("month", month);
		outputMap.put("resultList", bonusList);
		Output.jspOutput(outputMap, context, "/bonus/bonusShow.jsp");
	}
	
	private List<Map<String, Object>> addManagerMoney(List<Map<String, Object>> managerList, Double percent) {
		System.out.println(percent);
		Double money = 0D;
		if (percent >= 120) {
			money = 3000.00;
		} else if (percent >= 100) {
			money = 2500.00;
		} else if (percent >= 90) {
			money = 2000.00;
		}
		for (int i = 0; i < managerList.size(); i++) {
			managerList.get(i).put("money", money);
		}
		return managerList;
	}
	private List<Map<String, Object>> addAssistantMoney(List<Map<String, Object>> assistantList, Double percent) {
		System.out.println(percent);
		Double money = 0D;
		if (percent >= 100) {
			money = 600.00;
		} else if (percent >= 90) {
			money = 500.00;
		}
		for (int i = 0; i < assistantList.size(); i++) {
			assistantList.get(i).put("money", money);
		}
		return assistantList;
	}
	
	
	/**
	 * 2012/02/09
	 * 奖金报表 Daily Job
	 * @param context
	 * @throws Exception 
	 */
	public void bonusJob() throws Exception{
		System.out.println("==============================Start Bonus Job.==================================");
		List<Map<String, Object>> resultList = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String, Object>> bonusList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> subtotalList = new ArrayList<Map<String,Object>>();
		//String not_contain = "待补";
		String today = DateUtil.dateToString(new Date());
		ReportDateTo monthReportDateTo = ReportDateUtil.getDateByDate(today);
		paramMap.put("month", monthReportDateTo.getMonth());
		paramMap.put("year", monthReportDateTo.getYear());
		SqlMapClient sqlMapper = null;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			//1.删除当月奖金报表内容
			sqlMapper.delete("bonusManage.bonusReportDelete", paramMap);
			//2.查询基础数据（当月业绩目标、业绩、利差、案件数和全年的业绩目标、业绩、利差、案件数），然后插入奖金表中。
			
			ReportDateTo yearReportDateTo = ReportDateUtil.getDateByYear(monthReportDateTo.getYear());
			paramMap.put("month_start_date", monthReportDateTo.getBeginTime());
			paramMap.put("month_end_date", monthReportDateTo.getEndTime());
			paramMap.put("year_start_date", yearReportDateTo.getBeginTime());
			paramMap.put("year_end_date", yearReportDateTo.getEndTime());
			sqlMapper.insert("bonusManage.bonusReportCreate", paramMap);
			//3.增加“小计”
			paramMap.put("sum_dept", "*小计");
			sqlMapper.insert("bonusManage.addSubtotal", paramMap);
			//构建奖金报表
			//a.查询刚刚插入的基础数据
			resultList = sqlMapper.queryForList("bonusManage.getBasicBonus", paramMap);
			double project_bonus = 0D;	//案件奖金
			double rate_diff_bonus = 0D;//利差奖金
			double company_bonus = 0D;	//单位奖金
			double monthRateDiff = 0D;	//月利差
			double yearRateDiff = 0D;	//年利差
			int proCount = 0;	//月案件数
			double monthTarget = 0D;	//月业绩目标
			double yearTarget = 0D;		//年业绩目标
			double monthPayMoney = 0D;	//月实际业绩
			double yearPayMoney = 0D;	//年实际业绩
			double month_rate_target = 0D;	//月利差目标
			double yearRateDiffTarget = 0D;	//年利差目标
			double month_pay_money_percent = 0D;	//月业绩达成率
			double month_rate_diff_percent = 0D;	//月利差达成率
			double year_pay_money_percent = 0D;	//年业绩达成率
			double year_rate_diff_percent = 0D;	//年利差达成率
			//String is_finish = "N";
			//b.循环算出每个员工的月业绩、利差达成率和年业绩、利差达成率，和奖金（案件奖金和利差奖金）
			for (Map<String, Object> map : resultList) {
				//月------------------------------------------------------------------------
				//月利差
				if (map.get("MONTH_RATE_DIFF") != null) {	
					monthRateDiff = ((BigDecimal) map.get("MONTH_RATE_DIFF")).doubleValue();
				}
				//月案件数
				if (map.get("MONTH_COUNT") != null) {	
					proCount = (Integer) map.get("MONTH_COUNT");
				}
				//月业绩目标
				if (map.get("MONTH_TARGET") != null) {	
					monthTarget = ((BigDecimal) map.get("MONTH_TARGET")).doubleValue();
					//根据业绩目标计算利差目标
					month_rate_target = monthTarget / 500 * 47.64;	
				}
				//月实际业绩
				if (map.get("MONTH_PAY_MONEY") != null) {	
					monthPayMoney = ((BigDecimal) map.get("MONTH_PAY_MONEY")).doubleValue();
				}
				//月利差达成率
				if (monthRateDiff > 0 && month_rate_target > 0) {	
					month_rate_diff_percent = monthRateDiff / month_rate_target * 100;
				} else {
					month_rate_diff_percent = 0;
				}
				//月业绩达成率
				if (monthPayMoney > 0 && monthTarget > 0) {	
					month_pay_money_percent = monthPayMoney / monthTarget * 100;
				} else {
					month_pay_money_percent = 0;
				}
				//奖金计算(小计的奖金除外)
				if ((month_pay_money_percent >= 100 || month_rate_diff_percent >= 100) && (Integer)map.get("EMP_ID") != -1) {
					//is_finish = "Y";
					project_bonus = getProjectBonus(proCount);
					rate_diff_bonus = getRateDiffBonus(monthRateDiff);
				} else {
					project_bonus = 0;
					rate_diff_bonus = 0;
				}
				map.put("month_rate_target", month_rate_target);
				map.put("month_rate_diff_percent", month_rate_diff_percent);
				map.put("month_pay_money_percent", month_pay_money_percent);
				map.put("project_bonus", project_bonus);
				map.put("rate_diff_bonus", rate_diff_bonus);
				map.put("company_bonus", company_bonus);
				//map.put("is_finish", is_finish);
				//年-------------------------------------------------------------------------
				//年实际业绩
				if (map.get("YEAR_PAY_MONEY") != null) {	
					yearPayMoney = ((BigDecimal) map.get("YEAR_PAY_MONEY")).doubleValue();
				}
				//年实际利差
				if (map.get("YEAR_RATE_DIFF") != null) {	
					yearRateDiff = ((BigDecimal) map.get("YEAR_RATE_DIFF")).doubleValue();
				}
				//年业绩目标和利差目标
				if (map.get("YEAR_TARGET") != null) {	
					yearTarget = ((BigDecimal) map.get("YEAR_TARGET")).doubleValue();
					yearRateDiffTarget = ((BigDecimal) map.get("YEAR_TARGET")).doubleValue() / 500 * 47.64;
				}
				//年利差达成率
				if (yearRateDiff > 0 && yearRateDiffTarget > 0) {	
					year_rate_diff_percent = yearRateDiff / yearRateDiffTarget * 100;
				} else {
					year_rate_diff_percent = 0;
				}
				//年业绩达成率
				if (yearPayMoney > 0 && yearTarget > 0) {	
					year_pay_money_percent = yearPayMoney / yearTarget * 100;
				} else {
					year_pay_money_percent = 0;
				}
				map.put("year_rate_target", yearRateDiffTarget);
				map.put("year_rate_diff_percent", year_rate_diff_percent);
				map.put("year_pay_money_percent", year_pay_money_percent);
				if ((Integer)map.get("EMP_ID") == -1) {
					subtotalList.add(map);
				}
				bonusList.add(map);
			}
			//c.更新到数据库
			int result = 0;
			for (Map<String, Object> bonus : bonusList) {
				result = sqlMapper.update("bonusManage.updateBonus", bonus);
				if (result == 0) {
					throw new Exception(bonus.get("EMP_NAME") + "-----更新失败！");
				}
			}
			//d.更新小计的奖金
			for (Map<String, Object> subtotal : subtotalList) {
				result = sqlMapper.update("bonusManage.updateSubtotal", subtotal);
				if (result == 0) {
					throw new Exception(subtotal.get("DECP_NAME") + "“小计”-----更新失败！");
				}
			}
			sqlMapper.commitTransaction();
			System.out.println("==============================Bonus Job Success.==================================");
		} catch (Exception e) {
			System.out.println("==============================Bonus Job Failed.==================================");
			throw e;
		} finally {
			sqlMapper.endTransaction();
		}
		System.out.println("==============================End Bonus Job.==================================");
	}
	
	/**
	 * 2012/02/09 Yang Yun
	 * 计算案件奖金
	 * @param projectCount
	 * @return
	 */
	private static double getProjectBonus(int projectCount){
		return 200 * projectCount;
	}
	
	/**
	 * 2012/02/09 Yang Yun
	 * 计算利差奖金
	 * @param money
	 * @param projectCount
	 * @return
	 */
	private static double getRateDiffBonus(double money){
		double resultMoney = 0;
		//resultMoney = resultMoney + (200 * projectCount);
		if (money > 30000) {
			if (money <= 70000) {
				resultMoney = resultMoney + (money * 0.0105);
			} else {
				resultMoney = resultMoney + (70000 * 0.0105);
				if (money <= 120000) {
					resultMoney = resultMoney + ((money - 70000) * 0.01125);
				} else {
					resultMoney = resultMoney + ((120000 - 70000) * 0.01125);
					if (money <= 200000) {
						resultMoney = resultMoney + ((money - 120000) * 0.0125);
					} else {
						resultMoney = resultMoney + ((200000 - 120000) * 0.0125);
						resultMoney = resultMoney + ((money - 200000) * 0.015);
					}
				}
			}
		}
		//奖金上限 5000
		resultMoney = resultMoney > 5000 ? 5000 : resultMoney;
		return resultMoney;
	}
	
	//导出Excel报表 add by ShenQi
	public void bonusExportExcel(Context context) {
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		List<Map<String, Object>> bonusList=new ArrayList<Map<String,Object>>();
		
		//取当前的 年月
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
		Date now=new Date();
		String currentYear=sdf.format(now);	//当前年份
		sdf.applyPattern("MM");
		String currentMonth=sdf.format(now);	//当前月份
		
		String year_month=context.contextMap.get("year_month")==null?null:(String)context.contextMap.get("year_month");
		String[] year_month_array=year_month==null?null:year_month.split("-");
		String month=year_month_array== null?currentMonth:year_month_array[1];
		String year=year_month_array==null?currentYear:year_month_array[0];
		
		paramMap.put("month",month);
		paramMap.put("year",year);
		
		try {
			bonusList=(List<Map<String, Object>>) DataAccessor.query("bonusManage.bonusShow", paramMap, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<bonusList.size();i++) {
				// 加入业助和区域主管
				List<Map<String, Object>> assistantList = null;
				List<Map<String, Object>> managerList = null;
				Double payPercent = null;
				Double ratePercent = null;
				if ((Integer)bonusList.get(i).get("EMP_ID") == -1) {
					assistantList = (List<Map<String, Object>>) baseService.queryForList("bonusManage.getAssistantList", bonusList.get(i));
					managerList = (List<Map<String, Object>>) baseService.queryForList("bonusManage.getManagerList", bonusList.get(i));
					payPercent = ((BigDecimal)bonusList.get(i).get("MONTH_PAY_MONEY_PERCENT")).doubleValue();
					ratePercent = ((BigDecimal)bonusList.get(i).get("MONTH_RATE_DIFF_PERCENT")).doubleValue();
					assistantList = addAssistantMoney(assistantList, payPercent > ratePercent ? payPercent : ratePercent);
					managerList = addManagerMoney(managerList, payPercent > ratePercent ? payPercent : ratePercent);
					bonusList.get(i).put("assistantList", assistantList);
					bonusList.get(i).put("managerList", managerList);
				}
			}
			
			ExcelPOI excel=new ExcelPOI();
			
			context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
			context.response.setHeader("Content-Disposition","attachment;filename="+ new String(("奖金报表("+year+"-"+month+").xls").getBytes("GBK"),"ISO-8859-1"));
			ServletOutputStream out=context.response.getOutputStream();
			
			context.contextMap.put("sheetName","奖金报表");
			context.contextMap.put("date",year+"-"+month);
			excel.generateBonusReport(bonusList,context).write(out);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void doBonusReportByEmpJob() throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<Object, Object>> resultList = null;
		String year = DateUtil.getCurrentYear();
		paramMap.put("year", year);
		Map<Object, Object> resultmMap = new HashMap<Object, Object>();
		Map<Object, Object> detailMap = null;
		try {
			baseService.delete("bonusManage.deleteThisYearData", paramMap);
			resultList = (List<Map<Object, Object>>) baseService.queryForList("bonusManage.getReportByEmp", paramMap);
			for (Map<Object, Object> map : resultList) {
				if (resultmMap.get(map.get("EMP_NAME")) == null) {
					detailMap = new HashMap<Object, Object>();
				} else {
					detailMap = (Map<Object, Object>) resultmMap.get(map.get("EMP_NAME"));
				}
				detailMap.put("month_" + map.get("BONUS_MONTH"), map.get("BONUS"));
				resultmMap.put(map.get("EMP_NAME"), detailMap);
			}
			List<Map<String, Object>> empList = (List<Map<String, Object>>) baseService.queryForList("bonusManage.getAllEmp", paramMap);
			Map<String, Object> insertMap = null;
			for (Map<String, Object> emp : empList) {
				insertMap = (Map<String, Object>) resultmMap.get(emp.get("NAME"));
				if (insertMap == null) {
					continue;
				}
				insertMap.put("DECP_NAME_CN", emp.get("DECP_NAME_CN"));
				insertMap.put("EMP_NAME", emp.get("NAME"));
				insertMap.put("ORDER", emp.get("ORDER"));
				insertMap.put("year", year);
				baseService.insert("bonusManage.insertBonusReportByEmp", insertMap);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void getReportByEmp(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<Object, Object>> resultList = null;
		String year = (String) context.contextMap.get("year");
		if (StringUtils.isEmpty(year)) {
			year = DateUtil.dateToString(new Date(), "yyyy");
		}
		context.contextMap.put("year", year);
		Map<Object, Object> resultmMap = new HashMap<Object, Object>();
		Map<Object, Object> detailMap = null;
		try {
			resultList = (List<Map<Object, Object>>) baseService.queryForList("bonusManage.getEmpReportForShow", context.contextMap);
			outputMap.put("bonusList", resultList);
			outputMap.put("year", year);
			Output.jspOutput(outputMap, context, "/report/bonusEmployeeReport.jsp");
		} catch (ServiceException e) {
			logger.error(e);
			Output.errorPageOutput(e, context);
		}
	}
	
	public void changeDataForEmpReport(Context context){
		boolean flag = true;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String dataId = (String) context.contextMap.get("reportId");
			String dataName = (String) context.contextMap.get("inputName");
			String dataValue = (String) context.contextMap.get("dataValue");
			if (StringUtils.isEmpty(dataId) || StringUtils.isEmpty(dataName) || StringUtils.isEmpty(dataValue)) {
				throw new Exception();
			}
			paramMap.put(dataName, dataValue);
			paramMap.put("reportId", dataId);
			baseService.update("bonusManage.changeDataForEmpReport", paramMap);
		} catch (Exception e) {
			flag = false;
		}
		Output.jsonFlageOutput(flag, context);
	}
	
}
