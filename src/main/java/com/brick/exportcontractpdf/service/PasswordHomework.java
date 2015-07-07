package com.brick.exportcontractpdf.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PasswordHomework extends AService {
	Log logger = LogFactory.getLog(PasswordHomework.class);
	
    public void prePdf(Context context){
    	 System.out.println("p") ;
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getDateMap(context);
	        			        	    
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getDateMap(context);
//	                		
	                	} 
	        		}
			} catch (Exception e) {
				    e.printStackTrace();
				    LogPrint.getLogStackTrace(e, logger);
			}
		  }
		}	
	  }
    
    public void  getDateMap(Context context){
    	 System.out.println("g") ;
		 String[]  con = null;
		 ArrayList creditmapandleasehold=new ArrayList();
		  con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< con.length;ii++){	
	        	HashMap outputMap= new HashMap();
	        	List leaseholds =new ArrayList();
	        	context.contextMap.put("credit_id",  con[ii]);
		       try{
		    	 leaseholds = (List) DataAccessor.query("exportContractPdf.queryPassWordByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
		    	 outputMap.put("leaseholds", leaseholds);
		    	 creditmapandleasehold.add(outputMap);
		    	 
		       }catch(Exception e){
		    	   e.printStackTrace();
		    	  LogPrint.getLogStackTrace(e, logger);
		       }
		    }
	        this.expLeaseHoldPdfs(context,creditmapandleasehold);
	 }
    public void expLeaseHoldPdfs(Context context,ArrayList creditmapandleasehold) {
    	 System.out.println("e") ;
    	ByteArrayOutputStream baos = null;
    	//String[]  con = null;
    	ArrayList leaseholds =new ArrayList();	
     	
    	try {   
    		 Document document=new Document(PageSize.A4, 50, 50, 100, 50);
    		   
    	     Rectangle pageRect=document.getPageSize();
    	   //PdfWriter.getInstance(document, new FileOutputStream("D:\\tables.pdf"));
    	  //   PdfWriter.getInstance(document, new FileOutputStream(new File("D:\\tablepass.pdf")));
    	     //创建汉字字体
    	     BaseFont bfSong = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
    	    // Font fontSong = new Font(bfSong, 10, Font.NORMAL,Color.RED);
    	     Font fontSong = new Font(bfSong, 10, Font.BOLD);
    	     
    	     Font FontColumn = new Font(bfSong, 11, Font.UNDERLINE);
    	   
    	     
    	     baos = new ByteArrayOutputStream();
 	        PdfWriter.getInstance(document, baos);
 	        
 	       HeaderFooter header = new HeaderFooter(new Phrase("密码作业通知说明书",FontColumn), false);
  	     header.setBorder(0);    //设0 没有了那个下划线 设边框的
  	     header.setAlignment(Element.ALIGN_CENTER);
  	     document.setHeader(header);
 	        // 打开文档
    	     document.open();
    	     for(int ii=0; ii< creditmapandleasehold.size();ii++){//for 循环在这开始
    	    	 HashMap outputMap = (HashMap)creditmapandleasehold.get(ii); 
 	        	leaseholds = (ArrayList)outputMap.get("leaseholds");
 	        	HashMap baseinfo = (HashMap)leaseholds.get(0);
 	        String code="0";
 	   	//厂牌
    		String changpai="";
    		if(baseinfo.get("THING_KIND")==null){
    			changpai="";
    		}else{
    			changpai=baseinfo.get("THING_KIND").toString();
    		}
 	   	//机型
    		String machineKind="";
    		if(baseinfo.get("CREDIT_CO")==null){
    			machineKind="";
    		}else{
    			machineKind=baseinfo.get("CREDIT_CO").toString();
    		}
 	        //承租方名称
 	        String custname = "";
     		if(baseinfo.get("CUST_NAME")==null){
     			custname = "  ";
 	    	}else{
 	    		custname = baseinfo.get("CUST_NAME").toString();
 	    	}
     		//承租方地址
     		String custaddress = "";
      		if(baseinfo.get("CUST_ADDRESS")==null){
      			custaddress = "  ";
  	    	}else{
  	    		custaddress = baseinfo.get("CUST_ADDRESS").toString();
  	    	}
     		String Lcode = "";
     		if(baseinfo.get("LEASE_CODE")==null){
     			Lcode = "无编号";
 	    	}else{
 	    		Lcode = baseinfo.get("LEASE_CODE").toString();
 	    	}
     		 System.out.println("1") ;
    	     PdfPTable tT30 = new PdfPTable(1);  
    		    tT30.setWidthPercentage(100f);
    		   tT30.addCell(removeCellAll("（合同编号: "+Lcode+"                   承租人:  "+custname+"                        ）", PdfPCell.ALIGN_CENTER,fontSong,0));
    		   tT30.addCell(removeCellAll("敬爱的客户： "+custname+"", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("非常感谢您使用本公司提供的融资租赁服务，谨致谢忱。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("本次租赁之机器设备某些特定机型出厂时即具有锁码功能，若未能于时限内输入密码将引致", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("机器设备停机，为维护贵户权益，特将锁码方式及密码通知相关事宜专函说明如后:", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("壹、	锁码日期", PdfPCell.ALIGN_LEFT,FontColumn,0));
    		   tT30.addCell(removeCellAll("             锁码日期为每月_________日 ,锁码日每月固定日期。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("貳、	锁码方式", PdfPCell.ALIGN_LEFT,FontColumn,0));
    		   tT30.addCell(removeCellAll("          □  一、直接锁码：", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            须于锁码日期到前取得本公司自供货商处预先取得之密码以凭译码，否则时限一到，", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            机器设备即锁住停机。，", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("           □ 二、间接锁码:", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            □	(1) 锁码日期到前几天先跳出提示码", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            锁码日期到前几天,机器设备会跳出提示码,须告知本公司提示码内容以凭向供货商要", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            求提供密码译码, 否则时限一到，机器设备即锁住停机。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            □	（2）锁码日期到前不会跳出提示码直接锁码", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            机器设备到了设定的锁码日自动锁码", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("叁、密码通知作业", PdfPCell.ALIGN_LEFT,FontColumn,0));
    		   tT30.addCell(removeCellAll("           □ 一、直接锁码-锁码日期到直接锁码方式", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               本公司于贵户当期租金入账后即会由专人负责以电邮方式提供密码至贵户指定之电", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               邮信箱。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("           □ 二、间接锁码。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("            □ (1) 锁码日期到前几天先跳出提示码方式", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               原则上机器设备均会于时限日到前五天跳出提示码,请贵户实时通知本公司提示", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               码内容，以便本公司向供货商要求提供密码再由专人负责以电邮方式提供密码至", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               贵户指定之电邮信箱。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               □	（2）锁码日期到机器不会跳出提示码直接锁码", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               机器设备不会預先跳出提示码，只要确认租金已经到账，本公司会向供货商要求", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               提供密码再由专人负责以电邮方式提供密码至贵户指定之电邮信箱。", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("肆、密码作业联络窗口", PdfPCell.ALIGN_LEFT,FontColumn,0));
    		   tT30.addCell(removeCellAll("               本公司密码作业系由本公司作业管理部专责处理，如有任何问题请随时来电", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               洽询 业管部： +86-512-80983566 分机号：88200 ", PdfPCell.ALIGN_LEFT,fontSong,0));
    		   tT30.addCell(removeCellAll("               本案标的物明细", PdfPCell.ALIGN_LEFT,fontSong,0));
    		  
    		   document.add(tT30);
    		   PdfPTable tT2 = new PdfPTable(4); 
    		   
    		   tT2.addCell(makeCellSetColspanLRBorderAuto("标的物名称",PdfPCell.ALIGN_CENTER,fontSong,0));
    		   tT2.addCell(makeCellSetColspanLRBorderAuto("厂牌",PdfPCell.ALIGN_CENTER,fontSong,0));
    		   tT2.addCell(makeCellSetColspanLRBorderAuto("机型",PdfPCell.ALIGN_CENTER,fontSong,0));
    		//   tT2.addCell(makeCellSetColspanBTBorder("机号",PdfPCell.ALIGN_CENTER,fontSong,0));
    		   tT2.addCell(makeCell("机号",PdfPCell.ALIGN_CENTER,fontSong,0,new int[]{1,0,1,1}));
    		   
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
	    			String brand="";
	    			if(((HashMap)leaseholds.get(k)).get("BRAND")==null){
	    				brand="";
	    			}else{
	    				brand=((HashMap)leaseholds.get(k)).get("BRAND").toString();
	    			}
	    			tT2.addCell(makeCellSetColspanLRBorderAuto(""+thingname+"",PdfPCell.ALIGN_CENTER,fontSong,0));
					tT2.addCell(makeCellSetColspanLRBorderAuto(""+changpai+"",PdfPCell.ALIGN_CENTER,fontSong,0));
					tT2.addCell(makeCellSetColspanLRBorderAuto(""+machineKind+"",PdfPCell.ALIGN_CENTER,fontSong,0));
					//tT2.addCell(makeCellWithBorder(""+modelspec+"",PdfPCell.ALIGN_CENTER,fontSong,0));
					tT2.addCell(makeCell(""+modelspec+"",PdfPCell.ALIGN_CENTER,fontSong,0,new int[]{1,0,1,1}));
    		   }

				tT2.addCell(makeCell(" ",PdfPCell.ALIGN_CENTER,fontSong,4,new int[]{1,0,0,0} ));
				document.add(tT2);
    	     }//for循环在这结束
    		   document.add(Chunk.NEXTPAGE);
    	  
    		   document.close();
    	    
    		   // 支付表PDF名字的定义
    		    String strFileName =  "password.pdf";
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
    	}catch(Exception e){
	    	   e.printStackTrace();
		    	  LogPrint.getLogStackTrace(e, logger);
		       }
    }
    /**
     * 流关闭操作
     * @param content
     * @param align
     * @param FontDefault
     * @return
     */
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
    /** 无边框**/
	  private PdfPCell removeCellAll(String content, int align, Font FontDefault,int spancol) {
			Phrase objPhase = new Phrase(content, FontDefault);
			PdfPCell objCell = new PdfPCell(objPhase);
			objCell.setFixedHeight(30);
			objCell.setHorizontalAlignment(align);
			objCell.setBorderWidthBottom(0f);
			objCell.setBorderWidthRight(0f);
	    	objCell.setBorderWidthTop(0f);
			objCell.setBorderWidthLeft(0f);
			objCell.setColspan(spancol);
			return objCell;
		}
	 
		/** 创建 有上下左右边框 合并 单元格 */
		private PdfPCell makeCellSetColspanBTBorder(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setFixedHeight(20);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(1);
		    objCell.setBorderWidthTop(1);
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
	
		/** 创建 上下左边框 单元格 */
		private PdfPCell makeCellWithBorderLeft(String content, int align, Font FontDefault) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setFixedHeight(20);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setBorderWidthLeft(1);
		   
		    objCell.setBorderWidthBottom(1);
		    objCell.setBorderWidthTop(1);
		    objCell.setBorderWidthRight(0);
		    return objCell;
		}
		
		/** 创建 (右下边)单元格 */
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
		/** 创建 有边框 合并 单元格 */
		private PdfPCell makeCellSetColspan(String content, int align, Font FontDefault,int colspan) {
			Phrase objPhase = new Phrase(content, FontDefault);
			PdfPCell objCell = new PdfPCell(objPhase);
			objCell.setHorizontalAlignment(align);
			objCell.setVerticalAlignment(align);
			objCell.setFixedHeight(20);
			objCell.setColspan(colspan);	
			return objCell;
		}
		/** 创建 左右上下边框 合并 单元格 */
		private PdfPCell makeCellSetColspanLRBorder(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setFixedHeight(20);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(1);
		    objCell.setBorderWidthTop(1);
		    return objCell;
		}	
		
		/** 创建 有上左边框 合并 单元格 */
		private PdfPCell makeCellSetColspanLRBorderAuto(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		  
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(0);
		    objCell.setBorderWidthBottom(0);
		    objCell.setBorderWidthTop(1);
		    return objCell;
		}	
		/** 创建 左右上下边框 合并 单元格 */
		private PdfPCell makeCell(String content, int align, Font FontDefault,int colspan,int[] border) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setFixedHeight(20);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(border[2]);
		    objCell.setBorderWidthRight(border[3]);
		    objCell.setBorderWidthBottom(border[1]);
		    objCell.setBorderWidthTop(border[0]);
		    return objCell;
		}	
    
}
