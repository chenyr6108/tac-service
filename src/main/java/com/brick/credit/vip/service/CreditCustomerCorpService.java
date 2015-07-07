package com.brick.credit.vip.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

/**
 * 资信 法人信息
 * 
 * @author li shaojie
 * @date May 10, 2010
 */

public class CreditCustomerCorpService extends AService {
	Log logger = LogFactory.getLog(CreditCustomerCorpService.class);

	/**
	 * 查询一条资信信息 用于更新
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditCustomerCorpForUpdate(Context context) {
		Map outputMap = new HashMap();
		Map creditMap = null;
		List corpTypeList = null;
		List IDCardTypeList = null;
		Map creditCustomerCorpMap = new HashMap();
		List corpBankAccount = null;
		List corpSharholder = null;
		List corpProject = null;
		List natufam = null;
		List natusoc = null;
		Map trueContact = null;
		List errList = context.errList;
		/*2011/12/21 Yang Yun Add get default link man. Start*/
		Map<String, Object> linkMan = new HashMap<String, Object>();
		/*2011/12/21 Yang Yun Add get default link man. End*/
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);

			String type = creditMap.get("CUST_TYPE") + "";
			/*2011/12/21 Yang Yun Add get default link man. Start*/
			//Get default link man
			Map<String, Object> paraMapForLinMan = new HashMap<String, Object>();
			paraMapForLinMan.put("cust_id", creditMap.get("CUST_ID"));
			List<Map<String, Object>> linkManTempList = (List) DataAccessor.query("customer.getDefaultLinkMan", paraMapForLinMan, RS_TYPE.LIST);
			if (linkManTempList != null && linkManTempList.size() == 1) {
				linkMan = linkManTempList.get(0);
			} else {
				linkMan.put("CUST_ID", creditMap.get("CUST_ID"));
				linkMan.put("CUST_TYPE", creditMap.get("CUST_TYPE"));
				logger.info("没有默认联系人，或者错误地设置了2个或以上默认的联系人！");
			}
			outputMap.put("linkMan", linkMan);
			/*2011/12/21 Yang Yun Add get default link man. End*/
			trueContact = (Map) DataAccessor.query(
					"trueContact.getTrueContactByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("trueContact", trueContact);
			List companyList = null;
			companyList = (List) DataAccessor.query(
					"companyManage.queryCompanyAlias", null,
					DataAccessor.RS_TYPE.LIST);
			//System.out.println(creditMap.get("DECP_ID").toString()+"=========");
			outputMap.put("companyList", companyList);
			if (type.equals("1")) {

				context.contextMap.put("dataType", "企业类型");
				corpTypeList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpTypeList", corpTypeList);

				context.contextMap.put("dataType", "证件类型");
				IDCardTypeList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("IDCardTypeList", IDCardTypeList);

				creditCustomerCorpMap = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCustomerCorpByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.MAP);

				if (creditCustomerCorpMap == null) {

					context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
					Object obj = DataAccessor.query(
							"creditReportManage.getMaxCredit_id",
							context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					if (obj != null) {
						context.contextMap.put("credit_id", obj);
						creditCustomerCorpMap = (Map) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCustomerCorpByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.MAP);

						if (creditCustomerCorpMap == null) {
							creditCustomerCorpMap = (Map) DataAccessor
									.query(
											"creditCustomer.getCustomerInfoBycredit_id",
											context.contextMap,
											DataAccessor.RS_TYPE.MAP);
						}

						creditCustomerCorpMap.put("PJCCC_ID", "");

						outputMap.put("creditCustomerCorpMap",
								creditCustomerCorpMap);

						corpBankAccount = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("corpBankAccount", corpBankAccount);
						corpSharholder = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCorpShareholderByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("corpSharholder", corpSharholder);
						corpProject = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCorpProjectByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("corpProject", corpProject);
					} else {
						creditCustomerCorpMap = (Map) DataAccessor.query(
								"creditCustomer.getCustomerInfoBycredit_id",
								context.contextMap, DataAccessor.RS_TYPE.MAP);
						outputMap.put("creditCustomerCorpMap",
								creditCustomerCorpMap);
					}

					outputMap.put("showFlag", 1);
				} else {
					outputMap.put("creditCustomerCorpMap",
							creditCustomerCorpMap);
					corpBankAccount = (List) DataAccessor
							.query(
									"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
					outputMap.put("corpBankAccount", corpBankAccount);

					corpSharholder = (List) DataAccessor
							.query(
									"creditCustomerCorp.getCreditCorpShareholderByCreditId",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
					outputMap.put("corpSharholder", corpSharholder);

					corpProject = (List) DataAccessor
							.query(
									"creditCustomerCorp.getCreditCorpProjectByCreditId",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
					outputMap.put("corpProject", corpProject);
					outputMap.put("showFlag", 1);

				}

			}
			if (type.equals("0")) {

				creditCustomerCorpMap = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCustomerNatuByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.MAP);

				if (creditCustomerCorpMap == null) {

					context.contextMap.put("cust_id", creditMap.get("CUST_ID"));
					Object obj = DataAccessor.query(
							"creditReportManage.getMaxCredit_id",
							context.contextMap, DataAccessor.RS_TYPE.OBJECT);
					if (obj != null) {
						context.contextMap.put("credit_id", obj);
						creditCustomerCorpMap = (Map) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCustomerNatuByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.MAP);
						if (creditCustomerCorpMap == null) {

							creditCustomerCorpMap = (Map) DataAccessor
									.query(
											"creditCustomer.getCustomerInfoBycredit_id",
											context.contextMap,
											DataAccessor.RS_TYPE.MAP);
							outputMap.put("creditCustomerCorpMap",
									creditCustomerCorpMap);
						}
						outputMap.put("creditCustomerCorpMap",
								creditCustomerCorpMap);

						corpBankAccount = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("corpBankAccount", corpBankAccount);

						natufam = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditNatuFamByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("natufam", natufam);
						natusoc = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditNatuSocByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("natusoc", natusoc);

						corpProject = (List) DataAccessor
								.query(
										"creditCustomerCorp.getCreditCorpProjectByCreditId",
										context.contextMap,
										DataAccessor.RS_TYPE.LIST);
						outputMap.put("corpProject", corpProject);
					} else {
						creditCustomerCorpMap = (Map) DataAccessor.query(
								"creditCustomer.getCustomerInfoBycredit_id",
								context.contextMap, DataAccessor.RS_TYPE.MAP);
						outputMap.put("creditCustomerCorpMap",
								creditCustomerCorpMap);
					}

					outputMap.put("showFlag", 1);
				} else {
					outputMap.put("creditCustomerCorpMap",
							creditCustomerCorpMap);
					corpBankAccount = (List) DataAccessor
							.query(
									"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
					outputMap.put("corpBankAccount", corpBankAccount);
					natufam = (List) DataAccessor.query(
							"creditCustomerCorp.getCreditNatuFamByCreditId",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("natufam", natufam);
					natusoc = (List) DataAccessor.query(
							"creditCustomerCorp.getCreditNatuSocByCreditId",
							context.contextMap, DataAccessor.RS_TYPE.LIST);
					outputMap.put("natusoc", natusoc);

					corpProject = (List) DataAccessor
							.query(
									"creditCustomerCorp.getCreditCorpProjectByCreditId",
									context.contextMap,
									DataAccessor.RS_TYPE.LIST);
					outputMap.put("corpProject", corpProject);
					outputMap.put("showFlag", 1);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报告管理--现场调查报告修改页初始化错误!请联系管理员");
		} finally {
			if (errList.isEmpty()) {
				Output.jspOutput(outputMap, context, "/credit_vip/creditFrame.jsp");
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	/**
	 * 查询一条资信法人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void selectCreditCustomerCorpForShow(Context context) {
		Map outputMap = new HashMap();
		Map creditMap = null;
		List corpTypeList = null;
		Map creditCustomerCorpMap = null;
		List corpBankAccount = null;
		List corpSharholder = null;
		List corpProject = null;
		List natufam = null;
		List natusoc = null;
		Map memoMap = null;
		Map trueContact = null;
		List errList = context.errList;
		/*2011/12/21 Yang Yun Add get default link man. Start*/
		Map<String, Object> linkMan = new HashMap<String, Object>();
		/*2011/12/21 Yang Yun Add get default link man. End*/
		try {
			context.contextMap.put("data_type", "客户来源");
			creditMap = (Map) DataAccessor.query(
					"creditReportManage.selectCreditBaseInfo",
					context.contextMap, DataAccessor.RS_TYPE.MAP);
			outputMap.put("creditMap", creditMap);
			/*2011/12/21 Yang Yun Add get default link man. Start*/
			//Get default link man
			Map<String, Object> paraMapForLinMan = new HashMap<String, Object>();
			paraMapForLinMan.put("cust_id", creditMap.get("CUST_ID"));
			List<Map<String, Object>> linkManTempList = (List) DataAccessor.query("customer.getDefaultLinkMan", paraMapForLinMan, RS_TYPE.LIST);
			if (linkManTempList != null && linkManTempList.size() == 1) {
				linkMan = linkManTempList.get(0);
			} else {
				logger.info("没有默认联系人，或者错误地设置了2个或以上默认的联系人！");
			}
			outputMap.put("linkMan", linkMan);
			/*2011/12/21 Yang Yun Add get default link man. End*/
			memoMap = (Map) DataAccessor.query(
					"creditReportManage.selectNewMemo", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("memoMap", memoMap);
			String type = creditMap.get("CUST_TYPE") + "";

			trueContact = (Map) DataAccessor.query(
					"trueContact.getTrueContactByCreditId", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("trueContact", trueContact);

			if (type.equals("1")) {
				context.contextMap.put("dataType", "企业类型");
				corpTypeList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpTypeList", corpTypeList);
				creditCustomerCorpMap = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCustomerCorpByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				if (creditCustomerCorpMap == null) {
					creditCustomerCorpMap = (Map) DataAccessor.query(
							"creditCustomer.getCustomerInfoBycredit_id",
							context.contextMap, DataAccessor.RS_TYPE.MAP);
				}
				outputMap.put("creditCustomerCorpMap", creditCustomerCorpMap);
				corpBankAccount = (List) DataAccessor
						.query(
								"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
								context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpBankAccount", corpBankAccount);
				corpSharholder = (List) DataAccessor
						.query(
								"creditCustomerCorp.getCreditCorpShareholderByCreditId",
								context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpSharholder", corpSharholder);
				corpProject = (List) DataAccessor.query(
						"creditCustomerCorp.getCreditCorpProjectByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpProject", corpProject);
				outputMap.put("examineFlag", context.contextMap
						.get("examineFlag"));
				outputMap.put("showFlag", 1);
			}
			if (type.equals("0")) {

				creditCustomerCorpMap = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCustomerNatuByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
				if (creditCustomerCorpMap == null) {
					creditCustomerCorpMap = (Map) DataAccessor.query(
							"creditCustomer.getCustomerInfoBycredit_id",
							context.contextMap, DataAccessor.RS_TYPE.MAP);
				}
				outputMap.put("creditCustomerCorpMap", creditCustomerCorpMap);
				corpBankAccount = (List) DataAccessor
						.query(
								"creditCustomerCorp.getCreditCorpBankAccountByCreditId",
								context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("corpBankAccount", corpBankAccount);

				natufam = (List) DataAccessor.query(
						"creditCustomerCorp.getCreditNatuFamByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("natufam", natufam);
				natusoc = (List) DataAccessor.query(
						"creditCustomerCorp.getCreditNatuSocByCreditId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("natusoc", natusoc);

				outputMap.put("examineFlag", context.contextMap
						.get("examineFlag"));
				outputMap.put("showFlag", 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报告管理--现场调查报告公司沿革错误!请联系管理员");
		} finally {
			if (errList.isEmpty()) {
				if (DataUtil.intUtil(context.contextMap.get("commit_flag")) == 1) {
					outputMap.put("commit_flag", context.contextMap
							.get("commit_flag"));
					Output.jspOutput(outputMap, context,
							"/credit_vip/creditFrameCommit.jsp");
				} else {
					Output.jspOutput(outputMap, context,
							"/credit_vip/creditFrameShow.jsp");
				}
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	/**
	 * 添加或更新一条资信法人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createOrUpdateCreditCustomerCorp(Context context) {
		Map outputMap = new HashMap();
		Map creditCustomerCorpMap = null;
		Map creditMap = null;
		SqlMapClient sqlMapper = null;
		List errList = context.errList;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

			//加入后台验证 add by ShenQi
			this.validate(context,errList);
			if(!errList.isEmpty()) {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
				return;
			}
			
			sqlMapper.delete(
					"creditCustomerCorp.deleteCreditCustomerCorpByCreditId",
					context.contextMap);

			sqlMapper.insert("creditCustomerCorp.createCreditCustomerCorp",
					context.contextMap);

			// 添加或更新实际联系人信息
			String s[] = HTMLUtil.getParameterValues(
					context.request, "CARD_ID", "");
			String flag = (String)context.contextMap.get("CARD_FLAG");
			if("0".equals(flag)){
				context.contextMap.put("CARD_ID", s[0]);
			}else{
				context.contextMap.put("CARD_ID", s[1]);
			}
			String trueContact_id = (String) context.contextMap
					.get("trueContact_id");
			if (trueContact_id.equals("")) {
				sqlMapper.insert("trueContact.createTrueContact",
						context.contextMap);
			} else {
				sqlMapper.update("trueContact.updateTrueContact",
						context.contextMap);
			}

			// 公司基本账户
			sqlMapper.update(
					"creditCustomerCorp.deleteCreditCorpBankAccountById",
					context.contextMap);
			Map baseBankAccount = new HashMap();
			String B_PCCBA_ID = (String) context.contextMap.get("B_PCCBA_ID");
			baseBankAccount.put("BANK_NAME", context.contextMap
					.get("B_BANK_NAME"));
			baseBankAccount.put("BANK_ACCOUNT", context.contextMap
					.get("B_BANK_ACCOUNT"));
			baseBankAccount.put("STATE", "0");
			baseBankAccount.put("CREDIT_ID", context.contextMap
					.get("credit_id"));
			baseBankAccount.put("PCCBA_ID", B_PCCBA_ID);

			sqlMapper.insert("creditCustomerCorp.createCreditCorpBankAccount",
					baseBankAccount);

			// 公司其他账户
			sqlMapper.delete(
					"creditCustomerCorp.deleteCreditCorpBankAccountByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("BANK_NAME") != null) {
				String[] BANK_NAME = HTMLUtil.getParameterValues(
						context.request, "BANK_NAME", "");
				String[] BANK_ACCOUNT = HTMLUtil.getParameterValues(
						context.request, "BANK_ACCOUNT", "");
				for (int i = 0; i < BANK_NAME.length; i++) {
					Map bankAccount = new HashMap();
					bankAccount.put("BANK_NAME", BANK_NAME[i]);
					bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
					bankAccount.put("STATE", "1");
					bankAccount.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					sqlMapper.insert(
							"creditCustomerCorp.createCreditCorpBankAccount",
							bankAccount);
				}
			}

			// 公司股东信息
			sqlMapper.delete(
					"creditCustomerCorp.deleteCreditCorpShareholderByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("HOLDER_NAME") != null) {
				String[] HOLDER_NAME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_NAME", "");
				String[] HOLDER_CAPITAL = HTMLUtil.getParameterValues(
						context.request, "HOLDER_CAPITAL", null);
				String[] HOLDER_WAY = HTMLUtil.getParameterValues(
						context.request, "HOLDER_WAY", "");
				String[] HOLDER_RATE = HTMLUtil.getParameterValues(
						context.request, "HOLDER_RATE", null);
				String[] HOLDER_MOME = HTMLUtil.getParameterValues(
						context.request, "HOLDER_MOME", "");
				for (int i = 0; i < HOLDER_NAME.length; i++) {
					Map shareholder = new HashMap();
					shareholder.put("HOLDER_NAME", HOLDER_NAME[i]);
					shareholder.put("HOLDER_CAPITAL", HOLDER_CAPITAL[i]);
					shareholder.put("HOLDER_WAY", HOLDER_WAY[i]);
					shareholder.put("HOLDER_RATE", HOLDER_RATE[i]);
					shareholder.put("HOLDER_MOME", HOLDER_MOME[i]);
					shareholder.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					sqlMapper.insert(
							"creditCustomerCorp.createCreditCorpShareholder",
							shareholder);
				}
			}

			// 公司项目信息
			sqlMapper.delete(
					"creditCustomerCorp.deleteCreditCorpProjectByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("PROJECT_NAME") != null) {
				String[] PROJECT_NAME = HTMLUtil.getParameterValues(
						context.request, "PROJECT_NAME", "");
				String[] PROJECT_DATE = HTMLUtil.getParameterValues(
						context.request, "PROJECT_DATE", "");
				String[] PROJECT_CONTENT = HTMLUtil.getParameterValues(
						context.request, "PROJECT_CONTENT", "");
				for (int i = 0; i < PROJECT_NAME.length; i++) {
					Map project = new HashMap();
					project.put("PROJECT_NAME", PROJECT_NAME[i]);
					project.put("PROJECT_DATE", PROJECT_DATE[i]);
					project.put("PROJECT_CONTENT", PROJECT_CONTENT[i]);
					project.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					sqlMapper.insert(
							"creditCustomerCorp.createCreditCorpProject",
							project);
				}
			}
			Integer cnt = (Integer) sqlMapper.queryForObject(
					"creditCustomerCorp.validOldCustomer", context.contextMap);
			if (cnt <= 5) {
				sqlMapper.update("creditCustomer.updateCustomerCorp",
						context.contextMap);

				/*2011/12/26 Yang Yun 删除客户联系人信息的验证，因为是联动的，只要验证联系人是否存在。
				String link = context.getContextMap().get("LINK_MAN") + "";

				if (link != null & link != "") {

					Integer cust_id = Integer.parseInt(sqlMapper
							.queryForObject("creditCustomer.selectID",
									context.contextMap)
							+ "");
					context.contextMap.put("cust_id", cust_id);
					Integer inm = (Integer) sqlMapper.queryForObject(
							"creditCustomer.selectCorpLinkcountBycust_id",
							context.contextMap);
					if (inm > 0) {

						Integer culm_id = Integer
								.parseInt(sqlMapper
										.queryForObject(
												"creditCustomer.selectCorpLinkidBycust_id",
												context.contextMap)
										+ "");
						context.contextMap.put("culm_id", culm_id);

						sqlMapper.update("creditCustomer.updateCorpLinkman",
								context.contextMap);
					} else {
						context.contextMap.put("link_type", "0");
						sqlMapper.insert("creditCustomer.insertCorpLinkman",
								context.contextMap);
					}
				}*/
			}
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报告管理--现场调查报告添加/更新法人错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			if (errList.isEmpty()) {
				Output
						.jspSendRedirect(
								context,
								"defaultDispatcher?__action=creditPriorRecordsVip.getCreditPriorRecords&credit_id="
										+ context.contextMap.get("credit_id"));
			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}

	/**
	 * 添加或更新一条资信自然人信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void createOrUpdateCreditCustomerNatu(Context context) {
		Map outputMap = new HashMap();
		Map creditCustomerCorpMap = null;
		Map creditMap = null;
		SqlMapClient sqlMapper = null;
		List errList = context.errList;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();

			// 法人基本信息
			String creditCustomerNatu_id = (String) context.contextMap
					.get("creditCustomerNatu_id");
			String id_card_type = (String) context.contextMap
					.get("ID_CARD_TYPE");

			if (id_card_type.equals("1")) {
				context.contextMap.put("natu_idcard", context.contextMap
						.get("natu_idcard1"));
			} else {
				context.contextMap.put("natu_idcard", context.contextMap
						.get("natu_idcard2"));
			}
			if (creditCustomerNatu_id.equals("")) {
				sqlMapper.insert("creditCustomerCorp.createCreditNatunal",
						context.contextMap);
			} else {
				sqlMapper.update(
						// 修改，
						"creditCustomerCorp.updateCreditCustomerNatuById",
						context.contextMap);
			}

			// 公司基本账户
			Map baseBankAccount = new HashMap();
			String B_PCCBA_ID = (String) context.contextMap.get("B_PCCBA_ID");
			baseBankAccount.put("BANK_NAME", context.contextMap
					.get("B_BANK_NAME"));
			baseBankAccount.put("BANK_ACCOUNT", context.contextMap
					.get("B_BANK_ACCOUNT"));
			baseBankAccount.put("STATE", "0");
			baseBankAccount.put("CREDIT_ID", context.contextMap
					.get("credit_id"));
			baseBankAccount.put("PCCBA_ID", B_PCCBA_ID);
			if (B_PCCBA_ID.equals("")) {
				sqlMapper.insert(
						"creditCustomerCorp.createCreditCorpBankAccount",
						baseBankAccount);
			} else {
				sqlMapper.update(
						"creditCustomerCorp.updateCreditCorpBankAccountById",
						baseBankAccount);
			}

			// 公司其他账户
			sqlMapper.delete(
					"creditCustomerCorp.deleteCreditCorpBankAccountByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("BANK_NAME") != null) {
				String[] BANK_NAME = HTMLUtil.getParameterValues(
						context.request, "BANK_NAME", "");
				String[] BANK_ACCOUNT = HTMLUtil.getParameterValues(
						context.request, "BANK_ACCOUNT", "");
				for (int i = 0; i < BANK_NAME.length; i++) {
					Map bankAccount = new HashMap();
					bankAccount.put("BANK_NAME", BANK_NAME[i]);
					bankAccount.put("BANK_ACCOUNT", BANK_ACCOUNT[i]);
					bankAccount.put("STATE", "1");
					bankAccount.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					sqlMapper.insert(
							"creditCustomerCorp.createCreditCorpBankAccount",
							bankAccount);
				}
			}

			// 改成 家庭关系
			sqlMapper.delete("creditCustomerCorp.deleteCreditFamByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("fam_name") != null) {
				String[] HOLDER_NAME = HTMLUtil.getParameterValues(
						context.request, "fam_name", "");
				String[] HOLDER_CAPITAL = HTMLUtil.getParameterValues(
						context.request, "fam_relation", "");
				String[] HOLDER_WAY = HTMLUtil.getParameterValues(
						context.request, "fam_link", "");
				String[] HOLDER_RATE = HTMLUtil.getParameterValues(
						context.request, "fam_addr", "");
				String[] HOLDER_MOME = HTMLUtil.getParameterValues(
						context.request, "remark", "");
				for (int i = 0; i < HOLDER_NAME.length; i++) {
					Map shareholder = new HashMap();
					shareholder.put("fam_name", HOLDER_NAME[i]);
					shareholder.put("fam_relation", HOLDER_CAPITAL[i]);
					shareholder.put("fam_link", HOLDER_WAY[i]);
					shareholder.put("fam_addr", HOLDER_RATE[i]);
					shareholder.put("remark", HOLDER_MOME[i]);
					shareholder.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					shareholder.put("s_employeeId", context.contextMap
							.get("s_employeeId"));
					sqlMapper.insert("creditCustomerCorp.createNatuFam",
							shareholder);
				}
			}

			// 社会关系
			sqlMapper.delete("creditCustomerCorp.deleteCreditSocByCreditId",
					context.contextMap);
			if (context.request.getParameterValues("soci_name") != null) {
				String[] PROJECT_NAME = HTMLUtil.getParameterValues(
						context.request, "soci_name", "");
				String[] PROJECT_DATE = HTMLUtil.getParameterValues(
						context.request, "soci_work", "");
				String[] PROJECT_CONTENT = HTMLUtil.getParameterValues(
						context.request, "soci_position", "");
				String[] soci_link = HTMLUtil.getParameterValues(
						context.request, "soci_link", "");
				String[] soci_remark = HTMLUtil.getParameterValues(
						context.request, "soci_remark", "");
				for (int i = 0; i < PROJECT_NAME.length; i++) {
					Map project = new HashMap();
					project.put("soci_name", PROJECT_NAME[i]);
					project.put("soci_work", PROJECT_DATE[i]);
					project.put("soci_position", PROJECT_CONTENT[i]);
					project.put("soci_link", soci_link[i]);
					project.put("soci_remark", soci_remark[i]);

					project.put("s_employeeId", context.contextMap
							.get("s_employeeId"));
					project.put("CREDIT_ID", context.contextMap
							.get("credit_id"));
					sqlMapper.insert("creditCustomerCorp.createNatuSoc",
							project);
				}
			}
			Integer cnt = (Integer) sqlMapper.queryForObject(
					"creditCustomerCorp.validOldCustomer", context.contextMap);
			if (cnt <= 1) {

				sqlMapper.update("creditCustomer.updateCustomerNatu",
						context.contextMap);

				String link = context.getContextMap().get("natu_linkman") + "";
				if (link != null & !"".equals(link)) {

					Integer cust_id = Integer.parseInt(sqlMapper
							.queryForObject("creditCustomer.selectID",
									context.contextMap)
							+ "");

					context.contextMap.put("cust_id", cust_id);

					Integer inm = (Integer) sqlMapper.queryForObject(
							"creditCustomer.selectLinkcount",
							context.contextMap);

					if (inm > 0) {

						Integer culm_id = Integer.parseInt(sqlMapper
								.queryForObject("creditCustomer.selectLinkid",
										context.contextMap)
								+ "");
						context.contextMap.put("culm_id", culm_id);

						sqlMapper.update("creditCustomer.updateCustLinkman",
								context.contextMap);

					} else {

						sqlMapper.insert("creditCustomer.insertCustLinkman",
								context.contextMap);
					}
				}
			}
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("报告管理--现场调查报告添加/更新自然人错误!请联系管理员") ;
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				
			}
			if (errList.isEmpty()) {
				Output
						.jspSendRedirect(
								context,
								"defaultDispatcher?__action=creditPriorRecordsVip.getCreditPriorRecords&credit_id="
										+ context.contextMap.get("credit_id"));

			} else {
				outputMap.put("errList", errList);
				Output.jspOutput(outputMap, context, "/error.jsp");
			}
		}
	}
	
	private void validate(Context context,List<String> errorList) {
		
		if(((String)context.contextMap.get("MEMO")).length()>120000) {
			errorList.add("备注长度超过120000!");
		}
		if(((String)context.contextMap.get("OTHER_INFO")).length()>120000) {
			errorList.add("其他信息长度超过120000!");
		}
	}
}
