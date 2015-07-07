package com.brick.collection.core;

import java.util.Calendar;
import java.util.Date;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class ChangeRateStrategy {
    private double yearRate;
    private Date startDate;
    private int type;
    private int unit;

    public ChangeRateStrategy(double yearRate, Date startDate, int type,
        int unit) {
        this.yearRate = yearRate;

        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        }

        this.type = type;
        this.unit = unit;
    }

    public void process(Date changeDate, double changeRate, Pay pay) {
        int gapIndex = 0;
        double gapRate = changeRate;

        if (startDate != null) {
            gapIndex = this.getGapIndex(changeDate);

            gapRate = calculateGapRate(gapIndex, changeRate, changeDate);
        }

        double lastPrice = pay.getRestPrice();

        if (gapIndex > 1) {
            lastPrice = pay.getPayItems().get(gapIndex - 2).getLastPrice();
        }

        PayItem item = pay.getPayItems().get(0);

        if (gapIndex > 0) {
            item = pay.getPayItems().get(gapIndex - 1);
        }

        double monthPrice = item.getMonthPrice() + (gapRate * lastPrice);
        item.setMonthPrice(monthPrice);
        item.setOwnPrice(item.getMonthPrice() - item.getRenPrice());
        item.setLastPrice(lastPrice - item.getOwnPrice());

        for (int i = 0; i < gapIndex; i++) {
            pay.getPayItems().get(i).lock();
        }

        pay.recalculateForChangeRate(gapIndex);
        this.yearRate = changeRate;
    }

    public double calculateGapRate(int gapIndex, double changeRate,
        Date changeDate) {
        Date gapEndDate = this.getGapDate(gapIndex);

        int gapDays = this.getGapDays(changeDate, gapEndDate);
        double gapRate = ((changeRate - yearRate) / Pay.PERCENT_NUM / Pay.DAY_IN_YEAR * gapDays);

        return gapRate;
    }

    public int getGapDays(Date beginDate, Date endDate) {
        return (int) ((endDate.getTime() - beginDate.getTime()) / Pay.MS_IN_DAY);
    }

    public int getGapIndex(Date changeDate) {
        Calendar changeCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        changeCalendar.setTime(changeDate);
        startCalendar.setTime(startDate);

        int months = ((changeCalendar.get(Calendar.YEAR)
            - startCalendar.get(Calendar.YEAR)) * Pay.MONTH_IN_YEAR)
            + (changeCalendar.get(Calendar.MONTH)
            - startCalendar.get(Calendar.MONTH));
        int gap = ((months * unit) / Pay.MONTH_IN_YEAR) + 1;
        int mod = (months * unit) % Pay.MONTH_IN_YEAR;

        if ((mod == 0)
                && (changeCalendar.get(Calendar.DATE) > startCalendar.get(
                    Calendar.DATE))) {
            gap++;
        }

        return gap;
    }

    public Date getGapDate(int gapIndex) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        if (type == Pay.TYPE_BEGIN_ACCRUAL) {
            startCalendar.add(Calendar.MONTH,
                ((gapIndex - 1) * Pay.MONTH_IN_YEAR) / unit);
        } else {
            startCalendar.add(Calendar.MONTH,
                (gapIndex * Pay.MONTH_IN_YEAR) / unit);
        }

        return startCalendar.getTime();
    }
}
