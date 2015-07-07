package com.brick.completedFile.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.completedFile.biz.CompletedFileBiz;
import com.brick.log.service.LogPrint;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class CompletedFileControl extends BaseCommand{
	Log logger=LogFactory.getLog(CompletedFileControl.class);
	
	private  CompletedFileBiz completedFileBiz;
	public CompletedFileBiz getCompletedFileBiz() {
		return completedFileBiz;
	}
	public void setCompletedFileBiz(CompletedFileBiz completedFileBiz) {
		this.completedFileBiz = completedFileBiz;
	}
	//可结清案件列表
	@SuppressWarnings("unchecked")
	public void getCompletedFileList(Context context){
		
		List errList = context.errList;
		List<Map<String,Object>> dateList=null;
		List officeList=null;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
			try { 
				//01表示可结清
				String fileType ="01";
				//时间列表
				dateList=this.completedFileBiz.getDateList(fileType);
				//办事处列表
				officeList=this.baseService.queryForList("customerVisit.getDeptList",outputMap);
				//（默认取dateList最大值）
				if(dateList.size()>0){
					context.contextMap.put("getTime", dateList.get(0).get("LOGDATE"));
				}
				//某一日所有记录
				dw = baseService.queryForListWithPaging("settleManage.getCompletedFileLog", context.contextMap,"DIFF_DAY", ORDER_TYPE.DESC);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		outputMap.put("dw", dw);
		outputMap.put("dateList", dateList);
		outputMap.put("officeList", officeList);
		outputMap.put("CONTENT",context.contextMap.get("CONTENT"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("DIFF",context.contextMap.get("DIFF"));
		outputMap.put("DATE",context.contextMap.get("DATE"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/completedFile/completedFile.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}

}
