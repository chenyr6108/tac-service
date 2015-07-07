package com.brick.rent.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.decompose.service.ComposeService;
import com.brick.log.service.LogPrint;
import com.brick.rent.RentFinanceUtil;
import com.brick.rent.service.RentFinanceService;
import com.brick.rent.to.SettlementLogTO;
import com.brick.rent.to.SettlementTO;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.settle.service.SettlePDF;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibm.icu.math.BigDecimal;
import com.tac.user.service.UserService;
import com.tac.user.to.UserTo;

public class RentFinanceCommand extends BaseCommand {

	String failId="";
	Log logger=LogFactory.getLog(this.getClass());
	private RentFinanceService rentFinanceService;
	private MailUtilService mailUtilService;
	private UserService userService;
	
	public RentFinanceService getRentFinanceService() {
		return rentFinanceService;
	}

	public void setRentFinanceService(RentFinanceService rentFinanceService) {
		this.rentFinanceService=rentFinanceService;
	}

	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	//**************************************************************************************来款上传
	//跳转到上传Excel页面
	public void goToUploadIncomeMoneyPage(Context context) {
		Map<String,Object> outputMap =  new HashMap<String,Object>();
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/finance/uploadIncomeMoney.jsp");
	}

	//读出财务上传来款数据,返回至页面等待保存至数据库中
	public void readIncomeMoney(Context context) throws Exception {

		Map<String,Object> outputMap=new HashMap<String,Object>();

		//读取 Excel 文件: 得到 Workbook 对象
		Workbook workbook=null;
		//得到输入流
		InputStream in=(InputStream)context.contextMap.get("excelInputStream");

		try {
			workbook=WorkbookFactory.create(in);
		} catch (InvalidFormatException e) {
		} catch (IOException e) {
		}
		if(in==null) {
			context.errList.add("上传Excel读取失败!请联系管理员");
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		} else {
			
			List<Map<String,Object>> composeList=this.rentFinanceService.buildComposeListFromWorkbook(workbook);

			this.isValidatorComposesList(context,composeList);
			//判断公司别是否正确
			if(composeList!=null){
				String companyCode  = (String) context.contextMap.get("companyCode");
				if(companyCode!=null && !"".equals(companyCode)){
					StringBuffer errorMsg = new StringBuffer("");
					List bankList1 = DictionaryUtil.getDictionary("裕融收款账号");
					List bankList2 = DictionaryUtil.getDictionary("裕国收款账号");
					boolean flag = false;
					int index = 1;
					String companyname = "";
					if("1".equals(companyCode)){
						companyname = "裕融";
					}else if("2".equals(companyCode)){
						companyname = "裕国";
					}
					for(Map<String,Object> compose:composeList){
						String bankNo = (String) compose.get("receipt_bankno");
						String recpUnit = (String) compose.get("receipt_unit");
						
						flag = false;
						if("1".equals(companyCode)){
							for(int i=0;i<bankList1.size();i++){
								String no = (String) ((Map)bankList1.get(i)).get("FLAG");
								if(no.equals(bankNo)){
									flag = true;
									break;
								}
							}
						}else if("2".equals(companyCode)){
							for(int i=0;i<bankList2.size();i++){
								String no = (String) ((Map)bankList2.get(i)).get("FLAG");
								if(no.equals(bankNo)){
									flag = true;
									break;
								}
							}
						}
						//收款账号判断
						if(recpUnit.indexOf(companyname)<0 || !flag){//上传的收款单位跟选择的不一致
							errorMsg.append(index+",");
						}
					index++;
					}
					if(!"".equals(errorMsg.toString())){
						errorMsg.append("以上几行数据的收款账号、收款单位和选择的公司别不一致！");
						outputMap.put("companys", LeaseUtil.getCompanys());	
						outputMap.put("errorMsg", errorMsg.toString());	
						Output.jspOutput(outputMap,context,"/rent/finance/uploadIncomeMoney.jsp");
						return;
					}
				}
				
			}
			context.getRequest().setAttribute("errorList",context.getErrList());
			
			outputMap.put("composeList",composeList);
			
			if(context.errList.isEmpty()) {
				Output.jspOutput(outputMap,context,"/rent/finance/readIncomeMoney.jsp");
			} else {
				outputMap.put("errList",context.errList) ;
				Output.jspOutput(outputMap,context,"/error.jsp") ;
			}
		}
	}

	//保存Excel到服务器,并且将Excel数据保存至数据库
	public void saveExcelToDiskAndSaveData(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		//从session获得文件
		FileItem fileItem=(FileItem)context.getRequest().getSession().getAttribute("fileItem");
		
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		
		try {
			sqlMapClient.startTransaction();
			
			FileExcelUpload.saveExcelFileToDisk(context,fileItem,sqlMapClient);
			
			this.rentFinanceService.saveData(context,fileItem,sqlMapClient);
			
			sqlMapClient.commitTransaction();
		} catch(Exception e) {
			logger.debug("保存失败");
			context.errList.add("保存失败");
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}
		
		if(context.errList.isEmpty()) {
			this.goToUploadIncomeMoneyPage(context);
		} else {
			outputMap.put("errList",context.errList) ;
			Output.jspOutput(outputMap,context,"/error.jsp") ;
		}
	}
	//**************************************************************************************来款上传
	
	
	
