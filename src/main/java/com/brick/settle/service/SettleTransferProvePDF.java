package com.brick.settle.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.contract.util.PasswordControlTablePDF;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class SettleTransferProvePDF extends AService {
static Log logger = LogFactory.getLog(PasswordControlTablePDF.class) ;
	
	
	@SuppressWarnings("unchecked")
	public static void expPDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "TransferCertificate" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			String[] ids = HTMLUtil.getParameterValues(context.getRequest(),"ids","");
			 // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize,30, 30, 30, 30); // 其余4个参数，设置了页面的4个边距
	        PdfWriter.getInstance(document, baos);
	        // 打开文档
	        document.open();
				
			for(int i=0;i<ids.length;i++){
				context.contextMap.put("RECP_ID", ids[i]) ;
				//取出数据
				content = (Map) DataAccessor.query("collectionManage.readPaylistById", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
				content.put("idsI", i) ;
				content.put("idsLen", ids.length) ;
				//Modify By Michael 2011 12/16 公司Title错误 裕融
				//content.put("TITLE_NAME","融资租赁（苏州）有限公司");
				content.put("TITLE_NAME",Constants.COMPANY_NAME);
				content.put("TITLE_NAME1","清偿证明暨所有权移转证明书");
				
				if(content.get("LESSEE_TIME") != null && !content.get("LESSEE_TIME").equals("")){
					SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日") ;
					content.put("LESSEE_TIME", sf.format(content.get("LESSEE_TIME"))) ;
				}
			
				//Add by Michael 2012 02/23  增加合同类型查询  一般租赁：清偿证明不需要之前提需求的附注				
				content.put("CONTRACT_TYPE", (Integer) DataAccessor.query("collectionManage.queryContractTypeByRecpID", context.contextMap, DataAccessor.RS_TYPE.OBJECT)) ;
				
				
				//调用模型
				model(content,baos,document) ;
				
				//
			}
				document.close();
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
		   		o.close() ;
			}catch(Exception e){
				e.printStackTrace() ;
				LogPrint.getLogStackTrace(e, logger) ;
			}
	}
	public static void model(Map content,OutputStream baos,Document document) throws Exception {
		
		
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	float[] borderNo = {0,0,0,0} ;//无边框
	 	//定义默认字体
	 	int[] fontDefault = {13,-1} ;
	 	int[] fontU = {-1,Font.UNDERLINE} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {10f,10f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
	 	int [] alignRight = {PdfCell.ALIGN_RIGHT,PdfPCell.ALIGN_CENTER} ;//居右
		//pdf名字	 	
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
       
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(20);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{14,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 20)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME1")== null ? "" :content.get("TITLE_NAME1").toString(),new int[]{14,Font.UNDERLINE}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 20)) ;
	 	//空白行
	 	t1.addCell(makeCell(bfChinese, " ",new int[]{14,Font.UNDERLINE}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 20)) ;
		 
	 	//第1行 
	 	t1.addCell(makeCell(bfChinese, "查" , fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("CUST_NAME")== null ? " " :content.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderNo,alignDefault, 10)) ;
	 	t1.addCell(makeCell(bfChinese, "于          "+ (content.get("LESSEE_TIME") == null ? "年          月          日" :content.get("LESSEE_TIME").toString())  , fontDefault, paddingDefault, borderNo, alignRight, 8)) ;
	 	
	 	//空白行
//	 	t1.addCell(makeCell(bfChinese, " ",new int[]{14,Font.UNDERLINE}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 20)) ;
		
	 	//第2行 
	 	if( String.valueOf(content.get("CONTRACT_TYPE")).equals("3") || String.valueOf(content.get("CONTRACT_TYPE")).equals("4")){
	 		t1.addCell(makeCell(bfChinese, "与本司签订商用车融资租赁合同（合同编号      "+(content.get("LEASE_CODE")== null ? "           " : content.get("LEASE_CODE").toString())+"      ），", fontDefault, paddingDefault, borderNo,alignDefault, 20)) ;
	 	}else{
	 		t1.addCell(makeCell(bfChinese, "与本司签订机器设备融资租赁合同（合同编号      "+(content.get("LEASE_CODE")== null ? "           " : content.get("LEASE_CODE").toString())+"      ），", fontDefault, paddingDefault, borderNo,alignDefault, 20)) ;
	 	}
	 	//第3行 
	 	t1.addCell(makeCell(bfChinese, "今已缴清全部租金及期满购买金，本公司谨立此书证明并声明上开合", fontDefault, paddingDefault, borderNo,new int[]{PdfPCell.ALIGN_UNDEFINED,PdfPCell.ALIGN_CENTER}, 20)) ;
	 	t1.addCell(makeCell(bfChinese, "同项下所有租赁物所有权自即日起移转与贵司所有无误，此  致", fontDefault, paddingDefault, borderNo,new int[]{PdfPCell.ALIGN_UNDEFINED,PdfPCell.ALIGN_CENTER}, 20)) ;
	 	//加一行
		t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderNo,alignDefault, 2)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("CUST_NAME")== null ? " " :content.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderNo,alignDefault, 16)) ;
	 	t1.addCell(makeCell(bfChinese, " " , fontDefault, paddingDefault, borderNo, alignRight, 2)) ;
	 	
	 	//第4行 
	 	for (int i=0;i<4;i++){   //Modify BY Michael 2011 12/16 减少一行空行
	 		t1.addCell(makeCell(bfChinese, "  ", fontDefault, paddingDefault, borderNo,alignDefault,20)) ;
	 	}
	 	//第5行
	 	t1.addCell(makeCell(bfChinese, "立书人："+Constants.COMPANY_NAME, fontDefault, paddingDefault, borderNo,alignDefault,20)) ;
	 	//第6行
	 	t1.addCell(makeCell(bfChinese, "                    法定代表人："+Constants.LEGAL_PERSON, fontDefault, paddingDefault, borderNo,alignDefault,20)) ;
	 	//第7行
	 	t1.addCell(makeCell(bfChinese, "年          月          日", fontDefault, paddingDefault, borderNo,alignRight,20)) ;
	 	
	 	//Add by Michael 2012 02/23  增加合同类型查询  一般租赁：清偿证明不需要之前提需求的附注	
	 	if (String.valueOf(content.get("CONTRACT_TYPE")).equals("1") || String.valueOf(content.get("CONTRACT_TYPE")).equals("2")  || String.valueOf(content.get("CONTRACT_TYPE")).equals("3")  || String.valueOf(content.get("CONTRACT_TYPE")).equals("4") ||  String.valueOf(content.get("CONTRACT_TYPE")).equals("5")) {
		 	// Add By Michael 2011 12/16 For客服需求 增加 附注---------------------------------------------------------------
		 	t1.addCell(makeCell(bfChinese, "  ", fontDefault, paddingDefault, borderNo,alignDefault,20)) ; //空白行
		 	//第8行
			if( String.valueOf(content.get("CONTRACT_TYPE")).equals("3") || String.valueOf(content.get("CONTRACT_TYPE")).equals("4")){
				t1.addCell(makeCell(bfChinese, "附注：本件相关商用车之全部发票正本亦已经"+Constants.COMPANY_NAME, fontDefault, paddingDefault, borderNo,alignDefault, 20)) ;
				
			}else{
				t1.addCell(makeCell(bfChinese, "附注：本件相关机器设备之全部发票正本亦已经"+Constants.COMPANY_NAME, fontDefault, paddingDefault, borderNo,alignDefault, 20)) ;
				
			}
		 	t1.addCell(makeCell(bfChinese, "交付", fontDefault, paddingDefault, borderNo,alignDefault, 3)) ;
			t1.addCell(makeCell(bfChinese, content.get("CUST_NAME")== null ? " " :content.get("CUST_NAME").toString()+"亲收完毕无误。", fontDefault, paddingDefault, borderNo,alignDefault, 18)) ;
		 	
			//第9行
			t1.addCell(makeCell(bfChinese, "                              签收人：", fontDefault, paddingDefault, borderNo,alignDefault,10)) ;
		 	t1.addCell(makeCell(bfChinese, content.get("CUST_NAME")== null ? " " :content.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderNo,alignDefault,10)) ;
		 	
		 	//第10行
		 	t1.addCell(makeCell(bfChinese, "年          月          日", fontDefault, paddingDefault, borderNo,alignRight,20)) ;
		 	//------------------------------------------------------------------------------------------------------------
	 	}
	 	//----------------------------------------------------------------------------------------------------------------
	 	document.add(t1);
		if((Integer)content.get("idsI")< ((Integer)content.get("idsLen") - 1)){	
			//document.resetHeader();
			document.add(Chunk.NEXTPAGE);
		    
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
}
