package com.brick.contract.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
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

		public class ExpBankNoticepdf extends AService {
			static	Log logger = LogFactory.getLog(ExpBankNoticepdf.class);
		    /**
		     * 导出PDF  银行账户信息通知
		     * @param context
		     */
			@SuppressWarnings("unchecked")
			public static void prePdf(Context context){
				ByteArrayOutputStream baos =  new ByteArrayOutputStream();
				//导出文件PDF名字的定义
				String pdfName = "BankAccountNotice" ;
				try{
					//设置数据
					Map content = new HashMap() ;
					Calendar date=Calendar.getInstance();
					int year=date.get(Calendar.YEAR);
					//取出数据
					List obj = (List<Map>) DataAccessor.query("rentContract.getCreditCustNameByID", context.contextMap, DataAccessor.RS_TYPE.LIST) ;
					content.put("YEAR",year);
					content.put("NAME", ((Map)obj.get(0)).get("CUST_NAME"));
					//直租 添加公司别判断
				 	String creditId = (String) context.contextMap.get("PRCD_ID");
					String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
					int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
					String companyName = Constants.COMPANY_NAME;
					
					if("7".equals(contractType)){
						companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
					}
					content.put("creditId",creditId );
					
					content.put("TITLE_NAME",companyName);
					content.put("TITLE_Ntice","银行账户信息通知");
					//Modify by michael 2012 3-8 直接抓虚拟账号
					//content.put("CUST_CODE", "88" + ((Map)obj.get(0)).get("CUST_CODE")) ;
					
			
					content.put("CUST_CODE", ((Map)obj.get(0)).get("VIRTUAL_CODE")) ;
	
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
					   		 "导出 银行账户信息通知",
				   		 	 "合同浏览导出 银行账户信息通知",
				   		 	 null,
				   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
				   		 	 1,
				   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
				   		 	 DataUtil.longUtil(0),
				   		 	 context.getRequest().getRemoteAddr());
					}catch(Exception e){
						e.printStackTrace() ;
						LogPrint.getLogStackTrace(e, logger) ;
					}
			}
			public static void model(Map content,OutputStream baos) throws Exception {
				//标题名称 : TITLE_NAME
				//合同编号：LEASE_CODE
				//收款账号：CUST_CODE
			 	//定义Cell边框粗细   顺序是：上下左右
			 	float[] borderStart = {0,0,0,0} ;
			 	float[] borderEnd = {0,0,0,0} ;
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
			 	PdfPTable t1 = new PdfPTable(1);
			 	//标题行"YEAR",year
//				"NAME", obj
//				"TITLE_NAME","裕融租赁（苏州）有限公司"
//				"TITLE_Ntice","银行账户信息通知"
			 	t1.addCell(makeCell(bfChinese, content.get("TITLE_NAME")== null ? "" :content.get("TITLE_NAME").toString(),new int[]{20,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 9)) ;
			 	t1.addCell(makeCell(bfChinese, content.get("TITLE_Ntice")== null ? "" :content.get("TITLE_Ntice").toString(),new int[]{20,Font.BOLD}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignCenter, 9)) ;
			 	
			 	String creditId = (String) content.get("creditId");
				String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
				int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
				
				
	
				
				
				
			 	//内容
			 	t1.addCell(makeCell(bfChinese,"            ",new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"尊敬的客户：",new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"         您好！",new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         首先感谢您对我们一如既往的支持，并衷心祝愿你的事业更加兴",new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"旺，我们的合作更加愉快。", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         我司银行账户信息如下：", new int[]{14,Font.NORMAL},new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         收款户名："+content.get("TITLE_NAME"), new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         开户银行：交通银行苏州分行园区支行", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         收款账号：" + (content.get("CUST_CODE") == null ? "" : content.get("CUST_CODE").toString()), new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				if("7".equals(contractType)&&companyCode==2){
					t1.addCell(makeCell(bfChinese,"	         如您有关银行汇款之疑问，请致电管理部：", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
					t1.addCell(makeCell(bfChinese,"	        0571-57576388转89505（李庆）", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				}else{
					t1.addCell(makeCell(bfChinese,"	         如您有关银行汇款之疑问，请致电财务部：", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
					t1.addCell(makeCell(bfChinese,"	        0512-80983566转88508（张丽君）", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				}
				t1.addCell(makeCell(bfChinese,"	         如您有其它相关账户之疑问，请致电业管部：",new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"         0512-80983566转88200（杨晶晶）", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"	         祝：商祺！", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"                                                                                          "+content.get("TITLE_NAME"), new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
				t1.addCell(makeCell(bfChinese,"                                                                                               "+content.get("YEAR").toString()+"年   月    日", new int[]{14,Font.NORMAL}, new float[]{10f,10f,-1f,-1f}, new float[]{0f,0f,0f,0f}, alignDefault, 9)) ;
			 	
			 	
			 	
			 	
			 	
			 	
			 	
				//空白行
				t1.addCell(makeCell(bfChinese, "  ", fontDefault, new float[]{10,10,0,0}, new float[]{0,0,0,0}, alignCenter, 9)) ; 
				
				
			
				//租赁物部分  
				PdfPTable t2 = new PdfPTable(5) ;
					//第一行  租赁物名称 、型号、数量 
				t2.addCell(makeCell(bfChinese, " ", fontDefault,new float[]{0,0,0,0}, new float[]{0,0,0,0},alignCenter, 2)) ;
				t2.addCell(makeCell(bfChinese, "承租人", new int[]{14,Font.NORMAL}, paddingDefault, new float[]{0.5f,0.5f,0.5f,0},alignCenter, 1)) ;
				t2.addCell(makeCell(bfChinese, content.get("NAME")== null ? "" : content.get("NAME").toString(), new int[]{14,Font.NORMAL}, paddingDefault, new float[]{0.5f,0.5f,0.5f,0.5f},alignCenter, 2)) ;

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

