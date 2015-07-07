package com.brick.contract.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.DataUtil;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 导出密码控制表
 * 
 * @author 
 * 
 * 
 */
public class PasswordControlTablePDF extends AService{
	static Log logger = LogFactory.getLog(PasswordControlTablePDF.class) ;
	
	
	@SuppressWarnings("unchecked")
	public static void expCreditEuipPDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "MiMaKongGuanBiaoCredit" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			//取出数据
			
			content = (Map) DataAccessor.query("rentContract.passwordControlCreditTablePDF", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
			if(content != null && content.size() > 0) {
				if(content.get("RECT_ID") == null || content.get("RECP_ID").equals("")){
					content.put("CUST_PHONE",content.get("TELEPHONE")) ;//公司电话
					content.put("CUST_LINKMAN",content.get("LINK_MAN")) ;//联系人
					content.put("CUST_LINK_JOB",content.get("LINK_JOB")) ;//职称
					content.put("NATU_MOBILE",content.get("LINK_MOBILE_NUMBER1")) ;//手提电话
					content.put("CUST_LINK_EMAIL",content.get("LINK_EMAIL")) ;//EMAIL
					content.put("CUST_FAX",content.get("FAX")) ;//传真
				}
				content.put("TITLE_NAME","密码控管表");
				//调用模型
				model(content,baos) ;
				//
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
		   		
		   		//add by ShenQi 插入系统日志
		   		BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
				   		 "导出 密码控管表",
			   		 	 "合同浏览导出 密码控管表",
			   		 	 null,
			   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
			   		 	 1,
			   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
			   		 	 DataUtil.longUtil(0),
			   		 	 context.getRequest().getRemoteAddr());
		   		
			}
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
		}
	}
	@SuppressWarnings("unchecked")
	public static void expPDF(Context context){
		ByteArrayOutputStream baos =  new ByteArrayOutputStream();
		// 支付表PDF名字的定义
		String pdfName = "MiMaKongGuanBiao" ;
		try{
			//设置数据
			Map content = new HashMap() ;
			//取出数据
			content = (Map) DataAccessor.query("rentContract.passwordControlTablePDF", context.contextMap, DataAccessor.RS_TYPE.MAP) ;
			content.put("TITLE_NAME","密码控管表");
			//判断是否有合同
			
			if(content != null && content.size() > 0 ){
				
				//调用模型
				model(content,baos) ;
				//
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
			}
		}catch(Exception e){
			e.printStackTrace() ;
			LogPrint.getLogStackTrace(e, logger) ;
		}
	}
	public static void model(Map content,OutputStream baos) throws Exception {
		//标题名称 : TITLE_NAME
		//合同编号：LEASE_CODE
		
		//承租人:CUST_NAME
		//公司电话:CUST_PHONE
		//分机号:
		//联系人:CUST_LINKMAN
		//公司职称：CUST_LINK_JOB
		//手提电话:NATU_MOBILE
		//email:CUST_LINK_EMAIL
		//传真：CUST_FAX
		//
		//供货商：NAME
		//公司电话：LINKMAN_TELPHONE
		//分机号
		//联系人LINKMAN_NAME
		//公司职称LINKMAN_JOB
		//手提电话LINKMAN_MOBILE
		//email:LINKMAN_EMAIL
		//传真
		
		//租赁物名称THING_NAME
		//型号MODEL_SPEC
		//数量AMOUNT
		
		
		
		
		
	 	//定义Cell边框粗细   顺序是：上下左右
	 	float[] borderStart = {0.5f,0,0.5f,0} ;
	 	float[] borderEnd = {0.5f,0,0.5f,0.5f} ;
	 	//定义默认字体
	 	int[] fontDefault = {-1,-1} ;
	 	//定义默认边距   顺序是：上下左右
	 	float[] paddingDefault = {5f,5f,-1f,-1f};
	 	//定义默认位置    水平，垂直
	 	int [] alignDefault = {-1,-1} ;//靠左
	 	int [] alignCenter = {PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_CENTER} ;//居中
		//pdf名字
	 	
	 	
 		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	 
 		// 数字格式
        NumberFormat nfFSNum = new DecimalFormat("###,###,###,###.00");
        nfFSNum.setGroupingUsed(true);
        nfFSNum.setMaximumFractionDigits(2);
        // 页面设置
        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
        
        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
        
        
    
        PdfWriter.getInstance(document, baos);
        // 打开文档
        document.open();
        //写入标题
        //t1 承租人部分
	 	PdfPTable t1 = new PdfPTable(9);
	 	//标题行
	 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{20,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 9)) ;
	 	//合同号
	 	t1.addCell(makeCell(bfChinese, " ", fontDefault, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignDefault, 5)) ;
	 	t1.addCell(makeCell(bfChinese, "（合同编号：" + (content.get("LEASE_CODE")== null ? "" :content.get("LEASE_CODE").toString()) +"）", new int[]{9,-1}, new float[]{1,1,1,1}, new float[]{0,0,0,0},alignCenter, 4)) ;
		 
	 	//第一行  承租人、公司电话、分机号
	 	t1.addCell(makeCell(bfChinese, "承  租  人", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("CUST_NAME") == null ? "" : content.get("CUST_NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "公司电话", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
//	 	t1.addCell(makeCell(bfChinese, content.get("CUST_PHONE")== null ? "" : content.get("CUST_PHONE").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "分  机  号", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderEnd, alignCenter, 1)) ;
	 	
	 	//第二行 联系人、公司职称、手提电话
	 	t1.addCell(makeCell(bfChinese, "联  系  人", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, content.get("CUST_LINKMAN")== null ? "" : content.get("CUST_LINKMAN").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "公司职称", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese,  "", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese,  content.get("LINKMAN_JOB")== null ? "" : content.get("LINKMAN_JOB").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "手提电话", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderEnd, alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, content.get("NATU_MOBILE")== null ? "" : content.get("NATU_MOBILE").toString(), fontDefault, paddingDefault, borderEnd, alignCenter, 3)) ;
	 	
		//第三行 E-mail		传真	
		t1.addCell(makeCell(bfChinese, "E - mail", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, content.get("CUST_LINK_EMAIL")== null ? "" : content.get("CUST_LINK_EMAIL").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, "传        真", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderEnd, alignCenter, 4)) ; 
//		t1.addCell(makeCell(bfChinese, content.get("CUST_FAX")== null ? "" : content.get("CUST_FAX").toString(), fontDefault, paddingDefault, borderEnd, alignCenter, 4)) ; 
		
		//空白行
		t1.addCell(makeCell(bfChinese, "", fontDefault, new float[]{10,10,0,0}, new float[]{0.5f,0,0,0}, alignCenter, 9)) ; 
		
		//供货商部分
	 		//第一行  供货商、公司电话、分机号
	 	t1.addCell(makeCell(bfChinese, "供  货  商", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, content.get("NAME") == null ? "" : content.get("NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
	 	t1.addCell(makeCell(bfChinese, "公司电话", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
//	 	t1.addCell(makeCell(bfChinese, content.get("LINKMAN_TELPHONE")== null ? "" : content.get("LINKMAN_TELPHONE").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "分  机  号", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderEnd, alignCenter, 1)) ;
	 	
	 		//第二行 联系人、公司职称、手提电话
	 	t1.addCell(makeCell(bfChinese, "联  系  人", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
//		t1.addCell(makeCell(bfChinese, content.get("LINKMAN_NAME")== null ? "" : content.get("LINKMAN_NAME").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 2)) ;
	 	t1.addCell(makeCell(bfChinese, "公司职称", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
//		t1.addCell(makeCell(bfChinese, content.get("LINKMAN_JOB")== null ? "" : content.get("LINKMAN_JOB").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 1)) ;
	 	t1.addCell(makeCell(bfChinese, "手提电话", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese,"", fontDefault, paddingDefault, borderEnd, alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, content.get("LINKMAN_MOBILE")== null ? "" : content.get("LINKMAN_MOBILE").toString(), fontDefault, paddingDefault, borderEnd, alignCenter, 3)) ;
	 	
		
			//第三行 E-mail		传真	
		t1.addCell(makeCell(bfChinese, "E - mail", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
//		t1.addCell(makeCell(bfChinese, content.get("LINKMAN_EMAIL")== null ? "" : content.get("LINKMAN_EMAIL").toString(), fontDefault, paddingDefault, borderStart, alignCenter, 3)) ;
		t1.addCell(makeCell(bfChinese, "传        真", fontDefault, paddingDefault, borderStart,alignCenter, 1)) ;
		t1.addCell(makeCell(bfChinese, "", fontDefault, paddingDefault, borderEnd, alignCenter, 4)) ; 
		
		//空白行
		t1.addCell(makeCell(bfChinese, "", fontDefault, new float[]{10,10,0,0}, new float[]{0.5f,0,0,0}, alignCenter, 9)) ; 
		
		//租赁物部分  
		PdfPTable t2 = new PdfPTable(8) ;
			//第一行  租赁物名称 、型号、数量 
		t2.addCell(makeCell(bfChinese, "租赁物名称", fontDefault, paddingDefault, borderStart,alignCenter, 4)) ;
		t2.addCell(makeCell(bfChinese, "型号", fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t2.addCell(makeCell(bfChinese, "数量", fontDefault, paddingDefault, borderEnd,alignCenter, 1)) ;
			//第二行 
		t2.addCell(makeCell(bfChinese, content.get("THING_NAME")== null ? " " : content.get("THING_NAME").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 4)) ;
		t2.addCell(makeCell(bfChinese, content.get("MODEL_SPEC")== null ? " " : content.get("MODEL_SPEC").toString(), fontDefault, paddingDefault, borderStart,alignCenter, 3)) ;
		t2.addCell(makeCell(bfChinese, content.get("AMOUNT")== null ? " " : content.get("AMOUNT").toString(), fontDefault, paddingDefault, borderEnd,alignCenter, 1)) ;
		
		//表内容部分
		t2.addCell(makeCell(bfChinese, "  密码控管内容:", fontDefault, paddingDefault, new float[]{0.5f,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "  一、设置方式:             1）□ 按日期 : 每___月___日锁码", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "                                             2）□ 按时数 : ", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "                                             3）□ 按其他 :", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "  二、密码控管方式:    1）□ 直接控管", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "                                             2）□ 间接控管  — 1.□ 有提示码    2.□ 无提示码。", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "  三、解码说明:             1）□ 直接控管:拨款前供货商一次性给齐全部密码。", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "                                             2）□ 间接控管:由供货商每月给我司密码，再由我司提供给客户。", fontDefault, paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;
		t2.addCell(makeCell(bfChinese, "", fontDefault,paddingDefault, new float[]{0,0,0.5f,0.5f},alignDefault, 8)) ;

		//最后一行 填表人、日期     确认人、日期
		t2.addCell(makeCell(bfChinese, "  填表人：", fontDefault,paddingDefault, new float[]{0.5f,0.5f,0.5f,0},new int[]{-1,PdfPCell.ALIGN_CENTER}, 2)) ;
		t2.addCell(makeCell(bfChinese, " 日期：", fontDefault,paddingDefault, new float[]{0.5f,0.5f,0,0},new int[]{-1,PdfPCell.ALIGN_CENTER}, 2)) ;
		t2.addCell(makeCell(bfChinese, "  确认人：", fontDefault,paddingDefault, new float[]{0.5f,0.5f,0.5f,0},new int[]{-1,PdfPCell.ALIGN_CENTER}, 2)) ;
		t2.addCell(makeCell(bfChinese, " 日期：", fontDefault,paddingDefault, new float[]{0.5f,0.5f,0,0.5f},new int[]{-1,PdfPCell.ALIGN_CENTER}, 2)) ;
		

		document.add(t1);
		document.add(t2);
		document.close();
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
