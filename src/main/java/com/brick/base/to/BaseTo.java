package com.brick.base.to;

import java.io.Serializable;
import java.util.Date;

import com.brick.util.DateUtil;

public class BaseTo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Date modify_date;
	private String modify_by;
	private Date create_date;
	private String create_by;
	private Date auth_date;
	private String auth_by;
	private String content;
	private String create_date_str;
	private String modify_date_str;
	private String auth_date_str;
	
	private String table_name;
	private String primary_key;
	private String key_value;
	private String resource_code;
	
	public Date getModify_date() {
		try {
			if (this.modify_date == null && this.modify_date_str != null) {
				this.modify_date = DateUtil.parseDateWithMillisecond(modify_date_str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modify_date;
	}
	public void setModify_date(Date modify_date) {
		this.modify_date = modify_date;
		if (this.modify_date_str == null && modify_date != null) {
			this.modify_date_str = DateUtil.formatDateWithMillisecond(modify_date);
		}
	}
	public String getModify_by() {
		return modify_by;
	}
	public void setModify_by(String modify_by) {
		this.modify_by = modify_by;
	}
	public Date getCreate_date() {
		try {
			if (this.create_date == null && this.create_date_str != null) {
				this.create_date = DateUtil.parseDateWithMillisecond(this.create_date_str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public String getCreate_by() {
		return create_by;
	}
	public void setCreate_by(String create_by) {
		this.create_by = create_by;
	}
	public Date getAuth_date() {
		try {
			if (this.auth_date == null && this.auth_date_str != null) {
				this.auth_date = DateUtil.parseDateWithMillisecond(this.auth_date_str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return auth_date;
	}
	public void setAuth_date(Date auth_date) {
		this.auth_date = auth_date;
	}
	public String getAuth_by() {
		return auth_by;
	}
	public void setAuth_by(String auth_by) {
		this.auth_by = auth_by;
	}
	public String getCreate_date_str() {
		if (this.create_date != null && this.create_date_str == null) {
			this.create_date_str = DateUtil.formatDateWithMillisecond(this.create_date);
		}
		return create_date_str;
	}
	public void setCreate_date_str(String create_date_str) {
		this.create_date_str = create_date_str;
	}
	public String getModify_date_str() {
		if (this.modify_date != null && this.modify_date_str == null) {
			this.modify_date_str = DateUtil.formatDateWithMillisecond(this.modify_date);
		}
		return modify_date_str;
	}
	public void setModify_date_str(String modify_date_str) {
		this.modify_date_str = modify_date_str;
	}
	public String getAuth_date_str() {
		if (this.auth_date != null && this.auth_date_str == null) {
			this.auth_date_str = DateUtil.formatDateWithMillisecond(this.auth_date);
		}
		return auth_date_str;
	}
	public void setAuth_date_str(String auth_date_str) {
		this.auth_date_str = auth_date_str;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getPrimary_key() {
		return primary_key;
	}
	public void setPrimary_key(String primary_key) {
		this.primary_key = primary_key;
	}
	public String getKey_value() {
		return key_value;
	}
	public void setKey_value(String key_value) {
		this.key_value = key_value;
	}
	public String getResource_code() {
		return resource_code;
	}
	public void setResource_code(String resource_code) {
		this.resource_code = resource_code;
	}

}
