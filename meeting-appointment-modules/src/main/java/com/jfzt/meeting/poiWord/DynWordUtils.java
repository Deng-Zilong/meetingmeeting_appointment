package com.jfzt.meeting.poiWord;


import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


/**
 * poi工具包
 */
public class DynWordUtils {

    private final Logger logger = LoggerFactory.getLogger(DynWordUtils.class);

    /**
     * 被list替换的段落 被替换的都是oldParagraph
     */
    private XWPFParagraph oldParagraph;

    /**
     * 参数
     */
    private Map<String, Object> paramMap;

    /**
     * 当前元素的位置
     */
    int n = 0;

    /**
     * 判断当前是否是遍历的表格
     */
    boolean isTable = false;

    /**
     * 模板对象
     */
    XWPFDocument templateDoc;

    /**
     * 默认字体的大小
     */
    final int DEFAULT_FONT_SIZE = 10;

    /**
     * 重复模式的占位符所在的行索引
     */
    private int currentRowIndex;
    /**
     * 用户查询所在行
     */
    public String searchText = "主要内容";
    /**
     * 所在行
     */
    public int row;
    /**
     * 入口
     *
     * @param paramMap     模板中使用的参数
     * @param templatePaht 模板全路径
     * @param response     下载response
     */
    public static void process(Map<String, Object> paramMap, String templatePaht, HttpServletResponse response, MeetingRecordVO meetingRecordVO, String operation, String path, List<MeetingWord> meetingWords1) {
        DynWordUtils dynWordUtils = new DynWordUtils();
        dynWordUtils.setParamMap(paramMap);
        dynWordUtils.createWord(templatePaht, response, meetingRecordVO, operation, path, meetingWords1);
    }


    /**
     * 生成动态的word
     *
     * @param templatePath
     * @param response
     */
    public void createWord(String templatePath, HttpServletResponse response, MeetingRecordVO meetingRecordVO, String operation, String path, List<MeetingWord> meetingWords1) {
        try (InputStream inputStream = new FileInputStream(ResourceUtils.getFile(templatePath))) {
            templateDoc = new XWPFDocument(OPCPackage.open(inputStream));
            parseTemplateWord();
            //填写主要内容word的
            contentWord(templateDoc, meetingWords1);
            if ("1".equals(operation)) {
                //直接导出电脑文件
                FileOutputStream outputStream = new FileOutputStream(path + meetingRecordVO.getId() + meetingRecordVO.getTitle() + ".docx");
                templateDoc.write(outputStream);
                outputStream.close();
                templateDoc.close();
            } else {
                //导出word
                response.setContentType("application/msword");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(meetingRecordVO.getTitle() + ".docx", "UTF-8"));
                OutputStream os = response.getOutputStream();
                templateDoc.write(os);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            String className = stackTrace[0].getClassName();
            String methodName = stackTrace[0].getMethodName();
            int lineNumber = stackTrace[0].getLineNumber();

            logger.error("错误：第:{}行, 类名:{}, 方法名:{}", lineNumber, className, methodName);
            throw new RuntimeException(e.getCause().getMessage());
        }
    }

    private void contentWord(XWPFDocument document, List<MeetingWord> meetingWords) throws IOException {

        //所在行

        row =  findWord(document, searchText);
        meetingWords.stream()
                .filter(meetingWord ->meetingWord.getMeetingRecordId() == 574)
                .map((menu) -> {
                    // 添加一级列表项
                    XWPFParagraph para1 = document.getParagraphs().get(++row);
                    addListLevel(para1, menu.getTitle(),  menu.getLevel());
                    addListLevel(para1, menu.getContent(),  menu.getLevel());
                    para1.setIndentationLeft(menu.getLevel()*400);
                    menu.setChildrenPart(getChildrenPart(menu, document));
                    return menu;
                }).collect(Collectors.toList());

    }

