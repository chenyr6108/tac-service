package com.brick.collection.core;

/**
 * @author HF
 * 
 * 20112:27:24 PM
 */
public class PVUtils {

	/**
	 * @param rate 利率
	 * @param n		期次
	 * @param money	收入-成本
	 * @return
	 */
	public static double pv2(double rate, int n, double money) {
		double sum = 0;
		sum = money / ((double) Math.pow(1 + rate/12, n));
		return sum;
	}

	public static void main(String[] args) {
		 System.out.println(PVUtils.pv2(0.0606, 3, 3605.6 - 1545.02));
	}
}
