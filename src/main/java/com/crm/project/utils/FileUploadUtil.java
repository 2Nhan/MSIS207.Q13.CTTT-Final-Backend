package com.crm.project.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public final class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String IMPORT_PATTERN = "([^\\s]+(\\.(?i)(csv|xls|xlsx))$)";
    public static final int MAX_TOTAL_ROWS = 100;
    public static final List TYPE = List.of("text/csv",
            "application/vnd.ms-excel", // .xls
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" //.xlsx
    );

    public static void checkContentType(MultipartFile file) {
        if (!TYPE.contains(file.getContentType())) {
            throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void checkImage(final MultipartFile file, final String pattern) {
        if (!isAllowedExtension(file.getOriginalFilename(), pattern)) {
            throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        final long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.INVALID_FILE_SIZE);
        }
    }

    public static String standardizeFileName(final String fileName) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        final String prefix = dateFormat.format(System.currentTimeMillis());

        final String baseName = FilenameUtils.getBaseName(fileName);

        final String extension = FilenameUtils.getExtension(fileName);

        return prefix + "_" + baseName + "." + extension;
    }

    public static void checkImportRows(final long countRow) {
        if (countRow == 0) {
            throw new AppException(ErrorCode.EMPTY_FILE);
        } else if (countRow > MAX_TOTAL_ROWS + 1) {
            throw new AppException(ErrorCode.LIMIT_ROWS_EXCEEDED);
        }
    }


}
