Feature: Integración del endpoint saveFinancialFixedAssetController

  Background:
    # baseUrl inyectado desde la IT y gestionamos cookies automáticamente
    * url baseUrl
    * configure cookies = { auto: true }

  Scenario: Guardar activos tras un upload exitoso
    # 1) Primero inicializamos la sesión con /upload
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200
    And match response.code == '00'

    # 2) Ahora invocamos el save
    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 201
    And match response.message == 'Información guardada correctamente para el periodo: 2022-01-01'
    And match response.code == '00'
    # asumimos que assets.xlsx tenía 2 filas válidas
    And match response.response == 2

  Scenario: Error al guardar sin haber subido nada
    Given path '/api/v1/financial-fixed-assets'
    And param period = '2022-01-01'
    When method post
    Then status 417
    And match response.code == '01'
    And match response.message contains 'Error al obtener información de entrada'

  Scenario: Error por formato de periodo inválido
    # subimos correctamente para llenar la sesión
    Given path '/api/v1/financial-fixed-assets/upload'
    And multipart file file = { read: 'classpath:data/assets.xlsx', filename: 'assets.xlsx', contentType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
    And multipart field period = '2022-01-01'
    When method post
    Then status 200

    # intentamos guardar con formato de fecha incorrecto
    Given path '/api/v1/financial-fixed-assets'
    And param period = '01-2022-01'
    When method post
    Then status 417
    And match response.code == '01'
    And match response.message contains 'Error al obtener información de entrada para el periodo 01-2022-01'
