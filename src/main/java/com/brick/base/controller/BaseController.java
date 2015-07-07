package com.brick.base.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brick.base.service.BaseService;
import com.brick.log.service.LogPrint;
import com.brick.service.entity.Context;

import flexjson.JSONSerializer;

public class BaseController {
	
	
	protected BaseService baseService;
	
	public BaseService getBaseService() {
		return baseService;
	}
	
	@Resource(name = "baseService")
	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}

	public static JSONSerializer serializer = new JSONSerializer();
	
	
	public void jsonFlagOutput(boolean flag, HttpServletResponse response){
		PrintWriter out = null;
		try {
			response.setContentType("text/Xml;charset=utf-8"); 
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.print(flag);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null){
				out.close();
			}
		}
	}
	
	public void jsonObjectOutput(Object o, HttpServletResponse response){
		String json = null;
		if (o instanceof Map) {
			json = serializer.serialize((Map)o);
		} else {
			JSONObject jsonObj = JSONObject.fromObject(o);
			json= jsonObj.toString();
		}
		PrintWriter out = null;
		try {
			response.setContentType("text/Xml;charset=utf-8"); 
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null){
				out.close();
			}
		}
	}
	
	public void jsonArrayOutput(List<? extends Object> list, HttpServletResponse response){
		JSONArray jsonArray = new JSONArray();
		for (Object o : list) {
			JSONObject jsonObj = JSONObject.fromObject(o);
			jsonArray.add(jsonObj);
		}
		PrintWriter out = null;
		try {
			response.setContentType("text/Xml;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.print(jsonArray);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null){
				out.close();
			}
		}
	}
	
	public void download(String filePath, HttpServletResponse response) throws Exception {
		File file = new File(filePath);
		response.reset();
		OutputStream output = null;
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
	        response.setContentType("bin");
	        response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(file.getName().getBytes("utf-8"), "iso8859-1") + "\"");
	        output = response.getOutputStream();
	        byte[] b = new byte[100];
	        int len;
            while ((len = fis.read(b)) > 0){
            	output.write(b, 0, len);
            }
            fis.close();
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fis = null;
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}
		}
	}
	
}
