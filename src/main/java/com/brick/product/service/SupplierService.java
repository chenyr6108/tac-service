package com.brick.product.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.baseManage.service.BusinessLog;
import com.brick.batchjob.to.CustomerCaseTo;
import com.brick.coderule.service.CodeRule;
import com.brick.customerVisit.service.CustomerVisitService;
import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.supplier.to.LogMsgTo;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.brick.util.web.JsonUtils;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.brick.log.service.LogPrint;
/**
 * @author yangxuan
 * @version Created：2010-4-19 下午03:20:26
 *
 */

public class SupplierService extends BaseCommand{
	Log logger = LogFactory.getLog(SupplierService.class);
	private static final String RESUTL = "<script type=\"text/javascript\">alert(\"操作成功!\")</script>";
	private static final String ERROR = "<script type=\"text/javascript\">alert(\"操作失败!\")</script>";
	private List<LogMsgTo> msgs;
	
	private CustomerVisitService customerVisitService;
	
	public CustomerVisitService getCustomerVisitService() {
		return customerVisitService;
	}
	public void setCustomerVisitService(CustomerVisitService customerVisitService) {
		this.customerVisitService = customerVisitService;
	}
	/**得到所有的供应商  get all supplier
	 * @throws Exception **/
	@SuppressWarnings("unchecked")
	public void findAllSupplier(Context context) throws Exception {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		PagingInfo<Object> dw = null;
		List<Map<String,Object>> areaList=null;
		//view是控制页面是否显示查看的链接
		boolean view=false;
		//add是控制页面是否显示添加的按钮
		boolean add=false;
		//modify是控制页面是否显示修改,资料的链接
		boolean modify=false;
		//delete是控制页面是否显示删除的链接
		boolean delete=false;
		//info是控制页面是否显示资料的链接
		boolean info=false;
		//display是控制页面是否显示操作的列
		boolean display=true;
		if (errorList.isEmpty()) {
			try {
				if (StringUtils.isEmpty(context.contextMap.get("SUPP_TYPE"))) {
					context.contextMap.put("SUPP_TYPE", "");
				}
				String searchMonth = (String) context.contextMap.get("searchMonth");
				if (!StringUtils.isEmpty(searchMonth)) {
					Pattern pattern = Pattern.compile("^\\d+$");
					Matcher matcher = pattern.matcher(searchMonth);
					if (!matcher.matches()) {
						outputMap.put("msg", "月数只能是整数。");
						Output.jspOutput(outputMap, context, "/product/supplier/supplierList.jsp");
						return;
					}
				}
				dw = baseService.queryForListWithPaging("supplier.query", context.contextMap, "NAME");
				
				//获得区域
				context.contextMap.put("dataType", "区域");
				areaList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				//通过emplId获得用户的ResourceId.(create by ShenQi,2012-2-27)
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
			    /*ResourceId               Permission  
		         *167                                                           查看
			     *168                                                           添加
			     *169                                                           修改
			     *170                                                           删除
			     *171                                                           资料
			     * */
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					//below is hard code for ResourceId,we will enhance it in the future
					if("167".equals(resourceIdList.get(i))) {
						view=true;
					} else if("168".equals(resourceIdList.get(i))) {
						add=true;
					} else if("169".equals(resourceIdList.get(i))) {
						modify=true;
					} else if("170".equals(resourceIdList.get(i))) {
						delete=true;
					} else if("171".equals(resourceIdList.get(i))) {
						info=true;
					}
				}
				if(!view&&!modify&&!delete&&!info) {
					display=false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
			}	
		} 
		if (errorList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("searchValue", context.contextMap.get("searchValue"));
			
			//设置页面的权限控制
			outputMap.put("view", view);
			outputMap.put("add", add);
			outputMap.put("modify", modify);
			outputMap.put("delete", delete);
			outputMap.put("info", info);
			outputMap.put("display", display);
			
			outputMap.put("areaList", areaList);
			outputMap.put("LICENCE_ADDRESS", context.contextMap.get("LICENCE_ADDRESS"));
			outputMap.put("SUPP_TYPE", context.contextMap.get("SUPP_TYPE"));
			outputMap.put("SUPP_LEVEL", context.contextMap.get("SUPP_LEVEL"));
			outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
			
			List<Map<String,Object>> intentList=null;
			List<Map<String,String>> importantRecordList=null;
			Map paramMap = new HashMap();
			paramMap.put("decp_id", 2);
			List deptList = (List) DataAccessor.query("employee.getCompany", paramMap, RS_TYPE.LIST);
			try {
				Map<String,String> param1=new HashMap<String,String>();
				param1.put("dataType","拜访目的");
				intentList=this.customerVisitService.queryDataDictionary(param1);

				param1.put("dataType","重点记录");
				param1.put("shortName","1");
				importantRecordList=this.customerVisitService.queryDataDictionary1(param1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			outputMap.put("date", DateUtil.dateToString(Calendar.getInstance().getTime(), "yyyy-MM-dd"));
			outputMap.put("intentList", intentList);
			outputMap.put("importRecordList", importantRecordList);
			outputMap.put("deptList", deptList);
			outputMap.put("deptId", context.contextMap.get("deptId"));
			outputMap.put("searchMonth", context.contextMap.get("searchMonth"));
			
			Output.jspOutput(outputMap, context, "/product/supplier/supplierList.jsp");
		}else{
			
		}		
	}	
	
	public void getDecp(Context context) throws Exception{
		Map paramMap = new HashMap();
		paramMap.put("decp_id", 2);
		List deptList = (List) DataAccessor.query("employee.getCompany", paramMap, RS_TYPE.LIST);
		Output.jsonArrayOutput(deptList, context);
	}
	/**创建一个代理商对象 create the new supplier**/
	@SuppressWarnings({ "unchecked", "static-access" })
	public void createSupplier(Context context) {
		List errorList = context.errList;
		Map outputMap = context.contextMap;
		SqlMapClient sqlMapper=null;
		CodeRule coder = new CodeRule();
//		List fileItems =(List)context.contextMap.get("uploadList");	
		int id = 0 ;
		if (errorList.isEmpty()) {
			try {
				
			    	if( context.contextMap.get("Type") == null || "".equals(context.contextMap.get("Type")+"")){
			    	 
			    	    	context.contextMap.put("Type",null);
			    	}else{
			    	    	context.contextMap.put("Type", Integer.parseInt((String)context.contextMap.get("Type")));
			    	    
			    	}
			    	if(context.contextMap.get("Founded_date") == null || "".equals(context.contextMap.get("Founded_date")+"")){
			    	   
			    	    	context.contextMap.put("Founded_date", null);

			    	}else{
			    	    	context.contextMap.put("Founded_date", new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String)context.contextMap.get("Founded_date")));
			    	    
			    	}
			    	if(  context.contextMap.get("Registered_capital") == null || "".equals(context.contextMap.get("Registered_capital")+"")){
			    	    
			    	 context.contextMap.put("Registered_capital", null);
			    	}else{
			    		//Modify by Michael 纠正收入小数时 报错
			    	    //context.contextMap.put("Registered_capital",Integer.parseInt((String)context.contextMap.get("Registered_capital")));
			    		context.contextMap.put("Registered_capital",Float.parseFloat((String)context.contextMap.get("Registered_capital")));
			    	}
			    	if(context.contextMap.get("Income_capital") == null || "".equals(context.contextMap.get("Income_capital")+"")){
			    	    
			    	    	context.contextMap.put("Income_capital", null);
			    	}else{
			    		//Modify by Michael 纠正收入小数时 报错
			    	    //	context.contextMap.put("Income_capital", Integer.parseInt((String)context.contextMap.get("Income_capital")));
			    		context.contextMap.put("Income_capital", Float.parseFloat((String)context.contextMap.get("Income_capital")));
			    	}
			    	if(context.contextMap.get("Validity_Period") == null || "".equals(context.contextMap.get("Validity_Period")+"")){
		    	    	context.contextMap.put("Validity_Period",  null);
			    	}else{
			    	   context.contextMap.put("Validity_Period", Integer.parseInt((String)context.contextMap.get("Validity_Period")));
			    	}
			    	if(context.contextMap.get("Credit_type") == null || "".equals(context.contextMap.get("Credit_type")+"")){
			    	    
			    	    context.contextMap.put("Credit_type", null);

			    	}else{
			    	    
			    	    context.contextMap.put("Credit_type", Integer.parseInt((String)context.contextMap.get("Credit_type")));
			    	}
				//context.contextMap.put("Create_date", new java.util.Date());
				//context.contextMap.put("Modify_date", new java.util.Date());
			    	
			    	String code = coder.generateSupplierCode(context);
			    	
				context.contextMap.put("code", code);
			    	
				id = (Integer)DataAccessor.execute("supplier.create", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
				//sqlMapper.insert("supplier.create", context.contextMap);
				Map endID=(Map) DataAccessor.query("supplier.queryEndId", context.contextMap, RS_TYPE.MAP);
				
				
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				//公司基本账户
				sqlMapper.update("supplier.deleteSuppliesBankAccountById", context.contextMap);
				Map baseBankAccount=new HashMap();
				String B_PCCBA_ID=(String)context.contextMap.get("B_PCCBA_ID");
				baseBankAccount.put("BANK_NAME", context.contextMap.get("B_BANK_NAME"));
				baseBankAccount.put("BANK_ACCOUNT", context.contextMap.get("B_BANK_ACCOUNT"));
				baseBankAccount.put("STATE","0");
				baseBankAccount.put("SUPL_ID", endID.get("ID"));
				baseBankAccount.put("PCCBA_ID", B_PCCBA_ID);
				
				sqlMapper.insert("supplier.createSupplierBankAccount", baseBankAccount);
				
				
				//公司其他账户
				sqlMapper.delete("supplier.deleteSuppliersBankAccountBySuplId", context.contextMap);
				if(context.request.getParameterValues("BANK_NAME")!=null){
					String[] BANK_NAME=HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
					String[] BANK_ACCOUNT=HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
					for (int i = 0; i < BANK_NAME.length; i++) {
						Map bankAccount=new HashMap();
						bankAccount.put("BANK_NAME", BANK_NAME[i]);
						bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
						bankAccount.put("STATE","1");
						bankAccount.put("SUPL_ID", endID.get("ID"));
						sqlMapper.insert("supplier.createSupplierBankAccount", bankAccount);
					}
				}
				
//				//添加供应商图片
//				context.contextMap.put("SUPPLIER_ID", id) ;
//				if(fileItems != null && fileItems.size() > 0){
//					for (int i = 0 ;i < fileItems.size() ;i++ ) {
//						FileItem fileItem = (FileItem) fileItems.get(i);
//						InputStream in =fileItem.getInputStream();		
//						if(!fileItem.getName().equals("")){
//							saveFileToDisk(context,fileItem,sqlMapper,context.contextMap.get("imagesName"+i).toString());
//						}
//					}
//				}
				
		    	sqlMapper.commitTransaction();
				
				
				outputMap.put("msg", RESUTL);
			}catch (Exception e) {
				e.printStackTrace();
				outputMap.put("msg", ERROR);
				LogPrint.getLogStackTrace(e, logger);
				errorList.add(e);
		    }
			finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errorList.add(e);
				} 
			}
		}
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		if (errorList.isEmpty()) {
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=supplier.findAllSupplier&isSalesDesk=Y");
			} else {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=supplier.findAllSupplier");
			}
		}
	}
		
	/**根据ID查找一个代理商的信息**/
	@SuppressWarnings("unchecked")
	public void findSupplierById(Context context ) {
		List errList = context.errList;
		List suplBankAccount=null;
		Map outputMap = new HashMap();
		Map suplGrantMoneyMap=null;
		Map totalPayMoneyMap=null;
		String id=context.request.getParameter("id");
		List supplLinkman=null;
		List supplLinkRecord=null;
		List creditList=null;
		if (errList.isEmpty()) {
			try {
				//获得货币类型 add by ShenQi
				context.contextMap.put("dataType","货币类型");
				outputMap.put("moneyType",(List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
				
				outputMap.put("rs",(Map) DataAccessor.query("supplier.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP));
				context.contextMap.put("supplier_id",context.contextMap.get("id").toString());
				context.contextMap.put("suppl_id",context.contextMap.get("id").toString());
				suplBankAccount=(List) DataAccessor.query(
						"supplier.getSupplierBankAccountByCreditId", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				//查询 有多少关联多少案件
				context.contextMap.put("checkType", "SUPPL") ;
				context.contextMap.put("ID", context.contextMap.get("id").toString()) ;
				outputMap.put("count", DataAccessor.query("suplEquipment.checkCreditExist", context.contextMap, RS_TYPE.OBJECT)) ;
				//查询 有多少关联多少案件  结束
				outputMap.put("suplBankAccount", suplBankAccount);
				outputMap.put("supplierImage",DataAccessor.query("SupplierImage.querySupplierImageBySupplierId", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
				
				outputMap.put("creditLine", baseService.getSuplCreditLine((String) context.contextMap.get("supplier_id")));
				
				/*//抓取供应商交机前授信额度
				suplGrantMoneyMap=(Map) DataAccessor.query("supplier.getSuplGrantMoneyBySuplID", context.contextMap,DataAccessor.RS_TYPE.MAP);
				
				outputMap.put("LIEN_GRANT_PRICE", suplGrantMoneyMap.get("LIEN_GRANT_PRICE"));
				outputMap.put("REPURCH_GRANT_PRICE", suplGrantMoneyMap.get("REPURCH_GRANT_PRICE"));
				outputMap.put("LIEN_LAST_PRICE",SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(context.contextMap.get("id").toString()))==null?0:SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(context.contextMap.get("id").toString())));
				outputMap.put("REPURCH_LAST_PRICE",SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(context.contextMap.get("id").toString()))==null?0:SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(context.contextMap.get("id").toString())));
				
				if (suplGrantMoneyMap!=null){
					outputMap.put("advance_grant", suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"));

					totalPayMoneyMap=(Map) DataAccessor.query("rentContract.getTotalPayMoneyBySupl", context.contextMap,DataAccessor.RS_TYPE.MAP);
					if (totalPayMoneyMap!=null){
						//判断授信的交机前拨款额度是否大于已用额度
						if (new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).compareTo(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON"))))==-1){
							outputMap.put("advance_machine", 0);
						}else{
							outputMap.put("advance_machine", new BigDecimal(String.valueOf(suplGrantMoneyMap.get("ADVANCEMACHINE_GRANT_PRICE"))).subtract(new BigDecimal(String.valueOf(totalPayMoneyMap.get("TOTAL_APPRORIATEMON")))));
						}
					}
				}else{
					outputMap.put("advance_machine", 0);
					outputMap.put("advance_grant", 0);
				}*/
				List provinces=null;
				// 取省份
				provinces = (List) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("provinces", provinces);
				
				// 取所有市
				List citys=null;
				citys = (List) DataAccessor.query("area.getAllCitys", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				// 取所有地区
				List areas=null;
				areas = (List) DataAccessor.query("area.getAllAreas", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("areas", areas);
				supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
				supplLinkRecord=(List)DataAccessor.query("supplier.querySupplLinkRecord", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
				outputMap.put("supplLinkman", supplLinkman);
				outputMap.put("supplLinkRecord", supplLinkRecord);
				
				//该供应商所有报告
				creditList = (List) DataAccessor.query( "supplier.querySupplCredit", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("creditList", creditList);
				
				//该供应商所有操作日志
				context.contextMap.put("OPERATOR_TABLE_NAME", "T_SUPL_SUPPLIER");
				List logs = (List) DataAccessor.query( "supplier.queryOperationLogs", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("logs", logs);
				
			}catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		
		List<Map<String,Object>> intentList=null;
		List<Map<String,String>> importantRecordList=null;
		
		try {
			Map<String,String> param1=new HashMap<String,String>();
			param1.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param1);

			param1.put("dataType","重点记录");
			param1.put("shortName","1");
			importantRecordList=this.customerVisitService.queryDataDictionary1(param1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("date", DateUtil.dateToString(Calendar.getInstance().getTime(), "yyyy-MM-dd"));
		outputMap.put("intentList", intentList);
		outputMap.put("importRecordList", importantRecordList);
		if (errList.isEmpty()) {
			if (((String)context.contextMap.get("flag")).equals("1"))
			{
				outputMap.put("showFlag",0);
				outputMap.put("supplier_id", id);
				Output.jspOutput(outputMap, context, "/product/supplier/allSupplierReports.jsp");
			}
			else {Output.jspOutput(outputMap, context, "/product/supplier/supplierUpdate.jsp");}
		}
	}
	
	/**删除代理商信息**/
	@SuppressWarnings({ "unchecked" })
	public void deleteSupplierById(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				DataAccessor.execute("supplier.delteById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				outputMap.put("msg",RESUTL);
			} catch (Exception e) {
				e.printStackTrace();
				outputMap.put("msg",ERROR);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
		    }
		}
		if (errList.isEmpty()) {
			
			outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=supplier.findAllSupplier&isSalesDesk=Y");
			} else {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=supplier.findAllSupplier");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updateSupplier(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		SqlMapClient sqlMapper=null;
		if (errList.isEmpty()) {
			try {
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				if("".equals(context.contextMap.get("Type"))  | context.contextMap.get("Type") == null){
				    	 
			    	    	context.contextMap.put("Type",null);
			    	}else{
			    	    	context.contextMap.put("Type", Integer.parseInt((String)context.contextMap.get("Type")));
			    	    
			    	}
			    	if("".equals(context.contextMap.get("Founded_date")) |  context.contextMap.get("Founded_date") == null){
			    	   
			    	    	context.contextMap.put("Founded_date", null);

			    	}else{
			    	    	context.contextMap.put("Founded_date", new java.text.SimpleDateFormat("yyyy-MM-dd").parse((String)context.contextMap.get("Founded_date")));
			    	    
			    	}
			    	if("".equals(context.contextMap.get("Registered_capital")) |  context.contextMap.get("Registered_capital") == null){
			    	    
			    	 context.contextMap.put("Registered_capital", null);
			    	}else{
			    		//Modify by Michael 纠正收入小数时 报错   
			    		//context.contextMap.put("Registered_capital",Integer.parseInt((String)context.contextMap.get("Registered_capital")));
			    	    context.contextMap.put("Registered_capital",Float.parseFloat((String)context.contextMap.get("Registered_capital")));
			    	}
			    	if("".equals(context.contextMap.get("Income_capital")) |  context.contextMap.get("Income_capital") == null){
			    	    
		    	    	context.contextMap.put("Income_capital", null);
			    	}else{
			    		//Modify by Michael 纠正收入小数时 报错   
			    		//context.contextMap.put("Income_capital", Integer.parseInt((String)context.contextMap.get("Income_capital")));
			    	    context.contextMap.put("Income_capital", Float.parseFloat((String)context.contextMap.get("Income_capital")));
			    	}
			    	if("".equals(context.contextMap.get("Validity_Period")) |  context.contextMap.get("Validity_Period") == null){
			    	    	
			    	    	context.contextMap.put("Validity_Period",  null);

			    	}else{
			    	    context.contextMap.put("Validity_Period", Integer.parseInt((String)context.contextMap.get("Validity_Period")));
			    	    
			    	}
			    	if("".equals(context.contextMap.get("Credit_type")) |  context.contextMap.get("Credit_type") == null){
			    	    
			    	    context.contextMap.put("Credit_type", null);

			    	}else{
			    	    context.contextMap.put("Credit_type", Integer.parseInt((String)context.contextMap.get("Credit_type")));
			    	}
			    	
			    	//供应商维护日志记录 yangliu added 2013/11/19
			    	msgs = new ArrayList<LogMsgTo>();
			    	String[] bankName = {};
			    	String[] bankAccount = {};
			    	if(context.request.getParameterValues("BANK_NAME")!=null){
			    		bankName = HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
			    		bankAccount = HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
			    	}
			    	context.contextMap.put("bankName", bankName);
			    	context.contextMap.put("bankAccount", bankAccount);
			    	this.makeSupplierUpdateLogMessage(context, sqlMapper);
			    	String operationMsgs = JsonUtils.list2json(msgs);
			    	
			    	//如果operationMsgs为空，则其内容为[],判断length>2则为非空
			    	if(operationMsgs.length() > 2){
				    	Map operationLogs = new HashMap();
				    	operationLogs.put("OPERATOR_TABLE_ID", context.contextMap.get("id"));
				    	operationLogs.put("OPERATOR_TABLE_NAME", "T_SUPL_SUPPLIER");
				    	operationLogs.put("OPERATION_MESSAGE", operationMsgs);
				    	operationLogs.put("OPERATOR_ID", context.contextMap.get("s_employeeId"));
				    	operationLogs.put("OPERATOR_IP", context.getRequest().getRemoteAddr());
				    	operationLogs.put("MEMO", "");
				    	sqlMapper.insert("supplier.insertOperationLogs", operationLogs);
			    	}
			    	
			    	
			    	sqlMapper.update("supplier.updateById", context.contextMap);
			    	
			    	//公司基本账户
					sqlMapper.update("supplier.deleteSuppliesBankAccountById", context.contextMap);
					Map baseBankAccount=new HashMap();
					String B_PCCBA_ID=(String)context.contextMap.get("B_PCCBA_ID");
					baseBankAccount.put("BANK_NAME", context.contextMap.get("B_BANK_NAME"));
					baseBankAccount.put("BANK_ACCOUNT", context.contextMap.get("B_BANK_ACCOUNT"));
					baseBankAccount.put("STATE","0");
					baseBankAccount.put("SUPL_ID", context.contextMap.get("supl_id"));
					baseBankAccount.put("PCCBA_ID", B_PCCBA_ID);
					
					sqlMapper.insert("supplier.createSupplierBankAccount", baseBankAccount);
	
					//公司其他账户
					sqlMapper.delete("supplier.deleteSuppliersBankAccountBySuplId", context.contextMap);
					if(context.request.getParameterValues("BANK_NAME")!=null){
//						String[] BANK_NAME=HTMLUtil.getParameterValues(context.request, "BANK_NAME", "");
						String[] BANK_ACCOUNT = bankAccount;
						String[] BANK_NAME = bankName;
//						String[] BANK_ACCOUNT = context.request.getParameterValues("BANK_ACCOUNT");
						for (int i = 0; i < BANK_NAME.length; i++) {
					    	
							//***********************************************TEST*************************************************************************************
							logger.info("===========================add by yangliu, test 供应商其他帐户乱码BUG ======================================");
							logger.info("===========================insert BANK_NAME(编码前) ======================================");
							logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
							logger.info("===========================BANK_NAME[" + i + "]:" + BANK_NAME[i] + "===========================");
							logger.info("===========================insert BANK_NAME(旧，编码后) ======================================");
							logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
							logger.info("===========================BANK_NAME[" + i + "]:" + HTMLUtil.getParameterValues(context.request, "BANK_NAME", "")[i] + "===========================");
					    	//***********************************************TEST*************************************************************************************
							
							Map bankAccountMap=new HashMap();
//							bankAccount.put("BANK_NAME", URLDecoder.decode(URLEncoder.encode(BANK_NAME[i],"ISO-8859-1"), "UTF-8"));
//							bankAccount.put("BANK_ACCOUNT", URLDecoder.decode(URLEncoder.encode(BANK_ACCOUNT[i],"ISO-8859-1"), "UTF-8"));
							bankAccountMap.put("BANK_NAME", BANK_NAME[i]);
							bankAccountMap.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
							

					    	//***********************************************TEST*************************************************************************************
							logger.info("===========================insert BANK_NAME(新，编码后) ======================================");
							logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
							logger.info("===========================BANK_NAME[" + i + "]:" + bankAccountMap.get("BANK_NAME").toString() + "===========================");
							logger.info("===========================insert BANK_NAME(新，测试编码后) ======================================");
							logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
							logger.info("===========================URLDecoder.decode>UTF-8：BANK_NAME[" + i + "]:" + URLDecoder.decode(BANK_NAME[i], "UTF-8") + "===========================");
							logger.info("===========================URLDecoder.decode>GBK：BANK_NAME[" + i + "]:" + URLDecoder.decode(BANK_NAME[i], "GBK") + "===========================");
							logger.info("===========================URLEncoder.encode>UTF-8：BANK_NAME[" + i + "]:" + URLEncoder.encode(BANK_NAME[i],"UTF-8") + "===========================");
							logger.info("===========================URLEncoder.encode>GBK：BANK_NAME[" + i + "]:" + URLEncoder.encode(BANK_NAME[i],"GBK") + "===========================");
							logger.info("===========================URLEncoder.encode>ISO-8859-1：BANK_NAME[" + i + "]:" + URLEncoder.encode(BANK_NAME[i],"ISO-8859-1") + "===========================");
							logger.info("===========================ISO-8859-1>UTF-8：BANK_NAME[" + i + "]:" + URLDecoder.decode(URLEncoder.encode(BANK_NAME[i],"ISO-8859-1"), "UTF-8") + "===========================");
							logger.info("===========================ISO-8859-1>GBK：BANK_NAME[" + i + "]:" + URLDecoder.decode(URLEncoder.encode(BANK_NAME[i],"ISO-8859-1"), "GBK") + "===========================");
							logger.info("===========================GBK>UTF-8：BANK_NAME[" + i + "]:" + URLDecoder.decode(URLEncoder.encode(BANK_NAME[i],"GBK"), "UTF-8") + "===========================");
							logger.info("===========================UTF-8>GBK：BANK_NAME[" + i + "]:" + URLDecoder.decode(URLEncoder.encode(BANK_NAME[i],"UTF-8"), "GBK") + "===========================");
					    	//***********************************************TEST*************************************************************************************
							
							bankAccountMap.put("STATE","1");
							bankAccountMap.put("SUPL_ID", context.contextMap.get("supl_id"));
							sqlMapper.insert("supplier.createSupplierBankAccount", bankAccountMap);
						}
					}
			    	
			    	sqlMapper.commitTransaction();
				outputMap.put("msg",RESUTL);
			} catch (Exception e) {
				e.printStackTrace();
				outputMap.put("msg",ERROR);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
			finally { 
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) { 
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				} 
			}
		} 
		
		if (errList.isEmpty()) {
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=supplier.findAllSupplier&isSalesDesk=Y");
			} else {
				Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=supplier.findAllSupplier");
			}
		}
	}
	
	/**
	 * 拼接供应商资料修改日志记录信息
	 * @param context
	 * @param sqlMapper
	 * @return	修改记录信息
	 * @throws Exception
	 */
	private void makeSupplierUpdateLogMessage(Context context, SqlMapClient sqlMapper) throws Exception {
		//表字段中英文对照
    	Map<String, String> englishToChineseNames = new HashMap<String, String>();
    	englishToChineseNames.put("NAME", "企业名称");
    	englishToChineseNames.put("TYPE", "企业性质");
    	englishToChineseNames.put("FOUNDED_DATE", "成立日期");
    	englishToChineseNames.put("REGISTERED_CAPITAL", "注册资本(万)");
    	englishToChineseNames.put("INCOME_CAPITAL", "实收资本(万)");
    	englishToChineseNames.put("COMPANY_CORPORATION", "法定代表人");
    	englishToChineseNames.put("CORPORATION_ID_CARD", "法人身份证号码");
    	englishToChineseNames.put("CORPORATION_LINK", "法人联系方式");
    	englishToChineseNames.put("CORPORATION_ADDRESS", "法人代表住址");
    	englishToChineseNames.put("BUSINESS_LICENCE", "营业执照注册号");
    	englishToChineseNames.put("ORGANIZATION_CERTIFICATE", "组织机构代码号");
    	englishToChineseNames.put("TEX_CODE", "税务登记号");
    	englishToChineseNames.put("VALIDITY_PERIOD", "有效期(年)");
    	englishToChineseNames.put("LICENCE_ADDRESS", "注册地址");
    	englishToChineseNames.put("WORK_ADDRESS", "通讯地址");
    	englishToChineseNames.put("BUSINESS_SCOPE", "经营范围");
    	englishToChineseNames.put("LINKMAN_NAME", "联系人");
    	englishToChineseNames.put("LINKMAN_JOB", "职务");
    	englishToChineseNames.put("LINKMAN_TELPHONE", "办公电话");
    	englishToChineseNames.put("LINKMAN_MOBILE", "手机");
    	englishToChineseNames.put("LINKMAN_ZIP", "邮编");
    	englishToChineseNames.put("COMPANY_WEB", "公司网址");
//    	englishToChineseNames.put("CREDIT_TYPE", "信用级别");
    	englishToChineseNames.put("OPEN_ACCOUNT_BANK", "开户银行基本账户(开户银行)");
    	englishToChineseNames.put("BANK_ACCOUNT", "开户银行基本账户(账号)");
    	englishToChineseNames.put("OTHER_OPEN_ACCOUNT_BANK", "开户银行");
    	englishToChineseNames.put("OTHER_BANK_ACCOUNT", "账号");
    	englishToChineseNames.put("MEMO", "备注");
//    	englishToChineseNames.put("MODIFY_DATE", "修改日期");
    	englishToChineseNames.put("LINKMAN_EMAIL", "Email");
    	englishToChineseNames.put("BUYBACK_PRICE", "回购金额额度(万)");
    	englishToChineseNames.put("SUPP_TYPE", "供应商类型");
    	englishToChineseNames.put("REGISTERED_CAPITAL_MONEY_TYPE", "注册资本(万)(币种)");
    	englishToChineseNames.put("INCOME_CAPITAL_MONEY_TYPE", "实收资本(万)(币种)");
    	englishToChineseNames.put("SUPP_MODEL", "供应商类别");
    	englishToChineseNames.put("SUPP_LEVEL", "供应商级别");
    	englishToChineseNames.put("BUY_BACK", "全面回购");
    	englishToChineseNames.put("NET_PAY", "网银汇款");
    	englishToChineseNames.put("LINKMAN_FAX", "传真");
    	List supplierList = sqlMapper.queryForList("supplier.queryByid", context.contextMap);
		Map<String,Object> supplier = (Map<String,Object>)supplierList.get(0);
		//币种code对应中文名
		context.contextMap.put("dataType","货币类型");
		List<Map> moneyType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String dateOld = "";
    	String dateNew = "";
    	if(!StringUtils.isEmpty(supplier.get("FOUNDED_DATE"))){
    		dateOld = sdf.format((Date)supplier.get("FOUNDED_DATE"));
    	}
		if(!StringUtils.isEmpty(context.contextMap.get("Founded_date"))){
			dateNew = sdf.format((Date)context.contextMap.get("Founded_date"));
		}
    	this.addLog(supplier.get("NAME"), context.contextMap.get("name"), englishToChineseNames.get("NAME"));
    	//存放value对应中文名
    	Map<String, String> nameByType = new HashMap<String, String>();
    	
    	nameByType.put("1", "厂商");
    	nameByType.put("2", "代理商");
    	nameByType.put("3", "其他供应商");
    	String[] types = this.getNameByType(supplier.get("TYPE"), context.contextMap.get("Type"), nameByType);
    	this.addLog(types[0], types[1], englishToChineseNames.get("TYPE"));
    	
		this.addLog(dateOld, dateNew, englishToChineseNames.get("FOUNDED_DATE"));
		this.addLog(supplier.get("REGISTERED_CAPITAL")==null?null:StringUtils.str2double(supplier.get("REGISTERED_CAPITAL").toString()), 
					context.contextMap.get("Registered_capital")==null?null:StringUtils.str2double(context.contextMap.get("Registered_capital").toString()), 
					englishToChineseNames.get("REGISTERED_CAPITAL"));
		this.addLog(supplier.get("INCOME_CAPITAL")==null?null:StringUtils.str2double(supplier.get("INCOME_CAPITAL").toString()), 
					context.contextMap.get("Income_capital")==null?null:StringUtils.str2double(context.contextMap.get("Income_capital").toString()), 
					englishToChineseNames.get("INCOME_CAPITAL"));
		this.addLog(supplier.get("COMPANY_CORPORATION"), context.contextMap.get("Company_Corporation"), englishToChineseNames.get("COMPANY_CORPORATION"));
		this.addLog(supplier.get("CORPORATION_ID_CARD"), context.contextMap.get("Corporation_id_card"), englishToChineseNames.get("CORPORATION_ID_CARD"));
		this.addLog(supplier.get("CORPORATION_LINK"), context.contextMap.get("Corporation_link"), englishToChineseNames.get("CORPORATION_LINK"));
		this.addLog(supplier.get("CORPORATION_ADDRESS"), context.contextMap.get("Corporation_address"), englishToChineseNames.get("CORPORATION_ADDRESS"));
		this.addLog(supplier.get("BUSINESS_LICENCE"), context.contextMap.get("Business_licence"), englishToChineseNames.get("BUSINESS_LICENCE"));
		this.addLog(supplier.get("ORGANIZATION_CERTIFICATE"), context.contextMap.get("Organization_certificate"), englishToChineseNames.get("ORGANIZATION_CERTIFICATE"));
		this.addLog(supplier.get("TEX_CODE"), context.contextMap.get("Tex_code"), englishToChineseNames.get("TEX_CODE"));
		this.addLog(supplier.get("VALIDITY_PERIOD")==null?null:StringUtils.str2double(supplier.get("VALIDITY_PERIOD").toString()), 
					context.contextMap.get("Validity_Period")==null?null:StringUtils.str2double(context.contextMap.get("Validity_Period").toString()), 
					englishToChineseNames.get("VALIDITY_PERIOD"));
		this.addLog(supplier.get("LICENCE_ADDRESS"), context.contextMap.get("Licence_address"), englishToChineseNames.get("LICENCE_ADDRESS"));
		this.addLog(supplier.get("WORK_ADDRESS"), context.contextMap.get("Work_address"), englishToChineseNames.get("WORK_ADDRESS"));
		this.addLog(supplier.get("BUSINESS_SCOPE"), context.contextMap.get("Business_scope"), englishToChineseNames.get("BUSINESS_SCOPE"));
		this.addLog(supplier.get("LINKMAN_NAME"), context.contextMap.get("Linkman_name"), englishToChineseNames.get("LINKMAN_NAME"));
		this.addLog(supplier.get("LINKMAN_JOB"), context.contextMap.get("Linkman_job"), englishToChineseNames.get("LINKMAN_JOB"));
		this.addLog(supplier.get("LINKMAN_TELPHONE"), context.contextMap.get("Linkman_telphone"), englishToChineseNames.get("LINKMAN_TELPHONE"));
		this.addLog(supplier.get("LINKMAN_MOBILE"), context.contextMap.get("Linkman_mobile"), englishToChineseNames.get("LINKMAN_MOBILE"));
		this.addLog(supplier.get("LINKMAN_ZIP"), context.contextMap.get("Linkman_zip"), englishToChineseNames.get("LINKMAN_ZIP"));
		this.addLog(supplier.get("COMPANY_WEB"), context.contextMap.get("Company_Web"), englishToChineseNames.get("COMPANY_WEB"));
		this.addLog(supplier.get("OPEN_ACCOUNT_BANK"), context.contextMap.get("B_BANK_NAME"), englishToChineseNames.get("OPEN_ACCOUNT_BANK"));
		this.addLog(supplier.get("BANK_ACCOUNT"), context.contextMap.get("B_BANK_ACCOUNT"), englishToChineseNames.get("BANK_ACCOUNT"));
		this.addLog(supplier.get("MEMO"), context.contextMap.get("memo"), englishToChineseNames.get("MEMO"));
		this.addLog(supplier.get("LINKMAN_EMAIL"), context.contextMap.get("LINKMAN_EMAIL"), englishToChineseNames.get("LINKMAN_EMAIL"));
		this.addLog(supplier.get("BUYBACK_PRICE")==null?null:StringUtils.str2double(supplier.get("BUYBACK_PRICE").toString()), 
					context.contextMap.get("buyback_price")==null?null:StringUtils.str2double(context.contextMap.get("buyback_price").toString()), 
					englishToChineseNames.get("BUYBACK_PRICE"));
		
		nameByType.clear();
		nameByType.put("0", "非大型供应商");
		nameByType.put("1", "大型供应商");
		types = this.getNameByType(supplier.get("SUPP_TYPE"), context.contextMap.get("supp_type"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("SUPP_TYPE"));
		
		nameByType.clear();
		//获取币种value对应中文名
		for (Map moneyTp : moneyType) {
			nameByType.put(moneyTp.get("CODE").toString(), moneyTp.get("FLAG").toString());
		}
    	types = this.getNameByType(supplier.get("REGISTERED_CAPITAL_MONEY_TYPE"), context.contextMap.get("moneyType1"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("REGISTERED_CAPITAL_MONEY_TYPE"));
		
    	types = this.getNameByType(supplier.get("INCOME_CAPITAL_MONEY_TYPE"), context.contextMap.get("moneyType2"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("INCOME_CAPITAL_MONEY_TYPE"));
		
		nameByType.clear();
		nameByType.put("0", "一般设备");
		nameByType.put("1", "重车");
		nameByType.put("2", "其他");
		types = this.getNameByType(supplier.get("SUPP_MODEL"), context.contextMap.get("SUPP_MODEL"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("SUPP_MODEL"));
		
		this.addLog(supplier.get("SUPP_LEVEL"), context.contextMap.get("SUPP_LEVEL"), englishToChineseNames.get("SUPP_LEVEL"));
		
		nameByType.clear();
		nameByType.put("Y", "是");
		nameByType.put("N", "否");
		types = this.getNameByType(supplier.get("BUY_BACK"), context.contextMap.get("BUY_BACK"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("BUY_BACK"));
		
		nameByType.clear();
		nameByType.put("Y", "可");
		nameByType.put("N", "不可");
		types = this.getNameByType(supplier.get("NET_PAY"), context.contextMap.get("NET_PAY"), nameByType);
		this.addLog(types[0], types[1], englishToChineseNames.get("NET_PAY"));
		
		this.addLog(supplier.get("LINKMAN_FAX"), context.contextMap.get("LINKMAN_FAX"), englishToChineseNames.get("LINKMAN_FAX"));
		
		//添加开户银行（其他帐户修改信息）log
    	List<Map> suplBankAccount=(List<Map>) DataAccessor.query(
				"supplier.getSupplierBankAccountByCreditId", context.contextMap,
				DataAccessor.RS_TYPE.LIST);
    	//修改前所有其他开户银行信息
    	String oldBankInfo = "";
    	//修改后所有其他开户银行信息
    	String newBankInfo = "";
    	String[] BANK_NAME = (String[])context.contextMap.get("bankName");
    	String[] BANK_ACCOUNT = (String[])context.contextMap.get("bankAccount");;
    	List<String> OLD_BANK_NAME = new ArrayList<String>();
    	List<String> OLD_BANK_ACCOUNT = new ArrayList<String>();
//    	if(context.request.getParameterValues("BANK_NAME")!=null){
//			BANK_ACCOUNT=HTMLUtil.getParameterValues(context.request, "BANK_ACCOUNT", "");
//    	}
    	//***********************************************TEST*************************************************************************************
    	if(BANK_NAME != null){
	    	for(int i = 0; i< BANK_NAME.length; i++){
		    	logger.info("===========================add by yangliu, test 供应商其他帐户乱码BUG ======================================");
				logger.info("===========================log BANK_NAME(编码前) ======================================");
				logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
				logger.info("===========================BANK_NAME[" + i + "]:" + context.request.getParameterValues("BANK_NAME")[i] + "===========================");
				logger.info("===========================log BANK_NAME(编码后) ======================================");
				logger.info("===========================CharacterEncod:" + context.request.getCharacterEncoding() + " ======================================");
				logger.info("===========================BANK_NAME[" + i + "]:" + BANK_NAME[i] + "===========================");
	    	}
    	}
    	//***********************************************TEST*************************************************************************************
		
    	for(int i = 0; i < suplBankAccount.size(); i++){
    		Map supl = suplBankAccount.get(i);
    		//其他帐户
    		if(supl.get("STATE").toString().equals("1")){
    			OLD_BANK_NAME.add(supl.get("BANK_NAME").toString());
    			OLD_BANK_ACCOUNT.add(supl.get("BANK_ACCOUNT").toString());
    		}
    	}
    	//判断其他帐户是否有修改
    	boolean isModify = false;
		if(BANK_NAME != null && BANK_NAME.length == OLD_BANK_NAME.size()){
			for(int i = 0; i < BANK_NAME.length; i++){
				boolean hasDifferent = true;
				for(int j = 0; j < OLD_BANK_NAME.size(); j++){
					//如果找到有相同值则跳过，标记该数据无变化
					if(BANK_NAME[i].equals(OLD_BANK_NAME.get(j)) && BANK_ACCOUNT[i].equals(OLD_BANK_ACCOUNT.get(j))){
						hasDifferent = false;
						break;
					}
				}
				//如果有一个值未找到发现变化则退出后标记为有修改
				if(hasDifferent){
					isModify = true;
					break;
				}
			}
		} else {
			isModify = true;
		}
		//修改过添加入修改日志
		if(isModify){
			if(BANK_NAME != null){
				for(int i = 0; i < BANK_NAME.length; i++){
					newBankInfo = newBankInfo + englishToChineseNames.get("OTHER_OPEN_ACCOUNT_BANK") + "：" + BANK_NAME[i] + "，"
								+ englishToChineseNames.get("OTHER_BANK_ACCOUNT") + "：" + BANK_ACCOUNT[i] + "；";
				}
			}
			for(int i = 0; i < OLD_BANK_NAME.size(); i++){
				oldBankInfo = oldBankInfo + englishToChineseNames.get("OTHER_OPEN_ACCOUNT_BANK") + "：" + OLD_BANK_NAME.get(i) + "，"
						+ englishToChineseNames.get("OTHER_BANK_ACCOUNT") + "：" + OLD_BANK_ACCOUNT.get(i) + "；";
			}
		}
		this.addLog(oldBankInfo, newBankInfo, "开户银行(其他帐户)");
	}
	
	/**
	 * 添加log信息
	 * @param oldObject 修改前信息
	 * @param newObject 修改后信息
	 * @param name 修改字段中文名
	 */
	private void addLog(Object oldObject, Object newObject, String chineseName){
		if(oldObject != null && newObject != null && !oldObject.toString().equals(newObject.toString())
    			|| (oldObject != null && !oldObject.toString().equals("") && newObject == null)
    			|| (newObject != null && !newObject.toString().equals("") && oldObject == null)){
			if(newObject instanceof Double && oldObject instanceof Double && StringUtils.str2double(newObject.toString()) == StringUtils.str2double(oldObject.toString())){
				return;
			}
			LogMsgTo msg = new LogMsgTo();
			msg.setName(chineseName);
			String oldMsg = oldObject==null?"":oldObject.toString();
			String newMsg = newObject==null?"":newObject.toString();
			oldMsg = oldMsg.replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
			newMsg = newMsg.replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("\\\\", "\\\\\\\\");
			msg.setMsgs(new String[]{oldMsg,newMsg});
			msgs.add(msg);
		}
	}
	
	/**
	 * 根据类型取得中文值名
	 * @param oldObject 修改前数据
	 * @param newObject 修改后数据
	 * @param nameByType 类型对应的中文值名
	 * @return 取得中文值名的修改前后对象[修改前值，修改后值]
	 */
	private String[] getNameByType(Object oldObject, Object newObject, Map<String, String> nameByType){
		String oldName = null;
		String newName = null;
		if(!StringUtils.isEmpty(oldObject)){
			oldName = nameByType.get(oldObject.toString());
		}
		if(!StringUtils.isEmpty(newObject)){
			newName = nameByType.get(newObject.toString());
		}
		return new String[]{oldName, newName};
	}
	
	@SuppressWarnings("unchecked")
	public void invalidte(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		if (errList.isEmpty()) {
			try {			
				DataAccessor.execute("supplier.updateStatusInvalidte", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
				outputMap.put("msg",RESUTL);
			}catch (Exception e) {
				e.printStackTrace();
				outputMap.put("msg",ERROR);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		} 
		if (errList.isEmpty()) {			
			Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=supplier.findAllSupplier");
		}
	}
	/** cheng 6.30
	 * ajax 方法 为供应商管理  编号唯一检查   
	 *  select count(1) from t_supl_supplier t where t.code="#code" 
	 *  
	 */
	@SuppressWarnings("unchecked")
	public void codeExist(Context context){
	    
	    Map outputMap = new HashMap();
	    List errList = context.errList;
	   
	    Map codeMap = null;
	    
	    if(errList.isEmpty()){
        	    try{
        			
        		        codeMap = (Map) DataAccessor.query("supplier.queryCode", context.contextMap, RS_TYPE.MAP);
        			
        		        outputMap.put("codeMap", codeMap); 
        		        
        	    	}catch(Exception e){
        	    	    errList.add(e.getMessage());
        	    	    e.printStackTrace();
        	    	    LogPrint.getLogStackTrace(e, logger);
        				errList.add(e);
        	    	}
	    }
	    
	    if(errList.isEmpty()){
		Output.jsonOutput(outputMap, context);
	    } else {
		// 错误页面
	    }
	}
	/** cheng 6.30
	 * ajax 方法 为供应商管理  名称唯一检查   
	 *  select count(1) from t_supl_supplier t where t.name="#name" 
	 *  
	 */
	@SuppressWarnings("unchecked")
	public void checkSupplierName(Context context){
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map codeMap = null;
		
		
		if(errList.isEmpty()){
			try{
				
				codeMap = (Map) DataAccessor.query("supplier.queryName", context.contextMap, RS_TYPE.MAP);
				
				//System.out.println("-------------------------------------------");
				outputMap.put("codeMap", codeMap); 
			}catch(Exception e){
				errList.add(e.getMessage());
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		} else {
			// 错误页面
		}
	}
	/**
	 * 供应商资料查看
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showSupplierImage(Context context ) {
		List errList = context.errList;
		List suplBankAccount=null;
		Map outputMap = new HashMap();
		/* 2012/01/12 Yang Yun 上传图片完成后返回当前页面---------------------- */
		String id=(String) context.contextMap.get("id");//context.request.getParameter("id");
		/* ------------------------------------------------------------------ */
		if (errList.isEmpty()) {
			try {
				//获得货币类型 add by ShenQi
				context.contextMap.put("dataType","货币类型");
				outputMap.put("moneyType",(List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST));
				
				outputMap.put("rs",(Map) DataAccessor.query("supplier.queryByid", context.contextMap, DataAccessor.RS_TYPE.MAP));
				context.contextMap.put("supplier_id",context.contextMap.get("id").toString());
				suplBankAccount=(List) DataAccessor.query(
						"supplier.getSupplierBankAccountByCreditId", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("suplBankAccount", suplBankAccount);
				outputMap.put("supplierImage",DataAccessor.query("SupplierImage.querySupplierImageBySupplierId", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
			}catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
				outputMap.put("supplier_id", id);
				Output.jspOutput(outputMap, context, "/product/supplier/supplierUploadImage.jsp");
		}
	}
	/**
	 * 资料添加
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createSupplierImage(Context context){
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		SqlMapClient sqlMapper = null ;
		List fileItems =(List)context.contextMap.get("uploadList");	
		try{
			sqlMapper=DataAccessor.getSession();
			sqlMapper.startTransaction();
			//添加供应商图片
			if(fileItems != null && fileItems.size() > 0){
				for (int i = 0 ;i < fileItems.size() ;i++ ) {
					FileItem fileItem = (FileItem) fileItems.get(i);
					InputStream in =fileItem.getInputStream();		
					if(!fileItem.getName().equals("")){
						saveFileToDisk(context,fileItem,sqlMapper,context.contextMap.get("imagesName"+i).toString());
					}
				}
			}
			sqlMapper.commitTransaction() ;
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (errList.isEmpty()) {
			/* 2012/01/12 Yang Yun 上传图片完成后返回当前页面---------------------- */
			//Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=supplier.findAllSupplier");
			this.showSupplierImage(context);
			/* ------------------------------------------------------------------- */
		}
	}
	
	/**
	 * 删除供应商图片AJAX
	 * 于秋辰2011-08-28
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteSupplierImage(Context context){
		Map<String,Object> outputMap = new HashMap<String,Object>() ;
		List<Object> errList = context.errList ;
		try{
			DataAccessor.execute("SupplierImage.deleteSupplierImage", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE) ;
			
		}catch(Exception e){
			LogPrint.getLogStackTrace(e, logger) ;
			e.printStackTrace() ;
			errList.add("删除供应商图片错误！") ;
		}
		if(errList.isEmpty()){
			outputMap.put("content", "1");
			Output.jsonOutput(outputMap, context) ;
		} else {
			outputMap.put("content", "2");
			Output.jsonOutput(outputMap, context) ;
		}
	}
	
	/**
	 * 保存文件到硬盘中 并将保存信息存入数据库
	 * @param context
	 * @param fileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String saveFileToDisk(Context context, FileItem fileItem,SqlMapClient sqlMapClient ,String title) throws Exception {
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		List errList = context.errList;
		Map contextMap = context.contextMap;
		String bootPath = null;
		bootPath = this.getUploadPath("supplierUpload");
		String file_path="";
		
		if (bootPath != null) {
			//Modify by Michael 2012 07-13 上传附档增加日期文件夹
			File realPath = new File(bootPath+ File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
			if (!realPath.exists())
				realPath.mkdirs();
				String excelNewName = FileExcelUpload.getNewFileName();
				File uploadedFile = new File(realPath.getPath() + File.separator + excelNewName + "." + type);
				file_path = File.separator+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+File.separator + type + File.separator + excelNewName + "." + type;
				try {
					
					if (errList.isEmpty()) {
						fileItem.write(uploadedFile); 
						contextMap.put("path", file_path);
						contextMap.put("image_name", fileItem.getName());
						contextMap.put("title", title);
						sqlMapClient.insert("SupplierImage.createSupplierImage", contextMap);							
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
					throw e ;
				}finally{
				  try{
					    fileItem.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
				
				fileItem.delete();
			}
		}
		return null;
	}
	/**
	 * 
	 * @return 读取upload-config.xml文件 获取保存根路径
	 */
	public String getUploadPath(String xmlPath) {
		String path = null;		
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
			Element root = document.getRootElement();
			List nodes = root.elements("action");
		    for(Iterator it=nodes.iterator();it.hasNext();){
		    	Element element = (Element) it.next();
		    	Element nameElement=element.element("name");
		    	String s = nameElement.getText();
		    	if(xmlPath.equals(s)){
		    		Element pathElement=element.element("path");
		    		path = pathElement.getText();
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} 	
		return path;
	}
	
	//add by ShenQi 增加货币类型
	public void getMoneyType(Context context) {
		
		List<Map> resultList=null;
		try {
			context.contextMap.put("dataType","货币类型");
			resultList=(List<Map>)DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Output.jsonArrayOutput(resultList,context);
		
	}
	
	
	/**根据拨款日期查询供应商列表**/
	@SuppressWarnings("unchecked")
	public void findSupplierByFinanceDate(Context context ) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		if (errList.isEmpty()) {
			try {
				if (context.contextMap.get("START_DATE")==null){
					context.contextMap.put("START_DATE",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				}
				if (context.contextMap.get("END_DATE")==null){
					context.contextMap.put("END_DATE", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
				}
				dw = baseService.queryForListWithPaging("applyCompanyManage.getAllSupplierByFinanceDate", context.contextMap, "NAME", ORDER_TYPE.DESC);
				outputMap.put("dw",dw);
				outputMap.put("START_DATE",context.contextMap.get("START_DATE"));
				outputMap.put("END_DATE",context.contextMap.get("END_DATE"));
				outputMap.put("searchValue",context.contextMap.get("searchValue"));
			}catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/product/supplierBusinessLetter/supplierBusinessLetterQuery.jsp");
		}
	}
	
	/**
	 * 根据供应商的id查询对应的所有的合同
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void findContractInfoBySupplierId (Context context) {
		
		List errList = context.errList;
		Map outputMap = new HashMap();
		List contractlist = null;
		List bigcontractlistmoney = new ArrayList();
		context.contextMap.put("START_DATE", context.contextMap.get("start_date"));
		context.contextMap.put("END_DATE", context.contextMap.get("end_date"));
		if(errList.isEmpty()){		
			try {
				//找到所有的合同的信息，包括合同的id
				contractlist = (List) DataAccessor.query("applyCompanyManage.getCustRentListBySupplID", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//对合同进行一个遍历，找到每个合同对应的所有的支付表
				for(int i=0;i<contractlist.size();i++){
					HashMap money = new HashMap();
					money.put("contractlist", (HashMap)contractlist.get(i));
					context.contextMap.put("C", "租金");
					context.contextMap.put("RECT_ID", ((HashMap)contractlist.get(i)).get("RECT_ID"));
					context.contextMap.put("RECP_ID", ((HashMap)contractlist.get(i)).get("RECP_ID"));
					//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
					HashMap paylinesum = (HashMap) DataAccessor.query("applyCompanyManage.getCustRentDataByRectID", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if(paylinesum==null){
						paylinesum=new HashMap();
					}
					
					HashMap contractShengyuBenjin = (HashMap) DataAccessor.query("applyCompanyManage.findShengyuBenjinContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					paylinesum.put("LASTPRICE", contractShengyuBenjin.get("SHENGYUBENJIN"));
			
					money.put("paylinesum", paylinesum);
					
					//查询逾期状况
					List dunList;
					int fifteenDay=0;
					int thirtyDay=0;
					dunList=(List<Map>) DataAccessor.query("applyCompanyManage.getCustRentDunDayByRecpID", context.contextMap, DataAccessor.RS_TYPE.LIST);
					for(int j=0;j<dunList.size();j++){
						if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=15 && DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))<30){
							fifteenDay+=1;
						}else if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=30){
							thirtyDay+=1;
						}
					}
					money.put("fifteenDay", fifteenDay);
					money.put("thirtyDay", thirtyDay);
					
					//查询出合同状态
					context.contextMap.put("DICTYPE", "供应商保证");
					HashMap paylineState=(HashMap)DataAccessor.query("applyCompanyManage.findDicTypeContractId", context.contextMap, DataAccessor.RS_TYPE.MAP);
					money.put("paylineState", paylineState);
					bigcontractlistmoney.add(money);	
				}
				List rentListSuplTrue;
				int rentCountLien=0;
				int rentCountRepurch=0;
				int eqmtCountRepurch=0;
				int eqmtCountLien=0;
				rentListSuplTrue=(List<Map>) DataAccessor.query("applyCompanyManage.getCustRentSuplTrueListBySuplID", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int j=0;j<rentListSuplTrue.size();j++){
					//1为连保，2为回购 3为回购含灭失
					if (DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==1){
						rentCountLien+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("COUNTI"));
						eqmtCountLien+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("NUM"));
					}else if(DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==2||DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==3){
						rentCountRepurch+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("COUNTI"));
						eqmtCountRepurch+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("NUM"));
					}
				}
				outputMap.put("rentCountLien",rentCountLien);
				outputMap.put("eqmtCountLien",eqmtCountLien);
				outputMap.put("rentCountRepurch",rentCountRepurch);
				outputMap.put("eqmtCountRepurch",eqmtCountRepurch);
				
				//查询连保授信余额
				Double lienLastPriceDouble=(Double) (SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(context.contextMap.get("supplierid").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLastPrice(Integer.parseInt(context.contextMap.get("supplierid").toString())));
				if (lienLastPriceDouble<0){
					lienLastPriceDouble=0.0;
				} 
				Double repurchLastPrice=(Double)(SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(context.contextMap.get("supplierid").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLastPrice(Integer.parseInt(context.contextMap.get("supplierid").toString())));
				if (repurchLastPrice<0){
					repurchLastPrice=0.0;
				} 
				outputMap.put("LienLastPrice",Math.round(lienLastPriceDouble));
				//查询回购授信余额
				outputMap.put("RepurchLastPrice",Math.round(repurchLastPrice));
				//查询供应商授信状况
				outputMap.put("SuplGrantPrice",DataAccessor.query("applyCompanyManage.getSuplGrantDetailBySuplID", context.contextMap, DataAccessor.RS_TYPE.MAP));
				
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add("供应商管理--供应商往来错误!请联系管理员");
			}
		}
		outputMap.put("contractlists", bigcontractlistmoney);	
		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/product/supplierBusinessLetter/contractpact.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/*
	 * Add by Michael 2012 8-23
	 * 查询供应商往来函备注信息
	 */
	public void querySupplierBusinessMemo(Context context)
	{
		Map outputMap = new HashMap();

		Map writeBackDetails = null;	

		List errList = context.errList ;
		try {
			writeBackDetails = (Map) DataAccessor.query("applyCompanyManage.getSupplierBusinessMemoByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("查询供应商往来函备注!请联系管理员");			
		}
		
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
	}
	
	/*
	 * Add by Michael 2012 8-23
	 * 创建供应商往来函备注信息
	 */
	@SuppressWarnings("unchecked")
	public void createSupplierBusinessMemo(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;

		/*-------- data access --------*/	
		Map rsMap ;
		if(errList.isEmpty()){	
		
			try {
				rsMap = (Map) DataAccessor.query("applyCompanyManage.getSupplierBusinessMemoByID", context.contextMap, DataAccessor.RS_TYPE.MAP);
				DataAccessor.getSession().startTransaction();
				if (rsMap!=null){
					DataAccessor.getSession().insert("applyCompanyManage.modifySupplierBusinessMemo", context.contextMap);
				}else{
					DataAccessor.getSession().update("applyCompanyManage.createSupplierBusinessMemo", context.contextMap) ;
				}
				DataAccessor.getSession().commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					DataAccessor.getSession().endTransaction() ;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		Map writeBackDetails=new HashMap();
		writeBackDetails.put("strReturn","操作成功！");
		outputMap.put("writeBackDetails", writeBackDetails);	
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
		}
		
	}
	
	public static Map findContractInfoBySupplierIdForExport (String supplier_id,String start_date,String end_date) throws Exception{
		
		Map resultMap = new HashMap();
		List contractlist = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("supplierid", supplier_id);
		paramMap.put("START_DATE", start_date);
		paramMap.put("END_DATE", end_date);
		//找到所有的合同的信息，包括合同的id
		contractlist = (List) DataAccessor.query("applyCompanyManage.getCustRentListBySupplID", paramMap, DataAccessor.RS_TYPE.LIST);
		//对合同进行一个遍历，找到每个合同对应的所有的支付表
		for(int i=0;i<contractlist.size();i++){
			HashMap money = new HashMap();
			money.put("contractlist", (HashMap)contractlist.get(i));
			paramMap.put("C", "租金");
			paramMap.put("RECT_ID", ((HashMap)contractlist.get(i)).get("RECT_ID"));
			paramMap.put("RECP_ID", ((HashMap)contractlist.get(i)).get("RECP_ID"));
			//根据合同的id查询到所有的没有交钱的支付表，并统计出总期数，未交期数，剩余租金，实际剩余租金
			HashMap paylinesum = (HashMap) DataAccessor.query("applyCompanyManage.getCustRentDataByRectID", paramMap, DataAccessor.RS_TYPE.MAP);
			if(paylinesum==null){
				paylinesum=new HashMap();
			}
			((Map)contractlist.get(i)).put("WEIJIAOQISHU", paylinesum.get("WEIJIAOQISHU"));
			((Map)contractlist.get(i)).put("ZONGQISHU", paylinesum.get("ZONGQISHU"));
			
			HashMap contractShengyuBenjin = (HashMap) DataAccessor.query("applyCompanyManage.findShengyuBenjinContractId", paramMap, DataAccessor.RS_TYPE.MAP);
			((Map)contractlist.get(i)).put("LASTPRICE", contractShengyuBenjin.get("SHENGYUBENJIN"));
			
			//查询逾期状况
			List dunList;
			int fifteenDay=0;
			int thirtyDay=0;
			dunList=(List<Map>) DataAccessor.query("applyCompanyManage.getCustRentDunDayByRecpID", paramMap, DataAccessor.RS_TYPE.LIST);
			for(int j=0;j<dunList.size();j++){
				if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=15 && DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))<30){
					fifteenDay+=1;
				}else if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=30){
					thirtyDay+=1;
				}
			}
			((Map)contractlist.get(i)).put("fifteenDay", fifteenDay);
			((Map)contractlist.get(i)).put("thirtyDay", thirtyDay);
			
			//查询出合同状态
			paramMap.put("DICTYPE", "供应商保证");
			HashMap paylineState=(HashMap)DataAccessor.query("applyCompanyManage.findDicTypeContractId", paramMap, DataAccessor.RS_TYPE.MAP);
			((Map)contractlist.get(i)).put("paylineState", paylineState.get("FLAG"));
		}
				
		resultMap.put("contractlists", contractlist);	
		return resultMap;
	}
	
	public static Map findSupplGrantForExport (String supplier_id,String supplier_name,String start_date,String end_date) throws Exception{
		
		Map resultMap = new HashMap();
		Map supplGrantDetail= new HashMap();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("supplierid", supplier_id);
		paramMap.put("START_DATE", start_date);
		paramMap.put("END_DATE", end_date);
        
		List rentListSuplTrue;
		int rentCountLien=0;
		int rentCountRepurch=0;
		int eqmtCountRepurch=0;
		int eqmtCountLien=0;
		rentListSuplTrue=(List<Map>) DataAccessor.query("applyCompanyManage.getCustRentSuplTrueListBySuplID", paramMap, DataAccessor.RS_TYPE.LIST);
		for(int j=0;j<rentListSuplTrue.size();j++){
			//1为连保，2为回购 3为回购含灭失
			if (DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==1){
				rentCountLien+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("COUNTI"));
				eqmtCountLien+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("NUM"));
			}else if(DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==2||DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("SUPL_TRUE"))==3){
				rentCountRepurch+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("COUNTI"));
				eqmtCountRepurch+=DataUtil.intUtil(((Map)rentListSuplTrue.get(j)).get("NUM"));
			}
		}
		resultMap.put("rentCountLien",rentCountLien);
		resultMap.put("eqmtCountLien",eqmtCountLien);
		resultMap.put("rentCountRepurch",rentCountRepurch);
		resultMap.put("eqmtCountRepurch",eqmtCountRepurch);
				
		//查询连保授信余额
		Double lienLastPrice=(Double) (SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(paramMap.get("supplierid").toString()))==null ? 0.0 :SelectReportInfo.selectApplyLienLastPrice(Integer.parseInt(paramMap.get("supplierid").toString())));
		if (lienLastPrice<0){
			lienLastPrice=0.0;
		} 
		Double repurchLastPrice=(Double)(SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(paramMap.get("supplierid").toString()))==null ? 0.0 :SelectReportInfo.selectApplyRepurchLastPrice(Integer.parseInt(paramMap.get("supplierid").toString())));
		if (repurchLastPrice<0){
			repurchLastPrice=0.0;
		} 
		resultMap.put("LienLastPrice",Math.round(lienLastPrice));
		//查询回购授信余额
		resultMap.put("RepurchLastPrice",Math.round(repurchLastPrice));
		//查询供应商授信状况
		Map supplGrantPriceMap=(Map) DataAccessor.query("applyCompanyManage.getSuplGrantDetailBySuplID", paramMap, DataAccessor.RS_TYPE.MAP);
		resultMap.put("SuplLienGrantPrice",supplGrantPriceMap.get("LIEN_GRANT_PRICE"));
		resultMap.put("SuplRepurchGrantPrice",supplGrantPriceMap.get("REPURCH_GRANT_PRICE"));
		resultMap.put("supplGrantDetail", supplGrantPriceMap);
		resultMap.put("supplier_name", supplier_name);
		return resultMap;
	}
	
	//Add by Michael 2012 08-23 增加供应商往来函备注
	public static Map getSupplBusinessLetterForExport (String supplier_id,String start_date,String end_date) throws Exception{
		
		Map resultMap = new HashMap();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("supplier_id", supplier_id);
		paramMap.put("START_DATE", start_date);
		paramMap.put("END_DATE", end_date);
		Map supplierBusinessMemo=(Map) DataAccessor.query("applyCompanyManage.getSupplierBusinessMemoByID", paramMap, DataAccessor.RS_TYPE.MAP);
		resultMap.put("supplierBusinessMemo",supplierBusinessMemo.get("MEMO"));
		return resultMap;
	}
	

	/**
	 * Add by Michael 2012 08-28 增加供应商联系人
	 * 设置默认联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void setDefaultLinkMan(Context context){
		Map outputMap = new HashMap();
		Integer rsCount = 0;
		try{
			DataAccessor.getSession().startTransaction();
	
			DataAccessor.getSession().update("supplier.rollBackDefaultLinkMan", context.contextMap);
	
			rsCount = DataAccessor.getSession().update("supplier.setDefaultLinkMan", context.contextMap);
			DataAccessor.getSession().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		//Query all link man
		List supplLinkman = null;
		try {
			supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("supplLinkman", supplLinkman);
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 新建 供应商联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createSupplLinkMan(Context context) {
		Map outputMap = new HashMap();
		List supplLinkman = null;
		Long culm_id = 0l;	
		
		try{	
			culm_id = (Long) DataAccessor.execute("supplier.createSupplLinkMan", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("supplLinkman", supplLinkman);
		Output.jsonOutput(outputMap, context);
	}
	
	
	/**
	 * 查找 供应商联系人 为单条查看
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showSupplLinkManById(Context context) {
		Map outputMap = new HashMap();
		Map culm = new HashMap();
		
		/*-------- data access --------*/		
		try{
			
			culm = (Map)DataAccessor.query("supplier.readSupplLinkManById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("culm", culm);
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 修改  供应商联系人 为单条查看
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateSupplLinkManById(Context context) {
		Map outputMap = new HashMap();
		List supplLinkman = null;
		
		/*-------- data access --------*/		
		try{	
			DataAccessor.execute("supplier.updateSupplLinkManById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//增加记录日志 zhangbo0718
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null, "供应商联系人信息", "供应商联系人信息",  null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")将供应商联系人信息变更为：联系人："+ context.contextMap.get("link_name")+"，手机："+ context.contextMap.get("link_mobile"),
		   		 	 1,  DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()), DataUtil.longUtil(0), context.getRequest().getRemoteAddr());
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("supplLinkman", supplLinkman);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 作废/启用联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void operteSuplLinkManStatus(Context context) {
		Map outputMap = new HashMap();
		
		Integer rsCount = 0;
		
		try{	
			rsCount = (Integer) DataAccessor.execute("supplier.operteSupplLinkManStatus", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		List supplLinkman = null;
		try {
			supplLinkman = (List)DataAccessor.query("supplier.querySupplLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("supplLinkman", supplLinkman);
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 查询供应商 与联系人
	 * suppl_id
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void querySupplLinkerInfo(Context context){
	    
	    Map outputMap = new HashMap();
	    List errList = context.errList;
	    List supplierLinker = new  ArrayList();
	    
	    if(errList.isEmpty()){
		
		try {
		   
			supplierLinker = DataAccessor.getSession().queryForList("supplier.querySuppllinInfo", context.contextMap);
		
		} catch (SQLException e) {
		
		    errList.add("查询供应商 与联系人 信息出错 : "+e.getMessage()); 
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} 
	    }
	    
	    if(errList.isEmpty()){
		outputMap.put("supplierLinker", supplierLinker);
		Output.jsonOutput(outputMap, context);
		
	    }else{
		outputMap.put("errList",  errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	/**
	 * 保存联系记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createSupplLinkRecord(Context context) {

	    Map outputMap = new HashMap();
	    List errList = context.getErrList();
	    
		Map supplLinkRecord = new HashMap();
		Long culr_id = 0l;	
	if(errList.isEmpty()){	
		
		try{	
			culr_id =  (Long) DataAccessor.execute("supplier.createSupplLINKRecord", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			context.contextMap.put("CULR_ID", culr_id);
			supplLinkRecord = (Map)DataAccessor.query("supplier.readSupplLinkRecord",context.contextMap , DataAccessor.RS_TYPE.MAP);
		}catch(Exception e){
	    	errList.add("保存联系记录出错 ："+e.getMessage());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	}
	if(errList.isEmpty()){	
	   
		outputMap.put("supplLinkRecord", supplLinkRecord);
		Output.jsonOutput(outputMap, context);
	}else {
	    	outputMap.put("errList", errList);
	    	Output.jspOutput(outputMap, context, "/error.jsp");
    	}
	}
	
	/**
	 * 删除一条记录
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteLinkrecord(Context context){
	    
	    List errList = context.getErrList();
	    Map outputMap = new HashMap();
	    
	    int result = 0;
	    if(errList.isEmpty()){
		
		try {
		    result = (Integer) DataAccessor.execute("supplier.deleteLinkrecord", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		    outputMap.put("result", result);
		    
		} catch (Exception e) {
		    errList.add("删除联系人记录出错 ： "+e.getMessage());	
		    e.printStackTrace();
		    LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    }
	    
	    if(errList.isEmpty()){
		
		Output.jsonOutput(outputMap, context);
		
	    }else{
		
		outputMap.put("errList", errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	    }
	}
	
	public void updateMaintainUserId(Context context) {
		
		try {
			DataAccessor.execute("supplier.updateMaintainUserId",context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
		}catch (Exception e) {
			logger.debug("更新供应商第"+context.contextMap.get("flag")+"维护人出错!");
			e.printStackTrace();
			Output.jsonFlageOutput(false,context);
		}
		Output.jsonFlageOutput(true,context);
	}
	
	public void getEmployeeList(Context context) {
		List<Map<String,String>> employeeList=null;
		
		try {
			employeeList=(List<Map<String,String>>)DataAccessor.query("supplier.getEmployeeList",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Output.jsonArrayListOutput(employeeList,context);
	}
	
	public void getAreaMaintenance(Context context) {
		
		List<Map<String,Object>> resultList=null;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<Map<String,Object>> employeeList=null;
		List<CustomerCaseTo> deptList=null;
		try {
			
			resultList=(List<Map<String,Object>>)DataAccessor.query("supplier.getAreaMaintenance",context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("resultList",resultList);
			
			//获得第一维护人第二维护人的选择List
			employeeList=(List<Map<String,Object>>)DataAccessor.query("supplier.getEmployeeList",context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("employeeList",employeeList);
			
			//获得办事处
			deptList=(List<CustomerCaseTo>)DataAccessor.query("businessReport.getDeptList",context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("deptList",deptList);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Output.jsonOutput(outputMap,context);
	}
	
	public void updateAreaMaintenance(Context context) {
		
		SqlMapClient sqlMapper=null;
		
		sqlMapper=DataAccessor.getSession();
		
		boolean flag=false;
		try {
			
			String deptIds=(String)context.contextMap.get("DEPT_ID");
			String firstIds=(String)context.contextMap.get("FIRST_MAINTAIN_USER_ID");
			String secondIds=(String)context.contextMap.get("SECOND_MAINTAIN_USER_ID");
			
			String[] DEPT_ID=HTMLUtil.getParameterValues(context.request,"DEPT_ID","");
			String[] FIRST_MAINTAIN_USER_ID=HTMLUtil.getParameterValues(context.request,"FIRST_MAINTAIN_USER_ID","");
			String[] SECOND_MAINTAIN_USER_ID=HTMLUtil.getParameterValues(context.request,"SECOND_MAINTAIN_USER_ID","");
			
			DEPT_ID=deptIds.substring(0,deptIds.length()-1).split(",");
			FIRST_MAINTAIN_USER_ID=firstIds.substring(0,firstIds.length()-1).split(",");
			SECOND_MAINTAIN_USER_ID=secondIds.substring(0,secondIds.length()-1).split(",");
			
			sqlMapper.startTransaction();
			
			sqlMapper.update("supplier.updateAreaMaintenance",context.contextMap);
			
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("suppl_id",context.contextMap.get("suppl_id"));
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			for(int i=0;i<DEPT_ID.length;i++) {
				param.put("DEPT_ID",DEPT_ID[i]);
				param.put("FIRST_MAINTAIN_USER_ID",FIRST_MAINTAIN_USER_ID[i]);
				param.put("SECOND_MAINTAIN_USER_ID",SECOND_MAINTAIN_USER_ID[i]);
				sqlMapper.insert("supplier.insertAreaMaintenance",param);
			}
			
			sqlMapper.commitTransaction();
			
			flag=true;
			
		} catch(Exception e) {
			logger.debug("维护供应商地区别出错!");
			Output.jsonFlageOutput(flag,context);
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		Output.jsonFlageOutput(flag,context);
	}
}
