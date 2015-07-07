package com.brick.base.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class CreditStatusFormatTag extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = this.pageContext.getOut();
			String result = null;
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
			if ("-600".equals(value)) {
				result = "正常结清";
			} else if ("-500".equals(value)) {
				result = "提前结清";
			} else if ("-400".equals(value)) {
				result = "合同作废";
			} else if ("-300".equals(value)) {
				result = "撤销";
			} else if ("-200".equals(value)) {
				result = "婉拒";
			} else if ("-100".equals(value)) {
				result = "已核准未拨款已过期";
			} else if ("100".equals(value)) {
				result = "调查中";
			} else if ("200".equals(value)) {
				result = "业务主管审批中";
			} else if ("300".equals(value)) {
				result = "业务副总审批中";
			} else if ("400".equals(value)) {
				result = "初级评审";
			} else if ("500".equals(value)) {
				result = "一级评审";
			} else if ("600".equals(value)) {
				result = "二级评审";
			} else if ("700".equals(value)) {
				result = "三级评审";
			} else if ("800".equals(value)) {
				result = "四级评审";
			} else if ("900".equals(value)) {
				result = "已核准文件准备中";
			} else if ("1000".equals(value)) {
				result = "已核准文审中";
			} else if ("1100".equals(value)) {
				result = "拨款审批中";
			} else if ("1200".equals(value)) {
				result = "付款审批中	";
			} else if ("1300".equals(value)) {
				result = "已拨款";
			} else {
				result = "";
			}
			out.print(result);
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
