package com.brick.contract.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
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

		public class ExpEqmtPriceAgreeYBpdf extends AService {
			static	Log logger = LogFactory.getLog(ExpBankNoticepdf.class);
		    /**
		     * 导出PDF 三方支付协议(出租人/承租人/供应商) yiban
		     * @param context
		     */
			@SuppressWarnings("unchecked")
			public static void prePdf(Context context){
				ByteArrayOutputStream baos = null;
				try {
					 Map content = new HashMap() ;
					 SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd");
					//取出数据
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
					content.put("custname", ((Map)obj.get(0)).get("CUST_NAME"));
					//qian
					content.put("GYSH", ((Map)obj.get(0)).get("GYSH"));
					content.put("GYSHR", ((Map)obj.get(0)).get("GYSHR"));
					content.put("MC", ((Map)obj.get(0)).get("MC"));
					content.put("MCTOAG", ((Map)obj.get(0)).get("MCTOAG"));
					content.put("HE", ((Map)obj.get(0)).get("HE"));
					content.put("PUCT_CODE", ((Map)obj.get(0)).get("PUCT_CODE"));
					content.put("BRAND", ((Map)obj.get(0)).get("BRAND"));//供应商名
					content.put("SIGN_DATE", ((Map)obj.get(0)).get("SIGN_DATE"));
					//求出总金额 - GYSH金额
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
					sumUnitPrice=Math.round(sumUnitPrice);
					sumTotalPrice = Math.round(sumTotalPrice) ;
					sumShuiPrice = sumTotalPrice - sumUnitPrice ;
					double shengyuweihanshui = sumTotalPrice - Double.parseDouble(content.get("GYSH").toString());
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
					Font smallDefault = new Font(bfChinese, 10, Font.NORMAL);
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
					document.add(new Paragraph("\n"));


					//第1段
					PdfPTable t11 = new PdfPTable(1);
					t11.setWidthPercentage(100f);
					PdfPCell objCell11 = new PdfPCell();
				    Phrase phrase11 = new Phrase();
				    objCell11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell11.setBorder(0);
				    Chunk chunk11 = new Chunk("以下本着友好合作的精神，就",FontDefault);
				    phrase11.add(chunk11);
				    chunk11=new Chunk("____年___月___日  ",FontDefault);
//				    chunk11=new Chunk((" "+(content.get("SIGN_DATE")==null ? "         年     月      日  " : (sdf.format(content.get("SIGN_DATE"))))+" "),FontUnder);
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("签订的买卖合同[合同号",FontDefault);
				    phrase11.add(chunk11);
				    chunk11=new Chunk(("   "+(content.get("LEASE_CODE")==null ? "                  " : content.get("LEASE_CODE"))+"   "),FontUnder);
//				    chunk11=new Chunk(("   "+(content.get("PUCT_CODE")==null ? "                  " : content.get("PUCT_CODE"))+"   "),FontUnder);
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("]和租赁合同[合同号",FontDefault);
				    phrase11.add(chunk11);
				    chunk11=new Chunk(("      "+(content.get("LEASE_CODE")==null ? "                 " : content.get("LEASE_CODE"))+"      "),FontUnder);
				    phrase11.add(chunk11);
				    chunk11 = new Chunk("]的有关事宜制定此协议。",FontDefault);
				    phrase11.add(chunk11);
				    objCell11.addElement(phrase11);
					t11.addCell(objCell11);
					document.add(t11);
					document.add(new Paragraph("\n"));
					//第2段
					PdfPTable t12 = new PdfPTable(2);
					t12.setWidthPercentage(100f);
					t12.addCell(makeCellSetColspanWithNoBorder(("卖方(甲方)  ： "+(content.get("BRAND")==null ? "                      " : content.get("BRAND"))), PdfPCell.ALIGN_LEFT, FontDefault, 2));
					t12.addCell(makeCellSetColspanWithNoBorder("购买方(乙方)： "+Constants.COMPANY_NAME, PdfPCell.ALIGN_LEFT, FontDefault, 2));
					t12.addCell(makeCellSetColspanWithNoBorder(("承租方(丙方)： "+(content.get("custname")==null ? "                    " : content.get("custname"))), PdfPCell.ALIGN_LEFT, FontDefault, 2));
					document.add(t12);
					document.add(new Paragraph("\n"));
					//第3段
					PdfPTable t13 = new PdfPTable(1);
					t13.setWidthPercentage(100f);
					PdfPCell objCell13 = new PdfPCell();
				    Phrase phrase13 = new Phrase();
				    objCell13.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				    objCell13.setBorder(0);
				    Chunk chunk13 = new Chunk("甲、乙、丙三方经过一致协商同意，由丙方先代乙方垫付买卖物品采购款即人民币",FontDefault);
				    phrase13.add(chunk13);
				    chunk13=new Chunk(("    "+(content.get("GYSH")==null ? "    " : nfFSNum.format(Double.parseDouble(content.get("GYSH").toString())))+"  "),FontUnder);
				    phrase13.add(chunk13);
				    chunk13 = new Chunk("元整（即上述融资租赁合同附表中的保证金）给甲方，剩余款项人民币",FontDefault);
				    phrase13.add(chunk13);
				    chunk13=new Chunk(("    "+(content.get("shengyuweihanshui")==null ? "            " : nfFSNum.format(Double.parseDouble(content.get("shengyuweihanshui").toString())))+"    "),FontUnder);
				    phrase13.add(chunk13);
				    chunk13 = new Chunk("元整由乙方直接支付给甲方。至此买卖物品采购款项即全部支付完毕，甲方不得再向乙方和丙方要求支付买卖物品采购款项。丙方代乙方支付的款项由丙方和乙方再行结算，不再与甲方就买卖物品采购款事宜提出异议。甲方应将买卖物品采购款发票按全额100%开具给乙方，以下无正文。",FontDefault);
				    phrase13.add(chunk13);
				    objCell13.addElement(phrase13);
					t13.addCell(objCell13);
					document.add(t13);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					//第6段
					PdfPTable t14 = new PdfPTable(1);
					t14.setWidthPercentage(100f);
					t14.addCell(makeCellSetColspanWithNoBorder("签字栏：", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t14);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					PdfPTable t15 = new PdfPTable(1);
					t15.setWidthPercentage(100f);
					t15.addCell(makeCellSetColspanWithNoBorder("卖方(甲方)：" + (content.get("BRAND")==null ? "                      " : content.get("BRAND")) + "  (盖章)", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t15);
					document.add(new Paragraph("\n"));
					PdfPTable t16 = new PdfPTable(1);
					t16.setWidthPercentage(100f);
					t16.addCell(makeCellSetColspanWithNoBorder("代表人签名：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					t16.addCell(makeCellSetColspanWithNoBorder("日期：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					document.add(t16);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					
					PdfPTable t17 = new PdfPTable(1);
					t17.setWidthPercentage(100f);
					t17.addCell(makeCellSetColspanWithNoBorder("购买方(乙方)："+Constants.COMPANY_NAME+"(盖章)", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t17);
					document.add(new Paragraph("\n"));
					PdfPTable t18 = new PdfPTable(1);
					t18.setWidthPercentage(100f);
					t18.addCell(makeCellSetColspanWithNoBorder("代表人签名：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					t18.addCell(makeCellSetColspanWithNoBorder("日期：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					document.add(t18);
					document.add(new Paragraph("\n"));
					document.add(new Paragraph("\n"));
					
					PdfPTable t19 = new PdfPTable(1);
					t19.setWidthPercentage(100f);
					t19.addCell(makeCellSetColspanWithNoBorder("承租方(丙方)："+(content.get("custname")==null ? "                    " : content.get("custname"))+"  (盖章)", PdfPCell.ALIGN_LEFT, FontDefault, 1));
					document.add(t19);
					document.add(new Paragraph("\n"));
					PdfPTable t20 = new PdfPTable(1);
					t20.setWidthPercentage(100f);
					t20.addCell(makeCellSetColspanWithNoBorder("代表人签名：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					t20.addCell(makeCellSetColspanWithNoBorder("日期：  ", PdfPCell.ALIGN_LEFT, smallDefault, 1));
					document.add(t20);
				    
				    
					
					
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition", "attachment; filename=expEqmtPriceAgreeYBpdf.pdf");
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o);
					o.flush();
					o.close();
				} catch (Exception e) {
					e.printStackTrace();
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
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

			@SuppressWarnings("unused")
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

			@SuppressWarnings("unused")
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

			@SuppressWarnings("unused")
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
			@SuppressWarnings("unused")
			private static PdfPCell makeCell(String content, int align, Font FontDefault) {
				Phrase objPhase = new Phrase(content, FontDefault);
				PdfPCell objCell = new PdfPCell(objPhase);
				objCell.setHorizontalAlignment(align);
				return objCell;
			}
		}

