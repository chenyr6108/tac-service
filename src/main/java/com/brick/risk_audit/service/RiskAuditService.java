package com.brick.risk_audit.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.ProcessException;
import com.brick.base.service.BaseService;
import com.brick.base.to.BaseTo;
import com.brick.base.to.CheckedResult;
import com.brick.base.to.GuiHuInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.LeaseUtil.CREDIT_LINE_TYPE;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.credit.to.CreditTo;
import com.brick.customer.service.CustomerCredit;
import com.brick.customer.service.CustomerCredit.GUIHU_TYPE;
import com.brick.product.SupplierUtil;
import com.brick.risk_audit.dao.RiskAuditDAO;
import com.brick.risk_audit.to.RiskAuditTo;
import com.brick.risk_audit.to.RiskScoreCard;
import com.brick.risk_audit.to.ScoreCardTO;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.NumberUtils;
import com.brick.util.StringUtils;
import com.brick.util.nciic.NciicEntity;
import com.brick.util.nciic.NciicUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class RiskAuditService extends BaseService {
	private RiskAuditDAO riskAuditDAO;

	private MailUtilService mailUtilService;

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public RiskAuditDAO getRiskAuditDAO() {
		return riskAuditDAO;
	}

	public void setRiskAuditDAO(RiskAuditDAO riskAuditDAO) {
		this.riskAuditDAO = riskAuditDAO;
	}
	
	Log logger = LogFactory.getLog(RiskAuditService.class);
	
	/**
	 * 提交到审查
	 * @param credit_id
	 * @throws Exception
	 */
	public void commitRisk(String credit_id, SqlMapClient sqlMapper) throws Exception{
		RiskAuditTo risk = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			risk = this.getRiskLevel(credit_id);
			paramMap.put("risk_level", risk.getRiskLevel());
			paramMap.put("risk_level_memo", risk.getRiskLevelMsg());
			paramMap.put("credit_id", credit_id);
			sqlMapper.insert("riskAudit.doCommitRisk", paramMap);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 提交到审查
	 * @param credit_id
	 * @throws Exception
	 */
	public void commitRisk(String credit_id) throws Exception{
		RiskAuditTo risk = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			risk = this.getRiskLevel(credit_id);
			paramMap.put("risk_level", risk.getRiskLevel());
			paramMap.put("risk_level_memo", risk.getRiskLevelMsg());
			paramMap.put("credit_id", credit_id);
			this.insert("riskAudit.doCommitRisk", paramMap);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 共案
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean projectsMerged(String credit_id) throws Exception{
		boolean mergeFlag = false;
		List<Map<String, Object>> projectInfoList = null;
		Map<String, Object> projectInfo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CREDIT_ID", credit_id);
		//查询当前案件的一些用到的比较的字段
		projectInfoList = (List<Map<String, Object>>) this.queryForList("creditReportManage.getProInfoForMerge", paramMap);
		if (projectInfoList != null && projectInfoList.size() > 0) {
			projectInfo = projectInfoList.get(0);
		} else {
			throw new Exception("共案失败，未找到提交的报告。");
		}
		//检查共案表中是否有本案（是否提交过），如有，则在共案表中作废本案记录，避免判断共案是出错。
		int result_flag = update("creditReportManage.deleteBeforeMerge", paramMap);
		if (result_flag > 0) {
			logger.debug("该案子跑过共案的业务流程,并已作废历史。");
		} else {
			logger.debug("该案子第一次跑共案的业务流程。");
		}
		//去共案管理表找最近1个月的同一承租人 有无案件
		List<Map<String, Object>> resultByGroup = null;
		Map<String, Object> mergedProject = null;
		List<Map<String, Object>> resultByCust = (List<Map<String, Object>>) this.queryForList("creditReportManage.getResultByCust", projectInfo);
		if (resultByCust != null && resultByCust.size() > 0) {
			resultByGroup = (List<Map<String, Object>>) this.queryForList("creditReportManage.getResultByGroup", resultByCust.get(0));
		}
		if (resultByGroup != null && resultByGroup.size() > 0) {
			if ((Integer)resultByGroup.get(0).get("DAY_DIFF") > 60) {
				//不共案
				mergeFlag = false;
			} else {
				//可能共案，还须判断详细项
				mergedProject = (Map<String, Object>) this.queryForObj("creditReportManage.getProInfoForMerge", resultByGroup.get(0));
				if (!mergedProject.get("THING_NAME").toString().equals(projectInfo.get("THING_NAME").toString())
						&& !mergedProject.get("DECP_NAME_CN").toString().equals(projectInfo.get("DECP_NAME_CN").toString())
						&& !mergedProject.get("EQUPMENT_ADDRESS").toString().substring(0, 2).equals(projectInfo.get("EQUPMENT_ADDRESS").toString().substring(0, 2))) {
					//不共案
					mergeFlag = false;
				} else {
					//共案
					mergeFlag = true;
				}
			}
		} else {
			mergeFlag = false;
		}
		Map<String, Object> paramForMerge = new HashMap<String, Object>();
		paramForMerge.put("CREDIT_ID", credit_id);
		paramForMerge.put("CUST_ID", projectInfo.get("CUST_ID"));
		paramForMerge.put("COMMIT_DATE", projectInfo.get("COMMIT_WIND_DATE") == null ? new Date() : projectInfo.get("COMMIT_WIND_DATE"));
		if (mergeFlag) {
			//共案就新增一条相同的组号的记录
			paramForMerge.put("GROUP_CODE", resultByGroup.get(0).get("GROUP_CODE"));
			this.insert("creditReportManage.insertMergeProject", paramForMerge);
		} else {
			//不共案就新增一条不同组号的记录
			paramForMerge.put("GROUP_CODE", "G" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			this.insert("creditReportManage.insertMergeProject", paramForMerge);
		}
		return mergeFlag;
	}
	
	public int cancelProjectsMerged(String credit_id) throws Exception{
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CREDIT_ID", credit_id);
		return update("creditReportManage.deleteBeforeMerge", paramMap);
	}
	
	/**
	 * 2012/04/19 Yang Yun
	 * =====权限别=====
	 * @param prcd_id
	 * @return
	 * @throws Exception 
	 */
	public RiskAuditTo getRiskLevel(String credit_id) throws Exception{
		RiskAuditTo risk = null;
		//业种别
		int productionType = LeaseUtil.getProductionTypeByCreditId(credit_id);
		
		if (productionType == 1) {
			risk = getRiskByEqmt(credit_id);
		} else if (productionType == 2) {
			risk = new RiskAuditTo();
			risk.setRiskLevel(1);
			risk.setRiskLevelMsg("默认为1。");
		} else if (productionType == 3) {
			risk = getRiskByCar(credit_id);
			/*risk = new RiskAuditTo();
			risk.setRiskLevel(1);
			risk.setRiskLevelMsg("默认为1。");*/
		}
		return risk;
		
	}
	
	/**
	 * 设备类权限别
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	private RiskAuditTo getRiskByEqmt(String credit_id) throws Exception{
		List<RiskAuditTo> resultRiskLevel = new ArrayList<RiskAuditTo>();
		CreditTo creditTo = new CreditTo();
		creditTo.setCreditId(credit_id);
		//合同类型
		creditTo = (CreditTo) this.queryForObj("riskAudit.getContractTypeForRiskLevel", creditTo);
		
		//回租 且不是新机器的，判断是否标准客户
		resultRiskLevel.add(getRiskLevelForCust(creditTo));
		
		//验证客户成立时间，不满6个月的为2
		Integer result = (Integer) this.queryForObj("riskAudit.checkIncorporatingDate", creditTo);
		if (result == null || result < 6) {
			RiskAuditTo checkIncorporatingDate = new RiskAuditTo();
			checkIncorporatingDate.setRiskLevel(2);
			checkIncorporatingDate.setRiskLevelMsg("客户公司成立未满6个月(或“公司沿革”未保存)。");
			resultRiskLevel.add(checkIncorporatingDate);
		}
		
		//负面表列
		resultRiskLevel.add(getRiskLevelByAdverseImpact(creditTo));
		
		//取融资额
		Double LEASE_RZE = (Double) this.queryForObj("riskAudit.getLeaseRzeSum", creditTo);
		if (LEASE_RZE == null || LEASE_RZE == 0) {
			LEASE_RZE = LeaseUtil.getLeaseRzeByCreditId(credit_id);
		}
		creditTo.setLeaseRze(LEASE_RZE);
		//TR判断
		resultRiskLevel.add(getRiskLevelByTr(creditTo));
		
		//交机前拨款判断供应商级别和授信额度
		resultRiskLevel.add(getRiskLevelByBeforeDelivery(creditTo));
		
		//金额判断
		resultRiskLevel.add(getRiskLevelByMoney(creditTo));

		//......(可增加更多判断条件) 格式 resultRiskLevel.add(RiskAuditTo);
		
		//取最大
		RiskAuditTo maxRisk = null;
		for (RiskAuditTo riskAuditTo : resultRiskLevel) {
			System.out.println(riskAuditTo.getRiskLevel() + "," + riskAuditTo.getRiskLevelMsg());
			if (maxRisk == null) {
				maxRisk = riskAuditTo;
				continue;
			} else {
				if (maxRisk.getRiskLevel() < riskAuditTo.getRiskLevel()) {
					maxRisk = riskAuditTo;
				}
			}
		}
		return maxRisk;
	}
	
	private boolean isNewCar(String credit_id) throws Exception{
		String contractType = LeaseUtil.getContractTypeByCreditId(credit_id);
		boolean isNewCar = true;
		if ("12".equals(contractType) || "13".equals(contractType) || "14".equals(contractType)) {
			isNewCar = false;
		}
		return isNewCar;
	}
	
	/**
	 * 小车权限别
	 * @param credit_id
	 * @return
	 * @throws Exception 
	 */
	private RiskAuditTo getRiskByCar(String credit_id) throws Exception{
		RiskAuditTo risk = null;
		List<RiskAuditTo> resultRiskLevel = new ArrayList<RiskAuditTo>();
		
		
		//金额判断
		resultRiskLevel.add(getRiskForMoneyByCar(credit_id));
		//客户归户
		resultRiskLevel.add(getRiskForGuiHuMoneyByCar(credit_id));
		//成数
		resultRiskLevel.add(getRiskForPercentageByCar(credit_id));
		//TR
		resultRiskLevel.add(getRiskByTrForCar(credit_id));
		
		//取最大
		RiskAuditTo maxRisk = null;
		for (RiskAuditTo riskAuditTo : resultRiskLevel) {
			if (maxRisk == null) {
				maxRisk = riskAuditTo;
				continue;
			} else {
				if (maxRisk.getRiskLevel() < riskAuditTo.getRiskLevel()) {
					maxRisk = riskAuditTo;
				}
			}
		}
		risk = maxRisk;
		return risk;
	}
	
	/**
	 * 乘用车TR判断
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	private RiskAuditTo getRiskByTrForCar(String credit_id) throws Exception {
		RiskAuditTo risk = new RiskAuditTo();
		Integer group_inside = LeaseUtil.getGroupInsideByCreditId(credit_id);
		group_inside = group_inside == null ? 2 : group_inside;
		Double tr = LeaseUtil.getTRByCreditId(credit_id);
		if (group_inside == 1) {	//集团内
			if (isNewCar(credit_id)) {
				if (tr < 6.5) {
					risk.setRiskLevel(5);
				} else if (tr < 7) {
					risk.setRiskLevel(4);
				}
			} else {
				if (tr < 9.5) {
					risk.setRiskLevel(5);
				} else if (tr < 10) {
					risk.setRiskLevel(4);
				}
			}
		} else {	//集团外
			if (isNewCar(credit_id)) {
				if (tr < 8.5) {
					risk.setRiskLevel(5);
				} else if (tr < 9) {
					risk.setRiskLevel(4);
				}
			} else {
				if (tr < 11.5) {
					risk.setRiskLevel(5);
				} else if (tr < 12) {
					risk.setRiskLevel(4);
				}
			}
		}
		
		risk.setRiskLevelMsg("TR判断");
		return risk;
	}

	/**
	 * 乘用车金额判断
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	private RiskAuditTo getRiskForMoneyByCar(String credit_id) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		//本案金额
		Double leaseRze = LeaseUtil.getLeaseRzeByCreditId(credit_id);
		if (leaseRze <= 100000) {
			risk.setRiskLevel(1);
		} else if (leaseRze <= 250000) {
			risk.setRiskLevel(2);
		} else if (leaseRze <= 500000) {
			risk.setRiskLevel(3);
		} else if (leaseRze <= 1000000) {
			risk.setRiskLevel(4);
		} else {
			risk.setRiskLevel(5);
		}
		risk.setRiskLevelMsg("单案金额判断。");
		return risk;
	}
	
	/**
	 * 乘用车归户金额判断
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	private RiskAuditTo getRiskForGuiHuMoneyByCar(String credit_id) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		String cust_id = LeaseUtil.getCustIdByCreditId(credit_id);
		//归户金额
		GuiHuInfo guihu = LeaseUtil.getGuiHuByCustId(cust_id);
		if (guihu.getRemainingPrincipal() <= 200000) {
			risk.setRiskLevel(1);
		} else if (guihu.getRemainingPrincipal() <= 500000) {
			risk.setRiskLevel(2);
		} else if (guihu.getRemainingPrincipal() <= 1000000) {
			risk.setRiskLevel(3);
		} else if (guihu.getRemainingPrincipal() <= 1500000) {
			risk.setRiskLevel(4);
		} else {
			risk.setRiskLevel(5);
		}
		risk.setRiskLevelMsg("归户金额判断。");
		return risk;
	}
	
	/**
	 * 乘用车金额判断
	 * @param credit_id
	 * @return
	 * @throws Exception
	 */
	private RiskAuditTo getRiskForPercentageByCar(String credit_id) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		Double lease_rze = LeaseUtil.getLeaseRzeByCreditId(credit_id);
		lease_rze = lease_rze == null ? 0 : lease_rze;
		Double totalMoney = LeaseUtil.getTotalPriceByCreditId(credit_id);
		totalMoney = totalMoney == null ? 0 : totalMoney;
		Double percentage = totalMoney == 0 ? 0 : (lease_rze / totalMoney * 100);
		if (isNewCar(credit_id)) {
			//新车分期
			if (percentage <= 70) {
				risk.setRiskLevel(1);
			} else if (percentage <= 80) {
				risk.setRiskLevel(2);
			} else if (percentage <= 100) {
				risk.setRiskLevel(3);
			} else if (percentage <= 120) {
				risk.setRiskLevel(4);
			} else {
				risk.setRiskLevel(5);
			}
		} else {
			String contractType = LeaseUtil.getContractTypeByCreditId(credit_id);
			if (Constants.CONTRACT_TYPE_13.equals(contractType)) {
				//原车融资
				if (percentage <= 80) {
					risk.setRiskLevel(1);
				} else if (percentage <= 100) {
					risk.setRiskLevel(2);
				} else if (percentage <= 100) {
					risk.setRiskLevel(3);
				} else if (percentage <= 120) {
					risk.setRiskLevel(4);
				} else {
					risk.setRiskLevel(5);
				}
			} else {
				//二手车
				if (percentage <= 50) {
					risk.setRiskLevel(1);
				} else if (percentage <= 70) {
					risk.setRiskLevel(2);
				} else if (percentage <= 100) {
					risk.setRiskLevel(3);
				} else if (percentage <= 120) {
					risk.setRiskLevel(4);
				} else {
					risk.setRiskLevel(5);
				}
			}
		}
		risk.setRiskLevelMsg("成数判断。");
		return risk;
	}
	
	//回租 且不是新机器的，判断是否标准客户
	private RiskAuditTo getRiskLevelForCust(CreditTo creditTo) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		//回租 且不是新机器的，判断是否标准客户
		if ("2".equals(creditTo.getContractType().trim()) && !"Y".equals(creditTo.getIsNewProduction())) {
			List<Map<String, Object>> financeInfoList = (List<Map<String, Object>>) this.queryForList("riskAudit.getFinanceInfo", creditTo);
			if (financeInfoList != null && financeInfoList.size() == 1) {
				Map<String, Object> financeInfo = financeInfoList.get(0);
				risk = (getRiskLevelByFinanceInfo(financeInfo));
			} else {
				risk.setRiskLevel(3);
				risk.setRiskLevelMsg("客户财务报表数据抓取失败(未找到去年的财务报表)。");
			}
		}
		return risk;
	}
	
	/**
	 * TR判断
	 * @param creditTo
	 * @return
	 * @throws Exception 
	 */
	private RiskAuditTo getRiskLevelByTr(CreditTo creditTo) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		String msg = null;
		int level = 0;
		try {
			double LeaseRZE = creditTo.getLeaseRze();
			Map<String, Object> infoMap = (Map<String, Object>) this.queryForObj("riskAudit.getTrAndCustType", creditTo);
			double tr = infoMap.get("TR") == null ? 0 : Double.parseDouble(infoMap.get("TR").toString());
			if (tr == 0) {
				throw new Exception("TR=0");
			}
			double cust_type = infoMap.get("CUST_TYPE") == null ? 0 : Double.parseDouble(infoMap.get("CUST_TYPE").toString());
			boolean isForeign = false;
			if(cust_type == 4 || cust_type == 6){
				isForeign = true;
			} else {
				isForeign = false;
			}
			msg = "TR判断：授信额度=" + NumberUtils.getCurrencyFormat(LeaseRZE, Locale.CHINA)  + ",";
			if (LeaseRZE > 500000) {
				if (isForeign) {
					msg += "外资企业,";
					if (tr >= 13.9) {
						//level = 0;
					} else if (tr >= 12.4) {
						if("1".equals(String.valueOf(infoMap.get("VIP_FLAG")==null?0:infoMap.get("VIP_FLAG")))) {//如果是绿色通告过来 level=2 add by ShenQi 2012-12-28 因为业务副总卡关只针对普通案件
							level = 2;
						}
						//level = 0;
					} else if (tr >= 9.9) {
						level = 3;
					} else {
						level = 4;
					}
				} else {
					msg += "内资企业,";
					if (tr >= 15.4) {
						//level = 0;
					} else if (tr >= 13.9) {
						if("1".equals(String.valueOf(infoMap.get("VIP_FLAG")==null?0:infoMap.get("VIP_FLAG")))) {//如果是绿色通告过来 level=2 add by ShenQi 2012-12-28 因为业务副总卡关只针对普通案件
							level = 2;
						}
						//level = 0;
					} else if (tr >= 10.9) {
						level = 3;
					} else {
						level = 4;
					}
				}
			} else {
				if (isForeign) {
					msg += "外资企业,";
					if (tr >= 14.5) {
						//level = 0;
					} else if (tr >= 13.0) {
						if("1".equals(String.valueOf(infoMap.get("VIP_FLAG")==null?0:infoMap.get("VIP_FLAG")))) {//如果是绿色通告过来 level=2 add by ShenQi 2012-12-28 因为业务副总卡关只针对普通案件
							level = 2;
						}
						//level = 0;
					} else if (tr >= 10.5) {
						level = 3;
					} else {
						level = 4;
					}
				} else {
					msg += "内资企业,";
					if (tr >= 16) {
						//level = 0;
					} else if (tr >= 14.5) {
						if("1".equals(String.valueOf(infoMap.get("VIP_FLAG")==null?0:infoMap.get("VIP_FLAG")))) {//如果是绿色通告过来 level=2 add by ShenQi 2012-12-28 因为业务副总卡关只针对普通案件
							level = 2;
						}
						//level = 0;
					} else if (tr >= 11.5) {
						level = 3;
					} else {
						level = 4;
					}
				}
			}
			msg += "TR=" + NumberUtils.retain2rounded(tr) + "%";
		} catch (Exception e) {
			risk.setRiskLevel(3);
			risk.setRiskLevelMsg("TR异常！");
		}
		risk.setRiskLevel(level);
		risk.setRiskLevelMsg(msg);
		return risk;
	}
	
	/**
	 * 回租，判断是否标准客户
	 * @param financeInfo
	 * @return
	 */
	private RiskAuditTo getRiskLevelByFinanceInfo(Map<String, Object> financeInfo){
		RiskAuditTo risk = new RiskAuditTo();
		double ying_ye_shou_ru = Double.parseDouble((financeInfo.get("YING_YE_SHOU_RU") == null ? 0 : financeInfo.get("YING_YE_SHOU_RU")).toString());
		double max_jing_li_run = Double.parseDouble((financeInfo.get("MAX_JING_LI_RUN") == null ? 0 : financeInfo.get("MAX_JING_LI_RUN")).toString());
		double zong_zi_can = Double.parseDouble((financeInfo.get("ZONG_ZI_CAN") == null ? 0 : financeInfo.get("ZONG_ZI_CAN")).toString());
		double qi_shu = Double.parseDouble((financeInfo.get("QI_SHU") == null ? 0 : financeInfo.get("QI_SHU")).toString());
		double zong_fu_zhai = Double.parseDouble((financeInfo.get("ZONG_FU_ZHAI") == null ? 0 : financeInfo.get("ZONG_FU_ZHAI")).toString());
		double li_xi_zhi_chu = Double.parseDouble((financeInfo.get("LI_XI_ZHI_CHU") == null ? 0 : financeInfo.get("LI_XI_ZHI_CHU")).toString());
		double liu_dong_1 = Double.parseDouble((financeInfo.get("LIU_DONG_1") == null ? 0 : financeInfo.get("LIU_DONG_1")).toString());
		double liu_dong_2 = Double.parseDouble((financeInfo.get("LIU_DONG_2") == null ? 0 : financeInfo.get("LIU_DONG_2")).toString());
		double zi_li = Double.parseDouble((financeInfo.get("ZI_LI") == null ? 0 : financeInfo.get("ZI_LI")).toString());
		double cheng_li_shi_jian = Double.parseDouble((financeInfo.get("CHENG_LI_SHI_JIAN") == null ? 0 : financeInfo.get("CHENG_LI_SHI_JIAN")).toString());
		String shang_shi = (financeInfo.get("SHANG_SHI") == null ? 0 : financeInfo.get("SHANG_SHI")).toString();
		try {
			if (zi_li < 5) {				//本业经验
				throw new Exception("本业经验 < 5年");
			}
			if (cheng_li_shi_jian < 3) {	//成立时间
				throw new Exception("成立时间 < 3年");
			}
			if (ying_ye_shou_ru > 50000) {	//营业收入
				double fu_zhai_lv = zong_fu_zhai / ying_ye_shou_ru;
				if (fu_zhai_lv > 0.6) {		//总负债/营业收入
					throw new Exception("总负债/营业收入 > 0.6");
				}
				double li_xi_lv = li_xi_zhi_chu / ying_ye_shou_ru;
				if (li_xi_lv > 0.3) {		//利息支出/营业收入
					throw new Exception("利息支出/营业收入 > 0.3");
				}
			} else {
				throw new Exception("营业收入<50000");
			}
			if (liu_dong_2 == 0) {			//流动比率分母
				throw new Exception("流动比率 < 1");
			} else {
				double liu_dong_lv = liu_dong_1 / liu_dong_2;
				if (liu_dong_lv < 1) {		//流动比率
					throw new Exception("流动比率 < 1");
				}
			}
			if (max_jing_li_run <= 0) {		//近3年的净利润
				throw new Exception("近3年的净利润 <= 0");
			}
			if (qi_shu < 18) {				//承做期数
				throw new Exception("承做期数 < 18");
			}
			if (zong_zi_can <= 0) {			//净资产
				throw new Exception("净资产 <= 0");
			}
			risk.setRiskLevel(0);
		} catch (Exception e) {
			risk.setRiskLevel(3);
			risk.setRiskLevelMsg("非标准客户（" + e.getMessage() + "）");
		}
		return risk;
	}
	
	/**
	 * 负面表列
	 * @param creditTo
	 * @return
	 */
	private RiskAuditTo getRiskLevelByAdverseImpact(CreditTo creditTo){
		RiskAuditTo risk = new RiskAuditTo();
		String msg = null;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("creditId", creditTo.getCreditId());
			//逾期15天的次数
			paramMap.put("dunDay", 15);
			Integer countOf7Day = (Integer) this.queryForObj("riskAudit.getDunInfo", paramMap);
			
			//逾期31天的次数
			paramMap.put("dunDay", 31);
			Integer countOf15Day = (Integer) this.queryForObj("riskAudit.getDunInfo", paramMap);
			
			//近六个月的逾期次数
			paramMap.put("dunDay", 1);
			paramMap.put("selectFlag", "true");
			Integer countOf6Month = (Integer) this.queryForObj("riskAudit.getDunInfo", paramMap);
			if ((countOf7Day >= 3 || countOf15Day >= 1) && countOf6Month > 0) {
				msg = "负面，逾期15日以上3次 或 31日以上1次 （近六个月正常还款的除外）。";
				throw new Exception(msg);
			}
			//公司未满6个月，且没有供应商联保，回购，回购含灭失
			List<Map<String, Object>> corporationInfo = (List<Map<String, Object>>) this.queryForList("riskAudit.getCorporationInfo", creditTo);
			if (corporationInfo != null && corporationInfo.size() > 0) {
				msg = "负面，公司未满6个月，且没有其他公司或供应商连保、回购。";
				throw new Exception(msg);
			}
			//单一股东持股超过50%，且没有联保
			List<Map<String, Object>> holderInfo = (List<Map<String, Object>>) this.queryForList("riskAudit.getHolderInfo", creditTo);
			if (holderInfo != null && holderInfo.size() > 0) {
				List<Map<String, Object>> holderInfoAnother = (List<Map<String, Object>>) this.queryForList("riskAudit.getHolderInfoAnother", creditTo);
				//既是法人代表 又是担保人的除外
				if (holderInfoAnother == null || (holderInfoAnother != null && holderInfoAnother.size() == 0)) {
					msg = "负面，单一股东持股超过50%，且没有连保。";
					throw new Exception(msg);
				}
			}
			risk.setRiskLevel(0);
		} catch (Exception e) {
			risk.setRiskLevel(3);
			risk.setRiskLevelMsg(e.getMessage());
		}
		return risk;
	}
	
	/**
	 * 交机前拨款，判断供应商级别，以及供应商授信额度。
	 * @author Yang Yun
	 * @param creditTo
	 * @return RiskAuditTo
	 */
	private RiskAuditTo getRiskLevelByBeforeDelivery(CreditTo creditTo){
		RiskAuditTo risk = new RiskAuditTo();
		//String suppLevel = null;
		try {
			creditTo = SupplierUtil.getInfoByBeforeDelivery(creditTo.getCreditId());
			if (creditTo == null || creditTo.getCreditId() == null) {
				//非交机前拨款的案件
				risk.setRiskLevel(0);
				return risk;
			}
			CheckedResult result = checkSuplCreditLine(creditTo.getCreditId(), CREDIT_LINE_TYPE.PAY_BEFORE);
			if (!result.getResult()) {
				risk.setRiskLevel(3);
				risk.setRiskLevelMsg(result.getMsg());
			} else {
				risk.setRiskLevel(0);
				return risk;
			}
		} catch (Exception e) {
			risk.setRiskLevel(3);
			risk.setRiskLevelMsg(e.getMessage());
		}
		return risk;
	}
	
	/**
	 * @author 2012/04/19 Yang Yun
	 * 根据金额判断权限别（设备）
	 * @param money
	 * @return
	 * @throws Exception 
	 */
	private RiskAuditTo getRiskLevelByMoney(CreditTo creditTo) throws Exception{
		RiskAuditTo risk = new RiskAuditTo();
		int level = 0;
		int tempLevel = 0;
		double leaseRze = 0;
		double moneyMin = 0;
		double moneyMax = 0;
		double grantMin = 0;
		double grantMax = 0;
		List<Map<String, Object>> riskLevelInfo = null;
		try {
			riskLevelInfo = (List<Map<String, Object>>) this.queryForList("riskLevel.queryRiskLevAllInfo", new HashMap());
			String cust_id = LeaseUtil.getCustIdByCreditId(creditTo.getCreditId());
			GuiHuInfo guiHu = LeaseUtil.getGuiHuByCustId(cust_id, creditTo.getCreditId());
			System.out.println("客户归户" + guiHu.getRemainingPrincipal());
			leaseRze = creditTo.getLeaseRze();
			System.out.println(leaseRze);
			for (Map<String, Object> map : riskLevelInfo) {
				moneyMin = map.get("LEVEL_PRICE_LOW") == null ? 0 : Double.parseDouble(map.get("LEVEL_PRICE_LOW").toString());
				moneyMax = map.get("LEVEL_PRICE_UPPER") == null ? 0 : Double.parseDouble(map.get("LEVEL_PRICE_UPPER").toString());
				grantMin = map.get("GRANT_PRICE_LOW") == null ? 0 : Double.parseDouble(map.get("GRANT_PRICE_LOW").toString());
				grantMax = map.get("GRANT_PRICE_UPPER") == null ? 0 : Double.parseDouble(map.get("GRANT_PRICE_UPPER").toString());
				if (leaseRze >= moneyMin && leaseRze <= moneyMax) {
					tempLevel = map.get("RANK") == null ? 0 : Integer.parseInt(map.get("RANK").toString());
					level = tempLevel > level ? tempLevel : level;
				}
				if ((leaseRze + guiHu.getRemainingPrincipal()) >= grantMin && (leaseRze + guiHu.getRemainingPrincipal()) <= grantMax) {
					tempLevel = map.get("RANK") == null ? 0 : Integer.parseInt(map.get("RANK").toString());
					level = tempLevel > level ? tempLevel : level;
				} 
			}
		} catch (Exception e) {
			throw e;
		}
		risk.setRiskLevel(level);
		risk.setRiskLevelMsg("金额判断");
		return risk;
	}
	
	/**
	 * @author yangyun 2013/6/27
	 * 项目评审<br/>
	 * 评审操作，包含初级-4级评审
	 * @throws Exception 
	 * 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void doRiskAuth(Context context) throws Exception{
		//判断页面是否过期
		BaseTo baseTo = new BaseTo();
		baseTo.setTable_name("T_PRJT_CREDIT");
		baseTo.setPrimary_key("ID");
		baseTo.setKey_value((String) context.contextMap.get("credit_id"));
		baseTo.setModify_date(new java.sql.Date(DateUtil.parseDateWithMillisecond((String) context.contextMap.get("modify_date")).getTime()));
		if (!this.checkModifyDateIsEq(baseTo)) {
			throw new Exception("该记录已被操作。当前操作失效。");
		}
		
		//修改资信表的gr_state状态，并刷新modify_date
		update("riskAudit.updatecredit_gr", context.contextMap);
		
		try {
			//修改供应商担保
			context.contextMap.put("CREDIT_ID",context.contextMap.get("credit_id"));
			update("riskAudit.updateSupl",context.contextMap);
		} catch (Exception e) {
			logger.error("修改供应商担保出错", e);
			throw new Exception("修改供应商担保出错");
		}
		
		//担保人本票修改
		updateGuarantorNoteFlag(context);
		
		//支票还款信息
		updateCheckDetail(context);
		
		//评审操作
		doRiskService(context);
		
		//更新主档
		updateMasterFile(context);
	}
	
	/**
	 * @author yangyun 2013/6/27
	 * 担保人本票修改
	 * @param context
	 * @throws Exception
	 */
	private void updateGuarantorNoteFlag(Context context) throws Exception{
		try {
			// 担保人本票修改
			String istype="";
			String istypeId="";
			String creditCustTypes = context.request.getParameter("creditCustType");
			String[] creditCustType = creditCustTypes.split(",");
			String corpManBox = context.request.getParameter("corpManBox");
			String[] corpManBoxOne = corpManBox.split(";");
			for (int i = 0; i < corpManBoxOne.length; i++) {
				String corpManBoxOneSun = corpManBoxOne[i];
				String[] corpManIsType = corpManBoxOneSun.split(",");
				for (int j = 0; j < corpManIsType.length; j++) {
					if (corpManIsType.length > 1) {
						istypeId = corpManIsType[0];
						istype = corpManIsType[1];
						context.contextMap.put("istypeId", istypeId);
						context.contextMap.put("istype", istype);
						if (Integer.parseInt(creditCustType[i]) == 0) {
							update("beforeMakeContract.updateCorpByIsType", context.contextMap);
						} else if (Integer.parseInt(creditCustType[i]) == 1) {
							update("beforeMakeContract.updateNatuByIsType", context.contextMap);
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error("修改供应商担保出错", e);
			throw new Exception("修改供应商担保出错");
		}
	}
	
	/**
	 * @author yangyun 2013/6/27
	 * 支票还款信息
	 * @param context
	 * @throws Exception 
	 */
	private void updateCheckDetail(Context context) throws Exception{
		try {
			update("riskAudit.updatecreditCheckPay", context.contextMap);
			delete("riskAudit.deleteCreditSchemaCheck", context.contextMap);
			String[] CHECK_PAY_START = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_START", "0");
			String[] CHECK_PAY_END = HTMLUtil.getParameterValues(context.request, "CHECK_PAY_END", "0");
			for (int i=0; i<CHECK_PAY_START.length; i++) {
				Map paramMap = new HashMap();
				paramMap.put("CHECK_PAY_START", DataUtil.intUtil(CHECK_PAY_START[i]));
				paramMap.put("CHECK_PAY_END", DataUtil.intUtil(CHECK_PAY_END[i]));
				paramMap.put("credit_id", context.contextMap.get("credit_id"));
				paramMap.put("s_employeeId", context.contextMap.get("s_employeeId"));
				//TYPE 1:表示是自行输入 ；0：表示是审查录入
				paramMap.put("TYPE", Constants.TYPE_EXAMINANT_INPUT);
				insert("riskAudit.addCreditSchemaCheck", paramMap);				
			}
		} catch (Exception e) {
			logger.error("更新支票还款信息出错", e);
			throw new Exception("更新支票还款信息出错");
		}
	}
	
	/**
	 * @author yangyun 2013/6/27
	 * 各级评审操作，
	 * @param context
	 * @throws Exception 
	 */
	private void doRiskService(Context context) throws Exception{
		try{
			String prc_hao=CodeRule.generateWindCode(context.contextMap.get("credit_id"));
			String credit_id = (String)(context.contextMap.get("credit_id"));
			String user_id = String.valueOf(context.contextMap.get("s_employeeId"));
			String prcNode = (String) context.contextMap.get("prc_node");
			String riskLevelStr = (String) context.contextMap.get("riskLevel");
			String riskLevelFrom = null;
			String riskLevelTo = null;
			System.out.println(prcNode);
			if (StringUtils.isEmpty(prcNode)) {
				prcNode = "0";
			} else {
				prcNode = String.valueOf(Integer.parseInt(prcNode) + 1);
			}
			riskLevelFrom = prcNode;
			riskLevelTo = String.valueOf(Integer.parseInt(prcNode) + 1);
			String prc_id = context.contextMap.get("prc_id").toString();
			String risk_flag = (String) context.contextMap.get("memo");
			context.contextMap.put("prc_hao", prc_hao);
			RiskAuditTo risk = null;
			risk = new RiskAuditTo();
			risk.setPrcId(prc_id);
			risk.setPrcCode(prc_hao);
			risk.setPrcmContext((String) context.contextMap.get("context"));
			risk.setCreate_by(context.contextMap.get("s_employeeId").toString());
			risk.setPrcNode(prcNode);
			risk.setPrcmLevel(prcNode);
			//记录评审意见
			insert("riskAudit.insertRiskMemo", risk);
			if (Constants.RISK_FLAG_COMMIT.equals(risk_flag)) {
				risk.setState(0);
				update("riskAudit.updateRiskControl", risk);
				addBusinessLog(credit_id
						,"0".equals(riskLevelFrom) ? "初级评审" : riskLevelFrom + "级评审"
						,"项目评审"
						,"提交到" + riskLevelTo+"级:"+context.contextMap.get("context")
						,user_id
						,(String)context.contextMap.get("IP"));
				if (Constants.RISK_LEVEL_GM.equals(riskLevelTo)) {
					sendMailToGM(credit_id);
				}
			} else {
				risk.setState(Integer.parseInt(risk_flag));
				risk.setFinishTime(new Timestamp(new Date().getTime()));
				risk.setReturnClassLevelOne((String) context.contextMap.get("returnClassLevelOne"));
				risk.setReturnClassLevelTwo((String) context.contextMap.get("returnClassLevelTwo"));
				update("riskAudit.updateRiskControl", risk);
				update("riskAudit.updatecredit", context.contextMap);
				if (Constants.RISK_FLAG_PASS.equals(risk_flag)) {
					String leaseCode = (String) queryForObj("riskAudit.selectCreditLeaseCode", context.contextMap);
					if(StringUtils.isEmpty(leaseCode)){
						String le_code = CodeRule.generateRentContractCode(context.contextMap.get("credit_id"));
						context.contextMap.put("le_code", le_code);
					} else {
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("leaseCode", leaseCode);
						String leaseHead = leaseCode.substring(0, 2);
						String leaseType = leaseCode.substring(2, 4);
						String leaseYear = leaseCode.substring(4, 8);
						String leaseMonth = leaseCode.substring(8, 10);
						String leaseSequ = leaseCode.substring(10, 14);
						Integer leaseCodeUpdateFlag = (Integer) queryForObj("riskAudit.getLeaseCodeFlag", paramMap);
						paramMap.put("credit_id", credit_id);
						String thisLeaseType = (String) queryForObj("riskAudit.getLeaseTypeById", paramMap);
						if (leaseCodeUpdateFlag == null || leaseCodeUpdateFlag == 1) {
							//保留合同号,需要判断租赁方式是否相同
							if (!leaseType.equals(thisLeaseType)) {
								throw new ProcessException("保留合同号，但是案件类型已被修改，评审失败");
							}
						} else {
							//不保留合同号
							/*
							 * 不保留合同号，为了不造成合同号跳号，所以流水号要保留
							 * 但是合同号编码规则中，有6位是年月码，
							 * 当前年不是旧合同号的年份时，保留旧合同号的年份，把月份固定在12月。
							 */
							if (!DateUtil.getCurrentYear().equals(leaseYear)) {
								leaseMonth = "12";
							} else {
								leaseMonth = DateUtil.getCurrentMonth();
							}
							leaseCode = leaseHead + thisLeaseType + leaseYear + leaseMonth + leaseSequ;
						}
						context.contextMap.put("le_code", leaseCode);
					}
					update("riskAudit.updatecreditLeaseCode", context.contextMap);
					//结案修改风控表的真正流水号
					String real_code=CodeRule.geneRiskCode(credit_id);
					context.contextMap.put("real_code", real_code);					
					update("riskAudit.updatecreditLeaseCode", context.contextMap);
					
					//无条件通过时 如果当前级别大于 现有的权限别，则要更新权限别
					int thisNode = Integer.parseInt(prcNode);
					int riskLevel = riskLevelStr == null ? 0 : Integer.parseInt(riskLevelStr);
					if (thisNode > riskLevel) {
						context.contextMap.put("newRiskLevel", thisNode);
					}
					update("riskAudit.updateWindRealCode", context.contextMap);
					doUpdateCompanyCodeAndVirtualCode(credit_id);
				} else if (Constants.RISK_FLAG_RETURN.equals(risk_flag)) {
					update("riskAudit.updatecreditstate", context.contextMap);
				} else if (Constants.RISK_FLAG_REJECT.equals(risk_flag)) {
					String real_code=CodeRule.geneRiskCode(credit_id);
					context.contextMap.put("real_code", real_code);
					update("riskAudit.updateWindRealCode", context.contextMap);
				}
				addBusinessLog(credit_id
						,"0".equals(riskLevelFrom) ? "初级评审" : riskLevelFrom + "级评审"
						,"项目评审"
						,getRiskFlagDesc(risk_flag) + context.contextMap.get("context")
						,user_id
						,(String)context.contextMap.get("IP"));
				sendEmailWhenPass(context);
			}
			
		} catch(ProcessException p){
			logger.error("保留合同号，但是案件类型已被修改，评审失败。", p);
			throw new Exception(p);
		}catch(Exception e){
			logger.error("评审操作错误", e);
			throw new Exception("评审操作错误");
		}
	}
	
	private void doUpdateCompanyCodeAndVirtualCode(String credit_id) throws Exception {
		boolean hasApprovalPro = false;
		boolean hasTacPro = false;
		String cust_id = LeaseUtil.getCustIdByCreditId(credit_id);
		List<String> allCreditId = LeaseUtil.getCreditIdByCustId(cust_id);
		if (allCreditId != null) {
			for (String c : allCreditId) {
				if (!credit_id.equals(c)) {
					hasApprovalPro = true;
					hasTacPro = hasTacPro ? hasTacPro : (1 == LeaseUtil.getCompanyCodeByCreditId(c));
				}
			}
		}
		if (!hasTacPro) {
			//没有裕融的案件，要判断是否裕国
			updateCompanyCodeByCreditId(credit_id);
		}
		
		if (!hasApprovalPro || StringUtils.isEmpty(LeaseUtil.getCustVirtualCodeById(cust_id))) {
			//没有核准过的案件,或者虚拟账号空的，生成虚拟账号
			String custCode = LeaseUtil.getCustCodeById(cust_id);
			int realCompanyCode = LeaseUtil.getCompanyCodeByCreditId(credit_id);
			String virtual_code = null;
			if (realCompanyCode == 1) {
				virtual_code = "88" + custCode;
			} else if (realCompanyCode == 2) {
				virtual_code = "89" + custCode;
			} else {
				logger.warn("公司别错误，无法生成虚拟账号。");
			}
			if (!StringUtils.isEmpty(virtual_code)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("virtual_code", virtual_code);
				paramMap.put("cust_id", cust_id);
				update("customer.updateVirtualCode", paramMap);
			}
		}
		
	}
	
	private void updateCompanyCodeByCreditId(String credit_id) throws Exception{
		Date today = DateUtil.strToDay(DateUtil.getCurrentDate());
		//2014-6-1 之后佛山直接租赁->裕国, 佛山ID=8
		Date d = DateUtil.strToDay("2014-6-1");
		String dc = LeaseUtil.getDecpIdByCreditId(credit_id);
		String ct = LeaseUtil.getContractTypeByCreditId(credit_id);
		if ("8".equals(dc) && !today.before(d) && "7".equals(ct)) {
			//更新裕国
			updateCompanyCode(credit_id, Constants.COMPANY_CODE_YUGUO);
		}
		//2014-6-16 之后厦门直接租赁->裕国， 厦门ID=11
		d = DateUtil.strToDay("2014-6-16");
		dc = LeaseUtil.getDecpIdByCreditId(credit_id);
		if ("11".equals(dc) && !today.before(d) && "7".equals(ct)) {
			//更新裕国
			updateCompanyCode(credit_id, Constants.COMPANY_CODE_YUGUO);
		}
		
		//2014-7-16之后东莞直接租赁->裕国， 东莞ID=3
		d = DateUtil.strToDay("2014-7-16");
		dc = LeaseUtil.getDecpIdByCreditId(credit_id);
		if ("3".equals(dc) && !today.before(d) && "7".equals(ct)) {
			//更新裕国
			updateCompanyCode(credit_id, Constants.COMPANY_CODE_YUGUO);
		}
		
		//2014-6-9之后宁波直接租赁->裕国，宁波ID=26
		d = DateUtil.strToDay("2014-6-9");
		dc = LeaseUtil.getDecpIdByCreditId(credit_id);
		if ("26".equals(dc) && !today.before(d) && "7".equals(ct)) {
			//更新裕国
			updateCompanyCode(credit_id, Constants.COMPANY_CODE_YUGUO);
		}
	}
	
	private String getRiskFlagDesc(String risk_flag){
		String riskFlagStr = null;
		if (Constants.RISK_FLAG_PASS.equals(risk_flag)) {
			riskFlagStr = "无条件通过:";
		} else if (Constants.RISK_FLAG_RETURN.equals(risk_flag)) {
			riskFlagStr = "不通过附条件:";
		} else if (Constants.RISK_FLAG_REJECT.equals(risk_flag)) {
			riskFlagStr = "不通过:";
		}
		return riskFlagStr;
	}
	
	/**
	 * 提交到总经理时发送Email
	 * @param credit_id
	 * @throws Exception
	 */
	private void sendMailToGM(String credit_id) throws Exception{
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("creidt_id", credit_id);
		Map<String, Object> projectInfo = (Map<String, Object>) queryForObj("riskAudit.getProjectInfo", pMap);
		String mailContent = "<head> " +
				"<style type='text/css' media='screen'> " +
				"body{font-family: '微软雅黑';}" +
				"	#mail_table { " +
				"	border-collapse:collapse;  " +
				"	border:solid #999; " +
				"	border-width:1px 0 0 1px;  " +
				"	} " +
				"	#mail_table th {border:solid #999;border-width:0 1px 1px 0;padding:2px;text-align:right;white-space: nowrap; vertical-align: top;} " +
				"	#mail_table td {border:solid #999;border-width:0 1px 1px 0;padding:2px;text-align:left;} " +
				"</style> " +
				"</head> " +
				"<body> " +
				"您有一个案件需要审批，请进入租赁系统，进行审批:<br/>" +
				"<table id=\"mail_table\"> " +
				"	<tr> " +
				"		<th>案件号：</th> " +
				"		<td>" + projectInfo.get("CREDIT_RUNCODE") + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>客户名称：</th> " +
				"		<td>" + projectInfo.get("CUST_NAME") + "</td> " +
				"	</tr> " +
				"	<tr> " +
				"		<th>融资额：</th> " +
				"		<td>" + NumberUtils.getCurrencyFormat(projectInfo.get("LEASE_RZE"), Locale.CHINA) + "</td> " +
				"	</tr> " +
				"</table> " +
				"</body>";
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailTo(Constants.GM_MAIL);
		//mailSettingTo.setEmailCc("yangyun@tacleasing.cn;kyle@tacleasing.cn");
		mailSettingTo.setEmailSubject("待评审");
		mailSettingTo.setEmailContent(mailContent);
		try {
			getMailUtilService().sendMail(mailSettingTo);
		} catch (Exception e) {
			logger.error("评审发送邮件失败！" + e.getMessage());
		}
	}
	
	/**
	 * 更新主档
	 * @param context
	 * @throws Exception 
	 */
	public void updateMasterFile(Context context) throws Exception{
		String risk_flag = (String) context.contextMap.get("memo");
		try {
			if (Constants.RISK_FLAG_PASS.equals(risk_flag)) {
				// 查询出该合同的客户经理
				Map<String, Object> creditById = (Map<String, Object>) queryForObj("creditReportManage.selectSensor_IdById", context.contextMap);

				// 查询出在数据字典中的案件狀況細項下的提案的主键
				String prvLog = "核准状态";
				String prvLogSun = "已核准";
				context.contextMap.put("logFlag", prvLog);
				context.contextMap.put("logName", prvLogSun);
				Map<String, Object> logTypeMap = (Map<String, Object>) queryForObj("activitiesLog.logActlog_idNotStatus", context.contextMap);

				// 有这个客户经理建立的主档
				context.contextMap.put("sensoridBycredit", creditById.get("SENSOR_ID"));
				context.contextMap.put("custidBycredit", creditById.get("CUST_ID"));
				Map<String, Object> logMaps = (Map<String, Object>) queryForObj("activitiesLog.logFirstByCreditId", context.contextMap);
				if (logMaps != null) {
					if (logMaps.size() > 0) {
						Map<String, Object> entityMap = new HashMap<String, Object>();
						entityMap.put("id",	context.request.getAttribute("s_employeeId"));
						entityMap.put("casesun", prvLogSun);
						entityMap.put("actilog", logMaps.get("ACTILOG_ID"));
						entityMap.put("credit_id", context.contextMap.get("credit_id"));
						update("activitiesLog.updateCaseStateBycredit_id", entityMap);
						entityMap.put("caseFather", logTypeMap.get("DATA_ID"));
						entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
						insert("activitiesLog.createLogByOther", entityMap);
					}
				} else {
					Map<String, Object> logMap = (Map<String, Object>) queryForObj("activitiesLog.logFirst", context.contextMap);
					if (logMap != null) {
						if (logMap.size() > 0) {
							Map<String, Object> entityMap = new HashMap<String, Object>();
							entityMap.put("id", context.request.getAttribute("s_employeeId"));
							entityMap.put("casesun", prvLogSun);
							entityMap.put("actilog", logMap.get("ACTILOG_ID"));
							entityMap.put("credit_id", context.contextMap.get("credit_id"));
							update("activitiesLog.updateCaseStateBycredit_id", entityMap);
							entityMap.put("caseFather",	logTypeMap.get("DATA_ID"));
							entityMap.put("casesunId", logTypeMap.get("ACTLOG_ID"));
							insert("activitiesLog.createLogByOther", entityMap);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("主档更新失败", e);
			throw new Exception("主档更新失败");
		}
		
	}
	
	/**
	 * 结案时发送短信和Email
	 * @param context
	 * @throws Exception 
	 */
	private void sendEmailWhenPass(Context context) throws Exception{
		try {
			List<Map<String, Object>> send = new ArrayList<Map<String, Object>>();
			/* 2012/01/13 Yang Yun 给案件的客户经理和主管发短信。. */
			List<Map> sendManager=new ArrayList<Map>();
			Map<String, Object> sendData = null;
			Map map=new HashMap();
			List<Map<String, Object>> mailList = new ArrayList<Map<String,Object>>();
			Map<String, Object> mailMap = null;
			map.put("credit_id", context.contextMap.get("credit_id"));
			//Add by Michael 2012 12-19 绿色通道案件发送给审查服务课人员
			if("1".equals(String.valueOf(context.contextMap.get("VIP_FLAG")))){
				sendManager =(List<Map>) DataAccessor.query("creditReportManage.getMobleByCreditIdForVIP", map, DataAccessor.RS_TYPE.LIST);
			}else{
				sendManager =(List<Map>) DataAccessor.query("creditReportManage.getMobleByCreditId", map, DataAccessor.RS_TYPE.LIST);
			}
			String risk_flag = (String) context.contextMap.get("memo");
			String msg = null;
			Integer decp_id = null;
			String riskFlagStr = getRiskFlagDesc(risk_flag);
			for (Map m : sendManager) {
				msg = (StringUtils.isEmpty((String) m.get("LEASE_CODE")) ? "" : m.get("LEASE_CODE")) + " " + 
					  (StringUtils.isEmpty((String) m.get("CUST_NAME")) ? "" : m.get("CUST_NAME"))
						+ "-本件已" + riskFlagStr + "--审查处。";
				//判断手机是否为空
				if (!StringUtils.isEmpty((String) m.get("MOBILE"))) {
					sendData = new HashMap<String, Object>();
					sendData.put("SENDTYPE", 1);
					sendData.put("MESSAGE", msg);
					sendData.put("STATE", 1);
					sendData.put("LEASE_CODE", m.get("LEASE_CODE"));
					sendData.put("MTEL", m.get("MOBILE"));
					
					//add by Shen Qi 准备插入SEND_TEST表数据
					sendData.put("CONTRACT_NUMBER",m.get("LEASE_CODE")==null?"":m.get("LEASE_CODE"));
					sendData.put("CUST_NAME",m.get("CUST_NAME")==null?"":m.get("CUST_NAME"));
					sendData.put("CREATE_BY",context.contextMap.get("s_employeeName")==null?"":context.contextMap.get("s_employeeName"));
					sendData.put("SEND_MODE","0");//0 means 手动, 1 means 自动
					sendData.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
					sendData.put("LOG","inserttowind");
					
					send.add(sendData);
				} else {
					logger.warn("客户经理手机为空！");
				}
				//判断主管手机是否为空
				if (!StringUtils.isEmpty((String) m.get("UPPER_MOBILE"))) {
					sendData = new HashMap<String, Object>();
					sendData.put("SENDTYPE", 1);
					sendData.put("MESSAGE", msg);
					sendData.put("STATE", 1);
					sendData.put("LEASE_CODE", m.get("LEASE_CODE"));
					sendData.put("MTEL", m.get("UPPER_MOBILE"));
					
					//add by Shen Qi 准备插入SEND_TEST表数据
					sendData.put("CONTRACT_NUMBER",m.get("LEASE_CODE")==null?"":m.get("LEASE_CODE"));
					sendData.put("CUST_NAME",m.get("CUST_NAME")==null?"":m.get("CUST_NAME"));
					sendData.put("CREATE_BY",context.contextMap.get("s_employeeName")==null?"":context.contextMap.get("s_employeeName"));
					sendData.put("SEND_MODE","0");//0 means 手动, 1 means 自动
					sendData.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
					sendData.put("LOG","inserttowind");
					
					send.add(sendData);
				} else {
					logger.warn("客户经理手机为空！");
				}
				
				/* 2012/05/11 yang yun */
				//科主管发短信
				StringBuffer mailAdd = new StringBuffer();
				String classLeader = LeaseUtil.getClassLeaderByCreditId(String.valueOf(context.contextMap.get("credit_id")));
				if (!StringUtils.isEmpty(classLeader)) {
				//苏州设备的发给潘勇涛 13962597143
					sendData = new HashMap();
					sendData.put("SENDTYPE", 1);
					sendData.put("MESSAGE", msg);
					sendData.put("STATE", 1);
					sendData.put("LEASE_CODE", m.get("LEASE_CODE"));
					sendData.put("MTEL", LeaseUtil.getMobileByUserId(classLeader));
					sendData.put("CONTRACT_NUMBER",m.get("LEASE_CODE")==null?"":m.get("LEASE_CODE"));
					sendData.put("CUST_NAME",m.get("CUST_NAME")==null?"":m.get("CUST_NAME"));
					sendData.put("CREATE_BY",context.contextMap.get("s_employeeName")==null?"":context.contextMap.get("s_employeeName"));
					sendData.put("SEND_MODE","0");//0 means 手动, 1 means 自动
					sendData.put("SEND_TYPE","1");//0 means 邮件, 1 means 发送短信
					sendData.put("LOG","inserttowind");
					send.add(sendData);
					mailAdd.append(";" + LeaseUtil.getEmailByUserId(classLeader));
				}
				MailSettingTo mailSettingTo = new MailSettingTo();
				mailSettingTo.setEmailTo(StringUtils.isEmpty(String.valueOf(m.get("EMAIL"))) ? "" : String.valueOf(m.get("EMAIL")));
				mailSettingTo.setEmailSubject("审查评审");
				mailSettingTo.setEmailCc((StringUtils.isEmpty(String.valueOf(m.get("UPPER_EMAIL"))) ? "" : String.valueOf(m.get("UPPER_EMAIL"))) + mailAdd.toString());
				mailSettingTo.setEmailContent(msg);
				try {
					getMailUtilService().sendMail(mailSettingTo);
				} catch (Exception e) {
					logger.error("评审发送邮件失败！" + e.getMessage());
				}
			}
			SendSMSMsg(context, send);
		} catch (Exception e) {
			logger.error("结案时发送短信和Email错误", e);
			throw new Exception("结案时发送短信和Email错误");
		}
		
	}

	@Transactional(rollbackFor = Exception.class)
	public RiskAuditTo getRiskLevelForTemp(String credit_id) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		RiskAuditTo risk = null;
		paramMap.put("credit_id", credit_id);
		Integer prjtMerge = (Integer) this.queryForObj("riskAudit.getPrjtMerge", paramMap);
		if (prjtMerge != null && prjtMerge > 0) {
			risk = this.getRiskLevel(credit_id);
		} else {
			this.projectsMerged(credit_id);
			risk = this.getRiskLevel(credit_id);
			this.cancelProjectsMerged(credit_id);
		}
		return risk;
	}
	
	public Map<String, Object> getAllScoreCard(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ScoreCardTO scoreCardParam = new ScoreCardTO();
		scoreCardParam.setSubject_level(1);
		List<ScoreCardTO> scoreCardList1 = (List<ScoreCardTO>) queryForList("riskAudit.getAllSubjectAndOptionByLevel", scoreCardParam);
		scoreCardParam.setSubject_level(2);
		List<ScoreCardTO> scoreCardList2 = (List<ScoreCardTO>) queryForList("riskAudit.getAllSubjectAndOptionByLevel", scoreCardParam);
		resultMap.put("scoreCardList1", scoreCardList1);
		resultMap.put("scoreCardList2", scoreCardList2);
		return resultMap;
	}
	
	public Map<String, Object> getScoreCard(String scoreCard){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(scoreCard)) {
			resultMap.put("msg", "无评分表！");
		}else {
			ScoreCardTO scoreCardParam = new ScoreCardTO();
			scoreCardParam.setScoreCard(scoreCard);
			scoreCardParam.setSubject_level(1);
			List<ScoreCardTO> scoreCardList1 = (List<ScoreCardTO>) queryForList("riskAudit.getSubjectAndOptionForCurrent", scoreCardParam);
			scoreCardParam.setSubject_level(2);
			List<ScoreCardTO> scoreCardList2 = (List<ScoreCardTO>) queryForList("riskAudit.getSubjectAndOptionForCurrent", scoreCardParam);
			resultMap.put("scoreCardCode", scoreCard);
			resultMap.put("scoreCardList1", scoreCardList1);
			resultMap.put("scoreCardList2", scoreCardList2);
		}
		return resultMap;
	}
	
	public Map<String, Object> getAllScoreCardByPrcId(String prc_id){
		String scoreCard = getAllScoreCardCodeByRisk(prc_id);
		return getScoreCard(scoreCard);
	}
	
	public String getScoreCardCodeByRisk(String prc_id){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prc_id", prc_id);
		return (String) queryForObj("riskAudit.getScoreCardByRisk", paramMap);
	}
	
	public RiskScoreCard getRiskScoreCardById(String prc_id){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prc_id", prc_id);
		RiskScoreCard rsc = (RiskScoreCard) queryForObj("riskAudit.getScoreByRisk", paramMap);
		return rsc;
	}
	
	public String getAllScoreCardCodeByRisk(String prc_id){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("prc_id", prc_id);
		return (String) queryForObj("riskAudit.getAllScoreCardByRisk", paramMap);
	}
	
	public Map<String, Object> getScoreCardByContractType(String contractType) throws SQLException{
		String scoreCard = LeaseUtil.getScoreCardByContractType(contractType);
		return getScoreCard(scoreCard);
	}

	@Transactional
	public void saveScoreCardSetting(String contractType, String scoreCard, String create_by) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("contractType", contractType);
		paraMap.put("scoreCard", scoreCard);
		paraMap.put("create_by", create_by);
		update("riskAudit.deleteScoreCardSetting", paraMap);
		insert("riskAudit.saveScoreCardSetting", paraMap);
		
	}

	@Transactional(rollbackFor = Exception.class)
	public void doUpdateCreditReport(Context context) throws Exception {
		List<Map<String, Object>> guarantors = new ArrayList<Map<String,Object>>();
		Map<String, Object> guarantor = null;
		Map<String, Object> other = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//修改担保人证件号
		int g_count = Integer.parseInt((String) context.contextMap.get("g_count"));
		String guarantor_code = null;
		String guarantor_state = null;
		String guarantor_id = null;
		for (int i = 1; i <= g_count; i++) {
			guarantor_code = (String) context.contextMap.get("guarantor_code_" + i);
			guarantor_state = (String) context.contextMap.get("guarantor_state_" + i);
			guarantor_id = (String) context.contextMap.get("guarantor_id_" + i);
			if ("0".equals(guarantor_state)) {
				
				paramMap.put("guarantor_id", guarantor_id);
				paramMap.put("guarantor_code", guarantor_code);
				guarantor = (Map<String, Object>) queryForObj("riskAudit.getGuarantorForCorp", paramMap);
				guarantor.put("newValue", guarantor_code);
				update("riskAudit.updateGuarantorForCorp", paramMap);
			} else if("1".equals(guarantor_state)) {
				paramMap.put("guarantor_id", guarantor_id);
				paramMap.put("guarantor_code", guarantor_code);
				guarantor = (Map<String, Object>) queryForObj("riskAudit.getGuarantorForNatu", paramMap);
				guarantor.put("newValue", guarantor_code);
				update("riskAudit.updateGuarantorForNatu", paramMap);
			}
			guarantors.add(guarantor);
		}
		//修改租赁物放置地
		String credit_id = (String) context.contextMap.get("credit_id");
		String equpment_address = (String) context.contextMap.get("equpment_address");
		paramMap.put("credit_id", credit_id);
		paramMap.put("equpment_address", equpment_address);
		other.put("oldAddr", LeaseUtil.getEqupmentAddressByCreditId(credit_id));
		other.put("newAddr", equpment_address);
		update("riskAudit.updateEqmAddr", paramMap);
		
		//修改业务端意见
		/*String project_content = (String) context.contextMap.get("project_content");
		String project_content_id = (String) context.contextMap.get("project_content_id");
		paramMap.put("memo", project_content);
		paramMap.put("memo_id", project_content_id);
		other.put("old_memo", queryForObj("riskAudit.getProjectMemo", paramMap));
		other.put("new_memo", project_content);
		update("riskAudit.updateProjectMemo", paramMap);
		
		String project_content_other = (String) context.contextMap.get("project_content_other");
		String project_content_other_id = (String) context.contextMap.get("project_content_other_id");
		paramMap.put("memo", project_content_other);
		paramMap.put("memo_id", project_content_other_id);
		other.put("old_memo_other", queryForObj("riskAudit.getProjectMemo", paramMap));
		other.put("new_memo_other", project_content_other);
		update("riskAudit.updateProjectMemo", paramMap);
		
		String memo_manage = (String) context.contextMap.get("memo_manage");
		String memo_manage_id = (String) context.contextMap.get("memo_manage_id");
		paramMap.put("memo", memo_manage);
		paramMap.put("memo_id", memo_manage_id);
		other.put("old_memo_manage", queryForObj("riskAudit.getProjectMemoForManage", paramMap));
		other.put("new_memo_manage", memo_manage);
		update("riskAudit.updateProjectMemoForManage", paramMap);
		
		String memo_manage_dgm = (String) context.contextMap.get("memo_manage_dgm");
		String memo_manage_dgm_id = (String) context.contextMap.get("memo_manage_dgm_id");
		paramMap.put("memo", memo_manage_dgm);
		paramMap.put("memo_id", memo_manage_dgm_id);
		other.put("old_memo_dgm", queryForObj("riskAudit.getProjectMemoForManage", paramMap));
		other.put("new_memo_dgm", memo_manage_dgm);
		update("riskAudit.updateProjectMemoForManage", paramMap);*/
		
		String user = String.valueOf(context.contextMap.get("s_employeeId"));
		String sensor = LeaseUtil.getSensorIdByCreditId(credit_id);
		String up_sensor = LeaseUtil.getUpUserByUserId(sensor);
		MailSettingTo mail = new MailSettingTo();
		
		mail.setEmailTo(LeaseUtil.getEmailByUserId(sensor) + ";" + LeaseUtil.getEmailByUserId(up_sensor));
		mail.setEmailCc(LeaseUtil.getEmailByUserId(user));
		mail.setEmailSubject("审查修改报告内容提醒");
		mail.setEmailContent(generateContent(guarantors, other));
		
		mailUtilService.sendMail(mail);
		
	}

	private String generateContent(List<Map<String, Object>> guarantors, Map<String, Object> other) {
		StringBuffer sb = new StringBuffer("<html>");
		sb.append("<style type=\"text/css\">");
		sb.append(".panel_table {");
		sb.append("	width : 100%;");
		sb.append("	border-collapse:collapse;");
		sb.append("	border:solid #A6C9E2; ");
		sb.append("	border-width:1px 0 0 1px;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table th {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append("	background-color: #E1EFFB;");
		sb.append("	padding : 2;");
		sb.append("	margin : 1;");
		sb.append("	font-weight: bold;");
		sb.append("	text-align: center;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	color: #2E6E9E;");
		sb.append("	height: 28px;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table th *{");
		sb.append("	text-align: center;");
		sb.append("	font-size: 14px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");

		sb.append(".panel_table tr{");
		sb.append("	cursor: default;");
		sb.append("	overflow: hidden;");
		sb.append("}");
		sb.append(".panel_table td {");
		sb.append("	border:solid #A6C9E2;");
		sb.append(" border-width:0 1px 1px 0;");
		sb.append(" text-align: left;");
		sb.append("	white-space: pre-wrap;");
		sb.append("	overflow: hidden;");
		sb.append("	background-color: #FFFFFF;");
		sb.append("	padding : 5px 5px;");
		sb.append("	font-size: 12px;");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append(".panel_table td *{");
		sb.append("	font-weight: normal;");
		sb.append("	color: black;");
		sb.append("	text-align: left;");
		sb.append("	font-size: 12px;");
		sb.append("	font-family: \"微软雅黑\";");
		sb.append("}");
		sb.append("</style>");
		sb.append("<table class=\"panel_table\">");
		sb.append("<tr>");
		sb.append("<th style=\"width: 20%;\">");
		sb.append("&nbsp;");
		sb.append("</th>");
		sb.append("<th style=\"width: 40%;\">");
		sb.append("修改前");
		sb.append("</th>");
		sb.append("<th style=\"width: 40%;\">");
		sb.append("修改后");
		sb.append("</th>");
		sb.append("</tr>");
		for (Map<String, Object> map : guarantors) {
			sb.append("<tr>");
			sb.append("<th>");
			sb.append("担保人：" + map.get("NAME"));
			sb.append("</th>");
			sb.append("<td>");
			sb.append(map.get("CODE"));
			sb.append("</td>");
			sb.append("<td>");
			sb.append(map.get("newValue"));
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("<tr>");
		sb.append("<th>");
		sb.append("租赁物放置地");
		sb.append("</th>");
		sb.append("<td>");
		sb.append(other.get("oldAddr"));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(other.get("newAddr"));
		sb.append("</td>");
		sb.append("</tr>");
		
		/*sb.append("<tr>");
		sb.append("<th>");
		sb.append("建议承做理由");
		sb.append("</th>");
		sb.append("<td>");
		sb.append(other.get("old_memo"));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(other.get("new_memo"));
		sb.append("</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>");
		sb.append("其他租赁条件");
		sb.append("</th>");
		sb.append("<td>");
		sb.append(other.get("old_memo_other"));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(other.get("new_memo_other"));
		sb.append("</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>");
		sb.append("区域主管审核意见");
		sb.append("</th>");
		sb.append("<td>");
		sb.append(other.get("old_memo_manage"));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(other.get("new_memo_manage"));
		sb.append("</td>");
		sb.append("</tr>");
		
		sb.append("<tr>");
		sb.append("<th>");
		sb.append("业务副总审核意见");
		sb.append("</th>");
		sb.append("<td>");
		sb.append(other.get("old_memo_dgm"));
		sb.append("</td>");
		sb.append("<td>");
		sb.append(other.get("new_memo_dgm"));
		sb.append("</td>");
		sb.append("</tr>");*/
		
		sb.append("</table>");
		sb.append("</html>");
		
		return sb.toString();
	}

	public NciicEntity doIdcardVerified(String name, String code, String userId) throws Exception {
		NciicEntity result = NciicUtil.nciicCheck(code, name);
		result.setCreate_by(userId);
		insert("riskAudit.saveVerifiedLog", result);
		return result;
	}

}
