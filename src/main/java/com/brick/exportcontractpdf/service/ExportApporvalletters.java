package com.brick.exportcontractpdf.service;

import com.brick.risk_audit.SelectReportInfo;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.util.LeaseUtil;
import com.brick.collection.service.StartPayService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
//核准函导出
public class ExportApporvalletters extends AService {
	Log logger = LogFactory.getLog(ExportApporvalletters.class);

	//接受页面报告的ID数组
	@SuppressWarnings("unchecked")
	public void prePdf(Context context){
		String[]  con = HTMLUtil.getParameterValues(context.getRequest(), "credit_idxx", "00");
		String credit_id = null;
		if (con != null && !"00".equals(con[0])) {
			credit_id = con[0];
		}
		context.contextMap.put("credit_id", credit_id);
		exportForOne(context);
	}
	 
	 public void exportForOne(Context context){
		Map<String, Object> outputMap = new HashMap();
		List riskMemoList = null;
		Integer contract_type = null;
		ArrayList creditmaps = new ArrayList();
		ArrayList creditmapsForAuto = new ArrayList();
		List checkPayList=null;
		String credit_id = String.valueOf(context.contextMap.get("credit_id"));
		int productionType = 0;
		 try {
			SelectReportInfo.selectReportInfo_zulin(context, outputMap);
			riskMemoList = (List) DataAccessor.query("riskAudit.selectRiskMemoListByCreditid", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("riskMemoList", riskMemoList);
			contract_type = (Integer) DataAccessor.query("riskAudit.getContractType", context.contextMap, RS_TYPE.OBJECT);
			productionType = LeaseUtil.getProductionTypeByCreditId(credit_id);
			
			//Add by Michael 2012 12-13 增加支票还款明细
			checkPayList=(List) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
			outputMap.put("checkPayList", checkPayList);
			
			//加入锁码方式add by ShenQi
			context.contextMap.put("dataType", "锁码方式");
			List lockList = (List) DataAccessor.query(
					"dataDictionary.queryDataDictionary", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			context.contextMap.put("lockList", lockList);
			//权限别
			Integer riskLevel = (Integer) DataAccessor.query("riskAudit.getRiskLevel", context.contextMap, RS_TYPE.OBJECT);
			outputMap.put("riskLevel", riskLevel);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		if (productionType == 2 || productionType == 3) {
			//重车
			exportCreditMakeConPdfForAuto(context, outputMap);
		} else if(productionType == 1) {
			//设备
			exportCreditMakeConPdf(context, outputMap);
		} else {
			logger.error("案件类型不正确！【productionType = " + productionType + "】");
		}
	 }
	 
	 public void  getDateMap(Context context){
		String[] con = null;
		Map<String, Object> outputMap = null;
		List riskMemoList = null;
		Integer contract_type = null;
		ArrayList creditmaps = new ArrayList();
		ArrayList creditmapsForAuto = new ArrayList();
		con = (String[]) context.contextMap.get("credtdxx");
		List checkPayList=null;
		for (int ii = 0; ii < con.length; ii++) {
			outputMap = new HashMap();
			// List<Map> bokuanType =new ArrayList();
			context.contextMap.put("credit_id", con[ii]);

			try {
				SelectReportInfo.selectReportInfo_zulin(context, outputMap);
				riskMemoList = (List) DataAccessor.query("riskAudit.selectRiskMemoListByCreditid", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("riskMemoList", riskMemoList);
				contract_type = (Integer) DataAccessor.query("riskAudit.getContractType", context.contextMap, RS_TYPE.OBJECT);
				
				//Add by Michael 2012 12-13 增加支票还款明细
				checkPayList=(List) DataAccessor.query("riskAudit.getCheckPaylines", context.contextMap, DataAccessor.RS_TYPE.LIST);
				outputMap.put("checkPayList", checkPayList);
				
				//加入锁码方式add by ShenQi
				context.contextMap.put("dataType", "锁码方式");
				List lockList = (List) DataAccessor.query(
						"dataDictionary.queryDataDictionary", context.contextMap,
						DataAccessor.RS_TYPE.LIST);
				context.contextMap.put("lockList", lockList);
				//权限别
				Integer riskLevel = (Integer) DataAccessor.query("riskAudit.getRiskLevel", context.contextMap, RS_TYPE.OBJECT);
				outputMap.put("riskLevel", riskLevel);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
			if (contract_type == 3 || contract_type == 4 || contract_type == 6 || contract_type == 8) {
				//重车
				this.exportCreditMakeConPdfForAuto(context, outputMap);
			} else if(contract_type == 0 || contract_type == 1 || contract_type == 2 || contract_type == 5 || contract_type == 7) {
				//设备
				this.exportCreditMakeConPdf(context, outputMap);
			} else {
				logger.error("案件类型不正确！【contract_type = " + contract_type + "】");
			}
		}
	 }
	 
    /**
     * 导出核准函的文件(设备)
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void exportCreditMakeConPdf(Context context, Map<String, Object> outputMap) {
	
	ByteArrayOutputStream baos = null;
 	//Map outputMap = new HashMap();
 	//String[]  con = null;
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefault222 = new Font(bfChinese, 6, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	       
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        NumberFormat nfFSNums = new DecimalFormat("###,###,###,##0.00%");
		       
	        nfFSNums.setGroupingUsed(true);
	        nfFSNums.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        	        
	        // 打开文档
	        document.open();
		    String songjiandanwei = "";
		    String yingyerenyuan="";
		    String quanxianbie="";
		    String pifushuhao="";
		    String shoujianri="";
		    String cust_name="";
		    String farendaibiao="";
		    String yingyezhizhaozhucehao="";
		    String yuewudengjihao="";
		    String baogaoleixing="";
		    String kehuedu="0";
		    String zulinfangshi="";
		    String gongyingshangedu="0";
		    String gongyingshang="";
		    int gongyingshang_type;
		    String ty="";
		    String gysyingyezhizhaozhucehao="";
		    String zhizaoshang = "" ;
		    String bokuanType="无";
		    String bokuanjine="￥ 0.00元";
		    String cust_type="";
		    if(outputMap.get("newbeforemap")!=null){
		    	if(((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN")!=null){
		    		songjiandanwei = ((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN").toString();
		    		
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME")!=null){
		    		yingyerenyuan = ((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE")!=null){
		    		shoujianri = ((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE").toString().substring(0,10);
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON")!=null){
		    		farendaibiao = ((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE")!=null){
		    		yingyezhizhaozhucehao = ((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER")!=null){
		    		yuewudengjihao = ((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER").toString();
		    	}

		    }
		    //权限别需要待定
		    //Modify by Michael 2012 01/13 权限别重新定义，二级及以上不管公司成立日期
//		    if(outputMap.get("rank")!=null&&outputMap.get("defuMonth")!=null){
////		    	if(((HashMap)outputMap.get("rank")).get("RANK")!=null&&outputMap.get("defuMonth")!=null){
////		    		if(outputMap.get("defuMonth").toString().equals("0")){
////		    			if(((HashMap)outputMap.get("rank")).get("RANK").toString().equals("4")){
////		    				quanxianbie = "4";
////		    			}else{
////		    				quanxianbie = "3";
////		    			}
////		    		}else if(outputMap.get("defuMonth").toString().equals("1")){
////		    			quanxianbie =((HashMap)outputMap.get("rank")).get("RANK").toString();
////		    		}else{
////		    			quanxianbie = "3";
////		    		}		    		
////		    	}
//		    	
//		    	if(((HashMap)outputMap.get("rank")).get("RANK").toString().equals("1")&&outputMap.get("defuMonth").toString().equals("0")){
//		    		quanxianbie = "2";
//		    	}else{
//		    		quanxianbie =((HashMap)outputMap.get("rank")).get("RANK").toString();
//		    	}
//		    	
//	    	}else if(outputMap.get("rank")!=null&&outputMap.get("defuMonth")==null){
//	    		if(((HashMap)outputMap.get("rank")).get("RANK")!=null){
//	    			quanxianbie = ((HashMap)outputMap.get("rank")).get("RANK").toString();
//	    		}
//	    	}else{
//	    		quanxianbie = "2";
//	    	}
		    
		    quanxianbie = outputMap.get("riskLevel") == null ? "N/A" : outputMap.get("riskLevel").toString();
		    
		    if(outputMap.get("customeredu")!=null){
		    	if(((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE").toString();
		    	}
	    	}
		  /*  if(outputMap.get("applydu")!=null){
		    	if(((HashMap)outputMap.get("applydu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("applydu")).get("GRANT_PRICE").toString();
		    	}
		    	if(((HashMap)outputMap.get("applydu")).get("NAME")!=null){
		    		gongyingshang = ((HashMap)outputMap.get("applydu")).get("NAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("applydu")).get("SUPP_TYPE")!=null){
		    		gongyingshang_type = DataUtil.intUtil(((HashMap)outputMap.get("applydu")).get("SUPP_TYPE").toString());
		    		
		    		if(gongyingshang_type==0){
		    			ty="非大型供应商";
		    		}else{
		    			ty="大型供应商";
		    		}
		    	}
		    	
		    	if(((HashMap)outputMap.get("applydu")).get("BUSINESS_LICENCE")!=null){
		    		gysyingyezhizhaozhucehao = ((HashMap)outputMap.get("applydu")).get("BUSINESS_LICENCE").toString();
		    	}
	    	}
	    	*/
		    if(outputMap.get("contractType")!=null&&((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE")!=null){
		    	for(int y=0;y<((List)outputMap.get("contractType")).size();y++){
		    		if(((HashMap)((List)outputMap.get("contractType")).get(y)).get("CODE").toString().equals(((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString())){
		    			zulinfangshi = ((HashMap)((List)outputMap.get("contractType")).get(y)).get("FLAG").toString();
		    			break;
		    		}
		    	}
		    	
		    }
		    if(outputMap.get("bokuanType")!=null&&(HashMap)outputMap.get("reportBoKuanMap")!=null){
		    	if(((HashMap)outputMap.get("reportBoKuanMap")).get("PAYWAY")!=null){
			    	for(int y=0;y<((List)outputMap.get("bokuanType")).size();y++){
			    		if(((HashMap)((List)outputMap.get("bokuanType")).get(y)).get("CODE").toString().equals(((HashMap)outputMap.get("reportBoKuanMap")).get("PAYWAY").toString())){
			    			bokuanType = ((HashMap)((List)outputMap.get("bokuanType")).get(y)).get("FLAG").toString();
			    			break;
			    		}
			    	}
		    	}
		    	if(((HashMap)outputMap.get("reportBoKuanMap")).get("PAY_MONEY")!=null){
		    		bokuanjine="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("reportBoKuanMap")).get("PAY_MONEY").toString()));
		    	}
		    }
		    if(outputMap.get("creditMap")!=null){
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE")!=null){
		    		pifushuhao=((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CUST_NAME")!=null){
		    		cust_name=((HashMap)outputMap.get("creditMap")).get("CUST_NAME").toString();
		    		
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE")!=null){
		    		baogaoleixing=((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CUST_TYPE")!=null){
		    		cust_type=((HashMap)outputMap.get("creditMap")).get("CUST_TYPE").toString();
		    	}
		    }
			PdfPTable tT = new PdfPTable(14);
			//页眉
			PdfPTable table = new PdfPTable(14);
			table.setWidthPercentage(100f);
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,12,0.5f,0,0,0));
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
			Phrase phrase = new Phrase();
			phrase.add(table);
			HeaderFooter hf = new HeaderFooter(phrase,false);
			hf.setBorder(0);
			document.setHeader(hf);	
				
			
			//页眉结束
			tT.setWidthPercentage(100f);
			int i=0;
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,14));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14)); 
				i++;
				tT.addCell(makeCellSetColspan2("核准函",PdfPCell.ALIGN_CENTER, fa,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,14));
	    		i++;
	    		//表头的相关信息,第一行
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3("承租人信息（法人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		*/
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		String creditId = (String) context.contextMap.get("credit_id");
	    		String contractType =LeaseUtil.getContractTypeByCreditId(creditId);
	    		int companyCode =LeaseUtil.getCompanyCodeByCreditId(creditId);
	    		String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
	    		if("7".equals(contractType)){
	    			companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
	    		}
				tT.addCell(makeCellSetColspanNull(companyName, PdfPCell.ALIGN_CENTER, FontDefault2,8,0.5f,0,0.5f,0.5f));		
				tT.addCell(makeCellSetColspan3NoLeft("送件单位", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan3NoLeft(songjiandanwei, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,8));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft("营业人员", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yingyerenyuan, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("核准函", PdfPCell.ALIGN_CENTER, FontDefault2,8));
				tT.addCell(makeCellSetColspan2NoTopAndLeft("权限别", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(quanxianbie, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		if(cust_type.equals("0")){
	    			tT.addCell(makeCellSetColspan3NoTop("承租人信息（自然人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	
	    		}else{
	    			tT.addCell(makeCellSetColspan3NoTop("承租人信息（法人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	
	    		}
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("报告编号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(pifushuhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("收件日期", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(shoujianri, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("承租人", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(cust_name, PdfPCell.ALIGN_LEFT, FontDefault222,3));	
				tT.addCell(makeCellSetColspan3NoTop("法人代表", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(farendaibiao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("营业执照注册号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(yingyezhizhaozhucehao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("税务登记号", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yuewudengjihao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("报告类型", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baogaoleixing, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("客户额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft("￥"+nfFSNum.format(Double.parseDouble(kehuedu)), PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		 if(outputMap.get("applydu")!=null){
	    			 for(int applynum=0;applynum<((List)outputMap.get("applydu")).size();applynum++){
			    			HashMap applysin=(HashMap)(((List)outputMap.get("applydu"))).get(applynum);
			    			if(applysin.get("GRANT_PRICE")!=null){
			    				gongyingshangedu += applysin.get("GRANT_PRICE").toString();
			 		    	}
			    			if(applysin.get("NAME")!=null){
			    				gongyingshang += applysin.get("NAME").toString();
			    			}
	    			 }
	 	    	}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("租赁方式", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(zulinfangshi, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				//tT.addCell(makeCellSetColspan2NoTopAndRight("供应商:"+gongyingshang, PdfPCell.ALIGN_LEFT, FontDefault222,3));	
				//tT.addCell(makeCellSetColspan2NoTopAndLeft("对应额度:"+"￥"+nfFSNum.format(Double.parseDouble(gongyingshangedu)), PdfPCell.ALIGN_LEFT, FontDefault22,3));
//				tT.addCell(makeCellSetColspan3NoTop("供应商:"+gongyingshang+"   对应额度:"+"￥"+nfFSNum.format(Double.parseDouble(gongyingshangedu))+"   余额:"+"￥"+nfFSNum.format(Double.parseDouble(outputMap.get("applyGrantLastPrice")==null?"0.0":outputMap.get("applyGrantLastPrice").toString())), PdfPCell.ALIGN_LEFT, FontDefault222,6));
//	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				if (((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString().equals("2")) {
					tT.addCell(makeCellSetColspan3NoTop("是否为三个月新机：", PdfPCell.ALIGN_LEFT, FontDefault22,3));
					String IS_NEW_PRODUCTION = "Y".equals(String.valueOf(((Map)outputMap.get("creditMap")).get("IS_NEW_PRODUCTION")))
							? "是" : "否";
					tT.addCell(makeCellSetColspan2NoTopAndLeft(IS_NEW_PRODUCTION, PdfPCell.ALIGN_LEFT, FontDefault22,3));
					tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				} else {
					tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault222,6));
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				}
	    		i++;
	    		
	    		//评分
	    		int score = LeaseUtil.getScoreByCreditId(creditId);
	    		String scoreLevel = LeaseUtil.getScoreLevelByScore(score);
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("评分/评等", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(String.valueOf(score) + "/" + scoreLevel, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault222,6));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		String credit_id = String.valueOf(context.contextMap.get("credit_id"));
	    		int productionType = LeaseUtil.getProductionTypeByCreditId(creditId);
	    		if(productionType==1){
	    			if(LeaseUtil.isImportEqipByCreditId(creditId)){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				tT.addCell(makeCellSetColspan2NoTopAndRight("是否为进口设备，成本不确定：", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
	    				tT.addCell(makeCellSetColspan2NoTopAndRight("是", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
						tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault222,6));
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}
	    		}
	    		
	    		
	    		if(outputMap.get("applydu")!=null){
	    			for(int applynum=0;applynum<((List)outputMap.get("applydu")).size();applynum++){
		    			HashMap applysin=(HashMap)(((List)outputMap.get("applydu"))).get(applynum);
		    			String suplTrue = "" ;
		    			String shouXinEDu = "";
		    			String shouXinYuE = "";
		    			if(applysin.get("GRANT_PRICE")!=null){
		 		    		kehuedu = applysin.get("GRANT_PRICE").toString();
		 		    	}
		 		    	if(applysin.get("NAME")!=null){
		 		    		gongyingshang = applysin.get("NAME").toString();
		 		    	}
		 		    	if(applysin.get("SUPP_TYPE")!=null){
		 		    		gongyingshang_type = DataUtil.intUtil(applysin.get("SUPP_TYPE").toString());
		 		    		
		 		    		if(gongyingshang_type==0){
		 		    			ty="非大型供应商";
		 		    		}else{
		 		    			ty="大型供应商";
		 		    		}
		 		    	}
		 		    	
		 		    	if(applysin.get("BUSINESS_LICENCE")!=null){
		 		    		gysyingyezhizhaozhucehao = applysin.get("BUSINESS_LICENCE").toString();
		 		    	}
		 		    	//制造商和制造商状态
		 		    	if(applysin.get("MANUFACTURER") != null){
		 		    		zhizaoshang = applysin.get("MANUFACTURER").toString() ;
		 		    	}
		 		    	if(applysin.get("SUPL_TRUE") != null){
		 		    		suplTrue = applysin.get("SUPL_TRUE").toString() ;
		 		    		if(suplTrue.equals("1")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[ √ ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("2")){
		 		    			suplTrue = "回购[  ]   回购含灭失[ √ ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("3")){
		 		    			suplTrue = "回购[ √ ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("4")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   " ;
		 		    		} else {
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		}
		 		    	}
		 		    	//授信额度
		 		    	if(applysin.get("GRANT_PRICE") != null ){
		 		    		shouXinEDu = "授信额度："+ nfFSNum.format(Double.parseDouble(applysin.get("GRANT_PRICE").toString()));
		 		    	} else {
		 		    		shouXinEDu = "该供应商未授信" ;
		 		    	}
		 		    	//余额
		 		    	if(applysin.get("applyGrantLastPrice") != null){
		 		    		shouXinYuE = nfFSNum.format(Double.parseDouble(applysin.get("applyGrantLastPrice").toString()));
		 		    	} 
		 		    	
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoTopAndRight("供应商："+gongyingshang, PdfPCell.ALIGN_LEFT, FontDefault22,6));
						tT.addCell(makeCellSetColspan3NoTop("供应商类别:"+ty + "、          级别:" + applysin.get("SUPP_LEVEL"), PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoTopAndRight("营业执照注册号:"+gysyingyezhizhaozhucehao, PdfPCell.ALIGN_LEFT, FontDefault22,6));		
						tT.addCell(makeCellSetColspan3NoTop("供应商性质: "+suplTrue, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight(shouXinEDu, PdfPCell.ALIGN_LEFT, FontDefault22,6));		
			    		tT.addCell(makeCellSetColspan3NoTop("授信余额为："+shouXinYuE, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
	    			}
	    		}
	    		if(outputMap.get("manufacturers") != null){
	    			for(int j=0;j<((List)outputMap.get("manufacturers")).size();j++){
	    				String suplTrue = "" ;
	    				Map manufacturers = (Map) ((List)outputMap.get("manufacturers")).get(j) ;
	    				//制造商和制造商状态
		 		    	if(manufacturers.get("MANUFACTURER") != null){
		 		    		zhizaoshang = manufacturers.get("MANUFACTURER").toString() ;
		 		    	}
		 		    	if(manufacturers.get("SUPL_TRUE") != null){
		 		    		suplTrue = manufacturers.get("SUPL_TRUE").toString() ;
		 		    		if(suplTrue.equals("1")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[ √ ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("2")){
		 		    			suplTrue = "回购[  ]   回购含灭失[ √ ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("3")){
		 		    			suplTrue = "回购[ √ ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("4")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   " ;
		 		    		} else {
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		}
		 		    	}
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight("制造商："+zhizaoshang, PdfPCell.ALIGN_LEFT, FontDefault22,6));
//			    		tT.addCell(makeCellSetColspan3NoTop("制造商性质: "+suplTrue, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellSetColspan3NoTop("制造商性质: 回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   ", PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_CENTER, FontDefault22,6));		
			    		tT.addCell(makeCellSetColspan3NoTop("营业执照注册号:", PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}
	    		}
	    		if(((List)outputMap.get("danbaorens")).size()>0){
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("担保人信息",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		}
	    		for(int k=0;k<((List)outputMap.get("danbaorens")).size();k++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("担保人名称", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    			String danbaorenname="";
	    			String zhengjianhao="";
	    			String zhengjianpiao="";
	    			//证件类型
	    			String zhengjianType = "" ;
	    			Map danbaoren = (Map) ((List)outputMap.get("danbaorens")).get(k);
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME")!=null){
	    				danbaorenname=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME").toString();
	    			}
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE")!=null){
	    				zhengjianhao=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE").toString();
	    			}
	    			//证件类型
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ZHENGJIANTYPE")!=null){
	    				zhengjianType=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ZHENGJIANTYPE").toString() ;
	    				if("0".equals(zhengjianType)){
	    					zhengjianType = "身份证" ;
	    				} else if("1".equals(zhengjianType)){
	    					zhengjianType = "港澳台身份证" ;
	    				} else if("2".equals(zhengjianType)){
	    					zhengjianType = "护照" ;
	    				} else if("3".equals(zhengjianType)){
	    					zhengjianType = "其他" ;
	    				} else if("99".equals(zhengjianType)){
	    					zhengjianType = "组织机构代码证号" ;
	    				}
	    			}
					tT.addCell(makeCellSetColspan2NoTopAndRight(danbaorenname, PdfPCell.ALIGN_LEFT, FontDefault22,2));	
					tT.addCell(makeCellSetColspan2NoTopAndRight(zhengjianType, PdfPCell.ALIGN_LEFT, FontDefault22,2));	
					tT.addCell(makeCellSetColspan3NoTop(zhengjianhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    		tT.addCell(makeCellSetColspan3NoTop("本票",PdfPCell.ALIGN_LEFT, FontDefault22,1));
		    		if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ISTYPE")!=null){
		    			if((((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ISTYPE").toString()).equals("0")){
		    				zhengjianpiao="是";
		    			}
		    			else
		    			{
		    				zhengjianpiao="否";
		    			}
		    		}else{
		    			zhengjianpiao="否";
		    		}
		    		tT.addCell(makeCellSetColspan3NoTop(zhengjianpiao,PdfPCell.ALIGN_LEFT, FontDefault22,1));
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;  
//		    		if(i%55==0){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    		}
	    		i++;
	    		//i+=((List)outputMap.get("danbaorens")).size();
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("设备内容",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("制造商", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
    			tT.addCell(makeCellSetColspan2NoTopAndRight("厂牌", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
    			tT.addCell(makeCellSetColspan2NoTopAndRight("产品名称", PdfPCell.ALIGN_LEFT, FontDefault22,1));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("规格型号", PdfPCell.ALIGN_LEFT, FontDefault22,1));
    			
				tT.addCell(makeCellSetColspan2NoTopAndRight("留购价(元)", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				tT.addCell(makeCellSetColspan2NoTopAndRight("含税单价(元)", PdfPCell.ALIGN_LEFT, FontDefault222,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("单价(元)", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("数量", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				
				tT.addCell(makeCellSetColspan2NoTopAndRight("单位", PdfPCell.ALIGN_LEFT, FontDefault22,1));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("合计", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				tT.addCell(makeCellSetColspan2NoTopAndRight("锁码方式", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				tT.addCell(makeCellSetColspan3NoTop("备注", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		String shebeizongjia="";
	    		String totalD="";
	    		if(outputMap.get("sheBeiHeX")!=null){
	    			shebeizongjia=nfFSNum.format(Double.parseDouble((outputMap.get("sheBeiHeX")).toString()));
	    			//System.out.println(shebeizongjia.toString().replaceAll(",", ""));
	    			totalD=TfAmt.num2cn(shebeizongjia.toString().replaceAll(",", ""));
	    			shebeizongjia="￥"+nfFSNum.format(Double.parseDouble((outputMap.get("sheBeiHeX")).toString()));
	    		}
	    		for(int h=0;h<((List)outputMap.get("equipmentsList")).size();h++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			String thingname="";
	    			String thingTypeName="";
	    			String thingkind="";
	    			String changpai="";
	    			Double total=0.0;
	    			String liugoujia="";
	    			String hanshuidanjia="";
	    			String danjia="";
	    			String shuliang="";
	    			String danwei="";
	    			String totals="";
	    			String lockCode="";
	    			String beizhu="";
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME")!=null&&((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MANUFACTURER")!=null){
	    				thingname=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME").toString();
	    				thingkind=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MANUFACTURER").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TYPE_NAME")!=null){
	    				thingTypeName=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TYPE_NAME").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL")!=null){
	    				total=Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL").toString());
	    				totals=nfFSNum.format(total);
	    				
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC")!=null){
	    				changpai=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("STAYBUY_PRICE")!=null){
	    				liugoujia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("STAYBUY_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("SHUI_PRICE")!=null){
	    				hanshuidanjia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("SHUI_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT_PRICE")!=null){
	    				danjia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("AMOUNT")!=null){
	    				shuliang=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("AMOUNT").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT")!=null){
	    				danwei=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT").toString();
	    			}
	    			//加入锁码方式 add by Shen Qi see mantis356
	    			List lockList=(List)context.contextMap.get("lockList");
	    			if(((HashMap)((List)(outputMap.get("equipmentsList"))).get(h)).get("LOCK_CODE")!=null) {
	    				for(int j=0;lockList!=null&&j<lockList.size();j++) {
		    				if(((Map)(lockList.get(j))).get("CODE").toString().equals(((HashMap)((List)(outputMap.get("equipmentsList"))).get(h)).get("LOCK_CODE").toString())) {
		    					lockCode=(String)((Map)(lockList.get(j))).get("FLAG");
		    				}
		    			}
	    			}
	    			
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MEMO")!=null){
	    				beizhu=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MEMO").toString();
	    			}
	    			
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingkind, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingTypeName, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(changpai, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			
					tT.addCell(makeCellSetColspan2NoTopAndRight(liugoujia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(hanshuidanjia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(danjia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(shuliang, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					
					tT.addCell(makeCellSetColspan2NoTopAndRight(danwei, PdfPCell.ALIGN_LEFT, FontDefault222,1));	
					tT.addCell(makeCellSetColspan2NoTopAndRight(totals, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					
					tT.addCell(makeCellSetColspan2NoTopAndRight(lockCode, PdfPCell.ALIGN_LEFT, FontDefault222,1));	
					tT.addCell(makeCellSetColspan3NoTop(beizhu, PdfPCell.ALIGN_LEFT, FontDefault22,1));	
					
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
//		    		if(i%55==0){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    		}
	    		//i+=((List)outputMap.get("equipmentsList")).size();
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("总价:"+totalD, PdfPCell.ALIGN_LEFT, FontDefault22,4));	
				tT.addCell(makeCellSetColspan3NoTop("总价小写:"+shebeizongjia, PdfPCell.ALIGN_LEFT, FontDefault22,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("设备放置地",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//Add by Michael 2012 4-23 增加设备放置地
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight(((HashMap)outputMap.get("schemeMap")).get("EQUPMENT_ADDRESS").toString(), PdfPCell.ALIGN_LEFT, FontDefault22,12));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("合同归户",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double benjinyue=0.0;
	    		double zujinyue=0.0;
	    		/*if(outputMap.get("schemeMap")!=null){
	    			if(outputMap.get("custguihu")!=null){
	    				if(((HashMap)outputMap.get("custguihu")).get("SUMLASTPRICE")!=null){
	    					benjinyue=Double.parseDouble(((HashMap)outputMap.get("custguihu")).get("SUMLASTPRICE").toString());
	    				}	    				
	    			}
	    			if(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("schemeMap")).get("HEAD_HIRE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("HEAD_HIRE").toString());	    				
	    			}
	    			
	    		}*/
	    		if(outputMap.get("creditriskcontrolpassMap")==null){
	    			if(outputMap.get("sumIrrMonthPriceAndLastPrice")!=null){
		    			if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN")!=null){
		    				benjinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN").toString());
		    			}
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());	    				
	    			}
	    		}else if(((HashMap)outputMap.get("creditriskcontrolpassMap")).get("ID")!=null){
	    			if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN")!=null){
	    				benjinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN").toString());
	    			}
	    		}
	    		double TOTAL_IRR_MONTH_PRICE = 0.0d ;
	    		if(outputMap.get("irrMonthPaylines")!=null && ((List)outputMap.get("irrMonthPaylines")).size() > 0){
	    			/*if(outputMap.get("custguihu")!=null){
	    				if(((HashMap)outputMap.get("custguihu")).get("SHENGYUZUJIN")!=null){
	    					zujinyue=Double.parseDouble(((HashMap)outputMap.get("custguihu")).get("SHIJISHENGYUZUJIN").toString());
	    				}	    				
	    			}*/
	    			List irrMonthTemp = (List) outputMap.get("irrMonthPaylines") ;
	    			for(int j = 0 ;j<irrMonthTemp.size();j++){
	    				Map map = (Map) irrMonthTemp.get(j) ;
	    				if(map.get("IRR_MONTH_PRICE") == null){
	    					map.put("IRR_MONTH_PRICE", 0) ;
	    				}
	    				if(map.get("IRR_MONTH_PRICE_END") == null){
	    					map.put("IRR_MONTH_PRICE_END", 0) ;
	    				}
	    				if(map.get("IRR_MONTH_PRICE_START") == null){
	    					map.put("IRR_MONTH_PRICE_START", 0) ;
	    				}
	    				TOTAL_IRR_MONTH_PRICE += Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) *
	    						(Integer.parseInt(map.get("IRR_MONTH_PRICE_END").toString()) - Integer.parseInt(map.get("IRR_MONTH_PRICE_START").toString()) + 1) ;
	    			}
	    			
	    			if(outputMap.get("creditriskcontrolpassMap")==null){
	    				if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN")!=null){
	    					zujinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN").toString());
		    			}
	    			/*
		    			List irrMonthPaylines=(List)outputMap.get("irrMonthPaylines");
		    			for(int t=0;t<irrMonthPaylines.size();t++){
		    				HashMap yingfuMap=(HashMap)irrMonthPaylines.get(t);
		    				double summoney=Double.parseDouble(yingfuMap.get("IRR_MONTH_PRICE").toString())*(Integer.parseInt(yingfuMap.get("IRR_MONTH_PRICE_END").toString())-Integer.parseInt(yingfuMap.get("IRR_MONTH_PRICE_START").toString())+1);
		    				zujinyue+=summoney;
		    			}
		    			*/
	    				zujinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC").toString());
	    			}else if(((HashMap)outputMap.get("creditriskcontrolpassMap")).get("ID")!=null){
	    				if(outputMap.get("sumIrrMonthPriceAndLastPrice")!=null){
	    					if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN")!=null){
	    						zujinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN").toString());
	    					}
	    				}
	    			}
	    		}
	    		double fenxianedu=0.0;
	    		/*if(outputMap.get("riskEduBenan")!=null){
	    			if(outputMap.get("riskEdu")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString());
	    			}
	    		}else{
	    			if(outputMap.get("riskEdu")!=null&&outputMap.get("creditshemadetail")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString());
	    			}
	    		}*/
	    		if(outputMap.get("creditcontract")==null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString())+Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());
	    			}
	    		}else if(((HashMap)outputMap.get("creditcontract")).get("RECT_ID")!=null){
	    			if(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString());
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			//tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				//tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(fenxianedu), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(outputMap.get("GUIHUOWN") == null? 0.0:outputMap.get("GUIHUOWN")), PdfPCell.ALIGN_LEFT, FontDefault22,3));
//				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(benjinyue), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				
				tT.addCell(makeCellSetColspan2NoTopAndRight("租金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(outputMap.get("GUIHUIRR") == null? 0.0:outputMap.get("GUIHUIRR")), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
//				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(TOTAL_IRR_MONTH_PRICE), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
//				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(zujinyue), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("租金余额:"+"￥"+nfFSNum.format(zujinyue),PdfPCell.ALIGN_LEFT, FontDefault22,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		*/
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("本案",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double benanbenjinyue=0.0;
	    		String fengxianedu="";
	    		double shenqingbokuane=0.0;
	    		int zulinqishu=0;
	    		if(outputMap.get("creditshemadetail")!=null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				benanbenjinyue=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				fengxianedu="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString()));
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC")!=null){
	    				shenqingbokuane=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null){
	    				shenqingbokuane-=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TERM")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("LEASE_COURSE")!=null){
	    				zulinqishu=Integer.parseInt(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TERM").toString())*Integer.parseInt(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_COURSE").toString());
	    				
	    			}
	    			
	    		}
	    		
	    		/*
				 *2012/03/21 Yang Yun 
				 *导出核准函（重车）本金余额计算错误，要加入计算TR的费用 
				 */
				List<Map<String, Object>> feeList = (List<Map<String, Object>>) DataAccessor.query("rentContract.getFeeList", (Map) outputMap.get("creditMap"), DataAccessor.RS_TYPE.LIST);
				double feeForTr = 0D;
				for (Map<String, Object> map : feeList) {
					if((Integer)map.get("IS_LEASERZE_COST") == 1){
						feeForTr +=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						//feeForTr += ((BigDecimal)map.get("FEE")).doubleValue();
					}
				}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(fengxianedu, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(benanbenjinyue + feeForTr), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("申请拨款金额", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(shenqingbokuane), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("租赁期数", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(zulinqishu+"期", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double baozhengjin=0.0;
	    		String baozhengjins = "";
	    		String baozhengjinchengshu="";
	    		String applypromisemoney="";
	    		String applypromiserate="￥0.00";
	    		String promisemctoag="￥0.00";
	    		String promisemctoagrate="￥0.00";
	    		String mycompanypromse="";
	    		String shuijin="";
	    		String pingjundichong="";
	    		String qimofanhuan="";
	    		String promsemoneytype="";
	    		String mycompanypromsesum="￥0.00";
	    		String applypromisesum="￥0.00";
	    		String promiseshui="￥0.00";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		/*
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			baozhengjin=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());
		    			
		    			baozhengjins="￥"+this.updateMoneyStr(baozhengjin+"", nfFSNum);
		    		}*/
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE")!=null){
		    			baozhengjin=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE").toString());
		    			
		    			baozhengjins="￥"+this.updateMoneyStr(baozhengjin+"", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE")!=null){
		    			baozhengjinchengshu=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE").toString()))+"%";
		    			
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG")!=null){
		    			//applypromisemoney=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG").toString()));
		    			applypromisemoney="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_AG", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE")!=null){
		    			//applypromiserate=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE").toString()));
		    			applypromiserate="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_AGRATE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG")!=null){
		    			//promisemctoag=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString()));
		    			promisemctoag="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_MCTOAG", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE")!=null){
		    			//promisemctoag=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString()));
		    			promisemctoagrate="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_MCTOAGRATE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG")!=null){
		    			double agsum=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG").toString());
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE")!=null){
		    				agsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE").toString());
		    			}
		    			applypromisesum=nfFSNum.format(agsum);
		    			applypromisesum="￥"+applypromisesum;
		    		}
		    		
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY")!=null){
		    			promsemoneytype=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			
		    			//shuijin=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString()));
		    			//shuijin="￥"+nfFSNum.format(DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE"))+DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE_TAX")));
		    			shuijin="￥"+nfFSNum.format(DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null){
		    			//mycompanypromse=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString()));
		    			mycompanypromse="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_CMPRICE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			//mycompanypromse=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString()));
		    			double promiseshuifake=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString());
		    			
		    			promiseshui=nfFSNum.format(promiseshuifake);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null){
		    			pingjundichong="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			qimofanhuan="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			//Double mycomsum=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString());
		    			Double mycomsum=0.0;
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG")!=null){
		    				mycomsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString());
		    			}
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE")!=null){
		    				mycomsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE").toString());
		    			}
		    			mycompanypromsesum=nfFSNum.format(mycomsum);
		    			//mycompanypromsesum="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_CMPRICE", nfFSNum);
		    			mycompanypromsesum="￥"+mycompanypromsesum;
		    		}
	        	}	
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baozhengjins, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("保证金成数", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(baozhengjinchengshu, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		String tr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE")!=null){
		    			tr=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE").toString())/100);
		    		}
	    		}
	    		String hetongtr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST")!=null){
		    			hetongtr=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST").toString())/100);
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("合同TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(hetongtr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("税后TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(tr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金入账金流", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("(1)保证金："+applypromisemoney+"元  稅金：0元 ", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		*/
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金入账金流", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
    			tT.addCell(makeCellSetColspan3NoTop("入我司  保证金："+mycompanypromse+"元  稅金："+shuijin+"元 合计："+promiseshui+"元;", PdfPCell.ALIGN_LEFT, FontDefault222,9));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
	    		tT.addCell(makeCellSetColspan3NoTop("我司入供应商："+promisemctoag+" 税金："+promisemctoagrate+" 合计："+mycompanypromsesum+"元;", PdfPCell.ALIGN_LEFT, FontDefault222,9));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("合计："+applypromisemoney+"元入供应商;", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%55==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			i++;
	    		}
	    		*/
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan3NoTop("入供应商   保证金："+applypromisemoney+"元  稅金："+applypromiserate+"元      合计："+applypromisesum+"元", PdfPCell.ALIGN_LEFT, FontDefault222,9));		

				tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		String avdichong="";
	    		String avdichongqishu="0";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				avdichong="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString())+DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE_TAX")));
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PERIOD")!=null){
	    				avdichongqishu=((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PERIOD").toString();
	    			}
	    		}
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));	
    			String depositType = null;
    			if ("7".equals(((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString())) {
    				depositType = "一次性抵冲";
				} else {
					depositType = "平均抵冲";
				}
				tT.addCell(makeCellSetColspan3NoTop(depositType + "："+pingjundichong+"元; 用于最后抵冲含税金额/期数："+avdichong+"元/"+avdichongqishu+"期;  期末返还："+qimofanhuan+"元", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if (feeList != null) {
	    			for (int j = 1; j <= feeList.size(); j++) {
		    			if (j % 2 != 0) {
		    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight((String) feeList.get(j - 1).get("FEE_NAME"), PdfPCell.ALIGN_LEFT, FontDefault22,3));		
							tT.addCell(makeCellSetColspan2NoTopAndRight("￥" + nfFSNum.format((BigDecimal) feeList.get(j - 1).get("FEE")), PdfPCell.ALIGN_LEFT, FontDefault22,3));
						}
		    			if (j % 2 == 0) {	
							tT.addCell(makeCellSetColspan2NoTopAndRight((String) feeList.get(j - 1).get("FEE_NAME"), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
							tT.addCell(makeCellSetColspan3NoTop("￥" + nfFSNum.format((BigDecimal) feeList.get(j - 1).get("FEE")), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    			}
		    			if (j == feeList.size() && j % 2 != 0) {
		    				tT.addCell(makeCellSetColspan2NoTopAndRight(" ", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
							tT.addCell(makeCellSetColspan3NoTop(" ", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						}
					}
				}
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		String zujinshouqufangshi="";
	    		if(outputMap.get("schemeMap")!=null){
	    			for(int zujins=0;zujins<((List)outputMap.get("payWayList")).size();zujins++){
		    			if(((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE")!=null&&((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE").equals(((HashMap)outputMap.get("schemeMap")).get("PAY_WAY").toString())){
		    				zujinshouqufangshi=((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("FLAG").toString();
		    			}
		    		}
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("租金收取方式", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop(zujinshouqufangshi, PdfPCell.ALIGN_LEFT, FontDefault22,8));	
				tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				i++;
				/*
				tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式", PdfPCell.ALIGN_LEFT, FontDefault22,2));	
				tT.addCell(makeCellSetColspan2NoTopAndRight(bokuanType, PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("拨款金额", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan3NoTop(bokuanjine, PdfPCell.ALIGN_LEFT, FontDefault22,2));
				*/
				
				
	    		
	    		String paypercentbefore="无";
	    		String paypercentafter="无";
	    		String paymoneybefore="无";
	    		String paymoneyafter="无";
	    		String payapplynamebefore="无";
	    		String payapplynameafter="无";
	    		if(outputMap.get("appropiateList")!=null){
	    			List appropiateList=(List)outputMap.get("appropiateList");
	    			for(int h=0;h<appropiateList.size();h++){
	    				HashMap appropiateMap=(HashMap)appropiateList.get(h);
	    				if(appropiateMap.get("TYPE")!=null){
	    					if(appropiateMap.get("TYPE").toString().equals("0")){
	    						if(appropiateMap.get("PAYPERCENT")!=null){
	    							if(!"".equals(appropiateMap.get("PAYPERCENT"))){
	    								paypercentbefore=appropiateMap.get("PAYPERCENT").toString()+"%";
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATEMON")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATEMON"))){
	    								paymoneybefore=nfFSNum.format(Double.parseDouble(appropiateMap.get("APPRORIATEMON").toString()));	    							
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATENAME")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATENAME"))){
	    								payapplynamebefore=appropiateMap.get("APPRORIATENAME").toString();	
	    							}
	    						}
	    					}else if(appropiateMap.get("TYPE").toString().equals("1")){
	    						if(appropiateMap.get("PAYPERCENT")!=null){
	    							if(!"".equals(appropiateMap.get("PAYPERCENT"))){
	    								paypercentafter=appropiateMap.get("PAYPERCENT").toString()+"%";
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATEMON")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATEMON"))){
	    								paymoneyafter=nfFSNum.format(Double.parseDouble(appropiateMap.get("APPRORIATEMON").toString()));
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATENAME")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATENAME"))){
	    								payapplynameafter=appropiateMap.get("APPRORIATENAME").toString();	
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（交机情形）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("交机前  交机前比例："+paypercentbefore+" 交机前金额："+paymoneybefore+" 交机前拨款给："+payapplynamebefore, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("交机后  交机后比例："+paypercentafter+" 交机后金额："+paymoneyafter+" 交机后拨款给："+payapplynameafter, PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		String applypromisemore="";
	    		if(outputMap.get("suplList")!=null&&outputMap.get("creditshemadetail")!=null){
	    			List suplList=(List)outputMap.get("suplList");
	    			for(int h=0;h<suplList.size();h++){
	    				HashMap suplMap=(HashMap)suplList.get(h);
	    				HashMap schemeMap=(HashMap)outputMap.get("creditshemadetail");
	    				if(suplMap.get("CODE")!=null&&schemeMap.get("SUPL_TRUE")!=null){
	    					if(suplMap.get("CODE").toString().equals(schemeMap.get("SUPL_TRUE").toString())){
	    						applypromisemore=suplMap.get("FLAG").toString();
	    						break;
	    					}
	    				}
	    			}
	    		}
	    		String appropriationWay = String.valueOf(((Map)outputMap.get("schemeMap")).get("APPROPRIATION_WAY")!=null?((Map)outputMap.get("schemeMap")).get("APPROPRIATION_WAY"):2);
	    		if("2".equals(appropriationWay)){
	    			
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（勾选）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop("网银[ √ ]  支票[   ]", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
	    		}else if("1".equals(appropriationWay)){
	    			String  deferPeriod= String.valueOf(((Map)outputMap.get("schemeMap")).get("DEFER_PERIOD")!=null?((Map)outputMap.get("schemeMap")).get("DEFER_PERIOD"):0);
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（勾选）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop("网银[   ]  支票[ √ ](延迟拨款期数："+deferPeriod+")", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
		    		
	    			String endorsers ="";
	    			String endorser1 = (String) (((Map)outputMap.get("schemeMap")).get("ENDORSER_1")!=null?((Map)outputMap.get("schemeMap")).get("ENDORSER_1"):"           ");
	    			String endorser2 = (String) (((Map)outputMap.get("schemeMap")).get("ENDORSER_2")!=null?((Map)outputMap.get("schemeMap")).get("ENDORSER_2"):"           ");
	    			endorsers = "第一背书人：" + endorser1 +" 第二背书人：" + endorser2;
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("背书人", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop(endorsers, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("供应商保证", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop(applypromisemore, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double yingfuzujin=0.0;
	    		int monthstart=0;
	    		int monthend=0;
	    		if(outputMap.get("irrMonthPaylines")!=null){
	    			
	    			List irrMonthPaylines = (List)outputMap.get("irrMonthPaylines");
	    			if(irrMonthPaylines.size()>0){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tT.addCell(makeCellSetColspan3NoTop("融资租赁方案测算方式一",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;		    		
//			    		if(i%55==0){
//			    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//			    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//			    			i++;
//			    		}
			    		for(int l=0;l<irrMonthPaylines.size();l++){
			    			HashMap irrMonthPayMap = (HashMap)irrMonthPaylines.get(l);
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE")!=null){
			    				yingfuzujin=Double.parseDouble(irrMonthPayMap.get("IRR_MONTH_PRICE").toString());
			    			}
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE_START")!=null){
			    				monthstart=Integer.parseInt(irrMonthPayMap.get("IRR_MONTH_PRICE_START").toString());
			    			}
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE_END")!=null){
			    				monthend=Integer.parseInt(irrMonthPayMap.get("IRR_MONTH_PRICE_END").toString());
			    			}
			    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight("应付租金："+"￥"+nfFSNum.format(yingfuzujin), PdfPCell.ALIGN_LEFT, FontDefault22,4));		
							tT.addCell(makeCellSetColspan3NoTop("从"+monthstart+"到"+monthend+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 
				    		
//				    		if(i%55==0){
//				    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//				    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//				    			i++;
//				    		}
			    		}
			    		//增加增值税含税报价
			    		Map<String, Object> schemeMap = (Map)outputMap.get("schemeMap");
			    		if ("2".equals(schemeMap.get("TAX_PLAN_CODE").toString())) {
			    			Map<String, Object> paylist = new HashMap<String, Object>();
			    			paylist.put("TOTAL_VALUEADDED_TAX", schemeMap.get("TOTAL_VALUEADDED_TAX"));
			    			paylist.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
			    			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
			    			StartPayService.packagePaylinesForValueAdded(paylist);
			    			List newIrrMonthPaylines = (List) paylist.get("irrMonthPaylines");
			    			Map m = null;
			    			for (Object o : newIrrMonthPaylines) {
			    				m = (Map) o;
			    				monthstart = Integer.parseInt(m.get("MONTH_PRICE_START").toString());
			    				monthend = Integer.parseInt(m.get("MONTH_PRICE_END").toString());
			    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    			tT.addCell(makeCellSetColspan2NoTopAndRight("含税应付租金："+"￥"+nfFSNum.format(Double.parseDouble(m.get("MONTH_PRICE_TAX").toString())), PdfPCell.ALIGN_LEFT, FontDefault22,4));		
								tT.addCell(makeCellSetColspan3NoTop("从"+monthstart+"到"+monthend+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
					    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					    		i++; 
							}
						}
	    			}
	    		}
	    		
	    		//Add by Michael 2012 12-13 增加支票还款明细
	    		if(outputMap.get("checkPayList")!=null){
	    			
	    			List checkPayList = (List)outputMap.get("checkPayList");
	    			if(checkPayList.size()>0){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tT.addCell(makeCellSetColspan3NoTop("支票还款明细",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		for(int l=0;l<checkPayList.size();l++){
			    			HashMap checkPayListMap = (HashMap)checkPayList.get(l);
			    			
			    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight("支票还款", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
							tT.addCell(makeCellSetColspan3NoTop("从"+checkPayListMap.get("CHECK_START")+"期到"+checkPayListMap.get("CHECK_END")+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 

			    		}
	    			}
	    		}
	    		
//		    		for(;i<54;i++){
//		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=55){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
		    	document.add(tT);
		    	document.resetHeader();
				document.add(Chunk.NEXTPAGE);	
		    	PdfPTable tTs = new PdfPTable(14);
				tTs.setWidthPercentage(100f);	
		    	
	    		tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
    			i++;
    			if(outputMap.get("otherPriceList")!=null){
	    			
	    			List otherPriceList = (List)outputMap.get("otherPriceList");
	    			if(otherPriceList.size()>0){
	    				tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tTs.addCell(makeCellSetColspan3("其它费用",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    			tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用名称", PdfPCell.ALIGN_CENTER, FontDefault22,2));		
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用金额", PdfPCell.ALIGN_CENTER, FontDefault22,2));		
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用产生时间", PdfPCell.ALIGN_CENTER, FontDefault22,4));		
						tTs.addCell(makeCellSetColspan3("备注", PdfPCell.ALIGN_CENTER, FontDefault22,4));		
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++; 
			    		double feesum=0.0;
			    		for(int f=0;f<otherPriceList.size();f++){
			    			HashMap otherFeeMap = (HashMap)otherPriceList.get(f);
			    			String feename="";
			    			String feecost="0.0";
			    			String makefeedate="";
			    			String feememo="";
			    			
			    			if(otherFeeMap.get("OTHER_NAME")!=null){
			    				feename=otherFeeMap.get("OTHER_NAME").toString();
			    			}
			    			if(otherFeeMap.get("OTHER_PRICE")!=null){
			    				feecost=Double.parseDouble(otherFeeMap.get("OTHER_PRICE").toString())+"";
			    				feesum+=Double.parseDouble(otherFeeMap.get("OTHER_PRICE").toString());
			    			}
			    			if(otherFeeMap.get("OTHER_DATE")!=null){
			    				makefeedate=otherFeeMap.get("OTHER_DATE").toString().substring(0,10);
			    			}
			    			if(otherFeeMap.get("MEMO")!=null){
			    				feememo=otherFeeMap.get("MEMO").toString();
			    			}
			    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tTs.addCell(makeCellSetColspan2NoBottomAndRight(feename, PdfPCell.ALIGN_CENTER, FontDefault22,2));		
							tTs.addCell(makeCellSetColspan2NoBottomAndRight(nfFSNum.format(Double.parseDouble(feecost)), PdfPCell.ALIGN_CENTER, FontDefault22,2));		
							tTs.addCell(makeCellSetColspan2NoBottomAndRight(makefeedate, PdfPCell.ALIGN_CENTER, FontDefault22,4));		
							tTs.addCell(makeCellSetColspan3(feememo, PdfPCell.ALIGN_CENTER, FontDefault22,4));		
				    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 
			    		}
			    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用总额(大写)："+TfAmt.num2cn(nfFSNum.format(feesum).replaceAll(",", "")), PdfPCell.ALIGN_RIGHT, FontDefault22,6));		
						tTs.addCell(makeCellSetColspan3("费用总额："+nfFSNum.format(feesum), PdfPCell.ALIGN_RIGHT, FontDefault22,6));		
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}
    			}
    			
	    		String PROJECT_CONTENT="";
	    		String pSIdeaMapName="";
	    		String pSIdeaMapTime=""; 
	    		if(outputMap.get("pSIdeaMap")!=null){
		    		Map pSIdeaMap= (Map)outputMap.get("pSIdeaMap");
					if(pSIdeaMap.get("PROJECT_CONTENT")!=null){
						PROJECT_CONTENT= pSIdeaMap.get("PROJECT_CONTENT").toString();
					}	
					if(pSIdeaMap.get("NAME")!=null){
						pSIdeaMapName= pSIdeaMap.get("NAME").toString();
					}
					if(pSIdeaMap.get("CRATE_DATE")!=null){
						pSIdeaMapTime= pSIdeaMap.get("CRATE_DATE").toString().substring(0,10);
					}
		    	}
	    		//String[] workercontext = PROJECT_CONTENT.split("\n");
	    		int workcontextlength= Math.round(PROJECT_CONTENT.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    			tTs.addCell(makeCellSetColspanNull("建议承做理由:"+PROJECT_CONTENT,PdfPCell.ALIGN_LEFT, FontDefault2,12,0.5f,0,0.5f,0.5f));	    			
	    			tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if(workcontextlength>=2){
	    			i+=workcontextlength;
	    		}else{
	    			i++;
	    		}
	    		
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
//	    		
	    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+pSIdeaMapName+"时间:"+pSIdeaMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
	    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//pSIdeaMapOther
	    		PROJECT_CONTENT="无";
	    		pSIdeaMapName="";
	    		pSIdeaMapTime=""; 
	    		if(outputMap.get("pSIdeaMapOther")!=null){
		    		Map pSIdeaMap= (Map)outputMap.get("pSIdeaMapOther");
					if(!StringUtils.isEmpty((String) pSIdeaMap.get("PROJECT_CONTENT"))){
						PROJECT_CONTENT= pSIdeaMap.get("PROJECT_CONTENT").toString();
					}	
					if(pSIdeaMap.get("NAME")!=null){
						pSIdeaMapName= pSIdeaMap.get("NAME").toString();
					}
					if(pSIdeaMap.get("CRATE_DATE")!=null){
						pSIdeaMapTime= pSIdeaMap.get("CRATE_DATE").toString().substring(0,10);
					}
		    	}
	    		workcontextlength= Math.round(PROJECT_CONTENT.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    			tTs.addCell(makeCellSetColspanNull("其他租赁条件:"+PROJECT_CONTENT,PdfPCell.ALIGN_LEFT, FontDefault2,12,0f,0,0.5f,0.5f));	    			
	    			tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if(workcontextlength>=2){
	    			i+=workcontextlength;
	    		}else{
	    			i++;
	    		}
	    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		if(!"无".equals(PROJECT_CONTENT)){
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+pSIdeaMapName+"时间:"+pSIdeaMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
	    		} else {
	    			tTs.addCell(makeCellSetColspan3NoTop("   ",PdfPCell.ALIGN_RIGHT, FontDefault2,12));
	    		}
	    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		String manageMapMemo="";
	    		String manageMapName="";
	    		String manageMapTime="";
	    		String manageMapCropMemo="";
	    		String manageMapCropName="";
	    		String manageMapCropTime="";
	    		if(outputMap.get("manageMap")!=null){
		    		Map manageMap= (Map)outputMap.get("manageMap");
					
					if(manageMap.get("MEMO")!=null){
						manageMapMemo=manageMap.get("MEMO").toString();
					}
					
					if(manageMap.get("CREATE_TIME")!=null){
						manageMapTime=manageMap.get("CREATE_TIME").toString().substring(0,10);
					}
					if(manageMap.get("NAME")!=null){
						manageMapName=manageMap.get("NAME").toString();
					}
					
		    	}
	    		if(outputMap.get("manageMapCrop")!=null){
	    			Map manageMapCrop= (Map) outputMap.get("manageMapCrop");
	    			if(manageMapCrop.get("MEMO")!=null){
						manageMapCropMemo=manageMapCrop.get("MEMO").toString();
					}
	    			if(manageMapCrop.get("CREATE_TIME")!=null){
						manageMapCropTime=manageMapCrop.get("CREATE_TIME").toString();
					}
					if(manageMapCrop.get("CREATE_TIME")!=null){
						manageMapCropName=manageMapCrop.get("NAME").toString();
					}
	    		}
	    		if(outputMap.get("manageMapCrop")==null){
	    			int manageMapMemolength= Math.round(manageMapMemo.length()/60);
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("区域主管审核意见:"+manageMapMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}

		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapName+"   时间:"+manageMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;

	    		}else{
	    			int manageMapMemolength= Math.round(manageMapMemo.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("区域主管审核意见:"+manageMapCropMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}
		    		
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapCropName+"   时间:"+manageMapCropTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
	    		}
	    		
	    		//manageMapDGM
	    		manageMapMemo="无";
	    		manageMapName="";
	    		manageMapTime="";
	    		if(outputMap.get("manageMapDGM")!=null){
		    		Map manageMap= (Map)outputMap.get("manageMapDGM");
					if(manageMap.get("MEMO")!=null){
						manageMapMemo=manageMap.get("MEMO").toString();
					}
					
					if(manageMap.get("CREATE_TIME")!=null){
						manageMapTime=manageMap.get("CREATE_TIME").toString().substring(0,10);
					}
					if(manageMap.get("NAME")!=null){
						manageMapName=manageMap.get("NAME").toString();
					}
					int manageMapMemolength= Math.round(manageMapMemo.length()/60);
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("业务副总审核意见:"+manageMapMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}

		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    		if ("无".equals(manageMapMemo)) {
		    			tTs.addCell(makeCellSetColspan3NoTop("     ",PdfPCell.ALIGN_RIGHT, FontDefault2,12));
					} else {
						tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapName+"   时间:"+manageMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
					}
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
		    	}
	    		
	    		for(int riskMome=0;riskMome<((List)outputMap.get("riskMemoList")).size();riskMome++){
	    			
	    			HashMap riskMomeMap = (HashMap)((List)outputMap.get("riskMemoList")).get(riskMome);
	    			String levelStr="";
	    			String levelcontext="";
	    			String levelname="";
	    			String leveltime="";
	    			if(riskMomeMap!=null){
	    				if(riskMomeMap.get("PRCM_USER_LEVEL")!=null){
	    					levelStr=riskMomeMap.get("PRCM_USER_LEVEL").toString();  					
	    				}
	    				if(riskMomeMap.get("PRCM_CONTEXT")!=null){
	    					levelcontext=riskMomeMap.get("PRCM_CONTEXT").toString();  					
	    				}
	    				if(riskMomeMap.get("NAME")!=null){
	    					levelname=riskMomeMap.get("NAME").toString();  					
	    				}
	    				if(riskMomeMap.get("CREATE_TIME")!=null){
	    					leveltime=riskMomeMap.get("CREATE_TIME").toString().substring(0,10);  					
	    				}	    				
	    			}
	    			int levellength=Math.round(levelcontext.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop((levelStr.equals("0") ? "初" : levelStr) +"级审批意见:"+levelcontext,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(levellength>=1){
		    			i+=levellength;
		    		}else{
		    			i++;
		    		}
//		    		if(i%55==0){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault2,14));
//		    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
		    		
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+levelname+"   时间:"+leveltime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
		    		
//
//		    		if(i%55==0){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    			
	    		}
//
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		
	    		int pagenum=Math.round(i/55);
	    		//if(i<=pagenum*55+65){
	    		//	tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		//}
	    		
//	    		if(i<pagenum*55+62){
//		    		for(;i<pagenum*55+61;i++){
//		    			tTs.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=pagenum*55+61){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//	    		}else{
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			for(;i<pagenum*55+61;i++){
//		    			tTs.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=pagenum*55+61){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//	    			
//	    		}
			document.add(tTs);
			document.resetHeader();
			document.add(Chunk.NEXTPAGE);
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "Approvalletters.pdf";
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
     * 2012/02/27 Yang Yun
     * 导出核准函的文件(重车)
     * @param context
     */
    @SuppressWarnings("unchecked")
    public void exportCreditMakeConPdfForAuto(Context context,Map<String, Object> outputMap) {
	
	ByteArrayOutputStream baos = null;
 	//Map outputMap = new HashMap();
 	//String[]  con = null;
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	        //Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        //Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefault222 = new Font(bfChinese, 6, Font.NORMAL);
	        Font FontDefaultTitle = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	       
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        NumberFormat nfFSNums = new DecimalFormat("###,###,###,##0.00%");
		       
	        nfFSNums.setGroupingUsed(true);
	        nfFSNums.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        	        
	        // 打开文档
	        document.open();
	       // con= (String[]) context.contextMap.get("credtdxx");	        
	       // 	context.contextMap.put("credit_id",  con[ii]);
	        //导出的pdf文件所需要的数据全部
	      //  SelectReportInfo.selectReportInfo_zulin(context, outputMap);    	    			    
		    String songjiandanwei = "";
		    String yingyerenyuan="";
		    String quanxianbie="";
		    String pifushuhao="";
		    String shoujianri="";
		    String cust_name="";
		    String farendaibiao="";
		    String yingyezhizhaozhucehao="";
		    String yuewudengjihao="";
		    String baogaoleixing="";
		    String kehuedu="0";
		    String zulinfangshi="";
		    String gongyingshangedu="0";
		    String gongyingshang="";
		    int gongyingshang_type;
		    String ty="";
		    String gysyingyezhizhaozhucehao="";
		    String zhizaoshang = "" ;
		    String bokuanType="无";
		    String bokuanjine="￥ 0.00元";
		    String cust_type="";
		    if(outputMap.get("newbeforemap")!=null){
		    	if(((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN")!=null){
		    		songjiandanwei = ((HashMap)outputMap.get("newbeforemap")).get("DECP_NAME_CN").toString();
		    		
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME")!=null){
		    		yingyerenyuan = ((HashMap)outputMap.get("newbeforemap")).get("YINGYENAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE")!=null){
		    		shoujianri = ((HashMap)outputMap.get("newbeforemap")).get("COMMIT_WIND_DATE").toString().substring(0,10);
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON")!=null){
		    		farendaibiao = ((HashMap)outputMap.get("newbeforemap")).get("LEGAL_PERSON").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE")!=null){
		    		yingyezhizhaozhucehao = ((HashMap)outputMap.get("newbeforemap")).get("BUSINESS_LICENCE_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER")!=null){
		    		yuewudengjihao = ((HashMap)outputMap.get("newbeforemap")).get("TAX_REGISTRATION_NUMBER").toString();
		    	}

		    }
		    //权限别需要待定
		    //Modify by Michael 2012 01/13 权限别重新定义，二级及以上不管公司成立日期
//		    if(outputMap.get("rank")!=null&&outputMap.get("defuMonth")!=null){
////		    	if(((HashMap)outputMap.get("rank")).get("RANK")!=null&&outputMap.get("defuMonth")!=null){
////		    		if(outputMap.get("defuMonth").toString().equals("0")){
////		    			if(((HashMap)outputMap.get("rank")).get("RANK").toString().equals("4")){
////		    				quanxianbie = "4";
////		    			}else{
////		    				quanxianbie = "3";
////		    			}
////		    		}else if(outputMap.get("defuMonth").toString().equals("1")){
////		    			quanxianbie =((HashMap)outputMap.get("rank")).get("RANK").toString();
////		    		}else{
////		    			quanxianbie = "3";
////		    		}		    		
////		    	}
//		    	
//		    	if(((HashMap)outputMap.get("rank")).get("RANK").toString().equals("1")&&outputMap.get("defuMonth").toString().equals("0")){
//		    		quanxianbie = "2";
//		    	}else{
//		    		quanxianbie =((HashMap)outputMap.get("rank")).get("RANK").toString();
//		    	}
//		    	
//	    	}else if(outputMap.get("rank")!=null&&outputMap.get("defuMonth")==null){
//	    		if(((HashMap)outputMap.get("rank")).get("RANK")!=null){
//	    			quanxianbie = ((HashMap)outputMap.get("rank")).get("RANK").toString();
//	    		}
//	    	}else{
//	    		quanxianbie = "2";
//	    	}
		    
		    quanxianbie = outputMap.get("riskLevel") == null ? "N/A" : outputMap.get("riskLevel").toString();

		    if(outputMap.get("customeredu")!=null){
		    	if(((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("customeredu")).get("GRANT_PRICE").toString();
		    	}
	    	}
		  /*  if(outputMap.get("applydu")!=null){
		    	if(((HashMap)outputMap.get("applydu")).get("GRANT_PRICE")!=null){
		    		kehuedu = ((HashMap)outputMap.get("applydu")).get("GRANT_PRICE").toString();
		    	}
		    	if(((HashMap)outputMap.get("applydu")).get("NAME")!=null){
		    		gongyingshang = ((HashMap)outputMap.get("applydu")).get("NAME").toString();
		    	}
		    	if(((HashMap)outputMap.get("applydu")).get("SUPP_TYPE")!=null){
		    		gongyingshang_type = DataUtil.intUtil(((HashMap)outputMap.get("applydu")).get("SUPP_TYPE").toString());
		    		
		    		if(gongyingshang_type==0){
		    			ty="非大型供应商";
		    		}else{
		    			ty="大型供应商";
		    		}
		    	}
		    	
		    	if(((HashMap)outputMap.get("applydu")).get("BUSINESS_LICENCE")!=null){
		    		gysyingyezhizhaozhucehao = ((HashMap)outputMap.get("applydu")).get("BUSINESS_LICENCE").toString();
		    	}
	    	}
	    	*/
		    if(outputMap.get("contractType")!=null&&((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE")!=null){
		    	for(int y=0;y<((List)outputMap.get("contractType")).size();y++){
		    		if(((HashMap)((List)outputMap.get("contractType")).get(y)).get("CODE").toString().equals(((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString())){
		    			zulinfangshi = ((HashMap)((List)outputMap.get("contractType")).get(y)).get("FLAG").toString();
		    			break;
		    		}
		    	}
		    	
		    }
		    if(outputMap.get("bokuanType")!=null&&(HashMap)outputMap.get("reportBoKuanMap")!=null){
		    	if(((HashMap)outputMap.get("reportBoKuanMap")).get("PAYWAY")!=null){
			    	for(int y=0;y<((List)outputMap.get("bokuanType")).size();y++){
			    		if(((HashMap)((List)outputMap.get("bokuanType")).get(y)).get("CODE").toString().equals(((HashMap)outputMap.get("reportBoKuanMap")).get("PAYWAY").toString())){
			    			bokuanType = ((HashMap)((List)outputMap.get("bokuanType")).get(y)).get("FLAG").toString();
			    			break;
			    		}
			    	}
		    	}
		    	if(((HashMap)outputMap.get("reportBoKuanMap")).get("PAY_MONEY")!=null){
		    		bokuanjine="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("reportBoKuanMap")).get("PAY_MONEY").toString()));
		    	}
		    }
		    if(outputMap.get("creditMap")!=null){
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE")!=null){
		    		pifushuhao=((HashMap)outputMap.get("creditMap")).get("CREDIT_CODE").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CUST_NAME")!=null){
		    		cust_name=((HashMap)outputMap.get("creditMap")).get("CUST_NAME").toString();
		    		
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE")!=null){
		    		baogaoleixing=((HashMap)outputMap.get("creditMap")).get("CREDIT_TYPE").toString();
		    	}
		    	if(((HashMap)outputMap.get("creditMap")).get("CUST_TYPE")!=null){
		    		cust_type=((HashMap)outputMap.get("creditMap")).get("CUST_TYPE").toString();
		    	}
		    }
			PdfPTable tT = new PdfPTable(14);
			//页眉
			PdfPTable table = new PdfPTable(14);
			table.setWidthPercentage(100f);
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,12,0.5f,0,0,0));
			table.addCell(this.makeCellSetColspanNull(" ",PdfPCell.ALIGN_LEFT, FontDefault,1,0,0,0,0));
			Phrase phrase = new Phrase();
			phrase.add(table);
			HeaderFooter hf = new HeaderFooter(phrase,false);
			hf.setBorder(0);
			document.setHeader(hf);	
				
			
			//页眉结束
			tT.setWidthPercentage(100f);
			int i=0;
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,14));	
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14)); 
				i++;
				tT.addCell(makeCellSetColspan2("核准函",PdfPCell.ALIGN_CENTER, fa,14));
				i++;
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,14));
				i++;
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,14));
	    		i++;
	    		//表头的相关信息,第一行
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3("承租人信息（法人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		*/
	    		//文字部分
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspanNull(Constants.COMPANY_NAME, PdfPCell.ALIGN_CENTER, FontDefault2,8,0.5f,0,0.5f,0.5f));		
				tT.addCell(makeCellSetColspan3NoLeft("送件单位", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan3NoLeft(songjiandanwei, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault2,8));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft("营业人员", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yingyerenyuan, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan3NoTop("核准函", PdfPCell.ALIGN_CENTER, FontDefault2,8));
				tT.addCell(makeCellSetColspan2NoTopAndLeft("权限别", PdfPCell.ALIGN_CENTER, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(quanxianbie, PdfPCell.ALIGN_CENTER, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		//if(cust_type.equals("0")){
	    			//tT.addCell(makeCellSetColspan3NoTop("承租人信息（自然人）",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	
	    		//}else{
	    			tT.addCell(makeCellSetColspan3NoTop("承租人信息",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	
	    		//}
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("报告编号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(pifushuhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("收件日期", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(shoujianri, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("承租人", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(cust_name, PdfPCell.ALIGN_LEFT, FontDefault222,3));	
				tT.addCell(makeCellSetColspan3NoTop("法人代表", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(farendaibiao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("营业执照注册号/身份证号", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(yingyezhizhaozhucehao, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("税务登记号", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft(yuewudengjihao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("报告类型", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baogaoleixing, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("客户额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndLeft("￥"+nfFSNum.format(Double.parseDouble(kehuedu)), PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		 if(outputMap.get("applydu")!=null){
	    			 for(int applynum=0;applynum<((List)outputMap.get("applydu")).size();applynum++){
			    			HashMap applysin=(HashMap)(((List)outputMap.get("applydu"))).get(applynum);
			    			if(applysin.get("GRANT_PRICE")!=null){
			    				gongyingshangedu += applysin.get("GRANT_PRICE").toString();
			 		    	}
			    			if(applysin.get("NAME")!=null){
			    				gongyingshang += applysin.get("NAME").toString();
			    			}
	    			 }
	 	    	}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				tT.addCell(makeCellSetColspan2NoTopAndRight("租赁方式", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(zulinfangshi, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault222,6));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		Map creditMap = (Map) outputMap.get("creditMap");
	    		String production_type = String.valueOf(creditMap.get("PRODUCTION_TYPE"));
	    		String group_inside = String.valueOf(creditMap.get("GROUP_INSIDE"));
	    		String group_inside_name = null;
	    		if ("3".equals(production_type)) {
	    			if ("1".equals(group_inside)) {
	    				group_inside_name = "集团内";
					} else {
						group_inside_name = "集团外";
					}
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					tT.addCell(makeCellSetColspan2NoTopAndRight("是否集团内", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
					tT.addCell(makeCellSetColspan2NoTopAndRight(group_inside_name, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
					tT.addCell(makeCellSetColspan3NoTop("", PdfPCell.ALIGN_LEFT, FontDefault222,6));
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
				}
	    		
	    		
	    		if(outputMap.get("applydu")!=null){
	    			for(int applynum=0;applynum<((List)outputMap.get("applydu")).size();applynum++){
		    			HashMap applysin=(HashMap)(((List)outputMap.get("applydu"))).get(applynum);
		    			String suplTrue = "" ;
		    			String shouXinEDu = "";
		    			String shouXinYuE = "";
		    			if(applysin.get("GRANT_PRICE")!=null){
		 		    		kehuedu = applysin.get("GRANT_PRICE").toString();
		 		    	}
		 		    	if(applysin.get("NAME")!=null){
		 		    		gongyingshang = applysin.get("NAME").toString();
		 		    	}
		 		    	if(applysin.get("SUPP_TYPE")!=null){
		 		    		gongyingshang_type = DataUtil.intUtil(applysin.get("SUPP_TYPE").toString());
		 		    		
		 		    		if(gongyingshang_type==0){
		 		    			ty="非大型供应商";
		 		    		}else{
		 		    			ty="大型供应商";
		 		    		}
		 		    	}
		 		    	
		 		    	if(applysin.get("BUSINESS_LICENCE")!=null){
		 		    		gysyingyezhizhaozhucehao = applysin.get("BUSINESS_LICENCE").toString();
		 		    	}
		 		    	//制造商和制造商状态
		 		    	if(applysin.get("MANUFACTURER") != null){
		 		    		zhizaoshang = applysin.get("MANUFACTURER").toString() ;
		 		    	}
		 		    	if(applysin.get("SUPL_TRUE") != null){
		 		    		suplTrue = applysin.get("SUPL_TRUE").toString() ;
		 		    		if(suplTrue.equals("1")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[ √ ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("2")){
		 		    			suplTrue = "回购[  ]   回购含灭失[ √ ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("3")){
		 		    			suplTrue = "回购[ √ ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("4")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   " ;
		 		    		} else {
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		}
		 		    	}
		 		    	//授信额度
		 		    	if(applysin.get("GRANT_PRICE") != null ){
		 		    		shouXinEDu = "授信额度："+ nfFSNum.format(Double.parseDouble(applysin.get("GRANT_PRICE").toString()));
		 		    	} else {
		 		    		shouXinEDu = "该供应商未授信" ;
		 		    	}
		 		    	//余额
		 		    	if(applysin.get("applyGrantLastPrice") != null){
		 		    		shouXinYuE = nfFSNum.format(Double.parseDouble(applysin.get("applyGrantLastPrice").toString()));
		 		    	} 
		 		    	
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoTopAndRight("供应商："+gongyingshang, PdfPCell.ALIGN_LEFT, FontDefault22,6));
						tT.addCell(makeCellSetColspan3NoTop("供应商类别:"+ty + "、          级别:" + applysin.get("SUPP_LEVEL"), PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						tT.addCell(makeCellSetColspan2NoTopAndRight("营业执照注册号:"+gysyingyezhizhaozhucehao, PdfPCell.ALIGN_LEFT, FontDefault22,6));		
						tT.addCell(makeCellSetColspan3NoTop("供应商性质: "+suplTrue, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight(shouXinEDu, PdfPCell.ALIGN_LEFT, FontDefault22,6));		
			    		tT.addCell(makeCellSetColspan3NoTop("授信余额为："+shouXinYuE, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
	    			}
	    		}
	    		if(outputMap.get("manufacturers") != null){
	    			for(int j=0;j<((List)outputMap.get("manufacturers")).size();j++){
	    				String suplTrue = "" ;
	    				Map manufacturers = (Map) ((List)outputMap.get("manufacturers")).get(j) ;
	    				//制造商和制造商状态
		 		    	if(manufacturers.get("MANUFACTURER") != null){
		 		    		zhizaoshang = manufacturers.get("MANUFACTURER").toString() ;
		 		    	}
		 		    	if(manufacturers.get("SUPL_TRUE") != null){
		 		    		suplTrue = manufacturers.get("SUPL_TRUE").toString() ;
		 		    		if(suplTrue.equals("1")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[ √ ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("2")){
		 		    			suplTrue = "回购[  ]   回购含灭失[ √ ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("3")){
		 		    			suplTrue = "回购[ √ ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		} else if(suplTrue.equals("4")){
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   " ;
		 		    		} else {
		 		    			suplTrue = "回购[  ]   回购含灭失[  ]   连保[  ]   无[  ]   " ;
		 		    		}
		 		    	}
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight("制造商："+zhizaoshang, PdfPCell.ALIGN_LEFT, FontDefault22,6));
//			    		tT.addCell(makeCellSetColspan3NoTop("制造商性质: "+suplTrue, PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellSetColspan3NoTop("制造商性质: 回购[  ]   回购含灭失[  ]   连保[  ]   无[ √ ]   ", PdfPCell.ALIGN_LEFT, FontDefault22,6));
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_CENTER, FontDefault22,6));		
			    		tT.addCell(makeCellSetColspan3NoTop("营业执照注册号:", PdfPCell.ALIGN_LEFT, FontDefault22,6));	
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}
	    		}
	    		if(((List)outputMap.get("danbaorens")).size()>0){
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("担保人信息",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		}
	    		for(int k=0;k<((List)outputMap.get("danbaorens")).size();k++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("担保人名称", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    			String danbaorenname="";
	    			String zhengjianhao="";
	    			String zhengjianpiao="";
	    			//证件类型
	    			String zhengjianType = "" ;
	    			Map danbaoren = (Map) ((List)outputMap.get("danbaorens")).get(k);
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME")!=null){
	    				danbaorenname=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("NAME").toString();
	    			}
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE")!=null){
	    				zhengjianhao=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("CODE").toString();
	    			}
	    			//证件类型
	    			if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ZHENGJIANTYPE")!=null){
	    				zhengjianType=((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ZHENGJIANTYPE").toString() ;
	    				if("0".equals(zhengjianType)){
	    					zhengjianType = "身份证" ;
	    				} else if("1".equals(zhengjianType)){
	    					zhengjianType = "港澳台身份证" ;
	    				} else if("2".equals(zhengjianType)){
	    					zhengjianType = "护照" ;
	    				} else if("3".equals(zhengjianType)){
	    					zhengjianType = "其他" ;
	    				} else if("99".equals(zhengjianType)){
	    					zhengjianType = "组织机构代码证号" ;
	    				}
	    			}
					tT.addCell(makeCellSetColspan2NoTopAndRight(danbaorenname, PdfPCell.ALIGN_LEFT, FontDefault22,2));	
					tT.addCell(makeCellSetColspan2NoTopAndRight(zhengjianType, PdfPCell.ALIGN_LEFT, FontDefault22,2));	
					tT.addCell(makeCellSetColspan3NoTop(zhengjianhao, PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    		tT.addCell(makeCellSetColspan3NoTop("本票",PdfPCell.ALIGN_LEFT, FontDefault22,1));
		    		if(((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ISTYPE")!=null){
		    			if((((HashMap)((List)outputMap.get("danbaorens")).get(k)).get("ISTYPE").toString()).equals("0")){
		    				zhengjianpiao="是";
		    			}
		    			else
		    			{
		    				zhengjianpiao="否";
		    			}
		    		}else{
		    			zhengjianpiao="否";
		    		}
		    		tT.addCell(makeCellSetColspan3NoTop(zhengjianpiao,PdfPCell.ALIGN_LEFT, FontDefault22,1));
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;  
//		    		if(i%55==0){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    		}
	    		i++;
	    		//i+=((List)outputMap.get("danbaorens")).size();
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("标的物内容",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("制造商", PdfPCell.ALIGN_LEFT, FontDefault22,2));	
    			tT.addCell(makeCellSetColspan2NoTopAndRight("厂牌", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
    			tT.addCell(makeCellSetColspan2NoTopAndRight("产品名称", PdfPCell.ALIGN_LEFT, FontDefault22,1));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("规格型号", PdfPCell.ALIGN_LEFT, FontDefault22,1));
    			
				tT.addCell(makeCellSetColspan2NoTopAndRight("留购价(元)", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				tT.addCell(makeCellSetColspan2NoTopAndRight("含税单价(元)", PdfPCell.ALIGN_LEFT, FontDefault222,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("单价(元)", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("数量", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				
				tT.addCell(makeCellSetColspan2NoTopAndRight("单位", PdfPCell.ALIGN_LEFT, FontDefault22,1));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("合计", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				tT.addCell(makeCellSetColspan3NoTop("备注", PdfPCell.ALIGN_LEFT, FontDefault22,1));
				
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		String shebeizongjia="";
	    		String totalD="";
	    		if(outputMap.get("sheBeiHeX")!=null){
	    			shebeizongjia=nfFSNum.format(Double.parseDouble((outputMap.get("sheBeiHeX")).toString()));
	    			//System.out.println(shebeizongjia.toString().replaceAll(",", ""));
	    			totalD=TfAmt.num2cn(shebeizongjia.toString().replaceAll(",", ""));
	    			shebeizongjia="￥"+nfFSNum.format(Double.parseDouble((outputMap.get("sheBeiHeX")).toString()));
	    		}
	    		for(int h=0;h<((List)outputMap.get("equipmentsList")).size();h++){
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			String thingname="";
	    			String thingTypeName="";
	    			String thingkind="";
	    			String changpai="";
	    			Double total=0.0;
	    			String liugoujia="";
	    			String hanshuidanjia="";
	    			String danjia="";
	    			String shuliang="";
	    			String danwei="";
	    			String totals="";
	    			String beizhu="";
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME")!=null&&((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MANUFACTURER")!=null){
	    				thingname=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("THING_NAME").toString();
	    				thingkind=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MANUFACTURER").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TYPE_NAME")!=null){
	    				thingTypeName=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TYPE_NAME").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL")!=null){
	    				total=Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("TOTAL").toString());
	    				totals=nfFSNum.format(total);
	    				
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC")!=null){
	    				changpai=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MODEL_SPEC").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("STAYBUY_PRICE")!=null){
	    				liugoujia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("STAYBUY_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("SHUI_PRICE")!=null){
	    				hanshuidanjia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("SHUI_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT_PRICE")!=null){
	    				danjia="￥"+nfFSNum.format(Double.parseDouble(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT_PRICE").toString()));
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("AMOUNT")!=null){
	    				shuliang=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("AMOUNT").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT")!=null){
	    				danwei=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("UNIT").toString();
	    			}
	    			if(((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MEMO")!=null){
	    				beizhu=((HashMap)(((List)outputMap.get("equipmentsList")).get(h))).get("MEMO").toString();
	    			}
	    			
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingkind, PdfPCell.ALIGN_LEFT, FontDefault222,2));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingTypeName, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(thingname, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight(changpai, PdfPCell.ALIGN_LEFT, FontDefault222,1));
	    			
					tT.addCell(makeCellSetColspan2NoTopAndRight(liugoujia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(hanshuidanjia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(danjia, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					tT.addCell(makeCellSetColspan2NoTopAndRight(shuliang, PdfPCell.ALIGN_LEFT, FontDefault222,1));
					
					tT.addCell(makeCellSetColspan2NoTopAndRight(danwei, PdfPCell.ALIGN_LEFT, FontDefault222,1));	
					tT.addCell(makeCellSetColspan2NoTopAndRight(totals, PdfPCell.ALIGN_LEFT, FontDefault222,1));	
					tT.addCell(makeCellSetColspan3NoTop(beizhu, PdfPCell.ALIGN_LEFT, FontDefault22,1));	
					
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
//		    		if(i%55==0){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    		}
	    		//i+=((List)outputMap.get("equipmentsList")).size();
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("总价:"+totalD, PdfPCell.ALIGN_LEFT, FontDefault22,4));	
				tT.addCell(makeCellSetColspan3NoTop("总价小写:"+shebeizongjia, PdfPCell.ALIGN_LEFT, FontDefault22,4));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("合同归户",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double benjinyue=0.0;
	    		double zujinyue=0.0;
	    		/*if(outputMap.get("schemeMap")!=null){
	    			if(outputMap.get("custguihu")!=null){
	    				if(((HashMap)outputMap.get("custguihu")).get("SUMLASTPRICE")!=null){
	    					benjinyue=Double.parseDouble(((HashMap)outputMap.get("custguihu")).get("SUMLASTPRICE").toString());
	    				}	    				
	    			}
	    			if(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("schemeMap")).get("HEAD_HIRE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("HEAD_HIRE").toString());	    				
	    			}
	    			
	    		}*/
	    		if(outputMap.get("creditriskcontrolpassMap")==null){
	    			if(outputMap.get("sumIrrMonthPriceAndLastPrice")!=null){
		    			if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN")!=null){
		    				benjinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN").toString());
		    			}
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString());	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
	    				benjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());	    				
	    			}
	    		}else if(((HashMap)outputMap.get("creditriskcontrolpassMap")).get("ID")!=null){
	    			if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN")!=null){
	    				benjinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUBENJIN").toString());
	    			}
	    		}
	    		double TOTAL_IRR_MONTH_PRICE = 0.0d ;
	    		if(outputMap.get("irrMonthPaylines")!=null && ((List)outputMap.get("irrMonthPaylines")).size() > 0){
	    			/*if(outputMap.get("custguihu")!=null){
	    				if(((HashMap)outputMap.get("custguihu")).get("SHENGYUZUJIN")!=null){
	    					zujinyue=Double.parseDouble(((HashMap)outputMap.get("custguihu")).get("SHIJISHENGYUZUJIN").toString());
	    				}	    				
	    			}*/
	    			List irrMonthTemp = (List) outputMap.get("irrMonthPaylines") ;
	    			for(int j = 0 ;j<irrMonthTemp.size();j++){
	    				Map map = (Map) irrMonthTemp.get(j) ;
	    				if(map.get("IRR_MONTH_PRICE") == null){
	    					map.put("IRR_MONTH_PRICE", 0) ;
	    				}
	    				if(map.get("IRR_MONTH_PRICE_END") == null){
	    					map.put("IRR_MONTH_PRICE_END", 0) ;
	    				}
	    				if(map.get("IRR_MONTH_PRICE_START") == null){
	    					map.put("IRR_MONTH_PRICE_START", 0) ;
	    				}
	    				TOTAL_IRR_MONTH_PRICE += Double.parseDouble(map.get("IRR_MONTH_PRICE").toString()) *
	    						(Integer.parseInt(map.get("IRR_MONTH_PRICE_END").toString()) - Integer.parseInt(map.get("IRR_MONTH_PRICE_START").toString()) + 1) ;
	    			}
	    			
	    			if(outputMap.get("creditriskcontrolpassMap")==null){
	    				if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN")!=null){
	    					zujinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN").toString());
		    			}
	    			/*
		    			List irrMonthPaylines=(List)outputMap.get("irrMonthPaylines");
		    			for(int t=0;t<irrMonthPaylines.size();t++){
		    				HashMap yingfuMap=(HashMap)irrMonthPaylines.get(t);
		    				double summoney=Double.parseDouble(yingfuMap.get("IRR_MONTH_PRICE").toString())*(Integer.parseInt(yingfuMap.get("IRR_MONTH_PRICE_END").toString())-Integer.parseInt(yingfuMap.get("IRR_MONTH_PRICE_START").toString())+1);
		    				zujinyue+=summoney;
		    			}
		    			*/
	    				zujinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC").toString());
	    			}else if(((HashMap)outputMap.get("creditriskcontrolpassMap")).get("ID")!=null){
	    				if(outputMap.get("sumIrrMonthPriceAndLastPrice")!=null){
	    					if(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN")!=null){
	    						zujinyue=Double.parseDouble(((HashMap)outputMap.get("sumIrrMonthPriceAndLastPrice")).get("SHENGYUZUJIN").toString());
	    					}
	    				}
	    			}
	    		}
	    		double fenxianedu=0.0;
	    		/*if(outputMap.get("riskEduBenan")!=null){
	    			if(outputMap.get("riskEdu")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString());
	    			}
	    		}else{
	    			if(outputMap.get("riskEdu")!=null&&outputMap.get("creditshemadetail")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString());
	    			}
	    		}*/
	    		if(outputMap.get("creditcontract")==null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null&&((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString())+Double.parseDouble(((HashMap)outputMap.get("schemeMap")).get("LEASE_RZE").toString());
	    			}
	    		}else if(((HashMap)outputMap.get("creditcontract")).get("RECT_ID")!=null){
	    			if(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE")!=null){
	    				fenxianedu=Double.parseDouble(((HashMap)outputMap.get("riskEdu")).get("SUMLEASE_RZE").toString());
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			//tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				//tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(fenxianedu), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(outputMap.get("GUIHUOWN") == null? 0.0:outputMap.get("GUIHUOWN")), PdfPCell.ALIGN_LEFT, FontDefault22,3));
//				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(benjinyue), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				
				tT.addCell(makeCellSetColspan2NoTopAndRight("租金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(outputMap.get("GUIHUIRR") == null? 0.0:outputMap.get("GUIHUIRR")), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
//				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(TOTAL_IRR_MONTH_PRICE), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
//				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(zujinyue), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("租金余额:"+"￥"+nfFSNum.format(zujinyue),PdfPCell.ALIGN_LEFT, FontDefault22,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		*/
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tT.addCell(makeCellSetColspan3NoTop("本案",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double benanbenjinyue=0.0;
	    		String fengxianedu="";
	    		double shenqingbokuane=0.0;
	    		int zulinqishu=0;
	    		if(outputMap.get("creditshemadetail")!=null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				benanbenjinyue=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE")!=null){
	    				fengxianedu="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_RZE").toString()));
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("HEAD_HIRE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
	    				benanbenjinyue+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC")!=null){
	    				shenqingbokuane=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TOPRIC").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null){
	    				shenqingbokuane-=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString());
	    				
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TERM")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("LEASE_COURSE")!=null){
	    				zulinqishu=Integer.parseInt(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_TERM").toString())*Integer.parseInt(((HashMap)outputMap.get("creditshemadetail")).get("LEASE_COURSE").toString());
	    				
	    			}
	    			
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("风险额度", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(fengxianedu, PdfPCell.ALIGN_LEFT, FontDefault22,3));
				
				/*
				 *2012/03/21 Yang Yun 
				 *导出核准函（重车）本金余额计算错误，要加入计算TR的费用 
				 */
				List<Map<String, Object>> feeList = (List<Map<String, Object>>) DataAccessor.query("rentContract.getFeeList", (Map) outputMap.get("creditMap"), DataAccessor.RS_TYPE.LIST);
				double feeForTr = 0D;
				for (Map<String, Object> map : feeList) {
					if((Integer)map.get("IS_LEASERZE_COST") == 1){
						feeForTr +=new BigDecimal(DataUtil.doubleUtil(map.get("FEE"))/1.06).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
						//feeForTr += ((BigDecimal)map.get("FEE")).doubleValue();
					}
				}
				tT.addCell(makeCellSetColspan2NoTopAndRight("本金余额", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop("￥"+nfFSNum.format(benanbenjinyue + feeForTr), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("申请拨款金额", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight("￥"+nfFSNum.format(shenqingbokuane), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("租赁期数", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(zulinqishu+"期", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double baozhengjin=0.0;
	    		String baozhengjins = "";
	    		String baozhengjinchengshu="";
	    		String applypromisemoney="";
	    		String applypromiserate="￥0.00";
	    		String promisemctoag="￥0.00";
	    		String promisemctoagrate="￥0.00";
	    		String mycompanypromse="";
	    		String shuijin="";
	    		String pingjundichong="";
	    		String qimofanhuan="";
	    		String promsemoneytype="";
	    		String mycompanypromsesum="￥0.00";
	    		String applypromisesum="￥0.00";
	    		String promiseshui="￥0.00";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		/*
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			baozhengjin=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString());
		    			
		    			baozhengjins="￥"+this.updateMoneyStr(baozhengjin+"", nfFSNum);
		    		}*/
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE")!=null){
		    			baozhengjin=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE").toString());
		    			
		    			baozhengjins="￥"+this.updateMoneyStr(baozhengjin+"", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE")!=null){
		    			baozhengjinchengshu=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_PRICE_RATE").toString()))+"%";
		    			
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG")!=null){
		    			//applypromisemoney=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG").toString()));
		    			applypromisemoney="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_AG", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE")!=null){
		    			//applypromiserate=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE").toString()));
		    			applypromiserate="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_AGRATE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG")!=null){
		    			//promisemctoag=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString()));
		    			promisemctoag="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_MCTOAG", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE")!=null){
		    			//promisemctoag=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString()));
		    			promisemctoagrate="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_MCTOAGRATE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG")!=null){
		    			double agsum=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AG").toString());
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE")!=null){
		    				agsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_AGRATE").toString());
		    			}
		    			applypromisesum=nfFSNum.format(agsum);
		    			applypromisesum="￥"+applypromisesum;
		    		}
		    		
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY")!=null){
		    			promsemoneytype=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_WAY").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			//shuijin=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString()));
		    			//shuijin="￥"+nfFSNum.format(DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE"))+DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE_TAX")));
		    			shuijin="￥"+nfFSNum.format(DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null){
		    			//mycompanypromse=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString()));
		    			mycompanypromse="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_CMPRICE", nfFSNum);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			//mycompanypromse=nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString()));
		    			double promiseshuifake=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString());
		    			
		    			promiseshui=nfFSNum.format(promiseshuifake);
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE")!=null){
		    			pingjundichong="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_AVE_PRICE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE")!=null){
		    			qimofanhuan="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_BACK_PRICE").toString()));
		    		}
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE")!=null&&((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE")!=null){
		    			//Double mycomsum=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMRATE").toString())+Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_CMPRICE").toString());
		    			Double mycomsum=0.0;
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG")!=null){
		    				mycomsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAG").toString());
		    			}
		    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE")!=null){
		    				mycomsum+=Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_ENTER_MCTOAGRATE").toString());
		    			}
		    			mycompanypromsesum=nfFSNum.format(mycomsum);
		    			//mycompanypromsesum="￥"+this.updateMoney((HashMap)outputMap.get("creditshemadetail"),"PLEDGE_ENTER_CMPRICE", nfFSNum);
		    			mycompanypromsesum="￥"+mycompanypromsesum;
		    		}
	        	}	
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(baozhengjins, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("保证金成数", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(baozhengjinchengshu, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		String tr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE")!=null){
		    			tr=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("TR_IRR_RATE").toString())/100);
		    		}
	    		}
	    		String hetongtr="";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
		    		if(((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST")!=null){
		    			hetongtr=nfFSNums.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("YEAR_INTEREST").toString())/100);
		    		}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("合同TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan2NoTopAndRight(hetongtr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("税后TR", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				tT.addCell(makeCellSetColspan3NoTop(tr, PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金入账金流", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("(1)保证金："+applypromisemoney+"元  稅金：0元 ", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		*/
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("保证金入账金流", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
    			tT.addCell(makeCellSetColspan3NoTop("入我司  保证金："+mycompanypromse+"元;", PdfPCell.ALIGN_LEFT, FontDefault222,9));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
	    		tT.addCell(makeCellSetColspan3NoTop("我司入供应商："+promisemctoag+"元;", PdfPCell.ALIGN_LEFT, FontDefault222,9));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		/*
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("合计："+applypromisemoney+"元入供应商;", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if(i%55==0){
	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    			i++;
	    		}
	    		*/
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,3));		
				tT.addCell(makeCellSetColspan3NoTop("入供应商   保证金："+applypromisemoney+"元", PdfPCell.ALIGN_LEFT, FontDefault222,9));		

				tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		String avdichong="";
	    		String avdichongqishu="0";
	    		if((HashMap)outputMap.get("creditshemadetail")!=null){
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE")!=null){
	    				avdichong="￥"+nfFSNum.format(Double.parseDouble(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE").toString())+DataUtil.doubleUtil(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PRICE_TAX")));
	    			}
	    			if(((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PERIOD")!=null){
	    				avdichongqishu=((HashMap)outputMap.get("creditshemadetail")).get("PLEDGE_LAST_PERIOD").toString();
	    			}
	    		}
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("平均抵冲："+pingjundichong+"元; 用于最后抵冲含税金额/期数："+avdichong+"元/"+avdichongqishu+"期;  期末返还："+qimofanhuan+"元", PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		if ("8".equals(((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString())) {
	    			Map<String, Object> income_pay = new HashMap<String, Object>();
	    			income_pay.put("FEE_NAME", "手续费");
	    			income_pay.put("FEE", new BigDecimal(((HashMap)outputMap.get("schemeMap")).get("INCOME_PAY").toString()));
	    			feeList.add(income_pay);
				} 
	    		
	    		if (feeList != null) {
	    			for (int j = 1; j <= feeList.size(); j++) {
		    			if (j % 2 != 0) {
		    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight((String) feeList.get(j - 1).get("FEE_NAME"), PdfPCell.ALIGN_LEFT, FontDefault22,3));		
							tT.addCell(makeCellSetColspan2NoTopAndRight("￥" + nfFSNum.format((BigDecimal) feeList.get(j - 1).get("FEE")), PdfPCell.ALIGN_LEFT, FontDefault22,3));
						}
		    			if (j % 2 == 0) {	
							tT.addCell(makeCellSetColspan2NoTopAndRight((String) feeList.get(j - 1).get("FEE_NAME"), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
							tT.addCell(makeCellSetColspan3NoTop("￥" + nfFSNum.format((BigDecimal) feeList.get(j - 1).get("FEE")), PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    			}
		    			if (j == feeList.size() && j % 2 != 0) {
		    				tT.addCell(makeCellSetColspan2NoTopAndRight(" ", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
							tT.addCell(makeCellSetColspan3NoTop(" ", PdfPCell.ALIGN_LEFT, FontDefault22,3));	
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
						}
					}
				}
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		String zujinshouqufangshi="";
	    		if(outputMap.get("schemeMap")!=null){
	    			for(int zujins=0;zujins<((List)outputMap.get("payWayList")).size();zujins++){
		    			if(((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE")!=null&&((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("CODE").equals(((HashMap)outputMap.get("schemeMap")).get("PAY_WAY").toString())){
		    				zujinshouqufangshi=((HashMap)((List)outputMap.get("payWayList")).get(zujins)).get("FLAG").toString();
		    			}
		    		}
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if ("14".equals(((HashMap)outputMap.get("creditMap")).get("CONTRACT_TYPE").toString())) {
	    			BigDecimal bd  =  new BigDecimal(((HashMap)outputMap.get("schemeMap")).get("INCOME_PAY").toString());
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("租金收取方式      手续费："+"￥" + nfFSNum.format(bd), PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		}else{
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("租金收取方式", PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		}
    			
    			
				tT.addCell(makeCellSetColspan3NoTop(zujinshouqufangshi, PdfPCell.ALIGN_LEFT, FontDefault22,8));	
				tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				i++;
				/*
				tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式", PdfPCell.ALIGN_LEFT, FontDefault22,2));	
				tT.addCell(makeCellSetColspan2NoTopAndRight(bokuanType, PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan2NoTopAndRight("拨款金额", PdfPCell.ALIGN_LEFT, FontDefault22,1));	
				tT.addCell(makeCellSetColspan3NoTop(bokuanjine, PdfPCell.ALIGN_LEFT, FontDefault22,2));
				*/
				
				
	    		
	    		String paypercentbefore="无";
	    		String paypercentafter="无";
	    		String paymoneybefore="无";
	    		String paymoneyafter="无";
	    		String payapplynamebefore="无";
	    		String payapplynameafter="无";
	    		if(outputMap.get("appropiateList")!=null){
	    			List appropiateList=(List)outputMap.get("appropiateList");
	    			for(int h=0;h<appropiateList.size();h++){
	    				HashMap appropiateMap=(HashMap)appropiateList.get(h);
	    				if(appropiateMap.get("TYPE")!=null){
	    					if(appropiateMap.get("TYPE").toString().equals("0")){
	    						if(appropiateMap.get("PAYPERCENT")!=null){
	    							if(!"".equals(appropiateMap.get("PAYPERCENT"))){
	    								paypercentbefore=appropiateMap.get("PAYPERCENT").toString()+"%";
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATEMON")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATEMON"))){
	    								paymoneybefore=nfFSNum.format(Double.parseDouble(appropiateMap.get("APPRORIATEMON").toString()));	    							
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATENAME")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATENAME"))){
	    								payapplynamebefore=appropiateMap.get("APPRORIATENAME").toString();	
	    							}
	    						}
	    					}else if(appropiateMap.get("TYPE").toString().equals("1")){
	    						if(appropiateMap.get("PAYPERCENT")!=null){
	    							if(!"".equals(appropiateMap.get("PAYPERCENT"))){
	    								paypercentafter=appropiateMap.get("PAYPERCENT").toString()+"%";
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATEMON")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATEMON"))){
	    								paymoneyafter=nfFSNum.format(Double.parseDouble(appropiateMap.get("APPRORIATEMON").toString()));
	    							}
	    						}
	    						if(appropiateMap.get("APPRORIATENAME")!=null){
	    							if(!"".equals(appropiateMap.get("APPRORIATENAME"))){
	    								payapplynameafter=appropiateMap.get("APPRORIATENAME").toString();	
	    							}
	    						}
	    					}
	    				}
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（设定情形）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("设定前  设定前比例："+paypercentbefore+" 设定前金额："+paymoneybefore+" 设定前拨款给："+payapplynamebefore, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop("设定后  设定后比例："+paypercentafter+" 设定后金额："+paymoneyafter+" 设定后拨款给："+payapplynameafter, PdfPCell.ALIGN_LEFT, FontDefault22,8));	
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		String applypromisemore="";
	    		if(outputMap.get("suplList")!=null&&outputMap.get("creditshemadetail")!=null){
	    			List suplList=(List)outputMap.get("suplList");
	    			for(int h=0;h<suplList.size();h++){
	    				HashMap suplMap=(HashMap)suplList.get(h);
	    				HashMap schemeMap=(HashMap)outputMap.get("creditshemadetail");
	    				if(suplMap.get("CODE")!=null&&schemeMap.get("SUPL_TRUE")!=null){
	    					if(suplMap.get("CODE").toString().equals(schemeMap.get("SUPL_TRUE").toString())){
	    						applypromisemore=suplMap.get("FLAG").toString();
	    						break;
	    					}
	    				}
	    			}
	    		}
	    		String appropriationWay = String.valueOf(((Map)outputMap.get("schemeMap")).get("APPROPRIATION_WAY")!=null?((Map)outputMap.get("schemeMap")).get("APPROPRIATION_WAY"):2);
	    		if("2".equals(appropriationWay)){
	    			
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（勾选）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop("网银[ √ ]  支票[   ]", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
	    		}else if("1".equals(appropriationWay)){
	    			String  deferPeriod= String.valueOf(((Map)outputMap.get("schemeMap")).get("DEFER_PERIOD")!=null?((Map)outputMap.get("schemeMap")).get("DEFER_PERIOD"):0);
		    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("拨款方式（勾选）", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop("网银[   ]  支票[ √ ](延迟拨款期数："+deferPeriod+")", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
		    		
	    			String endorsers ="";
	    			String endorser1 = (String) (((Map)outputMap.get("schemeMap")).get("ENDORSER_1")!=null?((Map)outputMap.get("schemeMap")).get("ENDORSER_1"):"           ");
	    			String endorser2 = (String) (((Map)outputMap.get("schemeMap")).get("ENDORSER_2")!=null?((Map)outputMap.get("schemeMap")).get("ENDORSER_2"):"           ");
	    			endorsers = "第一背书人：" + endorser1 +" 第二背书人：" + endorser2;
	    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			tT.addCell(makeCellSetColspan2NoTopAndRight("背书人", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
					tT.addCell(makeCellSetColspan3NoTop(endorsers, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
		    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++; 
	    		}
	    		
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
    			tT.addCell(makeCellSetColspan2NoTopAndRight("供应商保证", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
				tT.addCell(makeCellSetColspan3NoTop(applypromisemore, PdfPCell.ALIGN_LEFT, FontDefault22,8));		
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++; 
	    		
//	    		if(i%55==0){
//	    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		double yingfuzujin=0.0;
	    		int monthstart=0;
	    		int monthend=0;
	    		if(outputMap.get("irrMonthPaylines")!=null){
	    			
	    			List irrMonthPaylines = (List)outputMap.get("irrMonthPaylines");
	    			if(irrMonthPaylines.size()>0){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tT.addCell(makeCellSetColspan3NoTop("融资租赁方案测算方式一",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;		    		
//			    		if(i%55==0){
//			    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//			    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//			    			i++;
//			    		}
			    		for(int l=0;l<irrMonthPaylines.size();l++){
			    			HashMap irrMonthPayMap = (HashMap)irrMonthPaylines.get(l);
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE")!=null){
			    				yingfuzujin=Double.parseDouble(irrMonthPayMap.get("IRR_MONTH_PRICE").toString());
			    			}
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE_START")!=null){
			    				monthstart=Integer.parseInt(irrMonthPayMap.get("IRR_MONTH_PRICE_START").toString());
			    			}
			    			if(irrMonthPayMap.get("IRR_MONTH_PRICE_END")!=null){
			    				monthend=Integer.parseInt(irrMonthPayMap.get("IRR_MONTH_PRICE_END").toString());
			    			}
			    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight("应付租金："+"￥"+nfFSNum.format(yingfuzujin), PdfPCell.ALIGN_LEFT, FontDefault22,4));		
							tT.addCell(makeCellSetColspan3NoTop("从"+monthstart+"到"+monthend+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 
				    		
//				    		if(i%55==0){
//				    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//				    			tT.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//				    			i++;
//				    		}
			    		}
			    		//增加增值税含税报价
			    		Map<String, Object> schemeMap = (Map)outputMap.get("schemeMap");
			    		if ("2".equals(schemeMap.get("TAX_PLAN_CODE").toString())) {
			    			Map<String, Object> paylist = new HashMap<String, Object>();
			    			paylist.put("TOTAL_VALUEADDED_TAX", schemeMap.get("TOTAL_VALUEADDED_TAX"));
			    			paylist.put("LEASE_PERIOD", schemeMap.get("LEASE_TERM"));
			    			paylist.put("oldirrMonthPaylines", irrMonthPaylines);
			    			StartPayService.packagePaylinesForValueAdded(paylist);
			    			List newIrrMonthPaylines = (List) paylist.get("irrMonthPaylines");
			    			Map m = null;
			    			for (Object o : newIrrMonthPaylines) {
			    				m = (Map) o;
			    				monthstart = Integer.parseInt(m.get("MONTH_PRICE_START").toString());
			    				monthend = Integer.parseInt(m.get("MONTH_PRICE_END").toString());
			    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    			tT.addCell(makeCellSetColspan2NoTopAndRight("含税应付租金："+"￥"+nfFSNum.format(Double.parseDouble(m.get("MONTH_PRICE_TAX").toString())), PdfPCell.ALIGN_LEFT, FontDefault22,4));		
								tT.addCell(makeCellSetColspan3NoTop("从"+monthstart+"到"+monthend+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
					    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
					    		i++; 
							}
						}
	    			}
	    		}
	    		
	    		//Add by Michael 2012 12-13 增加支票还款明细
	    		if(outputMap.get("checkPayList")!=null){
	    			
	    			List checkPayList = (List)outputMap.get("checkPayList");
	    			if(checkPayList.size()>0){
	    				tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tT.addCell(makeCellSetColspan3NoTop("支票还款明细",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		for(int l=0;l<checkPayList.size();l++){
			    			HashMap checkPayListMap = (HashMap)checkPayList.get(l);
			    			
			    			tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tT.addCell(makeCellSetColspan2NoTopAndRight("支票还款", PdfPCell.ALIGN_LEFT, FontDefault22,4));		
							tT.addCell(makeCellSetColspan3NoTop("从"+checkPayListMap.get("CHECK_START")+"期到"+checkPayListMap.get("CHECK_END")+"期", PdfPCell.ALIGN_LEFT, FontDefault22,8));		
				    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 

			    		}
	    			}
	    		}
	    		
//		    		for(;i<54;i++){
//		    			tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=55){
//		    			tT.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
		    	document.add(tT);
		    	document.resetHeader();
				document.add(Chunk.NEXTPAGE);	
		    	PdfPTable tTs = new PdfPTable(14);
				tTs.setWidthPercentage(100f);	
		    	
	    		tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
    			i++;
    			if(outputMap.get("otherPriceList")!=null){
	    			
	    			List otherPriceList = (List)outputMap.get("otherPriceList");
	    			if(otherPriceList.size()>0){
	    				tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
			    		tTs.addCell(makeCellSetColspan3("其它费用",PdfPCell.ALIGN_LEFT, FontDefaultTitle,12));	    			
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
			    		
			    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    			tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用名称", PdfPCell.ALIGN_CENTER, FontDefault22,2));		
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用金额", PdfPCell.ALIGN_CENTER, FontDefault22,2));		
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用产生时间", PdfPCell.ALIGN_CENTER, FontDefault22,4));		
						tTs.addCell(makeCellSetColspan3("备注", PdfPCell.ALIGN_CENTER, FontDefault22,4));		
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++; 
			    		double feesum=0.0;
			    		for(int f=0;f<otherPriceList.size();f++){
			    			HashMap otherFeeMap = (HashMap)otherPriceList.get(f);
			    			String feename="";
			    			String feecost="0.0";
			    			String makefeedate="";
			    			String feememo="";
			    			
			    			if(otherFeeMap.get("OTHER_NAME")!=null){
			    				feename=otherFeeMap.get("OTHER_NAME").toString();
			    			}
			    			if(otherFeeMap.get("OTHER_PRICE")!=null){
			    				feecost=Double.parseDouble(otherFeeMap.get("OTHER_PRICE").toString())+"";
			    				feesum+=Double.parseDouble(otherFeeMap.get("OTHER_PRICE").toString());
			    			}
			    			if(otherFeeMap.get("OTHER_DATE")!=null){
			    				makefeedate=otherFeeMap.get("OTHER_DATE").toString().substring(0,10);
			    			}
			    			if(otherFeeMap.get("MEMO")!=null){
			    				feememo=otherFeeMap.get("MEMO").toString();
			    			}
			    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    			tTs.addCell(makeCellSetColspan2NoBottomAndRight(feename, PdfPCell.ALIGN_CENTER, FontDefault22,2));		
							tTs.addCell(makeCellSetColspan2NoBottomAndRight(nfFSNum.format(Double.parseDouble(feecost)), PdfPCell.ALIGN_CENTER, FontDefault22,2));		
							tTs.addCell(makeCellSetColspan2NoBottomAndRight(makefeedate, PdfPCell.ALIGN_CENTER, FontDefault22,4));		
							tTs.addCell(makeCellSetColspan3(feememo, PdfPCell.ALIGN_CENTER, FontDefault22,4));		
				    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
				    		i++; 
			    		}
			    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
						tTs.addCell(makeCellSetColspan2NoBottomAndRight("费用总额(大写)："+TfAmt.num2cn(nfFSNum.format(feesum).replaceAll(",", "")), PdfPCell.ALIGN_RIGHT, FontDefault22,6));		
						tTs.addCell(makeCellSetColspan3("费用总额："+nfFSNum.format(feesum), PdfPCell.ALIGN_RIGHT, FontDefault22,6));		
			    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
			    		i++;
	    			}
    			}
    			
    			String PROJECT_CONTENT="";
	    		String pSIdeaMapName="";
	    		String pSIdeaMapTime=""; 
	    		if(outputMap.get("pSIdeaMap")!=null){
		    		Map pSIdeaMap= (Map)outputMap.get("pSIdeaMap");
					if(pSIdeaMap.get("PROJECT_CONTENT")!=null){
						PROJECT_CONTENT= pSIdeaMap.get("PROJECT_CONTENT").toString();
					}	
					if(pSIdeaMap.get("NAME")!=null){
						pSIdeaMapName= pSIdeaMap.get("NAME").toString();
					}
					if(pSIdeaMap.get("CRATE_DATE")!=null){
						pSIdeaMapTime= pSIdeaMap.get("CRATE_DATE").toString().substring(0,10);
					}
		    	}
	    		//String[] workercontext = PROJECT_CONTENT.split("\n");
	    		int workcontextlength= Math.round(PROJECT_CONTENT.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    			tTs.addCell(makeCellSetColspanNull("建议承做理由:"+PROJECT_CONTENT,PdfPCell.ALIGN_LEFT, FontDefault2,12,0.5f,0,0.5f,0.5f));	    			
	    			tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if(workcontextlength>=2){
	    			i+=workcontextlength;
	    		}else{
	    			i++;
	    		}
	    		
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
//	    		
	    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+pSIdeaMapName+"时间:"+pSIdeaMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
	    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
	    		
	    		//pSIdeaMapOther
	    		PROJECT_CONTENT="无";
	    		pSIdeaMapName="";
	    		pSIdeaMapTime=""; 
	    		if(outputMap.get("pSIdeaMapOther")!=null){
		    		Map pSIdeaMap= (Map)outputMap.get("pSIdeaMapOther");
					if(pSIdeaMap.get("PROJECT_CONTENT")!=null){
						PROJECT_CONTENT= pSIdeaMap.get("PROJECT_CONTENT").toString();
					}	
					if(pSIdeaMap.get("NAME")!=null){
						pSIdeaMapName= pSIdeaMap.get("NAME").toString();
					}
					if(pSIdeaMap.get("CRATE_DATE")!=null){
						pSIdeaMapTime= pSIdeaMap.get("CRATE_DATE").toString().substring(0,10);
					}
		    	}
	    		workcontextlength= Math.round(PROJECT_CONTENT.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
	    			tTs.addCell(makeCellSetColspanNull("其他租赁条件:"+PROJECT_CONTENT,PdfPCell.ALIGN_LEFT, FontDefault2,12,0f,0,0.5f,0.5f));	    			
	    			tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		if(workcontextlength>=2){
	    			i+=workcontextlength;
	    		}else{
	    			i++;
	    		}
	    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		if(!"无".equals(PROJECT_CONTENT)){
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+pSIdeaMapName+"时间:"+pSIdeaMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
	    		} else {
	    			tTs.addCell(makeCellSetColspan3NoTop("   ",PdfPCell.ALIGN_RIGHT, FontDefault22,12));
	    		}
	    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		i++;
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		String manageMapMemo="";
	    		String manageMapName="";
	    		String manageMapTime="";
	    		String manageMapCropMemo="";
	    		String manageMapCropName="";
	    		String manageMapCropTime="";
	    		if(outputMap.get("manageMap")!=null){
		    		Map manageMap= (Map)outputMap.get("manageMap");
					
					if(manageMap.get("MEMO")!=null){
						manageMapMemo=manageMap.get("MEMO").toString();
					}
					
					if(manageMap.get("CREATE_TIME")!=null){
						manageMapTime=manageMap.get("CREATE_TIME").toString().substring(0,10);
					}
					if(manageMap.get("NAME")!=null){
						manageMapName=manageMap.get("NAME").toString();
					}
					
		    	}
	    		if(outputMap.get("manageMapCrop")!=null){
	    			Map manageMapCrop= (Map) outputMap.get("manageMapCrop");
	    			if(manageMapCrop.get("MEMO")!=null){
						manageMapCropMemo=manageMapCrop.get("MEMO").toString();
					}
	    			if(manageMapCrop.get("CREATE_TIME")!=null){
						manageMapCropTime=manageMapCrop.get("CREATE_TIME").toString();
					}
					if(manageMapCrop.get("CREATE_TIME")!=null){
						manageMapCropName=manageMapCrop.get("NAME").toString();
					}
	    		}
	    		if(outputMap.get("manageMapCrop")==null){
	    			int manageMapMemolength= Math.round(manageMapMemo.length()/60);
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("区域主管审核意见:"+manageMapMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}

		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapName+"   时间:"+manageMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;

	    		}else{
	    			int manageMapMemolength= Math.round(manageMapMemo.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("区域主管审核意见:"+manageMapCropMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}
		    		
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapCropName+"   时间:"+manageMapCropTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
	    		}
	    		
	    		//manageMapDGM
	    		manageMapMemo="无";
	    		manageMapName="";
	    		manageMapTime="";
	    		if(outputMap.get("manageMapDGM")!=null){
		    		Map manageMap= (Map)outputMap.get("manageMapDGM");
					if(manageMap.get("MEMO")!=null){
						manageMapMemo=manageMap.get("MEMO").toString();
					}
					
					if(manageMap.get("CREATE_TIME")!=null){
						manageMapTime=manageMap.get("CREATE_TIME").toString().substring(0,10);
					}
					if(manageMap.get("NAME")!=null){
						manageMapName=manageMap.get("NAME").toString();
					}
					int manageMapMemolength= Math.round(manageMapMemo.length()/60);
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("业务副总审核意见:"+manageMapMemo,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(manageMapMemolength>=1){
		    			i+=manageMapMemolength;
		    		}else{
		    			i++;
		    		}

		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    		if ("无".equals(manageMapMemo)) {
		    			tTs.addCell(makeCellSetColspan3NoTop("     ",PdfPCell.ALIGN_RIGHT, FontDefault2,12));
					} else {
						tTs.addCell(makeCellSetColspan3NoTop("签字:"+manageMapName+"   时间:"+manageMapTime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
					}
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
		    	}
	    		
	    		for(int riskMome=0;riskMome<((List)outputMap.get("riskMemoList")).size();riskMome++){
	    			
	    			HashMap riskMomeMap = (HashMap)((List)outputMap.get("riskMemoList")).get(riskMome);
	    			String levelStr="";
	    			String levelcontext="";
	    			String levelname="";
	    			String leveltime="";
	    			if(riskMomeMap!=null){
	    				if(riskMomeMap.get("PRCM_USER_LEVEL")!=null){
	    					levelStr=riskMomeMap.get("PRCM_USER_LEVEL").toString();  					
	    				}
	    				if(riskMomeMap.get("PRCM_CONTEXT")!=null){
	    					levelcontext=riskMomeMap.get("PRCM_CONTEXT").toString();  					
	    				}
	    				if(riskMomeMap.get("NAME")!=null){
	    					levelname=riskMomeMap.get("NAME").toString();  					
	    				}
	    				if(riskMomeMap.get("CREATE_TIME")!=null){
	    					leveltime=riskMomeMap.get("CREATE_TIME").toString().substring(0,10);  					
	    				}	    				
	    			}
	    			int levellength=Math.round(levelcontext.length()/60);
	    			tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop((levelStr.equals("0") ? "初" : levelStr) +"级审批意见:"+levelcontext,PdfPCell.ALIGN_LEFT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		if(levellength>=1){
		    			i+=levellength;
		    		}else{
		    			i++;
		    		}
//		    		if(i%55==0){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
		    		
		    		tTs.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));			   		    		
		    		tTs.addCell(makeCellSetColspan3NoTop("签字:"+levelname+"   时间:"+leveltime,PdfPCell.ALIGN_RIGHT, FontDefault2,12));	    			
		    		tTs.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		i++;
		    		
//
//		    		if(i%55==0){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    			i++;
//		    		}
	    			
	    		}
//
//	    		if(i%55==0){
//	    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			i++;
//	    		}
	    		
	    		
	    		int pagenum=Math.round(i/55);
	    		//if(i<=pagenum*55+65){
	    		//	tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
	    		//}
	    		
//	    		if(i<pagenum*55+62){
//		    		for(;i<pagenum*55+61;i++){
//		    			tTs.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=pagenum*55+61){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//	    		}else{
//	    			tTs.addCell(makeCellSetColspan3(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//	    			for(;i<pagenum*55+61;i++){
//		    			tTs.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//		    		if(i<=pagenum*55+61){
//		    			tTs.addCell(makeCellSetColspan3NoTop(" ",PdfPCell.ALIGN_CENTER, FontDefault22,14));
//		    		}
//	    			
//	    		}
			document.add(tTs);
			document.resetHeader();
			document.add(Chunk.NEXTPAGE);
			document.close();
	    // 支付表PDF名字的定义
	    String strFileName =  "Approvalletters.pdf";
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
    
    



    /** 创建 只有左边框 单元格 */
	private PdfPCell makeCellSetColspanNull(String content, int align,Font FontDefault, int colspan,float T,float B,float L,float R) {
				Phrase objPhase = new Phrase(content, FontDefault);
				PdfPCell objCell = new PdfPCell(objPhase);
				objCell.setHorizontalAlignment(align);
				objCell.setVerticalAlignment(align);
				objCell.setColspan(colspan);
				objCell.setBorderWidthBottom(B);
				objCell.setBorderWidthLeft(L);
				objCell.setBorderWidthRight(R);
				objCell.setBorderWidthTop(T);
				 
				return objCell;
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
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthRight(0);
	objCell.setBorderWidthTop(0);
	 
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
	objCell.setBorderWidthLeft(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }


    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoTopAndRight(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格|-
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoBottomAndRight(String content, int align,
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
    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspan2NoTopAndLeft(String content, int align,
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

    /** 创建 有上下边框 合并 单元格|_
     *  无上下边
     *  
     *  */
    private PdfPCell makeCellSetColspanOnlyLeft(String content, int align,
	    Font FontDefault, int colspan) {
	Phrase objPhase = new Phrase(content, FontDefault);
	PdfPCell objCell = new PdfPCell(objPhase);
	objCell.setHorizontalAlignment(align);
	objCell.setVerticalAlignment(align);
	objCell.setColspan(colspan);
	objCell.setBorderWidthTop(0);
	objCell.setBorderWidthRight(0);
	objCell.setBorderWidthBottom(0);
	return objCell;
    }
    /** 创建 有上下边框 合并 单元格=
     *  无上下边
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



   
	/** 创建 只有左边框 单元格 */
	private PdfPCell makeCellWithBorderLeft(String content, int align, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	   
	    objCell.setHorizontalAlignment(align);
	    objCell.setVerticalAlignment(align);
	    objCell.setBorderWidthBottom(0);
	    objCell.setBorderWidthTop(0);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthLeft(0);
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
	    objCell.setBorderWidthRight(0);
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
    private String updateMoneyStr(String content, NumberFormat nfFSNum) {
    	String str = "";
    	if (content == null) {
    	    str +=  "0.00";
    	    return str;
    	}
    	if (content.equals("0")) {
    	    str += "0.00";
    	    return str;
    	} else {
    	    str +=  nfFSNum.format(Double.parseDouble(content
    			    .toString()));
    	    return str;
    	}

        }
}