package com.brick.customerVisit.command;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.customerVisit.service.CustomerVisitService;
import com.brick.customerVisit.to.CustomerTO;
import com.brick.log.service.LogPrint;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;

/**
 * @author ShenQi
 * @客户拜访记录计划维护Command
 * */
public class CustomerVisitCommand extends BaseCommand {

	Log logger=LogFactory.getLog(CustomerVisitCommand.class);

	private CustomerVisitService customerVisitService;

	public CustomerVisitService getCustomerVisitService() {
		return customerVisitService;
	}

	public void setCustomerVisitService(CustomerVisitService customerVisitService) {
		this.customerVisitService = customerVisitService;
	}

	//查询拜访记录
	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}

		Map<String,Object> outputMap=new HashMap<String,Object>();

		outputMap.put("NAME",context.contextMap.get("NAME")==null?context.contextMap.get("s_employeeName"):context.contextMap.get("NAME"));
		//通过employeeID获得employee NODE
		//node=0意思是可以查看全部办事处,node=1意思是只能查看自己,node=2意思是可以查看某个办事处的
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("id",context.contextMap.get("s_employeeId"));
		Map<String,Object> authentication=null;
		List<CustomerTO> weekList=null;
		List<CustomerTO> resultList=null;
		List<String> selectDateList=new ArrayList<String>();
		
		List<CustomerTO> sundayList=new ArrayList<CustomerTO>();
		List<CustomerTO> mondayList=new ArrayList<CustomerTO>();
		List<CustomerTO> tuesdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> wednesdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> thursdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> fridayList=new ArrayList<CustomerTO>();
		List<CustomerTO> saturdayList=new ArrayList<CustomerTO>();
		
		List<Map<String,Object>> empolderWayList=null;
		List<Map<String,Object>> intentList=null;
		List<Map<String,Object>> importantRecordList=null;
		
		int maxRowNum=0;
		try {
			//获得登录人的node
			authentication=this.customerVisitService.getEmpInfoById(param);
			
			Map<String,String> param1=new HashMap<String,String>();
			param1.put("dataType","开拓方式");
			empolderWayList=this.customerVisitService.queryDataDictionary(param1);
			
			param1.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param1);
			
			param1.put("dataType","重点记录");
			importantRecordList=this.customerVisitService.queryDataDictionary(param1);
			
			outputMap.put("empolderWayList",empolderWayList);
			outputMap.put("intentList",intentList);
			outputMap.put("importantRecordList",importantRecordList);
			
			//获得时间下拉框
			weekList=this.customerVisitService.getWeekList();
			outputMap.put("weekList",weekList);
		} catch (Exception e) {
			context.errList.add("查询拜访记录出错!");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}

		if(authentication.get("NODE")==null) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		} else {
			String beginDate=null;
			String endDate=null;
			//初始化进入页面获得当前周
			int node=Integer.valueOf(authentication.get("NODE").toString());
			
			if(node==1) {//业务页面获得查询条件
				if(context.contextMap.get("WEEK")==null) {
					for(int i=0;i<weekList.size();i++) {
						if("Y".equalsIgnoreCase(weekList.get(i).getCurrentWeekFlag())) {
							selectDateList.add(weekList.get(i).getSunday());
							selectDateList.add(weekList.get(i).getMonday());
							selectDateList.add(weekList.get(i).getTuesday());
							selectDateList.add(weekList.get(i).getWednesday());
							selectDateList.add(weekList.get(i).getThursday());
							selectDateList.add(weekList.get(i).getFriday());
							selectDateList.add(weekList.get(i).getSaturday());
							beginDate=weekList.get(i).getSunday();//开始日期
							endDate=weekList.get(i).getSaturday();//结束日期
							break;
						}
					}
				} else {
					beginDate=context.contextMap.get("WEEK").toString().split("~")[0];
					endDate=context.contextMap.get("WEEK").toString().split("~")[1];
					for(int i=0;i<weekList.size();i++) {
						if(beginDate.equals(weekList.get(i).getSunday())) {
							selectDateList.add(weekList.get(i).getSunday());
							selectDateList.add(weekList.get(i).getMonday());
							selectDateList.add(weekList.get(i).getTuesday());
							selectDateList.add(weekList.get(i).getWednesday());
							selectDateList.add(weekList.get(i).getThursday());
							selectDateList.add(weekList.get(i).getFriday());
							selectDateList.add(weekList.get(i).getSaturday());
						}
					}
				}
				//储存页面选择的时间条件
				outputMap.put("WEEK",context.contextMap.get("WEEK"));
			} else if(node==2||node==0) {//经理页面获得查询条件
				//*************************************************************************************
				//时间过滤条件
				if(context.contextMap.get("BEGIN_DATE")==null||"".equals(context.contextMap.get("BEGIN_DATE"))) {
					Calendar cal=Calendar.getInstance();
					beginDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				} else {
					beginDate=context.contextMap.get("BEGIN_DATE").toString();
				}
				if(context.contextMap.get("END_DATE")==null||"".equals(context.contextMap.get("END_DATE"))) {
					Calendar cal=Calendar.getInstance();
					endDate=DateUtil.dateToString(cal.getTime(),"yyyy-MM-dd");
				} else {
					endDate=context.contextMap.get("END_DATE").toString();
				}
				//*************************************************************************************
				//办事处过滤条件
				List<Map<String,Object>> deptList=null;
				Map<String,String> deptParam=new HashMap<String,String>();
				deptParam.put("node",String.valueOf(node));
				deptParam.put("id",context.contextMap.get("s_employeeId").toString());
				try {
					deptList=this.customerVisitService.queryDept(deptParam);
				} catch (Exception e) {
					context.errList.add("办事处获取出错!请联系管理员");
					LogPrint.getLogStackTrace(e,logger);
					e.printStackTrace();
				}
				//*************************************************************************************
				//是否请假过滤条件
				if(context.contextMap.get("IS_HOLIDAY")==null||"".equals(context.contextMap.get("IS_HOLIDAY"))) {
					
				} else {
					param.put("isHoliday",context.contextMap.get("IS_HOLIDAY"));
				}
				//*************************************************************************************
				//是否主管陪同过滤条件
				if(context.contextMap.get("IS_WITH_SUPERVISOR")==null||"".equals(context.contextMap.get("IS_WITH_SUPERVISOR"))) {
					
				} else {
					param.put("isWithSupervisor",context.contextMap.get("IS_WITH_SUPERVISOR"));
				}
				//*************************************************************************************
				//业务员过滤条件
				List<CustomerTO> staffList=null;
				try {
					staffList=this.customerVisitService.getStaffList(deptParam);
				} catch (Exception e) {
					context.errList.add("业务员获取出错!请联系管理员");
					LogPrint.getLogStackTrace(e,logger);
					e.printStackTrace();
				}
				if(context.contextMap.get("STAFF")==null||"".equals(context.contextMap.get("STAFF"))) {
					
				} else {
					param.put("userId",context.contextMap.get("STAFF"));
				}
				//*************************************************************************************
				//查询内容过滤条件
				if(context.contextMap.get("CONTENT")==null||"".equals(context.contextMap.get("CONTENT"))) {
					
				} else {
					param.put("content",context.contextMap.get("CONTENT").toString().trim());
				}
				
				//纯纯页面查询条件
				outputMap.put("CONTENT",context.contextMap.get("CONTENT"));
				outputMap.put("STAFF",context.contextMap.get("STAFF"));
				outputMap.put("IS_HOLIDAY",context.contextMap.get("IS_HOLIDAY"));
				outputMap.put("IS_WITH_SUPERVISOR",context.contextMap.get("IS_WITH_SUPERVISOR"));
				outputMap.put("staffList",staffList);
				outputMap.put("deptList",deptList);
				outputMap.put("BEGIN_DATE",beginDate);
				outputMap.put("END_DATE",endDate);
				outputMap.put("DEPT_ID",(String)context.contextMap.get("DEPT_ID"));
			}

			param.put("beginDate",beginDate);
			param.put("endDate",endDate);
			param.put("TYPE1","开拓方式");
			param.put("TYPE2","拜访目的");
			param.put("TYPE3","重点记录");
			try{
				
				if(context.errList.isEmpty()) {
					if(node==1) {
						param.put("node",1);
						resultList=this.customerVisitService.getCustomerVisit(param);
						if(resultList.size()==0) {
							maxRowNum=0;
						} else {
							maxRowNum=this.customerVisitService.getMaxRow(param);
						}
						for(int i=0;i<selectDateList.size();i++) {
							for(int j=0;j<resultList.size();j++) {
								if(i==0&&selectDateList.get(i).equals(resultList.get(j).getDate())) {//0是星期日
									sundayList.add(resultList.get(j));
								} else if(i==1&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									mondayList.add(resultList.get(j));
								} else if(i==2&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									tuesdayList.add(resultList.get(j));
								} else if(i==3&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									wednesdayList.add(resultList.get(j));
								} else if(i==4&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									thursdayList.add(resultList.get(j));
								} else if(i==5&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									fridayList.add(resultList.get(j));
								} else if(i==6&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
									saturdayList.add(resultList.get(j));
								}
							}
						}
						outputMap.put("sundayList",this.addRow(sundayList,maxRowNum));
						outputMap.put("mondayList",this.addRow(mondayList,maxRowNum));
						outputMap.put("tuesdayList",this.addRow(tuesdayList,maxRowNum));
						outputMap.put("wednesdayList",this.addRow(wednesdayList,maxRowNum));
						outputMap.put("thursdayList",this.addRow(thursdayList,maxRowNum));
						outputMap.put("fridayList",this.addRow(fridayList,maxRowNum));
						outputMap.put("saturdayList",this.addRow(saturdayList,maxRowNum));
						outputMap.put("resultList",resultList);
						outputMap.put("RESULT",context.contextMap.get("result"));
						Output.jspOutput(outputMap,context,"/customerVisit/customerVisitStaff.jsp");
					} else if(node==2) {
						param.put("node",2);
						param.put("deptId",(String)context.contextMap.get("DEPT_ID"));
						resultList=this.customerVisitService.getCustomerVisit(param);
						outputMap.put("resultList",resultList);
						Output.jspOutput(outputMap,context,"/customerVisit/customerVisitManager.jsp");
					} else if(node==0) {
						param.put("node",0);
						param.put("deptId",(String)context.contextMap.get("DEPT_ID"));
						resultList=this.customerVisitService.getCustomerVisit(param);
						outputMap.put("resultList",resultList);
						Output.jspOutput(outputMap,context,"/customerVisit/customerVisitManager.jsp");
					}
				} else {
					outputMap.put("errList",context.errList);
					Output.jspOutput(outputMap,context,"/error.jsp");
				}
			} catch(Exception e) {
				LogPrint.getLogStackTrace(e,logger);
				e.printStackTrace();
				
				context.errList.add("查询拜访记录出错!");
				outputMap.put("errList",context.errList);
				Output.jspOutput(outputMap,context,"/error.jsp");
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}

	private List<CustomerTO> addRow(List<CustomerTO> list,int maxRowNum) {
		int loop=maxRowNum-list.size();
		for(int i=0;i<loop;i++) {
			list.add(new CustomerTO("","","","","","","","","","","","","","","N","","","","","","","","","","","","",""));
		}
		return list;
	}
	//跳转添加拜访记录页面
	public void toAddPage(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......toAddPage";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<Map<String,Object>> provincesList=null;
		List<Map<String,Object>> empolderWayList=null;
		List<Map<String,Object>> intentList=null;
		List<Map<String,String>> importRecordList=null;
		List<CustomerTO> employeeList=null;
		
		Map<String,String> param=new HashMap<String,String>();
		try {
			provincesList=this.customerVisitService.getProvincesList();
			
			param.put("dataType","开拓方式");
			empolderWayList=this.customerVisitService.queryDataDictionary(param);
			
			param.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param);
			
			param.put("dataType","重点记录");
			param.put("shortName","1");
			importRecordList=this.customerVisitService.queryDataDictionary1(param);
			
			param.put("id",context.contextMap.get("s_employeeId").toString());
			employeeList=this.customerVisitService.getEmployeeList(param);
			
		} catch (Exception e) {
			context.errList.add("跳转新增客户页面出错!");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		outputMap.put("provinces",provincesList);
		outputMap.put("empolderWayList",empolderWayList);
		outputMap.put("intentList",intentList);
		outputMap.put("importRecordList",importRecordList);
		outputMap.put("employeeList",employeeList);
		outputMap.put("NAME",context.contextMap.get("s_employeeName"));
		outputMap.put("RESULT",context.contextMap.get("result"));
		Output.jspOutput(outputMap,context,"/customerVisit/customerAdd.jsp");
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	//添加拜访记录方法
	public void add(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......add";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		CustomerTO customerTO=new CustomerTO();
		customerTO.setEmployeeId(context.contextMap.get("s_employeeId").toString());
		customerTO.setName(context.contextMap.get("s_employeeName").toString());
		customerTO.setDate(context.contextMap.get("DATE").toString());
		customerTO.setObject((String)context.contextMap.get("OBJECT"));
		customerTO.setEmpolderWay((String)context.contextMap.get("EMPOLDER_WAY"));
		customerTO.setProvinceId((String)context.contextMap.get("province_id"));
		customerTO.setCityId((String)context.contextMap.get("city_id"));
		customerTO.setAreaId((String)context.contextMap.get("area_id"));
		customerTO.setIntent((String)context.contextMap.get("INTENT"));
		customerTO.setExpectFromTime(context.contextMap.get("DATE")+" "+context.contextMap.get("FROM_HOUR")+":"+context.contextMap.get("FROM_MIN")+":00");
		customerTO.setExpectToTime(context.contextMap.get("DATE")+" "+context.contextMap.get("TO_HOUR")+":"+context.contextMap.get("TO_MIN")+":00");
		customerTO.setWithSupervisor((String)context.contextMap.get("WITH_SUPERVISOR"));
		customerTO.setImportantRecord((String)context.contextMap.get("IMPORTANT_RECORD"));
		customerTO.setRemark((String)context.contextMap.get("REMARK"));
		customerTO.setNeedColleague((String)context.contextMap.get("COLLEAGUE"));
		customerTO.setEmployee((String)context.contextMap.get("EMPLOYEE"));
		if("Y".equals(context.contextMap.get("HOLIDAY").toString())) {
			/*if("Y".equals(context.contextMap.get("FULL_DAY").toString())) {
				String fromTimeFull=(String)context.contextMap.get("HOLIDAY_FROM_HOUR_FULL")+":"+(String)context.contextMap.get("HOLIDAY_FROM_MIN_FULL")+":00";
				String toTimeFull=(String)context.contextMap.get("HOLIDAY_TO_HOUR_FULL")+":"+(String)context.contextMap.get("HOLIDAY_TO_MIN_FULL")+":00";
				customerTO.setHoliday("Y");
				customerTO.setHolidayFromTime(context.contextMap.get("DATE")+" "+fromTimeFull);
				customerTO.setHolidayToTime(context.contextMap.get("DATE")+" "+toTimeFull);
			} else {*/
				customerTO=new CustomerTO();
				customerTO.setEmployeeId(context.contextMap.get("s_employeeId").toString());
				customerTO.setName(context.contextMap.get("s_employeeName").toString());
				customerTO.setDate(context.contextMap.get("DATE").toString());
				String fromTimePart=(String)context.contextMap.get("HOLIDAY_FROM_HOUR_PART")+":"+(String)context.contextMap.get("HOLIDAY_FROM_MIN_PART")+":00";
				String toTimePart=(String)context.contextMap.get("HOLIDAY_TO_HOUR_PART")+":"+(String)context.contextMap.get("HOLIDAY_TO_MIN_PART")+":00";
				customerTO.setHoliday("Y");
				customerTO.setHolidayFromTime(context.contextMap.get("DATE")+" "+fromTimePart);
				customerTO.setHolidayToTime(context.contextMap.get("DATE")+" "+toTimePart);
			//}
		} else {
			customerTO.setHoliday("N");
		}
		
		try {
			this.customerVisitService.addCustomerVisit(customerTO);
			context.contextMap.put("result","Y");
		} catch(Exception e) {
			context.contextMap.put("result","N");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			this.toAddPage(context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void delete(Context context) {
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......delete";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("ID",context.contextMap.get("id").toString());
		try {
			this.customerVisitService.deleteCustomerVisit(param);
		} catch (Exception e) {
			context.errList.add("删除客户拜访记录出错!请联系管理员");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(context.errList.isEmpty()) {
			this.query(context);
		} else {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	public void update(Context context) {
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......update";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		String id=context.contextMap.get("id").toString();
		CustomerTO customerTO=new CustomerTO(id,
											 context.contextMap.get("s_employeeId").toString(),
											 context.contextMap.get("DATE"+id)+//日期
											 							   " "+//空格
											 context.contextMap.get("ACTUAL_FROM_HOUR"+context.contextMap.get("id").toString())+":"+//小时
											 context.contextMap.get("ACTUAL_FROM_MIN"+context.contextMap.get("id").toString())+":00",//分钟
											 context.contextMap.get("DATE"+id)+//日期
				 							   							   " "+//空格
											 context.contextMap.get("ACTUAL_TO_HOUR"+context.contextMap.get("id").toString())+":"+//小时
											 context.contextMap.get("ACTUAL_TO_MIN"+context.contextMap.get("id").toString())+":00",//分钟
											 context.contextMap.get("OBJECT"+id)+"",
											 context.contextMap.get("EMPOLDER_WAY"+id)+"",
											 context.contextMap.get("INTENT"+id)+"",
											 context.contextMap.get("WITH_SUPERVISOR"+id)+"");
		
		try {
			this.customerVisitService.updateCustomerVisit(customerTO);
			context.contextMap.put("result","Y");
		} catch (Exception e) {
			context.errList.add("更新客户拜访记录出错!请联系管理员");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
		
		if(context.errList.isEmpty()) {
			this.query(context);
		} else {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
	}
	
	public void viewRemark(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......viewRemark";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		Map<String,String> param=new HashMap<String,String>();
		String remark=null;
		
		try {
			param.put("id",(String)context.contextMap.get("id"));
			remark=this.customerVisitService.getRemark(param);
		} catch (Exception e) {
			context.errList.add("获取备注出错!请联系管理员");
			LogPrint.getLogStackTrace(e,logger);
			e.printStackTrace();
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		outputMap.put("REMARK",remark);
		if(context.errList.isEmpty()) {
			Output.jsonOutput(outputMap,context);
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void viewStaffCustVisit(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......viewStaffCustVisit";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		int maxRowNum=0;
		String date=context.contextMap.get("date").toString();
		
		Map<String,Object> outputMap=new HashMap<String,Object>();

		List<Map<String,Object>> empolderWayList=null;
		List<Map<String,Object>> intentList=null;
		List<Map<String,Object>> importantRecordList=null;
		
		outputMap.put("NAME",context.contextMap.get("employeeName"));
		
		List<CustomerTO> resultList=null;
		List<CustomerTO> weekList=null;
		List<String> selectDateList=new ArrayList<String>();
		
		List<CustomerTO> sundayList=new ArrayList<CustomerTO>();
		List<CustomerTO> mondayList=new ArrayList<CustomerTO>();
		List<CustomerTO> tuesdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> wednesdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> thursdayList=new ArrayList<CustomerTO>();
		List<CustomerTO> fridayList=new ArrayList<CustomerTO>();
		List<CustomerTO> saturdayList=new ArrayList<CustomerTO>();
		
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("id",context.contextMap.get("employeeId"));
		param.put("node",1);
		param.put("TYPE1","开拓方式");
		param.put("TYPE2","拜访目的");
		param.put("TYPE3","重点记录");
		
		try {
			//**********************************************************************************
			Map<String,String> param1=new HashMap<String,String>();
			param1.put("dataType","开拓方式");
			empolderWayList=this.customerVisitService.queryDataDictionary(param1);
			
			param1.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param1);
			
			param1.put("dataType","重点记录");
			importantRecordList=this.customerVisitService.queryDataDictionary(param1);
			
			outputMap.put("empolderWayList",empolderWayList);
			outputMap.put("intentList",intentList);
			outputMap.put("importantRecordList",importantRecordList);
			//**********************************************************************************
			
			weekList=this.customerVisitService.getWeekList();
			for(int i=0;i<weekList.size();i++) {
				if(date.equals(weekList.get(i).getSunday())||date.equals(weekList.get(i).getMonday())||
						date.equals(weekList.get(i).getTuesday())||date.equals(weekList.get(i).getWednesday())||
							date.equals(weekList.get(i).getThursday())||date.equals(weekList.get(i).getFriday())||
								date.equals(weekList.get(i).getSaturday())) {
					selectDateList.add(weekList.get(i).getSunday());
					selectDateList.add(weekList.get(i).getMonday());
					selectDateList.add(weekList.get(i).getTuesday());
					selectDateList.add(weekList.get(i).getWednesday());
					selectDateList.add(weekList.get(i).getThursday());
					selectDateList.add(weekList.get(i).getFriday());
					selectDateList.add(weekList.get(i).getSaturday());
					break;
				}
			}
			//**********************************************************************************
			//设定title的日期
			weekList=new ArrayList<CustomerTO>();
			CustomerTO to=new CustomerTO(selectDateList.get(0),selectDateList.get(1),selectDateList.get(2),selectDateList.get(3),selectDateList.get(4),selectDateList.get(5),selectDateList.get(6));	
			to.setCurrentWeekFlag("Y");
			weekList.add(to);
			outputMap.put("weekList",weekList);
			//**********************************************************************************
			
			param.put("beginDate",selectDateList.get(0));//星期日
			param.put("endDate",selectDateList.get(6));//星期六
			
			resultList=this.customerVisitService.getCustomerVisit(param);
			
			if(resultList.size()==0) {
				maxRowNum=0;
			} else {
				maxRowNum=this.customerVisitService.getMaxRow(param);
			}
			
			for(int i=0;i<selectDateList.size();i++) {
				for(int j=0;j<resultList.size();j++) {
					if(i==0&&selectDateList.get(i).equals(resultList.get(j).getDate())) {//0是星期日
						sundayList.add(resultList.get(j));
					} else if(i==1&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						mondayList.add(resultList.get(j));
					} else if(i==2&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						tuesdayList.add(resultList.get(j));
					} else if(i==3&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						wednesdayList.add(resultList.get(j));
					} else if(i==4&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						thursdayList.add(resultList.get(j));
					} else if(i==5&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						fridayList.add(resultList.get(j));
					} else if(i==6&&selectDateList.get(i).equals(resultList.get(j).getDate())) {
						saturdayList.add(resultList.get(j));
					}
				}
			}
			outputMap.put("sundayList",this.addRow(sundayList,maxRowNum));
			outputMap.put("mondayList",this.addRow(mondayList,maxRowNum));
			outputMap.put("tuesdayList",this.addRow(tuesdayList,maxRowNum));
			outputMap.put("wednesdayList",this.addRow(wednesdayList,maxRowNum));
			outputMap.put("thursdayList",this.addRow(thursdayList,maxRowNum));
			outputMap.put("fridayList",this.addRow(fridayList,maxRowNum));
			outputMap.put("saturdayList",this.addRow(saturdayList,maxRowNum));
			outputMap.put("resultList",resultList);
		} catch (Exception e) {
			context.errList.add("查看业务员拜访记录出错!请联系管理员");
			e.printStackTrace();
		}
		
		if(context.errList.isEmpty()) {
			outputMap.put("NOT_DISPLAY_SEARCH_UPDATE_BUTTON","Y");
			Output.jspOutput(outputMap,context,"/customerVisit/customerVisitStaff.jsp");
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void getImportanceRecord(Context context) {
		
		String log="employeeId="+context.contextMap.get("s_employeeId")+"......getImportanceRecord";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("dataType","重点记录");
		param.put("shortName",(String)context.contextMap.get("shortName"));
		
		List<Map<String,String>> importRecordList=null;
		
		try {
			importRecordList=this.customerVisitService.queryDataDictionary1(param);
			Output.jsonArrayListOutput(importRecordList,context);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
}
