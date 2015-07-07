package com.brick.user.service;
/**
 * 用于验证用户的登陆
 * @author yangxuan
 */

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import com.brick.log.service.FilsoftLogger;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DateUtil;
import com.brick.util.DeptMapListener;
import com.brick.util.Validation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;

import flexjson.JSONSerializer;



public class UserService extends AService {		
	Log logger = LogFactory.getLog(UserService.class);

	private static final String LOGINCHECK = "用户名或口令错误!";
	
	private String LOGIN_TYPE;
	private String LDAP_URL;
	private String AD_DOMAIN_NAME;
	
	public void setLOGIN_TYPE(String lOGIN_TYPE) {
		LOGIN_TYPE = lOGIN_TYPE;
	}

	public void setLDAP_URL(String lDAP_URL) {
		LDAP_URL = lDAP_URL;
	}


	public void setAD_DOMAIN_NAME(String aD_DOMAIN_NAME) {
		AD_DOMAIN_NAME = aD_DOMAIN_NAME;
	}

	/** cookie默认保存360天，大概一年. */
	public static final int COOKIE_MAX_AGE = 360 * 24 * 60 * 60;
	/**
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void login(Context context){
		//add by Kyle 2011.11.29.
		logger.debug("UserService.login");
		List errList = context.errList;
		Map outputMap = new HashMap();
		Map rs = null;
		Map rsTemp = null; 
		boolean isAuthorised = false;
		// 设置session不活动时间为30分     
		context.request.getSession().setMaxInactiveInterval(60*90);
		
		Validation.validateString("用户名", "code", context,  false, -1, -1);
		Validation.validateString("口令", "password", context,  false,-1, -1);
		/*-------- data access --------*/		
		if(errList.isEmpty()){			
			logger.debug("@_@");
			processRememberMe( context);
			try{
				String ip = InetAddress.getLocalHost().getHostAddress();
				//String ip = "10.2.1.236";
				logger.debug("before run DB Query!");
				if ("DB".equals(LOGIN_TYPE)) {
					rs = (Map)DataAccessor.query("acl.login", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if (rs != null) {
						if ("10.2.1.236".equals(ip)) {
							logger.info("设备租赁系统登录。");
							rsTemp = (Map)DataAccessor.query("acl.loginForEqmt", context.contextMap, DataAccessor.RS_TYPE.MAP);
							if (rsTemp == null) {
								rs = null;
								errList.add("您未被授权设备租赁系统权限。");
							} else {
								isAuthorised = true;
							}
						} else if ("10.2.1.193".equals(ip)) {
							logger.info("小车租赁系统登录");
							rsTemp = (Map)DataAccessor.query("acl.loginForCar", context.contextMap, DataAccessor.RS_TYPE.MAP);
							if (rsTemp == null) {
								rs = null;
								errList.add("您未被授权乘用车租赁系统权限。");
							} else {
								isAuthorised = true;
							}
						} else {
							logger.info("测试服务器登录");
							isAuthorised = true;
						}
					}
				} else if ("LDAP".equals(LOGIN_TYPE)) {
					isAuthorised = this.loginByLDAP((String)context.contextMap.get("code"), (String)context.contextMap.get("myPassword"));
					if (isAuthorised) {
						rs = (Map)DataAccessor.query("acl.login", context.contextMap, DataAccessor.RS_TYPE.MAP);
					}
				}
				logger.debug("after DB Query!!!");
					if(isAuthorised){
						logger.debug("查到資料了!!!!");
						
						//加入部门配置Map 2013-3-4 ShenQi
						DeptMapListener.configDeptSqlCondition(DeptMapListener.sqlConditionMap,String.valueOf(rs.get("DEPT_ID")),context,null);
						
						context.request.getSession().setAttribute("FIRST_LOGIN",rs.get("FIRST_LOGIN"));
						context.request.getSession().setAttribute("s_employeeDecpId",  rs.get("COMPID"));
						context.request.getSession().setAttribute("s_code",  rs.get("SSN"));
						context.request.getSession().setAttribute("s_employeeId", rs.get("UUID"));
						context.request.getSession().setAttribute("s_employeeName", rs.get("NAME"));
						context.request.getSession().setAttribute("IP", context.getRequest().getRemoteAddr());
						context.request.getSession().setAttribute("EMAIL", rs.get("EMAIL"));
						context.request.getSession().setAttribute("UPPER_NAME", rs.get("UPPER_NAME"));
						context.request.getSession().setAttribute("UPPER_EMAIL", rs.get("UPPER_EMAIL"));
						context.request.getSession().setAttribute("JOB", rs.get("JOB"));
						/**Add By Michael 2011 10/09  获取当前的JdbcURL值,DB 的IP地址**/
						context.request.getSession().setAttribute("JdbcUrl", DataAccessor.JdbcUrl.substring(17,27));
						context.request.getSession().setAttribute("DBName", DataAccessor.JdbcUrl.substring(46,DataAccessor.JdbcUrl.length()));
						/**登录人的登录时间**/
						context.contextMap.put("id", rs.get("UUID"));
						
						//DataAccessor.execute("employee.loginEndTime", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
						//记录员工的登录信息 add by ShenQi see mantis 417
						context.contextMap.put("USER_ID",rs.get("UUID"));
						context.contextMap.put("USER_NAME",rs.get("NAME"));
						context.contextMap.put("IP",context.getRequest().getRemoteAddr());
						context.contextMap.put("ACTION","LOGIN");
						DataAccessor.execute("employee.insertLoginInfo",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
						
						/**写入日志 insert into log**/
						context.contextMap.put("logTime",new java.sql.Timestamp(new java.util.Date().getTime()));
						context.contextMap.put("userId", ((Number) rs.get("UUID")).intValue());
						context.contextMap.put("logData", rs.get("NAME")+":"+DateUtil.dateToFullStr(new java.util.Date())+" enter system ");
						context.contextMap.put("sysNode", "1");
						new FilsoftLogger().getLog().doLog(context);
					}else{
						logger.debug("查無此人!");
						errList.add(LOGINCHECK);
					} 
			}catch(Exception e){
				errList.add("系统错误!");				
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		/*-------- output --------*/
		outputMap.put("rs", rs);
		outputMap.put("errList", errList);
		
		//Output.jsonOutput(outputMap, context);
		if(errList.isEmpty()){
			if(context.request.getHeader("x-requested-with")!=null){//判断请求是否为AJAX
				Output.jsonFlageOutput(true, context);
				return;
			 }
			Output.jspSendRedirect(context, context.request.getContextPath()+"/servlet/defaultDispatcher?__action=assignmentPermission.assignmentPermission");
		}else{
			if(context.request.getHeader("x-requested-with")!=null){//判断请求是否为AJAX
				Output.jsonFlageOutput(false, context);
				return;
			 }
			Output.jspOutput(outputMap, context, "/sys/acl/login.jsp");
		}
	}
	

 /******************************
            * LDAP認證
            * @throws Exception
    ******************************/
   public boolean loginByLDAP(String account, String password) throws Exception {
	   boolean flag = false;
	   if (account.isEmpty() || password.isEmpty()){
           throw new Exception("認證失敗!");
	   }
       Hashtable<String,String > env = new Hashtable<String, String>();
       env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
       env.put(javax.naming.Context.PROVIDER_URL, LDAP_URL);
       env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
       env.put(javax.naming.Context.SECURITY_PRINCIPAL, account + AD_DOMAIN_NAME);
       env.put(javax.naming.Context.SECURITY_CREDENTIALS, password);
       LdapContext ctx = null;
       try {
           ctx = new InitialLdapContext(env, null);
           flag = true;
       } catch (javax.naming.AuthenticationException e) {
           throw new Exception("認證失敗!");
       } catch (javax.naming.CommunicationException e) {
           throw new Exception("找不到伺服器!");
       } catch (Exception e) {
           throw new Exception("發生未知的錯誤!");
       } finally {
           if (ctx != null) {
               try {
                   ctx.close();
               } catch (NamingException e) {
            	   e.printStackTrace();
               }
           }
       }
       return flag;
   }

	
	/**
	 * 处理rememberMe.
	 */
	public void processRememberMe(Context context) {
		if (context.request.getParameter("rememberMe") != null) {
			HttpServletResponse response = context.response;
			Cookie cookie = new Cookie("c_code", context.getContextMap().get("code")+"");
			cookie.setMaxAge(COOKIE_MAX_AGE);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}
	
	
	/**
	 * 用户修改密码
	 */
	@SuppressWarnings("unchecked")
	public void GETPassword(Context context){
	    List errorList = context.errList;
	    Map outputMap = new HashMap();
	    Map password = null;
	    
	    if(errorList.isEmpty()){
		try {
		    
		    
		    password = (Map)DataAccessor.query("acl.getPassword", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    
		    } catch (Exception e) {
			
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		    
		    }
	    }
	    
	    if(errorList.isEmpty()){
		outputMap.put("password", password);
		Output.jsonOutput(outputMap, context);
	    }
	    
	}
	
	
	@SuppressWarnings("unchecked")
	public void changePassword(Context context){
	    
	    List errorList = context.errList;
	    Map outputMap = new HashMap();
	    
	
	    
	    if(errorList.isEmpty()){
		try {
		    
			context.request.getSession().setAttribute("FIRST_LOGIN","N");
		    DataAccessor.execute("acl.changePassword", context.getContextMap(), DataAccessor.OPERATION_TYPE.UPDATE);
		    
		} catch (Exception e) {
		    
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errorList.add(e);
		}
	    }
	    
	    if(errorList.isEmpty()){
		outputMap.put("password",1 );
		Output.jsonOutput(outputMap, context);
	    }
	}
	
	
	
	/**
	 * 注销.
	 * 
	 * @return success
	 */ 
	public void loginOut(Context context) { 
		context.request.getSession().invalidate();
		
		//记录员工的登出信息 add by ShenQi see mantis 417
		context.contextMap.put("USER_ID",context.contextMap.get("s_employeeId"));
		context.contextMap.put("USER_NAME",context.contextMap.get("s_employeeName"));
		context.contextMap.put("IP",context.contextMap.get("IP"));
		context.contextMap.put("ACTION","LOGOUT");
		try {
			DataAccessor.execute("employee.insertLoginInfo",context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Output.jspSendRedirect(context, "sys/acl/login.jsp");	 
	}

	
	@Override
	protected void afterExecute(String action, Context context) {
		// TODO Auto-generated method stub
		System.out.println("after: " + action);
	}

	@Override
	protected boolean preExecute(String action, Context context) {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
