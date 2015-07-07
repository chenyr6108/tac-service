package com.brick.collection.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.credit.service.ExportQuoToPdf;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author wuzd
 * @date 2010,8,4
 * @version 
 */
public class ExportPayToPdf extends AService {
	Log logger = LogFactory.getLog(ExportPayToPdf.class);
	/**
	 * 
	 * @param context
	 */
	//此方法是合同浏览页时如果没有支付表时导出报告的租金测算的数据
	 @SuppressWarnings("unchecked")
	public void exportPaylistBeforeByHu(Context context) {
		
		 Map outputMap = new HashMap();
		 List errList = context.errList;
		 Map creditMap = null;
		 Map schema = null;
		 Map paylist = null;
		 //Map memoMap = null;
		 try{
			 context.contextMap.put("PRCD_ID", context.contextMap.get("credit_id"));
			 List contractinfo=(List) DataAccessor.query("exportContractPdf.judgeExitContract", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    	//查找合同的相关信息		    	
		    if(contractinfo.size()==0){	
			 	context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfoByH", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				//查询方案
				schema  = (Map) DataAccessor.query(
						"creditReportManage.selectCreditScheme",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
//				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				
				if(schema!=null
						&&(Constants.TAX_PLAN_CODE_4.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_6.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_7.equals(schema.get("TAX_PLAN_CODE"))
								||Constants.TAX_PLAN_CODE_8.equals(schema.get("TAX_PLAN_CODE")))) {
					schema.put("payList",rePaylineList);
					schema.put("PLEDGE_AVE_PRICE",schema.get("PLEDGE_AVE_PRICE")==null||"".equals(schema.get("PLEDGE_AVE_PRICE"))?0:schema.get("PLEDGE_AVE_PRICE"));
					schema.put("PLEDGE_BACK_PRICE",schema.get("PLEDGE_BACK_PRICE")==null||"".equals(schema.get("PLEDGE_BACK_PRICE"))?"0":schema.get("PLEDGE_BACK_PRICE"));
					schema.put("MAGR_FEE",schema.get("MANAGEMENT_FEE")==null||"".equals(schema.get("MANAGEMENT_FEE"))?0:schema.get("MANAGEMENT_FEE"));
					schema.put("PLEDGE_LAST_PERIOD",schema.get("PLEDGE_LAST_PERIOD")==null||"".equals(schema.get("PLEDGE_LAST_PERIOD"))?0:schema.get("PLEDGE_LAST_PERIOD"));
				}
				// 
				if (schema != null) {
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
				
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);

					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));		
					
					
				}
				context.contextMap.put("paylist", paylist);
				context.contextMap.put("creditMap", creditMap);
				context.contextMap.put("schema", schema);
				context.contextMap.put("rePaylineList", rePaylineList);
				context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				context.contextMap.put("hu_rentcontractexportcredit", "true");
		    }else{
		    	HashMap contractRectid=(HashMap)contractinfo.get(0);
		    	context.contextMap.put("RECT_ID", contractRectid.get("RECT_ID"));
		    	Map rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		    	List<Map> equipList = (List<Map>) DataAccessor.query("exportContractPdf.queryEquipmentByRectIdHu", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    	context.contextMap.put("equipList", equipList);
				Map contractSchema = (Map) DataAccessor.query("rentContract.readSchemaByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				List<Map> insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("insureCompanyList", insureCompanyList);
				
				List<Map> insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("insureTypeList", insureTypeList);
				context.contextMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				List<Map> insureList = (List<Map>) DataAccessor.query("rentContract.readInsureByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);

				List<Map> otherFeeList = (List<Map>) DataAccessor.query("rentContract.readOtherFeeByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
		    	List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				// contain RECS_ID
		    	List<Map> irrMonthPaylines = (List<Map>) DataAccessor.query("rentContract.readSchemaIrrByRecsId", contractSchema, DataAccessor.RS_TYPE.LIST);
		    	context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				
				paylist = StartPayService.createPaylist(rentContract, contractSchema, equipList, insureList, otherFeeList, oldPaylists, rePaylineList,irrMonthPaylines);
				paylist.put("PLEDGE_ENTER_AGRATE", contractSchema.get("PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAG", contractSchema.get("PLEDGE_ENTER_MCTOAG"));
				context.contextMap.put("paylist", paylist);
				context.contextMap.put("creditMap", rentContract);
				context.contextMap.put("schema", contractSchema);
				context.contextMap.put("rePaylineList", rePaylineList);
				context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				context.contextMap.put("hu_rentcontractexportcredit", "true");
		    }
			 this.expPayToPdf(context);
		 }catch(Exception e){
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		 }
	 }
	@SuppressWarnings("unchecked")
	public void expPayToPdf(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
		Map paylist = null;
		@SuppressWarnings("unused")
		Map payMax = null;
		List<Map> paylines = null;

			try {
				// 字体设置
				BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
				Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
				Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
				Font fa = new Font(bfChinese, 15, Font.BOLD);
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				//打开文档
				document.open();
				//支付表PDF名字的定义
				@SuppressWarnings("unused")
				String strFileName = "";
				// 表格列宽定义
				float[] widthsStl = {0.1f,0.20f,0.20f,0.20f,0.25f};
				int iCnt = 0;			
				float[] widthsPPCa = { 1f };
				PdfPTable tT = new PdfPTable(widthsPPCa);
				tT.setWidthPercentage(100f);
				tT.addCell(makeCellWithNoBorder("租金支付明细表", PdfPCell.ALIGN_CENTER, fa));
				document.add(tT);
				document.add(new Paragraph("\n"));
				if(context.contextMap.get("hu_rentcontractexportcredit")!=null){
					if(Boolean.parseBoolean(context.contextMap.get("hu_rentcontractexportcredit").toString())){
						PdfPTable tableHdr1 = new PdfPTable(2);
						tableHdr1.addCell(makeCell("租金缴纳银行：",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr1.addCell(makeCell("交通银行苏州分行工业园区支行",PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr1);

						PdfPTable tableHdr2 = new PdfPTable(2);
						tableHdr2.addCell(makeCell("户名:",PdfPCell.ALIGN_CENTER, FontColumn));	
						//直租 添加公司别判断
						String contractType = LeaseUtil.getContractTypeByCreditId((String)context.contextMap.get("credit_id"));
						int companyCode = LeaseUtil.getCompanyCodeByCreditId((String)context.contextMap.get("credit_id"));
						String companyName = Constants.COMPANY_NAME;
						if("7".equals(contractType) ){
							companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
						}
						tableHdr2.addCell(makeCell(companyName,PdfPCell.ALIGN_CENTER, FontDefault));	
						
						document.add(tableHdr2);				
						String custcode="";
						String leasecode="";
						if(context.contextMap.get("creditMap")!=null){
							if(((Map)context.contextMap.get("creditMap")).get("CUST_CODE")!=null){
								custcode=((Map)context.contextMap.get("creditMap")).get("CUST_CODE").toString();
							}
							if(((Map)context.contextMap.get("creditMap")).get("LEASE_CODE")!=null){
								leasecode=((Map)context.contextMap.get("creditMap")).get("LEASE_CODE").toString();
							}
						}
						PdfPTable tableHdr3 = new PdfPTable(4);
						tableHdr3.addCell(makeCell("账号：",PdfPCell.ALIGN_CENTER, FontColumn));					
						tableHdr3.addCell(makeCell("                        ",PdfPCell.ALIGN_CENTER, FontDefault));
						tableHdr3.addCell(makeCell("合同号",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr3.addCell(makeCell(leasecode,PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr3);
						paylist=(Map)context.contextMap.get("paylist");				
						//融资租赁还款计划
						strFileName = "PayLst-" +leasecode + ".pdf";	
						paylines = (List<Map>)paylist.get("paylines");
						
						//保证金每期抵扣之租金
						
						Float ave=0F;
						if(context.contextMap.get("schema")!=null){
							Map schema = ((Map)context.contextMap.get("schema"));
							if(schema.get("PLEDGE_AVG_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVG_PRICE").toString());
							} else if(schema.get("PLEDGE_AVE_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVE_PRICE").toString());
							}
						}
					
						PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

						tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("合同各期租金",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("保证金每期抵扣之租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("租金支付日",PdfPCell.ALIGN_CENTER, FontColumn));
					
						for (Iterator iterator = paylines.iterator(); iterator.hasNext();) {
							Map map = (Map) iterator.next();
							iCnt++;
							tableHdr4.addCell(makeCell("第"+map.get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			
							if(map.get("PAY_DATE")==null){
								map.put("PAY_DATE","");
							}
							
							tableHdr4.addCell(makeCell(updateMoney(map,"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
							Double ave1 =Double.parseDouble(map.get("MONTH_PRICE").toString()) - Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) ;
							tableHdr4.addCell(makeCell(ave1==0?"0.00":nfFSNum.format(ave1),PdfPCell.ALIGN_RIGHT, FontColumn));	
//							tableHdr4.addCell(makeCell(ave==0?"0.00":nfFSNum.format(ave),PdfPCell.ALIGN_RIGHT, FontColumn));	
							tableHdr4.addCell(makeCell(updateMoney(map,"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));					
							//tableHdr4.addCell(makeCell(DateFormat.getDateInstance().format(map.get("PAY_DATE")).toString(),PdfPCell.ALIGN_CENTER, FontColumn));	
							tableHdr4.addCell(makeCell(" ",PdfPCell.ALIGN_CENTER, FontColumn));	
						}				
						document.add(tableHdr4);
						
						PdfPTable tableHdr5 = new PdfPTable(1);
						tableHdr5.addCell(makeCellWithNoBorder("承租人签章：",PdfPCell.ALIGN_LEFT, FontColumn));
						document.add(tableHdr5);					
						
						document.add(Chunk.NEXTPAGE);
					}
				}else{
				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("RECP_ID", ids[i]);						
					//表头 PDF
					paylist = (Map) DataAccessor.query("collectionManage.readPaylistByIdfForExpPay", context.contextMap, DataAccessor.RS_TYPE.MAP);
						//生成支付表PDF名字
						strFileName = "PayLst-" + paylist.get("RECP_CODE").toString() + ".pdf";	
																	
					PdfPTable tableHdr1 = new PdfPTable(2);
					tableHdr1.addCell(makeCell("租金缴纳银行：",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr1.addCell(makeCell("交通银行苏州分行工业园区支行",PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr1);
					
					//直租 添加公司别判断
					String contractType = LeaseUtil.getContractTypeByCreditId((String)context.contextMap.get("credit_id"));
					int companyCode = LeaseUtil.getCompanyCodeByCreditId((String)context.contextMap.get("credit_id"));
					String companyName = Constants.COMPANY_NAME;
					if("7".equals(contractType) ){
						companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
					}
					
					PdfPTable tableHdr2 = new PdfPTable(2);
					tableHdr2.addCell(makeCell("户名:",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr2.addCell(makeCell(companyName,PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr2);				

					
					PdfPTable tableHdr3 = new PdfPTable(4);
					tableHdr3.addCell(makeCell("账号：",PdfPCell.ALIGN_CENTER, FontColumn));					
					tableHdr3.addCell(makeCell("                       ",PdfPCell.ALIGN_CENTER, FontDefault));
					tableHdr3.addCell(makeCell("合同号",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr3.addCell(makeCell(paylist.get("LEASE_CODE").toString(),PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr3);
									
					//融资租赁还款计划
					paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);		
					
					//保证金每期抵扣之租金
					Float ave=(Float)DataAccessor.query("collectionManage.pledgeAvgPrice",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
					PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

					tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("合同各期租金",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("保证金每期抵扣之租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("租金支付日",PdfPCell.ALIGN_CENTER, FontColumn));
				
					for (Iterator iterator = paylines.iterator(); iterator.hasNext();) {
						Map map = (Map) iterator.next();
						iCnt++;
						tableHdr4.addCell(makeCell("第"+map.get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			
						if(map.get("PAY_DATE")==null){
							map.put("PAY_DATE","");
						}
						Double ave1 =Double.parseDouble(map.get("MONTH_PRICE").toString()) - Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) ;
						tableHdr4.addCell(makeCell(updateMoney(map,"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));				
						tableHdr4.addCell(makeCell(ave1==0?"0.00":nfFSNum.format(ave1),PdfPCell.ALIGN_RIGHT, FontColumn));	
//						tableHdr4.addCell(makeCell(ave==0?"0.00":nfFSNum.format(ave),PdfPCell.ALIGN_RIGHT, FontColumn));	
						tableHdr4.addCell(makeCell(updateMoney(map,"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));					
						//tableHdr4.addCell(makeCell(DateFormat.getDateInstance().format(map.get("PAY_DATE")).toString(),PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell(" " ,PdfPCell.ALIGN_CENTER, FontColumn));	
					}				
					document.add(tableHdr4);
					
					PdfPTable tableHdr5 = new PdfPTable(1);
					tableHdr5.addCell(makeCellWithNoBorder("承租人签章：",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);					
					
					document.add(Chunk.NEXTPAGE);	
				}
				}

				
				document.close();
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=payList");
				
				ServletOutputStream o = context.response.getOutputStream();

				baos.writeTo(o); 
				o.flush();				
				o.close();
				
				//add by ShenQi 插入系统日志
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 租金支付明细表",
			   		 	 "合同浏览导出 租金支付明细表",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	

	//Add by Michael 2012 4-20 增加导出本金摊还表
	@SuppressWarnings("unchecked")
	public void expOwnPriceToPdf(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
		List paylists = new ArrayList();
		List<Map> paylist = null;
			try {

				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("id", ids[i]);						
					paylist = (List<Map>)  DataAccessor.query("collectionManage.queryOwnLastPrice", context.contextMap, DataAccessor.RS_TYPE.LIST);
					paylists.add(paylist);
				}
					//生成支付表PDF名字
						
				for (Object object : paylists) {
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
					Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
					Font fa = new Font(bfChinese, 15, Font.BOLD);
					// 数字格式
					NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
					nfFSNum.setGroupingUsed(true);
					nfFSNum.setMaximumFractionDigits(2);
					// 页面设置
					Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
					Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					//打开文档
					document.open();
					//支付表PDF名字的定义
					@SuppressWarnings("unused")
					String strFileName = "";
					strFileName = "PayList.pdf";
					// 表格列宽定义
					float[] widthsStl = {0.1f,0.20f,0.20f,0.20f,0.25f};
					int iCnt = 0;			
					float[] widthsPPCa = { 1f };
					PdfPTable tT = new PdfPTable(widthsPPCa);
					tT.setWidthPercentage(100f);
					tT.addCell(makeCellWithNoBorder("本息摊还表", PdfPCell.ALIGN_CENTER, fa));
					document.add(tT);
					document.add(new Paragraph("\n"));
					
					List<Map> tempPaylist = (List<Map>) object;												
					PdfPTable tableHdr1 = new PdfPTable(1);
					tableHdr1.addCell(makeCell("致："+tempPaylist.get(0).get("CUST_NAME").toString(),PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr1);

					PdfPTable tableHdr2 = new PdfPTable(1);
					tableHdr2.addCell(makeCell("贵公司与我司签订的融资租赁合同编号为："+tempPaylist.get(0).get("LEASE_CODE").toString()+"，其本金摊还表如下：",PdfPCell.ALIGN_LEFT, FontDefault));				
					document.add(tableHdr2);				

					PdfPTable tableHdr3 = new PdfPTable(1);
					tableHdr3.addCell(makeCell("融资租赁还款期间",PdfPCell.ALIGN_LEFT, FontColumn));					
					document.add(tableHdr3);
								
					PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

					tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("租金",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("本金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("利息",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("剩余本金",PdfPCell.ALIGN_CENTER, FontColumn));
					double monthPriceCount=0.00;
					double renpriceCount=0.00;
					double ownpriceCount=0.00;
					for(int j=0;j<tempPaylist.size();j++){
						monthPriceCount+=Double.parseDouble(tempPaylist.get(j).get("MONTH_PRICE").toString());
						ownpriceCount+=Double.parseDouble(tempPaylist.get(j).get("OWN_PRICE").toString());
						renpriceCount+=Double.parseDouble(tempPaylist.get(j).get("REN_PRICE").toString());
						iCnt++;
						tableHdr4.addCell(makeCell("第"+tempPaylist.get(j).get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			

						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));				
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"OWN_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));					
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"REN_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"LAST_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
					}	
					tableHdr4.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontColumn));			
					tableHdr4.addCell(makeCell(nfFSNum.format(monthPriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));				
					tableHdr4.addCell(makeCell(nfFSNum.format(ownpriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));					
					tableHdr4.addCell(makeCell(nfFSNum.format(renpriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));	
					tableHdr4.addCell(makeCell("",PdfPCell.ALIGN_RIGHT, FontColumn));
					document.add(tableHdr4);
					PdfPTable tableHdr5 = new PdfPTable(1);
					tableHdr5.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);
					tableHdr5.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);
					tableHdr5.addCell(makeCellWithNoBorder("出租方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_RIGHT, FontColumn));
					document.add(tableHdr5);					
				
				document.add(Chunk.NEXTPAGE);	
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition","attachment; filename=payList");
			}
				
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o); 
			o.flush();	
			o.close();
				
			//add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
			   		 "导出 本息摊还表",
		   		 	 "合同浏览导出 租金支付明细表",
		   		 	 null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
		   		 	 1,
		   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
		   		 	 DataUtil.longUtil(0),
		   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	

	@SuppressWarnings("unchecked")
	public void expOwnPriceToPdfByValueAddedTax(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
		List paylists = new ArrayList();
		List<Map> paylist = null;
			try {

				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("id", ids[i]);						
					paylist = (List<Map>)  DataAccessor.query("collectionManage.queryOwnLastPrice", context.contextMap, DataAccessor.RS_TYPE.LIST);
					paylists.add(paylist);
				}
					//生成支付表PDF名字
						
				for (Object object : paylists) {
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
					Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
					Font fa = new Font(bfChinese, 15, Font.BOLD);
					// 数字格式
					NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
					nfFSNum.setGroupingUsed(true);
					nfFSNum.setMaximumFractionDigits(2);
					// 页面设置
					Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
					Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					//打开文档
					document.open();
					//支付表PDF名字的定义
					@SuppressWarnings("unused")
					String strFileName = "";
					strFileName = "PayList.pdf";
					// 表格列宽定义
					float[] widthsStl = {0.1f,0.20f,0.20f,0.20f,0.20f,0.20f,0.20f,0.20f,0.25f};
					int iCnt = 0;			
					float[] widthsPPCa = { 1f };
					PdfPTable tT = new PdfPTable(widthsPPCa);
					tT.setWidthPercentage(100f);
					tT.addCell(makeCellWithNoBorder("本息摊还表", PdfPCell.ALIGN_CENTER, fa));
					document.add(tT);
					document.add(new Paragraph("\n"));
					
					List<Map> tempPaylist = (List<Map>) object;												
					PdfPTable tableHdr1 = new PdfPTable(1);
					tableHdr1.addCell(makeCell("致："+tempPaylist.get(0).get("CUST_NAME").toString(),PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr1);

					PdfPTable tableHdr2 = new PdfPTable(1);
					tableHdr2.addCell(makeCell("贵公司与我司签订的融资租赁合同编号为："+tempPaylist.get(0).get("LEASE_CODE").toString()+"，其本金摊还表如下：",PdfPCell.ALIGN_LEFT, FontDefault));				
					document.add(tableHdr2);				

					PdfPTable tableHdr3 = new PdfPTable(1);
					tableHdr3.addCell(makeCell("融资租赁还款期间",PdfPCell.ALIGN_LEFT, FontColumn));					
					document.add(tableHdr3);
								
					PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

					tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("未税租金",PdfPCell.ALIGN_CENTER, FontColumn));	
					tableHdr4.addCell(makeCell("平均增值税",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("含税租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("合同租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("本金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("利息",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("剩余本金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("实际增值税",PdfPCell.ALIGN_CENTER, FontColumn));
					double monthPriceCount=0.00;
					double renpriceCount=0.00;
					double ownpriceCount=0.00;
					double irrMonthPriceTax=0.00;
					double totalIrrMonthPrice=0.00;
					double totalIrrMonthPriceTax=0.00;
					double totalAVEValueAdded=0.00;
					double totalValueAddedTrue=0.00;
					for(int j=0;j<tempPaylist.size();j++){
						monthPriceCount+=Double.parseDouble(tempPaylist.get(j).get("MONTH_PRICE").toString());
						ownpriceCount+=Double.parseDouble(tempPaylist.get(j).get("OWN_PRICE").toString());
						renpriceCount+=Double.parseDouble(tempPaylist.get(j).get("REN_PRICE").toString());
						irrMonthPriceTax=Double.parseDouble(tempPaylist.get(j).get("IRR_MONTH_PRICE").toString())+Double.parseDouble(tempPaylist.get(j).get("VALUE_ADDED_TAX").toString());
						totalIrrMonthPrice+=Double.parseDouble(tempPaylist.get(j).get("IRR_MONTH_PRICE").toString());
						totalIrrMonthPriceTax+=irrMonthPriceTax;
						totalAVEValueAdded+=Double.parseDouble(tempPaylist.get(j).get("VALUE_ADDED_TAX").toString());
						totalValueAddedTrue+=Double.parseDouble(tempPaylist.get(j).get("VALUE_ADDED_TAX_TRUE").toString());
						iCnt++;
						tableHdr4.addCell(makeCell("第"+tempPaylist.get(j).get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			

						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"VALUE_ADDED_TAX",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
						tableHdr4.addCell(makeCell(nfFSNum.format(irrMonthPriceTax),PdfPCell.ALIGN_RIGHT, FontColumn));	
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));				
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"OWN_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));					
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"REN_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"LAST_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
						tableHdr4.addCell(makeCell(updateMoney(tempPaylist.get(j),"VALUE_ADDED_TAX_TRUE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
					}	
					tableHdr4.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontColumn));			
					tableHdr4.addCell(makeCell(nfFSNum.format(totalIrrMonthPrice),PdfPCell.ALIGN_RIGHT, FontColumn));				
					tableHdr4.addCell(makeCell(nfFSNum.format(totalAVEValueAdded),PdfPCell.ALIGN_RIGHT, FontColumn));					
					tableHdr4.addCell(makeCell(nfFSNum.format(totalIrrMonthPriceTax),PdfPCell.ALIGN_RIGHT, FontColumn));
					tableHdr4.addCell(makeCell(nfFSNum.format(monthPriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));				
					tableHdr4.addCell(makeCell(nfFSNum.format(ownpriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));					
					tableHdr4.addCell(makeCell(nfFSNum.format(renpriceCount),PdfPCell.ALIGN_RIGHT, FontColumn));
					tableHdr4.addCell(makeCell("",PdfPCell.ALIGN_RIGHT, FontColumn));
					tableHdr4.addCell(makeCell(nfFSNum.format(totalValueAddedTrue),PdfPCell.ALIGN_RIGHT, FontColumn));
					document.add(tableHdr4);
					PdfPTable tableHdr5 = new PdfPTable(1);
					tableHdr5.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);
					tableHdr5.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);
					tableHdr5.addCell(makeCellWithNoBorder("出租方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_RIGHT, FontColumn));
					document.add(tableHdr5);					
				
				document.add(Chunk.NEXTPAGE);	
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition","attachment; filename=payList");
			}
				
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o); 
			o.flush();	
			o.close();
				
			//add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
			   		 "导出 本息摊还表",
		   		 	 "合同浏览导出 租金支付明细表",
		   		 	 null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
		   		 	 1,
		   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
		   		 	 DataUtil.longUtil(0),
		   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	

	
	@SuppressWarnings("unchecked")
	public void expPayToPdfByValueAdded(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
		Map paylist = null;
		@SuppressWarnings("unused")
		Map payMax = null;
		List<Map> paylines = null;

			try {
				// 字体设置
				BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
				Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
				Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
				Font fa = new Font(bfChinese, 15, Font.BOLD);
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				//打开文档
				document.open();
				//支付表PDF名字的定义
				@SuppressWarnings("unused")
				String strFileName = "";
				// 表格列宽定义
				float[] widthsStl = {0.1f,0.25f,0.25f,0.25f,0.25f,0.25f};
				int iCnt = 0;			
				float[] widthsPPCa = { 1f };
				PdfPTable tT = new PdfPTable(widthsPPCa);
				tT.setWidthPercentage(100f);
				tT.addCell(makeCellWithNoBorder("租金支付明细表", PdfPCell.ALIGN_CENTER, fa));
				document.add(tT);
				document.add(new Paragraph("\n"));
				if(context.contextMap.get("hu_rentcontractexportcredit")!=null){
					if(Boolean.parseBoolean(context.contextMap.get("hu_rentcontractexportcredit").toString())){
						PdfPTable tableHdr1 = new PdfPTable(2);
						tableHdr1.addCell(makeCell("租金缴纳银行：",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr1.addCell(makeCell("交通银行苏州分行工业园区支行",PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr1);

						PdfPTable tableHdr2 = new PdfPTable(2);
						tableHdr2.addCell(makeCell("户名:",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr2.addCell(makeCell(Constants.COMPANY_NAME,PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr2);				
						String custcode="";
						String leasecode="";
						if(context.contextMap.get("creditMap")!=null){
							if(((Map)context.contextMap.get("creditMap")).get("CUST_CODE")!=null){
								custcode=((Map)context.contextMap.get("creditMap")).get("CUST_CODE").toString();
							}
							if(((Map)context.contextMap.get("creditMap")).get("LEASE_CODE")!=null){
								leasecode=((Map)context.contextMap.get("creditMap")).get("LEASE_CODE").toString();
							}
						}
						PdfPTable tableHdr3 = new PdfPTable(4);
						tableHdr3.addCell(makeCell("账号：",PdfPCell.ALIGN_CENTER, FontColumn));					
						tableHdr3.addCell(makeCell("                        ",PdfPCell.ALIGN_CENTER, FontDefault));
						tableHdr3.addCell(makeCell("合同号",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr3.addCell(makeCell(leasecode,PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr3);
						paylist=(Map)context.contextMap.get("paylist");				
						//融资租赁还款计划
						strFileName = "PayLst-" +leasecode + ".pdf";	
						paylines = (List<Map>)paylist.get("paylines");
						
						//保证金每期抵扣之租金
						
						Float ave=0F;
						if(context.contextMap.get("schema")!=null){
							Map schema = ((Map)context.contextMap.get("schema"));
							if(schema.get("PLEDGE_AVG_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVG_PRICE").toString());
							} else if(schema.get("PLEDGE_AVE_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVE_PRICE").toString());
							}
						}
					
						PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

						tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("合同各期租金",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("保证金每期抵扣之租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("未税实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
						//tableHdr4.addCell(makeCell("平均增值税",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("含税实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("租金支付日",PdfPCell.ALIGN_CENTER, FontColumn));
						Double totalValueAdded=0d;
						Double irrMonthPriceTaxCount=0d;
						Double irrMonthPriceCount=0d;
						for (Iterator iterator = paylines.iterator(); iterator.hasNext();) {
							Map map = (Map) iterator.next();
							iCnt++;
							tableHdr4.addCell(makeCell("第"+map.get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			
							if(map.get("PAY_DATE")==null){
								map.put("PAY_DATE","");
							}
							
							tableHdr4.addCell(makeCell(updateMoney(map,"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
							Double ave1 =Double.parseDouble(map.get("MONTH_PRICE").toString()) - Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) ;
							tableHdr4.addCell(makeCell(ave1==0?"0.00":nfFSNum.format(ave1),PdfPCell.ALIGN_RIGHT, FontColumn));	
//							tableHdr4.addCell(makeCell(ave==0?"0.00":nfFSNum.format(ave),PdfPCell.ALIGN_RIGHT, FontColumn));
							tableHdr4.addCell(makeCell(updateMoney(map,"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
							//tableHdr4.addCell(makeCell(updateMoney(map,"VALUE_ADDED_TAX",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
							double irrMonthPriceTax=DataUtil.doubleUtil(map.get("VALUE_ADDED_TAX"))+DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"));
							tableHdr4.addCell(makeCell(irrMonthPriceTax==0?"0.00":nfFSNum.format(irrMonthPriceTax),PdfPCell.ALIGN_RIGHT, FontColumn));	
							
							tableHdr4.addCell(makeCell(" ",PdfPCell.ALIGN_CENTER, FontColumn));	
							//add by zhangbo 统计总值
							//差额
							totalValueAdded+=DataUtil.doubleUtil(map.get("VALUE_ADDED_TAX"));
							//含税实际支付租金总和
							irrMonthPriceTaxCount +=irrMonthPriceTax;
							//未税实际支付租金
							irrMonthPriceCount +=DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"));
							
						}				
						document.add(tableHdr4);
						
						PdfPTable tableHdr5 = new PdfPTable(1);
						tableHdr5.addCell(makeCellWithNoBorder("承租人签章：",PdfPCell.ALIGN_LEFT, FontColumn));
						document.add(tableHdr5);					
						
						PdfPTable tableHdr6 = new PdfPTable(1);
						tableHdr6.addCell(makeCellWithNoBorder("注：每期应缴租金为含税实际支付租金，含税实际支付租金共￥"+nfFSNum.format(irrMonthPriceTaxCount)+"元整，未税实际支付租金共￥"+nfFSNum.format(irrMonthPriceCount)+"元整，差额为￥"+nfFSNum.format(totalValueAdded)+"元整，做为增值税申报的抵扣税额",PdfPCell.ALIGN_LEFT, FontColumn));
						document.add(tableHdr6);
						
						document.add(Chunk.NEXTPAGE);
					}
				}else{
				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("RECP_ID", ids[i]);						
					//表头 PDF
					paylist = (Map) DataAccessor.query("collectionManage.readPaylistByIdfForExpPay", context.contextMap, DataAccessor.RS_TYPE.MAP);
						//生成支付表PDF名字
					strFileName = "PayLst-" + paylist.get("RECP_CODE").toString() + ".pdf";	
																	
					PdfPTable tableHdr1 = new PdfPTable(2);
					tableHdr1.addCell(makeCell("租金缴纳银行：",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr1.addCell(makeCell("交通银行苏州分行工业园区支行",PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr1);

					PdfPTable tableHdr2 = new PdfPTable(2);
					tableHdr2.addCell(makeCell("户名:",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr2.addCell(makeCell(Constants.COMPANY_NAME,PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr2);				
					
					PdfPTable tableHdr3 = new PdfPTable(4);
					tableHdr3.addCell(makeCell("账号：",PdfPCell.ALIGN_CENTER, FontColumn));					
					tableHdr3.addCell(makeCell("                       ",PdfPCell.ALIGN_CENTER, FontDefault));
					tableHdr3.addCell(makeCell("合同号",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr3.addCell(makeCell(paylist.get("LEASE_CODE").toString(),PdfPCell.ALIGN_CENTER, FontDefault));
					document.add(tableHdr3);
									
					//融资租赁还款计划
					paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);		
					
					//保证金每期抵扣之租金
					Float ave=(Float)DataAccessor.query("collectionManage.pledgeAvgPrice",context.contextMap,DataAccessor.RS_TYPE.OBJECT);
					PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

					tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("合同各期租金",PdfPCell.ALIGN_CENTER, FontColumn));				
					tableHdr4.addCell(makeCell("保证金每期抵扣之租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("未税实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
					//tableHdr4.addCell(makeCell("平均增值税",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("含税实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
					tableHdr4.addCell(makeCell("租金支付日",PdfPCell.ALIGN_CENTER, FontColumn));
					
					Double totalValueAdded=0d;
					Double irrMonthPriceTaxCount=0d;
					Double irrMonthPriceCount=0d;
					for (Iterator iterator = paylines.iterator(); iterator.hasNext();) {
						Map map = (Map) iterator.next();
						iCnt++;
						tableHdr4.addCell(makeCell("第"+map.get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			
						if(map.get("PAY_DATE")==null){
							map.put("PAY_DATE","");
						}
			
						tableHdr4.addCell(makeCell(updateMoney(map,"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
						Double ave1 =Double.parseDouble(map.get("MONTH_PRICE").toString()) - Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) ;
						tableHdr4.addCell(makeCell(ave1==0?"0.00":nfFSNum.format(ave1),PdfPCell.ALIGN_RIGHT, FontColumn));	
//						tableHdr4.addCell(makeCell(ave==0?"0.00":nfFSNum.format(ave),PdfPCell.ALIGN_RIGHT, FontColumn));
						tableHdr4.addCell(makeCell(updateMoney(map,"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
						//tableHdr4.addCell(makeCell(updateMoney(map,"VALUE_ADDED_TAX",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));
						double irrMonthPriceTax=Double.parseDouble(map.get("VALUE_ADDED_TAX").toString())+Double.parseDouble(map.get("IRR_MONTH_PRICE").toString());
						tableHdr4.addCell(makeCell(irrMonthPriceTax==0?"0.00":nfFSNum.format(irrMonthPriceTax),PdfPCell.ALIGN_RIGHT, FontColumn));	
						//add by zhangbo 统计总值
						//差额
						totalValueAdded+=Double.parseDouble(map.get("VALUE_ADDED_TAX").toString());
						//含税实际支付租金总和
						irrMonthPriceTaxCount +=irrMonthPriceTax;
						//未税实际支付租金
						irrMonthPriceCount +=DataUtil.doubleUtil(map.get("IRR_MONTH_PRICE"));
						tableHdr4.addCell(makeCell(" " ,PdfPCell.ALIGN_CENTER, FontColumn));	
					}				
					document.add(tableHdr4);
					
					PdfPTable tableHdr5 = new PdfPTable(1);
					tableHdr5.addCell(makeCellWithNoBorder("承租人签章：",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);	
					
					PdfPTable tableHdr6 = new PdfPTable(1);
					tableHdr6.addCell(makeCellWithNoBorder("注：每期应缴租金为含税实际支付租金，含税实际支付租金共￥"+nfFSNum.format(irrMonthPriceTaxCount)+"元整，未税实际支付租金共￥"+nfFSNum.format(irrMonthPriceCount)+"元整，差额为￥"+nfFSNum.format(totalValueAdded)+"元整，做为增值税申报的抵扣税额",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr6);
					document.add(Chunk.NEXTPAGE);	
				}
				}
				
				document.close();
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=payList");
				
				ServletOutputStream o = context.response.getOutputStream();

				baos.writeTo(o); 
				o.flush();				
				o.close();
				
				//add by ShenQi 插入系统日志
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 租金支付明细表",
			   		 	 "合同浏览导出 租金支付明细表",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	
	
//Add by Michael 2012 09-25 For 新品回租案件 
	public void exportPaylistBeforeByValueAdded(Context context) {
		
		 Map outputMap = new HashMap();
		 List errList = context.errList;
		 Map creditMap = null;
		 Map schema = null;
		 Map paylist = null;
		 //Map memoMap = null;
		 try{
			 context.contextMap.put("PRCD_ID", context.contextMap.get("credit_id"));
			 List contractinfo=(List) DataAccessor.query("exportContractPdf.judgeExitContract", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    	//查找合同的相关信息		    	
		    if(contractinfo.size()==0){	
			 	context.contextMap.put("data_type", "客户来源");
				creditMap = (Map) DataAccessor.query("creditReportManage.selectCreditBaseInfoByH", context.contextMap, DataAccessor.RS_TYPE.MAP);
				outputMap.put("creditMap", creditMap);
				//查询方案
				schema  = (Map) DataAccessor.query(
						"creditReportManage.selectCreditScheme",
						context.contextMap, DataAccessor.RS_TYPE.MAP);
//				schema = (Map) DataAccessor.query("creditReportManage.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
				// 查询应付租金列表
				List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
				
				// 解压irrMonthPaylines到每一期的钱
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				// 
				if (schema != null) {
					schema.put("TOTAL_PRICE", schema.get("LEASE_TOPRIC"));
					schema.put("LEASE_PERIOD", schema.get("LEASE_TERM"));
					schema.put("LEASE_TERM", schema.get("LEASE_COURSE"));
					// 
				
					if (irrMonthPaylines.size() > 0) {
						// 如果应付租金存在，则以应付租金的方式计算
						paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);

					} else {
						// 如果应付租金不存在，则以年利率(合同利率)的方式计算
						paylist = StartPayService.createCreditPaylist(schema,new ArrayList<Map>());
					}
					paylist.put("PLEDGE_ENTER_MCTOAG", schema.get("PLEDGE_ENTER_MCTOAG"));
					paylist.put("PLEDGE_ENTER_AGRATE", schema.get("PLEDGE_ENTER_AGRATE"));		
					
					
				}
				context.contextMap.put("paylist", paylist);
				context.contextMap.put("creditMap", creditMap);
				context.contextMap.put("schema", schema);
				context.contextMap.put("rePaylineList", rePaylineList);
				context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				context.contextMap.put("hu_rentcontractexportcredit", "true");
		    }else{
		    	HashMap contractRectid=(HashMap)contractinfo.get(0);
		    	context.contextMap.put("RECT_ID", contractRectid.get("RECT_ID"));
		    	Map rentContract = (Map) DataAccessor.query("rentContract.readRentContractByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
		    	List<Map> equipList = (List<Map>) DataAccessor.query("exportContractPdf.queryEquipmentByRectIdHu", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    	context.contextMap.put("equipList", equipList);
				Map contractSchema = (Map) DataAccessor.query("rentContract.readSchemaByRectId", context.contextMap, DataAccessor.RS_TYPE.MAP);
				
				List<Map> insureCompanyList = (List<Map>) DataAccessor.query("insuCompany.queryInsureCompanyListForSelect", null, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("insureCompanyList", insureCompanyList);
				
				List<Map> insureTypeList = (List<Map>) DataAccessor.query("insureType.queryInsureTypeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("insureTypeList", insureTypeList);
				context.contextMap.put("insureTypeJsonList", Output.serializer.serialize(insureTypeList));
				List<Map> insureList = (List<Map>) DataAccessor.query("rentContract.readInsureByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);

				List<Map> otherFeeList = (List<Map>) DataAccessor.query("rentContract.readOtherFeeByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				
		    	List<Map> oldPaylists = (List<Map>) DataAccessor.query("rentContractPact.queryPaylistByRectId", context.contextMap, DataAccessor.RS_TYPE.LIST);
				//
				// contain RECS_ID
		    	List<Map> irrMonthPaylines = (List<Map>) DataAccessor.query("rentContract.readSchemaIrrByRecsId", contractSchema, DataAccessor.RS_TYPE.LIST);
		    	context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
				
				paylist = StartPayService.createPaylist(rentContract, contractSchema, equipList, insureList, otherFeeList, oldPaylists, rePaylineList,irrMonthPaylines);
				paylist.put("PLEDGE_ENTER_AGRATE", contractSchema.get("PLEDGE_ENTER_AGRATE"));
				paylist.put("PLEDGE_ENTER_MCTOAG", contractSchema.get("PLEDGE_ENTER_MCTOAG"));
				context.contextMap.put("paylist", paylist);
				context.contextMap.put("creditMap", rentContract);
				context.contextMap.put("schema", contractSchema);
				context.contextMap.put("rePaylineList", rePaylineList);
				context.contextMap.put("irrMonthPaylines", irrMonthPaylines);
				context.contextMap.put("hu_rentcontractexportcredit", "true");
		    }
			 this.expPayToPdfByValueAdded(context);
		 }catch(Exception e){
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		 }
	 }
	
	//导出本金收据
	public void expOwnPricePrincipal(Context context) {
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
		Map paylist = null;
		Map payMax = null;
		List<Map> paylines = null;

			try {
				// 字体设置
				BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
				Font FontColumn = new Font(bfChinese, 10, Font.BOLD);
				Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
				Font fa = new Font(bfChinese, 15, Font.BOLD);
				Font FontSmall = new Font(bfChinese,8, Font.NORMAL);
				Font FontSmall2 = new Font(bfChinese,5, Font.NORMAL);
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A5); // 定义A5页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				PdfPCell cell=null;
				//打开文档
				document.open();
				//支付表PDF名字的定义
				String strFileName = "";
				// 表格列宽定义
				float[] widthsStl = {0.1f,0.20f,0.20f,0.20f,0.25f};
				int iCnt = 0;			
				float[] widthsPPCa = { 1f };
				PdfPTable tT = new PdfPTable(widthsPPCa);
				tT.setWidthPercentage(100f);
				PdfPTable tlogo=new PdfPTable(new float[]{5f,95f});
				String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
				Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");

				image.scaleAbsoluteHeight(20);
				image.scaleAbsoluteWidth(20);			
				
				cell=new PdfPCell();
				cell.addElement(image);
				cell.setBorder(0);
				tlogo.addCell(cell);
				
				Chunk chunk1=new Chunk(Constants.COMPANY_NAME,FontSmall);
				Chunk chunk2=new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
				cell=new PdfPCell();
				cell.addElement(chunk1);
				cell.addElement(chunk2);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				cell.setBorder(0);
				tlogo.addCell(cell);
				
				cell=new PdfPCell(tlogo);
				cell.setColspan(12);
				cell.setPaddingBottom(5);
				cell.setBorder(0);
				tT.addCell(cell);
				
				String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
				
				for (int i = 0; i < ids.length; i++) {
					context.contextMap.put("RECP_ID", ids[i]);						
					//表头 PDF
					paylist = (Map) DataAccessor.query("collectionManage.readPaylistByIdfForExpPay", context.contextMap, DataAccessor.RS_TYPE.MAP);
					if(context.contextMap.get("hu_rentcontractexportcredit")!=null){
					tT.addCell(makeCellWithNoBorder("本        金        收        据", PdfPCell.ALIGN_CENTER, fa));
					document.add(tT);
					document.add(new Paragraph("\n"));
					if(Boolean.parseBoolean(context.contextMap.get("hu_rentcontractexportcredit").toString())){
						PdfPTable tableHdr1 = new PdfPTable(2);
						tableHdr1.addCell(makeCell("租金缴纳银行：",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr1.addCell(makeCell("交通银行苏州分行工业园区支行",PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr1);

						PdfPTable tableHdr2 = new PdfPTable(2);
						tableHdr2.addCell(makeCell("户名:",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr2.addCell(makeCell(Constants.COMPANY_NAME,PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr2);				
						String custcode="";
						String leasecode="";
						if(context.contextMap.get("creditMap")!=null){
							if(((Map)context.contextMap.get("creditMap")).get("CUST_CODE")!=null){
								custcode=((Map)context.contextMap.get("creditMap")).get("CUST_CODE").toString();
							}
							if(((Map)context.contextMap.get("creditMap")).get("LEASE_CODE")!=null){
								leasecode=((Map)context.contextMap.get("creditMap")).get("LEASE_CODE").toString();
							}
						}
						PdfPTable tableHdr3 = new PdfPTable(4);
						tableHdr3.addCell(makeCell("账号：",PdfPCell.ALIGN_CENTER, FontColumn));					
						tableHdr3.addCell(makeCell("                        ",PdfPCell.ALIGN_CENTER, FontDefault));
						tableHdr3.addCell(makeCell("合同号",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr3.addCell(makeCell(leasecode,PdfPCell.ALIGN_CENTER, FontDefault));
						document.add(tableHdr3);
						paylist=(Map)context.contextMap.get("paylist");				
						//融资租赁还款计划
						strFileName = "Principal.pdf";	
						paylines = (List<Map>)paylist.get("paylines");
						
						//保证金每期抵扣之租金
						
						Float ave=0F;
						if(context.contextMap.get("schema")!=null){
							Map schema = ((Map)context.contextMap.get("schema"));
							if(schema.get("PLEDGE_AVG_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVG_PRICE").toString());
							} else if(schema.get("PLEDGE_AVE_PRICE")!=null){
								ave=Float.parseFloat(((Map)context.contextMap.get("schema")).get("PLEDGE_AVE_PRICE").toString());
							}
						}
					
						PdfPTable tableHdr4 = new PdfPTable(widthsStl);		

						tableHdr4.addCell(makeCell("期数",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("合同各期租金",PdfPCell.ALIGN_CENTER, FontColumn));				
						tableHdr4.addCell(makeCell("保证金每期抵扣之租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("实际支付租金",PdfPCell.ALIGN_CENTER, FontColumn));
						tableHdr4.addCell(makeCell("租金支付日",PdfPCell.ALIGN_CENTER, FontColumn));
					
						for (Iterator iterator = paylines.iterator(); iterator.hasNext();) {
							Map map = (Map) iterator.next();
							iCnt++;
							tableHdr4.addCell(makeCell("第"+map.get("PERIOD_NUM").toString()+"期",PdfPCell.ALIGN_CENTER, FontColumn));			
							if(map.get("PAY_DATE")==null){
								map.put("PAY_DATE","");
							}
							
							tableHdr4.addCell(makeCell(updateMoney(map,"MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));	
							Double ave1 =Double.parseDouble(map.get("MONTH_PRICE").toString()) - Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) ;
							tableHdr4.addCell(makeCell(ave1==0?"0.00":nfFSNum.format(ave1),PdfPCell.ALIGN_RIGHT, FontColumn));	
//							tableHdr4.addCell(makeCell(ave==0?"0.00":nfFSNum.format(ave),PdfPCell.ALIGN_RIGHT, FontColumn));	
							tableHdr4.addCell(makeCell(updateMoney(map,"IRR_MONTH_PRICE",nfFSNum),PdfPCell.ALIGN_RIGHT, FontColumn));					
							//tableHdr4.addCell(makeCell(DateFormat.getDateInstance().format(map.get("PAY_DATE")).toString(),PdfPCell.ALIGN_CENTER, FontColumn));	
							tableHdr4.addCell(makeCell(" ",PdfPCell.ALIGN_CENTER, FontColumn));	
						}				
						document.add(tableHdr4);
						
						PdfPTable tableHdr5 = new PdfPTable(1);
						tableHdr5.addCell(makeCellWithNoBorder("承租人签章：",PdfPCell.ALIGN_LEFT, FontColumn));
						document.add(tableHdr5);					
						
						document.add(Chunk.NEXTPAGE);
					}
				}
				}
				
				document.close();
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=expOwnPricePrincipal");
				
				ServletOutputStream o = context.response.getOutputStream();

				baos.writeTo(o); 
				o.flush();				
				o.close();
				
				//add by ShenQi 插入系统日志
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 本金收据",
			   		 	 "本金收据",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的导出本金收据使用导出本金收据",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	
	
	private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);

		return objCell;
	}
	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorder(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}
	/** 创建 有边框 合并 单元格 */
	@SuppressWarnings("unused")
	private PdfPCell makeCellSetColspan(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		
		return objCell;
	}
	/** 创建 无边框 合并 单元格 */
	@SuppressWarnings("unused")
	private PdfPCell makeCellSetColspanWithNoBorder(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
		return objCell;
	}
	/** ￥0.00 */
	@SuppressWarnings("unchecked")
	private String updateMoney(Map map,String content,NumberFormat nfFSNum) {
		String str="";
		if(map.get(content)==null){
			str+="0.00";
			return str;
		}else if(map.get(content).toString().equals("0")){
			str+="0.00";
			return str;
		}
		else{
			str+=nfFSNum.format(Double.parseDouble(map.get(content).toString()));
			return str;
		}	
	}		
}
