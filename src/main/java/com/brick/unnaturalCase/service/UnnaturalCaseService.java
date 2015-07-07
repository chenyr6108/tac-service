package com.brick.unnaturalCase.service;

import java.awt.Color;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.BirtReportEngine;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.common.mail.service.MailCommonService;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.service.core.DataAccessor;
import com.brick.unnaturalCase.dao.UnnaturalCaseDAO;
import com.brick.unnaturalCase.to.UnnaturalCaseTO;
import com.brick.util.DateUtil;


public class UnnaturalCaseService extends BaseService {

	Log logger=LogFactory.getLog(UnnaturalCaseService.class);
	private UnnaturalCaseDAO unnaturalCaseDAO;
	private MailCommonService mailCommonService;
	private String path;
	private MailUtilService mailUtilService;
	private BirtReportEngine birt;
	
	public void setBirt(BirtReportEngine birt) {
		this.birt = birt;
	}

	public BirtReportEngine getBirt() {
		return birt;
	}

	public UnnaturalCaseDAO getUnnaturalCaseDAO() {
		return unnaturalCaseDAO;
	}

	public void setUnnaturalCaseDAO(UnnaturalCaseDAO unnaturalCaseDAO) {
		this.unnaturalCaseDAO = unnaturalCaseDAO;
	}
	
	public MailCommonService getMailCommonService() {
		return mailCommonService;
	}

	public void setMailCommonService(MailCommonService mailCommonService) {
		this.mailCommonService = mailCommonService;
	}

