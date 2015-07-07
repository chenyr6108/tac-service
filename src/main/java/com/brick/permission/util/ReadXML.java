package com.brick.permission.util;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ibatis.common.resources.Resources;

public class ReadXML {

//	public static void main(String[] args) throws Exception {
//		List<Element> list = getConfigList();
//		for (Element el : list) {
//			System.out.println(el.attributeValue("name"));
//		}
//	}

	/**
	 * 读取xml文档放入list集合
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getConfigList() throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(Resources.getResourceAsReader("config/desk-config.xml"));
		List<Element> nodes = document.selectNodes("//div/value");
		return nodes;
	}

}
