package com.brick.decompose.service;

/***
 * @author ranping
 * @version Created：2010-6-17
 * function: Compose操作
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.Output;
import com.brick.service.entity.Context;
import com.brick.util.FileExcelUpload;
import com.ibatis.sqlmap.client.SqlMapClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.coderule.service.CodeRule;
import com.brick.log.service.LogPrint;
import java.text.NumberFormat;

public class ComposeService extends AService {
	Log logger = LogFactory.getLog(ComposeService.class);

	/**
	 * 1.上传页面，在页面对格式进行限制，只能上传后缀为.xls的文件 2.上传解析excel数据，看是否有错 3.在页面中显示excel数据 *
	 * 如果如果上传的数据为空，确定上传按钮为灰色，不允许上传 4.确认提交，如果有错，在有错行进行显示提示 * 确定上传按钮为灰，不允许上传
	 * 5.上传数据保存到指定文件夹中 6.将数据插入到数据库中
	 */


	/**
	 * 批量上传网银来款，解析excel
	 */
	
	@SuppressWarnings("unchecked")
	public void composeUpload(Context context) {
		Map outputMap = new HashMap();			
		List errList = context.errList ;
		Map contextmap = context.getContextMap();
		// 1. 读取 Excel 文件: 得到 Workbook 对象
		Workbook workbook = null;
		// 得到输入流
		InputStream in = (InputStream) contextmap.get("excelInputStream");
		
		if (in == null) {
			// TODO exception
		}

		try {
			workbook = WorkbookFactory.create(in);
			// 2. 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)
			List<Map> composes = buildComposeListFromWorkbook(workbook);
			// 判断数据是否合法
			isValidatorComposesList(context, composes);
			// 5. 响应信息		
			outputMap.put("composes", composes);
			HttpSession session = context.getRequest().getSession();
			HttpServletRequest request = context.getRequest();
			session.setAttribute("remark", context.getContextMap().get("remark"));
			//session.setAttribute("composes", composes);
			request.setAttribute("errorList", context.getErrList());
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("批量上传网银来款，解析excel错误!请联系管理员") ;
		}		
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/decompose/uploadDisplay.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}
	
	
	/**
	 * 批量上传奖金来款，解析excel
	 */

	@SuppressWarnings("unchecked")
	public void composeBonusUpload(Context context) {
		Map outputMap = new HashMap();	
		List errList = context.errList ;
		Map contextmap = context.getContextMap();
		List bonus=null;
		// 1. 读取 Excel 文件: 得到 Workbook 对象
		Workbook workbook = null;
		// 得到输入流
		InputStream in = (InputStream) contextmap.get("excelInputStream");
		
		if (in == null) {
			// TODO exception
		}

		try {
			workbook = WorkbookFactory.create(in);
			
			//查询奖金管理的项
			
				bonus = (List) DataAccessor.query("bonusManage.queryAllBonus", context.contextMap, DataAccessor.RS_TYPE.LIST);
			
			// 2. 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)
			List<Map> composes = buildBonusComposeListFromWorkbook(workbook,bonus);
			// 判断数据是否合法
			isValidatorComposesBonusList(context, composes,bonus);
			// 5. 响应信息		
			outputMap.put("composes", composes);
			HttpSession session = context.getRequest().getSession();
			HttpServletRequest request = context.getRequest();
			session.setAttribute("remark", context.getContextMap().get("remark"));
			//session.setAttribute("composes", composes);
			request.setAttribute("errorList", context.getErrList());
			outputMap.put("bonus", bonus);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("批量上传奖金来款，解析excel错误!请联系管理员") ;
		}						
		if(errList.isEmpty()){
			Output.jspOutput(outputMap, context,"/bonus/uploadBonusDisplay.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	
	/**
     * 判断是否为合法的日期时间字符串
     * @param str_input
     * @return boolean;符合为true,不符合为false
     */
	public static   boolean isDate(String str_input,String rDateFormat){
		if (!str_input.equals("")) {
	        SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
	       // formatter.setLenient(false);
	        try {
	            //formatter.format(formatter.parse(str_input));
	        	formatter.parse(str_input);
	        } catch (Exception e) {
	            return false;
	        }
	        return true;
	    }
		return false;
	}	
	/**
	 * 验证是否合法
	 * 
	 * @param context
	 * @param composes
	 */
	private void isValidatorComposesList(Context context, List<Map> composes) {
		List errorList = context.errList;

		List<Map> mapList = composes;
		if (mapList.isEmpty() || mapList.size() == 0) {
			errorList.add("上传的数据为空");
			return;
		}

		for (int i = 0; i < mapList.size(); i++) {
			Map map = mapList.get(i);
			Set entrySet = map.entrySet();
			Iterator it = entrySet.iterator();
			List<String> eList = new ArrayList<String>();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String str = entry.getKey().toString();
				//System.out.println("str======================"+str+"------------------=");
				// 对所有的进行判断
				if (str.equals("opposing_date")) {
					//System.out.println(entry.getValue());
					Object date = entry.getValue();
					// 如果为空或者不是日期类型就标识
					//if (date == null || !(date instanceof java.sql.Date)) {
					if (date == null) {
						eList.add("日期不能为空");
					}else{
						if(!isDate(date.toString(),"yyyy-MM-dd")){
							eList.add("日期格式不对");
						}
					}
				} else if (str.equals("income_money")) {// 收入金额
					Object income_money =entry.getValue();
					String incom_mon=income_money.toString();
					String income_moneys=income_money.toString();
					for(int j=0;j<incom_mon.length();j++)
					{
						if(incom_mon.charAt(j)>=48 && incom_mon.charAt(j)<=57)
						{
							break;
						}
						else
						{
							income_moneys=income_moneys.replace(incom_mon.charAt(j)+"", "");
						}
					}
					//System.out.println("---------------------------------="+income_money);
					String income=income_moneys.toString().trim().replace("$", "").replace("￥", "").replace(",", "").replace("，", "").replace("?", "").replace("？", "");
					//System.out.println("---------------------------------="+income);
					try {
						double dou = Double.valueOf(income);
						if (dou == 0.0) {
							eList.add("收入金额不能为空");
						}
					} catch (Exception e) {
						eList.add("收入金额格式不对");
						LogPrint.getLogStackTrace(e, logger);
					}

				} else if (str.equals("opposing_type")) {// 交易方式
					String opposing_type = (String) entry.getValue();
					if (StringUtils.isEmpty(opposing_type)) {
						eList.add("交易方式不能为空");
					}
				} else if (str.equals("opposing_unit")) {// 对方户名
					String opposing_unit = (String) entry.getValue();
					if (StringUtils.isEmpty(opposing_unit)) {
						eList.add("对方户名不能为空");
					}
					
				} else if (str.equals("receipt_bankno")) {//收款账号
					String receipt_bankno = (String) entry.getValue();

					if (receipt_bankno == null) {
						eList.add("收款账号不能为空");
					}
				} else if (str.equals("receipt_unit")){//来款单位
					String receipt_unit = (String) entry.getValue();

					if (receipt_unit == null) {
						eList.add("来款单位不能为空");
					}
				}
//				else if (str.equals("opposing_bankno")) {//对方账号
//					String opposing_bankno = (String) entry.getValue();
//
//					if (opposing_bankno == null) {
//						eList.add("对方账号不能为空");
//					}
//				} 
//				else if (str.equals("opposing_bankName")) {
//					String opposing_bankName = (String) entry.getValue();
//					
//					if (opposing_bankName == null) {
//						eList.add("交易行名不能为空");
//					}
//				} 
				// >>>>>>>>>>>>>>>>>>以上为必须存在的字段，不能为空
				else if (str.equals("")) {

				} else if (str.equals("")) {

				} else if (str.equals("")) {

				}
			}
			/** 如果有错,就加入标识，方便在jsp页面显示 */
			if (eList.size() != 0) {
				eList.add("行号为：" + map.get("rowNumber"));
				errorList.add(eList);
				map.put("isError", "1");
				context.getRequest().getSession().removeAttribute("composes");
			} else {
				map.put("isError", "0");
			}
		}
	}
	
	
	/**
	 * 验证是否合法
	 * 
	 * @param context
	 * @param composes
	 */
	private void isValidatorComposesBonusList(Context context, List<Map> composes,List bouns) {
		List errorList = context.errList;

		List<Map> mapList = composes;
		if (mapList.isEmpty() || mapList.size() == 0) {
			errorList.add("上传的数据为空");
			return;
		}

		for (int i = 0; i < mapList.size(); i++) {
			Map map = mapList.get(i);
			Set entrySet = map.entrySet();
			Iterator it = entrySet.iterator();
			List<String> eList = new ArrayList<String>();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String str = entry.getKey().toString().trim();
				// 对所有的进行判断
				//System.out.println("str======================"+str+"------------------=");
				if (str.equals("contract_code")) {// 合同号
					String contract_code = entry.getValue().toString().trim();
					//System.out.println("------------------------------------contract_code="+contract_code);
					if (StringUtils.isEmpty(contract_code)) {
						eList.add("合同号不能为空");
					}
					
				}
				else if (str.equals("name")) {//姓名
					String name =  entry.getValue().toString().trim();
					//System.out.println("------------------------------------name="+name);
					if (StringUtils.isEmpty(name)) {
						eList.add("姓名不能为空");
					}
				}
				else if(str.equals("remark"))
				{
					;
				}
				else{
					for(int j=0;j<bouns.size();j++)
					{
						
						String type="type"+(j+1);
						
						Map bounsMap=(Map)bouns.get(j);
						if (str.equals(type)) {
							Object income_money = entry.getValue();
							String income=null;
							String income_moneys=null;
							if(income_money==null)
							{
								income="0";
								income_moneys="0";
							}
							else
							{
								income=income_money.toString();
								income_moneys=income_money.toString();
							}
							
							for(int h=0;h<income.length();h++)
							{
								if(income.charAt(h)>=48 && income.charAt(h)<=57)
								{
									break;
								}
								else
								{
									income_moneys=income_moneys.replace(income.charAt(h)+"", "");
								}
							}
							try {
								double dou =Double.valueOf(income_moneys.replace("$", "").replace("￥", "").replace(",", "").replace("，", "").trim());
								//System.out.println("---------------------------="+dou);
							} catch (Exception e) {
								eList.add(bounsMap.get("BONUS_NAME")+"格式不对");
								LogPrint.getLogStackTrace(e, logger);
							}

						}
					
					}
				}
				
				
			}
			/** 如果有错,就加入标识，方便在jsp页面显示 */
			if (eList.size() != 0) {
				eList.add("行号为：" + map.get("rowNumber"));
				errorList.add(eList);
				map.put("isError", "1");
				context.getRequest().getSession().removeAttribute("composes");
			} else {
				map.put("isError", "0");
			}
		}
	}

	/**
	 * 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始) 为每一行都常见一个 Compose 对象, 把这些对象放到一个 List
	 * 中
	 * 
	 * @param workbook
	 * @return
	 */
	private List<Map> buildComposeListFromWorkbook(Workbook workbook) {
		
		int num = 0;
		// List<Compose> Composes = new ArrayList<Compose>();
		List<Map> Composes = new ArrayList<Map>();
		// 放置 Excel 一行中的 Compose 相关值, 其顺序为:
		// 当前的行数
		Map values = null;
		if (workbook != null) {
			
			int x = workbook.getNumberOfSheets();
			for(int y=0;y<x;y++){ 
			// 表格
			Sheet sheet = workbook.getSheetAt(y);
			if (sheet != null) {
				Iterator rit = sheet.rowIterator();
				rit.next();
				rit.next();
				// 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)			
				int i = 1 + num;	
				for (; rit.hasNext();) {
					values = new HashMap();					
					values.put("receipt_bankno", null);
					values.put("opposing_date", null);
					values.put("income_money", null);
					values.put("opposing_type", null);
					values.put("opposing_bankno", null);
					values.put("opposing_unit", null);
					values.put("opposing_xuli", null);
					values.put("receipt_unit", null);					
					values.put("opposing_dateTag", null);					
					values.put("payment_money", null);
					values.put("left_money", null);
					values.put("commission_money", null);				
					values.put("opposing_bankName", null);
					values.put("opposing_flag", null);
					values.put("opposing_address", null);
					values.put("opposing_explain", null);
					values.put("opposing_summary", null);
					values.put("opposing_postscript", null);
					values.put("opposing_memo", null);
					
					
					Row row = (Row) rit.next();// 得到行
					Iterator cit = row.cellIterator();// 遍历单元格
					// cit.next();
					for (; cit.hasNext();) {

						Cell cell = (Cell) cit.next();
						String clounmName = "";
						int j = cell.getColumnIndex();
						switch (cell.getColumnIndex()) {
						case 0:
							clounmName = "receipt_bankno";// 必须存在
							break;
						case 1:
							clounmName = "opposing_date";// 必须存在							
							break;
						case 2:
							clounmName = "income_money";// 必须存在
							break;
						case 3:
							clounmName = "opposing_type";// 必须存在							
							break;
						case 4:
							clounmName = "opposing_bankno";	
							break;
						case 5:
							clounmName = "opposing_unit";// 必须存在					
							break;
						case 6:
							clounmName = "opposing_xuli";// 必须存在					
							break;
						case 7:
							clounmName = "receipt_unit";//必须存在
							break;
						case 8:
							clounmName = "opposing_memo";
							break;
						case 9:
							clounmName = "opposing_dateTag";
							break;
						case 10:
							clounmName = "payment_money";
							break;
						case 11:
							clounmName = "left_money";
							break;
						case 12:
							clounmName = "commission_money";
							break;
						case 13:
							clounmName = "opposing_bankName";
							break;
						case 14:
							clounmName = "opposing_flag";
							break;
						case 15:
							clounmName = "opposing_address";
							break;
						case 16:
							clounmName = "opposing_explain";
							break;
						case 17:
							clounmName = "opposing_summary";
							break;
						case 18:
							clounmName = "opposing_postscript";
							break;
						}
						
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							if(clounmName.equals("income_money"))
							{
								String income_moneys= null;
								String incom_mon=null;
								if(cell.getStringCellValue()==null)
								{
									income_moneys="0";
									incom_mon="0";
								}
								else
								{
									income_moneys= cell.getStringCellValue().toString();
									incom_mon=cell.getStringCellValue().toString();
								}
								
								for(int h=0;h<incom_mon.length();h++)
								{
									if(incom_mon.charAt(h)>=48 && incom_mon.charAt(h)<=57)
									{
										break;
									}
									else
									{
										income_moneys=income_moneys.replace(incom_mon.charAt(h)+"", "");
									}
								}
								values.put(clounmName, income_moneys.replace("$", "").replace("￥", "").replace(",", "").replace("，", "").replace("?", "").replace("？", "").trim());
							}
							else if(clounmName.equals("commission_money"))
							{
								values.put(clounmName, cell.getStringCellValue().replace("$", "").replace("￥", "").replace(",", "").replace("，", "").trim());
							}
							else
							{
								values.put(clounmName, cell.getStringCellValue().trim());
							    if(clounmName.equals("opposing_xuli")){
							    	String tempStr= cell.getStringCellValue().trim();
								    	if(!"".equals(tempStr) && tempStr.length()>=18){
											String virtualCode =tempStr.substring(tempStr.length()-18,tempStr.length());
											Map<String, Object> virtualCodemap = new HashMap<String, Object>();
											virtualCodemap.put("opposing_xuli", virtualCode);
									//根据上传的虚拟帐号得到正确的来款人名称
										try {
											List incomeName = (List) DataAccessor.query("uploadComposeExcel.getIncomeNameByVirtualCode",virtualCodemap, DataAccessor.RS_TYPE.LIST);
											if(null!=incomeName && incomeName.size()>0){
												values.put("opposing_unit_true", ((Map) incomeName.get(0)).get("CUST_NAME"));
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
							    	}
								}
							}
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (clounmName.equals("opposing_date")) {
								values.put(clounmName, new Date(cell
										.getDateCellValue().getTime()));
								break;
							}						
							values.put(clounmName, cell.getNumericCellValue());							
						}
					}
					if (!values.isEmpty() && values.size() > 0) {
						Set<Entry> set = values.entrySet();
						int taglib = 0;
						for (Entry entry : set) {
							if (entry.getValue() == null) {
								taglib++;
							}
						}
						if (taglib == 19) {
							break;
						}
						values.put("rowNumber", (i++));
						Composes.add(values);
					}
					// Composes.add(buileComposeFromStringList(values));
				}
				num = Composes.size();
			}
			}
		}
		return Composes;		
	}
	
	
	/**
	 * 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始) 为每一行都常见一个 Compose 对象, 把这些对象放到一个 List
	 * 中
	 * 
	 * @param workbook
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map> buildBonusComposeListFromWorkbook(Workbook workbook,List bouns) {
		
		int num = 0;
		// List<Compose> Composes = new ArrayList<Compose>();
		List<Map> Composes = new ArrayList<Map>();
		// 放置 Excel 一行中的 Compose 相关值, 其顺序为:
		// 当前的行数
		Map values = null;
		if (workbook != null) {
			
			int x = workbook.getNumberOfSheets();
			for(int y=0;y<x;y++){ 
			// 表格
			Sheet sheet = workbook.getSheetAt(y);
			if (sheet != null) {
				Iterator rit = sheet.rowIterator();
				rit.next();
				rit.next();
				// 遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)			
				int i = 1 + num;	
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11clounmName="+2);
				for (; rit.hasNext();) {
					values = new HashMap();	
					
					Row row = (Row) rit.next();// 得到行
					Iterator cit1 = row.cellIterator();// 遍历单元格
					Iterator cit = row.cellIterator();// 遍历单元格
					
					
					Object cellValue1=null;
					Object cellValue2=null;
					for(; cit1.hasNext();)
					{
						Cell cell = (Cell) cit1.next();
						//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%="+cell.getStringCellValue());
						for(int h=0;h<=cell.getColumnIndex();h++)
						{
							if(cell.getColumnIndex()==0)
							{
								cellValue1=(Object)cell.getStringCellValue();
								break;
							}
							else if(cell.getColumnIndex()==1)
							{
								cellValue2=(Object)cell.getStringCellValue();
								break;
							}
							
						}
						
						
					}
					//System.out.println("############################="+cellValue1+"   %%%%%%%%%%%%%%%%%%%%cellValue2="+cellValue2);
					
					if(cellValue2!=null && cellValue1!=null)
					{
						
					
					
					values.put("contract_code", null);
					values.put("name", null);
					
					for(int j=0;j<bouns.size();j++)
					{
						values.put("type"+(j+1), null);
					}
					
					
					values.put("remark", null);	
					
					
					//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11clounmName="+1);
					// cit.next();
					for (; cit.hasNext();) {

						Cell cell = (Cell) cit.next();
						String clounmName = "";
						int j = cell.getColumnIndex();
						for(int h=0;h<=cell.getColumnIndex();h++)
						{
							if(cell.getColumnIndex()==0)
							{
								clounmName = "contract_code";// 必须存在
								break;
							}
							else if(cell.getColumnIndex()==1)
							{
								clounmName = "name";// 必须存在
								break;
							}
							else if(cell.getColumnIndex()==bouns.size()+2)
							{
								clounmName = "remark";
								break;
							}
							else
							{
								clounmName="type"+(cell.getColumnIndex()-1);
								break;
							}
						}
						//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11clounmName="+0);
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							//System.out.println("????????????????????11clounmName="+clounmName);
							if(!clounmName.equals("contract_code") && !clounmName.equals("name") && !clounmName.equals("remark"))
							{
								String income=null;
								String income_moneys=null;
								if(cell.getStringCellValue()==null)
								{
									income="0";
									income_moneys="0";
								}
								else
								{
									income=cell.getStringCellValue().toString();
									income_moneys=cell.getStringCellValue().toString();
								}
								for(int h=0;h<income.length();h++)
								{
									if(income.charAt(h)>=48 && income.charAt(h)<=57)
									{
										break;
									}
									else
									{
										income_moneys=income_moneys.replace(income.charAt(h)+"", "");
									}
								}
								values.put(clounmName, income_moneys.replace("$", "").replace("￥", "").replace(",", "").replace("，", "").trim());
							}
							else
							{
								values.put(clounmName, cell.getStringCellValue().trim());
							}
							
							break;	
						case Cell.CELL_TYPE_NUMERIC:
							//System.out.println("????????????????????22clounmName="+clounmName);
							if(clounmName.equals("contract_code"))
							{
								String contr= String.valueOf(cell.getNumericCellValue()).trim();
								String contrs=contr.substring(0,contr.length()-2).trim();
								
								values.put(clounmName, contrs);
							}
							else{
							values.put(clounmName, cell.getNumericCellValue());
							}
						}
					}
					}
					
					if (!values.isEmpty() && values.size() > 0) {
						Set<Entry> set = values.entrySet();
						int taglib = 0;
						for (Entry entry : set) {
							if (entry.getValue() == null) {
								taglib++;
							}
						}
						if (taglib == bouns.size()+3) {
							break;
						}
						values.put("rowNumber", (i++));
						//System.out.println("------------------===================="+values);
						Composes.add(values);
					}
					// Composes.add(buileComposeFromStringList(values));
				}
				num = Composes.size();
			}
			}
		}
		return Composes;		
	}
	
	

	/**
	 * 保存上传的excel文件
	 * 
	 * @param context
	 */
	public void saveServiceExcel(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		//String target = null;
		// 得到保存在session中的消息
		FileItem fileItem = (FileItem) context.getRequest().getSession()
				.getAttribute("fileItem");
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		// 保存文件到硬盘中
		try {
			sqlMapClient.startTransaction();
			Long syupId = FileExcelUpload.saveExcelFileToDisk(context, fileItem,sqlMapClient);
			this.saveExcelFileToDataBase(context, sqlMapClient,syupId);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("保存excel错误!请联系管理员") ;
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}
		if(errList.isEmpty()) {
			Output.jspSendRedirect(context,context.request.getContextPath()+"/decompose/upload.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}

/*		if (!target.equals("Success")) {
			context.errList.add("xml存储出错");
			Map map = new HashMap();
			context.getRequest().getRequestDispatcher("/error.jsp");
		}*/
		//return target;
		
	}
	
	
	/**
	 * 保存上传的excel文件
	 * 
	 * @param context
	 */
	public void saveBonusServiceExcel(Context context) {
		Map outputMap = new HashMap() ;
		List errList = context.errList ;
		//String target = null;
		// 得到保存在session中的消息
		FileItem fileItem = (FileItem) context.getRequest().getSession()
				.getAttribute("fileItem");
		SqlMapClient sqlMapClient = DataAccessor.getSession();
		// 保存文件到硬盘中
		try {
			sqlMapClient.startTransaction();
			Long syupId = FileExcelUpload.saveExcelFileToDisk(context, fileItem,sqlMapClient);
			this.saveExcelFileToDataBaseBonus(context, sqlMapClient,syupId);
			sqlMapClient.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.getLogStackTrace(e, logger);
			errList.add("保存上传的excel文件") ;
		} finally {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
			}
		}

/*		if (!target.equals("Success")) {
			context.errList.add("xml存储出错");
			Map map = new HashMap();
			context.getRequest().getRequestDispatcher("/error.jsp");
		}*/
		//return target;
		if(errList.isEmpty()){
			Output.jspSendRedirect(context,context.request.getContextPath()+"/bonus/bonusUpload.jsp");
		} else {
			outputMap.put("errList", errList) ;
			Output.jspOutput(outputMap, context, "/error.jsp") ;
		}
	}

	/**
	 * 将excel解析之后保存到list中的数据到数据库中
	 * 
	 * @param context
	 *            从该此取得session
	 * @throws Exception
	 */
	private void saveExcelFileToDataBase(Context context,
			SqlMapClient sqlMapClient, Long syupId) throws Exception {
		//List<Map> composes = (List<Map>) context.getRequest().getSession().getAttribute("composes");
		String path = realPath(context,syupId);
/*		String bootPath = FileExcelUpload.getUploadPath();
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		String realPath = bootPath + File.separator+ type;*/
		InputStream in =new FileInputStream(path); 
		Workbook workbook = WorkbookFactory.create(in);
		List<Map> composes = buildComposeListFromWorkbook(workbook);
		
		//Add by Michael 2012-3-9 增加来款上传流水号
		CodeRule coder = new CodeRule();
		String code=null;
		
		for (int i = 0; i < composes.size(); i++) {
			context.getContextMap().put("OPPOSING_UNIT_TRUE", "");
			Map map = composes.get(i);
			if(map.get("payment_money")==null){
				map.put("payment_money", 0);
			}
			if(map.get("commission_money")==null){
				map.put("commission_money", 0);
			}
			if(map.get("left_money")==null){
				map.put("left_money", 0);
			}
			
			code = coder.geneFinanceCode(context);
			map.put("income_finance_code", code);
			
			Set<Entry> set = map.entrySet();
			for (Entry entry : set) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				context.getContextMap().put(key, value);		
			}
			
			/*
			 * Add by Michael 2013 03-28 如果上传的虚拟账号为空，则要根据来款人到DB查询出虚拟账号
			 */
			if(map.get("opposing_xuli")==null||"".equals(map.get("opposing_xuli"))){
				List custList=null;
				//首先根据来款账号查询虚拟账号
				custList=sqlMapClient.queryForList("uploadComposeExcel.queryCustVirtualCode", map);
				if(null!=custList && custList.size()>0){
					context.getContextMap().put("opposing_xuli", ((Map) custList.get(0)).get("VIRTUAL_CODE"));
				}else{
					//否则没有根据来款账号没有找到虚拟账号，则根据来款备注来查虚拟账号
					custList=sqlMapClient.queryForList("uploadComposeExcel.queryCustVirtualCodeByOppoMemo", map);
					if(null!=custList && custList.size()>0){
						context.getContextMap().put("opposing_xuli", ((Map) custList.get(0)).get("VIRTUAL_CODE"));
					}
				}
			}else{
				/*
				 * 截取上传的虚拟账号后面18位，系统中的虚拟账号为18位
				 * 上传来款时的虚拟账号为‘代理880232012010080005’，系统自动截取
				 */
				if(map.get("opposing_xuli").toString().trim().length()>=18){
					String tempStr=map.get("opposing_xuli").toString().trim();
					context.getContextMap().put("opposing_xuli", tempStr.substring(tempStr.length()-18,tempStr.length()));
					//根据上传的虚拟帐号得到正确的来款人名称
					List incomeName=sqlMapClient.queryForList("uploadComposeExcel.getIncomeNameByVirtualCode", context.getContextMap());
					if(null!=incomeName && incomeName.size()>0){
						context.getContextMap().put("OPPOSING_UNIT_TRUE", ((Map) incomeName.get(0)).get("CUST_NAME"));
					}
				}	
			}
			// 存储到数据库中
			long fiinId=(Long)sqlMapClient.insert("uploadComposeExcel.create", context.getContextMap());
			
			//*********************************************************************************************
			context.contextMap.put("fiin_id",fiinId);//加入到租金分解新表中,TODO 老功能下线后就删除
			sqlMapClient.insert("rentFinance.saveIncomeMoney",context.contextMap);
			//*********************************************************************************************
		}
		//Output.jspSendRedirect(context,"/decompose/upload.jsp");
	}
	
	
	/**
	 * 将excel解析之后保存到list中的数据到数据库中
	 * 
	 * @param context
	 *            从该此取得session
	 * @throws Exception
	 */
	private void saveExcelFileToDataBaseBonus(Context context,
			SqlMapClient sqlMapClient, Long syupId) throws Exception {
		//List<Map> composes = (List<Map>) context.getRequest().getSession().getAttribute("composes");
		String path = realPath(context,syupId);
/*		String bootPath = FileExcelUpload.getUploadPath();
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		String filePath = fileItem.getName();
		String type = filePath.substring(filePath.lastIndexOf(".") + 1);
		String realPath = bootPath + File.separator+ type;*/
		InputStream in =new FileInputStream(path); 
		Workbook workbook = WorkbookFactory.create(in);
		List bonus=null;
		bonus = (List) DataAccessor.query("bonusManage.queryAllBonus", context.contextMap, DataAccessor.RS_TYPE.LIST);
		List<Map> composes = buildBonusComposeListFromWorkbook(workbook,bonus);
		for (int i = 0; i < composes.size(); i++) {
			Map map = composes.get(i);
			map.put("s_employeeId", context.contextMap.get("s_employeeId"));
			java.util.Date time=new java.util.Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss"+i);
			String timeFormat=sf.format(time).toString();
			map.put("typenumber", timeFormat);
			//System.out.println("yunxinglema?="+composes.size());
			
			for(int j=0;j<bonus.size();j++)
			{
				Map bounsMap=(Map)bonus.get(j);
				String type="type"+(j+1);
				if(map.get(type)!=null)
				{
					map.put("typeId", bounsMap.get("BONUS_ID").toString());
					map.put("typeName", bounsMap.get("BONUS_NAME").toString());
					map.put("upmoney", map.get(type));
					Set<Entry> set = map.entrySet();
					for (Entry entry : set) {
						Object key = entry.getKey();
						Object value = entry.getValue();
						context.getContextMap().put(key, value);		
					}
					// 存储到数据库中
					sqlMapClient.insert("uploadComposeExcel.createBonus", context.getContextMap());
				}
			}
			
//			if(map.get("type1")!=null)
//			{
//				
//				map.put("typeId", "18");
//				map.put("typeName", "加班");
//				map.put("upmoney", map.get("type1"));
//				Set<Entry> set = map.entrySet();
//				for (Entry entry : set) {
//					Object key = entry.getKey();
//					Object value = entry.getValue();
//					context.getContextMap().put(key, value);		
//				}
//				// 存储到数据库中
//				sqlMapClient.insert("uploadComposeExcel.createBonus", context.getContextMap());
//			}
//			
//			if(map.get("type2")!=null)
//			{
//				map.put("typeId", "12");
//				map.put("typeName", "工作能力");
//				map.put("upmoney", map.get("type2"));
//				Set<Entry> set = map.entrySet();
//				for (Entry entry : set) {
//					Object key = entry.getKey();
//					Object value = entry.getValue();
//					context.getContextMap().put(key, value);		
//				}
//				// 存储到数据库中
//				sqlMapClient.insert("uploadComposeExcel.createBonus", context.getContextMap());
//			}
//			
//			if(map.get("type3")!=null)
//			{
//				map.put("typeId", "15");
//				map.put("typeName", "客户数量");
//				map.put("upmoney", map.get("type3"));
//				Set<Entry> set = map.entrySet();
//				for (Entry entry : set) {
//					Object key = entry.getKey();
//					Object value = entry.getValue();
//					context.getContextMap().put(key, value);		
//				}
//				// 存储到数据库中
//				sqlMapClient.insert("uploadComposeExcel.createBonus", context.getContextMap());
//			}

			
						
		}
		//Output.jspSendRedirect(context,"/decompose/upload.jsp");
	}
	
	
	/**
	 * 删除缓存文件 
	 * @param context
	 */
	public void deleteTemp(Context context) {
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		fileItem.delete();
		Output.jspSendRedirect(context,context.request.getContextPath()+"/decompose/upload.jsp");
		
	}
	
	/**
	 * 删除缓存文件 
	 * @param context
	 */
	public void deleteTempBonus(Context context) {
		FileItem fileItem = (FileItem) context.getRequest().getSession().getAttribute("fileItem");
		fileItem.delete();
		Output.jspSendRedirect(context,context.request.getContextPath()+"/bonus/bonusUpload.jsp");
		
	}
	/**
	 * 取得文件保存路径 
	 * @param context
	 */
	public String realPath(Context context,Long syupId) {
		String bootPath = FileExcelUpload.getUploadPath();
		Map pathMap = null;
		List errList = context.errList;
		if(errList.isEmpty()) {
			try {
				context.contextMap.put("syupId", syupId);
				pathMap = (Map)DataAccessor.query("uploadPicture.queryPath", context.contextMap, DataAccessor.RS_TYPE.MAP);
			} catch (Exception e) {
				e.printStackTrace();
				LogPrint.getLogStackTrace(e, logger);
				errList.add(e);
			}
		}
		String path = (String) pathMap.get("PATH");
		String realPath = bootPath + path;
		return realPath;
	}

}
