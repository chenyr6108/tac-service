package com.brick.backVisit.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.servlet.ServletOutputStream;

import com.brick.service.entity.Context;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;

public class pdfPublic
{
	Log logger = LogFactory.getLog(pdfPublic.class);
	 
	 
	 public void expPdf(Context context) 
	 {
		 	ByteArrayOutputStream baos = null;
		 	
		 	String pdfName=null;//pdf名字
		 
		 try
		 {
			 PdfPTable tT = new PdfPTable(3);//3表示列数
			 
			 // 字体设置
 	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			 
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
	        
	        //详细参数看方法makeCell()
	        tT.addCell(makeCell(bfChinese,"内容",-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,1f,1f,1f,-1));
	        
	        
	        
	        document.add(tT);
	    	document.add(Chunk.NEXTPAGE);
	    	    
	    	
	    	
	    	document.close();
	    	
	    	 // 支付表PDF名字的定义
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

	    	    if( (context.getContextMap().get("creditidflagi")+"").equals("" +context.getContextMap().get("creditidflagl"))  ){
	    		
	    		closeStream(o);
	    	    }
			 
			 
			 
			 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 LogPrint.getLogStackTrace(e, logger);
		 }
		 
		 
	 }
	
	 
	 private void closeStream(OutputStream  o){
			try {
			    
			    o.close();
			    
			} catch (IOException e) {


			    e.printStackTrace();
			    LogPrint.getLogStackTrace(e, logger);
			    
			}finally{
			    
			    try {
				
				o.close();
				
			    } catch (IOException e) {
				 
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			    }
			}
			
		    }
	 
	 //pdf样式设定()
	 private PdfPCell makeCell(BaseFont bfChinese,String content,int fontSize,int fontStyle,float paddingTopF,float paddingBottomF
			 ,float paddingLeftF,float paddingRightF,int alignHorizontal,int alignVertical,float borderTopF,float borderBottomF,float borderLeftF,
			 float borderRightF,int colspan) 
	 {
		 	//字体自定义
			//int fontSize=0;//字体大小(一般设置成11,默认为11（标记-1）)
			//int fontStyle=0;//字体（用系统函数Font下的参数（例如：Font.BOLD）,默认为Font.BOLD（标记-1））
			
			//字体位置
		 	 //BaseFont bfChinese //字体设置
			 
			 
			 //float paddingTopF=0f;//离上边距距离
			 //float paddingBottomF=0f;//离下边距距离
			 //float paddingLeftF=0f;//离左边距距离
			 //float paddingRightF=0f;//离右边距距离
		 
		 	
		 	//int alignHorizontal //水平位置（用系统函数PdfPCell的参数（居中：PdfPCell.ALIGN_CENTER,靠左：PdfPCell.ALIGN_LEFT或PdfPCell.LEFT,
		 							//靠右PdfPCell.ALIGN_RIGHT或PdfPCell.RIGHT,靠上PdfPCell.ALIGN_TOP或PdfPCell.TOP，靠下PdfPCell.ALIGN_BOTTOM或PdfPCell.BOTTOM）
		 								//默认为不设置（标记为-1））
		 	
		 	//int alignVertical   //垂直位置(同水平位置设定)默认为不设置（标记为-1）
		 
		 	//float borderTopF=0f;//上边框粗细
		 	//float borderBottomF=0f;//下边框粗细
		 	//float borderLeftF=0f;//左边框粗细
		 	//float borderRightF=0f;//右边框粗细
		 
		 
		 	//int colspan=0;合并单元格,默认为不设置（标记为-1）
		 	
		 	Font FontStyleDe=null;
		 	if(fontSize<=0f)
		 	{
		 		if(fontStyle==-1f)
		 		{
		 			FontStyleDe = new Font(bfChinese, 11f, Font.BOLD);
		 		}
		 		else
		 		{
		 			FontStyleDe = new Font(bfChinese, 11f, fontStyle);
		 		}
		 		
		 	}
		 	else
		 	{
		 		if(fontStyle==-1f)
		 		{
		 			FontStyleDe = new Font(bfChinese, fontSize, Font.BOLD);
		 		}
		 		else
		 		{
		 			FontStyleDe = new Font(bfChinese, fontSize, fontStyle);
		 		}
		 	} 
		 	
			Phrase objPhase = new Phrase(content, FontStyleDe);
			PdfPCell objCell = new PdfPCell(objPhase);
			
			if(paddingTopF!=-1)
			{
				objCell.setPaddingTop(paddingTopF);
			}
			if(paddingBottomF!=-1)
			{
				objCell.setPaddingBottom(paddingBottomF);
			}
			
			if(paddingLeftF!=-1)
			{
				objCell.setPaddingLeft(paddingLeftF);
			}
			if(paddingRightF!=-1)
			{
				objCell.setPaddingRight(paddingRightF);
			}
			
			objCell.setBorderWidthTop(borderTopF);
			objCell.setBorderWidthBottom(borderBottomF);
			objCell.setBorderWidthLeft(borderLeftF);
			objCell.setBorderWidthRight(borderRightF);
			
			if(alignHorizontal!=-1)
			{
				objCell.setHorizontalAlignment(alignHorizontal);
			}
			if(alignVertical!=-1)
			{
				objCell.setVerticalAlignment(alignVertical);
			}
			
			if(colspan!=-1)
			{
				objCell.setColspan(colspan);
			}

			return objCell;
	}
	 
	
	 
}
