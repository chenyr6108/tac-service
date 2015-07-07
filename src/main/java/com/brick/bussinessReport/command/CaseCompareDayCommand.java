package com.brick.bussinessReport.command;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.batchjob.service.CaseCompareDayService;
import com.brick.batchjob.to.CaseCompareDayTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class CaseCompareDayCommand extends BaseCommand {

	Log logger=LogFactory.getLog(CaseCompareDayCommand.class);

	private CaseCompareDayService caseCompareDayService;

	public CaseCompareDayService getCaseCompareDayService() {
		return caseCompareDayService;
	}

	public void setCaseCompareDayService(CaseCompareDayService caseCompareDayService) {
		this.caseCompareDayService = caseCompareDayService;
	}

	public void query(Context context) {

		String log="employeeId="+context.contextMap.get("s_employeeId")+"......query";

		if(logger.isDebugEnabled()) {
			logger.debug(log+" start.....");
		}
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		this.validate(context);//后台验证
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		List<CaseCompareDayTo> resultList=null;
		
		if(context.contextMap.get("DATE")==null||"".equals(context.contextMap.get("DATE"))) {
			Calendar c=Calendar.getInstance();
			c.add(Calendar.DATE,-1);
			java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
//			context.contextMap.put("DATE",df.format(c.getTime()));
			outputMap.put("DATE",df.format(c.getTime()));
			context.contextMap.put("IS_CURRENT_MONTH","Y");
		} else {
			Calendar c=Calendar.getInstance();
			java.text.SimpleDateFormat df=new java.text.SimpleDateFormat("yyyy-MM");
			String date=context.contextMap.get("DATE").toString().split("-")[0]+context.contextMap.get("DATE").toString().split("-")[1];
			if(df.format(c.getTime()).equals(date)) {
				context.contextMap.put("IS_CURRENT_MONTH","Y");
				outputMap.put("DATE",context.contextMap.get("DATE").toString());
			} else {
				context.contextMap.put("IS_CURRENT_MONTH","N");
				outputMap.put("DATE",context.contextMap.get("DATE").toString());
			}
		}
		
		//从异常案件跳转过来
		if("Y".equals(context.contextMap.get("isUnnatural"))) {
			context.contextMap.put("IS_CURRENT_MONTH","N");
			outputMap.put("isUnnatural","Y");
		}
		
		if("Y".equals(context.contextMap.get("showBackButton"))) {
			outputMap.put("isUnnatural","Y");
		}
		Map<String,List<CaseCompareDayTo>> filterMap=new HashMap<String,List<CaseCompareDayTo>>();
		
		try {
			filterMap=this.caseCompareDayService.getCaseCompareDayFilter();
			
			//获得时间下拉框
			List<CaseCompareDayTo> dateList=filterMap.get("dateList");
			outputMap.put("dateList",dateList);
			
			//获得办事处下拉框
			List<CaseCompareDayTo> deptList=filterMap.get("deptList");
			outputMap.put("deptList",deptList);
			outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
			
			//获得用户名下拉框
			List<CaseCompareDayTo> userList=filterMap.get("userList");
			outputMap.put("userList",userList);
			outputMap.put("USER_ID",context.contextMap.get("USER_ID"));
			
			//排序,默认设置
			if(context.contextMap.get("TYPE")==null||"".equals(context.contextMap.get("TYPE"))) {
				/*context.contextMap.put("TYPE","A_F");*/
				context.contextMap.put("TYPE","A_G");
				context.contextMap.put("SORT","DESC");
			} else {
				context.contextMap.put("SORT",context.contextMap.get(context.contextMap.get("TYPE")+"_SORT")==null?"DESC":context.contextMap.get(context.contextMap.get("TYPE")+"_SORT"));
			}
			outputMap.put(context.contextMap.get("TYPE")+"_SORT",context.contextMap.get("SORT"));
			resultList=this.caseCompareDayService.queryCaseCompareDay(context);
			
		} catch(Exception e) {
			context.errList.add("案件进度时间比较出错!请联系管理员");
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		if(context.errList.isEmpty()) {
			
			outputMap.put("A_G",context.contextMap.get("A-G"));
			outputMap.put("A_B",context.contextMap.get("A-B"));
			outputMap.put("B_C",context.contextMap.get("B-C"));
			outputMap.put("C_D",context.contextMap.get("C-D"));
			outputMap.put("D_E",context.contextMap.get("D-E"));
			outputMap.put("E_F",context.contextMap.get("E-F"));
			outputMap.put("F_G",context.contextMap.get("F-G"));
			outputMap.put("CONTENT",context.contextMap.get("CONTENT"));
			outputMap.put("resultList",resultList);
			Output.jspOutput(outputMap,context,"/caseCompareDay/caseCompareDay.jsp");
			
		} else {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug(log+" end.....");
		}
	}
	
	public void reset(Context context) {
		
		context.contextMap.put("A-G","");
		context.contextMap.put("A-B","");
		context.contextMap.put("B-C","");
		context.contextMap.put("C-D","");
		context.contextMap.put("D-E","");
		context.contextMap.put("E-F","");
		context.contextMap.put("F-G","");
		context.contextMap.put("DATE","");
		context.contextMap.put("DEPT_ID","");
		context.contextMap.put("USER_ID","");
		context.contextMap.put("TYPE","");
		this.query(context);
	}
	
	public void validate(Context context) {
		
		Pattern pattern = Pattern.compile("[0-9]*");
		String a_g=(String)context.contextMap.get("A-G");
		String a_b=(String)context.contextMap.get("A-B");
		String b_c=(String)context.contextMap.get("B-C");
		String c_d=(String)context.contextMap.get("C-D");
		String d_e=(String)context.contextMap.get("D-E");
		String e_f=(String)context.contextMap.get("E-F");
		String f_g=(String)context.contextMap.get("F-G");
		
		if(a_g!=null&&!"".equals(a_g)) {
			if(!pattern.matcher(a_g).matches()) {
				context.errList.add("G-A栏位必须输入整数!");
			}
		}
		if(a_b!=null&&!"".equals(a_b)) {
			if(!pattern.matcher(a_b).matches()) {
				context.errList.add("G-F栏位必须输入整数!");
			}
		}
		if(b_c!=null&&!"".equals(b_c)) {
			if(!pattern.matcher(b_c).matches()) {
				context.errList.add("F-E栏位必须输入整数!");
			}
		}
		if(c_d!=null&&!"".equals(c_d)) {
			if(!pattern.matcher(c_d).matches()) {
				context.errList.add("E-D栏位必须输入整数!");
			}
		}
		if(d_e!=null&&!"".equals(d_e)) {
			if(!pattern.matcher(d_e).matches()) {
				context.errList.add("D-C栏位必须输入整数!");
			}
		}
		if(e_f!=null&&!"".equals(e_f)) {
			if(!pattern.matcher(e_f).matches()) {
				context.errList.add("C-B栏位必须输入整数!");
			}
		}
		if(f_g!=null&&!"".equals(f_g)) {
			if(!pattern.matcher(f_g).matches()) {
				context.errList.add("B-A栏位必须输入整数!");
			}
		}
	}
}
