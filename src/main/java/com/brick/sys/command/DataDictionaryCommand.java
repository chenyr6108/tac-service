package com.brick.sys.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.sys.TO.DataDictionaryTO;
import com.brick.sys.service.DataDictionaryService;
import com.brick.util.web.HTMLUtil;

public class DataDictionaryCommand extends BaseCommand {
	
	Log logger=LogFactory.getLog(DataDictionaryCommand.class);
			
	private DataDictionaryService dataDictionaryService;
	
	public DataDictionaryService getDataDictionaryService() {
		return dataDictionaryService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	public void query(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		try {
			
			List<DataDictionaryTO> itUserList=this.getDataDictionaryService().getItUserList();
			outputMap.put("itUserList",itUserList);
			
			PagingInfo<Object> pagingInfo=null;
			pagingInfo=this.dataDictionaryService.queryForListWithPaging("common.getDBTableInfo",context.contextMap,"lastUpdateOn",ORDER_TYPE.DESC);
			
			List<DataDictionaryTO> isMaintenanceTableList=null;
			isMaintenanceTableList=this.dataDictionaryService.isMaintenanceTable();
			
			//下面的for循环是判断表是否被维护过 
			Map<String,String> param=new HashMap<String,String>();
			for(int i=0;i<isMaintenanceTableList.size();i++) {
				for(int j=0;j<pagingInfo.getResultList().size();j++) {
					if(isMaintenanceTableList.get(i).getTableName().equals(((DataDictionaryTO)pagingInfo.getResultList().get(j)).getTableName())) {
						((DataDictionaryTO)pagingInfo.getResultList().get(j)).setIsMaintenanceTable("Y");
						param.put("TABLE_NAME",isMaintenanceTableList.get(i).getTableName());
						if(this.dataDictionaryService.getDBTableDetail1(param).size()>this.dataDictionaryService.getDBTableDetail2(param).size()) {
							((DataDictionaryTO)pagingInfo.getResultList().get(j)).setIsMaintenanceTable("N");
						}
						break;
					}
				}
			}
			
			outputMap.put("pagingInfo",pagingInfo);
			outputMap.put("TABLE_NAME",context.contextMap.get("TABLE_NAME"));
			outputMap.put("ID",context.contextMap.get("ID"));
			outputMap.put("IS_MAINTENANCE",context.contextMap.get("IS_MAINTENANCE"));
			outputMap.put("START_DATE",context.contextMap.get("START_DATE"));
			outputMap.put("END_DATE",context.contextMap.get("END_DATE"));
			outputMap.put("RESULT",context.contextMap.get("RESULT"));
		} catch (Exception e) {
			logger.debug("数据库表结构维护查询出错!");
			context.errList.add("数据库表结构维护查询出错!");
			outputMap.put("errList",context.errList);
			e.printStackTrace();
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		Output.jspOutput(outputMap,context,"/sys/dbTable.jsp");
	}
	
	//获得Table detail的方法
	public void getTableDetail(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<DataDictionaryTO> tableDetailList=null;
		List<DataDictionaryTO> tableConstraintList=null;
		try {
			Map<String,String> param=new HashMap<String,String>();
			param.put("TABLE_NAME",(String)context.contextMap.get("TABLE_NAME"));//参数:表名
			tableDetailList=(List<DataDictionaryTO>)this.dataDictionaryService.getDBTableDetail(param);
			outputMap.put("TABLE_NAME",context.contextMap.get("TABLE_NAME"));
			outputMap.put("tableDetailList",tableDetailList);
			outputMap.put("MIN_ID",tableDetailList.get(0).getId());//防止并发问题使用的比较的version
			
			//获得主外键
			tableConstraintList=(List<DataDictionaryTO>)this.dataDictionaryService.getDBTableConstraint(param);
			outputMap.put("tableConstraintList",tableConstraintList);
		} catch (Exception e) {
			logger.debug("数据库表结构维护获取表信息详细出错!");
			context.errList.add("数据库表结构维护获取表信息详细出错!");
			e.printStackTrace();
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		Output.jspOutput(outputMap,context,"/sys/dbTableDetail.jsp");
	}
	
	//字段描述更新方法
	public void update(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		String [] COLUMN_NAME=HTMLUtil.getParameterValues(context.request,"COLUMN_NAME","");//获得页面栏位名字
		String [] DATA_TYPE=HTMLUtil.getParameterValues(context.request,"DATE_TYPE","");//获得页面栏位数据类型
		String [] DESCRIPTION=HTMLUtil.getParameterValues(context.request,"DESCRIPTION","");//获得页面栏位描述
		String [] VERSION=HTMLUtil.getParameterValues(context.request,"VERSION","");//获得页面栏位版本号
		String REMARK=(String)context.contextMap.get("REMARK");
		String TABLE_CHINESE_NAME=(String)context.contextMap.get("TABLE_CHINESE_NAME");
		
		DataDictionaryTO dataDictionaryTO=new DataDictionaryTO();
		dataDictionaryTO.setS_employeeId(context.contextMap.get("s_employeeId").toString());
		dataDictionaryTO.setTableName((String)context.contextMap.get("TABLE_NAME"));
		dataDictionaryTO.setColumnNameInsert(COLUMN_NAME);
		dataDictionaryTO.setDataTypeInsert(DATA_TYPE);
		dataDictionaryTO.setDescriptionInsert(DESCRIPTION);
		dataDictionaryTO.setRemark(REMARK);
		dataDictionaryTO.setTableChineseName(TABLE_CHINESE_NAME);
		try {
			//***********************************************************************************************************************
			//防止并发问题
			Map<String,String> param=new HashMap<String,String>();
			param.put("TABLE_NAME",(String)context.contextMap.get("TABLE_NAME"));//参数:表名
			List<DataDictionaryTO> flag=this.dataDictionaryService.getDBTableDetail2(param);//获得此表是否维护过
			if(flag.size()==0) {//如果没有则做插入操作
				this.dataDictionaryService.insertTableInfo(dataDictionaryTO);
			} else {//如果有则做更新操作
				List<DataDictionaryTO> compareList=this.dataDictionaryService.getDBTableDetail2(param);
				for(int i=0;i<COLUMN_NAME.length;i++) {
					for(int j=0;j<compareList.size();j++) {
						if(COLUMN_NAME[i].equals(compareList.get(j).getColumnName())) {
							if(Integer.valueOf(VERSION[i])<compareList.get(j).getVersion()) {//并发产生
								List<DataDictionaryTO> tableDetailList=new ArrayList<DataDictionaryTO>();
								//把数据装回去
								for(int ii=0;ii<COLUMN_NAME.length;ii++) {
									DataDictionaryTO backData=new DataDictionaryTO();
									backData.setTableName(param.get("TABLE_NAME"));
									backData.setTableChineseName(TABLE_CHINESE_NAME);
									backData.setColumnName(COLUMN_NAME[ii]);
									backData.setDataType((DATA_TYPE[ii]));
									backData.setDescription(DESCRIPTION[ii]);
									backData.setRemark(REMARK);
									backData.setVersion(Integer.valueOf(VERSION[ii]));
									tableDetailList.add(backData);
								}
								outputMap.put("SYNC","Y");
								outputMap.put("tableDetailList",tableDetailList);
								
								//获得主外键
								List<DataDictionaryTO> tableConstraintList=(List<DataDictionaryTO>)this.dataDictionaryService.getDBTableConstraint(param);
								outputMap.put("tableConstraintList",tableConstraintList);
								
								outputMap.put("TABLE_NAME",context.contextMap.get("TABLE_NAME"));
								
								Output.jspOutput(outputMap,context,"/sys/dbTableDetail.jsp");
								return;
							} else {
								break;//如果不并发,继续下个栏位
							}
						}
					}
				}
				//更新操作
				dataDictionaryTO.setVersion(compareList.get(0).getVersion()+1);
				this.dataDictionaryService.deleteInsertTableInfo(dataDictionaryTO);
			}
			/*List<DataDictionaryTO> tableDetailList=(List<DataDictionaryTO>)this.dataDictionaryService.getDBTableDetail(param);
			this.dataDictionaryService.deleteInsertTableInfo(dataDictionaryTO);
			if(tableDetailList.get(0).getId()==Integer.valueOf(context.contextMap.get("VERSION").toString())) {
				this.dataDictionaryService.deleteInsertTableInfo(dataDictionaryTO);
			} else {
				context.errList.add("此表已经有人维护请获取最新信息更新!");
				outputMap.put("errList",context.errList);
				Output.jspOutput(outputMap,context,"/error.jsp");
				return;
			}*/
			//***********************************************************************************************************************
		} catch(Exception e) {
			logger.debug("数据库表结构维护更新描述出错!");
			context.errList.add("数据库表结构维护更新描述出错!");
			outputMap.put("errList",context.errList);
			e.printStackTrace();
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		context.contextMap.remove("TABLE_NAME");//跳转到search all页面需要移除表名,否则会传到页面的过滤条件
		context.contextMap.put("RESULT","Y");
		this.query(context);
	}
	
	public static List<DataDictionaryTO> exportTableDetailList() {
		
		List<DataDictionaryTO> exportTableDetailList=null;
		try {
			exportTableDetailList=(List<DataDictionaryTO>)DataAccessor.query("common.exportTableDetailList",null,RS_TYPE.LIST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return exportTableDetailList;
	}
}
