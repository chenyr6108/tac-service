package com.brick.collection.core;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brick.log.service.LogPrint;
/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class BalanceBaseStrategy implements BalanceStrategy {
    public void process(Pay pay) {
        List<PayItem> payItems = pay.getPayItems();
        double totalOwnPrice = 0D;
        double lastPrice = pay.getRestPrice();

        for (PayItem payItem : pay.getPayItems()) {
            double monthPrice = MathUtils.round(payItem.getMonthPrice());
            double ownPrice = MathUtils.round(payItem.getOwnPrice());
            double renPrice = MathUtils.round(monthPrice - ownPrice);
            payItem.setMonthPrice(monthPrice);
            payItem.setOwnPrice(ownPrice);
            payItem.setRenPrice(renPrice);
            lastPrice -= ownPrice;
            payItem.setLastPrice(MathUtils.round(lastPrice));

            totalOwnPrice += ownPrice;
        }

        PayItem payItem = payItems.get(payItems.size() - 1);
        payItem.setOwnPrice((payItem.getOwnPrice() + pay.getRestPrice())
            - totalOwnPrice);
        payItem.setMonthPrice(payItem.getOwnPrice()
            + payItem.getRenPrice());

        int len = payItems.size();

        if (len < 2) {
            lastPrice = pay.getRestPrice();
        } else {
            lastPrice = payItems.get(len - 2).getLastPrice();
        }

        payItem.setLastPrice(lastPrice - payItem.getOwnPrice());
        payItem.setMonthPrice(MathUtils.round(payItem.getMonthPrice()));
        payItem.setOwnPrice(MathUtils.round(payItem.getOwnPrice()));
        payItem.setLastPrice(MathUtils.round(payItem.getLastPrice()));
    }
}
