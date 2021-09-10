package com.loctek.file.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/3
 */
public class ExcelUtil {

    /**
     * @param is          excel源文件输入流
     * @param isWithStyle 是否需要表格样式 包含 字体 颜色 边框 对齐方式
     * @return
     */
    public static String excelToHtml(InputStream is, boolean isWithStyle) throws Exception{
        String htmlExcel = null;
        try {
            Workbook wb = WorkbookFactory.create(is);
            //07及10版以后的excel处理方法
            if (wb instanceof SXSSFWorkbook) {
                SXSSFWorkbook hWb = (SXSSFWorkbook) wb;
                htmlExcel = getExcelInfo(hWb, isWithStyle);
                //07及10版以后的excel处理方法
            } else if (wb instanceof HSSFWorkbook) {
                HSSFWorkbook hWb = (HSSFWorkbook) wb;
                htmlExcel = getExcelInfo(hWb, isWithStyle);
                //03版excel处理方法
            } else if (wb instanceof XSSFWorkbook) {
                XSSFWorkbook xWb = (XSSFWorkbook) wb;
                htmlExcel = getExcelInfo(xWb, isWithStyle);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return htmlExcel;
    }


    public static String getExcelInfo(Workbook wb, boolean isWithStyle) {

        StringBuffer sb = new StringBuffer();
        //获取第一个Sheet的内容
        Sheet sheet = wb.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        Map<String, String> map[] = getRowSpanColSpanMap(sheet);
        sb.append("<table border='1' style='border-collapse:collapse;' width='100%'>");
        Row row = null;
        Cell cell = null;

        for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                sb.append("<tr><td ></td></tr>");
                continue;
            }
            sb.append("<tr>");
            int lastColNum = row.getLastCellNum();
            for (int colNum = 0; colNum < lastColNum; colNum++) {
                cell = row.getCell(colNum);
                //特殊情况 空白的单元格会返回null
                if (cell == null) {
                    sb.append("<td> </td>");
                    continue;
                }
                String stringValue = getCellValue(cell);
                if (map[0].containsKey(rowNum + "," + colNum)) {
                    String pointString = map[0].get(rowNum + "," + colNum);
                    map[0].remove(rowNum + "," + colNum);
                    int bottomeRow = Integer.valueOf(pointString.split(",")[0]);
                    int bottomeCol = Integer.valueOf(pointString.split(",")[1]);
                    int rowSpan = bottomeRow - rowNum + 1;
                    int colSpan = bottomeCol - colNum + 1;
                    sb.append("<td rowspan= '" + rowSpan + "' colspan= '" + colSpan + "' ");
                } else if (map[1].containsKey(rowNum + "," + colNum)) {
                    map[1].remove(rowNum + "," + colNum);
                    continue;
                } else {
                    sb.append("<td ");
                }

                //判断是否需要样式
                if (isWithStyle) {
                    //处理单元格样式
                    dealExcelStyle(wb, sheet, cell, sb);
                }
                sb.append(">");
                if (stringValue == null || "".equals(stringValue.trim())) {
                    sb.append("   ");
                } else {
                    // 将ascii码为160的空格转换为html下的空格（ ）
                    sb.append(stringValue.replace(String.valueOf((char) 160), " "));
                }
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private static Map<String, String>[] getRowSpanColSpanMap(Sheet sheet) {

        Map<String, String> map0 = new HashMap<String, String>();
        Map<String, String> map1 = new HashMap<String, String>();
        int mergedNum = sheet.getNumMergedRegions();
        CellRangeAddress range = null;
        for (int i = 0; i < mergedNum; i++) {
            range = sheet.getMergedRegion(i);
            int topRow = range.getFirstRow();
            int topCol = range.getFirstColumn();
            int bottomRow = range.getLastRow();
            int bottomCol = range.getLastColumn();
            map0.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
            int tempRow = topRow;
            while (tempRow <= bottomRow) {
                int tempCol = topCol;
                while (tempCol <= bottomCol) {
                    map1.put(tempRow + "," + tempCol, "");
                    tempCol++;
                }
                tempRow++;
            }
            map1.remove(topRow + "," + topCol);
        }
        Map[] map = {map0, map1};
        return map;
    }


    /**
     * 获取表格单元格Cell内容
     *
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        DecimalFormat df = new DecimalFormat("0");
        DecimalFormat dfFloar = new DecimalFormat("0.00");
        String result = new String();
        switch (cell.getCellType()) {
            case NUMERIC:// 数字类型
                // 处理日期格式、时间格式
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    result = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil
                            .getJavaDate(value);
                    result = sdf.format(date);
                } else {

                    Object val = cell.getNumericCellValue();
                    Boolean isNum = false;//data是否为数值型
//                    Boolean isInteger=false;//data是否为整数
//                    Boolean isPercent=false;//data是否为百分数
                    if (val != null || "".equals(val)) {
                        val = val.toString().trim();
                        //判断data是否为数值型
                        isNum = val.toString().matches("^(-?\\d+)(\\.\\d+)?$");
                        //判断data是否为整数（小数部分是否为0）
//                            isInteger=val.toString().matches("^[-\\+]?[\\d]*$");
                        //判断data是否为百分数（是否包含“%”）
//                            isPercent=val.toString().contains("%");
                    }
                    //如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
                    if (isNum) {
                        String valTe = (String) val;
                        if (StringUtils.hasText(valTe) && (valTe.substring(valTe.length() - 1, valTe.length()).equals("0") || valTe.substring(valTe.length() - 2, valTe.length()).equals("00"))) {
                            result = (valTe.split("\\.")[0]);
                        }
                    } else {
                        String valTe = dfFloar.format(cell.getNumericCellValue());
                        if (StringUtils.hasText(valTe) && (valTe.substring(valTe.length() - 1, valTe.length()).equals("0") || valTe.substring(valTe.length() - 2, valTe.length()).equals("00"))) {
                            result = (valTe.split("\\.")[0]);
                        }
//                        result = dfFloar.format(cell.getNumericCellValue());
                    }
//                     result = cell.getNumericCellValue();
//                    CellStyle style = cell.getCellStyle();
//                    DecimalFormat format = new DecimalFormat();
//                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
//                    if (temp.equals("General")) {
//                        format.applyPattern("#");
//                    }
//                    result = format.format(value);
//                    result = String.valueOf(value);
                }
                break;
            case STRING:// String类型
                result = cell.getRichStringCellValue().toString();
                break;
            case BLANK:
                result = "";
                break;
            case FORMULA:
                try {
                    Object val = cell.getNumericCellValue();
                    Boolean isNum = false;//data是否为数值型
//                    Boolean isInteger=false;//data是否为整数
//                    Boolean isPercent=false;//data是否为百分数
                    if (val != null || "".equals(val)) {
                        val = val.toString().trim();
                        //判断data是否为数值型
                        isNum = val.toString().matches("^(-?\\d+)(\\.\\d+)?$");
                        //判断data是否为整数（小数部分是否为0）
//                            isInteger=val.toString().matches("^[-\\+]?[\\d]*$");
                        //判断data是否为百分数（是否包含“%”）
//                            isPercent=val.toString().contains("%");
                    }
                    //如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
                    if (isNum) {
                        String valTe = (String) val;
                        if (StringUtils.hasText(valTe) && (valTe.substring(valTe.length() - 1, valTe.length()).equals("0") || valTe.substring(valTe.length() - 2, valTe.length()).equals("00"))) {
                            result = (valTe.split("\\.")[0]);
                        }
                    } else {
                        result = dfFloar.format(cell.getNumericCellValue());
                    }
//                    result = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    result = String.valueOf(cell.getRichStringCellValue());
                }
                break;
            default:
                result = "";
                break;
        }
        return result;
    }


    /**
     * 处理表格样式
     *
     * @param wb
     * @param sheet
     * @param sb
     */
    private static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuffer sb) {

        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            cellStyle.setWrapText(true);
            short alignment = cellStyle.getAlignment().getCode();
            //单元格内容的水平对齐方式
            sb.append("align='" + convertAlignToHtml(alignment) + "' ");
            short verticalAlignment = cellStyle.getVerticalAlignment().getCode();
            //单元格中内容的垂直排列方式
            sb.append("valign='" + convertVerticalAlignToHtml(verticalAlignment) + "' ");

            if (wb instanceof XSSFWorkbook || (wb instanceof SXSSFWorkbook)) {

                XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
                short boldWeight = xf.getBold() ? (short) 0 : (short) 1;
                String align = convertAlignToHtml(alignment);
                sb.append("style='");
                // 自动换行
                sb.append("word-wrap:break-word;");
                // 字体加粗
                sb.append("font-weight:" + boldWeight + ";");
                // 字体大小
                sb.append("font-size: " + xf.getFontHeight() / 2 + "%;");
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                sb.append("width:" + columnWidth + "px;");
                //表头排版样式
                sb.append("text-align:" + align + ";");
                XSSFColor xc = xf.getXSSFColor();
                if (xc != null && !"".equals(xc)) {
                    // 字体颜色
                    sb.append("color:#" + xc.getARGBHex().substring(2) + ";");
                }

                XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
                if (bgColor != null && !"".equals(bgColor)) {
                    sb.append("background-color:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
                }
                sb.append(getBorderStyle(0, cellStyle.getBorderTop().getCode(), ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
                sb.append(getBorderStyle(1, cellStyle.getBorderRight().getCode(), ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
                sb.append(getBorderStyle(2, cellStyle.getBorderBottom().getCode(), ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
                sb.append(getBorderStyle(3, cellStyle.getBorderLeft().getCode(), ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

            } else if (wb instanceof HSSFWorkbook) {

                HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
                short boldWeight = hf.getBold() ? (short) 0 : (short) 1;
                short fontColor = hf.getColor();
                sb.append("style='");
                // 类HSSFPalette用于求的颜色的国际标准形式
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                HSSFColor hc = palette.getColor(fontColor);
                // 字体加粗
                sb.append("font-weight:" + boldWeight + ";");
                // 字体大小
                sb.append("font-size: " + hf.getFontHeight() / 2 + "%;");
                String align = convertAlignToHtml(alignment);
                //表头排版样式
                sb.append("text-align:" + align + ";");
                String fontColorStr = convertToStardColor(hc);
                if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
                    // 字体颜色
                    sb.append("color:" + fontColorStr + ";");
                }
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
                sb.append("width:" + columnWidth + "px;");
                short bgColor = cellStyle.getFillForegroundColor();
                hc = palette.getColor(bgColor);
                String bgColorStr = convertToStardColor(hc);
                if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
                    // 背景颜色
                    sb.append("background-color:" + bgColorStr + ";");
                }
                sb.append(getBorderStyle(palette, 0, cellStyle.getBorderTop().getCode(), cellStyle.getTopBorderColor()));
                sb.append(getBorderStyle(palette, 1, cellStyle.getBorderRight().getCode(), cellStyle.getRightBorderColor()));
                sb.append(getBorderStyle(palette, 3, cellStyle.getBorderLeft().getCode(), cellStyle.getLeftBorderColor()));
                sb.append(getBorderStyle(palette, 2, cellStyle.getBorderBottom().getCode(), cellStyle.getBottomBorderColor()));
            }

            sb.append("' ");
        }
    }

    /**
     * 单元格内容的水平对齐方式
     *
     * @param alignment
     * @return
     */
    private static String convertAlignToHtml(short alignment) {

        String align = "center";
        switch (alignment) {
//            case HorizontalAlignment.LEFT.getCode():
            case 1:
                align = "left";
                break;
            case (short) 2:
                align = "center";
                break;
            case (short) 3:
                align = "right";
                break;
            default:
                break;
        }
        return align;
    }

    /**
     * 单元格中内容的垂直排列方式
     *
     * @param verticalAlignment
     * @return
     */
    private static String convertVerticalAlignToHtml(short verticalAlignment) {

        String valign = "middle";
        switch (verticalAlignment) {
            case (short) 2:
                valign = "bottom";
                break;
            case (short) 1:
                valign = "center";
                break;
            case (short) 0:
                valign = "top";
                break;
            default:
                break;
        }
        return valign;
    }

    private static String convertToStardColor(HSSFColor hc) {

        StringBuffer sb = new StringBuffer("");
        if (hc != null) {
            if ((short) HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex() == hc.getIndex()) {
                return null;
            }
            sb.append("#");
            for (int i = 0; i < hc.getTriplet().length; i++) {
                sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
            }
        }

        return sb.toString();
    }

    private static String fillWithZero(String str) {
        if (str != null && str.length() < 2) {
            return "0" + str;
        }
        return str;
    }

    static String[] bordesr = {"border-top:", "border-right:", "border-bottom:", "border-left:"};
    static String[] borderStyles = {"solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid ", "solid", "solid", "solid", "solid", "solid"};

    private static String getBorderStyle(HSSFPalette palette, int b, short s, short t) {

        if (s == 0) return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
        String borderColorStr = convertToStardColor(palette.getColor(t));
        borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr;
        return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";

    }

    private static String getBorderStyle(int b, short s, XSSFColor xc) {

        if (s == 0) return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
        if (xc != null && !"".equals(xc)) {
            String borderColorStr = xc.getARGBHex();//t.getARGBHex();
            borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr.substring(2);
            return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";
        }
        return "";
    }

    /*
     * @param content 生成的excel表格标签
     * @param htmlPath 生成的html文件地址
     */
    private static void writeFile(String content, String htmlPath) {
        File file2 = new File(htmlPath);
        StringBuilder sb = new StringBuilder();
        try {
            file2.createNewFile();//创建文件
            sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>Html Test</title></head><body>");
            sb.append("<div>");
            sb.append(content);
            sb.append("</div>");
            sb.append("</body></html>");

            PrintStream printStream = new PrintStream(new FileOutputStream(file2));
            //将字符串写入文件
            printStream.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
