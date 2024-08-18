package org.mike.delivery.controllers;


import org.mike.delivery.service.ExcelService;
import org.mike.delivery.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.mike.delivery.object.HierarchicalList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

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
