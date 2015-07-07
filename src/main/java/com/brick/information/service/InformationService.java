package com.brick.information.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.service.BaseService;
import com.brick.information.dao.InformationDao;
import com.brick.information.to.InforDataOfWeek;
import com.brick.information.to.InformationStatistic;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

public class InformationService extends BaseService {
              
	         Log logger=LogFactory.getLog(InformationService.class);
	
	         private InformationDao informationDao;

			public InformationDao getInformationDao() {
				return informationDao;
			}

			public void setInformationDao(InformationDao informationDao) {
				this.informationDao = informationDao;
			}
			
			public List<String> getAllYear(Context context){
				
				List<String> errList=context.errList;
				List<String> result=null;
				try{
					result=this.getInformationDao().getYearList();
				}catch(Exception e){
					LogPrint.getLogStackTrace(e, logger);
					e.printStackTrace();
					errList.add("数据分析--统计报表出错!请联系管理员");
				}
				return result;
				  
			}
			
			public List<InformationStatistic> getInfoStaList(Context context,Map<String,Object> params){
				List<String> errList=context.errList;
				List<InformationStatistic> result=null;
				try{
					result=this.getInformationDao().getInfoStaList(params);
				}catch(Exception e){
					LogPrint.getLogStackTrace(e, logger);
					e.printStackTrace();
					errList.add("数据分析--统计报表出错!请联系管理员");
				}
				return result;
			}
			
			//获取资讯周报表统计数据
			public List<InforDataOfWeek> getDataOfWeek(Context context){
				List<String> errList=context.errList;
				List<InforDataOfWeek> result=null;
				try{
					result=this.getInformationDao().getDateOfWeek();
				}catch(Exception e){
					LogPrint.getLogStackTrace(e, logger);
					e.printStackTrace();
					errList.add("数据分析--统计报表出错!请联系管理员");
				}
				return result;
			}
			
			public void addLog(){
				
				InforDataOfWeek infor=this.getInformationDao().getJobData();
				infor.setId(String.valueOf(System.currentTimeMillis()));
				this.getInformationDao().addLog(infor);
			}
			
			//test  补回数据
			/*public  void test() throws Exception{
				SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
				Map<String,Date> param=new HashMap<String,Date>();
				Calendar bcal=Calendar.getInstance();
				Calendar ecal=Calendar.getInstance();
				ecal.setTime(sd.parse("2014-2-28"));
				bcal.setTime(sd.parse("2014-2-28"));
				param.put("end", ecal.getTime());
				bcal.add(Calendar.DATE, -6);
				param.put("begin", bcal.getTime());
				InforDataOfWeek infor=getInformationDao().getJobData(param);
				infor.setId(String.valueOf(System.currentTimeMillis()));
				infor.setStaticDate(ecal.getTime());
				this.getInformationDao().addLog(infor);
				
				for(int i=0;i<20;i++){
					Map<String,Date> param1=new HashMap<String,Date>();
					bcal.add(Calendar.DATE, 7);
					ecal.add(Calendar.DATE, 7);
					param1.put("end", ecal.getTime());
					param1.put("begin", bcal.getTime());
					InforDataOfWeek infor1=getInformationDao().getJobData(param1);
					infor1.setId(String.valueOf(System.currentTimeMillis()));
					infor1.setStaticDate(ecal.getTime());
					this.getInformationDao().addLog(infor1);
				}
			}*/
}
