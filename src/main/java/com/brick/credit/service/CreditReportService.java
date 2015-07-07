package com.brick.credit.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.risk_audit.service.RiskAuditService;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.NumberUtils;
import com.brick.visitation.service.VisitationService;

public class CreditReportService extends BaseService {
	
	Log logger = LogFactory.getLog(CreditReportService.class);
	
private MailUtilService mailUtilService;
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	private RiskAuditService riskAuditService;
	private VisitationService visitationService;
	
	public RiskAuditService getRiskAuditService() {
		return riskAuditService;
	}
	public void setRiskAuditService(RiskAuditService riskAuditService) {
		this.riskAuditService = riskAuditService;
	}

	public VisitationService getVisitationService() {
		return visitationService;
	}

	public void setVisitationService(VisitationService visitationService) {
		this.visitationService = visitationService;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public String doCommitCredit(Context context) throws Exception {
		/**
		 * add by ShenQi 2012-7-26 加入业务副总审批模块
		 * 如果是内资企业,概算成本在50w元以上,实际TR在0-15.4者,或是概算成本在50w元以下(含50w),实际TR在0-16者
		 * 如果是外资企业,概算成本在50w元以上,实际TR在0-13.9者,或是概算成本在50w元以下(含50w),实际TR在0-14.5者
		 * 满足上面条件之一报告审批通过时候需转到业务副总审批方能提交评审
		 * */
		// ***************************************************************************************************************************
		Map<String, Object> outputMap = new HashMap<String, Object>();
		// 加入业务副总审批逻辑
		int creditState = 3;
		String creditRunCode = null;
		Map<String, Object> creditMap = new HashMap<String, Object>();
		try {
			// 如果此报告满足条件提交到业务副总 state=5
			creditMap = (Map<String, Object>) queryForObj(
					"creditReportManage.getStateByCreditId", context.contextMap);
			creditState = Integer.valueOf(creditMap.get("STATE").toString());
			creditRunCode = (String) creditMap.get("CREDIT_RUNCODE");
		} catch (Exception e2) {
			throw e2;
		}

		if (Constants.PRODUCTION_TYPE_3.equals(String.valueOf(creditMap
				.get("PRODUCTION_TYPE")))) {
			// 如果是乘用车报告提交,需要提交到商用车业务副总
			if (creditState != 5) {

				try {
					context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
					update("creditReportManage.updateCreditState",
							context.contextMap);
					context.contextMap.put("statee", 0);
					context.contextMap.put("AUDIT_STATE",
							Constants.AUDIT_STATE_0);// 区域主管审批的
					insert("creditReportManage.insertMemo", context.contextMap);
				} catch (Exception e1) {
					throw e1;
				}

				String crDate = (String) context.contextMap.get("CR_DATE");
				String leRze = (String) context.contextMap.get("LE_RZE");
				if (crDate == null) {
					crDate = "空";
				}
				if (leRze == null) {
					leRze = "空";
				}

				BusinessLog.addBusinessLogWithIp(DataUtil
						.longUtil(context.contextMap.get("credit_id")),
						DataUtil.longUtil(context.contextMap.get("RECT_ID")),
						"业务主管报告审批", "业务主管审批", creditRunCode, "报告审批通过  "
								+ "成立日期：" + crDate + ",融资额：" + leRze, 1,
						DataUtil.longUtil(context.contextMap
								.get("s_employeeId")), null,
						(String) context.contextMap.get("IP"));
				// 发送邮件
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setCreateBy(context.contextMap
						.get("s_employeeId").toString());
				mailSettingTo.setEmailCc((String) context.contextMap
						.get("EMAIL"));
				StringBuffer content = new StringBuffer();
				content.append("副总您好!<br>系统提示:案件号为" + creditRunCode + "需您审批!");
				mailSettingTo.setEmailContent(content.toString());
				try {
					mailUtilService.sendMail(8, mailSettingTo);
				} catch (Exception e) {

				}

				// this.creditExamine(context);
				return null;
			} else {
				logger.debug("业务副总审批通过!");
			}
		} else {
			if (creditState != 5) {// 如果从原先报告审批界面提交 state=3状态为提交主管
				Map<String, Object> resultMap = new HashMap<String, Object>();
				Map<String, Object> resultCorpMap = new HashMap<String, Object>();
				List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
				double moneyInternalCorp = 0;// 内资金额
				double moneyExternalCorp = 0;// 外资金额
				double trInternalCorpUp = 0;// 内资金额以上TR
				double trInternalCorpDown = 0;// 内资金额以下TR
				double trExternalCorpUp = 0;// 外资金额以上TR
				double trExternalCorpDown = 0;// 外资金额以下TR
				String corpType = null;
				String corpName = null;
				try {
					// 从数据字典获得判断范围
					context.contextMap.put("dataType", "业务副总审批条件");
					resultList = (List<Map<String, Object>>) queryForList(
							"dataDictionary.queryDataDictionary",
							context.contextMap);
					for (int i = 0; resultList != null && i < resultList.size(); i++) {
						if ("MONEY_INTERNAL_CORP".equals(resultList.get(i).get(
								"CODE"))) {
							moneyInternalCorp = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						} else if ("MONEY_EXTERNAL_CORP".equals(resultList.get(
								i).get("CODE"))) {
							moneyExternalCorp = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						} else if ("TR_INTERNAL_CORP_UP".equals(resultList.get(
								i).get("CODE"))) {
							trInternalCorpUp = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						} else if ("TR_INTERNAL_CORP_DOWN".equals(resultList
								.get(i).get("CODE"))) {
							trInternalCorpDown = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						} else if ("TR_EXTERNAL_CORP_UP".equals(resultList.get(
								i).get("CODE"))) {
							trExternalCorpUp = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						} else if ("TR_EXTERNAL_CORP_DOWN".equals(resultList
								.get(i).get("CODE"))) {
							trExternalCorpDown = Double
									.valueOf((String) resultList.get(i).get(
											"FLAG"));
						}
					}

					// 获得此报告案件的信息
					resultMap = (Map) queryForObj(
							"creditReportManage.selectCreditScheme",
							context.contextMap);

					// 获得该企业是内资或是外资
					resultCorpMap = (Map) queryForObj(
							"creditCustomerCorp.getCreditCustomerCorpByCreditId",
							context.contextMap);
					if (resultCorpMap != null) {
						corpType = (String) resultCorpMap.get("CORP_TYPE");
						corpName = (String) resultCorpMap.get("CORP_NAME_CN");
					}
				} catch (Exception e) {
					throw e;
				}

				if (resultMap == null) {
					throw new Exception("通过报告ID获得案件信息出错!请联系管理员");
				}
				if (resultMap.get("TR_IRR_RATE") == null
						|| resultMap.get("LEASE_RZE") == null) {
					throw new Exception("通过报告ID获得案件概算成本或者TR出错!请联系管理员");
				}

				double actualTR = NumberUtils.retain3rounded((Double) resultMap
						.get("TR_IRR_RATE") == null ? 0 : (Double) resultMap
						.get("TR_IRR_RATE"));// 实际TR
				double leaseCost = (Double) resultMap.get("LEASE_RZE");// 概算成本

				if ("4".equals(corpType) || "6".equals(corpType)) {// 如果企业类型是外资(包括台资)数据字典中4是外资,6是台资
					if (leaseCost > moneyExternalCorp
							&& actualTR < trExternalCorpUp) {
						logger.debug("外资企业,概算成本大于" + moneyExternalCorp
								+ ",tr小于" + trExternalCorpUp + "!");
						try {
							context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
							update("creditReportManage.updateCreditState",
									context.contextMap);
							context.contextMap.put("statee", 0);
							context.contextMap.put("AUDIT_STATE",
									Constants.AUDIT_STATE_0);// 区域主管审批的
							insert("creditReportManage.insertMemo",
									context.contextMap);

							// 插入日志
							String crDate = (String) context.contextMap
									.get("CR_DATE");
							String leRze = (String) context.contextMap
									.get("LE_RZE");
							if (crDate == null) {
								crDate = "空";
							}
							if (leRze == null) {
								leRze = "空";
							}
							BusinessLog
									.addBusinessLogWithIp(
											DataUtil.longUtil(context.contextMap
													.get("credit_id")),
											DataUtil.longUtil(context.contextMap
													.get("RECT_ID")),
											"业务主管报告审批",
											"业务主管审批",
											creditRunCode,
											"报告审批通过  " + "成立日期：" + crDate
													+ ",融资额：" + leRze,
											1,
											DataUtil.longUtil(context.contextMap
													.get("s_employeeId")),
											null, (String) context.contextMap
													.get("IP"));
							// 发送邮件
							MailSettingTo mailSettingTo = new MailSettingTo();
							mailSettingTo.setCreateBy(context.contextMap.get(
									"s_employeeId").toString());
							mailSettingTo
									.setEmailCc((String) context.contextMap
											.get("EMAIL"));
							StringBuffer content = new StringBuffer();
							content.append("系统提示:案件号为"
									+ creditRunCode
									+ "需您审批(客户名称:"
									+ corpName
									+ ",案件TR:"
									+ actualTR
									+ ",金额:"
									+ NumberUtils.getCurrencyFormat(leaseCost,
											Locale.CHINA)
									+ ")!<br><br>外资企业,概算成本大于"
									+ moneyExternalCorp + ",tr小于"
									+ trExternalCorpUp + "案子!");
							mailSettingTo.setEmailContent(content.toString());
							mailUtilService.sendMail(3, mailSettingTo);

						} catch (Exception e) {
							throw e;
						}
						return null;
					}
					if (leaseCost <= moneyExternalCorp
							&& actualTR < trExternalCorpDown) {
						logger.debug("外资企业,概算成本小于等于" + moneyExternalCorp
								+ ",tr小于" + trExternalCorpDown + "!");
						try {
							context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
							update("creditReportManage.updateCreditState",
									context.contextMap);
							context.contextMap.put("statee", 0);
							context.contextMap.put("AUDIT_STATE",
									Constants.AUDIT_STATE_0);// 区域主管审批的
							insert("creditReportManage.insertMemo",
									context.contextMap);

							// 插入日志
							String crDate = (String) context.contextMap
									.get("CR_DATE");
							String leRze = (String) context.contextMap
									.get("LE_RZE");
							if (crDate == null) {
								crDate = "空";
							}
							if (leRze == null) {
								leRze = "空";
							}
							BusinessLog
									.addBusinessLogWithIp(
											DataUtil.longUtil(context.contextMap
													.get("credit_id")),
											DataUtil.longUtil(context.contextMap
													.get("RECT_ID")),
											"业务主管报告审批",
											"业务主管审批",
											creditRunCode,
											"报告审批通过  " + "成立日期：" + crDate
													+ ",融资额：" + leRze,
											1,
											DataUtil.longUtil(context.contextMap
													.get("s_employeeId")),
											null, (String) context.contextMap
													.get("IP"));
							// 发送邮件
							MailSettingTo mailSettingTo = new MailSettingTo();
							mailSettingTo.setCreateBy(context.contextMap.get(
									"s_employeeId").toString());
							mailSettingTo
									.setEmailCc((String) context.contextMap
											.get("EMAIL"));
							StringBuffer content = new StringBuffer();
							content.append("系统提示:案件号为"
									+ creditRunCode
									+ "需您审批(客户名称:"
									+ corpName
									+ ",案件TR:"
									+ actualTR
									+ ",金额:"
									+ NumberUtils.getCurrencyFormat(leaseCost,
											Locale.CHINA)
									+ ")!<br><br>外资企业,概算成本小于等于"
									+ moneyExternalCorp + ",tr小于"
									+ trExternalCorpDown + "案子!");
							mailSettingTo.setEmailContent(content.toString());
							mailUtilService.sendMail(3, mailSettingTo);

						} catch (Exception e) {
							throw e;
						}
						return null;
					}
				} else {// 如果企业类型是内资(包括)
					if (leaseCost > moneyInternalCorp
							&& actualTR < trInternalCorpUp) {
						logger.debug("内资企业,概算成本大于" + moneyInternalCorp
								+ ",tr小于" + trInternalCorpUp + "!");
						try {
							context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
							update("creditReportManage.updateCreditState",
									context.contextMap);
							context.contextMap.put("statee", 0);
							context.contextMap.put("AUDIT_STATE",
									Constants.AUDIT_STATE_0);// 区域主管审批的
							insert("creditReportManage.insertMemo",
									context.contextMap);

							// 插入日志
							String crDate = (String) context.contextMap
									.get("CR_DATE");
							String leRze = (String) context.contextMap
									.get("LE_RZE");
							if (crDate == null) {
								crDate = "空";
							}
							if (leRze == null) {
								leRze = "空";
							}
							BusinessLog
									.addBusinessLogWithIp(
											DataUtil.longUtil(context.contextMap
													.get("credit_id")),
											DataUtil.longUtil(context.contextMap
													.get("RECT_ID")),
											"业务主管报告审批",
											"业务主管审批",
											creditRunCode,
											"报告审批通过  " + "成立日期：" + crDate
													+ ",融资额：" + leRze,
											1,
											DataUtil.longUtil(context.contextMap
													.get("s_employeeId")),
											null, (String) context.contextMap
													.get("IP"));
							// 发送邮件
							MailSettingTo mailSettingTo = new MailSettingTo();
							mailSettingTo.setCreateBy(context.contextMap.get(
									"s_employeeId").toString());
							mailSettingTo
									.setEmailCc((String) context.contextMap
											.get("EMAIL"));
							StringBuffer content = new StringBuffer();
							content.append("系统提示:案件号为"
									+ creditRunCode
									+ "需您审批(客户名称:"
									+ corpName
									+ ",案件TR:"
									+ actualTR
									+ ",金额:"
									+ NumberUtils.getCurrencyFormat(leaseCost,
											Locale.CHINA)
									+ ")!<br><br>内资企业,概算成本大于"
									+ moneyInternalCorp + ",tr小于"
									+ trInternalCorpUp + "案子!");
							mailSettingTo.setEmailContent(content.toString());
							mailUtilService.sendMail(3, mailSettingTo);

						} catch (Exception e) {
							throw e;
						}
						// this.creditExamine(context);
						return null;
					}
					if (leaseCost <= moneyInternalCorp
							&& actualTR < trInternalCorpDown) {
						logger.debug("内资企业,概算成本小于等于" + moneyInternalCorp
								+ ",tr小于" + trInternalCorpDown + "!");
						try {
							context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
							update("creditReportManage.updateCreditState",
									context.contextMap);
							context.contextMap.put("statee", 0);
							context.contextMap.put("AUDIT_STATE",
									Constants.AUDIT_STATE_0);// 区域主管审批的
							insert("creditReportManage.insertMemo",
									context.contextMap);

							// 插入日志
							String crDate = (String) context.contextMap
									.get("CR_DATE");
							String leRze = (String) context.contextMap
									.get("LE_RZE");
							if (crDate == null) {
								crDate = "空";
							}
							if (leRze == null) {
								leRze = "空";
							}
							BusinessLog
									.addBusinessLogWithIp(
											DataUtil.longUtil(context.contextMap
													.get("credit_id")),
											DataUtil.longUtil(context.contextMap
													.get("RECT_ID")),
											"业务主管报告审批",
											"业务主管审批",
											creditRunCode,
											"报告审批通过  " + "成立日期：" + crDate
													+ ",融资额：" + leRze,
											1,
											DataUtil.longUtil(context.contextMap
													.get("s_employeeId")),
											null, (String) context.contextMap
													.get("IP"));
							// 发送邮件
							MailSettingTo mailSettingTo = new MailSettingTo();
							mailSettingTo.setCreateBy(context.contextMap.get(
									"s_employeeId").toString());
							mailSettingTo
									.setEmailCc((String) context.contextMap
											.get("EMAIL"));
							StringBuffer content = new StringBuffer();
							content.append("系统提示:案件号为"
									+ creditRunCode
									+ "需您审批(客户名称:"
									+ corpName
									+ ",案件TR:"
									+ actualTR
									+ ",金额:"
									+ NumberUtils.getCurrencyFormat(leaseCost,
											Locale.CHINA)
									+ ")!<br><br>内资企业,概算成本小于等于"
									+ moneyInternalCorp + ",tr小于"
									+ trInternalCorpDown + "案子!");
							mailSettingTo.setEmailContent(content.toString());
							mailUtilService.sendMail(3, mailSettingTo);

						} catch (Exception e) {
							throw e;
						}
						// this.creditExamine(context);
						return null;
					}
				}

				try {
					// 判断概算成本/含税单价总和>70% 需要提交业务副总
					Map schemeMap = (Map) queryForObj(
							"creditReportManage.selectCreditScheme",
							context.contextMap);

					if (schemeMap != null) {
						List equipmentsList = (List) queryForList(
								"creditReportManage.selectCreditEquipment",
								context.contextMap);
						double price = 0;
						double LEASE_RZE = 0;
						double result = 0;
						for (int i = 0; equipmentsList != null
								&& i < equipmentsList.size(); i++) {
							if (((Map<String, Object>) equipmentsList.get(i))
									.get("DENOMINATOR") == null) {

							} else {
								price = price
										+ Double.valueOf(((Map<String, Object>) equipmentsList
												.get(i)).get("DENOMINATOR")
												.toString());
							}
						}
						if (schemeMap.get("LEASE_RZE") != null && price != 0) {
							LEASE_RZE = Double.valueOf(schemeMap.get(
									"LEASE_RZE").toString());
							BigDecimal a = new BigDecimal(
									Double.toString(LEASE_RZE));
							BigDecimal b = new BigDecimal(
									Double.toString(price));
							result = (a.divide(b, 2, BigDecimal.ROUND_HALF_UP)
									.doubleValue()) * 100;

							context.contextMap.put("data_type", "客户来源");
							Map contractMap = (Map) queryForObj(
									"creditReportManage.selectCreditBaseInfo",
									context.contextMap);

							int scale = 70;

							if (Integer.valueOf(contractMap
									.get("CONTRACT_TYPE").toString()) == 2
									&& "Y".equalsIgnoreCase((String) contractMap
											.get("IS_NEW_PRODUCTION"))) {
								scale = 80;
							}
							if (result > scale) {
								logger.debug("净承做成数>" + scale + "%!");// 如果合同类型是回租,并且是新机scale=80%,其他都是70%
								try {
									context.contextMap.put("STATE", 5);// 5意思是业务副总审批中
									update("creditReportManage.updateCreditState",
											context.contextMap);
									context.contextMap.put("statee", 0);
									context.contextMap.put("AUDIT_STATE",
											Constants.AUDIT_STATE_0);// 区域主管审批的
									insert("creditReportManage.insertMemo",
											context.contextMap);

									// 插入日志
									String crDate = (String) context.contextMap
											.get("CR_DATE");
									String leRze = (String) context.contextMap
											.get("LE_RZE");
									if (crDate == null) {
										crDate = "空";
									}
									if (leRze == null) {
										leRze = "空";
									}
									BusinessLog
											.addBusinessLogWithIp(
													DataUtil.longUtil(context.contextMap
															.get("credit_id")),
													DataUtil.longUtil(context.contextMap
															.get("RECT_ID")),
													"业务主管业务审批",
													"业务主管审批",
													creditRunCode,
													"报告审批通过  " + "成立日期："
															+ crDate + ",融资额："
															+ leRze,
													1,
													DataUtil.longUtil(context.contextMap
															.get("s_employeeId")),
													null,
													(String) context.contextMap
															.get("IP"));
									// 发送邮件
									MailSettingTo mailSettingTo = new MailSettingTo();
									mailSettingTo
											.setCreateBy(context.contextMap
													.get("s_employeeId")
													.toString());
									mailSettingTo
											.setEmailCc((String) context.contextMap
													.get("EMAIL"));
									StringBuffer content = new StringBuffer();
									content.append("系统提示:案件号为"
											+ creditRunCode
											+ "需您审批(客户名称:"
											+ corpName
											+ ",案件TR:"
											+ actualTR
											+ ",金额:"
											+ NumberUtils.getCurrencyFormat(
													leaseCost, Locale.CHINA)
											+ ")NF>" + scale + "%!");
									mailSettingTo.setEmailContent(content
											.toString());
									mailUtilService.sendMail(3, mailSettingTo);

								} catch (Exception e) {
									throw e;
								}
								// this.creditExamine(context);
								return null;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {// 从业务副总页面点击通过,程序代码继续执行下面的逻辑
				logger.debug("业务副总审批通过!");
			}
		}
		// 如果业务副总点击驳回,会调用原先的examineCredit2方法,并且不需要修改examineCredit2逻辑
		// ***************************************************************************************************************************

		if (creditState == 5) {// 如果等于5是业务副总提交过来
			context.contextMap.put("AUDIT_STATE", Constants.AUDIT_STATE_1);// 业务副总审批的
		} else {// 不等于5直接从业务主管提交
			context.contextMap.put("AUDIT_STATE", Constants.AUDIT_STATE_0);// 区域主管审批的
		}
		try {
			context.contextMap.put("statee", 1);
			update("creditReportManage.examineCredit", context.contextMap);
			insert("creditReportManage.insertMemo", context.contextMap);

			// 查询出该合同的客户经理
			Map creditById = (Map) queryForObj(
					"creditReportManage.selectSensor_IdById",
					context.contextMap);

			// 查询出在数据字典中的案件狀況細項下的提案的主键
			String prvLog = "提案与结果";
			String prvLogSun = "提案";
			context.contextMap.put("logFlag", prvLog);
			context.contextMap.put("logName", prvLogSun);
			Map logTypeMap = (Map) queryForObj(
					"activitiesLog.logActlog_idNotStatus", context.contextMap);

			// 有这个客户经理建立的主档
			context.contextMap.put("sensoridBycredit",
					creditById.get("SENSOR_ID"));
			context.contextMap.put("custidBycredit", creditById.get("CUST_ID"));

			// 如果该报告已经与主档有联系后就不更新下一个主档得credit
			Map logMaps = (Map) queryForObj("activitiesLog.logFirstByCreditId",
					context.contextMap);

			if (logMaps != null) {

				if (logMaps.size() > 0) {
					Map entityMap = new HashMap();
					entityMap.put("id", DataUtil.longUtil(context.contextMap
							.get("s_employeeId")));
					entityMap.put("logName", prvLogSun);
					entityMap.put("actilog", logMaps.get("ACTILOG_ID"));
					update("activitiesLog.updateCaseState", entityMap);

					entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
					entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
					insert("activitiesLog.createLogByOther", entityMap);
				}
			} else {

				Map logMap = (Map) queryForObj("activitiesLog.logFirst",
						context.contextMap);
				if (logMap != null) {
					if (logMap.size() > 0) {

						Map entityMap = new HashMap();
						entityMap.put("id", DataUtil
								.longUtil(context.contextMap
										.get("s_employeeId")));
						entityMap.put("logName", prvLogSun);
						entityMap.put("actilog", logMap.get("ACTILOG_ID"));
						update("activitiesLog.updateCaseState", entityMap);

						entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
						entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
						insert("activitiesLog.createLogByOther", entityMap);
					}

				}
			}

			// 跑共案
			String credit_id = context.contextMap.get("credit_id").toString();
			boolean isMerged = riskAuditService.projectsMerged(credit_id);
			// 访厂
			if (visitationService.checkCanApply(credit_id)) {
				// 没有访厂申请
				if (isMerged) {
					// 共案无需访厂
					//visitationService.addDontNeedVisit(credit_id, "共案无需访厂");
				} else {
					// 增加其他无需访厂
					String value = getValueBySpecialCode(
							LeaseUtil.getSpecialCodeByCreditId(credit_id),
							"VISITATION");
					if ("N".equals(value)) {
						// 专案免访厂
						visitationService.addDontNeedVisit(credit_id, "专案免访厂");
					}
				}
			}
			// 提交到审查
			riskAuditService.commitRisk(context.contextMap.get("credit_id")
					.toString());
		} catch (Exception e) {
			throw e;
		}

		Long creditId = DataUtil.longUtil(context.contextMap.get("credit_id"));
		Long contractId = DataUtil.longUtil(context.contextMap.get("RECT_ID"));
		String logType = "";
		String logTitle = "";
		if (creditState != 5) {
			logType = "业务主管报告审批";
			logTitle = "业务主管审批";
		} else {
			logType = "业务副总报告审批";
			logTitle = "业务副总审批";
		}

		String logCode = "";

		try {

			logCode = queryForObj("creditReportManage.selectCreditCode",
					context.getContextMap()) + "";

		} catch (Exception e) {

			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

		if (logCode == null) {
			logCode = "";
		}
		String crdate = "";
		String lerze = "";
		if (context.contextMap.get("CR_DATE") == null) {
			crdate = "空";
		} else {
			crdate = context.contextMap.get("CR_DATE").toString();
		}
		if (context.contextMap.get("LE_RZE") == null) {
			lerze = "空";
		} else {
			lerze = context.contextMap.get("LE_RZE").toString();
		}
		String memo = "报告审批通过  " + "成立日期：" + crdate + "融资额：" + lerze;
		int state = 1;
		Long userId = DataUtil.longUtil(context.contextMap.get("s_employeeId"));
		Long otherId = null;

		BusinessLog.addBusinessLogWithIp(creditId, contractId, logType,
				logTitle, logCode, memo, state, userId, otherId,
				(String) context.contextMap.get("IP"));
		// BusinessLog.addBusinessLog(creditId, contractId, logType, logTitle,
		// logCode, memo, state, userId, otherId);

		if (creditState == 5) {// 加入跳转条件,如果在业务副总页面审批,则跳转业务副总页面
			context.contextMap.put("content", "");
			context.contextMap.put("start_date", "");
			context.contextMap.put("end_date", "");
			return "next";
		} else {// 原先的逻辑
			return null;
		}
	}
	
	public String getValueBySpecialCode(String special_code, String property_code){
    	Map<String, Object> paraMap = new HashMap<String, Object>();
    	paraMap.put("special_code", special_code);
    	paraMap.put("property_code", property_code);
    	return (String) queryForObj("creditReportManage.getValueBySpecialCode", paraMap);
    }

	@Transactional
	public void doUpdateEstimatesPayDate(Context context) {
		int i = update("creditReportManage.updateEstimatesPayDate", context.contextMap);
		if (i > 0) {
			Map<String, Object> result = (Map<String, Object>) queryForObj("creditReportManage.getEstimatesPayDate", context.contextMap);
			context.contextMap.put("ESTIMATES_PAY_DATE_NUM", result.get("ESTIMATES_PAY_DATE_NUM"));
			insert("creditReportManage.insertEstimatesPayDateLog", context.contextMap);
		}
		context.contextMap.put("msg", i);
	}
	
}
