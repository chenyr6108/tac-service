package com.brick.estimatePay.to;

import java.util.Date;

public class EstimatePayJobTo {
     
	    private String id;                   //主键
	    private Date create_time;            //job创建时间
	    private int dept_id;                 //办事处ID
	    private String dept_name;            //办事处名称
	    private int pay_count;
	    private double pay_total;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Date getCreate_time() {
			return create_time;
		}
		public void setCreate_time(Date create_time) {
			this.create_time = create_time;
		}
		public int getDept_id() {
			return dept_id;
		}
		public void setDept_id(int dept_id) {
			this.dept_id = dept_id;
		}
		public String getDept_name() {
			return dept_name;
		}
		public void setDept_name(String dept_name) {
			this.dept_name = dept_name;
		}
		public int getPay_count() {
			return pay_count;
		}
		public void setPay_count(int pay_count) {
			this.pay_count = pay_count;
		}
		public double getPay_total() {
			return pay_total;
		}
		public void setPay_total(double pay_total) {
			this.pay_total = pay_total;
		}


}
