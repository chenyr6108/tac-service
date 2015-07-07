package com.brick.job.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.to.ReportDateTo;
import com.brick.base.util.ReportDateUtil;
import com.brick.decompose.service.DecomposeManager;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class FinanceDecomposeReport extends AService  {
	Log logger = LogFactory.getLog(DecomposeManager.class);

	public static final Logger log = Logger.getLogger(DecomposeManager.class);


	//Add by Michael 2012-3-15 每日销账报表
	@SuppressWarnings("unchecked")
	public void dailyFinaDecomposeReportDetail()
	{
		SqlMapClient sqlMapper = DataAccessor.getSession();
		
		Map context= new HashMap();
		String ficb_type="待分解来款";		
		context.put("ficb_type", ficb_type);
		
		Map outputMap = new HashMap();

		List finaTodayFinaIncome=new ArrayList();//现金销账
		List finaLastDecompose=new ArrayList();//暂收款销账
		List finaLastDynamicDecompose=new ArrayList();//暂收款余额变动表	

		sqlMapper=DataAccessor.getSession();
		Map currencyDecompose;
		Map tempDecompose;
		Map dynamicDecompose;
		try {
			System.out.println("--------  销账日报表 开始------------------");
			sqlMapper.startTransaction();
			finaTodayFinaIncome = (List) DataAccessor.query("decompose.getDailyCurrencyDecomposeRpt", context, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < finaTodayFinaIncome.size(); i++) {
				currencyDecompose = (Map) finaTodayFinaIncome.get(i);
				sqlMapper.insert("financeDecomposeReport.insertCurrencyDecomposeReport", currencyDecompose);
			}
			
			finaLastDecompose = (List) DataAccessor.query("decompose.getLastDecomposeRpt", context, DataAccessor.RS_TYPE.LIST);
			
			for (int i = 0; i < finaLastDecompose.size(); i++) {
				tempDecompose = (Map) finaLastDecompose.get(i);
				sqlMapper.insert("financeDecomposeReport.insertTempDecomposeReport", tempDecompose);
			}
			
			finaLastDynamicDecompose=(List) DataAccessor.query("decompose.getLastDynamicDecomposeRpt", context, DataAccessor.RS_TYPE.LIST);
			
			for (int i = 0; i < finaLastDynamicDecompose.size(); i++) {
				dynamicDecompose = (Map) finaLastDynamicDecompose.get(i);
				sqlMapper.insert("financeDecomposeReport.insertDynamicDecomposeReport", dynamicDecompose);
			}
			
			sqlMapper.commitTransaction();
			System.out.println("--------  销账日报表 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  销账日报表 失败 ------------------");
		}finally{
			try {
				sqlMapper.endTransaction();
				System.out.println("--------  销账日报表 结束 ------------------");
			} catch (SQLException e) {
				logger.debug(e);
			}
		}

	}	

	//Add by Michael 2012-3-15 每日销账报表
	public void dailyFinaDecomposeReportDetailByValueAdd()
	{
		Map context= new HashMap();
		String ficb_item="租金";		
		context.put("ficb_item", ficb_item);
		
		Map outputMap = new HashMap();

		List finaTodayFinaIncome=new ArrayList();//现金销账
		List finaLastDecompose=new ArrayList();//暂收款销账
		List finaLastDynamicDecompose=new ArrayList();//暂收款余额变动表	

		SqlMapClient sqlMapper = DataAccessor.getSession();
		Map currencyDecompose;
		Map tempDecompose;
		Map dynamicDecompose;
		double real_price=0.0d;
		int recp_id=0;
		int period_num=0;
		try {
			System.out.println("--------  整理销账日报表计算增值税 开始------------------");
			sqlMapper.startTransaction();
			finaTodayFinaIncome = (List) DataAccessor.query("financeDecomposeReport.queryDecomposeByDayForValueAdd", context, DataAccessor.RS_TYPE.LIST);
			for (int i = 0; i < finaTodayFinaIncome.size(); i++) {
				currencyDecompose = (Map) finaTodayFinaIncome.get(i);
				//如果是增值税税费方案的话，则要将租金与增值税切开来
				if("2".equals(currencyDecompose.get("TAX_PLAN_CODE"))){

					//判断销账来款与已销账金额进行比较，如果销账来款比已销账金额小，说明之前有进行过销账
					if(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))<DataUtil.doubleUtil(currencyDecompose.get("REDUCE_OWN_PRICE"))){
						//当条件成立时说明，此次此案件同一期租金有多次分解,说明今天之前有分解过
						if(recp_id==DataUtil.intUtil(currencyDecompose.get("RECP_ID")) && period_num==DataUtil.intUtil(currencyDecompose.get("RECD_PERIOD"))){
							//（金额差表示是之前销账的金额）
							Double diff_price=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REDUCE_OWN_PRICE"))).subtract(new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))).add(new BigDecimal(real_price))).doubleValue();
							//算出  增值税 与之前 销账金额进行 相减 
							Double unDecomposeValueAdd=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX"))).subtract(new BigDecimal(diff_price)).doubleValue();
							//如果 相减 金额 小于 0 则说明 之前 增值税已销账完成，此次销账的是租金，如果相减 金额 大于 0 则说明上次销账 增值税没有销账完，此次有销账增值税
							if(unDecomposeValueAdd>=0){
								//如果此次销账来款比剩余增值税大，则说明此次既有销账增值税，又有销账租金，否则此次只有销账增值税
								if(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))<=unDecomposeValueAdd){

									sqlMapper.update("financeDecomposeReport.updateDecomposeDailyFicbItem", currencyDecompose);
								}else{
									//否则此次销账的既有增值税又有租金，先销增值税，剩余的钱算租金
									Double curRealIrrMonthPrice=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))).subtract(new BigDecimal(unDecomposeValueAdd)).doubleValue();
									currencyDecompose.put("REAL_PRICE", curRealIrrMonthPrice);
									sqlMapper.update("financeDecomposeReport.addDecomposeDailyForValueAdd", currencyDecompose);
									currencyDecompose.put("FICB_ITEM", "增值税");
									currencyDecompose.put("REAL_PRICE", unDecomposeValueAdd);
									sqlMapper.insert("financeDecomposeReport.insertCurrencyDecomposeReportForValueAdd", currencyDecompose);
								}
							}
							//将上次的来款金额 和本次的来款金额相加
							real_price=new BigDecimal(real_price).add(new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE")))).doubleValue();
						}else{
							//（金额差表示是之前销账的金额）
							Double diff_price=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REDUCE_OWN_PRICE"))).subtract(new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE")))).doubleValue();
							//算出  增值税 与之前 销账金额进行 相减 
							Double unDecomposeValueAdd=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX"))).subtract(new BigDecimal(diff_price)).doubleValue();
							//如果 相减 金额 小于 0 则说明 之前 增值税已销账完成，此次销账的是租金，如果相减 金额 大于 0 则说明上次销账 增值税没有销账完，此次有销账增值税
							if(unDecomposeValueAdd>=0){
								//如果此次销账来款比剩余增值税大，则说明此次既有销账增值税，又有销账租金，否则此次只有销账增值税
								if(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))<=unDecomposeValueAdd){

									sqlMapper.update("financeDecomposeReport.updateDecomposeDailyFicbItem", currencyDecompose);
								}else if(unDecomposeValueAdd==0){ 
									//否则此次销账的既有增值税又有租金，先销增值税，剩余的钱算租金
									Double curRealIrrMonthPrice=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))).subtract(new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX")))).doubleValue();
									currencyDecompose.put("REAL_PRICE", curRealIrrMonthPrice);
									sqlMapper.update("financeDecomposeReport.addDecomposeDailyForValueAdd", currencyDecompose);
									currencyDecompose.put("FICB_ITEM", "增值税");
									currencyDecompose.put("REAL_PRICE", currencyDecompose.get("VALUE_ADDED_TAX"));
									sqlMapper.insert("financeDecomposeReport.insertCurrencyDecomposeReportForValueAdd", currencyDecompose);
								}else{
									//否则此次销账的既有增值税又有租金，先销增值税，剩余的钱算租金
									Double curRealIrrMonthPrice=new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))).subtract(new BigDecimal(unDecomposeValueAdd)).doubleValue();
									currencyDecompose.put("REAL_PRICE", curRealIrrMonthPrice);
									sqlMapper.update("financeDecomposeReport.addDecomposeDailyForValueAdd", currencyDecompose);
									currencyDecompose.put("FICB_ITEM", "增值税");
									currencyDecompose.put("REAL_PRICE", unDecomposeValueAdd);
									sqlMapper.insert("financeDecomposeReport.insertCurrencyDecomposeReportForValueAdd", currencyDecompose);
								}
							}
						}
					}else{   //否则 此次已销账的金额与来款销账金额一致，说明就是此次的销账
						//要判断此次的销账金额是否比增值税大，如果大的话，说明即有销账租金又有销账增值税
						if(DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"))>=DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX"))){
							Double real_priceDouble=DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"));
							//如果来款销账金额比增值税大，说明增值税已经全部销完，剩下的钱用来销账租金
							currencyDecompose.put("FICB_ITEM", "增值税");
							currencyDecompose.put("REAL_PRICE", DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX")));
							sqlMapper.insert("financeDecomposeReport.insertCurrencyDecomposeReportForValueAdd", currencyDecompose);
							Double curRealIrrMonthPrice=new BigDecimal(real_priceDouble).subtract(new BigDecimal(DataUtil.doubleUtil(currencyDecompose.get("VALUE_ADDED_TAX")))).doubleValue();
							currencyDecompose.put("REAL_PRICE", curRealIrrMonthPrice);
							
							sqlMapper.update("financeDecomposeReport.addDecomposeDailyForValueAdd", currencyDecompose);
						}else{   //否则说明只销账了增值税
							sqlMapper.update("financeDecomposeReport.updateDecomposeDailyFicbItem", currencyDecompose);
						}
						
					}
				}
				//此处作业用来判断是否一个案件同一期同时租金有多次分解
				if(recp_id!=DataUtil.intUtil(currencyDecompose.get("RECP_ID")) && period_num!=DataUtil.intUtil(currencyDecompose.get("RECD_PERIOD"))){
					recp_id=DataUtil.intUtil(currencyDecompose.get("RECP_ID"));
					period_num=DataUtil.intUtil(currencyDecompose.get("RECD_PERIOD"));
					real_price=DataUtil.doubleUtil(currencyDecompose.get("REAL_PRICE"));
				}
			}
			
			sqlMapper.commitTransaction();
			System.out.println("--------  整理销账日报表计算增值税 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  整理销账日报表计算增值税 失败 ------------------");
		}finally{
			try {
				sqlMapper.endTransaction();
				System.out.println("--------  整理销账日报表计算增值税 结束 ------------------");
			} catch (SQLException e) {
				logger.debug(e);
			}
		}

	}	

	
	//Add by Michael 2012-4-26 备份营业税报表
	@SuppressWarnings("unchecked")
	public void monthBusinessTaxReport()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		Map context= new HashMap();
		context.put("startDate", new Date()) ;
		List listMonthBusinessTax=new ArrayList();
		Map tempMap;
		System.out.println("--------  备份营业税报表 开始------------------");
		try {
			listMonthBusinessTax=(List) DataAccessor.query("priceReport.queryBusinessTax", context, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<listMonthBusinessTax.size();i++){
				tempMap=(Map) listMonthBusinessTax.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertMonthBusinessTaxReport", tempMap);;
			}
			System.out.println("--------  备份营业税报表 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  备份营业税报表 失败 ------------------");
		}

	}	

	
	//Add by Michael 2012-4-26 备份每月开票资料
	@SuppressWarnings("unchecked")
	public void openInvoiceByMonth()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		Map context= new HashMap();
		context.put("startDate", sf.format(new Date())) ;
		List listpenInvoiceByMonth=new ArrayList();
		Map tempMap;
		System.out.println("--------  备份每月开票资料 开始------------------");
		try {
			DataAccessor.getSession().startTransaction();
			listpenInvoiceByMonth=(List) DataAccessor.query("priceReport.exportOpenInvoiceByMonth", context, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<listpenInvoiceByMonth.size();i++){
				tempMap=(Map) listpenInvoiceByMonth.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertMonthOpenInvoice", tempMap);;
			}
			DataAccessor.getSession().commitTransaction();
			System.out.println("--------  备份每月开票资料 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  备份每月开票资料 失败 ------------------");
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
				System.out.println("--------  备份每月开票资料 结束 ------------------");
			} catch (SQLException e) {
				System.out.println("--------  备份每月开票资料 关闭事物失败 ------------------");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}
	
	//Add by Michael 2012-4-27 查询要发送锁码的信息
	public void getTestAllLockMsgAndMail(Context context1){
		Map context= new HashMap();
		context.put("zujin", "租金");
		context.put("daifenjielaikuan", "待分解来款");
		List listDailyLockManageSendInfo=new ArrayList();
		Map tempMap;
		System.out.println("--------  查询要发送锁码的信息 开始------------------");
		try {
			listDailyLockManageSendInfo=(List) DataAccessor.query("financeDecomposeReport.queryAllLockMsgAndMail", context, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<listDailyLockManageSendInfo.size();i++){
				tempMap=(Map) listDailyLockManageSendInfo.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertDailyLockManageSendInfo", tempMap);;
			}
			System.out.println("--------  查询要发送锁码的信息 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("-------- 查询要发送锁码的信息 失败 ------------------");
		}		
	}

	/*
	 * Add by Michael 2012-5-3 整理财务月份的销账数据
	 * 将每天的销账日报表数据汇总到财务月份
	 */
	
	@SuppressWarnings("unchecked")
	public void arrangementDecomposeByMonth()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		Map context= new HashMap();
		context.put("startDate", sf.format(new Date())) ;
		List listDailyDecompose=new ArrayList();
		Map tempMap;
		Map secondWD;
		Map secondLastMonthWD;
		Map firstMonthWD;
		Map runTime;
		//每个月的第二个工作日开始跑报表
		try {
			//获取当前月份的第二个工作日
			DataAccessor.getSession().startTransaction();
			//secondWD=(Map) DataAccessor.query("financeDecomposeReport.getSecondWDByMonth", context, DataAccessor.RS_TYPE.MAP);
			//按照结账周期来跑,每个结账周期endTime以后的第二个工作日
			runTime=(Map) DataAccessor.query("financeDecomposeReport.getRunTime1", context, DataAccessor.RS_TYPE.MAP);
			if (runTime!=null){				
				System.out.println("--------  整理财务月份的销账数据  开始  ------------------");
				/*secondLastMonthWD=(Map) DataAccessor.query("financeDecomposeReport.getSecondWDByLastMonth", context, DataAccessor.RS_TYPE.MAP);
				firstMonthWD=(Map) DataAccessor.query("financeDecomposeReport.getFirstWDByMonth", context, DataAccessor.RS_TYPE.MAP);
				context.put("secondLastMonthWD", secondLastMonthWD.get("DATE"));
				context.put("firstMonthWD", firstMonthWD.get("DATE"));*/
				
				context.put("startTime",(String)DataAccessor.query("financeDecomposeReport.getStartDate",runTime,DataAccessor.RS_TYPE.OBJECT));
				context.put("endTime",(String)DataAccessor.query("financeDecomposeReport.getEndDate",runTime,DataAccessor.RS_TYPE.OBJECT));
				String date=runTime.get("YEAR").toString()+"-"+runTime.get("MONTH").toString()+"-01";
				//获取财务月份的每日销账数据
				listDailyDecompose=(List) DataAccessor.query("financeDecomposeReport.getDailyDecomposeByMonth", context, DataAccessor.RS_TYPE.LIST);
				DataAccessor.getSession().startBatch();
				for(int i=0;i<listDailyDecompose.size();i++){
					tempMap=(Map) listDailyDecompose.get(i);
					tempMap.put("date",date);
					DataAccessor.getSession().insert("financeDecomposeReport.insertDecomposeByMonthByDaily", tempMap);;
				}
				DataAccessor.getSession().executeBatch();
			}
			DataAccessor.getSession().commitTransaction();
			System.out.println("--------  整理财务月份的销账数据  成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  整理财务月份的销账数据   失败 ------------------");
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
				System.out.println("--------  整理财务月份的销账数据 结束 ------------------");
			} catch (SQLException e) {
				System.out.println("--------  整理财务月份的销账数据 关闭事物失败 ------------------");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}

	
	/*
	 * Add by Michael 2012-5-3 备份每月的财务、税务利息收入明细
	 */
	
	@SuppressWarnings("unchecked")
	public void getInterestDetailByMonth()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		Map context= new HashMap();
		context.put("start_date", sf.format(new Date())) ;
		context.put("startDate", sf.format(new Date())) ;
		List listInterest=new ArrayList();
		Map tempMap;
		Map runTime;
		try {
			runTime=(Map) DataAccessor.query("financeDecomposeReport.getRunTime1", context, DataAccessor.RS_TYPE.MAP);
			if (runTime!=null){	
				DataAccessor.getSession().startTransaction();
				System.out.println("--------  备份每月的财务、税务利息收入明细  开始  ------------------");
				context.put("year",runTime.get("YEAR"));
				context.put("month",runTime.get("MONTH"));
				listInterest=(List) DataAccessor.query("interestDetail.queryDetail", context, DataAccessor.RS_TYPE.LIST);
				DataAccessor.getSession().startBatch();
				for(int i=0;i<listInterest.size();i++){
					tempMap=(Map) listInterest.get(i);
					tempMap.put("TIME",runTime.get("YEAR").toString()+"-"+runTime.get("MONTH").toString());
					DataAccessor.getSession().insert("financeDecomposeReport.insertInterestDetailByMonth", tempMap);;
				}
				DataAccessor.getSession().executeBatch();
				DataAccessor.getSession().commitTransaction();
			}
			System.out.println("--------  备份每月的财务、税务利息收入明细  成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  备份每月的财务、税务利息收入明细   失败 ------------------");
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
				System.out.println("--------  备份每月的财务、税务利息收入明细  结束 ------------------");
			} catch (SQLException e) {
				System.out.println("--------  备份每月的财务、税务利息收入明细  关闭事物失败 ------------------");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}
	
	
	//Add by Michael 2012-4-27 查询要发送锁码的信息
	public void getAllLockMsgAndMail(){
		Map context= new HashMap();
		context.put("zujin", "租金");
		context.put("daifenjielaikuan", "待分解来款");
		List listDailyLockManageSendInfo=new ArrayList();
		Map tempMap;
		System.out.println("--------  查询要发送锁码的信息 开始------------------");
		try {
			listDailyLockManageSendInfo=(List) DataAccessor.query("financeDecomposeReport.queryAllLockMsgAndMail", context, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<listDailyLockManageSendInfo.size();i++){
				tempMap=(Map) listDailyLockManageSendInfo.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertDailyLockManageSendInfo", tempMap);;
			}
			System.out.println("--------  查询要发送锁码的信息 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("-------- 查询要发送锁码的信息 失败 ------------------");
		}		
	}
	
	//发送间接锁码Email
//	public void sendDirectLockEmail(){
//		Map context= new HashMap();
//
//		List listRenterEmail=new ArrayList();
//		List listLockMsg=new ArrayList();
//		Map tempMap;
//		MailTest eMail=new MailTest();
//		System.out.println("--------  发送间接锁码的信息 开始------------------");
//		try {
//			listRenterEmail=(List) DataAccessor.query("financeDecomposeReport.getDirectLockRenterEmail", context, DataAccessor.RS_TYPE.LIST);
//			for(int i=0;i<listRenterEmail.size();i++){
//				tempMap=(Map) listRenterEmail.get(i);
//				listLockMsg=(List) DataAccessor.query("financeDecomposeReport.getDirectLockRenterEmailByEmail", context, DataAccessor.RS_TYPE.LIST);
//				context.put("TOEMAIL", tempMap.get("RENTER_EMAIL"));
//				context.put("LISTLOCKMSG", listLockMsg);
//				eMail.testSetAttachMail(context);
//			}
//			System.out.println("--------  发送间接锁码的信息 成功 ------------------");
//		}catch(Exception e){
//			e.printStackTrace();
//			LogPrint.getLogStackTrace(e, logger);
//			System.out.println("-------- 发送间接锁码的信息 失败 ------------------");
//		}		
//	}
	
	
	//Add by Michael 2012-7-3 查询要发送直接锁码的信息
	public void getDirectLockMsgAndMail(){
		Map context= new HashMap();
		context.put("zujin", "租金");
		context.put("daifenjielaikuan", "待分解来款");
		List listDailyLockManageSendInfo=new ArrayList();
		Map tempMap;
		System.out.println("--------  查询要发送直接锁码的信息 开始------------------");
		try {
			listDailyLockManageSendInfo=(List) DataAccessor.query("financeDecomposeReport.queryDirectLockMsgAndMail", context, DataAccessor.RS_TYPE.LIST);
			for(int i=0;i<listDailyLockManageSendInfo.size();i++){
				tempMap=(Map) listDailyLockManageSendInfo.get(i);
				DataAccessor.getSession().insert("financeDecomposeReport.insertDailyDirectLockManageSendInfo", tempMap);;
			}
			System.out.println("--------  查询要发送直接锁码的信息 成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("-------- 查询要发送直接锁码的信息 失败 ------------------");
		}		
	}
	
	// Add by Michael 2012 08-13 将缺发票的合同保存起来
	@SuppressWarnings("unchecked")
	public void getInsertFileLossInvoice()
	{
		try {
			DataAccessor.getSession().startTransaction();
			DataAccessor.getSession().delete("rentFile.deleteFileLossInvoice");
			DataAccessor.getSession().insert("rentFile.insertFileLossInvoice");
			DataAccessor.getSession().commitTransaction();
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}
	
	/*
	 * Add by Michael 2012-8-24  整理每月长期应收款报表数据（税务）
	 * 整理每月长期应收款报表数据（税务）
	 */
	
	@SuppressWarnings("unchecked")
	public void arrangementFinanceRealPriceByMonth()
	{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
		Map context= new HashMap();
		context.put("startDate", sf.format(new Date())) ;
		List listRealPriceByMonth=new ArrayList();
		Map tempMap;
		Map secondWD;
		Map secondLastMonthWD;
		Map firstMonthWD;
		Map runTime;
		//每个月的第二个工作日开始跑报表
		try {
			//获取当前月份的第二个工作日
			DataAccessor.getSession().startTransaction();
			secondWD=(Map) DataAccessor.query("financeDecomposeReport.getSecondWDByMonth", context, DataAccessor.RS_TYPE.MAP);

			runTime=(Map) DataAccessor.query("financeDecomposeReport.getRunTime1", context, DataAccessor.RS_TYPE.MAP);
			
			if (runTime!=null){	
				int year=Integer.valueOf(runTime.get("YEAR")+"");
				int month=Integer.valueOf(runTime.get("MONTH")+"");
				if(month==12) {
					year=year+1;
					month=1;
				} else {
					month=month+1;
				}
				
				ReportDateTo to=ReportDateUtil.getDateByYearAndMonth(year,month);
				context.put("nextStartTime",to.getBeginTime());
				context.put("nextEndTime",to.getEndTime());
				System.out.println("--------  整理财务月份的长期应收款  开始  ------------------");
				//secondLastMonthWD=(Map) DataAccessor.query("financeDecomposeReport.getSecondWDByLastMonth", context, DataAccessor.RS_TYPE.MAP);
				//firstMonthWD=(Map) DataAccessor.query("financeDecomposeReport.getFirstWDByMonth", context, DataAccessor.RS_TYPE.MAP);
				//context.put("secondLastMonthWD", secondLastMonthWD.get("DATE"));
				//context.put("firstMonthWD", firstMonthWD.get("DATE"));
				context.put("year",runTime.get("YEAR"));
				context.put("month",runTime.get("MONTH"));
				
				context.put("startTime",runTime.get("BEGINTIME"));
				context.put("endTime",runTime.get("ENDTIME"));
				
				//获取财务月份的每日销账数据
				listRealPriceByMonth=(List) DataAccessor.query("financeDecomposeReport.getFinanceRealPriceByMonth", context, DataAccessor.RS_TYPE.LIST);
			}
			DataAccessor.getSession().commitTransaction();
			System.out.println("--------  整理财务月份的长期应收款  成功 ------------------");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			System.out.println("--------  整理财务月份的长期应收款   失败 ------------------");
		}finally {
			try {
				DataAccessor.getSession().endTransaction();
				System.out.println("--------  整理财务月份的长期应收款 结束 ------------------");
			} catch (SQLException e) {
				System.out.println("--------  整理财务月份的长期应收款 关闭事物失败 ------------------");
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}
	
	
	//Add by Michael 2012-4-26 备份留购款营业税报表
		@SuppressWarnings("unchecked")
		public void monthStayPriceTaxReport()
		{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
			Map context= new HashMap();
			context.put("startDate", new Date()) ;
			List listMonthBusinessTax=new ArrayList();
			Map tempMap;
			System.out.println("--------  备份留购款营业税报表 开始------------------");
			try {
				listMonthBusinessTax=(List) DataAccessor.query("priceReport.queryStayBuyPriceTaxForJob", context, DataAccessor.RS_TYPE.LIST);
				for(int i=0;i<listMonthBusinessTax.size();i++){
					tempMap=(Map) listMonthBusinessTax.get(i);
					DataAccessor.getSession().insert("financeDecomposeReport.insertMonthStayPriceTaxReport", tempMap);;
				}
				System.out.println("--------  备份留购款营业税报表 成功 ------------------");
			}catch(Exception e){
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				System.out.println("--------  备份留购款营业税报表 失败 ------------------");
			}
		}	
		
		//整理 保险费余额变动表
		public void queryInsuranceDynamicForJob()
		{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
			Map context= new HashMap();
			context.put("startDate", sf.format(new Date())) ;
			List listRealPriceByMonth=new ArrayList();
			Map tempMap;
			try {
				DataAccessor.getSession().startTransaction();

				System.out.println("--------  保险费余额变动表  开始  ------------------");
				//获取保险费余额变动表
				listRealPriceByMonth=(List) DataAccessor.query("financeDecomposeReport.queryInsuranceDynamicForJob", context, DataAccessor.RS_TYPE.LIST);
				DataAccessor.getSession().commitTransaction();
				System.out.println("--------  保险费余额变动表  成功 ------------------");
			}catch(Exception e){
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				System.out.println("--------  保险费余额变动表   失败 ------------------");
			}finally {
				try {
					DataAccessor.getSession().endTransaction();
					System.out.println("--------  保险费余额变动表 结束 ------------------");
				} catch (SQLException e) {
					System.out.println("--------  保险费余额变动表 关闭事物失败 ------------------");
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}

		}
		
		/**
		 * @author michael
		 * For 保证金B期末抵充处理
		 * add by Michael 2013-01-07	
		 */
		public void prcDecomposePledgeB()
		{
			//第一步查出所有保证金B的有效支付表ID
			List decomposePledgeBRecpIDList=null;
			Map tempMap;
			Map tempBillMap;
			List rentDetaiList=null;
			Map tempFinanceIncome;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
	
			SqlMapClient sqlMapper = DataAccessor.getSession();
			System.out.println("--------  保证金B系统自动销账 开始 ------------------");
			try {
				decomposePledgeBRecpIDList=(List) DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBRecpID", null, DataAccessor.RS_TYPE.LIST);
				for(int i=0;i<decomposePledgeBRecpIDList.size();i++){
					//查询出最后抵充期数
					tempMap=(Map) DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBLastPeriod", ((Map)decomposePledgeBRecpIDList.get(i)), DataAccessor.RS_TYPE.MAP);
					tempMap.put("RECP_ID", ((Map)decomposePledgeBRecpIDList.get(i)).get("RECP_ID"));
					rentDetaiList=(List) DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBDetail", tempMap, DataAccessor.RS_TYPE.LIST);
					for(int j=0;j<rentDetaiList.size();j++){

						sqlMapper.startTransaction();
						//如果此案的最后期数没有还款，且支付日期在今天之前，则要进行销账作业
						if(DataUtil.doubleUtil(((Map)decomposePledgeBRecpIDList.get(i)).get("REAL_PRICE")) >= DataUtil.doubleUtil(((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE")) && DataUtil.doubleUtil(((Map)rentDetaiList.get(j)).get("REDUCE_OWN_PRICE"))==0 && DateUtil.strToDate(String.valueOf(((Map)rentDetaiList.get(j)).get("PAY_DATE")), "yyyy-MM-dd").getTime()<=sf.parse(sf.format(new Date())).getTime()){
							//插入一条冲回分解单
							tempBillMap=(Map) DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBCollectoinBill", ((Map)decomposePledgeBRecpIDList.get(i)), DataAccessor.RS_TYPE.MAP);
							tempBillMap.put("REAL_PRICE", 0-(DataUtil.doubleUtil(((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE"))));
							tempBillMap.put("FICB_TYPE", "1");
							tempBillMap.put("IS_PLEDGE_B", "1");
							sqlMapper.insert("financeDecomposeReport.insertCollectionBillByPledgeB", tempBillMap);
							
							//产生待分解来款
							tempBillMap.put("REAL_PRICE", (DataUtil.doubleUtil(((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE"))));
							tempBillMap.put("FICB_ITEM", "待分解来款");
							sqlMapper.insert("financeDecomposeReport.insertCollectionBillByPledgeB", tempBillMap);
							
							//将产生的待分解来款插入到来款表中
							tempFinanceIncome=(Map) DataAccessor.query("financeDecomposeReport.queryDecomposePledgeBFinanceIncome", ((Map)decomposePledgeBRecpIDList.get(i)), DataAccessor.RS_TYPE.MAP);
							tempFinanceIncome.put("INCOME_MONEY", (DataUtil.doubleUtil(((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE"))));
							
							tempFinanceIncome.put("PAYMENT_MONEY",0);
							tempFinanceIncome.put("LEFT_MONEY",0);
							tempFinanceIncome.put("COMMISSION_MONEY",0);
							
							Long fiin_id=(Long) sqlMapper.insert("financeDecomposeReport.insertFinanceIncome", tempFinanceIncome);
							tempBillMap.put("FIIN_ID", fiin_id);
							
							//插入租金分解单
							tempBillMap.put("PAY_DATE", ((Map)rentDetaiList.get(j)).get("PAY_DATE"));
							tempBillMap.put("RECD_PERIOD", ((Map)rentDetaiList.get(j)).get("PERIOD_NUM"));
							tempBillMap.put("SHOULD_PRICE", ((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE"));
							tempBillMap.put("REAL_PRICE", ((Map)rentDetaiList.get(j)).get("IRR_MONTH_PRICE"));
							tempBillMap.put("FICB_ITEM", "租金");
							tempBillMap.put("FICB_TYPE", "0");
							tempBillMap.put("RECD_TYPE", "0");
							tempBillMap.put("IS_PLEDGE_B", "0");
							sqlMapper.insert("financeDecomposeReport.insertCollectionBillByPledgeB", tempBillMap);
							
							//更新支付表的还款金额
							sqlMapper.update("financeDecomposeReport.updateRentCollectionReduce", (Map)rentDetaiList.get(j));
							
							//插入Log
							sqlMapper.insert("financeDecomposeReport.insertPledgeBDecomposeLog", (Map)rentDetaiList.get(j));
							
						}
						sqlMapper.commitTransaction();
					}
				}

			}catch(Exception e){
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				System.out.println("--------  保证金B系统自动销账   失败 ------------------");
			}finally {
				try {
					sqlMapper.endTransaction();
					System.out.println("--------  保证金B系统自动销账 结束 ------------------");
				} catch (SQLException e) {
					System.out.println("--------  保证金B系统自动销账 关闭事物失败 ------------------");
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
		
		//调整保险费 尾差
		@Transactional(rollbackFor = Exception.class)
		public void adjustTailInsuranceDynamicDetailForJob() throws Exception
		{
			Map context= new HashMap();
			List listRecpID=new ArrayList();

			//获取保险费余额变动表
			listRecpID=(List) DataAccessor.query("financeDecomposeReport.selectRentRecpIDByFinanceDate", context, DataAccessor.RS_TYPE.LIST);
			Map tempMap = new HashMap();
			Map tempMap2 =new HashMap();

			for(int j=0;j<listRecpID.size();j++){
				tempMap=(Map) listRecpID.get(j);
				//DataAccessor.query("financeDecomposeReport.deaulInsuranceDynamicDetailForJob", tempMap, DataAccessor.RS_TYPE.LIST);
				List insuranceDetailList=(List) DataAccessor.query("financeDecomposeReport.selectRentInsuranceDetailByRecpID", tempMap, DataAccessor.RS_TYPE.LIST);
				
				double totalInsurance=0.0;
				double insurance=0.0;
				double monthInsurance=0.0;
				
				for(int i=0;i<insuranceDetailList.size();i++){
					tempMap2=(Map) insuranceDetailList.get(i);
					
					if(i==0){
						insurance=DataUtil.doubleUtil(tempMap2.get("INSURANCE"));
					}
					
					if(i==insuranceDetailList.size()-1){
						monthInsurance= new BigDecimal(insurance).subtract(new BigDecimal(totalInsurance)).doubleValue();
					
						tempMap2.put("MONTHINSURANCE", monthInsurance);
						DataAccessor.execute("financeDecomposeReport.updateRentInsuranceDetailByRecpID", tempMap2,DataAccessor.OPERATION_TYPE.UPDATE);
					}
					
					totalInsurance=new BigDecimal(DataUtil.doubleUtil(tempMap2.get("MONTHINSURANCE"))).add(new BigDecimal(totalInsurance)).doubleValue();
				}
			}
		}
		
		//批量生成保险费明细表
		@Transactional(rollbackFor = Exception.class)
		public void batchInsuranceDynamicDetailForJob()throws Exception
		{
			Map context= new HashMap();
			List listRecpID=new ArrayList();

			//获取保险费余额变动表
			listRecpID=(List) DataAccessor.query("financeDecomposeReport.selectRentRecpIDByFinanceDate", context, DataAccessor.RS_TYPE.LIST);
			Map tempMap = new HashMap();
			Map tempMap2 =new HashMap();
			for(int j=0;j<listRecpID.size();j++){
				tempMap=(Map) listRecpID.get(j);
				DataAccessor.query("financeDecomposeReport.deaulInsuranceDynamicDetailForJob", tempMap, DataAccessor.RS_TYPE.LIST);
			}
		}
		
		public void batchDualInsuranceDynamicDetailByDay() throws Exception{
			batchInsuranceDynamicDetailForJob();
			adjustTailInsuranceDynamicDetailForJob();
		}
}
