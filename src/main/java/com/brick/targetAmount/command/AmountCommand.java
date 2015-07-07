package com.brick.targetAmount.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.targetAmount.service.AmountService;
import com.brick.targetAmount.to.TargetAmountTo;

public class AmountCommand extends BaseCommand {
	Log logger = LogFactory.getLog(AmountCommand.class);
	private AmountService amountService;

	public AmountService getAmountService() {
		return amountService;
	}

	public void setAmountService(AmountService amountService) {
		this.amountService = amountService;
	} 

	// 区域 周
	public void queryWeekByArea(Context context) {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		// 周拨款额
		List<List<TargetAmountTo>> weekByArea = new ArrayList<List<TargetAmountTo>>();
		// 月拨款额
		List<TargetAmountTo> monthByArea = new ArrayList<TargetAmountTo>();
		String yearFirst = context.contextMap.get("year") == null ? "" : String
				.valueOf(context.contextMap.get("year"));
		if ("".equals(yearFirst)) {
			Calendar c = Calendar.getInstance();// 获得系统当前日期
			int yearNow = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
			context.contextMap.put("year", String.valueOf(yearNow));
			context.contextMap.put("month", String.valueOf(month));
		}
		try {
			// 华东
			// String[] huadong = {"17","2","7","13","16","15"};
			// 华南
			// String[] huanan = {"3","8","11"};
			// 西南
			// String[] xinan = {"9","14"};
			List<String> areaList = new ArrayList<String>();
			areaList.add("huadong");
			areaList.add("huanan");
			areaList.add("xinan");
			for (int i = 0; i < areaList.size(); i++) {
				context.contextMap.put("area", areaList.get(i));
				try {
					// 周
					List<TargetAmountTo> weekListBydecpId = amountService
							.getWeekDateByDpetId(context.contextMap);
					weekByArea.add(weekListBydecpId);
					// 月
					TargetAmountTo monthAmount = queryMonthByArea(context.contextMap);
					monthByArea.add(monthAmount);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			int year = Integer
					.parseInt((String) context.contextMap.get("year"));
			int month = Integer.parseInt((String) context.contextMap
					.get("month"));
			int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
			if (year % 4 == 0) {
				days[2] = 29;
			}
			int targetDay = days[month];
			// 每周总额不分区域
			context.contextMap.put("areaAll", "areaAll");
			List<TargetAmountTo> weekAllList = amountService.getAmountByWeek(context.contextMap);
			outputMap.put("weekAllList", weekAllList);
			outputMap.put("targetDay", targetDay);
			outputMap.put("weekByArea", weekByArea);
			outputMap.put("monthByArea", monthByArea);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		outputMap.put("year", context.contextMap.get("year"));
		outputMap.put("month", context.contextMap.get("month"));
		Output.jspOutput(outputMap, context, "/targetAmount/weekByArea.jsp");
	}

	// 月值(拨款)
	public TargetAmountTo queryMonthByArea(Map contextMap) {
		TargetAmountTo monthAmount = new TargetAmountTo();
		try {
			monthAmount = amountService.queryMonthByArea(contextMap);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return monthAmount;
	}

	// 区域 月
	public void getMonthByArea(Context context) {
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String yearFirst = context.contextMap.get("year") == null ? "" : String
				.valueOf(context.contextMap.get("year"));
		if ("".equals(yearFirst)) {
			Calendar c = Calendar.getInstance();// 获得系统当前日期
			int yearNow = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
			context.contextMap.put("year", String.valueOf(yearNow));
			context.contextMap.put("month", String.valueOf(month));
		}
		String[] months1 = { "1", "2", "3" };
		String[] months2 = { "4", "5", "6" };
		String[] months3 = { "7", "8", "9" };
		String[] months4 = { "10", "11", "12" };
		String[] areas = { "huadong", "huanan", "xinan" };
		// 区域
		List<List<TargetAmountTo>> monthThreeAreas = new ArrayList<List<TargetAmountTo>>();
		// 季度
		List<List<TargetAmountTo>> seasonAreas = new ArrayList<List<TargetAmountTo>>();
		try {
			for (String area : areas) {
				// 每个区域取某一季度3个月的列表
				List<TargetAmountTo> monthThree = new ArrayList<TargetAmountTo>();

				Map<String, Object> areaMonthMap = new HashMap<String, Object>();
				areaMonthMap.put("year",
						(String) context.contextMap.get("year"));
				areaMonthMap.put("area", area);
				if ("1".equals(context.contextMap.get("month"))
						|| "2".equals(context.contextMap.get("month"))
						|| "3".equals(context.contextMap.get("month"))) {
					// 一季度
					for (String month : months1) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAmount = amountService
								.queryMonthByArea(areaMonthMap);
						monthThree.add(monthAmount);
					}
				} else if ("4".equals(context.contextMap.get("month"))
						|| "5".equals(context.contextMap.get("month"))
						|| "6".equals(context.contextMap.get("month"))) {
					// 2季度
					for (String month : months2) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAmount = amountService
								.queryMonthByArea(areaMonthMap);
						monthThree.add(monthAmount);
					}
				} else if ("7".equals(context.contextMap.get("month"))
						|| "8".equals(context.contextMap.get("month"))
						|| "9".equals(context.contextMap.get("month"))) {
					for (String month : months3) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAmount = amountService
								.queryMonthByArea(areaMonthMap);
						monthThree.add(monthAmount);
					}
				} else if ("10".equals(context.contextMap.get("month"))
						|| "11".equals(context.contextMap.get("month"))
						|| "12".equals(context.contextMap.get("month"))) {
					for (String month : months4) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAmount = amountService
								.queryMonthByArea(areaMonthMap);
						monthThree.add(monthAmount);
					}
				}
				monthThreeAreas.add(monthThree);

				List<TargetAmountTo> seasonAmount = amountService
						.querySeasonByArea(areaMonthMap);
				seasonAreas.add(seasonAmount);

			}
			// 所有区域的 每季度和
			Map<String, Object> seasonByAllAreaMap = new HashMap<String, Object>();
			seasonByAllAreaMap.put("year", (String) context.contextMap.get("year"));
			seasonByAllAreaMap.put("areaAll", "areaAll");
			List<TargetAmountTo> seasonByAllArea = amountService.querySeasonByAllArea(seasonByAllAreaMap);
			outputMap.put("seasonByAllArea", seasonByAllArea);
			// 所有区域的 每月和
			List<TargetAmountTo> monthAllAreaList = queryMonthAllAreaList(context.contextMap);
			outputMap.put("monthAllAreaList", monthAllAreaList);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		outputMap.put("seasonAreas", seasonAreas);
		outputMap.put("year", context.contextMap.get("year"));
		outputMap.put("month", context.contextMap.get("month"));
		outputMap.put("monthThreeAreas", monthThreeAreas);
		Output.jspOutput(outputMap, context, "/targetAmount/monthByArea.jsp");
	}
	// 所有区域的 每月和
	public List<TargetAmountTo> queryMonthAllAreaList(Map mapMonth) {
		String[] months1 = { "1", "2", "3" };
		String[] months2 = { "4", "5", "6" };
		String[] months3 = { "7", "8", "9" };
		String[] months4 = { "10", "11", "12" };
		//
		List<TargetAmountTo> monthAllAreaList = new ArrayList<TargetAmountTo>();
		try {
			Map<String, Object> areaMonthMap = new HashMap<String, Object>();
			areaMonthMap.put("year", mapMonth.get("year"));
			areaMonthMap.put("areaAll", "areaAll");
			if ("1".equals(mapMonth.get("month"))
					|| "2".equals(mapMonth.get("month"))
					|| "3".equals(mapMonth.get("month"))) {
				// 一季度
				for (String month : months1) {
					areaMonthMap.put("month", month);
					TargetAmountTo monthAllArea = amountService
							.queryMonthByAllArea(areaMonthMap);
					monthAllAreaList.add(monthAllArea);
				}
			} else if ("4".equals(mapMonth.get("month"))
					|| "5".equals(mapMonth.get("month"))
					|| "6".equals(mapMonth.get("month"))) {
				// 2季度
				for (String month : months2) {
					areaMonthMap.put("month", month);
					TargetAmountTo monthAllArea = amountService
							.queryMonthByAllArea(areaMonthMap);
					monthAllAreaList.add(monthAllArea);
				}
			} else if ("7".equals(mapMonth.get("month"))
					|| "8".equals(mapMonth.get("month"))
					|| "9".equals(mapMonth.get("month"))) {
				for (String month : months3) {
					areaMonthMap.put("month", month);
					TargetAmountTo monthAllArea = amountService
							.queryMonthByAllArea(areaMonthMap);
					monthAllAreaList.add(monthAllArea);
				}
			} else if ("10".equals(mapMonth.get("month"))
					|| "11".equals(mapMonth.get("month"))
					|| "12".equals(mapMonth.get("month"))) {
				for (String month : months4) {
					areaMonthMap.put("month", month);
					TargetAmountTo monthAllArea = amountService
							.queryMonthByAllArea(areaMonthMap);
					monthAllAreaList.add(monthAllArea);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return monthAllAreaList;
	}
	/**
	 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
	 */
	// 区域 月
		public void getMonthByType(Context context) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			String yearFirst = context.contextMap.get("year") == null ? "" : String
					.valueOf(context.contextMap.get("year"));
			if ("".equals(yearFirst)) {
				Calendar c = Calendar.getInstance();// 获得系统当前日期
				int yearNow = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
				context.contextMap.put("year", String.valueOf(yearNow));
				context.contextMap.put("month", String.valueOf(month));
			}
			String[] months1 = { "1", "2", "3" };
			String[] months2 = { "4", "5", "6" };
			String[] months3 = { "7", "8", "9" };
			String[] months4 = { "10", "11", "12" };
			String[] areas = { "shebei", "shangyongche"};
			// 区域
			List<List<TargetAmountTo>> monthThreeAreas = new ArrayList<List<TargetAmountTo>>();
			// 季度
			List<List<TargetAmountTo>> seasonAreas = new ArrayList<List<TargetAmountTo>>();
			try {
				for (String area : areas) {
					// 每个区域取某一季度3个月的列表
					List<TargetAmountTo> monthThree = new ArrayList<TargetAmountTo>();

					Map<String, Object> areaMonthMap = new HashMap<String, Object>();
					areaMonthMap.put("year",
							(String) context.contextMap.get("year"));
					areaMonthMap.put("area", area);
					if ("1".equals(context.contextMap.get("month"))
							|| "2".equals(context.contextMap.get("month"))
							|| "3".equals(context.contextMap.get("month"))) {
						// 一季度
						for (String month : months1) {
							areaMonthMap.put("month", month);
							TargetAmountTo monthAmount = amountService
									.queryMonthByArea(areaMonthMap);
							monthThree.add(monthAmount);
						}
					} else if ("4".equals(context.contextMap.get("month"))
							|| "5".equals(context.contextMap.get("month"))
							|| "6".equals(context.contextMap.get("month"))) {
						// 2季度
						for (String month : months2) {
							areaMonthMap.put("month", month);
							TargetAmountTo monthAmount = amountService
									.queryMonthByArea(areaMonthMap);
							monthThree.add(monthAmount);
						}
					} else if ("7".equals(context.contextMap.get("month"))
							|| "8".equals(context.contextMap.get("month"))
							|| "9".equals(context.contextMap.get("month"))) {
						for (String month : months3) {
							areaMonthMap.put("month", month);
							TargetAmountTo monthAmount = amountService
									.queryMonthByArea(areaMonthMap);
							monthThree.add(monthAmount);
						}
					} else if ("10".equals(context.contextMap.get("month"))
							|| "11".equals(context.contextMap.get("month"))
							|| "12".equals(context.contextMap.get("month"))) {
						for (String month : months4) {
							areaMonthMap.put("month", month);
							TargetAmountTo monthAmount = amountService
									.queryMonthByArea(areaMonthMap);
							monthThree.add(monthAmount);
						}
					}
					monthThreeAreas.add(monthThree);

					List<TargetAmountTo> seasonAmount = amountService
							.querySeasonByArea(areaMonthMap);
					seasonAreas.add(seasonAmount);

				}
				// 所有区域的 每季度和
				Map<String, Object> seasonByAllAreaMap = new HashMap<String, Object>();
				seasonByAllAreaMap.put("year", (String) context.contextMap.get("year"));
				seasonByAllAreaMap.put("typeAll", "typeAll");
				List<TargetAmountTo> seasonByAllArea = amountService.querySeasonByAllArea(seasonByAllAreaMap);
				outputMap.put("seasonByAllArea", seasonByAllArea);
				// 所有区域的 每月和
				List<TargetAmountTo> monthAllAreaList = queryMonthTypeAreaList(context.contextMap);
				outputMap.put("monthAllAreaList", monthAllAreaList);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			outputMap.put("seasonAreas", seasonAreas);
			outputMap.put("year", context.contextMap.get("year"));
			outputMap.put("month", context.contextMap.get("month"));
			outputMap.put("monthThreeAreas", monthThreeAreas);
			Output.jspOutput(outputMap, context, "/targetAmount/monthByType.jsp");
		}
		
		// 所有区域的 每月和
		public List<TargetAmountTo> queryMonthTypeAreaList(Map mapMonth) {
			String[] months1 = { "1", "2", "3" };
			String[] months2 = { "4", "5", "6" };
			String[] months3 = { "7", "8", "9" };
			String[] months4 = { "10", "11", "12" };
			//
			List<TargetAmountTo> monthAllAreaList = new ArrayList<TargetAmountTo>();
			try {
				Map<String, Object> areaMonthMap = new HashMap<String, Object>();
				areaMonthMap.put("year", mapMonth.get("year"));
				areaMonthMap.put("typeAll", "typeAll");
				if ("1".equals(mapMonth.get("month"))
						|| "2".equals(mapMonth.get("month"))
						|| "3".equals(mapMonth.get("month"))) {
					// 一季度
					for (String month : months1) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAllArea = amountService
								.queryMonthByAllArea(areaMonthMap);
						monthAllAreaList.add(monthAllArea);
					}
				} else if ("4".equals(mapMonth.get("month"))
						|| "5".equals(mapMonth.get("month"))
						|| "6".equals(mapMonth.get("month"))) {
					// 2季度
					for (String month : months2) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAllArea = amountService
								.queryMonthByAllArea(areaMonthMap);
						monthAllAreaList.add(monthAllArea);
					}
				} else if ("7".equals(mapMonth.get("month"))
						|| "8".equals(mapMonth.get("month"))
						|| "9".equals(mapMonth.get("month"))) {
					for (String month : months3) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAllArea = amountService
								.queryMonthByAllArea(areaMonthMap);
						monthAllAreaList.add(monthAllArea);
					}
				} else if ("10".equals(mapMonth.get("month"))
						|| "11".equals(mapMonth.get("month"))
						|| "12".equals(mapMonth.get("month"))) {
					for (String month : months4) {
						areaMonthMap.put("month", month);
						TargetAmountTo monthAllArea = amountService
								.queryMonthByAllArea(areaMonthMap);
						monthAllAreaList.add(monthAllArea);
					}
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return monthAllAreaList;
		}
		
		// 区域 周
		public void getWeekByType(Context context) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			// 周拨款额
			List<List<TargetAmountTo>> weekByArea = new ArrayList<List<TargetAmountTo>>();
			// 月拨款额
			List<TargetAmountTo> monthByArea = new ArrayList<TargetAmountTo>();
			String yearFirst = context.contextMap.get("year") == null ? "" : String
					.valueOf(context.contextMap.get("year"));
			if ("".equals(yearFirst)) {
				Calendar c = Calendar.getInstance();// 获得系统当前日期
				int yearNow = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
				context.contextMap.put("year", String.valueOf(yearNow));
				context.contextMap.put("month", String.valueOf(month));
			}
			try {
				// 华东
				// String[] huadong = {"17","2","7","13","16","15"};
				// 华南
				// String[] huanan = {"3","8","11"};
				// 西南
				// String[] xinan = {"9","14"};
				List<String> areaList = new ArrayList<String>();
				areaList.add("shebei");
				areaList.add("shangyongche");
				for (int i = 0; i < areaList.size(); i++) {
					context.contextMap.put("area", areaList.get(i));
					try {
						// 周
						List<TargetAmountTo> weekListBydecpId = amountService
								.getWeekDateByDpetId(context.contextMap);
						weekByArea.add(weekListBydecpId);
						// 月
						TargetAmountTo monthAmount = queryMonthByArea(context.contextMap);
						monthByArea.add(monthAmount);
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
				int year = Integer
						.parseInt((String) context.contextMap.get("year"));
				int month = Integer.parseInt((String) context.contextMap
						.get("month"));
				int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
				if (year % 4 == 0) {
					days[2] = 29;
				}
				int targetDay = days[month];
				// 每周总额不分区域
				context.contextMap.put("typeAll", "typeAll");
				List<TargetAmountTo> weekAllList = amountService.getAmountByWeek(context.contextMap);
				outputMap.put("weekAllList", weekAllList);
				outputMap.put("targetDay", targetDay);
				outputMap.put("weekByArea", weekByArea);
				outputMap.put("monthByArea", monthByArea);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			outputMap.put("year", context.contextMap.get("year"));
			outputMap.put("month", context.contextMap.get("month"));
			Output.jspOutput(outputMap, context, "/targetAmount/weekByType.jsp");
		}
}
