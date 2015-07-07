package com.brick.customer.service;

import com.brick.service.core.AService;
import com.brick.service.core.Output;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;

/*
 * Add by Michael 2012 4-12
 * 导出客户虚拟账号
 */
public class ExportCustomerVirtualCodeExcel {

	public static Map<String, Object> getCustomerVirtualCodeData(String custID,
			String s_employeeId) throws Exception {
		System.out.println("==================Start========================");
		List customerVirtualCode = new ArrayList();
		Map customer = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		resultMap.put("APPLY_DATE", sf.format(new Date()));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("CUST_ID", custID);
		try {
			DataAccessor.getSession().startTransaction();
			customerVirtualCode = (List) DataAccessor.query(
					"customer.getCustomerVirtualCodeBirt", paramMap,
					DataAccessor.RS_TYPE.LIST);
			resultMap.put("customerVirtualCode", customerVirtualCode);

			for (int i = 0; i < customerVirtualCode.size(); i++) {
				customer = (Map) customerVirtualCode.get(i);
				customer.put("s_employeeId", s_employeeId);
				DataAccessor.getSession().update(
						"customer.updateCustomerExportFlag", customer);
				DataAccessor.getSession().insert(
						"customer.createExportCustVirtualLog", customer);

			}
			DataAccessor.getSession().commitTransaction();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				DataAccessor.getSession().endTransaction();
			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		System.out.println("==================end========================");
		return resultMap;

	}

	// @SuppressWarnings("unchecked")
	// public void expCustomerVirtualCodeData(Context context,
	// List customerVirtualCode) {
	//
	// ByteArrayOutputStream baos = null;
	// try {
	// // 字体设置
	// BaseFont bfChinese = BaseFont.createFont("STSong-Light",
	// "UniGB-UCS2-H", BaseFont.EMBEDDED);
	// Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	// Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	// Font FontDefault222 = new Font(bfChinese, 7, Font.NORMAL);
	// Font FontDefault2 = new Font(bfChinese, 11, Font.NORMAL);
	// Font FontUnde2 = new Font(bfChinese, 10, Font.UNDERLINE);
	// Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	// Font fa = new Font(bfChinese, 22, Font.BOLD);
	// // 数字格式
	// NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
	// nfFSNum.setGroupingUsed(true);
	// nfFSNum.setMaximumFractionDigits(2);
	// // 页面设置
	// Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	// Document document = new Document(rectPageSize, 20, 20, 20, 20); //
	// 其余4个参数，设置了页面的4个边距
	//
	// baos = new ByteArrayOutputStream();
	// PdfWriter.getInstance(document, baos);
	//
	// // 打开文档
	// document.open();
	//
	// PdfPTable tT = new PdfPTable(3);
	// tT.setWidthPercentage(100f);
	// Map customer;
	//
	// int i = 0;
	//
	// tT.addCell(makeCellSetColspan("公司名称",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// tT.addCell(makeCellSetColspan("承租人",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// tT.addCell(makeCellSetColspan("虚拟账号",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// i++;
	//
	// for (int ii = 0; ii < customerVirtualCode.size(); ii++) {
	// customer = (HashMap) customerVirtualCode.get(ii);
	//
	// // 承租方名称
	// String custname = "";
	// if (customer.get("CUST_NAME") == null) {
	// custname = "  ";
	// } else {
	// custname = customer.get("CUST_NAME").toString();
	// }
	// // 承租方地址
	// String virtualcode = "";
	// if (customer.get("VIRTUAL_CODE") == null) {
	// virtualcode = "  ";
	// } else {
	// virtualcode = customer.get("VIRTUAL_CODE")
	// .toString();
	// }
	//
	// tT.addCell(makeCellSetColspan(Constants.COMPANY_NAME,
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// tT.addCell(makeCellSetColspan(custname,
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// tT.addCell(makeCellSetColspan(virtualcode,
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	// i++;
	// }
	// tT.addCell(makeCellSetColspanNoBorder("",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 2));
	// tT.addCell(makeCellSetColspanNoBorder("申请人：",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	//
	// tT.addCell(makeCellSetColspanNoBorder("",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 2));
	// tT.addCell(makeCellSetColspanNoBorder("申请日期：",
	// PdfPCell.ALIGN_LEFT, FontDefault2, 1));
	//
	// document.add(tT);
	// document.add(Chunk.NEXTPAGE);
	//
	// document.close();
	// // 支付表PDF名字的定义
	// String strFileName = "customervirtual.pdf";
	// context.response.setContentType("application/pdf");
	// context.response.setCharacterEncoding("UTF-8");
	// context.response.setHeader("Pragma", "public");
	// context.response.setHeader("Cache-Control",
	// "must-revalidate, post-check=0, pre-check=0");
	// context.response.setDateHeader("Expires", 0);
	// context.response.setHeader("Content-Disposition",
	// "attachment; filename=" + strFileName);
	//
	// ServletOutputStream o = context.response.getOutputStream();
	//
	// baos.writeTo(o);
	// o.flush();
	// closeStream(o);
	//
	// // add by ShenQi 插入系统日志
	// BusinessLog.addBusinessLogWithIp(DataUtil
	// .longUtil(context.contextMap.get("s_employeeId")), null,
	// "导出 客户虚拟账号", "导出 客户虚拟账号", null,
	// context.contextMap.get("s_employeeName") + "("
	// + context.contextMap.get("s_employeeId")
	// + ")导出 客户虚拟账号", 1, DataUtil
	// .longUtil(context.contextMap.get("s_employeeId")
	// .toString()), DataUtil.longUtil(0), context
	// .getRequest().getRemoteAddr());
	// } catch (Exception e) {
	// e.printStackTrace();
	// LogPrint.getLogStackTrace(e, logger);
	// }
	// }
	//
	//
	// /**
	// * 流关闭操作
	// *
	// * @param content
	// * @param align
	// * @param FontDefault
	// * @return
	// */
	// private void closeStream(OutputStream o) {
	// try {
	//
	// o.close();
	//
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// LogPrint.getLogStackTrace(e, logger);
	//
	// } finally {
	//
	// try {
	//
	// o.close();
	//
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// LogPrint.getLogStackTrace(e, logger);
	// }
	// }
	//
	// }
	//
	// /**
	// * 创建 有边框 合并 单元格|-| 无下边用于表格的顶
	// *
	// * */
	// private PdfPCell makeCellSetColspan3(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(0);
	//
	// return objCell;
	// }
	//
	// /**
	// * 创建 有边框 合并 单元格|- 无下边用于表格的顶
	// *
	// * */
	// private PdfPCell makeCellSetColspan2NoBottomAndRight(String content,
	// int align, Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthRight(0);
	// return objCell;
	// }
	//
	// /**
	// * 创建 有边框 合并 单元格|_| 无上边
	// *
	// * */
	// private PdfPCell makeCellSetColspan3NoTop(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthTop(0);
	//
	// return objCell;
	// }
	//
	// /**
	// * 创建 下边框 单元格_ 无上边
	// *
	// * */
	// private PdfPCell makeCellSetColspan4NoTop(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthTop(0);
	// objCell.setBorderWidthLeft(0);
	// objCell.setBorderWidthRight(0);
	// return objCell;
	// }
	//
	// /**
	// * 创建 有边框 合并 单元格| | 无上下边
	// *
	// * */
	// private PdfPCell makeCellSetColspan2(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthTop(0);
	// return objCell;
	// }
	//
	// /**
	// * 创建 无边框 合并 单元格
	// *
	// */
	// private PdfPCell makeCellSetColspanNoBorder(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthTop(0);
	// objCell.setBorderWidthLeft(0);
	// objCell.setBorderWidthRight(0);
	// objCell.setPaddingTop(5);
	// objCell.setPaddingBottom(5);
	// return objCell;
	// }
	//
	// /**
	// * 创建 无边框 合并 单元格
	// *
	// */
	// private PdfPCell makeCellSetColspanNoBorder(Phrase phrase, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = phrase;
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthTop(0);
	// objCell.setBorderWidthLeft(0);
	// objCell.setBorderWidthRight(0);
	// objCell.setPaddingTop(7);
	// objCell.setPaddingBottom(7);
	// return objCell;
	// }
	//
	// private PdfPCell makeCellSetColspan3NoLeft(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthLeft(0);
	// return objCell;
	// }
	//
	// /*
	// * 创建 有边框 四边都有 合并 单元格|-_|
	// */
	// private PdfPCell makeCellSetColspan(String content, int align,
	// Font FontDefault, int colspan) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	//
	// return objCell;
	// }
	//
	// /** 创建 只有左边框 单元格 */
	// private PdfPCell makeCellWithBorderLeft(String content, int align,
	// Font FontDefault) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	//
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthTop(0);
	// objCell.setBorderWidthRight(0);
	// return objCell;
	// }
	//
	// /** 创建 只有右边框 单元格 */
	// private PdfPCell makeCellWithBorderRight(String content, int align,
	// Font FontDefault) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	//
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setBorderWidthBottom(0);
	// objCell.setBorderWidthTop(0);
	// objCell.setBorderWidthLeft(0);
	// return objCell;
	// }
	//
	// /** 创建 只有左边框 单元格 */
	// private PdfPCell makeCellSetColspanNull(String content, int align,Font
	// FontDefault, int colspan,float T,float B,float L,float R) {
	// Phrase objPhase = new Phrase(content, FontDefault);
	// PdfPCell objCell = new PdfPCell(objPhase);
	// objCell.setHorizontalAlignment(align);
	// objCell.setVerticalAlignment(align);
	// objCell.setColspan(colspan);
	// objCell.setBorderWidthBottom(B);
	// objCell.setBorderWidthLeft(L);
	// objCell.setBorderWidthRight(R);
	// objCell.setBorderWidthTop(T);
	//
	// return objCell;
	// }
}