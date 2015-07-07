package com.brick.bussinessReport.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.bussinessReport.service.AchievementDailyService;
import com.brick.chartDirector.ChartDataSet;
import com.brick.chartDirector.ChartFactory;
import com.brick.chartDirector.ChartInfo;
import com.brick.chartDirector.ChartResult;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

public class AchievementDailyCommand extends BaseCommand {

	Log logger = LogFactory.getLog(AchievementDailyCommand.class);
	
	private AchievementDailyService achievementDailyService;

	public AchievementDailyService getAchievementDailyService() {
		return achievementDailyService;
	}

	public void setAchievementDailyService(
			AchievementDailyService achievementDailyService) {
		this.achievementDailyService = achievementDailyService;
	}
	
	@SuppressWarnings("unchecked")
	public void query(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> dailyAchevement=null;
		
		try {
			
			dailyAchevement=this.achievementDailyService.getDailyAchievement(context);
			
			List<Map<String,Object>> dailyCount=this.achievementDailyService.getDailyCount(context);
			
			List<Map<String,Object>> dailyMoney=(List<Map<String,Object>>)DataAccessor.query("loan.getDailyMoney",null,RS_TYPE.LIST);
			
			for(int i=0;i<dailyAchevement.size();i++) {
				int count=0;
				for(int ii=1;ii<32;ii++) {
					for(int j=0;j<dailyCount.size();j++) {
							String payDate=(String)dailyAchevement.get(i).get("PAY_DATE_"+String.valueOf(ii));
							if(payDate.equals(dailyCount.get(j).get("PAY_DATE"))) {
								count=(Integer)dailyCount.get(j).get("PAY_COUNT")+count;
								dailyAchevement.get(i).put("PAY_COUNT_"+String.valueOf(ii),count);
							} else {
								dailyAchevement.get(i).put("PAY_COUNT_"+String.valueOf(ii),count);
							}
					}
				}
			}
			
			if(dailyMoney!=null) {
				for(int i=0;i<dailyAchevement.size();i++) {
					for(int j=0;j<dailyMoney.size();j++) {
						for(int ii=1;ii<32;ii++) {
							if(dailyMoney.get(j).get("PAY_DATE").toString().split("-")[0].
									equals(dailyAchevement.get(i).get("PAY_DATE_"+ii).toString().split("-")[0])
								&&dailyMoney.get(j).get("PAY_DATE").toString().split("-")[1].
									equals(dailyAchevement.get(i).get("PAY_DATE_"+ii).toString().split("-")[1])) {
								if(DateUtil.strToDate(dailyMoney.get(j).get("PAY_DATE").toString(),"yyyy-MM-dd").
										compareTo(DateUtil.strToDate(dailyAchevement.get(i).get("PAY_DATE_"+ii).toString(),"yyyy-MM-dd"))<=0) {
									dailyAchevement.get(i).put("PAY_COUNT_"+ii,
											Integer.valueOf(dailyAchevement.get(i).get("PAY_COUNT_"+ii).toString())+Integer.valueOf(dailyMoney.get(j).get("PAY_COUNT").toString()));
									dailyAchevement.get(i).put("PAY_MONEY_"+ii,
											Double.valueOf(dailyAchevement.get(i).get("PAY_MONEY_"+ii).toString())+Double.valueOf(dailyMoney.get(j).get("PAY_MONEY").toString()));
								}
							}
							if("Y".equals(dailyAchevement.get(i).get("DISPLAY_"+ii))) {
								dailyAchevement.get(i).put("PAY_TOTAL",dailyAchevement.get(i).get("PAY_MONEY_"+ii));
							}	
						}
					}
				}
			}
			//生成折线统计图
			ChartResult chartResult1=null;
			ChartInfo chartInfo1=new ChartInfo();

			chartInfo1.chartWidth=1024;
			chartInfo1.chartHeight=350;
			chartInfo1.xLableFontAngle=0;
			chartInfo1.setChartTitle("日业绩累计比较图(金额)");
			chartInfo1.setyTitle("金额(单位K)");

			String[] xLabel=new String[31];
			for(int i=0;i<xLabel.length;i++) {
				xLabel[i]=(i+1)+"";
			}
			chartInfo1.setxLabel(xLabel);

			List<ChartDataSet> chartDataSet1=new ArrayList<ChartDataSet>();
			ChartDataSet dataSet1=null;
			List<Double> yData1=null;
			for(Map<String, Object> m:dailyAchevement) {
				dataSet1=new ChartDataSet();
				yData1=new ArrayList<Double>();
				dataSet1.setTitle(m.get("PAY_DATE_1").toString().substring(0,7)+"月");
				for (int j=1;j<=31;j++) {
					if ("Y".equals(m.get("DISPLAY_"+j))) {
						yData1.add(Double.valueOf(String.valueOf(m.get("PAY_MONEY_"+j))));
					}
				}
				dataSet1.setyData(yData1);
				chartDataSet1.add(dataSet1);
			}
			chartInfo1.setChartDataList(chartDataSet1);
			chartInfo1.setAlertTitle("{dataSetName}-{xLabel}日的金额是{value}");
			chartInfo1.setChartName("money");
			chartResult1=ChartFactory.getLineChart(context.getRequest(),chartInfo1);

			ChartResult chartResult2=null;
			ChartInfo chartInfo2=new ChartInfo();

			chartInfo2.chartWidth=1024;
			chartInfo2.chartHeight=350;
			chartInfo2.xLableFontAngle=0;
			chartInfo2.setChartTitle("日业绩累计比较图(件数)");
			chartInfo2.setyTitle("件数");

			List<ChartDataSet> chartDataSet2=new ArrayList<ChartDataSet>();
			ChartDataSet dataSet2=null;
			List<Double> yData2=null;

			chartInfo2.setxLabel(xLabel);

			for(Map<String, Object> m:dailyAchevement) {
				dataSet2=new ChartDataSet();
				yData2=new ArrayList<Double>();
				dataSet2.setTitle(m.get("PAY_DATE_1").toString().substring(0,7)+"月");
				for (int j=1;j<=31;j++) {
					if ("Y".equals(m.get("DISPLAY_"+j))) {
						yData2.add(Double.valueOf(String.valueOf(m.get("PAY_COUNT_"+j))));
					}
				}
				dataSet2.setyData(yData2);
				chartDataSet2.add(dataSet2);
			}
			chartInfo2.setChartDataList(chartDataSet2);
			chartInfo2.setAlertTitle("{dataSetName}-{xLabel}日的件数是{value}");
			chartInfo2.setChartName("count");
			chartResult2=ChartFactory.getLineChart(context.getRequest(),chartInfo2);
			
			outputMap.put("chartResult1",chartResult1);
			outputMap.put("chartResult2",chartResult2);
			
		} catch (Exception e) {
			context.errList.add("生成折线图表出错!");
			logger.debug("生成折线图表出错!");
		}

		if(context.errList.isEmpty()) {
			outputMap.put("resultList",dailyAchevement);
			Output.jspOutput(outputMap,context,"/achievementDaily/achievementDaily.jsp");
			if(logger.isDebugEnabled()) {
				logger.debug(log+" end.....");
			}
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
}
