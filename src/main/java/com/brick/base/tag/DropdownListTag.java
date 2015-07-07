package com.brick.base.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.brick.base.to.SelectionTo;
import com.brick.util.StringUtils;

public class DropdownListTag extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String id;
	private String style;
	private String className;
	private String displayValue = "";
	private List<SelectionTo> item;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public List<SelectionTo> getItem() {
		return item;
	}

	public void setItem(List<SelectionTo> item) {
		this.item = item;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			String selectedStr = "selected=\"selected\"";
			displayValue = StringUtils.isEmpty(displayValue) ? "" : displayValue;
			out.println("<select " + (StringUtils.isEmpty(name) ? "" : " name=\"" + name + "\"") + 
						(StringUtils.isEmpty(style) ? "" : " style=\"" + style + "\"") + 
						(StringUtils.isEmpty(id) ? "" : " id=\"" + id + "\"") + 
						(StringUtils.isEmpty(className) ? "" : " class=\"" + className + "\"") + 
						">");
			out.println("<option value=\"\">全部</option>");
			for (SelectionTo i : item) {
				out.println("<option value=\"" + i.getOption_value() + "\" " + (displayValue.equals(i.getOption_value()) ? selectedStr : "") + ">" + i.getDisplay_name() + "</option>");
			}
			out.println("</select>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
