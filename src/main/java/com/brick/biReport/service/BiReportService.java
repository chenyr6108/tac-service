package com.brick.biReport.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dom4j.Document;
import com.brick.service.core.AService;
import com.brick.service.core.DataAccessor;
import com.brick.service.entity.Context;

public class BiReportService extends AService {
	
	
	public void createAllXml(Context context){
		fahuoe(context);
		yingshou(context);
		yinshouShiyebu(context);
		tFen(context); 
		System.out.println("Loading R0 Data Complate!");
		zongjine(context); 
		allCompany(context); 
		fenlei(context); 
		System.out.println("Loading R2 Data Complate!");
	}
	
	
	/**
	 * R0  查询 已收/未收 租金
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void fahuoe(Context context) {
		Map outputMap = new HashMap();
		try {
			Map fahuoeMap = (Map) DataAccessor.query(
					"biReport.fahuoe", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("weishou", fahuoeMap.get("WEISHOU"));
			outputMap.put("yishou", fahuoeMap.get("YISHOU"));
			String path = context.request.getRealPath("/")
					+ "/biReport/modules/bi/data/R0/fahuoefenbu.xml";
			CreateXml.writeXml(CreateXml.createFahuoeXml(outputMap), path);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * R0  查询 应收租金构成
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void yingshou(Context context) {
		Map outputMap = new HashMap();
		try {
			Map yingshouMap = (Map) DataAccessor.query(
					"biReport.yingshou", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("weishou", yingshouMap.get("WEISHOU"));
			outputMap.put("shouru", yingshouMap.get("SHOURU"));
			outputMap.put("yingyeshui", yingshouMap.get("YINGYESHUI"));
			outputMap.put("yuqifaxi", yingshouMap.get("YUQIFAXI"));
			String path = context.request.getRealPath("/")
			+ "/biReport/modules/bi/data/R0/yingshouzujinfenbu.xml";
			CreateXml.writeXml(CreateXml.createYingshouXml(outputMap), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * R0 查询 分公司
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void yinshouShiyebu(Context context) {
		try {
			List yinshouShiyebuList = (List) DataAccessor.query(
					"biReport.yinshouShiyebu", context.contextMap,
					DataAccessor.RS_TYPE.LIST);		
			List<Object> decpNameList = new ArrayList();
			List<Object> weishouList = new ArrayList();
			for (int i = 0 ; i < yinshouShiyebuList.size() ; i++ ){
				Map m = (Map) yinshouShiyebuList.get(i);
				decpNameList.add(m.get("DECP_NAME_CN"));
				weishouList.add(m.get("WEISHOU"));
			}
			String path = context.request.getRealPath("/")
			+ "/biReport/modules/bi/data/R0/yingshouzujinshiyebu.xml";
			CreateXml.writeXml(CreateXml.createYinshouShiyebuXml(decpNameList,weishouList), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * R0 分公司 表格显示
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void tFen(Context context) {
		try {
			List yinshouShiyebuList = (List) DataAccessor.query(
					"biReport.yinshouShiyebu", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			String basePath = context.request.getRealPath(""); 
			CreateHtml.writeHtml(basePath+"/biReport/modules/bi/contents", "R-0Table.html", CreateFenHtml(yinshouShiyebuList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * R0 分公司 生成表格html
	 * 
	 * @param context
	 */
	public static String CreateFenHtml(List yinshouShiyebuList)  {
		List<Map<String,Object>> list = yinshouShiyebuList;
		NumberFormat format = NumberFormat.getCurrencyInstance();
		StringBuffer buf = new StringBuffer();
		buf.append("\t\t<table  class='tab-list' style='margin: 0 auto;width:98%;'>\n" +
				"\t\t<thead>\n" + 
				"\t\t\t<tr class='ui-widget-header'>\n" + 
				"\t\t\t\t<th>分公司</th>\n" + 
				"\t\t\t\t<th>应收租金</th>\n" + 
				"\t\t\t</tr>\n" + 
				"\t\t</thead>\n" + 
				"\t\t<tbody>\n"); 
		for (Map<String, Object> map : list) {
			buf.append("<tr>\n"+
					"\t\t\t\t<td align='center' height='26px'>"+map.get("DECP_NAME_CN")+"</td>\n" +
					"\t\t\t\t<td align='right'>"+format.format(map.get("WEISHOU"))+"</td>\n" +
					"\t\t\t</tr>\n");
		}
		buf.append("</tboday>\n</table>"); 
		return buf.toString();
	}
	/**
	 * R1 总金额
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void zongjine(Context context) {
		Map outputMap = new HashMap();
		try {
			Map zongjineMap = (Map) DataAccessor.query(
					"biReport.zongjine", context.contextMap,
					DataAccessor.RS_TYPE.MAP);
			outputMap.put("guanzhu", zongjineMap.get("GUANZHU"));
			outputMap.put("ciji", zongjineMap.get("CIJI"));
			outputMap.put("zhengchang", zongjineMap.get("ZHENGCHANG"));
			outputMap.put("keyi", zongjineMap.get("KEYI"));
			outputMap.put("sunshi", zongjineMap.get("SUNSHI"));
			String path = context.request.getRealPath("/")
			+ "/biReport/modules/bi/data/R1/wujifenlei_zongjine.xml";
			CreateXml.writeXml(CreateXml.createZongjineXml(outputMap), path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * R1 所有公司 
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void allCompany(Context context) {
		try {
			List companyList = (List) DataAccessor.query(
					"biReport.allcompany", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			List<Object> companyNameList = new ArrayList();
			for (int i = 0 ; i < companyList.size() ; i++ ){
				Map m = (Map) companyList.get(i);
				companyNameList.add(m.get("DECP_NAME_CN"));
			}
			String basePath = context.request.getRealPath(""); 
			CreateHtml.writeHtml(basePath+"/biReport/modules/bi/contents", "R-1Company.html", CreateCompanyHtml(companyNameList));
			CreateHtml.writeHtml(basePath+"/biReport/modules/bi/contents", "R-2Company.html", CreateR2CompanyHtml(companyNameList));
			CreateHtml.writeHtml(basePath+"/biReport/modules/bi/contents", "R-3Company.html", CreateCompanyR3Html(companyNameList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * R1 所有公司 生成html
	 * 
	 * @param context
	 */
	public static String CreateCompanyHtml(List companyNameList)  {
		StringBuffer buf = new StringBuffer();
		buf.append("\t\t<table  class='tab-list' style='width:100%;'>\n" +
				"\t\t<tbody>\n" + 
				"\t\t\t<tr>\n" + 
				"\t\t\t\t<td align='left' height='26px' colspan='2' />\n" + 
				"\t\t\t\t\t分公司\n"+
				"\t\t\t\t\t\t<input type='radio' id='division' name='division' value='0'checked='checked' onchange='$R1.changeFeiLei()'>全部"); 
		for (int i=0;i<companyNameList.size();i++) {
			buf.append("<br/>\t\t\t\t\t\t<input type='radio' id='division' name='division' value='"+(i+1)+"' onchange='$R1.changeFeiLei()'>"+companyNameList.get(i));
		}
		buf.append("</td>\n</tr>\n</tboday>\n</table>"); 
		return buf.toString();
	}
	
	
	
