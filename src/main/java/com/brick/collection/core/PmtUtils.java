package com.brick.collection.core;

import java.util.List;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class PmtUtils {
    protected PmtUtils() {
    }

    public static double getPmt(double price, double rate, int renNum,
        int renType) {
        if (renType == Pay.TYPE_BEGIN_ACCRUAL) {
            return (price * rate * Math.pow((1 + rate), renNum)) / (Math
            .pow((1 + rate), renNum) - 1) / (1 + rate);
        } else {
            return (price * rate * Math.pow((1 + rate), renNum)) / (Math
            .pow((1 + rate), renNum) - 1);
        }
    }

    public static double getLockedPmt(double price, double rate,
        int renNum, int renType, List<PayItem> lockedPayItems) {
        double molecular;

        if (renType == Pay.TYPE_BEGIN_ACCRUAL) {
            molecular = (price * rate) / (1 + rate);
        } else {
            molecular = price * rate;
        }

        double denominator = 1 - (1 / Math.pow(1 + rate, renNum));

        for (PayItem payItem : lockedPayItems) {
            double monthPrice = payItem.getMonthPrice();
            int index = payItem.getIndex();

            molecular -= ((monthPrice * rate) / Math.pow(1 + rate, index));
            denominator -= (rate / Math.pow(1 + rate, index));
        }

        return molecular / denominator;
    }

    public static double getPmtForChangeRate(double price, double rate,
        int renNum, List<PayItem> lockedPayItems, int gapIndex) {
        double molecular = price * rate;
        double denominator = 1 - (1 / Math.pow(1 + rate, renNum));

        for (PayItem payItem : lockedPayItems) {
            double monthPrice = payItem.getMonthPrice();
            int index = payItem.getIndex() - gapIndex;

            molecular -= ((monthPrice * rate) / Math.pow(1 + rate, index));
            denominator -= (rate / Math.pow(1 + rate, index));
        }

        return molecular / denominator;
    }

    public static double getPmtForChangeNum(double price, double rate,
        int renNum, int renType, List<PayItem> lockedPayItems,
        int passedIndex) {
        double molecular;

        if ((passedIndex == 0) && (renType == Pay.TYPE_BEGIN_ACCRUAL)) {
            molecular = (price * rate) / (1 + rate);
        } else {
            molecular = price * rate;
        }

        double denominator = 1 - (1 / Math.pow(1 + rate, renNum));

        for (PayItem payItem : lockedPayItems) {
            double monthPrice = payItem.getMonthPrice();
            int index = payItem.getIndex() - passedIndex;

            molecular -= ((monthPrice * rate) / Math.pow(1 + rate, index));
            denominator -= (rate / Math.pow(1 + rate, index));
        }

        return molecular / denominator;
    }
}
