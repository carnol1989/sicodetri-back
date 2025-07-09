@integration
Feature: Integración del endpoint /report

  Background:
    * url baseUrl
    * configure cookies = { auto: true }
    # limpiar la base antes de cada escenario
    Given path '/api/v1/test/cleanup'
    When method post
    Then status 200

  Scenario: Éxito – reporte Libro Tributario (Tax Book) en XLSX
    # preparar datos
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 201

    # solicitar reporte tax book
    Given path '/api/v1/financial-fixed-assets/report'
    And param option = '1'
    And param fileType = 'xlsx'
    And param year = '2022'
    And param mainCode = '22.01.010862'
    And param accountingAccount = '3321161001'
    And param startAcquisitionDate = '2007-12-31'
    And param endAcquisitionDate   = '2007-12-31'
    And param startDateUse         = '2007-12-31'
    And param endDateUse           = '2007-12-31'
    When method get
    Then status 200
    And match responseHeaders['Content-Disposition'][0] contains 'Libro_Tributario_Activo_Fijo_'
    And match responseHeaders['Content-Type'][0] == 'application/octet-stream'
    And assert responseBytes.length > 0

  Scenario: Éxito – reporte Detalle Financiero y Tributario en PDF
    # preparar datos (reutilizamos el mismo upload+save del XLSX anterior)
    Given path '/api/v1/financial-fixed-assets/report'
    And param option = '2'
    And param fileType = 'pdf'
    And param year = '2022'
    And param mainCode = '22.01.010862'
    And param accountingAccount = '3321161001'
    And param startAcquisitionDate = '2007-12-31'
    And param endAcquisitionDate   = '2007-12-31'
    And param startDateUse         = '2007-12-31'
    And param endDateUse           = '2007-12-31'
    When method get
    Then status 200
    And match responseHeaders['Content-Disposition'][0] contains 'Libro_Detalle_Financiero_Tributario_Depreciacion_'
    And match responseHeaders['Content-Type'][0] == 'application/octet-stream'
    And assert responseBytes.length > 0

  Scenario: Error – sin datos para el periodo
    # limpiar y no preparar datos
    Given path '/api/v1/test/cleanup'
    When method post
    Then status 200

    Given path '/api/v1/financial-fixed-assets/report'
    And param option = '1'
    And param fileType = 'xlsx'
    And param year = '1999'
    And param mainCode = '22.01.010862'
    And param accountingAccount = '3321161001'
    And param startAcquisitionDate = '2007-12-31'
    And param endAcquisitionDate   = '2007-12-31'
    And param startDateUse         = '2007-12-31'
    And param endDateUse           = '2007-12-31'
    When method get
    Then status 404
    And match response.code == '01'
    And match response.message contains 'No hay información para el periodo.'

  Scenario: Error – opción de reporte no soportada
    Given path '/api/v1/financial-fixed-assets/report'
    And param option = '99'
    And param fileType = 'xlsx'
    And param year = '2022'
    And param mainCode = '22.01.010862'
    And param accountingAccount = '3321161001'
    And param startAcquisitionDate = '2007-12-31'
    And param endAcquisitionDate   = '2007-12-31'
    And param startDateUse         = '2007-12-31'
    And param endDateUse           = '2007-12-31'
    When method get
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Tipo de reporte no soportado para opción: 99'
