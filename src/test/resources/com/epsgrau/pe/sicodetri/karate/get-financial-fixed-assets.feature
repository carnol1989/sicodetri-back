@integration
Feature: Integración del endpoint GET /api/v1/financial-fixed-assets/{option}

  Background:
    * url baseUrl
    * configure cookies = { auto: true }
    # limpiar la base antes de cada escenario
    Given path '/api/v1/test/cleanup'
    When method post
    Then status 200

  Scenario: Opción 1 – obtener por periodo existente
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

    # búsqueda por periodo (option=1)
    Given path '/api/v1/financial-fixed-assets/1'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = ''
    When method get
    Then status 200
    And match response.code == '00'
    And assert response.response.length > 0
    And match each response.response[*].accountingPeriod contains '2022-01-01'

  Scenario: Opción 1 – periodo inválido
    Given path '/api/v1/financial-fixed-assets/1'
    And param period = '01-2022-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = ''
    When method get
    Then status 417
    And match response.code == '01'
    And match response.message contains 'Error al obtener informacion del activo fijo'

  Scenario: Opción 1 – sin registros para el periodo
    Given path '/api/v1/financial-fixed-assets/1'
    And param period = '1999-01-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = ''
    When method get
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Error al obtener informacion para el periodo: 1999-01-01'

  Scenario: Opción 2 – búsqueda por código de activo fijo principal existente
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

    # búsqueda por código principal (option=2)
    Given path '/api/v1/financial-fixed-assets/2'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = '22.01.010862'
    And param accountingAccount = ''
    When method get
    Then status 200
    And match response.code == '00'
    And match response.message contains 'Se obtuvo informacion para el código de activo fijo principal: 22.01.010862'
    And assert response.response.length > 0

  Scenario: Opción 2 – código principal no existe
    Given path '/api/v1/financial-fixed-assets/2'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = 'NO_EXISTE'
    And param accountingAccount = ''
    When method get
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Error al obtener informacion para el código de activo fijo principal: NO_EXISTE'

  Scenario: Opción 3 – búsqueda por cuenta contable existente
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

    # búsqueda por cuenta contable (option=3)
    Given path '/api/v1/financial-fixed-assets/3'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = '3321161001'
    When method get
    Then status 200
    And match response.code == '00'
    And match response.message contains 'Se obtuvo informacion para la cuenta contable: 3321161001'
    And assert response.response.length > 0

  Scenario: Opción 3 – cuenta contable no existe
    Given path '/api/v1/financial-fixed-assets/3'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = '999999999'
    When method get
    Then status 404
    And match response.code == '01'
    And match response.message contains 'Error al obtener informacion para la cuenta contable: 999999999'

  Scenario: Opción inválida – retorna error
    Given path '/api/v1/financial-fixed-assets/99'
    And param period = '2022-01-01'
    And param mainFixedAssetCode = ''
    And param accountingAccount = ''
    When method get
    Then status 417
    And match response.code == '01'
    And match response.message contains 'Error al obtener informacion del activo fijo'
