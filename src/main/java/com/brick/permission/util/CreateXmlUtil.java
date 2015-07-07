package com.brick.permission.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class CreateXmlUtil {

	/**
	 * 写入xml文件
	 * 
	 * @param doc
	 * @param path
	 * @throws IOException
	 */
	public static void writeXml(Document doc, String path) throws IOException {
		OutputFormat out = OutputFormat.createPrettyPrint();
		out.setEncoding("GBK");
		out.setIndent("    ");
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path), out);
		xmlWriter.write(doc);
		xmlWriter.close();
	}

	/**
	 * 创建应收账款xml
	 * 
	 * @param map
	 * @return Document
	 */
	@SuppressWarnings("unchecked")
	public static Document createAccountReceivable(Map map) {
		Document doc = DocumentHelper.createDocument();

		Element root = DocumentHelper.createElement("chart");
		root.addAttribute("caption", "应收账款");
		root.addAttribute("palette", "4");
		root.addAttribute("decimals", "0");
		root.addAttribute("enableSmartLabels", "1");
		root.addAttribute("enableRotation", "0");
		root.addAttribute("bgColor", "FFFFFF,FFFFFF");
		root.addAttribute("formatNumberScale", "0");
		root.addAttribute("bgAlpha", "40,100");
		root.addAttribute("bgRatio", "0,100");
		root.addAttribute("bgAngle", "360");
		root.addAttribute("showBorder", "1");
		root.addAttribute("startingAngle", "70");
		doc.setRootElement(root);

		root.addElement("set").addAttribute("label", "应收本金").addAttribute(
				"value", String.valueOf(map.get("ownprice"))).addAttribute(
				"isSliced", "0");
		root.addElement("set").addAttribute("label", "利息").addAttribute(
				"value", String.valueOf(map.get("renprice"))).addAttribute(
				"isSliced", "0");
		root.addElement("set").addAttribute("label", "罚金").addAttribute(
				"value", String.valueOf(map.get("dunfine"))).addAttribute(
				"isSliced", "0");
		root.addElement("set").addAttribute("label", "逾期罚金").addAttribute(
				"value", String.valueOf(map.get("dunfineinterest")))
				.addAttribute("isSliced", "0");
		return doc;
	}

	/**
	 * 创建逾期统计xml
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Document createDunStatistics(Map map) {
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("chart");
		root.addAttribute("caption", "逾期统计（6个月）");
		root.addAttribute("numberPrefix", "￥");
		root.addAttribute("palette", "1");
		root.addAttribute("shownames", "1");
		root.addAttribute("showLegend", "1");
		root.addAttribute("showvalues", "0");
		root.addAttribute("showSum", "1");
		root.addAttribute("yAxisMaxValue", "100");
		root.addAttribute("decimals", "1");
		root.addAttribute("overlapColumns", "0");
		root.addAttribute("showAlternateHGridColor", "1");
		doc.setRootElement(root);

		Element categories = root.addElement("categories");
		List monthList = (List) map.get("monthList");
		for (int i = monthList.size(); i > 0; i--) {
			categories.addElement("category").addAttribute("label",
					String.valueOf(monthList.get(i - 1)));
		}

		Element dataset1 = root.addElement("dataset").addAttribute(
				"seriesName", "1~30天").addAttribute("anchorBorderColor",
				"1D8BD1").addAttribute("anchorBgColor", "1D8BD1");
		List list30 = (List) map.get("list30");
		for (int i = list30.size(); i > 0; i--) {
			dataset1.addElement("set").addAttribute("color", "B1D7FB")
					.addAttribute("value",String.valueOf(list30.get(i - 1)));
		}
		
		Element dataset2 = root.addElement("dataset").addAttribute(
				"seriesName", "30~60天").addAttribute("anchorBorderColor",
				"1D8BD1").addAttribute("anchorBgColor", "1D8BD1");
		List list60 = (List) map.get("list60");
		for (int i = list60.size(); i > 0; i--) {
			dataset2.addElement("set").addAttribute("color", "F3BF0B")
					.addAttribute("value", String.valueOf(list60.get(i - 1)));
		}
		
		Element dataset3 = root.addElement("dataset").addAttribute(
				"seriesName", "60~90天").addAttribute("anchorBorderColor",
				"1D8BD1").addAttribute("anchorBgColor", "1D8BD1");
		List list90 = (List) map.get("list90");
		for (int i = list90.size(); i > 0; i--) {
			dataset3.addElement("set").addAttribute("color", "8AB900")
					.addAttribute("value", String.valueOf(list90.get(i - 1)));
		}
		
		Element dataset4 = root.addElement("dataset").addAttribute(
				"seriesName", "90天以上").addAttribute("anchorBorderColor",
				"1D8BD1").addAttribute("anchorBgColor", "1D8BD1");
		List listEnd = (List) map.get("listEnd");
		for (int i = listEnd.size(); i > 0; i--) {
			dataset4.addElement("set").addAttribute("color", "FF9048")
					.addAttribute("value", String.valueOf(listEnd.get(i - 1)));
		}

		return doc;
	}

	/**
	 * 创建销售漏斗xml
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Document creatSalesLeads(List list) {
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("chart");
		root.addAttribute("caption", "销售漏斗");
		root.addAttribute("isHollow", "0");
		root.addAttribute("decimals", "1");
		root.addAttribute("baseFontSize", "12");
		root.addAttribute("isSliced", "0");
		root.addAttribute("useSameSlantAngle", "1");
		root.addAttribute("labelDistance", "5");
		root.addAttribute("numberPrefix", "￥");
		root.addAttribute("streamlinedData", "0");
		doc.setRootElement(root);
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			if (Integer.parseInt(map.get("count").toString()) != 0) {
				root.addElement("set").addAttribute("label",
						String.valueOf(map.get("flag"))).addAttribute("value",
						String.valueOf(map.get("expect_money_sum")));
			}
		}
		Element styles = root.addElement("styles");
		Element definition = styles.addElement("definition");
		definition.addElement("style").addAttribute("name", "CaptionFont")
				.addAttribute("type", "font").addAttribute("size", "12");
		Element application = styles.addElement("application");
		application.addElement("apply").addAttribute("toObject", "CAPTION")
				.addAttribute("styles", "CaptionFont");
		return doc;
	}
}
