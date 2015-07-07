package com.brick.service.entity;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class Context {
	
	public Map contextMap = null;
	public List msgList = null;
	public List errList = null;		
	public Map dataMap = null;
	public HttpServletRequest request = null;
	public HttpServletResponse response = null;
	public ServletContext servletContext = null;
	public FileInfo fileInfo = null;
	public boolean multiPart = false;
	
	private Map<String, Object> formBean;

	public Map<String, Object> getFormBean() {
		return formBean;
	}
	
	public Object getFormBean(String formBeanName){
		if (this.formBean != null) {
			return formBean.get(formBeanName);
		} else {
			return null;
		}
	}

	public void setFormBean(Map<String, Object> formBean) {
		this.formBean = formBean;
	}


	public Context(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext ){
		contextMap = new HashMap();
		msgList = new LinkedList();
		errList = new LinkedList();
		dataMap = new HashMap();
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;
	}


	public HttpServletRequest getRequest() {
		return request;
	}


	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	public HttpServletResponse getResponse() {
		return response;
	}


	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}


	public Map getContextMap() {
		return contextMap;
	}


	public void setContextMap(Map contextMap) {
		this.contextMap = contextMap;
	}


	public List getMsgList() {
		return msgList;
	}


	public void setMsgList(List msgList) {
		this.msgList = msgList;
	}


	public List getErrList() {
		return errList;
	}


	public void setErrList(List errList) {
		this.errList = errList;
	}


	public Map getDataMap() {
		return dataMap;
	}


	public void setDataMap(Map dataMap) {
		this.dataMap = dataMap;
	}





	public FileInfo getFileInfo() {
		return fileInfo;
	}


	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}


	public boolean isMultiPart() {
		return multiPart;
	}


	public void setMultiPart(boolean multiPart) {
		this.multiPart = multiPart;
	}
	
	

}
