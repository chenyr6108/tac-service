package com.brick.util.poi;
/**
 * @author ShenQi
 * @version Created：2012-03-29
 * function: POI 样式
 */
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;

public class ExcelStyleLoader {
    public static final int DEFAULT = 0;
    public static final int BORDERED = 1;
    public static final int BORDERED_RIGHT = 13;
    public static final int MAIN_HEADING = 2;
    public static final int SUB_HEADING = 3;
    public static final int CASE_HEADING = 4;
    public static final int CONTACT_HEADING = 5;
    public static final int BORDERED_BOLD = 6;
    public static final int BORDERED_BOLD_CENTER = 7;
    public static final int BORDERED_BOLD_RIGHT = 8;
    public static final int DEFAULT_CENTER = 9;
    public static final int BORDERED_CENTER = 10;
    public static final int BORDERED_BOLD_TITLE = 11;
    public static final int DEFAULT_BOLD = 12;

    public static final short DATA_FORMAT_MONEY = (short) 4; //set dataformat to "#,##0.00"
    public static final int BORDERED_RIGHT_2DIG = 14;
    public static final int BORDERED_LEFT_2DIG = 20;
    public static final int BORDERED_BOLD_RIGHT_2DIG = 15;
    public static final int BORDERED_RED = 16;
    public static final int BORDERED_RIGHT_5DIG = 17;
    public static final short DATA_FORMAT_MONEY4 = (short) 6; //set dataformat to "#,##0.0000"
    public static final int BORDERED_RED_RIGHT_2DIG = 18;
    public static final int BORDERED_RED_RIGHT_5DIG = 19;
    
    public static final int BORDERED_BOLD_RED_RIGHT_MONEY = 21;//bold,red(negative),right align,set dataformat to "#,##0.00"
    public static final int BORDERED_RED_RIGHT_MONEY = 22;//red(negative),right align,set dataformat to "#,##0.00"
    public static final int BORDERED_BOLD_RIGHT_MONEY = 23;//bold,black(positive),right algin,set dataformat to "#,##0.00"
    public static final int BORDERED_RIGHT_MONEY = 24;//black(positive),right algin,set dataformat to "#,##0.00"
    

    public static HSSFCellStyle load(HSSFCellStyle tgt, HSSFFont aFont, int style) {
        HSSFCellStyle myStyle = tgt;
        HSSFFont myFont = aFont;
        switch (style) {
            case DEFAULT: {
                myFont.setFontName(HSSFFont.FONT_ARIAL);
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("TEXT"));
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED_RIGHT: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                break;
            }
            case MAIN_HEADING: {
                myStyle.setFont(myFont);
                myFont.setFontHeightInPoints((short) 18);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myFont.setUnderline(HSSFFont.U_SINGLE);
                break;
            }
            case SUB_HEADING: {
                myStyle.setFont(myFont);
                myFont.setFontHeightInPoints((short) 14);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                break;
            }
            case CASE_HEADING: {
                myStyle.setFont(myFont);
                myFont.setFontHeightInPoints((short) 12);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myFont.setColor(HSSFColor.WHITE.index);
                myStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                myStyle.setFillForegroundColor(HSSFColor.BLUE.index);
                break;
            }
            case CONTACT_HEADING: {
                myStyle.setFont(myFont);
                myFont.setFontHeightInPoints((short) 12);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myFont.setColor(HSSFColor.WHITE.index);
                myStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                myStyle.setFillForegroundColor(HSSFColor.GREEN.index);
                break;
            }
            case BORDERED_BOLD: {
                myStyle = ExcelStyleLoader.load(tgt, aFont, BORDERED);
                myStyle.setFont(myFont);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                break;
            }
            case BORDERED_LEFT_2DIG: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(DATA_FORMAT_MONEY);
                break;
            }
            case BORDERED_RIGHT_2DIG: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(DATA_FORMAT_MONEY);
                break;
            }
            case DEFAULT_BOLD: {
            	myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("TEXT"));
                myStyle.setFont(myFont);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                break;
            }
            case BORDERED_BOLD_TITLE: {
                myStyle = ExcelStyleLoader.load(tgt, aFont, BORDERED);
                myStyle.setFont(myFont);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                myStyle.setWrapText(true);
                myStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                myStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                break;
            }
            //NO BORDER, LEFT ALIGN, USED IN REPORT HEAD
            case BORDERED_BOLD_CENTER: {
                myStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                myStyle.setFont(myFont);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                break;
            }
            case BORDERED_BOLD_RIGHT: {
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED_BOLD_RIGHT_2DIG: {
                myStyle = ExcelStyleLoader.load(tgt, aFont, BORDERED_BOLD);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setDataFormat(DATA_FORMAT_MONEY);
                myStyle.setFont(myFont);
                break;
            }
            case DEFAULT_CENTER: {
                myStyle = ExcelStyleLoader.load(tgt, aFont, DEFAULT);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED_CENTER: {
                myStyle = ExcelStyleLoader.load(tgt, aFont, BORDERED);
                myStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED_RED: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myFont.setColor(HSSFColor.RED.index);
                myStyle.setFont(myFont);
                break;
            }
            case BORDERED_RIGHT_5DIG: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("##0.00000"));
                break;
            }
            case BORDERED_RED_RIGHT_2DIG: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myFont.setColor(HSSFColor.RED.index);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(DATA_FORMAT_MONEY);
                break;
            }
            case BORDERED_RED_RIGHT_5DIG: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myFont.setColor(HSSFColor.RED.index);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("##0.00000"));
                break;
            }
            case BORDERED_BOLD_RED_RIGHT_MONEY: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myFont.setColor(HSSFColor.RED.index);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                break;
            }
            case BORDERED_RED_RIGHT_MONEY: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myFont.setColor(HSSFColor.RED.index);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                break;
            }
            case BORDERED_BOLD_RIGHT_MONEY: {
                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                break;
            }
            case BORDERED_RIGHT_MONEY: {
//                myStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//                myStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
//                myStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
//                myStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                myStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
                myStyle.setFont(myFont);
                myStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
                break;
            }
        }
        return myStyle;
    }
}
