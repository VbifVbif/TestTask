package org.mike.delivery.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mike.delivery.entity.HierarchicalList;
import org.mike.delivery.entity.HierarchicalObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelService {
    public ResponseEntity<byte[]> generateExcel(HierarchicalList hierarchicalList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Detail-list");

        int rowIndex = 0;

        Row headerRow = sheet.createRow(rowIndex++);
        headerRow.createCell(0).setCellValue("Название");
        headerRow.createCell(1).setCellValue("Цена");
        headerRow.createCell(2).setCellValue("Количество");
        headerRow.createCell(3).setCellValue("Стоимость");
        headerRow.createCell(5).setCellValue("Общая Сумма");


        for (HierarchicalObject obj : hierarchicalList.getObjects()) {
            rowIndex = createRows(sheet, obj, rowIndex, 0);
        }

        String columnAReference = "A1:A100";
        String columnDReference = "D1:D100";
        String TotalFormula = String.format("SUMIF(%s, \"<>\", %s)", columnAReference, columnDReference);
        headerRow.createCell(6).setCellFormula(TotalFormula);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] bytes = bos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "Details-list.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    private int createRows(Sheet sheet, HierarchicalObject obj, int rowIndex, int level) {
        if (level == 0 && rowIndex > 0) {
            rowIndex++;
        }

        String[] names = obj.getName().split(",");
        String[] prices = obj.getPrice().split(",");
        String[] quantities = obj.getQuantity().split(",");

        for (int i = 0; i < names.length; i++) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(level).setCellValue(names[i]);
            row.createCell(level + 1).setCellValue(i < prices.length ? prices[i] : "");
            row.createCell(level + 2).setCellValue(i < quantities.length ? quantities[i] : "");

            Cell costCell = row.createCell(level + 3);
            String priceCellReference = getCellReference(rowIndex - 1, level + 1);
            String quantityCellReference = getCellReference(rowIndex - 1, level + 2);
            String childPriceCellReference1 = getCellReference(rowIndex, level + 4);
            String childPriceCellReference2 = getCellReference(rowIndex + 100, level + 4);
            String childNameCellReference1 = getCellReference(rowIndex, level + 1);
            String childNameCellReference2 = getCellReference(rowIndex + 100, level + 1);
            String childQuantityCellReference1 = getCellReference(rowIndex, level + 2);
            String childQuantityCellReference2 = getCellReference(rowIndex + 100, level + 2);

            String formula = String.format(
                    "SUM(%s:INDEX(%s:%s, MATCH(1, --((%s:%s=\"\")*(%s:%s=\"\")), 0)-1)) + (%s * %s)",
                    childPriceCellReference1, childPriceCellReference1, childPriceCellReference2,
                    childNameCellReference1, childNameCellReference2,
                    childQuantityCellReference1, childQuantityCellReference2,
                    priceCellReference, quantityCellReference
            );

            costCell.setCellFormula(formula);
        }

        for (HierarchicalObject subObject : obj.getSubObjects()) {
            rowIndex = createRows(sheet, subObject, rowIndex, level + 1);
        }

        return rowIndex;
    }

    private String getCellReference(int rowIndex, int colIndex) {
        return CellReference.convertNumToColString(colIndex) + (rowIndex + 1);
    }
}