package com.brick.customer.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.command.BaseCommand;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.coderule.service.CodeRule;
import com.brick.customer.util.CustomerInfoExcel;
import com.brick.customerVisit.service.CustomerVisitService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.supplier.to.LogMsgTo;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.brick.util.web.JsonUtils;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 2010-04-12 customer
 * 
 * @author wujw
 * 
 */
public class Customer extends BaseCommand {
	Log logger = LogFactory.getLog(this.getClass());

	private CustomerVisitService customerVisitService;
	
	private CustomerService customerService;

	private List<LogMsgTo> msgs;

	public CustomerVisitService getCustomerVisitService() {
		return customerVisitService;
	}

	public void setCustomerVisitService(CustomerVisitService customerVisitService) {
		this.customerVisitService = customerVisitService;
	}
	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * 管现页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void query(Context context) {

		//super.commonQuery("customer.query", context, DataAccessor.RS_TYPE.PAGED, Output.OUTPUT_TYPE.JSP, "/customer/query.jsp");

		Map outputMap = new HashMap();
		DataWrap dw = null;
		List custLevel = null;
		Map paramMap = new HashMap();
		Map rsMap = null;
		paramMap.put("id", context.contextMap.get("s_employeeId"));
		List<String> permissionList=null;
		/*-------- data access --------*/
		try {
			rsMap = (Map) DataAccessor.query("employee.getEmpInforById",
					paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("NODE", rsMap.get("NODE"));
			
			/*context.contextMap.put("dataType", "承租人级别");remove by ShenQi 2012-12-18 按需求删除栏位
			custLevel = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);*/

			dw = (DataWrap) DataAccessor.query("customer.query", context.contextMap, DataAccessor.RS_TYPE.PAGED);
			
			permissionList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		
		
		boolean add=false;
		boolean modify=false;
		boolean addContractMan=false;
		for(int i=0;permissionList!=null&&i<permissionList.size();i++) {
			if("customerAdd".equalsIgnoreCase((String)permissionList.get(i))) {
				add=true;
			} else if("customerModify".equalsIgnoreCase((String)permissionList.get(i))) {
				modify=true;
			} else if("customerAddContractMan".equalsIgnoreCase((String)permissionList.get(i))) {
				addContractMan=true;
			}
		}
		
		boolean custShift = baseService.checkAccessForResource("custShift", String.valueOf(context.contextMap.get("s_employeeId")));
		
		/*-------- output --------*/
		outputMap.put("add", add);
		outputMap.put("modify", modify);
		outputMap.put("addContractMan", addContractMan);
		outputMap.put("dw", dw);
		outputMap.put("custLevel", custLevel);
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		outputMap.put("cust_type", context.contextMap.get("cust_type"));
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		outputMap.put("custShift", custShift);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap, context, "/customer/query.jsp");

	}
	
	public void custShift(Context context){
		String user_id = (String) context.contextMap.get("newName");
		String cust_id = (String) context.contextMap.get("cust_id");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user_id", user_id);
		paramMap.put("cust_id", cust_id);
		baseService.update("customer.custShift", paramMap);
		query(context);
	}
	
	/**
	 * 初始化新建 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initCreate(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List provinces = null;
		// 是否使用数据字典
		List custLevel = null;
		
		List<Map> companyList=null;
		try {
			provinces = (List) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			context.contextMap.put("dataType", "承租人级别");
			custLevel = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			outputMap.put("companyList", companyList);

		
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("provinces", provinces);
		outputMap.put("custLevel", custLevel);
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		Output.jspOutput(outputMap, context, "/customer/createCust.jsp");
	}
	/**
	 * 检验承租人是否存在，返回存在的个数
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkCustomer(Context context) {

		Map outputMap = new HashMap();
		Map rs = null;
		Map rss = null;
		Integer cust_type = HTMLUtil.getIntParam(context.request, "cust_type", -1);
		context.contextMap.put("status", 0) ;
		try {
			if (cust_type == 0) {
				rs = (Map) DataAccessor.query("customer.checkCustomerNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} else if (cust_type == 1) {
				rs = (Map) DataAccessor.query("customer.checkCustomerCorp", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} else {
				rs = new HashMap();
				rs.put("COUNT", "undefined");
			}
			String VIRTUAL_CODE=context.contextMap.get("VIRTUAL_CODE")==null ? "" :context.contextMap.get("VIRTUAL_CODE").toString();
			context.contextMap.put("VIRTUAL_CODE", VIRTUAL_CODE);
			if(!"".equals(VIRTUAL_CODE)){
				rss = (Map) DataAccessor.query("customer.checkVIRTUAL", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}else{
				rss = new HashMap();
				rss.put("COUNTVIRTUAL","VIRTUAL_CODE is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("rs", rs);
		outputMap.put("rss", rss);

		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 创建承租人表头基本信息
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCust(Context context) {
		@SuppressWarnings("unused")
		Long cust_id = 0l;	
		
		Integer cust_type = HTMLUtil.getIntParam(context.request, "cust_type", -1);
		
		//承租人编码规则
		String cust_code = CodeRule.generateCustCode(context);
		context.contextMap.put("cust_code", cust_code);
		
		//Add by Michael 2012 3-8 增加客户虚拟账号 ,虚拟账号为88 +客户编号
		//context.contextMap.put("virtual_code", "88"+cust_code);
		
		
		/*-------- data access --------*/		
		try{	
			if (cust_type == 0) {
				cust_id = (Long) DataAccessor.execute("customer.createCustNatu", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);			
			} else if (cust_type == 1) {
				cust_id = (Long) DataAccessor.execute("customer.createCustCrop", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);			
			} else {
				// 数据传输错误
			}
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
				
		/*-------- output --------*/
		//Output.jspSendRedirect(context, "defaultDispatcher?__action=customer.updateCust&cust_id="+cust_id+"&cust_type="+cust_type);
		if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
			Output.jspSendRedirect(context, "defaultDispatcher?__action=customer.query&isSalesDesk=Y");
		} else {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=customer.query");
		}
		// Output.jspOutput(outputMap, context, "/servlet/defaultDispatcher?__action=customer.updateCust&cust_id="+cust_id+"&cust_type="+cust_type);
	}
	/**
	 * 修改自然人详细信息 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustomerNatu(Context context){
		Map outputMap = new HashMap();
		Integer rsCount = 0;

		try{	
			//客户维护日志记录 yangliu added 2013/12/23
			msgs = new ArrayList<LogMsgTo>();
			SqlMapClient sqlMapper = DataAccessor.getSession();
			this.makeCustUpdateLogMessage(context, sqlMapper);
			String operationMsgs = JsonUtils.list2json(msgs);
			
			//如果operationMsgs为空，则其内容为[],判断length>2则为非空
			if(operationMsgs.length() > 2){
		    	Map operationLogs = new HashMap();
		    	operationLogs.put("OPERATOR_TABLE_ID", context.contextMap.get("cust_id"));
		    	operationLogs.put("OPERATOR_TABLE_NAME", "T_CUST_CUSTOMER");
		    	operationLogs.put("OPERATION_MESSAGE", operationMsgs);
		    	operationLogs.put("OPERATOR_ID", context.contextMap.get("s_employeeId"));
		    	operationLogs.put("OPERATOR_IP", context.getRequest().getRemoteAddr());
		    	operationLogs.put("MEMO", "");
		    	sqlMapper.insert("supplier.insertOperationLogs", operationLogs);
			}
			
			rsCount = (Integer) DataAccessor.execute("customer.createCustomerNatu", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 修改法人详细信息 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustomerCorp(Context context){
		Map outputMap = new HashMap();
		Integer rsCount = 0;

		try{	
			//客户维护日志记录 yangliu added 2013/12/23
			msgs = new ArrayList<LogMsgTo>();
			SqlMapClient sqlMapper = DataAccessor.getSession();
			this.makeCustUpdateLogMessage(context, sqlMapper);
			String operationMsgs = JsonUtils.list2json(msgs);
			
			//如果operationMsgs为空，则其内容为[],判断length>2则为非空
			if(operationMsgs.length() > 2){
		    	Map operationLogs = new HashMap();
		    	operationLogs.put("OPERATOR_TABLE_ID", context.contextMap.get("cust_id"));
		    	operationLogs.put("OPERATOR_TABLE_NAME", "T_CUST_CUSTOMER");
		    	operationLogs.put("OPERATION_MESSAGE", operationMsgs);
		    	operationLogs.put("OPERATOR_ID", context.contextMap.get("s_employeeId"));
		    	operationLogs.put("OPERATOR_IP", context.getRequest().getRemoteAddr());
		    	operationLogs.put("MEMO", "");
		    	sqlMapper.insert("supplier.insertOperationLogs", operationLogs);
			}
			
			rsCount = (Integer) DataAccessor.execute("customer.createCustomerCorp", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 新建 承租人联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createCustLinkMan(Context context) {
		Map outputMap = new HashMap();
		/*2011/12/21 Yang Yun Update to Query all link man. Start*/
		//Map custLinkman = null;
		List custLinkman = null;
		/*2011/12/21 Yang Yun Update to Query all link man. Start*/
		Long culm_id = 0l;	
		
