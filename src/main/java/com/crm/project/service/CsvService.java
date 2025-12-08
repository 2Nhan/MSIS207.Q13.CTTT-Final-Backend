package com.crm.project.service;

import com.crm.project.internal.ImportInfo;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.utils.FileUploadUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class CsvService {
    public ImportInfo parseCsvFile(Map<String, String> matching, MultipartFile file) throws IOException {
        FileUploadUtil.checkContentType(file);

        List<Map<String, String>> rows = new ArrayList<>();

        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        CSVParser parser = format.parse(reader);

        List<String> headers = parser.getHeaderNames();

        for (CSVRecord record : parser) {
            if (record.size() != headers.size()) {
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }
            Map<String, String> row = new LinkedHashMap<>();
            for (String key : matching.keySet()) {
                if (!headers.contains(key)) {
                    throw new AppException(ErrorCode.WRONG_MATCHING);
                }
                row.put(matching.get(key), record.get(key));
            }
            rows.add(row);
        }

        FileUploadUtil.checkImportRows(rows.size());

        return ImportInfo.builder()
                .data(rows)
                .build();
    }
}
