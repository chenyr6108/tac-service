package com.brick.util.poi;

import java.io.*;
import java.util.Hashtable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelFileWriter {
    private FileOutputStream targetExcelFile;
    private HSSFWorkbook templateExcelFile;
    private HSSFCellStyle cellStyle;

    private Hashtable styleCache = new Hashtable();
    private Hashtable fontCache = new Hashtable();

    /**
     * Constructor without input stream. so save() is not avaliable
     * <p/>
     * GetWorkbook after before pass it out
     */
    public ExcelFileWriter() {
        templateExcelFile = new HSSFWorkbook();
        cellStyle = templateExcelFile.createCellStyle();
    }

    /**
     * Constructor.
     *
     * @param fullPathFileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ExcelFileWriter(String fullPathFileName) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook();
        targetExcelFile = new FileOutputStream(fullPathFileName);
        cellStyle = templateExcelFile.createCellStyle();
    }

    /**
     * @param templateFile , full name of Template file
     * @param targetFile   , full name target Excel File
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ExcelFileWriter(String templateFile, String targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(new FileInputStream(templateFile));
        targetExcelFile = new FileOutputStream(targetFile);
        cellStyle = templateExcelFile.createCellStyle();
    }

    public ExcelFileWriter(String templateFile, File targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(new FileInputStream(templateFile));
        targetExcelFile = new FileOutputStream(targetFile);
        cellStyle = templateExcelFile.createCellStyle();
    }

    public ExcelFileWriter(String templateFile, FileOutputStream targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(new FileInputStream(templateFile));
        targetExcelFile = targetFile;
        cellStyle = templateExcelFile.createCellStyle();
    }

    // added by sim kay meng 18  Nov 2003
    public ExcelFileWriter(InputStream templateStream, String targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(templateStream);
        targetExcelFile = new FileOutputStream(targetFile);
        cellStyle = templateExcelFile.createCellStyle();
    }

    public ExcelFileWriter(InputStream templateStream, File targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(templateStream);
        targetExcelFile = new FileOutputStream(targetFile);
        cellStyle = templateExcelFile.createCellStyle();
    }

    public ExcelFileWriter(InputStream templateStream, FileOutputStream targetFile) throws FileNotFoundException, IOException {
        templateExcelFile = new HSSFWorkbook(templateStream);
        targetExcelFile = targetFile;
        cellStyle = templateExcelFile.createCellStyle();
    }


    public HSSFWorkbook getWorkbook() {
        return templateExcelFile;
    }

    public void setIntValue(int sheetNo, int rowNo, int cellNo, int cellValue, HSSFCellStyle cellStyle) {
        setIntValue(sheetNo, rowNo, (short) cellNo, cellValue, cellStyle);
    }

    public void setIntValue(int sheetNo, int rowNo, short cellNo, int cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = getCell(sheetNo, rowNo, cellNo);

        if (cellStyle == null)
            cell.setCellStyle(this.cellStyle);
        else
            cell.setCellStyle(cellStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(cellValue);

    }

    public void setDoubleValue(int sheetNo, int rowNo, int cellNo, double cellValue, HSSFCellStyle cellStyle) {
        setDoubleValue(sheetNo, rowNo, (short) cellNo, cellValue, cellStyle);
    }

    public void setDoubleValue(int sheetNo, int rowNo, short cellNo, double cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = getCell(sheetNo, rowNo, cellNo);

        if (cellStyle == null)
            cell.setCellStyle(this.cellStyle);
        else
            cell.setCellStyle(cellStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(cellValue);

    }

    public void setBooleanValue(int sheetNo, int rowNo, int cellNo, boolean cellValue, HSSFCellStyle cellStyle) {
        setBooleanValue(sheetNo, rowNo, (short) cellNo, cellValue, cellStyle);
    }

    public void setBooleanValue(int sheetNo, int rowNo, short cellNo, boolean cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = getCell(sheetNo, rowNo, cellNo);

        if (cellStyle == null)
            cell.setCellStyle(this.cellStyle);
        else
            cell.setCellStyle(cellStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
        cell.setCellValue(cellValue);

    }

    public void setStringValue(int sheetNo, int rowNo, int cellNo, String cellValue, HSSFCellStyle cellStyle) {
        setStringValue(sheetNo, rowNo, (short) cellNo, cellValue, cellStyle);
    }

    public void setStringValue(int sheetNo, int rowNo, short cellNo, String cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = getCell(sheetNo, rowNo, cellNo);

        if (cellStyle == null)
            cell.setCellStyle(this.cellStyle);
        else
            cell.setCellStyle(cellStyle);
        if (cellValue != null && cellValue.length() != 0) {
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(cellValue);
        }
    }

    public void setStringValue(int sheetNo, int rowNo, short cellNo, String cellValue) {
        setStringValue(sheetNo, rowNo, cellNo, cellValue, null);
    }

    public void setFormula(int sheetNo, int rowNo, int cellNo, String formula) {
        setFormula(sheetNo, rowNo, (short) cellNo, formula, null);
    }

    public void setFormula(int sheetNo, int rowNo, int cellNo, String formula, HSSFCellStyle cellStyle) {
        setFormula(sheetNo, rowNo, (short) cellNo, formula, cellStyle);
    }

    public void setFormula(int sheetNo, int rowNo, short cellNo, String cellValue) {
        setFormula(sheetNo, rowNo, cellNo, cellValue, null);
    }

    public void setFormula(int sheetNo, int rowNo, short cellNo, String formula, HSSFCellStyle cellStyle) {
        HSSFCell cell = getCell(sheetNo, rowNo, cellNo);

        if (cellStyle == null)
            cell.setCellStyle(this.cellStyle);
        else
            cell.setCellStyle(cellStyle);
        cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
        cell.setCellFormula(formula);
    }

    public void setStringValue(int sheetNo, int rowNo, int cellNo, String cellValue) {
        setStringValue(sheetNo, rowNo, (short) cellNo, cellValue, null);
    }

    public HSSFCell getCell(int sheetNo, int rowNo, int cellNo) {
        return getCell(sheetNo, rowNo, (short) cellNo);
    }

    public HSSFCell getCell(int sheetNo, int rowNo, short cellNo) {
        HSSFSheet sheet = getSheet(sheetNo);
        HSSFRow row = sheet.getRow(rowNo);
        if (row == null) {
            sheet.createRow(rowNo);
            row = sheet.getRow(rowNo);
        }

        return row.getCell(cellNo) == null ? row.createCell(cellNo) : row.getCell(cellNo);
    }

    /**
     * To obtain a worksheet if the specified index number exist.
     * A new worksheet is created if it does not exist.
     *
     * @param strSheetName as an index for worksheet of workbook
     * @return existing or new worksheet
     */
    public HSSFSheet getSheet(String strSheetName) {
        return templateExcelFile.getSheet(strSheetName);
    }

    /**
     * To obtain a worksheet if the specified index number exist.
     * A new worksheet is created if it does not exist.
     *
     * @param intSheet as an index for worksheet of workbook
     * @return existing or new worksheet
     */
    public HSSFSheet getSheet(int intSheet) {
        try {
            return templateExcelFile.getSheetAt(intSheet);
        } catch (IndexOutOfBoundsException e) {
            templateExcelFile.createSheet();
            return getSheet(intSheet);
        }
    }

    /**
     * To obtain a worksheet if the specified index number exist.
     * A new worksheet is created if it does not exist.
     *
     * @param intSheet as an index for worksheet of workbook
     * @return existing or new worksheet
     */
    public String getSheetName(int intSheet) {
        try {
            return templateExcelFile.getSheetName(intSheet);
        } catch (IndexOutOfBoundsException e) {
            templateExcelFile.createSheet();
            return getSheetName(intSheet);
        }
    }

    public void setSheetName(int sheetNo, String sheetName) {
        templateExcelFile.setSheetName(sheetNo, sheetName);
    }

    public HSSFSheet createSheet() {
        return templateExcelFile.createSheet();
    }

    public HSSFSheet createSheet(String sheetName) {
        return templateExcelFile.createSheet(sheetName);
    }

    /**
     * To save and close the excel file.
     */
    public void save() throws IOException {
        templateExcelFile.write(targetExcelFile);
        targetExcelFile.close();
    }

    public HSSFCellStyle getStyle(int iKey) {
        Integer key = new Integer(iKey);
        HSSFCellStyle style = (HSSFCellStyle) styleCache.get(key);
        if (style == null) {
            styleCache.put(key, templateExcelFile.createCellStyle());
        }
        return (HSSFCellStyle) styleCache.get(key);
    }

    public HSSFFont getFont(int iKey) {
        Integer key = new Integer(iKey);
        HSSFFont font = (HSSFFont) fontCache.get(key);
        if (font == null) {
            fontCache.put(key, templateExcelFile.createFont());
        }
        return (HSSFFont) fontCache.get(key);
    }

    public HSSFCellStyle getStyle() {
        return templateExcelFile.createCellStyle();
    }

    public HSSFFont getFont() {
        return templateExcelFile.createFont();
    }

    public void setFormulaCellSum(HSSFCell c, int row, int fromLine, int toLine) {
        StringBuffer formula = new StringBuffer("");
        formula.append("SUM(");
        formula.append(convCellNo(fromLine));
        formula.append(String.valueOf(row + 1));
        formula.append(":");
        formula.append(convCellNo(toLine));
        formula.append(String.valueOf(row + 1));
        formula.append(")");
        c.setCellFormula(formula.toString());
    }

    public String convCellNo(int cellNo) {
        int lineSize = cellNo / 26;
        StringBuffer strLineInd = new StringBuffer("");
        char a = (char) ((lineSize % 26 - 1) + 65);
        if (lineSize > 0) {
            strLineInd.append(String.valueOf(a));
        }
        char b = (char) ((cellNo % 26) + 65);
        strLineInd.append(String.valueOf(b));

        return strLineInd.toString();
    }

    public String getCellName(int row, int col) {
        return convCellNo(col) + (row + 1);
    }

    public HSSFCellStyle getStyle(int sheetNo, int rowNo, int cellNo) {
        return getCell(sheetNo, rowNo, cellNo).getCellStyle();
    }
}
