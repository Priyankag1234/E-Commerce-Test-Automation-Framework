package com.ecommerce.framework.utils;

import com.ecommerce.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils — Apache POI wrapper for reading Excel test data files.
 *
 * <p>Reads .xlsx files from the classpath (src/main/resources/testdata/).
 * <p>Converts each row to a Map<columnHeader, cellValue> for clean consumption in tests.
 */
public final class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    private ExcelUtils() {}

    /**
     * Reads all rows from the given sheet and returns a list of row data maps.
     * The first row is treated as the header row (column names).
     *
     * @param filePath  classpath-relative path (e.g., "testdata/products.xlsx")
     * @param sheetName name of the sheet to read
     * @return list of maps where each map represents one row: header -> cell value
     */
    public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
        log.info("Reading Excel file: [{}] | Sheet: [{}]", filePath, sheetName);
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            if (inputStream == null) {
                throw new FrameworkException("Excel file not found on classpath: [" + filePath + "]");
            }

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new FrameworkException("Sheet [" + sheetName + "] not found in file [" + filePath + "]");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                log.warn("Excel sheet [{}] has no header row. Returning empty data.", sheetName);
                return data;
            }

            // Extract column headers from row 0
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }
            log.debug("Headers found: {}", headers);

            // Extract data rows (row 1 onwards)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);
                if (dataRow == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                    Cell cell = dataRow.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowMap.put(headers.get(colIndex), getCellValueAsString(cell));
                }
                data.add(rowMap);
            }

            log.info("Read {} data rows from sheet [{}]", data.size(), sheetName);
        } catch (IOException e) {
            throw new FrameworkException("Failed to read Excel file: [" + filePath + "]", e);
        }

        return data;
    }

    /**
     * Reads a specific cell value from a sheet.
     *
     * @param filePath  classpath-relative path
     * @param sheetName sheet name
     * @param rowIndex  row index (0-based)
     * @param colIndex  column index (0-based)
     * @return cell value as String
     */
    public static String getCellValue(String filePath, String sheetName, int rowIndex, int colIndex) {
        try (InputStream inputStream = ExcelUtils.class.getClassLoader().getResourceAsStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowIndex);
            Cell cell = row.getCell(colIndex);
            return getCellValueAsString(cell);

        } catch (IOException e) {
            throw new FrameworkException("Failed to read cell [" + rowIndex + "," + colIndex + "] from [" + filePath + "]", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helper
    // ─────────────────────────────────────────────────────────────

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
