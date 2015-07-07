package com.brick.invoiceManagement.command;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.invoiceManagement.service.InvoiceManagementService;
import com.brick.invoiceManagement.util.InvoiceManagementUtil;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.FileExcelUpload;
import com.ibatis.sqlmap.client.SqlMapClient;

public class InvoiceManagementCommand extends BaseCommand {

	private InvoiceManagementService invoiceManagementService;

	public InvoiceManagementService getInvoiceManagementService() {
		return invoiceManagementService;
	}

	public void setInvoiceManagementService(
			InvoiceManagementService invoiceManagementService) {
		this.invoiceManagementService = invoiceManagementService;
	}
	
	//开票查询
	public void issueInvoiceQuery(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<Map<String,Object>> financeList=this.invoiceManagementService.getFinanceDateList();
		outputMap.put("caseType",context.contextMap.get("caseType"));
		if(InvoiceManagementUtil.CASE_TYPE.NEW.toString().equals(context.contextMap.get("caseType"))) {
			context.contextMap.put("caseType","新案");
		} else if(InvoiceManagementUtil.CASE_TYPE.OLD.toString().equals(context.contextMap.get("caseType"))) {
			context.contextMap.put("caseType","旧案");
		}
		if(context.contextMap.get("cardFlag")==null) {//0直租
			context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
			context.contextMap.put("cardFlag","0");
			context.contextMap.put("companyCode","1");
			context.contextMap.put("caseType","新案");
			context.contextMap.put("financeDate",financeList==null||financeList.size()==0?"":((Map<String,Object>)financeList.get(0)).get("FINANCE_DATE"));
		} else if("0".equals(context.contextMap.get("cardFlag").toString())) {
			context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_4});
		} else if("1".equals(context.contextMap.get("cardFlag").toString())) {//1增值税
			context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_2});
		} else if("2".equals(context.contextMap.get("cardFlag").toString())) {//2乘用车委贷
			context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_5});
		} else if("3".equals(context.contextMap.get("cardFlag").toString())) {//3售后回租
			if(context.contextMap.get("taxPlanCode")==null||"0".equals(context.contextMap.get("taxPlanCode").toString())) {//选择全部
				outputMap.put("taxPlanCode","0");
				context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_6,Constants.TAX_PLAN_CODE_7,Constants.TAX_PLAN_CODE_8});
			} else {
				outputMap.put("taxPlanCode",context.contextMap.get("taxPlanCode"));
				context.contextMap.put("taxPlanCode",new String [] {context.contextMap.get("taxPlanCode").toString()});
			}
		} else if("4".equals(context.contextMap.get("cardFlag").toString())) {//4营业税
			context.contextMap.put("taxPlanCode",new String [] {Constants.TAX_PLAN_CODE_1});
		}
		
		PagingInfo<Object> pagingInfo=baseService.queryForListWithPaging("invoiceManagement.getInvoiceList",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		for(int i=0;pagingInfo!=null&&pagingInfo.getResultList()!=null&&i<pagingInfo.getResultList().size();i++) {
			Map<String,Object> result=this.invoiceManagementService.getInvoiceInfo((Map<String,Object>)pagingInfo.getResultList().get(i));
			if(result==null) {
				continue;
			}
			((Map<String,Object>)pagingInfo.getResultList().get(i)).put("ADDRESS",result.get("ADDRESS"));
			((Map<String,Object>)pagingInfo.getResultList().get(i)).put("BANK_INFO",result.get("BANK_INFO"));
			((Map<String,Object>)pagingInfo.getResultList().get(i)).put("TAX_REGISTRATION_NUMBER",result.get("TAX_REGISTRATION_NUMBER"));
		}
		outputMap.put("dw",pagingInfo);
		outputMap.put("result",context.contextMap.get("result"));
		outputMap.put("hasPay",context.contextMap.get("hasPay"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("financeDate",context.contextMap.get("financeDate"));
		outputMap.put("companyCode",context.contextMap.get("companyCode"));
		outputMap.put("financeDateList",financeList);
		outputMap.put("cardFlag",context.contextMap.get("cardFlag"));//1增值税,2乘用车委贷,3售后回租,4营业税
		outputMap.put("__action",context.contextMap.get("__action"));
		Output.jspOutput(outputMap,context,"/invoiceManagement/queryInvoice.jsp");
	}
	
	//停开发票查询
	public void stopInvoiceQuery(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		List<String> effectDateList=this.invoiceManagementService.getEffectDateList();
		//获得数据
		PagingInfo<Object> pagingInfo=baseService.queryForListWithPaging("invoiceManagement.stopInvoiceQuery",context.contextMap,"RECP_ID",ORDER_TYPE.DESC);
		
		//获得办事处List
		outputMap.put("deptList",this.baseService.getAllDecp());
		outputMap.put("effectDateList",effectDateList);
		outputMap.put("effectDate",context.contextMap.get("effectDate"));
		outputMap.put("deptId",context.contextMap.get("deptId"));//办事处过滤
		outputMap.put("fromDate",context.contextMap.get("fromDate"));//拨款日期开始过滤
		outputMap.put("toDate",context.contextMap.get("toDate"));//拨款日期结束过滤
		outputMap.put("hasStop",context.contextMap.get("hasStop"));//有停开的发票的
		outputMap.put("hasLog",context.contextMap.get("hasLog"));//有停复开操作的
		outputMap.put("companyCode",context.contextMap.get("companyCode"));//公司别
		outputMap.put("content",context.contextMap.get("content"));//内容查询包括案件号,合同号,客户名称等
		outputMap.put("dw",pagingInfo);
		Output.jspOutput(outputMap,context,"/invoiceManagement/stopInvoiceQuery.jsp");
	}
	
	public void getPaymentDetail(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> paymentList=null;
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		if(Constants.TAX_PLAN_CODE_1.equals(context.contextMap.get("taxPlanCode"))) {//营业税税费方案
			paymentList=this.invoiceManagementService.getPaymentDetail(context);
			for(int i=0;paymentList!=null&&i<paymentList.size();i++) {
				Map<String,Object> value=new HashMap<String,Object>();
				value.put("IS_OPEN",paymentList.get(i).get("IS_OPEN"));
				value.put("IS_STOP",paymentList.get(i).get("IS_STOP"));
				value.put("PERIOD_NUM",paymentList.get(i).get("PERIOD_NUM"));
				value.put("REN_PRICE",paymentList.get(i).get("REN_PRICE"));
				value.put("PAY_DATE",paymentList.get(i).get("PAY_DATE"));
				value.put("EFFECT_DATE",paymentList.get(i).get("EFFECT_DATE"));
				resultList.add(value);
			}
		} else if(Constants.TAX_PLAN_CODE_2.equals(context.contextMap.get("taxPlanCode"))) {//增值税税费方案
			paymentList=this.invoiceManagementService.getPaymentDetail(context);
			for(int i=0;paymentList!=null&&i<paymentList.size();i++) {
				Map<String,Object> value=new HashMap<String,Object>();
				value.put("IS_OPEN",paymentList.get(i).get("IS_OPEN"));
				value.put("IS_STOP",paymentList.get(i).get("IS_STOP"));
				value.put("PERIOD_NUM",paymentList.get(i).get("PERIOD_NUM"));
				value.put("REN_PRICE",paymentList.get(i).get("REN_PRICE"));
				value.put("PAY_DATE",paymentList.get(i).get("PAY_DATE"));
				value.put("EFFECT_DATE",paymentList.get(i).get("EFFECT_DATE"));
				resultList.add(value);
			}
		} else if(Constants.TAX_PLAN_CODE_4.equals(context.contextMap.get("taxPlanCode"))) {//直租税费方案
			paymentList=this.invoiceManagementService.getPaymentDetail(context);
			for(int i=0;paymentList!=null&&i<paymentList.size();i++) {
				Map<String,Object> value=new HashMap<String,Object>();
				value.put("RECP_ID",paymentList.get(i).get("RECP_ID"));
				if("1".equals(paymentList.get(i).get("PERIOD_NUM").toString())) {//直租第一期
					value.put("DEPOSIT_A",paymentList.get(i).get("DEPOSIT_A"));
					value.put("OWN_PRICE",paymentList.get(i).get("OWN_PRICE_2"));
				} else {
					value.put("OWN_PRICE",paymentList.get(i).get("OWN_PRICE_1"));
					
				}
				value.put("IS_OPEN",paymentList.get(i).get("IS_OPEN"));
				value.put("IS_STOP",paymentList.get(i).get("IS_STOP"));
				value.put("PERIOD_NUM",paymentList.get(i).get("PERIOD_NUM"));
				value.put("REN_PRICE",paymentList.get(i).get("REN_PRICE_1"));
				value.put("PAY_DATE",paymentList.get(i).get("PAY_DATE"));
				value.put("EFFECT_DATE",paymentList.get(i).get("EFFECT_DATE"));
				resultList.add(value);
			}
		} else if(Constants.TAX_PLAN_CODE_5.equals(context.contextMap.get("taxPlanCode"))) {//乘用车委贷
			paymentList=this.invoiceManagementService.getPaymentDetail(context);
			for(int i=0;paymentList!=null&&i<paymentList.size();i++) {
				Map<String,Object> value=new HashMap<String,Object>();
				value.put("IS_OPEN",paymentList.get(i).get("IS_OPEN"));
				value.put("IS_STOP",paymentList.get(i).get("IS_STOP"));
				value.put("PERIOD_NUM",paymentList.get(i).get("PERIOD_NUM"));
				value.put("REN_PRICE",paymentList.get(i).get("REN_PRICE"));
				value.put("PAY_DATE",paymentList.get(i).get("PAY_DATE"));
				value.put("EFFECT_DATE",paymentList.get(i).get("EFFECT_DATE"));
				resultList.add(value);
			}
		} else if(Constants.TAX_PLAN_CODE_6.equals(context.contextMap.get("taxPlanCode"))||//售后回租
				  Constants.TAX_PLAN_CODE_7.equals(context.contextMap.get("taxPlanCode"))||
				  Constants.TAX_PLAN_CODE_8.equals(context.contextMap.get("taxPlanCode"))) {
			paymentList=this.invoiceManagementService.getPaymentDetail(context);
		}
		
		outputMap.put("RECP_ID",context.contextMap.get("recpId"));
		outputMap.put("FLAG",context.contextMap.get("flag"));//STOP为停开,OPEN为复开
		outputMap.put("TAX_PLAN_CODE",context.contextMap.get("taxPlanCode"));
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/invoiceManagement/paymentDetail.jsp");
	}
	
	//检查打开2个以上tab重复提交
	public void checkDuplicate(Context context) {
		boolean flag=false;
		String result="";
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("RECP_ID",context.contextMap.get("RECP_ID"));
		if(InvoiceManagementUtil.PROCESS.STOP.toString().equals(context.contextMap.get("FLAG").toString())) {
			result=this.invoiceManagementService.checkStopInvoice(param);
		} else if(InvoiceManagementUtil.PROCESS.OPEN.toString().equals(context.contextMap.get("FLAG").toString())) {
			result=this.invoiceManagementService.checkOpenInvoice(param);
		} else if(InvoiceManagementUtil.PROCESS.NOT_OPEN.toString().equals(context.contextMap.get("FLAG").toString())) {
			result=this.invoiceManagementService.checkNotOpenInvoice(param);
		} else if(InvoiceManagementUtil.PROCESS.NOT_STOP.toString().equals(context.contextMap.get("FLAG").toString())) {
			result=this.invoiceManagementService.checkNotStopInvoice(param);
		}
		if("Y".equals(result)) {
			flag=true;
		}
		Output.jsonFlageOutput(flag,context);
	}
	
	public void updateInvoicePaymentDetail(Context context) {
		String flag=context.contextMap.get("FLAG").toString();
		try {
			if(InvoiceManagementUtil.PROCESS.STOP.toString().equals(flag)) {
				this.invoiceManagementService.stopInvoiceProcess(context);
			} else if(InvoiceManagementUtil.PROCESS.OPEN.toString().equals(flag)) {
				this.invoiceManagementService.openInvoiceProcess(context);
			}
		} catch(Exception e) {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			context.errList.add("停复开发票失败,请联系管理员");
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		this.stopInvoiceQuery(context);
	}
	
	public void stopInvoiceProcess(Context context) {
		try {
			this.invoiceManagementService.stopInvoiceProcess(context);
		} catch(Exception e) {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			context.errList.add("停开发票失败,请联系管理员");
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		 Output.jsonFlageOutput(true,context);
	}
	
	public void showLog(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=this.invoiceManagementService.showLog(context);
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/invoiceManagement/showLog.jsp");
	}
	public void showSpecialLog(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=this.invoiceManagementService.showSpecialLog(context);
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/invoiceManagement/showSpecialLog.jsp");
	}
	
	public void maintenanceSpecialInvoiceQuery(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		//获取系统当前逾期44天所有的案子,需要设定不自动停开
		PagingInfo<Object> pagingInfo=baseService.queryForListWithPaging("invoiceManagement.dun45DaysCaseQuery",context.contextMap,"DUN_DAY",ORDER_TYPE.DESC);
		
		//获得办事处List
		outputMap.put("deptList",this.baseService.getAllDecp());
		outputMap.put("deptId",context.contextMap.get("deptId"));//办事处过滤
		outputMap.put("fromDate",context.contextMap.get("fromDate"));//拨款日期开始过滤
		outputMap.put("toDate",context.contextMap.get("toDate"));//拨款日期结束过滤
		outputMap.put("content",context.contextMap.get("content"));//内容查询包括案件号,合同号,客户名称等
		outputMap.put("hasStop",context.contextMap.get("hasStop"));//有停开的发票的
		outputMap.put("hasLog",context.contextMap.get("hasLog"));//有停复开操作的
		outputMap.put("companyCode",context.contextMap.get("companyCode"));//公司别
		outputMap.put("dw",pagingInfo);
		Output.jspOutput(outputMap,context,"/invoiceManagement/specialInvoiceQuery.jsp");
	}
	
	public void maintenanceSpecialInvoice(Context context) {
		
		try {
			this.invoiceManagementService.maintenanceSpecialInvoice(context);
		} catch (Exception e) {
			Map<String,Object> outputMap=new HashMap<String,Object>();
			context.errList.add("设定失败,请联系管理员");
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		}
		this.maintenanceSpecialInvoiceQuery(context);
	}
	
	//导出开票资料日志查询
	public void queryLog(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		//查找导出开票资料日志
		PagingInfo<Object> pagingInfo=baseService.queryForListWithPaging("invoiceManagement.queryLog",context.contextMap,"CREATE_TIME",ORDER_TYPE.DESC);
		
		outputMap.put("dw",pagingInfo);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		Output.jspOutput(outputMap,context,"/invoiceManagement/invoiceLog.jsp");
	}
	
	//上传开票资料日志查询
	public void queryUploadLog(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		//查找上传开票资料日志
		PagingInfo<Object> pagingInfo=baseService.queryForListWithPaging("invoiceManagement.queryUploadLog",context.contextMap,"CREATE_TIME",ORDER_TYPE.DESC);
		
		outputMap.put("dw",pagingInfo);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		Output.jspOutput(outputMap,context,"/invoiceManagement/invoiceUploadLog.jsp");
	}
	
	//跳转上传页面
	public void gotoUploadPage(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		outputMap.put("msg",context.contextMap.get("msg"));
		Output.jspOutput(outputMap,context,"/invoiceManagement/uploadPage.jsp");
	}
	
	//EXCEL文件上传
	public void uploadFile(Context context) {
		
		String fileName="";
		List<Map<String,Object>> resultList=null;
		//读取 Excel文件:得到 Workbook 对象
		Workbook workbook=null;
		//得到输入流
		InputStream in=(InputStream)context.contextMap.get("excelInputStream");

		try {
			FileItem fileItem=(FileItem)context.getRequest().getSession().getAttribute("fileItem");//保存文件到硬盘
			fileName=this.saveFileToDisk(fileItem);
			
			workbook=WorkbookFactory.create(in);
			if(workbook!=null) {
				//表格
				Sheet sheet=workbook.getSheetAt(0);//只拿sheet1
				Iterator<Row> rit=sheet.rowIterator();
				if(rit.hasNext()) {
					Row row=(Row)rit.next();//得到第一行,因为上传格式有2种,一种是乘用车委贷款公司外部开票后的资料回传,另外一种公司普通开票的资料回传
					Iterator<Cell> cit=row.cellIterator();
					if(cit.hasNext()) {
						Cell cell=(Cell)cit.next();
						if("乘用车委贷开票明细表".equals(cell.getStringCellValue())) {
							resultList=this.invoiceManagementService.buildExternalInvoiceListFromWorkbook(workbook);
						} else {
							resultList=this.invoiceManagementService.buildInternalInvoiceListFromWorkbook(workbook);
						}
						for(int i=0;resultList!=null&&i<resultList.size();i++) {
							resultList.get(i).put("fileName",fileName);
							resultList.get(i).put("s_employeeId",context.contextMap.get("s_employeeId"));
							resultList.get(i).put("caseType",InvoiceManagementUtil.CASE_TYPE.NORMAL.toString());
							this.invoiceManagementService.insertUploadLog(resultList.get(i));
							resultList.get(i).put("result",1);//0失败,1成功
							resultList.get(i).put("resultDescr","成功");
							resultList.get(i).put("resultMemo","手动上传开票资料");
							this.invoiceManagementService.uploadInvoiceNum(resultList.get(i));
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			context.contextMap.put("msg","上传失败");
			this.gotoUploadPage(context);
			return;
		}
		context.contextMap.put("msg","上传成功");
		this.gotoUploadPage(context);
	}
	
	private String saveFileToDisk(FileItem fileItem) throws Exception {
		File filePath=new File(File.separator+"home"+File.separator+"filsoft"+File.separator+"financelease"+File.separator+"upload"+File.separator+"invoiceUpload");
		if(!filePath.exists()) {
			filePath.mkdirs();
		}
		String fileName=FileExcelUpload.getNewFileName();
		fileItem.write(new File(filePath.getPath()+File.separator+fileName+".xls"));
		return fileName;
	}
	
	public void getResult(Context context) {
		
		Map<String,Object> outputMap=this.invoiceManagementService.getResult(context);
		Output.jsonOutput(outputMap,context);
	}
}
