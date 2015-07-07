package com.brick.backMoney.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.collection.service.StartPayService;
import com.brick.credit.service.ExportQuoToPdf;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.CurrencyConverter;
import com.brick.util.DataUtil;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class backMoneyToPdf extends AService
{
	Log logger = LogFactory.getLog(backMoneyToPdf.class);
	
	//导出设备清单
	@SuppressWarnings("unchecked")
	public void expExcelEqmts(Context context){
		//System.out.println("--------->exportEqmtList");
		SqlMapClient client=null;
		try {
			client = DataAccessor.getSession();
			client.startTransaction();
			
			List<Map> content=client.queryForList("payMoney.expExcelEqmts", context.getContextMap());
			
			ByteArrayOutputStream baos=exportEqmtModel("设备清单",content);
			
			context.response.setContentType("application/vnd.ms-excel;charset=utf-8");
			context.response.setHeader("Content-Disposition", "attachment;filename=equipment.xls");
			
			ServletOutputStream os=context.getResponse().getOutputStream();
			baos.writeTo(os);
			os.flush();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}finally{
			try {
				client.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream exportEqmtModel(String name,List content){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		WorkbookSettings setting=new WorkbookSettings();
		
		/*
		 * Add by Michael 2011 12/19 For 客服增加设备总金额与保险总金额栏位
		 */
		Double eqmt_TOTAL=0.0;
		Double insu_TOTAL=0.0;
		
		try{
			WritableWorkbook workbook=Workbook.createWorkbook(baos, setting);
			/* 解决中文乱码 */
			setting.setEncoding("ISO-8859-1");
			
			WritableSheet sheet=workbook.createSheet(name,1);
			
			WritableFont font1=new WritableFont(WritableFont.createFont("宋体"),11,WritableFont.BOLD);
			WritableCellFormat format1=new WritableCellFormat(font1);
			format1.setAlignment(Alignment.CENTRE);
			format1.setVerticalAlignment(VerticalAlignment.CENTRE);
			format1.setWrap(true);
			
			// 表格部分 字体 宋体10号 水平居左对齐 垂直居中对齐 带边框 自动换行
			WritableFont font3 = new WritableFont(
					WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
			WritableCellFormat format3 = new WritableCellFormat(font3);
			format3.setAlignment(Alignment.CENTRE);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			format3.setWrap(true);

			// 设置列宽
			sheet.setColumnView(0, 10);
			sheet.setColumnView(1, 30);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 30);
			sheet.setColumnView(4, 15);
			sheet.setColumnView(5, 20);
			sheet.setColumnView(6, 30);
			sheet.setColumnView(7, 10);
			sheet.setColumnView(8, 15);
			sheet.setColumnView(9, 10);
			
			
			Label cell = null;
			
			cell=new Label(0,0,"序号",format1);
			sheet.addCell(cell);
			cell=new Label(1,0,"投保日期",format1);
			sheet.addCell(cell);
			cell=new Label(2,0,"合约编号",format1);
			sheet.addCell(cell);
			cell=new Label(3,0,"承租人名称",format1);
//--------Marked by Michael 2011 12/15 For 客服导出设备明细时，By保单号跑出设备汇总表，跑出总金额--------------
//			sheet.addCell(cell);
//			cell=new Label(4,0,"保单金额",format1);
//			sheet.addCell(cell);
//			cell=new Label(5,0,"保险单号",format1);
//			sheet.addCell(cell);
//			cell=new Label(6,0,"保险项目",format1);
//			sheet.addCell(cell);
//			cell=new Label(7,0,"数量",format1);
//			sheet.addCell(cell);
//			cell=new Label(8,0,"保险费",format1);
//			sheet.addCell(cell);
//			cell=new Label(9,0,"费率(‰)",format1);
//-------------------------------------------------------------------------------------------------
//--------Add by Michael 2011 12/15 For 客服导出设备明细时，By保单号跑出设备汇总表，跑出总金额--------------	
			sheet.addCell(cell);
			cell=new Label(4,0,"保险单号",format1);
			sheet.addCell(cell);
			cell=new Label(5,0,"保险项目",format1);
			sheet.addCell(cell);
			cell=new Label(6,0,"数量",format1);
			sheet.addCell(cell);
			cell=new Label(7,0,"设备金额",format1);
			sheet.addCell(cell);
			cell=new Label(8,0,"保单金额",format1);
			sheet.addCell(cell);
			cell=new Label(9,0,"费率(‰)",format1);
			sheet.addCell(cell);

			int i=1;
			for(Iterator iterator=content.iterator();iterator.hasNext();){
				Map contentMap=(Map)iterator.next();
				//序号
				cell = new Label(0, i,i+"", format3);
				sheet.addCell(cell);
				
				Object obj=null;
				//投保日期
				obj=contentMap.get("INSU_START_DATE");
				if(obj==null)obj=" ";
				String date=obj.toString()+"至";
				obj=contentMap.get("INSU_END_DATE");
				if(obj==null)obj=" ";
				date+=obj.toString();
				
				cell = new Label(1, i,date, format3);
				sheet.addCell(cell);
				
				//合约编号
				obj=contentMap.get("LEASE_CODE");
				if(obj==null)obj="";
				cell = new Label(2, i,obj.toString(), format3);
				sheet.addCell(cell);
				
				//承租人名称
				obj=contentMap.get("CUST_NAME");
				if(obj==null)obj="";
				cell = new Label(3, i,obj.toString(), format3);
				sheet.addCell(cell);

				//----Add By Michael 2011 12/15 For 客服导出设备明细时，By保单号跑出设备汇总表，跑出总金额--------------	
				//保险单号
				obj=contentMap.get("INCU_CODE");
				if(obj==null)obj="";
				cell = new Label(4, i,obj.toString(), format3);
				sheet.addCell(cell);
				
				//保险项目
				obj=contentMap.get("THING_NAME");
				if(obj==null)obj="";
				cell = new Label(5, i,obj.toString(), format3);
				sheet.addCell(cell);
				
				//数量
				obj=contentMap.get("NUM");
				if(obj==null)obj="";
				cell = new Label(6, i,obj.toString(), format3);
				sheet.addCell(cell);
				
				//设备金额
				obj=contentMap.get("INSU_PRICE_TOTAL");
				if(obj==null)obj="";
				cell = new Label(7, i,obj.toString(), format3);
				sheet.addCell(cell);
				//Add By Michael 2011 12/19 设备总金额
				eqmt_TOTAL+=(Double)obj;
				
				
				//保单金额
				obj=contentMap.get("INSU_PRICE");
				if(obj==null)obj="";
				cell = new Label(8, i,String.valueOf(((BigDecimal)obj).doubleValue()), format3);
				sheet.addCell(cell);
				//Add By Michael 2011 12/19 保单总金额
				insu_TOTAL+=((BigDecimal)obj).doubleValue();
				//费率
				obj=contentMap.get("INSU_RATE");
				if(obj==null)obj="";
				cell = new Label(9, i,obj.toString(), format3);
				sheet.addCell(cell);
//--------------------------------------------------------------------------------------------------
//----------------Marked by Michael 2011 12/15------------------------------------------------------
				//保险单号
//				obj=contentMap.get("INCU_CODE");
//				if(obj==null)obj="";
//				cell = new Label(5, i,obj.toString(), format3);
//				sheet.addCell(cell);
//				
//				//保险项目
//				obj=contentMap.get("THING_NAME");
//				if(obj==null)obj="";
//				cell = new Label(6, i,obj.toString(), format3);
//				sheet.addCell(cell);
//	
//				//数量
//				obj=contentMap.get("AMOUNT");
//				if(obj==null)obj=" ";
//				String num=obj.toString();
//				obj=contentMap.get("UNIT");
//				if(obj==null)
//					obj=" ";
//				else
//					num+="("+obj.toString()+")";
//				cell = new Label(7, i,num, format3);
//				sheet.addCell(cell);
//	
//				//保险费
//				obj=contentMap.get("UNIT_INSU_PRICE");
//				BigDecimal big=null;
//				if(obj==null)obj="";
//				else{
//					big=new BigDecimal(Double.parseDouble(obj.toString()));
//					big=big.setScale(2,BigDecimal.ROUND_HALF_UP);
//				}
//				cell = new Label(8, i,big.toString(), format3);
//				sheet.addCell(cell);
//	
//				//费率
//				obj=contentMap.get("RATE");
//				if(obj==null)obj="";
//				cell = new Label(9, i,obj.toString(), format3);
//				sheet.addCell(cell);
//----------------------------------------------------------------------------------------------------------------------				
				i++;
			}

			/*
			 * Add by Michael 2011 12/19 For 客服增加设备总金额与保险总金额栏位
			 */
			cell = new Label(6, i,"合计金额：", format3);
			sheet.addCell(cell);
			cell = new Label(7, i,new DecimalFormat("#.00").format(eqmt_TOTAL).toString(), format3);
			sheet.addCell(cell);
			cell = new Label(8, i,new DecimalFormat("#.00").format(insu_TOTAL).toString(), format3);
			sheet.addCell(cell);
			
			workbook.write();
			workbook.close();
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

	
	
	@SuppressWarnings("unchecked")
	//保险费导出
	public void expPdf3(Context context)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//DateFormat format=SimpleDateFormat.getDateInstance();
		Map payMoney=null;
		
		try {
			payMoney = (Map) DataAccessor.query("payMoney.payMoneyManagerInsurancePdf", context.contextMap,DataAccessor.RS_TYPE.MAP);

			Object APPLICATION_DATE=payMoney.get("APPLICATION_DATE");//申请日期
			Object PAY_DATE=payMoney.get("PAY_DATE");//实际支付日期
			Object BACKCOMP=payMoney.get("BACKCOMP");//厂商名称
			Object BANK_NAME=payMoney.get("BANK_NAME");//开户行
			Object BANK_ACCOUNT=payMoney.get("BANK_ACCOUNT");//开户账号
			Object PAYCOUNT=payMoney.get("PAYCOUNT");//金额
			Object PAY_WAY=payMoney.get("PAY_WAY");//付款方式
			Object BACKSTATE=payMoney.get("BACKSTATE");//付款状态
			Object FLAG=payMoney.get("FLAG");//付款方式
			
			
			String backState=null;
			if(BACKSTATE!=null && !"".equals(BACKSTATE))
			{
				backState=BACKSTATE.toString();
			}
			
			@SuppressWarnings("unused")
			String payway=null;
			if(PAY_WAY!=null && !"".equals(PAY_WAY))
			{
				payway=PAY_WAY.toString();
			}
			
			String flag=null;
			if(FLAG!=null && !"".equals(FLAG))
			{
				flag=FLAG.toString();
			}
			
			String applicationdate=null;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
			
			if(APPLICATION_DATE!=null && !"".equals(APPLICATION_DATE))
			{
				applicationdate=sf.format(APPLICATION_DATE);
			}
			else
			{
				applicationdate="  年   月   日";
			}
			
			String paydate=null;
			if(PAY_DATE!=null && !"".equals(PAY_DATE))
			{
				paydate=sf.format(PAY_DATE);
			}
			else
			{
				paydate="";
			}
			
			String backComps=null;
			if(BACKCOMP!=null && !"".equals(BACKCOMP))
			{
				backComps=BACKCOMP.toString();
			}
			
			String bankName=null;
			if(BANK_NAME!=null && !"".equals(BANK_NAME))
			{
				bankName=BANK_NAME.toString();
			}
			
			String bankAccount=null;
			if(BANK_ACCOUNT!=null && !"".equals(BANK_ACCOUNT))
			{
				bankAccount=BANK_ACCOUNT.toString();
			}
			
			String payCounts=null;
			String payCountss=null;
			if(PAYCOUNT!=null && !"".equals(PAYCOUNT))
			{
				payCountss=PAYCOUNT.toString();
				payCounts=this.updateMon(PAYCOUNT.toString());
			}
			
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontTitle= new Font(bfChinese,17,Font.BOLD);
			Font FontBold = new Font(bfChinese, 10, Font.BOLD);
			Font FontDefault = new Font(bfChinese,10, Font.NORMAL);
			Font FontSmall = new Font(bfChinese,8, Font.NORMAL);
			Font FontSmall2 = new Font(bfChinese,5, Font.NORMAL);
			
			Rectangle rect=new Rectangle(PageSize.A4);
			Document doc=new Document(rect,5,5,20,20);

			PdfWriter.getInstance(doc,baos);
			PdfPCell cell=null;
			
			doc.open();			
			
			@SuppressWarnings("unused")
			float[] width={6f,8f,8f,7f,7f,12f,12f,8f,8f,8f,8f,8f};
			PdfPTable t1=new PdfPTable(12);
			
			PdfPTable tlogo=new PdfPTable(new float[]{5f,95f});
			String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
			Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");

			image.scaleAbsoluteHeight(20);
			image.scaleAbsoluteWidth(20);			
			
			cell=new PdfPCell();
			cell.addElement(image);
			cell.setBorder(0);
			tlogo.addCell(cell);
			//Modify By michael 2011 12/16 纠正公司Title错误
			//Chunk chunk1=new Chunk("融资租赁(苏州)有限公司",FontSmall);
			String payId =(String) context.contextMap.get("payMoneyId");
			//int creditId = LeaseUtil.getCreditIdByPayId(payId);
			//String contractType = LeaseUtil.getContractTypeByCreditId(String.valueOf(creditId));
			int companyCode = LeaseUtil.getCompanyCodeByPayId(payId); //LeaseUtil.getCompanyCodeByCreditId(String.valueOf(creditId));
			String companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
			String companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(companyCode);
			/*if("7".equals(contractType)){
				companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
				companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(companyCode);
			}*/
			Chunk chunk1=new Chunk(companyName,FontSmall);
			Chunk chunk2=new Chunk(companyEngName,FontSmall2);
			
			cell=new PdfPCell();
			cell.addElement(chunk1);
			cell.addElement(chunk2);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			tlogo.addCell(cell);
			
			
			
			cell=new PdfPCell(tlogo);
			cell.setColspan(12);
			cell.setPaddingBottom(5);
			cell.setBorder(0);
			t1.addCell(cell);
			
			cell=makeCellNoBorderSetColspan(" ",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,new Font(bfChinese, 14, Font.BOLD),12);
			t1.addCell(cell);
			
			//Modify By Michael 2011 12/16 公司Title错误
			//cell=makeCellNoBorderSetColspan("融资租赁(苏州)有限公司",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,12);
			cell=makeCellNoBorderSetColspan(companyName,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,12);
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setFixedHeight(21);
			t1.addCell(cell);
			
			
			cell=makeCellNoBorderSetColspan("请   款   单",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,new Font(bfChinese, 17, Font.BOLD),12);
			t1.addCell(cell);
			
			cell=makeCellNoBorderSetColspan(" ",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,new Font(bfChinese, 17, Font.BOLD),12);
			t1.addCell(cell);
			
			//第一行
			if(backState.equals("2"))
			{
				context.contextMap.put("backComp", backComps);
				
				Map comp = (Map) DataAccessor.query("rentContract.userDecpName", context.contextMap,DataAccessor.RS_TYPE.MAP);
				Object compName=null;
				if(comp!=null)
				{
					if(comp.size()>0)
					{
						compName=comp.get("DEPT_NAME");
					}
				}
				
				if(compName!=null && !"".equals(compName))
				{
					t1.addCell(makeCellWithLTBorderSetColspan("部门：  "+compName.toString(), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
				}
				else
				{
					t1.addCell(makeCellWithLTBorderSetColspan("部门：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
				}
			}
			else
			{
				t1.addCell(makeCellWithLTBorderSetColspan("部门：  	业管处", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			
			
			t1.addCell(makeCellWithLRTBorderSetColspan("日期:     "+applicationdate, PdfCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			
			//第二行
			t1.addCell(makeCellWithLTBorderSetColspan(" 说 明 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" 金额 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan(" 备注 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(backState.equals("2"))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan(" 员工资料 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan(" 厂商资料 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			
			
			//第三行
			if(backState.equals("2"))
			{
				t1.addCell(makeCellWithLTBorderSetColspan(" 奖金 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			}
			else
			{
				t1.addCell(makeCellWithLTBorderSetColspan(" 保险费 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			}
			
			t1.addCell(makeCellWithLTBorderSetColspan(" "+payCounts, PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(backState.equals("2"))
			{
				t1.addCell(makeCellWithLTBorderSetColspan(" 员工姓名 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			}
			else
			{
				t1.addCell(makeCellWithLTBorderSetColspan(" 厂商名称 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			}
			if(backComps!=null && !"".equals(backComps))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  "+backComps, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			
			
			//第行四
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspan(" 开户银行 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			//t1.addCell(makeCellWithLRTBorderSetColspan("  "+bankName, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			if(bankName!=null && !"".equals(bankName))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  "+bankName, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			
			//第行五
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspan(" 银行账号 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			//t1.addCell(makeCellWithLRTBorderSetColspan("  "+bankAccount, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			if(bankAccount!=null && !"".equals(bankAccount))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  "+bankAccount, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			}
			
			//第行六
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspan(" 付款条件 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			t1.addCell(makeCellWithLRTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			
			//第行七
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspan(" 付款日期 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			t1.addCell(makeCellWithLRTBorderSetColspan("  "+paydate, PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,4));
			
			//第八行
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLRTBorderSetColspan(" 付款方式 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			
			//第九行
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(flag.equals("支票转账") || flag.equals("转款支票"))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("[√] 转款支票 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("[ ] 转款支票 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			
			
			//第十行
			t1.addCell(makeCellWithLTBorderSetColspan(" 合计 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan("  "+payCounts, PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLRTBorderSetColspan("[ ] 电汇 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			
			//第十一行
			t1.addCell(makeCellWithLTBorderSetColspan(" 暂借款单号 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(flag.equals("网银付款") || flag.equals("网银"))
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("[√] 网银 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			else
			{
				t1.addCell(makeCellWithLRTBorderSetColspan("[ ]  网银 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
			}
			
			
			//第行十二
			t1.addCell(makeCellWithLTBorderSetColspan(" 应补金额 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(flag.equals("现金")){
				t1.addCell(makeCellWithLTBorderSetColspan("[√] 现金 ", PdfCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			} else {
				t1.addCell(makeCellWithLTBorderSetColspan("[ ] 现金 ", PdfCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			}
			
			t1.addCell(makeCellWithLRTBorderSetColspans(" 签领 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			
			//第行十三
			t1.addCell(makeCellWithLTBorderSetColspan(" 应退金额 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			if(flag.equals("现金支票")){
				t1.addCell(makeCellWithLTBorderSetColspan("[√] 现金支票 ", PdfCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			} else {
				t1.addCell(makeCellWithLTBorderSetColspan("[ ] 现金支票 ", PdfCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			}
			t1.addCell(makeCellWithLRTBorderSetColspans(" 签领 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			
			//第行十四
			t1.addCell(makeCellWithLTBorderSetColspan(" 大写金额 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
			
			t1.addCell(makeCellWithLRTBorderSetColspan("  "+CurrencyConverter.toUpper(payCountss), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,10));
			
			//第行十五
			t1.addCell(makeCellWithLTBBorderSetColspans(" 请 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspans(" 总经理 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			if(companyCode ==2){
				t1.addCell(makeCellWithLTBBorderSetColspans(" 经管处/\n管理部 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));					
			}else{
				t1.addCell(makeCellWithLTBBorderSetColspans(" 经管处/\n财管部 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));					
			}
		
			t1.addCell(makeCellWithLTBBorderSetColspans(" 请款部门 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			if(companyCode ==2){
				t1.addCell(makeCellWithLRTBorderSetColspan(" 管理部 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));				
			}else{
				t1.addCell(makeCellWithLRTBorderSetColspan(" 财管部 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));		
			}
		
			//第行十六
			t1.addCell(makeCellWithLTBBorderSetColspanss(" 款 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspan("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBBorderSetColspan(" 处级主管 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspan(" 部主管 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBBorderSetColspan(" 请款人 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspan(" 主管 ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBBorderSetColspan(" 会计 ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLRTBorderSetColspan(" 出纳 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			
			//第行十七
			t1.addCell(makeCellWithLTBBorderSetColspanss(" 流 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBorderSetColspanleft("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLRTBorderSetColspan("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			
			//第行十八
			t1.addCell(makeCellWithLTBBorderSetColspansss(" 程 ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 2));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 1));
			t1.addCell(makeCellWithLTBBorderSetColspansss("  ", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			t1.addCell(makeCellWithLRTBorderSetColspanslbr("  ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,1));
			
			doc.add(t1);
			doc.close();
			String fileName=(String)context.getContextMap().get("payMoneyId");
			HttpServletResponse response=context.getResponse();
			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition","attachment; filename="+fileName);			
			ServletOutputStream os=response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();
		}catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	
	//保证金导出
	@SuppressWarnings("unchecked")
	public void expPdf2(Context context){
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	DateFormat format=SimpleDateFormat.getDateInstance();
	Map payMoney=null;
	List<Map> psTypeList=null;
	Map payDetail=null;
	List payDw=null;
	try {
		payMoney = (Map) DataAccessor.query("payMoney.payMoneyManager", context.contextMap,DataAccessor.RS_TYPE.MAP);
		psTypeList = (List<Map>) DataAccessor.query("dataDictionary.queryDataDictionary", context.contextMap, DataAccessor.RS_TYPE.LIST);
		
		payDetail = (Map) DataAccessor.query("rentContract.queryPayById", context.contextMap,DataAccessor.RS_TYPE.MAP);
		payDw= (List) DataAccessor.query("rentContract.payMoneyBankManagerByRECTIDByMargin", context.contextMap,DataAccessor.RS_TYPE.LIST);
		
		//加入管理费收入和非管理费收入 add by ShenQi
		List feeListRZE=null;
		context.contextMap.put("credit_id", context.contextMap.get("CREDIT_ID"));
		String creditId = StringUtils.isEmpty(context.contextMap.get("CREDIT_ID")) ? "" : String.valueOf(context.contextMap.get("CREDIT_ID"));
		feeListRZE = (List) DataAccessor.query("creditReportManage.getCreditFeeListRZE",context.contextMap, DataAccessor.RS_TYPE.LIST);
		List feeList=null;
		feeList = (List) DataAccessor.query("creditReportManage.getCreditFeeList",context.contextMap, DataAccessor.RS_TYPE.LIST);
		
		// 字体设置
		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
		Font FontTitle= new Font(bfChinese,17,Font.BOLD);
		Font FontBold = new Font(bfChinese, 10, Font.BOLD);
		Font FontDefault = new Font(bfChinese,10, Font.NORMAL);
		Font FontSmall = new Font(bfChinese,10, Font.NORMAL);
		Font FontSmall2 = new Font(bfChinese,5, Font.NORMAL);
		
		Rectangle rect=new Rectangle(PageSize.A4);
		Document doc=new Document(rect,5,5,20,20);

		PdfWriter.getInstance(doc,baos);
		PdfPCell cell=null;
		
		doc.open();			
		
		float[] width={14f,13f,7f,15f,13f,20f,20f};
		PdfPTable t1=new PdfPTable(width);
		
		PdfPTable tlogo=new PdfPTable(new float[]{5f,95f});
		String  imageUrl=ExportQuoToPdf.class.getResource("/").toString();//Class文件所在路径			
		Image image = Image.getInstance(imageUrl.substring(6,imageUrl.length()-16)+"images/yrlogo.png");
		
		image.scaleAbsoluteHeight(20);
		image.scaleAbsoluteWidth(20);			
		
		cell=new PdfPCell();
		cell.addElement(image);
		cell.setBorder(0);
		tlogo.addCell(cell);
		//Modify By Michael 2011 12/16 公司Title错误
		//Chunk chunk1=new Chunk("融资租赁(苏州)有限公司",FontSmall);
		
		String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
		String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
		String companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(1);
		int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
		if("7".equals(contractType)){
			companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
			companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(companyCode);
		}
		Chunk chunk1=new Chunk(companyName,FontSmall);
		Chunk chunk2=new Chunk(companyEngName,FontSmall2);
		cell=new PdfPCell();
		cell.addElement(chunk1);
		cell.addElement(chunk2);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setBorder(0);
		tlogo.addCell(cell);
		
		cell=new PdfPCell(tlogo);
		cell.setColspan(7);
		cell.setPaddingBottom(5);
		cell.setBorder(0);
		t1.addCell(cell);
		
		cell=makeCellNoBorderSetColspan(" ",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontBold,7);
		t1.addCell(cell);
		//Modify By Michael 2011 12/16 公司Title错误
		//cell=makeCellNoBorderSetColspan("融资租赁(苏州)有限公司",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,7);
		cell=makeCellNoBorderSetColspan(companyName,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_BOTTOM, FontTitle,7);
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		cell.setFixedHeight(21);
		t1.addCell(cell);
		
		@SuppressWarnings("unused")
		int backState=0;
        if(payMoney.get("BACKSTATE")!=null && !"".equals(payMoney.get("BACKSTATE")))
        {
        	backState=Integer.parseInt(payMoney.get("BACKSTATE").toString());//0：设备款，1：保证金拨款，:2：奖金拨款，3：保险费,4：法务费用
        } 
		cell=makeCellNoBorderSetColspan("租赁合同<保证金>付款凭证",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,new Font(bfChinese, 16, Font.BOLD),7);
		t1.addCell(cell);
		
		cell=makeCellNoBorderSetColspan(" ",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,new Font(bfChinese, 13, Font.BOLD),7);
		cell.setFixedHeight(14);
		t1.addCell(cell);
		
		t1.addCell(makeCellNoBorderSetColspan("预定支付日：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("申请日期："+payMoney.get("APPLICATION_DATE")==null?"":payMoney.get("APPLICATION_DATE").toString().substring(0,10), PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_MIDDLE, FontDefault,6));
		
//		cell=makeCellNoBorderSetColspan("申请日期：",PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_MIDDLE, FontSmall,6);
//		cell.setFixedHeight(14);
//		t1.addCell(cell);
//		
//		cell=makeCellNoBorderSetColspan(payMoney.get("APPLICATION_DATE").toString(),PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontSmall,1);
//		cell.setPaddingRight(40);
//		cell.setFixedHeight(14);
//		t1.addCell(cell);

		
		//第一行
		t1.addCell(makeCellWithLTBorder("合同号", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		t1.addCell(makeCellWithLTBorderSetColspan(payMoney.get("LEASE_CODE").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontDefault,2));
		t1.addCell(makeCellWithLTBorder("承租人", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		t1.addCell(makeCellWithLRTBorderSetColspan(payMoney.get("CUST_NAME").toString(), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault,3));
		//第二行
		t1.addCell(makeCellWithLTBorder("购买金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLTBorderSetColspan(payMoney.get("LEASE_TOPRIC1")==null?"￥0.0":updateMoney(payMoney.get("LEASE_TOPRIC1").toString()),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		t1.addCell(makeCellWithLTBorder("其他支出", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLRTBorderSetColspan("￥0.00", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall,2));
		//第二行
		double isPay=0.0;
		if(payDw.size()>0){
			for(int i=0;i<payDw.size();i++){
				HashMap payDwMap=(HashMap)payDw.get(i);
				isPay+=Double.parseDouble(payDwMap.get("PAY_MONEY")==null?"0.00":payDwMap.get("PAY_MONEY").toString());
			}
		}
		t1.addCell(makeCellWithLTBorder("应付总额          （成本）", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLTBorderSetColspan(payMoney.get("LEASE_TOPRIC")==null?"￥0.00":updateMoney(payMoney.get("LEASE_TOPRIC").toString()),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		t1.addCell(makeCellWithLTBorder("已付预付款", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLRTBorderSetColspan(updateMoney(isPay+""), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall,2));
		
		//----------------------------------付款凭证pdf加入新的栏位add by ShenQi 
//		t1.addCell(makeCellWithLTBorder("本次付款额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
//		t1.addCell(makeCellWithLTBorderSetColspan(payDetail.get("PAY_MONEY")==null?"￥0.00":updateMoney(payDetail.get("PAY_MONEY").toString()),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		
		t1.addCell(makeCellWithLTBorder("入账金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		//如我公司金额
		String pay=String.valueOf(payMoney.get("PLEDGE_ENTER_CMPRICE"));
		String tax=String.valueOf(payMoney.get("PLEDGE_ENTER_CMRATE"));
		if(pay==null) {
			pay="0.0";
		}
		if(tax==null) {
			tax="0.0";
		}
		double cost=Double.parseDouble(pay)+Double.parseDouble(tax);
		
		//Add by Michael 2012 4-26 For 李芳修改保证金凭证
		//我司入供应商保证金+税金
		String agpay=String.valueOf(payMoney.get("PLEDGE_ENTER_MCTOAG"));
		String agtax=String.valueOf(payMoney.get("PLEDGE_ENTER_MCTOAGRATE"));
		if(agpay==null) {
			agpay="0.0";
		}
		if(agtax==null) {
			agtax="0.0";
		}
		double agcost=Double.parseDouble(agpay)+Double.parseDouble(tax);
		
		//方案费用查询
		double cost1=0.0;
		double cost2=0.0;
//		for(int i=0;feeListRZE!=null&&i<feeListRZE.size();i++) {
//			cost1=cost1+Double.parseDouble(((Map)(feeListRZE.get(i))).get("FEE")==null?"0":((Map)(feeListRZE.get(i))).get("FEE").toString());
//		}
//		for(int j=0;feeList!=null&&j<feeList.size();j++) {
//			cost2=cost2+Double.parseDouble(((Map)(feeList.get(j))).get("FEE")==null?"0":((Map)(feeList.get(j))).get("FEE").toString());
//		}
		
		t1.addCell(makeCellWithLTBorderSetColspan(cost+cost1+cost2==0.0?"￥0.00":updateMoney(String.valueOf(cost+cost1+cost2)),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		
		t1.addCell(makeCellWithLRTBorderSetColspan("财务确认人： ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3));
		
		t1.addCell(makeCellWithLTBorder("税金", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLRTBorderSetColspan(payMoney.get("PLEDGE_ENTER_CMRATE")==null?"￥0.00":updateMoney(payMoney.get("PLEDGE_ENTER_CMRATE").toString())+"   入我司（代收款）",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,6));
		
		//平均抵充金额计算
		double avgPay=0.0;
		if(payDetail.get("PAY_MONEY")!=null&&payMoney.get("PLEDGE_ENTER_CMRATE")!=null) {
			if(Double.parseDouble(payDetail.get("PAY_MONEY").toString())-Double.parseDouble(payMoney.get("PLEDGE_ENTER_CMRATE").toString())>=0.000000001) {
				//PLEDGE_ENTER_AG
				//Modify by Michael 平均冲抵包含入供应商平均冲抵
				//avgPay=Double.parseDouble(payDetail.get("PAY_MONEY").toString())-Double.parseDouble(payMoney.get("PLEDGE_ENTER_CMRATE").toString());
				avgPay=Double.parseDouble(payDetail.get("PAY_MONEY").toString())-(Double.parseDouble(payMoney.get("PLEDGE_ENTER_CMRATE").toString())+Double.parseDouble(payMoney.get("PLEDGE_ENTER_AG").toString()));
				
			}
		}
		
		//Modify by Michael 2012 4-26 For 李芳修改保证金凭证
//		t1.addCell(makeCellWithLTBorder("平均抵充金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
//		t1.addCell(makeCellWithLRTBorderSetColspan(avgPay==0.0?"￥0.00":updateMoney(String.valueOf(avgPay)),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,6));
        double sumPayMoney=0.0;
        if (context.contextMap.get("SUM_PAY_MONEY")!=null ){
        	 sumPayMoney=Double.parseDouble(String.valueOf(context.contextMap.get("SUM_PAY_MONEY")));
        }

		//Add by Michael 2012 4-26 For 李芳修改保证金凭证
       
        if ("7".equals(contractType)) {
        	t1.addCell(makeCellWithLTBorder("入我司一次性抵充金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		} else {
			t1.addCell(makeCellWithLTBorder("入我司平均抵充金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		}
		t1.addCell(makeCellWithLTBorderSetColspan(payMoney.get("PLEDGE_ENTER_MCTOAG")==null?"￥0.00":updateMoney(payMoney.get("PLEDGE_ENTER_MCTOAG").toString()),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		if ("7".equals(contractType)) {
			t1.addCell(makeCellWithLTBorder("入供应商一次性抵充金额", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		} else {
			t1.addCell(makeCellWithLTBorder("入供应商平均抵充金额", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		}
		t1.addCell(makeCellWithLRTBorderSetColspan(payMoney.get("PLEDGE_ENTER_AG")==null?"￥0.00":updateMoney(payMoney.get("PLEDGE_ENTER_AG").toString()), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall,2));
		
		t1.addCell(makeCellWithLTBorder("本次付款额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLTBorderSetColspan(updateMoney(String.valueOf(payMoney.get("PAY_MONEY"))),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		t1.addCell(makeCellWithLTBorder("剩余未付款", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
		t1.addCell(makeCellWithLRTBorderSetColspan(updateMoney((agcost-sumPayMoney)+""), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall,2));
		
		//第二行
		//Add by Michael 2012 4-26 For 李芳修改保证金凭证
//		t1.addCell(makeCellWithLTBorder("本次付款额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
//		t1.addCell(makeCellWithLTBorderSetColspan(payDetail.get("PAY_MONEY")==null?"￥0.00":updateMoney(payDetail.get("PAY_MONEY").toString()),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		double leftpay1=0.0;
		if(payDetail.get("PAY_MONEY")!=null&&payMoney.get("LEASE_TOPRIC")!=null){
			if(Double.parseDouble(payMoney.get("LEASE_TOPRIC").toString())-Double.parseDouble(payDetail.get("PAY_MONEY").toString())>=0.000000001){
				leftpay1=Double.parseDouble(payMoney.get("LEASE_TOPRIC").toString())-Double.parseDouble(payDetail.get("PAY_MONEY").toString());
			}
		}
		//Add by Michael 2012 4-26 For 李芳修改保证金凭证
//		t1.addCell(makeCellWithLTBorder("剩余未付款", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
//		t1.addCell(makeCellWithLRTBorderSetColspan(updateMoney(Double.parseDouble(payMoney.get("LEASE_TOPRIC").toString())-avgPay+""), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall,2));

		//新版本的PDF不使用保证金栏位
//		t1.addCell(makeCellWithLTBorder("保证金", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
//		cell=makeCellWithLTBorderSetColspan("     金额        "+updateMoney(payMoney.get("PAYCOUNT").toString())+"     ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3);
//		cell.setFixedHeight(40);
//		t1.addCell(cell);
//		t1.addCell(makeCellWithLRTBorderSetColspan("财务确认人     ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3));
		
//		t1.addCell(makeCellWithLRTBorderSetColspan("财务确认人     "+payMoney.get("USERNAME"), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3));
		//第三行
		t1.addCell(makeCellWithLTBorder("收款人名称", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		t1.addCell(makeCellWithLTBorderSetColspan(payMoney.get("NAME").toString(), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 4));
		t1.addCell(makeCellWithLTBorder("付款日期", PdfCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		Object PAY_DATE=payMoney.get("PAY_DATE");
		if(PAY_DATE!=null && !"".equals(PAY_DATE) && !"1900-01-01 00:00:00.0".equals(PAY_DATE.toString()))
		{
			t1.addCell(makeCellWithLRTBorder(format.format(payMoney.get("PAY_DATE")).toString(), PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		}
		else
		{
			t1.addCell(makeCellWithLRTBorder(" ", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		}
		//第四行
		t1.addCell(makeCellWithLTBorder("开  户  行", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		t1.addCell(makeCellWithLRTBorderSetColspan(payMoney.get("BANK_NAME").toString(), PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontDefault, 6));
		//第五行
		t1.addCell(makeCellWithLTBorder("账    号", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		t1.addCell(makeCellWithLRTBorderSetColspan(payMoney.get("BANK_ACCOUNT").toString(), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 6));
		//第六行
		t1.addCell(makeCellWithLTBBorder("支付金额", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontDefault));
		//t1.addCell(makeCellWithLTBBorderSetColspan("(小写)"+updateMoney(payMoney.get("PAY_MONEY").toString()),PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		t1.addCell(makeCellWithLTBBorderSetColspan("(小写)"+updateMoney(String.valueOf(payMoney.get("PAY_MONEY"))),PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE,FontSmall,3));
		//t1.addCell(makeCellWithLRTBBorderSetColspan("(大写)"+CurrencyConverter.toUpper(payMoney.get("PAY_MONEY").toString()), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3));
		t1.addCell(makeCellWithLRTBBorderSetColspan("(大写)"+CurrencyConverter.toUpper(String.valueOf(payMoney.get("PAY_MONEY"))), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 3));
		//		t1.addCell(makeCellWithLTBBorderSetColspan(""+updateMoney(payMoney.get("PAY_MONEY").toString()), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontSmall, 2));
		
//		t1.addCell(makeCellWithLTBBorder("(大写)", PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontSmall));
//		t1.addCell(makeCellWithLRTBBorderSetColspan(CurrencyConverter.toUpper(payMoney.get("PAY_MONEY").toString()), PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault, 4));
		
		t1.addCell(makeCellNoBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,7));
		
		t1.addCell(makeCellNoBorderSetColspan("业管处：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("业务部：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,2));
		t1.addCell(makeCellNoBorderSetColspan("承办：", PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("承办：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("业管部主管：", PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("单位主管：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("处级主管：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("区部主管：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		t1.addCell(makeCellNoBorderSetColspan("处级主管：", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,5));
		
		
		t1.addCell(makeCellNoBorderSetColspan(" ", PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontDefault,7));
		//创建另一个Table
		  float[] width2={30f,30f,30f,20f};
	        PdfPTable tT2 = new PdfPTable(width2);
	        tT2.addCell(makeCell(bfChinese,"拨款申请",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0.5f,4));
	       
	        
	        tT2.addCell(makeCell(bfChinese,"总经理",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese,"经管处",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        if ("7".equals(contractType) && companyCode==2) {
	        	tT2.addCell(makeCell(bfChinese,"管理部",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        }else{
	        	tT2.addCell(makeCell(bfChinese,"财管部",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        }
	        tT2.addCell(makeCell(bfChinese,"承办",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0.5f,1));
	    
	        
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,1));
	       
	        
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0.5f,1));
	        
		
		doc.add(t1);
		doc.add(tT2) ;
		doc.close();
		String fileName="earnestMoney"+new String(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) +".pdf"; 
		HttpServletResponse response=context.getResponse();
		response.setContentType("application/pdf");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		response.setDateHeader("Expires", 0);
		response.setHeader("Content-Disposition","attachment; filename="+fileName);			
		ServletOutputStream os=response.getOutputStream();
		baos.writeTo(os);
		os.flush();
		os.close();
	}catch (Exception e) {
		e.printStackTrace();
		LogPrint.getLogStackTrace(e, logger);
	}
	
}
	
	/** 创建 只有左上边框 单元格 */
	private static PdfPCell makeCellWithLTBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	/** 创建 只有左右上边框 单元格 */
	private static PdfPCell makeCellWithLRTBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	
	/** 创建 只有左右上边框 单元格 */
	private static PdfPCell makeCellWithLRTBorderSetColspan(String content, int alignh,int alignv, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	
	/** 创建 只有右上边框 单元格 */
	private static PdfPCell makeCellWithLRTBorderSetColspans(String content, int alignh,int alignv, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	
	/** 创建 只有右下上边框 单元格 */
	private static PdfPCell makeCellWithLRTBorderSetColspanslbr(String content, int alignh,int alignv, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);
	    return objCell;
	}
	
	/** 创建 只有左上边框 合并 单元格 */
	private static PdfPCell makeCellWithLTBorderSetColspan(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
		objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);
		return objCell;
	}
	
	/** 创建 只有左框 合并 单元格 */
	private static PdfPCell makeCellWithLTBorderSetColspanleft(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
		objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
		return objCell;
	}
	
	
	/** 创建 只有左上下边框 单元格 */
	private static PdfPCell makeCellWithLTBBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	/** 创建 只有左右上下边框 单元格 */
	private static PdfPCell makeCellWithLRTBBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	
	/** 创建 只有左右上边框 单元格 */
	private static PdfPCell makeCellWithLRTBBorderSetColspan(String content, int alignh,int alignv, Font FontDefault,int colspan) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(30);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);	    
	    return objCell;
	}
	
	/** 创建 只有左上边框 合并 单元格 */
	private static PdfPCell makeCellWithLTBBorderSetColspan(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);		
	    return objCell;
	}
	
	/** 创建 只有左上边框 合并 单元格 */
	private static PdfPCell makeCellWithLTBBorderSetColspans(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0.5f);		
	    return objCell;
	}
	/** 创建 只有左上边框 合并 单元格 */
	private static PdfPCell makeCellWithLTBBorderSetColspanss(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);		
	    return objCell;
	}
	
	/** 创建 只有左下边框 合并 单元格 */
	private static PdfPCell makeCellWithLTBBorderSetColspansss(String content, int alignh,int alignv, Font FontDefault,int colspan) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(30);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setColspan(colspan);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);		
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
	
	/**  财务格式  0.00 */
	private String updateMoney(String content) {
	    String str="";
	    
	    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
		
		str+="￥ 0.00";
		return str;
		
	    }
	    else{
		
		DecimalFormat df1 = new DecimalFormat("#,###.00"); 
		
		str+=df1.format(Double.parseDouble(content));
		str="￥ "+str;
		return str;
	    }	
	}
	
	
	public void expPdfotherInsurance(Context context)
	{
		//保险费导出
		this.expPdf3(context);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void  expPdfother(Context context)
	{
		 Map payMoney=null;
		try
		{
			payMoney = (Map) DataAccessor.query("rentContract.payMoneyManagerToPdf", context.contextMap,DataAccessor.RS_TYPE.MAP);
			
			//Add by Michael 2012 5-2 增加已申请拨款金额
			context.contextMap.put("SUM_PAY_MONEY", DataAccessor.query("rentContract.getSumPayMoneyByCreditId", context.contextMap,DataAccessor.RS_TYPE.OBJECT));
			//Add by Michael 2012 09-003 增加查询发票延迟超过15天的件数
			context.contextMap.put("NO_INVOICE_COUNT", DataAccessor.query("rentContract.getNoInvoceRentCountByRecpID", payMoney,DataAccessor.RS_TYPE.OBJECT));
			
			int backState=0;
			 Object BACKSTATE=payMoney.get("BACKSTATE");
		        if(BACKSTATE!=null && !"".equals(BACKSTATE))
		        {
		        	backState=Integer.parseInt(payMoney.get("BACKSTATE").toString());//0：设备款，1：保证金拨款，:2：奖金拨款，3：保险费,4：法务费用
		        } 
		        @SuppressWarnings("unused")
				String backStateStr="";
		        if(backState==0)
		        {
		        	//backStateStr="设备款";
		        	this.expPdf(context);
		        }
		        else if(backState==1)
		        {
		        	//backStateStr="保证金拨款";
		        	context.contextMap.put("SUM_PAY_MONEY", DataAccessor.query("rentContract.getSumPayPledgeMoneyByCreditId", context.contextMap,DataAccessor.RS_TYPE.OBJECT));
		        	this.expPdf2(context);
		        }
		        else if(backState==2)
		        {
		        	//backStateStr="奖金拨款";
		        	this.expPdf(context);
		        }
		        else if(backState==3)
		        {
		        	//backStateStr="保险费";
		        	this.expPdf(context);
		        }
		        else
		        {
		        	//backStateStr="法务费用";
		        	this.expPdf(context);
		        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	//除保证金以外的导出
	 @SuppressWarnings("unchecked")
	public void expPdf(Context context) throws Exception 
	 {
		 	ByteArrayOutputStream baos = null;
		 	
		 	String payMoneyId=context.request.getParameter("payMoneyId");
		 	//String rectId=context.request.getParameter("RECT_ID");
		 	
		 	String pdfName=payMoneyId;//pdf名字
		 
		 try
		 {
			 float[] width={16f,7f,13f,14f,7f,8f,7f,7f,16f};
			 PdfPTable tT = new PdfPTable(width);//2表示列数
			 
			 // 字体设置
 	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
 	        
 	    
			Font FontTitle= new Font(bfChinese,17,Font.BOLD);
			//Font FontBold = new Font(bfChinese, 10, Font.BOLD);
			//Font FontDefault = new Font(bfChinese,10, Font.NORMAL);
			Font FontSmall = new Font(bfChinese,8, Font.NORMAL);
			Font FontSmall2 = new Font(bfChinese,5, Font.NORMAL);
			 
 	       // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        
	        Map payMoney=null;
	      //查询该供应商的基本信息
			payMoney = (Map) DataAccessor.query("rentContract.payMoneyManagerToPdf", context.contextMap,DataAccessor.RS_TYPE.MAP);	
			
			//Add by Michael 如果拨尾款就不要体现前收租金
			Integer num=(Integer) DataAccessor.query("rentContract.queryIsFirstPayMoney", context.contextMap,DataAccessor.RS_TYPE.OBJECT);	
			/* 2012/02/09 Yang Yun 新增费用显示.------------------------------ */
			List<Map<String, Object>> feeList = (List<Map<String, Object>>) DataAccessor.query("rentContract.getFeeList", context.contextMap, DataAccessor.RS_TYPE.LIST);
			/*--------------------------------------------------------------- */
			
			StartPayService.calculateAveValueAdded(payMoney);
	        // 打开文档
	        document.open();
	        
	        //详细参数看方法makeCell()
	        
	        //申请日期
	        Date destDateAPPLICATION_DATE = new SimpleDateFormat("yyyy-MM-dd").parse(payMoney.get("APPLICATION_DATE").toString());
	        String timeAPPLICATION=DateUtil.dateToString(destDateAPPLICATION_DATE);
	        
	        //预订支付日
	        Object EXPECTEDDATE=payMoney.get("EXPECTEDDATE");
	        Date destDateSTART_DATE=null;
	        String timeSTART=null;
	        if(EXPECTEDDATE!=null && !"".equals(EXPECTEDDATE))
	        {
	        	destDateSTART_DATE= new SimpleDateFormat("yyyy-MM-dd").parse(payMoney.get("EXPECTEDDATE").toString());
	        	timeSTART=DateUtil.dateToString(destDateSTART_DATE);
	        }
	        else
	        {
	        	timeSTART="";
	        }
	        
	        Date destDatePAY_DATE=null;
	        String timePAY_DATE=null;
	        //实际支付日
	        if(payMoney.get("PAY_DATE")!=null && !"".equals(payMoney.get("PAY_DATE")))
	        {
	        	destDatePAY_DATE= new SimpleDateFormat("yyyy-MM-dd").parse(payMoney.get("PAY_DATE").toString());
	        	if(!DateUtil.dateToString(destDatePAY_DATE).equals("1900-01-01")){
	        		timePAY_DATE=DateUtil.dateToString(destDatePAY_DATE);
	        	}else{
	        		timePAY_DATE="";
	        	}
	        }
	        else
	        {
	        	timePAY_DATE="";
	        }
	       
	        int backtype=0;
	        if(payMoney.get("BACKTYPE")!=null && !"".equals(payMoney.get("BACKTYPE"))){
	        backtype=Integer.parseInt(payMoney.get("BACKTYPE").toString());//0:供应商拨款，1:承租人拨款，2：制造商拨款，3：其他拨款
	        }
	        @SuppressWarnings("unused")
			String backTypeStr="";
	        if(backtype==0)
	        {
	        	backTypeStr="供应商拨款";
	        }
	        else if(backtype==1)
	        {
	        	backTypeStr="承租人拨款";
	        }
	        else if(backtype==2)
	        {
	        	backTypeStr="制造商拨款";
	        }
	        else
	        {
	        	backTypeStr="其他拨款";
	        }
	        int backState=0;
	        if(payMoney.get("BACKSTATE")!=null && !"".equals(payMoney.get("BACKSTATE")))
	        {
	        	backState=Integer.parseInt(payMoney.get("BACKSTATE").toString());//0：设备款，1：保证金拨款，:2：奖金拨款，3：保险费,4：法务费用
	        } 
	        @SuppressWarnings("unused")
			String backStateStr="";
	        if(backState==0)
	        {
	        	backStateStr="设备款";
	        }
	        else if(backState==1)
	        {
	        	backStateStr="保证金拨款";
	        }
	        else if(backState==2)
	        {
	        	backStateStr="奖金拨款";
	        }
	        else if(backState==3)
	        {
	        	backStateStr="保险费";
	        }
	        else
	        {
	        	backStateStr="法务费用";
	        }
	        
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
			//Modify By Michael 2011 12/16 公司Title错误 裕融
			//Chunk chunk1=new Chunk("融资租赁(苏州)有限公司",FontSmall);
			int creditId = LeaseUtil.getCreditIdByPayId(payMoneyId);
			
			int companyCode = LeaseUtil.getCompanyCodeByCreditId(String.valueOf(creditId));
			
			String contractType = LeaseUtil.getContractTypeByCreditId(String.valueOf(creditId));
			String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
			String companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(1);
			if("7".equals(contractType)){
				companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
				companyEngName = LeaseUtil.getCompanyEnglisgNameByCompanyCode(companyCode);
			}
			Chunk chunk1=new Chunk(companyName,FontSmall);
			Chunk chunk2=new Chunk(companyEngName,FontSmall2);
			
			cell=new PdfPCell();
			cell.addElement(chunk1);
			cell.addElement(chunk2);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			tlogo.addCell(cell);
			
			cell=new PdfPCell(tlogo);
			cell.setColspan(12);
			cell.setPaddingBottom(5);
			cell.setBorder(0);
			tT.addCell(cell);
			tT.addCell(makeCell(bfChinese," ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,9));
			cell=makeCellNoBorderSetColspan(companyName,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER, FontTitle,9);
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setFixedHeight(21);
			tT.addCell(cell);
	        tT.addCell(makeCell(bfChinese,"租赁合同<应付款 预付款凭证>",13,Font.BOLD,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,9));
	        tT.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        tT.addCell(makeCell(bfChinese,"租赁方式：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
	       
	        Object CONTRACT_TYPE=payMoney.get("CONTRACT_TYPE");
	        String contract_type=null;
	        if(CONTRACT_TYPE!=null && !"".equals(CONTRACT_TYPE))
	        {
	        	contract_type=CONTRACT_TYPE.toString();
	        }
	        
//	        if(contract_type.equals("0"))
//	        {
//	        	tT.addCell(makeCell(bfChinese,"[√] 一般租赁   [ ] 委托购买   [ ] 售后回租   [ ] 机动车租赁   [ ] 机动车回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
//	        }
//	        else if(contract_type.equals("1"))
//	        {
//	        	tT.addCell(makeCell(bfChinese,"[ ] 一般租赁   [√] 委托购买   [ ] 售后回租   [ ] 机动车租赁   [ ] 机动车回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
//	        }
	        if(contract_type.equals("5")){
	        	tT.addCell(makeCell(bfChinese,"[√] 新品回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("2")){
	        	tT.addCell(makeCell(bfChinese,"[√] 售后回租 ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("3")){
	        	tT.addCell(makeCell(bfChinese,"[√] 商用车租赁",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("4")){
	        	tT.addCell(makeCell(bfChinese,"[√] 商用车回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("0")){
	        	tT.addCell(makeCell(bfChinese,"[√] 一般租赁",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("1")){
	        	tT.addCell(makeCell(bfChinese,"[√] 委托购买",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("6")){
	        	tT.addCell(makeCell(bfChinese,"[√] 乘用车回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("7")){
	        	tT.addCell(makeCell(bfChinese,"[√] 直接租赁",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("8")){
	        	tT.addCell(makeCell(bfChinese,"[√] 新车委贷",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("9")){
	        	tT.addCell(makeCell(bfChinese,"[√] 设备售后回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("10")){
	        	tT.addCell(makeCell(bfChinese,"[√] 新车回租方案",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("11")){
	        	tT.addCell(makeCell(bfChinese,"[√] 商用车售后回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("12")){
	        	tT.addCell(makeCell(bfChinese,"[√] 二手车回租方案",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("13")){
	        	tT.addCell(makeCell(bfChinese,"[√] 原车回租方案",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }else if(contract_type.equals("14")){
	        	tT.addCell(makeCell(bfChinese,"[√] 二手车委贷",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }
	        else{
	        	//tT.addCell(makeCell(bfChinese,"[ ] 一般租赁   [ ] 委托购买   [ ] 售后回租   [ ] 机动车租赁   [ ] 机动车回租   [ ] 新品回租",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        	throw new Exception("租赁方式未匹配。");
	        }
	        
	        tT.addCell(makeCell(bfChinese,"交机情形：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
	        
	        Object QIANTYPE=payMoney.get("QIANTYPE");
	        Object HOUTYPE=payMoney.get("HOUTYPE");
	        //Modify by Michael 2012 10-12 前收租金 增加增值税
	        Double QIANAPPRORIATEMON=DataUtil.doubleUtil(payMoney.get("QIANAPPRORIATEMON"))+DataUtil.doubleUtil(payMoney.get("valueAddedTax"));
	        Object HOUAPPRORIATEMON=payMoney.get("HOUAPPRORIATEMON");
	        String type="";
	        String qianapp=null;
	        @SuppressWarnings("unused")
			String houapp=null;
	        if(QIANTYPE!=null && !"".equals(QIANTYPE)){
	        	type += "[√] 前  ";
	        } else {
	        	if(HOUTYPE!=null && !"".equals(HOUTYPE)){
		        	type += "[√] 后  ";
		        } 
	        }
	        
	        if(QIANAPPRORIATEMON!=null && QIANAPPRORIATEMON!=0)
	        {
	        	qianapp=QIANAPPRORIATEMON.toString();
	        }
	        else
	        {
	        	qianapp="0";
	        }
	        
	        if(HOUAPPRORIATEMON!=null && !"".equals(HOUAPPRORIATEMON))
	        {
	        	houapp=HOUAPPRORIATEMON.toString();
	        }
	        else
	        {
	        	houapp="0";
	        }
	        
	        tT.addCell(makeCell(bfChinese,type,-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        
	        Object FLAG=payMoney.get("SUPL_PAY_WAY");
	        tT.addCell(makeCell(bfChinese,"付款方式：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
	        if(FLAG!=null && !"".equals(FLAG))
	        {
	        	String flag=FLAG.toString();
	        	if(flag.equals("1"))
	        	{
	        		String  deferPeriod= String.valueOf(payMoney.get("DEFER_PERIOD")!=null?payMoney.get("DEFER_PERIOD"):0);
	        		if("0".equals(deferPeriod)){
	        			tT.addCell(makeCell(bfChinese,"[√] 支票转账 ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        		}else{
	        			tT.addCell(makeCell(bfChinese,"[√] 支票转账 (延迟拨款期数："+deferPeriod+")",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        		}
	        		
	        		String ENDORSER_1 = (String) payMoney.get("ENDORSER_1");
	        		if (!StringUtils.isEmpty(ENDORSER_1)) {
	        			tT.addCell(makeCell(bfChinese,"第一背书人：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
		        		tT.addCell(makeCell(bfChinese,ENDORSER_1,-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
					}
	        		String ENDORSER_2 = (String) payMoney.get("ENDORSER_2");
	        		if (!StringUtils.isEmpty(ENDORSER_2)) {
	        			tT.addCell(makeCell(bfChinese,"第二背书人：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
		        		tT.addCell(makeCell(bfChinese,ENDORSER_2,-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
					}
	        		String CHEQUE_TIME = (String) payMoney.get("CHEQUE_TIME");
	        		if (!StringUtils.isEmpty(CHEQUE_TIME)) {
	        			tT.addCell(makeCell(bfChinese,"支票开立日：",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
		        		tT.addCell(makeCell(bfChinese,CHEQUE_TIME,-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
					}
	        	}
	        	else
	        	{
	        		tT.addCell(makeCell(bfChinese,"[√] 网银付款  ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        	}
	        }
	        else
	        {
	        	tT.addCell(makeCell(bfChinese,"[ ] 支票转账   [ ] 网银付款  ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0f,0f,0f,8));
	        }
	        
	        
	        
	        tT.addCell(makeCell(bfChinese,"预订支付日",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT.addCell(makeCell(bfChinese,timeSTART,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
	        tT.addCell(makeCell(bfChinese,"实际支付日",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,-1));
	        tT.addCell(makeCell(bfChinese,timePAY_DATE,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
	        tT.addCell(makeCell(bfChinese,"申 请 日 期",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,2));
	        tT.addCell(makeCell(bfChinese,timeAPPLICATION,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0.5f,1));
	        
	        
	        PdfPTable tT1 = new PdfPTable(width);
	        tT1.addCell(makeCell(bfChinese,"请款单位:",-1,-1,-1f,-1f,-1f,-1f,-1,PdfPCell.ALIGN_CENTER,0.5f,0f,0.5f,0f,1));
	       // tT1.addCell(makeCell(bfChinese,payMoney.get("NAME").toString(),-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0.5f,3));
	         tT1.addCell(makeCell(bfChinese,payMoney.get("BACKCOMP")==null?"":payMoney.get("BACKCOMP").toString(),-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,3));
	        if(backtype==0)
	        {
	        	//tT1.addCell(makeCell(bfChinese,"   [√] 供应商      [ ]承租人   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0.5f,3));
	        	tT1.addCell(makeCell(bfChinese,"[√]发票人 [ ]供应商 [ ]承租人 [ ]制造商           [ ]其它   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	        }
	        else if(backtype==1)
	        {
	        	//tT1.addCell(makeCell(bfChinese,"   [ ] 供应商      [√]承租人   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0.5f,3));
	        	tT1.addCell(makeCell(bfChinese,"[ ]发票人 [√]供应商 [ ]承租人 [ ]制造商           [ ]其它   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	        }
	        else if(backtype==2)
	        {
	        	//tT1.addCell(makeCell(bfChinese,"   [ ] 供应商      [√]承租人   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0.5f,3));
	        	tT1.addCell(makeCell(bfChinese,"[ ]发票人 [ ]供应商 [√]承租人 [ ]制造商           [ ]其它   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	        }
	        else if(backtype==3)
	        {
	        	//tT1.addCell(makeCell(bfChinese,"   [ ] 供应商      [√]承租人   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0.5f,3));
	        	tT1.addCell(makeCell(bfChinese,"[ ]发票人 [ ]供应商 [ ]承租人 [√]制造商           [ ]其它   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	        }
	        else
	        {
	        	//tT1.addCell(makeCell(bfChinese,"   [ ] 供应商      [√]承租人   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0.5f,3));
	        	tT1.addCell(makeCell(bfChinese,"[]发票人 []供应商 []承租人 []制造商           [√]其它   ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	        }
	        
	        tT1.addCell(makeCell(bfChinese,"开户及账号:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,1));
	        Object BANK_NAME=payMoney.get("BANK_NAME");
	        if(BANK_NAME!=null && !"".equals(BANK_NAME))
	        {
	        	tT1.addCell(makeCell(bfChinese,payMoney.get("BANK_NAME").toString(),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,4));
	        }
	        else
	        {
	        	tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,4));
	        }
	        Object BANK_ACCOUNT=payMoney.get("BANK_ACCOUNT");
	        if(BANK_ACCOUNT!=null && !"".equals(BANK_ACCOUNT))
	        {
	        	tT1.addCell(makeCell(bfChinese,"账号:"+payMoney.get("BANK_ACCOUNT").toString(),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
	        }
	        else
	        {
	        	tT1.addCell(makeCell(bfChinese,"账号:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
	        }
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"承租人:"+payMoney.get("CUST_NAME"),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,4));
	        tT1.addCell(makeCell(bfChinese,"合同号:"+payMoney.get("LEASE_CODE"),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
	        
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"购买金额:",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        Object LEASE_TOPRIC=payMoney.get("LEASE_TOPRIC");
	        Object INIT_LEASE_TOPRIC=payMoney.get("INIT_LEASE_TOPRIC");
	        String leasetopric=null;
	        String leasetopric1=null;
	        if(LEASE_TOPRIC!=null && !"".equals(LEASE_TOPRIC))
	        {
	        	leasetopric=this.updateMon(payMoney.get("LEASE_TOPRIC").toString());
	        }
	        else
	        {
	        	leasetopric=this.updateMon("0");
	        }
	        
	        if(INIT_LEASE_TOPRIC!=null && !"".equals(INIT_LEASE_TOPRIC))
	        {
	        	leasetopric1=this.updateMon(payMoney.get("INIT_LEASE_TOPRIC").toString());
	        }
	        else
	        {
	        	leasetopric1=this.updateMon("0");
	        }
	        tT1.addCell(makeCell(bfChinese,leasetopric1 + "",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"其他支出:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0.5f,2));
	        
	        
	        double payCount=Double.parseDouble(payMoney.get("PAYCOUNT").toString());
	        //System.out.println("+++++++++++++++++++++++++++++="+payCount);
	        double payMoneys=Double.parseDouble(payMoney.get("PAY_MONEY").toString());
	        double payed=Double.parseDouble(payMoney.get("PAYED").toString());
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"可拨款总金额:",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        tT1.addCell(makeCell(bfChinese,leasetopric,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        
	        //Modify by Michael 2012 5-2 For 李芳修改申请
	        //tT1.addCell(makeCell(bfChinese,"已付应付款:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,2));
	        double sumPayMoney=0.0;
	        if (context.contextMap.get("SUM_PAY_MONEY")!=null ){
	        	 sumPayMoney=Double.parseDouble(String.valueOf(context.contextMap.get("SUM_PAY_MONEY")));
	        }
	        
	        double sumPayMoneyTemp=sumPayMoney;
	        
	        tT1.addCell(makeCell(bfChinese,"已申请付款:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,2));
	        
	        Object PAYED=payMoney.get("PAYED");
	        String paye=null;
	        if(PAYED!=null && !"".equals(PAYED))
	        {
	        	paye=this.updateMon(payMoney.get("PAYED").toString());
	        }
	        else
	        {
	        	paye=this.updateMon("0");
	        }
	      //Modify by Michael 2012 5-2 For 李芳修改申请
	        //tT1.addCell(makeCell(bfChinese,paye,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0.5f,2));
	        tT1.addCell(makeCell(bfChinese,this.updateMon(String.valueOf(sumPayMoneyTemp)),-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0.5f,2));
	        
	        tT1.addCell(makeCell(bfChinese,"事由及用途:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"本次付款额:",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        Object PAY_MONEY=payMoney.get("PAY_MONEY");
	        String paymoney=null;
	        if(PAY_MONEY!=null && !"".equals(PAY_MONEY))
	        {
	        	paymoney=this.updateMon(payMoney.get("PAY_MONEY").toString());
	        }
	        else
	        {
	        	paymoney=this.updateMon("0");
	        }
	        tT1.addCell(makeCell(bfChinese,paymoney,-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"剩余未付款:",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,2));
	       
	        String T8STATE=payMoney.get("T8STATE").toString();
	        double leftPad=0f;
	        if(T8STATE.equals("3"))
	        {
	        	leftPad=payCount-payMoneys-payed;
	        }
	        else
	        {
	        	leftPad=payCount-payed;
	        }
	        
	        //Modify by Michael 2012 5-2 For 李芳修改申请
	        //double leftPads=payCount-payMoneys-payed;
	        double leftPads=Double.parseDouble(payMoney.get("LEASE_TOPRIC").toString())-sumPayMoney-Double.parseDouble(String.valueOf(payMoney.get("PAY_MONEY")));
	        
	        tT1.addCell(makeCell(bfChinese,this.updateMon(String.valueOf(leftPads)),-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0.5f,2));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,1f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"保证金:",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,1f,0f,0.5f,0f,1));
	        /* 2012/02/09 Yang Yun 修改保证金计算方式.
	        tT1.addCell(makeCell(bfChinese,this.updateMon(payMoney.get("PLEDGE_REALPRIC").toString()),-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,0f,0.5f,0f,2));
	        */
	        tT1.addCell(makeCell(bfChinese,this.updateMon(String.valueOf(
	        		((Double)payMoney.get("PLEDGE_AVE_PRICE")).doubleValue() + 
	        		((Double)payMoney.get("PLEDGE_BACK_PRICE")).doubleValue() + 
	        		((Double)payMoney.get("PLEDGE_LAST_PRICE")).doubleValue())),-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,0f,0.5f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,0f,0.5f,1f,4));
	        
	        //入供应商钱数
	        Object PLEDGE_ENTER_CMPRICE=payMoney.get("PLEDGE_ENTER_CMPRICE");
	        double rugys=0;
	        if(PLEDGE_ENTER_CMPRICE!=null)
	        {
	        	rugys=Double.parseDouble(PLEDGE_ENTER_CMPRICE.toString());
	        }
	        
	        
	        //入我司钱数
	        Object PLEDGE_ENTER_AG=payMoney.get("PLEDGE_ENTER_AG");
	        double ruws=0;
	        if(PLEDGE_ENTER_AG!=null)
	        {
	        	ruws=Double.parseDouble(PLEDGE_ENTER_AG.toString());
	        }
	        
	        //平均抵充
	        Object PLEDGE_AVE_PRICE=payMoney.get("PLEDGE_AVE_PRICE");
	        double pjtc=0;
	        if(PLEDGE_AVE_PRICE!=null)
	        {
	        	pjtc=Double.parseDouble(PLEDGE_AVE_PRICE.toString());
	        }
	        
	        String zifu="";
	        if(rugys>0)
	        {
	        	zifu="[√]入供应商 ";
	        }
	        else
	        {
	        	zifu="[ ]入供应商 ";
	        }
	        
	        if(ruws>0)
	        {
	        	zifu=zifu+"[√]入我司";
	        }
	        else
	        {
	        	zifu=zifu+"[ ]入我司";
	        }
	        
	        String zifu1="";
	        if(pjtc>0)
	        {
	        	zifu1=" [√] 平均抵充   [ ] 其他   ";
	        }
	        else
	        {
	        	zifu1=" [ ] 平均抵充   [ ] 其他   ";
	        }
	        DecimalFormat df5 = new DecimalFormat("###.###");
	        String newzifu1="";
	        String newzifu2="";
	        String newzifu3="";
	        String newzifu4="";
	        String newzifu5="";
	        String newzifu6="";
	        	
	        newzifu1="入我司 "+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_CMPRICE")))));
	        newzifu2="入供应商 "+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_AG")))));
	        newzifu3="入供应商税金 "+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_AGRATE")))));
	        newzifu4="我司入供应商税金 "+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_MCTOAGRATE")))));
	        newzifu5=" 入我司税金 "+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_CMRATE")))));
	        newzifu6=" 我司入供应商"+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_ENTER_MCTOAG")))));
	        
	        String newzifu7="";
	        String newzifu8="";
	        String newzifu9="";
	        String newzifu10="";
	        if (contract_type.equals("7")) {
	        	newzifu7=" 用于一次性抵冲金额"+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_AVE_PRICE")))));
			} else {
				newzifu7=" 用于平均抵冲金额"+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_AVE_PRICE")))));
			}
	        newzifu8=" 用于期末退还金额"+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_BACK_PRICE")))));
	        newzifu9=" 用于最后抵冲未税金额"+this.updateMon(String.valueOf((df5.format(payMoney.get("PLEDGE_LAST_PRICE")))));
	        newzifu10=" 用于最后抵冲增值税金额"+this.updateMon(String.valueOf((df5.format(DataUtil.doubleUtil(payMoney.get("PLEDGE_LAST_PRICE_TAX"))))));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"扣除:",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,newzifu1+newzifu5+newzifu6+newzifu4,8,-1,-1f,-1f,0f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,1f,7));
	        //tT1.addCell(makeCell(bfChinese,newzifu5,-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
	        //tT1.addCell(makeCell(bfChinese,newzifu2,-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,2));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,newzifu2 + "  " + newzifu3,-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,1f,7));
//	        tT1.addCell(makeCell(bfChinese,newzifu3,-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
//	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,2));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,newzifu7+newzifu8+newzifu9,8,-1,-1f,-1f,0f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,1f,7));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,newzifu10,8,-1,-1f,-1f,0f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,1f,7));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,1f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"其他：",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_RIGHT,-1,0.5f,0f,0.5f,0f,1));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,1f,3));
	        
	        tT1.addCell(makeCell(bfChinese,"实际支付金额",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        @SuppressWarnings("unused")
			double payCounts=payMoneys+payed;
	        //String payCountss=String.valueOf(payCounts);leftPad
	        @SuppressWarnings("unused")
			String payCountss=String.valueOf(payCount-leftPad);
	        DecimalFormat df4 = new DecimalFormat("###.###");
	        //System.out.println("--------------------------------"+payCount+"-------------------"+leftPad+"--------------------"+(payCount-leftPad)+"---------------"+df4.format(payCount-leftPad));
	        
	        
	        //tT1.addCell(makeCell(bfChinese,"RMB："+CurrencyConverter.toUpper(String.valueOf(df4.format(payCount-leftPad)))+"零    元整",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,5));
	       // tT1.addCell(makeCell(bfChinese,"【 "+this.updateMon(String.valueOf((df4.format(payCount-leftPad))))+" 元整】",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,5));
	       
	        tT1.addCell(makeCell(bfChinese,"RMB："+CurrencyConverter.toUpper(String.valueOf(df4.format(payMoney.get("PAY_MONEY"))))+"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,0f,0.5f,0f,5));
	        tT1.addCell(makeCell(bfChinese,"【 "+this.updateMon(String.valueOf((df4.format(payMoney.get("PAY_MONEY")))))+" 元整】",-1,-1,-1f,-1f,-1f,-1f,-1,-1,1f,0f,0.5f,0.5f,5));
	        
	        DecimalFormat numFormat = new DecimalFormat("###,###,###,##0.00");
	        //Add by Michael 如果拨尾款就不要体现前收租金
	        if (num==0){
		        if(Double.parseDouble(payMoney.get("PAY_WAY").toString())==11 || Double.parseDouble(payMoney.get("PAY_WAY").toString())==12 || Double.parseDouble(payMoney.get("PAY_WAY").toString())==13)
		        //if(QIANTYPE!=null && QIANTYPE!="")
		        {
		        	if(String.valueOf(payMoney.get("TAX_PLAN_CODE")).equals("1")){
		        		tT1.addCell(makeCell(bfChinese,"[√] 未税前收租金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        	}else if(String.valueOf(payMoney.get("TAX_PLAN_CODE")).equals("2")){
		        		tT1.addCell(makeCell(bfChinese,"[√] 含税前收租金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        	} else if("3".equals(String.valueOf(payMoney.get("TAX_PLAN_CODE")))) {//加入增值税内含方案 add by ShenQi
		        		tT1.addCell(makeCell(bfChinese,"[√] 未税前收租金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        	} else{
		        		tT1.addCell(makeCell(bfChinese,"[√] 前收租金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        	}
		        }
		        else
		        {
		        	tT1.addCell(makeCell(bfChinese,"[ ] 前收租金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        }
		        if(Double.parseDouble(payMoney.get("PAY_WAY").toString())==11 || Double.parseDouble(payMoney.get("PAY_WAY").toString())==12 || Double.parseDouble(payMoney.get("PAY_WAY").toString())==13)
			    {
		        	tT1.addCell(makeCell(bfChinese,"金额："+ numFormat.format(Double.parseDouble(String.valueOf(qianapp))),7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
			    }else{
			        tT1.addCell(makeCell(bfChinese,"金额：",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
			    }
		        tT1.addCell(makeCell(bfChinese,"入账期：",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
		        tT1.addCell(makeCell(bfChinese,"确认人：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
		        tT1.addCell(makeCell(bfChinese,"财务：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,1));
	        }
	        
	        
	        /* 2012/02/09 Yang Yun 增加费用. */
	        if (contract_type.equals("8")) {
    			Map<String, Object> income_pay = new HashMap<String, Object>();
    			income_pay.put("FEE_NAME", "手续费");
    			income_pay.put("FEE", new BigDecimal(StringUtils.isEmpty(payMoney.get("INCOME_PAY")) ? "0" : String.valueOf(payMoney.get("INCOME_PAY"))));
    			feeList.add(income_pay);
			} 
	        
	        int feeCount = 0;
	        for (Map<String, Object> map : feeList) {
	        	if (((BigDecimal)map.get("FEE")).doubleValue() != 0) {
		        	tT1.addCell(makeCell(bfChinese,"[√] " 
		        			+ ((String)map.get("FEE_NAME")),7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
		        	tT1.addCell(makeCell(bfChinese,"金额：" + numFormat.format(map.get("FEE")),7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
		        	tT1.addCell(makeCell(bfChinese,"入账期：",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
			        tT1.addCell(makeCell(bfChinese,"确认人：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
			        tT1.addCell(makeCell(bfChinese,"财务：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,1));
			        feeCount ++;
	        	}
			}
	        /* 2014/07/30 xuwei增加费用. */
	        if (contract_type.equals("14")) {
	        	BigDecimal bd  = new BigDecimal(StringUtils.isEmpty(payMoney.get("INCOME_PAY")) ? "0" : String.valueOf(payMoney.get("INCOME_PAY")));
	        	tT1.addCell(makeCell(bfChinese,"[√] 手续费" ,7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        	tT1.addCell(makeCell(bfChinese,"金额：" + numFormat.format(bd),7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
	        	tT1.addCell(makeCell(bfChinese,"入账期：",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0f,2));
		        tT1.addCell(makeCell(bfChinese,"确认人：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,3));
		        tT1.addCell(makeCell(bfChinese,"财务：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,1));
			} 
	        /*if(HOUTYPE!=null && HOUTYPE!="")
	        {
	        	//tT1.addCell(makeCell(bfChinese,"[√] 后收租金",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
	        	tT1.addCell(makeCell(bfChinese,"[ ] 后收租金",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,1));
	        }
	        else
	        {*/
	        if (((Double)payMoney.get("PLEDGE_ENTER_CMPRICE")) != 0) {
	        	tT1.addCell(makeCell(bfChinese,"[√] 含税保证金",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,1));
	        /*}*/
	        	//double baoZhenJin = (Double)payMoney.get("PLEDGE_ENTER_CMPRICE") + (Double)payMoney.get("PLEDGE_ENTER_CMRATE")+DataUtil.doubleUtil(payMoney.get("PLEDGE_LAST_PRICE_TAX"));
	        	
	        	double baoZhenJin = (Double)payMoney.get("PLEDGE_ENTER_CMPRICE") + (Double)payMoney.get("PLEDGE_ENTER_CMRATE");
		        tT1.addCell(makeCell(bfChinese,"金额：" + numFormat.format(baoZhenJin),7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0.5f,0.5f,0f,2));
		        
		        tT1.addCell(makeCell(bfChinese,"入账期：",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0.5f,0.5f,0f,2));
		        tT1.addCell(makeCell(bfChinese,"确认人：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,3));
		        tT1.addCell(makeCell(bfChinese,"财务：",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0.5f,1));
	        } else {
	        	tT1.addCell(makeCell(bfChinese,"",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0f,1));
		        tT1.addCell(makeCell(bfChinese,"",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0f,0f,2));
		        tT1.addCell(makeCell(bfChinese,"",7,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0f,0f,2));
		        tT1.addCell(makeCell(bfChinese,"",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0f,3));
		        tT1.addCell(makeCell(bfChinese,"",7,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0f,0f,1));
			}
	        
	        /*tT1.addCell(makeCell(bfChinese,"[ ] 其他",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,5));
	        tT1.addCell(makeCell(bfChinese,"确认人：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"财务：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0.5f,1));*/
	        
	        //Add by Michael 2012-10-12  增加交机前拨款额度提示
	        if(backState==0)
	        {
	        	if(QIANTYPE!=null && !"".equals(QIANTYPE)){
	        		Map sum_APPRORIATEMON = (Map) DataAccessor.query("rentContract.payMoneyManagerAdvanceToPdf", payMoney,DataAccessor.RS_TYPE.MAP);
	        		tT1.addCell(makeCell(bfChinese,"已核准交机前授信额度："+(payMoney.get("ADVANCEMACHINE_GRANT_PRICE")==null?"0.0":this.updateMon(String.valueOf((df4.format(payMoney.get("ADVANCEMACHINE_GRANT_PRICE"))))))+"元，已核未拨交机前拨款金额："+(sum_APPRORIATEMON.get("SUM_APPRORIATEMON")==null?"0.0":this.updateMon(String.valueOf((df4.format(sum_APPRORIATEMON.get("SUM_APPRORIATEMON"))))))+"元。",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
        			feeCount ++;
	        	}
	        }	        
	        //Add by Michael 2012-09-03 如果是设备款时，且办事处待补发票超过3件的大于15天的要Show出来
	        
	        if(backState==0)
	        {
	        	if (null!=context.contextMap.get("NO_INVOICE_COUNT")){
	        		if(Integer.parseInt(((Map)context.contextMap.get("NO_INVOICE_COUNT")).get("NUM").toString())>3){
	        			tT1.addCell(makeCell(bfChinese,"本办事处超过15日发票待补"+((Map)context.contextMap.get("NO_INVOICE_COUNT")).get("NUM")+"件。",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        			feeCount ++;
	        		}
	        	}
	        }
	        int productionType =LeaseUtil.getProductionTypeByCreditId(String.valueOf(creditId));
	        if(productionType==1){
	        	if(LeaseUtil.isImportEqipByCreditId(String.valueOf(creditId))){
	        		tT1.addCell(makeCell(bfChinese,"此案件为进口设备，成本不确定。",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
        			feeCount ++;
	        	}
	        }
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"总经理",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"业管处",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"业务处",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,4));
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"承办：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"承办："+(payMoney.get("USER_NAME")==null?"":payMoney.get("USER_NAME").toString())+"    "+(payMoney.get("MODIFY_DATE")==null?"":payMoney.get("MODIFY_DATE").toString()),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,4));
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"业管部主管：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"单位主管："+(payMoney.get("DEPARTMENT_USER_NAME")==null?"":payMoney.get("DEPARTMENT_USER_NAME").toString())+"    "+(payMoney.get("DEPARTMENT_DATE")==null?"":payMoney.get("DEPARTMENT_DATE").toString()),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,4));
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"处级主管：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"区部主管：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,4));
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,2));
	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,3));
	        tT1.addCell(makeCell(bfChinese,"处级主管：",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,4));
	        
	        tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        
	        tT1.addCell(makeCell(bfChinese,"备注：",7,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
	        String remark = (String) payMoney.get("REMARK");
	        tT1.addCell(makeCell(bfChinese, remark,-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0.5f,0f,0.5f,0.5f,8));
	        tT1.addCell(makeCell(bfChinese," ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0f,0f,0f,0f,1));
	        tT1.addCell(makeCell(bfChinese, " ",-1,Font.NORMAL,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_LEFT,-1,0f,0.5f,0.5f,0.5f,8));
	        for(int i = 0; i < 2; i++){
	        	tT1.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0f,0f,9));
	        }

	        float[] width2={30f,30f,30f,20f};
	        PdfPTable tT2 = new PdfPTable(width2);
	        tT2.addCell(makeCell(bfChinese,"拨款申请",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0.5f,4));
	       
	        
	        tT2.addCell(makeCell(bfChinese,"总经理",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese,"经管处",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        if("7".equals(contract_type)&& companyCode==2){
	        	   tT2.addCell(makeCell(bfChinese,"管理部",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        }else{
	        	   tT2.addCell(makeCell(bfChinese,"财管部",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0f,1));
	        }
	     
	        tT2.addCell(makeCell(bfChinese,"承办",-1,-1,-1f,-1f,-1f,-1f,PdfPCell.ALIGN_CENTER,-1,0.5f,0f,0.5f,0.5f,1));
	    
	        
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,1));
	       
	        
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0f,1));
	        tT2.addCell(makeCell(bfChinese," ",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0.5f,0.5f,0.5f,1));
	        
//	        tT1.addCell(makeCell(bfChinese,"本次付款额："+this.updateMon(payMoney.get("PAY_MONEY").toString())+"         剩余未付款："+this.updateMon(String.valueOf(leftPad)),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
//	        
//	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
//	        tT1.addCell(makeCell(bfChinese,"税金：     "+this.updateMon(payMoney.get("PLEDGE_ENTER_CMRATE").toString())+"     入我司（代收款）",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
//	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
//	        tT1.addCell(makeCell(bfChinese,"扣除：        保证金：       "+this.updateMon(payMoney.get("PLEDGE_REALPRIC").toString())+"       入我司",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
//	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
//	        tT1.addCell(makeCell(bfChinese,"用于平均抵冲金额："+this.updateMon(payMoney.get("PLEDGE_AVE_PRICE").toString())+"         用于期末退还金额："+this.updateMon(payMoney.get("PLEDGE_BACK_PRICE").toString())+"            用于最后抵冲金额/期数："+this.updateMon(payMoney.get("PLEDGE_LAST_PRICE").toString()),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
//	        tT1.addCell(makeCell(bfChinese,"",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0f,0f,0.5f,0f,1));
//	        tT1.addCell(makeCell(bfChinese,"手续费：  "+ this.updateMon("0.00"),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0f,0.5f,0.5f,4));
//	        tT1.addCell(makeCell(bfChinese,"实际支付总额",-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0f,1));
//	        float payCounts=payMoneys+payed;
//	        String payCountss=String.valueOf(payCounts);
//	        tT1.addCell(makeCell(bfChinese,"RMB："+this.updateMon(String.valueOf((payMoneys+payed)))+"           "+CurrencyConverter.toUpper(payCountss),-1,-1,-1f,-1f,-1f,-1f,-1,-1,0.5f,0.5f,0.5f,0.5f,4));
	        
	       
	        document.add(tT);
	        document.add(tT1);
	        document.add(tT2);
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
			 
			 
			 
			 
		 }catch(Exception e){
			 logger.error(e);
			 throw e;
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
	 
	 
	 /**  财务格式  0.00 */
		private String updateMon(String content) {
		    String str="";
		    
		    if( content == null	|| DataUtil.doubleUtil(content)==0.0){
			
			str+="￥ 0.00";
			return str;
			
		    }
		    else{
			
			DecimalFormat df1 = new DecimalFormat("#,###.00"); 
			
			str+=df1.format(Double.parseDouble(content));
			str="￥ "+str;
			return str;
		    }	
		}	
		
	 
	 //pdf样式设定()
	 private PdfPCell makeCell(BaseFont bfChinese,String content,int fontSize,int fontStyle,float paddingTopF,float paddingBottomF
			 ,float paddingLeftF,float paddingRightF,int alignHorizontal,int alignVertical,float borderTopF,float borderBottomF,float borderLeftF,
			 float borderRightF,int colspan) 
	 {
		 	//字体自定义
			//int fontSize=0;//字体大小(一般设置成10,默认为10（标记-1）)
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
		 			FontStyleDe = new Font(bfChinese, 11f, Font.NORMAL);
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
		 			FontStyleDe = new Font(bfChinese, fontSize, Font.NORMAL);
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
			}else {
				objCell.setPaddingTop(3);
			}
			if(paddingBottomF!=-1)
			{
				objCell.setPaddingBottom(paddingBottomF);
			}else{
				objCell.setPaddingBottom(3);
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
