package com.brick.service.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.brick.base.to.BaseTo;
import com.brick.service.entity.Context;

import flexjson.JSONSerializer;

public class Output {
	
	public static enum OUTPUT_TYPE {TXT, JSON, XML, JSP,JSONArray,FLAGE};
	
	private static Logger logger = Logger.getLogger(Output.class.getName());
	public static JSONSerializer serializer = new JSONSerializer();
	
	/**
	 * 
	 * @param outputMap
	 * @param context
	 */
	public static void jsonOutput(final Map outputMap, final Context context){
		
		String json = serializer.serialize(outputMap);
		
		try{

			if(context.response != null){
				context.response.getWriter().print(json);
			}else{
				logger.info(json);
			}
			
		}catch(Exception e){
			logger.error("Json out error by \n." + e.getMessage());
		}
		
	}
	
	/**
	 * 
	 * @param content
	 * @param context
	 */
	public static void txtOutput(final String content, final Context context){
		try{

			if(context.response != null){
				context.response.setCharacterEncoding("GBK");
				context.response.getWriter().print(content);
			}else{
				logger.info(content);
			}
		}catch(Exception e){
			logger.error("Txt out error by \n." + e.getMessage());
		}		
	}
	
	
	
	/**
	 * 
	 * @param outputMap
	 * @param jspPath
	 * @param context
	 */
	public static void jspOutput(final Map outputMap, final Context context, final String jspPath){
		
		HttpServletRequest request = context.request;
		
		Set set = outputMap.keySet();
		for(Iterator it = set.iterator(); it.hasNext();){
			String key = (String)it.next();
			request.setAttribute(key, outputMap.get(key));
		}
		
		try{
			context.servletContext.getRequestDispatcher(jspPath).forward(request, context.response);
//			context.response.sendRedirect(jspPath);
		}catch(Exception e){
			logger.error("Jsp out error by \n." + e.getMessage());
		}
	}

	/**
	 * 重定向
	 */
	public static void jspSendRedirect(final Context context, final String jspPath) {
		HttpServletResponse response = context.response;
		try {
			response.sendRedirect(jspPath);
		} catch (Exception e) {
			logger.error("Jsp out error by \n." + e.getMessage());
		} 
	} 
	
	public static void jsonArrayOutput(final java.util.List<java.util.Map> outputList,final Context context) {
		JSONArray jsonArray = new JSONArray();
		
		for (Map map : outputList) {
			JSONObject jsonObj = JSONObject.fromObject(map);
			jsonArray.add(jsonObj);
		}
		PrintWriter out = null;
		try {
			out = context.response.getWriter();
			out.print(jsonArray);
		} catch (IOException e) {
			logger.debug(e.getCause().getMessage().toString());
		} finally {
			if (out != null)out.close();
			//TODO
		}
	}
	
	public static void jsonArrayListOutput(final java.util.List<java.util.Map<String,String>> outputList,final Context context) {
		JSONArray jsonArray = new JSONArray();
		
		for (Map<String,String> map : outputList) {
			JSONObject jsonObj = JSONObject.fromObject(map);
			jsonArray.add(jsonObj);
		}
		PrintWriter out = null;
		try {
			out = context.response.getWriter();
			out.print(jsonArray);
		} catch (IOException e) {
			logger.debug(e.getCause().getMessage().toString());
		} finally {
			if (out != null)out.close();
			//TODO
		}
	}
	
	public static void jsonArrayOutputForObject(final List<? extends BaseTo> outputList,final Context context) {
		JSONArray jsonArray = new JSONArray();
		
		for (Object baseTo : outputList) {
			JSONObject jsonObj = JSONObject.fromObject(baseTo);
			jsonArray.add(jsonObj);
		}
		logger.debug("Json Object ===========================>>>" + jsonArray.toString());
		PrintWriter out = null;
		try {
			out = context.response.getWriter();
			out.print(jsonArray);
		} catch (IOException e) {
			logger.debug(e.getCause().getMessage().toString());
		} finally {
			if (out != null)out.close();
			//TODO
		}
	} 
	
	public static void jsonArrayOutputForList(final List<?> outputList,final Context context) {
		JSONArray jsonArray = new JSONArray();
		
		for (Object baseTo : outputList) {
			JSONObject jsonObj = JSONObject.fromObject(baseTo);
			jsonArray.add(jsonObj);
		}
		logger.debug("Json Object ===========================>>>" + jsonArray.toString());
		PrintWriter out = null;
		try {
			out = context.response.getWriter();
			out.print(jsonArray);
		} catch (IOException e) {
			logger.debug(e.getCause().getMessage().toString());
		} finally {
			if (out != null)out.close();
			//TODO
		}
	} 
	
	public static void jsonFlageOutput(final boolean flage,Context context) {
		PrintWriter out = null;
		try {
			if(context.response != null){
				out = context.response.getWriter();
				out.print(flage);
			}
			return;
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			if (out != null) out.close();
		}
	}
	
	public static void jsonObjectOutputForTo(BaseTo baseTo,final Context context) {
		
		JSONObject jsonObj = JSONObject.fromObject(baseTo);
		
		logger.debug("Json Object ===========================>>>" + jsonObj.toString());
		PrintWriter out = null;
		try {
			out = context.response.getWriter();
			out.print(jsonObj);
		} catch (IOException e) {
			logger.debug(e.getCause().getMessage().toString());
		} finally {
			if (out != null)out.close();
			//TODO
		}
	} 
	
	public static void errorPageOutput(Throwable e, final Context context){
		Map<String, Object> outputMap = new HashMap<String, Object>();
		context.errList.add(e.getMessage());
		outputMap.put("errList", context.errList);
		Output.jspOutput(outputMap, context, "/error.jsp");
	}
	
	public static void downLoadFile(String filePath, final Context context) throws Exception{
		FileInputStream fis = null;
		BufferedInputStream buff = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new Exception("文件不存在。");
			}
			//解决中文乱码问题
	        String filename = new String(file.getName().getBytes("iso-8859-1"),"gbk");

	        //设置response的编码方式
	        context.response.setContentType("application/x-msdownload");

	        //写明要下载的文件的大小
	        context.response.setContentLength((int)file.length());

	        //设置附加文件名
	        context.response.setHeader("Content-Disposition","attachment;filename=" + filename);
	        
	        //解决中文乱码
	        context.response.setHeader("Content-Disposition","attachment;filename=" + new String(filename.getBytes("gbk"),"iso-8859-1"));        

	        //读出文件到i/o流
	        fis = new FileInputStream(file);
	        buff = new BufferedInputStream(fis);

	        byte [] b=new byte[1024];//相当于我们的缓存

	        long k=0;//该值用于计算当前实际下载了多少字节

	        //从response对象中得到输出流,准备下载

	        OutputStream myout = context.response.getOutputStream();

	        //开始循环下载

	        while(k<file.length()){

	            int j=buff.read(b,0,1024);
	            k+=j;

	            //将b中的数据写到客户端的内存
	            myout.write(b,0,j);

	        }

	        //将写入到客户端的内存的数据,刷新到磁盘
	        myout.flush();


		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw e;
				}
				fis = null;
			}
			if (buff != null) {
				try {
					buff.close();
				} catch (IOException e) {
					throw e;
				}
				buff = null;
			}
		}
	}
	
}