	/**
	 * R2 所有公司 生成html
	 * 
	 * @param context
	 */
	public static String CreateR2CompanyHtml(List companyNameList)  {
		StringBuffer buf = new StringBuffer();
		buf.append("<table width='100%'> <tbody>"); 
		if(companyNameList.size()%4==0){
			for (int i=0;i<companyNameList.size()/4;i++) {
				buf.append("<tr>");
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+1)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+1)+"'>"+ companyNameList.get(i));
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+2)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+2)+"'>"+ companyNameList.get(i+1));
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+3)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+3)+"'>"+ companyNameList.get(i+2));
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+4)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+4)+"'>"+ companyNameList.get(i+3));
				buf.append("</td>");
				buf.append("<tr>");
			}
		}else{
			for (int i=0;i<companyNameList.size()/4;i++) {
				buf.append("<tr>");
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+1)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+1)+"'>"+ companyNameList.get(i));
				buf.append("</td>");
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+2)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+2)+"'>"+ companyNameList.get(i+1));
				buf.append("</td>");
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+3)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+3)+"'>"+ companyNameList.get(i+2));
				buf.append("</td>");
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+4)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+4)+"'>"+ companyNameList.get(i+3));
				buf.append("</td>");
				buf.append("<tr>");
			}
			buf.append("<tr>");
			int n=(companyNameList.size()/4)*4;
			for (int i=n;i<companyNameList.size();i++) {
				buf.append("<td align='center'>");
				buf.append("<div id='R2_fusionCharts"+(i+1)+"'></div>");
				buf.append("<input name='divisionr2' type='radio'    onclick='$R2.dialog(1);'  value='"+(i+1)+"'>"+ companyNameList.get(i));
				buf.append("</td>");
			}
			for(int i=0;i<4-companyNameList.size()%4;i++){
				buf.append("<td align='center'>&nbsp;");
				buf.append("</td>");
			}
			buf.append("<tr>");
		}
		
		buf.append("</tbody> </table>"); 
		return buf.toString();
	}
	/**
	 * R3 所有公司 生成html
	 * 
	 * @param context
	 */
	public static String CreateCompanyR3Html(List companyNameList)  {
		StringBuffer buf = new StringBuffer();
		buf.append("<input type='radio'  name='divisionR3' value='0' checked='checked' onchange='$R3.changeCharts()'>全部");
		for (int i=0;i<companyNameList.size();i++) {
			buf.append("<input type='radio'  name='divisionR3' value='"+(i+1)+"' onchange='$R3.changeCharts()'>"+companyNameList.get(i)+"<img src='images/"+(i+1)+".jpg' />");
		}
		return buf.toString();
	}
	/**
	 * R1  五级分类
	 * 
	 * @param context
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public void fenlei(Context context) {
		Map outputMap = new HashMap();
		try {
			List<Map> fenleiList = (List<Map>) DataAccessor.query(
					"biReport.fenlei", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			List companyList = (List) DataAccessor.query(
					"biReport.allcompany", context.contextMap,
					DataAccessor.RS_TYPE.LIST);
			
			Set<String> setMonth = new HashSet<String>();
			List<String> monthList = new ArrayList<String>();
			for (Map map : fenleiList) {
				if (setMonth.add(String.valueOf(map.get("SHIJIAN")))) {
					monthList.add(String.valueOf(map.get("SHIJIAN")));
				}
			}
/*			System.out.println("------------------------");
			System.out.println(setMonth);
			System.out.println(monthList);
			System.out.println("------------------------");*/
			
			
			List<List<Map>> listAll = new ArrayList<List<Map>>();
			for (int i = 0;i<companyList.size();i++){
				Map m = (Map) companyList.get(i);
				String decpname = (String) m.get("DECP_NAME_CN");
				for(int x=0;x<fenleiList.size();x++){
					if (decpname.equals(String.valueOf(fenleiList.get(x).get("GONGSI")))) {
						List<Map> list = new ArrayList<Map>();
						list.add(fenleiList.get(x));
						listAll.add(list);
					}
				}
			}
			
