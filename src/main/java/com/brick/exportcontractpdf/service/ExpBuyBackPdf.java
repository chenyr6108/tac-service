package com.brick.exportcontractpdf.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.backMoney.service.backMoneyToPdf;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.log.service.LogPrint;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DataUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ExpBuyBackPdf extends AService{
	Log logger = LogFactory.getLog(backMoneyToPdf.class);
	//导出不含灭失的回购合同_如果合同不存在，则从报告中取数据
	@SuppressWarnings("unchecked")
	public void expPdfprjt(Context context){
		try {
			if(context.getContextMap().get("RECT_ID")!=null && !"".equals(context.getContextMap().get("RECT_ID"))){
				Map contract = (Map)DataAccessor.query("expbuyback.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				
				expPdf_model(context, contract, eqmts);
			}else{
				Map contract = (Map)DataAccessor.query("expbuyback.queryContractPrjt",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				
				expPdf_model(context, contract, eqmts);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//导出不含灭失的回购合同_如果合同不存在，则从报告中取数据
	@SuppressWarnings("unchecked")
	public void expPdf2prjt(Context context){
		try {
			if(context.getContextMap().get("RECT_ID")!=null && !"".equals(context.getContextMap().get("RECT_ID"))){
				Map contract = (Map)DataAccessor.query("expbuyback.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				
				expPdf2_model(context, contract, eqmts);
			}else{
				Map contract = (Map)DataAccessor.query("expbuyback.queryContractPrjt",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
				List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmtsPrjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
				
				expPdf2_model(context, contract, eqmts);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//导出不含灭失的回购合同
	@SuppressWarnings("unchecked")
	public void expPdf(Context context){
		try {
			Map contract = (Map)DataAccessor.query("expbuyback.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
			List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			
			expPdf_model(context, contract, eqmts);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	//导出不含灭失的回购合同
	@SuppressWarnings("unchecked")
	public void expPdf2(Context context){
		try {
			Map contract = (Map)DataAccessor.query("expbuyback.queryContract",context.getContextMap(),DataAccessor.RS_TYPE.MAP);
			List<Map> eqmts=(List)DataAccessor.query("expbuyback.queryeqmts", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			
			expPdf2_model(context, contract, eqmts);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//导出确认书
	@SuppressWarnings("unchecked")
	public void expConfirm(Context context){
		try{
			Map map=(Map)DataAccessor.query("expbuyback.confirm", context.getContextMap(), DataAccessor.RS_TYPE.MAP);
			expConfirm_model(context
					,toString(map,"LEASE_CODE")
					,toString(map,"SELLER_UNIT_NAME")
					,toString(map,"CUST_UNIT_NAME"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void expConfirmprjt(Context context){
		try{
			//Modify by Michael 2012 07-25 确认书带出所有供应商名称
			List map=(List)DataAccessor.query("expbuyback.confirmprjt", context.getContextMap(), DataAccessor.RS_TYPE.LIST);
			expConfirm_model(context,map);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private String toString(Map map,String key){
		return map.get(key)==null?" ":map.get(key).toString();
	}
	
	//导出不含灭失的回购合同模板
	@SuppressWarnings("unchecked")
	public void expPdf_model(Context context,Map contract,List<Map> eqmts){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try{
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontTitle= new Font(bfChinese,13,Font.BOLD);
			Font FontNormal = new Font(bfChinese, 11,Font.NORMAL);
			Font FontUN = new Font(bfChinese, 11,Font.UNDERLINE);//下划线
			
			//页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        PdfWriter.getInstance(document, baos);
	        
	        HeaderFooter footer=new HeaderFooter(new Phrase("页码："), true);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        footer.setBorder(Rectangle.NO_BORDER);
	        document.setFooter(footer);
	        document.open();
	        
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);

		 	    
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				PdfPCell cell1=noTopandButton("回购合同",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontTitle,8);
				cell1.setPaddingRight(35);
				tT.addCell(cell1);
				
				
			    String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
				String address = LeaseUtil.getCompanyAddressByCompanyCode(1);
				String postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(1);
				String telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(1);
				String fax = LeaseUtil.getCompanyFaxByCompanyCode(1);
				String creditId = null;
				if(context.getContextMap().get("RECT_ID")!=null && !"".equals(context.getContextMap().get("RECT_ID"))){
					creditId = String.valueOf(LeaseUtil.getCreditIdByRectId((String)context.getContextMap().get("RECT_ID")));
				}else{
					creditId = (String) context.getContextMap().get("PRCD_ID");
				}
				int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
				String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
				if("7".equals(contractType)){
				    companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
					address = LeaseUtil.getCompanyAddressByCompanyCode(companyCode);
					postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(companyCode);
					telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(companyCode);
					fax = LeaseUtil.getCompanyFaxByCompanyCode(companyCode);
				}
				
	    		tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,5));	   
	    		tT.addCell(noTopandButton("合同编号："+contract.get("LEASE_CODE"),PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,3));
	   
	    		tT.addCell(noTopandButton("甲方："+companyName ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
	    		tT.addCell(noTopandButton("地址："+address ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("电话 ：" +telephone,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("传真：" +fax,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("邮政编码：" +postCode,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,4));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("乙方："+contract.get("NAME") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("地址： "+contract.get("LICENCE_ADDRESS") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("电话 ："+contract.get("LINKMAN_TELPHONE") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("传真： "+(contract.get("LINKMAN_FAX")==null?"":contract.get("LINKMAN_FAX")) ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("邮政编码："+contract.get("LINKMAN_ZIP") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,4));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("甲方："+companyName+"（盖章） " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("法定代表人或授权代表：______________（签字）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("签订日期:                 年           月          日    " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("乙方："+contract.get("NAME")+"（盖章）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("法定代表人或授权代表：______________（签字）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("签订日期:                 年           月          日    " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("双方经过友好协商，特订立如下条款，以便明确各方权利义务，共同遵守：" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第一条		合同的前提和范围" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		
	    		Phrase p1 = new Phrase("          甲方与     ", FontNormal); 
	    		p1.add(new Phrase(contract.get("CUST_NAME")+"", FontUN)); 
	    		p1.add(new Phrase("   （下称“承租人”）签订了编号为     ", FontNormal)); 
	    		p1.add(new Phrase(contract.get("LEASE_CODE")+"", FontUN)); 
	    		p1.add(new Phrase("  的融资租", FontNormal)); 
	    		
	    		tT.addCell(noTopandP1(p1,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("赁合同(下称“租赁合同”)，乙方同意就前述租赁合同项下的租赁物(下称“回购标的物”)进行回购。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第二条		回购标的物" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		float[] ws={0.25f,0.3f,0.15f,0.15f,0.15f};
	    		PdfPTable table=new PdfPTable(ws);

	    		table.addCell(makeCellWithLTBBorder("名称",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("厂牌",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("规格及型号",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("机号",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLRTBBorder("单位及数量",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		
	    		int size=eqmts.size();
	    		if(size>=1){
	    			for(int i=0;i<size;i++){
		    			Map eqmt=eqmts.get(i);
		    			Chunk c1=new Chunk(toString(eqmt,"THING_NAME"),FontNormal);
		    			Chunk c2=new Chunk(toString(eqmt,"TYPE_NAME"),FontNormal);
		    			Chunk c3=new Chunk(toString(eqmt,"MODEL_SPEC"),FontNormal);
		    			Chunk c4=new Chunk(toString(eqmt,"THING_NUMBER"),FontNormal);
		    			Chunk c5=new Chunk(toString(eqmt,"AMOUNT")+" "+toString(eqmt,"UNIT"),FontNormal);
			    		table.addCell(makeCellWithLBBorderChunk(c1,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c2,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c3,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c4,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLRBBorderChunk(c5,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
	    			}
//	    			Map eqmt=eqmts.get(size-1);
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NAME").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("BRAND").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("MODEL_SPEC").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NUMBER")==null?" ":eqmt.get("THING_NUMBER").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLRBBorder(eqmt.get("AMOUNT").toString()+" "+eqmt.get("UNIT").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//	    		}else if(size==1){
//	    			Map eqmt=eqmts.get(0);
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NAME").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("BRAND").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("MODEL_SPEC").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NUMBER")==null?" ":eqmt.get("THING_NUMBER").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLRBBorder(eqmt.get("AMOUNT").toString()+" "+eqmt.get("UNIT").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		}else{
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLRBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		}
	    		
	    		PdfPCell cell=new PdfPCell(table);
	    		cell.setBorder(0);
	    		cell.setPaddingLeft(35);
	    		cell.setPaddingRight(35);
	    		cell.setColspan(8);
	    		tT.addCell(cell);

	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第三条		回购条件的成就" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          租赁合同履行期间，承租人任意一期租金逾期超过30日而未支付的，回购条件在逾期之第30" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("日成就。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第四条	 	本合同的生效",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          本合同自当事人双方法定代表人或其授权代表签字并加盖公章后生效。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第五条		权利的行使",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          回购条件成就时，回购开始，甲方可以选择行使以下权利：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          1、要求乙方立即、无条件地按本合同第六条规定价格回购租赁标的物；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          2、直接处分租赁标的物，所得价款低于本合同第六条约定之回购价格的，要求乙方补足差额。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第六条		回购价格",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          回购标的物的价格=承租人全部未支付净租金本金的总额×1.05，其组成参照以下说明：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          1、承租人支付的保证金不能冲抵未支付净租金；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          2、承租人应付的净租金本金总和=租赁物件购买金额-履约保证金 ；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          乙方要求甲方向承租人提起诉讼的，因诉讼产生的全部费用，包括但不限于催收费用、诉讼费、",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("保全费、公告费、执行费、拍卖评估费、律师费、差旅费、财产保全保证金全部由乙方支付。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第七条		回购标的物回收",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("          甲方通知乙方，由乙方负责准备回购所需的全部准备工作；在双方商定具体日期后，由甲方派出",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("法务人员及项目经理，由乙方派出技术人员，现场拆机，清点造单，封箱并指导吊装车，标的物装车",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("完毕，由乙方人员在回购交付单上签字；拆装封箱及运输等全部费用由乙方承担；标的物装车完毕并",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("驶出承租方所在厂区或公司，视为回购交付完毕。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第八条		回购价款的支付",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          乙方承诺在回购交付完毕之日起五（5）个工作日内，将全额回购款支付给甲方。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第九条		所有权及风险的转移",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          1、回购标的物所有权自乙方向甲方缴纳完毕全部回购价款之日起，转移至乙方。同时与回购标",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("的物有关的保险理赔权，及妨碍排除等诉讼权利一并转移至乙方。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          2、回购标的物风险自回购交付完毕之日起由乙方承担。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十条		违约责任",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          若乙方未按照本合同约定实施回购，甲方有权直接处置回购标的物，甲方直接处置回购标的物所",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("得价款低于本合同第六条约定之回购价格的，乙方应承担连带赔偿责任，并补足差额。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十一条	合同效力",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("          1、本合同未尽事宜，当事人双方可另行约定达成书面协议，作为本合同的其它附件。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          2、本合同附件是本合同不可分割的组成部分，与本合同正文具有同等法律效力。",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十二条	争议解决",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     	凡因履行本合同所发生的或与本合同有关的一切争议，甲、乙双方应通过友好协商解决；如果协",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("商不能解决，则向甲方注册所在地有管辖权的法院诉讼解决。甲方为实现本合同项下权利产生的费用",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("（包括但不限于催收费用、诉讼费、保全费、公告费、执行费、拍卖评估费、律师费、差旅费及其它",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("费用）由乙方承担。争议期间，各方仍应继续履行未涉争议的条款。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十三条	其它",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));			 			    			    
			  	tT.addCell(noTopandButton("          本合同一式二份，法律效力相同，双方各执一份。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("   	",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));

			document.add(tT);
	        document.close();
	        //设置文件名
			String fileName=contract.get("LEASE_CODE").toString() + ".pdf";
	        
			HttpServletResponse response=context.getResponse();
			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition","attachment; filename="+fileName);			
			ServletOutputStream os=response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();

		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}

	}
	//导出含灭失的回购合同的模板
	@SuppressWarnings("unchecked")
	public void expPdf2_model(Context context,Map contract,List<Map> eqmts){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try{
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontTitle= new Font(bfChinese,13,Font.BOLD);
			Font FontSTitle = new Font(bfChinese, 8, Font.BOLD);
			Font FontNormal = new Font(bfChinese, 11,Font.NORMAL);
			Font FontUnder = new Font(bfChinese, 11, Font.UNDERLINE);
			
			//页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        PdfWriter.getInstance(document, baos);
	        
	        HeaderFooter footer=new HeaderFooter(new Phrase("页码："), true);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        footer.setBorder(Rectangle.NO_BORDER);
	        document.setFooter(footer);
	        document.open();
	        
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);

		 	    
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				
//			
				tT.addCell(noTopandButton("回购合同",PdfPCell.ALIGN_RIGHT,PdfPCell.ALIGN_MIDDLE,FontTitle,4));
			    Phrase objPhase = new Phrase("（含标的物灭失）", FontSTitle);
			    PdfPCell objCell = new PdfPCell(objPhase);
			    objCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			    objCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			    objCell.setBorder(0);
			    objCell.setColspan(4);
			    tT.addCell(objCell);
			    String companyName = LeaseUtil.getCompanyNameByCompanyCode(1);
				String address = LeaseUtil.getCompanyAddressByCompanyCode(1);
				String postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(1);
				String telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(1);
				String fax = LeaseUtil.getCompanyFaxByCompanyCode(1);
			
				String creditId = null;
				if(context.getContextMap().get("RECT_ID")!=null && !"".equals(context.getContextMap().get("RECT_ID"))){
					creditId = String.valueOf(LeaseUtil.getCreditIdByRectId((String)context.getContextMap().get("RECT_ID")));
				}else{
					creditId = (String) context.getContextMap().get("PRCD_ID");
				}
				int companyCode = LeaseUtil.getCompanyCodeByCreditId(creditId);
				String contractType = LeaseUtil.getContractTypeByCreditId(creditId);
				if("7".equals(contractType)){
				    companyName = LeaseUtil.getCompanyNameByCompanyCode(companyCode);
				    address = LeaseUtil.getCompanyAddressByCompanyCode(companyCode);
					postCode = LeaseUtil.getCompanyPostcodeByCompanyCode(companyCode);
					telephone =  LeaseUtil.getCompanyTelephoneByCompanyCode(companyCode);
					fax = LeaseUtil.getCompanyFaxByCompanyCode(companyCode);
				}
				
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,5));	   
	    		tT.addCell(noTopandButton("合同编号："+contract.get("LEASE_CODE"),PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,3));
	    		   
	    		tT.addCell(noTopandButton("甲方："+companyName ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
	    		tT.addCell(noTopandButton("地址："+address ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("电话 ：" + telephone ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("传真：" + fax ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("邮政编码：" + postCode,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,4));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("乙方："+contract.get("NAME") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("地址： "+contract.get("LICENCE_ADDRESS") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("电话 ："+contract.get("LINKMAN_TELPHONE") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("传真： "+(contract.get("LINKMAN_FAX")==null?"":contract.get("LINKMAN_FAX")) ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,2));
	    		tT.addCell(noTopandButton("邮政编码："+contract.get("LINKMAN_ZIP") ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,4));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("甲方："+companyName+"（盖章） " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("法定代表人或授权代表：______________（签字）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("签订日期:                 年           月          日    " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("乙方："+contract.get("NAME")+"（盖章）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("法定代表人或授权代表：______________（签字）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("签订日期:                 年           月          日    " ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("双方经过友好协商，特订立如下条款，以便明确各方权利义务，共同遵守：" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第一条		合同的前提和范围" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		
	    		//增加承租人名称和编号
	    		Phrase objPhasetemp = new Phrase();
	    		Chunk c = new Chunk("          甲方与   ",FontNormal) ;
	    		objPhasetemp.add(c);
	    		c = new Chunk(contract.get("CUST_NAME") == null || "".equals(contract.get("CUST_NAME").toString())? "__________________":contract.get("CUST_NAME").toString(),FontUnder) ;
	    		objPhasetemp.add(c);
	    		c = new Chunk("  （下称“承租人”）签订了编号为",FontNormal) ;
	    		objPhasetemp.add(c);
	    		c = new Chunk(contract.get("LEASE_CODE") == null || "".equals(contract.get("LEASE_CODE").toString())? "___________":contract.get("LEASE_CODE").toString(),FontUnder) ;
	    		objPhasetemp.add(c);
	    		c = new Chunk("的融资租",FontNormal) ;
	    		objPhasetemp.add(c);
	    		PdfPCell objCelltemp = new PdfPCell(objPhasetemp);
	    		objCelltemp.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    		objCelltemp.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    		objCelltemp.setBorderWidthTop(0);
	    		objCelltemp.setBorderWidthBottom(0);
	    		objCelltemp.setBorderWidthLeft(0);
	    		objCelltemp.setBorderWidthRight(0);
	    		objCelltemp.setColspan(8);
	    		objCelltemp.setPaddingLeft(35);
	    		tT.addCell(objCelltemp) ;
	    		//增加承租人名称和编号   结束
	    		
	    		
//	    		tT.addCell(noTopandButton("          甲方与__________________（下称“承租人”）签订了编号为________ 的融资租" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("赁合同(下称“租赁合同”)，乙方同意就前述租赁合同项下的租赁物(下称“回购标的物”)进行回购。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第二条		回购标的物" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		float[] ws={0.25f,0.3f,0.15f,0.15f,0.15f};
	    		PdfPTable table=new PdfPTable(ws);
	    		table.addCell(makeCellWithLTBBorder("名称",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("厂牌",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("规格及型号",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLTBBorder("机号",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		table.addCell(makeCellWithLRTBBorder("单位及数量",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		
	    		int size=eqmts.size();
	    		if(size>=1){
	    			for(int i=0;i<size;i++){
		    			Map eqmt=eqmts.get(i);
		    			Chunk c1=new Chunk(toString(eqmt,"THING_NAME"),FontNormal);
		    			Chunk c2=new Chunk(toString(eqmt,"TYPE_NAME"),FontNormal);
		    			Chunk c3=new Chunk(toString(eqmt,"MODEL_SPEC"),FontNormal);
		    			Chunk c4=new Chunk(toString(eqmt,"THING_NUMBER"),FontNormal);
		    			Chunk c5=new Chunk(toString(eqmt,"AMOUNT")+" "+toString(eqmt,"UNIT"),FontNormal);
			    		table.addCell(makeCellWithLBBorderChunk(c1,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c2,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c3,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLBBorderChunk(c4,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
			    		table.addCell(makeCellWithLRBBorderChunk(c5,PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE));
	    			}
//	    			Map eqmt=eqmts.get(size-1);
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NAME").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("BRAND").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("MODEL_SPEC").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NUMBER")==null?" ":eqmt.get("THING_NUMBER").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLRBBorder(eqmt.get("AMOUNT").toString()+" "+eqmt.get("UNIT").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//	    		}else if(size==1){
//	    			Map eqmt=eqmts.get(0);
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NAME").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("BRAND").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("MODEL_SPEC").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLBBorder(eqmt.get("THING_NUMBER")==null?" ":eqmt.get("THING_NUMBER").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
//		    		table.addCell(makeCellWithLRBBorder(eqmt.get("AMOUNT").toString()+" "+eqmt.get("UNIT").toString(),PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		}else{
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
		    		table.addCell(makeCellWithLRBBorder("",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE,FontNormal));
	    		}

	    		PdfPCell cell=new PdfPCell(table);
	    		cell.setBorder(0);
	    		cell.setPaddingLeft(35);
	    		cell.setPaddingRight(35);
	    		cell.setColspan(8);
	    		tT.addCell(cell);

	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第三条		回购条件的成就" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          租赁合同履行期间，承租人任意一期租金逾期超过30日而未支付的，回购条件在逾期之第30" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("日成就。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第四条	 	本合同的生效",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          本合同自当事人双方法定代表人或其授权代表签字并加盖公章后生效。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第五条		权利的行使",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          回购条件成就时，回购开始，甲方可以选择行使以下权利：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          1、要求乙方立即、无条件地按本合同第六条规定价格回购租赁标的物；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          2、直接处分租赁标的物，所得价款低于本合同第六条约定之回购价格的，要求乙方补足差额。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("第六条		回购价格",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          回购标的物的价格=承租人全部未支付净租金本金的总额×1.05，其组成参照以下说明：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          1、承租人支付的保证金不能冲抵未支付净租金；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("          2、承租人应付的净租金本金总和=租赁物件购买金额-履约保证金 ；",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          乙方要求甲方向承租人提起诉讼的，因诉讼产生的全部费用，包括但不限于催收费用、诉讼费、",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("保全费、公告费、执行费、拍卖评估费、律师费、差旅费、财产保全保证金全部由乙方支付。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第七条		回购标的物回收",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("          甲方通知乙方，由乙方负责准备回购所需的全部准备工作；在双方商定具体日期后，由甲方派出",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("法务人员及项目经理，由乙方派出技术人员，现场拆机，清点造单，封箱并指导吊装车，标的物装车",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("完毕，由乙方人员在回购交付单上签字；拆装封箱及运输等全部费用由乙方承担；标的物装车完毕并",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("驶出承租方所在厂区或公司，视为回购交付完毕。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第八条		回购价款的支付",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          乙方承诺在回购交付完毕之日起五（5）个工作日内，将全额回购款支付给甲方。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第九条		所有权及风险的转移",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          1、回购标的物所有权自乙方向甲方缴纳完毕全部回购价款之日起，转移至乙方。同时与回购标",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("的物有关的保险理赔权，及妨碍排除等诉讼权利一并转移至乙方。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          2、回购标的物风险自回购交付完毕之日起由乙方承担。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十条		违约责任",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          若乙方未按照本合同约定实施回购，甲方有权直接处置回购标的物，甲方直接处置回购标的物所",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("得价款低于本合同第六条约定之回购价格的，乙方应承担连带赔偿责任，并补足差额。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	
			  	tT.addCell(noTopandButton("第十一条 	标的物灭失",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("          如在回购条件成立之时，标的物已灭失，则乙方仍应按照第六条之规定回购价格支付给甲方，且",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("连带保证人对此仍负有连带责任。",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));

			  	tT.addCell(noTopandButton("第十二条	合同效力",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("          1、本合同未尽事宜，当事人双方可另行约定达成书面协议，作为本合同的其它附件。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("          2、本合同附件是本合同不可分割的组成部分，与本合同正文具有同等法律效力。",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	
			  	tT.addCell(noTopandButton("第十三条	争议解决",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     	凡因履行本合同所发生的或与本合同有关的一切争议，甲、乙双方应通过友好协商解决；如果协",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("商不能解决，则向甲方注册所在地有管辖权的法院诉讼解决。甲方为实现本合同项下权利产生的费用",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("（包括但不限于催收费用、诉讼费、保全费、公告费、执行费、拍卖评估费、律师费、差旅费及其它",PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE,FontNormal,8));
			  	tT.addCell(noTopandButton("费用）由乙方承担。争议期间，各方仍应继续履行未涉争议的条款。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("第十四条	其它",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));			 			    			    
			  	tT.addCell(noTopandButton("          本合同一式二份，法律效力相同，双方各执一份。",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("   	",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
			  	tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));

			document.add(tT);
	        document.close();
	        //设置文件名
	    	String fileName=contract.get("LEASE_CODE").toString();
	        
			HttpServletResponse response=context.getResponse();
			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition","attachment; filename="+fileName);			
			ServletOutputStream os=response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();

		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	//导出确认书的模板
	public void expConfirm_model(Context context,String code,String supl_name,String cust_name){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try{
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontTitle= new Font(bfChinese,13,Font.BOLD);
			Font FontNormal = new Font(bfChinese, 11,Font.NORMAL);
			
			//页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        PdfWriter.getInstance(document, baos);
	        
	        HeaderFooter footer=new HeaderFooter(new Phrase("页码："), true);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        footer.setBorder(Rectangle.NO_BORDER);
	        document.setFooter(footer);
	        document.open();
	        
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);

		 	    
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				
				PdfPCell cell1=noTopandButton("确认书",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontTitle,8);
				cell1.setPaddingRight(35);
				tT.addCell(cell1);
	    		
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	
	    		tT.addCell(noTopandButton("						承租人在此确认：" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
	    		tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						1.承租人基于"+Constants.COMPANY_NAME+"（“裕融”）的授权及委托，购买了合",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						同号为［"+code+"］租赁合同和委托购买合同项下的所有设备（“设备”）。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						2.由于承租人用于支付设备价格的款项是由裕融提供的，所以设备自" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
//	    		tT.addCell(noTopandButton("						["+supl_name+"]（“卖方”）交货并经承租人验收合格后即成为裕" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
//	    		tT.addCell(noTopandButton("						融之财产，裕融享有设备的所有权及其他利益。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("						["+supl_name+"]（“卖方”）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						交货并经承租人验收合格后即成为裕融之财产，裕融享有设备的所有权及其他利益。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						3.即使供应商开具的发票抬头为承租人，也仅可证明承租人根据租赁合同和委托" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						购买合同向供应商支付了相应设备价格，在任何情况下不应视为承租人享有设备" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						所有权的证据。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						4.承租人同意在租赁期间将发票交由裕融保管。经裕融事先同意，承租人可以为" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						其自身目的使用该发票。但使用后，承租人应立即向裕融归还发票;承租人未实时" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						返还者,裕融得视为违约并得保留终止合同之权利。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						5.承租人在租赁期间，因各种原因被有关部门处罚所导致的经济损失，均由承租" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						人自行承担。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		
	    		tT.addCell(noTopandButton("						承租人："+cust_name,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						签署：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						日期：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));

			document.add(tT);
	        document.close();
	        //设置文件名
	        
			HttpServletResponse response=context.getResponse();
			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition","attachment;");			
			ServletOutputStream os=response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();

			//插入系统日志 add by ShenQi
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
			   		 "导出 确认书",
		   		 	 "合同浏览导出 确认书",
		   		 	 null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
		   		 	 1,
		   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
		   		 	 DataUtil.longUtil(0),
		   		 	 context.getRequest().getRemoteAddr());
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	//Add by Michael 2012 07-25 导出时带出所有供应商信息
	public void expConfirm_model(Context context,List info){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try{
			// 字体设置
			BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
			Font FontTitle= new Font(bfChinese,13,Font.BOLD);
			Font FontNormal = new Font(bfChinese, 11,Font.NORMAL);
			
			//页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        Document document = new Document(rectPageSize, 20, 20, 20, 20); // 其余4个参数，设置了页面的4个边距
	        
	        PdfWriter.getInstance(document, baos);
	        
	        HeaderFooter footer=new HeaderFooter(new Phrase("页码："), true);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        footer.setBorder(Rectangle.NO_BORDER);
	        document.setFooter(footer);
	        document.open();
	        
			PdfPTable tT = new PdfPTable(8);
			tT.setWidthPercentage(100f);
//add by Michael 2012 07-25 增加多个供应商显示
    		List suplName = new ArrayList() ;
    		String cust_name="";
    		String code="";
    		for(int k=0;k<info.size();k++){
    			if(k==0){
	    			if(((HashMap)info.get(k)).get("CUST_NAME")==null){
	    				cust_name="";
	    			}else{
	    				cust_name=String.valueOf(((HashMap)info.get(k)).get("CUST_NAME"));
	    			}
	    			if(((HashMap)info.get(k)).get("LEASE_CODE")==null){
	    				code="";
	    			}else{
	    				code=String.valueOf(((HashMap)info.get(k)).get("LEASE_CODE"));
	    			}
    			}
    			suplName.add(String.valueOf(((HashMap)info.get(k)).get("NAME")));
    		}
			
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
				
				PdfPCell cell1=noTopandButton("确认书",PdfPCell.ALIGN_CENTER,PdfPCell.ALIGN_MIDDLE, FontTitle,8);
				cell1.setPaddingRight(35);
				tT.addCell(cell1);
	    		
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
				tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	
	    		tT.addCell(noTopandButton("						承租人在此确认：" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));	   
	    		tT.addCell(noTopandButton("    ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						1.承租人基于"+Constants.COMPANY_NAME+"（“裕融”）的授权及委托，购买了合",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						同号为［"+code+"］租赁合同和委托购买合同项下的所有设备（“设备”）。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						2.由于承租人用于支付设备价格的款项是由裕融提供的，所以设备自" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
//	    		tT.addCell(noTopandButton("						["+supl_name+"]（“卖方”）交货并经承租人验收合格后即成为裕" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
//	    		tT.addCell(noTopandButton("						融之财产，裕融享有设备的所有权及其他利益。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		for(int j = 0;j<suplName.size();j++){
	    			tT.addCell(noTopandButton("						["+suplName.get(j).toString()+"]（“卖方”）" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		}
	    		
	    		tT.addCell(noTopandButton("						交货并经承租人验收合格后即成为裕融之财产，裕融享有设备的所有权及其他利益。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						3.即使供应商开具的发票抬头为承租人，也仅可证明承租人根据租赁合同和委托" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						购买合同向供应商支付了相应设备价格，在任何情况下不应视为承租人享有设备" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						所有权的证据。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						4.承租人同意在租赁期间将发票交由裕融保管。经裕融事先同意，承租人可以为" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						其自身目的使用该发票。但使用后，承租人应立即向裕融归还发票;承租人未实时" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						返还者,裕融得视为违约并得保留终止合同之权利。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						5.承租人在租赁期间，因各种原因被有关部门处罚所导致的经济损失，均由承租" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						人自行承担。" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("    	" ,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		
	    		
	    		tT.addCell(noTopandButton("						承租人："+cust_name,PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("		",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						签署：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("     ",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));
	    		tT.addCell(noTopandButton("						日期：",PdfPCell.ALIGN_LEFT,PdfPCell.ALIGN_MIDDLE, FontNormal,8));

			document.add(tT);
	        document.close();
	        //设置文件名
	        
			HttpServletResponse response=context.getResponse();
			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition","attachment;");			
			ServletOutputStream os=response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();

			//插入系统日志 add by ShenQi
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("PRCD_ID")),null,
			   		 "导出 确认书",
		   		 	 "合同浏览导出 确认书",
		   		 	 null,
		   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")在合同管理的合同浏览使用导出合同功能",
		   		 	 1,
		   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
		   		 	 DataUtil.longUtil(0),
		   		 	 context.getRequest().getRemoteAddr());
		}catch(Exception e){
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/** 创建 只有左上下边框 单元格 */
	private static PdfPCell makeCellWithLTBBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	/** 创建 只有左右上下边框 单元格 */
	private static PdfPCell makeCellWithLRTBBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0.5f);
	    return objCell;
	}
	
	/** 创建 只有左右下边框 单元格 */
	private static PdfPCell makeCellWithLRBBorder(String content, int alignh,int alignv, Font FontDefault) {
	    Phrase objPhase = new Phrase(content, FontDefault);
	    PdfPCell objCell = new PdfPCell(objPhase);
	    objCell.setFixedHeight(20);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);	    
	    return objCell;
	}
	/** 创建 只有左右下边框 单元格 */
	private static PdfPCell makeCellWithLRBBorderChunk(Chunk chunk, int alignh,int alignv) {
	    PdfPCell objCell = new PdfPCell();
	    objCell.addElement(chunk);
	    objCell.setPaddingTop(0);
	    objCell.setHorizontalAlignment(alignh);
	    objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0.5f);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);	    
	    return objCell;
	}
	/** 创建 只有左下边框 合并 单元格 */
	private static PdfPCell makeCellWithLBBorder(String content, int alignh,int alignv, Font FontDefault) {
		Phrase objPhase = new Phrase(content, FontDefault);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setFixedHeight(20);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);		
	    return objCell;
	}
	/** 创建 只有左下边框 合并 单元格 */
	private static PdfPCell makeCellWithLBBorderChunk(Chunk chunk, int alignh,int alignv) {
		PdfPCell objCell = new PdfPCell();
		objCell.addElement(chunk);
		objCell.setPaddingTop(0);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
	    objCell.setBorderWidthLeft(0.5f);
	    objCell.setBorderWidthRight(0);
	    objCell.setBorderWidthBottom(0.5f);
	    objCell.setBorderWidthTop(0);		
	    return objCell;
	}

	
	//////////////////////////////
	/** 创建无上下边框 合并 单元格 */
	private static PdfPCell noTopandButton(String content, int alignh,int alignv, Font FontNormal,int colspan) {
		Phrase objPhase = new Phrase(content, FontNormal);
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthLeft(0);
		objCell.setBorderWidthRight(0);
		objCell.setColspan(colspan);
		objCell.setPaddingLeft(35);
	    return objCell;
	}
	/**
	 * 插入短句 为了多个字体使用
	 * @param phrase
	 * @param alignh
	 * @param alignv
	 * @param FontNormal
	 * @param colspan
	 * @return
	 */
	private static PdfPCell noTopandP1(Phrase phrase ,int alignh,int alignv, Font FontNormal,int colspan) {
		Phrase objPhase = phrase ;
		PdfPCell objCell = new PdfPCell(objPhase);
		objCell.setHorizontalAlignment(alignh);
		objCell.setVerticalAlignment(alignv);
		objCell.setBorderWidthTop(0);
		objCell.setBorderWidthBottom(0);
		objCell.setBorderWidthLeft(0);
		objCell.setBorderWidthRight(0);
		objCell.setColspan(colspan);
		objCell.setPaddingLeft(35);
		return objCell;
	}
	
}
