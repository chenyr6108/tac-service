package com.brick.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.brick.base.to.PagingInfo;
import com.brick.util.StringUtils;

public class PagingSortTag extends TagSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ASC = "ASC";
	private static final String DESC = "DESC";
	
	private String orderBy;
	
	private PagingInfo<Object> pagingInfo;
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public PagingInfo<Object> getPagingInfo() {
		return pagingInfo;
	}

	public void setPagingInfo(PagingInfo<Object> pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			if (!StringUtils.isEmpty(pagingInfo.getOrderBy()) && pagingInfo.getOrderBy().equalsIgnoreCase(orderBy)) {
				if (ASC.equals(pagingInfo.getOrderType())) {
					out.print("▲");
				} else if (DESC.equals(pagingInfo.getOrderType())) {
					out.print("▼");
				}
			}
			out.print("</a>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
	        if(StringUtils.isEmpty(orderBy)) {
	            return EVAL_BODY_INCLUDE;
	        }
	        out.print("<a href='javascript:void(0)' " +
	        		"onclick='doSort(\"" + orderBy + "\", \"" + pagingInfo.getOrderBy() + "\", \"" + 
	        		pagingInfo.getOrderType() + "\")' class=\"panel_a\">");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}
}
