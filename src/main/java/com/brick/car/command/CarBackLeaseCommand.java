package com.brick.car.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.CollectionConstants;
import com.brick.collection.service.StartPayService;
import com.brick.contract.util.SimpleMoneyFormat;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class CarBackLeaseCommand extends BaseCommand{
	
	Log logger = LogFactory.getLog(CarBackLeaseCommand.class);
	//Add by xuwei 导出乘用车车回租租赁物情况表  
		public void expCarLeaseBackZulwToPdfForValueAdded(Context context) {
				context.contextMap.put("creditId", context.contextMap.get("credit_id"));
				ArrayList booknotes=new ArrayList();
				HashMap booknote=new HashMap();
				Map creditCustomerMap = null;
				List<Map> equipmentsList = null;
				Map schemeMap = null;
				Map paylist = new HashMap();
				try{
					Map schema = new HashMap();
					Map insure_FEE= new HashMap();
					schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
					// 查询应付租金列表
					List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
					List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
					
					schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
					insure_FEE = (Map) DataAccessor.query("exportQuoToPdf.selectCreditSchemeINSUREFEE",context.contextMap, DataAccessor.RS_TYPE.MAP);
					paylist = schemeMap ;
					paylist.put("rePaylineList", rePaylineList);
					paylist.put("oldirrMonthPaylines", irrMonthPaylines);
					paylist.put("irrMonthPaylines", irrMonthPaylines);
					paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
					paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
					StartPayService.packagePaylinesForValueAdded(paylist);
					
					irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
					
					equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditCarEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
					creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
					String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
					if(custTyp.equals("1")){
					    
					    creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
					}
					if(creditCustomerMap==null){
						creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
					}
					booknote.put("rePaylineList", rePaylineList);
					booknote.put("schema", schema);
					booknote.put("paylist", paylist);
					booknote.put("irrMonthPaylines", irrMonthPaylines);
					booknote.put("monthPaylines", this.ExpectMonthPriceValueAdded((List)paylist.get("oldirrMonthPaylines"),Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""),Double.parseDouble(paylist.get("valueAddedTax")+""),Double.parseDouble(paylist.get("lastValueAddedTax")+"")));
					booknote.put("equipmentsList", equipmentsList);
					booknote.put("creditCustomerMap", creditCustomerMap);
					booknote.put("schemeMap", schemeMap);
					booknote.put("insure_FEE", insure_FEE);
					booknotes.add(booknote);
					context.contextMap.put("booknotes", booknotes);
					this.expCarZulwPdfModelByValueAdded(context,"expZulwToPdf");
				}catch(Exception e){
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		
		public static List ExpectMonthPriceValueAdded(List irrMonthPaylines,Double pledgeAVEPrice,Double valueAddedTax,Double lastValueAddedTax) {
			//增加预期租金计算 	
			List monthPaylines = new ArrayList() ; 
			if(pledgeAVEPrice == null){
				pledgeAVEPrice = 0.0d ;
			}
			if(irrMonthPaylines == null){
				irrMonthPaylines = new ArrayList() ;
			}
			if(irrMonthPaylines.size() != 0){
				int endNum = Integer.parseInt(((Map)irrMonthPaylines.get(irrMonthPaylines.size()-1)).get("IRR_MONTH_PRICE_END").toString()) ;
				double eachAVE = Math.round(pledgeAVEPrice / endNum * 100.0d)/100.0d ;
				double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) *100.0d)/100.0d ;
				for(int i=0;i<irrMonthPaylines.size();i++){
					Map temp = (Map) irrMonthPaylines.get(i) ;
					Map map = null ;
					int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
					int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
					double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
					if(i == irrMonthPaylines.size() - 1){
						if(valueAddedTax!=lastValueAddedTax||eachAVE != endAVE){
							if(start != end ){
								map = new HashMap() ;
								map.put("MONTH_PRICE_START",start ) ;
								map.put("MONTH_PRICE_END",end - 1 ) ;
								map.put("MONTH_PRICE", price + eachAVE) ;
								map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
								monthPaylines.add(map) ;
							} 
							map = new HashMap() ;
							map.put("MONTH_PRICE_START",end ) ;
							map.put("MONTH_PRICE_END",end ) ;
							map.put("MONTH_PRICE", price + endAVE) ;
							map.put("MONTH_PRICE_TAX", price + endAVE+lastValueAddedTax) ;
							monthPaylines.add(map) ;
						}else {
							map = new HashMap() ;
							map.put("MONTH_PRICE_START",start ) ;
							map.put("MONTH_PRICE_END",end ) ;
							map.put("MONTH_PRICE", price + eachAVE) ;
							map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
							monthPaylines.add(map) ;
						}
					}else {
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end ) ;
						map.put("MONTH_PRICE", price + eachAVE) ;
						map.put("MONTH_PRICE_TAX", price + eachAVE+valueAddedTax) ;
						monthPaylines.add(map) ;
					}
				}
			}
			//增加预期租金计算    结束
			return monthPaylines ;
		}
		//导出回租租赁物情况表
		@SuppressWarnings("unchecked")
		public void expCarZulwPdfModelByValueAdded(Context context,String methodname){
			ArrayList booknotes=new ArrayList();
			if(context.contextMap.get("booknotes")!=null&&((ArrayList)context.contextMap.get("booknotes")).size()>0){
				booknotes=(ArrayList)context.contextMap.get("booknotes");
			}
			
			ByteArrayOutputStream baos = null;
			try {
				// 字体设置
				BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
				Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
				Font FontDefault = new Font(bfChinese, 12, Font.NORMAL);
				 
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
				nfFSNum.setGroupingUsed(true);
				nfFSNum.setMaximumFractionDigits(2);
				// 页面设置
				Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
				Document document = new Document(rectPageSize, 5, 5, 20, 0); // 其余4个参数，设置了页面的4个边距 左右上下
				baos = new ByteArrayOutputStream();
				PdfWriter.getInstance(document, baos);
			
				//打开文档
				document.open();
				//支付表PDF名字的定义
				String strFileName = "";
				// 表格列宽定义
				float[] widthsStl = {0.10f,0.20f,0.30f,0.20f,0.20f};
				int iCnt = 0;			
				float[] widthsPPCa = { 1f };
				int t=2;
				Map schema=null;
				Map insure_FEE=null;
				for(int bookint=0;bookint<booknotes.size();bookint++){
					Map paylist = null;
					schema = new HashMap();
					insure_FEE=new HashMap();
					schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
					insure_FEE=(HashMap)((HashMap)booknotes.get(bookint)).get("insure_FEE");
					paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
					List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
					List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
					List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
					int lenn1 = irrMonthPaylines.size();
				
					PdfPTable tT1 = new PdfPTable(widthsPPCa);
					 
					tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
					tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tT1);
					
					List<Map> equipmentsList = (List<Map>)((HashMap)booknotes.get(bookint)).get("equipmentsList");
					PdfPTable tT2 = new PdfPTable(2);
		
					
					//页眉
					PdfPTable table = new PdfPTable(1);
					table.addCell(makeCellSetColspan2ForZLWHead("",PdfPCell.ALIGN_LEFT, FontDefault,1));
					Phrase phrase = new Phrase();
					phrase.add(table);
					    
					HeaderFooter hf = new HeaderFooter(phrase,false);
					hf.setBorder(0);
					document.setHeader(hf);	
						
					
					tT2.addCell(makeCellS("1、租 赁 物 ", PdfPCell.ALIGN_LEFT, FontColumn));
					tT2.addCell(makeCellSGAI("2、卖 方 及 制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
					
					int siz = equipmentsList.size();
					if(siz!=0){
						t=t+siz*5;
					}
					for(int i=0;i<equipmentsList.size();i++){
					    int cnt=1;
						if(equipmentsList.get(i).get("BRAND").toString().length()>=16){
							cnt=2;
							t++;
						}
					    int cnt2=1;
						if(equipmentsList.get(i).get("THING_KIND").toString().length()>=16){
							cnt2=2;
							t++;
						}		
						

						tT2.addCell(makeCellWithBorder("名称 : "+( equipmentsList.get(i).get("THING_NAME")==null?"":equipmentsList.get(i).get("THING_NAME") ), PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP("卖方 : "+( equipmentsList.get(i).get("BRAND")==null?"":equipmentsList.get(i).get("BRAND")), PdfPCell.ALIGN_LEFT, FontDefault,cnt));	
						
						tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+
								"     牌号:"+(equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")==null?"":equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")), PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
						
						tT2.addCell(makeCellWithBorder("发动机号:"+(equipmentsList.get(i).get("CAR_ENGINE_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ENGINE_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
						
						tT2.addCell(makeCellWithBorder("车架号："+(equipmentsList.get(i).get("CAR_ID_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ID_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));
						
						tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")+
								"  颜色："+(equipmentsList.get(i).get("CAR_COLOR")==null?"":equipmentsList.get(i).get("CAR_COLOR"))) , PdfPCell.ALIGN_LEFT, FontDefault));
						tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
							
					}
					
					Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
					
					Map schemeMap = (HashMap)((HashMap)booknotes.get(bookint)).get("schemeMap");
					int equAdd_cnt=1; 
					if(creditCustomerMap.get("EQUPMENT_ADDRESS")!=null){
						if(creditCustomerMap.get("EQUPMENT_ADDRESS").toString().length()>=16){
							equAdd_cnt=2;
							t++;
						}				
					}
					
					int compAdd_cnt=1; 
					if(creditCustomerMap.get("CORP_REGISTE_ADDRESS")!=null){
						if(creditCustomerMap.get("CORP_REGISTE_ADDRESS").toString().length()>=16){
							compAdd_cnt=2;
							t++;
						}				
					}			
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("3、租 赁 物 放 置 场 所 ", PdfPCell.ALIGN_LEFT, FontColumn,2));
					tT2.addCell(makeCellSetColspanLRBorder("公 司 名 称 ："+((creditCustomerMap==null||creditCustomerMap.get("CUST_NAME")==null)?"":creditCustomerMap.get("CUST_NAME")) , PdfPCell.ALIGN_LEFT, FontDefault,2));
					tT2.addCell(makeCellSetColspanLRBorder("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2));
					tT2.addCell(makeCellSetColspanLRBorder("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2));
					tT2.addCell(makeCellSetColspanNoBorderTOPAuto("使用区域 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
					t=t+4;
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));

					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
					
					t=t+2;
					for(int x = 0;x<irrMonthPaylines.size();x++){
					    
//					    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元" 
//							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
						
					    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元" 
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					
					t=t+monthPaylines.size();
					int payWay =0;

					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
					
					if(payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
							|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
							|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL){
						tT2.addCell(makeCellSetColspanLRBorder("    首期月租金支付日 ：起租日前", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("    首期月租金支付日 ：起租后31天之内", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					

					t++;
					
					tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
					tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
					tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
					t=t+3;			
					tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));

					tT2.addCell(makeCellSetColspanNoBorderTOP("    (1)RMB【"+   updateMon(schemeMap.get("PLEDGE_AVE_PRICE")+"") +"】元（平均抵充） 。"+"    (2)RMB【"+   updateMon(schemeMap.get("PLEDGE_BACK_PRICE")+"") +"】元（期末无息返还） 。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					t=t+2; 
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
					t=t+1;


					if (payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_CAPITAL
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_EQUAL_RATE
						|| payWay == CollectionConstants.PAY_WAY_BEGIN_UNEQUAL) {
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					else{
						tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
				 	} else {
				 		if(t==39||(t-39)%38==0){
					 		tT2.addCell(makeCellSetColspanNoBorderTOP("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 		}else{
					 		tT2.addCell(makeCellSetColspanLRBorder("    支 付 方 法 ：第1期 第一期支付日____年____月____日，以转账支付。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 		}
				 	}
					t++;
			 		if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款，如遇" , PdfPCell.ALIGN_LEFT, FontDefault,2));
			 		}else{
						tT2.addCell(makeCellSetColspanLRBorder("                                第2期 从第二期开始前每月【    】日前选择以下支付方式到款，如遇" , PdfPCell.ALIGN_LEFT, FontDefault,2));
			 		}
			 		t++;
			 		if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("                                             节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}else{
						tT2.addCell(makeCellSetColspanLRBorder("                                             节假日则提前到前一银行工作日。" , PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}
			 		t++;
			 		if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("                                各期支付的金额详见甲方出具的《租金支付明细表》", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}else{
						tT2.addCell(makeCellSetColspanLRBorder("                                各期支付的金额详见甲方出具的《租金支付明细表》", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}
			 		t++;
			 		if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}else{
						tT2.addCell(makeCellSetColspanLRBorder( "    支 付 方 式 ：（1）银行转账  [√]  （2）商业承兑汇票  [   ]  （3）其他  [   ]", PdfPCell.ALIGN_LEFT, FontDefault,2)); 
			 		}
			 		t++;
			 		//Modify by Michael 2012 5-17 hardcode 留购款为100块
			 		double stayBuyPrice = 100d;
					
					tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
					 
					t=t+2;
					int lenn2 = irrMonthPaylines.size();
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
					t++;
	 				if(t==39||(t-39)%38==0){
	 					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 				}
	 				else{
	 					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数:【"+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"】元/"+paylist.get("PLEDGE_LAST_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 				}
	 				t++;
	 				if(methodname.equals("expZulwToPdf")){
	 					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 				}else{
	 					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 				}
	 				t++;
					for(int x = 0;x<irrMonthPaylines.size();x++){
						if(t==39||(t-39)%38==0){
					    tT2.addCell(makeCellSetColspanNoBorderTOP("     每期实缴租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"  
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
						}else{
						    tT2.addCell(makeCellSetColspanLRBorder("     每期实缴租金：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"  
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				 		}
					}
					t=t+irrMonthPaylines.size();
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("      乙方交付甲方人民币"+(insure_FEE.get("FEE")==null?"":insure_FEE.get("FEE"))+"元，作为租赁期间保险押金,于乙方次年全额购买"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("      乙方交付甲方人民币"+(insure_FEE.get("FEE")==null?"":insure_FEE.get("FEE"))+"元，作为租赁期间保险押金,于乙方次年全额购买"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					t++;
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("      保险后予以无息退还。"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("      保险后予以无息退还。"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					t++;
	 				int pageNum=(int)Math.floor((t-39)/38)+1;
	 				if(t<=39){
	 					for (;t<39; t++) {
	 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 					}	
	 					if(t==39){
	 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
	 						 t++;
	 					}
	 				}else{
	 					for (;t<38*pageNum+39; t++) {
	 						 tT2.addCell(makeCellSetColspanLRBorder("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));
	 					}	
	 					if(t==38*pageNum+39){
	 						tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
	 						 t++;
	 					}
	 				}
					document.add(tT2);
					if(booknotes.size()>1	&&	bookint<booknotes.size()){	
						document.resetHeader();
					    document.add(Chunk.NEXTPAGE);	
					    
					}
					
				}			
					document.close();
					context.response.setContentType("application/pdf");
					context.response.setCharacterEncoding("UTF-8");
					context.response.setHeader("Pragma", "public");
					context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
					context.response.setDateHeader("Expires", 0);
					context.response.setHeader("Content-Disposition","attachment; filename=zulinwuinfo.pdf");			
					ServletOutputStream o = context.response.getOutputStream();
					baos.writeTo(o); 
					o.flush();				
					o.close();			
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		

		
	   
	    // make a PdfPCell ,for insert into pdf.
	    private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(17f);
		objCell.setHorizontalAlignment(align);

		return objCell;
	    }
	    private PdfPCell makeCell2(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		
		return objCell;
	    }
	    private PdfPCell makeCell3(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(20f);
		objCell.setHorizontalAlignment(align);
		
		return objCell;
	    }
	    
		/**
		 * 创建 无边框 合并 单元格
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
			objCell.setPaddingTop(5);
			objCell.setPaddingBottom(5);
			return objCell;
		}
		
		/**
		 * 创建 无边框 合并 单元格
		 * 
		 */
		private PdfPCell makeCellSetColspanNoBorder(Phrase phrase, int align,
				Font FontDefault, int colspan) {
			Phrase objPhase = phrase;
			PdfPCell objCell = new PdfPCell(objPhase);
			objCell.setHorizontalAlignment(align);
			objCell.setVerticalAlignment(align);
			objCell.setColspan(colspan);
			objCell.setBorderWidthBottom(0);
			objCell.setBorderWidthTop(0);
			objCell.setBorderWidthLeft(0);
			objCell.setBorderWidthRight(0);
			objCell.setPaddingTop(7);
			objCell.setPaddingBottom(7);
			return objCell;
		}

		/**
		 * 创建 有边框 合并 单元格|_| 无上边
		 * 
		 * */
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
	    /** 创建 无边框 单元格 */
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
	    private PdfPCell makeCellWithNoBorder2(String content, int align,
		    Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		 
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	    }

	    /** 创建 有边框 合并 单元格 */
	    private PdfPCell makeCellSetColspan(String content, int align,
		    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);

		return objCell;
	    }
	    /** 创建 有边框 合并 单元格 */
	    private PdfPCell makeCellSetColspanBisdieLeft(String content, int align,
		    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setColspan(colspan);
		objCell.setBorderWidthLeft(0);
		 
		return objCell;
	    }
	    

		/**
		 * 创建 有边框 合并 单元格|- 无下边用于表格的顶
		 * 
		 * */
		private PdfPCell makeCellSetColspan2NoBottomAndRight(String content,
				int align, Font FontDefault, int colspan) {
			Phrase objPhase = new Phrase(content, FontDefault);
			PdfPCell objCell = new PdfPCell(objPhase);
			objCell.setHorizontalAlignment(align);
			objCell.setVerticalAlignment(align);
			objCell.setColspan(colspan);
			objCell.setBorderWidthBottom(0);
			objCell.setBorderWidthRight(0);
			return objCell;
		}
	    
	    /** 创建 有边框 合并 单元格
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
	    
	    private PdfPCell makeCellSetColspan2222(String content, int align,
	    	    Font FontDefault, int colspan) {
	    	Phrase objPhase = new Phrase(content, FontDefault);
	    	PdfPCell objCell = new PdfPCell(objPhase);
	    	objCell.setPaddingLeft(50);
	    	objCell.setHorizontalAlignment(align);
	    	objCell.setVerticalAlignment(align);
	    	objCell.setColspan(colspan);
	    	objCell.setBorderWidthBottom(0);
	    	objCell.setBorderWidthTop(0);
	    	return objCell;
	        }    
	    /** 创建 有边框 合并 单元格
	     *  无下边
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
	    
	    /** 创建 只有上边框 合并 单元格 */
	    private PdfPCell makeCellSetColspan3WithNoBorder(String content, int align,
		    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthLeft(0);
		objCell.setColspan(colspan);
		return objCell;
	    }
	    
	    /** 创建 有边框 合并 单元格
	     *  无上边
	     *  
	     *  */
	    private PdfPCell makeCellSetColspan4(String content, int align,
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
	    private PdfPCell makeCellSetColspanWithNoBorder(String content, int align,
		    Font FontDefault, int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
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
		
		
		/** 创建 只有左右边框 单元格 */
		private PdfPCell makeCellWithBorder(String content, int align, Font FontDefault) {
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
		private PdfPCell makeCellOnlyBottom(String content, int align, Font FontDefault) {
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
		
	    private PdfPCell makeCellSetColspan2ForOne(String content, int align,Font FontDefault, int colspan) {
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
	    

		private PdfPCell makeCellWithBorderLeftForOne(String content, int align, Font FontDefault) {
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
		
		/** 创建 只有右边框 单元格 没有限制高度的特别版 */
		private PdfPCell makeCellWithBorderRightP(String content, int align, Font FontDefault,int auto) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(0);
		    objCell.setBorderWidthTop(0);
//		    objCell.setFixedHeight(20*auto);
		    objCell.setBorderWidthLeft(0);
		    return objCell;
		}

		private PdfPCell makeCellWithBorderRightForOne(String content, int align, Font FontDefault) {
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
		
	    private PdfPCell makeCellSetColspan2ForZLWHead(String content, int align, Font FontDefault, int colspan) {
	    	Phrase objPhase = new Phrase(content, FontDefault);
	    	PdfPCell objCell = new PdfPCell(objPhase);
	    	objCell.setHorizontalAlignment(align);
	    	objCell.setVerticalAlignment(align);
	    	objCell.setColspan(colspan);
	    	objCell.setBorderWidthBottom(0);
	    	objCell.setBorderWidthLeft(0);
	    	objCell.setBorderWidthRight(0);
	    	objCell.setBorderWidthTop(1);
	    	objCell.setFixedHeight(16);
	    	return objCell;
	        }
	    
		private PdfPCell makeCellS(String content, int align, Font FontDefault) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setFixedHeight(20);
		    objCell.setHorizontalAlignment(align);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(1f);
		    objCell.setBorderWidthTop(1f);
		    return objCell;
		}
		
		private PdfPCell makeCellSGAI(String content, int align, Font FontDefault) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setFixedHeight(20);
		    objCell.setHorizontalAlignment(align);
		    objCell.setBorderWidthLeft(0f);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(1f);
		    objCell.setBorderWidthTop(1f);
		    return objCell;
		}
		
		/** 创建 无上边框 合并 单元格 */
		private PdfPCell makeCellSetColspanNoBorderTOP(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setFixedHeight(20);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthBottom(1f);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthTop(0);
		    return objCell;
		}
		
		/** 创建 无上边框 合并 单元格 */
		private PdfPCell makeCellSetColspanNoBorderTOPAuto(String content, int align, Font FontDefault,int colspan,int auto) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthBottom(1f);
		    objCell.setBorderWidthLeft(1);
			objCell.setFixedHeight(20*auto);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthTop(0);
		    return objCell;
		}	
		/** 创建 有边框 合并 单元格 边框加粗版 */
		private PdfPCell makeCellCOS(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(1f);
		    objCell.setBorderWidthTop(1f);
		    objCell.setFixedHeight(20);
		    return objCell;
		}
		
		/** 创建 无上下边框 合并 单元格 */
		private PdfPCell makeCellSetColspanLRBorder(String content, int align, Font FontDefault,int colspan) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setFixedHeight(20);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthLeft(1);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthBottom(0);
		    objCell.setBorderWidthTop(0);
		    return objCell;
		}	
		
		/** 创建 没有顶边 (右边)单元格 */
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
		
		/** 创建 无上边框 合并 单元格 */
		private PdfPCell makeCellSetColspanNoButtomBorderTOPAuto(String content, int align, Font FontDefault,int colspan,int auto) {
		    Phrase objPhase = new Phrase(content, FontDefault);
		    PdfPCell objCell = new PdfPCell(objPhase);
		    objCell.setHorizontalAlignment(align);
		    objCell.setVerticalAlignment(align);
		    objCell.setColspan(colspan);
		    objCell.setBorderWidthBottom(0);
		    objCell.setBorderWidthLeft(1);
			objCell.setFixedHeight(20*auto);
		    objCell.setBorderWidthRight(1);
		    objCell.setBorderWidthTop(0);
		    return objCell;
		}
		
		/** ￥0.00 */
		private String updateMoney(Double dNum,NumberFormat nfFSNum) {
			String str="";
			if (dNum == 0d) {
				str+="0.00";
				return str;
			} else {
				str+=nfFSNum.format(dNum);
				return str;
			}
		}	
		
		
		/**  财务格式  0.00 */
		private String updateMon(Object content) {
		    String str="";
		    
		    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
			
			str+="0.00";
			return str;
			
		    }
		    else{
			
			DecimalFormat df1 = new DecimalFormat("#,###.00"); 
			
			str+=df1.format(Double.parseDouble(content.toString()));
			return str;
		    }	
		}	
		
		/** ￥0.00 */
		private String updateMoney(Map map,String content,NumberFormat nfFSNum) {
			String str="";
			if(map.get(content).toString().equals("0")){
				str+="0.00";
				return str;
			}
			else{
				str+=nfFSNum.format(Double.parseDouble(map.get(content).toString()));
				return str;
			}	
		}
		
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
					
				    this.expLeaseBackPayMoneyPdf(context,rentandpuctcontract);
				}catch(Exception e){
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		 
		 public void expLeaseBackPayMoneyPdf(Context context,Map rentandpuctcontract) {
				
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
				     			money=String.valueOf(rentandpuctcontract.get("MONEY"));
				     		}

					    }
						PdfPTable tT = new PdfPTable(new float[]{10f,20f,20f,20f,20f,20f,10f});
						tT.setWidthPercentage(100f);

						
						
						int i=0;

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
							tT.addCell(makeCellSetColspanNoBorder("根   据   ______年____月 ____日 贵公司与我们签署的融资租赁合同", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("[合同号："+rentcode+"  ] 和买卖合同  [合同号："+rentcode+"  ]，请贵公司代替", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("我方将货款分别支付至以下帐户。", PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("户名："+custname, PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		String creditId =  (String) context.getContextMap().get("PRCD_ID");
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("账号： "+LeaseUtil.getBankAccountByCreditId(creditId), PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("开户银行： "+LeaseUtil.getBankNameByCreditId(creditId), PdfPCell.ALIGN_LEFT, FontDefault2,5));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("金额：   " + SimpleMoneyFormat.getInstance().format(new Double(money)) , PdfPCell.ALIGN_LEFT, FontDefault2,5));
//							tT.addCell(makeCellSetColspanNoBorder("元", PdfPCell.ALIGN_LEFT, FontDefaultTitle,3));
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,7));				
				    		i++;

				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("        我方确认贵公司对设备质量暇疵并不承担任何责任。我方在融资租赁合同项下均无任何违约" , PdfPCell.ALIGN_LEFT, FontDefault2,5));
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++;
				    		
				    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
							tT.addCell(makeCellSetColspanNoBorder("行为,并将严格遵守签订的所有合同,无条件支付融资租赁合同项下所有租金和其他应付款项。"  , PdfPCell.ALIGN_LEFT, FontDefault2,5));
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
		
}
