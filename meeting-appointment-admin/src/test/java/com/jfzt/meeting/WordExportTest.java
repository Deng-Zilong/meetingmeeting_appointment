package com.jfzt.meeting;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.service.MeetingWordService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class WordExportTest {
    @Autowired
    private MeetingWordService meetingWordService;

    @Test
    public void test () {
        Result<List<MeetingWord>> result = meetingWordService.getMeetingWord(574L, "ym");

        List<MeetingWord> nodes = result.getData();
        nodes.forEach(System.out::println);
        // 假设jsonString是包含嵌套结构的JSON字符串

        try {
            // 读取Word模板文件
            FileInputStream fis = new FileInputStream("D:\\项目\\保险问答系统Sprint02回顾会议纪要.docx");
            XWPFDocument document = new XWPFDocument(fis);

            // 打印所有段落的文本以调试
            System.out.println("Document paragraphs:");
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                System.out.println(paragraph.getText());
            }
            // 查找并定位到“主要内容：”标题
            XWPFParagraph targetParagraph = findParagraph(document);
            if (targetParagraph != null) {
                // 获取“主要内容：”标题所在位置
                int pos = document.getPosOfParagraph(targetParagraph) + 1;
                List<Integer> numbering = new ArrayList<>();

                // 在“主要内容：”标题后插入树形数据
                for (MeetingWord node : nodes) {
                    pos = fillTreeData(document, node, pos, numbering, 0);
                }

                // 保存修改后的文档
                FileOutputStream fos = new FileOutputStream("D:\\项目\\保险问答系统Sprint02回顾会议纪要2.docx");
                document.write(fos);
                fos.close();
            } else {
                System.out.println("没有找到'主要内容：'段落");
            }
            document.close();
            fis.close();

            System.out.println("Word文档处理完成并保存！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static XWPFParagraph findParagraph (XWPFDocument document) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getText().contains("主要内容：")) {
                return paragraph;
            }
        }
        return null;
    }

    private static int fillTreeData (XWPFDocument document, MeetingWord node, int pos, List<Integer> numbering, Integer parentLevel) {
        if (node.getLevel() != 0) {
            while (numbering.size() > node.getLevel()) {
                numbering.removeLast();
            }
            if (numbering.size() == node.getLevel()) {
                numbering.set(node.getLevel() - 1, numbering.get(node.getLevel() - 1) + 1);
            } else {
                numbering.add(1);
            }

            StringBuilder numberPrefix = new StringBuilder();
            for (int i = 0; i < numbering.size(); i++) {
                if (i > 0) numberPrefix.append(".");
                numberPrefix.append(numbering.get(i));
            }
            numberPrefix.append(". ");

            XWPFParagraph titleParagraph = document.insertNewParagraph(document.getParagraphArray(pos++).getCTP().newCursor());
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(numberPrefix + node.getTitle());
            titleRun.setBold(true);
            titleRun.setFontSize(getFontSizeForLevel(node.getLevel()));
            titleParagraph.setIndentationLeft(node.getLevel() * 200);
        } else {
            XWPFParagraph contentParagraph = document.insertNewParagraph(document.getParagraphArray(pos++).getCTP().newCursor());
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(node.getContent());
            contentParagraph.setIndentationLeft(parentLevel * 200 + 200); // 内容缩进
        }
        if (node.getChildrenPart() == null || node.getChildrenPart().isEmpty()) {
            return pos;
        }

        for (MeetingWord child : node.getChildrenPart()) {
            pos = fillTreeData(document, child, pos, numbering, node.getLevel());
        }
        return pos;
    }

    private static int getFontSizeForLevel (int level) {
        return switch (level) {
            case 1 -> 16; // 一级标题字号
            case 2 -> 14; // 二级标题字号
            case 3 -> 12; // 三级标题字号
            default -> 12; // 默认字号
        };
    }
}
