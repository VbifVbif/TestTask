package org.mike.delivery.service;

import org.mike.delivery.object.HierarchicalList;

import java.io.IOException;

public interface PdfService {
    byte[] convertExcelToPdf(byte[] excelBytes, HierarchicalList hierarchicalList) throws IOException;

}