/**/
			
			String basePath = context.request.getRealPath(""); 
			
			// =========资产变化趋势分析==================================================================================================
			try {
				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeCount(
							"资产变化趋势分析", listAll.get(i), "正常");
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei1" + (i + 1)
							+ ".xml");
				}
				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeCount(
							"资产变化趋势分析", listAll.get(i), "关注");
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei2" + (i + 1)
							+ ".xml");
				}
				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeCount(
							"资产变化趋势分析", listAll.get(i), "次级");
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei3" + (i + 1)
							+ ".xml");
				}
				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeCount(
							"资产变化趋势分析", listAll.get(i), "可疑");
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei4" + (i + 1)
							+ ".xml");
				}
				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeCount(
							"资产变化趋势分析", listAll.get(i), "损失");
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei5" + (i + 1)
							+ ".xml");
				}

				for (int i = 0; i < listAll.size(); i++) {
					Document doc = CreateXml.createR1AssetChangeAllCount(
							"资产变化趋势分析", listAll.get(i));
					CreateXml.writeXml(doc, basePath
							+ "/biReport/modules/bi/data/R1/data/wujifenlei0" + (i + 1)
							+ ".xml");
				}
			} catch (Exception e) {
				
			}
			
			
			List<Map> listTotal = new ArrayList<Map>();
			
			
			//for(int i=0;i<listAll.get(0).size();i++){
			for(int i=0;i<monthList.size();i++){
				Map temMap = new HashMap(); 
				temMap.put("GONGSI", "全部");
//				temMap.put("SHIJIAN", listAll.get(0).get(i).get("SHIJIAN"));
				temMap.put("SHIJIAN", monthList.get(i));
				Double zhengchange = 0d;
				Double guanzhu = 0d;
				Double ciji = 0d;
				Double keyi = 0d;
				Double sunshi = 0d;
				for (int j = 0; j < listAll.size(); j++) { 
					try {
						zhengchange=zhengchange+ 
						Double.parseDouble(String.valueOf(listAll.get(j).get(i).get("ZHENGCHANG")));
						guanzhu += Double.parseDouble(String.valueOf(listAll.get(j)
								.get(i).get("GUANZHU")));
						ciji += Double.parseDouble(String.valueOf(listAll.get(j).get(i)
								.get("CIJI")));
						keyi += Double.parseDouble(String.valueOf(listAll.get(j).get(i)
								.get("KEYI")));
						sunshi += Double.parseDouble(String.valueOf(listAll.get(j).get(
								i).get("SUNSHI"))); 
					} catch (Exception e) {
					}
					
				}
				temMap.put("ZHENGCHANG", zhengchange);
				temMap.put("GUANZHU", guanzhu);
				temMap.put("CIJI", ciji);
				temMap.put("KEYI", keyi);
				temMap.put("SUNSHI", sunshi);
				listTotal.add(temMap);
				
			} 

