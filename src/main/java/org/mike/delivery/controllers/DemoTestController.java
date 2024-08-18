package org.mike.delivery.controllers;


import com.itextpdf.kernel.pdf.PdfDocument;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mike.delivery.service.ExcelService;
import org.mike.delivery.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.mike.delivery.entity.HierarchicalObject;
import org.mike.delivery.entity.HierarchicalList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
//import org.mike.delivery.service.ExcelToPdfConverter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.layout.element.Cell;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Controller
public class DemoTestController {

    @Autowired
    private ExcelService excelService;
    @Autowired
    private PdfService pdfService;


    @GetMapping("/part")
    public String mainPage(Model model) {
        return "MainPage";
    }

    @PostMapping("/submit-list/excel")
    public ResponseEntity<byte[]> downloadExcel(@ModelAttribute HierarchicalList hierarchicalList) throws IOException {
        hierarchicalList.printObjects();
        return excelService.generateExcel(hierarchicalList);
    }

    @PostMapping("/submit-list/pdf")
    public ResponseEntity<byte[]> downloadPDF(@ModelAttribute HierarchicalList hierarchicalList) throws IOException {
        hierarchicalList.printObjects();
        byte[] excelBytes = excelService.generateExcel(hierarchicalList).getBody();
        byte[] pdfBytes = pdfService.convertExcelToPdf(excelBytes, hierarchicalList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "Detail-list.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);


        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }


}
