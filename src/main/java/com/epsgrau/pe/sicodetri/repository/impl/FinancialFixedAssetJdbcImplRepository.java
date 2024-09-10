package com.epsgrau.pe.sicodetri.repository.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epsgrau.pe.sicodetri.util.Constants.*;

@Repository
public class FinancialFixedAssetJdbcImplRepository implements FinancialFixedAssetJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params) {
        List<FinancialFixedAssetDto> dtoList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            CallableStatement call = connection.prepareCall("call get_report_tax_book(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            call.setInt(1, params.getYear());
            call.setString(2, params.getMainCode());
            call.setString(3, params.getAccountingAccount());
            call.setString(4, params.getStartAcquisitionDate());
            call.setString(5, params.getEndAcquisitionDate());
            call.setString(6, params.getStartDateUse());
            call.setString(7, params.getEndDateUse());
            call.registerOutParameter(8, Types.VARCHAR);
            call.registerOutParameter(9, Types.VARCHAR);
            call.registerOutParameter(10, Types.OTHER);
            call.execute();
            String errorCode = call.getString(8);
            String errorMessage = call.getString(9);
            ResultSet rs = (ResultSet) call.getObject(10);
            while (rs.next()) {
                FinancialFixedAssetDto dto = new FinancialFixedAssetDto();

                String mainFixedAssetCode = (rs.getString("CODIGO_ACTIVO_FIJO_PRINCIPAL") != null ?
                        rs.getString("CODIGO_ACTIVO_FIJO_PRINCIPAL") : _BLANK);
                dto.setMainFixedAssetCode(mainFixedAssetCode);

                String fixedAssetCodeComponent = (rs.getString("CODIGO_ACTIVO_FIJO_COMPONENTE") != null ?
                        rs.getString("CODIGO_ACTIVO_FIJO_COMPONENTE") : _BLANK);
                dto.setFixedAssetCodeComponent(fixedAssetCodeComponent);

                String fixedAssetAccountingAccount = (rs.getString("CUENTA_CONTABLE_ACTIVO_FIJO") != null ?
                        rs.getString("CUENTA_CONTABLE_ACTIVO_FIJO") : _BLANK);
                dto.setFixedAssetAccountingAccount(fixedAssetAccountingAccount);

                String description = (rs.getString("DESCRIPTION") != null ? rs.getString("DESCRIPTION") : _BLANK);
                dto.setDescription(description);

                String fixedAssetBrand = (rs.getString("MARCA_ACTIVO_FIJO") != null ?
                        rs.getString("MARCA_ACTIVO_FIJO") : _BLANK);
                dto.setFixedAssetBrand(fixedAssetBrand);

                String fixedAssetModel = (rs.getString("MODELO_ACTIVO_FIJO") != null ?
                        rs.getString("MODELO_ACTIVO_FIJO") : _BLANK);
                dto.setFixedAssetModel(fixedAssetModel);

                String fixedAssetSerialNumberAndOrPlate = (rs.getString("NUMERO_SERIE_PLACA_ACTIVO_FIJO") != null ?
                        rs.getString("NUMERO_SERIE_PLACA_ACTIVO_FIJO") : _BLANK);
                dto.setFixedAssetSerialNumberAndOrPlate(fixedAssetSerialNumberAndOrPlate);

                BigDecimal initialBalance = (rs.getBigDecimal("COSTO_HISTORICO") != null ?
                        rs.getBigDecimal("COSTO_HISTORICO") : BigDecimal.ZERO);
                dto.setInitialBalance(initialBalance);

                BigDecimal revaluedAmount = (rs.getBigDecimal("VALOR_REVALUADO") != null ?
                        rs.getBigDecimal("VALOR_REVALUADO") : BigDecimal.ZERO);
                dto.setRevaluedAmount(revaluedAmount);

                BigDecimal acquisitionsAdditions = (rs.getBigDecimal("ADQUISICIONES_ADICIONES") != null ?
                        rs.getBigDecimal("ADQUISICIONES_ADICIONES") : BigDecimal.ZERO);
                dto.setAcquisitionsAdditions(acquisitionsAdditions);

                BigDecimal withdrawalsAndOrRetirements = (rs.getBigDecimal("RETIROS_BAJAS") != null ?
                        rs.getBigDecimal("RETIROS_BAJAS") : BigDecimal.ZERO);
                dto.setWithdrawalsAndOrRetirements(withdrawalsAndOrRetirements);

                BigDecimal otherAdjustments = (rs.getBigDecimal("OTROS_AJUSTES") != null ?
                        rs.getBigDecimal("OTROS_AJUSTES") : BigDecimal.ZERO);
                dto.setOtherAdjustments(otherAdjustments);

                BigDecimal bookValueOfFixedAssets = (rs.getBigDecimal("VALOR_LIBROS_ACTIVO_FIJO") != null ?
                        rs.getBigDecimal("VALOR_LIBROS_ACTIVO_FIJO") : BigDecimal.ZERO);
                dto.setBookValueOfFixedAssets(bookValueOfFixedAssets);

                LocalDate acquisitionDate = (rs.getDate("FECHA_ADQUISICION") != null ?
                        rs.getDate("FECHA_ADQUISICION").toLocalDate() : LOCAL_DATE_DEFAULT);
                dto.setAcquisitionDate(acquisitionDate);

                LocalDate startDateFixedAssetUsage = (rs.getDate("FECHA_INICIO_USO_ACTIVO_FIJO") != null ?
                        rs.getDate("FECHA_INICIO_USO_ACTIVO_FIJO").toLocalDate() : LOCAL_DATE_DEFAULT);
                dto.setStartDateFixedAssetUsage(startDateFixedAssetUsage);

                LocalDate retirementDate = (rs.getDate("FECHA_RETIRO") != null ?
                        rs.getDate("FECHA_RETIRO").toLocalDate() : LOCAL_DATE_DEFAULT);
                dto.setRetirementDate(retirementDate);

                String appliedMethod = (rs.getString("METODO_APLICADO") != null ?
                        rs.getString("METODO_APLICADO") : _BLANK);
                dto.setAppliedMethod(appliedMethod);

                String authorizationDocumentNumber = (rs.getString("N_DOCUMENTO_AUTORIZACION") != null ?
                        rs.getString("N_DOCUMENTO_AUTORIZACION") : _BLANK);
                dto.setAuthorizationDocumentNumber(authorizationDocumentNumber);

                Integer usefulLifeYears = rs.getInt("ANIOS_VIDA_UTIL");
                dto.setUsefulLifeYears(usefulLifeYears);

                String annualRate = (rs.getString("TASA") != null ? rs.getString("TASA") : _BLANK);
                dto.setAnnualRate(annualRate);

                BigDecimal annualAccumulatedDepreciationPriorPeriod = (rs.getBigDecimal("DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR") != null ?
                        rs.getBigDecimal("DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR") : BigDecimal.ZERO);
                dto.setAnnualAccumulatedDepreciationPriorPeriod(annualAccumulatedDepreciationPriorPeriod);

                BigDecimal currentDepreciation = (rs.getBigDecimal("DEPRECIACION_EJERCICIO") != null ?
                        rs.getBigDecimal("DEPRECIACION_EJERCICIO") : BigDecimal.ZERO);
                dto.setCurrentDepreciation(currentDepreciation);

                BigDecimal currentDepreciationRelatedWithdrawalsRetirements = (rs.getBigDecimal("DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS") != null ?
                        rs.getBigDecimal("DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS") : BigDecimal.ZERO);
                dto.setCurrentDepreciationRelatedWithdrawalsRetirements(currentDepreciationRelatedWithdrawalsRetirements);

                BigDecimal currentDepreciationRelatedOtherAdjustments = (rs.getBigDecimal("DEPRECIACION_RELACIONADA_OTROS_AJUSTES") != null ?
                        rs.getBigDecimal("DEPRECIACION_RELACIONADA_OTROS_AJUSTES") : BigDecimal.ZERO);
                dto.setCurrentDepreciationRelatedOtherAdjustments(currentDepreciationRelatedOtherAdjustments);

                BigDecimal historicalAccumulatedDepreciation = (rs.getBigDecimal("DEPRECIACION_ACUMULADA_HISTORICA") != null ?
                        rs.getBigDecimal("DEPRECIACION_ACUMULADA_HISTORICA") : BigDecimal.ZERO);
                dto.setHistoricalAccumulatedDepreciation(historicalAccumulatedDepreciation);

                BigDecimal netFixedAssets = (rs.getBigDecimal("ACTIVO_FIJO_NETO") != null ?
                        rs.getBigDecimal("ACTIVO_FIJO_NETO") : BigDecimal.ZERO);
                dto.setNetFixedAssets(netFixedAssets);

                dtoList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dtoList;
    }

}
