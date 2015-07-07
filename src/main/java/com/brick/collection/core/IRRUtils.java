package com.brick.collection.core;

/**
 * @author wujw
 * @date Aug 30, 2010
 * @version
 */
public class IRRUtils {

	public static double getIRR(final double[] cashFlows,
			final double estimatedResult) {

		double result = Double.NaN;

		if (cashFlows != null && cashFlows.length > 0) {

			if (cashFlows[0] != 0.0) {

				final double noOfCashFlows = cashFlows.length;

				double sumCashFlows = 0.0;

				int noOfNegativeCashFlows = 0;

				int noOfPositiveCashFlows = 0;

				for (int i = 0; i < noOfCashFlows; i++) {

					sumCashFlows += cashFlows[i];

					if (cashFlows[i] > 0) {

						noOfPositiveCashFlows++;

					} else if (cashFlows[i] < 0) {

						noOfNegativeCashFlows++;

					}

				}

				if (noOfNegativeCashFlows > 0 && noOfPositiveCashFlows > 0) {

					double irrGuess = 0.1; // default: 10%

					if (!Double.isNaN(estimatedResult)) {

						irrGuess = estimatedResult;

						if (irrGuess <= 0.0) {
							
							irrGuess = 0.5;

						}

					}

					double irr = 0.0;

					if (sumCashFlows < 0) {

						irr = -irrGuess;

					} else {

						irr = irrGuess;

					}

					final double minDistance = 1E-15;

					final double cashFlowStart = cashFlows[0];

					final int maxIteration = 1000;

					boolean wasHi = false;

					double cashValue = 0.0;

					for (int i = 0; i <= maxIteration; i++) {

						cashValue = cashFlowStart;

						for (int j = 1; j < noOfCashFlows; j++) {

							cashValue += cashFlows[j] / Math.pow(1.0 + irr, j);

						}

						if (Math.abs(cashValue) < 0.001) {

							result = irr;

							break;

						}

						if (cashValue > 0.0) {

							if (wasHi) {

								irrGuess /= 2;

							}

							irr += irrGuess;

							if (wasHi) {

								irrGuess -= minDistance;

								wasHi = false;

							}

						} else {

							irrGuess /= 2;

							irr -= irrGuess;

							wasHi = true;

						}

						if (irrGuess <= minDistance) {

							result = irr;

							break;

						}

					}

				}

			}

		}

		return result;

	}

	/**
	 * @author michael
	 * @param cashFlows   现金流量
	 * @param estimatedResult  返回结果
	 * @param defer  延迟拨款期数
	 * @return
	 */
	public static double getTrIRR(final double[] cashFlows,
			final double estimatedResult,final int defer,final double leaseTopric) {

		double result = Double.NaN;

		if (cashFlows != null && cashFlows.length > 0) {

			if (cashFlows[0] != 0.0) {

				final double noOfCashFlows = cashFlows.length;

				double sumCashFlows = 0.0;

				int noOfNegativeCashFlows = 0;

				int noOfPositiveCashFlows = 0;
				
				for (int i = 0; i < noOfCashFlows; i++) {
				
					//增加延后拨款期数的判断及增加现金流量金额
					if(defer>0){
						
						if(i==0){
							cashFlows[0] += leaseTopric;
						}
						
						if(defer==i){
							cashFlows[i] += -leaseTopric;
						}
					}

					sumCashFlows += cashFlows[i];

					if (cashFlows[i] > 0) {

						noOfPositiveCashFlows++;

					} else if (cashFlows[i] < 0) {

						noOfNegativeCashFlows++;

					}

				}

				if (noOfNegativeCashFlows > 0 && noOfPositiveCashFlows > 0) {

					double irrGuess = 0.1; // default: 10%

					if (!Double.isNaN(estimatedResult)) {

						irrGuess = estimatedResult;

						if (irrGuess <= 0.0) {
							
							irrGuess = 0.5;

						}

					}

					double irr = 0.0;

					if (sumCashFlows < 0) {

						irr = -irrGuess;

					} else {

						irr = irrGuess;

					}

					final double minDistance = 1E-15;

					final double cashFlowStart = cashFlows[0];

					final int maxIteration = 1000;

					boolean wasHi = false;

					double cashValue = 0.0;

					for (int i = 0; i <= maxIteration; i++) {

						cashValue = cashFlowStart;

						for (int j = 1; j < noOfCashFlows; j++) {

							cashValue += cashFlows[j] / Math.pow(1.0 + irr, j);

						}

						if (Math.abs(cashValue) < 0.001) {

							result = irr;

							break;

						}

						if (cashValue > 0.0) {

							if (wasHi) {

								irrGuess /= 2;

							}

							irr += irrGuess;

							if (wasHi) {

								irrGuess -= minDistance;

								wasHi = false;

							}

						} else {

							irrGuess /= 2;

							irr -= irrGuess;

							wasHi = true;

						}

						if (irrGuess <= minDistance) {

							result = irr;

							break;

						}

					}

				}

			}

		}

		return result;

	}

	
	public static void main(String[] args) {

		 //double[] cashFlows =  {-230000,10768.1,11776.43552,11784.46659,11792.59923,11800.83474,11809.17442,11817.61959,11826.17157,11834.83173,11843.60142,11852.48204,11861.47498,11870.58168,11879.80356,11889.14209,11898.59873,11908.17499,11917.87238,11927.69242,11937.63668,11947.70671,11957.90412,11968.23051,11978.68752,10.72321204};
		 //double[] cashFlows = { -70000, 12000, 15000, 18000, 21000, 26000 };
		double[] cashFlows = new double[86];
		cashFlows[0] = -350000d;
		for (int i=1; i<=24;i++) {
			cashFlows[i] = 17000d;
		}
		double irr = IRRUtils.getIRR(cashFlows, Double.NaN);
		System.out.println(irr * 100 * 12);
	}

}
