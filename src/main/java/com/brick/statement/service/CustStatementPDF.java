package com.brick.statement.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.contract.util.PasswordControlTablePDF;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class CustStatementPDF extends AService {
static Log logger = LogFactory.getLog(PasswordControlTablePDF.class) ;
	
	
	@SuppressWarnings("unchecked")
	public static void expPDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "settlePDF" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			//导出多个pdf 开始
			String[] ids=context.request.getParameterValues("ids");
			 // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize,10, 10, 10, 10); // 其余4个参数，设置了页面的4个边距
	        PdfWriter.getInstance(document, baos);
	        // 打开文档  
	        document.open();
	        //导出多个pdf 结束
				
			//查询基本信息
			content.put("info", DataAccessor.query("statement.custStatementPDFInfo", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
			for(int i = 0 ; i<ids.length ; i++){
				//取出数据
				content.put("idsI", i) ;  //当前第几个        
				content.put("idsLen", ids.length) ;//共多少个
				context.contextMap.put("recp_id", ids[i]) ;
				
				content.put("info", (Map)DataAccessor.query("statement.custStatementPDFInfo",context.contextMap,DataAccessor.RS_TYPE.MAP)) ;
				//ficbItem='租金罚息'
				context.contextMap.put("ficbItem", "租金") ;
				content.put("list", (List)DataAccessor.query("statement.custStatementPDFList", context.contextMap, DataAccessor.RS_TYPE.LIST)) ;
				content.put("period", (Map)DataAccessor.query("statement.custStatementPDFPeriod", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
				content.put("rent", (Map)DataAccessor.query("statement.custStatementPDFRent", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
				//罚息   ficbItem='租金罚息'
				context.contextMap.put("ficbItem", "租金罚息") ;
				context.contextMap.put("zujin", "租金") ;
				content.put("fine", (Map)DataAccessor.query("statement.custStatementFine", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
				//法务费用
				context.contextMap.put("ficbItem", "法务费用") ;
				content.put("lawyFee", (Map)DataAccessor.query("statement.custStatementPDFLawyFee", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;				
				//回购价
				//Modify by michael 2012 1/10 修改留购款计算逻辑
				context.contextMap.put("staybuy", "设备留购价") ;
				content.put("staybuy", (Map)DataAccessor.query("statement.custStatementPDFStaybuy", context.contextMap, DataAccessor.RS_TYPE.MAP)) ;
				content.put("TITLE_NAME",Constants.COMPANY_NAME);
				content.put("TITLE_NAME1","客户对账单——还款明细");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd") ;
				//调用模型
				model(content,baos,document) ;
			}
			//关闭
			document.close();
			
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
	@SuppressWarnings("unchecked")
	public static void model(Map content,OutputStream baos,Document document) throws Exception {
		List list = (List) content.get("list") ;
		Map period = (Map) content.get("period") ;
		Map fine = (Map) content.get("fine") ;
		Map rent = (Map) content.get("rent") ;
		Map staybuy = (Map) content.get("staybuy") ;//留购价 和 用户名
		Map lawyFee=(Map) content.get("lawyFee") ; //法务费用
		
		if(list == null){
			list = new ArrayList() ;
		}
		if(period == null){
			period = new HashMap() ;
		}
		if(fine == null) {
			fine = new HashMap() ;
		}
		if(rent == null){
			rent = new HashMap() ; 
		}
		if(staybuy == null){
			staybuy = new HashMap() ;
		}
		if(lawyFee== null){
			lawyFee= new HashMap() ;
		}
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	float[] borderNo = {0,0,0,0} ;
	 	//定义默认字体
	 	int[] fontDefault = {9,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
	 	int [] alignRight = {PdfPCell.ALIGN_RIGHT,-1} ;//靠右 
		//pdf名字
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(9);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 9)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{16,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 9)) ;
	 	//合约编号
	 	if(content.get("info") == null || ((Map)content.get("info")).size() <= 0){
	 		content.put("info",new HashMap()) ;
	 	}
	 	t1.addCell(makeCell(bfChinese, "合约编号： " + (((Map)content.get("info")).get("LEASE_CODE") == null ? " ":((Map)content.get("info")).get("LEASE_CODE").toString()), fontDefault, paddingDefault, borderNo,alignDefault, 9)) ;
	 	t1.addCell(makeCell(bfChinese, "客户姓名： " + (((Map)content.get("info")).get("CUST_NAME") == null ? " ":((Map)content.get("info")).get("CUST_NAME").toString()), fontDefault, paddingDefault, borderNo,alignDefault, 9)) ;
	 	String type = "" ;
	 	if(((Map)content.get("info")).get("FUND_STATUS") != null ){
	 		type =((Map)content.get("info")).get("FUND_STATUS") + "" ;
	 		//正常1正常结清2提前结清3回购
	 		if(type.equals("0")){
	 			type = "正常" ;
	 		} else if(type.equals("1")){
	 			type = "正常结清" ;
	 		} else if(type.equals("2")){
	 			type = "提前结清" ;
	 		} else if(type.equals("3")){
	 			type = "回购" ;
	 		}
	 	}
	 	t1.addCell(makeCell(bfChinese, "结清状态： " + type, fontDefault, paddingDefault, borderNo,alignDefault, 7)) ;
	 	t1.addCell(makeCell(bfChinese, "应还其他费用： " , fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;

	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignDefault, 9)) ;
	 	//第一行 期数 应还日 应还起付款  应还迟延费 实还日  实还期付款 实还迟延费  实还其他费   实还合计  
	 	t1.addCell(makeCell(bfChinese, "期数", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "应还日", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "应还期付款", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "应还迟延费", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "实还日", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "实还期付款", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "实还迟延费", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "实还其他费", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "实还合计", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	
	 	
	 	Double SUMShouldPrice = 0.0 ;
	 	Double SUMFine = 0.0 ;
	 	Double SUMRealPrice = 0.0 ;
	 	Double SUMRealFine = 0.0 ;
	 	Double SUMOther = 0.0 ;
	 	Double SUMHe = 0.0 ;
	 	String recpCode = null ;
	 	for (int i=0 ; i < list.size() ;i ++){
	 		Map temp = (Map) list.get(i) ;
//	 		if(recpCode == null || !(recpCode.equals(temp.get("LEASE_CODE")))){
//	 			recpCode = temp.get("LEASE_CODE") + "" ;
//	 		}
	 		t1.addCell(makeCell(bfChinese, temp.get("RECD_PERIOD")== null ? "" : temp.get("RECD_PERIOD").toString(), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, temp.get("PAY_DATE")== null ? "" :temp.get("PAY_DATE").toString(), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, nfFSNum.format(temp.get("SHOULD_PRICE")== null ? "" :temp.get("SHOULD_PRICE")), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, "0.0", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, temp.get("CHECK_DATE")== null ? "" :temp.get("CHECK_DATE").toString(), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, nfFSNum.format(temp.get("REAL_PRICE")== null ? "" :temp.get("REAL_PRICE")), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, "0.0", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, "0.0", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		 	t1.addCell(makeCell(bfChinese, nfFSNum.format(temp.get("HE")== null ? "" :temp.get("HE")), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 		
			SUMShouldPrice += (Double)temp.get("SHOULD_PRICE") ;
//			SUMFine += Double.valueOf(temp.get("FINE").toString());
		 	SUMRealPrice += Double.valueOf((String) temp.get("REAL_PRICE").toString())  ;
//		 	SUMRealFine += Double.valueOf((String) temp.get("REAL_FINE").toString())  ;
//		 	SUMOther += Double.valueOf((String) temp.get("OTHER").toString())  ;
		 	SUMHe += Double.valueOf(temp.get("HE").toString())  ;
	 	}
	 	t1.addCell(makeCell(bfChinese, "合计:", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
		t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMShouldPrice), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMFine), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMRealPrice), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMRealFine), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMOther), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(SUMHe), fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "---------------------------------------------------------------------------------------------------------------------------------------", fontDefault, paddingDefault, borderNo,alignDefault, 9)) ;
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignDefault, 9)) ;
	 	
	 	
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignDefault, 5)) ;
	 	t1.addCell(makeCell(bfChinese,"应还", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"已还", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"未还", fontDefault, paddingDefault, borderNo,alignCenter	, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"", fontDefault, paddingDefault, borderNo,alignRight, 1)) ;
	 	
	 	
	 	t1.addCell(makeCell(bfChinese, "应还期数：", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, (period.get("PERIOD_NUM") == null? " " :period.get("PERIOD_NUM").toString())  + "期", fontDefault, paddingDefault, borderNo,alignRight, 1)) ;
//	 	t1.addCell(makeCell(bfChinese, "期", fontDefault, paddingDefault, borderNo,alignRight, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"期付款：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(rent.get("YING_RENT") == null? 0.0 :rent.get("YING_RENT")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(rent.get("YI_RENT") == null? 0.0 :rent.get("YI_RENT")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(rent.get("ZONG_RENT") == null? 0.0 :rent.get("ZONG_RENT")) + "", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "实还至第：", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese, (period.get("RECD_PERIOD") == null? " " :period.get("RECD_PERIOD").toString())  + "期", fontDefault, paddingDefault, borderNo,alignRight, 1)) ;
//	 	t1.addCell(makeCell(bfChinese, "期", fontDefault, paddingDefault, borderNo,alignRight, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"", fontDefault, paddingDefault, borderNo,alignDefault, 1)) ;
	 	t1.addCell(makeCell(bfChinese,"罚息：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
//	 	t1.addCell(makeCell(bfChinese, fine.get("YING_FINE") == null? "" :nfFSNum.format(fine.get("YING_FINE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
//	 	t1.addCell(makeCell(bfChinese, fine.get("YI_FINE") == null? "" : nfFSNum.format(fine.get("YI_FINE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
//	 	t1.addCell(makeCell(bfChinese, fine.get("ZONG_FINE") == null? "" :nfFSNum.format(fine.get("ZONG_FINE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
//		Modify by Michael 2012 1/11 用新逻辑计算罚息	 	
	 	t1.addCell(makeCell(bfChinese, fine.get("DUN_PRICE") == null? "" :nfFSNum.format(fine.get("DUN_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, fine.get("YI_DUN_PRICE") == null? "" : nfFSNum.format(fine.get("YI_DUN_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, fine.get("WEI_DUN_PRICE") == null? "" :nfFSNum.format(fine.get("WEI_DUN_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese,"损害金：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignCenter, 4)) ;

	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese,"法务费用：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, lawyFee.get("SUM_LAWYFEE") == null? "" :nfFSNum.format(lawyFee.get("SUM_LAWYFEE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, lawyFee.get("YI_LAWYFEE") == null? "" :nfFSNum.format(lawyFee.get("YI_LAWYFEE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, lawyFee.get("WEI_LAWYFEE") == null? "" :nfFSNum.format(lawyFee.get("WEI_LAWYFEE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignCenter, 3)) ;
	 	
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese,"期满购买金：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(staybuy.get("STAYBUY_PRICE") == null? "" :staybuy.get("STAYBUY_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(staybuy.get("YI_STAYBUY_PRICE") == null? "" :staybuy.get("YI_STAYBUY_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, nfFSNum.format(staybuy.get("WEI_STAYBUY_PRICE") == null? "" :staybuy.get("WEI_STAYBUY_PRICE")), fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, paddingDefault, borderNo,alignCenter, 3)) ;

		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese,"其他费用：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignCenter, 4)) ;
	 	
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese,"合计：", fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	if(rent.get("YING_RENT") == null){
	 		rent.put("YING_RENT", 0.0) ;
	 	}
	 	if(fine.get("DUN_PRICE") == null){
	 		fine.put("DUN_PRICE", 0.0) ;
	 	}
	 	if(staybuy.get("STAYBUY_PRICE") == null){
	 		staybuy.put("STAYBUY_PRICE", 0.0) ;
	 	}
	 	if(rent.get("YI_RENT") == null){
	 		rent.put("YI_RENT", 0.0) ;
	 	}
	 	if(fine.get("YI_DUN_PRICE") == null){
	 		fine.put("YI_DUN_PRICE", 0.0) ;
	 	}
	 	/*
	 	 * Modify By Michael 修正未还合计计算Bug
	 	 */
	 	if(staybuy.get("YI_STAYBUY_PRICE") == null){
	 		staybuy.put("YI_STAYBUY_PRICE", 0.0) ;
	 	}	
	 	
	 	if(lawyFee.get("YI_LAWYFEE") == null){
	 		lawyFee.put("YI_LAWYFEE", 0.0) ;
	 	}
	 	if(lawyFee.get("SUM_LAWYFEE") == null){
	 		lawyFee.put("SUM_LAWYFEE", 0.0) ;
	 	}
	 	//--------------------------------------------------------------------------------------------------------------
	 	Double YING_RENT=((Double)rent.get("YING_RENT") +  (Double)fine.get("DUN_PRICE") + (Double)staybuy.get("STAYBUY_PRICE") +DataUtil.doubleUtil(lawyFee.get("SUM_LAWYFEE")) ) ;
	 	Double YI_RENT=((Double)rent.get("YI_RENT") +  (Double)fine.get("YI_DUN_PRICE")+  (Double)staybuy.get("YI_STAYBUY_PRICE")+DataUtil.doubleUtil(lawyFee.get("YI_LAWYFEE")));
	 	Double Total_WEIRENT=YING_RENT-YI_RENT;
	 	t1.addCell(makeCell(bfChinese,nfFSNum.format((YING_RENT )) + "" , fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese,nfFSNum.format((YI_RENT)) + "" , fontDefault, paddingDefault, borderNo,alignCenter, 1));
	 	t1.addCell(makeCell(bfChinese,nfFSNum.format((Total_WEIRENT)) + "" , fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	
	 	//t1.addCell(makeCell(bfChinese,nfFSNum.format((((Double)rent.get("YING_RENT") - (Double)rent.get("YI_RENT"))  + (Double)fine.get("YING_FINE") - (Double)fine.get("YI_FINE"))) + "" , fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	//------------------------------------------------------------------------------------------------------------------------------------------
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderNo,alignCenter, 1)) ;
	 	
	 	document.add(t1);
	 	
	 	if((Integer)content.get("idsI")< ((Integer)content.get("idsLen") - 1)){	
			document.add(Chunk.NEXTPAGE);
		}
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
