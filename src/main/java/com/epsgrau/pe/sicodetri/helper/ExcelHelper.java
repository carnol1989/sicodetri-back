package com.epsgrau.pe.sicodetri.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_DD_MM_YYYY;
import static com.epsgrau.pe.sicodetri.util.Constants.TYPE_XLSX;
import static com.epsgrau.pe.sicodetri.util.Functions.convertDateToLocalDateString;

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

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE_XLSX.equals(file.getContentType());
    }

    public static Object getValueCell(Cell currentCell) {
        Object cellValue = null;
        if (currentCell.getCellType() == CellType.STRING) {
            cellValue = currentCell.getStringCellValue();
        } else if (currentCell.getCellType() == CellType.NUMERIC) {
            if(DateUtil.isCellDateFormatted(currentCell)) {
                cellValue = convertDateToLocalDateString(currentCell.getDateCellValue(), FORMATTER_DD_MM_YYYY);
            } else {
                cellValue = currentCell.getNumericCellValue();
            }
        } else if (currentCell.getCellType() == CellType.FORMULA) {
            cellValue = currentCell.getCellFormula();
        } else if (currentCell.getCellType() == CellType.BOOLEAN) {
            cellValue = currentCell.getBooleanCellValue();
        }
        return cellValue;
    }

}
