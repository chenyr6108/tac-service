package com.brick.chartDirector.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.brick.chartDirector.ChartResult;
import com.brick.util.StringUtils;

public class ChartTag extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private ChartResult chartResult;

	public ChartResult getChartResult() {
		return chartResult;
	}

	public void setChartResult(ChartResult chartResult) {
		this.chartResult = chartResult;
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
	        if(chartResult == null) {
	            out.println("没有图表信息... ...");
	            return SKIP_BODY;
	        }
	        out.println("<img src='" + pageContext.getServletContext().getContextPath() + 
	        		"/chartDirector/getchart.jsp?" + chartResult.getChartURL() + "'" + 
	        	    "usemap='#map_" + chartResult.getChartName() + "' border='0'>");
	        out.println("<map name='map_" + chartResult.getChartName() + "'>" + 
	        	    chartResult.getImageMap() + "</map>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
