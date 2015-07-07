package com.brick.base.to;

public class DataDictionaryTo extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type;
	private String flag;
	private String code;
	private Integer defaultValue;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Integer defaultValue) {
		this.defaultValue = defaultValue;
	}
}
