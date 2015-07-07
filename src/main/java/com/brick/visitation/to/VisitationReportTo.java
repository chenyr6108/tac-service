package com.brick.visitation.to;

import java.sql.Date;

import com.brick.base.to.BaseTo;
import com.brick.util.DateUtil;

public class VisitationReportTo extends BaseTo {
	/*
	T_PRJT_VISIT_REPORT
	STATUS INT,
	BUSI_NAME NVARCHAR(10),
	BUSI_MANAGER NVARCHAR(1),
	CUST_COMP_NAME NVARCHAR(50),
	CUST_APPLY_AMOUNT NVARCHAR(50),
	CUST_GUAR NVARCHAR(100),
	CUST_COMP_STATUS NVARCHAR(100),
	CUST_RELATION NVARCHAR(100),
	CUST_PRODUCT NVARCHAR(100),
	CUST_INVOICE NVARCHAR(50),
	LEGAL_NAME NVARCHAR(50),
	LEGAL_INFO NVARCHAR(100),
	LEGAL_ADDRESS NVARCHAR(100),
	LEGAL_EXPERIENCE NVARCHAR(2000),
	FACTORY_ENVI NVARCHAR(100),
	FACTORY_IS_SELF NVARCHAR(10),
	FACTORY_HAS_DORM NVARCHAR(10),
	FACTORY_HAS_MESS NVARCHAR(10),
	FACTORY_CLEAN NVARCHAR(50),
	FACTORY_WORKER NVARCHAR(50),
	FACTORY_JIA_DONG NVARCHAR(50),
	FACTORY_APPLY_USE_TO NVARCHAR(100),
	FACTORY_WORKER_ATTITUDE NVARCHAR(100),
	FACTORY_HAS_OVERALLS NVARCHAR(10),
	INVENTORY_STATUS NVARCHAR(100),
	INVENTORY_PUT_STATUS NVARCHAR(100),
	MANAGE_WORK_STATUS NVARCHAR(100),
	MANAGE_HAS_OVERALLS NVARCHAR(10),
	MANAGE_WORK_ATTITUDE NVARCHAR(100),
	ADDED_INFO NVARCHAR(1000),
	VISIT_RESULT INT,
	REASON  NVARCHAR(2000)
	*/
	
	private static final long serialVersionUID = 1L;
	
	private String report_id;
	private String visit_id;
	private Integer status;
	private String busi_name;
	private String busi_manager;
	private String cust_comp_name;
	private String cust_apply_amount;
	private String cust_guar;
	private String cust_comp_status;
	private String cust_comp_status2;
	private String cust_relation;
	private String cust_product;
	private String cust_product_process;
	private String cust_product_percent1;
	private String cust_product_percent2;
	private String cust_product_other;
	private String cust_invoice;
	private String legal_name;
	private String legal_position;
	private String legal_position_other;
	private String legal_age;
	private String legal_info;
	private String legal_address;
	private String legal_address2;
	private String legal_address_other;
	private String legal_experience;
	private String legal_experience_year1;
	private String legal_experience_year2;
	private String legal_experience_affiliated;
	private String legal_experience_affiliated_company;
	private String legal_experience_from;
	private String legal_experience_from_other;
	private String legal_experience_from_year;
	private String legal_experience_inherit;
	private String legal_experience_flag;
	
	
	private String factory_envi;
	private String factory_is_self;
	private String factory_has_dorm;
	private String factory_has_mess;
	private String factory_clean;
	private String factory_worker;
	private String factory_worker_class;
	private String factory_jia_dong;
	private String factory_apply_use_to;
	private String factory_apply_use_to_other;
	private String factory_worker_attitude;
	private String factory_has_overalls;
	private String inventory_status;
	private String inventory_put_status;
	private String manage_work_status;
	private String manage_has_overalls;
	private String manage_work_attitude;
	private String added_info;
	private Integer visit_result;
	private String reason;
	private String reason_s;
	private String reason_w;
	private String reason_o;
	private String reason_t;
	private Date real_visit_date;
	private String real_visit_date_str;
	private String real_visit_date_time;
	private Integer real_visitor;
	private String real_visitor_name;
	private String create_by_name;
	private String credit_id;
	
