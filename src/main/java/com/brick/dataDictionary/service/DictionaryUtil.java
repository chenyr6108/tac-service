package com.brick.dataDictionary.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.service.core.DataAccessor;

/**
 * @author wujw
 * @date Oct 13, 2010
 * @version 
 */
public class DictionaryUtil {
	
	/**
	 * 查询数据字典
	 * @param dataType
	 * @return 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List getDictionary(String dataType) throws Exception {
		
		Map dataDictionaryMap = new HashMap();
		
		dataDictionaryMap.put("dataType", dataType);
		
		List destList = (List) DataAccessor.query("dataDictionary.queryDataDictionary", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
		
		return destList;
	
	}
	
	public static List getDictionaryForAll(String dataType) throws Exception {
		
		Map dataDictionaryMap = new HashMap();
		
		dataDictionaryMap.put("dataType", dataType);
		
		List destList = (List) DataAccessor.query("dataDictionary.queryDataDictionaryAll", dataDictionaryMap, DataAccessor.RS_TYPE.LIST);
		
		return destList;
	
	}
	
	public static String getFlag(String type,String code) throws Exception{
		String flag = null;
		Map<String,String> params = new HashMap<String,String>();
		params.put("type", type);
		params.put("code", code);
		flag = (String) DataAccessor.query("dataDictionary.getDataDictionaryFlag", params, DataAccessor.RS_TYPE.OBJECT);
		return flag;
	}
}
