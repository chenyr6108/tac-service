package com.brick.batchjob.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.exception.DaoException;
import com.brick.base.service.BaseService;
import com.brick.batchjob.dao.SupplerContributeDAO;
import com.brick.batchjob.to.SupplerContributeTo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;

public class SupplerContributeService extends BaseService {

	Log logger=LogFactory.getLog(SupplerContributeService.class);
			
	private SupplerContributeDAO supplerContributeDAO;

	public SupplerContributeDAO getSupplerContributeDAO() {
		return supplerContributeDAO;
	}

	public void setSupplerContributeDAO(SupplerContributeDAO supplerContributeDAO) {
		this.supplerContributeDAO = supplerContributeDAO;
	}
	@Transactional(rollbackFor=Exception.class)
	public void batchJob() throws Exception {
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 供应商贡献度 start  --------------------");
		}
		
		//获得供应商,设备款By供应商,利息,TR,合同号
		List<SupplerContributeTo> suplUnitPriceAccrualTRByContractResult=null;
		
		//获得一个合同有多个供应商的设备款总额
		List<SupplerContributeTo> suplUnitPriceSumByContractResult=null;
		
		//获得供应商净拨款金额
		List<SupplerContributeTo> payMoneyByContractResult=null;
		
		//获得供应商合同数
		List<SupplerContributeTo> leaseCountBySuplResult=null;
		
		//获得供应商设备数
		List<SupplerContributeTo> equipmentCountBySuplResult=null;
		
		//获得供应商的授信额度
		List<SupplerContributeTo> grantPriceResult=null;
		
		//获得逾期15天(含15天)以上的合同数
		List<SupplerContributeTo> dunCountResult=null;
		
