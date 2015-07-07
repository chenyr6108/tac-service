/*
 * Created on 2005-4-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.brick.util.config;

import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * <b>require:</b>log4j, dom4j<br>
 * <b>depend:</b>com.brick.util.config.Global<br>
 * <b>useage:</b>ConfigurationLoader.load(this, "config-file");
 * 
 * @author zxb
 * @version 1
 * </p>
 * 
 */
public class ConfigurationLoader{
	
	static Log logger = LogFactory.getLog(ConfigurationLoader.class);

	public static void load(HttpServlet httpservlet, String paramName){		
		
		String cfgFile = httpservlet.getInitParameter(paramName);
		logger.info("load config from file " + httpservlet.getServletContext().getRealPath(cfgFile));
		
		Document document = null;
		SAXReader reader = new SAXReader();
		
		try{
			document = reader.read(httpservlet.getServletContext().getRealPath(cfgFile));
		}catch(Exception e){
			logger.info("can not read config file \"" + httpservlet.getServletContext().getRealPath(cfgFile) + "\"\n" + e.toString());
		}
		
		
		List list = document.selectNodes("//configuration/params/param");
		if(list != null){
			Iterator it = list.iterator();
			while(it.hasNext()){
				
				Node node = (Node)it.next();					
				
				String name = node.selectSingleNode("./name").getText();					
				String type = node.selectSingleNode("./type").getText();
				String value = node.selectSingleNode("./value").getText();
				
				logger.info("load " + name + " ...");
				
				if("log4j-cfg".equals(type)){
					initLog4j(httpservlet.getServletContext().getRealPath("/") + value);
				}else if("datasource".equals(type)){					
					Global.put(name, initDataSource(value));
				}else{
					Global.put(name,  value);
				}
			}
		}	
		
		logger.info("load completed");
	}

	/**
	 * will init log4j config when type of current config item is  "log4j-cfg"
	 * @param fileName
	 */
	private static void initLog4j(String fileName){
		logger.info("config log4j ... ");
	    // if the log4j-init-file is not set, then no point in trying
	    PropertyConfigurator.configure(fileName);	  	    
	}
	
	
	/**
	 * will lookup datasource when type of current config item is  "datasource"
	 * @param dsName
	 * @return
	 */
	private static DataSource initDataSource(String dsName){
		logger.info("config datasource ... ");
		Context ctx = null;
		Context ectx = null;
		DataSource dataSource = null;
		
		try{			
			
			ctx = new InitialContext();
			ectx = (Context)ctx.lookup("java:/comp/env");
			dataSource = (DataSource)ectx.lookup(dsName);			
			
		}catch(Exception e){
			logger.info("can not load datasource named " + dsName + " : \n" + e.toString());
		}		
		return dataSource;
	}

}
