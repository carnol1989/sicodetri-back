package com.epsgrau.pe.sicodetri.helper;

import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_DD_MM_YYYY;
import static com.epsgrau.pe.sicodetri.util.Constants.TYPE_XLSX;
import static com.epsgrau.pe.sicodetri.util.Functions.convertDateToLocalDateString;

import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility class for working with Excel files.
 * Provides methods to verify that an uploaded file is in Excel format (.xlsx),
 * and to extract typed values from individual Excel cells, handling
 * strings, numerics (including dates), formulas, and booleans.
 * <b>Class</b>: {@link ExcelHelper}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class ExcelHelper {

    /**
     * Checks whether the provided MultipartFile represents an Excel file in .xlsx format.
     * The method first inspects the content type for the official Excel MIME type.
     * If that check fails, it falls back to verifying the file extension.
     *
     * @param file the uploaded file to validate
     * @return {@code true} if the file is an Excel .xlsx file; {@code false} otherwise
     */
    public static boolean hasExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (TYPE_XLSX.equals(contentType)) {
            return true;
        }
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".xlsx");
    }

    /**
     * Extracts the value from a given Excel cell and converts it to an appropriate Java type.
     * - {@link CellType#STRING}: returns the cell's string content, trimmed.<br>
     * - {@link CellType#NUMERIC}: if the cell is date-formatted, returns a formatted date string;
     *   otherwise returns the formatted numeric value.<br>
     * - {@link CellType#FORMULA}: returns the cell's formula string.<br>
     * - {@link CellType#BOOLEAN}: returns the cell's boolean value.<br>
     * - {@code null}: returns {@code null} if the cell itself is {@code null}.
     *
     * @param currentCell the cell from which to retrieve the value
     * @return the cell's value as {@link String}, {@link Boolean}, or formatted
     *         date {@link String}, or {@code null} if the cell is {@code null}
     */
    public static Object getValueCell(Cell currentCell) {
        if (currentCell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();

        Object cellValue = null;
        if (currentCell.getCellType() == CellType.STRING) {
            cellValue = currentCell.getStringCellValue().trim();
        } else if (currentCell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(currentCell)) {
                cellValue = convertDateToLocalDateString(currentCell.getDateCellValue(),
                    FORMATTER_DD_MM_YYYY);
            } else {
                cellValue = formatter.formatCellValue(currentCell);
            }
        } else if (currentCell.getCellType() == CellType.FORMULA) {
            cellValue = currentCell.getCellFormula();
        } else if (currentCell.getCellType() == CellType.BOOLEAN) {
            cellValue = currentCell.getBooleanCellValue();
        }

        return cellValue;
    }

}
