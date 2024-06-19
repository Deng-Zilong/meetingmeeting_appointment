package com.jfzt.meeting.admin;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.config.WxCpDefaultConfiguration;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.tp.service.WxCpTpService;
import me.chanjar.weixin.cp.tp.service.impl.WxCpTpOAServiceImpl;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class SysuserTest {


    @Autowired
    private WxCpServiceImpl wxCpService;
    @Autowired
    private WxCpDefaultConfiguration wxCpDefaultConfiguration;
    @Autowired
    private SysUserService sysUserService;


    /**
     * 返回企业微信二维码地址
     */
    @Test
    public void qrCode() throws IOException {
        String templatePath = "F:\\1.docx";
        String outputPath = "F:\\2.docx";

        // 模拟不确定数量的列表项
        List<String> dynamicItems = new ArrayList<>();
        dynamicItems.add("1");
        dynamicItems.add("2");
        dynamicItems.add("3");
        dynamicItems.add("4");
        // 动态添加更多...

        // 读取模板文档
        FileInputStream fis = new FileInputStream(templatePath);
        XWPFDocument document = new XWPFDocument(fis);

        // 替换模板中的占位符
        replacePlaceholders(document, dynamicItems, "${item}");

        // 保存新文档
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            document.write(out);
        }

        // 关闭资源
        document.close();
        fis.close();

    }

    private static void replacePlaceholders(XWPFDocument document, List<String> items, String placeholder) {
        for (XWPFParagraph p : document.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (!runs.isEmpty() && runs.get(0).getText(0).contains(placeholder)) {
                // 如果段落包含占位符，则替换之
                String originalText = runs.get(0).getText(0);
                StringBuilder replacedText = new StringBuilder(originalText);

                int index = 1;
                for (String item : items) {
                    replacedText.replace(replacedText.indexOf(placeholder), replacedText.indexOf(placeholder) + placeholder.length(), item);

                    // 如果还有剩余占位符且有更多项目，则继续替换
                    if (replacedText.indexOf(placeholder) != -1 && index < items.size()) {
                        index++;
                    } else {
                        break; // 占位符替换完毕或项目用尽
                    }
                }

                // 清除原有内容并设置新文本
                runs.get(0).setText("", 0);
                for (char c : replacedText.toString().toCharArray()) {
                    runs.get(0).setText(Character.toString(c), 0);
                }
            }
        }
    }





}

