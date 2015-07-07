package com.brick.aprv.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import net.sf.json.JSONArray;

import com.brick.aprv.filter.ApprovalFilter;
import com.brick.aprv.service.ApprovalService;
import com.brick.aprv.to.ApprovalTo;
import com.brick.base.command.BaseCommand;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.base.util.LeaseUtil;
import com.brick.baseManage.service.BusinessLog;
import com.brick.bpm.DeptTo;
import com.brick.bpm.ins.Task;
import com.brick.bpm.service.TaskService;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.log.service.LogPrint;
import com.brick.modifyOrder.command.ModifyOrderCommand;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.signOrder.PdfPageNumerEventHelper;
import com.brick.signOrder.command.SignOrderCommand;
import com.brick.signOrder.to.SignOrderLogTo;
import com.brick.signOrder.to.SignOrderTo;
import com.brick.util.DataUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.ibatis.common.resources.Resources;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ApprovalCommand extends BaseCommand {
	
	Log logger = LogFactory.getLog(ModifyOrderCommand.class);
	
	public static final String DEPT_CODE = "BPM会签部门";
	
	private ApprovalService approvalService;
	
	private TaskService bpmTaskService;
	
	@SuppressWarnings("unchecked")
	public void selectApproval(Context context) {
		
		boolean selectApproval_selectAll = baseService.checkAccessForResource("selectApproval_selectAll", String.valueOf(context.contextMap.get("s_employeeId")));
		if(context.getContextMap().get("selfOnly")==null || context.getContextMap().get("selfOnly").toString().isEmpty()){
			context.getContextMap().put("selfOnly", "2");
		}
		if(context.getContextMap().get("companyCode")==null || context.getContextMap().get("companyCode").toString().isEmpty()){
			context.getContextMap().put("companyCode", "");
		}
		PagingInfo<Object> dw = approvalService.queryForListWithPaging("approval.selectApprovalWithPaging", context.contextMap, "aprvId", ORDER_TYPE.DESC);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("dw", dw);
		outputMap.put("flowStatus", context.getContextMap().get("flowStatus"));
		outputMap.put("searchContent", context.getContextMap().get("searchContent"));
		outputMap.put("selfOnly", context.getContextMap().get("selfOnly"));
		outputMap.put("selfApply", context.getContextMap().get("selfApply"));
		outputMap.put("companyCode", context.getContextMap().get("companyCode"));
		outputMap.put("selectApproval_selectAll", selectApproval_selectAll);
		Output.jspOutput(outputMap, context, "/aprv/SelectApproval.jsp");
	}
	
	public void insertApproval(Context context) {
		this.uploadFiles(context);
		ApprovalTo approval = new ApprovalTo();
		String prjtId = null;
		try {
			prjtId = LeaseUtil.getCreditIdByLeaseCode(context.getContextMap().get("leaseCode").toString().trim());
		} catch (SQLException e) {
			prjtId = null;
		}
		if(prjtId == null || prjtId.isEmpty()) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("leaseCode", context.getContextMap().get("leaseCode"));
			outputMap.put("companyCode", context.getContextMap().get("companyCode"));
			outputMap.put("summary", context.getContextMap().get("summary"));
			outputMap.put("content", context.getContextMap().get("content"));
			outputMap.put("s_employeeId", context.getContextMap().get("s_employeeId"));
			outputMap.put("auditData", context.getContextMap().get("auditData"));
			outputMap.put("errorMsg", "Y");
			Output.jspOutput(outputMap, context, "/aprv/insertApproval.jsp");
		} else {
			approval.setRectId(Integer.valueOf(prjtId));
			approval.setLeaseCode(context.getContextMap().get("leaseCode").toString().trim());
			approval.setCompanyCode(Integer.valueOf(context.getContextMap().get("companyCode").toString()));
			approval.setSummary(context.getContextMap().get("summary").toString());
			approval.setContent(context.getContextMap().get("content").toString());
			approval.setApplyUserId(Integer.valueOf(context.getContextMap().get("s_employeeId").toString()));
			approval.setAuditData(context.getContextMap().get("auditData").toString());
			approval.setCreateTime(new Date());
			context.getContextMap().put("aprvId", approvalService.insertApproval(approval));
			this.relateFiles(context);
			this.viewApproval(context);
		}
		
	}
	
	public void viewApproval(Context context) {
		boolean bpm_admin = baseService.checkAccessForResource("bpm_admin", String.valueOf(context.contextMap.get("s_employeeId")));
		Integer aprvId = Integer.valueOf(context.getContextMap().get("aprvId").toString());
		ApprovalTo approval = approvalService.selectApproval(aprvId);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputMap.put("approval", approval);
		
		List<DeptTo> deptList = new ArrayList<DeptTo>();
		
		if(approval.getAuditData() != null && !approval.getAuditData().isEmpty()) {
			JSONArray array = JSONArray.fromObject(approval.getAuditData());
			deptList = JSONArray.toList(array,DeptTo.class);
		}
		
		List<Task> tasks = new ArrayList<Task>();
		List<Task> allTasks = new ArrayList<Task>();
		if(approval.getProcessId()!=null) {
			tasks = bpmTaskService.findCompleteTask(approval.getProcessId());
			allTasks = bpmTaskService.findAllTask(approval.getProcessId());
		}
		if(approval.getHisProcess()!=null && !approval.getHisProcess().isEmpty()) {
			outputMap.put("hisProcess",approval.getHisProcess().split(","));
		}
		if(approval.getUpUser()!=null) {
			outputMap.put("upUser", approval.getUpUser());
		} else {
			outputMap.put("upUser", "");
		}
		if(approval.getRiskUser()!=null) {
			outputMap.put("riskUser", approval.getRiskUser().split(","));
		} else {
			outputMap.put("riskUser", new String[0]);
		}
		outputMap.put("bpm_admin", bpm_admin);
		outputMap.put("tasks",tasks);
		outputMap.put("allTasks",allTasks);
		outputMap.put("auditData",deptList);
		outputMap.put("deptList",baseService.getDataDictionaryByType(DEPT_CODE));
		
		this.getFiles(context);
		outputMap.put("fileList", context.getContextMap().get("fileList"));
		
		Output.jspOutput(outputMap, context, "/aprv/ViewApproval.jsp");
	}
	
	public void updateApproval(Context context) {
		Integer aprvId = Integer.valueOf(context.getContextMap().get("aprvId").toString());
		ApprovalTo approval = approvalService.selectApproval(aprvId);
		
		String auditData = null;
		String summary = null;
		String content = null;
		if(context.getContextMap().get("auditData") != null) {
			auditData = context.getContextMap().get("auditData").toString();
			approval.setAuditData(auditData);
		}
		if(context.getContextMap().get("summary") != null) {
			summary = context.getContextMap().get("summary").toString();
			approval.setSummary(summary);
		}
		if(context.getContextMap().get("content") != null) {
			content = context.getContextMap().get("content").toString();
			approval.setContent(content);
		}
		try {
			approvalService.updateApproval(approval);
			this.viewApproval(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void submitApproval(Context context) {
		Integer aprvId = Integer.valueOf(context.getContextMap().get("aprvId").toString());
		try {
			approvalService.submitApproval(aprvId);
			this.viewApproval(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void insertAndSubmitApproval(Context context) {
		this.uploadFiles(context);
		ApprovalTo approval = new ApprovalTo();
		String prjtId = null;
		try {
			prjtId = LeaseUtil.getCreditIdByLeaseCode(context.getContextMap().get("leaseCode").toString().trim());
		} catch (SQLException e) {
			prjtId = null;
		}
		if(prjtId == null || prjtId.isEmpty()) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("leaseCode", context.getContextMap().get("leaseCode"));
			outputMap.put("companyCode", context.getContextMap().get("companyCode"));
			outputMap.put("summary", context.getContextMap().get("summary"));
			outputMap.put("content", context.getContextMap().get("content"));
			outputMap.put("s_employeeId", context.getContextMap().get("s_employeeId"));
			outputMap.put("auditData", context.getContextMap().get("auditData"));
			outputMap.put("errorMsg", "Y");
			Output.jspOutput(outputMap, context, "/aprv/insertApproval.jsp");
		} else {
			approval.setRectId(Integer.valueOf(prjtId));
			approval.setLeaseCode(context.getContextMap().get("leaseCode").toString().trim());
			approval.setCompanyCode(Integer.valueOf(context.getContextMap().get("companyCode").toString()));
			approval.setSummary(context.getContextMap().get("summary").toString());
			approval.setContent(context.getContextMap().get("content").toString());
			approval.setApplyUserId(Integer.valueOf(context.getContextMap().get("s_employeeId").toString()));
			approval.setAuditData(context.getContextMap().get("auditData").toString());
			approval.setCreateTime(new Date());
			try {
				context.getContextMap().put("aprvId", approvalService.insertAndSubmitApproval(approval));
				this.relateFiles(context);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.viewApproval(context);
		}
	}
	
	public void resubmitApproval(Context context) {
		Integer aprvId = Integer.valueOf(context.getContextMap().get("aprvId").toString());
		try {
			approvalService.resubmitApproval(aprvId);
			this.viewApproval(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void previewApproval(Context context) {
		ApprovalFilter filter = new ApprovalFilter();
		filter.setLeaseCode(context.getContextMap().get("leaseCode").toString());
		ApprovalTo approval = approvalService.previewApproval(filter);
		
		Map<String, Object> outputMap = new HashMap<String, Object>();
		if (approval == null) {
			outputMap.put("result", "fail");
			Output.jsonOutput(outputMap, context);
		}
		outputMap.put("result", "success");
		outputMap.put("custName", approval.getCustName());
		outputMap.put("payed", approval.getPayed());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(approval.getPayDate()==null) {
			outputMap.put("payDate", "");
		} else {
			outputMap.put("payDate", dateFormat.format(approval.getPayDate()));
		}
		DecimalFormat decformat = new DecimalFormat("##,###");
		if(approval.getPayMoney()==null) {
			outputMap.put("payMoney","");
		} else {
			outputMap.put("payMoney",decformat.format(approval.getPayMoney()));
		}
		
		Output.jsonOutput(outputMap, context);
	}
	
	public void setApprovalService(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}
	
	
	public void setBpmTaskService(TaskService bpmTaskService) {
		this.bpmTaskService = bpmTaskService;
	}

	/**
	 * 上传文件
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void uploadFiles(Context context) {
		List fileItems = (List) context.contextMap.get("uploadList");
		String file_path = "";
		String err = "";
		String ids = "";
		if (fileItems != null && fileItems.size() > 0) {
			FileItem fileItem = null;
			for (int i = 0; i < fileItems.size(); i++) {
				fileItem = (FileItem) fileItems.get(i);
				logger.info("文件大小==========>>" + fileItem.getSize());
				if (fileItem.getSize() > (2*1024*1024)) {
					err = "不好意思，您上传的文件大于2M了。";
				}
			}
			for (int i = 0 ;i < fileItems.size() ;i++ ) {
				fileItem = (FileItem) fileItems.get(i);
				if(!fileItem.getName().equals("")){
					String title = "核准函变更申请附件";
					String filePath = fileItem.getName();
					String type = filePath.substring(filePath.lastIndexOf(".") + 1);
					List errList = context.errList;
					Map contextMap = context.contextMap;
					String xmlPath = "aprvFile";
					
					String path = null;
					try {
						SAXReader reader = new SAXReader();
						Document document = reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
						Element root = document.getRootElement();
						List nodes = root.elements("action");
						for (Iterator it = nodes.iterator(); it.hasNext();) {
							Element element = (Element) it.next();
							Element nameElement = element.element("name");
							String s = nameElement.getText();
							if (xmlPath.equals(s)) {
								Element pathElement = element.element("path");
								path = pathElement.getText();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						LogPrint.getLogStackTrace(e, logger);
					}
					String bootPath = path;
					if (bootPath != null) {
						File realPath = new File(bootPath + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + type);
						if (!realPath.exists()){
							realPath.mkdirs();
						}
						String imageName = FileExcelUpload.getNewFileName();
						File uploadedFile = new File(realPath.getPath() + File.separator + imageName + "." + type);
						file_path = '/' + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + '/' + type + '/'+ imageName + "."+ type;
						try {
							if (errList.isEmpty()) {
								fileItem.write(uploadedFile);
								//增加关联
								contextMap.put("path", file_path);
								contextMap.put("fileName", fileItem.getName().replaceAll(" ", ""));
								contextMap.put("title", title);
								contextMap.put("fileType", "aprv");
								contextMap.put("userId", context.contextMap.get("s_employeeId"));
								contextMap.put("date", new Date());
								int fId = (Integer)DataAccessor.execute("demand.insertDemandFile", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
								if(ids.equals("")){
									ids = Integer.toString(fId);
								} else {
									ids = ids + "," + Integer.toString(fId);
								}
							}
						} catch (Exception e) {
							LogPrint.getLogStackTrace(e, logger);
							errList.add(e);
						} finally {
							try {
								fileItem.getInputStream().close();
							} catch (IOException e) {
								e.printStackTrace();
								LogPrint.getLogStackTrace(e, logger);
								errList.add(e);
							}
							fileItem.delete();
						}
					}
				}
			}
		}
		Map<String,String> outputMap = new HashMap<String,String>();
		context.contextMap.put("ids", ids);
		context.contextMap.put("err", err);
		if(context.contextMap.get("aprvId")!=null && !context.contextMap.get("aprvId").toString().isEmpty()) {
			relateFiles(context);
			this.viewApproval(context);
		}
	}
	
	public void relateFiles(Context context) {
		if (context.contextMap.get("ids") == null || context.contextMap.get("ids").toString().isEmpty()) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("demandIdForFile", context.contextMap.get("aprvId"));
		params.put("files", context.contextMap.get("ids"));
		params.put("fileType", "aprv");
		this.baseService.update("demand.updateDemandFiles", params);
	}
	
	public void deleteFiles(Context context) {
		if (context.contextMap.get("delFiles") == null || context.contextMap.get("delFiles").toString().isEmpty()) {
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("delFiles", context.contextMap.get("delFiles"));
		this.baseService.update("demand.delDemandFiles", params);
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("result", "success");
		Output.jsonOutput(output, context);
	}
	
	public void getFiles(Context context) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("fileType", "aprv");
		params.put("demandId", context.contextMap.get("aprvId"));
		context.getContextMap().put("fileList", this.baseService.queryForList("demand.getFilesByDemandId", params));
	}
	
	public void export(Context context) {
		
		ByteArrayOutputStream baos = null;
		
		ApprovalTo approval = null;
		
		try {
			//查询
			int aprvId = Integer.parseInt(context.contextMap.get("aprvId").toString());
			approval = this.approvalService.selectApproval(aprvId);
			
			List<Task> tasks = bpmTaskService.findCompleteTask(approval.getProcessId());
			
			 // 字体设置
	        BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.EMBEDDED);
	       // Font FontColumn = new Font(bfChinese, 12, Font.BOLD);
	        Font fontTitle = new Font(bfChinese, 22, Font.BOLD);
	        Font fontDefault = new Font(bfChinese, 11, Font.NORMAL);
	        //Font FontDefaultP = new Font(bfChinese, 20, Font.NORMAL);
	        //Font FontUnder = new Font(bfChinese, 12, Font.UNDERLINE);
	        // 数字格式
	        NumberFormat nfFSNum = new DecimalFormat("###,###,###,##0.00");
	        nfFSNum.setGroupingUsed(true);
	        nfFSNum.setMaximumFractionDigits(2);
	        // 页面设置
	        Rectangle rectPageSize = new Rectangle(PageSize.A4); // 定义A4页面大小
	        com.lowagie.text.Document document = new com.lowagie.text.Document(rectPageSize, 0, 0, 0, 30); // 其余4个参数，设置了页面的4个边距
	        
	        
	        baos = new ByteArrayOutputStream();
	        PdfWriter.getInstance(document, baos).setPageEvent(new PdfPageNumerEventHelper("核准函变更申请 " + approval.getAprvCode()));
	        
	        Paragraph headerParagraph = new Paragraph();
	        String image = this.getClass().getResource("/").getPath() + ((approval.getCompanyCode()==1)?"/pdf_title_1.jpg":"/pdf_title_2.jpg");
	        headerParagraph.add(Image.getInstance(image));
	        HeaderFooter header = new HeaderFooter(headerParagraph, false);
	        header.setBorder(0);
	        header.setAlignment(HeaderFooter.ALIGN_CENTER);
	        document.setHeader(header);
	        
	        // 打开文档
	        document.open();
	        
	        PdfPTable tTitle = new PdfPTable(1);
	        tTitle.setWidthPercentage(90);
			
	        String title = (approval.getCompanyCode()==1)?"裕融租赁有限公司\n核准函变更申请":"裕国融资租赁有限公司 \n核准函变更申请";
	        
			PdfPCell objTitle = new PdfPCell(new Phrase(title, fontTitle));
			objTitle.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objTitle.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objTitle.setBorder(0);
			tTitle.addCell(objTitle);
			
			PdfPCell objCode = new PdfPCell(new Phrase("\n编号:" + approval.getAprvCode() + "\n", fontDefault));
			objCode.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			objCode.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCode.setBorder(0);
			tTitle.addCell(objCode);
			
			
			PdfPTable tT = new PdfPTable(4);
			tT.setWidthPercentage(90);
			tT.setWidths(new float[] {0.12f,0.38f,0.12f,0.38f});
			tT.getDefaultCell().setBorder(0);
			
			PdfPCell objCell11 = new PdfPCell(new Phrase("类          型:", fontDefault));
			objCell11.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell11.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell11.setBorder(0);
			tT.addCell(objCell11);
			
			PdfPCell objCell12 = new PdfPCell(new Phrase((approval.getPayed()==1)?"已拨款":"未拨款" , fontDefault));
			objCell12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell12.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell12.setColspan(3);
			objCell12.setBorder(0);
			tT.addCell(objCell12);
			
			PdfPCell objCell21 = new PdfPCell(new Phrase("承   租   人:", fontDefault));
			objCell21.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell21.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell21.setBorder(0);
			tT.addCell(objCell21);
			
			PdfPCell objCell22 = new PdfPCell(new Phrase(approval.getCustName(), fontDefault));
			objCell22.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell22.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell22.setColspan(3);
			objCell22.setBorder(0);
			tT.addCell(objCell22);
			
			PdfPCell objCell31 = new PdfPCell(new Phrase("合同编号:", fontDefault));
			objCell31.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell31.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell31.setBorder(0);
			tT.addCell(objCell31);
			
			PdfPCell objCell32 = new PdfPCell(new Phrase(approval.getLeaseCode(), fontDefault));
			objCell32.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell32.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell32.setBorder(0);
			tT.addCell(objCell32);
			
			PdfPCell objCell33 = new PdfPCell(new Phrase("申请部门:", fontDefault));
			objCell33.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell33.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell33.setBorder(0);
			tT.addCell(objCell33);
			
			PdfPCell objCell34 = new PdfPCell(new Phrase(approval.getDeptName(), fontDefault));
			objCell34.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell34.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell34.setBorder(0);
			tT.addCell(objCell34);
			
			PdfPCell objCell41 = new PdfPCell(new Phrase("核准日期:", fontDefault));
			objCell41.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell41.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell41.setBorder(0);
			tT.addCell(objCell41);
			
			PdfPCell objCell42 = new PdfPCell(new Phrase((approval.getPayDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(approval.getPayDate())), fontDefault));
			objCell42.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell42.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell42.setBorder(0);
			tT.addCell(objCell42);
			
			PdfPCell objCell43 = new PdfPCell(new Phrase("申   请   人:", fontDefault));
			objCell43.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell43.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell43.setBorder(0);
			tT.addCell(objCell43);
			
			PdfPCell objCell44 = new PdfPCell(new Phrase(approval.getApplyUserName(), fontDefault));
			objCell44.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell44.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			objCell44.setBorder(0);
			tT.addCell(objCell44);
			
			PdfPCell objCell51 = new PdfPCell(new Phrase("核准金额:", fontDefault));
			objCell51.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell51.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell51.setBorder(0);
			tT.addCell(objCell51);
			
			PdfPCell objCell52 = new PdfPCell(new Phrase(((approval.getPayMoney()==null)?"":(new DecimalFormat("##,###").format(approval.getPayMoney())+ " 元")), fontDefault));
			objCell52.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell52.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell52.setBorder(0);
			tT.addCell(objCell52);
			
			PdfPCell objCell53 = new PdfPCell(new Phrase("申请日期:", fontDefault));
			objCell53.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell53.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			objCell53.setBorder(0);
			tT.addCell(objCell53);
			
			PdfPCell objCell54 = new PdfPCell(new Phrase((approval.getCreateTime()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(approval.getCreateTime())), fontDefault));
			objCell54.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			objCell54.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			objCell54.setBorder(0);
			tT.addCell(objCell54);
			
			PdfPCell objCell61 = new PdfPCell(new Phrase("申请内容:", fontDefault));
			objCell61.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			objCell61.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			objCell61.setBorder(0);
			tT.addCell(objCell61);
			
			
			
			
			PdfPTable tT1 = new PdfPTable(2);
			tT1.setWidthPercentage(88);
			tT1.setWidths(new float[] {0.50f,0.50f});
			
			PdfPCell cCell11 = new PdfPCell(new Phrase("原核准事项:", fontDefault));
			cCell11.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cCell11.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tT1.addCell(cCell11);
			
			PdfPCell cCell12 = new PdfPCell(new Phrase("拟变更事项:", fontDefault));
			cCell12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cCell12.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			tT1.addCell(cCell12);
			
			PdfPCell cCell21 = new PdfPCell(new Phrase(approval.getSummary(), fontDefault));
			cCell21.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cCell21.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT1.addCell(cCell21);
			
			PdfPCell cCell22 = new PdfPCell(new Phrase(approval.getContent() , fontDefault));
			cCell22.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			cCell22.setVerticalAlignment(PdfPCell.ALIGN_TOP);
			tT1.addCell(cCell22);
			
			PdfPTable tT3 = new PdfPTable(5);
			tT3.setWidthPercentage(88);
			tT3.setWidths(new float[] {0.12f,0.06f,0.62f,0.10f,0.10f});
			
			for (Task task : tasks) {
				
				String flowDefId = task.getFlowDefId();
				if("tosubmit".equals(flowDefId)) {
					flowDefId = "直属主管";
				} else if("exam".equals(flowDefId)) {
					flowDefId = "评审人员";
				} else if("toaduit1".equals(flowDefId)) {
					flowDefId = "会签";
				} else if("examproc".equals(flowDefId)) {
					flowDefId = "审查部会办";
				}  else if("itproc".equals(flowDefId)) {
					flowDefId = "资讯部会办";
				}
				
				PdfPCell tCell10 = new PdfPCell(new Phrase(flowDefId, fontDefault));
				tCell10.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				tCell10.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tT3.addCell(tCell10);
				
				String result = task.getResult();
				if("C".equals(result)) {
					result = "完成";
				} else if("P".equals(result)) {
					result = "通过";
				} else if("A".equals(result)) {
					result = "同意";
				} else if("R".equals(result)) {
					result = "驳回";
				}  else if("I".equals(result)) {
					result = "转发";
				}
				
				PdfPCell tCell11 = new PdfPCell(new Phrase(result, fontDefault));
				tCell11.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				tCell11.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tT3.addCell(tCell11);
				
				PdfPCell tCell12 = new PdfPCell(new Phrase(task.getComment(), fontDefault));
				tCell12.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				tCell12.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tT3.addCell(tCell12);
				
				PdfPCell tCell13 = new PdfPCell(new Phrase(task.getChargeName(), fontDefault));
				tCell13.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				tCell13.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tT3.addCell(tCell13);
				
				PdfPCell tCell14 = new PdfPCell(new Phrase(task.getOperatorName(), fontDefault));
				tCell14.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
				tCell14.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				tT3.addCell(tCell14);
			}
			
			document.add(tTitle);
			document.add(tT);
			document.add(tT1);
			document.add(tT3);
			document.close();
			
			// PDF名字的定义
			String strFileName = "approval.pdf";
			
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
	
			//记录到系统日志中
			BusinessLog.addBusinessLogWithIp(DataUtil.longUtil(context.contextMap.get("signOrderId")),null,
		   		 "导出核准函变更申请",
	   		 	 "导出核准函变更申请",
	   		 	 null,
	   		 	 context.contextMap.get("s_employeeName")+"("+context.contextMap.get("s_employeeId")+")使用导出核准函变更申请功能",
	   		 	 1,
	   		 	 DataUtil.longUtil(context.contextMap.get("s_employeeId").toString()),
	   		 	 DataUtil.longUtil(0),
	   		 	 context.getRequest().getRemoteAddr());
			
		} catch (Exception e) {
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
	
}
