package com.brick.insurance.to;

import com.brick.base.to.BaseTo;

public class InsuCompanyTo extends BaseTo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int incp_id;
	private String incp_name;
	private String incp_mail;
	private String incp_code;
	private String insu_way;
	private String insu_way_code;
	private String incp_insu_code;
	private String is_renewal;
	private String insu_type;
	public int getIncp_id() {
		return incp_id;
	}
	public void setIncp_id(int incp_id) {
		this.incp_id = incp_id;
	}
	public String getIncp_name() {
		return incp_name;
	}
	public void setIncp_name(String incp_name) {
		this.incp_name = incp_name;
	}
	public String getIncp_mail() {
		return incp_mail;
	}
	public void setIncp_mail(String incp_mail) {
		this.incp_mail = incp_mail;
	}
	public String getIncp_code() {
		return incp_code;
	}
	public void setIncp_code(String incp_code) {
		this.incp_code = incp_code;
	}
	public String getInsu_way() {
		return insu_way;
	}
	public void setInsu_way(String insu_way) {
		this.insu_way = insu_way;
	}
	public String getInsu_way_code() {
		return insu_way_code;
	}
	public void setInsu_way_code(String insu_way_code) {
		this.insu_way_code = insu_way_code;
	}
	public String getIncp_insu_code() {
		return incp_insu_code;
	}
	public void setIncp_insu_code(String incp_insu_code) {
		this.incp_insu_code = incp_insu_code;
	}
	public String getIs_renewal() {
		return is_renewal;
	}
	public void setIs_renewal(String is_renewal) {
		this.is_renewal = is_renewal;
	}
	public String getInsu_type() {
		return insu_type;
	}
	public void setInsu_type(String insu_type) {
		this.insu_type = insu_type;
	}
	
}
