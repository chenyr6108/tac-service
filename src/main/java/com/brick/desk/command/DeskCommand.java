package com.brick.desk.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.desk.service.DeskService;
import com.brick.desk.to.DeskTO;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;

public class DeskCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(DeskCommand.class);
			
	private DeskService deskService;

	public DeskService getDeskService() {
		return deskService;
	}

	public void setDeskService(DeskService deskService) {
		this.deskService = deskService;
	}
	
	public void query(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<DeskTO> resultList=this.deskService.getPermissionGroup();
		
		List<DeskTO> permissionList=this.deskService.getPermissionList(context);
		
		List<DeskTO> userList=this.deskService.getUserList();
		
		String sign="";
		String comma="";
		
		for(int i=0;i<resultList.size();i++) {
			int count1=1;
			int count2=1;
			for(int j=0;j<permissionList.size();j++) {
				if(resultList.get(i).getCode().equals(permissionList.get(j).getCode())) {
					if(resultList.get(i).getAuth()!=null) {
						if(count1%5==0) {
							sign="<br>";
						} else {
							sign="";
						}
						if((count1-1)%5==0) {
							comma="";
						} else {
							comma=",";
						}
						resultList.get(i).setAuth(resultList.get(i).getAuth()+comma+permissionList.get(j).getAuth()+sign);
					} else {
						resultList.get(i).setAuth(permissionList.get(j).getAuth());
					}
					count1++;
				}
			}
			for(int j=0;j<userList.size();j++) {
				if(resultList.get(i).getCode().equals(userList.get(j).getCode())) {
					if(resultList.get(i).getUserName()!=null) {
						if(count2%5==0) {
							sign="<br>";
						} else {
							sign="";
						}
						if((count2-1)%5==0) {
							comma="";
						} else {
							comma=",";
						}
						resultList.get(i).setUserName(resultList.get(i).getUserName()+comma+userList.get(j).getUserName()+sign);
					} else {
						resultList.get(i).setUserName(userList.get(j).getUserName());
					}
					count2++;
				}
			}
		}
		
		outputMap.put("resultList",resultList);
		
		if(context.errList.isEmpty()){
			outputMap.put("isExist",context.contextMap.get("isExist"));
			Output.jspOutput(outputMap,context,"/desk/deskQuery.jsp");
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void saveDeskGroup(Context context) {
		
		//插入组别前查看code是否已经使用,如果已经使用则不能插入返回跳转页面
		if(this.deskService.checkIsExist(context)>=1) {
			context.contextMap.put("isExist","Y");
			this.query(context);
			return;
		}
		
		this.deskService.insertPermissionGroup(context);
		context.contextMap.put("isExist","N");
		this.query(context);
	}
	
	public void deleteDeskGroup(Context context) {
		
		this.deskService.deletePermissionGroup(context);
		context.contextMap.put("isExist","N");
		this.query(context);
	}
	
	public void getDeskAuthList(Context context) {
		
		List<DeskTO> permissionList=this.deskService.getPermissionMap(context);
		
		context.contextMap.put("dataType","欢迎页面模块");
		List<Map<String,String>> resultList=this.deskService.getDeskAuthList(context);
		
		for(int i=0;i<resultList.size();i++) {
			for(int j=0;j<permissionList.size();j++) {
				if(resultList.get(i).get("CODE").equals(permissionList.get(j).getCode())) {
					resultList.get(i).put("CHECKED","Y");
					break;
				}
			}
		}
		
		Output.jsonArrayListOutput(resultList,context);
	}
	
	public void saveDeskAuth(Context context) {
		
		this.deskService.savePermissionMap(context);
		
		context.contextMap.put("isExist","N");
		this.query(context);
	}
	
	public void saveDeskUser(Context context) {
		
		this.deskService.savePermissionUser(context);
		
		context.contextMap.put("isExist","N");
		this.query(context);
	}
	
	public void getPermissionUserList(Context context) {
		
		List<Map> resultList=this.deskService.getPermissionUserList(context);
		
		Output.jsonArrayOutput(resultList,context);
	}
}
