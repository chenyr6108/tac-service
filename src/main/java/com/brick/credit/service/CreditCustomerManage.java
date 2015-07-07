package com.brick.credit.service;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.brick.base.command.BaseCommand;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.customer.service.CustomerCredit;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.OPERATION_TYPE;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.special.to.CreditSpecialTO;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.chart.computation.LabelLimiter.Option;

import com.brick.log.service.LogPrint;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 尽职调查报告承租人信息管理
 * 
 * @author li shaojie
 * @date Apr 26, 2010
 * 
 */

public class CreditCustomerManage extends BaseCommand {
	Log logger = LogFactory.getLog(CreditCustomerManage.class);

	private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	/**
	 * 初始化尽职调查报告承租人信息添加页面
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void initCreditCustomerAdd(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List provinces = null;
		List creditTypes = null;
		List companyList = null;
		List contractType = null;
		List customerCome = null;
		List<CreditSpecialTO> creditSpecialList=null;
		
		try {
			provinces = (List) DataAccessor.query("area.getProvinces", context.contextMap, DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("dictionaryType", "公司");

			context.contextMap.put("dictionaryType", "尽职调查报告类型");
			creditTypes = (List) DataAccessor.query("creditCustomer.getItems", context.contextMap, DataAccessor.RS_TYPE.LIST);

			companyList = (List) DataAccessor.query("companyManage.queryCompanyAlias", null, DataAccessor.RS_TYPE.LIST);
			outputMap.put("companyList", companyList);
			context.contextMap.put("dataType", "融资租赁合同类型");
			contractType = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("contractType", contractType);
			context.contextMap.put("dataType", "客户来源");
			customerCome = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("customerCome", customerCome);
			//增加办事处限制
			String s_employeeDecpId = String.valueOf(context.contextMap.get("s_employeeDecpId"));
			Map<String, Object> decpMap=new HashMap<String, Object>();
			decpMap.put("propertyCode", "BELONGDEPT");
			decpMap.put("decpId", s_employeeDecpId);
			creditSpecialList=(List<CreditSpecialTO>)DataAccessor.query("creditSpecial.queryCreditSpecialGroupByDecpId",decpMap,DataAccessor.RS_TYPE.LIST);
			outputMap.put("creditSpecialList",creditSpecialList);
			
			//判断是否绿色通道案件
			String s_employeeId = String.valueOf(context.contextMap.get("s_employeeId"));
			int vip_flag = getVipFlagForUserId(s_employeeId);
			outputMap.put("vip_flag", vip_flag);
			
			//区域主管
			String up_user = LeaseUtil.getUpUserByUserId(s_employeeId);
			String up_user_name = LeaseUtil.getUserNameByUserId(up_user);
			outputMap.put("up_user", up_user);
			outputMap.put("up_user_name", up_user_name);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加页面初始化错误!请联系管理员");
		}
		outputMap.put("provinces", provinces);
		outputMap.put("creditTypes", creditTypes);
		double token = Math.random();
		HttpSession session = context.request.getSession();
		session.setAttribute("s_token", token);
		outputMap.put("token", token);
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		if (errList.isEmpty()) {
			Output.jspOutput(outputMap, context, "/credit/creditCustomerCreate.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	private int getVipFlagForUserId(String s_employeeId){
		int vip_flag = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dept_name", "行销企划课");
		paramMap.put("s_employeeId", s_employeeId);
		Integer result = (Integer) baseService.queryForObj("creditReportManage.isUserInTheDept", paramMap);
		if (result != null && result > 0) {
			vip_flag = 1;
		}
		return vip_flag;
	}
	
	/**
	 * 根据省份ID获取该省份下的所有市，区。
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCitysByProvinceId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List citys = null;
		try {
			citys = (List) DataAccessor.query("area.getCitysByProvinceId",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--地区列表 省份 错误!请联系管理员");
		}
		outputMap.put("citys", citys);
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 根据市ID获取该市下的所有区。
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAreaByCityId(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		List area = null;
		try {
			area = (List) DataAccessor.query("area.getCitysByProvinceId",
					context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--地区列表 城市 错误!请联系管理员");
		}
		outputMap.put("area", area);
		if (errList.isEmpty()) {
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 生成一个尽职调查报告
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {
		long custId = 0;
		long creditId = 0;
		String credti_code = CodeRule
				.generateProjectCreditCode(context.contextMap.get("decp_id"));
		//Add by Michael 2012 06-29 每个报告都将产生一个流水号
		String creditRunCode=CodeRule.geneCreditRunCode();
		context.contextMap.put("credit_runcode", creditRunCode);
		
		List errList = context.errList;
		Map outputMap = new HashMap() ;
		Map custInfo=new HashMap();
		SqlMapClient sqlMapper = DataAccessor.getSession() ;
		try {
			sqlMapper.startTransaction() ;
			if (context.contextMap.get("cust_idcard1") != null) {
				context.contextMap.put("cust_idcard", context.contextMap.get("cust_idcard1"));
			} else if (context.contextMap.get("cust_idcard2") != null) {
				context.contextMap.put("cust_idcard", context.contextMap.get("cust_idcard2"));
			}
			Object obj = sqlMapper.queryForObject("creditCustomer.validateCustomer", context.contextMap);
			if (obj == null) {

				// 添加承租人编号
				// context.contextMap.put("cust_code", "123456789876543");
				// CodeRule codeRule = new CodeRule();
				String code = CodeRule.generateCustCode(context);
				context.contextMap.put("cust_code", code);
				//增加客户虚拟账号
				context.contextMap.put("virtual_code", "88"+code);
				
				String cust_type = (String) context.contextMap.get("cust_type");
				if (cust_type.equals("1")) {
					custId = (Long)sqlMapper.insert("creditCustomer.createCustCrop",context.contextMap);
				} else {
					String id_card_type = (String) context.contextMap
							.get("id_card_type");
					if (id_card_type.equals("1")) {
						context.contextMap.put("cust_idcard",
								context.contextMap.get("cust_idcard1"));
					} else {
						context.contextMap.put("cust_idcard",
								context.contextMap.get("cust_idcard2"));
					}
					custId = (Long) sqlMapper.insert("creditCustomer.createCustNatu",context.contextMap);
				}
			} else {
				custId = (Integer) obj;
			}
			context.contextMap.put("cust_id", custId);

			// 添加业务申请ID
			context.contextMap.put("project_id", "");
			context.contextMap.put("credti_code", credti_code);
			if (((String) context.contextMap.get("city_id")).equals("-1")) {
				context.contextMap.put("city_id", "");
			}
			//报告生成时候插入产品类别 add by ShenQi
			String contractType=context.contextMap.get("contract_type").toString();
			String productionType="";
			if(Constants.CONTRACT_TYPE_2.equals(contractType)||Constants.CONTRACT_TYPE_5.equals(contractType)||Constants.CONTRACT_TYPE_7.equals(contractType)
					||Constants.CONTRACT_TYPE_9.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_1;//设备类型
			} else if(Constants.CONTRACT_TYPE_4.equals(contractType)||Constants.CONTRACT_TYPE_11.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_2;//商用车类型
			} else if(Constants.CONTRACT_TYPE_6.equals(contractType)||Constants.CONTRACT_TYPE_8.equals(contractType)||Constants.CONTRACT_TYPE_10.equals(contractType)
					||Constants.CONTRACT_TYPE_12.equals(contractType)
					||Constants.CONTRACT_TYPE_13.equals(contractType)
					||Constants.CONTRACT_TYPE_14.equals(contractType)) {
				productionType=Constants.PRODUCTION_TYPE_3;//乘用车类型
			}
			
			context.contextMap.put("production_type",productionType);
			creditId = (Long) sqlMapper.insert("creditCustomer.addCredit", context.contextMap);
			//添加到T_LOG_ACTIVITIESLOG
			context.contextMap.put("creditId", creditId);
			if( (!"".equals((String)context.contextMap.get("ACTILOG_ID")))){
				sqlMapper.update("creditCustomer.updatelog", context.contextMap);
			}
			BusinessLog.addBusinessLog(creditId, null, "现场调查报告", "生成", credti_code,
					"", 1, Long.parseLong(context.contextMap.get("s_employeeId")+ ""), null,sqlMapper,(String)context.contextMap.get("IP"));
			
			//加入了报告复制功能,所以此功能废除
			//****************************************************************************************************
			//报告添加的同时,通过cust_id在公司沿革中插入客户信息在表t_prjt_creditcustomercorp中 add by ShenQi 2012-7-9
			//0是指自然人,1是指法人
			/*String cust_type=(String)context.contextMap.get("cust_type");//from page radio
			
			//不管客户类型直接从T_CUST_CUSTOMER表中取出客户信息
			context.contextMap.put("credit_id", context.contextMap.get("creditId"));
			custInfo=(Map)sqlMapper.queryForObject("creditCustomer.getCustomerInfoBycredit_id",context.contextMap);
			
			custInfo.put("credit_id", context.contextMap.get("creditId"));
			custInfo.put("s_employeeId", context.contextMap.get("s_employeeId"));
			
			if("0".equals(cust_type)) {
				//2张表查询和插入的#变量#名字不同,重新设定
				custInfo.put("natu_name",custInfo.get("CORP_NAME_CN"));
				custInfo.put("cust_code",custInfo.get("CUST_CODE"));
				custInfo.put("natu_idcard",custInfo.get("NATU_IDCARD"));
				custInfo.put("age",custInfo.get("NATU_AGE"));
				custInfo.put("sex",custInfo.get("NATU_GENDER"));
				custInfo.put("home_phone",custInfo.get("NATU_PHONE"));
				custInfo.put("home_addr",custInfo.get("NATU_HOME_ADDRESS"));
				custInfo.put("work_unit",custInfo.get("NATU_WORK_UNITS"));
				custInfo.put("mobile_phone",custInfo.get("NATU_MOBILE"));
				custInfo.put("mate_name",custInfo.get("NATU_MATE_NAME"));
				//custInfo.put("mate_age",custInfo.get(""));
				custInfo.put("mate_idcard",custInfo.get("NATU_MATE_IDCARD"));
				custInfo.put("mate_work_unit",custInfo.get("NATU_MATE_WORK_UNITS"));
				//custInfo.put("mate_phone",custInfo.get(""));
				custInfo.put("mate_mobile",custInfo.get("NATU_MATE_MOBILE"));
				custInfo.put("natu_zip",custInfo.get("NATU_ZIP"));
				sqlMapper.insert("creditCustomerCorp.createCreditNatunal",custInfo);
				
			} else if("1".equals(cust_type)) {
				
				if(custInfo.get("REGISTERED_CAPITAL")==null) {
					custInfo.put("REGISTERED_CAPITAL",0);
				}
				if(custInfo.get("CONTRIBUTED_CAPITAL")==null) {
					custInfo.put("CONTRIBUTED_CAPITAL",0);
				}
				
				sqlMapper.insert("creditCustomerCorp.createCreditCustomerCorp",custInfo);
			}*/
			
