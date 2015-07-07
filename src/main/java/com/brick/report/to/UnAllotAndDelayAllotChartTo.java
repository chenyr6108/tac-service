package com.brick.report.to;

import java.util.Date;

import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.SimpleDateFormat;

public class UnAllotAndDelayAllotChartTo {
             
	         private BigDecimal unAllotPay;           //未拨金额
	         private BigDecimal delayAllotPay;        //缓拨金额
	         private int unAllotCount;            //未拨件数
	         private int delayAllotCount;         //缓拨件数
	         private Date createOn;               //创建日期
	         private double unAllot_pay;
	         private double delayAllot_pay;
	         private String unAllotPayWithDisplay;
	         private String delayAllotPayWithDisplay;
	         public String getUnAllotPayWithDisplay() {
				return unAllotPayWithDisplay;
			}
			public void setUnAllotPayWithDisplay(String unAllotPayWithDisplay) {
				this.unAllotPayWithDisplay = unAllotPayWithDisplay;
			}
			public String getDelayAllotPayWithDisplay() {
				return delayAllotPayWithDisplay;
			}
			public void setDelayAllotPayWithDisplay(String delayAllotPayWithDisplay) {
				this.delayAllotPayWithDisplay = delayAllotPayWithDisplay;
			}
			private String backDate;
	         SimpleDateFormat df=new SimpleDateFormat();

			public String getBackDate() {
				return backDate;
			}
			public void setBackDate(String backDate) {
				this.backDate = backDate;
			}
			public BigDecimal getUnAllotPay() {
				return unAllotPay;
			}
			public double getUnAllot_pay() {
				return unAllot_pay;
			}
			public void setUnAllot_pay(double unAllot_pay) {
				this.unAllot_pay = unAllot_pay;
			}
			public double getDelayAllot_pay() {
				return delayAllot_pay;
			}
			public void setDelayAllot_pay(double delayAllot_pay) {
				this.delayAllot_pay = delayAllot_pay;
			}
			public void setUnAllotPay(BigDecimal unAllotPay) {
				this.unAllotPay = unAllotPay;
			}
			public BigDecimal getDelayAllotPay() {
				return delayAllotPay;
			}
			public void setDelayAllotPay(BigDecimal delayAllotPay) {
				this.delayAllotPay = delayAllotPay;
			}
			public Date getCreateOn() {
				return createOn;
			}
			public void setCreateOn(Date createOn) {
				this.createOn = createOn;
			}
			
			//无参构造 方法 
			public UnAllotAndDelayAllotChartTo() {
				super();
				this.delayAllotPay=new BigDecimal(this.delayAllot_pay);
				this.unAllotPay=new BigDecimal(this.unAllot_pay);
			}
			public UnAllotAndDelayAllotChartTo(BigDecimal unAllotPay,
					BigDecimal delayAllotPay, int unAllotCount,
					int delayAllotCount, String backDate,double unAllot_pay,double delayAllot_pay
					,String unAllotPayWithDisplay,String delayAllotPayWithDisplay) {
				super();
				this.unAllotPay = unAllotPay;
				this.delayAllotPay = delayAllotPay;
				this.unAllotCount = unAllotCount;
				this.delayAllotCount = delayAllotCount;
				this.backDate=backDate;
				this.unAllot_pay=unAllot_pay;
				this.delayAllot_pay=delayAllot_pay;
				this.unAllotPayWithDisplay=unAllotPayWithDisplay;
				this.delayAllotPayWithDisplay=delayAllotPayWithDisplay;
			}
			
			public int getUnAllotCount() {
				return unAllotCount;
			}
			public void setUnAllotCount(int unAllotCount) {
				this.unAllotCount = unAllotCount;
			}
			public int getDelayAllotCount() {
				return delayAllotCount;
			}
			public void setDelayAllotCount(int delayAllotCount) {
				this.delayAllotCount = delayAllotCount;
			}
}