/*			System.out.println("--------------------------");
			System.out.println(listTotal);
			System.out.println("--------------------------");*/
			
			Document doc = CreateXml.createR1AssetChangeAllCount(
					"资产变化趋势分析", listTotal);
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei00.xml");
			doc = CreateXml.createR1AssetChangeCount("资产变化趋势分析", listTotal,
					"正常");
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei10.xml");
			doc = CreateXml.createR1AssetChangeCount("资产变化趋势分析", listTotal,
					"关注");
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei20.xml");
			doc = CreateXml.createR1AssetChangeCount("资产变化趋势分析", listTotal,
					"次级");
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei30.xml");
			doc = CreateXml.createR1AssetChangeCount("资产变化趋势分析", listTotal,
					"可疑");
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei40.xml");
			doc = CreateXml.createR1AssetChangeCount("资产变化趋势分析", listTotal,
					"损失");
			CreateXml.writeXml(doc, basePath
					+ "/biReport/modules/bi/data/R1/data/wujifenlei50.xml");		
			
			// =========资产构成横向比较==================================================================================================
			List<Map> ListH = new ArrayList<Map>();
			for (List<Map> l : listAll) {
				Map hexiangMap = new HashMap();
				Double zhengchange = 0d;
				Double guanzhu = 0d;
				Double ciji = 0d;
				Double keyi = 0d;
				Double sunshi = 0d;
				for (Map map : l) {
					zhengchange = Double
							.parseDouble(String.valueOf(map.get("ZHENGCHANG")));
					guanzhu = Double.parseDouble(String.valueOf(map.get("GUANZHU")));
					ciji = Double.parseDouble(String.valueOf(map.get("CIJI")));
					keyi = Double.parseDouble(String.valueOf(map.get("KEYI")));
					sunshi = Double.parseDouble(String.valueOf(map.get("SUNSHI")));
				}
				
				Double hexiangzonghe = zhengchange + guanzhu + ciji + keyi + sunshi;

				Double zhengchangeRate = 0d;
				Double guanzhuRate = 0d;
				Double cijiRate = 0d;
				Double keyiRate = 0d;
				Double sunshiRate = 0d;
				if (hexiangzonghe != 0d) {
					zhengchangeRate = zhengchange / hexiangzonghe * 100;
					guanzhuRate = guanzhu / hexiangzonghe * 100;
					cijiRate = ciji / hexiangzonghe * 100;
					keyiRate = keyi / hexiangzonghe * 100;
					sunshiRate = sunshi / hexiangzonghe * 100;
				} else {
					zhengchangeRate = 100d;
					guanzhuRate = 0d;
					cijiRate = 0d;
					keyiRate = 0d;
					sunshiRate = 0d;
				}
				hexiangMap.put("公司", l.get(0).get("GONGSI"));
				hexiangMap.put("正常", zhengchangeRate);
				hexiangMap.put("关注", guanzhuRate);
				hexiangMap.put("次级", cijiRate);
				hexiangMap.put("可疑", keyiRate);
				hexiangMap.put("损失", sunshiRate);

				ListH.add(hexiangMap);
			}
				Document doc1 = CreateXml.createR1AssetMadeAllCount("资产构成横向比较",
						"", ListH);
				CreateXml.writeXml(doc1, basePath
						+ "/biReport/modules/bi/data/R1/data3/wujifenlei.xml");
				
				// ============资产构成趋势分析（12个月）=======================================================

				int n = 0;
				for (List<Map> l2 : listAll) {
					List<Map> ListH2 = new ArrayList<Map>();
					String subname = l2.get(0).get("GONGSI") + "";
					Double zhengchange = 0d;
					Double guanzhu = 0d;
					Double ciji = 0d;
					Double keyi = 0d;
					Double sunshi = 0d;
					Double zhengchangeRate = 0d;
					Double guanzhuRate = 0d;
					Double cijiRate = 0d;
					Double keyiRate = 0d;
					Double sunshiRate = 0d;
					for (Map map : l2) {
						Map hexiangMap = new HashMap();
						zhengchange = Double.parseDouble(String.valueOf(map.get("ZHENGCHANG")));
						guanzhu = Double.parseDouble(String.valueOf(map.get("GUANZHU")));
						ciji = Double.parseDouble(String.valueOf(map.get("CIJI")));
						keyi = Double.parseDouble(String.valueOf(map.get("KEYI")));
						sunshi = Double.parseDouble(String.valueOf(map.get("SUNSHI")));
						Double hexiangzonghe = zhengchange + guanzhu + ciji + keyi
								+ sunshi;
						if (hexiangzonghe != 0d) {
							zhengchangeRate = zhengchange / hexiangzonghe * 100;
							guanzhuRate = guanzhu / hexiangzonghe * 100;
							cijiRate = ciji / hexiangzonghe * 100;
							keyiRate = keyi / hexiangzonghe * 100;
							sunshiRate = sunshi / hexiangzonghe * 100;
						} else {
							zhengchangeRate = 100d;
							guanzhuRate = 0d;
							cijiRate = 0d;
							keyiRate = 0d;
							sunshiRate = 0d;
						}
						hexiangMap.put("公司", map.get("SHIJIAN"));
						hexiangMap.put("正常", zhengchangeRate);
						hexiangMap.put("关注", guanzhuRate);
						hexiangMap.put("次级", cijiRate);
						hexiangMap.put("可疑", keyiRate);
						hexiangMap.put("损失", sunshiRate);
						ListH2.add(hexiangMap);
					}

						Document doc3 = CreateXml.createR1AssetMadeAllCount(
								"资产构成趋势（12个月）", subname, ListH2);
						CreateXml.writeXml(doc3, basePath
								+ "/biReport/modules/bi/data/R1/data2/wujifenlei" + (n + 1)
								+ ".xml");

					n = n + 1;
				}
				
				List<Map> ListH3 = new ArrayList<Map>();
				Double zhengchange = 0d;
				Double guanzhu = 0d;
				Double ciji = 0d;
				Double keyi = 0d;
				Double sunshi = 0d;
				Double zhengchangeRate = 0d;
				Double guanzhuRate = 0d;
				Double cijiRate = 0d;
				Double keyiRate = 0d;
				Double sunshiRate = 0d;
				for (Map map2 : listTotal) {
					Map hexiangMap = new HashMap();
					zhengchange = Double.parseDouble(String.valueOf(map2.get("ZHENGCHANG")));
					guanzhu = Double.parseDouble(String.valueOf(map2.get("GUANZHU")));
					ciji = Double.parseDouble(String.valueOf(map2.get("CIJI")));
					keyi = Double.parseDouble(String.valueOf(map2.get("KEYI")));
					sunshi = Double.parseDouble(String.valueOf(map2.get("SUNSHI")));
					Double hexiangzonghe = zhengchange + guanzhu + ciji + keyi + sunshi;
					if (hexiangzonghe != 0d) {
						zhengchangeRate = zhengchange / hexiangzonghe * 100;
						guanzhuRate = guanzhu / hexiangzonghe * 100;
						cijiRate = ciji / hexiangzonghe * 100;
						keyiRate = keyi / hexiangzonghe * 100;
						sunshiRate = sunshi / hexiangzonghe * 100;
					} else {
						zhengchangeRate = 100d;
						guanzhuRate = 0d;
						cijiRate = 0d;
						keyiRate = 0d;
						sunshiRate = 0d;
					}
					hexiangMap.put("公司", map2.get("SHIJIAN"));
					hexiangMap.put("正常", zhengchangeRate);
					hexiangMap.put("关注", guanzhuRate);
					hexiangMap.put("次级", cijiRate);
					hexiangMap.put("可疑", keyiRate);
					hexiangMap.put("损失", sunshiRate);
					ListH3.add(hexiangMap);
				}

					Document doc2 = CreateXml.createR1AssetMadeAllCount(
							"资产构成趋势（12个月）", "全部", ListH3);
					CreateXml.writeXml(doc2, basePath
							+ "/biReport/modules/bi/data/R1/data2/wujifenlei0"
							+ ".xml");
					//========R2===============================================
					List<Map> r3List = new ArrayList<Map>();
					int r2Index = 1;
					for (List<Map> l : listAll) {
						Double zhengchangeR2 = 0d;
						Double guanzhuR2 = 0d;
						Double cijiR2 = 0d;
						Double keyiR2 = 0d;
						Double sunshiR2 = 0d;
						Map map = new HashMap();
						String name = String.valueOf(l.get(0).get("GONGSI"));
						for (Map m : l) {
							zhengchangeR2 = Double.parseDouble(String.valueOf(m.get("ZHENGCHANG")));
							guanzhuR2 = Double.parseDouble(String.valueOf(m.get("GUANZHU")));
							cijiR2 = Double.parseDouble(String.valueOf(m.get("CIJI")));
							keyiR2 = Double.parseDouble(String.valueOf(m.get("KEYI")));
							sunshiR2 = Double.parseDouble(String.valueOf(m.get("SUNSHI")));
						}
						Double hexiangzongheR2 = zhengchangeR2 + guanzhuR2 + cijiR2 + keyiR2 + sunshiR2;
						if (hexiangzongheR2 == 0d) {
							zhengchangeR2 = 1d;
						}
						map.put("公司", name);
						map.put("正常", zhengchangeR2);
						map.put("关注", guanzhuR2);
						map.put("次级", cijiR2);
						map.put("可疑", keyiR2);
						map.put("损失", sunshiR2);
						
						CreateXml.writeXml(CreateXml.createR2(map, name), basePath+"/biReport/modules/bi/data/R2/"+r2Index+".xml");

						r2Index ++;
						r3List.add(map);
					}
					//========R3===============================================
					CreateXml.writeXml(CreateXml.createR3(r3List, "正常"), basePath+"/biReport/modules/bi/data/R3/1.xml");
					CreateXml.writeXml(CreateXml.createR3(r3List, "关注"), basePath+"/biReport/modules/bi/data/R3/2.xml");
					CreateXml.writeXml(CreateXml.createR3(r3List, "次级"), basePath+"/biReport/modules/bi/data/R3/3.xml");
					CreateXml.writeXml(CreateXml.createR3(r3List, "可疑"), basePath+"/biReport/modules/bi/data/R3/4.xml");
					CreateXml.writeXml(CreateXml.createR3(r3List, "损失"), basePath+"/biReport/modules/bi/data/R3/5.xml");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
