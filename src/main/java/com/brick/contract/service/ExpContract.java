package com.brick.contract.service;

import com.brick.service.core.AService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
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

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;

public class ExpContract extends AService {
	Log logger = LogFactory.getLog(ExpContract.class);
    /**
     * 导出PDF  资信报告
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void prePdf(Context context){
	String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
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
   
    public void outPutDirectContract(Context context){
    	String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
    	Map cust =new HashMap();
    	String type = null;
    	 if(con != null ){
    	     if(!(con[0].equals("00"))){
    		 try {
            		 if(con.length >1){
            				 context.contextMap.put("credtdxx",  con);
                                 	    this.expDirectContractPdf(context);             	    
            			    }
            		 else{
                    		 if(con.length ==1){
                    		 context.contextMap.put("credtdxx",   con);
                                	    this.expDirectContractPdf(context);
                    		 } 
            			}
    			} catch (Exception e) {
    			    e.printStackTrace();
    			    LogPrint.getLogStackTrace(e, logger);
    			}
    	     }
    	 }	
        
    }
    
    
    public void prePdfAll(Context context){
    	String[] con = null ;
    	/*2011/12/27 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件)*/
    	Map<String, Object> rsMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", context.contextMap.get("s_employeeId"));
    	/*2011/12/27 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件)*/
    	try {
    		/*2011/12/27 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件)*/
    		rsMap = (Map<String, Object>) DataAccessor.query("employee.getEmpInforById", paramMap, DataAccessor.RS_TYPE.MAP);
			context.contextMap.put("p_usernode", rsMap.get("NODE"));
    		/*2011/12/27 Yang Yun Mantis[0000253] (區域主管無法看到該區域之逾期案件)*/
			List renConList = (List) DataAccessor.query("rentContract.queryRentContract", context.contextMap, RS_TYPE.LIST) ;
			if(renConList != null && renConList.size() > 0) {
				con = new String[renConList.size()] ;
				for(int i = 0;i<renConList.size();i++){
					Map temp = (Map) renConList.get(i) ;
					con[i] = temp.get("PRCD_ID").toString() ;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			LogPrint.getLogStackTrace(e1, logger) ;
		}  
    	Map cust =new HashMap();
    	String type = null;
    	
    	if(con != null ){
    		if(!(con[0].equals("00"))){
    			try {
    				if(con.length >= 1){
    					context.contextMap.put("credtdxx",  con);
    					this.expPdf(context);             	    
    				} 
    			} catch (Exception e) {
    				e.printStackTrace();
    				LogPrint.getLogStackTrace(e, logger);
    			}
    		}
    	}	
    }
    
    
    private static final String imageFilePath = "D:/image/b.gif";
    
    /**
     * 
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void expPdf(Context context) {
    	
	ByteArrayOutputStream baos = null;
	String[]  con = null;
	 
	Map contract =new HashMap();
	List  CREDITNATU = new ArrayList();
	List  CROP = new ArrayList();
	Map natu  = new HashMap();
	Map  crp =  new HashMap();
 	
	try {   
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
//			HeaderFooter footer = new HeaderFooter(new Phrase(" "), true);
//			footer.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
//			footer.setAlignment(Element.ALIGN_CENTER);
//			document.setFooter(footer);	        
	        
	        
	        
	        
	        // 打开文档
	        document.open();
	        

	    
	    	con= (String[]) context.contextMap.get("credtdxx");
	    	//
	    	
	    	//准备参数  add by Shen Qi 2012.03.05
	    	StringBuffer param=new StringBuffer();
	    	for(int i=0;con!=null&&i<con.length;i++) {
	    		param.append("'").append(con[i]).append("'");
	    		if(i!=con.length-1) {
	    			param.append(",");
	    		}
	    	}
	    	Map<String,String> paramMap=new HashMap<String,String>();
	    	paramMap.put("RECT_ID", param.toString());
	    	List<String> rectId=(List<String>)DataAccessor.query("rentContract.checkIsAudit", paramMap, DataAccessor.RS_TYPE.LIST);
    	    
    	    Map<String,String> checkIsAudit=new HashMap<String, String>();
    	    for(int i=0;rectId!=null&&i<rectId.size();i++) {
    	    	checkIsAudit.put(rectId.get(i),rectId.get(i));
    	    }
    	    
	    	for(int ii=0; ii< con.length;ii++){
	    		int t=0;
	    	context.contextMap.put("credit_id",  con[ii]);
	    	
	    	//add by ShenQi 插入系统日志
//	    	BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),checkIsAudit.get(con[ii])==null?DataUtil.longUtil("0"):DataUtil.longUtil(con[ii]),
//	    							   "导出 融资租赁合同",
//	    							   "合同浏览导出 融资租赁合同",
//	    							   null,
//	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
//	    							   1,
//	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
//	    							   DataUtil.longUtil(0));
	    	
	    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(con[ii]),checkIsAudit.get(con[ii])==null?DataUtil.longUtil("0"):DataUtil.longUtil(con[ii]),
	    							   		 "导出 融资租赁合同",
    							   		 	 "合同浏览导出 融资租赁合同",
    							   		 	 null,
    							   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
    							   		 	 1,
    							   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
    							   		 	 DataUtil.longUtil(0),
    							   		 	 context.getRequest().getRemoteAddr());
	    	//当合同表中的RECT_STATUS为1时表明为以复核的   取数据应该尽量从合同中取
	    	//getCreditCCorpByCreditIdUpdateCon代表是否存在以复核的数据   如果存在则取出   如果不存在则取报告中的数据
	    	contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditIdUpdateCon", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	if(contract!=null)
	    	{
	    		if(contract.size()>0)
	    		{
	    			;
	    		}
	    		else
	    		{
	    			contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		}
	    	}
	    	else
	    	{
	    		contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	}
	    	
	    
        	    if(contract == null){
        		
        		contract =new HashMap();
        	   
        		contract.put("LEASE_CODE", "  ____ ");
        		contract.put("CORP_NAME_CN", " ____  ");
        		contract.put("LEGAL_PERSON", " ____  ");
        		contract.put("REGISTERED_OFFICE_ADDRESS", " ");
        		contract.put("COMMON_OFFICE_ADDRESS", " ");
        		contract.put("POSTCODE", " ");
        		contract.put("TELEPHONE", " ");
        		contract.put("FAX", " ");
        	    contract.put("CUST_CODE", " ");
        	    //contract.put("CONTRACT_TYPE", "1");
        	    contract.put("CONTRACT_TYPE", context.contextMap.get("contractType"));    
        	    }
	    
	    String code = contract.get("CONTRACT_TYPE")+"";
	    

	    float[] widthsPPCa = { 3f };
	    PdfPTable tT = new PdfPTable(2);
	    
	    tT.setWidthPercentage(100f);
	    tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,2));
	 
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));

	  
	    tT.addCell(makeCellSetColspan2("融资租赁合同",PdfPCell.ALIGN_CENTER, fa,2));
	  
	      
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	  
	    t=t+5;
	     
	    String Lcode = contract.get("LEASE_CODE") +"";
	    	Lcode=	Lcode.trim();
	    if(Lcode.equals("")){
		Lcode = "           ";
	    }
	    tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_RIGHT, FontDefault,2));
	   
	    tT.addCell(makeCellWithBorderLeft(" " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRight("合同编号:    " +Lcode ,PdfPCell.ALIGN_CENTER,  FontDefault));
	    
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	 
	    tT.addCell(makeCellSetColspan2ForOne("          合同签订日:    20____年____月____日" ,PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	  
	    tT.addCell(makeCellSetColspan2ForOne("          合同签订地:    中华人民共和国" ,PdfPCell.ALIGN_LEFT, FontDefault,2));
	   
	   
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));

	    
	 
	    tT.addCell(makeCellWithBorderLeftForOne("            出租方(甲方):    "+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):    "+ contract.get("CORP_NAME_CN")+"",PdfPCell.ALIGN_LEFT, FontDefault22));
	     
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            法定代表或负责人:    "+Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("法定代表或负责人:    "+contract.get("LEGAL_PERSON")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	  
	  
	    tT.addCell(makeCellWithBorderLeftForOne("            注册地址:    苏州工业园区东富路8号",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("注册地址:    "+contract.get("REGISTERED_OFFICE_ADDRESS")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            实际经营地:    ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("实际经营地:    ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            "+Constants.COMPANY_COMMON_ADDRESS,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne(""+contract.get("COMMON_OFFICE_ADDRESS")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	   
	    tT.addCell(makeCellWithBorderLeftForOne("            邮政编码:    215022 ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("邮政编码:    "+contract.get("POSTCODE")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	   
	     
	    tT.addCell(makeCellWithBorderLeftForOne("            电话号码:    0512-80983566 ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("电话号码:    "+contract.get("TELEPHONE")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	 
	    tT.addCell(makeCellWithBorderLeftForOne("            传真号码:    0512-80983567 ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("传真号码:    "+contract.get("FAX")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	   
	     

	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));  
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));	  
	    
	    t=t+26;
	    
  Paragraph mm11 = new Paragraph();
	    
	    mm11.setFont(FontDefault);
	   
	    mm11.add("                      本合同的租赁实指中国合同法规定的融资租赁形式。出租方 ");
	    Chunk c361 = new Chunk( Constants.COMPANY_NAME ,FontUnder); 
	    mm11.add(c361);
	    
	    PdfPCell objCell = new PdfPCell(mm11);
	    
	    
	    objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setColspan(2);
		objCell.setPaddingLeft(35);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
	    
	    
	   tT.addCell(objCell);
	   
	   
	   
	   Paragraph mm12 = new Paragraph();
	    
        	   mm12.setFont(FontDefault);
        	   
        	   
        	   mm12.add("           (以下简称“甲方”)和承租方");
                	     String neme1 = contract.get("CORP_NAME_CN")+"";
                	    
                	     neme1 = neme1.trim();
                	     if(neme1.equals("")){
                		 
                		 neme1 = "____________________________________";
                		 mm12.add(neme1);
                		 
                	     }else{
                		 
                		 int le = neme1.length();
                		 String px ="                         ";
                		 
                    		 if(le<19){
                    		     
                    		     String pp = px.substring(0, Math.round(21-le));
                    		  
                    		     
                    		     neme1 =   pp +pp +  neme1 + pp + pp ;
                    		 }
                		 
                		 Chunk c461 = new Chunk(neme1,FontUnder); 
                		 mm12.add(c461);
                	     }
        	     
                	     mm12.add("(以下简称“乙");
	    
	    PdfPCell  Cell2 = new PdfPCell(mm12);

        	    
        	    Cell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        	    Cell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
        	    Cell2.setColspan(2);
        	    Cell2.setPaddingLeft(35);
        	    Cell2.setBorderWidthBottom(0);
        	    Cell2.setBorderWidthTop(0);
        	    
        	    
        	   tT.addCell(Cell2);
        	   
	    
        	   
	    tT.addCell(makeCellSetColspan2ForOne("            方”)双方就甲方出租本合同规定的合同正本及合同附件中记载的设备(以下简称租赁物)，乙",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            方从甲方处承租租赁物事宜，在平等互惠的基础上经友好协商达成以下协议并签订本合同(本",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            合同分为合同正本与合同附件，合同附件经甲、乙双方及卖方签字确认后与合同正本有同等",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            的法律效力)。",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));

	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellWithBorderLeftForOne("          出租方(甲方): " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	   
	     
	    tT.addCell(makeCellSetColspan2("                         "+Constants.COMPANY_NAME+"                                                                "+contract.get("CORP_NAME_CN")+"",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	   
	    tT.addCell(makeCellWithBorderLeftForOne("          法定代表人或授权人:  " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("法定代表人或授权人:  " ,PdfPCell.ALIGN_LEFT, FontDefault));
	  
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	 
	    tT.addCell(makeCellWithBorderLeftForOne("          日期: " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("日期: " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));	 
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	   
	    //后来修改
	    t=t+23;
	    for(;t<59;t++){
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));    
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("1",PdfPCell.ALIGN_CENTER, FontDefault,2));	   
	    	// tT.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,2));
	    }
	    tT.addCell(makeCellSetColspan2("1",PdfPCell.ALIGN_CENTER, FontDefault,2));
	    t+=1;
	    if(t==60){
			tT.addCell(makeCellSetColspan4("全国服务专线：400-928-1999",PdfPCell.ALIGN_RIGHT,FontDefault, 2));
	    }
	    //以上是后来修改的
	    document.add(tT);
	    document.add(Chunk.NEXTPAGE);
	    
//循环    连带保证人:  
//	    身份证号码:
//	    身份证地址:
//	    签约日期:
	   
	   
	    List pageList = new  ArrayList<Map>();
	    Map  pageMap  = new HashMap();
	    CREDITNATU =  (List) DataAccessor.query("creditVoucher.selectAND", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    CROP  =  (List) DataAccessor.query("creditVoucher.selectVND", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    	 
	    
	 
	    int cr = CROP.size();
	    int na = CREDITNATU.size();
	    
	    //整合担保人信息到List
	    
	    for(int n=0;n<na;n++){
    		natu = (Map) CREDITNATU.get(n);
    		natu.put("CUSTYPE", "NA");
    		 
    		pageList.add(natu);
    	     }
    	     
    	
    	     for(int m=0;m<cr;m++){
    		crp =(Map) CROP.get(m);
    		crp.put("CUSTYPE", "CR");
    		 
    		pageList.add(crp);
    		 
    	     }
    	     
    	     
    	     int listSize =  pageList.size() ;
    	     int pageN = ((Number)Math.floor( listSize/5)).intValue();  //页数
    
    	     int pageL = listSize%5;	//余数
    
    	     int p=0;		//页数标记	
    	     int m = 0;           //数据标记
	   
	 
	   
	//   String   urlpath =   context.request.getContextPath() +"/images/disagree.gif";
	//   urlpath =   (ExpContract.class.getResource("hes.jpg")).getPath();
	  
	//  Image  img1 =   Image.getInstance(urlpath);
	//  img1.setAlignment(Image.ALIGN_CENTER);
	//  img1.scaleAbsoluteHeight(18f);
	//  img1.scaleAbsoluteWidth(1f);
    	context.contextMap.put("dataType", "证件类型");
    	List natuTypeList = (List) DataAccessor.query(
    	 					"dataDictionary.queryDataDictionary", context.contextMap,
    	 					DataAccessor.RS_TYPE.LIST);
    	String flag = "";
	 //多页 
	    if(pageN >0){ 
		
		//多页循环开始
		for( ;p<pageN; p++){
		    
        		    PdfPTable tT20 = new PdfPTable(10);
        		    tT20.setWidthPercentage(100f); 
        		    tT20.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		 
        		    	 
        		    	     for(int n=0;n<5;n++){
        		    		
                		    		pageMap.clear();
                		    		
                		    		  m = 5*p + n;
                		    		
                		    		pageMap = (Map) pageList.get(m);
                		    		
                		    		if((pageMap.get("CUSTYPE")+"").equals("NA")){
                		    		    
                		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
                		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
                		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
                		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
                		    		    }
                		    		    tT20.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
											Map mapyy = (Map) natuTypeList.get(yy);
											int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
											if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
												flag = mapyy.get("FLAG").toString();
											}
										}
                		    		    tT20.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		}
                		    		
                		    		if((pageMap.get("CUSTYPE")+"").equals("CR")){
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		}
        		    	    
        		    	     }
        		    	 
        		    
        		    document.add(Chunk.NEWLINE);
        		 
        		    
        		    
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2( (p+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));
        		   
        		    tT20.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                	 
        		    tT20.addCell(makeCell3("对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
        		    tT20.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		   
        		    tT20.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,10));
        		    
        		    document.add(tT20);
        		    
        		    //另一页
        		    document.add(Chunk.NEXTPAGE);
		    
		} //多页循环结束
		
		//尾页
		if(pageL >0){
		    
		    
        		    PdfPTable tT19 = new PdfPTable(10);
        		    tT19.setWidthPercentage(100f); 
        		    tT19.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT19.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		   
        		    
        		    
                		    for(int n=(m+1) ;n < listSize; n++){
                	    		
                	    		    pageMap.clear();
                	    		    pageMap = (Map) pageList.get(n);
                	    		
                	    		    if((pageMap.get("CUSTYPE")+"").equals("NA")){
                	    		    
                		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
                		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
                		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
                		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
                		    		    }
                		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
											Map mapyy = (Map) natuTypeList.get(yy);
											int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
											if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
												flag = mapyy.get("FLAG").toString();
											}
										}
                        	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		    
                        	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                	    		    }
                	    		
                	    		    if((pageMap.get("CUSTYPE")+"").equals("CR")){
                	    		    
                        	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		    
                        	    		     
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                	    		}
                    	    
                		    }
		            //最后页  空字段补齐5个
		  
                    		    if(pageL>0){
                    			
                    			for( ; pageL<5 ;pageL++){
                    			    
                    			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    		    		
                    		    	 
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    		          
                    			}
                    		    }
                    		    
                    		    
                    		 document.add(Chunk.NEWLINE);
                		 
             		    
             		    
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		 
             		   
                    		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));
                    		
                    		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    	 
                    		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
                    		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		
                    		tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,10));
             		    
                    		document.add(tT19);
             		    
             		    //另一页
             		    document.add(Chunk.NEXTPAGE);
		    
		}//尾页结束
		
		 //多页 结束
		
		
		
	    }
	    
	    
	  //单页
	    else {
		
		

		    PdfPTable tT19 = new PdfPTable(10);
		    tT19.setWidthPercentage(100f); 
		    tT19.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
		    tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
		    tT19.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
		   
		    //有数据
		    if(pageL>0){
			
            		    for(int n=0 ;n < listSize; n++){
            	    		
            	    		    pageMap.clear();
            	    		    pageMap = (Map) pageList.get(n);
            	    		
            	    		    if((pageMap.get("CUSTYPE")+"").equals("NA")){
            		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
            		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
            		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
            		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
            		    		    }   
            		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
										Map mapyy = (Map) natuTypeList.get(yy);
										int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
										if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
											flag = mapyy.get("FLAG").toString();
										}
									}
                    	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		    
                    	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
            	    		    }
            	    		
            	    		    if((pageMap.get("CUSTYPE")+"").equals("CR")){
            	    		    
                    	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		    
                    	    		     
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
            	    		}
                	    
            		    }
        	            //最后页  空字段补齐4个
        	  
                		    
                			
                			for( ; pageL<5 ;pageL++){
                			    
                			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		
                		    	 
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		          
                			}
		    	}
        		    
        		    //无数据
        		    if(pageL == 0 & pageN == 0 ){    
        		    
        			  // 空字段补齐4个
        			
        			for( ; pageL<5 ;pageL++){
        			    
        			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    		
        		    	 
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		          
        			}
        				
        		    
        		    
        		    }
		    
        		 document.add(Chunk.NEWLINE);

        		for (int i = 1; i <=6; i++) {
            		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));					
				}
        		
        		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));

            		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            	 
            		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		
        		tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_RIGHT, FontDefault,10));
 		    
        		document.add(tT19);
 		    
 		    //另一页
 		    document.add(Chunk.NEXTPAGE);
		
	    }
	    
	    
	
	    if(pageL == 0 & pageN > 0  ){
		
		pageN = pageN -1;
	    }
	    
	    
	    //合同条款
	    
	    
	    PdfPTable tT30 = new PdfPTable(2);  
	    tT30.setWidthPercentage(100f);
	    tT30.addCell(makeCellSetColspan3("合同条款", PdfPCell.ALIGN_CENTER,fa,2));
	 
	    tT30.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
		   
	 
	    /*
	    
	    if(code.equals("0")){
			// 一般租赁 0
		 * 
	     */	
			  tT30.addCell(makeCellSetColspan2222("    第一条 租赁物",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("            甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买合同附表所记载的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         租赁物租予乙方，乙方则向甲方承租并使用该物件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、租赁物包括:全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、租赁物的购买:租赁物是乙方根据自己的需要，自主选定租赁物及生产商和卖方。乙方就租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         物的名称、规格、型号、性能、质量、数量、技术指标和品质、技术保证、售后服务和维护以及价格、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         交货、安装、验收时间等交易条件直接和卖方商定。甲方根据乙方的选择与要求与卖方签订购买合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         乙方同意合同附件中的购买合同中的全部条款，并在购买合同上签字。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方应对上述选择和决定承担全部责任，甲方对该选定不承担任何责任；乙方须向甲方提供",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         甲方认为有必要的各种批准许可证明。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             4、甲方不对租赁物的选定和品质作任何建议或保证，对租赁物的瑕疵不承担责任。对于任何与",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         租赁物的瑕疵有关的争议及赔偿应当由乙方与卖方之间自行解决，而不得牵涉甲方。有关购买租赁物应",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         交纳的税费由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			
			  tT30.addCell(makeCellSetColspan2222("     第二条 租赁物的所有权",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、在本合同租赁期间内，租赁物的所有权归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所，不得转让给第三人或允许他人使用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施向第三方销售、转让、转租租",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         赁物、不得向他人设置质押、抵押等担保。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			 
			   
			    tT30.addCell(makeCellSetColspan2222("   第三条 租赁物的交付",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           1、乙方在合同附表中载明的卖方处收取租赁物后，应在合同附件一载明的验收期限内自行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       对取得的租赁物实施检验，并应在三天内向甲方提交《租赁物验收证明书》。如乙方未按本款规定的时",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       间验收并出具《租赁物验收证明书》，甲方可视为租赁物已在符合乙方要求的状态下由乙方验收完毕，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       乙方已经接受该租赁物。上述期限届满之日即视为本合同起租日，否则甲方有权选择终止本合同并依据",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       本合同违约、保证条款进行索赔。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           2、租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，或卖方有其他违反买卖",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       合同的行为，乙方应该在三天内以书面形式通知甲方；并且，乙方应直接与卖方协商解决前述纠纷，并",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       在与卖方解决该纠纷后，及时向甲方提交《租赁物验收证明书》，乙方不得拖延时间（自收到租赁物之",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       日起不超过15天，如遇特殊情况应及时通知甲方）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           3、乙方拒收租赁物的，乙方应当赔偿由此给甲方带来的一切损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           4、乙方提交《租赁物验收证明书》后，即为认可租赁物已在符合要求的状态下由甲方交付完毕",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       ，此后不得再提出任何异议。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           5、乙方在向甲方提交《租赁物验收证明书》之日起，即可按照本合同的规定使用该租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           6、乙方取得租赁物后，应自行负责将租赁物安装至合同附表载明的场所。根据甲方的委托和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       确认，乙方与运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其他所需费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       全部由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  
			    
			    
			    tT30.addCell(makeCellSetColspan2222("    第四条 租赁期间",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("            1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("            2、承租方在本合同有效期内不得自行解除本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    
			    
			    tT30.addCell(makeCellSetColspan2222("    第五条 租赁物的瑕疵",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("             1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         的内容不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据购买合同的规定，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         由购买合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("             2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         卖方的原因以及其他不属于出租方的故意或重大过失引起而发生的事由，造成租赁物交付延迟或者不能",PdfPCell.ALIGN_LEFT, FontDefault2,2));	    
			    tT30.addCell(makeCellSetColspan2222("         交付时，甲方不承担任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲方的任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("             3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         ，以便于乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与卖方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         之间的各种直接交涉，甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         法律后果均由乙方承担并享受其利益。因卖方违反买卖合同而造成的一切损失由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));    
			    tT30.addCell(makeCellSetColspan2222("             4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));   
			    tT30.addCell(makeCellSetColspan2222("     第六条 	租金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
			    tT30.addCell(makeCellSetColspan2222( (pageN+3)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan3("                                    乙方应当按照附表第（7）项中规定的数额及支付条件向甲方支付租金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    tT30.addCell(makeCellSetColspan2222("     第七条 租赁物的保管及使用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，对安全",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          检查中发现的各种灾害事故隐患，在接到安全主管部门或甲方提出的整改通知书后，认真付诸实施。若",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物受到损害的，乙方应当积极采取抢救措施，使损失减少至最低程度，同时保护现场，并立即",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通知甲方，协助勘查。",PdfPCell.ALIGN_LEFT, FontDefault,2));
			    tT30.addCell(makeCellSetColspan2222("              3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收回或请求返还租赁物及请求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、由于租赁物自身或其设置保管、使用的原因,而对第三人造成人身伤害或者财产损害的,",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          甲方不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第八条 租赁物的保养及费用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          相应的维护和修理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          定期或者不定期的检查和进行其他一切维护、修护，并承担一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方应按照税法规定的税率承担因租金而产生的营业税，并与每次应当支付的租金一起付",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          给甲方。根据本合同向甲方支付的费用须缴纳增值税时，乙方应按照甲方的结算请求进行支付。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          （租赁期间如遇国家税收政策发生重大变化，所产生的税负增加，仍由乙方担负。）",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不负担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    tT30.addCell(makeCellSetColspan2222("     第九条 租赁物的灭失、损毁",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、保全措施、乙方的原因或其他任",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通常的损耗、减耗不适用本项。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 A. 将租赁物复原或修理至完好状态；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 B. 用与租赁物相同、性能相似的物件替换租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、租赁物灭失（包括不能修理或者侵害所有权）的情况，乙方应根据未付的租金金额向甲方支",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          付赔偿金，保证金由甲方没入。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支付赔",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          偿金额，同时，本合同自动终止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          方要将租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的资力等",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));			    
			    tT30.addCell(makeCellSetColspan2222("     第十条 	租赁物所有权变更的情形",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方在本合同规定的租赁期届满时，可选择买取租赁物或终止合同。乙方选择终止合同的，应在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁期届满2个月前以书面形式通知甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("     第十一条 租赁物的状态改变",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               乙方没有得到甲方的书面承诺，不得将租赁物附着在其他物件上，或改造其外观、性能、机能、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所产生的价值无",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           偿归属于租赁物的所有人即甲方，但由此产生的损害由乙方负无条件赔偿责任。属于租赁物，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           产生损害乙方负责无条件赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十二条 租赁物的检查",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转状况",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           及维护情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十三条 保险",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                甲方从起租日起向保险公司投保相应险种，保险人由甲方指定，保险公司以甲方为受益人，保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           费包含在租金中。在租赁期间，如乙方未按时支付租金，造成甲方不能按时对租赁物进行投保和续保",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           而造成的损失，乙方应承担赔偿责任;甲方价金未支付前、未投保前,本项租赁物危险负担责任仍由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("     第十四条 保险金的收取",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、发生事故时，乙方应立即通知甲方，并将领受保险金所需的一切文件交付给甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
			    tT30.addCell(makeCellSetColspan2222((pageN+4)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));				    
			    tT30.addCell(makeCellSetColspan3("                                     2、租赁物发生保险事故后获得赔偿时，由甲方领受保险金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("            如保险金不足以支付甲方损失的，乙方应当予以赔偿。如由于乙方的故意或重大过失造成保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("            公司不予理赔时，乙方应承担该事故的全部损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				    
			    tT30.addCell(makeCellSetColspan2222("     第十五条 租赁保证金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的同时向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           甲方预先支付合同附表规定的保证金额。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    //Modify by Michael 2012 7-4 融资合同改版
//			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           不得凭保证金免除其超出保证金部分的支付义务。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务,包括但不限",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           于已到期平均抵冲的租金及解除合同后至清偿日止依未平均抵冲前的日租金两倍的使用费、滞纳金、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           诉讼费、律师费及处理债权的相关费用,抵消后剩余的保证金作为乙方因违约所应支付给甲方的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                3、发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的裁量优先从该保证金帐户支付。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十六条 违约责任",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方有违反本合同条款及发生下列各项情形之一时，甲方无需催告通知即可解除本合同:",PdfPCell.ALIGN_LEFT, FontDefault2,2));//有违反本合同条款及
			    tT30.addCell(makeCellSetColspan2222("                   A. 发生一次或一次以上迟延支付租金时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B. 乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产、解散清算或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、扣押时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      乙方被卷入诉讼、仲裁或其他法律程序，可能给乙方的经营活动带来显著不利影响时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   C.乙方迁移住所前未通知甲方时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   D.乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   E.乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   F.乙方经营状况显著恶化，或有足够理由相信有此可能时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   G.本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   H.违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，但未在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     该期限内做出回应时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   I.发生与上述各项相当的其他事由时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   J.连带保证人有上述各项情形之一时。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙方承担。甲方收回租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      物时，租赁物的价值由双方确定或者由评估机构评估后确定；确定的价格不足以支付甲方损失",PdfPCell.ALIGN_LEFT, FontDefault2,2));		    
			    tT30.addCell(makeCellSetColspan2222("                      的，乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．乙方有义务支付已到期未支付及全部未到期的租金及由租赁物产生的其他一切费用，并对甲方承",PdfPCell.ALIGN_LEFT, FontDefault2,2));//已到期未支付及全部未到期的；，并对甲方承担相应的损害赔偿责任。
			    tT30.addCell(makeCellSetColspan2222("                      担相应的损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    //Add by Michael 2012 07-09 For 签呈改版合同
				tT30.addCell(makeCellSetColspan2222("                   C.承担因解除合同所产生的诉讼费、律师费及处理债权的相关费用。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                   D.甲方得申请法院委托具有相应资质的机构对租赁物进行评估、拍卖,拍卖不成的,依甲方所",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                      选定的评估机构评估确定的价格为准,确定的价格不足以支付本款的B款金额时,乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
			    
			    tT30.addCell(makeCellSetColspan2222("                 3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         以下情况，乙方应承担以下责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、取消）或者",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       在租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖合同（包括撤回要约）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并承担",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       相应的违约金（计算标准:购买方实际支付日起至实际收到乙方返还全部支付款项日，以日息万分",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       之五计算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 4、延迟支付而产生的违约金:乙方怠于向甲方支付本合同租金及其他相关费用时，或者甲方为乙方垫付",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
			    //tT30.addCell(makeCellSetColspan2222("         费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应按照应付的金额以每万元每日6元计算，向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第十七条 租赁物的违约返还",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的选择是否购买租赁物的权利。购买金额以附表(8)中载明的购买选择权为准。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以当",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           时的状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切费用和税款均由",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    
			    //Modify by Michael 2012 02-24 修改融资租赁合同内容
			    tT30.addCell(makeCellSetColspan2222("                3、本合同因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了租赁物通常损耗或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //tT30.addCell(makeCellSetColspan2222("                3、本合同在租赁期届满或者因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予以返还。运送租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //tT30.addCell(makeCellSetColspan2222("           租赁物通常损耗或甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222( (pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan3("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    
			    tT30.addCell(makeCellSetColspan2222("           物所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
//			    tT30.addCell(makeCellSetColspan2222("           以返还。运送租赁物所需的必要费用由乙方负担。本合同租赁期满或者因解除而终止时，甲方可以要求",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           乙方报废租赁物。甲方要求乙方在报废处理期限内处分租赁物的，乙方应立即将租赁物送交具有中国政",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
//			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
//			    tT30.addCell(makeCellSetColspan2222((pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault,2));
//			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));				    
//			    tT30.addCell(makeCellSetColspan3("                                   府认定资格的废弃处理单位，委托其在处理期限内处分租赁物，且将该单位出具的《租赁物回收证明 》",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           （或者有关废弃处理的合同及该单位的收据）的原件交付给甲方。如在废弃处理期间未能向甲方交付回",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           收证明（或者有关废弃处理的合同及该单位的收据）的，乙方应按照超过废弃处理期限的天数相应地向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    
//			    
//			    tT30.addCell(makeCellSetColspan2222("           甲方支付相当于日租费的违约金。租赁物报废时所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    tT30.addCell(makeCellSetColspan2222("                 4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           完毕前按照迟延天数支付相应的损害赔偿金，计算方法如下:每天应当支付相当于双倍的日租费作为损",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("           害赔偿金。同时遵守本合同的其他约定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第十八条 连带保证人",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产保全费、申请",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           执行费律师费、公告费、 评估费、拍卖费等。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                3、连带保证人保证的期间同乙方所负全部债务履行期限。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           已形成的债务向甲方主张免责或要求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           仍负全部责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始得",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           解除保证责任，以其他方式声明退保，均不生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十九条 甲方权利的转让",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			 
			    tT30.addCell(makeCellSetColspan2222("     第二十条 乙方提供必要的情况和资料",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方资产负债表、乙方利润表及其他的明细情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第二十一条 争议的解决",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
				/*以下zhangbo0723*/
				String decpId =contract.get("DECP_ID")==null?"":String.valueOf(contract.get("DECP_ID"));
			
						if(decpId.equals("3") || decpId.equals("8")){
							tT30.addCell(makeCellSetColspan2222( "          东莞分公司注册所在地的人民法院提起诉讼。", PdfPCell.ALIGN_LEFT, FontDefault2, 2));
						}else{
							tT30.addCell(makeCellSetColspan2222( "          注册所在地的人民法院提起诉讼。", PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
						}
				/*以上zhangbo0723*/
			    tT30.addCell(makeCellSetColspan2222("     第二十二条 合同及附件",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 合同附件与本合同具有同等法律效力。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("            本合同一式两份，双方各执一份，经双方盖章后生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    for (int i = 0; i <=19; i++) {
			    	tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
				}

			    tT30.addCell(makeCellSetColspan2222((pageN+6)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    document.add(tT30);
		    /*
		   
		
	    }else{
			//委托购买1
        	    
		

			  tT30.addCell(makeCellSetColspan2222("     第一条 租赁物",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买合同附件一所记载的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          租赁物租予乙方，乙方则向甲方承租并使用该物件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              1、租赁物包括:全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              2、租赁物的购买:租赁物是乙方根据自己的需要，自主选定租赁物及生产商和卖方。乙方就租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          物的名称、规格、型号、性能、质量、数量、技术指标和品质、技术保证、售后服务和维护以及价格、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          交货、安装、验收时间等交易条件直接和卖方商定。甲方根据乙方的选择与要求与卖方签订购买合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          乙方同意合同附件中的购买合同中的全部条款，并在购买合同上签字。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              3、乙方应对上述选择和决定承担全部责任，甲方对该选定不承担任何责任；乙方须向甲方提供",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          甲方认为有必要的各种批准许可证明。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              4、甲方不对租赁物的选定和品质作任何建议或保证，对租赁物的瑕疵不承担责任。对于任何与",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          租赁物的瑕疵有关的争议及赔偿应当由乙方与卖方之间自行解决，而不得牵涉甲方。有关购买租赁物应",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          交纳的税费由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			
			  tT30.addCell(makeCellSetColspan2222("     第二条 租赁物的所有权",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、在本合同租赁期间内，租赁物的所有权归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所，不得转让给第三人或允许他人使用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施向第三方销售、转让、转租租",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          赁物、不得向他人设置质押、抵押等担保。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			 
			   
			    tT30.addCell(makeCellSetColspan2222("     第三条	租赁物的交付",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方在合同附件一中载明的卖方处收取租赁物后，应在合同附件一载明的验收期限内自行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          对取得的租赁物实施检验，并应在三天内向甲方提交《租赁物验收证明书》。如乙方未按本款规定的时",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          间验收并出具《租赁物验收证明书》，甲方可视为租赁物已在符合乙方要求的状态下由乙方验收完毕，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方已经接受该租赁物。上述期限届满之日即视为本合同起租日，否则甲方有权选择终止本合同并依据",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          本合同违约、保证条款进行索赔。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，或卖方有其他违反买卖",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          合同的行为，乙方应该在三天内以书面形式通知甲方；并且，乙方应直接与卖方协商解决前述纠纷，并",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          在与卖方解决该纠纷后，向甲方提交《租赁物验收证明书》。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、乙方拒收租赁物的，乙方应当赔偿由此给甲方带来的一切损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方提交《租赁物验收证明书》后，即为认可租赁物已在符合要求的状态下由甲方交付完毕",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          ，此后不得再提出任何异议。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、乙方在向甲方提交《租赁物验收证明书》之日起，即可按照本合同的规定使用该租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              6、乙方取得租赁物后，应自行负责将租赁物安装至合同附件一载明的场所。根据甲方的委托和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         确认，乙方与运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其他所需费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         全部由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第四条 租赁期间",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、承租方在本合同有效期内不得自行解除本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第五条 租赁物的瑕疵",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的内容不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据购买合同的规定，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           由购买合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           卖方的原因以及其他不属于出租方的故意或重大过失引起而发生的事由，造成租赁物交付延迟或者不能",PdfPCell.ALIGN_LEFT, FontDefault2,2));	    
			    tT30.addCell(makeCellSetColspan2222("           交付时，甲方不承担任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲方的任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ，以便于乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与卖方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           之间的各种直接交涉，甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           法律后果均由乙方承担并享受其利益。因卖方违反买卖合同而造成的一切损失由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));    
			    tT30.addCell(makeCellSetColspan2222("               4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第六条 租赁物的保管及使用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+3)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan3("                                       1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，对安全",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          检查中发现的各种灾害事故隐患，在接到安全主管部门或甲方提出的整改通知书后，认真付诸实施。若",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物受到损害的，乙方应当积极采取抢救措施，使损失减少至最低程度，同时保护现场，并立即",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通知甲方，协助勘查。",PdfPCell.ALIGN_LEFT, FontDefault,2));
			    tT30.addCell(makeCellSetColspan2222("                3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收回或请求返还租赁物及请求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、由于租赁物自身或其设置、保管、使用的原因，而对第三人产生的损害应由乙方进行赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第七条 	租赁物的保养及费用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          相应的维护和修理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           定期或者不定期的检查和进行其他一切维护、修护，并承担一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方应按照税法规定的税率承担因租金而产生的营业税，并与每次应当支付的租金一起付",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           给甲方。根据本合同向甲方支付的费用须缴纳增值税时，乙方应按照甲方的结算请求进行支付。（租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           期间如遇国家税收政策发生重大变化，所产生的税负增加，仍由乙方担负。）",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           不负担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    tT30.addCell(makeCellSetColspan2222("     第八条	租赁物的灭失、损毁",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、保全措施、乙方的原因或其他任",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。通常的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          损耗、减耗不适用本项。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  A. 将租赁物复原或修理至完好状态；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  B. 用与租赁物相同、性能相似的物件替换租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               3、租赁物灭失（包括不能修理或者侵害所有权）的情况，乙方应根据未付的租金金额向甲方支",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          付赔偿金，保证金由甲方没入。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支付赔",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          偿金额，同时，本合同自动终止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          方要将租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的资力等",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));			    
			    tT30.addCell(makeCellSetColspan2222("     第九条	租赁物所有权变更的情形",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方在本合同规定的租赁期届满时，可选择买取租赁物或终止合同。乙方选择终止合同的，应在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁期届满2个月前以书面形式通知甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("     第十条	租赁物的状态改变",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方没有得到甲方的书面承诺，不得将租赁物附着在其他物件上，或改造其外观、性能、机能、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所产生的价值无偿归",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          属于租赁物，产生损害乙方负责无条件赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十一条 租赁物的检查",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转状况",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          及维护情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十二条 保险",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               甲方从起租日起向保险公司投保相应险种，保险人由甲方指定，保险公司以甲方为受益人，保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          费包含在租金中。在租赁期间，如乙方未按时支付租金，造成甲方不能按时对租赁物进行投保和续保",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          而造成的损失，乙方应承担赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("     第十三条 保险金的收取",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、发生事故时，乙方应立即通知甲方，并将领受保险金所需的一切文件交付给甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、租赁物发生保险事故后获得赔偿时，由甲方领受保险金。甲方领取保险金后应支付由此发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("          的损失。如保险金不足以支付甲方损失的，乙方应当予以赔偿。如由于乙方的故意或重大过失造成保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          公司不予理赔时，乙方应承担该事故的全部损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+4)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));					    
			    tT30.addCell(makeCellSetColspan3("                           第十四条  租赁保证金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的同时向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           甲方预先支付合同附表规定的保证金额。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           不得凭保证金免除其超出保证金部分的支付义务。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                3、发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的裁量优先从该保证金帐户支付。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十五条 违约责任",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、乙方发生下列各项情形之一时，甲方无需催告通知即可解除本合同：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  A. 发生一次或一次以上迟延支付租金时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  B. 乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产、解散清算或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、扣押时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     乙方被卷入诉讼、仲裁或其他法律程序，可能给乙方的经营活动带来显著不利影响时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  C.乙方迁移住所前未通知甲方时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  D.乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  E.乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  F.乙方经营状况显著恶化，或有足够理由相信有此可能时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  G.本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  H.违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，但未在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    该期限内做出回应时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  I.发生与上述各项相当的其他事由时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  J.连带保证人有上述各项情形之一时。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙方承担。甲方收回租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      物时，租赁物的价值由双方确定或者由评估机构评估后确定；确定的价格不足以支付甲方损失",PdfPCell.ALIGN_LEFT, FontDefault2,2));		    
			    tT30.addCell(makeCellSetColspan2222("                      的，乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．乙方有义务支付租金及由租赁物产生的其他一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          以下情况，乙方应承担以下责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、取消）或者",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      在租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖合同（包括撤回要约）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并承担",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      相应的违约金（计算标准:购买方实际支付日起至实际收到乙方返还全部支付款项日，以日息万分",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      之五计算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、延迟支付而产生的违约金:乙方怠于向甲方支付本合同相关费用时，或者甲方为乙方垫付费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    tT30.addCell(makeCellSetColspan2222("     第十六条  租赁物的返还",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 1、本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          的选择是否购买租赁物的权利。购买金额以附表(8)中载明的购买选择权为准。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以当",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          时的状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切费用和税款均由",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    
			    
			    tT30.addCell(makeCellSetColspan2222("                 3、本合同在租赁期届满或者因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物通常损耗或甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          以返还。运送租赁物所需的必要费用由乙方负担。本合同租赁期满或者因解除而终止时，甲方可以要求",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方报废租赁物。甲方要求乙方在报废处理期限内处分租赁物的，乙方应立即将租赁物送交具有中国政",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          府认定资格的废弃处理单位，委托其在处理期限内处分租赁物，且将该单位出具的《租赁物回收证明 》",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          （或者有关废弃处理的合同及该单位的收据）的原件交付给甲方。如在废弃处理期间未能向甲方交付回",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收证明（或者有关废弃处理的合同及该单位的收据）的，乙方应按照超过废弃处理期限的天数相应地向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          甲方支付相当于日租费的违约金。租赁物报废时所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan3("                         4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          完毕前按照迟延天数支付相应的损害金，计算方法如下:每天应当支付相当于双倍的日租费作为损害金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          同时遵守本合同的其他约定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第十七条  连带保证人",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产保全费、申请",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           执行费律师费、公告费、 评估费、拍卖费等。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 3、连带保证人保证的期间同乙方所负全部债务履行期限。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           已形成的债务向甲方主张免责或要求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                 5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           仍负全部责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始得",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           解除保证责任，以其他方式声明退保，均不生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第十八条 甲方权利的转让",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			 
			    tT30.addCell(makeCellSetColspan2222("      第十九条 乙方提供必要的情况和资料",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方资产负债表、乙方利润表及其他的明细情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("      第二十条 争议的解决",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           注册所在地的人民法院提起诉讼。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("      第二十一条 合同及附件",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                  合同附件与本合同具有同等法律效力。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          本合同一式两份，双方各执一份，签字后生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    		    
			    
			    for (int i = 1; i <=24; i++) {
			    	tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
				}
			    tT30.addCell(makeCellSetColspan2222((pageN+6)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
		    
		  
        	    
        	   
        	   
        	    document.add(tT30);
	    }
	      * 
		     */
	    
	    	}
	    document.close();
	    // 支付表PDF名字的定义
	    String strFileName = contract.get("CUST_CODE").toString() + ".pdf";
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
    
    public void expDirectContractPdf(Context context){
    	
	ByteArrayOutputStream baos = null;
	String[]  con = null;
	 
	Map contract =new HashMap();
	List  CREDITNATU = new ArrayList();
	List  CROP = new ArrayList();
	Map natu  = new HashMap();
	Map  crp =  new HashMap();
 	
	try {   
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
//			HeaderFooter footer = new HeaderFooter(new Phrase(" "), true);
//			footer.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
//			footer.setAlignment(Element.ALIGN_CENTER);
//			document.setFooter(footer);	        
	        
	        
	        
	        
	        // 打开文档
	        document.open();
	        

	    
	    	con= (String[]) context.contextMap.get("credtdxx");
	    	//
	    	
	    	//准备参数  add by Shen Qi 2012.03.05
	    	StringBuffer param=new StringBuffer();
	    	for(int i=0;con!=null&&i<con.length;i++) {
	    		param.append("'").append(con[i]).append("'");
	    		if(i!=con.length-1) {
	    			param.append(",");
	    		}
	    	}
	    	Map<String,String> paramMap=new HashMap<String,String>();
	    	paramMap.put("RECT_ID", param.toString());
	    	List<String> rectId=(List<String>)DataAccessor.query("rentContract.checkIsAudit", paramMap, DataAccessor.RS_TYPE.LIST);
    	    
    	    Map<String,String> checkIsAudit=new HashMap<String, String>();
    	    for(int i=0;rectId!=null&&i<rectId.size();i++) {
    	    	checkIsAudit.put(rectId.get(i),rectId.get(i));
    	    }
    	    
	    	for(int ii=0; ii< con.length;ii++){
	    		int t=0;
	    	context.contextMap.put("credit_id",  con[ii]);
	    	
	    	//add by ShenQi 插入系统日志
//	    	BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),checkIsAudit.get(con[ii])==null?DataUtil.longUtil("0"):DataUtil.longUtil(con[ii]),
//	    							   "导出 融资租赁合同",
//	    							   "合同浏览导出 融资租赁合同",
//	    							   null,
//	    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
//	    							   1,
//	    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
//	    							   DataUtil.longUtil(0));
	    	
	    	BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(con[ii]),checkIsAudit.get(con[ii])==null?DataUtil.longUtil("0"):DataUtil.longUtil(con[ii]),
	    							   		 "导出 融资租赁合同",
    							   		 	 "合同浏览导出 融资租赁合同",
    							   		 	 null,
    							   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
    							   		 	 1,
    							   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
    							   		 	 DataUtil.longUtil(0),
    							   		 	 context.getRequest().getRemoteAddr());
	    	//当合同表中的RECT_STATUS为1时表明为以复核的   取数据应该尽量从合同中取
	    	//getCreditCCorpByCreditIdUpdateCon代表是否存在以复核的数据   如果存在则取出   如果不存在则取报告中的数据
	    	contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditIdUpdateCon", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	if(contract!=null)
	    	{
	    		if(contract.size()>0)
	    		{
	    			;
	    		}
	    		else
	    		{
	    			contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		}
	    	}
	    	else
	    	{
	    		contract = (Map) DataAccessor.query("creditCustomerCorp.getCreditCCorpByCreditId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	}
	    	
	    
        	    if(contract == null){
        		
        		contract =new HashMap();
        	   
        		contract.put("LEASE_CODE", "  ____ ");
        		contract.put("CORP_NAME_CN", " ____  ");
        		contract.put("LEGAL_PERSON", " ____  ");
        		contract.put("REGISTERED_OFFICE_ADDRESS", " ");
        		contract.put("COMMON_OFFICE_ADDRESS", " ");
        		contract.put("POSTCODE", " ");
        		contract.put("TELEPHONE", " ");
        		contract.put("FAX", " ");
        	    contract.put("CUST_CODE", " ");
        	    //contract.put("CONTRACT_TYPE", "1");
        	    contract.put("CONTRACT_TYPE", context.contextMap.get("contractType"));    
        	    }
	    
	    String code = contract.get("CONTRACT_TYPE")+"";
	    

	    float[] widthsPPCa = { 3f };
	    PdfPTable tT = new PdfPTable(2);
	    
	    tT.setWidthPercentage(100f);
	    tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,2));
	 
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));

	  
	    tT.addCell(makeCellSetColspan2("融资租赁合同",PdfPCell.ALIGN_CENTER, fa,2));
	  
	      
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	  
	    t=t+5;
	     
	    String Lcode = contract.get("LEASE_CODE") +"";
	    	Lcode=	Lcode.trim();
	    if(Lcode.equals("")){
		Lcode = "           ";
	    }
	    tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_RIGHT, FontDefault,2));
	   
	    tT.addCell(makeCellWithBorderLeft(" " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRight("合同编号:    " +Lcode ,PdfPCell.ALIGN_CENTER,  FontDefault));
	    
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	 
	    tT.addCell(makeCellSetColspan2ForOne("          合同签订日:    20____年____月____日" ,PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
    	String contractAddress = "中华人民共和国";  	
		String decpId =contract.get("DECP_ID")==null?"":String.valueOf(contract.get("DECP_ID"));
		if(decpId.equals("3") || decpId.equals("8")){
			contractAddress = "中华人民共和国东莞市长安镇长青南路303号";
		}
	  
	    tT.addCell(makeCellSetColspan2ForOne("          合同签订地:    " + contractAddress ,PdfPCell.ALIGN_LEFT, FontDefault,2));
	   
	   
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));


		int companyCode  = LeaseUtil.getCompanyCodeByCreditId(con[ii]);
		String contractType = LeaseUtil.getContractTypeByCreditId(con[ii]);
	    String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
		String registeredAddress = LeaseUtil.getCompanyRegisteredAddressByCompanyCode(1);
		String address = LeaseUtil.getCompanyAddressByCompanyCode(1);
		String postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(1);
		String telephone = LeaseUtil.getCompanyTelephoneByCompanyCode(1);
		String fax = LeaseUtil.getCompanyFaxByCompanyCode(1);
		if("7".equals(contractType)){
		    companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
			registeredAddress = LeaseUtil.getCompanyRegisteredAddressByCompanyCode(companyCode);
			address = LeaseUtil.getCompanyAddressByCompanyCode(companyCode);
			postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(companyCode);
			telephone = LeaseUtil.getCompanyTelephoneByCompanyCode(companyCode);
			fax = LeaseUtil.getCompanyFaxByCompanyCode(companyCode);
		}
	 
	    tT.addCell(makeCellWithBorderLeftForOne("            出租方(甲方):    "+companyName,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):    "+ contract.get("CORP_NAME_CN")+"",PdfPCell.ALIGN_LEFT, FontDefault22));
	     
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            法定代表或负责人:    "+Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("法定代表或负责人:    "+contract.get("LEGAL_PERSON")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	  
	  
	    tT.addCell(makeCellWithBorderLeftForOne("            注册地址:    "+registeredAddress,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("注册地址:    "+contract.get("REGISTERED_OFFICE_ADDRESS")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            实际经营地:    ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("实际经营地:    ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	    
	    tT.addCell(makeCellWithBorderLeftForOne("            "+address,PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne(""+contract.get("COMMON_OFFICE_ADDRESS")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	   
	    tT.addCell(makeCellWithBorderLeftForOne("            邮政编码:    "+postCode+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("邮政编码:    "+contract.get("POSTCODE")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    
	   
	     
	    tT.addCell(makeCellWithBorderLeftForOne("            电话号码:    "+telephone+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("电话号码:    "+contract.get("TELEPHONE")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	 
	    tT.addCell(makeCellWithBorderLeftForOne("            传真号码:    "+fax+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	    tT.addCell(makeCellWithBorderRightForOne("传真号码:    "+contract.get("FAX")+" ",PdfPCell.ALIGN_LEFT, FontDefault22));
	   
	     

	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));  
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));	  
	    
	    t=t+26;
	    
  Paragraph mm11 = new Paragraph();
	    
	    mm11.setFont(FontDefault);
	   
	    mm11.add("                      本合同的租赁实指中国合同法规定的融资租赁形式。出租方 ");
	    Chunk c361 = new Chunk( companyName ,FontUnder); 
	    mm11.add(c361);
	    
	    PdfPCell objCell = new PdfPCell(mm11);
	    
	    
	    objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
		objCell.setColspan(2);
		objCell.setPaddingLeft(35);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthTop(0);
	    
	    
	   tT.addCell(objCell);
	   
	   
	   
	   Paragraph mm12 = new Paragraph();
	    
        	   mm12.setFont(FontDefault);
        	   
        	   
        	   mm12.add("           (以下简称“甲方”)和承租方");
                	     String neme1 = contract.get("CORP_NAME_CN")+"";
                	    
                	     neme1 = neme1.trim();
                	     if(neme1.equals("")){
                		 
                		 neme1 = "____________________________________";
                		 mm12.add(neme1);
                		 
                	     }else{
                		 
                		 int le = neme1.length();
                		 String px ="                         ";
                		 
                    		 if(le<19){
                    		     
                    		     String pp = px.substring(0, Math.round(21-le));
                    		  
                    		     
                    		     neme1 =   pp +pp +  neme1 + pp + pp ;
                    		 }
                		 
                		 Chunk c461 = new Chunk(neme1,FontUnder); 
                		 mm12.add(c461);
                	     }
        	     
                	     mm12.add("(以下简称“乙");
	    
	    PdfPCell  Cell2 = new PdfPCell(mm12);

        	    
        	    Cell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        	    Cell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
        	    Cell2.setColspan(2);
        	    Cell2.setPaddingLeft(35);
        	    Cell2.setBorderWidthBottom(0);
        	    Cell2.setBorderWidthTop(0);
        	    
        	    
        	   tT.addCell(Cell2);
        	   
	    
        	   
	    tT.addCell(makeCellSetColspan2ForOne("            方”)双方就甲方出租本合同规定的合同正本及合同附件中记载的设备(以下简称租赁物)，乙",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            方从甲方处承租租赁物事宜，在平等互惠的基础上经友好协商达成以下协议并签订本合同(本",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            合同分为合同正本与合同附件，合同附件经甲、乙双方及卖方签字确认后与合同正本有同等",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2ForOne("            的法律效力)。",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));

	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellWithBorderLeftForOne("          出租方(甲方): " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):  ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	   
	     
	    tT.addCell(makeCellSetColspan2("                         "+companyName+"                                                                "+contract.get("CORP_NAME_CN")+"",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	   
	    tT.addCell(makeCellWithBorderLeftForOne("          法定代表人或授权人:  " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("法定代表人或授权人:  " ,PdfPCell.ALIGN_LEFT, FontDefault));
	  
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    
	 
	    tT.addCell(makeCellWithBorderLeftForOne("          日期: " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    tT.addCell(makeCellWithBorderRightForOne("日期: " ,PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));	 
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	   
	    //后来修改
	    t=t+23;
	    for(;t<59;t++){
	    tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));    
	    	// tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
	    	// tT.addCell(makeCellSetColspan2("1",PdfPCell.ALIGN_CENTER, FontDefault,2));	   
	    	// tT.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,2));
	    }
	    tT.addCell(makeCellSetColspan2("1",PdfPCell.ALIGN_CENTER, FontDefault,2));
	    t+=1;
	    if(t==60){
			tT.addCell(makeCellSetColspan4("全国服务专线：400-928-1999",PdfPCell.ALIGN_RIGHT,FontDefault, 2));
	    }
	    //以上是后来修改的
	    document.add(tT);
	    document.add(Chunk.NEXTPAGE);
	    
//循环    连带保证人:  
//	    身份证号码:
//	    身份证地址:
//	    签约日期:
	   
	   
	    List pageList = new  ArrayList<Map>();
	    Map  pageMap  = new HashMap();
	    CREDITNATU =  (List) DataAccessor.query("creditVoucher.selectAND", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    CROP  =  (List) DataAccessor.query("creditVoucher.selectVND", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    	 
	    
	 
	    int cr = CROP.size();
	    int na = CREDITNATU.size();
	    
	    //整合担保人信息到List
	    
	    for(int n=0;n<na;n++){
    		natu = (Map) CREDITNATU.get(n);
    		natu.put("CUSTYPE", "NA");
    		 
    		pageList.add(natu);
    	     }
    	     
    	
    	     for(int m=0;m<cr;m++){
    		crp =(Map) CROP.get(m);
    		crp.put("CUSTYPE", "CR");
    		 
    		pageList.add(crp);
    		 
    	     }
    	     
    	     
    	     int listSize =  pageList.size() ;
    	     int pageN = ((Number)Math.floor( listSize/5)).intValue();  //页数
    
    	     int pageL = listSize%5;	//余数
    
    	     int p=0;		//页数标记	
    	     int m = 0;           //数据标记
	   
	 
	   
	//   String   urlpath =   context.request.getContextPath() +"/images/disagree.gif";
	//   urlpath =   (ExpContract.class.getResource("hes.jpg")).getPath();
	  
	//  Image  img1 =   Image.getInstance(urlpath);
	//  img1.setAlignment(Image.ALIGN_CENTER);
	//  img1.scaleAbsoluteHeight(18f);
	//  img1.scaleAbsoluteWidth(1f);
    	context.contextMap.put("dataType", "证件类型");
    	List natuTypeList = (List) DataAccessor.query(
    	 					"dataDictionary.queryDataDictionary", context.contextMap,
    	 					DataAccessor.RS_TYPE.LIST);
    	String flag = "";
	 //多页 
	    if(pageN >0){ 
		
		//多页循环开始
		for( ;p<pageN; p++){
		    
        		    PdfPTable tT20 = new PdfPTable(10);
        		    tT20.setWidthPercentage(100f); 
        		    tT20.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		 
        		    	 
        		    	     for(int n=0;n<5;n++){
        		    		
                		    		pageMap.clear();
                		    		
                		    		  m = 5*p + n;
                		    		
                		    		pageMap = (Map) pageList.get(m);
                		    		
                		    		if((pageMap.get("CUSTYPE")+"").equals("NA")){
                		    		    
                		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
                		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
                		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
                		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
                		    		    }
                		    		    tT20.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
											Map mapyy = (Map) natuTypeList.get(yy);
											int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
											if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
												flag = mapyy.get("FLAG").toString();
											}
										}
                		    		    tT20.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		}
                		    		
                		    		if((pageMap.get("CUSTYPE")+"").equals("CR")){
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    
                		    		    
                		    		    
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		    tT20.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		}
        		    	    
        		    	     }
        		    	 
        		    
        		    document.add(Chunk.NEWLINE);
        		 
        		    
        		    
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
        		    tT20.addCell(makeCellSetColspan2( (p+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));
        		   
        		    tT20.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                	 
        		    tT20.addCell(makeCell3("对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT20.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
        		    tT20.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		   
        		    tT20.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,10));
        		    
        		    document.add(tT20);
        		    
        		    //另一页
        		    document.add(Chunk.NEXTPAGE);
		    
		} //多页循环结束
		
		//尾页
		if(pageL >0){
		    
		    
        		    PdfPTable tT19 = new PdfPTable(10);
        		    tT19.setWidthPercentage(100f); 
        		    tT19.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    tT19.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		   
        		    
        		    
                		    for(int n=(m+1) ;n < listSize; n++){
                	    		
                	    		    pageMap.clear();
                	    		    pageMap = (Map) pageList.get(n);
                	    		
                	    		    if((pageMap.get("CUSTYPE")+"").equals("NA")){
                	    		    
                		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
                		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
                		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
                		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
                		    		    }
                		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
											Map mapyy = (Map) natuTypeList.get(yy);
											int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
											if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
												flag = mapyy.get("FLAG").toString();
											}
										}
                        	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		    
                        	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                	    		    }
                	    		
                	    		    if((pageMap.get("CUSTYPE")+"").equals("CR")){
                	    		    
                        	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		    
                        	    		     
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                        	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                	    		}
                    	    
                		    }
		            //最后页  空字段补齐5个
		  
                    		    if(pageL>0){
                    			
                    			for( ; pageL<5 ;pageL++){
                    			    
                    			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    		    		
                    		    	 
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    		          
                    			}
                    		    }
                    		    
                    		    
                    		 document.add(Chunk.NEWLINE);
                		 
             		    
             		    
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));
                    		 
             		   
                    		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));
                    		
                    		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    	 
                    		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
                    		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
                    		
                    		tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_CENTER, FontDefault,10));
             		    
                    		document.add(tT19);
             		    
             		    //另一页
             		    document.add(Chunk.NEXTPAGE);
		    
		}//尾页结束
		
		 //多页 结束
		
		
		
	    }
	    
	    
	  //单页
	    else {
		
		

		    PdfPTable tT19 = new PdfPTable(10);
		    tT19.setWidthPercentage(100f); 
		    tT19.addCell(makeCellSetColspan3("     " , PdfPCell.ALIGN_LEFT,FontDefault,10));
		    tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
		    tT19.addCell(makeCellSetColspan2("     ", PdfPCell.ALIGN_LEFT,FontDefault,10));
		   
		    //有数据
		    if(pageL>0){
			
            		    for(int n=0 ;n < listSize; n++){
            	    		
            	    		    pageMap.clear();
            	    		    pageMap = (Map) pageList.get(n);
            	    		
            	    		    if((pageMap.get("CUSTYPE")+"").equals("NA")){
            		    		    if(pageMap.get("CUST_NAME").equals(" ") && pageMap.get("NATU_IDCARD").equals(" ") && pageMap.get("NATU_IDCARD_ADDRESS").equals(" ")){
            		    		    	pageMap.put("CUST_NAME",pageMap.get("NATU_MATE_NAME"));
            		    		    	pageMap.put("NATU_IDCARD",pageMap.get("NATU_MATE_IDCARD"));
            		    		    	pageMap.put("NATU_IDCARD_ADDRESS",pageMap.get("NATU_MATE_IDCARD_ADDRESS"));
            		    		    }   
            		    		    for (int yy = 0; yy < natuTypeList.size(); yy++) {
										Map mapyy = (Map) natuTypeList.get(yy);
										int codeyy = Integer.parseInt(mapyy.get("CODE").toString());
										if (Integer.parseInt(pageMap.get("FLAGPERMIT").toString())==codeyy) {
											flag = mapyy.get("FLAG").toString();
										}
									}
                    	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("CUST_NAME")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                "+flag+":   " +pageMap.get("NATU_IDCARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                身份证地址:   " +pageMap.get("NATU_IDCARD_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		    
                    	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("      ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("       ", PdfPCell.ALIGN_LEFT,FontDefault,10));
            	    		    }
            	    		
            	    		    if((pageMap.get("CUSTYPE")+"").equals("CR")){
            	    		    
                    	    		     tT19.addCell(makeCellSetColspan2("                连带保证人:   " +pageMap.get("LEGAL_PERSON")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                营业执照编号:   " +pageMap.get("LEGAL_ID_CARD")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                注册地址:   " +pageMap.get("LEGAL_HOME_ADDRESS")+"    ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                法人代表: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		    
                    	    		     
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                    	    		     tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
            	    		}
                	    
            		    }
        	            //最后页  空字段补齐4个
        	  
                		    
                			
                			for( ; pageL<5 ;pageL++){
                			    
                			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
                		    		
                		    	 
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
                		          
                			}
		    	}
        		    
        		    //无数据
        		    if(pageL == 0 & pageN == 0 ){    
        		    
        			  // 空字段补齐4个
        			
        			for( ; pageL<5 ;pageL++){
        			    
        			    tT19.addCell(makeCellSetColspan2("                连带保证人:   "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                身份证号码:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                身份证地址:   " , PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("                签约日期: "  , PdfPCell.ALIGN_LEFT,FontDefault,10));
        		    		
        		    	 
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        			    tT19.addCell(makeCellSetColspan2("   ", PdfPCell.ALIGN_LEFT,FontDefault,10));
        		          
        			}
        				
        		    
        		    
        		    }
		    
        		 document.add(Chunk.NEWLINE);

        		for (int i = 1; i <=6; i++) {
            		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));					
				}
        		
        		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));

            		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            	 
            		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		
        		tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_RIGHT, FontDefault,10));
 		    
        		document.add(tT19);
 		    
 		    //另一页
 		    document.add(Chunk.NEXTPAGE);
		
	    }
	    
	    
	
	    if(pageL == 0 & pageN > 0  ){
		
		pageN = pageN -1;
	    }
	    
	    
	    //合同条款
	    
	    
	    PdfPTable tT30 = new PdfPTable(2);  
	    tT30.setWidthPercentage(100f);
	    tT30.addCell(makeCellSetColspan3("合同条款", PdfPCell.ALIGN_CENTER,fa,2));
	 
	    tT30.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
		   
	 
	    /*
	    
	    if(code.equals("0")){
			// 一般租赁 0
		 * 
	     */	
			  tT30.addCell(makeCellSetColspan2222("    第一条 租赁物",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("            甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买合同附表所记载的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         租赁物租予乙方，乙方则向甲方承租并使用该物件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、租赁物包括:全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、租赁物的购买:租赁物是乙方根据自己的需要，自主选定租赁物及生产商和卖方。乙方就租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         物的名称、规格、型号、性能、质量、数量、技术指标和品质、技术保证、售后服务和维护以及价格、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         交货、安装、验收时间等交易条件直接和卖方商定。甲方根据乙方的选择与要求与卖方签订购买合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         乙方同意合同附件中的购买合同中的全部条款，并在购买合同上签字。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方应对上述选择和决定承担全部责任，甲方对该选定不承担任何责任；乙方须向甲方提供",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         甲方认为有必要的各种批准许可证明。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             4、甲方不对租赁物的选定和品质作任何建议或保证，对租赁物的瑕疵不承担责任。对于任何与",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         租赁物的瑕疵有关的争议及赔偿应当由乙方与卖方之间自行解决，而不得牵涉甲方。有关购买租赁物应",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         交纳的税费由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			
			  tT30.addCell(makeCellSetColspan2222("     第二条 租赁物的所有权",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、在本合同租赁期间内，租赁物的所有权归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所，不得转让给第三人或允许他人使用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施向第三方销售、转让、转租租",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("         赁物、不得向他人设置质押、抵押等担保。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			 
			   
			    tT30.addCell(makeCellSetColspan2222("   第三条 租赁物的交付",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           1、乙方在合同附表中载明的卖方处收取租赁物后，应在合同附件一载明的验收期限内自行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       对取得的租赁物实施检验，并应在三天内向甲方提交《租赁物验收证明书》。如乙方未按本款规定的时",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       间验收并出具《租赁物验收证明书》，甲方可视为租赁物已在符合乙方要求的状态下由乙方验收完毕，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       乙方已经接受该租赁物。上述期限届满之日即视为本合同起租日，否则甲方有权选择终止本合同并依据",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       本合同违约、保证条款进行索赔。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           2、租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，或卖方有其他违反买卖",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       合同的行为，乙方应该在三天内以书面形式通知甲方；并且，乙方应直接与卖方协商解决前述纠纷，并",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       在与卖方解决该纠纷后，及时向甲方提交《租赁物验收证明书》，乙方不得拖延时间（自收到租赁物之",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       日起不超过15天，如遇特殊情况应及时通知甲方）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           3、乙方拒收租赁物的，乙方应当赔偿由此给甲方带来的一切损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           4、乙方提交《租赁物验收证明书》后，即为认可租赁物已在符合要求的状态下由甲方交付完毕",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       ，此后不得再提出任何异议。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           5、乙方在向甲方提交《租赁物验收证明书》之日起，即可按照本合同的规定使用该租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           6、乙方取得租赁物后，应自行负责将租赁物安装至合同附表载明的场所。根据甲方的委托和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       确认，乙方与运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其他所需费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("       全部由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  
			    
			    
			    tT30.addCell(makeCellSetColspan2222("    第四条 租赁期间",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("            1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("            2、承租方在本合同有效期内不得自行解除本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    
			    
			    tT30.addCell(makeCellSetColspan2222("    第五条 租赁物的瑕疵",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("             1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         的内容不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据购买合同的规定，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         由购买合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("             2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         卖方的原因以及其他不属于出租方的故意或重大过失引起而发生的事由，造成租赁物交付延迟或者不能",PdfPCell.ALIGN_LEFT, FontDefault2,2));	    
			    tT30.addCell(makeCellSetColspan2222("         交付时，甲方不承担任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲方的任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("             3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         ，以便于乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与卖方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         之间的各种直接交涉，甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         法律后果均由乙方承担并享受其利益。因卖方违反买卖合同而造成的一切损失由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));    
			    tT30.addCell(makeCellSetColspan2222("             4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));   
			    tT30.addCell(makeCellSetColspan2222("     第六条 	租金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
			    tT30.addCell(makeCellSetColspan2222( (pageN+3)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan3("                                    乙方应当按照附表第（7）项中规定的数额及支付条件向甲方支付租金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    tT30.addCell(makeCellSetColspan2222("     第七条 租赁物的保管及使用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，对安全",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          检查中发现的各种灾害事故隐患，在接到安全主管部门或甲方提出的整改通知书后，认真付诸实施。若",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物受到损害的，乙方应当积极采取抢救措施，使损失减少至最低程度，同时保护现场，并立即",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通知甲方，协助勘查。",PdfPCell.ALIGN_LEFT, FontDefault,2));
			    tT30.addCell(makeCellSetColspan2222("              3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收回或请求返还租赁物及请求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、由于租赁物自身或其设置保管、使用的原因,而对第三人造成人身伤害或者财产损害的,",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          甲方不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第八条 租赁物的保养及费用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          相应的维护和修理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          定期或者不定期的检查和进行其他一切维护、修护，并承担一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方应按照税法规定承担就融资租赁交易所生的各种税费，并依本合同有关税费的规定履行完税",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          或缴款的义务。根据本合同向甲方支付的费用须缴纳增值税或相关税费时，乙方应按照甲方的结算请求",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          进行支付。租赁期间如遇国家税收政策发生重大变化，所产生的税负增加，仍由乙方担负。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不负担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    tT30.addCell(makeCellSetColspan2222("     第九条 租赁物的灭失、损毁",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、保全措施、乙方的原因或其他任",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通常的损耗、减耗不适用本项。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 A. 将租赁物复原或修理至完好状态；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 B. 用与租赁物相同、性能相似的物件替换租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、租赁物灭失（包括不能修理或者侵害所有权）的情况，乙方应根据未付的租金金额向甲方支",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          付赔偿金，保证金由甲方没入。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支付赔",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          偿金额，同时，本合同自动终止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          方要将租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的资力等",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));			    
			    tT30.addCell(makeCellSetColspan2222("     第十条 	租赁物所有权变更的情形",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方在本合同规定的租赁期届满时，可选择买取租赁物或终止合同。乙方选择终止合同的，应在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁期届满2个月前以书面形式通知甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("     第十一条 租赁物的状态改变",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               乙方没有得到甲方的书面承诺，不得将租赁物附着在其他物件上，或改造其外观、性能、机能、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所产生的价值无",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           偿归属于租赁物的所有人即甲方，但由此产生的损害由乙方负无条件赔偿责任。属于租赁物，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           产生损害乙方负责无条件赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十二条 租赁物的检查",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转状况",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           及维护情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十三条 保险",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                甲方从起租日起向保险公司投保相应险种，保险人由甲方指定，保险公司以甲方为受益人，保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           费包含在租金中。在租赁期间，如乙方未按时支付租金，造成甲方不能按时对租赁物进行投保和续保",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           而造成的损失，乙方应承担赔偿责任;甲方价金未支付前、未投保前,本项租赁物危险负担责任仍由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("     第十四条 保险金的收取",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、发生事故时，乙方应立即通知甲方，并将领受保险金所需的一切文件交付给甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
			    tT30.addCell(makeCellSetColspan2222((pageN+4)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));				    
			    tT30.addCell(makeCellSetColspan3("                                     2、租赁物发生保险事故后获得赔偿时，由甲方领受保险金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("            如保险金不足以支付甲方损失的，乙方应当予以赔偿。如由于乙方的故意或重大过失造成保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("            公司不予理赔时，乙方应承担该事故的全部损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				    
			    tT30.addCell(makeCellSetColspan2222("     第十五条 租赁保证金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的同时向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           甲方预先支付合同附表规定的保证金额。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    //Modify by Michael 2012 7-4 融资合同改版
//			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           不得凭保证金免除其超出保证金部分的支付义务。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务,包括但不限",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           于已到期平均抵冲的租金及解除合同后至清偿日止依未平均抵冲前的日租金两倍的使用费、滞纳金、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           诉讼费、律师费及处理债权的相关费用,抵消后剩余的保证金作为乙方因违约所应支付给甲方的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                3、发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的裁量优先从该保证金帐户支付。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十六条 违约责任",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方有违反本合同条款及发生下列各项情形之一时，甲方无需催告通知即可解除本合同:",PdfPCell.ALIGN_LEFT, FontDefault2,2));//有违反本合同条款及
			    tT30.addCell(makeCellSetColspan2222("                   A. 发生一次或一次以上迟延支付租金时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B. 乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产、解散清算或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、扣押时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      乙方被卷入诉讼、仲裁或其他法律程序，可能给乙方的经营活动带来显著不利影响时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   C.乙方迁移住所前未通知甲方时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   D.乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   E.乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   F.乙方经营状况显著恶化，或有足够理由相信有此可能时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   G.本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   H.违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，但未在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     该期限内做出回应时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   I.发生与上述各项相当的其他事由时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   J.连带保证人有上述各项情形之一时。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙方承担。甲方收回租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      物时，租赁物的价值由双方确定或者由评估机构评估后确定；确定的价格不足以支付甲方损失",PdfPCell.ALIGN_LEFT, FontDefault2,2));		    
			    tT30.addCell(makeCellSetColspan2222("                      的，乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．乙方有义务支付已到期未支付及全部未到期的租金及由租赁物产生的其他一切费用，并对甲方承",PdfPCell.ALIGN_LEFT, FontDefault2,2));//已到期未支付及全部未到期的；，并对甲方承担相应的损害赔偿责任。
			    tT30.addCell(makeCellSetColspan2222("                      担相应的损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    //Add by Michael 2012 07-09 For 签呈改版合同
				tT30.addCell(makeCellSetColspan2222("                   C.承担因解除合同所产生的诉讼费、律师费及处理债权的相关费用。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                   D.甲方得申请法院委托具有相应资质的机构对租赁物进行评估、拍卖,拍卖不成的,依甲方所",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                      选定的评估机构评估确定的价格为准,确定的价格不足以支付本款的B款金额时,乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
			    
			    tT30.addCell(makeCellSetColspan2222("                 3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         以下情况，乙方应承担以下责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、取消）或者",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       在租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖合同（包括撤回要约）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并承担",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       相应的违约金（计算标准:购买方实际支付日起至实际收到乙方返还全部支付款项日，以日息万分",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                       之五计算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 4、延迟支付而产生的违约金:乙方怠于向甲方支付本合同租金及其他相关费用时，或者甲方为乙方垫付",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
			    //tT30.addCell(makeCellSetColspan2222("         费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应按照应付的金额以每万元每日6元计算，向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第十七条 租赁物的违约返还",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的选择是否购买租赁物的权利。购买金额以附表(8)中载明的购买选择权为准。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以当",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           时的状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切费用和税款均由",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    
			    //Modify by Michael 2012 02-24 修改融资租赁合同内容
			    tT30.addCell(makeCellSetColspan2222("                3、本合同因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了租赁物通常损耗或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //tT30.addCell(makeCellSetColspan2222("                3、本合同在租赁期届满或者因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222( (pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan3("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan2222("           甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予以返还。运送租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    //tT30.addCell(makeCellSetColspan2222("           租赁物通常损耗或甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    
			    tT30.addCell(makeCellSetColspan2222("           物所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
//			    tT30.addCell(makeCellSetColspan2222("           以返还。运送租赁物所需的必要费用由乙方负担。本合同租赁期满或者因解除而终止时，甲方可以要求",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           乙方报废租赁物。甲方要求乙方在报废处理期限内处分租赁物的，乙方应立即将租赁物送交具有中国政",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
//			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));		
//			    tT30.addCell(makeCellSetColspan2222((pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault,2));
//			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));				    
//			    tT30.addCell(makeCellSetColspan3("                                   府认定资格的废弃处理单位，委托其在处理期限内处分租赁物，且将该单位出具的《租赁物回收证明 》",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           （或者有关废弃处理的合同及该单位的收据）的原件交付给甲方。如在废弃处理期间未能向甲方交付回",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    tT30.addCell(makeCellSetColspan2222("           收证明（或者有关废弃处理的合同及该单位的收据）的，乙方应按照超过废弃处理期限的天数相应地向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//			    
//			    
//			    tT30.addCell(makeCellSetColspan2222("           甲方支付相当于日租费的违约金。租赁物报废时所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			    tT30.addCell(makeCellSetColspan2222("                 4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           完毕前按照迟延天数支付相应的损害赔偿金，计算方法如下:每天应当支付相当于双倍的日租费作为损",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("           害赔偿金。同时遵守本合同的其他约定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第十八条 连带保证人",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产保全费、申请",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           执行费律师费、公告费、 评估费、拍卖费等。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                3、连带保证人保证的期间同乙方所负全部债务履行期限。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           已形成的债务向甲方主张免责或要求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           仍负全部责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始得",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           解除保证责任，以其他方式声明退保，均不生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十九条 甲方权利的转让",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			 
			    tT30.addCell(makeCellSetColspan2222("     第二十条 乙方提供必要的情况和资料",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方资产负债表、乙方利润表及其他的明细情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第二十一条 争议的解决",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			   
				
				/*以下zhangbo0723*/

			
						if(decpId.equals("3") || decpId.equals("8")){
							 tT30.addCell(makeCellSetColspan2222("                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向合同",PdfPCell.ALIGN_LEFT, FontDefault2,2));
							tT30.addCell(makeCellSetColspan2222( "          签订所在地的人民法院提起诉讼。", PdfPCell.ALIGN_LEFT, FontDefault2, 2));
						}else{
							 tT30.addCell(makeCellSetColspan2222("                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
							tT30.addCell(makeCellSetColspan2222( "          注册所在地的人民法院提起诉讼。", PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
						}
				/*以上zhangbo0723*/
			    tT30.addCell(makeCellSetColspan2222("     第二十二条 合同及附件",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 合同附件与本合同具有同等法律效力。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("            本合同一式两份，双方各执一份，经双方盖章后生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    for (int i = 0; i <=19; i++) {
			    	tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
				}

			    tT30.addCell(makeCellSetColspan2222((pageN+6)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    document.add(tT30);
		    /*
		   
		
	    }else{
			//委托购买1
        	    
		

			  tT30.addCell(makeCellSetColspan2222("     第一条 租赁物",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买合同附件一所记载的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          租赁物租予乙方，乙方则向甲方承租并使用该物件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              1、租赁物包括:全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              2、租赁物的购买:租赁物是乙方根据自己的需要，自主选定租赁物及生产商和卖方。乙方就租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          物的名称、规格、型号、性能、质量、数量、技术指标和品质、技术保证、售后服务和维护以及价格、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          交货、安装、验收时间等交易条件直接和卖方商定。甲方根据乙方的选择与要求与卖方签订购买合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          乙方同意合同附件中的购买合同中的全部条款，并在购买合同上签字。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              3、乙方应对上述选择和决定承担全部责任，甲方对该选定不承担任何责任；乙方须向甲方提供",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          甲方认为有必要的各种批准许可证明。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("              4、甲方不对租赁物的选定和品质作任何建议或保证，对租赁物的瑕疵不承担责任。对于任何与",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          租赁物的瑕疵有关的争议及赔偿应当由乙方与卖方之间自行解决，而不得牵涉甲方。有关购买租赁物应",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          交纳的税费由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			
			  tT30.addCell(makeCellSetColspan2222("     第二条 租赁物的所有权",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			  tT30.addCell(makeCellSetColspan2222("             1、在本合同租赁期间内，租赁物的所有权归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             2、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所，不得转让给第三人或允许他人使用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("             3、乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施向第三方销售、转让、转租租",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  tT30.addCell(makeCellSetColspan2222("          赁物、不得向他人设置质押、抵押等担保。",PdfPCell.ALIGN_LEFT, FontDefault2,2));

			 
			   
			    tT30.addCell(makeCellSetColspan2222("     第三条	租赁物的交付",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方在合同附件一中载明的卖方处收取租赁物后，应在合同附件一载明的验收期限内自行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          对取得的租赁物实施检验，并应在三天内向甲方提交《租赁物验收证明书》。如乙方未按本款规定的时",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          间验收并出具《租赁物验收证明书》，甲方可视为租赁物已在符合乙方要求的状态下由乙方验收完毕，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方已经接受该租赁物。上述期限届满之日即视为本合同起租日，否则甲方有权选择终止本合同并依据",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          本合同违约、保证条款进行索赔。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，或卖方有其他违反买卖",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          合同的行为，乙方应该在三天内以书面形式通知甲方；并且，乙方应直接与卖方协商解决前述纠纷，并",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          在与卖方解决该纠纷后，向甲方提交《租赁物验收证明书》。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、乙方拒收租赁物的，乙方应当赔偿由此给甲方带来的一切损失。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方提交《租赁物验收证明书》后，即为认可租赁物已在符合要求的状态下由甲方交付完毕",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          ，此后不得再提出任何异议。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、乙方在向甲方提交《租赁物验收证明书》之日起，即可按照本合同的规定使用该租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              6、乙方取得租赁物后，应自行负责将租赁物安装至合同附件一载明的场所。根据甲方的委托和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         确认，乙方与运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其他所需费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("         全部由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			  
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第四条 租赁期间",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、承租方在本合同有效期内不得自行解除本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第五条 租赁物的瑕疵",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的内容不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据购买合同的规定，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           由购买合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           卖方的原因以及其他不属于出租方的故意或重大过失引起而发生的事由，造成租赁物交付延迟或者不能",PdfPCell.ALIGN_LEFT, FontDefault2,2));	    
			    tT30.addCell(makeCellSetColspan2222("           交付时，甲方不承担任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲方的任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ，以便于乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与卖方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           之间的各种直接交涉，甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           法律后果均由乙方承担并享受其利益。因卖方违反买卖合同而造成的一切损失由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));    
			    tT30.addCell(makeCellSetColspan2222("               4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("     第六条 租赁物的保管及使用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+3)+"",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan3("                                       1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，对安全",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          检查中发现的各种灾害事故隐患，在接到安全主管部门或甲方提出的整改通知书后，认真付诸实施。若",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物受到损害的，乙方应当积极采取抢救措施，使损失减少至最低程度，同时保护现场，并立即",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          通知甲方，协助勘查。",PdfPCell.ALIGN_LEFT, FontDefault,2));
			    tT30.addCell(makeCellSetColspan2222("                3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收回或请求返还租赁物及请求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、由于租赁物自身或其设置、保管、使用的原因，而对第三人产生的损害应由乙方进行赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第七条 	租赁物的保养及费用",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          相应的维护和修理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           定期或者不定期的检查和进行其他一切维护、修护，并承担一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              4、乙方应按照税法规定的税率承担因租金而产生的营业税，并与每次应当支付的租金一起付",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           给甲方。根据本合同向甲方支付的费用须缴纳增值税时，乙方应按照甲方的结算请求进行支付。（租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           期间如遇国家税收政策发生重大变化，所产生的税负增加，仍由乙方担负。）",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("              5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           不负担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			 
			    tT30.addCell(makeCellSetColspan2222("     第八条	租赁物的灭失、损毁",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、保全措施、乙方的原因或其他任",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。通常的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          损耗、减耗不适用本项。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  A. 将租赁物复原或修理至完好状态；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  B. 用与租赁物相同、性能相似的物件替换租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               3、租赁物灭失（包括不能修理或者侵害所有权）的情况，乙方应根据未付的租金金额向甲方支",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          付赔偿金，保证金由甲方没入。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支付赔",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          偿金额，同时，本合同自动终止。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("               5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          方要将租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的资力等",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));			    
			    tT30.addCell(makeCellSetColspan2222("     第九条	租赁物所有权变更的情形",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方在本合同规定的租赁期届满时，可选择买取租赁物或终止合同。乙方选择终止合同的，应在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁期届满2个月前以书面形式通知甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("     第十条	租赁物的状态改变",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("              乙方没有得到甲方的书面承诺，不得将租赁物附着在其他物件上，或改造其外观、性能、机能、",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所产生的价值无偿归",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          属于租赁物，产生损害乙方负责无条件赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十一条 租赁物的检查",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转状况",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          及维护情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("     第十二条 保险",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               甲方从起租日起向保险公司投保相应险种，保险人由甲方指定，保险公司以甲方为受益人，保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          费包含在租金中。在租赁期间，如乙方未按时支付租金，造成甲方不能按时对租赁物进行投保和续保",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          而造成的损失，乙方应承担赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    tT30.addCell(makeCellSetColspan2222("     第十三条 保险金的收取",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、发生事故时，乙方应立即通知甲方，并将领受保险金所需的一切文件交付给甲方。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、租赁物发生保险事故后获得赔偿时，由甲方领受保险金。甲方领取保险金后应支付由此发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		    
			    tT30.addCell(makeCellSetColspan2222("          的损失。如保险金不足以支付甲方损失的，乙方应当予以赔偿。如由于乙方的故意或重大过失造成保险",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          公司不予理赔时，乙方应承担该事故的全部损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+4)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));					    
			    tT30.addCell(makeCellSetColspan3("                           第十四条  租赁保证金",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的同时向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           甲方预先支付合同附表规定的保证金额。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           不得凭保证金免除其超出保证金部分的支付义务。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                3、发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           的裁量优先从该保证金帐户支付。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("     第十五条 违约责任",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("               1、乙方发生下列各项情形之一时，甲方无需催告通知即可解除本合同：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  A. 发生一次或一次以上迟延支付租金时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  B. 乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产、解散清算或",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、扣押时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                     乙方被卷入诉讼、仲裁或其他法律程序，可能给乙方的经营活动带来显著不利影响时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  C.乙方迁移住所前未通知甲方时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  D.乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  E.乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  F.乙方经营状况显著恶化，或有足够理由相信有此可能时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  G.本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  H.违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，但未在",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                    该期限内做出回应时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  I.发生与上述各项相当的其他事由时；",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                  J.连带保证人有上述各项情形之一时。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙方承担。甲方收回租赁",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      物时，租赁物的价值由双方确定或者由评估机构评估后确定；确定的价格不足以支付甲方损失",PdfPCell.ALIGN_LEFT, FontDefault2,2));		    
			    tT30.addCell(makeCellSetColspan2222("                      的，乙方应当予以补偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．乙方有义务支付租金及由租赁物产生的其他一切费用。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          以下情况，乙方应承担以下责任:",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、取消）或者",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      在租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖合同（包括撤回要约）。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                   B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并承担",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      相应的违约金（计算标准:购买方实际支付日起至实际收到乙方返还全部支付款项日，以日息万分",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                      之五计算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                4、延迟支付而产生的违约金:乙方怠于向甲方支付本合同相关费用时，或者甲方为乙方垫付费用",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向甲方支付违约金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			   
			    tT30.addCell(makeCellSetColspan2222("     第十六条  租赁物的返还",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 1、本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          的选择是否购买租赁物的权利。购买金额以附表(8)中载明的购买选择权为准。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以当",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          时的状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切费用和税款均由",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方承担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    

			    
			    
			    
			    tT30.addCell(makeCellSetColspan2222("                 3、本合同在租赁期届满或者因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          租赁物通常损耗或甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          以返还。运送租赁物所需的必要费用由乙方负担。本合同租赁期满或者因解除而终止时，甲方可以要求",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          乙方报废租赁物。甲方要求乙方在报废处理期限内处分租赁物的，乙方应立即将租赁物送交具有中国政",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          府认定资格的废弃处理单位，委托其在处理期限内处分租赁物，且将该单位出具的《租赁物回收证明 》",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          （或者有关废弃处理的合同及该单位的收据）的原件交付给甲方。如在废弃处理期间未能向甲方交付回",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          收证明（或者有关废弃处理的合同及该单位的收据）的，乙方应按照超过废弃处理期限的天数相应地向",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          甲方支付相当于日租费的违约金。租赁物报废时所需的必要费用由乙方负担。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222((pageN+5)+"",PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
			    tT30.addCell(makeCellSetColspan3("                         4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          完毕前按照迟延天数支付相应的损害金，计算方法如下:每天应当支付相当于双倍的日租费作为损害金。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          同时遵守本合同的其他约定。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第十七条  连带保证人",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关的",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产保全费、申请",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           执行费律师费、公告费、 评估费、拍卖费等。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 3、连带保证人保证的期间同乙方所负全部债务履行期限。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           已形成的债务向甲方主张免责或要求损害赔偿。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("                 5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           仍负全部责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始得",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           解除保证责任，以其他方式声明退保，均不生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    
			    tT30.addCell(makeCellSetColspan2222("      第十八条 甲方权利的转让",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			 
			    tT30.addCell(makeCellSetColspan2222("      第十九条 乙方提供必要的情况和资料",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           乙方资产负债表、乙方利润表及其他的明细情况。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("      第二十条 争议的解决",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("           注册所在地的人民法院提起诉讼。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    
			    tT30.addCell(makeCellSetColspan2222("      第二十一条 合同及附件",PdfPCell.ALIGN_LEFT, FontColumn2,2));
			    tT30.addCell(makeCellSetColspan2222("                  合同附件与本合同具有同等法律效力。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("                 ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    tT30.addCell(makeCellSetColspan2222("          本合同一式两份，双方各执一份，签字后生效。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
			    		    
			    
			    for (int i = 1; i <=24; i++) {
			    	tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
				}
			    tT30.addCell(makeCellSetColspan2222((pageN+6)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,2));
			    tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
		    
		  
        	    
        	   
        	   
        	    document.add(tT30);
	    }
	      * 
		     */
	    
	    	}
	    document.close();
	    // 支付表PDF名字的定义
	    String strFileName = contract.get("CUST_CODE").toString() + ".pdf";
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
    
    /*
	 * Add by Michael 2012-3-23 导出重车融资租赁合同PDF
	 */
	@SuppressWarnings("unchecked")
	public void preCarPdf(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");
		Map cust = new HashMap();
		String type = null;

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.expCarPdf(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.expCarPdf(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}

	/*
	 * Add by Michael 2012-3-23 导出重车融资租赁合同PDF
	 */
	public void expCarPdf(Context context) {

		ByteArrayOutputStream baos = null;
		String[] con = null;

		Map contract = new HashMap();
		List CREDITNATU = new ArrayList();
		List CROP = new ArrayList();
		Map natu = new HashMap();
		Map crp = new HashMap();

		try {
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.EMBEDDED);
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

			// 打开文档
			document.open();

			con = (String[]) context.contextMap.get("credtdxx");
			//

			// 准备参数 add by Shen Qi 2012.03.05
			StringBuffer param = new StringBuffer();
			for (int i = 0; con != null && i < con.length; i++) {
				param.append("'").append(con[i]).append("'");
				if (i != con.length - 1) {
					param.append(",");
				}
			}
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("RECT_ID", param.toString());
			List<String> rectId = (List<String>) DataAccessor.query(
					"rentContract.checkIsAudit", paramMap,
					DataAccessor.RS_TYPE.LIST);

			Map<String, String> checkIsAudit = new HashMap<String, String>();
			for (int i = 0; rectId != null && i < rectId.size(); i++) {
				checkIsAudit.put(rectId.get(i), rectId.get(i));
			}

			for (int ii = 0; ii < con.length; ii++) {
				int t = 0;
				context.contextMap.put("credit_id", con[ii]);

				BusinessLog.addBusinessLog(
						DataUtil.longUtil(con[ii]),
						checkIsAudit.get(con[ii]) == null ? DataUtil
								.longUtil("0") : DataUtil.longUtil(con[ii]),
						"导出 融资租赁合同", "合同浏览导出合同", null,
						context.contextMap.get("s_employeeName") + "("
								+ context.contextMap.get("s_employeeId")
								+ ")在合同管理的合同浏览使用导出合同功能", 1, DataUtil
								.longUtil(context.contextMap
										.get("s_employeeId").toString()),
						DataUtil.longUtil(0),(String)context.contextMap.get("IP"));

				contract = (Map) DataAccessor.query(
						"creditCustomerCorp.getCreditCCorpByCreditIdUpdateCon",
						context.getContextMap(), DataAccessor.RS_TYPE.MAP);
				if (contract != null) {
					if (contract.size() > 0) {
						;
					} else {
						contract = (Map) DataAccessor.query(
								"creditCustomerCorp.getCreditCCorpByCreditId",
								context.getContextMap(),
								DataAccessor.RS_TYPE.MAP);
					}
				} else {
					contract = (Map) DataAccessor.query(
							"creditCustomerCorp.getCreditCCorpByCreditId",
							context.getContextMap(), DataAccessor.RS_TYPE.MAP);
				}

				if (contract == null) {

					contract = new HashMap();

					contract.put("LEASE_CODE", "  ____ ");
					contract.put("CORP_NAME_CN", " ____  ");
					contract.put("LEGAL_PERSON", " ____  ");
					contract.put("REGISTERED_OFFICE_ADDRESS", " ");
					contract.put("COMMON_OFFICE_ADDRESS", " ");
					contract.put("POSTCODE", " ");
					contract.put("TELEPHONE", " ");
					contract.put("FAX", " ");
					contract.put("CUST_CODE", " ");
					contract.put("CONTRACT_TYPE", "1");

				}

				String code = contract.get("CONTRACT_TYPE") + "";

				float[] widthsPPCa = { 3f };
				PdfPTable tT = new PdfPTable(2);

				tT.setWidthPercentage(100f);
				tT.addCell(makeCellSetColspan3("        ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("融资租赁合同", PdfPCell.ALIGN_CENTER,
						fa, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				t = t + 5;

				String Lcode = contract.get("LEASE_CODE") + "";
				Lcode = Lcode.trim();
				if (Lcode.equals("")) {
					Lcode = "           ";
				}
				tT.addCell(makeCellSetColspan2(" ", PdfPCell.ALIGN_RIGHT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeft(" ", PdfPCell.ALIGN_LEFT,
						FontDefault));
				tT.addCell(makeCellWithBorderRight("合同编号:    " + Lcode,
						PdfPCell.ALIGN_CENTER, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2ForOne(
						"          合同签订日:    20____年____月____日",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));

				tT.addCell(makeCellSetColspan2ForOne(
						"          合同签订地:    中华人民共和国", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            出租方(甲方):    " + Constants.COMPANY_NAME,
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):    "
						+ contract.get("CORP_NAME_CN") + "",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            法定代表或负责人:    "+Constants.LEGAL_PERSON, PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("法定代表或负责人:    "
						+ contract.get("LEGAL_PERSON") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            注册地址:    苏州工业园区东富路8号",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"注册地址:    " + contract.get("REGISTERED_OFFICE_ADDRESS")
								+ " ", PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            实际经营地:    ", PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne("实际经营地:    ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            "+Constants.COMPANY_COMMON_ADDRESS, PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"" + contract.get("COMMON_OFFICE_ADDRESS") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            邮政编码:    215022 ", PdfPCell.ALIGN_LEFT,
						FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"邮政编码:    " + contract.get("POSTCODE") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            电话号码:    0512-80983566 ",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"电话号码:    " + contract.get("TELEPHONE") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellWithBorderLeftForOne(
						"            传真号码:    0512-80983567 ",
						PdfPCell.ALIGN_LEFT, FontDefault22));
				tT.addCell(makeCellWithBorderRightForOne(
						"传真号码:    " + contract.get("FAX") + " ",
						PdfPCell.ALIGN_LEFT, FontDefault22));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				t = t + 26;

				Paragraph mm11 = new Paragraph();

				mm11.setFont(FontDefault);

				mm11.add("                      本合同的租赁是指中国合同法规定的融资租赁形式。出租方 ");
				Chunk c361 = new Chunk(Constants.COMPANY_NAME, FontUnder);
				mm11.add(c361);

				PdfPCell objCell = new PdfPCell(mm11);

				objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				objCell.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				objCell.setColspan(2);
				objCell.setPaddingLeft(35);
				objCell.setBorderWidthBottom(0);
				objCell.setBorderWidthTop(0);

				tT.addCell(objCell);

				Paragraph mm12 = new Paragraph();

				mm12.setFont(FontDefault);

				mm12.add("           (以下简称“甲方”)和承租方");
				String neme1 = contract.get("CORP_NAME_CN") + "";

				neme1 = neme1.trim();
				if (neme1.equals("")) {

					neme1 = "____________________________________";
					mm12.add(neme1);

				} else {

					int le = neme1.length();
					String px = "                         ";

					if (le < 19) {

						String pp = px.substring(0, Math.round(21 - le));

						neme1 = pp + pp + neme1 + pp + pp;
					}

					Chunk c461 = new Chunk(neme1, FontUnder);
					mm12.add(c461);
				}

				mm12.add("(以下简称“乙");

				PdfPCell Cell2 = new PdfPCell(mm12);

				Cell2.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				Cell2.setVerticalAlignment(PdfPCell.ALIGN_LEFT);
				Cell2.setColspan(2);
				Cell2.setPaddingLeft(35);
				Cell2.setBorderWidthBottom(0);
				Cell2.setBorderWidthTop(0);

				tT.addCell(Cell2);

				tT.addCell(makeCellSetColspan2ForOne(
						"            方”)双方就甲方出租本合同规定的合同正本及合同附件中记载的设备(以下简称租赁物)，乙",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne(
						"            方从甲方处承租租赁物事宜，在平等互惠的基础上经友好协商达成以下协议并签订本合同(本",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne(
						"            合同分为合同正本与合同附件，合同附件经甲、乙双方及卖方签字确认后与合同正本有同等",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2ForOne("            的法律效力)。",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellWithBorderLeftForOne("          出租方(甲方): ",
						PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("承租方(乙方):  ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2(
						"                         "
								+ Constants.COMPANY_NAME
								+ "                                                                "
								+ contract.get("CORP_NAME_CN") + "",
						PdfPCell.ALIGN_LEFT, FontDefault, 2));
				tT.addCell(makeCellSetColspan2("", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne(
						"          法定代表人或授权人:  ", PdfPCell.ALIGN_LEFT,
						FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("法定代表人或授权人:  ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT.addCell(makeCellWithBorderLeftForOne("          日期: ",
						PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellWithBorderRightForOne("日期: ",
						PdfPCell.ALIGN_LEFT, FontDefault));

				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));
				tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				// 后来修改
				t = t + 23;
				for (; t < 59; t++) {
					tT.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
							FontDefault, 2));

				}
				tT.addCell(makeCellSetColspan2("1", PdfPCell.ALIGN_CENTER,
						FontDefault, 2));
				t += 1;
				if (t == 60) {
					tT.addCell(makeCellSetColspan4("    ",
							PdfPCell.ALIGN_CENTER, FontDefault, 2));
				}
				// 以上是后来修改的
				document.add(tT);
				document.add(Chunk.NEXTPAGE);

				// 循环 连带保证人:
				// 身份证号码:
				// 身份证地址:
				// 签约日期:

				List pageList = new ArrayList<Map>();
				Map pageMap = new HashMap();
				CREDITNATU = (List) DataAccessor.query(
						"creditVoucher.selectAND", context.getContextMap(),
						DataAccessor.RS_TYPE.LIST);
				CROP = (List) DataAccessor.query("creditVoucher.selectVND",
						context.getContextMap(), DataAccessor.RS_TYPE.LIST);

				int cr = CROP.size();
				int na = CREDITNATU.size();

				// 整合担保人信息到List

				for (int n = 0; n < na; n++) {
					natu = (Map) CREDITNATU.get(n);
					natu.put("CUSTYPE", "NA");

					pageList.add(natu);
				}

				for (int m = 0; m < cr; m++) {
					crp = (Map) CROP.get(m);
					crp.put("CUSTYPE", "CR");

					pageList.add(crp);

				}

				int listSize = pageList.size();
				int pageN = ((Number) Math.floor(listSize / 5)).intValue(); // 页数

				int pageL = listSize % 5; // 余数

				int p = 0; // 页数标记
				int m = 0; // 数据标记

				context.contextMap.put("dataType", "证件类型");
				List natuTypeList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary",
						context.contextMap, DataAccessor.RS_TYPE.LIST);
				String flag = "";
				// 多页
				if (pageN > 0) {

					// 多页循环开始
					for (; p < pageN; p++) {

						PdfPTable tT20 = new PdfPTable(10);
						tT20.setWidthPercentage(100f);
						tT20.addCell(makeCellSetColspan3("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("      ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						for (int n = 0; n < 5; n++) {

							pageMap.clear();

							m = 5 * p + n;

							pageMap = (Map) pageList.get(m);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {

								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								tT20.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));

								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT20.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT20.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT20.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}

						document.add(Chunk.NEWLINE);

						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT20.addCell(makeCellSetColspan2((p + 2) + "",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						tT20.addCell(makeCellWithBorderLeft("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT20.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT20.addCell(makeCell3("对保人:", PdfPCell.ALIGN_CENTER,
								FontDefault));
						
//						tT20.addCell(makeCell3("日期:", PdfPCell.ALIGN_CENTER,
//								FontDefault));
						
						tT20.addCell(makeCellSetColspan("     ",
								PdfPCell.ALIGN_CENTER, FontDefaultP, 2));
						tT20.addCell(makeCellWithBorderRight("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT20.addCell(makeCellSetColspan4("    ",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						document.add(tT20);

						// 另一页
						document.add(Chunk.NEXTPAGE);

					} // 多页循环结束

					// 尾页
					if (pageL > 0) {
						PdfPTable tT19 = new PdfPTable(10);
						tT19.setWidthPercentage(100f);
						tT19.addCell(makeCellSetColspan3("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("      ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("     ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						for (int n = (m + 1); n < listSize; n++) {

							pageMap.clear();
							pageMap = (Map) pageList.get(n);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {

								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}
						// 最后页 空字段补齐5个

						if (pageL > 0) {

							for (; pageL < 5; pageL++) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证号码:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

							}
						}

						document.add(Chunk.NEWLINE);

						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));
						tT19.addCell(makeCellSetColspan2("    ",
								PdfPCell.ALIGN_LEFT, FontDefault, 10));

						tT19.addCell(makeCellSetColspan2((pageN + 2) + "",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));

						tT19.addCell(makeCellWithBorderLeft("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));
						tT19.addCell(makeCellWithNoBorder("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT19.addCell(makeCell3("对保人:", PdfPCell.ALIGN_CENTER,
								FontDefault));
						tT19.addCell(makeCellSetColspan("     ",
								PdfPCell.ALIGN_CENTER, FontDefaultP, 2));
						tT19.addCell(makeCellWithBorderRight("     ",
								PdfPCell.ALIGN_CENTER, FontDefault));

						tT19.addCell(makeCellSetColspan4("    ",
								PdfPCell.ALIGN_CENTER, FontDefault, 10));
						
						document.add(tT19);

						// 另一页
						document.add(Chunk.NEXTPAGE);

					}// 尾页结束

					// 多页 结束
				}

				// 单页
				else {

					PdfPTable tT19 = new PdfPTable(10);
					tT19.setWidthPercentage(100f);
					tT19.addCell(makeCellSetColspan3("     ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));
					tT19.addCell(makeCellSetColspan2("      ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));
					tT19.addCell(makeCellSetColspan2("     ",
							PdfPCell.ALIGN_LEFT, FontDefault, 10));

					// 有数据
					if (pageL > 0) {

						for (int n = 0; n < listSize; n++) {

							pageMap.clear();
							pageMap = (Map) pageList.get(n);

							if ((pageMap.get("CUSTYPE") + "").equals("NA")) {
								if (pageMap.get("CUST_NAME").equals(" ")
										&& pageMap.get("NATU_IDCARD").equals(
												" ")
										&& pageMap.get("NATU_IDCARD_ADDRESS")
												.equals(" ")) {
									pageMap.put("CUST_NAME",
											pageMap.get("NATU_MATE_NAME"));
									pageMap.put("NATU_IDCARD",
											pageMap.get("NATU_MATE_IDCARD"));
									pageMap.put("NATU_IDCARD_ADDRESS", pageMap
											.get("NATU_MATE_IDCARD_ADDRESS"));
								}
								for (int yy = 0; yy < natuTypeList.size(); yy++) {
									Map mapyy = (Map) natuTypeList.get(yy);
									int codeyy = Integer.parseInt(mapyy.get(
											"CODE").toString());
									if (Integer.parseInt(pageMap.get(
											"FLAGPERMIT").toString()) == codeyy) {
										flag = mapyy.get("FLAG").toString();
									}
								}
								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("CUST_NAME")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                " + flag + ":   "
												+ pageMap.get("NATU_IDCARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                身份证地址:   "
												+ pageMap
														.get("NATU_IDCARD_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("      ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("       ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

							if ((pageMap.get("CUSTYPE") + "").equals("CR")) {

								tT19.addCell(makeCellSetColspan2(
										"                连带保证人:   "
												+ pageMap.get("LEGAL_PERSON")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                营业执照编号:   "
												+ pageMap.get("LEGAL_ID_CARD")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                注册地址:   "
												+ pageMap
														.get("LEGAL_HOME_ADDRESS")
												+ "    ", PdfPCell.ALIGN_LEFT,
										FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                法人代表: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2(
										"                签约日期: ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));

								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
								tT19.addCell(makeCellSetColspan2("   ",
										PdfPCell.ALIGN_LEFT, FontDefault, 10));
							}

						}
						// 最后页 空字段补齐4个

						for (; pageL < 5; pageL++) {

							tT19.addCell(makeCellSetColspan2(
									"                连带保证人:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证号码:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证地址:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                签约日期: ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

						}
					}

					// 无数据
					if (pageL == 0 & pageN == 0) {

						// 空字段补齐4个

						for (; pageL < 5; pageL++) {

							tT19.addCell(makeCellSetColspan2(
									"                连带保证人:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证号码:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                身份证地址:   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2(
									"                签约日期: ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));
							tT19.addCell(makeCellSetColspan2("   ",
									PdfPCell.ALIGN_LEFT, FontDefault, 10));

						}

					}

					document.add(Chunk.NEWLINE);

	        		for (int i = 1; i <=4; i++) {
	            		tT19.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,10));					
					}
	        		
	        		tT19.addCell(makeCellSetColspan2( (pageN+2)+"" ,PdfPCell.ALIGN_CENTER, FontDefault,10));

	            		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	            	 
	            		tT19.addCell(makeCell3( "对保人:" ,PdfPCell.ALIGN_CENTER, FontDefault));
	            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
	            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
	        		//tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_RIGHT, FontDefault,10));
            		
	        		tT19.addCell(makeCellWithBorderLeft( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellWithNoBorder( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCell3( "日期:" ,PdfPCell.ALIGN_CENTER, FontDefault));
            		tT19.addCell(makeCellSetColspan( "     " ,PdfPCell.ALIGN_CENTER, FontDefaultP,2));
            		tT19.addCell(makeCellWithBorderRight( "     " ,PdfPCell.ALIGN_CENTER, FontDefault));
        		    tT19.addCell(makeCellSetColspan4("    ",PdfPCell.ALIGN_RIGHT, FontDefault,10));	        		
	 		    
	        		document.add(tT19);

					// 另一页
					document.add(Chunk.NEXTPAGE);

				}

				if (pageL == 0 & pageN > 0) {

					pageN = pageN - 1;
				}

				// 合同条款
				PdfPTable tT30 = new PdfPTable(2);
				tT30.setWidthPercentage(100f);
				tT30.addCell(makeCellSetColspan3("合同条款", PdfPCell.ALIGN_CENTER,
						fa, 2));

				tT30.addCell(makeCellSetColspan2("    ", PdfPCell.ALIGN_LEFT,
						FontDefault, 2));

				tT30.addCell(makeCellSetColspan2222("    第一条 租赁物",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            甲方根据乙方的要求及乙方的自主选定，以出租给乙方为目的，为乙方购买本合同附件",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         《租赁物情况表》所记载的租赁物并将其租予乙方，乙方则向甲方承租并使用该租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、租赁物包括：全部补充配件、增设物、修缮物及附属或定着于该租赁物的从物在内。 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             2、租赁物的购买：租赁物是乙方根据自己的需要，自主选定租赁物及生产商和卖方。乙方就租",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         赁物的名称、规格、型号、性能、质量、数量、技术指标和品质、技术保证、售后服务和维护以及价格、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         交付、安装、验收时间等交易条件直接和卖方商定。甲方根据乙方的选择与要求与卖方签订购买合同或委托",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         乙方与卖方签定购买合同，因就租赁物购买所应缴纳的相关税、费由乙方承担缴纳，乙方同意合同附件中的购",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         买合同中的全部条款，并在购买合同上签字。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             3、乙方应对上述选择和决定承担全部责任，甲方对该选定不承担任何责任；甲方不对租赁物的选定和品质",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         作任何建议或保证，对租赁物的瑕疵不承担责任。对于任何与租赁物的瑕疵有关的争议及赔偿应当由乙方与卖方之", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         间自行解决，而不得牵涉甲方。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));				
				tT30.addCell(makeCellSetColspan2222(
						"             4、本合同项下的租赁物如须办理包括但不限于立项、进口、登记等行政审批程序，则一切审批程序由乙方负责办理、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         缴费，并须向甲方提供甲方认为有必要的各种批准许可证明。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第二条	租赁物的所有权及抵押的设定",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             1、本合同交易是根据《中华人民共和国合同法》第十四章融资租赁合同相关条款签订，在乙方未完全履行本",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         合同项下的所有义务前，租赁物的所有权归甲方所有，即使因现行行政管理法规或其他管理制度无法依本融资租合同，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         将租赁物登记为甲方所有名下，而登记于乙方或第三方所有名下，租赁物的所有权及处分权仍全归甲方所有。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             2、甲、乙双方同意甲方购买租赁物后，产权可登记于乙方名下，但乙方须将租赁物抵押登记予甲方，并由",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         乙方承担办理抵押登记手续及产生的相关费用，甲、乙双方了解并确认，前述运作目的是用于保障甲方对租赁物", 
						PdfPCell.ALIGN_LEFT,FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         的所有权及预防第三人对租赁物主张任何的权利，双方因本融资租赁法律关系所签订的抵押合同及其他协议，均不", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         影响租赁物所有权归属甲方的事实。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             3、未经甲方同意，乙方不得将租赁物迁离合同约定的设置场所或约定使用区域。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));		
				tT30.addCell(makeCellSetColspan2222(
						"             4、	乙方不得以任何形式侵犯甲方的所有权，在租赁期间不得实施包括但不限于向第三方销售、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));					
				tT30.addCell(makeCellSetColspan2222(
						"         转让、转租租赁物、设置质押、抵押等担保及其他有损甲方权益的行为。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("   第三条 租赁物的交付",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           1、租赁物由制造商或供货商根据购买合同于租赁物设定抵押登记予甲方后直接交付给乙方，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       即视为本合同起租日，乙方直接从制造商或供货商处接受租赁物并由乙方签名验收，甲方对租赁物的交付",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       不承担任何责任，乙方在合同附表中载明的卖方处收取租赁物后，应在合同附件一载明的验收期限内自行",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       对取得的租赁物实施检验，并应在三天内向甲方提交《租赁物验收证明书》。如乙方未按本款规定的时间验收",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       并出具《租赁物验收证明书》，甲方可视为租赁物已在符合乙方要求的状态下由乙方验收完毕，乙方已经",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       接受该租赁物，否则甲方有权选择终止本合同并依据本合同违约、保证条款向乙方进行索赔。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           2、租赁物的规格、式样、质量、性能、机能、数量等被发现有瑕疵的，或卖方有其他违反买卖",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       合同的行为，不论乙方是否向卖方进行索赔或是否得到赔偿，均不影响乙方在本合同项下支付租金等其他应",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       付款项及其他应履行的义务，且乙方应该在三天内以书面形式通知甲方；并由乙方直接与卖方协商解决前述纠",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       纷，在与卖方解决该纠纷后，及时向甲方提交《租赁物验收证明书》，乙方不得拖延时间（自收到租赁物之日起", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"       不超过15天，如遇特殊情况应及时通知甲方）。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           3、乙方拒收租赁物的，乙方应当赔偿由此给甲方造成的一切损失。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           4、乙方接收租赁物后，应自行负责将租赁物安装移置到合同附表载明的场所。根据甲方的委托和确认，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      乙方与卖方、运输、搬运、安装及调试公司签订有关协议的，除甲方承诺支付的费用以外，其他所需费用全部由",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("    第四条 租赁期间",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            1、租赁期间是以第三条第1项规定为起租日至租赁物的租金及相关费用交付完毕为止。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            2、乙方在本合同有效期内不得自行解除本合同，否则应承担本合同第十六条的违约责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("    第五条 租赁物的瑕疵",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"            1、基于乙方购买租赁物的自主权，如卖方延迟租赁物的交货，或提供租赁物与购买合同所规定的内容",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      不符，或在安装调试、操作过程中及质量保证期间有质量瑕疵等情况，根据购买合同的规定，由购买合同的卖方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222((pageN + 3) + "",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				

				tT30.addCell(makeCellSetColspan3(
						"                  负责，甲方不承担赔偿责任，乙方不得向甲方追索。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            2、因发生自然灾害、地震、战争及其他不可抗力、运输途中的事故、劳动争议、法令等改废、卖方的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      原因以及其他不属于甲方的故意或重大过失引起而发生的事由，造成租赁物交付延迟或者不能交付时，甲方不承担",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      任何责任。乙方应与卖方直接进行交涉，并协商解决，不得追究甲方的任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            3、甲方认为有必要时，可向乙方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利，以便于",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      乙方向卖方直接交涉或请求。但是，对于卖方的责任履行，以及甲方转让所有权后乙方与卖方之间的各种直接交涉，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"      甲方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和法律后果均由乙方承担并享受其利",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"      益。因卖方违反买卖合同而造成的一切损失由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				tT30.addCell(makeCellSetColspan2222(
						"            4、发生上述1、2、3项所述情况，本合同履行不受影响，乙方须按约定支付租金并承担责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第六条 	租金",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                                    乙方应当按照本合同附件《租赁物情况表》规定的数额及支付条件向甲方支付租金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第七条 租赁物的保管及使用",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              1、乙方应当按照甲方的要求在租赁物上注明其所有权属于甲方。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              2、保管和使用租赁物时，乙方应当遵照国家有关部门制定的保护财产安全的各项规定，进行各项检验、验证、审批",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          及安全检查，所产生的相关税、费由乙方承担缴纳，若因乙方未依上述各项规定进行保管使用，有可能损害甲方权益时，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          甲方可解除本合同，因而给甲方造成损害的，甲方可向乙方请求损害赔偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              3、如乙方违反上述情况，致使租赁物发生部分或全部的损坏，甲方可解除本合同，收回或请求返还租赁物及请求",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          损害赔偿。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              4、由于租赁物自身或其设置、保管、使用的原因，而对甲方或第三人造成人身伤害或者财产损害的，甲方不承担",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          任何责任，因而造成甲方损害的，均由乙方承担赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              5、因乙方对租赁物的保管、使用违反国家相关法令规定所产生的罚款及其他任何费用，乙方应承担该罚款及其他",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          任何费用，并应于缴款期限内缴款。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第八条 租赁物的保养及费用",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              1、乙方应确保租赁物长期处于正常的运转状态，或者处于充分发挥其机能的工作状态，并进行相关法令规定",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("          及正常的维护和修理。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              2、乙方应自行负责因前项义务发生的零件、附属零件的更换、租赁物的维修、损害处的修理、定期或者",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          不定期的检查和进行其他一切维护、修护，并承担一切费用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              3、因维持租赁物的所有权或保管、使用，以及本合同下的交易所产生的税费，由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              4、乙方应按照税法规定的税率承担因租金而产生的营业税，并与每次应当支付的租金一起付给甲方。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          根据本合同向甲方支付的费用须缴纳增值税时，乙方应按照甲方的结算请求进行支付。（租赁期间如遇国家税",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          收政策发生重大变化，所产生的税、费增加，仍由乙方担负。）",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              5、关于租赁物，因第三人的专利权、商标、著作权或其他知识产权而发生侵权或纠纷时，甲方不负担",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("          任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第九条 租赁物的灭失、损毁",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              1、直至租赁物返还，因盗窃、火灾、风水灾害、地震、征用、没入、执行、扣押、保全措施、乙方的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          原因或其他任何不可归责于甲方的原因，而引起的租赁物的灭失、毁损及其他一切危险，均由乙方承担损失。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("          通常的损耗、减耗不适用本项。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              2、租赁物灭失或者毁损的，乙方应按照甲方的要求采取措施，并自行承担一切费用：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 A. 将租赁物复原或修理至完好状态；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 B. 用与租赁物相同、性能相似的对象替换租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              3、如租赁物灭失包括但不限于不能修理、侵害所有权、无法返还的情况，乙方应就已到期未付及未",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          到期的租金总额向甲方一次清偿，且保证金由甲方没入，并赔偿甲方所受的损害。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              4、发生本条第2项的情况时，本合同履行不受影响；发生本条第3项的情况时，应按照规定支付赔偿",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          偿金额，同时，本合同自动终止。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              5、乙方按照本条第3项的规定向甲方支付规定的赔偿金额时，在不改变租赁物状态的情况下，甲方要将",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          租赁物的所有权转移给乙方或者第三人。甲方对租赁物的性能、机能以及第三人的资力等不承担任何责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222((pageN + 4) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan3(
						"                         第十条 	租赁物所有权变更的情形",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"              乙方在本合同规定的租赁期届满时，可选择买取租赁物或终止合同。乙方选择终止合同的，应在租赁",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"          期届满2个月前以书面形式通知甲方。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"     第十一条 租赁物的状态改变",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"               乙方没有得到甲方的书面承诺，不得将租赁物附着在其他对象上，或改造其外观、性能、机能、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           品质等，使租赁物的原状态发生任何变更。如果没有承诺的情形下，其改造、变更所产生的价值无偿",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           归属于租赁物的所有人即甲方，但由此产生的损害由乙方负无条件赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十二条 租赁物的检查",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                无论何时，甲方均可随时进入乙方的事务所、工厂、公司等场所，检查租赁物的现状、运转状况",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           及维护情况，甲方可要求乙方随时告知租赁物目前所在位置及租赁物相关信息，乙方不得有议异。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十三条 保险",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                乙方从起租日起至本合同应履行义务履行完毕前应向保险公司投保相应险种，保险期限至融资租赁",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           合同到期之日，如乙方不履行到期还款的义务，乙方应继续购买保险，直至融资租赁合同下债务履行完毕",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           为止，保险代办人及保险人由甲方指定，保险费用由乙方依保险公司规定交付予保险代办人或交付予甲方转",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           付予保险公司，乙方并同意保险公司以甲方为第一受益人，在本合同有效期间，因乙方原因不能按时对租赁",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           物进行投保和续保而造成的损失，乙方应承担赔偿责任;甲方租赁物价金未支付前、未投保前,本项租赁物危险",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           负担责任仍由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan2222("     第十四条 保险金的收取",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                1、发生事故时，乙方应立即通知保险公司及甲方，依保险公司规定的理赔程序申请理赔，并将理赔及",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"                                     受领保险金所需的一切文件交付予保险代办人或保险公司。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            2、	租赁物发生保险事故后获得赔偿时，由甲方领受保险金。如保险金不足以支付甲方损失的，乙方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            应当予以赔偿。如由于乙方的故意或重大过失造成保险公司不予理赔时，乙方应承担该事故的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"            全部损害赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十五条 租赁保证金",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                1、乙方按照本合同的规定承担相应债务，作为债务履行的担保，乙方应在本合同成立的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           同时向甲方预先支付本合同附件《租赁物情况表》规定的保证金额。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
//				tT30.addCell(makeCellSetColspan2222(
//						"                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务。但是，",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
//				tT30.addCell(makeCellSetColspan2222(
//						"           乙方不得凭保证金免除其超出保证金部分的支付义务。",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"                2、保证金不计利息，甲方可将保证金抵消乙方基于本合同产生的全部或部分债务,包括但",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           不限于已到期平均抵冲的租金及解除合同后至清偿日止依未平均抵冲前的日租金两倍的使用费、滞纳金、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           诉讼费、律师费及处理债权的相关费用,抵消后剩余的保证金作为乙方因违约所应支付给甲方的违约金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				
				tT30.addCell(makeCellSetColspan2222(
						"                3、	发生前项情形时，甲方有权对其与乙方之间的各项支付义务进行结算，所得金额得根据甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           的裁量优先从该保证金帐户支付。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                4、	甲方和乙方之间有本合同以外的其他交易时，或者第1项的保证金以外提供担保时，全部保证",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           金是对全部债权共通的担保。担保的偿付顺序由甲方决定。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十六条 违约责任",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                1、	乙方有违反本合同条款及发生下列各项情形之一时，甲方无需催告通知即可解除本合同。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));// 有违反本合同条款及
				tT30.addCell(makeCellSetColspan2222(
						"                   A.发生一次或一次以上迟延支付租金时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   B.包括但不限于乙方停止履行其对任何第三人的支付义务，或停止生产、 歇业、提出破产",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      、解散清算或被停业整顿、被吊销营业执照时；乙方被命令、通知接受财产保全措施、查封、",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      扣押或因租赁物的保管使用违反相关法令又被没入、扣押之虞时；乙方被卷入诉讼、仲裁或",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      其他法律程序，可能给乙方的经营活动带来显著不利影响时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   C.乙方迁移住所前未通知甲方时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   D.乙方发生合资、分立、减资、股权变更、股份比例变更且未经甲方书面同意时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   E.乙方于议定本合同时，曾为虚伪陈述、保证或伪造相关文件时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   F.乙方经营状况显著恶化，或有足够理由相信有此可能时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   G.本合同外其他对甲方的债务履行，发生一次或一次以上迟延支付时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   H.违反本合同条款或与甲方签订的其他合同条款之一，经甲方催告限定 5 日内改正，但未在",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                     该期限内做出回应时；", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   I.发生与上述各项相当的其他事由时；",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   J.连带保证人有上述各项情形之一时。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 2、本合同基于本条款第1项规定被解除时，甲方可以要求乙方承担以下违约责任：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   A．乙方应按照本合同的规定立即将租赁物返还给甲方，并向甲方支付本合同约定的违约金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      按本项规定返还租赁物发生的修缮及其他费用、各项税款等一切费用均由乙方承担。甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      收回租赁物时，租赁物的价值由双方确定或者由评估机构评估后确定；确定的价格不足以",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                      支付甲方损失的，乙方应当予以补偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                   B．乙方有义务支付已到期未支付及全部未到期的租金及由租赁物产生的其他一切费用，并对甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));// 已到期未支付及全部未到期的；，并对甲方承担相应的损害赔偿责任。
				
				tT30.addCell(makeCellSetColspan2222((pageN + 5) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));	
				
				tT30.addCell(makeCellSetColspan3(
						"                                          承担相应的损害赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"                   C.承担因解除合同所产生的诉讼费、律师费及处理债权的相关费用。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"                   D.甲方得申请法院委托具有相应资质的机构对租赁物进行评估、拍卖,拍卖不成的,依甲方所",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222(
						"                      选定的评估机构评估确定的价格为准,确定的价格不足以支付本款的B款金额时,乙方应当予以补偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"                 3、根据本合同，以及本合同当事人与卖方签订的买卖合同，甲方为乙方订购租赁物后，如发生以",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("         下情况，乙方应承担以下责任：",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                    A．因不可归责于甲方（购买方）的事由所导致的租赁合同未能签署（包括无效、撤销）或者在",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                       租赁物交接完毕前租赁合同被解除时， 甲方（购买方）可以无条件解除买卖合同（包括撤",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                       回要约）。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                    B．如发生本条第A款的情形下，乙方应及时返还甲方（购买方） 已支付的全部款项，并承担",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                       相应的违约金（计算标准：购买方实际支付日起至实际收到乙方返还全部支付款项日，",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				//Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
				//tT30.addCell(makeCellSetColspan2222("                       以日息万分之五计 算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                       以每万元每日6元计算）。同时乙方应立即代替甲方（购买方）与卖方进行协商、妥善处理。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 4、延迟支付而产生的违约金：乙方怠于向甲方支付本合同租金及其他相关费用时，或者甲方为乙方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
//				tT30.addCell(makeCellSetColspan2222(
//						"         垫付费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应每日按照应付金额的万分之五向甲方支付",
//						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				//Modify by Michael 2012-12-13 将罚息有万分之五改为万分之六
				tT30.addCell(makeCellSetColspan2222(
						"         垫付费用后乙方怠于偿还该垫付款时，在此延迟期间，乙方应按照应付的金额以每万元每日6元计算，向",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"         甲方支付违约金。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十七条 租赁物的返还",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                1、	本合同在租赁期届满且乙方已履行其在本合同下的债务后，乙方有权行使其在本合同下拥有的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           选择是否购买租赁物的权利。购买金额以本合同附件《租赁物情况表》中载明的购买选择权为准。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                2、乙方按本条第1项规定支付购买金额后，即取得租赁物的所有权，所有权在租赁物所在地以当时的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           状态转移。甲方不对任何瑕疵或者隐藏的瑕疵承担责任。因所有权转移发生的一切费用和税款均由乙方承担。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"                3、本合同在租赁期届满或者因解除而终止时，或者甲方基于本合同要求返还租赁物时，除了租赁物",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           通常损耗或甲方认可范围外，乙方应立即对租赁物恢复原状，将租赁物送交至甲方指定的地点予以返还。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           运送租赁物所需的必要费用由乙方负担。本合同租赁期满或者因解除而终止时，甲方可以要求乙方", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           报废租赁物。甲方要求乙方在报废处理期限内处分租赁物的，乙方应立即将租赁物送交具有中国政府", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           认定资格的废弃处理单位，委托其在处理期限内处分租赁物，且将该单位出具的《租赁物回收证明 》", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           （或者有关废弃处理的合同及该单位的收据）的原件交付给甲方。如在废弃处理期间未能向甲方交付", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           回收证明（或者有关废弃处理的合同及该单位的收据）的，乙方应按照超过废弃处理期限的天数相应地向", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           甲方支付相当于日租费的违约金。租赁物报废时所需的必要费用由乙方负担。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 4、经甲方同意提前解约的情况下，乙方迟延返还租赁物时，如甲方提出要求的，乙方应在返还",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           完毕前按照迟延天数支付相应地损害赔偿金，计算方法如下：每天应当支付相当于双倍的日租费作为",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           损害赔偿金。同时遵守本合同的其他约定。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 5、乙方迟延返还租赁物时，甲方有权自行或指定第三人从租赁物所在地点收回租赁物。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十八条 连带保证人",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                1、乙方的连带保证人应保证乙方完全履行本合同，并保证对其债务承担连带赔偿责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                2、连带保证人承担保证责任的范围为乙方在本合同项下对甲方的全部债务及与前述款项有关的",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           滞纳金、损害赔偿金、实现债权的费用和其他一切费用，包括但不限于诉讼费用、财产保全费、申请",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           执行费、律师费、公告费、 评估费、拍卖费等。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                3、连带保证人保证的期间同乙方所负全部债务履行期限。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                4、因甲方的原因变更、解除本合同下担保责任或其他保证时，连带保证人不得对乙方所付的业",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           已形成的债务向甲方主张免责或要求损害赔偿。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222(
						"                5、连带保证人在偿还乙方因本合同所付有的一切债务以前，不得取得代甲方向乙方求偿的权利。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                6、本合同或票据的要件有欠缺、或请求的手续不完备，或担保物有追索瑕疵等情况时，保证人",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("           仍负全部责任。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                7、保证人要求退保时，在乙方提供经甲方认可的保证人办妥手续，并经甲方书面通知后，始得",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           解除保证责任，以其他方式声明退保，均不生效。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第十九条 甲方权利的转让",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 甲方在本合同履行期间，随时可将本合同规定的全部或部分权利转让给第三人。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第二十条 乙方提供必要的情况和资料",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                乙方按甲方的要求定期或随时向甲方提供能反映乙方企业真实状况的资料和情况，包括但不限于",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"           乙方资产负债表、乙方利润表及其他的明细情况。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222((pageN + 6) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan3("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				
				tT30.addCell(makeCellSetColspan2222("     第二十一条 争议的解决",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 本合同的一切争议，首先应友好协商，如协商不能解决需提起诉讼时，本合同当事人应当向甲方",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"             注册所在地的人民法院提起诉讼。", PdfPCell.ALIGN_LEFT,
						FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				tT30.addCell(makeCellSetColspan2222("     第二十二条 合同及附件",
						PdfPCell.ALIGN_LEFT, FontColumn2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"                 合同附件与本合同具有同等法律效力。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222("                 ",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				tT30.addCell(makeCellSetColspan2222(
						"            本合同一式三份，甲方执两份，乙方执一份，经双方盖章后生效。",
						PdfPCell.ALIGN_LEFT, FontDefault2, 2));

				for (int j=0;j<=40;j++){
					tT30.addCell(makeCellSetColspan2222("                 ",
							PdfPCell.ALIGN_LEFT, FontDefault2, 2));
				}
				
				tT30.addCell(makeCellSetColspan2222((pageN + 7) + "",
						PdfPCell.ALIGN_CENTER, FontDefault, 2));
				tT30.addCell(makeCellSetColspan4("           ",
						PdfPCell.ALIGN_CENTER, FontDefault2, 2));

				document.add(tT30);

			}
			document.close();
			// 支付表PDF名字的定义
			String strFileName = contract.get("CUST_CODE").toString() + ".pdf";
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

			if ((context.getContextMap().get("creditidflagi") + "").equals(""
					+ context.getContextMap().get("creditidflagl"))) {

				closeStream(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}


	//Add by Michael 2012 3-30 导出重车管理协议
	@SuppressWarnings("unchecked")
	public void preCarManageAgreementPdf(Context context) {
		String[] con = HTMLUtil.getParameterValues(context.getRequest(),
				"credit_idxx", "00");
		Map cust = new HashMap();
		String type = null;

		if (con != null) {
			if (!(con[0].equals("00"))) {
				try {
					if (con.length > 1) {
						context.contextMap.put("credtdxx", con);
						this.expCarManageAgreementPdf(context);
					} else {
						if (con.length == 1) {
							context.contextMap.put("credtdxx", con);
							this.expCarManageAgreementPdf(context);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
			}
		}
	}
	
    @SuppressWarnings("unchecked")
    public void expCarManageAgreementPdf(Context context) {
    	
	ByteArrayOutputStream baos = null;
	String[]  con = null;
	 
	Map contract =new HashMap();
 	
	try {   
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
        
        // 打开文档
        document.open();
    
    	con= (String[]) context.contextMap.get("credtdxx");
    	//
    	StringBuffer param=new StringBuffer();
    	for(int i=0;con!=null&&i<con.length;i++) {
    		param.append("'").append(con[i]).append("'");
    		if(i!=con.length-1) {
    			param.append(",");
    		}
    	}
    	Map<String,String> paramMap=new HashMap<String,String>();
    	paramMap.put("RECT_ID", param.toString());
    	List<String> rectId=(List<String>)DataAccessor.query("rentContract.checkIsAudit", paramMap, DataAccessor.RS_TYPE.LIST);
	    
	    Map<String,String> checkIsAudit=new HashMap<String, String>();
	    for(int i=0;rectId!=null&&i<rectId.size();i++) {
	    	checkIsAudit.put(rectId.get(i),rectId.get(i));
	    }
    	    
    	for(int ii=0; ii< con.length;ii++){
    		int t=0;
    	context.contextMap.put("credit_id",  con[ii]);
    	    	
    	BusinessLog.addBusinessLog(DataUtil.longUtil(con[ii]),checkIsAudit.get(con[ii])==null?DataUtil.longUtil("0"):DataUtil.longUtil(con[ii]),
    							   "导出重车管理协议",
    							   "合同浏览导出合同",
    							   null,
    							   context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
    							   1,
    							   DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
    							   DataUtil.longUtil(0),(String)context.contextMap.get("IP"));
    	contract= (Map) DataAccessor.query("exportContractPdf.getPRJTLeaseCodeBYCredit", context.contextMap,DataAccessor.RS_TYPE.MAP);   	     

	 
	   
    	String strCustName=contract.get("CUST_NAME").toString() ;
    	
	    PdfPTable tT30 = new PdfPTable(2);  
	    tT30.setWidthPercentage(100f);
	    tT30.addCell(makeCellSetColspan3("车辆挂靠管理协议", PdfPCell.ALIGN_CENTER,fa,2));
	 
		tT30.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,2));
		tT30.addCell(makeCellSetColspan2222("    甲方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontColumn2,2));
		tT30.addCell(makeCellSetColspan2222("    乙方："+strCustName,PdfPCell.ALIGN_LEFT , FontColumn2,2));
		tT30.addCell(makeCellSetColspan2222("    甲、乙双方在友好、公平的基础上，依据《中华人民共和国合同法》及其他相关",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    法令，经充分协商及合于法令的前提下，就车辆挂靠管理相关事宜，达成如下协",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    议条款,以资共同遵守：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    一、挂靠车辆：指甲方客户在甲方处以融资租赁形式购买的各式重型车辆，经甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    方同意由客户挂靠于乙方处管理的车辆。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		
		tT30.addCell(makeCellSetColspan2222("    二、双方的权利与义务",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("       （一）、甲方的权利与义务",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          1、在车辆《租赁物委托管理协议》有效期间内，甲方于任何情况下拥有融",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            资租赁车辆的所有权，有权保管车辆登记证书、发票及其他车辆相关证",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            书原件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          2、如乙方存在侵害融资租赁车辆的所有权行为或因乙方原因有可能侵害",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            甲方对融资租赁车辆的所有权时，甲方有权在不经通知乙方的情况下，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            自行或委托第三人取回融资租赁车辆，并于取回后有权处分融资租赁车",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            辆。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          3、甲方的融资租赁客户，存在逾期缴纳车辆租金的行为或有其他违反与甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            方签订的《融资租赁合同》约定的行为时，甲方有权自行或委托第三人",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            取回融资租赁车辆，并于取回后有权处分融资租赁车辆。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("       （二）、乙方的权利与义务",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          1、协助甲方的融资租赁客户开展正常的经营业务，并不得干涉、妨碍其正",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            常的营业性活动。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          2、有权向甲方的融资租赁客户收取合理的挂靠费及签订相关的挂靠文件。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          3、督导及协助甲方的融资租赁客户，按时缴纳所有法定的包括但不限于运",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            管费、车船税、二级维护费等相关税、费。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          4、督导及协助甲方的融资租赁客户，完成挂靠车辆的年检及相关法令规定",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            的检修、行政管理事宜。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          5、协助甲方及甲方的融资租赁客户，处理车辆各种保险、出险理赔事宜及",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            其他交通事故的处理。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          6、不得私自对本协议挂靠的车辆，实行买卖、抵押、质押、抵销债务、过",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            户、转让或其他侵害甲方车辆所有权的行为，前述车辆如需过户、年检，",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            须则乙方必须实时通知甲方并经甲方出具书面通知同意书后，始可进行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            办理的手续。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          7、协助甲方对逾期缴纳租金及违反上述《融资租赁合同》规定客户的管理",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            及车辆取回等其他的保障债权实施的措施，并对甲方取回的车辆根据甲",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            方的需要，无条件协助甲方办理相关包括但不限于报停、移转过户等行",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            政管理手续。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          8、挂靠车辆的融资租赁合同终止后，由甲方出具相关书面证明予乙方，后",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("            续车辆的管理，由乙方自行与客户协议，不与甲方产生任何关系。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    三、签订《租赁物委托管理协议》",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    就客户透过甲方融资租赁形式，于乙方处挂靠的每一台车辆，须由甲方、乙方及",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    客户三方另行签定《租赁物委托管理协议》。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
						
		tT30.addCell(makeCellSetColspan2222("    四、违约责任",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("       （一）、本协议履行过程中，由于甲方违约及其他可归责于甲方事由，致乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          受有损害的，甲方应承担相应的损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("       （二）、本协议履行过程中，由于乙方违约及其他可归责于乙方事由，致甲方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("          受有损害的，乙方应承担相应的损害赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
		tT30.addCell(makeCellSetColspan2222("    五、本于公平、互利的真诚长久合作，乙方及其法定代表人，保证双方于协议合",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    作期间，就甲方客户透过融资租赁形式，于乙方处挂靠的每一台车辆，客户的权",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    益及甲方的所有权益不受侵害，若有侵害行为导致损害的，愿承担相应的民、刑",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    事法律责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
		tT30.addCell(makeCellSetColspan2222("    六、本协议有效期限为长期有效，甲、乙双方不得无故终止协议，否则应承担相",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    应的法律责任，双方若需终止本协议，须另行书面签定终止的协议书,如因乙方",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    原因单方解除本协议而造成客户及甲方利益的损害,由乙方全部赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
		tT30.addCell(makeCellSetColspan2222("    七、本协议未尽事宜，双方可另行签订补充协议，对本协议产生争议时，双方应",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("    先友好协商解决，协商不成时以甲方登记住所所在地法院为管辖法院。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
		tT30.addCell(makeCellSetColspan2222("    八、本协议一式三份，甲、乙双方及乙方代表人各执一份为凭。",PdfPCell.ALIGN_LEFT, FontDefault2,2));
		tT30.addCell(makeCellSetColspan2222("                                                            ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
				
		for (int i = 0; i <=2; i++) {
			tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
		}
		
		tT30.addCell(makeCellSetColspan2222("    甲方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontColumn2,2));
		
		for (int i = 0; i <=2; i++) {
			tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
		}
		tT30.addCell(makeCellSetColspan2222("    乙方："+strCustName,PdfPCell.ALIGN_LEFT, FontColumn2,2));
		tT30.addCell(makeCellSetColspan2222("    法定代表人（签名）：",PdfPCell.ALIGN_LEFT, FontColumn2,2));
		
		for (int i = 0; i <=2; i++) {
			tT30.addCell(makeCellSetColspan2222("           ",PdfPCell.ALIGN_CENTER, FontDefault,2));
		}
		tT30.addCell(makeCellSetColspan2222("    签订日期：            年        月        日",PdfPCell.ALIGN_LEFT, FontColumn2,2));

		tT30.addCell(makeCellSetColspan4("           ",PdfPCell.ALIGN_CENTER, FontDefault2,2));	
		  
		document.add(tT30);
	    }
	    document.close();
	    // 支付表PDF名字的定义
	    String strFileName = "CarManageAgreement.pdf";
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
 
    public void preValueAddTaxOpenInvoice(Context context){
			String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
			
			 if(con != null ){
			   if(!(con[0].equals("00"))){
				 try {
		        		 if(con.length >1){
		        			 context.contextMap.put("credtdxx",  con);
		        			 this.getDateValueAddTaxOpenInvoice(context);
		        			 //this.expLeaseHoldPdfs(context);             	    
		        		 }else{
		                	if(con.length ==1){
		                		 context.contextMap.put("credtdxx",   con);
		                		 this.getDateValueAddTaxOpenInvoice(context);
//		                		 this.expLeaseHoldPdfs(context);
		                	} 
		        		}
				} catch (Exception e) {
					    e.printStackTrace();
					    LogPrint.getLogStackTrace(e, logger);
				}
			  }
			}	
		  }

	public void  getDateValueAddTaxOpenInvoice(Context context){
		 String[]  con = null;
		  con= (String[]) context.contextMap.get("credtdxx");	        
	        for(int ii=0; ii< con.length;ii++){	
	        	context.contextMap.put("credit_id",  con[ii]);
		       try{
		    	   context.contextMap.put("PRCD_ID",  context.contextMap.get("credit_id"));
		    	   Map custOpenInvoiceData=(Map) DataAccessor.query("exportContractPdf.queryValueAddInvoiceData", context.contextMap, DataAccessor.RS_TYPE.MAP);
		    	   context.contextMap.put("custOpenInvoiceData", custOpenInvoiceData);
		       }catch(Exception e){
		    	   e.printStackTrace();
		    	   LogPrint.getLogStackTrace(e, logger);
		       }
		    }
	        this.expValueAddTaxOpenInvoiceDataPdf(context);
	 }
    
	public void expValueAddTaxOpenInvoiceDataPdf(Context context) {
		Map outputMap = new HashMap();
		ByteArrayOutputStream baos = null;
		List errList = context.errList;		
			try {
				// 字体设置
				BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
				Font FontColumn = new Font(bfChinese, 9, Font.BOLD);
				Font FontDefault = new Font(bfChinese, 9, Font.NORMAL);
				Font fa = new Font(bfChinese, 15, Font.BOLD);
				// 数字格式
				NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
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
				// 表格列宽定义
				float[] widthsStl = {0.1f,0.5f};
				int iCnt = 0;			
				float[] widthsPPCa = { 1f };
				PdfPTable tT = new PdfPTable(widthsPPCa);
				tT.setWidthPercentage(100f);
				tT.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_CENTER, fa));
				tT.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_CENTER, fa));
				tT.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_CENTER, fa));
				tT.addCell(makeCellWithNoBorder("增值税开票资料确认书", PdfPCell.ALIGN_CENTER, fa));
				tT.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_CENTER, fa));
				tT.addCell(makeCellWithNoBorder("  ", PdfPCell.ALIGN_CENTER, fa));
				document.add(tT);
				document.add(new Paragraph("\n"));
				if(context.contextMap.get("custOpenInvoiceData")!=null){
					Map custOpenInvoiceData=(Map) context.contextMap.get("custOpenInvoiceData");
					String cust_name="";
					if(custOpenInvoiceData.get("CUST_NAME")==null){
						cust_name="";
					}else{
						cust_name=custOpenInvoiceData.get("CUST_NAME").toString();
					}
					String link_address="";
					if(custOpenInvoiceData.get("LINK_WORK_ADDRESS")==null){
						link_address="";
					}else{
						link_address=custOpenInvoiceData.get("LINK_WORK_ADDRESS").toString();
					}
					String link_phone="";
					if(custOpenInvoiceData.get("LINK_PHONE")==null){
						link_phone="";
					}else{
						link_phone=custOpenInvoiceData.get("LINK_PHONE").toString();
					}
					
					String bank_name="";
					if(custOpenInvoiceData.get("BANK_NAME")==null){
						bank_name="";
					}else{
						bank_name=custOpenInvoiceData.get("BANK_NAME").toString();
					}
					
					String bank_account="";
					if(custOpenInvoiceData.get("BANK_ACCOUNT")==null){
						bank_account="";
					}else{
						bank_account=custOpenInvoiceData.get("BANK_ACCOUNT").toString();
					}
					
					PdfPTable tableHdr1 = new PdfPTable(widthsStl);
					tableHdr1.addCell(makeCell("开票名称：",PdfPCell.ALIGN_LEFT, FontColumn));
					tableHdr1.addCell(makeCell(cust_name,PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr1);

					PdfPTable tableHdr2 = new PdfPTable(widthsStl);
					tableHdr2.addCell(makeCell("税务登记号:",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr2.addCell(makeCell(custOpenInvoiceData.get("CORP_TAX_CODE")==null?"":custOpenInvoiceData.get("CORP_TAX_CODE").toString(),PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr2);
					
					PdfPTable tableHdr3 = new PdfPTable(widthsStl);
					tableHdr3.addCell(makeCell("地址、电话:",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr3.addCell(makeCell(link_address+"    "+link_phone,PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr3);	
					
					PdfPTable tableHdr4 = new PdfPTable(widthsStl);
					tableHdr4.addCell(makeCell("开户行及账号:",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr4.addCell(makeCell(bank_name+"    "+bank_account,PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr4);	
					
					PdfPTable tableHdr5 = new PdfPTable(widthsStl);
					tableHdr5.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr5.addCell(makeCellWithNoBorder("    ",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr5);
					
					PdfPTable tableHdr6 = new PdfPTable(widthsStl);
					tableHdr6.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr6.addCell(makeCellWithNoBorder("    ",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr6);
					
					PdfPTable tableHdr7 = new PdfPTable(widthsStl);
					tableHdr7.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr7.addCell(makeCellWithNoBorder("    ",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr7);
					
					PdfPTable tableHdr8 = new PdfPTable(widthsStl);
					tableHdr8.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr8.addCell(makeCellWithNoBorder("    ",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr8);
					
					PdfPTable tableHdr9 = new PdfPTable(1);
					tableHdr9.addCell(makeCellWithNoBorder("承租人："+cust_name,PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr9);
					
					PdfPTable tableHdr11 = new PdfPTable(1);
					tableHdr11.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr11);
					
					PdfPTable tableHdr12= new PdfPTable(1);
					tableHdr12.addCell(makeCellWithNoBorder("签署：",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr12);
					
					PdfPTable tableHdr13 = new PdfPTable(1);
					tableHdr13.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr13);
					
					PdfPTable tableHdr10 = new PdfPTable(1);
					tableHdr10.addCell(makeCellWithNoBorder("",PdfPCell.ALIGN_LEFT, FontColumn));				
					tableHdr10.addCell(makeCellWithNoBorder("日期：",PdfPCell.ALIGN_LEFT, FontColumn));
					document.add(tableHdr10);
					
					document.add(Chunk.NEXTPAGE);
				}
				document.close();
				context.response.setContentType("application/pdf");
				context.response.setCharacterEncoding("UTF-8");
				context.response.setHeader("Pragma", "public");
				context.response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
				context.response.setDateHeader("Expires", 0);
				context.response.setHeader("Content-Disposition","attachment; filename=openInvoiceData");
				
				ServletOutputStream o = context.response.getOutputStream();

				baos.writeTo(o); 
				o.flush();				
				o.close();
				
				BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("credit_id")),null,
				   		 "导出 增值税开票资料确认书",
			   		 	 "合同浏览导出 增值税开票资料确认书",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
				
			} catch (Exception e) {
				e.printStackTrace();	
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
}	

    /**
     * 流关闭操作
     * @param content
     * @param align
     * @param FontDefault
     * @return
     */
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