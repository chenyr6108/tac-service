package com.brick.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.brick.base.to.PagingInfo;

public class PagingToolbarTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PagingInfo<Object> pagingInfo;
	
	public PagingInfo<Object> getPagingInfo() {
		return pagingInfo;
	}

	public void setPagingInfo(PagingInfo<Object> pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	@Override
	public int doEndTag() throws JspException {
		// TODO Auto-generated method stub
		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
	        if(pagingInfo == null) {
	            out.println("没有分页信息... ...");
	            return SKIP_BODY;
	        }
	        out.println("<table style=\"width: 100%\">");
	        out.println("<tr>");
	        out.println("<td style='text-align: right; border-width: 0; background-color: transparent; text-decoration:none;'>");
	        out.println("<input type='hidden' id='__currentPage' name='__currentPage' value='" + pagingInfo.getPageNo() +"' />");
	        out.println("<input type='hidden' id='__pageSize' name='__pageSize' value='" + pagingInfo.getPageSize() +"' />");
	        out.println("<input type='hidden' id='__orderBy' name='__orderBy' value='" + pagingInfo.getOrderBy() +"' />");
	        out.println("<input type='hidden' id='__orderType' name='__orderType' value='" + pagingInfo.getOrderType() +"' />");
	        out.println("<font style='color:#2E6EAF; font-weight: bold;'>每页显示</font>");
	        if (pagingInfo.getPageSize() == 10) {
	        	out.println("<font style='color:#C0C0C0;'>10</font>");
			} else {
				out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='changePageTo10();'>[10]</a>");
			}
	        if (pagingInfo.getPageSize() == 20) {
	        	out.println("<font style='color:#C0C0C0;'>20</font>");
			} else {
				out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='changePageTo20();'>[20]</a>");
			}
			if (pagingInfo.getPageSize() == 50) {
				out.println("<font style='color:#C0C0C0;'>50</font>");
			} else {
				out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='changePageTo50();'>[50]</a>");
			}
	        out.println("&nbsp;&nbsp; <font style='color:#2E6EAF; font-weight: bold;'>共" + pagingInfo.getTotalCount() +"条  第" + pagingInfo.getPageNo() +"/" + pagingInfo.getTotalPage() + "</font>");
	        if (pagingInfo.isHasPre()) {
	        	out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='gotoF();'>[首页]</a>");
	        	out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='gotoP();'>[上一页]</a>");
			} else {
				out.println("<font style='color:#C0C0C0;'>首页</font>");
				out.println("<font style='color:#C0C0C0;'>上一页</font>");
			}
	        if (pagingInfo.isHasNext()) {
	        	out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='gotoN();'>[下一页]</a>");
	        	out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='gotoL(" + pagingInfo.getTotalPage() + ");'>[尾页]</a>");
	        } else {
				out.println("<font style='color:#C0C0C0;'>下一页</font>");
				out.println("<font style='color:#C0C0C0;'>尾页</font>");
			}
	        out.println("<font style='color:#2E6EAF; font-weight: bold;'>到第</font>");
	        out.println("<input type='text' size='3' id='jump_t' name='jump'>");
	        out.println("<font style='color:#2E6EAF; font-weight: bold;'>页</font>");
	        out.println("<a href='javascript:void(0)' style='color:#2E6EAF; font-weight: bold; text-decoration:none;' class=\"panel_a\" onclick='jumpTo_t(" + pagingInfo.getTotalPage() + ");' >[跳转]</a>");
	        out.println("&nbsp;&nbsp;</td>");
	        out.println("</tr>");
	        out.println("</table>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
	
}
