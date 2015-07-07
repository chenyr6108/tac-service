package com.brick.settle.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.contract.util.PasswordControlTablePDF;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SettlePDF extends AService {
static Log logger = LogFactory.getLog(PasswordControlTablePDF.class) ;
	
	
	@SuppressWarnings("unchecked")
	public static void expSettlePDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePDF" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			//取出数据
			content = (Map) DataAccessor.query("settleManage.showSettle", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
			content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("TITLE_NAME1","客户结清数据明细表");
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
			if(content.get("START_DATE") != null ){
				content.put("START_DATE",sf.format(content.get("START_DATE"))) ;
			}
			if(content.get("END_DATE") != null){
				content.put("END_DATE",sf.format(content.get("END_DATE"))) ;
			}
			if(content.get("SETTLE_DATE") != null){
				content.put("SETTLE_DATE",sf.format(content.get("SETTLE_DATE"))) ;
			}
			
			
			
			//调用模型
			model(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	/**
	 * 2012/02/01 Yang Yun
	 * 导出空白明细
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static void expBlankSettlePDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePDF" ;
		try{
			//设置数据
			Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			context.contextMap.put("lawyfee", "法务费用") ;
			content = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if (content == null) {
				content = new HashMap<String, Object>();
			}
			content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			
			String rectId = LeaseUtil.getRectIdByRecpId((String)context.contextMap.get("RECP_ID"));
			String creditId = String.valueOf(LeaseUtil.getCreditIdByRectId(rectId));
			String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
			String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
			if("7".equals(contractType)){
				companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
			}
			content.put("contractType", contractType);
			content.put("companyCode", companyCode);
			Double total = new Double(0);
			total = (content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
					(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX"))+
					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
			content.put("total", total);
			content.put("TITLE_NAME",companyName);
			content.put("TITLE_NAME1","客户结清数据明细表");

			//调用模型
			blankModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	/**
	 * 2012/02/01 Yang Yun
	 * 导出空白明细
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public static void expBlankSettlePDFNew(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePDF" ;
		try{
			//设置数据
			Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			context.contextMap.put("lawyfee", "法务费用") ;
			content = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if (content == null) {
				content = new HashMap<String, Object>();
			}
			content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			Double total = new Double(0);
			total = (content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
					(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX"))+
					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
			content.put("total", total);
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("TITLE_NAME1","客户结清数据明细表");
			
			//调用模型
			blankModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	
	/*
	 * Add by Michael 2012 10-08
	 * 导出结清金额通知函	
	 */
	public static void expSettlePayNotePDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePayNotePDF" ;
		try{
			//设置数据
			Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			content = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if (content == null) {
				content = new HashMap<String, Object>();
			}
			content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			Double total = new Double(0);
			total = (content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
					(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX"))+
					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
			content.put("total", total);
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("TITLE_NAME1","通知函");
			
			//调用模型
			settlePayNoteModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	/*
	 * 导出结清金额通知函(新)	
	 */
	public static void expSettlePayNotePDFNew(Context context, Map content){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePayNotePDF" ;
		try{
			//设置数据
			//Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			//content = (Map) DataAccessor.query("settleManage.selectSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
//			if (content == null) {
//				content = new HashMap<String, Object>();
//			}
			//content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			content.put("readpay", content) ;
			
//			Double total = new Double(0);
//			total = (content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
//					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
//					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
//					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
//					(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX"))+
//					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
//			content.put("total", total);
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("TITLE_NAME1","通知函");
			
			//调用模型
			settlePayNoteModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	
	public static void model(Map content,OutputStream baos) throws Exception {
		Map readPay = (Map) content.get("readpay") ;
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
		//pdf名字
	 	
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
        
        
    
        PdfWriter.getInstance(document, baos);
        // 打开文档
        document.open();
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(15);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{14,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	//结清日期
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 9)) ;
	 	t1.addCell(makeCell(bfChinese, "结清日期："+(content.get("SETTLE_DATE")== null ? "" :content.get("SETTLE_DATE").toString()) , new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
		 
	 	//第一行 合同编号 承租人  组织机构证  租赁期数  已缴纳期数
	 	t1.addCell(makeCell(bfChinese, "合同编号", fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "承租人", fontDefault, paddingDefault, borderStart, alignCenter, 5)) ;
	 	t1.addCell(makeCell(bfChinese, "组织机构证", fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "租赁期数", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "已缴期数", fontDefault, paddingDefault, borderEnd, alignCenter, 2)) ;
	 	
	 	//第二行 
	 	t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_CODE")== null ? " " : readPay.get("LEASE_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("CUST_NAME")== null ? " " : readPay.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter,5)) ;
	 	t1.addCell(makeCell(bfChinese,  readPay.get("CORP_ORAGNIZATION_CODE")== null ? " " : readPay.get("CORP_ORAGNIZATION_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_PERIOD")== null ? " " : readPay.get("LEASE_PERIOD").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese,  content.get("HAS_PERIOD_NUM")== null ? " " : content.get("HAS_PERIOD_NUM").toString(), fontDefault, paddingDefault, borderEnd,alignCenter, 2)) ;
		
	 	//第三行 结清明细  失算金额  实收金额  付款方式
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "试算金额", fontU, paddingDefault,new float[]{0.5f,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "实收金额", fontU, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "付款方式", fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignCenter, 4)) ;
		
		//第四行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "本      金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0} ,alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("OWN_PRICE")== null ? "__________" :  nfFSNum.format(content.get("OWN_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_OWN_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_OWN_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "汇      款", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("REMIT")== null ? " " : nfFSNum.format(content.get("REMIT")), fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		
		//第五行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "结", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "利      息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("REN_PRICE")== null && content.get("REN_PRICE").equals("0")? "__________" : nfFSNum.format(content.get("REN_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_REN_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_REN_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "现      金", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("CASH")== null ? " " : nfFSNum.format(content.get("CASH")), fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		
		//Add by Michael 2011 12/30 修改导出结清明细表
		//第五行 结清明细  罚息   实收    票据
		t1.addCell(makeCell(bfChinese, "清", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "罚     息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("DUN_PRICE")== null ? "" : nfFSNum.format(content.get("DUN_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_DUN_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_DUN_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "票      据", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOTE")== null ? " " : nfFSNum.format(content.get("NOTE")), fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
				
		//第六行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "违  约  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("BREACH_PRICE")== null ? "" : nfFSNum.format(content.get("BREACH_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_BREACH_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_BREACH_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "其      他", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("OTHER")== null ? " " : nfFSNum.format( content.get("OTHER") ), fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
			
		//第七行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "明", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "损  害  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("DAMAGE_PRICE")== null ? "" : nfFSNum.format(content.get("DAMAGE_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_DAMAGE_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_DAMAGE_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第八行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "细", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "期满购买金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("STAYBUY_PRICE")== null ? "" : nfFSNum.format(content.get("STAYBUY_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_STAYBUY_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_STAYBUY_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "法务费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("TOTAL_LAWYFEE")== null ? "" : nfFSNum.format(content.get("TOTAL_LAWYFEE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_TOTAL_LAWYFEE")== null ? " " : nfFSNum.format(content.get("REAL_TOTAL_LAWYFEE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "其他费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("OTHER_PRICE")== null ? "" : nfFSNum.format(content.get("OTHER_PRICE")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_REN_PRICE")== null ? " " : nfFSNum.format(content.get("REAL_OTHER_PRICE")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "合      计", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("TOTAL")== null ? "" : nfFSNum.format(content.get("TOTAL")), fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, content.get("REAL_TOTAL")== null ? " " : nfFSNum.format(content.get("REAL_TOTAL")), fontU, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 5)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "撤", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "张数：" + (content.get("BACKOUT_COUNT")== null ? " " : content.get("BACKOUT_COUNT").toString()), fontDefault, paddingDefault, borderStart,alignDefault, 7)) ;
		t1.addCell(makeCell(bfChinese, "票面总额：" + (content.get("BACKOUT_TOTAL")== null ? " " : content.get("BACKOUT_TOTAL").toString()), fontDefault, paddingDefault,borderEnd,alignDefault, 7)) ;
		//第十一行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "票", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "到期起迄：      " + (content.get("START_DATE")== null ? "20     /     / " : content.get("START_DATE").toString())
				+ "   ~   " + (content.get("END_DATE")== null ? "20     /     / " : content.get("END_DATE").toString())
				, fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		//第十二行 
		t1.addCell(makeCell(bfChinese, "成", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "1、未摊提保费" , fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		//第十三行 
		t1.addCell(makeCell(bfChinese, "本", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "2、其他未摊成本" , fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		
		t1.addCell(makeCell(bfChinese, "未       收       款       项       说       明", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "未收款金额：" , fontDefault, paddingDefault, borderStart,alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOT_RECEIVED_PRICE")== null ? " " : content.get("NOT_RECEIVED_PRICE").toString() , fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignDefault, 11)) ;		
		
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0.5f,0,0,0},alignCenter, 15)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;

		//Add by Michael 2012 02-23 增加2行空白栏位
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		
		t1.addCell(makeCell(bfChinese, "部门经办：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "单位主管：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "处/部级主管：____________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		
		//Add by Michael 2012 02-23  增加业管部主管签字栏位
		t1.addCell(makeCell(bfChinese, "业管部主管：______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "总  经  理：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		//t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "财  务  部：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		
		
		document.add(t1);
		document.close();
	}
	
	/**
	 * 2012/02/01 Yang Yun
	 * 导出空白明细
	 */
	public static void blankModel(Map content,OutputStream baos) throws Exception {
		Map readPay = (Map) content.get("readpay") ;
		
		
		
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
		//pdf名字
	 	
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
        
        
    
        PdfWriter.getInstance(document, baos);
        // 打开文档
        document.open();
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(16);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 16)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{14,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 16)) ;
	 	//结清日期
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 10)) ;
	 	t1.addCell(makeCell(bfChinese, "结清日期：", new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
		 
	 	//第一行 合同编号 承租人  组织机构证  租赁期数  已缴纳期数
	 	t1.addCell(makeCell(bfChinese, "合同编号", fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "承租人", fontDefault, paddingDefault, borderStart, alignCenter, 6)) ;
	 	t1.addCell(makeCell(bfChinese, "组织机构证", fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "租赁期数", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "已缴期数", fontDefault, paddingDefault, borderEnd, alignCenter, 2)) ;
	 	
	 	//第二行 
	 	t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_CODE")== null ? " " : readPay.get("LEASE_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("CUST_NAME")== null ? " " : readPay.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter,6)) ;
	 	t1.addCell(makeCell(bfChinese,  readPay.get("CORP_ORAGNIZATION_CODE")== null ? " " : readPay.get("CORP_ORAGNIZATION_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_PERIOD")== null ? " " : readPay.get("LEASE_PERIOD").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese,  content.get("REAL_PERIOD")== null ? " " : content.get("REAL_PERIOD").toString(), fontDefault, paddingDefault, borderEnd,alignCenter, 2)) ;
		
	 	//第三行 结清明细  失算金额  实收金额  付款方式
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "试算金额", fontU, paddingDefault,new float[]{0.5f,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "实收金额", fontU, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "付款方式", fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignCenter, 3)) ;
		//补增值税
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "增值税", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("SUM_VALUE_ADDED_TAX")== null ? "_________" : nfFSNum.format(content.get("SUM_VALUE_ADDED_TAX")), content.get("SUM_VALUE_ADDED_TAX")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		
		
		//第四行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "本      金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0} ,alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("SUM_OWN_PRICE")== null ? "_________" :  nfFSNum.format(content.get("SUM_OWN_PRICE")), content.get("SUM_OWN_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "汇      款", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("REMIT")== null ? "________" : nfFSNum.format(content.get("REMIT")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		
		//第五行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "结", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "利      息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("SUM_REN_PRICE")== null || content.get("SUM_REN_PRICE").equals("0")? "_________" : nfFSNum.format(content.get("SUM_REN_PRICE")), content.get("SUM_REN_PRICE")== null || content.get("SUM_REN_PRICE").equals("0")? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "现      金", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("CASH")== null ? "_________" : nfFSNum.format(content.get("CASH")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		
		//Add by Michael 2011 12/30 修改导出结清明细表
		//第五行 结清明细  罚息   实收    票据
		t1.addCell(makeCell(bfChinese, "清", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "罚     息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("DUN_PRICE")== null ? "_________" : nfFSNum.format(content.get("DUN_PRICE")), content.get("DUN_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "票      据", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOTE")== null ? "_________" : nfFSNum.format(content.get("NOTE")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
				
		//第六行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "违  约  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("BREACH_PRICE")== null ? "_________" : nfFSNum.format(content.get("BREACH_PRICE")), content.get("BREACH_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, "其      他", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("OTHER")== null ? "_________" : nfFSNum.format( content.get("OTHER") ), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
			
		//第七行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "明", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "损  害  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("DAMAGE_PRICE")== null ? "_________" : nfFSNum.format(content.get("DAMAGE_PRICE")), content.get("DAMAGE_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		//第八行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "细", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "期满购买金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("LGJ")== null ? "_________" : nfFSNum.format(content.get("LGJ")), content.get("LGJ")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "法务费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("TOTAL_LAWYFEE")== null ? "_________" : nfFSNum.format(content.get("TOTAL_LAWYFEE")), content.get("TOTAL_LAWYFEE")== null ? fontDefault :fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "其他费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, content.get("OTHER_PRICE")== null ? "_________" : nfFSNum.format(content.get("OTHER_PRICE")), content.get("OTHER_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "合      计", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese,content.get("total")== null ? "_________" : nfFSNum.format(content.get("total")), content.get("total")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "_________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 3)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "撤", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "张数：" + (content.get("BACKOUT_COUNT")== null ? " " : content.get("BACKOUT_COUNT").toString()), fontDefault, paddingDefault, borderStart,alignDefault, 8)) ;
		t1.addCell(makeCell(bfChinese, "票面总额：" + (content.get("BACKOUT_TOTAL")== null ? " " : content.get("BACKOUT_TOTAL").toString()), fontDefault, paddingDefault,borderEnd,alignDefault, 7)) ;
		//第十一行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "票", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "到期起迄：      " + (content.get("START_DATE")== null ? "20     /     / " : content.get("START_DATE").toString())
				+ "   ~   " + (content.get("END_DATE")== null ? "20     /     / " : content.get("END_DATE").toString())
				, fontDefault, paddingDefault, borderEnd,alignDefault, 15)) ;
		//第十二行 
		t1.addCell(makeCell(bfChinese, "成", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "1、未摊提保费" , fontDefault, paddingDefault, borderEnd,alignDefault, 15)) ;
		//第十三行 
		t1.addCell(makeCell(bfChinese, "本", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "2、其他未摊成本" , fontDefault, paddingDefault, borderEnd,alignDefault, 15)) ;
		
		t1.addCell(makeCell(bfChinese, "未       收       款       项       说       明", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "未收款金额：" , fontDefault, paddingDefault, borderStart,alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOT_RECEIVED_PRICE")== null ? " " : content.get("NOT_RECEIVED_PRICE").toString() , fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignDefault, 11)) ;
		
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0.5f,0,0,0},alignCenter, 16)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		
		//Add by Michael 2012 02-23 增加2行空白栏位
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;

		t1.addCell(makeCell(bfChinese, "部门经办：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		t1.addCell(makeCell(bfChinese, "单位主管：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		t1.addCell(makeCell(bfChinese, "区部主管：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		t1.addCell(makeCell(bfChinese, "处级主管：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		
		//Add by Michael 2012 02-23  增加业管部主管签字栏位
		t1.addCell(makeCell(bfChinese, "业  管  处：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;		
		t1.addCell(makeCell(bfChinese, "总  经  理：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		String contractType = (String) content.get("contractType");
		int companyCode = (Integer) content.get("companyCode");
		if("7".equals(contractType) && companyCode==2){
			t1.addCell(makeCell(bfChinese, "管 理  部：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		}else{
			t1.addCell(makeCell(bfChinese, "财  管  部：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		}
		t1.addCell(makeCell(bfChinese, "经  管  处：________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 4)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 16)) ;
		
		document.add(t1);
		document.close();
	}
	
	/*
	 * Add by Michael 2012 10-08
	 * 增加结清金额通知函
	 */
	public static void settlePayNoteModel(Map content,OutputStream baos) throws Exception {
		Map readPay = (Map) content.get("readpay") ;
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	int[] fontB = {-1,Font.BOLD} ;
	 	
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
	 	int [] alignRight = {PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_RIGHT} ;//居右
		//pdf名字
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
    
        PdfWriter.getInstance(document, baos);
        // 打开文档
        document.open();
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(15);
	 	//日期格式
	 	DateFormat df = new SimpleDateFormat("yyyy年M月d日"); 
	 	//总租金
	 	//Double totalRenPrice=DataUtil.doubleUtil(content.get("SUM_OWN_PRICE"))+DataUtil.doubleUtil(content.get("SUM_REN_PRICE"))+DataUtil.doubleUtil(content.get("SUM_VALUE_ADDED_TAX"));
	 	Double totalRenPrice = DataUtil.doubleUtil(content.get("rent"));
	 	
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{14,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "致：", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, readPay.get("CUST_NAME")== null ? " " : readPay.get("CUST_NAME").toString(), fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 14)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "       就贵司(户)与我司所签署合同编号为",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 7)) ;
	 	t1.addCell(makeCell(bfChinese, readPay.get("LEASE_CODE")== null ? " " : readPay.get("LEASE_CODE").toString(),fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "的融资租赁合同结清事宜，",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "通知如下：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "一、至",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, df.format(new Date()),fontU, paddingDefault,  new float[]{0,0,0,0},alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "止结清金额为：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 10)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "       租金：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(totalRenPrice),fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;

	 	t1.addCell(makeCell(bfChinese, "       期满购买金：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("LGJ")== null ? "__________" : nfFSNum.format(content.get("LGJ")), content.get("LGJ")== null ? fontDefault : fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "       罚息：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("DUN_PRICE")== null ? "__________" : nfFSNum.format(content.get("DUN_PRICE")), content.get("DUN_PRICE")== null ? fontDefault : fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "       违约金：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("BREACH_PRICE")== null ? "__________" : nfFSNum.format(content.get("BREACH_PRICE")), content.get("BREACH_PRICE")== null ? fontDefault : fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;
	
	 	t1.addCell(makeCell(bfChinese, "       法务费用：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TOTAL_LAWYFEE")== null ? "__________" : nfFSNum.format(content.get("TOTAL_LAWYFEE")), content.get("TOTAL_LAWYFEE")== null ? fontDefault :fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;

	 	t1.addCell(makeCell(bfChinese, "       其他费用：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("OTHER_PRICE")== null ? "__________" : nfFSNum.format(content.get("OTHER_PRICE")), content.get("OTHER_PRICE")== null ? fontDefault : fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。(含：                                                                                 )",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "合计结清金额：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 3)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("total")== null ? "__________" : nfFSNum.format(content.get("total")), content.get("total")== null ? fontDefault : fontU, paddingDefault,  new float[]{0,0,0,0},alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "元。",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 9)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "二、请贵司将上述合计结清金额，汇入我司账户并将汇款水单传真至我司后来电确认：",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "    联络电话：0512-80983566                           传真号码：0512-80983567",fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "┏  上表所列结清金额，于七日内有效，逾七日因罚息及违约金变动，须增补差额，",fontB, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
	 	t1.addCell(makeCell(bfChinese, "特此提醒  ┛",fontB, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
	 	
		t1.addCell(makeCell(bfChinese, "    此      致   ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
		//Add by Michael 增加2行空白栏位
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		
		t1.addCell(makeCell(bfChinese, "通知人："+Constants.COMPANY_NAME,fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 15)) ;
		t1.addCell(makeCell(bfChinese, df.format(new Date()),fontDefault, paddingDefault,  new float[]{0,0,0,0},alignRight, 15)) ;
		document.add(t1);
		document.close();
	}
	
	/**
	 * @author michael
	 * @param context
	 * Add by Michael 2012 12-21 增加导出预估结清数据明细
	 */
	public static void expForcastSettlePDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "forcastSettlePDF" ;
		try{
			//设置数据
			Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			context.contextMap.put("lawyfee", "法务费用") ;
			content = (Map) DataAccessor.query("settleManage.selectForcastSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			if (content == null) {
				content = new HashMap<String, Object>();
			}
			content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			Double total = new Double(0);
			total =	(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX")) + 
					(content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
			content.put("total", total);
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("TITLE_NAME1","客户预估结清数据明细表");
			
			content.put("QUERY_DATE", context.contextMap.get("QUERY_DATE"));
			//调用模型
			forcastSettleModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	/**
	 * 预估结清打印PDF
	 * @param context
	 */
	public static void expAdvanceSettlePDF(Context context, Map content){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "forcastSettlePDF" ;
		try{
			//设置数据
			//Map content = null;
			//取出数据
			context.contextMap.put("zujin", "租金") ;
			context.contextMap.put("zujinfaxi", "租金罚息") ;
			context.contextMap.put("sblgj", "设备留购价") ;
			context.contextMap.put("lawyfee", "法务费用") ;
			
			//content = (Map) DataAccessor.query("settleManage.selectForcastSettlePrice", context.contextMap,DataAccessor.RS_TYPE.MAP);
			
//			if (content == null) {
//				content = new HashMap<String, Object>();
//			}
			//content.put("readpay", (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			content.put("readpay", content) ;
			
//			Double total = new Double(0);
//			total =	(content.get("SUM_VALUE_ADDED_TAX") == null ? 0 : (Double)content.get("SUM_VALUE_ADDED_TAX")) + 
//					(content.get("SUM_OWN_PRICE") == null ? 0 : (Double)content.get("SUM_OWN_PRICE")) + 
//					(content.get("SUM_REN_PRICE") == null ? 0 : (Double)content.get("SUM_REN_PRICE")) + 
//					(content.get("LGJ") == null ? 0 : (Double)content.get("LGJ")) + 
//					(content.get("DUN_PRICE") == null ? 0 : (Double)content.get("DUN_PRICE"))+
//					(content.get("TOTAL_LAWYFEE") == null ? 0 : (Double)content.get("TOTAL_LAWYFEE"));
//			content.put("total", total);
			content.put("TITLE_NAME",Constants.COMPANY_NAME);
			content.put("type", context.contextMap.get("type"));
			if(!StringUtils.isEmpty(content.get("type"))) {
				content.put("TITLE_NAME1","客户结清数据明细表");
			} else {
				content.put("TITLE_NAME1","客户预估结清数据明细表");
			}
			
			content.put("QUERY_DATE", context.contextMap.get("QUERY_DATE"));
			//调用模型
			forcastSettleModel(content,baos) ;
			//
	   	    String strFileName = pdfName+".pdf";
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
	   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	
	public static void forcastSettleModel(Map content,OutputStream baos) throws Exception {
		Map readPay = (Map) content.get("readpay") ;
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
		//pdf名字
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
        
        PdfWriter.getInstance(document, baos);
        // 打开文档
        document.open();
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(15);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{14,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 15)) ;
	 	//打印时间
	 	DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 	DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 9)) ;
	 	if(!StringUtils.isEmpty(content.get("type"))) {
	 		t1.addCell(makeCell(bfChinese, " ", new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
		 	//预估结清日期
		 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 9)) ;
		 	t1.addCell(makeCell(bfChinese, "结清日期：", new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
	 	} else {
		 	t1.addCell(makeCell(bfChinese, "列印时间："+format1.format(new Date()), new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
		 	//预估结清日期
		 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 9)) ;
		 	t1.addCell(makeCell(bfChinese, "预估结清日期："+content.get("QUERY_DATE"), new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 6)) ;
		 
	 	}
	 	//第一行 合同编号 承租人  组织机构证  租赁期数  已缴纳期数
	 	t1.addCell(makeCell(bfChinese, "合同编号", fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "承租人", fontDefault, paddingDefault, borderStart, alignCenter, 5)) ;
	 	t1.addCell(makeCell(bfChinese, "组织机构证", fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "租赁期数", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "已缴期数", fontDefault, paddingDefault, borderEnd, alignCenter, 2)) ;
	 	
	 	//第二行 
	 	t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_CODE")== null ? " " : readPay.get("LEASE_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("CUST_NAME")== null ? " " : readPay.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter,5)) ;
	 	t1.addCell(makeCell(bfChinese,  readPay.get("CORP_ORAGNIZATION_CODE")== null ? " " : readPay.get("CORP_ORAGNIZATION_CODE").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,  readPay.get("LEASE_PERIOD")== null ? " " : readPay.get("LEASE_PERIOD").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese,  content.get("REAL_PERIOD")== null ? " " : content.get("REAL_PERIOD").toString(), fontDefault, paddingDefault, borderEnd,alignCenter, 2)) ;
		
	 	//第三行 结清明细  失算金额  实收金额  付款方式
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, "试算金额", fontU, paddingDefault,new float[]{0.5f,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "实收金额", fontU, paddingDefault, new float[]{0.5f,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0.5f,0,0,0}, alignCenter, 2)) ;
		//t1.addCell(makeCell(bfChinese, "付款方式", fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignCenter, 4)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignCenter, 4)) ;
		
		//第四行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "结", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "本      金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0} ,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("SUM_OWN_PRICE")== null ? "__________" :  nfFSNum.format(content.get("SUM_OWN_PRICE")), content.get("SUM_OWN_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		//t1.addCell(makeCell(bfChinese, "汇      款", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		//t1.addCell(makeCell(bfChinese, content.get("REMIT")== null ? "__________" : nfFSNum.format(content.get("REMIT")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		t1.addCell(makeCell(bfChinese, content.get("REMIT")== null ? " " : nfFSNum.format(content.get("REMIT")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		
		//第五行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "利      息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, content.get("SUM_REN_PRICE")== null || content.get("SUM_REN_PRICE").equals("0")? "__________" : nfFSNum.format(content.get("SUM_REN_PRICE")), content.get("SUM_REN_PRICE")== null || content.get("SUM_REN_PRICE").equals("0")? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		//t1.addCell(makeCell(bfChinese, "现      金", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		//t1.addCell(makeCell(bfChinese, content.get("CASH")== null ? "__________" : nfFSNum.format(content.get("CASH")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		t1.addCell(makeCell(bfChinese, content.get("CASH")== null ? " " : nfFSNum.format(content.get("CASH")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		
		//增加一行增值税zhangbo
		t1.addCell(makeCell(bfChinese, "清", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "增值税", fontDefault, paddingDefault, new float[]{0,0,0.5f,0} ,alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("SUM_VALUE_ADDED_TAX")== null ? "__________" :  nfFSNum.format(content.get("SUM_VALUE_ADDED_TAX")), content.get("SUM_OWN_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		
		//Add by Michael 2011 12/30 修改导出结清明细表
		//第五行 结清明细  罚息   实收    票据
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "罚     息", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("DUN_PRICE")== null ? "__________" : nfFSNum.format(content.get("DUN_PRICE")), content.get("DUN_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		//t1.addCell(makeCell(bfChinese, "票      据", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		//t1.addCell(makeCell(bfChinese, content.get("NOTE")== null ? "__________" : nfFSNum.format(content.get("NOTE")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOTE")== null ? " " : nfFSNum.format(content.get("NOTE")), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
				
		//第六行 结清明细  本金   实收    汇款
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, "违  约  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese,content.get("BREACH_PRICE")== null ? "__________" : nfFSNum.format(content.get("BREACH_PRICE")), content.get("BREACH_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		//t1.addCell(makeCell(bfChinese, "其      他", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, content.get("OTHER")== null ? "__________" : nfFSNum.format( content.get("OTHER") ), fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
			
		//第七行 结清明细  本金   实收    汇款
//		t1.addCell(makeCell(bfChinese, "损  害  金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese,content.get("DAMAGE_PRICE")== null ? "__________" : nfFSNum.format(content.get("DAMAGE_PRICE")), content.get("DAMAGE_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第八行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "明", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "期满购买金", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("LGJ")== null ? "__________" : nfFSNum.format(content.get("LGJ")), content.get("LGJ")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "法务费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("TOTAL_LAWYFEE")== null ? "__________" : nfFSNum.format(content.get("TOTAL_LAWYFEE")), content.get("TOTAL_LAWYFEE")== null ? fontDefault :fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontU, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第九行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "细", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "其他费用", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, content.get("OTHER_PRICE")== null ? "__________" : nfFSNum.format(content.get("OTHER_PRICE")), content.get("OTHER_PRICE")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 2)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "合      计", fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese,content.get("total")== null ? "__________" : nfFSNum.format(content.get("total")), content.get("total")== null ? fontDefault : fontU, paddingDefault,new float[]{0,0,0,0},alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "__________", fontDefault, paddingDefault, new float[]{0,0,0,0},alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0}, alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, new float[]{0,0,0,0.5f},alignCenter, 4)) ;
		//第十行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "撤", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "张数：" + (content.get("BACKOUT_COUNT")== null ? " " : content.get("BACKOUT_COUNT").toString()), fontDefault, paddingDefault, borderStart,alignDefault, 7)) ;
		t1.addCell(makeCell(bfChinese, "票面总额：" + (content.get("BACKOUT_TOTAL")== null ? " " : content.get("BACKOUT_TOTAL").toString()), fontDefault, paddingDefault,borderEnd,alignDefault, 7)) ;
		//第十一行 结清明细  本金   实收    汇款
		t1.addCell(makeCell(bfChinese, "票", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "到期起迄：      " + (content.get("START_DATE")== null ? "20     /     / " : content.get("START_DATE").toString())
				+ "   ~   " + (content.get("END_DATE")== null ? "20     /     / " : content.get("END_DATE").toString())
				, fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		//第十二行 
		t1.addCell(makeCell(bfChinese, "成", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "1、未摊提保费" , fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		//第十三行 
		t1.addCell(makeCell(bfChinese, "本", fontDefault, paddingDefault,  new float[]{0,0,0.5f,0},alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "2、其他未摊成本" , fontDefault, paddingDefault, borderEnd,alignDefault, 14)) ;
		
		t1.addCell(makeCell(bfChinese, "未       收       款       项       说       明", fontDefault, paddingDefault,  borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "未收款金额：" , fontDefault, paddingDefault, borderStart,alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, content.get("NOT_RECEIVED_PRICE")== null ? " " : content.get("NOT_RECEIVED_PRICE").toString() , fontU, paddingDefault, new float[]{0.5f,0,0,0.5f},alignDefault, 11)) ;
		if(!StringUtils.isEmpty(content.get("type"))) {
			t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0.5f,0,0,0},alignCenter, 15)) ;
			t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
			t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
		} else {
			t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0.5f,0,0,0},alignCenter, 15)) ;
			t1.addCell(makeCell(bfChinese, "备注说明：以上费用不包括从列印时间"+format2.format(new Date())+"到预估结清日"+content.get("QUERY_DATE")+"期间所发生", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
			t1.addCell(makeCell(bfChinese, "的其他费用。", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 15)) ;
		}
		
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;
		//Add by Michael 2012 02-23 增加2行空白栏位
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;

		t1.addCell(makeCell(bfChinese, "部门经办：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "单位主管：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "处/部级主管：____________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;

		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignCenter, 15)) ;

		//Add by Michael 2012 02-23  增加业管部主管签字栏位
		t1.addCell(makeCell(bfChinese, "业管部主管：_____________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;		
		t1.addCell(makeCell(bfChinese, "总  经  理：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		//t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		t1.addCell(makeCell(bfChinese, "财  务  部：_______________", fontDefault, paddingDefault,  new float[]{0,0,0,0},alignDefault, 5)) ;
		
		
		document.add(t1);
		document.close();
	}
	
	private static PdfPCell makeCell(BaseFont bfChinese,String content,int[] fontType,float[] paddingType,
				float[] borderType,int[] alignType,int colspan) {
		 //BaseFont bfChinese //字体设置
	 	//字体自定义
		//int fontType[0]=0;//字体大小(一般设置成11,默认为11（标记-1）)
		//int fontType[1]=0;//字体（用系统函数Font下的参数（例如：Font.BOLD）,默认为Font.BOLD（标记-1））

		//字体位置
		 //float paddingTopF=0f;//离上边距距离paddingType[0]
		 //float paddingBottomF=0f;//离下边距距离paddingType [1]
		 //float paddingLeftF=0f;//离左边距距离paddingType [2]
		 //float paddingRightF=0f;//离右边距距离 paddingType[3]
	 
	 	
	 	//int alignHorizontal alignType[0]//水平位置（用系统函数PdfPCell的参数（居中：PdfPCell.ALIGN_CENTER,靠左：PdfPCell.ALIGN_LEFT或PdfPCell.LEFT,
	 							//靠右PdfPCell.ALIGN_RIGHT或PdfPCell.RIGHT,靠上PdfPCell.ALIGN_TOP或PdfPCell.TOP，靠下PdfPCell.ALIGN_BOTTOM或PdfPCell.BOTTOM）
	 								//默认为不设置（标记为-1））
	 	
	 	//int alignVertical   alignType[1]//垂直位置(同水平位置设定)默认为不设置（标记为-1）
	 
	 	//float borderTopF=0f;//上边框粗细
	 	//float borderBottomF=0f;//下边框粗细
	 	//float borderLeftF=0f;//左边框粗细
	 	//float borderRightF=0f;//右边框粗细
	 
	 
	 	//int colspan=0;合并单元格,默认为不设置（标记为-1）
	 	
	 	Font FontStyleDe=null;
	 	if(fontType[0]<=0f)
	 	{
	 		if(fontType[1]==-1f)
	 		{
	 			FontStyleDe = new Font(bfChinese, 11f, Font.BOLD);
	 		}
	 		else
	 		{
	 			FontStyleDe = new Font(bfChinese, 11f, fontType[1]);
	 		}
	 		
	 	}
	 	else
	 	{
	 		if(fontType[1]==-1f)
	 		{
	 			FontStyleDe = new Font(bfChinese, fontType[0], Font.BOLD);
	 		}
	 		else
	 		{
	 			FontStyleDe = new Font(bfChinese, fontType[0], fontType[1]);
	 		}
	 	} 
	 	
		Phrase objPhase = new Phrase(content, FontStyleDe);
		PdfPCell objCell = new PdfPCell(objPhase);
		
		
		if(paddingType[0]!=-1)
		{
			objCell.setPaddingTop(paddingType[0]);
		}
		if(paddingType[1]!=-1)
		{
			objCell.setPaddingBottom(paddingType[1]);
		}
		
		if(paddingType[2]!=-1)
		{
			objCell.setPaddingLeft(paddingType[2]);
		}
		if(paddingType[3]!=-1)
		{
			objCell.setPaddingRight(paddingType[3]);
		}
		
		objCell.setBorderWidthTop(borderType[0]);
		objCell.setBorderWidthBottom(borderType[1]);
		objCell.setBorderWidthLeft(borderType[2]);
		objCell.setBorderWidthRight(borderType[3]);
		
		if(alignType[0]!=-1)
		{
			objCell.setHorizontalAlignment(alignType[0]);
		}
		if(alignType[1]!=-1)
		{
			objCell.setVerticalAlignment(alignType[1]);
		}
		
		if(colspan!=-1)
		{
			objCell.setColspan(colspan);
		}

		return objCell;
	}
}
