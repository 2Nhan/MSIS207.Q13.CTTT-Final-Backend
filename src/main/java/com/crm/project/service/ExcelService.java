package com.crm.project.service;

import com.crm.project.dto.response.ImportPreviewResponse;
import com.crm.project.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ExcelService {

    public ImportPreviewResponse parseExcelFile(MultipartFile file) throws IOException {
        FileUploadUtil.checkContentType(file);

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);

        List<String> headerList = extractHeader(headerRow);

        List<Map<String, String>> dataRows = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            Map<String, String> rowMap = new LinkedHashMap<>();

            for (int i = 0; i < headerList.size(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                String value = (cell == null) ? "" : formatCell(cell);

                if (!value.isEmpty()) {
                    rowMap.put(headerList.get(i), value);
                }
            }

            dataRows.add(rowMap);
        }

        FileUploadUtil.checkImportRows(dataRows.size());

        return ImportPreviewResponse.builder()
                .userHeader(new LinkedHashSet<>(headerList)) // Set nhưng vẫn giữ thứ tự
                .data(dataRows)
                .build();
    }


    private List<String> extractHeader(Row row) {
        List<String> headers = new ArrayList<>();

        int colCount = row.getLastCellNum();
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String value = (cell == null) ? "" : formatCell(cell);

            if (!value.isEmpty()) {
                headers.add(value.trim());
            }
        }
        return headers;
    }

    private String formatCell(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
