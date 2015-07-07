package com.brick.insurance.service;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

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

import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.web.HTMLUtil;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ExportExcel extends AService{
	private static Log logger=LogFactory.getLog(ExportExcel.class);
	
	@SuppressWarnings("unchecked")
	public void exportEqmtList(Context context){
		SqlMapClient client=null;
		try {
			client = DataAccessor.getSession();
			client.startTransaction();
			List<Map> content=new ArrayList<Map>();
			Map paramap=new HashMap();
			paramap.put("flag", "已导出");
			String[] recds=HTMLUtil.getParameterValues(context.getRequest(), "cbox", "");
			for(String recd:recds){
				paramap.put("recd_id", recd);
				Map map=(Map)DataAccessor.query("insuranceExp.queryByEqmtId",paramap,DataAccessor.RS_TYPE.MAP);
				content.add(map);
				//修改导出标志
				client.update("insuranceExp.expFlag", paramap);
			}
			
			ByteArrayOutputStream baos=exportEqmt("待投保设备",content);
			
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
			//Output.jspSendRedirect(context, "defaultDispatcher?__action=insure.queryAll");
		}
	}
	
	/**
	 * 续保，保单管理 中导出设备
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void exportEqmtListForInsuId(Context context){
		SqlMapClient client=null;
		try {
			client = DataAccessor.getSession();
			client.startTransaction();
			List<Map> content=new ArrayList<Map>();
			Map paramap=new HashMap();
			paramap.put("flag", "已导出");
			String[] insu_ids = HTMLUtil.getParameterValues(context.getRequest(), "cbox", "");
			StringBuffer sb = new StringBuffer();
			for (String string : insu_ids) {
				sb.append("'" + string + "',");
			}
			String insu_idsForSql = "(" + sb.substring(0, sb.length() - 1).toString() + ")";
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("insu_idsForSql", insu_idsForSql);
			List<Map<String, Object>> recds = (List<Map<String, Object>>) DataAccessor.query("insurance.getEqmtByInsuIdForExp",paramMap,DataAccessor.RS_TYPE.LIST);
			for(Map<String, Object> rsMap : recds){
				paramap.put("recd_id", rsMap.get("RECD_ID"));
				Map map=(Map)DataAccessor.query("insuranceExp.queryByEqmtId",paramap,DataAccessor.RS_TYPE.MAP);
				//添加保单号
				map.put("INSU_CODE", rsMap.get("INSU_CODE"));
				content.add(map);
				//修改导出标志
				client.update("insuranceExp.expFlag", paramap);
			}
			
			ByteArrayOutputStream baos=exportEqmtForInsu("已投保设备",content);
			
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
			//Output.jspSendRedirect(context, "defaultDispatcher?__action=insure.queryAll");
		}
	}
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream exportEqmtForInsu(String name,List content){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		WorkbookSettings setting=new WorkbookSettings();
		
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
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			format3.setWrap(true);

			// 设置列宽
			sheet.setColumnView(0, 10);
			sheet.setColumnView(1, 30);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 30);
			sheet.setColumnView(4, 30);
			sheet.setColumnView(5, 30);
//			sheet.setColumnView(6, 40);
			
			Label cell = null;
			
			cell=new Label(0,0,"序号",format1);
			sheet.addCell(cell);
			cell=new Label(1,0,"客户",format1);
			sheet.addCell(cell);
			cell=new Label(2,0,"合同号",format1);
			sheet.addCell(cell);
			cell=new Label(3,0,"保单号",format1);
			sheet.addCell(cell);
			cell=new Label(4,0,"设备名称",format1);
			sheet.addCell(cell);
			cell=new Label(5,0,"设备型号",format1);
			sheet.addCell(cell);
			cell=new Label(6,0,"机号",format1);
			sheet.addCell(cell);
//			cell=new Label(6,0,"设备存放场所",format1);
//			sheet.addCell(cell);
			/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。Start*/
			cell=new Label(7,0,"合同到期日期",format1);
			sheet.addCell(cell);
			cell=new Label(8,0,"保单到期日期",format1);
			sheet.addCell(cell);
			/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。End*/

			int i=1;
			for(Iterator iterator=content.iterator();iterator.hasNext();){
				Map contentMap=(Map)iterator.next();
				cell = new Label(0, i,i+"", format3);
				sheet.addCell(cell);
				Object obj=null;
				obj=contentMap.get("CUST_NAME");
				if(obj==null)obj="";
				cell = new Label(1, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("LEASE_CODE");
				if(obj==null)obj="";
				cell = new Label(2, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("INSU_CODE");
				if(obj==null)obj="";
				cell = new Label(3, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("THING_NAME");
				if(obj==null)obj="";
				cell = new Label(4, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("MODEL_SPEC");
				if(obj==null)obj="";
				cell = new Label(5, i,obj.toString(), format3);
				sheet.addCell(cell);
				//加入机号
				obj=contentMap.get("THING_NUMBER");
				if(obj==null)obj="";
				cell = new Label(6, i,obj.toString(), format3);
				sheet.addCell(cell);
				/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。Start*/
				cell = new Label(7, i, contentMap.get("PAY_DATE") == null ? "N/A" : (String)contentMap.get("PAY_DATE"), format3);
				sheet.addCell(cell);
				cell = new Label(8, i, contentMap.get("INSU_END_DATE") == null ? "N/A" : (String)contentMap.get("INSU_END_DATE"), format3);
				sheet.addCell(cell);
				/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。End*/
//				obj=contentMap.get("EQUPMENT_ADDRESS");
//				if(obj==null)obj="";
//				cell = new Label(6, i,obj.toString(), format3);
//				sheet.addCell(cell);
			
				i++;
			}
			
			workbook.write();
			workbook.close();
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}
	
	
	@SuppressWarnings("unchecked")
	public ByteArrayOutputStream exportEqmt(String name,List content){
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		WorkbookSettings setting=new WorkbookSettings();
		
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
			format3.setAlignment(Alignment.LEFT);
			format3.setVerticalAlignment(VerticalAlignment.CENTRE);
			format3.setWrap(true);

			// 设置列宽
			sheet.setColumnView(0, 10);
			sheet.setColumnView(1, 30);
			sheet.setColumnView(2, 20);
			sheet.setColumnView(3, 30);
			sheet.setColumnView(4, 30);
			sheet.setColumnView(5, 30);
//			sheet.setColumnView(6, 40);
			
			Label cell = null;
			
			cell=new Label(0,0,"序号",format1);
			sheet.addCell(cell);
			cell=new Label(1,0,"客户",format1);
			sheet.addCell(cell);
			cell=new Label(2,0,"合同号",format1);
			sheet.addCell(cell);
			cell=new Label(3,0,"设备名称",format1);
			sheet.addCell(cell);
			cell=new Label(4,0,"设备型号",format1);
			sheet.addCell(cell);
			cell=new Label(5,0,"机号",format1);
			sheet.addCell(cell);
//			cell=new Label(6,0,"设备存放场所",format1);
//			sheet.addCell(cell);
			/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。Start*/
			cell=new Label(6,0,"合同到期日期",format1);
			sheet.addCell(cell);
			cell=new Label(7,0,"保单到期日期",format1);
			sheet.addCell(cell);
			/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。End*/

			int i=1;
			for(Iterator iterator=content.iterator();iterator.hasNext();){
				Map contentMap=(Map)iterator.next();
				cell = new Label(0, i,i+"", format3);
				sheet.addCell(cell);
				Object obj=null;
				obj=contentMap.get("CUST_NAME");
				if(obj==null)obj="";
				cell = new Label(1, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("LEASE_CODE");
				if(obj==null)obj="";
				cell = new Label(2, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("THING_NAME");
				if(obj==null)obj="";
				cell = new Label(3, i,obj.toString(), format3);
				sheet.addCell(cell);
				obj=contentMap.get("MODEL_SPEC");
				if(obj==null)obj="";
				cell = new Label(4, i,obj.toString(), format3);
				sheet.addCell(cell);
				//加入机号
				obj=contentMap.get("THING_NUMBER");
				if(obj==null)obj="";
				cell = new Label(5, i,obj.toString(), format3);
				sheet.addCell(cell);
				/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。Start*/
				cell = new Label(6, i, contentMap.get("PAY_DATE") == null ? "N/A" : (String)contentMap.get("PAY_DATE"), format3);
				sheet.addCell(cell);
				cell = new Label(7, i, contentMap.get("INSU_END_DATE") == null ? "N/A" : (String)contentMap.get("INSU_END_DATE"), format3);
				sheet.addCell(cell);
				/*2011/12/23 Yang Yun 增加 合同到期日期和保单到期日期 字段。End*/
//				obj=contentMap.get("EQUPMENT_ADDRESS");
//				if(obj==null)obj="";
//				cell = new Label(6, i,obj.toString(), format3);
//				sheet.addCell(cell);
			
				i++;
			}
			
			workbook.write();
			workbook.close();
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		return baos;
	}

}
