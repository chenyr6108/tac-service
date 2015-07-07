package com.brick.signOrder;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPageNumerEventHelper extends PdfPageEventHelper {
	
	private PdfTemplate tpl;
	private BaseFont bf;
	private String title;
	
	public PdfPageNumerEventHelper(String title) {
		this.title = title;
	}
	
	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            tpl = writer.getDirectContent().createTemplate(100, 100);
            bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
        }
        catch(Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
	@Override
    public void onEndPage(PdfWriter writer, Document document) {
       //在每页结束的时候把“第x页”信息写道模版指定位置
        PdfContentByte cb = writer.getDirectContent();
        //cb.saveState();
        cb.beginText();
        cb.setFontAndSize(bf, 8);
        cb.setTextMatrix(50, 15);//定位“第x页,共” 在具体的页面调试时候需要更改这xy的坐标
        cb.showText(title);
        cb.endText();
        String text = writer.getPageNumber() + " /";
        cb.beginText();
        cb.setFontAndSize(bf, 8);
        cb.setTextMatrix(290, 15);//定位“第x页,共” 在具体的页面调试时候需要更改这xy的坐标
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, 300, 15);//定位“y页” 在具体的页面调试时候需要更改这xy的坐标
        //cb.saveState();
        //cb.stroke();
        //cb.restoreState();       
        //cb.closePath();//sanityCheck();
    }
    
	@Override
    public void onCloseDocument(PdfWriter writer, Document document) {
       //关闭document的时候获取总页数，并把总页数按模版写道之前预留的位置
       tpl.beginText();
       tpl.setFontAndSize(bf, 8);
       tpl.showText(Integer.toString(writer.getPageNumber() - 1));
       tpl.endText();
       //tpl.closePath();//sanityCheck();
    }
	
}
