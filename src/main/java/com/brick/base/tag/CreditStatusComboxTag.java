package com.brick.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.brick.util.StringUtils;

public class CreditStatusComboxTag extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String id;
	private String style;
	private String displayValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			String selectedStr = "selected=\"selected\"";
			displayValue = StringUtils.isEmpty(displayValue) ? "0" : displayValue;
			out.println("<select " + (StringUtils.isEmpty(name) ? "" : "name=\"" + name + "\"") + 
						(StringUtils.isEmpty(style) ? "" : "style=\"" + style + "\"") + 
						(StringUtils.isEmpty(id) ? "" : "id=\"" + id + "\"") + 
						"\">");
			/*
			 * 案件状态列表
			 * -600----正常结清
			 * -500----提前结清
			 * -400----合同作废
			 * -300----撤销
			 * -200----婉拒
			 * -100----已核准未拨款已过期
			 * 100-----调查中
			 * 200-----业务主管审批中
			 * 300-----业务副总审批中
			 * 400-----初级评审
			 * 500-----一级评审
			 * 600-----二级评审
			 * 700-----三级评审
			 * 800-----四级评审
			 * 900-----已核准文件准备中
			 * 1000----已核准文审中
			 * 1100----拨款审批中
			 * 1200----付款审批中
			 * 1300----已拨款
			 * */
			out.println("<option value=\"\">全部</option>");
			out.println("<option value=\"-600\" " + (displayValue.equals("-600") ? selectedStr : "") + ">正常结清</option>");
			out.println("<option value=\"-500\" " + (displayValue.equals("-500") ? selectedStr : "") + ">提前结清</option>");
			out.println("<option value=\"-400\" " + (displayValue.equals("-400") ? selectedStr : "") + ">合同作废</option>");
			out.println("<option value=\"-300\" " + (displayValue.equals("-300") ? selectedStr : "") + ">撤销</option>");
			out.println("<option value=\"-200\" " + (displayValue.equals("-200") ? selectedStr : "") + ">婉拒</option>");
			out.println("<option value=\"-100\" " + (displayValue.equals("-100") ? selectedStr : "") + ">已核准未拨款已过期</option>");
			out.println("<option value=\"100\" " + (displayValue.equals("100") ? selectedStr : "") + ">调查中</option>");
			out.println("<option value=\"200\" " + (displayValue.equals("200") ? selectedStr : "") + ">业务主管审批中</option>");
			out.println("<option value=\"300\" " + (displayValue.equals("300") ? selectedStr : "") + ">业务副总审批中</option>");
			out.println("<option value=\"400\" " + (displayValue.equals("400") ? selectedStr : "") + ">初级评审</option>");
			out.println("<option value=\"500\" " + (displayValue.equals("500") ? selectedStr : "") + ">一级评审</option>");
			out.println("<option value=\"600\" " + (displayValue.equals("600") ? selectedStr : "") + ">二级评审</option>");
			out.println("<option value=\"700\" " + (displayValue.equals("700") ? selectedStr : "") + ">三级评审</option>");
			out.println("<option value=\"800\" " + (displayValue.equals("800") ? selectedStr : "") + ">四级评审</option>");
			out.println("<option value=\"900\" " + (displayValue.equals("900") ? selectedStr : "") + ">已核准文件准备中</option>");
			out.println("<option value=\"1000\" " + (displayValue.equals("1000") ? selectedStr : "") + ">已核准文审中</option>");
			out.println("<option value=\"1100\" " + (displayValue.equals("1100") ? selectedStr : "") + ">拨款审批中</option>");
			out.println("<option value=\"1200\" " + (displayValue.equals("1200") ? selectedStr : "") + ">付款审批中</option>");
			out.println("<option value=\"1300\" " + (displayValue.equals("1300") ? selectedStr : "") + ">已拨款</option>");
			out.println("</select>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
