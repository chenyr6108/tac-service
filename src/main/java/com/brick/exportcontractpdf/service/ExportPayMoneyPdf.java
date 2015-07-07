package com.brick.exportcontractpdf.service;

import com.brick.service.core.AService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.HashMap;

import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.core.DataAccessor;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;
//租赁物验收证明暨起租通知书
public class ExportPayMoneyPdf extends AService {
	Log logger = LogFactory.getLog(ExportPayMoneyPdf.class);

	public void exportPayMoney(Context context){
		try{
			Map rentandpuctcontract = new HashMap();
		    rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
		    this.expPayMoneyPdf(context,rentandpuctcontract);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	public void exportPayMoneyNOT(Context context){
		try{
			Map rentandpuctcontract = new HashMap();
			//is=0 没有生成合同 否则已经生成合同
			
			int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(is==0){
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT2", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}else{
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT1", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}
			String bankAccount=(String)context.contextMap.get("bankAccount"+context.contextMap.get("PRCD_ID"));
			rentandpuctcontract.put("bankAccount", bankAccount);
			
		    this.expPayMoneyPdf(context,rentandpuctcontract);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
    /**
     * 导出报告相关的文件
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void expPayMoneyPdf(Context context,Map rentandpuctcontract) {
	
	ByteArrayOutputStream baos = null;
	NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
    nfFSNum.setGroupingUsed(true);
    nfFSNum.setMaximumFractionDigits(2);
 	//Map rentandpuctcontract = new HashMap();
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
//	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
//	        nfFSNum.setGroupingUsed(true);
//	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        	        
	        // 打开文档
	        document.open();

    	  	    	    	
	    	//查找报告的相关信息以及租赁物的相关信息		    
		   // rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
		    String code="";
		    String[] rentdate =new String[3];
		    rentdate[0]="";
    		rentdate[1]="";
    		rentdate[2]="";
		  //租赁合同编号
	        String rentcode = "";
	      //购销合同编号
    		String puctcode = "";
    		//承租方名称
     		String custname="";
     		//供应商
     		String sellername="";
     		//供应商
     		String money = "";
     		String bankaccount="";
     		String openbank="";
		    if(rentandpuctcontract!=null){
		    	if(rentandpuctcontract.get("CONTRACT_TYPE")!=null){
		    		code=rentandpuctcontract.get("CONTRACT_TYPE").toString();
		    	}
		    	if(rentandpuctcontract.get("LESSOR_TIME")!=null){
		    		String[] temp = rentandpuctcontract.get("LESSOR_TIME").toString().substring(0,10).split("-");
		    		if(!temp[0].equals("1900")){
		    			rentdate = temp ;
		    		}
		    	}
		   
		        
	    		if(rentandpuctcontract.get("LEASE_CODE")==null){
	    			rentcode = "  ";
		    	}else{
		    		rentcode = rentandpuctcontract.get("LEASE_CODE").toString();
		    	}
	    		
	     		if(rentandpuctcontract.get("PUCT_CODE")==null){
	     			puctcode = "  ";
	 	    	}else{
	 	    		puctcode = rentandpuctcontract.get("PUCT_CODE").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("CUST_NAME")==null){
	     			custname = "  ";
	 	    	}else{
	 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("NAME")==null){
	     			sellername = "  ";
	 	    	}else{
	 	    		sellername=rentandpuctcontract.get("NAME").toString();
	 	    	}
	     		if(rentandpuctcontract.get("MONEY")==null){
	     			money = "  ";
	     		}else{
	     			money=nfFSNum.format(rentandpuctcontract.get("MONEY"));
	     		}
	     		if(rentandpuctcontract.get("BANK_ACCOUNT")!=null){
	     			bankaccount = rentandpuctcontract.get("BANK_ACCOUNT").toString();;
	     		}
	     		if(rentandpuctcontract.get("OPEN_ACCOUNT_BANK")!=null){
	     			openbank = rentandpuctcontract.get("OPEN_ACCOUNT_BANK").toString();;
	     		}
		    }
			PdfPTable tT = new PdfPTable(new float[]{10f,20f,20f,20f,20f,20f,10f});
			tT.setWidthPercentage(100f);

			
			int i=0;
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
				i++;
				tT.addCell(makeCellSetColspan2("关于设备价款的支付指示",PdfPCell.ALIGN_CENTER, fa,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("致："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日   贵   公   司   与   我   们   签   署   的   租   赁   合   同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//				tT.addCell(makeCellSetColspanNoBorder("根   据   "+(rentdate[0].length() ==0 ? "___" :rentdate[0])+"年 "+(rentdate[1].length() ==0 ? "___" :rentdate[1])+" 月 "+(rentdate[2].length() ==0 ? "___" :rentdate[2])+" 日   贵   公   司   与   我   们   签   署   的   租   赁   合   同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("[      "+rentcode+"      ]   和   买   卖   合   同   [         "+rentcode+"         ]，", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("请   贵   公   司   代   替   我   方   将   货   款   支   付   至   以   下   帐   户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;

	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("户名："+sellername, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("金额：   " + money +" 元", PdfPCell.ALIGN_LEFT, FontDefault2,5));
//				tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("承租方： "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		for(;i<51;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}
	    		if(i<=51){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}
	    	}
			else if(code.equals("3")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
				i++;
				tT.addCell(makeCellSetColspan2("关于设备价款的支付指示",PdfPCell.ALIGN_CENTER, fa,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("致："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日   贵   公   司   与   我   们   签   署   的   租   赁   合   同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//				tT.addCell(makeCellSetColspanNoBorder("根   据   "+(rentdate[0].length() ==0 ? "___" :rentdate[0])+"年 "+(rentdate[1].length() ==0 ? "___" :rentdate[1])+" 月 "+(rentdate[2].length() ==0 ? "___" :rentdate[2])+" 日   贵   公   司   与   我   们   签   署   的   租   赁   合   同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("[      "+rentcode+"      ]   和   买   卖   合   同   [         "+rentcode+"         ]，", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("请   贵   公   司   代   替   我   方   将   货   款   支   付   至   以   下   帐   户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;

	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("户名："+sellername, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("金额：   " + money +" 元", PdfPCell.ALIGN_LEFT, FontDefault2,5));
//				tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("承租方： "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		for(;i<51;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}
	    		if(i<=51){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}				
			}
			else if(code.equals("1")||"5".equals(code)){//5：新品回租
//	    		tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
//				i++;
//				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
//				i++;
//				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
//				i++;
//				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
//				i++;
//				tT.addCell(makeCellSetColspan2("设备价款付款指示",PdfPCell.ALIGN_CENTER, fa,7));
//				i++;
//				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
//				i++;
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
//	    		i++;
//	    		//表头的相关信息,第一行
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
//	    		tT.addCell(makeCellSetColspanNoBorder("致：裕融租赁（苏州）有限公司",PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
//	    		i++;
//	    		
//	    		//文字部分
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("根据 _____年____月 ____日贵公司与我们签署的租赁合同   合同号:[ "+rentcode+" ]和委托", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
////				tT.addCell(makeCellSetColspanNoBorder("根   据   "+(rentdate[0].length() == 0 ? "___" :rentdate[0])+"年 "+(rentdate[1].length() ==0 ? "___" :rentdate[1])+" 月 "+(rentdate[2].length() ==0 ? "___" :rentdate[2])+" 日贵公司与我们签署的租赁合同   合同号:[ "+rentcode+" ]和委托", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("购买合同     合同号：[      "+rentcode+ "      ]，我们已经与[  "+sellername+"  ]", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("（“供应商”）于    "+rentdate[0]+"   年   "+rentdate[1]+"   月    "+rentdate[2]+"   日签订销售合同，我们已经验收供应商交", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("付的所有设备，所有设备均处于良好外观及工作状态，并符合我们的用途。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("请贵公司代替我方将货款支付至以下帐户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("户名： "+sellername, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("金额：   "+money+" 元", PdfPCell.ALIGN_LEFT, FontDefault2,3));
//				tT.addCell(makeCellSetColspanNoBorder("  ", PdfPCell.ALIGN_LEFT, FontDefaultTitle,2));
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("我们确认贵公司对设备质量瑕疵并不承担任何责任。我们在租赁合同项下均无任", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//				tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("何违约，并将严格遵守该等合同，无条件支付租赁合同项下所有租金和应付款项。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("承租方："+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		
//	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//				tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
//	    		i++;
//	    		for(;i<51;i++){
//	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
//	    		}
//	    		if(i<=51){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
//	    		}
	    		FontDefault = new Font(bfChinese, 12, Font.NORMAL);
	    		//文本宽度
	    		final float  TEXTWIDE = 80f ;
	    		
	    		document.add(new Paragraph("\n"));
	    		
	    		PdfPTable t1 = new PdfPTable(1);
				t1.setWidthPercentage(100f);
				t1.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_CENTER, fa, 1));
				t1.addCell(makeCellSetColspanWithNoBorder("设备价款付款指示", PdfPCell.ALIGN_CENTER, fa, 1));
				document.add(t1);
				document.add(new Paragraph("\n"));
				document.add(new Paragraph("\n"));

				PdfPTable t2 = new PdfPTable(2);
				t2.setWidthPercentage(TEXTWIDE);
				t2.addCell(makeCellSetColspanWithNoBorder("致："+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
				document.add(t2);

				
				PdfPTable t9 = new PdfPTable(1);
			    t9.setWidthPercentage(TEXTWIDE);
				PdfPCell objCell2 = new PdfPCell();
			    Phrase phrase2 = new Phrase();
			    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
			    objCell2.setBorder(0);
		    	Chunk chunk2 = new Chunk("根据 _____年____月 ____日贵公司与我们签署的租赁合同 ",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("合同号:["+rentcode +"]和委托购买合同",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("合同号：[" +rentcode+ "]，我们已经与",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("["+sellername+"]",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("（“供应商”）于_____年____月 ____日",FontDefault);
//		    	chunk2 = new Chunk("（“供应商”）于    "+rentdate[0]+"   年   "+rentdate[1]+"     月    "+rentdate[2]+"   日",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("签订销售合同，我们已经验收供应商交付的所有设备，所有设备均处于良好外观及工作状态，并符合我们的用途。\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("请贵公司代替我方将货款支付至以下帐户。",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	//add by ShenQi 拿页面选择的开户帐号
		    	String [] bankInfo=((String)rentandpuctcontract.get("bankAccount")).split("=");
		    	
		    	chunk2 = new Chunk("户名： "+bankInfo[0] +"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("账号： "+bankInfo[1]+"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("开户银行： "+bankInfo[2]+"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("金额：   "+nfFSNum.format(Double.valueOf(bankInfo[3]))+" 元\n",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("我们确认贵公司对设备质量瑕疵并不承担任何责任。我们在租赁合同项下均无任" +
						"何违约，并将严格遵守该等合同，无条件支付租赁合同项下所有租金和应付款项。",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	chunk2 = new Chunk("顺祝 商祺",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	
		    	chunk2 = new Chunk("\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("承租方："+custname,FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("日期：",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	
		    	
		    	phrase2.setLeading(25f) ;
		    	objCell2.addElement(phrase2);
		    	t9.addCell(objCell2);
				document.add(t9);
	    	}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
	     //  }
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "AboutEquipMoneyPay.pdf";
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
		BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
		   		 "导出 付款请示书或请款书(合同类型:"+code+")",
	   		 	 "合同浏览导出 付款请示书或请款书",
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
    
    //Add by Michael 2012 4-1导出重车付款指示书
    public void exportCarPayMoneyNOT(Context context){
		try{
			Map rentandpuctcontract = new HashMap();
			//is=0 没有生成合同 否则已经生成合同
			
			int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
			if(is==0){
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT2", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}else{
				rentandpuctcontract = (Map) DataAccessor.query("exportContractPdf.queryTwoContractByPrcdIdNOT1", context.getContextMap(), DataAccessor.RS_TYPE.MAP);	        
			}
			String bankAccount=(String)context.contextMap.get("bankAccount"+context.contextMap.get("PRCD_ID"));
			rentandpuctcontract.put("bankAccount", bankAccount);
			
		    this.expCarPayMoneyPdf(context,rentandpuctcontract);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
    
    //Add by Michael 2012 4-1导出重车付款指示书    
    @SuppressWarnings("unchecked")
    public void expCarPayMoneyPdf(Context context,Map rentandpuctcontract) {
	
	ByteArrayOutputStream baos = null;
 	//Map rentandpuctcontract = new HashMap();
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
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

		    String code="";
		    String[] rentdate =new String[3];
		    rentdate[0]="";
    		rentdate[1]="";
    		rentdate[2]="";
		  //租赁合同编号
	        String rentcode = "";
	      //购销合同编号
    		String puctcode = "";
    		//承租方名称
     		String custname="";
     		//供应商
     		String sellername="";
     		//供应商
     		String money = "";
     		String bankaccount="";
     		String openbank="";
		    if(rentandpuctcontract!=null){
		    	if(rentandpuctcontract.get("CONTRACT_TYPE")!=null){
		    		code=rentandpuctcontract.get("CONTRACT_TYPE").toString();
		    	}
		    	if(rentandpuctcontract.get("LESSOR_TIME")!=null){
		    		String[] temp = rentandpuctcontract.get("LESSOR_TIME").toString().substring(0,10).split("-");
		    		if(!temp[0].equals("1900")){
		    			rentdate = temp ;
		    		}
		    	}
		   
		        
	    		if(rentandpuctcontract.get("LEASE_CODE")==null){
	    			rentcode = "  ";
		    	}else{
		    		rentcode = rentandpuctcontract.get("LEASE_CODE").toString();
		    	}
	    		
	     		if(rentandpuctcontract.get("PUCT_CODE")==null){
	     			puctcode = "  ";
	 	    	}else{
	 	    		puctcode = rentandpuctcontract.get("PUCT_CODE").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("CUST_NAME")==null){
	     			custname = "  ";
	 	    	}else{
	 	    		custname=rentandpuctcontract.get("CUST_NAME").toString();
	 	    	}
	     		
	     		if(rentandpuctcontract.get("NAME")==null){
	     			sellername = "  ";
	 	    	}else{
	 	    		sellername=rentandpuctcontract.get("NAME").toString();
	 	    	}
	     		if(rentandpuctcontract.get("MONEY")==null){
	     			money = "  ";
	     		}else{
	     			money=nfFSNum.format(rentandpuctcontract.get("MONEY"));
	     		}
	     		if(rentandpuctcontract.get("BANK_ACCOUNT")!=null){
	     			bankaccount = rentandpuctcontract.get("BANK_ACCOUNT").toString();;
	     		}
	     		if(rentandpuctcontract.get("OPEN_ACCOUNT_BANK")!=null){
	     			openbank = rentandpuctcontract.get("OPEN_ACCOUNT_BANK").toString();;
	     		}
		    }
			PdfPTable tT = new PdfPTable(new float[]{10f,20f,20f,20f,20f,20f,10f});
			tT.setWidthPercentage(100f);

			
			int i=0;
			if(code.equals("3")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,7));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7)); 
				i++;
				tT.addCell(makeCellSetColspan2("关于设备价款的支付指示",PdfPCell.ALIGN_CENTER, fa,7));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,7));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspanNoBorder("致："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefaultTitle,5));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));
	    		i++;
	    		
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日   贵   公   司   与   我   们   签   署   的  融  资 租   赁   合   同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("[合同号："+rentcode+"      ] 和委托购买合同  [合同号："+rentcode+"         ]，", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("我方已经与["+sellername+"]于______年____月____日签订销售合同。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("我方已经验收供应商交付的所有设备，所有设备均符合合同项下的约定内容且具", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("备良好的外观及工作状态处于良好，并符合我方的用途。请贵公司代替我方将货", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("款支付至以下帐户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("户名："+sellername, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("账号： "+bankaccount, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("开户银行： "+openbank, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("金额：   " + money +" 元", PdfPCell.ALIGN_LEFT, FontDefault2,5));
//				tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;

	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("我方确认贵公司对设备质量瑕疵并不承担任何责任。我方在融资租赁合同项下均", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("无任何违约行为，并将严格遵守签订的所有合同，无条件支付融资租赁合同项下", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("所有租金和其他应付款项。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("顺祝    商祺", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("承租方： "+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNoBorder("日期：", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		for(;i<51;i++){
	    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}
	    		if(i<=51){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,7));
	    		}				
			}
			else if(code.equals("1")){

	    		FontDefault = new Font(bfChinese, 12, Font.NORMAL);
	    		//文本宽度
	    		final float  TEXTWIDE = 80f ;
	    		
	    		document.add(new Paragraph("\n"));
	    		
	    		PdfPTable t1 = new PdfPTable(1);
				t1.setWidthPercentage(100f);
				t1.addCell(makeCellSetColspanWithNoBorder(" ", PdfPCell.ALIGN_CENTER, fa, 1));
				t1.addCell(makeCellSetColspanWithNoBorder("设备价款付款指示", PdfPCell.ALIGN_CENTER, fa, 1));
				document.add(t1);
				document.add(new Paragraph("\n"));
				document.add(new Paragraph("\n"));

				PdfPTable t2 = new PdfPTable(2);
				t2.setWidthPercentage(TEXTWIDE);
				t2.addCell(makeCellSetColspanWithNoBorder("致："+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
				document.add(t2);

				
				PdfPTable t9 = new PdfPTable(1);
			    t9.setWidthPercentage(TEXTWIDE);
				PdfPCell objCell2 = new PdfPCell();
			    Phrase phrase2 = new Phrase();
			    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
			    objCell2.setBorder(0);
		    	Chunk chunk2 = new Chunk("根据 _____年____月 ____日贵公司与我们签署的租赁合同 ",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("合同号:["+rentcode +"]和委托购买合同",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("合同号：[" +rentcode+ "]，我们已经与",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("["+sellername+"]",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("（“供应商”）于_____年____月 ____日",FontDefault);
//		    	chunk2 = new Chunk("（“供应商”）于    "+rentdate[0]+"   年   "+rentdate[1]+"     月    "+rentdate[2]+"   日",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("签订销售合同，我们已经验收供应商交付的所有设备，所有设备均处于良好外观及工作状态，并符合我们的用途。\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("请贵公司代替我方将货款支付至以下帐户。",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	String [] bankInfo=((String)rentandpuctcontract.get("bankAccount")).split("=");
		    	chunk2 = new Chunk("户名： "+bankInfo[0] +"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("账号： "+bankInfo[1]+"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("开户银行： "+bankInfo[2]+"\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("金额：   "+bankInfo[3]+" 元\n",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("我们确认贵公司对设备质量瑕疵并不承担任何责任。我们在租赁合同项下均无任" +
						"何违约，并将严格遵守该等合同，无条件支付租赁合同项下所有租金和应付款项。",FontDefault);
		    	phrase2.add(chunk2);

		    	chunk2 = new Chunk("\n\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	chunk2 = new Chunk("顺祝 商祺",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	
		    	chunk2 = new Chunk("\n\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("承租方："+custname,FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("\n",FontDefault);
		    	phrase2.add(chunk2);
		    	chunk2 = new Chunk("日期：",FontDefault);
		    	phrase2.add(chunk2);
		    	
		    	
		    	
		    	phrase2.setLeading(25f) ;
		    	objCell2.addElement(phrase2);
		    	t9.addCell(objCell2);
				document.add(t9);
	    	}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
	     //  }
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "AboutEquipMoneyPay.pdf";
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

}