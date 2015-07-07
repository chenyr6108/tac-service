package com.brick.information.to;

import java.util.Date;

import com.ibm.icu.math.BigDecimal;

public class InforDataOfWeek {
        
	     private String id;
	     private int total;                          //总数
	     private int totalOfFinish;                  //结案总数
	     private int totalOfUnFinish;                //没完成总数
	     private BigDecimal totalOfScale;            //总完成比例
	     private int addNumOfWeek;                   //本期新增
	     private int addNumOfWeek_fis;               //本期完成
	     private BigDecimal weekOfScale;             //本期完成比
	     private Date staticDate;                    //统计时间
	     private String backDate;
	     
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public int getTotalOfFinish() {
			return totalOfFinish;
		}
		public void setTotalOfFinish(int totalOfFinish) {
			this.totalOfFinish = totalOfFinish;
		}
		public int getTotalOfUnFinish() {
			return totalOfUnFinish;
		}
		public void setTotalOfUnFinish(int totalOfUnFinish) {
			this.totalOfUnFinish = totalOfUnFinish;
		}

		public BigDecimal getTotalOfScale() {
			return totalOfScale;
		}
		public void setTotalOfScale(BigDecimal totalOfScale) {
			this.totalOfScale = totalOfScale;
		}
		public BigDecimal getWeekOfScale() {
			return weekOfScale;
		}
		public void setWeekOfScale(BigDecimal weekOfScale) {
			this.weekOfScale = weekOfScale;
		}
		public int getAddNumOfWeek() {
			return addNumOfWeek;
		}
		public void setAddNumOfWeek(int addNumOfWeek) {
			this.addNumOfWeek = addNumOfWeek;
		}
		public int getAddNumOfWeek_fis() {
			return addNumOfWeek_fis;
		}
		public void setAddNumOfWeek_fis(int addNumOfWeek_fis) {
			this.addNumOfWeek_fis = addNumOfWeek_fis;
		}

		public Date getStaticDate() {
			return staticDate;
		}
		public void setStaticDate(Date staticDate) {
			this.staticDate = staticDate;
		}
		public String getBackDate() {
			return backDate;
		}
		public void setBackDate(String backDate) {
			this.backDate = backDate;
		}
}
