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
 * This class is helper for file excel. <br/>
 * <b>Class</b>: {@link ExcelHelper}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class ExcelHelper {

    /**
     * Validate that a file is an Excel file.
     *
     * @param file File to validate
     * @return True if the file is an Excel file, false otherwise
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
     * Get value of cell from Excel file.
     *
     * @param currentCell Current cell
     * @return Value of cell
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