		/*-------- data access --------*/		
		try{	
			culm_id = (Long) DataAccessor.execute("customer.createCustLinkMan", context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);
			/*2011/12/21 Yang Yun Update to Query all link man. Start*/
			//context.contextMap.put("culm_id", culm_id);
			//custLinkman = (Map)DataAccessor.query("customer.readCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.MAP);
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			/*2011/12/21 Yang Yun Update to Query all link man. End*/
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("custLinkman", custLinkman);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 查找 承租人联系人 为单条查看
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void showCustLinkManById(Context context) {
		Map outputMap = new HashMap();
		Map culm = new HashMap();
		
		/*-------- data access --------*/		
		try{	
			culm = (Map)DataAccessor.query("customer.readCustLinkManById", context.contextMap, DataAccessor.RS_TYPE.MAP);
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("culm", culm);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 2012/01/20 Yang Yun
	 * 查询是否有默认联系人
	 * 用于验证
	 */
	public void checkDefaultLinkMan(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		Integer defaultLinkMan = null;
		try {
			defaultLinkMan = (Integer) DataAccessor.query("customer.checkDefaultLinkMan", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		outputMap.put("defaultLinkMan", defaultLinkMan);
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 修改 承租人联系人 为单条查看
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateCustLinkManById(Context context) {
		Map outputMap = new HashMap();
		List custLinkman = null;
		
		/*-------- data access --------*/		
		try{	
			DataAccessor.execute("customer.updateCustLinkManById", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		/*-------- output --------*/
		outputMap.put("custLinkman", custLinkman);
		Output.jsonOutput(outputMap, context);
	}
	
	/*2011/12/21 Yang Yun Add set default link man. Start*/
	/**
	 * 设置默认联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void setDefaultLinkMan(Context context){
		Map outputMap = new HashMap();
		Integer rsCount = 0;
		try{
			DataAccessor.getSession().startTransaction();
			//Roll back the default link man.
			DataAccessor.getSession().update("customer.rollBackDefaultLinkMan", context.contextMap);
			//Set default link man
			rsCount = DataAccessor.getSession().update("customer.setDefaultLinkMan", context.contextMap);
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
		List custLinkman = null;
		try {
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("custLinkman", custLinkman);
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}
	/*2011/12/21 Yang Yun Add set default link man. End*/
	
	/**
	 * 作废/启用联系人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void operteCustLinkManStatus(Context context) {
		Map outputMap = new HashMap();
		
		Integer rsCount = 0;
		
		try{	
			rsCount = (Integer) DataAccessor.execute("customer.operteCustLinkManStatus", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		/*2011/12/21 Yang Yun Update to Query all link man. Start*/
		//Query all link man
		List custLinkman = null;
		try {
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("custLinkman", custLinkman);
		/*2011/12/21 Yang Yun Update to Query all link man. Start*/
		outputMap.put("rsCount", rsCount);
		Output.jsonOutput(outputMap, context);
	}
	/**
	 * 查看自然人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void readCust(Context context) {
		Map outputMap = new HashMap();
		Map cust = null;
		List custLinkman = null;
		
		List custLinkrecord = null;
		List custLinkcare = null;
		List custLinkExpense = null;
		
		List provinces = null;
		List citys = null;
		Map custType=null;
		List creditList = null;
		// 是否使用数据字典
		List custLevel = null;
		List corpTypeList=null;
		
		Integer cust_type = HTMLUtil.getIntParam(context.request, "cust_type", -1);
		//Long cust_id = HTMLUtil.getLongParam(context.request, "cust_id", 0l);
		
		try{	
			// 0自然人  1法人
			if (cust_type == 0) {
				cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} else if (cust_type == 1) {
				cust = (Map) DataAccessor.query("customer.readCustCorp", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}
			outputMap.put("cust", cust);
			
			//该客户所有操作日志
			context.contextMap.put("OPERATOR_TABLE_NAME", "T_CUST_CUSTOMER");
			context.contextMap.put("id", cust.get("CUST_ID"));
			List logs = (List) DataAccessor.query( "supplier.queryOperationLogs", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("logs", logs);
			
			//取客户状态
			custType=(Map)DataAccessor.query("customer.readCustState", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("custType", custType);
			
			// 联系人
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkmanList", custLinkman);
			
			//联系记录
			custLinkrecord = (List)DataAccessor.query("customer.queryCustLinkRecord", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkRecord", custLinkrecord);
			
			//客户关怀
			custLinkcare = (List)DataAccessor.query("customer.queryCustLinkcare", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkcare", custLinkcare);
			
			//费用
			custLinkExpense = (List)DataAccessor.query("customer.queryCustLinkExpense", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkExpense", custLinkExpense);
			
			// 取省份
			provinces = (List) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("provinces", provinces);
			
			// 取市
			Long provincesId= Long.parseLong(String.valueOf(cust.get("PROVINCE_ID")));
			context.contextMap.put("provinceId", provincesId);
			citys = (List) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("citys", citys);
			
			// 数据字典 承租人级别
			context.contextMap.put("dataType", "承租人级别");
			custLevel = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLevel", custLevel);
			// 数据字典 公司性质
			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);
			
			//该客户所有报告
			creditList = (List) DataAccessor.query( "customer.queryCredit", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditList", creditList);
			
			//取得客户经理
			context.contextMap.put("CREATE_ID", cust.get("CREATE_USER_ID")) ;
			outputMap.put("createName", DataAccessor.query("customer.getUserUserName", context.contextMap, DataAccessor.RS_TYPE.MAP));
			//2012/03/28 Yang Yun 查询承租人的行业别
			outputMap.put("trade_type_list", DataAccessor.query("customer.getTradeTypeByCust", cust, RS_TYPE.LIST));
			
			List currencys = DictionaryUtil.getDictionary("货币类型");
			outputMap.put("currencys",currencys);
		} catch (Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		// 0自然人  1法人
		if (cust_type == 0) {
			Output.jspOutput(outputMap, context, "/customer/readCustNatu.jsp");
		} else if (cust_type == 1) {
			Output.jspOutput(outputMap, context, "/customer/readCustCorp.jsp");
		}
	}
	/**
	 * 更新承租人
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateCust(Context context) {
		
		Map outputMap = new HashMap();
		Map cust = null;
		List custLinkman = null;
		List custLinkrecord = null;
		List custLinkcare = null;
		List custLinkExpense = null;
		List provinces = null;
		List citys = null;
		List areas = null;
		Map custType=null;
		// 是否使用数据字典
		List custLevel = null;
		List creditList = null;
		List corpTypeList=null;
		Integer cust_type = HTMLUtil.getIntParam(context.request, "cust_type", -1);
		//Long cust_id = HTMLUtil.getLongParam(context.request, "cust_id", 0l);
		
		
		try{	
			// 0自然人  1法人
			if (cust_type == 0) {
				cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} else if (cust_type == 1) {
				cust = (Map) DataAccessor.query("customer.readCustCorp", context.contextMap, DataAccessor.RS_TYPE.MAP);
				/*
				 * Add by Michael 2012 4-23 限制组织机构代码证输入
				 */
				String corpOragnizationCode=String.valueOf(cust.get("CORP_ORAGNIZATION_CODE"));
				String[] corpOragnizationCodeArry=corpOragnizationCode.split("-");
				if (corpOragnizationCodeArry.length==2){
					cust.put("CORP_ORAGNIZATION_CODE_BEFORE", corpOragnizationCodeArry[0]);
					cust.put("CORP_ORAGNIZATION_CODE_END", corpOragnizationCodeArry[1]);
				}else{
					cust.put("CORP_ORAGNIZATION_CODE_BEFORE", corpOragnizationCode);
				}
					
			}
			outputMap.put("cust", cust);
			
			//该客户所有操作日志
			context.contextMap.put("OPERATOR_TABLE_NAME", "T_CUST_CUSTOMER");
			context.contextMap.put("id", cust.get("CUST_ID"));
			List logs = (List) DataAccessor.query( "supplier.queryOperationLogs", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("logs", logs);
			
			//取客户状态
			custType=(Map)DataAccessor.query("customer.readCustState", context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("custType", custType);
			
			// 联系人
			custLinkman = (List)DataAccessor.query("customer.queryCustLinkMan", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkmanList", custLinkman);
			
			//联系记录
			custLinkrecord = (List)DataAccessor.query("customer.queryCustLinkRecord", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkRecord", custLinkrecord);
			
			//客户关怀
			custLinkcare = (List)DataAccessor.query("customer.queryCustLinkcare", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkcare", custLinkcare);
			
			//费用
			custLinkExpense = (List)DataAccessor.query("customer.queryCustLinkExpense", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLinkExpense", custLinkExpense);
			
			// 取省份
			provinces = (List) DataAccessor.query( "area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("provinces", provinces);
			
			// 取市
			Long provincesId= Long.parseLong(String.valueOf(cust.get("PROVINCE_ID")));
			context.contextMap.put("provinceId", provincesId);
			citys = (List) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("citys", citys);
			
			//去地区getAreaByCityId
			Long cityId= Long.parseLong(String.valueOf(cust.get("CITY_ID")));
			context.contextMap.put("cityId", cityId);
			areas = (List) DataAccessor.query("area.getAreaByCityId", context.contextMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("areas", areas);
			
			// 数据字典 承租人级别
			context.contextMap.put("dataType", "承租人级别");
			custLevel = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("custLevel", custLevel);
			
			// 数据字典 公司性质
			context.contextMap.put("dataType", "企业类型");
			corpTypeList = (List) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("corpTypeList", corpTypeList);
			
		
			//该客户所有报告
			creditList = (List) DataAccessor.query( "customer.queryCredit", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditList", creditList);
			
			//取得客户经理
			context.contextMap.put("CREATE_ID", cust.get("CREATE_USER_ID")) ;
			outputMap.put("createName", DataAccessor.query("customer.getUserUserName", context.contextMap, DataAccessor.RS_TYPE.MAP));
			
			//2012/03/27 Yang Yun 增加行业别
			context.contextMap.put("trade_level", 1);
			outputMap.put("trade_type_first_list", DataAccessor.query("customer.getAllFirstTradeType", context.contextMap, RS_TYPE.LIST));
			//2012/03/28 Yang Yun 查询承租人的行业别
			outputMap.put("trade_type_list", DataAccessor.query("customer.getTradeTypeByCust", cust, RS_TYPE.LIST));
			
			
			List currencys = DictionaryUtil.getDictionary("货币类型");
			outputMap.put("currencys",currencys);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		outputMap.put("s_employeeName",context.contextMap.get("s_employeeName"));
		outputMap.put("date",DateUtil.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));
		
		List<Map<String,Object>> intentList=null;
		List<Map<String,String>> importantRecordList=null;

		Map<String,String> param1=new HashMap<String,String>();

		try {
			param1.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param1);

			param1.put("dataType","重点记录");
			param1.put("shortName","1");
			importantRecordList=this.customerVisitService.queryDataDictionary1(param1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("intentList",intentList);
		outputMap.put("importRecordList",importantRecordList);
		outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
		// 0自然人  1法人
		if (cust_type == 0) {

			Output.jspOutput(outputMap, context, "/customer/createCustNatu.jsp");
		
		} else if (cust_type == 1) {
		
			Output.jspOutput(outputMap, context, "/customer/createCustCorp.jsp");
		
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
	
	public void getIntentAndImportRecord(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		Map<String,String> param1=new HashMap<String,String>();

		List<Map<String,Object>> intentList=null;
		List<Map<String,String>> importantRecordList=null;
		
		try {
			param1.put("dataType","拜访目的");
			intentList=this.customerVisitService.queryDataDictionary(param1);

			param1.put("dataType","重点记录");
			param1.put("shortName","1");
			importantRecordList=this.customerVisitService.queryDataDictionary1(param1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputMap.put("intentList",intentList);
		outputMap.put("importRecordList",importantRecordList);
		outputMap.put("isSalesDesk",context.contextMap.get("isSalesDesk"));
		outputMap.put("date",DateUtil.dateToString(Calendar.getInstance().getTime(),"yyyy-MM-dd"));
		
		Output.jsonOutput(outputMap,context);
	}
	
	/**
	 * Add By Michael 2011 11/29 
	 * 删除承租人时检查此客户是否有关联到合同，有关联合同的不允许删除
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void checkCustContractExist(Context context) {
		List errList = context.errList ;
		Map outputMap = new HashMap() ;
		try{
			int count = (Integer) DataAccessor.query("customer.checkCustContractExist", context.contextMap, RS_TYPE.OBJECT) ;
			if(count > 0){
				outputMap.put("count", 1) ;
			} else {
				outputMap.put("count", 0) ;
			}
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
			errList.add("检核客户关系合同时出错!请联系管理员") ;
		}
		if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("error", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	/**
	 * 删除承租人
	 * @param context
	 */
	public void deleteCust(Context context) {

		try{	

			//DataAccessor.execute("customer.deleteCust", context.contextMap, DataAccessor.OPERATION_TYPE.DELETE);
			DataAccessor.execute("customer.updateStatusCust", context.contextMap, DataAccessor.OPERATION_TYPE.UPDATE);
			
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		
		Output.jspSendRedirect(context, "defaultDispatcher?__action=customer.query");
	}	
	
	/**
	 * 2012/03/27 Yang Yun 
	 * 查询行业别
	 */
	public void getTradeType(Context context){
		logger.info("============================================getTradeType.Start==============================================");
		try {
			Output.jsonArrayOutput((List<Map>) DataAccessor.query("customer.getAllFirstTradeType", context.contextMap, RS_TYPE.LIST), context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("============================================getTradeType.End================================================");
	}
	
	
	public void getCustVirtualCode(Context context) {

		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		if(context.contextMap.get("com_type")==null){
			context.contextMap.put("com_type", 1);
		}
		try {
			dw = baseService.queryForListWithPaging("customer.getCustomerVirtualCode", context.contextMap, "CUST_NAME");
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("dw", dw);
		outputMap.put("cust_type", context.contextMap.get("cust_type"));
		outputMap.put("com_type", context.contextMap.get("com_type"));
		outputMap.put("searchValue", context.contextMap.get("searchValue"));
		Output.jspOutput(outputMap, context, "/customer/queryCustVirtualCode.jsp");

	}
	
	
	public void getExportCustVirtualCodeLog(Context context) {

		Map outputMap = new HashMap();
		DataWrap dw = null;
		
		try {
			dw = (DataWrap) DataAccessor.query("customer.getExportCustVirtualLog", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("dw", dw);

		Output.jspOutput(outputMap, context, "/customer/queryCustVirtualCodeExportLog.jsp");

	}
	
	public void queryCustLinkerManByFinance(Context context) {

		Map outputMap = new HashMap();
		DataWrap dw = null;
		/*-------- data access --------*/
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
			if(context.contextMap.get("QUERY_END_DATE") == null){
				context.contextMap.put("QUERY_END_DATE", sf.format(new Date())) ;
			}
			
			if(context.contextMap.get("QUERY_START_DATE") == null){
				context.contextMap.put("QUERY_START_DATE", "2008-01-01") ;
			}
			
			outputMap.put("QUERY_START_DATE", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("QUERY_START_DATE").toString())) ;	
			outputMap.put("QUERY_END_DATE", new SimpleDateFormat("yyyy-MM-dd").parse(context.contextMap.get("QUERY_END_DATE").toString())) ;
			
			dw = (DataWrap) DataAccessor.query("customer.getCustLinkManLinker", context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap, context, "/customer/queryCustLinkerManByFinance.jsp");

	}
	
	//Add by Michael 2012 5-14  通过日期  查询报表
	public static Map<String,Object> queryCustLinkerManByFinance(String query_start_date,String query_end_date,String companyCode) throws Exception {
		List custLinkManLinker = new ArrayList();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		if(query_end_date == null){
			query_end_date= sf.format(new Date()) ;
		}
		
		if(query_start_date == null){
			query_start_date="2008-01-01" ;
		}
		paramMap.put("QUERY_START_DATE", new SimpleDateFormat("yyyy-MM-dd").parse(query_start_date.toString()));
		paramMap.put("QUERY_END_DATE", new SimpleDateFormat("yyyy-MM-dd").parse(query_end_date.toString()));
		paramMap.put("companyCode", companyCode);
		custLinkManLinker = (List) DataAccessor.query("customer.getCustLinkManLinker", paramMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("custLinkManLinker", custLinkManLinker);
		return resultMap;
	}
	
	/*
	 * Add by Michael 2012-09-10
	 * 客户缴息情况查询
	 */
	public static Map<String,Object>  queryCustRentDunList()throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map paramMap=null;
		List<Map> allRecpIdList=null;
		/*
		 * 获取客户的List（已拨款）
		 * 获取客户的支付表的List
		 */
		try {
			//后去所有已拨款客户的Recp_id 
			allRecpIdList=(List<Map>) DataAccessor.query("customer.queryAllFinanceRecpID", paramMap, DataAccessor.RS_TYPE.LIST);
			List dunList;
			Map settleMap = null;
			int sevenDay=0;// 1~7 天
			int fifteenDay=0; //8~15天
			int thirtyDay=0; // 16~30 天
			int overThirtyDay=0; // 31天以上
			for (Map recpIDMap : allRecpIdList) {
				sevenDay=0;
				fifteenDay=0;
				thirtyDay=0;
				overThirtyDay=0;
				dunList=(List<Map>) DataAccessor.query("applyCompanyManage.getCustRentDunDayByRecpID", recpIDMap, DataAccessor.RS_TYPE.LIST);
				
				for(int j=0;j<dunList.size();j++){
					if(DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=1 && DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))<=7){
						sevenDay+=1;
					}else if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=8 && DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))<=15){
						fifteenDay+=1;
					}else if(DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>=16 && DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))<=30){
						thirtyDay+=1;
					}else if (DataUtil.intUtil(((Map)dunList.get(j)).get("DUN_DAY"))>30){
						overThirtyDay+=1;
					}
				}
				recpIDMap.put("sevenDay", sevenDay);
				recpIDMap.put("fifteenDay", fifteenDay);
				recpIDMap.put("thirtyDay", thirtyDay);
				recpIDMap.put("overThirtyDay", overThirtyDay);
				//查询实际剩余本金
				paramMap=new HashMap();
				paramMap.put("zujin", "租金") ;				
				paramMap.put("zujinfaxi", "租金罚息") ;
				paramMap.put("sblgj", "设备留购价") ;
				paramMap.put("RECP_ID", recpIDMap.get("RECP_ID")) ;
				settleMap = (Map) DataAccessor.query("settleManage.selectSettlePrice", paramMap,DataAccessor.RS_TYPE.MAP);
				recpIDMap.put("SUM_OWN_PRICE", settleMap.get("SUM_OWN_PRICE")==null?0:settleMap.get("SUM_OWN_PRICE"));
				recpIDMap.put("REAL_PERIOD", settleMap.get("REAL_PERIOD"));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("allRecpIdList", allRecpIdList);
		return resultMap;
	}
	
	/**
	 * 拼接客户资料修改日志记录信息
	 * @param context
	 * @param sqlMapper
	 * @return	修改记录信息
	 * @throws Exception
	 */
	private void makeCustUpdateLogMessage(Context context, SqlMapClient sqlMapper) throws Exception {
		//表字段中英文对照
    	Map<String, String> englishToChineseNames = new HashMap<String, String>();
    	englishToChineseNames.put("cust_level", "承租人分类");
    	englishToChineseNames.put("trade_type", "行业类别");
    	englishToChineseNames.put("natu_idcard", "身份证号码");
    	englishToChineseNames.put("cust_area", "承租人所在省市");
    	englishToChineseNames.put("province_id", "省");
    	englishToChineseNames.put("city_id", "市");
    	englishToChineseNames.put("area_id", "区");
    	englishToChineseNames.put("x_point", "承租人所在经纬度X");
    	englishToChineseNames.put("y_point", "承租人所在经纬度Y");
    	
    	/*----------------------法人----------------------*/
    	englishToChineseNames.put("corp_oragnization_code", "组织机构代码号");
    	englishToChineseNames.put("corp_enterprises_property", "企业类型");
    	englishToChineseNames.put("corp_setup_date", "成立日期");
    	englishToChineseNames.put("corp_registe_capital", "注册资本");
    	englishToChineseNames.put("corp_paiclup_capital", "实收资本");
    	englishToChineseNames.put("corp_business_license", "营业执照注册号");
    	englishToChineseNames.put("tax_code", "税务编号");
    	englishToChineseNames.put("corp_tax_code", "税务登记号");
    	englishToChineseNames.put("corp_period_validity", "有效期");
    	englishToChineseNames.put("corp_company_zip", "公司邮编");
    	englishToChineseNames.put("corp_registe_address", "注册地址");
    	englishToChineseNames.put("corp_work_address", "公司办公地址");
    	englishToChineseNames.put("corp_business_range", "经营范围");
    	englishToChineseNames.put("corp_company_website", "公司网址");
    	/*----------------------法人----------------------*/
    	
    	englishToChineseNames.put("natu_gender", "性别");
    	englishToChineseNames.put("natu_age", "年龄");
    	englishToChineseNames.put("natu_mobile", "手机号码");
    	englishToChineseNames.put("natu_phone", "联系电话(家庭座机)");
    	englishToChineseNames.put("natu_zip", "邮编");
    	englishToChineseNames.put("natu_idcard_address", "身份证地址");
    	englishToChineseNames.put("natu_home_address", "家庭常住地址");
    	englishToChineseNames.put("natu_work_units", "工作单位");
    	englishToChineseNames.put("natu_work_address", "单位地址");
    	englishToChineseNames.put("corp_company_email", "公司邮箱");
    	
    	/*----------------------法人----------------------*/
    	englishToChineseNames.put("corp_other_massage", "其他信息");
    	englishToChineseNames.put("corp_head_signature", "法人信息(法定代表人)");
    	englishToChineseNames.put("corp_hs_idcard", "法人信息(法人身份证号码)");
    	englishToChineseNames.put("corp_hs_link_mode", "法人信息(法人联系方式)");
    	englishToChineseNames.put("corp_hs_home_address", "法人信息(法人代表住址)");
    	englishToChineseNames.put("corp_remark", "备注");
    	/*----------------------法人----------------------*/
    	
    	englishToChineseNames.put("natu_mate_name", "配偶信息(配偶姓名)");
    	englishToChineseNames.put("natu_mate_idcard", "配偶信息(身份证号码)");
    	englishToChineseNames.put("natu_mate_mobile", "配偶信息(手机号码)");
    	englishToChineseNames.put("natu_mate_idcard_address", "配偶信息(身份证地址)");
    	englishToChineseNames.put("natu_mate_work_units", "配偶信息(工作单位)");
    	englishToChineseNames.put("natu_mate_work_address", "配偶信息(单位地址)");
    	englishToChineseNames.put("remark", "配偶信息(备注)");

    	//存放value对应中文名
    	Map<String, String> nameByType = new HashMap<String, String>();
    	
    	//查询修改前客户信息
    	Map<String,Object> cust = null;
    	boolean isCorp = context.contextMap.get("corp_tax_code") != null;
		if (!isCorp) {
			cust = (Map) DataAccessor.query("customer.readCustNatu", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} else {
			cust = (Map) DataAccessor.query("customer.readCustCorp", context.contextMap, DataAccessor.RS_TYPE.MAP);
		}

    	//承租人分类
		context.contextMap.put("dataType", "承租人级别");
		List<Map> custLevel = (List<Map>) DataAccessor.query("customer.getCustLevelDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		for(int i = 0; i < custLevel.size(); i++){
			nameByType.put(custLevel.get(i).get("CODE").toString(), custLevel.get(i).get("FLAG").toString());
		}
    	String[] types = this.getNameByType(cust.get("CUST_LEVEL"), context.contextMap.get("cust_level"), nameByType);
    	this.addLog(types[0], types[1], englishToChineseNames.get("cust_level"));
    	
    	//行业类别
    	context.contextMap.put("TRADE_TYPE", cust.get("TRADE_TYPE"));
		List<Map<String, String>> oldTradeTypes = (ArrayList<Map<String,String>>)DataAccessor.query("customer.getTradeTypeByCust", context.contextMap, RS_TYPE.LIST);
    	context.contextMap.put("TRADE_TYPE", context.contextMap.get("trade_type"));
		List<Map<String, String>> newTradeTypes = (ArrayList<Map<String,String>>)DataAccessor.query("customer.getTradeTypeByCust", context.contextMap, RS_TYPE.LIST);

		String oldTradeType = "";
		String newTradeType = "";
		for(Map<String, String> tt : oldTradeTypes){
			oldTradeType += (tt.get("TRADE_NAME") + "_");
		}
		if(oldTradeType.length() > 1){
			oldTradeType = oldTradeType.substring(0, oldTradeType.length() - 1);
		}
		for(Map<String, String> tt : newTradeTypes){
			newTradeType += (tt.get("TRADE_NAME") + "_");
		}
		if(newTradeType.length() > 1){
			newTradeType = newTradeType.substring(0, newTradeType.length() - 1);
		}
		this.addLog(oldTradeType, newTradeType, englishToChineseNames.get("trade_type"));

		this.addLog(cust.get("NATU_IDCARD"), context.contextMap.get("natu_idcard"), englishToChineseNames.get("natu_idcard"));
		this.addLog(cust.get("CORP_ORAGNIZATION_CODE"), context.contextMap.get("corp_oragnization_code"), englishToChineseNames.get("corp_oragnization_code"));
		
		//根据ID查询并拼接格式：省_市_区
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, String> nullArea = new HashMap<String, String>();
		nullArea.put("NAME", "");
		Map<String, String> province = null;
		Map<String, String> city = null;
		Map<String, String> area = null;
		if(!StringUtils.isEmpty(context.contextMap.get("province_id"))){
			param.put("id", context.contextMap.get("province_id"));
			province = (Map)DataAccessor.query("area.queryById", param, DataAccessor.RS_TYPE.MAP);
			if(province == null || province.get("NAME") == null){
				province = nullArea;
			}
		}
		if(!StringUtils.isEmpty(context.contextMap.get("city_id"))){
			param.put("id", context.contextMap.get("city_id"));
			city = (Map)DataAccessor.query("area.queryById", param, DataAccessor.RS_TYPE.MAP);
			if(city == null || city.get("NAME") == null){
				city = nullArea;
			}
		}
		if(!StringUtils.isEmpty(context.contextMap.get("area_id"))){
			param.put("id", context.contextMap.get("area_id"));
			area = (Map)DataAccessor.query("area.queryById", param, DataAccessor.RS_TYPE.MAP);
			if(area == null || area.get("NAME") == null){
				area = nullArea;
			}
		}
		this.addLog(cust.get("CUST_AREA"), province.get("NAME")+"_"+city.get("NAME")+"_"+area.get("NAME"), englishToChineseNames.get("cust_area"));
		
		this.addLog(cust.get("X_POINT"), context.contextMap.get("x_point"), englishToChineseNames.get("x_point"));
		this.addLog(cust.get("Y_POINT"), context.contextMap.get("y_point"), englishToChineseNames.get("y_point"));
		
    	/*----------------------法人----------------------*/
		if (isCorp) {
			//公司性质
			context.contextMap.put("dataType", "企业类型");
			context.contextMap.put("code", cust.get("CORP_ENTERPRISES_PROPERTY"));
			Map<String, String> oldCorpType = (Map<String, String>) DataAccessor.query("customer.getCustLevelDataByCode", context.contextMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("code", context.contextMap.get("corp_enterprises_property")==null?"":context.contextMap.get("corp_enterprises_property"));
			Map<String, String> newCorpType = (Map<String, String>) DataAccessor.query("customer.getCustLevelDataByCode", context.contextMap, DataAccessor.RS_TYPE.MAP);
			this.addLog(oldCorpType==null?"":oldCorpType.get("FLAG"), newCorpType==null?"":newCorpType.get("FLAG"), englishToChineseNames.get("corp_enterprises_property"));
			
			this.addLog(cust.get("CORP_SETUP_DATE"), context.contextMap.get("corp_setup_date"), englishToChineseNames.get("corp_setup_date"));
			this.addLog(cust.get("CORP_REGISTE_CAPITAL")==null?null:StringUtils.str2double(cust.get("CORP_REGISTE_CAPITAL").toString()), 
					context.contextMap.get("corp_registe_capital")==null?null:StringUtils.str2double(context.contextMap.get("corp_registe_capital").toString()), 
					englishToChineseNames.get("corp_registe_capital"));
			this.addLog(cust.get("CORP_PAICLUP_CAPITAL")==null?null:StringUtils.str2double(cust.get("CORP_PAICLUP_CAPITAL").toString()), 
					context.contextMap.get("corp_paiclup_capital")==null?null:StringUtils.str2double(context.contextMap.get("corp_paiclup_capital").toString()), 
					englishToChineseNames.get("corp_paiclup_capital"));
			this.addLog(cust.get("CORP_BUSINESS_LICENSE"), context.contextMap.get("corp_business_license"), englishToChineseNames.get("corp_business_license"));
			this.addLog(cust.get("TAX_CODE"), context.contextMap.get("tax_code"), englishToChineseNames.get("tax_code"));
			this.addLog(cust.get("CORP_TAX_CODE"), context.contextMap.get("corp_tax_code"), englishToChineseNames.get("corp_tax_code"));
			this.addLog(cust.get("CORP_PERIOD_VALIDITY"), context.contextMap.get("corp_period_validity"), englishToChineseNames.get("corp_period_validity"));
			this.addLog(cust.get("CORP_COMPANY_ZIP"), context.contextMap.get("corp_company_zip"), englishToChineseNames.get("corp_company_zip"));
			this.addLog(cust.get("CORP_REGISTE_ADDRESS"), context.contextMap.get("corp_registe_address"), englishToChineseNames.get("corp_registe_address"));
			this.addLog(cust.get("CORP_WORK_ADDRESS"), context.contextMap.get("corp_work_address"), englishToChineseNames.get("corp_work_address"));
			this.addLog(cust.get("CORP_BUSINESS_RANGE"), context.contextMap.get("corp_business_range"), englishToChineseNames.get("corp_business_range"));
			this.addLog(cust.get("CORP_COMPANY_WEBSITE"), context.contextMap.get("corp_company_website"), englishToChineseNames.get("corp_company_website"));
		}
    	/*----------------------法人----------------------*/
		
		this.addLog(cust.get("NATU_GENDER"), context.contextMap.get("natu_gender"), englishToChineseNames.get("natu_gender"));
		this.addLog(cust.get("NATU_AGE"), context.contextMap.get("natu_age"), englishToChineseNames.get("natu_age"));
		this.addLog(cust.get("NATU_MOBILE"), context.contextMap.get("natu_mobile"), englishToChineseNames.get("natu_mobile"));
		this.addLog(cust.get("NATU_PHONE"), context.contextMap.get("natu_phone"), englishToChineseNames.get("natu_phone"));
		this.addLog(cust.get("NATU_ZIP"), context.contextMap.get("natu_zip"), englishToChineseNames.get("natu_zip"));
		this.addLog(cust.get("NATU_IDCARD_ADDRESS"), context.contextMap.get("natu_idcard_address"), englishToChineseNames.get("natu_idcard_address"));
		this.addLog(cust.get("NATU_HOME_ADDRESS"), context.contextMap.get("natu_home_address"), englishToChineseNames.get("natu_home_address"));
		this.addLog(cust.get("NATU_WORK_UNITS"), context.contextMap.get("natu_work_units"), englishToChineseNames.get("natu_work_units"));
		this.addLog(cust.get("NATU_WORK_ADDRESS"), context.contextMap.get("natu_work_address"), englishToChineseNames.get("natu_work_address"));
		this.addLog(cust.get("CORP_COMPANY_EMAIL"), context.contextMap.get("corp_company_email"), englishToChineseNames.get("corp_company_email"));

    	/*----------------------法人----------------------*/
		this.addLog(cust.get("CORP_OTHER_MASSAGE"), context.contextMap.get("corp_other_massage"), englishToChineseNames.get("corp_other_massage"));
		this.addLog(cust.get("CORP_HEAD_SIGNATURE"), context.contextMap.get("corp_head_signature"), englishToChineseNames.get("corp_head_signature"));
		this.addLog(cust.get("CORP_HS_IDCARD"), context.contextMap.get("corp_hs_idcard"), englishToChineseNames.get("corp_hs_idcard"));
		this.addLog(cust.get("CORP_HS_LINK_MODE"), context.contextMap.get("corp_hs_link_mode"), englishToChineseNames.get("corp_hs_link_mode"));
		this.addLog(cust.get("CORP_HS_HOME_ADDRESS"), context.contextMap.get("corp_hs_home_address"), englishToChineseNames.get("corp_hs_home_address"));
    	/*----------------------法人----------------------*/
		
		this.addLog(cust.get("NATU_MATE_NAME"), context.contextMap.get("natu_mate_name"), englishToChineseNames.get("natu_mate_name"));
		this.addLog(cust.get("NATU_MATE_IDCARD"), context.contextMap.get("natu_mate_idcard"), englishToChineseNames.get("natu_mate_idcard"));
		this.addLog(cust.get("NATU_MATE_MOBILE"), context.contextMap.get("natu_mate_mobile"), englishToChineseNames.get("natu_mate_mobile"));
		this.addLog(cust.get("NATU_MATE_IDCARD_ADDRESS"), context.contextMap.get("natu_mate_idcard_address"), englishToChineseNames.get("natu_mate_idcard_address"));
		this.addLog(cust.get("NATU_MATE_WORK_UNITS"), context.contextMap.get("natu_mate_work_units"), englishToChineseNames.get("natu_mate_work_units"));
		this.addLog(cust.get("NATU_MATE_WORK_ADDRESS"), context.contextMap.get("natu_mate_work_address"), englishToChineseNames.get("natu_mate_work_address"));
		if (!isCorp) {
			this.addLog(cust.get("REMARK"), context.contextMap.get("remark"), englishToChineseNames.get("remark"));
		} else {
			this.addLog(cust.get("REMARK"), context.contextMap.get("remark"), englishToChineseNames.get("corp_remark"));
		}
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

	/**
	 * 虚拟账号管理页面
	 * @param context
	 */
	public void virtualAccountManager(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		if(context.contextMap.get("search_company") == null){
			context.contextMap.put("search_company",1);
		}
		String search_status = null;
		boolean manageRole = baseService.checkAccessForResource("virtualAccountManager", String.valueOf(context.contextMap.get("s_employeeId")));
		if (manageRole) {
			search_status = StringUtils.isEmpty(context.contextMap.get("search_status")) ? "0" : String.valueOf(context.contextMap.get("search_status"));
		} else {
			search_status = "2";
		}
		context.contextMap.put("search_status", search_status);
		PagingInfo<Object> pagingInfo = baseService.queryForListWithPaging("customer.virtualAccountManager", context.contextMap, "EXPORT_DATE");
		outputMap.put("pagingInfo", pagingInfo);
		outputMap.put("search_content", context.contextMap.get("search_content"));
		outputMap.put("search_date_from", context.contextMap.get("search_date_from"));
		outputMap.put("search_date_to", context.contextMap.get("search_date_to"));
		outputMap.put("search_status", search_status);
		outputMap.put("search_type", context.contextMap.get("search_type"));
		outputMap.put("search_company", context.contextMap.get("search_company"));
		outputMap.put("manageRole", manageRole);
		Output.jspOutput(outputMap, context, "/customer/virtualAccountManager.jsp");
	}
	
	public void confirmAll(Context context){
		boolean flage = true;
		String cust_ids = (String) context.contextMap.get("cust_ids");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			for (String id : cust_ids.split(",")) {
				updateExportStatus(id, "2", company_code);
			}
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void confirmOne(Context context){
		boolean flage = true;
		String cust_id = (String) context.contextMap.get("cust_id");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			updateExportStatus(cust_id, "2", company_code);
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void backAll(Context context){
		boolean flage = true;
		String cust_ids = (String) context.contextMap.get("cust_ids");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			for (String id : cust_ids.split(",")) {
				updateExportStatus(id, "0", company_code);
			}
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void backOne(Context context){
		boolean flage = true;
		String cust_id = (String) context.contextMap.get("cust_id");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			updateExportStatus(cust_id, "0", company_code);
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void applyAll(Context context){
		boolean flage = true;
		String cust_ids = (String) context.contextMap.get("cust_ids");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			for (String id : cust_ids.split(",")) {
				updateExportStatus(id, "10", company_code);
			}
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void applyOne(Context context){
		boolean flage = true;
		String cust_id = (String) context.contextMap.get("cust_id");
		String company_code = (String) context.contextMap.get("company_code");
		try {
			updateExportStatus(cust_id, "10", company_code);
		} catch (Exception e) {
			flage = false;
		}
		Output.jsonFlageOutput(flage, context);
	}
	
	public void updateExportStatus(String cust_id, String flag, String company_code){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("cust_id", cust_id);
		paramMap.put("flag", flag);
		paramMap.put("company_code", company_code);
		baseService.update("customer.updateExportStatus", paramMap);
	}
	
	public void getRemarkById(Context context){
		String result = (String) baseService.queryForObj("customer.getRemarkById", context.contextMap);
		Output.txtOutput(result, context);
	}
	
	public void addRemark(Context context){
		baseService.update("customer.addRemark", context.contextMap);
		context.contextMap.put("search_status", "1");
		virtualAccountManager(context);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void doApplyVirtualAccount(Context context) throws Exception{
		customerService.doApplyVirtualAccount();
		Output.jsonFlageOutput(true, context);
	}
	
	//导出客户信息  add by xuyuefei  2014/6/10
	public void exportCustomerInfo(Context context){
		CustomerInfoExcel cie=new CustomerInfoExcel();
		try{
		context.getResponse().setContentType("application/vnd.ms-excel;charset=GB2312");
		 context.response.setHeader("Content-Disposition"
				   ,"attachment;filename="+new String(("客户信息.xls").getBytes("GBK"),"ISO-8859-1"));
		 List<HashMap<String,Object>> cusInfo=(List<HashMap<String,Object>>)DataAccessor.query("customer.getCustomerInfoList", null, DataAccessor.RS_TYPE.LIST);
		 
		 ServletOutputStream out=context.getResponse().getOutputStream();
		 context.contextMap.put("sheetName", "客户信息");
		 Map<String,Object> map=new HashMap<String,Object>();
		 map.put("cusInfo", cusInfo);
		 cie.createReport(map,context).write(out);
		 out.flush();
		 out.close();
		 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//add by ShenQi 验证添加客户信息有效性
	public void validateCustomer(Context context) {
		
		Map<String,Object> resultMap=new HashMap<String,Object>();
		boolean flag=false;
		resultMap.put("flag",flag);
		StringBuffer msg=new StringBuffer();
		//验证栏位合法性
		//验证承租人名称**************************************************************************
		String originalCustName=context.contextMap.get("custName").toString().trim();
		if(originalCustName.length()==0) {
			msg.append("承租人名称不能为空！\n");
			flag=true;
			resultMap.put("flag",flag);
			resultMap.put("msg",msg.toString());
		} else {
			StringBuffer packageCustName=new StringBuffer();
			for(int i=0;i<originalCustName.toCharArray().length;i++) {
				packageCustName.append(String.valueOf(originalCustName.toCharArray()[i]).trim());
			}
			if(packageCustName.toString().length()<originalCustName.length()) {
				msg.append("承租人名称中间不能包含空格！\n");
				flag=true;
				resultMap.put("flag",flag);
				resultMap.put("msg",msg.toString());
			}
		}
		//***************************************************************************************
		//验证承租人所在省市**********************************************************************
		String provinceId=context.contextMap.get("provinceId").toString().trim();
		if("-1".equals(provinceId)) {
			msg.append("请选择承租人所在省市！\n");
			flag=true;
			resultMap.put("flag",flag);
			resultMap.put("msg",msg.toString());
		}
		//***************************************************************************************
		
		String originalOrgCode="";
		String idCard="";
		if("1".equals(context.contextMap.get("custType")+"")) {//法人类型
			//验证组织机构代码**************************************************************************
			originalOrgCode=context.contextMap.get("corpOragnizationCode").toString().trim();
			if(originalOrgCode.length()<10) {
				msg.append("组织机构代码少于9位！\n");
				flag=true;
				resultMap.put("flag",flag);
				resultMap.put("msg",msg.toString());
			}
			StringBuffer packageOrgCode=new StringBuffer();
			for(int i=0;i<originalOrgCode.toCharArray().length;i++) {
				packageOrgCode.append(String.valueOf(originalOrgCode.toCharArray()[i]).trim());
			}
			if(packageOrgCode.length()<10) {
				msg.append("组织机构代码中间不能包含空格！\n");
				flag=true;
				resultMap.put("flag",flag);
				resultMap.put("msg",msg.toString());
			}
			//***************************************************************************************
		} else if("0".equals(context.contextMap.get("custType")+"")) {//自然人类型
			//验证身份证号码**************************************************************************
			idCard=context.contextMap.get("idCard").toString().trim();
			if(idCard.length()<18) {
				msg.append("身份证号码小于18位！\n");
				flag=true;
				resultMap.put("flag",flag);
				resultMap.put("msg",msg.toString());
			}
		}
		
		String isExist="N";
		//后台数据检测
		if(!flag) {//前台栏位数据都是有效的开始后台验证
			Map<String,Object> param=new HashMap<String,Object>();
			param.put("custType",context.contextMap.get("custType"));
			param.put("originalCustName",originalCustName);
			param.put("originalOrgCode",originalOrgCode);
			param.put("idCard",idCard);
			try {
				isExist=(String)DataAccessor.query("customer.checkCorpCust",param,RS_TYPE.OBJECT);
				if("Y".equals(isExist)) {
					msg.append("此客户已存在,不得重复添加客户！\n");
					flag=true;
					resultMap.put("flag",flag);
					resultMap.put("msg",msg.toString());
				}
			} catch (Exception e) {
			}
		}
		Output.jsonOutput(resultMap,context);
	}
}
