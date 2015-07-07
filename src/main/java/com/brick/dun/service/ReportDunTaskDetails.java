package com.brick.dun.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import com.brick.base.util.LeaseUtil;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.ExcelEntity;
import com.brick.util.NumberUtils;
import com.brick.util.PublicExcel;
import com.brick.util.StringUtils;

public class ReportDunTaskDetails {
	Log logger = LogFactory.getLog(ReportDunTaskDetails.class);
	String excelName=null;//该Excel导出文件名
	String excelHead=null;//该Excel头部信息
	int cell=0;//该Excel列数
	int row=0;//该Excel头部所占的行数
	WritableCellFormat format1=null;//该Excel列表的通用样式
	
	
	public void reportDunTaskDetail(List list,Context context) throws Exception
	{
		ByteArrayOutputStream baos = null;
		excelName="催收报表.xls";
		excelHead="催收报表";
		cell=27;
		row=2;
		format1=this.tableFont();
		List cellList=this.cellSome();
		List titleList=this.titleListMethod();
		List contentList=this.contextListMethod(list);
		PublicExcel exl = new PublicExcel();
		exl.createexl(); 
		baos = exl.export(excelName,excelHead,cell,row,format1,cellList,titleList,contentList);
		context.response.setContentType("application/vnd.ms-excel;charset=GB2312");
		try {
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(excelName.getBytes("GBK"), "ISO-8859-1"));
			ServletOutputStream out1 = context.response.getOutputStream();
			exl.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {
			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	
	//设置该Excel有和每一列的宽度,cellList的长度和cell（多少列）相同
	public List cellSome()
	{   //21 行
		List cellList=new ArrayList();
		cellList.add(10);
		cellList.add(15);
		cellList.add(10);
		cellList.add(15);
		cellList.add(30);
		cellList.add(15);
		cellList.add(10);
		cellList.add(15);
		cellList.add(10);
		cellList.add(10);
		cellList.add(10);
		cellList.add(15);
		cellList.add(10);
		
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);
		cellList.add(25);

		cellList.add(12);
		cellList.add(10);
		cellList.add(15);
		cellList.add(15);
		cellList.add(15);

		cellList.add(60);
		cellList.add(15);
		cellList.add(10);
		cellList.add(10);
	
		return cellList;
	}
	
	//设置标题
	public List titleListMethod()
	{
		List titleList=new ArrayList();
		
		//format(样式)
		//colspan(int)跨几列
		//rowspan(int)跨几行
		//content(String)内容
		//row(int)显示在第几行（有的标题有两行，说明该信息显示在第几行,从0开始算起）
		//col(int)显示在第几列（跨行时用到,从0开始算起）
		
		WritableCellFormat format=this.tableFont();//标题通用样式
//		//第一行
//		//标题第一列（跨两行,一列,居中显示在第一行）
//		//公司名称
		ExcelEntity col0=new ExcelEntity();
		col0.setFormat(format);
		col0.setRowSpan(1);
		col0.setColSpan(0);
		col0.setRow(0);
		col0.setCol(0);
		col0.setContent("公司别");
		titleList.add(col0);
		//第2行
		//标题第一列（跨两行,一列,居中显示在第一行）
		//地区
		ExcelEntity col1=new ExcelEntity();
		col1.setFormat(format);
		col1.setRowSpan(1);
		col1.setColSpan(0);
		col1.setRow(0);
		col1.setCol(1);
		col1.setContent("地区");
		titleList.add(col1);
		
		ExcelEntity col2=new ExcelEntity();
		col2.setFormat(format);
		col2.setRowSpan(1);
		col2.setColSpan(0);
		col2.setRow(0);
		col2.setCol(2);
		col2.setContent("经办人");
		titleList.add(col2);
		
		//标题第二列（跨一行,五列,居中显示在第一行）
		//合同编号
		ExcelEntity col33=new ExcelEntity();
		col33.setFormat(format);
		col33.setRowSpan(1);
		col33.setColSpan(0);
		col33.setRow(0);
		col33.setCol(3);
		col33.setContent("合同编号");
		titleList.add(col33);
		
		//承租人
		ExcelEntity col3=new ExcelEntity();
		col3.setFormat(format);
		col3.setRowSpan(1);
		col3.setColSpan(0);
		col3.setRow(0);
		col3.setCol(4);
		col3.setContent("承租人");
		titleList.add(col3);
		//"拨款金额"
		ExcelEntity col55=new ExcelEntity();
		col55.setFormat(format);
		col55.setRowSpan(1);
		col55.setColSpan(0);
		col55.setRow(0);
		col55.setCol(5);
		col55.setContent("拨款金额");
		titleList.add(col55);
		
		//期数
		ExcelEntity col6=new ExcelEntity();
		col6.setFormat(format);
		col6.setRowSpan(1);
		col6.setColSpan(0);
		col6.setRow(0);
		col6.setCol(6);
		col6.setContent("期数");
		titleList.add(col6);
		//首次逾期日期
		ExcelEntity col002=new ExcelEntity();
		 col002.setFormat(format);
		 col002.setRowSpan(1);
		 col002.setColSpan(0);
		 col002.setRow(0);
		 col002.setCol(7);
		 col002.setContent("首次逾期日期");
		 titleList.add(col002);
		//首次逾缴期
		ExcelEntity col5=new ExcelEntity();
		col5.setFormat(format);
		col5.setRowSpan(1);
		col5.setColSpan(0);
		col5.setRow(0);
		col5.setCol(8);
		col5.setContent("首次逾缴期");
		titleList.add(col5);	
		//已缴期数
		ExcelEntity col8=new ExcelEntity();
		col8.setFormat(format);
		col8.setRowSpan(1);
		col8.setColSpan(0);
		col8.setRow(0);
		col8.setCol(9);
		col8.setContent("已缴期数");
		titleList.add(col8);
		//每期租金
		ExcelEntity col7=new ExcelEntity();
		col7.setFormat(format);
		col7.setRowSpan(1);
		col7.setColSpan(0);
		col7.setRow(0);
		col7.setCol(10);
		col7.setContent("每期租金");
		titleList.add(col7);	
		//租金到期日
		ExcelEntity col9=new ExcelEntity();
		col9.setFormat(format);
		col9.setRowSpan(1);
		col9.setColSpan(0);
		col9.setRow(0);
		col9.setCol(11);
		col9.setContent("租金到期日");
		titleList.add(col9);
		
		//逾期天数
		ExcelEntity col10=new ExcelEntity();
		col10.setFormat(format);
		col10.setRowSpan(1);
		col10.setColSpan(0);
		col10.setRow(0);
		col10.setCol(12);
		col10.setContent("逾期天数");
		titleList.add(col10);
		
		//应收租金
		ExcelEntity col11=new ExcelEntity();
		col11.setFormat(format);
		col11.setRowSpan(1);
		col11.setColSpan(0);
		col11.setRow(0);
		col11.setCol(13);
		col11.setContent("应收租金");
		titleList.add(col11);
		//未收总租金
		ExcelEntity col12=new ExcelEntity();
		col12.setFormat(format);
		col12.setRowSpan(1);
		col12.setColSpan(0);
		col12.setRow(0);
		col12.setCol(14);
		col12.setContent("未收总租金");
		titleList.add(col12);
		//剩余本金
		ExcelEntity col13=new ExcelEntity();
		col13.setFormat(format);
		col13.setRowSpan(1);
		col13.setColSpan(0);
		col13.setRow(0);
		col13.setCol(15);
		col13.setContent("实际剩余本金");
		titleList.add(col13);
		//催收人员
		ExcelEntity col14=new ExcelEntity();
		col14.setFormat(format);
		col14.setRowSpan(1);
		col14.setColSpan(0);
		col14.setRow(0);
		col14.setCol(16);
		col14.setContent("催收人员");
		titleList.add(col14);
		//租赁方式
		ExcelEntity col15=new ExcelEntity();
		col15.setFormat(format);
		col15.setRowSpan(1);
		col15.setColSpan(0);
		col15.setRow(0);
		col15.setCol(17);
		col15.setContent("租赁方式");
		titleList.add(col15);
		//供应商
		ExcelEntity col16=new ExcelEntity();
		col16.setFormat(format);
		col16.setRowSpan(1);
		col16.setColSpan(0);
		col16.setRow(0);
		col16.setCol(18);
		col16.setContent("供应商");
		titleList.add(col16);
		//公司类型
		//Modify by Michael 2012 09-11  将公司栏位取消，改成显示供应商责任栏位
		ExcelEntity col17=new ExcelEntity();
		col17.setFormat(format);
		col17.setRowSpan(1);
		col17.setColSpan(0);
		col17.setRow(0);
		col17.setCol(19);
		col17.setContent("供应商责任");
		titleList.add(col17);
		//锁码
		ExcelEntity col18=new ExcelEntity();
		col18.setFormat(format);
		col18.setRowSpan(1);
		col18.setColSpan(0);
		col18.setRow(0);
		col18.setCol(20);
		col18.setContent("锁码");
		titleList.add(col18);
		//锁码日
		ExcelEntity col19=new ExcelEntity();
		col19.setFormat(format);
		col19.setRowSpan(1);
		col19.setColSpan(0);
		col19.setRow(0);
		col19.setCol(21);
		col19.setContent("锁码日");
		titleList.add(col19);
		//催收日
		ExcelEntity col20=new ExcelEntity();
		col20.setFormat(format);
		col20.setRowSpan(1);
		col20.setColSpan(0);
		col20.setRow(0);
		col20.setCol(22);
		col20.setContent("催收日");
		titleList.add(col20);
		//催收结果
		ExcelEntity col21=new ExcelEntity();
		col21.setFormat(format);
		col21.setRowSpan(1);
		col21.setColSpan(0);
		col21.setRow(0);
		col21.setCol(23);
		col21.setContent("催收结果");
		titleList.add(col21);
		
		//催收结果
		ExcelEntity col22=new ExcelEntity();
		col22.setFormat(format);
		col22.setRowSpan(1);
		col22.setColSpan(0);
		col22.setRow(0);
		col22.setCol(24);
		col22.setContent("催收简要记录");
		titleList.add(col22);
		
		ExcelEntity col23=new ExcelEntity();
		col23.setFormat(format);
		col23.setRowSpan(1);
		col23.setColSpan(0);
		col23.setRow(0);
		col23.setCol(25);
		col23.setContent("回访日期");
		titleList.add(col23);
		
		ExcelEntity col24=new ExcelEntity();
		col24.setFormat(format);
		col24.setRowSpan(1);
		col24.setColSpan(0);
		col24.setRow(0);
		col24.setCol(26);
		col24.setContent("回访人员");
		titleList.add(col24);
		
		ExcelEntity col25=new ExcelEntity();
		col25.setFormat(format);
		col25.setRowSpan(1);
		col25.setColSpan(0);
		col25.setRow(0);
		col25.setCol(27);
		col25.setContent("回访次数");
		titleList.add(col25);
		
		ExcelEntity col27=new ExcelEntity();
		col27.setFormat(format);
		col27.setRowSpan(1);
		col27.setColSpan(0);
		col27.setRow(0);
		col27.setCol(28);
		col27.setContent("拨款日期");
		titleList.add(col27);

		
		
		return titleList;
		
	}
	
	
	public List contextListMethod(List contextList) throws Exception
	{
		List contextListFather=new ArrayList();
		//for(int i=0;i<contextList.size();i++)
		//{
			Iterator iter = contextList.iterator();
			 DecimalFormat df = new DecimalFormat("#.00");
			Map<String, Object> paramMap = null;
			//添加  拨款金额\应收租金\未收总租金\实际剩余本金 列'总和'12-25 zhang
			double payMoneyTotal =0;
			double dunMonthPriceTotal =0;
			double totalRentPriceTotal =0;
			double sunOwnPriceTotal =0;
			
			while(iter.hasNext()){
		       
			Map contentmap=(HashMap)iter.next();
			List contextListSun=new ArrayList();
			
			contextListSun.add(LeaseUtil.getCompanyShortNameByCompanyCode(Integer.parseInt((String)contentmap.get("COMPANY_CODE"))));
			contextListSun.add(contentmap.get("DECP_NAME_CN")==null ? "" :contentmap.get("DECP_NAME_CN").toString());
			contextListSun.add(contentmap.get("NAME")==null ? "" :contentmap.get("NAME").toString());
			contextListSun.add(contentmap.get("LEASE_CODE")==null ? "" :contentmap.get("LEASE_CODE").toString());
			contextListSun.add(contentmap.get("CUST_NAME")==null ? "" :contentmap.get("CUST_NAME").toString());
			contextListSun.add(contentmap.get("PAYMONEY")==null ? "" :contentmap.get("PAYMONEY").toString());//拨款金额
			
			String payMoney=contentmap.get("PAYMONEY")==null ? "0" :contentmap.get("PAYMONEY").toString();
			if(StringUtils.isEmpty(contentmap.get("PAYMONEY"))){
				 payMoney="0";
			}
			BigDecimal PAYMONEYBD = new BigDecimal(Double.parseDouble(payMoney));
	        double PAYMONEYDOUBLE = PAYMONEYBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			payMoneyTotal=payMoneyTotal+PAYMONEYDOUBLE;
			
			contextListSun.add(contentmap.get("MAX_PERIOD_NUM")==null ? "" :contentmap.get("MAX_PERIOD_NUM").toString());
			contextListSun.add(contentmap.get("FIRSTDUNDATE")==null ? "" :contentmap.get("FIRSTDUNDATE").toString());
			contextListSun.add(contentmap.get("FIRSTDUNPERIOD")==null ? "" :contentmap.get("FIRSTDUNPERIOD").toString());
			contextListSun.add(contentmap.get("AL_PERIOD_NUM")==null ? "" :contentmap.get("AL_PERIOD_NUM").toString());
			contextListSun.add(contentmap.get("IRR_MONTH_price")==null ? "" :contentmap.get("IRR_MONTH_price").toString());
			contextListSun.add(contentmap.get("PAY_DATE")==null ? "" :contentmap.get("PAY_DATE").toString());//租金到期日
			contextListSun.add(contentmap.get("DUN_DAY")==null ? "" :contentmap.get("DUN_DAY").toString());
			contextListSun.add(contentmap.get("DUN_MONTHPRICE")==null ? "" :contentmap.get("DUN_MONTHPRICE").toString());//应缴租金
			
			String dunMonthPrice =contentmap.get("DUN_MONTHPRICE")==null ? "0" :contentmap.get("DUN_MONTHPRICE").toString();
			BigDecimal DUN_MONTHPRICEBD = new BigDecimal(Double.parseDouble(dunMonthPrice));
	        double DUN_MONTHPRICEDOUBLE = DUN_MONTHPRICEBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	        dunMonthPriceTotal=dunMonthPriceTotal+DUN_MONTHPRICEDOUBLE;
			
			contextListSun.add(contentmap.get("TOTAL_RENT_PRICE")==null ? "" :contentmap.get("TOTAL_RENT_PRICE").toString()); //未收总租金
			
			String totalRentPrice =contentmap.get("TOTAL_RENT_PRICE")==null ? "0" :contentmap.get("TOTAL_RENT_PRICE").toString();
			BigDecimal TOTAL_RENT_PRICEBD = new BigDecimal(Double.parseDouble(totalRentPrice));
	        double TOTAL_RENT_PRICEDOUBLE = TOTAL_RENT_PRICEBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	        totalRentPriceTotal=totalRentPriceTotal+TOTAL_RENT_PRICEDOUBLE;
			
			//Add by Michael 2012 3-23 增加实际剩余本金信息----------------------------
			paramMap = new HashMap<String, Object>();
			Map settleMap = null;
			paramMap.put("zujin", "租金") ;				
			paramMap.put("zujinfaxi", "租金罚息") ;
			paramMap.put("sblgj", "设备留购价") ;
			paramMap.put("RECP_ID", contentmap.get("RECP_ID"));
			settleMap = (Map) DataAccessor.query("settleManage.selectSettlePrice", paramMap,DataAccessor.RS_TYPE.MAP);
			//以下----去掉钱符号:因sql被多出调用,固在java中保留两位 12-25 zhang----以下
			double sumOwnPrice =settleMap.get("SUM_OWN_PRICE") == null ? 0 : (Double)settleMap.get("SUM_OWN_PRICE");
			BigDecimal bg = new BigDecimal(sumOwnPrice);
	        double sumOwnPriceTwo = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			contextListSun.add(sumOwnPriceTwo);
			sunOwnPriceTotal=sunOwnPriceTotal+sumOwnPriceTwo;
			//以上--去掉钱符号:因sql被多出调用,固在java中保留两位 12-25 zhang-----以上
			
			contextListSun.add(contentmap.get("ANSWERPHONE_NAME")==null ? "" : contentmap.get("ANSWERPHONE_NAME").toString());
			contextListSun.add(contentmap.get("RECT_TYPE")==null ? "" :contentmap.get("RECT_TYPE").toString());//租赁方式
			contextListSun.add(contentmap.get("BRAND")==null ? "" :contentmap.get("BRAND").toString());
			contextListSun.add(contentmap.get("SUPL_TRUE")==null ? "" :contentmap.get("SUPL_TRUE").toString());//供应商保证
			contextListSun.add(contentmap.get("LOCK_CODE")==null ? "" :contentmap.get("LOCK_CODE").toString());//锁码
			
			contextListSun.add(contentmap.get("LOCK_DATE")==null ? "":contentmap.get("LOCK_DATE").toString());//锁码
			contextListSun.add(contentmap.get("CALL_DATE")==null ? "" :contentmap.get("CALL_DATE").toString());//cuishou日期
			contextListSun.add(contentmap.get("RESULT")==null ? "" :contentmap.get("RESULT").toString());//jieguo
			//Add by Michael 2012 09-11 增加催收简要内容
			contextListSun.add(contentmap.get("CALL_CONTENT")==null ? "" :contentmap.get("CALL_CONTENT").toString());//催收简要

			contextListSun.add(contentmap.get("VISIT_DATE")==null ? "" :contentmap.get("VISIT_DATE").toString());
			contextListSun.add(contentmap.get("VISIT_NAME")==null ? "" :contentmap.get("VISIT_NAME").toString());
			contextListSun.add(contentmap.get("VISIT_TIMES")==null ? "" :contentmap.get("VISIT_TIMES").toString());
			contextListSun.add(contentmap.get("FINANCECONTRACT_DATE")==null ? "" :contentmap.get("FINANCECONTRACT_DATE").toString());
			
			contextListFather.add(contextListSun);
     	}
			
			List contextListDown=new ArrayList();
			
			contextListDown.add("合计：");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add(df.format(payMoneyTotal));
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add(df.format(dunMonthPriceTotal));
			contextListDown.add(df.format(totalRentPriceTotal));
			contextListDown.add(df.format(sunOwnPriceTotal));
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListDown.add("");
			contextListFather.add(contextListDown);
		return contextListFather;
		
	}
	
	
	//设置样式（字体大小，颜色，边框样式）//标题通用样式（参数后在改进）
	public WritableCellFormat tableFont()
	{
		WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font1);
		try {
			
			format.setAlignment(Alignment.CENTRE);
			format.setVerticalAlignment(VerticalAlignment.CENTRE);
			//format.setBorder(Border.ALL, BorderLineStyle.THIN);
			format.setWrap(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return format;
	}
}

