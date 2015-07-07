package com.brick.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.brick.common.dao.DeptMapDAO;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.deptCmpy.to.DeptCmpyTO;
import com.brick.service.entity.Context;

public final class DeptMapListener implements ServletContextListener {

	Log logger=LogFactory.getLog(DeptMapListener.class);
			
	public static Map<String,List<String>> departmentMap=new HashMap<String,List<String>>();
	public static Map<String,String> sqlConditionMap=new HashMap<String,String>();
	public static String isLock="Y";

	@Override
	public void contextDestroyed(ServletContextEvent arg) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg) {
		this.initDeptMap(arg);
	}
	
	//初始化部门Map
	private void initDeptMap(ServletContextEvent arg) {
		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(arg.getServletContext());
		DeptMapDAO deptMapDAO=(DeptMapDAO)appContext.getBean("deptMapDAO");
		
		//获得所有上级部门List
		List<DeptCmpyTO> rootList=deptMapDAO.getDeptId_1();
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
					departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
				} else {
					if(rootList.get(i).getUpperDeptId().equals(rootList.get(i-1).getUpperDeptId())) {
						depts.add(rootList.get(i).getDeptId());
						departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
					} else {
						depts=new ArrayList<String>();
						depts.add(rootList.get(i).getDeptId());
						departmentMap.put(rootList.get(i).getUpperDeptId(),depts);
					}
				}
			}
			
			Map<String,String> dept=new HashMap<String,String>();
			dept.put("depts",param.toString());
			
			while(true) {
				leafList=deptMapDAO.getDeptId_1(dept);
				
				//如果没有查到子部门,跳出while循环
				if(leafList.size()==0) {
					break;
				} else {
					param=new StringBuffer();
				}
				
				for(int i=0;i<leafList.size();i++) {
					
					//加入下属部门
					Iterator<Map.Entry<String,List<String>>> it=departmentMap.entrySet().iterator();
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
								List<String> newValue=departmentMap.get(entry.getKey());
								newValue.add(leafList.get(i).getDeptId());
								departmentMap.put(entry.getKey(),newValue);
								break;
							}
						}
					}
				}
				dept.put("depts",param.toString());
				
			}
			
			Iterator<Map.Entry<String,List<String>>> it=departmentMap.entrySet().iterator();
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
			
			logger.debug("初始化办事处结构:"+departmentMap);
			
			if(departmentMap.size()==0) {
				//如果没有配置过部门上下级关系,不发送mail
				return;
			}
			//加入邮件内容
			Map<DeptCmpyTO,List<DeptCmpyTO>> mailMap=new HashMap<DeptCmpyTO,List<DeptCmpyTO>>();
			
			Iterator<Map.Entry<String,List<String>>> itr=departmentMap.entrySet().iterator();
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
			MailUtilService mailUtilService=(MailUtilService)appContext.getBean("mailUtilService");
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			
			mailSettingTo.setEmailSubject("初始化部门配置邮件");
			
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
			
			try {
				InetAddress addr=InetAddress.getLocalHost();
				Runtime r=Runtime.getRuntime();  
				Properties props=System.getProperties();
				String ip=addr.getHostAddress();
				Map<String,String> map=System.getenv();  
				String userName=map.get("USERNAME");// 获取用户名  
				String computerName=map.get("COMPUTERNAME");// 获取计算机名  
				String userDomain=map.get("USERDOMAIN");// 获取计算机域名  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>计算机信息:    </span><br>");
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>用户名:    " + userName+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>计算机名:   " + computerName+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>计算机域名:  " + userDomain+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>本地ip地址: " + ip+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>本地主机名:  " + addr.getHostName()+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>JVM可以使用的总内存:    " + r.totalMemory()+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>JVM可以使用的剩余内存:   " + r.freeMemory()+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>JVM可以使用的处理器个数:  " + r.availableProcessors()+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的运行环境版本：    " + props.getProperty("java.version")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的安装路径：  " + props.getProperty("java.home")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的虚拟机规范版本：   " + props.getProperty("java.vm.specification.version")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的虚拟机实现版本：   " + props.getProperty("java.vm.version")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的虚拟机实现名称：   " + props.getProperty("java.vm.name")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java运行时环境规范版本：  " + props.getProperty("java.specification.version")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的类格式版本号：    " + props.getProperty("java.class.version")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>Java的类路径：   " + props.getProperty("java.class.path")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>加载库时搜索的路径列表：    " + props.getProperty("java.library.path")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>默认的临时文件路径：  " + props.getProperty("java.io.tmpdir")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>一个或多个扩展目录的路径：   " + props.getProperty("java.ext.dirs")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>操作系统的名称：    " + props.getProperty("os.name")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>操作系统的构架：    " + props.getProperty("os.arch")+"</span><br>");  
				mailContent.append("<span style='font-family: 微软雅黑;font-size: 12px;'>操作系统的版本：    " + props.getProperty("os.version")); 
			} catch (UnknownHostException e1) {
				logger.debug("获得IP地址出错");
			}
			
			mailSettingTo.setEmailContent(mailContent.toString());
			
			try {
				mailUtilService.sendMail(7,mailSettingTo);
				configDeptSqlCondition(departmentMap);
			} catch (Exception e) {
				logger.debug("发送初始化办事处邮件出错");
			}
	}
	
	public static void configDeptSqlCondition(Map<String,List<String>> param) {
		
		Iterator<Map.Entry<String,List<String>>> itr=param.entrySet().iterator();
		while(itr.hasNext()) {
			StringBuffer depts=new StringBuffer(); 
			Map.Entry<String,List<String>> entry=itr.next();
			for(int i=0;i<entry.getValue().size();i++) {
				if(i!=entry.getValue().size()-1) {
					depts.append("'").append(entry.getValue().get(i)).append("',");
				} else {
					depts.append("'").append(entry.getValue().get(i)).append("'");
				}
			}
			sqlConditionMap.put(entry.getKey(),depts.toString());
		}
	}
	
	public static void configDeptSqlCondition(Map<String,String> param,String deptId,Context context1,Map<String,Object> context2) {
		//加入部门配置映射Map 2013-3-5 ShenQi
		Iterator<Map.Entry<String,String>> itr=param.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry<String,String> entry=itr.next();
			if(deptId.equals(entry.getKey())) {
				context1.request.getSession().setAttribute("sqlConditioinMap",entry.getValue());
				if(context2!=null) {
					context2.put("sqlConditioinMap",entry.getValue());
				}
			}
		}
	}
}
