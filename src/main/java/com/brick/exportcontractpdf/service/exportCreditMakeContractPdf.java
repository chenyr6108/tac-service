package com.brick.exportcontractpdf.service;

import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.AService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.HashMap;
import java.util.List;

import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
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
import com.brick.log.service.LogPrint;
//租赁物验收证明暨起租通知书
public class exportCreditMakeContractPdf extends AService {
	Log logger = LogFactory.getLog(exportCreditMakeContractPdf.class);

	//接受页面报告的ID数组
	 @SuppressWarnings("unchecked")
	    public void prePdf(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		
		 if(con != null ){
		   if(!(con[0].equals("00"))){
			 try {
	        		 if(con.length >1){
	        			 context.contextMap.put("credtdxx",  con);
	                     this.exportCreditMakeConPdf(context);             	    
	        		 }else{
	                	if(con.length ==1){
	                		 context.contextMap.put("credtdxx",   con);
	                         this.exportCreditMakeConPdf(context);
	                	} 
	        		}
			} catch (Exception e) {
				    e.printStackTrace();
				    LogPrint.getLogStackTrace(e, logger);
			}
		  }
		}	
	  }
    /**
     * 导出报告相关的文件
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void exportCreditMakeConPdf(Context context) {
	
	ByteArrayOutputStream baos = null;
 	Map outputMap = new HashMap();
 	String[]  con = null;
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
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
	        con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< con.length;ii++){
		    	
	        	context.contextMap.put("credit_id",  con[ii]);
	        //导出的pdf文件所需要的数据全部
	        SelectReportInfo.selectReportInfo_zulin(context, outputMap);    	    			    
		    String songjiandanwei = "";
		    String yingyerenyuan="";
		    String quanxianbie="";
		    String pifushuhao="";
		    String shoujianri="";
		    String cust_name="";
		    String farendaibiao="";
		    String yingyezhizhaozhucehao="";
		    String yuewudengjihao="";
		    String baogaoleixing="";
		    String kehuedu="";
		    String zulinfangshi="";
		    String gongyingshangedu="";
		    String gongyingshang="";
		    String gysyingyezhizhaozhucehao="";

		    if(outputMap.get("newbeforemap")!=null){
		    	if(((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN")!=null){
		    		songjiandanwei = ((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME")!=null){
		    		yingyerenyuan = ((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE")!=null){
		    		shoujianri = ((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE").toString().substring(0,10);
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON")!=null){
		    		farendaibiao = ((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE")!=null){
		    		yingyezhizhaozhucehao = ((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER")!=null){
		    		yuewudengjihao = ((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER").toString();
		    	}

		    }
		    //权限别需要待定
		    if(outputMap.get("rank")!=null){
		    	if(((HashMap)outputMap.get("rank")).get("RANK")!=null){
		    		quanxianbie = ((HashMap)outputMap.get("rank")).get("RANK").toString();
		    	}
	    	}
		    if(outputMap.get("customeredu")!=null){
		    	if(((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE").toString();
		    	}
	    	}
		    if(outputMap.get("applydu")!=null){
		    	if(((HashMap)outputMap.get("applydu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("applydu")).get("GRANT_PRICE").toString();
		    	}
		    	if(((HashMap)outputMap.get("applydu")).get("NAME")!=null){
		    		gongyingshang = ((HashMap)outputMap.get("applydu")).get("NAME").toString();
		    	}
	    	}
		    if(outputMap.get("contractType")!=null&&((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE")!=null){
		    	for(int y=0;y<((List)outputMap.get("contractType")).size();y++){
		    		if(((HashMap)((List)outputMap.get("contractType")).get(y)).get("CODE").toString().equals(((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE").toString())){
		    			zulinfangshi = ((HashMap)outputMap.get("contractType")).get("CODE").toString();
		    		}
		    	}
	    	}
		    if(outputMap.get("creditMap")!=null){
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE")!=null){
		    		pifushuhao=((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CUST_NAME")!=null){
		    		cust_name=((HashMap)outputMap.get("creditMap")).get("CUST_NAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE")!=null){
		    		baogaoleixing=((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE").toString();
		    	}
		    }
			PdfPTable tT = new PdfPTable(14);
			tT.setWidthPercentage(100f);
			int i=0;
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,14));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14)); 
				i++;
				tT.addCell(makeCellSetColspan2("合同生成预览",PdfPCell.ALIGN_CENTER, fa,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,14));
	    		i++;
	    		//表头的相关信息,第一行
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3("承租人信息（法人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3(Constants.COMPANY_NAME, PdfPCell.ALIGN_CENTER, FontDefault2,8));		
				tT.addCell(makeCellSetColspan3NoLeft("送件单位", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan3NoLeft(songjiandanwei, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,8));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft("营业人员", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yingyerenyuan, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("核准函", PdfPCell.ALIGN_CENTER, FontDefault2,8));
				tT.addCell(makeCellSetColspan2NoTopAndLeft("权限别", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(quanxianbie, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("批覆书号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(pifushuhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("收件日", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(shoujianri, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("承租人", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(cust_name, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("法人代表", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(farendaibiao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("营业执照注册号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(yingyezhizhaozhucehao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("税务登记号", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yuewudengjihao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("报告类型", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baogaoleixing, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("客户额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(kehuedu, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("租赁方式", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(zulinfangshi, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("供应商额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(gongyingshangedu, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanOnlyLeft("供应商："+gongyingshang, PdfPCell.ALIGN_CENTER, FontDefault22,6));		
				tT.addCell(makeCellSetColspan2("供应商类别:0", PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_CENTER, FontDefault22,6));		
				tT.addCell(makeCellSetColspan3NoTop("营业执照注册号:"+gysyingyezhizhaozhucehao, PdfPCell.ALIGN_CENTER, FontDefault22,6));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("担保人信息",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		for(int k=0;k<((List)outputMap.get("danbaorens")).size();k++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("担保人名称", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    			String danbaorenname="";
	    			String zhengjianhao="";
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME")!=null){
	    				danbaorenname=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME").toString();
	    			}
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE")!=null){
	    				zhengjianhao=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE").toString();
	    			}
					tT.addCell(makeCellSetColspan2NoTopAndRight(danbaorenname, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
					tT.addCell(makeCellSetColspan2NoTopAndRight("证件号", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
					tT.addCell(makeCellSetColspan3NoTop(zhengjianhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;  
		    		if(i%57==0){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    		}
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("设备内容",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("设备名称/型号", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("厂牌", PdfPCell.ALIGN_LEFT, FontDefault22,4));	
				tT.addCell(makeCellSetColspan3NoTop("总价", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		String shebeizongjia="";
	    		String totalD="";
	    		if(outputMap.get("sheBeiHeX")!=null){
	    			shebeizongjia=nfFSNum.format(Double.parseDouble((outputMap.get("sheBeiHeX")).toString()));
	    			//System.out.println(shebeizongjia.toString().replaceAll(",", ""));
	    			totalD=TfAmt.num2cn(shebeizongjia.toString().replaceAll(",", ""));
	    		}
	    		for(int h=0;h<((List)outputMap.get("equipmentsList")).size();h++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			String thingname="";
	    			String thingkind="";
	    			String changpai="";
	    			Double total=0.0;
	    			
	    			String totals="";
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME")!=null&&((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_KIND")!=null){
	    				thingname=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME").toString();
	    				thingkind=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_KIND").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL")!=null){
	    				total=Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL").toString());
	    				totals=nfFSNum.format(total);
	    				
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC")!=null){
	    				changpai=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC").toString();
	    			}
	    			
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingname+"/"+thingkind, PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan2NoTopAndRight(changpai, PdfPCell.ALIGN_LEFT, FontDefault22,4));	
					tT.addCell(makeCellSetColspan3NoTop(totals, PdfPCell.ALIGN_LEFT, FontDefault22,4));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
		    		if(i%57==0){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("总价:"+totalD, PdfPCell.ALIGN_LEFT, FontDefault22,4));	
				tT.addCell(makeCellSetColspan3NoTop("总价小写:"+shebeizongjia, PdfPCell.ALIGN_LEFT, FontDefault22,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("合同归户",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		double benjinyue=0.0;
	    		if(outputMap.get("schemeMap")!=null){
	    			if(outputMap.get("custguihu")!=null){
	    				if(((HashMap)outputMap.get("custguihu")).get("SHENGYUZUJIN")!=null){
	    					benjinyue=Double.parseDouble(((HashMap)outputMap.get("custguihu")).get("SHENGYUZUJIN").toString());
	    				}	    				
	    			}
	    			if(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());	    				
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("0", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(nfFSNum.format(benjinyue), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("租金余额:0",PdfPCell.ALIGN_LEFT, FontDefault22,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("本案",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==07){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		String benanbenjinyue="";
	    		if(outputMap.get("creditshemadetail")!=null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC")!=null){
	    				benanbenjinyue=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC").toString()));
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("0", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(benanbenjinyue, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款金额", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("0", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("实际月租", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("0", PdfPCell.ALIGN_LEFT, FontDefault2,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		double baozhengjin=0.0;
	    		String baozhengjins = "";
	    		String baozhengjinchengshu="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			baozhengjin=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());
		    			baozhengjins=nfFSNum.format(baozhengjin);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE")!=null){
		    			baozhengjinchengshu=((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE").toString();
		    			
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baozhengjins, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("保证金成数", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(baozhengjinchengshu, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		String tr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE")!=null){
		    			tr=((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE").toString();
		    		}
	    		}
	    		String hetongtr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST")!=null){
		    			hetongtr=((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST").toString();
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("合同TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(hetongtr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("税后TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(tr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金入账金流", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("(1)保證金"+baozhengjins+"RMB元+稅金 RMB元 ", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		String rugongyingshang="";
	    		String ruwosibaozhengjin="";
	    		String shuijin="";
	    		String pingjundichong="";
	    		String qimofanhuan="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG")!=null){
		    			rugongyingshang=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY")!=null){
		    			ruwosibaozhengjin=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			shuijin=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null){
		    			pingjundichong=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			qimofanhuan=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString()));
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("合計"+rugongyingshang+"RMB元入供應商;", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("(2)保證金0RMB元+稅金"+shuijin+"RMB元入我司;我司匯入供應商RMB元 ", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("平均抵冲"+pingjundichong+"RMB元;抵租金RMB元;期末返還"+qimofanhuan+"RMB元  ", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%57==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		}
	    		String zujinshouqufangshi="";
	    		if(outputMap.get("schemeMap")!=null){
	    			for(int zujins=0;zujins<((List)outputMap.get("payWayList")).size();zujins++){
		    			if(((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE")!=null&&((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE").equals(((HashMap)outputMap.get("schemeMap")).get("PAY_WAY").toString())){
		    				zujinshouqufangshi=((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("FLAG").toString();
		    			}
		    		}
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("租金收取方式", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(zujinshouqufangshi, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,2));	
				tT.addCell(makeCellSetColspan3NoTop("拨RMB元予", PdfPCell.ALIGN_LEFT, FontDefault22,1));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		int pagenum=Math.round(i/57)+1;
	    		if(i<pagenum*56){
		    		for(;i<pagenum*56;i++){
		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    		}
		    		if(i<=pagenum*56){
		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
		    		}
	    		}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
	       }
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "shengchenghetongyulan.pdf";
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
    /** 创建 有上下边框 合并 单元格=
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoTopAndBottom(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格-|
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoLeftAndBottom(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthBottom(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoTopAndRight(String content, int align,
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
    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoTopAndLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthBottom(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	objCell.setBorderWidthBottom(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格=
     *  无上下边
     *  
     *  */
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
    /** 创建 有上下边框 合并 单元格=
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan3NoLeftAndTop(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthLeft(0);
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

}