			//****************************************************************************************************
			sqlMapper.commitTransaction() ;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加错误!请联系管理员");
		} finally{
			try {
				sqlMapper.endTransaction() ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		StringBuffer log=new StringBuffer();
//		BusinessLog.addBusinessLog(creditId, null, "现场调查报告", "生成", credti_code,
//				"", 1, Long.parseLong(context.contextMap.get("s_employeeId")
//						+ ""), null);
		
		//如果客户类型是既有客户,则复制以前的报告信息到,公司沿革,担保人信息,过往记录,财务报表,add by ShenQi 2012-9-24
		//已提交和续约客户合并为既有客户
		if("已提交".equals(context.contextMap.get("type"))||"续签客户".equals(context.contextMap.get("type"))||"既有客户".equals(context.contextMap.get("type"))) {
			try {
				//****************************************************************************************************
				//复制公司沿革
				/*Integer credit_id=(Integer)DataAccessor.query("common.getNewestCreditId",context.contextMap,RS_TYPE.OBJECT);*///通过客户ID获得最近的报告号
				//现在的报告号让用户选择 modify by ShenQi
				Integer credit_id=0;
				if(context.contextMap.get("PERIOD_CREDIT")==null||"".equals(context.contextMap.get("PERIOD_CREDIT"))) {
					Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.selectCreditForUpdate&credit_id="+creditId);
					return;
				} else {
					credit_id=Integer.valueOf(context.contextMap.get("PERIOD_CREDIT").toString());
				}
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("credit_id",credit_id);
				
				logger.debug("复制公司沿革---法人基本信息---开始");
				log.append("复制公司沿革---法人基本信息---开始<br>");
				//通过最近的报告号获得法人基本信息
				Map<String,Object> cmpyInfo=(Map<String,Object>)DataAccessor.query("creditCustomerCorp.getCreditCustomerCorpByCreditId",param,RS_TYPE.MAP);
				if(cmpyInfo==null) {
					log.append("复制公司沿革---公司银行账户---查无数据<br>");
				} else {
					cmpyInfo.put("credit_id",context.contextMap.get("credit_id"));
					cmpyInfo.put("s_employeeId",context.contextMap.get("s_employeeId"));
					cmpyInfo.put("experience",cmpyInfo.get("EXPERIENCE"));
					cmpyInfo.put("isListed",cmpyInfo.get("ISLISTED"));
					DataAccessor.execute("creditCustomerCorp.createCreditCustomerCorp",cmpyInfo,OPERATION_TYPE.INSERT);//法人基本信息
				}
				logger.debug("复制公司沿革---法人基本信息---结束");
				log.append("复制公司沿革---法人基本信息---结束<br>");
				
				logger.debug("复制公司沿革---公司银行账户---开始");
				log.append("复制公司沿革---公司银行账户---开始<br>");
				//通过最近的报告号获得公司基本账户  基本账户STATE=0
				//通过最近的报告号获得公司其他账户  其他账户STATE=1
				List<Map<String,Object>> bankAccountList=(List<Map<String,Object>>)DataAccessor.query("creditCustomerCorp.getCreditCorpBankAccountByCreditId",param,RS_TYPE.LIST);
				Map<String,Object> bankAccount=null;
				for(int i=0;bankAccountList!=null&&i<bankAccountList.size();i++) {
					bankAccount=bankAccountList.get(i);
					bankAccount.put("CREDIT_ID",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditCustomerCorp.createCreditCorpBankAccount",bankAccount,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制公司沿革---公司银行账户---结束");
				log.append("复制公司沿革---公司银行账户---结束<br>");
				
				logger.debug("复制公司沿革---主要产品---开始");
				log.append("复制公司沿革---主要产品---开始<br>");
				//通过最近的报告号获得主要产品
				List<Map<String,Object>> mainProductionList=(List<Map<String,Object>>)DataAccessor.query("creditReportManage.getMainProduction",param,RS_TYPE.LIST);
				Map<String,Object> mainProduction=null;
				for(int i=0;mainProductionList!=null&&i<mainProductionList.size();i++) {
					mainProduction=mainProductionList.get(i);
					mainProduction.put("s_employeeId",context.contextMap.get("s_employeeId"));
					mainProduction.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditReportManage.insertMainProduction",mainProduction,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制公司沿革---主要产品---结束");
				log.append("复制公司沿革---主要产品---结束<br>");
				
				logger.debug("复制公司沿革---公司股东---开始");
				log.append("复制公司沿革---公司股东---开始<br>");
				//通过最近的报告号获得公司股东信息
				List<Map<String,Object>> bossList=(List<Map<String,Object>>)DataAccessor.query("creditCustomerCorp.getCreditCorpShareholderByCreditId",param,RS_TYPE.LIST);
				Map<String,Object> boss=null;
				for(int i=0;bossList!=null&&i<bossList.size();i++) {
					boss=bossList.get(i);
					boss.put("CREDIT_ID",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditCustomerCorp.createCreditCorpShareholder",boss,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制公司沿革---公司股东---结束");
				log.append("复制公司沿革---公司股东---结束<br>");
				
				logger.debug("复制公司沿革---公司项目---开始");
				log.append("复制公司沿革---公司项目---开始<br>");
				//通过最近的报告号获得公司项目信息
				List<Map<String,Object>> projectList=(List<Map<String,Object>>)DataAccessor.query("creditCustomerCorp.getCreditCorpProjectByCreditId",param,RS_TYPE.LIST);
				Map<String,Object> project=null;
				for(int i=0;projectList!=null&&i<projectList.size();i++) {
					project=projectList.get(i);
					project.put("CREDIT_ID",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditCustomerCorp.createCreditCorpProject",project,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制公司沿革---公司项目---结束");
				log.append("复制公司沿革---公司项目---结束<br>");
				
				logger.debug("复制公司沿革---实际经营者---开始");
				log.append("复制公司沿革---实际经营者---开始<br>");
				//通过最近的报告号获得实际经营者
				Map<String,Object> dealMan=(Map<String,Object>)DataAccessor.query("trueContact.getTrueContactByCreditId",param,RS_TYPE.MAP);
				if(dealMan==null) {
					log.append("复制公司沿革---实际经营者---查无数据<br>");
				} else {
					dealMan.put("credit_id",context.contextMap.get("credit_id"));
					dealMan.put("MODIFY_ID",context.contextMap.get("s_employeeId"));
					dealMan.put("TC_NAME",dealMan.get("NAME"));
					dealMan.put("TC_TELEPHONE",dealMan.get("TELEPHONE"));
					dealMan.put("TC_POSTCODE",dealMan.get("POSTCODE"));
					dealMan.put("TC_ADDRESS",dealMan.get("ADDRESS"));
					dealMan.put("TC_PHONE1",dealMan.get("PHONE1"));
					dealMan.put("TC_PHONE2",dealMan.get("PHONE2"));
					dealMan.put("TC_EMAIL",dealMan.get("EMAIL"));
					dealMan.put("TC_MSN",dealMan.get("MSN"));
					DataAccessor.execute("trueContact.createTrueContact",dealMan,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制公司沿革---实际经营者---结束");
				log.append("复制公司沿革---实际经营者---结束<br>");
				//****************************************************************************************************
				
				//****************************************************************************************************
				//复制担保人信息
				
				//自然人的资产
				List<Map<String,Object>> natuGuyAssetList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getNatuProperty",param,RS_TYPE.LIST);
				Map<String,Object> natuGuyAsset=null;
				//自然人部分
				logger.debug("复制担保人---自然人---开始");
				log.append("复制担保人---自然人---开始<br>");
				List<Map<String,Object>> natuGuyList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getAllVouchNatuShowByCredit",param,RS_TYPE.LIST);
				Map<String,Object> natuGuy=null;
				for(int i=0;natuGuyList!=null&&i<natuGuyList.size();i++) {
					natuGuy=natuGuyList.get(i);
					natuGuy.put("PRCD_ID",context.contextMap.get("credit_id"));
					natuGuy.put("s_employeeId",context.contextMap.get("s_employeeId"));
					String pron_id=String.valueOf(DataAccessor.execute("creditVoucher.insertNatuByCredit",natuGuy,OPERATION_TYPE.INSERT));
					for(int j=0;j<natuGuyAssetList.size();j++) {
						if(String.valueOf(natuGuy.get("PRON_ID")).equals(String.valueOf(natuGuyAssetList.get(j).get("VOUCH_ID")))) {
							natuGuyAsset=natuGuyAssetList.get(j);
							natuGuyAsset.put("VOUCH_ID",pron_id);
							natuGuyAsset.put("CREDIT_ID",context.contextMap.get("credit_id"));
							DataAccessor.execute("creditVoucher.createProperty",natuGuyAsset,OPERATION_TYPE.INSERT);
						}
					}
				}
				logger.debug("复制担保人---自然人---结束");
				log.append("复制担保人---自然人---结束<br>");
				
				logger.debug("复制担保人---法人---法人基本信息---开始");
				//法人部分
				//担保人法人基本信息
				List<Map<String,Object>> corpList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.selectCorpByCreditId",param,RS_TYPE.LIST);
				Map<String,Object> corp=null;
				String PJCCC_ID=null;
				for(int j=0;corpList!=null&&j<corpList.size();j++) {
					corp=corpList.get(j);
					corp.put("credit_id",context.contextMap.get("credit_id"));
					corp.put("s_employeeId",context.contextMap.get("s_employeeId"));
					corp.put("flagPermit",context.contextMap.get("FLAGPERMIT"));
					corp.put("linkflagPermit",context.contextMap.get("LINKFLAGPERMIT"));
					PJCCC_ID=String.valueOf(DataAccessor.execute("creditVoucher.createCreditCustomerCorp",corp,OPERATION_TYPE.INSERT));
					
					logger.debug("复制担保人---法人---公司账户信息---开始");
					log.append("复制担保人---法人---公司账户信息---开始<br>");
					//公司基本账户,其他账户
					param.put("PJCCC_ID",corp.get("PJCCC_ID"));
					List<Map<String,Object>> corpBankList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getCreditCorpBankAccountByPjcccId",param,RS_TYPE.LIST);
					Map<String,Object> corpBank=null;
					for(int i=0;corpBankList!=null&&i<corpBankList.size();i++) {
						corpBank=corpBankList.get(i);
						corpBank.put("pjccc_id",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createCreditCorpBankAccount",corpBank,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---公司账户信息---结束");
					log.append("复制担保人---法人---公司账户信息---结束<br>");
					
					logger.debug("复制担保人---法人---公司股东信息---开始");
					log.append("复制担保人---法人---公司股东信息---开始<br>");
					//公司股东及份额
					List<Map<String,Object>> corpBossList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getCreditShareholderByPjcccId",param,RS_TYPE.LIST);
					Map<String,Object> corpBoss=null;
					for(int i=0;corpBossList!=null&&i<corpBossList.size();i++) {
						corpBoss=corpBossList.get(i);
						corpBoss.put("pjccc_id",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createCreditCorpShareholder",corpBoss,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---公司股东信息---结束");
					log.append("复制担保人---法人---公司股东信息---结束<br>");
					
					logger.debug("复制担保人---法人---公司成立信息---开始");
					log.append("复制担保人---法人---公司成立信息---开始<br>");
					//公司成立、历次变动的情况
					List<Map<String,Object>> cmpyList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getCreditCorpProjectByPjcccId",param,RS_TYPE.LIST);
					Map<String,Object> cmpy=null;
					for(int i=0;cmpyList!=null&&i<cmpyList.size();i++) {
						cmpy=cmpyList.get(i);
						cmpy.put("pjccc_id",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createCreditCorpProject",cmpy,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---公司成立信息---结束");
					log.append("复制担保人---法人---公司成立信息---结束<br>");
					
					logger.debug("复制担保人---法人---财务信息---开始");
					log.append("复制担保人---法人---财务信息---开始<br>");
					//财务信息
					List<Map<String,Object>> financeList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getAllFinanceReports",param,RS_TYPE.LIST);
					Map<String,Object> finance=null;
					for(int i=0;financeList!=null&&i<financeList.size();i++) {
						finance=financeList.get(i);
						finance.put("project_item",finance.get("PROJECT_ITEM"));
					    finance.put("ca_cash_price",finance.get("CA_CASH_PRICE"));
						finance.put("ca_short_Invest",finance.get("CA_SHORT_INVEST"));
						finance.put("ca_bills_should",finance.get("CA_BILLS_SHOULD"));
						finance.put("ca_Funds_should",finance.get("CA_FUNDS_SHOULD"));
						finance.put("ca_Goods_stock",finance.get("CA_GOODS_STOCK"));
						finance.put("ca_other",finance.get("CA_OTHER"));
						finance.put("fa_land",finance.get("FA_LAND"));
						finance.put("fa_buildings",finance.get("FA_BUILDINGS"));
						finance.put("fa_equipments",finance.get("FA_EQUIPMENTS"));
						finance.put("fa_rent_Assets",finance.get("FA_RENT_ASSETS"));
						finance.put("fa_transports",finance.get("FA_TRANSPORTS"));
						finance.put("fa_other",finance.get("FA_OTHER"));
						finance.put("fa_Depreciations",finance.get("FA_DEPRECIATIONS"));
						finance.put("fa_Incompleted_projects",finance.get("FA_INCOMPLETED_PROJECTS"));
						finance.put("lang_Invest",finance.get("LANG_INVEST"));
						finance.put("other_Assets",finance.get("OTHER_ASSETS"));
						finance.put("sd_short_debt",finance.get("SD_SHORT_DEBT"));
						finance.put("sd_bills_should",finance.get("SD_BILLS_SHOULD"));
						finance.put("sd_funds_should",finance.get("SD_FUNDS_SHOULD"));
						finance.put("sd_other_pay",finance.get("SD_OTHER_PAY"));
						finance.put("sd_shareholders",finance.get("SD_SHAREHOLDERS"));
						finance.put("sd_one_year",finance.get("SD_ONE_YEAR"));
						finance.put("sd_other",finance.get("SD_OTHER"));
						finance.put("lang_debt",finance.get("LANG_DEBT"));
						finance.put("other_long_debt",finance.get("OTHER_LONG_DEBT"));
						finance.put("other_debt",finance.get("OTHER_DEBT"));
						finance.put("share_capital",finance.get("SHARE_CAPITAL"));
						finance.put("surplus_Capital",finance.get("SURPLUS_CAPITAL"));
						finance.put("surplus_income",finance.get("SURPLUS_INCOME"));
						finance.put("this_losts",finance.get("THIS_LOSTS"));
						finance.put("project_changed",finance.get("PROJECT_CHANGED"));
						finance.put("s_start_date",finance.get("S_START_DATE"));
						finance.put("s_sale_net_income",finance.get("S_SALE_NET_INCOME"));
						finance.put("s_sale_cost",finance.get("S_SALE_COST"));
						finance.put("s_other_gross_profit",finance.get("S_OTHER_GROSS_PROFIT"));
						finance.put("s_operating_expenses",finance.get("S_OPERATING_EXPENSES"));
						finance.put("s_nonbusiness_income",finance.get("S_NONBUSINESS_INCOME"));
						finance.put("s_interest_expense",finance.get("S_INTEREST_EXPENSE"));
						finance.put("s_other_nonbusiness_expense",finance.get("S_OTHER_NONBUSINESS_EXPENSE"));
						finance.put("s_income_tax_expense",finance.get("S_INCOME_TAX_EXPENSE"));
						finance.put("ca_other_Funds_should",finance.get("CA_OTHER_FUNDS_SHOULD"));
						finance.put("credit_id",context.contextMap.get("credit_id"));
						finance.put("pjccc_id",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createVoucherReport",finance,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---财务信息---结束");
					log.append("复制担保人---法人---财务信息---结束<br>");
					
					logger.debug("复制担保人---法人---其他调查说明---开始");
					log.append("复制担保人---法人---其他调查说明---开始<br>");
					//其他调查说明
					List<Map<String,Object>> spyList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getCreditCorpPriorProjectsByPjcccId",param,RS_TYPE.LIST);
					Map<String,Object> spy=null;
					for(int i=0;spyList!=null&&i<spyList.size();i++) {
						spy=spyList.get(i);
						spy.put("pjccc_id",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createCreditPriorProjects",spy,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---其他调查说明---结束");
					log.append("复制担保人---法人---其他调查说明---结束<br>");
					
					logger.debug("复制担保人---法人---名下资产---开始");
					log.append("复制担保人---法人---名下资产---开始<br>");
					//名下资产
					List<Map<String,Object>> assetList=(List<Map<String,Object>>)DataAccessor.query("creditVoucher.getCorpPropertyForCopy",param,RS_TYPE.LIST);
					Map<String,Object> asset=null;
					for(int i=0;assetList!=null&&i<assetList.size();i++) {
						asset=assetList.get(i);
						asset.put("CREDIT_ID",context.contextMap.get("credit_id"));
						asset.put("VOUCH_ID",PJCCC_ID);
						DataAccessor.execute("creditVoucher.createProperty",asset,OPERATION_TYPE.INSERT);
					}
					logger.debug("复制担保人---法人---名下资产---结束");
					log.append("复制担保人---法人---名下资产---结束<br>");
				}
				logger.debug("复制担保人---法人---法人基本信息---结束");
				//****************************************************************************************************
				
				//****************************************************************************************************
				//复制过往记录
				logger.debug("复制过往记录---过往记录---开始");
				log.append("复制过往记录---过往记录---开始<br>");
				//过往记录
				List<Map<String,Object>> contractList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorRecords",param,RS_TYPE.LIST);
				Map<String,Object> contract=null;
				for(int i=0;contractList!=null&&i<contractList.size();i++) {
					contract=contractList.get(i);
					contract.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorRecords",contract,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---过往记录---结束");
				log.append("复制过往记录---过往记录---结束<br>");
				
				logger.debug("复制过往记录---过往项目---开始");
				log.append("复制过往记录---过往项目---开始<br>");
				//过往项目
				List<Map<String,Object>> pastProjectList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCreditPriorProjects",param,RS_TYPE.LIST);
				Map<String,Object> pastProject=null;
				for(int i=0;pastProjectList!=null&&i<pastProjectList.size();i++) {
					pastProject=pastProjectList.get(i);
					pastProject.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorProjects",pastProject,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---过往项目---结束");
				log.append("复制过往记录---过往项目---结束<br>");
				
				logger.debug("复制过往记录---厂房信息,银行往来---开始");
				log.append("复制过往记录---厂房信息,银行往来---开始<br>");
				//厂房信息,银行往来
				Map<String,Object> bankPayMap=(Map<String,Object>)DataAccessor.query("creditPriorRecords.getcreditPriorFactoryBuld",param,RS_TYPE.MAP);
				if(bankPayMap==null) {
					log.append("复制过往记录---厂房信息,银行往来---查无数据<br>");
				} else {
					bankPayMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					bankPayMap.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorFactoryBulding",bankPayMap,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---厂房信息,银行往来---结束");
				log.append("复制过往记录---厂房信息,银行往来---结束<br>");
				
				logger.debug("复制过往记录---经营效益分析---开始");
				log.append("复制过往记录---经营效益分析---开始<br>");
				//经营效益分析
				Map<String,Object> profitMap=(Map<String,Object>)DataAccessor.query("creditPriorRecords.getcreditPriorSubmitMachine",param,RS_TYPE.MAP);
				if(profitMap==null) {
					log.append("复制过往记录---经营效益分析---查无数据<br>");
				} else {
					profitMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					profitMap.put("credit_id",context.contextMap.get("credit_id"));
					profitMap.put("benefit_remark",profitMap.get("REMARK"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorSubmitMachine",profitMap,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---经营效益分析---结束");
				log.append("复制过往记录---经营效益分析---结束<br>");
				
				//设备总备注
				Map<String,Object> remark1Map=(Map<String,Object>)DataAccessor.query("creditPriorRecords.getcreditPriorALLEquipRemarks",param,RS_TYPE.MAP);
				if(remark1Map==null) {
					log.append("复制过往记录---设备总备注---查无数据<br>");
				} else {
					remark1Map.put("s_employeeId",context.contextMap.get("s_employeeId"));
					remark1Map.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorALLEquipRemarks",remark1Map,OPERATION_TYPE.INSERT);
				}
				
				logger.debug("复制过往记录---设备明细---开始");
				log.append("复制过往记录---设备明细---开始<br>");
				//设备明细
				List<Map<String,Object>> equList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getcreditPriorEquipments",param,RS_TYPE.LIST);
				Map<String,Object> equ=null;
				for(int i=0;equList!=null&&i<equList.size();i++) {
					equ=equList.get(i);
					equ.put("s_employeeId",context.contextMap.get("s_employeeId"));
					equ.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorEquipments",equ,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---设备明细---结束");
				log.append("复制过往记录---设备明细---结束<br>");
				
				//主要往来客户总备注
				Map<String,Object> remark2Map=(Map<String,Object>)DataAccessor.query("creditPriorRecords.queryCCRemarkByCreditId",param,RS_TYPE.MAP);
				if(remark2Map==null) {
					
				} else {
					remark2Map.put("s_employeeId",context.contextMap.get("s_employeeId"));
					remark2Map.put("credit_id",context.contextMap.get("credit_id"));
					remark2Map.put("CC_REMARK",remark2Map.get("ALLREMARK"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorCCALLRemarks",remark2Map,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---主要往来客户明细---开始");
				log.append("复制过往记录---主要往来客户明细---开始<br>");
				//主要往来客户明细
				List<Map<String,Object>> customerList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.queryCCByCreditId",param,RS_TYPE.LIST);
				Map<String,Object> customer=null;
				for(int i=0;customerList!=null&&i<customerList.size();i++) {
					customer=customerList.get(i);
					customer.put("s_employeeId",context.contextMap.get("s_employeeId"));
					customer.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorCC",customer,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---主要往来客户明细---结束");
				log.append("复制过往记录---主要往来客户明细---结束<br>");
				
				logger.debug("复制过往记录---主要进货客户---开始");
				log.append("复制过往记录---主要进货客户---开始<br>");
				//主要进货客户
				List<Map<String,Object>> customerIncomeList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getcreditPriorBuyFactorys",param,RS_TYPE.LIST);
				Map<String,Object> customerIncome=null;
				for(int i=0;customerIncomeList!=null&&i<customerIncomeList.size();i++) {
					customerIncome=customerIncomeList.get(i);
					customerIncome.put("s_employeeId",context.contextMap.get("s_employeeId"));
					customerIncome.put("credit_id",context.contextMap.get("credit_id"));
					customerIncome.put("BUYFACTORYNAME",customerIncome.get("FACTORYNAME"));
					customerIncome.put("BUYTHINGKIND",customerIncome.get("THINGKIND"));
					customerIncome.put("BUYMONTHINGOPRICE",customerIncome.get("MONTHINGOPRICE"));
					customerIncome.put("BUYPERCENTGRAVE",customerIncome.get("PERCENTGRAVE"));
					customerIncome.put("BUYPAYCONDITIONS",customerIncome.get("PAYCONDITIONS"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorBuyFactorys",customerIncome,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---主要进货客户---结束");
				log.append("复制过往记录---主要进货客户---结束<br>");
				
				logger.debug("复制过往记录---银行对账单---开始");
				log.append("复制过往记录---银行对账单---开始<br>");
				//银行对账单
				List<Map<String,Object>> bankPayList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getcreditPriorBankCheckBillSix",param,RS_TYPE.LIST);
				Map<String,Object> bankPay=null;
				for(int i=0;bankPayList!=null&&i<bankPayList.size();i++) {
					bankPay=bankPayList.get(i);
					bankPay.put("s_employeeId",context.contextMap.get("s_employeeId"));
					bankPay.put("credit_id",context.contextMap.get("credit_id"));
					bankPay.put("CHECKMONTHFIVE",bankPay.get("CHECKMONTHFINE"));
					bankPay.put("LASTSUMFIVE",bankPay.get("LASTSUMFINE"));
					bankPay.put("MONTHINCOMEFIVE",bankPay.get("MONTHINCOMEFINE"));
					bankPay.put("MONTHCOSTFIVE",bankPay.get("MONTHCOSTFINE"));
					bankPay.put("THISSUMFIVE",bankPay.get("THISSUMFINE"));
					bankPay.put("MONEYFLOWINFIVE",bankPay.get("MONEYFLOWINFINE"));
					bankPay.put("MONEYFLOWINFOUR",bankPay.get("MONEYFLOWIFOUR"));
					bankPay.put("THISSUMTHREE",bankPay.get("THISSUMOTHREE"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorBankCheckBillSix",bankPay,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---银行对账单---结束");
				log.append("复制过往记录---银行对账单---结束<br>");
				
				logger.debug("复制过往记录---内销比重,外销比重---开始");
				log.append("复制过往记录---内销比重,外销比重---开始<br>");
				//内销比重,外销比重
				Map<String,Object> saleMap=(Map<String,Object>)DataAccessor.query("creditPriorRecords.getcreditPriorALLCellFacRemark",param,RS_TYPE.MAP);
				if(saleMap==null) {
					log.append("复制过往记录---内销比重,外销比重---查无数据<br>");
				} else {
					saleMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
					saleMap.put("credit_id",context.contextMap.get("credit_id"));
					DataAccessor.execute("creditPriorRecords.createCreditPriorALLCellFacRemark",saleMap,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制过往记录---内销比重,外销比重---结束");
				log.append("复制过往记录---内销比重,外销比重---结束<br>");
				//****************************************************************************************************
				
				//****************************************************************************************************
				//复制财务报表
				logger.debug("复制财务报表---开始");
				log.append("复制财务报表---开始<br>");
				List<Map<String,Object>> corpFinanceList=(List<Map<String,Object>>)DataAccessor.query("creditPriorRecords.getCorpReports",param,RS_TYPE.LIST);
				Map<String,Object> corpFinance=null;
				for(int i=0;corpFinanceList!=null&&i<corpFinanceList.size();i++) {
					corpFinance=corpFinanceList.get(i);
					corpFinance.put("credit_id",context.contextMap.get("credit_id"));
			        corpFinance.put("project_item",corpFinance.get("PROJECT_ITEM"));
					corpFinance.put("ca_cash_price",corpFinance.get("CA_CASH_PRICE"));
					corpFinance.put("ca_short_Invest",corpFinance.get("CA_SHORT_INVEST"));
					corpFinance.put("ca_bills_should",corpFinance.get("CA_BILLS_SHOULD"));
					corpFinance.put("ca_Funds_should",corpFinance.get("CA_FUNDS_SHOULD"));
					corpFinance.put("ca_Goods_stock",corpFinance.get("CA_GOODS_STOCK"));
					corpFinance.put("ca_other",corpFinance.get("CA_OTHER"));
					corpFinance.put("fa_land",corpFinance.get("FA_LAND"));
					corpFinance.put("fa_buildings",corpFinance.get("FA_BUILDINGS"));
					corpFinance.put("fa_equipments",corpFinance.get("FA_EQUIPMENTS"));
					corpFinance.put("fa_rent_Assets",corpFinance.get("FA_RENT_ASSETS"));
					corpFinance.put("fa_transports",corpFinance.get("FA_TRANSPORTS"));
					corpFinance.put("fa_other",corpFinance.get("FA_OTHER"));
					corpFinance.put("fa_Depreciations",corpFinance.get("FA_DEPRECIATIONS"));
					corpFinance.put("fa_Incompleted_projects",corpFinance.get("FA_INCOMPLETED_PROJECTS"));
					corpFinance.put("lang_Invest",corpFinance.get("LANG_INVEST"));
					corpFinance.put("other_Assets",corpFinance.get("OTHER_ASSETS"));
					corpFinance.put("sd_short_debt",corpFinance.get("SD_SHORT_DEBT"));
					corpFinance.put("sd_bills_should",corpFinance.get("SD_BILLS_SHOULD"));
					corpFinance.put("sd_funds_should",corpFinance.get("SD_FUNDS_SHOULD"));
					corpFinance.put("sd_other_pay",corpFinance.get("SD_OTHER_PAY"));
					corpFinance.put("sd_shareholders",corpFinance.get("SD_SHAREHOLDERS"));
					corpFinance.put("sd_one_year",corpFinance.get("SD_ONE_YEAR"));
					corpFinance.put("sd_other",corpFinance.get("SD_OTHER"));
					corpFinance.put("lang_debt",corpFinance.get("LANG_DEBT"));
					corpFinance.put("other_long_debt",corpFinance.get("OTHER_LONG_DEBT"));
					corpFinance.put("other_debt",corpFinance.get("OTHER_DEBT"));
					corpFinance.put("share_capital",corpFinance.get("SHARE_CAPITAL"));
					corpFinance.put("surplus_Capital",corpFinance.get("SURPLUS_CAPITAL"));
					corpFinance.put("surplus_income",corpFinance.get("SURPLUS_INCOME"));
					corpFinance.put("this_losts",corpFinance.get("THIS_LOSTS"));
					corpFinance.put("project_changed",corpFinance.get("PROJECT_CHANGED"));
					corpFinance.put("s_start_date",corpFinance.get("S_START_DATE"));
					corpFinance.put("s_sale_net_income",corpFinance.get("S_SALE_NET_INCOME"));
					corpFinance.put("s_sale_cost",corpFinance.get("S_SALE_COST"));
					corpFinance.put("s_other_gross_profit",corpFinance.get("S_OTHER_GROSS_PROFIT"));
					corpFinance.put("s_operating_expenses",corpFinance.get("S_OPERATING_EXPENSES"));
					corpFinance.put("s_nonbusiness_income",corpFinance.get("S_NONBUSINESS_INCOME"));
					corpFinance.put("s_interest_expense",corpFinance.get("S_INTEREST_EXPENSE"));
					corpFinance.put("s_other_nonbusiness_expense",corpFinance.get("S_OTHER_NONBUSINESS_EXPENSE"));
					corpFinance.put("s_income_tax_expense",corpFinance.get("S_INCOME_TAX_EXPENSE"));
					corpFinance.put("ca_other_Funds_should",corpFinance.get("CA_OTHER_FUNDS_SHOULD"));
					DataAccessor.execute("creditPriorRecords.createCorpReport",corpFinance,OPERATION_TYPE.INSERT);
				}
				
				param.put("type",0);
				Map<String,Object> remarkMap1=(Map<String,Object>)DataAccessor.query("creditPriorRecords.selectCorpReportsRemark",param,RS_TYPE.MAP);
				if(remarkMap1==null) {
					log.append("复制财务报表---备注---查无数据<br>");
				} else {
					remarkMap1.put("s_employeeId",context.contextMap.get("s_employeeId"));
					remarkMap1.put("credit_id",context.contextMap.get("credit_id"));
					remarkMap1.put("remark",remarkMap1.get("REMARK"));
					remarkMap1.put("type",remarkMap1.get("TYPE"));
					DataAccessor.execute("creditPriorRecords.insertCorpReportRemark",remarkMap1,OPERATION_TYPE.INSERT);
				}
				
				param.put("type",1);
				Map<String,Object> remarkMap2=(Map<String,Object>)DataAccessor.query("creditPriorRecords.selectCorpReportsRemark",param,RS_TYPE.MAP);
				if(remarkMap2==null) {
					log.append("复制财务报表---备注---查无数据<br>");
				} else {
					remarkMap2.put("s_employeeId",context.contextMap.get("s_employeeId"));
					remarkMap2.put("credit_id",context.contextMap.get("credit_id"));
					remarkMap2.put("remark",remarkMap2.get("REMARK"));
					remarkMap2.put("type",remarkMap2.get("TYPE"));
					DataAccessor.execute("creditPriorRecords.insertCorpReportRemark",remarkMap2,OPERATION_TYPE.INSERT);
				}
				logger.debug("复制财务报表---结束");
				log.append("复制财务报表---结束<br>");
				//****************************************************************************************************
			} catch (Exception e) {
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.selectCreditForUpdate&credit_id="+creditId);
				logger.debug("报告复制出错!报告号:"+context.contextMap.get("credit_id"));
				MailSettingTo mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailSubject("报告复制出错(报告复制出错不影响报告添加)");
				mailSettingTo.setEmailContent("<style>.Body2 { font-family: Arial, Helvetica, sans-serif; font-weight: normal; color:#000000; font-size: 9pt; text-decoration: none}</style>" +
						"<font class='body2'>报告复制出错Trace日志:<br>"+log.toString()+"</font>");
				try {
					this.mailUtilService.sendMail(0,mailSettingTo);
				} catch(Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		
		if (errList.isEmpty()) {
			if("Y".equals(context.contextMap.get("isSalesDesk"))) {//判断是否是从业务人员桌面提交的 add by ShenQi
				Output.jspSendRedirect(context,"defaultDispatcher?__action=creditReport.creditManage&isSalesDesk=Y");
			} else {
			Output.jspSendRedirect(context,
					"defaultDispatcher?__action=creditReport.selectCreditForUpdate&credit_id="
							+ creditId);
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}

	/**
	 * 承租人自动提示
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCustomersForHint(Context context) {
		Map outputMap = new HashMap();
		List customers = null;
		List errList = context.errList ;
		try {
			customers = (List) DataAccessor.query(
					"creditCustomer.getCustomersForHint", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--承租人自动提示错误!请联系管理员");
		}
		outputMap.put("customers", customers);
		outputMap.put("isSalesDesk", context.contextMap.get("isSalesDesk"));
		Output.jsonOutput(outputMap, context);
	}

	/**
	 * 添加报告页面 在客户自动提示时 获取客户id 根据客户id查询 该客户 项目 合同等信息 显示在添加报告页面中
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getCustInfo(Context context) {
		Map outputMap = new HashMap();
		Map custInfo = null;
		List<Map> custLevel = null;
		Map grantcustinfo = null;
		List<Map> groupNumIdlist = null;
		List errList = context.errList ;
		String groupNumId=context.contextMap.get("s_employeeId").toString();
		context.contextMap.put("groupNumId", Integer.parseInt(groupNumId));
		try {
			custInfo = (Map) DataAccessor.query("creditCustomer.custInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			custLevel = DictionaryUtil.getDictionary("承租人级别");
			for (int i = 0; i < custLevel.size(); i++) {
				if (DataUtil.intUtil(custInfo.get("CUST_LEVEL")) == DataUtil
						.intUtil(custLevel.get(i).get("CODE"))) {
					custInfo.put("custLevel", custLevel.get(i).get("FLAG"));
				}
			}
			grantcustinfo = (Map) DataAccessor.query(
					"creditCustomer.grantcustInfo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			if (grantcustinfo != null) {
				if (grantcustinfo.get("GRANT_PRICE") != null) {
					grantcustinfo.put("GRANT_PRICE", "￥"
							+ updateMon(grantcustinfo.get("GRANT_PRICE")));
					if (grantcustinfo.get("CUST_ID") != null) {
						grantcustinfo.put("LAST_PRICE", "￥"
								+ updateMon(CustomerCredit.getCustCredit(grantcustinfo.get("CUST_ID"))));
					}
				}
				
			}
			groupNumIdlist = (List<Map>) DataAccessor.query("customer.groupNumId", context.contextMap,DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("项目管理--报告添加中提示客户信息错误!请联系管理员");
		}
		outputMap.put("custInfo", custInfo);
		outputMap.put("grantcustInfo", grantcustinfo);
		outputMap.put("groupNumIdlist", groupNumIdlist);
		Output.jsonOutput(outputMap, context);
	}

	/** 财务格式 0.00 */
	private String updateMon(Object content) {
		String str = "";

		if (content == null || DataUtil.doubleUtil(content) == 0.0) {

			str += "0.00";
			return str;

		} else {

			DecimalFormat df1 = new DecimalFormat("#,###.00");

			str += df1.format(Double.parseDouble(content.toString()));
			return str;
		}
	}
	
	//获得产品类型:设备,重车
	public void getProductionType(Context context) {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("dataType","产品类型");
		List resultList=null;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",param,RS_TYPE.LIST);
		} catch (Exception e) {
			
		}
		Output.jsonArrayOutput(resultList,context);
	}
	
	public void getPeriodCredit(Context context) {
		
		
		List<Map<String,String>> resultList=null;
		try {
			resultList=(List<Map<String,String>>)DataAccessor.query("common.getCopyCreditList",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
		}

		Output.jsonArrayListOutput(resultList,context);
	}
}
