package com.brick.collection.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class Pay implements Payable {
    public static final int TYPE_BEGIN_CORPUS = 3;
    public static final int TYPE_END_CORPUS = 2;
    public static final int TYPE_BEGIN_ACCRUAL = 1;
    public static final int TYPE_END_ACCRUAL = 0;
    public static final int MONTH_IN_YEAR = 12;
    public static final int DAY_IN_YEAR = 360;
    public static final long MS_IN_DAY = 24 * 60 * 60 * 1000;
    public static final int PERCENT_NUM = 100;
    public static final double HAND_PRICE_RATE = 0.5;
    public static final double DEFAULT_BASE_RATE = 15;
    private ChangeRateStrategy changeRateStrategy = null;
    private double totalPrice = 0D;
   // private double saveRate = 0D;
    private double firstRate = 0D;
    private double pledgeRate = 0D;
    private double baseRate = 0D;
    private double upRate = DEFAULT_BASE_RATE;
    private int type = TYPE_END_ACCRUAL;
    private int num = MONTH_IN_YEAR;
    private Date startDate = null;
    private int unit = MONTH_IN_YEAR;
    private List<PayItem> payItems = null;
    private double nowPrice = 0D;
   // private double savePrice = 0D;
    private double firstPrice = 0D;
    private double pledgePrice = 0D;
    private double restPrice = 0D;
    private double handPrice = 0D;
    private double handRate = HAND_PRICE_RATE;
    private double yearRate = 0D;
    private double unitRate = 0D;
    private double pmt = 0D;
    private double totalMonthPrice = 0D;
    private double totalOwnPrice = 0D;
    private double totalRenPrice = 0D;
    private double totalShortMonthPrice = 0D;
    private double totalShortOwnPrice = 0D;
    private double totalShortRenPrice = 0D;
    private int numMonths = num;
    
//    private double salesTaxRate = 0d;
//    private int pledgeWay = 0;
//    private int pledgePeriod = 0;
//    private double insureRate = 0d;

	public void prepare() {
        this.updateYearRate();

        nowPrice = totalPrice;

		// savePrice = (totalPrice * saveRate) / PERCENT_NUM;

		// firstPrice = (totalPrice * firstRate) / PERCENT_NUM;

		// restPrice = totalPrice - firstPrice;
        // restPrice = totalPrice - pledgePrice;
        restPrice = totalPrice;
        
		// handPrice = (restPrice * numMonths) / MONTH_IN_YEAR * (handRate  /100);
        //PMT公式
        pmt = PmtUtils.getPmt(restPrice, unitRate, num, type);
        //pmt = (Double.isNaN(pmt))?0:pmt;
        pmt = (Double.isNaN(pmt)) ? (restPrice/num) : pmt;

        changeRateStrategy = new ChangeRateStrategy(yearRate, startDate,
                type, unit);
    }

    public void updateYearRate() {
        // this.yearRate = this.baseRate * (1 + (this.upRate / PERCENT_NUM));
        this.unitRate = yearRate / unit / PERCENT_NUM;
    }

    public void createItems() {
        payItems = new ArrayList<PayItem>();

        for (int i = 0; i < num; i++) {
            PayItem payItem = new PayItem();
            payItem.updateValues(i, this);
            payItems.add(payItem);
        }
    }

    public void createStatistic() {
        totalMonthPrice = 0;
        totalOwnPrice = 0;
        totalRenPrice = 0;

        totalShortMonthPrice = 0;
        totalShortOwnPrice = 0;
        totalShortRenPrice = 0;

        for (int i = 0; i < num; i++) {
            PayItem payItem = payItems.get(i);
           
            totalMonthPrice += payItem.getMonthPrice();
            totalOwnPrice += payItem.getOwnPrice();
            totalRenPrice += payItem.getRenPrice();

            totalShortMonthPrice += payItem.getShortMonthPrice();
            totalShortOwnPrice += payItem.getShortOwnPrice();
            totalShortRenPrice += payItem.getShortRenPrice();
        }
    }

    public void calculate() {
    	//准备年利率  PMT
        this.prepare();
        //创建还款计划
        this.createItems();
        //静态化配平
        this.createStatistic();
    }

    public void updateMonthPrice(int index, double monthPrice) {
        PayItem payItem = payItems.get(index - 1);
        payItem.setMonthPrice(monthPrice);
        payItem.lock();
    }

    public void recalculate() {
        List<PayItem> lockedPayItems = new ArrayList<PayItem>();

        int count = 0;
		double sum = 0;
        for (PayItem payItem : payItems) {
            if (payItem.isLocked()) {
                lockedPayItems.add(payItem);
                count ++ ;
				sum += payItem.getMonthPrice();
            }
        }

        double price = this.restPrice;
        double lockedPmt = PmtUtils.getLockedPmt(restPrice, unitRate, num, type, lockedPayItems);
        lockedPmt = Double.isNaN(lockedPmt) ? (restPrice - sum)/(num - count): lockedPmt ;

        for (int i = 0; i < num; i++) {
            double renPrice = PayUtils.calculateRenPrice(type, i, price,
                    unitRate);
            PayItem item = payItems.get(i);
            double monthPrice = lockedPmt;

            if (item.isLocked()) {
                monthPrice = item.getMonthPrice();
            }
// 这里的price是合同总价款
            double ownPrice = monthPrice - renPrice;
            double lastPrice = price - ownPrice;
            item.setMonthPrice(monthPrice);
            item.setOwnPrice(ownPrice);
            item.setRenPrice(renPrice);
            item.setLastPrice(lastPrice);

            price = lastPrice;
        }

        this.createStatistic();
    }

    public void recalculateForChangeRate(int gapIndex) {
        List<PayItem> lockedPayItems = new ArrayList<PayItem>();

        for (int i = gapIndex; i < num; i++) {
            PayItem payItem = payItems.get(i);

            if (payItem.isLocked()) {
                lockedPayItems.add(payItem);
            }
        }

        double gapLastPrice = restPrice;

        if (gapIndex != 0) {
            gapLastPrice = payItems.get(gapIndex - 1).getLastPrice();
        }

        double price = gapLastPrice;
        double lockedPmt = PmtUtils.getPmtForChangeRate(price, unitRate,
                num - gapIndex, lockedPayItems, gapIndex);

        for (int i = gapIndex; i < num; i++) {
            double renPrice = PayUtils.calculateRenPrice(type, i, price,
                    unitRate);
            PayItem item = payItems.get(i);
            double monthPrice = lockedPmt;

            if (item.isLocked()) {
                monthPrice = item.getMonthPrice();
            }

            double ownPrice = monthPrice - renPrice;
            double lastPrice = price - ownPrice;
            item.setMonthPrice(monthPrice);
            item.setOwnPrice(ownPrice);
            item.setRenPrice(renPrice);
            item.setLastPrice(lastPrice);

            price = lastPrice;
        }

        this.createStatistic();
    }

    public void changeRate(Date changeDate, double changeRate) {
        this.setBaseRate(changeRate);

        changeRateStrategy.process(changeDate, yearRate, this);
    }

    public void changeNum(int passedIndex, int destNum) {
        if (num < destNum) {
            for (int i = num; i < destNum; i++) {
                PayItem payItem = new PayItem();
                payItem.setIndex(i + 1);
                payItem.setRate(yearRate);

                if (startDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);

                    if ((type == TYPE_BEGIN_ACCRUAL)
                            || (type == TYPE_BEGIN_CORPUS)) {
                        calendar.add(Calendar.MONTH,
                            (i * MONTH_IN_YEAR) / unit);
                    } else {
                        calendar.add(Calendar.MONTH,
                            ((i * MONTH_IN_YEAR) / unit) + 1);
                    }

                    payItem.setPayDate(calendar.getTime());
                }

                payItems.add(payItem);
            }
        } else {
            for (int i = num - 1; i >= destNum; i--) {
                payItems.remove(i);
            }
        }

        this.num = destNum;

        List<PayItem> lockedPayItems = new ArrayList<PayItem>();
        int beginIndex = passedIndex - 1;

        if (beginIndex < 0) {
            beginIndex = 0;
        }

        for (int i = beginIndex; i < num; i++) {
            PayItem payItem = payItems.get(i);

            if (payItem.isLocked()) {
                lockedPayItems.add(payItem);
            }
        }

        double passedLastPrice = restPrice;

        if (passedIndex != 0) {
            passedLastPrice = payItems.get(passedIndex - 1).getLastPrice();
        }

        double price = passedLastPrice;
        double lockedPmt = PmtUtils.getPmtForChangeNum(price, unitRate,
                num - passedIndex, type, lockedPayItems, passedIndex);

        for (int i = passedIndex; i < num; i++) {
            double renPrice = PayUtils.calculateRenPrice(type, i, price,
                    unitRate);
            PayItem item = payItems.get(i);
            double monthPrice = lockedPmt;

            if (item.isLocked()) {
                monthPrice = item.getMonthPrice();
            }

            if ((type == TYPE_BEGIN_ACCRUAL) || (type == TYPE_END_ACCRUAL)) {
                double ownPrice = monthPrice - renPrice;
                double lastPrice = price - ownPrice;
                item.setMonthPrice(monthPrice);
                item.setOwnPrice(ownPrice);
                item.setRenPrice(renPrice);
                item.setLastPrice(lastPrice);

                price = lastPrice;
            } else {
                double ownPrice = passedLastPrice / (destNum - passedIndex);
                monthPrice = ownPrice + renPrice;

                double lastPrice = price - ownPrice;
                item.setMonthPrice(monthPrice);
                item.setOwnPrice(ownPrice);
                item.setRenPrice(renPrice);
                item.setLastPrice(lastPrice);

                price = lastPrice;
            }
        }

        this.createStatistic();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

//    public double getSaveRate() {
//        return saveRate;
//    }
//
//    public void setSaveRate(double saveRate) {
//        this.saveRate = saveRate;
//    }

    public double getFirstRate() {
        return firstRate;
    }

    public void setFirstRate(double firstRate) {
        this.firstRate = firstRate;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
        this.updateYearRate();
    }

    public double getUpRate() {
        return upRate;
    }

    public void setUpRate(double upRate) {
        this.upRate = upRate;
        this.updateYearRate();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date getStartDate() {
        if (startDate != null) {
            return (Date) startDate.clone();
        } else {
            return null;
        }
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        }
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
        this.updateYearRate();
    }

    public double getUnitRate() {
        return unitRate;
    }

    public List<PayItem> getPayItems() {
        return payItems;
    }

    public double getMonthPrice() {
        return pmt;
    }

    public double getNowPrice() {
        return nowPrice;
    }

//    public double getSavePrice() {
//        return savePrice;
//    }

    public double getRestPrice() {
        return restPrice;
    }

    public double getFirstPrice() {
        return firstPrice;
    }

    public double getHandPrice() {
        return handPrice;
    }

    public double getYearRate() {
        return yearRate;
    }

    public double getTotalMonthPrice() {
        return totalMonthPrice;
    }

    public double getTotalOwnPrice() {
        return totalOwnPrice;
    }

    public double getTotalRenPrice() {
        return totalRenPrice;
    }

    public double getTotalShortMonthPrice() {
        return totalShortMonthPrice;
    }

    public double getTotalShortOwnPrice() {
        return totalShortOwnPrice;
    }

    public double getTotalShortRenPrice() {
        return totalShortRenPrice;
    }

	public double getHandRate() {
		return handRate;
	}

	public void setHandRate(double handRate) {
		this.handRate = handRate;
	}

	public int getNumMonths() {
		return numMonths;
	}

	public void setNumMonths(int numMonths) {
		this.numMonths = numMonths;
	}

	/**
	 * @return the pledgePrice
	 */
	public double getPledgePrice() {
		return pledgePrice;
	}

	/**
	 * @param pledgePrice the pledgePrice to set
	 */
	public void setPledgePrice(double pledgePrice) {
		this.pledgePrice = pledgePrice;
	}

	/**
	 * @param yearRate the yearRate to set
	 */
	public void setYearRate(double yearRate) {
		this.yearRate = yearRate;
	}

	/**
	 * @return the salesTaxRate
	 *//*
	public double getSalesTaxRate() {
		return salesTaxRate;
	}

	*//**
	 * @param salesTaxRate the salesTaxRate to set
	 *//*
	public void setSalesTaxRate(double salesTaxRate) {
		this.salesTaxRate = salesTaxRate;
	}
    *//**
	 * @return the pledgeWay
	 *//*
	public int getPledgeWay() {
		return pledgeWay;
	}

	*//**
	 * @param pledgeWay the pledgeWay to set
	 *//*
	public void setPledgeWay(int pledgeWay) {
		this.pledgeWay = pledgeWay;
	}
	*//**
	 * @return the pledgePeriod
	 *//*
	public int getPledgePeriod() {
		return pledgePeriod;
	}

	*//**
	 * @param pledgePeriod the pledgePeriod to set
	 *//*
	public void setPledgePeriod(int pledgePeriod) {
		this.pledgePeriod = pledgePeriod;
	}

	*//**
	 * @return the insureRate
	 *//*
	public double getInsureRate() {
		return insureRate;
	}

	*//**
	 * @param insureRate the insureRate to set
	 *//*
	public void setInsureRate(double insureRate) {
		this.insureRate = insureRate;
	}*/

}
