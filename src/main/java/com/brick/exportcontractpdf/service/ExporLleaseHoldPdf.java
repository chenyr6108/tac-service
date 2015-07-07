package com.brick.exportcontractpdf.service;

import com.brick.service.core.AService;
import com.brick.service.core.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
//租赁物验收证明暨起租通知书
public class ExporLleaseHoldPdf extends AService {
	Log logger = LogFactory.getLog(ExporLleaseHoldPdf.class);

	//接受页面报告的ID数组
	 @SuppressWarnings("unchecked")
	    public void prePdf(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getDateMap(context);
	        			 //this.expLeaseHoldPdfs(context);             	    
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getDateMap(context);
//	                		 this.expLeaseHoldPdfs(context);
	                	} 
	        		}
			} catch (Exception e) {
				    e.printStackTrace();
				    LogPrint.getLogStackTrace(e, logger);
			}
		  }
		}	
	  }
	 public void prePdfFromCredit(Context context){
			String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
			
			 if(con != null ){
			   if(!(con[0].equals("00"))){
				 try {
		        		 if(con.length >1){
		        			 context.contextMap.put("credtdxx",  con);
		        			 this.getDateMapFromCredit(context);
		        			 //this.expLeaseHoldPdfs(context);             	    
		        		 }else{
		                	if(con.length ==1){
		                		 context.contextMap.put("credtdxx",   con);
		                		 this.getDateMapFromCredit(context);
//		                		 this.expLeaseHoldPdfs(context);
		                	} 
		        		}
				} catch (Exception e) {
					    e.printStackTrace();
					    LogPrint.getLogStackTrace(e, logger);
				}
			  }
			}	
		  }
	 
	 public void prePdfFromNewCredit(Context context){
			String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
			
			 if(con != null ){
			   if(!(con[0].equals("00"))){
				 try {
		        		 if(con.length >1){
		        			 context.contextMap.put("credtdxx",  con);
		        			 this.getNewDateMapFromCredit(context);
		        		 }else{
		                	if(con.length ==1){
		                		 context.contextMap.put("credtdxx",   con);
		                		 this.getNewDateMapFromCredit(context);
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
	public void  getNewDateMapFromCredit(Context context){
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
	        this.expNewLeaseHoldPdfs(context,creditmapandleasehold);
	 }
   
	 
	public void  getDateMap(Context context){
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
		    	   
		    	// leaseholds = (List) DataAccessor.query("exportContractPdf.queryLeaseHoldByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
		    	 outputMap.put("leaseholds", leaseholds);
		    	 creditmapandleasehold.add(outputMap);
		    	 
		       }catch(Exception e){
		    	   e.printStackTrace();
		    	   LogPrint.getLogStackTrace(e, logger);
		       }
		    }
	        this.expLeaseHoldPdfs(context,creditmapandleasehold);
	 }
	
    @SuppressWarnings("unchecked")
    public void expLeaseHoldPdfs(Context context,ArrayList creditmapandleasehold) {
	
	ByteArrayOutputStream baos = null;
	//String[]  con = null;
	ArrayList leaseholds =new ArrayList();	
 	
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
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
	     //   con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< creditmapandleasehold.size();ii++){
		    	
	     //   	context.contextMap.put("credit_id",  con[ii]);
    	  	    	    	
	    	//查找报告的相关信息以及租赁物的相关信息		    
		//    leaseholds = (List) DataAccessor.query("exportContractPdf.queryLeaseHoldByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
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
// 	    		custaddress = baseinfo.get("CUST_ADDRESS").toString();
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
    		float[] widthsStl = {0.2f,0.3f,0.3f,0.3f,0.3f,0.2f};
			PdfPTable tT = new PdfPTable(widthsStl);
			tT.setWidthPercentage(100f);
			int i=0;
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,6));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6)); 
				i++;
				//Modify by Michael 2012 07-06 For 签呈修改合同版本
				//tT.addCell(makeCellSetColspan2("租赁物验收证明暨起租通知书",PdfPCell.ALIGN_CENTER, fa,6));
				tT.addCell(makeCellSetColspan2("起租通知书",PdfPCell.ALIGN_CENTER, fa,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault22,2));	    		
	    		tT.addCell(makeCellSetColspanNoBorder("合同编号："+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,2));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));
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
	    		//phrase.add(new Phrase(createdate[0], FontUnde2)); 
	    		//phrase.add(new Phrase(createdate[0], FontUnde2)); 
	    		phrase.add(new Phrase("____年", FontDefault2)); 
	    		//phrase.add(new Phrase(createdate[1], FontUnde2)); 
	    		phrase.add(new Phrase("____月", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    		
//				tT.addCell(makeCellSetColspanNoBorder("根据我方（承租方）与裕融租赁（苏州）有限公司（出租方）之间于"+createdate[0]+"年"+createdate[1]+"月", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//为编号加入下划线
	    		phrase = new Phrase("", FontDefault2); 
	    		//phrase.add(new Phrase(createdate[2], FontUnde2)); 
	    		phrase.add(new Phrase("____日签订的编号为", FontDefault2)); 
	    		phrase.add(new Phrase(Lcode+"", FontUnde2)); 
	    		phrase.add(new Phrase("的租赁合同，下列租赁物已确实收到，并在", FontDefault2)); 
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
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
//				if(baseinfo.get("START_DATE")==null||startdate.equals("1900-01-01")){
//					tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于 ____年 ____月 ____日正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//				}else{
//					//为日期加入下划线
//		    		phrase = new Phrase("本日由我方验收合格并订于", FontDefault2); 
//		    		phrase.add(new Phrase(startdate+"", FontUnde2)); 
//		    		phrase.add(new Phrase("正式起租 。特此通知！", FontDefault2)); 
//		    		tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));							
////		    		tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于"+startdate+"正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));							
//				}
	    		tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于 ____年 ____月 ____日正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		
	    		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));				
	    		i++;
				//第一个子表开始,第一行
	    		//租赁物的数量是动态增加的
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("名称", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("型号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("机号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan3("单位及数量", PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		String thing_kind = "";
	    		String name = "";
	    		String brand="";
	    		List manufacturerList = new ArrayList() ;
	    		
	    		//Add by Michael 2012 07-10 增加供应商列表
	    		List brandList = new ArrayList();
	    		
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
	    				//Add by Michael 2012 07-10 增加供应商列表
	    				if (!brand.equals(((HashMap)leaseholds.get(k)).get("BRAND").toString())){
	    	    			brandList.add(((HashMap)leaseholds.get(k)).get("BRAND").toString());
	    				}
	    				brand=((HashMap)leaseholds.get(k)).get("BRAND").toString();
	    			}
	    			

	    			
	    			//改为制造商以前是厂牌
	    			if(((HashMap)leaseholds.get(k)).get("MANUFACTURER")==null){
	    				thing_kind="";
	    			}else{
	    				//Add by Michael 2012 07-10 增加供应商列表
	    				if (!thing_kind.equals(((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString())){
	    					manufacturerList.add(((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString());
	    				}
	    				
	    				thing_kind=((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString();
	    			}
	    			
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
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else{
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault222,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
		    		}
		    		if(i<55&&i%51==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    			}
	    		//所租机械循环显示完毕
	    		}
	    		//System.out.println(i+"xunhuanwanbi");
	    		//所租机械循环显示完毕
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					tT.addCell(makeCellSetColspan3("租赁物卖方：", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
		    		i+=1;
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					tT.addCell(makeCellSetColspan3("租赁物卖方：", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
		    		i+=1;
	    			
	    		}else{
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					tT.addCell(makeCellSetColspan3("租赁物卖方：", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
		    		i+=1;
	    		}
	    		
	    		//Add by Michael 2012 07-10 增加供应商列表
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				for(int j = 0;j<brandList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != brandList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				for(int j = 0;j<brandList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != brandList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else{
	    			for(int j = 0;j<brandList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != brandList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(brandList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("制造商：", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
//		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//					tT.addCell(makeCellSetColspan3NoTop(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
//		    		i+=1;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
//		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//					tT.addCell(makeCellSetColspan3NoTop(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
//		    		i+=1;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else{
	    			for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("租赁期间", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
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
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(leasePeriod != null && leasePeriod > 0){
		    			tT.addCell(makeCellSetColspan2("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
		    		} else {
		    			tT.addCell(makeCellSetColspan2("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
		    		}
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));	    			    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i<55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所: "+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;

	    		if(i<55&&i%51==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;	
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;	
    			}
	    		
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		i+=3;

	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("承租方:   "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,3));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;
	    		/*for(;i<p*50;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    		}
	    		if(i<=p*50){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    		}
	    		*/
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("地址:   "+custaddress, PdfPCell.ALIGN_LEFT, FontDefault2,3));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("法人代表或授权人:", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("（签章）", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("日期:", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1 ;
	    		if(i<53){
	    			for(;i<49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
		    		if(i<=49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
	    		}else if(i>53){
		    		int p=Math.round((i-49)/52)+1;
		    		for(;i<p*52+49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
		    		if(i<=p*52+49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
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
		
		//add by ShenQi 插入系统日志
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

	
	 @SuppressWarnings("unchecked")
	public void  getDateMapFromCredit(Context context){
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
	        this.expLeaseHoldPdfs(context,creditmapandleasehold);
	 }
    /**
     * 导出新品回租 起租通知书
     * @param context
     */
    public void expNewLeaseHoldPdfs(Context context,ArrayList creditmapandleasehold) {
	
	ByteArrayOutputStream baos = null;
	//String[]  con = null;
	ArrayList leaseholds =new ArrayList();	
 	
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
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
	     //   con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< creditmapandleasehold.size();ii++){
		    	
	     //   	context.contextMap.put("credit_id",  con[ii]);
    	  	    	    	
	    	//查找报告的相关信息以及租赁物的相关信息		    
		//    leaseholds = (List) DataAccessor.query("exportContractPdf.queryLeaseHoldByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
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
// 	    		custaddress = baseinfo.get("CUST_ADDRESS").toString();
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
    		float[] widthsStl = {0.2f,0.3f,0.3f,0.3f,0.3f,0.2f};
			PdfPTable tT = new PdfPTable(widthsStl);
			tT.setWidthPercentage(100f);
			int i=0;
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,6));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6)); 
				i++;
				//Modify by Michael 2012 07-06 For 签呈修改合同版本
				//tT.addCell(makeCellSetColspan2("租赁物验收证明暨起租通知书",PdfPCell.ALIGN_CENTER, fa,6));
				tT.addCell(makeCellSetColspan2("起租通知书",PdfPCell.ALIGN_CENTER, fa,6));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("    ",PdfPCell.ALIGN_LEFT, FontDefault22,2));	    		
	    		tT.addCell(makeCellSetColspanNoBorder("合同编号："+Lcode  ,PdfPCell.ALIGN_RIGHT,  FontDefault22,2));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));
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
	    		//phrase.add(new Phrase(createdate[0], FontUnde2)); 
	    		//phrase.add(new Phrase(createdate[0], FontUnde2)); 
	    		phrase.add(new Phrase("____年", FontDefault2)); 
	    		//phrase.add(new Phrase(createdate[1], FontUnde2)); 
	    		phrase.add(new Phrase("____月", FontDefault2)); 
	    		tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    		
//				tT.addCell(makeCellSetColspanNoBorder("根据我方（承租方）与裕融租赁（苏州）有限公司（出租方）之间于"+createdate[0]+"年"+createdate[1]+"月", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//为编号加入下划线
	    		phrase = new Phrase("", FontDefault2); 
	    		//phrase.add(new Phrase(createdate[2], FontUnde2)); 
	    		phrase.add(new Phrase("____日签订的编号为", FontDefault2)); 
	    		phrase.add(new Phrase(Lcode+"", FontUnde2)); 
	    		phrase.add(new Phrase("的租赁合同，下列租赁物已确实收到，并在", FontDefault2)); 
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
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
//				if(baseinfo.get("START_DATE")==null||startdate.equals("1900-01-01")){
//					tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于 ____年 ____月 ____日正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//				}else{
//					//为日期加入下划线
//		    		phrase = new Phrase("本日由我方验收合格并订于", FontDefault2); 
//		    		phrase.add(new Phrase(startdate+"", FontUnde2)); 
//		    		phrase.add(new Phrase("正式起租 。特此通知！", FontDefault2)); 
//		    		tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));							
////		    		tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于"+startdate+"正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));							
//				}
	    		tT.addCell(makeCellSetColspanNoBorder("本日由我方验收合格并订于 ____年 ____月 ____日正式起租 。特此通知！", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		
	    		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,6));				
	    		i++;
				//第一个子表开始,第一行
	    		//租赁物的数量是动态增加的
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("名称", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("型号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan2NoBottomAndRight("机号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspan3("单位及数量", PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		String thing_kind = "";
	    		String name = "";
	    		String brand="";
	    		List manufacturerList = new ArrayList() ;
	    		
	    		//Add by Michael 2012 07-10 增加供应商列表
	    		List brandList = new ArrayList();
	    		
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
	    				//Add by Michael 2012 07-10 增加供应商列表
	    				if (!brand.equals(((HashMap)leaseholds.get(k)).get("BRAND").toString())){
	    	    			brandList.add(((HashMap)leaseholds.get(k)).get("BRAND").toString());
	    				}
	    				brand=((HashMap)leaseholds.get(k)).get("BRAND").toString();
	    			}
	    			

	    			
	    			//改为制造商以前是厂牌
	    			if(((HashMap)leaseholds.get(k)).get("MANUFACTURER")==null){
	    				thing_kind="";
	    			}else{
	    				//Add by Michael 2012 07-10 增加供应商列表
	    				if (!thing_kind.equals(((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString())){
	    					manufacturerList.add(((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString());
	    				}
	    				
	    				thing_kind=((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString();
	    			}
	    			
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
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}else{
		    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan2NoBottomAndRight(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
						tT.addCell(makeCellSetColspan3(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault222,1));
			    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
		    		}
		    		if(i<55&&i%51==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    			}else if(i>55&&(i-51)%54==0){
	    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    				i+=3;
	    			}
	    		//所租机械循环显示完毕
	    		}
	    		//System.out.println(i+"xunhuanwanbi");
	    		//所租机械循环显示完毕
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
	    		}else{
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					tT.addCell(makeCellSetColspan3("  ", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	 
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("制造商：", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
//		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//					tT.addCell(makeCellSetColspan3NoTop(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
//		    		i+=1;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else if(i>55&&(i-51)%54==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
//		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//					tT.addCell(makeCellSetColspan3NoTop(thing_kind, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
//		    		i+=1;
    				for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}else{
	    			for(int j = 0;j<manufacturerList.size();j++){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				if(j != manufacturerList.size() - 1){
	    					tT.addCell(makeCellSetColspan2(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				} else {
	    					tT.addCell(makeCellSetColspan3NoTop(manufacturerList.get(j).toString(), PdfPCell.ALIGN_LEFT, FontDefault2,4));	
	    				}
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
			    		i+=1;
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("租赁期间", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
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
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				if(leasePeriod != null && leasePeriod > 0){
    					tT.addCell(makeCellSetColspan3("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				} else {
    					tT.addCell(makeCellSetColspan3("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
    				}
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(leasePeriod != null && leasePeriod > 0){
		    			tT.addCell(makeCellSetColspan2("         "+ fmYear +"  年（   " +leasePeriod+ "  期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
		    		} else {
		    			tT.addCell(makeCellSetColspan2("         ____年（   ____期，每期1个月，以本租赁物验收证明暨起租通知书", PdfPCell.ALIGN_LEFT, FontDefault2,4));
		    		}
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	    		
	    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2("所记载的起租日或以租赁合同第4条第（1）款的标准计算的日期为起租日）", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("     ",PdfPCell.ALIGN_LEFT, FontDefault));	    			    		
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i<55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所:"+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    				
    			}else{
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("租赁物设置场所: "+eAddress, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		}
	    		i+=1;

	    		if(i<55&&i%51==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;	
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;	
    			}
	    		
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		i+=3;

	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("承租方:   "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,3));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;
	    		/*for(;i<p*50;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    		}
	    		if(i<=p*50){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    		}
	    		*/
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    				
    			}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("地址:   "+custaddress, PdfPCell.ALIGN_LEFT, FontDefault2,3));
//				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		i+=1;
	    		if(i<55&&i%51==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}else if(i>55&&(i-51)%54==0){
    				tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
    				tT.addCell(makeCellSetColspanNoBorder(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
    				i+=3;
    			}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("法人代表或授权人:", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("（签章）", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder("日期:", PdfPCell.ALIGN_LEFT, FontDefault2,1));
				tT.addCell(makeCellSetColspanNoBorder(" ", PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=1 ;
	    		if(i<53){
	    			for(;i<49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
		    		if(i<=49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
	    		}else if(i>53){
		    		int p=Math.round((i-49)/52)+1;
		    		for(;i<p*52+49;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
		    		}
		    		if(i<=p*52+49){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));
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
		
		//add by ShenQi 插入系统日志
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

    //Add by Michael 2012 3-26 导出重车确认书
    public void preCarPdfFromCredit(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarDateMapFromCredit(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarDateMapFromCredit(context);
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
public void  getCarDateMapFromCredit(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
        for(int ii=0; ii< con.length;ii++){	
        	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
	    	   HashMap baseinfo = (HashMap)leaseholds.get(0);
	    	   outputMap.put("BASEINFO", baseinfo);
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showRentCarConfirm.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
        
 }
 
 
 //Add by Michael 2012 3-26 导出重车抵押合同
 public void preCarMortgageRent(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarMortgageRent(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarMortgageRent(context);
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
public void  getCarMortgageRent(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
     for(int ii=0; ii< con.length;ii++){	
     	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志
	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车抵押合同",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));	    	   
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showCarMortgageRent.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
     
}

//Add by Michael 2012 3-26 导出重车承诺书
public void preCarPromiseGrant(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarPromiseGrant(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarPromiseGrant(context);
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
public void  getCarPromiseGrant(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
   for(int ii=0; ii< con.length;ii++){	
   	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志

	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车承诺书",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showCarPromiseGrant.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}

//Add by Michael 2012 3-26 导出重车确认函
public void preCarConfirmationLetter(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarConfirmationLetter(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarConfirmationLetter(context);
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
public void  getCarConfirmationLetter(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
 for(int ii=0; ii< con.length;ii++){	
 	outputMap= new HashMap();
 	List leaseholds =new ArrayList();
 	context.contextMap.put("credit_id",  con[ii]);
	       try{
	    	   context.contextMap.put("PRCD_ID",  context.contextMap.get("credit_id"));

	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志
				List<Map> eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				Map info=(Map)DataAccessor.query("rentContract.queryContractPrjt",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				double sumShuiPrice = 0.0d;
				double sumUnitPrice = 0.0d;
				double sumTotalPrice = 0.0d;
				String agrate="0";
				String agprice="0";
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
				
				if(info.get("PLEDGE_ENTER_AGRATE")!=null){
					agrate=info.get("PLEDGE_ENTER_AGRATE").toString();
				}
				if(info.get("PLEDGE_ENTER_AG")!=null){
					agprice=info.get("PLEDGE_ENTER_AG").toString();
				}
				Double totalPrice=sumTotalPrice-Double.parseDouble(info.get("PLEDGE_AVE_PRICE").toString());
				outputMap.put("TOTALPRICE",totalPrice);
				outputMap.put("AGPRICE",agprice);
				outputMap.put("SUMSHUIPRICE",sumShuiPrice);
				outputMap.put("INFOMAP",info);
				outputMap.put("eqmts",eqmts);			
				
				BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车确认函",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showCarConfirmationLetter.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}


//Add by Michael 2012 3-26 导出重车起租通知书
public void preCarRentNotice(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarRentNotice(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarRentNotice(context);
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
public void  getCarRentNotice(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
for(int ii=0; ii< con.length;ii++){	
	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志
	    	   HashMap baseinfo = (HashMap)leaseholds.get(0);
	    		Integer leasePeriod = null ;
	    		String fmYear = "";
	    	   if(baseinfo.get("LEASE_PERIOD")!= null){
		    		leasePeriod = Integer.parseInt( baseinfo.get("LEASE_PERIOD").toString()) ;
		    		if(leasePeriod != null && leasePeriod>0){
		    			NumberFormat formatNum = new DecimalFormat("##0.##");
		    			double year = Math.round(leasePeriod/12.0 * 100.0) /100.0 ;
		    			fmYear = formatNum.format(year) ;
		    		}
	    		}
	    	   outputMap.put("BASEINFO", baseinfo);
	    	   outputMap.put("FMYEAR", fmYear);
	    	   
	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车起租通知书",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showCarRentNotice.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}

//Add by Michael 2012 3-26 导出重车承诺书
public void preRentEntrustManageAgreement(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getRentEntrustManageAgreement(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getRentEntrustManageAgreement(context);
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
public void  getRentEntrustManageAgreement(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
 for(int ii=0; ii< con.length;ii++){	
 	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志

	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车租赁物委托管理协议",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showRentEntrustManageAgreement.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}


//Add by Michael 2012 3-26 导出重车挂靠授权书
public void preRelyGrantBook(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getRelyGrantBook(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getRelyGrantBook(context);
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
public void  getRelyGrantBook(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
for(int ii=0; ii< con.length;ii++){	
	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志

	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车挂靠授权书",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showRelyGrantBook.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}

//Add by Michael 2012 3-26 导出重车抵押过户授权书
public void preCarMortgageSetupGrant(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	        			 this.getCarMortgageSetupGrant(context);
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                		 this.getCarMortgageSetupGrant(context);
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
public void  getCarMortgageSetupGrant(Context context){
	 String[]  con = null;
	 ArrayList creditmapandleasehold=new ArrayList();
	  con= (String[]) context.contextMap.get("credtdxx");
	  HashMap outputMap=null;
for(int ii=0; ii< con.length;ii++){	
	outputMap= new HashMap();
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
	    	   outputMap.put("LEASECODE", (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP));
		    	// 插入系统日志

	    	   BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),null,
	    							   "导出重车抵押过户授权书",
	    							   "合同浏览导出合同",
	    							   null,
	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	    							   1,
	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
	    	   creditmapandleasehold.add(outputMap);
	    	   Output.jspOutput(outputMap, context, "/rentcontract/showCarMortgageSetupGrant.jsp");
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
}


public void prePassWordNote(Context context){
	String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
	
	 if(con != null ){
	   if(!(con[0].equals("00"))){
		 try {
        		 if(con.length >1){
        			 context.contextMap.put("credtdxx",  con);
        			 this.getPassWordNoteDateMap(context);
        			 //this.expLeaseHoldPdfs(context);             	    
        		 }else{
                	if(con.length ==1){
                		 context.contextMap.put("credtdxx",   con);
                		 this.getPassWordNoteDateMap(context);
//                		 this.expLeaseHoldPdfs(context);
                	} 
        		}
		} catch (Exception e) {
			    e.printStackTrace();
			    LogPrint.getLogStackTrace(e, logger);
		}
	  }
	}	
  }

public void  getPassWordNoteDateMap(Context context){
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
	    		   leaseholds = (List) DataAccessor.query("exportContractPdf.queryPasswordNoteByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    	   }else{
	    		   leaseholds = (List) DataAccessor.query("exportContractPdf.queryPasswordNoteEquipmentByRectId", context.getContextMap(), DataAccessor.RS_TYPE.LIST);   
	    	   }
	    	   outputMap.put("leaseholds", leaseholds);
	    	   creditmapandleasehold.add(outputMap);
	    	 
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   LogPrint.getLogStackTrace(e, logger);
	       }
	    }
       this.expPassWordNote(context,creditmapandleasehold);
}
/**
 * 导出密码作业通知书
 * @param context
 */
@SuppressWarnings("unchecked")
public void expPassWordNote(Context context,ArrayList creditmapandleasehold) {

ByteArrayOutputStream baos = null;
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
		String Lcode = "";
		if(baseinfo.get("LEASE_CODE")==null){
			Lcode = "无编号";
    	}else{
    		Lcode = baseinfo.get("LEASE_CODE").toString();
    	}
		
		float[] widthsStl = {0.2f,0.3f,0.3f,0.3f,0.3f,0.2f};
		PdfPTable tT = new PdfPTable(widthsStl);
		tT.setWidthPercentage(100f);
		int i=0;
		if(code.equals("0")){
			tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,6));	
			i++;
			tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
			i++;
//			tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
//			i++;
			tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6)); 
			i++;
			tT.addCell(makeCellSetColspan2("密码作业通知说明书",PdfPCell.ALIGN_CENTER, fa,6));
			i++;
			tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,6));
			i++;
    		
			tT.addCell(makeCellSetColspan2("（合同编号: "+Lcode+"     承租人: "+custname+"）",PdfPCell.ALIGN_CENTER, FontDefault,6));
			i++;
    		
			Phrase phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("敬爱的客户：", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("非常感谢您使用本公司提供的融资租赁服务，谨致谢忱。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;   		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("本次租赁之机器设备某些特定机型出厂时即具有锁码功能，若未能于时限内输入密码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;     		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("将引致机器设备停机，为维护贵户权益，特将锁码方式及密码通知相关事宜专函说明", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;  
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("如后:", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("壹、", FontDefault2)); 
    		phrase.add(new Phrase("	锁码日期", FontUnde2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("    ", FontDefault2)); 
    		phrase.add(new Phrase("锁码日期为每月____日 ,锁码日每月固定日期。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;      		
    			
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("貳、", FontDefault2)); 
    		phrase.add(new Phrase("	锁码方式", FontUnde2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ 一、直接锁码:时限到直接锁码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        须于最后时限到前取得本公司提供之密码以凭译码，否则时限一到，机器设备", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 

    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        即锁住停机。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;  

    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("    二、间接锁码:", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ （1）时限到前几天先跳出提示码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        于机器设备跳出提示码时,请即告知本公司提示码内容以凭向供货商要求提供密", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        码译码。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ （2）时限到直接锁码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        机器设备到了设定的锁码日自动锁码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("叁、", FontDefault2)); 
    		phrase.add(new Phrase("	密码通知作业", FontUnde2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ 一、直接锁码-时限到直接锁码方式", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        本公司于贵户当期租金入账后即会由专人负责以电邮方式提供密码至贵户指定", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	       之电邮信箱。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ 二、间接锁码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ （1）时限到前几天先跳出提示码方式", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("	        原则上机器设备均会于时限日到前五天跳出提示码,请贵户实时通知本公司提示", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("码内容，以便本公司向供货商要求提供密码再由专人负责以电邮方式提供密码至贵户", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("指定之电邮信箱。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  □ （2）时限到机器不会跳出提示码直接锁码", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("     设备均会于租金缴付日的后几天不跳出提示码直接锁码，只要确认租金已经到账，", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("     本公司会向供货商要求提供密码再由专人负责以电邮方式提供密码至贵户指定之电", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("     邮信箱。", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("肆、", FontDefault2)); 
    		phrase.add(new Phrase("	密码作业联络窗口", FontUnde2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("    本公司密码作业系由本公司业管部专责处理，如有任何问题请随时来电", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("    洽询 业管部： ", FontDefault2)); 
    		phrase.add(new Phrase("+86-512-80983566 分机号：88200", FontUnde2));
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
//    		i++;   
//    		phrase = new Phrase("", FontDefault2);  
//    		phrase.add(new Phrase("                            手机：15995769745", FontDefault2)); 
//    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
//			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
//    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++; 
    		phrase = new Phrase("", FontDefault2);  
    		phrase.add(new Phrase("  本案目标物明细", FontDefault2)); 
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspanNoBorder(phrase, PdfPCell.ALIGN_LEFT, FontDefault2,4));		
    		tT.addCell(makeCellWithBorderRight("  ",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;

    		//第一个子表开始,第一行
    		//租赁物的数量是动态增加的
    		tT.addCell(makeCellWithBorderLeft("  ",PdfPCell.ALIGN_LEFT, FontDefault));
			tT.addCell(makeCellSetColspan("名称", PdfPCell.ALIGN_LEFT, FontDefault2,1));
			tT.addCell(makeCellSetColspan("型号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
			tT.addCell(makeCellSetColspan("机号", PdfPCell.ALIGN_LEFT, FontDefault2,1));
			tT.addCell(makeCellSetColspan("单位及数量", PdfPCell.ALIGN_LEFT, FontDefault2,1));
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

    			if(((HashMap)leaseholds.get(k)).get("MANUFACTURER")==null){
    				thing_kind="";
    			}else{
    				thing_kind=((HashMap)leaseholds.get(k)).get("MANUFACTURER").toString();
    			}
    			manufacturerList.add(thing_kind) ;
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
				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
				tT.addCell(makeCellSetColspan(modelspec, PdfPCell.ALIGN_LEFT, FontDefault222,1));
				tT.addCell(makeCellSetColspan(name, PdfPCell.ALIGN_LEFT, FontDefault222,1));
				tT.addCell(makeCellSetColspan(amount+unit, PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));
    		i++;
    		}
    		
//			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//			tT.addCell(makeCellSetColspan4NoTop("      ", PdfPCell.ALIGN_LEFT, FontDefault2,4));
//    		tT.addCell(makeCellWithBorderRight("",PdfPCell.ALIGN_LEFT, FontDefault));

			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,6));	
		}
		document.add(tT);
		document.add(Chunk.NEXTPAGE);
       }
		document.close();
    // 支付表PDF名字的定义
    String strFileName =  "PasswordNote.pdf";
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
	   		 "导出 密码作业通知书",
   		 	 "合同管理导出 密码作业通知书",
   		 	 null,
   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理导出 密码作业通知书功能",
   		 	 1,
   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
   		 	 DataUtil.longUtil(0),
   		 	 context.getRequest().getRemoteAddr());
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
    /** 创建 有边框 合并 单元格|-
     *  无下边用于表格的顶
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoBottomAndRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthRight(0);
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

    /** 创建 下边框 单元格_
     *  无上边
     *  
     *  */
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



    /** 创建 无边框 合并 单元格
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
	objCell.setPaddingTop(5) ;
	objCell.setPaddingBottom(5) ;
	return objCell;
    }
    /**创建 无边框 合并 单元格 
     * 
     */
    private PdfPCell makeCellSetColspanNoBorder(Phrase phrase, int align,
    		Font FontDefault, int colspan) {
    	Phrase objPhase = phrase ;
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthTop(0);
    	objCell.setBorderWidthLeft(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setPaddingTop(7) ;
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

    /* 创建 有边框 四边都有 合并 单元格|-_|
     * 
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

}