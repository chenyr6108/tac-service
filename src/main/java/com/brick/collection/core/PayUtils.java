package com.brick.collection.core;
/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class PayUtils {
    protected PayUtils() {
    }

    public static double calculateRenPrice(int type, int index,
        double lastPrice, double unitRate) {
        boolean isBegin = (type == Pay.TYPE_BEGIN_ACCRUAL)
            || (type == Pay.TYPE_BEGIN_CORPUS);

        if ((index == 0) && isBegin) {
            return 0D;
        } else {
            return lastPrice * unitRate;
        }
    }
}
