package com.brick.util;
/***
 * resource from net 
 * @auther webster
 */
import java.util.HashMap;

/*
 * 1. 金额为整数时，只表示整数部分，后面加“整” 2. 连续的“0”，只写一个“零” 3. 整数后尾数0省略，如100表示成“壹佰元整” 4. 四舍五入到分 5. 最大范围到千亿(12位)
 */
public class CurrencyConverter {
	static HashMap<Integer, String> hm = new HashMap<Integer, String>();
	public static String[] chineseDigits = new String[] { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
	//Modify by Michael 此方法在转成大写是有bug，如果是1060000，不能转成零陆万
//	public static String toUpper(String num) {
//		// 小数如果没有，自动补个0
//		// System.out.println("sa1:" + snum.indexOf("\\."));
//		if (num.indexOf("\\.") == -1) {
//			num = num + ".00";
//		}
//
//		hm.put(0, "零");
//		hm.put(1, "壹");
//		hm.put(2, "贰");
//		hm.put(3, "叁");
//		hm.put(4, "肆");
//		hm.put(5, "伍");
//		hm.put(6, "陆");
//		hm.put(7, "柒");
//		hm.put(8, "捌");
//		hm.put(9, "玖");
//		hm.put(10, "拾");
//		hm.put(100, "佰");
//		hm.put(1000, "仟");
//		hm.put(10000, "万");
//		String snum = num;
//		String intpart = null;
//		String decpart = null;
//		String dec0 = null;
//		String dec1 = null;
//		String hasdec = null;
//
//		String[] sa = new String[2];
//		sa = snum.split("\\.");
//
//		intpart = sa[0];
//		decpart = sa[1];
//		String[] sint = intpart.split(""); // 整数部分
//		
//		switch (sint.length) {
//		case 2:
//			snum = hm.get(Integer.parseInt(sint[1]));
//			break;
//		case 3:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10) + hm.get(Integer.parseInt(sint[2]));
//			break;
//		case 4:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[3]));
//			break;
//		case 5:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[3])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[4]));
//			break;
//		case 6:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10000) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(1000) + hm.get(Integer.parseInt(sint[3])) + hm.get(100)
//					+ hm.get(Integer.parseInt(sint[4])) + hm.get(10) + hm.get(Integer.parseInt(sint[5]));
//			break;
//		case 7:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(10000) + hm.get(Integer.parseInt(sint[3])) + hm.get(1000)
//					+ hm.get(Integer.parseInt(sint[4])) + hm.get(100) + hm.get(Integer.parseInt(sint[5]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[6]));
//			break;
//		case 8:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[3])) + hm.get(10000)
//					+ hm.get(Integer.parseInt(sint[4])) + hm.get(1000) + hm.get(Integer.parseInt(sint[5]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[6])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[7]));
//			break;
//		case 9:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[3])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[4])) + hm.get(10000) + hm.get(Integer.parseInt(sint[5]))
//					+ hm.get(1000) + hm.get(Integer.parseInt(sint[6])) + hm.get(100)
//					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10) + hm.get(Integer.parseInt(sint[8]));
//			break;
//		case 10:
//			snum = hm.get(Integer.parseInt(sint[1])) + "亿" + hm.get(Integer.parseInt(sint[2])) + hm.get(1000)
//					+ hm.get(Integer.parseInt(sint[3])) + hm.get(100) + hm.get(Integer.parseInt(sint[4]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[5])) + hm.get(10000)
//					+ hm.get(Integer.parseInt(sint[6])) + hm.get(1000) + hm.get(Integer.parseInt(sint[7]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[8])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[9]));
//			break;
//		case 11:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(10) + hm.get(Integer.parseInt(sint[2])) + "亿"
//					+ hm.get(Integer.parseInt(sint[3])) + hm.get(1000) + hm.get(Integer.parseInt(sint[4]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[5])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[6])) + hm.get(10000) + hm.get(Integer.parseInt(sint[7]))
//					+ hm.get(1000) + hm.get(Integer.parseInt(sint[8])) + hm.get(100)
//					+ hm.get(Integer.parseInt(sint[9])) + hm.get(10) + hm.get(Integer.parseInt(sint[10]));
//			break;
//		case 12:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(100) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[3])) + "亿"
//					+ hm.get(Integer.parseInt(sint[4])) + hm.get(1000) + hm.get(Integer.parseInt(sint[5]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[6])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10000) + hm.get(Integer.parseInt(sint[8]))
//					+ hm.get(1000) + hm.get(Integer.parseInt(sint[9])) + hm.get(100)
//					+ hm.get(Integer.parseInt(sint[10])) + hm.get(10) + hm.get(Integer.parseInt(sint[11]));
//			break;
//		case 13:
//			snum = hm.get(Integer.parseInt(sint[1])) + hm.get(1000) + hm.get(Integer.parseInt(sint[2]))
//					+ hm.get(100) + hm.get(Integer.parseInt(sint[3])) + hm.get(10)
//					+ hm.get(Integer.parseInt(sint[4])) + "亿" + hm.get(Integer.parseInt(sint[5]))
//					+ hm.get(1000) + hm.get(Integer.parseInt(sint[6])) + hm.get(100)
//					+ hm.get(Integer.parseInt(sint[7])) + hm.get(10) + hm.get(Integer.parseInt(sint[8]))
//					+ hm.get(10000) + hm.get(Integer.parseInt(sint[9])) + hm.get(1000)
//					+ hm.get(Integer.parseInt(sint[10])) + hm.get(100) + hm.get(Integer.parseInt(sint[11]))
//					+ hm.get(10) + hm.get(Integer.parseInt(sint[12]));
//			break;
//		}
//
//		snum += "元";
//
//		snum = snum.replaceAll("零仟", "");
//		snum = snum.replaceAll("零佰", "零");
//		snum = snum.replaceAll("零拾", "");
//		snum = snum.replaceAll("零零亿", "亿");
//		snum = snum.replaceAll("零亿", "亿");
//		snum = snum.replaceAll("零零万", "万");
//		snum = snum.replaceAll("零万", "万");
//		snum = snum.replaceAll("亿万", "亿");
//		snum = snum.replaceAll("零零元", "元");
//		snum = snum.replaceAll("零元", "元");
//		if (snum.startsWith("元"))
//			snum = "零" + snum;
//		
//		String[] sdec = decpart.split(""); // 小数部分
//
//		if(sdec.length>=3){
//			if (sdec[1].equals("0") && sdec[2].equals("0")) {
//				hasdec = "整";
//				snum += hasdec;
//			} else {
//				if (sdec[1].equals("0"))
//					dec0 = "零";
//				else
//					dec0 = hm.get(Integer.parseInt(sdec[1])) + "角";
//
//				if (sdec[2].equals("0"))
//					dec1 = "";
//				else
//					dec1 = hm.get(Integer.parseInt(sdec[2])) + "分";
//
//				snum += dec0 + dec1;
//			}			
//		}
//
//
//		return snum;
//
//	}
//	
    /**
     * Add by Michael 2012 09-04 
     * 把金额转换为汉字表示的数量，小数点后四舍五入保留两位
     * @param amount
     * @return
     */
	public static String toUpper(String num) {
		Double amount=Double.parseDouble(num);
        if(amount > 99999999999999.99 || amount < -99999999999999.99)
            throw new IllegalArgumentException("参数值超出允许范围 (-99999999999999.99 ～ 99999999999999.99)！");

        boolean negative = false;
        if(amount < 0) {
            negative = true;
            amount = amount * (-1);
        }

        long temp = Math.round(amount * 100);
        int numFen = (int)(temp % 10); // 分
        temp = temp / 10;
        int numJiao = (int)(temp % 10); //角
        temp = temp / 10;
        //temp 目前是金额的整数部分

        int[] parts = new int[20]; // 其中的元素是把原来金额整数部分分割为值在 0~9999 之间的数的各个部分
        int numParts = 0; // 记录把原来金额整数部分分割为了几个部分（每部分都在 0~9999 之间）
        for(int i=0; ; i++) {
            if(temp ==0)
                break;
            int part = (int)(temp % 10000);
            parts[i] = part;
            numParts ++;
            temp = temp / 10000;
        }

        boolean beforeWanIsZero = true; // 标志“万”下面一级是不是 0

        String chineseStr = "";
        for(int i=0; i<numParts; i++) {

            String partChinese = partTranslate(parts[i]);
            if(i % 2 == 0) {
                if("".equals(partChinese))
                    beforeWanIsZero = true;
                else
                    beforeWanIsZero = false;
            }

            if(i != 0) {
                if(i % 2 == 0)
                    chineseStr = "亿" + chineseStr;
                else {
                    if("".equals(partChinese) && !beforeWanIsZero)   // 如果“万”对应的 part 为 0，而“万”下面一级不为 0，则不加“万”，而加“零”
                        chineseStr = "零" + chineseStr;
                    else {
                        if(parts[i-1] < 1000 && parts[i-1] > 0) // 如果"万"的部分不为 0, 而"万"前面的部分小于 1000 大于 0， 则万后面应该跟“零”
                            chineseStr = "零" + chineseStr;
                        chineseStr = "万" + chineseStr;
                    }
                }
            }
            chineseStr = partChinese + chineseStr;
        }

        if("".equals(chineseStr))  // 整数部分为 0, 则表达为"零元"
            chineseStr = chineseDigits[0];
        else if(negative) // 整数部分不为 0, 并且原金额为负数
            chineseStr = "负" + chineseStr;

        chineseStr = chineseStr + "元";

        if(numFen == 0 && numJiao == 0) {
            chineseStr = chineseStr + "整";
        }
        else if(numFen == 0) { // 0 分，角数不为 0
            chineseStr = chineseStr + chineseDigits[numJiao] + "角";
        }
        else { // “分”数不为 0
            if(numJiao == 0)
                chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
            else
                chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
        }

        return chineseStr;
	}
	
	  /**
     * 把一个 0~9999 之间的整数转换为汉字的字符串，如果是 0 则返回 ""
     * @param amountPart
     * @return
     */
    private static String partTranslate(int amountPart) {

        if(amountPart < 0 || amountPart > 10000) {
            throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
        }


        String[] units = new String[] {"", "拾", "佰", "仟"};

        int temp = amountPart;

        String amountStr = new Integer(amountPart).toString();
        int amountStrLength = amountStr.length();
        boolean lastIsZero = true; //在从低位往高位循环时，记录上一位数字是不是 0
        String chineseStr = "";

        for(int i=0; i<amountStrLength; i++) {
            if(temp == 0)  // 高位已无数据
                break;
            int digit = temp % 10;
            if(digit == 0) { // 取到的数字为 0
                if(!lastIsZero)  //前一个数字不是 0，则在当前汉字串前加“零”字;
                    chineseStr = "零" + chineseStr;
                lastIsZero = true;
            }
            else { // 取到的数字不是 0
                chineseStr = chineseDigits[digit] + units[i] + chineseStr;
                lastIsZero = false;
            }
            temp = temp / 10;
        }
        return chineseStr;
    }
    
}
