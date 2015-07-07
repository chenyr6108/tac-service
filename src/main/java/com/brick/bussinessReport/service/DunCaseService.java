package com.brick.bussinessReport.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.bussinessReport.dao.DunCaseDAO;
import com.brick.bussinessReport.to.DunCaseChartTO;
import com.brick.bussinessReport.to.DunCaseDetailTO;
import com.brick.bussinessReport.to.DunCaseTO;
import com.brick.common.mail.service.MailCommonService;
import com.brick.deptCmpy.to.DeptCmpyTO;

import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.util.Constants;

public class DunCaseService extends BaseService {

	Log logger=LogFactory.getLog(DunCaseService.class);
	
	private DunCaseDAO dunCaseDAO;
	private MailCommonService mailCommonService;
	
	public DunCaseDAO getDunCaseDAO() {
		return dunCaseDAO;
	}

	public void setDunCaseDAO(DunCaseDAO dunCaseDAO) {
		this.dunCaseDAO = dunCaseDAO;
	}
	
	public MailCommonService getMailCommonService() {
		return mailCommonService;
	}

	public void setMailCommonService(MailCommonService mailCommonService) {
		this.mailCommonService = mailCommonService;
	}

	@Transactional(rollbackFor=Exception.class)
	public void batchJobByUser() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期报表(按人员分)batch job开始--------------");
		}
		
		Map<String,Integer> param=new HashMap<String,Integer>();
		
		List<DunCaseTO> totalList=null;
		List<DunCaseTO> list8_14=null;
		List<DunCaseTO> list15_30=null;
		List<DunCaseTO> list31_60=null;
		List<DunCaseTO> list61_90=null;
		List<DunCaseTO> list31_=null;
		List<DunCaseTO> list91_=null;
		List<DunCaseTO> list181_=null;
		List<DunCaseTO> list8_=null;
		try {
			//获得总逾期件数与金额
			totalList=this.dunCaseDAO.getTotalDunCaseByUserId();
			
			//获得总逾期8天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			list8_=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期31天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			list31_=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期91天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			list91_=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期181天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			list181_=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期8~14天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			param.put("DUN_DAY2",Constants.DUN_DAY_14);
			list8_14=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期15~30天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			param.put("DUN_DAY2",Constants.DUN_DAY_30);
			list15_30=this.dunCaseDAO.getDunCaseByUserId(param);
			
			//获得总逾期31~60天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			param.put("DUN_DAY2",Constants.DUN_DAY_60);
			list31_60=this.dunCaseDAO.getDunCaseByUserId(param);

			//获得总逾期61~90天件数与金额
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			param.put("DUN_DAY2",Constants.DUN_DAY_90);
			list61_90=this.dunCaseDAO.getDunCaseByUserId(param);
			
			for(int i=0;i<totalList.size();i++) {
				
				for(int j=0;j<list8_.size();j++) {
					if(totalList.get(i).getUserId()==list8_.get(j).getUserId()) {
						totalList.get(i).setCount8_(list8_.get(j).getTotalCount());
						totalList.get(i).setMoney8_(list8_.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list31_.size();j++) {
					if(totalList.get(i).getUserId()==list31_.get(j).getUserId()) {
						totalList.get(i).setCount31_(list31_.get(j).getTotalCount());
						totalList.get(i).setMoney31_(list31_.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list91_.size();j++) {
					if(totalList.get(i).getUserId()==list91_.get(j).getUserId()) {
						totalList.get(i).setCount91_(list91_.get(j).getTotalCount());
						totalList.get(i).setMoney91_(list91_.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list181_.size();j++) {
					if(totalList.get(i).getUserId()==list181_.get(j).getUserId()) {
						totalList.get(i).setCount181_(list181_.get(j).getTotalCount());
						totalList.get(i).setMoney181_(list181_.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list8_14.size();j++) {
					if(totalList.get(i).getUserId()==list8_14.get(j).getUserId()) {
						totalList.get(i).setCount8_14(list8_14.get(j).getTotalCount());
						totalList.get(i).setMoney8_14(list8_14.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list15_30.size();j++) {
					if(totalList.get(i).getUserId()==list15_30.get(j).getUserId()) {
						totalList.get(i).setCount15_30(list15_30.get(j).getTotalCount());
						totalList.get(i).setMoney15_30(list15_30.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list31_60.size();j++) {
					if(totalList.get(i).getUserId()==list31_60.get(j).getUserId()) {
						totalList.get(i).setCount31_60(list31_60.get(j).getTotalCount());
						totalList.get(i).setMoney31_60(list31_60.get(j).getTotalMoney());
						break;
					}
				}
				
				for(int j=0;j<list61_90.size();j++) {
					if(totalList.get(i).getUserId()==list61_90.get(j).getUserId()) {
						totalList.get(i).setCount61_90(list61_90.get(j).getTotalCount());
						totalList.get(i).setMoney61_90(list61_90.get(j).getTotalMoney());
						break;
					}
				}
				
			}
			
			//插入数据
			for(int i=0;i<totalList.size();i++) {
				totalList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.dunCaseDAO.insertDunCaseByUserId(totalList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
		} catch (Exception e) {
			logger.debug("--------------逾期报表(按人员分)batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("逾期报表(按人员分)batch job失败",e);
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期报表(按人员分)batch job结束--------------");
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJobByMoney() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期报表(按融资金额分)batch job开始--------------");
		}
		
		Map<String,Object> param=new HashMap<String,Object>();
		
		List<DunCaseTO> insertList=new ArrayList<DunCaseTO>();
		
		DunCaseTO totalTO_50$=null;//50万元以下总应收租金
		DunCaseTO totalTO50_100$=null;//50万元~100万元总应收租金
		DunCaseTO totalTO100_200$=null;//100万元~200万元总应收租金
		DunCaseTO totalTO200_300$=null;//200万元~300万元总应收租金
		DunCaseTO totalTO300$_=null;//300万元以上总应收租金
		
		DunCaseTO tO_7=null;//7天以下逾期金额
		DunCaseTO tO8_=null;//8天以上逾期金额
		DunCaseTO tO15_=null;//15天以上逾期金额
		DunCaseTO tO31_=null;//31天以上逾期金额
		DunCaseTO tO61_=null;//61天以上逾期金额
		DunCaseTO tO91_=null;//91天以上逾期金额
		DunCaseTO tO181_=null;//91天以上逾期金额
		try {
			//获得total金额
			param.put("MONEY2",Constants.$50);
			param.put("FLAG",Constants._50);
			totalTO_50$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$50);
			param.put("MONEY2",Constants.$100);
			param.put("FLAG",Constants._50_100);
			totalTO50_100$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$100);
			param.put("MONEY2",Constants.$200);
			param.put("FLAG",Constants._100_200);
			totalTO100_200$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$200);
			param.put("MONEY2",Constants.$300);
			param.put("FLAG",Constants._200_300);
			totalTO200_300$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$300);
			param.remove("MONEY2");
			param.put("FLAG",Constants._300);
			totalTO300$_=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.remove("MONEY1");
			param.remove("FLAG");
			//获得50万元以下,逾期7天以下租金
			param.put("MONEY2",Constants.$50);
			param.put("DUN_DAY2",Constants.DUN_DAY_7);
			tO_7=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount_7(tO_7.getTotalCount());
			totalTO_50$.setMoney_7(tO_7.getTotalMoney());
			//获得50万元以下,逾期8天以上租金
			param.remove("DUN_DAY2");
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			tO8_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount8_(tO8_.getTotalCount());
			totalTO_50$.setMoney8_(tO8_.getTotalMoney());
			//获得50万元以下,逾期15天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			tO15_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount15_(tO15_.getTotalCount());
			totalTO_50$.setMoney15_(tO15_.getTotalMoney());
			//获得50万元以下,逾期31天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			tO31_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount31_(tO31_.getTotalCount());
			totalTO_50$.setMoney31_(tO31_.getTotalMoney());
			//获得50万元以下,逾期61天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			tO61_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount61_(tO61_.getTotalCount());
			totalTO_50$.setMoney61_(tO61_.getTotalMoney());
			//获得50万元以下,逾期91天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			tO91_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount91_(tO91_.getTotalCount());
			totalTO_50$.setMoney91_(tO91_.getTotalMoney());
			//获得50万元以下,逾期181天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			tO181_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO_50$.setCount181_(tO181_.getTotalCount());
			totalTO_50$.setMoney181_(tO181_.getTotalMoney());
			insertList.add(totalTO_50$);
			
			param.remove("DUN_DAY1");
			param.remove("MONEY2");
			//获得50~100万元,逾期7天以下租金
			param.put("MONEY1",Constants.$50);
			param.put("MONEY2",Constants.$100);
			param.put("DUN_DAY2",Constants.DUN_DAY_7);
			tO_7=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount_7(tO_7.getTotalCount());
			totalTO50_100$.setMoney_7(tO_7.getTotalMoney());
			//获得50~100万元,逾期8天以上租金
			param.remove("DUN_DAY2");
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			tO8_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount8_(tO8_.getTotalCount());
			totalTO50_100$.setMoney8_(tO8_.getTotalMoney());
			//获得50~100万元,逾期15天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			tO15_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount15_(tO15_.getTotalCount());
			totalTO50_100$.setMoney15_(tO15_.getTotalMoney());
			//获得50~100万元,逾期31天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			tO31_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount31_(tO31_.getTotalCount());
			totalTO50_100$.setMoney31_(tO31_.getTotalMoney());
			//获得50~100万元,逾期61天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			tO61_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount61_(tO61_.getTotalCount());
			totalTO50_100$.setMoney61_(tO61_.getTotalMoney());
			//获得50~100万元,逾期91天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			tO91_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount91_(tO91_.getTotalCount());
			totalTO50_100$.setMoney91_(tO91_.getTotalMoney());
			//获得50~100万元以下,逾期181天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			tO181_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO50_100$.setCount181_(tO181_.getTotalCount());
			totalTO50_100$.setMoney181_(tO181_.getTotalMoney());
			insertList.add(totalTO50_100$);
			
			param.remove("DUN_DAY1");
			//获得100~200万元,逾期7天以下租金
			param.put("MONEY1",Constants.$100);
			param.put("MONEY2",Constants.$200);
			param.put("DUN_DAY2",Constants.DUN_DAY_7);
			tO_7=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount_7(tO_7.getTotalCount());
			totalTO100_200$.setMoney_7(tO_7.getTotalMoney());
			//获得100~200万元,逾期8天以上租金
			param.remove("DUN_DAY2");
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			tO8_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount8_(tO8_.getTotalCount());
			totalTO100_200$.setMoney8_(tO8_.getTotalMoney());
			//获得100~200万元,逾期15天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			tO15_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount15_(tO15_.getTotalCount());
			totalTO100_200$.setMoney15_(tO15_.getTotalMoney());
			//获得100~200万元,逾期31天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			tO31_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount31_(tO31_.getTotalCount());
			totalTO100_200$.setMoney31_(tO31_.getTotalMoney());
			//获得100~200万元,逾期61天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			tO61_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount61_(tO61_.getTotalCount());
			totalTO100_200$.setMoney61_(tO61_.getTotalMoney());
			//获得100~200万元,逾期91天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			tO91_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount91_(tO91_.getTotalCount());
			totalTO100_200$.setMoney91_(tO91_.getTotalMoney());
			//获得100~200万元以下,逾期181天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			tO181_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO100_200$.setCount181_(tO181_.getTotalCount());
			totalTO100_200$.setMoney181_(tO181_.getTotalMoney());
			insertList.add(totalTO100_200$);
			
			param.remove("DUN_DAY1");
			//获得200~300万元,逾期7天以下租金
			param.put("MONEY1",Constants.$200);
			param.put("MONEY2",Constants.$300);
			param.put("DUN_DAY2",Constants.DUN_DAY_7);
			tO_7=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount_7(tO_7.getTotalCount());
			totalTO200_300$.setMoney_7(tO_7.getTotalMoney());
			//获得200~300万元,逾期8天以上租金
			param.remove("DUN_DAY2");
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			tO8_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount8_(tO8_.getTotalCount());
			totalTO200_300$.setMoney8_(tO8_.getTotalMoney());
			//获得200~300万元,逾期15天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			tO15_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount15_(tO15_.getTotalCount());
			totalTO200_300$.setMoney15_(tO15_.getTotalMoney());
			//获得200~300万元,逾期31天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			tO31_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount31_(tO31_.getTotalCount());
			totalTO200_300$.setMoney31_(tO31_.getTotalMoney());
			//获得200~300万元,逾期61天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			tO61_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount61_(tO61_.getTotalCount());
			totalTO200_300$.setMoney61_(tO61_.getTotalMoney());
			//获得200~300万元,逾期91天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			tO91_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount91_(tO91_.getTotalCount());
			totalTO200_300$.setMoney91_(tO91_.getTotalMoney());
			//获得200~300万元以下,逾期181天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			tO181_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO200_300$.setCount181_(tO181_.getTotalCount());
			totalTO200_300$.setMoney181_(tO181_.getTotalMoney());
			insertList.add(totalTO200_300$);
			
			param.remove("DUN_DAY1");
			param.remove("MONEY2");
			//获得300万元以上,逾期7天以下租金
			param.put("MONEY1",Constants.$300);
			param.put("DUN_DAY2",Constants.DUN_DAY_7);
			tO_7=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount_7(tO_7.getTotalCount());
			totalTO300$_.setMoney_7(tO_7.getTotalMoney());
			//获得300万元以上,逾期8天以上租金
			param.remove("DUN_DAY2");
			param.put("DUN_DAY1",Constants.DUN_DAY_8);
			tO8_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount8_(tO8_.getTotalCount());
			totalTO300$_.setMoney8_(tO8_.getTotalMoney());
			//获得300万元以上,逾期15天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_15);
			tO15_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount15_(tO15_.getTotalCount());
			totalTO300$_.setMoney15_(tO15_.getTotalMoney());
			//获得300万元以上,逾期31天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_31);
			tO31_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount31_(tO31_.getTotalCount());
			totalTO300$_.setMoney31_(tO31_.getTotalMoney());
			//获得300万元以上,逾期61天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_61);
			tO61_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount61_(tO61_.getTotalCount());
			totalTO300$_.setMoney61_(tO61_.getTotalMoney());
			//获得300万元以上,逾期91天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_91);
			tO91_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount91_(tO91_.getTotalCount());
			totalTO300$_.setMoney91_(tO91_.getTotalMoney());
			//获得300万元以上,逾期181天以上租金
			param.put("DUN_DAY1",Constants.DUN_DAY_181);
			tO181_=this.dunCaseDAO.getDunCaseByMoney(param);
			totalTO300$_.setCount181_(tO181_.getTotalCount());
			totalTO300$_.setMoney181_(tO181_.getTotalMoney());
			insertList.add(totalTO300$_);
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.dunCaseDAO.insertDunCaseByMoney(insertList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
		} catch (Exception e) {
			logger.debug("--------------逾期报表(按融资金额分)batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("逾期报表(按融资金额分)batch job失败",e);
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期报表(按融资金额分)batch job结束--------------");
		}
	}
	
	public List<DunCaseTO> getDunCaseDetail(Map<String,Object> param) throws Exception {
		return this.dunCaseDAO.getDunCaseDetail(param);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void batchJobLeaseRzeByMoney() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------融资金额,租金比batch job开始--------------");
		}
		
		DunCaseTO totalTO_50$=null;//50万元以下总金额
		DunCaseTO totalTO50_100$=null;//50万元~100万元总金额
		DunCaseTO totalTO100_200$=null;//100万元~200万元总金额
		DunCaseTO totalTO200_300$=null;//200万元~300万元总金额
		DunCaseTO totalTO300$_=null;//300万元以上总金额
		
		DunCaseTO rentTO_50$=null;//50万元以下总应收租金
		DunCaseTO rentTO50_100$=null;//50万元~100万元总应收租金
		DunCaseTO rentTO100_200$=null;//100万元~200万元总应收租金
		DunCaseTO rentTO200_300$=null;//200万元~300万元总应收租金
		DunCaseTO rentTO300$_=null;//300万元以上总应收租金
		
		List<DunCaseTO> insertList=new ArrayList<DunCaseTO>();
		
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			
			param.put("MONEY2",Constants.$50);
			param.put("FLAG",Constants._50);
			totalTO_50$=this.dunCaseDAO.getTotalMoney(param);
			
			param.put("MONEY1",Constants.$50);
			param.put("MONEY2",Constants.$100);
			param.put("FLAG",Constants._50_100);
			totalTO50_100$=this.dunCaseDAO.getTotalMoney(param);
			
			param.put("MONEY1",Constants.$100);
			param.put("MONEY2",Constants.$200);
			param.put("FLAG",Constants._100_200);
			totalTO100_200$=this.dunCaseDAO.getTotalMoney(param);
			
			param.put("MONEY1",Constants.$200);
			param.put("MONEY2",Constants.$300);
			param.put("FLAG",Constants._200_300);
			totalTO200_300$=this.dunCaseDAO.getTotalMoney(param);
			
			param.remove("MONEY2");
			param.put("MONEY1",Constants.$300);
			param.put("FLAG",Constants._300);
			totalTO300$_=this.dunCaseDAO.getTotalMoney(param);
			
			param.remove("MONEY1");
			param.put("MONEY2",Constants.$50);
			param.put("FLAG",Constants._50);
			rentTO_50$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$50);
			param.put("MONEY2",Constants.$100);
			param.put("FLAG",Constants._50_100);
			rentTO50_100$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$100);
			param.put("MONEY2",Constants.$200);
			param.put("FLAG",Constants._100_200);
			rentTO100_200$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.put("MONEY1",Constants.$200);
			param.put("MONEY2",Constants.$300);
			param.put("FLAG",Constants._200_300);
			rentTO200_300$=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			param.remove("MONEY2");
			param.put("MONEY1",Constants.$300);
			param.put("FLAG",Constants._300);
			rentTO300$_=this.dunCaseDAO.getTotalDunCaseByMoney(param);
			
			double totalMoney=totalTO_50$.getTotalMoney()+totalTO50_100$.getTotalMoney()+totalTO100_200$.getTotalMoney()
					+totalTO200_300$.getTotalMoney()+totalTO300$_.getTotalMoney();
			double rentMoney=rentTO_50$.getTotalMoney()+rentTO50_100$.getTotalMoney()+rentTO100_200$.getTotalMoney()
					+rentTO200_300$.getTotalMoney()+rentTO300$_.getTotalMoney();
			
			totalTO_50$.setTotalScale(String.valueOf(totalTO_50$.getTotalMoney()/totalMoney));
			totalTO_50$.setRentMoney(rentTO_50$.getTotalMoney());
			totalTO_50$.setRentScale(String.valueOf(rentTO_50$.getTotalMoney()/rentMoney));
			totalTO_50$.setOriginalScale(String.valueOf(rentTO_50$.getTotalMoney()/totalTO_50$.getTotalMoney()));
			insertList.add(totalTO_50$);
			
			totalTO50_100$.setTotalScale(String.valueOf(totalTO50_100$.getTotalMoney()/totalMoney));
			totalTO50_100$.setRentMoney(rentTO50_100$.getTotalMoney());
			totalTO50_100$.setRentScale(String.valueOf(rentTO50_100$.getTotalMoney()/rentMoney));
			totalTO50_100$.setOriginalScale(String.valueOf(rentTO50_100$.getTotalMoney()/totalTO50_100$.getTotalMoney()));
			insertList.add(totalTO50_100$);
			
			totalTO100_200$.setTotalScale(String.valueOf(totalTO100_200$.getTotalMoney()/totalMoney));
			totalTO100_200$.setRentMoney(rentTO100_200$.getTotalMoney());
			totalTO100_200$.setRentScale(String.valueOf(rentTO100_200$.getTotalMoney()/rentMoney));
			totalTO100_200$.setOriginalScale(String.valueOf(rentTO100_200$.getTotalMoney()/totalTO100_200$.getTotalMoney()));
			insertList.add(totalTO100_200$);
			
			totalTO200_300$.setTotalScale(String.valueOf(totalTO200_300$.getTotalMoney()/totalMoney));
			totalTO200_300$.setRentMoney(rentTO200_300$.getTotalMoney());
			totalTO200_300$.setRentScale(String.valueOf(rentTO200_300$.getTotalMoney()/rentMoney));
			totalTO200_300$.setOriginalScale(String.valueOf(rentTO200_300$.getTotalMoney()/totalTO200_300$.getTotalMoney()));
			insertList.add(totalTO200_300$);
			
			totalTO300$_.setTotalScale(String.valueOf(totalTO300$_.getTotalMoney()/totalMoney));
			totalTO300$_.setRentMoney(rentTO300$_.getTotalMoney());
			totalTO300$_.setRentScale(String.valueOf(rentTO300$_.getTotalMoney()/rentMoney));
			totalTO300$_.setOriginalScale(String.valueOf(rentTO300$_.getTotalMoney()/totalTO300$_.getTotalMoney()));
			insertList.add(totalTO300$_);
			
			//插入数据
			for(int i=0;i<insertList.size();i++) {
				insertList.get(i).setId(String.valueOf(System.currentTimeMillis()));
				this.dunCaseDAO.insertTotalMoneyRentMoney(insertList.get(i));
				//防止主键重复
				Thread.sleep(1);
			}
			
		} catch(Exception e) {
			logger.debug("--------------融资金额,租金比batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("融资金额,租金比batch job失败",e);
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("--------------融资金额,租金比batch job结束--------------");
		}
	}
	
	public List<DunCaseTO> queryDunCaseByMoney(Map<String,Object> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.dunCaseDAO.queryDunCaseByMoney(param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(一级查询数据)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<DunCaseDetailTO> queryDunCase(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList = this.dunCaseDAO.queryDunCase(param);
		return resultList;
	}
	
	/**
	 * 逾期状况统计表(二级查询数据)
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<DunCaseDetailTO> queryDunCaseDetail(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList = this.dunCaseDAO.queryDunCaseDetail(param);
		return resultList;
	}

	/**
	 * 一级查询+二级查询金额
	 * @param contextMap
	 * @return
	 */
	public List<DunCaseDetailTO> queryDunCaseDetailByPrimaryAndPrice(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList = this.dunCaseDAO.queryDunCaseDetailByPrimaryAndPrice(param);
		return resultList;
	}
	
	/**
	 * 一级查询金额+二级查询
	 * @param contextMap
	 * @return
	 */
	public List<DunCaseDetailTO> queryDunCaseDetailByPriceAndSecondary(Map<String,Object> param) throws Exception {
		List<DunCaseDetailTO> resultList = this.dunCaseDAO.queryDunCaseDetailByPriceAndSecondary(param);
		return resultList;
	}
	
	public List<DunCaseTO> queryRentMoney(Map<String,Object> param) throws Exception {
		
		List<DunCaseTO> resultList=null;
		
		resultList=this.dunCaseDAO.queryRentMoney(param);
		
		if(resultList==null) {
			resultList=new ArrayList<DunCaseTO>();
		}
		
		return resultList;
	}
	
	//逾期图表的batch job
	public void batchJob() throws Exception {
		
		List<DunCaseChartTO> insertList=null;
		List<DunCaseChartTO> _181List=null;
		List<DunCaseChartTO> _31_91List=null;
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("NAME","上海设备");
		param.put("NAME1","苏州小车");
		
		insertList=this.dunCaseDAO.getTotalMoneyCount(param);
		
		_181List=this.dunCaseDAO.getDun181_(param);
		
		_31_91List=this.dunCaseDAO.getDun31_91_(param);
		
		for(int j=0;j<insertList.size();j++) {
			
			for(int ii=0;ii<_181List.size();ii++) {
				if(insertList.get(j).getCompanyName().equals(_181List.get(ii).getCompanyName())) {
					insertList.get(j).setCompanyId(_181List.get(ii).getCompanyId());
					insertList.get(j).setDun181_Count(_181List.get(ii).getDun181_Count());
					insertList.get(j).setDun181_Money(_181List.get(ii).getDun181_Money());
					break;
				}
			}
			
			for(int ii=0;ii<_31_91List.size();ii++) {
				if(insertList.get(j).getCompanyName().equals(_31_91List.get(ii).getCompanyName())) {
					insertList.get(j).setDun31_Count(_31_91List.get(ii).getDun31_Count());
					insertList.get(j).setDun31_Money(_31_91List.get(ii).getDun31_Money());
					insertList.get(j).setDun91_Count(_31_91List.get(ii).getDun91_Count());
					insertList.get(j).setDun91_Money(_31_91List.get(ii).getDun91_Money());
					break;
				}
			}
		}
		
		for(int j=0;j<insertList.size();j++) {
			this.dunCaseDAO.insertDunChart(insertList.get(j));
		}
	}
	
	/**
	 * 逾期状况统计详细表
	 * 每天定时插入DunReportDetail表,总计表
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Exception.class)
	public void batchJobAsDunReportDetail() throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期状况统计详细表(DunReportDetail)batch job开始--------------");
		}
		try {
			//清空当天
			this.dunCaseDAO.delete("businessReport.deleteDunReportDetail");
			//插入当天详细逾期状况数据
			this.dunCaseDAO.insert("businessReport.insertDunReportDetail");
		} catch (Exception e) {
			logger.debug("--------------逾期状况统计详细表(DunReportDetail)batch job失败--------------");
			try {
				this.mailCommonService.sendErrorMail("逾期状况统计详细表(DunReportDetail)batch job失败",e);
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("--------------逾期状况统计详细表(DunReportDetail)batch job结束--------------");
		}
	}
	
	public List<DunCaseChartTO> getDayChart(Map<String,Object> param) throws Exception {
		
		return this.dunCaseDAO.getDayChart(param);
	}
	
	public List<DunCaseChartTO> getWeekChart(Map<String,Object> param) throws Exception {
		
		return this.dunCaseDAO.getWeekChart(param);
	}
	
	public List<DunCaseChartTO> getMonthChart(Map<String,Object> param) throws Exception {
		
		return this.dunCaseDAO.getMonthChart(param);
	}
	
	public List<DeptCmpyTO> getCompanyList() throws Exception {
		return this.dunCaseDAO.getCompanyList();
	}

}
