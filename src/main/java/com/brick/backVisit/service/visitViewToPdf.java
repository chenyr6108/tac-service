package com.brick.backVisit.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.brick.util.web.HTMLUtil;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;


/**
 * 
 * @author 齐姜龙
 * @创建日期 2011-3-31
 * @版本 V 1.0
 */
public class visitViewToPdf extends AService
{
	Log logger = LogFactory.getLog(visitViewToPdf.class);
	 /**
     * 导出PDF  回访记录
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void prePdf(Context context){
	String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "lease_code_List", "00");
	Map cust =new HashMap();
	String type = null;
	 if(con != null ){
	     if(!(con[0].equals("00"))){
		 try {
        		 if(con.length >1){
        				 context.contextMap.put("credtdxx",  con);
                             	    this.expPdf(context);             	    
        			    }
        		 else{
                		 if(con.length ==1){
                		 context.contextMap.put("credtdxx",   con);
                            	    this.expPdf(context);
                		 } 
        			}
			} catch (Exception e) {
			    e.printStackTrace();
			    LogPrint.getLogStackTrace(e, logger);
			}
	     }
	 }	
    }
    
    private static final String imageFilePath = "D:/image/b.gif";
    
    
    public void expPdf(Context context) {
    	
    	ByteArrayOutputStream baos = null;
    	String[]  con = null;
    	 
    	List visitList =null;
    	List  CREDITNATU = new ArrayList();
    	List  CROP = new ArrayList();
    	Map natu  = new HashMap();
    	Map  crp =  new HashMap();
    	String code=null;
     	
    	try {   
    		 	float[] widthsStl = {0.2f,0.4f,0.2f};
    		 	PdfPTable tT = new PdfPTable(widthsStl);
    		 	
    	        // 字体设置
    	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
    	        Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
    	        Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
    	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
    	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
    	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
    	        Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
    	        Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
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
    	         
    	        
    	        //页码
//    			HeaderFooter footer = new HeaderFooter(new Phrase(" "), true);
//    			footer.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
//    			footer.setAlignment(Element.ALIGN_CENTER);
//    			document.setFooter(footer);	 
    	        
    	        tT.addCell(makeCellSetColspanWithNoBorder("回访信息记录",PdfPCell.ALIGN_CENTER, fa,4));
    	        // 打开文档
    	        document.open();
	    	   
    	    
    	    	con= (String[]) context.contextMap.get("credtdxx");
    	    	
        	    	
    	    	for(int ii=0; ii< con.length;ii++){
    	    		int t=0;
//    	    	context.contextMap.put("lease_code_List",  con[ii]);
//    	    	
//    	    	visitList = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCode", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
    	    		String leasecode=con[ii];
        	    	String leasecoderecdid[]=leasecode.split(",");
        	    	context.contextMap.put("lease_code_List",  leasecoderecdid[0]);
        	    	context.contextMap.put("rect_id",  leasecoderecdid[1]);
        	    	context.contextMap.put("cust_id",  leasecoderecdid[1]);
        	    	visitList = (List) DataAccessor.query("backVisit.viewVisitReviewRecordByLeaseCodeNew", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
                	  
    	    		if(visitList == null){
            		
            	    	visitList =new ArrayList();
            	        
            	    }
            	    else
            	    {
            	    	 float[] widthsPPCa = { 3f };
         	    	    
         	    	    tT.setWidthPercentage(100f);
         	    	    
            	    	for(int visitInfo=0;visitInfo<visitList.size();visitInfo++)
            	    	{
            	    	    
            	    	  
            	    	    t=t+5;
            	    		
            	    		
            	    		Map contract=(Map)visitList.get(visitInfo);
            	    		String lease_code=contract.get("LEASE_CODE").toString().trim();
            	    		String end_date=contract.get("END_DATE").toString().trim();
            	    		String end_dateFomate="";
            	    		if(end_date!=null && !"".equals(end_date))
            	    		{
            	    			end_dateFomate=end_date.substring(0,end_date.length()-11);
            	    		}
            	    		String cust_name=contract.get("CUST_NAME").toString().trim();
            	    		String province=contract.get("PROVINCE").toString().trim();
            	    		String city=contract.get("CITY").toString().trim();
            	    		String recp_code=contract.get("RECP_CODE").toString().trim();
//            	    		String thingkind=contract.get("THING_KIND").toString().trim();
//            	    		String thingname=contract.get("THING_NAME").toString().trim();
//            	    		String modelspec=contract.get("MODEL_SPEC").toString().trim();
            	    		code=lease_code;
            	    		
             	    	    tT.addCell(makeCell("合同编号:    " +lease_code ,PdfPCell.ALIGN_CENTER,  FontDefault));
             	    	    tT.addCell(makeCell("客户名称:    " +cust_name ,PdfPCell.ALIGN_CENTER,  FontDefault));
             	    	    tT.addCell(makeCell("租金到期日:    " +end_dateFomate ,PdfPCell.ALIGN_CENTER,  FontDefault));
             	    	    
             	    	    tT.addCell(makeCell("区域:    " +province ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    tT.addCell(makeCell("城市:    " +city ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    tT.addCell(makeCell("  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    		
            	    		break;
            	    	}
            	    	
            	    	for(int visitSize=0;visitSize<visitList.size();visitSize++)
            	    	{
            	    		
            	    		Map contract=(Map)visitList.get(visitSize);
            	    		String visit_date=contract.get("VISIT_DATE").toString();//回访日期
            	    		int is_business=Integer.parseInt(contract.get("IS_BUSINESS").toString().trim());//是否营业
            	    		String visit_conperson=contract.get("VISIT_CONPERSON").toString().trim();//接触对象
            	    		int is_products=Integer.parseInt(contract.get("IS_PRODUCTS").toString().trim());//	是否见标的物
            	    		int prod_degree=Integer.parseInt(contract.get("PROD_DEGREE").toString().trim());//标的物外观
            	    		int is_run=Integer.parseInt(contract.get("IS_RUN").toString().trim());//是否正常运行
            	    		int prod_degree_datailed=Integer.parseInt(contract.get("PROD_DEGREE_DETAILED").toString().trim());//机器运行率
            	    		int visit_results=Integer.parseInt(contract.get("VISIT_RESULTS").toString().trim());//回访结果
            	    		int is_backvisit=Integer.parseInt(contract.get("IS_BACKVISIT").toString().trim());//是否在回访
            	    		String visit_note=contract.get("VISIT_NOTE").toString().trim();//备注/建议
            	    		String visit_name=contract.get("NAME").toString().trim();//回访人员
            	    		

            	    	   
            	    		 tT.addCell(makeCellSetColspan2("第"+(visitSize+1)+"次回访",PdfPCell.LEFT, FontDefault,3));

            	    	    tT.addCell(makeCell("回访人员:   "+visit_name  ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	   
            	    	    tT.addCell(makeCell("回访日期:    " +visit_date ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    
            	    	    if(is_business==0)
            	    	    {
            	    	    	tT.addCell(makeCell("是否营业:  是  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(is_business==1)
            	    	    {
            	    	    	tT.addCell(makeCell("是否营业:  否  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("是否营业:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    
            	    	    tT.addCell(makeCell("接触对象:    " +visit_conperson ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    
            	    	    if(is_products==0)
            	    	    {
            	    	    	tT.addCell(makeCell("是否见标的物:  是  " +"",PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(is_products==1)
            	    	    {
            	    	    	tT.addCell(makeCell("是否见标的物:  否  "  +"",PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("是否见标的物:   "  +"",PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    
            	    	    if(prod_degree==0)
            	    	    {
            	    	    	tT.addCell(makeCell("标的物外观:  正常  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(prod_degree==1)
            	    	    {
            	    	    	tT.addCell(makeCell("标的物外观:  破损  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("标的物外观:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    
            	    	    if(is_run==0)
            	    	    {
            	    	    	tT.addCell(makeCell("是否正常运行:  是  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(is_run==1)
            	    	    {
            	    	    	tT.addCell(makeCell("是否正常运行:  否  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("是否正常运行:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    
            	    	    if(prod_degree_datailed==0)
            	    	    {
            	    	    	tT.addCell(makeCell("机器运行率:  正常  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(prod_degree_datailed==1)
            	    	    {
            	    	    	tT.addCell(makeCell("机器运行率:  良好  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(prod_degree_datailed==2)
            	    	    {
            	    	    	tT.addCell(makeCell("机器运行率:  差  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("机器运行率:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    
            	    	    if(visit_results==0)
            	    	    {
            	    	    	tT.addCell(makeCell("回访结果:  正常  " +"" ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	    else if(visit_results==1)
            	    	    {
            	    	    	tT.addCell(makeCell("回访结果:  异常  " +"" ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    	 if(is_backvisit==0)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  是  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else if(is_backvisit==1)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  否  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    
                 	    	    tT.addCell(makeCellSetColspan4("备注/建议:    "+visit_note ,PdfPCell.ALIGN_CENTER,  FontDefault,2));
                 	    	   
            	    	    }
            	    	    else if(visit_results==2)
            	    	    {
            	    	    	tT.addCell(makeCell("回访结果:  已停业 " +"" ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    	 if(is_backvisit==0)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  是  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else if(is_backvisit==1)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  否  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    
                 	    	    tT.addCell(makeCellSetColspan4("备注/建议:    "+visit_note ,PdfPCell.ALIGN_CENTER,  FontDefault,2));
                 	    	   
            	    	    }
            	    	    else if(visit_results==3)
            	    	    {
            	    	    	tT.addCell(makeCell("回访结果:  其他  " +"" ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    	 if(is_backvisit==0)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  是  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else if(is_backvisit==1)
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:  否  " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    else
                 	    	    {
                 	    	    	tT.addCell(makeCell("是否在回访:   " ,PdfPCell.ALIGN_CENTER,  FontDefault));
                 	    	    }
                 	    	    
                 	    	    tT.addCell(makeCellSetColspan4("备注/建议:    "+visit_note ,PdfPCell.ALIGN_CENTER,  FontDefault,2));
                 	    	   
            	    	    }
            	    	    else
            	    	    {
            	    	    	tT.addCell(makeCell("回访结果:   " +"" ,PdfPCell.ALIGN_CENTER,  FontDefault));
            	    	    }
            	    	   
            	    	   
            	    	}
            	    	
            	    	 tT.addCell(makeCellSetColspanWithNoBorder("",PdfPCell.ALIGN_CENTER, fa,3));
            	    	 tT.addCell(makeCellSetColspanWithNoBorder("",PdfPCell.ALIGN_CENTER, fa,3));
            	    	 tT.addCell(makeCellSetColspanWithNoBorder("",PdfPCell.ALIGN_CENTER, fa,3));
            	    	 tT.addCell(makeCellSetColspanWithNoBorder("",PdfPCell.ALIGN_CENTER, fa,3));
            	    	 tT.addCell(makeCellSetColspanWithNoBorder("",PdfPCell.ALIGN_CENTER, fa,3));
            	    	   
            	    	}
            	    
            	    }
    	    	 document.add(tT);
 	    	    document.add(Chunk.NEXTPAGE);
 	    	    
 	    
 	    	
 	    	    document.close();
 	    	    // 支付表PDF名字的定义
 	    	    String strFileName = code+".pdf";
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

    /** ￥0.00 */
    private String updateMoney(Map map, String content, NumberFormat nfFSNum) {
	String str = "";
	if (map == null) {
	    str +=  "0.00";
	    return str;
	}
	if (map.get(content).toString().equals("0")) {
	    str += "0.00";
	    return str;
	} else {
	    str +=  nfFSNum.format(Double.parseDouble(map.get(content)
			    .toString()));
	    return str;
	}

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

      
}
