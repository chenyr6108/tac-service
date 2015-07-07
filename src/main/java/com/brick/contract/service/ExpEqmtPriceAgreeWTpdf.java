package com.brick.contract.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
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
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

		public class ExpEqmtPriceAgreeWTpdf extends AService {
			static	Log logger = LogFactory.getLog(ExpBankNoticepdf.class);
		    /**
		     * 导出PDF 三方支付协议(出租人/承租人/供应商) 委托
		     * @param context
		     */
//			public static void prePdf(Context context){
//				ByteArrayOutputStream baos =  new ByteArrayOutputStream();
//				//导出文件PDF名字的定义
//				String pdfName = "ExpEqmtPriceAgreeWTpdf" ;
//				try{
//					//设置数据
//					Map content = new HashMap() ;
////					select  t2.PLEDGE_ENTER_AG gysh,
////					t2.PLEDGE_ENTER_AGRATE gyshR,
////					t2.PLEDGE_ENTER_CMPRICE MC,
////					t2.PLEDGE_ENTER_MCTOAG  MCTOAG, 
////					t1.LEASE_CODE LEASE_CODE, 合同号
////					t1.CUST_NAME CUST_NAME,
////					t1.LESSEE LESSEE, 供应商名
////					t1.RECT_ID RECT_ID ,
////					t5.PUCT_CODE PUCT_CODE 购销合同号
//					
//					//取出数据
//					context.contextMap.put("RECT_TYPE", 1);
//					List obj = (List<Map>) DataAccessor.query("rentContract.expEqmtPriceAgreepdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
//					
//					content.put("custname", ((Map)obj.get(0)).get("CUST_NAME"));
//					//qian
//					content.put("gysh", ((Map)obj.get(0)).get("GYSH"));
//					content.put("gyshR", ((Map)obj.get(0)).get("GYSHR"));
//					content.put("MC", ((Map)obj.get(0)).get("MC"));
//					content.put("MCTOAG", ((Map)obj.get(0)).get("MCTOAG"));
//					
//					content.put("PUCT_CODE", ((Map)obj.get(0)).get("CUST_NAME"));
//					content.put("LEASE_CODE", ((Map)obj.get(0)).get("LEASE_CODE"));
//					content.put("LESSEENAME", ((Map)obj.get(0)).get("LESSEE"));//供应商名
//					//调用模型
//					model(content,baos) ;
//					//
//			   	    String strFileName = pdfName+".pdf";
//			   	    context.response.setContentType("application/pdf");
//			   	    context.response.setCharacterEncoding("UTF-8");
//			   	    context.response.setHeader("Pragma", "public");
//			   	    context.response.setHeader("Cache-Control",
//			   		    "must-revalidate, post-check=0, pre-check=0");
//			   	    context.response.setDateHeader("Expires", 0);
//			   	    context.response.setHeader("Content-Disposition",
//			   		    "attachment; filename=" + strFileName);
//			   	    ServletOutputStream o = context.response.getOutputStream();
//			   	    baos.writeTo(o);
//			   	    o.flush();
//			   		o.close() ;
//					}catch(Exception e){
//						e.printStackTrace() ;
//						LogPrint.getLogStackTrace(e, logger) ;
//					}
//			}
//			public static void model(Map content,OutputStream baos) throws Exception {
//				//标题名称 : TITLE_NAME
//				//合同编号：LEASE_CODE
//				//收款账号：CUST_CODE
//			 	//定义Cell边框粗细   顺序是：上下左右
//			 	float[] borderStart = {0,0,0,0} ;
//			 	float[] borderEnd = {0,0,0,0} ;
//			 	//定义默认字体
//			 	int[] fontDefault = {-1,-1} ;
//			
//			 	//定义默认边距   顺序是：上下左右
//			 	float[] paddingDefault = {5f,5f,-1f,-1f};
//			 	//定义默认位置    水平，垂直
//			 	int [] alignDefault = {-1,-1} ;//靠左
//			 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
//				//pdf名字
//			 
//			 	
//		 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
//			 
//		 		// 数字格式
//		        NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
//		        nfFSNum.setGroupingUsed(true);
//		        nfFSNum.setMaximumFractionDigits(2);
//		        // 页面设置
//		        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
//		        
//		        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
//		        
//		        
//		    
//		        PdfWriter.getInstance(document, baos);
//		        // 打开文档
//		        document.open();
//		        //写入标题
//		        //t1 承租人部分
//			 	PdfPTable t1 = new PdfPTable(1);
//
//			 	
//			 	
//			 	
//			 	
//			 	
//			 	//标题
//			 	t1.addCell(makeCell(bfChinese,"设备价款支付方式协议书",new int[]{17,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 1)) ;
//				//空白行
//				t1.addCell(makeCell(bfChinese, "  ", fontDefault, new float[]{10,10,0,0}, new float[]{0,0,0,0}, alignCenter, 9)) ; 
//			 	//正文
//			 	t1.addCell(makeCell(bfChinese,"1、    承租人名称   下称“甲方”）与裕融租赁（苏州）有限公司（下称“乙方”）",new int[]{12,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 1)) ;
//			 	t1.addCell(makeCell(bfChinese,"鉴于：",new int[]{12,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 1)) ;
//			 	t1.addCell(makeCell(bfChinese,"鉴于：",new int[]{12,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 1)) ;
//			 	t1.addCell(makeCell(bfChinese,"鉴于：",new int[]{12,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 1)) ;
//
//			
//				//租赁物部分  
//				PdfPTable t2 = new PdfPTable(5) ;
//					//第一行  租赁物名称 、型号、数量 
//				t2.addCell(makeCell(bfChinese, " ", fontDefault,new float[]{0,0,0,0}, new float[]{0,0,0,0},alignCenter, 2)) ;
//				t2.addCell(makeCell(bfChinese, "承租人", new int[]{14,Font.NORMAL}, paddingDefault, new float[]{0.5f,0.5f,0.5f,0},alignCenter, 1)) ;
//				t2.addCell(makeCell(bfChinese, content.get("NAME")== null ? "" : content.get("NAME").toString(), new int[]{14,Font.NORMAL}, paddingDefault, new float[]{0.5f,0.5f,0.5f,0.5f},alignCenter, 2)) ;
//
//				document.add(t1);
//				document.add(t2);
//				document.close();
//			}
			@SuppressWarnings("unchecked")
			public static void prePdf(Context context){
				ByteArrayOutputStream baos = null;
				try {
					 Map content = new HashMap() ;
					 List obj=null;
					 List eqmts = null ;
					//取出数据
					 
					int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
					if(is==0){
						eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
						obj = (List<Map>) DataAccessor.query("rentContract.expEqmtPriceAgreenopdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
					}else{
						eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
						obj = (List<Map>) DataAccessor.query("rentContract.expEqmtPriceAgreepdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
						
					}
					content.put("LEASE_CODE", ((Map)obj.get(0)).get("LEASE_CODE"));
					content.put("CUST_NAME", ((Map)obj.get(0)).get("CUST_NAME"));
					//qian
					content.put("GYSH", ((Map)obj.get(0)).get("GYSH"));
					content.put("GYSHR", ((Map)obj.get(0)).get("GYSHR"));
					content.put("MC", ((Map)obj.get(0)).get("MC"));
					content.put("MCTOAG", ((Map)obj.get(0)).get("MCTOAG"));
					content.put("HE", ((Map)obj.get(0)).get("HE"));
					content.put("PUCT_CODE", ((Map)obj.get(0)).get("PUCT_CODE"));
					content.put("BRAND", ((Map)obj.get(0)).get("BRAND"));//供应商名
					
					//add by Michael 2012-4-17
					if(((Map)obj.get(0)).get("PLEDGE_ENTER_MCTOAG") == null){
						((Map)obj.get(0)).put("PLEDGE_ENTER_MCTOAG",0) ;
					}
					content.put("PLEDGE_ENTER_MCTOAG", ((Map)obj.get(0)).get("PLEDGE_ENTER_MCTOAG"));//我司入供应商
					
					if(((Map)obj.get(0)).get("PLEDGE_ENTER_AG") == null){
						((Map)obj.get(0)).put("PLEDGE_ENTER_AG",0) ;
					}
					content.put("PLEDGE_ENTER_AG", ((Map)obj.get(0)).get("PLEDGE_ENTER_AG"));
					double sumTotalPrice = 0.0d ;
					double sumUnitPrice = 0.0d ;
					double sumShuiPrice = 0.0d ;
					for(int i = 0;i<eqmts.size();i++){
						Map eqmt = (Map) eqmts.get(i) ;
						if(eqmt.get("SHUI_PRICE")!=null){
							sumTotalPrice += Double.parseDouble(eqmt.get("SHUI_PRICE").toString());
						}
						if(eqmt.get("UNIT_PRICE")!=null){
							sumUnitPrice += Double.parseDouble(eqmt.get("UNIT_PRICE").toString());
						}
					}
//					sumUnitPrice=(Double) ((Map)obj.get(0)).get("LEASE_TOPRIC") ;;
					sumUnitPrice= Math.round(sumUnitPrice) ;
					sumShuiPrice = sumTotalPrice - sumUnitPrice ;
					double shengyuweihanshui = sumTotalPrice - sumShuiPrice - Double.parseDouble(content.get("PLEDGE_ENTER_AG").toString())- Double.parseDouble(content.get("PLEDGE_ENTER_MCTOAG").toString());
					content.put("sumTotalPrice", sumTotalPrice) ;
					content.put("sumUnitPrice", sumUnitPrice) ;
					content.put("sumShuiPrice", sumShuiPrice) ;
					content.put("shengyuweihanshui", shengyuweihanshui) ;
					
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light",
							"UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
//					Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
//					Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
//					Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
					Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
//					Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
					Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
					Font fa = new Font(bfChinese, 18, Font.BOLD);
					// 数字格式
					NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
					nfFSNum.setGroupingUsed(true);
					nfFSNum.setMaximumFractionDigits(2);
					// 页面设置
					Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
					Document document = new Document(rectPageSize, 60, 60, 60, 60); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					// 打开文档
					document.open();

					PdfPTable t1 = new PdfPTable(1);
					t1.setWidthPercentage(100f);
					t1.addCell(makeCellSetColspanWithNoBorder("设备价款支付协议", PdfPCell.ALIGN_CENTER, fa, 1));
					document.add(t1);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));

					PdfPTable t2 = new PdfPTable(2);
					t2.setWidthPercentage(100f);
					t2.addCell(makeCellSetColspanWithNoBorder("鉴于：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t2);

					
					PdfPTable t9 = new PdfPTable(1);
				    t9.setWidthPercentage(100f);
					PdfPCell objCell2 = new PdfPCell();
				    Phrase phrase2 = new Phrase();
				    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setBorder(0);
			    	Chunk chunk2 = new Chunk("1、",FontDefault);
			    	phrase2.add(chunk2);
			    	
					if(content.get("CUST_NAME")!=null){
						chunk2=new Chunk(("       "+(content.get("CUST_NAME")==null ? "            " : content.get("CUST_NAME").toString())+"       "),FontUnder);
					}else{
			    		chunk2 = new Chunk("                             ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("（下称“甲方”）与 ",FontDefault);
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("    "+Constants.COMPANY_NAME+"    ",FontUnder);
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk(" （下称“乙方”）签署了编号为",FontDefault);
			    	phrase2.add(chunk2);
			    	if(content.get("LEASE_CODE")!=null){
			    		chunk2 = new Chunk(("   "+(content.get("LEASE_CODE")==null? " ":content.get("LEASE_CODE").toString())+"  "), FontUnder);
			    	}else{
			    		chunk2 = new Chunk("                                 ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("的融资租赁合同（下称“租赁合同”），由乙方向甲方提供融资租赁服务，租赁合同项下的标的物和供应商系由甲方依乙方委托甲方向供应商",FontDefault);
			    	phrase2.add(chunk2);
			    	if(content.get("BRAND")!=null){
			    		chunk2 = new Chunk(("     "+(content.get("BRAND")==null? "   ":content.get("BRAND").toString())+"    "), FontUnder);
			    	}else{
			    		chunk2 = new Chunk("                                     ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("（以下简称“丙方”）购买租赁合同项下标的物，并自由意思选定，非依赖乙方的技能确定或受其干预而选择。",FontDefault);
			    	phrase2.add(chunk2);		    	

			    	objCell2.addElement(phrase2);
			    	t9.addCell(objCell2);
					document.add(t9);
				    
					
					//第2段
					PdfPTable t8 = new PdfPTable(1);
				    t8.setWidthPercentage(100f);
					PdfPCell objCell3 = new PdfPCell();
				    Phrase phrase3 = new Phrase();
				    objCell3.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell3.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell3.setBorder(0);
			    	Chunk chunk3 = new Chunk("2、为使甲方能够充分行使其对设备的选择权授权甲方与丙方就标的物签署销售合同    （ 合同号为 ",FontDefault);
			    	phrase3.add(chunk3);
			    	chunk3=new Chunk("    "+(content.get("PUCT_CODE")==null ? "                     " : content.get("PUCT_CODE"))+"  ",FontUnder);
					phrase3.add(chunk3);
					chunk3 = new Chunk("   ）。",FontDefault);
			    	phrase3.add(chunk3);
					objCell3.addElement(phrase3);
			    	t8.addCell(objCell3);
					document.add(t8);
					
					//第2段
					PdfPTable t7 = new PdfPTable(2);
					t7.setWidthPercentage(100f);
					t7.addCell(makeCellSetColspanWithNoBorder("经协商一致，三方就前述销售合同达成的共识如下：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t7);
					
					//第3段
					PdfPTable t6 = new PdfPTable(2);
					t6.setWidthPercentage(100f);
					t6.addCell(makeCellSetColspanWithNoBorder("一.增值税发票的开具事宜：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t6);
					
					//第4段
					PdfPTable t5 = new PdfPTable(1);
					t5.setWidthPercentage(100f);
					PdfPCell objCell6 = new PdfPCell();
				    Phrase phrase6 = new Phrase();
				    objCell6.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell6.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell6.setBorder(0);
				    Chunk chunk6 = new Chunk("      因甲丙双方签订了销售合同，故增值税发票开具的抬头为甲方，即",FontDefault);
				    phrase6.add(chunk6);
				    if(content.get("CUST_NAME")!=null){
				    	chunk6=new Chunk(("     "+(content.get("CUST_NAME").toString()==null ? "   " : content.get("CUST_NAME").toString())+"        "),FontUnder);
					}else{
						chunk6 = new Chunk(".                           .", FontUnder);
			    	}
				    phrase6.add(chunk6);
					objCell6.addElement(phrase6);
					t5.addCell(objCell6);
					document.add(t5);
					
					//第5段
					PdfPTable t4 = new PdfPTable(2);
					t4.setWidthPercentage(100f);
					t4.addCell(makeCellSetColspanWithNoBorder("二.付款履行事宜：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t4);
					
					
					//第6段
					PdfPTable t12 = new PdfPTable(1);
					t12.setWidthPercentage(100f);
					t12.addCell(makeCellSetColspanWithNoBorder("      针对销售合同项下标的物总价款的履行，约定如下：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t12);
					
					
					//第7段
					PdfPTable t11 = new PdfPTable(1);
					t11.setWidthPercentage(93f);
					PdfPCell objCell11 = new PdfPCell();
				    Phrase phrase11 = new Phrase();
				    objCell11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setBorder(0);
				    Chunk chunk11 = new Chunk("1）增值税税款人民币",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("sumShuiPrice")!=null){
				        String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("sumShuiPrice").toString()));
				        chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);

				    }else{
						chunk11 = new Chunk("                        ", FontUnder);
			    	} 
//				    if(content.get("GYSHR")!=null){
//				    	String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("GYSHR").toString()));
//				    	chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
//				    }else{
//				    	chunk11 = new Chunk("                        ", FontUnder);
//				    } 
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，和部分未稅设备价款（即上述融资租赁合同附表中的保证金）",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("GYSH")!=null){
				        //String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("GYSH").toString()));
				        String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("PLEDGE_ENTER_AG").toString())+ Double.parseDouble(content.get("PLEDGE_ENTER_MCTOAG").toString()));
				        chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
					}else{
						chunk11 = new Chunk("                         ", FontUnder);
			    	}
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，合计人民币",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("sumShuiPrice")!=null && content.get("GYSH")!=null){
				        //String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("sumShuiPrice").toString()) + Double.parseDouble(content.get("GYSH").toString()));
				    	String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("sumShuiPrice").toString()) + Double.parseDouble(content.get("PLEDGE_ENTER_AG").toString())+ Double.parseDouble(content.get("PLEDGE_ENTER_MCTOAG").toString()));
				    	chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
					}else{
						 chunk11=new Chunk("                    ",FontUnder);
					}
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，由甲方支付予丙方；",FontDefault);
				    phrase11.add(chunk11);
				    objCell11.addElement(phrase11);
					t11.addCell(objCell11);
					document.add(t11);
					
					
					
					//第7段
					PdfPTable t13 = new PdfPTable(1);
					t13.setWidthPercentage(93f);
					PdfPCell objCell13 = new PdfPCell();
				    Phrase phrase13 = new Phrase();
				    objCell13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setBorder(0);
				    Chunk chunk13 = new Chunk("2）剩余未含税设备价款，计人民币",FontDefault);
				    phrase13.add(chunk13);
				    if(content.get("shengyuweihanshui")!=null){
				        String MCTOAG=nfFSNum.format(Double.parseDouble(content.get("shengyuweihanshui").toString()));
				        chunk13=new Chunk("  "+(MCTOAG==null ? "   " : MCTOAG)+"  ",FontUnder);
					}else{
						 chunk13=new Chunk("                    ",FontUnder);
					}
				    phrase13.add(chunk13);
				    chunk13 = new Chunk("元，由乙方支付予丙方。",FontDefault);
				    phrase13.add(chunk13);
				    objCell13.addElement(phrase13);
					t13.addCell(objCell13);
					document.add(t13);
					
					
					//第6段
					PdfPTable t14 = new PdfPTable(1);
					t14.setWidthPercentage(100f);
					t14.addCell(makeCellSetColspanWithNoBorder("三. 本项租赁物若属可设置密码者,甲、丙双方均同意于本项租赁物上设置密码。", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t14.addCell(makeCellSetColspanWithNoBorder("本协议经甲乙丙三方盖章后即为生效，本协议一式三份,三方各执一份。", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t14);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t15 = new PdfPTable(1);
					t15.setWidthPercentage(93f);
					t15.addCell(makeCellSetColspanWithNoBorder("甲方:  " +(content.get("CUST_NAME")==null ? "            " : content.get("CUST_NAME").toString()), PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t15.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t15);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t16 = new PdfPTable(1);
					t16.setWidthPercentage(93f);
					t16.addCell(makeCellSetColspanWithNoBorder("乙方:  "+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t16.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t16);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t17 = new PdfPTable(1);
					t17.setWidthPercentage(93f);
					t17.addCell(makeCellSetColspanWithNoBorder("丙方:  "+(content.get("BRAND")==null? "   ":content.get("BRAND").toString()), PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t17.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t17);
				    
				
					
					
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition", "attachment; filename=expEqmtPriceAgreeWTpdf.pdf");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
					
					//add by ShenQi 插入系统日志
					BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
					   		 "导出 三方支付协议(出租人/承租人/供应商)",
				   		 	 "合同浏览导出 三方支付协议(出租人/承租人/供应商)",
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

			public static void preCarPdf(Context context){
				ByteArrayOutputStream baos = null;
				try {
					 Map content = new HashMap() ;
					 List obj=null;
					 List eqmts = null ;
					//取出数据
					 
					int  is= (Integer) DataAccessor.query("rentContract.isCONTRACT", context.contextMap, DataAccessor.RS_TYPE.OBJECT) ;
					if(is==0){
						eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
						obj = (List<Map>) DataAccessor.query("rentContract.expEqmtPriceAgreenopdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
					}else{
						eqmts=(List<Map>)DataAccessor.query("rentContract.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
						obj = (List<Map>) DataAccessor.query("rentContract.expEqmtPriceAgreepdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
						
					}
					content.put("LEASE_CODE", ((Map)obj.get(0)).get("LEASE_CODE"));
					content.put("CUST_NAME", ((Map)obj.get(0)).get("CUST_NAME"));
					//qian
					content.put("GYSH", ((Map)obj.get(0)).get("GYSH"));
					content.put("GYSHR", ((Map)obj.get(0)).get("GYSHR"));
					content.put("MC", ((Map)obj.get(0)).get("MC"));
					content.put("MCTOAG", ((Map)obj.get(0)).get("MCTOAG"));
					content.put("HE", ((Map)obj.get(0)).get("HE"));
					content.put("PUCT_CODE", ((Map)obj.get(0)).get("PUCT_CODE"));
					content.put("BRAND", ((Map)obj.get(0)).get("BRAND"));//供应商名
					if(((Map)obj.get(0)).get("PLEDGE_ENTER_AG") == null){
						((Map)obj.get(0)).put("PLEDGE_ENTER_AG",0) ;
					}
					content.put("PLEDGE_ENTER_AG", ((Map)obj.get(0)).get("PLEDGE_ENTER_AG"));
					double sumTotalPrice = 0.0d ;
					double sumUnitPrice = 0.0d ;
					double sumShuiPrice = 0.0d ;
					for(int i = 0;i<eqmts.size();i++){
						Map eqmt = (Map) eqmts.get(i) ;
						if(eqmt.get("SHUI_PRICE")!=null){
							sumTotalPrice += Double.parseDouble(eqmt.get("SHUI_PRICE").toString());
						}
						if(eqmt.get("UNIT_PRICE")!=null){
							sumUnitPrice += Double.parseDouble(eqmt.get("UNIT_PRICE").toString());
						}
					}
					sumUnitPrice= Math.round(sumUnitPrice) ;
					sumShuiPrice = sumTotalPrice - sumUnitPrice ;
					double shengyuweihanshui = sumTotalPrice - sumShuiPrice - Double.parseDouble(content.get("PLEDGE_ENTER_AG").toString());
					content.put("sumTotalPrice", sumTotalPrice) ;
					content.put("sumUnitPrice", sumUnitPrice) ;
					content.put("sumShuiPrice", sumShuiPrice) ;
					content.put("shengyuweihanshui", shengyuweihanshui) ;
					
					// 字体设置
					BaseFont bfChinese = BaseFont.createFont("STSong-Light",
							"UniGB-UCS2-H", BaseFont.EMBEDDED);
					Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
//					
					Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
					Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
					Font fa = new Font(bfChinese, 18, Font.BOLD);
					// 数字格式
					NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
					nfFSNum.setGroupingUsed(true);
					nfFSNum.setMaximumFractionDigits(2);
					// 页面设置
					Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
					Document document = new Document(rectPageSize, 60, 60, 60, 60); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					// 打开文档
					document.open();

					PdfPTable t1 = new PdfPTable(1);
					t1.setWidthPercentage(100f);
					t1.addCell(makeCellSetColspanWithNoBorder("设备价款支付协议", PdfPCell.ALIGN_CENTER, fa, 1));
					document.add(t1);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));

					PdfPTable t2 = new PdfPTable(2);
					t2.setWidthPercentage(100f);
					t2.addCell(makeCellSetColspanWithNoBorder("鉴于：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t2);
					PdfPTable t9 = new PdfPTable(1);
				    t9.setWidthPercentage(100f);
					PdfPCell objCell2 = new PdfPCell();
				    Phrase phrase2 = new Phrase();
				    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setBorder(0);
			    	Chunk chunk2 = new Chunk("1、",FontDefault);
			    	phrase2.add(chunk2);
			    	
					if(content.get("CUST_NAME")!=null){
						chunk2=new Chunk(("       "+(content.get("CUST_NAME")==null ? "            " : content.get("CUST_NAME").toString())+"       "),FontUnder);
					}else{
			    		chunk2 = new Chunk("                             ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("（下称“乙方”）与 ",FontDefault);
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("    "+Constants.COMPANY_NAME+"    ",FontUnder);
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk(" （下称“甲方”）签署了编号为",FontDefault);
			    	phrase2.add(chunk2);
			    	if(content.get("LEASE_CODE")!=null){
			    		chunk2 = new Chunk(("   "+(content.get("LEASE_CODE")==null? " ":content.get("LEASE_CODE").toString())+"  "), FontUnder);
			    	}else{
			    		chunk2 = new Chunk("                                 ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("的融资租赁合同（下称“租赁合同”），由甲方向乙方提供融资租赁服务，租赁合同项下的标的物和供应商系由乙方自主选定,且甲方委托乙方向供应商",FontDefault);
			    	phrase2.add(chunk2);
			    	if(content.get("BRAND")!=null){
			    		chunk2 = new Chunk(("     "+(content.get("BRAND")==null? "   ":content.get("BRAND").toString())+"    "), FontUnder);
			    	}else{
			    		chunk2 = new Chunk("                                     ", FontUnder);
			    	}
			    	phrase2.add(chunk2);
			    	chunk2 = new Chunk("（以下简称“丙方”）购买租赁合同项下标的物，其充分体现乙方的自由意思，非依赖甲方的技能确定或受其干预而选择。",FontDefault);
			    	phrase2.add(chunk2);		    	

			    	objCell2.addElement(phrase2);
			    	t9.addCell(objCell2);
					document.add(t9);
				    
					
					//第2段
					PdfPTable t8 = new PdfPTable(1);
				    t8.setWidthPercentage(100f);
					PdfPCell objCell3 = new PdfPCell();
				    Phrase phrase3 = new Phrase();
				    objCell3.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell3.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell3.setBorder(0);
			    	Chunk chunk3 = new Chunk("2、为使乙方能够充分行使其对设备的选择权，甲方授权乙方与丙方就标的物签署销售合同（ 合同号为 ",FontDefault);
			    	phrase3.add(chunk3);
			    	chunk3=new Chunk("    "+(content.get("PUCT_CODE")==null ? "                     " : content.get("PUCT_CODE"))+"  ",FontUnder);
					phrase3.add(chunk3);
					chunk3 = new Chunk("   ）。",FontDefault);
			    	phrase3.add(chunk3);
					objCell3.addElement(phrase3);
			    	t8.addCell(objCell3);
					document.add(t8);
					
					//第2段
					PdfPTable t7 = new PdfPTable(2);
					t7.setWidthPercentage(100f);
					t7.addCell(makeCellSetColspanWithNoBorder("经协商一致，三方就前述销售合同达成的共识如下：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t7);
					
					//第3段
					PdfPTable t6 = new PdfPTable(2);
					t6.setWidthPercentage(100f);
					t6.addCell(makeCellSetColspanWithNoBorder("一.增值税发票的开具事宜：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t6);
					
					//第4段
					PdfPTable t5 = new PdfPTable(1);
					t5.setWidthPercentage(100f);
					PdfPCell objCell6 = new PdfPCell();
				    Phrase phrase6 = new Phrase();
				    objCell6.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell6.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell6.setBorder(0);
				    Chunk chunk6 = new Chunk("      因乙、丙双方签订了销售合同，故增值税发票开具的抬头为乙方，即",FontDefault);
				    phrase6.add(chunk6);
				    if(content.get("CUST_NAME")!=null){
				    	chunk6=new Chunk(("     "+(content.get("CUST_NAME").toString()==null ? "   " : content.get("CUST_NAME").toString())+"        "),FontUnder);
					}else{
						chunk6 = new Chunk(".                           .", FontUnder);
			    	}
				    phrase6.add(chunk6);
					objCell6.addElement(phrase6);
					t5.addCell(objCell6);
					document.add(t5);
					
					//第5段
					PdfPTable t4 = new PdfPTable(2);
					t4.setWidthPercentage(100f);
					t4.addCell(makeCellSetColspanWithNoBorder("二.付款履行事宜：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t4);
					
					//第6段
					PdfPTable t12 = new PdfPTable(1);
					t12.setWidthPercentage(100f);
					t12.addCell(makeCellSetColspanWithNoBorder("      针对销售合同项下标的物总价款的履行，约定如下：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t12);
					//第7段
					PdfPTable t11 = new PdfPTable(1);
					t11.setWidthPercentage(93f);
					PdfPCell objCell11 = new PdfPCell();
				    Phrase phrase11 = new Phrase();
				    objCell11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setBorder(0);
				    Chunk chunk11 = new Chunk("1、增值税税款人民币",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("sumShuiPrice")!=null){
				        String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("sumShuiPrice").toString()));
				        chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
					}else{
						chunk11 = new Chunk("                        ", FontUnder);
			    	} 
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，和部分未稅设备价款（即上述融资租赁合同附表中的保证金）",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("GYSH")!=null){
				        String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("GYSH").toString()));
				        chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
					}else{
						chunk11 = new Chunk("                         ", FontUnder);
			    	}
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，合计人民币",FontDefault);
				    phrase11.add(chunk11);
				    if(content.get("sumShuiPrice")!=null && content.get("GYSH")!=null){
				        String GYSHRs=nfFSNum.format(Double.parseDouble(content.get("sumShuiPrice").toString()) + Double.parseDouble(content.get("GYSH").toString()));
				        chunk11=new Chunk("  "+(GYSHRs==null ? "   " : GYSHRs)+"  ",FontUnder);
					}else{
						 chunk11=new Chunk("                    ",FontUnder);
					}
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("元，由乙方支付予丙方；",FontDefault);
				    phrase11.add(chunk11);
				    objCell11.addElement(phrase11);
					t11.addCell(objCell11);
					document.add(t11);
					//第7段
					PdfPTable t13 = new PdfPTable(1);
					t13.setWidthPercentage(93f);
					PdfPCell objCell13 = new PdfPCell();
				    Phrase phrase13 = new Phrase();
				    objCell13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setBorder(0);
				    Chunk chunk13 = new Chunk("2、剩余未含税设备价款，计人民币",FontDefault);
				    phrase13.add(chunk13);
				    if(content.get("shengyuweihanshui")!=null){
				        String MCTOAG=nfFSNum.format(Double.parseDouble(content.get("shengyuweihanshui").toString()));
				        chunk13=new Chunk("  "+(MCTOAG==null ? "   " : MCTOAG)+"  ",FontUnder);
					}else{
						 chunk13=new Chunk("                    ",FontUnder);
					}
				    phrase13.add(chunk13);
				    chunk13 = new Chunk("元，由甲方支付予丙方。",FontDefault);
				    phrase13.add(chunk13);
				    objCell13.addElement(phrase13);
					t13.addCell(objCell13);
					document.add(t13);
					PdfPTable t14 = new PdfPTable(1);
					t14.setWidthPercentage(100f);
					t14.addCell(makeCellSetColspanWithNoBorder("本协议经甲乙丙三方盖章后即为生效，本协议一式三份,三方各执一份。", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t14);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t15 = new PdfPTable(1);
					t15.setWidthPercentage(93f);
					t15.addCell(makeCellSetColspanWithNoBorder("甲方:  " +Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t15.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t15);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t16 = new PdfPTable(1);
					t16.setWidthPercentage(93f);
					t16.addCell(makeCellSetColspanWithNoBorder("乙方:  "+(content.get("CUST_NAME")==null ? "            " : content.get("CUST_NAME").toString()), PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t16.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t16);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t17 = new PdfPTable(1);
					t17.setWidthPercentage(93f);
					t17.addCell(makeCellSetColspanWithNoBorder("丙方:  "+(content.get("BRAND")==null? "   ":content.get("BRAND").toString()), PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t17.addCell(makeCellSetColspanWithNoBorder("日期：     年     月     日", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t17);					
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition", "attachment; filename=expEqmtPriceAgreeWTpdf.pdf");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
					
					//add by ShenQi 插入系统日志
					BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
					   		 "导出 三方支付协议(出租人/承租人/供应商)",
				   		 	 "合同浏览导出 三方支付协议(出租人/承租人/供应商)",
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

			public static void exptConsultationAgree(Context context){
				ByteArrayOutputStream baos = null;
				try {
				 List objList=null;
				 objList = (List) DataAccessor.query("rentContract.expEqmtPriceAgreenopdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				 Map obj=(Map) objList.get(0);
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
					Document document = new Document(rectPageSize, 60, 60, 60, 60); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					// 打开文档
					document.open();
					
					//直租 添加公司别判断
					String creditId = (String) context.contextMap.get("PRCD_ID");
					String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
					int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
					String companyName = Constants.COMPANY_NAME;
					String bankName = LeaseUtil.getCompanyBankNameByCompanyCode(1);
					String bankAccount = LeaseUtil.getCompanyBankAccountByCompanyCode(1);
					if("7".equals(contractType)){
						companyName =  LeaseUtil.getCompanyNameByCompanyCode(companyCode);
						bankName = LeaseUtil.getCompanyBankNameByCompanyCode(companyCode);
						bankAccount = LeaseUtil.getCompanyBankAccountByCompanyCode(companyCode);
					}
					
					
					PdfPTable t1 = new PdfPTable(1);
					t1.setWidthPercentage(100f);
					t1.addCell(makeCellSetColspanWithNoBorder("融资租赁咨询服务协议书", PdfPCell.ALIGN_CENTER, fa, 1));
					document.add(t1);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					PdfPTable t9 = new PdfPTable(1);
				    t9.setWidthPercentage(100f);
					PdfPCell objCell2 = new PdfPCell();
				    Phrase phrase2 = new Phrase();
				    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setBorder(0);
			    	Chunk chunk2 = new Chunk("甲方：",FontDefault);
			    	phrase2.add(chunk2);
			    	
			    	chunk2 = new Chunk("    "+companyName+"    ",FontUnder);
			    	phrase2.add(chunk2);
			    	objCell2.addElement(phrase2);
			    	t9.addCell(objCell2);
					document.add(t9);
				    
					PdfPTable t2 = new PdfPTable(1);
					t2.setWidthPercentage(100f);
					PdfPCell objCell12 = new PdfPCell();
				    Phrase phrase12 = new Phrase();
				    objCell12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell12.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell12.setBorder(0);
			    	Chunk chunk22 = new Chunk("乙方：",FontDefault);
			    	phrase12.add(chunk22);
			    	
//					if(obj.get("CUST_NAME")!=null){
//						chunk22=new Chunk(("       "+(obj.get("CUST_NAME")==null ? "            " : obj.get("CUST_NAME").toString())+"       "),FontUnder);
//					}else{
//			    		chunk22 = new Chunk("                             ", FontUnder);
//			    	}
			    	chunk22 = new Chunk("                             ", FontUnder);
					phrase12.add(chunk22);
			    	objCell12.addElement(phrase12);
			    	t2.addCell(objCell12);
					document.add(t2);
					
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					//第2段
					PdfPTable t7 = new PdfPTable(2);
					t7.setWidthPercentage(100f);
					t7.addCell(makeCellSetColspanWithNoBorder("      爰甲、乙双方在合作互惠公平的原则下，双方同意由甲方对乙方提供融资租", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t7);
					
					//第3段
					PdfPTable t6 = new PdfPTable(2);
					t6.setWidthPercentage(100f);
					t6.addCell(makeCellSetColspanWithNoBorder("赁业务服务的相关咨询，乙方则依约给付甲方服务咨询费，条件如后：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t6);
					
					//第5段
					PdfPTable t4 = new PdfPTable(2);
					t4.setWidthPercentage(100f);
					t4.addCell(makeCellSetColspanWithNoBorder("一、	咨询内容：甲方同意就乙方提出之融资租赁业务服务咨询的需求，包含但", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t4);
					
					//第6段
					PdfPTable t12 = new PdfPTable(1);
					t12.setWidthPercentage(100f);
					t12.addCell(makeCellSetColspanWithNoBorder("    不限于乙方之业务范围，甲方均应依诚实信用原则，善尽告知办理融资租", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t12.addCell(makeCellSetColspanWithNoBorder("    赁的流程及所需具备的相关条件。", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t12);
					
					document.add(new Paragraph("\n"));
					
					PdfPTable t8 = new PdfPTable(2);
					t8.setWidthPercentage(100f);
					t8.addCell(makeCellSetColspanWithNoBorder("二、	费用金额及付款期限：甲乙双方同意,依本次协议书所服务的咨询费用为人", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t8);
					
					PdfPTable t10 = new PdfPTable(1);
					t10.setWidthPercentage(100f);
					t10.addCell(makeCellSetColspanWithNoBorder("    民币___________元（含税）。乙方应于双方签订本协议时或之前，将前开", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t10.addCell(makeCellSetColspanWithNoBorder("    服务咨询费款项汇入甲方指定账户：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t10);
					
					PdfPTable t11 = new PdfPTable(1);
					t11.setWidthPercentage(100f);
					t11.addCell(makeCellSetColspanWithNoBorder("    户名--"+companyName, PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t11.addCell(makeCellSetColspanWithNoBorder("    银行—"+bankName, PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t11);
					
					PdfPTable t13 = new PdfPTable(2);
					t13.setWidthPercentage(100f);
					t13.addCell(makeCellSetColspanWithNoBorder("    账号—"+bankAccount, PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t13);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					//第6段
					PdfPTable t14 = new PdfPTable(1);
					t14.setWidthPercentage(100f);
					t14.addCell(makeCellSetColspanWithNoBorder("三、	本协议书一式二份，双方各执一份，经双方同意盖章或签署后即生效。", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t14);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t15 = new PdfPTable(1);
					t15.setWidthPercentage(93f);
					t15.addCell(makeCellSetColspanWithNoBorder("立书人：甲方: "+companyName, PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t15);
					PdfPTable t18 = new PdfPTable(1);
					t18.setWidthPercentage(93f);
					t18.addCell(makeCellSetColspanWithNoBorder("法人代表/授权代表人： ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t18);
					PdfPTable t19 = new PdfPTable(1);
					t19.setWidthPercentage(93f);
					t19.addCell(makeCellSetColspanWithNoBorder("     年            月           日 ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t19);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t16 = new PdfPTable(1);
					t16.setWidthPercentage(93f);
					t16.addCell(makeCellSetColspanWithNoBorder("乙方： " , PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t16);
					PdfPTable t17 = new PdfPTable(1);
					t17.setWidthPercentage(93f);
					t17.addCell(makeCellSetColspanWithNoBorder("法人代表/授权代表人：  ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t17);
					
					PdfPTable t20 = new PdfPTable(1);
					t20.setWidthPercentage(93f);
					t20.addCell(makeCellSetColspanWithNoBorder("     年            月           日 ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t20);
					
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition", "attachment; filename=exptConsultationAgree.pdf");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
					
					BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
					   		 "导出 咨询服务协议书",
				   		 	 "合同浏览导出 咨询服务协议书",
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

			public static void exptCarConsultationAgree(Context context){
				ByteArrayOutputStream baos = null;
				Double totalManageFee=0.0;
				try {
				 List objList=null;
				 List<Map> prjtFeeSourceList=null;
				 prjtFeeSourceList = (List<Map>) DataAccessor.query("rentContract.queryPrjtFeeSourceByPrcdID", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				 
				for (Map map : prjtFeeSourceList) {
					
					if("MANAGE_FEE".equals(map.get("CREATE_FILED_NAME"))||"MANAGE_FEE2".equals(map.get("CREATE_FILED_NAME"))||"home_fee".equals(map.get("CREATE_FILED_NAME"))){
						totalManageFee+=DataUtil.doubleUtil(map.get("FEE"));
					}
				}
				 
				 objList = (List) DataAccessor.query("rentContract.expEqmtPriceAgreenopdf", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
				 Map obj=(Map) objList.get(0);
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
					Document document = new Document(rectPageSize, 60, 60, 60, 60); // 其余4个参数，设置了页面的4个边距
					baos = new ByteArrayOutputStream();
					PdfWriter.getInstance(document, baos);
					// 打开文档
					document.open();

					PdfPTable t1 = new PdfPTable(1);
					t1.setWidthPercentage(100f);
					t1.addCell(makeCellSetColspanWithNoBorder("融资租赁咨询服务协议书", PdfPCell.ALIGN_CENTER, fa, 1));
					document.add(t1);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					PdfPTable t9 = new PdfPTable(1);
				    t9.setWidthPercentage(100f);
					PdfPCell objCell2 = new PdfPCell();
				    Phrase phrase2 = new Phrase();
				    objCell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell2.setBorder(0);
			    	Chunk chunk2 = new Chunk("甲方：",FontDefault);
			    	phrase2.add(chunk2);
			    	
			    	chunk2 = new Chunk("    "+Constants.COMPANY_NAME+"    ",FontUnder);
			    	phrase2.add(chunk2);
			    	objCell2.addElement(phrase2);
			    	t9.addCell(objCell2);
					document.add(t9);
				    
					PdfPTable t2 = new PdfPTable(1);
					t2.setWidthPercentage(100f);
					PdfPCell objCell12 = new PdfPCell();
				    Phrase phrase12 = new Phrase();
				    objCell12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell12.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell12.setBorder(0);
			    	Chunk chunk22 = new Chunk("乙方：",FontDefault);
			    	phrase12.add(chunk22);
			    	
					if(obj.get("CUST_NAME")!=null){
						chunk22=new Chunk(("    "+(obj.get("CUST_NAME")==null ? "            " : obj.get("CUST_NAME").toString())+"       "),FontUnder);
					}else{
			    		chunk22 = new Chunk("                             ", FontUnder);
			    	}
			    	//chunk22 = new Chunk("    "+Constants.COMPANY_NAME+"    ",FontUnder);
					phrase12.add(chunk22);
			    	objCell12.addElement(phrase12);
			    	t2.addCell(objCell12);
					document.add(t2);
					
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					//第2段
					PdfPTable t7 = new PdfPTable(2);
					t7.setWidthPercentage(100f);
					t7.addCell(makeCellSetColspanWithNoBorder("      爰甲、乙双方在合作互惠公平的原则下，双方同意由甲方对乙方提供融资租", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t7);
					
					//第3段
					PdfPTable t6 = new PdfPTable(2);
					t6.setWidthPercentage(100f);
					t6.addCell(makeCellSetColspanWithNoBorder("赁业务服务的相关咨询，乙方则依约给付甲方服务咨询费，条件如后：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t6);
					
					//第5段
					PdfPTable t4 = new PdfPTable(2);
					t4.setWidthPercentage(100f);
					t4.addCell(makeCellSetColspanWithNoBorder("一、	咨询内容：甲方同意就乙方提出之融资租赁业务服务咨询的需求，包含但", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t4);
					
					//第6段
					PdfPTable t12 = new PdfPTable(1);
					t12.setWidthPercentage(100f);
					t12.addCell(makeCellSetColspanWithNoBorder("    不限于乙方之业务范围，甲方均应依诚实信用原则，善尽告知办理融资租", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t12.addCell(makeCellSetColspanWithNoBorder("    赁的流程及所需具备的相关条件。", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t12);
					
					document.add(new Paragraph("\n"));
					
					PdfPTable t8 = new PdfPTable(2);
					t8.setWidthPercentage(100f);
					t8.addCell(makeCellSetColspanWithNoBorder("二、	费用金额及付款期限：甲乙双方同意,依本次协议书所服务的咨询费用为人", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t8);
					
					PdfPTable t3 = new PdfPTable(1);
					t3.setWidthPercentage(100f);
					PdfPCell objCell13 = new PdfPCell();
				    Phrase phrase13 = new Phrase();
				    objCell13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setBorder(0);
				    Chunk chunk33 = new Chunk("    民币",FontDefault);
				    phrase13.add(chunk33);
			    	chunk33=new Chunk("  "+nfFSNum.format(totalManageFee)+"  ",FontUnder);
			    	phrase13.add(chunk33);
			    	chunk33=new Chunk("  元（含税）。乙方应于双方签订本协议时或之前，将前开",FontDefault);
			    	phrase13.add(chunk33);
			    	
			    	objCell13.addElement(phrase13);
			    	t3.addCell(objCell13);
					document.add(t3);
		
					
					PdfPTable t10 = new PdfPTable(1);
					t10.setWidthPercentage(100f);
					t10.addCell(makeCellSetColspanWithNoBorder("    服务咨询费款项汇入甲方指定账户：", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t10);
					
					PdfPTable t11 = new PdfPTable(1);
					t11.setWidthPercentage(100f);
					t11.addCell(makeCellSetColspanWithNoBorder("    户名--裕融租赁有限公司", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					t11.addCell(makeCellSetColspanWithNoBorder("    银行—中国银行苏州工业园区娄葑支行", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t11);
					
					PdfPTable t13 = new PdfPTable(2);
					t13.setWidthPercentage(100f);
					t13.addCell(makeCellSetColspanWithNoBorder("    账号—497558194856", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t13);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					//第6段
					PdfPTable t14 = new PdfPTable(1);
					t14.setWidthPercentage(100f);
					t14.addCell(makeCellSetColspanWithNoBorder("三、	本协议书一式二份，双方各执一份，经双方同意盖章或签署后即生效。", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t14);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t15 = new PdfPTable(1);
					t15.setWidthPercentage(93f);
					t15.addCell(makeCellSetColspanWithNoBorder("立书人：甲方: "+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t15);
					PdfPTable t18 = new PdfPTable(1);
					t18.setWidthPercentage(93f);
					t18.addCell(makeCellSetColspanWithNoBorder("法人代表/授权代表人： ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t18);
					PdfPTable t19 = new PdfPTable(1);
					t19.setWidthPercentage(93f);
					t19.addCell(makeCellSetColspanWithNoBorder("     年            月           日 ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t19);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					PdfPTable t16 = new PdfPTable(1);
					t16.setWidthPercentage(93f);
					t16.addCell(makeCellSetColspanWithNoBorder("乙方： "+(obj.get("CUST_NAME")==null?"":obj.get("CUST_NAME")), PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t16);
					PdfPTable t17 = new PdfPTable(1);
					t17.setWidthPercentage(93f);
					t17.addCell(makeCellSetColspanWithNoBorder("法人代表/授权代表人：  ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t17);
					
					PdfPTable t20 = new PdfPTable(1);
					t20.setWidthPercentage(93f);
					t20.addCell(makeCellSetColspanWithNoBorder("     年            月           日 ", PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t20);
					
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition", "attachment; filename=exptConsultationAgree.pdf");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
					
					BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
					   		 "导出 咨询服务协议书",
				   		 	 "合同浏览导出 咨询服务协议书",
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
			
			/** 创建 没有左右上 单元格 */
			public static PdfPCell makeCellWithBorderLeftTopRight(String content, int align, Font FontDefault,int colspan) {
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
		}

