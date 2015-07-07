package com.brick.contract.util;

import com.brick.service.core.AService;
import com.brick.service.core.Output;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.CollectionConstants;
import com.brick.collection.service.StartPayService;
import com.brick.credit.service.CreditPaylistService;
import com.brick.log.service.LogPrint;

//回租合同
public class LeaseBackContractPDF extends AService {
	Log logger = LogFactory.getLog(LeaseBackContractPDF.class);

	@SuppressWarnings("unchecked")
	public void preLeaseBackContractPdf(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.getLeaseBackContractDateMap(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.getLeaseBackContractDateMap(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void getLeaseBackContractDateMap(Context context) {
		String[] con = null;
		ArrayList creditmapandleasehold = new ArrayList();
		con = (String[]) context.contextMap.get("credtdxx");
		for (int ii = 0; ii < con.length; ii++) {
			HashMap outputMap = new HashMap();
			List leaseholds = new ArrayList();
			context.contextMap.put("credit_id", con[ii]);
			try {

				context.contextMap.put("PRCD_ID",
						context.contextMap.get("credit_id"));
				List contractinfo = (List) DataAccessor.query(
						"exportContractPdf.judgeExitContract",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if (contractinfo.size() == 0) {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				} else {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryEquipmentByRectIdForleaseholds",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				}

				outputMap.put("leaseholds", leaseholds);
				creditmapandleasehold.add(outputMap);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		this.expLeaseBackBuyPdfs(context, creditmapandleasehold);
	}

	/**
	 * 导出回租买卖合同
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expLeaseBackBuyPdfs(Context context,
			ArrayList creditmapandleasehold) {

		ByteArrayOutputStream baos = null;
		ArrayList leaseholds = new ArrayList();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();
			for (int ii = 0; ii < creditmapandleasehold.size(); ii++) {
				HashMap outputMap = (HashMap) creditmapandleasehold.get(ii);
				leaseholds = (ArrayList) outputMap.get("leaseholds");
				HashMap baseinfo = (HashMap) leaseholds.get(0);
				String code = "0";
				String leasetopric="";
				if (baseinfo.get("LEASE_TOPRIC") == null) {
					leasetopric = "  ";
				} else {
					leasetopric = baseinfo.get("LEASE_TOPRIC").toString();
				}
				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}
				// 承租方地址
				String custaddress = "";
				if (baseinfo.get("CORP_REGISTE_ADDRESS") == null) {
					custaddress = "  ";
				} else {
					custaddress = baseinfo.get("CORP_REGISTE_ADDRESS")
							.toString();
				}
				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}

				// 租赁物设置场所

				String eAddress = "";
				if (baseinfo.get("EQUPMENT_ADDRESS") == null) {
					eAddress = "  ";
				} else {
					eAddress = baseinfo.get("EQUPMENT_ADDRESS").toString();
				}
				PdfPTable tT = new PdfPTable(10);
				//页眉
//				PdfPTable table = new PdfPTable(10);
//				table.setWidthPercentage(100f);
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,12,0.5f,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
				Phrase phrase = new Phrase();
//				phrase.add(table);
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("        ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("买卖合同", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				// 表头的相关信息,第一行
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder("合同编号：" + Lcode,
						PdfPCell.ALIGN_RIGHT, FontDefault22, 7));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_RIGHT, FontDefault22, 1));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				i++;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;

				// 文字部分
				String[] createdate = new String[3];
				if (((HashMap) (leaseholds.get(0))).get("CREATE_DATE") != null) {
					createdate = ((HashMap) (leaseholds.get(0)))
							.get("CREATE_DATE").toString().substring(0, 10)
							.split("-");
					if (createdate[0].equals("1900")) {
						createdate[0] = "        ";
						createdate[1] = "        ";
						createdate[2] = "        ";
					}
				} else {
					createdate[0] = "        ";
					createdate[1] = "        ";
					createdate[2] = "        ";
				}


				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				phrase = new Phrase("立买卖合同双方：", FontDefault2);
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				i++;
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("买受人（以下简称甲方）：", FontDefault2)); 
	    		phrase.add(new Phrase(Constants.COMPANY_NAME , FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("出卖人（以下简称乙方）：", FontDefault2)); 
	    		phrase.add(new Phrase(custname , FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("        兹经充分友好协商，甲、乙双方本着自愿、平等、诚实信用的原则，签订如下合同条", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("款，以资共同恪守履行。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第一条	 标的物名称、型号、规格及数量", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 

				// 第一个子表开始,第一行
				// 租赁物的数量是动态增加的
				tT.addCell(makeCellSetColspanNoBorder("      ",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspan("名称",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT.addCell(makeCellSetColspan("厂牌",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT.addCell(makeCellSetColspan("型号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("机号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("数量", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				tT.addCell(makeCellSetColspan("附注", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				tT.addCell(makeCellSetColspanNoBorder("", PdfPCell.ALIGN_LEFT,
						FontDefault,1));
				i++;
				String thing_kind = "";
				String name = "";
				String brand = "";
				List manufacturerList = new ArrayList();
				for (int k = 0; k < leaseholds.size(); k++) {

					String thingname = "";
					if (((HashMap) leaseholds.get(k)).get("THING_NAME") == null) {
						thingname = "";
					} else {
						thingname = ((HashMap) leaseholds.get(k)).get(
								"THING_NAME").toString();
					}
					String modelspec = "";
					if (((HashMap) leaseholds.get(k)).get("MODEL_SPEC") == null) {
						modelspec = "";
					} else {
						modelspec = ((HashMap) leaseholds.get(k)).get(
								"MODEL_SPEC").toString();
					}

					if (((HashMap) leaseholds.get(k)).get("BRAND") == null) {
						brand = "";
					} else {
						brand = ((HashMap) leaseholds.get(k)).get("BRAND")
								.toString();
					}
					// 厂牌
					if (((HashMap) leaseholds.get(k)).get("TYPE_NAME") == null) {
						thing_kind = "";
					} else {
						thing_kind = ((HashMap) leaseholds.get(k)).get(
								"TYPE_NAME").toString();
					}
					manufacturerList.add(thing_kind);
					if (((HashMap) leaseholds.get(k)).get("NAME") == null) {
						name = "";
					} else {
						name = ((HashMap) leaseholds.get(k)).get("NAME")
								.toString();
					}
					String unit = "";
					if (((HashMap) leaseholds.get(k)).get("UNIT") == null) {
						unit = "";
					} else {
						unit = ((HashMap) leaseholds.get(k)).get("UNIT")
								.toString();
					}
					String amount = "";
					if (((HashMap) leaseholds.get(k)).get("AMOUNT") == null) {
						amount = "";
					} else {
						amount = ((HashMap) leaseholds.get(k)).get("AMOUNT")
								.toString();
					}
					tT.addCell(makeCellSetColspanNoBorder("      ",
							PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT.addCell(makeCellSetColspan(
							thingname, PdfPCell.ALIGN_LEFT, FontDefault222,
							2));
					tT.addCell(makeCellSetColspan(
							thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,
							2));
					tT.addCell(makeCellSetColspan(modelspec,
							PdfPCell.ALIGN_LEFT, FontDefault222, 1));
					tT.addCell(makeCellSetColspan(name,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan(amount + unit,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan("  ",
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspanNoBorder("",
							PdfPCell.ALIGN_LEFT, FontDefault,1));
					i++;
				}
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第二条	标的物总价款及付款方式", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	标的物总价款：人民币设备金额 ", FontDefault2)); 
	    		phrase.add(new Phrase(SimpleMoneyFormat.getInstance().format(new Double(leasetopric)) , FontUnde2)); 
	    		phrase.add(new Phrase("（RMB￥  ", FontDefault2));
	    		phrase.add(new Phrase(nfFSNum.format(new Double(leasetopric))  , FontUnde2));
	    		phrase.add(new Phrase("）", FontDefault2));
	    		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	甲、乙双方签订本合同，是基于乙方将目标物出售予甲方后，再由甲方将目标物出租予", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("乙方，据此甲方得以目标物总价款其中一部分，作为双方就标的物所签订融资租赁合同的保", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("证金。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("3.	乙方将标的物和第四条规定之单据交付甲方之日起 七 日内，甲方支付合同价款予乙方", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("或乙方指定之第三人。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第三条	 标的物所有权及交付地点", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	自本合同生效之日起，该标的物之所有权转移予甲方，但标的物", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   ■①仍由乙方占有保管，乙方占有保管该标的物的期间", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("      自 20   年    月    日至 20   年    月    日；或/和", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   □②根据甲方指示的时间由乙方交付甲方或其指定之第三人。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	标的物需要拆离、搬动、装卸、运输、安装、调试和测试的，由乙方负责并承担相关", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("费用。乙方从事上述行为时，应尽善良占有人之注意义务，不得毁损标的物。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	  
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("3.	交付地点为 ", FontDefault2));  
	    		phrase.add(new Phrase( eAddress , FontUnde2)); 
	    		phrase.add(new Phrase("，甲方另有指示的，按照指示的地点交货。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第四条	标的物单据的转移", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       乙方应在本合同签订之日向甲方提供该标的物的包括但不限于购买发票、购买合同、购", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("买时的检验凭证、维修凭证、产品说明等相关单据和资料。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第五条	质量瑕疵担保责任", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(一)	该标的物须符合以下质量要求：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    1.具备产品应当具备的使用性能，但是，合同签订前对产品存在使用性能的瑕疵作出书面", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    说明的除外；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    2.符合在产品或者其包装上注明采用的产品标准，符合以产品说明、实物样品等方式表明", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    的质量状况。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    3.不存在危及人身、财产安全的不合理的危险，有保障人体健康和人身、财产安全的国家", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("        标准、行业标准的，应当符合该标准；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(二)	乙方对于该标的物负有质量瑕疵担保责任，该标的物若有如下情况之一，乙方在签订", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 

	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     本合同前，须明确告知甲方。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     1.	不具备产品应当具备的使用性能的；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     2.	不符合在产品或者其包装上注明采用的产品标准的；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     3.	不符合以产品说明、实物样品等方式表明的质量状况的。该标的物有质量瑕疵或由于", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         设计、制造不良而减损通常效用或预定效力或故障。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     乙方未履行告知义务的，甲方可以解除合同；标的物上存在其它重大、显著、根本性瑕", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("  疵，以致实际上剥夺了买受人根据合同有权期待获得的标的物的价值或效用的，甲方可拒", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("  绝接受标的物或解除合同。因此给甲方造成损失的，乙方应当予以赔偿。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第六条	权利瑕疵担保责任", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(一)	乙方对于该标的物负有权利瑕疵担保责任，乙方保证：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     1.	标的物上不存在第三人的任何权利，包括但不限于所有权、抵押权、质押权、留", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     置权、优先权、租赁权等权利；不会有任何第三人向甲方提出类似权利主张；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     2.	标的物的生产、销售、使用不会侵害任何第三人的知识产权；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     3.	标的物上无其它负担或处分之存在；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     4.	标的物上不涉及其它债务关系。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(二)	乙方若违反权利瑕疵担保责任，甲方可解除合同，并要求其支付违约金、损害赔偿", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    金。违约金按照总价款的  5 %计算。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第七条	租赁物瑕疵及修缮", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       基于目标物原为乙方自行购买取得所有权，甲方仅依双方签订合同号为：", FontDefault2)); 
	    		phrase.add(new Phrase( Lcode, FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("的融资租赁合同向乙方购买后再出租给乙方使用，乙方同意目标物于本合同 ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("成立后至租赁期间届满前，就本合同成立前、后，如有质量瑕疵或设计制造不良，而有减", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("少一般效用或故障、毁损时，愿自行承担责任及修缮费用。  ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第八条	乙方内部程序", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       乙方出卖的标的物如系乙方全部或主要部分之财产时，已依《公司法》或其公司章程 ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       第__条之规定，取得股东会或董事会或全体股东出售标的物之同意。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第九条	保险", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	若乙方在签订本合同前，已就该标的物办理相关保险的，在签订本合同之日起三日", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   内，乙方应协助甲方办理保险受益人变更手续。就该标的物投保的险种至少包括火险、", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   盗窃险等险种，未投保的险种由乙方补办。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	若乙方在签订本合同时，未就该标的物办理相关保险，在签订本合同之日起三日内，", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   乙方须协助甲方办理相关保险。投保险种同前款，保险费由乙方支付。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十条	标的物的毁损、灭失", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	标的物在交付前或第三条第1款规定的占有保管期间内发生毁损、灭失的风险由乙方", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("承担。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	标的物在前述规定期限内发生毁损或灭失的，乙方应立即通知甲方，甲方有权选择下列", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("任一方式进行处理。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ①乙方将租赁机械恢复原状或修理至完全能正常使用的状态，并承担相关费用。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ②乙方更换与租赁机械同等型号、性能的部件或配件使其能正常使用，并承担相关费用。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ③当租赁机械毁损至无法修理的程度或灭失时，本合同失效，乙方返还甲方支付的全部价", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    款。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十一条	争议解决", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    凡因履行本合同所发生的或与本合同有关的一切争议，甲、乙双方应通过友好协商解决；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("如果协商不能解决，则向甲方所在地有管辖权的法院诉讼解决。甲方为实现本合同项下债", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("权所需费用（包括但不限于催收费用、诉讼费、保全费、公告费、执行费、律师费、差旅费", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("及其它费用）由乙方承担。争议期间，各方仍应继续履行未涉争议的条款。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十二条	其它约定", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    未尽事宜，双方另行协商解决，本合同壹式贰份，甲乙双方各执壹份。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("甲      方：", FontDefault2)); 
	    		phrase.add(new Phrase(Constants.COMPANY_NAME , FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("法定代表人："+Constants.LEGAL_PERSON, FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("委托代理人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("日      期：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("乙      方：", FontDefault2)); 
	    		phrase.add(new Phrase(custname , FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("法定代表人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("委托代理人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("日      期：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 


//				tT.addCell(makeCellWithBorderLeft("      ",
//						PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT,
//						FontDefault2, 1));
//				tT.addCell(makeCellSetColspanNoBorder("法人代表或授权人:",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
//				tT.addCell(makeCellSetColspanNoBorder("（签章）",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
//				tT.addCell(makeCellWithBorderRight("  ", PdfPCell.ALIGN_LEFT,
//						FontDefault));
//				i += 1;
//				tT.addCell(makeCellWithBorderLeft("      ",
//						PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT,
//						FontDefault2, 1));
//				tT.addCell(makeCellSetColspanNoBorder("日期:",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT,
//						FontDefault2, 2));
//				tT.addCell(makeCellWithBorderRight("  ", PdfPCell.ALIGN_LEFT,
//						FontDefault));
//				i += 1;
//				if (i < 53) {
//					for (; i < 49; i++) {
//						tT.addCell(makeCellSetColspan2(" ",
//								PdfPCell.ALIGN_CENTER, FontDefault22, 6));
//					}
//					if (i <= 49) {
//						tT.addCell(makeCellSetColspan3NoTop(" ",
//								PdfPCell.ALIGN_CENTER, FontDefault22, 6));
//					}
//				} else if (i > 53) {
//					int p = Math.round((i - 49) / 52) + 1;
//					for (; i < p * 52 + 49; i++) {
//						tT.addCell(makeCellSetColspan2(" ",
//								PdfPCell.ALIGN_CENTER, FontDefault22, 6));
//					}
//					if (i <= p * 52 + 49) {
//						tT.addCell(makeCellSetColspan3NoTop(" ",
//								PdfPCell.ALIGN_CENTER, FontDefault22, 6));
//					}
//				}
				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "equipmentsforshow.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			// add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出 交货验收证明暨起租通知书", "合同浏览导出 交货验收证明暨起租通知书", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	/*
	 * 导出回租出售资产股东会决议
	 */
	@SuppressWarnings("unchecked")
	public void preLeaseBackMettingResolution(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.getLeaseBackMettingResolutionDateMap(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.getLeaseBackMettingResolutionDateMap(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}
	/*
	 * 导出回租出售资产股东会决议
	 */
	@SuppressWarnings("unchecked")
	public void getLeaseBackMettingResolutionDateMap(Context context) {
		String[] con = null;
		ArrayList creditmapandleasehold = new ArrayList();
		con = (String[]) context.contextMap.get("credtdxx");
		for (int ii = 0; ii < con.length; ii++) {
			HashMap outputMap = new HashMap();
			List leaseholds = new ArrayList();
			context.contextMap.put("credit_id", con[ii]);
			try {

				context.contextMap.put("PRCD_ID",
						context.contextMap.get("credit_id"));
				List contractinfo = (List) DataAccessor.query(
						"exportContractPdf.judgeExitContract",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if (contractinfo.size() == 0) {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				} else {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryEquipmentByRectIdForleaseholds",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				}

				outputMap.put("leaseholds", leaseholds);
				creditmapandleasehold.add(outputMap);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		this.expLeaseBackMettingResolutionPdfs(context, creditmapandleasehold);
	}
	/*
	 * 导出回租出售资产股东会决议
	 */
	@SuppressWarnings("unchecked")
	public void expLeaseBackMettingResolutionPdfs(Context context,
			ArrayList creditmapandleasehold) {

		ByteArrayOutputStream baos = null;
		ArrayList leaseholds = new ArrayList();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();
			for (int ii = 0; ii < creditmapandleasehold.size(); ii++) {
				HashMap outputMap = (HashMap) creditmapandleasehold.get(ii);
				leaseholds = (ArrayList) outputMap.get("leaseholds");
				HashMap baseinfo = (HashMap) leaseholds.get(0);
				String code = "0";
				String leasetopric="";
				if (baseinfo.get("LEASE_TOPRIC") == null) {
					leasetopric = "  ";
				} else {
					leasetopric = baseinfo.get("LEASE_TOPRIC").toString();
				}
				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}
				// 承租方地址
				String custaddress = "";
				if (baseinfo.get("CORP_REGISTE_ADDRESS") == null) {
					custaddress = "  ";
				} else {
					custaddress = baseinfo.get("CORP_REGISTE_ADDRESS")
							.toString();
				}
				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}

				// 租赁物设置场所

				String eAddress = "";
				if (baseinfo.get("EQUPMENT_ADDRESS") == null) {
					eAddress = "  ";
				} else {
					eAddress = baseinfo.get("EQUPMENT_ADDRESS").toString();
				}
				PdfPTable tT = new PdfPTable(10);
				//页眉
//				PdfPTable table = new PdfPTable(10);
//				table.setWidthPercentage(100f);
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,12,0.5f,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
				Phrase phrase = new Phrase();
//				phrase.add(table);
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder(custname, PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("股东会决议", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("公司名称：", FontDefault2)); 
	    		phrase.add(new Phrase(custname, FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));	
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("时间：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("地点：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("主席：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2); 	    		
	    		phrase.add(new Phrase("本次股东会的召集程序、审议程序和表决方式均符合《中华人民共和国公司法》及本公司章", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("程规定和要求。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 	    		
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("根据本公司章程规定，本公司全体股东均出席本次股东会会议，经协商一致，", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("本公司同意将下表机器出售给裕融租赁有限公司", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 

				// 第一个子表开始,第一行
				// 租赁物的数量是动态增加的
				tT.addCell(makeCellSetColspan("名称",
						PdfPCell.ALIGN_LEFT, FontDefault2, 3));
				tT.addCell(makeCellSetColspan("厂牌",
						PdfPCell.ALIGN_LEFT, FontDefault2, 3));
				tT.addCell(makeCellSetColspan("型号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("机号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("数量", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				tT.addCell(makeCellSetColspan("附注", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				i++;
				String thing_kind = "";
				String name = "";
				String brand = "";
				List manufacturerList = new ArrayList();
				for (int k = 0; k < leaseholds.size(); k++) {

					String thingname = "";
					if (((HashMap) leaseholds.get(k)).get("THING_NAME") == null) {
						thingname = "";
					} else {
						thingname = ((HashMap) leaseholds.get(k)).get(
								"THING_NAME").toString();
					}
					String modelspec = "";
					if (((HashMap) leaseholds.get(k)).get("MODEL_SPEC") == null) {
						modelspec = "";
					} else {
						modelspec = ((HashMap) leaseholds.get(k)).get(
								"MODEL_SPEC").toString();
					}

					if (((HashMap) leaseholds.get(k)).get("BRAND") == null) {
						brand = "";
					} else {
						brand = ((HashMap) leaseholds.get(k)).get("BRAND")
								.toString();
					}
					// 厂牌
					if (((HashMap) leaseholds.get(k)).get("TYPE_NAME") == null) {
						thing_kind = "";
					} else {
						thing_kind = ((HashMap) leaseholds.get(k)).get(
								"TYPE_NAME").toString();
					}
					manufacturerList.add(thing_kind);
					if (((HashMap) leaseholds.get(k)).get("NAME") == null) {
						name = "";
					} else {
						name = ((HashMap) leaseholds.get(k)).get("NAME")
								.toString();
					}
					String unit = "";
					if (((HashMap) leaseholds.get(k)).get("UNIT") == null) {
						unit = "";
					} else {
						unit = ((HashMap) leaseholds.get(k)).get("UNIT")
								.toString();
					}
					String amount = "";
					if (((HashMap) leaseholds.get(k)).get("AMOUNT") == null) {
						amount = "";
					} else {
						amount = ((HashMap) leaseholds.get(k)).get("AMOUNT")
								.toString();
					}
					tT.addCell(makeCellSetColspan(
							thingname, PdfPCell.ALIGN_LEFT, FontDefault222,
							3));
					tT.addCell(makeCellSetColspan(
							thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,
							3));
					tT.addCell(makeCellSetColspan(modelspec,
							PdfPCell.ALIGN_LEFT, FontDefault222, 1));
					tT.addCell(makeCellSetColspan(name,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan(amount + unit,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan("  ",
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					i++;
				}

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("特此决议。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("出席股东：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名）________________________（签字）                    （姓名）________________________（签字）", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名）________________________（签字）                    （姓名）________________________（签字）", FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名）________________________（签字）                    （姓名）________________________（签字）", FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("公司盖章：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("本公司担保上述决议内容真实无伪。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	    	

				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "MettingResolution.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			// add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出 股东会决议", "合同浏览导出 股东会决议", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

    /*
	 * Add by Michael 2012-4-12 导出回租融资租赁合同PDF
	 */
	@SuppressWarnings("unchecked")
	public void preLeaseBackPdf(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");
		Map cust = new HashMap();
		String type = null;

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.expLeaseBackPdf(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.expLeaseBackPdf(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}

	/*
	 * Add by Michael 2012-4-12 导出回租融资租赁合同PDF
	 */
	public void expLeaseBackPdf(Context context) {

		ByteArrayOutputStream baos = null;
		String[] con = null;

		Map contract = new HashMap();
		List CREDITNATU = new ArrayList();
		List CROP = new ArrayList();
		Map natu = new HashMap();
		Map crp = new HashMap();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小

			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();

			con = (String[]) context.contextMap.get("credtdxx");

			StringBuffer param = new StringBuffer();
			for (int i = 0; con != null && i < con.length; i++) {
				param.append("'").append(con[i]).append("'");
				if (i != con.length - 1) {
					param.append(",");
				}
			}
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("RECT_ID", param.toString());
			List<String> rectId = (List<String>) DataAccessor.query(
					"rentContract.checkIsAudit", paramMap,
					DataAccessor.RS_TYPE.LIST);

			Map<String, String> checkIsAudit = new HashMap<String, String>();
			for (int i = 0; rectId != null && i < rectId.size(); i++) {
				checkIsAudit.put(rectId.get(i), rectId.get(i));
			}

			for (int ii = 0; ii < con.length; ii++) {
				int t = 0;
				context.contextMap.put("credit_id", con[ii]);

				BusinessLog.addBusinessLog(
						DataUtil.longUtil(con[ii]),
						checkIsAudit.get(con[ii]) == null ? DataUtil
								.longUtil("0") : DataUtil.longUtil(con[ii]),
						"导出 融资租赁合同", "合同浏览导出合同", null,
						context.contextMap.get("s_employeeName") + "("
								+ context.contextMap.get("s_employeeId")
								+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
								.longUtil(context.contextMap
										.get("s_employeeId").toString()),
						DataUtil.longUtil(0),(String)context.contextMap.get("IP"));

				contract = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCCorpByCreditIdUpdateCon",
						context.getContextMap(), DataAccessor.RS_TYPE.MAP);
				if (contract != null) {
					if (contract.size() > 0) {
						;
					} else {
						contract = (Map) DataAccessor.query(
								"creditCustomerCorp.getCreditCCorpByCreditId",
								context.getContextMap(),
								DataAccessor.RS_TYPE.MAP);
					}
				} else {
					contract = (Map) DataAccessor.query(
							"creditCustomerCorp.getCreditCCorpByCreditId",
							context.getContextMap(), DataAccessor.RS_TYPE.MAP);
				}

				if (contract == null) {

					contract = new HashMap();

					contract.put("LEASE_CODE", "  ____ ");
					contract.put("CORP_NAME_CN", " ____  ");
					contract.put("LEGAL_PERSON", " ____  ");
					contract.put("REGISTERED_OFFICE_ADDRESS", " ");
					contract.put("COMMON_OFFICE_ADDRESS", " ");
					contract.put("POSTCODE", " ");
					contract.put("TELEPHONE", " ");
					contract.put("FAX", " ");
					contract.put("CUST_CODE", " ");
					contract.put("CONTRACT_TYPE", "1");

				}

				String code = contract.get("CONTRACT_TYPE") + "";

				float[] widthsPPCa = { 3f };
				PdfPTable tT = new PdfPTable(2);

				tT.setWidthPercentage(100f);
				tT.addCell(makeCellSetColspan3("        ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("融资租赁合同", PdfPCell.ALIGN_CENTER,
						fa, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				t = t + 5;

				String Lcode = contract.get("LEASE_CODE") + "";
				Lcode = Lcode.trim();
				if (Lcode.equals("")) {
					Lcode = "           ";
				}
				tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeft(" ", PdfPCell.ALIGN_LEFT,
						FontDefault));
				tT.addCell(makeCellWithBorderRight("合同编号:    " + Lcode,
						PdfPCell.ALIGN_CENTER, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2ForOne(
						"          合同签订日:    20____年____月____日",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));

				tT.addCell(makeCellSetColspan2ForOne(
						"          合同签订地:    中华人民共和国", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            出租方(甲方):    " + Constants.COMPANY_NAME,
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):    "
						+ contract.get("CORP_NAME_CN") + "",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            法定代表或负责人:    "+Constants.LEGAL_PERSON, PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("法定代表或负责人:    "
						+ contract.get("LEGAL_PERSON") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            注册地址:    苏州工业园区东富路8号",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"注册地址:    " + contract.get("REGISTERED_OFFICE_ADDRESS")
								+ " ", PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            实际经营地:    ", PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("实际经营地:    ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            "+Constants.COMPANY_COMMON_ADDRESS, PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"" + contract.get("COMMON_OFFICE_ADDRESS") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            邮政编码:    215022 ", PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"邮政编码:    " + contract.get("POSTCODE") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            电话号码:    0512-80983566 ",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"电话号码:    " + contract.get("TELEPHONE") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            传真号码:    0512-80983567 ",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"传真号码:    " + contract.get("FAX") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				t = t + 26;

				Paragraph mm11 = new Paragraph();

				mm11.setFont(FontDefault);

				mm11.add("                      本合同的租赁是指中国合同法规定的融资租赁形式。出租方 ");
				Chunk c361 = new Chunk(Constants.COMPANY_NAME, FontUnder);
				mm11.add(c361);

				PdfPCell objCell = new PdfPCell(mm11);

				objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				objCell.setColspan(2);
				objCell.setPaddingLeft(35);
				objCell.setBorderWidthBottom(0);
				objCell.setBorderWidthTop(0);

				tT.addCell(objCell);

				Paragraph mm12 = new Paragraph();

				mm12.setFont(FontDefault);

				mm12.add("           (以下简称“甲方”)和承租方");
				String neme1 = contract.get("CORP_NAME_CN") + "";

				neme1 = neme1.trim();
				if (neme1.equals("")) {

					neme1 = "____________________________________";
					mm12.add(neme1);

				} else {

					int le = neme1.length();
					String px = "                         ";

					if (le < 19) {

						String pp = px.substring(0, Math.round(21 - le));

						neme1 = pp + pp + neme1 + pp + pp;
					}

					Chunk c461 = new Chunk(neme1, FontUnder);
					mm12.add(c461);
				}

				mm12.add("(以下简称“乙");

				PdfPCell Cell2 = new PdfPCell(mm12);

				Cell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				Cell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				Cell2.setColspan(2);
				Cell2.setPaddingLeft(35);
				Cell2.setBorderWidthBottom(0);
				Cell2.setBorderWidthTop(0);

				tT.addCell(Cell2);

				tT.addCell(makeCellSetColspan2ForOne(
						"            方”)双方就甲方出租本合同规定的合同正本及合同附件中记载的设备(以下简称租赁物)，乙",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne(
						"            方从甲方处承租租赁物事宜，在平等互惠的基础上经友好协商达成以下协议并签订本合同(本",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne(
						"            合同分为合同正本与合同附件，合同附件经甲、乙双方及卖方签订确认后与合同正本有同等",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne("            的法律效力)。",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellWithBorderLeftForOne("          出租方(甲方): ",
						PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):  ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2(
						"                         "
								+ Constants.COMPANY_NAME
								+ "                                                                "
								+ contract.get("CORP_NAME_CN") + "",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2("", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne(
						"          法定代表人或授权人:  ", PdfPCell.ALIGN_LEFT,
						FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("法定代表人或授权人:  ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne("          日期: ",
						PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("日期: ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				// 后来修改
				t = t + 23;
				for (; t < 59; t++) {
					tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
							FontDefault, 2));

				}
				tT.addCell(makeCellSetColspan2("1", PdfPCell.ALIGN_CENTER,
						FontDefault, 2));
				t += 1;
				if (t == 60) {
					tT.addCell(makeCellSetColspan4("全国服务专线：400-928-1999",PdfPCell.ALIGN_RIGHT,FontDefault, 2));
				}
				// 以上是后来修改的
				document.add(tT);
				document.add(Chunk.NEXTPAGE);

				// 循环 连带保证人:
				// 身份证号码:
				// 身份证地址:
				// 签约日期:

				List pageList = new ArrayList<Map>();
				Map pageMap = new HashMap();
				CREDITNATU = (List) DataAccessor.query(
						"creditVoucher.selectAND", context.getContextMap(),
						DataAccessor.RS_TYPE.LIST);
				CROP = (List) DataAccessor.query("creditVoucher.selectVND",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);

				int cr = CROP.size();
				int na = CREDITNATU.size();

				// 整合担保人信息到List

				for (int n = 0; n < na; n++) {
					natu = (Map) CREDITNATU.get(n);
					natu.put("CUSTYPE", "NA");

					pageList.add(natu);
				}

				for (int m = 0; m < cr; m++) {
					crp = (Map) CROP.get(m);
					crp.put("CUSTYPE", "CR");

					pageList.add(crp);

				}

				int listSize = pageList.size();
				int pageN = ((Number) Math.floor(listSize / 5)).intValue(); // 页数

				int pageL = listSize % 5; // 余数

				int p = 0; // 页数标记
				int m = 0; // 数据标记

				context.contextMap.put("dataType", "证件类型");
				List natuTypeList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				String flag = "";
				// 多页
				if (pageN > 0) {

					// 多页循环开始
					for (; p < pageN; p++) {

						PdfPTable tT20 = new PdfPTable(10);
						tT20.setWidthPercentage(100f);
						tT20.addCell(makeCellSetColspan3("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("      ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						for (int n = 0; n < 5; n++) {

							pageMap.clear();

							m = 5 * p + n;

							pageMap = (Map) pageList.get(m);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {

								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								tT20.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));

								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT20.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT20.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}

						document.add(Chunk.NEWLINE);

						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2((p + 2) + "",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						tT20.addCell(makeCellWithBorderLeft("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT20.addCell(makeCell3("对保人:", PdfPCell.ALIGN_CENTER,
								FontDefault));
						
						tT20.addCell(makeCellSetColspan("     ",
								PdfPCell.ALIGN_CENTER, FontDefaultP, 2));
						tT20.addCell(makeCellWithBorderRight("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT20.addCell(makeCellSetColspan4("    ",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						document.add(tT20);

						// 另一页
						document.add(Chunk.NEXTPAGE);

					} // 多页循环结束

					// 尾页
					if (pageL > 0) {
						PdfPTable tT19 = new PdfPTable(10);
						tT19.setWidthPercentage(100f);
						tT19.addCell(makeCellSetColspan3("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("      ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						for (int n = (m + 1); n < listSize; n++) {

							pageMap.clear();
							pageMap = (Map) pageList.get(n);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {

								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}
						// 最后页 空字段补齐5个

						if (pageL > 0) {

							for (; pageL < 5; pageL++) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证号码:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

							}
						}

						document.add(Chunk.NEWLINE);

						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						tT19.addCell(makeCellSetColspan2((pageN + 2) + "",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						tT19.addCell(makeCellWithBorderLeft("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT19.addCell(makeCell3("对保人:", PdfPCell.ALIGN_CENTER,
								FontDefault));
						tT19.addCell(makeCellSetColspan("     ",
								PdfPCell.ALIGN_CENTER, FontDefaultP, 2));
						tT19.addCell(makeCellWithBorderRight("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT19.addCell(makeCellSetColspan4("    ",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));
						
						document.add(tT19);

						// 另一页
						document.add(Chunk.NEXTPAGE);

					}// 尾页结束

					// 多页 结束
				}

				// 单页
				else {

					PdfPTable tT19 = new PdfPTable(10);
					tT19.setWidthPercentage(100f);
					tT19.addCell(makeCellSetColspan3("     ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));
					tT19.addCell(makeCellSetColspan2("      ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));
					tT19.addCell(makeCellSetColspan2("     ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));

					// 有数据
					if (pageL > 0) {

						for (int n = 0; n < listSize; n++) {

							pageMap.clear();
							pageMap = (Map) pageList.get(n);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {
								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}
						// 最后页 空字段补齐4个

						for (; pageL < 5; pageL++) {

							tT19.addCell(makeCellSetColspan2(
									"                连带保证人:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证号码:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证地址:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                签约日期: ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

						}
					}

					// 无数据
					if (pageL == 0 & pageN == 0) {

						// 空字段补齐4个

						for (; pageL < 5; pageL++) {

							tT19.addCell(makeCellSetColspan2(
									"                连带保证人:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证号码:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证地址:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                签约日期: ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

						}

					}

					document.add(Chunk.NEWLINE);

	        		for (int i = 1; i <=4; i++) {
	            		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));					
					}
	        		
	        		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));

	            		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            	 
	            		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
	            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		
	        		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCell3( "日期:" ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_RIGHT, FontDefault,10));	        		
	 		    
	        		document.add(tT19);

					// 另一页
					document.add(Chunk.NEXTPAGE);

				}

				if (pageL == 0 & pageN > 0) {

					pageN = pageN - 1;
				}

				// 合同条款
				PdfPTable tT30 = new PdfPTable(2);
				tT30.setWidthPercentage(100f);
				tT30.addCell(makeCellSetColspan3("", PdfPCell.ALIGN_CENTER,
						FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT30.addCell(makeCellSetColspan2222("    第一条 租赁物",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买买卖合同附表",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         所记载的租赁物租予乙方，乙方则同意向甲方承租并使用该对象。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、租赁物包括：全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             2、乙方应对其上述要求及选定承担全部责任，甲方对该选定不承担任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第二条 租赁物的所有权",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、在本合同租赁期间内，租赁物的所有权归甲方所有。甲方对租赁物得标明出租人名称及有关所有权、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"               租赁关系的标章、识别，乙方应以维护，不得将其移动、丢弃或损毁。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             2、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所，不得转让给第三人或允许他人使用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             3、乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施向第三方销售、转让、转租租赁物、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"               不得向他人设置质押、抵押等担保。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("   第三条 租赁物的交付、验收",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、租赁物由出卖人直接将租赁物交付承租人或依中华人民共和国物权法第二十七条规定交付予",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              承租人，乙方应在合同附表载明的验收期限内自行对租赁物实施验收，并应在三天内向甲方提交",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              《租赁物验收证明书》，否则视为甲方已验收完毕，并以租赁物交付日视为本合同起租日 。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"              在租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，乙方应该在三天内以书面",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              形式通知甲方；并且，乙方应直接与卖方协商解决前述纠纷，并在与卖方解决该纠纷后，及时向",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              甲方提交《租赁物验收证明书》，乙方不得拖延时间（自收到租赁物之日起不超过15天，如遇特",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              殊情况应及时通知甲方）。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             2、乙方拒收租赁物的，乙方应当赔偿由此给甲方带来的一切损失。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));		
				tT30.addCell(makeCellSetColspan2222(
						"             3、乙方提交《租赁物验收证明书》后，即为认可租赁物已在符合要求的状态下由甲方交付完毕，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              此后不得再提出任何异议。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             4、乙方在向甲方提交《租赁物验收证明书》之日起，即可按照本合同的规定使用该租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             5、甲方交付租赁物后，乙方应自行负责将租赁物安装至合同附件一载明的场所。根据甲方的委托",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              和确认，乙方与运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              他所需费用全部由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第四条  租赁期间",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"             1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             2、承租方在本合同有效期内不得自行解除本合同。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             3、乙方于租赁期间届满前欲续租租赁物时，可于租赁期间届满两个月前通知甲方，由双方就续",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              租事宜另行协商解决。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第五条	租赁物的瑕疵",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              的内容不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据买卖合同的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"              规定，由买卖合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              卖方的原因以及其他不属于出租方的故意或重大过失引起而发生的事由，造成租赁物交付延迟",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              或者不能交付时，甲方不承担任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              方的任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				
				tT30.addCell(makeCellSetColspan2222(
						"             3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              以便于乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              卖方之间的各种直接交涉，甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              师费等）和法律后果均由乙方承担并享受其利益。因卖方违反买卖合同而造成的一切损失由乙方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"              承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				
				tT30.addCell(makeCellSetColspan2222("     第六条	租金",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              乙方应当按照附表第（7）项中规定的数额及支付条件向甲方支付租金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第七条	租赁物的保管及使用",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方，并应将租赁物放置于合同附表",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              内规定的地点使用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));								
				tT30.addCell(makeCellSetColspan2222(
						"             2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，对安全",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              检查中发现的各种灾害事故隐患，在接到安全主管部门或甲方提出的整改通知书后，认真付诸",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              实施。若租赁物受到损害的，乙方应当积极采取抢救措施，使损失减少至最低程度，同时保护",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222((pageN + 3) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan3(
						"                                     现场，并立即通知甲方，协助勘查。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"             3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，收回或请求",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              返还租赁物及请求损害赔偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             4、由于租赁物自身或其设置、保管、使用的原因，而对第三人造成人身伤害或者财产损害的，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              甲方不承担任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			

				tT30.addCell(makeCellSetColspan2222("     第八条	租赁物的保养及费用",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              相应的维护和修理。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));								
				tT30.addCell(makeCellSetColspan2222(
						"             2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              定期或者不定期的检查和进行其他一切维护、修护，并承担一切费用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));													
				tT30.addCell(makeCellSetColspan2222(
						"             3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             4、乙方应按照税法规定的税率承担因租金而产生的营业税，并与每次应当支付的租金一起付给",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              甲方。根据本合同向甲方支付的费用须缴纳增值税时，乙方应按照甲方的结算请求进行支付。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              （租赁期间如遇国家税收政策发生重大变化，所产生的税负增加，仍由乙方担负。）",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              不负担任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				

				tT30.addCell(makeCellSetColspan2222("     第九条	租赁物的灭失、损毁",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、保全措施、乙方的原因或其他任",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              通常的损耗、减耗不适用本项。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                A. 将租赁物复原或修理至完好状态；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                B. 用与租赁物相同、性能相似的对象替换租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             3、租赁物灭失（包括不能修理或者侵害所有权）的情况，乙方应根据未付的租金金额向甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              支付赔偿金，保证金由甲方没入。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"              付赔偿金额，同时，本合同自动终止。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"             5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              甲方要将租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              资力等不承担任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222("     第十条	租赁物所有权变更的情形",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              乙方在本合同规定的租赁期届满时，依本合同第十七条规定选择买取租赁物或提前终止合同",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			
				tT30.addCell(makeCellSetColspan2222(
						"              返还租赁物时，应在租赁期届满2个月前以书面形式通知甲方。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222("     第十一条 租赁物的状态改变",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              乙方没有得到甲方的书面承诺，不得将租赁物附着在其他对象上，或改造其外观、性能、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			
				tT30.addCell(makeCellSetColspan2222(
						"              机能、品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              产生的价值无偿归属于租赁物的所有人即甲方，但由此产生的损害由乙方负无条件赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222("     第十二条 租赁物的检查",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			
				tT30.addCell(makeCellSetColspan2222(
						"              状况及维护情况。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			
				
				tT30.addCell(makeCellSetColspan2222("     第十三条 保险",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              甲方从起租日起向保险公司投保相应险种，保险人由甲方指定，保险公司以甲方为受益人，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));			
				tT30.addCell(makeCellSetColspan2222(
						"              保险费包含在租金中。在租赁期间，如乙方未按时支付租金，造成甲方不能按时对租赁物",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              进行投保和续保而造成的损失，乙方应承担赔偿责任;乙方于首期租金交付甲方前、投保前,",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              此段期间内之租赁目标物危险责任仍由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第十四条 保险金的收取",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));		
				tT30.addCell(makeCellSetColspan2222(
						"             1、发生事故时，乙方应立即通知甲方，并将领受保险金所需的一切文件交付给甲方。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             2、租赁物发生保险事故后获得赔偿时，由甲方领受保险金。甲方领取保险金后应支付由此发生",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              的损失。如保险金不足以支付甲方损失的，乙方应当予以赔偿。如由于乙方的故意或重大过失",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              造成保险公司不予理赔时，乙方应承担该事故的全部损害赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222("     第十五条 租赁保证金",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的同时向",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              甲方预先支付附表规定的保证金额。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
//				tT30.addCell(makeCellSetColspan2222(
//						"             2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，乙方",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
//				tT30.addCell(makeCellSetColspan2222(
//						"              不得凭保证金免除其超出保证金部分的支付义务。",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	

				tT30.addCell(makeCellSetColspan2222(
						"             2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务,包括但不限",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              于已到期平均抵冲的租金及解除合同后至清偿日止依未平均抵冲前的日租金两倍的使用费、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222((pageN + 4) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan3(
						"                                    滞纳金、诉讼费、律师费及处理债权的相关费用,抵消后剩馀的保证金作为乙方因违约所应支付",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"              给甲方的违约金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              3、发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              的裁量优先从该保证金帐户支付。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"             4、甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第十六条 违约责任",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、乙方发生下列各项情形之一时，甲方无需催告通知即可解除本合同。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                A.发生一次或一次以上迟延支付租金时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                B. 乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产、解散清算",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      或被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      扣押时；乙方被卷入诉讼、仲裁或其他法律程序，可能给乙方的经营活动带来显著",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      不利影响时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                C.	乙方迁移住所前未通知甲方时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                D.	乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                E.	乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                F.	乙方经营状况显著恶化，或有足够理由相信有此可能时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"                G.	本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                H.	违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      但未在该期限内做出回应时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                I.	发生与上述各项相当的其他事由时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                J.连带保证人有上述各项情形之一时。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"             2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      金。按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      方承担。甲方收回租赁物时，租赁物的价值由双方确定或者由评估机构评估后确定；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      确定的价格不足以支付甲方损失的，乙方应当予以补偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                B．乙方有义务支付已到期未支付及全部未到期的租金及由租赁物产生的其他一切费用，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      并对甲方承担相应的损害赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"                C．承担因解除合同所产生的诉讼费、律师费及处理债权的相关费用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                D．甲方得申请法院委托具有相应资质的机构对租赁物进行评估、拍卖,拍卖不成的,依甲方所",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      选定的评估机构评估确定的价格为准,确定的价格不足以支付本款的B款金额时,乙方应当予以补偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				
				
				tT30.addCell(makeCellSetColspan2222(
						"             3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              以下情况，乙方应承担以下责任：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、取消）",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      或者在租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      合同（包括撤回要约）。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      承担相应的违约金（计算标准：购买方实际支付日起至实际收到乙方返还全部支",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				//Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
//				tT30.addCell(makeCellSetColspan2222(
//						"                      付款项日，以日息万分之五计 算）。同时乙方应立即代替甲方（购买方）与卖方",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                      付款项日，以每万元每日6元计算）。同时乙方应立即代替甲方（购买方）与卖方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                      进行协商、妥善处理。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             4、延迟支付而产生的违约金：乙方怠于向甲方支付本合同租金及相关费用时，或者甲方为乙方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				//Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
//				tT30.addCell(makeCellSetColspan2222(
//						"              垫付费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              垫付费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应按照应付的金额以每万元每日6元",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              计算，向甲方支付违约金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第十七条 租赁物的违约返还",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                的选择是否购买租赁物的权利。购买金额以附表(8)中载明的购买选择权为准。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"                当时的状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                费用和税款均由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             3、本合同因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了租赁物通常损耗或甲",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予以返还。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              运送租赁物所需的必要费用由乙方负担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              完毕前按照迟延天数支付相应地损害赔偿金，计算方法如下：每天应当支付相当于双倍的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				
				tT30.addCell(makeCellSetColspan2222((pageN + 5) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan3(
						"                                    日租费作为损害赔偿金。同时遵守本合同的其他约定。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222("     第十八条 连带保证人",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"             1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"             2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222(
						"                的滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                保全费、申请执行费、律师费、公告费、 评估费、拍卖费等。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             3、连带保证人保证的期间同乙方所负全部债务履行期限。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              已形成的债务向甲方主张免责或要求损害赔偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              仍负全部责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"              得解除保证责任，以其他方式声明退保，均不生效。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第十九条 甲方权利的转让",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第二十条 乙方提供必要的情况和资料",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"          乙方资产负债表、乙方利润表及其他的明细情况。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第二十一条 争议的解决",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				/*以下zhangbo0723*/
				String decpId =contract.get("DECP_ID")==null?"":String.valueOf(contract.get("DECP_ID"));
				
					 if(decpId.equals("3") || decpId.equals("8")){
							tT30.addCell(makeCellSetColspan2222("      东莞分公司注册所在地的人民法院提起诉讼。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
						}else{	
							tT30.addCell(makeCellSetColspan2222("          注册所在地的人民法院提起诉讼。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
						}
				/*以上zhangbo0723*/
				
				tT30.addCell(makeCellSetColspan2222("     第二十二条 合同及附件",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             合同附件与本合同具有同等法律效力。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"             本合同一式两份，双方各执一份。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				for (int j=0;j<=28;j++){
					tT30.addCell(makeCellSetColspan2222(
							"                ",
							PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				}
	
				tT30.addCell(makeCellSetColspan2222((pageN + 6) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));

				document.add(tT30);

			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = contract.get("CUST_CODE").toString() + ".pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();

			if ((context.getContextMap().get("creditidflagi") + "").equals(""
					+ context.getContextMap().get("creditidflagl"))) {

				closeStream(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}


	/*
	 * 导出回租承诺书
	 */
	@SuppressWarnings("unchecked")
	public void preLeaseBackPromiseBook(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.getLeaseBackPromiseBookDateMap(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.getLeaseBackPromiseBookDateMap(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}
	
	/*
	 * 导出回租承诺书
	 */
	@SuppressWarnings("unchecked")
	public void getLeaseBackPromiseBookDateMap(Context context) {
		String[] con = null;
		ArrayList creditmapandleasehold = new ArrayList();
		con = (String[]) context.contextMap.get("credtdxx");
		for (int ii = 0; ii < con.length; ii++) {
			HashMap outputMap = new HashMap();
			List leaseholds = new ArrayList();
			context.contextMap.put("credit_id", con[ii]);
			try {

				context.contextMap.put("PRCD_ID",
						context.contextMap.get("credit_id"));
				List contractinfo = (List) DataAccessor.query(
						"exportContractPdf.judgeExitContract",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if (contractinfo.size() == 0) {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				} else {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryEquipmentByRectIdForleaseholds",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				}

				outputMap.put("leaseholds", leaseholds);
				creditmapandleasehold.add(outputMap);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		this.expLeaseBackPromiseBookPdfs(context, creditmapandleasehold);
	}
	/*
	 * 导出回租承诺书
	 */
	@SuppressWarnings("unchecked")
	public void expLeaseBackPromiseBookPdfs(Context context,
			ArrayList creditmapandleasehold) {

		ByteArrayOutputStream baos = null;
		ArrayList leaseholds = new ArrayList();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();
			for (int ii = 0; ii < creditmapandleasehold.size(); ii++) {
				HashMap outputMap = (HashMap) creditmapandleasehold.get(ii);
				leaseholds = (ArrayList) outputMap.get("leaseholds");
				HashMap baseinfo = (HashMap) leaseholds.get(0);

				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}

				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}
				
				PdfPTable tT = new PdfPTable(10);
				//页眉
//				PdfPTable table = new PdfPTable(10);
//				table.setWidthPercentage(100f);
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,12,0.5f,0,0,0));
//				table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
				Phrase phrase = new Phrase();
//				phrase.add(table);
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("承诺书", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("致："+Constants.COMPANY_NAME, FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     本司与贵公司签订了合同号为", FontDefault2)); 
	    		phrase.add(new Phrase(Lcode, FontUnde2)); 
	    		phrase.add(new Phrase("的买卖合同和合同号为 ", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase(Lcode, FontUnde2)); 
	    		phrase.add(new Phrase("的融资租赁合同，作为与贵公司往来设备售后租回业务", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（设备明细如上述买卖合同第一条规定），本司在此承诺在和贵公司签订售", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("后租回相关协议后：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1、	承诺该设备之所有权系贵公司所有。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2、	承诺决不将上述设备提供予第三人设定动产抵押权、质权等其", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("      他影响贵公司取得完全所有权的权利、利益。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("3、	承诺决不将上述设备为处分、变更等足以损害贵公司权益的行为。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("4、	承诺贵公司取得上述设备的所有权，绝无限制到贵公司所享有", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("      所有权权益和任何所有权的权利争议。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;	    		
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("如本司有违反上述承诺，愿承担对贵公司的损害赔偿和相应的法律责任。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;
	    		
	    		for(int j=0;j<=2;j++){
		    		phrase = new Phrase("", FontDefault2);  
		    		phrase.add(new Phrase("", FontDefault2)); 
					tT.addCell(makeCellSetColspanNoBorder(phrase,
							PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
		    		i++; 
	    		}

	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("承 诺 人："+custname+"（承租方盖章）", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 3));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 6));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;	
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("签署日期：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 3));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 6));
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 1));
	    		i++;

				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "PromiseBook.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			// add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出 承诺书", "合同浏览导出 承诺书", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

    //Add by Michael 2012 4-12导出回租付款指示书
    public void exportCarPayMoneyNOT(Context context){
		try{
			Map rentandpuctcontract = new HashMap();
			//is=0 没有生成合同 否则已经生成合同
			
			int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(is==0){
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT2", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}else{
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT1", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}
			String bankAccount=(String)context.contextMap.get("bankAccount"+context.contextMap.get("PRCD_ID"));
			rentandpuctcontract.put("bankAccount", bankAccount);
			
		    this.expLeaseBackPayMoneyPdf(context,rentandpuctcontract);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
    
    //Add by Michael 2012 4-12导出回租付款指示书    
    @SuppressWarnings("unchecked")
    public void expLeaseBackPayMoneyPdf(Context context,Map rentandpuctcontract) {
	
	ByteArrayOutputStream baos = null;
 	//Map rentandpuctcontract = new HashMap();
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        	        
	        // 打开文档
	        document.open();

		    String code="";
		    String[] rentdate =new String[3];
		    rentdate[0]="";
    		rentdate[1]="";
    		rentdate[2]="";
		  //租赁合同编号
	        String rentcode = "";
	      //购销合同编号
    		String puctcode = "";
    		//承租方名称
     		String custname="";
     		//供应商
     		String sellername="";
     		//供应商
     		String money = "";
     		String bankaccount="";
     		String openbank="";
		    if(rentandpuctcontract!=null){
		    	if(rentandpuctcontract.get("CONTRACT_TYPE")!=null){
		    		code=rentandpuctcontract.get("CONTRACT_TYPE").toString();
		    	}
		    	if(rentandpuctcontract.get("LESSOR_TIME")!=null){
		    		String[] temp = rentandpuctcontract.get("LESSOR_TIME").toString().substring(0,10).split("-");
		    		if(!temp[0].equals("1900")){
		    			rentdate = temp ;
		    		}
		    	}
		   
		    	String [] bankInfo=((String)rentandpuctcontract.get("bankAccount")).split("=");
		    	
		        
	    		if(rentandpuctcontract.get("LEASE_CODE")==null){
	    			rentcode = "  ";
		    	}else{
		    		rentcode = rentandpuctcontract.get("LEASE_CODE").toString();
		    	}
	    		
	     		if(rentandpuctcontract.get("PUCT_CODE")==null){
	     			puctcode = "  ";
	 	    	}else{
	 	    		puctcode = rentandpuctcontract.get("PUCT_CODE").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("CUST_NAME")==null){
	     			custname = "  ";
	 	    	}else{
	 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
	 	    	}
	     		
	    		if(rentandpuctcontract.get("CUST_NAME")==null){
	     			custname = "  ";
	 	    	}else{
	 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("NAME")==null){
	     			sellername = "  ";
	 	    	}else{
	 	    		sellername=bankInfo[0].toString();
	 	    	}
	     		if(rentandpuctcontract.get("MONEY")==null){
	     			money = "  ";
	     		}else{
	     			System.out.println(bankInfo[3]);
	     			money=nfFSNum.format(DataUtil.doubleUtil(bankInfo[3]));
	     		}
	     		if(rentandpuctcontract.get("BANK_ACCOUNT")!=null){
	     			bankaccount = bankInfo[1].toString();;
	     		}
	     		if(rentandpuctcontract.get("OPEN_ACCOUNT_BANK")!=null){
	     			openbank = bankInfo[2].toString();;
	     		}
		    }
			PdfPTable tT = new PdfPTable(new float[]{10f,20f,20f,20f,20f,20f,10f});
			tT.setWidthPercentage(100f);

			
			int i=0;

				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
				i++;
				tT.addCell(makeCellSetColspan2("关于设备价款的支付指示",PdfPCell.ALIGN_CENTER, fa,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("致："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日 贵公司与我们签署的融资租赁合同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("[合同号："+rentcode+"  ] 和买卖合同  [合同号："+rentcode+"  ]，请贵公司代替", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("我方将货款分别支付至以下帐户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("户名："+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("金额：   " + money+"元" , PdfPCell.ALIGN_LEFT, FontDefault2,5));
//				tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;

	    	
	    	
	    	
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("共计支付:" +  money+"元" , PdfPCell.ALIGN_LEFT, FontDefault2,5));
//				tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("承租方： "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		for(;i<51;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}
	    		if(i<=51){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}				
			
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
	     //  }
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "AboutEquipMoneyPay.pdf";
	    context.response.setContentType("application/pdf");
	    context.response.setCharacterEncoding("UTF-8");
	    context.response.setHeader("Pragma", "public");
	    context.response.setHeader("Cache-Control",
		    "must-revalidate, post-check=0, pre-check=0");
	    context.response.setDateHeader("Expires", 0);
	    context.response.setHeader("Content-Disposition",
		    "attachment; filename=" + strFileName);

	    ServletOutputStream o = context.response.getOutputStream();

	    baos.writeTo(o);
	    o.flush();
		closeStream(o);


	    
	} catch (Exception e) {
	    e.printStackTrace();
	    LogPrint.getLogStackTrace(e, logger);
	}
	
	
    }
    
    /**
     * 导出回租起租通知书
     * @param context
     */
	 public void preLeaseBackLeaseHoldPdf(Context context){
			String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
			
			 if(con != null ){
			   if(!(con[0].equals("00"))){
				 try {
		        		 if(con.length >1){
		        			 context.contextMap.put("credtdxx",  con);
		        			 this.getLeaseBackLeaseHoldDateMap(context);
		        		 }else{
		                	if(con.length ==1){
		                		 context.contextMap.put("credtdxx",   con);
		                		 this.getLeaseBackLeaseHoldDateMap(context);
		                	} 
		        		}
				} catch (Exception e) {
					    e.printStackTrace();
					    LogPrint.getLogStackTrace(e, logger);
				}
			  }
			}	
		  }
	 
    /**
     * 导出回租起租通知书
     * @param context
     */
	 @SuppressWarnings("unchecked")
	public void  getLeaseBackLeaseHoldDateMap(Context context){
		 String[]  con = null;
		 ArrayList creditmapandleasehold=new ArrayList();
		  con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< con.length;ii++){	
	        	HashMap outputMap= new HashMap();
	        	List leaseholds =new ArrayList();
	        	context.contextMap.put("credit_id",  con[ii]);
		       try{
		    	   context.contextMap.put("PRCD_ID",  context.contextMap.get("credit_id"));
		    	   List contractinfo=(List) DataAccessor.query("exportContractPdf.judgeExitContract", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
		    	   if(contractinfo.size()==0){
		    		   leaseholds = (List) DataAccessor.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
		    	   }else{
		    		   leaseholds = (List) DataAccessor.query("exportContractPdf.queryEquipmentByRectIdForleaseholds", context.getContextMap(), DataAccessor.RS_TYPE.LIST);   
		    	   }
		    	   outputMap.put("leaseholds", leaseholds);
		    	   creditmapandleasehold.add(outputMap);
		    	 
		       }catch(Exception e){
		    	   e.printStackTrace();
		    	   LogPrint.getLogStackTrace(e, logger);
		       }
		    }
	        this.expLeaseBackLeaseHoldPdfs(context,creditmapandleasehold);
	 }
   /**
    * 导出回租起租通知书
    * @param context
    */
   public void expLeaseBackLeaseHoldPdfs(Context context,ArrayList creditmapandleasehold) {
	
	ByteArrayOutputStream baos = null;
	//String[]  con = null;
	ArrayList leaseholds =new ArrayList();	
 	
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
	        Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        	        
	        // 打开文档
	        document.open();
	        for(int ii=0; ii< creditmapandleasehold.size();ii++){
		    	
	        	HashMap outputMap = (HashMap)creditmapandleasehold.get(ii); 
	        	leaseholds = (ArrayList)outputMap.get("leaseholds");
	        	HashMap baseinfo = (HashMap)leaseholds.get(0);
	        String code="0";
	        //承租方名称
	        String custname = "";
    		if(baseinfo.get("CUST_NAME")==null){
    			custname = "  ";
	    	}else{
	    		custname = baseinfo.get("CUST_NAME").toString();
	    	}
    		//承租方地址
    		String custaddress = "";
     		if(baseinfo.get("CORP_REGISTE_ADDRESS")==null){
     			custaddress = "  ";
 	    	}else{
 	    		custaddress = baseinfo.get("CORP_REGISTE_ADDRESS").toString();
 	    	}
    		String Lcode = "";
    		if(baseinfo.get("LEASE_CODE")==null){
    			Lcode = "无编号";
	    	}else{
	    		Lcode = baseinfo.get("LEASE_CODE").toString();
	    	}
    		
    		//租赁物设置场所
    		
    		String eAddress = "";
    		if(baseinfo.get("EQUPMENT_ADDRESS")==null){
    			eAddress = "  ";
	    	}else{
	    		eAddress = baseinfo.get("EQUPMENT_ADDRESS").toString();
	    	}
    		float[] widthsStl = {0.2f,0.3f,0.3f,0.3f,0.3f,0.3f,0.2f};
			PdfPTable tT = new PdfPTable(widthsStl);
			tT.setWidthPercentage(100f);
			int i=0;
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
				i++;
				//Modify by Michael 2012 07-06 For 签呈修改合同版本
				//tT.addCell(makeCellSetColspan2("租赁物验收证明暨起租通知书",PdfPCell.ALIGN_CENTER, fa,7));
				tT.addCell(makeCellSetColspan2("起租通知书",PdfPCell.ALIGN_CENTER, fa,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault22,2));	    		
	    		tT.addCell(makeCellSetColspanNoBorder("合同编号："+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		
	    		//文字部分
	    		String[] createdate = new String[3];
	    		if(((HashMap)(leaseholds.get(0))).get("CREATE_DATE")!=null){
	    			createdate = ((HashMap)(leaseholds.get(0))).get("CREATE_DATE").toString().substring(0, 10).split("-");
	    			if(createdate[0].equals("1900")){
	    				createdate[0] ="        ";
		    			createdate[1] ="        ";
		    			createdate[2] ="        ";
	    			}
	    		}else{
	    			createdate[0] ="        ";
	    			createdate[1] ="        ";
	    			createdate[2] ="        ";
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		Phrase phrase = new Phrase("根据我方（承租方）与"+Constants.COMPANY_NAME+"（出租方）之间于", FontDefault2); 

	    		phrase.add(new Phrase("____年", FontDefault2)); 
	    		phrase.add(new Phrase("____月____日", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//为编号加入下划线
	    		phrase = new Phrase("", FontDefault2); 
	    		phrase.add(new Phrase("签订的编号为", FontDefault2)); 
	    		phrase.add(new Phrase(Lcode+"", FontUnde2)); 
	    		phrase.add(new Phrase("的租赁合同，下列租赁物已确实收到，并在", FontDefault2)); 
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//				tT.addCell(makeCellSetColspanNoBorder(createdate[2]+"日签订的编号为"+Lcode+"的租赁合同，下列租赁物已确实收到，並在", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		String startdate="";
	    		if(baseinfo.get("START_DATE")==null){
	    			startdate="";
	    		}else{
	    			startdate=baseinfo.get("START_DATE").toString().substring(0,10);
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于 ____年 ____月 ____日正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		
	    		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
				//第一个子表开始,第一行
	    		//租赁物的数量是动态增加的
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3("租赁物：", PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("名称", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("厂牌", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("型号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("机号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan3("单位及数量", PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		String thing_kind = "";
	    		String name = "";
	    		String brand="";
	    		List manufacturerList = new ArrayList() ;
	    		for(int k=0;k<leaseholds.size();k++){
	    		
	    			String thingname="";
	    			if(((HashMap)leaseholds.get(k)).get("THING_NAME")==null){
	    				thingname="";
	    			}else{
	    				thingname=((HashMap)leaseholds.get(k)).get("THING_NAME").toString();
	    			}
	    			String modelspec="";
	    			if(((HashMap)leaseholds.get(k)).get("MODEL_SPEC")==null){
	    				modelspec="";
	    			}else{
	    				modelspec=((HashMap)leaseholds.get(k)).get("MODEL_SPEC").toString();
	    			}
	    			
	    			if(((HashMap)leaseholds.get(k)).get("BRAND")==null){
	    				brand="";
	    			}else{
	    				brand=((HashMap)leaseholds.get(k)).get("BRAND").toString();
	    			}
	    			//改为制造商以前是厂牌
	    			if(((HashMap)leaseholds.get(k)).get("TYPE_NAME")==null){
	    				thing_kind="";
	    			}else{
	    				//Add by Michael 2012 07-10 去除重复的制造商
	    				if (!thing_kind.equals(((HashMap)leaseholds.get(k)).get("TYPE_NAME").toString())){
	    					manufacturerList.add(((HashMap)leaseholds.get(k)).get("TYPE_NAME").toString());
	    				}
	    				
	    				thing_kind=((HashMap)leaseholds.get(k)).get("TYPE_NAME").toString();
	    			}
	    			//Add by Michael 2012 07-10 去除重复的制造商
	    			//manufacturerList.add(thing_kind) ;
	    			
	    			if(((HashMap)leaseholds.get(k)).get("NAME")==null){
	    				name="";
	    			}else{
	    				name=((HashMap)leaseholds.get(k)).get("NAME").toString();
	    			}
	    			String unit = "";
	    			if(((HashMap)leaseholds.get(k)).get("UNIT")==null){
	    				unit="";
	    			}else{
	    				unit=((HashMap)leaseholds.get(k)).get("UNIT").toString();
	    			}
	    			String amount = "";
	    			if(((HashMap)leaseholds.get(k)).get("AMOUNT")==null){
	    				amount="";
	    			}else{
	    				amount=((HashMap)leaseholds.get(k)).get("AMOUNT").toString();
	    			}
	    			if(i<55&&i%51==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				i+=3;
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				i+=3;
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else{
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault222,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
		    		}
		    		if(i<55&&i%51==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				i+=3;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    				i+=3;
	    			}
	    		}
	    
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3("制造商：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else{
	    			for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,5));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("租赁期间", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		i+=1;	  
	    		Integer leasePeriod = null ;
	    		String fmYear = "";
	    		if(baseinfo.get("LEASE_PERIOD")!= null){
		    		leasePeriod = Integer.parseInt( baseinfo.get("LEASE_PERIOD").toString()) ;
		    		if(leasePeriod != null && leasePeriod>0){
		    			NumberFormat formatNum = new DecimalFormat("##0.##");
		    			formatNum.setGroupingUsed(true);
		    			formatNum.setMaximumFractionDigits(2);
		    			double year = Math.round(leasePeriod/12.0 * 100.0) /100.0 ;
		    			fmYear = formatNum.format(year) ;
		    		}
	    		}
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(leasePeriod != null && leasePeriod > 0){
		    			tT.addCell(makeCellSetColspan2("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));
		    		} else {
		    			tT.addCell(makeCellSetColspan2("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,5));
		    		}
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));	    			    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i<55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所: "+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;

	    		if(i<55&&i%51==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;	
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;	
    			}
	    		
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,7));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,7));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,7));	
	    		i+=3;

	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("承租方:   "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,4));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;

	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("地址:   "+custaddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
    				i+=3;
    			}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("法人代表或授权人:", PdfPCell.ALIGN_LEFT, FontDefault2,2));
				tT.addCell(makeCellSetColspanNoBorder("（签章）", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("日期:", PdfPCell.ALIGN_LEFT, FontDefault2,2));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1 ;
	    		if(i<53){
	    			for(;i<49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
		    		}
		    		if(i<=49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
		    		}
	    		}else if(i>53){
		    		int p=Math.round((i-49)/52)+1;
		    		for(;i<p*52+49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
		    		}
		    		if(i<=p*52+49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
		    		}
	    		}
			}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
	       }
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "equipmentsforshow.pdf";
	    context.response.setContentType("application/pdf");
	    context.response.setCharacterEncoding("UTF-8");
	    context.response.setHeader("Pragma", "public");
	    context.response.setHeader("Cache-Control",
		    "must-revalidate, post-check=0, pre-check=0");
	    context.response.setDateHeader("Expires", 0);
	    context.response.setHeader("Content-Disposition",
		    "attachment; filename=" + strFileName);

	    ServletOutputStream o = context.response.getOutputStream();

	    baos.writeTo(o);
	    o.flush();
		closeStream(o);
		
		BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
		   		 "导出 交货验收证明暨起租通知书",
	   		 	 "合同浏览导出 交货验收证明暨起租通知书",
	   		 	 null,
	   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	   		 	 1,
	   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	   		 	 DataUtil.longUtil(0),
	   		 	 context.getRequest().getRemoteAddr());
	} catch (Exception e) {
	    e.printStackTrace();
	    LogPrint.getLogStackTrace(e, logger);
	}
    }

   //Add by Michael 回租起租检核表
  	//接受页面报告的ID数组
  	 @SuppressWarnings("unchecked")
  	    public void preBeginCustPdfs(Context context){
  		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
  		 String[] comType=HTMLUtil.getParameterValues(context.getRequest(), "contract_type", "00");
  		
  		 if(con != null ){
  		   if(!(con[0].equals("00"))){
  			 try {
  	        		 if(con.length >1){
  	        			 context.contextMap.put("credtdxx",  con);
  	        			 context.contextMap.put("comType", comType);
  	                     this.getLeaseBackCustDateMap(context);
  	        		 }else{
  	                	if(con.length ==1){
  	                		 context.contextMap.put("credtdxx",   con);
  	                		 context.contextMap.put("comType", comType);
  	                		 this.getLeaseBackCustDateMap(context);
  	                	} 
  	        		}
  			} catch (Exception e) {
  				    e.printStackTrace();
  				    LogPrint.getLogStackTrace(e, logger);
  			}
  		  }
  		}	
  	  }
  	 /*
  	  * 回租合同检核表
  	  */
  	 public void  getLeaseBackCustDateMap(Context context){
  		 String[]  con = null;
  		 String[] comType = null;
  		  con= (String[]) context.contextMap.get("credtdxx");
  		  comType=(String[]) context.contextMap.get("comType");
  		  ArrayList creditmaps = new ArrayList();
  		  ArrayList fileUps=new ArrayList();
  		  for(int ii=0; ii< con.length;ii++){		    	
  	        	Map creditmap =new HashMap();
  	        	ArrayList list=new ArrayList();
  	        	context.contextMap.put("credit_id",  con[ii]);
  	        	context.contextMap.put("contract_type", comType[ii]);
  		       try{
  	        	creditmap = (Map) DataAccessor.query("exportContractPdf.queryCreditByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
  	        	creditmap.put("comType", comType[ii]);
  	        	list=(ArrayList)DataAccessor.query("rentFile.getUPContractCount", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
  	        	creditmaps.add(creditmap);
  	        	fileUps.add(list);
  		       }catch(Exception e){
  		    	   e.printStackTrace();
  		    	   LogPrint.getLogStackTrace(e, logger);
  		       }
  		    }
  		  this.expLeaseBackBeginCustPdfs(context,creditmaps,fileUps);
  	 }
     /**
      *导出回租合同检核表
      * @param context
      */
     @SuppressWarnings("unchecked")
     public void expLeaseBackBeginCustPdfs(Context context,ArrayList creditmaps,ArrayList fileUps) {
  	
  	ByteArrayOutputStream baos = null;
  	try {   
  	        // 字体设置
  	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);

  	        Font FontDefault22 = new Font(bfChinese, 10, Font.NORMAL);
  	        Font FontDefault222 = new Font(bfChinese, 6, Font.NORMAL);
  	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
  	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
  	    	Font FontSmall = new Font(bfChinese,8, Font.NORMAL);
  			Font FontSmall2 = new Font(bfChinese,5, Font.NORMAL);
  			Font FontTitle= new Font(bfChinese,13,Font.BOLD);
  	        Font fa = new Font(bfChinese, 22, Font.BOLD);
  	        // 数字格式
  	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
  	        nfFSNum.setGroupingUsed(true);
  	        nfFSNum.setMaximumFractionDigits(2);
  	        // 页面设置
  	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
  	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
  	        baos = new ByteArrayOutputStream();
  	        PdfWriter.getInstance(document, baos);
  	        	        
  	        // 打开文档
  	        document.open();
  	        for(int ii=0; ii< creditmaps.size();ii++){
  		    	Map creditmap=(HashMap)creditmaps.get(ii);

  	    	String customername = "";
  	    	if(creditmap.get("CUST_NAME")==null){
  	    		customername = "";
  	    	}else{
  	    		customername = creditmap.get("CUST_NAME").toString();
  	    	}
  	    	String sellername = "";
  	    	if(creditmap.get("BRAND")==null){
  	    		sellername = "";
  	    	}else{
  	    		sellername = creditmap.get("BRAND").toString();
  	    	}
  	    	
  	    	float[] width={5f,10f,10f,10f,10f,10f,10f,15f,10f,10f,10f,12f,12f};
  			PdfPTable tT = new PdfPTable(width);
  			PdfPCell cell=null;
  			
  			PdfPTable tlogo=new PdfPTable(new float[]{5f,95f});
  			cell=makeCellNoBorderSetColspan(Constants.COMPANY_NAME,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,13);
  			cell.setFixedHeight(18);
  			tT.addCell(cell);
  			
  			int i=0;
  				tT.addCell(makeCellSetColspanNoBorder("租赁案件起租文件检核表",PdfPCell.ALIGN_CENTER, FontDefault22,13));	
  	    	     
  	    		String Lcode = creditmap.get("LEASE_CODE") +"";
  	    		Lcode=	Lcode.trim();
  	    		if(Lcode.equals("")||Lcode.equals("null")){
  	    			Lcode = "           ";
  	    		}
  	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_RIGHT, FontDefault22,13));
  	    		tT.addCell(makeCellSetColspanNoBorder("客户:"+customername,PdfPCell.ALIGN_LEFT, FontDefault22,9));	    		
  	    		tT.addCell(makeCellSetColspanNoBorder("合约编号:"+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,4));	
  	    		tT.addCell(makeCellSetColspanNoBorder("供应商:"+sellername,PdfPCell.ALIGN_LEFT, FontDefault22,13));	

  	    		tT.addCell(makeCellSetColspan2LeftAndTop("    ",PdfPCell.ALIGN_LEFT, FontDefault22,1));	
  	    		if(creditmap.get("comType")==null)
  	    		{
  	    			tT.addCell(makeCellSetColspan3("", PdfPCell.ALIGN_CENTER, FontDefault2,12));
  	    		}
  	    		else
  	    		{
  	    			tT.addCell(makeCellSetColspan3("", PdfPCell.ALIGN_CENTER, FontDefault2,12));
  	    		}
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 序号 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 文件名称及附件 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 应征份数 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 已征 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 查验 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 点收 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan3(" 备 注 ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
  	    			    		
  	    		for(int h=0;h<fileUps.size();h++)
  	    		{
  	    			ArrayList list=(ArrayList)fileUps.get(h);
  	    			for(int j=0;j<list.size();j++)
  	    			{
  	    			Map fileUpContext=(Map)list.get(j);
  	    			Object FILE_NAME=fileUpContext.get("FILE_NAME");
  	    			Object WANT_COUNT=fileUpContext.get("WANT_COUNT");
  	    			Object STATE=fileUpContext.get("STATE");
  	    			Object UPFILECOUNT=fileUpContext.get("UPFILECOUNT");
  	    			if(j<(list.size()-1))
  	    			{
  	    				if(STATE==null)
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				}
  	    				else if(STATE.toString().equals("1"))
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				}
  	    				else
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				}
  	    			
  	    				String num=" "+(j+1)+" ";
  	    				tT.addCell(makeCellSetColspan2LeftAndTop(num,PdfPCell.ALIGN_CENTER, FontDefault22,1));
  		    		
  	    				if(FILE_NAME==null)
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
  	    				}
  	    				else
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop(" "+FILE_NAME+" ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
  	    				}
  		    		
  	    				if(WANT_COUNT==null)
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop(" 0 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				}
  	    				else
  	    				{
  	    					tT.addCell(makeCellSetColspan2LeftAndTop(" "+WANT_COUNT+" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				}
  	    				tT.addCell(makeCellSetColspan2LeftAndTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    				tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
  	    			}
  		    		else
  		    		{
  		    			//最后一行
  		    			if(STATE==null)
  		    			{
  		    				tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  		    			}
  		    			else if(STATE.toString().equals("1"))
  		    			{
  		    				tT.addCell(makeCellSetColspan3NoRight(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  		    			}
  		    			else
  		    			{
  		    				tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  		    			}
  		    			
  			    		String num=" "+(j+1)+" ";
  			    		tT.addCell(makeCellSetColspan3NoRight(num,PdfPCell.ALIGN_CENTER, FontDefault22,1));
  			    		
  			    		if(FILE_NAME==null)
  			    		{
  			    			tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
  			    		}
  			    		else
  			    		{
  			    			tT.addCell(makeCellSetColspan3NoRight(" "+FILE_NAME+" ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
  			    		}
  			    		
  			    		if(WANT_COUNT==null)
  			    		{
  			    			tT.addCell(makeCellSetColspan3NoRight(" 0 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  			    		}
  			    		else
  			    		{
  			    			tT.addCell(makeCellSetColspan3NoRight(" "+WANT_COUNT+" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  			    		}
  			    		tT.addCell(makeCellSetColspan3NoRight(" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
  			    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  			    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  			    		tT.addCell(makeCellWithNoBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  			    		tT.addCell(makeCellWithNoBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  		    		}
  	    			}
  		    		
  	    		}
  	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
  	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 起租记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 业管部点收记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
  	    		tT.addCell(makeCellSetColspan3(" 业务处 ",PdfPCell.ALIGN_CENTER, FontDefault22,3));
  	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("租赁期间： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 经办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 地区业务 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
  	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("期数： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspanOnlyLeft(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspanOnlyLeft("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 协办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
  	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("首次收租日： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 复核 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 单位主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
  	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellSetColspan3NoRight("标有 * 的文件為不可待補之文件。",PdfPCell.ALIGN_LEFT, FontDefault222,4));
  	    		tT.addCell(makeCellSetColspan3NoRight(" 处/部级主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
  	    		tT.addCell(makeCellSetColspan3NoRight(" 处/部级主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
  	    		tT.addCell(makeCellWithNoBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		tT.addCell(makeCellWithNoBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
  	    		i+=25;
//  	    		for(;i<43;i++){
//  	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//  	    		}
//  	    		if(i<=43){
//  	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//  	    		}
  			document.add(tT);
  			document.add(Chunk.NEXTPAGE);
  	        }
  			document.close();
  	    // 支付表PDF名字的定义
  	    String strFileName =  "rentdetectionbook.pdf";
  	    context.response.setContentType("application/pdf");
  	    context.response.setCharacterEncoding("UTF-8");
  	    context.response.setHeader("Pragma", "public");
  	    context.response.setHeader("Cache-Control",
  		    "must-revalidate, post-check=0, pre-check=0");
  	    context.response.setDateHeader("Expires", 0);
  	    context.response.setHeader("Content-Disposition",
  		    "attachment; filename=" + strFileName);

  	    ServletOutputStream o = context.response.getOutputStream();

  	    baos.writeTo(o);
  	    o.flush();
  		closeStream(o);
  	    
  	} catch (Exception e) {
  	    e.printStackTrace();
  	    LogPrint.getLogStackTrace(e, logger);
  	}
   }
   
 	public static List ExpectMonthPrice(Double pledgeAVEPrice,List irrMonthPaylines) {
		//增加预期租金计算 	
		List monthPaylines = new ArrayList() ; 
		if(pledgeAVEPrice == null){
			pledgeAVEPrice = 0.0d ;
		}
		if(irrMonthPaylines == null){
			irrMonthPaylines = new ArrayList() ;
		}
		if(irrMonthPaylines.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylines.get(irrMonthPaylines.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			double eachAVE = Math.round(pledgeAVEPrice / endNum * 100.0d)/100.0d ;
			double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) *100.0d)/100.0d ;
			for(int i=0;i<irrMonthPaylines.size();i++){
				Map temp = (Map) irrMonthPaylines.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylines.size() - 1  && eachAVE != endAVE){
					if(start != end ){
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end - 1 ) ;
						map.put("MONTH_PRICE", price + eachAVE) ;
						monthPaylines.add(map) ;
					} 
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",end ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price + endAVE) ;
					monthPaylines.add(map) ;
				}else {
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",start ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price + eachAVE) ;
					monthPaylines.add(map) ;
				}
			}
		}
		//增加预期租金计算    结束
		return monthPaylines ;
	}
     
 	public static List ExpectMonthPriceValueAdded(List irrMonthPaylines,Double pledgeAVEPrice,Double valueAddedTax,Double lastValueAddedTax) {
		//增加预期租金计算 	
		List monthPaylines = new ArrayList() ; 
		if(pledgeAVEPrice == null){
			pledgeAVEPrice = 0.0d ;
		}
		if(irrMonthPaylines == null){
			irrMonthPaylines = new ArrayList() ;
		}
		if(irrMonthPaylines.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylines.get(irrMonthPaylines.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			double eachAVE = Math.round(pledgeAVEPrice / endNum * 100.0d)/100.0d ;
			double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) *100.0d)/100.0d ;
			for(int i=0;i<irrMonthPaylines.size();i++){
				Map temp = (Map) irrMonthPaylines.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylines.size() - 1  && eachAVE != endAVE){
					if(valueAddedTax!=lastValueAddedTax){
						if(start != end ){
							map = new HashMap() ;
							map.put("MONTH_PRICE_START",start ) ;
							map.put("MONTH_PRICE_END",end - 1 ) ;
							map.put("MONTH_PRICE", price + eachAVE) ;
							map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
							monthPaylines.add(map) ;
						} 
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",end ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE", price + endAVE) ;
						map.put("MONTH_PRICE_TAX", price + endAVE+lastValueAddedTax) ;
						monthPaylines.add(map) ;
					}else {
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE", price + eachAVE) ;
						map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
						monthPaylines.add(map) ;
					}
				}else {
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",start ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price + eachAVE) ;
					map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
					monthPaylines.add(map) ;
				}
			}
		}
		//增加预期租金计算    结束
		return monthPaylines ;
	}
   
 	
 	public static List ExpectMonthPriceValueAddedTest(List irrMonthPaylines,Double valueAddedTax,Double lastValueAddedTax) {
		//增加预期租金计算 	
		List monthPaylines = new ArrayList() ; 
		if(irrMonthPaylines == null){
			irrMonthPaylines = new ArrayList() ;
		}
		if(irrMonthPaylines.size() != 0){
			int endNum = Integer.parseInt(((Map)irrMonthPaylines.get(irrMonthPaylines.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
			for(int i=0;i<irrMonthPaylines.size();i++){
				Map temp = (Map) irrMonthPaylines.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylines.size() - 1  ){
					if(valueAddedTax!=lastValueAddedTax){
						if(start != end ){
							map = new HashMap() ;
							map.put("MONTH_PRICE_START",start ) ;
							map.put("MONTH_PRICE_END",end - 1 ) ;
							map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
							monthPaylines.add(map) ;
						} 
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",end ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE_TAX", price + lastValueAddedTax) ;
						monthPaylines.add(map) ;
					}else {
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
						monthPaylines.add(map) ;
					}
				}else {
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",start ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE_TAX", price + valueAddedTax) ;
					monthPaylines.add(map) ;
				}
			}
		}
		//增加预期租金计算    结束
		return monthPaylines ;
	}
  
 	//导出租赁物情况表    
 	@SuppressWarnings("unchecked")
 	public void expZulwToPdf_new(Context context) {

 		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
 		ArrayList booknotes=new ArrayList();
 		HashMap booknote=new HashMap();
 		Map creditCustomerMap = null;
 		List<Map> equipmentsList = null;
 		Map schemeMap = null;
 		Map paylist = new HashMap();
 		try{
 			Map schema = new HashMap();
 			schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			// 查询应付租金列表
 			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
 			//System.out.println(irrMonthPaylines.size()+"=============");
 			// 解压irrMonthPaylines到每一期的钱
 			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
 			//System.out.println(rePaylineList.size());
 			//List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
 			
 			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			paylist = schemeMap ;
 			//paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
 			paylist.put("rePaylineList", rePaylineList);
 			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
 			paylist.put("irrMonthPaylines", irrMonthPaylines);
 			paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
 			paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
 			StartPayService.packagePaylinesForMon(paylist);
 			
 			//List<Map> irrMonthPaylines = new ArrayList<Map>();			 
 			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
 			
 			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
 			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
 			//修改 若是法人，查法人表			
 			if(custTyp.equals("1")){
 			    
 			    creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			if(creditCustomerMap==null){
 				creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			booknote.put("rePaylineList", rePaylineList);
 			booknote.put("schema", schema);
 			booknote.put("paylist", paylist);
 			booknote.put("irrMonthPaylines", irrMonthPaylines);
 			booknote.put("monthPaylines", this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""), (List)paylist.get("oldirrMonthPaylines")));
 			booknote.put("equipmentsList", equipmentsList);
 			booknote.put("creditCustomerMap", creditCustomerMap);
 			booknote.put("schemeMap", schemeMap);
 			booknotes.add(booknote);
 			context.contextMap.put("booknotes", booknotes);
 			this.expZulwPdfModel(context,"expZulwToPdf");
 		}catch(Exception e){
 			e.printStackTrace();
 			LogPrint.getLogStackTrace(e, logger);
 		}

 	}
     
 //导出租赁物情况表    
 	@SuppressWarnings("unchecked")
 	public void expZulwPdfModel(Context context,String methodname){
 		ArrayList booknotes=new ArrayList();
 		if(context.contextMap.get("booknotes")!=null&&((ArrayList)context.contextMap.get("booknotes")).size()>0){
 			booknotes=(ArrayList)context.contextMap.get("booknotes");
 		}
 		
 		ByteArrayOutputStream baos = null;
 		try {
 			// 字体设置
 			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
 			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
 			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
 			 
 			// 数字格式
 			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
 			nfFSNum.setGroupingUsed(true);
 			nfFSNum.setMaximumFractionDigits(2);
 			// 页面设置
 			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
 			Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距 左右上下
 			baos = new ByteArrayOutputStream();
 			PdfWriter.getInstance(document, baos);
 		
 			//打开文档
 			document.open();
 			//支付表PDF名字的定义
 			String strFileName = "";
 			// 表格列宽定义
 			float[] widthsStl = {0.10f,0.20f,0.30f,0.20f,0.20f};
 			int iCnt = 0;			
 			float[] widthsPPCa = { 1f };
 			int t=2;
 			for(int bookint=0;bookint<booknotes.size();bookint++){
 				Map paylist = null;
 				Map schema = new HashMap();
 				schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
 				paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
 				List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
 				List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
 				List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
 				int lenn1 = irrMonthPaylines.size();
 			
 				PdfPTable tT1 = new PdfPTable(widthsPPCa);
 				 
 				tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
 				tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
 				document.add(tT1);
 				
 				List<Map> equipmentsList = (List<Map>)((HashMap)booknotes.get(bookint)).get("equipmentsList");
 				PdfPTable tT2 = new PdfPTable(2);
 	
 				
 				//页眉
 				PdfPTable table = new PdfPTable(1);
 				table.addCell(makeCellSetColspan2ForZLWHead("",PdfPCell.ALIGN_LEFT, FontDefault,1));
 				Phrase phrase = new Phrase();
 				phrase.add(table);
 				    
 				HeaderFooter hf = new HeaderFooter(phrase,false);
 				hf.setBorder(0);
 				document.setHeader(hf);	
 					
 				
 				tT2.addCell(makeCellS("1、租 赁 物 ", PdfPCell.ALIGN_LEFT, FontColumn));
 				tT2.addCell(makeCellSGAI("2、卖 方 及 制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
 				
 				int siz = equipmentsList.size();
 				if(siz!=0){
 					t=t+siz*3;
 				}
 				for(int i=0;i<equipmentsList.size();i++){
 				    int cnt=1;
 					if(equipmentsList.get(i).get("BRAND").toString().length()>=16){
 						cnt=2;
 						t++;
 					}
 				    int cnt2=1;
 					if(equipmentsList.get(i).get("THING_KIND").toString().length()>=16){
 						cnt2=2;
 						t++;
 					}				
 					tT2.addCell(makeCellWithBorder("名称 : "+( equipmentsList.get(i).get("THING_NAME")==null?"":equipmentsList.get(i).get("THING_NAME") ), PdfPCell.ALIGN_LEFT, FontDefault,1));
 					tT2.addCell(makeCellWithBorderRightP("卖方 : "+( equipmentsList.get(i).get("BRAND")==null?"":equipmentsList.get(i).get("BRAND")), PdfPCell.ALIGN_LEFT, FontDefault,cnt));	
 					
 					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+"      机号：" , PdfPCell.ALIGN_LEFT, FontDefault,1));
 					tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
 					
 					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
 					tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
 						
 				}
 				 
 				Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
 				
 				Map schemeMap = (HashMap)((HashMap)booknotes.get(bookint)).get("schemeMap");
 				int equAdd_cnt=1; 
 				if(creditCustomerMap.get("EQUPMENT_ADDRESS")!=null){
 					if(creditCustomerMap.get("EQUPMENT_ADDRESS").toString().length()>=16){
 						equAdd_cnt=2;
 						t++;
 					}				
 				}
 				
 				int compAdd_cnt=1; 
 				if(creditCustomerMap.get("CORP_REGISTE_ADDRESS")!=null){
 					if(creditCustomerMap.get("CORP_REGISTE_ADDRESS").toString().length()>=16){
 						compAdd_cnt=2;
 						t++;
 					}				
 				}			
 	
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("3、租 赁 物 放 置 场 所 ", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				tT2.addCell(makeCellSetColspanLRBorder("公 司 名 称 ："+((creditCustomerMap==null||creditCustomerMap.get("CUST_NAME")==null)?"":creditCustomerMap.get("CUST_NAME")) , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanNoButtomBorderTOPAuto("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
 				tT2.addCell(makeCellSetColspanNoBorderTOPAuto("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,equAdd_cnt));
 				t=t+4;
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				if(methodname.equals("expZulwToPdf")){
 				
 					//年保留两位小数
// 					double year=DataUtil.doubleUtil(paylist.get("LEASE_PERIOD"))*DataUtil.doubleUtil(paylist.get("LEASE_TERM"))/12;
 					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
 					BigDecimal byear=new BigDecimal(year);
 					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
 					
 					tT2.addCell(makeCellSetColspanLRBorder("   【" + newyear  +"】年（【"+DataUtil.intUtil(paylist.get("LEASE_PERIOD"))+"】期，每期" + DataUtil.intUtil(paylist.get("LEASE_TERM"))+"个月）" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
 					BigDecimal byear=new BigDecimal(year);
 					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

 					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+2;
 				for(int x = 0;x<monthPaylines.size();x++){
 				    
 				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元" 
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+monthPaylines.size();

 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				
 				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				// 交付预定日及验收期限栏：交付预定日：2010年___月（此月份用空白显示，让业务员填写）
 				//tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：20____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				t=t+3;			
 				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+  updateMoney(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE")),nfFSNum) +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+   updateMon(schemeMap.get("PLEDGE_PRICE")+"") +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+2; 
 				
 				 
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				t=t+1;
 				// 4、租金支付方法及方式栏：如果是期初的话 第1期显示为：“租赁物验收证明书交付之日，以转账支付。”
 				// 如果是期末的话 第1期显示为：“租赁物验收证明书交付之日，后30日以转账支付。”
 				int payWay =0;
 				if(methodname.equals("expZulwToPdf")){
 					payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
 				}else{
 					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
 				}
 					if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
 						|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
 						|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
 					if(t==39||(t-39)%38==0){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}
 					else{
 						tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}
 			 	} else {
 			 		if(t==39||(t-39)%38==0){
 				 		tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 			 		}else{
 				 		tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 			 		}
 			 	}
 				t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 				double stayBuyPrice = 0d;
 				for (int i=0;i<equipmentsList.size();i++) {
 					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
 				}
 				
 				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				 
 				t=t+2;
 	
 				if(methodname.equals("expZulwToPdf")){
 				//StartPayService.packagePaylines(paylist);
 					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");
 				}else{
 					
 					//StartPayService.packagePaylines(paylist);
 					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");	
 				}
 				int lenn2 = irrMonthPaylines.size();
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金	用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));				
 				}
 				t++;			

 				for(int x = 0;x<irrMonthPaylines.size();x++){
 					if(t==39||(t-39)%38==0){
 				    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}else{
 					    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 					    //t++;
 			 		}
 				}
 				t=t+irrMonthPaylines.size();
 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				int pageNum=(int)Math.floor((t-39)/38)+1;
 				if(t<=39){
 					for (;t<39; t++) {
 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}	
 					if(t==39){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
 						 t++;
 					}
 				}else{
 					for (;t<38*pageNum+39; t++) {
 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}	
 					if(t==38*pageNum+39){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
 						 t++;
 					}
 				}
 				document.add(tT2);
 				if(booknotes.size()>1	&&	bookint<booknotes.size()){	
 					document.resetHeader();
 				    document.add(Chunk.NEXTPAGE);	
 				    
 				}
 				
 			}			
 				document.close();
 				context.response.setContentType("application/pdf");
 				context.response.setCharacterEncoding("UTF-8");
 				context.response.setHeader("Pragma", "public");
 				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
 				context.response.setDateHeader("Expires", 0);
 				context.response.setHeader("Content-Disposition","attachment; filename=zulinwuinfo.pdf");			
 				ServletOutputStream o = context.response.getOutputStream();
 				baos.writeTo(o); 
 				o.flush();				
 				o.close();	
 				
 				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
 				   		 "导出 租赁物情况表",
 			   		 	 "合同浏览导出 租赁物情况表",
 			   		 	 null,
 			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
 			   		 	 1,
 			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
 			   		 	 DataUtil.longUtil(0),
 			   		 	 context.getRequest().getRemoteAddr());
 		} catch (Exception e) {
 			e.printStackTrace();	
 			LogPrint.getLogStackTrace(e, logger);
 		}
 	}

 	//Add by Michael 2012 09-25 导出租赁物情况表 For 新品回租 增值税版本
 	public void expZulwToPdfByValueAdded(Context context) {
 		
 		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
 		ArrayList booknotes=new ArrayList();
 		HashMap booknote=new HashMap();
 		Map creditCustomerMap = null;
 		List<Map> equipmentsList = null;
 		Map schemeMap = null;
 		Map paylist = new HashMap();
 		try{
 			Map schema = new HashMap();
 			schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			// 查询应付租金列表
 			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
 			//System.out.println(irrMonthPaylines.size()+"=============");
 			// 解压irrMonthPaylines到每一期的钱
 			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
 			//System.out.println(rePaylineList.size());
 			//List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
 			
 			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			paylist = schemeMap ;
 			//paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
 			paylist.put("rePaylineList", rePaylineList);
 			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
 			paylist.put("irrMonthPaylines", irrMonthPaylines);
 			paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
 			paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
 			StartPayService.packagePaylinesForValueAdded(paylist);
 			//List<Map> irrMonthPaylines = new ArrayList<Map>();			 
 			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
 			
 			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
 			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
 			//修改 若是法人，查法人表			
 			if(custTyp.equals("1")){
 			    
 			    creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			if(creditCustomerMap==null){
 				creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			booknote.put("rePaylineList", rePaylineList);
 			booknote.put("schema", schema);
 			booknote.put("paylist", paylist);
 			booknote.put("irrMonthPaylines", irrMonthPaylines);
 			booknote.put("monthPaylines", this.ExpectMonthPriceValueAdded((List)paylist.get("oldirrMonthPaylines"),Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""),Double.parseDouble(paylist.get("valueAddedTax")+""),Double.parseDouble(paylist.get("lastValueAddedTax")+"")));
 			booknote.put("equipmentsList", equipmentsList);
 			booknote.put("creditCustomerMap", creditCustomerMap);
 			booknote.put("schemeMap", schemeMap);
 			booknotes.add(booknote);
 			context.contextMap.put("booknotes", booknotes);
 			this.expZulwPdfModelByValueAdded(context,"expZulwToPdf");
 		}catch(Exception e){
 			e.printStackTrace();
 			LogPrint.getLogStackTrace(e, logger);
 		}

 	}

 	
 	//Add by Michael 2012 09-25 导出租赁物情况表 For 一般回租 增值税版本
 	public void expLeaseBackZulwToPdfByValueAdded(Context context) {
 		
 		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
 		ArrayList booknotes=new ArrayList();
 		HashMap booknote=new HashMap();
 		Map creditCustomerMap = null;
 		List<Map> equipmentsList = null;
 		Map schemeMap = null;
 		Map paylist = new HashMap();
 		try{
 			Map schema = new HashMap();
 			schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			// 查询应付租金列表
 			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
 			//System.out.println(irrMonthPaylines.size()+"=============");
 			// 解压irrMonthPaylines到每一期的钱
 			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
 			//System.out.println(rePaylineList.size());
 			//List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
 			
 			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
 			paylist = schemeMap ;
 			//paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
 			paylist.put("rePaylineList", rePaylineList);
 			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
 			paylist.put("irrMonthPaylines", irrMonthPaylines);
 			paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
 			paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
 			StartPayService.packagePaylinesForValueAdded(paylist);
 			//List<Map> irrMonthPaylines = new ArrayList<Map>();			 
 			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
 			
 			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
 			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
 			//修改 若是法人，查法人表			
 			if(custTyp.equals("1")){
 			    
 			    creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			if(creditCustomerMap==null){
 				creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
 			}
 			booknote.put("rePaylineList", rePaylineList);
 			booknote.put("schema", schema);
 			booknote.put("paylist", paylist);
 			booknote.put("irrMonthPaylines", irrMonthPaylines);
 			booknote.put("monthPaylines", this.ExpectMonthPriceValueAdded((List)paylist.get("oldirrMonthPaylines"),Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""),Double.parseDouble(paylist.get("valueAddedTax")+""),Double.parseDouble(paylist.get("lastValueAddedTax")+"")));
 			booknote.put("equipmentsList", equipmentsList);
 			booknote.put("creditCustomerMap", creditCustomerMap);
 			booknote.put("schemeMap", schemeMap);
 			booknotes.add(booknote);
 			context.contextMap.put("booknotes", booknotes);
 			this.expLeaseBackZulwPdfModelByValueAdded(context,"expZulwToPdf");
 		}catch(Exception e){
 			e.printStackTrace();
 			LogPrint.getLogStackTrace(e, logger);
 		}

 	}

	 //导出租赁物情况表      For 新品回租 增值税版本
	@SuppressWarnings("unchecked")
	public void expZulwPdfModelByValueAdded(Context context,String methodname){
		ArrayList booknotes=new ArrayList();
		if(context.contextMap.get("booknotes")!=null&&((ArrayList)context.contextMap.get("booknotes")).size()>0){
			booknotes=(ArrayList)context.contextMap.get("booknotes");
		}
		
		ByteArrayOutputStream baos = null;
		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
			 
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距 左右上下
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
		
			//打开文档
			document.open();
			//支付表PDF名字的定义
			String strFileName = "";
			// 表格列宽定义
			float[] widthsStl = {0.10f,0.20f,0.30f,0.20f,0.20f};
			int iCnt = 0;			
			float[] widthsPPCa = { 1f };
			int t=2;
			for(int bookint=0;bookint<booknotes.size();bookint++){
				Map paylist = null;
				Map schema = new HashMap();
				schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
				paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
				List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
				List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
				List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
				int lenn1 = irrMonthPaylines.size();
			
				PdfPTable tT1 = new PdfPTable(widthsPPCa);
				 
				tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
				tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
				document.add(tT1);
				
				List<Map> equipmentsList = (List<Map>)((HashMap)booknotes.get(bookint)).get("equipmentsList");
				PdfPTable tT2 = new PdfPTable(2);
	
				
				//页眉
				PdfPTable table = new PdfPTable(1);
				table.addCell(makeCellSetColspan2ForZLWHead("",PdfPCell.ALIGN_LEFT, FontDefault,1));
				Phrase phrase = new Phrase();
				phrase.add(table);
				    
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
					
				
				tT2.addCell(makeCellS("1、租 赁 物 ", PdfPCell.ALIGN_LEFT, FontColumn));
				tT2.addCell(makeCellSGAI("2、制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
				
				int siz = equipmentsList.size();
				if(siz!=0){
					t=t+siz*3;
				}
				for(int i=0;i<equipmentsList.size();i++){
				    int cnt=1;
					if(equipmentsList.get(i).get("BRAND").toString().length()>=16){
						cnt=2;
						t++;
					}
				    int cnt2=1;
					if(equipmentsList.get(i).get("THING_KIND").toString().length()>=16){
						cnt2=2;
						t++;
					}				
					tT2.addCell(makeCellWithBorder("名称 : "+( equipmentsList.get(i).get("THING_NAME")==null?"":equipmentsList.get(i).get("THING_NAME") ), PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt));	
					
					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+"      机号：" , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP("" , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
					
					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
					tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
						
				}
				 
				Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
				
				Map schemeMap = (HashMap)((HashMap)booknotes.get(bookint)).get("schemeMap");
				int equAdd_cnt=1; 
				if(creditCustomerMap.get("EQUPMENT_ADDRESS")!=null){
					if(creditCustomerMap.get("EQUPMENT_ADDRESS").toString().length()>=16){
						equAdd_cnt=2;
						t++;
					}				
				}
				
				int compAdd_cnt=1; 
				if(creditCustomerMap.get("CORP_REGISTE_ADDRESS")!=null){
					if(creditCustomerMap.get("CORP_REGISTE_ADDRESS").toString().length()>=16){
						compAdd_cnt=2;
						t++;
					}				
				}			
	
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("3、租 赁 物 放 置 场 所 ", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanLRBorder("公 司 名 称 ："+((creditCustomerMap==null||creditCustomerMap.get("CUST_NAME")==null)?"":creditCustomerMap.get("CUST_NAME")) , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanNoButtomBorderTOPAuto("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
				tT2.addCell(makeCellSetColspanNoBorderTOPAuto("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,equAdd_cnt));
				t=t+4;
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				if(methodname.equals("expZulwToPdf")){
				
					//年保留两位小数
//					double year=DataUtil.doubleUtil(paylist.get("LEASE_PERIOD"))*DataUtil.doubleUtil(paylist.get("LEASE_TERM"))/12;
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					
					tT2.addCell(makeCellSetColspanLRBorder("   【" + newyear  +"】年（【"+DataUtil.intUtil(paylist.get("LEASE_PERIOD"))+"】期，每期" + DataUtil.intUtil(paylist.get("LEASE_TERM"))+"个月）" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+2;
				for(int x = 0;x<monthPaylines.size();x++){
				    
				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+monthPaylines.size();

				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
				// 交付预定日及验收期限栏：交付预定日：2010年___月（此月份用空白显示，让业务员填写）
				//tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：20____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
				t=t+3;			
				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+  updateMoney(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE")),nfFSNum) +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+   updateMon(schemeMap.get("PLEDGE_PRICE")+"") +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+2; 
				
				 
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
				t=t+1;
				// 4、租金支付方法及方式栏：如果是期初的话 第1期显示为：“租赁物验收证明书交付之日，以转账支付。”
				// 如果是期末的话 第1期显示为：“租赁物验收证明书交付之日，后30日以转账支付。”
				int payWay =0;
				if(methodname.equals("expZulwToPdf")){
					payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
				}else{
					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
				}
					if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					else{
						tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
			 	} else {
			 		if(t==39||(t-39)%38==0){
				 		tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
			 		}else{
				 		tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
			 		}
			 	}
				t++;
		 		if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
		 		}else{
					tT2.addCell(makeCellSetColspanLRBorder("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
		 		}
		 		t++;
		 		if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}else{
					tT2.addCell(makeCellSetColspanLRBorder("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}
		 		t++;
		 		if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}else{
					tT2.addCell(makeCellSetColspanLRBorder("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}
		 		t++;
		 		if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}else{
					tT2.addCell(makeCellSetColspanLRBorder( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
		 		}
		 		t++;
				double stayBuyPrice = 0d;
				for (int i=0;i<equipmentsList.size();i++) {
					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
				}
				
				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 
				t=t+2;
				
				int lenn2 = irrMonthPaylines.size();
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金	用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));				
				}
				t++;			

				for(int x = 0;x<irrMonthPaylines.size();x++){
					if(t==39||(t-39)%38==0){
				    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
					    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"  
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					    //t++;
			 		}
				}
				t=t+irrMonthPaylines.size();
				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				int pageNum=(int)Math.floor((t-39)/38)+1;
				if(t<=39){
					for (;t<39; t++) {
						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}	
					if(t==39){
						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
						 t++;
					}
				}else{
					for (;t<38*pageNum+39; t++) {
						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}	
					if(t==38*pageNum+39){
						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
						 t++;
					}
				}
				document.add(tT2);
				if(booknotes.size()>1	&&	bookint<booknotes.size()){	
					document.resetHeader();
				    document.add(Chunk.NEXTPAGE);	
				    
				}
				
			}			
				document.close();
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=zulinwuinfo.pdf");			
				ServletOutputStream o = context.response.getOutputStream();
				baos.writeTo(o); 
				o.flush();				
				o.close();	
				
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 租赁物情况表",
			   		 	 "合同浏览导出 租赁物情况表",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();	
			LogPrint.getLogStackTrace(e, logger);
		}
	}

 	
 	 //导出租赁物情况表      For 新品回租 增值税版本
 	@SuppressWarnings("unchecked")
 	public void expLeaseBackZulwPdfModelByValueAdded(Context context,String methodname){
 		ArrayList booknotes=new ArrayList();
 		if(context.contextMap.get("booknotes")!=null&&((ArrayList)context.contextMap.get("booknotes")).size()>0){
 			booknotes=(ArrayList)context.contextMap.get("booknotes");
 		}
 		
 		ByteArrayOutputStream baos = null;
 		try {
 			// 字体设置
 			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
 			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
 			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
 			 
 			// 数字格式
 			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
 			nfFSNum.setGroupingUsed(true);
 			nfFSNum.setMaximumFractionDigits(2);
 			// 页面设置
 			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
 			Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距 左右上下
 			baos = new ByteArrayOutputStream();
 			PdfWriter.getInstance(document, baos);
 		
 			//打开文档
 			document.open();
 			//支付表PDF名字的定义
 			String strFileName = "";
 			// 表格列宽定义
 			float[] widthsStl = {0.10f,0.20f,0.30f,0.20f,0.20f};
 			int iCnt = 0;			
 			float[] widthsPPCa = { 1f };
 			int t=2;
 			for(int bookint=0;bookint<booknotes.size();bookint++){
 				Map paylist = null;
 				Map schema = new HashMap();
 				schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
 				paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
 				List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
 				List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
 				List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
 				int lenn1 = irrMonthPaylines.size();
 			
 				PdfPTable tT1 = new PdfPTable(widthsPPCa);
 				 
 				tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
 				tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
 				document.add(tT1);
 				
 				List<Map> equipmentsList = (List<Map>)((HashMap)booknotes.get(bookint)).get("equipmentsList");
 				PdfPTable tT2 = new PdfPTable(2);
 	
 				
 				//页眉
 				PdfPTable table = new PdfPTable(1);
 				table.addCell(makeCellSetColspan2ForZLWHead("",PdfPCell.ALIGN_LEFT, FontDefault,1));
 				Phrase phrase = new Phrase();
 				phrase.add(table);
 				    
 				HeaderFooter hf = new HeaderFooter(phrase,false);
 				hf.setBorder(0);
 				document.setHeader(hf);	
 					
 				
 				tT2.addCell(makeCellS("1、租 赁 物 ", PdfPCell.ALIGN_LEFT, FontColumn));
 				tT2.addCell(makeCellSGAI("2、卖 方 及 制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
 				
 				int siz = equipmentsList.size();
 				if(siz!=0){
 					t=t+siz*3;
 				}
 				for(int i=0;i<equipmentsList.size();i++){
 				    int cnt=1;
 					if(equipmentsList.get(i).get("BRAND").toString().length()>=16){
 						cnt=2;
 						t++;
 					}
 				    int cnt2=1;
 					if(equipmentsList.get(i).get("THING_KIND").toString().length()>=16){
 						cnt2=2;
 						t++;
 					}				
 					tT2.addCell(makeCellWithBorder("名称 : "+( equipmentsList.get(i).get("THING_NAME")==null?"":equipmentsList.get(i).get("THING_NAME") ), PdfPCell.ALIGN_LEFT, FontDefault,1));
 					tT2.addCell(makeCellWithBorderRightP("卖方 : "+( equipmentsList.get(i).get("BRAND")==null?"":equipmentsList.get(i).get("BRAND")), PdfPCell.ALIGN_LEFT, FontDefault,cnt));	
 					
 					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+"      机号：" , PdfPCell.ALIGN_LEFT, FontDefault,1));
 					tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
 					
 					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
 					tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
 						
 				}
 				 
 				Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
 				
 				Map schemeMap = (HashMap)((HashMap)booknotes.get(bookint)).get("schemeMap");
 				int equAdd_cnt=1; 
 				if(creditCustomerMap.get("EQUPMENT_ADDRESS")!=null){
 					if(creditCustomerMap.get("EQUPMENT_ADDRESS").toString().length()>=16){
 						equAdd_cnt=2;
 						t++;
 					}				
 				}
 				
 				int compAdd_cnt=1; 
 				if(creditCustomerMap.get("CORP_REGISTE_ADDRESS")!=null){
 					if(creditCustomerMap.get("CORP_REGISTE_ADDRESS").toString().length()>=16){
 						compAdd_cnt=2;
 						t++;
 					}				
 				}			
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("3、租 赁 物 放 置 场 所 ", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				tT2.addCell(makeCellSetColspanLRBorder("公 司 名 称 ："+((creditCustomerMap==null||creditCustomerMap.get("CUST_NAME")==null)?"":creditCustomerMap.get("CUST_NAME")) , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanNoButtomBorderTOPAuto("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
 				tT2.addCell(makeCellSetColspanNoBorderTOPAuto("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,equAdd_cnt));
 				t=t+4;
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				if(methodname.equals("expZulwToPdf")){
 				
 					//年保留两位小数
// 					double year=DataUtil.doubleUtil(paylist.get("LEASE_PERIOD"))*DataUtil.doubleUtil(paylist.get("LEASE_TERM"))/12;
 					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
 					BigDecimal byear=new BigDecimal(year);
 					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
 					
 					tT2.addCell(makeCellSetColspanLRBorder("   【" + newyear  +"】年（【"+DataUtil.intUtil(paylist.get("LEASE_PERIOD"))+"】期，每期" + DataUtil.intUtil(paylist.get("LEASE_TERM"))+"个月）" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
 					BigDecimal byear=new BigDecimal(year);
 					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

 					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+2;
 				for(int x = 0;x<monthPaylines.size();x++){
 				    
 				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+monthPaylines.size();

 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				
 				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				// 交付预定日及验收期限栏：交付预定日：2010年___月（此月份用空白显示，让业务员填写）
 				//tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：20____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				t=t+3;			
 				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+  updateMoney(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE")),nfFSNum) +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+   updateMon(schemeMap.get("PLEDGE_PRICE")+"") +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t=t+2; 
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				t=t+1;
 				// 4、租金支付方法及方式栏：如果是期初的话 第1期显示为：“租赁物验收证明书交付之日，以转账支付。”
 				// 如果是期末的话 第1期显示为：“租赁物验收证明书交付之日，后30日以转账支付。”
 				int payWay =0;
 				if(methodname.equals("expZulwToPdf")){
 					payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
 				}else{
 					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
 				}
 					if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
 						|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
 						|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
 					if(t==39||(t-39)%38==0){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}
 					else{
 						tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}
 			 	} else {
 			 		if(t==39||(t-39)%38==0){
 				 		tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 			 		}else{
 				 		tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 			 		}
 			 	}
 				t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款。" , PdfPCell.ALIGN_LEFT, FontDefault,2));
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                             支付日如遇节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder("                                各期支付的金额详见甲方出具的《租金支付明细表》。", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 		 		if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}else{
 					tT2.addCell(makeCellSetColspanLRBorder( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
 		 		}
 		 		t++;
 				double stayBuyPrice = 0d;
 				for (int i=0;i<equipmentsList.size();i++) {
 					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
 				}
 				
 				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
 				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				 
 				t=t+2;
 				
				int lenn2 = irrMonthPaylines.size();
 				
 				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于平均抵冲金额:【"+updateMon(paylist.get("PLEDGE_AVE_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金	用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于期末退还金额:【"+updateMon(paylist.get("PLEDGE_BACK_PRICE"))+"】", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				if(t==39||(t-39)%38==0){
 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				else{
 					tT2.addCell(makeCellSetColspanLRBorder("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));				
 				}
 				t++;			

 				for(int x = 0;x<irrMonthPaylines.size();x++){
 					if(t==39||(t-39)%38==0){
 				    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元" 
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}else{
 					    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"  
 						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
 					    //t++;
 			 		}
 				}
 				t=t+irrMonthPaylines.size();
 				if(methodname.equals("expZulwToPdf")){
 					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}else{
 					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
 				}
 				t++;
 				int pageNum=(int)Math.floor((t-39)/38)+1;
 				if(t<=39){
 					for (;t<39; t++) {
 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}	
 					if(t==39){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
 						 t++;
 					}
 				}else{
 					for (;t<38*pageNum+39; t++) {
 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
 					}	
 					if(t==38*pageNum+39){
 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
 						 t++;
 					}
 				}
 				document.add(tT2);
 				if(booknotes.size()>1	&&	bookint<booknotes.size()){	
 					document.resetHeader();
 				    document.add(Chunk.NEXTPAGE);	
 				    
 				}
 				
 			}			
 				document.close();
 				context.response.setContentType("application/pdf");
 				context.response.setCharacterEncoding("UTF-8");
 				context.response.setHeader("Pragma", "public");
 				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
 				context.response.setDateHeader("Expires", 0);
 				context.response.setHeader("Content-Disposition","attachment; filename=zulinwuinfo.pdf");			
 				ServletOutputStream o = context.response.getOutputStream();
 				baos.writeTo(o); 
 				o.flush();				
 				o.close();	
 				
 				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
 				   		 "导出 租赁物情况表",
 			   		 	 "合同浏览导出 租赁物情况表",
 			   		 	 null,
 			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
 			   		 	 1,
 			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
 			   		 	 DataUtil.longUtil(0),
 			   		 	 context.getRequest().getRemoteAddr());
 		} catch (Exception e) {
 			e.printStackTrace();	
 			LogPrint.getLogStackTrace(e, logger);
 		}
 	}

	/*
	 * 导出担保公司股东会决议
	 */
	@SuppressWarnings("unchecked")
	public void expVouchCorpMettingResolutionPdfs(Context context) {

		ByteArrayOutputStream baos = null;
		List vouchCorpList = new ArrayList();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			vouchCorpList = (List) DataAccessor.query(
					"exportContractPdf.queryVouchCorpList", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			// 打开文档
			document.open();
			Map baseinfo=null;
			for (int ii = 0; ii < vouchCorpList.size(); ii++) {
				baseinfo = (Map) vouchCorpList.get(ii);
				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}
				// 担保公司
				String  corp_name= "";
				if (baseinfo.get("CORP_NAME_CN") == null) {
					corp_name = "  ";
				} else {
					corp_name = baseinfo.get("CORP_NAME_CN")
							.toString();
				}
				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}

				PdfPTable tT = new PdfPTable(10);
				Phrase phrase = new Phrase();
//				phrase.add(table);
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder(corp_name, PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("股东会决议", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("公司名称：", FontDefault2)); 
	    		phrase.add(new Phrase(corp_name, FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));	
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("时间：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("地点：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("主席：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2); 	    		
	    		phrase.add(new Phrase("本次股东会的召集程序、审议程序和表决方式均符合《中华人民共和国公司法》及本公司章", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("程规定和要求。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 	    		
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("根据本公司章程规定，本公司全体股东均出席本次股东会会议，经协商一致，同意就", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase(custname , FontUnde2)); 
	    		phrase.add(new Phrase(("(下称“承租人”)与") , FontDefault2));
	    		String creditId =(String) context.contextMap.get("credit_id"); 
	    		
	    		String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
				int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
				String companyName = Constants.COMPANY_NAME;
				
				if("7".equals(contractType)){
					companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
				}
				
	    		phrase.add(new Phrase(companyName , FontUnde2));
	    		
	    		phrase.add(new Phrase(("(下称“出租人”)") , FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("的融资租赁项目的相关事宜，作出如下决议：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 

	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("本公司同意就承租人与出租人签订的编号为", FontDefault2)); 
	    		phrase.add(new Phrase(Lcode , FontUnde2)); 
	    		phrase.add(new Phrase(("的《融资租赁合同》及其他相关合同") , FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("项下的所有义务(包括但不限于付款义务)向出租人提供保证责任。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("特此决议。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("出席股东：", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名/名称）__________________（签字/盖章）                    （姓名/名称）__________________（签字/盖章）", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名/名称）__________________（签字/盖章）                    （姓名/名称）__________________（签字/盖章）", FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("（姓名/名称）__________________（签字/盖章）                    （姓名/名称）__________________（签字/盖章）", FontDefault2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("公司盖章：", FontDefault2)); 
	    		phrase.add(new Phrase(corp_name , FontUnde2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("本公司担保上述决议内容真实无伪。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	    	

				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "MettingResolution.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出连保公司股东会决议", "合同浏览导出连保公司股东会决议", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

  
 	
 	/** ￥0.00 */
	private String updateMoney(Map map,String content,NumberFormat nfFSNum) {
		String str="";
		if(map.get(content).toString().equals("0")){
			str+="0.00";
			return str;
		}
		else{
			str+=nfFSNum.format(Double.parseDouble(map.get(content).toString()));
			return str;
		}	
	}	
	/** ￥0.00 */
	private String updateMoney(Double dNum,NumberFormat nfFSNum) {
		String str="";
		if (dNum == 0d) {
			str+="0.00";
			return str;
		} else {
			str+=nfFSNum.format(dNum);
			return str;
		}
	}	
	
	
	/**  财务格式  0.00 */
	private String updateMon(Object content) {
	    String str="";
	    
	    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
		
		str+="0.00";
		return str;
		
	    }
	    else{
		
		DecimalFormat df1 = new DecimalFormat("#,###.00"); 
		
		str+=df1.format(Double.parseDouble(content.toString()));
		return str;
	    }	
	}	
	
	@SuppressWarnings("unchecked")
	public void preNewLeaseBackContractPdf(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.getNewLeaseBackContractDateMap(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.getNewLeaseBackContractDateMap(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void getNewLeaseBackContractDateMap(Context context) {
		String[] con = null;
		ArrayList creditmapandleasehold = new ArrayList();
		con = (String[]) context.contextMap.get("credtdxx");
		for (int ii = 0; ii < con.length; ii++) {
			HashMap outputMap = new HashMap();
			List leaseholds = new ArrayList();
			context.contextMap.put("credit_id", con[ii]);
			try {

				context.contextMap.put("PRCD_ID",
						context.contextMap.get("credit_id"));
				List contractinfo = (List) DataAccessor.query(
						"exportContractPdf.judgeExitContract",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				if (contractinfo.size() == 0) {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				} else {
					leaseholds = (List) DataAccessor
							.query("exportContractPdf.queryEquipmentByRectIdForleaseholds",
									context.getContextMap(),
									DataAccessor.RS_TYPE.LIST);
				}

				outputMap.put("leaseholds", leaseholds);
				creditmapandleasehold.add(outputMap);

			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		this.expNewLeaseBackBuyPdfs(context, creditmapandleasehold);
	}

	/**
	 * 导出回租买卖合同
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void expNewLeaseBackBuyPdfs(Context context,
			ArrayList creditmapandleasehold) {

		ByteArrayOutputStream baos = null;
		ArrayList leaseholds = new ArrayList();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();
			for (int ii = 0; ii < creditmapandleasehold.size(); ii++) {
				HashMap outputMap = (HashMap) creditmapandleasehold.get(ii);
				leaseholds = (ArrayList) outputMap.get("leaseholds");
				HashMap baseinfo = (HashMap) leaseholds.get(0);
				String code = "0";
				String leasetopric="";
				if (baseinfo.get("LEASE_TOPRIC") == null) {
					leasetopric = "  ";
				} else {
					leasetopric = baseinfo.get("LEASE_TOPRIC").toString();
				}
				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}
				// 承租方地址
				String custaddress = "";
				if (baseinfo.get("CORP_REGISTE_ADDRESS") == null) {
					custaddress = "  ";
				} else {
					custaddress = baseinfo.get("CORP_REGISTE_ADDRESS")
							.toString();
				}
				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}

				// 租赁物设置场所

				String eAddress = "";
				if (baseinfo.get("EQUPMENT_ADDRESS") == null) {
					eAddress = "  ";
				} else {
					eAddress = baseinfo.get("EQUPMENT_ADDRESS").toString();
				}
				PdfPTable tT = new PdfPTable(10);

				Phrase phrase = new Phrase();
//				phrase.add(table);
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("        ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("买卖合同", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				// 表头的相关信息,第一行
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder("合同编号：" + Lcode,
						PdfPCell.ALIGN_RIGHT, FontDefault22, 7));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_RIGHT, FontDefault22, 1));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				i++;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;

				// 文字部分
				String[] createdate = new String[3];
				if (((HashMap) (leaseholds.get(0))).get("CREATE_DATE") != null) {
					createdate = ((HashMap) (leaseholds.get(0)))
							.get("CREATE_DATE").toString().substring(0, 10)
							.split("-");
					if (createdate[0].equals("1900")) {
						createdate[0] = "        ";
						createdate[1] = "        ";
						createdate[2] = "        ";
					}
				} else {
					createdate[0] = "        ";
					createdate[1] = "        ";
					createdate[2] = "        ";
				}


				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				phrase = new Phrase("立买卖合同双方：", FontDefault2);
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));
				tT.addCell(makeCellSetColspanNoBorder("",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				i++;
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("买受人（以下简称甲方）：", FontDefault2)); 
	    		phrase.add(new Phrase(Constants.COMPANY_NAME , FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("出卖人（以下简称乙方）：", FontDefault2)); 
	    		phrase.add(new Phrase(custname , FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("        兹经充分友好协商，甲、乙双方本着自愿、平等、诚实信用的原则，签订如下合同条", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("款，以资共同恪守履行。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第一条	 标的物名称、型号、规格及数量", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 

				// 第一个子表开始,第一行
				// 租赁物的数量是动态增加的
				tT.addCell(makeCellSetColspanNoBorder("      ",
						PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspan("名称",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT.addCell(makeCellSetColspan("厂牌",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT.addCell(makeCellSetColspan("型号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("机号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 1));
				tT.addCell(makeCellSetColspan("数量", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				tT.addCell(makeCellSetColspan("附注", PdfPCell.ALIGN_LEFT,
						FontDefault2, 1));
				tT.addCell(makeCellSetColspanNoBorder("", PdfPCell.ALIGN_LEFT,
						FontDefault,1));
				i++;
				String thing_kind = "";
				String name = "";
				String brand = "";
				List manufacturerList = new ArrayList();
				for (int k = 0; k < leaseholds.size(); k++) {

					String thingname = "";
					if (((HashMap) leaseholds.get(k)).get("THING_NAME") == null) {
						thingname = "";
					} else {
						thingname = ((HashMap) leaseholds.get(k)).get(
								"THING_NAME").toString();
					}
					String modelspec = "";
					if (((HashMap) leaseholds.get(k)).get("MODEL_SPEC") == null) {
						modelspec = "";
					} else {
						modelspec = ((HashMap) leaseholds.get(k)).get(
								"MODEL_SPEC").toString();
					}

					if (((HashMap) leaseholds.get(k)).get("BRAND") == null) {
						brand = "";
					} else {
						brand = ((HashMap) leaseholds.get(k)).get("BRAND")
								.toString();
					}
					// 厂牌
					if (((HashMap) leaseholds.get(k)).get("TYPE_NAME") == null) {
						thing_kind = "";
					} else {
						thing_kind = ((HashMap) leaseholds.get(k)).get(
								"TYPE_NAME").toString();
					}
					manufacturerList.add(thing_kind);
					if (((HashMap) leaseholds.get(k)).get("NAME") == null) {
						name = "";
					} else {
						name = ((HashMap) leaseholds.get(k)).get("NAME")
								.toString();
					}
					String unit = "";
					if (((HashMap) leaseholds.get(k)).get("UNIT") == null) {
						unit = "";
					} else {
						unit = ((HashMap) leaseholds.get(k)).get("UNIT")
								.toString();
					}
					String amount = "";
					if (((HashMap) leaseholds.get(k)).get("AMOUNT") == null) {
						amount = "";
					} else {
						amount = ((HashMap) leaseholds.get(k)).get("AMOUNT")
								.toString();
					}
					tT.addCell(makeCellSetColspanNoBorder("      ",
							PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT.addCell(makeCellSetColspan(
							thingname, PdfPCell.ALIGN_LEFT, FontDefault222,
							2));
					tT.addCell(makeCellSetColspan(
							thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,
							2));
					tT.addCell(makeCellSetColspan(modelspec,
							PdfPCell.ALIGN_LEFT, FontDefault222, 1));
					tT.addCell(makeCellSetColspan(name,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan(amount + unit,
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspan("  ",
							PdfPCell.ALIGN_LEFT, FontDefault2, 1));
					tT.addCell(makeCellSetColspanNoBorder("",
							PdfPCell.ALIGN_LEFT, FontDefault,1));
					i++;
				}
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第二条	标的物总价款及付款方式", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	标的物总价款：人民币设备金额 ", FontDefault2)); 
	    		phrase.add(new Phrase(SimpleMoneyFormat.getInstance().format(new Double(leasetopric)) , FontUnde2)); 
	    		phrase.add(new Phrase("（RMB￥  ", FontDefault2));
	    		phrase.add(new Phrase(nfFSNum.format(new Double(leasetopric)), FontUnde2));
	    		phrase.add(new Phrase("）", FontDefault2));
	    		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	甲、乙双方签订本合同，是基于乙方将目标物出售予甲方后，再由甲方将目标物出租予", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("乙方，据此甲方得以目标物总价款其中一部分，作为双方就标的物所签订融资租赁合同的保", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("证金。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("3.	乙方将标的物和第四条规定之单据交付甲方之日起 七 日内，甲方支付合同价款予乙方", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("或乙方指定之第三人。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第三条	 标的物所有权及交付地点", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	自本合同生效日为：_______年_____月_____日。并自本合同生效之日起，该标的物", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("之所有权转移予甲方。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	标的物需要拆离、搬动、装卸、运输、安装、调试和测试的，由乙方负责并承担相关", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("费用。乙方从事上述行为时，应尽善良占有人之注意义务，不得毁损标的物。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	  
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("3.	交付地点为 ", FontDefault2));  
	    		phrase.add(new Phrase( eAddress , FontUnde2)); 
	    		phrase.add(new Phrase("，", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("甲方另有指示的，按照指示的地点交货。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第四条	标的物单据的转移", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       乙方应在本合同签订之日向甲方提供该标的物的包括但不限于购买发票、购买合同、购", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("买时的检验凭证、维修凭证、产品说明等相关单据和资料。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第五条	质量瑕疵担保责任", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(一)	该标的物须符合以下质量要求：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    1.具备产品应当具备的使用性能，但是，合同签订前对产品存在使用性能的瑕疵作出书面", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    说明的除外；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    2.符合在产品或者其包装上注明采用的产品标准，符合以产品说明、实物样品等方式表明", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    的质量状况。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    3.不存在危及人身、财产安全的不合理的危险，有保障人体健康和人身、财产安全的国家", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("        标准、行业标准的，应当符合该标准；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(二)	乙方对于该标的物负有质量瑕疵担保责任，该标的物若有如下情况之一，乙方在签订", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 

	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     本合同前，须明确告知甲方。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     1.	不具备产品应当具备的使用性能的；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     2.	不符合在产品或者其包装上注明采用的产品标准的；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     3.	不符合以产品说明、实物样品等方式表明的质量状况的。该标的物有质量瑕疵或由于", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         设计、制造不良而减损通常效用或预定效力或故障。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     乙方未履行告知义务的，甲方可以解除合同；标的物上存在其它重大、显著、根本性瑕", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("  疵，以致实际上剥夺了买受人根据合同有权期待获得的标的物的价值或效用的，甲方可拒", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("  绝接受标的物或解除合同。因此给甲方造成损失的，乙方应当予以赔偿。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第六条	权利瑕疵担保责任", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(一)	乙方对于该标的物负有权利瑕疵担保责任，乙方保证：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 	
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     1.	标的物上不存在第三人的任何权利，包括但不限于所有权、抵押权、质押权、留", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     置权、优先权、租赁权等权利；不会有任何第三人向甲方提出类似权利主张；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     2.	标的物的生产、销售、使用不会侵害任何第三人的知识产权；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     3.	标的物上无其它负担或处分之存在；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("     4.	标的物上不涉及其它债务关系。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("(二)	乙方若违反权利瑕疵担保责任，甲方可解除合同，并要求其支付违约金、损害赔偿", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    金。违约金按照总价款的  5 %计算。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第七条	租赁物瑕疵及修缮", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       基于目标物原为乙方自行购买取得所有权，甲方仅依双方签订合同号为：", FontDefault2)); 
	    		phrase.add(new Phrase( Lcode, FontUnde2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("的融资租赁合同向乙方购买后再出租给乙方使用，乙方同意目标物于本合同 ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("成立后至租赁期间届满前，就本合同成立前、后，如有质量瑕疵或设计制造不良，而有减", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("少一般效用或故障、毁损时，愿自行承担责任及修缮费用。  ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第八条	乙方内部程序", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       乙方出卖的标的物如系乙方全部或主要部分之财产时，已依《公司法》或其公司章程 ", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("       第__条之规定，取得股东会或董事会或全体股东出售标的物之同意。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第九条	保险", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	若乙方在签订本合同前，已就该标的物办理相关保险的，在签订本合同之日起三日", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   内，乙方应协助甲方办理保险受益人变更手续。就该标的物投保的险种至少包括火险、", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   盗窃险等险种，未投保的险种由乙方补办。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	若乙方在签订本合同时，未就该标的物办理相关保险，在签订本合同之日起三日内，", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("   乙方须协助甲方办理相关保险。投保险种同前款，保险费由乙方支付。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十条	标的物的毁损、灭失", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("1.	标的物在交付前或第三条第1款规定的占有保管期间内发生毁损、灭失的风险由乙方", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("承担。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("2.	标的物在前述规定期限内发生毁损或灭失的，乙方应立即通知甲方，甲方有权选择下列", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("任一方式进行处理。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ①乙方将租赁机械恢复原状或修理至完全能正常使用的状态，并承担相关费用。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ②乙方更换与租赁机械同等型号、性能的部件或配件使其能正常使用，并承担相关费用。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    ③当租赁机械毁损至无法修理的程度或灭失时，本合同失效，乙方返还甲方支付的全部价", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    款。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十一条	争议解决", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    凡因履行本合同所发生的或与本合同有关的一切争议，甲、乙双方应通过友好协商解决；", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("如果协商不能解决，则向甲方所在地有管辖权的法院诉讼解决。甲方为实现本合同项下债", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("权所需费用（包括但不限于催收费用、诉讼费、保全费、公告费、执行费、律师费、差旅费", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("及其它费用）由乙方承担。争议期间，各方仍应继续履行未涉争议的条款。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("         第十二条	其它约定", FontColumn2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("    未尽事宜，双方另行协商解决，本合同壹式贰份，甲乙双方各执壹份。", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++;
	    		
	    		
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("甲      方：", FontDefault2)); 
	    		phrase.add(new Phrase(Constants.COMPANY_NAME , FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("法定代表人："+Constants.LEGAL_PERSON, FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("委托代理人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("日      期：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("乙      方：", FontDefault2)); 
	    		phrase.add(new Phrase(custname , FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));	
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("法定代表人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("委托代理人：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("日      期：", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 8));		
	    		tT.addCell(makeCellSetColspanNoBorder("  ",PdfPCell.ALIGN_LEFT, FontDefault,1));
	    		i++; 

				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "equipmentsforshow.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			// add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出 买卖合同", "合同浏览导出 买卖合同", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	/*
	 * 导出交机承诺书
	 */
	@SuppressWarnings("unchecked")
	public void expMarkMachineCommitLetter(Context context) throws SQLException {

		ByteArrayOutputStream baos = null;
		List leaseholds = new ArrayList();
		//直租 添加公司别判断
		String creditId = (String) context.contextMap.get("credit_id");
		String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
		int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
		String companyName = Constants.COMPANY_NAME;
		if("7".equals(contractType)){
			companyName =  LeaseUtil.getCompanyNameByCompanyCode(companyCode);
		}
		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距

			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// 打开文档
			document.open();
			leaseholds = (List) DataAccessor
					.query("exportContractPdf.queryLeaseHoldFromCreditByCreditId",
							context.getContextMap(),
							DataAccessor.RS_TYPE.LIST);
			Map baseinfo = (Map) DataAccessor
						.query("exportContractPdf.queryAdvanceMachineDetail",
								context.getContextMap(),
								DataAccessor.RS_TYPE.MAP);
				String appMoney="";
				if (baseinfo.get("APPRORIATEMON") == null) {
					appMoney = "  ";
				} else {
					appMoney = baseinfo.get("APPRORIATEMON").toString();
				}
				// 承租方名称
				String custname = "";
				if (baseinfo.get("CUST_NAME") == null) {
					custname = "  ";
				} else {
					custname = baseinfo.get("CUST_NAME").toString();
				}

				String Lcode = "";
				if (baseinfo.get("LEASE_CODE") == null) {
					Lcode = "无编号";
				} else {
					Lcode = baseinfo.get("LEASE_CODE").toString();
				}
				
				//拨款供应商
				String brand = "";
				if (baseinfo.get("APPRORIATENAME") == null) {
					brand = "   ";
				} else {
					brand = baseinfo.get("APPRORIATENAME").toString();
				}
				
				PdfPTable tT = new PdfPTable(10);
				Phrase phrase = new Phrase();
				HeaderFooter hf = new HeaderFooter(phrase,false);
				hf.setBorder(0);
				document.setHeader(hf);	
				
				//页眉结束
				tT.setWidthPercentage(100f);
				int i = 0;

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 10));
				i++;
				tT.addCell(makeCellSetColspanNoBorder("交机承诺书", PdfPCell.ALIGN_CENTER,
						fa, 10));
				i++;
				
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));
				i++;
				
	    		phrase = new Phrase("", FontDefault2); 	    		
	    		phrase.add(new Phrase("    兹本司", FontDefault2)); 
	    		phrase.add(new Phrase(brand, FontUnde2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("为"+companyName+"与", FontDefault2)); 
	    		phrase.add(new Phrase(custname, FontUnde2));
	    		phrase.add(new Phrase("间融资租赁合同(合同编号："+Lcode+")", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("机器设备之供应商，针对本项融资租赁之标的物", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 

				// 第一个子表开始,第一行
				tT.addCell(makeCellSetColspan("厂牌",
						PdfPCell.ALIGN_LEFT, FontDefault2, 4));
				tT.addCell(makeCellSetColspan("品名",
						PdfPCell.ALIGN_LEFT, FontDefault2, 3));
				tT.addCell(makeCellSetColspan("型号",
						PdfPCell.ALIGN_LEFT, FontDefault2, 3));

				i++;
				String thing_kind = "";
				String thingname = "";
				String modelspec = "";
				for (int k = 0; k < leaseholds.size(); k++) {
					
					if (((HashMap) leaseholds.get(k)).get("THING_NAME") == null) {
						thingname = "";
					} else {
						thingname = ((HashMap) leaseholds.get(k)).get(
								"THING_NAME").toString();
					}
					
					if (((HashMap) leaseholds.get(k)).get("MODEL_SPEC") == null) {
						modelspec = "";
					} else {
						modelspec = ((HashMap) leaseholds.get(k)).get(
								"MODEL_SPEC").toString();
					}
					// 厂牌
					if (((HashMap) leaseholds.get(k)).get("TYPE_NAME") == null) {
						thing_kind = "";
					} else {
						thing_kind = ((HashMap) leaseholds.get(k)).get(
								"TYPE_NAME").toString();
					}
					tT.addCell(makeCellSetColspan(
							thing_kind, PdfPCell.ALIGN_LEFT, FontDefault222,
							4));
					tT.addCell(makeCellSetColspan(
							thingname, PdfPCell.ALIGN_LEFT, FontDefault222,
							3));
					tT.addCell(makeCellSetColspan(modelspec,
							PdfPCell.ALIGN_LEFT, FontDefault222, 3));
					i++;
				}

				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("本公司保证于收到"+companyName+"拨款", FontDefault2)); 
	    		phrase.add(new Phrase("RMB￥"+appMoney, FontUnde2));
	    		phrase.add(new Phrase("元后，______天内将机器交付到上述融资租赁", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 	
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("合同或"+companyName+"指定应交付的租赁物放置址处，若交机产生争议，依上述融资租赁合同", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("及上述设备购销合同处理。", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("此致", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 

	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++;
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase(companyName, FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("立书人：", FontDefault2)); 
	    		phrase.add(new Phrase(brand, FontUnde2));
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("法定代表人/授权代表人:", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		phrase = new Phrase("", FontDefault2);  
	    		phrase.add(new Phrase("日期：                  年          月         日", FontDefault2)); 
				tT.addCell(makeCellSetColspanNoBorder(phrase,
						PdfPCell.ALIGN_LEFT, FontDefault2, 10));		
	    		i++; 
	    		
				tT.addCell(makeCellSetColspanNoBorder("    ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 10));	
				i++; 
				
				document.add(tT);
				document.add(Chunk.NEXTPAGE);
			document.close();
			// 支付表PDF名字的定义
			String strFileName = "MettingResolution.pdf";
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition",
					"attachment; filename=" + strFileName);

			ServletOutputStream o = context.response.getOutputStream();

			baos.writeTo(o);
			o.flush();
			closeStream(o);

			BusinessLog.addBusinessLogWithIp(DataUtil
					.longUtil(context.contextMap.get("credit_id")), null,
					"导出交机承诺书", "合同浏览导出 导出交机承诺书", null,
					context.contextMap.get("s_employeeName") + "("
							+ context.contextMap.get("s_employeeId")
							+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
							.longUtil(context.contextMap.get("s_employeeId")
									.toString()), DataUtil.longUtil(0), context
							.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 流关闭操作
	 * 
	 * @param content
	 * @param align
	 * @param FontDefault
	 * @return
	 */
	private void closeStream(OutputStream o) {
		try {

			o.close();

		} catch (IOException e) {

			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);

		} finally {

			try {

				o.close();

			} catch (IOException e) {

				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

	}

	/**
	 * 创建 有边框 合并 单元格|-| 无下边用于表格的顶
	 * 
	 * */
	private PdfPCell makeCellSetColspan3(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0);

		return objCell;
	}

	/**
	 * 创建 有边框 合并 单元格|- 无下边用于表格的顶
	 * 
	 * */
	private PdfPCell makeCellSetColspan2NoBottomAndRight(String content,
			int align, Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthRight(0);
		return objCell;
	}

	/**
	 * 创建 有边框 合并 单元格|_| 无上边
	 * 
	 * */
	private PdfPCell makeCellSetColspan3NoTop(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthTop(0);

		return objCell;
	}

	/**
	 * 创建 下边框 单元格_ 无上边
	 * 
	 * */
	private PdfPCell makeCellSetColspan4NoTop(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthLeft(0);
		objCell.setBorderWidthRight(0);
		return objCell;
	}

	/**
	 * 创建 有边框 合并 单元格| | 无上下边
	 * 
	 * */
	private PdfPCell makeCellSetColspan2(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		return objCell;
	}

	/**
	 * 创建 无边框 合并 单元格
	 * 
	 */
	private PdfPCell makeCellSetColspanNoBorder(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthLeft(0);
		objCell.setBorderWidthRight(0);
		objCell.setPaddingTop(5);
		objCell.setPaddingBottom(5);
		return objCell;
	}

	/**
	 * 创建 无边框 合并 单元格
	 * 
	 */
	private PdfPCell makeCellSetColspanNoBorder(Phrase phrase, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = phrase;
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthLeft(0);
		objCell.setBorderWidthRight(0);
		objCell.setPaddingTop(7);
		objCell.setPaddingBottom(7);
		return objCell;
	}

	private PdfPCell makeCellSetColspan3NoLeft(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthLeft(0);
		return objCell;
	}

	/*
	 * 创建 有边框 四边都有 合并 单元格|-_|
	 */
	private PdfPCell makeCellSetColspan(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);

		return objCell;
	}

	/** 创建 只有左边框 单元格 */
	private PdfPCell makeCellWithBorderLeft(String content, int align,
			Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);

		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthRight(0);
		return objCell;
	}

	/** 创建 只有右边框 单元格 */
	private PdfPCell makeCellWithBorderRight(String content, int align,
			Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);

		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthLeft(0);
		return objCell;
	}
	
    private PdfPCell makeCellSetColspan2222(String content, int align,
    	    Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setPaddingLeft(50);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthTop(0);
    	return objCell;
        } 
    
    /** 创建 只有左边框 单元格 */
	private PdfPCell makeCellSetColspanNull(String content, int align,Font FontDefault, int colspan,float T,float B,float L,float R) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthBottom(B);
		objCell.setBorderWidthLeft(L);
		objCell.setBorderWidthRight(R);
		objCell.setBorderWidthTop(T);
		 
		return objCell;
	}
	
    private PdfPCell makeCellSetColspan2ForOne(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setPaddingLeft(35);
    	objCell.setPaddingRight(35);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthTop(0);
    	return objCell;
        }	
    
	private PdfPCell makeCellWithBorderRightForOne(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase); 
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setPaddingLeft(15);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
	    return objCell;
	} 
	
	private PdfPCell makeCellWithBorderLeftForOne(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase); 
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setPaddingLeft(35);
	    objCell.setFixedHeight(15);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
	    return objCell;
	}
	
    /** 创建 有边框 合并 单元格
     *  无上边
     *  
     *  */
    private PdfPCell makeCellSetColspan4(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	 
	return objCell;
    }
    
    private PdfPCell makeCell3(String content, int align, Font FontDefault) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setFixedHeight(20f);
	objCell.setHorizontalAlignment(align);
	
	return objCell;
    }
    
    /** 创建 无边框 单元格 */
    private PdfPCell makeCellWithNoBorder(String content, int align,
	    Font FontDefault) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setFixedHeight(17f);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setBorder(0);
	return objCell;
    }
    
	/** 创建 无边框 合并 单元格 */
	private static PdfPCell makeCellSetColspanWithNoBorder(String content,
			int align, Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
		return objCell;
	}
	
	/** 创建无边框 合并 单元格 */
	private static PdfPCell makeCellNoBorderSetColspan(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
	    return objCell;
	}
	
    private PdfPCell makeCellSetColspan2LeftAndTop(String content, int align,
    	    Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setPaddingTop(5) ;
    	objCell.setPaddingBottom(5) ;
    	return objCell;
       }
    
    /** 创建 有边框 合并 单元格|-_
     *  无左边
     *  
     *  */
    private PdfPCell makeCellSetColspan3NoRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthRight(0);
	 
	return objCell;
    }
    
    /** 创建 有边框 合并 单元格 |
     *  只有左边框
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }
    
	/** 创建 没有右侧边框 单元格 */
	private PdfPCell makeCellWithNoBorderRight(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	 
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    //objCell.setBorderWidthBottom(0);
	    //objCell.setBorderWidthTop(0);
	    //objCell.setBorderWidthLeft(0);
	    objCell.setBorderWidthRight(0);
	    return objCell;
	}
	
	/** 创建 没有右侧边框 单元格 */
	private PdfPCell makeCellWithNoBorderLeft(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	 
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    //objCell.setBorderWidthBottom(0);
	    //objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
	    //objCell.setBorderWidthRight(0);
	    return objCell;
	}
	
    private PdfPCell makeCellSetColspan2ForZLWHead(String content, int align, Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthLeft(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setBorderWidthTop(1);
    	objCell.setFixedHeight(16);
    	return objCell;
        }
    
	private PdfPCell makeCellS(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthTop(1f);
	    return objCell;
	}
	
	private PdfPCell makeCellSGAI(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setBorderWidthLeft(0f);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthTop(1f);
	    return objCell;
	}
	
	/** 创建 只有右边框 单元格 没有限制高度的特别版 */
	private PdfPCell makeCellWithBorderRightP(String content, int align, Font FontDefault,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
//	    objCell.setFixedHeight(20*auto);
	    objCell.setBorderWidthLeft(0);
	    return objCell;
	}
	
	/** 创建 没有顶边 单元格 */
	private PdfPCell makeCellOnlyBottom(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 没有顶边 (左边)单元格 */
	private PdfPCell makeCellLeftBottom(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
	    return objCell;
	}
	/** 创建 没有顶边 (右边)单元格 */
	private PdfPCell makeCellRightBottom(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
	    return objCell;
	}
	
	/** 创建 只有左右边框 单元格 */
	private PdfPCell makeCellWithBorder(String content, int align, Font FontDefault,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 无上下边框 合并 单元格 */
	private PdfPCell makeCellSetColspanLRBorderAuto(String content, int align, Font FontDefault,int colspan,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setFixedHeight(20*auto);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 无上边框 合并 单元格 */
	private PdfPCell makeCellSetColspanNoBorderTOP(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setFixedHeight(20);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 无上边框 合并 单元格 */
	private PdfPCell makeCellSetColspanNoBorderTOPAuto(String content, int align, Font FontDefault,int colspan,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
		objCell.setFixedHeight(20*auto);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}	
	
	/** 创建 无上下边框 合并 单元格 */
	private PdfPCell makeCellSetColspanLRBorder(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setFixedHeight(20);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}	
	
	/** 创建 无上边框 合并 单元格 */
	private PdfPCell makeCellSetColspanNoButtomBorderTOPAuto(String content, int align, Font FontDefault,int colspan,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthLeft(1);
		objCell.setFixedHeight(20*auto);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}	
	
	/** 创建 有边框 合并 单元格 边框加粗版 */
	private PdfPCell makeCellCOS(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthTop(1f);
	    objCell.setFixedHeight(20);
	    return objCell;
	}
	//add zhangbo0724 
	private PdfPTable getTable(Map contract,int t){
		
		PdfPTable tT = new PdfPTable(2);
		try {
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
			Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
			Font fa = new Font(bfChinese, 22, Font.BOLD);
			Font FontColumn2 = new Font(bfChinese, 11, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			String Lcode = contract.get("LEASE_CODE") + "";
			Lcode = Lcode.trim();
			if (Lcode.equals("")) {
					Lcode = "           ";
				}
			tT.setWidthPercentage(100f);
			tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("融资租赁合同", PdfPCell.ALIGN_CENTER, fa, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			tT.addCell(makeCellWithBorderLeft(" ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellWithBorderRight("合同编号:    " + Lcode, PdfPCell.ALIGN_CENTER, FontDefault));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2ForOne( "          合同签订日:    20____年____月____日", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2ForOne( "          合同签订地:    中华人民共和国", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellWithBorderLeftForOne( "            出租方(甲方):    " + Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):    " + contract.get("CORP_NAME_CN") + "", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            法定代表或负责人:    "+Constants.LEGAL_PERSON, PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne("法定代表或负责人:    " + contract.get("LEGAL_PERSON") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            注册地址:    苏州工业园区东富路8号", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne( "注册地址:    " + contract.get("REGISTERED_OFFICE_ADDRESS") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            实际经营地:    ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne("实际经营地:    ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            "+Constants.COMPANY_COMMON_ADDRESS, PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne( "" + contract.get("COMMON_OFFICE_ADDRESS") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            邮政编码:    215022 ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne( "邮政编码:    " + contract.get("POSTCODE") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            电话号码:    0512-80983566 ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne( "电话号码:    " + contract.get("TELEPHONE") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderLeftForOne( "            传真号码:    0512-80983567 ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellWithBorderRightForOne( "传真号码:    " + contract.get("FAX") + " ", PdfPCell.ALIGN_LEFT, FontDefault22));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));

			Paragraph mm11 = getParagraphSmall(contract,FontDefault,FontUnder);
			
			tT.addCell(getCell(mm11,FontDefault));
			
			Paragraph mm12 = getParagraph(contract,FontDefault,FontUnder);
			
			tT.addCell(getCell(mm12,FontDefault));
			
			tT.addCell(makeCellSetColspan2ForOne( "            方”)双方就甲方出租本合同规定的合同正本及合同附件中记载的设备(以下简称租赁物)，乙", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2ForOne( "            方从甲方处承租租赁物事宜，在平等互惠的基础上经友好协商达成以下协议并签订本合同(本", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2ForOne( "            合同分为合同正本与合同附件，合同附件经甲、乙双方及卖方签订确认后与合同正本有同等", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2ForOne("            的法律效力)。", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellWithBorderLeftForOne("          出租方(甲方): ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):  ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspan2( "                         " + Constants.COMPANY_NAME + "                                                                "+ contract.get("CORP_NAME_CN") + "", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellWithBorderLeftForOne( "          法定代表人或授权人:  ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellWithBorderRightForOne("法定代表人或授权人:  ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellWithBorderLeftForOne("          日期: ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellWithBorderRightForOne("日期: ", PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			t = t + 54;
			for (;t < 59; t++) {
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			}
			tT.addCell(makeCellSetColspan2("1", PdfPCell.ALIGN_CENTER, FontDefault, 2));
			t += 1;
			if (t == 60) {
				tT.addCell(makeCellSetColspan4("全国服务专线：400-928-1999",PdfPCell.ALIGN_RIGHT,FontDefault, 2));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tT;
	}
	//段落
	private Paragraph getParagraph(Map contract,Font FontDefault,Font FontUnder){
		Paragraph mm12 = new Paragraph();
		mm12.setFont(FontDefault);
		mm12.add("           (以下简称“甲方”)和承租方");
		String neme1 = contract.get("CORP_NAME_CN") + "";
		neme1 = neme1.trim();
		if (neme1.equals("")) {
			neme1 = "____________________________________";
			mm12.add(neme1);
		} else {
			int le = neme1.length();
			String px = "                         ";
			if (le < 19) {
				String pp = px.substring(0, Math.round(21 - le));
				neme1 = pp + pp + neme1 + pp + pp;
				}
			Chunk c461 = new Chunk(neme1, FontUnder);
			mm12.add(c461);
		}
		mm12.add("(以下简称“乙");
		
		return mm12;
	}
	//段落
	private Paragraph getParagraphSmall(Map contract,Font FontDefault,Font FontUnder){
		Paragraph mm11 = new Paragraph();
		mm11.setFont(FontDefault);
		mm11.add("                      本合同的租赁是指中国合同法规定的融资租赁形式。出租方 ");
		Chunk c361 = new Chunk(Constants.COMPANY_NAME, FontUnder);
		mm11.add(c361);
		return mm11;
	}
	//表格字体样式
	private PdfPCell getCell(Paragraph content,Font FontDefault) {
		PdfPCell objCell = new PdfPCell(content);
		objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setColspan(2);
		objCell.setPaddingLeft(35);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	 //Add by xuwei 2014 5-39导出售后回租付款指示书
    public void exportPayMoneyNotice(Context context){
		try{
			Map rentandpuctcontract = new HashMap();
			//is=0 没有生成合同 否则已经生成合同
			
			int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(is==0){
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT2", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}else{
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT1", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}
			String bankAccount=(String)context.contextMap.get("bankAccount"+context.contextMap.get("PRCD_ID"));
			rentandpuctcontract.put("bankAccount", bankAccount);
			
			ByteArrayOutputStream baos = null;
		 	//Map rentandpuctcontract = new HashMap();
			try {   
			        // 字体设置
			        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
			        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
			        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
			        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
			        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
			        Font fa = new Font(bfChinese, 22, Font.BOLD);
			        // 数字格式
			        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
			        nfFSNum.setGroupingUsed(true);
			        nfFSNum.setMaximumFractionDigits(2);
			        // 页面设置
			        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
			        
			        
			        baos = new ByteArrayOutputStream();
			        PdfWriter.getInstance(document, baos);
			        	        
			        // 打开文档
			        document.open();

				    String code="";
				    String[] rentdate =new String[3];
				    rentdate[0]="";
		    		rentdate[1]="";
		    		rentdate[2]="";
				  //租赁合同编号
			        String rentcode = "";
			      //购销合同编号
		    		String puctcode = "";
		    		//承租方名称
		     		String custname="";
		     		//供应商
		     		String sellername="";
		     		//供应商
		     		String money = "";
		     		String bankaccount="";
		     		String openbank="";
		     		 int type = 1;//判断是选择的供应商还是客户的银行账户
				    if(rentandpuctcontract!=null){
				    	if(rentandpuctcontract.get("CONTRACT_TYPE")!=null){
				    		code=rentandpuctcontract.get("CONTRACT_TYPE").toString();
				    	}
				    	if(rentandpuctcontract.get("LESSOR_TIME")!=null){
				    		String[] temp = rentandpuctcontract.get("LESSOR_TIME").toString().substring(0,10).split("-");
				    		if(!temp[0].equals("1900")){
				    			rentdate = temp ;
				    		}
				    	}
				   
				    	String [] bankInfo=((String)rentandpuctcontract.get("bankAccount")).split("=");
				    	
				       
			    		if(rentandpuctcontract.get("LEASE_CODE")==null){
			    			rentcode = "  ";
				    	}else{
				    		rentcode = rentandpuctcontract.get("LEASE_CODE").toString();
				    	}
			    		
			     		if(rentandpuctcontract.get("PUCT_CODE")==null){
			     			puctcode = "  ";
			 	    	}else{
			 	    		puctcode = rentandpuctcontract.get("PUCT_CODE").toString();
			 	    	}
			     		
			     		if(rentandpuctcontract.get("CUST_NAME")==null){
			     			custname = "  ";
			 	    	}else{
			 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
			 	    	}
			     		
			    		if(rentandpuctcontract.get("CUST_NAME")==null){
			     			custname = "  ";
			 	    	}else{
			 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
			 	    	}
			     		
			     		if(rentandpuctcontract.get("NAME")==null){
			     			sellername = "  ";
			 	    	}else{
			 	    		sellername=bankInfo[0].toString();
			 	    	}
			     		if(rentandpuctcontract.get("MONEY")==null){
			     			money = "  ";
			     		}else{
			     			System.out.println(bankInfo[3]);
			     			money=nfFSNum.format(DataUtil.doubleUtil(bankInfo[3]));
			     		}
			     		if(rentandpuctcontract.get("BANK_ACCOUNT")!=null){
			     			bankaccount = bankInfo[1].toString();;
			     		}
			     		if(rentandpuctcontract.get("OPEN_ACCOUNT_BANK")!=null){
			     			openbank = bankInfo[2].toString();;
			     		}
			     		if(bankInfo.length>=5){
			     			type = Integer.parseInt(bankInfo[4]);
			     		}
			     		
				    }
					PdfPTable tT = new PdfPTable(new float[]{10f,20f,20f,20f,20f,20f,10f});
					tT.setWidthPercentage(100f);

					
						int i=0;

						tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
						i++;
						tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
						i++;
						tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
						i++;
						tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
						i++;
						tT.addCell(makeCellSetColspan2("关于设备价款的支付指示",PdfPCell.ALIGN_CENTER, fa,7));
						i++;
						tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
						i++;
			    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
			    		i++;
			    		//表头的相关信息,第一行
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tT.addCell(makeCellSetColspanNoBorder("致："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
			    		i++;
			    		
			    		//文字部分
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日 贵公司与我们签署的融资租赁合同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("[合同号："+rentcode+"  ] 和买卖合同  [合同号："+rentcode+"  ]，请贵公司代替", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("我方将货款分别支付至以下帐户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		if(type == 0){
			    			tT.addCell(makeCellSetColspanNoBorder("户名："+LeaseUtil.getSuplNameByCreditId((String)context.contextMap.get("PRCD_ID")), PdfPCell.ALIGN_LEFT, FontDefault2,5));
			    		}else{
			    			tT.addCell(makeCellSetColspanNoBorder("户名："+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));	
			    		}
							
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("金额：   " + money+"元" , PdfPCell.ALIGN_LEFT, FontDefault2,5));
//						tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
			    		i++;

			    	
			    	
			    	
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("共计支付:" +  money+"元" , PdfPCell.ALIGN_LEFT, FontDefault2,5));
//						tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("承租方： "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		for(;i<51;i++){
			    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
			    		}
			    		if(i<=51){
			    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
			    		}				
					
					document.add(tT);
					document.add(Chunk.NEXTPAGE);
			     //  }
					document.close();
			    // 支付表PDF名字的定义
			    String strFileName =  "AboutEquipMoneyPay.pdf";
			    context.response.setContentType("application/pdf");
			    context.response.setCharacterEncoding("UTF-8");
			    context.response.setHeader("Pragma", "public");
			    context.response.setHeader("Cache-Control",
				    "must-revalidate, post-check=0, pre-check=0");
			    context.response.setDateHeader("Expires", 0);
			    context.response.setHeader("Content-Disposition",
				    "attachment; filename=" + strFileName);

			    ServletOutputStream o = context.response.getOutputStream();

			    baos.writeTo(o);
			    o.flush();
				closeStream(o);


			    
			} catch (Exception e) {
			    e.printStackTrace();
			    LogPrint.getLogStackTrace(e, logger);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
}