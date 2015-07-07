package com.brick.targetAmount.service;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService;
import com.brick.targetAmount.dao.AmountDao;
import com.brick.targetAmount.to.TargetAmountTo;

public class AmountService extends BaseService {
	Log logger = LogFactory.getLog(AmountService.class);
	private AmountDao amountDao;

	public AmountDao getAmountDao() {
		return amountDao;
	}

	public void setAmountDao(AmountDao amountDao) {
		this.amountDao = amountDao;
	} 

	public List<TargetAmountTo> getWeekDateByDpetId(Map contextMap) throws ServiceException, ParseException {
		int year =Integer.parseInt((String)contextMap.get("year"));
		int month =Integer.parseInt((String)contextMap.get("month"));
		List<Map<String,String>> week = getWeekListByData(contextMap);
		String startDate = "";
		String endDate = "";
		List<TargetAmountTo> weekAmountList = new ArrayList<TargetAmountTo>();
		if(week.size()>0){
				for(Map<String,String> map :week){
					String[] start = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
					String[] end = {"SATURDAY","FRIDAY","THURSDAY","WEDNESDAY","TUESDAY","MONDAY","SUNDAY"};
					int st=0;
					int en=0;
					while("".equals(map.get(start[st])) || map.get(start[st])==null){
						st++;
					}
					startDate =map.get(start[st]);
					while("".equals(map.get(end[en])) || map.get(end[en])==null){
						en++;
					}
					endDate =map.get(end[en]);
					//查询每周的实际拨款额度（设备，商用车）、（区域）
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("startDate",startDate);
					paramMap.put("endDate",endDate);
					paramMap.put("year",(String)contextMap.get("year"));
					paramMap.put("month",(String)contextMap.get("month"));
					paramMap.put("area",contextMap.get("area"));
					TargetAmountTo weekAmount =(TargetAmountTo) queryForObj("targetAmount.getPlayAmountByWeek", paramMap);
					Map targetMoney =(Map) queryForObj("targetAmount.getMonthTargetMoneyByArea", paramMap);
					
					int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
					if (year % 4 == 0){
							days[2] = 29;
						}
					int targetDay=days[month];
					double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
					//每天的目标额
						double avgTarget =goalMoney/(double)targetDay;
					weekAmount.setTargetMoney(avgTarget*(weekAmount.getDays()+1));
					weekAmountList.add(weekAmount);
				}
		}
		return weekAmountList;
	}
	//日历
	public List<Map<String,String>> getWeekListByData(Map date) throws ServiceException {
		List<Map<String,String>> weekList = (List<Map<String,String>>) queryForList( "targetAmount.getWeekLisyByData", date);
		return weekList;
	}
	//实际拨款(目标)
	public TargetAmountTo queryMonthByArea(Map contextMap) throws ServiceException {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("year",(String)contextMap.get("year"));
		paramMap.put("month",(String)contextMap.get("month"));
		paramMap.put("area",contextMap.get("area"));
		paramMap.put("type",contextMap.get("type"));
		TargetAmountTo monthAmount =(TargetAmountTo) queryForObj("targetAmount.queryMonthByArea", paramMap);
		Map targetMoney =(Map) queryForObj("targetAmount.getMonthTargetMoneyByArea", paramMap);
		//月目标
		double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
		monthAmount.setMonthTargetMoney(goalMoney);
		return monthAmount;
	}
	//查询（设备）每周拨款、目标总额(不分区域)
	public List<TargetAmountTo> getAmountByWeek(Map contextMap) throws ServiceException, ParseException {
		int year =Integer.parseInt((String)contextMap.get("year"));
		int month =Integer.parseInt((String)contextMap.get("month"));
		List<Map<String,String>> week = getWeekListByData(contextMap);
		String startDate = "";
		String endDate = "";
		List<TargetAmountTo> weekAmountTotalList = new ArrayList<TargetAmountTo>();
		if(week.size()>0){
				for(Map<String,String> map :week){
					String[] start = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
					String[] end = {"SATURDAY","FRIDAY","THURSDAY","WEDNESDAY","TUESDAY","MONDAY","SUNDAY"};
					int st=0;
					int en=0;
					while("".equals(map.get(start[st])) || map.get(start[st])==null){
						st++;
					}
					startDate =map.get(start[st]);
					while("".equals(map.get(end[en])) || map.get(end[en])==null){
						en++;
					}
					endDate =map.get(end[en]);
					//查询每周的实际拨款额度（设备，商用车）、（区域）
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("startDate",startDate);
					paramMap.put("endDate",endDate);
					paramMap.put("year",(String)contextMap.get("year"));
					paramMap.put("month",(String)contextMap.get("month"));
					paramMap.put("areaAll",(String)contextMap.get("areaAll"));
					paramMap.put("typeAll",(String)contextMap.get("typeAll"));
					TargetAmountTo weekAmount =(TargetAmountTo) queryForObj("targetAmount.getPlayAmountByWeek", paramMap);
					Map targetMoney =(Map) queryForObj("targetAmount.getMonthTargetMoneyByArea", paramMap);
					
					int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
					if (year % 4 == 0){
							days[2] = 29;
						}
					int targetDay=days[month];
					double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
					//每天的目标额
						double avgTarget =goalMoney/(double)targetDay;
					weekAmount.setTargetMoney(avgTarget*(weekAmount.getDays()+1));
					weekAmountTotalList.add(weekAmount);
				}
		}
		return weekAmountTotalList;
	}
	////季度实际拨款(目标)(某一区域四个季度)
		public List<TargetAmountTo> querySeasonByArea(Map contextMap) throws ServiceException {
			List<TargetAmountTo> seasonAoumtByArea =new ArrayList<TargetAmountTo>();
				for(int season=1; season<=4;season++){
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("year",(String)contextMap.get("year"));
					paramMap.put("season",season);
					paramMap.put("area",contextMap.get("area"));
					paramMap.put("type",contextMap.get("type"));
					TargetAmountTo seaAmount =(TargetAmountTo) queryForObj("targetAmount.querySeasonByArea", paramMap);
					Map targetMoney =(Map) queryForObj("targetAmount.getSeasonTargetMoneyByArea", paramMap);
					//季度目标
					double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
					seaAmount.setMonthTargetMoney(goalMoney);
					seasonAoumtByArea.add(seaAmount);
				}
			return seasonAoumtByArea;
		}
		//不分区域的 每季度和
		public List<TargetAmountTo> querySeasonByAllArea(Map contextMap) throws ServiceException {
			List<TargetAmountTo> seasonAoumtByArea =new ArrayList<TargetAmountTo>();
				for(int season=1; season<=4;season++){
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("year",(String)contextMap.get("year"));
					paramMap.put("season",season);
					paramMap.put("areaAll",(String)contextMap.get("areaAll"));
					TargetAmountTo seaAmount =(TargetAmountTo) queryForObj("targetAmount.querySeasonByArea", paramMap);
					Map targetMoney =(Map) queryForObj("targetAmount.getSeasonTargetMoneyByArea", paramMap);
					//季度目标
					double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
					seaAmount.setMonthTargetMoney(goalMoney);
					seasonAoumtByArea.add(seaAmount);
				}
			return seasonAoumtByArea;
		}
		//不分区域的 每月和
		public TargetAmountTo queryMonthByAllArea(Map contextMap) throws ServiceException {
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("year",(String)contextMap.get("year"));
			paramMap.put("month",(String)contextMap.get("month"));
			paramMap.put("areaAll",(String)contextMap.get("areaAll"));
			TargetAmountTo monthAmount =(TargetAmountTo) queryForObj("targetAmount.queryMonthByArea", paramMap);
			Map targetMoney =(Map) queryForObj("targetAmount.getMonthTargetMoneyByArea", paramMap);
			//月目标
			double goalMoney=Double.parseDouble(targetMoney.get("GOAL_MONEY").toString());
			monthAmount.setMonthTargetMoney(goalMoney);
			return monthAmount;
		}
		
}
