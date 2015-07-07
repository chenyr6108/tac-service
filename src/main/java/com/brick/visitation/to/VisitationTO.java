package com.brick.visitation.to;

import java.sql.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brick.base.to.BaseTo;
import com.brick.util.DateUtil;
import com.brick.util.StringUtils;

public class VisitationTO extends BaseTo {
	
	Log logger = LogFactory.getLog(this.getClass());
	
	private static final long serialVersionUID = 1L;
	/*
	T_PRJT_VISIT(
	STATUS INT,
	CREDIT_ID INT ,
	VISIT_STATUS INT, -- -1：业务主管驳回， 0：初始值， 1：业务申请:2：待访厂（业务主管通过），3：已访厂
	VISIT_AREA NVARCHAR(50),
	REAL_VISIT_DATE DATE,
	REAL_VISIT_DATE_TIME NVARCHAR(5),
	REAL_VISITOR INT,
	BUSI_HOPE_VISIT_DATE DATE,
	BUSI_HOPE_VISIT_DATE_TIME NVARCHAR(5),
	EXAM_ASSI_VISIT_DATE DATE,
	EXAM_ASSI_VISIT_DATE_TIME NVARCHAR(5),
	EXAM_ASSI_VISITOR INT
	)
	*/
	private String visit_id;
	private String credit_id;
	private String visit_area;
	private String hope_visit_date_str;
	private String hope_visit_time;
	private int status;
	private int visit_status;
	private String assi_visit_date_str;
	private String assi_visit_date_time;
	private int assi_visitor;
	private String real_visit_date_str;
	private String real_visit_date_time;
	private int real_visitor;
	private Date hope_visit_date;
	private Date assi_visit_date;
	private Date real_visit_date;
	private String auth_memo;
	
	private String reject_memo;
	
	private String none_visit_memo;
	private String none_visit_reason;
	
	
	
	public VisitationTO(){
		this.setTable_name("T_PRJT_VISIT");
		this.setPrimary_key("ID");
	}
	
	public String getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(String visit_id) {
		this.visit_id = visit_id;
		this.setKey_value(visit_id);
	}
	public String getCredit_id() {
		return credit_id;
	}
	public void setCredit_id(String credit_id) {
		this.credit_id = credit_id;
	}
	public String getVisit_area() {
		return visit_area;
	}
	public void setVisit_area(String visit_area) {
		this.visit_area = visit_area;
	}
	public String getHope_visit_date_str() {
		if (StringUtils.isEmpty(this.hope_visit_date_str)) {
			this.hope_visit_date_str = DateUtil.dateToString(this.hope_visit_date);
		}
		return hope_visit_date_str;
	}
	public void setHope_visit_date_str(String hope_visit_date_str) {
		this.hope_visit_date_str = hope_visit_date_str;
	}
	public String getHope_visit_time() {
		return hope_visit_time;
	}
	public void setHope_visit_time(String hope_visit_time) {
		this.hope_visit_time = hope_visit_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getVisit_status() {
		return visit_status;
	}
	public void setVisit_status(int visit_status) {
		this.visit_status = visit_status;
	}
	public String getAssi_visit_date_str() {
		return assi_visit_date_str;
	}
	public void setAssi_visit_date_str(String assi_visit_date_str) {
		this.assi_visit_date_str = assi_visit_date_str;
	}
	public String getAssi_visit_date_time() {
		return assi_visit_date_time;
	}
	public void setAssi_visit_date_time(String assi_visit_date_time) {
		this.assi_visit_date_time = assi_visit_date_time;
	}
	public int getAssi_visitor() {
		return assi_visitor;
	}
	public void setAssi_visitor(Integer assi_visitor) {
		if (assi_visitor == null) {
			assi_visitor = 0;
		}
		this.assi_visitor = assi_visitor;
	}
	public String getReal_visit_date_str() {
		return real_visit_date_str;
	}
	public void setReal_visit_date_str(String real_visit_date_str) {
		this.real_visit_date_str = real_visit_date_str;
	}
	public String getReal_visit_date_time() {
		return real_visit_date_time;
	}
	public void setReal_visit_date_time(String real_visit_date_time) {
		this.real_visit_date_time = real_visit_date_time;
	}
	public int getReal_visitor() {
		return real_visitor;
	}
	public void setReal_visitor(Integer real_visitor) {
		
		this.real_visitor = real_visitor == null ? 0 : real_visitor;
	}
	public Date getHope_visit_date() {
		try {
			if (this.hope_visit_date == null && !StringUtils.isEmpty(this.hope_visit_date_str)) {
				this.hope_visit_date = new Date(DateUtil.strToDay(this.hope_visit_date_str).getTime());
			}
		} catch (Exception e) {
			logger.warn("hope_visit_date 格式错误[" + hope_visit_date + "]");
		}
		
		return hope_visit_date;
	}
	public void setHope_visit_date(Date hope_visit_date) {
		this.hope_visit_date = hope_visit_date;
	}
	public Date getAssi_visit_date() {
		if (this.assi_visit_date == null && this.assi_visit_date_str != null) {
			this.assi_visit_date = new Date(DateUtil.strToDay(this.assi_visit_date_str).getTime());
		}
		return assi_visit_date;
	}
	public void setAssi_visit_date(Date assi_visit_date) {
		this.assi_visit_date = assi_visit_date;
		if (this.assi_visit_date_str == null && assi_visit_date != null) {
			this.assi_visit_date_str = DateUtil.dateToStr(assi_visit_date);
		}
	}
	public Date getReal_visit_date() {
		if (this.real_visit_date == null && this.real_visit_date_str != null) {
			this.real_visit_date = new Date(DateUtil.strToDay(this.real_visit_date_str).getTime());
		}
		return real_visit_date;
	}
	public void setReal_visit_date(Date real_visit_date) {
		this.real_visit_date = real_visit_date;
	}
	
	public String getVisitTimeStr(String visit_time) throws Exception{
		int flag = Integer.parseInt(visit_time);
		if (flag == 0) {
			return "全天";
		} else if (flag == 1) {
			return "上午";
		} else if (flag == 2) {
			return "下午";
		} else {
			throw new Exception("时间不正确。");
		}
	}

	public String getReject_memo() {
		return reject_memo;
	}

	public void setReject_memo(String reject_memo) {
		this.reject_memo = reject_memo;
	}

	public String getAuth_memo() {
		return auth_memo;
	}

	public void setAuth_memo(String auth_memo) {
		this.auth_memo = auth_memo;
	}

	public String getNone_visit_memo() {
		return none_visit_memo;
	}

	public void setNone_visit_memo(String none_visit_memo) {
		this.none_visit_memo = none_visit_memo;
	}

	public String getNone_visit_reason() {
		return none_visit_reason;
	}

	public void setNone_visit_reason(String none_visit_reason) {
		this.none_visit_reason = none_visit_reason;
	}
	
}
