package com.brick.bussinessReport.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.batchjob.service.AvgPayMoneyBatchJobService;
import com.brick.batchjob.to.AvgPayMoneyBatchJobTo;
import com.brick.bussinessReport.to.AchievementTo;
import com.brick.ichart.to.IchartTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;

public class AvgPayMoneyReportCommand extends BaseCommand {

	Log logger=LogFactory.getLog(AvgPayMoneyReportCommand.class);

	private AvgPayMoneyBatchJobService avgPayMoneyBatchJobService;

	public AvgPayMoneyBatchJobService getAvgPayMoneyBatchJobService() {
		return avgPayMoneyBatchJobService;
	}

	public void setAvgPayMoneyBatchJobService(
			AvgPayMoneyBatchJobService avgPayMoneyBatchJobService) {
		this.avgPayMoneyBatchJobService = avgPayMoneyBatchJobService;
	}

	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		List<String> dateList=this.avgPayMoneyBatchJobService.getDateList1();
		boolean showButtonFlag=false;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		Calendar cal=Calendar.getInstance();
		//cal.add(Calendar.DATE,-1);
		
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		} else {
			if(Integer.valueOf(context.contextMap.get("DATE").toString())!=(cal.getTime().getYear()+1900)) {
				context.contextMap.put("DATE",context.contextMap.get("DATE")+"-12-01");
			} else {
				context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			}
		}
		
		if(context.contextMap.get("SORT")==null||"".equals(context.contextMap.get("SORT"))) {
			
		}
		
		context.contextMap.put("decp_id","2");
		/*List<Map<String,Object>> deptList=this.avgPayMoneyBatchJobService.getDeptList(context.contextMap);*/
		
		List<AvgPayMoneyBatchJobTo> resultList=this.avgPayMoneyBatchJobService.getAvgPayMoneyGroupByDept(context);

		AvgPayMoneyBatchJobTo avgTotal=this.avgPayMoneyBatchJobService.getAvgPayMoneyTotal(context);
		//全年办事处zhangbo
		List<AvgPayMoneyBatchJobTo> avgYearList=this.avgPayMoneyBatchJobService.getAvgPayMoneyYearList(context);
		AvgPayMoneyBatchJobTo avgYear=this.avgPayMoneyBatchJobService.getAvgYear(context);
		//如果日期过滤条件选择的是当前月,则当前月的平均数据时时取
		int currentMonth=Calendar.getInstance().get(Calendar.MONTH)+1;
		int currentYear=Calendar.getInstance().get(Calendar.YEAR);
		int currentDay=Calendar.getInstance().get(Calendar.DATE);
		if(currentMonth==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[1])
				&&currentYear==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[0])) {
			showButtonFlag=true;
			List<AvgPayMoneyBatchJobTo> currentAvgList=this.avgPayMoneyBatchJobService.getAvgPayMoneyOfCurrentMonth(context);
			
			AvgPayMoneyBatchJobTo currentAvgTotal=this.avgPayMoneyBatchJobService.getAvgPayMoneyOfCurrentMonthTotal(context);
			for(int i=0;i<resultList.size();i++) {
				for(int j=0;currentAvgList!=null&&j<currentAvgList.size();j++) {
					if(currentAvgList.get(j).getDeptId().equals(resultList.get(i).getDeptId())) {
						if(currentMonth==1) {
							resultList.get(i).setAvgMoney_1(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_1(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==2) {
							resultList.get(i).setAvgMoney_2(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_2(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==3) {
							resultList.get(i).setAvgMoney_3(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_3(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==4) {
							resultList.get(i).setAvgMoney_4(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_4(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==5) {
							resultList.get(i).setAvgMoney_5(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_5(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==6) {
							resultList.get(i).setAvgMoney_6(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_6(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==7) {
							resultList.get(i).setAvgMoney_7(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_7(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==8) {
							resultList.get(i).setAvgMoney_8(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_8(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==9) {
							resultList.get(i).setAvgMoney_9(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_9(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==10) {
							resultList.get(i).setAvgMoney_10(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_10(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==11) {
							resultList.get(i).setAvgMoney_11(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_11(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==12) {
							resultList.get(i).setAvgMoney_12(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_12(currentAvgTotal.getAvgMoney());
						}
					}
				}
			}
			
			if(resultList.size()==0) {
				resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
				for(int i=0;currentAvgList!=null&&i<currentAvgList.size();i++) {
					AvgPayMoneyBatchJobTo to=new AvgPayMoneyBatchJobTo();
					to.setDeptId(currentAvgList.get(i).getDeptId());
					to.setDeptName(currentAvgList.get(i).getDeptName());
					to.setAvgMoney_1(currentAvgList.get(i).getAvgMoney());
					avgTotal.setAvgMoney_1(currentAvgTotal.getAvgMoney());
					resultList.add(to);
				}
			}
		}
		if(context.errList.isEmpty()) {

			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID")==null?"":context.contextMap.get("DEPT_ID"));
			outputMap.put("DATE",context.contextMap.get("DATE"));
			//outputMap.put("deptList",deptList);
			outputMap.put("avgYearList",avgYearList);
			outputMap.put("avgYear",avgYear);
			outputMap.put("resultList",resultList);
			outputMap.put("avgTotal",avgTotal);
			
			if(currentMonth==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[1])
					&&currentYear==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[0])) {
				Calendar c=Calendar.getInstance();
				c.add(Calendar.MONTH,1);
				c.set(Calendar.DATE,1);
				c.add(Calendar.DATE,-1);//获得当月最后天
				java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
				context.contextMap.remove("DEPT_ID");
				context.contextMap.put("DATE",df.format(c.getTime()));
				AvgPayMoneyBatchJobTo currentAvgTotal=this.avgPayMoneyBatchJobService.getAvgPayMoneyOfCurrentMonthTotal(context);

				List<AvgPayMoneyBatchJobTo> currentAvgTotalList=this.avgPayMoneyBatchJobService.getPayMoneyPayCountCurrentMonth(context);

				List<AchievementTo> financeDateInOneDayCount=null;
				try {
					financeDateInOneDayCount=(List<AchievementTo>)DataAccessor.query("businessReport.getFinanceDateInOneDayCount",null,DataAccessor.RS_TYPE.LIST);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				for(int i=0;i<currentAvgTotalList.size();i++) {
					if("设备款类型".equals(currentAvgTotalList.get(i).getMoneyType())) {
						//去除拨款日在同一天的相同记录
						for(int j=0;financeDateInOneDayCount!=null&&j<financeDateInOneDayCount.size();j++) {
							currentAvgTotalList.get(i).setPayCount(currentAvgTotalList.get(i).getPayCount()
									-financeDateInOneDayCount.get(j).getLastAchievementCount());
						}
						outputMap.put("currentEquCount",currentAvgTotalList.get(i).getPayCount());//设备款件数
						outputMap.put("currentEquMoney",currentAvgTotalList.get(i).getPayMoney());//设备款总金额
					} else {
						outputMap.put("currentMotorCount",currentAvgTotalList.get(i).getPayCount());//重车件数
						outputMap.put("currentMotorMoney",currentAvgTotalList.get(i).getPayMoney());//重车总金额
					}
				}
				outputMap.put("avgMoneyTotal",currentAvgTotal.getAvgMoney());
			}
			
			
			outputMap.put("currentMonth",currentMonth);
			outputMap.put("currentYear",currentYear);
			outputMap.put("currentDay",currentDay);
			outputMap.put("showButtonFlag",showButtonFlag);
			outputMap.put("DATE",context.contextMap.get("DATE").toString().split("-")[0]);
			outputMap.put("dateList",dateList);
			Output.jspOutput(outputMap,context,"/avgPayMoney/avgPayMoney.jsp");
			
			if(logger.isDebugEnabled()) {
				logger.debug(log+" end.....");
			}
			
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	//图表
	public void queryIchart(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";
		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Calendar cal=Calendar.getInstance();
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
		} else {
			if(Integer.valueOf(context.contextMap.get("DATE").toString())!=(cal.getTime().getYear()+1900)) {
				context.contextMap.put("DATE",context.contextMap.get("DATE")+"-12-01");
			} else {
				context.contextMap.put("DATE",DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd"));
			}
		}
		//已经查询的办事处列表
		List<AvgPayMoneyBatchJobTo> resultList=this.avgPayMoneyBatchJobService.getAvgPayMoneyGroupByDept(context);
		//全区
		AvgPayMoneyBatchJobTo avgTotal=this.avgPayMoneyBatchJobService.getAvgPayMoneyTotal(context);
		//如果日期过滤条件选择的是当前月,则当前月的平均数据时时取
		int currentMonth=Calendar.getInstance().get(Calendar.MONTH)+1;
		int currentYear=Calendar.getInstance().get(Calendar.YEAR);
		int currentDay=Calendar.getInstance().get(Calendar.DATE);
		if(currentMonth==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[1])
				&&currentYear==Integer.valueOf(context.contextMap.get("DATE").toString().split("-")[0])) {
			List<AvgPayMoneyBatchJobTo> currentAvgList=this.avgPayMoneyBatchJobService.getAvgPayMoneyOfCurrentMonth(context);
			AvgPayMoneyBatchJobTo currentAvgTotal=this.avgPayMoneyBatchJobService.getAvgPayMoneyOfCurrentMonthTotal(context);
			for(int i=0;i<resultList.size();i++) {
				for(int j=0;currentAvgList!=null&&j<currentAvgList.size();j++) {
					if(currentAvgList.get(j).getDeptId().equals(resultList.get(i).getDeptId())) {
						if(currentMonth==1) {
							resultList.get(i).setAvgMoney_1(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_1(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==2) {
							resultList.get(i).setAvgMoney_2(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_2(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==3) {
							resultList.get(i).setAvgMoney_3(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_3(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==4) {
							resultList.get(i).setAvgMoney_4(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_4(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==5) {
							resultList.get(i).setAvgMoney_5(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_5(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==6) {
							resultList.get(i).setAvgMoney_6(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_6(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==7) {
							resultList.get(i).setAvgMoney_7(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_7(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==8) {
							resultList.get(i).setAvgMoney_8(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_8(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==9) {
							resultList.get(i).setAvgMoney_9(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_9(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==10) {
							resultList.get(i).setAvgMoney_10(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_10(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==11) {
							resultList.get(i).setAvgMoney_11(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_11(currentAvgTotal.getAvgMoney());
						} else if(currentMonth==12) {
							resultList.get(i).setAvgMoney_12(currentAvgList.get(j).getPayMoney()/currentAvgList.get(j).getPayCount());
							avgTotal.setAvgMoney_12(currentAvgTotal.getAvgMoney());
						}
					}
				}
			}
			if(resultList.size()==0) {
				resultList=new ArrayList<AvgPayMoneyBatchJobTo>();
				for(int i=0;currentAvgList!=null&&i<currentAvgList.size();i++) {
					AvgPayMoneyBatchJobTo to=new AvgPayMoneyBatchJobTo();
					to.setDeptId(currentAvgList.get(i).getDeptId());
					to.setDeptName(currentAvgList.get(i).getDeptName());
					to.setAvgMoney_1(currentAvgList.get(i).getAvgMoney());
					avgTotal.setAvgMoney_1(currentAvgTotal.getAvgMoney());
					resultList.add(to);
				}
			}
			
		}
		
		if(context.errList.isEmpty()) {
			//ichart的date
			//曲线图集合
			List<IchartTo> ichartList =new ArrayList<IchartTo>();
			List<Double> allList =getValueList(avgTotal);
			//全区曲线图var值集合
			IchartTo ichartAll=new IchartTo();
			ichartAll.setName("全区");
			ichartAll.setColor("black");
			ichartAll.setValue(allList);
			ichartAll.setLine_width(2);
			//ichartList.add(ichartAll);
			//颜色
			String[] colors =new String[]{"black","#FF3333","#6666FF","#00FF00","#563624","#FF00FF","#00FFFF","#EEB422","#B03060","#708090"};
			//复选框
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),
					"chk_value", "");
			if(ids.length>0){
			String[] deptids= ids[0].toString().split(",");
			if(deptids.length>0){
				for (int i = 0; i < deptids.length; i++) {
					String DECP_ID = deptids[i] ;
					if(DECP_ID.equals("true")){
						ichartList.add(ichartAll);
					}
					for(AvgPayMoneyBatchJobTo decp :resultList){
						if(decp.getDeptId()!=""){
							if(decp.getDeptId().equals(DECP_ID)){
								List<Double> decpVList =getValueList(decp);
								IchartTo ichartDecp=new IchartTo();
								ichartDecp.setName(decp.getDeptName());
								ichartDecp.setColor(colors[i]);
								ichartDecp.setValue(decpVList);
								ichartDecp.setLine_width(2);
								ichartList.add(ichartDecp);
							}
						}
					}
					
				}
			}
			}
			Output.jsonArrayOutputForObject(ichartList, context);
		}
		else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
	}
	//封装曲线图值
		public List<Double> getValueList(AvgPayMoneyBatchJobTo jobto) {
				List<Double> varList= new ArrayList<Double>();
				varList.add(jobto.getAvgMoney_1());
				varList.add(jobto.getAvgMoney_2());
				varList.add(jobto.getAvgMoney_3());
				varList.add(jobto.getAvgMoney_4());
				varList.add(jobto.getAvgMoney_5());
				varList.add(jobto.getAvgMoney_6());
				varList.add(jobto.getAvgMoney_7());
				varList.add(jobto.getAvgMoney_8());
				varList.add(jobto.getAvgMoney_9());
				varList.add(jobto.getAvgMoney_10());
				varList.add(jobto.getAvgMoney_11());
				varList.add(jobto.getAvgMoney_12());
			return varList;
		}
		
		public void queryComputeMoneyCount(Context context) throws Exception {
			
			//PagingInfo<Object> pagingInfo=null;
			Map<String,Object> outputMap=new HashMap<String,Object>();
			
//			try {
//				pagingInfo=baseService.queryForListWithPaging("businessReport.queryComputeMoneyCount",context.contextMap,"PAY_DATE",ORDER_TYPE.ASC);
//			} catch(Exception e) {
//				
//			}
			List list = (List) DataAccessor.query("businessReport.getComputeMoneyCountOneYear", context.contextMap, RS_TYPE.LIST);
			outputMap.put("FROM_DATE",context.contextMap.get("FROM_DATE"));
			outputMap.put("TO_DATE",context.contextMap.get("TO_DATE"));
			outputMap.put("list",list);
			//outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("companyCode", context.contextMap.get("companyCode"));
			outputMap.put("companys", LeaseUtil.getCompanys());	
			Output.jspOutput(outputMap,context,"/avgPayMoney/computeMoneyCount.jsp");
		}
		
		public static List<Map<String,Object>> getComputeMoneyCountOneYear(String startDate,String endDate,String companyCode) {
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("FROM_DATE",startDate);
			param.put("TO_DATE",endDate);
			param.put("companyCode",companyCode);
			try {
				return (List<Map<String,Object>>)DataAccessor.query("businessReport.getComputeMoneyCountOneYear",param,RS_TYPE.LIST);
			} catch (Exception e) {
				return null;
			}
			
		}
}
