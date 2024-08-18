package org.mike.delivery.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mike.delivery.entity.HierarchicalList;
import org.mike.delivery.entity.HierarchicalObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    public byte[] convertExcelToPdf(byte[] excelBytes, HierarchicalList hierarchicalList) throws IOException {
        PDDocument document = new PDDocument();
        PDRectangle pageSize = PDRectangle.A4;
        PDPage page = new PDPage(pageSize);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        ClassPathResource fontResource = new ClassPathResource("fonts/font.ttf");
        PDType0Font font = PDType0Font.load(document, fontResource.getInputStream());
        contentStream.setFont(font, 12); // Устанавливаем шрифт

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);

            float y = pageSize.getHeight() - 50;
            float xOffset = 50;
            float yOffset = 20;

            float pageWidth = pageSize.getWidth();

            int rowIndex = 0;
            for (Row row : sheet) {
                HierarchicalObject currentObject = getObjectForRow(hierarchicalList, rowIndex-1);

                float x = xOffset;
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);

                    String cellValue = getCachedValueFromCell(cell, currentObject, hierarchicalList);
                    contentStream.showText(cellValue);
                    contentStream.endText();

                    float textWidth = (font.getStringWidth(cellValue) / 1000) * 12;
                    if (x + textWidth > pageWidth - 50) {
                        pageWidth = x + textWidth + 50;
                        page.setMediaBox(new PDRectangle(pageWidth, pageSize.getHeight()));
                    }
                    x += Math.max(100, textWidth + 20);
                }
                y -= yOffset;
                rowIndex++;

                if (y < yOffset) {
                    contentStream.close();
                    page = new PDPage(new PDRectangle(pageWidth, pageSize.getHeight()));
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, 12);
                    y = pageSize.getHeight() - 50;
                }
            }
        }

        contentStream.close();

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        document.save(pdfOutputStream);
        document.close();

        return pdfOutputStream.toByteArray();
    }

    private String getCachedValueFromCell(Cell cell, HierarchicalObject currentObject, HierarchicalList hierarchicalList) {
        if (cell.getCellType() == CellType.FORMULA) {
            String formula = cell.getCellFormula();

            if (formula.matches("SUM\\(.*INDEX\\(.*MATCH\\(.*--\\(\\(.*=.*\\)\\*\\(.*=.*\\)\\), 0\\)-1\\)\\) \\+ \\(.*\\*.*\\)")) {
                return currentObject != null ? currentObject.getCost() : "0";
            }

            if (formula.matches("SUMIF\\(.*, \"<>\".*, .*\\)")) {
                // Возвращаем сумму всех главных объектов (объекты на верхнем уровне)
                double totalCost = calculateTotalCostOfMainObjects(hierarchicalList);
                return String.valueOf(totalCost);
            }
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private double calculateTotalCostOfMainObjects(HierarchicalList hierarchicalList) {
        double totalCost = 0;

        for (HierarchicalObject object : hierarchicalList.getObjects()) {
            totalCost += Double.parseDouble(object.getCost());
        }

        return totalCost;
    }

    private HierarchicalObject getObjectForRow(HierarchicalList hierarchicalList, int rowIndex ) {
        List<HierarchicalObject> objects = hierarchicalList.getObjects();
        HierarchicalObject object = getObjectForRowRecursive(objects, rowIndex, new int[] { 0 });
        if (object != null) {
            System.out.println("Row " + rowIndex + " maps to object: " + object.getName() + " with cost: " + object.getCost());
        } else {
            System.out.println("Row " + rowIndex + " does not map to any object.");
        }
        return object;
    }

    private HierarchicalObject getObjectForRowRecursive(List<HierarchicalObject> objects, int rowIndex, int[] currentRow) {
        for (HierarchicalObject object : objects) {
            if (currentRow[0] == rowIndex) {
                return object;
            }
            currentRow[0]++;
            if (!object.getSubObjects().isEmpty()) {
                HierarchicalObject foundObject = getObjectForRowRecursive(object.getSubObjects(), rowIndex, currentRow);
                if (foundObject != null) {
                    return foundObject;
                }
            }
        }
        return null;
    }
}
