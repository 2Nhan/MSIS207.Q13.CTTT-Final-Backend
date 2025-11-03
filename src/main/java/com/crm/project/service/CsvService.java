package com.crm.project.service;

import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.utils.FileUploadUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
public class CsvService {
    public <T> List<T> parseCsvFile(MultipartFile file, Class<T> csvClass) throws IOException {
        FileUploadUtil.checkContentType(file);

        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(csvClass);

        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(strategy)
                .withIgnoreEmptyLine(true)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<T> list = csvToBean.parse();

        FileUploadUtil.checkImportRows(list.size());
        return list;
    }
}
