package com.epsgrau.pe.sicodetri.repository.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


/**
 * This class repository implement for Jdbc. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetJdbcImplRepository}<br/>
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
 *     <li>Sep 08, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Slf4j
@Repository
public class FinancialFixedAssetJdbcImplRepository implements FinancialFixedAssetJdbcRepository {

    private static final String CALL_SQL =
            "CALL get_report_tax_book(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Índices de parámetros en el CallableStatement (1–7 IN, 8–9 OUT, 10 OUT cursor)
    private static final int IDX_YEAR                       = 1;
    private static final int IDX_MAIN_CODE                  = 2;
    private static final int IDX_ACCOUNTING_ACCOUNT         = 3;
    private static final int IDX_START_ACQ_DATE             = 4;
    private static final int IDX_END_ACQ_DATE               = 5;
    private static final int IDX_START_DATE_USE             = 6;
    private static final int IDX_END_DATE_USE               = 7;
    private static final int IDX_OUT_ERROR_CODE             = 8;
    private static final int IDX_OUT_ERROR_MESSAGE          = 9;
    private static final int IDX_OUT_RESULT_SET_CURSOR      = 10;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params) {
        List<FinancialFixedAssetDto> dtoList = new ArrayList<>();

        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            throw new IllegalStateException("El DataSource no está configurado "
                + "(jdbcTemplate.getDataSource() retornó null).");
        }

        try (
                Connection conn = ds.getConnection();
                CallableStatement cs = conn.prepareCall(CALL_SQL)
        ) {
            conn.setAutoCommit(false);

            // 1) Seteamos parámetros IN (1–7)
            cs.setInt(IDX_YEAR,                   params.getYear());
            cs.setString(IDX_MAIN_CODE,           params.getMainCode());
            cs.setString(IDX_ACCOUNTING_ACCOUNT,  params.getAccountingAccount());
            cs.setString(IDX_START_ACQ_DATE,      params.getStartAcquisitionDate());
            cs.setString(IDX_END_ACQ_DATE,        params.getEndAcquisitionDate());
            cs.setString(IDX_START_DATE_USE,      params.getStartDateUse());
            cs.setString(IDX_END_DATE_USE,        params.getEndDateUse());

            // 2) Registramos parámetros OUT (8–9 texto, 10 cursor)
            cs.registerOutParameter(IDX_OUT_ERROR_CODE,    Types.VARCHAR);
            cs.registerOutParameter(IDX_OUT_ERROR_MESSAGE, Types.VARCHAR);
            cs.registerOutParameter(IDX_OUT_RESULT_SET_CURSOR, Types.OTHER);

            // 3) Ejecutamos el PROCEDURE
            cs.execute();

            // 4) Leemos OUT error code y error message
            String errorCode = cs.getString(IDX_OUT_ERROR_CODE);
            String errorMessage = cs.getString(IDX_OUT_ERROR_MESSAGE);

            // 5) Recuperamos el cursor (REFCURSOR) como ResultSet
            try (ResultSet rs = (ResultSet) cs.getObject(IDX_OUT_RESULT_SET_CURSOR)) {
                while (rs.next()) {
                    FinancialFixedAssetDto dto = mapRowToDto(rs);
                    dto.setErrorCode(errorCode);
                    dto.setErrorMessage(errorMessage);
                    dtoList.add(dto);
                }
            }

        } catch (SQLException e) {
            log.error("Error al ejecutar get_report_tax_book: \nMensaje: {} \nCódigo Error: {} "
                    + "\nCausa: {} \nSQL State: {}", e.getMessage(), e.getErrorCode(), e.getCause(),
                e.getSQLState());
            throw new RuntimeException("Error al obtener información Libro Tributario "
                + "de Activo Fijos");
        }

        return dtoList;
    }

    /**
     * Mapea una fila de ResultSet a FinancialFixedAssetDto,
     * aplicando valores por defecto cuando la columna venga null.
     */
    private FinancialFixedAssetDto mapRowToDto(ResultSet rs) throws SQLException {
        FinancialFixedAssetDto dto = new FinancialFixedAssetDto();

        dto.setMainFixedAssetCode(getStringOrDefault(rs, "CODIGO_ACTIVO_FIJO_PRINCIPAL"));
        dto.setFixedAssetCodeComponent(getStringOrDefault(rs, "CODIGO_ACTIVO_FIJO_COMPONENTE"));
        dto.setFixedAssetAccountingAccount(getStringOrDefault(rs, "CUENTA_CONTABLE_ACTIVO_FIJO"));
        dto.setDescription(getStringOrDefault(rs, "DESCRIPTION"));
        dto.setFixedAssetBrand(getStringOrDefault(rs, "MARCA_ACTIVO_FIJO"));
        dto.setFixedAssetModel(getStringOrDefault(rs, "MODELO_ACTIVO_FIJO"));
        dto.setFixedAssetSerialNumberAndOrPlate(getStringOrDefault(rs,
            "NUMERO_SERIE_PLACA_ACTIVO_FIJO"));

        dto.setInitialBalance(getBigDecimalOrDefault(rs, "COSTO_HISTORICO"));
        dto.setRevaluedAmount(getBigDecimalOrDefault(rs, "VALOR_REVALUADO"));
        dto.setAcquisitionsAdditions(getBigDecimalOrDefault(rs, "ADQUISICIONES_ADICIONES"));
        dto.setWithdrawalsAndOrRetirements(getBigDecimalOrDefault(rs, "RETIROS_BAJAS"));
        dto.setOtherAdjustments(getBigDecimalOrDefault(rs, "OTROS_AJUSTES"));
        dto.setBookValueOfFixedAssets(getBigDecimalOrDefault(rs, "VALOR_LIBROS_ACTIVO_FIJO"));

        dto.setAcquisitionDate(getDateOrDefault(rs, "FECHA_ADQUISICION"));
        dto.setStartDateFixedAssetUsage(getDateOrDefault(rs, "FECHA_INICIO_USO_ACTIVO_FIJO"));
        dto.setRetirementDate(getDateOrDefault(rs, "FECHA_RETIRO"));

        dto.setAppliedMethod(getStringOrDefault(rs, "METODO_APLICADO"));
        dto.setAuthorizationDocumentNumber(getStringOrDefault(rs, "N_DOCUMENTO_AUTORIZACION"));
        dto.setUsefulLifeYearsTax(rs.getInt("ANIOS_VIDA_UTIL_TRIBUTARIO"));
        dto.setAnnualRateTax(getStringOrDefault(rs, "TASA_ANUAL_TRIBUTARIO"));

        dto.setAnnualAccumulatedDepreciationPriorPeriod(
                getBigDecimalOrDefault(rs, "DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR")
        );
        dto.setCurrentDepreciation(getBigDecimalOrDefault(rs, "DEPRECIACION_EJERCICIO"));
        dto.setCurrentDepreciationRelatedWithdrawalsRetirements(
                getBigDecimalOrDefault(rs, "DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS")
        );
        dto.setCurrentDepreciationRelatedOtherAdjustments(
                getBigDecimalOrDefault(rs, "DEPRECIACION_RELACIONADA_OTROS_AJUSTES")
        );
        dto.setHistoricalAccumulatedDepreciation(
                getBigDecimalOrDefault(rs, "DEPRECIACION_ACUMULADA_HISTORICA")
        );
        dto.setNetFixedAssets(getBigDecimalOrDefault(rs, "ACTIVO_FIJO_NETO"));

        return dto;
    }

    /**
     * If the column string is null, returns BLANK; otherwise, the value.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return String
     * @throws SQLException SQL Exception
     */
    private String getStringOrDefault(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        return (val != null) ? val : _BLANK;
    }

    /**
     * If the column's BigDecimal is null, returns BigDecimal.ZERO; otherwise, the value.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return BigDecimal
     * @throws SQLException SQL Exception
     */
    private BigDecimal getBigDecimalOrDefault(ResultSet rs, String column) throws SQLException {
        BigDecimal val = rs.getBigDecimal(column);
        return (val != null) ? val : BigDecimal.ZERO;
    }

    /**
     * If the date column is null, returns LOCAL_DATE_DEFAULT; otherwise, converts it to LocalDate.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return LocalDate
     * @throws SQLException SQL Exception
     */
    private LocalDate getDateOrDefault(ResultSet rs, String column) throws SQLException {
        Date d = rs.getDate(column);
        return (d != null) ? d.toLocalDate() : LOCAL_DATE_DEFAULT;
    }
}