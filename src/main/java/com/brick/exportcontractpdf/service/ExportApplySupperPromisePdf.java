package com.brick.exportcontractpdf.service;

import com.brick.service.core.AService;
import com.brick.service.core.Output;

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
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
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
import com.brick.log.service.LogPrint;

public class ExportApplySupperPromisePdf extends AService {
	Log logger = LogFactory.getLog(ExportApplySupperPromisePdf.class);
   
	public void expPromiseContract(Context context){
		try{
			Map contract =new HashMap();
			Map contracttype =new HashMap();
			List<Map> pucsContractDetail = null;
	    	String cons= (String) context.contextMap.get("rect_id");
	    	
	    	context.contextMap.put("PRCD_ID",  cons);	    	
	    	//查找合同的相关信息
	    	contract = (Map) DataAccessor.query("exportContractPdf.selectRectInfoByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	if(contract==null){
	    		contract = (Map) DataAccessor.query("exportContractPdf.selectCreditInfoByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	}
	    	this.expContractPdf(context,contract,contracttype,pucsContractDetail);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	/**
     * 导出PDF  购买合同,因为是单条导出，所以prePdf方法没有用到
     * @param context
     * 单条合同导出，没有合同Id的数组
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void expContractPdf(Context context,Map contract,Map contracttype,List<Map> pucsContractDetail) {
	
	ByteArrayOutputStream baos = null;
	//Map contract =new HashMap();
	//Map contracttype =new HashMap();

	//List<Map> pucsContractDetail = null;

 	
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	       // Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        //Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
	        Font FontUnder2 = new Font(bfChinese, 10, Font.UNDERLINE);
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
	        
	        
	    	/*String cons= (String) context.contextMap.get("rect_id");
	    	
	    	context.contextMap.put("PUCT_ID",  cons);	    	
	    	//查找合同的相关信息
	    	contract = (Map) DataAccessor.query("exportContractPdf.queryContractByPrcdIds", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	//查找合同的类型
	    	contracttype = (Map) DataAccessor.query("exportContractPdf.queryContractTypeByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	
	    	//pucsContractPlan = (Map) DataAccessor.query("exportContractPdf.readPucsContractPlan", context.contextMap, DataAccessor.RS_TYPE.MAP);
	    	//查找合同所租的机械
	    	pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.readPucsContractDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
	    	*/
			String code = "0";
			String baozhengren = "";
			String applyaddr = "";
			String applydaibiao = "";
			String applytele = "";
			String applyfax = "";
			String applyzip = "";
			String leasecode = "";
			String createtime="";
			String custname="";
			String yeartime="_____";
			String monthtime="____";
			String daytime="____";
			if(contract!=null){
				if(contract.get("APPLYNAME")!=null){
					baozhengren = contract.get("APPLYNAME").toString();
				}
				if(contract.get("CORPORATION_ADDRESS")!=null){
					applyaddr=contract.get("CORPORATION_ADDRESS").toString();
				}
				/*if(contract.get("LINKMAN_NAME")!=null){
					applydaibiao=contract.get("LINKMAN_NAME").toString();
				}*/
				if(contract.get("COMPANY_CORPORATION")!=null){
					applydaibiao=contract.get("COMPANY_CORPORATION").toString();
				}
				if(contract.get("LINKMAN_TELPHONE")!=null){
					applytele=contract.get("LINKMAN_TELPHONE").toString();
				}
				if(contract.get("LINKMAN_FAX")!=null){
					applyfax=contract.get("LINKMAN_FAX").toString();
				}
				if(contract.get("LINKMAN_ZIP")!=null){
					applyzip=contract.get("LINKMAN_ZIP").toString();
				}
				if(contract.get("LEASE_CODE")!=null){
					leasecode=contract.get("LEASE_CODE").toString();
				}
//				if(contract.get("CREATE_TIME")!=null&&contract.get("LEASE_CODE")!=null){
//					createtime=contract.get("CREATE_TIME").toString().substring(0,10);
//					yeartime=createtime.substring(0,4);
//					monthtime=createtime.substring(5,7);
//					daytime=createtime.substring(8,10);
//				}else{
//					yeartime="";
//					monthtime="    ";
//					daytime="    ";
//				}
				if(contract.get("CUST_NAME")!=null){
					custname=contract.get("CUST_NAME").toString();
				}
				
			}
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);
			int i=0;
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
  
				tT.addCell(makeCellSetColspan2("保证合同",PdfPCell.ALIGN_CENTER, fa,8));
	  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		i+=6;
	    
	     
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,8));
	    
	    		tT.addCell(makeCellSetColspanOnlyLeft("     " ,PdfPCell.ALIGN_LEFT,  FontDefault,5));	   
//	    		tT.addCell(makeCellWithNoBorder("合同编号: "  ,PdfPCell.ALIGN_LEFT,  FontDefault2));
	    		tT.addCell(makeCellSetColspanOnlyRight("合同编号: "+leasecode,PdfPCell.ALIGN_LEFT, FontDefault2,3));
//	    		tT.addCell(makeCellSetColspanOnlyRight(""+leasecode,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	   
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));	   
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    
	    
	    		i+=6;
	    		//表头结束
	    
	    
	    		//第一个子表开始
				String contractType =LeaseUtil.getContractTypeByCreditId((String)context.contextMap.get("PRCD_ID"));
				String decpId  = LeaseUtil.getDecpIdByCreditId((String)context.contextMap.get("PRCD_ID"));
				int companyCode = LeaseUtil.getCompanyCodeByCreditId((String)context.contextMap.get("PRCD_ID"));
				String contractAddress = "中华人民共和国";  	
			    String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
				String companyAddress = LeaseUtil.getCompanyAddressByCompanyCode(1);
				String postcode = LeaseUtil.getCompanyPostcodeByCompanyCode(1);
				String telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(1);
				String fax = LeaseUtil.getCompanyFaxByCompanyCode(1);
				
				if("7".equals(contractType)){
					contractAddress = "中华人民共和国东莞市长安镇长青南路303号";
				    companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
				    companyAddress = LeaseUtil.getCompanyAddressByCompanyCode(companyCode);
				    postcode = LeaseUtil.getCompanyPostcodeByCompanyCode(companyCode);
					telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(companyCode);
					fax = LeaseUtil.getCompanyFaxByCompanyCode(companyCode);
				}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("合同签订日：  "+createtime,PdfPCell.ALIGN_LEFT, FontDefault2,6));	    
