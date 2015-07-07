package com.brick.backVisit.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.ALink;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 导出回访记录表
 * 
 * @author 
 * 
 * 
 */
public class backVisitRecordPDF extends AService{
	static Log logger = LogFactory.getLog(backVisitRecordPDF.class) ;
	
	
	@SuppressWarnings("unchecked")
	public static void expPDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "HuiFangJiLu" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			//取出数据
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
	public static void model(Map content,OutputStream baos) throws Exception {
		
		
		
		
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {10f,10f,2f,2f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
		//pdf名字
	 	
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
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
	   t1.addCell(makeCell(bfChinese,"承租户回访记录表",new int[]{20,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 16)) ;
	 	//第一行   合同編號	承租人	回訪日期	回訪人員
	 	t1.addCell(makeCell(bfChinese, "合 同 编 号" , fontDefault, paddingDefault, borderStart,alignCenter, 4)) ;
	 	t1.addCell(makeCell(bfChinese, "承    租    人" , fontDefault, paddingDefault, borderStart,alignCenter, 6)) ;
	 	t1.addCell(makeCell(bfChinese, "回访日期" , fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "回访人员" , fontDefault, paddingDefault, borderEnd,alignCenter, 3)) ;
		 //第二行
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderStart,alignDefault, 4)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderStart,alignDefault, 6)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderStart,alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderEnd,alignDefault, 3)) ;
	 	//第三行  標的物存放地	
	 	t1.addCell(makeCell(bfChinese, "标的物存放地" , fontDefault, paddingDefault, borderStart,alignCenter, 4)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderEnd,alignDefault, 12)) ;
	 	//第四行 標的物名稱	廠牌	機型	機號
	 	t1.addCell(makeCell(bfChinese, "标 的 物 名 称" , fontDefault, paddingDefault, borderStart,alignCenter, 4)) ;
	 	t1.addCell(makeCell(bfChinese, "厂      牌" , fontDefault, paddingDefault, borderStart,alignCenter, 6)) ;
	 	t1.addCell(makeCell(bfChinese, "机      型" , fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "机      号" , fontDefault, paddingDefault, borderEnd,alignCenter, 3)) ;
	 	//第五行 
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignDefault, 4)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignDefault, 6)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0},alignDefault, 3)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0.5f},alignDefault, 3)) ;
	 	for(int i = 0 ;i<12 ;i++){
		 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignDefault, 4)) ;
		 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignDefault, 6)) ;
		 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0,0,0.5f,0},alignDefault, 3)) ;
		 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 3)) ;
	 	}
	 	//第六行 接觸人員	□本人 □家人 □員工 
	 	t1.addCell(makeCell(bfChinese, "接触人员" , fontDefault, paddingDefault, borderStart,alignDefault, 4)) ;
	 	t1.addCell(makeCell(bfChinese, "□本人      □家人      □员工" , fontDefault, paddingDefault, borderEnd,alignDefault, 12)) ;
	 	//標的物外觀	□良好 □尚可 □差	是否見標的物	□是 □否
	 	t1.addCell(makeCell(bfChinese, "标的物外观" , fontDefault, paddingDefault, borderStart,alignDefault, 4)) ;
	 	t1.addCell(makeCell(bfChinese, "□良好      □尚可       □差" , fontDefault, paddingDefault, borderStart,alignDefault, 6)) ;
	 	t1.addCell(makeCell(bfChinese, "是否见标的物" , fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "□是      □否" , fontDefault, paddingDefault, borderEnd,alignCenter, 3)) ;
	 	//密碼設置是否正常	□是     □否	廠內營運狀況		□佳 □可 □差
	 	t1.addCell(makeCell(bfChinese, "密码设置是否正常" , fontDefault, paddingDefault, borderStart,alignDefault, 4)) ;
	 	t1.addCell(makeCell(bfChinese, "□是           □否" , fontDefault, paddingDefault, borderStart,alignDefault, 6)) ;
	 	t1.addCell(makeCell(bfChinese, "场内营运状况" , fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "□佳  □可  □差" , fontDefault, paddingDefault, borderEnd,alignDefault, 3)) ;
	 	//照片四張(廠房外觀.廠內營運狀況.標的物外觀.標的物機型機號)
	 	t1.addCell(makeCell(bfChinese, "照片四张（厂房外观、场内营运状况、标的物外观、标的物机型机号）" , fontDefault, paddingDefault,new float[]{0.5f,0,0.5f,0.5f},alignDefault, 16)) ;
	 	//補充說明：
	 	t1.addCell(makeCell(bfChinese, "补充说明：" , fontDefault, paddingDefault,new float[]{0,0,0.5f,0.5f},alignDefault, 16)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault,new float[]{0,0,0.5f,0.5f},alignDefault, 16)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault,new float[]{0,0.5f,0.5f,0.5f},alignDefault, 16)) ;
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
