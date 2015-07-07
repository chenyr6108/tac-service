package com.brick.prePayStatic.command;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.log.service.LogPrint;
import com.brick.prePayStatic.to.PrePayStaticForSupplierJobTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.ibm.icu.text.SimpleDateFormat;

public class PrePayStaticForSupplierCommand extends BaseCommand {
         
	Log logger = LogFactory.getLog(PrePayStaticForSupplierCommand.class);
	private Calendar date = Calendar.getInstance(); 
	
	   public void query(Context context){
		   
		   Map<String, Object> outputMap = new HashMap<String, Object>();
		   DataWrap dw=null;
		   SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		   
		  if(context.contextMap.get("startDate")==null
			  ||"".equals((String)context.contextMap.get("startDate"))	  
		      || context.contextMap.get("endDate")==null
		      ||"".equals((String)context.contextMap.get("endDate"))){
			  
			  context.contextMap.put("end",new Date());                  //设置起始日期 
			  date.setTime(new Date());
			  date.set(Calendar.DATE, date.get(Calendar.DATE) - 29);
			  context.contextMap.put("begin", date.getTime());               //设置终止日期
			  SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			  outputMap.put("end", sdf.format(new Date()));
			  outputMap.put("begin", sdf.format(context.contextMap.get("begin")));
		  }else{
			  context.contextMap.put("begin",context.contextMap.get("startDate"));
			  context.contextMap.put("end",context.contextMap.get("endDate"));
			  outputMap.put("begin", context.contextMap.get("startDate"));
			  outputMap.put("end", context.contextMap.get("endDate"));
		  }
		   try {

			dw=(DataWrap)DataAccessor.query("prePayStaticForSupplier.getAll"
					, context.contextMap, DataAccessor.RS_TYPE.PAGED);
			
		   } catch (Exception e) {
			LogPrint.getLogStackTrace(e, logger);
			e.printStackTrace();
			context.errList.add("数据查询失败!");
		}
		   if(context.errList.isEmpty()){
			    outputMap.put("backDate", df.format(new Date()));
			    outputMap.put("dw", dw);
			    outputMap.put("dataList", dw.rs);
			   Output.jspOutput(outputMap, context, "/prePayStatic/prePayStaticForSupplier.jsp");
		   }else{
				outputMap.put("errList", context.errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
		   }
	   }
	   
	   
	   //run job
	   @SuppressWarnings("unchecked")
	@Transactional(rollbackFor=Exception.class)
	   public void addLog() throws Exception{
		   Map paramMap=new HashMap();
			List<PrePayStaticForSupplierJobTo> list=(List<PrePayStaticForSupplierJobTo>)DataAccessor
					.query("prePayStaticForSupplier.getJobData", null, DataAccessor.RS_TYPE.LIST);
			
	          for (PrePayStaticForSupplierJobTo p : list) {
				p.setId(String.valueOf(System.currentTimeMillis()));
				paramMap.put("id", p.getId());
				paramMap.put("name", p.getName());
				paramMap.put("payNum", p.getPayNum());
				paramMap.put("payMoney",p.getPayMoney());
				DataAccessor.execute("prePayStaticForSupplier.addRecordOfStatic"
						, paramMap, DataAccessor.OPERATION_TYPE.INSERT);
			}
	   }
}