	//**************************************************************************************租金分解
	//查询未分解
	public void queryUnDecomposeMoney(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		outputMap.put("cardFlag",0);
		outputMap.put("__action","rentFinanceCommand.queryUnDecomposeMoney");
		outputMap.put("menu",context.contextMap.get("menu"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("hasFile",context.contextMap.get("hasFile"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getUnDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/queryRentMoney.jsp");
	}
	
	//查询分解中
	public void queryOnDecomposeMoney(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> dw=null;
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		
		outputMap.put("cardFlag",1);
		outputMap.put("__action","rentFinanceCommand.queryOnDecomposeMoney");
		outputMap.put("menu",context.contextMap.get("menu"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getOnDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/queryRentMoney.jsp");
	}
	
	//查询提交财务
	public void queryCommitFinanceMoney(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> dw=null;
		
		outputMap.put("type",context.contextMap.get("type")==null?"0":context.contextMap.get("type"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		if(context.contextMap.get("from")==null) {
			context.contextMap.put("from","-1");
			outputMap.put("from","-1");
		} else {
			outputMap.put("from",context.contextMap.get("from"));
		}
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		
		outputMap.put("cardFlag",2);
		outputMap.put("__action","rentFinanceCommand.queryCommitFinanceMoney");
		outputMap.put("menu",context.contextMap.get("menu"));
		outputMap.put("msg",context.contextMap.get("msg"));
		outputMap.put("companyCode",context.contextMap.get("companyCode"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getCommitDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/queryRentMoney.jsp");
	}
	
	//查询财务确认
	public void queryFinanceConfirm(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> dw=null;
		
		outputMap.put("type",context.contextMap.get("type")==null?"0":context.contextMap.get("type"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("companyCode",context.contextMap.get("companyCode"));
		if(context.contextMap.get("from")==null) {
			context.contextMap.put("from","-1");
			outputMap.put("from","-1");
		} else {
			outputMap.put("from",context.contextMap.get("from"));
		}
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		
		outputMap.put("cardFlag",3);
		outputMap.put("__action","rentFinanceCommand.queryFinanceConfirm");
		outputMap.put("menu",context.contextMap.get("menu"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getConfirmDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/queryRentMoney.jsp");
	}
	
	//查询财务驳回
	public void queryFinanceReject(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		
		PagingInfo<Object> dw=null;
		
		outputMap.put("type",context.contextMap.get("type")==null?"0":context.contextMap.get("type"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("companyCode",context.contextMap.get("companyCode"));
		if(context.contextMap.get("from")==null) {
			context.contextMap.put("from","-1");
			outputMap.put("from","-1");
		} else {
			outputMap.put("from",context.contextMap.get("from"));
		}
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		
		outputMap.put("cardFlag",4);
		outputMap.put("__action","rentFinanceCommand.queryFinanceReject");
		outputMap.put("menu",context.contextMap.get("menu"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getRejectDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/queryRentMoney.jsp");
	}
	
	//选择某个客户后,获得租金分解列表
	public void getDecomposeList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> rentFinanceMap=null;
		List<Map<String,Object>> decomposeList=null;//租金列表
		List<Map<String,Object>> feeList=null;//管理费,保险费押金代收款,家访费收入,设定费收入 ,其他收入
		List<Map<String,Object>> depositAList=null;//保证金A
		List<Map<String,Object>> depositBList=null;//保证金B
		List<Map<String,Object>> depositCList=null;//保证金C
		List<Map<String,Object>> stayBuyList=null;//留购价
		List<Map<String,Object>> taxList=null;//税金
		List<Map<String,Object>> lawFeeList=null;//法务费用
		List<Map<String,Object>> fineList=null;//法务费用
		List<Map<String,Object>> incomePayList=null;//银行手续费收入
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		
		try {
			rentFinanceMap=this.rentFinanceService.getIncomeInfoByIncomeId(context);
			decomposeList=this.rentFinanceService.getLeasePriceByCustCode(context);//租金列表
			feeList=this.rentFinanceService.getFeeByCustCode(context);//管理费,保险费押金代收款,家访费收入,设定费收入 ,其他收入
			depositAList=this.rentFinanceService.getPledgeAByCustCode(context);//保证金A
			depositBList=this.rentFinanceService.getPledgeBByCustCode(context);//保证金B
			depositCList=this.rentFinanceService.getPledgeCByCustCode(context);//保证金C
			taxList=this.rentFinanceService.getTaxByCustCode(context);//税金
			lawFeeList=this.rentFinanceService.getLawFeeByCustCode(context);//法务费用
			stayBuyList=this.rentFinanceService.getStayBuyByCustCode(context);//留购价
			fineList=LeaseUtil.getTotalFineByMapForDecompose((String)context.getContextMap().get("cust_code"));//租金罚息
			incomePayList=this.rentFinanceService.getIncomePayByCustCode(context);//银行手续费收入
		} catch (Exception e) {
			
		}
		resultList.addAll(fineList);
		resultList.addAll(feeList);
		resultList.addAll(decomposeList);
		resultList.addAll(depositAList);
		resultList.addAll(depositBList);
		resultList.addAll(depositCList);
		resultList.addAll(taxList);
		resultList.addAll(lawFeeList);
		resultList.addAll(stayBuyList);
		resultList.addAll(incomePayList);
		
		outputMap.put("rentFinanceMap",rentFinanceMap);
		outputMap.put("resultList",resultList);
		
		Output.jspOutput(outputMap,context,"/rent/finance/rentDecompose/queryDecomposeList.jsp");
	}
	
	//生成租金分解明细数据
	public void createDecomposeDetailList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		List<Map<String,Object>> resultList=null;
		try {
			sqlMapClient.startTransaction();
			resultList=this.rentFinanceService.createDecomposeDetailList(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("生成租金分解明细数据出错,请联系管理员!");
			context.errList.add("生成租金分解明细数据出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}//进行租金分解
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/rent/finance/rentDecompose/commitDecomposeList.jsp");
	}
	
	//显示租金分解明细数据
	public void showDecomposeDetailList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> decomposeList=new ArrayList<Map<String,Object>>();
		double totalMoney=0;
		try {
			
			decomposeList=this.rentFinanceService.getDecomposePriceDetail(context);
			
			for(int i=0;i<decomposeList.size();i++) {
				if(RentFinanceUtil.RENT_TYPE.RENT.toString().equals(decomposeList.get(i).get("BILL_CODE"))) {
					decomposeList.get(i).put("descr","第"+context.contextMap.get("periodNum")+"期租金");
				} else if(RentFinanceUtil.RENT_TYPE.VALUE_ADD_TAX.toString().equals(decomposeList.get(i).get("BILL_CODE"))) {
					decomposeList.get(i).put("descr","第"+context.contextMap.get("periodNum")+"期增值税");
				} else {
					decomposeList.get(i).put("descr",decomposeList.get(i).get("DESCR"));
				}
				totalMoney=new BigDecimal(totalMoney).subtract(new BigDecimal(decomposeList.get(i).get("INCOME_MONEY")+"")).doubleValue();
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		decomposeList.get(0).put("TOTAL_MONEY",-totalMoney);//设定来款总金额,是租金分解明细表所有数据的总和,因为来款表中的数据不能修改
		outputMap.put("resultList",decomposeList);
		outputMap.put("menu",context.contextMap.get("menu"));
		outputMap.put("recpId",context.contextMap.get("recpId"));
		outputMap.put("decomposeStatus",context.contextMap.get("decomposeStatus"));//判断是从分解中页面,还是提交财务页面跳转到此方法,分解中decomposeStatus=0,提交财务=1
		
		//用于返回按钮父页面的条件
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		Output.jspOutput(outputMap,context,"/rent/finance/rentDecompose/showCommitDecomposeList.jsp");
	}
	
	//把租金分解更新成提交财务状态
	public void commitDecompose(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			this.rentFinanceService.commitDecompose(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("租金分解更新提交财务出错,请联系管理员!");
			context.errList.add("租金分解更新提交财务出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		context.contextMap.put("menu","rentDecompose");
		this.queryUnDecomposeMoney(context);
	}
	
	//把租金分解更新成财务通过状态
	public void confirmDecompose(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			this.rentFinanceService.confirmDecompose(context,sqlMapClient);//更新租金分解表
			this.rentFinanceService.updateRentPayDetail(context,sqlMapClient);//更新支付表
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("租金分解更新财务通过出错,请联系管理员!");
			context.errList.add("租金分解更新财务通过出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		context.contextMap.put("menu","confirmDecompose");
		this.queryCommitFinanceMoney(context);
	}
	
	//把租金分解更新成财务驳回状态
	public void rejectDecompose(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			this.rentFinanceService.rejectDecompose(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("租金分解更新财务驳回出错,请联系管理员!");
			context.errList.add("租金分解更新财务驳回出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		context.contextMap.put("menu","confirmDecompose");
		this.queryCommitFinanceMoney(context);
	}
	//**************************************************************************************租金分解
	
	//通过客户名称查询客户code
	//TODO  要加入锁支付表以后会有多用户使用租金分解功能
	public void checkCustNameHasPayList(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		try {
			outputMap.put("CUST_CODE",this.rentFinanceService.checkCustNameHasPayList(context));
		} catch(Exception e) {
			context.errList.add("租金分解-检查客户姓名错误!请联系管理员");
		}
		Output.jsonOutput(outputMap,context);
	}
	
	private void isValidatorComposesList(Context context,List<Map<String,Object>> composeList) {

		if(composeList==null||composeList.isEmpty()||composeList.size()==0) {
			context.errList.add("上传的数据为空");
			return;
		}

		for (int i=0;i<composeList.size();i++) {
			Map<String,Object> map=composeList.get(i);
			Set<Entry<String,Object>> entrySet=map.entrySet();
			Iterator<Entry<String,Object>> it=entrySet.iterator();
			List<String> eList=new ArrayList<String>();
			while(it.hasNext()) {
				Entry<String,Object> entry=(Entry<String,Object>)it.next();
				String str = entry.getKey().toString();
				if("opposing_date".equals(str)) {
					Object date=entry.getValue();
					if(date==null) {
						eList.add("日期不能为空");
					} else {
						if(!ComposeService.isDate(date.toString(),"yyyy-MM-dd")) {
							eList.add("日期格式不对");
						}
					}
				} else if("income_money".equals(str)) {//收入金额
					Object income_money=entry.getValue();
					String incom_mon=income_money.toString();
					String income_moneys=income_money.toString();
					for(int j=0;j<incom_mon.length();j++) {
						if(incom_mon.charAt(j)>=48&&incom_mon.charAt(j)<=57) {
							break;
						} else {
							income_moneys=income_moneys.replace(incom_mon.charAt(j)+"","");
						}
					}
					String income=income_moneys.toString().trim().replace("$","").replace("￥","").replace(",","").replace("，","").replace("?","").replace("？","");
					try {
						double dou=Double.valueOf(income);
						if(dou==0.0) {
							eList.add("收入金额不能为空");
						}
					} catch(Exception e) {
						eList.add("收入金额格式不对");
					}

				} else if("opposing_type".equals(str)) {// 交易方式
					String opposing_type=(String)entry.getValue();
					if(StringUtils.isEmpty(opposing_type)) {
						eList.add("交易方式不能为空");
					}
				} else if("opposing_unit".equals(str)) {// 对方户名
					String opposing_unit=(String)entry.getValue();
					if(StringUtils.isEmpty(opposing_unit)) {
						eList.add("对方户名不能为空");
					}

				} else if("receipt_bankno".equals(str)) {//收款账号
					String receipt_bankno=(String)entry.getValue();
					if(receipt_bankno==null) {
						eList.add("收款账号不能为空");
					}
				} else if("receipt_unit".equals(str)) {//来款单位
					String receipt_unit=(String)entry.getValue();
					if(receipt_unit==null) {
						eList.add("来款单位不能为空");
					}
				}
			}
			/** 如果有错,就加入标识,方便在jsp页面显示 */
			if(eList.size()!=0) {
				eList.add("行号为："+map.get("rowNumber"));
				context.errList.add(eList);
				map.put("isError",1);
				context.getRequest().getSession().removeAttribute("composeList");
			} else {
				map.put("isError",0);
			}
		}
	}
	
	//**********************************************冲红页面查询,所有能冲红的分解款项
	public void queryRedDecomposeMoney(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		if(context.contextMap.get("from")==null) {
			context.contextMap.put("from","-1");
			outputMap.put("from","-1");
		} else {
			outputMap.put("from",context.contextMap.get("from"));
		}
		
		dw=baseService.queryForListWithPaging("rentFinance.getRedDecomposeList",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/redDecompose/queryRedMoney.jsp");
	}
	
	public void checkCanBeAutoDecompose(Context context) {
		boolean flag=false;
		int res=0;
		try {
			res=this.rentFinanceService.checkCanBeAutoDecompose();
		} catch (Exception e) {
		}
		
		if(res==0) {
			flag=true;
		}
		Output.jsonFlageOutput(flag,context);
	}
	
	public void checkRedDecomposeIsLock(Context context) {
		boolean flag=false;
		String res="";
		try {
			res=this.rentFinanceService.checkRedDecomposeIsLock(context);
		} catch (Exception e) {
		}
		
		if("Y".equals(res)) {
			flag=true;
		}
		Output.jsonFlageOutput(flag,context);
	}
	
	public void checkRedDepositBCDecomposeIsLock(Context context) {
		boolean flag=false;
		String res="";
		try {
			res=this.rentFinanceService.checkRedDepositBCDecomposeIsLock(context);
		} catch (Exception e) {
		}
		
		if("Y".equals(res)) {
			flag=true;
		}
		Output.jsonFlageOutput(flag,context);
	}
	
	public void createRedDecomposeList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> redDecomposeList=null;
		
		try {
			redDecomposeList=this.rentFinanceService.getRedListByBillId(context);
		} catch (Exception e) {
			logger.debug("生成冲红单出错,请联系管理员!");
			context.errList.add("生成冲红单出错,请联系管理员"+e);
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		double totalPrice=0;
		for(int i=0;i<redDecomposeList.size();i++) {
			totalPrice=totalPrice+Double.valueOf(redDecomposeList.get(i).get("SHOULD_PRICE")+"");
		}
		outputMap.put("TOTAL_PRICE",totalPrice);
		outputMap.put("incomeId",context.contextMap.get("incomeId"));
		outputMap.put("recpId",context.contextMap.get("recpId"));
		outputMap.put("periodNum",context.contextMap.get("periodNum"));
		outputMap.put("resultList",redDecomposeList);
		Output.jspOutput(outputMap,context,"/rent/finance/redDecompose/redDecomposeList.jsp");
	}
	
	//生成冲红单,状态为提交财务
	public void commitRedDecomposeMoney(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		
		List<Map<String,Object>> redDecomposeList=null;
		try {
			redDecomposeList=this.rentFinanceService.getRedListByBillId(context);
			
			sqlMapClient.startTransaction();
			this.rentFinanceService.createRedDecompose(context,redDecomposeList,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("冲红单提交财务出错,请联系管理员!");
			context.errList.add("冲红单提交财务出错,请联系管理员"+e);
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		} else {
			this.queryRedDecomposeMoney(context);
		}
	}
	
	//自动分解,自动分解原则虚拟账户匹配,金额相等才会销帐
	public void autoDecompose(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		String msg="";
		try {
			msg=this.rentFinanceService.autoDecompose(context,sqlMapClient);
			outputMap.put("msg",msg);
		} catch (Exception e) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
			logger.debug("自动分解出错!");
			context.errList.add("自动分解出错,请联系管理员"+e);
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		} else {
			Output.jsonOutput(outputMap,context);
		}
	}
	
	public void autoConfirm(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			this.rentFinanceService.autoConfirm(context,sqlMapClient);
		} catch (Exception e) {
			logger.debug("确认通过所选分解单出错!");
			context.errList.add("确认通过所选分解单出错,请联系管理员"+e);
		} 
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
		} else {
			context.contextMap.put("msg","自动确认通过完成!");
			this.queryCommitFinanceMoney(context);
		}
	}
	
	//********************************************************************结清模组
	//结清申请页面
	public void queryContractList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		Map<String,Object> rsMap=null;
		
		outputMap.put("content",context.contextMap.get("content"));
		if(context.contextMap.get("recp_status")==null||"".equals(context.contextMap.get("recp_status"))) {
			context.contextMap.put("recp_status",-1);
		}
		outputMap.put("recp_status",context.contextMap.get("recp_status"));
		outputMap.put("normal_settlement",context.contextMap.get("normal_settlement"));
		outputMap.put("advance_settlement",context.contextMap.get("advance_settlement"));
		outputMap.put("process_settlement",context.contextMap.get("process_settlement"));
		
		context.contextMap.put("id",context.contextMap.get("s_employeeId"));
		try {
			rsMap=(Map)DataAccessor.query("employee.getEmpInforById",context.contextMap,DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) {
			logger.debug("获得结清合同列表出错!");
			context.errList.add("获得结清合同列表出错"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		context.contextMap.put("p_usernode",rsMap.get("NODE"));
		dw=baseService.queryForListWithPaging("rentFinance.getContractList",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/rentSettlement/contractList.jsp");
	}
	
	//结清审批页面
	public void queryCommitList(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		Map<String,Object> rsMap=null;
		//所有权限code
		List<Integer> allAuth = new ArrayList<Integer>();
		List<Map> dataType = null;
		Map<Integer, Map<String, String>> states = null;
		try {
			dataType = (List<Map>)DictionaryUtil.getDictionary("结清审批状态");
			states = this.rentFinanceService.getTypeState(dataType);
			context.contextMap.put("id",context.contextMap.get("s_employeeId"));
			rsMap=(Map)DataAccessor.query("employee.getEmpInforById",context.contextMap,DataAccessor.RS_TYPE.MAP);
			
			List<String> authList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,RS_TYPE.LIST);
			
			for(int i=0;authList!=null&&i<authList.size();i++) {
				if(authList.get(i)==null) {
					continue;
				}
				if(RentFinanceUtil.AUTHORITY.SALES_MANAGER.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "SALES_MANAGER"));
				} else if(RentFinanceUtil.AUTHORITY.SALES_DIRECT.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "SALES_DIRECT"));
				} else if(RentFinanceUtil.AUTHORITY.SERVICE_DIRECT.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "SERVICE_DIRECT"));
				} else if(RentFinanceUtil.AUTHORITY.GENERAL_MANAGER.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "GENERAL_MANAGER"));
				} else if(RentFinanceUtil.AUTHORITY.FINANCIAL.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "FINANCIAL"));
				} else if(RentFinanceUtil.AUTHORITY.FINANCIAL_STAFF.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "FINANCIAL_STAFF"));
				} else if(RentFinanceUtil.AUTHORITY.ACCOUNTANCY_STAFF.toString().equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "ACCOUNTANCY_STAFF"));
				} else if("UNIT_MANAGER".equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "UNIT_MANAGER"));
				} else if("SET_MANAGEMENT".equals(authList.get(i).toString())) {
					allAuth.add(this.rentFinanceService.getTypeStateCodeByEnName(dataType, "SET_MANAGEMENT"));
				}
			}
		} catch (Exception e) {
			logger.debug("获得结清审批列表出错!");
			context.errList.add("获得结清审批列表出错"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		if(context.contextMap.get("state")==null||"".equals(context.contextMap.get("state"))) {
			context.contextMap.put("state",0);
		}
		
		context.contextMap.put("allAuth", allAuth);
		context.contextMap.put("p_usernode",rsMap.get("NODE"));
		dw=baseService.queryForListWithPaging("rentFinance.queryCommitList",context.contextMap,"ID",ORDER_TYPE.DESC);
		
		outputMap.put("states",states);
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("state",context.contextMap.get("state"));
		outputMap.put("type",context.contextMap.get("type"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("dw",dw);
		outputMap.put("isMy", context.contextMap.get("isMy"));
		outputMap.put("allAuth", allAuth.toString());
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		
		Output.jspOutput(outputMap,context,"/rent/finance/rentSettlement/commitList.jsp");
	}
	
	//迁移数据
	public void qianYiSettlement(Context context) {
		List<Map> datas = null;
		SqlMapClient sqlMapper = null;
		try {
			sqlMapper = DataAccessor.getSession();
			sqlMapper.startTransaction();
			//迁移数据标识
			context.contextMap.put("Migration", "Migration");
			datas=(List<Map>)DataAccessor.query("rentFinance.queryCommitList",context.contextMap,RS_TYPE.LIST);
			for(Map d : datas){
				Map param = new HashMap();
				String remark = d.get("REMARK")==null?"":d.get("REMARK").toString();
				String[] remarks = remark.split("<br>");
				param.put("ID", d.get("ID"));
				//申请备注
				String applyRemark = remarks[0];
				if(!StringUtils.isEmpty(applyRemark)){
					String[] aa = applyRemark.split("\\|");
					String name = "";
					String aRemark = "";
					String time = null;
					int id = 0;
					if(aa.length < 2){
						aRemark = applyRemark;
					} else {
						name = aa[0].split("：")[0];
						aRemark = aa[0].split("：")[1];
						time = aa[1].split("：")[1];
						param.put("name", name);
						id = (Integer)DataAccessor.query("rentFinance.getUserIdByName",param,RS_TYPE.OBJECT);
					}
					param.put("APPLY_REMARK", aRemark);
					param.put("APPLY_TIME", time);
					param.put("APPLY_USER_ID", id);
					sqlMapper.update("rentFinance.updateQianYiSettlement", param);
				}
				int currentState = 0;
				int nextState = 0;
				int settlementId = Integer.parseInt(d.get("ID").toString());
				//审批信息
				boolean isValid = true;
				int realCount = 0;
				if(!StringUtils.isEmpty(d.get("FINANCIAL"))){
					realCount = 7;
				} else if(!StringUtils.isEmpty(d.get("GENERAL_MANAGER"))){
					realCount = 6;
				} else if(!StringUtils.isEmpty(d.get("SERVICE_DIRECT"))){
					realCount = 5;
				} else if(!StringUtils.isEmpty(d.get("SALES_DIRECT"))){
					realCount = 4;
				} else if(!StringUtils.isEmpty(d.get("SALES_MANAGER"))){
					realCount = 3;
				} else if(!StringUtils.isEmpty(d.get("ACCOUNTANCY_STAFF"))){
					realCount = 2;
				} else if(!StringUtils.isEmpty(d.get("FINANCIAL_STAFF"))){
					realCount = 1;
				} 
				int maybeCount = remarks.length;
				for(int i = 1; i <= realCount && isValid; i++){
					int opState = -1;
					String opTime = null;
					int opUserId = 0;
					int stateCode = 0;
					String opMsg = "";
					String rr = " ： | ： ";
					if(i < maybeCount){
						rr = remarks[i];
					}
					double shouldFine = d.get("SHOULD_FINE")==null?0:Double.parseDouble(d.get("SHOULD_FINE").toString());
					if(!StringUtils.isEmpty(rr)){
						String[] r = rr.split("\\|");
						if(r.length < 2){
							opMsg = rr;
						} else {
							String name = r[0].split("：")[0];
							opMsg = r[0].split("：")[1];
							opTime = r[1].split("：")[1];
							param.put("name", name);
							opUserId = (Integer)DataAccessor.query("rentFinance.getUserIdByName",param,RS_TYPE.OBJECT);
							stateCode = i*10;
							if (i==1 && "Y".equals(d.get("FINANCIAL_STAFF"))) {
								opState = 1;
							} else if(i==1 && "N".equals(d.get("FINANCIAL_STAFF"))){
								opState = 0;
								isValid = false;
								nextState = -1;
							} else if(i==2 && "Y".equals(d.get("ACCOUNTANCY_STAFF"))){
								opState = 1;
							} else if(i==2 && "N".equals(d.get("ACCOUNTANCY_STAFF"))){
								opState = 0;
								isValid = false;
								nextState = -1;
							} else if(i==3 && "Y".equals(d.get("SALES_MANAGER"))){
								opState = 1;
							} else if(i==3 && "N".equals(d.get("SALES_MANAGER"))){
								opState = 0;
								isValid = false;
								nextState = -1;
							} else if(i==4 && "Y".equals(d.get("SALES_DIRECT"))){
								opState = 1;
							} else if(i==4 && "N".equals(d.get("SALES_DIRECT"))){
								opState = 0;
								isValid = false;
								nextState = -1;
							} else if(i==5 && "Y".equals(d.get("SERVICE_DIRECT"))){
								//插入总经理前一条审批
								opState = 1;
//								param.put("opState", opState);
//								param.put("opTime", opTime);
//								param.put("opUserId", opUserId);
//								param.put("stateCode", stateCode);
//								param.put("opMsg", opMsg);
//								param.put("settlementId", settlementId);
								SettlementLogTO setLogTo = new SettlementLogTO();
								setLogTo.setOpState(opState);
								if(StringUtils.isEmpty(opTime)){
									setLogTo.setOpTime(null);
								} else {
									setLogTo.setOpTime(DateUtil.strToDate(opTime, "yyyy-MM-dd HH:mm:ss"));
								}
								setLogTo.setOpUserId(opUserId);
								setLogTo.setStateCode(stateCode);
								setLogTo.setOpMsg(opMsg);
								setLogTo.setSettlementId(settlementId);
								sqlMapper.insert("rentFinance.insertSettlementLog", setLogTo);

								//如果shouldFine <= 0直接插入总经理审批
								if(shouldFine <= 0){
//									param.put("opState", 1);
//									param.put("opTime", null);
//									param.put("opUserId", 0);
//									param.put("stateCode", 60);
//									param.put("opMsg", "没有应缴罚息,无需总经理审批");
//									param.put("settlementId", settlementId);
									setLogTo = new SettlementLogTO();
									setLogTo.setOpState(1);
									setLogTo.setOpTime(null);
									setLogTo.setOpUserId(0);
									setLogTo.setStateCode(60);
									setLogTo.setOpMsg("没有应缴罚息,无需总经理审批");
									setLogTo.setSettlementId(settlementId);
									sqlMapper.insert("rentFinance.insertSettlementLog", setLogTo);
									//阻止本轮insert
									opState = -1;
								}
							} else if(i==5 && "N".equals(d.get("SERVICE_DIRECT"))){
								opState = 0;
								isValid = false;
								nextState = -1;
							} else if(i==6){
								if(shouldFine <= 0){
									stateCode = 70;
									if("Y".equals(d.get("FINANCIAL"))){
										opState = 1;
										currentState = stateCode;
									} else if("N".equals(d.get("FINANCIAL"))){
										opState = 0;
										currentState = stateCode;
										nextState = -1;
									}
									isValid = false;
								} else {
									stateCode = 60;
									if("Y".equals(d.get("GENERAL_MANAGER"))){
										opState = 1;
									} else if("N".equals(d.get("GENERAL_MANAGER"))){
										opState = 0;
										isValid = false;
										nextState = -1;
									}
								}
							} else if(i==7){
								if("Y".equals(d.get("FINANCIAL"))){
									opState = 1;
									currentState = stateCode;
								} else if("N".equals(d.get("FINANCIAL"))){
									opState = 0;
									currentState = stateCode;
									nextState = -1;
								}
								isValid = false;
							}
						}
						currentState = stateCode;
					}
					if(opState >= 0){
//						param.put("opState", opState);
//						param.put("opTime", opTime);
//						param.put("opUserId", opUserId);
//						param.put("stateCode", stateCode);
//						param.put("opMsg", opMsg);
//						param.put("settlementId", settlementId);
						SettlementLogTO setLogTo = new SettlementLogTO();
						setLogTo.setOpState(opState);
						if(StringUtils.isEmpty(opTime)){
							setLogTo.setOpTime(null);
						} else {
							setLogTo.setOpTime(DateUtil.strToDate(opTime, "yyyy-MM-dd HH:mm:ss"));
						}
						setLogTo.setOpUserId(opUserId);
						setLogTo.setStateCode(stateCode);
						setLogTo.setOpMsg(opMsg);
						setLogTo.setSettlementId(settlementId);
						sqlMapper.insert("rentFinance.insertSettlementLog", setLogTo);
					}
				}
				//更新状态
				if(d.get("STATE")!= null && d.get("STATE").toString().equals("0")){
					nextState = currentState + 10;
					if(nextState != -1){
						nextState = nextState>=70?900:nextState;
					}
					param.put("ID", settlementId);
					param.put("currentState", nextState);
					sqlMapper.update("rentFinance.updateQianYiById", param);
					param.clear();
					nextState = 0;
				} else if(d.get("STATE")!= null && d.get("STATE").toString().equals("1")){
					//通过
					param.put("ID", settlementId);
					param.put("currentState", 900);
					sqlMapper.update("rentFinance.updateQianYiById", param);
					param.clear();
				} else if(d.get("STATE")!= null && d.get("STATE").toString().equals("-1")){
					//驳回
					param.put("ID", settlementId);
					param.put("currentState", -1);
					sqlMapper.update("rentFinance.updateQianYiById", param);
					param.clear();
				}
			}
			//处理操作时间为1900/1/1 0:00:00为null
			sqlMapper.update("rentFinance.updateQianYiTimeToNull", new HashMap());
			sqlMapper.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) { 
				e.printStackTrace();
			} 
		}
	}
	
	//获得正常结清详细信息
	public void getSettlementDetail(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> custInfo=null;
		Map<String,Object> settlementPrice=null;
		
		
		try {
			custInfo=this.rentFinanceService.getCustInfoByRecpId(context);//获得客户信息
			settlementPrice=this.rentFinanceService.getSettlementPrice(context);
		} catch (Exception e) {
			logger.debug("获得正常结清明细出错!"+e);
		}
		
		DecimalFormat f=new DecimalFormat();
		f.applyPattern("##,##0.00");
		custInfo.put("PAY_PRICE",f.format(custInfo.get("PAY_PRICE")));
		
		outputMap.put("custInfo",custInfo);
		outputMap.put("settlementPrice",settlementPrice);
		Output.jsonOutput(outputMap,context);
	}
	
	//查看结清单详情
	public void viewSettlementDetail(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> custInfo=null;
		Map<String,Object> settlementPrice=null;
		List<Map> settlementLog=null;
		String auth = "N";
		//所有权限拼接起来的字符串，用以数据库检索
		StringBuilder allAuth = new StringBuilder("");
		List<Map> dataType = null;
		Map<Integer, Map<String, String>> states = null;
		try {
			//所有状态
//			dataType = (List<Map>)DictionaryUtil.getDictionary("结清审批状态");
			dataType = (List<Map>)DataAccessor.query("rentFinance.getSettleDataDirectionAndUser", context.contextMap,RS_TYPE.LIST);
			for(Map m : dataType){
				m.put("CODE", Integer.parseInt(m.get("CODE")==null?"0":m.get("CODE").toString()));
			}
			//权限
			List<String> authList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,RS_TYPE.LIST);
			states = this.rentFinanceService.getTypeState(dataType);
			custInfo=this.rentFinanceService.getCustInfoByRecpId(context);//获得客户信息
			settlementPrice=this.rentFinanceService.getSettlementDetailById(context);
			//区域主管
			int leaderId = this.rentFinanceService.getAreaLeaderByDeptId(Integer.parseInt(settlementPrice.get("DEPARTMENT")==null?"0":settlementPrice.get("DEPARTMENT").toString()));
			UserTo userTo = userService.getUserById(leaderId);
			outputMap.put("areaLeaderName", userTo.getName());
			settlementLog = (List<Map>)DataAccessor.query("rentFinance.getSettlementLogBySetId",context.contextMap, RS_TYPE.LIST);
			//判断是否有通过驳回权限
			if(settlementPrice.get("STATE")!= null && settlementPrice.get("STATE").toString().equals("0")){
				//必要条件1：审批中
				int stateCode = Integer.parseInt(settlementPrice.get("STATE_CODE").toString());
				//当前签核所需要的权限
				String temAuth = states.get(stateCode)==null?"":states.get(stateCode).get("enName");
				for(int i=0;authList!=null&&!StringUtils.isEmpty(temAuth)&&i<authList.size();i++) {
					if(authList.get(i)==null) {
						continue;
					}
					if(temAuth.equals(authList.get(i).toString())) {
						auth = "Y";
						break;
					} 
				}
			}
		} catch (Exception e) {
			logger.debug("获得正常结清明细出错!"+e);
			e.printStackTrace();
		}

		outputMap.put("dataType",dataType);
		outputMap.put("auth",auth);
		outputMap.put("showOnly",context.contextMap.get("showOnly"));
		outputMap.put("states",states);
		outputMap.put("custInfo",custInfo);
		outputMap.put("settlementLog",settlementLog);
		outputMap.put("settlementPrice",settlementPrice);
		Output.jspOutput(outputMap,context,"/rent/finance/rentSettlement/showSettlementDetail.jsp");
	}
	
	//检查是否重复提交
	public void checkDuplicateCommit(Context context) {
		
		try {
			Output.jsonFlageOutput(this.rentFinanceService.checkDuplicateCommit(context),context);
		} catch (Exception e) {
			logger.debug("检查是否重复提交出错!"+e);
		}
	}
	//检查是否有在租金分解中
	public void checkPendingData(Context context) {
		try {
			Output.jsonFlageOutput(this.rentFinanceService.checkPendingData(context),context);
		} catch (Exception e) {
			logger.debug("检查是否有在租金分解中!"+e);
		}
	}
	
	//业务员提交结清单
	public void commitSettlement(Context context) {
		
		Map<String,Object> resMap=null;
		try {
			context.contextMap.put("recpId",context.contextMap.get("recp_id"));
			resMap=this.rentFinanceService.getCustInfoByRecpId(context);
			resMap.put("remark",context.contextMap.get("remark"));
			//获取结清审批的第一个状态码,默认会计
			int firstState = 20;
			int firstUserId = 0;
			List<Map> dictionary = (List<Map>) DictionaryUtil.getDictionary("结清审批状态");
			if(dictionary != null && dictionary.size() > 0){
				firstState = Integer.parseInt(dictionary.get(0).get("CODE")==null?"10":dictionary.get(0).get("CODE").toString());
				firstUserId = Integer.parseInt(dictionary.get(0).get("REMARK")==null?"0":dictionary.get(0).get("REMARK").toString());
			}
			context.contextMap.put("firstState", firstState);
			context.contextMap.put("firstUserId", firstUserId);
			this.rentFinanceService.insertSettlement(context);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			switch (firstState) {
				case 10://财务专员
					mailSettingTo.setEmailSubject("结清单提交财务专员");
					mailSettingTo.setEmailContent(this.createMailContent(resMap));
					this.mailUtilService.sendMail(16, mailSettingTo);
					break;
				case 20://会计专员
					mailSettingTo.setEmailSubject("结清单提交会计专员");
					mailSettingTo.setEmailContent(this.createMailContent(resMap));
					this.mailUtilService.sendMail(17,mailSettingTo);
					break;
				default:
					mailSettingTo.setEmailContent(this.createMailContent(resMap));
					UserTo userTo = userService.getUserById(firstUserId);
					mailSettingTo.setEmailTo(userTo.getEmail());
					mailSettingTo.setEmailSubject("有一张结清单等待您审批");
					this.mailUtilService.sendMail(mailSettingTo);
					break;
			}
			
		} catch (Exception e) {
			logger.debug("业务员提交结清单出错!"+e);
			context.errList.add("业务员提交结清单出错"+e);
			Map<String,Object> outputMap=new HashMap<String,Object>();
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		/*MailSettingTo mailSettingTo=new MailSettingTo();  3月5日开会,财务需要增加财务专员审核,须在单位主管之前
		mailSettingTo.setEmailTo((String)context.contextMap.get("UPPER_EMAIL"));
		mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
		mailSettingTo.setEmailSubject("结清单提交业务主管审批");
		mailSettingTo.setCreateBy(context.contextMap.get("s_employeeId").toString());
		mailSettingTo.setEmailContent(this.createMailContent(resMap));
		this.mailUtilService.sendMail(mailSettingTo);*/
		this.queryContractList(context);
	}
	
	//获得支付表的历史结清清单
	public void getSettlementHistoryByRecpId(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SettlementTO result=null;
		List<Map> dataType = null;
		try {
//			resultList=this.rentFinanceService.getSettlementHistoryByRecpId(context);
			result = (SettlementTO)DataAccessor.query("rentFinance.getSettlementingByRecpId",context.contextMap, RS_TYPE.OBJECT);
			dataType = (List<Map>)DictionaryUtil.getDictionary("结清审批状态");
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("dataType",dataType);
		outputMap.put("result",result);
		Output.jspOutput(outputMap,context,"/rent/finance/rentSettlement/settlementHistoryNew.jsp");
	}
	
	public void approveSettlement(Context context) {
		
		Map<String,Object> resMap=null;
		context.contextMap.put("result","Y");
		String column = "";
		try {
			SettlementTO m = this.rentFinanceService.getSettlementById(context);
//			int StateCode = m.getStateCode();
			context.contextMap.put("settlementTO", m);
			context.contextMap.put("RECP_ID", m.getRecpId());
//			List<Map> dataType = (List<Map>)DictionaryUtil.getDictionary("结清审批状态");
			List<Map> dataType = (List<Map>)DataAccessor.query("rentFinance.getSettleDataDirectionAndUser", context.contextMap,RS_TYPE.LIST);
//			//权限
//			List<String> authList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,RS_TYPE.LIST);
			Map<Integer, Map<String, String>> states = this.rentFinanceService.getTypeState(dataType);
			column = states.get(m.getStateCode())==null?"":states.get(m.getStateCode()).get("enName");
			context.contextMap.put("column", column);
			context.contextMap.put("dataType",dataType);
			
			SettlementTO newSettlement = this.rentFinanceService.approveOrRejectSettlement(context);
			
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setCreateBy(context.contextMap.get("s_employeeId").toString());
			resMap=this.rentFinanceService.getCustInfoByRecpId(context);
			resMap.put("remark",m.getApplyRemark());
			//发送邮件
			if(dataType != null && dataType.size() > 0){
				int firstState = Integer.parseInt(dataType.get(0).get("CODE")==null?"20":dataType.get(0).get("CODE").toString());
				if(newSettlement.getStateCode() != firstState){
					switch (newSettlement.getStateCode()) {
					case 10://财务专员
						mailSettingTo.setEmailSubject("结清单提交财务专员");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(16, mailSettingTo);
						break;
					case 20://会计专员
						mailSettingTo.setEmailSubject("结清单提交会计专员");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(17,mailSettingTo);
						break;
					case 30://单位主管
						mailSettingTo.setEmailSubject("结清单提交单位主管");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						UserTo userTo = userService.getUserById(newSettlement.getCurrentUserId());
						mailSettingTo.setEmailTo(userTo.getEmail());
						this.mailUtilService.sendMail(mailSettingTo);
						break;
					case 40://业务副总
						mailSettingTo.setEmailSubject("结清单提交业务副总");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(18,mailSettingTo);
						break;
					case 50://业管部
						mailSettingTo.setEmailSubject("结清单提交业管部");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(11,mailSettingTo);
						break;
					case 60://总经理
						mailSettingTo.setEmailSubject("结清单提交总经理");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(13,mailSettingTo);
						break;
					case 70://财管部
						mailSettingTo.setEmailSubject("结清单提交财管部");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						this.mailUtilService.sendMail(12,mailSettingTo);
						break;
					case 80://区域主管
//						mailSettingTo.setEmailContent(this.createMailContent(resMap));
//						String decpId = resMap.get("DECP_ID")==null?"":resMap.get("DECP_ID").toString();
//						//成都和重庆发邮件给龙哥，其他给刘副总
//						if(decpId.equals("9") || decpId.equals("14")){
//							mailSettingTo.setEmailTo("jacky@tacleasing.cn");
//						} else {
//							mailSettingTo.setEmailTo("david.liu@tacleasing.cn");
//						}
//						this.mailUtilService.sendMail(mailSettingTo);
//						break;
						mailSettingTo.setEmailSubject("结清单提交区域主管");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						UserTo userTo1 = userService.getUserById(newSettlement.getCurrentUserId());
						mailSettingTo.setEmailTo(userTo1.getEmail());
						this.mailUtilService.sendMail(mailSettingTo);
						break;
					case 90://经管处
						mailSettingTo.setEmailSubject("结清单提交经管处");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						UserTo userTo3 = userService.getUserById(newSettlement.getCurrentUserId());
						mailSettingTo.setEmailTo(userTo3.getEmail());
						this.mailUtilService.sendMail(mailSettingTo);
						break;
					case 900:
						//审批完成最后一步发邮件
						if(context.contextMap.get("opType").equals("1")){
							if(m.getTotalPayPrice()>0) {//申请还款金额大于0,需要发送销租金通知
								mailSettingTo.setEmailContent(this.createMailContent1(resMap));
								this.mailUtilService.sendMail(14,mailSettingTo);
							} else {//小于等于0,需要通知正常结清直接通过,支付表更新为正常结清状态
								mailSettingTo.setEmailTo(this.rentFinanceService.getEmailByRecpId(context.contextMap));
								mailSettingTo.setEmailSubject("支付表更新为正常结清");
								mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
								mailSettingTo.setEmailContent(this.createMailContent2(resMap));
								this.mailUtilService.sendMail(mailSettingTo);
							}
						}
						break;
					default:
						mailSettingTo.setEmailSubject("有一张结清单等待您审批");
						mailSettingTo.setEmailContent(this.createMailContent(resMap));
						UserTo userTo2 = userService.getUserById(newSettlement.getCurrentUserId());
						mailSettingTo.setEmailTo(userTo2.getEmail());
						this.mailUtilService.sendMail(mailSettingTo);
						break;
					}
				}
			}
			
//			if(RentFinanceUtil.AUTHORITY.SALES_MANAGER.toString().equals(context.contextMap.get("column"))) {//业务主管审批通过
//				mailSettingTo.setEmailSubject("结清单提交业务副总");
//				mailSettingTo.setEmailContent(this.createMailContent(resMap));
//				this.mailUtilService.sendMail(18,mailSettingTo);
//			} else if(RentFinanceUtil.AUTHORITY.SALES_DIRECT.toString().equals(context.contextMap.get("column"))) {//业务副总审批通过
//				mailSettingTo.setEmailContent(this.createMailContent(resMap));
//				this.mailUtilService.sendMail(11,mailSettingTo);
//			} else if(RentFinanceUtil.AUTHORITY.SERVICE_DIRECT.toString().equals(context.contextMap.get("column"))) {//业管部审批通过
//				if(m.getTotalPayPrice()>0) {
//					//如果应缴金额大于0 需要提交总经理审批
//					mailSettingTo.setEmailContent(this.createMailContent(resMap));
//					this.mailUtilService.sendMail(13,mailSettingTo);
//				} else {//直接提交财务部审批
//					mailSettingTo.setEmailContent(this.createMailContent(resMap));
//					this.mailUtilService.sendMail(12,mailSettingTo);
//				}
//			} else if(RentFinanceUtil.AUTHORITY.GENERAL_MANAGER.toString().equals(context.contextMap.get("column"))) {//总经理审批通过
//				mailSettingTo.setEmailContent(this.createMailContent(resMap));
//				this.mailUtilService.sendMail(12,mailSettingTo);
//			} else if(RentFinanceUtil.AUTHORITY.FINANCIAL_STAFF.toString().equals(context.contextMap.get("column"))) {
//				mailSettingTo.setEmailContent(this.createMailContent(resMap));
//				this.mailUtilService.sendMail(17,mailSettingTo);
//			}  else if(RentFinanceUtil.AUTHORITY.ACCOUNTANCY_STAFF.toString().equals(context.contextMap.get("column"))) {
//				mailSettingTo.setEmailContent(this.createMailContent(resMap));
//				mailSettingTo.setEmailTo(resMap.get("EMAIL")+"");
//				this.mailUtilService.sendMail(mailSettingTo);
//			} else if(RentFinanceUtil.AUTHORITY.FINANCIAL.toString().equals(context.contextMap.get("column"))) {//财务部审批通过
//				if(m.getTotalPayPrice()>0) {//申请还款金额大于0,需要发送销租金通知
//					mailSettingTo.setEmailContent(this.createMailContent1(resMap));
//					this.mailUtilService.sendMail(14,mailSettingTo);
//				} else {//小于等于0,需要通知正常结清直接通过,支付表更新为正常结清状态
//					mailSettingTo.setEmailTo(this.rentFinanceService.getEmailByRecpId(context.contextMap));
//					mailSettingTo.setEmailSubject("支付表更新为正常结清");
//					mailSettingTo.setEmailCc((String)context.contextMap.get("EMAIL"));
//					mailSettingTo.setEmailContent(this.createMailContent2(resMap));
//					this.mailUtilService.sendMail(mailSettingTo);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("通过结清单出错!"+e);
		}
		Output.jsonFlageOutput(true,context);
	}
	
	public void rejectSettlement(Context context) {
		
		context.contextMap.put("result","N");
		try {
			SettlementTO m = this.rentFinanceService.getSettlementById(context);
			context.contextMap.put("settlementTO", m);
			context.contextMap.put("RECP_ID", m.getRecpId());
			this.rentFinanceService.approveOrRejectSettlement(context);
			
			Map<String,Object> mail=this.rentFinanceService.getSettlementDetailByIdForEmail(context);
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("结清驳回");
			mailSettingTo.setEmailTo(mail.get("EMAIL")+"");
			mailSettingTo.setEmailContent("合同号："+mail.get("LEASE_CODE")+"的案件结清审批被驳回！");
			this.mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			logger.debug("驳回结清单出错!"+e);
		}
		Output.jsonFlageOutput(true,context);
	}
	
	private String createMailContent(Map<String,Object> resMap) {
		
		if(resMap==null) {
			return "";
		}
		StringBuffer mailContent=new StringBuffer();
		mailContent.append("<html><head></head>");
		
		mailContent.append("<style>.grid_table th {"+
							"border:solid #A6C9E2;"+
							"border-width:0 1px 1px 0;"+
							"background-color: #E1EFFB;"+
							"padding : 2;"+
							"margin : 1;"+
							"font-weight: bold;"+
							"text-align: center;"+
							"color: #2E6E9E;"+
							"height: 28px;"+
							"font-size: 14px;"+
							"font-family: '微软雅黑';"+
							"}" +
							".grid_table td {"+
							"border:solid #A6C9E2;"+
						    "border-width:0 1px 1px 0;"+
						    "text-align: center;"+
							"white-space: nowrap;"+
							"overflow: hidden;"+
							"background-color: #FFFFFF;"+
							"padding : 5px 5px;"+
							"font-size: 12px;"+
							"font-weight: normal;"+
							"color: black;"+
							"font-family: '微软雅黑';"+
							"}" +
							".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
		mailContent.append("<font class='ff'>Greeting:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下案件的结清单需您审批！</font><br>");
		mailContent.append("<table class='grid_table'><tr><th>案件号</th><th>合同号</th><th>期数</th><th>客户名称</th></tr>"+
						   "<tr><td style='text-align: center;'>"+resMap.get("CREDIT_RUNCODE")+"</td><td style='text-align: center;'>"+resMap.get("LEASE_CODE")+"</td>" +
							   "<td style='text-align: center;'>"+resMap.get("PERIOD_NUM")+"</td><td style='text-align: center;'>"+resMap.get("CUST_NAME")+"</td></tr>" +
							   		"<tr><th colspan='4'>备注</th></tr><tr><td colspan='4' style='text-align: left'>"+resMap.get("remark")+"</td></tr></table>" +
							   		"</body></html>");
		
		return mailContent.toString();
	}
	
	private String createMailContent1(Map<String,Object> resMap) {//用于最后结清全部通过后通知业管人员销帐
		
		if(resMap==null) {
			return "";
		}
		StringBuffer mailContent=new StringBuffer();
		mailContent.append("<html><head></head>");
		
		mailContent.append("<style>.grid_table th {"+
							"border:solid #A6C9E2;"+
							"border-width:0 1px 1px 0;"+
							"background-color: #E1EFFB;"+
							"padding : 2;"+
							"margin : 1;"+
							"font-weight: bold;"+
							"text-align: center;"+
							"color: #2E6E9E;"+
							"height: 28px;"+
							"font-size: 14px;"+
							"font-family: '微软雅黑';"+
							"}" +
							".grid_table td {"+
							"border:solid #A6C9E2;"+
						    "border-width:0 1px 1px 0;"+
						    "text-align: center;"+
							"white-space: nowrap;"+
							"overflow: hidden;"+
							"background-color: #FFFFFF;"+
							"padding : 5px 5px;"+
							"font-size: 12px;"+
							"font-weight: normal;"+
							"color: black;"+
							"font-family: '微软雅黑';"+
							"}" +
							".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
		mailContent.append("<font class='ff'>Greeting:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下案件的结清单已经审批通过,请进入系统操作结清销帐！</font><br>");
		mailContent.append("<table class='grid_table'><tr><th>案件号</th><th>合同号</th><th>期数</th><th>客户名称</th></tr>"+
						   "<tr><td style='text-align: center;'>"+resMap.get("CREDIT_RUNCODE")+"</td><td style='text-align: center;'>"+resMap.get("LEASE_CODE")+"</td>" +
							   "<td style='text-align: center;'>"+resMap.get("PERIOD_NUM")+"</td><td style='text-align: center;'>"+resMap.get("CUST_NAME")+"</td></tr>" +
							   		"<tr><th colspan='4'>备注</th></tr><tr><td colspan='4' style='text-align: left'>"+resMap.get("remark")+"</td></tr></table>" +
							   		"</body></html>");
		
		return mailContent.toString();
	}
	
	private String createMailContent2(Map<String,Object> resMap) {//用于最后结清金额为0,直接进行支付表结清
		
		if(resMap==null) {
			return "";
		}
		StringBuffer mailContent=new StringBuffer();
		mailContent.append("<html><head></head>");
		
		mailContent.append("<style>.grid_table th {"+
							"border:solid #A6C9E2;"+
							"border-width:0 1px 1px 0;"+
							"background-color: #E1EFFB;"+
							"padding : 2;"+
							"margin : 1;"+
							"font-weight: bold;"+
							"text-align: center;"+
							"color: #2E6E9E;"+
							"height: 28px;"+
							"font-size: 14px;"+
							"font-family: '微软雅黑';"+
							"}" +
							".grid_table td {"+
							"border:solid #A6C9E2;"+
						    "border-width:0 1px 1px 0;"+
						    "text-align: center;"+
							"white-space: nowrap;"+
							"overflow: hidden;"+
							"background-color: #FFFFFF;"+
							"padding : 5px 5px;"+
							"font-size: 12px;"+
							"font-weight: normal;"+
							"color: black;"+
							"font-family: '微软雅黑';"+
							"}" +
							".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
		mailContent.append("<font class='ff'>Greeting:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下案件的申请结清的金额为0,系统自动将其更新为正常结清状态！</font><br>");
		mailContent.append("<table class='grid_table'><tr><th>案件号</th><th>合同号</th><th>期数</th><th>客户名称</th></tr>"+
						   "<tr><td style='text-align: center;'>"+resMap.get("CREDIT_RUNCODE")+"</td><td style='text-align: center;'>"+resMap.get("LEASE_CODE")+"</td>" +
							   "<td style='text-align: center;'>"+resMap.get("PERIOD_NUM")+"</td><td style='text-align: center;'>"+resMap.get("CUST_NAME")+"</td></tr>" +
							   		"<tr><th colspan='4'>备注</th></tr><tr><td colspan='4' style='text-align: left'>"+resMap.get("remark")+"</td></tr></table>" +
							   		"</body></html>");
		
		return mailContent.toString();
	}
	
	//获得租金分解的客户信息
	public void queryCustInfo(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> custList=null;
		
		try {
			custList=this.rentFinanceService.getCustInfo();
		} catch(Exception e) {
			logger.debug("获得租金分解客户信息出错!"+e);
			context.errList.add("获得租金分解客户信息出错,请联系管理员");
		}
		
		outputMap.put("custList",custList);
		if(context.errList.isEmpty()) {
			Output.jsonOutput(outputMap,context);
		}
	}
	//获得结清审批通过待分解的客户信息
	public void querySettlementCustInfo(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> custList=null;
		
		try {
			custList=this.rentFinanceService.getSettlementCustInfo();
		} catch(Exception e) {
			logger.debug("获得结清审批通过待分解的客户信息出错!"+e);
			context.errList.add("获得结清审批通过待分解的客户信息出错,请联系管理员");
		}
		
		outputMap.put("custList",custList);
		if(context.errList.isEmpty()) {
			Output.jsonOutput(outputMap,context);
		}
	}
	
	//获得结清分解列表
	public void showSettlementList(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> custMap=new HashMap<String,Object>();
		List<Map<String,Object>> settlementList=null;
		try {
			custMap=this.rentFinanceService.getIncomeInfoByIncomeId(context);
			settlementList=this.rentFinanceService.getSettlementDetailByRecpId(context);
			custMap.put("RECP_ID",context.contextMap.get("recpId"));
			outputMap.put("custMap",custMap);
			outputMap.put("settlementList",settlementList);
		} catch (Exception e) {
			logger.debug("获得结清分解列表出错"+e);
			context.errList.add("获得结清分解列表出错");
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		Output.jspOutput(outputMap,context,"/rent/finance/rentSettlement/querySettlementList.jsp");
	}
	
	//插入结清分解单
	public void createSettlementDetailList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		List<Map<String,Object>> resultList=null;
		try {
			sqlMapClient.startTransaction();
			resultList=this.rentFinanceService.createSettlementDetailList(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("生成租金分解明细数据出错,请联系管理员!");
			context.errList.add("生成租金分解明细数据出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}//进行租金分解
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/rent/finance/rentDecompose/commitDecomposeList.jsp");//共用租金分解的页面
	}
	
	//结清分解财务通过
	public void confirmSettlementDecompose(Context context) {
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			context.contextMap.put("recpId",context.contextMap.get("recpId_"));
			Double shouldPayTotalPrice=this.rentFinanceService.getSettlementPayTotalPriceByRecpId(context,sqlMapClient);
			this.rentFinanceService.confirmDecompose(context,sqlMapClient);//更新租金分解表
			if(Double.valueOf(context.contextMap.get("totalDecomposePrice").toString())-shouldPayTotalPrice==0) {//此案子的结清金额全部销完,需要更新支付表结清状态
				this.rentFinanceService.updateRecpStatus(context,sqlMapClient);
			} else {
				//不需要更新支付表recp_status
			}
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("租金分解更新财务通过出错,请联系管理员!");
			context.errList.add("租金分解更新财务通过出错,请联系管理员"+e);
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				logger.debug("事务关闭异常");
			}
		}
		
		if(!context.errList.isEmpty()) {
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		context.contextMap.put("menu","confirmDecompose");
		this.queryCommitFinanceMoney(context);
		
	}
	
	//结清分解财务驳回
	public void rejectSettlementDecompose(Context context) {
		this.rejectDecompose(context);//和租金分解的驳回相同
	}
	
	//***********************************************************保证金B,C红冲
	public void queryDepositBCList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		dw=baseService.queryForListWithPaging("rentFinance.getDepositBCList",context.contextMap,"DECOMPOSE_TIME",ORDER_TYPE.DESC);
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("dFromDate",context.contextMap.get("dFromDate"));
		outputMap.put("dToDate",context.contextMap.get("dToDate"));
		outputMap.put("__action","rentFinanceCommand.queryDepositBCList");
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/redDecompose/redDepositBCList.jsp");
	}
	
	//***********************************************************保证金C红冲,业管用
	public void queryDepositCList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		dw=baseService.queryForListWithPaging("rentFinance.getDepositCList",context.contextMap,"DECOMPOSE_TIME",ORDER_TYPE.DESC);
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("dFromDate",context.contextMap.get("dFromDate"));
		outputMap.put("dToDate",context.contextMap.get("dToDate"));
		outputMap.put("__action","rentFinanceCommand.queryDepositCList");
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/redDecompose/redDepositBCList.jsp");
	}
	
	public void createDepositBCRed(Context context) {

		Map<String,Object> outputMap=new HashMap<String,Object>();
		try {
			this.rentFinanceService.redDecomposeDepositBC(context);
		} catch (Exception e) {
			
		}
		
		outputMap.put("msg","红冲成功");
		Output.jsonOutput(outputMap,context);
	}
	
	//本金列印
	public void queryPrincipalList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		if(context.contextMap.get("isPrint")==null) {
			context.contextMap.put("isPrint",-1);
		}
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("isPrint",context.contextMap.get("isPrint"));
		
		dw=baseService.queryForListWithPaging("rentFinance.getPrincipalList",context.contextMap,"INVOICE_CODE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/finance/principal/principalList.jsp");
	}
	//导出Excel
	public static List<Map<String,Object>> principalExcel(String isPrint,String fromDate,String toDate,String fromMoney,String toMoney,String companyCode) {
		
		Map<String,String> param=new HashMap<String,String>();
		param.put("isPrint",isPrint);
		param.put("fromDate",fromDate);
		param.put("toDate",toDate);
		param.put("fromMoney",fromMoney);
		param.put("toMoney",toMoney);
		param.put("companyCode",companyCode);
		List<Map<String,Object>> resultList=null;
		
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getPrincipalList",param,RS_TYPE.LIST);
		} catch (Exception e) {
		}
		
		return resultList;
	}
	
	public void generatePrincipal(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> principalList=null;
		String principalCode=null;
		
		try {
			principalList=this.rentFinanceService.getNullPrincipalList(context);
			
			for(int i=0;principalList!=null&&i<principalList.size();i++) {
				principalCode=CodeRule.genePrincipalRunCode();
				principalList.get(i).put("PRINCIPAL_CODE",principalCode);
				this.rentFinanceService.updateNullPrincipalByBillId(principalList.get(i));
			}
		} catch (Exception e) {
			logger.debug("生成本金收据号码出错"+e);
		}
		
		if(principalList==null||principalList.size()==0) {
			outputMap.put("msg","无本金收据号码需生成!");
		} else {
			outputMap.put("msg","生成本金收据号码完成!");
		}
		Output.jsonOutput(outputMap,context);
	}
	
	//***********************************************************************************************
	//暂收款管理
	public void queryFundList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		List<String> authList=null;
		try {
			authList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug("暂收款管理查询出错!"+e);
		}
		
		for(int i=0;authList!=null&&i<authList.size();i++) {
			if(authList.get(i)==null) {
				continue;
			}
			if(RentFinanceUtil.AUTHORITY.CLAIM.toString().equals(authList.get(i).toString())) {
				outputMap.put("CLAIM","Y");
			} else if(RentFinanceUtil.AUTHORITY.REFUND.toString().equals(authList.get(i).toString())) {
				outputMap.put("REFUND","Y");
			} else if(RentFinanceUtil.AUTHORITY.UPLOAD.toString().equals(authList.get(i).toString())) {
				outputMap.put("UPLOAD","Y");
			} else if(RentFinanceUtil.AUTHORITY.MODIFY_REMARK.toString().equals(authList.get(i).toString())) {
				outputMap.put("MODIFY_REMARK","Y");
			} else if(RentFinanceUtil.AUTHORITY.APPROVE.toString().equals(authList.get(i).toString())) {
				outputMap.put("APPROVE","Y");
			} else if(RentFinanceUtil.AUTHORITY.REJECT.toString().equals(authList.get(i).toString())) {
				outputMap.put("REJECT","Y");
			}
		}
		
		if(StringUtils.isEmpty(context.contextMap.get("hasRemark"))) {
			context.contextMap.put("hasRemark",0);
		}
		if(StringUtils.isEmpty(context.contextMap.get("hasFile"))) {
			context.contextMap.put("hasFile",0);
		}
		if (StringUtils.isEmpty(context.contextMap.get("initCount"))) {
			context.contextMap.put("initCount", "0");
		}
		List<Map<String, String>> companys = LeaseUtil.getCompanys();
		if(context.contextMap.get("companyCode")!=null && !"".equals(context.contextMap.get("companyCode"))){
			for(Map<String,String> c:companys){
				if(c.get("code").equals(context.contextMap.get("companyCode"))){
					context.contextMap.put("companyName",c.get("name"));
				}
			}
		}
		dw=baseService.queryForListWithPaging("rentFinance.queryFundList",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("restFromMoney",context.contextMap.get("restFromMoney"));
		outputMap.put("restToMoney",context.contextMap.get("restToMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("hasRemark",context.contextMap.get("hasRemark"));
		outputMap.put("hasFile",context.contextMap.get("hasFile"));
		outputMap.put("fromCount",context.contextMap.get("fromCount"));
		outputMap.put("toCount",context.contextMap.get("toCount"));
		outputMap.put("initCount",context.contextMap.get("initCount"));
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys",companys );	
		
		outputMap.put("dw",dw);
		Output.jspOutput(outputMap,context,"/rent/finance/fund/fundList.jsp");
	}
	
	//检查认领金额或者退款金额是否大于剩余金额
	public void checkClaimRefundAmount(Context context) {
		try {
			Output.jsonFlageOutput(this.rentFinanceService.checkClaimRefundAmount(context),context);
		} catch (Exception e) {
			
		}
	}
	
	//提交认领款申请
	public void commitClaim(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=null;
		try {
			sqlMapClient=DataAccessor.getSession();
			sqlMapClient.startTransaction();
			this.rentFinanceService.commitClaim(context,sqlMapClient);
			sqlMapClient.commitTransaction();
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailCc(context.contextMap.get("EMAIL").toString());
			mailSettingTo.setEmailContent("Hi: 有新的认退申请提交，麻烦进入系统确认！");
			mailUtilService.sendMail(19,mailSettingTo);
		} catch (Exception e) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
			logger.debug("申请认领款出错,请联系管理员!"+e);
			context.errList.add("申请认领款出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}		
		this.queryFundList(context);
	}
	
	//提交退款
	public void commitRefund(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=null;
		try {
			sqlMapClient=DataAccessor.getSession();
			sqlMapClient.startTransaction();
			this.rentFinanceService.commitRefund(context,sqlMapClient);
			sqlMapClient.commitTransaction();
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailCc(context.contextMap.get("EMAIL").toString());
			mailSettingTo.setEmailContent("Hi: 有新的认退申请提交，麻烦进入系统确认！");
			mailUtilService.sendMail(19,mailSettingTo);
		} catch (Exception e) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
			logger.debug("申请退款出错,请联系管理员!"+e);
			context.errList.add("申请退款出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}		
		this.queryFundList(context);
	}
	
	public void showClaimRefundList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<String> authList=null;
		try {
			resultList=this.rentFinanceService.showClaimRefundList(context);
			for(int i=0;resultList!=null&&i<resultList.size();i++){//判断是否有直租的
				Map paramMap = new HashMap();
				paramMap.put("ID", resultList.get(i).get("ID"));
				String account = (String) rentFinanceService.queryForObj("rentFinance.getRecpAccountByRefundId", paramMap);
				if(LeaseUtil.getCompanyBankAccountByCompanyCode(2).equals(account)){
					resultList.get(i).put("company_code", 2);
				}else{
					resultList.get(i).put("company_code", 1);
				}
			}
			authList=(List<String>)DataAccessor.query("common.getPermissions",context.contextMap,RS_TYPE.LIST);
		} catch (Exception e) {
			logger.debug("获得暂收款列表出错!"+e);
			context.errList.add("获得暂收款列表出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		for(int i=0;authList!=null&&i<authList.size();i++) {
			if(authList.get(i)==null) {
				continue;
			}
			if(RentFinanceUtil.AUTHORITY.CLAIM.toString().equals(authList.get(i).toString())) {
				outputMap.put("CLAIM","Y");
			} else if(RentFinanceUtil.AUTHORITY.REFUND.toString().equals(authList.get(i).toString())) {
				outputMap.put("REFUND","Y");
			} else if(RentFinanceUtil.AUTHORITY.UPLOAD.toString().equals(authList.get(i).toString())) {
				outputMap.put("UPLOAD","Y");
			} else if(RentFinanceUtil.AUTHORITY.MODIFY_REMARK.toString().equals(authList.get(i).toString())) {
				outputMap.put("MODIFY_REMARK","Y");
			} else if(RentFinanceUtil.AUTHORITY.APPROVE.toString().equals(authList.get(i).toString())) {
				outputMap.put("APPROVE","Y");
			} else if(RentFinanceUtil.AUTHORITY.REJECT.toString().equals(authList.get(i).toString())) {
				outputMap.put("REJECT","Y");
			}
		}
		
		outputMap.put("resultList",resultList);
		Output.jspOutput(outputMap,context,"/rent/finance/fund/fundDetailList.jsp");
	}
	
	public void approveFund(Context context) {
		SqlMapClient sqlMapClient=null;
		try {
			sqlMapClient=DataAccessor.getSession();
			sqlMapClient.startTransaction();
			this.rentFinanceService.approveFund(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("暂收款认退审批出错,请联系管理员!"+e);
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
				
			}
		}
		Output.jsonFlageOutput(true,context);
	}
	
	public void approveOrRejectFund(Context context) {
		
		SqlMapClient sqlMapClient=null;
		try {
			sqlMapClient=DataAccessor.getSession();
			sqlMapClient.startTransaction();
			this.rentFinanceService.approveOrRejectFund(context,sqlMapClient);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			logger.debug("暂收款认退审批出错,请联系管理员!"+e);
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
				
			}
		}
		Output.jsonFlageOutput(true,context);
	}
	
	public void addRemark(Context context) {
		
		try {
			this.rentFinanceService.addRemark(context);
		} catch (Exception e) {
			logger.debug("添加来款备注出错!"+e);
		}
		
		Output.jsonFlageOutput(true,context);
	}
	
	public void saveFileToDiskAndDB(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			sqlMapClient.startTransaction();
			this.rentFinanceService.saveFileToDiskAndDB(context,sqlMapClient);
			sqlMapClient.commitTransaction();
			Map<String,Object> result=this.rentFinanceService.getCashIncome(context);//上传水单后发送邮件,获得来款户名
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailTo("HW@tacleasing.cn");
			mailSettingTo.setEmailSubject("水单上传");
			mailSettingTo.setEmailContent("<font style='font-style:微软雅黑'>大家好:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;暂收款管理中来款户名为"+result.get("INCOME_NAME")+"有水单上传,请进入系统查看!</font>");
			this.mailUtilService.sendMail(mailSettingTo);
		} catch (Exception e) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
			logger.debug("上传水单出错,请联系管理员!"+e);
			context.errList.add("上传水单出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		this.queryFundList(context);
	}
	
	//获得水单列表by incomeId
	public void showUploadFileList(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> uploadList=null;
		
		try {
			uploadList=this.rentFinanceService.getUploadFileList(context);
		} catch (Exception e) {
			logger.debug("获得水单附件列表出错!"+e);
			context.errList.add("获得水单附件列表出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
		
		outputMap.put("uploadList",uploadList);
		Output.jspOutput(outputMap,context,"/rent/finance/fund/uploadFileList.jsp");
	}
	
	//查看上传的水单
	public void viewFile(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> fileInfo=new HashMap<String,Object>();
		try {
			fileInfo=this.rentFinanceService.getUploadFileList(context).get(0);
			this.rentFinanceService.viewFile(context,fileInfo);
		} catch (Exception e) {
			logger.debug("查看水单出错!"+e);
			context.errList.add("查看水单出错,请联系管理员!"+e);
			outputMap.put("errList",context.errList);
			Output.jspOutput(outputMap,context,"/error.jsp");
			return;
		}
	}
	
	//销帐日报表,现金销帐
	public void queryCashDailyReport(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> taxPlanList=null;
		List<Map<String,Object>> productionTypeList=null;
		
		outputMap.put("cardFlag",0);
		
		try {
			context.contextMap.put("dataType","税费方案");
			taxPlanList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			context.contextMap.put("dataType","产品类别");
			productionTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			
			outputMap.put("__action","rentFinanceCommand.queryCashDailyReport");
			if(StringUtils.isEmpty(context.contextMap.get("queryDate"))||DateUtil.getCurrentDate().equals(context.contextMap.get("queryDate").toString())) {
				context.contextMap.put("queryDate",DateUtil.getCurrentDate());
				resultList=this.rentFinanceService.getCashReport(context);
			} else {
				resultList=this.rentFinanceService.getHistoryCashReport(context);
			}
			
			outputMap.put("deptList",this.rentFinanceService.getDeptList());
		} catch (Exception e) {
			
		}
		
		outputMap.put("queryDate",context.contextMap.get("queryDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("taxPlanCode",context.contextMap.get("taxPlanCode"));
		outputMap.put("productionType",context.contextMap.get("productionType"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("taxPlanList",taxPlanList);
		outputMap.put("productionTypeList",productionTypeList);
		outputMap.put("resultList",resultList);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());
		Output.jspOutput(outputMap,context,"/rent/report/decomposeDailyReport.jsp");
	}
	
	//销帐月报表,现金销帐
	public void queryCashMonthReport(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> taxPlanList=null;
		List<Map<String,Object>> productionTypeList=null;
		List<String> monthDateList=null;
		
		outputMap.put("cardFlag",0);
		
		try {
			context.contextMap.put("dataType","税费方案");
			taxPlanList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			context.contextMap.put("dataType","产品类别");
			productionTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			monthDateList=(List<String>)DataAccessor.query("rentFinance.getMonthDate1",context.contextMap,RS_TYPE.LIST);
			
			outputMap.put("__action","rentFinanceCommand.queryCashMonthReport");
			if(StringUtils.isEmpty(context.contextMap.get("monthDate"))) {
				context.contextMap.put("monthDate",monthDateList.get(0));
			}
			ReportDateTo reportDateTo=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(context.contextMap.get("monthDate").toString().split("-")[0]),
					Integer.valueOf(context.contextMap.get("monthDate").toString().split("-")[1]));
			context.contextMap.put("startDate",reportDateTo.getBeginTime());
			context.contextMap.put("endDate",reportDateTo.getEndTime());
			resultList=this.rentFinanceService.getHistoryCashReport(context);
			
			outputMap.put("deptList",this.rentFinanceService.getDeptList());
		} catch (Exception e) {
			
		}
		
		outputMap.put("monthDate",context.contextMap.get("monthDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("taxPlanCode",context.contextMap.get("taxPlanCode"));
		outputMap.put("productionType",context.contextMap.get("productionType"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("taxPlanList",taxPlanList);
		outputMap.put("productionTypeList",productionTypeList);
		outputMap.put("monthDateList",monthDateList);
		outputMap.put("resultList",resultList);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/report/decomposeMonthReport.jsp");
	}
	
	//销帐日报表,暂收款销帐
	public void queryFundDailyReport(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> taxPlanList=null;
		List<Map<String,Object>> productionTypeList=null;
		
		outputMap.put("cardFlag",1);
		
		try {
			context.contextMap.put("dataType","税费方案");
			taxPlanList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			context.contextMap.put("dataType","产品类别");
			productionTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			
			outputMap.put("__action","rentFinanceCommand.queryFundDailyReport");
			if(StringUtils.isEmpty(context.contextMap.get("queryDate"))||DateUtil.getCurrentDate().equals(context.contextMap.get("queryDate").toString())) {
				context.contextMap.put("queryDate",DateUtil.getCurrentDate());
				resultList=this.rentFinanceService.getFundReport(context);
			} else {
				resultList=this.rentFinanceService.getHistoryFundReport(context);
			}
			outputMap.put("deptList",this.rentFinanceService.getDeptList());
		} catch (Exception e) {
			
		}
		
		outputMap.put("queryDate",context.contextMap.get("queryDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("taxPlanCode",context.contextMap.get("taxPlanCode"));
		outputMap.put("productionType",context.contextMap.get("productionType"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("taxPlanList",taxPlanList);
		outputMap.put("productionTypeList",productionTypeList);
		outputMap.put("resultList",resultList);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/report/decomposeDailyReport.jsp");
	}
	
	public void queryFundMonthReport(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> taxPlanList=null;
		List<Map<String,Object>> productionTypeList=null;
		List<String> monthDateList=null;
		
		outputMap.put("cardFlag",1);
		
		try {
			context.contextMap.put("dataType","税费方案");
			taxPlanList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			context.contextMap.put("dataType","产品类别");
			productionTypeList=(List<Map<String,Object>>)DataAccessor.query("dataDictionary.queryDataDictionary",context.contextMap,RS_TYPE.LIST);
			monthDateList=(List<String>)DataAccessor.query("rentFinance.getMonthDate1",context.contextMap,RS_TYPE.LIST);
			
			outputMap.put("__action","rentFinanceCommand.queryFundMonthReport");
			if(StringUtils.isEmpty(context.contextMap.get("monthDate"))) {
				context.contextMap.put("monthDate",monthDateList.get(0));
			}
			ReportDateTo reportDateTo=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(context.contextMap.get("monthDate").toString().split("-")[0]),
					Integer.valueOf(context.contextMap.get("monthDate").toString().split("-")[1]));
			context.contextMap.put("startDate",reportDateTo.getBeginTime());
			context.contextMap.put("endDate",reportDateTo.getEndTime());
			resultList=this.rentFinanceService.getHistoryFundReport(context);
			
			outputMap.put("deptList",this.rentFinanceService.getDeptList());
		} catch (Exception e) {
			
		}
		
		outputMap.put("monthDate",context.contextMap.get("monthDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("DEPT_ID",context.contextMap.get("DEPT_ID"));
		outputMap.put("taxPlanCode",context.contextMap.get("taxPlanCode"));
		outputMap.put("productionType",context.contextMap.get("productionType"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("taxPlanList",taxPlanList);
		outputMap.put("productionTypeList",productionTypeList);
		outputMap.put("monthDateList",monthDateList);
		outputMap.put("resultList",resultList);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/report/decomposeMonthReport.jsp");
	}

	//销帐日报表,余额变动
	public void queryBalanceDailyReport(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		List<Map<String,Object>> resultList=null;
		
		outputMap.put("cardFlag",2);
		
		try {
			
			outputMap.put("__action","rentFinanceCommand.queryBalanceDailyReport");
			String companyCode = (String) context.contextMap.get("companyCode");
			if(!StringUtils.isEmpty(companyCode)){
				List<Map<String,String>> companys = LeaseUtil.getCompanys();
				for(Map<String,String> c:companys){
					if(c.get("code").equals(companyCode)){
						context.contextMap.put("companyName", c.get("name"));
					}
				}
			}
			if(StringUtils.isEmpty(context.contextMap.get("queryDate"))||DateUtil.getCurrentDate().equals(context.contextMap.get("queryDate").toString())) {
				context.contextMap.put("queryDate",DateUtil.getCurrentDate());
				resultList=this.rentFinanceService.getBalanceReport(context);
			} else {
				resultList=this.rentFinanceService.getHistoryBalanceReport(context);
			}
		} catch (Exception e) {
			
		}
		
		outputMap.put("queryDate",context.contextMap.get("queryDate"));
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("resultList",resultList);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/report/decomposeDailyReport.jsp");
	}

	//财务确认查看销赃流
	public void showCashFlow(Context context) {
		
		Map<String,Object> outputMap=new HashMap<String,Object>();
		try {
			outputMap.put("cashIncome",this.rentFinanceService.getCashIncome(context));
			outputMap.put("cashFlow",this.rentFinanceService.getCashFlow(context));
		} catch (Exception e) {
			
		}
		
		Output.jsonOutput(outputMap,context);
	}
	
	public void transferData(Context context) {
		
		List<Map<String,Object>> baseList=null;
		SqlMapClient sqlMapClient=DataAccessor.getSession();
		try {
			baseList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getBaseData",null,RS_TYPE.LIST);
			
			for(int i=0;i<baseList.size();i++) {
				String incomeId=null;
				failId=baseList.get(i).get("FICB_ID").toString();
				Map<String,Object> insertMap=new HashMap<String,Object>();
				boolean flag=true;
				Map<String,Object> param=new HashMap<String,Object>();
				param.put("select_income_id",baseList.get(i).get("FIIN_ID"));
				while(flag) {
					String leftId=(String)DataAccessor.query("decompose.getLeftId",param,RS_TYPE.OBJECT);
					if(leftId==null||"".equals(leftId)) {
						incomeId=(String)DataAccessor.query("rentFinance.getIncomeId",param,RS_TYPE.OBJECT);
						baseList.get(i).put("INCOME_ID",incomeId);
						//incomeId=(String)DataAccessor.query("decompose.getOriginalFiinId",param,RS_TYPE.OBJECT);
						flag=false;
					} else {
						param.put("select_income_id",leftId);
					}
				}
				if(StringUtils.isEmpty(incomeId)) {
					continue;
				}
				sqlMapClient.startTransaction();
				long billId=(Long)sqlMapClient.insert("rentFinance.insertDecompose",baseList.get(i));
				baseList.get(i).put("BILL_ID",billId);
				baseList.get(i).put("DECOMPOSE_STATUS",1);
				baseList.get(i).put("TABLE","T_RENT_DECOMPOSE");
				sqlMapClient.insert("rentFinance.insertRecord",baseList.get(i));
				sqlMapClient.update("rentFinance.updateTransfer",baseList.get(i));
				sqlMapClient.commitTransaction();
			}
		} catch (Exception e) {
			System.out.println(failId);
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
			logger.debug(e);
		}
		System.out.println("租金类数据迁移成功");
	}
	

	/**
	 * 提前结清列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void queryAdvanceSettleList(Context context) {
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		boolean applySettle=false;
		/*-------- data access --------*/		
		if(errList.isEmpty()){		
			try {
				Map rsMap = null;
				Map paramMap = new HashMap();
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
				if(context.contextMap.get("recp_status")==null||"".equals(context.contextMap.get("recp_status"))) {
					context.contextMap.put("recp_status",-1);
				}
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				context.contextMap.put("advance_settlement", 1);
				dw = baseService.queryForListWithPaging("rentFinance.getContractList",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
			//Add by Michael 2012 09-14  结清申请按钮权限单独切出来
				List<String> resourceIdList=(List<String>) DataAccessor.query("supplier.getResourceIdListByEmplId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				for(int i=0;resourceIdList!=null&&i<resourceIdList.size();i++) {
					if("259".equals(resourceIdList.get(i))) {
						applySettle=true;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		/*-------- output --------*/
		outputMap.put("dw", dw);
		outputMap.put("applySettle", true);
		outputMap.put("content", context.contextMap.get("content"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		outputMap.put("QSELECT_STATUS", context.contextMap.get("QSELECT_STATUS"));

		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/rent/finance/rentSettlement/queryAdvanceSettleList.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	

	/**
	 * 预估结清按钮
	 * @param context
	 */
	public void estimateAdvanceSettleList(Context context) {
		List errList = context.errList ;
		Map<String,Object> outputMap=new HashMap<String,Object>();
		Map<String,Object> custInfo=null;
		Map<String,Object> settlementPrice=null;
		context.contextMap.put("recpId", context.contextMap.get("RECP_ID"));
		try {
			custInfo=this.rentFinanceService.getCustInfoByRecpId(context);//获得客户信息
			settlementPrice=this.rentFinanceService.getSettlementPriceAdvance(context);
		} catch (Exception e) {
			logger.debug("获得正常结清明细出错!"+e);
		}
		
		DecimalFormat f=new DecimalFormat();
		f.applyPattern("##,##0.00");
		custInfo.put("PAY_PRICE",f.format(custInfo.get("PAY_PRICE")));
		
		double total = 0.0;
		try {
			total = f.parse(settlementPrice.get("VALUE_ADDED_TAX").toString()).doubleValue()
					+ f.parse(settlementPrice.get("OWN_PRICE").toString()).doubleValue()
					+ f.parse(settlementPrice.get("REN_PRICE").toString()).doubleValue()
					+ f.parse(settlementPrice.get("FINE").toString()).doubleValue()
					+ f.parse(settlementPrice.get("STAY_FEE").toString()).doubleValue()
					+ f.parse(settlementPrice.get("LAW_FEE").toString()).doubleValue()
					+ f.parse(settlementPrice.get("OTHER_FEE").toString()).doubleValue();
		} catch (ParseException e) {
			logger.debug("求和出错!"+e);
			e.printStackTrace();
		}
		settlementPrice.put("total", total);
		outputMap.put("RECP_ID",context.contextMap.get("RECP_ID"));
		outputMap.put("custInfo",custInfo);
		outputMap.put("settlementPrice",settlementPrice);
		outputMap.put("QUERY_DATE", context.contextMap.get("QUERY_DATE"));
		Output.jspOutput(outputMap, context, "/collection/showAdvanceSettleList.jsp");
	}
	
	/**
	 * 预估结清导出PDF
	 * @param context
	 */
	public void expAdvanceSettlePDF(Context context){
		Map<String,Object> custInfo=null;
		Map<String,Object> settlementPrice=null;
		Map<String,Object> content = new HashMap<String, Object>();
		context.contextMap.put("recpId", context.contextMap.get("RECP_ID"));
		try {
			custInfo=this.rentFinanceService.getCustInfoByRecpId(context);
			settlementPrice=this.rentFinanceService.getSettlementPriceAdvance(context);
			DecimalFormat f=new DecimalFormat();
			f.applyPattern("##,##0.00");
			//合同编号
			content.put("LEASE_CODE", custInfo.get("LEASE_CODE"));
			//承租人
			content.put("CUST_NAME", custInfo.get("CUST_NAME"));
			//组织机构代码
			content.put("CORP_ORAGNIZATION_CODE", custInfo.get("CORP_ORAGNIZATION_CODE"));
			//租赁期数
			content.put("LEASE_PERIOD", custInfo.get("PERIOD_NUM"));
			//已缴期数
			content.put("REAL_PERIOD", custInfo.get("PAY_PERIOD"));
			//增值税
			double valueAddedTax = f.parse(settlementPrice.get("VALUE_ADDED_TAX").toString()).doubleValue();
			content.put("SUM_VALUE_ADDED_TAX", valueAddedTax);
			//本金
			double ownPrice = f.parse(settlementPrice.get("OWN_PRICE").toString()).doubleValue();
			content.put("SUM_OWN_PRICE", ownPrice);
			//利息
			double renPrice = f.parse(settlementPrice.get("REN_PRICE").toString()).doubleValue();
			content.put("SUM_REN_PRICE", renPrice);
			//罚息
			double fine = f.parse(settlementPrice.get("FINE").toString()).doubleValue();
			content.put("DUN_PRICE", fine);
			//期满购买金	
			double stayFee = f.parse(settlementPrice.get("STAY_FEE").toString()).doubleValue();
			content.put("LGJ", stayFee);
			//法务费用	
			double lawFee = f.parse(settlementPrice.get("LAW_FEE").toString()).doubleValue();
			content.put("TOTAL_LAWYFEE", lawFee);
			//其他费用	
			double otherFee = f.parse(settlementPrice.get("OTHER_FEE").toString()).doubleValue();
			content.put("OTHER_PRICE", otherFee);
			
			double total = 0.0;
				total = valueAddedTax + ownPrice + renPrice + fine + stayFee + lawFee + otherFee;
			//合计	
			content.put("total", total);
		} catch (Exception e) {
			logger.debug("预估结清导出PDF出错!"+e);
			e.printStackTrace();
		}
		SettlePDF.expAdvanceSettlePDF(context,content) ;
	}
	
	/**
	 * 导出结清金额通知函PDF新
	 * @param context
	 */
	public void expSettlePayNote(Context context){
		Map<String,Object> custInfo=null;
		Map<String,Object> settlementPrice=null;
		Map<String,Object> content = new HashMap<String, Object>();
		context.contextMap.put("recpId", context.contextMap.get("RECP_ID"));
		try {
			custInfo=this.rentFinanceService.getCustInfoByRecpId(context);
			settlementPrice=this.rentFinanceService.getSettlementPriceAdvance(context);
			DecimalFormat f=new DecimalFormat();
			f.applyPattern("##,##0.00");
			//合同编号
			content.put("LEASE_CODE", custInfo.get("LEASE_CODE"));
			//承租人
			content.put("CUST_NAME", custInfo.get("CUST_NAME"));
			//组织机构代码
			content.put("CORP_ORAGNIZATION_CODE", custInfo.get("CORP_ORAGNIZATION_CODE"));
			//租赁期数
			content.put("LEASE_PERIOD", custInfo.get("PERIOD_NUM"));
			//已缴期数
			content.put("REAL_PERIOD", custInfo.get("PAY_PERIOD"));
			//增值税
			double valueAddedTax = f.parse(settlementPrice.get("VALUE_ADDED_TAX").toString()).doubleValue();
			content.put("SUM_VALUE_ADDED_TAX", valueAddedTax);
			//本金
			double ownPrice = f.parse(settlementPrice.get("OWN_PRICE").toString()).doubleValue();
			content.put("SUM_OWN_PRICE", ownPrice);
			//利息
			double renPrice = f.parse(settlementPrice.get("REN_PRICE").toString()).doubleValue();
			content.put("SUM_REN_PRICE", renPrice);
			//罚息
			double fine = f.parse(settlementPrice.get("FINE").toString()).doubleValue();
			content.put("DUN_PRICE", fine);
			//期满购买金	
			double stayFee = f.parse(settlementPrice.get("STAY_FEE").toString()).doubleValue();
			content.put("LGJ", stayFee);
			//法务费用	
			double lawFee = f.parse(settlementPrice.get("LAW_FEE").toString()).doubleValue();
			content.put("TOTAL_LAWYFEE", lawFee);
			//其他费用	
			double otherFee = f.parse(settlementPrice.get("OTHER_FEE").toString()).doubleValue();
			content.put("OTHER_PRICE", otherFee);

			double total = 0.0;
			double rent = 0.0;
			//SUM_OWN_PRICE + SUM_REN_PRICE + SUM_VALUE_ADDED_TAX
			rent = ownPrice + renPrice + valueAddedTax;
			//SUM_OWN_PRICE + SUM_REN_PRICE + SUM_VALUE_ADDED_TAX + LGJ + DUN_PRICE + TOTAL_LAWYFEE
			total = rent + stayFee + fine + lawFee;
			//合计	
			content.put("rent", rent);
			content.put("total", total);
		} catch (Exception e) {
			logger.debug("预估结清导出PDF出错!"+e);
			e.printStackTrace();
		}
		//SettlePDF.expAdvanceSettlePDF(context,content) ;
		SettlePDF.expSettlePayNotePDFNew(context, content);
	}
	
	/**
	 * 直租发票列印列表
	 * @param context
	 */
	public void queryDirectLeaseList(Context context){
		Map<String,Object> outputMap=new HashMap<String,Object>();
		PagingInfo<Object> dw=null;
		
		if(context.contextMap.get("isPrint")==null) {
			context.contextMap.put("isPrint",-1);
		}
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("isPrint",context.contextMap.get("isPrint"));
		
		//因为直租2014年之前旧案件可能无法获取发票号码，查询数据过多，会计邓碧要求查询财务日期在2014-3-1之后，之前的数据由会计手动解决
		context.contextMap.put("resultBeginDate", "2014-3-1");
		
		dw=baseService.queryForListWithPaging("rentFinance.getDirectLeaseList",context.contextMap,"INVOICE_CODE",ORDER_TYPE.DESC);
		
		outputMap.put("dw",dw);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		
		Output.jspOutput(outputMap,context,"/rent/finance/directLeaseList.jsp");
	}
	
	/**
	 * 直租发票列印列表导出excel
	 * @param context
	 */
	public static List<Map<String,Object>> directLeaseExcel(String isPrint, String content,String companyCode){
		try {
			content = new String(content.getBytes("iso8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Map<String,String> param=new HashMap<String,String>();
		if(StringUtils.isEmpty(isPrint)){
			isPrint = "-1";
		}
		param.put("isPrint",isPrint);
		param.put("content",content);
		param.put("companyCode",companyCode);
		
		//因为直租2014年之前旧案件可能无法获取发票号码，查询数据过多，会计邓碧要求查询财务日期在2014-3-1之后，之前的数据由会计手动解决
		param.put("resultBeginDate", "2014-3-1");
		List<Map<String,Object>> resultList=null;
		try {
			resultList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getDirectLeaseList",param,RS_TYPE.LIST);
			System.out.println(resultList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	/**
	 * 打印直租发票收据日志列表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void viewLog(Context context) { 
		List errList = context.errList;
		Map outputMap = new HashMap();
		PagingInfo<Object> dw = null;
		try {
			//调分页查询方法
			context.contextMap.put("RECEIPT_TYPE", "direct");
			dw = baseService.queryForListWithPaging("rentReceipt.queryReciptLog", context.contextMap,"LOGCREATEDATE", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		outputMap.put("dw", dw);
		outputMap.put("QSEARCH_VALUE", context.contextMap.get("QSEARCH_VALUE"));
		outputMap.put("QSTART_DATE", context.contextMap.get("QSTART_DATE"));
		outputMap.put("QEND_DATE", context.contextMap.get("QEND_DATE"));
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context, "/collection/queryDirectReceiptLog.jsp");
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 财务已确认的认退款记录显示页面
	 * @param context
	 */
	public void fundsAuthSow(Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		PagingInfo<Object> fundsAuthed = null;
		String amount_from = (String) context.contextMap.get("amount_from");
		String amount_to = (String) context.contextMap.get("amount_to");
		String income_money_from = (String) context.contextMap.get("income_money_from");
		String income_money_to = (String) context.contextMap.get("income_money_to");
		String opp_data_from = (String) context.contextMap.get("opp_data_from");
		String opp_data_to = (String) context.contextMap.get("opp_data_to");
		String search_content = (String) context.contextMap.get("search_content");
		String fund_type = (String) context.contextMap.get("fund_type");
		try {
			
			List<String> authList=null;
			try {
				authList=(List<String>)baseService.queryForList("common.getPermissions",context.contextMap);
			} catch (Exception e) {
				logger.debug("暂收款管理查询出错!"+e);
			}
			
			for(int i=0;authList!=null&&i<authList.size();i++) {
				if(authList.get(i)==null) {
					continue;
				}
				if(RentFinanceUtil.AUTHORITY.CLAIM.toString().equals(authList.get(i).toString())) {
					outputMap.put("CLAIM","Y");
				} else if(RentFinanceUtil.AUTHORITY.REFUND.toString().equals(authList.get(i).toString())) {
					outputMap.put("REFUND","Y");
				} else if(RentFinanceUtil.AUTHORITY.UPLOAD.toString().equals(authList.get(i).toString())) {
					outputMap.put("UPLOAD","Y");
				} else if(RentFinanceUtil.AUTHORITY.MODIFY_REMARK.toString().equals(authList.get(i).toString())) {
					outputMap.put("MODIFY_REMARK","Y");
				} else if(RentFinanceUtil.AUTHORITY.APPROVE.toString().equals(authList.get(i).toString())) {
					outputMap.put("APPROVE","Y");
				} else if(RentFinanceUtil.AUTHORITY.REJECT.toString().equals(authList.get(i).toString())) {
					outputMap.put("REJECT","Y");
				}
			}
			
			amount_from = StringUtils.isEmpty(amount_from) ? null : amount_from.trim();
			amount_to = StringUtils.isEmpty(amount_to) ? null : amount_to.trim();
			income_money_from = StringUtils.isEmpty(income_money_from) ? null : income_money_from.trim();
			income_money_to = StringUtils.isEmpty(income_money_to) ? null : income_money_to.trim();
			opp_data_from = StringUtils.isEmpty(opp_data_from) ? null : opp_data_from.trim();
			opp_data_to = StringUtils.isEmpty(opp_data_to) ? null : opp_data_to.trim();
			search_content = StringUtils.isEmpty(search_content) ? null : search_content.trim();
			outputMap.put("amount_from", amount_from);
			outputMap.put("amount_to", amount_to);
			outputMap.put("income_money_from", income_money_from);
			outputMap.put("income_money_to", income_money_to);
			outputMap.put("opp_data_from", opp_data_from);
			outputMap.put("opp_data_to", opp_data_to);
			outputMap.put("search_content", search_content);
			outputMap.put("fund_type", fund_type);
			context.contextMap.put("amount_from", amount_from);
			context.contextMap.put("amount_to", amount_to);
			context.contextMap.put("income_money_from", income_money_from);
			context.contextMap.put("income_money_to", income_money_to);
			context.contextMap.put("opp_data_from", opp_data_from);
			context.contextMap.put("opp_data_to", opp_data_to);
			context.contextMap.put("search_content", search_content);
			context.contextMap.put("fund_type", fund_type);
			List<Map<String, String>> companys = LeaseUtil.getCompanys();
			if(context.contextMap.get("companyCode")!=null && !"".equals(context.contextMap.get("companyCode"))){
				for(Map<String,String> c:companys){
					if(c.get("code").equals(context.contextMap.get("companyCode"))){
						context.contextMap.put("companyName",c.get("name"));
					}
				}
			}
			
			fundsAuthed = baseService.queryForListWithPaging("rentFinance.getAllAuthedFunds", context.contextMap, "AUTH_TIME", ORDER_TYPE.DESC);
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("dw", fundsAuthed);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	

		Output.jspOutput(outputMap, context, "/rent/finance/fund/fundsAuthSow.jsp");
	}
	
	//查询可修改的来款户名
	public void queryIncomeInfo(Context context) {
		Map<String, Object> outputMap=new HashMap<String, Object>();
		PagingInfo<Object> dw=null;
		List<Map<String, String>> companys = LeaseUtil.getCompanys();
		if(context.contextMap.get("companyCode")!=null && !"".equals(context.contextMap.get("companyCode"))){
			for(Map<String,String> c:companys){
				if(c.get("code").equals(context.contextMap.get("companyCode"))){
					context.contextMap.put("companyName",c.get("name"));
				}
			}
		}
		dw=baseService.queryForListWithPaging("rentFinance.getUnDecomposeMoney",context.contextMap,"INCOME_DATE",ORDER_TYPE.DESC);
		
		outputMap.put("content",context.contextMap.get("content"));
		outputMap.put("fromMoney",context.contextMap.get("fromMoney"));
		outputMap.put("toMoney",context.contextMap.get("toMoney"));
		outputMap.put("fromDate",context.contextMap.get("fromDate"));
		outputMap.put("toDate",context.contextMap.get("toDate"));
		outputMap.put("dw",dw);
		outputMap.put("companyCode", context.contextMap.get("companyCode"));
		outputMap.put("companys", LeaseUtil.getCompanys());	
		Output.jspOutput(outputMap,context,"/rent/finance/incomeInfo.jsp");
	}
	//更新来款信息
	public void updateIncomeInfo(Context context) {
		try {
			this.rentFinanceService.updateIncomeInfo(context);
		} catch (Exception e) {
		}
		Output.jsonFlageOutput(true,context);
		
	}
}