    private List<MeetingWord> getChildrenPart(MeetingWord menu1, XWPFDocument document) {
        List<MeetingWord> meetingWordList = menu1.getChildrenPart();
        List<MeetingWord> meetingWords1 = meetingWordList.stream()
                .filter(meetingWord ->menu1.getId().equals(meetingWord.getParentId()))
                .map((menu) -> {
                    // 添加1.级列表项
                    XWPFParagraph para2 = document.getParagraphs().get(++row);
                    addListLevel(para2, menu.getTitle(), menu.getLevel());
                    para2.setIndentationLeft(menu.getLevel()*300);
                    menu.setChildrenPart(getChildrenPart(menu, document));
                    return menu;
                }).collect(Collectors.toList());

        return meetingWords1;

    }

    /**
     * 解析word模板
     */
    public void parseTemplateWord() throws Exception {

        List<IBodyElement> elements = templateDoc.getBodyElements();

        for (; n < elements.size(); n++) {
            IBodyElement element = elements.get(n);
            // 普通段落
            if (element instanceof XWPFParagraph) {

                XWPFParagraph paragraph = (XWPFParagraph) element;
                oldParagraph = paragraph;
                if (paragraph.getParagraphText().isEmpty()) {
                    continue;
                }

                delParagraph(paragraph);

            } else if (element instanceof XWPFTable) {
                // 表格
                isTable = true;
                XWPFTable table = (XWPFTable) element;

                delTable(table, paramMap);
                isTable = false;
            }
        }

    }