//	    		tT.addCell(makeCellSetColspanNoBoreder("合同签订日：",PdfPCell.ALIGN_LEFT, FontDefault2,1));	    
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+createtime,PdfPCell.ALIGN_LEFT, FontDefault2,5));	    
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBoreder("合同签订地：  "+contractAddress,PdfPCell.ALIGN_LEFT, FontDefault2,6));	
//	    		tT.addCell(makeCellSetColspanNoBoreder("合同签订地：",PdfPCell.ALIGN_LEFT, FontDefault2,1));	
//	    		tT.addCell(makeCellSetColspanNoBoreder("中华人民共和国",PdfPCell.ALIGN_LEFT, FontDefault2,5));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		
	    		tT.addCell(makeCellWithBorderLeft("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBoreder("保证人：  "+baozhengren,PdfPCell.ALIGN_LEFT, FontDefault2,6));	
//	    		tT.addCell(makeCellSetColspanNoBoreder("保证人：",PdfPCell.ALIGN_LEFT, FontDefault2,1));	
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+baozhengren,PdfPCell.ALIGN_LEFT, FontDefault2,5));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("地址：  "+applyaddr,PdfPCell.ALIGN_LEFT, FontDefault2,6));
//	    		tT.addCell(makeCellSetColspanNoBoreder("地址：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder(" "+applyaddr,PdfPCell.ALIGN_LEFT, FontDefault2,5));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人/授权代表人：  "+applydaibiao,PdfPCell.ALIGN_LEFT, FontDefault2,6));
//	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人/授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//	    		tT.addCell(makeCellSetColspanNoBoreder(" "+applydaibiao,PdfPCell.ALIGN_LEFT, FontDefault2,4));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("电话：  "+applytele,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("传真：  "+applyfax,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("邮政编码：  "+applyzip,PdfPCell.ALIGN_LEFT, FontDefault2,2));
//	    		tT.addCell(makeCellSetColspanNoBoreder("电话：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+applytele,PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("传真：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+applyfax,PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("邮政编码：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+applyzip,PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=8;
	    		
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		tT.addCell(makeCellWithBorderLeft("",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBoreder("债权人：  "+companyName,PdfPCell.ALIGN_LEFT, FontDefault2,6));	
//	    		tT.addCell(makeCellSetColspanNoBoreder("债权人：",PdfPCell.ALIGN_LEFT, FontDefault2,1));	
//	    		tT.addCell(makeCellSetColspanNoBoreder("裕融租赁（苏州）有限公司",PdfPCell.ALIGN_LEFT, FontDefault2,5));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("地址：  "+companyAddress,PdfPCell.ALIGN_LEFT, FontDefault2,6));
//	    		tT.addCell(makeCellSetColspanNoBoreder("地址：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人/授权代表人：  "+Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault2,6));
//	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人/授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
//	    		tT.addCell(makeCellSetColspanNoBoreder("陈力雄",PdfPCell.ALIGN_LEFT, FontDefault2,4));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("电话：  "+telephone,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("传真：  "+fax,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("邮政编码：  "+postcode,PdfPCell.ALIGN_LEFT, FontDefault2,2));
//	    		tT.addCell(makeCellSetColspanNoBoreder("电话：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("0512-80983566",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("传真：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("0512-80983567",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("邮政编码：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
//	    		tT.addCell(makeCellSetColspanNoBoreder("523560",PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));

	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		Phrase phrase = new Phrase("          鉴于：", FontDefault2) ;
//	    		phrase.add(new Phrase("                                                ", FontUnder2)) ;
	    		phrase.add(new Phrase(custname+"", FontUnder2)) ;
	    		phrase.add(new Phrase("（下称“债务人”）与债权人于"+yeartime+"年"+monthtime+"月"+daytime+"日", FontDefault2)) ;
	    		
	    		tT.addCell(makeCellSetColspanNoBoreder(phrase,PdfPCell.ALIGN_LEFT, FontDefault2,6));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));

	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if("".equals(leasecode)){
		    		tT.addCell(makeCellSetColspanNoBoreder("签订了编号为【                               】的融资租赁合同（下称“主合同”），保证人愿意为实",PdfPCell.ALIGN_LEFT, FontDefault2,6));	
	    		}else{
	    			phrase = null ;
	    			phrase = new Phrase("签订了编号为【", FontDefault2) ;
	    			phrase.add(new Phrase(leasecode+"", FontUnder2)) ;
	    			phrase.add(new Phrase("】的融资租赁合同（下称“主合同”），保证人愿意为实", FontDefault2)) ;
	    			tT.addCell(makeCellSetColspanNoBoreder(phrase,PdfPCell.ALIGN_LEFT, FontDefault2,6));
	    			
//	    			tT.addCell(makeCellSetColspanNoBoreder("签订了编号为【"  +leasecode+     "】的融资租赁合同（下称“主合同”），保证人愿意为实",PdfPCell.ALIGN_LEFT, FontDefault2,6));
	    		}
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));

	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBoreder("现主合同项下的债权向债权人提供保证担保。",PdfPCell.ALIGN_LEFT, FontDefault2,6));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspanNoBoreder("为明确双方权利义务，保证人与债权人经协商一致，特订立本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,6));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("保证人：   "+baozhengren,PdfPCell.ALIGN_LEFT, FontDefault2,3));
//	    		tT.addCell(makeCellSetColspanNoBoreder(""+baozhengren,PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("债权人：   "+companyName,PdfPCell.ALIGN_LEFT, FontDefault2,3));
//	    		tT.addCell(makeCellSetColspanNoBoreder("裕融租赁（苏州）有限公司",PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder(" "+applydaibiao,PdfPCell.ALIGN_LEFT, FontDefault22,1));
	    		tT.addCell(makeCellSetColspanNoBoreder("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder(Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspanNoBoreder("日期：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellSetColspanNoBoreder("        ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellSetColspanNoBoreder("日期：",PdfPCell.ALIGN_LEFT, FontDefault2,1));
	    		tT.addCell(makeCellSetColspanNoBoreder("   ",PdfPCell.ALIGN_LEFT, FontDefault2,2));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i+=17;
	    		for(;i<51;i++){
	    			tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    			if(i==50){
	    				tT.addCell(makeCellSetColspan3NoTop("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    			}
	    		}

	    		tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("						第一条  保证责任",PdfPCell.ALIGN_LEFT, FontColumn2,8));
	    		tT.addCell(makeCellSetColspan2222("						1、本合同项下的保证为连带责任保证。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("						2、保证的范围为主合同项下全部租金、手续费、违约金、迟延利息、损害赔偿金",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			和实现债权的费用。实现债权的费用包括但不限于催收费用、诉讼费（或仲裁费）、保",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			全费、公告费、执行费、律师费、差旅费及其它费用。前述费用包括自实际支出之日起",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			的利息。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("						3、保证期间同主合同乙方所负债务履行期限。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			主合同约定债务人分期履行还款义务的，保证期间按各期还款义务分别计算，自每",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			期债务履行期限届满之日起，计至最后一期债务履行期限止。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("			债权人宣布主合同项下债务全部提前到期的，以其宣布的提前到期日为债务履行期",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			限届满日。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						4、根据《中华人民共和国担保法》第五条的规定，本合同双方特别约定如下：本",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			合同效力独立于主合同，主合同或其有关条款无效时不影响本合同的效力。对债务人在",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			主合同无效后应承担的返还责任或赔偿责任，保证人应承担连带责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			
			  	tT.addCell(makeCellSetColspan2222("						5、在主合同约定需由债务人付款的期限届满或者条件成就后，债务人不履行或者",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			不完全履行合同约定义务的，债权人即有权要求保证人履行付款义务。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	
			  	tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
			  	
			  	tT.addCell(makeCellSetColspan2222("						第二条   保证人的陈述与保证",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("						1、保证人是依中华人民共和国法律注册成立并合法存续的企业/公司，关于保证人",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			设立和存续的所有必要的政府批准、文件都已获得，且该批准是充分的、有效的。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			   
			  	tT.addCell(makeCellSetColspan2222("						2、 依据中华人民共和国法律，保证人具有提供保证的主体资格，且具有足够的财",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			力、资产及能力承担本担保书所规定的保证责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						3、保证人已经取得了出具本担保书所应获得的一切授权、许可、审批、决议、承",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			诺等先决条件，且不会造成任何不合法的情形。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						4、 签署和履行本合同是保证人真实的意思表示，不存在任何法律上的瑕疵。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						5、保证人在签署和履行本合同过程中向债权人提供的全部文件、资料及信息是真",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			实、准确、完整和有效的。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						6、 保证人同意接受债权人对其财产状况或经营状况的监督、检查，并同意应债权",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			人要求，随时提供保证人真实、准确、完整的关于经营情况或收入情况等资料，并承诺",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			不存在任何未向债权人披露的可能影响保证人履行本担保书能力的事实。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						7、按时缴纳为拥有和行使保证人的任何财产权利而应负担的税款、行政规费及有",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			关政府机构对其征收的任何其它费用，在债权人提出要求时，向债权人提供已付的前述",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			税费的证明材料。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						8、 采取所有必要和适当的措施，遵守法律、法规、规章的各项规定、谨慎履行管",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			理义务以保证其对任何财产的合法拥有权，并保证财产以及财产权利的完好。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						9、 不论是在债权人根据本合同行使权利之前或之后，如果保证人的任何财产和财",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			产权利有任何缺陷，并因此导致任何损失或损害，均与债权人无关；如有第三方因此提",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			出赔偿要求，债权人无需承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			   			    
			  	tT.addCell(makeCellSetColspan2222("						第三条　保证人的义务",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("						1、债务人未按主合同的规定按时、足额偿还租金以及手续费，或发生其它违反约",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			定的情形时，保证人应无条件地向债权人立即支付债务人的全部到期应付款项或债权人",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			依法宣布提前到期的应付租金及其他款项。保证人如届时不履行或不完全履行保证义",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			务，违反本保证合同的约定，将自愿接受债权人提起的对保证人的强制执行。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						保证人同意：主合同同时受债务人或第三方提供的抵押或质押担保的，债权人有权",PdfPCell.ALIGN_LEFT, FontDefault2,8));	    
			  	tT.addCell(makeCellSetColspan2222("			自行决定行使权力的顺序，债权人有权要求保证人立即支付债务人的全部到期应付款项",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			而无需先行行使担保物权；债权人放弃担保物权或其权利顺位或变更担保物权的，保证",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			人仍按本合同承担保证责任而不免除任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						2、 保证人应配合债权人对其财产状况或经营状况以及资信情况的监督、检查，及",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			时提供债权人要求的资料及信息，并保证所提供的资料或信息是真实、完整和准确的。",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
			  	tT.addCell(makeCellSetColspan2222("						3、 保证人应当在下列事项发生或可能发生之日起二（2）个工作日内书面通知债权人",PdfPCell.ALIGN_LEFT, FontDefault2,8));   
			  	tT.addCell(makeCellSetColspan2222("						（1）对保证人经济状况有或可能有重大不利影响的诉讼、仲裁、行政措施、财产",PdfPCell.ALIGN_LEFT, FontDefault2,8));			    			
			  	tT.addCell(makeCellSetColspan2222("			保全措施、强制执行措施或其它重大不利事件；",PdfPCell.ALIGN_LEFT, FontDefault2,8));			 
			  	
			  	tT.addCell(makeCellSetColspan2222("						（2）保证人的财产状况或经营状况发生重大变化；或保证人财务状况恶化，将对",PdfPCell.ALIGN_LEFT, FontDefault2,8));	
			  	
			  	tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
				tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_CENTER, FontDefault,8));  
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));		  
			 
			  	tT.addCell(makeCellSetColspan2222("			债权人权益造成重大不利影响；",PdfPCell.ALIGN_LEFT, FontDefault2,8));	
			  	
			  	tT.addCell(makeCellSetColspan2222("						（3）保证人无法继续正常经营或被宣告停业、清算、解散或破产；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						（4）保证人任何财产之上设定的任何形式的担保将被执行，且该情况会实质性影",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			响保证人履行本担保书的能力；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						（5）为第三方提供保证，并因此而对其经济状况或履行本担保书项下义务的能力",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			产生重大不利影响；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						（6）住所等联系方式变更。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("						4、在债务人向债权人清偿主合同项下所有债务之前，保证人不向债务人或其它担",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("			保人行使因履行本合同所享有的追偿权。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			    			    
			  	tT.addCell(makeCellSetColspan2222("						5、债权人和债务人变更主合同，保证人仍应承担连带保证责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	
			  	tT.addCell(makeCellSetColspan2222("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));  
			  	
			  	tT.addCell(makeCellSetColspan2222("						第四条 本合同附件：",PdfPCell.ALIGN_LEFT, FontColumn2,8));
		    		
		    	
		  	 	tT.addCell(makeCellSetColspan2222("						1、	保证人之营业执照；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
		    	tT.addCell(makeCellSetColspan2222("						2、	保证人之公司章程；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("						3、	保证人关于同意保证行为的股东会或董事会决议。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				
				tT.addCell(makeCellSetColspan2222("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8)); 
				
				tT.addCell(makeCellSetColspan2222("						第五条    争议解决",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  
				tT.addCell(makeCellSetColspan2222("						双方在履行本合同过程中发生的争议，应首先由双方协商解决；若协商不成，本合",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				
				
				if("7".equals(contractType) && ("3".equals(decpId)|| "8".equals(decpId))){
					tT.addCell(makeCellSetColspan2222("			同项下争议由合同签订地有管辖权的法院诉讼解决。争议期间，各方仍应继续履行未",PdfPCell.ALIGN_LEFT, FontDefault2,8));

				}else{
					tT.addCell(makeCellSetColspan2222("			同项下争议由债权人所在地有管辖权的法院诉讼解决。争议期间，各方仍应继续履行未",PdfPCell.ALIGN_LEFT, FontDefault2,8));

				}
				
				tT.addCell(makeCellSetColspan2222("			涉争议的条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				tT.addCell(makeCellSetColspan2222("						第六条    其它条款",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				tT.addCell(makeCellSetColspan2222("						1、保证人有逃避债权人监督、拖欠保证债务、恶意逃废债等行为时，债权人有权",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("			将该种行为向有关单位通报，并在新闻媒体上公告。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("						2、保证人已认真阅读了主合同，并确认了所有条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("						3、本合同自双方当事人的法定代表人或其授权代表人签字并加盖公章之日起生效。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("						4、本合同正本一式两份，保证人、债权人各执一份。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				tT.addCell(makeCellSetColspan2222("",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				
				tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan("   保证人已通读上述条款，债权人已应保证人的要求作了相应说明，保证人对所有内容无异议。",PdfPCell.ALIGN_CENTER, FontDefault22,6));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
				tT.addCell(makeCellSetColspan2222("",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				tT.addCell(makeCellSetColspan2222("			 【附件清单】",PdfPCell.ALIGN_LEFT, FontColumn2,8));  
				tT.addCell(makeCellSetColspan2222("			  附件一：保证人之营业执照",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				tT.addCell(makeCellSetColspan2222("			  附件二：保证人之公司章程",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				tT.addCell(makeCellSetColspan2222("			  附件三：保证人之股东会或董事会决议",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				  for(;i<70;i++){
				  	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
				  }
				  if(i<=70){
				  	tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));
				  }
			}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName = "applypromisecontract.pdf";
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
		closeStream(o);


	    
	} catch (Exception e) {
	    e.printStackTrace();
	    LogPrint.getLogStackTrace(e, logger);
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
    



    /** 创建 有边框 合并 单元格|-|
     *  无下边用于表格的顶
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
    /** 创建 有边框 合并 单元格|_|
     *  无上边
     *  
     *  */
    private PdfPCell makeCellSetColspan3NoTop(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	 
	return objCell;
    }
    /** 创建 有边框 合并 单元格-_|
     *  无左边
     *  
     *  */
    private PdfPCell makeCellSetColspan3NoLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthLeft(0);
	 
	return objCell;
    }
    /** 创建 有边框 合并 单元格|-_
     *  无左边
     *  
     *  */
    private PdfPCell makeCellSetColspan3NoRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthRight(0);
	 
	return objCell;
    }
    /** 创建 有边框 合并 单元格 _|
     *  
     *  
     *  */
    private PdfPCell makeCellSetColspan2RightAndBottom(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(0); 
	return objCell;
    }
    /** 创建 有边框 合并 单元格| |
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
    /** 创建 有边框 合并 单元格| |
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2RightAndTop(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthLeft(0);
	return objCell;
    }
    /** 创建 有边框 合并 单元格 |
     *  只有左边框
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }
    /** 创建 有边框 合并 单元格   |
     *  只有右边框
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(0);
	return objCell;
    }
    /** 创建 没有边框 合并 单元格   
     *  只有右边框
     *  
     *  */
    private PdfPCell makeCellSetColspanNoBoreder(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthRight(0);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthLeft(0);
	return objCell;
    }
    /** 创建 没有边框 合并 单元格   
     *  只有右边框
     *  用于多个zit
     *  
     *  */
    private PdfPCell makeCellSetColspanNoBoreder(Phrase phrase, int align,
    	    Font FontDefault, int colspan) {
    	Phrase objPhase = phrase;
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthBottom(0);
    	objCell.setBorderWidthRight(0);
    	objCell.setBorderWidthTop(0);
    	objCell.setBorderWidthLeft(0);
    	return objCell;
    }
    /** 创建 无边框 单元格
     * 
     *  */
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


    /* 创建 有边框 四边都有 合并 单元格|-_|
     * 
     */
    private PdfPCell makeCellSetColspan(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);

	return objCell;
    }

   


    



    // 创建 有边框只有上边框 合并 单元格
    private PdfPCell makeCellSetColspan2LeftAndTop(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthBottom(0);
	objCell.setBorderWidthRight(0);
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
    	//objCell.setBorderWidthLeft(0);
    	//objCell.setBorderWidthRight(0);
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



	






   
         
	
 
}