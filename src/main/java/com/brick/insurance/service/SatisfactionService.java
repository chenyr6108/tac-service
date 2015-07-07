package com.brick.insurance.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.service.BaseService.ORDER_TYPE;
import com.brick.base.to.PagingInfo;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.log.service.LogPrint;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.FileUpload;
import com.brick.util.StringUtils;

/**
 * 理赔service
 * @author Administrator
 *
 */
public class SatisfactionService  extends BaseCommand{

	public static final Logger log = Logger.getLogger(SatisfactionService.class);
	Log logger = LogFactory.getLog(SatisfactionService.class);
	
	private MailUtilService mailUtil;
	
	public MailUtilService getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtilService mailUtil) {
		this.mailUtil = mailUtil;
	}
	
	/*
	 * @see com.brick.service.core.AService#afterExecute(java.lang.String,
	 *      com.brick.service.entity.Context)
	 */
	@Override
	protected void afterExecute(String action, Context context) {

	}

	/*
	 * @see com.brick.service.core.AService#preExecute(java.lang.String,
	 *      com.brick.service.entity.Context)
	 */
	@Override
	protected boolean preExecute(String action, Context context) {
		return super.preExecute(action, context);
	}
	/**
	 * 查看理赔信息
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void queryAll(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		PagingInfo<Object> dw = null;
		boolean satisfaction_edit = false;
		Map rsMap = null;
		Map paramMap = new HashMap();
		if (errList.isEmpty()) {
			try {
				paramMap.put("id", context.contextMap.get("s_employeeId"));
				rsMap = (Map) baseService.queryForObj("employee.getEmpInforById", paramMap);
				context.contextMap.put("p_usernode", rsMap.get("NODE"));
				dw = baseService.queryForListWithPaging("satisfaction.querySatisfaction", context.contextMap, "CREATE_DATE", ORDER_TYPE.DESC);
				satisfaction_edit = baseService.checkAccessForResource("satisfaction_edit", context.contextMap.get("s_employeeId").toString());
			} catch (Exception e) {
				errList.add("理赔信息管理页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("satisfaction_edit", satisfaction_edit);
			outputMap.put("DANG_DATE", context.contextMap.get("DANG_DATE"));
			outputMap.put("INSF_DATE", context.contextMap.get("INSF_DATE"));
			outputMap.put("content", context.contextMap.get("content"));
			
			Output.jspOutput(outputMap, context,"/insurance/insuSatisfaction.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/**
	 * 查看保单信息
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void insuManage(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		DataWrap dw = null;
		
		if (errList.isEmpty()) {
			try {
				dw = (DataWrap) DataAccessor.query("satisfaction.queryInsuList",
						context.contextMap, DataAccessor.RS_TYPE.PAGED);
			} catch (Exception e) {
				errList.add("保单信息管理页 : " + e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		if (errList.isEmpty()) {
			outputMap.put("dw", dw);
			outputMap.put("AFFIRM_INSU_DATE", context.contextMap.get("AFFIRM_INSU_DATE"));
			outputMap.put("PRINT_INSU_DATE", context.contextMap.get("PRINT_INSU_DATE"));
			outputMap.put("content", context.contextMap.get("content"));
			Output.jspOutput(outputMap, context,
							"/insurance/insuList.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}
	/**
	 * 创建添加理赔信息页
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void CreatePre(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		if (errList.isEmpty()) {
			try{
				
			List eqmts=(List)DataAccessor.query("satisfaction.queryEqmtByIncuId",context.getContextMap(),DataAccessor.RS_TYPE.LIST);
			
			outputMap.put("eqmts", eqmts);
			outputMap.put("INCU_ID", context.contextMap.get("INCU_ID"));
			Output.jspOutput(outputMap, context,"/insurance/addSatisfaction.jsp");
			}catch(Exception e){
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}

	}	
	/**
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void addSatisfaction(Context context) {
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		try {
			DataAccessor.execute("satisfaction.createSatisfaction", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
		
			errList.add("添加理赔信息失败！\n"+e.toString());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		
		}
		if (errList.isEmpty()) {
			/*2011/12/12 Yang Yun Merger "理赔" for add function. Start*/
			//Output.jspSendRedirect(context,"defaultDispatcher?__action=satisfaction.queryAll");
			Output.jspSendRedirect(context,"defaultDispatcher?__action=insuranceList.queryAll");
			/*2011/12/12 Yang Yun Merger "理赔" for add function. End*/
			
		} else {
			
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
			
		}
	}
	/**
	 * 根据ID获取一条理赔信息，查看使用
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getSatisfaction(Context context){
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map satisfaction=null;
		
		try {
		
			satisfaction=(Map)DataAccessor.query("satisfaction.readSatisfaction", context.contextMap, DataAccessor.RS_TYPE.MAP);
		
		} catch (Exception e) { 
			errList.add("读取理赔信息错误!" + e.toString());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		
		if (errList.isEmpty()) {
			outputMap.put("satisfaction", satisfaction);
			Output.jsonOutput(outputMap, context);
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	/**
	 * 根据ID获取一条理赔信息，跟新使用
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void updateSatisfaction(Context context){
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		Map satisfaction=null;
		List eqmts=null;
		try {
			satisfaction=(Map)DataAccessor.query("satisfaction.readSatisfaction", context.contextMap, DataAccessor.RS_TYPE.MAP);
		} catch (Exception e) { 
			errList.add("读取理赔信息错误!" + e.toString());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
		
		if (errList.isEmpty()) {
			outputMap.put("eqmts", eqmts);
			outputMap.put("satisfaction", satisfaction);
			Output.jspOutput(outputMap, context, "/insurance/updateSatisfaction.jsp");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	/**
	 * 更新
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void modifySatisfaction(Context context){
		
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		try {
			context.contextMap.put("status", 0);
			DataAccessor.execute("satisfaction.updateSatisfaction", context.contextMap,DataAccessor.OPERATION_TYPE.UPDATE);
			
		} catch (Exception e) { 
			
			errList.add("删除理赔信息错误!"+e.toString());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		
		}
		if (errList.isEmpty()) {
			
			Output.jspSendRedirect(context, "defaultDispatcher?__action=satisfaction.queryAll");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 删除
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void delSatisfaction(Context context){	
		Map contextMap=context.contextMap;
		Map outputMap = new HashMap();
		List errList = context.errList;
		
		try {
			DataAccessor.getSession().startTransaction();
			DataAccessor.getSession().update("satisfaction.updateInsuDel",
					contextMap);
			DataAccessor.getSession().commitTransaction();
			
		} catch (Exception e) { 
			
			errList.add("删除理赔信息错误!"+e.toString());
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		
		}finally{
			try {
				DataAccessor.getSession().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if (errList.isEmpty()) {
			
			Output.jspSendRedirect(context, "defaultDispatcher?__action=satisfaction.queryAll");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 转租金记录添加到到来款表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void toRents(Context context){		
		Map outputMap = new HashMap();
		Map contextMap=context.contextMap;
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();
			    DataAccessor.getSession().insert(
						"satisfaction.createIncome", contextMap);
			    DataAccessor.getSession().update(
						"satisfaction.updateToRents", contextMap);
				DataAccessor.getSession().executeBatch();
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
					Output.jspSendRedirect(context, "defaultDispatcher?__action=satisfaction.querySatisfaction");
				} catch (SQLException e) {
					errList.add(e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		} else {
			outputMap.put("errList", errList);
			
		}
	}
	/**
	 * 转租金记录页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void toRentsCreate(Context context){
		Map contextMap = context.contextMap;
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			outputMap.put("INSF_ID", context.contextMap.get("INSF_ID").toString());
			outputMap.put("BALANCE",context.contextMap.get("BALANCE").toString());
			Output.jspOutput(outputMap, context, "/insurance/toRents.jsp");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 退客户页面
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void toCustomerCreate(Context context){		
		Map outputMap = new HashMap();
		List errList = context.errList;
		if (errList.isEmpty()) {
			outputMap.put("INSF_ID", context.contextMap.get("INSF_ID").toString());
			
			outputMap.put("BALANCE",context.contextMap.get("BALANCE").toString());
			Output.jspOutput(outputMap, context, "/insurance/toCustomer.jsp");

		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	/**
	 * 退客户记录到来款表
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void toCustomer(Context context){		
		Map outputMap = new HashMap();
		Map contextMap=context.contextMap;
		List errList = context.errList;
		if (errList.isEmpty()) {
			try {
				DataAccessor.getSession().startTransaction();
			    DataAccessor.getSession().insert(
						"satisfaction.createIncome", contextMap);
			    DataAccessor.getSession().update(
						"satisfaction.updateToCust", contextMap);
				DataAccessor.getSession().executeBatch();
				DataAccessor.getSession().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			} finally {
				try {
					DataAccessor.getSession().endTransaction();
					Output.jspSendRedirect(context, "defaultDispatcher?__action=satisfaction.querySatisfaction");
				} catch (SQLException e) {
					errList.add(e.getMessage());
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
					errList.add(e);
				}
			}
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	/**
	 * 查询理赔跟踪信息
	 *
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void trackById(Context context) {
	
		Map outputMap = new HashMap();
		List trackList = new ArrayList<Map>();
		boolean satisfaction_edit = false;
		try {
			trackList = DataAccessor.getSession().queryForList(
					"satisfaction.queryTrackById", context.contextMap);
			satisfaction_edit = baseService.checkAccessForResource("satisfaction_edit", context.contextMap.get("s_employeeId").toString());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("INSF_ID", context.contextMap.get("INSF_ID").toString());
		outputMap.put("trackList", trackList);
		outputMap.put("satisfaction_edit", satisfaction_edit);
		outputMap.put("now", DateUtil.dateToString(new Date()));
		Output.jspOutput(outputMap, context, "/insurance/trackInfo.jsp");
	}
	
	/**
	 * 添加理赔跟踪信息
	 *
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void addTrack(Context context) {
		Map outputMap = new HashMap();
		List errList = context.errList;
		System.out.println(context.contextMap.get("RECORD_DATE"));
		System.out.println(context.contextMap.get("RECORD_NAME"));
		System.out.println(context.contextMap.get("RECORD_REMARK"));
		String img_path = null;
		String img_name = (String) context.contextMap.get("new_file_name");
		try {
			//保存文件
			log.info("============保存文件.Start============");
			List fileItems = (List) context.contextMap.get("uploadList");
			if (fileItems.size() == 1) {
				FileItem fileItem = (FileItem) fileItems.get(0);
				String filePath = fileItem.getName();
				if (!filePath.equals("")) {
					try {
						String type = filePath.substring(filePath.lastIndexOf(".") + 1);
						String excelNewName = FileExcelUpload.getNewFileName();
						img_path = File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + File.separator + excelNewName + "." + type;
						String bootPath = null;
						bootPath = FileUpload.getUploadPath("satisfactionImg");
						File realPath = new File(bootPath + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
						if (!realPath.exists()) {
							realPath.mkdirs();
						}
						File uploadedFile = new File(realPath.getPath() + File.separator + excelNewName + "." + type);
						if (bootPath != null) {
							fileItem.write(uploadedFile);
						}
					} catch (Exception e) {
						throw e;
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
			context.contextMap.put("IMG_NAME", img_name);
			context.contextMap.put("IMG_PATH", img_path);
			DataAccessor.execute("satisfaction.createTrack", context.contextMap,DataAccessor.OPERATION_TYPE.INSERT);
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add(e);
		}
	    if(errList.isEmpty()){
			Output.jsonOutput(outputMap, context);
		}else{
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
	}
	
	public void downloadImg(Context context) {
		System.out.println("===========================下载图片咯===============================");
		String savaPath = (String) context.contextMap.get("img_path");
		String name = (String) context.contextMap.get("img_name");
		String bootPath = FileUpload.getUploadPath("satisfactionImg");
		String path = bootPath + savaPath;
		System.out.println(path);
		path.replace("\\", "/");
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output = null;
		FileInputStream fis = null;
		try {
			context.response.setHeader("Content-Disposition",
					"attachment; filename="
							+ new String(name.getBytes("gb2312"), "iso8859-1"));

			output = context.response.getOutputStream();
			fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = fis.read(b)) != -1) {
				output.write(b, 0, i);
			}
			output.write(b, 0, b.length);
			output.flush();
			context.response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogPrint.getLogStackTrace(e, logger);
				}
				output = null;
			}
		}
	}
	
	public void submitSatisfaction(Context context){
		Map outputMap = new HashMap();
		List errList = context.errList;
		String eqmt_name = (String) context.contextMap.get("eqmt_name");
		String DANG_DATE_WIN = (String) context.contextMap.get("DANG_DATE");
		String INSF_DATE_WIN = (String) context.contextMap.get("INSF_DATE");
		String INSF_MONEY_WIN = (String) context.contextMap.get("INSF_MONEY");
		String CHARGE_DATE_WIN = (String) context.contextMap.get("CHARGE_DATE");
		String CHARGE_MONEY_WIN = (String) context.contextMap.get("CHARGE_MONEY");
		String INSF_ID_WIN = (String) context.contextMap.get("INSF_ID");
		String insu_code = (String) context.contextMap.get("insu_code");
		try {
			if(StringUtils.isEmpty(INSF_ID_WIN)){
				throw new ServiceException("数据过期，请刷新。");
			}
			if(StringUtils.isEmpty(eqmt_name)){
				throw new ServiceException("请填写设备名称。");
			}
			if(StringUtils.isEmpty(DANG_DATE_WIN)){
				throw new ServiceException("请填写出险日期。");
			}
			if(StringUtils.isEmpty(INSF_DATE_WIN)){
				throw new ServiceException("请填写理赔日期。");
			}
			if(StringUtils.isEmpty(INSF_MONEY_WIN)){
				throw new ServiceException("请填写理赔金额。");
			}
			if(StringUtils.isEmpty(CHARGE_DATE_WIN)){
				throw new ServiceException("请填写定损日期。");
			}
			if(StringUtils.isEmpty(CHARGE_MONEY_WIN)){
				throw new ServiceException("请填写定损金额。");
			}
			context.contextMap.put("status", 1);
			baseService.update("satisfaction.updateSatisfaction", context.contextMap);
			MailSettingTo mailInfo = new MailSettingTo();
			String mailContent = "保单号为【" + (insu_code == null ? "" : insu_code) + "】，" +
					"设备名称为【" + (eqmt_name == null ? "未填写" : eqmt_name) + "】，" +
					"理赔金额为【" + (INSF_MONEY_WIN == null ? "未填写" : INSF_MONEY_WIN) + "】，" +
					"理赔完成，转交业管。";
			mailInfo.setEmailContent(mailContent);
			mailUtil.sendMail(102, mailInfo);
		} catch (ServiceException e) {
			logger.error(e);
			errList.add(e);
		} catch (Exception e) {
			logger.error(e);
			errList.add(e);
		}
		
		if (errList.isEmpty()) {
			Output.jspSendRedirect(context, "defaultDispatcher?__action=satisfaction.queryAll");
		} else {
			outputMap.put("errList", errList);
			Output.jspOutput(outputMap, context, "/error.jsp");
		}
		
	}
	
}