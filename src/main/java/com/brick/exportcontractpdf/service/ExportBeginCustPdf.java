package com.brick.exportcontractpdf.service;

import com.brick.service.core.AService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.lowagie.text.Image;
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
import com.brick.credit.service.ExportQuoToPdf;
import com.brick.log.service.LogPrint;
//导出起租检测书
public class ExportBeginCustPdf extends AService {
	Log logger = LogFactory.getLog(ExportBeginCustPdf.class);

	//接受页面报告的ID数组
	 @SuppressWarnings("unchecked")
	    public void prePdf(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		 String[] comType=HTMLUtil.getParameterValues(context.getRequest(), "contract_type", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 context.contextMap.put("comType", comType);
	                     this.getDateMap(context);
	        			// this.expBeginCustPdfs(context);             	    
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 context.contextMap.put("comType", comType);
	                		 this.getDateMap(context);
	                		 //      this.expBeginCustPdfs(context);
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
		  this.expBeginCustPdfs(context,creditmaps,fileUps);
	 }
    /**
     * 单条合同导出，没有合同Id的数组，导出报告相关的文件
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void expBeginCustPdfs(Context context,ArrayList creditmaps,ArrayList fileUps) {
	
	ByteArrayOutputStream baos = null;
	//String[]  con = null;
	//Map creditmap =new HashMap();

	
 	
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
	       // con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< creditmaps.size();ii++){
		    	Map creditmap=(HashMap)creditmaps.get(ii);
	      //  	context.contextMap.put("credit_id",  con[ii]);
    	  	    	    	
	    	//查找报告的相关信息
	      //  creditmap = (Map) DataAccessor.query("exportContractPdf.queryCreditByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	//查找合同的类型
	    	//contracttype = (Map) DataAccessor.query("exportContractPdf.queryContractTypeByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
			//String code=contracttype.get("CONTRACT_TYPE").toString();
	    	//String code=creditmap.get("CONTRACT_TYPE").toString();
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
			String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
			Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");
			
			image.scaleAbsoluteHeight(20);
			image.scaleAbsoluteWidth(20);			
			
			cell=new PdfPCell();
			cell.addElement(image);
			cell.setBorder(0);
			tlogo.addCell(cell);
			//Modify By michael 2011 12/16 纠正公司Title错误  裕融
			//Chunk chunk1=new Chunk("融资租赁(苏州)有限公司",FontSmall);
			Chunk chunk1=new Chunk(Constants.COMPANY_NAME,FontSmall);
			Chunk chunk2=new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
			cell=new PdfPCell();
			cell.addElement(chunk1);
			cell.addElement(chunk2);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			tlogo.addCell(cell);
			
			cell=new PdfPCell(tlogo);
			cell.setColspan(13);
			cell.setPaddingBottom(5);
			cell.setBorder(0);
			tT.addCell(cell);
			//Modify By michael 2011 12/16 纠正公司Title错误  裕融
			//cell=makeCellNoBorderSetColspan("融资租赁(苏州)有限公司",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,13);
			cell=makeCellNoBorderSetColspan(Constants.COMPANY_NAME,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,13);
			//cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setFixedHeight(18);
			tT.addCell(cell);
			
			
			
			
			int i=0;
			//if(code.equals("0")){
				tT.addCell(makeCellSetColspanNoBorder("租赁案件起租文件检核表",PdfPCell.ALIGN_CENTER, FontDefault22,13));	
	    	     
	    		String Lcode = creditmap.get("LEASE_CODE") +"";
	    		Lcode=	Lcode.trim();
	    		if(Lcode.equals("")||Lcode.equals("null")){
	    			Lcode = "           ";
	    		}
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_RIGHT, FontDefault22,13));
	    		//表头的相关信息,第一行
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("客户:"+customername,PdfPCell.ALIGN_LEFT, FontDefault22,9));	    		
	    		tT.addCell(makeCellSetColspanNoBorder("合约编号:"+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,4));	
	    		tT.addCell(makeCellSetColspanNoBorder("供应商:"+sellername,PdfPCell.ALIGN_LEFT, FontDefault22,13));	
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第一个子表开始,第一行
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("    ",PdfPCell.ALIGN_LEFT, FontDefault22,1));	
	    		if(creditmap.get("comType")==null)
	    		{
	    			tT.addCell(makeCellSetColspan3("", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		}
	    		else if(creditmap.get("comType").equals("0"))
	    		{
	    			tT.addCell(makeCellSetColspan3("一般租赁案件", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		}
	    		else if(creditmap.get("comType").equals("1"))
	    		{
	    			tT.addCell(makeCellSetColspan3("委托购买租赁案件", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		} else if("5".equals(creditmap.get("comType"))) {
	    			tT.addCell(makeCellSetColspan3("新品回租案件", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		}
	    		else
	    		{
	    			tT.addCell(makeCellSetColspan3("", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		}
	    		
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//第2行
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 序号 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 文件名称及附件 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 应征份数 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 已征 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 查验 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 点收 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3(" 备 注 ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//第0行
	    	
	    		
	    		for(int h=0;h<fileUps.size();h++)
	    		{
	    			ArrayList list=(ArrayList)fileUps.get(h);
	    			for(int j=0;j<list.size();j++)
	    			{
	    			Map fileUpContext=(Map)list.get(j);
	    			//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
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
	    				//不要求带出已征
//	    				if(UPFILECOUNT ==null)
//	    				{
	    					tT.addCell(makeCellSetColspan2LeftAndTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    				}
//	    				else
//	    				{
//	    					tT.addCell(makeCellSetColspan2LeftAndTop(" "+UPFILECOUNT+" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
//	    				}
	    				tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    				tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    				tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    				//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
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
			    		
//			    		if(UPFILECOUNT==null)
//			    		{
			    			tT.addCell(makeCellSetColspan3NoRight(" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
//			    		}
//			    		else
//			    		{
//			    			tT.addCell(makeCellSetColspan3NoRight(" "+UPFILECOUNT+" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
//			    		}
			    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
			    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
			    		tT.addCell(makeCellWithNoBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellWithNoBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    			
		    			
		    		}
	    			}
		    		
	    		}
	    		
	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 1 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 租赁合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		//第1行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 1-1 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 法人代表/连保人身份证复印件 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第2行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 2 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 买卖合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第3行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 3 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 购置凭证（发票） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第4行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 4 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 交货验收证明暨起租通知书 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第5行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 5 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 付款指示书 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第6行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 6 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 供应商确认函（自备款收款确认） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第7行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 7 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 三方支付协议（出租人/承租人/供应商） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		
//	    		//第8行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 8 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 供应商买回约定书/回购合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		
//	    		//第9行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 9 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 保证合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第10行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 10 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 授权书（签约人非法人代表） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第11行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 11 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 密码控管表 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		
//	    		//第12行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 12 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 标的物相片及位置图 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3(" 相片须日期显示 ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		
//	    		//第13行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 13 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 法人保证人董事会决议 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		
//	    		//第14行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 14 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 租金支付明细表 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//第15行
//	    		
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 15 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 其他文件 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		
//	    		//最后一行
//	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
//	    		//tT.addCell(makeCellSetColspan3NoTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
//	    		tT.addCell(makeCellWithNoBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		tT.addCell(makeCellWithNoBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
	    		//第一个子表结束，第二个子表开始
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 起租记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 业管部点收记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan3(" 业务处 ",PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("租赁期间： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 经办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 地区业务 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("期数： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspanOnlyLeft(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspanOnlyLeft("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 协办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("首次收租日： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 复核 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 单位主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3NoRight("标有 * 的文件為不可待補之文件。",PdfPCell.ALIGN_LEFT, FontDefault222,4));
	    		tT.addCell(makeCellSetColspan3NoRight(" 处/部级主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan3NoRight(" 处/部级主管 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		//tT.addCell(makeCellSetColspan3NoTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithNoBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i+=27;
//	    		for(;i<43;i++){
//	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//	    		}
//	    		if(i<=43){
//	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//	    		}
			/*}else if(code.equals("1")){		  
				
				
				
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,15));	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15)); 
				tT.addCell(makeCellSetColspan2("租赁案件起租文件检核表",PdfPCell.ALIGN_CENTER, fa,15));	  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,15));
	    		//表头的相关信息,第一行
	    		String Lcode = creditmap.get("LEASE_CODE") +"";
	    		Lcode=	Lcode.trim();
	    		if(Lcode.equals("")){
	    			Lcode = "           ";
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("客户:"+customername,PdfPCell.ALIGN_LEFT, FontDefault22,5));	    		
	    		tT.addCell(makeCellSetColspanNoBorder("供应商:"+sellername,PdfPCell.ALIGN_CENTER, FontDefault22,4));	
	    		tT.addCell(makeCellSetColspanNoBorder("合约编号:"+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,4));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,15));
	    		
	    		//第一个子表开始,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//tT.addCell(makeCellSetColspan2LeftAndTop("    ",PdfPCell.ALIGN_LEFT, FontDefault22,1));	
	    		tT.addCell(makeCellSetColspan3("委托购买租赁案件", PdfPCell.ALIGN_CENTER, FontDefault2,13));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//第2行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 序号 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 文件名称及附件 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 應徵份數 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 已徵 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 查验 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 点收 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3(" 备 注 ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//第0行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 1 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 租赁合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" ",PdfPCell.ALIGN_LEFT, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		//第1行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 1-1 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 法人代表/連保人身份证复印件 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第2行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 2 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 委托购买合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第3行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 3 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 确认书（承租人确认所有权归属） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第4行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 4 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 交货验收证明暨起租通知 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第5行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 5 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 付款指示书或请款书 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第6行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 6 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("购置凭证（发票正本/抵扣聯） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第7行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 7 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 供应商确认函（自备款收款确认） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第8行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 8 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 三方支付协议（出租人/承租人/供应商） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第9行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 9 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 供应商买回约定書/回购合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第10行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 10 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 保证合同 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第11行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 11 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 授权书（签约人非法人代表） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第12行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" * ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 12 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("买卖合同复印件（供应商/承租人） ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第13行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 13 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 密码控管表 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		//第14行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 14 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 标標的物相片及位置图 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3(" 相片须有日期显示 ",PdfPCell.ALIGN_CENTER, FontDefault222,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第15行
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 15 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 法人保证人董事会决议 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第16行	
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 16 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 租金支付明细表 ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//第17行	
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 17 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 其他文件： ",PdfPCell.ALIGN_LEFT, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		//最后一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
	    		//第一个子表结束，第二个子表开始
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 起租记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 作服部点收记录 ",PdfPCell.ALIGN_CENTER, FontDefault22,5));
	    		tT.addCell(makeCellSetColspan3(" 业务部 ",PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("租赁期间： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 经办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 经办 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("期数： ",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspanOnlyLeft(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspanOnlyLeft("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 业助 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		

	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellWithNoBorder("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3NoRight("首次收租日：",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan3NoRight(" ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan3NoRight("  ",PdfPCell.ALIGN_CENTER, FontDefault22,4));
	    		tT.addCell(makeCellSetColspan3NoRight(" 经理 ",PdfPCell.ALIGN_CENTER, FontDefault22,1));
	    		tT.addCell(makeCellSetColspan("  ",PdfPCell.ALIGN_CENTER, FontDefault22,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=25;
	    		for(;i<43;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,15));
	    		}
	    		if(i<=43){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,15));
	    		}
		    		
			}else {
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,15));	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15)); 
				tT.addCell(makeCellSetColspan2("回租方式的租赁单，没有对应的表格",PdfPCell.ALIGN_CENTER, fa,15));	  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,15));
				i+=25;
	    		for(;i<43;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,15));
	    		}
	    		if(i<=43){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,15));
	    		}
				
			}
			*/
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

		//add by ShenQi 插入系统日志
		BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
		   		 "导出 租赁案件起租档检核表",
	   		 	 "合同浏览导出 租赁案件起租档检核表",
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
 
 //Add by Michael 2012-3-31 导出重车起租检核表
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
	                     this.getCarDateMap(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 context.contextMap.put("comType", comType);
	                		 this.getCarDateMap(context);
	                	} 
	        		}
			} catch (Exception e) {
				    e.printStackTrace();
				    LogPrint.getLogStackTrace(e, logger);
			}
		  }
		}	
	  }
	 public void  getCarDateMap(Context context){
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
		  this.expCarBeginCustPdfs(context,creditmaps,fileUps);
	 }
   /**
    * 单条合同导出，没有合同Id的数组，导出报告相关的文件
    * @param context
    */
   @SuppressWarnings("unchecked")
   public void expCarBeginCustPdfs(Context context,ArrayList creditmaps,ArrayList fileUps) {
	
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
//			String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
//			Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");
//			
//			image.scaleAbsoluteHeight(20);
//			image.scaleAbsoluteWidth(20);			
			
//			cell=new PdfPCell();
//			//cell.addElement(image);
//			cell.setBorder(0);
//			tlogo.addCell(cell);
//			Chunk chunk1=new Chunk(Constants.COMPANY_NAME,FontSmall);
//			Chunk chunk2=new Chunk(Constants.COMPANY_NAME_ENGLISH,FontSmall2);
//			cell=new PdfPCell();
//			cell.addElement(chunk1);
//			cell.addElement(chunk2);
//			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
//			cell.setBorder(0);
//			tlogo.addCell(cell);
			
//			cell=new PdfPCell(tlogo);
//			cell.setColspan(13);
//			cell.setPaddingBottom(5);
//			cell.setBorder(0);
//			tT.addCell(cell);
			cell=makeCellNoBorderSetColspan(Constants.COMPANY_NAME+"【机动车】",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,13);
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
	    		else if(creditmap.get("comType").equals("0"))
	    		{
	    			tT.addCell(makeCellSetColspan3("一般租赁案件", PdfPCell.ALIGN_CENTER, FontDefault2,12));
	    		}
	    		else if(creditmap.get("comType").equals("1"))
	    		{
	    			tT.addCell(makeCellSetColspan3("委托购买租赁案件", PdfPCell.ALIGN_CENTER, FontDefault2,12));
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
//	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
//	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault,13));
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
//	    		for(;i<43;i++){
//	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//	    		}
//	    		if(i<=43){
//	    			tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,13));
//	    		}
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
    
    
 


    /** 创建 有边框 合并 单元格|-|
     *  无下边用于表格的顶
     *  
     *  */
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
    /** 创建 有边框 合并 单元格|_|
     *  无上边
     *  
     *  */
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

    /** 创建 有边框 合并 单元格| |
     *  无上下边
     *  
     *  */
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

    /* 创建 无边框 合并 单元格
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
	return objCell;
    }
    /** 创建 无边框 单元格
     * 
     *  */
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



   


    


    // 创建 有边框只有左下边框 合并 单元格
    private PdfPCell makeCellSetColspan2LeftAndBottom(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }
    // 创建 有边框只有上边框 合并 单元格
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
    



    
	/** 创建 只有左边框 单元格 */
	private PdfPCell makeCellWithBorderLeft(String content, int align, Font FontDefault) {
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
	private PdfPCell makeCellWithBorderRight(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	 
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
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


   
         
	
 
}