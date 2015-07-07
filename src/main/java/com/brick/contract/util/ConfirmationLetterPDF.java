package com.brick.contract.util;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import com.brick.baseManage.service.BusinessLog;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 导出确认函
 * 
 * @author HF
 * 
 * 2011 3:31:55 PM
 */
public class ConfirmationLetterPDF {

	/**
	 * 0导出一般租赁确认函
	 */
	@SuppressWarnings("unchecked")
	public static void includeLeasesToPdf(Context context,Map infoMap,List<Map> eqmts) {
//		Map creditInfoMap = null;
		ByteArrayOutputStream baos = null;
		try {
//			creditInfoMap = (Map) DataAccessor.query(
//					"rentContract.getCreditByID", context.contextMap,
//					DataAccessor.RS_TYPE.MAP);
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
//			Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
//			Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
//			Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
			Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
//			Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
			Font fa = new Font(bfChinese, 18, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 60, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			// 打开文档
			document.open();

			PdfPTable t1 = new PdfPTable(1);
			t1.setWidthPercentage(100f);
			t1.addCell(makeCellSetColspanWithNoBorder("确认函", PdfPCell.ALIGN_CENTER, fa, 1));
			document.add(t1);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));

			PdfPTable t2 = new PdfPTable(2);
			t2.setWidthPercentage(100f);
			t2.addCell(makeCellSetColspanWithNoBorder("致："+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
			document.add(t2);

			PdfPTable t3 = new PdfPTable(1);
			t3.setWidthPercentage(80f);
			
			t3.addCell(makeCellSetColspanWithNoBorder(toString(infoMap, "SUPL_NAME")+"	确认，本司已于本确认函出具之日收",PdfPCell.ALIGN_LEFT, FontDefault,1));
			t3.addCell(makeCellSetColspanWithNoBorder(toString(infoMap, "CUST_NAME")+"	（以下简称“承租人”）汇入本司的下述机器设备之相关款项：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			document.add(t3);

			PdfPTable t4 = new PdfPTable(1);
			t4.setWidthPercentage(100f);
			PdfPCell objCell = new PdfPCell();
		    Phrase phrase = new Phrase();
	    	objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell.setBorder(0);
	    	Chunk chunk = new Chunk("（1）部分含税价款，计人民币",FontDefault);
	    	phrase.add(chunk);
	    	String lease_price="0";
	    	//String agratefirst="0";
	    	if(infoMap.get("PLEDGE_ENTER_AG")!=null ){
	    		lease_price=infoMap.get("PLEDGE_ENTER_AG").toString();
	    	}
	    	//if(creditInfoMap.get("PLEDGE_ENTER_AGRATE")!=null){
	    	//	agratefirst=creditInfoMap.get("PLEDGE_ENTER_AGRATE").toString();
	    	//}
	    	chunk = new Chunk(nfFSNum.format(Double.parseDouble(lease_price)), FontUnder);
	    	phrase.add(chunk);
	    	chunk = new Chunk("元整。",FontDefault);
	    	phrase.add(chunk);
	    	objCell.addElement(phrase);
		    t4.addCell(objCell);
			document.add(t4);
			
			PdfPTable t5 = new PdfPTable(1);
			t5.setWidthPercentage(100f);
			t5.addCell(makeCellSetColspanWithNoBorder("机器设备：", PdfPCell.ALIGN_LEFT, FontColumn, 1));
			document.add(t5);
			
			float[] widthsStl = {0.15f,0.2f,0.2f,0.1f,0.05f,0.20f,0.10f};
			PdfPTable t6 = new PdfPTable(widthsStl);
			t6.setWidthPercentage(100);
			t6.addCell(makeCell("名称", PdfPCell.ALIGN_CENTER, FontColumn));
			t6.addCell(makeCell("厂牌", PdfPCell.ALIGN_CENTER, FontColumn));
			t6.addCell(makeCell("规格型号", PdfPCell.ALIGN_CENTER, FontColumn));
			t6.addCell(makeCell("机号", PdfPCell.ALIGN_CENTER, FontColumn));
			t6.addCell(makeCell("数量", PdfPCell.ALIGN_CENTER, FontColumn));
			PdfPTable t7 = new PdfPTable(1);
			t7.addCell(makeCellWithBorderBottom("单价（元）", PdfPCell.ALIGN_CENTER, FontColumn,1));
			t7.addCell(makeCellSetColspanWithNoBorder("含税价款", PdfPCell.ALIGN_CENTER, FontColumn,1));
			t6.addCell(t7);
			t6.addCell(makeCell("附注", PdfPCell.ALIGN_CENTER, FontColumn));
			
			
			int size=eqmts.size();
			double total=0;
			for(int i=0;i<size;i++){
				Map eqmt=eqmts.get(i);
				t6.addCell(makeCell(toString(eqmt,"THING_NAME"), PdfPCell.ALIGN_CENTER, FontDefault));
				t6.addCell(makeCell(toString(eqmt, "THING_KIND"), PdfPCell.ALIGN_CENTER, FontDefault));
				t6.addCell(makeCell(toString(eqmt, "MODEL_SPEC"), PdfPCell.ALIGN_CENTER, FontDefault));
				t6.addCell(makeCell(toString(eqmt, "THING_NUMBER"), PdfPCell.ALIGN_CENTER, FontDefault));
				t6.addCell(makeCell(toString(eqmt,"AMOUNT"), PdfPCell.ALIGN_CENTER, FontDefault));
				PdfPTable t11 = new PdfPTable(1);
				String shui_price="0";
				if(eqmt.get("SHUI_PRICE")!=null){
					shui_price=eqmt.get("SHUI_PRICE").toString();
				}
				if(eqmt.get("LEASE_PRICE")!=null){
					lease_price=eqmt.get("LEASE_PRICE").toString();
				}
				total+=Double.parseDouble(lease_price);
	
				t11.addCell(makeCellWithBorderLeftTopRight(nfFSNum.format(Double.parseDouble(shui_price)), PdfPCell.ALIGN_RIGHT, FontDefault,1));				
				t6.addCell(t11);
			    t6.addCell(makeCell("", PdfPCell.ALIGN_CENTER, FontDefault));

			}
			t6.addCell(makeCellCol(" ", PdfPCell.ALIGN_CENTER, FontDefault,5));
			t6.addCell(makeCell("合计:"+nfFSNum.format(total), PdfPCell.ALIGN_RIGHT, FontDefault));
			t6.addCell(makeCell("", PdfPCell.ALIGN_CENTER, FontDefault));
			document.add(t6);
		    document.add(new Paragraph("\n"));

			
			
//			
//			t6.addCell(makeCell(creditInfoMap.get("THING_NAME")==null?"":creditInfoMap.get("THING_NAME").toString(), PdfPCell.ALIGN_CENTER, FontDefault));
//			t6.addCell(makeCell(creditInfoMap.get("THING_KIND")==null?"":creditInfoMap.get("THING_KIND").toString(), PdfPCell.ALIGN_CENTER, FontDefault));
//			t6.addCell(makeCell(creditInfoMap.get("MODEL_SPEC")==null?"":creditInfoMap.get("MODEL_SPEC").toString(), PdfPCell.ALIGN_CENTER, FontDefault));
//			t6.addCell(makeCell(creditInfoMap.get("THING_NUMBER")==null?" ":creditInfoMap.get("THING_NUMBER").toString(), PdfPCell.ALIGN_CENTER, FontDefault));
//			t6.addCell(makeCell(creditInfoMap.get("AMOUNT")==null?"":creditInfoMap.get("AMOUNT").toString(), PdfPCell.ALIGN_CENTER, FontDefault));
//			PdfPTable t8 = new PdfPTable(1);
//			String shui_price="0";
//			String newlease_price="0";
//			if(creditInfoMap.get("SHUI_PRICE")!=null){
//				shui_price=creditInfoMap.get("SHUI_PRICE").toString();
//			}
//			if(creditInfoMap.get("LEASE_PRICE")!=null){
//				newlease_price=creditInfoMap.get("LEASE_PRICE").toString();
//			}
//			t8.addCell(makeCellWithBorderLeftTopRight(nfFSNum.format(Double.parseDouble(shui_price)), PdfPCell.ALIGN_RIGHT, FontDefault,1));
//			t8.addCell(makeCellSetColspanWithNoBorder("合计:"+nfFSNum.format(Double.parseDouble(newlease_price)), PdfPCell.ALIGN_RIGHT, FontDefault,1));
//		    t6.addCell(t8);
//		    t6.addCell(makeCell("", PdfPCell.ALIGN_CENTER, FontDefault));
//		    document.add(t6);
//		    document.add(new Paragraph("\n"));
		    
		    PdfPTable t9 = new PdfPTable(1);
		    t9.setWidthPercentage(100f);
			PdfPCell objCell2 = new PdfPCell();
		    Phrase phrase2 = new Phrase();
		    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
		    objCell2.setBorder(0);
	    	Chunk chunk2 = new Chunk("	     依据贵公司和承租人签订的编号为",FontDefault);
	    	phrase2.add(chunk2);
	    	if(infoMap.get("LEASE_CODE")!=null){
	    		chunk2 = new Chunk(toString(infoMap,"LEASE_CODE"), FontUnder);
	    	}else{
	    		chunk2 = new Chunk("___________", FontDefault);
	    	}
	    	phrase2.add(chunk2);
	    	chunk2 = new Chunk("《融资租赁合同》及相关协议，以及本司和贵公司签订的编号为",FontDefault);
	    	phrase2.add(chunk2);
//	    	if(infoMap.get("PUCT_CODE")!=null){
//	    		chunk2 = new Chunk(toString(infoMap, "PUCT_CODE"), FontUnder);
//	    	}else{
//	    		chunk2 = new Chunk("___________", FontDefault);
//	    	}
	    	if(infoMap.get("LEASE_CODE")!=null){
	    		chunk2 = new Chunk(toString(infoMap,"LEASE_CODE"), FontUnder);
	    	}else{
	    		chunk2 = new Chunk("___________", FontDefault);
	    	}
	    	phrase2.add(chunk2);
	    	chunk2 = new Chunk("《买卖合同》，贵公司就上述机器设备需向本公司另行支付人民币",FontDefault);
	    	phrase2.add(chunk2);
	    	String addlease_price="0.0";
	    	if(infoMap.get("PLEDGE_ENTER_AG")!=null ){
	    		addlease_price=infoMap.get("PLEDGE_ENTER_AG").toString();
	    	}
	    	double yingfu=total-Double.parseDouble(addlease_price);
	    	chunk2 = new Chunk(nfFSNum.format(yingfu), FontDefault);
	    	phrase2.add(chunk2);
	    	chunk2 = new Chunk("元货款。",FontDefault);
	    	phrase2.add(chunk2);
	    	objCell2.addElement(phrase2);
	    	t9.addCell(objCell2);
			document.add(t9);
		    
			PdfPTable t10 = new PdfPTable(3);
			t10.setWidthPercentage(100f);
			t10.addCell(makeCellSetColspanWithNoBorder("	     特此确认", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 3));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			t10.addCell(makeCellSetColspanWithNoBorder(toString(infoMap, "SUPL_NAME"), PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			t10.addCell(makeCellSetColspanWithNoBorder("法定代表人：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			t10.addCell(makeCellSetColspanWithNoBorder("日期："+"      "+"年"+"      "+"月"+"      "+"日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault, 3));
			t10.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t10.addCell(makeCellSetColspanWithNoBorder("（公司盖章）  ", PdfPCell.ALIGN_RIGHT, FontDefault, 2));
			document.add(t10);
			
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition", "attachment; filename=querenhanYBZL.pdf");
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o);
			o.flush();
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 1导出委托租赁确认函
	 */
	@SuppressWarnings("unchecked")
	public static void consignmentFinancialLeasingToPdf(Context context,Map InfoMap,List<Map> eqmts) {
		ByteArrayOutputStream baos = null;
		try {
			
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
			Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
			Font fa = new Font(bfChinese, 18, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 60, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
			baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			// 打开文档
			document.open();

			PdfPTable t1 = new PdfPTable(1);
			t1.setWidthPercentage(100f);
			t1.addCell(makeCellSetColspanWithNoBorder("确认函", PdfPCell.ALIGN_CENTER, fa, 1));
			document.add(t1);
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			
			PdfPTable t2 = new PdfPTable(2);
			t2.setWidthPercentage(100f);
			t2.addCell(makeCellSetColspanWithNoBorder("致："+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
			document.add(t2);
			
			PdfPTable t3 = new PdfPTable(1);
			t3.setWidthPercentage(100f);
			PdfPCell objCell = new PdfPCell();
		    Phrase phrase = new Phrase();
	    	objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell.setBorder(0);
	    	Chunk chunk = new Chunk("         本司",FontDefault);
	    	phrase.add(chunk);
	    	chunk = new Chunk("  "+toString(InfoMap,"SUPL_NAME")+"  ", FontUnder);
	    	phrase.add(chunk);
	    	chunk = new Chunk("确认，本司已于本确认函出具之日收到",FontDefault);
	    	phrase.add(chunk);
	    	objCell.addElement(phrase);
		    t3.addCell(objCell);
			document.add(t3);
			
			PdfPTable t4 = new PdfPTable(1);
			t4.setWidthPercentage(100f);
			PdfPCell objCell2 = new PdfPCell();
		    Phrase phrase2 = new Phrase();
	    	objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
	    	objCell2.setBorder(0);
	    	Chunk chunk2 = new Chunk("         ",FontDefault);
	    	phrase2.add(chunk2);
	    	chunk2 = new Chunk(toString(InfoMap, "CUST_NAME")+"  （以下简称“承租人”）", FontUnder);
	    	phrase2.add(chunk2);
	    	chunk2 = new Chunk("汇入本司的下述机器设备之相关款项：",FontDefault);
	    	phrase2.add(chunk2);
	    	objCell2.addElement(phrase2);
		    t4.addCell(objCell2);
			document.add(t4);
			
			
			//增值税 和 未含税价款的计算
			double sumShuiPrice = 0.0d;
			double sumUnitPrice = 0.0d;
			double sumTotalPrice = 0.0d;
			for(int i=0 ;i<eqmts.size();i++){
				Map eqmt = eqmts.get(i) ;
				if(eqmt.get("SHUI_PRICE")!=null){
					sumTotalPrice += Double.parseDouble(eqmt.get("SHUI_PRICE").toString());
				}
				if(eqmt.get("UNIT_PRICE")!=null){
					sumUnitPrice += Double.parseDouble(eqmt.get("UNIT_PRICE").toString());
				}
			}
			sumUnitPrice= Math.round(sumUnitPrice);
			sumShuiPrice = sumTotalPrice - sumUnitPrice ;
			
			//增值税 和 未含税价款的计算   结束
			
			
			PdfPTable t5 = new PdfPTable(1);
			t5.setWidthPercentage(100f);
			PdfPCell objCell3 = new PdfPCell();
			Phrase phrase3 = new Phrase();
			objCell3.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell3.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
			objCell3.setBorder(0);
			Chunk chunk3 = new Chunk("         ",FontDefault);
			phrase3.add(chunk3);
			chunk3 = new Chunk("（1）增值税税款，计人民币",FontDefault);
			phrase3.add(chunk3);
			String agrate="0";
			String agprice="0";
			//Add by Michael 2012 4-10 增加我司入供应商
			String mcagprice="0";
			if(InfoMap.get("PLEDGE_ENTER_MCTOAG")!=null){
				mcagprice=InfoMap.get("PLEDGE_ENTER_MCTOAG").toString();
			}
			//-----------------------------------------------
			if(InfoMap.get("PLEDGE_ENTER_AGRATE")!=null){
				agrate=InfoMap.get("PLEDGE_ENTER_AGRATE").toString();
			}
			if(InfoMap.get("PLEDGE_ENTER_AG")!=null){
				agprice=InfoMap.get("PLEDGE_ENTER_AG").toString();
			}
			
			chunk3 = new Chunk(nfFSNum.format(sumShuiPrice), FontUnder);
//			chunk3 = new Chunk(nfFSNum.format(Double.parseDouble(agrate)), FontUnder);
			phrase3.add(chunk3);
			chunk3 = new Chunk("元。", FontDefault);
			phrase3.add(chunk3);
			objCell3.addElement(phrase3);
			t5.addCell(objCell3);
			document.add(t5);
			
			PdfPTable t6 = new PdfPTable(1);
			t6.setWidthPercentage(100f);
			PdfPCell objCell4 = new PdfPCell();
			Phrase phrase4 = new Phrase();
			objCell4.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell4.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
			objCell4.setBorder(0);
			Chunk chunk4 = new Chunk("         ",FontDefault);
			phrase4.add(chunk4);
			chunk4 = new Chunk("（2）部分未含税价款，计人民币",FontDefault);
			phrase4.add(chunk4);
			
			chunk4 = new Chunk(nfFSNum.format(Double.parseDouble(agprice)+Double.parseDouble(mcagprice)), FontUnder);
//			chunk4 = new Chunk(nfFSNum.format(Double.parseDouble(agprice)), FontUnder);
			//chunk4 = new Chunk("        ", FontUnder);
			phrase4.add(chunk4);
			chunk4 = new Chunk("元。", FontDefault);
			phrase4.add(chunk4);
			objCell4.addElement(phrase4);
			t6.addCell(objCell4);
			document.add(t6);
			
			PdfPTable t7 = new PdfPTable(1);
			t7.setWidthPercentage(100f);
			PdfPCell objCell5 = new PdfPCell();
			Phrase phrase5 = new Phrase();
			objCell5.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell5.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
			objCell5.setBorder(0);
			Chunk chunk5 = new Chunk("         ",FontDefault);
			phrase5.add(chunk5);
			chunk5 = new Chunk("上述（1）和（2）合计人民币",FontDefault);
			phrase5.add(chunk5);
			chunk5 = new Chunk(nfFSNum.format(sumShuiPrice+Double.parseDouble(agprice)+Double.parseDouble(mcagprice)), FontUnder);
//			chunk5 = new Chunk(nfFSNum.format(Double.parseDouble(agrate)+Double.parseDouble(agprice)), FontUnder);
			phrase5.add(chunk5);
			chunk5 = new Chunk("元整。", FontDefault);
			phrase5.add(chunk5);
			objCell5.addElement(phrase5);
			t7.addCell(objCell5);
			document.add(t7);
			
			PdfPTable t8 = new PdfPTable(1);
			t8.setWidthPercentage(100f);
			t8.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_LEFT, FontColumn, 1));
			t8.addCell(makeCellSetColspanWithNoBorder("机器设备：", PdfPCell.ALIGN_LEFT, FontColumn, 1));
			document.add(t8);
			
			float[] widthsStl = {0.15f,0.2f,0.2f,0.1f,0.05f,0.20f,0.10f};
			PdfPTable t9 = new PdfPTable(widthsStl);
			t9.setWidthPercentage(100);
			t9.addCell(makeCell("名称", PdfPCell.ALIGN_CENTER, FontColumn));
			t9.addCell(makeCell("厂牌", PdfPCell.ALIGN_CENTER, FontColumn));
			t9.addCell(makeCell("规格型号", PdfPCell.ALIGN_CENTER, FontColumn));
			t9.addCell(makeCell("机号", PdfPCell.ALIGN_CENTER, FontColumn));
			t9.addCell(makeCell("数量", PdfPCell.ALIGN_CENTER, FontColumn));
			PdfPTable t10 = new PdfPTable(2);
//			t10.addCell(CellSetColspanWithBorderLeftTopRight("单价（元）", PdfPCell.ALIGN_CENTER, FontColumn,2));
			t10.addCell(makeCellSetColspanWithNoBorder("含税价款（元）", PdfPCell.ALIGN_CENTER, FontColumn,2));
//			t10.addCell(makeCellWithBorderRight("含税价款", PdfPCell.ALIGN_CENTER, FontColumn,2));
//			t10.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_CENTER, FontColumn,1));
			t9.addCell(t10);
			t9.addCell(makeCell("附注", PdfPCell.ALIGN_CENTER, FontColumn));
			
			int size=eqmts.size();
			double total=0;
			for(int i=0;i<size;i++){
				Map eqmt=eqmts.get(i);
				t9.addCell(makeCell(toString(eqmt,"THING_NAME"), PdfPCell.ALIGN_CENTER, FontDefault));
				t9.addCell(makeCell(toString(eqmt, "THING_KIND"), PdfPCell.ALIGN_CENTER, FontDefault));
				t9.addCell(makeCell(toString(eqmt, "MODEL_SPEC"), PdfPCell.ALIGN_CENTER, FontDefault));
				t9.addCell(makeCell(toString(eqmt, "THING_NUMBER"), PdfPCell.ALIGN_CENTER, FontDefault));
				t9.addCell(makeCell(toString(eqmt,"AMOUNT"), PdfPCell.ALIGN_CENTER, FontDefault));
				PdfPTable t11 = new PdfPTable(2);
				String shui_price="0";
				String unit_price="0";
				String lease_price="0";
				if(eqmt.get("SHUI_PRICE")!=null){
					shui_price=eqmt.get("SHUI_PRICE").toString();
				}
				if(eqmt.get("UNIT_PRICE")!=null){
					unit_price=eqmt.get("UNIT_PRICE").toString();
				}
				if(eqmt.get("LEASE_PRICE")!=null){
					lease_price=eqmt.get("LEASE_PRICE").toString();
				}
				total+=Double.parseDouble(shui_price);
//				total+=Double.parseDouble(lease_price);
				
				t11.addCell(makeCellWithBorderLeftTopRight(nfFSNum.format(Double.parseDouble(shui_price)), PdfPCell.ALIGN_RIGHT, new Font(bfChinese, 10, Font.NORMAL),2));
//				t11.addCell(makeCellWithBorderLeftTopRight(nfFSNum.format(Double.parseDouble(unit_price)), PdfPCell.ALIGN_RIGHT, new Font(bfChinese, 10, Font.NORMAL),1));
				
				t9.addCell(t11);
			    t9.addCell(makeCell("", PdfPCell.ALIGN_CENTER, FontDefault));

			}
//			t9.addCell(makeCellCol(" ", PdfPCell.ALIGN_CENTER, FontDefault,4));
			t9.addCell(makeCellSetColspanWithNoBorder("合计:"+nfFSNum.format(sumTotalPrice) + "            未税" +nfFSNum.format(sumUnitPrice), PdfPCell.ALIGN_RIGHT, FontDefault,8));
			t9.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_RIGHT, FontDefault,8));
//			PdfPTable t112 = new PdfPTable(4);
//			t112.addCell(makeCellWithBorderLeftTop(nfFSNum.format(sumTotalPrice), PdfPCell.ALIGN_RIGHT, new Font(bfChinese, 10, Font.NORMAL),1));
//			t112.addCell(makeCellWithBorderLeftTopRight(nfFSNum.format(sumUnitPrice), PdfPCell.ALIGN_RIGHT, new Font(bfChinese, 10, Font.NORMAL),1));
//			t9.addCell(t112);
//			t9.addCell(makeCell("", PdfPCell.ALIGN_CENTER, FontDefault));
			document.add(t9);
		    document.add(new Paragraph("\n"));

		    PdfPTable t12 = new PdfPTable(1);
		    t12.setWidthPercentage(100f);
			PdfPCell objCell6 = new PdfPCell();
		    Phrase phrase6 = new Phrase();
		    objCell6.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		    objCell6.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
		    objCell6.setBorder(0);
	    	Chunk chunk6 = new Chunk("	     依据贵公司和承租人签订的编号为",FontDefault);
	    	phrase6.add(chunk6);
	    	
//	    	if(InfoMap.get("PUCT_CODE")!=null){
//		    	chunk6 = new Chunk(toString(InfoMap, "PUCT_CODE"), FontUnder);
//	    	}else{
//	    		chunk6 = new Chunk("___________", FontDefault);
//	    	}
	    	if(InfoMap.get("LEASE_CODE")!=null){
	    		chunk6 = new Chunk("   "+toString(InfoMap, "LEASE_CODE"), FontUnder);
	    	}else{
	    		chunk6 = new Chunk("___________", FontDefault);
	    	}
	    	phrase6.add(chunk6);
	    	chunk6 = new Chunk("《委托购买合同》及相关协议，以及本司和承租人签订的编号为",FontDefault);
	    	phrase6.add(chunk6);
	    	
//	    	if(InfoMap.get("LEASE_CODE")!=null){
//	    		chunk6 = new Chunk(toString(InfoMap, "LEASE_CODE"), FontUnder);
//	    	}else{
//	    		chunk6 = new Chunk("___________", FontDefault);
//	    	}
	    	chunk6 = new Chunk("___________", FontDefault);
	    	
	    	
	    	phrase6.add(chunk6);
	    	chunk6 = new Chunk("《销售合同》，贵公司就上述机器设备需向本公司另行支付人民币",FontDefault);
	    	phrase6.add(chunk6);
	    	int yingfu= (int)( total - sumShuiPrice-Double.parseDouble(agprice)-Double.parseDouble(mcagprice));
//	    	int yingfu= (int)(sumShuiPrice + Double.parseDouble(agprice));
	    	chunk6 = new Chunk(nfFSNum.format(yingfu),FontUnder);
	    	phrase6.add(chunk6);
	    	chunk6 = new Chunk("元货款。",FontDefault);
	    	phrase6.add(chunk6);
	    	objCell6.addElement(phrase6);
	    	t12.addCell(objCell6);
			document.add(t12);
			
			PdfPTable t13 = new PdfPTable(3);
			t13.setWidthPercentage(100f);
			t13.addCell(makeCellSetColspanWithNoBorder("	     特此确认", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault, 4));
			t13.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder("法定代表人或授权人：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			t13.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_RIGHT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder("公司：        "+toString(InfoMap,"SUPL_NAME"), PdfPCell.ALIGN_LEFT, FontDefault, 2));
			t13.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder("日期："+"      "+"年"+"      "+"月"+"      "+"日", PdfPCell.ALIGN_LEFT, FontDefault, 2));
			t13.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault, 3));
			t13.addCell(makeCellSetColspanWithNoBorder("", PdfPCell.ALIGN_LEFT, FontDefault, 1));
			t13.addCell(makeCellSetColspanWithNoBorder("              （公司盖章）     ", PdfPCell.ALIGN_CENTER, FontDefault,2));
			document.add(t13);
			
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition", "attachment; filename=querenhanWTZL.pdf");
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o);
			o.flush();
			o.close();
			
			//add by ShenQi 插入系统日志
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
			   		 "导出 供应商确认函(自备款收款确认)",
		   		 	 "合同浏览导出 供应商确认函(自备款收款确认)",
		   		 	 null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
		   		 	 1,
		   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
		   		 	 DataUtil.longUtil(0),
		   		 	 context.getRequest().getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 创建 没有左右上 单元格 */
	public static PdfPCell makeCellWithBorderLeftTopRight(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorder(0);
	    objCell.setColspan(colspan);
	    return objCell;
	}
	/** 创建 没有左右上 单元格 */
	public static PdfPCell makeCellWithBorderBottom(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
	    objCell.setBorderWidthRight(0);
	    objCell.setColspan(colspan);
	    return objCell;
	}
	
	
	public static PdfPCell CellSetColspanWithBorderLeftTopRight(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthLeft(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
		objCell.setColspan(colspan);
	    return objCell;
	}
	/** 创建 没有左上 单元格 */
	public static PdfPCell makeCellWithBorderLeftTop(String content, int align, Font FontDefault, int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthLeft(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setColspan(colspan);
	    return objCell;
	}
	
	/** 创建 没有右上 单元格 */
	public static PdfPCell makeCellWithBorderRightTop(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(20);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthRight(0);
		objCell.setBorderWidthTop(0);
		return objCell;
	}
	/**
	 * 创建 有边框 合并 单元格 无上下边
	 * 
	 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellSetColspan2(String content, int align,
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
	 * 创建 有边框 合并 单元格 无下边
	 * 
	 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellSetColspan3(String content, int align,
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
	 * 创建 有边框 合并 单元格 无上边
	 * 
	 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellSetColspan4(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthTop(0);

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

	/** 创建 只有左边框 单元格 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellWithBorderLeft(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);

		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthRight(0);
		objCell.setColspan(colspan);
		return objCell;
	}

	/** 创建 只有右边框 单元格 */
	private static PdfPCell makeCellWithBorderRight(String content, int align,
			Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);

		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthLeft(0);
		objCell.setColspan(colspan);
		return objCell;
	}

	/** 创建 只有左右边框 单元格 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellWithBorder(String content, int align,
			Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(23f);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthLeft(1f);
		objCell.setBorderWidthRight(1f);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
		return objCell;
	}

	/** 创建 没有顶边 单元格 */
	@SuppressWarnings("unused")
	private static PdfPCell makeCellOnlyBottom(String content, int align,
			Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(23f);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorderWidthBottom(1f);
		objCell.setBorderWidthLeft(1f);
		objCell.setBorderWidthRight(1f);
		objCell.setBorderWidthTop(0);

		return objCell;
	}

	@SuppressWarnings("unused")
	private static PdfPCell makeCellSetColspan2ForOne(String content,
			int align, Font FontDefault, int colspan) {
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

	@SuppressWarnings("unused")
	private static PdfPCell makeCellWithBorderLeftForOne(String content,
			int align, Font FontDefault) {
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

	@SuppressWarnings("unused")
	private static PdfPCell makeCellWithBorderRightForOne(String content,
			int align, Font FontDefault) {
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

	// make a PdfPCell ,for insert into pdf.
	private static PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		return objCell;
	}
	private static PdfPCell makeCellCol(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setColspan(colspan);
		return objCell;
	}

	@SuppressWarnings("unchecked")
	private	static String toString(Map map,String key){
		return map.get(key)==null?"":map.get(key).toString();
	}

}
