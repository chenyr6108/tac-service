package com.brick.dun.to;

import java.util.Date;

public class DunCaseTo {
          
	      private String id;
	      private String leaseCode;        //合同号
	      private int rectId;              //合同编号
	      private double balance;          //未结清的金额
	      private String creditCode;       //案件号
	      private int creditId;
	      private Date createtime;
		public Date getCreatetime() {
			return createtime;
		}
		public void setCreatetime(Date createtime) {
			this.createtime = createtime;
		}
		public int getCreditId() {
			return creditId;
		}
		public void setCreditId(int creditId) {
			this.creditId = creditId;
		}
		public String getLeaseCode() {
			return leaseCode;
		}
		public void setLeaseCode(String leaseCode) {
			this.leaseCode = leaseCode;
		}
		public int getRectId() {
			return rectId;
		}
		public void setRectId(int rectId) {
			this.rectId = rectId;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCreditCode() {
			return creditCode;
		}
		public void setCreditCode(String creditCode) {
			this.creditCode = creditCode;
		}
}
