package com.brick.rent.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.transaction.annotation.Transactional;

import com.brick.base.service.BaseService;
import com.brick.base.to.ReportDateTo;
import com.brick.base.util.LeaseUtil;
import com.brick.base.util.ReportDateUtil;
import com.brick.coderule.service.CodeRule;
import com.brick.common.mail.service.MailUtilService;
import com.brick.common.mail.to.MailSettingTo;
import com.brick.dataDictionary.service.DictionaryUtil;
import com.brick.rent.RentFinanceUtil;
import com.brick.rent.dao.RentFinanceDAO;
import com.brick.rent.to.RentFinanceTO;
import com.brick.rent.to.SettlementLogTO;
import com.brick.rent.to.SettlementTO;
import com.brick.service.core.DataAccessor;
import com.brick.service.core.DataAccessor.RS_TYPE;
import com.brick.service.entity.Context;
import com.brick.util.Constants;
import com.brick.util.DateUtil;
import com.brick.util.FileExcelUpload;
import com.brick.util.StringUtils;
import com.brick.util.web.HTMLUtil;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.tac.dept.service.DeptService;
import com.tac.dept.to.DeptTo;

public class RentFinanceService extends BaseService {
	private RentFinanceDAO rentFinanceDAO;
	private MailUtilService mailUtilService;
	private DeptService deptService;

	public RentFinanceDAO getRentFinanceDAO() {
		return rentFinanceDAO;
	}

	public void setRentFinanceDAO(RentFinanceDAO rentFinanceDAO) {
		this.rentFinanceDAO = rentFinanceDAO;
	}
	
	public MailUtilService getMailUtilService() {
		return mailUtilService;
	}

	public void setMailUtilService(MailUtilService mailUtilService) {
		this.mailUtilService = mailUtilService;
	}
	
	public void setDeptService(DeptService deptService) {
		this.deptService = deptService;
	}

	//把excel数据保存到数据库表中,T_RENT_INCOME来款表
	public void saveData(Context context,FileItem fileItem,SqlMapClient sqlMapClient) throws Exception {
		
		String fileType=fileItem.getName().split("\\.")[1];
		
		String filePath=FileExcelUpload.getUploadPath();
		
		filePath=filePath+File.separator+fileType+File.separator+context.contextMap.get("uploadFileName")+"."+fileType;
		
		InputStream in=new FileInputStream(filePath);
		
		Workbook workbook=WorkbookFactory.create(in);
		
		List<Map<String,Object>> composes=this.buildComposeListFromWorkbook(workbook);
		
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,String> resultMap=null;
		for(int i=0;composes!=null&&i<composes.size();i++) {
			
			if(StringUtils.isEmpty(composes.get(i).get("opposing_xuli"))) {
				if(StringUtils.isEmpty(composes.get(i).get("ID"))) {//身份证号码不为空就不去匹配虚拟帐号
					param.put("content",composes.get(i).get("opposing_unit"));//通过来款户名获得虚拟帐号
					resultMap=(Map<String,String>)sqlMapClient.queryForObject("rentFinance.getVirtualCodeByCustName",param);
					
					if(resultMap==null) {
						param.put("content",composes.get(i).get("opposing_memo")==null?composes.get(i).get("opposing_memo"):composes.get(i).get("opposing_memo").toString());//通过备注获得虚拟帐号
						resultMap=(Map<String,String>)sqlMapClient.queryForObject("rentFinance.getVirtualCodeByCustName",param);
						composes.get(i).put("opposing_xuli",resultMap==null?"":resultMap.get("VIRTUAL_CODE"));
					} else {
						composes.get(i).put("opposing_xuli",resultMap.get("VIRTUAL_CODE"));
					}
				}
			} else {
				String tempVirtualCode=composes.get(i).get("opposing_xuli").toString().trim();
				if(tempVirtualCode.length()>=18) {//如果大于18位,截取代理2字
					composes.get(i).put("opposing_xuli",tempVirtualCode.substring(tempVirtualCode.length()-18,tempVirtualCode.length()));
					composes.get(i).put("opposing_unit_true",sqlMapClient.queryForObject("rentFinance.getCustNameByVirtualCode",composes.get(i)));
				}
			}
			
			composes.get(i).put("s_employeeId",context.contextMap.get("s_employeeId"));
			composes.get(i).put("upload_file_name",context.contextMap.get("uploadFileName"));
			sqlMapClient.insert("rentFinance.saveIncomeMoney",composes.get(i));
		}
	}
	
	public List<Map<String,Object>> buildComposeListFromWorkbook(Workbook workbook) {

		int num=0;
		List<Map<String,Object>> composes=new ArrayList<Map<String,Object>>();
		//放置 Excel一行中的 Compose相关值,其顺序为:
		//当前的行数
		Map<String,Object> values=null;
		if(workbook!=null) {
			int x=workbook.getNumberOfSheets();
			for(int y=0;y<x;y++){ 
				//表格
				Sheet sheet=workbook.getSheetAt(y);
				if (sheet!=null) {
					Iterator<Row> rit=sheet.rowIterator();
					rit.next();
					rit.next();
					//遍历 Workbook 中的指定 Sheet 的所有行(从第二行开始)			
					int i=1+num;	
					for (;rit.hasNext();) {
						values=new HashMap<String,Object>();					
						values.put("receipt_bankno",null);
						values.put("opposing_date",null);
						values.put("income_money",null);
						values.put("opposing_type",null);
						values.put("opposing_bankno",null);
						values.put("opposing_unit",null);
						values.put("opposing_xuli",null);
						values.put("receipt_unit",null);					
						values.put("opposing_dateTag",null);					
						values.put("payment_money",null);
						values.put("left_money",null);
						values.put("commission_money",null);				
						values.put("opposing_bankName",null);
						values.put("opposing_flag",null);
						values.put("opposing_address",null);
						values.put("opposing_explain",null);
						values.put("opposing_summary",null);
						values.put("opposing_postscript",null);
						values.put("opposing_memo",null);
						values.put("ID",null);

						Row row=(Row)rit.next();//得到行
						Iterator<Cell> cit=row.cellIterator();//遍历单元格
						for (;cit.hasNext();) {
							Cell cell=(Cell)cit.next();
							String clounmName="";
							switch (cell.getColumnIndex()) {
							case 0:
								clounmName="receipt_bankno";//必须存在
								break;
							case 1:
								clounmName="opposing_date";//必须存在							
								break;
							case 2:
								clounmName="income_money";//必须存在
								break;
							case 3:
								clounmName="opposing_type";//必须存在							
								break;
							case 4:
								clounmName="opposing_bankno";	
								break;
							case 5:
								clounmName="opposing_unit";//必须存在					
								break;
							case 6:
								clounmName="opposing_xuli";//必须存在					
								break;
							case 7:
								clounmName="receipt_unit";//必须存在
								break;
							case 8:
								clounmName="opposing_memo";
								break;
							case 9:
								clounmName="opposing_dateTag";
								break;
							case 10:
								clounmName="payment_money";
								break;
							case 11:
								clounmName="left_money";
								break;
							case 12:
								clounmName="commission_money";
								break;
							case 13:
								clounmName="opposing_bankName";
								break;
							case 14:
								clounmName="opposing_flag";
								break;
							case 15:
								clounmName="opposing_address";
								break;
							case 16:
								clounmName="opposing_explain";
								break;
							case 17:
								clounmName="opposing_summary";
								break;
							case 18:
								clounmName="opposing_postscript";
								break;
							case 19:
								clounmName="ID";
								break;
							}

							switch(cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								if(clounmName.equals("income_money")) {
									String income_moneys=null;
									String incom_mon=null;
									if(cell.getStringCellValue()==null) {
										income_moneys="0";
										incom_mon="0";
									} else {
										income_moneys=cell.getStringCellValue().toString();
										incom_mon=cell.getStringCellValue().toString();
									}

									for(int h=0;h<incom_mon.length();h++) {
										if(incom_mon.charAt(h)>=48&&incom_mon.charAt(h)<=57) {
											break;
										} else {
											income_moneys=income_moneys.replace(incom_mon.charAt(h)+"","");
										}
									}
									values.put(clounmName, income_moneys.replace("$", "").replace("￥", "").replace(",", "").replace("，", "").replace("?", "").replace("？", "").trim());
								} else if(clounmName.equals("commission_money")) {
									values.put(clounmName, cell.getStringCellValue().replace("$", "").replace("￥", "").replace(",", "").replace("，", "").trim());
								} else {
									values.put(clounmName, cell.getStringCellValue().trim());
									if(clounmName.equals("opposing_xuli")) {
										String tempStr=cell.getStringCellValue().trim();
										if(!"".equals(tempStr)&&tempStr.length()>=18) {
											String virtualCode=tempStr.substring(tempStr.length()-18,tempStr.length());
											Map<String,Object> virtualCodemap=new HashMap<String,Object>();
											virtualCodemap.put("opposing_xuli",virtualCode);
											//根据上传的虚拟帐号得到正确的来款人名称
											try {
												List<Map<String,Object>> incomeName=(List<Map<String,Object>>)DataAccessor.query("uploadComposeExcel.getIncomeNameByVirtualCode",virtualCodemap,DataAccessor.RS_TYPE.LIST);
												if(null!=incomeName&&incomeName.size()>0) {
													values.put("opposing_unit_true",((Map<String,Object>)incomeName.get(0)).get("CUST_NAME"));
												}
											} catch(Exception e) {

											}
										}
									}
								}
								break;
							case Cell.CELL_TYPE_NUMERIC:
								if(clounmName.equals("opposing_date")) {
									values.put(clounmName,new Date(cell.getDateCellValue().getTime()));
									break;
								}						
								values.put(clounmName,cell.getNumericCellValue());							
							}
						}
						if (!values.isEmpty()&&values.size()>0) {
							Set<Entry<String,Object>> set=values.entrySet();
							int taglib=0;
							for(Entry<String,Object> entry:set) {
								if(entry.getValue()==null) {
									taglib++;
								}
							}
							if(taglib==20) {
								break;
							}
							values.put("rowNumber",(i++));
							composes.add(values);
						}
					}
					num=composes.size();
				}
			}
		}
		return composes;
	}
	
	public List<RentFinanceTO> getUnDecomposeMoney(Map<String,Object> param) throws Exception {
		return this.rentFinanceDAO.getUnDecomposeMoney(param);
	}
	
	public String checkCustNameHasPayList(Context context) throws Exception {
		return this.rentFinanceDAO.checkCustNameHasPayList(context);
	}
	
	public Map<String,Object> getIncomeInfoByIncomeId(Context context) throws Exception {
		return this.rentFinanceDAO.getIncomeInfoByIncomeId(context);
	}
	
