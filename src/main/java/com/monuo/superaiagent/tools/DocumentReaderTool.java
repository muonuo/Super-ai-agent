//package com.monuo.superaiagent.tools;
//
//import cn.hutool.core.io.FileUtil;
//import com.monuo.superaiagent.constant.FileConstant;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.ai.tool.annotation.ToolParam;
//
//import java.io.File;
//import java.io.FileInputStream;
//
///**
// * 文档读取工具类（支持 Word、PDF 等格式）
// *
// * 注意：需要添加以下依赖到 pom.xml：
// *
// * <!-- Apache POI for Word documents -->
// * <dependency>
// *     <groupId>org.apache.poi</groupId>
// *     <artifactId>poi-ooxml</artifactId>
// *     <version>5.2.3</version>
// * </dependency>
// *
// * <!-- Apache PDFBox for PDF documents -->
// * <dependency>
// *     <groupId>org.apache.pdfbox</groupId>
// *     <artifactId>pdfbox</artifactId>
// *     <version>2.0.29</version>
// * </dependency>
// */
//public class DocumentReaderTool {
//    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";
//
//    @Tool(description = "Read content from Word document (.docx)")
//    public String readWordDocument(@ToolParam(description = "Name of the Word file to read") String fileName) {
//        String filePath = FILE_DIR + "/" + fileName;
//        try {
//            FileInputStream fis = new FileInputStream(filePath);
//            XWPFDocument document = new XWPFDocument(fis);
//            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
//            String content = extractor.getText();
//            extractor.close();
//            document.close();
//            fis.close();
//            return content;
//        } catch (Exception e) {
//            return "Error reading Word document: " + e.getMessage();
//        }
//    }
//
//    @Tool(description = "Read content from PDF document (.pdf)")
//    public String readPdfDocument(@ToolParam(description = "Name of the PDF file to read") String fileName) {
//        String filePath = FILE_DIR + "/" + fileName;
//        try {
//            PDDocument document = PDDocument.load(new File(filePath));
//            PDFTextStripper stripper = new PDFTextStripper();
//            String content = stripper.getText(document);
//            document.close();
//            return content;
//        } catch (Exception e) {
//            return "Error reading PDF document: " + e.getMessage();
//        }
//    }
//
//    @Tool(description = "Read content from any supported document format (auto-detect)")
//    public String readDocument(@ToolParam(description = "Name of the file to read") String fileName) {
//        String extension = FileUtil.extName(fileName).toLowerCase();
//
//        switch (extension) {
//            case "docx":
//                return readWordDocument(fileName);
//            case "pdf":
//                return readPdfDocument(fileName);
//            case "txt":
//            case "md":
//            case "log":
//            case "csv":
//            case "json":
//            case "xml":
//                // 文本文件，直接读取
//                String filePath = FILE_DIR + "/" + fileName;
//                try {
//                    return FileUtil.readUtf8String(filePath);
//                } catch (Exception e) {
//                    return "Error reading text file: " + e.getMessage();
//                }
//            default:
//                return "Unsupported file format: " + extension +
//                       ". Supported formats: txt, md, log, csv, json, xml, docx, pdf";
//        }
//    }
//}
