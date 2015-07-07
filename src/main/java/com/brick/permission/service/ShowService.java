package com.brick.permission.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.brick.activityLog.to.LoanTo;
import com.brick.area.service.AreaService;
import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.util.LeaseUtil;
import com.brick.batchjob.to.CaseCompareDayTo;
import com.brick.bussinessReport.to.DunCaseDetailTO;
import com.brick.permission.util.CreateXmlUtil;
import com.brick.permission.util.ReadXML;
import com.brick.report.service.ShowReportService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * @author hefei
 * 
 */
public class ShowService extends BaseCommand {
	Log logg = LogFactory.getLog(ShowService.class);
	static Logger logger = Logger.getLogger(AreaService.class);

	private ShowReportService showReportService;
	
	public ShowReportService getShowReportService() {
		return showReportService;
	}

	public void setShowReportService(ShowReportService showReportService) {
		this.showReportService = showReportService;
	}

	/**
	 * 查询判断desk表
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkDesk(Context context) {
		Map outputMap = new HashMap();
		boolean flag = checkUserByDesk(context);
		try {
			if (!flag) {
				// 未设置则初始化数据
				List<Element> list = ReadXML.getConfigList();
				List lists = new ArrayList();

				for (Element el : list) {
					String divName = el.attributeValue("name");
					lists.add(divName);
				}
				// 添加至数据库
				addDeskByNames(lists, context);
				// outputMap.put("names", lists);
				// outputMap.put("head", 0);
				outputMap.put("head", 1);
				List deskList = (List) DataAccessor.query(
						"show.selectDeskByUser", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("deskList", deskList);
			} else {
				// 设置过则读取数据
				List deskList = (List) DataAccessor.query(
						"show.selectDeskByUser", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("deskList", deskList);
				outputMap.put("head", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 根据div名添加模块
	 * 
	 * @param where
	 */
	@SuppressWarnings("unchecked")
	public void addDeskByNames(List list, Context context) {
		int count = 1;
		for (int i = 0; i < list.size(); i++) {
			String divName = (String) list.get(i);
			Map where = new HashMap();
			where.put("s_employeeId", context.getRequest().getSession()
					.getAttribute("s_employeeId"));
			where.put("div_id", divName);
			where.put("desk_level", 1);
			if (count % 2 == 0) {
				where.put("desk_column", "column_1");
			} else {
				where.put("desk_column", "column_2");
			}
			try {
				DataAccessor.execute("show.create", where,
						DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logg);
			}
			count++;
		}
	}

	/**
	 * 查询客户汇总结果
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void cust_colletResult(Context context) {
		Map outputMap = new HashMap();
		List<Map<String, Object>> resultList = null;
		try {
			/* 2012/02/10 Yang Yun 修改客户汇总
			// 客户表
			int customerCount = ((Integer) DataAccessor.query(
					"show.selectCustomerCount", context.contextMap,
					DataAccessor.RS_TYPE.OBJECT)).intValue();
			outputMap.put("customerCount", customerCount);
			// 合同
			Map contractMap = (Map) DataAccessor.query(
					"show.selectContractCountAndSum", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("contractMap", contractMap);
			// 支付表
			Map collectionPlanMap = (Map) DataAccessor.query(
					"show.selectCollectionPlanCountAndSum", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("collectionPlanMap", collectionPlanMap);*/
			
			resultList = (List<Map<String, Object>>) DataAccessor.query("show.getCustInfo", new HashMap<String, Object>(), DataAccessor.RS_TYPE.LIST);
			
			/*//加入委贷金额 add by ShenQi
			Map<String, Object> result=null;
			LoanTo loanTo=(LoanTo)DataAccessor.query("loan.getLoanInfo",null,DataAccessor.RS_TYPE.OBJECT);
			int count=(Integer)DataAccessor.query("loan.getLoanCount",null,DataAccessor.RS_TYPE.OBJECT);
			if(resultList!=null&&resultList.size()>0) {
				result=resultList.get(0);
				result.put("LOSS_OWN_PRICE",(Double)result.get("LOSS_OWN_PRICE")+((BigDecimal)loanTo.getOriginalMoney()).doubleValue());
				result.put("LOSS_REN_PRICE",(Double)result.get("LOSS_REN_PRICE")+((BigDecimal)loanTo.getAccrual()).doubleValue());
				result.put("LOSS_PRICE",(Double)result.get("LOSS_OWN_PRICE")+(Double)result.get("LOSS_REN_PRICE"));
				result.put("CUST_COUNT",(Integer)result.get("CUST_COUNT")+count);
				result.put("PRJT_COUNT",(Integer)result.get("PRJT_COUNT")+count);
			}*/
			outputMap.put("descr","全部");
			outputMap.put("divName","cust_collet");
			outputMap.put("result",resultList.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/cust_collet.jsp");
	}