	public List<Map<String,Object>> getLeasePriceByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getLeasePriceByCustCode(context);
	}
	
	public List<Map<String,Object>> createDecomposeDetailList(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		long billId=0;
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		
		context.contextMap.put("table","T_RENT_DECOMPOSE");
		double decomposePrice=0;//用来分解的总金额
		//如果来款金额大于选择的金额
		if(Double.valueOf(context.contextMap.get("income_money").toString())>
				Double.valueOf(context.contextMap.get("should_price").toString())) {
			decomposePrice=Double.valueOf(context.contextMap.get("should_price").toString());
		} else {
			decomposePrice=Double.valueOf(context.contextMap.get("income_money").toString());
		}
		
		context.contextMap.put("has_red_decompose",0);
		if(RentFinanceUtil.RENT_TYPE.RENT.toString().equals(context.contextMap.get("ficb_code"))) {//租金类
			
			Map<String,Object> decomposePriceMap=this.getDecomposePrice(context);
			
			context.contextMap.put("pay_date",decomposePriceMap.get("PAY_DATE"));
			context.contextMap.put("decompose_status",0);//分解中
			context.contextMap.put("decompose_type",0);//分解类型
			
			if(Constants.TAX_PLAN_CODE_2.equals(context.contextMap.get("tax_plan_code"))) {//增值税税费方案
				
				//数据库中取出的decomposeValueAddTax,decomposeRent都是用负数形式
				double decomposeValueAddTax=Double.valueOf(decomposePriceMap.get("DECOMPOSE_VALUE_ADD_TAX").toString());//需要分解的增值税金额
				double decomposeRent=Double.valueOf(decomposePriceMap.get("DECOMPOSE_RENT").toString());//需要分解的租金
				if(decomposeValueAddTax<0) {//既有增值税又有租金
					
					if(decomposePrice>-decomposeValueAddTax) {//如果用来分解的总金额大于需要分解的增值税金额, 那既要销增值税又要销租金
						
						context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.VALUE_ADD_TAX);//增值税
						context.contextMap.put("should_price",-decomposeValueAddTax);
						context.contextMap.put("decompose_price",decomposeValueAddTax);//销的增值税金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
						context.contextMap.put("bill_id",billId);
						sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
						
						resultList.add(this.generateMap(context,-decomposeValueAddTax,-decomposeValueAddTax,"期增值税"));
						
						context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						context.contextMap.put("should_price",-decomposeRent);
						context.contextMap.put("decompose_price",-(new BigDecimal(decomposePrice).add(new BigDecimal(decomposeValueAddTax)).doubleValue()));//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
						context.contextMap.put("bill_id",billId);
						sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
						
						resultList.add(this.generateMap(context,-decomposeRent,(new BigDecimal(decomposePrice).add(new BigDecimal(decomposeValueAddTax)).doubleValue()),"期租金"));
					} else {//只有增值税
						context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.VALUE_ADD_TAX);//增值税
						context.contextMap.put("should_price",-decomposeValueAddTax);
						context.contextMap.put("decompose_price",-decomposePrice);//销的增值税金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
						context.contextMap.put("bill_id",billId);
						sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
						resultList.add(this.generateMap(context,-decomposeValueAddTax,decomposePrice,"期增值税"));
					}
				} else {//只有租金
					if(decomposePrice>-decomposeRent) {
						
						context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						context.contextMap.put("should_price",-decomposeRent);
						context.contextMap.put("decompose_price",decomposeRent);//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
						context.contextMap.put("bill_id",billId);
						sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
						
						resultList.add(this.generateMap(context,-decomposeRent,-decomposeRent,"期租金"));
					} else {
						
						context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						context.contextMap.put("should_price",-decomposeRent);
						context.contextMap.put("decompose_price",-decomposePrice);//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
						context.contextMap.put("bill_id",billId);
						sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
						
						resultList.add(this.generateMap(context,-decomposeRent,decomposePrice,"期租金"));
					}
				}
			} else {//其他所有税费方案,没有增值税
				double decomposeRent=Double.valueOf(decomposePriceMap.get("DECOMPOSE_RENT").toString());//需要分解的租金
				
				if(decomposePrice>-decomposeRent) {
					
					context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
					context.contextMap.put("should_price",-decomposeRent);
					context.contextMap.put("decompose_price",decomposeRent);//销的租金额
					billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
					context.contextMap.put("bill_id",billId);
					sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
					
					resultList.add(this.generateMap(context,-decomposeRent,-decomposeRent,"期租金"));
				} else {
					
					context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
					context.contextMap.put("should_price",-decomposeRent);
					context.contextMap.put("decompose_price",-decomposePrice);//销的租金额
					billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
					context.contextMap.put("bill_id",billId);
					sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
					
					resultList.add(this.generateMap(context,-decomposeRent,decomposePrice,"期租金"));
				}
			}
		} else if(RentFinanceUtil.RENT_TYPE.DEPOSIT_A.toString().equals(context.contextMap.get("ficb_code"))||
					RentFinanceUtil.RENT_TYPE.DEPOSIT_B.toString().equals(context.contextMap.get("ficb_code"))||
					  RentFinanceUtil.RENT_TYPE.DEPOSIT_C.toString().equals(context.contextMap.get("ficb_code"))||
						RentFinanceUtil.RENT_TYPE.TAX.toString().equals(context.contextMap.get("ficb_code"))) {
			
			if(RentFinanceUtil.RENT_TYPE.DEPOSIT_B.toString().equals(context.contextMap.get("ficb_code"))) {
				double incomeMoney=Double.valueOf(context.contextMap.get("income_money").toString());
				double shouldMoney=Double.valueOf(context.contextMap.get("should_price").toString());
				double decomposeMoney=0;
				if(incomeMoney-shouldMoney>=0) {
					decomposeMoney=shouldMoney;
				} else {
					decomposeMoney=incomeMoney;
				}
				Map<String,Object> depositBMap=this.getLastDepositB(context);
				
				double pledgePrice=Double.valueOf(depositBMap.get("PLEDGE_LAST_PRICE").toString());
				int pledgePeriod=Integer.valueOf(depositBMap.get("PLEDGE_LAST_PERIOD").toString());
				
				int loop=(int)((decomposeMoney-decomposeMoney%(pledgePrice/pledgePeriod))/(pledgePrice/pledgePeriod));
				for(int i=0;i<loop;i++) {
					context.contextMap.put("decompose_price",-pledgePrice/pledgePeriod);
					context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
					context.contextMap.put("should_price",pledgePrice/pledgePeriod);
					context.contextMap.put("period_num",0);
					context.contextMap.put("decompose_status",0);
					context.contextMap.put("decompose_type",0);
					billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
					context.contextMap.put("bill_id",billId);
					sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
					resultList.add(this.generateMap(context,pledgePrice/pledgePeriod,pledgePrice/pledgePeriod,""));
					resultList.get(i).put("descr","保证金B");
					decomposeMoney=decomposeMoney-pledgePrice/pledgePeriod;
				}
				if(decomposeMoney>0) {
					context.contextMap.put("decompose_price",-decomposeMoney);
					context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
					context.contextMap.put("should_price",decomposeMoney);
					context.contextMap.put("period_num",0);
					context.contextMap.put("decompose_status",0);
					context.contextMap.put("decompose_type",0);
					billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
					context.contextMap.put("bill_id",billId);
					sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
					resultList.add(this.generateMap(context,decomposeMoney,decomposeMoney,""));
					resultList.get(resultList.size()-1).put("descr","保证金B");
				}
			} else {
				context.contextMap.put("decompose_price",-decomposePrice);
				context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
				context.contextMap.put("should_price",Double.valueOf(context.contextMap.get("fee")+""));
				context.contextMap.put("period_num",0);
				context.contextMap.put("decompose_status",0);
				context.contextMap.put("decompose_type",0);
				billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
				context.contextMap.put("bill_id",billId);
				sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
				
				resultList.add(this.generateMap(context,Double.valueOf(context.contextMap.get("fee")+""),decomposePrice,""));
				if(RentFinanceUtil.RENT_TYPE.DEPOSIT_A.toString().equals(context.contextMap.get("ficb_code"))) {
					resultList.get(0).put("descr","保证金A");
				} else if(RentFinanceUtil.RENT_TYPE.DEPOSIT_C.toString().equals(context.contextMap.get("ficb_code"))) {
					resultList.get(0).put("descr","保证金C");
				} else if(RentFinanceUtil.RENT_TYPE.TAX.toString().equals(context.contextMap.get("ficb_code"))) {
					resultList.get(0).put("descr","税金");
				}
			}
			
		} else if(RentFinanceUtil.RENT_TYPE.RENT_FINE.toString().equals(context.contextMap.get("ficb_code"))) {//租金罚息
			
			double should_price=Double.valueOf(context.contextMap.get("should_price")+"");
			if(decomposePrice>should_price) {
				context.contextMap.put("decompose_price",-should_price);//销的金额
			} else {
				context.contextMap.put("decompose_price",-decomposePrice);//销的金额
			}
			context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
			context.contextMap.put("should_price",should_price);
			context.contextMap.put("period_num",0);
			context.contextMap.put("decompose_status",0);
			context.contextMap.put("decompose_type",0);
			context.contextMap.put("pay_date",DateUtil.getCurrentDate());
			billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
			context.contextMap.put("bill_id",billId);
			sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
			
			resultList.add(this.generateMap(context,should_price,decomposePrice,""));
			resultList.get(0).put("descr","租金罚息");
			resultList.get(0).put("pay_date",DateUtil.getCurrentDate());
			
		} else if(RentFinanceUtil.RENT_TYPE.MANAGE_FEE.toString().equals(context.contextMap.get("ficb_code"))||
				RentFinanceUtil.RENT_TYPE.MANAGE_FEE2.toString().equals(context.contextMap.get("ficb_code"))||
					 RentFinanceUtil.RENT_TYPE.INSURANCE_DEPUTY_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						RentFinanceUtil.RENT_TYPE.HOME_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   RentFinanceUtil.RENT_TYPE.SETUP_FEE.toString().equals(context.contextMap.get("ficb_code"))||
							  RentFinanceUtil.RENT_TYPE.OTHER_FEE.toString().equals(context.contextMap.get("ficb_code"))) {//管理费,保险费押金代收款,家访费收入,设定费收入 ,其他收入
			
			double should_price=Double.valueOf(context.contextMap.get("should_price")+"");
			if(decomposePrice>should_price) {
				context.contextMap.put("decompose_price",-should_price);//销的金额
			} else {
				context.contextMap.put("decompose_price",-decomposePrice);//销的金额
			}
			context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
			context.contextMap.put("should_price",Double.valueOf(context.contextMap.get("fee")+""));
			context.contextMap.put("period_num",0);
			context.contextMap.put("decompose_status",0);
			context.contextMap.put("decompose_type",0);
			billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
			context.contextMap.put("bill_id",billId);
			sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
			
			resultList.add(this.generateMap(context,Double.valueOf(context.contextMap.get("fee")+""),decomposePrice,""));
			if(RentFinanceUtil.RENT_TYPE.MANAGE_FEE.toString().equals(context.contextMap.get("ficb_code"))||RentFinanceUtil.RENT_TYPE.MANAGE_FEE2.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","管理费收入");
			} else if(RentFinanceUtil.RENT_TYPE.INSURANCE_DEPUTY_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","保险费押金代收款");
			} else if(RentFinanceUtil.RENT_TYPE.HOME_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","家訪費收入");
			} else if(RentFinanceUtil.RENT_TYPE.SETUP_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","設定費收入");
			} else if(RentFinanceUtil.RENT_TYPE.OTHER_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","其他費用收入");
			}
		} else if(RentFinanceUtil.RENT_TYPE.STAY_BUY_PRICE.toString().equals(context.contextMap.get("ficb_code"))||//留购价
					RentFinanceUtil.RENT_TYPE.BANK_FEE_INCOME.toString().equals(context.contextMap.get("ficb_code"))) {//银行手续费收入
			context.contextMap.put("decompose_price",-decomposePrice);
			context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
			context.contextMap.put("should_price",Double.valueOf(context.contextMap.get("fee")+""));
			context.contextMap.put("period_num",0);
			context.contextMap.put("decompose_status",0);
			context.contextMap.put("decompose_type",0);
			billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
			context.contextMap.put("bill_id",billId);
			sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
			
			resultList.add(this.generateMap(context,Double.valueOf(context.contextMap.get("fee")+""),decomposePrice,""));
			if(RentFinanceUtil.RENT_TYPE.STAY_BUY_PRICE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","设备留购价");
			} else if(RentFinanceUtil.RENT_TYPE.BANK_FEE_INCOME.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","银行手续费收入");
			}
		} else if(RentFinanceUtil.RENT_TYPE.LITIGATION_FEE.toString().equals(context.contextMap.get("ficb_code"))||//法务费用
					 RentFinanceUtil.RENT_TYPE.SHIFTING_CHARGES_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						RentFinanceUtil.RENT_TYPE.ANNOUNCEMENT_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   RentFinanceUtil.RENT_TYPE.LAWYER_LETTER_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  RentFinanceUtil.RENT_TYPE.COLLECTION_LETTER_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 RentFinanceUtil.RENT_TYPE.LAWYER_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	RentFinanceUtil.RENT_TYPE.FILE_EXECUTION_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	   RentFinanceUtil.RENT_TYPE.LITIGATION_COPY_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	      RentFinanceUtil.RENT_TYPE.OTHER_LAWY_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	         RentFinanceUtil.RENT_TYPE.OUT_VISIT_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	         	RentFinanceUtil.RENT_TYPE.PAY_TOKEN_FEE.toString().equals(context.contextMap.get("ficb_code"))||
						   	  	 	         	   RentFinanceUtil.RENT_TYPE.REPAY_LITIGATION_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
			
			context.contextMap.put("decompose_price",-decomposePrice);
			context.contextMap.put("bill_code",context.contextMap.get("ficb_code"));
			context.contextMap.put("should_price",Double.valueOf(context.contextMap.get("fee")+""));
			context.contextMap.put("period_num",0);
			context.contextMap.put("decompose_status",0);
			context.contextMap.put("decompose_type",0);
			billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
			context.contextMap.put("bill_id",billId);
			sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
			
			resultList.add(this.generateMap(context,Double.valueOf(context.contextMap.get("fee")+""),decomposePrice,""));
			if(RentFinanceUtil.RENT_TYPE.LITIGATION_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","起诉费");
			} else if(RentFinanceUtil.RENT_TYPE.SHIFTING_CHARGES_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","调档费");
			} else if(RentFinanceUtil.RENT_TYPE.ANNOUNCEMENT_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","公告费");
			} else if(RentFinanceUtil.RENT_TYPE.LAWYER_LETTER_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","律师函费");
			} else if(RentFinanceUtil.RENT_TYPE.COLLECTION_LETTER_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","催收函费");
			} else if(RentFinanceUtil.RENT_TYPE.LAWYER_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","律师费");
			} else if(RentFinanceUtil.RENT_TYPE.FILE_EXECUTION_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","立案执行费");
			} else if(RentFinanceUtil.RENT_TYPE.LITIGATION_COPY_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","诉状复印费");
			} else if(RentFinanceUtil.RENT_TYPE.OTHER_LAWY_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","其他法务费用");
			} else if(RentFinanceUtil.RENT_TYPE.OUT_VISIT_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","委外回访费");
			} else if(RentFinanceUtil.RENT_TYPE.PAY_TOKEN_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","支付令费");
			} else if(RentFinanceUtil.RENT_TYPE.REPAY_LITIGATION_FEE.toString().equals(context.contextMap.get("ficb_code"))) {
				resultList.get(0).put("descr","补缴起诉费");
			}
		}
		
		return resultList;
	}
	
	public List<Map<String,Object>> createSettlementDetailList(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
		long billId=0;
		context.contextMap.put("table","T_RENT_DECOMPOSE");
		context.contextMap.put("has_red_decompose",0);//未被冲红过
		context.contextMap.put("is_settlement_decompose",1);//结清单分解类型
		context.contextMap.put("decompose_status",0);//分解中
		context.contextMap.put("decompose_type",0);//分解类型
		context.contextMap.put("period_num",0);
		
		String [] billCode=HTMLUtil.getParameterValues(context.request,"bill_code","");
		String [] settlementFrom=HTMLUtil.getParameterValues(context.request,"settlement_from","");
		String incomeMoney=context.contextMap.get("income_money").toString();//来款金额
		
		double decomposePrice=0;
		for(int i=0;i<billCode.length;i++) {
			if(Double.valueOf(incomeMoney)==0) {
				break;
			}
			if(Double.valueOf(incomeMoney)>Double.valueOf(billCode[i].split("\\*")[2])) {
				incomeMoney=new BigDecimal(Double.valueOf(incomeMoney)).subtract(new BigDecimal(Double.valueOf(billCode[i].split("\\*")[2]))).toString();
				decomposePrice=-Double.valueOf(billCode[i].split("\\*")[2]);
			} else {
				decomposePrice=-Double.valueOf(incomeMoney);
				incomeMoney="0";
			}
			//billCode[i].split("-")[0] billCode
			//billCode[i].split("-")[1] payDate
			//billCode[i].split("-")[2] shouldPrice	
			context.contextMap.put("bill_code",billCode[i].split("\\*")[0]);
			context.contextMap.put("pay_date",billCode[i].split("\\*")[1]);
			context.contextMap.put("should_price",billCode[i].split("\\*")[2]);
			context.contextMap.put("decompose_price",decomposePrice);
			context.contextMap.put("decompose_from",settlementFrom[i]);
			
			billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);
			context.contextMap.put("bill_id",billId);
			sqlMapClient.insert("rentFinance.insertRent",context.contextMap);
			
			Map<String,Object> resMap=new HashMap<String,Object>();
			resMap.put("income_name",context.contextMap.get("income_name"));
			resMap.put("income_account",context.contextMap.get("income_account"));
			resMap.put("income_name_true",context.contextMap.get("income_name_true"));
			resMap.put("income_date",context.contextMap.get("income_date"));
			resMap.put("income_money",context.contextMap.get("income_money"));
			
			resMap.put("lease_code",billCode[i].split("\\*")[3]);
			resMap.put("recp_code",billCode[i].split("\\*")[4]);
			resMap.put("descr",billCode[i].split("\\*")[5]);
			resMap.put("pay_date",billCode[i].split("\\*")[1]);
			resMap.put("should_price",billCode[i].split("\\*")[2]);
			resMap.put("real_price",-decomposePrice);
			resMap.put("bill_id",billId);
			resultList.add(resMap);
		}
		return resultList;
	}
	
	private Map<String,Object> generateMap(Context context,double shouldPrice,double realPrice,String descr) {
		
		Map<String,Object> resultMap=new HashMap<String,Object>();
		if(context.contextMap.get("pay_date")!=null&&!"".equals(context.contextMap.get("pay_date"))) {
			resultMap.put("pay_date",DateUtil.dateToString(DateUtil.strToDate(context.contextMap.get("pay_date").toString(),"yyyy-MM-dd"),"yyyy-MM-dd"));
		}
		resultMap.put("income_name",context.contextMap.get("income_name"));
		resultMap.put("income_money",context.contextMap.get("income_money"));
		resultMap.put("income_name_true",context.contextMap.get("income_name_true"));
		resultMap.put("income_date",context.contextMap.get("income_date"));
		resultMap.put("income_account",context.contextMap.get("income_account"));
		resultMap.put("bill_type","分解单");
		resultMap.put("descr","第"+context.contextMap.get("period_num")+descr);
		resultMap.put("lease_code",context.contextMap.get("lease_code"));
		resultMap.put("recp_code",context.contextMap.get("recp_code"));
		resultMap.put("should_price",shouldPrice);
		resultMap.put("real_price",realPrice);
		resultMap.put("bill_id",context.contextMap.get("bill_id"));
		return resultMap;
	}
	public Map<String,Object> getDecomposePrice(Context context) throws Exception {
		return this.rentFinanceDAO.getDecomposePrice(context);
	}
	
	public List<Map<String,Object>> getDecomposePriceDetail(Context context) throws Exception {
		return this.rentFinanceDAO.getDecomposePriceDetail(context);
	}
	
	public void commitDecompose(Context context,SqlMapClient sqlMapClient) throws Exception {
		String [] billId=HTMLUtil.getParameterValues(context.request,"bill_id","");
		Map<String,Object> param=new HashMap<String,Object>();
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			param.put("decomposeStatus",1);//更新成提交财务状态
			sqlMapClient.update("rentFinance.commitDecompose",param);
		}
	}
	
	public void confirmDecompose(Context context,SqlMapClient sqlMapClient) throws Exception {
		String [] billId=HTMLUtil.getParameterValues(context.request,"bill_id","");
		Map<String,Object> param=new HashMap<String,Object>();
		double totalDecomposePrice=0;
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			Map<String,Object> resultMap=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getRentPayDetail",param);
			totalDecomposePrice=totalDecomposePrice+Double.valueOf(resultMap.get("DECOMPOSE_PRICE").toString());
			if("1".equals(resultMap.get("DECOMPOSE_TYPE")+"")) {//红冲类型,需要把此条数据更新为做过红冲
				param.put("BILL_ID",billId[i]);
				param.put("hasRedDecompose",1);
				sqlMapClient.update("rentFinance.updateRedFlag",param);
			}
			param.put("decomposeStatus",2);//更新成财务通过状态
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			sqlMapClient.update("rentFinance.confirmOrRejectDecompose",param);
			
			//更新金流表
			param.put("decomposeStatus",1);//在金流表中status=1是已确认
			sqlMapClient.update("rentFinance.commitRecord",param);
		}
		context.contextMap.put("totalDecomposePrice",totalDecomposePrice);
	}
	
	//更新支付表
	public void updateRentPayDetail(Context context,SqlMapClient sqlMapClient) throws Exception {
		String [] billId=HTMLUtil.getParameterValues(context.request,"bill_id","");
		double rentPrice=0;
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		Map<String,Object> resMap1=new HashMap<String,Object>();
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			resMap=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getRentPayDetail",param);
			rentPrice=Double.valueOf(resMap.get("DECOMPOSE_PRICE").toString());
			resMap1=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getReduceOwnPrice",resMap);
			if(resMap1==null) {
				return;
			}
			DecimalFormat f=new DecimalFormat("0.00");
			resMap.put("REDUCE_OWN_PRICE",f.format(new BigDecimal(Double.valueOf(resMap1.get("REDUCE_OWN_PRICE").toString())).add(new BigDecimal(rentPrice)).doubleValue()));
			sqlMapClient.update("rentFinance.updateReduceOwnPrice",resMap);
		}
	}
	
	public void rejectDecompose(Context context,SqlMapClient sqlMapClient) throws Exception {
		String [] billId=HTMLUtil.getParameterValues(context.request,"bill_id","");
		Map<String,Object> param=new HashMap<String,Object>();
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			Map<String,Object> resultMap=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getRentPayDetail",param);
			if("1".equals(resultMap.get("DECOMPOSE_TYPE")+"")) {//红冲驳回,需要把直接分解的数据更新为未冲红状态
				param.put("BILL_ID",billId[i]);
				param.put("hasRedDecompose",1);
				sqlMapClient.update("rentFinance.updateRedFlag",param);
				if(i==0) {
					List<Map<String,Object>> resultList=sqlMapClient.queryForList("rentFinance.getHasRedListByBillId",resultMap);
					for(int j=0;j<resultList.size();j++) {
						resultList.get(j).put("hasRedDecompose",0);
						sqlMapClient.update("rentFinance.updateRedFlag",resultList.get(j));
					}
				}
			}
			param.put("decomposeStatus",-1);//更新成提交财务驳回状态
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			sqlMapClient.update("rentFinance.confirmOrRejectDecompose",param);
			
			//更新金流表
			sqlMapClient.update("rentFinance.commitRecord",param);
		}
	}
	
	public List<Map<String,Object>> getFeeByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getFeeByCustCode(context);
	}
	
	public List<Map<String,Object>> getPledgeAByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getPledgeAByCustCode(context);
	}
	
	public List<Map<String,Object>> getPledgeBByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getPledgeBByCustCode(context);
	}
	
	public List<Map<String,Object>> getPledgeCByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getPledgeCByCustCode(context);
	}
	
	public List<Map<String,Object>> getStayBuyByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getStayBuyByCustCode(context);
	}
	
	public List<Map<String,Object>> getTaxByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getTaxByCustCode(context);
	}
	
	public List<Map<String,Object>> getLawFeeByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getLawFeeByCustCode(context);
	}
	
	public List<Map<String,Object>> getIncomePayByCustCode(Context context) throws Exception {
		return this.rentFinanceDAO.getIncomePayByCustCode(context);
	}
	
	public List<Map<String,Object>> getRedListByBillId(Context context) throws Exception {
		return this.rentFinanceDAO.getRedListByBillId(context);
	}
	
	public void createRedDecompose(Context context,List<Map<String,Object>> resultList,SqlMapClient sqlMapClient) throws Exception {
		
		for(int i=0;i<resultList.size();i++) {
			Map<String,Object> redDecomposeMap=resultList.get(i);
			redDecomposeMap.put("hasRedDecompose",1);//标记此数据做过冲红
			sqlMapClient.update("rentFinance.updateRedFlag",redDecomposeMap);
			redDecomposeMap.put("income_id",redDecomposeMap.get("INCOME_ID"));
			redDecomposeMap.put("recp_id",redDecomposeMap.get("RECP_ID"));
			redDecomposeMap.put("period_num",redDecomposeMap.get("PERIOD_NUM"));
			redDecomposeMap.put("pay_date",redDecomposeMap.get("PAY_DATE"));
			redDecomposeMap.put("bill_code",redDecomposeMap.get("BILL_CODE"));
			redDecomposeMap.put("should_price",redDecomposeMap.get("SHOULD_PRICE"));
			redDecomposeMap.put("decompose_price",-Double.valueOf(redDecomposeMap.get("DECOMPOSE_PRICE")+""));
			redDecomposeMap.put("decompose_from","红冲");
			redDecomposeMap.put("decompose_status",1);//提交财务状态
			redDecomposeMap.put("decompose_type",1);//红冲类型
			redDecomposeMap.put("s_employeeId",context.contextMap.get("s_employeeId"));
			redDecomposeMap.put("redRemark",context.contextMap.get("redRemark"));//红冲备注
			long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",redDecomposeMap);
			redDecomposeMap.put("bill_id",billId);
			redDecomposeMap.put("decompose_status",0);
			redDecomposeMap.put("table","T_RENT_DECOMPOSE");
			sqlMapClient.insert("rentFinance.insertRent",redDecomposeMap);
		}
	}
	
	public String autoDecompose(Context context,SqlMapClient sqlMapClient) throws Exception {//自动分解
		
		DecimalFormat f=new DecimalFormat("0.00");
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("autoDecompose","Y");
		List<Map<String,Object>> decomposeList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getUnDecomposeMoney",param,RS_TYPE.LIST);//拿出当天所有来款信息

		for(int i=0;decomposeList!=null&&i<decomposeList.size();i++) {
			boolean flagForVirtualAccount=false;//客户信息通过虚拟帐号查找出
			boolean flagForId=false;//客户信息通过身份证查找出
			boolean flagForIncomeName=false;//客户信息通过来款户名查找出
			Map<String,Object> decomposeMap=decomposeList.get(i);
			String virtualAccount=decomposeMap.get("VIRTUAL_ACCOUNT")+"";//虚拟帐号
			String incomeMoney=decomposeMap.get("INCOME_MONEY")+"";//来款金额
			String incomeId=decomposeMap.get("INCOME_ID")+"";//来款ID

			//通过虚拟帐号找到客户信息,
			param.put("virtualAccount",virtualAccount);
			String custCode=(String)DataAccessor.query("rentFinance.getCustCodeByVirtualCode",param,RS_TYPE.OBJECT);
			if(custCode==null||"".equals(custCode)) {//如果通过虚拟帐号查找客户信息为空,则通过身份证号码继续查找
				param.put("id",decomposeMap.get("ID")+"");//身份证
				custCode=(String)DataAccessor.query("rentFinance.getCustCodeByIdCard",param,RS_TYPE.OBJECT);
				if(custCode==null||"".equals(custCode)) {//如果通过身份证查找客户信息为空,则通过来款户名继续查找
					param.put("incomeName",decomposeMap.get("INCOME_NAME")+"");
					List<String> custCodeList=(List<String>)DataAccessor.query("rentFinance.getCustCodeByIncomeName",param,RS_TYPE.LIST);
					if(custCodeList==null||custCodeList.size()==0||custCodeList.size()>1) {//用来款户名匹配必须是唯一的,其他情况都不做自动分解
						continue;
					} else {
						custCode=custCodeList.get(0);
						flagForIncomeName=true;
					}
				} else {
					flagForId=true;
				}
			} else {
				flagForVirtualAccount=true;
			}
			
			if(flagForVirtualAccount||flagForIncomeName) {//虚拟帐号匹配的与来款户名匹配的租金销帐一致,需要金额也要相等才能销帐
				//*****************************************************************************************获得支付表信息
				//通过客户信息找到支付表需要缴费的那一期,
				param.put("cust_code",custCode);//SQL按照支付日升序排列,所以拿list.get(0)就是需要缴费的那一期
				Map<String,Object> rentMap=(Map<String,Object>)DataAccessor.query("rentFinance.getTop1LeasePriceByCustCode",param,RS_TYPE.OBJECT);
				if(rentMap==null) {
					continue;//没有支付表跳过
				}
				
				String shouldMoney=rentMap.get("SHOULD_PRICE")+"";//获得应付金额
				String taxPlanCode=rentMap.get("TAX_PLAN_CODE")+"";//获得税费方案
				//*****************************************************************************************
				
				//金额是否匹配支付表需要缴费的那一期,如果相同才会自动销帐,否则就跳过
				if(Double.valueOf(f.format(Double.valueOf(incomeMoney)-Double.valueOf(shouldMoney)))!=0.00) {
					continue;//金额不同跳过
				}
	
				Map<String,Object> insertMap=new HashMap<String,Object>();
				long billId=0;
				
				insertMap.put("income_id",incomeId);
				insertMap.put("recp_id",rentMap.get("RECP_ID"));
				insertMap.put("period_num",rentMap.get("PERIOD_NUM"));
				insertMap.put("pay_date",rentMap.get("PAY_DATE"));
				insertMap.put("decompose_from","客户");
				insertMap.put("decompose_status",1);//提交财务状态
				insertMap.put("decompose_type",0);//分解类型
				insertMap.put("has_red_decompose",0);//未冲红过
				insertMap.put("table","T_RENT_DECOMPOSE");
				insertMap.put("s_employeeId",184);//自动分解使用系统帐号
				
				if(Constants.TAX_PLAN_CODE_2.equals(taxPlanCode)) {//如果是增值税税费方案,需要拆分租金与增值税
					
					context.contextMap.put("recp_id",rentMap.get("RECP_ID"));
					context.contextMap.put("period_num",rentMap.get("PERIOD_NUM"));
					Map<String,Object> decomposePriceMap=this.getDecomposePrice(context);
					
					//数据库中取出的decomposeValueAddTax,decomposeRent都是用负数形式
					double decomposeValueAddTax=Double.valueOf(decomposePriceMap.get("DECOMPOSE_VALUE_ADD_TAX").toString());//需要分解的增值税金额
					double decomposeRent=Double.valueOf(decomposePriceMap.get("DECOMPOSE_RENT").toString());//需要分解的租金
					
					sqlMapClient.startTransaction();
					if(decomposeValueAddTax<0) {//既有增值税,又有租金
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.VALUE_ADD_TAX);//增值税
						insertMap.put("should_price",-decomposeValueAddTax);
						insertMap.put("decompose_price",decomposeValueAddTax);//销的增值税金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",0);
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						insertMap.put("should_price",-decomposeRent);
						insertMap.put("decompose_price",decomposeRent);//销的租金额
						insertMap.put("decompose_status",1);//提交财务状态
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",0);
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
					} else {//只有租金
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						insertMap.put("should_price",-decomposeRent);
						insertMap.put("decompose_price",decomposeRent);//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",0);
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
					}
					sqlMapClient.commitTransaction();
				} else {//非增值税税费方案
					sqlMapClient.startTransaction();
					insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
					insertMap.put("should_price",rentMap.get("MONTH_PRICE"));
					insertMap.put("decompose_price",-Double.valueOf(shouldMoney));//销的租金额
					billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
					insertMap.put("bill_id",billId);
					insertMap.put("decompose_status",0);//待确认状态,租金分解表是提交财务状态,在金流表中是待确认状态
					sqlMapClient.insert("rentFinance.insertRent",insertMap);
					sqlMapClient.commitTransaction();
				}
			}
			if(flagForId) {//通过身份证查找支付表并进行销帐
				String taxPlanCode="";
				int j=0;
				//*****************************************************************************************获得支付表信息
				//通过客户信息找到支付表需要缴费的那一期,
				param.put("cust_code",custCode);//SQL按照支付日升序排列,所以拿list.get(0)就是需要缴费的那一期
				Map<String,Object> rentMap=(Map<String,Object>)DataAccessor.query("rentFinance.getTop1LeasePriceByCustCode",param,RS_TYPE.OBJECT);
				String shouldMoney=rentMap.get("SHOULD_PRICE")+"";
				List<Map<String,Object>> rentList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getAllLeasePriceByCustCode",param,RS_TYPE.LIST);
				if(rentList==null||rentList.size()==0) {
					continue;//没有支付表跳过
				}
				//*****************************************************************************************
				while(Double.valueOf(f.format(Double.valueOf(incomeMoney)-Double.valueOf(shouldMoney)))>0.00) {
					
					shouldMoney=rentList.get(j).get("SHOULD_PRICE")+"";//获得应付金额
					taxPlanCode=rentList.get(j).get("TAX_PLAN_CODE")+"";//获得税费方案
					if(Constants.TAX_PLAN_CODE_2.equals(taxPlanCode)) {
						//乘用车类型不可能有增值税税费方案						
					} else {
						Map<String,Object> insertMap=new HashMap<String,Object>();
						long billId=0;
						
						insertMap.put("income_id",incomeId);
						insertMap.put("recp_id",rentList.get(j).get("RECP_ID"));
						insertMap.put("period_num",rentList.get(j).get("PERIOD_NUM"));
						insertMap.put("pay_date",rentList.get(j).get("PAY_DATE"));
						insertMap.put("decompose_from","客户");
						insertMap.put("decompose_status",1);//提交财务状态
						insertMap.put("decompose_type",0);//分解类型
						insertMap.put("has_red_decompose",0);//未冲红过
						insertMap.put("table","T_RENT_DECOMPOSE");
						insertMap.put("s_employeeId",184);//自动分解使用系统帐号
						
						sqlMapClient.startTransaction();
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						insertMap.put("should_price",rentList.get(j).get("MONTH_PRICE"));
						insertMap.put("decompose_price",-Double.valueOf(shouldMoney));//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",0);//待确认状态,租金分解表是提交财务状态,在金流表中是待确认状态
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						sqlMapClient.commitTransaction();
					}
					j++;
					//销帐后的剩余来款金额
					incomeMoney=f.format(Double.valueOf(incomeMoney)-Double.valueOf(shouldMoney));
					if(rentList.size()==j) {
						break;
					} else {
						shouldMoney=rentList.get(j).get("SHOULD_PRICE")+"";//获得应付金额
					}
				}
				if(Double.valueOf(f.format(Double.valueOf(incomeMoney)))>0.00) {//还有剩余的钱,则继续销帐
					if(rentList.size()==j) {
						//所有租金销完了,还有剩余来款
					} else {
						Map<String,Object> insertMap=new HashMap<String,Object>();
						long billId=0;
						
						insertMap.put("income_id",incomeId);
						insertMap.put("recp_id",rentList.get(j).get("RECP_ID"));
						insertMap.put("period_num",rentList.get(j).get("PERIOD_NUM"));
						insertMap.put("pay_date",rentList.get(j).get("PAY_DATE"));
						insertMap.put("decompose_from","客户");
						insertMap.put("decompose_status",1);//提交财务状态
						insertMap.put("decompose_type",0);//分解类型
						insertMap.put("has_red_decompose",0);//未冲红过
						insertMap.put("table","T_RENT_DECOMPOSE");
						insertMap.put("s_employeeId",184);//自动分解使用系统帐号
						
						sqlMapClient.startTransaction();
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);//租金
						insertMap.put("should_price",rentList.get(j).get("MONTH_PRICE"));
						insertMap.put("decompose_price",-Double.valueOf(incomeMoney));//销的租金额
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",0);//待确认状态,租金分解表是提交财务状态,在金流表中是待确认状态
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						sqlMapClient.commitTransaction();
					}
				}
			}
		}
		return "自动分解完成!";
	}
	
	public void autoConfirm(Context context,SqlMapClient sqlMapClient) {
		
		String [] values=HTMLUtil.getParameterValues(context.request,"checkBox","");
		String isSettlementDecompose="";
		for(int i=0;values!=null&&i<values.length;i++) {
			isSettlementDecompose=values[i].split("-")[4];
			
			context.contextMap.put("incomeId",values[i].split("-")[0]);
			context.contextMap.put("periodNum",values[i].split("-")[1]);
			context.contextMap.put("recpId",values[i].split("-")[2]);
			context.contextMap.put("decomposeStatus",values[i].split("-")[3]);
			try {
				List<Map<String,Object>> decomposeList=this.getDecomposePriceDetail(context);
				String [] billId=new String[decomposeList==null?0:decomposeList.size()];
				for(int j=0;decomposeList!=null&&j<decomposeList.size();j++) {//批量财务确认
					billId[j]=decomposeList.get(j).get("BILL_ID")+"";
				}
				if(billId==null||billId.length==0) {
					continue;
				}
				sqlMapClient.startTransaction();
				if("1".equals(isSettlementDecompose)) {//结清分解确认类型
					context.contextMap.put("recpId",values[i].split("-")[2]);
					Double shouldPayTotalPrice=this.getSettlementPayTotalPriceByRecpId(context,sqlMapClient);
					this.autoConfirmDecompose(context,billId,sqlMapClient);//更新租金分解表,金流表
					if(Double.valueOf(context.contextMap.get("totalDecomposePrice").toString())-shouldPayTotalPrice==0) {//此案子的结清金额全部销完,需要更新支付表结清状态,分解金额等于总共需要分解的金额
						this.updateRecpStatus(context,sqlMapClient);
					}
				} else {//普通租金分解类型
					this.autoConfirmDecompose(context,billId,sqlMapClient);//更新租金分解表,金流表
					this.autoUpdateRentPayDetail(context,billId,sqlMapClient);//更新支付表
				}
				sqlMapClient.commitTransaction();
			} catch(Exception e) {
				try {
					sqlMapClient.endTransaction();
				} catch (SQLException e1) {
					
				}
			}
		}
	}
	
	private void autoConfirmDecompose(Context context,String [] billId,SqlMapClient sqlMapClient) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		double totalDecomposePrice=0;
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			Map<String,Object> resultMap=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getRentPayDetail",param);
			totalDecomposePrice=totalDecomposePrice+Double.valueOf(resultMap.get("DECOMPOSE_PRICE").toString());
			if("1".equals(resultMap.get("DECOMPOSE_TYPE")+"")) {//红冲类型,需要把此条数据更新为做过红冲
				param.put("BILL_ID",billId[i]);
				param.put("hasRedDecompose",1);
				sqlMapClient.update("rentFinance.updateRedFlag",param);
			}
			param.put("decomposeStatus",2);//更新成财务通过状态
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			sqlMapClient.update("rentFinance.confirmOrRejectDecompose",param);
			
			//更新金流表
			param.put("decomposeStatus",1);//在金流表中status=1是已确认
			sqlMapClient.update("rentFinance.commitRecord",param);
		}
		context.contextMap.put("totalDecomposePrice",totalDecomposePrice);
	}
	
	private void autoUpdateRentPayDetail(Context context,String [] billId,SqlMapClient sqlMapClient) throws Exception {
		double rentPrice=0;
		Map<String,Object> param=new HashMap<String,Object>();
		Map<String,Object> resMap=new HashMap<String,Object>();
		Map<String,Object> resMap1=new HashMap<String,Object>();
		for(int i=0;i<billId.length;i++) {
			param.put("billId",billId[i]);
			resMap=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getRentPayDetail",param);
			rentPrice=Double.valueOf(resMap.get("DECOMPOSE_PRICE").toString());
			resMap1=(Map<String,Object>)sqlMapClient.queryForObject("rentFinance.getReduceOwnPrice",resMap);
			if(resMap1==null) {
				return;
			}
			DecimalFormat f=new DecimalFormat("0.00");
			resMap.put("REDUCE_OWN_PRICE",f.format(new BigDecimal(Double.valueOf(resMap1.get("REDUCE_OWN_PRICE").toString())).add(new BigDecimal(rentPrice)).doubleValue()));
			sqlMapClient.update("rentFinance.updateReduceOwnPrice",resMap);
		}
	}
	
	public Map<String,Object> getCustInfoByRecpId(Context context) throws Exception {
		return this.rentFinanceDAO.getCustInfoByRecpId(context);
	}
	
	/**
	 * 根据字典表数据生成map
	 * @param dataType
	 * @return
	 */
	public Map<Integer, Map<String, String>> getTypeState(List<Map> dataType) {
		Map<Integer, Map<String, String>> states = new HashMap<Integer, Map<String,String>>();
		for(Map t : dataType){
			Map<String,String> msg = new HashMap<String, String>();
			msg.put("chsName", t.get("FLAG").toString());
			msg.put("enName", t.get("SHORTNAME").toString());
			if(org.apache.commons.lang.StringUtils.isNumeric(t.get("REMARK")==null?"":t.get("REMARK").toString())){
				
			}
			msg.put("userName", t.get("U_NAME")==null?"":t.get("U_NAME").toString());
			states.put(Integer.parseInt(t.get("CODE").toString()), msg);
		}
		return states;
	}

	/**
	 * 根据权限英文名获取权限code
	 * @param dataType
	 * @return
	 */
	public int getTypeStateCodeByEnName(List<Map> dataType, String enName) {
		for(Map t : dataType){
			if(enName.equals(t.get("SHORTNAME").toString())){
				return Integer.parseInt(t.get("CODE").toString());
			}
		}
		return 0;
	}
	
	public Map<String,Object> getSettlementPrice(Context context) throws Exception {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		
		DecimalFormat f=new DecimalFormat();
		f.applyPattern("##,##0.00");
		
		resultMap.put("OWN_PRICE",f.format(this.rentFinanceDAO.getSettlementOwnPrice(context)));
		resultMap.put("REN_PRICE",f.format(this.rentFinanceDAO.getSettlementInterest(context)));
		if(StringUtils.isEmpty(LeaseUtil.getTotalFineByRecpId(context.contextMap.get("recpId").toString()))) {
			resultMap.put("FINE",f.format(0.00d));
		} else {
			resultMap.put("FINE",f.format(LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")));
		}
		resultMap.put("VALUE_ADDED_TAX",f.format(this.rentFinanceDAO.getSettlementValueAddedTax(context)));
		resultMap.put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));
		resultMap.put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));
		resultMap.put("OTHER_FEE",f.format(0));//其他费用默认为0
		
		return resultMap;
	}
	
	/**
	 * 预估结清
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getSettlementPriceAdvance(Context context) throws Exception {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		
		DecimalFormat f=new DecimalFormat();
		f.applyPattern("##,##0.00");
		
		resultMap.put("OWN_PRICE",f.format(this.rentFinanceDAO.getSettlementOwnPrice(context)));
		resultMap.put("REN_PRICE",f.format(this.rentFinanceDAO.getSettlementInterest(context)));
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("RECP_ID", context.contextMap.get("recpId").toString());
		paramMap.put("COM_DATE", context.contextMap.get("QUERY_DATE")==null?"":context.contextMap.get("QUERY_DATE").toString());
		Map<String, Object> result = (Map<String,Object>) DataAccessor.query("decompose.getTotalFineByMap", paramMap, RS_TYPE.MAP);
		
		if(StringUtils.isEmpty(result)) {
			resultMap.put("FINE",f.format(0.00d));
		} else {
			resultMap.put("FINE",f.format(result.get("FINE")));
		}
		resultMap.put("VALUE_ADDED_TAX",f.format(this.rentFinanceDAO.getSettlementValueAddedTax(context)));
		resultMap.put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));
		resultMap.put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));
		resultMap.put("OTHER_FEE",f.format(0));//其他费用默认为0
		
		return resultMap;
	}
	
	public void insertSettlement(Context context) throws Exception {
		this.rentFinanceDAO.insertSettlement(context);
	}
	
	public boolean checkDuplicateCommit(Context context) throws Exception {
		return this.rentFinanceDAO.checkDuplicateCommit(context);
	}
	
	public boolean checkPendingData(Context context) throws Exception {
		return this.rentFinanceDAO.checkPendingData(context);
	}
	
	public List<Map<String,Object>> getSettlementHistoryByRecpId(Context context) throws Exception {
		return this.rentFinanceDAO.getSettlementHistoryByRecpId(context);
	}
	
	/**
	 * 根据当前审批信息获取下一状态审批信息
	 * @param settlementTO
	 * @return
	 */
	public SettlementTO getNextStateSettlement(SettlementTO settlementTO) {
		
		return null;
	}
	
	@Transactional
	public SettlementTO approveOrRejectSettlement(Context context) throws Exception {
		SettlementTO settlement = (SettlementTO)context.contextMap.get("settlementTO");
		int currentUserId = Integer.parseInt(context.contextMap.get("s_employeeId").toString());
		java.util.Date date = new java.util.Date(); 
		int oldStateCode = settlement.getStateCode()==null?0:settlement.getStateCode();
		SettlementLogTO setLog = new SettlementLogTO();
		setLog.setOpMsg(context.contextMap.get("remark").toString());
		setLog.setOpUserId(currentUserId);
		setLog.setOpTime(date);
		setLog.setSettlementId(settlement.getId());
		setLog.setOpState(Integer.parseInt(context.contextMap.get("opType").toString()));
		setLog.setStateCode(oldStateCode);
		//插日志
		this.rentFinanceDAO.insertSettlementLog(setLog);
		//结清审批状态流程
		List<Map> dataType = (List<Map>)context.contextMap.get("dataType");
		
		//更新状态
		int newStateCode = 0;
		int newUserId = 0;
		if(context.contextMap.get("opType").equals("1")){
			//通过
			SettlementTO newSettlement = this.getNewSettlement(dataType, settlement);
			newStateCode = newSettlement.getStateCode();
			newUserId = newSettlement.getCurrentUserId();
		} else {
			//驳回
			newStateCode = -1;
		}
		context.contextMap.put("stateCode", newStateCode);
		context.contextMap.put("newUserId", newUserId);
		settlement.setStateCode(newStateCode);
		this.rentFinanceDAO.approveOrRejectSettlement(context);
		
//		if(RentFinanceUtil.AUTHORITY.FINANCIAL.toString().equals(context.contextMap.get("column"))&&
//				"Y".equals(context.contextMap.get("result").toString())) {
		if(newStateCode == 900 && "Y".equals(context.contextMap.get("result").toString())){
			this.rentFinanceDAO.updateSettlementState(context);
			this.rentFinanceDAO.updateSettleDateByRecpId(context);
			//如果结清单的申请还款金额为0,需要直接更新支付表状态为正常结清状态
			if(settlement.getTotalPayPrice()==0) {
				this.rentFinanceDAO.updatePayDetailTableByRecpId(context);
			}
		}
		return settlement;
	}
	
	/**
	 * 获取新的结清审批状态
	 * @param dataType
	 * @param settlement
	 * @return 状态码，900表示已完成最后一步
	 * @throws Exception
	 */
	public SettlementTO getNewSettlement(List<Map> dataType, SettlementTO settlement) throws Exception {
		//新的状态code
		int newStateCode = this.getSettlementNextStateCode(dataType, settlement.getStateCode());
		//新的处理人id
		int newUserId = 0;
//		减免金额：
//		a.500元（含）以下
//		b.500元以上，需总经理、经管处审批 
		if(settlement.getTotalPrice() - settlement.getTotalPayPrice() <= 500 && (newStateCode == 60 || newStateCode == 90)){
			//小于等于500，pass掉总经理,经管处
			newStateCode = this.getSettlementNextStateCode(dataType, newStateCode);
		}
		switch (newStateCode) {
		case 30:
			//单位主管
			newUserId = settlement.getUpUserId();
			break;
		case 80:
			//区域主管
			newUserId = this.getAreaLeaderByDeptId(settlement.getDepartment());
			break;
		default:
			newUserId = this.getSettlementNextUserId(dataType, newStateCode);
			break;
		}
		settlement.setStateCode(newStateCode);
		settlement.setCurrentUserId(newUserId);
		return settlement;
	}
	
	/**
	 * 根据部门id获取区域主管id
	 * @param deptId 
	 * @return 无效返回0
	 */
	public int getAreaLeaderByDeptId(int deptId){
		//区域主管
		DeptTo deptTo = this.deptService.getDeptById(deptId);
		//防止无限循环
		int index = 0;
		while(deptTo.getDeptLevel() > 10 && index < 5){
			deptTo = this.deptService.getParentDeptDetailById(deptTo.getId());
			index++;
		}
		if(deptTo.getDeptLevel()==10){
			//部级
			return deptTo.getDeptLeader();
		}
		return 0;
	}
	
	/**
	 * 根据新stateCode获取流程新的处理人id，无则返回0
	 * @param dataType
	 * @param newStateCode
	 * @return
	 */
	private int getSettlementNextUserId(List<Map> dataType, int newStateCode){
		for(Map m : dataType){
			if(Integer.toString(newStateCode).equals(m.get("CODE")==null?"":m.get("CODE").toString())){
				String userId = m.get("REMARK")==null?"0":m.get("REMARK").toString();
				if(org.apache.commons.lang.StringUtils.isNumeric(userId)){
					return Integer.parseInt(userId);
				}
				return 0;
			}
		}
		return 0;
	}
	
	/**
	 * 根据旧stateCode获取流程新的stateCode，完结返回900
	 * @param dataType
	 * @param oldStateCode
	 * @return
	 */
	private int getSettlementNextStateCode(List<Map> dataType, int oldStateCode){
		if(oldStateCode <= 0 || oldStateCode >= 900){
			return Integer.parseInt(dataType.get(0).get("CODE").toString());
		}
		int index = 0;
		for(int i = 0; i < dataType.size(); i++){
			if(dataType.get(i).get("CODE").equals(String.valueOf(oldStateCode))){
				index = i;
			}
		}
		if(index == dataType.size() - 1){
			return 900;
		} else {
			return Integer.parseInt(dataType.get(index + 1).get("CODE").toString());
		}
	}
	
	public Map<String,Object> getSettlementDetailByIdForEmail(Context context) throws Exception {
		return this.rentFinanceDAO.getSettlementDetailByIdForEmail(context);
	}
	
	public List<Map<String,Object>> getSettlementDetailByRecpId(Context context) throws Exception {
		return this.rentFinanceDAO.getSettlementDetailByRecpId(context);
	}
	
	public Map<String,Object> getSettlementDetailById(Context context) throws Exception {
		return this.rentFinanceDAO.getSettlementDetailById(context);
	}

	@Transactional
	public SettlementTO getSettlementById(Context context) throws Exception {
		return this.rentFinanceDAO.getSettlementById(context);
	}
	
	public String getEmailByRecpId(Map<String,Object> param) throws Exception {
		return this.rentFinanceDAO.getEmailByRecpId(param);
	}
	
	public List<Map<String,Object>> getCustInfo() throws Exception {
		return this.rentFinanceDAO.getCustInfo();
	}
	
	public List<Map<String,Object>> getSettlementCustInfo() throws Exception {
		return this.rentFinanceDAO.getSettlementCustInfo();
	}
	
	public Double getSettlementPayTotalPriceByRecpId(Context context,SqlMapClient sqlMapClient) throws Exception {
		return (Double)sqlMapClient.queryForObject("rentFinance.getSettlementPayTotalPriceByRecpId",context.contextMap);
	}
	
	public void updateRecpStatus(Context context,SqlMapClient sqlMapClient) throws Exception {
		sqlMapClient.update("rentFinance.updateRecpStatus",context.contextMap);
	}
	
	public Map<String,Object> getLastDepositB(Context context) throws Exception {
		return this.rentFinanceDAO.getLastDepositB(context);
	}
	
	public List<Map<String,Object>> getDepositBRedList(Context context) throws Exception {
		return this.rentFinanceDAO.getDepositBRedList(context);
	}
	
	public void commitDepositBCDecompose(Context context,String [] params,SqlMapClient sqlMapClient) throws Exception {
		
		context.contextMap.put("billId",params[0].split("-")[0]);
		Map<String,Object> rentInfo=this.rentFinanceDAO.getRentPayDetail(context);
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("table","T_RENT_DECOMPOSE");
		param.put("recp_id",rentInfo.get("RECP_ID"));
		param.put("period_num",rentInfo.get("PERIOD_NUM"));
		param.put("income_id",rentInfo.get("INCOME_ID"));
		param.put("is_settlement_decompose",rentInfo.get("IS_SETTLEMENT_DECOMPOSE"));
		param.put("pay_date",rentInfo.get("PAY_DATE"));
		
		for(int i=0;i<params.length;i++) {
			String decompsoePrice=params[i].split("-")[1];
			String billCode=params[i].split("-")[2];
			param.put("should_price",decompsoePrice);
			param.put("decompose_price",decompsoePrice);
			param.put("bill_code",billCode);
			param.put("decompose_from","红冲");
			param.put("decompose_status",1);
			param.put("decompose_type",1);
			param.put("has_red_decompose",1);
			param.put("s_employeeId",context.contextMap.get("s_employeeId"));
			long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",param);
			param.put("bill_id",billId);
			param.put("decompose_status",0);
			sqlMapClient.insert("rentFinance.insertRent",param);
		}
	}
	
	public String checkRedDecomposeIsLock(Context context) throws Exception {
		return this.rentFinanceDAO.checkRedDecomposeIsLock(context);
	}
	
	public String checkRedDepositBCDecomposeIsLock(Context context) throws Exception {
		return this.rentFinanceDAO.checkRedDepositBCDecomposeIsLock(context);
	}
	
	public int checkCanBeAutoDecompose() throws Exception {
		return this.rentFinanceDAO.checkCanBeAutoDecompose();
	}
	
	public long insertRedDecompose(Context context) throws Exception {
		return this.rentFinanceDAO.insertRedDecompose(context);
	}
	
	public void insertRed(Context context) throws Exception {
		this.rentFinanceDAO.insertRed(context);
	}
	
	public void updateRedFlag(Context context) throws Exception {
		this.rentFinanceDAO.updateRedFlag(context);
	}
	
	public void commitRecord(Context context) throws Exception {
		this.rentFinanceDAO.commitRecord(context);
	}
	@Transactional
	public void redDecomposeDepositBC(Context context) throws Exception {
		
		context.contextMap.put("BILL_ID",context.contextMap.get("billId"));
		context.contextMap.put("hasRedDecompose",1);
		this.updateRedFlag(context);
		long bill_id=this.insertRedDecompose(context);
		context.contextMap.put("bill_id",bill_id);
		this.insertRed(context);
	}
	
	public List<Map<String,Object>> getNullPrincipalList(Context context) throws Exception {
		return this.rentFinanceDAO.getNullPrincipalList(context);
	}
	
	public void updateNullPrincipalByBillId(Map<String,Object> param) throws Exception {
		this.rentFinanceDAO.updateNullPrincipalByBillId(param);
	}
	
	public void updateNullPrincipalByBillIdPeriodNumRecpId(Map<String,Object> param) throws Exception {
		this.rentFinanceDAO.updateNullPrincipalByBillIdPeriodNumRecpId(param);
	}
	
	public boolean checkClaimRefundAmount(Context context) throws Exception {
		double restMoney=this.rentFinanceDAO.checkClaimRefundAmount(context);
		if(Double.valueOf(context.contextMap.get("money").toString())-restMoney>0) {//如果认领金额大于剩余金额,则返回true,提示锁住
			return true;
		}
		return false;
	}
	
	public void commitClaim(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		context.contextMap.put("claimState",0);
		sqlMapClient.insert("rentFinance.insertClaim",context.contextMap);//插入认领款表
		context.contextMap.put("income_id",context.contextMap.get("claimIncomeId"));
		context.contextMap.put("recp_id",-1);
		context.contextMap.put("period_num",0);
		context.contextMap.put("pay_date",DateUtil.getCurrentDate());
		context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.CLAIM);
		context.contextMap.put("should_price",context.contextMap.get("claimMoney"));
		context.contextMap.put("decompose_price","-"+context.contextMap.get("claimMoney"));
		context.contextMap.put("decompose_from","客户");
		context.contextMap.put("decompose_status",1);//提交财务待确认
		context.contextMap.put("decompose_type",0);
		context.contextMap.put("has_red_decompose",0);
		context.contextMap.put("is_settlement_decompose",0);
		long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);//插入租金分解表
		
		context.contextMap.put("bill_id",billId);
		context.contextMap.put("decompose_status",0);
		context.contextMap.put("table",RentFinanceUtil.TABLE.T_RENT_DECOMPOSE);
		sqlMapClient.insert("rentFinance.insertRent",context.contextMap);//插入金流表
	}
	
	public void commitRefund(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		if("-1".equals(context.contextMap.get("refundType").toString())) {
			context.contextMap.put("refundType",context.contextMap.get("other"));
		}
		context.contextMap.put("serialNumber",CodeRule.geneFundsReturnCode());
		context.contextMap.put("refundState",0);
		long refundId=(Long)sqlMapClient.insert("rentFinance.insertRefund",context.contextMap);//插入退款表
		context.contextMap.put("income_id",context.contextMap.get("refundIncomeId"));
		context.contextMap.put("recp_id",-1);
		context.contextMap.put("period_num",0);
		context.contextMap.put("pay_date",context.contextMap.get("refundDate"));
		context.contextMap.put("bill_code",RentFinanceUtil.RENT_TYPE.REFUND);
		context.contextMap.put("should_price",Double.valueOf(context.contextMap.get("amount").toString()));
		context.contextMap.put("decompose_price",-1*(Double.valueOf(context.contextMap.get("amount").toString())));
		context.contextMap.put("decompose_from","客户");
		context.contextMap.put("decompose_status",1);//提交财务待确认
		context.contextMap.put("decompose_type",0);
		context.contextMap.put("has_red_decompose",0);
		context.contextMap.put("is_settlement_decompose",0);
		long billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",context.contextMap);//插入租金分解表
		
		context.contextMap.put("bill_id",billId);
		context.contextMap.put("decompose_status",0);
		context.contextMap.put("table",RentFinanceUtil.TABLE.T_RENT_DECOMPOSE);
		sqlMapClient.insert("rentFinance.insertRent",context.contextMap);//插入金流表
		
		context.contextMap.put("refundId",refundId);
		sqlMapClient.insert("rentFinance.insertPayDetail",context.contextMap);//插入PAY_DETAIL表
		
		context.contextMap.put("memo","申请退款");
		sqlMapClient.insert("rentFinance.insertRefundLog",context.contextMap);//插入退款日志表
	}
	
	public List<Map<String,Object>> showClaimRefundList(Context context) throws Exception {
		return this.rentFinanceDAO.showClaimRefundList(context);
	}
	
	public void approveFund(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		sqlMapClient.update("rentFinance.batchUpdateClaim",context.contextMap);//更新认领款表
		sqlMapClient.update("rentFinance.batchUpdateRefund",context.contextMap);//更新退款表
		
		//更新之前先获得此incomeId更新的所有billId
		List<String> billIds=sqlMapClient.queryForList("rentFinance.getBatchBillId",context.contextMap);
		context.contextMap.put("decomposeStatus",1);//通过在T_RENT_RECORD中是1
		for(int i=0;i<billIds.size();i++) {
			context.contextMap.put("billId",billIds.get(i));
			sqlMapClient.update("rentFinance.commitRecord",context.contextMap);
		}
		context.contextMap.put("decomposeStatus",2);//通过在T_RENT_DECOMPOSE中是2
		sqlMapClient.update("rentFinance.batchUpdateDecompose",context.contextMap);
	}
	
	public void approveOrRejectFund(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		String billId="";
		if(RentFinanceUtil.RENT_TYPE.CLAIM.toString().equals(context.contextMap.get("type"))) {
			billId=(String)sqlMapClient.queryForObject("rentFinance.getBillIdByClaim",context.contextMap);
			sqlMapClient.update("rentFinance.updateClaim",context.contextMap);//更新认领款表
		} else if(RentFinanceUtil.RENT_TYPE.REFUND.toString().equals(context.contextMap.get("type"))) {
			billId=(String)sqlMapClient.queryForObject("rentFinance.getBillIdByRefund",context.contextMap);
			sqlMapClient.update("rentFinance.updateRefund",context.contextMap);//更新退款表
		}
		context.contextMap.put("billId",billId);
		if("1".equals(context.contextMap.get("code").toString())) {//1是通过
			context.contextMap.put("decomposeStatus",2);//通过在T_RENT_DECOMPOSE中是2
			sqlMapClient.update("rentFinance.confirmOrRejectDecompose",context.contextMap);
			context.contextMap.put("decomposeStatus",1);//通过在T_RENT_RECORD中是1
			sqlMapClient.update("rentFinance.commitRecord",context.contextMap);
		} else {
			context.contextMap.put("decomposeStatus",-1);
			sqlMapClient.update("rentFinance.confirmOrRejectDecompose",context.contextMap);
			sqlMapClient.update("rentFinance.commitRecord",context.contextMap);
		}
	}
	
	public void addRemark(Context context) throws Exception {
		this.rentFinanceDAO.addRemark(context);
	}
	
	//保存暂收款水单
	public void saveFileToDiskAndDB(Context context,SqlMapClient sqlMapClient) throws Exception {
		
		List<FileItem> fileItems=(List<FileItem>)context.contextMap.get("uploadList");//获得上传的文件
		String rootPath=this.getUploadPath("transferCertificate");
		String filePath="";
		String fileName="";
		for(Iterator<FileItem> iterator=fileItems.iterator();iterator.hasNext();) {
			FileItem fileItem=iterator.next();
			String fileFormat=fileItem.getName().substring(fileItem.getName().lastIndexOf(".")+1);//获得文件类型
			if(rootPath!=null) {
				File realPath=new File(rootPath+File.separator+new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())+File.separator+fileFormat);
				if(!realPath.exists())realPath.mkdirs();
				
				fileName=FileExcelUpload.getNewFileName();
				File uploadFile=new File(realPath.getPath()+File.separator+fileName+"."+fileFormat);
				filePath=File.separator+new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())+File.separator+fileFormat+File.separator+fileName+"."+fileFormat;
				fileItem.write(uploadFile);
				fileItem.getInputStream().close();
				
				context.contextMap.put("filePath",filePath);//存储到数据库中
				context.contextMap.put("fileName",fileItem.getName());
				context.contextMap.put("title","暂收款水单附件");
				
				sqlMapClient.insert("rentFinance.insertReceipt",context.contextMap);
			}
		}
		
	}
	
	private String getUploadPath(String xmlName) throws Exception {
		String path=null;
		SAXReader reader=new SAXReader();
		Document document=reader.read(Resources.getResourceAsReader("config/upload-config.xml"));
		Element root=document.getRootElement();
		List nodes=root.elements("action");
		for(Iterator it=nodes.iterator();it.hasNext();) {
			Element element=(Element)it.next();
			Element nameElement=element.element("name");
			String s=nameElement.getText();
			if (xmlName.equals(s)) {
				Element pathElement=element.element("path");
				path=pathElement.getText();
			}
		}
		return path;
	}
	
	public List<Map<String,Object>> getUploadFileList(Context context) throws Exception {
		return this.rentFinanceDAO.getUploadFileList(context);
	}
	
	public void viewFile(Context context,Map<String,Object> fileInfo) throws Exception {
		String path=this.getUploadPath("transferCertificate")+fileInfo.get("PATH");
		File file = new File(path);
		context.response.reset();
		context.response.setCharacterEncoding("gb2312");
		OutputStream output=context.response.getOutputStream();
		context.response.setHeader("Content-Disposition","attachment;filename="+new String(fileInfo.get("FILE_NAME").toString().getBytes("gb2312"),"iso8859-1"));
		FileInputStream fis=new FileInputStream(file);
		byte[] b=new byte[1024];
		int i=0;
		while((i=fis.read(b))!=-1) {
			output.write(b,0,i);
		}
		output.write(b,0,b.length);
		output.flush();
		context.response.flushBuffer();
		output.close();
	}
	
	public List<Map<String,Object>> getCashReport(Context context) throws Exception {
		
		List<Map<String,Object>> resultList=this.rentFinanceDAO.getCashReport(context);
		String recpId=null;
		String periodNum=null;
		double decomposePrice=0;//此次租金分解金额
		double reduceOwnPrice=0;//此期租金总共分解的金额
		double renPrice=0;//利息
		double irrMonthPrice=0;//应付租金
		Map<String,Object> param=new HashMap<String,Object>();
		for(int i=0;i<resultList.size();i++) {
			if(RentFinanceUtil.RENT_TYPE.RENT.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				if(resultList.get(i).get("DECOMPOSE_TYPE")==null||"1".equals(resultList.get(i).get("DECOMPOSE_TYPE").toString())) {
					continue;//冲红类型不需要拆分本金,利息
				}
				recpId=resultList.get(i).get("RECP_ID").toString();
				periodNum=resultList.get(i).get("PERIOD_NUM").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("recpId",recpId);
				param.put("periodNum",periodNum);
				param=this.rentFinanceDAO.getOwnPriceAndRenPrice(param);
				reduceOwnPrice=Double.valueOf(param.get("REDUCE_OWN_PRICE").toString());
				renPrice=Double.valueOf(param.get("REN_PRICE").toString());
				if(reduceOwnPrice-decomposePrice==0) {//此期之前未销过
					if(decomposePrice>=renPrice) {//如果分解租金大于等于本期总利息,则此次分解利息为全额销帐,分解的本金为decomposePrice-renPrice
						resultList.get(i).put("REN_PRICE",renPrice);
						resultList.get(i).put("OWN_PRICE",decomposePrice-renPrice);
					} else {//销的租金小于利息金额,则只销利息,本金销的金额为0
						resultList.get(i).put("REN_PRICE",decomposePrice);
						resultList.get(i).put("OWN_PRICE",0);
					}
				} else if(reduceOwnPrice-decomposePrice>0) {//此期之前有销过
					if(reduceOwnPrice-decomposePrice-renPrice>=0) {//如果之前销帐金额已把利息销完
						resultList.get(i).put("REN_PRICE",0);
						resultList.get(i).put("OWN_PRICE",decomposePrice);
					} else {
						double restRenPrice=renPrice-(reduceOwnPrice-decomposePrice);//剩余未销的利息
						if(decomposePrice>=restRenPrice) {
							resultList.get(i).put("REN_PRICE",restRenPrice);
							resultList.get(i).put("OWN_PRICE",decomposePrice-restRenPrice);
						} else {
							resultList.get(i).put("REN_PRICE",decomposePrice);
							resultList.get(i).put("OWN_PRICE",0);
						}
					}
				}
			} else if(RentFinanceUtil.RENT_TYPE.CLAIM.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				String incomeId=resultList.get(i).get("INCOME_ID").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("incomeId",incomeId);
				param.put("amount",decomposePrice);
				resultList.get(i).put("REASON",this.rentFinanceDAO.getClaimCause(param));
			} else if(RentFinanceUtil.RENT_TYPE.REFUND.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				String incomeId=resultList.get(i).get("INCOME_ID").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("incomeId",incomeId);
				param.put("amount",decomposePrice);
				resultList.get(i).put("REASON",this.rentFinanceDAO.getRefundCause(param));
			} else {
				continue;
			}
		}
		return resultList;
	}
	
	public List<Map<String,Object>> getFundReport(Context context) throws Exception {
		
		List<Map<String,Object>> resultList=this.rentFinanceDAO.getFundReport(context);
		String recpId=null;
		String periodNum=null;
		double decomposePrice=0;//此次租金分解金额
		double reduceOwnPrice=0;//此期租金总共分解的金额
		double renPrice=0;//利息
		double irrMonthPrice=0;//应付租金
		Map<String,Object> param=new HashMap<String,Object>();
		for(int i=0;i<resultList.size();i++) {
			if(RentFinanceUtil.RENT_TYPE.RENT.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				if(resultList.get(i).get("DECOMPOSE_TYPE")==null||"1".equals(resultList.get(i).get("DECOMPOSE_TYPE").toString())) {
					continue;//冲红类型不需要拆分本金,利息
				}
				recpId=resultList.get(i).get("RECP_ID").toString();
				periodNum=resultList.get(i).get("PERIOD_NUM").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("recpId",recpId);
				param.put("periodNum",periodNum);
				param=this.rentFinanceDAO.getOwnPriceAndRenPrice(param);
				reduceOwnPrice=Double.valueOf(param.get("REDUCE_OWN_PRICE").toString());
				renPrice=Double.valueOf(param.get("REN_PRICE").toString());
				if(reduceOwnPrice-decomposePrice==0) {//此期之前未销过
					if(decomposePrice>=renPrice) {//如果分解租金大于等于本期总利息,则此次分解利息为全额销帐,分解的本金为decomposePrice-renPrice
						resultList.get(i).put("REN_PRICE",renPrice);
						resultList.get(i).put("OWN_PRICE",decomposePrice-renPrice);
					} else {//销的租金小于利息金额,则只销利息,本金销的金额为0
						resultList.get(i).put("REN_PRICE",decomposePrice);
						resultList.get(i).put("OWN_PRICE",0);
					}
				} else if(reduceOwnPrice-decomposePrice>0) {//此期之前有销过
					if(reduceOwnPrice-decomposePrice-renPrice>=0) {//如果之前销帐金额已把利息销完
						resultList.get(i).put("REN_PRICE",0);
						resultList.get(i).put("OWN_PRICE",decomposePrice);
					} else {
						double restRenPrice=renPrice-(reduceOwnPrice-decomposePrice);//剩余未销的利息
						if(decomposePrice>=restRenPrice) {
							resultList.get(i).put("REN_PRICE",restRenPrice);
							resultList.get(i).put("OWN_PRICE",decomposePrice-restRenPrice);
						} else {
							resultList.get(i).put("REN_PRICE",decomposePrice);
							resultList.get(i).put("OWN_PRICE",0);
						}
					}
				}
			} else if(RentFinanceUtil.RENT_TYPE.CLAIM.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				String incomeId=resultList.get(i).get("INCOME_ID").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("incomeId",incomeId);
				param.put("amount",decomposePrice);
				resultList.get(i).put("REASON",this.rentFinanceDAO.getClaimCause(param));
			} else if(RentFinanceUtil.RENT_TYPE.REFUND.toString().equals(resultList.get(i).get("BILL_CODE"))) {
				String incomeId=resultList.get(i).get("INCOME_ID").toString();
				decomposePrice=Double.valueOf(resultList.get(i).get("DECOMPOSE_PRICE").toString());
				param.put("incomeId",incomeId);
				param.put("amount",decomposePrice);
				resultList.get(i).put("REASON",this.rentFinanceDAO.getRefundCause(param));
			} else {
				continue;
			}
		}
		return resultList;
	}
	
	public List<Map<String,Object>> getBalanceReport(Context context) throws Exception {
		return this.rentFinanceDAO.getBalanceReport(context);
	}
	
	public List<Map<String,Object>> getDeptList() throws Exception {
		return this.rentFinanceDAO.getDeptList();
	}
	
	
	//销帐日报表JOB
	public void batchJobForCashFund() throws Exception {
		if(super.isWorkingDay()) {
			Context context=new Context(null,null,null);
			List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
			try {
				ReportDateTo reportDateTo=this.rentFinanceDAO.getDateReportMap();//获得属于哪个结账周期
				dataList.addAll(this.getCashReport(context));//获得现金销帐
				dataList.addAll(this.getFundReport(context));//获得暂收款销帐
				for(int i=0;i<dataList.size();i++) {
					dataList.get(i).put("FINANCE_DATE",String.valueOf(reportDateTo.getYear())+"-"+String.valueOf(reportDateTo.getMonth()));
					this.rentFinanceDAO.insertDecomposeDailyReport(dataList.get(i));
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
	public void batchJobForBalance() throws Exception {
		if(super.isWorkingDay()) {
			Context context=new Context(null,null,null);
			List<Map<String,Object>> dataList=new ArrayList<Map<String,Object>>();
			try {
				ReportDateTo reportDateTo=this.rentFinanceDAO.getDateReportMap();//获得属于哪个结账周期
				dataList.addAll(this.getBalanceReport(context));//获得暂收款余额变动
				for(int i=0;i<dataList.size();i++) {
					dataList.get(i).put("FINANCE_DATE",String.valueOf(reportDateTo.getYear())+"-"+String.valueOf(reportDateTo.getMonth()));
					this.rentFinanceDAO.insertDecomposeDailyDynamicReport(dataList.get(i));
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	public List<Map<String,Object>> getHistoryCashReport(Context context) throws Exception {
		return this.rentFinanceDAO.getHistoryCashReport(context);
	}
	public List<Map<String,Object>> getHistoryFundReport(Context context) throws Exception {
		return this.rentFinanceDAO.getHistoryFundReport(context);
	}
	public List<Map<String,Object>> getHistoryBalanceReport(Context context) throws Exception {
		return this.rentFinanceDAO.getHistoryBalanceReport(context);
	}
	
	public static List<Map<String,Object>> getHistoryCashReport(String queryDate,String companyCode) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("queryDate",queryDate);
		param.put("companyCode",companyCode);
		return (List<Map<String,Object>>)DataAccessor.query("rentFinance.getHistoryCashReport",param,RS_TYPE.LIST);
	}
	public static List<Map<String,Object>> getHistoryFundReport(String queryDate,String companyCode) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("queryDate",queryDate);
		param.put("companyCode",companyCode);
		return (List<Map<String,Object>>)DataAccessor.query("rentFinance.getHistoryFundReport",param,RS_TYPE.LIST);
	}
	public static List<Map<String,Object>> getHistoryBalanceReport(String queryDate,String companyCode) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("queryDate",queryDate);
		if(!StringUtils.isEmpty(companyCode)){
			List<Map<String,String>> companys = LeaseUtil.getCompanys();
			for(Map<String,String> c:companys){
				if(c.get("code").equals(companyCode)){
					param.put("companyName", c.get("name"));
				}
			}
		}
		return (List<Map<String,Object>>)DataAccessor.query("rentFinance.getHistoryBalanceReport",param,RS_TYPE.LIST);
	}
	
	public static List<Map<String,Object>> getHistoryCashMonthReport(String monthDate,String companyCode) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("monthDate",monthDate);
		ReportDateTo reportDateTo=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(monthDate.split("-")[0]),
				Integer.valueOf(monthDate.split("-")[1]));
		param.put("startDate",reportDateTo.getBeginTime());
		param.put("endDate",reportDateTo.getEndTime());
		param.put("companyCode",companyCode);
		return (List<Map<String,Object>>)DataAccessor.query("rentFinance.getHistoryCashReport",param,RS_TYPE.LIST);
	}
	public static List<Map<String,Object>> getHistoryFundMonthReport(String monthDate,String companyCode) throws Exception {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("monthDate",monthDate);
		ReportDateTo reportDateTo=ReportDateUtil.getDateByYearAndMonth(Integer.valueOf(monthDate.split("-")[0]),
				Integer.valueOf(monthDate.split("-")[1]));
		param.put("startDate",reportDateTo.getBeginTime());
		param.put("endDate",reportDateTo.getEndTime());
		param.put("companyCode",companyCode);
		return (List<Map<String,Object>>)DataAccessor.query("rentFinance.getHistoryFundReport",param,RS_TYPE.LIST);
	}
	
	//保证金B自动抵充冲账
	public void autoDecomposePledgeB() {
		
		SqlMapClient sqlMapClient=null;
		try {
			List<Map<String,Object>> pledgeBList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getAutoDecomposeList",null,RS_TYPE.LIST);
			
			List<Map<String,Object>> recpDetailList=null;
			Map<String,Object> param=new HashMap<String,Object>();
			Map<String,Object> pledgeRecord=new HashMap<String,Object>();
			
			sqlMapClient=DataAccessor.getSession();
			for(int i=0;i<pledgeBList.size();i++) {
				param.put("RECP_ID",pledgeBList.get(i).get("RECP_ID"));
				recpDetailList=(List<Map<String,Object>>)DataAccessor.query("rentFinance.getAutoDecomposeDetailList",param,RS_TYPE.LIST);
				if(recpDetailList.size()>Integer.valueOf(pledgeBList.get(i).get("PLEDGE_LAST_PERIOD").toString())) {
					//还未到此合同最后抵充开始的期数,所以跳过
					continue;
				} else {
					Map<String,Object> rent=recpDetailList.get(0);
					String payDate=rent.get("PAY_DATE").toString();
					if(DateUtil.strToDate(payDate,"yyyy-MM-dd").compareTo(DateUtil.strToDate(DateUtil.getCurrentDate(),"yyyy-MM-dd"))==0) {//当天日期为支付日期,则自动冲账
					pledgeRecord=(Map<String,Object>)DataAccessor.query("rentFinance.getPledgeBRecord",param,RS_TYPE.OBJECT);
					if(pledgeRecord==null||pledgeRecord.get("BILL_ID")==null) {
						continue;
					}
					param.put("BILL_ID",pledgeRecord.get("BILL_ID"));
					
					sqlMapClient.startTransaction();
					//插入红冲单
					param.put("hasRedDecompose",1);
					sqlMapClient.update("rentFinance.updateRedFlag",param);
					pledgeRecord.put("billId",pledgeRecord.get("BILL_ID"));
					Long billId=(Long)sqlMapClient.insert("rentFinance.insertRedDecompose",pledgeRecord);
					pledgeRecord.put("bill_id",billId);
					sqlMapClient.insert("rentFinance.insertRed",pledgeRecord);
					String taxPlanCode=pledgeBList.get(i).get("TAX_PLAN_CODE").toString();//获得税费方案
					
					if(Constants.TAX_PLAN_CODE_2.equals(taxPlanCode)) {//增值税税费方案
						Map<String,Object> insertMap=new HashMap<String,Object>();
						String irrMonthPrice=rent.get("IRR_MONTH_PRICE").toString();
						String valueAddTax=rent.get("VALUE_ADDED_TAX").toString();
						
						insertMap.put("income_id",pledgeRecord.get("INCOME_ID"));
						insertMap.put("recp_id",pledgeRecord.get("RECP_ID"));
						insertMap.put("period_num",rent.get("PERIOD_NUM"));
						insertMap.put("pay_date",rent.get("PAY_DATE"));
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.VALUE_ADD_TAX);
						insertMap.put("should_price",valueAddTax);
						insertMap.put("decompose_price","-"+valueAddTax);
						insertMap.put("decompose_from",pledgeRecord.get("DECOMPOSE_FROM"));
						insertMap.put("decompose_status",2);
						insertMap.put("decompose_type",0);
						insertMap.put("has_red_decompose",0);
						insertMap.put("s_employeeId",184);
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",1);
						insertMap.put("table","T_RENT_DECOMPOSE");
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);
						insertMap.put("should_price",irrMonthPrice);
						insertMap.put("decompose_price","-"+irrMonthPrice);
						insertMap.put("decompose_status",2);
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",1);
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						
						//更新支付表
						insertMap.put("RECP_ID",pledgeRecord.get("RECP_ID"));
						insertMap.put("PERIOD_NUM",rent.get("PERIOD_NUM"));
						insertMap.put("REDUCE_OWN_PRICE",pledgeRecord.get("SHOULD_PRICE"));
						sqlMapClient.update("rentFinance.updateReduceOwnPrice",insertMap);
						
						//插入日志
						sqlMapClient.insert("rentFinance.insertPledgeBLog",insertMap);
					} else {//非增值税税费方案
						Map<String,Object> insertMap=new HashMap<String,Object>();
						insertMap.put("income_id",pledgeRecord.get("INCOME_ID"));
						insertMap.put("recp_id",pledgeRecord.get("RECP_ID"));
						insertMap.put("period_num",rent.get("PERIOD_NUM"));
						insertMap.put("pay_date",rent.get("PAY_DATE"));
						insertMap.put("bill_code",RentFinanceUtil.RENT_TYPE.RENT);
						insertMap.put("should_price",pledgeRecord.get("SHOULD_PRICE"));
						insertMap.put("decompose_price",pledgeRecord.get("DECOMPOSE_PRICE"));
						insertMap.put("decompose_from",pledgeRecord.get("DECOMPOSE_FROM"));
						insertMap.put("decompose_status",2);
						insertMap.put("decompose_type",0);
						insertMap.put("has_red_decompose",0);
						insertMap.put("s_employeeId",184);
						billId=(Long)sqlMapClient.insert("rentFinance.insertRentDecompose",insertMap);
						insertMap.put("bill_id",billId);
						insertMap.put("decompose_status",1);
						insertMap.put("table","T_RENT_DECOMPOSE");
						sqlMapClient.insert("rentFinance.insertRent",insertMap);
						
						//更新支付表
						insertMap.put("RECP_ID",pledgeRecord.get("RECP_ID"));
						insertMap.put("PERIOD_NUM",rent.get("PERIOD_NUM"));
						insertMap.put("REDUCE_OWN_PRICE",pledgeRecord.get("SHOULD_PRICE"));
						sqlMapClient.update("rentFinance.updateReduceOwnPrice",insertMap);
						
						//插入日志
						sqlMapClient.insert("rentFinance.insertPledgeBLog",insertMap);
					}
					sqlMapClient.commitTransaction();
					}
				}
			}
		} catch (Exception e) {
			try {
				sqlMapClient.endTransaction();
			} catch (SQLException e1) {
			}
		}
	}
	
	public Map<String,Object> getCashIncome(Context context) throws Exception {
		return this.rentFinanceDAO.getCashIncome(context);
	}
	
	public List<Map<String,Object>> getCashFlow(Context context) throws Exception {
		return this.rentFinanceDAO.getCashFlow(context);
	}
	
	
	/**
	 * 退款单
	 * @param detail_id
	 * @return
	 */
	public static Map<String,Object> exportFundReturnDetail(String  detail_id){
		Map paramMap= new HashMap();
		detail_id = StringUtils.isEmpty(detail_id) ? null : detail_id.trim();
		paramMap.put("detail_id", detail_id);
		Map<String, Object> fundReturn = null;
		Map<String, Object> financeIncome = null;
		List<Map<String, Object>> financeBillList=null;
		try {
			fundReturn = (Map<String, Object>) DataAccessor.query("rentFinance.getFundReturnForPrint", paramMap, RS_TYPE.MAP);
			financeIncome = (Map<String, Object>) DataAccessor.query("rentFinance.queryFinanceIncome", fundReturn, RS_TYPE.MAP);
			financeIncome.put("remainingMoney", LeaseUtil.getRealRemainingMoneyByIncomeId(String.valueOf(fundReturn.get("INCOME_ID"))));
			financeBillList = (List<Map<String, Object>>) DataAccessor.query("rentFinance.queryFinanceBillDetail", fundReturn, RS_TYPE.LIST);
			//fundReturn.put("REFUND_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(fundReturn.get("REFUND_DATE")));
			//financeIncome.put("INCOME_DATE", new SimpleDateFormat("yyyy年 MM月 dd日").format(financeIncome.get("INCOME_DATE")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		fundReturn.put("financeIncome", financeIncome);
		fundReturn.put("financeBillList", financeBillList);
		return fundReturn;
	}
	
	
	
	
	
	//可结清案件明细提醒,90天以上和以下2封邮件,发送给老大们
	public void sendSettlementMail() {
		
		DecimalFormat f=new DecimalFormat("##,##0.00");
		Context context=new Context(null,null,null);
		List<Map<String,Object>> resultListLess90Day=null;
		List<Map<String,Object>> resultListGreater90Day=null;
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			
			if(!super.isWorkingDay()) {
				return;
			}
			
			param.put("less","Y");
			resultListLess90Day=this.rentFinanceDAO.getSettlementMailContent(param);//小于90天
			param.remove("less");
			for(int i=0;i<resultListLess90Day.size();i++) {
				context.contextMap.put("recpId",resultListLess90Day.get(i).get("RECP_ID"));
				resultListLess90Day.get(i).put("FINE",//罚息栏位
						LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")==null?f.format(0.00d):
							f.format(LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")));
				resultListLess90Day.get(i).put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));//留购价栏位
				resultListLess90Day.get(i).put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));//法务费栏位
			}
			MailSettingTo mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("90天以下可结清案件");
			mailSettingTo.setEmailContent(this.getSettlementMailContent(resultListLess90Day));
			this.mailUtilService.sendMail(203,mailSettingTo);
			
			param.put("greater","Y");
			resultListGreater90Day=this.rentFinanceDAO.getSettlementMailContent(param);//大于90天
			param.remove("greater");
			for(int i=0;i<resultListGreater90Day.size();i++) {
				context.contextMap.put("recpId",resultListGreater90Day.get(i).get("RECP_ID"));
				resultListGreater90Day.get(i).put("FINE",
						LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")==null?f.format(0.00d):
							f.format(LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")));
				resultListGreater90Day.get(i).put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));
				resultListGreater90Day.get(i).put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));
			}
			
			mailSettingTo=new MailSettingTo();
			mailSettingTo.setEmailSubject("90天以上可结清案件");
			mailSettingTo.setEmailContent(this.getSettlementMailContent(resultListGreater90Day));
			this.mailUtilService.sendMail(400,mailSettingTo);
		} catch (Exception e) {
			
		}
	}
	
	//给各个办事处发送可结清邮件
	public void sendSettlementMailForEveryCmpy() {
		
		DecimalFormat f=new DecimalFormat("##,##0.00");
		Context context=new Context(null,null,null);
		
		List<Map<String,Object>> cmpyList=null;//办事处
		List<Map<String,Object>> resultList=null;
		Map<String,Object> param=new HashMap<String,Object>();
		MailSettingTo mailSettingTo=null;
		try {
			if(!super.isWorkingDay()) {
				return;
			}
			cmpyList=super.getAllDecp();
			for(int i=0;i<cmpyList.size();i++) {
				param.put("cmpyId",cmpyList.get(i).get("DECP_ID"));
				resultList=this.rentFinanceDAO.getSettlementMailContent(param);
				
				if(resultList==null||resultList.size()==0) {
					continue;
				}
				for(int j=0;j<resultList.size();j++) {
					context.contextMap.put("recpId",resultList.get(j).get("RECP_ID"));
					resultList.get(j).put("FINE",//罚息栏位
							LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")==null?f.format(0.00d):
								f.format(LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")));
					resultList.get(j).put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));//留购价栏位
					resultList.get(j).put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));//法务费栏位
				}
				
				mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailSubject("可结清案件");
				mailSettingTo.setEmailContent(this.getSettlementMailContent(resultList));
				if(Constants.CMPY_17.equals(cmpyList.get(i).get("DECP_ID").toString())) {//苏州设备
					this.mailUtilService.sendMail(204,mailSettingTo);
				} else if(Constants.CMPY_2.equals(cmpyList.get(i).get("DECP_ID").toString())) {//昆山设备
					this.mailUtilService.sendMail(205,mailSettingTo);
				} else if(Constants.CMPY_7.equals(cmpyList.get(i).get("DECP_ID").toString())) {//南京设备
					this.mailUtilService.sendMail(207,mailSettingTo);
				} else if(Constants.CMPY_13.equals(cmpyList.get(i).get("DECP_ID").toString())) {//上海设备
					this.mailUtilService.sendMail(206,mailSettingTo);
				} else if(Constants.CMPY_3.equals(cmpyList.get(i).get("DECP_ID").toString())) {//东莞设备
					this.mailUtilService.sendMail(210,mailSettingTo);
				} else if(Constants.CMPY_8.equals(cmpyList.get(i).get("DECP_ID").toString())) {//佛山设备
					this.mailUtilService.sendMail(211,mailSettingTo);
				} else if(Constants.CMPY_11.equals(cmpyList.get(i).get("DECP_ID").toString())) {//厦门设备
					this.mailUtilService.sendMail(212,mailSettingTo);
				} else if(Constants.CMPY_9.equals(cmpyList.get(i).get("DECP_ID").toString())) {//重庆设备
					this.mailUtilService.sendMail(208,mailSettingTo);
				} else if(Constants.CMPY_14.equals(cmpyList.get(i).get("DECP_ID").toString())) {//成都设备
					this.mailUtilService.sendMail(209,mailSettingTo);
				} else if(Constants.CMPY_16.equals(cmpyList.get(i).get("DECP_ID").toString())) {//苏州商用车
					this.mailUtilService.sendMail(213,mailSettingTo);
				} else{			
					List list = DictionaryUtil.getDictionary("可结清案件");//后续的只需在字典表中配置
					if(list!=null){
						for(int j=0;j<list.size();j++){
							Map deptInfo = (Map) list.get(j);
							String decpId = (String) deptInfo.get("FLAG");
							String emailType = (String) deptInfo.get("CODE");	
							if(decpId.equals(cmpyList.get(i).get("DECP_ID").toString())){
								this.mailUtilService.sendMail(Integer.parseInt(emailType),mailSettingTo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	public void sendSettlementMailForEverySales() {
		
		DecimalFormat f=new DecimalFormat("##,##0.00");
		Context context=new Context(null,null,null);
		List<Map<String,Object>> resultList=null;
		List<Map<String,Object>> salesList=null;//业务员
		Map<String,Object> param=new HashMap<String,Object>();
		MailSettingTo mailSettingTo=null;
		try {
			if(!super.isWorkingDay()) {
				return;
			}
			salesList=this.rentFinanceDAO.getSalesList();
			for(int i=0;salesList!=null&&i<salesList.size();i++) {
				param.put("userId",salesList.get(i).get("ID"));
				resultList=this.rentFinanceDAO.getSettlementMailContent(param);
				if(resultList==null||resultList.size()==0) {
					continue;
				}
				for(int j=0;j<resultList.size();j++) {
					context.contextMap.put("recpId",resultList.get(j).get("RECP_ID"));
					resultList.get(j).put("FINE",//罚息栏位
							LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")==null?f.format(0.00d):
								f.format(LeaseUtil.getTotalFineByRecpIdForDecompose(context.contextMap.get("recpId").toString()).get("FINE")));
					resultList.get(j).put("STAY_FEE",f.format(this.rentFinanceDAO.getSettlementStayFee(context)));//留购价栏位
					resultList.get(j).put("LAW_FEE",f.format(this.rentFinanceDAO.getSettlementLawFee(context)));//法务费栏位
				}
				mailSettingTo=new MailSettingTo();
				mailSettingTo.setEmailSubject("可结清案件");
				mailSettingTo.setEmailContent(this.getSettlementMailContent(resultList));
				if(StringUtils.isEmpty(salesList.get(i).get("EMAIL"))) {//如果没有邮件地址跳过不发送邮件
					continue;
				}
				mailSettingTo.setEmailTo(salesList.get(i).get("EMAIL").toString());
				this.mailUtilService.sendMail(mailSettingTo);
			}
			
		} catch (Exception e) {
			
		}
	}
	
	private String getSettlementMailContent(List<Map<String,Object>> dataList) {
		
		StringBuffer mailContent=new StringBuffer();
		mailContent.append("<html><head></head>");
		mailContent.append("<style>.grid_table th {"+
							"border:solid #A6C9E2;"+
							"border-width:0 1px 1px 0;"+
							"background-color: #E1EFFB;"+
							"padding : 2;"+
							"margin : 1;"+
							"font-weight: bold;"+
							"text-align: center;"+
							"color: #2E6E9E;"+
							"height: 28px;"+
							"font-size: 14px;"+
							"font-family: '微软雅黑';"+
							"}" +
							".grid_table td {"+
							"border:solid #A6C9E2;"+
						    "border-width:0 1px 1px 0;"+
						    "text-align: center;"+
							"white-space: nowrap;"+
							"overflow: hidden;"+
							"background-color: #FFFFFF;"+
							"padding : 5px 5px;"+
							"font-size: 12px;"+
							"font-weight: normal;"+
							"color: black;"+
							"font-family: '微软雅黑';"+
							"}" +
							".ff {font-size: 13px;font-family: '微软雅黑';}</style><body>");
		mailContent.append("<font class='ff'>下午好：<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以下为可结清案件明细表，请参阅</font><br>");
		mailContent.append("<table class='grid_table'>" +
								"<tr>" +
									"<th>序号</th>" +
									"<th>合同号</th>" +
									"<th>客户名称</th>" +
									"<th>办事处</th>" +
									"<th>业务员</th>" +
									"<th>租赁期数</th>" +
									"<th>已缴期数</th>" +
									"<th>未缴罚息</th>" +
									"<th>未缴法务费用</th>" +
									"<th>未缴期末购买金</th>" +
									"<th>最后一期来款时间</th>" +
									"<th>距今天数</th>" +
								"</tr>");
		
		for(int i=0;i<dataList.size();i++) {
			mailContent.append("<tr>" +
									"<td>"+(i+1)+"</td>" +
									"<td>"+dataList.get(i).get("RECP_CODE").toString().substring(0,14)+"</td>" +
									"<td>"+dataList.get(i).get("CUST_NAME")+"</td>" +
									"<td>"+dataList.get(i).get("DECP_NAME_CN")+"</td>" +
									"<td>"+dataList.get(i).get("NAME")+"</td>"+
									"<td>"+dataList.get(i).get("LEASE_PERIOD")+"</td>" +
									"<td>"+dataList.get(i).get("LEASE_PERIOD")+"</td>" +
									"<td style='text-align:right'>￥"+dataList.get(i).get("FINE")+"</td>" +
									"<td style='text-align:right'>￥"+dataList.get(i).get("LAW_FEE")+"</td>" +
									"<td style='text-align:right'>￥"+dataList.get(i).get("STAY_FEE")+"</td>" +
									"<td>"+dataList.get(i).get("INCOME_DATE")+"</td>" +
									"<td style='text-align:right'>"+dataList.get(i).get("DAY")+"</td>" +
							   "</tr>");
		}
		mailContent.append("</table></body></html>");
		
		return mailContent.toString();
	}
	
	public void updateIncomeInfo(Context context) throws Exception {
		this.rentFinanceDAO.updateIncomeInfo(context);
	}
	
	@Transactional
	public void batchjobForSettlementPayment() throws Exception {
		List<String> recpList=this.rentFinanceDAO.getSettlementRecpId();
		Map<String,Object> param=new HashMap<String,Object>();
		for(int i=0;recpList!=null&&i<recpList.size();i++) {
			param.put("RECP_ID",recpList.get(i));
			this.rentFinanceDAO.insertSettlementPayment(param);
			this.rentFinanceDAO.updateSettlementPayment(param);
		}
	}
	
}
