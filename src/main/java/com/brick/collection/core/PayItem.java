package com.brick.collection.core;

import java.util.Calendar;
import java.util.Date;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class PayItem {
    public static final double HUNDRED = 100D;
    private int index = 0;
    private Date payDate = null;
    private double monthPrice = 0;
    private double ownPrice = 0;
    private double renPrice = 0;
    private double lastPrice = 0;
    private boolean locked = false;
    private boolean passed = false;
    private double rate = 0;
    private double shortMonthPrice = 0;
    private double shortOwnPrice = 0;
    private double shortRenPrice = 0;
    private double shortLastPrice = 0;
    
    private double depositPrice = 0;
    private double irrPrice = 0;
    private double irrMonthPrice = 0;
    private double salesTax = 0;

  //Add by Michael 2012 01/15
    private double  currentRenPrice=0;   //当期利息
    private double  currentNetFinance=0;  //当期净本金
    
    //Add by Michael 2012 09-24 
    private double valueAdded=0;   //增值税

	public double getValueAdded() {
		return valueAdded;
	}

	public void setValueAdded(double valueAdded) {
		this.valueAdded = valueAdded;
	}

	public double getCurrentRenPrice() {
		return currentRenPrice;
	}

	public void setCurrentRenPrice(double currentRenPrice) {
		this.currentRenPrice = currentRenPrice;
	}

	public double getCurrentNetFinance() {
		return currentNetFinance;
	}

	public void setCurrentNetFinance(double currentNetFinance) {
		this.currentNetFinance = currentNetFinance;
	}

	public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Date getPayDate() {
        if (payDate != null) {
            return (Date) payDate.clone();
        } else {
            return null;
        }
    }

    public void setPayDate(Date payDate) {
        if (payDate != null) {
            this.payDate = (Date) payDate.clone();
        }
    }

    public double getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(double monthPrice) {
        this.monthPrice = monthPrice;
    }

    public double getOwnPrice() {
        return ownPrice;
    }

    public void setOwnPrice(double ownPrice) {
        this.ownPrice = ownPrice;
    }

    public double getRenPrice() {
        return renPrice;
    }

    public void setRenPrice(double renPrice) {
        this.renPrice = renPrice;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public boolean isPassed() {
        return passed;
    }

    public void pass() {
        passed = true;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getShortMonthPrice() {
        return shortMonthPrice;
    }

    public double getShortOwnPrice() {
        return shortOwnPrice;
    }

    public double getShortRenPrice() {
        return shortRenPrice;
    }

    public double getShortLastPrice() {
        return shortLastPrice;
    }

    public void updateValues(int num, Pay pay) {
        index = num + 1;

        double unitRate = pay.getUnitRate();
        double price = 0;

        if (num == 0) {
            price = pay.getRestPrice();
        } else {
        	PayItem tempPayItem = pay.getPayItems().get(num - 1);
            price = tempPayItem.getLastPrice();
        }

        double shortPrice = Math.round(price / HUNDRED) * HUNDRED;

        if (num == 0) {
            if ((pay.getType() == Pay.TYPE_BEGIN_ACCRUAL)
                    || (pay.getType() == Pay.TYPE_BEGIN_CORPUS)) {
                renPrice = 0;
            } else {
                renPrice = unitRate * pay.getRestPrice();
            }
        } else {
            renPrice = unitRate * price;
        }

        if (pay.getStartDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pay.getStartDate());
            calendar.add(Calendar.MONTH,
                    (num * Pay.MONTH_IN_YEAR) / pay.getUnit());
            /*
            if ((pay.getType() == Pay.TYPE_BEGIN_ACCRUAL)
                    || (pay.getType() == Pay.TYPE_BEGIN_CORPUS)) {
                calendar.add(Calendar.MONTH,
                    (num * Pay.MONTH_IN_YEAR) / pay.getUnit());
            } else {
                calendar.add(Calendar.MONTH,
                    ((num + 1) * Pay.MONTH_IN_YEAR) / pay.getUnit() );
            }
			*/
            payDate = calendar.getTime();
        }

        rate = pay.getYearRate();

        if ((pay.getType() == Pay.TYPE_BEGIN_ACCRUAL)
                || (pay.getType() == Pay.TYPE_END_ACCRUAL)) {
            monthPrice = pay.getMonthPrice();

            ownPrice = monthPrice - renPrice;

            lastPrice = price - ownPrice;
        } else {
            ownPrice = pay.getRestPrice() / pay.getNum();

            monthPrice = ownPrice + renPrice;

            lastPrice = price - ownPrice;
        }
        //
        /*salesTax = renPrice * pay.getSalesTaxRate();
        salesTax = Math.round(salesTax * HUNDRED) / HUNDRED;
        
        if (pay.getPledgeWay() == 1) {
        	depositPrice =  Math.round( pay.getPledgePrice() / pay.getNum() * HUNDRED) / HUNDRED;
        	irrMonthPrice = monthPrice - depositPrice;
        	irrPrice = irrMonthPrice - salesTax;
        }*/

        shortMonthPrice = Math.round(monthPrice * HUNDRED) / HUNDRED;
        shortRenPrice = Math.round(renPrice * HUNDRED) / HUNDRED;
        shortOwnPrice = shortMonthPrice - shortRenPrice;
        shortLastPrice = shortPrice - shortOwnPrice;
    }

	/**
	 * @return the depositPrice
	 */
	public double getDepositPrice() {
		return depositPrice;
	}

	/**
	 * @return the irrPrice
	 */
	public double getIrrPrice() {
		return irrPrice;
	}

	/**
	 * @return the irrMonthPrice
	 */
	public double getIrrMonthPrice() {
		return irrMonthPrice;
	}

	/**
	 * @return the salesTax
	 */
	public double getSalesTax() {
		return salesTax;
	}
}
