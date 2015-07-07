package com.brick.rentReceipt;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code39Encoder;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.encode.InvalidAtributeException;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;

import com.brick.common.command.OneBarcodeCommand;
import com.brick.credit.service.ExportQuoToPdf;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.CurrencyConverter;
import com.brick.util.DateUtil;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
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

public class RentReceiptToPdf extends AService{
	
	Log logger = LogFactory.getLog(RentReceiptToPdf.class);
	/**
	 * 打印pdf文件——本金收据列印
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void receiptToPdf(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;
		List receiptlists = new ArrayList();
		List<Map> receiptlist = null;
		SqlMapClient sqlMapper = DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),
					"ids", "");
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("PRINCIPALRUNCODE", ids[i]);
				// 查出数据@合同的id
				receiptlist = (List<Map>) DataAccessor.query(
						"rentReceipt.queryRentReceiptByRecpId",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				receiptlists.add(receiptlist);
			}
			if (receiptlists.size() > 0) {
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4).rotate(); // 定义A5页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				document.open();
				// 生成支付表PDF名字
				for (Object object : receiptlists) {
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light",
							"UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 20, Font.BOLD);
					Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
					Font fa = new Font(bfChinese, 30, Font.BOLD);
					Font faf = new Font(bfChinese, 20, Font.BOLD);
					Font faNum = new Font(bfChinese, 18, Font.BOLD);
					// 支付表PDF名字的定义
					@SuppressWarnings("unused")
					String strFileName = "";
					strFileName = "receiptList.pdf";
					// 表格列宽定义
					float[] widthsSt2 = { 0.2f, 0.75f };
					float[] widthsSt4 = { 0.2f, 0.25f, 0.25f, 0.25f };
					float[] widthsSt3 = { 0.2f, 0.5f, 0.25f };
					/* int iCnt = 0; */
					float[] widthsPPCa = { 2f };
					List<Map> receipt = (List<Map>) object;
					Object CREATEDATE = receipt.get(0).get("CREATEDATE");// 创建日期
					String CTEATEDATESTRING = null;
					SimpleDateFormat sf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
					if (CREATEDATE != null && !"".equals(CREATEDATE)) {
						CTEATEDATESTRING = sf.format(CREATEDATE);
					} else {
						CTEATEDATESTRING = "  年   月   日";
					}
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					// LOGO
					Font FontSmall = new Font(bfChinese, 15, Font.NORMAL);
					Font FontSmall2 = new Font(bfChinese, 15, Font.NORMAL);
					PdfPCell cell = null;
					PdfPTable t1 = new PdfPTable(12);
					PdfPTable tlogo = new PdfPTable(new float[] { 5f,30f,50f,40f});
					String imageUrl = ExportQuoToPdf.class.getResource("/").toString();//
					Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length() - 16) + "images/yrlogop.png");
					image.scaleAbsoluteHeight(25);
					image.scaleAbsoluteWidth(25);
					cell = new PdfPCell();//1
					cell.addElement(image);
					cell.setBorder(0);
					tlogo.addCell(cell);
					cell.addElement(image);
					//2
					Chunk chunk1 = new Chunk(Constants.COMPANY_NAME, FontSmall);
					Chunk chunk2 = new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
					cell = new PdfPCell();
					cell.addElement(chunk1);
					cell.addElement(chunk2);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//3
					cell = new PdfPCell();
					Chunk chunkNull = new Chunk("", FontSmall);
					cell.addElement(chunkNull);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//4
					cell = new PdfPCell();
					String oneCodePath=getOneBarcodeUtil( receipt.get(0).get("RECP_CODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString()) ;
					Image OneCodeImage = Image.getInstance(oneCodePath);
					OneCodeImage.scaleAbsoluteHeight(50);
					OneCodeImage.scaleAbsoluteWidth(200);
					cell.addElement(OneCodeImage);
					cell.setBorder(0);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setRight(PdfPCell.RIGHT);
					tlogo.addCell(cell);
					
					cell = new PdfPCell(tlogo);
					cell.setColspan(12);
					cell.setPaddingBottom(5);
					cell.setBorder(0);
					t1.addCell(cell);
					document.add(t1);
					//增加条形码
					/*String oneCodePath=getOneBarcodeUtil( receipt.get(0).get("LEASECODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString()) ;
					Image OneCodeImage = Image.getInstance(oneCodePath);
					PdfPTable tCode = new PdfPTable(new float[]{0.1f});
					tCode.setWidthPercentage(18f);
					cell = new PdfPCell();
					tCode.addCell(OneCodeImage);
					document.add(tCode);*/
					// 标题
					PdfPTable tT = new PdfPTable(widthsPPCa);
					tT.setWidthPercentage(100f);
					tT.addCell(makeCellWithNoBorder("裕  融  租  赁  有  限  公  司",PdfPCell.ALIGN_CENTER, fa));
					document.add(tT);
					document.add(new Paragraph("\n"));
					// 副标题
					PdfPTable tTf = new PdfPTable(widthsPPCa);
					tTf.setWidthPercentage(100f);
					tTf.addCell(makeCellWithNoBorder("本 金 收 据",PdfPCell.ALIGN_CENTER, faf));
					document.add(tTf);
					//
					PdfPTable tTNum = new PdfPTable(widthsPPCa);
					tTNum.setWidthPercentage(80f);
					tTNum.addCell(makeCellWithNoBorder("收据号：NO"+ receipt.get(0).get("PRINCIPALRUNCODE").toString(), PdfPCell.ALIGN_RIGHT,faNum));
					document.add(tTNum);
					// 表格
					PdfPTable tb1 = new PdfPTable(widthsSt2);
					tb1.addCell(makeCell('\r' + "" + "入账日期:",PdfPCell.ALIGN_CENTER, FontColumn));
					tb1.addCell(makeCell('\r' + "" + CTEATEDATESTRING,PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb1);
					PdfPTable tb2 = new PdfPTable(widthsSt2);
					tb2.addCell(makeCell('\r' + "" + "交款单位:",PdfPCell.ALIGN_CENTER, FontColumn));
					tb2.addCell(makeCell('\r' + ""+ receipt.get(0).get("CUSTNAME").toString(),PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb2);
					PdfPTable tb3 = new PdfPTable(widthsSt4);
					tb3.addCell(makeCell('\r' + "" + "合同号：",PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ receipt.get(0).get("LEASECODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString(), PdfPCell.ALIGN_CENTER,FontColumn));
					tb3.addCell(makeCell('\r' + "" + "发票号码：",
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ (receipt.get(0).get("INVOICE_CODE")==null?" ":String.valueOf(receipt.get(0).get("INVOICE_CODE"))),
							PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb3);
					BigDecimal bd1 = new BigDecimal(receipt.get(0)
							.get("REALOWNPRICE").toString());

					double price = bd1.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();

					PdfPTable tb4 = new PdfPTable(widthsSt3);
					tb4.addCell(makeCell("交款金额:" + '\r' + "（大写）",
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb4.addCell(makeCell(
							'\r'
									+ ""
									+ CurrencyConverter.toUpper(Double
											.toString(price)),
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb4.addCell(makeCell('\r' + "" + "￥" + price,
							PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb4);
					PdfPTable tb5 = new PdfPTable(widthsSt2);
					tb5.addCell(makeCell("收款银行:", PdfPCell.ALIGN_CENTER,
							FontColumn));
					tb5.addCell(makeCell(receipt.get(0).get("BANKNAME")==null?" ":String.valueOf(receipt.get(0).get("BANKNAME")), PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb5);
					PdfPTable tb6 = new PdfPTable(widthsSt2);
					tb6.addCell(makeCell('\r' + "" + '\r' + "" + '\r' + "单位盖章"
							+ '\r' + "(财务专用章)" + '\r' + '\r' + '\r',
							PdfPCell.ALIGN_CENTER, FontColumn));
					Image imageSeal = Image.getInstance(imageUrl.substring(6,
							imageUrl.length() - 16) + "images/seal.png");
					imageSeal.scaleAbsoluteHeight(121);
					imageSeal.scaleAbsoluteWidth(121);
					
					cell = new PdfPCell();
					cell.addElement(imageSeal);
					
					if (receipt.get(0).get("ORIPRINCIPALRUNCODE") != null
							&& !("").equals(receipt.get(0).get(
									"ORIPRINCIPALRUNCODE"))) {
						String ORIPRINCIPALRUNCODE = receipt.get(0)
								.get("ORIPRINCIPALRUNCODE").toString();
						/*tb6.addCell(makeCell("(原本金收据单号为：NO"
								+ ORIPRINCIPALRUNCODE + ")",
								PdfPCell.ALIGN_LEFT, FontColumn));*/
						Chunk chunkRUNCODE = new Chunk("(原本金收据单号为：NO"+ ORIPRINCIPALRUNCODE + ")", FontSmall);
						cell.addElement(chunkRUNCODE);
						tb6.addCell(cell);
						
					} else {
						tb6.addCell(cell);
						
					}
					document.add(tb6);
					// 增加pdf页面
					document.add(Chunk.NEXTPAGE);
				}

				if (document != null) {
					document.close();
				}

				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control",
						"must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition",
						"attachment; filename=receiptList");
				ServletOutputStream o = context.response.getOutputStream();
				baos.writeTo(o);
				o.flush();
				o.close();
				// 记录日志

				for (Object object : receiptlists) {
					List<Map> receipt = (List<Map>) object;
					receipt.get(0).put("PRINTUSERNAME",
							context.contextMap.get("s_employeeId"));
					// 更新T_FINA_COLLECTIONBILL为已打印
					try {
						DataAccessor.execute("rentReceipt.insertReceiptLog",
								receipt.get(0),
								DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentReceipt.upadteIsPrint",
								receipt.get(0),
								DataAccessor.OPERATION_TYPE.UPDATE);
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
				sqlMapper.commitTransaction();
			} else {
				ServletOutputStream o = context.response.getOutputStream();
				o.flush();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void receiptToPdfNew(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;
		List receiptlists = new ArrayList();
		List<Map> receiptlist = null;
		SqlMapClient sqlMapper = DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),
					"ids", "");
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("PRINCIPALRUNCODE", ids[i]);
				// 查出数据@合同的id
				receiptlist = (List<Map>) DataAccessor.query(
						"rentReceipt.queryRentReceiptByRecpIdNew",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				receiptlists.add(receiptlist);
			}
			if (receiptlists.size() > 0) {
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4).rotate(); // 定义A5页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				document.open();
				// 生成支付表PDF名字
				for (Object object : receiptlists) {
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light",
							"UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 20, Font.BOLD);
					Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
					Font fa = new Font(bfChinese, 30, Font.BOLD);
					Font faf = new Font(bfChinese, 20, Font.BOLD);
					Font faNum = new Font(bfChinese, 18, Font.BOLD);
					// 支付表PDF名字的定义
					@SuppressWarnings("unused")
					String strFileName = "";
					strFileName = "receiptList.pdf";
					// 表格列宽定义
					float[] widthsSt2 = { 0.2f, 0.75f };
					float[] widthsSt4 = { 0.2f, 0.25f, 0.25f, 0.25f };
					float[] widthsSt3 = { 0.2f, 0.5f, 0.25f };
					/* int iCnt = 0; */
					float[] widthsPPCa = { 2f };
					List<Map> receipt = (List<Map>) object;
					Object CREATEDATE = receipt.get(0).get("CREATEDATE");// 创建日期
					String CTEATEDATESTRING = null;
					SimpleDateFormat sf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
					if (CREATEDATE != null && !"".equals(CREATEDATE)) {
						CTEATEDATESTRING = sf.format(CREATEDATE);
					} else {
						CTEATEDATESTRING = "  年   月   日";
					}
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					// LOGO
					Font FontSmall = new Font(bfChinese, 15, Font.NORMAL);
					Font FontSmall2 = new Font(bfChinese, 15, Font.NORMAL);
					PdfPCell cell = null;
					PdfPTable t1 = new PdfPTable(12);
					PdfPTable tlogo = new PdfPTable(new float[] { 5f,30f,50f,40f});
					String imageUrl = ExportQuoToPdf.class.getResource("/").toString();//
					Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length() - 16) + "images/yrlogop.png");
					image.scaleAbsoluteHeight(25);
					image.scaleAbsoluteWidth(25);
					cell = new PdfPCell();//1
					cell.addElement(image);
					cell.setBorder(0);
					tlogo.addCell(cell);
					cell.addElement(image);
					//2
					Chunk chunk1 = new Chunk(Constants.COMPANY_NAME, FontSmall);
					Chunk chunk2 = new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
					cell = new PdfPCell();
					cell.addElement(chunk1);
					cell.addElement(chunk2);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//3
					cell = new PdfPCell();
					Chunk chunkNull = new Chunk("", FontSmall);
					cell.addElement(chunkNull);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//4
					cell = new PdfPCell();
					String oneCodePath=getOneBarcodeUtil( receipt.get(0).get("RECP_CODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString()) ;
					Image OneCodeImage = Image.getInstance(oneCodePath);
					OneCodeImage.scaleAbsoluteHeight(50);
					OneCodeImage.scaleAbsoluteWidth(200);
					cell.addElement(OneCodeImage);
					cell.setBorder(0);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setRight(PdfPCell.RIGHT);
					tlogo.addCell(cell);
					
					cell = new PdfPCell(tlogo);
					cell.setColspan(12);
					cell.setPaddingBottom(5);
					cell.setBorder(0);
					t1.addCell(cell);
					document.add(t1);
					// 标题
					PdfPTable tT = new PdfPTable(widthsPPCa);
					tT.setWidthPercentage(100f);
					tT.addCell(makeCellWithNoBorder("裕  融  租  赁  有  限  公  司",PdfPCell.ALIGN_CENTER, fa));
					document.add(tT);
					document.add(new Paragraph("\n"));
					// 副标题
					PdfPTable tTf = new PdfPTable(widthsPPCa);
					tTf.setWidthPercentage(100f);
					tTf.addCell(makeCellWithNoBorder("本 金 收 据",PdfPCell.ALIGN_CENTER, faf));
					document.add(tTf);
					//
					PdfPTable tTNum = new PdfPTable(widthsPPCa);
					tTNum.setWidthPercentage(80f);
					tTNum.addCell(makeCellWithNoBorder("收据号：NO"+ receipt.get(0).get("PRINCIPALRUNCODE").toString(), PdfPCell.ALIGN_RIGHT,faNum));
					document.add(tTNum);
					// 表格
					PdfPTable tb1 = new PdfPTable(widthsSt2);
					tb1.addCell(makeCell('\r' + "" + "入账日期:",PdfPCell.ALIGN_CENTER, FontColumn));
					tb1.addCell(makeCell('\r' + "" + CTEATEDATESTRING,PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb1);
					PdfPTable tb2 = new PdfPTable(widthsSt2);
					tb2.addCell(makeCell('\r' + "" + "交款单位:",PdfPCell.ALIGN_CENTER, FontColumn));
					tb2.addCell(makeCell('\r' + ""+ receipt.get(0).get("CUSTNAME").toString(),PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb2);
					PdfPTable tb3 = new PdfPTable(widthsSt4);
					tb3.addCell(makeCell('\r' + "" + "合同号：",PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ receipt.get(0).get("LEASECODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString(), PdfPCell.ALIGN_CENTER,FontColumn));
					tb3.addCell(makeCell('\r' + "" + "发票号码：",
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ (receipt.get(0).get("INVOICE_CODE")==null?" ":String.valueOf(receipt.get(0).get("INVOICE_CODE"))),
							PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb3);
					BigDecimal bd1 = new BigDecimal(receipt.get(0)
							.get("REALOWNPRICE").toString());

					double price = bd1.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();

					PdfPTable tb4 = new PdfPTable(widthsSt3);
					tb4.addCell(makeCell("交款金额:" + '\r' + "（大写）",
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb4.addCell(makeCell(
							'\r'
									+ ""
									+ CurrencyConverter.toUpper(Double
											.toString(price)),
							PdfPCell.ALIGN_CENTER, FontColumn));
					tb4.addCell(makeCell('\r' + "" + "￥" + price,
							PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb4);
					PdfPTable tb5 = new PdfPTable(widthsSt2);
					tb5.addCell(makeCell("收款银行:", PdfPCell.ALIGN_CENTER,
							FontColumn));
					tb5.addCell(makeCell(receipt.get(0).get("BANKNAME")==null?" ":String.valueOf(receipt.get(0).get("BANKNAME")), PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb5);
					PdfPTable tb6 = new PdfPTable(widthsSt2);
					tb6.addCell(makeCell('\r' + "" + '\r' + "" + '\r' + "单位盖章"
							+ '\r' + "(财务专用章)" + '\r' + '\r' + '\r',
							PdfPCell.ALIGN_CENTER, FontColumn));
					Image imageSeal = Image.getInstance(imageUrl.substring(6,
							imageUrl.length() - 16) + "images/seal.png");
					imageSeal.scaleAbsoluteHeight(121);
					imageSeal.scaleAbsoluteWidth(121);
					
					cell = new PdfPCell();
					cell.addElement(imageSeal);
					
					if (receipt.get(0).get("ORIPRINCIPALRUNCODE") != null
							&& !("").equals(receipt.get(0).get(
									"ORIPRINCIPALRUNCODE"))) {
						String ORIPRINCIPALRUNCODE = receipt.get(0)
								.get("ORIPRINCIPALRUNCODE").toString();
						/*tb6.addCell(makeCell("(原本金收据单号为：NO"
								+ ORIPRINCIPALRUNCODE + ")",
								PdfPCell.ALIGN_LEFT, FontColumn));*/
						Chunk chunkRUNCODE = new Chunk("(原本金收据单号为：NO"+ ORIPRINCIPALRUNCODE + ")", FontSmall);
						cell.addElement(chunkRUNCODE);
						tb6.addCell(cell);
						
					} else {
						tb6.addCell(cell);
						
					}
					document.add(tb6);
					// 增加pdf页面
					document.add(Chunk.NEXTPAGE);
				}

				if (document != null) {
					document.close();
				}

				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control",
						"must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition",
						"attachment; filename=receiptList");
				ServletOutputStream o = context.response.getOutputStream();
				baos.writeTo(o);
				o.flush();
				o.close();
				// 记录日志

				for (Object object : receiptlists) {
					List<Map> receipt = (List<Map>) object;
					receipt.get(0).put("PRINTUSERNAME",
							context.contextMap.get("s_employeeId"));
					// 更新T_FINA_COLLECTIONBILL为已打印
					try {
						DataAccessor.execute("rentReceipt.insertReceiptLog",
								receipt.get(0),
								DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentReceipt.upadteIsPrintNew",
								receipt.get(0),
								DataAccessor.OPERATION_TYPE.UPDATE);
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
				sqlMapper.commitTransaction();
			} else {
				ServletOutputStream o = context.response.getOutputStream();
				o.flush();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 直租发票打印PDF
	 * @param context
	 * modify by xuyuefei 2014/7/4
	 */
	public void directReceiptToPdfNew(Context context) {
		@SuppressWarnings("unused")
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;
		List receiptlists = new ArrayList();
		List<Map> receiptlist = null;
		SqlMapClient sqlMapper = DataAccessor.getSession();
		try {
			sqlMapper.startTransaction();
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(), "ids", "");
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("billId", ids[i]);
				// 查出数据@合同的id
				receiptlist = (List<Map>) DataAccessor.query("rentReceipt.queryRentReceiptByBillIdNew", context.contextMap, DataAccessor.RS_TYPE.LIST);
				receiptlists.add(receiptlist);
			}
			if (receiptlists.size() > 0) {
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4).rotate(); // 定义A5页面大小
				Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
				document.open();
				// 生成支付表PDF名字
				for (Object object : receiptlists) {
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 20, Font.BOLD);
					Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
					Font fa = new Font(bfChinese, 30, Font.BOLD);
					Font faf = new Font(bfChinese, 20, Font.BOLD);
					Font faNum = new Font(bfChinese, 18, Font.BOLD);
					// 支付表PDF名字的定义
					@SuppressWarnings("unused")
					String strFileName = "";
					strFileName = "receiptList.pdf";
					// 表格列宽定义
					float[] widthsSt2 = { 0.2f, 0.75f };
					float[] widthsSt4 = { 0.2f, 0.25f, 0.25f, 0.25f };
					float[] widthsSt3 = { 0.2f, 0.5f, 0.25f };
					/* int iCnt = 0; */
					float[] widthsPPCa = { 2f };
					List<Map> receipt = (List<Map>) object;
					Object CREATEDATE = receipt.get(0).get("CREATEDATE");// 创建日期
					String CTEATEDATESTRING = null;
					SimpleDateFormat sf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
					if (CREATEDATE != null && !"".equals(CREATEDATE)) {
						CTEATEDATESTRING = sf.format(CREATEDATE);
					} else {
						CTEATEDATESTRING = "  年   月   日";
					}
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					// LOGO
					Font FontSmall = new Font(bfChinese, 15, Font.NORMAL);
					Font FontSmall2 = new Font(bfChinese, 15, Font.NORMAL);
					PdfPCell cell = null;
					PdfPTable t1 = new PdfPTable(12);
					PdfPTable tlogo = new PdfPTable(new float[] { 5f,30f,50f,40f});
					String imageUrl = ExportQuoToPdf.class.getResource("/").toString();//
					Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length() - 16) + "images/yrlogop.png");
					image.scaleAbsoluteHeight(25);
					image.scaleAbsoluteWidth(25);
					cell = new PdfPCell();//1
					cell.addElement(image);
					cell.setBorder(0);
					tlogo.addCell(cell);
					cell.addElement(image);
					//2
					//根据公司code,判断开裕融还是裕国的发票收据    1裕融   2裕国
					Chunk chunk1=null;
					Chunk chunk2=null;
					int code=Integer.parseInt(receipt.get(0).get("COMPANY_CODE").toString());
					if(code==1){
						chunk1 = new Chunk(Constants.COMPANY_NAME, FontSmall);
						chunk2 = new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
					}
					if(code==2){
						chunk1 = new Chunk(Constants.COMPANY_NAME_YUGUO, FontSmall);
						chunk2 = new Chunk(Constants.COMPANY_NAME_ENGLISH_YUGUO,FontSmall2);
					}
					cell = new PdfPCell();
					cell.addElement(chunk1);
					cell.addElement(chunk2);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//3
					cell = new PdfPCell();
					Chunk chunkNull = new Chunk("", FontSmall);
					cell.addElement(chunkNull);
					cell.setBorder(0);
					tlogo.addCell(cell);
					//4
					cell = new PdfPCell();
					String oneCodePath=getOneBarcodeUtil( receipt.get(0).get("RECP_CODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString()) ;
					Image OneCodeImage = Image.getInstance(oneCodePath);
					OneCodeImage.scaleAbsoluteHeight(50);
					OneCodeImage.scaleAbsoluteWidth(200);
					cell.addElement(OneCodeImage);
					cell.setBorder(0);
					cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					cell.setRight(PdfPCell.RIGHT);
					tlogo.addCell(cell);
					
					cell = new PdfPCell(tlogo);
					cell.setColspan(12);
					cell.setPaddingBottom(5);
					cell.setBorder(0);
					t1.addCell(cell);
					document.add(t1);
					// 标题
					PdfPTable tT = new PdfPTable(widthsPPCa);
					tT.setWidthPercentage(100f);
					if(code==1){
						tT.addCell(makeCellWithNoBorder("裕 融 租 赁 有 限 公 司",PdfPCell.ALIGN_CENTER, fa));
					}
					if(code==2){
						tT.addCell(makeCellWithNoBorder("裕 国 融 资 租 赁 有 限 公 司",PdfPCell.ALIGN_CENTER, fa));
					}
					document.add(tT);
					document.add(new Paragraph("\n"));
					// 副标题
					PdfPTable tTf = new PdfPTable(widthsPPCa);
					tTf.setWidthPercentage(100f);
					tTf.addCell(makeCellWithNoBorder("直 租 发 票 收 据",PdfPCell.ALIGN_CENTER, faf));
					document.add(tTf);
					//
					PdfPTable tTNum = new PdfPTable(widthsPPCa);
					tTNum.setWidthPercentage(80f);
					tTNum.addCell(makeCellWithNoBorder(" ", PdfPCell.ALIGN_RIGHT,faNum));
					document.add(tTNum);
					// 表格
//					PdfPTable tb1 = new PdfPTable(widthsSt2);
//					tb1.addCell(makeCell('\r' + "" + "入账日期:",PdfPCell.ALIGN_CENTER, FontColumn));
//					tb1.addCell(makeCell('\r' + "" + CTEATEDATESTRING,PdfPCell.ALIGN_CENTER, FontColumn));
//					document.add(tb1);
					PdfPTable tb2 = new PdfPTable(widthsSt2);
					tb2.addCell(makeCell('\r' + "" + "交款单位:",PdfPCell.ALIGN_CENTER, FontColumn));
					tb2.addCell(makeCell('\r' + ""+ receipt.get(0).get("CUSTNAME").toString(),PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb2);
					PdfPTable tb3 = new PdfPTable(widthsSt4);
					tb3.addCell(makeCell('\r' + "" + "合同号：",PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ receipt.get(0).get("LEASECODE").toString()+"-"+receipt.get(0).get("RECDPERIOD").toString(), PdfPCell.ALIGN_CENTER,FontColumn));
					tb3.addCell(makeCell('\r' + "" + "发票号码：", PdfPCell.ALIGN_CENTER, FontColumn));
					tb3.addCell(makeCell('\r'+ ""+ (receipt.get(0).get("INVOICE_CODE")==null?" ":String.valueOf(receipt.get(0).get("INVOICE_CODE"))),
							PdfPCell.ALIGN_CENTER, FontColumn));
					document.add(tb3);
					// 增加pdf页面
					document.add(Chunk.NEXTPAGE);
				}
				if (document != null) {
					document.close();
				}
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				String filename = "直租发票收据" + DateUtil.dateToString(new Date(), "yyyyMMddHHmmss") + ".pdf";
				filename = new String(filename.getBytes(),"iso8859-1");
				context.response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream o = context.response.getOutputStream();
				baos.writeTo(o);
				o.flush();
				o.close();
				// 记录日志
				for (Object object : receiptlists) {
					List<Map> receipt = (List<Map>) object;
					receipt.get(0).put("PRINTUSERNAME", context.contextMap.get("s_employeeId"));
					receipt.get(0).put("RECEIPT_TYPE", "direct");
					// 更新T_FINA_COLLECTIONBILL为已打印
					try {
						DataAccessor.execute("rentReceipt.insertReceiptLog", receipt.get(0), DataAccessor.OPERATION_TYPE.INSERT);
						DataAccessor.execute("rentReceipt.upadteIsPrintNewByBillId", receipt.get(0), DataAccessor.OPERATION_TYPE.UPDATE);
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
				}
				sqlMapper.commitTransaction();
			} else {
				ServletOutputStream o = context.response.getOutputStream();
				o.flush();
				o.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		} finally {
			try {
				sqlMapper.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 创建多列单元格
	private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);

		return objCell;
	}

	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorder(String content, int align,
			Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}
	
	// 得到条形码（支付表号）
		public String getOneBarcodeUtil(String recpCode)  {
				String path="";
					try {
						JBarcode localJBarcode = new JBarcode(EAN13Encoder.getInstance(),
								WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
						localJBarcode.setEncoder(Code39Encoder.getInstance());
						localJBarcode.setPainter(WideRatioCodedPainter.getInstance());
						localJBarcode.setTextPainter(BaseLineTextPainter.getInstance());
						localJBarcode.setShowCheckDigit(false);
					BufferedImage localBufferedImage;
					localBufferedImage = localJBarcode.createBarcode(recpCode);
					String recpCodeName = DateUtil.dateToString(new Date(),"yyyyMMddHHmmSSS") + recpCode;
					 path = saveToPNG(localBufferedImage, recpCodeName + ".png");
				} catch (InvalidAtributeException e) {
					Throwable t = e.getCause();
					t.printStackTrace();
				}
			return path;
		}

		// 得到条形码类型
		public String saveToPNG(BufferedImage paramBufferedImage, String paramString) {
			String path = saveToFile(paramBufferedImage, paramString, "png");
			return path;
		}

		// 写入服务器
		public String saveToFile(BufferedImage paramBufferedImage,
				String paramString1, String paramString2) {
			String path = "";
			try {
				File realPath = new File("D:/home/filsoft/financelease/oneCode"
						+ File.separator
						+ DateUtil.dateToString(new Date(), "yyyy_MM_dd")
						+ File.separator);
				if (!realPath.exists())
					realPath.mkdirs();
				FileOutputStream localFileOutputStream = new FileOutputStream(
						realPath + File.separator + paramString1);
				ImageUtil.encodeAndWrite(paramBufferedImage, paramString2,
						localFileOutputStream, 96,96);
				localFileOutputStream.close();
				path = realPath + File.separator + paramString1;
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return path;
		}
}
