package com.brick.collection.core;

/**
 * @author wujw
 * @date Jun 7, 2010
 * @version
 */
public class DefaultPayableStrategy implements PayableStrategy {
	public static final int BALANCE_TYPE_BASE = 0;
	public static final int BALANCE_TYPE_OWN = 1;

	protected DefaultPayableStrategy() {
	}

	public static Pay createPayable() {
		return new Pay();
	}

	public static void calculate(Pay pay, int balanceType) {
		pay.calculate();

		if (balanceType == BALANCE_TYPE_BASE) {
			new BalanceBaseStrategy().process(pay);
		} else if (balanceType == BALANCE_TYPE_OWN) {
			new BalanceOwnStrategy().process(pay);
		}

		pay.createStatistic();
	}

	public static void recalculate(Pay pay, int balanceType) {
		pay.recalculate();

		if (balanceType == BALANCE_TYPE_BASE) {
			new BalanceBaseStrategy().process(pay);
		} else if (balanceType == BALANCE_TYPE_OWN) {
			new BalanceOwnStrategy().process(pay);
		}

		pay.createStatistic();
	}

	public static void changeNum(Pay pay, int balanceType, int passedIndex,
			int num) {
		pay.changeNum(passedIndex, num);

		if (balanceType == BALANCE_TYPE_BASE) {
			new BalanceBaseStrategy().process(pay);
		} else if (balanceType == BALANCE_TYPE_OWN) {
			new BalanceOwnStrategy().process(pay);
		}

		pay.createStatistic();
	}

	public static void recalculateForChangeRate(Pay pay, int balanceType,
			int passedIndex) {
		pay.recalculateForChangeRate(passedIndex);

		if (balanceType == BALANCE_TYPE_BASE) {
			new BalanceBaseStrategy().process(pay);
		} else if (balanceType == BALANCE_TYPE_OWN) {
			new BalanceOwnStrategy().process(pay);
		}

		pay.createStatistic();
	}
}
