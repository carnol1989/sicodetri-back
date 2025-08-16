package com.epsgrau.pe.sicodetri.repository.impl;

import static com.epsgrau.pe.sicodetri.util.Functions.getBigDecimalOrDefault;
import static com.epsgrau.pe.sicodetri.util.Functions.getDateOrDefault;
import static com.epsgrau.pe.sicodetri.util.Functions.getIntegerOrDefault;
import static com.epsgrau.pe.sicodetri.util.Functions.getStringOrDefault;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * JDBC-based implementation of {@link FinancialFixedAssetJdbcRepository}.
 * Provides methods to invoke stored procedures for generating tax book and
 * detailed financial &amp; tax depreciation reports, mapping result set rows
 * to {@link FinancialFixedAssetDto} instances.
 * <b>Class</b>: {@link FinancialFixedAssetJdbcImplRepository}<br/>
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
@Slf4j
@Repository
public class FinancialFixedAssetJdbcImplRepository implements FinancialFixedAssetJdbcRepository {

    private static final String SP_TAX_BOOK =
        "CALL get_report_tax_book(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SP_DETAIL_DEPRE =
        "CALL get_report_detail_financial_and_tax_depreciation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Índices parámetros IN/OUT
    private static final int P_YEAR      = 1;
    private static final int P_MAIN_CODE = 2;
    private static final int P_ACCOUNT   = 3;
    private static final int P_START_ACQ = 4;
    private static final int P_END_ACQ   = 5;
    private static final int P_START_USE = 6;
    private static final int P_END_USE   = 7;
    private static final int P_OUT_CODE  = 8;
    private static final int P_OUT_MSG   = 9;
    private static final int P_OUT_CUR   = 10;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Calls the {@code get_report_tax_book} stored procedure to generate the tax book report.
     *
     * @param params the report parameters encapsulated in a {@link ParamReportDto}
     * @return a list of {@link FinancialFixedAssetDto} representing each row of the tax book report
     */
    @Override
    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params) {
        return executeProcedure(SP_TAX_BOOK, params, this::mapTaxBookRow);
    }

    /**
     * Calls the {@code get_report_detail_financial_and_tax_depreciation} stored procedure
     * to generate detailed financial and tax depreciation data.
     *
     * @param params the report parameters encapsulated in a {@link ParamReportDto}
     * @return a list of {@link FinancialFixedAssetDto} representing each row of the detail report
     */
    @Override
    public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationJdbc(
        ParamReportDto params) {
        return executeProcedure(SP_DETAIL_DEPRE, params, this::mapDetailDepRow);
    }

    /**
     * Executes a stored procedure with signature (IN×7, OUT code, OUT msg, OUT cursor)
     * and maps each row of the returned cursor using the provided rowMapper.
     *
     * @param callSql   the SQL call string for the stored procedure
     * @param params    the input parameters for the report
     * @param rowMapper a function that maps a {@link ResultSet} row and output array
     *                  to a {@link FinancialFixedAssetDto}
     * @return a list of mapped {@link FinancialFixedAssetDto} objects
     */
    private List<FinancialFixedAssetDto> executeProcedure(
        String callSql,
        ParamReportDto params,
        BiFunction<ResultSet, String[], FinancialFixedAssetDto> rowMapper
    ) {
        List<FinancialFixedAssetDto> list = new ArrayList<>();
        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            throw new IllegalStateException("DataSource no configurado");
        }
        try (
            Connection conn = ds.getConnection();
            CallableStatement cs = conn.prepareCall(callSql)
        ) {
            conn.setAutoCommit(false);
            // 1) IN params
            cs.setInt(P_YEAR,            params.getYear());
            cs.setString(P_MAIN_CODE,    params.getMainCode());
            cs.setString(P_ACCOUNT,      params.getAccountingAccount());
            cs.setString(P_START_ACQ,    params.getStartAcquisitionDate());
            cs.setString(P_END_ACQ,      params.getEndAcquisitionDate());
            cs.setString(P_START_USE,    params.getStartDateUse());
            cs.setString(P_END_USE,      params.getEndDateUse());
            // 2) OUT params
            cs.registerOutParameter(P_OUT_CODE, Types.VARCHAR);
            cs.registerOutParameter(P_OUT_MSG,  Types.VARCHAR);
            cs.registerOutParameter(P_OUT_CUR,  Types.OTHER);
            // 3) Ejecutar
            cs.execute();
            String code = cs.getString(P_OUT_CODE);
            String msg  = cs.getString(P_OUT_MSG);
            // 4) Cursor → ResultSet
            try (ResultSet rs = (ResultSet) cs.getObject(P_OUT_CUR)) {
                while (rs.next()) {
                    FinancialFixedAssetDto dto = rowMapper.apply(rs, new String[]{ code, msg });
                    list.add(dto);
                }
            }
        } catch (SQLException ex) {
            log.error("Error al ejecutar SP: {} – code: {}, msg: {}",
                callSql, ex.getErrorCode(), ex.getSQLState(), ex);
            throw new RuntimeException("Fallo al obtener datos de SP", ex);
        }
        return list;
    }

    /**
     * Maps fields common to both tax book and detail depreciation reports.
     *
     * @param rs the {@link ResultSet} positioned at the current row
     * @return a partially populated {@link FinancialFixedAssetDto}
     * @throws SQLException if a database access error occurs
     */
    private FinancialFixedAssetDto mapCommonFields(ResultSet rs) throws SQLException {
        FinancialFixedAssetDto dto = new FinancialFixedAssetDto();
        dto.setMainFixedAssetCode(getStringOrDefault(rs, "CODIGO_ACTIVO_FIJO_PRINCIPAL"));
        dto.setFixedAssetCodeComponent(getStringOrDefault(rs, "CODIGO_ACTIVO_FIJO_COMPONENTE"));
        dto.setFixedAssetAccountingAccount(getStringOrDefault(rs, "CUENTA_CONTABLE_ACTIVO_FIJO"));
        dto.setDescription(getStringOrDefault(rs, "DESCRIPTION"));
        dto.setFixedAssetBrand(getStringOrDefault(rs, "MARCA_ACTIVO_FIJO"));
        dto.setFixedAssetModel(getStringOrDefault(rs, "MODELO_ACTIVO_FIJO"));
        dto.setFixedAssetSerialNumberAndOrPlate(
            getStringOrDefault(rs, "NUMERO_SERIE_PLACA_ACTIVO_FIJO"));
        dto.setInitialBalance(getBigDecimalOrDefault(rs, "COSTO_HISTORICO"));
        dto.setRevaluedAmount(getBigDecimalOrDefault(rs, "VALOR_REVALUADO"));
        dto.setBookValueOfFixedAssets(getBigDecimalOrDefault(rs, "VALOR_LIBROS_ACTIVO_FIJO"));
        dto.setAcquisitionDate(getDateOrDefault(rs, "FECHA_ADQUISICION"));
        dto.setStartDateFixedAssetUsage(getDateOrDefault(rs, "FECHA_INICIO_USO_ACTIVO_FIJO"));
        dto.setAppliedMethod(getStringOrDefault(rs, "METODO_APLICADO"));
        dto.setAuthorizationDocumentNumber(getStringOrDefault(rs, "N_DOCUMENTO_AUTORIZACION"));
        // código y mensaje de error se inyectarán al final
        return dto;
    }

    /**
     * Maps a single row from the tax book stored procedure to a DTO.
     *
     * @param rs  the {@link ResultSet} for the tax book row
     * @param out the output parameters array [code, message]
     * @return a fully populated {@link FinancialFixedAssetDto} for the tax book report
     */
    private FinancialFixedAssetDto mapTaxBookRow(ResultSet rs, String[] out) {
        try {
            FinancialFixedAssetDto dto = mapCommonFields(rs);
            dto.setAcquisitionsAdditions(getBigDecimalOrDefault(rs, "ADQUISICIONES_ADICIONES"));
            dto.setWithdrawalsAndOrRetirements(getBigDecimalOrDefault(rs, "RETIROS_BAJAS"));
            dto.setOtherAdjustments(getBigDecimalOrDefault(rs, "OTROS_AJUSTES"));
            dto.setRetirementDate(getDateOrDefault(rs, "FECHA_RETIRO"));
            dto.setUsefulLifeYearsTax(rs.getInt("ANIOS_VIDA_UTIL_TRIBUTARIO"));
            dto.setAnnualRateTax(getStringOrDefault(rs, "TASA_ANUAL_TRIBUTARIO"));
            dto.setCurrentDepreciation(getBigDecimalOrDefault(rs, "DEPRECIACION_EJERCICIO"));
            dto.setCurrentDepreciationRelatedWithdrawalsRetirements(
                getBigDecimalOrDefault(rs, "DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS"));
            dto.setCurrentDepreciationRelatedOtherAdjustments(
                getBigDecimalOrDefault(rs, "DEPRECIACION_RELACIONADA_OTROS_AJUSTES"));
            dto.setHistoricalAccumulatedDepreciation(
                getBigDecimalOrDefault(rs, "DEPRECIACION_ACUMULADA_HISTORICA"));
            dto.setNetFixedAssets(getBigDecimalOrDefault(rs, "ACTIVO_FIJO_NETO"));
            dto.setErrorCode(out[0]);
            dto.setErrorMessage(out[1]);
            return dto;
        } catch (SQLException ex) {
            throw new RuntimeException("Error mapeando fila TaxBook", ex);
        }
    }

    /**
     * Maps a single row from the detailed financial &amp; tax depreciation stored
     * procedure to a DTO.
     *
     * @param rs  the {@link ResultSet} for the detail depreciation row
     * @param out the output parameters array [code, message]
     * @return a fully populated {@link FinancialFixedAssetDto} for the detail depreciation report
     */
    private FinancialFixedAssetDto mapDetailDepRow(ResultSet rs, String[] out) {
        try {
            FinancialFixedAssetDto dto = mapCommonFields(rs);
            dto.setId(getIntegerOrDefault(rs, "ID"));
            dto.setAccountingPeriod(getDateOrDefault(rs, "PERIODO_CONTABLE"));
            dto.setUsefulLifeYearsFinancial(getIntegerOrDefault(rs, "ANIOS_VIDA_UTIL_FINANCIERO"));
            dto.setValueDepreciationFinancial(getBigDecimalOrDefault(rs,
                "DEPRECIACION_FINANCIERA"));
            dto.setAnnualAccumulatedDepreciationPriorPeriod(
                getBigDecimalOrDefault(rs, "DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR"));
            dto.setAnnualNetTaxValuePriorPeriod(
                getBigDecimalOrDefault(rs, "VALOR_NETO_TRIBUTARIO_CIERRE_EJERCICIO_ANTERIOR"));
            dto.setValueDepreciationTax(getBigDecimalOrDefault(rs, "DEPRECIACION_TRIBUTARIA"));
            dto.setNetTaxValue(getBigDecimalOrDefault(rs, "VALOR_NETO_TRIBUTARIO"));
            dto.setValueAddition(getBigDecimalOrDefault(rs, "ADICION"));
            dto.setValueDeduction(getBigDecimalOrDefault(rs, "DEDUCCION"));
            dto.setErrorCode(out[0]);
            dto.setErrorMessage(out[1]);
            return dto;
        } catch (SQLException ex) {
            throw new RuntimeException("Error mapeando fila DetailDepreciation", ex);
        }
    }
}