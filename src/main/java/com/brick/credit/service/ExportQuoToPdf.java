package com.brick.credit.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.brick.baseManage.service.BusinessLog;
import com.brick.collection.CollectionConstants;
import com.brick.collection.service.StartPayService;
import com.brick.collection.util.PayUtils;
import com.brick.credit.service.CreditPaylistService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;

import com.brick.log.service.LogPrint;



public class ExportQuoToPdf extends AService {
	Log logger = LogFactory.getLog(ExportQuoToPdf.class);
	@SuppressWarnings("unchecked")
	/**
	 * 
	 * 于秋辰 2011-09-07
	 * 传入平均冲抵，租金方案 计算出预期租金
	 */
	public static List ExpectMonthPrice(Double pledgeAVEPrice,List irrMonthPaylines) {
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
			
			//Modify by Michael 2012 5-16 租赁物情况表与租金支付明细的租金算法保持一致
//			double eachAVE = Math.round(pledgeAVEPrice / endNum * 100.0d)/100.0d ;
//			double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) *100.0d)/100.0d ;
			//double eachAVE = Math.round( pledgeAVEPrice / endNum * PayUtils.HUNDRED) /PayUtils.HUNDRED;
			//double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) * PayUtils.HUNDRED)/PayUtils.HUNDRED ;
			
	        BigDecimal bd1 = new BigDecimal(Double.toString(pledgeAVEPrice)); 
	        BigDecimal bd2 = new BigDecimal(Double.toString(Double.valueOf(endNum))); 
	        double eachAVE=bd1.divide(bd2,2,BigDecimal.ROUND_HALF_UP).doubleValue();
	        double endAVE = Math.round((pledgeAVEPrice - (eachAVE * (endNum - 1))) * PayUtils.HUNDRED)/PayUtils.HUNDRED ;
			
			for(int i=0;i<irrMonthPaylines.size();i++){
				Map temp = (Map) irrMonthPaylines.get(i) ;
				Map map = null ;
				int start = Integer.parseInt(temp.get("IRR_MONTH_PRICE_START").toString()) ;
				int end = Integer.parseInt(temp.get("IRR_MONTH_PRICE_END").toString()) ;
				double price = Double.parseDouble(temp.get("IRR_MONTH_PRICE").toString()) ;
				if(i == irrMonthPaylines.size() - 1  && eachAVE != endAVE){
					if(start != end ){
						map = new HashMap() ;
						map.put("MONTH_PRICE_START",start ) ;
						map.put("MONTH_PRICE_END",end - 1 ) ;
						map.put("MONTH_PRICE", price + eachAVE) ;
						monthPaylines.add(map) ;
					} 
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",end ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price + endAVE) ;
					monthPaylines.add(map) ;
				}else {
					map = new HashMap() ;
					map.put("MONTH_PRICE_START",start ) ;
					map.put("MONTH_PRICE_END",end ) ;
					map.put("MONTH_PRICE", price + eachAVE) ;
					monthPaylines.add(map) ;
				}
			}
		}
		//增加预期租金计算    结束
		return monthPaylines ;
	}
	
	public void expPayToPdf(Context context) {
		String creditId = context.request.getParameter("credit_id");
		context.contextMap.put("creditId", creditId);
		String dateType = "融资租赁合同类型";
		context.contextMap.put("dateType", dateType);
		Map creditCustomerMap = null;
		List<Map> equipmentsList = null;
		Map schemeMap = null;
		
		ByteArrayOutputStream baos = null;
		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontColumn = new Font(bfChinese, 11, Font.BOLD);
			Font FontDefault = new Font(bfChinese,11, Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 11, Font.UNDERLINE);
			Font fa = new Font(bfChinese, 15, Font.BOLD);
			Font fa2 = new Font(bfChinese, 24, Font.BOLD);
			// 数字格式
			NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
			nfFSNum.setGroupingUsed(true);
			nfFSNum.setMaximumFractionDigits(2);		
			// 页面设置
			Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
			Document document = new Document(rectPageSize, 5, 5, 5, 5); // 其余4个参数，设置了页面的4个边距
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
			float[] widthsPPCb = { 0.8f,0.2f };
			float[] widthsPP666 = { 0.2f,0.8f };
			float[] widthsPP333 = { 0.5f,0.5f };

			
			//   String   urlpath =   context.request.getContextPath() +"/images/disagree.gif";
			//   urlpath =   (ExpContract.class.getResource("hes.jpg")).getPath();		  
			//  Image  img1 =   Image.getInstance(urlpath);			
			String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
			Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");
			//image.setAbsolutePosition(194f, 202f);
			//image.setAlignment(Image.RIGHT);//设置图片的位置
			//image.scaleAbsolute(10,10);//图片的大小
			//image.setAbsolutePosition(194,202); //设置图片的绝对位置 	
			image.setAbsolutePosition(460,710);
			//image.setAlignment(Image.);
			image.scaleAbsoluteHeight(60f);
			image.scaleAbsoluteWidth(60f);			
			//new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime()当前时间		
			
			PdfPCell cell = null;	
			
			PdfPTable tT1 = new PdfPTable(widthsPPCa);
			tT1.setWidthPercentage(90f);
			tT1.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, fa));
			tT1.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, fa));
			tT1.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, fa));
			tT1.addCell(makeCellWithNoBorder("Quotation", PdfPCell.ALIGN_LEFT, fa));
			tT1.addCell(makeCellWithNoBorder("", PdfPCell.ALIGN_LEFT, fa));
			//tT1.addCell(image);
			document.add(tT1);

			PdfPTable tT2 = new PdfPTable(widthsPPCa);
			tT2.setWidthPercentage(90f);
			tT2.addCell(makeCellWithNoBorderForBJD("报价单", PdfPCell.ALIGN_LEFT,fa2));	
			document.add(tT2);

			document.add(image);

			/**
			 * 每期租金和每期实付租金查询语句开始
			 */
			Map paylist = null;
			Map schema = new HashMap();
			schema = CreditPaylistService.copySchema(schema, context.contextMap);
			//
			List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			//	
			paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
			/*paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
			StartPayService.packagePaylinesForMon(paylist);
			
			List<Map> irrMonthPaylines = new ArrayList<Map>();
			 
			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");		*/
			// 每期租金
			StartPayService.packagePaylinesForMon(paylist);
			List<Map> monthPaylines = (List<Map>)paylist.get("irrMonthPaylines");
			// 应付租金
			List<Map> irrMonthPaylines = StartPayService.keepPackagePayline(context);
			paylist.put("irrMonthPaylines", irrMonthPaylines);
			
			/**
			 * 每期租金和每期实付租金查询语句结束
			 */			
			
			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);

			PdfPTable tT333 = new PdfPTable(2);	
			tT333.setWidthPercentage(90f);
			tT333.addCell(makeCellSetColspan2("致:                                                                                                                                                                             "+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontColumn,2));
			document.add(tT333);

			PdfPTable tT3 = new PdfPTable(2);	
			tT3.setWidthPercentage(80f);
			tT3.addCell(makeCellSetColspan2(creditCustomerMap.get("CUST_NAME").toString(),PdfPCell.CCITT_ENDOFLINE, FontUnder,1));
			tT3.addCell(makeCellSetColspan2("                                "+Constants.COMPANY_NAME_ENGLISH,PdfPCell.ALIGN_RIGHT, FontColumn,1));
			document.add(tT3);
			
			PdfPTable tT33 = new PdfPTable(1);
			tT33.setWidthPercentage(30f);
			tT33.setHorizontalAlignment(Element.ALIGN_RIGHT);
			tT33.addCell(makeCellSetColspan2VS1("电话："+creditCustomerMap.get("TELEPHONE").toString()+"(              )",PdfPCell.ALIGN_LEFT, FontDefault,1));
			tT33.addCell(makeCellSetColspan2VS1("传真："+creditCustomerMap.get("FAX").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
			tT33.addCell(makeCellSetColspan2VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));
			document.add(tT33);			

			PdfPTable tT33333 = new PdfPTable(widthsPP333);
			tT33333.setWidthPercentage(30f);
			tT33333.setHorizontalAlignment(Element.ALIGN_RIGHT);
			tT33333.addCell(makeCellWithNoBorder("业务主管:",PdfPCell.ALIGN_RIGHT, FontDefault));
			tT33333.addCell(makeCellWithNoBorder(creditCustomerMap.get("CLERK_NAME").toString(),PdfPCell.ALIGN_LEFT, FontUnder));
			tT33333.addCell(makeCellWithNoBorder("业务承办:",PdfPCell.ALIGN_RIGHT, FontDefault));
			tT33333.addCell(makeCellWithNoBorder(creditCustomerMap.get("SENSOR_NAME").toString(),PdfPCell.ALIGN_LEFT, FontUnder));
			document.add(tT33333);	
			
			PdfPTable tT10 = new PdfPTable(widthsPPCa);
			tT10.setWidthPercentage(90f);
			tT10.addCell(makeCellWithNoBorder("          我公司已按贵公司的要求，特别制定了本报价，敬请过目！",PdfPCell.ALIGN_LEFT, FontDefault));
			tT10.addCell(makeCellWithNoBorder("诚挚盼望 贵我双方通过这次合作能达成长期友好的伙伴关系。",PdfPCell.ALIGN_LEFT, FontDefault));
			document.add(tT10);
			document.add(new Paragraph("\n"));


			
			PdfPTable tableHdr2 = new PdfPTable(5);
			tableHdr2.setWidthPercentage(90f);   
			tableHdr2.addCell(makeCellSetColspan22("租赁形式",PdfPCell.ALIGN_CENTER, FontColumn,1));
			tableHdr2.addCell(makeCellSetColspan22VS1(String.valueOf(creditCustomerMap.get("CONTRACT_TYPE")),PdfPCell.ALIGN_CENTER, FontColumn,4)); 

			
			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			if(equipmentsList.size()<=4){
				if(equipmentsList.size()==0){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));		
				}
				if(equipmentsList.size()==1){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));	
				}
				if(equipmentsList.size()==2){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));
				}
				if(equipmentsList.size()==3){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,1));
				}
				if(equipmentsList.size()==4){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("THING_NAME").toString()+"("+equipmentsList.get(3).get("MODEL_SPEC")+")",PdfPCell.ALIGN_CENTER, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("BRAND").toString(),PdfPCell.ALIGN_CENTER, FontDefault,1));
				}				
			}else{
				if(equipmentsList.size()==5){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("THING_NAME").toString()+"("+equipmentsList.get(3).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("THING_NAME").toString()+"("+equipmentsList.get(4).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));
				}
				if(equipmentsList.size()==6){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("THING_NAME").toString()+"("+equipmentsList.get(3).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("THING_NAME").toString()+"("+equipmentsList.get(4).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("THING_NAME").toString()+"("+equipmentsList.get(5).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell("",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));
				}
				if(equipmentsList.size()==7){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("THING_NAME").toString()+"("+equipmentsList.get(3).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("THING_NAME").toString()+"("+equipmentsList.get(4).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("THING_NAME").toString()+"("+equipmentsList.get(5).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(6).get("THING_NAME").toString()+"("+equipmentsList.get(6).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(6).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_LEFT, FontDefault,1));
				}
				if(equipmentsList.size()>=8){
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("THING_NAME").toString()+"("+equipmentsList.get(0).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("THING_NAME").toString()+"("+equipmentsList.get(1).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("THING_NAME").toString()+"("+equipmentsList.get(2).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("THING_NAME").toString()+"("+equipmentsList.get(3).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(0).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(1).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(2).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(3).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
					tableHdr2.addCell(makeCellSetColspan33("产品(型号)",PdfPCell.ALIGN_CENTER, FontColumn,1));				
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("THING_NAME").toString()+"("+equipmentsList.get(4).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("THING_NAME").toString()+"("+equipmentsList.get(5).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(6).get("THING_NAME").toString()+"("+equipmentsList.get(6).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(7).get("THING_NAME").toString()+"("+equipmentsList.get(7).get("MODEL_SPEC")+")",PdfPCell.ALIGN_LEFT, FontDefault,1));				
					tableHdr2.addCell(makeCellSetColspan33("供应商",PdfPCell.ALIGN_CENTER, FontColumn,1));			
					tableHdr2.addCell(makeCell(""+equipmentsList.get(4).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(5).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCell(""+equipmentsList.get(6).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault));	
					tableHdr2.addCell(makeCellSetColspan33VS1(""+equipmentsList.get(7).get("BRAND").toString(),PdfPCell.ALIGN_LEFT, FontDefault,1));
				}				
			}
			
			double total = 0d;
			for (int i=0;i<equipmentsList.size();i++) {
				total += DataUtil.doubleUtil(equipmentsList.get(i).get("TOTAL"));
			}
			if(equipmentsList.size()==0){			
				tableHdr2.addCell(makeCellSetColspanNoBott("购买额",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr2.addCell(makeCellSetColspanNoBottVS1("",PdfPCell.ALIGN_CENTER, FontDefault,4));			
			}else{
				tableHdr2.addCell(makeCellSetColspanNoBott("购买额",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr2.addCell(makeCellSetColspanNoBottVS1(updateMon(total+"")+"元",PdfPCell.ALIGN_CENTER, FontDefault,4));	
			}

			
			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);

			if(schemeMap==null){			
				tableHdr2.addCell(makeCellSetColspan33("保证金",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr2.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,4));		
			}else{
				tableHdr2.addCell(makeCellSetColspan33("保证金",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr2.addCell(makeCellSetColspan33VS1(updateMon(schemeMap.get("PLEDGE_PRICE").toString())+"元",PdfPCell.ALIGN_CENTER, FontDefault,4));
				
			}
			if(schemeMap==null){				
				tableHdr2.addCell(makeCellSetColspanNoBott("租赁期数",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr2.addCell(makeCellSetColspanNoBottVS2("",PdfPCell.ALIGN_CENTER, FontDefault,2));
				tableHdr2.addCell(makeCellSetColspanNoBottVS1("实际支付（  ）期",PdfPCell.ALIGN_CENTER, FontDefault,2));
			}else{
				tableHdr2.addCell(makeCellSetColspanNoBott("租赁期数",PdfPCell.ALIGN_CENTER, FontColumn,1));
				// tableHdr2.addCell(makeCellSetColspanNoBottVS2(schemeMap.get("YEAR").toString()+"年（"+schemeMap.get("LEASE_TERM").toString()+"期、每期"+schemeMap.get("LEASE_COURSE").toString()+"个月）",PdfPCell.ALIGN_CENTER, FontDefault,2));
				tableHdr2.addCell(makeCellSetColspanNoBottVS2(schemeMap.get("LEASE_TERM").toString()+" 期(每期 "+schemeMap.get("LEASE_COURSE").toString()+" 个月）",PdfPCell.ALIGN_CENTER, FontDefault,2));
				tableHdr2.addCell(makeCellSetColspanNoBottVS1("实际支付（"+schemeMap.get("LEASE_TERM").toString()+"）期",PdfPCell.ALIGN_CENTER, FontDefault,2));
			}
			document.add(tableHdr2);
			
			PdfPTable tableHdr6 = new PdfPTable(widthsPP666);
			tableHdr6.setWidthPercentage(90f);
			int cnt=monthPaylines.size();
			tableHdr6.addCell(makeCellSetColspanForAuto("每期租金",PdfPCell.ALIGN_CENTER, FontColumn,1,cnt*20));
			String cont="";
			for(int x = 0;x<monthPaylines.size();x++){
				 cont=cont+"第"+ (monthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元\n\n";
			}
			tableHdr6.addCell(makeCellSetColspanForAutoVS1(cont, PdfPCell.ALIGN_LEFT, FontDefault,1,cnt*20));
			document.add(tableHdr6);

			
			PdfPTable tableHdr0 = new PdfPTable(widthsPP666);
			tableHdr0.setWidthPercentage(90f);
			int cnt2=irrMonthPaylines.size();
			tableHdr0.addCell(makeCellSetColspanNoBottForAuto("每期实付金额",PdfPCell.ALIGN_CENTER, FontColumn,1,cnt2*20));
			String cont2="";
			for(int x = 0;x<irrMonthPaylines.size();x++){
				cont2+="第"+irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")+"-"+irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")+"期每月RMB【"+  updateMon((irrMonthPaylines.get(x).get("IRR_MONTH_PRICE")+"" ))  +"】元\n\n";
			}		
			tableHdr0.addCell(makeCellSetColspanForAutoVS1(cont2, PdfPCell.ALIGN_LEFT, FontDefault,1,cnt2*20));
			document.add(tableHdr0);			
			
			PdfPTable tableHdr7 = new PdfPTable(5);
			tableHdr7.setWidthPercentage(90f);
			if(equipmentsList.size()==0){
				tableHdr7.addCell(makeCellSetColspan33("期末购买权",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr7.addCell(makeCellSetColspan33VS1("",PdfPCell.ALIGN_CENTER, FontDefault,4));
			}else{
				double staybuyPrice =0.0;
				for(int i=0;i<equipmentsList.size();i++){
					staybuyPrice +=DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE")) ;
				}
				tableHdr7.addCell(makeCellSetColspan33("期末购买权",PdfPCell.ALIGN_CENTER, FontColumn,1));
				tableHdr7.addCell(makeCellSetColspan33VS1(updateMon(String.valueOf(staybuyPrice))+"元",PdfPCell.ALIGN_CENTER, FontDefault,4));
			}
			document.add(tableHdr7);
			
			PdfPTable tableHdr8 = new PdfPTable(widthsPP666);	
			tableHdr8.setWidthPercentage(90f);
			tableHdr8.addCell(makeCellSetColspan44("特殊说明",PdfPCell.ALIGN_CENTER, FontColumn,1));
			String cont3="1、本报价单中的每期租金金额已含营业税、保险费、印纸税。\n\n"+
			"2、本报价单有效期为30天。\n\n"+
			"3、报价有效内，若中国人民银行贷款利率发生变动，裕融租賃保留更改租金金额的权利\n\n"+
			"4、融资额度：\n\n"+
			"5、付款方式：\n\n"+
			"                                                                                         确认签字：\n\n"+
			"                                                                                         回传日期：";
			tableHdr8.addCell(makeCellSetColspan44VS1(cont3,PdfPCell.ALIGN_LEFT, FontColumn,1));			
			document.add(tableHdr8);			
			
			document.close();
			context.response.setContentType("application/pdf");
			context.response.setCharacterEncoding("UTF-8");
			context.response.setHeader("Pragma", "public");
			context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			context.response.setDateHeader("Expires", 0);
			context.response.setHeader("Content-Disposition","attachment; filename=quotation");			
			ServletOutputStream o = context.response.getOutputStream();
			baos.writeTo(o); 
			o.flush();				
			o.close();			
		} catch (Exception e) {
			e.printStackTrace();	
			LogPrint.getLogStackTrace(e, logger);
		}
		
	}	

	
	

	
	
	@SuppressWarnings("unchecked")
	public void expZulwToPdf(Context context) {

		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
		ArrayList booknotes=new ArrayList();
		HashMap booknote=new HashMap();
		Map creditCustomerMap = null;
		List<Map> equipmentsList = null;
		Map schemeMap = null;
		Map paylist = null;
		try{
			Map schema = new HashMap();
			schema = CreditPaylistService.copySchema(schema, context.contextMap);
			List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			
			//paylist.put("rePaylineList", rePaylineList);
			paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
			paylist.put("oldirrMonthPaylines", paylist.get("irrMonthPaylines"));
			paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
			StartPayService.packagePaylinesForMon(paylist);
			
			List<Map> irrMonthPaylines = new ArrayList<Map>();			 
			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
			
			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
			//修改 若是法人，查法人表			
			if(custTyp.equals("1")){
			    
			    creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}
			
			if(creditCustomerMap==null){
				creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			}
			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
			booknote.put("rePaylineList", rePaylineList);
			booknote.put("schema", schema);
			booknote.put("paylist", paylist);
			booknote.put("irrMonthPaylines", irrMonthPaylines);
			booknote.put("monthPaylines",  this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE").toString()),(List)paylist.get("oldirrMonthPaylines")));
			booknote.put("equipmentsList", equipmentsList);
			booknote.put("creditCustomerMap", creditCustomerMap);
			booknote.put("schemeMap", schemeMap);
			booknotes.add(booknote);
			context.contextMap.put("booknotes", booknotes);
			this.expZulwPdfModel(context,"expZulwToPdf");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void expZulwToPdf_new(Context context) {

		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
		ArrayList booknotes=new ArrayList();
		HashMap booknote=new HashMap();
		Map creditCustomerMap = null;
		List<Map> equipmentsList = null;
		Map schemeMap = null;
		Map paylist = new HashMap();
		try{
			Map schema = new HashMap();
			schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 查询应付租金列表
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			//System.out.println(irrMonthPaylines.size()+"=============");
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
			//System.out.println(rePaylineList.size());
			//List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			
			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
			paylist = schemeMap ;
			//paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
			paylist.put("rePaylineList", rePaylineList);
			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
			paylist.put("irrMonthPaylines", irrMonthPaylines);
			paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
			paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
			StartPayService.packagePaylinesForMon(paylist);
			
			//List<Map> irrMonthPaylines = new ArrayList<Map>();			 
			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
			
			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
			//修改 若是法人，查法人表			
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
			booknote.put("monthPaylines", this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""), (List)paylist.get("oldirrMonthPaylines")));
			booknote.put("equipmentsList", equipmentsList);
			booknote.put("creditCustomerMap", creditCustomerMap);
			booknote.put("schemeMap", schemeMap);
			booknotes.add(booknote);
			context.contextMap.put("booknotes", booknotes);
			this.expZulwPdfModel(context,"expZulwToPdf");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	

	@SuppressWarnings("unchecked")
	public void expZulwPdfModel(Context context,String methodname){
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
			for(int bookint=0;bookint<booknotes.size();bookint++){
				Map paylist = null;
				Map schema = new HashMap();
				/*	schema = CreditPaylistService.copySchema(schema, context.contextMap);
				//
				List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
				//	
				paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
				paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
				StartPayService.packagePaylinesForMon(paylist);
				
				List<Map> irrMonthPaylines = new ArrayList<Map>();
				 
				irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
				*/
				schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
				paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
				List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
				List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
				List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
				int lenn1 = irrMonthPaylines.size();
			
				PdfPTable tT1 = new PdfPTable(widthsPPCa);
				 
				tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
				tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
				document.add(tT1);
				
			//	equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
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
				tT2.addCell(makeCellSGAI("2、制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
				//用于当页结尾 保正最后一行有下划线 吴振东 2010-12-9
				
				int siz = equipmentsList.size();
				if(siz!=0){
					t=t+siz*3;
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
					tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt));	
					
					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP("", PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
					
					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
					tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
						
				}
				 
				
				//creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
				
				//schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
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
				tT2.addCell(makeCellSetColspanNoButtomBorderTOPAuto("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
				tT2.addCell(makeCellSetColspanNoBorderTOPAuto("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,equAdd_cnt));
				t=t+4;
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				if(methodname.equals("expZulwToPdf")){
					// 2010-12-08 wujw 历史代码查询数据库 得出历史的租赁周期和期数
					//年保留两位小数
//					double year=DataUtil.doubleUtil(paylist.get("LEASE_PERIOD"))*DataUtil.doubleUtil(paylist.get("LEASE_TERM"))/12;
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					
					tT2.addCell(makeCellSetColspanLRBorder("   【" + newyear  +"】年（【"+DataUtil.intUtil(paylist.get("LEASE_PERIOD"))+"】期，每期" + DataUtil.intUtil(paylist.get("LEASE_TERM"))+"个月）" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+2;
				for(int x = 0;x<monthPaylines.size();x++){
				    
				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+monthPaylines.size();
//				for(int x = 0;x<irrMonthPaylines.size();x++){
//				    
//				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
//						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
//				}
//				t=t+monthPaylines.size();
				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
				// 交付预定日及验收期限栏：交付预定日：2010年___月（此月份用空白显示，让业务员填写）
				//tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：20____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
				t=t+3;			
				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+  updateMoney(DataUtil.doubleUtil(paylist.get("PLEDGE_PRICE")),nfFSNum) +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+   updateMon(schemeMap.get("PLEDGE_PRICE")+"") +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+2; 
				
				 
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
				t=t+1;
				// 4、租金支付方法及方式栏：如果是期初的话 第1期显示为：“租赁物验收证明书交付之日，以转账支付。”
				// 如果是期末的话 第1期显示为：“租赁物验收证明书交付之日，后30日以转账支付。”
				int payWay =0;
				if(methodname.equals("expZulwToPdf")){
					payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
				}else{
					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
				}
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
				double stayBuyPrice = 0d;
				for (int i=0;i<equipmentsList.size();i++) {
					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
				}
				
				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 
				t=t+2;
	
				if(methodname.equals("expZulwToPdf")){
				//StartPayService.packagePaylines(paylist);
					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");
				}else{
					
					//StartPayService.packagePaylines(paylist);
					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");	
				}
				int lenn2 = irrMonthPaylines.size();
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于平均抵冲金额: "+updateMon(paylist.get("PLEDGE_AVE_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于平均抵冲金额: "+updateMon(paylist.get("PLEDGE_AVE_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金	用于期末退还金额: "+updateMon(paylist.get("PLEDGE_BACK_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于期末退还金额: "+updateMon(paylist.get("PLEDGE_BACK_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数: "+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"/"+paylist.get("PLEDGE_LAST_PERIOD"), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数: "+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"/"+paylist.get("PLEDGE_LAST_PERIOD"), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));				
				}
				t++;			
				for(int x = 0;x<irrMonthPaylines.size();x++){
					if(t==39||(t-39)%38==0){
				    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
					    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					    //t++;
			 		}
				}
				t=t+irrMonthPaylines.size();
				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
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
				
				//add by ShenQi 插入系统日志
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 租赁物情况表",
			   		 	 "合同浏览导出 租赁物情况表",
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


//Add by Michael 2012 04-01 导出重车情况表
	@SuppressWarnings("unchecked")
	public void expCarZulwToPdf_new(Context context) {

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
			StartPayService.packagePaylinesForMon(paylist);
			
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
			booknote.put("monthPaylines", this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""), (List)paylist.get("oldirrMonthPaylines")));
			booknote.put("equipmentsList", equipmentsList);
			booknote.put("creditCustomerMap", creditCustomerMap);
			booknote.put("schemeMap", schemeMap);
			booknote.put("insure_FEE", insure_FEE);
			booknotes.add(booknote);
			context.contextMap.put("booknotes", booknotes);
			this.expCarZulwPdfModel(context,"expZulwToPdf");
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	@SuppressWarnings("unchecked")
	public void expCarZulwPdfModel(Context context,String methodname){
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
					t=t+siz*3;
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
					
					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+"     颜色:"+(equipmentsList.get(i).get("CAR_COLOR")==null?"":equipmentsList.get(i).get("CAR_COLOR")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
					
					tT2.addCell(makeCellWithBorder("发动机号:"+(equipmentsList.get(i).get("CAR_ENGINE_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ENGINE_NUMBER"))+"   牌号："+(equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")==null?"":equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
					
					tT2.addCell(makeCellWithBorder("车架号："+(equipmentsList.get(i).get("CAR_ID_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ID_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));
					
					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
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
				for(int x = 0;x<monthPaylines.size();x++){
				    
				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+monthPaylines.size();

				if(methodname.equals("expZulwToPdf")){
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
				t=t+3;			
				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));

				tT2.addCell(makeCellSetColspanNoBorderTOP("    (1)RMB【"+   updateMon(schemeMap.get("PLEDGE_AVE_PRICE")+"") +"】元（平均抵充） 。"+"    (2)RMB【"+   updateMon(schemeMap.get("PLEDGE_BACK_PRICE")+"") +"】元（期末返还） 。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				t=t+2; 
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
				t=t+1;

				int payWay =0;

				payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
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
//				double stayBuyPrice = 0d;
//				for (int i=0;i<equipmentsList.size();i++) {
//					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
//				}
				
				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 
				t=t+2;
				int lenn2 = irrMonthPaylines.size();
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
				t++;
				for(int x = 0;x<irrMonthPaylines.size();x++){
					if(t==39||(t-39)%38==0){
				    tT2.addCell(makeCellSetColspanLRBorder("       (1)每期实缴租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期人民币RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
					    tT2.addCell(makeCellSetColspanLRBorder("       (1)每期实缴租金：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期人民币RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
			 		}
				}
				t=t+irrMonthPaylines.size();
				t++;
				tT2.addCell(makeCellSetColspanLRBorder("        (2)乙方交付甲方人民币"+(insure_FEE.get("FEE")==null?"":insure_FEE.get("FEE"))+"元，作为租赁期间保险押金于乙方次年全额购买"
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("        保险后予以无息退还."
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				
				int pageNum=(int)Math.floor((t-39)/38)+1;
				tT2.addCell(makeCellSetColspanNoBorderTOP("    ", PdfPCell.ALIGN_LEFT, FontDefault,2));	
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

	//Add by Michael 2012 04-01 导出重车情况表  For增值税
		@SuppressWarnings("unchecked")
	public void expCarZulwToPdfByValueAdded(Context context) {

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

	// Add by Michael For增值税
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
						
						tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC"))+"     颜色:"+(equipmentsList.get(i).get("CAR_COLOR")==null?"":equipmentsList.get(i).get("CAR_COLOR")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
						
						tT2.addCell(makeCellWithBorder("发动机号:"+(equipmentsList.get(i).get("CAR_ENGINE_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ENGINE_NUMBER"))+"   牌号："+(equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")==null?"":equipmentsList.get(i).get("CAR_RIGSTER_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
						
						tT2.addCell(makeCellWithBorder("车架号："+(equipmentsList.get(i).get("CAR_ID_NUMBER")==null?"":equipmentsList.get(i).get("CAR_ID_NUMBER")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
						tT2.addCell(makeCellWithBorderRightP(" " , PdfPCell.ALIGN_LEFT, FontDefault,cnt2));
						
						tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
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
					for(int x = 0;x<monthPaylines.size();x++){
					    
					    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (monthPaylines.get(x).get("MONTH_PRICE_START")==null?"":monthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(monthPaylines.get(x).get("MONTH_PRICE_END")==null?"":monthPaylines.get(x).get("MONTH_PRICE_END"))+"期每月未税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(monthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"  
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					t=t+monthPaylines.size();

					if(methodname.equals("expZulwToPdf")){
						tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					t++;
					
					tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
					tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
					tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
					t=t+3;			
					tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));

					tT2.addCell(makeCellSetColspanNoBorderTOP("    (1)RMB【"+   updateMon(schemeMap.get("PLEDGE_AVE_PRICE")+"") +"】元（平均抵充） 。"+"    (2)RMB【"+   updateMon(schemeMap.get("PLEDGE_BACK_PRICE")+"") +"】元（期末返还） 。", PdfPCell.ALIGN_LEFT, FontDefault,2));
					t=t+2; 
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
					t=t+1;

					int payWay =0;

					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
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
//					double stayBuyPrice = 0d;
//					for (int i=0;i<equipmentsList.size();i++) {
//						stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
//					}
					
					tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
					 
					t=t+2;
					int lenn2 = irrMonthPaylines.size();
					
					tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
					t++;
					for(int x = 0;x<irrMonthPaylines.size();x++){
						if(t==39||(t-39)%38==0){
					    tT2.addCell(makeCellSetColspanNoBorderTOP("       (1)每期实缴租金 ：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期人民币未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"   
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
						}else{
						    tT2.addCell(makeCellSetColspanLRBorder("       (1)每期实缴租金：第"+ (irrMonthPaylines.get(x).get("MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("MONTH_PRICE_END"))+"期人民币未税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE" ,nfFSNum)+"】元,含税RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"MONTH_PRICE_TAX" ,nfFSNum)+"】元"   
							 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				 		}
					}
					t=t+irrMonthPaylines.size();
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("        (2)乙方交付甲方人民币"+(insure_FEE.get("FEE")==null?"":insure_FEE.get("FEE"))+"元，作为租赁期间保险押金于乙方次年全额购买"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("        (2)乙方交付甲方人民币"+(insure_FEE.get("FEE")==null?"":insure_FEE.get("FEE"))+"元，作为租赁期间保险押金于乙方次年全额购买"
								 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}
					t++;
					if(t==39||(t-39)%38==0){
						tT2.addCell(makeCellSetColspanNoBorderTOP("        保险后予以无息退还."
									 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
						tT2.addCell(makeCellSetColspanLRBorder("        保险后予以无息退还."
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
	
	@SuppressWarnings("unchecked")
	public void expDirectAttaPdf(Context context) {

		context.contextMap.put("creditId", context.contextMap.get("credit_id"));
		ArrayList booknotes=new ArrayList();
		HashMap booknote=new HashMap();
		Map creditCustomerMap = null;
		List<Map> equipmentsList = null;
		Map schemeMap = null;
		Map paylist = new HashMap();
		try{
			Map schema = new HashMap();
			schema = (Map)DataAccessor.query("beforeMakeContract.selectCreditShemaDetail",context.contextMap, DataAccessor.RS_TYPE.MAP);
			// 查询应付租金列表
			List<Map> irrMonthPaylines = StartPayService.queryPackagePayline(context.contextMap.get("credit_id"), Integer.valueOf(1));
			//System.out.println(irrMonthPaylines.size()+"=============");
			// 解压irrMonthPaylines到每一期的钱
			List<Map> rePaylineList = StartPayService.upPackagePaylines(irrMonthPaylines);
			//System.out.println(rePaylineList.size());
			//List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
			
			schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme_new",context.contextMap, DataAccessor.RS_TYPE.MAP);
			paylist = schemeMap ;
			//paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,irrMonthPaylines);
			paylist.put("rePaylineList", rePaylineList);
			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
			paylist.put("irrMonthPaylines", irrMonthPaylines);
			paylist.put("PLEDGE_LAST_PERIOD", schema.get("PLEDGE_LAST_PERIOD"));
			paylist.put("PLEDGE_PERIOD", schema.get("PLEDGE_PERIOD"));
			StartPayService.packagePaylinesForMon(paylist);
			
			//List<Map> irrMonthPaylines = new ArrayList<Map>();			 
			irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
			
			equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
			creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
			String custTyp =creditCustomerMap.get("CUST_TYPE") +"";		
			//修改 若是法人，查法人表			
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
			booknote.put("monthPaylines", this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""), (List)paylist.get("oldirrMonthPaylines")));
			booknote.put("equipmentsList", equipmentsList);
			booknote.put("creditCustomerMap", creditCustomerMap);
			booknote.put("schemeMap", schemeMap);
			booknotes.add(booknote);
			context.contextMap.put("booknotes", booknotes);
			this.directAttaPdf(context);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	

	@SuppressWarnings("unchecked")
	public void directAttaPdf(Context context){
		ArrayList booknotes=new ArrayList();
		String thing_name = null;
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
			for(int bookint=0;bookint<booknotes.size();bookint++){
				Map paylist = null;
				Map schema = new HashMap();
				/*	schema = CreditPaylistService.copySchema(schema, context.contextMap);
				//
				List<Map> rePaylineList = StartPayService.upPackagePaylines(context);
				//	
				paylist = StartPayService.createCreditPaylistIRR(schema,rePaylineList,StartPayService.keepPackagePayline(context));
				paylist.put("irrMonthPaylines", StartPayService.keepPackagePayline(context));
				StartPayService.packagePaylinesForMon(paylist);
				
				List<Map> irrMonthPaylines = new ArrayList<Map>();
				 
				irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");
				*/
				schema=(HashMap)((HashMap)booknotes.get(bookint)).get("schema");
				paylist=(HashMap)((HashMap)booknotes.get(bookint)).get("paylist");
				List<Map> rePaylineList=(List<Map>)((HashMap)booknotes.get(bookint)).get("rePaylineList");
				List<Map> irrMonthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("irrMonthPaylines");
				List<Map> monthPaylines=(List<Map>)((HashMap)booknotes.get(bookint)).get("monthPaylines");
				int lenn1 = irrMonthPaylines.size();
			
				PdfPTable tT1 = new PdfPTable(widthsPPCa);
				 
				tT1.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_LEFT, FontColumn)); 
				tT1.addCell(makeCellWithNoBorder("附表", PdfPCell.ALIGN_LEFT, FontColumn));
				document.add(tT1);
				
			//	equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCreditEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
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
				tT2.addCell(makeCellSGAI("2、制 造 商 ", PdfPCell.ALIGN_LEFT, FontColumn));
				//用于当页结尾 保正最后一行有下划线 吴振东 2010-12-9
				
				int siz = equipmentsList.size();
				if(siz!=0){
					t=t+siz*3;
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
					
					thing_name = StringUtils.isEmpty(equipmentsList.get(i).get("THING_NAME")) ? "" : (String)equipmentsList.get(i).get("THING_NAME");
					for(int j = 0; j < (thing_name.length() % 15 == 0 ? thing_name.length() / 15 : (thing_name.length() / 15) + 1); j++){
						if (j == 0) {
							tT2.addCell(makeCellWithBorder("名称 : " + thing_name.substring(j * 15, thing_name.length() <= (j + 1) * 15 ? thing_name.length() : (j + 1) * 15), PdfPCell.ALIGN_LEFT, FontDefault,1));
							tT2.addCell(makeCellWithBorderRightP("制造商 : "+(equipmentsList.get(i).get("MANUFACTURER")==null?"":equipmentsList.get(i).get("MANUFACTURER")) , PdfPCell.ALIGN_LEFT, FontDefault,cnt));
						} else {
							tT2.addCell(makeCellWithBorder("      " + thing_name.substring(j * 15, thing_name.length() <= (j + 1) * 15 ? thing_name.length() : (j + 1) * 15), PdfPCell.ALIGN_LEFT, FontDefault,1));
							tT2.addCell(makeCellWithBorderRightP("", PdfPCell.ALIGN_LEFT, FontDefault,cnt));
						}
					}
					
						
					
					tT2.addCell(makeCellWithBorder("型号 : "+(equipmentsList.get(i).get("MODEL_SPEC")==null?"":equipmentsList.get(i).get("MODEL_SPEC")) , PdfPCell.ALIGN_LEFT, FontDefault,1));
					tT2.addCell(makeCellWithBorderRightP("", PdfPCell.ALIGN_LEFT, FontDefault,cnt2));	
					
					tT2.addCell(makeCellOnlyBottom("数量 : "+(equipmentsList.get(i).get("AMOUNT")==null?"":equipmentsList.get(i).get("AMOUNT")) +" "+(equipmentsList.get(i).get("UNIT")==null?"":equipmentsList.get(i).get("UNIT")) , PdfPCell.ALIGN_LEFT, FontDefault));
					tT2.addCell(makeCellRightBottom(" ", PdfPCell.ALIGN_LEFT, FontDefault));	
						
				}
				 
				
				//creditCustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCreditInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				Map creditCustomerMap = (HashMap)((HashMap)booknotes.get(bookint)).get("creditCustomerMap");
				
				//schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCreditScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);
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
				tT2.addCell(makeCellSetColspanNoButtomBorderTOPAuto("公 司 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("CORP_REGISTE_ADDRESS")==null)?"":creditCustomerMap.get("CORP_REGISTE_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,compAdd_cnt));
				tT2.addCell(makeCellSetColspanNoBorderTOPAuto("租 赁 物 放 置 地 址 ："+((creditCustomerMap==null||creditCustomerMap.get("EQUPMENT_ADDRESS")==null)?"":creditCustomerMap.get("EQUPMENT_ADDRESS")), PdfPCell.ALIGN_LEFT, FontDefault,2,equAdd_cnt));
				t=t+4;
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("4、租 赁 期 间 及 租 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				/*if(methodname.equals("expZulwToPdf")){*/
					// 2010-12-08 wujw 历史代码查询数据库 得出历史的租赁周期和期数
					//年保留两位小数
//					double year=DataUtil.doubleUtil(paylist.get("LEASE_PERIOD"))*DataUtil.doubleUtil(paylist.get("LEASE_TERM"))/12;
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
					
					tT2.addCell(makeCellSetColspanLRBorder("   【" + newyear  +"】年（【"+DataUtil.intUtil(paylist.get("LEASE_PERIOD"))+"】期，每期" + DataUtil.intUtil(paylist.get("LEASE_TERM"))+"个月）" , PdfPCell.ALIGN_LEFT, FontDefault,2));
					/*}else{
					double year=DataUtil.doubleUtil(schemeMap.get("YEAR"));
					BigDecimal byear=new BigDecimal(year);
					double newyear=byear.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

					tT2.addCell(makeCellSetColspanLRBorder("   【"+newyear+"】年（【"+schemeMap.get("LEASE_PERIOD").toString()+"】期，每期"+schemeMap.get("LEASE_TERM").toString()+"个月）", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}*/
				t=t+2;
				
				//updateMon(paylist.get("PLEDGE_AVE_PRICE"))
				Object firstMonthPriceObj = irrMonthPaylines.get(0).get("IRR_MONTH_PRICE");
				Object pledgeAvePriceObj = paylist.get("PLEDGE_AVE_PRICE");
				Double firstMonthPrice = firstMonthPriceObj == null ? 0 : Double.parseDouble(String.valueOf(firstMonthPriceObj));
				Double pledgeAvePrice = pledgeAvePriceObj == null ? 0 : Double.parseDouble(String.valueOf(pledgeAvePriceObj));
				
				tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第1期每月RMB【"+ updateMoney(firstMonthPrice + pledgeAvePrice ,nfFSNum)+"】元。实际支付租金依《租金支付明细表》" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("             应为RMB【"+ updateMoney(firstMonthPrice ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				t = t + 2;
				String startPeriod = null;
				for(int x = 0;x<irrMonthPaylines.size();x++){
					if (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START") != null && !String.valueOf(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")).equals("1")) {
						startPeriod = String.valueOf(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"));
					} else {
						startPeriod = "2";
					}
				    tT2.addCell(makeCellSetColspanLRBorder("    实际支付租金 ：第"+ startPeriod +"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t=t+irrMonthPaylines.size() + 1;
//				for(int x = 0;x<irrMonthPaylines.size();x++){
//				    
//				    tT2.addCell(makeCellSetColspanLRBorder("    租 金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
//						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
//				}
//				t=t+monthPaylines.size();
				/*if(methodname.equals("expZulwToPdf")){*/
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				/*}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}*/
				t++;
				
				tT2.addCell(makeCellCOS("5、交 付 预 定 日 及 验 收 期 限", PdfPCell.ALIGN_LEFT, FontColumn,2));
				// 交付预定日及验收期限栏：交付预定日：2010年___月（此月份用空白显示，让业务员填写）
				//tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ："+schemeMap.get("START_DATE").toString().substring(0, 4)+"年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    交 付 预 定 日 ：20____年____月" , PdfPCell.ALIGN_LEFT, FontDefault,2));
				tT2.addCell(makeCellSetColspanLRBorder("    验 收 期 限 ：交付日后3天内", PdfPCell.ALIGN_LEFT, FontDefault,2));
				t=t+3;			
				tT2.addCell(makeCellCOS("6、保 证 金", PdfPCell.ALIGN_LEFT, FontColumn,2));
				/*if(methodname.equals("expZulwToPdf")){*/
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+  updateMoney(pledgeAvePrice,nfFSNum) +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				/*}else{
					tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+   updateMon(schemeMap.get("PLEDGE_PRICE")+"") +"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}*/
				t=t+2; 
				
				 
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("7、租 金 支 付 方 法 及 方 式", PdfPCell.ALIGN_LEFT, FontColumn,2));
				t=t+1;
				// 4、租金支付方法及方式栏：如果是期初的话 第1期显示为：“租赁物验收证明书交付之日，以转账支付。”
				// 如果是期末的话 第1期显示为：“租赁物验收证明书交付之日，后30日以转账支付。”
				int payWay =0;
				/*if(methodname.equals("expZulwToPdf")){*/
					payWay = DataUtil.intUtil(paylist.get("PAY_WAY"));
				/*}else{
					payWay = DataUtil.intUtil(schemeMap.get("PAY_WAY"));
				}*/
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
				double stayBuyPrice = 0d;
				for (int i=0;i<equipmentsList.size();i++) {
					stayBuyPrice += DataUtil.doubleUtil(equipmentsList.get(i).get("STAYBUY_PRICE"));
				}
				
				tT2.addCell(makeCellCOS("8、租 期 结 束 后 的 购 买 选 择 权", PdfPCell.ALIGN_LEFT, FontColumn,2));
				tT2.addCell(makeCellSetColspanNoBorderTOP("    RMB【"+updateMoney(stayBuyPrice, nfFSNum)+"】元", PdfPCell.ALIGN_LEFT, FontDefault,2));
				 
				t=t+2;
	
				/*if(methodname.equals("expZulwToPdf")){*/
				//StartPayService.packagePaylines(paylist);
					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");
				/*}else{
					
					//StartPayService.packagePaylines(paylist);
					irrMonthPaylines = (List<Map>) paylist.get("oldirrMonthPaylines");	
				}*/
				int lenn2 = irrMonthPaylines.size();
				
				tT2.addCell(makeCellSetColspanNoBorderTOP("9、附 属 条 款 " , PdfPCell.ALIGN_LEFT, FontColumn,2));
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于抵充第一期租金金额: "+updateMon(paylist.get("PLEDGE_AVE_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于抵充第一期租金金额: "+updateMon(paylist.get("PLEDGE_AVE_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于平均抵冲金额: 0.00", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于平均抵冲金额: 0.00", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金	用于期末退还金额: "+updateMon(paylist.get("PLEDGE_BACK_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于期末退还金额: "+updateMon(paylist.get("PLEDGE_BACK_PRICE")), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金用于最后抵冲含税金额/期数: "+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"/"+paylist.get("PLEDGE_LAST_PERIOD"), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金用于最后抵冲含税金额/期数: "+updateMon(DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE"))+DataUtil.doubleUtil(paylist.get("PLEDGE_LAST_PRICE_TAX")))+"/"+paylist.get("PLEDGE_LAST_PERIOD"), PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				t++;
				if(t==39||(t-39)%38==0){
					tT2.addCell(makeCellSetColspanNoBorderTOP("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}
				else{
					tT2.addCell(makeCellSetColspanLRBorder("    保证金收入时间: "+"第"+paylist.get("PLEDGE_PERIOD")+"期", PdfPCell.ALIGN_LEFT, FontDefault,2));				
				}
				t++;			
				for(int x = 0;x<irrMonthPaylines.size();x++){
					if(t==39||(t-39)%38==0){
				    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					}else{
					    tT2.addCell(makeCellSetColspanLRBorder("       实际支付租金 ：第"+ (irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_START"))+"-"+(irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END")==null?"":irrMonthPaylines.get(x).get("IRR_MONTH_PRICE_END"))+"期每月RMB【"+ updateMoney((Map)(irrMonthPaylines.get(x)),"IRR_MONTH_PRICE" ,nfFSNum)+"】元" 
						 , PdfPCell.ALIGN_LEFT, FontDefault,2));
					    //t++;
			 		}
				}
				t=t+irrMonthPaylines.size();
				/*if(methodname.equals("expZulwToPdf")){*/
					tT2.addCell(makeCellSetColspanLRBorder("    支 付 期 数 ：【"+ DataUtil.intUtil(paylist.get("LEASE_PERIOD")) + "】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				/*}else{
					tT2.addCell(makeCellSetColspanLRBorder("    支付期数：【"+schemeMap.get("LEASE_PERIOD").toString()+"】期,每期应缴租金为含税实际支付租金。", PdfPCell.ALIGN_LEFT, FontDefault,2));
				}*/
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
				
				//add by ShenQi 插入系统日志
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 租赁物情况表",
			   		 	 "合同浏览导出 租赁物情况表",
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
	public void expCollectionZulwToPdf(Context context) {
		
		ArrayList booknotes=new ArrayList();
		
		Map CustomerMap = null;
		List<Map> equipmentsList = null;
		Map schemeMap = null;
		try{
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
			for (int i = 0; i < ids.length; i++) {
				context.contextMap.put("RECP_ID", ids[i]);
				HashMap booknote=new HashMap();		
				Map paylist = null;
				List<Map> paylines = null;
			
				paylist = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP);
				paylines = (List<Map>) DataAccessor.query("collectionManage.readPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);			
				List oldirrMonthPaylines=(List<Map>) DataAccessor.query("collectionManage.queryirrMonthpaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				paylist.put("paylines", paylines);
				paylist.put("oldirrMonthPaylines", oldirrMonthPaylines);
				StartPayService.packagePaylinesForMon(paylist);
				List<Map> irrMonthPaylines = new ArrayList<Map>();			
				irrMonthPaylines = (List<Map>) paylist.get("irrMonthPaylines");			
				equipmentsList = (List<Map>) DataAccessor.query("exportQuoToPdf.selectCollectionEquipment",context.contextMap, DataAccessor.RS_TYPE.LIST);
				CustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCollectionInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				String custTyp =CustomerMap.get("CUST_TYPE") +"";
	
				if(custTyp.equals("1")){	    
				    CustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCropInfo2", context.contextMap, DataAccessor.RS_TYPE.MAP);
				}
				if(CustomerMap  == null ){		    
				    CustomerMap = (Map) DataAccessor.query("exportQuoToPdf.queryCollectionInfo", context.contextMap, DataAccessor.RS_TYPE.MAP);
				}
				schemeMap = (Map) DataAccessor.query("exportQuoToPdf.selectCollectionScheme",context.contextMap, DataAccessor.RS_TYPE.MAP);	
				booknote.put("paylist", paylist);
				booknote.put("irrMonthPaylines", irrMonthPaylines);
				booknote.put("equipmentsList", equipmentsList);
				booknote.put("creditCustomerMap", CustomerMap);
				booknote.put("schemeMap", schemeMap);
				booknote.put("monthPaylines", this.ExpectMonthPrice(Double.parseDouble(paylist.get("PLEDGE_AVE_PRICE")+""),oldirrMonthPaylines));
				booknotes.add(booknote);
				context.contextMap.put("booknotes", booknotes);
						
		   	}
			this.expZulwPdfModel(context,"expCollectionZulwToPdf");	
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
				if(i == irrMonthPaylines.size() - 1  && eachAVE != endAVE){
					if(valueAddedTax!=lastValueAddedTax){
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
   
	private PdfPCell makeCell(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(align);
		objCell.setBorderWidthBottom(0.125f);
		objCell.setBorderWidthRight(0);
		objCell.setBorderWidthTop(0.125f);
		objCell.setBorderWidthLeft(0.125f);
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
	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorder(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(20);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}
	
	/** 创建 无边框 单元格 */
	private PdfPCell makeCellWithNoBorderForBJD(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}	
	/** 创建 无边框 单元格 左边填充*/
	private PdfPCell makeCellWithNoBorder2(String content, int align, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(20);
		objCell.setPaddingLeft(400);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		return objCell;
	}	
	/** 创建 无左右上边框 合并 单元格 */
	private PdfPCell makeCellSetColspanBTBorder(String content, int align, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setFixedHeight(20);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);
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
	/** 创建 只有左右边框 单元格 */
	private PdfPCell makeCellWithBorderP(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setFixedHeight(20);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	/** 创建 只有左边框 单元格 */
	private PdfPCell makeCellWithBorderLeft(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthLeft(1);
	   
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
	    return objCell;
	}
	/** 创建 只有右边框 单元格 */
	private PdfPCell makeCellWithBorderRight(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthLeft(0);
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
//	    objCell.setFixedHeight(20*auto);
	    objCell.setBorderWidthLeft(0);
	    return objCell;
	}
	
	/** 创建 没有顶边 单元格 */
	private PdfPCell makeCellOnlyBottom(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 没有顶边 (左边)单元格 */
	private PdfPCell makeCellLeftBottom(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(1f);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
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
	/** 创建 无边框 合并 单元格 */
	private PdfPCell makeCellSetColspanWithNoBorder(String content, int align, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(align);
		objCell.setVerticalAlignment(align);
		objCell.setBorder(0);
		objCell.setColspan(colspan);
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
	
	/** 创建 无上下边框 合并 单元格 */
	private PdfPCell makeCellSetColspanLRBorderAuto(String content, int align, Font FontDefault,int colspan,int auto) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setFixedHeight(20*auto);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(1);
	    objCell.setBorderWidthRight(1);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
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

    /** 创建 有边框 合并 单元格
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2(String content, int align, Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthRight(0);
	objCell.setBorderWidthTop(0);
	objCell.setFixedHeight(20);
	return objCell;
    }
    private PdfPCell makeCellSetColspan2VS1(String content, int align, Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthLeft(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setBorderWidthTop(0);
    	objCell.setFixedHeight(15);
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
    
    private PdfPCell makeCellSetColspan2ForZLWFoot(String content, int align, Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(1);
    	objCell.setBorderWidthLeft(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setBorderWidthTop(0);
    	objCell.setFixedHeight(1);
    	return objCell;
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
    /** 创建 有边框 合并 单元格
     *  无下右边
     *  
     *  */
    private PdfPCell makeCellSetColspan22(String content, int align,Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthRight(0.25f);
	objCell.setBorderWidthBottom(0.25f);
	objCell.setBorderWidthLeft(1);
	objCell.setBorderWidthTop(1);
	objCell.setFixedHeight(20); 
	return objCell;
    }	
    
    /** 创建 有边框 合并 单元格
     *  无左下边
     *  
     *  */
    private PdfPCell makeCellSetColspan22VS1(String content, int align,Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0.25f);
	objCell.setBorderWidthLeft(0.25f);
	objCell.setBorderWidthRight(1);
	objCell.setBorderWidthTop(1);
	objCell.setFixedHeight(20);
	return objCell;
    }    
    /** 创建 有边框 合并 单元格
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan33(String content, int align,Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthLeft(1);
	objCell.setBorderWidthBottom(0.125f);
	objCell.setBorderWidthRight(0.125f);
	objCell.setBorderWidthTop(0.125f);
	objCell.setFixedHeight(20);
	return objCell;
    }    
    private PdfPCell makeCellSetColspan33VS1(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthRight(1);
    	objCell.setBorderWidthBottom(0.125f);
    	objCell.setBorderWidthLeft(0.125f);
    	objCell.setBorderWidthTop(0.125f);
    	objCell.setFixedHeight(20);
    	return objCell;
        } 
    private PdfPCell makeCellSetColspan33VS3(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(1);
    	objCell.setBorderWidthBottom(0.125f);
    	objCell.setBorderWidthRight(0.125f);
    	objCell.setBorderWidthTop(0.125f);
    	objCell.setFixedHeight(30);
    	return objCell;
        }     
    private PdfPCell makeCellSetColspanNoBott(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(1);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthRight(0.125f);
    	objCell.setBorderWidthTop(0);
    	objCell.setFixedHeight(20);
    	return objCell;
        } 
    private PdfPCell makeCellSetColspanNoBottForAuto(String content, int align,Font FontDefault, int colspan,int auto) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(1);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthRight(0.125f);
    	objCell.setBorderWidthTop(0);
    	objCell.setFixedHeight(auto);
    	return objCell;
        }    
        private PdfPCell makeCellSetColspanNoBottVS1(String content, int align,Font FontDefault, int colspan) {
        	Phrase objPhase = new Phrase(content, FontDefault);
        	PdfPCell objCell = new PdfPCell(objPhase);
        	objCell.setHorizontalAlignment(align);
        	objCell.setVerticalAlignment(align);
        	objCell.setColspan(colspan);
        	objCell.setBorderWidthRight(1);
        	objCell.setBorderWidthBottom(0);
        	objCell.setBorderWidthLeft(0.125f);
        	objCell.setBorderWidthTop(0);
        	objCell.setFixedHeight(20);
        	return objCell;
            }   
        private PdfPCell makeCellSetColspanNoBottVS2(String content, int align,Font FontDefault, int colspan) {
        	Phrase objPhase = new Phrase(content, FontDefault);
        	PdfPCell objCell = new PdfPCell(objPhase);
        	objCell.setHorizontalAlignment(align);
        	objCell.setVerticalAlignment(align);
        	objCell.setColspan(colspan);
        	objCell.setBorderWidthRight(0);
        	objCell.setBorderWidthBottom(0);
        	objCell.setBorderWidthLeft(0.125f);
        	objCell.setBorderWidthTop(0);
        	objCell.setFixedHeight(20);
        	return objCell;
            }          
    private PdfPCell makeCellSetColspan333(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(1);
    	objCell.setBorderWidthBottom(0.25f);
    	objCell.setBorderWidthRight(0.25f);
    	objCell.setBorderWidthTop(0.25f);
    	objCell.setFixedHeight(50);
    	return objCell;
        }    
        private PdfPCell makeCellSetColspan333VS1(String content, int align,Font FontDefault, int colspan) {
        	Phrase objPhase = new Phrase(content, FontDefault);
        	PdfPCell objCell = new PdfPCell(objPhase);
        	objCell.setHorizontalAlignment(align);
        	objCell.setVerticalAlignment(align);
        	objCell.setColspan(colspan);
        	objCell.setBorderWidthRight(1);
        	objCell.setBorderWidthBottom(0);
        	objCell.setBorderWidthLeft(0.125f);
        	objCell.setBorderWidthTop(0.125f);
        	objCell.setFixedHeight(50);
        	return objCell;
            }  
        
        private PdfPCell makeCellSetColspanForAuto(String content, int align,Font FontDefault, int colspan,int auto) {
        	Phrase objPhase = new Phrase(content, FontDefault);
        	PdfPCell objCell = new PdfPCell(objPhase);
        	objCell.setHorizontalAlignment(align);
        	objCell.setVerticalAlignment(align);
        	objCell.setColspan(colspan);
        	objCell.setBorderWidthLeft(1);
        	objCell.setBorderWidthBottom(0.25f);
        	objCell.setBorderWidthRight(0.25f);
        	objCell.setBorderWidthTop(0.25f);
        	objCell.setFixedHeight(auto);
        	return objCell;
            }    
            private PdfPCell makeCellSetColspanForAutoVS1(String content, int align,Font FontDefault, int colspan,int auto) {
            	Phrase objPhase = new Phrase(content, FontDefault);
            	PdfPCell objCell = new PdfPCell(objPhase);
            	objCell.setHorizontalAlignment(align);
            	objCell.setVerticalAlignment(align);
            	objCell.setColspan(colspan);
            	objCell.setBorderWidthRight(1);
            	objCell.setBorderWidthBottom(0);
            	objCell.setBorderWidthLeft(0.125f);
            	objCell.setBorderWidthTop(0.125f);
            	objCell.setFixedHeight(auto);
            	return objCell;
                }        
    private PdfPCell makeCellSetColspan33VS2(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthRight(0.125f);
    	objCell.setBorderWidthBottom(0.25f);
    	objCell.setBorderWidthLeft(0.25f);
    	objCell.setBorderWidthTop(0.25f);
    	objCell.setFixedHeight(20);
    	return objCell;
        }    
    /** 创建 有边框 合并 单元格
     *  无上边
     *  
     *  */
    private PdfPCell makeCellSetColspan44(String content, int align,Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(1);
	objCell.setBorderWidthRight(0.25f);
	objCell.setBorderWidthBottom(1);
	objCell.setFixedHeight(150);
	return objCell;
    }	
    private PdfPCell makeCellSetColspan44VS1(String content, int align,Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthTop(0);
    	objCell.setBorderWidthLeft(0.25f);
    	objCell.setBorderWidthRight(1);
    	objCell.setBorderWidthBottom(1);
    	objCell.setFixedHeight(150);
    	return objCell;
        }		
}