	//案件进度异常Batch Job
	@Transactional
	public void batchJobCaseCompare() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件进度异常batch job开始--------------");
		}
		
		List<Map<String,Object>> deptList=null;
		try {
			deptList=this.baseDAO.queryDept(new HashMap<String,String>());
		} catch (Exception e) {
			logger.debug("获得办事处出错!");
			e.printStackTrace();
		}
		
		//初始化办事处
		List<UnnaturalCaseTO> insertList=new ArrayList<UnnaturalCaseTO>();
		for(int i=0;deptList!=null&&i<deptList.size();i++) {
			UnnaturalCaseTO to=new UnnaturalCaseTO();
			if(deptList.get(i).get("DEPT_ID")!=null) {
				to.setDeptId(String.valueOf(deptList.get(i).get("DEPT_ID")));
				to.setDeptName((String)deptList.get(i).get("DEPT_NAME"));
				insertList.add(to);
			}
		}
		
		Map<String,String> param=new HashMap<String,String>();
		
		try {
			param.put("flag","1");
			//进件~访厂
			List<UnnaturalCaseTO> result1=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result1.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result1.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setA_B(result1.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","2");
			//访厂~初次提交风控
			List<UnnaturalCaseTO> result2=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result2.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result2.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setB_C(result2.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","3");
			//初次提交风控~最终提交风控
			List<UnnaturalCaseTO> result3=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result3.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result3.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setC_D(result3.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","4");
			//最终提交风控~审查核准
			List<UnnaturalCaseTO> result4=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result4.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result4.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setD_E(result4.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","5");
			//审查核准~业管初审
			List<UnnaturalCaseTO> result5=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result5.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result5.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setE_F(result5.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","6");
			//业管初审~拨款
			List<UnnaturalCaseTO> result6=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result6.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result6.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setF_G(result6.get(i).getAmount());
						break;
					}
				}
			}
			
			param.put("flag","7");
			//进件~拨款
			List<UnnaturalCaseTO> result7=this.unnaturalCaseDAO.getCaseCompare(param);
			for(int i=0;i<result7.size();i++) {
				for(int j=0;j<insertList.size();j++) {
					if(result7.get(i).getDeptId().equals(insertList.get(j).getDeptId())) {
						insertList.get(j).setA_G(result7.get(i).getAmount());
						break;
					}
				}
			}
			
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				if(!"18".equals(insertList.get(i).getDeptId())) {
					insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
					this.unnaturalCaseDAO.insertCaseCompare(insertList.get(i));
					//防止主键重复
					Thread.sleep(1);
				}
			}
		} catch (Exception e) {
			logger.debug("--------------案件进度异常batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("案件进度异常batch job失败!",e);
			} catch (Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------案件进度异常batch job结束--------------");
		}
	}

	public List<Map<String,Object>> getDateList(String flag) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getDateList开始--------------");
		}
		
		List<Map<String,Object>> resultList=null;
		
		resultList=this.unnaturalCaseDAO.getDateList(flag);
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getDateList结束--------------");
		}
		
		return resultList;
	}
	
	public List<UnnaturalCaseTO> getUnnaturalCaseCompare(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getUnnaturalCaseCompare开始--------------");
		}
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.unnaturalCaseDAO.getUnnaturalCaseCompare(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getUnnaturalCaseCompare结束--------------");
		}
		return resultList;
		
	}
	
	//逾期25天,前3期逾期未回访Batch Job
	@Transactional
	public void batchJobDunCase() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期25天,前3期逾期未回访batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertList=null;
		List<UnnaturalCaseTO> lockCodeList=null;
		List<UnnaturalCaseTO> suplNameList=null;
		
		try {
			logger.debug("--------------逾期25天,前3期逾期未回访getDunCase开始--------------");
			insertList=this.unnaturalCaseDAO.getDunCase();
			logger.debug("--------------逾期25天,前3期逾期未回访getDunCase结束--------------");
			StringBuffer rectId=new StringBuffer();
			for(int i=0;i<insertList.size();i++) {
				if(insertList.get(i).getRectId()!=null) {
					rectId.append("'");
					rectId.append(insertList.get(i).getRectId()+"',");
				}
			}
			if(rectId.length()!=0) {
				Map<String,String> param=new HashMap<String,String>();
				String rectIds=rectId.substring(0,rectId.length()-1).toString();
				param.put("rectId",rectIds);
				logger.debug("--------------逾期25天,前3期逾期未回访rectIds="+rectIds);
				logger.debug("--------------逾期25天,前3期逾期未回访getLockCode,getSuplName开始--------------");
				lockCodeList=this.unnaturalCaseDAO.getLockCode(param);
				suplNameList=this.unnaturalCaseDAO.getSuplNameForDunCase(param);
				logger.debug("--------------逾期25天,前3期逾期未回访getLockCode,getSuplName结束--------------");
				
				logger.debug("--------------逾期25天,前3期逾期未回访锁码,供应商名字设置开始--------------");
				for(int i=0;i<insertList.size();i++) {
					//加入锁码方式,因为1对多,所以用java代码加入
					for(int j=0;j<lockCodeList.size();j++) {
						if(insertList.get(i).getRectId()!=null) {
							if(insertList.get(i).getRectId().equals(lockCodeList.get(j).getRectId())) {
								if("".equals(insertList.get(i).getLockCode())) {
									insertList.get(i).setLockCode(lockCodeList.get(j).getLockCode());
								} else {
									insertList.get(i).setLockCode(insertList.get(i).getLockCode()+","+lockCodeList.get(j).getLockCode());
								}
							}
						}
					}
					
					for(int j=0;j<suplNameList.size();j++) {
						if(insertList.get(i).getRectId()!=null) {
							if(insertList.get(i).getRectId().equals(suplNameList.get(j).getRectId())) {
								if("".equals(insertList.get(i).getSuplName())) {
									insertList.get(i).setSuplName(suplNameList.get(j).getSuplName());
								} else {
									insertList.get(i).setSuplName(insertList.get(i).getSuplName()+","+suplNameList.get(j).getSuplName());
								}
							}
						}
					}
					logger.debug("--------------逾期25天,前3期逾期未回访锁码,供应商名字设置结束--------------");
				}
				
				//插入数据
				logger.debug("--------------逾期25天,前3期逾期未回访插数据开始--------------");
				for(int i=0;i<insertList.size();i++) {
					insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
					this.unnaturalCaseDAO.insertDunCase(insertList.get(i));
					//防止主键重复
					Thread.sleep(1);
				}
				logger.debug("--------------逾期25天,前3期逾期未回访插数据结束--------------");
			}
			
		} catch(Exception e) {
			logger.debug("--------------逾期25天,前3期逾期未回访batch job失败--------------");
			logger.debug("--------------e.printStackTrace()--------------="+e);
			try {
				this.mailCommonService.sendErrorMail("逾期25天,前3期逾期未回访batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期25天,前3期逾期未回访batch job结束--------------");
		}
	}
	
	public List<UnnaturalCaseTO> getUnnaturalDunCase(Map<String,String> param) throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getUnnaturalDunCase开始--------------");
		}
		
		List<UnnaturalCaseTO> resultList=null;
		
		resultList=this.unnaturalCaseDAO.getUnnaturalDunCase(param);
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------getUnnaturalDunCase结束--------------");
		}
		
		return resultList;
	}
	
	//拨款后待补文件Batch Job
	@Transactional
	public void batchJobUncompletedFileCase() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------拨款后待补文件batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertList=null;
		List<UnnaturalCaseTO> suplNameList=null;
		
		try {
			insertList=this.unnaturalCaseDAO.getUncompletedFileCase();
			
			StringBuffer creditId=new StringBuffer();
			for(int i=0;i<insertList.size();i++) {
				creditId.append("'");
				creditId.append(insertList.get(i).getCreditId()+"',");
			}
			
			if(creditId.length()!=0) {
				Map<String,String> param=new HashMap<String,String>();
				String creditIds=creditId.substring(0,creditId.length()-1).toString();
				param.put("creditIds",creditIds);
				suplNameList=this.unnaturalCaseDAO.getSuplNameForUncompletedFile(param);
				
				for(int i=0;i<insertList.size();i++) {
					for(int j=0;j<suplNameList.size();j++) {
						if(insertList.get(i).getCreditId().equals(suplNameList.get(j).getCreditId())) {
							if("".equals(insertList.get(i).getSuplName())) {
								insertList.get(i).setSuplName(suplNameList.get(j).getSuplName());
							} else {
								insertList.get(i).setSuplName(insertList.get(i).getSuplName()+","+suplNameList.get(j).getSuplName());
							}
						}
					}
				}
				
				//插入数据
				for(int i=0;i<insertList.size();i++) {
					insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
					this.unnaturalCaseDAO.insertUncompletedFileCase(insertList.get(i));
					//防止主键重复
					Thread.sleep(1);
				}
			}
		} catch(Exception e) {
			logger.debug("--------------拨款后待补文件batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("拨款后待补文件batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------拨款后待补文件batch job结束--------------");
		}
	}
	
	//出险逾期60天未理赔结案Batch Job
	@Transactional
	public void batchJobOnGoingInsuranceCase() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------出险逾期60天未理赔结案batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertList=null;
		
		try {
			insertList=this.unnaturalCaseDAO.getOnGoingInsuranceCase();
			
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertOnGoingInsuranceCase(insertList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
		} catch(Exception e) {
			logger.debug("--------------出险逾期60天未理赔结案batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("出险逾期60天未理赔结案batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------出险逾期60天未理赔结案batch job结束--------------");
		}
	}
	
	//提交审查逾期10天未核准,访厂逾期12天未提交审查batch job
	@Transactional
	public void batchJobPendingApproveOrCommitCase() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------提交审查逾期10天未核准,访厂逾期12天未提交审查batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertList1=null;
		List<UnnaturalCaseTO> insertList2=null;
		try {
			insertList1=this.unnaturalCaseDAO.getPendingApproveCase();
			insertList2=this.unnaturalCaseDAO.getPendingCommitCase();
			
			//插入数据
			for(int i=0;i<insertList1.size();i++) {
				insertList1.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertPendingApproveCase(insertList1.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			
			//插入数据
			for(int i=0;i<insertList2.size();i++) {
				insertList2.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertPendingCommitCase(insertList2.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			
			
		} catch(Exception e) {
			logger.debug("--------------提交审查逾期10天未核准,访厂逾期12天未提交审查batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("提交审查逾期10天未核准,访厂逾期12天未提交审查batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------提交审查逾期10天未核准,访厂逾期12天未提交审查batch job结束--------------");
		}
	}
	
	@Transactional
	public void batchJobDynamicCase() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertListA=null;
		List<UnnaturalCaseTO> insertListB=null;
		List<UnnaturalCaseTO> insertListC=null;
		List<UnnaturalCaseTO> insertListD=null;
		List<UnnaturalCaseTO> insertListE=null;
		List<UnnaturalCaseTO> insertListF=null;
		List<UnnaturalCaseTO> insertListG=null;
		List<UnnaturalCaseTO> insertList2=new ArrayList<UnnaturalCaseTO>();
		Map<String,String> param=new HashMap<String,String>();
		List<UnnaturalCaseTO> aList=null;
		List<UnnaturalCaseTO> bList=null;
		List<UnnaturalCaseTO> cList=null;
		List<UnnaturalCaseTO> dList=null;
		List<UnnaturalCaseTO> eList=null;
		List<UnnaturalCaseTO> fList=null;
		List<UnnaturalCaseTO> gList=null;
		
		List<Map<String,Object>> deptList=null;
		try {
			
			deptList=this.baseDAO.queryDept(param);
			
			insertListA=this.unnaturalCaseDAO.getNotVisitCustomer();
			insertListB=this.unnaturalCaseDAO.getVisitCustomer();
			insertListC=this.unnaturalCaseDAO.getFisrtWindControl();
			insertListD=this.unnaturalCaseDAO.getLastWindControl();
			insertListE=this.unnaturalCaseDAO.getApprovedNotAudit();
			insertListF=this.unnaturalCaseDAO.getHasAuditNotPay();
			insertListG=this.unnaturalCaseDAO.getHasCreditNotPay();
			
			//插入数据
			for(int i=0;i<insertListA.size();i++) {
				insertListA.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListA.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListB.size();i++) {
				insertListB.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListB.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListC.size();i++) {
				insertListC.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListC.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListD.size();i++) {
				insertListD.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListD.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListE.size();i++) {
				insertListE.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListE.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListF.size();i++) {
				insertListF.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListF.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			for(int i=0;i<insertListG.size();i++) {
				insertListG.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCase(insertListG.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			
			param.put("CODE","进件~未访厂");
			aList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","已访厂~未提交风控");
			bList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","初次提交风控~未最终提交风控");
			cList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","最终提交风控~审查未核准");
			dList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","审查核准~业管未初审");
			eList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","业管初审~未拨款");
			fList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			param.put("CODE","进件~未拨款");
			gList=this.unnaturalCaseDAO.getDynamicCaseCount(param);
			
			//加入办事处
			for(int i=0;deptList!=null&&i<deptList.size();i++) {
				UnnaturalCaseTO to=new UnnaturalCaseTO();
				to.setDeptId(String.valueOf(deptList.get(i).get("DEPT_ID")));
				to.setDeptName((String)deptList.get(i).get("DEPT_NAME"));
				insertList2.add(to);
			}
			
			for(int i=0;i<insertList2.size();i++) {
				//加入进件~未访厂
				for(int j=0;j<aList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(aList.get(j).getDeptId())) {
						insertList2.get(i).setA(aList.get(j).getNum());
					}
				}
				//加入已访厂~未提交风控
				for(int j=0;j<bList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(bList.get(j).getDeptId())) {
						insertList2.get(i).setB(bList.get(j).getNum());
					}
				}
				//加入初次提交风控~未最终提交风控
				for(int j=0;j<cList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(cList.get(j).getDeptId())) {
						insertList2.get(i).setC(cList.get(j).getNum());
					}
				}
				//加入最终提交风控~审查未核准
				for(int j=0;j<dList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(dList.get(j).getDeptId())) {
						insertList2.get(i).setD(dList.get(j).getNum());
					}
				}
				//加入审查核准~业管未初审
				for(int j=0;j<eList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(eList.get(j).getDeptId())) {
						insertList2.get(i).setE(eList.get(j).getNum());
					}
				}
				//加入业管初审~未拨款
				for(int j=0;j<fList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(fList.get(j).getDeptId())) {
						insertList2.get(i).setF(fList.get(j).getNum());
					}
				}
				//加入进件~未拨款
				for(int j=0;j<gList.size();j++) {
					if(insertList2.get(i).getDeptId().equals(gList.get(j).getDeptId())) {
						insertList2.get(i).setG(gList.get(j).getNum());
					}
				}
			}
			
			//插入数据
			for(int i=0;i<insertList2.size();i++) {
				insertList2.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDynamicCaseCount(insertList2.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			
			
		} catch(Exception e) {
			logger.debug("--------------未拨款案件进度异常batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("未拨款案件进度异常batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------未拨款案件进度异常batch job结束--------------");
		}
	}
	
	public Map<String,Object> getUnnaturalDunCaseCount(Map<String,String> param) throws Exception {
		
		Map<String,Object> resultMap=null;
		
		resultMap=this.unnaturalCaseDAO.getUnnaturalDunCaseCount(param);
		
		return resultMap;
	}
	
	@Transactional
	public void batchJobDunVisitCase() throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("--------------访厂报告逾11天未提交batch job开始--------------");
		}
		
		List<UnnaturalCaseTO> insertList=null;
		
		try {
			insertList=this.unnaturalCaseDAO.getDunVisit();
			
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.unnaturalCaseDAO.insertDunVisit(insertList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
		} catch(Exception e) {
			logger.debug("--------------访厂报告逾11天未提交batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("访厂报告逾11天未提交batch job失败!",e);
			} catch(Exception e1) {
				logger.debug("--------------邮件发送失败--------------");
				e1.printStackTrace();
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------访厂报告逾11天未提交batch job结束--------------");
		}
	}
	
	
	public void getFilesAfterPayMoney() throws Exception{
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH)+1;

		month -= 1;
		if(month==0){
			year -= 1;
			month = 12;
		}
		ReportDateTo dateTo = ReportDateUtil.getDateByYearAndMonth(year, month);
		
		Map params = new HashMap();
		params.put("beginDate", dateTo.getBeginTime());
		params.put("endDate", dateTo.getEndTime());
		List<Map<String, Object>> list = this.baseDAO.queryForListUseMap("unnaturalCase.getFilesAfterPayMoney", params);

		WritableSheet sheet = null;
		
		String filePath =  getPath() + File.separator+year;
		File file = new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}

		filePath +=  File.separator +"拨款案件后待补文件("+month+"月).xls";
		file = new File(filePath);
		
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("ISO-8859-1");
		WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
		sheet = wb.createSheet("拨款案件后待补文件", 0);
		
		
		WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		format.setAlignment(Alignment.CENTRE);
		format.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format.setWrap(true);
		

		WritableFont font2 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);	
		WritableCellFormat format2 = new WritableCellFormat(font2);
		format2.setAlignment(Alignment.CENTRE);
		format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
		format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		format2.setWrap(true);
		
	
		WritableCellFormat precent = new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
		precent.setAlignment(Alignment.CENTRE);
		precent.setVerticalAlignment(VerticalAlignment.CENTRE); 
		precent.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
		precent.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
		precent.setWrap(true);
		
		
		
		int column = 0;
		int row = 1;
		
		createLabel(sheet, row, column, "案件号",format);
		column += 2;

		createLabel(sheet, row, column, "合同号",format);
		column += 2;
		
		createLabel(sheet, row, column, "客户名称",format);
		column += 2;
		
		createLabel(sheet, row, column, "待补文件名称",format);
		column += 2;
		
		createLabel(sheet, row, column, "业务员",format);
		column += 2;
		
		createLabel(sheet, row, column, "原始经办主管",format);
		column += 2;
		
		createLabel(sheet, row, column, "供应商",format);
		column += 2;
		
		createLabel(sheet, row, column, "拨款日",format);
		column += 2;
				
		createLabel(sheet, row, column, "应补回日",format);
		column += 2;
		
		createLabel(sheet, row, column, "延迟天数",format);
		column += 2;
		
		createLabel(sheet, row, column, "待补原因",format);
		column += 2;
		
		createLabel(sheet, row, column, "拨款方式",format);
		column += 2;
		
		createLabel(sheet, row, column, "已拨款金额",format);
		column += 2;
				
		createLabel(sheet, row, column, "拨款金额",format);
	
		
		row++;
		if(list != null && list.size()>0){
	
			for(Map m :list){
				column = 0;
				createLabel(sheet, row, column, m.get("CREDITRUNCODE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("LEASECODE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("CUSTNAME").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("FILENAME").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("NAME").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("ORG_MANAGER").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("SUPLNAME").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("FINANCEDATE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("SHOULDFINISHDATE").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("DELAYDAY").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("ISSUEREASON").toString(),format2);
				column += 2;
				
				createLabel(sheet, row, column, m.get("TYPE").toString(),format2);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("PAY_MONEY").toString(),format2);
				column += 2;
				
				createNumberLabel(sheet, row, column, m.get("TOTAL_MONEY").toString(),format2);
				
				row++;

			}
		}
	
		wb.write();
		wb.close();
		
		MailSettingTo mailSettingTo = new MailSettingTo();
		mailSettingTo.setEmailContent("拨款案件后待补文件");
		mailSettingTo.setEmailAttachPath(filePath);
		mailUtilService.sendMail(2004, mailSettingTo);
		
	}
	
	
	public void sendUnnaturalCaseBirtReport() throws Exception{
		
		if(!this.isWorkingDay()){
			return;
		}
		
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		
		String filePath = birt.getOutputPath() +File.separator+"待补文件(购置凭证)" +File.separator +DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = "待补文件(购置凭证)"+dateStr+".xls";
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
			WritableSheet sheet = wb.createSheet("待补文件(购置凭证)", 0);
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			
			WritableFont redfont = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.BOLD);
			redfont.setColour(Colour.RED);
			WritableCellFormat redformat = new WritableCellFormat(redfont);
			redformat.setAlignment(Alignment.CENTRE);
			redformat.setVerticalAlignment(VerticalAlignment.CENTRE); 
			redformat.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			WritableFont numberfont2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			WritableCellFormat numberformat2 = new WritableCellFormat(numberfont2);
			numberformat2.setAlignment(Alignment.RIGHT);
			numberformat2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			numberformat2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setWrap(true);
			
			WritableFont redfont2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			redfont2.setColour(Colour.RED);
			WritableCellFormat redformat2 = new WritableCellFormat(redfont2);
			redformat2.setAlignment(Alignment.RIGHT);
			redformat2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			redformat2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setWrap(true);
						
			
			int column = 0;
			int row = 0;
			
			sheet.setColumnView(column, 20);
			Label lable = new Label(column, row, "合同号", format);
			sheet.addCell(lable);
			column ++;

			sheet.setColumnView(column, 40);
			lable = new Label(column, row, "客户名称", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 25);
			lable = new Label(column, row, "待补文件名称", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "办事处", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "经办人", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 40);
			lable = new Label(column, row, "供应商", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 0);
			lable = new Label(column, row, "利差", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "拨款日", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "拨款金额", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "设备总价款", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "稅務風險", redformat);
			sheet.addCell(lable);

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			row++;
			List<Map> list = (List<Map>) DataAccessor.query("unnaturalCase.getUnnaturalCaseBirtReport",null);
			
			Map total = (Map) DataAccessor.queryForObj("unnaturalCase.getUnnaturalCaseBirtReportTotal",null);

			
			if(list != null && list.size()>0){
	
				for(Map m :list){
					column = 0;
					
					lable = new Label(column, row, m.get("LEASE_CODE").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("CUST_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("FILE_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("DEPT_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("SUPL_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("RATE_DIFF")).toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("PAY_DATE").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("PAY_MONEY")), numberformat2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("LEASE_TOPRIC")), numberformat2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("TAX")), redformat2);
					sheet.addCell(lable);
					column ++;
															
					row++;
				}
			}
			lable = new Label(7, row, "合计", format);
			sheet.addCell(lable);
			if(total!=null){
				lable = new Label(8, row, nf.format(total.get("PAY_MONEY")), numberformat2);
				sheet.addCell(lable);
				
				lable = new Label(9, row, nf.format(total.get("LEASE_TOPRIC")), numberformat2);
				sheet.addCell(lable);
				
				lable = new Label(10, row, nf.format(total.get("TAX")), redformat2);
				sheet.addCell(lable);
			}
			wb.write();
			wb.close();
			
			
			//birt.executeReport("unnaturalCase/unnaturalCaseExcelForEmail.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			logger.error("制作待补文件(购置凭证)出错-" + e.getMessage());
			throw new Exception("制作待补文件(购置凭证)出错-" + e.getMessage());
		}
		
		// 发送Email
		try {
//			// 发邮件
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(filePath + File.separator + fileName);
			mailSettingTo.setEmailSubject("待补文件(购置凭证)");
			mailUtilService.sendMail(2005, mailSettingTo);
		} catch (Exception e) {
			logger.error("发送待补文件(购置凭证)-" + e.getMessage());
			throw new Exception("发送待补文件(购置凭证)-" + e.getMessage());
		}
	}
	
	public void sendUnnaturalCaseBeforePayBirtReport() throws Exception{
		
		if(!this.isWorkingDay()){
			return;
		}
		
		String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmSSS");
		
		String filePath = birt.getOutputPath() +File.separator+"待补文件(交机前拨款)" +File.separator +DateUtil.dateToString(new Date(), "yyyy_MM_dd");
		String fileName = "待补文件(交机前拨款)"+dateStr+".xls";
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("ISO-8859-1");
			WritableWorkbook wb = Workbook.createWorkbook(file, workbookSettings);
			WritableSheet sheet = wb.createSheet("待补文件(交机前拨款)", 0);
			
			
			WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format.setWrap(true);
			
			WritableFont redfont = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.BOLD);
			redfont.setColour(Colour.RED);
			WritableCellFormat redformat = new WritableCellFormat(redfont);
			redformat.setAlignment(Alignment.CENTRE);
			redformat.setVerticalAlignment(VerticalAlignment.CENTRE); 
			redformat.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			redformat.setWrap(true);
			

			WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			WritableCellFormat format2 = new WritableCellFormat(font2);
			format2.setAlignment(Alignment.CENTRE);
			format2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			format2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			format2.setWrap(true);
			
			WritableFont numberfont2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			WritableCellFormat numberformat2 = new WritableCellFormat(numberfont2);
			numberformat2.setAlignment(Alignment.RIGHT);
			numberformat2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			numberformat2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			numberformat2.setWrap(true);
			
			WritableFont redfont2 = new WritableFont(WritableFont.createFont("微软雅黑"), 12, WritableFont.NO_BOLD);	
			redfont2.setColour(Colour.RED);
			WritableCellFormat redformat2 = new WritableCellFormat(redfont2);
			redformat2.setAlignment(Alignment.RIGHT);
			redformat2.setVerticalAlignment(VerticalAlignment.CENTRE); 
			redformat2.setBorder(Border.BOTTOM, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.TOP, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.LEFT, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setBorder(Border.RIGHT, BorderLineStyle.THIN,Colour.BLACK);
			redformat2.setWrap(true);
						
			
			int column = 0;
			int row = 0;
			
			sheet.setColumnView(column, 20);
			Label lable = new Label(column, row, "合同号", format);
			sheet.addCell(lable);
			column ++;

			sheet.setColumnView(column, 40);
			lable = new Label(column, row, "客户名称", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 25);
			lable = new Label(column, row, "待补文件名称", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "办事处", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "经办人", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 40);
			lable = new Label(column, row, "供应商", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 0);
			lable = new Label(column, row, "利差", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "拨款日", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "拨款金额", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "设备总价款", format);
			sheet.addCell(lable);
			column ++;
			
			sheet.setColumnView(column, 15);
			lable = new Label(column, row, "稅務風險", redformat);
			sheet.addCell(lable);

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			row++;
			List<Map> list = (List<Map>) DataAccessor.query("unnaturalCase.getUnnaturalCaseBeforePayBirtReport",null);
			
			Map total = (Map) DataAccessor.queryForObj("unnaturalCase.getUnnaturalCaseBeforePayBirtReportTotal",null);

			
			if(list != null && list.size()>0){
	
				for(Map m :list){
					column = 0;
					
					lable = new Label(column, row, m.get("LEASE_CODE").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("CUST_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("FILE_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("DEPT_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("SUPL_NAME").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("RATE_DIFF")).toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, m.get("PAY_DATE").toString(), format2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("PAY_MONEY")), numberformat2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("LEASE_TOPRIC")), numberformat2);
					sheet.addCell(lable);
					column ++;
					
					lable = new Label(column, row, nf.format(m.get("TAX")), redformat2);
					sheet.addCell(lable);
					column ++;
															
					row++;
				}
			}
			lable = new Label(7, row, "合计", format);
			sheet.addCell(lable);
			if(total!=null){
				lable = new Label(8, row, nf.format(total.get("PAY_MONEY")), numberformat2);
				sheet.addCell(lable);
				
				lable = new Label(9, row, nf.format(total.get("LEASE_TOPRIC")), numberformat2);
				sheet.addCell(lable);
				
				lable = new Label(10, row, nf.format(total.get("TAX")), redformat2);
				sheet.addCell(lable);
			}
			wb.write();
			wb.close();
			
			
			//birt.executeReport("unnaturalCase/unnaturalCaseExcelForEmail.rptdesign", fileName, paramMap);
		} catch (Exception e) {
			logger.error("制作待补文件(交机前拨款)出错-" + e.getMessage());
			throw new Exception("制作待补文件(交机前拨款)出错-" + e.getMessage());
		}
		
		// 发送Email
		try {
//			// 发邮件
			MailSettingTo mailSettingTo = new MailSettingTo();
			mailSettingTo.setEmailAttachPath(filePath + File.separator + fileName);
			mailSettingTo.setEmailSubject("待补文件(交机前拨款)");
			mailUtilService.sendMail(6000, mailSettingTo);
		} catch (Exception e) {
			logger.error("发送待补文件(交机前拨款)-" + e.getMessage());
			throw new Exception("发送待补文件(交机前拨款)-" + e.getMessage());
		}
	}
	
	public static List<Map> getUnnaturalCaseBirtReport() throws Exception{
		 List<Map> list = (List<Map>) DataAccessor.query("unnaturalCase.getUnnaturalCaseBirtReport",null);
		 return list;
	}
	
	private void createLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		sheet.mergeCells(column, row, column+1, row);
		Label lable = new Label(column, row, content, format);
		sheet.addCell(lable);
	}
	
	private void createNumberLabel(WritableSheet sheet,int row,int column,String content,WritableCellFormat format) throws RowsExceededException, WriteException{
		sheet.mergeCells(column, row, column+1, row);
		jxl.write.Number numberLabel  =   new  jxl.write.Number(column, row ,Double.parseDouble(content),format);  
		sheet.addCell(numberLabel);
	}

	public String getPath() {
		return "\\\\"+LeaseUtil.getIPAddress()+path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
}