		try {
			suplUnitPriceAccrualTRByContractResult=this.supplerContributeDAO.getSuplUnitPriceAccrualTRByContract();
			
			suplUnitPriceSumByContractResult=this.supplerContributeDAO.getSuplUnitPriceSumByContract();
			
			payMoneyByContractResult=this.supplerContributeDAO.getPayMoneyByContract();
			
			leaseCountBySuplResult=this.supplerContributeDAO.getLeaseCountBySupl();
			
			equipmentCountBySuplResult=this.supplerContributeDAO.getEquipmentCountBySupl();
			
			grantPriceResult=this.supplerContributeDAO.getGrantPrice();
			
			dunCountResult=this.supplerContributeDAO.getDunCountResult();
			
			Map<String,String> param=new HashMap<String,String>();
			for(int i=0;i<grantPriceResult.size();i++) {
				double usedGrantPrice=0.0;
				param.put("applyId",grantPriceResult.get(i).getSuplId());
				List<Map<String,Object>> result=(List<Map<String,Object>>)DataAccessor.query("beforeMakeContract.selectApplyAllCredit_HeZhun",param,DataAccessor.RS_TYPE.LIST);
				for(int j=0;result!=null&&j<result.size();j++) {
					if(result.get(j).get("RECT_ID")!=null) {
						result.get(j).put("ID",grantPriceResult.get(i).getSuplId());
						Map<String,Object> restGrantPrice=(Map<String,Object>)DataAccessor.query("beforeMakeContract.selectApplyContractOne_Jianshaoe",result.get(j),DataAccessor.RS_TYPE.MAP);
						if(restGrantPrice!=null) {
							if(restGrantPrice.get("SHOUXINJIANSHAOE")==null) {
								restGrantPrice.put("SHOUXINJIANSHAOE",0.0);
							}
							usedGrantPrice+=Double.parseDouble(restGrantPrice.get("SHOUXINJIANSHAOE")+"");
						}
					} else {
						result.get(j).put("credit_id",result.get(j).get("CREDIT_ID"));
						Map<String,Object> restGrantPrice1=(Map<String,Object>)DataAccessor.query("beforeMakeContract.selectApplySumIrrMonthAndLastPrice_THISCASE",result.get(j),DataAccessor.RS_TYPE.MAP);						
						if(restGrantPrice1!=null){
							usedGrantPrice+=Double.parseDouble(restGrantPrice1.get("SHENGYUBENJINYUEBENAN").toString());
						}
					}
				}
				grantPriceResult.get(i).setRestGrantPrice(grantPriceResult.get(i).getGrantPrice()-usedGrantPrice);
			}
			
			DecimalFormat df=new DecimalFormat("0.00");
			Map<String,Object> restMoneyPeriod=new HashMap<String,Object>();
			int payPeriod=0;
			int totalPeriod=0;
			for(int i=0;i<suplUnitPriceAccrualTRByContractResult.size();i++) {
				
				//设置一个合同有多个供应商的设备款比例
				for(int j=0;j<suplUnitPriceSumByContractResult.size();j++) {
					if(suplUnitPriceSumByContractResult.get(j).getLeaseCode().equals(suplUnitPriceAccrualTRByContractResult.get(i).getLeaseCode())) {
						suplUnitPriceAccrualTRByContractResult.get(i).
						setProrate(suplUnitPriceAccrualTRByContractResult.get(i).getUnitPrice()/suplUnitPriceSumByContractResult.get(j).getUnitPrice());
						break;
					}
				}
				
				//计算利息
				suplUnitPriceAccrualTRByContractResult.get(i).setAccrual(df.format(Double.valueOf(suplUnitPriceAccrualTRByContractResult.get(i).getAccrual())*
						suplUnitPriceAccrualTRByContractResult.get(i).getProrate()));
				
				//设置供应商的净拨款金额
				for(int j=0;j<payMoneyByContractResult.size();j++) {
					if(payMoneyByContractResult.get(j).getLeaseCode().equals(suplUnitPriceAccrualTRByContractResult.get(i).getLeaseCode())
							&&payMoneyByContractResult.get(j).getSuplId().equals(suplUnitPriceAccrualTRByContractResult.get(i).getSuplId())) {
						suplUnitPriceAccrualTRByContractResult.get(i).setPayMoney(Double.valueOf(df.format(suplUnitPriceAccrualTRByContractResult.get(i).getProrate()*
								payMoneyByContractResult.get(j).getPayMoney())));
						suplUnitPriceAccrualTRByContractResult.get(i).setCustId(payMoneyByContractResult.get(j).getCustId());
						suplUnitPriceAccrualTRByContractResult.get(i).setCreditId(payMoneyByContractResult.get(j).getCreditId());
						suplUnitPriceAccrualTRByContractResult.get(i).setCustName(payMoneyByContractResult.get(j).getCustName());
					}
				}
				
				//设置供应商合同数量
				for(int j=0;j<leaseCountBySuplResult.size();j++) {
					if(leaseCountBySuplResult.get(j).getSuplId().equals(suplUnitPriceAccrualTRByContractResult.get(i).getSuplId())) {
						suplUnitPriceAccrualTRByContractResult.get(i).setLeaseCount(leaseCountBySuplResult.get(j).getLeaseCount());
						break;
					}
				}
				
				//设置供应商设备数量
				for(int j=0;j<equipmentCountBySuplResult.size();j++) {
					if(equipmentCountBySuplResult.get(j).getSuplId().equals(suplUnitPriceAccrualTRByContractResult.get(i).getSuplId())) {
						suplUnitPriceAccrualTRByContractResult.get(i).setEquipmentCount(equipmentCountBySuplResult.get(j).getEquipmentCount());
						break;
					}
				}
				
				//设置供应商授权额度
				for(int j=0;j<grantPriceResult.size();j++) {
					if(grantPriceResult.get(j).getSuplId().equals(suplUnitPriceAccrualTRByContractResult.get(i).getSuplId())) {
						suplUnitPriceAccrualTRByContractResult.get(i).setGrantPrice(grantPriceResult.get(j).getGrantPrice());
						suplUnitPriceAccrualTRByContractResult.get(i).setRestGrantPrice(grantPriceResult.get(j).getRestGrantPrice());
						break;
					}
				}
				
				//加入逾期15天含15天的逾期个数 GROUP BY 供应商
				for(int j=0;j<dunCountResult.size();j++) {
					if(dunCountResult.get(j).getRecpId().equals(suplUnitPriceAccrualTRByContractResult.get(i).getRecpId())) {
						suplUnitPriceAccrualTRByContractResult.get(i).setDunCountByLease(dunCountResult.get(j).getDunCountByLease());
						suplUnitPriceAccrualTRByContractResult.get(i).setDunCountBySupl(1);
					}
				}
				//加入剩余本金
				restMoneyPeriod.put("RECT_ID",suplUnitPriceAccrualTRByContractResult.get(i).getRectId());
				restMoneyPeriod=(Map<String,Object>)DataAccessor.query("applyCompanyManage.findShengyuBenjinContractId",restMoneyPeriod,DataAccessor.RS_TYPE.MAP);
				suplUnitPriceAccrualTRByContractResult.get(i).setRestMoney(Double.valueOf(restMoneyPeriod.get("SHENGYUBENJIN")+""));
			
				//加入剩余期数
				restMoneyPeriod.put("RECP_ID",suplUnitPriceAccrualTRByContractResult.get(i).getRecpId());
				restMoneyPeriod.put("RECT_ID",suplUnitPriceAccrualTRByContractResult.get(i).getRectId());
				restMoneyPeriod.put("zujin","租金");
				payPeriod=(Integer)DataAccessor.query("businessReport.getPayPeriod",restMoneyPeriod,DataAccessor.RS_TYPE.OBJECT);
				
				Object result=DataAccessor.query("businessReport.getTotalPeriod",restMoneyPeriod,DataAccessor.RS_TYPE.OBJECT);
				
				if(result==null) {
					totalPeriod=0;
				} else {
					totalPeriod=(Integer)result;
				}
						/*(Map<String,Object>)DataAccessor.query("applyCompanyManage.findContractNoPayByContractId",restMoneyPeriod,DataAccessor.RS_TYPE.MAP);*/
				suplUnitPriceAccrualTRByContractResult.get(i).setRestPeriod((totalPeriod-payPeriod)+"");
			}
			
			
			//插入数据
			for(int i=0;i<suplUnitPriceAccrualTRByContractResult.size();i++) {
				Thread.sleep(1);//防止主键重复
				suplUnitPriceAccrualTRByContractResult.get(i).setSupplerContributeId(String.valueOf(System.currentTimeMillis()));
				this.getSupplerContributeDAO().insertSuplContribute(suplUnitPriceAccrualTRByContractResult.get(i));
			}
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("batch job for 供应商贡献度 end  --------------------");
		}
	}
	
	public List<SupplerContributeTo> getDetailBySuplId(Context context) throws DaoException {
		
		List<SupplerContributeTo> resultList=null;
		
		resultList=this.supplerContributeDAO.getDetailBySuplId(context.contextMap);
		
		if(resultList==null) {
			resultList=new ArrayList<SupplerContributeTo>();
		}
		return resultList;
	}
	
	public static List<SupplerContributeTo> getSuplContributeTotal() {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("ORDER_TYPE","ROUND(SUM(PAY_MONEY),0)");
		param.put("SORT","DESC");
		List<SupplerContributeTo> resultList=null;
		
		try {
			resultList=(List<SupplerContributeTo>)DataAccessor.query("businessReport.getSuplContributeTotal",param,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public static List<SupplerContributeTo> getDetailBySuplId(String suplId) {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("TYPE","供应商保证");
		param.put("suplId",suplId);
		List<SupplerContributeTo> resultList=null;
		
		try {
			resultList=(List<SupplerContributeTo>)DataAccessor.query("businessReport.getDetailBySuplId",param,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
}
