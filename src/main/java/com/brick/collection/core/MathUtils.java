package com.brick.collection.core;

import java.math.BigDecimal;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class MathUtils {
    protected MathUtils() {
    }

    public static double round(double value, int scale) {
        BigDecimal bigDecimal = new BigDecimal(value);

        return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP)
                         .doubleValue();
    }

    public static double round(double value) {
        return round(value, 2);
    }
}
