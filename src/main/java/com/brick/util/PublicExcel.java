package com.brick.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.brick.util.ExcelEntity;
import java.text.DecimalFormat;


public class PublicExcel 
{
	
	WritableWorkbook wb = null;
	WorkbookSettings workbookSettings = new WorkbookSettings();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();


	
	public void createexl() {
		try {
			wb = Workbook.createWorkbook(baos, workbookSettings);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	public ByteArrayOutputStream export(String excelName,String excelHead,int cells,int rows,WritableCellFormat format,List cellList,List titleList,List contentList) 
	{
		WritableSheet sheet = null;
		try {
			/* 解决中文乱码 */
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet(excelHead, 1);
			
			
			//设置列宽
			for(int i=0;i<cells;i++)
			{
				int cellWidth=Integer.parseInt(cellList.get(i).toString());
				sheet.setColumnView(i, cellWidth);
			}
			
			//创建列
			Label cell = null;//文字和符号
			Number number=null;//钱
			//标题列
			for(int i=0;i<titleList.size();i++)
			{
				ExcelEntity excelEntity=(ExcelEntity)titleList.get(i);
				cell = new Label(excelEntity.getCol(), excelEntity.getRow(), excelEntity.getContent(), excelEntity.getFormat());
				if(excelEntity.getColSpan()!=1 || excelEntity.getRowSpan()!=1)
				{
					sheet.mergeCells(excelEntity.getCol(), excelEntity.getRow(),excelEntity.getCol()+excelEntity.getColSpan(), excelEntity.getRow()+excelEntity.getRowSpan());
				}
				sheet.addCell(cell);
			}
			
			//列表
			for(int i=0;i<contentList.size();i++)
			{
				
				List contentListSun=(List)contentList.get(i);
				for(int j=0;j<contentListSun.size();j++)
				{
					cell = new Label(j,rows,contentListSun.get(j).toString(),format);
					sheet.addCell(cell);
				}
				rows++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return baos;
	}
	
	
	
	public ByteArrayOutputStream export1(String excelName,String excelHead,int cells,int rows,WritableCellFormat format,List cellList,List titleList,List contentList) 
	{
		WritableSheet sheet = null;
		try {
			/* 解决中文乱码 */
			workbookSettings.setEncoding("ISO-8859-1");
			sheet = wb.createSheet(excelHead, 1);
			
			
			//设置列宽
			for(int i=0;i<cells;i++)
			{
				int cellWidth=Integer.parseInt(cellList.get(i).toString());
				sheet.setColumnView(i, cellWidth);
			}
			
			//创建列
			Label cell = null;//文字和符号
			Number number=null;//钱
			//标题列
			for(int i=0;i<titleList.size();i++)
			{
				ExcelEntity excelEntity=(ExcelEntity)titleList.get(i);
				cell = new Label(excelEntity.getCol(), excelEntity.getRow(), excelEntity.getContent(), excelEntity.getFormat());
				if(excelEntity.getColSpan()!=1 || excelEntity.getRowSpan()!=1)
				{
					sheet.mergeCells(excelEntity.getCol(), excelEntity.getRow(),excelEntity.getCol()+excelEntity.getColSpan(), excelEntity.getRow()+excelEntity.getRowSpan());
				}
				sheet.addCell(cell);
			}
			
			//列表
			for(int i=0;i<contentList.size();i++)
			{
				
				ExcelEntity excelEntity=(ExcelEntity)contentList.get(i);
				cell = new Label(excelEntity.getCol(), excelEntity.getRow(), excelEntity.getContent(), excelEntity.getFormat());
				if(excelEntity.getColSpan()!=1 || excelEntity.getRowSpan()!=1)
				{
					sheet.mergeCells(excelEntity.getCol(), excelEntity.getRow(),excelEntity.getCol()+excelEntity.getColSpan(), excelEntity.getRow()+excelEntity.getRowSpan());
				}
				sheet.addCell(cell);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return baos;
	}
	
	
	public void close() {
		try {
			wb.write();
			wb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
