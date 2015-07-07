package com.brick.project.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.brick.coderule.service.CodeRule;
import com.brick.collection.support.PayRate;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

public class ProjectManage extends AService {
	Log logger = LogFactory.getLog(ProjectManage.class);
	
	/**
	 * 项目管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void projectManage(Context context) {
		Map outputMap = new HashMap(); 
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("projectManage.getProject", context.contextMap,DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		Output.jspOutput(outputMap, context, "/project/projectManage.jsp");
	}
	/**
	 * 项目管理受理页
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void projectMan(Context context) {
		Map outputMap = new HashMap(); 
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("projectManage.getProjectManage", context.contextMap,DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("start_date", context.contextMap.get("start_date"));
		outputMap.put("end_date", context.contextMap.get("end_date"));
		Output.jspOutput(outputMap, context, "/project/projectMan.jsp");
	}
	
	/**
	 * 初始化新建项目页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initProjectAdd(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List provinces = null;
		List projectTypes = null;
		List companyList = null;
		List contractType = null;
		List customerCome = null;
		if(errList.isEmpty()) {
			try {
				provinces = (List) DataAccessor.query("area.getProvinces",context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dictionaryType", "尽职调查报告类型");
				projectTypes = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);			
				companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("companyList", companyList);
				context.contextMap.put("dataType", "融资租赁合同类型");
				contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("contractType", contractType);  
				context.contextMap.put("dataType", "客户来源");
				customerCome = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("customerCome", customerCome);  
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("provinces", provinces);
			outputMap.put("projectTypes", projectTypes);
			Output.jspOutput(outputMap, context,"/project/projectCreate.jsp");
		}	
		
	}
	/**
	 * 生成一个项目意向书
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) { 
		List errList = context.errList;		
		long prjtId = 0l;
		@SuppressWarnings("unused")
		long prpcId = 0l;		
		if(errList.isEmpty()) {		
			try {
				DataAccessor.getSession().startTransaction();				
				long decpId = DataUtil.longUtil(context.contextMap.get("decp_id"));
				String prjtCode = CodeRule.generateProjectCode(decpId);
				context.contextMap.put("prjt_code", prjtCode);				
				prjtId = (Long) DataAccessor.getSession().insert("projectManage.insertProject", context.contextMap);
				context.contextMap.put("prjt_id", prjtId);				
				String cust_type = (String) context.contextMap.get("cust_type");				
				if (cust_type.equals("1")) {
					prpcId = (Long) DataAccessor.getSession().insert("projectManage.insertProCustomerCrop", context.contextMap);
				} else {
					prpcId = (Long) DataAccessor.getSession().insert("projectManage.insertProCustomerNatu", context.contextMap);
				}				
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally {
				try {
					DataAccessor.getSession().endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectCreateJsp&prjt_id="+prjtId);
		}
	
	}
	/**
	 * 初始化项目意向书添加页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void projectCreateJsp(Context context) { 
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map projectMap = null;
		List custLevel = null;
		List equipmentsList = null;
		Map schemeMap = null;
		List insuresList = null;
		List payWayList = null;
		List dealWayList = null;
		List insureBuyWayList = null;
		List insureCompanyList = null;
		List insureTypeList = null;
		Map natuMap = null;
		Map corpMap = null;
		List provinces = null;
		List citys = null;
		List lockList = null;
		if(errList.isEmpty()) {			
			try {
				context.contextMap.put("data_type", "客户来源");
				projectMap = (Map) DataAccessor.query("projectManage.selectProjectBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				int cust_type = DataUtil.intUtil(projectMap.get("CUST_TYPE"));			
				if (cust_type == 1) {
					corpMap = (Map) DataAccessor.query("projectManage.selectCorpInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("corpMap", corpMap);					
				} else {
					natuMap = (Map) DataAccessor.query("projectManage.selectNatuInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("natuMap", natuMap);
				}	
				provinces = (List) DataAccessor.query("area.getProvinces",context.contextMap, DataAccessor.RS_TYPE.LIST);
				Long provincesId= Long.parseLong(String.valueOf(projectMap.get("PROVINCE_ID")));
				context.contextMap.put("provinceId", provincesId);
				citys = (List) DataAccessor.query("area.getCitysByProvinceId", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("citys", citys);
				outputMap.put("provinces", provinces);
				context.contextMap.put("dataType", "承租人级别");
				custLevel = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				equipmentsList = (List) DataAccessor.query("projectManage.selectEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				schemeMap = (Map) DataAccessor.query("projectManage.selectScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				insuresList = (List) DataAccessor.query("projectManage.selectInsure",context.contextMap, DataAccessor.RS_TYPE.LIST);
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);					
				insureTypeList = (List<Map>) DataAccessor.query("projectManage.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				context.contextMap.put("dictionaryType", "支付方式");
				payWayList = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dictionaryType", "租赁期满处理方式");
				dealWayList = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dictionaryType", "保险购买方式");
				insureBuyWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap,DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dataType", "锁码方式");
				lockList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				outputMap.put("lockList", lockList);
				outputMap.put("payWayList", payWayList);
				outputMap.put("dealWayList", dealWayList);
				outputMap.put("insureBuyWayList", insureBuyWayList);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("projectMap", projectMap);
			outputMap.put("custLevel", custLevel);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("insuresList", insuresList);
			Output.jspOutput(outputMap, context,"/project/projectListCreate.jsp");
		}
		
	}
	/**
	 * 保存项目
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createProject(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		List errList = context.errList;		
		if(errList.isEmpty()) {	
			SqlMapClient sqlMapper = null;
			try {			
				sqlMapper = DataAccessor.getSession();
				sqlMapper.startTransaction();
				sqlMapper.delete("projectManage.deleteScheme",context.contextMap);
				sqlMapper.delete("projectManage.deleteInsure",context.contextMap);
				sqlMapper.delete("projectManage.deleteEquipment",context.contextMap);
				if (context.request.getParameterValues("SUPPIER") != null) {
					String[] TYPE = context.request.getParameterValues("TYPE");
					String[] KIND = context.request.getParameterValues("KIND");
					String[] PRODUCT = context.request.getParameterValues("PRODUCT");
					String[] SUPPIER = context.request.getParameterValues("SUPPIER");
					String[] TYPE_NAME = context.request.getParameterValues("TYPE_NAME");
					String[] KIND_NAME = context.request.getParameterValues("KIND_NAME");
					String[] PRODUCT_NAME = context.request.getParameterValues("PRODUCT_NAME");
					String[] SUPPIER_NAME = context.request.getParameterValues("SUPPIER_NAME");
					String[] STAYBUY_PRICE = context.request.getParameterValues("STAYBUY_PRICE");
					String[] UNIT_PRICE = context.request.getParameterValues("UNIT_PRICE");
					String[] AMOUNT = context.request.getParameterValues("AMOUNT");
					String[] UNIT = context.request.getParameterValues("UNIT");
					//String[] MEMO = context.request.getParameterValues("MEMO");
					String[] LOCK_CODE = context.request.getParameterValues("LOCK_CODE");
					for (int i = 0; i < SUPPIER.length; i++) {
						if (!SUPPIER[i].equals("-1") && !TYPE[i].equals("-1")
								&& !PRODUCT[i].equals("-1")
								&& !KIND[i].equals("-1")) {
							int amount = Integer.parseInt(AMOUNT[i]);
							for (int j = 0; j < amount; j++) {
								Map map = new HashMap();
								map.put("SUEQ_ID", HTMLUtil.parseStrParam2(SUPPIER[i], "0"));
								map.put("THING_NAME", HTMLUtil.parseStrParam2(KIND_NAME[i], ""));
								map.put("BRAND", HTMLUtil.parseStrParam2(SUPPIER_NAME[i], ""));
								map.put("MODEL_SPEC", HTMLUtil.parseStrParam2(PRODUCT_NAME[i], ""));
								map.put("UNIT_PRICE", UNIT_PRICE[i]);
								//map.put("MEMO", HTMLUtil.parseStrParam2(MEMO[i], ""));
								map.put("STAYBUY_PRICE", STAYBUY_PRICE[i]);
								map.put("UNIT", HTMLUtil.parseStrParam2(UNIT[i], ""));
								map.put("THING_KIND", HTMLUtil.parseStrParam2(TYPE_NAME[i], ""));
								map.put("LOCK_CODE", HTMLUtil.parseStrParam2(LOCK_CODE[i], ""));
								map.put("s_employeeId", context.request.getSession().getAttribute("s_employeeId"));
								map.put("prjt_id", context.contextMap.get("prjt_id"));
								sqlMapper.insert("projectManage.createEquipment",map);
							}
						}
					}
				}
				sqlMapper.insert("projectManage.createScheme",context.contextMap);

				if (context.request.getParameterValues("INSURE_ITEM") != null) {
					String[] INSURE_ITEM = context.request.getParameterValues("INSURE_ITEM");
					String[] START_DATE = context.request.getParameterValues("START_DATE");
					String[] END_DATE = context.request.getParameterValues("END_DATE");
					String[] INSURE_RATE = context.request.getParameterValues("INSURE_RATE");
					String[] INSURE_PRICE = context.request.getParameterValues("INSURE_PRICE");
					String[] INSURE_MEMO = context.request.getParameterValues("INSURE_MEMO");
					for (int i = 0; i < INSURE_ITEM.length; i++) {
						Map map = new HashMap();
						map.put("INSURE_ITEM", HTMLUtil.parseStrParam2(INSURE_ITEM[i], "0"));
						map.put("START_DATE", HTMLUtil.parseStrParam2(START_DATE[i], ""));
						map.put("END_DATE", HTMLUtil.parseStrParam2(END_DATE[i], ""));
						map.put("INSURE_RATE", HTMLUtil.parseStrParam2(INSURE_RATE[i], "0"));
						map.put("INSURE_PRICE", HTMLUtil.parseStrParam2(INSURE_PRICE[i], "0"));
						map.put("MEMO", HTMLUtil.parseStrParam2(INSURE_MEMO[i], ""));
						map.put("prjt_id", context.contextMap.get("prjt_id"));
						sqlMapper.insert("projectManage.createInsure",map);
					}
				}
				String cust_type = (String) context.contextMap.get("cust_type");			
				if (cust_type.equals("1")) {
					sqlMapper.update("projectManage.updateCorp",context.contextMap);
					sqlMapper.update("projectManage.updateCorpArea",context.contextMap);
				} else {
					sqlMapper.update("projectManage.updateNatu",context.contextMap);
				}
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectManage");
		}


	}
	/**
	 * 根据PRJT_ID查看项目管理
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectProjectForShow(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		Map projectMap = null;
		List custLevel = null;
		List equipmentsList = null;
		Map schemeMap = null;
		List insuresList = null;
		List payWayList = null;
		List dealWayList = null;
		List insureBuyWayList = null;
		List insureCompanyList = null;
		List insureTypeList = null;
		Map natuMap = null;
		Map corpMap = null;
		@SuppressWarnings("unused")
		List lockList = null;
		if(errList.isEmpty()) {			
			try {
				context.contextMap.put("data_type", "客户来源");
				projectMap = (Map) DataAccessor.query("projectManage.selectProjectBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				int cust_type = DataUtil.intUtil(projectMap.get("CUST_TYPE"));			
				if (cust_type == 1) {
					corpMap = (Map) DataAccessor.query("projectManage.selectCorpInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("corpMap", corpMap);					
				} else {
					natuMap = (Map) DataAccessor.query("projectManage.selectNatuInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
					outputMap.put("natuMap", natuMap);
				}						
				context.contextMap.put("dataType", "承租人级别");
				custLevel = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
				equipmentsList = (List) DataAccessor.query("projectManage.selectEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				schemeMap = (Map) DataAccessor.query("projectManage.selectScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				insuresList = (List) DataAccessor.query("projectManage.selectInsure",context.contextMap, DataAccessor.RS_TYPE.LIST);
				insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureCompanyList", insureCompanyList);					
				insureTypeList = (List<Map>) DataAccessor.query("projectManage.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("insureTypeList", insureTypeList);
				Map baseRate = PayRate.getBaseRate();
				outputMap.put("baseRateJson", Output.serializer.serialize(baseRate));
				context.contextMap.put("dictionaryType", "支付方式");
				payWayList = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dictionaryType", "租赁期满处理方式");
				dealWayList = (List) DataAccessor.query("creditCustomer.getItems",context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("dictionaryType", "保险购买方式");
				insureBuyWayList = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap,DataAccessor.RS_TYPE.LIST);
				outputMap.put("payWayList", payWayList);
				outputMap.put("dealWayList", dealWayList);
				outputMap.put("insureBuyWayList", insureBuyWayList);
				
				context.contextMap.put("dataType", "锁码方式");
				lockList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			outputMap.put("projectMap", projectMap);
			outputMap.put("custLevel", custLevel);
			outputMap.put("equipmentsList", equipmentsList);
			outputMap.put("schemeMap", schemeMap);
			outputMap.put("insuresList", insuresList);			
			String flag = (String) context.contextMap.get("FLAG");		
			outputMap.put("flag", flag);
			Output.jspOutput(outputMap, context,"/project/projectListShow.jsp");		
		}
	}
	/**
	 * 提交项目
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void commitProject(Context context) {
		List errList = context.errList;
		Long prjtId = 0l;
		if(errList.isEmpty()) {			
			try {
				prjtId = DataUtil.longUtil(context.contextMap.get("prjt_id"));
				context.contextMap.put("PRJT_ID", prjtId);
				DataAccessor.execute("projectManage.commitProject", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectManage");
		}
	}
	/**
	 * 删除项目
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void deleteProject(Context context) {
		List errList = context.errList;
		Long prjtId = 0l;
		SqlMapClient sqlMapper=null;
		if(errList.isEmpty()) {			
			try {
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				
				prjtId = DataUtil.longUtil(context.contextMap.get("prjt_id"));
				context.contextMap.put("PRJT_ID", prjtId);
//				DataAccessor.execute("projectManage.deleteProject", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("projectManage.deleteProjectCus", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("projectManage.deleteProjectEqu", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("projectManage.deleteProjectIns", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
//				DataAccessor.execute("projectManage.deleteProjectSch", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				
				sqlMapper.update("projectManage.deleteProject", context.contextMap);
				sqlMapper.update("projectManage.deleteProjectCus", context.contextMap);
				sqlMapper.update("projectManage.deleteProjectEqu", context.contextMap);
				sqlMapper.update("projectManage.deleteProjectIns", context.contextMap);
				sqlMapper.update("projectManage.deleteProjectSch", context.contextMap);
				
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectManage");
		}
	}
	/**
	 * 受理项目
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void acceptProject(Context context) {
		List errList = context.errList;
		Long prjtId = 0l;
		long custId = 0;
		@SuppressWarnings("unused")
		long creditId = 0; 
		Map projectMap = null;
		Map typeMap = null;
		
		//Add by Michael 2012 06-29 每个报告都将产生一个流水号
		String creditRunCode=CodeRule.geneCreditRunCode();
		context.contextMap.put("credit_runcode", creditRunCode);
		
		SqlMapClient sqlMapper=null;
		if(errList.isEmpty()) {		
			try {
				sqlMapper=DataAccessor.getSession();
				sqlMapper.startTransaction();
				
				prjtId = DataUtil.longUtil(context.contextMap.get("prjt_id"));
				context.contextMap.put("PRJT_ID", prjtId);
				//DataAccessor.execute("projectManage.acceptProject", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
				
				sqlMapper.update("projectManage.acceptProject", context.contextMap);
				
				Object obj = DataAccessor.query("creditCustomer.validateCustomer", context.contextMap, DataAccessor.RS_TYPE.OBJECT);
				if (obj == null) {
					String code = CodeRule.generateCustCode(context);
					context.contextMap.put("cust_code", code);
					String cust_type = (String) context.contextMap.get("cust_type");
					if (cust_type.equals("1")) {
//						custId = (Long) DataAccessor.execute(
//								"creditCustomer.createCustCrop",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.INSERT);
						custId = (Long)sqlMapper.insert("creditCustomer.createCustCrop", context.contextMap);
					} else {
//						custId = (Long) DataAccessor.execute(
//								"creditCustomer.createCustNatu",
//								context.contextMap,
//								DataAccessor.OPERATION_TYPE.INSERT);
						custId = (Long)sqlMapper.insert("creditCustomer.createCustNatu", context.contextMap);
					}
				} else {
					custId = (Integer) obj;
				}
				context.contextMap.put("cust_id", custId);
				context.contextMap.put("project_id", prjtId);
				context.contextMap.put("data_type", "客户来源");
				context.contextMap.put("prjt_id", prjtId);
				projectMap = (Map) DataAccessor.query("projectManage.selectProjectBaseInfo",context.contextMap, DataAccessor.RS_TYPE.MAP);
				String credti_code = CodeRule.generateProjectCreditCode(projectMap.get("DECP_ID"));
				context.contextMap.put("sensor_id", context.contextMap.get("s_employeeId"));
				context.contextMap.put("credti_code", credti_code);
				
				context.contextMap.put("dictionaryType", "尽职调查报告类型");
				typeMap = (Map) DataAccessor.query("projectManage.queryMap",context.contextMap, DataAccessor.RS_TYPE.MAP);
				context.contextMap.put("type", typeMap.get("TYPE"));
//				creditId = (Long) DataAccessor.execute("creditCustomer.addCredit",
//						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT); 
				creditId = (Long)sqlMapper.insert("creditCustomer.addCredit", context.contextMap);
				
				sqlMapper.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}finally{
				try {
					sqlMapper.endTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectMan");
		}
	}
	/**
	 * 驳回项目
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void receptProject(Context context) {
		List errList = context.errList;
		Long prjtId = 0l;
		if(errList.isEmpty()) {			
			try {
				prjtId = DataUtil.longUtil(context.contextMap.get("prjt_id"));
				context.contextMap.put("PRJT_ID", prjtId);
				DataAccessor.execute("projectManage.receptProject", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}		
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=projectManage.projectMan");
		}
	}
	/**
	 * ajax得到保险公司下的保险险种
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getComTypeById(Context context){
		Map outputMap = new HashMap();
		List insureTypeList=null;
		try {
			insureTypeList = (List<Map>) DataAccessor.query("projectManage.queryInsureTypeListById", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("insureTypeList", insureTypeList);
			outputMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
		} catch (Exception e) { 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		Output.jsonOutput(outputMap, context);
	}
	
	/**
	 * 
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void lockManager(Context context) {
		Map outputMap = new HashMap();
		List lockList = null;
		try {
			context.contextMap.put("dataType", "锁码方式");
			lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		outputMap.put("lockList", lockList);
		Output.jsonOutput(outputMap, context);
	}

	
}
