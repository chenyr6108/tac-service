package com.brick.deptCmpy.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.deptCmpy.service.DeptCmpyService;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.deptCmpy.to.TreeTO;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DeptMapListener;
import com.brick.util.web.HTMLUtil;

public class DeptCmpyCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(DeptCmpyCommand.class);
			
	private DeptCmpyService deptCmpyService;

	public DeptCmpyService getDeptCmpyService() {
		return deptCmpyService;
	}

	public void setDeptCmpyService(DeptCmpyService deptCmpyService) {
		this.deptCmpyService = deptCmpyService;
	}
	
	private com.brick.common.dao.DeptMapDAO deptMapDAO;
	public com.brick.common.dao.DeptMapDAO getDeptMapDAO() {
		return deptMapDAO;
	}
	public void setDeptMapDAO(com.brick.common.dao.DeptMapDAO deptMapDAO) {
		this.deptMapDAO = deptMapDAO;
	}

	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void queryDeptCmpy(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> param=new HashMap<String,Object>();
		
		List<DeptCmpyTO> companyList=null;
		List<DeptCmpyTO> deptList=null;
		List<DeptCmpyTO> deptCmpyList=null;
		List<Map<String,Object>> classList=null;
		
		param.put("companyId",context.contextMap.get("COMPANY_ID"));
		param.put("deptId",context.contextMap.get("DEPT_ID"));
		param.put("COMPANY_ID",context.contextMap.get("COMPANY_ID"));
		
		if(context.contextMap.get("COMPANY_ID")==null) {
			param.put("COMPANY_ID",1);
			param.put("companyId",1);
		}
		param.put("type","部门级别");
		try {
			//获得页面办事处查询条件
			companyList=this.deptCmpyService.getCompanyList();
			//获得页面部门查询条件
			deptList=this.deptCmpyService.getDeptList(param);
			
			//获得部门办事处查询结果
			deptCmpyList=this.deptCmpyService.queryDeptCmpy(param);
			
			param.put("dataType","部门级别");
			classList=this.deptCmpyService.queryDataDictionary(param);
		} catch (Exception e) {
			logger.debug("部门管理出错!");
			context.errList.add("部门管理出错!");
		}
		
		outputMap.put("COMPANY_ID",context.contextMap.get("COMPANY_ID"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		
		outputMap.put("deptList",deptList);
		outputMap.put("companyList",companyList);
		outputMap.put("resultList",deptCmpyList);
		outputMap.put("classList",classList);
		outputMap.put("isLock",DeptMapListener.isLock);
		
		if(context.errList.isEmpty()){
			Output.jspOutput(outputMap,context,"/deptCmpy/deptCmpyManage.jsp");
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void batchUpdate(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		String [] ids=(String[])HTMLUtil.getParameterValues(context.request,"ID","");
		
		Map<String,Object> param=new HashMap<String,Object>();
		
		param.put("upperDeptId",context.contextMap.get("UPPER_DEPT_ID"));
		param.put("deptMgr",context.contextMap.get("DEPT_MGR_ID"));
		param.put("classId",context.contextMap.get("DEPT_CLASS_ID"));
		
		StringBuffer id=new StringBuffer();
		
		for(int i=0;i<ids.length;i++) {
			if(i!=ids.length-1) {
				id.append("'").append(ids[i]).append("',");
			} else {
				id.append("'").append(ids[i]).append("'");
			}
		}
		param.put("ids",id);
		try {
			this.deptCmpyService.batchUpdateDept(param);
		} catch (Exception e) {
			logger.debug("批量更新部门出错!");
			context.errList.add("批量更新部门出错!");
		}
		
		if(context.errList.isEmpty()){
			this.queryDeptCmpy(context);
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void addDept(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			this.deptCmpyService.addDept(context.contextMap);
		} catch (Exception e) {
			logger.debug("添加部门出错!");
			context.errList.add("添加部门出错!");
		}
		
		if(context.errList.isEmpty()){
			this.queryDeptCmpy(context);
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	
	public void updateDeptMap(Context context) {
		
		DeptMapListener.departmentMap.clear();
		//获得所有上级部门List
		List<DeptCmpyTO> rootList=this.deptMapDAO.getDeptId_1();
		List<DeptCmpyTO> leafList=null;
		List<String> depts=new ArrayList<String>();
		
		StringBuffer param=new StringBuffer();
			for(int i=0;i<rootList.size();i++) {
				if(i!=rootList.size()-1) {
					param.append("'"+rootList.get(i).getDeptId()+"',");
				} else {
					param.append("'"+rootList.get(i).getDeptId()+"'");
				}
				
				if(i==0) {
					depts.add(rootList.get(i).getDeptId());
					DeptMapListener.departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
				} else {
					if(rootList.get(i).getUpperDeptId().equals(rootList.get(i-1).getUpperDeptId())) {
						depts.add(rootList.get(i).getDeptId());
						DeptMapListener.departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
					} else {
						depts=new ArrayList<String>();
						depts.add(rootList.get(i).getDeptId());
						DeptMapListener.departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
					}
				}
			}
			
			Map<String,String> dept=new HashMap<String,String>();
			dept.put("depts",param.toString());
			
			while(true) {
				leafList=this.deptMapDAO.getDeptId_1(dept);
				
				//如果没有查到子部门,跳出while循环
				if(leafList.size()==0) {
					break;
				} else {
					param=new StringBuffer();
				}
				
				for(int i=0;i<leafList.size();i++) {
					
					//加入下属部门
					Iterator<Map.Entry<String,List<String>>> it=DeptMapListener.departmentMap.entrySet().iterator();
					List<String> value=null;
					
					if(i!=leafList.size()-1) {
						param.append("'"+leafList.get(i).getDeptId()+"',");
					} else {
						param.append("'"+leafList.get(i).getDeptId()+"'");
					}
					
					while(it.hasNext()) {
						Map.Entry<String,List<String>> entry=it.next();
						value=entry.getValue();
						for(int j=0;j<value.size();j++) {
							if(leafList.get(i).getUpperDeptId().equals(value.get(j))) {
								List<String> newValue=DeptMapListener.departmentMap.get(entry.getKey());
								newValue.add(leafList.get(i).getDeptId());
								DeptMapListener.departmentMap.put(entry.getKey(),newValue);
								break;
							}
						}
					}
				}
				dept.put("depts",param.toString());
				
			}
			
			Iterator<Map.Entry<String,List<String>>> it=DeptMapListener.departmentMap.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String,List<String>> entry=it.next();
			    for(int i=0;i<entry.getValue().size()-1;i++) {
			       for(int j=entry.getValue().size()-1;j>i;j--) {
			         if(entry.getValue().get(j).equals(entry.getValue().get(i))) {
			    	     entry.getValue().remove(j);
			         } 
			       } 
			    } 
			}
			
			//加入邮件内容
			Map<DeptCmpyTO,List<DeptCmpyTO>> mailMap=new HashMap<DeptCmpyTO,List<DeptCmpyTO>>();
			
			Iterator<Map.Entry<String,List<String>>> itr=DeptMapListener.departmentMap.entrySet().iterator();
			while(itr.hasNext()) {
				List<DeptCmpyTO> toList=new ArrayList<DeptCmpyTO>();
				Map.Entry<String,List<String>> entry=itr.next();
				for(int j=0;j<entry.getValue().size();j++) {
					DeptCmpyTO to=new DeptCmpyTO();
					to.setDeptId(entry.getValue().get(j));
					toList.add(to);
				}
				
				DeptCmpyTO keyTo=new DeptCmpyTO();
				keyTo.setDeptId(entry.getKey());
				mailMap.put(keyTo,toList);
			}
			
			List<DeptCmpyTO> deptList=deptMapDAO.getDeptId_2();
			for(int i=0;i<deptList.size();i++) {
				Iterator<Map.Entry<DeptCmpyTO,List<DeptCmpyTO>>> itor=mailMap.entrySet().iterator();
				while(itor.hasNext()) {
					Map.Entry<DeptCmpyTO,List<DeptCmpyTO>> entry=itor.next();
					for(int j=0;j<entry.getValue().size();j++) {
						if(deptList.get(i).getDeptId().equals(entry.getValue().get(j).getDeptId())) {
							entry.getValue().get(j).setDeptName(deptList.get(i).getDeptName());
							entry.getValue().get(j).setCompanyName(deptList.get(i).getCompanyName());
						}
					}
					
					if(deptList.get(i).getDeptId().equals(entry.getKey().getDeptId())) {
						entry.getKey().setDeptName(deptList.get(i).getDeptName());
						entry.getKey().setCompanyName(deptList.get(i).getCompanyName());
						mailMap.put(entry.getKey(),entry.getValue());
					}
				}
			}
			
			//发送邮件
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			mailSettingTo.setEmailSubject("更新部门配置邮件");
			
			StringBuffer mailContent=new StringBuffer();
			
			Iterator<Map.Entry<DeptCmpyTO,List<DeptCmpyTO>>> iterator=mailMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<DeptCmpyTO,List<DeptCmpyTO>> entry=iterator.next();
				mailContent.append("[<b style='font-family: 微软雅黑'>上级部门</b>:<span style='font-family: 微软雅黑;font-size: 12px;'>"+entry.getKey().getCompanyName()+"-"+entry.getKey().getDeptName()+"("+entry.getKey().getDeptId()+")</span>]&nbsp;&nbsp;&nbsp;&nbsp;[<b style='font-family: 微软雅黑'>下级部门</b>:");
				for(int i=0;i<entry.getValue().size();i++) {
					if(i!=entry.getValue().size()-1) {
						mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>"+entry.getValue().get(i).getCompanyName()+"-"+entry.getValue().get(i).getDeptName()+"("+entry.getValue().get(i).getDeptId()+"),</span>");
					} else {
						mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>"+entry.getValue().get(i).getCompanyName()+"-"+entry.getValue().get(i).getDeptName()+"("+entry.getValue().get(i).getDeptId()+")</span>]<br>");
					}
				}
			}
			mailSettingTo.setEmailContent(mailContent.toString());
			
			DeptMapListener.isLock="Y";
			try {
				mailUtilService.sendMail(7,mailSettingTo);
				
				DeptMapListener.configDeptSqlCondition(DeptMapListener.departmentMap);
				
			} catch (Exception e) {
				logger.debug("发送初始化办事处邮件出错");
			}
			
			this.queryDeptCmpy(context);
	}
	
	public void showTree(Context context) {
		
/*		Iterator<Map.Entry<String,List<String>>> itor=DeptMapListener.departmentMap.entrySet().iterator();
		
		List<TreeTO> treeList=new ArrayList<TreeTO>();
		
		while(itor.hasNext()) {
			Map.Entry<String,List<String>> entry=itor.next();
			Iterator<Map.Entry<String,List<String>>> itor1=DeptMapListener.departmentMap.entrySet().iterator();
			boolean isRootDeptId=true;
			while(itor1.hasNext()) {
				Map.Entry<String,List<String>> entry1=itor1.next();
				for(int i=0;i<entry1.getValue().size();i++) {
					if(entry.getKey().equals(entry1.getValue().get(i))) {
						isRootDeptId=false;
						break;
					}
				}
			}
			if(isRootDeptId) {
				TreeTO rootTreeTo=new TreeTO();
				rootTreeTo.setDeptId(entry.getKey());
				treeList.add(rootTreeTo);
				isRootDeptId=true;
			}
		}*/
	}
}