	public void cust_collet_equResult(Context context) {
		Map outputMap = new HashMap();
		List<Map<String, Object>> resultList = null;
		try {
			
			resultList = (List<Map<String, Object>>) DataAccessor.query("show.getCustInfoEqu", new HashMap<String, Object>(), DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("descr","设备");
			outputMap.put("divName","cust_collet_equ");
			outputMap.put("result",resultList.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/cust_collet.jsp");
	}
	
	public void cust_collet_motorResult(Context context) {
		Map outputMap = new HashMap();
		List<Map<String, Object>> resultList = null;
		try {
			
			resultList = (List<Map<String, Object>>) DataAccessor.query("show.getCustInfoMotor", new HashMap<String, Object>(), DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("descr","商用车");
			outputMap.put("divName","cust_collet_motor");
			outputMap.put("result",resultList.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/cust_collet.jsp");
	}
	
	//应收统计 乘用车
	public void cust_collet_carResult(Context context) {
		Map outputMap = new HashMap();
		List<Map<String, Object>> resultList = null;
		try {
			
			resultList = (List<Map<String, Object>>) DataAccessor.query("show.getCustInfoCar", new HashMap<String, Object>(), DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("descr","乘用车");
			outputMap.put("divName","cust_collet_car");
			outputMap.put("result",resultList.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/cust_collet.jsp");
	}
	
	/**
	 * 查询客户调查结果
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void cust_researchResult(Context context) {
		Map outputMap = new HashMap();
		try {
			// 客户调查
			List custResearchList = (List) DataAccessor.query(
					"show.selectPrjtCredit", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("custResearchList", custResearchList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/cust_research.jsp");
	}

	//委贷目标额度
	public void loan_target_limitResult(Context context) {
		
		Map<String,Object> outputMap = new HashMap<String,Object>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		List<LoanTo> result=null;
		
		paramMap.put("YEAR",DateUtil.getCurrentYear());
		try {
			result = (List<LoanTo>)baseService.queryForList("loan.getLoanTarget", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("divName","loan_target_limit");
		outputMap.put("result",result==null || result.size()==0 ? new LoanTo() : result.get(0));
		Output.jspOutput(outputMap,context,"/frame/desk/loan_target_limit.jsp");
	}
	/**
	 * 查询目标额度结果
	 * 
	 * @param context
	 * @throws Exception 
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void target_limitResult(Context context) {
		Map<String, Object> outputMap = null;
		try {
			outputMap = new HashMap<String, Object>();
			outputMap.put("dataList", showReportService.getTargetLimitReport(DateUtil.getCurrentYear(), null));
			outputMap.put("sumLeaseToday", showReportService.getSumLeaseToday(null));
			outputMap.put("descr","全部");
			outputMap.put("divName","target_limit");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/frame/desk/target_limit.jsp");
	}
	
	//目标额度  设备  add by ShenQi
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void target_limit_equResult(Context context) {
		Map<String, Object> outputMap = null;
		try {
			outputMap = new HashMap<String, Object>();
			outputMap.put("dataList", showReportService.getTargetLimitReport(DateUtil.getCurrentYear(), "1"));
			outputMap.put("sumLeaseToday", showReportService.getSumLeaseToday("1"));
			outputMap.put("descr","设备");
			outputMap.put("divName","target_limit_equ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/frame/desk/target_limit.jsp");
	}
	
	//目标额度  重车  add by ShenQi
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void target_limit_motorResult(Context context) {
		Map<String, Object> outputMap = null;
		try {
			outputMap = new HashMap<String, Object>();
			outputMap.put("dataList", showReportService.getTargetLimitReport(DateUtil.getCurrentYear(), "2"));
			outputMap.put("sumLeaseToday", showReportService.getSumLeaseToday("2"));
			outputMap.put("descr","商用车");
			outputMap.put("divName","target_limit_motor");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/frame/desk/target_limit.jsp");
	}
	
	//目标额度  小车  add by ShenQi
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void target_limit_carResult(Context context) {
		Map<String, Object> outputMap = null;
		try {
			outputMap = new HashMap<String, Object>();
			outputMap.put("dataList", showReportService.getTargetLimitReport(DateUtil.getCurrentYear(), "3"));
			outputMap.put("sumLeaseToday", showReportService.getSumLeaseToday("3"));
			outputMap.put("descr","乘用车");
			outputMap.put("divName","target_limit_car");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/frame/desk/target_limit.jsp");
	}
	
	/**
	 * 目标额度(页面)
	 * @param context
	 * @throws Exception 
	 */
	public void target_limitResultForReport(Context context) throws Exception {
		Map<String, Object> outputMap = null;
		String productionType = (String) context.contextMap.get("productionType");
		String year = (String) context.contextMap.get("year");
		if (StringUtils.isEmpty(year)) {
			year = DateUtil.getCurrentYear();
		}
		outputMap = new HashMap<String, Object>();
		outputMap.put("dataList", showReportService.getTargetLimitReport(year, productionType));
		outputMap.put("productionTypeList", LeaseUtil.getProductionType());
		outputMap.put("productionType", productionType);
		outputMap.put("year", year);
		Output.jspOutput(outputMap, context, "/report/getTargetLimitResult.jsp");
	}

	

	/**
	 * 求百分比
	 * 
	 * @param tested
	 *            总数
	 * @param passed
	 *            求证的数
	 * @return 百分比
	 */
	public String getPassedYield(double tested, double passed) {
		NumberFormat nf = NumberFormat.getPercentInstance();
		if (tested == 0) {
			return ("0% ");
		} else {
			return nf.format((double) passed / (double) tested);
		}
	}

	/**
	 * 查询逾期情况结果（全部）
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dun_conditionResult(Context context) {
		Map outputMap = new HashMap();
		/*List dunDailyList = null;
		try {
			dunDailyList = (List) DataAccessor.query("show.selectDunDaily",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			// 小于5天
			int countFive = 0;
			double moneyFive = 0.0;
			// 大于等于5天，小于15天
			int countFifteen = 0;
			double moneyFifteen = 0.0;
			// 大于等于15天，小于30天
			int countThirty = 0;
			double moneyThirty = 0.0;
			
			 * Add by Michael 2011 12/9
			 * 增加大于30天以上的统计
			 
			int countThirtyAbove = 0;
			double moneyThirtyAbove = 0.0;
			
			for (int i = 0; i < dunDailyList.size(); i++) {
				Map where = (Map) dunDailyList.get(i);
				int dunDay = Integer.parseInt(where.get("DUN_DAY").toString());
				// int recpId = ((Integer)where.get("RECP_ID")).intValue();
				double dunMonthprice = Double.valueOf(where.get(
						"DUN_MONTHPRICE").toString());

				if (dunDay < 5) {
					countFive++;
					moneyFive += dunMonthprice;
				} else if (dunDay >= 5 && dunDay < 15) {
					countFifteen++;
					moneyFifteen += dunMonthprice;
				} else if (dunDay >= 15 && dunDay < 30) {
					countThirty++;
					moneyThirty += dunMonthprice;
					
					 * Add by Michael 2011 12/9
					 * 增加大于30天以上的统计
					 
				}else if(dunDay>=30){
					countThirtyAbove++;
					moneyThirtyAbove+=dunMonthprice;
				}
			}
			outputMap.put("countFive", countFive);
			outputMap.put("moneyFive", moneyFive);
			outputMap.put("countFifteen", countFifteen);
			outputMap.put("moneyFifteen", moneyFifteen);
			outputMap.put("countThirty", countThirty);
			outputMap.put("moneyThirty", moneyThirty);
			
			 * Add by Michael 2011 12/9
			 * 增加大于30天以上的统计
			 
			outputMap.put("countThirtyAbove", countThirtyAbove);
			outputMap.put("moneyThirtyAbove", moneyThirtyAbove);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}*/
		//List<Map<String, Object>> dunInfo = null;
		DunCaseDetailTO dunInfo = null;
		//DunCaseChartTO to=null;
		try {
			//dunInfo = (List<Map<String, Object>>) DataAccessor.query("show.getDunInfo", new HashMap(), DataAccessor.RS_TYPE.LIST);
			Map param = new HashMap();
			//获得昨天
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			param.put("date", date);
			dunInfo = (DunCaseDetailTO) DataAccessor.query("businessReport.getDunTotalByDecpType", param, DataAccessor.RS_TYPE.OBJECT);
			
			//to=(DunCaseChartTO)DataAccessor.query("businessReport.getDeskDun",null, DataAccessor.RS_TYPE.OBJECT);
			
			/*if(dunInfo!=null&&to!=null) {
				//java.text.DecimalFormat df=new java.text.DecimalFormat("#0.00");
				dunInfo.get(0).put("count_31",to.getDun31_Count());
				dunInfo.get(0).put("money_31",Double.valueOf(to.getDun31_Money()));
				dunInfo.get(0).put("count_per_31",Double.valueOf(to.getPer31_count()));
				dunInfo.get(0).put("money_per_31",Double.valueOf(to.getPer31_money()));
				
				dunInfo.get(0).put("count_181",to.getDun181_Count());
				dunInfo.get(0).put("money_181",Double.valueOf(to.getDun181_Money()));
				dunInfo.get(0).put("count_per_181",Double.valueOf(to.getPer181_count()));
				dunInfo.get(0).put("money_per_181",Double.valueOf(to.getPer181_money()));
			}*/
			outputMap.put("dunInfo", dunInfo == null ? new ArrayList<Map<String, Object>>() : dunInfo);
			
			//获得昨天
			outputMap.put("date",date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		outputMap.put("descr","全部");
		outputMap.put("divName","dun_condition");
		Output.jspOutput(outputMap, context, "/frame/desk/dun_condition_new.jsp");
	}
	
	
	/**
	 * 查询逾期情况结果（乘用车）
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dun_condition_carResult(Context context) {
		Map outputMap = new HashMap();
		Map param = new HashMap();
		//获得昨天
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		param.put("date", date);
		param.put("cmpyType1","乘用车");
		DunCaseDetailTO dunInfo = null;
		try {
			dunInfo = (DunCaseDetailTO) DataAccessor.query("businessReport.getDunTotalByDecpType", param, DataAccessor.RS_TYPE.OBJECT);
			outputMap.put("dunInfo", dunInfo == null ? new ArrayList<Map<String, Object>>() : dunInfo);
			
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("descr","乘用车");
		outputMap.put("divName","dun_condition_car");
		Output.jspOutput(outputMap, context, "/frame/desk/dun_condition_new.jsp");
	}

	public void dun_condition_equResult(Context context) {//逾期情况  设备 add by Shenqi
		Map outputMap = new HashMap();
//		List<Map<String, Object>> dunInfo = null;
		DunCaseDetailTO dunInfo = null;
		try {
			Map param = new HashMap();
			//获得昨天
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			param.put("date", date);
			param.put("cmpyType1","设备");
			param.put("cmpyType2","办事处");
//			dunInfo = (List<Map<String, Object>>) DataAccessor.query("show.getDunInfoEqu", param, DataAccessor.RS_TYPE.LIST);
			dunInfo = (DunCaseDetailTO) DataAccessor.query("businessReport.getDunTotalByDecpType", param, DataAccessor.RS_TYPE.OBJECT);
			
			outputMap.put("dunInfo", dunInfo == null ? new ArrayList<Map<String, Object>>() : dunInfo);
			
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("descr","设备");
		outputMap.put("divName","dun_condition_equ");
		Output.jspOutput(outputMap, context, "/frame/desk/dun_condition_new.jsp");
	}
	
	public void dun_condition_motorResult(Context context) {//逾期情况  重车 add by Shenqi
		Map outputMap = new HashMap();
//		List<Map<String, Object>> dunInfo = null;
		DunCaseDetailTO dunInfo = null;
		try {
			Map param = new HashMap();
			//获得昨天
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,-1);
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			param.put("date", date);
			param.put("cmpyType1","重车");
			param.put("cmpyType2","商用车");
//			dunInfo = (List<Map<String, Object>>) DataAccessor.query("show.getDunInfoMotor", param, DataAccessor.RS_TYPE.LIST);
			dunInfo = (DunCaseDetailTO) DataAccessor.query("businessReport.getDunTotalByDecpType", param, DataAccessor.RS_TYPE.OBJECT);
			
			outputMap.put("dunInfo", dunInfo == null ? new ArrayList<Map<String, Object>>() : dunInfo);
			
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("descr","商用车");
		outputMap.put("divName","dun_condition_motor");
		Output.jspOutput(outputMap, context, "/frame/desk/dun_condition_new.jsp");
	}
	
	/**
	 * 查询应收账款结果
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void receivable_creditResult(Context context) {
		Map outputMap = new HashMap();
		try {
			Map where = (Map) DataAccessor.query(
					"show.selectRentCollectionDetail", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			Map map = (Map) DataAccessor.query("show.selectDunDetail",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("ownprice", where.get("OWNPRICE"));
			outputMap.put("renprice", where.get("RENPRICE"));
			outputMap.put("dunfine", map.get("DUNFINE"));
			outputMap.put("dunfineinterest", map.get("DUNFINEINTEREST"));
			String path = context.request.getRealPath("/")
					+ "/frame/data/accountReceivable.xml";
			CreateXmlUtil.writeXml(CreateXmlUtil
					.createAccountReceivable(outputMap), path);

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context,
				"/frame/desk/receivable_credit.jsp");
	}

	/**
	 * 查询逾期统计结果
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void dun_statisticsResult(Context context) {
		Map where = getMonth();
		Map outputMap = new HashMap();
		try {
			List list = new ArrayList();
			List oneList = (List) DataAccessor.query("show.selectDunDaily",
					where, DataAccessor.RS_TYPE.LIST);
			List twoList = (List) DataAccessor.query(
					"show.selectDunDailyTwoMonth", where,
					DataAccessor.RS_TYPE.LIST);
			List threeList = (List) DataAccessor.query(
					"show.selectDunDailyThreeMonth", where,
					DataAccessor.RS_TYPE.LIST);
			List fourList = (List) DataAccessor.query(
					"show.selectDunDailyFourMonth", where,
					DataAccessor.RS_TYPE.LIST);
			List fiveList = (List) DataAccessor.query(
					"show.selectDunDailyFiveMonth", where,
					DataAccessor.RS_TYPE.LIST);
			List sixList = (List) DataAccessor.query(
					"show.selectDunDailySixMonth", where,
					DataAccessor.RS_TYPE.LIST);
			list.add(oneList);
			list.add(twoList);
			list.add(threeList);
			list.add(fourList);
			list.add(fiveList);
			list.add(sixList);
			String path = context.request.getRealPath("/")
					+ "/frame/data/dunStatistics.xml";
			CreateXmlUtil.writeXml(CreateXmlUtil
					.createDunStatistics(getMap(list)), path);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jspOutput(outputMap, context, "/frame/desk/dun_statistics.jsp");
	}

	/**
	 * 分解集合逾期数据
	 * 
	 * @param list
	 * @return map
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public Map getMap(List list) {
		Map map = new HashMap();
		List monthList = new ArrayList();
		for (int i = 0; i < 6; i++) {
			Calendar c = Calendar.getInstance();
			// 得到上个月的月份
			c.add(c.MONTH, -i);
			Date d = c.getTime();
			String m = DateUtil.dateToString(d, "yyyy-MM");
			monthList.add(m);
		}
		List list30 = new ArrayList();
		List list60 = new ArrayList();
		List list90 = new ArrayList();
		List listEnd = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			// 小于30天
			int countThirty = 0;
			double moneyThirty = 0.0;
			// 大于等于30天，小于60天
			int countSixty = 0;
			double moneySixty = 0.0;
			// 大于等于60天，小于90天
			int countNinety = 0;
			double moneyNinety = 0.0;
			// 大于等于90天
			int countEnd = 0;
			double moneyEnd = 0.0;
			// Map map = new HashMap();
			List lis = (List) list.get(i);
			for (int j = 0; j < lis.size(); j++) {
				Map one = (Map) lis.get(j);
				int dunDay = Integer.parseInt(one.get("DUN_DAY").toString());
				double dunMonthprice = Double.valueOf(one.get("DUN_MONTHPRICE")
						.toString());
				if (dunDay < 30) {
					countThirty++;
					moneyThirty += dunMonthprice;
				} else if (dunDay >= 30 && dunDay < 60) {
					countSixty++;
					moneySixty += dunMonthprice;
				} else if (dunDay >= 60 && dunDay < 90) {
					countNinety++;
					moneyNinety += dunMonthprice;
				} else if (dunDay >= 90) {
					countEnd++;
					moneyEnd += dunMonthprice;
				}
			}
			list30.add(moneyThirty);
			// map.put("moneyThirty", moneyThirty);
			list60.add(moneySixty);
			// map.put("moneySixty", moneySixty);
			list90.add(moneyNinety);
			// map.put("moneyNinety", moneyNinety);
			listEnd.add(moneyEnd);
			// map.put("moneyEnd", moneyEnd);
		}
		map.put("monthList", monthList);
		map.put("list30", list30);
		map.put("list60", list60);
		map.put("list90", list90);
		map.put("listEnd", listEnd);

		return map;
	}

	/**
	 * 得到当前月及之前5个月的月份
	 * 
	 * @return
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public Map getMonth() {
		Map map = new HashMap();

		for (int i = 1; i < 6; i++) {
			Calendar c = Calendar.getInstance();
			// 得到上个月的月份
			c.add(c.MONTH, -i);
			Date d = c.getTime();
			String month = DateUtil.dateToString(d, "yyyy-MM") + "-01";
			if (i == 1) {
				map.put("twoMonth", month);
			} else if (i == 2) {
				map.put("threeMonth", month);
			} else if (i == 3) {
				map.put("fourMonth", month);
			} else if (i == 4) {
				map.put("fiveMonth", month);
			} else if (i == 5) {
				map.put("sixMonth", month);
			}
		}
		return map;
	}

	/**
	 * 查询租金提醒
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void dun_rentResult(Context context) {
		Map outputMap = new HashMap();
		List dunRentList = null;
		try {
			dunRentList = (List) DataAccessor.query("show.getAllDunRent",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		outputMap.put("dunRentList", dunRentList);
		Output.jspOutput(outputMap, context, "/frame/desk/dun_rent.jsp");
	}
	
	/**
	 * 查询所有结果
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getResult(Context context) {
		Map outputMap = new HashMap();
		boolean flag = checkUserByDesk(context);
		// 查看用户是否有设置
		try {
			if (!flag) {
				// 未设置则初始化数据
				List<Element> list = ReadXML.getConfigList();
				List lists = new ArrayList();
				for (Element el : list) {
					String divName = el.attributeValue("name");
					Map map = getMapByName(context, divName);
					outputMap.put(divName, map);
					lists.add(divName);
				}
				outputMap.put("names", lists);
			} else {
				// 设置过则读取数据
				List deskList = (List) DataAccessor.query(
						"show.selectDeskByUser", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				List list = new ArrayList();
				for (int i = 0; i < deskList.size(); i++) {
					Map map = (Map) deskList.get(i);
					String name = (String) map.get("DIV_ID");
					Map getMap = getMapByName(context, name);
					list.add(name);
					outputMap.put(name, getMap);
				}
				outputMap.put("names", list);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logg);
		}
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 查询符合条件的集合
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getMapByName(Context context, String name) {
		Map outputMap = new HashMap();
		try {
			if ("cust_collet".equals(name)) {
				// 客户表
				int customerCount = ((Integer) DataAccessor.query(
						"show.selectCustomerCount", context.contextMap,
						DataAccessor.RS_TYPE.OBJECT)).intValue();
				outputMap.put("customerCount", customerCount);
				// 合同
				Map contractMap = (Map) DataAccessor.query(
						"show.selectContractCountAndSum", context.contextMap,
						DataAccessor.RS_TYPE.MAP);
				outputMap.put("contractMap", contractMap);
				// 支付表
				Map collectionPlanMap = (Map) DataAccessor.query(
						"show.selectCollectionPlanCountAndSum",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("collectionPlanMap", collectionPlanMap);

			}
			if ("cust_research".equals(name)) {
				// 客户调查
				List custResearchList = (List) DataAccessor.query(
						"show.selectPrjtCredit", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("custResearchList", custResearchList);
			}
			if ("target_limit".equals(name)) {

			}
			if ("dun_condition".equals(name)) {

			}
			if ("receivable_credit".equals(name)) {

			}
			if ("dun_statistics".equals(name)) {

			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
			LogPrint.getLogStackTrace(e, logg);
		}

		return outputMap;
	}

	/**
	 * 查询用户是否设置过desk
	 * 
	 * @return true 设置过，FALSE 未设置过
	 */
	public boolean checkUserByDesk(Context context) {
		boolean flag = false;
		try {
			int count = ((Integer) DataAccessor.query(
					"show.selectDeskCountByUser", context.contextMap,
					DataAccessor.RS_TYPE.OBJECT)).intValue();
			if (count != 0) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		return flag;
	}

	/**
	 * 更新desk数据库
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateDask(Context context) {
		List list = StringByMap(context);
		Map outputMap = new HashMap();
		String msg = "";
		try {
			for (int i = 0; i < list.size(); i++) {
				Map where = (Map) list.get(i);
				boolean flag_desk = checkDeskByUserAndDiv(where);
				if (!flag_desk) {
					// 未设置则添加数据
					DataAccessor.execute("show.create", where,
							DataAccessor.OPERATION_TYPE.INSERT);
					msg = "添加成功";
				} else {
					// 设置过则修改数据
					DataAccessor.execute("show.update", where,
							DataAccessor.OPERATION_TYPE.UPDATE);
					msg = "更新成功";
				}
			}
			outputMap.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 查询用户是否添加过desk
	 * 
	 * @return true 添加过，FALSE 未添加过
	 */
	@SuppressWarnings("unchecked")
	public boolean checkDeskByUserAndDiv(Map context) {
		boolean flag = false;
		try {
			Map map = (Map) DataAccessor.query("show.selectDeskByUserAndDiv",
					context, DataAccessor.RS_TYPE.MAP);
			if (map != null) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		return flag;
	}

	/**
	 * 拆分字符串
	 * 
	 * @param result
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List StringByMap(Context context) {
		List list = new ArrayList();

		String result = context.getRequest().getParameter("result");
		String[] str = result.split("-");
		for (int i = 0; i < str.length; i++) {
			String s = str[i];
			String[] arry = s.split(":");
			if (arry.length > 1) {
				int count = 1;
				String column = arry[0];
				for (int j = 1; j < arry.length; j++) {
					String div_id = arry[j];
					Map where = new HashMap();
					where.put("desk_column", column);
					where.put("s_employeeId", context.getRequest().getSession()
							.getAttribute("s_employeeId"));
					where.put("div_id", div_id);
					where.put("desk_level", count);
					count++;
					list.add(where);

				}
			}
		}
		return list;
	}

	/**
	 * 添加模块
	 * 
	 * @param result
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void addDiv(Context context) {
		String[] divNames = context.getRequest().getParameterValues("divName");
		for (int i = 0; i < divNames.length; i++) {
			String divName = divNames[i];
			Map map = new HashMap();
			map.put("s_employeeId", context.getRequest().getSession()
					.getAttribute("s_employeeId"));
			map.put("div_id", divName);
			try {
			if (i % 2 == 0) {
				int col1=0;
				map.put("desk_column", "column_1");
				col1=(Integer)DataAccessor.query("show.getDeskLevel",map,RS_TYPE.OBJECT);
				map.put("desk_level", col1);
			} else {
				int col2=0;
				map.put("desk_column", "column_2");
				col2=(Integer)DataAccessor.query("show.getDeskLevel",map,RS_TYPE.OBJECT);
				map.put("desk_level", col2);
			}
				DataAccessor.execute("show.create", map,
						DataAccessor.OPERATION_TYPE.INSERT);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logg);
			}
		}
		Output.jspSendRedirect(context, "../frame/desktop.jsp");
	}

	/**
	 * 判断页面是否存在div层
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkDivName(Context context) {
		Map outputMap = new HashMap();
		String msg = "";
		String divName = context.getRequest().getParameter("name");
		context.contextMap.put("s_employeeId", context.getRequest()
				.getSession().getAttribute("s_employeeId"));
		context.contextMap.put("div_id", divName);
		boolean falg = checkDeskByUserAndDiv(context.contextMap);
		if (falg) {
			msg = "该模块已存在，请您重新选择";
		}
		outputMap.put("msg", msg);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 删除div层
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteDiv(Context context) {
		Map outputMap = new HashMap();
		String msg = "";
		String divName = context.getRequest().getParameter("name");
		context.contextMap.put("s_employeeId", context.getRequest()
				.getSession().getAttribute("s_employeeId"));
		context.contextMap.put("div_id", divName);
		try {
			DataAccessor.execute("show.delete", context.contextMap,
					DataAccessor.OPERATION_TYPE.DELETE);
			msg = "删除成功";
		} catch (Exception e) {
			msg = "删除失败";
			LogPrint.getLogStackTrace(e, logg);
		}
		outputMap.put("msg", msg);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 查询并添加div层
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectDiv(Context context) {
		Map outputMap = new HashMap();
		context.contextMap.put("s_employeeId", context.getRequest()
				.getSession().getAttribute("s_employeeId"));
		context.contextMap.put("dataType", "欢迎页面模块");
		try {
			// 查询字典表中所有
			List dicList = (List) DataAccessor.query(
					"permission.getWelcomeDesk", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			// 查询本用户的设置DIV层
			List deskList = (List) DataAccessor.query("show.selectDeskByUser",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
			for (Iterator it = dicList.iterator(); it.hasNext();) {
				Map dicMap = (Map) it.next();
				String dicCode = (String) dicMap.get("CODE");
				for (int i = 0; i < deskList.size(); i++) {
					Map deskMap = (Map) deskList.get(i);
					String deskCode = (String) deskMap.get("DIV_ID");
					if (deskCode.equals(dicCode)) {
						it.remove();
					}
				}
			}
			outputMap.put("dicList", dicList);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	//各区案况表的数据查询 add by ShenQi  全部的
	@SuppressWarnings("unchecked")
	public void case_reportResult(Context context) {
		
		String log="employeeId"+context.contextMap.get("s_employeeId")+"......case_reportResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotal1", param, DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","全部");
		outputMap.put("divName","case_report");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportAmountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}
	
	//各区案况表金额     设备类型
	public void case_report_equResult(Context context) {
		
		String log="employeeId"+context.contextMap.get("s_employeeId")+"......case_report_equResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForEqu",param,DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","设备");
		outputMap.put("divName","case_report_equ");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportAmountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}

	//各区案况表金额     重车类型
		public void case_report_motorResult(Context context) {
			
			String log="employeeId"+context.contextMap.get("s_employeeId")+"......case_report_equResult";
			if(logg.isDebugEnabled()) {
				logg.debug(log+" start.....");
			}
			
			Map outputMap=new HashMap();
			List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
			List<Map<String,String>> result=null;
			
			try {
				Map<String,String> param=new HashMap<String,String>();
				param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
				param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
				param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
				param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
				param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
				param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
				
				dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForMotor",param,DataAccessor.RS_TYPE.LIST);
				
				Calendar cal=Calendar.getInstance();
				String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
					for(int i=0;i<dw.size();i++) {
						if(i==(dw.size()-1)) {
							dw.get(i).put("DISPLAY","FALSE");
						}
					}
				}
				
				cal.add(Calendar.DATE,-1);
				date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				outputMap.put("date",date);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logg);
			}
			
			outputMap.put("descr","商用车");
			outputMap.put("divName","case_report_motor");
			outputMap.put("dw",dw);
			
			Output.jspOutput(outputMap, context, "/frame/desk/caseReportAmountOfDesk.jsp");
			
			if(logg.isDebugEnabled()) {
				logg.debug(log+" end.....");
			}
		}
		
		//各区案况表金额     乘用车
		public void case_report_carResult(Context context) {
			
			String log="employeeId"+context.contextMap.get("s_employeeId")+"......case_report_equResult";
			if(logg.isDebugEnabled()) {
				logg.debug(log+" start.....");
			}
			
			Map outputMap=new HashMap();
			List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
			List<Map<String,String>> result=null;
			
			try {
				Map<String,String> param=new HashMap<String,String>();
				param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
				param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
				param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
				param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
				param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
				param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
				
				dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForCar",param,DataAccessor.RS_TYPE.LIST);
				
				Calendar cal=Calendar.getInstance();
				String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
					for(int i=0;i<dw.size();i++) {
						if(i==(dw.size()-1)) {
							dw.get(i).put("DISPLAY","FALSE");
						}
					}
				}
				
				cal.add(Calendar.DATE,-1);
				date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				outputMap.put("date",date);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logg);
			}
			
			outputMap.put("descr","乘用车");
			outputMap.put("divName","case_report_car");
			outputMap.put("dw",dw);
			
			Output.jspOutput(outputMap, context, "/frame/desk/caseReportAmountOfDesk.jsp");
			
			if(logg.isDebugEnabled()) {
				logg.debug(log+" end.....");
			}
		}	
	
		
	//各区案况件数的数据查询 add by ShenQi  全部
	@SuppressWarnings("unchecked")
	public void case_report_countResult(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......case_report_countResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotal1", param, DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","全部");
		outputMap.put("divName","case_report_count");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportCountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}
	
	public void case_report_equ_countResult(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......case_report_countResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForEqu", param, DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","设备");
		outputMap.put("divName","case_report_equ_count");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportCountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}

	public void case_report_motor_countResult(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......case_report_countResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForMotor", param, DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","商用车");
		outputMap.put("divName","case_report_motor_count");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportCountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}
	
public void case_report_car_countResult(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......case_report_countResult";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<Map<String,String>> dw=new ArrayList<Map<String,String>>();
		List<Map<String,String>> result=null;
		
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("LAST_DATE_1",this.getLastDayOfMonth(1));
			param.put("LAST_DATE_2",this.getLastDayOfMonth(2));
			param.put("LAST_DATE_3",this.getLastDayOfMonth(3));
			param.put("LAST_DATE_4",this.getLastDayOfMonth(4));
			param.put("LAST_DATE_5",this.getLastDayOfMonth(5));
			param.put("LAST_DATE_6",this.getLastDayOfMonth(6));
			
			dw=(List<Map<String,String>>)DataAccessor.query("caseReportService.queryTotalForCar", param, DataAccessor.RS_TYPE.LIST);
			
			Calendar cal=Calendar.getInstance();
			String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			if("01".equals(date.split("-")[2])) {//每个月1号不显示昨天的信息
				for(int i=0;i<dw.size();i++) {
					if(i==(dw.size()-1)) {
						dw.get(i).put("DISPLAY","FALSE");
					}
				}
			}
			
			cal.add(Calendar.DATE,-1);
			date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
			outputMap.put("date",date);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("descr","乘用车");
		outputMap.put("divName","case_report_car_count");
		outputMap.put("dw",dw);
		
		Output.jspOutput(outputMap, context, "/frame/desk/caseReportCountOfDesk.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}

	private List<Map<String,String>> configResult(List<Map<String,String>> dw) {
		List<Map<String,String>> result=new ArrayList<Map<String,String>>(); 
		
		boolean LAST_DAY_OF_6=true;
		boolean LAST_DAY_OF_5=true;
		boolean LAST_DAY_OF_4=true;
		boolean LAST_DAY_OF_3=true;
		boolean LAST_DAY_OF_2=true;
		boolean LAST_DAY_OF_1=true;
		for(int i=0;i<dw.size();i++) {
			if("6".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_6=false;
			} else if("5".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_5=false;
			} else if("4".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_4=false;
			} else if("3".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_3=false;
			} else if("2".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_2=false;
			} else if("1".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
				LAST_DAY_OF_1=false;
			} else if("0".equals(dw.get(i).get("MONTH"))) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_6".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_6) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_5".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_5) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_4".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_4) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_3".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_3) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_2".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_2) {
				result.add(dw.get(i));
			}
			if("LAST_DAY_OF_1".equals(dw.get(i).get("MONTH"))&&LAST_DAY_OF_1) {
				result.add(dw.get(i));
			}
		}
		
		return result;
	}
	
	public String getLastDayOfMonth(int i) {
		
		String date="";
		
//		SimpleDateFormat mm=new SimpleDateFormat("yyyy-MM"); 
//        Calendar cal=Calendar.getInstance(); 
//        cal.setTime(new Date());
//        if(Integer.valueOf(mm.format(cal.getTime()).split("-")[1])-2==i) {
//        	if((Integer.valueOf(mm.format(cal.getTime()).split("-")[0])%4==0
//        			&&Integer.valueOf(mm.format(cal.getTime()).split("-")[0])%100!=0||
//        			Integer.valueOf(mm.format(cal.getTime()).split("-")[0])%400==0)
//        			) {
//        		date=mm.format(cal.getTime()).split("-")[0]+"-02-29";
//        	} else {
//        		date=mm.format(cal.getTime()).split("-")[0]+"-02-28";
//        	}
//        	return date;
//        }
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM"); 
        Calendar calendar=Calendar.getInstance(); 
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-i); 
        String month= sdf.format(calendar.getTime());
        int lastDay=calendar.getActualMaximum(calendar.DAY_OF_MONTH);
        
        date=month+"-"+String.valueOf(lastDay);
        return date;
	}
	
	//各区案况时间比较表 add by ShenQi
	@SuppressWarnings("unchecked")
	public void case_compare_dayResult(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......case_compare_day";
		if(logg.isDebugEnabled()) {
			logg.debug(log+" start.....");
		}
		
		Map outputMap=new HashMap();
		List<CaseCompareDayTo> resultList=null;
		List<CaseCompareDayTo> c_e_typeList=null;
		List<CaseCompareDayTo> c_e_totalList=null;
		//获得昨天
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		String date=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
		
		try {
			resultList=(List<CaseCompareDayTo>)DataAccessor.query("businessReport.avgCaseCompareDay",null, DataAccessor.RS_TYPE.LIST);
			c_e_typeList=(List<CaseCompareDayTo>)DataAccessor.query("businessReport.getCEAvgComapreDay",null, DataAccessor.RS_TYPE.LIST);
			c_e_totalList=(List<CaseCompareDayTo>)DataAccessor.query("businessReport.getCETotalAvgComapreDay",null, DataAccessor.RS_TYPE.LIST);
			
			for(int i=0;resultList!=null&&i<resultList.size();i++) {
				for(int j=0;c_e_typeList!=null&&j<c_e_typeList.size();j++) {
					if(resultList.get(i).getDeptId().equals(c_e_typeList.get(j).getDeptId())) {
						if("ONE_TIME_PASS".equals(c_e_typeList.get(j).getC_eType())) {
							resultList.get(i).setC_e_1(c_e_typeList.get(j).getC_e());
							resultList.get(i).setA_g_1(c_e_typeList.get(j).getA_g());
						} else if("NOT_ONE_TIME_PASS".equals(c_e_typeList.get(j).getC_eType())) {
							resultList.get(i).setC_e_2(c_e_typeList.get(j).getC_e());
							resultList.get(i).setA_g_2(c_e_typeList.get(j).getA_g());
						}
					}
				}
			}
			//加入一次过案是指(初次提交风控时间=最终提交风控时间)和非一次过按是指(初次提交风控时间!=最终提交风控时间)平均总计
			for(int i=0;c_e_totalList!=null&&i<c_e_totalList.size();i++) {
				if("ONE_TIME_PASS".equals(c_e_totalList.get(i).getC_eType())) {
					outputMap.put("c_e_1",c_e_totalList.get(i).getC_e());
					outputMap.put("a_g_1",c_e_totalList.get(i).getA_g());
				} else if("NOT_ONE_TIME_PASS".equals(c_e_totalList.get(i).getC_eType())) {
					outputMap.put("c_e_2",c_e_totalList.get(i).getC_e());
					outputMap.put("a_g_2",c_e_totalList.get(i).getA_g());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logg);
		}
		
		outputMap.put("date",date);
		outputMap.put("year",date.split("-")[0]);
		outputMap.put("month",date.split("-")[1]);
		outputMap.put("resultList",resultList);
		
		Output.jspOutput(outputMap,context,"/frame/desk/caseCompareDay.jsp");
		
		if(logg.isDebugEnabled()) {
			logg.debug(log+" end.....");
		}
	}
	
	public void delay_payResult(Context context){
		Map outputMap = new HashMap();
		try {
			outputMap.put("resultList", baseService.queryForList("show.getDelayPay"));
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap,context,"/frame/desk/delay_pay.jsp");
	}
	
	//add by xuyuefei
	public void case_litigationResult(Context context){
		Map outputMap = new HashMap();
		List<Map<String,Object>> back=new ArrayList<Map<String,Object>>();
		SimpleDateFormat sd1=new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd3=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			List<String> sdate=new ArrayList<String>();
			Calendar cal=Calendar.getInstance();
			int year=cal.get(Calendar.YEAR);
			cal.setTime(sd2.parse(year+"-"+"01"+"-"+"01"));
			sdate.add(sd1.format(cal.getTime()));
			while(!(year+"-"+"12").equals(sd1.format(cal.getTime()))){
				cal.add(Calendar.MONTH, 1);
				sdate.add(sd1.format(cal.getTime()));
			}
			
			for(int i=0;i<sdate.size();i++){
				Map<String,Object> backMap=new HashMap<String,Object>();
				Map<String,Object> paramMap=new HashMap<String,Object>();
				String backmonth=sdate.get(i).substring(5,7);
				if(backmonth.charAt(0)=='0'){
					backMap.put("YearAndMonth", backmonth.charAt(1));
				}else{
					backMap.put("YearAndMonth", backmonth);
				}
				paramMap.put("searchDate", sdate.get(i));
				double sum1=0;          //机器设备未缴总租金
				double sum2=0;          //乘用车未缴总租金
				double sum3=0;          //商用车未缴总租金
				//分别获取设备，乘用车，商用车的合同列表
				List<HashMap> sb=(List<HashMap>)DataAccessor.query("show.getequ", paramMap, DataAccessor.RS_TYPE.LIST);
				List<HashMap> cyc=(List<HashMap>)DataAccessor.query("show.getPassengerVehicle", paramMap, DataAccessor.RS_TYPE.LIST);
				List<HashMap> syc=(List<HashMap>)DataAccessor.query("show.geteCommercialVehicle", paramMap, DataAccessor.RS_TYPE.LIST);
				List<HashMap> newSb=new ArrayList<HashMap>();
				List<HashMap> newCyc=new ArrayList<HashMap>();
				List<HashMap> newSyc=new ArrayList<HashMap>();
				if(sb!=null||!"".equals(sb)){
					for(int j=0;j<sb.size();j++){
						paramMap.put("rectId", sb.get(j).get("RECT_ID"));
						HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
						double s1=t==null?0:
								Double.parseDouble(t.get("UNPAY_PRICE").toString());
						//未结清
						if(s1>0){
							newSb.add(t);
							sum1+=s1;
						}
					}
					backMap.put("count1", newSb.size());           //机器设备诉讼案件的件数
					backMap.put("sum1",sum1);
				}
				if(cyc!=null||!"".equals(cyc)){
					for(int j=0;j<cyc.size();j++){
						paramMap.put("rectId", cyc.get(j).get("RECT_ID"));
						HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
						double s2=t==null?0:
								Double.parseDouble(t.get("UNPAY_PRICE").toString());
						if(s2>0){
							newCyc.add(t);
							sum2+=s2;
						}
					}
					backMap.put("count2", newCyc.size());           //乘用车诉讼案件的件数
					backMap.put("sum2",sum2);
				}
				if(syc!=null||!"".equals(syc)){
					for(int j=0;j<syc.size();j++){
						paramMap.put("rectId", syc.get(j).get("RECT_ID"));
						HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
						double s3=t==null?0:
								Double.parseDouble(t.get("UNPAY_PRICE").toString());
						if(s3>0){
							newSyc.add(t);
							sum3+=s3;
						}
					}
					backMap.put("count3", newSyc.size());           //商用车诉讼案件的件数
					backMap.put("sum3",sum3);
				}
				back.add(backMap);
			}
			outputMap.put("backDate", sd3.format(new Date()));
			int total1=0;   //设备处诉讼案件总件数
			double money1=0; //设备处诉讼案件总金额
			int total2=0;   //乘用车处诉讼案件总件数
			double money2=0; //乘用车处诉讼案件总金额
			int total3=0;   //商用车处诉讼案件总件数
			double money3=0; //商用车处诉讼案件总金额
			for(int i=0;i<back.size();i++){
				total1+=Integer.parseInt(back.get(i).get("count1").toString());
				money1+=Double.parseDouble(back.get(i).get("sum1").toString());
				total2+=Integer.parseInt(back.get(i).get("count2").toString());
				money2+=Double.parseDouble(back.get(i).get("sum2").toString());
				total3+=Integer.parseInt(back.get(i).get("count3").toString());
				money3+=Double.parseDouble(back.get(i).get("sum3").toString());
			}
			outputMap.put("total1", total1);
			outputMap.put("money1", money1);
			outputMap.put("total2", total2);
			outputMap.put("money2", money2);
			outputMap.put("total3", total3);
			outputMap.put("money3", money3);
			outputMap.put("result", back);
			outputMap.put("divName","case_litigation");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspOutput(outputMap, context, "/frame/desk/case_litigation.jsp");
	}
	
	//统计所有的诉讼案件
	public void getLitigationCaseData(Context context){
			Map outputMap = new HashMap();
			List<Map<String,Object>> back=new ArrayList<Map<String,Object>>();
			SimpleDateFormat sd1=new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sd3=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				List<String> sdate=new ArrayList<String>();
				String tt=DataAccessor.query("show.getSearchDate", null, DataAccessor.RS_TYPE.OBJECT).toString();
				Calendar cal=Calendar.getInstance();
				cal.setTime(sd2.parse(tt));
				sdate.add(sd1.format(cal.getTime()));
				while(!sd1.format(new Date()).equals(sd1.format(cal.getTime()))){
					cal.add(Calendar.MONTH, 1);
					sdate.add(sd1.format(cal.getTime()));
				}
				
				for(int i=0;i<sdate.size();i++){
					Map<String,Object> backMap=new HashMap<String,Object>();
					Map<String,Object> paramMap=new HashMap<String,Object>();
				    backMap.put("YearAndMonth", sdate.get(i));
					paramMap.put("searchDate", sdate.get(i));
					double sum1=0;          //机器设备未缴总租金
					double sum2=0;          //乘用车未缴总租金
					double sum3=0;          //商用车未缴总租金
					//分别获取设备，乘用车，商用车的合同列表
					List<HashMap> sb=(List<HashMap>)DataAccessor.query("show.getequ", paramMap, DataAccessor.RS_TYPE.LIST);
					List<HashMap> cyc=(List<HashMap>)DataAccessor.query("show.getPassengerVehicle", paramMap, DataAccessor.RS_TYPE.LIST);
					List<HashMap> syc=(List<HashMap>)DataAccessor.query("show.geteCommercialVehicle", paramMap, DataAccessor.RS_TYPE.LIST);
					List<HashMap> newSb=new ArrayList<HashMap>();
					List<HashMap> newCyc=new ArrayList<HashMap>();
					List<HashMap> newSyc=new ArrayList<HashMap>();
					if(sb!=null||!"".equals(sb)){
						for(int j=0;j<sb.size();j++){
							paramMap.put("rectId", sb.get(j).get("RECT_ID"));
							HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
							double s1=t==null?0:
									Double.parseDouble(t.get("UNPAY_PRICE").toString());
							//未结清
							if(s1>0){
								newSb.add(t);
								sum1+=s1;
							}
						}
						backMap.put("count1", newSb.size());           //机器设备诉讼案件的件数
						backMap.put("sum1",sum1);
					}
					if(cyc!=null||!"".equals(cyc)){
						for(int j=0;j<cyc.size();j++){
							paramMap.put("rectId", cyc.get(j).get("RECT_ID"));
							HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
							double s2=t==null?0:
									Double.parseDouble(t.get("UNPAY_PRICE").toString());
							if(s2>0){
								newCyc.add(t);
								sum2+=s2;
							}
						}
						backMap.put("count2", newCyc.size());           //乘用车诉讼案件的件数
						backMap.put("sum2",sum2);
					}
					if(syc!=null||!"".equals(syc)){
						for(int j=0;j<syc.size();j++){
							paramMap.put("rectId", syc.get(j).get("RECT_ID"));
							HashMap t=(HashMap)DataAccessor.query("dunTask.getUnPayPriceByRectId", paramMap, DataAccessor.RS_TYPE.OBJECT);
							double s3=t==null?0:
									Double.parseDouble(t.get("UNPAY_PRICE").toString());
							if(s3>0){
								newSyc.add(t);
								sum3+=s3;
							}
						}
						backMap.put("count3", newSyc.size());           //商用车诉讼案件的件数
						backMap.put("sum3",sum3);
					}
					back.add(backMap);
				}
				outputMap.put("backDate", sd3.format(new Date()));
				int total1=0;   //设备处诉讼案件总件数
				double money1=0; //设备处诉讼案件总金额
				int total2=0;   //乘用车处诉讼案件总件数
				double money2=0; //乘用车处诉讼案件总金额
				int total3=0;   //商用车处诉讼案件总件数
				double money3=0; //商用车处诉讼案件总金额
				for(int i=0;i<back.size();i++){
					total1+=Integer.parseInt(back.get(i).get("count1").toString());
					money1+=Double.parseDouble(back.get(i).get("sum1").toString());
					total2+=Integer.parseInt(back.get(i).get("count2").toString());
					money2+=Double.parseDouble(back.get(i).get("sum2").toString());
					total3+=Integer.parseInt(back.get(i).get("count3").toString());
					money3+=Double.parseDouble(back.get(i).get("sum3").toString());
				}
				outputMap.put("total1", total1);
				outputMap.put("money1", money1);
				outputMap.put("total2", total2);
				outputMap.put("money2", money2);
				outputMap.put("total3", total3);
				outputMap.put("money3", money3);
				outputMap.put("result", back);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Output.jspOutput(outputMap, context, "/litigation/case_litigation_all.jsp");
		}
	
	//add by xuyuefei 2014/8/7
	//逾期180天以上案件
	public void case_dun_pay_180Result(Context context){
		Map outputMap = new HashMap();
		List<Map<String,Object>> back=new ArrayList<Map<String,Object>>();
		SimpleDateFormat sd1=new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd3=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar backcal=Calendar.getInstance();
		backcal.add(Calendar.DATE, -1);
		int total1=0;   //设备处逾期案件总件数
		double money1=0; //设备处逾期案件总金额
		int total2=0;   //乘用车处逾期案件总件数
		double money2=0; //乘用车处逾期案件总金额
		int total3=0;   //商用车处逾期案件总件数
		double money3=0; //商用车处逾期案件总金额
		try{
			List<String> sdate=new ArrayList<String>();
			Calendar cal=Calendar.getInstance();
			int year=cal.get(Calendar.YEAR);
			cal.setTime(sd2.parse(year+"-"+"01"+"-"+"01"));
			sdate.add(sd1.format(cal.getTime()));
			while(!(year+"-"+"12").equals(sd1.format(cal.getTime()))){
				cal.add(Calendar.MONTH, 1);
				sdate.add(sd1.format(cal.getTime()));
			}

			for(int i=0;i<sdate.size();i++){
				Map<String,Object> paramMap=new HashMap<String,Object>();
				Map<String,Object> backMap=new HashMap<String,Object>();
				String backmonth=sdate.get(i).substring(5,7);
				if(backmonth.charAt(0)=='0'){
					backMap.put("YearAndMonth", backmonth.charAt(1));
				}else{
					backMap.put("YearAndMonth", backmonth);
				}
				paramMap.put("thisMonth", sdate.get(i));
				//分别获取设备，乘用车，商用车的数据
				Map sb=(Map)DataAccessor.query("show.getDunCaseOfEqu", paramMap, DataAccessor.RS_TYPE.OBJECT);
				Map cyc=(Map)DataAccessor.query("show.getDunCaseOfPassengerVehicle", paramMap, DataAccessor.RS_TYPE.OBJECT);
				Map syc=(Map)DataAccessor.query("show.getDunCaseOfCommercialVehicle", paramMap, DataAccessor.RS_TYPE.OBJECT);
				backMap.put("sbCount",sb.get("AMOUNT")==null?"0":sb.get("AMOUNT"));
				backMap.put("sbMoney", sb.get("BALANCE")==null?"0":sb.get("BALANCE"));
				backMap.put("cycCount", cyc.get("AMOUNT")==null?"0":cyc.get("AMOUNT"));
				backMap.put("cycMoney", cyc.get("BALANCE")==null?"0":cyc.get("BALANCE"));
				backMap.put("sycCount", syc.get("AMOUNT")==null?"0":syc.get("AMOUNT"));
				backMap.put("sycMoney", syc.get("BALANCE")==null?"0":syc.get("BALANCE"));
				total1+=Integer.parseInt(sb.get("AMOUNT")==null?"0":sb.get("AMOUNT").toString());
				money1+=Double.parseDouble(sb.get("BALANCE")==null?"0":sb.get("BALANCE").toString());
				total2+=Integer.parseInt(cyc.get("AMOUNT")==null?"0":cyc.get("AMOUNT").toString());
				money2+=Double.parseDouble(cyc.get("BALANCE")==null?"0":cyc.get("BALANCE").toString());
				total3+=Integer.parseInt(syc.get("AMOUNT")==null?"0":syc.get("AMOUNT").toString());
				money3+=Double.parseDouble(syc.get("BALANCE")==null?"0":syc.get("BALANCE").toString());
				back.add(backMap);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		outputMap.put("backDate", sd2.format(backcal.getTime()));//
		outputMap.put("result", back);
		outputMap.put("total1", total1);
		outputMap.put("money1", money1);
		outputMap.put("total2", total2);
		outputMap.put("money2", money2);
		outputMap.put("total3", total3);
		outputMap.put("money3", money3);
		outputMap.put("divName","case_dun_pay_180");
		Output.jspOutput(outputMap, context, "/frame/desk/case_dun_pay_180.jsp");
	}
	
	//所有逾期180天以上的案件
	public void getDunCase(Context context){
		Map outputMap = new HashMap();
		List<Map<String,Object>> back=new ArrayList<Map<String,Object>>();
		SimpleDateFormat sd1=new SimpleDateFormat("yyyy-MM");
		SimpleDateFormat sd2=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd3=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar backcal=Calendar.getInstance();
		backcal.add(Calendar.DATE, -1);
		int total1=0;   //设备处逾期案件总件数
		double money1=0; //设备处逾期案件总金额
		int total2=0;   //乘用车处逾期案件总件数
		double money2=0; //乘用车处逾期案件总金额
		int total3=0;   //商用车处逾期案件总件数
		double money3=0; //商用车处逾期案件总金额
		try{
			List<String> sdate=new ArrayList<String>();
			String tt=DataAccessor.query("show.getMinDate", null, DataAccessor.RS_TYPE.OBJECT).toString();
			Calendar cal=Calendar.getInstance();
			cal.setTime(sd2.parse(tt));
			sdate.add(sd1.format(cal.getTime()));
			while(!sd1.format(new Date()).equals(sd1.format(cal.getTime()))){
				cal.add(Calendar.MONTH, 1);
				sdate.add(sd1.format(cal.getTime()));
			}
			for(int i=0;i<sdate.size();i++){
				Map<String,Object> paramMap=new HashMap<String,Object>();
				Map<String,Object> backMap=new HashMap<String,Object>();
				backMap.put("YearAndMonth", sdate.get(i));
				paramMap.put("thisMonth", sdate.get(i));
				//分别获取设备，乘用车，商用车的数据
				Map sb=(Map)DataAccessor.query("show.getDunCaseOfEqu", paramMap, DataAccessor.RS_TYPE.OBJECT);
				Map cyc=(Map)DataAccessor.query("show.getDunCaseOfPassengerVehicle", paramMap, DataAccessor.RS_TYPE.OBJECT);
				Map syc=(Map)DataAccessor.query("show.getDunCaseOfCommercialVehicle", paramMap, DataAccessor.RS_TYPE.OBJECT);
				backMap.put("sbCount",sb.get("AMOUNT")==null?"0":sb.get("AMOUNT"));
				backMap.put("sbMoney", sb.get("BALANCE")==null?"0":sb.get("BALANCE"));
				backMap.put("cycCount", cyc.get("AMOUNT")==null?"0":cyc.get("AMOUNT"));
				backMap.put("cycMoney", cyc.get("BALANCE")==null?"0":cyc.get("BALANCE"));
				backMap.put("sycCount", syc.get("AMOUNT")==null?"0":syc.get("AMOUNT"));
				backMap.put("sycMoney", syc.get("BALANCE")==null?"0":syc.get("BALANCE"));
				total1+=Integer.parseInt(sb.get("AMOUNT")==null?"0":sb.get("AMOUNT").toString());
				money1+=Double.parseDouble(sb.get("BALANCE")==null?"0":sb.get("BALANCE").toString());
				total2+=Integer.parseInt(cyc.get("AMOUNT")==null?"0":cyc.get("AMOUNT").toString());
				money2+=Double.parseDouble(cyc.get("BALANCE")==null?"0":cyc.get("BALANCE").toString());
				total3+=Integer.parseInt(syc.get("AMOUNT")==null?"0":syc.get("AMOUNT").toString());
				money3+=Double.parseDouble(syc.get("BALANCE")==null?"0":syc.get("BALANCE").toString());
				back.add(backMap);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		outputMap.put("backDate", sd2.format(backcal.getTime()));
		outputMap.put("result", back);
		outputMap.put("total1", total1);
		outputMap.put("money1", money1);
		outputMap.put("total2", total2);
		outputMap.put("money2", money2);
		outputMap.put("total3", total3);
		outputMap.put("money3", money3);
		outputMap.put("divName","case_dun_pay_180");
		Output.jspOutput(outputMap, context, "/dun/dunCase.jsp");
	}
}
