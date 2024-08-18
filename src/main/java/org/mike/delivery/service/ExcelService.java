package org.mike.delivery.service;

import org.mike.delivery.object.HierarchicalList;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface ExcelService {
    ResponseEntity<byte[]> generateExcel(HierarchicalList hierarchicalList) throws IOException;
}
