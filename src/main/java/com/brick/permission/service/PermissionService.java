package com.brick.permission.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.service.entity.DataWrap;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.command.BaseCommand;
import com.brick.base.exception.ServiceException;
import com.brick.base.to.BaseTo;
import com.brick.log.service.LogPrint;
import com.brick.log.to.ActionLogTo;
import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 权限管理
 * 
 * @author li shaojie
 * @date Apr 12, 2010
 */

public class PermissionService extends BaseCommand {
	Log logger = LogFactory.getLog(PermissionService.class);

	public static final Logger log = Logger.getLogger(PermissionService.class);

	public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String ROLE_ID = "ROLE_ID";
	public static final String RESOURCE_ID = "RESOURCE_ID";
	/**
	 * 获取所有的资源信息，分页显示
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllResource(Context context) {

		// 查询条件
		String content = null;
		if (context.contextMap.get("content") == null) {
			content = "";
		} else {
			content = (String) context.contextMap.get("content");
			content = content.trim();
		}
		context.contextMap.put("content", content);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("permission.getAllResource",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			log
					.debug("com.brick.permission.service.PermissionService.getAllResource() "
							+ e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		List<Map<String, Object>> secondResources = this
				.getSecondResources(context);
		outputMap.put("secondResources", secondResources);
		outputMap.put("content", content);
		outputMap.put("searchType", context.contextMap.get("searchType"));
		Output.jspOutput(outputMap, context, "/permission/showResources.jsp");
	}
	
	/**
	 * 查询权限信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllResourceForAssign(Context context) {

		// 查询条件
		String username = null;
		String rolename = null;
		String resname = null;
		if (context.contextMap.get("username") == null) {
			username = "";
		} else {
			username = (String) context.contextMap.get("username");
			username = username.trim();
		}
		if (context.contextMap.get("rolename") == null) {
			rolename = "";
		} else {
			rolename = (String) context.contextMap.get("rolename");
			rolename = rolename.trim();
		}
		if (context.contextMap.get("resname") == null) {
			resname = "";
		} else {
			resname = (String) context.contextMap.get("resname");
			resname = resname.trim();
		}
		context.contextMap.put("username", username);
		context.contextMap.put("rolename", rolename);
		context.contextMap.put("resname", resname);
		log.debug("查询条件 ：{username : " + username + "," +
				"rolename : " + rolename + "," +
				"resname : " + resname + "}");
		//排序
		String sortorder = null;
		String ordertype = null;
		if (context.contextMap.get("ordertype") == null) {
			ordertype = "ASC";
		} else {
			ordertype = (String) context.contextMap.get("ordertype");
			ordertype = ordertype.trim();
		}
		if (context.contextMap.get("sortorder") == null) {
			sortorder = "RESNAME";
		} else {
			sortorder = (String) context.contextMap.get("sortorder");
			sortorder = sortorder.trim();
		}
		context.contextMap.put("sortorder", sortorder);
		context.contextMap.put("ordertype", ordertype);
		Map<String, Object> outputMap = new HashMap<String, Object>();
		DataWrap dw = null;
		try {
			dw = (DataWrap) DataAccessor.query("permission.getRes2Rol2User",
					context.contextMap, DataAccessor.RS_TYPE.PAGED);
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.getAllResource() " + e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("dw", dw);
		List<Map<String, Object>> secondResources = this.getSecondResources(context);
		outputMap.put("secondResources", secondResources);
		outputMap.put("username", username);
		outputMap.put("rolename", rolename);
		outputMap.put("resname", resname);
		outputMap.put("sortorder", sortorder);
		outputMap.put("ordertype", ordertype);
		outputMap.put("searchType", context.contextMap.get("searchType"));
		Output.jspOutput(outputMap, context, "/permission/showResourcesForAssign.jsp");
	}
	
	/**
	 * 资源-角色对照
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllRes2Rol(Context context) {

		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> res2Rol = null;
		List<Map<String, Object>> resourceList = null;
		List<Map<String, Object>> rols = null;
		try {
			res2Rol = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRes2Rol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			resourceList = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRes", context.contextMap, DataAccessor.RS_TYPE.LIST);
			rols = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRol", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.getAllResource() " + e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("res2Rol", res2Rol);
		try {
			outputMap.put("MODIFY_DATE", 
					res2Rol != null && res2Rol.size() > 0 ? 
							DateUtil.dateToString(
									(Date) res2Rol.get(0).get("MODIFY_DATE"), 
									"yyyy-MM-dd HH:mm:ss SSS")
							: "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("rols", rols);
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		outputMap.put("resourceList", resourceList);
		Output.jspOutput(outputMap, context, "/permission/showResourcesToRole.jsp");
	}
	
	/**
	 * 导出资源角色对照表
	 */
	public void exportRes2Rol(Context context){
		List<Map<String, Object>> res2Rol = null;
		List<Map<String, Object>> resourceList = null;
		List<Map<String, Object>> rols = null;
		Map<String, String> result = new HashMap<String, String>();
		try {
			res2Rol = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRes2Rol", null, DataAccessor.RS_TYPE.LIST);
			resourceList = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRes", null, DataAccessor.RS_TYPE.LIST);
			rols = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRol", null, DataAccessor.RS_TYPE.LIST);
			for(int i = 0; i < res2Rol.size(); i++){
				result.put(res2Rol.get(i).get("RESOURCE_ID").toString()+"@"+res2Rol.get(i).get("ROLE_ID").toString(), "V");
			}
		    HSSFWorkbook wb = new HSSFWorkbook();
		    HSSFSheet sheet = wb.createSheet("资源角色对照表");
		    HSSFFont font = wb.createFont();
		    font.setFontName("宋体");
		    font.setFontHeightInPoints((short) 10);
		    // 创建标题单元格样式
		    HSSFCellStyle headStyle = wb.createCellStyle();
		    headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		    headStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		    headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    //自动换行结合单元格行高模拟竖排文字
		    headStyle.setWrapText(true);
		    headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    headStyle.setFont(font);
		    //脚时间单元格样式
		    HSSFCellStyle footStyle = wb.createCellStyle();
		    footStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		    footStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    footStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		    footStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		    footStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    HSSFDataFormat format = wb.createDataFormat();  
		    footStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm")); 
		    footStyle.setFont(font);
		    // 创建普通单元格样式
		    HSSFCellStyle commonStyle = wb.createCellStyle();
		    commonStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		    commonStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		    commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setFont(font);
		    //第一行标题行
		    HSSFRow row = sheet.createRow(0);
		    HSSFCell cell = row.createCell(0);
		    cell.setCellStyle(commonStyle);
		    cell.setCellValue("一级菜单");
		    cell = row.createCell(1);
		    cell.setCellStyle(commonStyle);
		    cell.setCellValue("二级菜单");
		    for(int i =0; i<rols.size(); i++){
		    	//列宽仅1个字宽度,配合自动换行
			    sheet.setColumnWidth(i+2, (short)700);
			    //行高设为10字汉字高度
			    row.setHeight((short)2600);
		    	cell = row.createCell(i+2);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(rols.get(i).get("NAME").toString());
		    }
		    //数据行
		    for(int i =0; i<resourceList.size(); i++){
			    sheet.autoSizeColumn((short)0); //调整第一列宽度
			    sheet.autoSizeColumn((short)1); //调整第二列宽度
			    row = sheet.createRow(i+1);
			    //一级菜单
			    cell = row.createCell(0);
			    cell.setCellStyle(commonStyle);
			    cell.setCellValue(resourceList.get(i).get("PARENT_NAME").toString());
			    //二级菜单
			    cell = row.createCell(1);
			    cell.setCellStyle(commonStyle);
			    cell.setCellValue(resourceList.get(i).get("NAME").toString());
			    //数据
			    for(int j = 0; j < rols.size(); j++){
				    cell = row.createCell(j + 2);
				    cell.setCellStyle(commonStyle);
				    String value = result.get(resourceList.get(i).get("ID").toString()+"@"+rols.get(j).get("ID").toString());
				    value = value==null?"":value;
				    cell.setCellValue(value);
			    }
		    }
		    row = sheet.createRow(resourceList.size() + 2);
		    cell = row.createCell(0);
		    cell.setCellValue(new Date());
		    cell.setCellStyle(footStyle);
		    cell = row.createCell(1);
		    cell.setCellStyle(footStyle);
		    sheet.addMergedRegion(new CellRangeAddress(resourceList.size() + 2,resourceList.size() + 2,0,1));
		    sheet.createFreezePane( 0, 1, 0, 1 );  
		    //写回客户端
			context.response.setContentType("application/vnd.ms-excel");
			context.response.setCharacterEncoding("UTF-8");
	        String fileName = "资源角色对照表.xls";
	        fileName = new String(fileName.getBytes("GB2312"), "ISO_8859_1"); 
			context.response.setHeader("Content-Disposition","attachment; filename=" + fileName + "");
		    ServletOutputStream sos = context.response.getOutputStream();
		    wb.write(sos);
		    sos.flush();
		    sos.close();
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.exportRes2Rol() " + e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}
	
	/**
	 * 用户-角色对照
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void getAllUser2Rol(Context context) {

		Map<String, Object> outputMap = new HashMap<String, Object>();
		List<Map<String, Object>> user2Rol = null;
		List<Map<String, Object>> rols = null;
		List<Map<String, Object>> users = null;
		try {
			context.contextMap.put("db", "待补");
			context.contextMap.put("cs", "测试");
			context.contextMap.put("xt", "系统");
			user2Rol = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser2Rol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			rols = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			users = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser", context.contextMap, DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.getAllResource() " + e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
		outputMap.put("user2Rol", user2Rol);
		try {
			outputMap.put("MODIFY_DATE", 
					user2Rol != null && user2Rol.size() > 0 ? 
							DateUtil.dateToString(
									(Date) user2Rol.get(0).get("MODIFY_DATE"), 
									"yyyy-MM-dd HH:mm:ss SSS")
							: "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		outputMap.put("rols", rols);
		outputMap.put("users", users);
		outputMap.put("errorMsg", context.contextMap.get("errorMsg"));
		Output.jspOutput(outputMap, context, "/permission/showUserToRole.jsp");
	}
	
	/**
	 * 导出用户角色对照表
	 */
	public void exportUser2Rol(Context context){
		List<Map<String, Object>> user2Rol = null;
		List<Map<String, Object>> rols = null;
		List<Map<String, Object>> users = null;
		Map<String, String> result = new HashMap<String, String>();
		try {
			context.contextMap.put("db", "待补");
			context.contextMap.put("cs", "测试");
			context.contextMap.put("xt", "系统");
			user2Rol = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser2Rol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			rols = (List<Map<String, Object>>) DataAccessor.query("permission.getAllRol", context.contextMap, DataAccessor.RS_TYPE.LIST);
			users = (List<Map<String, Object>>) DataAccessor.query("permission.getAllUser", context.contextMap, DataAccessor.RS_TYPE.LIST);
			//
			for(int i = 0; i < user2Rol.size(); i++){
				result.put(user2Rol.get(i).get("EMPLOYEE_ID").toString()+"@"+user2Rol.get(i).get("ROLE_ID").toString(), "V");
			}
		    HSSFWorkbook wb = new HSSFWorkbook();
		    HSSFSheet sheet = wb.createSheet("用户角色对照表");
		    HSSFFont font = wb.createFont();
		    font.setFontName("宋体");
		    font.setFontHeightInPoints((short) 10);
		    // 创建标题单元格样式
		    HSSFCellStyle headStyle = wb.createCellStyle();
		    headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
		    headStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		    headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    //自动换行结合单元格行高模拟竖排文字
		    headStyle.setWrapText(true);
		    headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    headStyle.setFont(font);
		    //脚时间单元格样式
		    HSSFCellStyle footStyle = wb.createCellStyle();
		    footStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		    footStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		    footStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		    footStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		    footStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    footStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    HSSFDataFormat format = wb.createDataFormat();  
		    footStyle.setDataFormat(format.getFormat("yyyy-MM-dd hh:mm")); 
		    footStyle.setFont(font);
		    // 创建普通单元格样式
		    HSSFCellStyle commonStyle = wb.createCellStyle();
		    commonStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		    commonStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		    commonStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    commonStyle.setFont(font);
		    //第一行标题行
		    HSSFRow row = sheet.createRow(0);
		    HSSFCell cell = row.createCell(0);
		    cell.setCellStyle(commonStyle);
		    cell.setCellValue("用户名");
		    for(int i =0; i<rols.size(); i++){
		    	//列宽仅1个字宽度,配合自动换行
			    sheet.setColumnWidth(i+1, (short)700);
			    //行高
			    row.setHeight((short)2600);
		    	cell = row.createCell(i+1);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(rols.get(i).get("NAME").toString());
		    }
		    //数据行
		    for(int i = 0; i < users.size(); i++){
			    sheet.autoSizeColumn((short)0); //调整第一列宽度
			    row = sheet.createRow(i+1);
			    //用户名
			    cell = row.createCell(0);
			    cell.setCellStyle(commonStyle);
			    cell.setCellValue(users.get(i).get("NAME").toString());
			    //数据
			    for(int j = 0; j < rols.size(); j++){
				    cell = row.createCell(j + 1);
				    cell.setCellStyle(commonStyle);
				    String value = result.get(users.get(i).get("ID").toString()+"@"+rols.get(j).get("ID").toString());
				    value = value==null?"":value;
				    cell.setCellValue(value);
			    }
		    }
		    row = sheet.createRow(users.size() + 2);
		    cell = row.createCell(0);
		    cell.setCellValue(new Date());
		    cell.setCellStyle(footStyle);
		    //合并单元格数
		    int rows = 4;
		    for(int i = 0; i < rows; i ++){
			    cell = row.createCell(i + 1);
			    cell.setCellStyle(footStyle);
		    }
		    //合并单元格
		    sheet.addMergedRegion(new CellRangeAddress(users.size() + 2, users.size() + 2, 0, rows));
		    //锁定第一行
		    sheet.createFreezePane(0, 1, 0, 1);  
		    //写回客户端
			context.response.setContentType("application/vnd.ms-excel");
			context.response.setCharacterEncoding("UTF-8");
	        String fileName = "用户角色对照表.xls";
	        fileName = new String(fileName.getBytes("GB2312"), "ISO_8859_1"); 
			context.response.setHeader("Content-Disposition","attachment; filename=" + fileName + "");
		    ServletOutputStream sos = context.response.getOutputStream();
		    wb.write(sos);
		    sos.flush();
		    sos.close();
		} catch (Exception e) {
			log.debug("com.brick.permission.service.PermissionService.exportUser2Rol() " + e);
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		}
	}

	/**
	 * 2012/02/23 Yang Yun 
	 * 保存用户-角色
	 * @param context
	 */
	public void updateUserPermission(Context context){
		//获取所有权限
		String[] resource = context.request.getParameterValues("cb_permission");
		String[] userRoleStr = null;
		Map<String, Object> paraMap = null;
		String MODIFY_DATE = (String) context.contextMap.get("MODIFY_DATE");
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			Date selectModifyDate = (Date) baseService.queryForObj("permission.getUser2RolMinModifyDate");
			if (!StringUtils.isEmpty(MODIFY_DATE) && selectModifyDate != null) {
				String selectModifyDateStr = DateUtil.dateToString(selectModifyDate, "yyyy-MM-dd HH:mm:ss SSS");
				if (!selectModifyDateStr.equals(MODIFY_DATE)) {
					throw new Exception("权限已被更新过，请刷新页面，再做修改。");
				}
			}
			
			//查询出所有旧的权限
			List<Map<String, Object>> user2RolOld = (List<Map<String, Object>>) sqlMap.queryForList("permission.getAllUser2Rol");
			List<Map<String, Object>> user2RolNew = new ArrayList<Map<String,Object>>();
			//删除旧权限
			sqlMap.delete("permission.deleteAllUser2Rol", null);
			//解析所有权限明细
			if (resource != null) {
				for (int i = 0; i < resource.length; i++) {
					userRoleStr = resource[i].split("_");
					if (userRoleStr.length == 2) {
						paraMap = new HashMap<String, Object>();
						paraMap.put(EMPLOYEE_ID, userRoleStr[0]);
						paraMap.put(ROLE_ID, userRoleStr[1]);
						//插入权限明细
						sqlMap.insert("permission.addUser2Rol", paraMap);
						user2RolNew.add(paraMap);
					} else {
						logger.warn("数据抓取失败！");
					}
				}
			}
			String diffInfo = getDiffInfo(user2RolOld, user2RolNew, ROLE_ID, EMPLOYEE_ID);
			logger.info(diffInfo);
			ActionLogTo actionLogTo = new ActionLogTo();
			actionLogTo.setLogBy((String) context.contextMap.get("s_employeeName"));
			actionLogTo.setLogAction("修改“人员-角色”对照表");
			actionLogTo.setLogContent(diffInfo);
			actionLogTo.setLogIp(context.contextMap.get("IP").toString());
			baseService.insertActionLog(actionLogTo);
			sqlMap.commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		this.getAllUser2Rol(context);
	}
	
	/**
	 * 2012/02/23 Yang Yun 
	 * 保存资源-角色
	 * @param context
	 */
	public void updateResPermission(Context context){
		//获取所有权限
		String[] resource = context.request.getParameterValues("cb_permission");
		String[] resRoleStr = null;
		Map<String, Object> paraMap = null;
		String MODIFY_DATE = (String) context.contextMap.get("MODIFY_DATE");
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			Date selectModifyDate = (Date) baseService.queryForObj("permission.getRes2RolMinModifyDate");
			if (!StringUtils.isEmpty(MODIFY_DATE) && selectModifyDate != null) {
				String selectModifyDateStr = DateUtil.dateToString(selectModifyDate, "yyyy-MM-dd HH:mm:ss SSS");
				if (!selectModifyDateStr.equals(MODIFY_DATE)) {
					throw new Exception("权限已被更新过，请刷新页面，再做修改。");
				}
			}
			//查询出所有旧的权限
			List<Map<String, Object>> res2RolOld = (List<Map<String, Object>>) sqlMap.queryForList("permission.getAllRes2Rol");
			List<Map<String, Object>> res2RolNew = new ArrayList<Map<String,Object>>();
			
			//删除旧权限
			sqlMap.delete("permission.deleteAllRes2Rol", null);
			//解析所有权限明细
			if (resource != null) {
				for (int i = 0; i < resource.length; i++) {
					resRoleStr = resource[i].split("_");
					if (resRoleStr.length == 2) {
						paraMap = new HashMap<String, Object>();
						paraMap.put(RESOURCE_ID, resRoleStr[0]);
						paraMap.put(ROLE_ID, resRoleStr[1]);
						//插入权限明细
						sqlMap.insert("permission.addRes2Rol", paraMap);
						res2RolNew.add(paraMap);
					} else {
						logger.warn("数据抓取失败！");
					}
				}
			}
			String diffInfo = getDiffInfo(res2RolOld, res2RolNew, ROLE_ID, RESOURCE_ID);
			logger.info(diffInfo);
			ActionLogTo actionLogTo = new ActionLogTo();
			actionLogTo.setLogBy((String) context.contextMap.get("s_employeeName"));
			actionLogTo.setLogAction("修改“资源-角色”对照表");
			actionLogTo.setLogContent(diffInfo);
			actionLogTo.setLogIp(context.contextMap.get("IP").toString());
			baseService.insertActionLog(actionLogTo);
			sqlMap.commitTransaction();
		} catch (Exception e) {
			context.contextMap.put("errorMsg", e.getMessage());
			try {
				sqlMap.endTransaction();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		this.getAllRes2Rol(context);
	}
	
	/**
	 * 将权限对应表，按照角色合并。
	 * ex：
	 * {资讯专员：[杨赟,胡天书,沈祺]}
	 * @param permissionInfos
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<String>> mergerPermissionInfo(List<Map<String, Object>> permissionInfos, String key, String value) throws Exception{
		Map<String, List<String>> permissionInfo = new HashMap<String, List<String>>();
		if (permissionInfos == null) {
			return null;
		}
		List<String> values = null;
		String eId = null;
		String rId = null;
		for (Map<String, Object> map : permissionInfos) {
			eId = map.get(key).toString();
			rId = map.get(value).toString();
			if (permissionInfo.get(eId) == null) {
				values = new ArrayList<String>();
			} else {
				values = permissionInfo.get(eId);
			}
			values.add(rId);
			permissionInfo.put(eId, values);
		}
		return permissionInfo;
	}
	
	/**
	 * 将合并好的 旧权限 和 新权限 做解析
	 * 把不同的地方拼成String
	 * @param fromList
	 * @param toList
	 * @param keyStr
	 * @param valueStr
	 * @return
	 * @throws Exception
	 */
	private String getDiffInfo(List<Map<String, Object>> fromList, List<Map<String, Object>> toList, String keyStr, String valueStr) throws Exception {
		Map<String, List<String>> fromMap = mergerPermissionInfo(fromList, keyStr, valueStr);
		Map<String, List<String>> toMap = mergerPermissionInfo(toList, keyStr, valueStr);
		StringBuffer sb = new StringBuffer();
		fromMap = fromMap == null ? new HashMap<String, List<String>>() : fromMap;
		toMap = toMap == null ? new HashMap<String, List<String>>() : toMap;
		Set<String> fromKeys = fromMap.keySet();
		Set<String> toKeys = toMap.keySet();
		List<String> fromValue = null;
		List<String> toValue = null;
		List<String> sameValue = null;
		for (Iterator iterator = fromKeys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			fromValue = fromMap.get(key);
			toValue = toMap.get(key) == null ? new ArrayList<String>() : toMap.get(key);
			sameValue = new ArrayList<String>();
			for (String string : fromValue) {
				if (toValue.contains(string)) {
					sameValue.add(string);
				}
			}
			for (String string : sameValue) {
				fromValue.remove(string);
				toValue.remove(string);
			}
			if (fromValue.size() == 0 && toValue.size() == 0) {
				toKeys.remove(key);
				continue;
			}
			sb.append("角色(" + getDesc(key, keyStr) + ")：{");
			if (fromValue.size() > 0) {
				sb.append("删除[");
				for (int i = 0; i < fromValue.size(); i ++) {
					if (!toValue.contains(fromValue.get(i))) {
						sb.append("‘" + getDesc(fromValue.get(i), valueStr) + "’");
						sb.append(",");
					} else {
						toValue.remove(fromValue.get(i));
					}
				}
				sb = new StringBuffer(sb.substring(0, sb.length() - 1));
				sb.append("],");
			}
			if (toValue.size() > 0) {
				sb.append("增加[");
				for (int j = 0; j < toValue.size(); j ++) {
					if (!fromValue.contains(toValue.get(j))) {
						sb.append("‘" + getDesc(toValue.get(j), valueStr) + "’");
						sb.append(",");
					}
				}
				sb = new StringBuffer(sb.substring(0, sb.length() - 1));
				sb.append("],");
			}
			sb = new StringBuffer(sb.substring(0, sb.length() - 1));
			sb.append("};");
			toKeys.remove(key);
		}
		if (toKeys.size() > 0) {
			List<String> alladd = null;
			for (Iterator iterator = toKeys.iterator(); iterator.hasNext();) {
				String toKey = (String) iterator.next();
				alladd = toMap.get(toKey);
				sb.append("角色(" + getDesc(toKey, keyStr) + ")：{");
				if (alladd.size() > 0) {
					sb.append("增加[");
					for (int i = 0; i < alladd.size(); i ++) {
						sb.append("‘" + getDesc(alladd.get(i), valueStr) + "’");
						if (i != (alladd.size() - 1)) {
							sb.append(",");
						} else {
							sb.append("],");
						}
					}
				}
				sb = new StringBuffer(sb.substring(0, sb.length() - 1));
				sb.append("};");
			}
		}
		return sb.length() > 0 ? (sb.substring(0, sb.length() - 1)) : "";
	}
	
	private String getDesc(String id, String key) throws ServiceException{
		String result = null;
		String sqlStr = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("select_id", id);
		if (EMPLOYEE_ID.equals(key)) {
			sqlStr = "permission.getEmployee_id";
		} else if (ROLE_ID.equals(key)) {
			sqlStr = "permission.getRole_id";
		} else if (RESOURCE_ID.equals(key)) {
			sqlStr = "permission.getResource_id";
		}
		try {
			result = (String) baseService.queryForObj(sqlStr, paramMap);
		} catch (ServiceException e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 插入一条资源信息
	 * 
	 * @param context
	 */
	@SuppressWarnings("unchecked")
	public void add(Context context) {

		List errList = context.errList;
		Map<String, Object> outputMap = new HashMap<String, Object>();
		String type = (String) context.contextMap.get("type");
		if (type.equals("1")) {
			context.contextMap.put("parent_id", "");
		}
		context.contextMap.put("layout", "/servlet/defaultDispatcher?__action=" + String.valueOf(context.contextMap.get("layout")));
		if (errList.isEmpty()) {
			try {
				@SuppressWarnings("unused")
				Object obj = DataAccessor.execute("permission.create",
						context.contextMap, DataAccessor.OPERATION_TYPE.INSERT);

			} catch (Exception e) {
				errList.add("添加失败！");
				log
						.debug("com.brick.permission.service.PermissionService.add() "
								+ e);
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}

		}

		outputMap.put("errList", errList);
		Output.jspSendRedirect(context, "defaultDispatcher?__action=permission.getAllResource&__currentPage=1");
	}

	/**
	 * 获取所有的一级菜单。
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSecondResources(Context context) {

		List<Map<String, Object>> secondResources = null;
		try {
			secondResources = (List<Map<String, Object>>) DataAccessor.query(
					"permission.getSecondResources", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
		} catch (Exception e) {

			log
					.debug("com.brick.permission.service.PermissionService.getSecondResources() "
							+ e);
			LogPrint.getLogStackTrace(e, logger);
		}

		return secondResources;
	}
	/**
	 * 根据id，作废菜单
	 * @param context
	 */
	public void invalidResource(Context context) {
		SqlMapClient sqlMap = DataAccessor.getSession();
		try {
			sqlMap.startTransaction();
			sqlMap.update("permission.invalid", context.contextMap);
			sqlMap.delete("permission.deleteRes2Rol", context.contextMap);
			baseService.insertActionLog(context, "资源作废", "作废资源：[" + context.contextMap.get("resName") + "],并删除对应的‘资源-角色’权限明细。");
			sqlMap.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
		} finally {
			try {
				sqlMap.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Output.jspSendRedirect(context, "defaultDispatcher?__action=permission.getAllResource&__currentPage=1");
	}
	
	/**
	 * 根据id查找资源
	 * @param context
	 */
	@SuppressWarnings("static-access")
	public void getResourceById(Context context) { 
		this.commonQuery("permission.getResourceById", context, DataAccessor.RS_TYPE.MAP,
				Output.OUTPUT_TYPE.JSON);
	}
	@SuppressWarnings({ "unchecked", "static-access" })
	public void update(Context context) throws Exception{
		 
		String type = (String) context.contextMap.get("type");
		if (type.equals("1")) {
			context.contextMap.put("parent_id", "");
		} 
			this.commonExecute("permission.update", context, DataAccessor.OPERATION_TYPE.UPDATE, Output.OUTPUT_TYPE.JSP);
			Output.jspSendRedirect(context, "defaultDispatcher?__action=permission.getAllResource&__currentPage=1");
	}
}
