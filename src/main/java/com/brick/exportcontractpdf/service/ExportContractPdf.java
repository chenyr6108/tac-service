package com.brick.exportcontractpdf.service;

import com.brick.service.core.AService;
import com.brick.service.core.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;

import jxl.write.Label;

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

import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;

public class ExportContractPdf extends AService {
	Log logger = LogFactory.getLog(ExportContractPdf.class);
   
	public void expContract(Context context){
		try{
			Map contract =new HashMap();
			Map contracttype =new HashMap();
			List<Map> pucsContractDetail = null;
	    	String cons= (String) context.contextMap.get("rect_id");
	    	
	    	context.contextMap.put("PUCT_ID",  cons);	    	
	    	//查找合同的相关信息
	    	contract = (Map) DataAccessor.query("exportContractPdf.queryContractByPrcdIds", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	//查找合同的类型
	    	contracttype = (Map) DataAccessor.query("exportContractPdf.queryContractTypeByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    	
	    	//pucsContractPlan = (Map) DataAccessor.query("exportContractPdf.readPucsContractPlan", context.contextMap, DataAccessor.RS_TYPE.MAP);
	    	//查找合同所租的机械
	    	pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.readPucsContractDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
        
		    this.expContractPdf(context,contract,contracttype,pucsContractDetail);
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	public void expContractCredit(Context context){
		try{
			Map contract =new HashMap();
			Map contractaplyname =new HashMap();
			Map contracttype =new HashMap();
			List<Map> pucsContractDetail = null;
	    	String cons= (String) context.contextMap.get("rect_id");
	    	
	    	context.contextMap.put("PRCD_ID",  cons);
	    	List contractinfo=(List) DataAccessor.query("exportContractPdf.judgeExitContract", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    	//查找合同的相关信息
	    	
	    	if(contractinfo.size()==0){
	    		
		    	contract = (Map) DataAccessor.query("exportContractPdf.queryCreditInfoByCreditIds", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	contractaplyname = (Map) DataAccessor.query("exportContractPdf.selectCreditInfoByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	//查找合同的类型
		    	if(contractaplyname!=null){
		    		if(contractaplyname.get("APPLYNAME")!=null){
		    			contract.put("SELLER_UNIT_NAME", contractaplyname.get("APPLYNAME"));
		    		}
		    	}
		    	contracttype = (Map) DataAccessor.query("exportContractPdf.queryCreditTypeByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	if(contracttype!=null){
		    		if(contracttype.get("CONTRACT_TYPE")!=null){
		    			if(contracttype.get("CONTRACT_TYPE").toString().equals("1")||"5".equals(contracttype.get("CONTRACT_TYPE").toString())){
		    				contract.put("BUYER_UNIT_NAME", contract.get("CUST_UNIT_NAME"));
		    				//contract.put("LEASE_CODE", contracttype.get("LEASE_CODE"));
		    			}
		    		}
		    	}
		    	pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.readCreditInfoDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
//		    	if(pucsContractDetail!=null){
//		    		int total = 0;
//		    		for(int i=0;i<pucsContractDetail.size();i++){
//		    			HashMap moneymap=(HashMap)pucsContractDetail.get(i);
//		    			if(moneymap.get("UNIT_PRICE")!=null&&moneymap.get("AMOUNT")!=null){
//		    				total +=Double.parseDouble(moneymap.get("UNIT_PRICE").toString())*Integer.parseInt(moneymap.get("AMOUNT").toString());
//		    			}
//		    		}
//		    		contract.put("TOTAL", total);
//	    		}
		    	contract.put("TOTAL", contracttype.get("LEASE_TOPRIC"));
	    	}else{
	    		
	    		contract = (Map) DataAccessor.query("exportContractPdf.findContractInfoByPrcdidnew", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		contracttype = (Map) DataAccessor.query("exportContractPdf.findContractInfoByPrcdidnew", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.findContractEquipmentByPrcdidnew", context.contextMap, DataAccessor.RS_TYPE.LIST);
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
	        Font FontDefault23 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        //Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
	        //Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
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
			String code = "";
			String totalmoney = "";
			String qiandingdate = "";
			String deleverydate = "";
			if(contracttype!=null){
				if(contracttype.get("CONTRACT_TYPE")!=null){
					code=contracttype.get("CONTRACT_TYPE").toString();
				}
				if(contracttype.get("LESSOR_TIME")!=null){
					qiandingdate = contracttype.get("LESSOR_TIME").toString().substring(0, 10);
				}
	    	}
			if(contract!=null){
				if(contract.get("TOTAL")!=null){
					totalmoney = updateMoney(contract,"TOTAL",NumberFormat.getInstance());
				}
				if(contract.get("DELIVERY_DATE")!=null){
					deleverydate=contract.get("DELIVERY_DATE").toString().substring(0,10);
				}
			}
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);
			int i=0;
			String Lcode ="";
			if(code.equals("0")){
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
  
				tT.addCell(makeCellSetColspan2("买卖合同",PdfPCell.ALIGN_CENTER, fa,8));
	  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		i+=6;
	    		
	    		if(contract.get("PUCT_CODE")!=null){
	    			//Lcode = contract.get("PUCT_CODE") +"";
	    		}
	    		if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    				Lcode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
	    		Lcode=	Lcode.trim();
	    		if(Lcode.equals("")){
	    			Lcode = "           ";
	    		}
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_RIGHT, FontDefault,8));
	    
	    		tT.addCell(makeCellSetColspanOnlyLeft("     " ,PdfPCell.ALIGN_LEFT,  FontDefault,5));	   
	    		tT.addCell(makeCellWithNoBorder("合同编号: "  ,PdfPCell.ALIGN_LEFT,  FontDefault));
	    		tT.addCell(makeCellSetColspanOnlyRight(""+Lcode,PdfPCell.ALIGN_LEFT, FontDefault,2));
	    		String contractdate="";
	    		if(contract.get("DELIVERY_DATE")!=null){
	    			 contractdate = contract.get("DELIVERY_DATE").toString().substring(0,10);
	    		}
	    		tT.addCell(makeCellSetColspanOnlyLeft("     " ,PdfPCell.ALIGN_LEFT,  FontDefault,5));	   
	    		tT.addCell(makeCellWithNoBorder("日期: "  ,PdfPCell.ALIGN_LEFT,  FontDefault));	
	    		tT.addCell(makeCellSetColspanOnlyRight(""+qiandingdate ,PdfPCell.ALIGN_LEFT, FontDefault,2));   
	   
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));	   
	    		tT.addCell(makeCellSetColspan2("    " ,PdfPCell.ALIGN_LEFT, FontDefault,8));
	    
	    
	    		i+=6;
	    		//表头结束
	    
	    
	    		//第一个子表开始
	    
	    
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspan3("卖方:   ",PdfPCell.ALIGN_LEFT, FontDefault,3));	    
	    		tT.addCell(makeCellSetColspan2RightAndTop("购买方：  ",PdfPCell.ALIGN_LEFT, FontDefault,3));	    
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		String sellname="";
	    		if(contract.get("SELLER_UNIT_NAME")!=null){
	    			sellname=contract.get("SELLER_UNIT_NAME").toString();
	    		}
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3NoTop("      "+sellname,PdfPCell.ALIGN_LEFT, FontDefault22,3));	
	    		tT.addCell(makeCellSetColspan2RightAndBottom("       "+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));	

	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_LEFT, FontDefault,3));	
	    		tT.addCell(makeCellSetColspanOnlyRight("承租方:  ",PdfPCell.ALIGN_LEFT, FontDefault,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		tT.addCell(makeCellSetColspan3NoTop("       ",PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellSetColspan2RightAndBottom("      "+contract.get("CUST_UNIT_NAME")==null?"":contract.get("CUST_UNIT_NAME")+"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
	    		tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("购买方按照下列记载的条件订购下列设备作为租赁物租赁给承租方。",PdfPCell.ALIGN_CENTER, FontDefault,8));
	    		tT.addCell(makeCellSetColspan2("卖方按本合同的约定向购买方出售该设备。",PdfPCell.ALIGN_CENTER, FontDefault,8));
	    		i+=8;
	    
	    		//第一个子表结束，第二个子表开始
	    		tT.addCell(makeCellWithBorderLeft("     ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("租赁合同签订日",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		
	    		tT.addCell(makeCellSetColspan3(""+qiandingdate,PdfPCell.ALIGN_LEFT, FontDefault22,4));	    
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		String leasecode="";
	    		if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    			leasecode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("租赁合同编号",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		tT.addCell(makeCellSetColspan3(""+leasecode,PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));	
	    		String delivery_addr="";
	    		if(contracttype!=null){
	    			if(contracttype.get("DELIVERY_ADDRESS")!=null){
	    				delivery_addr=contracttype.get("DELIVERY_ADDRESS").toString();
	    			}
	    		}
	    		String cust_name="";
	    		if(contract!=null){
	    			if(contract.get("CUST_UNIT_NAME")!=null){
	    				cust_name=contract.get("CUST_UNIT_NAME").toString();
	    			}
	    		}
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop(" 承租方名称",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		tT.addCell(makeCellSetColspan3(""+cust_name,PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("交付场所",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		tT.addCell(makeCellSetColspan3(""+delivery_addr,PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	   
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("交付时间",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    	
	    		tT.addCell(makeCellSetColspan3(""+deleverydate,PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	   
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan2LeftAndTop("物品价款支付条件",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		tT.addCell(makeCellSetColspan3("买方在收到承租方出具的《租赁物验收证明书》并确定起租，以及收到卖方提供的发票及《承租方付款指示书》后7个银行工作日内付款",PdfPCell.ALIGN_LEFT, FontDefault22,4));
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));

	    
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3("买卖物品内容",PdfPCell.ALIGN_CENTER, FontDefault,6)); 
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    
	    		tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		tT.addCell(makeCellSetColspan3("品名、型号、规格、数量、质量、技术性能、服务内容、交货条件、制造厂商等    ",PdfPCell.ALIGN_LEFT, FontDefault22,4)); 
	    		tT.addCell(makeCellSetColspan2RightAndTop(" 金额 ",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    		
	    		i+=8;
	    		//int n = -1;
	    		for(int k=0;k<pucsContractDetail.size();k++){
	    			String thinginfo = "";
	    			String thinginfor = "";
	    			HashMap equipt = (HashMap)pucsContractDetail.get(k);
	    			thinginfo = "制造商:"+equipt.get("THING_KIND").toString();
	    			//Modify by Michael 将品牌改为厂牌
	    			thinginfo += " 名称:"+equipt.get("THING_NAME").toString()+" 厂牌:"+equipt.get("TYPE_NAME");
	    			thinginfor = " 型号:"+equipt.get("MODEL_SPEC").toString()+" 单价:"+(nfFSNum.format(equipt.get("UNIT_PRICE")))+" 数量:"+equipt.get("AMOUNT").toString();
		    			
	    			tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    		tT.addCell(makeCellSetColspan3(""+thinginfo,PdfPCell.ALIGN_LEFT, FontDefault23,4)); 
		    		tT.addCell(makeCellSetColspan2RightAndTop("  ",PdfPCell.ALIGN_LEFT, FontDefault22,2));	
		    		tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    				
	    			tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			if(k<pucsContractDetail.size()-1){
	    				tT.addCell(makeCellSetColspan2(""+thinginfor,PdfPCell.ALIGN_LEFT, FontDefault23,4)); 
	    			}else{
	    				tT.addCell(makeCellSetColspan3NoTop(""+thinginfor,PdfPCell.ALIGN_LEFT, FontDefault23,4)); 
	    			}
	    			if(k<pucsContractDetail.size()-1){
	    				
	    				tT.addCell(makeCellSetColspanOnlyRight(""+nfFSNum.format(((Double.parseDouble(equipt.get("UNIT_PRICE").toString())*Integer.parseInt(equipt.get("AMOUNT").toString())))),PdfPCell.ALIGN_LEFT, FontDefault22,2));	
	    			}else{
	    				tT.addCell(makeCellSetColspan2RightAndBottom(""+nfFSNum.format(((Double.parseDouble(equipt.get("UNIT_PRICE").toString())*Integer.parseInt(equipt.get("AMOUNT").toString())))),PdfPCell.ALIGN_LEFT, FontDefault22,2));	

	    			}
	    			tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
	    			
	    				
	    			
	    			//n++;
	    			i+=2;
	    		}
	    		if(pucsContractDetail.size()>3)
	    		{
	    			i++;
	    		}

	    		
	    		for(;i<=49;i++){
	    			tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
	    		}
	    		if(i<=50){
	    			tT.addCell(makeCellSetColspan2("  1  ",PdfPCell.ALIGN_CENTER, FontDefault,8)); 
	    		}
	    		//i++;
	    		if(i<=51){
	    			tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));
	    		}

	    		tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     第一条	    标的物",PdfPCell.ALIGN_LEFT, FontColumn2,8));
	    		tT.addCell(makeCellSetColspan2222("              本合同的买卖标的物系购买方(出租方)与承租方签订的融资租赁合同项下的融资租赁标的",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     物，此标的物系承租方根据自己的选择和判断要求出租方购买的，不依赖于出租方的技能，亦未",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     受到出租方的任何影响。承租方对该融资租赁标的物的品名、型号、规格、数量、质量、技术性",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     能、价格、技术标准、服务内容、技术保障、交货方式等选择承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     第二条 标的物的交付",PdfPCell.ALIGN_LEFT, FontColumn2,8));
	    		tT.addCell(makeCellSetColspan2222("               1、合同签订之日起5日内，卖方将标的物运至承租方指定的场所，运费由卖方与承租方自",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("     行协商。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("               2、标的物运至承租方指定的场所后，承租方应立即进行验货，查看标的物是否与合同约定",PdfPCell.ALIGN_LEFT, FontDefault2,8));
	    		tT.addCell(makeCellSetColspan2222("    的相符，包装、质量是否完好，如果查验无问题，承租方应立即将《租赁物验收证明书》交付于",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    出租方，出租方收到承租方提交的《租赁物验收证明书》内记载的租赁物起租日即为交付日。收",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    到《租赁物验收证明书》之日起，交付完毕，租赁物的所有权自卖方转移至购买方（出租方）。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    如在10天内，承租方未按前项规定向出租方交付《租赁物验收证明书》的，视为标的物已在完",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    整良好状态下由承租方验收完毕，并视同承租方已经将标的物的验收证明书交付给出租方。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			
			  	tT.addCell(makeCellSetColspan2222("    	        3、出租方在收到承租方出具的《租赁物验收证明书》及卖方提供的发票后7个工作日内付",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    款于卖方指定的银行账户中。如果因承租方未积极履行验收标的物，导致事后得知标的物与买卖",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    合同或租赁合同约定不符等情况，此与出租方无关，由承租方与卖方自行协商解决。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("               4、承租方在验收标的物时如发现标的物的品质、规格、数量等有不符，不良或瑕疵等情况",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    属于卖方的责任时，承租方应在接收到标的物3天内立即将上述情况书面通知出租方，出租方将",PdfPCell.ALIGN_LEFT, FontDefault2,8));			   
			  	tT.addCell(makeCellSetColspan2222("    根据与卖方签订的买卖合同规定的有关条款协助承租方对外进行交涉，办理索赔等事宜。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    第三条 延迟交付或交付不能",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("               1、卖方因自身原因导致标的物延迟交付或交付不能的，由卖方直接与承租方协商解决，由",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    此给承租方造成的损害由卖方直接向承租方负责。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("               2、卖方无法交付或未能如期交付的，承租方与卖方交涉后，卖方确无能力在承租方和出租",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    方均能接受的延期日期前交付时，承租方和出租方可以通过发送双方共同署名的解约通知书的方",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    式解除本合同。如购买方（出租方）已经支付部分或全部货款，或已经支付定金或预付款的，卖",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    方应将该款项连同利息一起在通知送达后3天内返还给出租方。利息应从出租方支付款项之日起",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    到实际返还日按照以每万元每日6元计算。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    第四条 标的物的瑕疵担保责任",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("                1、对于出租方与卖方之间签订的买卖合同中规定的对于标的物瑕疵保证、卖方的义务及提",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    供的其他服务，卖方应当直接对承租方负有履约义务。购买方（出租方）认为有必要时，可向承",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    租方转让租赁物的所有权或其在买卖合同中享有的要求赔偿的权利，以便于承租方向卖方直接交",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    涉或请求。但是，对于卖方的责任履行，以及出租方转让所有权后承租方与卖方之间的各种直接",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    交涉，出租方不作任何保证。要求赔偿所需的费用（包括仲裁费、诉讼费和律师费等）和法律后",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    果均由承租方承担并享受其利益。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			 			    			    
			  	tT.addCell(makeCellSetColspan2222("                2、卖方对于标的物与第三者的专利权、商标权、著作权存在抵触而致的纠纷，应由卖方负",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    责解决并承担解决纠纷的费用及法律后果，因前述情形给出租方及承租方造成损失的，由卖方赔",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("    偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			   			    
			  	tT.addCell(makeCellSetColspan2222("     第五条 标的物的维护",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("               卖方应根据承租方的要求，负责租赁物的保养、维修（包括修补瑕疵）。当承租方提出申请",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     时，卖方应向承租方有偿提供标的物维修时所需的零部件正品。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     由购买合同的卖方负责，甲方不承担赔偿责任，乙方不得向甲方追索。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     第六条 合同的解除或无法履行",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("                1、因不可归责于出租方的事由所导致的租赁合同未能签署（包括无效、撤销等）或者在租",PdfPCell.ALIGN_LEFT, FontDefault2,8));	    
			  	tT.addCell(makeCellSetColspan2222("     赁物交接完毕前租赁合同被解除时，出租方可以无条件解除本合同（包括撤回要约）。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("                2、本条第1款的情形下，承租方应及时返还出租方已支付的全部款项，并承担相应的违约",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     金（出租方实际支付日起至实际收到承租方返还全部支付款项日，以每万元每日6元计算）。同时",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     承租方应立即代替出租方与卖方进行协商，处理此事件。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("                3、由于天灾、战争、暴动、罢工及其他不可抗力事由，或者非因购买方（出租方）或者承",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
			  	tT.addCell(makeCellSetColspan2222("     租方的责任导致本合同的全部或部分不能履行，延迟履行时，购买方不承担责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));   
			  	tT.addCell(makeCellSetColspan2222("     第七条 合同外部条件变化",PdfPCell.ALIGN_LEFT, FontColumn2,8));			    			
			  	tT.addCell(makeCellSetColspan2222("               本合同签订后，因发生法规变更引起的各项税费的增加、运费及其他各项开支的增加等均由",PdfPCell.ALIGN_LEFT, FontDefault2,8));			 
			  	tT.addCell(makeCellSetColspan2222("     卖方承担，不得因此变更销售价款及其他交易条件。",PdfPCell.ALIGN_LEFT, FontDefault2,8));	
			  	tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
			 //结束页
			  	tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan3NoTop("  2  ", PdfPCell.ALIGN_CENTER, FontDefault,8));  
			  	tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));		  
			 
			  	tT.addCell(makeCellSetColspan2222("     第八条 争议解决",PdfPCell.ALIGN_LEFT, FontColumn2,8));	
			  	tT.addCell(makeCellSetColspan2222("                1、卖方和出租方（购买方）之间发生争议时，承租方要求代替购买方与卖方就解决争议进",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     行交涉的，卖方应与承租方就争议解决进行协商，不得向购买方要求索赔或进行其他直接接触。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("                2、有关本合同发生的一切争议均由当事人友好协商解决。当事人难以协商解决的，通过出",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     租方（购买方）注册所在地的有管辖权的人民法院通过诉讼解决。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     第九条 其他约定",PdfPCell.ALIGN_LEFT, FontColumn2,8));
			  	tT.addCell(makeCellSetColspan2222("                1、卖方在未得到购买方（出租方）的书面承诺时，本合同的全部或部分权利不可转让给第",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("     三方或作担保。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	tT.addCell(makeCellSetColspan2222("                2、本合同自合同三方当事人签字或盖章后生效，本合同一式三份，具同等法律效力。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			    			    
			  	tT.addCell(makeCellSetColspan2222("                3、本合同条目括号中的小标题只作为醒目之用，不作为本合同解释内容之用。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			  	i+=71;
			  	for(;i<164;i++){
			  		
			  		tT.addCell(makeCellSetColspan2222("   ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
	   			 	  		  
			  	}
			  	if(i==164){
			  		
			  		tT.addCell(makeCellSetColspan2222("  3  ",PdfPCell.ALIGN_CENTER, FontDefault2,8)); 
	   			
			  	}
			  	i+=1;
			  	if(i==165){
			  		tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8)); 
			  	}
			}else if(code.equals("1")||"5".equals(code)){//加入新品回租合同
		  
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
	  
				tT.addCell(makeCellSetColspan2("委托购买合同",PdfPCell.ALIGN_CENTER, fa,8));
		  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				i+=6;
		    
				Lcode ="";
				if(contract.get("PUCT_CODE")!=null){
					//Lcode = contract.get("PUCT_CODE") +"";
				}
				if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    				Lcode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
		    	Lcode=	Lcode.trim();
		    	if(Lcode.equals("")){
		    		Lcode = "           ";
		    	}
		    	tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_RIGHT, FontDefault,8));
		   
		    	tT.addCell(makeCellSetColspanOnlyLeft("     " ,PdfPCell.ALIGN_LEFT,  FontDefault,5));	   
		    	tT.addCell(makeCellWithNoBorder("合同编号: "  ,PdfPCell.ALIGN_LEFT,  FontDefault));	
		    	tT.addCell(makeCellSetColspanOnlyRight(""+Lcode,PdfPCell.ALIGN_LEFT, FontDefault,2));  
		   
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));		   
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
   

		    	tT.addCell(makeCellWithBorderLeft("     ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspan2LeftAndTop("甲方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefault,3));	
		    	tT.addCell(makeCellSetColspan3(contract.get("BUYER_UNIT_NAME")==null?"乙方: ":"乙方: "+contract.get("BUYER_UNIT_NAME") +"",PdfPCell.ALIGN_LEFT, FontDefault,3));	    
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("法定代表人："+Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2(contract.get("BUYER_AGENT")==null?"法定代表人：":"法定代表人："+contract.get("BUYER_AGENT").toString() +"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("地址："+Constants.COMPANY_COMMON_ADDRESS,PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2(contract.get("REGISTERED_OFFICE_ADDRESS")==null?"地址：":"地址："+contract.get("REGISTERED_OFFICE_ADDRESS").toString() +"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	String buyerphone="";
		    	if(contract.get("TELEPHONE")!=null){
		    		buyerphone=contract.get("TELEPHONE").toString();
		    	}else if(contract.get("CUST_PHONE")!=null){
		    		buyerphone=contract.get("CUST_PHONE").toString();
		    	}else if(contract.get("BUYER_PHONE")!=null){
		    		buyerphone=contract.get("BUYER_PHONE").toString();
		    	}

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("电话号码：0512-80983566",PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2("电话号码："+buyerphone+"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		   
		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanLeftAndBottom("传真号码：0512-80983567",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellSetColspan3NoTop(contract.get("FAX")==null?"传真号码：":"传真号码："+contract.get("FAX").toString(),PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));
		    	tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));

		    	i+=8;
	    //第一个子表结束，以下是第二个子表

		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("甲方签章栏 ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0.5f,0.5f,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("乙方签章栏  ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0.5f,0.5f,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		        
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("日期：     ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("日期：    ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0.5f,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0.5f,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		   
		    
		    	i+=9;
		    	for(;i<43;i++){
		    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
		    	}
		    	if(i<=43){
		    		tT.addCell(makeCellSetColspan2("  1  ", PdfPCell.ALIGN_CENTER, FontDefault,8));
		    	}
		    	i+=1;
		    	if(i<=44){
		    		tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));
		    	}
		    	//document.add(Chunk.NEXTPAGE);
		    	String leasecode="";
	    		if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    			leasecode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
		    		tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));  
		  	 	  tT.addCell(makeCellSetColspan2222("     甲方与乙方经协商，达成如下协议：",PdfPCell.ALIGN_LEFT, FontColumn2,8));
		    	  tT.addCell(makeCellSetColspan2222("     第一条	合同签订依据",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                甲方与乙方签订了合同编号为【"+leasecode+"】的融资租赁合同（以下简称“租",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           赁合同” ），根据该合同甲方为乙方提供融资租赁服务。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第二条	标的物",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               甲、乙双方约定的标的物时乙方向甲方的租赁物，是指本合同中的设备，详见融",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           资租赁合同项下的标的物清单。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第三条	委托购买事项",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               1、	乙方已经自行选定设备和供应商",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               2、	乙方为设备的最终使用人",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               3、	根据本合同的条款和条件，甲方同意委托乙方代表其与供应商商定条件并签署销售",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          合同。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               4、	为使乙方能够充分行使其对设备的选择权和决定权，甲方委托乙方购买相应的设备，",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          未含17%增值税设备的总价款为人民币【"+totalmoney+"】元。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          如果乙方在起租前有任何违反租赁合同约定的情况，甲方有权终止本合同。在上款",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          情形下，乙方应作为买方，履行与供应商之间签订的销售合同项下的所有义务。乙",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          方应当保证甲方免于供应商就设备购买的事项产生的任何索赔，包括甲方由此产生",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          的责任、费用和损失，并补偿甲方的费用及损失。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第四条	销售合同签署",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                1、	乙方自行与供应商商定所有设备购买的条款和条件，包括但不限于设备规格、性能、",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          安装、验收、维护、保修等一切条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                2、	乙方得以自身名义与供应商就设备签署销售合同（“销售合同” ），但是销售合同",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          签署前应当得到甲方的书面确认，乙方与供应商签署的销售合同应当与甲方确认",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          的版本一致。销售合同签署后，乙方应将副本提交甲方。未得甲方实现书面同意，",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          乙方不得同意修改销售合同的任何内容。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				    
				    
				  tT.addCell(makeCellSetColspan2222("                3、	本合同项下设备所有权及其他权益归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                4、	甲方的义务只是承担销售合同设备价款的支付，不承担销售合同的其他义务。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("        ",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				   			    
				  tT.addCell(makeCellSetColspan2222("     第五条	付款的要求与流程",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               设备价款的支付应当符合销售合同的约定，并且甲方仅有义务在收到下列所有文",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           件后【30】日内向供应商支付每一笔设备价款：",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （1）	供应商的付款通知书；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （2）	供应商发票；",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （3）	乙方的付款指示书；",PdfPCell.ALIGN_LEFT, FontDefault2,8));	    
				  tT.addCell(makeCellSetColspan2222("            （4）	乙方签署的租赁物验收证明书（如销售合同项下任何设备价款的支付时间在设",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("             备交付之后）。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  
				  tT.addCell(makeCellSetColspan2222("     第六条	  设备的交付及相关税金",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  
				  tT.addCell(makeCellSetColspan2222("               1、	设备由供应商根据销售合同直接交付给乙方，即视为甲方将租赁合同项下的租赁物",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           交付于乙方。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			      tT.addCell(makeCellSetColspan2222("               2、	乙方同意根据中华人民共和国相关的法律和规定，缴纳所应承担的税款。如果乙",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
				  tT.addCell(makeCellSetColspan2222("           方享有与设备有关的减免税或其他税收优惠待遇，适用的相关手续应由乙方自行",PdfPCell.ALIGN_LEFT, FontDefault2,8));   
				  tT.addCell(makeCellSetColspan2222("           办理，所产生的法律后果由乙方承担。如果因乙方办理上述手续过程中的任何违",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			      tT.addCell(makeCellSetColspan2222("           法行为而致使甲方受到任何处罚，乙方将就此对甲方进行赔偿。乙方办理与设备",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
				  tT.addCell(makeCellSetColspan2222("           有关的减免税或其他税收优惠待遇不影响甲方对设备的所有权。",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				  			
				  tT.addCell(makeCellSetColspan2222("     第七条	设备涉及的索赔",PdfPCell.ALIGN_LEFT, FontColumn2,8));			    
				  tT.addCell(makeCellSetColspan2222("               1、	乙方作为设备的最终使用人在此确认，乙方对设备购买的自主选择和决定负全部责",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           任，在任何情况下，甲方不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));		
				  tT.addCell(makeCellSetColspan2222("               2、	若发生供应商延迟设备的交货或提供的设备与销售合同所规定的内容不符，或在安",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           装、调试、操作过程中及质量保证期间发生设备存在质量瑕疵，乙方因任何原因不",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           满意设备或其他与设备有关的任何问题，甲方不承担赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               3、	乙方若因前款原因遭受损害，由乙方根据销售合同的规定直接向供应商行使索赔",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           权。乙方若与供应商达成赔偿协议，该协议应当得到甲方的书面认可。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           满意设备或其他与设备有关的任何问题，甲方不承担赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               4、	乙方若就赔偿事宜提起仲裁和/或诉讼，则仲裁和/或诉讼的全部费用和一切法律后",PdfPCell.ALIGN_LEFT, FontDefault2,8));
		//结束页			 
				  tT.addCell(makeCellSetColspan2222("  2  ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
				  tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));  
				  tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("           果均由乙方承担。索赔的全部补偿所得归乙方所有。在仲裁和/或诉讼过程中，乙",PdfPCell.ALIGN_LEFT, FontDefault2,8));							 
				  tT.addCell(makeCellSetColspan2222("           方若要求甲方协助，甲方应当给予合理协助。乙方同意，即使出现乙方向供应商",PdfPCell.ALIGN_LEFT, FontDefault2,8));

				  tT.addCell(makeCellSetColspan2222("           索赔的情况，无论乙方是否能够通过索赔得到补偿，也无论索赔是否在进行中，",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          均不影响租赁合同的效力，乙方均应按租赁合同规定向甲方支付租金及其他应付",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          款项。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  				  				  						  
				  tT.addCell(makeCellSetColspan2222("     第八条	适用法律和争议解决",PdfPCell.ALIGN_LEFT, FontColumn2,8));	
				  tT.addCell(makeCellSetColspan2222("               本合同的签署、执行及其他事项适用中华人民共和国法律。有关本合同的任何争",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           议，由当事人友好协商解决。当事人难以协商解决时，由甲方注册所在地有管辖权的",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           法院通过诉讼解决。甲方为实现本合同项下债权所需费用（包括但不限于催收费用、",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           诉讼费、保全费、公告费、执行费、律师费、差旅费及其他费用）由乙方承担。争议",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           期间，各方仍应继续履行未涉争议的条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第九条 其他约定",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                1、	乙方签署本合同及履行本合同所产生的任何费用均由乙方自行承担，并且甲方无须",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           向其支付任何报酬。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                2、	本合同经双方协商，并经双方确认后才能修改本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			    			    
				  tT.addCell(makeCellSetColspan2222("     第十条	本合同一式两份，经双方盖章后生效。",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  i+=83;
				  for(;i<162;i++){
				  	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
				  }
				  if(i==162){
				  	tT.addCell(makeCellSetColspan2("  3  ", PdfPCell.ALIGN_CENTER, FontDefault,8));
				  }
				  i+=1;
				  if(i<=163){
				  	tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));
				  }
			}
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
			document.close();
	    // 支付表PDF名字的定义
			String strFileName = "pucscontract.pdf";
		if(contract.get("PUCT_CODE")!=null){
			 strFileName = contract.get("PUCT_CODE")+ ".pdf";
		}else{
			 strFileName = "pucscontract.pdf";
		}
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

		//记录到系统日志中 add by ShenQi
		BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("rect_id")),null,
		   		 "导出 委托购买合同(合同类型:"+code+")",
	   		 	 "合同浏览导出 委托购买合同",
	   		 	 null,
	   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
	   		 	 1,
	   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	   		 	 DataUtil.longUtil(0),
	   		 	 context.getRequest().getRemoteAddr());
	} catch (Exception e) {
	    e.printStackTrace();
	    LogPrint.getLogStackTrace(e, logger);
	}
	
	
    }
    
/*
 * Add by Michael 2012-3-23 
 * 导出重车委托购买合同
 */
	public void expCarContractCredit(Context context){
		try{
			Map contract =new HashMap();
			Map contractaplyname =new HashMap();
			Map contracttype =new HashMap();
			List<Map> pucsContractDetail = null;
	    	String cons= (String) context.contextMap.get("rect_id");
	    	
	    	context.contextMap.put("PRCD_ID",  cons);
	    	List contractinfo=(List) DataAccessor.query("exportContractPdf.judgeExitContract", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
	    	//查找合同的相关信息
	    	
	    	if(contractinfo.size()==0){
	    		
		    	contract = (Map) DataAccessor.query("exportContractPdf.queryCreditInfoByCreditIds", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	contractaplyname = (Map) DataAccessor.query("exportContractPdf.selectCreditInfoByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	contracttype = (Map) DataAccessor.query("exportContractPdf.queryCreditTypeByPrcdId", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
		    	if(contracttype!=null){
		    		if(contracttype.get("CONTRACT_TYPE")!=null){
	    				contract.put("BUYER_UNIT_NAME", contract.get("CUST_UNIT_NAME"));
		    		}
		    	}
		    	//查找合同的类型
		    	if(contractaplyname!=null){
		    		if(contractaplyname.get("APPLYNAME")!=null){
		    			contract.put("SELLER_UNIT_NAME", contractaplyname.get("APPLYNAME"));
		    		}
		    	}

		    	contract.put("BUYER_UNIT_NAME", contract.get("CUST_UNIT_NAME"));		    		
		  
		    	pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.readCreditInfoDetail", context.contextMap, DataAccessor.RS_TYPE.LIST);
		    	contract.put("TOTAL", contracttype.get("LEASE_TOPRIC"));
	    	}else{
	    		
	    		contract = (Map) DataAccessor.query("exportContractPdf.findContractInfoByPrcdidnew", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		contracttype = (Map) DataAccessor.query("exportContractPdf.findContractInfoByPrcdidnew", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
	    		pucsContractDetail = (List<Map>) DataAccessor.query("exportContractPdf.findContractEquipmentByPrcdidnew", context.contextMap, DataAccessor.RS_TYPE.LIST);
	    	}
		    this.expCarContractPdf(context,contract,contracttype,pucsContractDetail);
		    
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}    

    @SuppressWarnings("unchecked")
    public void expCarContractPdf(Context context,Map contract,Map contracttype,List<Map> pucsContractDetail) {
	
	ByteArrayOutputStream baos = null;
 	
	try {   
	        // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	      	Font FontColumn2 = new Font(bfChinese, 10, Font.BOLD);
	        Font FontDefault22 = new Font(bfChinese, 9, Font.NORMAL);
	        Font FontDefault23 = new Font(bfChinese, 7, Font.NORMAL);
	        Font FontDefault2 = new Font(bfChinese, 10, Font.NORMAL);
	        Font FontDefault = new Font(bfChinese, 11, Font.NORMAL);

	        Font fa = new Font(bfChinese, 22, Font.BOLD);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos);
	        
	        // 打开文档
	        document.open();

			String code = "";
			String totalmoney = "";
			String qiandingdate = "";
			String deleverydate = "";
			if(contracttype!=null){
				if(contracttype.get("CONTRACT_TYPE")!=null){
					code=contracttype.get("CONTRACT_TYPE").toString();
				}
				if(contracttype.get("LESSOR_TIME")!=null){
					qiandingdate = contracttype.get("LESSOR_TIME").toString().substring(0, 10);
				}
	    	}
			if(contract!=null){
				if(contract.get("TOTAL")!=null){
					totalmoney = updateMoney(contract,"TOTAL",NumberFormat.getInstance());
				}
				if(contract.get("DELIVERY_DATE")!=null){
					deleverydate=contract.get("DELIVERY_DATE").toString().substring(0,10);
				}
			}
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);
			int i=0;
		  
				tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
	 	    
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
	  
				tT.addCell(makeCellSetColspan2("委托购买合同",PdfPCell.ALIGN_CENTER, fa,8));
		  	      
				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
				i+=6;
		    
				String Lcode ="";
				if(contract.get("PUCT_CODE")!=null){
					//Lcode = contract.get("PUCT_CODE") +"";
				}
				if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    				Lcode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
		    	Lcode=	Lcode.trim();
		    	if(Lcode.equals("")){
		    		Lcode = "           ";
		    	}
		    	tT.addCell(makeCellSetColspan2(" ",PdfPCell.ALIGN_RIGHT, FontDefault,8));
		   
		    	tT.addCell(makeCellSetColspanOnlyLeft("     " ,PdfPCell.ALIGN_LEFT,  FontDefault,5));	   
		    	tT.addCell(makeCellWithNoBorder("合同编号: "  ,PdfPCell.ALIGN_LEFT,  FontDefault));	
		    	tT.addCell(makeCellSetColspanOnlyRight(""+Lcode,PdfPCell.ALIGN_LEFT, FontDefault,2));  
		   
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));		   
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8));
   

		    	tT.addCell(makeCellWithBorderLeft("     ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspan2LeftAndTop("甲方："+Constants.COMPANY_NAME,PdfPCell.ALIGN_LEFT, FontDefault,3));	
		    	tT.addCell(makeCellSetColspan3(contract.get("BUYER_UNIT_NAME")==null?"乙方: ":"乙方: "+contract.get("BUYER_UNIT_NAME") +"",PdfPCell.ALIGN_LEFT, FontDefault,3));	    
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("法定代表人："+Constants.LEGAL_PERSON,PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2(contract.get("BUYER_AGENT")==null?"法定代表人：":"法定代表人："+contract.get("BUYER_AGENT").toString() +"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("地址："+Constants.COMPANY_COMMON_ADDRESS,PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2(contract.get("REGISTERED_OFFICE_ADDRESS")==null?"地址：":"地址："+contract.get("REGISTERED_OFFICE_ADDRESS").toString() +"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	String buyerphone="";
		    	if(contract.get("TELEPHONE")!=null){
		    		buyerphone=contract.get("TELEPHONE").toString();
		    	}else if(contract.get("CUST_PHONE")!=null){
		    		buyerphone=contract.get("CUST_PHONE").toString();
		    	}else if(contract.get("BUYER_PHONE")!=null){
		    		buyerphone=contract.get("BUYER_PHONE").toString();
		    	}

		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanOnlyLeft("电话号码：0512-80983566",PdfPCell.ALIGN_LEFT, FontDefault22,3));	
		    	tT.addCell(makeCellSetColspan2("电话号码："+buyerphone+"",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		   
		    	tT.addCell(makeCellWithBorderLeft("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	tT.addCell(makeCellSetColspanLeftAndBottom("传真号码：0512-80983567",PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellSetColspan3NoTop(contract.get("FAX")==null?"传真号码：":"传真号码："+contract.get("FAX").toString(),PdfPCell.ALIGN_LEFT, FontDefault22,3));
		    	tT.addCell(makeCellWithBorderRight("    ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));
		    	tT.addCell(makeCellSetColspan2("      ",PdfPCell.ALIGN_CENTER, FontDefault,8));

		    	i+=8;
	    //第一个子表结束，以下是第二个子表

		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("甲方签章栏 ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0.5f,0.5f,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("乙方签章栏  ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0.5f,0.5f,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		        
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("法定代表人或授权代表人：",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("日期：     ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("日期：    ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    
		    	tT.addCell(makeCellWithBorderLeft("      ",PdfPCell.ALIGN_LEFT, FontDefault));	
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0.5f,0.5f,0}));	    
		    	tT.addCell(makeCellSetColspanAuto("      ",PdfPCell.ALIGN_LEFT, FontDefault,3,new float[]{0,0.5f,0.5f,0.5f}));	    
		    	tT.addCell(makeCellWithBorderRight("      ",PdfPCell.ALIGN_LEFT, FontDefault));
		    	
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
		    	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
		    	i+=9;

		    	String leasecode="";
	    		if(contracttype!=null){
	    			if(contracttype.get("LEASE_CODE")!=null){
	    			leasecode=contracttype.get("LEASE_CODE").toString();
	    			}
	    		}
		    	  //tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));  
		  	 	  tT.addCell(makeCellSetColspan2222("     甲方与乙方经协商，达成如下协议：",PdfPCell.ALIGN_LEFT, FontColumn2,8));
		    	  tT.addCell(makeCellSetColspan2222("     第一条	合同签订依据",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                甲方与乙方签订了合同编号为【"+leasecode+"】的融资租赁合同（以下简称",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           “租赁合同” ），根据该合同甲方为乙方提供融资租赁服务。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第二条	标的物",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               甲、乙双方约定的标的物是乙方向甲方融资租赁的租赁物，是指本合同中的设备",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           (包括但不限于各式车辆，机械设施等)，详见融资租赁合同项下的《租赁物情况表》。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  
				  tT.addCell(makeCellSetColspan2222("     第三条	委托购买事项",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               1、	乙方已经自行选定设备和供应商",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               2、	乙方为设备的最终使用人",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               3、	根据本合同的条款和条件，甲方同意委托乙方代表其与供应商商定条件并签署销售",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          合同。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               4、为使乙方能够充分行使其对设备的选择权和决定权，甲方委托乙方购买相应的设",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          备，含17%增值税设备的总价款为人民币【"+totalmoney+"】元。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          如果乙方在起租前有任何违反租赁合同约定的情况，甲方有权终止本合同。在上款",PdfPCell.ALIGN_LEFT, FontDefault2,8));		    	
				  tT.addCell(makeCellSetColspan2222("          情形下，乙方应作为买方，履行与供应商之间签订的销售合同项下的所有义务。甲",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          方就乙方向供货商购买租赁物的事项不承担任何责任，乙方应当保证甲方免于供应",PdfPCell.ALIGN_LEFT, FontDefault2,8));		    	
				 
	    		tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
		    	
//		    	if(i<=43){
		    		tT.addCell(makeCellSetColspan2("  1  ", PdfPCell.ALIGN_CENTER, FontDefault,8));
//		    	}
//		    	i+=1;
//		    	if(i<=44){
					  tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));  
					  tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
//		    	}
		    	//document.add(Chunk.NEXTPAGE);


				  tT.addCell(makeCellSetColspan2222("          商就设备购买的事项产生的任何索赔，如因购买事项给甲方造成了任何损失，乙方",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          应负责赔偿甲方发生的损失。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               5、本合同仅限于购买上述租赁物，不予他用。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
														  		    	
				  tT.addCell(makeCellSetColspan2222("     第四条	销售合同签署",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                1、	乙方自行与供应商商定所有设备购买的条款和条件，包括但不限于设备规格、性能、",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          安装、验收、维护、保修等一切条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                2、	乙方得以自身名义与供应商就设备签署销售合同（“销售合同” ），但是销售合同",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          签署前应当得到甲方的书面确认，乙方与供应商签署的销售合同应当与甲方确认的",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          版本一致。销售合同签署后，乙方应将副本提交甲方。未经甲方实现书面同意，乙",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          方不得与供货商私自修改销售合同的任何内容。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                3、	本合同项下设备所有权及其他权益归甲方所有。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                4、甲方的义务只是依上述融资租赁合同内容承担销售合同设备价款的支付，不承担销",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("          售合同的其他义务。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("        ",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				   			    
				  tT.addCell(makeCellSetColspan2222("     第五条	付款的要求与流程",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("               设备价款的支付应当符合销售合同的约定，并且甲方仅有义务在收到下列所有文",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           件后【30】日内向供应商支付每一笔设备价款：",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （1）	供应商的确认函和与甲、乙三方签订的设备价款支付协议。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （2）	乙方与供货商签订的设备买卖合同复印件。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （3）	乙方签署的设备价款付款指示书、确认书、承诺暨授权书、车辆处分授权委托书。",PdfPCell.ALIGN_LEFT, FontDefault2,8));	    
				  tT.addCell(makeCellSetColspan2222("            （4）	乙方签署的租赁物验收证明暨起租通知书。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （5）	甲、乙双方签定的融资租赁合同、委托购买合同。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （6）	车辆挂靠案件挂靠公司的授权书、甲、乙双方签定的车辆挂靠管理协议及甲、",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("             乙、挂靠行三方签订的租赁物委托管理三方协议。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("            （7）	其他甲方要求的文件证明。",PdfPCell.ALIGN_LEFT, FontDefault2,8));	    
			  
				  tT.addCell(makeCellSetColspan2222("     第六条	  设备的交付及相关税金",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  
				  tT.addCell(makeCellSetColspan2222("               1、	设备由供应商根据销售合同直接交付给乙方，即视为甲方将租赁合同项下的租赁物",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           交付于乙方。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			      tT.addCell(makeCellSetColspan2222("               2、	乙方同意完成购买设备应办理的一切审批手续，并以符合产权登记人的资格条件，",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
				  tT.addCell(makeCellSetColspan2222("           依据中华人民共和国相关的法律和规定，缴纳所应承担的税、费。如果乙方享有与",PdfPCell.ALIGN_LEFT, FontDefault2,8));   
				  tT.addCell(makeCellSetColspan2222("           设备有关的减免税或其他税收优惠待遇，适用的相关手续应由乙方自行办理，所产",PdfPCell.ALIGN_LEFT, FontDefault2,8));
			      tT.addCell(makeCellSetColspan2222("           生的法律后果由乙方承担。如果因乙方办理上述手续过程中的任何违法行为而致使",PdfPCell.ALIGN_LEFT, FontDefault2,8));    
				  tT.addCell(makeCellSetColspan2222("           甲方受到任何处罚，乙方将就此对甲方进行赔偿。乙方办理与设备有关的减免税或",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				  tT.addCell(makeCellSetColspan2222("           其他税收优惠待遇不影响甲方对设备的所有权。",PdfPCell.ALIGN_LEFT, FontDefault2,8));  
				  				  			
				  tT.addCell(makeCellSetColspan2222("     第七条	设备涉及的索赔",PdfPCell.ALIGN_LEFT, FontColumn2,8));			    
				  tT.addCell(makeCellSetColspan2222("               1、	乙方作为设备的最终使用人在此确认，乙方对设备购买的自主选择和决定负全部责",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           任，在任何情况下，甲方不承担任何责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));		
				  tT.addCell(makeCellSetColspan2222("               2、	若发生供应商延迟设备的交货或提供的设备与销售合同所规定的内容不符，或在安",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           装、调试、操作过程中及质量保证期间发生设备存在质量瑕疵，乙方因任何原因不",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           满意设备或其他与设备有关的任何问题，甲方不承担赔偿责任。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               3、	乙方若因前款原因遭受损害，由乙方根据销售合同的规定直接向供应商行使索赔",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           权。乙方若与供应商达成赔偿协议，该协议应当得到甲方的书面认可。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("               4、	乙方若就赔偿事宜提起仲裁和/或诉讼，则仲裁和/或诉讼的全部费用和一切法律后",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           果均由乙方承担。索赔的全部补偿所得归乙方所有。在仲裁和/或诉讼过程中，乙",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           方若要求甲方协助，甲方应当给予合理协助。乙方同意，即使出现乙方向供应商索",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           赔的情况，无论乙方是否能够通过索赔得到补偿，也无论索赔是否在进行中，均不",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           影响租赁合同的效力，乙方均应按租赁合同规定向甲方支付租金及其他应付款项。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				 
				  tT.addCell(makeCellSetColspan2222("     第八条	适用法律和争议解决",PdfPCell.ALIGN_LEFT, FontColumn2,8));	
				  tT.addCell(makeCellSetColspan2222("               本合同的签署、执行及其他事项适用中华人民共和国法律。有关本合同的任何争",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           议，由当事人友好协商解决。当事人难以协商解决时，由甲方注册所在地有管辖权的",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           法院通过诉讼解决。甲方为实现本合同项下债权所需费用（包括但不限于催收费用、",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           诉讼费、保全费、公告费、执行费、律师费、差旅费及其他费用）由乙方承担。争议",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           期间，各方仍应继续履行未涉争议的条款。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  

				tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
			  
				  //结束页			 
				  tT.addCell(makeCellSetColspan2222("  2  ",PdfPCell.ALIGN_CENTER, FontDefault2,8));
				  tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));  
				  tT.addCell(makeCellSetColspan3("        ", PdfPCell.ALIGN_CENTER, FontDefault2,8));
				  

				  tT.addCell(makeCellSetColspan2222("     第九条 其他约定",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  tT.addCell(makeCellSetColspan2222("                1、	乙方签署本合同及履行本合同所产生的任何费用均由乙方自行承担，并且甲方无须",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("           向其支付任何报酬。",PdfPCell.ALIGN_LEFT, FontDefault2,8));
				  tT.addCell(makeCellSetColspan2222("                2、	本合同经双方协商，并经双方确认后才能修改本合同。",PdfPCell.ALIGN_LEFT, FontDefault2,8));			    			    
				  tT.addCell(makeCellSetColspan2222("     第十条	本合同一式两份，经双方盖章后生效。",PdfPCell.ALIGN_LEFT, FontColumn2,8));
				  				  
//				  i+=83;
				  for(i=0;i<36;i++){
				  	tT.addCell(makeCellSetColspan2("    ",PdfPCell.ALIGN_LEFT, FontDefault,8)); 
				  }
//				  if(i==162){
				  	tT.addCell(makeCellSetColspan2("  3  ", PdfPCell.ALIGN_CENTER, FontDefault,8));
//				  }
//				  i+=1;
//				  if(i<=163){
				  	tT.addCell(makeCellSetColspan3NoTop("    ", PdfPCell.ALIGN_LEFT, FontDefault,8));
//				  }
		
			document.add(tT);
			document.add(Chunk.NEXTPAGE);
			document.close();
	    // 支付表PDF名字的定义
			String strFileName = "pucscontract.pdf";
		if(contract.get("PUCT_CODE")!=null){
			 strFileName = contract.get("PUCT_CODE")+ ".pdf";
		}else{
			 strFileName = "pucscontract.pdf";
		}
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
    



    /** 自由式
     *  最后一个参数必须是4个float型数组
     *  顺序上下左右
     *  */
    private PdfPCell makeCellSetColspanAuto(String content, int align,
    		Font FontDefault, int colspan,float[] borderWidth) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	if(borderWidth.length >= 4){
	    	objCell.setBorderWidthTop(borderWidth[0]);
	    	objCell.setBorderWidthBottom(borderWidth[1]);
	    	objCell.setBorderWidthLeft(borderWidth[2]);
	    	objCell.setBorderWidthRight(borderWidth[3]) ;
    	}
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
    /**
     * 全有
     * @param content
     * @param align
     * @param FontDefault
     * @param colspan
     * @return
     */
    private PdfPCell makeCellSetColspanAll(String content, int align,
    		Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(0.5f);
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
    /** 创建 有边框 合并 单元格   |_
     *  只有Left Bottom
     *  
     *  */
    private PdfPCell makeCellSetColspanLeftAndBottom(String content, int align,
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


    /**
     *  创建 有边框 四边都有 合并 单元格|-_|
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
    /**
     * 对应四边都有的 
     * @param content
     * @param align
     * @param FontDefault
     * @param colspan
     * @return
     */
    private PdfPCell makeCellSetColspanNoL(String content, int align,
    		Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
    	objCell.setBorderWidthLeft(0.5f) ;
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
    /**
     * 左上下 
     * @param content
     * @param align
     * @param FontDefault
     * @param colspan
     * @return
     */
    private PdfPCell makeCellSetColspanLTB(String content, int align,
    		Font FontDefault, int colspan) {
    	Phrase objPhase = new Phrase(content, FontDefault);
    	PdfPCell objCell = new PdfPCell(objPhase);
    	objCell.setHorizontalAlignment(align);
    	objCell.setVerticalAlignment(align);
    	objCell.setColspan(colspan);
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


	public void queryRentFile(Context context){
		ArrayList commandFileConditions=new ArrayList();
		ArrayList specialFileConditions=new ArrayList();
		try {
		
			List commandFileNames = (List) DataAccessor.query("exportContractPdf.queryAllCommendContractFileNames", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			List specialFileNames = (List) DataAccessor.query("exportContractPdf.queryAllSpecialContractFileNames", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			List commonRectFiles = (List) DataAccessor.query("exportContractPdf.queryAllCommandRent", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			List specialRectFiles = (List) DataAccessor.query("exportContractPdf.queryAllSpecialRent", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			for(int k=0;k<commonRectFiles.size();k++){				
				HashMap rentMap=(HashMap)commonRectFiles.get(k);
				List commonFiles=(List) DataAccessor.query("exportContractPdf.queryAllCommandRentFiles",rentMap, DataAccessor.RS_TYPE.LIST);
					//控制这一行的显示列数，一般的是11，委托的是12	
					for(int i=0;i<commandFileNames.size();i++){
					    HashMap filename=(HashMap)commandFileNames.get(i);
						HashMap commonFileMap=null;
						if(commonFiles.size()==0){
							  rentMap.put(i, 'N');
							  rentMap.put("EQUL", -5);
						}else{
							for(int ks=0;ks<commonFiles.size();ks++){
								 commonFileMap=(HashMap)commonFiles.get(ks);
								 rentMap.put("EQUL", Integer.parseInt(commonFileMap.get("EQUL").toString()));
								if(commonFileMap!=null){
								   if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
										rentMap.put(i, commonFileMap.get("CHA").toString());
								 	 }
								} 
							}
						}
					}
					if(commonFiles!=null){
						int  EQUL=Integer.parseInt(rentMap.get("EQUL").toString());
						if(commandFileNames.size()-commonFiles.size()>0){
							EQUL=EQUL-(commandFileNames.size()-commonFiles.size());
						}
						  rentMap.put("EQUL", EQUL);
					}
					int  EQUL=Integer.parseInt(rentMap.get("EQUL").toString());
				if(EQUL<=-1){
					commandFileConditions.add(rentMap);
				}
				//合同的11项附件数小于总附件数时才显示合同信息
//				if(commonFiles.size()<commonRectFiles.size()){
//					//控制这一行的显示列数，一般的是11，委托的是12	
//					for(int i=0;i<commandFileNames.size();i++){
//							HashMap commonFileMap=null;
//							if(i<commonFiles.size()){
//								commonFileMap=(HashMap)commonFiles.get(i);
//							}				
//							if(commonFileMap!=null){
//								HashMap filename=(HashMap)commandFileNames.get(i);
//									if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
//										rentMap.put(i, "Y");
//									}else{
//										rentMap.put(i, "N");
//									}
//							}else{
//								rentMap.put(i, "N");
//							}
//							
//						}
//					commandFileConditions.add(rentMap);
//				}
//				
			}
			for(int k=0;k<specialRectFiles.size();k++){				
				HashMap rentMap=(HashMap)specialRectFiles.get(k);
				List specialFiles=(List) DataAccessor.query("exportContractPdf.queryAllSpecialRentFiles",rentMap, DataAccessor.RS_TYPE.LIST);
				//合同的12项附件数小于总附件数时才显示合同信息
				for(int i=0;i<specialFileNames.size();i++){
					HashMap filename=(HashMap)specialFileNames.get(i);
					HashMap commonFileMap=null;
					if(specialFiles.size()==0){
						  rentMap.put(i, 'N');
						  rentMap.put("EQUL", -5);
					}else{
						for(int ks=0;ks<specialFiles.size();ks++){
							 commonFileMap=(HashMap)specialFiles.get(ks);
							 rentMap.put("EQUL", Integer.parseInt(commonFileMap.get("EQUL").toString()));
							 if(commonFileMap!=null){
									   if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
											rentMap.put(i, commonFileMap.get("CHA").toString());
										}
								}
						}
					}
				}
				if(specialFiles!=null){
					int  EQUL=Integer.parseInt(rentMap.get("EQUL").toString());
					if(specialFileNames.size()-specialFiles.size()>0){
						EQUL=EQUL-(specialFileNames.size()-specialFiles.size());
					}
					  rentMap.put("EQUL", EQUL);
				}
				int  EQUL=Integer.parseInt(rentMap.get("EQUL").toString());
			if(EQUL<=-1){
				specialFileConditions.add(rentMap);
			}
					
					
				
				
				
//				
//				if(specialFiles.size()<specialRectFiles.size()){
//					//控制这一行的显示列数，一般的是11，委托的是12	
//					for(int i=0;i<specialFileNames.size();i++){
//							HashMap commonFileMap=null;
//							if(i<specialFiles.size()){
//								commonFileMap=(HashMap)specialFiles.get(i);
//							}				
//							if(commonFileMap!=null){
//								HashMap filename=(HashMap)specialFileNames.get(i);
//									if(commonFileMap!=null&&commonFileMap.get("FILE_NAME").toString().equals(filename.get("FILE_NAME").toString())){
//										rentMap.put(i, "Y");
//									}else{
//										rentMap.put(i, "N");
//									}
//							}else{
//								rentMap.put(i, "N");
//							}
//							
//						}
//					specialFileConditions.add(rentMap);
//				}
				
			}
			ExportWaitFileEcl tt=new ExportWaitFileEcl();
			List tts=new ArrayList();
			tt.createexl();
			
			//ByteArrayOutputStream baos =tt.export(commandFileNames,specialFileNames,commonRectFiles,specialRectFiles);
			ByteArrayOutputStream baos =tt.export(commandFileNames,specialFileNames,commandFileConditions,specialFileConditions);
			context.response.setContentType("application/vnd.ms-excel;charset=GB2312");		
			String strFileName = "待补文件统计("+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
			context.response.setHeader("Content-Disposition", "attachment;filename="+ new String(strFileName.getBytes("GBK"), "ISO-8859-1"));
			context.response.setContentType("application nd.ms-excel;charset=GB2312");
			ServletOutputStream out1 = context.response.getOutputStream();
			tt.close();
			baos.writeTo(out1);
			out1.flush();
		} catch (Exception e) {			 
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	
	}
	






   
         
	
 
}