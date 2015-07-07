package com.brick.collection.core;
/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class BalanceOwnStrategy extends BalanceBaseStrategy {
    public void process(Pay pay) {
        for (PayItem payItem : pay.getPayItems()) {
            double ownPrice = Math.floor(payItem.getOwnPrice());
            double renPrice = (payItem.getRenPrice()
                + payItem.getOwnPrice()) - ownPrice;
            payItem.setOwnPrice(ownPrice);
            payItem.setRenPrice(renPrice);
        }

        super.process(pay);
    }
}