	private String sensor_name;
	private String cust_name;
	
	private String cust_type;
	private String contract_type;
	private String contract_type_other;
	
	private String authLeader;
	private String auth_leader_name;
	
	public VisitationReportTo(){
		setTable_name("T_PRJT_VISIT_REPORT");
		setPrimary_key("ID");
	}
	
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}
	public String getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(String visit_id) {
		this.visit_id = visit_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getBusi_name() {
		return busi_name;
	}
	public void setBusi_name(String busi_name) {
		this.busi_name = busi_name;
	}
	public String getBusi_manager() {
		return busi_manager;
	}
	public void setBusi_manager(String busi_manager) {
		this.busi_manager = busi_manager;
	}
	public String getCust_comp_name() {
		return cust_comp_name;
	}
	public void setCust_comp_name(String cust_comp_name) {
		this.cust_comp_name = cust_comp_name;
	}
	public String getCust_apply_amount() {
		return cust_apply_amount;
	}
	public void setCust_apply_amount(String cust_apply_amount) {
		this.cust_apply_amount = cust_apply_amount;
	}
	public String getCust_guar() {
		return cust_guar;
	}
	public void setCust_guar(String cust_guar) {
		this.cust_guar = cust_guar;
	}
	public String getCust_comp_status() {
		return cust_comp_status;
	}
	public void setCust_comp_status(String cust_comp_status) {
		this.cust_comp_status = cust_comp_status;
	}
	public String getCust_relation() {
		return cust_relation;
	}
	public void setCust_relation(String cust_relation) {
		this.cust_relation = cust_relation;
	}
	public String getCust_product() {
		return cust_product;
	}
	public void setCust_product(String cust_product) {
		this.cust_product = cust_product;
	}
	public String getCust_invoice() {
		return cust_invoice;
	}
	public void setCust_invoice(String cust_invoice) {
		this.cust_invoice = cust_invoice;
	}
	public String getLegal_name() {
		return legal_name;
	}
	public void setLegal_name(String legal_name) {
		this.legal_name = legal_name;
	}
	public String getLegal_info() {
		return legal_info;
	}
	public void setLegal_info(String legal_info) {
		this.legal_info = legal_info;
	}
	public String getLegal_address() {
		return legal_address;
	}
	public void setLegal_address(String legal_address) {
		this.legal_address = legal_address;
	}
	public String getLegal_experience() {
		return legal_experience;
	}
	public void setLegal_experience(String legal_experience) {
		this.legal_experience = legal_experience;
	}
	public String getFactory_envi() {
		return factory_envi;
	}
	public void setFactory_envi(String factory_envi) {
		this.factory_envi = factory_envi;
	}
	public String getFactory_is_self() {
		return factory_is_self;
	}
	public void setFactory_is_self(String factory_is_self) {
		this.factory_is_self = factory_is_self;
	}
	public String getFactory_has_dorm() {
		return factory_has_dorm;
	}
	public void setFactory_has_dorm(String factory_has_dorm) {
		this.factory_has_dorm = factory_has_dorm;
	}
	public String getFactory_has_mess() {
		return factory_has_mess;
	}
	public void setFactory_has_mess(String factory_has_mess) {
		this.factory_has_mess = factory_has_mess;
	}
	public String getFactory_clean() {
		return factory_clean;
	}
	public void setFactory_clean(String factory_clean) {
		this.factory_clean = factory_clean;
	}
	public String getFactory_worker() {
		return factory_worker;
	}
	public void setFactory_worker(String factory_worker) {
		this.factory_worker = factory_worker;
	}
	public String getFactory_jia_dong() {
		return factory_jia_dong;
	}
	public void setFactory_jia_dong(String factory_jia_dong) {
		this.factory_jia_dong = factory_jia_dong;
	}
	public String getFactory_apply_use_to() {
		return factory_apply_use_to;
	}
	public void setFactory_apply_use_to(String factory_apply_use_to) {
		this.factory_apply_use_to = factory_apply_use_to;
	}
	public String getFactory_worker_attitude() {
		return factory_worker_attitude;
	}
	public void setFactory_worker_attitude(String factory_worker_attitude) {
		this.factory_worker_attitude = factory_worker_attitude;
	}
	public String getFactory_has_overalls() {
		return factory_has_overalls;
	}
	public void setFactory_has_overalls(String factory_has_overalls) {
		this.factory_has_overalls = factory_has_overalls;
	}
	public String getInventory_status() {
		return inventory_status;
	}
	public void setInventory_status(String inventory_status) {
		this.inventory_status = inventory_status;
	}
	public String getInventory_put_status() {
		return inventory_put_status;
	}
	public void setInventory_put_status(String inventory_put_status) {
		this.inventory_put_status = inventory_put_status;
	}
	public String getManage_work_status() {
		return manage_work_status;
	}
	public void setManage_work_status(String manage_work_status) {
		this.manage_work_status = manage_work_status;
	}
	public String getManage_has_overalls() {
		return manage_has_overalls;
	}
	public void setManage_has_overalls(String manage_has_overalls) {
		this.manage_has_overalls = manage_has_overalls;
	}
	public String getManage_work_attitude() {
		return manage_work_attitude;
	}
	public void setManage_work_attitude(String manage_work_attitude) {
		this.manage_work_attitude = manage_work_attitude;
	}
	public String getAdded_info() {
		return added_info;
	}
	public void setAdded_info(String added_info) {
		this.added_info = added_info;
	}
	public Integer getVisit_result() {
		return visit_result;
	}
	public void setVisit_result(Integer visit_result) {
		this.visit_result = visit_result;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getReal_visit_date() {
		return real_visit_date;
	}
	public void setReal_visit_date(Date real_visit_date) {
		this.real_visit_date = real_visit_date;
	}
	public String getReal_visit_date_str() {
		if (this.real_visit_date_str == null && this.real_visit_date != null) {
			this.real_visit_date_str = DateUtil.dateToStr(this.real_visit_date);
		}
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
	public Integer getReal_visitor() {
		return real_visitor;
	}
	public void setReal_visitor(Integer real_visitor) {
		this.real_visitor = real_visitor;
	}
	public String getReal_visitor_name() {
		return real_visitor_name;
	}
	public void setReal_visitor_name(String real_visitor_name) {
		this.real_visitor_name = real_visitor_name;
	}
	public String getCreate_by_name() {
		return create_by_name;
	}
	public void setCreate_by_name(String create_by_name) {
		this.create_by_name = create_by_name;
	}
	public String getCredit_id() {
		return credit_id;
	}
	public void setCredit_id(String credit_id) {
		this.credit_id = credit_id;
	}

	public String getSensor_name() {
		return sensor_name;
	}

	public void setSensor_name(String sensor_name) {
		this.sensor_name = sensor_name;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getCust_comp_status2() {
		return cust_comp_status2;
	}

	public void setCust_comp_status2(String cust_comp_status2) {
		this.cust_comp_status2 = cust_comp_status2;
	}

	public String getCust_product_percent1() {
		return cust_product_percent1;
	}

	public void setCust_product_percent1(String cust_product_percent1) {
		this.cust_product_percent1 = cust_product_percent1;
	}

	public String getCust_product_percent2() {
		return cust_product_percent2;
	}

	public void setCust_product_percent2(String cust_product_percent2) {
		this.cust_product_percent2 = cust_product_percent2;
	}

	public String getCust_product_other() {
		return cust_product_other;
	}

	public void setCust_product_other(String cust_product_other) {
		this.cust_product_other = cust_product_other;
	}

	public String getLegal_position() {
		return legal_position;
	}

	public void setLegal_position(String legal_position) {
		this.legal_position = legal_position;
	}

	public String getLegal_position_other() {
		return legal_position_other;
	}

	public void setLegal_position_other(String legal_position_other) {
		this.legal_position_other = legal_position_other;
	}

	public String getLegal_age() {
		return legal_age;
	}

	public void setLegal_age(String legal_age) {
		this.legal_age = legal_age;
	}

	public String getLegal_address2() {
		return legal_address2;
	}

	public void setLegal_address2(String legal_address2) {
		this.legal_address2 = legal_address2;
	}

	public String getLegal_experience_year1() {
		return legal_experience_year1;
	}

	public void setLegal_experience_year1(String legal_experience_year1) {
		this.legal_experience_year1 = legal_experience_year1;
	}

	public String getLegal_experience_year2() {
		return legal_experience_year2;
	}

	public void setLegal_experience_year2(String legal_experience_year2) {
		this.legal_experience_year2 = legal_experience_year2;
	}

	public String getLegal_experience_affiliated() {
		return legal_experience_affiliated;
	}

	public void setLegal_experience_affiliated(String legal_experience_affiliated) {
		this.legal_experience_affiliated = legal_experience_affiliated;
	}

	public String getLegal_experience_affiliated_company() {
		return legal_experience_affiliated_company;
	}

	public void setLegal_experience_affiliated_company(
			String legal_experience_affiliated_company) {
		this.legal_experience_affiliated_company = legal_experience_affiliated_company;
	}

	public String getLegal_experience_from() {
		return legal_experience_from;
	}

	public void setLegal_experience_from(String legal_experience_from) {
		this.legal_experience_from = legal_experience_from;
	}

	public String getLegal_experience_from_other() {
		return legal_experience_from_other;
	}

	public void setLegal_experience_from_other(String legal_experience_from_other) {
		this.legal_experience_from_other = legal_experience_from_other;
	}

	public String getLegal_experience_from_year() {
		return legal_experience_from_year;
	}

	public void setLegal_experience_from_year(String legal_experience_from_year) {
		this.legal_experience_from_year = legal_experience_from_year;
	}

	public String getLegal_experience_inherit() {
		return legal_experience_inherit;
	}

	public void setLegal_experience_inherit(String legal_experience_inherit) {
		this.legal_experience_inherit = legal_experience_inherit;
	}

	public String getLegal_experience_flag() {
		return legal_experience_flag;
	}

	public void setLegal_experience_flag(String legal_experience_flag) {
		this.legal_experience_flag = legal_experience_flag;
	}

	public String getFactory_worker_class() {
		return factory_worker_class;
	}

	public void setFactory_worker_class(String factory_worker_class) {
		this.factory_worker_class = factory_worker_class;
	}

	public String getFactory_apply_use_to_other() {
		return factory_apply_use_to_other;
	}

	public void setFactory_apply_use_to_other(String factory_apply_use_to_other) {
		this.factory_apply_use_to_other = factory_apply_use_to_other;
	}

	public String getReason_s() {
		return reason_s;
	}

	public void setReason_s(String reason_s) {
		this.reason_s = reason_s;
	}

	public String getReason_w() {
		return reason_w;
	}

	public void setReason_w(String reason_w) {
		this.reason_w = reason_w;
	}

	public String getReason_o() {
		return reason_o;
	}

	public void setReason_o(String reason_o) {
		this.reason_o = reason_o;
	}

	public String getReason_t() {
		return reason_t;
	}

	public void setReason_t(String reason_t) {
		this.reason_t = reason_t;
	}

	public String getCust_type() {
		return cust_type;
	}

	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}

	public String getContract_type() {
		return contract_type;
	}

	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}

	public String getContract_type_other() {
		return contract_type_other;
	}

	public void setContract_type_other(String contract_type_other) {
		this.contract_type_other = contract_type_other;
	}

	public String getLegal_address_other() {
		return legal_address_other;
	}

	public void setLegal_address_other(String legal_address_other) {
		this.legal_address_other = legal_address_other;
	}

	public String getAuthLeader() {
		return authLeader;
	}

	public void setAuthLeader(String authLeader) {
		this.authLeader = authLeader;
	}

	public String getCust_product_process() {
		return cust_product_process;
	}

	public void setCust_product_process(String cust_product_process) {
		this.cust_product_process = cust_product_process;
	}

	public String getAuth_leader_name() {
		return auth_leader_name;
	}

	public void setAuth_leader_name(String auth_leader_name) {
		this.auth_leader_name = auth_leader_name;
	}
	
}
