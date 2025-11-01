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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public final class UploadFileUtil {
    public static final long MAX_FILE_SIZE = 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png))$)";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String IMPORT_PATTERN = "([^\\s]+(\\.(?i)(csv|xls|xlsx))$)";
    public static final int MAX_TOTAL_ROWS = 500;

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

    public static void checkImportFile(final MultipartFile file) throws IOException {
        if (!isAllowedExtension(file.getOriginalFilename(), IMPORT_PATTERN)) {
            throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        long countRow = 0;
        if (extension.equalsIgnoreCase("csv")) {
            countRow = countCsvRows(file);
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            countRow = countExcelRows(file);
        }

        if (countRow == 0) {
            throw new AppException(ErrorCode.EMPTY_FILE);
        } else if (countRow > MAX_TOTAL_ROWS + 1) {
            throw new AppException(ErrorCode.LIMIT_ROWS_EXCEEDED);
        }
    }

    private long countCsvRows(final MultipartFile file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        return bufferedReader.lines().count();
    }

    private long countExcelRows(final MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        return sheet.getPhysicalNumberOfRows();
    }
}
