package com.jfzt.meeting.utils;

import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

/**
 * 向下添加行单元格
 */
/**
 * 向下添加行单元格
 * @author zhenxing.lu
 * @since  2024-05-17 14:43:45
 */
public class ExcelUtil {

    public static void InsertRow(ExcelWriter writer, int startRow, int rows, XSSFSheet sheet, Boolean copyvalue) {
        if(sheet.getRow(startRow+1)==null){
            // 如果复制最后一行，首先需要创建最后一行的下一行，否则无法插入，Bug 2023/03/20修复
            sheet.createRow(startRow+1);
        }

        //先获取原始的合并单元格address集合
        List<CellRangeAddress> originMerged = sheet.getMergedRegions();

        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            //判断移动的行数后重新拆分
            if(region.getFirstRow()>startRow){
                sheet.removeMergedRegion(i);
            }
        }

        sheet.shiftRows(startRow,sheet.getLastRowNum(),rows,true,false);
        sheet.createRow(startRow);

        for(CellRangeAddress cellRangeAddress : originMerged) {
            //这里的8是插入行的index，表示这行之后才重新合并
            if(cellRangeAddress.getFirstRow() > startRow) {
                //你插入了几行就加几，我这里插入了一行，加1
                int firstRow = cellRangeAddress.getFirstRow() + rows;
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(firstRow, (firstRow + (cellRangeAddress
                        .getLastRow() - cellRangeAddress.getFirstRow())), cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }
        writer.flush();
        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
        cellCopyPolicy.setCopyCellValue(copyvalue);
        cellCopyPolicy.isCopyCellValue();
        for (int i = 0; i < rows; i++) {
            sheet.copyRows(startRow-1,startRow-1,startRow+i,cellCopyPolicy);
        }
        writer.close();
    }
}
