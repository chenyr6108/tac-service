package com.brick.area.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.brick.service.core.DataAccessor;

/**
 * @author yangxuan
 * @version Created：2010-4-14 下午03:53:49
 * when tomcat startup,please load....
 * 
 */

public class TreeRead {
	StringBuilder sb = new StringBuilder("");
	static Logger logger = Logger.getLogger(TreeRead.class);

	public String readTree() {
		return read2(0,0);
	}

	@SuppressWarnings("unchecked")
	private String read2(int id,int leval) {
		leval++;
		Map parasMap = new HashMap();
		parasMap.put("pid", id);
		
		String isleaf = null;
		int pid = 0;
		String name = null;
			
		try {
			List<Map> list = null;
			if (id==0) {
				list = (List<Map>) DataAccessor.query("area.queryTop",parasMap,DataAccessor.RS_TYPE.LIST);
			} else {
				list = (List<Map>) DataAccessor.query("area.query",
						parasMap, DataAccessor.RS_TYPE.LIST);
			}
			if (!list.isEmpty()) {
				for (Map retVal : list) {
					isleaf = (String) retVal.get("ISLEAF");
					pid = ((Number) retVal.get("ID")).intValue();
					name = (String) retVal.get("NAME");
					if ("N".equals(isleaf)) {
						sb.append("<div>");
						sb.append("\n");
						for (int i = 0; i < leval-1; i++) {
							sb.append("<img src=\"images/white.gif\">");
						}
						sb.append("<img alt=\"展开\" style=\"cursor:hand;\" onClick=\"display('"
										+ pid
										+ "');\" id=\"img"
										+ pid
										+ "\"  src=\"images/plus.gif\">");
						sb.append("\n");
						sb.append("<img id=\"im" + pid
								+ "\" src=\"images/closedfold.gif\">");
						sb.append("\n");
						//sb.append("<a href=\"/finance/area.action?id=" + pid+"&_action=area.queryAreaById"
						sb.append("<a href=\"../../servlet/defaultDispatcher?__action=area.queryAreaById&id=" + pid
								+ "\" target=\"clientDispAreaFrame\" style='font-size:12px'>" + name
								+ "</a>");
						sb.append("\n");
						sb.append("<div style=\"display:none;\" id=\"div" + pid
								+ "\">");
						sb.append("\n");
						read2(pid,leval);
						sb.append("</div>");
						sb.append("\n");
					} else {
						sb.append("<div>");
						for (int i = 0; i < leval-1; i++) { 
							sb.append("<img src=\"images/white.gif\">");
						}
						sb.append("<img src=\"images/minus.gif\">");
						sb.append("\n");
						sb.append("<img src=\"images/openfold.gif\">");
						sb.append("\n");
						sb.append("<a href=\"../../servlet/defaultDispatcher?__action=area.queryAreaById&id=" + pid + "\" target=\"clientDispAreaFrame\" style='font-size:12px'>" + name + "</a>");	
						sb.append("\n");
					}
					sb.append("</div>");
					sb.append("\n");
				}
			}
		} catch (Exception e) {
			logger.debug("read area_tree error!cause:\n"+e.getCause().getMessage());
		}
		return sb.toString();
	}
}