    /**
     * 处理段落
     */
    private void delParagraph(XWPFParagraph paragraph) throws Exception {
        List<XWPFRun> runs = oldParagraph.getRuns();
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text == null) {
                continue;
            }
            sb.append(text);
            run.setText("", 0);
        }
        Placeholder(paragraph, runs, sb);
    }


    /**
     * 匹配传入信息集合与模板
     *
     * @param placeholder 模板需要替换的区域()
     * @param paramMap    传入信息集合
     * @return 模板需要替换区域信息集合对应值
     */
    public void changeValue(XWPFRun currRun, String placeholder, Map<String, Object> paramMap) throws Exception {

        String placeholderValue = placeholder;
        if (paramMap == null || paramMap.isEmpty()) {
            return;
        }

        Set<Map.Entry<String, Object>> textSets = paramMap.entrySet();
        for (Map.Entry<String, Object> textSet : textSets) {
            //匹配模板与替换值 格式${key}
            String mapKey = textSet.getKey();
            String docKey = PoiWordUtils.getDocKey(mapKey);

            if (placeholderValue.indexOf(docKey) != -1) {
                Object obj = textSet.getValue();
                // 需要添加一个list
                if (obj instanceof List) {
                    placeholderValue = delDynList(placeholder, (List) obj);
                } else {
                    placeholderValue = placeholderValue.replaceAll(
                            PoiWordUtils.getPlaceholderReg(mapKey)
                            , String.valueOf(obj));
                }
            }
        }

        currRun.setText(placeholderValue, 0);
    }

    /**
     * 处理的动态的段落（参数为list）
     *
     * @param placeholder 段落占位符
     * @param obj
     * @return
     */
    private String delDynList(String placeholder, List obj) {
        String placeholderValue = placeholder;
        List dataList = obj;
        Collections.reverse(dataList);
        if (dataList.size() == 0) {
            Object text = " ";
            placeholderValue = String.valueOf(text);
        } else {
            for (int i = 0, size = dataList.size(); i < size; i++) {
                Object text = dataList.get(i);
                // 占位符的那行, 不用重新创建新的行
                if (i == 0) {
                    placeholderValue = String.valueOf(text);
                } else {
                    XWPFParagraph paragraph = createParagraph(String.valueOf(text));
                    if (paragraph != null) {
                        oldParagraph = paragraph;
                    }
                    // 增加段落后doc文档的element的size会随着增加（在当前行的上面添加
                    // 这里减操作是回退并解析新增的行（因为可能新增的带有占位符，这里为了支持图片和表格）
                    if (!isTable) {
                        n--;
                    }
                }
            }
        }

        return placeholderValue;
    }

    /**
     * 创建段落 <p></p>
     *
     * @param texts
     */
    public XWPFParagraph createParagraph(String... texts) {

        // 使用游标创建一个新行
        XmlCursor cursor = oldParagraph.getCTP().newCursor();
        XWPFParagraph newPar = templateDoc.insertNewParagraph(cursor);
        // 设置段落样式
        newPar.getCTP().setPPr(oldParagraph.getCTP().getPPr());

        copyParagraph(oldParagraph, newPar, texts);

        return newPar;
    }

    /**
     * 处理表格（遍历）
     *
     * @param table    表格
     * @param paramMap 需要替换的信息集合
     */
    public void delTable(XWPFTable table, Map<String, Object> paramMap) throws Exception {
        List<XWPFTableRow> rows = table.getRows();
        for (int i = 0, size = rows.size(); i < size; i++) {
            XWPFTableRow row = rows.get(i);
            currentRowIndex = i;
            // 如果是动态添加行 直接处理后终止
            if (delAndJudgeRow(table, paramMap, row)) {
                return;
            }
        }
    }

    /**
     * 判断并且是否是动态行，并且处理表格占位符
     *
     * @param table    表格对象
     * @param paramMap 参数map
     * @param row      当前行
     * @return
     * @throws Exception
     */
    private boolean delAndJudgeRow(XWPFTable table, Map<String, Object> paramMap, XWPFTableRow row) throws Exception {
        // 当前行是动态行标志
        if (PoiWordUtils.isAddRow(row)) {
            List<XWPFTableRow> xwpfTableRows = addAndGetRows(table, row, paramMap);
            // 回溯添加的行，这里是试图处理动态添加的图片
            for (XWPFTableRow tbRow : xwpfTableRows) {
                delAndJudgeRow(table, paramMap, tbRow);
            }
            return true;
        }

        // 如果是重复添加的行
        if (PoiWordUtils.isAddRowRepeat(row)) {
            List<XWPFTableRow> xwpfTableRows = addAndGetRepeatRows(table, row, paramMap);
            /*// 回溯添加的行，这里是试图处理动态添加的图片
            for (XWPFTableRow tbRow : xwpfTableRows) {
                delAndJudgeRow(table, paramMap, tbRow);
            }*/
            return true;
        }
        // 当前行非动态行标签
        List<XWPFTableCell> cells = row.getTableCells();
        for (XWPFTableCell cell : cells) {
            //判断单元格是否需要替换
            if (PoiWordUtils.checkText(cell.getText())) {
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    StringBuilder sb = new StringBuilder();
                    for (XWPFRun run : runs) {
                        sb.append(run.toString());
                        run.setText("", 0);
                    }
                    Placeholder(paragraph, runs, sb);
                }
            }
        }
        return false;
    }

    /**
     * 处理占位符
     *
     * @param runs 当前段的runs
     * @param sb   当前段的内容
     * @throws Exception
     */
    private void Placeholder(XWPFParagraph currentPar, List<XWPFRun> runs, StringBuilder sb) throws Exception {
        if (runs.size() > 0) {
            String text = sb.toString();
            XWPFRun currRun = runs.get(0);
            if (PoiWordUtils.isPicture(text)) {
                // 清除段落的格式，否则图片的缩进有问题
                currentPar.getCTP().setPPr(null);
            } else {
                changeValue(currRun, text, paramMap);
            }
        }
    }


    /**
     * 添加行  标签行不是新创建的
     *
     * @param table
     * @param flagRow  flagRow 表有标签的行
     * @param paramMap 参数
     */
    private List<XWPFTableRow> addAndGetRows(XWPFTable table, XWPFTableRow flagRow, Map<String, Object> paramMap) throws Exception {
        List<XWPFTableCell> flagRowCells = flagRow.getTableCells();
        XWPFTableCell flagCell = flagRowCells.get(0);

        String text = flagCell.getText();
        List<List<String>> dataList = (List<List<String>>) PoiWordUtils.getValueByPlaceholder(paramMap, text);

        // 新添加的行
        List<XWPFTableRow> newRows = new ArrayList<>(dataList.size());
        if (dataList == null || dataList.size() <= 0) {
            return newRows;
        }

        XWPFTableRow currentRow = flagRow;
        int cellSize = flagRow.getTableCells().size();
        for (int i = 0, size = dataList.size(); i < size; i++) {
            if (i != 0) {
                currentRow = table.createRow();
                // 复制样式
                if (flagRow.getCtRow() != null) {
                    currentRow.getCtRow().setTrPr(flagRow.getCtRow().getTrPr());
                }
            }
            addRow(flagCell, currentRow, cellSize, dataList.get(i));
            newRows.add(currentRow);
        }
        return newRows;
    }

    /**
     * 添加重复多行 动态行  每一行都是新创建的
     *
     * @param table
     * @param flagRow
     * @param paramMap
     * @return
     * @throws Exception
     */
    private List<XWPFTableRow> addAndGetRepeatRows(XWPFTable table, XWPFTableRow flagRow, Map<String, Object> paramMap) throws Exception {
        List<XWPFTableCell> flagRowCells = flagRow.getTableCells();
        XWPFTableCell flagCell = flagRowCells.get(0);
        String text = flagCell.getText();
        List<List<String>> dataList = (List<List<String>>) PoiWordUtils.getValueByPlaceholder(paramMap, text);
        String tbRepeatMatrix = PoiWordUtils.getTbRepeatMatrix(text);
        Assert.notNull(tbRepeatMatrix, "模板矩阵不能为空");

        // 新添加的行
        List<XWPFTableRow> newRows = new ArrayList<>(dataList.size());
        if (dataList == null || dataList.size() <= 0) {
            return newRows;
        }

        String[] split = tbRepeatMatrix.split(PoiWordUtils.tbRepeatMatrixSeparator);
        int startRow = Integer.parseInt(split[0]);
        int endRow = Integer.parseInt(split[1]);
        int startCell = Integer.parseInt(split[2]);
        int endCell = Integer.parseInt(split[3]);

        XWPFTableRow currentRow;
        for (int i = 0, size = dataList.size(); i < size; i++) {
            int flagRowIndex = i % (endRow - startRow + 1);
            XWPFTableRow repeatFlagRow = table.getRow(flagRowIndex);
            // 清除占位符那行
            if (i == 0) {
                table.removeRow(currentRowIndex);
            }
            currentRow = table.createRow();
            // 复制样式
            if (repeatFlagRow.getCtRow() != null) {
                currentRow.getCtRow().setTrPr(repeatFlagRow.getCtRow().getTrPr());
            }
            addRowRepeat(startCell, endCell, currentRow, repeatFlagRow, dataList.get(i));

            // 处理新生成的那行是否有占位符需要处理
            delAndJudgeRow(table, paramMap, currentRow);

            newRows.add(currentRow);
        }
        deleteTemplateRow(startRow, endRow, table);
        return newRows;
    }

    /**
     * 删除模板行
     */
    private void deleteTemplateRow(int startRowIdx, int endRowIdx, XWPFTable table) {
        for (; startRowIdx <= endRowIdx; startRowIdx++) {
            table.removeRow(0);
        }
    }

    /**
     * 根据模板cell添加新行
     *
     * @param flagCell    模板列(标记占位符的那个cell)
     * @param row         新增的行
     * @param cellSize    每行的列数量（用来补列补足的情况）
     * @param rowDataList 每行的数据
     */
    private void addRow(XWPFTableCell flagCell, XWPFTableRow row, int cellSize, List<String> rowDataList) {
        for (int i = 0; i < cellSize; i++) {
            XWPFTableCell cell = row.getCell(i);
            cell = cell == null ? row.createCell() : row.getCell(i);
            if (i < rowDataList.size()) {
                PoiWordUtils.copyCellAndSetValue(flagCell, cell, rowDataList.get(i));
            } else {
                // 数据不满整行时，添加空列
                PoiWordUtils.copyCellAndSetValue(flagCell, cell, "");
            }
        }
    }

    /**
     * 根据模板cell  添加重复行
     *
     * @param startCell     模板列的开始位置
     * @param endCell       模板列的结束位置
     * @param currentRow    创建的新行
     * @param repeatFlagRow 模板列所在的行
     * @param rowDataList   每行的数据
     */
    private void addRowRepeat(int startCell, int endCell, XWPFTableRow currentRow, XWPFTableRow repeatFlagRow, List<String> rowDataList) {
        int cellSize = repeatFlagRow.getTableCells().size();
        for (int i = 0; i < cellSize; i++) {
            XWPFTableCell cell = currentRow.getCell(i);
            cell = cell == null ? currentRow.createCell() : currentRow.getCell(i);
            int flagCellIndex = i % (endCell - startCell + 1);
            XWPFTableCell repeatFlagCell = repeatFlagRow.getCell(flagCellIndex);
            if (i < rowDataList.size()) {
                PoiWordUtils.copyCellAndSetValue(repeatFlagCell, cell, rowDataList.get(i));
            } else {
                // 数据不满整行时，添加空列
                PoiWordUtils.copyCellAndSetValue(repeatFlagCell, cell, "");
            }
        }
    }

    /**
     * 复制段落
     *
     * @param sourcePar 原段落
     * @param targetPar
     * @param texts
     */
    private void copyParagraph(XWPFParagraph sourcePar, XWPFParagraph targetPar, String... texts) {

        targetPar.setAlignment(sourcePar.getAlignment());
        targetPar.setVerticalAlignment(sourcePar.getVerticalAlignment());

        // 设置布局
        targetPar.setAlignment(sourcePar.getAlignment());
        targetPar.setVerticalAlignment(sourcePar.getVerticalAlignment());

        if (texts != null && texts.length > 0) {
            String[] arr = texts;
            XWPFRun xwpfRun = sourcePar.getRuns().size() > 0 ? sourcePar.getRuns().get(0) : null;

            for (int i = 0, len = texts.length; i < len; i++) {
                String text = arr[i];
                XWPFRun run = targetPar.createRun();

                run.setText(text);

                run.setFontFamily(xwpfRun.getFontFamily());
                int fontSize = xwpfRun.getFontSize();
                run.setFontSize((fontSize == -1) ? DEFAULT_FONT_SIZE : fontSize);
                run.setBold(xwpfRun.isBold());
                run.setItalic(xwpfRun.isItalic());
            }
        }
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    private static int findWord(XWPFDocument document, String searchText) throws IOException {
        int position = -1;
        // 遍历文档中的所有段落
        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String text = paragraph.getText();
            if (text != null && text.contains(searchText)) {
                position = i;
                break;
            }
        }
        // 如果文本不在段落中，则搜索所有运行
        if (position == -1) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(run.getTextPosition());
                    if (text.contains(searchText)) {
                        position = paragraph.getRuns().indexOf(run);
                        break;
                    }
                }
            }
        }
        return position;
    }

    // 添加指定级别的列表项到指定段落
    private static void addListLevel(XWPFParagraph paragraph, String text, int level) {
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        // 设置段落为指定级别的有序列表
        BigInteger numId = getNumIdForLevel(level);
        paragraph.setNumID(numId);
    }

    // 根据级别获取对应的numId
    private static BigInteger getNumIdForLevel(int level) {
        // 实际应用中，根据级别返回不同的numId，这里简单示例固定几个级别对应的numId
        switch (level) {
            case 1:
                return BigInteger.valueOf(1);
            case 2:
                return BigInteger.valueOf(2);
            case 3:
                return BigInteger.valueOf(3);
            case 4:
                return BigInteger.valueOf(4);
            default:
                return BigInteger.valueOf(1);
        }
    }
}
